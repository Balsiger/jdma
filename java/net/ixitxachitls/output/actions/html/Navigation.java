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
import net.ixitxachitls.output.html.HTMLDocument;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a Navigation action. This action replaces a navigationtion with
 * its HTML equivalent.
 *
 * @file          Navigation.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 * @example       <PRE>
 * Action action = new Navigation("test");
 *
 * // get an execution of the action
 * Execution exec = action.getExecution();
 *
 * // add the arguments
 * exec.startArgument(null);
 * exec.add("1");
 * exec.stopArgument();
 * exec.startArgument(null);
 * exec.add("2");
 * exec.stopArgument();
 *
 * // now to the execute
 * String result = exec.execute(null);
 * </PRE>
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Navigation extends Action
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Navigation -----------------------------

  /**
   * Construct the action.
   *
   * @param       inIconDir the relative directory to the icons
   *
   */
  public Navigation(@Nonnull String inIconDir)
  {
    m_icons = inIconDir;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The relative directory to the icons. */
  private @Nonnull String m_icons;

  /** The base directory for pictures. */
  private static final @Nonnull String s_resources =
    Config.get("resource:html/dir.resources", "html");

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
    if(inArguments == null || inArguments.isEmpty())
      throw new IllegalArgumentException("expecting at least one argument");

    if(!(inDocument instanceof HTMLDocument))
      throw new IllegalArgumentException("for HTML documents only, silly");

    if(inArguments.size() % 2 != 0)
      throw new IllegalArgumentException("must have an even number of "
                                         + "arguments");

    String id = "navigation";

    if(inOptionals != null && inOptionals.isEmpty())
      id = inDocument.convert(inOptionals.get(0));

    inDocument.add("<span class=\"" + id + "\">");
    inDocument.add("<span class=\"" + id + "-top\"></span>");

    for(int i = 0; i < inArguments.size(); i += 2)
    {
      String arg   = inDocument.convert(inArguments.get(i));
      String link  = inDocument.convert(inArguments.get(i + 1));

      inDocument.add("<span class=\"" + id + "-" + arg.replaceAll(" ", "_")
                     + " " + id + "-part\" id=\"" + id + "-"
                     + arg.replaceAll(" ", "_") + "\">"
                     + "<a href=\"" + link + "\">");
      inDocument.add("<img src=\"/" + m_icons
                     + "/" + id + "_" + arg + ".png\" alt=\"" + id + "\"");
      inDocument.add(" onmouseover=\"gui.iconHighlight(this)\" "
                     + "onmouseout=\"gui.iconNormal(this)\"");
      inDocument.add("/>\n");
      inDocument.add("</a>\n"
                     + "</span>\n");
    }

    inDocument.add("\n<span class=\"" + id + "-bottom\"></span>");
    inDocument.add("</span>\n");
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
      Action action = new Navigation("icons");

      HTMLDocument doc = new HTMLDocument("title", "type");

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("menu 1", "link 1", "menu 2", "link 2"));

      assertEquals("test",
                   "<span class=\"navigation\">"
                   + "<span class=\"navigation-top\"></span>"
                   + "<span class=\"navigation-menu_1 navigation-part\" "
                   + "id=\"navigation-menu_1\">"
                   + "<a href=\"link 1\">"
                   + "<img src=\"/icons/navigation_menu 1.png\" "
                   + "alt=\"navigation\" "
                   + "onmouseover=\"gui.iconHighlight(this)\" "
                   + "onmouseout=\"gui.iconNormal(this)\"/>\n"
                   + "</a>\n"
                   + "</span>\n"
                   + "<span class=\"navigation-menu_2 navigation-part\" "
                   + "id=\"navigation-menu_2\">"
                   + "<a href=\"link 2\">"
                   + "<img src=\"/icons/navigation_menu 2.png\" "
                   + "alt=\"navigation\" "
                   + "onmouseover=\"gui.iconHighlight(this)\" "
                   + "onmouseout=\"gui.iconNormal(this)\"/>\n"
                   + "</a>\n"
                   + "</span>\n\n"
                   + "<span class=\"navigation-bottom\"></span>"
                   + "</span>\n",
                   doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
