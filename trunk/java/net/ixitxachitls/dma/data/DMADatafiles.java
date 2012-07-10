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
import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.BaseProduct;
import net.ixitxachitls.dma.entries.BaseType;
import net.ixitxachitls.dma.entries.Campaign;
import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.entries.Product;
import net.ixitxachitls.dma.entries.Type;
import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;
import net.ixitxachitls.util.resources.Resource;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * DMA file base data store class.
 *
 *
 * @file          DMADatafiles.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class DMADatafiles implements DMAData
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- DMADatafiles -----------------------------

  /**
   * Create the data repository with the given files.
   *
   * @param       inPath  the base path to all files
   * @param       inFiles the default files to read data from
   *
   */
  public DMADatafiles(@Nonnull String inPath, @Nullable String ... inFiles)
  {
    m_path = inPath;

    if(inFiles != null)
      for(String file : inFiles)
        if(file != null)
          addFile(file);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The path from where to load the files. */
  protected @Nonnull String m_path;

  /** The name of the files read. */
  protected @Nonnull List<String> m_names = new ArrayList<String>();

  /** The files for all the data. */
  protected @Nonnull ArrayList<DMAFile> m_files = new ArrayList<DMAFile>();

  /** All the entries, by type and by id. */
  protected @Nonnull HashMap<AbstractType<? extends AbstractEntry>,
                           NavigableMap<String, AbstractEntry>> m_entries =
    new HashMap<AbstractType<? extends AbstractEntry>,
                               NavigableMap<String, AbstractEntry>>();

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getPath --------------------------------

  /**
   * Get the path this data file is reading files from.
   *
   * @return      the path to the files.
   *
   */
  public @Nonnull String getPath()
  {
    return m_path;
  }

  //........................................................................
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
    NavigableMap<String, AbstractEntry> entries = m_entries.get(inType);

    if(entries == null)
    {
      entries = new TreeMap<String, AbstractEntry>();
      m_entries.put(inType, entries);
    }

    // TODO: this should actually be unmodifiable
    return (NavigableMap<String, T>)entries;
  }

  //........................................................................
  //------------------------------ getEntries ------------------------------

  /**
   * Gets all the entries of a specific type.
   *
   * @param    <T>      The type of entry to get
   * @param    inType   the type of entries to get
   * @param    inParent the parent type key for the entries, if any
   * @param    inStart  the starting number of entires to get (starts as 0)
   * @param    inSize   the maximal number of entries to return
   *
   * @return   a map with id and type
   *
   */
  @Override
