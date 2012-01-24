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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
// import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
// import java.util.StringTokenizer;
import java.util.TreeMap;
// import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

// import net.ixitxachitls.dma.data.CampaignData;
import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.data.DMAFile;
import net.ixitxachitls.dma.entries.extensions.AbstractExtension;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
// import net.ixitxachitls.dma.data.Storage;
// import net.ixitxachitls.dma.entries.indexes.ExtractorIndex;
// import net.ixitxachitls.dma.entries.indexes.Index;
// import net.ixitxachitls.dma.values.BaseNumber;
import net.ixitxachitls.dma.values.BaseText;
import net.ixitxachitls.dma.values.Comment;
// import net.ixitxachitls.dma.values.Modifiable;
import net.ixitxachitls.dma.values.Name;
// import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.formatters.LinkFormatter;
import net.ixitxachitls.input.ParseReader;
// import net.ixitxachitls.output.Document;
// import net.ixitxachitls.output.commands.Bold;
// import net.ixitxachitls.output.commands.Center;
// import net.ixitxachitls.output.commands.Color;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Divider;
import net.ixitxachitls.output.commands.Image;
import net.ixitxachitls.output.commands.ImageLink;
import net.ixitxachitls.output.commands.Linebreak;
import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.output.commands.Par;
import net.ixitxachitls.output.commands.Script;
// import net.ixitxachitls.output.commands.Table;
import net.ixitxachitls.output.commands.Title;
import net.ixitxachitls.output.commands.Window;
import net.ixitxachitls.util.Classes;
import net.ixitxachitls.util.EmptyIterator;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.Files;
// import net.ixitxachitls.util.Extractor;
// import net.ixitxachitls.util.Identificator;
import net.ixitxachitls.util.Pair;
// import net.ixitxachitls.util.Classes;
import net.ixitxachitls.util.Strings;
// import net.ixitxachitls.util.UniqueIdentificator;
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

  //----- Combiner ---------------------------------------------------------

  /**
   * A simple combiner to combine an old value with a new one and return
   * the desired result.
   */
//   protected interface Combiner<T, S>
//   {
//     public T combine(T inOld, S inNew);
//   }

  //........................................................................
  //----- UniqueEntryIdentificator -----------------------------------------

//   /**
//    * A class for uniquely identifying an entry in a repository.
//    */
//   public static class UniqueEntryIdentificator<T extends AbstractEntry>
//     implements UniqueIdentificator<T>
//   {
//     public String id(@Nonnull T inEntry)
//     {
//       return inEntry.getID();
//     }
//   }

  //........................................................................
  //----- EntryIdentificator -----------------------------------------------

  /**
   * A class for identifying an entry.
   */
//   public static class EntryIdentificator<T extends BaseEntry>
//     implements Identificator<T>
//   {
//     public @Nonnull List<String> id(@Nonnull T inEntry)
//     {
//       String []synonyms = inEntry.getSynonyms();

//       String []result = new String[synonyms.length + 1];

//       result[0] = inEntry.getID();

//       int i = 1;
//       for(String synonym : synonyms)
//         result[i++] = synonym;

