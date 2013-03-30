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

import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.data.DMAFile;
import net.ixitxachitls.dma.entries.extensions.AbstractExtension;
import net.ixitxachitls.dma.entries.extensions.ExtensionVariable;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.values.BaseText;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.Comment;
import net.ixitxachitls.dma.values.Contribution;
import net.ixitxachitls.dma.values.FormattedText;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Parameters;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.formatters.LinkFormatter;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Divider;
import net.ixitxachitls.output.commands.Image;
import net.ixitxachitls.output.commands.ImageLink;
import net.ixitxachitls.output.commands.Linebreak;
import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.output.commands.Par;
import net.ixitxachitls.output.commands.Script;
import net.ixitxachitls.output.commands.Title;
import net.ixitxachitls.util.Classes;
import net.ixitxachitls.util.EmptyIterator;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.errors.BaseError;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base class for all entries.
 *
 * @file          AbstractEntry.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class AbstractEntry extends ValueGroup
  implements Comparable<AbstractEntry>
{
  //----------------------------------------------------------------- nested

  //------------------------------- allows -------------------------------

  /**
   * Check if this type allows access by the given access level.
   *
   * @param       inLevel the level to check for
   *
   * @return      true if allowed, false if not
   *
   * @undefined   never
   *
   */
  //     public boolean allows(BaseCharacter.Group inLevel)
  //     {
  //       if(m_access == null)
  //         return true;

  //       return m_access.allows(inLevel);
  //     }

  //......................................................................

  //---------------------------- withAccess ------------------------------

  /**
   * Set the access level of this type.
   *
   * @param       inAccess the access level
   *
   * @return      this object
   *
   */
  // TODO: clean up comments
  //     protected Type withAccess(BaseCharacter.Group inAccess)
  //     {
  //       m_access = inAccess;

  //       return this;
  //     }

  //......................................................................

  //----- EntryKey ---------------------------------------------------------

  /**
   * The key for an entry for storage.
   *
   * @param     <T> the type of the entry represented by this key
   */
  @ParametersAreNonnullByDefault
  public static class EntryKey<T extends AbstractEntry>
  {
    /**
     * Create a key with a parent key.
     *
     * @param inID   the id of the entry
     * @param inType the type of the entry
     *
     */
    public EntryKey(String inID, AbstractType<T> inType)
    {
      m_id = inID;
      m_type = inType;
    }

    /**
     * Create a key with a parent key.
     *
     * @param inID     the id of the entry
     * @param inType   the type of the entry
     * @param inParent the parent key for the entry
     *
     */
    public EntryKey(String inID, AbstractType<T> inType,
                    EntryKey<? extends AbstractEntry> inParent)
    {
      this(inID, inType);

      m_parent = inParent;
    }

    /** The entry key. */
    private AbstractType<T> m_type;

    /** The entry id. */
    private String m_id;

    /** The parent key, if any. */
    private @Nullable EntryKey<?> m_parent;

    /**
     * Get the id of the entry represented by this key.
     *
     * @return the entry id
     */
    public String getID()
    {
      return m_id;
    }

    /**
     * Get the type for the entry represented by the key.
     *
     * @return the type
     *
     */
    public AbstractType<T> getType()
    {
      return m_type;
    }

    /**
     * Get the parent key for this one, if any.
     *
     * @return the parent key or null if there is no parent
     *
     */
    public @Nullable EntryKey<?> getParent()
    {
      return m_parent;
    }

    /**
     * Convert the key to a string for debugging.
     *
     * @return the converted string
     *
     */
    @Override
    public String toString()
    {
      return (m_parent != null ? m_parent : "") + "/" + m_type + "/" + m_id;
    }

    /**
     * Convert the given string to a key.
     *
     * @param   inText the text to convert
     *
     * @return  the converted key
     */
    public static @Nullable EntryKey<? extends AbstractEntry>
      fromString(String inText)
    {
      String []paths = inText.split("/");
      if(paths == null || paths.length == 0)
        return null;

      return fromString(paths, paths.length - 1);
    }

    /**
     * Extract the key from the given paths array nd index.
     *
     * @param    inPaths the paths pieces
     * @param    inIndex the index to start from with computation (descaending)
     *
     * @return   the key for the path part or null if not found
     *
     */
    @SuppressWarnings("unchecked") // creating wildard type
    private static @Nullable <T extends AbstractEntry> EntryKey<T>
    fromString(String []inPaths, int inIndex)
    {
      if(inPaths.length <= inIndex || inIndex < 1)
        return null;

      String id = inPaths[inIndex--].replace("%20", " ");
      AbstractType<T> type = (AbstractType<T>)
        AbstractType.getTyped(inPaths[inIndex].replace("%20", " "));

      if(type == null)
        return null;

      EntryKey<?> parent =
        fromString(inPaths, inIndex - 1);

      if(parent == null)
        return new EntryKey<T>(id, type);

      return new EntryKey<T>(id, type, parent);
    }

    /**
     * Determine if this key is equal to the given object.
     *
     * @param inOther the object to compare for
     *
     * @return true if they are equal, false if not
     */
    @Override
    public boolean equals(Object inOther)
    {
      if(this == inOther)
        return true;

      if(inOther == null)
        return false;

      if(!(inOther instanceof EntryKey))
        return false;

      EntryKey<?> other = (EntryKey<?>)inOther;
      return m_id.equals(other.m_id) && m_type.equals(other.m_type)
        && ((m_parent == null && other.m_parent == null)
            || (m_parent != null && m_parent.equals(other.m_parent)));
    }

    /**
     * Compute the hash code for the key.
     *
     * @return the hash value
     */
    @Override
    public int hashCode()
    {
      return toString().hashCode();
    }
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //---------------------------- AbstractEntry -----------------------------

  /**
   * The constructor with a type.
   *
   * @param  inType  the type of the entry
   *
   */
  protected AbstractEntry(AbstractType<? extends AbstractEntry> inType)
  {
    m_type = inType;

    // we have to init this here, as we need to have the type set
    m_base = new ValueList<Name>
      (new Name()
       .withTemplate("entrylink")
       .withFormatter(new LinkFormatter<Name>
                      ("/" + getType().getBaseType().getLink()
                       + "/")));

    setupExtensions();
  }

  //........................................................................
  //---------------------------- AbstractEntry -----------------------------

  /**
   * The complete constructor, with name and type. It is only used in
   * derivations, where the type has to be set.
   *
   * @param       inName the name of the entry
   * @param       inType the type of the entry
   *
   */
  protected AbstractEntry(String inName,
                          AbstractType<? extends AbstractEntry> inType)
  {
    this(inType);

    setName(inName);
    // addBase(inName); // when creating new entries, default bases are not
    //                  // preserved
    m_changed = false;
  }

  //........................................................................
  //---------------------------- AbstractEntry -----------------------------

  /**
   * Simple constructor for reading entries. This one is only used in tests.
   *
   */
  protected AbstractEntry()
  {
    this(BaseEntry.TYPE);
  }

  //........................................................................
  //---------------------------- AbstractEntry -----------------------------

  /**
   * The complete constructor, with name and type. It is only used in
   * derivations, where the type has to be set.
   *
   * @param       inType  the type of the entry
   * @param       inBases the name of the base entries
   *
   */
  protected AbstractEntry(AbstractType<? extends AbstractEntry> inType,
                          String ... inBases)
  {
    this(inType);

    for(String base : inBases)
      addBase(base);
  }

  //........................................................................
  //---------------------------- AbstractEntry -----------------------------

  /**
   * The complete constructor, with name and type. It is only used in
   * derivations, where the type has to be set.
   *
   * @param       inName  the name of the entry
   * @param       inType  the type of the entry
   * @param       inBases the name of the base entries
   *
   */
  protected AbstractEntry(String inName,
                          AbstractType<? extends AbstractEntry> inType,
                          String ... inBases)
  {
    this(inName, inType);

    for(String base : inBases)
      addBase(base);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  //----- general values ---------------------------------------------------

   /** The entry type. */
  protected AbstractType<? extends AbstractEntry> m_type;

  /** Flag if this entry has been changed but not saved. */
  protected boolean m_changed = false;

  /** Errors for this entry. */
  protected @Nullable List<BaseError> m_errors = null;

  /** All the extensions, indexed by name. */
  protected Map<String, AbstractExtension<? extends AbstractEntry>>
    m_extensions =
    new TreeMap<String, AbstractExtension<? extends AbstractEntry>>();

  /** The base entries for this entry, in the same order as the names. */
  protected @Nullable List<BaseEntry> m_baseEntries = null;

  /** The files for this entry. */
  private @Nullable List<DMAData.File> m_files = null;

  /** Flag if computing extension values. */
  private boolean m_computingExtension = false;

  /** The random generator. */
  protected static final Random s_random = new Random();

  /** The dashes to create comments. */
  protected static final String s_hyphens =
    "------------------------------------------------------------------------"
    + "-----";

  /** The dots to create comments. */
  protected static final String s_dots =
    "........................................................................"
    + "...";

  /** The introducer used to start the entry, after name and qualifiers. */
  protected static final char s_introducer =
    Config.get("resource:entries/introducer", '=');

  /** The maximal number of leading comments to read. */
  protected static final int s_maxLeadingComments =
    Config.get("resource:entries/comment.leading.max", -1);

  /** The maximal number of lines of leading comments to read. */
  protected static final int s_maxLeadingLines =
    Config.get("resource:entries/comment.leading.lines.max", -1);

  /** The maximal number of trailing comments to read. */
  protected static final int s_maxTrailingComments =
    Config.get("resource:entries/comment.trailing.max", 1);

  /** The maximal number of lines of trailing comments to read. */
  protected static final int s_maxTrailingLines =
    Config.get("resource:entries/comment.trailing.lines.max", 1);

  /** The name of the package for this class. */
  protected static final String s_package =
    "net.ixitxachitls.dma.entries.";

  /** The maximal number of keywords to read for an entry. */
  protected static final int s_keywordWords =
    Config.get("resource:entries/key.words", 2);

  /** The starter for the base name part. */
  protected static final String s_baseStart =
    Config.get("resource:entries/base.start", "[");

  /** The ending for the base name part. */
  protected static final String s_baseEnd =
    Config.get("resource:entries/base.end", "]");

  /** The pattern to replace values in expressions. */
  protected static final Pattern s_varPattern =
    Pattern.compile("\\$(\\w+)");

  /** The pattern for expressions. */
  protected static final Pattern s_expPattern =
    Pattern.compile("\\[\\[(.*?)\\]\\]");

  /** All registered extension classes. */
  protected static final
    Set<Class<? extends AbstractExtension<? extends AbstractEntry>>>
    s_extensions =
    new HashSet<Class<? extends AbstractExtension<? extends AbstractEntry>>>();

  //........................................................................

  //----- name -------------------------------------------------------------

  /** The name of the abstract entry. */
  @Key("name")
  @Note("Changing the name will not change any references to entries with "
        + "that name, thus leaving these references dangling. You will have "
        + "to update these manually.")
  protected BaseText<?> m_name = new Name();

  //........................................................................
  //----- comments ---------------------------------------------------------

  /** The leading comment(s) in front of the entry. */
  protected Comment m_leadingComment =
    new Comment(s_maxLeadingComments, s_maxLeadingLines);

  /** The trailing comment(s) after the entry. */
  protected Comment m_trailingComment =
    new Comment(s_maxTrailingComments, s_maxTrailingLines);

  //........................................................................
  //----- base names -------------------------------------------------------

  // Cannot use a static formatter here, as it depends on the
  // real type; thus we need to init this in the constructor after we have
  // the type.
  /** The base names. */
  @Key("base")
  @PrintUndefined
  protected ValueList<Name> m_base;

  //........................................................................

  //----- storage ----------------------------------------------------------

  /** The file this entry will be written to. */
  @Deprecated
  protected @Nullable DMAFile m_file;

  /** The starting position in the file (characters). */
  protected long m_startPos = 0;

  /** The starting position in the file (lines). */
  protected long m_startLine = 0;

  /** The ending position in the file (characters). */
  protected long m_endPos = 0;

  /** The ending position in the file (lines). */
  protected long m_endLine = 0;

  //........................................................................

  static
  {
    extractVariables(AbstractEntry.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- getKey --------------------------------

  /**
   * Get the key uniqueliy identifying this entry.
   *
   * @return   the key for the entry
   *
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T extends AbstractEntry> EntryKey<T> getKey()
  {
    return new EntryKey<T>(getName(), (AbstractType<T>)getType());
  }

  //........................................................................
  //----------------------------- getKeyWidth ------------------------------

  /**
   * Get the maximal width of the keys, including attachments.
   *
   * @return      the maximal key width
   *
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

  //........................................................................
  //------------------------------- getName --------------------------------

  /**
   * Get the name of the entry.
   *
   * @return      the requested name
   *
   */
  @Override
  public String getName()
  {
    if(m_name.isDefined())
      return m_name.get();

    return "";
  }

  //........................................................................
  //-------------------------- getQualifiedName ----------------------------

  /**
   * Get the qualified name for this entry.
   *
   * The qualified name is the hiearchical name of all the base entry and the
   * name of this entry. Hierarchies are seperatede by ':', while muliple base
   * are seperated by '|'.
   *
   * @return      A string of form 'base1:base2:entry|base3:base4:entry'
   *
   */
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

  //........................................................................
  //----------------------------- getBaseName ------------------------------

  /**
   * Get the name of the base entry this entry is based on if this entry is a
   * base entry or the value undefined then getBaseName returns getName().
   *
   * @return      the requested base name
   *
   */
//   public String getBaseName()
//   {
//     return getName();
//   }

  //........................................................................
  //----------------------------- getBaseNames -----------------------------

  /**
   * Get the names of the base entries this entry is based on.
   *
   * @return      the requested base names
   *
   */
  public List<String> getBaseNames()
  {
    List<String> names = new ArrayList<String>();
    for(Name name : m_base)
      names.add(name.get());

    return names;
  }

  //........................................................................
  //---------------------------- getBaseEntries ----------------------------

  /**
   * Get the base entries this abstract entry is based on, if any.
   *
   * @return      the requested base entries; note that an entry can be null
   *              if it is not found
   *
   */
  @Override
  public List<BaseEntry> getBaseEntries()
  {
    if(m_baseEntries == null)
    {
      m_baseEntries = new ArrayList<BaseEntry>();

      // TODO: make this in a single datastore request
      for(Name base : m_base)
        m_baseEntries.add((BaseEntry)DMADataFactory.get()
                          .getEntry(createKey(base.get(),
                                              getType().getBaseType())));
    }

    return m_baseEntries;
  }

  //........................................................................
  //-------------------------- ensureBaseEntries ---------------------------

  /**
   * Make sure that all base entries are available.
   *
   * @return      true if all are available, false if not
   *
   */
  public boolean ensureBaseEntries()
  {
    for(BaseEntry base : getBaseEntries())
      if(base == null)
      {
        m_baseEntries = null;
        return false;
      }

    return true;
  }

  //........................................................................
  //------------------------------ getRefName ------------------------------

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

  //........................................................................
  //--------------------------------- getID --------------------------------

  /**
   * Get the ID of the entry. This can mainly be used for reference purposes.
   * In this case, the lowercased name is equal to the id, which is not true
   * for entries.
   *
   * @return      the requested id
   *
   */
  @Override
  @Deprecated
  public String getID()
  {
    return getName();
  }

  //........................................................................
  //------------------------------- getType --------------------------------

  /**
   * Get the type of the entry.
   *
   * @return      the requested name
   *
   */
  @Override
  @SuppressWarnings("unchecked") // cast
  public <T extends AbstractEntry> AbstractType<T> getType()
  {
    return (AbstractType<T>)m_type;
  }

  //........................................................................
  //----------------------------- getEditType ------------------------------

  /**
   * Get the type of the entry.
   *
   * @return      the requested name
   *
   */
  @Override
  public String getEditType()
  {
    return m_type.toString();
  }

  //........................................................................
  //-------------------------- getExtensionNames ---------------------------

  /**
   * Get an all the names of the extensions.
   *
   * @return      the requested names
   *
   */
  public List<String> getExtensionNames()
  {
    return new ArrayList<String>(m_extensions.keySet());
  }

  //........................................................................
  //---------------------------- hasExtension -----------------------------

  /**
   * Check if the entry (or one of is bases) has an extension with the given
   * class.
   *
   * @param       inExtension the class of the extension to look for
   *
   * @return      true if an extension of this name is present, false if not
   *
   * @undefined   IllegalArgumentException if no extension given
   *
   */
  public boolean hasExtension
    (Class<? extends AbstractExtension<?>> inExtension)
  {
    for(AbstractExtension<?> extension : m_extensions.values())
      if(inExtension.isAssignableFrom(extension.getClass()))
         return true;

    return false;
  }

  //........................................................................
  //---------------------------- hasExtension -----------------------------

  /**
   * Check if the entry (or one of is bases) has an extension with the given
   * name.
   *
   * @param       inExtension the name of the extension to look for
   *
   * @return      true if an extension of this name is present, false if not
   *
   * @undefined   IllegalArgumentException if no extension given
   *
   */
  public boolean hasExtension(String inExtension)
  {
    return m_extensions.keySet().contains(inExtension);
  }

  //........................................................................
  //----------------------------- getExtension -----------------------------

  /**
   * Get the extension given by name.
   *
   * @param   inName the name of the extension
   *
   * @return  the extension found, if any
   *
   */
  public @Nullable AbstractExtension<?> getExtension(String inName)
  {
    return m_extensions.get(inName);
  }

  //........................................................................
  //---------------------------- getExtension ------------------------------

  /**
   * Get the extension with the given class.
   *
   * @param       inExtension the class of the attachment to look for
   * @param       <T> the type of extension to get
   *
   * @return      the extension found or null if not found
   *
   */
  @SuppressWarnings("unchecked")
  public @Nullable <T extends AbstractExtension<?>> T
    getExtension(Class<T> inExtension)
  {
    for(AbstractExtension<?> extension : m_extensions.values())
      if(inExtension.isAssignableFrom(extension.getClass()))
         return (T)extension;

    return null;
  }

  //........................................................................
  //------------------------------ getStorage ------------------------------

  /**
   * Get the storage space this entry is stored in.
   *
   * @return      the storage containing this entry
   *
   * @undefined   may return null if this entry is not stored (yet)
   *
   */
//   public Storage<AbstractEntry> getStorage()
//   {
//     return m_storage;
//   }

  //........................................................................
  //---------------------------- getQuantifiers ----------------------------

  /**
   * Get the current quantifiers.
   *
   * @return      A string with the current quantifiers.
   *
   * @undefined   never
   *
   */
  @Deprecated // now in m_base as a real value
  protected String getQuantifiers()
  {
    // if(m_base.size() > 1 || !m_base.get(0).get().equals(getName()))
    //   return s_baseStart + Strings.toString(m_base, ", ", "")
    //     + s_baseEnd + " ";

    return "";
  }

  //........................................................................
  //------------------------------ getErrors -------------------------------

  /**
   * Get an iterator over all errors in the entry. Returns null if no
   * errors are available.
   *
   * @return      an iterator over all errors or null if none
   *
   */
  public Iterator<BaseError> getErrors()
  {
    if(m_errors == null)
      return new EmptyIterator<BaseError>();

    return m_errors.iterator();
  }

  //........................................................................
  //----------------------------- getBaseValue -----------------------------

  /**
   * Get a value from any of the bases.
   *
   * @param       inExtractor the extractor to get the values
   * @param       inOrdinal   the extractor to get a value for sorting
   *
   * @return      the requested value or null if not found or undefined
   *
   */
//   @MayReturnNull
//   @SuppressWarnings("unchecked") // having to cast base entry to given T
//   protected <T extends BaseEntry, V>
//     V getBaseValue(Extractor<T, V> inExtractor,
//                    @MayBeNull Combiner<V, V> inCombiner)
//   {
//     if(m_baseEntries == null)
//       return null;

//     V result = null;

//     for(BaseEntry base : m_baseEntries)
//     {
//       if(base == null)
//         continue;

//       try
//       {
//         V temp = inExtractor.get((T)base);

//         if(temp != null)
//           if(result == null)
//             result = temp;
//         else
//           if(inCombiner != null)
//             result = inCombiner.combine(result, temp);
//           else
//             Log.warning("requested value not unique");
//       }
//       catch(ClassCastException e)
//       {
//         Log.error("Could not properly cast base '" + base.getName() + "': "
//                   + e);
//         throw e;
//       }
//     }

//     return result;
//   }

  //........................................................................
  //----------------------------- getBaseValue -----------------------------

  /**
   * Get a value from the base(s).
   *
   * @param       inName     the name of the value to get
   * @param       inCombine  how to combine multiple values to the final one
   * @param       inDM       true if setting for dm, false else
   *
   * @return      the value found or null for none
   *
   */
//   public Value getBaseValue(String inName, Combine inCombine, boolean inDM)
//   {
//     return getBaseValue(inName, inCombine, inDM, null);
//   }

  //........................................................................
  //----------------------------- getBaseValue -----------------------------

  /**
   * Get a value from the base(s).
   *
   * @param       inName     the name of the value to get
   * @param       inCombine  how to combine multiple values to the final one
   * @param       inDM       true if setting for dm, false else
   * @param       inStart    the value to start from, if any.
   *
   * @return      the value found or null for none
   *
   */
//   public Value getBaseValue(String inName, Combine inCombine, boolean inDM,
//                             @MayBeNull Value inStart)
//   {
//     Value result = null;

//     if(inStart != null)
//       result = inStart.clone();

//     if(m_baseEntries == null)
//       return result;

//     boolean checkDM = !inDM;
//     for(BaseEntry base : m_baseEntries)
//     {
//       if(base == null)
//         continue;

//       if(checkDM)
//       {
//         Pair<ValueGroup, Variable> var = base.getVariable(inName);

//         if(var == null)
//           return null;

//         if(var.second().isDMOnly())
//           return null;

//         // Only check once
//         checkDM = false;
//       }

//       Value<? extends Value> value =
//         base.getBaseValue(inName, inCombine, inDM, base.getValue(inName));

//       if(value == null || !value.isDefined())
//         continue;

//       if(inCombine == Combine.FIRST)
//         return value;

//       if(result == null)
//         result = value.clone();

//       if(inCombine == Combine.MINIMUM || inCombine == Combine.MAXIMUM)
//       {
//         int compare = value.compareTo(result);

//         if(compare < 0 && inCombine == Combine.MINIMUM)
//           result = value;
//         else
//           if(compare > 0 && inCombine == Combine.MAXIMUM);
//       }
//       else
//       {
//         if(inCombine == Combine.MODIFY)
//         {
//           if(!(result instanceof Modifiable))
//           {
//             Value old = result;
//             result = new Modifiable<Value>(old.getBase());
//             addToModifiable((Modifiable<?>)result, old, base.getName());
//           }

//           addToModifiable((Modifiable<?>)result, value, base.getName());
//         }
//         else if(inCombine == Combine.ADD)
//         {
//           if(result instanceof Modifiable)
//             addToModifiable((Modifiable<?>)result, value, base.getName());
//           else
//             if(result instanceof SimpleText)
//             {
//               ((SimpleText)value).set
//                 ("\\par\\right{\\scriptsize{\\italic{\\color{color-light}{"
//                  + base.getName() + ":}}}}"
//                  + ((SimpleText)value).get());
//               result.addTo(value);
//             }
//             else
//               result.addTo(value);
//         }
//       }
//     }

//     return result;
//   }

  //........................................................................
  //------------------------------- getFile --------------------------------

  /**
   * Get the file this entry is stored in.
   *
   * @return      the file stored in, if any
   *
   */
  @Deprecated
  public @Nullable DMAFile getFile()
  {
    return m_file;
  }

  //........................................................................
  //------------------------------- getFiles -------------------------------

  /**
   * Get the files associated with this entry.
   *
   * @return      the associated files
   *
   */
  public List<DMAData.File> getFiles()
  {
    if(m_files == null)
      m_files = DMADataFactory.get().getFiles(this);

    return m_files;
  }

  //........................................................................
  //----------------------------- getMainFile ------------------------------

  /**
   * Get the main file associated with this entry.
   *
   * @return      the associated main file
   *
   */
  public @Nullable DMAData.File getMainFile()
  {
    for(DMAData.File file : getFiles())
      if("main".equals(file.getName()))
        return file;

    return null;
  }

  //........................................................................
  //--------------------------------- dma ----------------------------------

  /**
   *
   *
   * @param
   *
   * @return
   *
   */
  public FormattedText dmaValues()
  {
    return new FormattedText(formatValues() + ".");
  }

  //........................................................................

  //------------------------------- collect --------------------------------

  /**
   *
   *
   * @param
   *
   * @return
   *
   */
  public <T extends Value<T>> Combined<T> collect(String inName)
  {
    Combined<T> combined = new Combined<T>(inName, this);
    collect(inName, combined);

    return combined;
  }

  //........................................................................

  @Override
  protected <T extends Value<T>> void collect(String inName,
                                              Combined<T> ioCombined)
  {
    super.collect(inName, ioCombined);

    for(BaseEntry base : getBaseEntries())
      if(base != null)
        base.collect(inName, ioCombined);

    for(AbstractExtension<?> extension : m_extensions.values())
      extension.collect(inName, ioCombined);
  }

  //------------------------- computeIndexValues ---------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @return      a multi map of values per index name
   *
   */
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = HashMultimap.create();
    for(AbstractExtension<? extends AbstractEntry> extension
          : m_extensions.values())
      extension.computeIndexValues(values);

    for(String extension : m_extensions.keySet())
      values.put(Index.Path.EXTENSIONS, extension);

    return values;
  }

  //........................................................................

  //------------------------------ hasErrors -------------------------------

  /**
   * Determine if the entry has some errors stored for it.
   *
   * @return      true if there are errors, false if not
   *
   */
  public boolean hasErrors()
  {
    return m_errors != null;
  }

  //........................................................................
  //------------------------------ isChanged -------------------------------

  /**
    * Check if the file has been changed (and thus might need saving).
    *
    * @return      true if changed, false if not
    *
    */
  public boolean isChanged()
  {
    return m_changed;
  }

  //........................................................................
  //------------------------------- matches --------------------------------

  /**
   * Check if this entry matches the given search string or pattern.
   *
   * @param       inPattern the pattern to search for
   *
   * @return      true if it matches, false if not
   *
   * @undefined   never
   *
   */
//   public boolean matches(String inPattern)
//   {
//     if(inPattern == null)
//       return false;

//     return getName().matches("(?i).*" + inPattern + ".*");
//   }

  //........................................................................
  //-------------------------------- equals --------------------------------

  /**
   * Check for equality of the given errors.
   *
   * @param       inOther the object to compare to
   *
   * @return      true if equal, false else
   *
   */
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

  //........................................................................
  //------------------------------- hashCode -------------------------------

  /**
   * Compute the hash code for this class.
   *
   * @return      the hash code
   *
   */
  @Override
  public int hashCode()
  {
    return getID().hashCode();
  }

  //........................................................................
  //------------------------------ compareTo -------------------------------

  /**
   * Compare this value to another one.
   *
   * @param       inOther the value to compare to
   *
   * @return      -1 for less than, 0 for equal and +1 for greater than the
   *              object given
   *
   */
  @Override
  public int compareTo(AbstractEntry inOther)
  {
    return getID().compareTo(inOther.getID());
  }

  //........................................................................
  //------------------------------ isBasedOn -------------------------------

  /**
   * Checks whether this entry is based on the given one, directly or
   * indirectly.
   *
   * @param      inBase the base entry to look for
   *
   * @return     true if this entry is directly or indirectly based on the
   *             given entry, false else
   *
   */
  public boolean isBasedOn(BaseEntry inBase)
  {
    if(m_baseEntries == null)
      return false;

    for(BaseEntry base : m_baseEntries)
      if(base == inBase || (base != this && inBase.isBasedOn(base)))
        return true;

    return false;
  }

  //.......................................................................
  //------------------------------ isValueIn -------------------------------

  /**
   * Check if the given value is in the group value with the given key.
   *
   * @param       inValue the value to look for
   * @param       inKey   the key of the value to check in
   *
   * @return      true if it is in, false if it is not
   *
   */
  @SuppressWarnings("unchecked")
  public boolean isValueIn(String inValue, String inKey)
  {
    if(super.isValueIn(inValue, inKey))
      return true;

    for(BaseEntry entry : getBaseEntries())
      if(entry.isValueIn(inValue, inKey))
        return true;

    return false;
  }

  //........................................................................
  //------------------------------ isValue -------------------------------

  /**
   * Check if the given value has the value given.
   *
   * @param       inValue the value to look for
   * @param       inKey   the key of the value to check in
   *
   * @return      true if it is in, false if it is not, null if undefined or
   *              invalid
   *
   */
  @SuppressWarnings("unchecked")
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

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert the object to a String for printing.
   *
   * @return      the String representation
   *
   */
  @Override
  public String toString()
  {
    StringBuilder result = new StringBuilder();

    if(m_leadingComment.isDefined())
      result.append(m_leadingComment);

    result.append(m_type);
    result.append(' ');

    if(m_extensions.size() > 0)
    {
      result.append("with ");
      result.append(Strings.toString(m_extensions.keySet(), ", ",
                                     "incomplete"));
      result.append(" ");
    }

    result.append(m_name);
    result.append(' ');

    result.append(getQuantifiers());

    result.append(s_introducer);
    result.append("\n\n");
    result.append(formatValues());
    result.append(s_delimiter);
    result.append("\n");

    if(m_trailingComment.isDefined())
      result.append(m_trailingComment);

    return result.toString();
  }

  //........................................................................

  //----------------------------- formatValues -----------------------------

  /**
   * Format all the values contained in the entry for printing.
   *
   * @return      a String with a representation of all values
   *
   */
  protected String formatValues()
  {
    StringBuilder result = new StringBuilder();

    boolean first = true;

    int width = getKeyWidth();

    formatValues(result, first, width);

    return result.toString();
  }

  //........................................................................
  //----------------------------- getAllValues -----------------------------

  /**
   * Get all the values in this entry, including attachments.
   *
   * @return      a map with all values by key
   *
   */
  public Map<String, Value<?>> getAllValues()
  {
    Map<String, Value<?>> values = new HashMap<String, Value<?>>();

    Variables vars = getVariables();

    for(Variable var : vars)
    {
      if(!var.isStored())
        continue;

      if(var instanceof ExtensionVariable
         && !hasExtension(((ExtensionVariable)var).getExtension()))
        continue;

      Value<?> value = var.get(this);

      // We don't store this if we don't have a value.
      if(value == null) // || (!value.isDefined() && !value.hasExpression()))
        continue;

      values.put(var.getKey(), value);
    }

    return values;
  }

  //........................................................................

  //----------------------------- computeValue -----------------------------

  /**
   * Get a value for printing.
   *
   * @param     inKey  the name of the value to get
   * @param     inDM   true if formattign for dm, false if not
   *
   * @return    a value handle ready for printing
   *
   */
  @Override
  @Deprecated
  public @Nullable ValueHandle<?> computeValue(String inKey, boolean inDM)
  {
    if(inKey.contains(":"))
    {
      String []parts = inKey.split(":");
      AbstractExtension<?> extension = getExtension(parts[0]);
      if(extension != null)
        return extension.computeValue(parts[1], inDM);
    }

    if("title".equals(inKey))
    {
      return new FormattedValue
        (new Title(computeValue("name", inDM).format(this, inDM, true),
                   "entrytitle"), computeValue("name", inDM).value(this, inDM),
         "title");
    }

    if("desc".equals(inKey))
    {
      List<Object> commands = new ArrayList<Object>();
      ValueHandle<?> subtitle = computeValue("subtitle", inDM);
      if(subtitle != null)
        commands.add(subtitle.format(this, inDM, true));

      commands.add(computeValue("description", inDM).format(this, inDM, true));
      commands.add(computeValue("short description", inDM)
                   .format(this, inDM, true));

      return new FormattedValue(new Divider("desc",
                                            new Command(commands.toArray())),
                                null, "desc");
    }

    if("clear".equals(inKey))
      // we need a non empty string here, because when parsing trailing empty
      // arguments are ignored.
      return new FormattedValue(new Divider("clear", " "), null, "clear");

    if("errors".equals(inKey))
    {
      Object value = null;

      if(hasErrors())
      {
        ArrayList<Object>errors = new ArrayList<Object>();
        for(Iterator<BaseError> i = m_errors.iterator(); i.hasNext(); )
        {
          errors.add(i.next().format());

          if(i.hasNext())
            errors.add(new Linebreak());
        }

        value = new Command(errors);
      }
      else
        value = null;

      return new FormattedValue(value, null, "errors")
        .withPlural("errors")
        .withDM(true);
    }

    if("as dma".equals(inKey))
      return new FormattedValue(new ImageLink("/icons/doc-dma.png",
                                              getName(), getName() + ".dma",
                                              "doc-link"), null, "as dma")
        .withDM(true);

    if("as pdf".equals(inKey))
      return new FormattedValue(new ImageLink("/icons/doc-pdf.png",
                                              getName(), getName() + ".pdf",
                                              "doc-link"), null, "as pdf");

    if("as text".equals(inKey))
      return new FormattedValue(new ImageLink("/icons/doc-txt.png",
                                              getName(), getName() + ".txt",
                                              "doc-link"), null, "as text")
        .withDM(true);

    if("label".equals(inKey))
      return new FormattedValue
        (new Image(Files.concatenate
                   ("/icons/labels", getType().getClassName()) + ".png",
                   "label"), null, "label");

    if("par".equals(inKey))
      return new FormattedValue(new Par(), null, "par");

    if("listlink".equals(inKey))
    {
      // we have to add a wrapping div to host the id there; when we assing the
      // body to the innerHTML of the page, script tags will be ignored and
      // thus the id would be missing
      String id = "linkrow-user-" + Encodings.toCSSString(getName());
      return new FormattedValue
        (new Divider(id, "", new Script
                     ("util.linkRow(document.getElementById('" + id + "'), "
                      + Encodings.toJSString(getPath()) + ");")),
         null, "listlink");
    }

    if("base".equals(inKey))
    {
      String type;
      if(this instanceof Entry)
        type = ((Type)getType()).getBaseType().getLink();
      else
        type = getType().getLink();

      List<Object> entries = new ArrayList<Object>();
      List<Object> values = new ArrayList<Object>();
      for(String entry : getBaseNames())
      {
        if(!entries.isEmpty())
          entries.add(", ");

        entries.add(new Link(entry, "/" + type + "/" + entry));
        values.add(entry);
      }

      return new FormattedValue(new Command(entries),
                                Strings.toString(values, ", ", ""), "base")
        .withDM(true)
        .withEditable(true)
        .withEditType("list(, )#name");
    }

    if("short description".equals(inKey))
      return new FormattedValue
        (new Divider("short-description",
                     computeValue("_short description", inDM)
                     .format(this, inDM, true)), null, "short-desc");

    if("extensions".equals(inKey))
      return new FormattedValue(Strings.toString(m_extensions.keySet(), ", ",
                                                 ""), null, "extensions")
        .withEditable(true)
        .withEditType("multiselection")
        .withEditChoices("armor||commodity||composite||container||counted"
                         + "||incomplete||light||magic||multiple||multiuse"
                         + "||timed||weapon||wearable");

    ValueHandle<?> value = super.computeValue(inKey, inDM);
    if(value != null)
      return value;

    // The value is not defined here, but might be in a base.
    // String key;
    // if(inKey.startsWith("_"))
    //   key = inKey.substring(1);
    // else
    //   key = inKey;

    return null;
  }

  //........................................................................
  //------------------------------- compute --------------------------------

  /**
   * Compute a value for a given key, taking base entries into account if
   * available.
   *
   * @param    inKey the key of the value to compute
   *
   * @return   the compute value
   *
   */
  @Override
  public @Nullable Object compute(String inKey)
  {
    if("extensions".equals(inKey))
    {
      List<Name> values = new ArrayList<Name>();
      for (String extension : m_extensions.keySet())
        values.add(new Name(extension).withTemplate("extension"));

      ValueList<Name> list;
      if(values.isEmpty())
        list = new ValueList<Name>(new Name(), ", ");
      else
        list = new ValueList<Name>(values, ", ");

      list.withEditType("multiselection")
        .withChoices("armor||commodity||composite||container||counted"
                     + "||incomplete||light||magic||multiple||multiuse||timed"
                     + "||weapon||wearable")
        .withTemplate("extensions");

      return list;
    }

    // check extensions for a value
    for(AbstractExtension<?> extension : m_extensions.values())
    {
      Object value = extension.compute(inKey);
      if(value != null)
        return value;
    }

    return super.compute(inKey);
  }

  //........................................................................
  //--------------------------- shortPrintCommand --------------------------

  /**
   * Print the item to the document, in the general section (short).
   *
   * @param       inDM       true if setting for dm, false if not
   *
   * @return      the command representing this item in a list
   *
   */
//   protected PrintCommand shortPrintCommand(boolean inDM)
//   {
//     return printCommand(inDM, true);
//   }

  //........................................................................
  //---------------------------- getListCommands ---------------------------

  /**
   * Get all the commands for printing all the lists.
   *
   * @param       inDM flag if printing for dm or not
   *
   * @return      a map with a list type and the corresponding commands for
   *              printing
   *
   * @undefined   never
   *
   */
//   public ListCommand getListCommands(boolean inDM)
//   {
//     ListCommand commands = new ListCommand();

//     for(Iterator<AbstractAttachment> i = getAttachments(); i.hasNext(); )
//       i.next().addListCommands(commands, inDM);

//     return commands;
//   }

  //........................................................................

  //------------------------------- getPath --------------------------------

  /**
   * Get the path to this entry.
   *
   * @return      the path to read this entry
   *
   */
  public String getPath()
  {
    return "/" + getType().getLink() + "/" + getName();
  }

  //........................................................................
  //---------------------------- getNavigation -----------------------------

  /**
   * Get the navigation information to this entry.
   *
   * @return      an array with pairs for caption and link per navigation entry
   *
   */
  public String [] getNavigation()
  {
    return new String [] {
      getType().getLink(), "/" + getType().getMultipleLink(),
      getName(), "/" + getType().getLink() + "/" + getName(),
    };
  }

  //........................................................................
  //-------------------------- getListNavigation ---------------------------

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

  //........................................................................
  //------------------------------- canEdit --------------------------------

  /**
   * Check if the given user is allowed to edit the value with the given key.
   *
   * @param       inKey  the key to edit
   * @param       inUser the user trying to edit
   *
   * @return      true if the value can be edited by the user, false if not
   *
   */
  public boolean canEdit(String inKey, BaseCharacter inUser)
  {
    return inUser != null && inUser.hasAccess(BaseCharacter.Group.ADMIN);
  }

  //........................................................................
  //----------------------------- isShownTo -------------------------------

  /**
   * Check if the given user is allowed to see the entry.
   *
   * @param       inUser the user trying to edit
   *
   * @return      true if the entry can be seen, false if not
   *
   */
  public boolean isShownTo(BaseCharacter inUser)
  {
    return true;
  }

  //........................................................................
  //------------------------------ createKey -------------------------------

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
  public static <T extends AbstractEntry> EntryKey<T>
    createKey(String inID, AbstractType<T> inType)
  {
    return new EntryKey<T>(inID, inType);
  }

  //........................................................................
  //------------------------------ createKey -------------------------------

  /**
   * Create a key for the given values. If parent id or type are null, no parent
   * will be used.
   *
   * @param       inID         the id of the entry to create the key for
   * @param       inType       the type of the id
   * @param       inParentID   the id of parent entry, if any
   * @param       inParentType the type of the parent entry, if any
   *
   * @param       <T> the type of entries to create for
   *
   * @return      the created key
   *
   */
  @SuppressWarnings("unchecked")
  public static <T extends AbstractEntry, U extends AbstractEntry>
    EntryKey<T> createKey
    (String inID, AbstractType<T> inType,
     @Nullable String inParentID,
     @Nullable AbstractType<T> inParentType)
  {
    if(inParentID == null || inParentType == null)
      return createKey(inID, inType);

    return new EntryKey<T>(inID, inType,
                           new EntryKey<T>(inParentID, inParentType));
  }

  //........................................................................
  //-------------------------- adjustCombination ---------------------------

  /**
   * Adjust the value for the given name for any special properites.
   *
   * @param       inName        the name of the value to adjust
   * @param       ioCombination the combinstaion to adjust
   * @param       <V>           the real type of the value combined
   *
   */
  // @Override
  // public <V extends Value<?>> void
  //           adjustCombination(String inName,
  //                             Combination<V> ioCombination)
  // {
  //   // now ask all the extensions if they want to contribute something
  //   for(AbstractExtension<? extends AbstractEntry> extension
  //         : m_extensions.values())
  //     extension.adjustCombination(inName, ioCombination);

  //   super.adjustCombination(inName, ioCombination);
  // }

  //........................................................................
  //---------------------------- addModifiers ------------------------------

  /**
   * Add current modifiers to the given map.
   *
   * @param       inName        the name of the value to modify
   * @param       ioModifers    the map of modifiers
   *
   */
  @Override
  public void addModifiers(String inName, Map<String, Modifier> ioModifiers)
  {
    // add the modifiers from all base values
    for(BaseEntry base : getBaseEntries())
    {
      if(base == null)
        continue;

      base.addModifiers(inName, ioModifiers);
    }

    // now ask all the extensions if they want to contribute something
    for(AbstractExtension<? extends AbstractEntry> extension
          : m_extensions.values())
      extension.addModifiers(inName, ioModifiers);

    super.addModifiers(inName, ioModifiers);
  }

  //........................................................................
  //--------------------------- addContributions ---------------------------

  /**
   * Add contributions for this entry to the given list.
   *
   * @param       inName          the name of the value to contribute to
   * @param       ioContributions the list of contributions to add to
   *
   */
  @Override
  public void addContributions
    (String inName, List<Contribution<? extends Value<?>>> ioContributions)
  {
    super.addContributions(inName, ioContributions);

    for(BaseEntry base : getBaseEntries())
      if(base != null)
        base.addContributions(inName, ioContributions);

    for(AbstractExtension<?> extension : m_extensions.values())
      extension.addContributions(inName, ioContributions);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- set ----------------------------------

  /**
   * Set the value for the given key.
   *
   * @param       inKey  the name of the key to set the value for
   * @param       inText the text to set the value to
   *
   * @return      the part of the string that could not be parsed
   *
   */
  @Override
  public @Nullable String set(String inKey, String inText)
  {
    // we have to treat the name specially, as it is not a readable value
    if("name".equals(inKey))
    {
      setName(inText);
      return null;
    }

    // base is also special
    if("base".equals(inKey))
    {
      // TODO: this is very inefficient!
      m_base = m_base.as(new ArrayList<Name>());
      m_baseEntries = null;
      if(!inText.startsWith(Value.UNDEFINED))
        for(String base : inText.split(",\\s*"))
          if(base != null && !base.isEmpty())
            addBase(base);

      // setup extensions from base entries
      setupExtensions();

      changed();
      return null;
    }

    if("extensions".equals(inKey))
    {
      List<String> extensions = Arrays.asList(inText.split(",\\s+"));
      for(Iterator<String> i = m_extensions.keySet().iterator(); i.hasNext(); )
        if(!extensions.contains(i.next()))
          i.remove();

      for(String extension : extensions)
        if(!m_extensions.containsKey(extension))
          addExtension(extension);

      return null;
    }

    if("dmaValues".equals(inKey))
    {
      readValues(new ParseReader(new StringReader(inText), "dma values"));

      return null;
    }

    return super.set(inKey, inText);
  }

  //........................................................................
  //------------------------------- setName --------------------------------

  /**
   * Set the name of the entry.
   *
   * @param       inName the new name
   *
   */
  @SuppressWarnings("unchecked") // cast for name
  public void setName(String inName)
  {
    m_name = m_name.as(inName);
    m_leadingComment = m_leadingComment.as("#----- " + m_name + "\n\n");
    if(!m_trailingComment.isDefined())
      m_trailingComment = m_trailingComment.as("\n#.....\n");

    changed();
  }

  //........................................................................
  //-------------------------- setLeadingComment ---------------------------

  /**
   * Set the comment at the beginning of the entry.
   *
   * @param       inComment the new comment
   *
   */
  public void setLeadingComment(Comment inComment)
  {
    m_leadingComment = inComment;
  }

  //........................................................................
  //------------------------- setTrailingComment ---------------------------

  /**
   * Set the comment at the end of the entry.
   *
   * @param       inComment the new comment
   *
   */
  public void setTrailingComment(Comment inComment)
  {
    m_trailingComment = inComment;
  }

  //........................................................................
  //------------------------------ updateKey -------------------------------

  /**
   * Update the any values that are related to the key with new data.
   *
   * @param       inKey the new key of the entry
   *
   */
  public void updateKey(EntryKey<? extends AbstractEntry> inKey)
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
  //--------------------------- setupExtensions ----------------------------

  /**
   * Setup all auto extensions from base entries.
   *
   */
  public void setupExtensions()
  {
    for(AbstractExtension<?> extension : m_extensions.values())
      for(String name
            : AbstractExtension.getAutoExtensions(extension.getClass()))
      {
        if(isBase())
        {
          if(name.startsWith("base "))
            addExtension(name.substring(5));
        }
        else
          if(!name.startsWith("base "))
            addExtension(name);
      }

    if(!isBase())
      for(BaseEntry base : getBaseEntries())
      {
        if(base == null)
          continue;

        for(AbstractExtension<?> extension : base.m_extensions.values())
          for(String name
                : AbstractExtension.getAutoExtensions(extension.getClass()))
            if(!name.startsWith("base "))
              addExtension(name);
      }
  }

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
  @SuppressWarnings("unchecked") // calling complete on base type
  public static @Nullable AbstractEntry read(ParseReader inReader)
  {
    if(inReader.isAtEnd())
      return null;

    ParseReader.Position start = inReader.getPosition();

    //----- leading comment ------------------------------------------------

    Comment leading = new Comment(s_maxLeadingComments, s_maxLeadingLines);
    leading = leading.read(inReader);

    //......................................................................
    //----- type -----------------------------------------------------------

    String typeName = "";
    String className = "";
    Class<? extends AbstractEntry> entry = null;
    for(int i = 0; i < s_keywordWords; i++)
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
          Class.forName(s_package + className);

        // could load class
        break;
      }
      catch(ClassNotFoundException e)
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
    if(!result.m_leadingComment.isDefined() && result.m_name.isDefined())
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
  @SuppressWarnings("unchecked") // cast for name
  protected boolean readEntry(ParseReader inReader)
  {
    if(inReader.isAtEnd())
      return false;

    //----- extension ------------------------------------------------------

    ArrayList<String> extensions = new ArrayList<String>();

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
      if(!inReader.expect(s_introducer))
      {
        inReader.logWarning(pos, "entry.missing.introducer",
                            "introducer is " + s_introducer);

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

  //---------------------------- addExtension ------------------------------

  /**
   * Add an extension denoted by a String to the entry.
   *
   * @param       inName the name of the extension to add
   *
   * @return      the extension added or null if none added (already there or
   *              not found)
   *
   */
  @SuppressWarnings("unchecked")
  public @Nullable AbstractExtension<?> addExtension(String inName)
  {
    if(m_extensions.containsKey(inName) || inName.isEmpty())
      return null;

    // TODO: clean up names if tags are not used anmore
    String []names = inName.split(":");

    String name;
    // if(names.length > 1)
    //   name = names[1];
    // else
    //   name = names[0];

    if(isBase())
      name = "Base " + inName;
    else
      name = inName;

    try
    {
      Class<?> cls = Class.forName(Classes.toClassName
                                   (name,
                                    "net.ixitxachitls.dma.entries.extensions"));

      // can't use the generic class type here, because generic class arrays
      // cannot be built
      AbstractExtension<?> extension = null;

      // find the constructor to use (getConstructor does not acceptably treat
      // derivations, unfortunately)
      Object []arguments = new Object[names.length + 1];
      arguments[0] = this;

      for(int i = 0; i < names.length; i++)
        arguments[i + 1] = names[i];

      loop : for(Constructor<?> constructor : cls.getConstructors())
      {
        Class<?> []types = constructor.getParameterTypes();

        // check if we have the right number of arguments
        if(types.length != arguments.length)
          continue;

        for(int i = 0; i < types.length; i++)
          if(!types[i].isAssignableFrom(arguments[i].getClass()))
            continue loop;

        extension = (AbstractExtension)constructor.newInstance(arguments);
        break;
      }

      if(extension == null)
        return null;

      addExtension(inName, extension);

      return extension;
    }
    catch(ClassNotFoundException e)
    {
      Log.warning("could not find class for extension " + name
                  + ", extension ignored");
      e.printStackTrace(System.out);
    }
    catch(InstantiationException e)
    {
      Log.warning("could not instantiate class for extension " + name
                  + ", extension ignored");
      e.printStackTrace(System.out);
    }
    catch(IllegalAccessException e)
    {
      Log.warning("could access constructor for extension " + name
                  + ", extension ignored");
      e.printStackTrace(System.out);
    }
    catch(java.lang.reflect.InvocationTargetException e)
    {
      Log.warning("could not invoke constructor for extension " + name
                  + ", extension ignored (" + e.getCause() + ")");
      e.printStackTrace(System.out);
    }

    return null;
  }

  //........................................................................
  //---------------------------- addExtension ------------------------------

  /**
   * Add the given extension to the entry.
   *
   * @param       inName      the name of the extension added
   * @param       inExtension the extension to add
   *
   */
  public void addExtension
    (String inName, AbstractExtension<? extends AbstractEntry> inExtension)
  {
    m_extensions.put(inName, inExtension);
  }

  //........................................................................
  //---------------------------- addExtensions -----------------------------

  /**
   * Add a list of extensions.
   *
   * @param       inNames the names of the extensions to add
   *
   */
  protected void addExtensions(List<String> inNames)
  {
    addExtensions(inNames.iterator());
  }

  //........................................................................
  //---------------------------- addExtensions -----------------------------

  /**
   * Add the attachements given to the entry.
   *
   * @param       inNames the names of the extensions to add
   *
   */
  protected void addExtensions(Iterator<String> inNames)
  {
    for(Iterator<String> i = inNames; i.hasNext(); )
      addExtension(i.next());
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

    BaseEntry entry = (BaseEntry)
      DMADataFactory.get().getEntry(createKey(inName, baseType));
    if(entry == null)
      Log.warning("base " + getType() + " '" + inName + "' not found");
    // else
    //   addExtensions(entry.getExtensionNames());

    m_base = m_base.asAppended(m_base.newElement().as(inName));

    if(m_baseEntries == null)
      m_baseEntries = new ArrayList<BaseEntry>();

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
  //-------------------------------- addTo ---------------------------------

  /**
   * Add the entry to the given file (also removing it from any current file).
   *
   * @param       inFile the file to add to
   *
   */
  public void addTo(DMAFile inFile)
  {
    if(m_file != null)
    {
      m_file.remove(this);
      m_startPos = -1;
      m_startLine = -1;
      m_endPos = -1;
      m_endLine = -1;
    }

    m_file = inFile;
  }

  //........................................................................

  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled.
   *
   * @undefined   never
   *
   */
//   @SuppressWarnings("unchecked") // don't know real type of value
//   public void complete()
//   {
//     // can't complete anything if we don't have a base value
//     if(m_complete)
//       return;

//     // setup the default comments
//     String name = getName();


//     // Copy over the values of the base entries (if any)
//     completeVariables(m_baseEntries);

//     // complete the attachments
//     for(Iterator<AbstractAttachment> i = getAttachments(); i.hasNext(); )
//       i.next().complete();

//     m_complete = true;
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

  //------------------------------- addError -------------------------------

  /**
   * Add the given error to the entry.
   *
   * @param       inError the error to add
   *
   */
  public void addError(BaseError inError)
  {
    if(m_errors == null)
      m_errors = new ArrayList<BaseError>();

    m_errors.add(inError);
  }

  //........................................................................
  //------------------------------ removeError -----------------------------

  /**
   * Remove the given error to the entry.
   *
   * @param       inError the error to remove
   *
   */
  protected void removeError(BaseError inError)
  {
    if(m_errors == null)
      return;

    for(Iterator<BaseError> i = m_errors.iterator(); i.hasNext(); )
      if(i.next() == inError)
        i.remove();

    if(m_errors.size() == 0)
      m_errors = null;
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

    if(m_changed && m_file != null)
      m_file.changed();
  }

  //........................................................................
  //--------------------------------- save ---------------------------------

  /**
   * Save the entry if it has been changed.
   *
   * @return      true if saved, false if not
   *
   */
  public boolean save()
  {
    if(!m_changed)
      return false;

    return DMADataFactory.get().update(this);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //-------------------------- computeExpressions --------------------------

  /**
   * Compute the expressions embedded in the given string and replace all
   * possible variables.
   *
   * @param       inText   the text to replace in
   * @param       inValues a map with all the values to replace.
   *
   * @return      the computed string
   *
   */
  public String computeExpressions(String inText,
                                   @Nullable Parameters inParameters)
  {
    // TODO: make this more generic and move it to a separate class
    String text = inText;
    StringBuffer result = new StringBuffer();

    if(inParameters != null)
    {
      Matcher matcher = s_varPattern.matcher(text);
      while(matcher.find())
      {
        Value<?> value = inParameters.getValue(matcher.group(1));
        if(value != null && value.isDefined())
          matcher.appendReplacement(result, value.toString().replace('$', '_'));
      }

      matcher.appendTail(result);

      text = result.toString();
      result = new StringBuffer();
    }

    Matcher matcher = s_expPattern.matcher(text);

    while(matcher.find())
      matcher.appendReplacement(result,
                                "" + computeExpression(matcher.group(1)));

    matcher.appendTail(result);

    return result.toString();
  }

  private String computeExpression(String inExpression)
  {
    inExpression = inExpression.replaceAll("[ \t\n\f\r]", "");

    StringTokenizer tokens =
      new StringTokenizer(inExpression, "()+-*/,^", true);

    return computeExpression(inExpression, tokens);
  }

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

      for(String range : ranges) {
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

      for(String option : options) {
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

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends ValueGroup.Test
  {
    //----- init -----------------------------------------------------------

    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      AbstractEntry entry =
        new AbstractEntry("just a test",
                          new AbstractType.Test.TestType<AbstractEntry>
                          (AbstractEntry.class)) {
          @SuppressWarnings("unchecked")
          @Override
          public <T extends AbstractEntry> EntryKey<T> getKey()
          {
            return new EntryKey<T>("guru", (AbstractType<T>)BaseEntry.TYPE);
          }
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

      assertEquals("create", "base entry $undefined$ =\n\n.\n",
                   entry.getType().create().toString());

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
    }

    //......................................................................
    //----- access ---------------------------------------------------------

    /** Check access to the object. */
//     @org.junit.Test
//     public void access()
//     {
//       AbstractEntry entry = new AbstractEntry("name");

//       assertTrue("unrestricted",
//                  entry.getType().allows(BaseCharacter.Group.GUEST));
//       assertTrue("unrestricted",
//                  entry.getType().allows(BaseCharacter.Group.USER));
//       assertTrue("unrestricted",
//                  entry.getType().allows(BaseCharacter.Group.DM));
//       assertTrue("unrestricted",
//                  entry.getType().allows(BaseCharacter.Group.ADMIN));

//       entry.getType().withAccess(BaseCharacter.Group.DM);

//       assertFalse("restricted",
//                   entry.getType().allows(BaseCharacter.Group.GUEST));
//       assertFalse("restricted",
//                   entry.getType().allows(BaseCharacter.Group.USER));
//       assertTrue("restricted",
//                  entry.getType().allows(BaseCharacter.Group.DM));
//       assertTrue("restricted",
//                  entry.getType().allows(BaseCharacter.Group.ADMIN));
//     }

    //......................................................................
    //----- indices --------------------------------------------------------

    /** Test index generation. */
//     @org.junit.Test
//     public void indices()
//     {
//       BaseCampaign.GLOBAL.m_bases.clear();

//       BaseEntry entry1 = new BaseItem("name1");
//       BaseEntry entry2 = new BaseItem("name2");

//       entry1.addError(new BaseError("error id"));
//       entry2.addAttachment("weapon");
//       entry2.store(new net.ixitxachitls.dma.data
//                    .DMAFile("name-file", null, BaseCampaign.GLOBAL));

//       BaseCampaign.GLOBAL.add(entry1);
//       // entry 2 added by store above

//       // compare
//       assertEquals("compare", 0, entry1.compareTo(entry1));
//       assertEquals("compare", 0, entry2.compareTo(entry2));
//       assertEquals("compare", -1, entry1.compareTo(entry2));
//       assertEquals("compare", +1, entry2.compareTo(entry1));

//       // storage
//       assertNull("storage", entry1.getStorage());
//       assertNotNull("storage", entry2.getStorage());

//       // errors
//       Iterator<BaseError> i = entry1.getErrors();
//       assertEquals("error",
//                  "Base Error: [error id] no definition found for this error",
//                    i.next().toString());
//       assertFalse("error", i.hasNext());
//       assertTrue("has errors", entry1.hasErrors());
//       assertFalse("has errors", entry2.hasErrors());

//       m_logger.addExpected("WARNING: cannot find file 'name-file'");
//       m_logger.verify();

//       for(net.ixitxachitls.dma.entries.indexes.Index<?> index : s_indexes)
//       {
//         if("General".equals(index.getGroup())
//            && "Attachments".equals(index.getTitle()))
//         {
//           assertEquals("attachments", 1,
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries()).size());
//           assertEquals("attachments", "weapon",
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries()).iterator()
//                        .next());

//           assertFalse("attachments", index.matchesName("weapon", entry1));
//           assertTrue("attachments", index.matchesName("weapon", entry2));
//           assertFalse("attachments", index.matchesName("armor", entry1));

//           continue;
//         }

//         if("Index".equals(index.getGroup())
//            && "Index".equals(index.getTitle()))
//         {
//           assertEquals("index", 1,
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries()).size());
//           assertEquals("index", "base items",
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries()).iterator()
//                        .next());

//           assertTrue("index", index.matchesName("base items", entry1));
//           assertTrue("index", index.matchesName("base items", entry2));
//           assertFalse("index", index.matchesName("base item", entry1));

//           continue;
//         }

//         if("General".equals(index.getGroup())
//            && "Errors".equals(index.getTitle()))
//         {
//           assertEquals("errors", 1,
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries()).size());
//           assertEquals("errors", "index",
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries()).iterator()
//                        .next());

//           assertTrue("errors", index.matchesName("index", entry1));
//           assertFalse("errors", index.matchesName("index", entry2));
//           assertFalse("errors", index.matchesName("error", entry1));

//           continue;
//         }

//         if("General".equals(index.getGroup())
//            && "Files".equals(index.getTitle()))
//         {
//           assertEquals("files", 1,
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries()).size());
//           assertEquals("files", "name-file",
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries())
//                        .iterator().next());

