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

package net.ixitxachitls.dma.entries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.AbstractEntryProto;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.File;
import net.ixitxachitls.dma.values.FormattedText;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Parameters;
import net.ixitxachitls.dma.values.Reference;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the base class for all entries.
 *
 * @file          AbstractEntry.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public abstract class AbstractEntry extends ValueGroup
  implements Comparable<AbstractEntry>, Serializable
{
  /** A simple auxiliary class to store a link with name. */
  public static class Link
  {
    private final String m_name;
    private final String m_url;

    public Link(String inName, String inURL)
    {
      m_name = inName;
      m_url = inURL;
    }

    public String getName()
    {
      return m_name;
    }

    public String getURL()
    {
      return m_url;
    }
  }

  /**
   * The constructor with a type.
   *
   * @param  inType  the type of the entry
   */
  protected AbstractEntry(AbstractType<?> inType)
  {
    m_type = inType;
  }

  /**
   * The complete constructor, with name and type. It is only used in
   * derivations, where the type has to be set.
   *
   * @param       inName the name of the entry
   * @param       inType the type of the entry
   */
  protected AbstractEntry(String inName, AbstractType<?> inType)
  {
    this(inType);

    m_name = inName;
    // addBase(inName); // when creating new entries, default bases are not
    //                  // preserved
    m_changed = false;
  }

  /**
   * The complete constructor, with name and type. It is only used in
   * derivations, where the type has to be set.
   *
   * @param       inType  the type of the entry
   * @param       inBases the name of the base entries
   */
  protected AbstractEntry(AbstractType<?> inType,
                          String ... inBases)
  {
    this(inType);

    for(String base : inBases)
      addBase(base);
  }

  /**
   * The complete constructor, with name and type. It is only used in
   * derivations, where the type has to be set.
   *
   * @param       inName  the name of the entry
   * @param       inType  the type of the entry
   * @param       inBases the name of the base entries
   */
  protected AbstractEntry(String inName,
                          AbstractType<?> inType,
                          String ... inBases)
  {
    this(inName, inType);

    for(String base : inBases)
      addBase(base);
  }

  /** The type of this entry. */
  public static final AbstractType<AbstractEntry> TYPE =
    new AbstractType<AbstractEntry>(AbstractEntry.class);

  /** The entry type. */
  protected AbstractType<?> m_type;

  /** Flag if this entry has been changed but not saved. */
  protected boolean m_changed = false;

  /** The base entries for this entry, in the same order as the names. */
  protected List<BaseEntry> m_baseEntries = new ArrayList<>();

  /** The files for this entry. */
  protected transient List<File> m_files = new ArrayList<>();

  /** The files for this entry and all base entries. */
  protected transient @Nullable List<File> m_allFiles = null;

  /** The random generator. */
  protected static final Random RANDOM = new Random();

  /** The dashes to create comments. */
  protected static final String HYPHENS =
    "------------------------------------------------------------------------"
    + "-----";

  /** The dots to create comments. */
  protected static final String DOTS =
    "........................................................................"
    + "...";

  /** The introducer used to start the entry, after name and qualifiers. */
  protected static final char INTRODUCER =
    Config.get("resource:entries/introducer", '=');

  /** The maximal number of keywords to read for an entry. */
  protected static final int MAX_KEYWORD_WORDS =
    Config.get("resource:entries/key.words", 2);

  /** The starter for the base name part. */
  protected static final String BASE_START =
    Config.get("resource:entries/base.start", "[");

  /** The ending for the base name part. */
  protected static final String BASE_END =
    Config.get("resource:entries/base.end", "]");

  /** The pattern to replace values in expressions. */
  protected static final Pattern PATTERN_VAR =
    Pattern.compile("\\$(\\w+)");

  /** The pattern for expressions. */
  protected static final Pattern PATTERN_EXPR =
    Pattern.compile("\\[\\[(.*?)\\]\\]");

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The undefined string value. */
  public static final String UNDEFINED_STRING = "";

  /** The name of the abstract entry. */
  public String m_name = UNDEFINED_STRING;

  /** The base entries for this one. */
  public List<String> m_base = new ArrayList<>();

  /**
   * Get the key uniquely identifying this entry.
   *
   * @param    <T> the type of entry to get the key for
   *
   * @return   the key for the entry
   */
  @Override
  public EntryKey getKey()
  {
    return new EntryKey(getName(), getType());
  }

  /**
   * Get the maximal width of the keys, including attachments.
   *
   * @return      the maximal key width
   */
  protected int getKeyWidth()
  {
    int width = 0;

    // the width of the normal values
    Variables variables = getVariables();

    if(variables != null)
      width = variables.getKeyWidth();

    // now for the width of the attachments
//     for(AbstractAttachment attachment : m_attachments.values())
//       width = Math.max(width, attachment.getValues().getKeyWidth());

    return width;
  }

  /**
   * Get the name of the entry.
   *
   * @return      the requested name
   */
  @Override
  public String getName()
  {
    return m_name;
  }

  /**
   * Get the entry associated with this group.
   *
   * @return  the associated entry
   */
  @Override
  public AbstractEntry getEntry()
  {
    return this;
  }

  /**
   * Get the qualified name for this entry.
   *
   * The qualified name is the hiearchical name of all the base entry and the
   * name of this entry. Hierarchies are seperatede by ':', while muliple base
   * are seperated by '|'.
   *
   * @return      A string of form 'base1:base2:entry|base3:base4:entry'
   */
  @Deprecated
  public String getQualifiedName()
  {
//     List<String> names = new ArrayList<String>();

//     if(m_baseEntries == null)
    return getName();

//     for(BaseEntry base : m_baseEntries)
//       if(base != null)
//         names.add(base.getQualifiedName());

//     return Strings.toString(names, "::" + getName() + "||", "")
//       + "::" + getName();
  }

  /**
   * Get the names of the base entries this entry is based on.
   *
   * @return      the requested base names
   */
  public List<String> getBaseNames()
  {
    return Collections.unmodifiableList(m_base);
  }

  /**
   * Get the base entries this abstract entry is based on, if any.
   *
   * @return      the requested base entries; note that an entry can be null
   *              if it is not found
   */
  @Override
  public List<BaseEntry> getBaseEntries()
  {
    if(m_baseEntries == null || m_baseEntries.isEmpty())
    {
      m_baseEntries = new ArrayList<>();

      // TODO: make this in a single datastore request
      for(String base : m_base)
      {
        BaseEntry entry = (BaseEntry)DMADataFactory.get()
          .getEntry(createKey(base, getType().getBaseType()));
        if(entry != null)
          m_baseEntries.add(entry);
      }
    }

    return m_baseEntries;
  }

  public boolean hasBaseName(String inName)
  {
    for(BaseEntry base : getBaseEntries())
      if(base.getName().equalsIgnoreCase(inName))
        return true;

    return false;
  }

  /**
   * Make sure that all base entries are available.
   *
   * @return      true if all are available, false if not
   */
  public boolean ensureBaseEntries()
  {
    for(AbstractEntry base : getBaseEntries())
      if(base == null)
      {
        m_baseEntries = null;
        return false;
      }

    return true;
  }

  /**
   * Get the name of the entry as a reference for humans (not necessarily how
   * it can be found in a campaign).
   *
   * @return      the requested name
   *
   */
  public String getRefName()
  {
    return getName();
  }

  /**
   * Get the ID of the entry. This can mainly be used for reference purposes.
   * In this case, the lowercased name is equal to the id, which is not true
   * for entries.
   *
   * @return      the requested id
   */
  @Override
  @Deprecated
  public String getID()
  {
    return getName();
  }

  /**
   * Get the type of the entry.
   *
   * @param       <T>  the type of entry to get the type for
   *
   * @return      the requested name
   */
  @Override
  public AbstractType<?> getType()
  {
    return m_type;
  }

  /**
   * Get the type of the entry.
   *
   * @return      the requested name
   */
  @Override
  public String getEditType()
  {
    return m_type.toString();
  }

  /**
   * Get the files associated with this entry.
   *
   * @return      the associated files
   */
  public List<File> getFiles()
  {
    List<File> files = new ArrayList<File>(m_files);
    files.add(new File("dummy file", "dummy type", "dummy path", "dummy icon"));

    return files;
  }

  /**
   * Get the files associated with this entry and all base entries.
   *
   * @return all the associated files
   */
  public List<File> getAllFiles()
  {
    if(m_allFiles == null)
    {
      m_allFiles = new ArrayList<>(getFiles());
      for(AbstractEntry entry : getBaseEntries())
        if(entry != null)
          m_allFiles.addAll(entry.getAllFiles());
    }

    return m_allFiles;
  }

  /**
   * Get the main file associated with this entry.
   *
   * @return      the associated main file
   */
  public @Nullable File getMainFile()
  {
    for(File file : getAllFiles())
      if("main".equals(file.getName()) || file.getName().startsWith("main."))
        return file;

    return null;
  }

  /**
   * Compute and return the dma representation of the entry.
   *
   * @return  the entry dma formatted
   */
  public FormattedText dmaValues()
  {
    return new FormattedText(formatValues() + ".");
  }

  /**
   * Collect the dependencies for this entry.
   *
   * @return      a list with all dependent entries
   */
  public Set<AbstractEntry> collectDependencies()
  {
    Set<AbstractEntry> entries = Sets.newHashSet();
    for(AbstractEntry base : getBaseEntries())
    {
      entries.addAll(base.collectDependencies());
      entries.add(base);
    }

    /*
    for(AbstractExtension<?, ?> extension : m_extensions.values())
    {
      List<Entry<?>> subEntries = extension.getSubEntries(true);
      if(subEntries != null)
        entries.addAll(subEntries);
    }
    */

    return entries;
  }

  /**
   * Collect all the searchable values by key.
   *
   * @return a map of keys to searchable values
   */
  public Map<String, Object> collectSearchables()
  {
    Map<String, Object> searchables = new HashMap<>();
    searchables.put("bases",
                    new ArrayList<>(Strings.toLowerCase(getBaseNames())));

    return searchables;
  }

  /**
   * Get the references of this entry with full information for printing.
   *
   * @return      a list with the references and all values
   */
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> fullReferences()
  {
    List<Map<String, Object>> references = Lists.newArrayList();

    Combined<ValueList<Multiple>> combinedRefs = collect("references");
    for(Multiple ref : combinedRefs.total())
    {
      Map<String, Object> values = Maps.newHashMap();
      Reference<BaseProduct> reference = (Reference<BaseProduct>)ref.get(0);
      BaseProduct product = reference.getEntry();
      Object pages = ref.get(1);

      if(product != null)
        values.put("title", product.getFullTitle());

      values.put("id", reference);
      values.put("pages", pages);
      references.add(values);
    }

    return references;
  }

  /**
   * Collect the name value.
   *
   * @param   inName the name of the value to collect
   * @param   <T>    the type of value to collect
   *
   * @return  the combined value collected.
   */
  public <T extends Value<T>> Combined<T> collect(String inName)
  {
    Combined<T> combined = new Combined<T>(inName, this);
    collect(inName, combined);

    return combined;
  }

  @Override
  protected <T extends Value<T>> void collect(String inName,
                                              Combined<T> ioCombined)
  {
    super.collect(inName, ioCombined);

    for(AbstractEntry base : getBaseEntries())
      if(base != null)
        base.collect(inName, ioCombined);
  }

  /**
    * Check if the file has been changed (and thus might need saving).
    *
    * @return      true if changed, false if not
    */
  public boolean isChanged()
  {
    return m_changed;
  }

  @Override
  public boolean equals(Object inOther)
  {
    if(this == inOther)
      return true;

    if(inOther == null)
      return false;

    if(inOther instanceof AbstractEntry)
      return getID().equals(((AbstractEntry)inOther).getID());

    return false;
  }

  @Override
  public int hashCode()
  {
    return getID().hashCode();
  }

  @Override
  public int compareTo(AbstractEntry inOther)
  {
    return getID().compareTo(inOther.getID());
  }

  /**
   * Checks whether this entry is based on the given one, directly or
   * indirectly.
   *
   * @param      inBase the base entry to look for
   *
   * @return     true if this entry is directly or indirectly based on the
   *             given entry, false else
   */
  public boolean isBasedOn(BaseEntry inBase)
  {
    if(m_baseEntries == null)
      return false;

    for(AbstractEntry base : m_baseEntries)
      if(base == inBase || (base != this && inBase.isBasedOn((BaseEntry)base)))
        return true;

    return false;
  }

  /**
   * Check if the given value is in the group value with the given key.
   *
   * @param       inValue the value to look for
   * @param       inKey   the key of the value to check in
   *
   * @return      true if it is in, false if it is not
   */
  @Override
  public boolean isValueIn(String inValue, String inKey)
  {
    if(super.isValueIn(inValue, inKey))
      return true;

    for(BaseEntry entry : getBaseEntries())
      if(entry.isValueIn(inValue, inKey))
        return true;

    return false;
  }

  /**
   * Check if the given value has the value given.
   *
   * @param       inValue the value to look for
   * @param       inKey   the key of the value to check in
   *
   * @return      true if it is in, false if it is not, null if undefined or
   *              invalid
   */
  @Override
  public @Nullable Boolean isValue(String inValue, String inKey)
  {
    Boolean result = super.isValue(inValue, inKey);
    if(result != null)
      return result;

    for(BaseEntry entry : getBaseEntries())
    {
      if(entry == null)
        continue;

      result = entry.isValue(inValue, inKey);
      if(result != null)
        return result;
    }

    return null;
  }

  @Override
  public String toString()
  {
    StringBuilder result = new StringBuilder();

    result.append(m_type);
    result.append(' ');

    result.append(m_name);
    result.append(' ');

    result.append(INTRODUCER);
    result.append("\n\n");
    result.append(formatValues());
    result.append(s_delimiter);
    result.append('\n');

    return result.toString();
  }

  /**
   * Format all the values contained in the entry for printing.
   *
   * @return      a String with a representation of all values
   */
  @Deprecated
  protected String formatValues()
  {
    StringBuilder result = new StringBuilder();

    boolean first = true;

    int width = getKeyWidth();

    formatValues(result, first, width);

    return result.toString();
  }

  /**
   * Get all the values in this entry, including attachments.
   *
   * @return      a map with all values by key
   */
  public Map<String, Value<?>> getAllValues()
  {
    Map<String, Value<?>> values = new HashMap<String, Value<?>>();

    Variables vars = getVariables();

    for(Variable var : vars)
    {
      if(!var.isStored())
        continue;

      Value<?> value = var.get(this);

      // We don't store this if we don't have a value.
      if(value == null) // || (!value.isDefined() && !value.hasExpression()))
        continue;

      values.put(var.getKey(), value);
    }

    return values;
  }

  /**
   * Get the path to this entry.
   *
   * @return      the path to read this entry
   */
  public String getPath()
  {
    return "/" + getType().getLink() + "/" + getName();
  }

  /**
   * Get the navigation information to this entry.
   *
   * @return      the links with the parent entries
   */
  public List<Link> getNavigation()
  {
    return ImmutableList.of
      (new Link(getType().getLink(), "/" + getType().getMultipleLink()),
       new Link(getName(), "/" + getType().getLink() + "/" + getName()));
  }

  /**
   * Get the list navigation information to this entry.
   *
   * @return      an array with pairs for caption and link per navigation entry
   *
   */
  public String [] getListNavigation()
  {
    return new String [] {
      getType().getMultipleLink(), "/" + getType().getMultipleLink(),
    };
  }

  /**
   * Check if the given user is allowed to edit the value with the given key.
   *
   * @param       inKey  the key to edit
   * @param       inUser the user trying to edit
   *
   * @return      true if the value can be edited by the user, false if not
   */
  public boolean canEdit(String inKey, BaseCharacter inUser)
  {
    return inUser != null && inUser.hasAccess(BaseCharacter.Group.ADMIN);
  }

  /**
   * Check if the given user is allowed to see the entry.
   *
   * @param       inUser the user trying to edit
   *
   * @return      true if the entry can be seen, false if not
   */
  public boolean isShownTo(Optional<BaseCharacter> inUser)
  {
    return true;
  }

  /**
   * Create a key for the given values.
   *
   * @param       inID   the id of the entry to create the key for
   * @param       inType the type of the id
   *
   * @param       <T> the type of entries to create for
   *
   * @return      the created key
   *
   */
  public static EntryKey createKey(String inID, AbstractType<?> inType)
  {
    return new EntryKey(inID, inType);
  }

  /**
   * Create a key for the given values. If parent id or type are null, no parent
   * will be used.
   *
   * @param       inID         the id of the entry to create the key for
   * @param       inType       the type of the id
   * @param       inParentID   the id of parent entry, if any
   * @param       inParentType the type of the parent entry, if any
   * @param       <T>          the type of entries to create for
   *
   * @return      the created key
   */
  public static EntryKey createKey(String inID, AbstractType<?> inType,
                                   Optional<String> inParentID,
                                   Optional<AbstractType<?>> inParentType)
  {
    if(inParentID.isPresent() && inParentType.isPresent())
      return createKey(inID, inType);

    EntryKey key = new EntryKey(inParentID.get(), inParentType.get());

    return new EntryKey(inID, inType, Optional.of(key));
  }

  /**
   * Get a summary for the entry, using the given parameters.
   *
   * @param       inParameters  the parameters to parameterize the summary
   *
   * @return      the string with the summary
   */
  public String getSummary(@Nullable Parameters inParameters)
  {
    Combined<Text> combined = collect("short description");
    String summary = combined.total().get();

    if(inParameters == null || !inParameters.isDefined())
      return summary;

    summary = computeExpressions(summary, inParameters);

    Value<?> notes = inParameters.getValue("Notes");
    if(notes != null)
      summary += " (" + notes + ")";

    return summary;
  }

  public void addFile(String inName, String inType, String inPath,
                      String inIcon)
  {
    m_files.add(new File(inName, inType, inPath, inIcon));
    m_changed = true;
  }

  public void addFile(ImagesService inImageService, String inName,
                      @Nullable String inType, BlobKey inBlobKey)
  {
    if (inType == null)
      inType = "image/png";

    String icon = "";
    if(inType.startsWith("image/"))
    {
      try
      {
        icon = inImageService.getServingUrl(ServingUrlOptions.Builder
                                            .withBlobKey(inBlobKey));
      }
      catch(IllegalArgumentException e)
      {
        Log.error("Cannot obtain serving url for '" + inBlobKey + "': " + e);
      }
    }
    else if("application/pdf".equals(inType))
      icon = "/icons/pdf.png";
    else
      Log.warning("unknown file type " + inType + " ignored for " + inName);

    addFile(inName, inType, "//file/" + inBlobKey.getKeyString(), icon);
  }


  /**
   * Remove the name file from the entry.
   *
   * @param inName the name of the file to remove
   *
   * @return true file was removed, false if not.
   */
  public boolean removeFile(String inName)
  {
    for(Iterator<File> i = m_files.iterator(); i.hasNext(); )
      if(i.next().getName().equals(inName))
      {
        i.remove();
        m_changed = true;
        return true;
      }

    return false;
  }

  @Override
  public void set(Values inValues)
  {
    m_name = inValues.use("name", m_name, Values.NOT_EMPTY);
    m_base = inValues.use("base", m_base, Values.NOT_EMPTY);
  }

  //------------------------------ updateKey -------------------------------

  /**
   * Update the values that are related to the key with new data.
   *
   * @param       inKey the new key of the entry
   *
   */
  public void updateKey(EntryKey inKey)
  {
    // nothing to do here
  }

  //........................................................................

  //------------------------------- setOwner -------------------------------

  /**
   * Set the owner of the entry.
   *
   * @param       inOwner the owning entry
   *
   */
  public void setOwner(AbstractEntry inOwner)
  {
    // abstract entries don't have an owner
  }

  //........................................................................
  //-------------------------------- store ---------------------------------

  /**
   * Store this entry in the given storage container.
   *
   * @param       inStorage   the storage that stores this entry
   *
   * @return      true if stored, false if not
   *
   * @undefined   never
   *
   */
//   public boolean store(Storage<? extends AbstractEntry> inStorage)
//   {
// //     return store(inStorage, -1, -1, -1, -1);
//   }

  //........................................................................
  //-------------------------------- store ---------------------------------

  /**
   * Store this entry in the given storage container.
   *
   * @param       inStorage   the storage that stores this entry
   * @param       inStartPos  the starting position in the file
   * @param       inStartLine the start line in the file
   * @param       inEndPos    the ending position in the file
   * @param       inEndLine   the ending line in the file
   *
   * @return      true if stored, false if not
   *
   * @undefined   never
   *
   */
//   @SuppressWarnings("unchecked") // casting of storage type
//   public boolean store(Storage<? extends AbstractEntry> inStorage,
//                        long inStartPos, long inStartLine, long inEndPos,
//                        long inEndLine)
//   {
//     remove();

//     m_storage = (Storage<AbstractEntry>)inStorage;

//     // store positions
// //     m_startPos  = inStartPos;
// //     m_startLine = inStartLine;
// //     m_endPos    = inEndPos;
// //     m_endLine   = inEndLine;

//     if(m_storage == null)
//       return false;

//     // add the entry to the campaign
//     if(!m_storage.getCampaign().add(this))
//       return false;

//     // store all the attachments as well
//     for(Iterator<AbstractAttachment> i = getAttachments(); i.hasNext(); )
//       i.next().store(m_storage);

//     // check if complete, if not do it
//     if(!m_complete)
//       complete();

//     // apply modifiers to storage, if any
//     // we don't have any here, but some might be required in derivations

//     return true;
//   }

  //........................................................................
  //-------------------------------- remove --------------------------------

  /**
   * Remove the entry from the current storage.
   *
   * @return      true if remove, false if not
   *
   * @undefined   never
   *
   */
//   public boolean remove()
//   {
//     if(m_storage == null)
//       return false;

//     // remove the entry from the current storage
//     m_storage.remove(this);
//     m_storage = null;

//     return true;
//   }

  //........................................................................
  //--------------------------------- read ---------------------------------

  /**
   * Read an entry from the reader.
   *
   * @param       inReader   the reader to read from
   *
   * @return      the entry read or null of no matching entry found.
   *
   */
  /*
  @SuppressWarnings("unchecked") // calling complete on base type
  public static @Nullable AbstractEntry read(ParseReader inReader)
  {
    if(inReader.isAtEnd())
      return null;

    ParseReader.Position start = inReader.getPosition();

    //----- leading comment ------------------------------------------------

    Comment leading = new Comment(MAX_LEADING_COMMENTS, MAX_LEADING_LINES);
    leading = leading.read(inReader);

    //......................................................................
    //----- type -----------------------------------------------------------

    String typeName = "";
    String className = "";
    Class<? extends AbstractEntry> entry = null;
    for(int i = 0; i < MAX_KEYWORD_WORDS; i++)
    {
      String word = null;
      try
      {
        word = inReader.readWord();
      }
      catch(net.ixitxachitls.input.ReadException e)
      {
        break;
      }

      typeName += " " + word;
      className += java.lang.Character.toUpperCase(word.charAt(0))
        + word.substring(1);

      try
      {
        entry = (Class<? extends AbstractEntry>)
          Class.forName(PACKAGE + className);

        // could load class
        break;
      }
      catch(ClassNotFoundException e) // $codepro.audit.disable
      {
        // class not found, try with next word
      }
    }

    if(entry == null)
    {
      inReader.seek(start);

      return null;
    }

    typeName = typeName.trim();

    //......................................................................
    //----- create ---------------------------------------------------------

    // create the entry
    AbstractType<? extends AbstractEntry> type =
      AbstractType.getTyped(typeName);

    if(type == null)
    {
      Log.error("cannot get type for '" + typeName + "'");
      return null;
    }

    AbstractEntry result = type.create();

    //......................................................................

    if(result == null || !result.readEntry(inReader))
      return null;

    // store the additional values
    if(leading != null)
      result.m_leadingComment = leading;

    // skip a newline (if any)
    inReader.expect('\n');

    // read the trailing comment
    Comment trailing = result.m_trailingComment.read(inReader);
    if(trailing != null)
      result.m_trailingComment = trailing;

    // clear the changed flag (we created a new one, of course it is
    // changed, but who should be interested in that...)
    result.changed(false);

    ParseReader.Position end = inReader.getPosition();
    result.m_startLine = start.getLine();
    result.m_startPos  = start.getPosition();
    result.m_endLine   = end.getLine();
    result.m_endPos    = end.getPosition();

    // fix the comments
    if(!result.m_leadingComment.isDefined() && !result.m_name.isEmpty())
        result.m_leadingComment =
          result.m_leadingComment.as("#----- " + result.m_name + "\n\n");
    else
      // fix the number of newlines before and after
      result.m_leadingComment.fix();

    if(!result.m_trailingComment.isDefined())
      result.m_trailingComment = result.m_trailingComment.as("\n#.....\n");
    else
      result.m_trailingComment.fix();

    // obtain and store all errors found for this entry
    for(BaseError error : inReader.fetchErrors())
      result.addError(error);

    return result;
  }
  */

  //........................................................................
  //------------------------------ readEntry -------------------------------

  /**
   * Read an entry, and only the entry without type and comments, from the
   * reader.
   *
   * @param       inReader the reader to read from
   *
   * @return      true if read successfully, false else
   *
   */
  /*
  protected boolean readEntry(ParseReader inReader)
  {
    if(inReader.isAtEnd())
      return false;

    //----- extension ------------------------------------------------------

    List<String> extensions = new ArrayList<String>();

    // now check for extensions
    if(inReader.expect("with"))
    {
      try
      {
        while(true)
        {
          // handle the extension
          String name = inReader.readWord();

          // if(inReader.expect(':'))
          //   // tag
          //   name += ':' + inReader.readWord();

          extensions.add(name);

          if(!inReader.expect(','))
            break;
        }

      }
      catch(net.ixitxachitls.input.ReadException e)
      {
        inReader.logWarning(inReader.getPosition(), "extension.incomplete",
                            null);
      }
    }

    //......................................................................
    //----- name -----------------------------------------------------------

    BaseText<?> name = m_name.read(inReader);
    if(name != null)
      m_name = name;

    // determine if we read values at all
    boolean values = true;

    if(inReader.expect(s_delimiter))
      values = false;
    else
    {
      ParseReader.Position pos = inReader.getPosition();
      if(!inReader.expect(INTRODUCER))
      {
        inReader.logWarning(pos, "entry.missing.introducer",
                            "introducer is " + INTRODUCER);

        return false;
      }
    }

    //......................................................................

    addExtensions(extensions);

    // read the values (including the final delimiter)
    if(values)
      readValues(inReader);

    // add the automatic extensions from base
    setupExtensions();

    return true;
  }
  */

  //........................................................................
  //------------------------------ readValues ------------------------------

  /**
   * Read the values, and only the values, from the reader into the object.
   *
   * @param       inReader   the reader to read from
   *
   */
  protected void readValues(ParseReader inReader)
  {
    Variables variables = getVariables();
    while(!inReader.isAtEnd() && !inReader.expect(s_delimiter))
    {
      String key = inReader.expect(variables.getKeywords());
      if(key == null)
      {
        inReader.logWarning(inReader.getPosition(), "entry.key.unknown",
                            "ignoring to next delimiter");

        if(inReader.ignore("" + s_keyDelimiter + s_delimiter) == s_delimiter)
          break;
        else
          continue;
      }
      else
        readVariable(inReader, variables.getVariable(key));

      if(inReader.expect(s_delimiter))
        break;

      if(!inReader.expect(s_keyDelimiter))
      {
        inReader.logWarning(inReader.getPosition(),
                            "entry.delimiter.expected",
                            "delimiter is " + s_delimiter + " or "
                            + s_keyDelimiter);
      }
    }
  }

  //........................................................................

  //------------------------------- addBase --------------------------------

  /**
   * Add a base to this entry.
   *
   * @param       inName the name to add with (or null to use the name of the
   *                     given base entry, if any)
   *
   */
  @SuppressWarnings("unchecked") // need to cast to base entry
  public void addBase(String inName)
  {
    AbstractType<? extends AbstractEntry> baseType = getType().getBaseType();
    if(baseType instanceof Type)
      baseType = ((Type)baseType).getBaseType();
    else
      if(inName.equalsIgnoreCase(getID()))
        return;

    BaseEntry entry =
      (BaseEntry)DMADataFactory.get().getEntry(createKey(inName, baseType));
    if(entry == null)
      Log.warning("base " + getType() + " '" + inName + "' not found");
    // else
    //   addExtensions(entry.getExtensionNames());

    if(m_baseEntries == null)
      m_baseEntries = new ArrayList<BaseEntry>();

    m_base.add(inName);
    m_baseEntries.add(entry);
  }

  //........................................................................
  //--------------------------- addToModifiable ----------------------------

  /**
   * Add the given value to the modifiable given.
   *
   * @param       inModifiable the modifier to add to
   * @param       inValue      the value to add
   * @param       inBaseName   the name of the base entry having the value
   *
   */
//   private void addToModifiable(Modifiable<?> inModifiable, Value inValue,
//                                String inBaseName)
//   {
//     if(inValue instanceof net.ixitxachitls.dma.values.Modifier)
//     {
//       net.ixitxachitls.dma.values.Modifier modifier =
//         (net.ixitxachitls.dma.values.Modifier)inValue;

//       String description = modifier.getDescription();

//       if(description == null || description.length() == 0)
//         description = inBaseName;
//       else
//         description += " (" + inBaseName + ")";

//       inModifiable.addModifier
//       (new NumberModifier(NumberModifier.Operation.ADD, modifier.getValue(),
//                             NumberModifier.Type.valueOf
//                             (modifier.getType().toString().toUpperCase()),
//                             description));
//     }
//     else
//       if(inValue instanceof BaseModifier)
//         inModifiable.addModifier((BaseModifier)inValue);
//       else
//         if(inValue instanceof Modifiable)
//         {
//           Modifiable<?> value = (Modifiable)inValue;

//           for(BaseModifier<?> modifier : value.modifiers())
//             inModifiable.addModifier(modifier);

//           Value base = value.getBaseValue();

//           if(base.isDefined())
//             inModifiable.addModifier(new ValueModifier<Value>
//                                      (ValueModifier.Operation.ADD, base,
//                                     ValueModifier.Type.GENERAL, inBaseName));
//         }
//         else
//           inModifiable.addModifier(new ValueModifier<Value>
//                                    (ValueModifier.Operation.ADD, inValue,
//                                     ValueModifier.Type.GENERAL, inBaseName));
//   }

  //........................................................................

  //-------------------------------- check ---------------------------------

  /**
   * Check the entry for possible problems.
   *
   * @return      false if a problem was found, true if not
   *
   */
  public boolean check()
  {
    return true;
  }

  //........................................................................

  //------------------------------- changed --------------------------------

  /**
   * Set the state of the file to changed.
   *
   * @param       inChanged the value to set to, true for changed (dirty), false
   *                        for unchanged (clean)
   *
   */
  @Override
  public void changed(boolean inChanged)
  {
    m_changed = inChanged;
  }

  //........................................................................

  /**
   * Save the entry if it has been changed.
   *
   * @return      true if saved, false if not
   */
  public boolean save()
  {
    if(!m_changed)
      return false;

    return DMADataFactory.get().update(this);
  }

  //........................................................................

  //------------------------------------------------- other member functions

  //--------------------------- ensureExtensions ---------------------------

  /**
   * Ensure that extensions are properly initialized.
   */
  protected void ensureExtensions()
  {
    // nothing to do here
  }

  //........................................................................
  //-------------------------- computeExpressions --------------------------

  /**
   * Compute the expressions embedded in the given string and replace all
   * possible variables.
   *
   * @param       inText       the text to replace in
   * @param       inParameters the parameters for parametrizing expressions
   *
   * @return      the computed string
   */
  public String computeExpressions(String inText,
                                   @Nullable Parameters inParameters)
  {
    // TODO: make this more generic and move it to a separate class
    String text = inText;
    StringBuffer result = new StringBuffer();

    if(inParameters != null)
    {
      Matcher matcher = PATTERN_VAR.matcher(text);
      while(matcher.find())
      {
        Value<?> value = inParameters.getValue(matcher.group(1));
        if(value != null && value.isDefined())
          matcher.appendReplacement(result, value.toString().replace('$', '_'));
        else
          matcher.appendReplacement(result,
                                    "\\\\color{error}{&#x24;" + matcher.group(1)
                                    + "}");
      }

      matcher.appendTail(result);

      text = result.toString();
      result = new StringBuffer();
    }

    Matcher matcher = PATTERN_EXPR.matcher(text);

    while(matcher.find())
      matcher.appendReplacement(result,
                                "" + computeExpression(matcher.group(1)));

    matcher.appendTail(result);

    return result.toString();
  }

  /**
   * Evaluate the given expression.
   *
   * @param  inExpression the expression to evaluate
   *
   * @return the evaluated expression
   */
  private String computeExpression(String inExpression)
  {
    String expression = inExpression.replaceAll("[ \t\n\f\r]", "");

    StringTokenizer tokens =
      new StringTokenizer(expression, "()+-*/,^", true);

    return computeExpression(expression, tokens);
  }

  /**
   * Compute expression from the given tokens.
   *
   * @param  inExpression the expression to compute
   * @param  inTokens     tokens following the expression
   *
   * @return the evaluated expression
   */
  private String computeExpression(String inExpression,
                                   StringTokenizer inTokens)
  {
    if(!inTokens.hasMoreTokens())
    {
      Log.warning("invalid expression, expected more: "  + inExpression);

      return "* invalid expression, expected (: " + inExpression + " *";
    }

    String token = inTokens.nextToken();

    if("min".equals(token))
    {
      if(!"(".equals(inTokens.nextToken()))
      {
        Log.warning("invalid expression, expected '(': " + inExpression);

        return "* invalid expression, expected (: " + inExpression + " *";
      }

      String first = computeExpression(inExpression, inTokens);
      String second = computeExpression(inExpression, inTokens);

    return "" + Math.min(Integer.parseInt(first), Integer.parseInt(second));
    }

    if("max".equals(token))
    {
      if(!"(".equals(inTokens.nextToken()))
      {
        Log.warning("invalid expression, expected '(': " + inExpression);

        return "* invalid expression, expect (: " + inExpression + " *";
      }

      String first = computeExpression(inExpression, inTokens);
      String second = computeExpression(inExpression, inTokens);

    return "" + Math.max(Integer.parseInt(first), Integer.parseInt(second));
    }

    if("range".equals(token))
    {
      if(!"(".equals(inTokens.nextToken()))
      {
        Log.warning("invalid expression, expected '(': " + inExpression);

        return "* invalid expression, expect (: " + inExpression + " *";
      }

      int level = Integer.parseInt(computeExpression(inExpression, inTokens));
      List<String> ranges = new ArrayList<String>();

      String current = "";
      for(String argument = inTokens.nextToken();
          !"(".equals(argument) && inTokens.hasMoreTokens();
          argument = inTokens.nextToken())
      {
        if(",".equals(argument))
        {
          ranges.add(current);
          current = "";
        }
        else
        {
          current += argument;
        }
      }
      ranges.add(current);
      Collections.reverse(ranges);

      for(String range : ranges)
      {
        String []parts = range.split(":\\s*");
        if(parts.length != 2)
          continue;

        try
        {
          if(level >= Integer.parseInt(parts[0]))
            return parts[1];
        }
        catch(NumberFormatException e)
        {
          // just ignore it
        }
      }

      return "* invalid range *";
    }

    if("switch".equals(token))
    {
      if(!"(".equals(inTokens.nextToken()))
      {
        Log.warning("invalid expression, expected '(': " + inExpression);

        return "* invalid expression, expect (: " + inExpression + " *";
      }

      String value = computeExpression(inExpression, inTokens);
      List<String> options = new ArrayList<String>();

      String current = "";
      for(String argument = inTokens.nextToken();
          !"(".equals(argument) && inTokens.hasMoreTokens();
          argument = inTokens.nextToken())
      {
        if(",".equals(argument))
        {
          options.add(current);
          current = "";
        }
        else
        {
          current += argument;
        }
      }
      options.add(current);

      for(String option : options)
      {
        String []parts = option.split(":\\s*");
        if(parts.length != 2)
          continue;

        String []cases = parts[0].split("\\|");
        for(String single : cases)
          if(single.trim().equalsIgnoreCase(value))
            return parts[1];
          else if("default".equalsIgnoreCase(single))
            return parts[1];
      }

      return "* invalid switch *";
    }

    try
    {
      String value;
      if("(".equals(token))
        value = computeExpression(inExpression, inTokens);
      else if("-".equals(token))
        value = "-" + computeExpression(inExpression, inTokens);
      else if("+".equals(token))
        value = "+" + computeExpression(inExpression, inTokens);
      else
        value = token;

      if(!inTokens.hasMoreTokens())
        return value;

      String operator = inTokens.nextToken();

      if(",".equals(operator) || ")".equals(operator))
        return value;

      String operand = computeExpression(inExpression, inTokens);

      if("+".equals(operator))
        return "" + (Integer.parseInt(value) + Integer.parseInt(operand));

      if("-".equals(operator))
        return "" + (Integer.parseInt(value) - Integer.parseInt(operand));

      if("*".equals(operator))
        return "" + (Integer.parseInt(value) * Integer.parseInt(operand));

      if("/".equals(operator))
        if(Integer.parseInt(value) == 0)
          return "0";
        else
          return "" + (Integer.parseInt(value) / Integer.parseInt(operand));

      if("^".equals(operator))
        return "" + (int)Math.pow(Integer.parseInt(value),
                                  Integer.parseInt(operand));

      Log.warning("invalid operator " + operator + ": " + inExpression);

      return value;
    }
    catch(NumberFormatException e)
    {
      Log.warning(e + ", for " + inExpression);

      return "* invalid number *";
    }
  }

  @Override
  public Message toProto()
  {
    AbstractEntryProto.Builder builder = AbstractEntryProto.newBuilder();

    builder.setName(m_name);
    builder.setType(m_type.toString());
    builder.addAllBase(m_base);
    for(File file : m_files)
      builder.addFiles(file.toProto());

    return builder.build();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof AbstractEntryProto))
    {
      Log.warning("Cannot parse abstract proto " + inProto.getClass());
      return;
    }

    AbstractEntryProto proto = (AbstractEntryProto)inProto;

    m_name = proto.getName();
    m_type = AbstractType.getTyped(proto.getType());
    m_base = proto.getBaseList();

    for (AbstractEntryProto.File file : proto.getFilesList())
      m_files.add(File.fromProto(file));
  }

  /**
   * Parse the proto buffer values from the given bytes.
   *
   * @param inBytes  the bytes to parse
   */
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(AbstractEntryProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test //extends ValueGroup.Test
  {
    //----- init -----------------------------------------------------------

    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      /*
      AbstractEntry<AbstractEntry<?, BaseEntry<?>>, BaseEntry<?>> entry =
        new AbstractEntry<AbstractEntry<?, BaseEntry<?>>, BaseEntry<?>>
          ("just a test",
            new AbstractType.Test.TestType<AbstractEntry>
            (AbstractEntry.class))
            {
              private static final long serialVersionUID = 1L;
            };

      // name
      assertEquals("name", "just a test", entry.getName());
      assertFalse("changed", entry.isChanged());

      // type
      assertEquals("type", "abstract entry", entry.getType().toString());
      assertEquals("type", "entry", entry.getType().getLink());
      assertEquals("type", "AbstractEntry", entry.getType().getClassName());
      assertEquals("type", "Abstract Entrys", entry.getType().getMultiple());
      assertEquals("type", "AbstractEntrys", entry.getType().getMultipleDir());
      assertEquals("type", "entrys", entry.getType().getMultipleLink());
//       assertNull("type", entry.getType().getBaseType());

      // conversion to string
      assertEquals("converted",
                   "#----- just a test\n"
                   + "\n"
                   + "abstract entry just a test =\n"
                   + "\n"
                   + ".\n"
                   + "\n"
                   + "#.....\n",
                   entry.toString());

      // an abstract entry with a base type
//       entry = new AbstractEntry("name",
//                                 new Type(AbstractEntry.class,
//                                          new Type(AbstractEntry.class)));

      // name
      assertEquals("name", "just a test", entry.getName());
      assertEquals("qualified name", "just a test", entry.getQualifiedName());
      assertEquals("id", "just a test", entry.getID());
      assertEquals("name", "just a test", entry.getRefName());
//       assertEquals("name", "name", entry.getBaseName());

//       // type
//       assertEquals("type", "abstract entry", entry.getType().toString());
//       assertEquals("type", "abstractentry", entry.getType().getLink());
//       assertEquals("type", "AbstractEntry", entry.getType().getClassName());
//       assertEquals("type", "Abstract Entrys", entry.getType().getMultiple());
//     assertEquals("type", "AbstractEntrys", entry.getType().getMultipleDir());
//       assertEquals("type", "abstractentrys",
//                    entry.getType().getMultipleLink());
//       assertEquals("type", "abstract entry",
//                    entry.getType().getBaseType().toString());

//       assertEquals("create", "abstract entry $undefined$ =\n\n.\n",
//                    entry.getType().create().toString());

//       // conversion to string
//       assertEquals("converted", "abstract entry name =\n\n.\n",
//                    entry.toString());
 *
 */
    }

    //......................................................................
  }

  //........................................................................
}
