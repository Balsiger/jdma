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

package net.ixitxachitls.output.ascii;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import net.ixitxachitls.output.Buffer;
import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.WrapBuffer;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.output.actions.Delimiter;
import net.ixitxachitls.output.actions.Multi;
import net.ixitxachitls.output.actions.ascii.Align;
import net.ixitxachitls.output.commands.Bold;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Emph;
import net.ixitxachitls.output.commands.Title;
import net.ixitxachitls.output.commands.Underline;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base document for all output documents used.
 *
 * @file          ANSIDocument.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class ANSIDocument extends ASCIIDocument
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- ANSIDocument ----------------------------

  /**
   * Basic constructor.
   *
   */
  public ANSIDocument()
  {
    this(Config.get("resource:ansi/width", 80));
  }

  //........................................................................
  //----------------------------- ANSIDocument ----------------------------

  /**
   * Basic constructor.
   *
   * @param       inWidth the width of a line in the document
   *
   */
  public ANSIDocument(int inWidth)
  {
    assert inWidth > 0 : "line width must be above zero";

    m_width  = inWidth;
    m_buffer = new WrapBuffer(m_width, s_ignore);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The escape character for ansi printing. */
  protected static final char s_escape = '\u001B';

  /** Pattern for text to ignore when determining the length of a line. */
  protected static final @Nonnull String s_ignore =
    Config.get("resource:ansi/ignore", "\u001B\\[\\d+m");

  /** Escape sequence to start bold text. */
  protected static final @Nonnull String s_bold =
    Config.get("resource:ansi/bold", s_escape + "[1m");

  /** Escape sequence to stop bold text. */
  protected static final @Nonnull String s_unbold =
    Config.get("resource:ansi/unbold", s_escape + "[22m");

  /** Escape sequence to start underlined text. */
  protected static final @Nonnull String s_underline =
    Config.get("resource:ansi/underline", s_escape + "[4m");

  /** Escape sequence to stop underlined text. */
  protected static final @Nonnull String s_ununderline =
    Config.get("resource:ansi/ununderline", s_escape + "[24m");

  /** Escape sequence to start black text. */
  protected static final @Nonnull String s_black =
    Config.get("resource:ansi/color.black", s_escape + "[30m");

  /** Escape sequence to start red text. */
  protected static final @Nonnull String s_red =
    Config.get("resource:ansi/color.red", s_escape + "[31m");

  /** Escape sequence to start green text. */
  protected static final @Nonnull String s_green =
    Config.get("resource:ansi/color.green", s_escape + "[32m");

  /** Escape sequence to start brown text. */
  protected static final @Nonnull String s_brown =
    Config.get("resource:ansi/color.brown", s_escape + "[33m");

  /** Escape sequence to start blue text. */
  protected static final @Nonnull String s_blue =
    Config.get("resource:ansi/color.blue", s_escape + "[34m");

  /** Escape sequence to start magenta text. */
  protected static final @Nonnull String s_magenta =
    Config.get("resource:ansi/color.magenta", s_escape + "[35m");

  /** Escape sequence to start cyan text. */
  protected static final @Nonnull String s_cyan =
    Config.get("resource:ansi/color.cyan", s_escape + "[36m");

  /** Escape sequence to start white text. */
  protected static final @Nonnull String s_white =
    Config.get("resource:ansi/color.white", s_escape + "[37m");

  /** The known actions. */
  protected static final @Nonnull Map<String, Action> s_actions =
    new HashMap<String, Action>(ASCIIDocument.s_actions);

  static
  {
    s_actions.put(Bold.NAME,
                  new Delimiter(null, null,
                                new String [] { s_bold },
                                new String [] { s_unbold }, null, null));
    s_actions.put(Emph.NAME,
                  new Delimiter(null, null,
                                new String [] { s_red },
                                new String [] { s_black }, null, null));
    s_actions.put(Underline.NAME,
                  new Delimiter(null, null,
                                new String [] { s_underline },
                                new String [] { s_ununderline },
                                null, null));
    s_actions.put(Title.NAME, new Multi(new Action []
      {
        new Align(Buffer.Alignment.center),
        new Delimiter(null, null,
                      new String [] { s_bold + s_underline },
                      new String [] { s_unbold + s_ununderline + "\n" },
                      null, null),
      }));

    s_actions.put("command", new Action());
    s_actions.put("baseCommand", new Action());
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------- getKnownActions ----------------------------

  /**
   * Get all the actions known to this document type.
   *
   * @return      a hash map with all the known converters
   *
   */
  protected @Nonnull Map<String, Action> getKnownActions()
  {
    return s_actions;
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

    /** Simple test. */
    @org.junit.Test
    public void simple()
    {
      Document doc = new ANSIDocument();

      doc.add(new Command(new Command("just "),
                          new net.ixitxachitls.output.commands.Bold("some "),
                          new Command("test")));

      assertEquals("simple", "just " + s_bold + "some " + s_unbold + "test",
                   doc.toString());
    }

    //......................................................................
    //----- wrapping -------------------------------------------------------

    /** Test wrapping. */
    @org.junit.Test
    public void wrapping()
    {
      Document doc = new ANSIDocument(40);

      doc.add(new Command(new Command("we do something similar to the stuff "
                                      + "we did before, "),
                          new net.ixitxachitls.output.commands.Bold("but"),
                          new Command(" this time the whole thing is to be "
                                      + "wrapped over several lines, and we "
                                      + "want to check that this works.")));

      assertEquals("command",
                   "we do something similar to the stuff we \n"
                   + "did before, " + s_bold + "but" + s_unbold
                   + " this time the whole     \n"
                   + "thing is to be wrapped over several     \n"
                   + "lines, and we want to check that this   \n"
                   + "works.", doc.toString());
    }

    //......................................................................
    //----- justification --------------------------------------------------

    /** Test justification. */
    @org.junit.Test
    public void justification()
    {
      Document doc = new ANSIDocument(40);

      doc.add(new net.ixitxachitls.output.commands.Center("center"));
      doc.add(new net.ixitxachitls.output.commands.Left("left"));
      doc.add(new net.ixitxachitls.output.commands.Right("right"));
      doc.add(new net.ixitxachitls.output.commands
              .Block("and now, finally the whole text set blocked, i.e. "
                     + "aligned to the left and to the right at the same "
                     + "time"));

      assertEquals("command",
                   "                 center                 \n"
                   + "left                                    \n"
                   + "                                   right\n"
                   + "and now,  finally  the  whole  text  set\n"
                   + "blocked, i.e. aligned to the left and to\n"
                   + "the right at the same time              \n",
                   doc.toString());

    }

    //......................................................................
  }

  //........................................................................
}
