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
import net.ixitxachitls.util.Files;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a link action. With this action, links can be inserted. These links
 * will also check if their targets exist.
 *
 * @file          Link.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Link extends Action
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Link ------------------------------

  /**
   * Construct the action, mainly by giving the links to use. Any of
   * the links given can be null, in which case they are ignored.
   *
   * @param       inName      the name of the action
   * @param       inDir       the relative subdirectory for the link
   * @param       inExtension the extension to use
   *
   */
  public Link(@Nonnull String inName, @Nullable String inDir,
              @Nonnull String inExtension)
  {
    m_name      = inName;
    m_dir       = inDir;
    m_extension = inExtension;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The name (class) of the link. */
  private @Nonnull String m_name;

  /** The relative sub directory. */
  private @Nullable String m_dir;

  /** The extension to use for the target. */
  private @Nonnull String m_extension;

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
    if(inArguments == null || inArguments.size() != 1)
      throw new IllegalArgumentException("expecting an argument");

    String target = inDocument.convert(inArguments.get(0));

    String name = null;
    String id   = m_name;
    if(inOptionals != null && !inOptionals.isEmpty())
    {
      name = inDocument.convert(inOptionals.get(0));

      if(inOptionals.size() > 1)
        id = inDocument.convert(inOptionals.get(1));
    }
    else
      // remove all html stuff
      name = target.replaceAll("<.*?>", "");

    // remove % signs
    name = name.replaceAll("%", "%25");

    if(!name.equals("index") && !name.matches(".*/index")
       && !name.startsWith("http://") && !name.startsWith("/index/")
       && !"/".equals(name))
    {
      String dir = Files.path(name);
      name = dir + Files.encodeName(name.substring(dir.length()));

      if(Files.extension(name).isEmpty())
        name += m_extension;
    }

    // the replacement in the target remove all paths that may be available,
    // but should not remove text in '<..>'
    if(!name.startsWith("http://"))
      inDocument.add("<a href=\""
                     + Files.concatenate(m_dir, name)
                     + "\" class=\"" + id + "\" "
                     + "onclick=\"return util.link(event, '"
                     + Files.concatenate(m_dir, name)
                     + "');\">"
                     + target + "</a>");
    else
      inDocument.add("<a href=\"" + name + "\" class=\""
                     + id + "\" onclick=\"return util.link(event, '"
                     + Files.concatenate(m_dir, name)
                     + "');\">" + target.replaceAll("^[^<>]*/", "")
                     + "</a>");
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- normal ---------------------------------------------------------

    /** Normal tests. */
    @org.junit.Test
    public void normal()
    {
      Action action = new Link("test", null, ".ext");

      net.ixitxachitls.output.html.HTMLDocument doc =
        new net.ixitxachitls.output.html.HTMLDocument("title", "type");

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("target_dir/target"));

      assertEquals("execution did not produce desired result",
                   "<a href=\"target_dir/target.ext\" class=\"test\" "
                   + "onclick=\"return util.link(event, "
                   + "'target_dir/target.ext');\">"
                   + "target_dir/target</a>",
                   doc.toString());
    }

    //......................................................................
    //----- optional -------------------------------------------------------

    /** Test with optional arguments. */
    @org.junit.Test
    public void optional()
    {
      Action action = new Link("test", null, ".ext");

      net.ixitxachitls.output.html.HTMLDocument doc =
        new net.ixitxachitls.output.html.HTMLDocument("title", "type");

      action.execute(doc,
                     com.google.common.collect.ImmutableList.of("optional",
                                                                "id"),
                     com.google.common.collect.ImmutableList.of("target"));

      // now to the execute
      assertEquals("execution did not produce desired result",
                   "<a href=\"optional.ext\" class=\"id\" "
                   + "onclick=\"return util.link(event, 'optional.ext');\">"
                   + "target</a>",
                   doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