@SuppressWarnings("unchecked") // need to cast
  public @Nonnull <T extends AbstractEntry> List<T>
                     getEntries(AbstractType<T> inType,
                                AbstractEntry.EntryKey<? extends AbstractEntry>
                                inParent, int inStart, int inSize)
  {
    NavigableMap<String, AbstractEntry> entries = m_entries.get(inType);

    if(entries == null)
    {
      entries = new TreeMap<String, AbstractEntry>();
      m_entries.put(inType, entries);
    }

    if(inSize > 0)
      return sublist((List<T>)new ArrayList<AbstractEntry>(entries.values()),
                     inStart, inStart + inSize);

    return (List<T>)new ArrayList<AbstractEntry>(entries.values());
  }

  //........................................................................
  //------------------------------ getEntries ------------------------------

  /**
   * Get the entry denoted by a key value pair. This throws a
   * TooManyResultsException if more thone one result is found.
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
    throw new UnsupportedOperationException("not implemented");
  }

  //........................................................................
  //-------------------------------- getIDs --------------------------------

  /**
   * Get all the ids of a specific type, sorted and navigable.
   *
   * @param       inType the type of entries to get ids for
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
    throw new UnsupportedOperationException("not yet implemented");
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
    throw
      new UnsupportedOperationException("has not been implemented for files");
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
   *                        filtering
   *
   * @return      a list with all the names
   *
   */
  @Override
  public @Nonnull SortedSet<String> getIndexNames
    (@Nonnull String inIndex,
     @Nonnull AbstractType<? extends AbstractEntry> inType, boolean inCached,
     @Nonnull String ... inFilters)
  {
    throw
      new UnsupportedOperationException("has not been implemented for files");
  }

  //........................................................................

  //------------------------------- hasEntry -------------------------------

  /**
   * Checks if an entry denoted by type and id exists.
   *
   * @param      inID   the id of the entry to get
   * @param      inType the type of the entry to get
   *
   * @return     true if the entry is already there, false if not
   *
   */
  public boolean hasEntry(@Nonnull String inID,
                          @Nonnull AbstractType<? extends AbstractEntry> inType)
  {

    NavigableMap<String, ? extends AbstractEntry> entries = getEntries(inType);
    return entries.get(inID) != null;
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
    DMADatafiles data =
      new DMADatafiles(Files.concatenate(getPath(),
                                         Product.TYPE.getMultipleDir()),
                       inUser.getName() + ".dma");

    data.read();

    // Associate all products with the user
    for(Product product : data.getEntries(Product.TYPE).values())
      product.setOwner(inUser);

    if(data.isChanged())
      data.save();

    return data;
  }

  //........................................................................
  //------------------------------- getTypes -------------------------------

  /**
   * Get all the types for entries present in the store.
   *
   * @return      a set of all the entry types
   *
   */
  public @Nonnull Set<AbstractType<? extends AbstractEntry>> getTypes()
  {
    return m_entries.keySet();
  }

  //........................................................................

  //--------------------------------- files --------------------------------

  /**
   * Get the names of the files that this entry can possibly be stored in.
   *
   * @param       inType the type of entries for which to get files
   *
   * @return      a list of names that can be used for storage
   *
   */
  public @Nonnull List<String> files
    (@Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    return m_names;
  };

  //........................................................................
  //----------------------------- getFilename ------------------------------

  /**
   * Get the name of the file the given entry is stored.
   *
   * @param       inEntry the entry to look up
   *
   * @return      the name of the file the entry is stored in, if any
   *
   */
  public @Nullable String getFilename(@Nonnull AbstractEntry inEntry)
  {
    for(DMAFile file : m_files)
    {
      if(file.getEntries().contains(inEntry))
        return file.getStorageName();
    }

    return null;
  }

  //........................................................................

  //------------------------------- getFile --------------------------------

  /**
   * Get the file with the given name.
   *
   * @param       inName the name of the file to get
   *
   * @return      the file found or null if not found
   *
   */
  public @Nullable DMAFile getFile(@Nonnull String inName)
  {
    for(DMAFile file : m_files)
      if(file.getStorageName().equals(inName))
        return file;

    return null;
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
    for(DMAFile file : m_files)
      if(file.isChanged())
        return true;

    return false;
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
    NavigableMap<String, T> entries = getEntries(inKey.getType());
    T entry = entries.get(inKey.getID());

    if(entry != null || !(inKey.getType() instanceof BaseType))
      return entry;

    // could not find the entry by id, try synonyms
    for(T synEntry : entries.values())
      if(synEntry instanceof BaseEntry
         && ((BaseEntry)synEntry).hasSynonym(inKey.getID()))
        return synEntry;

    return null;
  }

  //........................................................................
  //------------------------------- getEntry -------------------------------

  /**
   * Get the first entry denoted by a key value pair.
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
    NavigableMap<String, T> entries = getEntries(inType);
    for(T entry : entries.values())
      if(entry.getValue(inKey).toString().equals(inValue))
        return entry;

    return null;
  }

  //........................................................................
  //--------------------------- getRecentEntries ---------------------------

  /**
   * Get the recent ids of a specific type.
   *
   * @param       <T>    the real type of the entries to get
   * @param       inType the type of entries to get ids for
   *
   * @return      the recent ids
   *
   */
  @Override
