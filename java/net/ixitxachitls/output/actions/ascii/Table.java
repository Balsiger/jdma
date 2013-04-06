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

package net.ixitxachitls.output.actions.ascii;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Buffer;
import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This action is used to format ASCII tables.
 *
 * @file          Table.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @example       <PRE>
 * Action action = new Table("test");
 * BaseWriter.Status status = new BaseWriter.Status(40);
 *
 * // get an execution of the action
 * Execution exec = action.getExecution();
 *
 * // add the arguments
 * exec.startArgument(status);
 * exec.add("10:L;20:C,|,|;30:R, * , * ");
 * exec.stopArgument();
 * exec.startArgument(status);
 * exec.add("first");
 * exec.stopArgument();
 * exec.startArgument(status);
 * exec.add("second");
 * exec.stopArgument();
 * exec.startArgument(status);
 * exec.add("third");
 * exec.stopArgument();
 * exec.startArgument(status);
 * exec.add("first again, this time somewhat larger, to see that " +
 *          "wordwrapping works");
 * exec.stopArgument();
 * exec.startArgument(status);
 * exec.add("second, this is short");
 * exec.stopArgument();
 * exec.startArgument(status);
 * exec.add("third, this is also a bit longer, because two lines " +
 *          "should wrap, to have a better test case");
 * exec.stopArgument();
 * exec.startArgument(status);
 * exec.add("first");
 * exec.stopArgument();
 * exec.startArgument(status);
 * exec.add("second");
 * exec.stopArgument();
 *
 * // now to the execute
 * String result = exec.execute(status);
 * </PRE>
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Table extends Action
{
  //----------------------------------------------------------------- nested

  /**
   * This is an auxiliary class to store information about an individual
   * column.
   *
   */
  @Immutable
  protected static class Column
  {
    //------------------------------- Column -------------------------------

    /**
     * The constructor to set all up.
     *
     * @param       inWidth     the width of the column
     * @param       inAlignment the alignment of the column
     * @param       inLeader    the column leader
     * @param       inTrailer   the column trailer
     * @param       inName      the name of the column
     * @param       inTitle     the title of the column
     * @param       inImageDir  the directory to the images
     * @param       inHide      true if split columns are hidden, false if not
     *
     */
    public Column(int inWidth, @Nonnull Buffer.Alignment inAlignment,
                  @Nonnull String inLeader, @Nonnull String inTrailer,
                  @Nonnull String inName, @Nonnull String inTitle,
                  @Nullable String inImageDir, boolean inHide)
    {
      m_width     = inWidth;
      m_alignment = inAlignment;
      m_leader    = inLeader;
      m_trailer   = inTrailer;
      m_name      = inName;
      m_title     = inTitle;
      m_imageDir  = inImageDir;
      m_hide      = inHide;
    }

    //......................................................................

    //------------------------------------------------------------ variables

    /** The width of the column. */
    private int m_width;

    /** The alignment of the column. */
    private @Nonnull Buffer.Alignment m_alignment;

    /** The leading delimiter. */
    private @Nonnull String m_leader;

    /** The trailing delimiter. */
    private @Nonnull String m_trailer;

    /** The name of the column (for style sheets). */
    private @Nonnull String m_name;

    /** The title of the column. */
    private @Nonnull String m_title;

    /** The image directory for the column, if any. */
    private @Nullable String m_imageDir = null;

    /** A flag if split columns should be hidden or not. */
    private boolean m_hide = true;

    //......................................................................

    //------------------------------ getWidth ------------------------------

    /**
     * Accessor for the column width.
     *
     * @return      the requested width
     *
     */
    public int getWidth()
    {
      return m_width;
    }

    //......................................................................
    //---------------------------- getAlignment ----------------------------

    /**
     * Accessor for the column alignment.
     *
     * @return      the requested alignment
     *
     */
    public @Nonnull Buffer.Alignment getAlignment()
    {
      return m_alignment;
    }

    //......................................................................
    //----------------------------- getTrailer -----------------------------

    /**
     * Accessor for the column trailer.
     *
     * @return      the requested trailer
     *
     */
    public @Nonnull String getTrailer()
    {
      return m_trailer;
    }

    //......................................................................
    //------------------------------ getLeader -----------------------------

    /**
     * Accessor for the column leader.
     *
     * @return      the requested leader
     *
     */
    public @Nonnull String getLeader()
    {
      return m_leader;
    }

    //......................................................................
    //------------------------------- getName ------------------------------

    /**
     * Accessor for the column name.
     *
     * @return      the requested name
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    //......................................................................
    //------------------------------- getTitle -----------------------------

    /**
     * Accessor for the column title.
     *
     * @return      the requested title
     *
     */
    public @Nonnull String getTitle()
    {
      return m_title;
    }

    //......................................................................
    //------------------------------ getImageDir ---------------------------

    /**
     * Accessor for the image directory.
     *
     * @return      the image directory
     *
     */
    public @Nullable String getImageDir()
    {
      return m_imageDir;
    }

    //......................................................................
    //------------------------------ isSplitHidden -------------------------

    /**
     * Check if this column is to be hidden when split.
     *
     * @return      true if to hide, false if not
     *
     */
    public boolean isSplitHidden()
    {
      return m_hide;
    }

    //......................................................................

    //------------------------------ toString ------------------------------

    /**
     * Convert the column to a String for simple printing and debugging.
     *
     * @return      a String representation of the object
     *
     */
    @Override
    public String toString()
    {
      return m_width + ":" + m_alignment + ", '" + m_leader + "', '"
        + m_trailer + "' (" + m_name + ")";
    }

    //......................................................................

    //---------------------------- adjustWidth -----------------------------

    /**
     * Create a new Column with the given width.
     *
     * The current value is not changed.
     *
     * @param       inWidth the new width to use
     *
     * @return      a new column with the desired width
     *
     */
    public Column adjustWidth(int inWidth)
    {
      return new Column(inWidth, m_alignment, m_leader, m_trailer, m_name,
                        m_title, m_imageDir, m_hide);
    }

    //......................................................................
  }

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Table -----------------------------

  /**
   * Create the table action.
   *
   */
  public Table()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Sign for left aligned columns. */
  protected static final String s_left   =
    Config.get("resource:commands/writer.table.left", "L");

  /** Sign for right aligned columns. */
  protected static final String s_right  =
    Config.get("resource:commands/table.right", "R");

  /** Sign for centered columns. */
  protected static final String s_center =
    Config.get("resource:commands/table.center", "C");

  /** Sign for block aligned columns. */
  protected static final String s_block  =
    Config.get("resource:commands/table.block", "B");

  /** Width delimiter. */
  protected static final char s_width =
    Config.get("resource:commands/table.width", ':');

  /** The column delimiter. */
  protected static final char s_delimiter =
    Config.get("resource:commands/table.delimiter", ';');

  /** The delimiter for the table in-betweens. */
  protected static final char s_part =
    Config.get("resource:commands/table.part", ',');

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- parse ---------------------------------

  /**
   * Parse the first argument of the table with the table structure.
   *
   * @param       inPattern the pattern to parse
   *
   * @return      a list of columns parsed
   *
   */
  protected static @Nonnull Column []parse(@Nonnull String inPattern)
  {
    if(inPattern.startsWith("#inline#"))
      inPattern = inPattern.substring(8);

    String []patterns = inPattern.split("[" + s_delimiter + "]");

    Column []columns = new Column[patterns.length];

    for(int i = 0; i < patterns.length; i++)
    {
      // check if we have a name
      String name     = "";
      String imageDir = null;

      Matcher matcher =
        Pattern.compile("\\((.*?)\\)").matcher(patterns[i]);

      if(matcher.find())
      {
        name = matcher.group(1);

        imageDir = Strings.getPattern(name, "#(.*?)#$");

        if(imageDir != null)
          name = name.replaceFirst("#.*?#$", "");

        patterns[i] = matcher.replaceFirst("");
      }

      // look for a title
      boolean hide  = true;
      String  title = "";
      matcher = Pattern.compile("\\[(-?)(.*?)\\]").matcher(patterns[i]);

      if(matcher.find())
      {
        title       = matcher.group(2);
        patterns[i] = matcher.replaceFirst("");

        if(matcher.group(1).length() == 1)
          hide = false;
      }

      // find a width, if any
      int width = 1;

      int pos = patterns[i].indexOf(s_width);
      try
      {
        if(pos > 0)
        {
          // negative widths are fixed!
          if(patterns[i].startsWith("f"))
            width = -Integer.parseInt(patterns[i].substring(1, pos));
          else
            width = Integer.parseInt(patterns[i].substring(0, pos));
        }
      }
      catch(NumberFormatException e)
      {
        Log.warning("could not translate '" + patterns[i].substring(0, pos)
                    + "' into a width, ignored");
      }

      // split up the individual parts
      String []parts = patterns[i].substring(Math.max(pos + 1, 0))
        .split("[" + s_part + "]");

      // determine alignment
      Buffer.Alignment alignment = Buffer.Alignment.shortValueOf(parts[0]);

      if(alignment == null)
        alignment = Buffer.Alignment.left;

     // determine leaders and trailers
      String leader = "";
      if(parts.length >= 2)
        leader = parts[1];

      String trailer = "";
      if(parts.length >= 3)
        trailer = parts[2];

      // store it all
      columns[i] = new Column(width, alignment, leader, trailer, name, title,
                              imageDir, hide);
    }

    return columns;
  }

  //........................................................................

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
  @Override
  public void execute(@Nonnull Document inDocument,
                      @Nullable List<? extends Object> inOptionals,
                      @Nullable List<? extends Object> inArguments)
  {
    if(inArguments == null || inArguments.isEmpty())
      throw new IllegalArgumentException("expecting at least one argument");

    // empty table ?
    if(inArguments.size() == 1)
      return;

    // we need the colum information
    Column []columns = parse(inDocument.convert(inArguments.get(0)));

    // now adjust the widths of the columns
    int total = inDocument.getWidth();

    // total all widths
    int widths = 0;
    for(int i = 0; i < columns.length; i++)
      widths += Math.max(1, Math.abs(columns[i].getWidth()));

    // sum up the length of all filling characters
    int filling = 0;
    for(int i = 0; i < columns.length; i++)
      filling += columns[i].getLeader().length()
        + columns[i].getTrailer().length();

    total -= filling;

    // subtract all fixed with columns and all columns with a width below 1
    int fixed  = 0;
    int []adjusted = new int[columns.length];
    for(int i = 0; i < columns.length; i++)
      if(columns[i].getWidth() * total / widths < 1)
      {
        fixed++;

        int diff = 1;
        if(columns[i].getWidth() < 0)
        {
          diff = Math.max(1, -columns[i].getWidth());
          adjusted[i] = columns[i].getWidth();
        }
        else
          adjusted[i] = -1;

        total  -= diff;
        widths -= diff;
      }
      else
        adjusted[i] = columns[i].getWidth();

    // determine ratio and rest
    int rest = total;

    // adjust the widths
    for(int i = 0; i < adjusted.length; i++)
      if(adjusted[i] > 0)
      {
        adjusted[i] = Math.max(1, adjusted[i] * total / widths);

        rest -= adjusted[i];
      }

    // treat the rounding error
    for(int i = adjusted.length, j = adjusted.length - fixed;
        i > 0;
        i--, j--)
      if(adjusted[i - 1] > 0)
      {
        if(j > 0 && rest > 0)
        {
          adjusted[i - 1] += rest / j;
          rest            -= rest / j;
        }
      }
      else
        adjusted[i - 1] *= -1;

    // store the values
    for(int i = 0; i < columns.length; i++)
      columns[i] = columns[i].adjustWidth(adjusted[i]);

    Document.SubDocument []docs = new Document.SubDocument[columns.length];

    // create the buffers, one for each column
    for(int i = 0; i < docs.length; i++)
    {
      docs[i] = inDocument.createSubDocument(columns[i].getWidth());
      docs[i].setAlignment(columns[i].getAlignment());
    }

    // add the text to the buffers
    for(int i = 1; i < inArguments.size(); i += columns.length)
    {
      for(int j = 0; j < columns.length && i + j < inArguments.size(); j++)
      {
        docs[j].add(inArguments.get(i + j));

        // make sure the line is ended
        docs[j].endLine();
      }

      // process all lines of the current table line, until the buffers
      // are all empty
      while(true)
      {
        String []line = new String[docs.length];

        int j;
        for(j = 0; j < line.length; j++)
          line[j] = docs[j].getLine();

        for(j = 0; j < line.length; j++)
          if(line[j] != null)
            break;

        // no filled lines
        if(j >= line.length)
          break;

        for(j = 0; j < line.length; j++)
        {
          inDocument.add(columns[j].getLeader());

          if(line[j] != null && line[j].length() > 0)
            inDocument.add(line[j]);
          else
            inDocument.add(Strings.spaces(columns[j].getWidth()));

          inDocument.add(columns[j].getTrailer());
        }

        inDocument.endLine();
      }
    }
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- simple ---------------------------------------------------------

     /** Some simple tests. */
    @org.junit.Test
    public void testSimple()
    {
      Action action = new Table();

      Document document = new net.ixitxachitls.output.ascii.ASCIIDocument(40);

      action.execute(document, null, com.google.common.collect.ImmutableList.of
                     ("6:L;10:C,|,|;16:R, * , * ",
                      "first",
                      "second",
                      "third",
                      "first again, this time somewhat larger, to see that "
                      + "wordwrapping works",
                      "second, this is short",
                      "third, this is also a bit longer, because two lines "
                      + "should wrap, to have a better test case",
                      "first",
                      "second"));

      assertEquals("table",
                     "first |  second  | *            third * \n"
                   + "first |  second, | *   third, this is * \n"
                   + "again,|  this is | *       also a bit * \n"
                   + "this  |   short  | *  longer, because * \n"
                   + "time  |          | * two lines should * \n"
                   + "somewh|          | *  wrap, to have a * \n"
                   + "at    |          | * better test case * \n"
                   + "larger|          | *                  * \n"
                   + ", to  |          | *                  * \n"
                   + "see   |          | *                  * \n"
                   + "that  |          | *                  * \n"
                   + "wordwr|          | *                  * \n"
                   + "apping|          | *                  * \n"
                   + "works |          | *                  * \n"
                   + "first |  second  | *                  * \n",
                   document.toString());
    }

    //......................................................................
    //----- newlines -------------------------------------------------------

    /** Testing with newlines. */
    public void testNewlines()
    {
      Action action = new Table();

      Document document = new net.ixitxachitls.output.ascii.ANSIDocument(40);

      action.execute(document, null, com.google.common.collect.ImmutableList.of
                     ("1:C;1:R", "\u001B[0mfirst", "second"));

      assertEquals("newline",
                   "        \u001B[0mfirst                     second\n",
                   document.toString());

      document = new net.ixitxachitls.output.ascii.ASCIIDocument(5);

      action.execute(document, null, com.google.common.collect.ImmutableList.of
                     ("5:L", "12345\n"));

      assertEquals("newline", "12345\n", document.toString());
    }

    //......................................................................
    //----- nested ---------------------------------------------------------

    /** Testing nested tables. */
    @org.junit.Test
    public void nested()
    {
      Action action = new Table();

      Document document = new net.ixitxachitls.output.ascii.ASCIIDocument(20);

      action.execute(document, null, com.google.common.collect.ImmutableList.of
                     ("20:L", "first",
                      new net.ixitxachitls.output.commands.Table
                      ("1:R;1:C", new Object [] { "second", "twice"}),
                      "third"));

      assertEquals("nested table",
                   "first               \n"
                   + "    second   twice  \n"
                   + "third               \n",
                   document.toString());
    }

    //......................................................................
    //----- parsing --------------------------------------------------------

    /** Testing the parsing of columns. */
    @org.junit.Test
    public void parsing()
    {
      Column []columns = Table.parse("1:L;2:R;3:C;4:B");

      assertEquals("simple", 4, columns.length);
      assertEquals("simple", "1:left, '', '' ()", columns[0].toString());
      assertEquals("simple", "2:right, '', '' ()", columns[1].toString());
      assertEquals("simple", "3:center, '', '' ()", columns[2].toString());
      assertEquals("simple", "4:block, '', '' ()", columns[3].toString());
      assertNull("simple", columns[0].getImageDir());
      assertTrue("simple", columns[0].isSplitHidden());

      columns = Table.parse("#inline#55:R,start,end (name)[title];"
                            + "f55:,*(name2#dir#)[-title2]");

      assertEquals("complete", 2, columns.length);
      assertEquals("complete", "55:right, 'start', 'end ' (name)",
                   columns[0].toString());
      assertEquals("complete", "-55:left, '*', '' (name2)",
                   columns[1].toString());
      assertNull("complete", columns[0].getImageDir());
      assertEquals("complete", "title", columns[0].getTitle());
      assertTrue("complete", columns[0].isSplitHidden());
      assertEquals("complete", "dir", columns[1].getImageDir());
      assertEquals("complete", "title2", columns[1].getTitle());
      assertFalse("complete", columns[1].isSplitHidden());
    }

    //......................................................................
  }

  //........................................................................
}
