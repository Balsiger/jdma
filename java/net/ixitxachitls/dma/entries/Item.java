/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Lists;

import net.ixitxachitls.dma.entries.extensions.Composite;
import net.ixitxachitls.dma.entries.extensions.Contents;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.FormattedText;
import net.ixitxachitls.dma.values.Money;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.Weight;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a real item.
 *
 * @file          Item.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class Item extends CampaignEntry<BaseItem>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Item --------------------------------

  /**
   * This is the internal, default constructor.
   *
   */
  protected Item()
  {
    super(TYPE, BASE_TYPE);
  }

  //........................................................................
  //------------------------------- Item --------------------------------

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base item and the item
   *
   */
  public Item(String inName)
  {
    super(inName, TYPE, BASE_TYPE);
  }

  //........................................................................
  //------------------------------- Item --------------------------------

  /**
   * This constructs the item with random values from the given
   * base item.
   *
   * @param       inCampaign  the campaign this entry is in
   * @param       inBases     the base items to take values from
   *
   * @undefined   never
   *
   */
  public Item(Campaign inCampaign, String ... inBases)
  {
    super(TYPE, BASE_TYPE, inCampaign, inBases);
  }

  //........................................................................
  //------------------------------- Item --------------------------------

  /**
   * This constructs the item with random values from the given
   * base item.
   *
   * @param       inName  the name of the item
   * @param       inBases the base items to take values from
   *
   * @undefined   never
   *
   */
  // public Item(String inName, BaseItem ... inBases)
  // {
  //   super(inName, TYPE, BASE_TYPE, inBases);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final Type<Item> TYPE =
    new Type<Item>(Item.class, BaseItem.TYPE);

  /** The type of the base entry to this entry. */
  public static final BaseType<BaseItem> BASE_TYPE = BaseItem.TYPE;

  //----- hp ---------------------------------------------------------------

  /** The actual number of hit points the item currently has. */
  @Key("hp")
  protected Number m_hp = new Number(0, 10000);

  //........................................................................
  //----- value ------------------------------------------------------------

  /** The total value of the item. */
  @Key("value")
  @DM
  protected Money m_value = new Money();

  //........................................................................
  //----- appearance -------------------------------------------------------

  /** The appearance text for this entry. */
  @Key("appearance")
  protected FormattedText m_appearance = new FormattedText();

  //...........................................................................
  //----- player notes --------------------------------------------------

  /** The player notes of the item. */
  @Key("player notes")
  protected Text m_playerNotes = new Text();

  //........................................................................
  //----- player name ------------------------------------------------------

  /** The name from the player for the item. */
  @Key("player name")
  @PlayerEdit
  protected Text m_playerName = new Text();

  //........................................................................
  //----- dm notes ---------------------------------------------------------

  /** The DM notes of the item. */
  @Key("dm notes")
  @DM
  protected Text m_dmNotes = new Text();

  //........................................................................

  static
  {
    extractVariables(Item.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------------- getHP --------------------------------

  /**
   * Get the hp of the item.
   *
   * @return      the hp
   *
   */
  // public long getHP()
  // {
  //   return m_hp.getLow().get();
  // }

  //........................................................................
  //------------------------------- getMaxHP -------------------------------

  /**
   * Get the max hp of the item.
   *
   * @return      the max hp
   *
   */
  // public long getMaxHP()
  // {
  //   return m_maxHP.getLow().get();
  // }

  //........................................................................
  //------------------------------- getWeight ------------------------------

  /**
   * Get the total weight of the item.
   *
   * @return      the weight
   *
   */
  public Weight getTotalWeight()
  {
    Combined<Weight> value = collect("weight");
    return value.total();
  }

  //........................................................................
  //----------------------------- getGoldValue -----------------------------

  /**
   * Get the value of the item in gold piece and their fraction (e.g. silver is
   * 0.1).
   *
   * @return      the value
   *
   */
  public double getGoldValue()
  {
    return getValue().getAsGold().getValue();
  }

  //........................................................................
  //------------------------------- getValue -------------------------------

  /**
   * Get the value of the item.
   *
   * @return      the value
   *
   */
  public Money getValue()
  {
    Combined<Money> value = collect("value");
    return value.total();
  }

  //........................................................................
  //------------------------------- getName --------------------------------

  /**
   * Get the name of the entry. This time, also check for special quality
   * modifiers.
   *
   * @return      the requested name
   *
   */
  // public String getName()
  // {
  //   String qualities = null;
  //   if(m_qualities.isDefined())
  //     for(Iterator<EntryValue<Quality>> i = m_qualities.iterator();
  //         i.hasNext(); )
  //     {
  //       Quality quality = i.next().get();
  //       String qualifier = quality.getQualifier();

  //       if(qualifier != null)
  //         if(qualities == null)
  //           qualities = qualifier;
  //         else
  //           qualities += " " + qualifier;
  //     }

  //   if(qualities == null)
  //     return super.getName();
  //   else
  //     return qualities + " " + super.getName();
  // }

  //........................................................................
  //------------------------------- getSize --------------------------------

  /**
   * Get the size of the item.
   *
   * @return      the size or null if not defined
   *
   */
  // public BaseItem.Size getSize()
  // {
  //   return getBaseValue(new Extractor<BaseItem, BaseItem.Size>()
  //                       {
  //                         public BaseItem.Size get(BaseItem inBase)
  //                         {
  //                           return inBase.getSize();
  //                         }
  //                       }, new Combiner<BaseItem.Size, BaseItem.Size>()
  //                       {
  //                         public BaseItem.Size combine(BaseItem.Size inOld,
  //                                                      BaseItem.Size inNew)
  //                         {
  //                           if(inNew.ordinal() > inOld.ordinal())
  //                             return inNew;

  //                           return inOld;
  //                         }
  //                       });
  // }

  //........................................................................
  //----------------------------- getBreakDC -------------------------------

  /**
   * Get the break DC of the item.
   *
   * @return      the break dc
   *
   */
  // public long getBreakDC()
  // {
  //   return getBaseValue(new Extractor<BaseItem, Long>()
  //                       {
  //                         public Long get(BaseItem inBase)
  //                         {
  //                           return inBase.m_break.get();
  //                         }
  //                       }, new Combiner<Long, Long>()
  //                       {
  //                         public Long combine(Long inOld, Long inNew)
  //                         {
  //                           if(inNew > inOld)
  //                             return inNew;

  //                           return inOld;
  //                         }
  //                       });
  // }

  //........................................................................
  //---------------------------- getAppearance ----------------------------

  /**
   * Get the appearance of the item.
   *
   * @return      the requested appearance
   *
   */
  public String getAppearance()
  {
    return m_appearance.get();
  }

  //........................................................................
  //---------------------------- getPlayerNotes ----------------------------

  /**
   * Get the player notes of the item.
   *
   * @return      the requested notes
   *
   */
  public String getPlayerNotes()
  {
    return m_playerNotes.get();
  }

  //........................................................................
  //------------------------------ getDMNotes ------------------------------

  /**
   * Get the dm notes of the item.
   *
   * @return      the requested notes
   *
   */
  public String getDMNotes()
  {
    return m_dmNotes.get();
  }

  //........................................................................
  //---------------------------- getPlayerName -----------------------------

  /**
   * Get the name of the entry as given to the plaer.
   *
   * @return      the requested name
   *
   */
  @Override
  public String getPlayerName()
  {
    if(m_playerName.isDefined())
      return m_playerName.get();

    for(BaseEntry base : getBaseEntries())
      if(base != null)
        return base.getName();

    return getName();
  }

  //........................................................................
  //------------------------------ getDMName -------------------------------

  /**
   * Get the name of the item for DMs.
   *
   * @return the dm specific name
   *
   */
  @Override
  public String getDMName()
  {
    List<String> parts = new ArrayList<String>();

    for(BaseEntry base : getBaseEntries())
    {
      if(base == null)
        continue;

      String name = base.getName();
      List<String> synonyms = base.getSynonyms();
      // if the first synonym does not contain a ',', we use that name as it
      // might be better readable than the restricted real name
      if(!synonyms.isEmpty() && synonyms.get(0).indexOf(',') < 0)
        name = synonyms.get(0);

      parts.add(name);
    }

    if(parts.isEmpty())
      parts.add(getName());

    return Strings.SPACE_JOINER.join(parts);
  }

  //........................................................................
  //--------------------------- containedItems -----------------------------

  /**
   * Get all the items contained in this one.
   *
   * @param       inDeep true for returning all item, including nested ones,
   *                     false for only the top level items
   * @return      a list of all contained items
   *
   */
  public Map<String, Item> containedItems(boolean inDeep)
  {
    Map<String, Item> items = new HashMap<String, Item>();

    Contents contents = (Contents)getExtension("contents");
    if(contents != null)
    {
      Map<String, Item> contained = contents.containedItems(inDeep);
      for(String key : contained.keySet())
        if(items.containsKey(key))
          Log.warning("item loop detected for " + key);

      items.putAll(contents.containedItems(inDeep));
    }

    Composite composite = (Composite)getExtension("composite");
    if(composite != null)
    {
      Map<String, Item> contained = composite.containedItems(inDeep);
      for(String key : contained.keySet())
        if(items.containsKey(key))
          Log.warning("item loop detected for " + key);

      items.putAll(composite.containedItems(inDeep));
    }

    return items;
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
  public @Nullable Object compute(String inKey)
  {
    if("dmName".equals(inKey))
      return getDMName();

    if("playerName".equals(inKey))
      return getPlayerName();

    return super.compute(inKey);
  }

  //........................................................................

  //------------------------------ printCommand ----------------------------

  /**
   * Print the item to the document, in the general section.
   *
   * @param       inDM       true if set for DM, false for player
   * @param       inEditable true if values are editable, false if not
   *
   * @return      the command representing this item in a list
   *
   */
  // public PrintCommand printCommand(final boolean inDM, boolean inEditable)
  // {
  //   final PrintCommand commands = super.printCommand(inDM, inEditable);

  //   commands.type = "item";

  //   commands.addValue("player title",
  //                     new Title
  //                     (inEditable
  //                      ? new Editable(getID(), getPlayerName(),
  //                                     "player name", getPlayerName(),
  //                                     "string")
  //                      : getPlayerName(), "title",
  //                               new Link(getType(),
  //                                        "/index/"
  //                                        + getType().getMultipleLink())),
  //                     false, false, true, "player titles");
  //   commands.addValue("player name",
  //                     new Bold(new Color
  //                              ("subtitle",
  //                               inEditable
  //                               ? new Editable(getID(), getPlayerName(),
  //                                             "player name", getPlayerName(),
  //                                              "string")
  //                               : getPlayerName())),
  //                     false, false, true, "player names");
  //   commands.addValue("id", new Super(new Scriptsize("(" + m_id + ")")),
  //                     false, false, false, "ids");
  //   commands.reFlag("title", null, true, null);
  //   commands.appendValue("name", " (" + getPlayerName() + ")");

  //   commands.temp = new ArrayList<Object>();
  //   commands.temp.add(PAGE_COMMAND.transform(new ValueTransformer(commands,
  //                                                                 inDM)));

  //   return commands;
  // }

  //........................................................................
  //--------------------------- shortPrintCommand --------------------------

  /**
   * Print the item to the document in a short way.
   *
   * @param       inDM   true if set for DM, false for player
   *
   * @return      the command representing this item in a list
   *
   */
  // public PrintCommand shortPrintCommand(boolean inDM)
  // {
  //   PrintCommand commands = super.shortPrintCommand(inDM);

  //   commands.shortHeader.clear();
  //   commands.pre.clear();
  //   commands.temp =
  //     commands.asPageCommands(inDM, "title, player title",
  //                             "",
  //                             "base, player name, player notes, dm notes, "
  //                             + "value, weight, count, +unit, "
  //                             + "+references");

  //     return commands;
  // }

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
  // public ListCommand getListCommands(boolean inDM)
  // {
  //   ListCommand commands = super.getListCommands(inDM);

  //   List<Object> general = commands.getList(ListCommand.Type.GENERAL);

  //   return commands;
  // }

  //........................................................................
  //--------------------------------- asJS ---------------------------------

  /**
   * Get the command use to set the item as an icon.
   *
   * @param       inDM true if setting for dm, false if not
   *
   * @return      the command to set the item as an icon
   *
   * @undefined   never
   *
   */
  // public String asJS(boolean inDM)
  // {
  //   StringBuilder builder = new StringBuilder();

  //   boolean first = true;
  //   for(Entry entry : getSubEntries(true))
  //   {
  //     if(entry == this)
  //       continue;

  //     if(entry instanceof Item)
  //     {
  //       if(!first)
  //         builder.append(", ");

  //       builder.append(((Item)entry).asJS(inDM));

  //       first = false;
  //     }
  //   }

  //   String name = null;

  //   if(inDM)
  //     name = getName();
  //   else
  //     name = getPlayerName();

  //   String type;
  //   if(hasAttachment(Contents.class))
  //     type = "Container";
  //   else
  //     type = "Item";

  //   return "new " + type + "("
  //     + Encodings.toJSString(getID()) + ", "
  //     + Encodings.toJSString(name) + ", "
  //     + Encodings.toJSString("/images/" + getID()) + ","
  //     + Encodings.toJSString(m_weight.toString())
  //     + ", [" + builder.toString() + "])";
  // }

  //........................................................................
  //---------------------------- summaryCommand ----------------------------

  /**
   * Add the summary commands to the given list.
   *
   * @param       ioCommands the list of commands to add to
   * @param       inDM       true if setting for DM, false if not
   *
   */
  // protected void summaryCommands(List<Object> ioCommands, boolean inDM)
  // {
  //   super.summaryCommands(ioCommands, inDM);

  //   ioCommands.add(", ");
  //   ioCommands.add(m_value.format(false));
  //   ioCommands.add(", ");
  //   ioCommands.add(m_weight.format(false));
  //   ioCommands.add(", ");
  //   ioCommands.add(m_hp.format(false));
  //   ioCommands.add("/");
  //   ioCommands.add(m_maxHP.format(false));
  //   ioCommands.add(" hp");

  //   if(m_hardness.isDefined())
  //   {
  //     ioCommands.add(", hardness ");
  //     ioCommands.add(m_hardness.format(false));
  //   }

  //   long breakDC = getBreakDC();
  //   if(breakDC > 0)
  //   {
  //     ioCommands.add(", break dc ");
  //     ioCommands.add(breakDC);
  //   }

  //   ioCommands.add(", ");
  //   ioCommands.add(getSize());

  //   ioCommands.add(new Linebreak());
  //   ioCommands.add(m_description.format(false));

  //   ioCommands.add(new Linebreak());
  //   ioCommands.add(new Emph(getShortDescription()));

  //   if(m_playerNotes.isDefined())
  //   {
  //     ioCommands.add(new Color("#004400", "Player:"));
  //     ioCommands.add(m_playerNotes.format(false));
  //   }

  //   if(m_dmNotes.isDefined())
  //   {
  //     ioCommands.add(new Color("#440000", "DM:"));
  //     ioCommands.add(m_dmNotes.format(false));
  //   }

  //   ioCommands.add(new Par());
  // }

  // //........................................................................
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
  // @Override
  // public @Nullable ValueHandle<?> computeValue(String inKey, boolean inDM)
  // {
  //   // if("name".equals(inKey))
  //   // {
  //   //   return new FormattedValue
  // //     (new Command(getNameCommand(inDM), " (", getName(), ")"), getName(),
  //   //      "name")
  //   //     .withEditable(true)
  //   //     .withEditType("name");
  //   // }

  //   if("itemlist".equals(inKey))
  //   {
  //     List<Object> item = new ArrayList<Object>();
  //     item.add(new Small(new Bold(computeValue("name", inDM)
  //                                 .format(this, inDM, true))));
  //     item.add(new Scriptsize(computeValue("weight", inDM)
  //                             .format(this, inDM, true)));
  //     item.add(new Scriptsize(new Command(computeValue("appearance", inDM)
  //                                         .format(this, inDM, true),
  //                                         computeValue("player notes", inDM)
  //                                         .format(this, inDM, true))));

  //     if(inDM)
  //     {
  //       item.add(new Scriptsize(computeValue("value", inDM)
  //                               .format(this, inDM, true)));

  //       List<Object> values = new ArrayList<Object>();
  //       maybeAddValue(values, "size", inDM, null, ";");
  //       maybeAddValue(values, "hp", inDM, " ", ";");
  //       maybeAddValue(values, "break DC", inDM, " break DC", ";");
  //       maybeAddValue(values, "counted:summary", inDM, " ", ";");
  //       maybeAddValue(values, "wearable:summary", inDM, " ", ";");
  //       maybeAddValue(values, "timed:summary", inDM, " ", ";");
  //       maybeAddValue(values, "light:summary", inDM, " ", ";");
  //       maybeAddValue(values, "weapon:summary", inDM, " ", ";");
  //       maybeAddValue(values, "armor:summary", inDM, " ", ";");
  //       maybeAddValue(values, "incomplete:summary", inDM, " ", ";");

  //       item.add(new Scriptsize
  //                (new Command(new Color("subtitle",
  //                                     computeValue("short description", true)
  //                                       .format(this, true, true)),
  //                             " ",
  //                             new Color("dm-notes",
  //                                       computeValue("dm notes", true)
  //                                       .format(this, true, true)),
  //                             new Linebreak(),
  //                             new Command(values))));
  //     }

  //     Command command = new Table("keep", "85:L;15:R", new Command(item));

  //     List<Object> nested = new ArrayList<Object>();
  //     for(Map.Entry<String, Item> entry : containedItems(false).entrySet())
  //     {
  //       if(entry.getValue() == null)
  //         nested.add(entry.getKey());
  //       else
  //         nested.add(entry.getValue().computeValue("itemlist", inDM)
  //                    .format(entry.getValue(), inDM, true));
  //     }

  //     if(!nested.isEmpty())
  //       command = new Command(command, new Indent(new Command(nested)));

  //     return new FormattedValue(command, null, "itemlist");
  //   }

  //   return super.computeValue(inKey, inDM);
  // }

  //........................................................................
  //---------------------------- computeFullName ---------------------------

  /**
   * Get a command to format the name of the item.
   *
   * @return   the command to format the name
   */
  public String computeFullName()
  {
    List<String> names = Lists.newArrayList();
    for(BaseEntry base : getBaseEntries())
    {
      if(base == null)
        continue;

      List<String> synonyms = base.getSynonyms();
      if(!synonyms.isEmpty() && synonyms.get(0).indexOf(',') < 0)
        names.add(synonyms.get(0));
      else
        names.add(base.getName());
    }

    String name;
    if(names.isEmpty())
      name = getName();
    else
      name = Strings.SPACE_JOINER.join(names);

    if (m_playerName.isDefined() && !name.equals(m_playerName.get()))
      return m_playerName.get() + " (" + name + ")";

    return name;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- setHP --------------------------------

  /**
   * Set the hp of the item.
   *
   * @param       inHP the hp to set to
   *
   * @return      true if set, false if not
   *
   * @undefined   never
   *
   */
  // public boolean setHP(long inHP)
  // {
  //   if(inHP < 0 || inHP > getMaxHP())
  //     return false;

  //   m_hp.reset();
  //   m_hp.setBaseValue(new Number(inHP, 0, 100000));

  //   return true;
  // }

  //........................................................................
  //------------------------------- setValue -------------------------------

  /**
   * Set the value of the item.
   *
   * @param       inPlatinum the platinum value of the item
   * @param       inGold     the gold piece value
   * @param       inSilver   the silver piece value
   * @param       inCopper   the copper piece value
   *
   * @return      true for successful set, false else
   *
   * @undefined   assertion if null given
   *
   */
  // public boolean setValue(int inPlatinum, int inGold, int inSilver,
  //                         int inCopper)
  // {
  //   Rational platinum = inPlatinum != 0 ? new Rational(inPlatinum) : null;
  //   Rational gold     = inGold     != 0 ? new Rational(inGold)     : null;
  //   Rational silver   = inSilver   != 0 ? new Rational(inSilver)   : null;
  //   Rational copper   = inCopper   != 0 ? new Rational(inCopper)   : null;

  //   m_value.reset();
  // return m_value.getBaseValue().setStandard(platinum, gold, silver, copper);
  // }

  //........................................................................
  //------------------------------ setWeight -------------------------------

  /**
   * Set the weight of the item.
   *
   * @param       inPounds the weight in pounds
   *
   * @return      true for successful set, false else
   *
   * @undefined   assertion if null given
   *
   */
  // public boolean setWeight(Rational inPounds)
  // {
  //   if(inPounds == null)
  //     return false;

  //   return m_weight.getBaseValue().setPounds(inPounds, null);
  // }

  //........................................................................
  //---------------------------- setDescription ----------------------------

  /**
   * Set the description of the item.
   *
   * @param       inDescription the new text
   *
   * @return      true if set, false if not
   *
   * @undefined   never
   *
   */
  // public boolean setDescription(String inDescription)
  // {
  //   if(inDescription == null)
  //     return false;

  //   m_description.set(inDescription);

  //   return true;
  // }

  //........................................................................
  //---------------------------- setPlayerNotes ----------------------------

  /**
   * Set the player notes of the item.
   *
   * @param       inNotes the new text
   *
   * @return      true if set, false if not
   *
   * @undefined   never
   *
   */
  // public boolean setPlayerNotes(String inNotes)
  // {
  //   if(inNotes == null)
  //     return false;

  //   m_playerNotes.set(inNotes);

  //   return true;
  // }

  //........................................................................
  //----------------------------- setPlayerName ----------------------------

  /**
   * Set the player name of the item.
   *
   * @param       inName the new name to set to
   *
   * @return      true if set, false if not
   *
   * @undefined   never
   *
   */
  // public boolean setPlayerName(String inName)
  // {
  //   if(inName == null)
  //     return false;

  //   m_playerName.set(inName);

  //   return true;
  // }

  //........................................................................
  //------------------------------ setDMNotes ------------------------------

  /**
   * Set the dm notes of the item.
   *
   * @param       inNotes the new text
   *
   * @return      true if set, false if not
   *
   * @undefined   never
   *
   */
  // public boolean setDMNotes(String inNotes)
  // {
  //   if(inNotes == null)
  //     return false;

  //   m_dmNotes.set(inNotes);

  //   return true;
  // }

  //........................................................................
  //----------------------------- setAppearance ----------------------------

  /**
   * Set the appearance of the item.
   *
   * @param       inText the appearance
   *
   * @return      true if set, false if not
   *
   * @undefined   never
   *
   */
  // public boolean setAppearance(String inText)
  // {
  //   if(inText == null)
  //     return false;

  //   m_appearance.set(inText);

  //   return true;
  // }

  //........................................................................

  //---------------------------- addMaxHPModifier --------------------------

  /**
   * Add a modifier to the maximal hp of the item.
   *
   * @param       inModifier the modifier to add
   *
   */
  // public void addMaxHPModifier(BaseModifier inModifier)
  // {
  //   if(inModifier == null)
  //     return;

  //   m_maxHP.addModifier(inModifier);
  // }

  //........................................................................
  //----------------------------- addHPModifier ----------------------------

  /**
   * Add a modifier to the maximal hp of the item.
   *
   * @param       inModifier the modifier to add
   *
   */
  // public void addHPModifier(BaseModifier inModifier)
  // {
  //   if(inModifier == null)
  //     return;

  //   m_hp.addModifier(inModifier);
  // }

  //........................................................................
  //--------------------------- addValueModifier ---------------------------

  /**
   * Add a modifier to the value of the item.
   *
   * @param       inModifier the modifier to add
   *
   */
  // public void addValueModifier(BaseModifier inModifier)
  // {
  //   if(inModifier == null)
  //     return;

  //   m_value.addModifier(inModifier);
  // }

  //........................................................................
  //--------------------------- addWeightModifier --------------------------

  /**
   * Add a modifier to the weight of the item.
   *
   * @param       inModifier the modifier to add
   *
   */
  // public void addWeightModifier(BaseModifier inModifier)
  // {
  //   if(inModifier == null)
  //     return;

  //   m_weight.addModifier(inModifier);
  // }

  //........................................................................
  //------------------------------ addQuality ------------------------------

  /**
   * Add a quality by name to the item.
   *
   * @param       inName the name of quality
   *
   * @return      true if added, false if not
   *
   * @undefined   never
   *
   */
  // public boolean addQuality(String inName)
  // {
  //   if(inName == null)
  //     return false;

  //   return addQuality(new Quality(inName));
  // }

  //........................................................................
  //------------------------------ addQuality ------------------------------

  /**
   * Add a quality to the item.
   *
   * @param       inQuality the quality to add
   *
   * @return      true if added, false if not
   *
   * @undefined   never
   *
   */
  // public boolean addQuality(Quality inQuality)
  // {
  //   if(inQuality == null)
  //     return false;

  //   m_qualities.add(new EntryValue<Quality>(inQuality));
  //   inQuality.complete();

  //   return true;
  // }

  //........................................................................

  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled. We do only
   * the value and the appearance here and let the base class handle the rest.
   *
   */
  public void complete()
  {
    super.complete();

    if(!m_hp.isDefined())
    {
      Combined<Number> combinedHp = collect("hp");
      Number total = combinedHp.total();
      if(total != null)
      {
        m_hp = m_hp.as(total.get());
        changed();
      }
    }

    // appearane
    if(!m_appearance.isDefined())
    {
      changed();

      // correct the random value with the computation from the value in
      // relation to the base value
      Combined<Money> combinedValue = collect("value");
      Money total = combinedValue.total();
      double itemValue = getGoldValue();
      double baseValue = m_value.isDefined() || total == null ? itemValue
        : total.getAsGold().getValue();

      // We have to try to get the value from our bases.
      List<String> appearances = new ArrayList<String>();
      for(BaseEntry base : getBaseEntries())
      {
        if(base == null)
          continue;

        String appearance =
          ((BaseItem)base).getRandomAppearance(itemValue / baseValue);

        if(appearance != null)
          appearances.add(appearance);
      }

      m_appearance = m_appearance.as(Strings.toString(appearances, " ", ""));
    }
//     //----- qualities ------------------------------------------------------

//     if(!m_qualities.isDefined())
//       for(BaseEntry base : m_baseEntries)
//         if(base != null)
//           for(SimpleText value : ((BaseItem)base).m_qualities)
//             addQuality(value.get());

//     // finally, complete all the qualities
//     for(EntryValue<Quality> value : m_qualities)
//     {
//       Quality quality = value.get();

//       // complete the skill with this monster
//       quality.complete();
//     }

//     //......................................................................

//   we have to adjust some values that might have been changed by attachments

//     //----- hp -------------------------------------------------------------

//     if(!m_hp.isDefined() || getHP() > getMaxHP())
//       m_hp.setBaseValue(m_maxHP.getBaseValue());

//     //......................................................................

//     // TODO: check if we still need this and how to adapt it
//     // now we might have to replace some value dependent patterns (we do that
//     // after the super.complete() to make sure that all attachment and base
//     // values are completed
//     // String text = m_description.get();

//     // Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
//     // Matcher matcher = pattern.matcher(text);

//     // StringBuffer replaced = new StringBuffer();

//     // while(matcher.find())
//     // {
//     // Pair<ValueGroup, Variable> var = getVariable(matcher.group(1));

//     // if(var == null)
//     // matcher.appendReplacement(replaced, "\\\\color{error}{*no value*}");
//     // else
//     // {
//     // Variable   variable = var.second();
//     // ValueGroup entry    = var.first();

//     // TODO: this has to change
//     // if(variable.hasValue(entry))
//     // matcher.appendReplacement
//     // (replaced,
//     // Matcher.quoteReplacement
//     // (variable.asModifiedCommand
//     // (null, /*m_file.getCampaign(), */this, entry,
//     // Value.Convert.PRINT).statify(null, /*m_storage.getCampaign(), */
//     // this, entry, null,
//     // Value.Convert.PRINT).toString()));
//     // else
//     // matcher.appendReplacement(replaced,
//     // "\\\\color{error}{\\$undefined\\$}");
//     // }
//     // }

//     // matcher.appendTail(replaced);

//     // m_description.set(replaced.toString());
  }

  //......................................................................
  //------------------------------- identify -------------------------------

  /**
   * Identify the item by filling out the player name (and maybe notes?).
   *
   */
  public void identify()
  {
    m_playerName = m_playerName.as(computeFullName());
  }

  //........................................................................
  //----------------------------- modifyValue ------------------------------

  /**
    * Modify the given value with information from the current attachment.
    *
    * @param       inType    the type of value to modify
    * @param       inValue   the value to modify, return in this object
    * @param       inDynamic a flag denoting if dynamic values are requested
    *
    * @return      the newly computed value (or null if no value to use)
    *
    * @undefined   never
    *
    */
  // TODO: remove this
//   public Modifier modifyValue(PropertyKey inType, Value inValue,
//                               boolean inDynamic)
//   {
//     if(inValue == null || !inValue.isDefined() || !m_userSize.isDefined())
//       return null;

//     BaseItem.Size size = m_userSize.getSelected();

//     // nothing to do for medium sized weapons
//     if(size == BaseItem.Size.MEDIUM)
//       return null;

//     // if the user size is different from the standard size, we adjust
//     // the real size of the object in the same amount
//     if(inType == PropertyKey.getKey("size"))
//       // larger than medium
//       if(size.isBigger(BaseItem.Size.MEDIUM))
//         return new Modifier(Modifier.Type.MULTIPLY,
//                             size.difference(BaseItem.Size.MEDIUM));
//       else
//         // smaller than medium
//         return new Modifier(Modifier.Type.DIVIDE,
//                             BaseItem.Size.MEDIUM.difference(size));

//     // this is only an adjustment of the BASE HP, not one of the item!
//     if(inType == PropertyKey.getKey("weight")
//        || inType == PropertyKey.getKey("hp"))
//     {
//       if(inType == PropertyKey.getKey("hp"))
//         if(((Number)inValue).get() == 1)
//           return null;

//       // a small weapon is half as heavy, a large twice that (i.e. 2^x), the
//       // same for hit points (cf. Player's Handbook p. 117, p. 158)
//       if(size.isBigger(BaseItem.Size.MEDIUM))
//         return new Modifier
//           (Modifier.Type.MULTIPLY,
//            (int)Math.pow(2, size.difference(BaseItem.Size.MEDIUM)));
//       else
//         return new Modifier
//           (Modifier.Type.DIVIDE,
//            (int)Math.pow(2, BaseItem.Size.MEDIUM.difference(size)));
//     }

//     return super.modifyValue(inType, inValue, inDynamic);
//   }

  //........................................................................
  //------------------------------- execute --------------------------------

  /**
   * Execute the given action.
   *
   * @param       inAction the action to execute
   *
   * @return      true if executed and no more execution necessary, false if
   *              execute but either unsuccessfully or other instances need to
   *              execute as well.
   *
   * @undefined   IllegalArgumentException if no action given
   *
   */
  // public boolean execute(Action inAction)
  // {
  //   // if the super class can handle it, let it
  //   if(super.execute(inAction))
  //     return true;

  //   // do we have to move this item?
  //   if(inAction instanceof Move)
  //   {
  //     // remove the item from the current storage space
  //     return true;
  //   }

  //   return false;
  // }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends ValueGroup.Test
  {
    /** Storage to save the old, real random object. */
    //private java.util.Random m_random;

    /** Called before each test. */
    // public void setUp()
    // {
    //   super.setUp();

    //   m_random = Item.s_random;

    //   s_random = EasyMock.createMock(java.util.Random.class);

    //   BaseCampaign.GLOBAL.m_bases.clear();
    // }

    /** Called after each test.
     *
     * @throws Exception as in base class
     *
     */
    // public void tearDown()
    // {
    //   s_random = m_random;

    //   super.tearDown();

    //   BaseCampaign.GLOBAL.m_bases.clear();
    // }

    /** Give information about what random values are used.
     *
     * @param inValues always pairs of values requested and values to be
     *        returned
     *
     */
    // public void setupRandom(int ... inValues)
    // {
    //   if(inValues.length % 2 != 0)
    //     throw new IllegalArgumentException("must have pairs of values");

    //   for(int i = 0; i < inValues.length; i += 2)
    //     EasyMock.expect(s_random.nextInt(inValues[i]))
    //       .andReturn(inValues[i + 1]);

    //   EasyMock.replay(s_random);
    // }

    //----- createItem() -----------------------------------------------

    /** Create a typical item for testing purposes.
     *
     * @return the newly created item
     *
     */
    // public static AbstractEntry createItem()
    // {
    //   ParseReader reader =
    //     new ParseReader(new java.io.StringReader(s_text), "test");

    //   //return Item.read(reader, new BaseCampaign("Test"));
    //   return null;
    // }

    //......................................................................
    //----- createBasedItem() ------------------------------------------

    /** Create a typical item for testing purposes.
     *
     * @return the newly created item
     *
     */
//     public static AbstractEntry createBasedItem()
//     {
//       // read the base entry
// //       ParseReader reader =
// //         new ParseReader(new java.io.StringReader(s_base), "test");

// //       BaseCampaign campaign = new BaseCampaign("Test");

// //       BaseEntry base = (BaseEntry)BaseItem.read(reader, campaign);

// //       campaign.add(base);

// //       // and empty value
// //       reader =
// //         new ParseReader(new java.io.StringReader("item test 1."),
// //                         "test");

// //       return Item.read(reader, campaign);
//       return null;
//     }

    //......................................................................

    //----- text -----------------------------------------------------------

    /** Test text for item. */
    // private static String s_text =
    //   "#------ Winter Blanket ----------------------------------------\n"
    //   + "\n"
    //   + "item with wearable Winter Blanket = \n"
    //   + "\n"
    //   + "  user size     small;\n"
    //   + "  value      42 cp;\n"
    //   + "  hp         1.\n"
    //   + "\n"
    //   + "#..............................................................";

    /** Test text for first base item. */
    // private static String s_base =
    //   "base item test 1 = \n"
    //   + "\n"
    //   + "  synonyms      \"blanket, winter\";\n"
    //   + "  categories    1, 2, 3;\n"
    //   + "  value         100 gp;\n"
    //   + "  weight        7 lbs;\n"
    //   + "  size          small;\n"
    //   + "  probability   rare;\n"
    //   + "  hardness      0;\n"
    //   + "  hp            10;\n"
    //   + "  world         generic;\n"
    //   + "  appearances   unique    \"first\","
    //   + "                common    \"second\","
    //   + "                common    \"third\","
    //   + "                rare      \"fourth\","
    //   + "                very rare \"fifth\","
    //   + "                unique    \"and sixth, the last one\";"
    //   + "  references    \"TSR 11550\" 107;\n"
    //   + "  description   \n"
    //   + "\n"
    //   + "  \"A thick, quilted, wool blanket.\".\n"
    //   + "\n";

    /** Test text for first base item. */
    // private static String s_baseComplete =
    //   "base item Winter Blanket = \n"
    //   + "\n"
    //   + "  synonyms      \"blanket, winter\";\n"
    //   + "  categories    1, 2, 3;\n"
    //   + "  value         100 gp;\n"
    //   + "  weight        7 lbs;\n"
    //   + "  size          small;\n"
    //   + "  probability   rare;\n"
    //   + "  hardness      0;\n"
    //   + "  hp            10;\n"
    //   + "  world         generic;\n"
    //   + "  appearances   common \"A ${user size} weapon.\";\n"
    //   + "  references    \"TSR 11550\" 107;\n"
    //   + "  description   \n"
    //   + "\n"
    //   + "  \"A thick, quilted, wool blanket.\".\n"
    //   + "\n";

    /** Test text for first second item. */
    // private static String s_base2 =
    //   "base item test 2 = \n"
    //   + "\n"
    //   + "  synonyms      \"blanket, winter\";\n"
    //   + "  categories    2, 3;\n"
    //   + "  value         5 gp;\n"
    //   + "  weight        7 lbs;\n"
    //   + "  size          small;\n"
    //   + "  probability   very rare;\n"
    //   + "  hardness      0;\n"
    //   + "  hp            10;\n"
    //   + "  world         generic;\n"
    //   + "  appearances   unique    \"first\","
    //   + "                common    \"second\","
    //   + "                common    \"third\","
    //   + "                rare      \"fourth\","
    //   + "                very rare \"fifth\","
    //   + "                unique    \"and sixth, the last one\";"
    //   + "  references    \"TSR 11550\" 107;\n"
    //   + "  description   \n"
    //   + "\n"
    //   + "  \"A thick, quilted, wool blanket.\".\n"
    //   + "\n";

    /** Test text for first third item. */
    // private static String s_base3 =
    //   "base item test 3 = \n"
    //   + "\n"
    //   + "  synonyms      \"blanket, winter\";\n"
    //   + "  categories    1, 3;\n"
    //   + "  value         50 gp;\n"
    //   + "  weight        70 lbs;\n"
    //   + "  size          large;\n"
    //   + "  probability   common;\n"
    //   + "  hardness      5;\n"
    //   + "  hp            100;\n"
    //   + "  world         generic;\n"
    //   + "  appearances   unique    \"first\","
    //   + "                common    \"second\","
    //   + "                common    \"third\","
    //   + "                rare      \"fourth\","
    //   + "                very rare \"fifth\","
    //   + "                unique    \"and sixth, the last one\";"
    //   + "  references    \"TSR 11550\" 107;\n"
    //   + "  description   \n"
    //   + "\n"
    //   + "  \"A thick, quilted, wool blanket.\".\n"
    //   + "\n";

    /** Test text for first fourth item. */
    // private static String s_base4 =
    //   "base item test 4 = \n"
    //   + "\n"
    //   + "  synonyms      \"blanket, winter\";\n"
    //   + "  categories    2;\n"
    //   + "  value         50 gp;\n"
    //   + "  weight        7 lbs;\n"
    //   + "  size          small;\n"
    //   + "  probability   uncommon;\n"
    //   + "  hardness      6;\n"
    //   + "  hp            1;\n"
    //   + "  world         generic;\n"
    //   + "  appearances   unique    \"first\","
    //   + "                common    \"second\","
    //   + "                common    \"third\","
    //   + "                rare      \"fourth\","
    //   + "                very rare \"fifth\","
    //   + "                unique    \"and sixth, the last one\";"
    //   + "  references    \"TSR 11550\" 107;\n"
    //   + "  description   \n"
    //   + "\n"
    //   + "  \"A thick, quilted, wool blanket.\".\n"
    //   + "\n";

    /** Test text for first fifth item. */
    // private static String s_base5 =
    //   "base item test 5 = \n"
    //   + "\n"
    //   + "  synonyms      \"blanket, winter\";\n"
    //   + "  categories    1;\n"
    //   + "  value         50 gp;\n"
    //   + "  weight        7 lbs;\n"
    //   + "  size          small;\n"
    //   + "  probability   unique;\n"
    //   + "  hardness      6;\n"
    //   + "  hp            1;\n"
    //   + "  world         generic;\n"
    //   + "  appearances   unique    \"first\","
    //   + "                common    \"second\","
    //   + "                common    \"third\","
    //   + "                rare      \"fourth\","
    //   + "                very rare \"fifth\","
    //   + "                unique    \"and sixth, the last one\";"
    //   + "  references    \"TSR 11550\" 107;\n"
    //   + "  description   \n"
    //   + "\n"
    //   + "  \"A thick, quilted, wool blanket.\".\n"
    //   + "\n";

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    // public void testRead()
    // {
    //   ParseReader reader =
    //     new ParseReader(new java.io.StringReader(s_baseComplete), "test");

    //   BaseEntry base = (BaseEntry)BaseItem.read(reader);

    //   BaseCampaign.GLOBAL.add(base);

    //   reader = new ParseReader(new java.io.StringReader(s_text), "test");

    //   String result =
    //     "#----- Winter Blanket\n"
    //     + "\n"
    //     + "item with wearable Winter Blanket [Winter Blanket] =\n"
    //     + "\n"
    //     + "  user size         Small;\n"
    //     + "  hp                1;\n"
    //     + "  max hp            ;\n"
    //     + "  value             42 cp;\n"
    //     + "  hardness          ;\n"
    //     + "  appearance        \n"
    //     + "  \"A ${user size} weapon.\".\n"
    //     + "\n"
    //     + "#.....\n";

    //   Entry entry = (Entry)Item.read(reader);

    //   entry.complete();

    //   assertNotNull("item should have been read", entry);
    //   assertEquals("item name does not match", "Winter Blanket",
    //                 entry.getName());
    //   assertEquals("item does not match", result, entry.toString());
    // }

    //......................................................................
    //----- get/set --------------------------------------------------------

    /** Test setting and getting of values. */
    // public void testGetSet()
    // {
    //   // easy mock the random object
    //   setupRandom(0, 0,
    //               100 + BaseItem.Size.values().length, 50,
    //               100, 50);

    //   BaseItem base = new BaseItem("some item");

    //   assertTrue("base hp", base.setHP(42));
    //   assertTrue("value", base.setValue(0, 12, 3, 0));
    //   assertTrue("weight", base.setWeight(new Rational(2, 1, 2)));

    //   BaseItem base2 = new BaseItem("some other item");

    //   Item item = new Item(base, base2);

    //   item.complete();

    //   assertEquals("value", "12 gp 3 sp [+12 gp 3 sp some item]",
    //                item.m_value.toString());
    //   assertTrue("value", item.setValue(0, 0, 7, 1));

    //   // set back to medium to see that the values are base again
    //   assertEquals("max hp", 42, item.getMaxHP());
    //   assertEquals("hp", 42, item.getHP());
    //   assertEquals("value", "7 sp 1 cp", item.m_value.toString());
    //   assertEquals("weight", "2 1/2 lbs [+2 1/2 lbs some item]",
    //                item.m_weight.toString());

    //   assertTrue("hp", item.setHP(23));
    //   assertEquals("hp", 23, item.getHP());
    //   assertFalse("hp", item.setHP(-1));
    //   assertFalse("hp", item.setHP(50));
    //   assertTrue("hp", item.setHP(0));

    //   assertTrue("description", item.setDescription("a test"));
    //   assertEquals("description", "a test", item.getDescription());
    //   assertTrue("player notes", item.setPlayerNotes("some notes"));
    //   assertEquals("player notes", "some notes", item.getPlayerNotes());
    //   assertTrue("dm notes", item.setDMNotes("secret notes"));
    //   assertEquals("dm notes", "secret notes", item.getDMNotes());
    //   assertTrue("player name", item.setPlayerName("my name"));
    //   assertEquals("player name", "my name", item.getPlayerName());

    //   /*
    //   assertTrue("qualities");
    //   */
    // }

    //......................................................................
    //----- random ---------------------------------------------------------

    /** Test an item with random value. */
    // public void testRandom()
    // {
    //   setupRandom(625 + 625 + 25, 1251);

    //   BaseItem base = new BaseItem("some item for random");

    //   assertTrue("base hp", base.setHP(42));
    //   assertTrue("value", base.setValue(0, 12, 3, 0));
    // assertTrue("appearances", base.addAppearance(BaseItem.Probability.COMMON,
    //                                                "common appearance"));
    // assertTrue("appearances", base.addAppearance(BaseItem.Probability.COMMON,
    //                                                "common appearance 2"));
    //   assertTrue("appearances", base.addAppearance(BaseItem.Probability.RARE,
    //                                                "rare appearance"));

    //   Item item = new Item(base);

    //   item.complete();

    //   assertEquals("value", "12 gp 3 sp", item.getValue().toString());
    //   assertEquals("value", "12 gp 3 sp [+12 gp 3 sp some item for random]",
    //                item.m_value.toString());
    //   assertEquals("appearance", "rare appearance", item.getAppearance());

    //   EasyMock.verify(s_random);

    //   //s_random = old;
    // }

    //......................................................................
    //----- print ----------------------------------------------------------

    /** Testing printing. */
    // public void testPrint()
    // {
    //   setupRandom(0, 0,
    //               100 + BaseItem.Size.values().length, 50,
    //               100, 50);

    //   BaseItem base = new BaseItem("some item");

    //   assertTrue("weight", base.setWeight(new Rational(2)));

    //   Item item = new Item(base);

    //   item.complete();

    //   assertTrue("player name", item.setPlayerName("player name"));
    //   assertTrue("player notes", item.setPlayerNotes("player notes"));
    //   item.m_description.set("desc");
    //   item.m_short.set("short");

    //   PrintCommand commands = item.printCommand(false, false);

    //   assertNotNull("command", commands);

    //   // title with player name
    //   Command title =
    //   item.getCommand(commands, new Command("${title, player title}"), true);

    //   assertEquals("title", "title", extract(title, 1, 0));
    //   assertEquals("title", "some item", extract(title, 1, 1));

    //   title =
    //  item.getCommand(commands, new Command("${title, player title}"), false);

    //   assertEquals("title", "title", extract(title, 2, 0));
    //   assertEquals("title", "player name", extract(title, 2, 1));

    //   // values
    //   Command values =
    //     item.getCommand(commands,
    //                     new Command("%{base, player notes, weight}"), true);

    //   assertEquals("base", "Base:", extract(values, 1, 1, 1));
    //   assertEquals("base", "some item", extract(values, 2, 1, 1, 1));
    //   assertEquals("notes", "Player Notes:", extract(values, 3, 1, 1));
    //   assertEquals("notes", "player notes", extract(values, 4, 1, 2));
    //   assertEquals("weight", "Weight:", extract(values, 5, 1, 1));
    //   assertEquals("weight", "2 lbs", extract(values, 6, 1, 2, 1, 1, 2, 3));
    // }

    //......................................................................
    //----- print DM -------------------------------------------------------

    /** Test printing an item for the dm. */
    // public void testPrintDM()
    // {
    //   setupRandom(0, 0,
    //               100 + BaseItem.Size.values().length, 50,
    //               100, 50);

    //   BaseItem base = new BaseItem("some item");

    //   assertTrue("weight", base.setWeight(new Rational(2)));
    //   assertTrue("description", base.setDescription("some base desc"));
    //   assertTrue("player name", base.setPlayerName("base player name"));
    //   assertTrue("value", base.setValue(0, 3, 2, 0));
    //   assertTrue("hp", base.setHP(42));

    //   Item item = new Item(base);

    //   item.complete();

    //   assertTrue("player name", item.setPlayerName("player name"));
    //   assertTrue("player notes", item.setPlayerNotes("player notes"));
    //   assertTrue("player notes", item.setDMNotes("dm notes"));
    //   assertTrue("description", item.setDescription("desc"));
    //   assertTrue("hp", item.setHP(23));

    //   item.m_short.set("short");

    //   PrintCommand commands = item.printCommand(true, false);

    //   assertNotNull("command", commands);

    //   Command intro =
    //   item.getCommand(commands, new Command("${title, description}"), true);

    //   // title with player name
    //   assertEquals("title", "title", extract(intro, 1, 0));
    //   assertEquals("title", "link", extract(intro, 1, 1, 0));
    //   assertEquals("title", "/entry/item/some item",
    //                extract(intro, 1, 1, -1));
    //   assertEquals("title", "some item", extract(intro, 1, 1, 1));

    //   // description
    //   assertEquals("description", "description", extract(intro, 2, 3));

    //   Command values =
    //     item.getCommand(commands,
    //                     new Command("%{base, value, player notes, dm notes, "
    //                                 + "weight}"), true);

    //   // values
    //   assertEquals("base", "Base:", extract(values, 1, 1, 1));
    //   assertEquals("base", "some item", extract(values, 2, 1, 1, 1));
    //   assertEquals("value", "Value:", extract(values, 3, 1, 1));
    //   assertEquals("value", "3 gp", extract(values, 4, 1, 2, 1, 1, 1, 1, 2));
    //   assertEquals("value", "2 sp", extract(values, 4, 1, 2, 1, 1, 1, 3, 2));
    //   assertEquals("notes", "Player Notes:", extract(values, 5, 1, 1));
    //   assertEquals("notes", "player notes", extract(values, 6, 1, 3));
    //   assertEquals("notes", "Dm Notes:", extract(values, 7, 1, 1));
    //   assertEquals("notes", "dm notes", extract(values, 8, 1, 3));
    //   assertEquals("weight", "Weight:", extract(values, 9, 1, 1));
    // ssertEquals("weight", "2 lbs", extract(values, 10, 1, 2, 1, 1, 1, 1, 2));
    // }

    //......................................................................
    //----- lookup ---------------------------------------------------------

    /** Testing lookup. */
    // public void testLookup()
    // {
    //   setupRandom(0, 0,
    //               0, 0,
    //               0, 0,
    //               0, 0,
    //               100 + BaseItem.Size.values().length, 50, 100, 50,
    //               3125, 1200, // name lookup
    //               100 + BaseItem.Size.values().length, 50, 100, 50,
    //               625, 50, // lookup 2
    //               625, 50, // lookup 3
    //               4 * 625, 2 * 625 + 10, // lookup 4
    //               2 * 625, 626, // lookup 5
    //               2 * 625, 624, // lookup 6
    //               3 * 625, 2 * 625, // lookup 7
    //               2 * 625, 60, // lookup 8
    //               2 * 625, 627, // lookup 9
    //               625, 62, // lookup 10
    //               2 * 625, 50, // lookup 11
    //               2 * 625, 50, // lookup 12
    //               2 * 625, 680, // lookup 13
    //               625, 600 // lookup 14
    //               );

    //   BaseItem base1 = new BaseItem("base item 1");
    //   BaseItem base2 = new BaseItem("base item 2");
    //   BaseItem base3 = new BaseItem("base item 3");
    //   BaseItem base4 = new BaseItem("base item 4");
    //   BaseItem base5 = new BaseItem("base item 5");

    //   base1.setHP(42);
    //   base2.setHP(23);
    //   base3.setHP(1);
    //   base4.setHP(2);
    //   base5.setHP(15);

    //   base1.addCategory("1");
    //   base1.addCategory("2");
    //   base1.addCategory("3");
    //   base2.addCategory("2");
    //   base3.addCategory("3");
    //   base3.addCategory("4");
    //   base4.addCategory("1");
    //   base4.addCategory("3");
    //   base4.addCategory("2");
    //   base5.addCategory("1");
    //   base5.addCategory("5");

    //   BaseCampaign.GLOBAL.add(base1);
    //   BaseCampaign.GLOBAL.add(base2);
    //   BaseCampaign.GLOBAL.add(base3);
    //   BaseCampaign.GLOBAL.add(base4);
    //   BaseCampaign.GLOBAL.add(base5);

    //   Item item = new Item("base item 1");

    //   Campaign campaign = new Campaign("test", "tst", 0);

    //   campaign.add(item);

    //   assertEquals("simple lookup", base1, item.m_baseEntries.get(0));

    //   // how about an unnamed item
    //   item = new Item();

    //   campaign.add(item);

    //   assertEquals("empty lookup", base2, item.m_baseEntries.get(0));

    //   // lookup with predefines
    //   ParseReader reader =
    //     new ParseReader(new java.io.StringReader("item [[ = hp == 1. ]]."),
    //                     "test");

    //   BaseItem base =
    //     (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("hp", base3, base);

    //   reader =
    //     new ParseReader(new java.io.StringReader("item [[ = hp == 15. ]] "
    //                                              + "= hp 5."), "test");

    //   item = (Item)Item.read(reader);
    // base = (BaseItem)BaseCampaign.GLOBAL.lookup(item.getLookup(), BASE_TYPE);

    //   assertEquals("hp ==", base5, base);
    //   assertEquals("hp ==", 5,     item.getHP());

    //   // not equal
    //   reader =
    //     new ParseReader(new java.io.StringReader("item [[ = hp != 1. ]]."),
    //                     "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("hp !=", base4, base);
    //   assertTrue("hp !=", 1 != base.getHP());

    //   // smaller
    //   reader =
    //     new ParseReader(new java.io.StringReader("item [[ = hp < 10. ]]."),
    //                     "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("hp <", base4, base);
    //   assertTrue("hp <", base.getHP() < 10);

    //   // more or equal
    //   reader =
    //     new ParseReader(new java.io.StringReader("item [[ = hp >= 20. ]]."),
    //                     "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("hp >=", base1, base);
    //   assertTrue("hp >=", base.getHP() >= 20);

    //   // one of
    //   reader =
    //  new ParseReader(new java.io.StringReader("item [[ = categories ~= 2. ]]"
    //                                              + "."),
    //                     "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("categories ~=", base2, base);

    //   // two of
    //   reader =
    //     new ParseReader(new java.io.StringReader("item "
    //                                           + "[[ = categories ~= 1,2. ]]"
    //                                              + "."), "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("categories ~=", base1, base);

    //   // not one of
    //   reader =
    //  new ParseReader(new java.io.StringReader("item [[ = categories ~! 2. ]]"
    //                                              + "."), "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("categories ~!", base5, base);

    //   // not two of
    //   reader =
    //     new ParseReader(new java.io.StringReader("item "
    //                                           + "[[ = categories ~! 3, 2. ]]"
    //                                              + "."), "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("categories ~!", base5, base);

    //   // now with multiple matchers!
    //   reader =
    //     new ParseReader(new java.io.StringReader("item "
    //                                           + "[[ = hp > 10 && < 30. ]]."),
    //                     "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("hp < & >", base2, base);

    //   // now with multiple matchers!
    //   reader =
    //     new ParseReader(new java.io.StringReader("item "
    //                                            + "[[ = hp == 1 || == 23. ]]"
    //                                              + "."), "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("hp == | ==", base2, base);

    //   // now with multiple matchers!
    //   reader =
    //     new ParseReader(new java.io.StringReader("item "
    //                                             + "[[ = hp <= 1 || >= 40. ]]"
    //                                              + "."), "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("hp < | >", base3, base);

    //   // lookup with name
    //   reader =
    //     new ParseReader(new java.io.StringReader("item [[ **\\s+3. ]]."),
    //                     "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("name", base3, base);
    // }

    //......................................................................
    //----- auto attachment ------------------------------------------------

    /** Testing auto attachments. */
    // public void testAutoAttachment()
    // {
    //   String text =
    //     "base item with container Container = \n"
    //     + "  hp 3.\n"
    //     + "item Container = \n"
    //     + "   hp 2;\n"
    //     + "   contents item guru. item guru2 = hp 3..\n";

    //   ParseReader reader =
    //     new ParseReader(new java.io.StringReader(text), "container test");

    //   BaseItem base = (BaseItem)BaseItem.read(reader);

    //   BaseCampaign.GLOBAL.add(base);

    //   Item item = (Item)Item.read(reader);

    //   assertTrue("attachment",
    //              item.hasAttachment(net.ixitxachitls.dma.entries.attachments
    //                                 .Contents.class));

    //   m_logger.addExpectedPattern("WARNING: base.not-found:.*"
    //                               + "(base name 'guru').*");
    //   m_logger.addExpectedPattern("WARNING: base.not-found:.*"
    //                               + "(base name 'guru2').*");
    // }

    //......................................................................
    //----- qualities ------------------------------------------------------

    /** Testing qualities. */
    // public void testQualities()
    // {
    //   BaseQuality q1 = new BaseQuality("Q1");
    //   BaseQuality q2 = new BaseQuality("Q2");

    //   q1.m_qualifier.set("q1");
    //   q2.m_qualifier.set("q2");

    //   BaseCampaign.GLOBAL.add(q1);
    //   BaseCampaign.GLOBAL.add(q2);

    //   Item item = new Item("item");

    //   assertTrue("add", item.addQuality("Q1"));
    //   assertTrue("add", item.addQuality("Q2"));
    //   assertTrue("add", item.addQuality("Q3"));

    //   // check the name of the item
    //   // TODO: fix this test
    //   //assertEquals("name", "q1 q2 item", item.getName());

    //   m_logger.addExpected("WARNING: could not find base(s) for 'Q3'");
    // }

    //......................................................................
    //----- list commands --------------------------------------------------

    /** Test printing list commands. */
    // public void testListCommands()
    // {
    //   setupRandom();

    //   Item item = new Item("test");

    //   item.setWeight(new Rational(2));
    //   item.setPlayerNotes("player notes");
    //   item.setDescription("description");
    //   item.setPlayerName("name");
    //   item.setAppearance("shiny");

    //   item.complete();

    //   Command values =
    //   item.getCommand(item.printCommand(false, false), LIST_COMMAND, false);

    //   assertEquals("list", "table", extract(values, 1, 0));
    //   assertEquals("list", "name", extract(values, 1, 2, 1, 3, 1, 2));
    //   assertEquals("list", "2 lbs", extract(values, 1, 3, 1, 1, 1, 2));
    //   assertEquals("list", "shiny", extract(values, 1, 4, 1, 1, 1, 2));
    //   assertEquals("list", "player notes",
    //                extract(values, 1, 4, 1, 1, 2, 2));

    //   m_logger.addExpected("WARNING: could not find base(s) for 'test'");
    // }

    //......................................................................
    //----- based ----------------------------------------------------------

    /** Test basing items on multiple base items. */
    // public void testBased()
    // {
    //   BaseItem base1 = new BaseItem("base1");
    //   base1.addAttachment("armor");
    //   base1.setValue(0, 10, 0, 0);
    //   base1.setDescription("desc1");
    //   base1.set("max dexterity", "+4");
    //   base1.set("synonyms", "\"a\", \"b\"");

    //   BaseItem base2 = new BaseItem("base2");
    //   base2.addAttachment("armor");
    //   base2.setDescription("desc2");
    //   assertNull(base2.set("AC bonus", "+5 armor"));
    //   assertNull(base2.set("weight", "[/2 \"test\"]"));
    //   base2.set("synonyms", "\"c\", \"d\"");

    //   BaseItem base3 = new BaseItem("base3");
    //   base3.addAttachment("armor");
    //   base3.setValue(0, 10, 0, 0);
    //   assertNull(base3.set("AC bonus", "+2 shield"));
    //   assertNull(base3.set("max dexterity", "+2"));
    //   assertNull(base3.set("weight", "20 lbs"));

    //   Item item = new Item("test", base1, base2, base3);
    //   item.complete();

    // assertEquals("synonyms", "a,\nb,\nc,\nd [+\"c\",\n\"d\" base2]",
    //             item.getBaseValue("synonyms", Combine.ADD, true).toString());
    //   assertEquals("value", "20 gp [+10 gp base1, +10 gp base3]",
    //                item.m_value.toString());
    //assertEquals("description", "desc1 desc2", item.m_description.toString());
    //   assertEquals("ac bonus", "+7 [+5 armor, +2 shield]",
    //                item.getBaseValue("AC bonus", Combine.ADD, true,
    //                                  new Modifiable<Number>
    //                                  (new Number(0, 10, true))).toString());
    //   assertEquals("max dexterity", "+2",
    //                item.getBaseValue("max dexterity", Combine.MINIMUM, true)
    //                .toString());
    //   assertEquals("weight", "10 lbs [+20 lbs base3, /2 \"test\"]",
    //                item.m_weight.toString());
    // }

    //......................................................................
  }

  //........................................................................
}
