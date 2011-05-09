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

import java.net.URL;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a picture action. It formats a picture and if desired its caption
 * and a link to the real picture.
 *
 * @file          Picture.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 * @example       <PRE>
 * Action action = new Picture("test", "dir", "url", true, true);
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
public class Picture extends Action
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Picture ------------------------------

  /**
   * Construct the action, mainly by giving the pictures to use. Any of
   * the pictures given can be null, in which case they are ignored.
   *
   * @param       inClass   the type of picture
   * @param       inCaption flag if caption is given
   * @param       inLink    flag if this is a link to real file
   *
   */
  public Picture(@Nonnull String inClass, boolean inCaption, boolean inLink)
  {
    this(inClass, inCaption, inLink, 0, 0);
  }

  //........................................................................
  //------------------------------- Picture ------------------------------

  /**
   * Construct the action, mainly by giving the pictures to use. Any of
   * the pictures given can be null, in which case they are ignored.
   *
   * @param       inClass   the type of picture
   * @param       inCaption flag if caption is given
   * @param       inLink    flag if this is a link to real file
   * @param       inWidth   the width of the picture
   * @param       inHeight  the height of the picture
   *
   */
  public Picture(@Nonnull String inClass, boolean inCaption, boolean inLink,
                 int inWidth, int inHeight)
  {
    m_class   = inClass;
    m_caption = inCaption;
    m_link    = inLink;
    m_width   = inWidth;
    m_height  = inHeight;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The class or type of the picture. */
  private @Nonnull String m_class;

  /** Flag if a caption should be printed or not. */
  private boolean m_caption;

  /** Flag if a link should be given or not. */
  private boolean m_link;

  /** The width of the icon to print. */
  private int m_width;

  /** The height of the icon to print. */
  private int m_height;

  /** The name of the picture to display if a picture is not found. */
  private static @Nonnull String s_notFound =
    Config.get("resource:itext/resource.notfound", "icons/not_found.png");

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
    if(inArguments == null || inArguments.size() < 1 || inArguments.size() > 4)
      throw new
        IllegalArgumentException("expecting other number of arguments");

    if(m_caption && m_link
       && inArguments.size() != 3 && inArguments.size() != 4)
      throw new IllegalArgumentException("expecting three or four arguments");

    if(((!m_caption && m_link) || (m_caption && !m_link))
       && inArguments.size() != 2 && inArguments.size() != 3)
      throw new IllegalArgumentException("expecting two or three arguments");

    if(!m_caption && !m_link
       && inArguments.size() != 1 && inArguments.size() != 2)
      throw new IllegalArgumentException("expecting one or two argument");

    String name = inDocument.convert(inArguments.get(0));

    String caption = null;
    if(m_caption)
      caption = inDocument.convert(inArguments.get(1));

    if(name.startsWith("/"))
      name = name.substring(1);

    URL url = Picture.class.getResource(Files.concatenate("/", name));
    if(url == null)
    {
      Log.warning("image file '" + name + "' not found, using placeholder");
      name = s_notFound;
    }

    String size = "";
    if(m_width != 0)
      size += " width=\"" + m_width + "\"";

    if(m_height != 0)
      size += " height=\"" + m_height + "\"";

    if(inOptionals != null && !inOptionals.isEmpty())
    {
      String style = inDocument.convert(inOptionals.get(0));
      if("main-image".equals(style))
        size = " height=\"50\"";
      else if("other-image".equals(style))
        size = " height=\"20\"";
    }

    if(caption == null)
      inDocument.add("<image source=\"" + name + "\" " + size + "/>");
    else
      inDocument.add(new Command("\\table{C}{"
                                 + "<image source=\"" + name + "\" " + size
                                 + "/>}{\\scriptsize{\\color{#AAAAAA}{"
                                 + caption + "}}}"));
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
      Action action = new Picture("test", true, true);

      net.ixitxachitls.output.Document doc =
        new net.ixitxachitls.output.Document();

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("picture.extension", "caption", "link"));

      assertEquals("execution did not produce desired result",
                   "\\table{C}{<image source=\"icons/not_found.png\" />}"
                   + "{\\scriptsize{\\color{#AAAAAA}{caption}}}",
                   doc.toString());

      m_logger.addExpected("WARNING: image file 'picture.extension' "
                           + "not found, using placeholder");
    }

    //......................................................................
    //----- optional -------------------------------------------------------

    /** Test with optional argument. */
    @org.junit.Test
    public void optional()
    {
      Action action = new Picture("test", true, true);

      net.ixitxachitls.output.Document doc =
        new net.ixitxachitls.output.Document();

      action.execute(doc,
                     com.google.common.collect.ImmutableList.of("optional"),
                     com.google.common.collect.ImmutableList.of
                     ("picture.extension", "caption", "link"));

      assertEquals("execution did not produce desired result",
                   "\\table{C}{<image source=\"icons/not_found.png\" />}"
                   + "{\\scriptsize{\\color{#AAAAAA}{caption}}}",
                   doc.toString());

      m_logger.addExpected("WARNING: image file 'picture.extension' "
                           + "not found, using placeholder");
    }

    //......................................................................
  }

  //........................................................................
}
