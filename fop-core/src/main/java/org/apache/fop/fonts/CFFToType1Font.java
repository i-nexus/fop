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

/* $Id$ */
package org.apache.fop.fonts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.fontbox.cff.CFFFont;
import org.apache.fontbox.cff.CFFParser;
import org.apache.fontbox.cff.CFFType1Font;

import org.apache.fop.apps.io.InternalResourceResolver;
import org.apache.fop.fonts.type1.PFBData;
import org.apache.fop.fonts.type1.PFBParser;
import org.apache.fop.fonts.type1.Type1SubsetFile;
import org.apache.fop.render.ps.Type1FontFormatter;

public class CFFToType1Font extends MultiByteFont {

    public CFFToType1Font(InternalResourceResolver resourceResolver, EmbeddingMode embeddingMode) {
        super(resourceResolver, embeddingMode);
        setEmbeddingMode(EmbeddingMode.FULL);
        setFontType(FontType.TYPE1);
    }

    public InputStream getInputStream() throws IOException {
        InputStream cff = super.getInputStream();
        return convertOTFToType1(cff);
    }

    private InputStream convertOTFToType1(InputStream in) throws IOException {
        CFFFont f = new CFFParser().parse(IOUtils.toByteArray(in)).get(0);
        if (!(f instanceof  CFFType1Font)) {
            throw new IOException(getEmbedFileURI() + ": only OTF CFF Type1 font can be converted to Type1");
        }
        byte[] t1 = new Type1FontFormatter(cidSet.getGlyphs()).format((CFFType1Font) f);
        PFBData pfb = new PFBParser().parsePFB(new ByteArrayInputStream(t1));
        ByteArrayOutputStream s1 = new ByteArrayOutputStream();
        s1.write(pfb.getHeaderSegment());
        ByteArrayOutputStream s2 = new ByteArrayOutputStream();
        s2.write(pfb.getEncryptedSegment());
        ByteArrayOutputStream s3 = new ByteArrayOutputStream();
        s3.write(pfb.getTrailerSegment());
        byte[] out = new Type1SubsetFile().stitchFont(s1, s2, s3);
        return new ByteArrayInputStream(out);
    }
}