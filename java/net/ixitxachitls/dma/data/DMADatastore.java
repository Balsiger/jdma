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

package net.ixitxachitls.dma.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * Wrapper for accessing data from app engine's datastore.
 *
 *
 * @file          DMADatastore.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class DMADatastore implements DMAData
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- DMADatastore -----------------------------

  /**
   * Create the datastore.
   *
   */
  public DMADatastore()
  {
    m_store = DatastoreServiceFactory.getDatastoreService();
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The access to the datastore. */
  private @Nonnull DatastoreService m_store;

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getEntries ------------------------------

  /**
   * Gets all the entries of a specific type.
   *
   * @param    <T> The type of entry to get
   * @param    inType the type of entries to get
   *
   * @return   a map with id and type
   *
   */
  @SuppressWarnings("unchecked") // need to cast
  public @Nonnull <T extends AbstractEntry> NavigableMap<String, T>
    getEntries(AbstractType<T> inType)
  {
    NavigableMap<String, T> entries = new TreeMap<String, T>();

    return entries;
  }

  //........................................................................
  //---------------------------- getEntriesList ----------------------------

  /**
   * Gets all the entries of a specific type.
   *
   * @param    <T> The type of entry to get
   * @param    inType the type of entries to get
   *
   * @return   a map with id and type
   *
   */
  @SuppressWarnings("unchecked") // need to cast
  public @Nonnull <T extends AbstractEntry> List<T>
                     getEntriesList(AbstractType<T> inType)
  {
    List<T> entries = new ArrayList<T>();



    return entries;
  }

  //........................................................................
  //------------------------------- getEntry -------------------------------

  /**
   * Get a type denoted by type and id.
   *
   * @param      inID   the id of the entry to get
   * @param      inType the type of the entry to get
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entry found, if any
   *
   */
  public @Nullable <T extends AbstractEntry> T
                      getEntry(@Nonnull String inID,
                               @Nonnull AbstractType<T> inType)
  {
    Key key = KeyFactory.createKey(inType.toString(), inID);
    try
    {
      return convert(inID, inType, m_store.get(key));
    }
    catch(com.google.appengine.api.datastore.EntityNotFoundException e)
    {
      Log.warning("could not get entity for " + inType + " with id " + inID
                  + " (" + key + ")");

      return null;
    }
  }


  //........................................................................
  //----------------------------- getBaseData ------------------------------

  /**
   * Get the base data for entries.
   *
   * @return      the repository with all the base data
   *
   */
  public @Nonnull DMAData getBaseData()
  {
    return this;
  }

  //........................................................................
  //------------------------------ isChanged -------------------------------

  /**
   * Check if any of the data has been changed and needs saving.
   *
   * @return      true if data is changed from store, false if not
   *
   */
  public boolean isChanged()
  {
    return false;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- add ----------------------------------

  /**
   * Add an entry to the store.
   *
   * @param       inEntry the entry to add
   *
   * @return      true if added, false if there was an error
   *
   */
  public boolean update(@Nonnull AbstractEntry inEntry)
  {
    m_store.put(convert(inEntry));
    return true;
  }

  //........................................................................
  //--------------------------------- save ---------------------------------

  /**
   * Save the given entry.
   *
   * @param       inEntry the entry to save
   *
   * @return      true if saved, false if not
   *
   */
  public boolean save(@Nonnull AbstractEntry inEntry)
  {
    Entity entity = convert(inEntry);
    m_store.put(entity);

    return true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- convert --------------------------------

  /**
   * Convert the given datastore entity into a dma entry.
   *
   * @param      inID   the id of the entry to get
   * @param      inType   the type of the entry to get
   * @param      inEntity the entity to convert
   *
   * @param      <T>      the type of the entry to get
   *
   * @return     the entry found, if any
   *
   */
  public @Nonnull <T extends AbstractEntry> T convert
                     (@Nonnull String inID, @Nonnull AbstractType<T> inType,
                      @Nonnull Entity inEntity)
  {
    T entry = inType.create(inID, this);
    for(Map.Entry<String, Object> property
          : inEntity.getProperties().entrySet())
      entry.set(fromPropertyName(property.getKey()),
                (String)property.getValue());

    return entry;
  }

  //........................................................................
  //------------------------------- convert --------------------------------

  /**
   * Convert the given datastore entity into a dma entry.
   *
   * @param      inEntity the entity to convert
   *
   * @return     the entry found, if any
   *
   */
  public @Nullable AbstractEntry convert(@Nonnull Entity inEntity)
  {
    Key key = inEntity.getKey();
    String id = extractID(key);
    AbstractType<? extends AbstractEntry> type = extractType(key);

    if(type == null || id == null)
      return null;

    AbstractEntry entry = type.create(id, this);
    for(Map.Entry<String, Object> property
          : inEntity.getProperties().entrySet())
      entry.set(fromPropertyName(property.getKey()),
                (String)property.getValue());

    return entry;
  }

  //........................................................................
  //------------------------------- convert --------------------------------

  /**
   * Convert the given datastore entity into a dma entry.
   *
   * @param      inEntry the entry to convert
   *
   * @return     the entry found, if any
   *
   */
  public @Nonnull Entity convert(@Nonnull AbstractEntry inEntry)
  {
    Entity entity = new Entity(inEntry.getType().toString(), inEntry.getID());
    for(Map.Entry<String, Value> value : inEntry.getAllValues().entrySet())
    {
      String valueText = value.getValue().toString();
      if(valueText.length() >= 500)
        Log.warning("value for " + value.getKey() + " for "
                    + inEntry.getType() + " with id " + inEntry.getID()
                    + " is longer than 500 characters and will be truncated!");

      entity.setProperty(toPropertyName(value.getKey()), valueText);
    }

    return entity;
  }

  //........................................................................
  //---------------------------- toPropertyName ----------------------------

  /**
   * Convert the given name into a name that can be used as a property in the
   * datastore.
   *
   * @param    inName the name to convert
   *
   * @return   the converted name
   *
   */
  public @Nonnull String toPropertyName(@Nonnull String inName)
  {
    return inName.replaceAll(" ", "_");
  }

  //........................................................................
  //---------------------------- toPropertyName ----------------------------

  /**
   * Convert the given name into a name that can be used as a property in the
   * datastore.
   *
   * @param    inName the name to convert
   *
   * @return   the converted name
   *
   */
  public @Nonnull String fromPropertyName(@Nonnull String inName)
  {
    return inName.replaceAll("_", " ");
  }

  //........................................................................
  //------------------------------ extractID -------------------------------

  /**
   * Extract the id from the datastore key.
   *
   * @param       inKey the key of the entity
   *
   * @return      the id, if any
   *
   */
  public @Nullable String extractID(@Nonnull Key inKey)
  {
    System.out.println(inKey);
    return Strings.getPattern(inKey.toString(), "\\(\"(.*)\"\\)");
  }

  //........................................................................
  //----------------------------- extractType ------------------------------

  /**
   * Extract the type from the entity key.
   *
   * @param       inKey the key of the entity
   *
   * @return      the type, if any
   *
   */
  public @Nullable AbstractType<? extends AbstractEntry> extractType
                                  (@Nonnull Key inKey)
  {
    return AbstractType.get(Strings.getPattern(inKey.toString(), "(.*)\\("));
  }

  //........................................................................


  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  // public static class Test extends net.ixitxachitls.util.test.TestCase
  // {
  // }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
