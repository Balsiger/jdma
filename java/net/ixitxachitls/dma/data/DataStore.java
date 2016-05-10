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

package net.ixitxachitls.dma.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSortedSet;

import net.ixitxachitls.dma.server.servlets.DMAServlet;
import net.ixitxachitls.util.Tracer;
import net.ixitxachitls.util.logging.Log;

/**
 * The app engine datastore access.
 *
 * @file          DataStore.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class DataStore
{
  /**
   * Create the data store.
   *
   */
  public DataStore()
  {
    m_store = DatastoreServiceFactory.getDatastoreService();
  }

  /** The access to the datastore. */
  private DatastoreService m_store;

  /** The cache for entities. */
  private static MemcacheService s_cacheEntity =
      MemcacheServiceFactory.getMemcacheService("entity");

  /** The cache for multiple entities. */
  private static MemcacheService s_cacheEntities =
      MemcacheServiceFactory.getMemcacheService("entities");

  /** The cache for lookups by value. */
  private static MemcacheService s_cacheByValue =
    MemcacheServiceFactory.getMemcacheService("byValue");

  /** The cache for lookup lists by value. */
  private static MemcacheService s_cacheListByValue =
    MemcacheServiceFactory.getMemcacheService("listByValue");

  /** The cache for lookup ids. */
  // TODO: for some reason, the memcache does not cache long enough
  private static MemcacheService s_cacheIDs =
    MemcacheServiceFactory.getMemcacheService("ids");

  /** The cache for lookup ids by value. */
  private static MemcacheService s_cacheIDsByValue =
    MemcacheServiceFactory.getMemcacheService("idsByValue");

  /** The cache for lookup ids by value. */
  private static MemcacheService s_cacheRecent =
    MemcacheServiceFactory.getMemcacheService("recent");

  /** The cache for lookup ids by value. */
  private static MemcacheService s_cacheValues =
    MemcacheServiceFactory.getMemcacheService("values");

  /** The cache for lookup ids by value. */
  private static MemcacheService s_cacheMultiValues =
    MemcacheServiceFactory.getMemcacheService("multiValues");

  /** Short expiration time for the cache. */
  private static Expiration s_shortExpiration = Expiration.byDeltaSeconds(60);

  /** Expiration time for the cache. */
  private static Expiration s_expiration =
      Expiration.byDeltaSeconds(60 * 60 * 24);

  /** Long expiration time for the cache. */
  private static Expiration s_longExpiration =
    Expiration.byDeltaSeconds(60 * 60 * 24 * 7);

  /** The key for the value containing the last change of an entity. */
  private static final String CHANGE = "change";

  /**
   * Get an entity denoted with a key.
   *
   * @param       inKey the key of the entity to get
   *
   * @return      the entity found, if any
   *
   */
  public Optional<Entity> getEntity(Key inKey)
  {
    Tracer tracer = new Tracer("getting entity " + inKey);
    Entity entity = DMAServlet.isDev()
        ? null : (Entity)s_cacheEntity.get(inKey);

    if(entity == null)
    {
      try
      {
        Log.important("gae: getting entity for " + inKey);
        entity = m_store.get(inKey);
        if(!DMAServlet.isDev())
          s_cacheEntity.put(inKey, entity, s_expiration);
        tracer.done("uncached");
      }
      catch(com.google.appengine.api.datastore.EntityNotFoundException e)
      {
        Log.warning("could not get entity for " + inKey + ": " + e);

        tracer.done("not found");
        return Optional.absent();
      }
    }
    else
      tracer.done("cached");

    return Optional.fromNullable(entity);
  }

  /**
   * Get a single entity denoted with a type and a key value pair.
   *
   * @param       inType the type of entry to look up
   * @param       inKey the key of the value to look for
   * @param       inValue the value to look for
   *
   * @return      the entity found, if any
   */
  public Optional<Entity> getEntity(String inType, String inKey, String inValue)
  {
    Tracer tracer = new Tracer
      ("getting " + inType + " entity with " + inKey + " = " + inValue);
    Entity entity = (Entity)
      s_cacheByValue.get(inKey + "--" + inValue);

    if(entity == null)
    {
      Log.important("gae: getting " + inType + " entity for " + inKey + "="
                + inValue);
      Query query = new Query(inType);
      query.setFilter(new Query.FilterPredicate(toPropertyName(inKey),
                                                Query.FilterOperator.EQUAL,
                                                inValue));
      entity = m_store.prepare(query).asSingleEntity();

      if(entity == null)
      {
        tracer.done("none found");
        return Optional.absent();
      }

      // TODO: this fails when the entity is already otherwise cached!
      s_cacheByValue.put(inKey + "--" + inValue, entity, s_expiration);
      s_cacheEntity.put(entity.getKey(), entity, s_expiration);
      tracer.done("uncached");
    }
    else
      tracer.done("cached");

    return Optional.fromNullable(entity);
  }

  /**
   * Get all entities for the given type.
   *
   * @param    inType       the type of the entities to get
   * @param    inParent     the parent entity, if any
   * @param    inSortField  an optional name of the field to sort by
   * @param    inStart      the starting index of the entities to return
   *                        (0 based)
   * @param    inSize       the maximal number of entieties to return
   *
   * @return   a list with all the entries
   *
   */
  public List<Entity> getEntities(String inType,
                                  Optional<Key> inParent,
                                  Optional<String> inSortField,
                                  int inStart, int inSize)
  {
    String key = inType + ":" + inParent + ":" + inSortField + ":" + inStart
        + ":" + inSize;
    List<Entity> entities = (List<Entity>)s_cacheEntities.get(key);
    if(entities == null)
    {
      Tracer tracer = new Tracer("getting entities for " + inType);
      Query query;
      if(inParent.isPresent())
        query = new Query(inType, inParent.get());
      else
        query = new Query(inType);

      if(inSortField.isPresent())
        query.addSort(inSortField.get(), Query.SortDirection.ASCENDING);

      FetchOptions options =
          FetchOptions.Builder.withOffset(inStart).limit(inSize);

      Log.important("gae: getting entities for " + inType
                        + (inParent.isPresent() ? " (" + inParent + ")" : "")
                        + (inSortField.isPresent() ? " sorted by " + inSortField : "")
                        + " from " + inStart + " size " + inSize);

      tracer.done();

      entities = m_store.prepare(query).asList(options);
      s_cacheEntities.put(key, entities, s_shortExpiration);
    }

    return entities;
  }

  /**
   * Get all the entities matching the given key/value pair(s).
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
  public List<Entity> getEntities(String inType, Optional<Key> inParent,
                                  int inStart, int inSize,
                                  Optional<String> inSort,
                                  String ... inFilters)
  {
    String key = inType + ":" + inParent + ":" + inStart + ":" + inSize + ":"
        + Arrays.toString(inFilters);
    List<Entity> entities = (List<Entity>)s_cacheListByValue.get(key);

    if(entities == null)
    {
      Log.important("gae: getting multiple " + inType + " with "
                    + Arrays.toString(inFilters) + " (uncached)");

      Query query;
      if(inParent.isPresent())
        query = new Query(inType, inParent.get());
      else
        query = new Query(inType);

      if(inSort.isPresent())
        query.addSort(inSort.get());

      if(inFilters.length > 2)
      {
        List<Query.Filter> filters = new ArrayList<Query.Filter>();
        for(int i = 0; i + 1 < inFilters.length; i += 2)
          filters.add(new Query.FilterPredicate(toPropertyName(inFilters[i]),
                                                Query.FilterOperator.EQUAL,
                                                inFilters[i + 1]));

        query.setFilter(new Query.CompositeFilter
                        (Query.CompositeFilterOperator.AND, filters));
      }
      else if(inFilters.length > 0)
        query.setFilter(new Query.FilterPredicate(toPropertyName(inFilters[0]),
                                                  Query.FilterOperator.EQUAL,
                                                  inFilters[1]));

      FetchOptions options =
        FetchOptions.Builder.withOffset(inStart).limit(inSize);

      entities = new ArrayList<Entity>();
      for(Entity entity : m_store.prepare(query).asIterable(options))
        entities.add(entity);

      s_cacheListByValue.put(key, entities, s_expiration);
    }
    else
      Log.debug("gae: getting multiple " + inType + " with "
                + Arrays.toString(inFilters) + " (cached)");

    return entities;
  }

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
  public List<Entity> getIDs(String inType, String inKey, String inValue)
  {
    String key = inType + "--" + inKey + "=" + inValue;
    List<Entity> ids = (List<Entity>)s_cacheIDsByValue.get(key);

    if(ids == null)
    {
      Log.important("gae: getting ids for " + inType + " with " + inKey + " = "
                    + inValue);

      Query query = new Query(inType);
      query.setFilter(new Query.FilterPredicate(toPropertyName(inKey),
                                                Query.FilterOperator.EQUAL,
                                                inValue));
      query.setKeysOnly();

      FetchOptions options = FetchOptions.Builder.withChunkSize(1000);
      ids = new ArrayList<Entity>();
      for(Entity entity : m_store.prepare(query).asIterable(options))
        ids.add(entity);

      s_cacheIDsByValue.put(key, ids, s_expiration);
    }

    return ids;
  }

  /**
   * Get all the ids of all entities with the given type and parent.
   *
   * @param       inType      the type of entities to get
   * @param       inSortField the field to sorty results by, if any
   * @param       inParent    the key of the parent value, if any
   *
   * @return      the list of ids found
   */
  @SuppressWarnings("unchecked")
  public List<String> getIDs(String inType, Optional<String> inSortField,
                             Optional<Key> inParent)
  {
    List<String> ids = (List<String>)s_cacheIDs.get(inType);

    if(ids == null)
    {
      Log.important("gae: getting ids for " + inType
                    + (inParent.isPresent() ? " parent " + inParent : ""));

      Query query;
      if(inParent.isPresent())
        query = new Query(inType, inParent.get());
      else
        query = new Query(inType);

      if(inSortField.isPresent())
        query.addSort(inSortField.get(), Query.SortDirection.ASCENDING);

      query.setKeysOnly();
      FetchOptions options = FetchOptions.Builder.withChunkSize(1000);
      ids = new ArrayList<String>();
      for(Entity entity : m_store.prepare(query).asIterable(options))
        ids.add(entity.getKey().getName());

      s_cacheIDs.put(inType, ids, s_expiration);
    }

    return ids;
  }

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
  public List<Entity> getRecentEntities(String inType, int inSize,
                                        Optional<Key> inParent)
  {
    String key = inType + (inParent != null ? inParent.toString() : "");
    List<Entity> entities = (List<Entity>)s_cacheRecent.get(key);

    if(entities == null)
    {
      Log.important("gae: getting recent " + inType + " entities"
                    + (inParent.isPresent() ? " with parent " + inParent : ""));

      Query query;
      if(inParent.isPresent())
        query = new Query(inType, inParent.get());
      else
        query = new Query(inType);

      query.addSort(CHANGE, Query.SortDirection.DESCENDING);
      FetchOptions options =
        FetchOptions.Builder.withLimit(inSize);
      entities = m_store.prepare(query).asList(options);

      s_cacheRecent.put(key, entities, s_expiration);
    }

    return entities;
  }

  /**
   * Get the values for the given fields.
   *
   * @param       inType   the type of entries to look for
   * @param       inParent the key of the parent entry, if any
   * @param       inFields the fields to return
   *
   * @return      a list of records found, each with values for each field,
   *              in the order they were specificed
   *
   */
  @SuppressWarnings("unchecked")
  public List<List<String>> getMultiValues(String inType,
                                           Optional<Key> inParent,
                                           String ... inFields)
  {
    List<List<String>> records = (List<List<String>>)s_cacheMultiValues.get
      (inType + ":" + Arrays.toString(inFields));

    if(records == null)
    {
      Log.important("gae: get multi values for " + inType + " ("
                    + inParent + ") " + Arrays.toString(inFields));
      Query query;
      if(inParent.isPresent())
        query = new Query(inType, inParent.get());
      else
        query = new Query(inType);

      for(String field : inFields)
        query.addProjection(new PropertyProjection(field, String.class));

      records = new ArrayList<List<String>>();
      FetchOptions options = FetchOptions.Builder.withChunkSize(1000);
      for (Entity entity : m_store.prepare(query).asIterable(options))
      {
        List<String> record = new ArrayList<String>();
        for(String field : inFields)
          record.add((String)entity.getProperty(field));

        records.add(record);
      }

      // These queries are really expensive!
      s_cacheMultiValues.put(inType + ":" + Arrays.toString(inFields), records,
                             s_longExpiration);
    }

    return records;
  }

  /**
   * Get the values for the given field.
   *
   * @param       inType   the type of entries to look for
   * @param       inParent the key of the parent entry, if any
   * @param       inField  the fields to return
   *
   * @return      a list of values found
   *
   */
  @SuppressWarnings("unchecked")
  public SortedSet<String> getValues(String inType, Optional<Key> inParent,
                                     String inField)
  {
    SortedSet<String> values =
      (SortedSet<String>)s_cacheValues.get(inType + ":" + inField);

    if (values == null)
    {
      Log.important("gae: getting values for " + inType + " (" + inParent
                    + ") " + " for field " + inField);
      Query query;
      if(inParent.isPresent())
        query = new Query(inType, inParent.get());
      else
        query = new Query(inType);

      query.addProjection(new PropertyProjection(inField, String.class));

      ImmutableSortedSet.Builder<String> builder =
        ImmutableSortedSet.naturalOrder();
      FetchOptions options = FetchOptions.Builder.withChunkSize(1000);
      for(Entity entity : m_store.prepare(query).asIterable(options))
        if(entity != null && entity.getProperty(inField) != null)
          builder.add((String)entity.getProperty(inField));

      values = builder.build();

      // These queries are really expensive!
      s_cacheValues.put(inType + ":" + inField, values, s_longExpiration);
    }

    return values;
  }

  /**
   * Remove the entity from the store.
   *
   * @param     inKey the key of the entity to remove
   *
   * @return    true if removed, false on error
   *
   */
  public boolean remove(Key inKey)
  {
    Log.debug("removing entity " + inKey);

    try
    {
      Log.important("gae: removing entity " + inKey);
      s_cacheEntity.delete(inKey);
      m_store.delete(inKey);
      // TODO: we should clear some of these caches too, but just clearing all
      // of them results in too many requests to the datastore
      //s_cacheByValue.clearAll();
      //s_cacheListByValue.clearAll();
      //s_cacheRecent.clearAll();
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

  /**
   * Update the datastore with the given entity.
   *
   * @param   inEntity the updated data
   *
   * @return  true if successfully updated, false if not
   *
   */
  public boolean update(Entity inEntity)
  {
    Log.important("gae: storing data for " + inEntity.getKey());

    // Only clear the cache for new entities; this does only check the cache,
    // but should usually be enough.
    if(s_cacheEntity.get(inEntity.getKey()) == null)
    {
      s_cacheIDs.clearAll();
      s_cacheIDsByValue.clearAll();
      s_cacheRecent.clearAll();
    }

    s_cacheEntity.put(inEntity.getKey(), inEntity, s_expiration);
    m_store.put(inEntity);

    // Clear all the cache key value matches.
    for(Entry<String, Object> entry : inEntity.getProperties().entrySet())
      s_cacheByValue.delete(entry.getKey() + "--" + entry.getValue());

    s_cacheListByValue.clearAll();

    return true;
  }

  /**
   * Convert the given name into a name that can be used as a property in the
   * datastore.
   *
   * @param    inName the name to convert
   *
   * @return   the converted name
   */
  protected String toPropertyName(String inName)
  {
    return inName.replaceAll(" ", "_");
  }

  /**
   * Convert the given name into a name that can be used as a property in the
   * datastore.
   *
   * @param    inName the name to convert
   *
   * @return   the converted name
   *
   */
  protected String fromPropertyName(String inName)
  {
    return inName.replaceAll("_", " ");
  }
}