//           assertFalse("files", index.matchesName("name-file", entry1));
//           assertTrue("files", index.matchesName("name-file", entry2));
//           assertFalse("files", index.matchesName("other-file", entry2));

//           continue;
//         }
//       }

//       BaseCampaign.GLOBAL.m_bases.clear();
//     }

    //......................................................................
    //----- variables ------------------------------------------------------

    /** Test reading of varialbles. */
//     @org.junit.Test
//     public void variables()
//     {
//     // we only have attachments for base items, thus we have to use one here
//       BaseItem entry = new BaseItem("entry name");

//       // direct variable
//       assertEquals("name", "var name",
//                    entry.getVariable("name").second().toString());
//       assertNull("unknown", entry.getVariable("damage"));

//       // variable from attachment
//       entry.addAttachment("weapon");

//       assertEquals("damage", "var damage",
//                    entry.getVariable("damage").second().toString());
//     }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    //@org.junit.Test
    @Override
    public void read()
    {
      ParseReader reader =
        new ParseReader(new java.io.StringReader("    abstract entry just a "
                                                 + "\\= test = ."),
                        "test");

      AbstractEntry entry = AbstractEntry.read(reader);

      assertNotNull("entry should have been read", entry);
      assertEquals("entry name does not match", "just a = test",
                   entry.getName());
      assertEquals("entry does not match",
                   "#----- just a \\= test\n"
                   + "\n"
                   + "base entry just a \\= test =\n"
                   + "\n"
                   + ".\n"
                   + "\n"
                   + "#.....\n",
                   entry.toString());
    }

    //......................................................................
    //----- readNameOnly ---------------------------------------------------

    /** Testing reading of name only. */
    //@org.junit.Test
    public void readNameOnly()
    {
      ParseReader reader =
        new ParseReader(new java.io.StringReader("abstract entry test."),
                        "test");

      AbstractEntry entry = AbstractEntry.read(reader);

      assertNotNull("entry should have been read", entry);
      assertEquals("entry name does not match", "test", entry.getName());
      assertEquals("entry does not match",
                   "#----- test\n"
                   + "\n"
                   + "abstract entry test =\n"
                   + "\n"
                   + ".\n"
                   + "\n"
                   + "#.....\n",
                   entry.toString());
    }

    //......................................................................
    //----- readNoName -----------------------------------------------------

    /** Testing reading without a name. */
    //@org.junit.Test
    public void readNoName()
    {
      ParseReader reader =
        new ParseReader(new java.io.StringReader("abstract entry."),
                        "test");

      AbstractEntry entry = AbstractEntry.read(reader);

      assertNotNull("entry should have been read", entry);
      assertEquals("entry name does not match", "", entry.getName());
      assertEquals("entry does not match",
                   "abstract entry $undefined$ =\n"
                   + "\n"
                   + ".\n"
                   + "\n"
                   + "#.....\n",
                   entry.toString());
    }

    //......................................................................
    //----- comment --------------------------------------------------------

    /** Testing reading with comments. */
    @org.junit.Test
    public void comment()
    {
      ParseReader reader =
        new ParseReader(new java.io.StringReader
                        ("  #--- test \n"
                         + "\n"
                         + "  # just some test\n"
                         + "\n"
                         + "abstract entry test = .\n"
                         + "# some other text\n"
                         + "# as end comment\n"
                         + "\n"
                         + "# now the next entry\n"),
                        "test");

      AbstractEntry entry = AbstractEntry.read(reader);

      assertNotNull("entry should have been read", entry);
      assertEquals("entry name does not match", "test",
                   entry.getName());
      assertEquals("entry does not match",
                   "  #--- test \n"
                   + "\n"
                   + "  # just some test\n"
                   + "\n"
                   + "base entry test =\n\n.\n"
                   + "# some other text\n",
                   entry.toString());
    }

    //......................................................................
    //----- printing -------------------------------------------------------

    /** Testing the output. */
