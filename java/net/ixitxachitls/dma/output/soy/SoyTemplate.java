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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.SoyModule;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.shared.restricted.SoyFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.shared.restricted.SoyPrintDirective;
import com.google.template.soy.tofu.SoyTofu;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;
import net.ixitxachitls.util.resources.Resource;

/**
 * Wrapper for rendering a soy template.
 *
 * @file          SoyTemplate.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

public class SoyTemplate
{
  /**
   * Create the template.
   *
   * @param       inFiles the name of the template files to compile (without
   *                      path and .soy extensions)
   */
  public SoyTemplate(String ... inFiles)
  {
    m_injector = createInjector();
    m_files.addAll(Arrays.asList(inFiles));
  }

  /** A plugin function to return an entry for a given key. */
  public static class EntryFunction implements SoyJavaFunction
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
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      Optional<EntryKey> key = EntryKey.fromString(inArgs.get(0).toString());

      if(!key.isPresent())
      {
        Log.warning("invalid key for entry soy function for " + inArgs.get(0));
        return StringData.forValue("invalid key '" + inArgs.get(0) + "'");
      }

      Optional<AbstractEntry> entry = DMADataFactory.get().getEntry(key.get());
      if(!entry.isPresent())
      {
        Log.warning("unknown entry for entry soy function for "
                        + inArgs.get(0));
        return StringData.forValue("unknown entry '" + inArgs.get(0) + "'");
      }

      return new SoyValue(key.toString(), entry.get());
    }
  }

  /** A plugin function to return an entry for a given key. */
  public static class CallFunction implements SoyJavaFunction
  {
    @Override
    public String getName()
    {
      return "call";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Override
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      if(!(inArgs.get(0) instanceof SoyValue)
          || !(inArgs.get(1) instanceof StringData))
        return new SoyUndefined("call");

      SoyValue object = (SoyValue)inArgs.get(0);
      String method = inArgs.get(1).stringValue();

      Object []arguments = new Object[inArgs.size() - 2];
      for (int i = 0; i < arguments.length; i++)
        if((inArgs.get(i + 2) instanceof SoyValue))
          arguments[i] = ((SoyValue)inArgs.get(i + 2)).getValue();
        else if ((inArgs.get(i + 2)) instanceof IntegerData)
          arguments[i] = inArgs.get(i + 2).integerValue();
        else
          arguments[i] = inArgs.get(i + 2).stringValue();

      return object.call(method, arguments);
    }
  }

  /** A plugin function to convert the argument into an integer. */
  public static class IntegerFunction implements SoyJavaFunction
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
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      return IntegerData.forValue(Integer.valueOf(inArgs.get(0).toString()));
    }
  }

  /** A plugin function to convert the argument into an integer. */
  public static class DefFunction implements SoyJavaFunction
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
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      if(inArgs.get(0) == null
          || inArgs.get(0) instanceof SoyUndefined)
        return BooleanData.forValue(false);

      return BooleanData.forValue(true);
    }
  }

  /** A plugin function to convert the argument into an integer. */
  public static class ValFunction implements SoyJavaFunction
  {
    @Override
    public String getName()
    {
      return "val";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      if(inArgs.get(0) == null || !(inArgs.get(0) instanceof SoyValue))
        return BooleanData.forValue(false);

      return BooleanData.forValue(true);
    }
  }

  /** A plugin function to convert the argument into an integer. */
  public static class LengthFunction implements SoyJavaFunction
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
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      if(inArgs.get(0) instanceof SoyListData)
        return IntegerData.forValue(((SoyListData)inArgs.get(0)).length());

      if(inArgs.get(0) instanceof SoyUndefined)
        return IntegerData.forValue(0);

      Log.error("trying to compute length of " + inArgs.get(0).getClass()
                + ", returning 0 instead");
      return IntegerData.forValue(0);
    }
  }

  /** A plugin function to format numbers or printing. */
  public static class CamelFunction implements SoyJavaFunction
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
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      return StringData.forValue
        (Encodings.toWordUpperCase(inArgs.get(0).toString()));
    }
  }

  /** A plugin function to format strings lowercase. */
  public static class LowerFunction implements SoyJavaFunction
  {
    @Override
    public String getName()
    {
      return "lower";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      return StringData.forValue(inArgs.get(0).toString().toLowerCase());
    }
  }

    /** A plugin function to format strings lowercase. */
  public static class MatchesFunction implements SoyJavaFunction
  {
    @Override
    public String getName()
    {
      return "matches";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(2);
    }

    @Override
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      return BooleanData.forValue
          (inArgs.get(0).toString().matches(inArgs.get(1).toString()));
    }
  }

  /** A plugin function to format numbers or printing. */
  public static class CommandsFunction implements SoyJavaFunction
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
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      return UnsafeSanitizedContentOrdainer.ordainAsSafe
          (COMMAND_RENDERER.renderCommands(inArgs.get(0).toString()),
           SanitizedContent.ContentKind.HTML);
    }
  }

  /** A plugin function to format numbers or printing. */
  public static class FormatNumberFunction implements SoyJavaFunction
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
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      if(inArgs.get(0) instanceof IntegerData)
        return StringData.forValue
          (NumberFormat.getIntegerInstance()
           .format(((IntegerData)inArgs.get(0)).getValue()));

      return inArgs.get(0);
    }
  }

  /** A plugin function to format numbers or printing. */
  public static class BonusFunction implements SoyJavaFunction
  {
    @Override
    public String getName()
    {
      return "bonus";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      if(inArgs.get(0) instanceof IntegerData)
      {
        IntegerData data = (IntegerData)inArgs.get(0);
        if(data.getValue() >= 0)
          return StringData.forValue("+" + data.getValue());
      }

      if(inArgs.get(0) instanceof SoyValue
        && ((SoyValue)inArgs.get(0)).getValue() instanceof Optional)
      {
        Optional optional = (Optional)((SoyValue)inArgs.get(0)).getValue();
        if(optional.isPresent())
          if (optional.get() instanceof Integer)
            return StringData.forValue("+" + optional.get());
          else
            return StringData.forValue(optional.get().toString());
        else
          return StringData.forValue("+0");
      }


      return inArgs.get(0);
    }
  }

  /** A plugin function to format numbers or printing. */
  public static class AnnotateFunction implements SoyJavaFunction
  {
    @Override
    public String getName()
    {
      return "annotate";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1, 3);
    }

    @Override
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      if(inArgs.get(0) instanceof SoyMapData)
      {
        SoyMapData data = (SoyMapData)inArgs.get(0);
        return UnsafeSanitizedContentOrdainer.ordainAsSafe
            (COMMAND_RENDERER.renderSoy
                 ("dma.value.annotated",
                  Optional.of(map(
                      "value", data,
                      "link", inArgs.size() > 1 ? inArgs.get(1) : "",
                      "type", inArgs.size() > 2 ? inArgs.get(2) : ""))),
             SanitizedContent.ContentKind.HTML);
      }

      return inArgs.get(0);
    }
  }

  /** A plugin function to format values or printing. */
  public static class ValueFunction implements SoyJavaFunction
  {
    @Override
    public String getName()
    {
      return "value";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1, 2);
    }

    @Override
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      // Values are similar to SoyMapDate to access their values.
      if(inArgs.get(0) instanceof SoyValue)
      {
        SoyValue data = (SoyValue)inArgs.get(0);
        ImmutableMap.Builder<String, Object> map =
            ImmutableMap.<String, Object>builder()
                .put("value", data);
        for(int i = 1; i < inArgs.size(); i++)
        {
          String parts[] = inArgs.get(i).toString().split("\\s*=\\s*");
          if(parts.length == 2)
            map.put(parts[0], parts[1]);
          else
            map.put(inArgs.get(i).toString(), "true");
        }

        return UnsafeSanitizedContentOrdainer.ordainAsSafe
            (COMMAND_RENDERER.renderSoy
                 ("dma.value." + data.getValueName(),
                  Optional.of((Map<String, Object>)map.build())),
             SanitizedContent.ContentKind.HTML);
      }

      return inArgs.get(0);
    }
  }

  /** A plugin function to check if a value is a list. */
  public static class IsListFunction implements SoyJavaFunction
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
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      return BooleanData.forValue(inArgs.get(0) instanceof SoyListData);
    }
  }

  /** A plugin function to check if a value starts with a given string. */
  public static class StatsWithFunction implements SoyJavaFunction
  {
    @Override
    public String getName()
    {
      return "startsWith";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(2);
    }

    @Override
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      return BooleanData.forValue(inArgs.get(0).coerceToString().startsWith(
          inArgs.get(1).coerceToString()));
    }
  }

  /** A plugin function to check if a value is a list. */
  public static class EscapeFunction implements SoyJavaFunction
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
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      return StringData.forValue(inArgs.get(0).toString()
                                 .replace("+", "_")
                                 .replace(",", "_")
                                 .replace("'", "_")
                                 .replace(" ",  "-")
                                 .replace("(", "_")
                                 .replace(")", "_"));
    }
  }

  /** A plugin function to check if a value is a list. */
  public static class JsEscapeFunction implements SoyJavaFunction
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
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      return StringData.forValue(inArgs.get(0).toString()
                                     .replace("'", "\\'")
                                     .replace("\"",  "\\\""));
    }
  }

  /** A plugin function to convert a string to html. */
  public static class HtmlFunction implements SoyJavaFunction
  {
    @Override
    public String getName()
    {
      return "html";
    }

    @Override
    public Set<Integer> getValidArgsSizes()
    {
      return ImmutableSet.of(1);
    }

    @Override
    public com.google.template.soy.data.SoyValue computeForJava(
        List<com.google.template.soy.data.SoyValue> inArgs)
    {
      return UnsafeSanitizedContentOrdainer.ordainAsSafe(
          inArgs.get(0).toString(),
           SanitizedContent.ContentKind.HTML);
    }
  }

  /** A directive to nicely format a number. */
  public static class NumberDirective implements SoyJavaPrintDirective
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
    public com.google.template.soy.data.SoyValue applyForJava(
        com.google.template.soy.data.SoyValue inValue,
        List<com.google.template.soy.data.SoyValue> inList)
    {
      return StringData.forValue
        (NumberFormat.getIntegerInstance(new Locale("de", "ch"))
         .format(Integer.valueOf(inValue.toString())));
    }
  }

  /** A directive to parse and render comands embedded in a string. */
  public static class CommandsDirective implements SoyJavaPrintDirective
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
    public com.google.template.soy.data.SoyValue applyForJava(
        com.google.template.soy.data.SoyValue inValue,
        List<com.google.template.soy.data.SoyValue> inList)
    {
      return StringData.forValue
          (COMMAND_RENDERER.renderCommands(inValue.toString()));
    }
  }

  /** A directive to parse and render comands embedded in a string. */
  public static class FirstLineDirective implements SoyJavaPrintDirective
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
    public com.google.template.soy.data.SoyValue applyForJava(
        com.google.template.soy.data.SoyValue inValue,
        List<com.google.template.soy.data.SoyValue> inList)
    {
      return StringData.forValue(firstLine(inValue.toString()));
    }
  }

  /** A direactive to format a string as a css id. */
  public static class CSSDirective implements SoyJavaPrintDirective
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
    public com.google.template.soy.data.SoyValue applyForJava(
        com.google.template.soy.data.SoyValue inValue,
        List<com.google.template.soy.data.SoyValue> inList)
    {
      return StringData.forValue(inValue.toString().replace(" ", "-"));
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
      soyFunctionsSetBinder.addBinding().to(CallFunction.class);
      soyFunctionsSetBinder.addBinding().to(IntegerFunction.class);
      soyFunctionsSetBinder.addBinding().to(LengthFunction.class);
      soyFunctionsSetBinder.addBinding().to(DefFunction.class);
      soyFunctionsSetBinder.addBinding().to(ValFunction.class);
      soyFunctionsSetBinder.addBinding().to(EscapeFunction.class);
      soyFunctionsSetBinder.addBinding().to(JsEscapeFunction.class);
      soyFunctionsSetBinder.addBinding().to(HtmlFunction.class);
      soyFunctionsSetBinder.addBinding().to(FormatNumberFunction.class);
      soyFunctionsSetBinder.addBinding().to(BonusFunction.class);
      soyFunctionsSetBinder.addBinding().to(AnnotateFunction.class);
      soyFunctionsSetBinder.addBinding().to(ValueFunction.class);
      soyFunctionsSetBinder.addBinding().to(IsListFunction.class);
      soyFunctionsSetBinder.addBinding().to(StatsWithFunction.class);
      soyFunctionsSetBinder.addBinding().to(CamelFunction.class);
      soyFunctionsSetBinder.addBinding().to(LowerFunction.class);
      soyFunctionsSetBinder.addBinding().to(MatchesFunction.class);
      soyFunctionsSetBinder.addBinding().to(CommandsFunction.class);

      Multibinder<SoyPrintDirective> soyDirectivesSetBinder =
        Multibinder.newSetBinder(binder(), SoyPrintDirective.class);

      soyDirectivesSetBinder.addBinding().to(NumberDirective.class);
      soyDirectivesSetBinder.addBinding().to(CSSDirective.class);
      soyDirectivesSetBinder.addBinding().to(CommandsDirective.class);
      soyDirectivesSetBinder.addBinding().to(FirstLineDirective.class);
    }
  }

  /** The soy files for the template. */
  private List<String> m_files = new ArrayList<>();

  /** The compiled template file set. */
  private Optional<SoyTofu> m_compiled = Optional.absent();

  /** The project name. */
  public static final String PROJECT = Config.get("project.name", "jDMA");

  /** The project name. */
  public static final String PROJECT_URL =
    Config.get("project.url", "http://www.ixitxachitls.net");

  /** The version number (or monster in this case ;-)). */
  public static final String VERSION =
    Config.get("project.version", "Allip");

  /** The injector with our own plugins. */
  private Injector m_injector;

  /** The renderer for rendering commands. */
  public static final SoyRenderer COMMAND_RENDERER =
      new SoyRenderer("commands", "value");

  /** The renderer for rendering values. */
  public static final SoyRenderer VALUE_RENDERER = new SoyRenderer("value");

  /**
   * Render the template named.
   *
   * @param       inName      the name of the template to render.
   * @param       inData      the data for the template.
   * @param       inInjected  the injected data for the template.
   *
   * @return      the rendered template as a string
   */
  public String render(String inName,
                       Optional<Map<String, Object>> inData,
                       Optional<Map<String, Object>> inInjected)
  {
    Optional<SoyMapData> data;
    if(inData.isPresent())
      data = Optional.of(new SoyMapData(inData.get()));
    else
      data = Optional.absent();

    Optional<SoyMapData> injected;
    if(inInjected.isPresent())
      injected = Optional.of(new SoyMapData(inInjected.get()));
    else
      injected = Optional.absent();

    return renderSoy(inName, data, injected);
  }

  /**
   * Render the template named.
   *
   * @param       inName      the name of the template to render.
   * @param       inData      the data for the template.
   * @param       inInjected  the injected data for the template.
   *
   * @return      the rendered template as a string
   */
  public String renderSoy(String inName,
                          Optional<SoyMapData> inData,
                          Optional<SoyMapData> inInjected)
  {
    SoyTofu.Renderer renderer = createRenderer(inName, inData, inInjected);
    return renderer.render();
  }

  public SanitizedContent renderStrictSoy(String inName,
                                          Optional<SoyMapData> inData,
                                          Optional<SoyMapData> inInjected)
  {
    SoyTofu.Renderer renderer = createRenderer(inName, inData, inInjected);
    return renderer.renderStrict();
  }

  private SoyTofu.Renderer createRenderer(String inName,
                                          Optional<SoyMapData> inData,
                                          Optional<SoyMapData> inInjected)
  {
    compile();

    SoyTofu.Renderer renderer = m_compiled.get().newRenderer(inName);
    if(inData.isPresent())
      renderer.setData(inData.get());
    if(inInjected.isPresent())
      renderer.setIjData(inInjected.get());
    else
      renderer.setIjData(new SoyMapData());

    return renderer;
  }

  /**
   * Convert to a string for debugging.
   *
   * @return      a string representation
   */
  @Override
  public String toString()
  {
    return "files: " + m_files;
  }

  /**
   * Force recompilation when rendering next.
   */
  public void recompile()
  {
    m_compiled = Optional.absent();
  }

  /**
   * Compile the templates for rendering.
   *
   * @return this template for chaining
   */
  public SoyTemplate compile()
  {
    if(m_compiled.isPresent())
      return this;

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
      {
        Optional<File> fileResource = Resource.get(name).asFile();
        if(fileResource.isPresent())
          files.add(fileResource.get());
      }
    }

    files.setCompileTimeGlobals(map("dma.project", PROJECT,
                                    "dma.url", PROJECT_URL,
                                    "dma.version", VERSION));

    // Compile the template into a SoyTofu object.
    m_compiled = Optional.of(files.build().compileToTofu());

    return this;
  }

  /**
   * Convert the given data into a map, using odd params as keys and even as
   * values.
   *
   * @param    inData the data to convert to a map
   * @param    <T> the type of object values mapped
   *
   * @return   the converted map
   */
  public static <T> Map<String, T> map(Object ... inData)
  {
    assert inData.length % 2 == 0 : "invalid number of arguments";

    Map<String, T> map = new HashMap<>();
    for(int i = 0; i < inData.length; i += 2)
      map.put(inData[i].toString(), (T)inData[i + 1]);

    return map;
  }

  /**
   * Create an injector for compiling templates that includes our own
   * plugins.
   *
   * @return   the injector to use for creating compiled templates
   */
  public Injector createInjector()
  {
    List<Module> modules = new ArrayList<Module>();
    modules.add(new SoyModule());
    modules.add(new DMAModule());

    return Guice.createInjector(modules);
  }

  //----------------------------------------------------------------------------

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
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

    /** The render Test. */
    @org.junit.Test
    public void render()
    {
      SoyTemplate renderer = new SoyTemplate("test");

      assertEquals
          ("render",
           "first: first data second: second data "
               + "third: first injected fourth: second injected "
               + "fifth: jDMA",
           renderer.render
               ("test.commands.test",
                Optional.of(SoyTemplate.map("first", "first data",
                                            "second", "second data")),
                Optional.of(SoyTemplate.map("first", "first injected",
                                            "second", "second injected"))));
    }
  }
}
