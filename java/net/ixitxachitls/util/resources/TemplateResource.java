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

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;
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
  private static boolean s_reread = Config.get("web/template.reread", true);

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

  //-------------------------------- write ---------------------------------

  /**
   * Write the resources to the given output.
   *
   * @param       inOutput the output stream to write to
   *
   * @return      true if writing ok, false if not
   *
   */
  public boolean write(@Nonnull OutputStream inOutput)
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
        return false;
      }
    }

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
        new TemplateResource("/config/test/test.config",
                             TemplateResource.class
                             .getResource("/config/test/test.config"),
                             "test/test/template");

      ByteArrayOutputStream output = new ByteArrayOutputStream();

      System.setProperty("test/test/template.simple", "single word");
      assertTrue("writing", resource.write(output));
      assertPattern("content", ".*test.templateresource=single word.*",
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
