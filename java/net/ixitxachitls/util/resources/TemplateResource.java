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

package net.ixitxachitls.util.resources;

import java.io.File;
import java.io.OutputStream;
import java.net.URL;

import com.google.appengine.api.utils.SystemProperty;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.io.Files;

import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

/**
 * A resource for the file system with special variable translation.
 *
 * @file          TemplateResource.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

public class TemplateResource extends FileResource
{
  /**
   * Create the template file resource.
   *
   * @param    inName   the name of the file this resource represents
   * @param    inURL    the url to the resource
   * @param    inPrefix the prefix into the config for template values
   *
   */
  TemplateResource(String inName, Optional<URL> inURL, String inPrefix)
  {
    super(inName, inURL);

    m_prefix = inPrefix;
  }

  /** The prefix into the config for template values. */
  private String m_prefix;

  /** The contents of the file (caching). */
  private Optional<String> m_content = Optional.absent();

  /** Flag if files should be reread each time they are accessed. */
  private static boolean s_reread = SystemProperty.environment.value()
    == SystemProperty.Environment.Value.Development;

  /**
   * Get the templated resource represented by the given name.
   *
   * @param       inName      the name of the resource, relative to the
   *                          classpath
   * @param       inPrefix    the prefix into the configuration for template
   *                          values
   *
   * @return      the templated resource for this name
   */
  public static Resource get(String inName, String inPrefix)
  {
    String name = inName;
    if(name.charAt(0) != '/')
      name = "/" + name;

    URL url =
      Resource.class.getResource(net.ixitxachitls.util.Files.concatenate
                                 ("/", name));

    return new TemplateResource(name, Optional.fromNullable(url), inPrefix);
  }

  @Override
  public String read()
  {
    if(!m_content.isPresent())
    {
      try
      {
        Optional<File> file = asFile();
        String content = Files.toString(file.get(), Charsets.UTF_8);

        // do the replacement for all variables
        m_content = Optional.of(Strings.replaceTemplates(content, m_prefix));
      }
      catch(java.io.IOException e)
      {
        Log.warning("cannot read resource '" + this + ": " + e);
        m_content = Optional.of("");
      }
    }

    return m_content.get();
  }

  /**
   * Write the resources to the given output.
   *
   * @param       inOutput the output stream to write to
   *
   * @return      true if writing ok, false if not
   */
  @Override
  public boolean write(OutputStream inOutput)
  {
    read();

    try
    {
      if(m_content.isPresent())
        inOutput.write(m_content.get().getBytes(Charsets.UTF_8));
    }
    catch(java.io.IOException e)
    {
      return false;
    }

    if(s_reread)
      m_content = Optional.absent();

    return true;
  }

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }
}
