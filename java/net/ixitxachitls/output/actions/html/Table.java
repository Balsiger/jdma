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

package net.ixitxachitls.output.actions.html;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.util.Strings;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This action is used to format HTML tables.
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

    // parse the arguments
    String format = inDocument.convert(inArguments.get(0));

    String tableTag;
    String rowTag;
    String cellTag;
    String tableStyle;
    String rowStyle;
    String cellStyle;

    if(format.startsWith("#inline#"))
    {
      format = format.substring(8);
      tableTag = "span";
      rowTag = "span";
      cellTag = "span";
      tableStyle = "table";
      rowStyle = "table-row";
      cellStyle = "table-cell";
    }
    else
    {
      tableTag = "table";
      rowTag = "tr";
      cellTag = "td";
      tableStyle = "";
      rowStyle = "";
      cellStyle = "";
    }

    if(format.startsWith("#js#"))
    {
      executeJS(inDocument, format.substring(4), inOptionals, inArguments);

      return;
    }

    Column []columns = parse(format);

    // table settings
    inDocument.add("\n<" + tableTag);
    if(inOptionals != null && !inOptionals.isEmpty())
      inDocument.add(Strings.cssClasses(inOptionals.get(0), tableStyle));
    else
      inDocument.add(Strings.cssClasses(tableStyle));
    inDocument.add(">");

    // the column titles (if any)
    int i;
    for(i = 0; i < columns.length; i++)
      if(!columns[i].getTitle().isEmpty())
        break;

    if(i < columns.length)
    {
      // we have titles
      inDocument.add("<" + rowTag + Strings.cssClasses("title", rowStyle)
                     + ">");

      for(i = 0; i < columns.length; i++)
      {
        inDocument.add("<" + cellTag + Strings.cssClasses("title", cellStyle)
                       + ">");

        if(!columns[i].getTitle().isEmpty())
          inDocument.add(columns[i].getTitle());

        inDocument.add("</" + cellTag + ">");
      }

      inDocument.add("</" + rowTag + ">");
    }

    // table columns
    boolean odd = true;
    String  style = "";

    int col = 0;
    for(i = 0; i < inArguments.size() - 1; i++)
    {
      if(inArguments.get(i + 1) == null)
        continue;

      // ignore nopicture cells (this is actually quite ugly, but so far I
      // don't know another way to solve this...
      String arg = inArguments.get(i + 1).toString();

      if(arg.startsWith("\\nopictures"))
        continue;

      // handle inline style changes
      String styleArg = Strings.getPattern(arg, "##(\\w*)##");

      if(styleArg != null)
      {
        style = styleArg;

        continue;
      }

      if(col == 0)
        inDocument.add("<" + rowTag + Strings.cssClasses(rowStyle, style)
                       + ">");

      inDocument.add("<" + cellTag);
      inDocument.add(Strings.cssClasses(columns[col].getName(), cellStyle));
      inDocument.add(">");

      inDocument.add(columns[col].getLeader().replaceAll("[|]", ""));
      inDocument.add(inArguments.get(i + 1));
      inDocument.add(columns[col].getTrailer().replaceAll("[|]", ""));

      inDocument.add("</" + cellTag + ">");

      col++;

      if(col >= columns.length)
        col = 0;

      if((col == 0) || (i + 1 == inArguments.size() - 1))
        inDocument.add("</" + rowTag + ">");
    }

    // table ending
    inDocument.add("</" + tableTag + ">");
  }

  //........................................................................
  //------------------------------ executeJS -------------------------------

  /**
   * Execute the table printing using a javascript setup of the table.
   *
   * @param       inDocument  the document to print to
   * @param       inFormat    the table format to use
   * @param       inOptionals the optional arguments for the table
   * @param       inArguments the table arguments (i.e. cells)
   *
   */
  private void executeJS(@Nonnull Document inDocument, @Nonnull String inFormat,
                         @Nullable List<? extends Object> inOptionals,
                         @Nullable List<? extends Object> inArguments)
  {
    // determine the default split column(s), if any
    String split = Strings.getPattern(inFormat, "^#(.*?)#");

    // remove the leading stuff
    if(split != null)
      inFormat = inFormat.replaceFirst("^#.*?#", "");

    Column []columns = parse(inFormat);

    String id   = "table";
    String name = "";

    if(inOptionals != null && !inOptionals.isEmpty())
    {
      name  = inDocument.convert(inOptionals.get(0));
      id   += "_" + name;
    }

    inDocument.add("<table id='" + id + "' class='" + name + "'></table>");
    inDocument.add("<script type='text/javascript'>\n"
                   + "  var " + id + " = new Table('" + id + "', [" + split
                   + "], '/icons'");

    // the column titles (if any)
    for(int i = 0; i < columns.length; i++)
    {
      if(!columns[i].getName().isEmpty())
        inDocument.add(", '" + columns[i].getName() + "'");
      else
        inDocument.add(", ''");

      if(!columns[i].getTitle().isEmpty())
        inDocument.add(", '" + columns[i].getTitle() + "'");
      else
        inDocument.add(", ''");

      if(columns[i].getImageDir() != null)
        inDocument.add(", '" + columns[i].getImageDir() + "'");
      else
        inDocument.add(", null");

      inDocument.add(", " + columns[i].isSplitHidden());
    }

    inDocument.add(");\n\n");

    // the current document to write to
    Document doc = inDocument;

    if(inArguments != null)
      for(int i = 1; i < inArguments.size(); i++)
      {
        // ignore null arguments
        if(inArguments.get(i) == null)
          continue;

        // ignore nopicture cells (this is actually quite ugly, but so far I
        // don't know another way to solve this...
        if(inArguments.get(i).toString().startsWith("\\nopictures"))
          continue;

        String arg =
          doc.convert(inArguments.get(i)).replaceAll("\\'", "\\\\'")
          .replaceAll("\n", "\\\\n");

        // is there a group and sorting  given?
        String groups = Strings.getPattern(arg, "##(.*?##.*?)##$");

        if(groups != null)
        {
          String []parts = groups.split("##");

          arg = "new Table.Cell('" + arg.replaceAll("##.*?##.*?##$", "")
            + "', '" + (parts.length > 0 ? parts[0] : "") + "', '"
            + (parts.length > 1 ? parts[1] : "")
            + "')";
        }
        else
          arg = "'" + arg + "'";

        // start a new line
        if(i % columns.length == 1)
        {
          if(i != 1)
            doc.add(");\n");

          if(i / columns.length > 0 && (i / columns.length) % 100 == 0)
          {
            int number = (i / columns.length) / 100;
            int total  = (inArguments.size() / columns.length) / 100;

            doc = inDocument.createSubDocument();
            inDocument.addDocument(doc);

            inDocument.add("gui.delayed(\"gui.loadFile('" + id + "', "
                           + number + ", " + total + ");\", "
                           + (i * 10 / columns.length) + ");\n");
          }

          doc.add(id + ".add(" + arg);
        }
        else
          doc.add(", " + arg);
      }

    // close the last argument
    doc.add(");\n");

    inDocument.add("</script>\n");
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

      net.ixitxachitls.output.html.HTMLDocument doc =
        new net.ixitxachitls.output.html.HTMLDocument("title");

      action.execute(doc, null, com.google.common.collect.ImmutableList.of
                     ("10:L(name)[title];20:C[title2],|,|;30:R(name3), * , * ",
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
                   "\n<table>"
                   + "<tr class=\"title\">"
                   + "<td class=\"title\">title</td>"
                   + "<td class=\"title\">title2</td>"
                   + "<td class=\"title\"></td></tr>"
                   + "<tr>"
                   + "<td class=\"name\">first</td>"
                   + "<td>second</td>"
                   + "<td class=\"name3\"> * third * "
                   + "</td></tr>"
                   + "<tr>"
                   + "<td class=\"name\">first again, this time somewhat "
                   + "larger, to see that wordwrapping works</td>"
                   + "<td>second, this is short</td>"
                   + "<td class=\"name3\"> * third, this "
                   + "is also a bit longer, because two lines should wrap, "
                   + "to have a better test case * </td></tr>"
                   + "<tr>"
                   + "<td class=\"name\">first</td>"
                   + "<td>second</td></tr>"
                   + "</table>",
                   doc.toString());

      doc = new net.ixitxachitls.output.html.HTMLDocument("title");
      action.execute(doc,
                     com.google.common.collect.ImmutableList.of("name"),
                     com.google.common.collect.ImmutableList.of
                     ("#js#10:L;20:C,|,|;30:R(name)[title], * , * ",
                      "first",
                      "second",
                      "third",
                      "first again, this time somewhat 'larger', to see that "
                      + "wordwrapping \"works\"",
                      "second, this is short",
                      "third, this is also a bit longer, because two lines "
                      + "should wrap, to have a better test case",
                      "first",
                      "second"));

      assertEquals("execution did not produce desired result",
                   "<table id='table_name' class='name'></table>"
                   + "<script type='text/javascript'>\n"
                   + "  var table_name = new Table('table_name', [null], "
                   + "'/icons', '', '', null, true, '', '', null, true, "
                   + "'name', 'title', null, true);\n\n"
                   + "table_name.add('first', 'second', 'third');\n"
                   + "table_name.add('first again, this time somewhat "
                   + "\\'larger\\', to see that wordwrapping \"works\"', "
                   + "'second, this is short', "
                   + "'third, this is also a bit longer, because two lines "
                   + "should wrap, to have a better test case');\n"
                   + "table_name.add('first', 'second');\n"
                   + "</script>\n",
                   doc.toString());

      doc = new net.ixitxachitls.output.html.HTMLDocument("title");
      action.execute(doc,
                     com.google.common.collect.ImmutableList.of("class"),
                     com.google.common.collect.ImmutableList.of
                     ("#inline#10:L;20:C(name)[title],|,|;30:R, * , * ",
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
                   "\n<span class=\"class table\">"
                   + "<span class=\"title table-row\">"
                   + "<span class=\"title table-cell\"></span>"
                   + "<span class=\"title table-cell\">title</span>"
                   + "<span class=\"title table-cell\"></span>"
                   + "</span>"
                   + "<span class=\"table-row\">"
                   + "<span class=\"table-cell\">first</span>"
                   + "<span class=\"name table-cell\">second</span>"
                   + "<span class=\"table-cell\"> * third * </span>"
                   + "</span>"
                   + "<span class=\"table-row\">"
                   + "<span class=\"table-cell\">"
                   + "first again, this time somewhat larger, to see "
                   + "that wordwrapping works</span>"
                   + "<span class=\"name table-cell\">second, this is short"
                   + "</span>"
                   + "<span class=\"table-cell\"> * third, this "
                   + "is also a bit longer, because two lines should wrap, "
                   + "to have a better test case * </span></span>"
                   + "<span class=\"table-row\">"
                   + "<span class=\"table-cell\">first</span>"
                   + "<span class=\"name table-cell\">second</span></span>"
                   + "</span>",
                   doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
