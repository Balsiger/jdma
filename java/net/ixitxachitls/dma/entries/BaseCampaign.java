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

// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.HashMap;
// import java.util.Iterator;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;
// import java.util.SortedSet;
// import java.util.TreeSet;

import javax.annotation.Nonnull;
//import javax.annotation.Nullable;

import net.ixitxachitls.dma.data.DMAData;
//import net.ixitxachitls.dma.entries.indexes.GroupedIndex;
// import net.ixitxachitls.dma.data.DMAFile;
// import net.ixitxachitls.dma.entries.indexes.ExtractorIndex;
// import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
//import net.ixitxachitls.dma.values.Selection;
// import net.ixitxachitls.dma.values.Text;
// import net.ixitxachitls.dma.values.ValueList;
// import net.ixitxachitls.dma.values.formatters.LinkFormatter;
// import net.ixitxachitls.dma.values.formatters.ValueFormatter;
// import net.ixitxachitls.util.Filter;
// import net.ixitxachitls.util.FilteredIterator;
// import net.ixitxachitls.util.Identificator;
// import net.ixitxachitls.util.UniqueIdentificator;
// import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base information about a campaign. It is also the place
 * where all the base entries are finally stored..
 *
 * @file          Basecampaign.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class BaseCampaign extends BaseEntry
                          //implements CampaignData, Iterable<BaseEntry>
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- BaseCampaign ------------------------------

  /**
   * This is the internal, default constructor for an undefined value.
   *
   * @param     inData all the available data
   *
   */
  protected BaseCampaign(@Nonnull DMAData inData)
  {
    super(TYPE, inData);
  }

  //........................................................................
  //---------------------------- BaseCampaign ------------------------------

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base item
   * @param       inData all the available data
   *
   */
  public BaseCampaign(@Nonnull String inName, @Nonnull DMAData inData)
  {
    super(inName, TYPE, inData);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The printer for printing the whole base character. */
  public static final Print s_pagePrint =
    new Print("$title "
              + "${as pdf} ${as text} ${as dma} "
              + "$clear $files \n"
              + "$description "
              + "$par "
              + "%campaigns "
              // admin
              + "%file %errors"
              );

  /** The printer for printing in a list. */
  public static final ListPrint s_listPrint =
    new ListPrint("1:L(label);5:L(id)[ID];20(producttitle)[Title];"
                  + "1:L(worlds)[Worlds];"
                  + "1:L(short)[Short Description]",
                  "$label $listlink", null, "$name", "$worlds",
                  "${short description}");

  /** The type of this entry. */
  public static final BaseType<BaseCampaign> TYPE =
    new BaseType<BaseCampaign>(BaseCampaign.class);
  // TODO: do this right?
  //.withAccess(BaseCharacter.Group.DM);

  static
  {
    extractVariables(BaseCampaign.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

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

  //--------------------------------- isDM ---------------------------------

  /**
   * Check whether the given user is the DM for this entry. Every user is a DM
   * for a base campaign.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   *
   */
  public boolean isDM(@Nonnull BaseCharacter inUser)
  {
    return inUser.hasAccess(BaseCharacter.Group.USER);
  }

  //........................................................................

//   //---------------------------------- get ---------------------------------

//   /**
//     * Get an entry from the underlying campaign. The given name (for base
//     * entries) or the given id (for entries) has to be unique.
//     *
//     * @param       inNameOrID the id or name of the entry to get
//     *
//     * @return      the entry desired or null if not found
//     *
//     * @undefined   may return null
//     *
//     */
//   public <T extends Entry> T getEntry(String inNameOrID, Type<T> inType)
//   {
//     throw new UnsupportedOperationException
//       ("a base campaign does not have entries");
//   }

//   //........................................................................
//   //-------------------------- getAbstractEntries --------------------------

//   /**
//     * Get all the entries in the campaign.
//     *
//     * @return      an iterator over all the entries
//     *
//     * @undefined   never
//     *
//     */
//   @SuppressWarnings("unchecked")
//   public Iterator<AbstractEntry> getAbstractEntries()
//   {
//     return (Iterator<AbstractEntry>)(Iterator)iterator();
//   }

//   //........................................................................
//   //---------------------------- getBaseEntries ----------------------------

//   /**
//    * Get the all the base entries.
//    *
//    * @return      an iterator to all entries
//    *
//    * @undefined   never
//    *
//    */
//   public Iterator<BaseEntry> iterator()
//   {
//     return m_bases.iterator();
//   }

//   //........................................................................
//   //---------------------------- getBaseEntries ----------------------------

//   /**
//    * Get the all the base entries.
//    *
//    * @param       inType the type of entries to get
//    *
//    * @return      an iterator to all entries
//    *
//    * @undefined   never
//    *
//    */
//   public Iterator<? extends AbstractEntry> iterator
//     (AbstractEntry.Type<? extends AbstractEntry> inType)
//   {
//     if(inType == BaseProduct.TYPE)
//       return m_baseProducts.iterator();

//     return m_bases.iterator();
//   }

//   //........................................................................
//   //---------------------------- getBaseEntries ----------------------------

//   /**
//    * Get the some of the base entries.
//    *
//    * @param       inFilter a filter to select the entries to show
//    *
//    * @return      an iterator to all entries
//    *
//    * @undefined   never
//    *
//    */
//   public Iterator<BaseEntry> getBaseEntries(Filter<BaseEntry> inFilter)
//   {
//     return new FilteredIterator<BaseEntry>(m_bases.iterator(), inFilter);
//   }

//   //........................................................................
//   //----------------------------- getBaseEntry -----------------------------

//   /**
//    * Get a specific base entry.
//    *
//    * @param       inName the name of the base entry to get
//    *
//    * @return      the desired entry or null if not found
//    *
//    * @undefined   may return null
//    *
//    */
// //   public BaseEntry getBaseEntry(String inName)
// //   {
// //     return m_bases.getByID(inName);
// //   }

//   //........................................................................
//   //----------------------------- getBaseEntry -----------------------------

//   /**
//    * Get a specific base entry.
//    *
//    * @param       inName the name of the base entry to get
//    *
//    * @return      the desired entry or null if not found
//    *
//    * @undefined   may return null
//    *
//    */
//   @SuppressWarnings("unchecked") // casting to return type
//   public <T extends BaseEntry> T getBaseEntry(String inName, Type<T> inType)
//   {
//     if(inType == BaseProduct.TYPE)
//       return (T)m_baseProducts.getByID(inName);

//     return (T)m_bases.getByID(inName);
//   }

//   //........................................................................
//   //-------------------------------- lookup --------------------------------

//   /**
//    * Lookup the name of a base entry matching the requirements of the
//    * given entry.
//    *
//    * @param       inEntry the entry to match to
//    * @param       inType  the type of entry too look for
//    *
//    * @return      a matching base entry (or null if none is found)
//    *
//    */
//   @MayReturnNull
//   public BaseEntry lookup(@MayBeNull final BaseEntry inEntry,
//                           @MayBeNull final AbstractEntry.Type inType)
//   {
//     return lookup(m_bases, new Filter<BaseEntry>()
//     {
//       public boolean accept(BaseEntry inBase)
//       {
//         if(inBase == null)
//           return false;

//         if(inEntry == null)
//           return inBase.getType() == inType;
//         else
//           return inEntry.matches(inBase);
//       }
//     });
//   }

//   //........................................................................
//   //-------------------------------- lookup --------------------------------

//   /**
//    * Lookup the name of a base entry matching the requirements of the
//    * given entry.
//    *
//    * @param       inFilter the filter to determine which entries match for
//    *                       the lookup.
//    *
//    * @return      a matching base entry (or null if none is found)
//    *
//    * @undefined   can return null if none is found
//    *
//    */
//   public BaseEntry lookup(Filter<BaseEntry> inFilter)
//   {
//     return lookup(m_bases, inFilter);
//   }

//   //........................................................................
//   //----------------------------- getBaseTypes -----------------------------

//   /**
//    * Get all the base types stored in the campaign.
//    *
//    * @return      a list of the distinct classes stored.
//    *
//    */
//   public SortedSet<AbstractEntry.Type> getBaseTypes()
//   {
//     return m_baseTypes;
//   }

//   //........................................................................
//   //----------------------------- getFilenames -----------------------------

//   /**
//    * Get the filenames for this base campaign.
//    *
//    * @return      a list of all the file names
//    *
//    * @undefined   never
//    *
//    */
//   public List<String> getFilenames()
//   {
//     List<String> files = new ArrayList<String>();

//     for(DMAFile file : m_dmaFiles.values())
//       files.add(file.getStorageName());

//     Collections.sort(files);

//     return files;
//   }

//   //........................................................................
//   //------------------------------- getFile --------------------------------

//   /**
//    * Get the DMA file for a given (partial) file name.
//    *
//    * @param       inName the name of the file to get
//    *
//    * @return      the DMA file found or null if not found
//    *
//    * @undefined   may return null
//    *
//    */
//   public DMAFile getFile(String inName)
//   {
//     if(inName == null)
//       return null;

//     for(DMAFile file : m_dmaFiles.values())
//       if(inName.equals(file.getStorageName()))
//         return file;

//     return null;
//   }

//   //........................................................................
//   //---------------------------- getFileNumber -----------------------------

//   /**
//    * Get the number of files read.
//    *
//    * @return      the number of files read.
//    *
//    * @undefined   never
//    *
//    */
//   public int getFileNumber()
//   {
//     return m_dmaFiles.size();
//   }

//   //........................................................................
//   //-------------------------------- sizes ---------------------------------

//   /**
//    * Get the number of base entries stored.
//    *
//    * @return      the number of base entries
//    *
//    * @undefined   never
//    *
//    */
//   public Map<AbstractEntry.Type, Integer> sizes()
//   {
//     Map<AbstractEntry.Type, Integer> counts =
//       new HashMap<AbstractEntry.Type, Integer>();

//     for(BaseEntry entry : this)
//     {
//       AbstractEntry.Type type = entry.getType();

//       Integer current = counts.get(type);

//       if(current == null)
//         current = 0;

//       counts.put(type, current + 1);
//     }

//     return counts;
//   }

//   //........................................................................
//   //-------------------------- getFileLineNumber ---------------------------

//   /**
//    * Get the number of lines of all the files read.
//    *
//    * @return      the number of lines read
//    *
//    * @undefined   never
//    *
//    */
//   public long getFileLineNumber()
//   {
//     return m_lines;
//   }

//   //........................................................................
//   //--------------------------------- size ---------------------------------

//   /**
//    * Get the number of base entries stored.
//    *
//    * @return      the number of base entries
//    *
//    * @undefined   never
//    *
//    */
//   public long size()
//   {
//     return m_bases.size();
//   }

//   //........................................................................

  // //---------------------------- createBaseView ----------------------------

  // /**
  //  * Get a view on the base values of the repository.
  //  *
  //  * @param       inFilter        the filter to select the data
  //  * @param       inIdentificator the functor to get the entry IDs
  //  *
  //  * @return      the requested view
  //  *
  //  * @undefined   never
  //  *
  //  */
  // public View<BaseEntry>
  //   createBaseView(Filter<BaseEntry> inFilter,
  //                  Identificator<BaseEntry> inIdentificator)
  // {
  //   return m_bases.createView(inFilter, inIdentificator);
  // }

  // //........................................................................
  // //------------------------------ createView ------------------------------

  // /**
  //  * Create a view of abstract entries with all the base campaign entries.
  //  *
  //  * This method is in addition to <CODE>createBaseView</CODE>, because of
//  * the <CODE>AbstractEntry</CODE> type required by <CODE>CampaignData</CODE>.
  //  *
  //  * @param       inFilter the filter to select the values
  //  * @param       inIdentificator the identificator to identify the values
  //  *
  //  * @return      the view with all selected values
  //  *
  //  * @undefined   never
  //  *
  //  */
  // @SuppressWarnings("unchecked")
  // public View<AbstractEntry>
  //   createView(Filter<AbstractEntry> inFilter,
  //              Identificator<AbstractEntry> inIdentificator)
  // {
  //   // we cannot use createView in m_bases, as the type would be wrong.
  //   return new View<AbstractEntry>
  //     (new FilteredIterator<AbstractEntry>
  //      ((Iterator<AbstractEntry>)(Iterator)m_bases.getUnique(), inFilter),
  //      inIdentificator);
  // }

  // //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  // //------------------------------- readFile -------------------------------

  // /**
  //  * Read a dma file into the campaign.
  //  *
  //  * @param       inName the name of the file to read
  //  * @param       inPath the path to the fiel to read
  //  *
  //  * @return      true if read without error, false else
  //  *
  //  */
  // public boolean readFile(@MayBeNull String inName, String inPath)
  // {
  //   if(inName == null)
  //     return false;

  //   if(m_dmaFiles.containsKey(inName))
  //     return false;

  //   DMAFile file = new DMAFile(inName, inPath, this);

  //   if(file.wasRead())
  //   {
  //     m_dmaFiles.put(inName, file);
  //     m_lines += file.getLines();

  //     // store the names in the GLOBAL campaign as well
  //     if(!GLOBAL.m_dmaFiles.containsKey(inName))
  //     {
  //       GLOBAL.m_dmaFiles.put(inName, file);
  //       GLOBAL.m_lines += file.getLines();
  //     }

  //     return true;
  //   }

  //   return false;
  // }

  // //........................................................................
  // //------------------------------- complete -------------------------------

  // /**
  //  * Complete the entry and make sure that all values are filled.
  //  *
  //  */
  // public void complete()
  // {
  //   super.complete();

  //   for(Text text : m_files)
  //     readFile(text.get(), Global.DATA_DIR);
  // }

  // //........................................................................

  // //--------------------------------- add ----------------------------------

  // /**
  //  * Add the given entry to the global repository.
  //  *
  //  * @param       inEntry the entry to add
  //  *
  //  * @return      true if added, false if not
  //  *
  //  */
  // @SuppressWarnings("unchecked") // repository is not typed
  // public boolean add(BaseEntry inEntry)
  // {
  //   if(inEntry == null)
  //     return false;

  //   Type type = inEntry.getType();
  //   m_baseTypes.add(type);

  //   Repository data;
  //   if(type == BaseProduct.TYPE)
  //     data = m_baseProducts;
  //   else
  //     data = m_bases;

  //   // only complete it if it was not yet there, otherwise we'd do it twice
  //   if(data.getByID(inEntry.getName()) == null)
  //     inEntry.complete();

  //   if(!data.add(inEntry))
  //     Log.warning("Could not add this entry:\n" + inEntry + "\n");

  //   if(this != GLOBAL)
  //     GLOBAL.add(inEntry);

  //   return true;
  // }

  // //........................................................................
  // //--------------------------------- add ----------------------------------

  // /**
  //  * Add the given entry to the global repository.
  //  *
  //  * @param       inEntry the entry to add
  //  *
  //  * @return      true if added, false if not
  //  *
  //  */
  // public boolean add(AbstractEntry inEntry)
  // {
  //   if(!(inEntry instanceof BaseEntry))
  //   throw new IllegalArgumentException("entry must be a base entry to add");

  //   return add((BaseEntry)inEntry);
  // }

  // //........................................................................
  // //-------------------------------- write ---------------------------------

  // /**
  //  * Write the campaign and all its contents.
  //  *
  //  * @return      true if successfully written, false if not
  //  *
  //  * @undefined   never
  //  *
  //  */
  // public boolean write()
  // {
  //   boolean result = true;

  //   for(DMAFile file : m_dmaFiles.values())
  //     if(file.isChanged())
  //       result &= file.write();

  //   return result;
  // }

  // //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  // //-------------------------------- lookup --------------------------------

  // /**
  //  * Internal method to make a lookup for a base item.
  //  *
  //  * @param       inData   the data storage to use for getting the data
  //  * @param       inFilter the filter to select the base entries
  //  *
  //  * @return      a matching base entry (or null if none is found)
  //  *
  //  */
  // @MayReturnNull
  // protected static BaseEntry lookup(Repository<BaseEntry> inData,
  //                                   Filter<BaseEntry> inFilter)
  // {
  //   assert inData != null : "must have data here";
  //   assert inFilter != null : "must have a filter here";

  //   // TODO: optimize this by using a filtered iterator
  //   ArrayList<BaseEntry> matching = new ArrayList<BaseEntry>();
  //   int total = 0;

  //   for(BaseEntry base : inData)
  //   {
  //     if(inFilter.accept(base))
  //     {
  //       matching.add(base);
  //       total += base.getProbability();
  //     }
  //   }

  //   if(total == 0)
  //     return null;

  //   // determine a random value
  //   int select = s_random.nextInt(total);

  //   for(BaseEntry base : matching)
  //   {
  //     select -= base.getProbability();

  //     if(select <= 0)
  //       return base;
  //   }

  //   return null;
  // }

  // //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. @hidden */
  public static class Test extends ValueGroup.Test
  {
    // TODO: fix tests
    //----- text -----------------------------------------------------------

    /** Test text. */
    private static String s_text =
      "base campaign Test = \n"
      + "\n"
      + "  synonyms          \"test\", \"tst\";"
      + "  worlds            Generic, Forgotten Realms;"
      + "  short description \"Just a test\";"
      + "  description       \"A test campaign\".";

//     //......................................................................
    //----- createBaseCampaign() -------------------------------------------

    /** Create a typical base item for testing purposes.
     *
     * @return the newly created base item
     *
     */
    public static BaseCampaign createBaseCampaign()
    {
      net.ixitxachitls.input.ParseReader reader =
        new net.ixitxachitls.input.ParseReader
        (new java.io.StringReader(s_text), "test");

      return (BaseCampaign)AbstractEntry.read(reader, null);
    }

    //......................................................................

    //----- read -----------------------------------------------------------

    /** Test reading. */
    @org.junit.Test
    public void read()
    {
      String result =
        "#----- Test\n"
        + "\n"
        + "base campaign Test =\n"
        + "\n"
        + "  worlds            Generic,\n"
        + "                    Forgotten Realms;\n"
        + "  description       \"A test campaign\";\n"
        + "  short description \"Just a test\";\n"
        + "  synonyms          \"test\",\n"
        + "                    \"tst\".\n"
        + "\n"
        + "#.....\n";

      BaseCampaign entry = createBaseCampaign();

      assertNotNull("base campagin should have been read", entry);
      assertEquals("base campaign name does not match", "Test",
                   entry.getName());

      assertEquals("synonyms", 2, entry.m_synonyms.size());
      assertEquals("synonyms", "\"test\"", entry.m_synonyms.get(0).toString());
      assertEquals("synonyms", "\"tst\"", entry.m_synonyms.get(1).toString());

      assertEquals("world", "Generic,\nForgotten Realms",
                   entry.m_worlds.toString());

      assertEquals("short description", "\"Just a test\"",
                   entry.m_short.toString());

      assertEquals("description", "\"A test campaign\"",
                   entry.m_description.toString());

      assertEquals("base campaign does not match", result, entry.toString());
    }

    //......................................................................
  }

  //........................................................................
}
