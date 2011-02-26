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

package net.ixitxachitls.output.commands;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Buffer;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The table command.
 *
 * @file          Table.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Table extends BaseCommand
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Table --------------------------------

  /**
   * The constructor for the table command.
   *
   * @param       inLengths    the widths of each column in percents if
   *                           positive and in fixed characters if negative
   * @param       inAlignments the alignments of each individual cell
   * @param       inDelimiters the delimiters between the columns, two for
   *                           each cell
   * @param       inCells      the contents of the cells themselves
   *
   * @undefined   IllegalArgumentException if lengths or alignments not given
   *                                       or if sizes do not match
   *
   */
  public Table(int []inLengths, @Nonnull Buffer.Alignment []inAlignments,
               @Nonnull String []inDelimiters, @Nonnull Object []inCells)
  {
    this(createFormat(inLengths, inAlignments, inDelimiters), inCells);
  }

  //........................................................................
  //--------------------------------- Table --------------------------------

  /**
   * The constructor for the table command.
   *
   * @param       inType       the type of the table
   * @param       inLengths    the widths of each column in percents if
   *                           positive and in fixed characters if negative
   * @param       inAlignments the alignments of each individual cell
   * @param       inDelimiters the delimiters between the columns, two for
   *                           each cell
   * @param       inCells      the contents of the cells themselves
   *
   * @undefined   IllegalArgumentException if lengths or alignments not given
   *                                       or if sizes do not match or if no
   *                                       type is present
   *
   */
  public Table(@Nonnull String inType, int []inLengths,
               @Nonnull Buffer.Alignment []inAlignments,
               @Nonnull String []inDelimiters,
               @Nonnull Object []inCells)
  {
    this(inLengths, inAlignments, inDelimiters, inCells);

    withOptionals(inType);
  }

  //........................................................................
  //--------------------------------- Table --------------------------------

  /**
   * The constructor for the table command.
   *
   * @param       inType       the type of the table
   * @param       inLengths    the widths of each column in percents if
   *                           positive and in fixed characters if negative
   * @param       inAlignments the alignments of each individual cell
   * @param       inDelimiters the delimiters between the columns, two for
   *                           each cell
   * @param       inCommand    the command with all the cells
   *
   * @undefined   IllegalArgumentException if lengths or alignments not given
   *                                       or if sizes do not match or if no
   *                                       type is present
   *
   */
  public Table(@Nonnull String inType, int []inLengths,
               @Nonnull Buffer.Alignment []inAlignments,
               @Nonnull String []inDelimiters,
               @Nonnull Command inCommand)
  {
    this(createFormat(inLengths, inAlignments, inDelimiters), inCommand);

    withOptionals(inType);
  }

  //........................................................................
  //--------------------------------- Table --------------------------------

  /**
   * The constructor for the table command.
   *
   * @param       inLengths    the widths of each column in percents if
   *                           positive and in fixed characters if negative
   * @param       inAlignments the alignments of each individual cell
   * @param       inDelimiters the delimiters between the columns, two for
   *                           each cell
   * @param       inCommand    the command with all the cells
   *
   * @undefined   IllegalArgumentException if lengths or alignments not given
   *                                       or if sizes do not match or if no
   *                                       type is present
   *
   */
  public Table(int []inLengths, @Nonnull Buffer.Alignment []inAlignments,
               @Nonnull String []inDelimiters, @Nonnull Command inCommand)
  {
    this(createFormat(inLengths, inAlignments, inDelimiters), inCommand);
  }

  //........................................................................
  //--------------------------------- Table --------------------------------

  /**
   * The constructor for the table command.
   *
   * @param       inFormat the format describing the table
   * @param       inCells  the cells of the table
   *
   */
  public Table(@Nonnull String inFormat, @Nonnull Object []inCells)
  {
    this();

    withArguments(inFormat);
    withArguments(inCells);
  }

  //........................................................................
  //--------------------------------- Table --------------------------------

  /**
   * The constructor for the table command.
   *
   * @param       inType   the type of the table
   * @param       inFormat the format describing the table
   * @param       inCells  the cell contents of the table
   *
   */
  public Table(@Nonnull String inType, @Nonnull String inFormat,
               @Nonnull Object []inCells)
  {
    this(inFormat, inCells);

    withOptionals(inType);
  }

  //........................................................................
  //--------------------------------- Table --------------------------------

  /**
   * The constructor for the table command.
   *
   * @param       inType   the type of the table
   * @param       inFormat the format describing the table
   * @param       inCells  the cell contents of the table
   *
   */
  public Table(@Nonnull String inType, @Nonnull String inFormat,
               @Nonnull Command inCells)
  {
    this(inFormat, inCells);

    withOptionals(inType);
  }

  //........................................................................
  //--------------------------------- Table --------------------------------

  /**
   * The constructor for the table command.
   *
   * @param       inFormat the format describing the table
   * @param       inCells  the cell contents of the table
   *
   */
  public Table(@Nonnull String inFormat, @Nonnull Command inCells)
  {
    this();

    withArguments(inFormat);

    List<Object> arguments = inCells.getArguments();
    if(arguments.isEmpty())
      withArguments(inCells);
    else
      withArguments(arguments);
  }

  //........................................................................
  //--------------------------------- Table --------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected Table()
  {
    super(TABLE, 1, -1);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Command for formatting tables. */
  public static final String TABLE =
    Config.get("resource:commands/table", "table");

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //----------------------------- createFormat -----------------------------

  /**
   * Create the format string from the given arguments.
   *
   * @param       inLengths    the widths of each column in percents if
   *                           positive and in fixed characters if negative
   * @param       inAlignments the alignments of each individual cell
   * @param       inDelimiters the delimiters between the columns, two for
   *                           each cell
   *
   * @return      a String representing the format to be used for output
   *
   */
  protected static String createFormat(int []inLengths,
                                       @Nonnull Buffer.Alignment []inAlignments,
                                       @Nonnull String []inDelimiters)
  {
    StringBuilder format = new StringBuilder();

    for(int i = 0; i < inLengths.length; i++)
    {
      if(inLengths[i] < 0)
        format.append("f" + (-inLengths[i]));
      else
        format.append(inLengths[i]);

      if(inAlignments[i] != null)
        format.append(":" + inAlignments[i].getShort());
      else
        format.append(":" + Buffer.Alignment.left.getShort());

      if(inDelimiters != null)
        if(inDelimiters.length > (i * 2))
        {
          format.append(",");
          if(inDelimiters[i * 2] != null)
            format.append(inDelimiters[i * 2]);

          if(inDelimiters.length > (i * 2 + 1))
          {
            format.append(",");
            if(inDelimiters[i * 2 + 1] != null)
              format.append(inDelimiters[i * 2 + 1]);
          }
        }

      if(i + 1 < inLengths.length)
        format.append(";");
    }

    return format.toString();
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** Test test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- arguments ------------------------------------------------------

    /** Testing arguments. */
    @org.junit.Test
    public void arguments()
    {
      Command command = new Table("1:L;f2:C,|,:", new Object []
        { "first", "second" });
      assertEquals("table", "\\table{1:L;f2:C,|,:}{first}{second}",
                   command.toString());

      command = new Table(new int [] { 1, -2 }, new Buffer.Alignment []
        { Buffer.Alignment.left, Buffer.Alignment.center }, new String []
        { null, "|", ":" }, new Object []
        { "first", "second" });
      assertEquals("table", "\\table{1:L,,|;f2:C,:}{first}{second}",
                   command.toString());

      command = new Table("table", new int [] { 1, -2 },
                          new Buffer.Alignment []
        { Buffer.Alignment.left, Buffer.Alignment.center }, new String []
        { null, "|", ":" }, new Object []
        { "first", "second" });
      assertEquals("table", "\\table[table]{1:L,,|;f2:C,:}{first}{second}",
                   command.toString());

      command = new Table("1:L;f2:C,|,:", new Command(new Object []
        { "first", "second" }));
      assertEquals("table", "\\table{1:L;f2:C,|,:}{first}{second}",
                   command.toString());

      command = new Table(new int [] { 1, -2 }, new Buffer.Alignment []
        { Buffer.Alignment.left, Buffer.Alignment.center }, new String []
        { null, "|", ":" }, new Command(new Object []
          { "first", "second" }));
      assertEquals("table", "\\table{1:L,,|;f2:C,:}{first}{second}",
                   command.toString());

      command = new Table("table", new int [] { 1, -2 },
                          new Buffer.Alignment []
        { Buffer.Alignment.left, Buffer.Alignment.center }, new String []
        { null, "|", ":" }, new Command(new Object []
          { "first", "second" }));
      assertEquals("table", "\\table[table]{1:L,,|;f2:C,:}{first}{second}",
                   command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
