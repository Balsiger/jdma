/******************************************************************************
 * Copyright (c) 2002-2015 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.NestedEntry;
import net.ixitxachitls.util.Strings;

/**
 * A utility class to parse and generally deal with values.
 *
 * @file Values.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Values
{
  /** Check if a string given for a value is valid. */
  public interface Checker
  {
    /**
     * Check whether the given string repesents a value value.
     *
     * @param inCheck the string to check
     * @return true if the string is valid, false if not
     */
    public boolean check(String inCheck);
  }

  /** A checker to check for a non empty value. */
  public static final Checker NOT_EMPTY = new Checker()
  {
    @Override
    public boolean check(String inCheck)
    {
      return !inCheck.isEmpty();
    }
  };

  /** Interface for a parser to values. */
  public interface Parser<T>
  {
    /**
     * Parse the given values.
     *
     * @param inValues the values to parse from
     * @return the parsed values
     */
    public T parse(String ... inValues);
  }

  /**
   * Create a new set of values.
   *
   * @param inValues the values to create from
   */
  public Values(Multimap<String, String> inValues)
  {
    m_values = inValues;
  }

  /** All the values. */
  private final Multimap<String, String> m_values;

  /** Messages collected so far when setting the values. */
  private final List<String> m_messages = new ArrayList<>();

  /** The names of the values handled so far. */
  private final Set<String> m_handled = new HashSet<>();

  /** Flag whether the values changed an entry. */
  private boolean m_changed = false;

  /**
   * Check whether the values changed an entry.
   *
   * @return true if an entry has been changed, false if not
   */
  public boolean isChanged()
  {
    return m_changed;
  }

  /**
   * Use a value to set an entry value.
   *
   * @param inKey the name of the value
   * @param inDefault the default to use if no value is set
   *
   * @return the value to set to
   */
  public String use(String inKey, String inDefault)
  {
    return use(inKey, inDefault, Optional.<Checker>absent());
  }

  /**
   * Use a value to set an entry value.
   *
   * @param inKey the name of the value
   * @param inDefault the optional default value
   *
   * @return the value to set to
   */
  public Optional<String> use(String inKey, Optional<String> inDefault)
  {
    Optional<String> value = getFirst(inKey);
    if(!value.isPresent()
        && (!inDefault.isPresent() || inDefault.get().isEmpty()))
      return inDefault;

    if(!inDefault.equals(value))
      m_changed = true;

    return value;
  }

  /**
   * Use a value to set an entry value, checking if the value fulfills some
   * check.
   *
   * @param inKey the name of the value
   * @param inDefault the default value to use if none is set
   * @param inChecker the checker to check whether the value is valid
   *
   * @return the value to use for setting
   */
  public String use(String inKey, String inDefault, Checker inChecker)
  {
    return use(inKey, inDefault, Optional.of(inChecker));
  }

  /**
   * Use a value to set an entry value, optionally checking if the value
   * fulfills some check.
   *
   * @param inKey the name of the value
   * @param inDefault the default value
   * @param inChecker the optional validation checker
   *
   * @return the string to use to set the value
   */
  public String use(String inKey, String inDefault, Optional<Checker> inChecker)
  {
    Optional<String> value = getFirst(inKey);
    if(!value.isPresent())
      return inDefault;

    if(!inDefault.equals(value))
      m_changed = true;

    if(inChecker.isPresent() && !inChecker.get().check(value.get()))
    {
      m_messages.add("Check for '" + inKey + "' failed, value not set.");
      return inDefault;
    }

    return value.get();
  }

  /**
   * Use a list value to set an entry value.
   *
   * @param inKey the name of the value
   * @param inDefault the default list to use
   *
   * @return the list to set the value to
   */
  public List<String> use(String inKey, List<String> inDefault)
  {
    return use(inKey, inDefault, Optional.<Checker>absent());
  }

  /**
   * Use a list value to set an entry value, using some check for validity.
   *
   * @param inKey the name of the value
   * @param inDefault the default list
   * @param inChecker the validity checker, if any
   *
   * @return the list of strings to set the value to
   */
  public List<String> use(String inKey, List<String> inDefault,
                          Optional<Checker> inChecker)
  {
    Collection<String> values = get(inKey);
    if(values == null)
      return inDefault;

    List<String> result;
    if(inChecker.isPresent())
    {
      result = new ArrayList<>();
      for(String value : values)
        if(!value.isEmpty())
          if(!inChecker.get().check(value))
          {
            m_messages.add("Invalid value '" + value + "' for " + inKey);
            return inDefault;
          }
          else
            result.add(value);
    }
    else
      result = new ArrayList<>(values);

    if(inDefault.equals(values))
      return inDefault;

    m_changed = true;
    return result;
  }

  /**
   * Use a parametrized value to set an entry value.
   *
   * @param inKey the name of the value
   * @param inDefault the default value
   * @param inParser the parser to parser the value from a string
   * @param inParts the sub parts for the value
   * @param <T> the type of value to use
   *
   * @return the value to use for setting
   */
  public <T> T use(String inKey, T inDefault,
                   net.ixitxachitls.dma.values.Parser<T> inParser,
                   String ... inParts)
  {
    List<String []> values = listValues(inKey, inParts);
    if(values.size() == 0 || allEmpty(values.get(0)))
    {
      m_changed = true;
      return inDefault;
    }

    if(values.size() != 1
      || (values.get(0).length != inParts.length
         && inParts != null && inParts.length != 0
         && values.get(0).length != 1))
      throw new IllegalArgumentException("cannot properly parse '" + inKey
                                         + "' for " + Arrays.toString(inParts)
                                         + " with " + values);

    Optional<T> value = inParser.parse(values.get(0));
    if(!value.isPresent())
    {
      m_messages.add("Cannot properly parse " + inKey + " '"
                     + Arrays.toString(values.get(0)) + "'");
      return inDefault;
    }

    if(value.get().equals(inDefault))
      return inDefault;

     m_changed = true;
     return value.get();
  }

  /**
   * Use an optional value to set a value of an entry.
   *
   * @param inKey the name of the value
   * @param inDefault the default value to use, if any
   * @param inParser the parser for the value from a string
   * @param inParts the sub parts of the value, if any
   * @param <T> the type of value to set to
   *
   * @return the value to set the entry to, if any
   */
  public <T> Optional<T> use(String inKey, Optional<T> inDefault,
                             net.ixitxachitls.dma.values.Parser<T> inParser,
                             String ... inParts)
  {
    List<String []> values = listValues(inKey, inParts);
    if(values.size() == 0 || allEmpty(values.get(0)))
    {
      m_changed = inDefault.isPresent() ? true : m_changed;
      return Optional.absent();
    }

    if(values.size() != 1
      || (values.get(0).length != inParts.length
         && inParts != null && inParts.length != 0
         && values.get(0).length != 1))
      throw new IllegalArgumentException("cannot properly parse '" + inKey
                                         + "' for " + Arrays.toString(inParts)
                                         + " with " + values);

    Optional<T> value = inParser.parse(values.get(0));
    if(!value.isPresent())
    {
      m_messages.add("Cannot properly parse " + inKey + " '"
                     + Arrays.toString(values.get(0)) + "'");
      return inDefault;
    }

    if(value.equals(inDefault))
      return inDefault;

     m_changed = true;
     return value;
  }

  /**
   * Use the value to set a parametrized list value of an entry.
   *
   * @param inKey the value name
   * @param inDefault the default value
   * @param inParser the parser for the value
   * @param inParts the sub parts of the value
   * @param <T> the type of values in the list
   *
   * @return the list to use for setting the value
   */
  public <T> List<T> use(String inKey, List<T> inDefault,
                         net.ixitxachitls.dma.values.Parser<T> inParser,
                         String ... inParts)
  {
    List<String []> values = listValues(inKey, inParts);
    List<T> results = new ArrayList<>();
    for(String []single : values)
    {
      if(allEmpty(single))
        continue;

      Optional<T> value = inParser.parse(single);
      if(!value.isPresent())
      {
        m_messages.add("Cannot parse values for " + inKey + ": "
                       + Arrays.toString(single));
        return inDefault;
      }

      results.add(value.get());
    }

    if(inDefault.equals(results))
      return inDefault;

    m_changed = true;
    return results;
  }

  /**
   * Check whether all the strings given are empty.
   *
   * @param inValues the values to check
   * @return true if all values are empty, false if one is not
   */
  private static boolean allEmpty(String ... inValues)
  {
    for(String value : inValues)
      if(value != null && !value.isEmpty()
        && !"unknown".equalsIgnoreCase(value))
        return false;

    return true;
  }

  /**
   * Get the messages for parsing values.
   *
   * @return the messages encountered so far
   */
  public List<String> obtainMessages()
  {
    return m_messages;
  }

  /**
   * Get all the strings that can be used to set a value.
   *
   * @param inKey the name of the value to set
   *
   * @return all the strings setting the named value
   */
  private Collection<String> get(String inKey)
  {
    m_handled.add(inKey);
    Collection<String> values = m_values.get(inKey);
    if(values == null)
      m_messages.add("Tried to use unknown value " + inKey);

    List<String> cleanedValues = new ArrayList<>(values);
    for (int i = cleanedValues.size() - 1; i >= 0; i--)
    {
      if (cleanedValues.get(i) == null || cleanedValues.get(i).isEmpty())
        cleanedValues.remove(i);
      else
        break;
    }

    return cleanedValues;
  }

  /**
   * Get the first string that sets the named value.
   *
   * @param inKey the name of the value to set
   *
   * @return the first strings setting the value
   */
  private Optional<String> getFirst(String inKey)
  {
    Collection<String> values = get(inKey);
    if(values == null || values.isEmpty())
      return Optional.absent();

    if(values.size() > 1)
    {
      m_messages.add("Found multiple values for " + inKey
                         + ", expected single value.");
    }

    // Specially treat the empty case when setting a value from something to
    // empty.
    if(values.iterator().next().trim().isEmpty())
      return Optional.of("");

    return Optional.fromNullable(values.iterator().next());
  }

  /**
   * Get a list of all lists of values for setting the given value with the
   * given subparts.
   * @param inKey the name of the value that is being set
   * @param inParts the name of subvalues to look out for
   *
   * @return a list of all the subvalues (in the same order as the parts) for
   *         the list of values that are set
   */
  private List<String []> listValues(String inKey, String ... inParts)
  {
    List<String []> values = new ArrayList<>();
    if (inParts == null || inParts.length == 0)
      for(String value : get(inKey))
        values.add(new String [] { value });
    else
      // Convert from a -> [], b -> [] ...
      // to [a1, b1, ...], [a2, b2, ...]
      for(int i = 0; i < inParts.length; i++)
      {
        int j = 0;
        for(String value : get(inKey + "." + inParts[i]))
        {
          String []single;
          if (values.size() <= j)
          {
            single = new String[inParts.length];
            values.add(single);
          }
          else
            single = values.get(j);

          single[i] = value;
          j++;
        }
      }

    return values;
  }

  /**
   * Use the given value to set an entry integer value.
   *
   * @param inKey the name of the value
   * @param inDefault the default value
   *
   * @return the number to use to set the value, if any
   */
  public Optional<Integer> use(String inKey, Integer inDefault)
  {
    Optional<String> value = getFirst(inKey);
    if(!value.isPresent() || value.get().isEmpty())
      return Optional.of(inDefault);

    try
    {
      int intValue = Integer.parseInt(value.get());
      if(inDefault != null && intValue == inDefault)
        return Optional.of(inDefault);

      m_changed = true;
      return Optional.of(intValue);
    }
    catch(NumberFormatException e)
    {
      m_messages.add("Cannot parse number for " + inKey);
      return Optional.absent();
    }
  }

  /**
   * Get all the entries that set the named value.
   *
   * @param inKey the name of the value
   * @param inDefault the default list of entries to use
   * @param inCreator a creator function to create the nested entries from
   *                  strings
   * @param <E> the type of nested entries
   *
   * @return a list of all the nested entries set
   */
  public <E extends NestedEntry>
  List<E> useEntries(String inKey, List<E> inDefault,
                     NestedEntry.Creator<E> inCreator)
  {
    // create sub values list; we use insertion order, not the order of the
    // indexes, as indexes are nor properly ordered when entries are added on
    // the client
    String prefix = inKey + "@";
    Map<String, ListMultimap<String, String>> values = new LinkedHashMap<>();
    for (String key : m_values.keySet())
      if (key.startsWith(prefix))
      {
        String []sub = Strings.getPatterns(key, "(.*?)@(\\d+)\\.(.*)");
        if (sub.length != 3)
          continue;

        String id = sub[1];
        String subkey = sub[2];

        for (String value : m_values.get(key))
        {
          ListMultimap<String, String> subvalues = values.get(id);
          if(subvalues == null)
          {
            subvalues = ArrayListMultimap.create();
            values.put(id, subvalues);
          }

          subvalues.put(subkey, value);
        }
      }

    List<E> entries = new ArrayList<>();
    for (ListMultimap<String, String> submap : values.values())
    {
      E entry = inCreator.create();
      entry.set(new Values(submap));
      entries.add(entry);
    }

    if (inDefault.equals(entries))
      return inDefault;

    m_changed = true;
    return entries;
  }

  public void clear()
  {
    m_values.clear();
  }
}