//     @org.junit.Test
//     public void printing()
//     {
//       AbstractEntry entry = new AbstractEntry("name", TYPE);

//       // name
//       assertEquals("name (dm)",
//                 "\\bold{\\color{subtitle}{\\editable{name}{name}{name}{name}"
//                    + "{name}}}", entry.asCommand(true, "name"));
//       assertEquals("name (player)", "", entry.asCommand(false, "name"));

//       // title
//       assertEquals("title (dm)",
//                    "\\title[title][\\link[/index/abstractentries]"
//                    + "{abstract entry}]{\\editable{name}{name}{name}{name}"
//                    + "{name}}",
//                    entry.asCommand(true, "title"));
//       assertEquals("title (player)",
//                    "\\title[title][\\link[/index/abstractentries]"
//                    + "{abstract entry}]{\\editable{name}{name}{name}{name}"
//                    + "{name}}",
//                    entry.asCommand(false, "title"));

//       // errors
//       entry.addError(new BaseError("first error"));
//       entry.addError(new BaseError("second error"));
//       entry.addError(new BaseError("third error"));
//       assertEquals("errors (dm)",
//                    "Base Error: [third error] no definition found for this "
//                    + "error\\linebreak "
//                  + "Base Error: [second error] no definition found for this "
//                    + "error\\linebreak "
//                  + "Base Error: [first error] no definition found for this "
//                    + "error",
//                    entry.asCommand(true, "errors"));
//       assertEquals("errors (player)",
//                    "", entry.asCommand(false, "errors"));

