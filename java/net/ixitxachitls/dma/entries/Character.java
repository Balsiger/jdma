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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Money;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Rational;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.Weight;
import net.ixitxachitls.output.commands.Color;
import net.ixitxachitls.output.commands.Columns;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Divider;
import net.ixitxachitls.output.commands.Icon;
import net.ixitxachitls.output.commands.ImageLink;
import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.output.commands.OverlayIcon;
import net.ixitxachitls.output.commands.Table;
import net.ixitxachitls.output.commands.Window;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.Pair;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The storage space for a character in the game.
 *
 * In the long run, this will probably be derived from Monster or even NPC,
 * but we don't currently have these available. We still want to manage some
 * values now, like items, thus this class is currently incomplete.
 *
 * @file          Character.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Character extends CampaignEntry<BaseCharacter>
                       //implements Storage<Item>
{
  //----------------------------------------------------------------- nested

  //----- state -------------------------------------------------------------

  /** The character state. */
  public enum State implements EnumSelection.Named
  {
    /** A normal character going on adventures. */
    ADVENTURING("adventuring"),
    /** The character is currently incapable of adventuring. */
    INCAPACITATED("incapacitated"),
    /** The character has been retired by the player or the DM. */
    RETIRED("retired"),
    /** The character died. */
    DEAD("dead");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the name.
     *
     * @param inName     the name of the value
     *
     */
    private State(@Nonnull String inName)
    {
      m_name = constant("character.state", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Convert to human readable string.
     *
     * @return a human readable string representation
     *
     */
    @Override
    public @Nonnull String toString()
    {
      return m_name;
    }
  };

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------ Character -------------------------------

  /**
   * Create the character.
   *
   */
  protected Character()
  {
    super(TYPE, TYPE.getBaseType());
  }

  //........................................................................
  //------------------------------ Character -------------------------------

  /**
   * Create the character with an name.
   *
   * @param    inName the name of the character to create
   *
   */
  public Character(@Nonnull String inName)
  {
    super(inName, TYPE, TYPE.getBaseType());
  }

  //.......................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final Type<Character> TYPE =
    new Type<Character>(Character.class, BaseCharacter.TYPE);

  /** The print for nicely printing an overview of the entry with all data. */
  public static final Print s_print =
    new Print("\\table{3:L;10:L}{$image}{"
              + "$title "
              + "\\par "
              + "\\bold{Player: } $base \\linebreak "
              + "\\bold{Campaign: } $campaign \\linebreak "
              + "\\bold{Level: } $level \\linebreak "
              + "\\bold{Total Wealth: } $wealth \\linebreak "
              + "\\bold{Carried Weight: } $weight \\linebreak"
              + "} "
              + "\\par\\par "
              + "\\title{Items} "
              + "$itemlist "
              + "\\title{Counted} "
              + "$countedlist "
              + "\\title{Timed} "
              + "$timedlist ");

  /** The print for printing a whole page entry. */
  public static final Print s_pagePrint =
    new Print("$image "
              + "${do mail} ${as pdf dm} ${as pdf} ${as text} ${as dma} "
              + "$title "
              + "$clear "
              + "$files "
              + "\n"
              + "$par "
              + "%name %base %campaign "
              + "%level %state %wealth %weight %items "
              // admin
              + "%errors"
              );

  /** The printer for printing in a list. */
  public static final ListPrint s_listPrint =
    new ListPrint("1:L(label);20:L(id)[ID];20(producttitle)[Title];"
                  + "1:L(status)[Status];1:L(level)[Level]",
                  "$label $listlink", null, "$name", "$status", "$level");

  //----- state ------------------------------------------------------------

  /** The state value. */
  @Key("state")
  protected EnumSelection<State> m_state =
    new EnumSelection<State>(State.class);

  //........................................................................
  //----- items ------------------------------------------------------------

  /** The possessions value. */
  @Key("items")
  protected ValueList<Name> m_items = new ValueList<Name>(new Name());

  //........................................................................
  //----- level ------------------------------------------------------------

  /** The character level. This is a big simplification and has to be replaced
   * by a list of classes and their levels. */
  @Key("level")
  protected Number m_level = new Number(1, 1, 100);

  //........................................................................
  //----- wealth -----------------------------------------------------------

  /** The standard wealth per level in gold pieces. */
  private static final int []s_wealth =
    Config.get("/game/wealth.per.level", new int []
      {
        0,
        900,
        2700,
        5400,
        9000,
        13000,
        19000,
        27000,
        36000,
        49000,
        66000,
        88000,
        110000,
        150000,
        200000,
        260000,
        340000,
        440000,
        580000,
        760000,
      });

  //........................................................................

  static
  {
    extractVariables(Character.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //---------------------------- containedItems ----------------------------

  /**
   * Get all the items contained in this contents.
   *
   * @param       inDeep true for returning all item, including nested ones,
   *                     false for only the top level items
   * @return      a list with all the items
   *
   */
  public @Nonnull Map<String, Item> containedItems(boolean inDeep)
  {
    Map<String, Item> items = new HashMap<String, Item>();
    for(Name name : m_items)
    {
      Item item = getCampaign().getItem(name.get());
      if(item == null)
        continue;

      items.put(name.get(), item);
      items.putAll(item.containedItems(inDeep));
    }

    return items;
  }

  //........................................................................
  //------------------------------ possesses -------------------------------

  /**
   * Checks if the character has the item in possession.
   *
   * NOTE: this is an expensive operation, as it has to read all the items
   * recursively of a character.
   *
   * @param       inItem the name of the item to check
   *
   * @return      true if the character possesses the item, false if not
   *
   */
  public boolean possesses(@Nonnull String inItem)
  {
    return containedItems(true).containsKey(inItem);
  }

  //........................................................................

  //-------------------------- getCharacterLevel ---------------------------

  /**
   * Get the character level.
   *
   * @return      the level
   *
   */
  public int getCharacterLevel()
  {
    return (int)m_level.get();
  }

  //........................................................................
  //---------------------------- getStorageName ----------------------------

  /**
   * Get the name of the file.
   *
   * @return      the name of the file (without 'unnecessary path' info)
   *
   */
  // public String getStorageName()
  // {
  //   return m_storage.getStorageName() + ":" + getName();
  // }

  //........................................................................
  //----------------------------- getStorageID -----------------------------

  /**
   * Get the id of the storage or null if none (usually if not an value group).
   *
   * @return      the id of the storage or null
   *
   */
  // public String getStorageID()
  // {
  //   return getID();
  // }

  //........................................................................

  //---------------------------- wealthPerLevel ----------------------------

  /**
   * Get the wealth a character should approximately have for the given level.
   *
   * @param       inLevel the level for which to compute the wealth
   *
   * @return      the wealth for the given level in gp
   *
   */
  public static int wealthPerLevel(int inLevel)
  {
    if(inLevel <= 0)
      return s_wealth[0];

    if(inLevel > 20)
      return s_wealth[19] + (inLevel - 20) * (s_wealth[19] - s_wealth[18]);

    return s_wealth[inLevel - 1];
  }

  //........................................................................
  //----------------------------- totalWealth ------------------------------

  /**
   * The total wealth in gp of the character.
   *
   * @return      the gp value of all items
   *
   */
  public @Nonnull Money totalWealth()
  {
    Money total = new Money(0, 0, 0, 0);

    for(Name name : m_items)
    {
      Item item = getCampaign().getItem(name.get());
      if(item == null)
        continue;

      Money value = item.getValue();
      if(value != null)
        total = total.add(value);
    }

    return total;
  }

  //........................................................................
  //----------------------------- totalWeight ------------------------------

  /**
   * The total weight in pounts of the character.
   *
   * @return      the lb value of all items
   *
   */
  public @Nonnull Weight totalWeight()
  {
    Weight total = new Weight(new Rational(0), null);

    for(Name name : m_items)
    {
      Item item = getCampaign().getItem(name.get());
      if(item == null)
        continue;

      Weight weight = item.getWeight();
      if(weight != null)
        total = total.add(weight);
    }

    return total;
  }

  //........................................................................

  //------------------------------- hasEntry -------------------------------

  /**
   * Check if the character has the given entry in his responsibility.
   *
   * @param       inEntry the entry to check for
   *
   * @return      true if the entry is the characters responsibility, false if
   *              not
   *
   * @undefined   never
   *
   */
  // public boolean hasEntry(Entry inEntry)
  // {
  //   if(inEntry == null)
  //     return false;

  //   return getPossessions(true).contains(inEntry);
  // }

  //........................................................................
  //------------------------------- isBased --------------------------------

  /**
   * Check if the character is based on the given user (BaseCharacter).
   *
   * @param       inBase the base character to check for
   *
   * @return      true if based on given, false if not
   *
   */
  // public boolean isBased(BaseCharacter inBase)
  // {
  //   if(m_baseEntries == null)
  //     return false;

  //   for(BaseEntry base : m_baseEntries)
  //     if(base == inBase)
  //       return true;

  //   return false;
  // }

  //........................................................................

  //----------------------------- getPrint -----------------------------

  /**
   * Get the print for a full page.
   *
   * @return the print for page printing
   *
   */
  protected @Nonnull Print getPrint()
  {
    return s_print;
  }

  //........................................................................
  //----------------------------- getPagePrint -----------------------------

  /**
   * Get the print for a full page.
   *
   * @return the print for page printing
   *
   */
  @Override
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
  @Override
  protected @Nonnull ListPrint getListPrint()
  {
    return s_listPrint;
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
  public @Nullable ValueHandle computeValue(@Nonnull String inKey, boolean inDM)
  {
    if("wealth".equals(inKey) && inDM)
    {
      Money wealth = totalWealth();
      double current = wealth.getAsGold().getValue();
      int low = wealthPerLevel((int)m_level.get() - 1);
      int high = wealthPerLevel((int)m_level.get() + 1);
      int goal = wealthPerLevel((int)m_level.get());

      Object command = (int)wealth.getAsGold().getValue() + " gp";

      if(current <= low)
        command = new Window(new Color("error", command),
                            "Total wealth is way too low, should be "
                            + Strings.format(goal) + " gp for level "
                            + m_level);
      else
        if(current <= (goal + low) / 2.0)
          command = new Window(new Color("warning", command),
                              "Total wealth is a little too low, should be "
                              + Strings.format(goal) + " gp for level "
                              + m_level);
        else
          if(current >= high)
            command = new Window(new Color("error", command),
                                "Total wealth is a way too high, should be "
                                + Strings.format(goal) + " gp for level "
                                + m_level);
          else
            if(current >= (goal + high) / 2.0)
              command = new Window(new Color("warning", command),
                                  "Total wealth is a little too high, "
                                  + "should be " + Strings.format(goal)
                                  + " gp for level " + m_level);

      return new FormattedValue(command, null, "wealth");
    }

    if("itemlist".equals(inKey))
    {
      List<Object> commands = new ArrayList<Object>();
      for(Name name : m_items)
      {
        Item item = getCampaign().getItem(name.get());
        commands.add(item.computeValue("itemlist", inDM)
                     .format(item, inDM, true));
      }

      return new FormattedValue(new Columns("2", new Command(commands)),
                                null, "itemlist");
    }

    if("countedlist".equals(inKey))
    {
      List<Object> commands = new ArrayList<Object>();
      for(Item item : containedItems(true).values())
      {
        ValueHandle value = item.computeValue("counted:list", inDM);
        if (value != null && !value.value(item, inDM).toString().isEmpty())
          commands.addAll(((Command)value.format(item, inDM, true))
                          .getArguments());

        value = item.computeValue("multiple:list", inDM);
        if (value != null && !value.value(item, inDM).toString().isEmpty())
          commands.addAll(((Command)value.format(item, inDM, true))
                          .getArguments());

        value = item.computeValue("multiuse:list", inDM);
        if (value != null && !value.value(item, inDM).toString().isEmpty())
          commands.addAll(((Command)value.format(item, inDM, true))
                          .getArguments());
      }

      return new FormattedValue(new Table("colored",
                                          "30:L(name)[Name];70:L(count)[Count]",
                                          new Command(commands)),
                                null, "countedlist");
    }

    if("timedlist".equals(inKey))
    {
      List<Object> commands = new ArrayList<Object>();
      for(Item item : containedItems(true).values())
      {
        ValueHandle value = item.computeValue("timed:list", inDM);
        if (value == null || value.value(item, inDM).toString().isEmpty())
          continue;

        commands.addAll(((Command)value.format(item, inDM, true))
                        .getArguments());
      }

      return new FormattedValue(new Table("colored",
                                          "30:L(name)[Name];70:L(count)[Count]",
                                          new Command(commands)),
                                null, "timedlist");
    }

    if("items".equals(inKey))
    {
      List<Object> commands = new ArrayList<Object>();

      for(Name name : m_items)
      {
        if(!commands.isEmpty())
          commands.add(", ");

        Item item = getCampaign().getItem(name.get());
        String url = getCampaign().getPath() + "/item/" + name;
        if(item != null)
          commands.add(new Link(item.getNameCommand(inDM), url));
        else
          commands.add(new Link(name, url));
      }

      if(inDM)
      {
        commands.add(" | ");
        commands.add(new Link("Add", "javascript:item.create("
                              + Encodings.toJSString(getCampaign().getPath())
                              + ", "
                              + Encodings.toJSString
                              (getCampaign().getEditType() + "/"
                               + TYPE.getLink()
                               + "/" + getName()) + ");\""));
      }

      return new FormattedValue(new Command(commands), m_items, "items")
        .withPlural("items");
    }

    if("weight".equals(inKey))
      return new FormattedValue(totalWeight().format(), "", "weight");

    if(inDM && "as pdf dm".equals(inKey))
      return new FormattedValue
        (new Divider("doc-link doc-link-icon",
                     new Icon("doc-pdf.png", "DM", getName() + ".pdf")),
         null, "as dm pdf");

    if(inDM && "as pdf".equals(inKey))
      return new FormattedValue
        (new ImageLink("/icons/doc-pdf.png", getName(),
                       getName() + ".pdf?user="
                       + getBaseEntries().get(0).getName(), "doc-link"),
         null, "as pdf");

    if(inDM && "do mail".equals(inKey))
      return new FormattedValue
        (new ImageLink("/icons/mail.png", "Mail PDF",
                       "/actions/mail" + getPath(), "doc-link doc-link-mail"),
         null, "do mail");

    return super.computeValue(inKey, inDM);
  }

  //........................................................................

  //------------------------------ printCommand ----------------------------

  /**
   * Print the item to the document, in the general section.
   *
   * @param       inDM   true if set for DM, false for player
   * @param       inEditable true if values are editable, false if not
   *
   * @return      the command representing this item in a list
   *
   */
  // public PrintCommand printCommand(boolean inDM, boolean inEditable)
  // {
  //   // determine the dm for the campaign this user is in
  //   String email = "";
  //   BaseCharacter dm = getDM();

  //   if(dm != null)
  //     email = dm.m_email.get();
  //   else
  //     Log.warning("could not find DM for character " + getName());

  //   commands.pre.add
  //     (new Divider
  //      ("title",
  //       new Command(new Object[]
  //         {
  //           new Icon("pdf.png", "item list",
  //                    "/pdf/items/" + getID() + ".pdf"),
  //           inDM ? new Icon("pdf.png", "item list (DM)",
  //                           "/pdf/dm/items/" + getID() + ".pdf")
  //           : "",
  //           inDM ? new Icon("mail.png",
  //                           "mail item list to user",
  //                           "js:mail("
  //                           + "'" + email + "', "
  //                           + "'Itemliste "
  //                           + getName().replaceAll("\'", "\\\\'") + "', "
  //                           + "'Hi " + getName().replaceAll("\'", "\\\\'")
  //                           + ",\\\\n\\\\nAttached you find your "
  //                           + "current list of items. Please "
  //                           + "report any inconsistencies as "
  //                           + "usual.\\\\n\\\\n"
  //                           + "Your Dungeon Master', "
  //                           + "{ user: '" + getID() + "', "
  //                           + "type: 'items'})")
  //           : "",
  //         })));

  //   commands.post.add(new Divider("item-desc", "item-desc", null));
  //   commands.post.add("<table id='items-table' class='items'>");
  //   commands.post.add("<tr id='item-" + getID() + "' class='item-container'>"
  //                      + "<td>Items of " + getName() + "</td><td></td>"
  //                      + "</tr>\n");

  //   for(Entry entry : getPossessions(true))
  //   {
  //     if(entry instanceof Item)
  //     {
  //       String child;

  //       Item item = (Item)entry;
  //       String id = item.getStorage().getStorageID();
  //       if(id != null)
  //         child = "child-of-item-" + id;
  //       else
  //         child = "";

  //       if(entry.hasAttachment(Contents.class))
  //         child += " item-container";
  //       else
  //         child += " item";

  //       commands.post.add("<tr id='item-" + item.getID() + "' "
  //                         + "class='" + child + "' "
  //                         + "onclick='$p(\"item-desc\").innerHTML = "
  //                         + "ajax(\"/entry/item/" + item.getID()
  //                         + "?body&short\", null, null);"
  //                         + "'>"
  //                         + "<td><span class='item-name'>" + item.getName()
  //                         + "</span></td>"
  //                         + "<td>" + item.getWeight() + "</td>"
  //                         + "</tr>\n");
  //     }
  //   }
  //   commands.post.add("</table>");
  //   commands.post.add(new Divider("clear", null));

  //   commands.post.add(new Script("setupTreeTable('items-table', 'item', "
  //                                + "'item-container');"));

  //   return commands;
  // }

  //........................................................................
  //------------------------------- getIcon --------------------------------

  /**
   * Get the command to print an icon for this character.
   *
   * @param       inDM true if setting this for the dm, false if not
   *
   * @return      the command to display a character as an icon
   *
   */
  @SuppressWarnings("unchecked") // pair array creation of var args
  public Command getIcon(boolean inDM)
  {
    DMAData.File main = getMainFile();
    String src;
    if(main == null)
      src = "character/person.png";
    else
      src = main.getIcon() + "=s100";

    if(inDM)
      return new OverlayIcon(src, getName(), getPath(), true,
                             new Pair<String, Object>("right-bottom",
                                                      "character/"
                                                      + m_state.getSelected()),
                             new Pair<String, Object>("left-top",
                                                      "character/dm"));
    else
      return new OverlayIcon(src, getName(), getPath(), true,
                             new Pair<String, Object>("right-bottom",
                                                      "character/"
                                                      + m_state.getSelected()));
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
  public @Nullable Value compute(@Nonnull String inKey)
  {
    if("icon".equals(inKey))
    {
      DMAData.File main = getMainFile();
      if(main == null)
        return new Name("character/person.png");
      else
        return new Name(main.getIcon() + "=s100");
    }

    if("wealth".equals(inKey))
    {
      Money wealth = totalWealth();
      Number number = new Number(0, 100000000);

      return new Multiple
        (new Multiple.Element(wealth, false),
         new Multiple.Element(number.as((int)wealth.getAsGold().getValue()),
                              false),
         new Multiple.Element(number.as(wealthPerLevel((int)m_level.get() - 1)),
                              false),
         new Multiple.Element(number.as(wealthPerLevel((int)m_level.get())),
                              false),
         new Multiple.Element(number.as(wealthPerLevel((int)m_level.get() + 1)),
                              false));
    }

    if("weight".equals(inKey))
      return totalWeight();

    if("items".equals(inKey))
    {
      List<Multiple> items = new ArrayList<Multiple>();
      for(Name name : m_items)
      {
        Item item = getCampaign().getItem(name.get());
        items.add(new Multiple
                  (new Multiple.Element(name, false),
                   new Multiple.Element
                   (item != null ? new Name(item.getPlayerName()) : new Name(),
                    true),
                   new Multiple.Element
                   (item != null ? new Name(item.getDMName()) : new Name(),
                    true)));
      }

      return new ValueList<Multiple>(items);
    }

    return super.compute(inKey);
  }

  //........................................................................
  //------------------------- createItemsDocument --------------------------

  /**
   * Create a pdf document with all the users item.
   *
   * @param       inDM true if generating for dm, false if not
   *
   * @return      the pdf document
   *
   */
  // public PDFDocument createItemsDocument(boolean inDM)
  // {
  //   String title = "Items of " + getName() + (inDM ? " (DM)" : "");

  //   PDFDocument document = new PDFDocument(title, "items", inDM, false);

  //   // print all item information for available items
  //   document.add(new Title(title));

  //----- overview -------------------------------------------------------

  //----- counted & multiple ---------------------------------------------

  //   commands.clear();
  //   for(Entry<?> entry : allPossessions)
  //     addTypedCommand(commands, inDM, entry,
  //                     ValueGroup.ListCommand.Type.COUNTED);

  //   if(!commands.isEmpty())
  //     document.add(new Table("colored", "30:L(name)[Name];"
  //                            + "70:L(number)[Number]",
  //                            commands.toArray(new Object[0])));

  //   //......................................................................

  //   document.add(new Par());

  // //----- light ----------------------------------------------------------

  //   commands.clear();
  //   for(Entry<?> entry : allPossessions)
  //  addTypedCommand(commands, inDM, entry, ValueGroup.ListCommand.Type.LIGHT);

  //   if(!commands.isEmpty())
  //     document.add(new Table("colored", "60:L(name)[Name];20:L(bright)"
  //                            + "[Bright];20:L(shadowy)[Shadowy]",
  //                            commands.toArray(new Object[0])));

  //   //......................................................................

  //   document.add(new Par());

  //   //----- time ----------------------------------------------------------

  //   commands.clear();
  //   for(Entry<?> entry : allPossessions)
  //  addTypedCommand(commands, inDM, entry, ValueGroup.ListCommand.Type.TIMED);

  //   if(!commands.isEmpty())
  //     document.add(new Table("colored", "60:L(name)[Name];20:L(time)[Time]",
  //                            commands.toArray(new Object[0])));

  //   //......................................................................

  //   return document;
  // }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- add ----------------------------------

  /**
   * Add the given entry to the campaign entry.
   *
   * @param       inEntry the entry to add
   *
   * @return      true if added, false if not
   *
   */
  @Override
  public boolean add(@Nonnull CampaignEntry inEntry)
  {
    String name = inEntry.getName();
    List<Name> names = new ArrayList<Name>();
    for(Name item : m_items)
      if(name.equals(item.get()))
        return true;
      else
        names.add(item);

    names.add(m_items.newElement().as(name));
    m_items = m_items.as(names);

    changed();
    save();
    return true;
  }

  //........................................................................
  //------------------------------ updateKey -------------------------------

  /**
   * Update the any values that are related to the key with new data.
   *
   * @param       inKey the new key of the entry
   *
   */
  @Override
  public void updateKey(@Nonnull EntryKey<? extends AbstractEntry> inKey)
  {
    EntryKey parent = inKey.getParent();
    if(parent == null)
      return;

    EntryKey parentParent = parent.getParent();
    if(parentParent == null)
      return;

    m_campaign = m_campaign.as(new Name(parentParent.getID()),
                               new Name(parent.getID()));
  }

  //........................................................................

  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled. We do only
   * the value and the appearance here and let the base class handle the rest.
   *
   */
  // public void complete()
  // {
  //   super.complete();

  //   // if any of the possessions changed, then this one did as well
  //   for(EntryValue<Item> value : m_possessions)
  //     if(value instanceof EntryValue)
  //       if(value.get().isChanged())
  //       {
  //         changed();

  //         break;
  //       }
  // }

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
  // public boolean store(Storage<? extends AbstractEntry> inStorage,
  //                      long inStartPos, long inStartLine, long inEndPos,
  //                      long inEndLine)
  // {
  //   if(!super.store(inStorage, inStartPos, inStartLine, inEndPos, inEndLine))
  //     return false;

  //   // add all the possessions to the campaign
  //   for(EntryValue<Item> value : m_possessions)
  //     value.get().store(this);

  //   return true;
  // }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add an entry to the storage.
   *
   * @param       inEntry the entry to add
   * @param       inAfter the entry to add after or null to add at the beginning
   *
   * @return      true if added, false if not
   *
   * @undefined   never
   *
   */
  // public boolean add(Item inEntry, Item inAfter)
  // {
  //   if(inEntry == null || !(inEntry instanceof Item))
  //     return false;

  //   // remove from current storage
  //   if(!inEntry.getStorage().remove(inEntry))
  //     return false;

  //   int i = 0;
  //   if(inAfter != null)
  //     for(EntryValue<Item> value : m_possessions)
  //     {
  //       i++;

  //       if(value.get() == inAfter)
  //         break;
  //     }
  //   else
  //     i = 0;

  //   inEntry.store(this);

  //   if(m_possessions.add(i, new EntryValue<Item>(inEntry)))
  //   {
  //     changed();

  //     return write();
  //   }
  //   else
  //     return false;
  // }

  //........................................................................
  //-------------------------------- write ---------------------------------

  /**
   * Write the contents of the storage to a persistent place.
   *
   * @return      true if written, false if not
   *
   */
  // public boolean write()
  // {
  //   if(m_storage != null)
  //     return m_storage.write();

  //   Log.warning("cannot write item '" + getID() + "' (" + getName()
  //               + ") because of missing storage");

  //   return false;
  // }

  //........................................................................
  //------------------------------- remove ---------------------------------

  /**
   * Remove an entry from the storage.
   *
   * @param       inEntry the entry to remove
   *
   * @return      true if removed, false if not
   *
   * @undefined   never
   *
   */
  // public boolean remove(Item inEntry)
  // {
  //   for(EntryValue<Item> value : m_possessions)
  //     if(value.get() == inEntry)
  //       return m_possessions.remove(value);

  //   return false;
  // }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //---------------------------- addItemCommand ----------------------------

  /**
   * Add the item to the list of commands.
   *
   * @param       ioCommands the commands to add to
   * @param       inDM       true if setting for dm, false if not
   * @param       inEntry    the entry to set
   *
   * @undefined   never
   *
   */
  // protected void addItemCommand(List<Object> ioCommands, boolean inDM,
  //                               Entry inEntry)
  // {
  //   assert ioCommands != null : "must have a command here";
  //   assert inEntry    != null : "must have an entry here";

  //   if(!(inEntry instanceof Item))
  //     return;

  //   Item                    item  = (Item)inEntry;
  //   ValueGroup.PrintCommand print = item.printCommand(inDM, false);

  //   if(inDM)
  //     ioCommands.add(item.getCommand(print, Item.LIST_COMMAND_DM, inDM));
  //   else
  //     ioCommands.add(item.getCommand(print, Item.LIST_COMMAND, inDM));
  // }

  //........................................................................
  //--------------------------- addTypedCommand ---------------------------

  /**
   * Add the weapon to the list of commands.
   *
   * @param       ioCommands the commands to add to
   * @param       inDM       true if setting for dm, false if not
   * @param       inEntry    the entry to set
   * @param       inType     the type to set for
   *
   * @undefined   never
   *
   */
  // protected void addTypedCommand(List<Object> ioCommands, boolean inDM,
  //                                AbstractEntry inEntry,
  //                                ValueGroup.ListCommand.Type inType)
  // {
  //   assert ioCommands != null : "must have a command here";
  //   assert inEntry    != null : "must have an entry here";

  //   // only treat items
  //   if(!(inEntry instanceof Item))
  //     return;

  //   Item item = (Item)inEntry;

  //   ValueGroup.ListCommand commands = item.getListCommands(inDM);

  //   if(commands.hasCommand(inType))
  //     Collections.addAll(ioCommands, commands.getList(inType).toArray());
  // }

  //........................................................................

  //........................................................................
}
