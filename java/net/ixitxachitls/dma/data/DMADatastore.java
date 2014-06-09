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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.entries.Product;
import net.ixitxachitls.dma.entries.Variable;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.dma.values.File;
import net.ixitxachitls.util.Tracer;
import net.ixitxachitls.util.logging.Log;

/**
 * Wrapper for accessing data from app engine's datastore.
 * TODO: it would be nice to use a chache for entries here. Unfortunately, the
 * guava cache cannot be used on appengine and the memcache required
 * serializable objects and copies them for getting.
 *
 * @file          DMADatastore.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@ParametersAreNonnullByDefault
public class DMADatastore
{
  /**
   * Create the datastore.
   */
  public DMADatastore()
  {
    m_blobs = BlobstoreServiceFactory.getBlobstoreService();
    ImagesServiceFactory.getImagesService();
  }

  /** The access to the datastore. Don't use this except in the AdminServlet! */
  private DataStore m_data = new DataStore();

  /** The blob store service. */
  private BlobstoreService m_blobs;

  /** The id for serialization. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 1L;

  /** The cache for entries. */
  private static MemcacheService s_entryCache =
    MemcacheServiceFactory.getMemcacheService("entry");

  /**
   * Get an entry denoted by type and id and their respective parents.
   *
   * @param      inKey  the key to the entry to get
   * @param      <T>    the type of the entry to get
   *
   * @return     the entry found, if any
   */
  public @Nullable AbstractEntry getEntry(EntryKey inKey)
  {
    AbstractEntry entry = (AbstractEntry)s_entryCache.get(inKey.toString());
    if(entry == null)
    {
      Log.debug("gae: getting entry for " + inKey);
      entry = convert(inKey.getID(), inKey.getType(),
                      m_data.getEntity(convert(inKey)));
      //s_entryCache.put(inKey.toString(), entry);
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
  public <T extends AbstractEntry>
  List<T> getEntries(AbstractType<T> inType,
                     @Nullable EntryKey inParent,
                     int inStart, int inSize)
  {
    List<T> entries = new ArrayList<>();
    Iterable<Entity> entities =
      m_data.getEntities(escapeType(inType.toString()), convert(inParent),
                         inType.getSortField(), inStart, inSize);

    for(Entity entity : entities)
      entries.add((T)convert(entity));

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
  @SuppressWarnings("unchecked") // casting return
  public @Nullable <T extends AbstractEntry>
  T getEntry(AbstractType<T> inType, String inKey, String inValue)
  {
    Log.debug("Getting entry for " + inKey + "=" + inValue);
    return (T)convert(m_data.getEntity(escapeType(inType.toString()),
                                       inKey, inValue));
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
   * @return     the entries found
   */
  @SuppressWarnings("unchecked") // casting return
  public @Nullable <T extends AbstractEntry>
  List<T> getEntries(AbstractType<T> inType, @Nullable EntryKey inParent,
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
                             @Nullable EntryKey inParent)
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
  List<T> getRecentEntries(AbstractType<T> inType, @Nullable EntryKey inParent)
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
  public Multimap<String, String> getOwners(String inID)
  {
    Multimap<String, String> owners = HashMultimap.create();
    for(Entity entity : m_data.getIDs(escapeType(Product.TYPE.toString()),
                                      "base", inID.toLowerCase(Locale.US)))
      owners.put(entity.getKey().getParent().getName(),
                 entity.getKey().getName());

    return owners;
  }

  /**
   * Get the entries for the given index.
   *
   * @param    <T>      The type of the entries to get
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
                                             @Nullable EntryKey inParent,
                                             String inGroup,
                                             int inStart, int inSize)
  {
    List<AbstractEntry> entries = new ArrayList<AbstractEntry>();

    for(Entity entity : m_data.getEntities(escapeType(inType.toString()),
                                           convert(inParent),
                                           inStart, inSize,
                                           Index.PREFIX + inIndex, inGroup))
      entries.add(convert(entity));

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

    for(Entity entity : m_data.getEntities(escapeType(inType.toString()), null,
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
    return m_data.getMultiValues(escapeType(inType.toString()), null, inFields);
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
    return m_data.getValues(escapeType(inType.toString()), null, inField);
  }

  /**
   * Caches the given entry (or updates what is in the cache).
   * Does _NOT_ change what is stored in the data store.
   *
   * @param       inEntry the entry to cache
   */
  public void cacheEntry(AbstractEntry inEntry)
  {
    //s_entryCache.put(inEntry.getKey().toString(), inEntry);
  }

  /**
   * Remove the entry with the given key from the cache.
   *
   * @param      inKey the key of the entry to remove
   * @param      <T>   the type of entry to uncache
   */
  public void uncacheEntry(EntryKey inKey)
  {
    s_entryCache.delete(inKey.toString());
  }

  /**
   * Clear the cache of entries.
   */
  public void clearCache()
  {
    s_entryCache.clearAll();
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
    // also remove all blobs for this entry
    for(File file : inEntry.getFiles())
    {
      m_blobs.delete(new BlobKey(file.getPath().replace("//file/",  "")));
      Log.important("deleted file " + file.getPath() + " for "
                    + inEntry.getKey());
    }

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

    //s_entryCache.put(inEntry.getKey().toString(), inEntry);
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
    for(Entity entity : m_data.getEntities(inType.toString(), null, null,
                                           0, 10000))
    {
      m_data.update(convert(convert(entity)));
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
        m_data.getEntitiesList(escapeType(inType.toString()), null, null,
                               start, chunk);

      for(Entity entity : entities)
      {
        if(inRequest.timeIsRunningOut())
          break;

        Entity converted  = convert(convert(entity));
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
   * @param <T>
   *
   * @param       inKey the key to convert
   * @param       <T>   the type of entry to convert
   *
   * @return      the converted key
   */
  public @Nullable Key convert(@Nullable EntryKey inKey)
  {
    if(inKey == null)
      return null;

    Optional<EntryKey> parent = inKey.getParent();
    if(parent.isPresent())
      return KeyFactory.createKey(convert(parent.get()),
                                  escapeType(inKey.getType().toString()),
                                  inKey.getID().toLowerCase(Locale.US));
    else
      return KeyFactory.createKey(escapeType(inKey.getType().toString()),
                                  inKey.getID().toLowerCase(Locale.US));
  }

  /**
   * Convert the given entry key into a corresponding entity key.
   *
   * @param       inKey the key to convert
   * @param       <T>   the type of entry to convert
   *
   * @return      the converted key
   */
  public EntryKey convert(Key inKey)
  {
    Key parent = inKey.getParent();

    if(parent != null)
    {
      Optional<EntryKey> parentKey =
        Optional.of(convert(parent));
      return new EntryKey(inKey.getName(),
                          AbstractType
                          .getTyped(escapeType(inKey.getKind()))
                          , parentKey);
    }

    return new EntryKey(inKey.getName(),
                        AbstractType
                          .getTyped(escapeType(inKey.getKind())));
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
  public <T extends AbstractEntry>
  T convert(String inID, AbstractType<T> inType, @Nullable Entity inEntity)
  {
    if(inEntity == null)
      return null;

    Tracer tracer = new Tracer("converting " + inID);

    T entry = (T)s_entryCache.get(convert(inEntity.getKey()).toString());
    if (entry != null)
    {
      tracer.done("cached");
      return entry;
    }

    Log.debug("converting entity " + inID + " to " + inType);

    entry = inType.create(inID);
    if(entry == null)
    {
      Log.warning("cannot create conversion " + inType + " entity with id "
                  + inID + ": " + inEntity);
      tracer.done("cannot create");
      return null;
    }

    Blob blob = (Blob)inEntity.getProperty("proto");
    if (blob != null)
      entry.parseFrom(blob.getBytes());

//    if(entry instanceof BaseSkill)
//    {
//      BaseSkill skill = (BaseSkill)entry;
//      for(Map.Entry<String, Object> property
//        : inEntity.getProperties().entrySet())
//      {
//        String name = m_data.fromPropertyName(property.getKey());
//        if(name.startsWith(Index.PREFIX) || "change".equals(name)
//          || "extensions".equals(name) || "proto".equals(name))
//          continue;
//
//        Object value = property.getValue();
//        if(value == null)
//          continue;
//
//        switch(name)
//        {
//          case "name":
//            skill.m_name = value.toString();
//            break;
//
//          case "base":
//            for(String base : (List<String>)value)
//              skill.m_base.add(base);
//            break;
//
//          case "categories":
//            for(String category : (List<String>)value)
//              skill.m_categories.add(category);
//            break;
//
//          case "incomplete":
//            skill.m_incomplete = ((Text)value).getValue().replace("\"", "");
//            break;
//
//          case "check":
//            if(!value.toString().startsWith("$"))
//              skill.m_check = Optional.of(((Text)value).getValue().replace("\"", ""));
//            break;
//
//          case "action":
//            if(!value.toString().startsWith("$"))
//              skill.m_action = Optional.of(((Text)value).getValue().replace("\"", ""));
//            break;
//
//          case "retry":
//            if(!value.toString().startsWith("$"))
//              skill.m_retry = Optional.of(((Text)value).getValue().replace("\"", ""));
//            break;
//
//          case "special":
//            if(!value.toString().startsWith("$"))
//              skill.m_special = Optional.of(((Text)value).getValue().replace("\"", ""));
//            break;
//
//          case "restriction":
//            if(!value.toString().startsWith("$"))
//              skill.m_restriction = Optional.of(((Text)value).getValue().replace("\"", ""));
//            break;
//
//          case "untrained":
//            if(!value.toString().startsWith("$"))
//              skill.m_untrained = Optional.of(value.toString().replace("\"", ""));
//            break;
//
//         case "references":
//            for(String reference : (List<String>)value)
//              if(ProductReference.PARSER.parse(reference.split(":")).isPresent())
//                skill.m_references.add
//                (ProductReference.PARSER.parse(reference.split(":")).get());
//
//            break;
//
//          case "short_description":
//            skill.m_short = value.toString().replace("\"", "");
//            break;
//
//          case "synonyms":
//            for(String synonym : (List<String>)value)
//              skill.m_synonyms.add(synonym.replace("\"", ""));
//            break;
//
//          case "synergies":
//            for(String synergy: (List<String>)value)
//              skill.m_synergies.add(synergy.replace("\"", ""));
//            break;
//
//          case "ability":
//            if(BaseMonster.Ability.fromString(value.toString()).isPresent())
//              skill.m_ability =
//                BaseMonster.Ability.fromString(value.toString()).get();
//            break;
//
//          case "restrictions":
//            for(String synergy: (List<String>)value)
//              if(SkillRestriction.fromString(value.toString()).isPresent())
//                skill.m_restrictions.add
//                (SkillRestriction.fromString(value.toString()).get());
//            break;
//
//          case "modifiers":
//            for(String synergy: (List<String>)value)
//              if(SkillModifier.fromString(value.toString()).isPresent())
//                skill.m_modifiers.add
//                (SkillModifier.fromString(value.toString()).get());
//            break;
//
//          case "dc":
//            for(String dc: (List<String>)value)
//            {
//              String []parts = Strings.getPatterns(dc, "(\\d+)\\s+\"(.*)\"");
//              skill.m_dcs.add(BaseSkill.DC.PARSER.parse(parts).get());
//            }
//            break;
//
//          case "worlds":
//            for(String world : (List<String>)value)
//              skill.m_worlds.add(world);
//            break;
//
//          case "description":
//            skill.m_description = ((Text)value).getValue().replace("\"", "");
//            break;
//
//
//        }
//      }
//    }

    // update any key related value
    EntryKey key = convert(inEntity.getKey());
    entry.updateKey(key);

    //s_entryCache.put(entry.getKey().toString(), entry);
    tracer.done("uncached");
    return entry;
  }

  /**
   * Convert the given datastore entity into a dma entry.
   *
   * @param      inEntity the entity to convert
   *
   * @return     the entry found, if any
   */
  public @Nullable <T extends AbstractEntry>
  T convert(@Nullable Entity inEntity)
  {
    if(inEntity == null)
      return null;

    Key key = inEntity.getKey();
    String id = key.getName();
    @SuppressWarnings("unchecked")
    AbstractType<T> type = (AbstractType<T>)
      AbstractType.getTyped(unescapeType(key.getKind()));

    if(type == null || id == null)
    {
      Log.warning("cannot properly extract type or id: " + type + "/" + id
                  + " - " + key);
      return null;
    }

    return convert(id, type, inEntity);
  }

  /**
   * Convert the given datastore entities into a dma entries.
   *
   * @param      inEntities the entities to convert
   *
   * @return     the entries found, if any
   */
  @SuppressWarnings("unchecked")
  public @Nullable <T extends AbstractEntry>
  List<T> convert(List<Entity> inEntities)
  {
    List<T> entries = new ArrayList<>();

    for(Entity entity : inEntities)
      entries.add((T)convert(entity));

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

    // TODO: remove this once all searchable values are gone.
    for(Variable variable : inEntry.getVariables())
      if(variable.isSearchable())
        entity.setProperty(variable.getKey(),
                           variable.get(inEntry).toString().toLowerCase());

    // Save searchable values as distinct properties to be able to search
    // for them in the datastore.
    for(Map.Entry<String, Object> entry :
      inEntry.collectSearchables().entrySet())
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
