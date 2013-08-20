/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.entries.extensions;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.entries.Variables;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the basic attachment for all the entries.
 *
 * @file          AbstractExtension.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @param         <T> the type of entry this extension is associated with
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public abstract class AbstractExtension<T extends AbstractEntry>
  extends ValueGroup implements Serializable
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------- AbstractExtension --------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor.
   *
   * @param       inEntry  the entry attached to
   * @param       inName   the name of the extension
   *
   */
  protected AbstractExtension(T inEntry, String inName)
  {
    m_name = inName;
    m_entry = inEntry;
  }

  //........................................................................
  //-------------------------- AbstractExtension --------------------------

  /**
   * Constructor with all the values.
   *
   * @param       inEntry  the entry attached to
   * @param       inTag    the tag for this extension
   * @param       inName   the name for this extension
   *
   */
  // public AbstractExtension(T inEntry, String inTag, String inName)
  // {
  // this(inEntry, inName);
  //   m_tag   = inTag;
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The name of the extension. */
  protected String m_name;

  /** The tag of the extension, if any. */
  // protected String m_tag  = null;

  /** The entry this extension is associated with. */
  protected T m_entry;

  /** A flag denoting if the extension is complete or not. */
  // protected boolean m_complete = false;

  /** The directory for extension icons (inside the icon directory). */
  protected static final String s_extensionDir =
    Config.get("resource:html/dir.extensions", "extensions");

  /** All the possible auto extensions for each class (if any). */
  protected static final Map<Class<? extends AbstractExtension<?>>, String []>
    s_autoExtensions =
    new HashMap<Class<? extends AbstractExtension<?>>, String []>();

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getVariables ------------------------------

  /**
   * Get the values possible for this group. This version also handles tags,
   * if they are present.
   *
   * @return      all the variables
   *
   */
  @Override
  public Variables getVariables()
  {
    // if(m_tag == null)
      return super.getVariables();

    // return TaggedValues.tag(super.getValues(), m_tag);
  }

  //........................................................................
  //------------------------------- getValue -------------------------------

  /**
   * Get a value given as a field. We have to copy the method from ValueGroup
   * to give ValueGroups access to protected fields of this class.
   *
   * @param       inField the field for which to get the value
   *
   * @return      the value the field has in this object, if any
   *
   */
  public @Nullable Value<?> getValue(Field inField)
  {
    if(inField == null)
      throw new IllegalArgumentException("must have a field here");

    try
    {
      return (Value)inField.get(this);
    }
    catch(java.lang.IllegalAccessException e)
    {
      throw new UnsupportedOperationException
        ("Cannot access field " + inField.getName(), e);
    }
  }

  //........................................................................
  //------------------------------- getType --------------------------------

  /**
   * Get the type of the entry.
   *
   * @param       <S> the type of entry to get the type for
   * @return      the requested name
   *
   */
  @Override
  public <S extends AbstractEntry> AbstractType<S> getType()
  {
    return m_entry.getType();
  }

  //........................................................................
  //-------------------------------- getKey --------------------------------

  /**
   * Get the key uniqueliy identifying this entry.
   *
   * @return   the key for the entry
   *
   */
  @SuppressWarnings("unchecked")
  @Override
  public AbstractEntry.EntryKey<? extends AbstractEntry> getKey()
  {
    return m_entry.getKey();
  }

  //........................................................................
  //------------------------------- getEntry -------------------------------

  /**
   * Get the entry associated with this extension.
   *
   * @return  the associated entry
   */
  // public AbstractEntry getEntry()
  // {
  //   return m_entry;
  // }

  //........................................................................
  //----------------------------- getEditType ------------------------------

  /**
   * Get the type of the entry.
   *
   * @return      the requested name
   *
   */
  @Override
  public String getEditType()
  {
    return m_entry.getEditType();
  }

  //........................................................................
  //---------------------------- getSubEntries -----------------------------

  /**
   * Get all the sub entries present in this extension.
   *
   * @param       inDeep return subentries deeply or just for this extension
   *
   * @return      the list with all the entries or null if none
   *
   */
  public @Nullable List<Entry<?>> getSubEntries(boolean inDeep)
  {
    return null;
  }

  //........................................................................

  //------------------------------- getEntry -------------------------------

  /**
   * Get the entry this extension is attached to, if any.
   *
   * @return      the entry or base entry this one is attached to
   *
   */
  @Override
  public T getEntry()
  {
    return m_entry;
  }

  //........................................................................
  //------------------------------- getName --------------------------------

  /**
    * Get the name of the extension.
    *
    * @return      the name
    */
  @Override
  public String getName()
  {
    return m_name;
  }

  //........................................................................
  //-------------------------------- getID ---------------------------------

  /**
   * Get the identification of the extension, containing the name and the
   * tag, if any.
   *
   * @return      the id
   */
  @Override
  @Deprecated
  public String getID()
  {
    //   if(m_tag == null)
    return m_name;
    //   else
    //     return m_tag + ":" + m_name;
  }

  //........................................................................
  //-------------------------- getAutoExtensions --------------------------

  /**
   * Get all extensions automatically present in entry if this extension
   * is present in a base entry.
   *
   * @param       inClass the class to get the extensions for
   *
   * @return      an iterator with all the names of the extensions
   *
   */
  public static List<String> getAutoExtensions(Class<?> inClass)
  {
    String []names = s_autoExtensions.get(inClass);
    if(names == null)
      return new ArrayList<String>();

    return Arrays.asList(names);
  }

  //........................................................................
  //---------------------------- getBaseEntries ----------------------------

  /**
   * Get the base entries this abstract entry is based on, if any.
   *
   * @return      the requested base entries
   *
   */
  @Override
  public List<BaseEntry> getBaseEntries()
  {
    return m_entry.getBaseEntries();
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
  @Override
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    return m_entry.isDM(inUser);
  }

  //........................................................................

  //------------------------- computeIndexValues ---------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @param       ioValues a multi map of values per index name
   *
   */
  public abstract void computeIndexValues
    (Multimap<Index.Path, String> ioValues);

  //........................................................................
 //------------------------------- toString -------------------------------

  /**
   * Return a humand readable version of the value for debugging.
   *
   * @return      the string representation of the object
   *
   */
  @Override
  public String toString()
  {
    return m_name + " (for " + m_entry.getName() + ")";
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- store ---------------------------------

  /**
   * Store this entry in the given storage container.
   *
   * @param       inStorage   the storage that stores this entry
   *
   * @return      true if stored, false if not
   *
   */
  // public boolean store(Storage<? extends AbstractEntry> inStorage)
  // {
  //   // nothing to do here, maybe in derivations...
  //   return true;
  // }

  //........................................................................
  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled.
   *
   */
  // public void complete()
  // {
  //   List<AbstractEntry> bases = new ArrayList<AbstractEntry>();

  //   List<BaseEntry> baseEntries = m_entry.getBaseEntries();

  //   if(baseEntries != null)
  //     for(BaseEntry entry : m_entry.getBaseEntries())
  //     {
  //       if(entry == null)
  //         continue;

  //       bases.add(entry);
  //     }

  //   // complete the values to make sure intializers and the like are handled
  //   completeVariables(bases);

  //   m_complete = true;
  // }

  //........................................................................
  //-------------------------------- check ---------------------------------

  /**
   * Check the extension for possible problems.
   *
   * @return      true if no problem found, false if there was a problem
   *
   */
  // public boolean check()
  // {
  //   return true;
  // }

  //........................................................................
  //------------------------------- changed --------------------------------

  /**
   * Set the state of the file to changed.
   *
   * @param       inChanged the value to set to, true for changed (dirty),
   *                        false for unchanged (clean)
   *
   */
  @Override
  public void changed(boolean inChanged)
  {
    m_entry.changed(inChanged);
  }

  //........................................................................
  //------------------------------- execute --------------------------------

  /**
   * Execute the given action.
   *
   * @param       inAction the action to execute
   *
   * @return      true if executed and no more execution necessary, false if
   *              execute but either unsuccessfully or other instances need to
   *              execute as well.
   *
   * @undefined   IllegalArgumentException if no action given
   *
   */
  // public boolean execute(Action inAction)
  // {
  //   if(inAction == null)
  //     throw new IllegalArgumentException("must have an action here");

  //   // don't know how to handle actions
  //   return false;
  // }

  //........................................................................

  //-------------------------- setAutoExtensions --------------------------

  /**
   * Set the automatic extensions to be used for entry extensions.
   *
   * @param       inClass       the class to set for
   * @param       inAbstractExtensions the automatic extensions to use
   */
  public static void setAutoExtensions
    (Class<? extends AbstractExtension<?>> inClass,
     String ... inAbstractExtensions)
  {
    if(inAbstractExtensions.length == 0)
      throw new IllegalArgumentException("must have extensions here");

    s_autoExtensions.put(inClass, inAbstractExtensions);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // no tests here, abstract class

  //........................................................................
}