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

package net.ixitxachitls.dma.output.pdf;

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
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A document for printing dma base pdf documents.
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
   * This is a convenience creator using the standard dimension of the page
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
   * This is a convenience creator using the standard dimension of the page
   * from the configuration.
   *
   * @param       inTitle the HTML title of the document
   * @param       inDM    a flag denoting if this is a DM document or not
   *
   */
  public PDFDocument(@Nonnull String inTitle, boolean inDM)
  {
    super(inTitle, inDM);
  }

  //........................................................................
  //----------------------------- PDFDocument ----------------------------

  /**
   * This is the full constructor.
   *
   * @param       inTitle the HTML title of the document
   * @param       inDM    a flag denoting if this is a DM document or not
   * @param       inLandscape true for landscape printing, false else
   *
   */
  public PDFDocument(@Nonnull String inTitle, boolean inDM,
                     boolean inLandscape)
  {
    super(inTitle, inDM, inLandscape);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables
  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

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

      // instantiating the document printer
      DocumentPrinter printer =
        new DocumentPrinter(new StringReader(super.toString()), properties);

      // defining the ResourceLoader: This is necessary if you like to
      // dynamically load resources like images during template processing.
      printer.setResourceLoader
        (new ClassPathResourceLoader(getClass().getClassLoader()));

      // generating the document output
      printer.printDocument(inOutput);
    }
    catch(java.io.IOException e)
    {
      Log.warning("cannot write pdf: " + e);

      return false;
    }
    catch(org.ujac.print.DocumentHandlerException e)
    {
      Log.warning("cannot write pdf: " + e);

      try
      {
        inOutput.write(super.toString().getBytes("UTF-8"));
        inOutput.flush();
      }
      catch(java.io.UnsupportedEncodingException e2)
      {
        Log.error("invalid encoding: " + e2);
      }
      catch(java.io.IOException e2)
      {
        Log.error("cannot write pdf error: " + e2);
      }

      return false;
    }

    return true;
  }

  //........................................................................

  //--------------------------------- main ---------------------------------

  /**
   * Print the ujax xml document given as standard in (for testing).
   *
   * @param  inArgs the command line arguments
   *
   * @throws Exception should not happen
   *
   */
  public static void main(String []inArgs) throws Exception
  {
    // using UJAC and iText
    // document properties (...don't know yet...)
    Map properties = new HashMap();

    // instantiating the document printer
    DocumentPrinter printer =
      new DocumentPrinter(System.in, properties);

    // defining the ResourceLoader: This is necessary if you like to
    // dynamically load resources like images during template processing.
    printer.setResourceLoader
      (new ClassPathResourceLoader(PDFDocument.class.getClassLoader()));

    // generating the document output
    printer.printDocument(System.out);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
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

      // save to a temp file
      java.io.File tmp = java.io.File.createTempFile("test", "file");

      assertTrue("save", doc.save(tmp.getPath()));

      java.io.BufferedReader reader =
        new java.io.BufferedReader
        (new java.io.InputStreamReader(new java.io.FileInputStream(tmp)));

      assertEquals("text", "%PDF-1.4", reader.readLine());
      String line = reader.readLine();
      assertNotNull("line", line);
      assertEquals("text", "%", line.substring(0, 1));

      reader.close();

      assertTrue("delete", tmp.delete());
    }

    //......................................................................
  }

  //........................................................................
}
