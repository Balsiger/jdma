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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.entries.Product;
import net.ixitxachitls.dma.entries.Variable;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.util.Tracer;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * Wrapper for accessing data from app engine's datastore.
 * TODO: it would be nice to use a chache for entries here. Unfortunately, the
 * guava cache cannot be used on appengine and the memcache required
 * serializable objects and copies them for getting.
 *
 * @file          DMADatastore.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
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

  /** The access to the datastore. Don't use this except in the AdminServlet! */
  private DataStore m_data = new DataStore();

  /** The access to the datastore. */
  private DatastoreService m_store;

  /** The blob store service. */
  private BlobstoreService m_blobs;

  /** The image service to serve images. */
  private ImagesService m_image;

  /** The id for serialization. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 1L;

  /** The cache for entries. */
  private static MemcacheService s_entryCache =
    MemcacheServiceFactory.getMemcacheService("entry");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getEntry -------------------------------

  /**
   * Get an entry denoted by type and id and their respective parents.
   *
   * @param      inKey  the key to the entry to get
   *
   * @param      <T>    the type of the entry to get
   *
   * @return     the entry found, if any
   *
   */
  @SuppressWarnings("unchecked")
  @Override
  public @Nullable <T extends AbstractEntry> T getEntry
                      (AbstractEntry.EntryKey<T> inKey)
  {
    AbstractEntry entry = (AbstractEntry)s_entryCache.get(inKey.toString());
    if(entry == null)
    {
      Log.debug("gae: getting entry for " + inKey);
      entry = convert(inKey.getID(), inKey.getType(),
                      m_data.getEntity(convert(inKey)));
      //s_entryCache.put(inKey.toString(), entry);
    }

    return (T)entry;
  }

  //........................................................................
  //----------------------------- getEntries -------------------------------

  /**
   * Gets all the entries of a specific type.
   *
   * @param    <T>      the type of entry to get
   * @param    inType   the type of entries to get
   * @param    inParent the key of the parent, if any
   * @param    inStart  the starting number of entires to get (starts as 0)
   * @param    inSize   the maximal number of entries to return
   *
   * @return   a list with all the entries
   *
   */
  @Override
  @SuppressWarnings("unchecked") // need to cast
  public <T extends AbstractEntry> List<T> getEntries
            (AbstractType<T> inType,
             @Nullable AbstractEntry.EntryKey
             <? extends AbstractEntry> inParent,
             int inStart, int inSize)
  {
    List<T> entries = new ArrayList<T>();
    Iterable<Entity> entities =
      m_data.getEntities(escapeType(inType.toString()), convert(inParent),
                         inType.getSortField(), inStart, inSize);

    for(Entity entity : entities)
      entries.add((T)convert(entity));

    return entries;
  }

  //........................................................................
  //------------------------------- getEntry -------------------------------

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
   *
   */
  @Override
  @SuppressWarnings("unchecked") // casting return
  public @Nullable <T extends AbstractEntry> T getEntry
                      (AbstractType<T> inType, String inKey, String inValue)
  {
    Log.debug("Getting entry for " + inKey + "=" + inValue);
    return (T)convert(m_data.getEntity(escapeType(inType.toString()),
                                       inKey, inValue));
  }

  //........................................................................
  //------------------------------ getEntries ------------------------------

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
   *
   */
  @Override
  @SuppressWarnings("unchecked") // casting return
  public @Nullable <T extends AbstractEntry> List<T> getEntries
                      (AbstractType<T> inType, String inKey, String inValue)
  {
    return (List<T>)
      convert(m_data.getEntities(escapeType(inType.toString()), null, 0, 1000,
                                 inKey, inValue));
  }

  //........................................................................
  //-------------------------------- getIDs --------------------------------

  /**
   * Get all the ids of a specific type, sorted and navigable.
   *
   * @param       inType   the type of entries to get ids for
   * @param       inParent the key of the parent, if any
   *
   * @return      all the ids
   *
   */
  @Override
  public List<String> getIDs
    (AbstractType<? extends AbstractEntry> inType,
     @Nullable AbstractEntry.EntryKey<? extends AbstractEntry> inParent)
  {
    return m_data.getIDs(escapeType(inType.toString()), inType.getSortField(),
                         convert(inParent));
  }

  //........................................................................
  //---------------------------- getRecentEntries --------------------------

  /**
   * Get all the ids of a specific type, sorting by last change.
   *
   * @param       <T>      the real type of entries to get
   * @param       inType   the type of entries to get ids for
   * @param       inParent the key of the parent entry
   *
   * @return      all the ids
   *
   */
  @Override
  @SuppressWarnings("unchecked") // need to cast cache value
  public <T extends AbstractEntry> List<T> getRecentEntries
    (AbstractType<T> inType,
     @Nullable AbstractEntry.EntryKey<? extends AbstractEntry> inParent)
  {
    return (List<T>)
      convert(m_data.getRecentEntities(escapeType(inType.toString()),
                                       BaseCharacter.MAX_PRODUCTS + 1,
                                       convert(inParent)));
  }

  //........................................................................
  //------------------------------ getOwners -------------------------------

  /**
   * Get the owners and products for a given base procut.
   *
   * @param    inID the id of the base product
   *
   * @return   a multi map from owner to ids
   *
   */
  @Override
  public Multimap<String, String> getOwners(String inID)
  {
    Multimap<String, String> owners = HashMultimap.create();
    for(Entity entity : m_data.getIDs(escapeType(Product.TYPE.toString()),
                                      "base", inID.toLowerCase(Locale.US)))
      owners.put(entity.getKey().getParent().getName(),
                 entity.getKey().getName());

    return owners;
  }

  //........................................................................
  //------------------------------- getFiles -------------------------------

  /**
   * Get the files for the given entry.
   *
   * @param    inEntry       the entry for which to get files
   * @param    inIncludeBase whether to include files from base entries or not
   *
   * @return   a list of all the files found
   *
   */
  @Override
  public List<File> getFiles(AbstractEntry inEntry, boolean inIncludeBase)
  {
    List<File> files = Lists.newArrayList();
    Set<String> names = Sets.newHashSet();
    for(Entity entity : m_data.getEntities("file", convert(inEntry.getKey()),
                                           "__key__", 0, 100))
    {
      String name = (String)entity.getProperty("name");
      if(names.contains(name))
        name = inEntry.getName() + "-" + name;
      names.add(name);

      String type = (String)entity.getProperty("type");
      String path = (String)entity.getProperty("path");
      String icon = null;
      if(type == null)
        type = "image/png";

      if(type.startsWith("image/"))
      {
        try
        {
          icon = m_image.getServingUrl(ServingUrlOptions.Builder.withBlobKey
                                       (new BlobKey(path)));
        }
        catch(IllegalArgumentException e)
        {
          Log.error("Cannot obtain serving url for '" + path + "': " + e);
          continue;
        }
      }
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
    if(inIncludeBase)
      for(AbstractEntry entry : inEntry.getBaseEntries())
        if(entry != null)
          files.addAll(getFiles(entry, true));

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
   * @param    inParent the parent key, if any
   * @param    inGroup  the group to get entries for
   * @param    inStart  the 0 based index of the first entry to return
   * @param    inSize   the maximal number of entries to return
   *
   * @return   the entries matching the given index
   *
   */
  @Override
  @SuppressWarnings("unchecked") // need to cast return value for generics
  public <T extends AbstractEntry> List<T> getIndexEntries
    (String inIndex, AbstractType<T> inType,
     @Nullable AbstractEntry.EntryKey<? extends AbstractEntry> inParent,
     String inGroup, int inStart, int inSize)
  {
    List<AbstractEntry> entries = new ArrayList<AbstractEntry>();

    for(Entity entity : m_data.getEntities(escapeType(inType.toString()),
                                           convert(inParent),
                                           inStart, inSize,
                                           Index.PREFIX + inIndex, inGroup))
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
  @Override
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

  //........................................................................
  //------------------------------ getValues -------------------------------

  /**
   * Get the value for the given fields.
   *
   * @param       inType   the type of entries to look for
   * @param       inFields the fields to return
   *
   * @return      a list of records found, each with values for each field,
   *              in the order they were specificed
   *
   */
  @Override
  public List<List<String>> getMultiValues
    (AbstractType<? extends AbstractEntry> inType, String ... inFields)
  {
    return m_data.getMultiValues(escapeType(inType.toString()), null, inFields);
  }

  //........................................................................
  //------------------------------ getValues -------------------------------

  /**
   * Get the value for the given fields.
   *
   * @param       inType   the type of entries to look for
   * @param       inField  the field to return
   *
   * @return      a list of records found, each with values for each field,
   *              in the order they were specificed
   *
   */
  @Override
  public SortedSet<String> getValues
    (AbstractType<? extends AbstractEntry> inType, String inField)
  {
    return m_data.getValues(escapeType(inType.toString()), null, inField);
  }

  //........................................................................
  //------------------------------ cacheEntry ------------------------------

  /**
   * Caches the given entry (or updates what is in the cache).
   * Does _NOT_ change what is stored in the data store.
   *
   * @param       inEntry the entry to cache
   */
  @Override
  public void cacheEntry(AbstractEntry inEntry)
  {
    //s_entryCache.put(inEntry.getKey().toString(), inEntry);
  }

  //........................................................................
  //----------------------------- uncacheEntry -----------------------------

  /**
   * Remove the entry with the given key from the cache.
   *
   * @param      inKey the key of the entry to remove
   * @param      <T>   the type of entry to uncache
   */
  public <T extends AbstractEntry> void uncacheEntry
            (AbstractEntry.EntryKey<T> inKey)
  {
    s_entryCache.delete(inKey.toString());
  }

  //........................................................................
  //------------------------------ clearCache ------------------------------

  /**
   * Clear the cache of entires.
   */
  @Override
  public void clearCache()
  {
    s_entryCache.clearAll();
  }

  //........................................................................

  //------------------------------ isChanged -------------------------------

  /**
   * Check if any of the data has been changed and needs saving.
   *
   * @return      true if data is changed from store, false if not
   *
   */
  @Override
  @Deprecated
  public boolean isChanged()
  {
    return false;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- remove --------------------------------

  /**
   * Remove the described entity from the datastore.
   *
   * @param       inID    the id of the entry to remove
   * @param       inType  the type of the entry to remove
   *
   * @return      true if removed, false if not
   *
   */
  @Override
  public boolean remove(String inID,
                        AbstractType<? extends AbstractEntry> inType)
  {
    return m_data.remove(KeyFactory.createKey(inType.toString(),
                                              inID.toLowerCase(Locale.US)));
  }

  //........................................................................
  //-------------------------------- remove --------------------------------

  /**
   * Remove the described entity from the datastore.
   *
   * @param       inKey   the key of the entry to delete
   *
   * @return      true if removed, false if not
   *
   */
  @Override
  public boolean remove(AbstractEntry.EntryKey<?> inKey)
  {
    // also remove all blobs for this entry
    for(Entity entity : m_data.getEntities("file", convert(inKey), "__key__",
                                           0, 1000))
    {
      m_blobs.delete(new BlobKey((String)entity.getProperty("path")));
      m_data.remove(KeyFactory.createKey(convert(inKey), "file",
                                         (String)entity.getProperty("name")));
      Log.important("deleted file " + entity.getProperty("name") + " for "
                    + inKey);
    }

    return m_data.remove(convert(inKey));
  }

  //........................................................................
  //-------------------------------- update --------------------------------

  /**
   * Add an entry to the store.
   *
   * @param       inEntry the entry to add
   *
   * @return      true if added, false if there was an error
   *
   */
  @Override
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
  public boolean save(AbstractEntry inEntry)
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
  public void addFile(AbstractEntry inEntry, String inName, String inType,
                      BlobKey inKey)
  {
    Log.debug("adding file for " + inEntry.getType() + " " + inEntry.getName());
    // if a file with the same name is already there, we have to delete it first
    Key key = KeyFactory.createKey(convert(inEntry.getKey()), "file", inName);
    Entity entity = null;

      entity = m_data.getEntity(key);
      if(entity != null)
      {
        Log.important("replacing file " + inName + " for " + inEntry.getType()
                      + " " + inEntry.getName() + " [" + inKey + "]");
        m_blobs.delete(new BlobKey((String)entity.getProperty("path")));
        m_store.delete(key);
      }
      else
        entity = new Entity(key);

    entity.setProperty("path", inKey.getKeyString());
    entity.setProperty("name", inName);
    entity.setProperty("type", inType);
    m_data.update(entity);
  }

  //........................................................................
  //----------------------------- removeFile -------------------------------

  /**
   * Remove a file from the given entry.
   *
   * @param  inEntry the entry to add the file to
   * @param  inName  the name of the file
   *
   * @return true if the file was removed, false if not
   *
   */
  public boolean removeFile(AbstractEntry inEntry, String inName)
  {
    Key key = KeyFactory.createKey(convert(inEntry.getKey()), "file", inName);

    Entity entity = m_data.getEntity(key);
    if(entity != null)
    {
      m_blobs.delete(new BlobKey((String)entity.getProperty("path")));
      m_data.remove(key);
      Log.important("deleted file " + inName + " for " + inEntry.getType()
                    + " " + inEntry.getName());
      return true;
    }
    else
    {
      Log.warning("trying to delete noexistant file " + inName + " for "
                  + inEntry.getType() + " " + inEntry.getName());
      return false;
    }
  }

  //........................................................................

  //------------------------------- rebuild --------------------------------

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
  @Override
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

  //........................................................................
  //------------------------------- rebuild --------------------------------

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
   *
   */
  @Override
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

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //-------------------------------- equals --------------------------------

  /**
   * Check whether the two given entities are equal.
   *
   * @param       inFirst   the first entity to check
   * @param       inSecond  the second entity to check
   *
   * @return      true if the entities are equal, false if not
   *
   */
  private boolean equals(Entity inFirst, Entity inSecond)
  {
    if(!inFirst.equals(inSecond))
      return false;

    if(!propertyEquals(inFirst, inSecond))
      return false;

    return true;
  }

  //........................................................................

  //---------------------------- propertyEquals ----------------------------

  /**
   * Check if the two given entities have equal properties.
   *
   * @param       inFirst  the first entity
   * @param       inSecond the second entity
   *
   * @return      true if all the properties are equals, false if not
   *
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

  //........................................................................

  //------------------------------- convert --------------------------------

  /**
   * Convert the given entry key into a corresponding entity key.
   *
   * @param       inKey the key to convert
   * @param       <T>   the type of entry to convert
   *
   * @return      the converted key
   *
   */
  @SuppressWarnings("unchecked")
  public @Nullable <T extends AbstractEntry> Key convert
                      (@Nullable AbstractEntry.EntryKey<T> inKey)
  {
    if(inKey == null)
      return null;

    AbstractEntry.EntryKey<T> parent =
      (AbstractEntry.EntryKey<T>)inKey.getParent();
    if(parent != null)
      return KeyFactory.createKey(convert(parent),
                                  escapeType(inKey.getType().toString()),
                                  inKey.getID().toLowerCase(Locale.US));
    else
      return KeyFactory.createKey(escapeType(inKey.getType().toString()),
                                  inKey.getID().toLowerCase(Locale.US));
  }

  //........................................................................
  //------------------------------- convert --------------------------------

  /**
   * Convert the given entry key into a corresponding entity key.
   *
   * @param       inKey the key to convert
   * @param       <T>   the type of entry to convert
   *
   * @return      the converted key
   *
   */
  @SuppressWarnings("unchecked") // not using proper types
  public <T extends AbstractEntry> AbstractEntry.EntryKey<T> convert(Key inKey)
  {
    Key parent = inKey.getParent();

    if(parent != null)
      return new AbstractEntry.EntryKey<T>
        (inKey.getName(),
         (AbstractType<T>)AbstractType
         .getTyped(escapeType(inKey.getKind())),
         convert(parent));

    return new AbstractEntry.EntryKey<T>
      (inKey.getName(),
       (AbstractType<T>)AbstractType
       .getTyped(escapeType(inKey.getKind())));
  }

  //........................................................................
  //------------------------------- convert --------------------------------

  /**
   * Convert the given datastore entity into a dma entry.
   *
   * @param      inID     the id of the entry to convert
   * @param      inType   the type of the entry to convert
   * @param      inEntity the entity to convert
   *
   * @param      <T>      the type of the entry to convert
   *
   * @return     the converted entry, if any
   *
   */
  @SuppressWarnings("unchecked") // need to cast value gotten
  public @Nullable <T extends AbstractEntry> T convert
                      (String inID, AbstractType<T> inType,
                       @Nullable Entity inEntity)
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

    // update any key related value
    entry.updateKey(convert(inEntity.getKey()));

    // update extensions, if necessary
    entry.setupExtensions();

    //s_entryCache.put(entry.getKey().toString(), entry);
    tracer.done("uncached");
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
  public @Nullable AbstractEntry convert(@Nullable Entity inEntity)
  {
    if(inEntity == null)
      return null;

    Key key = inEntity.getKey();
    String id = key.getName();
    AbstractType<? extends AbstractEntry> type =
      AbstractType.getTyped(unescapeType(key.getKind()));

    if(type == null || id == null)
    {
      Log.warning("cannot properly extract type or id: " + type + "/" + id
                  + " - " + key);
      return null;
    }

    return convert(id, type, inEntity);
  }

  //........................................................................
  //------------------------------- convert --------------------------------

  /**
   * Convert the given datastore entities into a dma entries.
   *
   * @param      inEntities the entities to convert
   *
   * @return     the entries found, if any
   *
   */
  public @Nullable List<AbstractEntry> convert(List<Entity> inEntities)
  {
    List<AbstractEntry> entries = new ArrayList<AbstractEntry>();

    for(Entity entity : inEntities)
      entries.add(convert(entity));

    return entries;
  }

  //........................................................................
  //------------------------------- convert --------------------------------

  /**
   * Convert the given dma entry into a datastore entity.
   *
   * @param      inEntry the entry to convert
   *
   * @return     the entry found, if any
   *
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

  //........................................................................

  //---------------------------- escapeType -----------------------------

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

  //........................................................................
  //----------------------------- unsecapeType -----------------------------

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

  //........................................................................

  //........................................................................
}
