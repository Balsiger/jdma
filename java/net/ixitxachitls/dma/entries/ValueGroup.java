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
import java.lang.reflect.Field;
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
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.extensions.AbstractExtension;
import net.ixitxachitls.dma.entries.extensions.ExtensionVariable;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.NewValue;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.Pair;
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
        if(value != null && !value.isEmpty())
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

      return values;
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
        for(int i = 0; i < inParts.length; i++)
        {
          int j = 0;
          for(String value : get(inKey + "." + inParts[i]))
          {
            String []single;
            if(i == 0)
            {
              single = new String[inParts.length];
              values.add(single);
            }
            else
              single = values.get(j++);

            single[i] = value;
          }
        }

      return values;
    }

    public @Nullable Integer use(String inKey, Integer inDefault)
    {
      String value = getFirst(inKey);
      if(value == null)
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
  public abstract <T extends AbstractEntry> AbstractType<T> getType();

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
   * Get the key uniqueliy identifying this entry.
   *
   * @param    <T> the type of entry getting the key for
   *
   * @return   the key for the entry
   *
   */
  public <T extends AbstractEntry> AbstractEntry.EntryKey<T> getKey()
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
  //--------------------------------- isDM ---------------------------------

  /**
   * Check whether the given user is the DM for this entry.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   *
   */
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    return false;
  }

  //........................................................................
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
    return new ArrayList<BaseEntry>();
  }

  //........................................................................
  //------------------------------- collect --------------------------------

  /**
   * Collect the named value from all possible sources.
   *
   * @param   inName     the name of the value to collect
   * @param   ioCombined the combined value to collect into (can be changed)
   * @param   <T>        the type of value being collected
   */
  protected <T extends Value<T>> void collect(String inName,
                                              Combined<T> ioCombined)
  {
    // nothing to do here
  }

  //........................................................................

  //----------------------------- formatValues -----------------------------

  /**
   * Format all the values contained in the entry for storing.
   *
   * @param       inAppend   the buffer to append the values to
   * @param       inFirst    flag if the printed values will be the first or
   *                         not
   * @param       inKeyWidth the width of the keys to use
   *
   * @return      true if at least one value was printed, false else
   *
   */
  protected boolean formatValues(StringBuilder inAppend, boolean inFirst,
                                 int inKeyWidth)
  {
    Variables variables = getVariables();

    boolean first = inFirst;
    for(Variable var : variables)
    {
      // if the variable is not stored, skip it
      if(!var.isStored())
        continue;

      if(!var.hasVariable(this))
        continue;

      Value<?> value = var.get(this);

      // We don't store this if we don't have a value.
      if(value == null || (!value.isDefined() && !value.hasExpression()))
        continue;

      // add the delimiter
      if(first)
        first = false;
      else
      {
        inAppend.append(s_keyDelimiter);
        inAppend.append('\n');
      }

      inAppend.append(Strings.spaces(s_keyIndent));
      inAppend.append(variables.getPrefix());
      inAppend.append(var.getKey());
      inAppend.append(Strings.spaces
                      (Math.max(1, inKeyWidth - var.getKey().length()
                                - variables.getPrefix().length())));

      // now append the value of the variable
      // TODO: readd this handling when Text is in
//       if(value instanceof Text && ((Text)value).keepsFormatting())
//       {
//         inAppend.append("\n  ");
//         inAppend.append(value.toString());
//       }
//       else
        inAppend.append(value.toString().replaceAll
                        ("\\s*\n\\s*",
                         "\n" + Strings.spaces(inKeyWidth + s_keyIndent)));
    }

    return !first;
  }

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

  /**
   * Set the value for the given key.
   *
   * @param       inKey  the name of the key to set the value for
   * @param       inText the text to set the value to
   *
   * @return      the part of the string that could not be parsed
   */
  public @Nullable String set(String inKey, String inText)
  {
    Variable variable = getVariable(inKey);

    if(variable == null)
    {
      Log.warning("trying to set undefined variable " + inKey + " with "
                  + inText + " in " + getName() + " [" + getClass() + "]");
      return inText;
    }

    changed();
    return variable.setFromString(this, inText);
  }

  public void set(Values inValues)
  {
    // no values here
  }

  //........................................................................
  //----------------------------- readVariable -----------------------------

  /**
   * Read a value, and only one value, from the reader into the object.
   *
   * @param       inReader   the reader to read from
   * @param       inVariable the variable to read into
   *
   * @return      true if read, faluse if not
   *
   */
  protected boolean readVariable(ParseReader inReader, Variable inVariable)
  {
    return inVariable.read(this, inReader);
  }

  //........................................................................

  //------------------------ extractClassVariables -------------------------

  /**
   * Extract the variables from the given class.
   *
   * @param     inClass the class from which to extract
   *
   * @return    all the extracted variables
   *
   */
  @SuppressWarnings("unchecked") // casting class for variable creation
  protected static List<Variable>extractClassVariables(Class<?> inClass)
  {
    List<Variable> variables = new ArrayList<Variable>();

    // add all the annotated variables
    Field []fields = inClass.getDeclaredFields();

    for(Field field : fields)
    {
      Key key = field.getAnnotation(Key.class);

      if(key != null)
      {
        Searchable searchable = field.getAnnotation(Searchable.class);
        DM dm = field.getAnnotation(DM.class);
        NoEdit noEdit = field.getAnnotation(NoEdit.class);
        PlayerOnly player = field.getAnnotation(PlayerOnly.class);
        PlayerEdit playerEdit = field.getAnnotation(PlayerEdit.class);
        Plural plural = field.getAnnotation(Plural.class);
        NoStore noStore = field.getAnnotation(NoStore.class);
        Note note = field.getAnnotation(Note.class);
        PrintUndefined printUndefined =
          field.getAnnotation(PrintUndefined.class);
        WithBases withBases = field.getAnnotation(WithBases.class);

        // we have to use a variable class in the package of extensions to be
        // able to access extension variables.
        Variable variable;
        if(AbstractExtension.class.isAssignableFrom(inClass))
          variable =
            new ExtensionVariable((Class<? extends AbstractExtension<?>>)
                                  inClass,
                                  key.value(), field,
                                  noStore == null || !noStore.value(),
                                  printUndefined == null
                                  ? false : printUndefined.value());
        else
          variable = new Variable(key.value(), field,
                                  noStore == null || !noStore.value(),
                                  printUndefined == null
                                  ? false : printUndefined.value());
        variables.add(variable
                      .withSearchable(searchable != null && searchable.value())
                      .withDM(dm != null && dm.value())
                      .withPlayerOnly(player != null && player.value())
                      .withPlayerEditable(playerEdit != null
                                          && playerEdit.value())
                      .withPlural(plural == null ? null : plural.value())
                      .withNote(note == null ? null : note.value())
                      .withEditable(noEdit == null || !noEdit.value())
                      .withBases(withBases == null
                                 ? false : withBases.value()));
      }
    }

    // add all the variables of the parent class, if any
    Class<?> superClass = inClass.getSuperclass();

    if(superClass != null)
    {
      // In case the super class was not yet extracted, we have to do that
      // before trying to add the base variables.
      if(s_variables.get(superClass) == null)
        extractVariables(superClass);

      for(Variable v : s_variables.get(superClass))
        variables.add(v);
    }

    return variables;
  }

  //........................................................................
  //---------------------------- extractVariables ---------------------------

  /**
   * Extract all the keyed variables from the given class.
   *
   * @param       inClass the class to extract from
   *
   */
  protected static void extractVariables(Class<?> inClass)
  {
    if(s_variables.containsKey(inClass))
      return;

    s_variables.put(inClass, new Variables(extractClassVariables(inClass)));
  }

  //........................................................................
  //---------------------------- extractVariables ---------------------------

  /**
   * Extract all the keyed variables from the given class.
   *
   * @param       inEntryClass     the entry class to extract for
   * @param       inExtensionClass the extension class to extract from
   *
   */
  protected static void
    extractVariables(Class<?> inEntryClass,
                     Class<? extends AbstractExtension<?>> inExtensionClass)
  {
    Variables variables = s_variables.get(inEntryClass);

    if(variables == null)
    {
      Log.warning("cannot extract variables for " + inExtensionClass
                  + " as the variables for " + inEntryClass
                  + " were not found");
      return;
    }

    List<Variable> vars = extractClassVariables(inExtensionClass);
    variables.add(vars);

    if(!s_variables.containsKey(inExtensionClass))
      s_variables.put(inExtensionClass, new Variables(vars));
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

  //--------------------------------- sum ----------------------------------

  /**
   * Sum up all the values with the given key from all the given entries.
   *
   * @param   inKey     the key of the value to sum
   * @param   inEntries the entries to sum over
   * @param   <T>       the type of value being summed
   *
   * @return  the sum of all the value or null if no values found
   *
   */
  public @Nullable <T extends Value<T>>
                      T sum(String inKey, List<? extends ValueGroup> inEntries)
  {
    T total = null;
    for(ValueGroup entry : inEntries)
    {
      Combined<T> combined = ((AbstractEntry)entry).collect(inKey);
      T value = combined.total();
      if(value == null || !value.isDefined())
        continue;

      if(total == null)
        total = value;
      else
        total = total.add(value);
    }

    return total;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The testing class. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** A simple implementation of a value group for testing. */
    public static class TestGroup extends AbstractEntry
    {
      /** The serial version id. */
      private static final long serialVersionUID = 1L;

      /** The change state. */
      protected boolean m_testChanged = false;

      /** A simple value. */
      @Key("simple value")
      protected Value<?> m_value = new Value.Test.TestValue();

      /** A value for dms only. */
      @Key(value = "dm value")
      @DM
      @Plural("dms value")
      @NoStore
      protected Value<?> m_dmValue = new Value.Test.TestValue();

      /** A value for players only. */
      @Key("player value")
      @PlayerOnly
      protected Value<?> m_playerValue = new Value.Test.TestValue();

      /** A player editable value. */
      @Key("player editable")
      @PlayerEdit
      protected Value<?> m_playerEditableValue = new Value.Test.TestValue();

      /** Set the change state.
       *
       * @param inState the new state
       */
      @Override
      public void changed(boolean inState)
      {
        m_testChanged = inState;
      }

      /**
       * Get the id of the group.
       *
       * @return the id
       */
      @Override
      @Deprecated
      public String getID()
      {
        return "Test-ID";
      }

      /**
       * Get the name of the group.
       *
       * @return the name
       */
      @Override
      public String getName()
      {
        return "Test-Name";
      }

      /**
       * Get the type of the group.
       *
       * @param  <T> the type of entry
       * @return the type
       */
      @SuppressWarnings("unchecked") // unchecked creation
      @Override
      public <T extends AbstractEntry> AbstractType<T> getType()
      {
        return new AbstractType<T>((Class<T>)this.getClass());
      }

      /**
       * Compute the maximal base value.
       *
       * @param       inName the name of the value to add up
       * @return      the maximal base value found
       */
      public @Nullable Pair<Value<?>, BaseEntry>
        maximalBaseValue(String inName)
      {
        throw new UnsupportedOperationException("not implemented");
      }

      /**
       * Compute the minimal base value.
       *
       * @param       inName the name of the value to add up
       * @return      the minimal base value found
       */
      public @Nullable Pair<Value<?>, BaseEntry>
        minimalBaseValue(String inName)
      {
        throw new UnsupportedOperationException("not implemented");
      }

      /**
       * Get the type of the entry.
       *
       * @return      the requested name
       */
      @Override
      public String getEditType()
      {
        return "dummy";
      }
    }

    /** Simple test setup. */
    @org.junit.BeforeClass
    public static void setUpClass()
    {
      extractVariables(TestGroup.class);
    }

    //----- variables ------------------------------------------------------

    /** The variables Test. */
    @org.junit.Test
    public void variables()
    {
      Variables variables = s_variables.get(TestGroup.class);
      assertTrue("variables", variables != null);
      assertTrue("simple value", variables.getVariable("simple value") != null);
      assertNull("invalid", variables.getVariable("invalid"));
      assertFalse("stored", variables.getVariable("dm value").isStored());
      assertTrue("stored", variables.getVariable("simple value").isStored());
      assertEquals("plural", "dms value",
                   variables.getVariable("dm value").getPluralKey());
      assertEquals("plural", "player values",
                   variables.getVariable("player value").getPluralKey());
      assertTrue("dm", variables.getVariable("dm value").isDMOnly());
      assertFalse("dm", variables.getVariable("player value").isDMOnly());
      assertFalse("player", variables.getVariable("dm value").isPlayerOnly());
      assertTrue("player",
                 variables.getVariable("player value").isPlayerOnly());
      assertTrue("player editable",
                 variables.getVariable("player editable").isPlayerEditable());
      assertFalse("editable",
                  variables.getVariable("player value").isPlayerEditable());
    }

    //......................................................................
    //----- formatting -----------------------------------------------------

    /** The formatting Test. */
    @org.junit.Test
    public void formatting()
    {
      ValueGroup group = new TestGroup();

      StringBuilder builder = new StringBuilder();
      assertFalse("format", group.formatValues(builder, true, 5));
      assertEquals("format", "", builder.toString());

      group.set("simple value", "guru");
      group.set("dm value", "guru");
      group.set("player value", "guru");

      assertTrue("format", group.formatValues(builder, true, 13));
      assertEquals("format",
                   "  simple value guru;\n"
                   + "  player value guru",
                   builder.toString());
    }

    //......................................................................
    //----- values ---------------------------------------------------------

    /** The values Test. */
    @org.junit.Test
    public void values()
    {
      TestGroup group = new TestGroup();
      assertNull("set", group.set("simple value", "guru"));
      assertNull("set", group.set("dm value", "guru"));
      assertNull("set", group.set("player value", "guru"));

      assertEquals("id", "Test-ID", group.getID());
      assertEquals("name", "Test-Name", group.getName());
      assertFalse("base", group.isBase());
      assertEquals("value", "guru", group.getValue("simple value").toString());
      assertNull("value", group.getValue("invalid"));
      assertTrue("changed", group.m_testChanged);
      group.changed(false);
      assertFalse("changed", group.m_testChanged);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** The read Test. */
    @org.junit.Test
    public void read()
    {
      ValueGroup group = new TestGroup();
      try (ParseReader reader =
        new ParseReader(new java.io.StringReader("guru gugus"), "test"))
      {
        assertTrue("read",
                   group.readVariable(reader,
                                      group.getVariable("simple value")));
        assertEquals("value", "guru",
                     group.getValue("simple value").toString());
        assertTrue("expect", reader.expect("gugus"));
      }
    }

    //......................................................................
  }

  //........................................................................
}
