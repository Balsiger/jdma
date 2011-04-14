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

package net.ixitxachitls.output.actions.itext;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Buffer;
import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.output.commands.Bold;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This action is used to format iText tables.
 *
 * @file          Table.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
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

@Immutable
public class Table extends net.ixitxachitls.output.actions.ascii.Table
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------------- Table -------------------------------

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
    if(inArguments == null || inArguments.size() < 1)
      throw new IllegalArgumentException("expecting at least one argument");

    // empty table ?
    if(inArguments.size() == 1)
      return;

    String format = inDocument.convert(inArguments.get(0));
    Column []columns = parse(format);

    String type = "table";
    if(inOptionals != null && !inOptionals.isEmpty())
      type = inDocument.convert(inOptionals.get(0));

    boolean split = true;
    if(type.startsWith("keep"))
      split = false;

    boolean delimiter = false;
    if(type.endsWith("space"))
      delimiter = true;

    String width = "100";
    if(type.startsWith("width-"))
      width = type.replace("width-", "");

    // table settings
    inDocument.add("<table "
                   + "cell-valign=\"baseline\" "
                   + "width=\"" + width + "\" "
                   + "columns=\"" + columns.length + "\" "
                   + "padding=\"0\" "
                   + "class=\"" + type + "\" "
                   + "split-rows=\"" + split + "\" "
                   + "spacing-before=\"0\" "
                   + "spacing-after=\"3\" "
                   + "line-spacing=\"1.1\" "
                   + "keep-together=\"" + (!split) + "\" "
                   + "split-late=\"" + (!split) + "\" "
                   + "widths=\"");

    // add the column width information
    for(int i = 0; i < columns.length; i++)
    {
      if(i > 0)
        inDocument.add(",");

      inDocument.add(Math.abs(columns[i].getWidth()));
    }

    inDocument.add("\">");

    // the column titles (if any)
    int i;
    for(i = 0; i < columns.length; i++)
      if(!columns[i].getTitle().isEmpty())
        break;

    if(i < columns.length)
    {
      // we have titles
      inDocument.add("<table-header border-color=\"black\" "
                     + "border-style=\"bottom\" border-width=\"1\">");

      for(i = 0; i < columns.length; i++)
      {
        if(columns[i].getAlignment() == Buffer.Alignment.block)
          inDocument.add("<cell halign=\"justified\"");
        else
          inDocument.add("<cell halign=\"" + columns[i].getAlignment()
                         + "\"");

        if(delimiter && (i != 0))
          inDocument.add(" padding-left=\"15\"");

        if(columns[i].getWidth() < 0)
          inDocument.add(" no-wrap=\"true\">");
        else
          inDocument.add(">");

        String leader  = columns[i].getLeader();

        if(leader != null && !leader.equals("|"))
            inDocument.add(leader);

        inDocument.add(new Bold(columns[i].getTitle()));

        String trailer = columns[i].getTrailer();

        if(trailer != null && !trailer.equals("|"))
            inDocument.add(trailer);

        inDocument.add("</cell>");
      }

      inDocument.add("</table-header>");
    }

    // table columns
    int count = 0; // count the real number of cells printed
    for(i = 0; i < inArguments.size() - 1; i++)
    {
      if(inArguments.get(i + 1) == null)
        continue;

      String align = "justified";

      if(columns[i % columns.length].getAlignment()
         != Buffer.Alignment.block)
        align = columns[i % columns.length].getAlignment().toString();

      inDocument.add("<cell valign=\"top\" halign=\"" + align + "\"");

      if((count / columns.length) % 2 == 0)
        inDocument.add(" bgcolor=\"" + type + "-even\"");
      else
        inDocument.add(" bgcolor=\"" + type + "-odd\"");

      if(columns[i % columns.length].getWidth() < 0)
        inDocument.add(" no-wrap=\"true\"");

      if(i >= inArguments.size() - 2)
        inDocument.add(" fill-row=\"true\"");

      if(delimiter && (i % columns.length != 0))
        inDocument.add(" padding-left=\"15\"");

      // check if we want to span some columns
      int span = 1;
      for(; (i + span) % columns.length != 0
            && (i + span) < (inArguments.size() - 1)
            && inArguments.get(i + span + 1) == null; span++)
        ;

      if(span >= 2)
        inDocument.add(" colspan=\"" + span + "\"");

      inDocument.add(">");

      String leader  = columns[i % columns.length].getLeader();

      if(leader != null && !leader.equals("|"))
        inDocument.add(leader);

      inDocument.add(inArguments.get(i + 1));

      String trailer = columns[i % columns.length].getTrailer();

      if(trailer != null && !trailer.equals("|"))
        inDocument.add(trailer);

      inDocument.add("</cell>");
      count++;
   }

    // make sure we have enough table cells to fill each row
    if(count > 0)
      for(i = columns.length - (count % columns.length); i > 0; i--)
        inDocument.add("<cell></cell>");

    // table ending
    inDocument.add("</table>");
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- all ------------------------------------------------------------

    /** Test all. */
    @org.junit.Test
    public void all()
    {
      Action action = new Table();

      net.ixitxachitls.output.Document doc =
        new net.ixitxachitls.output.Document();

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("10:L;20:C,|,|;30:R, * , * ",
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

      assertEquals("execution did not produce desired result",
                   "<table cell-valign=\"baseline\" width=\"100\" "
                   + "columns=\"3\" padding=\"0\" class=\"table\" "
                   + "split-rows=\"true\" spacing-before=\"0\" "
                   + "spacing-after=\"3\" line-spacing=\"1.1\" "
                   + "keep-together=\"false\" split-late=\"false\" "
                   + "widths=\"10,20,30\">"
                   + "<cell valign=\"top\" halign=\"left\" "
                   + "bgcolor=\"table-even\">first</cell>"
                   + "<cell valign=\"top\" halign=\"center\" "
                   + "bgcolor=\"table-even\">second</cell>"
                   + "<cell valign=\"top\" halign=\"right\" "
                   + "bgcolor=\"table-even\"> * third * </cell>"
                   + "<cell valign=\"top\" halign=\"left\" "
                   + "bgcolor=\"table-odd\">first again, this time somewhat "
                   + "larger, to see that wordwrapping works</cell>"
                   + "<cell valign=\"top\" halign=\"center\" "
                   + "bgcolor=\"table-odd\">second, this is short</cell>"
                   + "<cell valign=\"top\" halign=\"right\" "
                   + "bgcolor=\"table-odd\"> * third, this is also a bit "
                   + "longer, because two lines should wrap, to have a better "
                   + "test case * </cell>"
                   + "<cell valign=\"top\" halign=\"left\" "
                   + "bgcolor=\"table-even\">first</cell>"
                   + "<cell valign=\"top\" halign=\"center\" "
                   + "bgcolor=\"table-even\" fill-row=\"true\">second</cell>"
                   + "<cell></cell></table>",
                   doc.toString());

      doc = new net.ixitxachitls.output.Document();

      action.execute(doc,
                     com.google.common.collect.ImmutableList.of
                     ("keep-id-space"),
                     com.google.common.collect.ImmutableList.of
                     ("10:L(name)[title];f20:C(name2)[title2],|,|;30:B, * , * ",
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

      assertEquals("execution did not produce desired result",
                   "<table cell-valign=\"baseline\" width=\"100\" "
                   + "columns=\"3\" padding=\"0\" class=\"keep-id-space\" "
                   + "split-rows=\"false\" spacing-before=\"0\" "
                   + "spacing-after=\"3\" line-spacing=\"1.1\" "
                   + "keep-together=\"true\" split-late=\"true\" "
                   + "widths=\"10,20,30\">"
                   + "<table-header border-color=\"black\" "
                   + "border-style=\"bottom\" border-width=\"1\">"
                   + "<cell halign=\"left\">title</cell>"
                   + "<cell halign=\"center\" padding-left=\"15\" "
                   + "no-wrap=\"true\">title2</cell>"
                   + "<cell halign=\"justified\" padding-left=\"15\"> *  * "
                   + "</cell></table-header>"
                   + "<cell valign=\"top\" halign=\"left\" "
                   + "bgcolor=\"keep-id-space-even\">first</cell>"
                   + "<cell valign=\"top\" halign=\"center\" "
                   + "bgcolor=\"keep-id-space-even\" no-wrap=\"true\" "
                   + "padding-left=\"15\">second</cell>"
                   + "<cell valign=\"top\" halign=\"justified\" "
                   + "bgcolor=\"keep-id-space-even\" padding-left=\"15\">"
                   + " * third * </cell><cell valign=\"top\" halign=\"left\" "
                   + "bgcolor=\"keep-id-space-odd\">"
                   + "first again, this time somewhat "
                   + "larger, to see that wordwrapping works</cell>"
                   + "<cell valign=\"top\" halign=\"center\" "
                   + "bgcolor=\"keep-id-space-odd\" no-wrap=\"true\" "
                   + "padding-left=\"15\">second, this is short</cell>"
                   + "<cell valign=\"top\" halign=\"justified\" "
                   + "bgcolor=\"keep-id-space-odd\" padding-left=\"15\">"
                   + " * third, this is also a bit "
                   + "longer, because two lines should wrap, to have a better "
                   + "test case * </cell>"
                   + "<cell valign=\"top\" halign=\"left\" "
                   + "bgcolor=\"keep-id-space-even\">first</cell>"
                   + "<cell valign=\"top\" halign=\"center\" "
                   + "bgcolor=\"keep-id-space-even\" no-wrap=\"true\" "
                   + "fill-row=\"true\" padding-left=\"15\">second</cell>"
                   + "<cell></cell></table>",
                   doc.toString());

      m_logger.addExpected("ERROR: Could not get action for 'bold'");
      m_logger.addExpected("ERROR: Could not get action for 'bold'");
      m_logger.addExpected("ERROR: Could not get action for 'bold'");
    }

    //......................................................................
  }

  //........................................................................
}
