/******************************************************************************
 * Copyright (c) 2002,2003 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.util.configuration;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.util.Classes;
import net.ixitxachitls.util.Pair;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the static accessor to all the configurations available in the
 * system.
 *
 * <P>
 * <STRONG>Removal:</STRONG><BR />
 * This class does not provide a <CODE>remove()</CODE> method, because all get
 * and set methods use the data type that is gotten or set as part of the
 * key name (to prevent clashes). For removal, this would not work, because
 * a type makes not sense when removing and putting the internals into the
 * public interface would not be appropriate.
 *
 * <P>
 * <STRONG>Rewrite Mode:</STRONG><BR />
 * The whole configuration can be setup in rewrite mode (by using
 * <CODE>setRewriting(true)</CODE>). In this mode, all configuration values
 * will be rewritten, even if there are already values present. This means,
 * that the current configuration values will be lost and will be replaced by
 * the ones set in the code. The reasoning behind that is that when testing,
 * the values from the code should be used and not the ones of the
 * configuration. Otherwise, the test might work with the special values of the
 * configuration but not with the default ones currently stored in the code.
 *
 * <P>
 * In the case you don't want to overwrite the current configuration values
 * (mainly because you use the value when checking the values in a test, where
 * you don't want to repeat the default value), simply use a <CODE>null</CODE>
 * as the default value and the currently stored value will be preserved.
 *
 * @file          Config.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @example       <PRE>
 * // set a value
 * Config.set("resource:dir/file/key.subkey", "test");
 *
 * // get a value (and set if not yet set)
 * Config.get("resource:dir/file/key.subkey", "guru");
 * </PRE>
 *
 */

//..........................................................................

//__________________________________________________________________________

