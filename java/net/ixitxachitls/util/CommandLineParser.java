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

package net.ixitxachitls.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Joiner;

import net.ixitxachitls.util.logging.Log;

/**
 * A simple class for parsing command lines.
 *
 * @file          CommandLineParser.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@NotThreadSafe
public class CommandLineParser
{
  /** The representation of a single command line option. */
  @ThreadSafe
  public abstract static class Option
  {
    /**
     * Create the option.
     *
     * @param inShort the short option identifier (-)
     * @param inLong  the long option identifier (--)
     * @param inDescription the help description of the option
     */
    protected Option(String inShort, String inLong, String inDescription)
    {
      m_short = inShort;
      m_long = inLong;
      m_description = inDescription;
    }

    /** The short identifier for this option. */
    protected String m_short;

    /** The long identifier for this option. */
    protected String m_long;

    /** The description. */
    protected String m_description;

    /**
     * Get the short option identification.
     *
     * @return the short option identification
     */
    public String getShort()
    {
      return m_short;
    }

    /**
     * Get the long option identification.
     *
     * @return the long option identification
     */
    public String getLong()
    {
      return m_long;
    }

    /**
     * Get the option description.
     *
     * @return the option description
     */
    public String getDescription()
    {
      return m_description;
    }

    @Override
    public String toString()
    {
      return m_long;
    }

    /**
     * Parse the option from the given arguments.
     *
     * @param inArguments the arguments to parse for an option
     * @param inIndex     the index from which to start parsing
     *
     * @return the index of the next argument to parse
     */
    public abstract int parse(String []inArguments, int inIndex);

    /**
     * Determine if the option has a valid value.
     *
     * @return true if a value is present (default or given), false if not
     */
    public abstract boolean hasValue();
  }

  /** A command line option representing a flag value. */
  @ThreadSafe
  public static class Flag extends Option
  {
    /**
     * Create the flag option.
     *
     * @param inShort       the short identification
     * @param inLong        the long identification
     * @param inDescription the description
     *
     */
    public Flag(String inShort, String inLong, String inDescription)
    {
      super(inShort, inLong, inDescription);
    }

    /** The flag denoting if the flag was found on the command line or not. */
    private boolean m_present = false;

    @Override
    public String toString()
    {
      if(m_present)
        return s_longStart + super.toString();

      return "";
    }

    @Override
    public synchronized int parse(String []inArguments, int inIndex)
    {
      m_present = true;

      // don't need any arguments here
      return inIndex;
    }

    @Override
    public synchronized boolean hasValue()
    {
      return m_present;
    }
  }

  /** An option with an integer value. */
  @NotThreadSafe
  public static class IntegerOption extends Option
  {
    /**
     * Create the integer option.
     *
     * @param   inShort       the short name for the option
     * @param   inLong        the long name
     * @param   inDescription the option description
     * @param   inDefault     the default value to use if not given
     *                        (0 for no default)
     */
    public IntegerOption(String inShort, String inLong,
                         String inDescription, int inDefault)
    {
      super(inShort, inLong, inDescription);

      m_value = inDefault;
    }

    /** The value given with the option. */
    private int m_value;

    @Override
    public synchronized int parse(String []inArguments, int inIndex)
    {
      if(inArguments.length > inIndex)
      {
        try
        {
          m_value = new Integer(inArguments[inIndex]);

          return inIndex + 1;
        }
        catch(NumberFormatException e)
        {
          Log.error("expected an number, found '" + inArguments[inIndex]
                    + "' instead");
        }
      }
      else
        Log.error("expected a number");

      return inIndex;
    }

    @Override
    public boolean hasValue()
    {
      return m_value != 0;
    }

    /**
     * Get the integer value stored with this option.
     *
     * @return      the value given or the default value
     */
    public int get()
    {
      return m_value;
    }

    @Override
    public String toString()
    {
      return s_longStart + m_long + "=" + m_value;
    }
  }

  /** An option with a string value. */
  @NotThreadSafe
  public static class StringOption extends Option
  {
    /**
     * Create the string option.
     *
     * @param       inShort       the short name
     * @param       inLong        the long name
     * @param       inDescription the option description
     * @param       inDefault     the default value if none given
     *
     */
    public StringOption(String inShort, String inLong,
                        String inDescription, String inDefault)
    {
      super(inShort, inLong, inDescription);

      m_value = inDefault;
    }

    /** The value stored. */
    private String m_value;

    @Override
    public synchronized int parse(String []inArguments, int inIndex)
    {
      if(inArguments.length > inIndex)
        m_value = inArguments[inIndex];
      else
        Log.error("expected a string");

      return inIndex + 1;
    }

    @Override
    public boolean hasValue()
    {
      return !m_value.isEmpty();
    }

    /**
     * Get the string value stored with this option.
     *
     * @return      the value given or the default value
     */
    public String get()
    {
      return m_value;
    }

    @Override
    public String toString()
    {
      if(!hasValue())
        return "";

      return s_longStart + m_long + "=" + m_value;
    }
  }

  /** An option with a string value. */
  @ThreadSafe
  public static class StringListOption extends Option
  {
    /**
     * Create the string list option.
     *
     * @param       inShort       the short name
     * @param       inLong        the long name
     * @param       inDescription the option description
     * @param       inDefault     the default value if none given
     */
    public StringListOption(String inShort, String inLong,
                            String inDescription, String []inDefault)
    {
      super(inShort, inLong, inDescription);

      m_values = Arrays.copyOf(inDefault, inDefault.length);
    }

    /** The values stored. */
    private String []m_values;

    /** The joiner to convert to string. */
    private static final Joiner s_commaJoiner = Joiner.on(',');

    @Override
    public synchronized int parse(String []inArguments, int inIndex)
    {
      if(inArguments.length > inIndex)
        m_values = inArguments[inIndex].split("\\s*,\\s*");
      else
        Log.error("expected a comma separated list of strings");

      return inIndex + 1;
    }

    /**
     * Check if the option has a value or not.
     *
     * @return      true if it has a value, false if not
     */
    @Override
    public boolean hasValue()
    {
      return m_values.length > 0;
    }

    /**
     * Get the integer value stored with this option.
     *
     * @return      the value given or the default value
     */
    public String []get()
    {
      if(m_values != null)
        return Arrays.copyOf(m_values, m_values.length);

      return new String[0];
    }

    @Override
    public String toString()
    {
      if(!hasValue())
        return "";

      return s_longStart + m_long + "=" + s_commaJoiner.join(m_values);
    }
  }

  /** An option with an enumeration value. */
  @ThreadSafe
  public static class EnumOption extends Option
  {
    /**
     * Create the enum option.
     *
     * @param       inShort       the short name
     * @param       inLong        the long name
     * @param       inDescription the option description
     * @param       inDefault     the default value if none given
     */
    public EnumOption(String inShort, String inLong,
                      String inDescription, Enum<?> inDefault)
    {
      super(inShort, inLong, inDescription);

      m_value = inDefault;
    }

    /** The value stored. */
    private Enum<?> m_value;

    @Override
    @SuppressWarnings("unchecked")
    public synchronized int parse(String []inArguments, int inIndex)
    {
      if(inArguments.length > inIndex)
        m_value = Enum.valueOf(m_value.getClass(), inArguments[inIndex]);
      else
        Log.error("expected one of the possible values");

      return inIndex + 1;
    }

    @Override
    public synchronized boolean hasValue()
    {
      return true;
    }

    /**
     * Get the integer value stored with this option.
     *
     * @return      the value given or the default value
     */
    public synchronized Enum<?> get()
    {
      return m_value;
    }

    @Override
    public String toString()
    {
      return s_longStart + m_long + "=" + m_value;
    }
  }

  /**
   * Create the command line parser.
   *
   * @param       inOptions the command line options to recognize
   */
  public CommandLineParser(Option ... inOptions)
  {
    // add the standard options
    add(new Flag("h", "help",    "Show all command line options and quit."));
    add(new Flag("v", "version", "Show version information and quit."));

    for(Option option : inOptions)
      add(option);
  }

  /** The short ids and its options. */
  private Map<String, Option> m_shorts = new HashMap<String, Option>();

  /** The long ids and its options. */
  private Map<String, Option> m_longs = new HashMap<String, Option>();

  /** The start of a short option. */
  private static final String s_shortStart = "-";

  /** The start of a long option. */
  private static final String s_longStart = "--";

  /** A space joiner. */
  private static final Joiner s_spaceJoiner = Joiner.on(' ');

  /**
   * Check if for the given option a value has been given or set per default.
   *
   * @param       inName the long name of the option
   *
   * @return      true if a value was given or a default was set
   */
  public boolean hasValue(String inName)
  {
    Option option = m_longs.get(inName);

    if(option == null)
      return false;

    return option.hasValue();
  }

  @Override
  public String toString()
  {
    List<String> result = new ArrayList<String>();
    for(Option option : m_longs.values())
      if(option.hasValue())
        result.add(option.toString());

    return s_spaceJoiner.join(result);
  }

  /**
   * Get a help string with all possible options.
   *
   * @return      the help string
   */
  public String help()
  {
    StringBuilder result = new StringBuilder();

    // determine the maximal length of short and long names
    int maxShort = 0;
    int maxLong  = 0;

    for(String key : m_shorts.keySet())
      maxShort = Math.max(maxShort, key.length());

    for(String key : m_longs.keySet())
      maxLong = Math.max(maxLong, key.length());

    List<String> keys = new ArrayList<String>(m_longs.keySet());
    Collections.sort(keys);

    for(String key : keys)
    {
      Option option = m_longs.get(key);

      result.append("  -");
      result.append(option.getShort());
      result.append(Strings.spaces(maxShort - option.getShort().length() + 2));
      result.append("--");
      result.append(option.getLong());
      result.append(Strings.spaces(maxLong - option.getLong().length() + 2));
      result.append(option.getDescription());
      result.append('\n');
    }

    return result.toString();
  }

  /**
   * Get an integer value of an option.
   *
   * @param       inName the name of the option value to get (long name)
   *
   * @return      the integer value
   */
  public int getInteger(String inName)
  {
    Option option = m_longs.get(inName);

    if(option == null)
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not exist");

    if(!(option instanceof IntegerOption))
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not represent a number");

    return ((IntegerOption)option).get();
  }

  /**
   * Get a string value of an option.
   *
   * @param       inName the name of the option value to get (long name)
   *
   * @return      the string value
   *
   */
  public String getString(String inName)
  {
    Option option = m_longs.get(inName);

    if(option == null)
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not exist");

    if(!(option instanceof StringOption))
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not represent a string");

    return ((StringOption)option).get();
  }

  /**
   * Get a string list value of an option.
   *
   * @param       inName the name of the option value to get (long name)
   *
   * @return      the string list value
   *
   */
  public String []getStringList(String inName)
  {
    Option option = m_longs.get(inName);

    if(option == null)
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not exist");

    if(!(option instanceof StringListOption))
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not represent a string "
                                         + "list");

    return ((StringListOption)option).get();
  }

  /**
   * Get an enumeration value of an option.
   *
   * @param       inName the name of the option value to get (long name)
   *
   * @return      the string value
   */
  public Enum<?> getEnum(String inName)
  {
    Option option = m_longs.get(inName);

    if(option == null)
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not exist");

    if(!(option instanceof EnumOption))
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not represent an enum");

    return ((EnumOption)option).get();
  }

  /**
   * Get an integer value of an option.
   *
   * @param       inName    the name of the option value to get (long name)
   * @param       inDefault the default value to return if none set on the
   *                        command line
   *
   * @return      the integer value
   */
  public int get(String inName, int inDefault)
  {
    Option option = m_longs.get(inName);

    if(option == null)
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not exist");

    if(!(option instanceof IntegerOption))
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not represent a number");

    if(option.hasValue())
      return ((IntegerOption)option).get();

    return inDefault;
  }

  /**
   * Get a string value of an option.
   *
   * @param       inName    the name of the option value to get (long name)
   * @param       inDefault the default value to return if none set on command
   *                        line
   *
   * @return      the string value
   */
  public String get(String inName, String inDefault)
  {
    Option option = m_longs.get(inName);

    if(option == null)
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not exist");

    if(!(option instanceof StringOption))
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not represent a string");

    if(option.hasValue())
      return ((StringOption)option).get();

    return inDefault;
  }

  /**
   * Get a string array value of an option.
   *
   * @param       inName    the name of the option value to get (long name)
   * @param       inDefault the default value to return if none set on command
   *                        line
   *
   * @return      the string value
   */
  public String []get(String inName, String []inDefault)
  {
    Option option = m_longs.get(inName);

    if(option == null)
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not exist");

    if(!(option instanceof StringListOption))
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not represent a string "
                                         + "list");

    if(option.hasValue())
      return ((StringListOption)option).get();

    return inDefault;
  }

  /**
   * Get an enumeration value of an option.
   *
   * @param       inName    the name of the option value to get (long name)
   * @param       inDefault the default value to get if non set on command line
   *
   * @return      the string value
   */
  public Enum<?> get(String inName, Enum<?> inDefault)
  {
    Option option = m_longs.get(inName);

    if(option == null)
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not exist");

    if(!(option instanceof EnumOption))
      throw new IllegalArgumentException("option '" + inName
                                         + "' does not represent an enum");

    if(option.hasValue())
      return ((EnumOption)option).get();

    return inDefault;
  }

  /**
   * Add an option to the parser.
   *
   * @param       inOption the option to add
   *
   */
  public void add(Option inOption)
  {
    String shrt = inOption.getShort();
    String lng  = inOption.getLong();

    if(shrt != null)
      m_shorts.put(shrt, inOption);

    if(lng != null)
      m_longs.put(lng, inOption);
  }

  /**
   * Parse the given command line arguments.
   *
   * @param       inArguments the command line arguments
   *
   * @return      the arguments that were not parsed
   */
  public List<String> parse(String ... inArguments)
  {
    List<String> rest = new ArrayList<>();

    if(inArguments != null)
      for(int i = 0; i < inArguments.length; i = parse(inArguments, i, rest))
      {
        /* nothing to do */
      }

    return rest;
  }

  /**
   * Internal parsing method for a single option to parse.
   *
   * @param       inArguments the command line arguments
   * @param       inIndex     which argument to read
   * @param       ioRest      the unparsed rest
   *
   * @return      the next argument to read
   */
  private int parse(String []inArguments, int inIndex, List<String> ioRest)
  {
    if(inIndex >= inArguments.length)
      return inIndex;

    Option option = null;

    // check to see if we have a long or short option identifier
    if(inArguments[inIndex].startsWith(s_longStart))
    {
      option = m_longs.get(inArguments[inIndex].substring(2));

      if(option == null)
      {
        Log.warning("unknown command line option '" + inArguments[inIndex]
                    + "'");

        return inIndex + 1;
      }
    }
    else
      if(inArguments[inIndex].startsWith(s_shortStart))
      {
        option = m_shorts.get(inArguments[inIndex].substring(1));

        if(option == null)
        {
          Log.warning("unknown command line option '" + inArguments[inIndex]
                      + "'");

          return inIndex + 1;
        }
      }

    if(option == null)
    {
      ioRest.add(inArguments[inIndex]);

      return inIndex + 1;
    }

    return option.parse(inArguments, inIndex + 1);
  }

  //---------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Test default values. */
    @org.junit.Test
    public void defaultValue()
    {
      CommandLineParser clp = new CommandLineParser();

      assertEquals("empty", "", clp.toString());
      assertEquals("empty",
                   "  -h  --help     Show all command line options and quit.\n"
                   + "  -v  --version  Show version information and quit.\n",
                   clp.help());

      assertEquals("rest", "[guru]", clp.parse("guru").toString());

      clp = new CommandLineParser();
      assertEquals("rest", "[guru]", clp.parse("-h", "guru").toString());
      assertEquals("flag", "--help", clp.toString());
      assertTrue("value", clp.hasValue("help"));
      assertFalse("no value", clp.hasValue("guru"));
    }

    /** Testing integer values. */
    @org.junit.Test
    public void integer()
    {
      CommandLineParser clp =
        new CommandLineParser(new IntegerOption("p", "port",
                                                "The port the server runs on",
                                                5555),
                              new IntegerOption("o", "other", "other", 0));

      assertEquals("integer", "--port=5555", clp.toString());
      assertEquals("value", 5555, clp.getInteger("port"));
      assertEquals("value", 5555, clp.get("port", 1234));
      assertEquals("no value", 42, clp.get("other", 42));

      // check for invalid values
      try
      {
        clp.getInteger("help");
        fail("help should not be integer");
      }
      catch(IllegalArgumentException e)
      { /* nothing to do here */ }

      try
      {
        clp.getInteger("guru");
        fail("option should not exist");
      }
      catch(IllegalArgumentException e)
      { /* nothing to do here */ }

      // check for invalid values
      assertEquals("rest", "[hello]", clp.parse("hello").toString());

      assertEquals("default", 5555, clp.getInteger("port"));

      // correct setting
      assertTrue("rest", clp.parse("-p", "6666").isEmpty());
      assertEquals("set", 6666, clp.getInteger("port"));

      assertTrue("rest", clp.parse("--port", "7777").isEmpty());
      assertEquals("set", 7777, clp.getInteger("port"));

      // missing value
      assertTrue("rest", clp.parse("--port").isEmpty());
      m_logger.addExpected("ERROR: expected a number");
    }

    /** Testing string values. */
    @org.junit.Test
    public void string()
    {
      // setup
      CommandLineParser clp =
        new CommandLineParser(new StringOption("h", "host",
                                               "The host name to use.",
                                               "localhost"),
                              new StringOption("o", "other",
                                               "The other",
                                               ""));

      assertEquals("string", "--host=localhost", clp.toString());
      assertEquals("value", "localhost", clp.getString("host"));

      assertTrue("rest", clp.parse("--host", "www.ixitxachitls.net").isEmpty());
      assertEquals("value", "www.ixitxachitls.net", clp.getString("host"));
      assertEquals("value", "www.ixitxachitls.net", clp.get("host", "guru"));
      assertEquals("no value", "guru", clp.get("other", "guru"));

      // no value
      assertTrue("no value", clp.parse("--host").isEmpty());
      m_logger.addExpected("ERROR: expected a string");
    }

    /** Testing string list values. */
    @org.junit.Test
    public void stringList()
    {
      // setup
      CommandLineParser clp = new CommandLineParser
        (new StringListOption("h", "hosts", "The host name to use.",
                              new String [] { "localhost1", "localhost2" }),
         new StringListOption("o", "other", "The other",
                              new String [] { "default" }));

      assertEquals("string", "--other=default --hosts=localhost1,localhost2",
                   clp.toString());
      assertArrayEquals("value", new String [] { "localhost1", "localhost2" },
                        clp.getStringList("hosts"));

      assertTrue("rest",
                 clp.parse("--hosts", "www.ixitxachitls.net,gugus").isEmpty());
      assertArrayEquals("value",
                        new String [] { "www.ixitxachitls.net", "gugus" },
                        clp.getStringList("hosts"));
      assertArrayEquals("value",
                        new String [] { "www.ixitxachitls.net", "gugus" },
                        clp.get("hosts", new String [] { "guru" }));
      assertArrayEquals("no value", new String [] { "default" },
                        clp.get("other", new String [] { "guru" }));

      assertTrue("rest", clp.parse("--hosts").isEmpty());
      m_logger.addExpected("ERROR: expected a comma separated list of "
                           + "strings");
    }

    /** An enum used for testing. */
    private enum TestEnum { one, two, three, four };

    /** Testing string values. */
    @org.junit.Test
    public void enumValue()
    {
      // setup
      CommandLineParser clp =
        new CommandLineParser(new EnumOption("e", "enum",
                                             "Some enumeration option",
                                             TestEnum.three),
                              new EnumOption("o", "other", "other",
                                             TestEnum.one));

      assertEquals("enum", "--other=one --enum=three", clp.toString());
      assertEquals("value", TestEnum.three, clp.getEnum("enum"));

      assertTrue("rest", clp.parse("--enum", "two").isEmpty());
      assertEquals("value", TestEnum.two, clp.getEnum("enum"));
      assertEquals("value", TestEnum.two, clp.get("enum", TestEnum.one));
      assertEquals("value", TestEnum.one, clp.get("other", TestEnum.three));

      assertTrue("rest", clp.parse("--enum").isEmpty());
      m_logger.addExpected("ERROR: expected one of the possible values");
    }
  }
}
