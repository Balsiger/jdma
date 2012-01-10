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

import javax.annotation.Nonnull;

// import net.ixitxachitls.dma.data.Storage;
// import net.ixitxachitls.dma.entries.actions.Action;
// import net.ixitxachitls.dma.entries.attachments.AbstractAttachment;
// import net.ixitxachitls.dma.entries.indexes.Index;
// import net.ixitxachitls.dma.values.Selection;
import net.ixitxachitls.dma.values.ID;
// import net.ixitxachitls.dma.values.Text;
// import net.ixitxachitls.dma.values.Value;
// import net.ixitxachitls.dma.values.ValueList;
// import net.ixitxachitls.input.ParseReader;
// import net.ixitxachitls.output.commands.Bold;
// import net.ixitxachitls.output.commands.Command;
// import net.ixitxachitls.output.commands.Divider;
// import net.ixitxachitls.output.commands.Large;
// import net.ixitxachitls.output.commands.Link;
// import net.ixitxachitls.output.commands.Script;
// import net.ixitxachitls.output.commands.Span;
// import net.ixitxachitls.output.commands.Super;
// import net.ixitxachitls.output.commands.Title;
// import net.ixitxachitls.util.Extractor;
// import net.ixitxachitls.util.Files;
// import net.ixitxachitls.util.Pair;
// import net.ixitxachitls.util.Encodings;
// import net.ixitxachitls.util.Strings;
// import net.ixitxachitls.util.configuration.Config;
// import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base class for all jDMA entries (not base entries!).
 *
 * @file          Entry.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @param         <B> the type of base entry associated with this entry
 */

//..........................................................................

//__________________________________________________________________________

