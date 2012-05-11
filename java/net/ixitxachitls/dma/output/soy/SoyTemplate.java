/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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

package net.ixitxachitls.dma.output.soy;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.appengine.api.utils.SystemProperty;
import com.google.inject.Key;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.tofu.internal.BaseTofu;

import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * Wrapper for rendering a soy template.
 *
 *
 * @file          SoyTemplate.java
 *
 * @author        Peter Balsiger
 *
 */

//..........................................................................

//__________________________________________________________________________

public class SoyTemplate
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- SoyTemplate ------------------------------

  /**
   * Create the template.
   *
   * @param       inFiles the name of the template files to compile (without
   *                      path and .soy extensions)
   *
   */
  public SoyTemplate(@Nonnull String ... inFiles)
  {
    m_files.addAll(Arrays.asList(inFiles));

    s_templates.add(this);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The soy files for the template. */
  private @Nonnull List<String> m_files = new ArrayList<String>();

  /** The compile template file set. */
  private @Nullable SoyTofu m_compiled = null;

  /** The project name. */
  public static final String PROJECT = Config.get("project.name", "jDMA");

  /** The project name. */
  public static final String PROJECT_URL =
    Config.get("project.url", "http://www.ixitxachitls.net");

  /** The version number (or monster in this case ;-)). */
  public static final String VERSION =
    Config.get("project.version", "Allip");

  /** A flag if templates should be recompiled. */
  private static List<SoyTemplate> s_templates = new ArrayList<SoyTemplate>();

  //........................................................................

  //-------------------------------------------------------------- accessors

  public @Nonnull String nestedRender(@Nonnull String inName,
                                      @Nonnull SoyMapData inData)
  {
    Map<Key<?>, Object> scope = removeScope();
    try
    {
      return render(inName, inData, null, null);
    }
    finally
    {
      addScope(scope);
    }
  }

  //-------------------------------- render --------------------------------

  /**
   * Render the template named.
   *
   * @param       inName      the name of the template to render.
   * @param       inData      the data for the template.
   * @param       inInjected  the injected data for the template.
   * @param       inDelegates the delegates used for rendering, if any
   *
   * @return      the rendered template as a string
   *
   */
  public @Nonnull String render(@Nonnull String inName,
                                @Nullable Map<String, Object> inData,
                                @Nullable Map<String, Object> inInjected,
                                @Nullable Set<String> inDelegates)
  {
    SoyMapData data = null;
    if(inData != null)
      data = new SoyMapData(inData);

    SoyMapData injected = null;
    if(inInjected != null)
      injected = new SoyMapData(inInjected);

    return render(inName, data, injected, inDelegates);
  }

  //........................................................................
  //-------------------------------- render --------------------------------

  /**
   * Render the template named.
   *
   * @param       inName      the name of the template to render.
   * @param       inData      the data for the template.
   * @param       inInjected  the injected data for the template.
   * @param       inDelegates the delegates used for rendering, if any
   *
   * @return      the rendered template as a string
   *
   */
  public @Nonnull String render(@Nonnull String inName,
                                @Nullable SoyMapData inData,
                                @Nullable SoyMapData inInjected,
                                @Nullable Set<String> inDelegates)
  {
    if(m_compiled == null)
      compile();

    Map<Key<?>, Object> scope = removeScope();
    String rendered = "";
    try
    {
      rendered = m_compiled.newRenderer(inName)
        .setData(inData)
        .setIjData(inInjected)
        .setActiveDelegatePackageNames(inDelegates)
        .render();
    }
    finally
    {
      if(scope != null)
        addScope(scope);
    }

    return rendered;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  private @Nonnull Map<Key<?>, Object> removeScope()
  {
    BaseTofu baseTofu = (BaseTofu)m_compiled;
    Map<Key<?>, Object> scope = baseTofu.apiCallScope.scopedValuesTl.get();
    if(scope != null)
      baseTofu.apiCallScope.scopedValuesTl.remove();

    return scope;
  }

  private void addScope(@Nonnull Map<Key<?>, Object> inScope)
  {
    if(inScope != null)
      ((BaseTofu)m_compiled).apiCallScope.scopedValuesTl.set(inScope);
  }


  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------ recompile -------------------------------

  /**
   * Force recompilation of when rendering next.
   *
   */
  public void recompile()
  {
    m_compiled = null;
  }

  //........................................................................

  //------------------------------- compile --------------------------------

  /**
   * Compile the templates for rendering.
   *
   * @param
   *
   * @return
   *
   */
  public void compile()
  {
    Log.important("compiling soy templates");

    // Bundle the Soy files for your project into a SoyFileSet.
    SoyFileSet.Builder files = new SoyFileSet.Builder();
    for(String file : m_files)
      if(file.contains("/"))
        files.add(new File(file + ".soy"));
      else
        files.add(new File("soy/" + file + ".soy"));

    files.setCompileTimeGlobals(map("dma.project", PROJECT,
                                    "dma.url", PROJECT_URL,
                                    "dma.version", VERSION));

    // Compile the template into a SoyTofu object.
    m_compiled = files.build().compileToTofu();
  }

  //........................................................................
  //--------------------------------- map ----------------------------------

  /**
   * Convert the given data into a map, using odd params as keys and even as
   * values.
   *
   * @param    inData the data to convert to a map
   *
   * @return   the converted map
   *
   */
  public @Nonnull Map<String, Object> map(Object ... inData)
  {
    assert inData.length % 2 == 0 : "invalid number of arguments";

    Map<String, Object> map = new HashMap<String, Object>();
    for(int i = 0; i < inData.length; i += 2)
      map.put(inData[i].toString(), inData[i + 1]);

    return map;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
