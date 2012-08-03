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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.SoyModule;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.shared.restricted.SoyFunction;
import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.tofu.internal.BaseTofu;
import com.google.template.soy.tofu.restricted.SoyTofuFunction;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;
import net.ixitxachitls.util.resources.Resource;


//..........................................................................

//------------------------------------------------------------------- header

/**
 * Wrapper for rendering a soy template.
 *
 *
 * @file          SoyTemplate.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
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

  /** A plugin function to return an entry for a given key. */
  public static class EntryFunction implements SoyTofuFunction
  {
    @Override
    public String getName()
    {
      return "entry";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public SoyData computeForTofu(@Nonnull List<SoyData> inArgs)
    {
      AbstractEntry.EntryKey key =
        AbstractEntry.EntryKey.fromString(inArgs.get(0).toString());

      if(key == null)
      {
        Log.warning("invalid key for entry soy function for " + inArgs.get(0));
        return StringData.forValue("invalid key '" + inArgs.get(0) + "'");
      }

      AbstractEntry entry = DMADataFactory.get().getEntry(key);

      if(entry == null)
      {
        Log.warning("unknown entry for entry soy function for "
                    + inArgs.get(0));
        return StringData.forValue("unknown entry '" + inArgs.get(0) + "'");
      }

      // TODO: will fail if a value is actually rendered
      return new SoyEntry(entry, null);
    }
  }

  /** A plugin function to convert the argument into an integer. */
  public static class IntegerFunction implements SoyTofuFunction
  {
    @Override
    public @Nonnull String getName()
    {
      return "integer";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public @Nonnull SoyData computeForTofu(@Nonnull List<SoyData> inArgs)
    {
      return IntegerData.forValue(Integer.valueOf(inArgs.get(0).toString()));
    }
  }

  /** A plugin function to format numbers or printing. */
  public static class FormatNumberFunction implements SoyTofuFunction
  {
    @Override
    public @Nonnull String getName()
    {
      return "formatNumber";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public @Nonnull SoyData computeForTofu(@Nonnull List<SoyData> inArgs)
    {
      if(inArgs.get(0) instanceof IntegerData)
        return StringData.forValue
          (NumberFormat.getIntegerInstance()
           .format(((IntegerData)inArgs.get(0)).getValue()));

      return inArgs.get(0);
    }
  }

  /** The Guice module with our own plugins. */
  public static class DMAModule extends AbstractModule
  {
    @Override
    public void configure()
    {
      Multibinder<SoyFunction> soyFunctionsSetBinder =
        Multibinder.newSetBinder(binder(), SoyFunction.class);

      soyFunctionsSetBinder.addBinding().to(EntryFunction.class);
      soyFunctionsSetBinder.addBinding().to(IntegerFunction.class);
      soyFunctionsSetBinder.addBinding().to(FormatNumberFunction.class);
    }
  }

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

  /** The injector with our own plugins. */
  private Injector m_injector = createInjector();

  //........................................................................

  //-------------------------------------------------------------- accessors

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

  //----------------------------- removeScope ------------------------------

  /**
   * Remove the current scope from the tofu call stack. This is used to allow
   * nested rendering (i.e. render a new template for inclusing in the one
   * currently rendered).
   *
   * @return      the scope removed, if any
   *
   */
  private @Nullable Map<Key<?>, Object> removeScope()
  {
    BaseTofu baseTofu = (BaseTofu)m_compiled;
    Map<Key<?>, Object> scope = baseTofu.apiCallScope.scopedValuesTl.get();
    if(scope != null)
      baseTofu.apiCallScope.scopedValuesTl.remove();

    return scope;
  }

  //........................................................................
  //------------------------------- addScope -------------------------------

  /**
   * Add the given scope back to the tofu call stack.
   *
   * @param     inScope the scope to add back, if any
   *
   */
  private void addScope(@Nullable Map<Key<?>, Object> inScope)
  {
    if(inScope != null)
      ((BaseTofu)m_compiled).apiCallScope.scopedValuesTl.set(inScope);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------ recompile -------------------------------

  /**
   * Force recompilation when rendering next.
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
   */
  public void compile()
  {
    if(m_compiled != null)
      return;

    Log.important("compiling soy templates");

    // Bundle the Soy files for your project into a SoyFileSet.
    SoyFileSet.Builder files = m_injector.getInstance(SoyFileSet.Builder.class);
    for(String file : m_files)
    {
      String name;
      if(file.contains("/"))
        name = file + ".soy";
      else
        name = "soy/" + file + ".soy";

      File pureFile = new File(name);
      if(pureFile.canRead())
        files.add(pureFile);
      else
        files.add(Resource.get(name).asFile());
    }

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
  public static @Nonnull Map<String, Object> map(@Nonnull Object ... inData)
  {
    assert inData.length % 2 == 0 : "invalid number of arguments";

    Map<String, Object> map = new HashMap<String, Object>();
    for(int i = 0; i < inData.length; i += 2)
      map.put(inData[i].toString(), inData[i + 1]);

    return map;
  }

  //........................................................................
  //---------------------------- createInjector ----------------------------

  /**
   * Create an injector for compiling templates that includes our own
   * plugins.
   *
   * @return   the injector to use for creating compiled templates
   *
   */
  public Injector createInjector()
  {
    List<Module> modules = new ArrayList<Module>();
    modules.add(new SoyModule());
    modules.add(new DMAModule());

    return Guice.createInjector(modules);
  }

  //........................................................................


  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- map ------------------------------------------------------------

    /** The map Test. */
    @org.junit.Test
    public void map()
    {
      assertEquals("empty", "{}", SoyTemplate.map().toString());
      assertEquals("simple", "{b=2, a=1}",
                   SoyTemplate.map("a", "1", "b", "2").toString());
      assertEquals("nested", "{b={n1=2, n2=3}, a=1}",
                   SoyTemplate.map("a", "1", "b",
                                   SoyTemplate.map("n1", "2", "n2", "3"))
                   .toString());
    }

    //......................................................................
    //----- render ---------------------------------------------------------

    /** The render Test. */
    @org.junit.Test
    public void render()
    {
      SoyTemplate renderer = new SoyTemplate("lib/test/soy/test");

      assertEquals("render",
                   "first: first data second: second data "
                   + "third: first injected fourth: second injected "
                   + "fifth: jDMA",
                   renderer.render
                   ("dma.commands.test",
                    SoyTemplate.map("first", "first data",
                                    "second", "second data"),
                    SoyTemplate.map("first", "first injected",
                                    "second", "second injected"),
                    null));
    }
  }

  //........................................................................
}
