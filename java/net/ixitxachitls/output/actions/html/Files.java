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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.Lists;

import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.resources.Resource;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the files action. This action searches for files in the given
 * directory and creates an HTML format of all the files found.
 *
 * @file          Files.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @example       <PRE>
 * Action action = new Files("test", "test/Products/", "../files",
 *                           "../icons",
 *                           new String [] { "cover", "back", "inside",
 *                                           "contents", "electronic" });
 * // get an execution of the action
 * Execution exec = action.getExecution();
 *
 * // add the arguments
 * exec.startArgument(null);
 * exec.add("WTC 88567");
 * exec.stopArgument();
 *
 * // execute it
 * String result = exec.execute(null);
 * </PRE>
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Files extends Action
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Files --------------------------------

  /**
   * Construct the action, mainly by giving the filess to use. Any of
   * the filess given can be null, in which case they are ignored.
   *
   * @param       inFiles         the relative url to the files
   * @param       inFilesInternal the relative url to the internal files
   * @param       inIcons         the relative url to the icons
   * @param       inOrder         special ordering of the files
   *
   */
  public Files(@Nonnull String inFiles, @Nullable String inFilesInternal,
               @Nonnull String inIcons, @Nonnull String []inOrder)
  {
    m_files         = inFiles;
    m_filesInternal = inFilesInternal;
    m_icons         = inIcons;
    m_order         = Arrays.copyOf(inOrder, inOrder.length);
    m_ordering      = new Comparator<String>()
      {
        public int compare(@Nonnull String inFirst, @Nonnull String inSecond)
        {
          for(int i = 0; i < m_order.length; i++)
          {
            if(inFirst.indexOf(m_order[i]) != -1)
              return -1;

            if(inSecond.indexOf(m_order[i]) != -1)
              return 1;
          }

          // now the normal text ordering
          return inFirst.compareTo(inSecond);
        }

        public boolean equals(@Nonnull Object inValue)
        {
          return this == inValue;
        }

        public int hashCode()
        {
          assert false : "just to make checkstyle happy...";

          return 0;
        }
      };
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The relative url to all the files. */
  private @Nonnull String m_files;

  /** The relative url to all the internal files. */
  private @Nullable String m_filesInternal;

  /** The relative url to the icons. */
  private @Nonnull String m_icons;

  /** A special ordering of files; files with any of these names are ordered
      in the given order. */
  private @Nonnull String []m_order;

  /** The ordering comparator. */
  private @Nonnull Comparator<String> m_ordering;

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
    if(inArguments != null)
      for(int i = 0; i < inArguments.size(); i++)
      {
        String argument = inDocument.convert(inArguments.get(i));
        String optional = null;

        if(inOptionals != null && inOptionals.size() > i
           && inOptionals.get(i) != null)
          optional = inDocument.convert(inOptionals.get(i));

        addFiles(inDocument, argument, optional, m_files, m_filesInternal);
      }
  }

  //........................................................................

  //------------------------------- addFiles -------------------------------

  /**
   * Add the available files to the page.
   *
   * @param       inDocument the document to print to
   * @param       inName     the name of the directory to copy
   * @param       inLimit    the limit to limit copying to
   * @param       inBaseDirs the base directories to read from
   *
   */
  private void addFiles(@Nonnull Document inDocument,
                        @Nonnull String inName, @Nullable String inLimit,
                        @Nonnull String ... inBaseDirs)
  {
    for(String baseDir : inBaseDirs)
    {
      if(baseDir == null)
        continue;

      String dirName = net.ixitxachitls.util.Files.concatenate(baseDir, inName);
      Resource dir = Resource.get(dirName);

      List<String> files = dir.files();

      // sort the files
      Collections.sort(files, m_ordering);

      for(String file : files)
      {
        // ignore thumbnail images
        if(file.matches(".*_thumbnail\\..*"))
          continue;

        // ignore files with no extension and files starting with a "."
        if(!file.matches(".+\\..+"))
          continue;

        // ignore files that don't match the given pattern
        if(inLimit != null)
        {
          if(inLimit.startsWith("~"))
          {
            if(file.matches("(.*/)?" + inLimit.substring(1) + "\\.[^/]*"))
              continue;
          }
          else
            if(!file.matches("(.*/)?" + inLimit + "\\.[^/]*"))
              continue;
        }

        String name = net.ixitxachitls.util.Files.file(file);
        String extension = net.ixitxachitls.util.Files.extension(file);

        if(dir.hasResource(dirName + "/" + name + "_thumbnail" + extension))
          inDocument.add("<span class=\"picture table\">"
                         + "<span class=\"picture-row table-row\">"
                         + "<span class=\"picture-cell table-cell\">"
                         + "<a href=\"" + file + "\">"
                         + "<img src=\"" + dirName + "/" +  name
                         + "_thumbnail" + extension + "\" " + "alt=\""
                         + name + "\"" + " class=\"picture\""
                         + "/>\n"
                         + "</a><br />" + name + "</span></span></span>");
        else
          if(file.matches(".*\\.(png|gif|jpeg|jpg|PNG|GIF|JPG|JPEG)"))
            inDocument.add("<span class=\"picture table\">"
                           + "<span class=\"picture-row table-row\">"
                           + "<span class=\"picture-cell table-cell\">"
                           + "<img src=\"" + file + "\" "
                           + "alt=\"" + name
                           + "\"" + " class=\"picture\"/>"
                           + "</a><br />" + name + "</span></span></span>");
          else
          {
            // we put an icon here and link the original file
            inDocument.add("<span class=\"picture table\">"
                           + "<span class=\"picture-row table-row\">"
                           + "<span class=\"picture-cell table-cell\">"
                           + "<a href=\"" + file + "\">"
                           + "<img src=\"" + m_icons + "/"
                           + extension.substring(1) + ".png\" alt=\"" + name
                           + "\" class=\"picture\"/>"
                           + "</a><br />" + name + "</span></span></span>");

          }
      }
    }
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //--------------------------------------------------------------- nested

    /** A resource used for testing. */
    private static final class TestResource extends Resource
    {
      /**
       * Create the test resource.
       *
       * @param inName the name of the resource
       */
      private TestResource(@Nonnull String inName)
      {
        super("test", null);

        m_name = inName;
      }

      /** The name of the resource. */
      private @Nonnull String m_name;

      @Override
      public @Nonnull List<String> files()
      {
        return Lists.newArrayList
          (m_name + "/back.png",
           m_name + "/back_thumbnail.png",
           m_name + "/contents.png",
           m_name + "/contents_thumbnail.png",
           m_name + "/cover.png",
           m_name + "/cover_thumbnail.png",
           m_name + "/inside.png",
           m_name + "/inside_thumbnail.png");
      }

      @Override
      public boolean hasResource(String inName)
      {
        return files().contains(inName);
      }
    }

    //......................................................................

    /** Setup tests. */
    @org.junit.Before
    public void setUp()
    {
      Resource.preset("test::files/BaseProducts/WTC 88567",
                      new TestResource("test::files/BaseProducts/WTC 88567"));
    }

    /** Teardown tests. */
    @org.junit.After
    public void tearDown()
    {
      Resource.clearPreset("test::files");
    }

    //----- normal ---------------------------------------------------------

    /** Test normal. */
    @org.junit.Test
    public void normal()
    {
      Action action = new Files("test::files", null, "icons",
                                new String [] { "cover", "back", "inside",
                                                "contents", "electronic" });

      net.ixitxachitls.output.html.HTMLDocument doc =
        new net.ixitxachitls.output.html.HTMLDocument("title", "type");

      action.execute(doc, null,
                     com.google.common.collect.ImmutableList.of
                     ("BaseProducts/WTC 88567"));

      String result =
        "<span class=\"picture table\">"
        + "<span class=\"picture-row table-row\">"
        + "<span class=\"picture-cell table-cell\">"
        + "<a href=\"test::files/BaseProducts/WTC 88567/cover.png\">"
        + "<img src=\"test::files/BaseProducts/WTC 88567/cover_thumbnail.png\" "
        + "alt=\"cover\" class=\"picture\"/>\n</a><br />cover"
        + "</span></span></span>"
        + "<span class=\"picture table\">"
        + "<span class=\"picture-row table-row\">"
        + "<span class=\"picture-cell table-cell\">"
        + "<a href=\"test::files/BaseProducts/WTC 88567/back.png\">"
        + "<img src=\"test::files/BaseProducts/WTC 88567/back_thumbnail.png\" "
        + "alt=\"back\" class=\"picture\"/>\n</a><br />back"
        + "</span></span></span>"
        + "<span class=\"picture table\">"
        + "<span class=\"picture-row table-row\">"
        + "<span class=\"picture-cell table-cell\">"
        + "<a href=\"test::files/BaseProducts/WTC 88567/inside.png\">"
        + "<img "
        + "src=\"test::files/BaseProducts/WTC 88567/inside_thumbnail.png\" "
        + "alt=\"inside\" class=\"picture\"/>\n</a><br />inside"
        + "</span></span></span>"
        + "<span class=\"picture table\">"
        + "<span class=\"picture-row table-row\">"
        + "<span class=\"picture-cell table-cell\">"
        + "<a href=\"test::files/BaseProducts/WTC 88567/contents.png\">"
        + "<img "
        + "src=\"test::files/BaseProducts/WTC 88567/contents_thumbnail.png\""
        + " alt=\"contents\" class=\"picture\"/>\n</a><br />contents"
        + "</span></span></span>";

      // now to the execute
      assertEquals("execution did not produce desired result",
                   result, doc.toString());
    }

    //......................................................................
    //----- limit ---------------------------------------------------------

    /** Test normal. */
    @org.junit.Test
    public void limit()
    {
      Action action = new Files("test::files", null, "icons",
                                new String [] { "cover", "back", "inside",
                                                "contents", "electronic" });

      net.ixitxachitls.output.html.HTMLDocument doc =
        new net.ixitxachitls.output.html.HTMLDocument("title", "type");

      action.execute(doc,
                     com.google.common.collect.ImmutableList.of("cover"),
                     com.google.common.collect.ImmutableList.of
                     ("BaseProducts/WTC 88567"));

      String result =
        "<span class=\"picture table\">"
        + "<span class=\"picture-row table-row\">"
        + "<span class=\"picture-cell table-cell\">"
        + "<a href=\"test::files/BaseProducts/WTC 88567/cover.png\">"
        + "<img src=\"test::files/BaseProducts/WTC 88567/cover_thumbnail.png\" "
        + "alt=\"cover\" class=\"picture\"/>\n</a><br />cover"
        + "</span></span></span>";

      // now to the execute
      assertEquals("execution did not produce desired result",
                   result, doc.toString());
    }

    //......................................................................
    //----- not limit ------------------------------------------------------

    /** Test normal. */
    @org.junit.Test
    public void noLimit()
    {
      Action action = new Files("test::files", null, "icons",
                                new String [] { "cover", "back", "inside",
                                                "contents", "electronic" });

      net.ixitxachitls.output.html.HTMLDocument doc =
        new net.ixitxachitls.output.html.HTMLDocument("title", "type");

      action.execute(doc, com.google.common.collect.ImmutableList.of("~cover"),
                     com.google.common.collect.ImmutableList.of
                     ("BaseProducts/WTC 88567"));

      String result =
        "<span class=\"picture table\">"
        + "<span class=\"picture-row table-row\">"
        + "<span class=\"picture-cell table-cell\">"
        + "<a href=\"test::files/BaseProducts/WTC 88567/back.png\">"
        + "<img src=\"test::files/BaseProducts/WTC 88567/back_thumbnail.png\" "
        + "alt=\"back\" class=\"picture\"/>\n</a><br />back"
        + "</span></span></span>"
        + "<span class=\"picture table\">"
        + "<span class=\"picture-row table-row\">"
        + "<span class=\"picture-cell table-cell\">"
        + "<a href=\"test::files/BaseProducts/WTC 88567/inside.png\">"
        + "<img "
        + "src=\"test::files/BaseProducts/WTC 88567/inside_thumbnail.png\" "
        + "alt=\"inside\" class=\"picture\"/>\n</a><br />inside"
        + "</span></span></span>"
        + "<span class=\"picture table\">"
        + "<span class=\"picture-row table-row\">"
        + "<span class=\"picture-cell table-cell\">"
        + "<a href=\"test::files/BaseProducts/WTC 88567/contents.png\">"
        + "<img "
        + "src=\"test::files/BaseProducts/WTC 88567/contents_thumbnail.png\""
        + " alt=\"contents\" class=\"picture\"/>\n</a><br />contents"
        + "</span></span></span>";

      // now to the execute
      assertEquals("execution did not produce desired result",
                   result, doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