//       // file
//       assertEquals("file (dm)",
//                    "\\editable{name}{}{_file}{<please select>}{selection}",
//                    entry.asCommand(true, "file"));
//       assertEquals("file (player)"
//                    "", entry.asCommand(false, "file"));

//       // script
//       String script = "\\script{gui.addAction('Report', function()";
//       assertEquals("scripts (dm)", script,
//                    entry.asCommand(true, "scripts").toString()
//                    .substring(0, script.length()));
//       assertEquals("scripts (player)", script,
//                    entry.asCommand(false, "scripts").toString()
//                    .substring(0, script.length()));
//     }

    //......................................................................
    //----- attachment -----------------------------------------------------

    /** Testing attachments. */
//     @org.junit.Test
//     public void testAttachment()
//     {
//       // normal
//       String text = "base item with light test = .";

//       ParseReader reader =
//         new ParseReader(new java.io.StringReader(text), "test");

//       AbstractEntry entry = AbstractEntry.read(reader);

//       assertNotNull("entry should have been read", entry);
//       assertEquals("entry name does not match", "test",
//                    entry.getName());
//       assertEquals("entry does not match",
//                    "base item with light test =\n\n"
//                    + "  probability       Common.\n",
//                    entry.toString());
//       assertTrue("has",
//                  entry.hasAttachment(net.ixitxachitls.dma.entries.attachments
//                                      .BaseLight.class));
//      assertNotNull("attachment",
//                  entry.getAttachment(net.ixitxachitls.dma.entries.attachments
//                                         .BaseLight.class));
//       assertFalse("has",
//                  entry.hasAttachment(net.ixitxachitls.dma.entries.attachments
//                                       .BaseWeapon.class));

