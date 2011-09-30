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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.data.DMAFile;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.formatters.LinkFormatter;
import net.ixitxachitls.dma.values.formatters.Formatter;
import net.ixitxachitls.output.commands.Divider;
import net.ixitxachitls.output.commands.Span;
import net.ixitxachitls.util.Filter;
import net.ixitxachitls.util.FilteredIterator;
import net.ixitxachitls.util.Identificator;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.UniqueIdentificator;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the storage container for campaign specific information.
 *
 * @file          Campaign.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Campaign extends Entry<BaseCampaign>
                      //implements CampaignData, Iterable<Entry>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Campaign -------------------------------

  /**
   * This is the internal default constructor.
   *
   * @param       inData all the available data
   *
   */
  protected Campaign(@Nonnull DMAData inData)
  {
    super(TYPE, BASE_TYPE, inData);
  }

  //........................................................................
  //------------------------------- Campaign -------------------------------

  /**
   * This is the normal constructor.
   *
   * @param       inName  the name of the campaign
   * @param       inData all the available data
   *
   */
  public Campaign(String inName, @Nonnull DMAData inData)
  {
    super(inName, TYPE, BASE_TYPE, inData);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final Type<Campaign> TYPE =
    new Type<Campaign>(Campaign.class, BaseCampaign.TYPE);

  /** The type of the base entry to this entry. */
  public static final BaseType<BaseCampaign> BASE_TYPE = BaseCampaign.TYPE;

  /** The print for printing a whole page entry. */
  public static final Print s_pagePrint =
    new Print("$title"
              + "$clear $files\n"
              + "$par "
              + "%name "
              + "%base "
              + "%dm "
              // admin
              + "%file %errors"
              );

  /** The printer for printing in a list. */
  public static final ListPrint s_listPrint =
    new ListPrint("1:L(label);20:L(id)[ID];20(producttitle)[Title];"
                  + "1:L(system)[System];1:L(worlds)[Worlds];"
                  + "1:L(status)[Status]",
                  "$label $listlink", null, "$name", "$+system", "$+worlds",
                  "$status");

  /** The base types stored in the campaign. */
  private final SortedSet<AbstractType> m_types = new TreeSet<AbstractType>();

  /** The files read for this campaign. */
  private final Map<String, DMAFile> m_dmaFiles =
    new HashMap<String, DMAFile>();

  /** The number of lines read. */
  private int m_lines = 0;

  //----- files -----------------------------------------------------------

  /** The files in the campaign. */
  @Key("files")
  protected ValueList<Text> m_files = new ValueList<Text>(", ", new Text());

  //........................................................................
  //----- dm ---------------------------------------------------------------

  /** The formatter for the dm. */
  protected static Formatter<Name> s_dmFormatter =
    new LinkFormatter<Name>("/index/dms/");

  /** The dm for this campaign. */
  @Key("dm")
  protected Name m_dm = new Name().withFormatter(s_dmFormatter);

  static
  {
    // s_indexes.add(new KeyIndex<KeyIndex>("Campaign", "DMs", "dms", "dm",
    //                                      true, FORMATTER, FORMAT, false, null)
    //               .withAccess(BaseCharacter.Group.DM)
    //               .withDataSource(Index.DataSource.campaign));
  }

  //........................................................................

  static
  {
    extractVariables(Campaign.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getDMName -------------------------------

  /**
   * Get the name of the DM of the campaign.
   *
   * @return      a String with the name
   *
   */
  public @Nonnull String getDMName()
  {
    return m_dm.get();
  }

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

  //----------------------------- getFilenames -----------------------------

  /**
   * Get the names of the files read into this campaign.
   *
   * @return      a list with all the file names
   *
   * @undefined   never
   *
   */
  public List<String> getFilenames()
  {
    ArrayList<String> files = new ArrayList<String>();

    for(DMAFile file : m_dmaFiles.values())
      files.add(file.getStorageName());

    return files;
  }

  //........................................................................
  //------------------------------- iterator -------------------------------

  /**
   * Get all the entries in the campaign.
   *
   * @return      an iterator over all the entries
   *
   * @undefined   never
   *
   */
  // public Iterator<Entry> iterator()
  // {
  //   return m_entries.iterator();
  // }

  //........................................................................
  //------------------------------ getUnique -------------------------------

  /**
   * Get all unique entries, without listing synonyms multiple times.
   *
   * @return      an iterator over all unique items
   *
   * @undefined   never
   *
   */
  // public Iterator<Entry> getUnique()
  // {
  //   return m_entries.getUnique();
  // }

  //........................................................................
  //------------------------------ getEntries ------------------------------

  /**
   * Get all the entries in the campaign.
   *
   * @return      an iterator over all the entries
   *
   * @undefined   never
   *
   */
  // @Deprecated
  // public Iterator<Entry> getEntries()
  // {
  //   return m_entries.iterator();
  // }

  //........................................................................
  //-------------------------- getAbstractEntries --------------------------

  /**
    * Get all the entries in the campaign.
    *
    * @return      an iterator over all the entries
    *
    * @undefined   never
    *
    */
  // @SuppressWarnings("unchecked")
  // public Iterator<AbstractEntry> getAbstractEntries()
  // {
  //   return (Iterator<AbstractEntry>)(Iterator)m_entries.iterator();
  // }

  //........................................................................
  //-------------------------------- getEntry ------------------------------

  /**
    *
    * Get an entry from the underlying campaign.
    *
    * @param       inID   the id of the entry to get
    * @param       inType the type of the entry to get
    *
    * @return      the entry desired or null if not found
    *
    * @undefined   may return null
    *
    */
  // public <T extends Entry> T getEntry(String inID, Type<T> inType)
  // {
  //   return (T)getEntry(inID);
  // }
  // public Entry getEntry(String inID)
  // {
  //   return m_entries.getByID(inID);
  // }

  //........................................................................
  //----------------------------- getBaseEntry -----------------------------

  /**
   * Get a specific base entry.
   *
   * @param       inName the name of the base entry to get
   *
   * @return      the desired entry or null if not found
   *
   * @undefined   may return null
   *
   */
  // public <T extends BaseEntry> T getBaseEntry(String inName, Type<T> inType)
  // {
  //   throw new UnsupportedOperationException("not implemented");
  // }

  //........................................................................
  //---------------------------- getEntriesFor -----------------------------

  /**
   * Get all the entries for the given base entry.
   *
   * @param       inBase the base entry to look for entries of
   *
   * @return      all the entries found
   *
   * @undefined   never (returns empty list if nothing found)
   *
   */
  // public List<Entry> getEntriesFor(BaseEntry inBase)
  // {
  //   List<Entry> result = new ArrayList<Entry>();

  //   if(inBase == null)
  //     return result;

  //   for(Entry entry : this)
  //     if(entry.hasBase(inBase))
  //       result.add(entry);

  //   return result;
  // }

  //........................................................................

  //------------------------------ getValues -------------------------------

  /**
    * Get all the entries with a given name from the underlying campaign.
    *
    * @param       inName the name of the entry to get
    *
    * @return      the entry desired or null if not found
    *
    * @undefined   may return null
    *
    */
  // public Iterator<Entry> getValues(String inName)
  // {
  //   return m_entries.iterator(inName);
  // }

  //........................................................................
  //------------------------------- getFile --------------------------------

  /**
    *
    * Get the DMA file for a given (partial) file name.
    *
    * @param       inName the name of the file to get
    *
    * @return      the DMA file found or null if not found
    *
    * @undefined   may return null
    *
    * @example     DMAFile file = campaign.get("Generic");
    *
    */
  // public DMAFile getFile(String inName)
  // {
  //   if(inName == null)
  //     return null;

  //   for(DMAFile file : m_dmaFiles.values())
  //     if(inName.equals(file.getStorageName()))
  //       return file;

  //   return null;
  // }

  //........................................................................
  //------------------------------ createView ------------------------------

  /**
   * Create a view of abstract entries with all the campaign entries.
   *
   * @param       inFilter the filter to select the values
   * @param       inIdentificator the identificator to identify the values
   *
   * @return      the view with all selected values
   *
   * @undefined   never
   *
   */
  // @SuppressWarnings("unchecked")
  // public View<AbstractEntry>
  //   createView(Filter<AbstractEntry> inFilter,
  //              Identificator<AbstractEntry> inIdentificator)
  // {
  //   // we cannot use createView in m_bases, as the type would be wrong.
  //   return new View<AbstractEntry>
  //     (new FilteredIterator<AbstractEntry>
  //      ((Iterator<AbstractEntry>)(Iterator)m_entries.iterator(), inFilter),
  //      inIdentificator);
  // }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- readFile -------------------------------

  /**
   * Read a dma file into the campaign.
   *
   * @param       inName the name of the file to read
   * @param       inPath the path to the file
   *
   * @return      true if read without error, false else
   *
   * @undefined   IllegalArgumentException if no name is given
   *
   */
   // public boolean readFile(String inName, String inPath)
   // {
   //   if(inName == null)
   //     throw new IllegalArgumentException("must have a name here");

   //   if(m_dmaFiles.containsKey(inName))
   //     return false;

   //   DMAFile file = new DMAFile(inName, inPath, this);

   //   m_dmaFiles.put(inName, file);
   //   m_lines += file.getLines();

   //   return true;
   // }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the given entry to the repository.
   *
   * @param       inEntry the entry to add
   *
   * @return      true if added, false if not because of error
   *
   * @undefined   IllegalArgumentException if no entry given
   *
   */
//   @SuppressWarnings("unchecked")
//   public boolean add(AbstractEntry inEntry)
//   {
//     if(inEntry == null)
//       throw new IllegalArgumentException("must have an entry here, dude");

//     if(inEntry.isBase())
//       throw new IllegalArgumentException("must not have a base entry here: "
//                                          + inEntry.getName() + inEntry);

//     m_types.add(inEntry.getType());

//     Entry entry = (Entry)inEntry;

//     // don't add it if the entry is already in another campaign
//     if(entry.isInCampaign())
//       return false;

//     // we need to adjust the ID in case it is wrong or in case there is
//     // none
//     String oldID = entry.getID();
//     String newID = updateID(oldID);

//     if(oldID != newID)
//       entry.setID(newID);

//     // update the base entry for this entry
//     BaseEntry base = null;

//     // if we don't have a name for the entry, try to look one up
//     if(entry.getName() == null)
//     {
//       base = BaseCampaign.GLOBAL.lookup(entry.getLookup(),
//                                         entry.getType().getBaseType());

//       if(base == null)
//       {
//         Log.warning("base entry " + entry.getName() + " for " + entry.getID()
//                     + " not found (lookup)");

//         return false;
//       }

//       // set the name of the entry to the name of the base item
//       entry.setName(base.getName());
//     }
//     else

//       // TODO: check that we can really remove this
// //     {
// //       String name = entry.getBaseName();

// //       // if the entry has no base name defined, take the real entry name
// //       if(name != null)
// //         base = BaseCampaign.GLOBAL.getBaseEntry(name);
// //       else
// //         base = BaseCampaign.GLOBAL.getBaseEntry(entry.getName());
// //     }

// //     if(base == null)
// //       Log.warning("base entry " + entry.getName() + " for " + entry.getID()
// //                   + " not found");
// //     else
// //       entry.setBase(base);

//     // remember that entry is in campaign
//     entry.addToCampaign(this);

//     // complete the entry
//     entry.complete();

//     // now add the entry
//     m_entries.add(entry);

//     // complete the entry
//     entry.complete();

//     // check the entry for possible problems
//     entry.check();

//     return true;
//   }

  //........................................................................
  //------------------------------- replace --------------------------------

  /**
   * Replace the given entry in the repository.  NOTE: Calling this method is
   * dangerous, as it will replace the entry but of course not any of the
   * references hold to the previous entry. This might screw up all kinds of
   * processes in the future.
   *
   * @param       inEntry the entry to replace
   *
   * @return      true if replace, false if not because of error
   *
   * @undefined   IllegalArgumentException if no entry given
   *
   */
  // @SuppressWarnings("unchecked")
  // public boolean replace(AbstractEntry inEntry)
  // {
  //   if(inEntry == null)
  //     throw new IllegalArgumentException("must have an entry here, dude");

  //   if(inEntry.isBase())
  //     throw new IllegalArgumentException("must not have a base entry here: "
  //                                        + inEntry.getName() + inEntry);

  //   Entry entry = (Entry)inEntry;

  //   // remove the current entry
  //   m_entries.removeByID(entry.getID());

  //   // move the entry to the new campaign
  //   entry.removeFromCampaign();
  //   entry.addToCampaign(this);

  //   // now add the entry
  //   m_entries.add(entry);

  //   return true;
  // }

  //........................................................................

  //------------------------------ updateID --------------------------------

  /**
    *
    * Update the ID for the current campaign, if necessary.
    *
    * @param       inID the current ID or null if currently none
    *
    * @return      a String with the ID &lt;short&gt;-&lt;number&gt; for the
    *              current campaign
    *
    * @undefined   never
    *
    * @algorithm   just put together the ID
    *
    * @derivation  possible
    *
    * @example     String id = campaign.createEntryID("Gen-444");
    *
    * @bugs
    * @to_do
    *
    * @keywords    update . id . entry
    *
    */
  // public String updateID(String inID)
  // {
  //   if(inID != null)
  //     if(inID.startsWith(m_shortName.get() + "-"))
  //     {
  //       // we have an id, thus fix the current max
  //       long current = Integer.parseInt(Strings.getPattern(inID, ".*-(\\d+)"));

  //       if(current > m_maxID.get())
  //       {
  //         m_maxID.set(current);
  //         changed();
  //       }

  //       return inID;
  //     }

  //   m_maxID.set(m_maxID.get() + 1);
  //   changed();

  //   return m_shortName.get() + "-" + m_maxID;
  // }

  //........................................................................
  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled.
   *
   */
  // public void complete()
  // {
  //   // prevent the completion of files with the base values
  //   boolean defined = m_files.isDefined();

  //   super.complete();

  //   if(!defined)
  //     m_files.reset();

  //   for(Text text : m_files)
  //   {
  //     String file = text.get();

  //     readFile(file, Global.DATA_DIR);
  //   }
  // }

  //........................................................................
  //-------------------------------- write ---------------------------------

  /**
    *
    * Write the campaign and all its contents.
    *
    * @return      true if successfully written, false if not
    *
    * @undefined   never
    *
    * @example     if(!file.write()) ...
    *
    */
  // public boolean write()
  // {
  //   boolean result = true;

  //   for(DMAFile file : m_dmaFiles.values())
  //     if(file.isChanged())
  //       result &= file.write();

  //   return result;
  // }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  /*  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }
  */

  //........................................................................
}
