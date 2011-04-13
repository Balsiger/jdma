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

import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a picture action. It formats a picture and if desired its caption
 * and a link to the real picture.
 *
 * @file          Count.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 * @example       <PRE>
 * Action action = new Count("test", "dir", "url", true, true);
 *
 * // get an execution of the action
 * Execution exec = action.getExecution();
 *
 * // add the arguments
 * exec.startOptionalArgument();
 * exec.add("optional");
 * exec.stopOptionalArgument();
 * exec.startArgument(null);
 * exec.add("picture.extension");
 * exec.stopArgument();
 * exec.startArgument(null);
 * exec.add("caption");
 * exec.stopArgument();
 * exec.startArgument(null);
 * exec.add("link");
 * exec.stopArgument();
 *
 * // now to the execute
 * String result = exec.execute(null));
 * </PRE>
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Count extends Action
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Count --------------------------------

  /**
   * Construct the action.
   *
   */
  public Count()
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
    if(inArguments == null || inArguments.size() != 3)
      throw new IllegalArgumentException("exactly three arguments expected");

    String stepString = "1";
    if(inOptionals != null && !inOptionals.isEmpty())
      stepString = inDocument.convert(inOptionals.get(0));

    String countString = inDocument.convert(inArguments.get(0));
    String maxString   = inDocument.convert(inArguments.get(1));
    String unit        = inDocument.convert(inArguments.get(2));

    // the following seems to have been necessary for a time, don't know why.
    // It causes a lot of problems when embedding a count into another
    // statement, though; thus I removed it temporarily
    //inDocument.add("</phrase><phrase>");
    try
    {
      int count = Integer.parseInt(countString);
      int max   = Integer.parseInt(maxString);
      int step  = Integer.parseInt(stepString);

      if(max > count)
        if(step > 1)
          for(int i = max; i > count; i -= step)
            inDocument.add("<font name=\"count-max\">" + i + "</font>"
                           + "<space />");
        else
          for(int i = max; i > count; i--)
          {
            if(i < max - 10 && i > count + 10
               && ((i >= 100 && (i % 100) != 0)
                   || (i >= 10 && (i % 10) != 0)))
              continue;

            // we need to add the space specially, because it will otherwise be
            // ignored
            inDocument.add("<font name=\"count-max\">" + i + "</font>"
                           + "<space />");
          }

      inDocument.add("<font color=\"count\">");

      if(step > 1)
        for(int i = count; i >= 0; i -= step)
          inDocument.add(i + " ");
      else
      {
        for(int i = count; i >= 0; i--)
        {
          if(i < count - 10 && i > 10
             && ((i >= 100 && (i % 100) != 0)
                 || (i >= 10 && (i % 10) != 0)))
            continue;

          inDocument.add(i + " ");
        }
      }

      inDocument.add("</font>");
    }
    catch(java.lang.NumberFormatException e)
    {
      inDocument.add("*" + countString + "/" + maxString + "*");
    }

    inDocument.add(unit);
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
    public void testAll()
    {
      Action action = new Count();

      net.ixitxachitls.output.Document doc =
        new net.ixitxachitls.output.Document();

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("10", "12", "gp"));

      assertEquals("simple",
                   "<font name=\"count-max\">12</font><space />"
                   + "<font name=\"count-max\">11</font><space />"
                   + "<font color=\"count\">10 9 8 7 6 5 4 3 2 1 0 </font>gp",
                   doc.toString());

      doc = new net.ixitxachitls.output.Document();

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("42", "124", "cp"));

      assertEquals("simple",
                   "<font name=\"count-max\">124</font><space />"
                   + "<font name=\"count-max\">123</font><space />"
                   + "<font name=\"count-max\">122</font><space />"
                   + "<font name=\"count-max\">121</font><space />"
                   + "<font name=\"count-max\">120</font><space />"
                   + "<font name=\"count-max\">119</font><space />"
                   + "<font name=\"count-max\">118</font><space />"
                   + "<font name=\"count-max\">117</font><space />"
                   + "<font name=\"count-max\">116</font><space />"
                   + "<font name=\"count-max\">115</font><space />"
                   + "<font name=\"count-max\">114</font><space />"
                   + "<font name=\"count-max\">100</font><space />"
                   + "<font name=\"count-max\">90</font><space />"
                   + "<font name=\"count-max\">80</font><space />"
                   + "<font name=\"count-max\">70</font><space />"
                   + "<font name=\"count-max\">60</font><space />"
                   + "<font name=\"count-max\">52</font><space />"
                   + "<font name=\"count-max\">51</font><space />"
                   + "<font name=\"count-max\">50</font><space />"
                   + "<font name=\"count-max\">49</font><space />"
                   + "<font name=\"count-max\">48</font><space />"
                   + "<font name=\"count-max\">47</font><space />"
                   + "<font name=\"count-max\">46</font><space />"
                   + "<font name=\"count-max\">45</font><space />"
                   + "<font name=\"count-max\">44</font><space />"
                   + "<font name=\"count-max\">43</font><space />"
                   + "<font color=\"count\">42 41 40 39 38 37 36 35 34 33 32 "
                   + "30 20 10 9 8 7 6 5 4 3 2 1 0 </font>cp",
                   doc.toString());

      doc = new net.ixitxachitls.output.Document();

      action.execute(doc,
                     com.google.common.collect.ImmutableList.of("2"),
                     com.google.common.collect.ImmutableList.of
                     ("17", "33", "sp"));

      assertEquals("simple",
                   "<font name=\"count-max\">33</font><space />"
                   + "<font name=\"count-max\">31</font><space />"
                   + "<font name=\"count-max\">29</font><space />"
                   + "<font name=\"count-max\">27</font><space />"
                   + "<font name=\"count-max\">25</font><space />"
                   + "<font name=\"count-max\">23</font><space />"
                   + "<font name=\"count-max\">21</font><space />"
                   + "<font name=\"count-max\">19</font><space />"
                   + "<font color=\"count\">17 15 13 11 9 7 5 3 1 </font>sp",
                   doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