//       // double attachments
//       text = "base item with light, light test = .";

//       reader = new ParseReader(new java.io.StringReader(text), "test");

//       entry = AbstractEntry.read(reader);

//       assertNotNull("entry should have been read", entry);
//       assertEquals("entry name does not match", "test",
//                    entry.getName());
//       assertEquals("entry does not match",
//                    "base item with light test =\n\n"
//                    + "  probability       Common.\n",
//                    entry.toString());

//       // tags
//       text = "base item with light, test:light test = .";

//       reader = new ParseReader(new java.io.StringReader(text), "test");

//       entry = AbstractEntry.read(reader);

//       assertNotNull("entry should have been read", entry);
//       assertEquals("entry name does not match", "test",
//                    entry.getName());
//       assertEquals("entry does not match",
//                    "base item with light, test:light test =\n\n"
//                    + "  probability        Common.\n",
//                    entry.toString());

//       m_logger.verify();

//       // unknown class
//       text = "abstract entry with guru test = .";

//       reader = new ParseReader(new java.io.StringReader(text), "test");

//       entry = AbstractEntry.read(reader);

//       assertNotNull("entry should have been read", entry);
//       assertEquals("entry name does not match", "test",
//                    entry.getName());
//       assertEquals("entry does not match", "abstract entry test =\n\n.\n",
//                    entry.toString());

