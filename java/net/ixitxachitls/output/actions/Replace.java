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

package net.ixitxachitls.output.actions;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Document;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a replace action, used to replace the argument with a predetermined
 * text, depending on the argument read.
 *
 * @file          Replace.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Replace extends Action
{
  //----------------------------------------------------------------- nested

  /**
   * This is an auxiliary used to store a single replacement.
   *
   * @example      <PRE>
   * Replacement replacement = new Replacement("bb", "c");
   *
   * String replaced = replacement.replace("just some text to replace");
   * </PRE>
   *
   */
  @Immutable
  public static class Replacement
  {
    //---------------------------- Replacement -----------------------------

    /**
     * Construct the replacement text.
     *
     * @param       inOriginal    the original text to replace
     * @param       inReplacement the text to replace with
     *
     */
    public Replacement(@Nonnull String inOriginal,
                       @Nonnull String inReplacement)
    {
      m_original    = inOriginal;
      m_replacement = inReplacement;
    }

    //......................................................................

    //------------------------------------------------------------ variables

    /** Original text that is replace. */
    private @Nonnull String m_original;

    /** Text the original is replace with, if found. */
    private @Nonnull String m_replacement;

    //......................................................................

    //------------------------------ replace -------------------------------

    /**
     * Replace all occurrences of the original string of this Replacement with
     * the predefined replacement.
     *
     * @param       inOriginal the text to replace in
     *
     * @return      the result, with all replacement done or null if nothing
     *              replaced
     *
     */
    public @Nullable String replace(@Nonnull String inOriginal)
    {
      if(!inOriginal.matches(".*" + m_original + ".*"))
        return null;

      return inOriginal.replaceAll(m_original, m_replacement);
    }

    //......................................................................
    //------------------------------ toString ------------------------------

    /**
     * Convert the Replacement to a human readable String. This is used mainly
     * for debugging purposes.
     *
     * @return      a String representation
     *
     */
    public @Nonnull String toString()
    {
      return m_original + " ==> " + m_replacement;
    }

    //......................................................................
  }

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Replace ------------------------------

  /**
   * Construct the action.
   *
   * @param       inReplacements the replacements to make
   *
   */
  public Replace(@Nonnull Replacement ... inReplacements)
  {
    m_replacements = inReplacements;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** All the replacement to use. */
  protected @Nonnull Replacement []m_replacements;

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- execute --------------------------------

  /**
   * Execute the action onto the given document.
   *
   * @param       inDocument  the document to output to
   * @param       inOptionals the optional argument
   * @param       inArguments the arguments
   *
   */
  public void execute(@Nonnull Document inDocument,
                      @Nullable List<? extends Object> inOptionals,
                      @Nullable List<? extends Object> inArguments)
  {
    if(inArguments == null || inArguments.size() != 1)
      throw new IllegalArgumentException("expecting exactly one argument");

    Document doc = inDocument.createSubDocument();

    doc.add(inArguments.get(0));

    String replace = doc.toString();

    for(int i = 0; i < m_replacements.length; i++)
    {
      String replaced = m_replacements[i].replace(replace);

      if(replaced != null)
      {
        inDocument.add(replaced);

        return;
      }
    }

    Log.warning("no replacement for '" + replace + "' having replacement "
                + Arrays.toString(m_replacements)
                + " only, returning original");

    inDocument.add(replace);;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- replacement ----------------------------------------------------

    /** Test replacements. */
    @org.junit.Test
    public void replacement()
    {
      assertEquals("toString", new Replacement("a", "b").toString(),
                   "a ==> b");
      assertEquals("simple", new Replacement("a", "b").replace("a"), "b");
      assertNull("complete", new Replacement("^a$", "b").replace("aaa"));
      assertEquals("complete", new Replacement("^a$", "b").replace("a"), "b");
      assertEquals("complete", new Replacement("a", "b").replace("aaa"),
                   "bbb");
      assertEquals("simple", new Replacement("a", "b").replace("aca"), "bcb");
      assertEquals("simple", new Replacement("a", "b").replace("cac"), "cbc");

      assertNull("null", new Replacement("a", "b").replace(""));
      assertNull("null", new Replacement("a", "b").replace("c"));
    }

    //......................................................................
    //----- all ------------------------------------------------------------

    /** Test all. */
    @org.junit.Test
    public void all()
    {
      Action action = new Replace(new Replacement []
        {
          new Replacement("a", "b"),
          new Replacement("bb", "c"),
          new Replacement("b", "d"),
        });

      Document doc = new Document();

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("just a test"));

      assertEquals("action did not produce desired result",
                   "just b test",
                   doc.toString());

      doc = new Document();
      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of("just b test"));

      assertEquals("action did not produce desired result",
                   "just d test",
                   doc.toString());

      doc = new Document();
      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("just bb test"));

      assertEquals("action did not produce desired result",
                   "just c test",
                   doc.toString());

      doc = new Document();
      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("just something"));

      assertEquals("action did not produce desired result",
                   "just something",
                   doc.toString());

       m_logger.addExpected("WARNING: no replacement for 'just "
                            + "something' having replacement "
                            + "[a ==> b, bb ==> c, b ==> d] only, "
                            + "returning original");
       m_logger.verify();
    }

    //......................................................................
  }

  //........................................................................
}


