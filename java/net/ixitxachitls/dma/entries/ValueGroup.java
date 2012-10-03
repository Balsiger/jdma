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

package net.ixitxachitls.dma.entries;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.extensions.AbstractExtension;
import net.ixitxachitls.dma.entries.extensions.ExtensionVariable;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.values.ModifiedNumber;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.BaseCommand;
import net.ixitxachitls.util.Pair;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class groups a bunch of Values, its be base for all entries.
 *
 * @file          ValueGroup.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public abstract class ValueGroup implements Changeable
{
  //----------------------------------------------------------------- nested

  //----- Annotations ------------------------------------------------------

  /**
   * The annotations for variables.
   *
   * @param The key to use for this variable.
   *
   */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface Key {
    /** The name of the value. */
    String value();
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

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------ ValueGroup ------------------------------

  /**
   * Default constructor.
   *
   */
  protected ValueGroup()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

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
  protected static final @Nonnull Random s_random = new Random();

  /** All the variables for each individual derived class. */
  protected static final @Nonnull Map<Class, Variables> s_variables =
    new HashMap<Class, Variables>();

  /** An empty set of values for all unknown classes. */
  private static final @Nonnull Variables s_emptyVariables = new Variables();

  /** All the indexes. */
  protected static final @Nonnull Multimap<String, Index> s_indexes =
    ArrayListMultimap.create();

  // TODO: make this not static and move to campaign.
  /** The name of the current game. */
  public static final String CURRENT =
    Config.get("configuration", "default");

  /** The print for printing a whole page entry. */
  public static final Print s_pagePrint = new Print("$title");

  /** The print for printing an entry in a list. */
  public static final ListPrint s_listPrint =
    new ListPrint("1:L(icon);20:L(name)[Name]", "$label", null);

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getVariables ------------------------------

  /**
   * Get the variables possible for this group.
   *
   * @return      all the variables
   *
   */
  public @Nonnull Variables getVariables()
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
  public static @Nullable Variables getVariables(@Nonnull Class inClass)
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
  public @Nullable Variable getVariable(@Nonnull String inKey)
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
  public @Nullable Value getValue(@Nonnull String inKey)
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
   *
   */
  public abstract @Nonnull AbstractType<? extends AbstractEntry> getType();

  //........................................................................
  //----------------------------- getEditType ------------------------------

  /**
   * Get the type of the entry.
   *
   * @return      the requested name
   *
   */
  public abstract @Nonnull String getEditType();

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
  protected static @Nonnull String link
    (@Nonnull AbstractType<? extends AbstractEntry> inType,
     @Nonnull Index.Path inPath)
  {
    return "/" + inType.getMultipleLink() + "/" + inPath.getPath() + "/";
  }

  //........................................................................
  //-------------------------------- getKey --------------------------------

  /**
   * Get the key uniqueliy identifying this entry.
   *
   * @return   the key for the entry
   *
   */
  public @Nonnull AbstractEntry.EntryKey<? extends AbstractEntry> getKey()
  {
    throw new UnsupportedOperationException("must be derived");
  }

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
  public boolean matches(@Nonnull String inKey, @Nonnull String inValue)
  {
    Value value = getValue(inKey);
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
  @SuppressWarnings("unchecked")
  public boolean isValueIn(@Nonnull String inValue, @Nonnull String inKey)
  {
    Value value = getValue(inKey);
    if(value == null)
      return false;

    if(!(value instanceof ValueList))
    {
      Log.warning("must have a value list for in conditions for " + inValue
                  + " in " + inKey + ", not a " + value.getClass());
      return false;
    }

    for(Value v : (ValueList<Value>)value)
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
  @SuppressWarnings("unchecked")
  public @Nullable Boolean isValue(@Nonnull String inValue,
                                   @Nonnull String inKey)
  {
    Value value = getValue(inKey);
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
  public abstract @Nonnull String getName();

  //........................................................................
  //--------------------------------- getID --------------------------------

  /**
   * Get the ID of the entry. This can mainly be used for reference purposes.
   * In this case, the name is equal to the id, which is not true for entries.
   *
   * @return      the requested id
   *
   */
  public abstract @Nonnull String getID();

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
    (@Nonnull String inPath,
     @Nonnull AbstractType<? extends AbstractEntry> inType)
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

  //----------------------------- getPrint -----------------------------

  /**
   * Get the print for a full page.
   *
   * @return the print for page printing
   *
   */
  protected @Nonnull Print getPrint()
  {
    return getPagePrint();
  }

  //........................................................................
  //----------------------------- getPagePrint -----------------------------

  /**
   * Get the print for a full page.
   *
   * @return the print for page printing
   *
   */
  protected @Nonnull Print getPagePrint()
  {
    return s_pagePrint;
  }

  //........................................................................
  //----------------------------- getListPrint -----------------------------

  /**
   * Get the print for a list entry.
   *
   * @return the print for list entry
   *
   */
  protected @Nonnull ListPrint getListPrint()
  {
    return s_listPrint;
  }

  //........................................................................
  //----------------------------- getListFormat ----------------------------

  /**
   * Get the print for a list entry.
   *
   * @return the print for list entry
   *
   */
  public @Nonnull String getListFormat()
  {
    return getListPrint().getFormat();
  }

  //........................................................................
  //------------------------------- printPage ------------------------------

  /**
   * Print the entry into a command for adding to a document.
   *
   * @param       inUser  the user printing, if any
   *
   * @return      the command representing this item in a list
   *
   */
  public @Nonnull Object printPage(@Nullable BaseCharacter inUser)
  {
    return getPagePrint().print(this, inUser);
  }

  //........................................................................
  //--------------------------------- print --------------------------------

  /**
   * Print the entry into a command for adding to a document.
   *
   * @param       inUser  the user printing, if any
   *
   * @return      the command representing this item in a list
   *
   */
  public @Nonnull Object print(@Nullable BaseCharacter inUser)
  {
    return getPrint().print(this, inUser);
  }

  //........................................................................
  //------------------------------- printList ------------------------------

  /**
   * Print the entry into a command for adding to a document.
   *
   * @param       inKey   the key (name) for the entry to be printed (this is
   *                      used when printing entries multiple times with synonym
   *                      names)
   * @param       inUser  the user printing, if any
   *
   * @return      the command representing this item in a list
   *
   */
  public @Nonnull List<Object> printList(@Nonnull String inKey,
                                         @Nullable BaseCharacter inUser)
  {
    return getListPrint().print(inKey, this, inUser);
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
  public @Nullable Object compute(@Nonnull String inKey)
  {
    return getValue(inKey);
  }

  //........................................................................
  //----------------------------- computeValue -----------------------------

  /**
   * Format a value for printing.
   *
   * @param     inKey the key of the value to format
   * @param     inDM  true if formattign for dm, false if not
   *
   * @return    a value handle ready for printing
   *
   */
  public @Nullable ValueHandle computeValue(@Nonnull String inKey, boolean inDM)
  {
    // use _ to denote using a variable name
    if(inKey.startsWith("_"))
      return getVariable(inKey.substring(1));

    return getVariable(inKey);
  }

  //........................................................................
  //----------------------------- adjustValue ------------------------------

  /**
   * Adjust the value for the given name for any special properites.
   *
   * @param       inName        the name of the value to adjust
   * @param       ioCombination the combinstaion to adjust
   * @param       <V>           the real type of the values combined
   *
   */
  public <V extends Value> void
            adjustCombination(@Nonnull String inName,
                              Combination<V> ioCombination)
  {
    // nothing to do
  }

  //........................................................................
  //--------------------------- collectModifiers ---------------------------

  /**
   * collect the modifiers for the named value.
   *
   * @param       inName the name of the value to collect
   *
   * @return      a mapping between location and modifier found
   *
   */
  public Map<String, Modifier> collectModifiers(@Nonnull String inName)
  {
    Map<String, Modifier> modifiers = new HashMap<String, Modifier>();

    addModifiers(inName, modifiers);

    return modifiers;
  }

  //........................................................................

  //---------------------------- addModifiers ------------------------------

  /**
   * Add current modifiers to the given map.
   *
   * @param       inName        the name of the value to modify
   * @param       ioModifers    the map of modifiers
   *
   */
  public void addModifiers(@Nonnull String inName,
                           @Nonnull Map<String, Modifier> inModifiers)
  {
    // nothing to do
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

  //------------------------------- getCommand -----------------------------

//   /**
//    * Print the item to the document, in the general section.
//    *
//    * @param       inValues  all the values used for printing
//    * @param       inCommand the command template to use for printing
//    * @param       inDM      true if setting for dm, false if not
//    *
//    * @return      the command representing this item in a list
//    *
//    */
//   public Command getCommand(PrintCommand inValues, Command inCommand,
//                             boolean inDM)
//   {
//     return inCommand.transform(new ValueTransformer(inValues, inDM));
//   }

  //........................................................................

  //--------------------------- createValueLabel ---------------------------

  /**
   * Create the command for a label of a value.
   *
   * @param       inKey the key of the value to create the label for
   *
   * @return      a window command for the label
   *
   */
//   protected @Nonnull Window createValueLabel(@Nonnull String inKey)
//   {
//     return new Window(new Bold(Encodings.toWordUpperCase(inKey) + ":"),
//                       Config.get("resource:help/labels/" + inKey,
//                                  "please add " + inKey
//                                  + " to help/labels.config"));
//   }

  //........................................................................
  //--------------------- createHighlightedValueLabel ----------------------

  /**
   * Create the command for a label of a value.
   *
   * @param       inKey the key of the value to create the label for
   * @param       inID  the id for highlighting
   *
   * @return      a window command for the label
   *
   */
//  protected @Nonnull Window createHighlightedValueLabel(@Nonnull String inKey,
//                                                         @Nonnull String inID)
//   {
//    return new Window(new Bold(new ID("highlight-" + inID,
//                                     Encodings.toWordUpperCase(inKey) + ":")),
//                       Config.get("resource:help/labels/" + inKey,
//                                  "please add " + inKey
//                                  + " to help/labels.config"));
//   }

  //........................................................................
  //-------------------------- createValueCommand ---------------------------

  /**
   * Create the command for a value.
   *
   * @param       inValue    the value to add
   * @param       inKey      the key of the value added
   * @param       inEditable true if editable, false if not
   *
   * @return      the command to print the value
   *
   */
//   protected @Nonnull Command createValueCommand(@Nonnull Value inValue,
//                                                 @Nonnull String inKey,
//                                                 boolean inEditable)
//   {
//     return createValueCommand(inValue, inKey, null, null, inEditable);
//   }

  //........................................................................
  //-------------------------- createValueCommand --------------------------

  /**
   * Create the command for a value.
   *
   * @param       inValue    the value to add
   * @param       inKey      the key of the value added
   * @param       inType     the edit type of the value
   * @param       inScript   any script code to associate with editable
   * @param       inEditable true if the value can be edited, false if not
   *
   * @return      the command to print the value
   *
   */
//   public @Nonnull Command createValueCommand(@Nonnull Value inValue,
//                                              @Nonnull String inKey,
//                                     @Nullable String inType,
//                                              @Nullable String inScript,
//                                     boolean inEditable)
//   {
//   }

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
  protected boolean formatValues(@Nonnull StringBuilder inAppend,
                                 boolean inFirst,
                                 int inKeyWidth)
  {
    Variables variables = getVariables();

    for(Variable var : variables)
    {
      // if the variable is not stored, skip it
      if(!var.isStored())
        continue;

      if(!var.hasVariable(this))
        continue;

      Value value = var.get(this);

      // We don't store this if we don't have a value.
      if(value == null || (!value.isDefined() && !value.hasExpression()))
        continue;

      // add the delimiter
      if(inFirst)
        inFirst = false;
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

    return !inFirst;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- set ----------------------------------

  /**
   * Set the value for the given key.
   *
   * @param       inKey  the name of the key to set the value for
   * @param       inText the text to set the value to
   *
   * @return      the part of the string that could not be parsed
   *
   */
  public @Nullable String set(@Nonnull String inKey, @Nonnull String inText)
  {
    Variable variable = getVariable(inKey);

    if(variable == null)
      return inText;

    changed();
    return variable.setFromString(this, inText);
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
  protected boolean readVariable(@Nonnull ParseReader inReader,
                                 @Nonnull Variable inVariable)
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
  protected static @Nonnull
    List<Variable> extractClassVariables(@Nonnull Class inClass)
  {
    List<Variable> variables = new ArrayList<Variable>();

    // add all the annotated variables
    Field []fields = inClass.getDeclaredFields();

    for(Field field : fields)
    {
      Key key = field.getAnnotation(Key.class);

      if(key != null)
      {
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
            new ExtensionVariable(inClass,
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
    Class superClass = inClass.getSuperclass();

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
  protected static void extractVariables(@Nonnull Class inClass)
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
  protected static void extractVariables(@Nonnull Class inEntryClass,
                                         @Nonnull Class inExtensionClass)
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
  protected static void addIndex(@Nonnull Index inIndex)
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
  public static @Nonnull String constant(@Nonnull String inGroup,
                                         @Nonnull String inName)
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
  public static @Nonnull String constant(@Nonnull String inGroup,
                                         @Nonnull String inName,
                                         @Nonnull String inDefault)
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
  public static int constant(@Nonnull String inGroup, @Nonnull String inName,
                             int inDefault)
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
  public static boolean constant(@Nonnull String inGroup,
                                 @Nonnull String inName,
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
  @SuppressWarnings("unchecked") // need to cast for type
  public @Nullable <T extends Value>
                      T sum(@Nonnull String inKey,
                            @Nonnull List<? extends ValueGroup> inEntries)
  {
    T total = null;
    for(ValueGroup entry : inEntries)
    {
      T value = new Combination<T>(entry, inKey).total();
      if(value == null || !value.isDefined())
        continue;

      if(total == null)
        total = value;
      else
        total = (T)total.add(value);
    }

    return total;
  }

  //........................................................................
  //---------------------------- maybeAddValue -----------------------------

  /**
   * Add the value for the given key to the list if it is not null.
   *
   * @param     ioList    the list to add to
   * @param     inKey     the key of the value to add
   * @param     inDM      true if adding for DM, false if not
   * @param     inPrefix  the object to add before the value
   * @param     inPostfix the object to add after the value
   *
   */
  public @Nonnull void maybeAddValue(@Nonnull List<Object> ioList,
                                     @Nonnull String inKey, boolean inDM,
                                     @Nullable Object inPrefix,
                                     @Nullable Object inPostfix)
  {
    Object value = computeValue(inKey, inDM).format(this, inDM, true);
    if(value != null && !value.toString().isEmpty())
    {
      if(inPrefix != null)
        ioList.add(inPrefix);

      ioList.add(value);

      if(inPostfix != null)
        ioList.add(inPostfix);
    }
  }

  //........................................................................
  //------------------------------- convert --------------------------------

  /**
   * Compute the modified number for the name value.
   *
   * @param       the name of the value to compute
   * @param       the type of modifiers to ignore for the value
   *
   * @return      the modified number for the value
   *
   */
  public @Nonnull ModifiedNumber modified(@Nonnull String inName,
                                          Modifier.Type ... inIgnore)
  {
    Combination<Number> combination = new Combination<Number>(this, inName);

    ModifiedNumber number = new ModifiedNumber(combination.total().get());
    for(Map.Entry<String, Modifier> entry : collectModifiers(inName).entrySet())
    {
      Modifier modifier = entry.getValue().ignore(inIgnore);
      if(modifier != null)
        number.withModifier(modifier, entry.getKey());
    }

    return number;
  }

  //........................................................................


  //........................................................................

  //------------------------------------------------------------------- test

  /** The testing class. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //------------------------------ extract -------------------------------

    /**
     * Extract a specific (sub)command from the given command.
     *
     * @param       inCommand the command to extract from
     * @param       inIndexes the list of indexes to return; positive values
     *              denote normal arguments, numbered from 1, negative argument
     *              denote optional arguments, numbered from -1; 0 will
     *              return the name of the command itself
     *
     * @return      a string representation of the desired command or argument
     *
     */
    protected @Nonnull String extract(@Nonnull Object inCommand,
                                      int ... inIndexes)
    {
      return extract(inCommand, inIndexes, 0);
    }

    //......................................................................
    //------------------------------ extract -------------------------------

    /**
     * Extract a specific (sub)command from the given command. This is the
     * internal implementation for the recursion.
     *
     * @param       inCommand the command to extract from
     * @param       inIndexes the list of indexes to return; positive values
     *              denote normal arguments, numbered from 1, negative argument
     *              denote optional arguments, numbered from -1; 0 will
     *              return the name of the command itself
     * @param       inStart the index to the inIndexes to start with.
     *
     * @return      a string representation of the desired command or argument
     *
     */
    protected @Nonnull String extract(@Nonnull Object inCommand,
                                      int []inIndexes, int inStart)
    {
      if(inStart >= inIndexes.length)
        return inCommand.toString();

      int index = inIndexes[inStart];

      if(index == 0)
        if(inCommand instanceof BaseCommand)
          return ((BaseCommand)inCommand).getName();
        else
          return "expected command, found '" + inCommand + "'";

      if(index > 0)
        return extract(((BaseCommand)inCommand).getArguments().get(index - 1),
                       inIndexes, inStart + 1);

      return extract(((BaseCommand)inCommand).getOptionals().get(-index - 1),
                     inIndexes, inStart + 1);
    }

    //......................................................................

    /** A simple implementation of a value group for testing. */
    public static class TestGroup extends ValueGroup
    {
      /** The change state. */
      protected boolean m_changed = false;

      /** A simple value. */
      @Key("simple value")
      protected Value m_value = new Value.Test.TestValue();

      /** A value for dms only. */
      @Key(value = "dm value")
      @DM
      @Plural("dms value")
      @NoStore
      protected Value m_dmValue = new Value.Test.TestValue();

      /** A value for players only. */
      @Key("player value")
      @PlayerOnly
      protected Value m_playerValue = new Value.Test.TestValue();

      /** A player editable value. */
      @Key("player editable")
      @PlayerEdit
      protected Value m_playerEditableValue = new Value.Test.TestValue();

      /** Set the change state.
       *
       * @param inState the new state
       */
      @Override
      public void changed(boolean inState)
      {
        m_changed = inState;
      }

      /** Get the id of the group.
       *
       * @return the id
       */
      @Override
      public String getID()
      {
        return "Test-ID";
      }

      /** Get the name of the group.
       *
       * @return the name
       */
      @Override
      public String getName()
      {
        return "Test-Name";
      }

      /** Get the type of the group.
       *
       * @return the type
       */
      @SuppressWarnings("unchecked") // unchecked creation
      @Override
      public @Nonnull AbstractType<? extends AbstractEntry> getType()
      {
        return new BaseType(this.getClass());
      }

      /** Combine all base results.
       *
       * @param inName the name of the value
       * @param inDM true if formatting for the dm
       * @param inInline true to format the value inline
       * @return the combined value
       */
      // @Override
      // public @Nonnull Command combineBaseValues(@Nonnull String inName,
      //                                           boolean inDM,
      //                                           boolean inInline)
      // {
      //   throw new UnsupportedOperationException("not implemented");
      // }

      /**
       * Compute the maximal base value.
       *
       * @param       inName the name of the value to add up
       * @return      the maximal base value found
       */
      public @Nullable Pair<Value, BaseEntry>
        maximalBaseValue(@Nonnull String inName)
      {
        throw new UnsupportedOperationException("not implemented");
      }

      /**
       * Compute the minimal base value.
       *
       * @param       inName the name of the value to add up
       * @return      the minimal base value found
       */
      public @Nullable Pair<Value, BaseEntry>
        minimalBaseValue(@Nonnull String inName)
      {
        throw new UnsupportedOperationException("not implemented");
      }

      /**
       * Get the type of the entry.
       *
       * @return      the requested name
       */
      @Override
      public @Nonnull String getEditType()
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
      assertTrue("changed", group.m_changed);
      group.changed(false);
      assertFalse("changed", group.m_changed);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** The read Test. */
    @org.junit.Test
    public void read()
    {
      ValueGroup group = new TestGroup();
      ParseReader reader =
        new ParseReader(new java.io.StringReader("guru gugus"), "test");

      assertTrue("read",
                 group.readVariable(reader, group.getVariable("simple value")));
      assertEquals("value", "guru", group.getValue("simple value").toString());
      assertTrue("expect", reader.expect("gugus"));
    }

    //......................................................................
  }

  //........................................................................
}