//       m_logger.addExpected("WARNING: could not find class for attachment "
//                            + "guru, attachment ignored");
//       m_logger.verify();

//       // value
//       text = "base item with light test = bright light 2m sphere; "
//         + "shadowy light 4m cone.";

//       reader = new ParseReader(new java.io.StringReader(text), "test");

//       entry = AbstractEntry.read(reader);

//       assertNotNull("entry should have been read", entry);
//       assertEquals("entry name does not match", "test",
//                    entry.getName());
//       assertEquals("entry does not match",
//                    "base item with light test =\n\n"
//                    + "  bright light      2 m Sphere;\n"
//                    + "  shadowy light     4 m Cone;\n"
//                    + "  probability       Common.\n",
//                    entry.toString());

//       m_logger.verify();
//       // value

//       text = "base item with light, test:light test = "
//         + "bright light 2m sphere; shadowy light 3m cone; "
//         + "test:bright light 4 m cone; test:shadowy light 8m sphere.";

//       //Log.add("test", new net.ixitxachitls.util.logging.ANSILogger());

//       reader = new ParseReader(new java.io.StringReader(text), "test");

//       entry = AbstractEntry.read(reader);

//       assertNotNull("entry should have been read", entry);
//       assertEquals("entry name does not match", "test",
//                    entry.getName());
//       assertEquals("entry does not match",
//                    "base item with light, test:light test =\n\n"
//                    + "  bright light       2 m Sphere;\n"
//                    + "  shadowy light      3 m Cone;\n"
//                    + "  test:bright light  4 m Cone;\n"
//                    + "  test:shadowy light 8 m Sphere;\n"
//                    + "  probability        Common.\n",
//                    entry.toString());

//       m_logger.verify();
//     }

    //......................................................................
  }

  //........................................................................
}
