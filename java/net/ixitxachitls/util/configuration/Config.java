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

package net.ixitxachitls.util.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the static accessor to all the configurations available in the
 * system. The configuration values are backed with system properites and can
 * also be set that way (you have to set them before accessing them, though).w
 *
 *
 * @file          Config.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @example       <PRE>
 * // get a value
 * Config.get("dir/file/key.subkey", "guru");
 * </PRE>
 *
 */

//..........................................................................

//__________________________________________________________________________

@ThreadSafe
@ParametersAreNonnullByDefault
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

  /** The postfix for integer values. */
  private static final String s_int = ":int";

  /** The postfix for long values. */
  private static final String s_long = ":long";

  /** The postfix for float values. */
  private static final String s_float = ":float";

  /** The postfix for double values. */
  private static final String s_double = ":double";

  /** The postfix for character values. */
  private static final String s_char = ":char";

  /** The postfix for boolean values. */
  private static final String s_boolean = ":boolean";

  /** The postfix for array values. */
  private static final String s_list = ":list";

  /** All the configuration values used so far. */
  private static final SortedSet<String> s_names = new TreeSet<String>();

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------------- get ----------------------------------

  /**
   * This is the get method for strings.
   *
   * All public getters use this method to actually access a value.
   *
   * @param       inKey     the key of the value to get (not null)
   * @param       inDefault the default value if none is currently stored
   *                        (may be null to ignore rewrite)
   *
   * @return      the requested configuration value or the default value if
   *              none was found (guaranteed non null)
   *
   */
  public static synchronized String get(String inKey,
                                        @Nullable String inDefault)
  {
    String value = System.getProperty(inKey);
    if(value != null)
      return value;

    set(inKey, inDefault);
    return inDefault;
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
  public static int get(String inKey, int inDefault)
  {
    return Integer.parseInt(get(inKey + s_int, "" + inDefault));
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
  public static long get(String inKey, long inDefault)
  {
    return Long.parseLong(get(inKey + s_long, "" + inDefault));
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
  public static float get(String inKey, float inDefault)
  {
    return Float.parseFloat(get(inKey + s_float, "" + inDefault));
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
  public static double get(String inKey, double inDefault)
  {
    return Double.parseDouble(get(inKey + s_double, "" + inDefault));
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
  public static char get(String inKey, char inDefault)
  {
    return get(inKey + s_char, "" + inDefault).charAt(0);
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
  public static boolean get(String inKey, boolean inDefault)
  {
    return Boolean.valueOf(get(inKey + s_boolean, "" + inDefault))
      .booleanValue();
  }

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * Get a list of property values from the configuration.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found
   *
   * @return      the requested string list
   *
   */
  public static String []get(String inKey, String []inDefault)
  {
    // if it does not yet exists, simply store and return it
    if(System.getProperty(inKey + s_list + "." + 0) == null)
    {
      set(inKey, inDefault);

      return inDefault;
    }

    // get the real values from the configuration
    List<String> list = new ArrayList<String>();
    for(int i = 0; System.getProperty(inKey + s_list + "." + i) != null; i++)
      list.add(get(inKey + s_list + "." + i, "guru"));

    return list.toArray(new String[list.size()]);
  }

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * Get a property value from the configuration.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found
   *
   * @return      the requested integer array
   *
   */
  public static int []get(String inKey, int []inDefault)
  {
    String []defaults = new String[inDefault.length];
    for(int i = 0; i < inDefault.length; i++)
        defaults[i] = "" + inDefault[i];

    String []strings = get(inKey + s_int, defaults);

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
  public static boolean []get(String inKey, boolean []inDefault)
  {
    String []defaults = new String[inDefault.length];

    for(int i = 0; i < inDefault.length; i++)
      defaults[i] = "" + inDefault[i];

    String []strings = get(inKey + s_boolean, defaults);

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
  public static String [][]get(String inKey, String [][]inDefault)
  {
    // if it does not yet exists, simply store and return it
    if(System.getProperty(inKey + s_list + ".0.0") == null)
    {
      set(inKey, inDefault);

      return inDefault;
    }

    // get the real values from the configuration
    List<String []> list = new ArrayList<String []>();
    for(int i = 0; System.getProperty(inKey + s_list + "." + i + ".0") != null;
        i++)
    {
      List<String> sub = new ArrayList<String>();

      for(int j = 0;
          System.getProperty(inKey + s_list + "." + i + "." + j) != null; j++)
        sub.add(get(inKey + s_list + "." + i + "." + j, "guru"));

      list.add(sub.toArray(new String[sub.size()]));
    }

    return list.toArray(new String[list.size()][]);
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
  public static String getPattern(String inKey, @Nullable String inDefault)
  {
    String pattern = get(inKey, inDefault);

    // now replace the pattern(s), if any
    Matcher matcher = Pattern.compile("\\{(.*?)\\}").matcher(pattern);

    StringBuffer result = new StringBuffer();
    while(matcher.find())
      matcher.appendReplacement(result,
                                Matcher
                                .quoteReplacement(Config.get(matcher.group(1),
                                                             "[unknown]")));

    matcher.appendTail(result);

    return result.toString();
  }

  //........................................................................
  //------------------------------ getValues -------------------------------

  /**
   * A map with all configuration names.
   *
   * @return  all the configuration names used so far
   *
   */
  public static SortedSet<String> getNames()
  {
    return Collections.unmodifiableSortedSet(s_names);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey     the key of the value to obtain
   * @param       inValue   the new value to set to
   *
   */
  public static void set(String inKey, String inValue)
  {
    s_names.add(inKey);
    System.setProperty(inKey, inValue);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   */
  public static void set(String inKey, int inValue)
  {
    set(inKey + s_int, "" + inValue);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   */
  public static void set(String inKey, long inValue)
  {
    set(inKey + s_long, "" + inValue);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   */
  public static void set(String inKey, float inValue)
  {
    set(inKey + s_float, "" + inValue);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   */
  public static void set(String inKey, double inValue)
  {
    set(inKey + s_double, "" + inValue);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   */
  public static void set(String inKey, char inValue)
  {
    set(inKey + s_char, "" + inValue);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   */
  public static void set(String inKey, boolean inValue)
  {
    set(inKey + s_boolean, "" + inValue);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   */
  public static void set(String inKey, String []inValue)
  {
    // remove old values
    for(int i = 0; System.getProperty(inKey + s_list + "." + i) != null; i++)
      System.clearProperty(inKey + s_list + "." + i);

    for(int i = 0; i < inValue.length; i++)
      set(inKey + s_list + "." + i, inValue[i]);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   */
  public static void set(String inKey, int []inValue)
  {
    String []values = new String [inValue.length];

    for(int i = 0; i < inValue.length; i++)
      values[i] = "" + inValue[i];

    set(inKey + s_int, values);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   */
  public static void set(String inKey, boolean []inValue)
  {
    String []values = new String [inValue.length];

    for(int i = 0; i < inValue.length; i++)
      values[i] = "" + inValue[i];

    set(inKey + s_boolean, values);
  }

  //........................................................................
  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey   the key of the value to obtain
   * @param       inValue the new value to set to
   *
   */
  public static void set(String inKey, String [][]inValue)
  {
    // clear existing values
    for(int i = 0; System.getProperty(inKey + s_list + "." + i + ".0") != null;
        i++)
      for(int j = 0;
          System.getProperty(inKey + s_list + "." + i + "." + j) != null; j++)
        System.clearProperty(inKey + s_list + "." + i + "." + j);

    for(int i = 0; i < inValue.length; i++)
    {
      if(inValue[i] == null)
        throw new IllegalArgumentException("no value given can be null");

      for(int j = 0; j < inValue[i].length; j++)
      {
        if(inValue[i][j] == null)
          throw new IllegalArgumentException("no value given can be null");

        set(inKey + s_list + "." + i + "." + j, inValue[i][j]);
      }

    }
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
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

      m_logger.verify();
    }

    //......................................................................
    //----- set ------------------------------------------------------------

    /** Test for setting values. */
    @org.junit.Test
    public void set()
    {
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
      Config.set("Config$Test$MockConfiguration:test", sCheck);
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
                   "just a [unknown] test with a [unknown] test",
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
  }

  //........................................................................
}
