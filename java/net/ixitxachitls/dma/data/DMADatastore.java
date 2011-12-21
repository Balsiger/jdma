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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.values.LongFormattedText;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
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
    m_blobs = BlobstoreServiceFactory.getBlobstoreService();
    m_image = ImagesServiceFactory.getImagesService();
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The access to the datastore. */
  private @Nonnull DatastoreService m_store;

  /** The blob store service. */
  private @Nonnull BlobstoreService m_blobs;

  /** The image service to serve images. */
  private @Nonnull ImagesService m_image;

  /** The cache for indexes. */
  private static MemcacheService s_cache =
    MemcacheServiceFactory.getMemcacheService();

  /** Joiner to join keys together. */
  private static final Joiner s_keyJoiner = Joiner.on(":");

  /** Experiation time for the cache. */
  private static Expiration s_expiration = Expiration.byDeltaSeconds(10 * 60);

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /** The maximal number of entries to rebuild in one pass. */
  private static final int s_maxRebuild = 1000;

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
  //----------------------------- getEntries -------------------------------

  /**
   * Gets all the entries of a specific type.
   *
   * @param    <T>     the type of entry to get
   * @param    inType  the type of entries to get
   * @param    inStart the starting number of entires to get (starts as 0)
   * @param    inSize  the maximal number of entries to return
   *
   * @return   a list with all the entries
   *
   */
  @SuppressWarnings("unchecked") // need to cast
  public @Nonnull <T extends AbstractEntry> List<T> getEntries
                     (@Nonnull AbstractType<T> inType, int inStart, int inSize)
  {
    List<T> entries = new ArrayList<T>();

    Query query = new Query(inType.toString());
    FetchOptions options =
      FetchOptions.Builder.withOffset(inStart).limit(inSize);
    for(Entity entity : m_store.prepare(query).asIterable(options))
      entries.add((T)convert(entity));

    return entries;
  }

  //........................................................................
  //------------------------------- getEntry -------------------------------

  /**
   * Get an entry denoted by type and id.
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
  //---------------------------- getFirstEntry -----------------------------

  /**
   * Get the first entry of the given type.
   *
   * @param      inType the type of the entry to get
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entry found, if any
   *
   */
  @SuppressWarnings("unchecked") // need to cast return value
  public @Nullable <T extends AbstractEntry> T
                      getFirstEntry(@Nonnull AbstractType<T> inType)
  {
    Query query = new Query(inType.toString());
    query.addSort("__key__", Query.SortDirection.ASCENDING);
    PreparedQuery preparedQuery = m_store.prepare(query);
    List<Entity> entities =
      preparedQuery.asList(FetchOptions.Builder.withLimit(1));

    if(entities.size() != 1)
      return null;

    return (T)convert(entities.get(0));
  }


  //........................................................................
  //---------------------------- getLastEntry ------------------------------

  /**
   * Get the last entry of the given type.
   *
   * @param      inType the type of the entry to get
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entry found, if any
   *
   */
  @SuppressWarnings("unchecked") // need to cast return value
  public @Nullable <T extends AbstractEntry> T
                      getLastEntry(@Nonnull AbstractType<T> inType)
  {
    Query query = new Query(inType.toString());
    query.addSort("__key__", Query.SortDirection.DESCENDING);
    PreparedQuery preparedQuery = m_store.prepare(query);
    List<Entity> entities =
      preparedQuery.asList(FetchOptions.Builder.withLimit(1));

    if(entities.size() != 1)
      return null;

    return (T)convert(entities.get(0));
  }


  //........................................................................
  //---------------------------- getNextEntry ------------------------------

  /**
   * Get the next entry of the given type.
   *
   * @param      inID   the id of the entry for which we want the next
   * @param      inType the type of the entry to get
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entry found, if any
   *
   */
  @SuppressWarnings("unchecked") // need to cast return value
  public @Nullable <T extends AbstractEntry> T
                      getNextEntry(@Nonnull String inID,
                                   @Nonnull AbstractType<T> inType)
  {
    Query query = new Query(inType.toString());
    query
      .addSort("__key__", Query.SortDirection.ASCENDING)
      .addFilter("__key__", Query.FilterOperator.GREATER_THAN,
                 KeyFactory.createKey(inType.toString(), inID));
    PreparedQuery preparedQuery = m_store.prepare(query);
    List<Entity> entities =
      preparedQuery.asList(FetchOptions.Builder.withLimit(1));

    if(entities.size() != 1)
      return null;

    return (T)convert(entities.get(0));
  }

  //........................................................................
  //-------------------------- getPreviousEntry ----------------------------

  /**
   * Get the previous entry of the given type.
   *
   * @param      inID   the id of the entry for which we want the previous
   * @param      inType the type of the entry to get
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entry found, if any
   *
   */
  @SuppressWarnings("unchecked") // need to cast return value
  public @Nullable <T extends AbstractEntry> T
                      getPreviousEntry(@Nonnull String inID,
                                       @Nonnull AbstractType<T> inType)
  {
    Query query = new Query(inType.toString());
    query
      .addSort("__key__", Query.SortDirection.DESCENDING)
      .addFilter("__key__", Query.FilterOperator.LESS_THAN,
                 KeyFactory.createKey(inType.toString(), inID));
    PreparedQuery preparedQuery = m_store.prepare(query);
    List<Entity> entities =
      preparedQuery.asList(FetchOptions.Builder.withLimit(1));

    if(entities.size() != 1)
      return null;

    return (T)convert(entities.get(0));
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
  //----------------------------- getUserData ------------------------------

  /**
   * Get user specific data for the given user.
   *
   * @param       inUser the user for whom to get the data
   *
   * @return      the user specific data
   *
   */
  public @Nonnull DMAData getUserData(@Nonnull BaseCharacter inUser)
  {
    // TODO: this is wrong!
    return this;
  }

  //........................................................................
  //------------------------------- getFiles -------------------------------

  /**
   * Get the files for the given entry.
   *
   * @param    inEntry the entry for which to get files
   *
   * @return   a list of all the files found
   *
   */
  public @Nonnull List<File> getFiles(@Nonnull AbstractEntry inEntry)
  {
    Query query =
      new Query("file", KeyFactory.createKey(inEntry.getType().toString(),
                                             inEntry.getID()));
    query.addSort("__key__", Query.SortDirection.ASCENDING);
    PreparedQuery preparedQuery = m_store.prepare(query);
    List<File> files = new ArrayList<File>();
    for(Entity entity : preparedQuery.asList
          (FetchOptions.Builder.withLimit(100)))
    {
      String name = (String)entity.getProperty("name");
      String type = (String)entity.getProperty("type");
      String path = (String)entity.getProperty("path");
      String icon = null;
      if(type == null)
        type = "image/png";

      if(type.startsWith("image/"))
        icon = m_image.getServingUrl(new BlobKey(path));
      else if("application/pdf".equals(type))
        icon = "/icons/pdf.png";
      else
      {
        Log.warning("unknown file type " + type + " ignored for " + name);
        continue;
      }

      files.add(new File(name, type, "//file/" + path, icon));
    }

    // add the files from any base entries
    for(AbstractEntry entry : inEntry.getBaseEntries())
      files.addAll(getFiles(entry));

    return files;
  }

  //........................................................................
  //--------------------------- getIndexEntries ----------------------------

  /**
   * Get the entries for the given index.
   *
   * @param    <T>      The type of the entries to get
   * @param    inIndex  the name of the index to get
   * @param    inType   the type of entries to return for the index (app engine
   *                    can only do filter on queries with kind)
   * @param    inGroup  the group to get entries for
   * @param    inStart  the 0 based index of the first entry to return
   * @param    inSize   the maximal number of entries to return
   *
   * @return   the entries matching the given index
   *
   */
  @SuppressWarnings("unchecked") // need to cast return value for generics
  public @Nonnull <T extends AbstractEntry> List<T> getIndexEntries
                     (@Nonnull String inIndex, @Nonnull AbstractType<T> inType,
                      @Nonnull String inGroup, int inStart, int inSize)
  {
    List<AbstractEntry> entries = new ArrayList<AbstractEntry>();

    Query query = new Query(inType.toString());
    FetchOptions options =
      FetchOptions.Builder.withOffset(inStart);
    if(inSize > 0)
      options.limit(inSize);

    query.addFilter(toPropertyName(Index.PREFIX + inIndex),
                    Query.FilterOperator.EQUAL, inGroup);
    for(Entity entity : m_store.prepare(query).asIterable(options))
      entries.add(convert(entity));

    return (List<T>)entries;
  }

  //........................................................................
  //---------------------------- getIndexNames -----------------------------

  /**
   * Get the names for the given index.
   *
   * @param       inIndex   the index to get it for
   * @param       inType    the type of entries to look for (required for app
   *                        engine)
   * @param       inCached  true to use the cache if possible, false for not
   * @param       inFilters pairs of property key and values to use for
   *                        filtering;
   *                        note that this filters on whole while entities and
   *                        indexes are independent (e.g. filtering by name and
   *                        job is not possible, as giving a job filter will
   *                        return all persons from all entities that have that
   *                        job, not necessarily that have that job for the
   *                        name)
   *
   * @return      a multi map with all the names
   *
   */
  @SuppressWarnings("unchecked") // need to cast from property value
  public @Nonnull SortedSet<String> getIndexNames
    (@Nonnull String inIndex,
     @Nonnull AbstractType<? extends AbstractEntry> inType, boolean inCached,
     @Nonnull String ... inFilters)
  {
    SortedSet<String> names = null;
    String key = inType + ":" + inIndex;
    if(inFilters.length > 0)
      key += ":" + s_keyJoiner.join(inFilters);

    if(inCached)
      names = (SortedSet<String>)s_cache.get(key);

    if(names != null)
      return names;

    names = new TreeSet<String>();

    Query query = new Query(inType.toString());
    for(int i = 0; i + 1 < inFilters.length; i += 2)
    {
      query.addFilter(inFilters[i], Query.FilterOperator.EQUAL,
                      inFilters[i + 1]);
    }
    FetchOptions options = FetchOptions.Builder.withChunkSize(100);
    for(Entity entity : m_store.prepare(query).asIterable(options))
    {
      List<String> values = (List<String>)
        entity.getProperty(toPropertyName(Index.PREFIX + inIndex));

      if(values == null)
        continue;

      for(String value : values)
        names.add(value);
    }

    s_cache.put(key, names, s_expiration);
    return names;
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

  //-------------------------------- update --------------------------------

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
    Log.debug("Storing data for " + inEntry.getType() + " "
              + inEntry.getName());
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
    return update(inEntry);
  }

  //........................................................................
  //------------------------------- addFile --------------------------------

  /**
   * Add a file for the given entry.
   *
   * @param  inEntry the entry to add the file to
   * @param  inName  the name of the file
   * @param  inType  the type of the file
   * @param  inKey   the key of the blob in the blobstore
   *
   */
  public void addFile(@Nonnull AbstractEntry inEntry, @Nonnull String inName,
                      @Nonnull String inType, @Nonnull BlobKey inKey)
  {
    // if a file with the same name is already there, we have to delete it first
    Key key =
      KeyFactory.createKey(KeyFactory.createKey(inEntry.getType().toString(),
                                                inEntry.getID()),
                           "file", inName);
    Entity entity = null;

    try
    {
      entity = m_store.get(key);
      Log.important("replacing file " + inName + " for " + inEntry.getType()
                    + " " + inEntry.getID() + " [" + inKey + "]");
      m_blobs.delete(new BlobKey((String)entity.getProperty("path")));
      m_store.delete(key);
    }
    catch(com.google.appengine.api.datastore.EntityNotFoundException e)
    {
      entity = new Entity(key);
    }

    entity.setProperty("path", inKey.getKeyString());
    entity.setProperty("name", inName);
    entity.setProperty("type", inType);
    m_store.put(entity);
  }

  //........................................................................
  //----------------------------- removeFile -------------------------------

  /**
   * Remove a file from the given entry.
   *
   * @param  inEntry the entry to add the file to
   * @param  inName  the name of the file
   *
   */
  public void removeFile(@Nonnull AbstractEntry inEntry, @Nonnull String inName)
  {
    Key key =
      KeyFactory.createKey(KeyFactory.createKey(inEntry.getType().toString(),
                                                inEntry.getID()),
                           "file", inName);
    try
    {
      Entity entity = m_store.get(key);
      m_blobs.delete(new BlobKey((String)entity.getProperty("path")));
      Log.important("deleting file " + inName + " for " + inEntry.getType()
                    + " " + inEntry.getID());
      m_store.delete(key);
    }
    catch(com.google.appengine.api.datastore.EntityNotFoundException e)
    {
      Log.warning("trying to delete noexistant file " + inName + " for "
                  + inEntry.getType() + " " + inEntry.getID());
    }
  }

  //........................................................................
  //------------------------------- rebuild --------------------------------

  /**
   * Rebuild the given types. This means mainly rebuilding the indexs. It is
   * accomplished by reading all entries and writing them back.
   *
   * NOTE: this produces a lot of datastore traffic.
   *
   * @param      inType  the type to rebuild for
   *
   * @return     the numbert of enties updated
   *
   */
  public int rebuild(@Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    Log.debug("rebuilding data for " + inType);
    List<Entity> entities = new ArrayList<Entity>();

    Query query = new Query(inType.toString());
    FetchOptions options = FetchOptions.Builder.withChunkSize(100);
    int ignored = 0;
    for(Entity entity : m_store.prepare(query).asIterable(options))
    {
      Entity newEntity = convert(convert(entity));

      Set<String> keys = new HashSet<String>();
      keys.addAll(entity.getProperties().keySet());
      keys.addAll(newEntity.getProperties().keySet());

      boolean add = false;
      for(String key : keys)
      {
        Object oldValue = entity.getProperty(key);
        Object newValue = newEntity.getProperty(key);

        if(oldValue == null)
        {
          if(newValue == null)
            continue;

          add = true;
          break;
        }

        if(newValue == null)
        {
          add = true;
          break;
        }

        if(!oldValue.toString().equals(newValue.toString()))
        {
          add = true;
          break;
        }
      }

      if(add)
      {
        entities.add(convert(convert(entity)));
        if(entities.size() >= s_maxRebuild)
          break;
      }
      else
        ignored++;
    }

    m_store.put(entities);
    Log.debug("rebuild " + entities.size() + " entries, ignored " + ignored);
    return entities.size();
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
   * @return     the converted entry, if any
   *
   */
  public @Nullable <T extends AbstractEntry> T convert
                      (@Nonnull String inID, @Nonnull AbstractType<T> inType,
                       @Nonnull Entity inEntity)
  {
    T entry = inType.create(inID, this);
    if(entry == null)
    {
      Log.warning("cannot convert " + inType + " entity with id " + inID + ": "
                  + inEntity);
      return null;
    }

    for(Map.Entry<String, Object> property
          : inEntity.getProperties().entrySet())
    {
      Object value = property.getValue();
      String text;
      if(value instanceof Text)
        text = ((Text)value).getValue();
      else
        text = value.toString();

      entry.set(fromPropertyName(property.getKey()), text);
    }

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
    String id = key.getName();
    AbstractType<? extends AbstractEntry> type =
      AbstractType.get(key.getKind());

    if(type == null || id == null)
      return null;

    return convert(id, type, inEntity);
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
      if(value.getValue() instanceof LongFormattedText
         || value.getValue() instanceof ValueList)
      {
        entity.setProperty(toPropertyName(value.getKey()), new Text(valueText));
      }
      else
      {
        if(valueText.length() >= 500)
          Log.warning("value for " + value.getKey() + " for "
                      + inEntry.getType() + " with id " + inEntry.getID()
                      + " is longer than 500 characters and will be "
                      + "truncated!");

        entity.setProperty(toPropertyName(value.getKey()), valueText);
      }
    }

    // save the index information to make it searchable afterwards
    Multimap<String, String> indexes = inEntry.computeIndexValues();
    for(String index : indexes.keySet())
      entity.setProperty(toPropertyName("index-" + index),
                         indexes.get(index));

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
  //--------------------------- fromPropertyName ---------------------------

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
