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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
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
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.shared.restricted.SoyFunction;
import com.google.template.soy.shared.restricted.SoyPrintDirective;
import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.tofu.internal.BaseTofu;
import com.google.template.soy.tofu.restricted.SoyAbstractTofuFunction;
import com.google.template.soy.tofu.restricted.SoyAbstractTofuPrintDirective;
import com.google.template.soy.tofu.restricted.SoyTofuFunction;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Parameters;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.Strings;
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

@ParametersAreNonnullByDefault
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
  public SoyTemplate(String ... inFiles)
  {
    m_files.addAll(Arrays.asList(inFiles));
  }

  //........................................................................

  //----- EntryFunction ----------------------------------------------------

  /** A plugin function to return an entry for a given key. */
  public static class EntryFunction extends SoyAbstractTofuFunction
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
    public SoyData compute(List<SoyData> inArgs)
    {
      Optional<EntryKey> key = EntryKey.fromString(inArgs.get(0).toString());

      if(!key.isPresent())
      {
        Log.warning("invalid key for entry soy function for " + inArgs.get(0));
        return StringData.forValue("invalid key '" + inArgs.get(0) + "'");
      }

      AbstractEntry entry = DMADataFactory.get().getEntry(key.get());

      if(entry == null)
      {
        Log.warning("unknown entry for entry soy function for "
                    + inArgs.get(0));
        return StringData.forValue("unknown entry '" + inArgs.get(0) + "'");
      }

      return new SoyEntry(entry);
    }
  }

  //........................................................................
  //----- IntegerFunction --------------------------------------------------

  /** A plugin function to convert the argument into an integer. */
  public static class IntegerFunction implements SoyTofuFunction
  {
    @Override
    public String getName()
    {
      return "integer";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public SoyData computeForTofu(List<SoyData> inArgs)
    {
      return IntegerData.forValue(Integer.valueOf(inArgs.get(0).toString()));
    }
  }

  //........................................................................
  //----- DefFunction ---------------------------------------------------

  /** A plugin function to convert the argument into an integer. */
  public static class DefFunction implements SoyTofuFunction
  {
    @Override
    public String getName()
    {
      return "def";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public SoyData computeForTofu(List<SoyData> inArgs)
    {
      if(inArgs.get(0) == null
         || inArgs.get(0) instanceof SoyAbstract.Undefined)
        return BooleanData.forValue(false);

      return BooleanData.forValue(true);
    }
  }

  //........................................................................
  //----- LengthFunction ---------------------------------------------------

  /** A plugin function to convert the argument into an integer. */
  public static class LengthFunction implements SoyTofuFunction
  {
    @Override
    public String getName()
    {
      return "len";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public SoyData computeForTofu(List<SoyData> inArgs)
    {
      if(inArgs.get(0) instanceof SoyListData)
        return IntegerData.forValue(((SoyListData)inArgs.get(0)).length());

      if(inArgs.get(0) instanceof SoyAbstract.Undefined)
        return IntegerData.forValue(0);

      Log.error("trying to compute length of " + inArgs.get(0).getClass()
                + ", returning 0 instead");
      return IntegerData.forValue(0);
    }
  }

  //........................................................................
  //----- CamelFunction ----------------------------------------------------

  /** A plugin function to format numbers or printing. */
  public static class CamelFunction implements SoyTofuFunction
  {
    @Override
    public String getName()
    {
      return "camel";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public SoyData computeForTofu(List<SoyData> inArgs)
    {
      return StringData.forValue
        (Encodings.toWordUpperCase(inArgs.get(0).toString()));
    }
  }

  //........................................................................
  //----- CommandsFunction -------------------------------------------------

  /** A plugin function to format numbers or printing. */
  public static class CommandsFunction implements SoyTofuFunction
  {
    @Override
    public String getName()
    {
      return "commands";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public SoyData computeForTofu(List<SoyData> inArgs)
    {
      return StringData.forValue
        (COMMAND_RENDERER.renderCommands(inArgs.get(0).toString()));
    }
  }

  //........................................................................
  //----- ReferenceFunction ------------------------------------------------

  /** A plugin function to format numbers or printing. */
  public static class ReferenceFunction implements SoyTofuFunction
  {
    @Override
    public String getName()
    {
      return "reference";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(2);
    }

    @Override
    public SoyData computeForTofu(List<SoyData> inArgs)
    {
      Optional<EntryKey> key =
        EntryKey.fromString(inArgs.get(0).toString());
      if(!key.isPresent())
        return inArgs.get(0);

      BaseEntry entry = (BaseEntry)DMADataFactory.get().getEntry(key.get());

      if(entry == null)
        return inArgs.get(0);

      Parameters parameters = new Parameters();
      for(SoyData opt : (SoyListData)inArgs.get(1))
      {
        String []parts = Strings.getPatterns(opt.toString(), "^(\\w+)\\s(.*)$");
        if(parts.length != 2)
          Log.warning("invalid parameter " + opt + " ignored");
        else
          parameters.with(parts[0], new Name(parts[1]), Parameters.Type.UNIQUE);
      }

      return StringData.forValue(entry.getSummary(parameters));
    }
  }

  //........................................................................
  //----- FormatNumberFunction ---------------------------------------------

  /** A plugin function to format numbers or printing. */
  public static class FormatNumberFunction implements SoyTofuFunction
  {
    @Override
    public String getName()
    {
      return "formatNumber";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public SoyData computeForTofu(List<SoyData> inArgs)
    {
      if(inArgs.get(0) instanceof IntegerData)
        return StringData.forValue
          (NumberFormat.getIntegerInstance()
           .format(((IntegerData)inArgs.get(0)).getValue()));

      return inArgs.get(0);
    }
  }

  //........................................................................
  //----- IsListFunction ---------------------------------------------------

  /** A plugin function to check if a value is a list. */
  public static class IsListFunction implements SoyTofuFunction
  {
    @Override
    public String getName()
    {
      return "isList";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public SoyData computeForTofu(List<SoyData> inArgs)
    {
      return BooleanData.forValue(inArgs.get(0) instanceof SoyListData);
    }
  }

  //........................................................................

  /** A plugin function to check if a value is a list. */
  public static class EscapeFunction implements SoyTofuFunction
  {
    @Override
    public String getName()
    {
      return "escape";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public SoyData computeForTofu(List<SoyData> inArgs)
    {
      return StringData.forValue(inArgs.get(0).toString()
                                 .replace("+", "_")
                                 .replace(" ",  "_"));
    }
  }

  /** A plugin function to check if a value is a list. */
  public static class JsEscapeFunction implements SoyTofuFunction
  {
    @Override
    public String getName()
    {
      return "jsescape";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public SoyData computeForTofu(List<SoyData> inArgs)
    {
      return StringData.forValue(inArgs.get(0).toString()
                                 .replace("'", "\\'")
                                 .replace("\"",  "\\\""));
    }
  }

  //----- NumberDirective --------------------------------------------------

  /** A directive to nicely format a number. */
  public static class NumberDirective extends SoyAbstractTofuPrintDirective
  {
    @Override
    public String getName()
    {
      return "|number";
    }


    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(0);
    }


    @Override
    public boolean shouldCancelAutoescape()
    {
      return false;
    }


    @Override
    public SoyData applyForTofu(SoyData inValue, List<SoyData> inArgs)
    {
      return StringData.forValue
        (NumberFormat.getIntegerInstance(new Locale("de", "ch"))
         .format(Integer.valueOf(inValue.toString())));
    }

    @Override
    public SoyData apply(SoyData inValue, List<SoyData> inArgs)
    {
      return applyForTofu(inValue, inArgs);
    }
  }

  //........................................................................
  //----- PrintDirective ---------------------------------------------------

  /** A directive to nicely print a vlue. */
  public static class PrintDirective extends SoyAbstractTofuPrintDirective
  {
    @Override
    public String getName()
    {
      return "|print";
    }


    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(0);
    }


    @Override
    public boolean shouldCancelAutoescape()
    {
      return false;
    }


    @Override
    public SoyData apply(SoyData inValue, List<SoyData> inArgs)
    {
      if(inValue instanceof SoyValue)
        return StringData.forValue(((SoyValue)inValue).print());

      if(inValue == null)
        return StringData.forValue("(null)");

      return inValue;
    }
  }

  //........................................................................
  //----- RawDirective -----------------------------------------------------

  /** A directive to print a value raw. */
  public static class RawDirective extends SoyAbstractTofuPrintDirective
  {
    @Override
    public String getName()
    {
      return "|raw";
    }


    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(0);
    }


    @Override
    public boolean shouldCancelAutoescape()
    {
      return false;
    }


    @Override
    public SoyData apply(SoyData inValue, List<SoyData> inArgs)
    {
      if(inValue instanceof SoyValue)
        return StringData.forValue(((SoyValue)inValue).raw());

      return inValue;
    }
  }

  //........................................................................
  //----- CommandsDirective ------------------------------------------------

  /** A directive to parse and render comands embedded in a string. */
  public static class CommandsDirective extends SoyAbstractTofuPrintDirective
  {
    @Override
    public String getName()
    {
      return "|commands";
    }


    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(0);
    }


    @Override
    public boolean shouldCancelAutoescape()
    {
      return false;
    }


    @Override
    public SoyData apply(SoyData inValue, List<SoyData> inArgs)
    {
      if(inValue instanceof SoyValue)
        return StringData.forValue
          (COMMAND_RENDERER.renderCommands(((SoyValue)inValue).raw()));

      return inValue;
    }
  }

  //........................................................................
  //----- FirstLineDirective ------------------------------------------------

  /** A directive to parse and render comands embedded in a string. */
  public static class FirstLineDirective extends SoyAbstractTofuPrintDirective
  {
    @Override
    public String getName()
    {
      return "|firstline";
    }


    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(0);
    }


    @Override
    public boolean shouldCancelAutoescape()
    {
      return false;
    }

    /**
     * Get the first line of the given string.
     *
     * @param  inText the text to get from
     * @return the first line of the text
     */
    private String firstLine(String inText)
    {
      String line = Strings.getPattern(inText, "(.*?)\n");
      if(line == null)
        return inText;

      return line;
    }

    @Override
    public SoyData apply(SoyData inValue, List<SoyData> inArgs)
    {
      if(inValue instanceof SoyValue)
        return StringData.forValue
          (COMMAND_RENDERER.renderCommands
           (firstLine(((SoyValue)inValue).raw())));

      return StringData.forValue(firstLine(inValue.toString()));
    }
  }

  //........................................................................
  //----- CSSDirective -----------------------------------------------------

  /** A direactive to format a string as a css id. */
  public static class CSSDirective extends SoyAbstractTofuPrintDirective
  {
    @Override
    public String getName()
    {
      return "|css";
    }


    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(0);
    }


    @Override
    public boolean shouldCancelAutoescape()
    {
      return true;
    }

    @Override
    public SoyData apply(SoyData inValue, List<SoyData> inArgs)
    {
      return StringData.forValue(inValue.toString().replace(" ", "-"));
    }
  }

  //........................................................................

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
      soyFunctionsSetBinder.addBinding().to(LengthFunction.class);
      soyFunctionsSetBinder.addBinding().to(DefFunction.class);
      soyFunctionsSetBinder.addBinding().to(EscapeFunction.class);
      soyFunctionsSetBinder.addBinding().to(JsEscapeFunction.class);
      soyFunctionsSetBinder.addBinding().to(FormatNumberFunction.class);
      soyFunctionsSetBinder.addBinding().to(IsListFunction.class);
      soyFunctionsSetBinder.addBinding().to(CamelFunction.class);
      soyFunctionsSetBinder.addBinding().to(CommandsFunction.class);
      soyFunctionsSetBinder.addBinding().to(ReferenceFunction.class);

      Multibinder<SoyPrintDirective> soyDirectivesSetBinder =
        Multibinder.newSetBinder(binder(), SoyPrintDirective.class);

      soyDirectivesSetBinder.addBinding().to(NumberDirective.class);
      soyDirectivesSetBinder.addBinding().to(PrintDirective.class);
      soyDirectivesSetBinder.addBinding().to(CSSDirective.class);
      soyDirectivesSetBinder.addBinding().to(RawDirective.class);
      soyDirectivesSetBinder.addBinding().to(CommandsDirective.class);
      soyDirectivesSetBinder.addBinding().to(FirstLineDirective.class);
    }
  }

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The soy files for the template. */
  private List<String> m_files = new ArrayList<String>();

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

  /** The injector with our own plugins. */
  private Injector m_injector = createInjector();

  /** The command renderer for rendering values. */
  public static final SoyRenderer COMMAND_RENDERER =
    new SoyRenderer(new SoyTemplate("commands", "value", "page"));


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
  public String render(String inName,
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
  public String render(String inName,
                       @Nullable SoyMapData inData,
                       @Nullable SoyMapData inInjected,
                       @Nullable Set<String> inDelegates)
  {
    compile();

    Map<Key<?>, Object> scope = removeScope();
    try
    {
      return m_compiled.newRenderer(inName)
        .setData(inData)
        .setIjData(inInjected)
        .setActiveDelegatePackageNames(inDelegates)
        .render();
    }
    // catch(Exception e)
    // {
    //   System.out.println("Exception when rendering template:");
    //   e.printStackTrace(System.out);
    // }
    finally
    {
      if(scope != null)
        addScope(scope);
    }

    // return "(error)";
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert to a string for debugging.
   *
   * @return      a string representation
   *
   */
  @Override
  public String toString()
  {
    return "files: " + m_files;
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
   * Note, this requires to make
   * com.google.template.soy.shared.internal.GuiceSimpleScope.scopedValuesTl
   * and com.google.template.soy.tofu.internal.BaseTofu.apiCallScope to be made
   * public in the original soy source.
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

    Log.important("compiling soy templates: " + m_files);

    // Bundle the Soy files for your project into a SoyFileSet.
    SoyFileSet.Builder files = m_injector.getInstance(SoyFileSet.Builder.class);
    for(String file : m_files)
    {
      String name;
      if(file.startsWith("/"))
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
  public static Map<String, Object> map(Object ... inData)
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
                   ("test.commands.test",
                    SoyTemplate.map("first", "first data",
                                    "second", "second data"),
                    SoyTemplate.map("first", "first injected",
                                    "second", "second injected"),
                    null));
    }
  }

  //........................................................................
}