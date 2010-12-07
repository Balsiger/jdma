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

package net.ixitxachitls.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Joiner;

import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A simple class for parsing command lines.
 *
 * @file          CommandLineParser.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class CommandLineParser
{
  //----------------------------------------------------------------- nested

  //----- Option -----------------------------------------------------------

  /** The representation of a single command line option. */
  @ThreadSafe
  public abstract static class Option
  {
    //------------------------------- Option -------------------------------

    /**
     * Create the option.
     *
     * @param inShort the short option identifier (-)
     * @param inLong  the long option identifier (--)
     * @param inDescription the help description of the option
     *
     */
    public Option(@Nullable String inShort, @Nullable String inLong,
                  @Nonnull String inDescription)
    {
      if(inShort == null && inLong == null)
        throw new IllegalArgumentException("must have at least a long or "
                                           + "short identifier");

      m_short       = inShort;
      m_long        = inLong;
      m_description = inDescription;
    }

    //......................................................................

    //------------------------------------------------------------ variables

    /** The short identifier for this option. */
    protected @Nullable String m_short;

    /** The long identifier for this option. */
    protected @Nullable String m_long;

    /** The description. */
    protected @Nonnull String m_description;

    //......................................................................

    //------------------------------ getShort ------------------------------

    /**
     * Get the short option identification.
     *
     * @return the short option identification
     *
     */
    public @Nullable String getShort()
    {
      return m_short;
    }

    //......................................................................
    //------------------------------ getLong -------------------------------

    /**
     * Get the long option identification.
     *
     * @return the long option identification
     *
     */
    public @Nullable String getLong()
    {
      return m_long;
    }

    //......................................................................
    //--------------------------- getDescription ---------------------------

    /**
     * Get the option description.
     *
     * @return the option description
     *
     */
    public @Nonnull String getDescription()
    {
      return m_description;
    }

    //......................................................................
    //------------------------------ toString ------------------------------

    /**
     * Convert the option into a human readable string.
     *
     * @return the string representation of the option
     *
     */
    public @Nonnull String toString()
    {
      if(m_long != null)
        return m_long;

      return m_short;
    }

    //......................................................................
    //------------------------------- parse --------------------------------

    /**
     * Parse the option from the given arguments.
     *
     * @param inArguments the arguments to parse for an option
     * @param inIndex     the index from which to start parsing
     *
     * @return the index of the next argument to parse
     *
     */
    public abstract int parse(@Nonnull String []inArguments, int inIndex);

    //......................................................................
    //------------------------------ hasValue ------------------------------

    /**
     * Determine if the option has a valid value.
     *
     * @return true if a value is present (default or given), false if not
     *
     */
    public abstract boolean hasValue();

    //......................................................................
  }

  //........................................................................
  //----- Flag -------------------------------------------------------------

  /** A command line option representing a flag value. */
  @ThreadSafe
  public static class Flag extends Option
  {
    //-------------------------------- Flag --------------------------------

    /**
     * Create the flag option.
     *
     * @param inShort       the short identification
     * @param inLong        the long identification
     * @param inDescription the description
     *
     */
    public Flag(@Nullable String inShort, @Nullable String inLong,
                @Nonnull String inDescription)
    {
      super(inShort, inLong, inDescription);
    }

    //......................................................................

    //------------------------------------------------------------ variables

    /** The flag denoting if the flag was found on the command line or not. */
    private boolean m_present = false;

    //......................................................................

    //------------------------------ toString ------------------------------

    /**
     * Convert the option into a human readable string.
     *
     * @return the string representation of the option
     *
     */
    public @Nonnull String toString()
    {
      if(m_present)
        return s_longStart + super.toString();

      return "";
    }

    //......................................................................
    //------------------------------- parse --------------------------------

    /**
     * Parse the option from the given arguments.
     *
     * @param  inArguments the arguments to parse for an option
     * @param  inIndex     the index from which to start parsing
     *
     * @return the index of the next argument to read
     *
     */
    public synchronized int parse(@Nonnull String []inArguments, int inIndex)
    {
      m_present = true;

      // don't need any arguments here
      return inIndex;
    }

    //......................................................................
    //------------------------------ hasValue ------------------------------

    /**
     * Determine if the option has a valid value.
     *
     * @return true if a value is present (default or given), false if not
     *
     */
    public synchronized boolean hasValue()
    {
      return m_present;
    }

    //......................................................................
  }

  //........................................................................
  //----- IntegerOption ----------------------------------------------------

  /** An option with an integer value. */
  @NotThreadSafe
  public static class IntegerOption extends Option
  {
    //--------------------------- IntegerOption ----------------------------

    /**
     * Create the integer option.
     *
     * @param   inShort       the short name for the option
     * @param   inLong        the long name
     * @param   inDescription the option description
     * @param   inDefault     the default value to use if not given
     *
     */
    public IntegerOption(@Nullable String inShort, @Nullable String inLong,
                         @Nonnull String inDescription,
                         @Nullable Integer inDefault)
    {
      super(inShort, inLong, inDescription);

      m_value = inDefault;
    }

    //......................................................................

    //------------------------------------------------------------ variables

    /** The value given with the option. */
    private @Nullable Integer m_value = null;

    //......................................................................

    //------------------------------- parse --------------------------------

    /**
     * Parse the given command line arguments.
     *
     * @param       inArguments the arguments to parse
     * @param       inIndex     the index to start parsing from
     *
     * @return      the index of the next value to parse
     *
     */
    public synchronized int parse(@Nonnull String []inArguments, int inIndex)
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

    //......................................................................
    //------------------------------ hasValue ------------------------------

    /**
     * Check if the option has a value or not.
     *
     * @return      true if it has a value, false if not
     *
     */
    public boolean hasValue()
    {
      return m_value != null;
    }

    //......................................................................
    //-------------------------------- get ---------------------------------

    /**
     * Get the integer value stored with this option.
     *
     * @return      the value given or the default value
     *
     * @undefined   never
     *
     */
    public int get()
    {
      if(m_value != null)
        return m_value;

      return 0;
    }

    //......................................................................
    //------------------------------ toString ------------------------------

    /**
     * Convert the option to a string for debugging.
     *
     * @return      a string representation of the option
     *
     * @undefined   never
     *
     */
    public String toString()
    {
      if(hasValue())
        return s_longStart + m_long + "=" + m_value;

      return "";
    }

    //......................................................................
  }

  //........................................................................
  //----- StringOption -----------------------------------------------------

  /** An option with a string value. */
  @NotThreadSafe
  public static class StringOption extends Option
  {
    //---------------------------- StringOption ----------------------------

    /**
     * Create the string option.
     *
     * @param       inShort       the short name
     * @param       inLong        the long name
     * @param       inDescription the option description
     * @param       inDefault     the default value if none given
     *
     */
    public StringOption(@Nullable String inShort, @Nullable String inLong,
                        @Nonnull String inDescription,
                        @Nullable String inDefault)
    {
      super(inShort, inLong, inDescription);

      m_value = inDefault;
    }

    //......................................................................

    //------------------------------------------------------------ variables

    /** The value stored. */
    private @Nullable String m_value = null;

    //......................................................................

    //------------------------------- parse --------------------------------

    /**
     * Parse the given command line arguments.
     *
     * @param       inArguments the argument to parse
     * @param       inIndex     the index to start parsing from
     *
     * @return      the index of the next argument to parse
     *
     */
    public synchronized int parse(@Nonnull String []inArguments, int inIndex)
    {
      if(inArguments.length > inIndex)
        m_value = inArguments[inIndex];
      else
        Log.error("expected a string");

      return inIndex + 1;
    }

    //......................................................................
    //------------------------------ hasValue ------------------------------

    /**
     * Check if the option has a value or not.
     *
     * @return      true if it has a value, false if not
     *
     */
    public boolean hasValue()
    {
      return m_value != null;
    }

    //......................................................................
    //-------------------------------- get ---------------------------------

    /**
     * Get the string value stored with this option.
     *
     * @return      the value given or the default value
     *
     */
    public @Nullable String get()
    {
      return m_value;
    }

    //......................................................................
    //------------------------------ toString ------------------------------

    /**
     * Convert the option to a string for debugging.
     *
     * @return      a string representation of the option
     *
     */
    public @Nonnull String toString()
    {
      if(hasValue())
        return s_longStart + m_long + "=" + m_value;

      return "";
    }

    //......................................................................
  }

  //........................................................................
  //----- StringListOption -------------------------------------------------

  /** An option with a string value. */
  @ThreadSafe
  public static class StringListOption extends Option
  {
    //-------------------------- StringListOption --------------------------

    /**
     * Create the string list option.
     *
     * @param       inShort       the short name
     * @param       inLong        the long name
     * @param       inDescription the option description
     * @param       inDefault     the default value if none given
     *
     */
    public StringListOption(@Nullable String inShort, @Nullable String inLong,
                            @Nonnull String inDescription,
                            @Nonnull String []inDefault)
    {
      super(inShort, inLong, inDescription);

      m_values = Arrays.copyOf(inDefault, inDefault.length);
    }

    //.................................................................

    //------------------------------------------------------------ variables

    /** The values stored. */
    private @Nullable String []m_values = null;

    /** The joiner to convert to string. */
    private static final Joiner s_commaJoiner = Joiner.on(',');

    //......................................................................

    //------------------------------- parse --------------------------------

    /**
     * Parse the given command line arguments.
     *
     * @param       inArguments the argument to parse
     * @param       inIndex     the index to start parsing from
     *
     * @return      the index of the next argument to parse
     *
     */
    public synchronized int parse(String []inArguments, int inIndex)
    {
      if(inArguments.length > inIndex)
        m_values = inArguments[inIndex].split("\\s*,\\s*");
      else
        Log.error("expected a comma separated list of strings");

      return inIndex + 1;
    }

    //......................................................................
    //------------------------------ hasValue ------------------------------

    /**
     * Check if the option has a value or not.
     *
     * @return      true if it has a value, false if not
     *
     */
    public boolean hasValue()
    {
      return m_values != null;
    }

    //......................................................................
    //-------------------------------- get ---------------------------------

    /**
     * Get the integer value stored with this option.
     *
     * @return      the value given or the default value
     *
     */
    public @Nonnull String []get()
    {
      if(m_values != null)
        return Arrays.copyOf(m_values, m_values.length);

      return new String[0];
    }

    //......................................................................
    //------------------------------ toString ------------------------------

    /**
     * Convert the option to a string for debugging.
     *
     * @return      a string representation of the option
     *
     */
    public @Nonnull String toString()
    {
      if(hasValue())
        return s_longStart + m_long + "=" + s_commaJoiner.join(m_values);

      return "";
    }

    //......................................................................
  }

  //........................................................................
  //----- EnumOption -------------------------------------------------------

  /** An option with an enumeration value. */
  @ThreadSafe
  public static class EnumOption extends Option
  {
    //----------------------------- EnumOption -----------------------------

    /**
     * Create the enum option.
     *
     * @param       inShort       the short name
     * @param       inLong        the long name
     * @param       inDescription the option description
     * @param       inDefault     the default value if none given
     *
     */
    public EnumOption(@Nullable String inShort, @Nullable String inLong,
                      @Nonnull String inDescription, @Nullable Enum inDefault)
    {
      super(inShort, inLong, inDescription);

      m_value = inDefault;
    }

    //......................................................................

    //------------------------------------------------------------ variables

    /** The value stored. */
    private @Nullable Enum m_value;

    //......................................................................

    //------------------------------- parse --------------------------------

    /**
     * Parse the given command line arguments.
     *
     * @param       inArguments the argument to parse
     * @param       inIndex     the index to start parsing from
     *
     * @return      the index of the next argument to parse
     *
     */
    @SuppressWarnings("unchecked")
    public synchronized int parse(@Nonnull String []inArguments, int inIndex)
    {
      if(inArguments.length > inIndex)
        m_value = m_value.valueOf(m_value.getClass(), inArguments[inIndex]);
      else
        Log.error("expected one of the possible values");

      return inIndex + 1;
    }

    //......................................................................
    //------------------------------ hasValue ------------------------------

    /**
     * Check if the option has a value or not.
     *
     * @return      true if it has a value, false if not
     *
     */
    public synchronized boolean hasValue()
    {
      return m_value != null;
    }

    //......................................................................
    //-------------------------------- get ---------------------------------

    /**
     * Get the integer value stored with this option.
     *
     * @return      the value given or the default value
     *
     */
    public synchronized @Nullable Enum get()
    {
      if(m_value != null)
        return m_value;

      return null;
    }

    //......................................................................
    //------------------------------ toString ------------------------------

    /**
     * Convert the option to a string for debugging.
     *
     * @return      a string representation of the option
     *
     * @undefined   never
     *
     */
    public @Nonnull String toString()
    {
      if(hasValue())
        return s_longStart + m_long + "=" + m_value;

      return "";
    }

    //......................................................................
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //-------------------------- CommandLineParser ---------------------------

  /**
   * Create the parser.
   *
   * @param       inOptions the command line options to recognize
   *
   */
  public CommandLineParser(@Nonnull Option ... inOptions)
  {
    // add the standard options
    add(new Flag("h", "help",    "Show all command line options and quit."));
    add(new Flag("v", "version", "Show version information and quit."));

    for(Option option : inOptions)
      add(option);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The short ids and its options. */
  private @Nonnull Map<String, Option> m_shorts =
    new HashMap<String, Option>();

  /** The long ids and its options. */
  private @Nonnull Map<String, Option> m_longs = new HashMap<String, Option>();

  /** The start of a short option. */
  private static final @Nonnull String s_shortStart = "-";

  /** The start of a long option. */
  private static final @Nonnull String s_longStart = "--";

  /** A space joiner. */
  private static final @Nonnull Joiner s_spaceJoiner = Joiner.on(' ');

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- hasValue -------------------------------

  /**
   * Check if for the given option a value has been given or set per default.
   *
   * @param       inName the long name of the option
   *
   * @return      true if a value was given or a default was set
   *
   */
  public boolean hasValue(@Nonnull String inName)
  {
    Option option = m_longs.get(inName);

    if(option == null)
      return false;

    return option.hasValue();
  }

  //........................................................................
  //------------------------------- toString -------------------------------

  /**
   * Convert to a human readable string representation.
   *
   * @return      the String representation
   *
   */
  public @Nonnull String toString()
  {
    List<String> result = new ArrayList<String>();
    for(Option option : m_longs.values())
      if(option.hasValue())
        result.add(option.toString());

    return s_spaceJoiner.join(result);
  }

  //........................................................................
  //--------------------------------- help ---------------------------------

  /**
   * Get a help string with all possible options.
   *
   * @return      the help string
   *
   */
  public @Nonnull String help()
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
      result.append("\n");
    }

    return result.toString();
  }

  //........................................................................

  //------------------------------ getInteger ------------------------------

  /**
   * Get an integer value of an option.
   *
   * @param       inName the name of the option value to get (long name)
   *
   * @return      the integer value
   *
   */
  public int getInteger(@Nonnull String inName)
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

  //........................................................................
  //------------------------------ getString -------------------------------

  /**
   * Get a string value of an option.
   *
   * @param       inName the name of the option value to get (long name)
   *
   * @return      the string value
   *
   */
  public @Nullable String getString(@Nonnull String inName)
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

  //........................................................................
  //---------------------------- getStringList -----------------------------

  /**
   * Get a string list value of an option.
   *
   * @param       inName the name of the option value to get (long name)
   *
   * @return      the string list value
   *
   */
  public @Nullable String []getStringList(@Nonnull String inName)
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

  //........................................................................
  //------------------------------- getEnum --------------------------------

  /**
   * Get an enumeration value of an option.
   *
   * @param       inName the name of the option value to get (long name)
   *
   * @return      the string value
   *
   */
  public @Nullable Enum getEnum(@Nonnull String inName)
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

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * Get an integer value of an option.
   *
   * @param       inName    the name of the option value to get (long name)
   * @param       inDefault the default value to return if none set on the
   *                        command line
   *
   * @return      the integer value
   *
   */
  public int get(@Nonnull String inName, int inDefault)
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

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * Get a string value of an option.
   *
   * @param       inName    the name of the option value to get (long name)
   * @param       inDefault the default value to return if none set on command
   *                        line
   *
   * @return      the string value
   *
   */
  public @Nonnull String get(@Nonnull String inName, @Nonnull String inDefault)
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

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * Get a string array value of an option.
   *
   * @param       inName    the name of the option value to get (long name)
   * @param       inDefault the default value to return if none set on command
   *                        line
   *
   * @return      the string value
   *
   */
  public @Nonnull String []get(@Nonnull String inName,
                               @Nonnull String []inDefault)
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

  //........................................................................
  //--------------------------------- get ----------------------------------

  /**
   * Get an enumeration value of an option.
   *
   * @param       inName    the name of the option value to get (long name)
   * @param       inDefault the default value to get if non set on command line
   *
   * @return      the string value
   *
   */
  public @Nonnull Enum get(@Nonnull String inName, @Nonnull Enum inDefault)
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

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- add ----------------------------------

  /**
   * Add an option to the parser.
   *
   * @param       inOption the option to add
   *
   */
  public void add(@Nonnull Option inOption)
  {
    String shrt = inOption.getShort();
    String lng  = inOption.getLong();

    if(shrt != null)
      m_shorts.put(shrt, inOption);

    if(lng != null)
      m_longs.put(lng, inOption);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //-------------------------------- parse ---------------------------------

  /**
   * Parse the given command line arguments.
   *
   * @param       inArguments the command line arguments
   *
   * @return      the text that was not parsed by any option or null if
   *              everything was parsed
   *
   */
  public @Nullable String parse(@Nonnull String ... inArguments)
  {
    StringBuilder rest = new StringBuilder();

    if(inArguments != null)
      for(int i = 0; i < inArguments.length; i = parse(inArguments, i, rest))
        ;

    if(rest.length() == 0)
      return null;

    return rest.toString().trim();
  }

  //........................................................................
  //-------------------------------- parse ---------------------------------

  /**
   * Internal parsing method for a single option to parse.
   *
   * @param       inArguments the command line arguments
   * @param       inIndex     which argument to read
   * @param       ioRest      the unparsed rest
   *
   * @return      the next argument to read
   *
   */
  private int parse(@Nonnull String []inArguments, int inIndex,
                    @Nonnull StringBuilder  ioRest)
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
      ioRest.append(inArguments[inIndex]);
      ioRest.append(" ");

      return inIndex + 1;
    }

    return option.parse(inArguments, inIndex + 1);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- default --------------------------------------------------------

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

      assertEquals("rest", "guru", clp.parse("guru"));

      clp = new CommandLineParser();
      assertEquals("rest", "guru", clp.parse("-h", "guru"));
      assertEquals("flag", "--help", clp.toString());
      assertTrue("value", clp.hasValue("help"));
      assertFalse("no value", clp.hasValue("guru"));
    }

    //......................................................................
    //----- integer --------------------------------------------------------

    /** Testing integer values. */
    @org.junit.Test
    public void integer()
    {
      CommandLineParser clp =
        new CommandLineParser(new IntegerOption("p", "port",
                                                "The port the server runs on",
                                                5555),
                              new IntegerOption("o", "other", "other", null));

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
      assertEquals("rest", "hello", clp.parse("hello"));

      assertEquals("default", 5555, clp.getInteger("port"));

      // correct setting
      assertNull("rest", clp.parse("-p", "6666"));
      assertEquals("set", 6666, clp.getInteger("port"));

      assertNull("rest", clp.parse("--port", "7777"));
      assertEquals("set", 7777, clp.getInteger("port"));

      // missing value
      assertNull("rest", clp.parse("--port"));
      m_logger.addExpected("ERROR: expected a number");
    }

    //......................................................................
    //----- string ---------------------------------------------------------

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
                                               null));

      assertEquals("string", "--host=localhost", clp.toString());
      assertEquals("value", "localhost", clp.getString("host"));

      assertNull("rest", clp.parse("--host", "www.ixitxachitls.net"));
      assertEquals("value", "www.ixitxachitls.net", clp.getString("host"));
      assertEquals("value", "www.ixitxachitls.net", clp.get("host", "guru"));
      assertEquals("no value", "guru", clp.get("other", "guru"));

      // no value
      assertNull("no value", clp.parse("--host"));
      m_logger.addExpected("ERROR: expected a string");
    }

    //......................................................................
    //----- stringlist -----------------------------------------------------

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

      assertNull("rest", clp.parse("--hosts", "www.ixitxachitls.net,gugus"));
      assertArrayEquals("value",
                        new String [] { "www.ixitxachitls.net", "gugus" },
                        clp.getStringList("hosts"));
      assertArrayEquals("value",
                        new String [] { "www.ixitxachitls.net", "gugus" },
                        clp.get("hosts", new String [] { "guru" }));
      assertArrayEquals("no value", new String [] { "default" },
                        clp.get("other", new String [] { "guru" }));

      assertNull("rest", clp.parse("--hosts"));
      m_logger.addExpected("ERROR: expected a comma separated list of "
                           + "strings");
    }

    //......................................................................
    //----- enum -----------------------------------------------------------

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
                              new EnumOption("o", "other", "other", null));

      assertEquals("enum", "--enum=three", clp.toString());
      assertEquals("value", TestEnum.three, clp.getEnum("enum"));

      assertNull("rest", clp.parse("--enum", "two"));
      assertEquals("value", TestEnum.two, clp.getEnum("enum"));
      assertEquals("value", TestEnum.two, clp.get("enum", TestEnum.one));
      assertEquals("value", TestEnum.three, clp.get("other", TestEnum.three));

      assertNull("rest", clp.parse("--enum"));
      m_logger.addExpected("ERROR: expected one of the possible values");
    }

    //......................................................................
  }

  //........................................................................
}
