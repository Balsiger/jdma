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

package net.ixitxachitls.dma.entries;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.values.NewValue;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

/**
 * This class groups a bunch of Values, its be base for all entries.
 *
 * @file          ValueGroup.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public abstract class ValueGroup implements Changeable
{
  /**
   * The annotations for variables.
   *
   * @param The key to use for this variable.
   */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface Key {
    /** The name of the value. */
    String value();
  }

  /** The annotation for individual searchable fields. */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface Searchable {
    /** If the value is searchable in the data (i.e. stored seperately). */
    boolean value() default true;
  }

  /** The annotation for a DM only variable. */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface DM {
    /** Flag if the value is for dms only. */
    boolean value() default true;
  }

  /** The annotation for a variable printed even when undefined. */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface PrintUndefined {
    /** Flag if the value should be printed when undefined. */
    boolean value() default true;
  }

  /** The annotation for a not editable variable. */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface NoEdit {
    /** Flag if the value cannot be edit. */
    boolean value() default true;
  }

  /** The annotation for a player only variable. */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface PlayerOnly {
    /** Flag if the value is for players only. */
    boolean value() default true;
  }

  /** The annotation for a player editable value. */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface PlayerEdit {
    /** Flag if the value can be edited by a player. */
    boolean value() default true;
  }

  /** The plural form of the key. */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface Plural {
    /** Plural form of the key of this value. */
    String value();
  }

  /** The annotation for a value that is not stored. */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface NoStore {
    /** Flag denoting that the values is not stored. */
    boolean value() default true;
  }

  /** The annotation for a note for editing. */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface Note {
    /** A note for editing the value. */
    String value();
  }

  /** The annotation for a value that always includes base values. */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface WithBases {
    /** A note for editing the value. */
    boolean value() default true;
  }

  public static class Values
  {
    public interface Checker
    {
      public boolean check(String inCheck);
    }

    public interface Parser<T>
    {
      public T parse(String ... inValues);
    }

    public static final Checker NOT_EMPTY = new Checker()
    {
      @Override
      public boolean check(String inCheck)
      {
        return !inCheck.isEmpty();
      }
    };

    public Values(Multimap<String, String> inValues)
    {
      m_values = inValues;
    }

    private Multimap<String, String> m_values;
    private boolean m_changed = false;
    private List<String> m_messages = new ArrayList<>();
    private Set<String> m_handled = new HashSet<>();

    public boolean isChanged()
    {
      return m_changed;
    }

    public String use(String inKey, String inDefault)
    {
      return use(inKey, inDefault, null);
    }

    public Optional<String> use(String inKey, Optional<String> inDefault)
    {
      String value = getFirst(inKey);
      if(value == null)
        return inDefault;

      Optional<String> result = Optional.of(value);
      if(!inDefault.equals(result))
        m_changed = true;

      return result;
    }

    public String use(String inKey, String inDefault,
                      @Nullable Checker inChecker)
    {
      String value = getFirst(inKey);
      if(value == null)
        return inDefault;

      if(!inDefault.equals(value))
        m_changed = true;

      if(inChecker != null && !inChecker.check(value))
      {
        m_messages.add("Check for " + inKey + " failed, value not set.");
        return inDefault;
      }

      return value;
    }

    public List<String> use(String inKey, List<String> inDefault)
    {
      return use(inKey, inDefault, null);
    }

    public List<String> use(String inKey, List<String> inDefault,
                            @Nullable Checker inChecker)
    {
      Collection<String> values = get(inKey);
      if(values == null)
        return inDefault;

      List<String> result;
      if(inChecker != null)
      {
        result = new ArrayList<>();
        for(String value : values)
          if(!value.isEmpty())
            if(!inChecker.check(value))
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

    public <T> T use(String inKey, T inDefault, NewValue.Parser<T> inParser,
                     String ... inParts) {
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

    public <T> Optional<T> use(String inKey, Optional<T> inDefault,
                               NewValue.Parser<T> inParser,
                               String ... inParts) {
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

    public <T> List<T> use(String inKey, List<T> inDefault,
                           NewValue.Parser<T> inParser, String ... inParts)
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

    private boolean allEmpty(String ... inValues)
    {
      for(String value : inValues)
        if(value != null && !value.isEmpty()
          && !"unknown".equalsIgnoreCase(value))
          return false;

      return true;
    }

    public List<String> obtainMessages()
    {
      return m_messages;
    }

    private @Nullable Collection<String> get(String inKey)
    {
      m_handled.add(inKey);
      Collection<String> values = m_values.get(inKey);
      if(values == null)
        m_messages.add("Tried to use unknown value " + inKey);

      List<String> cleanedValues = new ArrayList<>(values);
      for (int i = cleanedValues.size() - 1; i >= 0; i--) {
        if (cleanedValues.get(i) == null || cleanedValues.get(i).isEmpty())
          cleanedValues.remove(i);
        else
          break;
      }

      return cleanedValues;
    }

    private @Nullable String getFirst(String inKey)
    {
      Collection<String> values = get(inKey);
      if(values == null || values.isEmpty())
        return null;

      if(values.size() != 1)
        m_messages.add("Found multiple values for " + inKey
                       + ", expected single value.");

      return values.iterator().next();
    }

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

    public @Nullable Integer use(String inKey, Integer inDefault)
    {
      String value = getFirst(inKey);
      if(value == null || value.isEmpty())
        return inDefault;

      try
      {
        int intValue = Integer.parseInt(value);
        if(inDefault != null && intValue == inDefault)
          return inDefault;

        m_changed = true;
        return intValue;
      }
      catch(NumberFormatException e)
      {
        m_messages.add("Cannot parse number for " + inKey);
        return null;
      }
    }

    public <E extends NestedEntry>
    List<E> useEntries(String inKey, List<E> inDefault,
                       NestedEntry.Creator<E> inCreator)
    {
      // create sub values list
      String prefix = inKey + ".";
      List<ListMultimap<String, String>> values = new ArrayList<>();
      for (String key : m_values.keySet())
        if (key.startsWith(prefix))
        {
          if (values.isEmpty())
            for (@SuppressWarnings("unused") String value : m_values.get(key))
              values.add(ArrayListMultimap.<String, String>create());

          String subkey = key.substring(prefix.length());
          int i = 0;
          for (String value : m_values.get(key))
            values.get(i++).put(subkey, value);
        }

      List<E> entries = new ArrayList<E>();
      for (ListMultimap<String, String> submap : values)
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
  }


  /**
   * Default constructor.
   */
  protected ValueGroup()
  {
    // nothing to do
  }

  /** The delimiter between entries. */
  protected static final char s_delimiter =
    Config.get("resource:entries/delimiter", '.');

  /** The delimiter between individual values. */

  protected static final char s_keyDelimiter =
    Config.get("resource:entries/key.delimiter", ';');

  /** The delimiter for lists. */

  protected static final char s_listDelimiter =
    Config.get("resource:entries/list.delimiter", ',');

  /** The keyword indent to use. */

  protected static final int s_keyIndent =
    Config.get("resource:entries/key.indent", 2);

  /** The random generator. */
  protected static final Random s_random = new Random();

  /** All the variables for each individual derived class. */
  protected static final Map<Class<?>, Variables> s_variables =
    new HashMap<Class<?>, Variables>();

  /** An empty set of values for all unknown classes. */
  private static final Variables s_emptyVariables = new Variables();

  /** All the indexes. */
  protected static final Multimap<String, Index> s_indexes =
    ArrayListMultimap.create();

  // TODO: make this not static and move to campaign.
  /** The name of the current game. */

  public static final String CURRENT =
    Config.get("configuration", "default");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getVariables ------------------------------


  /**
   * Get the variables possible for this group.
   *
   * @return      all the variables
   *
   */
  public Variables getVariables()
  {
    Variables result = getVariables(this.getClass());

    if(result == null)
      return s_emptyVariables;

    return result;
  }

  //........................................................................
  //----------------------------- getVariables ------------------------------

  /**
   * Get the variables possible for the given class group.
   *
   * @param       inClass the class go get the variables for
   *
   * @return      all the variables
   *
   */
  public static @Nullable Variables getVariables(Class<?> inClass)
  {
    return s_variables.get(inClass);
  }

  //........................................................................
  //----------------------------- getVariable -----------------------------

  /**
   * Get the variable for the given key.
   *
   * @param       inKey the name of the key to get the value for
   *
   * @return      the value for the key
   *
   */
  public @Nullable Variable getVariable(String inKey)
  {
    return getVariables().getVariable(inKey);
  }

  //........................................................................
  //------------------------------ getValue --------------------------------

  /**
   * Get the value for the given key.
   *
   * @param       inKey the name of the key to get the value for
   *
   * @return      the value for the key
   *
   */
  public @Nullable Value<?> getValue(String inKey)
  {
    Variable var = getVariable(inKey);

    if(var == null)
      return null;

    return var.get(this);
  }

  //........................................................................
  //------------------------------- getType --------------------------------

  /**
   * Get the type of the entry.
   *
   * @return      the requested name
   * @param       <T> the type of the entry the type is for
   *
   */
  public abstract AbstractType<?> getType();

  //........................................................................
  //----------------------------- getEditType ------------------------------

  /**
   * Get the type of the entry.
   *
   * @return      the requested name
   *
   */
  public abstract String getEditType();

  //........................................................................
  //--------------------------------- link ---------------------------------

  /**
   * Create a link for the entry to the given index path.
   *
   * @param    inType the type to link for
   * @param    inPath the path to the index
   *
   * @return   a string for linking to the path
   *
   */
  protected static String link
    (AbstractType<? extends AbstractEntry> inType, Index.Path inPath)
  {
    return "/" + inType.getMultipleLink() + "/" + inPath.getPath() + "/";
  }

  //........................................................................
  //-------------------------------- getKey --------------------------------

  /**
   * Get the key uniquely identifying this entry.
   *
   * @param    <T> the type of entry getting the key for
   *
   * @return   the key for the entry
   *
   */
  public EntryKey getKey()
  {
    throw new UnsupportedOperationException("must be derived");
  }

  //........................................................................
  //------------------------------- getEntry -------------------------------

  /**
   * Get the entry associated with this group.
   *
   * @return  the associated entry
   */
  public abstract AbstractEntry getEntry();

  //........................................................................

  //-------------------------------- isBase --------------------------------

  /**
   * Check if the current entry represents a base entry or not.
   *
   * @return      true if this is a base entry, false else
   *
   */
  public boolean isBase()
  {
    return false;
  }

  //........................................................................

  /**
   * Check whether the given user is the DM for this entry.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   */
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    return false;
  }

  //-------------------------------- isOwner -------------------------------

  /**
   * Check whether the given user is the owner of this entry.
   *
   * @param       inUser the user accessing
   *
   * @return      true for owner, false for not
   *
   */
  public boolean isOwner(@Nullable BaseCharacter inUser)
  {
    if(inUser == null)
      return false;

    // Admins are owners of everything
    return inUser.hasAccess(BaseCharacter.Group.ADMIN);
  }

  //........................................................................
  //------------------------------- matches --------------------------------

  /**
   * Check whether the entry matches the given key and value.
   *
   * @param       inKey   the key of the value to match
   * @param       inValue the value to match with
   *
   * @return      true if the group matches the given key and value, false if
   *              not
   *
   */
  public boolean matches(String inKey, String inValue)
  {
    Value<?> value = getValue(inKey);
    if(value == null)
      return false;

    return inValue.equalsIgnoreCase(value.toString());
  }

  //........................................................................
  //------------------------------ isValueIn -------------------------------

  /**
   * Check if the given value is in the group value with the given key.
   *
   * @param       inValue the value to look for
   * @param       inKey   the key of the value to check in
   *
   * @return      true if it is in, false if it is not
   *
   */
  @SuppressWarnings({ "rawtypes" })
  public boolean isValueIn(String inValue, String inKey)
  {
    Value<?> value = getValue(inKey);
    if(value == null)
      return false;

    if(!(value instanceof ValueList))
    {
      Log.warning("must have a value list for in conditions for " + inValue
                  + " in " + inKey + ", not a " + value.getClass());
      return false;
    }

    for(Object v : (ValueList)value)
      if(inValue.equalsIgnoreCase(v.toString()))
        return true;

    return false;
  }

  //........................................................................
  //------------------------------ isValue -------------------------------

  /**
   * Check if the given value has the value given.
   *
   * @param       inValue the value to look for
   * @param       inKey   the key of the value to check in
   *
   * @return      true if it is in, false if it is not, null if undefined or
   *              invalid
   *
   */
  public @Nullable Boolean isValue(String inValue, String inKey)
  {
    Value<?> value = getValue(inKey);
    if(value == null || !value.isDefined())
      return null;

    return inValue.equalsIgnoreCase(value.toString());
  }

  //........................................................................

  //-------------------------------- getKey --------------------------------

  /**
   * Get the key for a given field.
   *
   * @param       inField the field to get the key for
   *
   * @return      the key of the field or null if none
   *
   */
//   protected static String getKey(Field inField)
//   {
//     Key key = inField.getAnnotation(Key.class);

//     if(key != null)
//       return key.value();

//     return null;
//   }

  //........................................................................
  //------------------------------- getName --------------------------------

  /**
    * Get the name of the group.
    *
    * @return      the name
    */
  public abstract String getName();

  //........................................................................
  //--------------------------------- getID --------------------------------

  /**
   * Get the ID of the entry. This can mainly be used for reference purposes.
   * In this case, the name is equal to the id, which is not true for entries.
   *
   * @return      the requested id
   *
   */
  public abstract String getID();

  //........................................................................
  //------------------------------- getIndex -------------------------------

  /**
   * Get all the indexes.
   *
   * @param       inPath the path to the index to get
   * @param       inType the type of the entries in the index
   *
   * @return      the index found or null if not found
   *
   */
  public static @Nullable Index getIndex
    (String inPath, AbstractType<? extends AbstractEntry> inType)
  {
    if(s_indexes.get(inPath) == null)
      return null;

    for(Index index : s_indexes.get(inPath))
      if(index.getType() == inType)
        return index;

    return null;
  }

  //........................................................................
  //------------------------------ getIndexes ------------------------------

  /**
   * Get all the registered indexes.
   *
   * @return      all the registered indexes
   *
   */
  public static Collection<Index> getIndexes()
  {
    return s_indexes.values();
  }

  //........................................................................

  //------------------------------- compute --------------------------------

  /**
   * Compute a value for a given key, taking base entries into account if
   * available.
   *
   * @param    inKey the key of the value to compute
   *
   * @return   the computed value
   *
   */
  public @Nullable Object compute(String inKey)
  {
    return getValue(inKey);
  }

  //........................................................................

  //---------------------------- getBaseEntries ----------------------------

  /**
   * Get the base entries this abstract entry is based on, if any.
   *
   * @return      the requested base entries
   *
   */
  public List<BaseEntry> getBaseEntries()
  {
    return new ArrayList<>();
  }

  //........................................................................
  //------------------------------- collect --------------------------------

  //........................................................................

  /**
   * Get all the values for all the indexes.
   *
   * @return      a multi map of values per index name
   */
  public Multimap<Index.Path, String> computeIndexValues()
  {
    return HashMultimap.create();
  }

  /**
   * Create a proto representing the entry.
   *
   * @return the proto representation
   */
  public abstract Message toProto();

  /**
   * Read the values for the entry from the given proto.
   *
   * @param   inProto  the proto buffer with all the data
   */
  public abstract void fromProto(Message inProto);

  public void set(Values inValues)
  {
    // no values here
  }

  //........................................................................
  //------------------------------- addIndex -------------------------------

  /**
   * Add an index for the given class.
   *
   * @param    inIndex the index to add
   *
   */
  protected static void addIndex(Index inIndex)
  {
    s_indexes.put(inIndex.getPath(), inIndex);
  }

  //........................................................................

  //------------------------------- changed --------------------------------

  /**
    * Set the state of the file to changed.
    *
    */
  @Override
  public void changed()
  {
    changed(true);
  }

  //........................................................................
  //------------------------------- changed --------------------------------

  /**
    * Set the state of the file to changed.
    *
    * @param       inChanged the value to set to, true for changed (dirty),
    *                        false for unchanged (clean)
    *
    */
  public abstract void changed(boolean inChanged);

  //........................................................................
  //-------------------------- completeVariables ---------------------------

  /**
   * Complete all the variables of this group with the given group.
   *
   * @param       inBases the base groups to copy values from
   *
   */
//   protected void completeVariables
//     (@MayBeNull List<? extends ValueGroup> inBases)
//   {
//     if(inBases == null)
//       return;

//     for(Variable variable : getValues())
//     {
//       Value value = variable.getValue(this);
//       boolean defined = value.hasValue();
//       for(ValueGroup base : inBases)
//       {
//         if(base == null)
//           continue;

//         Value baseValue = base.getValue(variable.getKey());

//         if(value instanceof Modifiable)
//         {
//           Modifiable mValue = (Modifiable)value;

//           if(baseValue instanceof Modifiable)
//           {
//             for(BaseModifier<?> modifier :
//                   ((Modifiable<?>)baseValue).modifiers())
//               mValue.addModifier(modifier);

//             baseValue = ((Modifiable)baseValue).getBaseValue();
//           }

//           if(baseValue != null && baseValue.isDefined())
//             mValue.addModifier(new ValueModifier<Value>
//                                (ValueModifier.Operation.ADD, baseValue,
//                                 ValueModifier.Type.GENERAL, base.getName()));
//         }
//         else
//         {
//           if(baseValue != null && baseValue.isDefined())
//           {
//             if(!defined)
//             {
//               value.complete(baseValue, !defined);
//               value.setStored(false);
//               defined = value.isDefined();
//             }
//           }
//         }
//       }
//     }
//   }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- constant -------------------------------

  /**
   * Get a string constant for an enumeration type from the configuration.
   *
   * @param       inGroup the group the constant is in
   * @param       inName  the name of the configuration value
   *
   * @return      the value as it is in the configuration
   *
   */
  public static String constant(String inGroup, String inName)
  {
    return constant(inGroup, inName, inName);
  }

  //........................................................................
  //------------------------------- constant -------------------------------

  /**
   * Get an string constant for an enumeration type from the configuration.
   *
   * @param       inGroup   the group the constant is in
   * @param       inName    the name of the configuration value
   * @param       inDefault the default value to return if none found
   *
   * @return      the value as it is in the configuration
   *
   */
  public static String constant(String inGroup, String inName, String inDefault)
  {
    return Config.get("resource:" + CURRENT + "/" + inGroup + "."
                      + inName, inDefault);
  }

  //........................................................................
  //------------------------------- constant -------------------------------

  /**
   * Get an integer constant for an enumeration type from the configuration.
   *
   * @param       inGroup  the group the constant is in
   * @param       inName   the name of the configuration value
   * @param       inDefault the default value to use if none is given
   *
   * @return      the value as it is in the configuration
   *
   */
  public static int constant(String inGroup, String inName, int inDefault)
  {
    return Config.get("resource:" + CURRENT + "/" + inGroup + "."
                      + inName, inDefault);
  }

  //........................................................................
  //------------------------------- constant -------------------------------

  /**
   * Get a boolean constant for an enumeration type from the configuration.
   *
   * @param       inGroup   the group the constant is in
   * @param       inName    the name of the configuration value
   * @param       inDefault the default value to use if none is given
   *
   * @return      the value as it is in the configuration
   *
   */
  public static boolean constant(String inGroup, String inName,
                                 boolean inDefault)
  {
    return Config.get("resource:" + CURRENT + "/" + inGroup + "."
                      + inName, inDefault);
  }

  //........................................................................
}