public final class Config
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Config --------------------------------

  /**
   * Private constructor to prevent instantiation.
   *
   * This is a static class after all.
   */
  private Config()
  {
    // not to be instantiated
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The list of all active configurations encountered so far. */
  private static final Hashtable <String, Configuration>s_configurations =
    new Hashtable<String, Configuration>();

  /** The name of the default configuration to use if none is given. */
  private static final String s_default = "resource";

  /** A flag if in rewrite mode (replace all stored values with the
   *  default ones). */
  private static boolean s_rewrite = false;

  /** The classloader to load class path specific values with. */
  private static ClassLoader s_classLoader =
    Config.class.getClassLoader();

  /** The postfix for String values. */
  private static final String s_string = null;

  /** The postfix for integer values. */
  private static final String s_int = "int";

  /** The postfix for long values. */
  private static final String s_long = "long";

  /** The postfix for float values. */
  private static final String s_float = "float";

  /** The postfix for double values. */
  private static final String s_double = "double";

  /** The postfix for character values. */
  private static final String s_char = "char";

  /** The postfix for boolean values. */
  private static final String s_boolean = "boolean";

  /** The postfix for array values. */
  private static final String s_list = "list";

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------------- get ----------------------------------

  /**
   * Get a property value from the configuration.
   *
   * This will read a simple String value from the configuration.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found (if null
   *                         is given, existing values will not be
   *                         overwritten, even in rewrite mode)
   *
   * @return      the requested string
   *
   */
  public static @Nonnull String get(@Nonnull String inKey,
                                    @Nullable String inDefault)
  {
    return get(inKey, inDefault, s_string);
  }

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * Get a property value from the configuration.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found
   *
   * @return      the requested string
   *
   */
  public static int get(@Nonnull String inKey, int inDefault)
  {
    return Integer.parseInt(get(inKey, "" + inDefault, s_int));
  }

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * Get a property value from the configuration.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found
   *
   * @return      the requested string
   *
   */
  public static long get(@Nonnull String inKey, long inDefault)
  {
    return Long.parseLong(get(inKey, "" + inDefault, s_long));
  }

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * Get a property value from the configuration.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found
   *
   * @return      the requested string
   *
   */
  public static float get(@Nonnull String inKey, float inDefault)
  {
    return Float.parseFloat(get(inKey, "" + inDefault, s_float));
  }

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * Get a property value from the configuration.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found
   *
   * @return      the requested string
   *
   */
  public static double get(@Nonnull String inKey, double inDefault)
  {
    return Double.parseDouble(get(inKey, "" + inDefault, s_double));
  }

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * Get a property value from the configuration.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found
   *
   * @return      the requested string
   *
   */
  public static char get(@Nonnull String inKey, char inDefault)
  {
    return get(inKey, "" + inDefault, s_char).charAt(0);
  }

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * Get a property value from the configuration.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found
   *
   * @return      the requested string
   *
   */
  public static boolean get(@Nonnull String inKey, boolean inDefault)
  {
    return Boolean.valueOf(get(inKey, "" + inDefault,
                               "boolean")).booleanValue();
  }

  //........................................................................

  //--------------------------------- get ----------------------------------

  /**
   * Get a property value from the configuration.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found
   *
   * @return      the requested string
   *
   */
  public static String []get(@Nonnull String inKey, @Nonnull String []inDefault)
  {
    return get(inKey, inDefault, s_string);
  }

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * Get a property value from the configuration.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found
   *
   * @return      the requested string
   *
   */
  public static @Nonnull int []get(@Nonnull String inKey,
                                   @Nullable int []inDefault)
  {
    String []defaults = null;

    if(inDefault != null)
    {
      defaults = new String[inDefault.length];

      for(int i = 0; i < inDefault.length; i++)
        defaults[i] = "" + inDefault[i];
    }

    String []strings = get(inKey, defaults, s_int);

    int []result = new int[strings.length];

    for(int i = 0; i < strings.length; i++)
      result[i] = Integer.parseInt(strings[i]);

    return result;
  }

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * Get a property value from the configuration.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found
   *
   * @return      the requested string
   *
   */
  public static @Nonnull boolean []get(@Nonnull String inKey,
                                       @Nullable boolean []inDefault)
  {
    String []defaults = null;

    if(inDefault != null)
    {
      defaults = new String[inDefault.length];

      for(int i = 0; i < inDefault.length; i++)
        defaults[i] = "" + inDefault[i];
    }

    String []strings = get(inKey, defaults, s_boolean);

    boolean []result = new boolean[strings.length];

    for(int i = 0; i < strings.length; i++)
      result[i] = Boolean.valueOf(strings[i]).booleanValue();

    return result;
  }

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * Get a property value from the configuration.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found
   *
   * @return      the requested string
   *
   */
  public static @Nonnull String [][]get(@Nonnull String inKey,
                                        @Nullable String [][]inDefault)
  {
    return get(inKey, inDefault, s_string);
  }

  //........................................................................

  //----------------------------- getPattern -------------------------------

  /**
   * Get a property value from the configuration and replace the pattern in
   * it with other configuration values. The patterns have the form {key},
   * where key is in itself a configuration value. Only String values may
   * be used that way.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found
   *
   * @return      the requested string, with patterns replaced
   *
   */
  public static @Nonnull String getPattern(@Nonnull String inKey,
                                           @Nullable String inDefault)
  {
    String pattern = get(inKey, inDefault, null);

    // now replace the pattern(s), if any
    Matcher matcher = Pattern.compile("\\{(.*?)\\}").matcher(pattern);

    StringBuffer result = new StringBuffer();
    while(matcher.find())
      matcher.appendReplacement(result,
                                Matcher
                                .quoteReplacement(Config.get(matcher.group(1),
                                                             "(unknown)")));

    matcher.appendTail(result);

    return result.toString();
  }

  //........................................................................

  //----------------------------- isRewriting ------------------------------

  /**
   * Get the flag describing if the configuration is in rewriting mode. This
   * means if the configuration will rewrite all values, even if they are
   * already there.
   *
   * @return      true if rewriting mode is turned on, false else
   *
   */
   public static boolean isRewriting()
   {
     return s_rewrite;
   }

  //........................................................................
  //-------------------------------- hasKey --------------------------------

  /**
   * Check if a key exists in the configuration.
   *
   * @param       inKey the key to check for
   *
   * @return      true if there is already a configuration value with the
   *              given key
   *
   */
  public static boolean hasKey(@Nonnull String inKey)
  {
    Pair<String, String> keys = splitName(inKey);

    Configuration configuration = getConfiguration(keys.first());

    if(configuration == null)
    {
      Log.warning("could not find configuration for '" + keys.first() + "'");

      return false;
    }

    return configuration.hasKey(keys.second());
  }

  //........................................................................

  //--------------------------------- get ----------------------------------

  /**
   * This is the internal get method.
   *
   * All public getters use this method to actually access a value.
   *
   * @param       inKey     the key of the value to get (not null)
   * @param       inDefault the default value if none is currently stored
   *                        (may be null to ignore rewrite)
   * @param       inPostfix the postfix to add to the key (may be null for no
   *                        postfix)
   *
   * @return      the requested configuration value or the default value if
   *              none was found (guaranteed non null)
   *
   */
  private static @Nonnull String get(@Nonnull String inKey,
                                     @Nullable String inDefault,
                                     @Nullable String inPostfix)
  {
    Pair<String, String> keys = splitName(inKey);

    Configuration configuration = getConfiguration(keys.first());

    if(configuration == null)
    {
      Log.warning("could not find configuration for '" + keys.first() + "'");

      return inDefault;
    }

    String key = keys.second();

    // add the postfix, if any
    if(inPostfix != null)
      key += "." + inPostfix;

    String result = null;
    if(inDefault == null && s_rewrite)
    {
      s_rewrite = false;
      result = configuration.get(key, "");
      s_rewrite = true;

      return result;
    }
    else
      if(inDefault == null)
        result = configuration.get(key, "");
      else
        result = configuration.get(key, inDefault);

    // make sure a non null value is returned
    assert result != null : "lookup should not have resulted in null";

    return result;
  }

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * This is the internal get method for arrays.
   *
   * All public getters use this method to actually access a value.
   *
   * @param       inKey     the key of the value to get (not null)
   * @param       inDefault the default value if none is currently stored
   *                        (may be null to ignore rewrite)
   * @param       inPostfix the postfix to add to the key (may be null for no
   *                        postfix)
   *
   * @return      the requested configuration values or the default values if
   *              none was found
   *
   */
  private static @Nonnull String []get(@Nonnull String inKey,
                                       @Nullable String []inDefault,
                                       @Nullable String inPostfix)
  {
    Pair<String, String> keys = splitName(inKey);

    Configuration configuration = getConfiguration(keys.first());

    if(configuration == null)
    {
      Log.warning("could not find configuration for '" + keys.first() + "'");

      return inDefault;
    }

    String key = keys.second();

    // add the postfix, if any
    if(inPostfix != null)
      key += "." + inPostfix;

    // if it does not yet exists, simply store and return it
    if((s_rewrite && inDefault != null)
       || !configuration.hasKey(key + "." + s_list + "." + 0))
    {
      // set the default value and return it
      for(int i = 0; i < inDefault.length; i++)
        configuration.set(key + "." + s_list + "." + i, inDefault[i]);

      return inDefault;
    }

    // get the real values from the configuration
    ArrayList<String> list = new ArrayList<String>();
    for(int i = 0; configuration.hasKey(key + "." + s_list + "." + i); i++)
      // it has to exist already, so the default value should never be used
      list.add(configuration.get(key + "." + s_list + "." + i, "guru"));

    return list.toArray(new String[list.size()]);
  }

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * This is the internal get method for arrays.
   *
   * All public getters use this method to actually access a value.
   *
   * @param       inKey     the key of the value to get (not null)
   * @param       inDefault the default value if none is currently stored
   *                        (may be null to ignore rewrite)
   * @param       inPostfix the postfix to add to the key (may be null for no
   *                        postfix)
   *
   * @return      the requested configuration values or the default values if
   *              none was found
   *
   */
  private static @Nonnull String [][]get(@Nonnull String inKey,
                                         @Nullable String [][]inDefault,
                                         @Nullable String inPostfix)
  {
    Pair<String, String> keys = splitName(inKey);

    Configuration configuration = getConfiguration(keys.first());

    if(configuration == null)
    {
      Log.warning("could not find configuration for '" + keys.first() + "'");

      return inDefault;
    }

    String key = keys.second();

    // add the postfix, if any
    if(inPostfix != null)
      key += "." + inPostfix;

    // if it does not yet exists, simply store and return it
    if((s_rewrite && inDefault != null)
       || !configuration.hasKey(key + "." + s_list + ".0.0"))
    {
      // set the default value and return it
      for(int i = 0; i < inDefault.length; i++)
        for(int j = 0; j < inDefault[i].length; j++)
          configuration.set(key + "." + s_list + "." + i + "." + j,
                            inDefault[i][j]);

      return inDefault;
    }

    // get the real values from the configuration
    ArrayList<String []> list = new ArrayList<String []>();
    for(int i = 0;
        configuration.hasKey(key + "." + s_list + "." + i + ".0");
        i++)
    {
      ArrayList<String> sub = new ArrayList<String>();

      for(int j = 0;
          configuration.hasKey(key + "." + s_list + "." + i + "." + j);
          j++)
        // it has to exist already, so the default value should never be used
        sub.add(configuration.get(key + "." + s_list + "." + i + "." + j,
                                  "guru"));

      list.add(sub.toArray(new String[sub.size()]));
    }

    return list.toArray(new String[list.size()][]);
  }

  //........................................................................

  //--------------------------- getConfiguration ---------------------------

  /**
   * Get a configuration for the given name.
   *
   * This method will determine the configuration type required for the name
   * given. It this type of configuration is already available, it will simply
   * be returned. Otherwise, this method will try to load the appropriate
   * class and add it to the pool of available configuration types.
   *
   * @param       inName the name of the configuration to get
   *
   * @return      the configuration obtained, or null if none was found
   *
   * @undefined   null is returned if the configuration cannot be found
   * @undefined   an assertion is raised when a null name is given
   *
   */
  private static @Nullable Configuration getConfiguration
    (@Nonnull String inName)
  {
    Configuration configuration = s_configurations.get(inName);

    // already loaded
    if(configuration != null)
      return configuration;

    // try to load the appropriate class
    String className =
      Classes.toClassName(inName, Classes.getPackage(Config.class));

    try
    {
      // we have to get an untyped class first and afterwards narrow it down
      // (otherwise we get an unchecked cast, which we don't want of course...)
      Class<?> untyped = Class.forName(className);

      // now narrow it down
      Class<? extends Configuration> load =
        untyped.asSubclass(Configuration.class);

      // finally load the class
      configuration = load.newInstance();

      Log.useful("loaded configuration class " + className + " for " + inName);
    }
    catch(ClassNotFoundException e)
    {
      Log.warning("could not load configuration '" + inName
                  + "' (tried loading class '" + className + "'");

      return null;
    }
    catch(InstantiationException e)
    {
      Log.warning("could not instantiate configuration '" + inName
                  + "' (tried instantiating class '" + className + "'");

      return null;
    }
    catch(IllegalAccessException e)
    {
      Log.warning("could not access configuration '" + inName
                  + "' (tried accessing constructor of class '" + className
                  + "'");

      return null;
    }
    catch(ClassCastException e)
    {
      Log.warning("configuration class " + className
                  + " not of correct type!");

      return null;
    }

    // store the value for later use
    s_configurations.put(inName, configuration);

    return configuration;
  }

  //........................................................................
  //------------------------ getCurrentClassLoader -------------------------

  /**
   * Get the class loader used.
   *
   * @return      the currently used class loader
   *
   */
  protected static ClassLoader getCurrentClassLoader()
  {
    return s_classLoader;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   * @return      true if value was set, false else
   *
   */
  public static boolean set(@Nonnull String inKey, @Nonnull String inValue)
  {
    return set(inKey, inValue, s_string);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   * @return      true if value was set, false else
   *
   */
  public static boolean set(@Nonnull String inKey, int inValue)
  {
    return set(inKey, "" + inValue, s_int);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   * @return      true if value was set, false else
   *
   */
  public static boolean set(@Nonnull String inKey, long inValue)
  {
    return set(inKey, "" + inValue, s_long);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   * @return      true if value was set, false else
   *
   */
  public static boolean set(@Nonnull String inKey, float inValue)
  {
    return set(inKey, "" + inValue, s_float);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   * @return      true if value was set, false else
   *
   */
  public static boolean set(@Nonnull String inKey, double inValue)
  {
    return set(inKey, "" + inValue, s_double);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   * @return      true if value was set, false else
   *
   */
  public static boolean set(@Nonnull String inKey, char inValue)
  {
    return set(inKey, "" + inValue, s_char);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   * @return      true if value was set, false else
   *
   */
  public static boolean set(@Nonnull String inKey, boolean inValue)
  {
    return set(inKey, "" + inValue, s_boolean);
  }

  //........................................................................

  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   * @return      true if value was set, false else
   *
   */
  public static boolean set(@Nonnull String inKey, @Nonnull String []inValue)
  {
    return set(inKey, inValue, s_string);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   * @return      true if value was set, false else
   *
   */
  public static boolean set(@Nonnull String inKey, @Nonnull int []inValue)
  {
    String []values = new String [inValue.length];

    for(int i = 0; i < inValue.length; i++)
      values[i] = "" + inValue[i];

    return set(inKey, values, s_int);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   * @return      true if value was set, false else
   *
   */
  public static boolean set(@Nonnull String inKey, @Nonnull boolean []inValue)
  {
    String []values = new String [inValue.length];

    for(int i = 0; i < inValue.length; i++)
      values[i] = "" + inValue[i];

    return set(inKey, values, s_boolean);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   * @return      true if value was set, false else
   *
   * @undefined   an assertion is raised if a given value is null
   *
   */
  public static boolean set(@Nonnull String inKey, @Nonnull String [][]inValue)
  {
    return set(inKey, inValue, s_string);
  }

  //........................................................................

  //------------------------- setCurrentClassLoader ------------------------

  /**
   * Set the class loader to use.
   *
   * @param       inClassLoader the new class loader to use
   *
   */
  public static void setCurrentClassLoader(@Nonnull ClassLoader inClassLoader)
  {
    s_classLoader = inClassLoader;
  }

  //........................................................................
  //----------------------------- setRewriting -----------------------------

  /**
   * Set the rewriting mode to use.
   *
   * @param       inMode the new mode to use for rewriting.
   *
   */
  public static void setRewriting(boolean inMode)
  {
    s_rewrite = inMode;
  }

  //........................................................................

  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey     the key of the value to obtain
   * @param       inValue   the new value to set to
   * @param       inPostfix the postfix to add to the key name for storage
   *                        (if any)
   *
   * @return      true if value was set, false else
   *
   */
  private static boolean set(@Nonnull String inKey, @Nonnull String inValue,
                             @Nullable String inPostfix)
  {
    Pair<String, String>keys = splitName(inKey);

    Configuration configuration = getConfiguration(keys.first());

    if(configuration == null)
    {
      Log.warning("could not find configuration for '" + keys.first() + "'");

      return false;
    }
    else
      if(inPostfix != null)
        return configuration.set(keys.second() + "." + inPostfix, inValue);
      else
        return configuration.set(keys.second(), inValue);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey     the key of the value to obtain
   * @param       inValue   the new value to set to
   * @param       inPostfix the postfix to add to the name, if any
   *
   * @return      true if value was set, false else
   *
   */
  public static boolean set(@Nonnull String inKey, @Nonnull String []inValue,
                            @Nullable String inPostfix)
  {
    Pair<String, String>keys = splitName(inKey);

    Configuration configuration = getConfiguration(keys.first());

    if(configuration == null)
    {
      Log.warning("could not find configuration for '" + keys.first() + "'");

      return false;
    }
    else
    {
      String key = keys.second();

      if(inPostfix != null)
        key += "." + inPostfix;

      for(int i = 0; i < inValue.length; i++)
      {
        if(inValue[i] == null)
          throw new IllegalArgumentException("no value given can be null");

        if(!configuration.set(key + "." + s_list + "." + i, inValue[i]))
          return false;
      }

      return true;
    }
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey     the key of the value to obtain
   * @param       inValue   the new value to set to
   * @param       inPostfix the postfix to add to the name, if any
   *
   * @return      true if value was set, false else
   *
   * @undefined   if key or value is null
   *
   */
  public static boolean set(@Nonnull String inKey, @Nonnull String [][]inValue,
                            @Nullable String inPostfix)
  {
    Pair<String, String>keys = splitName(inKey);

    Configuration configuration = getConfiguration(keys.first());

    if(configuration == null)
    {
      Log.warning("could not find configuration for '" + keys.first() + "'");

      return false;
    }
    else
    {
      String key = keys.second();

      if(inPostfix != null)
        key += "." + inPostfix;

      for(int i = 0; i < inValue.length; i++)
      {
        if(inValue[i] == null)
          throw new IllegalArgumentException("no value given can be null");

        for(int j = 0; j < inValue[i].length; j++)
        {
          if(inValue[i][j] == null)
            throw new IllegalArgumentException("no value given can be null");

          if(!configuration.set(key + "." + s_list + "." + i + "." + j,
                                inValue[i][j]))
            return false;
        }
      }

      return true;
    }
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------ splitName -------------------------------

  /**
   * Split a given configuration name into its key and configuration selection
   * part.
   *
   * @param       inName the name to split
   *
   * @return      a Pair with the first value being the configuration
   *              descriptor and the second value being the key
   *
   */
  protected static @Nonnull Pair<String, String> splitName
    (@Nonnull String inName)
  {
    int pos = inName.indexOf(':');
    if(pos >= 0)
      return new Pair<String, String>(inName.substring(0, pos),
                                      inName.substring(pos + 1));

    return new Pair<String, String>(s_default, inName);
  }

  //........................................................................

  //........................................................................

  //---------------------------------------------------------------- testing

  /** The class for testing. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** An integer value for testing. */
    private static final int INT = 123;

    /** An other integer value for testing. */
    private static final int OTHER_INT = 42;

    /** A long value for testing. */
    private static final long LONG = 123;

    /** An other long value for testing. */
    private static final long OTHER_LONG = 42;

    /** A float value for testing. */
    private static final float FLOAT = 1.23f;

    /** An other float value for testing. */
    private static final float OTHER_FLOAT = 4.2f;

    /** A double value for testing. */
    private static final double DOUBLE = 1.23;

    /** An other double value for testing. */
    private static final double OTHER_DOUBLE = 4.2;

    /** The error range. */
    private static final double RANGE = 0.0001;

    //----- split ----------------------------------------------------------

    /** Test for splitting values. */
    @org.junit.Test
    public void split()
    {
      // normal
      assertEquals("normal", "configuration",
                   splitName("configuration:key").first());
      assertEquals("normal", "key", splitName("configuration:key").second());

      // no configuration
      assertEquals("normal", "resource", splitName("key").first());
      assertEquals("normal", "key", splitName("key").second());

      // degenerate values
      assertEquals("normal", "", splitName(":key").first());
      assertEquals("normal", "", splitName("configuration:").second());

      // multiple colons
      assertEquals("normal", "config", splitName("config:key:other").first());
      assertEquals("normal", "key:other",
                   splitName("config:key:other").second());
    }

    //......................................................................
    //----- loading --------------------------------------------------------

    /** Test for loading the values. */
    @org.junit.Test
    public void loading()
    {
      m_logger.logClass(Config.class);

//       m_logger.addExpected("USEFUL: loaded configuration class " +
//                            "net.ixitxachitls.util.configuration." +
//                            "Config$Test$MockConfiguration for " +
//                            "Config$Test$MockConfiguration");

      assertNotNull("should have obtained configuration",
                    Config.getConfiguration("Config$Test$MockConfiguration"));

      m_logger.addExpected("WARNING: could not load configuration 'guru' "
                           + "(tried loading class "
                           + "'net.ixitxachitls.util.configuration.Guru'");

      assertNull("should not have obtained configuration",
                 Config.getConfiguration("guru"));

      m_logger.verify();
    }

    //.........................................................................
    //----- get ------------------------------------------------------------

    /** Test for getting values. */
    @org.junit.Test
    public void get()
    {
      assertEquals("get", "a test",
                   Config.get("Config$Test$MockConfiguration:test", "a test"));
      assertEquals("get", INT,
                   Config.get("Config$Test$MockConfiguration:test", INT));
      assertEquals("get", LONG,
                   Config.get("Config$Test$MockConfiguration:test", LONG));
      assertEquals("get", FLOAT,
                   Config.get("Config$Test$MockConfiguration:test", FLOAT),
                   RANGE);
      assertEquals("get", DOUBLE,
                   Config.get("Config$Test$MockConfiguration:test", DOUBLE),
                   RANGE);
      assertEquals("get", 'q',
                   Config.get("Config$Test$MockConfiguration:test", 'q'));
      assertEquals("get", true,
                   Config.get("Config$Test$MockConfiguration:test", true));

      String []check = { "first", "second", "third" };
      assertEquals("get", check[0],
                   Config.get("Config$Test$MockConfiguration:test", check)[0]);
      assertEquals("get", check[1],
                   Config.get("Config$Test$MockConfiguration:test", check)[1]);
      assertEquals("get", check[2],
                   Config.get("Config$Test$MockConfiguration:test", check)[2]);

      boolean []bCheck = { true, false, false };
      assertEquals("get", bCheck[0],
                   Config.get("Config$Test$MockConfiguration:test",
                              bCheck)[0]);
      assertEquals("get", bCheck[1],
                   Config.get("Config$Test$MockConfiguration:test",
                              bCheck)[1]);
      assertEquals("get", bCheck[2],
                   Config.get("Config$Test$MockConfiguration:test",
                              bCheck)[2]);

      String [][]mCheck =
        {
          { "first", "1", },
          { "second", "2", "b" },
          { "third" },
        };
      assertEquals("get", mCheck[0][0],
                   Config.get("Config$Test$MockConfiguration:test",
                              mCheck)[0][0]);
      assertEquals("get", mCheck[0][1],
                   Config.get("Config$Test$MockConfiguration:test",
                              mCheck)[0][1]);
      assertEquals("get", mCheck[1][0],
                   Config.get("Config$Test$MockConfiguration:test",
                              mCheck)[1][0]);
      assertEquals("get", mCheck[1][1],
                   Config.get("Config$Test$MockConfiguration:test",
                              mCheck)[1][1]);
      assertEquals("get", mCheck[2][0],
                   Config.get("Config$Test$MockConfiguration:test",
                              mCheck)[2][0]);

      MockConfiguration config =
        (MockConfiguration)
        Config.getConfiguration("Config$Test$MockConfiguration");

      config.clear();

      m_logger.verify();
    }

    //......................................................................
    //----- set ------------------------------------------------------------

    /** Test for setting values. */
    @org.junit.Test
    public void set()
    {
      // turn off rewrite mode
      Config.s_rewrite = false;

      Config.set("Config$Test$MockConfiguration:test", "guru");
      assertEquals("get", "guru",
                   Config.get("Config$Test$MockConfiguration:test", "a test"));

      Config.set("Config$Test$MockConfiguration:test", OTHER_INT);
      assertEquals("get", OTHER_INT,
                   Config.get("Config$Test$MockConfiguration:test", INT));
      Config.set("Config$Test$MockConfiguration:test", OTHER_LONG);
      assertEquals("get", OTHER_LONG,
                   Config.get("Config$Test$MockConfiguration:test", LONG));
      Config.set("Config$Test$MockConfiguration:test", OTHER_FLOAT);
      assertEquals("get", OTHER_FLOAT,
                   Config.get("Config$Test$MockConfiguration:test", FLOAT),
                              RANGE);
      Config.set("Config$Test$MockConfiguration:test", OTHER_DOUBLE);
      assertEquals("get", OTHER_DOUBLE,
                   Config.get("Config$Test$MockConfiguration:test", DOUBLE),
                              RANGE);
      Config.set("Config$Test$MockConfiguration:test", 'q');
      assertEquals("get", 'q',
                   Config.get("Config$Test$MockConfiguration:test", 'a'));
      Config.set("Config$Test$MockConfiguration:test", true);
      assertEquals("get", true,
                   Config.get("Config$Test$MockConfiguration:test", false));

      String []check  = { "first", "second", "third" };
      String []check2 = { "first2", };
      Config.set("Config$Test$MockConfiguration:test", check);
      assertEquals("get", check[0],
                   Config.get("Config$Test$MockConfiguration:test",
                              check2)[0]);
      assertEquals("get", check[1],
                   Config.get("Config$Test$MockConfiguration:test",
                              check2)[1]);
      assertEquals("get", check[2],
                   Config.get("Config$Test$MockConfiguration:test",
                              check2)[2]);

      final int []iCheck  = { 1, 2, 3 };
      final int []iCheck2 = { 42, };
      Config.set("Config$Test$MockConfiguration:test", iCheck);
      assertEquals("get", iCheck[0],
                   Config.get("Config$Test$MockConfiguration:test",
                              iCheck2)[0]);
      assertEquals("get", iCheck[1],
                   Config.get("Config$Test$MockConfiguration:test",
                              iCheck2)[1]);
      assertEquals("get", iCheck[2],
                   Config.get("Config$Test$MockConfiguration:test",
                              iCheck2)[2]);

      boolean []bCheck  = { true, false, false };
      boolean []bCheck2 = { true, };
      Config.set("Config$Test$MockConfiguration:test", bCheck);
      assertEquals("get", bCheck[0],
                   Config.get("Config$Test$MockConfiguration:test",
                              bCheck2)[0]);
      assertEquals("get", bCheck[1],
                   Config.get("Config$Test$MockConfiguration:test",
                              bCheck2)[1]);
      assertEquals("get", bCheck[2],
                   Config.get("Config$Test$MockConfiguration:test",
                              bCheck2)[2]);

      String [][]sCheck  = { { "a", "b", }, { "c", }, };
      String [][]sCheck2 = { { "guru", }, };
      assertTrue(Config.set("Config$Test$MockConfiguration:test", sCheck));
      assertEquals("get", sCheck[0][0],
                   Config.get("Config$Test$MockConfiguration:test",
                              sCheck2)[0][0]);
      assertEquals("get", sCheck[0][1],
                   Config.get("Config$Test$MockConfiguration:test",
                              sCheck2)[0][1]);
      assertEquals("get", sCheck[1][0],
                   Config.get("Config$Test$MockConfiguration:test",
                              sCheck2)[1][0]);

      // cross over
      Config.set("Config$Test$MockConfiguration:test2", "a test");

      assertEquals("get", INT,
                   Config.get("Config$Test$MockConfiguration:test2", INT));

      m_logger.verify();

      // turn REWRITE mode on again
      Config.s_rewrite = true;
    }

    //......................................................................
    //----- integration ----------------------------------------------------

    /** A simple integration test. */
    @org.junit.Test
    public void integration()
    {
      m_logger.logClass(Config.class);
      m_logger.logClass(Resource.class);
      m_logger.logClass(Resource.class);
      m_logger.logClass(Bundle.BundleHandler.class);
//       m_logger.logClass(User.PreferencesHandler.class);
//       m_logger.logClass(System.SystemPreferencesHandler.class);

      m_logger.addExpected("USEFUL: loaded configuration class "
                           + "net.ixitxachitls.util.configuration.Resource for "
                           + "resource");
      m_logger.addExpectedPattern("COMPLETE: loading resource "
                                  + ".*/test.config");

      assertEquals("get (resource)", "guru",
                   Config.get("resource:test/test/test.config", "guru"));

      m_logger.addExpected("USEFUL: loaded configuration class "
                           + "net.ixitxachitls.util.configuration.Bundle for "
                           + "bundle");
      m_logger.addExpected("COMPLETE: creating bundle test/test for "
                           + java.util.Locale.getDefault());

      m_logger.addExpected("WARNING: resource text for test.config not "
                           + "found in test/test for language "
                           + java.util.Locale.getDefault());
      m_logger.addExpected("WARNING: configuration value for "
                           + "test/test/test.config not found and cannot be "
                           + "stored");

      assertEquals("get (bundle)", "guru",
                   Config.get("bundle:test/test/test.config", "guru"));


// m_logger.addExpected("COMPLETE: creating user preferences test/test");

// assertEquals("get (user)", "guru",
// Config.get("user:test/test/test.config", "guru"));

// m_logger.addExpected("COMPLETE: creating system preferences test/test");

// assertEquals("get (system)", "guru",
// Config.get("system:test/test/test.config", "guru"));

      m_logger.verify();
    }

    //......................................................................
    //----- pattern --------------------------------------------------------

    /** Test the pattern. */
    @org.junit.Test
    public void pattern()
    {
      assertEquals("no pattern", "some pattern",
                   Config.getPattern("Config$Test$MockConfiguration:test.1",
                                     "some pattern"));

      assertEquals("pattern (no values)",
                   "just a (unknown) test with a (unknown) test",
                   Config.getPattern("Config$Test$MockConfiguration:test.2",
                                     "just a "
                                     + "{Config$Test$MockConfiguration:test.3}"
                                     + " test with a "
                                     + "{Config$Test$MockConfiguration:test.4}"
                                     + " test"));

      Config.set("Config$Test$MockConfiguration:test.3", "guru");
      Config.set("Config$Test$MockConfiguration:test.4", "whatever");

      assertEquals("pattern (values)",
                   "just a guru test with a whatever test",
                   Config.getPattern("Config$Test$MockConfiguration:test.2",
                                     "just a "
                                     + "{Config$Test$MockConfiguration:test.3}"
                                     + " test with a "
                                     + "{Config$Test$MockConfiguration:test.4}"
                                     + " test"));
    }

    //......................................................................
    //----- hasKey ---------------------------------------------------------

    /** Test if a key is present. */
    @org.junit.Test
    public void hasKey()
    {
      assertEquals("not yet", false,
                   Config.hasKey("Config$Test$MockConfiguration:hasKey"));

      assertEquals("get", "test",
                   Config.get("Config$Test$MockConfiguration:hasKey", "test"));

      assertEquals("now there", true,
                   Config.hasKey("Config$Test$MockConfiguration:hasKey"));
    }

    //......................................................................

    //----- mock -----------------------------------------------------------

    /** A simple mock for testing without the configuration. */
    public static class MockConfiguration implements Configuration
    {
      /** The simple storage for the values. */
      private java.util.Hashtable<String, String> m_storage =
        new java.util.Hashtable<String, String>();

      /** Get the given key or the given default, if the key is not found.
       *
       * @param   inKey the key to set
       * @param   inDefault the value to set to
       *
       * @return  true if the value was set, false else
       *
       */
      public @Nonnull String get(@Nonnull String inKey,
                                 @Nullable String inDefault)
      {
        if(m_storage.get(inKey) == null)
        {
          set(inKey, inDefault);

          return inDefault;
        }

        return m_storage.get(inKey);
      }

      /** Set the given key with the given String default.
       *
       * @param   inKey the key to set
       * @param   inDefault the value to set to
       *
       * @return  true if the value was set, false else
       *
       */
      public boolean set(@Nonnull String inKey, @Nullable String inDefault)
      {
        m_storage.put(inKey, inDefault);

        return true;
      }

      /** Remove the specified key.
       *
       * This is only possible in the mock configuration.
       *
       * @param   inKey the key to remove its value
       *
       * @return  true if the key was removed, false else
       */
      public boolean remove(@Nonnull String inKey)
      {
        return m_storage.remove(inKey) != null;
      }

      /** Clear the configuration. */
      public void clear()
      {
        m_storage.clear();
      }

      /** Check if a specific key is available.
       *
       * @param   inKey the key to get the value for
       *
       * @return  true if a key is available, false else
       *
       */
      public boolean hasKey(@Nonnull String inKey)
      {
        return m_storage.containsKey(inKey);
      }
    }

    //......................................................................
  }

  //........................................................................

  //------------------------------------------------------------------- main

  /** The main method.
   *
   * @param inArgs the command line arguments
   *
   */
//   public static void main(String []inArgs)
//   {
//     for(int i = 0; i < inArgs.length; i++)
//     {
//       java.lang.System.out.println(inArgs[i].replaceAll(".*:", "")
//                                    .replace('/', '.') + " = "
//                                    + Config.get(inArgs[i], "<undefined>"));
//     }
//  }

  //........................................................................
}