//       return result;
//     }
//   }

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
  protected AbstractEntry(@Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    m_type = inType;
    m_data = DMADataFactory.getBaseData();

    // we have to init this here, as we need to have the type set
    m_base = new ValueList<Name>
      (new Name().withFormatter(new LinkFormatter<Name>
                                ("/" + getType().getBaseType().getLink()
                                 + "/")));
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
  protected AbstractEntry(@Nonnull String inName,
                          @Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    this(inType);

    setName(inName);
    addBase(inName);
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
   * @param       inName the name of the entry
   * @param       inType the type of the entry
   *
   * @undefined   never
   *
   */
//   protected AbstractEntry(String inName, Type inType, BaseEntry ... inBases)
//   {
//     m_name.set(inName);

//     m_type = inType;

//     if(inBases != null)
//       for(BaseEntry base : inBases)
//         addBase(base, null);
//   }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  //----- general values ---------------------------------------------------

   /** The entry type. */
  protected @Nonnull AbstractType<? extends AbstractEntry> m_type;

  /** All the data available. */
  protected @Nonnull DMAData m_data;

  /** Flag if this entry has been changed but not saved. */
  protected boolean m_changed = false;

  /** Errors for this entry. */
  protected @Nullable List<BaseError> m_errors = null;

  /** All the extensions, indexed by name. */
  protected Map<String, AbstractExtension> m_extensions =
    new TreeMap<String, AbstractExtension>();

  /** The base entries for this entry, in the same order as the names. */
  protected @Nullable List<BaseEntry> m_baseEntries = null;

  /** The files for this entry. */
  private @Nullable List<DMAData.File> m_files = null;

  /** The print for printing a whole page entry. */
  public static final Print s_pagePrint = new Print("$title");

  /** The print for printing an entry in a list. */
  public static final ListPrint s_listPrint =
    new ListPrint("1:L(icon);20:L(name)[Name]", "$label", null);

  /** The random generator. */
  protected static final @Nonnull Random s_random = new Random();

  /** The dashes to create comments. */
  protected static final @Nonnull String s_hyphens =
    "------------------------------------------------------------------------"
    + "-----";

  /** The dots to create comments. */
  protected static final @Nonnull String s_dots =
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
  protected static final @Nonnull String s_package =
    "net.ixitxachitls.dma.entries.";

  /** The maximal number of keywords to read for an entry. */
  protected static final int s_keywordWords =
    Config.get("resource:entries/key.words", 2);

  /** The starter for the base name part. */
  protected static final @Nonnull String s_baseStart =
    Config.get("resource:entries/base.start", "[");

  /** The ending for the base name part. */
  protected static final @Nonnull String s_baseEnd =
    Config.get("resource:entries/base.end", "]");

  /** The pattern to replace values in expressions. */
  protected static final @Nonnull Pattern s_varPattern =
    Pattern.compile("\\$(\\w+)");

  /** The pattern for expressions. */
  protected static final @Nonnull Pattern s_expPattern =
    Pattern.compile("\\[\\[(.*?)\\]\\]");

  //........................................................................

  //----- name -------------------------------------------------------------

  /** The name of the abstract entry. */
  @Key("name")
  @NoStore
  @Note("Changing the name will not change any references to entries with "
        + "that name, thus leaving these references dangling. You will have "
        + "to update these manually.")
  protected BaseText<? extends BaseText> m_name = new Name();

  //........................................................................
  //----- comments ---------------------------------------------------------

  /** The leading comment(s) in front of the entry. */
  protected @Nonnull Comment m_leadingComment =
    new Comment(s_maxLeadingComments, s_maxLeadingLines);

  /** The trailing comment(s) after the entry. */
  protected @Nonnull Comment m_trailingComment =
    new Comment(s_maxTrailingComments, s_maxTrailingLines);

  //........................................................................
  //----- base names -------------------------------------------------------

  // Cannot use a static formatter here, as it depends on the
  // real type; thus we also need to init this in the constructor after we have
  // the type.
  /** The base names. */
  @Key("base")
  protected @Nonnull ValueList<Name> m_base;

  //........................................................................

  //----- storage ----------------------------------------------------------

  /** The file this entry will be written to. */
  @Deprecated
  protected @Nullable DMAFile m_file;

//   /** The place this entry is stored in (not read). */
//   protected Storage<AbstractEntry> m_storage = null;

  /** The starting position in the file (characters). */
  protected long m_startPos = 0;

  /** The starting position in the file (lines). */
  protected long m_startLine = 0;

  /** The ending position in the file (characters). */
  protected long m_endPos = 0;

  /** The ending position in the file (lines). */
  protected long m_endLine = 0;

//   /** The possible ways of extracting a base value. */
//   public enum Combine { FIRST, ADD, MODIFY, MINIMUM, MAXIMUM, };

  //........................................................................

  //----- combiners --------------------------------------------------------

//   /** The combiner to keep the first defined value. */
//   protected static final Combiner<String, String> COMBINER_KEEP_FIRST =
//     new Combiner<String, String>()
//     {
//       public String combine(String inOld, String inNew)
//       {
//         // only keep the old value
//         return inOld;
//       }
//     };

//   protected static final Combiner<List<String>, String> COMBINER_LIST =
//     new Combiner<List<String>, String>()
//     {
//       public List<String> combine(List<String> inOld, String inNew)
//       {
//         inOld.add(inNew);

//         return inOld;
//       }
//     };

  //........................................................................

  static
  {
    extractVariables(AbstractEntry.class);
  }

  //----- indices ----------------------------------------------------------

  static
  {
//     // all the attachments
//     s_indexes.add
//       (new ExtractorIndex<ExtractorIndex>
//        ("General", "Attachments", "attachments",
//         new ExtractorIndex.Extractor()
//         {
//           public Object []get(AbstractEntry inEntry)
//           {
//             ArrayList<Object> values = new ArrayList<Object>();

//             if(inEntry != null)
//               for(Iterator<AbstractAttachment> i = inEntry.getAttachments();
//                   i.hasNext(); )
//                 values.add(i.next().getName());

//             return values.toArray();
//           }
//         }, true, false));

//     // the main index with all the types
//     s_indexes.add(new ExtractorIndex<ExtractorIndex>
//                   ("Index", "Index", "index",
//                    new ExtractorIndex.Extractor()
//                    {
//                      public Object []get(AbstractEntry inEntry)
//                      {
//                        if(inEntry == null)
//                          return new Object [0];

//                        return new Object []
//                        { inEntry.getType().getMultiple().toLowerCase(), };
//                     }
//                    }, true, true));

//     // the index with all the errors
//     s_indexes.add(new ExtractorIndex<ExtractorIndex>
//                   ("General", "Errors", "errors",
//                    new ExtractorIndex.Extractor()
//                    {
//                      public Object []get(AbstractEntry inEntry)
//                      {
//                        if(inEntry == null || inEntry.m_errors == null
//                           || inEntry.m_errors.size() == 0)
//                          return null;

//                        return new Object [] { "index" };
//                     }
//                    }, true, false).withDataSource(Index.DataSource.dm));

//     // the index with all the files
//     s_indexes.add(new ExtractorIndex<ExtractorIndex>
//                   ("General", "Files", "files",
//                    new ExtractorIndex.Extractor()
//                    {
//                      public Object []get(AbstractEntry inEntry)
//                      {
//                        if(inEntry == null || inEntry.m_storage == null)
//                          return new Object [0];

//                        return new Object []
//                        { inEntry.m_storage.getStorageName() };
//                     }
//                    }, false, false));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

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
  //----------------------------- getVariable -----------------------------

  /**
   * Get the variable for the given key along with the group it is found in.
   * This method also looks in attachments.
   *
   * @param       inKey the name of the key to get the value for
   *
   * @return      the variable and the group it was found in (can return null
   *              if not found)
   *
   */
  public @Nullable Variable getVariable(@Nonnull String inKey)
  {
    Variable result = super.getVariable(inKey);

    if(result != null)
      return result;

    // check the attachments for a value
//     for(Iterator<AbstractAttachment> i = getAttachments(); i.hasNext(); )
//     {
//       AbstractAttachment att = i.next();
//       Pair<ValueGroup, Variable> var = att.getVariable(inKey);

//       if(var != null)
//         return var;
//     }

    return null;
  }

  //........................................................................
  //------------------------------- getName --------------------------------

  /**
   * Get the name of the entry.
   *
   * @return      the requested name
   *
   */
  public @Nonnull String getName()
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
  public @Nonnull String getQualifiedName()
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
   * @return      the requested base entries
   *
   */
  @Deprecated // this might be problematic with app engine
  public List<BaseEntry> getBaseEntries()
  {
    if(m_baseEntries == null)
    {
      m_baseEntries = new ArrayList<BaseEntry>();

      // TODO: make this in a single datastore request
      for(Name base : m_base)
        m_baseEntries.add((BaseEntry)
                          m_data.getEntry(base.get(), getType().getBaseType()));
    }

    return m_baseEntries;
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
  public @Nonnull String getRefName()
  {
    return getName();
  }

  //........................................................................
  //--------------------------------- getID --------------------------------

  /**
   * Get the ID of the entry. This can mainly be used for reference purposes.
   * In this case, the name is equal to the id, which is not true for entries.
   *
   * @return      the requested id
   *
   */
  @Deprecated
  public @Nonnull String getID()
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
  public @Nonnull AbstractType<? extends AbstractEntry> getType()
  {
    return m_type;
  }

  //........................................................................
  //----------------------------- getEditType ------------------------------

  /**
   * Get the type of the entry.
   *
   * @return      the requested name
   *
   */
  public @Nonnull String getEditType()
  {
    return m_type.toString();
  }

  //........................................................................
  //--------------------------- getAttachments -----------------------------

  /**
   * Get an iterator over all the attachments.
   *
   * @return      the requested iterator
   *
   * @undefined   never
   *
   */
//   public Iterator<AbstractAttachment> getAttachments()
//   {
//     return m_attachments.values().iterator();
//   }

  //........................................................................
  //---------------------------- hasAttachment -----------------------------

  /**
   * Check if the entry has an attachment with the given class.
   *
   * @param       inAttachment the class of the attachment to look for
   *
   * @return      true if an attachment of this name is present, false if not
   *
   * @undefined   IllegalArgumentException if no attachment given
   *
   */
//public boolean hasAttachment(Class<? extends AbstractAttachment> inAttachment)
//   {
//     if(inAttachment == null)
//       throw new IllegalArgumentException("must have an attachment here");

//     for(Iterator<AbstractAttachment> i = getAttachments(); i.hasNext(); )
//       if(inAttachment.isAssignableFrom(i.next().getClass()))
//         return true;

//     return false;
//   }

  //........................................................................
  //---------------------------- getAttachment -----------------------------

  /**
   * Get the first attachment with the given class.
   *
   * @param       inAttachment the class of the attachment to look for
   * @param       <T> the type of attachment to get
   *
   * @return      the attachment found or null if not found
   *
   * @undefined   IllegalArgumentException if no attachment given
   *
   */
//   @SuppressWarnings("unchecked")
// public <T extends AbstractAttachment> T getAttachment(Class<T> inAttachment)
//   {
//     if(inAttachment == null)
//       throw new IllegalArgumentException("must have an attachment here");

//     for(Iterator<AbstractAttachment> i = getAttachments(); i.hasNext(); )
//     {
//       AbstractAttachment attachment = i.next();

//       if(inAttachment.isAssignableFrom(attachment.getClass()))
//         return (T)attachment;
//     }

//     return null;
//   }

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
  public @Nonnull Iterator<BaseError> getErrors()
  {
    if(m_errors == null)
      return new EmptyIterator<BaseError>();

    return m_errors.iterator();
  }

  //........................................................................
  //----------------------------- getCampaign ------------------------------

  /**
   * Get the campaign this storage is in.
   *
   * @return      the Campaign for this storage
   *
   */
//   public CampaignData getCampaign()
//   {
//     return m_storage.getCampaign();
//   }

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
//       System.out.println(inName + ": " + value);

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
  public @Nonnull List<DMAData.File> getFiles()
  {
    if(m_files == null)
      m_files = m_data.getFiles(this);

    return m_files;
  }

  //........................................................................

  //------------------------- computeIndexValues ---------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @return      a multi map of values per index name
   *
   */
  public Multimap<String, String> computeIndexValues()
  {
    return HashMultimap.create();
  }

  //........................................................................

  //-------------------------- combineBaseValues ---------------------------

  /**
   * Combine specific values of all base entries into a single command.
   *
   * @param      inName the name of the value to obtain
   *
   * @return     the command for printing the value
   *
   */
  public @Nonnull Command combineBaseValues(@Nonnull String inName)
  {
    List<Pair<BaseEntry, Value>> baseValues = getBaseValues(inName);

    if(baseValues.isEmpty())
      return new Command("");

    List<Value> values = new ArrayList<Value>();
    List<List<BaseEntry>> entries = new ArrayList<List<BaseEntry>>();
    for(Pair<BaseEntry, Value> value : baseValues)
      addBaseValue(values, entries, value.second(), value.first());

    // Compute the command for printing.
    List<Command> commands = new ArrayList<Command>();
    Iterator<Value> i = values.iterator();
    Iterator<List<BaseEntry>> j = entries.iterator();
    while(i.hasNext() && j.hasNext())
    {
      Value value = i.next();
      List<BaseEntry> entryList = j.next();
      List<String> names = new ArrayList<String>();
      for(BaseEntry entry : entryList)
        names.add(entry.getName());

      commands.add(new Window(value.format(), "from "
                              + Strings.toString(names, ", ", "none")));
    }

    return new Command(commands);
  }

  //........................................................................
  //----------------------------- addBaseValue -----------------------------

  /**
   * Add the given value to the list of entries currently process for base
   * entry computation. This will add only unique values to the lists and
   * groups entries together for the same values. Additionally, a ValueList
   * will be added by it's element.
   *
   * @param       ioValues  all the values encountered
   * @param       ioEntries all the entries encountered per value
   * @param       inValue   the value to add
   * @param       inEntry   the entry containing the value
   *
   */
  private void addBaseValue(@Nonnull List<Value> ioValues,
                            @Nonnull List<List<BaseEntry>> ioEntries,
                            @Nonnull Value inValue, @Nonnull BaseEntry inEntry)
  {
    if(inValue instanceof ValueList)
      for(Value value : (ValueList<? extends Value>)inValue)
        addBaseValue(ioValues, ioEntries, value, inEntry);
    else
    {
      int pos = ioValues.indexOf(inValue);
      if(pos < 0)
      {
        ioValues.add(inValue);
        List<BaseEntry> entries = new ArrayList<BaseEntry>();
        entries.add(inEntry);
        ioEntries.add(entries);
      }
      else
        ioEntries.get(pos).add(inEntry);
    }
  }

  //........................................................................
  //---------------------------- getBaseValues -----------------------------

  /**
   * Get a list of all base values for a given name.
   *
   * @param       inName the name of the value to get
   *
   * @return      a list with all the values and where they were found
   *
   */
  public @Nonnull List<Pair<BaseEntry, Value>> getBaseValues
    (@Nonnull String inName)
  {
    List<Pair<BaseEntry, Value>> values =
      new ArrayList<Pair<BaseEntry, Value>>();

    if(m_baseEntries != null)
      for(BaseEntry base : m_baseEntries)
      {
        values.addAll(base.getBaseValues(inName));
        Value value = base.getValue(inName);
        if(value != null && value.isDefined())
          values.add(new Pair<BaseEntry, Value>(base, value));
      }

    return values;
  }

  //........................................................................
  //----------------------- getFirstDefinedBaseValue -----------------------

  /**
   * Get the first defined value from any of the bases.
   *
   * @param       inName the name of the value to get
   *
   * @return      the requested value or null if not found or undefined
   *
   */
  public @Nullable Value getFirstDefinedBaseValue(@Nonnull String inName)
  {
    if(m_baseEntries == null)
      return null;

    List<Pair<BaseEntry, Value>> values = getBaseValues(inName);
    for(Pair<BaseEntry, Value> value : values)
      if(value.second().isDefined())
        return value.second();

    return null;
  }

  //........................................................................
  //----------------------------- getBaseValues ----------------------------

  /**
   * Get a value from any of the bases.
   *
   * @param       inExtractor the extractor to get the values
   * @param       inOrdinal   the extractor to get a value for sorting
   *
   * @return      the requested value or null if not found or undefined
   *
   */
//   @SuppressWarnings("unchecked") // having to cast base entry to given T
//   protected <T extends BaseEntry, S, V>
//   S getBaseValues(Extractor<T, V> inExtractor, Combiner<S, V> inCombiner,
//                   S ioResult)
//   {
//     assert inCombiner != null : "must have a cominer here";
//     assert ioResult   != null : "must have a value for result";

//     if(m_baseEntries != null)
//       for(BaseEntry base : m_baseEntries)
//       {
//         if(base == null)
//           continue;

//         V temp = inExtractor.get((T)base);

//         if(temp != null)
//           inCombiner.combine(ioResult, temp);
//       }

//     return ioResult;
//   }

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
  public int compareTo(@Nonnull AbstractEntry inOther)
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
  public boolean isBasedOn(@Nonnull BaseEntry inBase)
  {
    if(m_baseEntries == null)
      return false;

    for(BaseEntry base : m_baseEntries)
      if(base == inBase || (base != this && inBase.isBasedOn(base)))
        return true;

    return false;
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert the object to a String for printing.
   *
   * @return      the String representation
   *
   */
  public @Nonnull String toString()
  {
    StringBuilder result = new StringBuilder();

    if(m_leadingComment.isDefined())
      result.append(m_leadingComment);

    result.append(m_type);
    result.append(' ');

//     if(m_attachments.size() > 0)
//     {
//       result.append("with ");
//       result.append(Strings.toString(m_attachments.keySet(), ", ",
//                                      "incomplete"));
//       result.append(" ");
//     }

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
  protected @Nonnull String formatValues()
  {
    StringBuilder result = new StringBuilder();

    boolean first = true;

    int width = getKeyWidth();
//     for(AbstractAttachment attachment : m_attachments.values())
//       first = attachment.formatValues(result, first, width);

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
  public Map<String, Value> getAllValues()
  {
    Map<String, Value> values = new HashMap<String, Value>();

    Variables vars = getVariables();

    for(Variable var : vars)
    {
      if(!var.isStored())
        continue;

      Value value = var.get(this);

      // We don't store this if we don't have a value.
      if(value == null || !value.isDefined())
        continue;

      values.put(var.getKey(), value);
    }

    return values;
  }

  //........................................................................

  //------------------------------ asCommand -------------------------------

  /**
   * Get the commands used for printing this entry.
   *
   * @param       inDM      true if to get values for the DM, false else
   * @param       inValues  comma seperated list of names to print for values
   *
   * @return      a command to print the entry as requested
   *
   */
//   public Command asCommand(boolean inDM, String inValues)
//   {
//     return new Command(printCommand(inDM, true).asCommands(inDM, inValues)
//                        .toArray());
//   }

  //........................................................................

  //----------------------------- getPagePrint -----------------------------

  /**
   * Get the print for a full page.
   *
   * @return the print for page printing
   *
   */
  protected @Nonnull Print getPagePrint()
  {
    return s_pagePrint;
  }

  //........................................................................
  //----------------------------- getListPrint -----------------------------

  /**
   * Get the print for a list entry.
   *
   * @return the print for list entry
   *
   */
  protected @Nonnull ListPrint getListPrint()
  {
    return s_listPrint;
  }

  //........................................................................
  //----------------------------- getListFormat ----------------------------

  /**
   * Get the print for a list entry.
   *
   * @return the print for list entry
   *
   */
  public @Nonnull String getListFormat()
  {
    return getListPrint().getFormat();
  }

  //........................................................................
  //------------------------------- printPage ------------------------------

  /**
   * Print the entry into a command for adding to a document.
   *
   * @param       inUser  the user printing, if any
   *
   * @return      the command representing this item in a list
   *
   */
  public @Nonnull Object printPage(@Nullable BaseCharacter inUser)
  {
    return getPagePrint().print(this, inUser);
  }

  //........................................................................
  //------------------------------- printList ------------------------------

  /**
   * Print the entry into a command for adding to a document.
   *
   * @param       inKey   the key (name) for the entry to be printed (this is
   *                      used when printing entries multiple times with synonym
   *                      names)
   * @param       inUser  the user printing, if any
   *
   * @return      the command representing this item in a list
   *
   */
  public @Nonnull List<Object> printList(@Nonnull String inKey,
                                         @Nullable BaseCharacter inUser)
  {
    return getListPrint().print(inKey, this, inUser);
  }

  //........................................................................

  //--------------------------------- print --------------------------------

  /**
   * Get the print object for printing this entry.
   *
   * @param       inDM   true if set for DM, false for player
   *
   * @return      the command representing this item in a list
   *
   */
//   public Command print(boolean inDM)
//   {
//     Print print = printValues(inDM, true);

//     ArrayList<Object> result = new ArrayList<Object>();

//     result.add(new Divider("center", new Command(commands.icons.toArray())));
//     result.addAll(commands.header);
//     result.addAll(commands.pre);
//     result.addAll(commands.images);

//     result.add(new Table("description", "f" + "Illustrations: ".length()
//                          + ":L(desc-label);100:L(desc-text)",
//                          commands.values.toArray()));
//     result.add(new Divider("clear", ""));
//     result.addAll(commands.post);

//     if(commands.temp != null) {
//       result.clear();
//       result.addAll(commands.temp);
//     }

//     return new Divider(commands.type, new Command(result.toArray()));
//     return null;
//   }

  //........................................................................
  //------------------------- getShortPrintCommand -------------------------

  /**
   * Print the item to the document in a short way.
   *
   * @param       inDM   true if set for DM, false for player
   *
   * @return      the command representing this item in a list
   *
   */
//   public Command getShortPrintCommand(boolean inDM)
//   {
//     PrintCommand commands = shortPrintCommand(inDM);

//     ArrayList<Object> result = new ArrayList<Object>();

//     commands.values.addAll(commands.iconValues);

//     result.addAll(commands.shortHeader);
//     result.addAll(commands.pre);
//     result.add(new Table("description", "f" + "Illustrations: ".length()
//                          + ":L(desc-label);100:L(desc-text)",
//                          commands.values.toArray()));
//     result.add(new Divider("clear", ""));

//     if(commands.temp != null)
//       result.addAll(commands.temp);

//     return new Divider(commands.type, new Command(result.toArray()));
//   }

  //........................................................................
  //------------------------------ printValues -----------------------------

  /**
   * Collect the values for printing.
   *
   * @param       inDM       true if setting for dm, false if not
   * @param       inEditable true if values are editable, false if not
   *
   * @return      the print object representing the values to print
   *
   */
//   private @Nonnull Print printValues(boolean inDM, boolean inEditable)
//   {
//     Print values = new Print();

//     commands.type = "abstract entry";

    // images
//     values.add("image", getType().getMultipleDir() + "/" + getID(), false,
//                false, false, "images");

//     String baseDir;
//     if(isBase())
//       baseDir = getType().getMultipleDir();
//     else
//       baseDir = ((Entry)this).getBaseType().getMultipleDir();

//     for(String name : getBaseNames())
//       commands.addValue("image", baseDir + "/" + name,
//                         false, false, false, "images");

//     Object name = createValueCommand(m_name, "name", inEditable);

//     if(getType().getBaseType() != null && inDM || getType() == Product.TYPE)
//       name = new Link(name,
//                       "/entry/" + getType().getLink()
//                       + "/" + getID());

// //     // attachments
// //     ValueList<SimpleText> attachments =
// //       new ValueList<SimpleText>(new SimpleText());
// //     for(String attachment : m_attachments.keySet())
// //       attachments.add(new SimpleText(attachment));

// //     commands.addValue("attachment", attachments, true, true, false,
// //                       "attachments");


//     commands.addValue("scripts", new Script
//                       ("gui.addAction('Report', "
//                        + "function() { "
//                        + "document.location=\"mailto:dma@ixitxachitls.net?"
//                        + "subject=Error Report for entry [" + getName()
//                        + "]\" }, 'Send an error report about this page.');\n"
//                        + "gui.addAction('Edit', gui.editAll, "
//                        + "'Edit all the values on the page. "
//                        + "Double-click or right-click to edit a single "
//                        + "value.');\n"
//                        + "gui.addAction('Save', "
//                        + "function() { save('base', '" + m_type.toString()
//                        + "'); }, "
//                        + "'Save all the changes made.', 'save', 'hidden');\n"
//                        + "gui.addAction('Reload', "
//                        + "function() { "
//                        + "reloadEntry('" + getID() + "', '" + m_type + "');"
//                        + "}, 'Reload the current entry only, base entries "
//                        + "will not be reloaded!');\n"), false, false, false,
//                       "scripts");

//     List<Object> subcommands = new ArrayList<Object>();
//     if(m_baseEntries != null)
//     {
//       Iterator<String> names = m_baseNames.iterator();
//       Iterator<BaseEntry> entries = m_baseEntries.iterator();

//       while(true)
//       {
//         BaseEntry entry     = entries.next();
//         String    entryName = names.next();

//         if(entryName == null && entry == null)
//           continue;

//         if(entry == null)
//           subcommands.add(new Link(entryName,
//                                    "/entry/" + getType().getLink()
//                                    + "/" + entryName));
//         else
//           subcommands.add(new Link(entryName,
//                                    "/entry/" + entry.getType().getLink()
//                                    + "/" + entry.getID()));

//         if(names.hasNext() && entries.hasNext())
//           subcommands.add(", ");
//         else
//           break;
//       }
//     }

//   commands.addValue("base", new Command(subcommands.toArray()), false, true,
//                       false, "bases");

//     // file
//     if(m_storage != null)

//     return values;
//   }

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
  public @Nullable ValueHandle computeValue(@Nonnull String inKey, boolean inDM)
  {
    if("title".equals(inKey))
    {
      return new FormattedValue
        (new Title(computeValue("name", inDM).format(this, inDM, true),
                   "entrytitle"),
         null, "title", false, false, false, false, "titles", "");
    }

    if("desc".equals(inKey))
    {
      List<Object> commands = new ArrayList<Object>();
      ValueHandle subtitle = computeValue("subtitle", inDM);
      if(subtitle != null)
        commands.add(subtitle.format(this, inDM, true));

      commands.add(computeValue("description", inDM).format(this, inDM, true));
      commands.add(computeValue("short description", inDM)
                   .format(this, inDM, true));

      return new FormattedValue(new Divider("desc",
                                            new Command(commands.toArray())),
                                null, "desc", false, false, false, false,
                                "desc", "");
    }

    if("image".equals(inKey))
    {
      AbstractType<? extends AbstractEntry> type = getType();

      for(DMAData.File file : getFiles())
        if("main".equals(file.getName()))
          return new FormattedValue
            (new ImageLink(file.getIcon() + "=s300", "main",
                           file.getPath(), "main")
             .withID("file-main"),
             "main", "image", false, true, false, false, "images", "")
            .withEditType("image");

      return new FormattedValue
        (new ImageLink("/icons/" + type.getMultipleLink()
                       + "-dummy.png", "main",
                       "/icons/" + type.getMultipleLink()
                       + "-dummy.png", "main")
         .withID("file-main"),
         "main", "image", false, true, false, false, "images", "")
        .withEditType("image");
    }

    if("clear".equals(inKey))
      // we need a non empty string here, because when parsing trailing empty
      // arguments are ignored.
      return new FormattedValue(new Divider("clear", " "), null,
                                "clear", false, false, false, false, "clear",
                                "");

    if("files".equals(inKey))
    {
      List<Command> commands = new ArrayList<Command>();
      boolean first = true;
      for(DMAData.File file : getFiles())
      {
        if(first && "main".equals(file.getName()))
        {
          first = false;
          continue;
        }

        String uri;
        if(file.getType().startsWith("image/"))
          uri = file.getIcon() + "=s50";
        else if("application/pdf".equals(file.getType()))
          uri = "/icons/pdf.png";
        else
        {
          Log.warning("unknown file '" + file + "' ignored");
          continue;
        }

        commands.add(new Divider("file", new ImageLink(uri, file.getName(),
                                                       file.getPath(),
                                                       "file")));
      }

      return new FormattedValue
        (new Divider("files", "files", new Command(commands)),
         "files", "files", false, true, false, false,
         "files", "").withEditType("files");
    }

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

      return new FormattedValue(value, null, "errors", true, false, false,
                                false, "errors", "");
    }

    if("as dma".equals(inKey))
      return new FormattedValue(new ImageLink("/icons/doc-dma.png",
                                              getName(), getName() + ".dma",
                                              "doc-link"),
                                null, "as dma", true, false, false, false,
                                "as dma", "");

    if("as pdf".equals(inKey))
      return new FormattedValue(new ImageLink("/icons/doc-pdf.png",
                                              getName(), getName() + ".pdf",
                                              "doc-link"),
                                null, "as pdf", true, false, false, false,
                                "as pdf", "");

    if("as text".equals(inKey))
      return new FormattedValue(new ImageLink("/icons/doc-txt.png",
                                              getName(), getName() + ".txt",
                                              "doc-link"),
                                null, "as text", true, false, false, false,
                                "as text", "");

    if("label".equals(inKey))
      return new FormattedValue
        (new Image(Files.concatenate
                   ("/icons/labels", getType().getClassName()) + ".png",
                   "label"),
         null, "label", false, false, false, false, "labels", "");

    if("par".equals(inKey))
      return new FormattedValue(new Par(), null, "par", false, false, false,
                                false, "par", "");

    if("listlink".equals(inKey))
    {
      // we have to add a wrapping div to host the id there; when we assing the
      // body to the innerHTML of the page, script tags will be ignored and
      // thus the id would be missing
      String id = "linkrow-user-" + Encodings.toCSSString(getName());
      return new FormattedValue
        (new Divider(id, "", new Script
                     ("util.linkRow(document.getElementById('" + id + "'), "
                      + "'" + getPath() + "');")),
         null, "listlink", false, false, false, false, "listlinks", "");
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
                                Strings.toString(values, ", ", ""), "base",
                                true, true, false, false, null, null)
      .withEditType("list(, )#name");
    }

    return super.computeValue(inKey, inDM);
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
  public @Nonnull String getPath()
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
  public @Nonnull String [] getNavigation()
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
  public @Nonnull String [] getListNavigation()
  {
    return new String [] {
      getType().getMultipleLink(), "/" + getType().getMultipleLink(),
    };
  }

  //........................................................................
  //------------------------- getShortDescription --------------------------

  /**
   * Get the short description of the entry.
   *
   * @return      the short description
   *
   */
  public @Nonnull String getShortDescription()
  {
    return "";
  }

  //........................................................................
  //--------------------------------- isDM ---------------------------------

  /**
   * Check whether the given user is the DM for this entry.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   *
   */
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    return false;
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
  public boolean canEdit(@Nonnull String inKey, @Nonnull BaseCharacter inUser)
  {
    return inUser != null && inUser.hasAccess(BaseCharacter.Group.ADMIN);
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
  public @Nullable String set(@Nonnull String inKey, @Nonnull String inText)
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
          addBase(base);

      changed();
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
  public void setName(@Nonnull String inName)
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
  public void setLeadingComment(@Nonnull Comment inComment)
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
  public void setTrailingComment(@Nonnull Comment inComment)
  {
    m_trailingComment = inComment;
  }

  //........................................................................
  //------------------------------- setOwner -------------------------------

  /**
   * Set the owner of the entry.
   *
   * @param       inOwner the owning entry
   *
   */
  public void setOwner(@Nonnull AbstractEntry inOwner)
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
  @SuppressWarnings("unchecked") // calling complete on base type
  public static @Nullable AbstractEntry read(@Nonnull ParseReader inReader)
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
  protected boolean readEntry(@Nonnull ParseReader inReader)
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

    BaseText<? extends BaseText> name = m_name.read(inReader);
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
  protected void readValues(@Nonnull ParseReader inReader)
  {
    Variables variables = getVariables();
    while(!inReader.isAtEnd() && !inReader.expect(s_delimiter))
    {
      String key = null;

      //----- normal -------------------------------------------------------

      key = inReader.expect(variables.getKeywords());

      if(key != null)
        readVariable(inReader, variables.getVariable(key));

      //....................................................................
      //----- extensionss --------------------------------------------------

//       for(Iterator<AbstractAttachment> i = m_attachments.values().iterator();
//           key == null && i.hasNext(); )
//       {
//         AbstractAttachment attachment = i.next();

//         if(attachment == null)
//           Log.warning("invalid attachment ignored for " + getName());

//         values = attachment.getValues();

//         if(values == null)
//           continue;

//         key = inReader.expect(values.getKeywords());

//         if(key != null)
//           attachment.readValue(inReader, values.getValue(key));
//       }

      //....................................................................

      if(key == null)
      {
        inReader.logWarning(inReader.getPosition(), "entry.key.unknown",
                            "ignoring to next delimiter");

        if(inReader.ignore("" + s_keyDelimiter + s_delimiter) == s_delimiter)
          break;
        else
          continue;
      }

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
  protected @Nullable AbstractExtension addExtension(@Nonnull String inName)
  {
    if(m_extensions.containsKey(inName))
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
      Class cls = Class.forName(Classes.toClassName
                                (name,
                                 "net.ixitxachitls.dma.entries.extensions"));

      // can't use the generic class type here, because generic class arrays
      // cannot be built
      AbstractExtension extension = null;

      // find the constructor to use (getConstructor does not acceptably treat
      // derivations, unfortunately)
      Object []arguments = new Object[names.length + 1];
      arguments[0] = this;

      for(int i = 0; i < names.length; i++)
        arguments[i + 1] = names[i];

      loop : for(Constructor constructor : cls.getConstructors())
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
      Log.warning("could not find class for attachment " + name
                  + ", attachment ignored");
    }
    catch(InstantiationException e)
    {
      Log.warning("could not instantiate class for attachment " + name
                  + ", attachment ignored");
    }
    catch(IllegalAccessException e)
    {
      Log.warning("could access constructor for attachment " + name
                  + ", attachment ignored");
    }
    catch(java.lang.reflect.InvocationTargetException e)
    {
      Log.warning("could not invoke constructor for attachment " + name
                  + ", attachment ignored (" + e.getCause() + ")");
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
  public void addExtension(@Nonnull String inName,
                           @Nonnull AbstractExtension inExtension)
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
  protected void addExtensions(@Nonnull List<String> inNames)
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
  protected void addExtensions(@Nonnull Iterator<String> inNames)
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
  public void addBase(@Nonnull String inName)
  {
    AbstractType<? extends AbstractEntry> baseType = getType().getBaseType();
    if(baseType instanceof Type)
      baseType = ((Type)baseType).getBaseType();
    else
      if(inName.equals(getID()))
        return;

    BaseEntry entry = (BaseEntry)m_data.getEntry(inName, baseType);
    if(entry == null)
      Log.warning("base " + getType() + " '" + inName + "' not found");

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
  public void addTo(@Nonnull DMAFile inFile)
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
  public void addError(@Nonnull BaseError inError)
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
  protected void removeError(@Nonnull BaseError inError)
  {
    if(m_errors == null)
      return;

    for(Iterator i = m_errors.iterator(); i.hasNext(); )
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

    return m_data.update(this);
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
  // TODO: make this more generic and move it to an external class
//   public String computeExpressions(String inText,
//                                    Map<String, ? extends Object> inValues)
//   {
//     String text = inText;
//     StringBuffer result = new StringBuffer();

//     if(inValues != null)
//     {
//       Matcher matcher = s_varPattern.matcher(text);

//       while(matcher.find())
//         if(inValues.containsKey(matcher.group(1)))
//           matcher.appendReplacement(result,
//                                   inValues.get(matcher.group(1)).toString());

//       matcher.appendTail(result);

//       text = result.toString();
//       result = new StringBuffer();
//     }

//     Matcher matcher = s_expPattern.matcher(text);

//     while(matcher.find())
//       matcher.appendReplacement(result,
//                                 "" + computeExpression(matcher.group(1)));

//     matcher.appendTail(result);

//     return result.toString();
//   }

//   private String computeExpression(String inExpression)
//   {
//     inExpression = inExpression.replaceAll("[ \t\n\f\r]", "");

//     StringTokenizer tokens =
//       new StringTokenizer(inExpression, "()+-*/,^", true);

//     return computeExpression(inExpression, tokens);
//   }

//   private String computeExpression(String inExpression,
//                                    StringTokenizer inTokens)
//   {
//     if(!inTokens.hasMoreTokens())
//     {
//       Log.warning("invalid expression, expected more: "  + inExpression);

//       return "* invalid expression, expected (: " + inExpression + " *";
//     }

//     String token = inTokens.nextToken();

//     if("min".equals(token))
//     {
//       if(!"(".equals(inTokens.nextToken()))
//       {
//         Log.warning("invalid expression, expected '(': " + inExpression);

//         return "* invalid expression, expected (: " + inExpression + " *";
//       }

//       String first = computeExpression(inExpression, inTokens);
//       String second = computeExpression(inExpression, inTokens);

//     return "" + Math.min(Integer.parseInt(first), Integer.parseInt(second));
//     }

//     if("max".equals(token))
//     {
//       if(!"(".equals(inTokens.nextToken()))
//       {
//         Log.warning("invalid expression, expected '(': " + inExpression);

//         return "* invalid expression, expect (: " + inExpression + " *";
//       }

//       String first = computeExpression(inExpression, inTokens);
//       String second = computeExpression(inExpression, inTokens);

//     return "" + Math.max(Integer.parseInt(first), Integer.parseInt(second));
//     }

//     if("range".equals(token))
//     {
//       if(!"(".equals(inTokens.nextToken()))
//       {
//         Log.warning("invalid expression, expected '(': " + inExpression);

//         return "* invalid expression, expect (: " + inExpression + " *";
//       }

//     int level = Integer.parseInt(computeExpression(inExpression, inTokens));
//       List<String> ranges = new ArrayList<String>();

//       String current = "";
//       for(String argument = inTokens.nextToken();
//           !"(".equals(argument) && inTokens.hasMoreTokens();
//           argument = inTokens.nextToken())
//       {
//         if(",".equals(argument))
//         {
//           ranges.add(current);
//           current = "";
//         }
//         else
//         {
//           current += argument;
//         }
//       }
//       ranges.add(current);
//       Collections.reverse(ranges);

//       for(String range : ranges) {
//         String []parts = range.split(":\\s*");
//         if(parts.length != 2)
//           continue;

//         try
//         {
//           if(level >= Integer.parseInt(parts[0]))
//             return parts[1];
//         }
//         catch(NumberFormatException e)
//         {
//           // just ignore it
//         }
//       }

//       return "* invalid range *";
//     }

//     try
//     {
//       String value;

//       if("(".equals(token))
//         value = computeExpression(inExpression, inTokens);
//       else
//         value = token;

//       if(!inTokens.hasMoreTokens())
//         return value;

//       String operator = inTokens.nextToken();

//       if(",".equals(operator) || ")".equals(operator))
//         return value;

//       String operand = computeExpression(inExpression, inTokens);

//       if("+".equals(operator))
//         return "" + (Integer.parseInt(value) + Integer.parseInt(operand));

//       if("-".equals(operator))
//         return "" + (Integer.parseInt(value) - Integer.parseInt(operand));

//       if("*".equals(operator))
//         return "" + (Integer.parseInt(value) * Integer.parseInt(operand));

//       if("/".equals(operator))
//         return "" + (Integer.parseInt(value) / Integer.parseInt(operand));

//       if("^".equals(operator))
//         return "" + (int)Math.pow(Integer.parseInt(value),
//                                   Integer.parseInt(operand));

//       Log.warning("invalid operator " + operator + ": " + inExpression);

//       return value;
//     }
//     catch(NumberFormatException e)
//     {
//       Log.warning(e + ", for " + inExpression);

//       return "* invalid number *";
//     }
//   }

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
                          (AbstractEntry.class));

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

//       //System.out.println(entry);

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

//       //System.out.println(entry);

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

//       //System.out.println(entry);

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

//       //System.out.println(entry);

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

//       //System.out.println(entry);

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
