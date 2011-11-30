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
import net.ixitxachitls.util.resources.Resource;

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
   * Construct the action, mainly by giving the pictures to use.
   *
   * @param       inClass   the type of picture
   * @param       inCaption flag if caption is given
   * @param       inLink    flag if this is a link to real file
   * @param       inSubDir  the sub directory to look in for the image
   *
   */
  public Picture(@Nonnull String inClass, boolean inCaption, boolean inLink,
                 @Nullable String inSubDir)
  {
    m_class   = inClass;
    m_caption = inCaption;
    m_link    = inLink;
    m_subDir  = inSubDir;
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

  /** The name of the sub directory for the pictures. */
  private @Nullable String m_subDir;

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
      throw new
        IllegalArgumentException("expecting at least two arguments");

    int i = 0;

    String target = inDocument.convert(inArguments.get(i++));
    String targetSrc = target;

    // determine if we have a thumbnail image
    boolean thumbnail = Resource.hasThumbnail(targetSrc);
    if(thumbnail)
      targetSrc = Files.asThumbnail(targetSrc);

    String caption = null;
    if(m_caption)
    {
      if(inArguments.size() <= i)
        throw new IllegalArgumentException("expected argument for caption");

      caption = inDocument.convert(inArguments.get(i++));
    }

    String link = null;
    if(m_link)
    {
      if(inArguments.size() <= i)
        throw new IllegalArgumentException("expected argument for link");

      link = inDocument.convert(inArguments.get(i++));
    }

    // overlays, if any
    StringBuilder overlays = new StringBuilder();
    while(i < inArguments.size() - 1)
    {
      Object css = inArguments.get(i++);
      String src = net.ixitxachitls.util.
        Files.concatenate(m_subDir, inArguments.get(i++) + ".png");

      overlays.append("<img src='" + src + "' class='overlay " + css + "'/>");
    }


    String style = "";
    if(inOptionals != null && !inOptionals.isEmpty())
      style = " " + inDocument.convert(inOptionals.get(0));

    if(m_caption)
      inDocument.add("<div class=\"caption-container\">");

    if(m_link)
    {
      inDocument.add("<a class=\"" + m_class + "-link\" ");

      if(link.startsWith("js:"))
        inDocument.add("onclick=\"" + link.substring(3) + "\">");
      else
      {
        inDocument.add("href=\"");
        inDocument.add(link.replace("%", "%25"));
        inDocument.add("\" onclick=\"link(event, '"
                       + link.replace("%", "%25") + "');\">");
      }
    }

    if(overlays.length() > 0)
      inDocument.add("<div class='overlay'>");

    String image = targetSrc;
    if(!image.startsWith("http://"))
      image = net.ixitxachitls.util.Files.concatenate
        (m_subDir, Files.encodeName(targetSrc)).replaceAll("%", "%25");

    inDocument.add("<img src=\"");
    inDocument.add(image);
    inDocument.add("\" alt=\"");
    inDocument.add(Files.file(target));
    inDocument.add("\" class=\"");
    inDocument.add(m_class);
    inDocument.add(style);
    inDocument.add("\"");
    if((thumbnail || target.contains("w=") || target.contains("h=")
        || target.contains("=s")) && !m_link)
    {
      String finalTarget = target.replace("\\", "\\\\")
        .replaceAll("(\\?|&)(w=|h=)\\d+(&|$)", "$1")
        .replaceAll("=s\\d+$", "");

      if(!finalTarget.startsWith("http://"))
        finalTarget = Files.concatenate(m_subDir, Files.encodeName(finalTarget))
          .replace("%", "%25");

      inDocument.add(" onclick=\"util.link(event, '" +  finalTarget + "');\" "
                     + "style=\"cursor: pointer\"");
    }
    inDocument.add("/>");

    if(overlays.length() > 0)
    {
      inDocument.add(overlays);
      inDocument.add("</div>");
    }

    if(m_caption)
    {
      inDocument.add("<div class='caption'>");
      inDocument.add(caption);
      inDocument.add("</div>");
    }

    if(m_link)
      inDocument.add("</a>");

    if(m_caption)
      inDocument.add("</div>");
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- normal ---------------------------------------------------------

    /** Testing normal. */
    @org.junit.Test
    public void normal()
    {
      Action action = new Picture("test", true, true, "sub");

      net.ixitxachitls.output.html.HTMLDocument doc =
        new net.ixitxachitls.output.html.HTMLDocument("title");

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("picture.extension", "caption", "link"));

      assertEquals("execution did not produce desired result",
                   "<div class=\"caption-container\">"
                   + "<a class=\"test-link\" href=\"link\" "
                   + "onclick=\"link(event, 'link');\">"
                   + "<img src=\"sub/picture.extension\" "
                   + "alt=\"picture\" class=\"test\"/>"
                   + "<div class='caption'>caption</div></a>"
                   + "</div>",
                   doc.toString());

      assertTrue("error number", doc.getErrors().isEmpty());
   }

    //......................................................................
    //----- optional -------------------------------------------------------

    /** Testing optional arguments. */
    @org.junit.Test
    public void optional()
    {
      Action action = new Picture("test", true, true, null);

      net.ixitxachitls.output.html.HTMLDocument doc =
        new net.ixitxachitls.output.html.HTMLDocument("title");

      action.execute(doc,
                     com.google.common.collect.ImmutableList.of("highlight"),
                     com.google.common.collect.ImmutableList.of
                     ("picture.extension", "caption", "link"));

      assertEquals("execution did not produce desired result",
                   "<div class=\"caption-container\">"
                   + "<a class=\"test-link\" href=\"link\" "
                   + "onclick=\"link(event, 'link');\">"
                   + "<img src=\"picture.extension\" alt=\"picture\" "
                   + "class=\"test highlight\"/>"
                   + "<div class='caption'>caption</div></a>"
                   + "</div>",
                   doc.toString());

      assertTrue("error number", doc.getErrors().isEmpty());
    }

    //......................................................................
    //----- overlay --------------------------------------------------------

    /** Testing normal. */
    @org.junit.Test
    public void overlay()
    {
      Action action = new Picture("test", true, false, "sub");

      net.ixitxachitls.output.html.HTMLDocument doc =
        new net.ixitxachitls.output.html.HTMLDocument("title");

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("picture.extension", "caption",
                      "css1", "overlay1", "css2", "overlay2",
                      "css3", "overlay3"));

      assertEquals("execution did not produce desired result",
                   "<div class=\"caption-container\">"
                   + "<div class='overlay'>"
                   + "<img src=\"sub/picture.extension\" "
                   + "alt=\"picture\" class=\"test\"/>"
                   + "<img src='sub/overlay1.png' class='overlay css1'/>"
                   + "<img src='sub/overlay2.png' class='overlay css2'/>"
                   + "<img src='sub/overlay3.png' class='overlay css3'/>"
                   + "</div>"
                   + "<div class='caption'>caption</div>"
                   + "</div>",
                   doc.toString());

      assertTrue("error number", doc.getErrors().isEmpty());
   }

    //......................................................................
  }

  //........................................................................
}
