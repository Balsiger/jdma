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

package net.ixitxachitls.dma.values;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a comment and is capable of reading such comments
 * from a reader (and write it to a writer of course).
 *
 * @file          Comment.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Comment extends Value<Comment>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Comment --------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Construct the comment object with an empty value.
   *
   * @param       inMax    the maximal number of disjoint comments to
   *                       read (-1 for any number)
   * @param       inLines  maximal number of comment lines to read (-1 for
   *                       any number)
   *
   */
  public Comment(int inMax, int inLines)
  {
    m_maxComments = inMax;
    m_maxLines = inLines;
  }

  //........................................................................
  //------------------------------- Comment --------------------------------

  /**
   * Construct the comment object from a string.
   *
   * @param       inString the string representing the comment
   *                       (with markers)
   * @param       inMax    the maximal number of disjoint comments to
   *                       read (-1 for any number)
   * @param       inLines  maximal number of comment lines to read (-1 for
   *                       any number)
   *
   */
  public Comment(String inString, int inMax, int inLines)
  {
    this(inMax, inLines);

    if(!check(inString))
      throw new IllegalArgumentException("given default value does not start "
                                         + "with " + s_starter);
    m_lines = inString;
  }

  //........................................................................

  //-------------------------------- create --------------------------------

  /**
   * Create a new list with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   *
   */
  @Override
  public Comment create()
  {
    return new Comment(m_maxComments, m_maxLines);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** All the lines of the comment. */
  private @Nullable String m_lines;

  /** The maximal number of disjoint comments to read (-1 for any number). */
  private int m_maxComments = -1;

  /** The maximal number of comment lines to read (-1 for any number). */
  private int m_maxLines = -1;

  /** The string used to start the comment. */
  private static final String s_starter =
    Config.get("resource:values/comment.starter", "#");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ isDefined -------------------------------

  /**
   * Check if the value is defined or notl.
   *
   * @return      true if the value is defined, false if not
   *
   */
  @Override
  public boolean isDefined()
  {
    return m_lines != null;
  }

  //........................................................................

  //--------------------------------- get ----------------------------------

  /**
   * Get the lines of the comments.
   *
   * @return      the comment lines (null if no comment defined)
   *
   */
  public @Nullable String get()
  {
    return m_lines;
  }

  //........................................................................
  //------------------------------ doToString ------------------------------

  /**
   * Convert the value to a string, depending on the given kind.
   *
   * @return      a String representation, depending on the kind given
   *
   */
  @Override
  protected String doToString()
  {
    return m_lines;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- fix ----------------------------------

  /**
   * Make sure that we don't have too many newlines or whitespace in the
   * comment.
   *
   * We allow at most to leading and two trailing newlines.
   * This is actually changing the value, but we allow this as an exception.
   *
   */
  public void fix()
  {
    if(!isDefined())
      return;

    // remove too many newlines
    m_lines = m_lines.replaceAll("([ \t\f]*\\n){3,}", "\n\n");

    // allow only #---- comments
    m_lines = m_lines.replaceAll("(^|\n)([ \t]*)#-{6,}", "$1$2#-----");
    m_lines = m_lines.replaceAll("[ \t]*-{3,}[ \t]*(\n|$)", "$1");

    // remove too many dots
    m_lines = m_lines.replaceAll("(^|\n)([ \t]*)#\\.{6,}[ \t]*(\n|$)",
                                 "$1$2#.....\n");
  }

  //........................................................................
  //-------------------------------- check ---------------------------------

  /**
   * Check if the given String is a valid comment (starts with the
   * comment starter on any line).
   *
   * @param       inText the text to check
   *
   * @return      true if it is a comment, false if not
   *
   */
  protected static boolean check(String inText)
  {
    String []lines = inText.split("\n");

    for(int i = 0; i < lines.length; i++)
    {
      String line = lines[i].trim().replaceAll("\\s", "");

      if(line.length() > 0 && !line.startsWith(s_starter))
        return false;
    }

    return true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //---------------------------------- as ----------------------------------

  /**
   * Set the text stored in the comment.
   *
   * @param       inText the text to set the comment to
   *
   * @return      a new comment with the given value
   *
   */
  public Comment as(String inText)
  {
    if(!check(inText))
      return this;

    Comment result = create();
    result.m_lines = inText;

    return result;
  }

  //........................................................................
  //------------------------------- doRead ---------------------------------

  /**
   * Read the value from the reader and replace the current one.
   *
   * @param       inReader   the reader to read from
   *
   * @return      true if read, false if not
   *
   */
  @Override
  public boolean doRead(ParseReader inReader)
  {
    if(m_maxComments == 0)
      return false;

    StringBuffer read = new StringBuffer();

    boolean lastWasComment = false;
    int max = m_maxComments;
    int lines = m_maxLines;

    while(!inReader.isAtEnd() && max != 0 && lines != 0)
    {
      ParseReader.Position pos = inReader.getPosition();

      String line    = inReader.readLine();

      // remove leading white spaces
      String trimmed = line.trim();

      // check if its a comment line
      if(trimmed.length() == 0)
      {
        // we saw an empty line, thus the previous comment is finished
        read.append('\n');

        if(max > 0 && lastWasComment)
        {
          max--;

          lastWasComment = false;
        }
      }
      else
        if(trimmed.startsWith(s_starter))
        {
          // this is a real comment line
          if(lines > 0)
            lines--;

          read.append(line);
          read.append('\n');

          lastWasComment = true;
        }
        else
        {
          // not a comment line, go a line back and stop
          inReader.seek(pos);

          break;
        }
    }

    // don't return an empty comment
    if(read.length() == 0)
      return false;

    m_lines = read.toString();

    // empty comment?
    if(m_lines.trim().length() == 0)
    {
      m_lines = null;

      return false;
    }

    return true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The Test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      // check for undefined
      Comment comment = new Comment(-1, -1);

      assertEquals("not undefined", false, comment.isDefined());
      assertEquals("not undefined", UNDEFINED, comment.toString());

      // check for defined
      comment = new Comment("  " + s_starter + " a default value", -1, -1);

      assertEquals("not defined", true, comment.isDefined());
      assertEquals("not correctly defined",
                   "  " + s_starter + " a default value", comment.toString());
      assertEquals("lines", "  " + s_starter + " a default value",
                   comment.get());

      Value.Test.createTest(comment);

      // check for an invalid value
      try
      {
        new Comment("invalid", -1, -1);

        fail("init with invalid values should have failed");
      }
      catch(IllegalArgumentException e)
      { /* nothing to do */ }

      try
      {
        new Comment(s_starter + " invalid\n other invalid", -1, -1);

        fail("init with invalid values should have failed");
      }
      catch(IllegalArgumentException e)
      { /* nothing to do */ }
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      m_logger.banClass("net.ixitxachitls.util.configuration.Config");

      String []tests =
        {
          "simple", "# first\n", "# first\n", null,
          "no whites", "#first", "#first\n", null,
          "no comment", "guru", null, "guru",
          "leading spaces", "     # first", "     # first\n", null,
          "leading whites", "\t\n\n\r  # first", "\n\n  # first\n", null,
          "two of two", "# first\n# second\n", "# first\n# second\n", null,
          "two of three", "# first\n# second\n# third", "# first\n# second\n",
          "# third",

          "multi two of two", "# first\n\n# second\n", "# first\n\n# second\n",
          null,

          "multi two of three", "# first\n\n# second\n\n# third",
          "# first\n\n# second\n",  null,

          "multi with no comment", "# first\n\nhello", "# first\n\n", "hello",
        };

      Value.Test.readTest(tests, new Comment(2, 2));
    }

    //......................................................................
    //----- as -------------------------------------------------------------

    /** Testing setting. */
    @org.junit.Test
    public void as()
    {
      Comment comment = new Comment(-1, -1);

      assertEquals("set", s_starter + " some value",
                   comment.as(s_starter + " some value").toString());

      assertEquals("set", s_starter + " another value",
                   comment.as(s_starter + " another value").toString());

      assertEquals("set", UNDEFINED, comment.as("whatever").toString());

      assertEquals("set", UNDEFINED,
                   comment.as(s_starter + "first\n\nsecond").toString());

      assertEquals("set", "\n\t\r\f   " + s_starter + " another value",
                   comment.as("\n\t\r\f   " + s_starter
                              + " another value").toString());
    }

    //......................................................................
    //----- convert --------------------------------------------------------

    /** Testing convert. */
    @org.junit.Test
    public void convert()
    {
      Comment comment = new Comment(s_starter + " some default value", -1, -1);

      assertEquals("string", s_starter + " some default value",
                   comment.toString());
    }

    //......................................................................
    //----- testFix --------------------------------------------------------

    /** Test fixing of newlines. */
    @org.junit.Test
    public void fix()
    {
      Comment comment = new Comment("# a comment", -1, -1);

      comment.fix();

      assertEquals("nothing", "# a comment", comment.toString());

      comment = new Comment("\n\n\n\n\n# a comment\n\n# with\n\n\n# more",
                            -1, -1);

      comment.fix();

      assertEquals("replaced", "\n\n# a comment\n\n# with\n\n# more",
                   comment.toString());

      // with spaces
      comment = new Comment("  \n  \n  \n  \n  # test   \n  \n \n    \n  ",
                            -1, -1);

      comment.fix();

      assertEquals("spaces", "\n\n  # test\n\n  ", comment.toString());

      // --- and ..
      comment = new Comment("     #----------- guru -------\n", -1, -1);

      comment.fix();

      assertEquals("hyphens", "     #----- guru\n", comment.toString());

      comment = new Comment("#..............................\n", -1, -1);

      comment.fix();

      assertEquals("dots", "#.....\n", comment.toString());
    }

    //......................................................................
  }

  //........................................................................
}