public @Nonnull <T extends AbstractEntry>
    List<T> getRecentEntries(@Nonnull AbstractType<T> inType)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  //........................................................................
  //---------------------------- getRecentEntries --------------------------

  /**
   * Get all the ids of a specific type, sorted and navigable.
   *
   * @param       <T>    the real type of entries to get
   * @param       inType the type of entries to get ids for
   * @param       inParentID   the id of the parent for recent entries
   * @param       inParentType the type of the parent entries
   *
   * @return      all the ids
   *
   */
  @Override
@SuppressWarnings("unchecked") // need to cast cache value
  public @Nonnull  <T extends AbstractEntry> List<T>
    getRecentEntries(@Nonnull AbstractType<T> inType,
                     @Nonnull String inParentID,
                     @Nonnull AbstractType<? extends AbstractEntry>
                     inParentType)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  //........................................................................
  //------------------------------ getOwners -------------------------------

  /**
   * Get the owner of the given base product.
   *
   * @param    inID the id of the base product to own
   *
   * @return   a multi map from owner to ids
   *
   */
  @Override
public Multimap<String, String> getOwners(String inID)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert to human readable string.
   *
   * @return      the string representation
   *
   */
  @Override
public @Nonnull String toString()
  {
    StringBuilder result =
      new StringBuilder("path " + m_path + ", files " + m_names);

    for(AbstractType<? extends AbstractEntry> type : m_entries.keySet())
    {
      result.append(", " + type + ": ");
      for(String name : m_entries.get(type).keySet())
        result.append(name + "/");
    }

    return result.toString();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- remove --------------------------------

  /**
   *
   *
   * @param       inID    the id of the entry to remove
   * @param       inType  the type of the entry to remove
   *
   * @return      true if removed, false if not
   *
   */
  @Override
public boolean remove(@Nonnull String inID,
                        @Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  //........................................................................
  //--------------------------------- read ---------------------------------

  /**
   * Read all the files associated, if not yet read.
   *
   * @return true if all files could be read without error, false if there were
   *         errors
   *
   */
  @SuppressWarnings("unchecked") // cast for entry.getType() in put
  public boolean read()
  {
    boolean result = true;
    for(DMAFile file : m_files)
    {
      if(!file.wasRead())
      {
        result &= file.read();

        for(AbstractEntry entry : file.getEntries())
          addInternal(entry);
      }
    }

    return result;
  }

  //........................................................................
  //------------------------------- addFile --------------------------------

  /**
   * Load the file with the given name.
   *
   * @param       inFile the file name of file to load relative to the path
   *
   * @return      true if loaded, false if already there
   *
   */
  public boolean addFile(@Nonnull String inFile)
  {
    if(m_names.contains(inFile))
      return false;

    m_names.add(inFile);
    m_files.add(new DMAFile(inFile, m_path));

    return true;
  }

  //........................................................................
  //------------------------------- addFiles -------------------------------

  /**
   * Load all the dma file from the given path.
   *
   * @param    inPath the path to the directory to load files from
   *
   */
  public void addAllFiles(@Nonnull String inPath)
  {
    Resource path = Resource.get(Files.concatenate(m_path, inPath));

    for(String name : path.files())
    {
      if(name.endsWith(".dma"))
        addFile(Files.concatenate(inPath, name));
    }
  }

  //........................................................................
  //---------------------------------- update -------------------------------

  /**
   * Update the entry in the store. Will add the entry to the store if not
   * yet there, otherwise just update all the values and stores it.
   *
   * @param       inEntry the entry to add
   *
   * @return      true if updated, false if there was an error
   *
   */
  @Override
  public boolean update(@Nonnull AbstractEntry inEntry)
  {
    if(hasEntry(inEntry.getName(), inEntry.getType()))
      return save();

    return add(inEntry, true);
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the entry to the data.
   *
   * @param     inEntry the entry to add
   * @param     inSave  whether to save or not
   *
   * @return    true if added, false if there was an error
   *
   */
  public boolean add(@Nonnull AbstractEntry inEntry, boolean inSave)
  {
    return add(inEntry, computeFile(inEntry), inSave);
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the entry to the data.
   *
   * @param     inEntry the entry to add
   * @param     inFile  the name of the file to store in
   * @param     inSave  whether to save or not
   *
   * @return    true if added, false if there was an error
   *
   */
  public boolean add(@Nonnull AbstractEntry inEntry, @Nonnull String inFile,
                     boolean inSave)
  {
    DMAFile file = getFile(inFile);
    if(file == null)
    {
      addFile(inFile);

      file = getFile(inFile);
      if(file == null)
        return false;
    }

    file.add(inEntry, true);
    addInternal(inEntry);

    if(inSave)
      return save();

    return true;
  }

  //........................................................................
  //----------------------------- addInternal ------------------------------

  /**
   * Add the entry to the internal data structures, without adding it to a file
   * and without saving.
   *
   * @param       inEntry the entry to add
   *
   */
  protected void addInternal(@Nonnull AbstractEntry inEntry)
  {
    NavigableMap<String, AbstractEntry> entries =
      m_entries.get(inEntry.getType());

    if(entries == null)
    {
      entries = new TreeMap<String, AbstractEntry>();
      m_entries.put(inEntry.getType(), entries);
    }

    String name = inEntry.getName();
    if(inEntry instanceof Entry)
    {
      Entry entry = (Entry)inEntry;

      if(Config.get("web.data.datastore", true) && entries.containsKey(name))
      {
        int i;
        for(i = 1; entries.containsKey("TEMPORARY-" + i); i++)
          ;

        name = "TEMPORARY-" + i;
        Log.warning("duplicate id detected for '" + name
                    + "', ignoring as only importing (hopefully)");
      }
      else
        while(entries.containsKey(name))
        {
          entry.randomID();
          String oldID = name;
          name = entry.getName();
          Log.warning("duplicate id detected for '" + oldID
                      + "', setting new id to '" + name + "'");
        }
    }

    entries.put(name, inEntry);
  }

  //........................................................................
  //------------------------------- getFiles -------------------------------

  /**
   * Get the files for the given entry.
   *
   * @param    inEntry the entry for which to get all files.
   *
   * @return   a list of all the files found
   *
   */
  @Override
public @Nonnull List<File> getFiles(@Nonnull AbstractEntry inEntry)
  {
    String baseType = null;
    String []baseNames = null;
    if(inEntry instanceof Entry)
    {
      baseType = ((Type)inEntry.getType()).getBaseType().getMultipleDir();
      List<String> names = inEntry.getBaseNames();
      baseNames = names.toArray(new String[names.size()]);
    }

    List<File> files = new ArrayList<File>();
    files.add(new File("main", "image/png",
                       DMAFiles.mainImage(inEntry.getName(),
                                          inEntry.getType().getMultipleDir(),
                                          baseType, baseNames),
                       DMAFiles.mainImage(inEntry.getName(),
                                          inEntry.getType().getMultipleDir(),
                                          baseType, baseNames)));
    for(String file : DMAFiles.otherFiles(inEntry.getName(),
                                          inEntry.getType().getMultipleDir(),
                                          baseType, baseNames))
      // TODO: this is actually not always the right type.
      files.add(new File(Files.file(file), "image/png", file, file));

    return files;
  }

  //........................................................................

  //--------------------------------- save ---------------------------------

  /**
   * Save all the changed files.
   *
   * @return true if all data successfully saved, false if there was an error.
   *
   */
  public boolean save()
  {
    boolean success = true;
    for(DMAFile file : m_files)
      success = success && file.write();

    Log.event("*system*", "save",
              "Saved all data " + (success ? "without errors" : "with errors"));
    return success;
  }

  //........................................................................
  //----------------------------- removeEntry ------------------------------

  /**
   * Remove the indicated entry from the repository.
   *
   * @param       inID   the id of the entry to remove
   * @param       inType the type of entry to remove
   *
   * @return      true if removed, false if not
   *
   */
  public boolean removeEntry
    (@Nonnull String inID,
     @Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    AbstractEntry entry = getEntries(inType).remove(inID);
    if(entry == null)
      return false;

    for(DMAFile file : m_files)
      if(file.remove(entry))
        return true;

    return false;
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
   *
   * @return     the numbert of enties updated
   */
  @Override
public int rebuild(@Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    throw new UnsupportedOperationException("not implemented for files");
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------ checkNames ------------------------------

  /**
   * Check all the filesnames if they match the computed ones.  TODO: this can
   * be removed once all the data is in the datastore and the importer exporter
   * are used to backup/restore files.
   *
   * @param  inPrefix the prefix to the path of the files
   *
   * @return true if all the names match, false if not
   *
   */
  public boolean checkNames(String inPrefix)
  {
    boolean result = true;
    for(DMAFile file : m_files)
      for(AbstractEntry entry : file.getEntries())
      {
        if(!computeFile(entry).equals(inPrefix + file.getStorageName()))
        {
          Log.error("file mismatch for " + entry.getName() + " : "
                    + computeFile(entry) + " <=> "
                    + inPrefix + file.getStorageName());

          result = false;
        }
      }

    return result;
  }

  //........................................................................
  //----------------------------- computeFile ------------------------------

  /**
   * Compute the file this entry should be stored in.
   * TODO: make this method simpler by moving code into entries.
   *
   * @param       inEntry the entry for which to compute the file
   *
   * @return      the name of the file to store in
   *
   */
  public @Nonnull String computeFile(@Nonnull AbstractEntry inEntry)
  {
    AbstractType<? extends AbstractEntry> type = inEntry.getType();
    String dir = type.getMultipleDir();

    if(inEntry instanceof Product)
      return dir + "/" + ((Product)inEntry).getOwner() + ".dma";

    if(inEntry instanceof Campaign)
      return dir + "/" + inEntry.getBaseNames().get(0) + ".dma";

    if(inEntry instanceof Item)
      return dir + "/" + ((Item)inEntry).getCampaign().getName() + ".dma";

    if(inEntry instanceof BaseCharacter)
      return dir + "/Ixitxachitls.dma";

    if(inEntry instanceof BaseProduct)
    {
      BaseProduct product = (BaseProduct)inEntry;
      if(product.getProductType() == BaseProduct.ProductType.MAGAZINE)
      {
        if(product.getName().indexOf("Dragon") >= 0
           || product.getTitle().indexOf("Dragon") >= 0)
          return dir + "/Magazines/Dragon.dma";

        if(product.getName().indexOf("Dungeon") >= 0
           || product.getTitle().indexOf("Dungeon") >= 0)
          return dir + "/Magazines/Dungeon.dma";

        if(product.getName().indexOf("Polyhedron") >= 0)
           return dir + "/Magazines/Polyhedron.dma";

        return dir + "/Magazines/Misc.dma";
      }

      String world = product.getWorlds().replaceAll("\\s", "");
      if("None".equals(world)
         || "Dominaria".equals(world)
         || "Rokugan".equals(world)
         || "Dark.Matter".equals(world)
         || "GammaWorld".equals(world)
         || "Star*Drive".equals(world)
         || "HollowWorld".equals(world)
         || "DiabloII".equals(world)
         || "DawnoftheEmperors".equals(world)
         || "Odyssey".equals(world)
         || "MiddleEarth".equals(world)
         || "BuckRogers".equals(world))
        world = "Misc";

      if("RedSteel".equals(world)
         || "SavageCoast".equals(world))
        world = "Mystara";

      if(product.getProductType() == BaseProduct.ProductType.NOVEL
         || product.getProductType() == BaseProduct.ProductType.CALENDAR)
        return dir + "/Novels/" + world + ".dma";

      if(world.equals("StarWars"))
        return dir + "/StarWars.dma";

      if(product.getProductType() == BaseProduct.ProductType.MINIATURE)
        return dir + "/Miniatures.dma";

      if(product.getProductType() == BaseProduct.ProductType.CATALOG)
        return dir + "/Catalogs.dma";

      if(product.getProductType() == BaseProduct.ProductType.COMICS)
        return dir + "/BaseProducts.dma";

      if(product.getSystem() == BaseProduct.System.MARVEL_SUPER_HEROES
         || product.getSystem() == BaseProduct.System.MARVEL_SUPER_DICE
         || world.equals("MarvelSuperHeroes"))
        return dir + "/MarvelSuperHeroes.dma";

      if(product.getSystem() == BaseProduct.System.ALTERNITY)
        return dir + "/Alternity.dma";

      if(product.getSystem() == BaseProduct.System.NONE
         || product.getSystem() == BaseProduct.System.WILD_SPACE
         || product.getSystem() == BaseProduct.System.DRAGON_STRIKE
         || product.getSystem() == BaseProduct.System.TERROR_TRAX
         || product.getSystem() == BaseProduct.System.DRAGON_DICE
         || product.getSystem() == BaseProduct.System.CHAINMAIL
         || product.getSystem() == BaseProduct.System.SAGA
         || product.getSystem() == BaseProduct.System.DnD_1ST
         || product.getSystem() == BaseProduct.System.ADnD_1ST
         || product.getSystem() == BaseProduct.System.ADnD_2ND_SAGA
         || product.getSystem() == BaseProduct.System.ADnD_2ND
         || product.getSystem() == BaseProduct.System.ADnD_REVISED
         || product.getSystem() == BaseProduct.System.DnD_3RD
         || product.getSystem() == BaseProduct.System.DnD_3_5
         || product.getSystem() == BaseProduct.System.DnD_4)
        return dir + "/DnD/" + world + ".dma";

      if(world.equals("WheelofTime"))
        return dir + "/WheelOfTime.dma";

      if(product.getSystem() == BaseProduct.System.D20
         || product.getSystem() == BaseProduct.System.D20_MODERN
         || product.getSystem() == BaseProduct.System.SWORD_SORCERY)
        return dir + "/d20.dma";
    }

    if(inEntry instanceof BaseEntry)
    {
      // use the first reference for the file name
      BaseEntry entry = (BaseEntry)inEntry;
      List<String> references = entry.getReferenceIDs();
      if(!references.isEmpty())
        return dir + "/" + references.get(0) + ".dma";
    }

    return dir + "/" + dir + ".dma";
  }

  //........................................................................
  //------------------------------- sublist --------------------------------

  /**
   * Create a sublist using the given limits.
   *
   * @param       <T>     the type of elements in the list
   * @param       inList  the list with the elements
   * @param       inStart the start position (inclusive)
   * @param       inEnd   the end position (exclusive)
   *
   * @return      a sublist for the given range
   *
   */
  public @Nonnull <T> List<T> sublist(@Nonnull List<T> inList, int inStart,
                                      int inEnd)
  {
    if(inList.isEmpty())
      return inList;

    int start = inStart;
    int end = inEnd;

    if(start < 0)
      start = 0;

    if(end > inList.size())
      end = inList.size();

    if(start > end || start > inList.size())
      start = end;

    return inList.subList(start, end);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- read -----------------------------------------------------------

    /** The read Test. */
    @org.junit.Test
    public void read()
    {
      DMADatafiles data =
        new DMADatafiles("../lib/test", "Test.dma");

      assertTrue("read", data.read());
    }

    //......................................................................
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}