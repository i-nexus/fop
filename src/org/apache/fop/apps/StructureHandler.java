/*
 * $Id$
 * Copyright (C) 2001 The Apache Software Foundation. All rights reserved.
 * For details on use and redistribution please refer to the
 * LICENSE file included with these sources.
 */

package org.apache.fop.apps;

// Java
import java.util.HashSet;

import org.apache.avalon.framework.logger.Logger;
import org.apache.fop.fo.pagination.*;
import org.apache.fop.fo.flow.*;
import org.apache.fop.fo.*;
import org.apache.fop.layout.FontInfo;

import org.xml.sax.SAXException;

/**
 * This class receives structure events from the FO Tree.
 * Sub-classes can then implement various methods to handle
 * the FO Tree when the SAX events occur.
 */
public class StructureHandler {
    /**
       The current set of id's in the FO tree
       This is used so we know if the FO tree contains duplicates
     */
    private HashSet idReferences = new HashSet();
    protected Logger log;

    public StructureHandler() {
    }

    public void setLogger(Logger logger) {
        log = logger;
    }

    public HashSet getIDReferences() {
        return idReferences;
    }

    public FontInfo getFontInfo() {
        return null;
    }

    public void startDocument() throws SAXException {

    }

    public void endDocument() throws SAXException {

    }

    public void startPageSequence(PageSequence pageSeq, LayoutMasterSet lms) {

    }

    public void endPageSequence(PageSequence pageSeq) throws FOPException {

    }

    public void startFlow(Flow fl) {

    }

    public void endFlow(Flow fl) {

    }

    public void startBlock(Block bl) {

    }

    public void endBlock(Block bl) {

    }


    // Tables
    public void startTable(Table tbl) {

    }

    public void endTable(Table tbl) {

    }

    public void startHeader(TableHeader th) {

    }

    public void endHeader(TableHeader th) {

    }

    public void startFooter(TableFooter tf) {

    }

    public void endFooter(TableFooter tf) {

    }

    public void startBody(TableBody tb) {

    }

    public void endBody(TableBody tb) {

    }

    public void startRow(TableRow tr) {

    }

    public void endRow(TableRow tr) {

    }

    public void startCell(TableCell tc) {

    }

    public void endCell(TableCell tc) {

    }


    // Lists
    public void startList(ListBlock lb) {

    }

    public void endList(ListBlock lb) {

    }

    public void startListItem(ListItem li) {

    }

    public void endListItem(ListItem li) {

    }

    public void startListLabel() {

    }

    public void endListLabel() {

    }

    public void startListBody() {

    }

    public void endListBody() {

    }


    // Static Regions
    public void startStatic() {

    }

    public void endStatic() {

    }


    public void startMarkup() {

    }

    public void endMarkup() {

    }


    public void startLink() {

    }

    public void endLink() {

    }


    public void image(ExternalGraphic eg) {

    }

    public void pageRef() {

    }

    public void foreignObject(InstreamForeignObject ifo) {

    }

    public void footnote() {

    }

    public void leader(Leader l) {

    }


    public void characters(char data[], int start, int length) {

    }

    public void pageBreak() {

    }


}

