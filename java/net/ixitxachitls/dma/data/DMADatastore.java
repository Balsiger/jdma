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

package net.ixitxachitls.dma.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.entries.Product;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.util.Tracer;
import net.ixitxachitls.util.logging.Log;

/**
 * Wrapper for accessing data from app engine's datastore.
 *
 * @file          DMADatastore.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class DMADatastore
{
  /**
   * Create the datastore.
   */
  public DMADatastore()
  {
  }

  /** The access to the datastore. Don't use this except in the AdminServlet! */
  private DataStore m_data = new DataStore();

  /** The cache of entries. */
  private static ThreadLocal<Map<EntryKey, AbstractEntry>> s_cache =
      new ThreadLocal<Map<EntryKey, AbstractEntry>>()
      {
        @Override
        protected Map<EntryKey, AbstractEntry> initialValue()
        {
          return new HashMap<>();
        }
      };

  /** The id for serialization. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 1L;

  /**
   * Cache the entry for later use. The cache is only per thread and is used
   * for a single request only.
   *
   * @param inKey the key of the entry
   * @param inEntry the entry to cache
   */
  private static void cache(EntryKey inKey, AbstractEntry inEntry)
  {
    s_cache.get().put(inKey, inEntry);
  }

  /**
   * Get the cached entry with the given key.
   *
   * @param inKey the key of the entry to get
   * @return the cached entry, optional if it is not cached
   */
  private static Optional<AbstractEntry> cached(EntryKey inKey)
  {
    return Optional.fromNullable(s_cache.get().get(inKey));
  }

  /** Clear the cache to prevent inconsistent states. */
  public static void clearCache()
  {
    s_cache.get().clear();
  }

  /**
   * Get an entry denoted by type and id and their respective parents.
   *
   * @param      inKey  the key to the entry to get
   * @param      <T>    the type of entry to get
   *
   * @return     the entry found, if any
   */
  public <T extends AbstractEntry> Optional<T> getEntry(EntryKey inKey)
  {
    Optional<T> entry = (Optional<T>) cached(inKey);
    if(!entry.isPresent())
    {
      Log.debug("getting entry for " + inKey);
      Optional<Entity> entity =
          m_data.getEntity(convert(Optional.of(inKey)).get());
      if (entity.isPresent())
        entry = (Optional<T>) convert(inKey.getID(), inKey.getType(),
                                      entity.get());

      if(entry.isPresent())
        cache(inKey, entry.get());
    }

    return entry;
  }

  /**
   * Gets all the entries of a specific type.
   *
   * @param    <T>      the type of entry to get
   * @param    inType   the type of entries to get
   * @param    inParent the key of the parent, if any
   * @param    inStart  the starting number of entries to get (starts as 0)
   * @param    inSize   the maximal number of entries to return
   *
   * @return   a list with all the entries
   */
  @SuppressWarnings("unchecked")
  public <T extends AbstractEntry> List<T>
  getEntries(AbstractType<T> inType, Optional<EntryKey> inParent,
             int inStart, int inSize)
  {
    List<T> entries = new ArrayList<>();
    try
    {
    Iterable<Entity> entities =
      m_data.getEntities(escapeType(inType.toString()), convert(inParent),
                         inType.getSortField(), inStart, inSize);

    for(Entity entity : entities)
    {
      Optional<T> entry = convert(entity);
      if(entry.isPresent())
        entries.add(entry.get());
    }
    } catch(Exception e)
    {
      Log.error("Cannot get entries: " + e);
    }

    return entries;
  }

  /**
   * Get the entry denoted by a key value pair.
   *
   * @param      inType  the type of entry to get
   * @param      inKey   the key to look for
   * @param      inValue the value for the key to look for
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entry found, if any
   */
  public <T extends AbstractEntry> Optional<T> getEntry(AbstractType<T> inType,
                                                        String inKey,
                                                        String inValue)
  {
    Log.debug("Getting entry for " + inKey + "=" + inValue);
    Optional<Entity> entity = m_data.getEntity(escapeType(inType.toString()),
                                               inKey, inValue);
    if(entity.isPresent())
      return convert(entity.get());

    return Optional.absent();
  }

  /**
   * Get the entry denoted by a key value pair.
   *
   * @param      inType   the type of entry to get
   * @param      inParent the key of the parent entry
   * @param      inKey    the key to look for
   * @param      inValue  the value for the key to look for
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entries found
   */
  @SuppressWarnings("unchecked") // casting return
  public <T extends AbstractEntry>
  List<T> getEntries(AbstractType<T> inType, Optional<EntryKey> inParent,
                     String inKey, String inValue)
  {
    return (List<T>)
      convert(m_data.getEntities(escapeType(inType.toString()),
                                 convert(inParent), 0, 1000, inKey, inValue));
  }

  /**
   * Get all the ids of a specific type, sorted and navigable.
   *
   * @param       inType   the type of entries to get ids for
   * @param       inParent the key of the parent, if any
   *
   * @return      all the ids
   */
  public List<String> getIDs(AbstractType<?> inType,
                             Optional<EntryKey> inParent)
  {
    return m_data.getIDs(escapeType(inType.toString()), inType.getSortField(),
                         convert(inParent));
  }

  /**
   * Get all the ids of a specific type, sorting by last change.
   *
   * @param       <T>      the real type of entries to get
   * @param       inType   the type of entries to get ids for
   * @param       inParent the key of the parent entry
   *
   * @return      all the ids
   */
  public <T extends AbstractEntry>
  List<T> getRecentEntries(AbstractType<T> inType, Optional<EntryKey> inParent)
  {
    return convert(m_data.getRecentEntities(escapeType(inType.toString()),
                                            BaseCharacter.MAX_PRODUCTS + 1,
                                            convert(inParent)));
  }

  /**
   * Get the owners and products for a given base product.
   *
   * @param    inID the id of the base product
   *
   * @return   a multi map from owner to ids
   */
  public ListMultimap<String, String> getOwners(String inID)
  {
    Log.debug("getting owners for " + inID);
    ListMultimap<String, String> owners = ArrayListMultimap.create();
    for(Entity entity : m_data.getIDs(escapeType(Product.TYPE.toString()),
                                      "bases", inID.toLowerCase(Locale.US)))
      owners.put(entity.getKey().getParent().getName(),
                 entity.getKey().getName());

    return owners;
  }

  /**
   * Get the entries for the given index.
   *
   * @param    inIndex  the name of the index to get
   * @param    inType   the type of entries to return for the index (app engine
   *                    can only do filter on queries with kind)
   * @param    inParent the parent key, if any
   * @param    inGroup  the group to get entries for
   * @param    inStart  the 0 based index of the first entry to return
   * @param    inSize   the maximal number of entries to return
   *
   * @return   the entries matching the given index
   */
  public List<AbstractEntry> getIndexEntries(String inIndex,
                                             AbstractType<?> inType,
                                             Optional<EntryKey> inParent,
                                             String inGroup,
                                             int inStart, int inSize)
  {
    List<AbstractEntry> entries = new ArrayList<AbstractEntry>();

    for(Entity entity : m_data.getEntities(escapeType(inType.toString()),
                                           convert(inParent),
                                           inStart, inSize,
                                           Index.PREFIX + inIndex, inGroup))
    {
      Optional<AbstractEntry> entry = convert(entity);
      if(entry.isPresent())
        entries.add(entry.get());
    }

    return entries;
  }

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
   */
  @Deprecated
  @SuppressWarnings("unchecked") // need to cast from property value
  public SortedSet<String> getIndexNames
  (String inIndex,
   AbstractType<? extends AbstractEntry> inType, boolean inCached,
   String ... inFilters)
  {
    SortedSet<String> names = new TreeSet<String>();

    for(Entity entity : m_data.getEntities(escapeType(inType.toString()),
                                           Optional.<Key>absent(),
                                           0, 10000, inFilters))

    {
      List<String> values = (List<String>)
        entity.getProperty(m_data.toPropertyName(Index.PREFIX + inIndex));

      if(values == null)
        continue;

      for(String value : values)
        names.add(value);
    }

    return names;
  }

  /**
   * Get the value for the given fields.
   *
   * @param       inType   the type of entries to look for
   * @param       inFields the fields to return
   *
   * @return      a list of records found, each with values for each field,
   *              in the order they were specificed
   */
  public List<List<String>> getMultiValues
  (AbstractType<? extends AbstractEntry> inType, String ... inFields)
  {
    return m_data.getMultiValues(escapeType(inType.toString()),
                                 Optional.<Key>absent(), inFields);
  }

  /**
   * Get the value for the given fields.
   *
   * @param       inType   the type of entries to look for
   * @param       inField  the field to return
   *
   * @return      a list of records found, each with values for each field,
   *              in the order they were specificed
   */
  public SortedSet<String> getValues
  (AbstractType<? extends AbstractEntry> inType, String inField)
  {
    return m_data.getValues(escapeType(inType.toString()),
                            Optional.<Key>absent(), inField);
  }

  /**
   * Check if any of the data has been changed and needs saving.
   *
   * @return      true if data is changed from store, false if not
   */
  @Deprecated
  public boolean isChanged()
  {
    return false;
  }

  /**
   * Remove the described entity from the datastore.
   *
   * @param       inEntry the entry to remove
   * @return      true if removed, false if not
   */
  public boolean remove(AbstractEntry inEntry)
  {
    return m_data.remove(convert(inEntry.getKey()));
  }

  /**
   * Add an entry to the store.
   *
   * @param       inEntry the entry to add
   *
   * @return      true if added, false if there was an error
   */
  public boolean update(AbstractEntry inEntry)
  {
    if(inEntry.getName().equals(Entry.TEMPORARY) && inEntry instanceof Entry)
    {
      // determine a new, real id to use; this should actually be in a
      // transaction to be safe...
      ((Entry)inEntry).complete();
    }

    return m_data.update(convert(inEntry));
  }

  /**
   * Save the given entry.
   *
   * @param       inEntry the entry to save
   *
   * @return      true if saved, false if not
   *
   */
  public boolean save(AbstractEntry inEntry)
  {
    return update(inEntry);
  }

  /**
   * Rebuild the given type. This means mainly rebuilding the indexes. It is
   * accomplished by reading all entries and writing them back.
   *
   * NOTE: this produces a lot of datastore traffic.
   *
   * @param      inType  the type to rebuild for
   *
   * @return     the numbert of enties updated
   *
   */
  public int rebuild(AbstractType<? extends AbstractEntry> inType)
  {
    Log.debug("rebuilding data for " + inType);

    int count = 0;
    for(Entity entity : m_data.getEntities(inType.toString(),
                                           Optional.<Key>absent(),
                                           Optional.<String>absent(),
                                           0, 10000))
    {
      Optional<AbstractEntry> entry = convert(entity);
      if(entry.isPresent())
        m_data.update(convert(entry.get()));
      count++;
    }

    return count;
  }

  /**
   * Rebuild the given type. This means mainly rebuilding the indexes. It is
   * accomplished by reading all entries and writing them back.
   *
   * NOTE: this produces a lot of datastore traffic.
   *
   * @param      inType    the type to rebuild for
   * @param      inRequest the original request
   *
   * @return     the numbert of enties updated
   */
  public int refresh(AbstractType<? extends AbstractEntry> inType,
                     DMARequest inRequest)
  {
    Log.debug("refresh data for " + inType);

    int count = 0;
    int chunk = 10;
    for(int start = 0; count < 10000; start += chunk)
    {
      if(inRequest.timeIsRunningOut())
        break;

      List<Entity> entities =
          m_data.getEntities(escapeType(inType.toString()),
                             Optional.<Key>absent(),
                             Optional.<String>absent(),
                             start, chunk);

      for(Entity entity : entities)
      {
        if(inRequest.timeIsRunningOut())
          break;

        Optional<AbstractEntry> entry = convert(entity);
        if(!entry.isPresent())
          continue;

        Entity converted  = convert(entry.get());
        if (equals(entity, converted))
          continue;

        m_data.update(converted);

        if (!entity.getKey().equals(converted.getKey()))
          m_data.remove(entity.getKey());

        count++;
      }

      if(entities.size() < chunk)
        break;
    }

    return count;
  }

  /**
   * Check whether the two given entities are equal.
   *
   * @param       inFirst   the first entity to check
   * @param       inSecond  the second entity to check
   *
   * @return      true if the entities are equal, false if not
   */
  private boolean equals(Entity inFirst, Entity inSecond)
  {
    if(!inFirst.equals(inSecond))
      return false;

    if(!propertyEquals(inFirst, inSecond))
      return false;

    return true;
  }

  /**
   * Check if the two given entities have equal properties.
   *
   * @param       inFirst  the first entity
   * @param       inSecond the second entity
   *
   * @return      true if all the properties are equals, false if not
   */
  private boolean propertyEquals(Entity inFirst, Entity inSecond)
  {
    for(Map.Entry<String, Object> entry : inFirst.getProperties().entrySet())
    {
      // Ignore the changed field.
      if("change".equals(entry.getKey()))
        continue;

      if(entry.getValue() != null
         && (!inSecond.hasProperty(entry.getKey())
             || !entry.getValue().equals(inSecond.getProperty(entry.getKey()))))
        return false;
    }

    for(String key : inSecond.getProperties().keySet())
      if(!inFirst.hasProperty(key))
        return false;

    return true;
  }

  /**
   * Convert the given entry key into a corresponding entity key.
   *
   * @param       inKey the key to convert
   *
   * @return      the converted key
   */
  public Optional<Key> convert(Optional<EntryKey> inKey)
  {
    if(!inKey.isPresent())
      return Optional.absent();

    return Optional.of(convert(inKey.get()));
  }

  /**
   * Convert the given key to an datastore key.
   *
   * @param inKey the entry key to convert
   * @return the converted entity key
   */
  public Key convert(EntryKey inKey)
  {
    Optional<EntryKey> parent = inKey.getParent();
    if(parent.isPresent())
      return KeyFactory.createKey(
          convert(parent).get(),
          escapeType(inKey.getType().toString()),
          inKey.getID().toLowerCase(Locale.US));
    else
    {
      if(inKey.getID().isEmpty())
        throw new IllegalArgumentException("name empty for " + inKey);
      return KeyFactory.createKey(
          escapeType(inKey.getType().toString()),
          inKey.getID().toLowerCase(Locale.US));
    }
  }

  /**
   * Convert the given entry key into a corresponding entity key.
   *
   * @param       inKey the key to convert
   *
   * @return      the converted key
   */
  public Optional<EntryKey> convert(Key inKey)
  {
    Key parent = inKey.getParent();
    Optional<? extends AbstractType<? extends AbstractEntry>> type =
        AbstractType.getTyped(unescapeType(inKey.getKind()));

    if(!type.isPresent())
      return Optional.absent();

    if(parent != null)
    {
      Optional<EntryKey> parentKey = convert(parent);
      return Optional.of(new EntryKey(inKey.getName(), type.get(), parentKey));
    }

    return Optional.of(new EntryKey(inKey.getName(), type.get()));
  }

  /**
   * Convert the given datastore entity into a dma entry.
   *
   * @param      inID     the id of the entry to convert
   * @param      inType   the type of the entry to convert
   * @param      inEntity the entity to convert
   * @param      <T>      the type of the entry to convert
   *
   * @return     the converted entry, if any
   */
  @SuppressWarnings("unchecked")
  public <T extends AbstractEntry> Optional<T>
    convert(String inID, AbstractType<T> inType, Entity inEntity)
  {
    Tracer tracer = new Tracer("converting " + inID);

    Log.debug("converting entity " + inID + " to " + inType);

    Optional<T> entry = inType.create(inID);
    if(!entry.isPresent())
    {
      Log.warning("cannot create conversion " + inType + " entity with id "
                  + inID + ": " + inEntity);
      tracer.done("cannot create");
      return entry;
    }

    Tracer parsing = new Tracer("parsing " + inID);
    Blob blob = (Blob)inEntity.getProperty("proto");
    parsing.done("blob property reading");
    if (blob != null)
      entry.get().parseFrom(blob.getBytes());
    parsing.done("parsing");

    // update any key related value
    Optional<EntryKey> key = convert(inEntity.getKey());
    if(key.isPresent())
    entry.get().updateKey(key.get());

    tracer.done("uncached");
    return entry;
  }

  /**
   * Convert the given datastore entity into a dma entry.
   *
   * @param      inEntity the entity to convert
   * @param      <T>      the type of entry converted
   *
   * @return     the entry found, if any
   */
  public <T extends AbstractEntry> Optional<T>
    convert(Entity inEntity)
  {
    if(inEntity == null)
      return Optional.absent();

    Key key = inEntity.getKey();
    String id = key.getName();
    @SuppressWarnings("unchecked")
    Optional<? extends AbstractType<? extends AbstractEntry>> type =
        AbstractType.getTyped(unescapeType(key.getKind()));

    if(!type.isPresent() || id == null)
    {
      Log.warning("cannot properly extract type or id: " + type + "/" + id
                  + " - " + key);
      return null;
    }

    return (Optional<T>) convert(id, type.get(), inEntity);
  }

  /**
   * Convert the given datastore entities into a dma entries.
   *
   * @param      inEntities the entities to convert
   * @param      <T>         the type of entries converted to
   *
   * @return     the entries found, if any
   */
  @SuppressWarnings("unchecked")
  public <T extends AbstractEntry> List<T> convert(List<Entity> inEntities)
  {
    List<T> entries = new ArrayList<>();

    for(Entity entity : inEntities)
    {
      Optional<T> entry = convert(entity);
      if(entry.isPresent())
      entries.add(entry.get());
    }

    return entries;
  }

  /**
   * Convert the given dma entry into a datastore entity.
   *
   * @param      inEntry the entry to convert
   *
   * @return     the entry found, if any
   */
  @SuppressWarnings({ "rawtypes" })
  public Entity convert(AbstractEntry inEntry)
  {
    Entity entity = new Entity(convert(inEntry.getKey()));

    // Save searchable values as distinct properties to be able to search
    // for them in the datastore.
    for(Map.Entry<String, Object> entry
        : inEntry.collectSearchables().entrySet())
      entity.setProperty(entry.getKey(), entry.getValue());

    // Save the index information to make it searchable afterwards.
    Multimap<Index.Path, String> indexes = inEntry.computeIndexValues();
    for(Index.Path index : indexes.keySet())
      // must convert the contained set to a list to make it serializable
      entity.setProperty(m_data.toPropertyName("index-" + index.getPath()),
                         new ArrayList<String>(indexes.get(index)));

    // Save the time for recent changes.
    entity.setProperty(m_data.toPropertyName("change"), new Date());

    entity.setProperty("proto", new Blob(inEntry.toProto().toByteArray()));
    return entity;
  }

  /**
   * Escape the given type for storage. This means to replace spaces with
   * underscores.
   *
   * @param   inType the type to escape
   *
   * @return  the escaped type
   */
  public String escapeType(String inType)
  {
    return inType.replace(" ", "_");
  }

  /**
   * Unescape the type from data storage. This replaces underscores with spaces.
   *
   * @param    inType type to unescape
   *
   * @return   the unescaped type
   */
  public String unescapeType(String inType)
  {
    return inType.replace("_", " ");
  }
}
