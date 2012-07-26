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

package net.ixitxachitls.util.resources;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.appengine.api.utils.SystemProperty;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A resource for the file system with special variable translation.
 *
 * @file          TemplateResource.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class TemplateResource extends FileResource
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------- TemplateResource ----------------------------

  /**
   * Create the template file resource.
   *
   * @param    inName   the name of the file this resource represents
   * @param    inURL    the url to the resource
   * @param    inPrefix the prefix into the config for template values
   *
   */
  TemplateResource(@Nonnull String inName, @Nullable URL inURL,
                   @Nonnull String inPrefix)
  {
    super(inName, inURL);

    m_prefix = inPrefix;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The prefix into the config for template values. */
  private @Nonnull String m_prefix;

  /** The contents of the file (caching). */
  private @Nullable String m_content;

  /** Flag if files should be reread each time they are accessed. */
  private static boolean s_reread = SystemProperty.environment.value()
    == SystemProperty.Environment.Value.Development;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------------- get ----------------------------------

  /**
   * Get the templated resource represented by the given name.
   *
   * @param       inName      the name of the resource, relative to the
   *                          classpath
   * @param       inPrefix    the prefix into the configuration for template
   *                          values
   *
   * @return      the templated resource for this name
   *
   */
  public static @Nonnull Resource get(@Nonnull String inName,
                                      @Nonnull String inPrefix)
  {
    String name = inName;
    if(!name.startsWith("/"))
      name = "/" + name;

    URL url =
      Resource.class.getResource(net.ixitxachitls.util.Files.concatenate
                                 ("/", name));

    return new TemplateResource(name, url, inPrefix);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //--------------------------------- read ---------------------------------

  /**
   * Get the whole contents of the resource as a string. Line termination is
   * normalized to \n.
   *
   * @return      the contents as a string
   *
   */
  @Override
  public @Nonnull String read()
  {
    if(m_content == null)
    {
      try
      {
        m_content = Files.toString(asFile(), Charsets.UTF_8);

        // do the replacement for all variables
        m_content = Strings.replaceTemplates(m_content, m_prefix);
      }
      catch(java.io.IOException e)
      {
        Log.warning("cannot read resource '" + this + ": " + e);
        m_content = "";
      }
    }

    return m_content;
  }

  //........................................................................
  //-------------------------------- write ---------------------------------

  /**
   * Write the resources to the given output.
   *
   * @param       inOutput the output stream to write to
   *
   * @return      true if writing ok, false if not
   *
   */
  @Override
  public boolean write(@Nonnull OutputStream inOutput)
  {
    read();

    try
    {
      inOutput.write(m_content.getBytes());
    }
    catch(java.io.IOException e)
    {
      return false;
    }

    if(s_reread)
      m_content = null;

    return true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- write ----------------------------------------------------------

    /** The write Test. */
    @org.junit.Test
    public void write()
    {
      Resource resource =
        new TemplateResource("/css/jdma.css",
                             TemplateResource.class
                             .getResource("/css/jdma.css"),
                             "test/test/template");

      ByteArrayOutputStream output = new ByteArrayOutputStream();

      System.setProperty("test/test/template.color_Monster", "single word");
      m_logger.banClass(net.ixitxachitls.util.Strings.class);
      assertTrue("writing", resource.write(output));
      assertPattern("content",
                    ".*A.Monster         \\{ color: single word \\}.*",
                    output.toString());

      // invalid resource
      resource = new FileResource("guru", null);
      assertFalse("writing", resource.write(output));

      m_logger.addExpected("WARNING: cannot obtain input stream for guru");
    }

    //......................................................................
  }

  //........................................................................
}
