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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileMetadata;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.ListItem;
import com.google.appengine.tools.cloudstorage.ListOptions;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.AbstractEntryProto;
import net.ixitxachitls.dma.server.servlets.DMAServlet;
import net.ixitxachitls.dma.values.File;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Group;
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
public abstract class AbstractEntry
  implements Comparable<AbstractEntry>, Serializable
{
  /** A simple auxiliary class to store a link with name. */
  public static class Link
  {
    /**
     * Create the link.
     *
     * @param inName the link name
     * @param inURL the url to link points to
     */
    public Link(String inName, String inURL)
    {
      m_name = inName;
      m_url = inURL;
    }

    /** The name of the link. */
    private final String m_name;

    /** The url the link points to. */
    private final String m_url;

    /**
     * Get the name of the link.
     *
     * @return the name
     */
    public String getName()
    {
      return m_name;
    }

    /**
     * Get the url of the link.
     *
     * @return the url
     */
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
    new AbstractType.Builder<>
        (AbstractEntry.class).build();

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
  protected String m_name = UNDEFINED_STRING;

  /** The base entries for this one. */
  protected List<String> m_base = new ArrayList<>();

  /** Google Cloud Storage service for accessing files. */
  private final GcsService m_gcs =
    GcsServiceFactory.createGcsService(new RetryParams.Builder()
      .initialRetryDelayMillis(500)
      .retryMaxAttempts(3)
      .totalRetryPeriodMillis(15_000)
      .build());

  /** The application identity to get the default bucket. */
  private final AppIdentityService m_appIdentity =
    AppIdentityServiceFactory.getAppIdentityService();

  /**
   * Get the key uniquely identifying this entry.
   *
   * @return   the key for the entry
   */
  public EntryKey getKey()
  {
    return new EntryKey(getName(), getType());
  }

  /**
   * Check whether the given user is the DM for this entry.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   */
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    return false;
  }

  /**
   * Check whether the given user is the owner of this entry.
   *
   * @param       inUser the user accessing
   *
   * @return      true for owner, false for not
   */
  public boolean isOwner(@Nullable BaseCharacter inUser)
  {
    if(inUser == null)
      return false;

    // Admins are owners of everything
    return inUser.hasAccess(Group.ADMIN);
  }

  /**
   * Get all the values for all the indexes.
   *
   * @return      a multi map of values per index name
   */
  public Multimap<Index.Path, String> computeIndexValues()
  {
    return HashMultimap.create();
  }

  /**
   * Check if the current entry represents a base entry or not.
   *
   * @return      true if this is a base entry, false else
   */
  public boolean isBase()
  {
    return false;
  }

  /**
   * Get the name of the entry.
   *
   * @return      the requested name
   */
  public String getName()
  {
    return m_name;
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
  public List<BaseEntry> getBaseEntries()
  {
    if(m_baseEntries == null || m_baseEntries.isEmpty())
    {
      m_baseEntries = new ArrayList<>();

      // TODO: make this in a single datastore request
      for(String base : m_base)
      {
        Optional<BaseEntry> entry = DMADataFactory.get()
          .getEntry(createKey(base, getType().getBaseType()));
        if(entry.isPresent())
          m_baseEntries.add(entry.get());
      }
    }

    return m_baseEntries;
  }

  /**
   * Check whether the entry has a base entry with the given name.
   *
   * @param inName the name of the base entry to look for
   * @return true if there is a such named base entry, false if not
   */
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
  @Deprecated
  private String getID()
  {
    return getName();
  }

  /**
   * Get the type of the entry.
   *
   * @return      the requested name
   */
  public AbstractType<?> getType()
  {
    return m_type;
  }

  /**
   * Get the type of the entry.
   *
   * @return      the requested name
   */
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
    // TODO: figure out why this does not work inside unit tests...
    if(m_files.isEmpty() && !DMAServlet.isTesting())
      try
      {
        String bucket = m_appIdentity.getDefaultGcsBucketName();
        ListOptions options = new ListOptions.Builder()
          .setRecursive(false)
          .setPrefix(getFilePath())
          .build();
        for(Iterator<ListItem> i = m_gcs.list(bucket, options); i.hasNext(); )
        {
          ListItem item = i.next();
          if(item.isDirectory() || item.getName().endsWith("/"))
            continue;

          String name = item.getName().replaceAll(".*/", "");
          String path =
            "https://storage.cloud.google.com/" + bucket + "/" + item.getName();

          GcsFileMetadata meta =
            m_gcs.getMetadata(new GcsFilename(bucket, item.getName()));
          String mime = meta.getOptions().getMimeType();
          String icon = icon(mime);
          if(icon.isEmpty())
            icon = path; // TODO: should be a smaller file.
          m_files.add(new File(name, mime, path, icon));
        }
      }
      catch(IOException e)
      {
        Log.warning("Exception when listing files for " + getName() + ": " + e);
      }

    return m_files;
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

  @Override
  public String toString()
  {
    return m_type + " " + m_name;
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
   * Get the path for the entry in the file system.
   *
   * @return the file system path
   */
  public String getFilePath()
  {
    return getType().getName() + "/" + getName().toLowerCase() + "/";
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
    return inUser != null && inUser.hasAccess(Group.ADMIN);
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
   * @return      the created key
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
   * Get the name of the icon to use for the given mime type.
   *
   * @param inMimeType the mime type to determine the icon for
   * @return the name of the icon or an empty string for none
   */
  private static String icon(String inMimeType)
  {
    switch(inMimeType)
    {
      case "application/pdf":
        return "/icons/pdf.png";

      default:
        return "";
    }
  }


  public void set(Values inValues) {
    String proto = inValues.use("proto", "");
    if(!proto.isEmpty() && !proto.equals(toProto().toString()))
    {
      parseFrom(proto);
      return;
    }

    setValues(inValues);
  }

  /**
   * Set the values in this entries.
   *
   * @param inValues the values to set
   *
   * @return true if setting should continue, false if not
   */
  public void setValues(Values inValues)
  {
    m_name = inValues.use("name", m_name, Optional.of(Values.NOT_EMPTY));
    m_base = inValues.use("base", m_base, Optional.of(Values.NOT_EMPTY));
  }

  /**
   * Update the values that are related to the key with new data.
   *
   * @param       inKey the new key of the entry
   */
  public void updateKey(EntryKey inKey)
  {
    // nothing to do here
  }

  /**
   * Set the owner of the entry.
   *
   * @param       inOwner the owning entry
   */
  public void setOwner(AbstractEntry inOwner)
  {
    // abstract entries don't have an owner
  }

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

    Optional<BaseEntry> entry =
      DMADataFactory.get().getEntry(createKey(inName, baseType));
    if(!entry.isPresent())
      Log.warning("base " + getType() + " '" + inName + "' not found");

    if(m_baseEntries == null)
      m_baseEntries = new ArrayList<BaseEntry>();

    m_base.add(inName);
    if(entry.isPresent())
      m_baseEntries.add(entry.get());
  }

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

  /**
   * Set the state of the file to changed.
   *
   * @param       inChanged the value to set to, true for changed (dirty), false
   *                        for unchanged (clean)
   *
   */
  public void changed(boolean inChanged)
  {
    m_changed = inChanged;
  }

  /**
   * Set the state of the file to changed.
   */
  public void changed()
  {
    changed(true);
  }

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

  /**
   * Convert the entry to a proto message.
   *
   * @return the convert proto
   */
  public Message toProto()
  {
    AbstractEntryProto.Builder builder = AbstractEntryProto.newBuilder();

    builder.setName(m_name);
    builder.setType(m_type.toString());
    builder.addAllBase(m_base);

    return builder.build();
  }

  /**
   * Set all the values for this entry from the given proto.
   *
   * @param inProto the proto to set from
   */
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof AbstractEntryProto))
    {
      Log.warning("Cannot parse abstract proto " + inProto.getClass());
      return;
    }


    AbstractEntryProto proto = (AbstractEntryProto)inProto;

    Optional<? extends AbstractType<? extends AbstractEntry>> type =
        AbstractType.getTyped(proto.getType());

    if(!type.isPresent())
    {
      Log.warning("Cannot get type for proto " + proto.getType());
      return;
    }

    m_name = proto.getName();
    m_type = type.get();
    m_base = proto.getBaseList();
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
      fromProto(defaultProto().getParserForType().parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

  public void parseFrom(String inText)
  {
    try
    {
      Message.Builder builder = defaultProto().toBuilder();
      TextFormat.merge(inText, builder);
      fromProto(builder.build());
    }
    catch(TextFormat.ParseException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

  protected Message defaultProto()
  {
    return AbstractEntryProto.getDefaultInstance();
  }

  public String getImageSearchQuery()
  {
    return m_name;
  }

  public void initialize()
  {
    // Nothing to do here, but derivations might want to setup some values
    // specially for newly created entries (e.g. randomly).
  }

  public int randomChance()
  {
    return 1;
  }

  public Optional<BaseItem.Random> randomChoice()
  {
    return Optional.absent();
  }

  public static <T extends AbstractEntry> T random(List<T> inEntries)
  {
    if(inEntries.isEmpty())
      throw new IllegalArgumentException("Cannot determine random entry from "
                                             + "empty list!");

    int total = 0;
    for(T entry : inEntries)
      total += entry.randomChance();

    int random = RANDOM.nextInt(total);
    for(T entry : inEntries)
    {
      random -= entry.randomChance();
      if(random < 0)
        return entry;
    }

    Log.warning("Did not find proper random entry!");
    return inEntries.get(inEntries.size() - 1);
  }

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test //extends ValueGroup.Test
  {
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
  }
}
