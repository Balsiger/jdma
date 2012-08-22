/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The app engine datastore access.
 *
 *
 * @file          DataStore.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class DataStore
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ DataStore -------------------------------

  /**
   * Create the data store.
   *
   */
  public DataStore()
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
  private static MemcacheService s_cacheEntity =
    MemcacheServiceFactory.getMemcacheService("entity");

  /** The cache for lookups by value. */
  private static MemcacheService s_cacheByValue =
    MemcacheServiceFactory.getMemcacheService("byValue");

  /** The cache for lookup lists by value. */
  private static MemcacheService s_cacheListByValue =
    MemcacheServiceFactory.getMemcacheService("listByValue");

  /** The cache for lookup ids. */
  private static MemcacheService s_cacheIDs =
    MemcacheServiceFactory.getMemcacheService("ids");

  /** The cache for lookup ids by value. */
  private static MemcacheService s_cacheIDsByValue =
    MemcacheServiceFactory.getMemcacheService("idsByValue");

  /** The cache for lookup ids by value. */
  private static MemcacheService s_cacheRecent =
    MemcacheServiceFactory.getMemcacheService("recent");

  /** Experiation time for the cache. */
  private static Expiration s_expiration = Expiration.byDeltaSeconds(60 * 60);

  /** The key for the value containing the last change of an entity. */
  private static final String CHANGE = "change";


  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getEntity ------------------------------

  /**
   * Get an entity denoted with a key.
   *
   * @param       inKey the key of the entity to get
   *
   * @return      the entity found, if any
   *
   */
  public @Nullable Entity getEntity(@Nonnull Key inKey)
  {
    Entity entity = (Entity)s_cacheEntity.get(inKey.toString());

    if(entity == null)
    {
      try
      {
        Log.debug("gae: getting entity for " + inKey);
        entity = m_store.get(inKey);
        s_cacheEntity.put(inKey, entity, s_expiration);
      }
      catch(com.google.appengine.api.datastore.EntityNotFoundException e)
      {
        Log.warning("could not get entity for " + inKey + ": " + e);

        return null;
      }
    }


    return entity;
  }

  //........................................................................
  //------------------------------- getEntity ------------------------------

  /**
   * Get a single entity denoted with a type and a key value pair.
   *
   * @param       inType the type of entry to look up
   * @param       inKey the key of the value to look for
   * @param       inValue the value to look for
   *
   * @return      the entity found, if any
   *
   */
  public @Nullable Entity getEntity(@Nonnull String inType,
                                    @Nonnull String inKey,
                                    @Nonnull String inValue)
  {
    Entity entity = (Entity)
      s_cacheByValue.get(inKey + "--" + inValue);

    if(entity == null)
    {
      Log.debug("gae: getting " + inType + " entity for " + inKey + "="
                + inValue);
      Query query = new Query(inType);
      query.addFilter(toPropertyName(inKey), Query.FilterOperator.EQUAL,
                      inValue);
      entity = m_store.prepare(query).asSingleEntity();

      if(entity == null)
        return null;

      s_cacheByValue.put(inKey + "--" + inValue, entity, s_expiration);
    }

    return entity;
  }

  //........................................................................
  //----------------------------- getEntities ------------------------------

  /**
   * Get all entities for the given type.
   *
   * @param    inType       the type of the entieties to get
   * @param    inParent     the parent entity, if any
   * @param    inSortField  an optional name of the field to sort by
   * @param    inStart      the starting index of the entities to return
   *                        (0 based)
   * @param    inSize       the maximal number of entieties to return
   *
   * @return   a list with all the entries
   *
   */
  public Iterable<Entity> getEntities(@Nonnull String inType,
                                      @Nullable Key inParent,
                                      @Nullable String inSortField,
                                      int inStart, int inSize)
  {
    Query query;
    if(inParent == null)
      query = new Query(inType);
    else
      query = new Query(inType, inParent);

    if(inSortField != null)
      query.addSort(inSortField, Query.SortDirection.ASCENDING);

    FetchOptions options =
      FetchOptions.Builder.withOffset(inStart).limit(inSize);

    Log.debug("gae: getting entities for " + inType
              + (inParent != null ? " (" + inParent + ")" : "")
              + (inSortField != null ? " sorted by " + inSortField : "")
              + " from " + inStart + " size " + inSize);

    return m_store.prepare(query).asIterable(options);
  }

  //........................................................................
  //----------------------------- getEntities ------------------------------

  /**
   * Get all the entities matching the given key/vallue pair(s).
   *
   * @param    inType    the type of entry to get
   * @param    inParent  the key to the parent entity, if any
   * @param    inStart   the index of the starting entity
   * @param    inSize    the number of entities to return
   * @param    inFilters key value pairs to look for.
   *
   * @return   all the matching entities found
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull List<Entity> getEntities(@Nonnull String inType,
                                           @Nullable Key inParent,
                                           int inStart, int inSize,
                                           @Nonnull String ... inFilters)
  {
    Log.debug("getting multiple " + inType + " with "
              + Arrays.toString(inFilters));

    String key = Arrays.toString(inFilters);
    List<Entity> entities = (List<Entity>)s_cacheListByValue.get(key);

    if(entities == null)
    {
      Query query;
      if(inParent == null)
        query = new Query(inType);
      else
        query = new Query(inType, inParent);

      for(int i = 0; i + 1 < inFilters.length; i += 2)
        query.addFilter(toPropertyName(inFilters[i]),
                        Query.FilterOperator.EQUAL,
                        inFilters[i + 1]);
      FetchOptions options =
        FetchOptions.Builder.withOffset(inStart).limit(inSize);

      entities = new ArrayList<Entity>();
      for(Entity entity : m_store.prepare(query).asIterable(options))
        entities.add(entity);

      s_cacheListByValue.put(key, entities, s_expiration);
    }

    return entities;
  }

  //........................................................................
  //-------------------------------- getIDs --------------------------------

  /**
   * Get all the ids of all entities with the given type and key value.
   *
   * @param       inType      the type of entities to get
   * @param       inKey       the key to look for
   * @param       inValue     the value to look for
   *
   * @return      the list of entities (ids and parent only)
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull List<Entity> getIDs(@Nonnull String inType,
                                      @Nonnull String inKey,
                                      @Nonnull String inValue)
  {
    String key = inType + "--" + inKey + "=" + inValue;
    List<Entity> ids = (List<Entity>)s_cacheIDsByValue.get(key);

    if(ids == null)
    {
      Log.debug("getting ids for " + inType + " with " + inKey + " = "
                + inValue);

      Query query = new Query(inType);
      query.addFilter(toPropertyName(inKey), Query.FilterOperator.EQUAL,
                      inValue);
      query.setKeysOnly();

      FetchOptions options = FetchOptions.Builder.withChunkSize(1000);
      ids = new ArrayList<Entity>();
      for(Entity entity : m_store.prepare(query).asIterable(options))
        ids.add(entity);

      s_cacheIDsByValue.put(key, ids, s_expiration);
    }

    return ids;
  }

  //........................................................................
  //-------------------------------- getIDs --------------------------------

  /**
   * Get all the ids of all entities with the given type and parent.
   *
   * @param       inType      the type of entities to get
   * @param       inSortField the field to sorty results by, if any
   * @param       inParent    the key of the parent value, if any
   *
   * @return      the list of ids found
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull List<String> getIDs(@Nonnull String inType,
                                      @Nullable String inSortField,
                                      @Nullable Key inParent)
  {
    List<String> ids = (List<String>)s_cacheIDs.get(inType);

    if(ids == null)
    {
      Log.debug("getting ids for " + inType
                + (inParent != null ? " parent " + inParent : ""));

      Query query;
      if(inParent == null)
        query = new Query(inType);
      else
        query = new Query(inType, inParent);

      if(inSortField != null)
        query.addSort(inSortField, Query.SortDirection.ASCENDING);

      query.setKeysOnly();
      FetchOptions options = FetchOptions.Builder.withChunkSize(1000);
      ids = new ArrayList<String>();
      for(Entity entity : m_store.prepare(query).asIterable(options))
        ids.add(entity.getKey().getName());

      s_cacheIDs.put(inType, ids, s_expiration);
    }

    return ids;
  }

  //........................................................................
  //-------------------------- getRecentEntities ---------------------------

  /**
   * Get the most recent entries for the given type.
   *
   * @param   inType   the type of entries to get
   * @param   inSize   the number of recent entitites to get back
   * @param   inParent the key of the parent entry, if any
   *
   * @return  the list of recent entities
   *
   */
  @SuppressWarnings("unchecked") // cache
  public List<Entity> getRecentEntities(@Nonnull String inType, int inSize,
                                        @Nullable Key inParent)
  {
    String key = inType + (inParent != null ? inParent.toString() : "");
    List<Entity> entities = (List<Entity>)s_cacheRecent.get(key);

    if(entities == null)
    {
      Log.debug("getting recent " + inType + " entities"
                + (inParent != null ? " with parent " + inParent : ""));

      Query query;
      if(inParent == null)
        query = new Query(inType);
      else
        query = new Query(inType, inParent);

      query.addSort(CHANGE, Query.SortDirection.DESCENDING);
      FetchOptions options =
        FetchOptions.Builder.withLimit(inSize);
      entities = m_store.prepare(query).asList(options);

      s_cacheRecent.put(key, entities, s_expiration);
    }

    return entities;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- remove --------------------------------

  /**
   * Remove the entity from the store.
   *
   * @param     inKey the key of the entity to remove
   *
   * @return    true if removed, false on error
   *
   */
  public boolean remove(@Nonnull Key inKey)
  {
    Log.debug("removing entity " + inKey);

    try
    {
      s_cacheEntity.delete(inKey);
      m_store.delete(inKey);
      s_cacheByValue.clearAll();
      s_cacheListByValue.clearAll();
      s_cacheRecent.clearAll();
      s_cacheIDs.clearAll();
      s_cacheIDsByValue.clearAll();

      return true;
    }
    catch(IllegalArgumentException e)
    {
      Log.warning("could not remove entity for " + inKey);
      return false;
    }
  }

  //........................................................................
  //-------------------------------- update --------------------------------

  /**
   * Update the datastore with the given entity.
   *
   * @param   inEntity the updated data
   *
   * @return  true if successfully updated, false if not
   *
   */
  public boolean update(@Nonnull Entity inEntity)
  {
    Log.debug("Storing data for " + inEntity.getKey());

    s_cacheEntity.put(inEntity.getKey(), inEntity, s_expiration);
    m_store.put(inEntity);
    s_cacheByValue.clearAll();
    s_cacheListByValue.clearAll();
    s_cacheRecent.clearAll();

    return true;
  }

  //........................................................................


  //........................................................................

  //------------------------------------------------- other member functions

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
  protected @Nonnull String toPropertyName(@Nonnull String inName)
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
  protected @Nonnull String fromPropertyName(@Nonnull String inName)
  {
    return inName.replaceAll("_", " ");
  }

  //........................................................................

  //........................................................................
}
