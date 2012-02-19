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
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.output.html.HTMLDocument;
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
public class Menu extends Action
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------------- Menu --------------------------------

  /**
   * Construct the action.
   *
   * @param       inIconDir the relative directory to the icons
   *
   */
  public Menu(@Nonnull String inIconDir)
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
  @Override
@SuppressWarnings("unchecked")
  public void execute(@Nonnull Document inDocument,
                      @Nullable List<? extends Object> inOptionals,
                      @Nullable List<? extends Object> inArguments)
  {
    if(inArguments == null || inArguments.isEmpty())
      throw new IllegalArgumentException("expecting at least one argument");

    if(!(inDocument instanceof HTMLDocument))
      throw new IllegalArgumentException("for HTML documents only, silly");

    if(inArguments.size() % 3 != 0)
      throw new IllegalArgumentException("must have multiple of three "
                                         + "arguments");

    String id = "navigation";

    if(inOptionals != null && !inOptionals.isEmpty())
      id = inDocument.convert(inOptionals.get(0));

    Map<String, Object> menus = new TreeMap<String, Object>();

    for(int i = 0; i < inArguments.size(); i += 3)
    {
      String group = inDocument.convert(inArguments.get(i));
      String arg   = inDocument.convert(inArguments.get(i + 1));
      String link  = inDocument.convert(inArguments.get(i + 2));

      String []parts = group.split("::");

      Map<String, Object> cur = menus;

      // add up all groups
      for(int j = 0; j < parts.length; j++)
      {
        Map<String, Object> sub = (Map<String, Object>)cur.get(parts[j]);

        if(sub == null)
          sub = new TreeMap<String, Object>();

        cur.put(parts[j], sub);

        cur = sub;
      }

      // add the real link
      cur.put(arg, link);
    }

    inDocument.add("<ul class='" + id + "-" + 0 + "'>");

    for(Map.Entry<String, Object> menu : menus.entrySet())
      addMenuEntries(inDocument, id, 1, menu.getKey(), menu.getValue());

    inDocument.add("</ul><div style='clear: both'></div>");
  }

  //........................................................................

  //---------------------------- addMenuEntries ----------------------------

  /**
   * Print the individual menu entries, recursive.
   *
   * @param       inDocument the document to print to
   * @param       inID       the class id (prefix) to use for printing
   * @param       inLevel    the level of printing
   * @param       inKey      the key to print
   * @param       inEntries  the individual menu entries (this is either a
   *                         simple string, i.e. the link, or a map with sub
   *                         entries)
   *
   */
  @SuppressWarnings("unchecked")
  private void addMenuEntries(@Nonnull Document inDocument,
                              @Nonnull String inID, int inLevel,
                              @Nonnull String inKey, @Nonnull Object inEntries)
  {
    if(inEntries instanceof String)
      inDocument.add("<li><a href='" + (String)inEntries + "'>" + inKey
                     + "</a></li>");
    else
    {
      inDocument.add("<li onmouseover=\"this.childNodes[1].style.display="
                     + "'block'\" "
                     + "onmouseout=\"this.childNodes[1].style.display="
                     + "'none'\">" + inKey + "<ul class='" + inID + "-"
                     + inLevel + "'>");

      for(String key : ((Map<String, Object>)inEntries).keySet())
        addMenuEntries(inDocument, inID, inLevel + 1, key,
                       ((Map<String, Object>)inEntries).get(key));

      inDocument.add("</ul></li>");
    }
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
      Action action = new Menu("icons");

      HTMLDocument doc = new HTMLDocument("title");

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("group 1", "menu 1", "link 1", "group 2", "menu 2",
                      "link 2"));

      assertEquals("test",
                   "<ul class=\'navigation-0'>"
                   + "<li onmouseover=\"this.childNodes[1].style.display="
                   + "'block'\" "
                   + "onmouseout=\"this.childNodes[1].style.display='none'\">"
                   + "group 1"
                   + "<ul class='navigation-1'>"
                   + "<li><a href='link 1'>menu 1</a></li>"
                   + "</ul></li>"
                   + "<li onmouseover=\"this.childNodes[1].style.display="
                   + "'block'\" "
                   + "onmouseout=\"this.childNodes[1].style.display='none'\">"
                   + "group 2"
                   + "<ul class='navigation-1'>"
                   + "<li><a href='link 2'>menu 2</a></li>"
                   + "</ul></li>"
                   + "</ul><div style='clear: both'></div>",
                   doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
