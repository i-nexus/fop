/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.fop.render.ps;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.apache.fontbox.cff.CFFType1Font;
import org.apache.fontbox.cff.DataOutput;
import org.apache.fontbox.cff.Type1FontUtil;

/**
 * This class represents a formatter for a given Type1 font.
 * author Villu Ruusmann
 * @version $Revision: 1.0 $
 */
public final class Type1FontFormatter {
    private Map<Integer, Integer> gids;

    public Type1FontFormatter(Map<Integer, Integer> gids) {
        this.gids = gids;
    }

    /**
     * Read and convert a given CFFFont.
     * @param font the given CFFFont
     * @return the Type1 font
     * @throws IOException if an error occurs during reading the given font
     */
    public byte[] format(CFFType1Font font) throws IOException {
        DataOutput output = new DataOutput();
        printFont(font, output);
        return output.getBytes();
    }

    private void printFont(CFFType1Font font, DataOutput output)
            throws IOException {
        output.println("%!FontType1-1.0 " + font.getName() + " "
                + font.getTopDict().get("version"));

        printFontDictionary(font, output);

        for (int i = 0; i < 8; i++) {
            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < 64; j++) {
                sb.append("0");
            }

            output.println(sb.toString());
        }

        output.println("cleartomark");
    }

    private void printFontDictionary(CFFType1Font font, DataOutput output)
            throws IOException {
        output.println("10 dict begin");
        output.println("/FontInfo 10 dict dup begin");
        output.println("/version (" + font.getTopDict().get("version")
                + ") readonly def");
        output.println("/Notice (" + font.getTopDict().get("Notice")
                + ") readonly def");
        output.println("/FullName (" + font.getTopDict().get("FullName")
                + ") readonly def");
        output.println("/FamilyName (" + font.getTopDict().get("FamilyName")
                + ") readonly def");
        output.println("/Weight (" + font.getTopDict().get("Weight")
                + ") readonly def");
        output.println("/ItalicAngle " + font.getTopDict().get("ItalicAngle")
                + " def");
        output.println("/isFixedPitch " + font.getTopDict().get("isFixedPitch")
                + " def");
        output.println("/UnderlinePosition "
                + font.getTopDict().get("UnderlinePosition") + " def");
        output.println("/UnderlineThickness "
                + font.getTopDict().get("UnderlineThickness") + " def");
        output.println("end readonly def");
        output.println("/FontName /" + font.getName() + " def");
        output.println("/PaintType " + font.getTopDict().get("PaintType") + " def");
        output.println("/FontType 1 def");
        NumberFormat matrixFormat = new DecimalFormat("0.########", new DecimalFormatSymbols(Locale.US));
        output.println("/FontMatrix "
                + formatArray(font.getTopDict().get("FontMatrix"), matrixFormat, false)
                + " readonly def");
        output.println("/FontBBox "
                + formatArray(font.getTopDict().get("FontBBox"), false)
                + " readonly def");
        output.println("/StrokeWidth " + font.getTopDict().get("StrokeWidth")
                + " def");

        output.println("/Encoding " + gids.size() + " array");
        output.println("0 1 " + (gids.size() - 1) + " {1 index exch /.notdef put} for");

        for (Map.Entry<Integer, Integer> gid : gids.entrySet()) {
            String name = font.getCharset().getNameForGID(gid.getKey());
            output.println("dup " + gid.getValue() + " /" + name + " put");
        }
        output.println("readonly def");

        output.println("currentdict end");

        DataOutput eexecOutput = new DataOutput();

        printEexecFontDictionary(font, eexecOutput);

        output.println("currentfile eexec");

        byte[] eexecBytes = Type1FontUtil.eexecEncrypt(eexecOutput.getBytes());
        output.write(eexecBytes);
    }

    private void printEexecFontDictionary(CFFType1Font font, DataOutput output)
            throws IOException {
        output.println("dup /Private 15 dict dup begin");
        output.println("/RD {string currentfile exch readstring pop} executeonly def");
        output.println("/ND {noaccess def} executeonly def");
        output.println("/NP {noaccess put} executeonly def");
        output.println("/BlueValues "
                + formatArray(font.getPrivateDict().get("BlueValues"), true) + " ND");
        output.println("/OtherBlues "
                + formatArray(font.getPrivateDict().get("OtherBlues"), true) + " ND");
        output.println("/BlueScale " + font.getPrivateDict().get("BlueScale") + " def");
        output.println("/BlueShift " + font.getPrivateDict().get("BlueShift") + " def");
        output.println("/BlueFuzz " + font.getPrivateDict().get("BlueFuzz") + " def");
        output.println("/StdHW " + formatArray(font.getPrivateDict().get("StdHW"), true)
                + " ND");
        output.println("/StdVW " + formatArray(font.getPrivateDict().get("StdVW"), true)
                + " ND");
        output.println("/ForceBold " + font.getPrivateDict().get("ForceBold") + " def");
        output.println("/MinFeature {16 16} def");
        output.println("/password 5839 def");

        output.println("2 index /CharStrings " + gids.size() + " dict dup begin");
        Type1CharStringFormatter formatter = new Type1CharStringFormatter();
        for (int gid : gids.keySet()) {
            String mapping = font.getCharset().getNameForGID(gid);
            byte[] type1Bytes = formatter.format(font.getType1CharString(mapping).getType1Sequence());
            byte[] charstringBytes = Type1FontUtil.charstringEncrypt(type1Bytes, 4);
            output.print("/" + mapping + " " + charstringBytes.length + " RD ");
            output.write(charstringBytes);
            output.print(" ND");
            output.println();
        }

        output.println("end");
        output.println("end");

        output.println("readonly put");
        output.println("noaccess put");
        output.println("dup /FontName get exch definefont pop");
        output.println("mark currentfile closefile");
    }

    private static String formatArray(Object object, boolean executable) {
        return formatArray(object, null, executable);
    }

    private static String formatArray(Object object, NumberFormat format, boolean executable) {
        StringBuffer sb = new StringBuffer();

        sb.append(executable ? "{" : "[");

        if (object instanceof Collection) {
            String sep = "";

            Collection<?> elements = (Collection<?>) object;
            for (Object element : elements) {
                sb.append(sep).append(formatElement(element, format));

                sep = " ";
            }
        } else if (object instanceof Number) {
            sb.append(formatElement(object, format));
        }

        sb.append(executable ? "}" : "]");

        return sb.toString();
    }

    private static String formatElement(Object object, NumberFormat format) {
        if (format != null) {
            if (object instanceof Double || object instanceof Float) {
                Number number = (Number)object;
                return format.format(number.doubleValue());
            } else if (object instanceof Long || object instanceof Integer) {
                Number number = (Number)object;
                return format.format(number.longValue());
            }
        }
        return String.valueOf(object);
    }
}