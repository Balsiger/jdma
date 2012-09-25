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
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.entries.Product;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.values.LongFormattedText;
import net.ixitxachitls.dma.values.Union;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.util.Strings;
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
  private @Nonnull DataStore m_data = new DataStore();

  /** The access to the datastore. */
  private @Nonnull DatastoreService m_store;

  /** The blob store service. */
  private @Nonnull BlobstoreService m_blobs;

  /** The image service to serve images. */
  private @Nonnull ImagesService m_image;

  /** Joiner to join keys together. */
  private static final Joiner s_keyJoiner = Joiner.on(":");

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /** The maximal number of entries to rebuild in one pass. */
  private static final int s_maxRebuild = 1000;

  //........................................................................

  //-------------------------------------------------------------- accessors

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
  public @Nonnull  <T extends AbstractEntry> List<T>
                      getEntries(@Nonnull AbstractType<T> inType,
                                 @Nullable AbstractEntry.EntryKey
                                 <? extends AbstractEntry> inParent,
                                 int inStart, int inSize)
  {
    List<T> entries = new ArrayList<T>();
    Iterable<Entity> entities =
      m_data.getEntities(inType.toString(), convert(inParent),
                         inType.getSortField(), inStart, inSize);

    for(Entity entity : entities)
      entries.add((T)convert(entity));

    return entries;
  }

  //........................................................................
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
  @Override
  public @Nullable <T extends AbstractEntry> T getEntry
                      (@Nonnull AbstractEntry.EntryKey<T> inKey)
  {
    return convert(inKey.getID(), inKey.getType(),
                   m_data.getEntity(convert(inKey)));
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
  public @Nullable <T extends AbstractEntry> T
                      getEntry(@Nonnull AbstractType<T> inType,
                               @Nonnull String inKey,
                               @Nonnull String inValue)
  {
    return (T)convert(m_data.getEntity(inType.toString(), inKey, inValue));
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
  public @Nullable <T extends AbstractEntry> List<T>
                      getEntries(@Nonnull AbstractType<T> inType,
                                 @Nonnull String inKey,
                                 @Nonnull String inValue)
  {
    return (List<T>)
      convert(m_data.getEntities(inType.toString(), null, 0, 1000,
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
  public @Nonnull List<String> getIDs
    (@Nonnull AbstractType<? extends AbstractEntry> inType,
     @Nullable AbstractEntry.EntryKey<? extends AbstractEntry> inParent)
  {
    return m_data.getIDs(inType.toString(), inType.getSortField(),
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
  public @Nonnull  <T extends AbstractEntry> List<T> getRecentEntries
    (@Nonnull AbstractType<T> inType,
     @Nullable AbstractEntry.EntryKey<? extends AbstractEntry> inParent)
  {
    return (List<T>)
      convert(m_data.getRecentEntities(inType.toString(),
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
    for(Entity entity : m_data.getIDs(Product.TYPE.toString(), "base", inID))
      owners.put(entity.getKey().getParent().getName(),
                 entity.getKey().getName());

    return owners;
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
  @Override
  public @Nonnull List<File> getFiles(@Nonnull AbstractEntry inEntry)
  {
    List<File> files = new ArrayList<File>();
    for(Entity entity : m_data.getEntities("file", convert(inEntry.getKey()),
                                           "__key__", 0, 100))
    {
      String name = (String)entity.getProperty("name");
      String type = (String)entity.getProperty("type");
      String path = (String)entity.getProperty("path");
      String icon = null;
      if(type == null)
        type = "image/png";

      if(type.startsWith("image/"))
        icon = m_image.getServingUrl(ServingUrlOptions.Builder.withBlobKey
                                     (new BlobKey(path)));
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
      if(entry != null)
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
  public @Nonnull <T extends AbstractEntry> List<T> getIndexEntries
    (@Nonnull String inIndex, @Nonnull AbstractType<T> inType,
     @Nullable AbstractEntry.EntryKey<? extends AbstractEntry> inParent,
     @Nonnull String inGroup, int inStart, int inSize)
  {
    List<AbstractEntry> entries = new ArrayList<AbstractEntry>();

    for(Entity entity : m_data.getEntities(inType.toString(), convert(inParent),
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
  public @Nonnull SortedSet<String> getIndexNames
    (@Nonnull String inIndex,
     @Nonnull AbstractType<? extends AbstractEntry> inType, boolean inCached,
     @Nonnull String ... inFilters)
  {
    SortedSet<String> names = new TreeSet<String>();

    for(Entity entity : m_data.getEntities(inType.toString(), null,
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
  public List<List<String>> getMultiValues
    (@Nonnull AbstractType<? extends AbstractEntry> inType,
     @Nonnull String ... inFields)
  {
    return m_data.getMultiValues(inType.toString(), null, inFields);
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
  public SortedSet<String> getValues
    (@Nonnull AbstractType<? extends AbstractEntry> inType,
     @Nonnull String inField)
  {
    return m_data.getValues(inType.toString(), null, inField);
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
  public boolean remove
    (@Nonnull String inID,
     @Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    return m_data.remove(KeyFactory.createKey(inType.toString(), inID));
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
  public boolean remove(@Nonnull AbstractEntry.EntryKey inKey)
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
  public boolean update(@Nonnull AbstractEntry inEntry)
  {
    if(inEntry.getName().equals(Entry.TEMPORARY) && inEntry instanceof Entry)
    {
      // determine a new, real id to use; this should actually be in a
      // transaction to be safe...
      ((Entry)inEntry).complete();
    }

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
   */
  public void removeFile(@Nonnull AbstractEntry inEntry, @Nonnull String inName)
  {
    Key key = KeyFactory.createKey(convert(inEntry.getKey()), "file", inName);

    Entity entity = m_data.getEntity(key);
    if(entity != null)
    {
      m_blobs.delete(new BlobKey((String)entity.getProperty("path")));
      m_data.remove(key);
      Log.important("deleted file " + inName + " for " + inEntry.getType()
                    + " " + inEntry.getName());
    }
    else
    {
      Log.warning("trying to delete noexistant file " + inName + " for "
                  + inEntry.getType() + " " + inEntry.getName());
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
  @Override
  public int rebuild(@Nonnull AbstractType<? extends AbstractEntry> inType)
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

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- convert --------------------------------

  /**
   * Convert the given entry key into a corresponding entity key.
   *
   * @param       inKey the key to convert
   *
   * @return      the converted key
   *
   */
  public @Nullable Key convert(@Nullable AbstractEntry.EntryKey inKey)
  {
    if(inKey == null)
      return null;

    AbstractEntry.EntryKey parent = inKey.getParent();
    if(parent != null)
      return KeyFactory.createKey(convert(parent), inKey.getType().toString(),
                                  inKey.getID());
    else
      return KeyFactory.createKey(inKey.getType().toString(), inKey.getID());
  }

  //........................................................................
  //------------------------------- convert --------------------------------

  /**
   * Convert the given entry key into a corresponding entity key.
   *
   * @param       inKey the key to convert
   *
   * @return      the converted key
   *
   */
  @SuppressWarnings("unchecked") // not using proper types
  public @Nonnull AbstractEntry.EntryKey convert(@Nonnull Key inKey)
  {
    Key parent = inKey.getParent();

    if(parent != null)
      return new AbstractEntry.EntryKey(inKey.getName(),
                                        AbstractType.getTyped(inKey.getKind()),
                                        convert(parent));

    return new AbstractEntry.EntryKey(inKey.getName(),
                                      AbstractType.getTyped(inKey.getKind()));
  }

  //........................................................................
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
  @SuppressWarnings("unchecked") // need to cast value gotten
  public @Nullable <T extends AbstractEntry> T convert
                      (@Nonnull String inID, @Nonnull AbstractType<T> inType,
                       @Nullable Entity inEntity)
  {
    if(inEntity == null)
      return null;

    Log.debug("converting entity " + inID + " to " + inType);

    T entry = inType.create(inID);
    if(entry == null)
    {
      Log.warning("cannot create conversion " + inType + " entity with id "
                  + inID + ": " + inEntity);
      return null;
    }

    // setup the extensions
    Object extensions = inEntity.getProperty("extensions");
    if(extensions != null && extensions instanceof Iterable)
      for(String extension : (Iterable<String>)extensions)
        entry.addExtension(extension);

    for(Map.Entry<String, Object> property
          : inEntity.getProperties().entrySet())
    {
      String name = m_data.fromPropertyName(property.getKey());
      if(name.startsWith(Index.PREFIX) || "change".equals(name)
         || "extensions".equals(name))
        continue;

      Object value = property.getValue();
      if(value == null)
        continue;

      String text;
      String rest;
      if(value instanceof Text)
        rest = entry.set(name, ((Text)value).getValue());
      else if(value instanceof ArrayList
              && entry.getValue(name) instanceof ValueList)
        rest = entry.set(name,
                  Strings.toString((ArrayList)value,
                                   ((ValueList)entry.getValue(name))
                                   .getDelimiter(),
                                   Value.UNDEFINED));
      else
        rest = entry.set(name, value.toString());

      if(rest != null && !rest.isEmpty())
        Log.warning("could not fully set value for " + name + ": " + rest);
    }

    // update any key related value
    entry.updateKey(convert(inEntity.getKey()));

    // update extensions, if necessary
    entry.setupExtensions();

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
      AbstractType.getTyped(key.getKind());

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
  public @Nullable List<AbstractEntry> convert(@Nonnull List<Entity> inEntities)
  {
    List<AbstractEntry> entries = new ArrayList<AbstractEntry>();

    for(Entity entity : inEntities)
      entries.add(convert(entity));

    return entries;
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
  @SuppressWarnings("unchecked") // need to case to value list
  public @Nonnull Entity convert(@Nonnull AbstractEntry inEntry)
  {
    Entity entity = new Entity(convert(inEntry.getKey()));
    for(Map.Entry<String, Value> value : inEntry.getAllValues().entrySet())
    {
      if(value.getValue() instanceof ValueList)
      {
        List<String> values = new ArrayList<String>();
        for(Value item : ((ValueList<Value>)value.getValue()))
          values.add(item.toString());
        entity.setProperty(m_data.toPropertyName(value.getKey()), values);
      }
      else
      {
        String valueText = value.getValue().toString();
        if(value.getValue() instanceof LongFormattedText
           || (value.getValue() instanceof Union
               && ((Union)value.getValue()).get() instanceof LongFormattedText))
          entity.setProperty(m_data.toPropertyName(value.getKey()),
                             new Text(valueText));
        else
        {
          if(valueText.length() >= 500)
            Log.warning("value for " + value.getKey() + " for "
                        + inEntry.getType() + " with id " + inEntry.getName()
                        + " is longer than 500 characters and will be "
                        + "truncated!");

          entity.setProperty(m_data.toPropertyName(value.getKey()), valueText);
        }
      }
    }

    // save the index information to make it searchable afterwards
    Multimap<Index.Path, String> indexes = inEntry.computeIndexValues();
    for(Index.Path index : indexes.keySet())
      // must convert the contained set to a list to make it serializable
      entity.setProperty(m_data.toPropertyName("index-" + index.getPath()),
                         new ArrayList<String>(indexes.get(index)));

    // save the time for recent changes
    entity.setProperty(m_data.toPropertyName("change"), new Date());

    // save the extensions
    entity.setProperty(m_data.toPropertyName("extensions"),
                       inEntry.getExtensionNames());

    return entity;
  }

  //........................................................................

  //........................................................................
}