public class Entry<B extends BaseEntry> extends AbstractEntry
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Entry ---------------------------------

  /**
   * The complete and 'default' constructor.
   *
   * @param       inName     the name of the entry
   * @param       inType     the type of the entry
   * @param       inBaseType the type of the base entry to this one
   *
   */
  protected Entry(@Nonnull String inName,
                  @Nonnull Type<? extends Entry> inType,
                  @Nonnull BaseType<? extends BaseEntry> inBaseType)
  {
    this(inName, inType, inBaseType, new BaseEntry[0]);
  }

  //........................................................................
  //-------------------------------- Entry ---------------------------------

  /**
   * The default constructor.
   *
   * @param       inType     the type of the entry
   * @param       inBaseType the type of the base entry to this one
   *
   */
  protected Entry(@Nonnull Type<? extends Entry> inType,
                  @Nonnull BaseType<? extends BaseEntry> inBaseType)
  {
    super(inType);

    m_baseType = inBaseType;
  }

  //........................................................................
  // //-------------------------------- Entry ---------------------------------

  // /**
  //  * The constructor with a name only.
  //  *
  //  * @param       inName     the name of the entry
  //  *
  //  * @undefined   never
  //  *
  //  */
  // public Entry(String inName)
  // {
  //   this(inName, TYPE, BASE_TYPE);
  // }

  // //........................................................................
  // //-------------------------------- Entry ---------------------------------

  // /**
  //  * The constructor with a name only.
  //  *
  //  * @param       inName     the name of the entry
  //  *
  //  * @undefined   never
  //  *
  //  */
  // public Entry(String inName, BaseEntry ... inBases)
  // {
  //   this(inName, TYPE, BASE_TYPE, inBases);
  // }

  // //........................................................................
  //-------------------------------- Entry ---------------------------------

  /**
   * The complete constructor.
   *
   * @param       inName     the name of the entry
   * @param       inType     the type of the entry
   * @param       inBaseType the type of the base entry to this one
   * @param       inBases    the base entries to use
   *
   */
  protected Entry(@Nonnull String inName,
                  @Nonnull Type<? extends Entry> inType,
                  @Nonnull BaseType<? extends BaseEntry> inBaseType,
                  @Nonnull BaseEntry ... inBases)
  {
    super(inName, inType);
    // TODO: do I really need to support bases here?
    //super(inName, inType, inData, inBases);

    m_baseType = inBaseType;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of the base entry. */
  protected @Nonnull BaseType<? extends BaseEntry> m_baseType;

  /** The type of the base to this entry. */
  public static final BaseType<BaseEntry> BASE_TYPE =
    BaseEntry.TYPE;

  /** The type of this entry. */
  public static final Type<? extends Entry> TYPE =
    new Type<Entry>(Entry.class, BaseEntry.TYPE);

  /** The name of a temporary entry. */
  public static final String TEMPORARY = "TEMPORARY";

  static
  {
    TYPE.withLink("entry", "entries");
  }

  //----- id ---------------------------------------------------------------

  /** The reference id of the entry. */
  // @Key("id")
  // protected ID m_id = new ID();

  //........................................................................

  static
  {
    extractVariables(Entry.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  // //------------------------------- getBase --------------------------------

  // /**
  //  * Get the base entry of this entry, if any.
  //  *
  //  * @return      the base entry or null if none
  //  *
  //  * @undefined   may return null
  //  *
  //  */
  // @Deprecated // We have more than one now?
  // @SuppressWarnings("unchecked")
  // public B getBase()
  // {
  //   if(m_baseEntries == null)
  //     return null;

  //   return (B)m_baseEntries.get(0);
  // }

  // //........................................................................
  // //------------------------------- getBase --------------------------------

  // /**
  //  * Get the first base entry of this entry, if any.
  //  *
  //  * @return      the base entry or null if none
  //  *
  //  * @undefined   may return null
  //  *
  //  */
  // @SuppressWarnings("unchecked")
  // public B getFirstBase()
  // {
  //   if(m_baseEntries == null)
  //     return null;

  //   return (B)m_baseEntries.get(0);
  // }

  // //........................................................................
  // //----------------------------- getBaseName ------------------------------

  // /**
  //  * Get the name of the base entry this entry is based on.
  //  *
  //  * @return      the requested base name
  //  *
  //  */
  // @MayReturnNull
  // public String getBaseName()
  // {
  //   if(m_baseNames == null || m_baseNames.size() == 0)
  //     return null;

  //   return m_baseNames.get(0);
  // }

  // //........................................................................
  // //----------------------------- getBaseType ------------------------------

  // /**
  //  * Get the type of the base entry.
  //  *
  //  * @return      the requested type
  //  *
  //  */
  // public Type getBaseType()
  // {
  //   return m_baseType;
  // }

  // //........................................................................
  // //-------------------------- getBaseAttachments --------------------------

  // /**
  //  * Get all the attachments of all base entries.
  //  *
  //  * @return      a list of all the attachments
  //  *
  //  */
  // public List<AbstractAttachment> getBaseAttachments()
  // {
  //   List<AbstractAttachment> result = new ArrayList<AbstractAttachment>();

  //   if(m_baseEntries == null)
  //     return result;

  //   for(BaseEntry base : m_baseEntries)
  //   {
  //     if(base == null)
  //       continue;

  //     for(Iterator<AbstractAttachment> i = base.getAttachments();
  //         i.hasNext(); )
  //       result.add(i.next());
  //   }

  //   return result;
  // }

  // //........................................................................
  // //-------------------------------- getDM ---------------------------------

  // /**
  //  * Get the DM responsible for the campaign this entry is in
  //  *
  //  * @return      the DM or null if unknown or not in a campaign
  //  *
  //  */
  // @MayReturnNull
  // public BaseCharacter getDM()
  // {
  //   CampaignData campaign = m_campaign;

  //   while(campaign != null && !(campaign instanceof Campaign))
  //     campaign = campaign.getCampaign();

  //   if(campaign == null)
  //     return null;

  //   return BaseCampaign.GLOBAL.getBaseEntry(((Campaign)campaign).getDMName(),
  //                                           BaseCharacter.TYPE);
  // }

  // //........................................................................
  // //------------------------------ getPlayer -------------------------------

  // /**
  //  * Get the player possessing this entry, if any.
  //  *
  //  * @return      the player or null if not in possession
  //  *
  //  */
  // @MayReturnNull
  // public Character getPlayer()
  // {
  //   Storage<? extends AbstractEntry> storage = getStorage();

  //   while(storage != null)
  //   {
  //     if(storage instanceof Character)
  //       return (Character)storage;

  //     if(storage instanceof AbstractEntry)
  //       storage = ((AbstractEntry)storage).getStorage();
  //     else
  //       return null;
  //   }

  //   return null;
  // }

  // //........................................................................

  // //---------------------------- getSubEntries -----------------------------

  // /**
  //  * Get the entries that are part of this entry. This will include
  //  * the current entry.
  //  *
  //  * @param       inDeep true for deep, false for from this only
  //  *
  //  * @return      an Iterator with all the sub entries
  //  *
  //  * @undefined   never
  //  *
  //  */
  // @SuppressWarnings("unchecked") // don't know why this is necessary
  // public List<Entry> getSubEntries(boolean inDeep)
  // {
  //   List<Entry> list = new ArrayList<Entry>();

  //   list.add(this);

  //   List<Entry> sublist = getEntrySubEntries();

  //   if(sublist != null)
  //     list.addAll(sublist);

  //   for(Iterator<AbstractAttachment> i = getAttachments(); i.hasNext(); )
  //   {
  //     sublist = i.next().getSubEntries(inDeep);

  //     if(sublist != null)
  //       list.addAll(sublist);
  //   }

  //   return list;
  // }

  // //........................................................................
  // //------------------------- getEntrySubEntries ---------------------------

  // /**
  //  * Get the sub entries that are part of this entry only (without
  //  * attachments).
  //  *
  //  * @return      an Iterator with all the sub entries
  //  *
  //  * @undefined   never
  //  *
  //  */
  // public List<Entry> getEntrySubEntries()
  // {
  //   return null;
  // }

  // //........................................................................
  // //----------------------------- getCategories ----------------------------

  // /**
  //  * Get the categories of the entry.
  //  *
  //  * @return      the categories
  //  *
  //  */
  // public List<String> getCategories()
  // {
  //   List<String> result = new ArrayList<String>();

  //   if(m_baseEntries != null)
  //     for(BaseEntry entry : m_baseEntries)
  //       for(String category : entry.getCategories())
  //         result.add(category);

  //   return result;
  // }

  // //........................................................................
  // //------------------------------ getSynonyms -----------------------------

  // /**
  //  * Get the synonyms of the entry.
  //  *
  //  * @return      the synonyms
  //  *
  //  * @undefined   never (a value is always returned)
  //  *
  //  */
  // public String []getSynonyms()
  // {
  //   if(m_base != null)
  //     return m_base.getSynonyms();

  //   return new String [0];
  // }

  // //........................................................................
  // //------------------------------- getValue -------------------------------

  // /**
  //  * Get the value for the given key. This will return the value stored in
  //  * the entry, but if this is not defined and we have a base, we will return
  //  * a value from there, if it exists.
  //  *
  //  * @param       inKey the name of the key to get the value for
  //  *
  //  * @return      the value for the key
  //  *
  //  * @undefined   return null if the given key is unknown or no value for
  //  *              it exists
  //  *
  //  */
  // public Value getValue(String inKey)
  // {
  //   Value entryValue = super.getValue(inKey);

  //   if(entryValue == null || m_base == null || entryValue.isDefined())
  //     return entryValue;

  //   Pair<ValueGroup, Variable> var = m_base.getVariable(inKey);

  //   if(var == null)
  //     return entryValue;

  //   return var.second().get(var.first());
  // }

  // //........................................................................
  // //------------------------------- getImage -------------------------------

  // /**
  //  * Get the name of the image for this entry.
  //  *
  //  * @return      a string with the full file name, including path
  //  *
  //  * @undefined   never
  //  *
  //  */
  // public String getImage()
  // {
  //   String file = getImage(Files.concatenate("/", s_dirFiles,
  //                                            getType().getMultipleDir(),
  //                                            getName()));

  //   if(file == null)
  //     file = getImage(Files.concatenate
  //                     ("/", s_dirFiles,
  //                      getType().getBaseType().getMultipleDir(), getName()));

  //   if(file == null)
  //     file = "unknown.png";

  //   return file;
  // }

  // /**
  //  * Get the image from a specific directory.
  //  *
  //  * @param     inDir the directory to look in for images
  //  *
  //  * @return    the best image found for this entry or null
  //  *
  //  * @undefined may return null
  //  * @undefined assertion if given dir is null
  //  *
  //  */
  // private String getImage(String inDir)
  // {
  //   assert inDir != null : "must have a directory here";

  //   List<String> files = Files.getResourceFiles(inDir);

  //   if(files == null || files.size() == 0)
  //     return null;

  //   // remove all the files to ignore
  //   for(Iterator<String> i = files.iterator(); i.hasNext(); )
  //   {
  //     String file = i.next();

  //     if(file.endsWith("CVS"))
  //       i.remove();

  //     if(file.indexOf("_thumbnail.") != -1)
  //       i.remove();
  //   }

  //   if(files.size() == 0)
  //     return null;

  //   return Strings.sort(files, "official", "cover", "main").get(0);
  // }

  // //........................................................................
  // //------------------------- getShortDescription --------------------------

  // /**
  //  * Get the short description of the base entry.
  //  *
  //  * @return      the selection containing the selected world
  //  *
  //  */
  // public String getShortDescription()
  // {
  //   List<String> result = getBaseValues(new Extractor<BaseEntry, String>()
  //                                       {
  //                                      public String get(BaseEntry inBase)
  //                                         {
  //                                       return inBase.getShortDescription();
  //                                         }
  //                                       }, COMBINER_LIST,
  //                                       new ArrayList<String>());

  //   return Strings.toString(result, " ", "* not found *");
  // }

  // //........................................................................
  // //---------------------------- getReferences -----------------------------

  // /**
  //  * Get all the references.
  //  *
  //  * @return      a list with all the references
  //  *
  //  */
  // public List<String> getReferences()
  // {
  //   return getBaseValues(new Extractor<BaseEntry, String []>()
  //                        {
  //                          public String []get(BaseEntry inBase)
  //                          {
  //                            return inBase.getReferences();
  //                          }
  //                        }, new Combiner<List<String>, String []>()
  //                        {
  //                          public List<String> combine
  //                            (List<String> ioOld, String []inNew)
  //                          {
  //                            for(String value : inNew)
  //                              ioOld.add(value);

  //                            return ioOld;
  //                          }
  //                        }, new ArrayList<String>());
  // }

  // //........................................................................
  // //------------------------------- getWorld -------------------------------

  // /**
  //  * Get the world the product is for.
  //  *
  //  * @return      the selection containing the selected world
  //  *
  //  */
  // public String getWorld()
  // {
  //   Selection world =
  //     getFirstDefinedBaseValue(new Extractor<BaseEntry, Selection>()
  //                              {
  //                                public Selection get(BaseEntry inBase)
  //                                {
  //                                  return inBase.m_world;
  //                                }
  //                              });

  //   if(world == null)
  //     return Value.UNDEFINED;

  //   return world.toString();
  // }

  // //........................................................................

  //--------------------------------- getID --------------------------------

  /**
   * Get the ID of the entry. This can mainly be used for reference purposes.
   *
   * @return      the requested id
   *
   */
  // public @Nonnull String getID()
  // {
  //   if(!m_id.isDefined())
  //     randomID();

  //   return m_id.get();
  // }

  //........................................................................
  //------------------------------- randomID -------------------------------

  /**
   * Set the id to a random value.
   *
   */
  public void randomID()
  {
    m_name = ID.random();
    changed(true);
  }

  //........................................................................

  // //------------------------------ getLookup -------------------------------

  // /**
  //  * Get the lookup for lookup searches. The internal lookup is cleared after
  //  * this method is called!
  //  *
  //  * @return      the lookup base entry or null if none
  //  *
  //  * @undefined   may return null
  //  *
  //  */
  // public BaseEntry getLookup()
  // {
  //   BaseEntry result = m_lookup;

  //   m_lookup = null;

  //   return result;
  // }

  // //........................................................................

  //-------------------------------- isBase --------------------------------

  /**
   * Check if the current entry represents a base entry or not.
   *
   * @return      true if this is a base entry, false else
   *
   */
  public boolean isBase()
  {
    return false;
  }

  //........................................................................
  // //----------------------------- isInCampaign -----------------------------

  // /**
  //  * Check if the entry has been added to a campaign.
  //  *
  //  * @return      true if in a campaign, false if not
  //  *
  //  * @undefined   never
  //  *
  //  */
  // public boolean isInCampaign()
  // {
  //   return m_campaign != null;
  // }

  // //........................................................................
  // //----------------------------- getCampaign ------------------------------

  // /**
  //  * Get the campaign this storage is in.
  //  *
  //  * @return      the Campaign for this storage
  //  *
  //  * @undefined   never (can return null if no campaign is associated)
  //  *
  //  */
  // public CampaignData getCampaign()
  // {
  //   return m_campaign;
  // }

  // //........................................................................
  // //----------------------------- hasCategory ------------------------------

  // /**
  //  * Check if the entry's base has a named category.
  //  *
  //  * @param       inCategory the category to look for
  //  *
  //  * @return      true if the category is present, false if not
  //  *
  //  * @undefined   never
  //  *
  //  */
  // public boolean hasCategory(String inCategory)
  // {
  //   if(m_baseEntries == null)
  //     return false;

  //   for(BaseEntry base : m_baseEntries)
  //     if(base != null && base.hasCategory(inCategory))
  //       return true;

  //   return false;
  // }

  // //........................................................................
  // //------------------------------- hasBase --------------------------------

  // /**
  //  * Check if the entry has the given base entry as a base.
  //  *
  //  * @param       inBase the base entry to look for
  //  *
  //  * @return      true if this entry is based on the given one, false if not
  //  *
  //  */
  // public boolean hasBase(BaseEntry inBase)
  // {
  //   if(m_baseEntries == null)
  //     return false;

  //   for(BaseEntry base : m_baseEntries)
  //     if(inBase == base)
  //       return true;

  //   return false;
  // }

  // //........................................................................

  // //----------------------------- printCommand -----------------------------

  // /**
  //  * Create the print command for printing this entry. This is an internal
  //  * method for more efficient creation. From the outside, use
  //  * <CODE>getPrintCommand()</CODE> instead.
  //  *
  //  * @param       inDM       true if set for DM, false for player
  //  * @param       inEditable true if values are editable, false if not
  //  *
  //  * @return      an object with the individual parts to set on the page
  //  *
  //  * @undefined   never
  //  *
  //  */
  // public PrintCommand printCommand(boolean inDM, boolean inEditable)
  // {
  //   PrintCommand commands = super.printCommand(inDM, inEditable);

  //   // now the attachments
  //   for(Iterator<AbstractAttachment> i = getAttachments(); i.hasNext(); )
  //     i.next().addPrintCommands(commands, inDM, inEditable);

  //   commands.post.add(new Script
  //                     ("gui.addAction('Save', "
  //                      + "function() { save('character', '"
  //                      + m_type.toString()
  //                      + "'); }, "
  //                      + "'Save all the changes made.', 'save', "
  //                      + "'hidden');\n"));

  //   return commands;
  // }

  // //........................................................................
  // //-------------------------- getSummaryCommand ---------------------------

  // /**
  //  * Get the command to set a summary of the entry.
  //  *
  //  * @param       inDM true if setting for dm, false if not
  //  *
  //  * @return      the command with the summary
  //  *
  //  */
  // public Command getSummaryCommand(boolean inDM)
  // {
  //   List<Object> commands = new ArrayList<Object>();

  //   summaryCommands(commands, inDM);

  //   return new Command(commands.toArray());
  // }

  // //........................................................................
  // //---------------------------- summaryCommand ----------------------------

  // /**
  //  * Add the summary commands to the given list.
  //  *
  //  * @param       ioCommands the list of commands to add to
  //  * @param       inDM       true if setting for DM, false if not
  //  *
  //  */
  // protected void summaryCommands(List<Object> ioCommands, boolean inDM)
  // {
  //   ioCommands.add(new Bold(new Link(m_name, "/entry/" + getType().getLink()
  //                                    + "/" + getID())));
  //   if(m_base != null)
  //     ioCommands.add(new Super("(" + getID() + ", "
  //                              + m_base.getReference(true) + ")"));
  //   else
  //     ioCommands.add(new Super("(" + getID() + ")"));

  //   // add value for all attachments
  //   for(AbstractAttachment<?> attachment : m_attachments.values())
  //     attachment.addSummaryCommands(ioCommands, inDM);
  // }

  // //........................................................................

  // //........................................................................

  //----------------------------------------------------------- manipulators

  // //----------------------------- setBaseName ------------------------------

  // /**
  //  * Set the name of the base entry this entry is based on.
  //  *
  //  * @param       inName the new base name
  //  *
  //  * @undefined   never
  //  *
  //  */
  // public void setBaseName(String inName)
  // {
  //   m_baseName.set(inName);
  // }

  // //........................................................................
  // //------------------------------- setBase --------------------------------

  // /**
  //  * Set the base entry this entry is based on.
  //  *
  //  * @param       inBase the entry based on
  //  *
  //  * @undefined   UnsupportedOperationException if no campaign is present
  //  *
  //  */
  // public void setBase(B inBase)
  // {
  //   if(m_campaign != null)
  //     throw new
  //       UnsupportedOperationException("cannot change the base entry of an "
  //                                     + "entry in a campaign");

  //   m_base = inBase;

  //   setBaseName(m_base.getName());
  // }

  // //........................................................................
  //-------------------------------- setID ---------------------------------

  /**
   * Set the id of the entry. Does not check if the id is unique.
   *
   * @param       inID the new id to set
   *
   * @return      true if id set, false if an error occurred
   *
   */
  @Deprecated
  public boolean setID(String inID)
  {
    m_name = m_name.as(inID);
    changed();

    return true;
  }

  //........................................................................
  // //---------------------------- addToCampaign -----------------------------

  // /**
  //  * Add the value group to the given campaign.
  //  *
  //  * @param       inCampaign the campaign to add it to
  //  *
  //  * @return      true if added, false if not (because already in one)
  //  *
  //  * @undefined   IllegalArgumentException if no campaign is given
  //  *
  //  */
  // public boolean addToCampaign(CampaignData inCampaign)
  // {
  //   if(inCampaign == null)
  //     throw new IllegalArgumentException("must have a campaign here");

  //   if(m_campaign == inCampaign)
  //     return true;

  //   // already in a campaign?
  //   if(m_campaign != null)
  //     return false;

  //   m_campaign = inCampaign;

  //   return true;
  // }

  // //........................................................................
  // //--------------------------- setTempCampaign ----------------------------

  // /**
  //  * Set the temporary campaign.
  //  *
  //  * @param       inCampaign the campaign to reference to.
  //  *
  //  */
  // public void setTempCampaign(CampaignData inCampaign)
  // {
  //   m_temporary = inCampaign;
  // }

  // //........................................................................
  // //------------------------- removeFromCampaign ---------------------------

  // /**
  //  * Remove the value group from the given campaign.
  //  *
  //  * @return      the campaign the entry was removed from (if any)
  //  *
  //  * @algorithm   set the campaign to null and return it
  //  *
  //  */
  // public CampaignData removeFromCampaign()
  // {
  //   CampaignData result = m_campaign;

  //   m_campaign = null;

  //   return result;
  // }

  // //........................................................................
  // //------------------------------- execute --------------------------------

  // /**
  //  * Execute the given action.
  //  *
  //  * @param       inAction the action to execute
  //  *
  //  * @return      true if executed and no more execution necessary, false if
  //  *            execute but either unsuccessfully or other instances need to
  //  *              execute as well.
  //  *
  //  * @undefined   IllegalArgumentException if no action given
  //  *
  //  */
  // public boolean execute(Action inAction)
  // {
  //   if(inAction == null)
  //     throw new IllegalArgumentException("must have an action here");

  //   // we don't know how to handle any actions here, thus let's ask if the
  //   // attachment know how to do that
  //   for(AbstractAttachment attachment : m_attachments.values())
  //     if(attachment.execute(inAction))
  //       return true;

  //   return false;
  // }

  // //........................................................................

  // //--------------------------- readQuantifiers ----------------------------

  // /**
  //  * Read an entry, and only the entry without type and comments, from the
  //  * reader.
  //  *
  //  * @param       inReader the reader to read from
  //  *
  //  * @return      true if read successfully, false else
  //  *
  //  * @undefined   IllegalArgumentException if given reader is null
  //  *
  //  */
  // @SuppressWarnings("unchecked")
  // protected boolean readQuantifiers(ParseReader inReader)
  // {
  //   assert inReader != null : "must have a reader here";

  //   // we read the lookup before the base entries because they use similar
  //   // markers
  //   if(inReader.expect(s_lookupStart) && m_baseType != null)
  //   {
  //  // we have to handle a lookup text, i.e. read a base entry to the given
  //     // entry type

  //     // create a lookup entry
  //     m_lookup = (B)m_baseType.create();

  //     // read a corresponding base entry
  //     m_lookup.readEntry(inReader);

  //     // if no end is found, we read the wrong thing
  //     if(!inReader.expect(s_lookupEnd))
  //     {
  //       inReader.logWarning(inReader.getPosition(), "parse.lookup",
  //                           "no end found");

  //       return false;
  //     }
  //   }

  //   super.readQuantifiers(inReader);

  //// we have to check if there is no base yet and we have to manually add one
  //   String name = getBaseName();

  //   if(m_baseEntries == null)
  //   {
  //     if(name == null)
  //       name = getName();

  //     if(name != null) // the name may still be null if lookup is desired
  //     {
  //       B base =
  //         (B)BaseCampaign.GLOBAL.getBaseEntry(name, getType().getBaseType());

  //       addBase(base, name);

  //       if(base == null)
  //         inReader.logWarning(inReader.getPosition(), "base.not-found",
  //                             "base name '" + name + "'");
  //     }
  //   }

  //   return true;
  // }

  // //........................................................................
  // //------------------------------- addBase --------------------------------

  // /**
// * Add a base to this entry. The entry is ignored if name and entry are null.
  //  *
  //  * @param       inBase the base entry to add
//  * @param       inName the name to add with (or null to use the name of the
  //  *                     given base entry, if any)
  //  *
  //  */
//protected void addBase(@MayBeNull BaseEntry inBase, @MayBeNull String inName)
  // {
  //   super.addBase(inBase, inName);

  //   if(inBase != null)
  //     addAttachments(inBase.getReqEntryAttachments());

  // }

  // //........................................................................

  // //------------------------------- complete -------------------------------

  // /**
  //  * Complete the entry and make sure that all values are filled.
  //  *
  //  * @undefined   never
  //  *
  //  */
  // @SuppressWarnings("unchecked") // Need to case BaseEntry to B
  // public void complete()
  // {
  //   // setup the default comments
  //   if(!m_leadingComment.isDefined())
  //  m_leadingComment.set("\n#----- " + getName() + " (" + getID() + ")\n\n");

  //   if(m_baseEntries == null)
  //   {
  //     B base =
  //       (B)BaseCampaign.GLOBAL.getBaseEntry
  //         (Encodings.toWordUpperCase(getName()), getType().getBaseType());

  //     if(base == null)
  //       Log.warning("could not find base(s) for '" + getName() + "'");
  //     else
  //     {
  //       m_baseEntries = new ArrayList<BaseEntry>();
  //       m_baseNames = new ArrayList<String>();

  //       m_baseEntries.add(base);
  //       m_baseNames.add(getName());
  //     }
  //   }

  //   super.complete();
  // }

  // //........................................................................
  // //-------------------------------- check ---------------------------------

  // /**
  //  * Check the entry for possible problems.
  //  *
  //  * @return      false if a problem was found, true if not
  //  *
  //  * @undefined   never
  //  *
  //  */
  // public boolean check()
  // {
  //   boolean result = true;

  //   // complete the attachments
  //   for(Iterator<AbstractAttachment> i = getAttachments(); i.hasNext(); )
  //     result &= i.next().check();

  //   // single & to make sure both are called
  //   return super.check() & result;
  // }

  // //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // /** The test.
  //  *
  //  * @hidden
  //  *
  //  */
  // public static class Test extends ValueGroup.Test
  // {
  //   //----- init -----------------------------------------------------------

  //   /** Testing init. */
  //   public void testInit()
  //   {
  //     m_logger.logClass(Entry.class);

  //     CampaignData campaign = new Campaign("Test", "tst", 0);

  //     Entry entry = new Entry("just a test", new Type(Entry.class),
  //                             new Type(BaseEntry.class));

  //     //System.out.println(entry);

  //     // undefined value
  //     assertEquals("name no correctly set", "just a test",
  //                  entry.getName().toString());
  //     assertEquals("type no correctly set", "entry",
  //                  entry.getType().toString());

  //     assertEquals("not correctly converted",
  //                  "entry just a test =\n\n"
  //                  + ".\n",
  //                  entry.toString());

  //     campaign.add(entry);

  //     assertEquals("not correctly converted",
  //                  "\n"
  //                  + "#----- just a test (tst-1)\n"
  //                  + "\n"
  //                  + "entry just a test =\n\n"
  //                  + "  id        tst-1.\n\n"
  //                  + "#.....\n",
  //                  entry.toString());

  //     m_logger.addExpected("INFO: changing id for 'just a test' "
  //                          + "from $undefined$ to tst-1");
  //     m_logger.addExpected("WARNING: could not find base(s) for "
  //                          + "'just a test'");
  // m_logger.addExpected("WARNING: could not find base(s) for 'just a test'");
  //   }

  //   //......................................................................
  //   //----- read -----------------------------------------------------------

  //   /** Testing reading. */
  //   public void testRead()
  //   {
  //     m_logger.logClass(Entry.class);

  //     ParseReader reader =
  //       new ParseReader(new java.io.StringReader
  //                       ("base entry guru = categories cat1, cat2.\n"
  //                        + "entry just a \\= test [guru] = \n"
  //                        + "  ."), "test");

  //     BaseEntry base = (BaseEntry)BaseEntry.read(reader);
  //     BaseCampaign.GLOBAL.add(base);

  //     Entry entry = (Entry)Entry.read(reader);

  //     //System.out.println(entry);
  //     assertNotNull("entry should have been read", entry);
  //     assertEquals("entry name does not match", "just a = test",
  //                  entry.getName());
  //     assertEquals("entry does not match",
  //                  "entry just a \\= test [guru] =\n\n"
  //                  + ".\n",
  //                  entry.toString());

  //     Campaign campaign = new Campaign("Test", "tst", 0);
  //     campaign.add(entry);

  //     assertEquals("base", base, entry.getBase());
  //     assertEquals("entry does not match",
  //                  "\n"
  //                  + "#----- just a = test (tst-1)\n"
  //                  + "\n"
  //                  + "entry just a \\= test [guru] =\n\n"
  //                  + "  id        tst-1.\n\n"
  //                  + "#.....\n",
  //                  entry.toString());

  //     assertEquals("category", "Cat1", entry.getCategories().get(0));
  //     assertEquals("category", "Cat2", entry.getCategories().get(1));
  //     assertTrue("category", entry.hasCategory("cat1"));
  //     assertTrue("category", entry.hasCategory("cat2"));
  //     assertFalse("category", entry.hasCategory("cat3"));

  //     BaseCampaign.GLOBAL.m_bases.clear();
  //     m_logger.addExpected("INFO: changing id for 'just a = test' "
  //                          + "from $undefined$ to tst-1");
  //   }

  //   //......................................................................
  //   //----- print ----------------------------------------------------------

  //   /** Testing printing. */
  //   public void testPrint()
  //   {
  //     BaseEntry base = new BaseEntry("test");

  //     base.setWorld("Forgotten Realms");
  //     base.addCategory("cat1");

  //     Entry<BaseEntry> entry =
  //       new Entry<BaseEntry>("test", Entry.TYPE, Entry.BASE_TYPE, base);

  //     PrintCommand command = entry.printCommand(true, false);

  //     assertNotNull("command", command);

  //     // icons
  //     Command icons =
  //    entry.getCommand(command, new Command("#{^world, ^categories}"), true);

  //   assertEquals("world", "world: Forgotten Realms", extract(icons, 1, 2));
  //     assertEquals("category", "categories: cat1", extract(icons, 2, 2));

  //     // values
  //     Command values =
  //       entry.getCommand(command,
  //                        new Command("%{base, +categories, file, synonyms, "
  //                                    + "references}"),
  //                        true);

  //     assertEquals("base", "Base:", extract(values, 1, 1, 1));
  //     assertEquals("base", "/entry/baseentry/test",
  //                  extract(values, 2, 1, 1, -1));
  //     assertEquals("base", "test", extract(values, 2, 1, 1, 1));

  //     assertEquals("category", "Categories:", extract(values, 3, 1, 1));
  //     assertEquals("category", "cat1", extract(values, 4));

  //     assertEquals("file", "File:", extract(values, 5, 1, 1));
  //     assertEquals("file", "_file", extract(values, 6, 1, 3));
  //     assertEquals("file", "<please select>", extract(values, 6, 1, 4));

  //     assertEquals("synoyms", "Synonyms:", extract(values, 7, 1, 1));
  //     assertEquals("synonyms", "", extract(values, 8));

  //     assertEquals("synoyms", "References:", extract(values, 9, 1, 1));
  //     assertEquals("references", "", extract(values, 10));
  //   }

  //   //......................................................................
  // }

  //........................................................................
}
