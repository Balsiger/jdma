/******************************************************************************
 * Copyright (c) 2002-2011 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
 * All rights reserved
 *
 * This file is part of Dungeon Master Assistant.
 *
 * Dungeon Master Assistant is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Dungeon Master Assistant is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dungeon Master Assistant; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *****************************************************************************/

//------------------------------------------------------------------ imports

package net.ixitxachitls.output.pdf;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import org.ujac.print.DocumentPrinter;
import org.ujac.util.io.ClassPathResourceLoader;

import net.ixitxachitls.output.Buffer;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base document for all output documents used.
 *
 * @file          PDFDocument.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class PDFDocument extends ITextDocument
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- PDFDocument ----------------------------

  /**
   * This is a convenience create using the standard dimension of the page
   * from the configuration.
   *
   * @param       inTitle           the HTML title of the document
   *
   */
  public PDFDocument(@Nonnull String inTitle)
  {
    super(inTitle);
  }

  //........................................................................
  //----------------------------- PDFDocument ----------------------------

  /**
   * This is a convenience create using the standard dimension of the page
   * from the configuration.
   *
   * @param       inTitle           the HTML title of the document
   * @param       inDM              true for landscape printing, false else
   *
   */
  public PDFDocument(@Nonnull String inTitle, boolean inDM)
  {
    super(inTitle, inDM);
  }

  //........................................................................
  //----------------------------- PDFDocument ----------------------------

  /**
   * This is a convenience create using the standard dimension of the page
   * from the configuration.
   *
   * @param       inTitle the HTML title of the document
   * @param       inDM    a flag denoting if this is a DM document or not
   * @param       inLandscape true for landscape printing, false else
   *
   */
  public PDFDocument(@Nonnull String inTitle, boolean inDM, boolean inLandscape)
  {
    super(inTitle, inDM, inLandscape);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables
  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------------- save ---------------------------------

  /**
   * Save the contents of the document to a file with the given name.
   *
   * @param       inFileName the name of the file to store in
   *
   * @return      true if stored, false if not
   *
   */
  public boolean save(@Nonnull String inFileName)
  {
    FileOutputStream output = null;
    try
    {
      output = new FileOutputStream(inFileName);
      write(output);
    }
    catch(java.io.IOException e)
    {
      Log.warning("cannot write to '" + inFileName + "': " + e);

      return false;
    }
    finally
    {
      try
      {
        if(output != null)
          output.close();
      }
      catch(java.io.IOException e)
      {
        Log.warning("cannot close output to '" + inFileName + "': " + e);
      }
    }

    Log.info("wrote file '" + inFileName + "'");

    return true;
  }

  //........................................................................
  //--------------------------------- write --------------------------------

  /**
   * Write the contents of the document to the given stream.
   *
   * @param       inOutput the stream to write to
   *
   * @return      true if writtenn, false if not
   *
   */
  public boolean write(@Nonnull OutputStream inOutput)
  {
    try
    {
      // using UJAC and iText
      // document properties (...don't know yet...)
      Map properties = new HashMap();

      String text = super.toString();

      // instantiating the document printer
      DocumentPrinter printer =
        new DocumentPrinter(new StringReader(text), properties);

      // defining the ResourceLoader: This is necessary if you like to
      // dynamically load resources like images during template processing.
      printer.setResourceLoader
        (new ClassPathResourceLoader(getClass().getClassLoader()));

      // generating the document output
      printer.printDocument(inOutput);
    }
    catch(java.io.IOException e)
    {
      Log.warning("cannot write product list: " + e);

      return false;
    }
    catch(org.ujac.print.DocumentHandlerException e)
    {
      Log.warning("cannot write product list31: " + e);

      return false;
    }

    return true;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- simple ---------------------------------------------------------

    /** Some simple tests. */
    @org.junit.Test
    public void simple()
    {
      PDFDocument doc = new PDFDocument("title");

      doc.add(new Command(new Command("just "),
                          new net.ixitxachitls.output.commands.Bold("some "),
                          new Command("test")));

      assertEquals("complete",
                   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                   + "<!DOCTYPE ITEXT SYSTEM "
                   + "\"http://itext.sourceforge.net/itext.dtd\">\n"
                   + "<document size=\"A4\" margin-left=\"25\" "
                   + "margin-right=\"25\" margin-top=\"25\" "
                   + "margin-bottom=\"25\">\n"
                   + "<meta title=\"title\" subject=\"title\" "
                   + "author=\"jDMA\" creator=\"jDMA\" "
                   + "creation-date=\"" + doc.m_date + "\" />"
                   + "<color-def name=\"title\" color-space=\"RGB\" "
                   + "value=\"0x00,0x00,0x80\" />"
                   + "<color-def name=\"subtitle\" color-space=\"RGB\" "
                   + "value=\"0x00,0x00,0x40\" />"
                   + "<color-def name=\"dm\" color-space=\"RGB\" "
                   + "value=\"0xee,0x88,0x00\" />"
                   + "<color-def name=\"player-notes\" color-space=\"RGB\" "
                   + "value=\"0x00,0x80,0x00\" />"
                   + "<color-def name=\"dm-notes\" color-space=\"RGB\" "
                   + "value=\"0xee,0x88,0x00\" />"
                   + "<color-def name=\"count\" color-space=\"RGB\" "
                   + "value=\"0x00,0x40,0x00\" />"
                   + "<color-def name=\"count-max\" color-space=\"RGB\" "
                   + "value=\"0xdd,0xdd,0xdd\" />"
                   + "<color-def name=\"header\" color-space=\"RGB\" "
                   + "value=\"0xaa,0xaa,0xaa\" />"
                   + "<color-def name=\"error\" color-space=\"RGB\" "
                   + "value=\"0xff,0x00,0x00\" />"
                   + "<color-def name=\"incomplete\" color-space=\"RGB\" "
                   + "value=\"0xff,0xaa,0xaa\" />"
                   + "<color-def name=\"table-odd\" color-space=\"RGB\" "
                   + "value=\"0xff,0xff,0xff\" />"
                   + "<color-def name=\"table-even\" color-space=\"RGB\" "
                   + "value=\"0xff,0xff,0xff\" />"
                   + "<color-def name=\"description-odd\" color-space=\"RGB\" "
                   + "value=\"0xff,0xff,0xff\" />"
                   + "<color-def name=\"description-even\" color-space=\"RGB\" "
                   + "value=\"0xff,0xff,0xff\" />"
                   + "<color-def name=\"keep-odd\" color-space=\"RGB\" "
                   + "value=\"0xff,0xff,0xff\" />"
                   + "<color-def name=\"keep-even\" color-space=\"RGB\" "
                   + "value=\"0xff,0xff,0xff\" />"
                   + "<color-def name=\"colored-odd\" color-space=\"RGB\" "
                   + "value=\"0xff,0xff,0xff\" />"
                   + "<color-def name=\"colored-even\" color-space=\"RGB\" "
                   + "value=\"0xff,0xff,0xff\" />"
                   + "<color-def name=\"base-odd\" color-space=\"RGB\" "
                   + "value=\"0xff,0xff,0xff\" />"
                   + "<color-def name=\"base-even\" color-space=\"RGB\" "
                   + "value=\"0xff,0xff,0xff\" />"
                   + "<color-def name=\"Place\" color-space=\"RGB\" "
                   + "value=\"0x80,0x40,0x00\" />"
                   + "<color-def name=\"Product\" color-space=\"RGB\" "
                   + "value=\"0x40,0x40,0x40\" />"
                   + "<color-def name=\"NPC\" color-space=\"RGB\" "
                   + "value=\"0x80,0x00,0x00\" />"
                   + "<color-def name=\"Monster\" color-space=\"RGB\" "
                   + "value=\"0x00,0x80,0x00\" />"
                   + "<color-def name=\"Group\" color-space=\"RGB\" "
                   + "value=\"0xff,0x80,0x80\" />"
                   + "<color-def name=\"Item\" color-space=\"RGB\" "
                   + "value=\"0x00,0x00,0x80\" />"
                   + "<color-def name=\"God\" color-space=\"RGB\" "
                   + "value=\"0x80,0x80,0x00\" />"
                   + "<color-def name=\"Event\" color-space=\"RGB\" "
                   + "value=\"0x80,0x00,0x80\" />"
                   + "<color-def name=\"Class\" color-space=\"RGB\" "
                   + "value=\"0xff,0x80,0xff\" />"
                   + "<color-def name=\"Spell\" color-space=\"RGB\" "
                   + "value=\"0x80,0x80,0xff\" />"
                   + "<color-def name=\"Domain\" color-space=\"RGB\" "
                   + "value=\"0x80,0x80,0xff\" />"
                   + "<color-def name=\"Feat\" color-space=\"RGB\" "
                   + "value=\"0x00,0x80,0x80\" />"
                   + "<color-def name=\"Skill\" color-space=\"RGB\" "
                   + "value=\"0x80,0xff,0xff\" />"
                   + "<color-def name=\"Quality\" color-space=\"RGB\" "
                   + "value=\"0x80,0xff,0xff\" />"
                   + "<color-def name=\"#AAAAAA\" color-space=\"RGB\" "
                   + "value=\"0xaa,0xaa,0xaa\" />"
                   + "<color-def name=\"BaseCharacter\" color-space=\"RGB\" "
                   + "value=\"0x33,0x99,0xcc\" />"
                   + "<color-def name=\"BaseProduct\" color-space=\"RGB\" "
                   + "value=\"0x80,0x80,0x80\" />"
                   + "<color-def name=\"colored-even\" color-space=\"RGB\" "
                   + "value=\"0xee,0xee,0xee\" />"
                   + "<color-def name=\"indent\" color-space=\"RGB\" "
                   + "value=\"0xcc,0xcc,0xcc\" />"
                   + "<font-def name=\"tiny\" family=\"Helvetica\" "
                   + "style=\"normal\" size=\"3\"/>"
                   + "<font-def name=\"footnote\" family=\"Helvetica\" "
                   + "style=\"normal\" size=\"4\"/>"
                   + "<font-def name=\"script\" family=\"Helvetica\" "
                   + "style=\"normal\" size=\"6\"/>"
                   + "<font-def name=\"small\" family=\"Helvetica\" "
                   + "style=\"normal\" size=\"8\"/>"
                   + "<font-def name=\"normal\" family=\"Helvetica\" "
                   + "style=\"normal\" size=\"10\"/>"
                   + "<font-def name=\"large\" family=\"Helvetica\" "
                   + "style=\"normal\" size=\"12\"/>"
                   + "<font-def name=\"larger\" family=\"Helvetica\" "
                   + "style=\"normal\" size=\"14\"/>"
                   + "<font-def name=\"largest\" family=\"Helvetica\" "
                   + "style=\"normal\" size=\"16\"/>"
                   + "<font-def name=\"huge\" family=\"Helvetica\" "
                   + "style=\"normal\" size=\"18\"/>"
                   + "<font-def name=\"huger\" family=\"Helvetica\" "
                   + "style=\"normal\" size=\"20\"/>"
                   + "<font-def name=\"title\" family=\"Helvetica\" "
                   + "style=\"bold\" size=\"16\" color=\"title\"/>"
                   + "<font-def name=\"subtitle\" family=\"Helvetica\" "
                   + "style=\"bold\" size=\"12\" color=\"subtitle\"/>"
                   + "<font-def name=\"count-max\" family=\"Helvetica\" "
                   + "style=\"line-through\" color=\"count-max\"/>"
                   + "<font-def name=\"count\" family=\"Helvetica\" "
                   + "color=\"count\"/>"
                   + "<font-def name=\"header\" family=\"Helvetica\" "
                   + "size=\"8\" color=\"header\"/>"
                   + "<font-def name=\"tiny-header\" family=\"Helvetica\" "
                   + "size=\"5\" color=\"header\"/>"
                   + "<font-def name=\"dm\" color=\"dm\"/>"
                   + "<register-font family=\"Webdings\" "
                   + "source=\"fonts/webdings.ttf\" />"
                   + "<font-def size=\"6\" name=\"symbol\" "
                   + "family=\"Webdings\"/>"
                   + "<style-def name=\".count\" border-style=\"box\" "
                   + "border-color=\"count\" font=\"count\"/>"
                   + "<style-def name=\".count-max\" border-style=\"box\" "
                   + "border-color=\"count-max\" bgcolor=\"dm\" "
                   + "font=\"count-max\" />"
                   + "<style-def name=\".table.colored\" padding-bottom=\"5\" "
                   + "padding-left=\"5\" padding-right=\"5\" />"
                   + "<style-def name=\".description\" padding-bottom=\"5\" />"
                   + "<style-def name=\".label\" padding-left=\"5\" "
                   + "padding-top=\"0\" padding-bottom=\"5\" "
                   + "border-width=\"0.1\" border-color=\"white\" "
                   + "border-style=\"top\"/>"
                   + "<style-def name=\".value\" padding-left=\"5\" "
                   + "padding-top=\"0\" padding-bottom=\"5\" />"
                   + "<header rule-width=\"0.1\" rule-color=\"header\">"
                   + "<header-part font=\"header\" halign=\"left\">title"
                   + "</header-part>"
                   + "</header>"
                   + "<footer rule-width=\"0.1\" rule-color=\"header\">"
                   + "<footer-part padding-top=\"10\" font=\"header\" "
                   + "width=\"400\" halign=\"center\">"
                   + "<font name=\"tiny-header\"></font>"
                   + "</footer-part>"
                   + "<footer-part padding-top=\"10\" font=\"header\" "
                   + "halign=\"left\">" + doc.m_date + "</footer-part>"
                   + "<footer-part padding-top=\"10\" font=\"header\" "
                   + "halign=\"right\">${pageNumber} / "
                   + "${pageCount}</footer-part></footer>"
                   + "just <b>some </b>test</document>\n",
                   doc.toString());
    }

    //......................................................................
    //----- save -----------------------------------------------------------

    /**
     * Test saving of a document.
     *
     * @throws Exception should not happen
     *
     */
    @org.junit.Test
    public void save() throws Exception
    {
      PDFDocument doc = new PDFDocument("title");

      doc.setAlignment(Buffer.Alignment.left);
      doc.add(new net.ixitxachitls.output.commands
              .Left("This is just some test file"));

      java.io.BufferedReader reader = null;
      java.io.File tmp = null;
      try
      {
        // save to a temp file
        tmp = java.io.File.createTempFile("test", "file");

        assertTrue("save", doc.save(tmp.getPath()));

        reader = new java.io.BufferedReader
        (new java.io.InputStreamReader(new java.io.FileInputStream(tmp)));

        assertEquals("text", "%PDF-1.4", reader.readLine());
        String line = reader.readLine();
        assertNotNull(line);
        assertEquals("text", "%", line.substring(0, 1));

      }
      finally
      {
        reader.close();
      }
      assertTrue("delete", tmp.delete());
    }

    //......................................................................
  }

  //........................................................................
}
