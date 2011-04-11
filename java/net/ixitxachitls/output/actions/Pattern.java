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

import java.util.List;
import java.util.regex.Matcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.commands.BaseCommand;
import net.ixitxachitls.util.Encodings;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a pattern action, allowing to fill a pattern with the arguments
 * found. In the pattern '$i' stands for the i-th argument, while $0 is the
 * optional argument, if any. Text enclosed in '[[' and ']]' is removed if
 * no optional argument is given.
 *
 * @file          Pattern.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Pattern extends Action
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Pattern ------------------------------

  /**
   * Construct the action, mainly by giving the patterns to use. Any of
   * the patterns given can be null, in which case they are ignored.
   *
   * @param       inPattern  the pattern to replace in
   *
   */
  public Pattern(@Nonnull String inPattern)
  {
    this(inPattern, false);
  }

  //........................................................................
  //------------------------------- Pattern ------------------------------

  /**
   * Construct the action, mainly by giving the patterns to use. Any of
   * the patterns given can be null, in which case they are ignored.
   *
   * @param       inPattern      the pattern to replace in
   * @param       inWithCommands flag to denote if pattern contains or
   *                             produces commands to parse again
   *
   */
  public Pattern(@Nonnull String inPattern, boolean inWithCommands)
  {
    m_pattern  = inPattern;
    m_commands = inWithCommands;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The pattern to use. */
  private @Nonnull String m_pattern;

  /** Flag if commands are to be parsed. */
  private boolean m_commands = false;

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
    String result = m_pattern;

    // optional argument
    if(inOptionals != null)
      for(int i = 0; i < inOptionals.size(); i++)
      {
        String optional = inDocument.convert(inOptionals.get(i));
        result = result.replaceAll("(?<!\\\\)\\%" + (i + 1) + "(?!\\d)",
                                   optional.replaceAll("\\\\", "\\\\\\\\")
                                   .replaceAll("\\%", "\\\\\\%"));
      }

    // replace all optional arguments not replaced so far
    result = result.replaceAll("\\[\\[[^\\]]*?(?<!\\\\)\\%\\d[^\\]]*?\\]\\]",
                               "");
    result = result.replaceAll("(?<!\\\\)\\%\\d+", "");

    // replace optionals that were replaced
    result = result.replaceAll("\\[\\[", "").replaceAll("\\]\\]", "");

    if(inArguments != null)
      for(int i = 0; i < inArguments.size(); i++)
      {
        Object arg = inArguments.get(i);
        if(arg == null)
          arg = "";

        String argument = inDocument.convert(arg);

        result = result.replaceAll("(?<!\\\\)\\$" + (i + 1) + "(?!\\d)",
                                   argument.replaceAll("\\\\", "\\\\\\\\")
                                   .replaceAll("\\$", "\\\\\\$"));
        result = result.replaceAll("(?<!\\\\)\\@" + (i + 1) + "(?!\\d)",
                                   arg.toString().replaceAll("\\\\", "\\\\\\\\")
                                   .replaceAll("\\$", "\\\\\\$"));
      }

    // replace special counters
    result = result.replaceAll("(?<!\\\\)\\$count\\b", ""
                               + inDocument.getCounter());

    // replace html encodings
    StringBuffer buffer = new StringBuffer();

    java.util.regex.Pattern pattern =
      java.util.regex.Pattern.compile
      ("(?<!\\\\)\\$html\\(\\((.*?)\\)\\)", java.util.regex.Pattern.DOTALL);

    Matcher matcher = pattern.matcher(result);

    while(matcher.find())
      matcher.appendReplacement
        (buffer,
         Encodings.encodeHTMLAttribute
         (matcher.group(1).replaceAll("\\\\", "\\\\\\\\")
          .replaceAll("\\$", "\\\\\\$")));

    matcher.appendTail(buffer);

    result = buffer.toString();

    if(m_commands)
      inDocument.add(new BaseCommand(result));
    else
      inDocument.add(result);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- normal ---------------------------------------------------------

    /** Normal test. */
    @org.junit.Test
    public void normal()
    {
      Action action = new Pattern("\\this is $0 a $1 \\$2 $3, not again $2, "
                                  + "$33");

      Document doc = new Document();

      action.execute(doc, null, com.google.common.collect.ImmutableList.of
                     ("first", "second" , "third"));

      // now to the execute
      assertEquals("execution did not produce desired result",
                   "\\this is $0 a first \\$2 third, not again second, $33",
                   doc.toString());
     }

    //......................................................................
    //----- optional -------------------------------------------------------

    /** Test of optional arguments. */
    @org.junit.Test
    public void optional()
    {
      Action action = new Pattern("this is [[%1]] a $1 \\$2 $3, not again "
                                  + "$2, [[*%2* ]]$33 %3");

      Document doc = new Document();

      action.execute(doc, com.google.common.collect.ImmutableList.of
                     ("optional", "opt2", "opt3"),
                     com.google.common.collect.ImmutableList.of
                     ("first", "second", "third"));

      // now to the execute
      assertEquals("execution did not produce desired result",
                   "this is optional a first \\$2 third, not again second, "
                   + "*opt2* $33 opt3",
                   doc.toString());

      doc = new Document();

      action.execute(doc,
                     com.google.common.collect.ImmutableList.of("optional"),
                     com.google.common.collect.ImmutableList.of("first",
                                                                "second",
                                                                "third"));

      // now to the execute
      assertEquals("execution did not produce desired result",
                   "this is optional a first \\$2 third, not again second, "
                   + "$33 ",
                   doc.toString());
     }

    //......................................................................
    //----- nested ---------------------------------------------------------

    /** Testing nested patterns. */
    @org.junit.Test
    public void nested()
    {
      assertEquals("test", "ab\\ba",
                   "a$1a".replaceAll("(?<!\\\\)\\$" + 1 + "(?!\\d)",
                                    "b\\b".replaceAll("\\\\", "\\\\\\\\")
                                     .replaceAll("\\$", "\\\\\\$")));

      net.ixitxachitls.output.html.HTMLDocument doc =
        new net.ixitxachitls.output.html.HTMLDocument("title");

      doc.add(new net.ixitxachitls.output.commands.Divider("1",
                                                           "just some \\ "
                                                           + "test"));

      assertEquals("nested",
                   "<div class=\"1\">just some \\ test</div>",
                   doc.toString());

      doc = new net.ixitxachitls.output.html.HTMLDocument("title");

      doc.add(new net.ixitxachitls.output.commands
              .Divider("1", new net.ixitxachitls.output.commands
                       .Divider("2", "just some \\ test")));

      assertEquals("nested",
                   "<div class=\"1\"><div class=\"2\">just some \\ test"
                   + "</div></div>", doc.toString());

      // nested string commands
      Pattern pattern = new Pattern("\\bold{$1}", true);

      doc = new net.ixitxachitls.output.html.HTMLDocument("title");

      pattern.execute(doc, com.google.common.collect.ImmutableList.of(),
                      com.google.common.collect.ImmutableList.of("guru"));

      assertEquals("commands", "<strong>guru</strong>", doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
