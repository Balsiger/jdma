/*****************************************************************************
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.entries.indexes.GroupedIndex;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.output.ascii.ASCIIDocument;
import net.ixitxachitls.dma.values.Date;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Group;
import net.ixitxachitls.dma.values.ISBN;
import net.ixitxachitls.dma.values.ISBN13;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Price;
import net.ixitxachitls.dma.values.Reference;
import net.ixitxachitls.dma.values.Selection;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.formatters.Formatter;
import net.ixitxachitls.dma.values.formatters.LinkFormatter;
import net.ixitxachitls.dma.values.formatters.ListFormatter;
import net.ixitxachitls.dma.values.formatters.MultipleFormatter;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.BaseCommand;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.output.commands.Subtitle;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the entry for base products, the basic description of a
 * product available.
 *
 * @file          BaseProduct.java
 *
 * @author        balsiger@ixitxachils.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class BaseProduct extends BaseEntry
{
  //----------------------------------------------------------------- nested

  //----- producers --------------------------------------------------------

  /** The producers of products. */
  private static final @Nonnull String []PRODUCERS =
    Config.get("/game/product.producers", new String []
      {
        "WTC",
        "TSR",
        "Paizo",
        "Celtos",
        "Cloud Kingdom Games",
        "Dark Platypus Studio",
        "Devil's Due",
        "Do Gooder Press",
        "Dork Storm Press",
        "Dover",
        "Dwarven Forge",
        "Fantasy Flight Games",
        "Fantasy Productions",
        "Fenryll",
        "Gale Force Nine",
        "Global Games Europe",
        "Goodman Games",
        "Heel",
        "Henchman Publishing",
        "K&C",
        "King of the Castle",
        "Laurin",
        "Litko",
        "Looney Labs",
        "Mega Miniatures",
        "Magnificient Egos",
        "Malhavoc Press",
        "Mirrorstone",
        "Necromancer Games",
        "Open Mind Games",
        "Pegasus Press",
        "Rackham",
        "Ral Partha",
        "Reaper",
        "RPG International",
        "RPGA",
        "Sterling' Publishing",
        "tosa",
        "Toy Vault",
        "White Wolf",
        "Wiley",
      });

  //........................................................................
  //----- part -------------------------------------------------------------

  /** The product parts. */
  public enum Part implements EnumSelection.Named
  {
    /** A game board. */
    BOARD("Board"),
    /** A normal book. */
    BOOK("Book"),
    /** A booklet. */
    BOOKLET("Booklet"),
    /** Some kind of box. */
    BOX("Box"),
    /** A playing card or an item card. */
    CARD("Card"),
    /** A cd with music or programs. */
    CD("CD"),
    /** Some kind of counter. */
    COUNTER("Counter"),
    /** A cover for a book or booklet (separate). */
    COVER("Cover"),
    /** Some dice, normal or special. */
    DICE("Dice"),
    /** A flyer, basically a piece of paper with something on it. */
    FLYER("Flyer"),
    /** A fold to organize things in it. */
    FOLDER("Folder"),
    /** Like a cover with a booklet it in, but the cover can be folded out. */
    GATEFOLD("Gatefold"),
    /** A magnet with some special design. */
    MAGNET("Magnet"),
    /** A map used for playing (battle map) or for showing the lay of the
     *  environment. */
    MAP("Map"),
    /** Some miniature figure that can be used for play or for decoration. */
    MINIATURE("Miniature"),
    /** Something not covered in any of the other categories. */
    MISC("Misc"),
    /** An transparent or partially transparent sheet that is used as an
     *  overlay over something else (e.g. a map). */
    OVERLAY("Overlay"),
    /** A pack o other things, usually cards. */
    PACK("Pack"),
    /** A page of paper. */
    PAGE("Page"),
    /** A playing piece other than a miniature. */
    PLAYING_PIECE("Playing Piece"),
    /** A poster other than a map. */
    POSTER("Poster"),
    /** A screen the DM can be used to not be observed too closely. */
    SCREEN("Screen"),
    /** A sheet of paper, e.g. a handout. */
    SHEET("Sheet"),
    /** A sticker. */
    STICKER("Sticker");

    /** The value's name. */
    private @Nonnull String m_name;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     *
     */
    private Part(@Nonnull String inName)
    {
      m_name = constant("product.part", inName);
    }

    /**
     * Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /**
     * Convert to a human readable string.
     *
     * @return the converted string
     *
     */
    public @Nonnull String toString()
    {
        return m_name;
    }
  };

  //........................................................................
  //----- layout -----------------------------------------------------------

  /** The product layouts. */
  public enum Layout implements EnumSelection.Named
  {
    /** A product with full color on most pages. */
    FULL_COLOR("Full Color"),
    /** A product that uses 4 colors on most pages. */
    FOUR_COLOR("4 Color"),
    /** A product that uses a two color print. */
    TWO_COLOR("2 Color"),
    /** A product that is basically black & white but has a color cover. */
    COLOR_COVER("Color Cover"),
    /** The product is completely in black & white. */
    BLACK_AND_WHITE("Black & White"),
    /** The product is mixed between different layout, with none really
     *  dominant (otherwise use the dominant layout). */
    MIXED("Mixed");

    /** The value's name. */
    private @Nonnull String m_name;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     *
     */
    private Layout(@Nonnull String inName)
    {
      m_name = constant("product.layout", inName);
    }

    /**
     * Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /**
     * Convert to a human readable string.
     *
     * @return the converted string
     *
     */
    public @Nonnull String toString()
    {
        return m_name;
    }
  };

  //........................................................................
  //----- system -----------------------------------------------------------

  /** The game system. */
  public enum System implements EnumSelection.Named
  {
    /** No game system, e.g. for novels. */
    NONE("None", null),
    /** Dungeons & Dragons, original edition. */
    DnD_1ST("D&D 1st", "D&D 1st"),
    /** Advanced Dungeon & Dragons, first edition. */
    ADnD_1ST("AD&D 1st", "AD&D 1st"),
    /** Advanced Dungeon & Dragons, second edition, together with the Saga
     * rules. */
    ADnD_2ND_SAGA("AD&D 2nd & Saga", "AD&D 2nd"),
    /** Advanced Dungeon & Dragons, second edition. */
    ADnD_2ND("AD&D 2nd", "AD&D 2nd"),
    /** Advanced Dungeon & Dragons, second but revised edition (new logo). */
    ADnD_REVISED("AD&D revised", "AD&D 2nd"),
    /** Dungeons & Dragons, third edition. */
    DnD_3RD("D&D 3rd", "D&D 3rd"),
    /** Dungeons & Dragons, version 3.5. */
    DnD_3_5("D&D 3.5", "D&D 3rd"),
    /** Dungeon & Dragons, fourth edition. */
    DnD_4("D&D 4th", "D&D 4th"),
    /** Games with d20 modern rules. */
    D20_MODERN("d20 Modern", null),
    /** Games with d20 future rules. */
    D20_FUTURE("d20 Future", null),
    /** Games with the d20 fantasy rules. */
    D20("d20", null),
    /** Games with the science-fition rules of Alternaty. */
    ALTERNITY("Alternity", null),
    /** Amazing Engine games. */
    AMAZING_ENGINE("Amazing Engine", null),
    /** The Blood Wars card game rules. */
    BLOOD_WARS("Blood Wars", null),
    /** Games using the Chaosium rules. */
    CHAOSIUM("Chaosium", null),
    /** Miniatures from the Dark Heaven line. */
    DARK_HEAVEN("Dark Heaven", null),
    /** Dragon Dice game system. */
    DRAGON_DICE("Dragon Dice", null),
    /** Dragon Strike game system. */
    DRAGON_STRIKE("Dragon Strike", null),
    /** Duel Master games. */
    DUEL_MASTER("Duel Master", null),
    /** Books with the Endless Quest system. */
    ENDLESS_QUEST("Endless Quest", null),
    /** First Quest introductory games. */
    FIRST_QUEST("First Quest", null),
    /** Games with the Gamma World rule. */
    GAMMA_WORLD("Gamma World", null),
    /** Games with the Ganbusters rules. */
    GANGBUSTERS("Gangbusters", null),
    /** The legend of the Five Rings oriental fantasy rules. */
    LEGEND_OF_THE_FIVE_RINGS("Legend of the Five Rings", null),
    /** Card games with the Magic: the Gathering rules. */
    MAGIC_THE_GAHTERING("Magic: The Gathering", null),
    /** The Marvel Super Dice rules. */
    MARVEL_SUPER_DICE("Marvel Super Dice", null),
    /** Games using the Marvel Super Heroes rules. */
    MARVEL_SUPER_HEROES("Marvel Super Heroes", null),
    /** Card games according to the MLB Showdown 2002 rules. */
    MLB_SHOWDOWN_2002("MLB Showdown 2002", null),
    /** Card games according to the MLB Showdown 2003 rules. */
    MLB_SHOWDOWN_2003("MLB Showdown 2003", null),
    /** Card games according to the MLB Showdown rules. */
    MLB_SHOWDOWN("MLB Showdown", null),
    /** Books with the Neopets rules. */
    NEOPETS("Neopets", null),
    /** Games with 1 on 1 rules. */
    ONE_ON_ONE("1 on 1", null),
    /** Games inside the Pokemon universe. */
    POKEMON("Pokemon", null),
    /** Games using the Saga narrative rules. */
    SAGA("Saga", null),
    /** Games using special rules (e.g. not covered by other systems). */
    SPECIAL("Special", null),
    /** The spellfire card game. */
    SPELLFIRE("Spellfire", null),
    /** The star wars trading card game rules. */
    STAR_WARS_TCG("Star Wars TCG", null),
    /** Games for the star wars role playing game. */
    STAR_WARS("Star Wars", null),
    /** Books with the super endless quest system. */
    SUPER_ENDLESS_QUEST("Super Endless Quest", null),
    /** Games for the Sword & Sorcery rules. */
    SWORD_SORCERY("Sword & Sorcery", null),
    /** Games using the Terror Tracks system. */
    TERROR_TRACKS("Terror Tracks", null),
    /** Gemes using the Terror T.R.A.X. system. */
    TERROR_TRAX("Terror T.R.A.X.", null),
    /** Games for Wild Space. */
    WILD_SPACE("Wild Space", null),
    /** Games for World War II. */
    WORLD_WAR_II("World War II", null),
    /** Games for the XXVC sci-fi game. */
    XXVC("XXVC", null);

    /** The value's name. */
    private @Nonnull String m_name;

    /** The group of styles, if any. */
    private @Nullable String m_group;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     * @param inGroup    the group (if any) to use when sorting by system.
     *
     */
    private System(@Nonnull String inName, @Nullable String inGroup)
    {
      m_name  = constant("system.name",  inName);
      if(inGroup != null)
        m_group = constant("system.group", inName, inGroup);
    }

    /**
     * Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /**
     * Get the group of the style, if any).
     *
     * @return the group of the style
     *
     */
    public @Nullable String getGroup()
    {
      return m_group;
    }

    /**
     * Convert to a human readable string.
     *
     * @return the converted string
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }

    /**
     * Get the enum with the given name (case insensitive).
     *
     * @param  inName the name to look for
     *
     * @return the enum value found or null if not found
     *
     */
    public static @Nullable System valueOfIgnoreCase(@Nonnull String inName)
    {
      // check ignoring case
      try
      {
        return valueOf(inName);
      }
      catch(java.lang.IllegalArgumentException e)
      { /* ignore it. */ }

      for(System value : values())
        if(inName.equalsIgnoreCase(value.toString()))
          return value;

      return null;
    }
  };

  //........................................................................
  //----- product type -----------------------------------------------------

  /** The product types. */
  public enum ProductType implements EnumSelection.Named
  {
    /** A game accessory, e.g. an optional product enhancing the game. */
    ACCESSORY("Accessory", "Accessories"),
    /** A game adventure. */
    ADVENTURE("Adventure", "Adventures"),
    /** A board game, usually outside of rpg. */
    BOARD_GAME("Board Game", null),
    /** A booster pack for a trading card or miniature game. */
    BOOSTER_PACK("Booster Pack", null),
    /** A calendar (no game). */
    CALENDAR("Calendar", null),
    /** An expansion to a campaign. */
    CAMPAIGN_EXPANSION("Campaign Expansion", "Accessories"),
    /** The base rules and description of a campaign. */
    CAMPAIGN_SETTING("Campaign Setting", "Accessories"),
    /** A card game. */
    CARD_GAME("Card Game", null),
    /** A bunch of cards that are part of something else. */
    CARDS("Cards", null),
    /** A catalog listing products. */
    CATALOG("Catalog", "Others"),
    /** A collection of other things. */
    COLLECTION("Collection", null),
    /** A comics book. */
    COMICS("Comics", "Comics"),
    /** A book with cooking recipies. */
    COOKBOOK("Cookbook", null),
    /** One or more dice. */
    DICE("Dice", null),
    /** Additional information in electronic form (not programs). */
    ELECTRONIC_ACCESSORY("Electronic Accessory", null),
    /** A guide for a game or something else. */
    GUIDE("Guide", null),
    /** A gaming magazine. */
    MAGAZINE("Magazine", "Magazines"),
    /** One ore more miniatures. */
    MINIATURE("Miniature", null),
    /** A compendium with monsters. */
    MONSTER_COMPENDIUM("Monster Compendium", "Accessories"),
    /** A novel. */
    NOVEL("Novel", "Novels"),
    /** A promotional product. */
    PROMOTION("Promotion", "Others"),
    /** A book with basic rules (usually the core rules books). */
    RULEBOOK("Rulebook", "Rulebooks"),
    /** A supplement with addtional rules. */
    RULES_SUPPLEMENT("Rules Supplement", "Accessories"),
    /** A software tool to support the game. */
    SOFTWARE("Software", null),
    /** A source book about rules and descriptions of real world things. */
    SOURCEBOOK("Sourcebook", "Accessories"),
    /** Something not covered by all the other types. */
    SPECIAL_BOOK("Special Book", "Others");

    /** The value's name. */
    private @Nonnull String m_name;

    /** The group of styles, if any. */
    private @Nullable String m_group = null;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inGroup    the group used for sorting
     *
     */
    private ProductType(@Nonnull String inName, @Nullable String inGroup)
    {
      m_name  = constant("product.type.name",  inName);
      if(inGroup != null)
        m_group = constant("product.type.group", inName, inGroup);
    }

    /**
     * Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /**
     * Get the group of the style, if any).
     *
     * @return the group of the style
     *
     */
    public @Nullable String getGroup()
    {
      return m_group;
    }

    /**
     * Convert to a human readable string.
     *
     * @return the converted string
     *
     */
    public String toString()
    {
      return m_name;
    }
  };

  //........................................................................
  //----- style ------------------------------------------------------------

  /** The product styles. */
  public enum Style implements EnumSelection.Named
  {
    /** A booklet, i.e. a small, stapled book. */
    BOOKLET("Booklet", null),
    /** A large box (A4 or bigger). */
    BOX("Box", "Boxes"),
    /** A set of cards. */
    CARDS("Cards", null),
    /** A sheet of paper. */
    FLYER("Flyer", null),
    /** A folder to store other things. */
    FOLDER("Folder", null),
    /** A hardcover book. */
    HARDCOVER("Hardcover", null),
    /** A map. */
    MAP("Map", null),
    /** A medium box, roughly A5 or similar. */
    MEDIUM_BOX("Medium Box", "Medium Boxes"),
    /** A pack of cards or miniatures. */
    PACK("Pack", null),
    /** A normal paperback book. */
    PAPERBAKC("Paperback", null),
    /** A poster. */
    POSTER("Poster", null),
    /** A screen for the DM to guard his secrets. */
    SCREEN("Screen", null),
    /** A bunch of sheets. */
    SHEETS("Sheets", null),
    /** A small box, usually for miniatures or similar. */
    SMALL_BOX("Small Box", "Small Boxes"),
    /** A bound book with a soft cover. */
    SOFT_COVER("Soft Cover", null),
    /** A sticker. */
    STICKER("Sticker", null);

    /** The value's name. */
    private @Nonnull String m_name;

    /** The group of styles, if any. */
    private @Nullable String m_group;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inGroup    the group to use for sorting
     *
     */
    private Style(@Nonnull String inName, @Nullable String inGroup)
    {
      m_name  = constant("product.style.name",  inName);
      if(inGroup != null)
        m_group = constant("product.style.group", inName, inGroup);
    }

    /**
     * Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /**
     * Get the group of the style, if any).
     *
     * @return the group of the style
     *
     */
    public @Nullable String getGroup()
    {
      return m_group;
    }

    /**
     * Convert to a human readable string.
     *
     * @return the converted string
     *
     */
    public @Nullable String toString()
    {
        return m_name;
    }
  };

  //........................................................................
  //----- audience --------------------------------------------------------

  /** The audiences for products. */
  public enum Audience implements EnumSelection.Named
  {
    /** Material indented for the DM only. */
    DM("DM"),
    /** Material targeted mostly to players. */
    PLAYER("Player"),
    /** Material that is open to all. */
    ALL("All");

    /** The value's name. */
    private @Nonnull String m_name;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     *
     */
    private Audience(@Nonnull String inName)
    {
      m_name = constant("audiences", inName);
    }

    /**
     * Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /**
     * Convert to a human readable string.
     *
     * @return the converted string
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  };

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //----------------------------- BaseProduct ------------------------------

  /**
   * This is the internal, default constructor.
   *
   * @param       inData all the available data
   *
   */
  protected BaseProduct(@Nonnull DMAData inData)
  {
    super(TYPE, inData);
  }

  //........................................................................
  //----------------------------- BaseProduct ------------------------------

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base product
   * @param       inData all the available data
   *
   */
  public BaseProduct(@Nonnull String inName, @Nonnull DMAData inData)
  {
    super(inName, TYPE, inData);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The printer for printing the whole base character. */
  public static final Print s_pagePrint =
    new Print("$title "
              + "${as pdf} ${as text} ${as dma}"
              + "$clear $files\n"
              + " $subtitle "
              + "${short description} $description"
              + "$par "
              + "%base %synonyms "
              + "%notes %owners "
              + "%system %audience %style %producer %layout %{product type} "
              + "%author %editor %cover %cartography "
              + "%illustrations %typography %management "
              + "%date %ISBN %ISBN13 "
              + "%pages %series %number %volume "
              + "%price %contents %requirements"
              // admin
              + "%references %file %errors"
              );

  /** The printer for printing in a list. */
  public static final ListPrint s_listPrint =
    new ListPrint("1:L(label);5:L(id)[ID];20(producttitle)[Title];"
                  + "1:L(system)[System];1:L(worlds)[Worlds];"
                  + "1:L(short)[Short Description]",
                  "$label $listlink", null, "$name", "$system",
                  "$worlds", "${short description}");

  /** The type of this entry. */
  public static final BaseType<BaseProduct> TYPE =
    new BaseType<BaseProduct>(BaseProduct.class);

  //----- title ------------------------------------------------------------

  /** The title of the product. */
  @Key("title")
  protected @Nonnull Text m_title =
    new Text().withEditType("string[title]").withRelated("leader");

  //........................................................................
  //----- leader -----------------------------------------------------------

  /** The leader of the product, any 'a', 'the' and the like. */
  @Key("leader")
  protected @Nonnull Text m_leader =
    new Text("").withEditType("string[leader]").withRelated("title");

  //........................................................................
  //----- subtitle ---------------------------------------------------------

  /** The sub title of the product. */
  @Key("subtitle")
  protected @Nonnull Text m_subtitle = new Text();

  //........................................................................
  //----- notes ------------------------------------------------------------

  /** Notes about the product. */
  @Key("notes")
  protected @Nonnull Text m_notes = new Text();

  //........................................................................
  //----- authors ----------------------------------------------------------

  /** The formatter for a person. */
  protected static final Formatter<Text> s_personFormatter =
    new LinkFormatter<Text>("/products/persons/");

  /** The formatter for a job. */
  protected static final Formatter<Name> s_jobFormatter =
    new LinkFormatter<Name>("/products/jobs/");

  /** The formatter for a complete person. */
  protected static final Formatter<Multiple> s_nameFormatter =
    new MultipleFormatter<Multiple>(null, null, " (", ")");

  /** The formatter for a complete person. */
  protected static final Formatter<ValueList<Multiple>> s_listFormatter =
    new ListFormatter<ValueList<Multiple>>("; ");

  /** All the authors of the product. */
  @Key("author")
  protected @Nonnull ValueList<Multiple> m_authors =
    new ValueList<Multiple>(new Multiple(new Multiple.Element []
      { new Multiple.Element
        (new Text().withFormatter(s_personFormatter)
         .withEditType("autostring(persons/author)[name]"),
         false),
        new Multiple.Element
        (new Name().withFormatter(s_jobFormatter)
         .withEditType("autoname(jobs/author|name)[job]")
         .withRelated("name"),
         true, " ", null) })
                            .withFormatter(s_nameFormatter))
    .withFormatter(s_listFormatter);

  static
  {
    // the index for all persons
    s_indexes.put("persons",
                  new GroupedIndex("Persons", BaseProduct.TYPE, 1)
                  {
                    private static final long serialVersionUID = 1L;

                    public Set<String> names(@Nonnull Set<String> ioCollected,
                                             @Nonnull DMAData inData,
                                             @Nonnull String []inGroups)
                    {
                      for(BaseProduct product
                            : inData.getEntriesList(BaseProduct.TYPE))
                        product.collectPersons(ioCollected, null, null);

                      return ioCollected;
                    }

                    public boolean matches(@Nonnull String []inGroups,
                                           @Nonnull AbstractEntry inProduct)
                    {
                      if(!(inProduct instanceof BaseProduct))
                        return false;

                      Set<String> names = new HashSet<String>();
                      ((BaseProduct)inProduct)
                        .collectPersons(names, null, null);
                     for(String name : names)
                        if(name.equalsIgnoreCase(inGroups[0]))
                          return true;

                      return false;
                    }
                    // CHECKSTYLE:OFF
                  }.withEditable());
    // CHECKSTYLE:ON


    // the index for all jobs
    s_indexes.put("jobs",
                  new GroupedIndex("Jobs", BaseProduct.TYPE, 2)
                  {
                    private static final long serialVersionUID = 1L;

                    public Set<String> names(@Nonnull Set<String> ioCollected,
                                             @Nonnull DMAData inData,
                                             @Nonnull String []inGroups)
                    {
                      if(inGroups.length == 0)
                        for(BaseProduct product
                              : inData.getEntriesList(BaseProduct.TYPE))
                          product.collectJobs(ioCollected, null, null);
                      else
                        for(BaseProduct product
                              : inData.getEntriesList(BaseProduct.TYPE))
                          product.collectPersons(ioCollected, inGroups[0],
                                                 null);

                      return ioCollected;
                    }

                    public boolean matches(@Nonnull String []inGroups,
                                           @Nonnull AbstractEntry inProduct)
                    {
                      if(!(inProduct instanceof BaseProduct))
                        return false;

                      String job = inGroups[0];
                      String name = inGroups[1];

                      Set<String> jobs = new HashSet<String>();
                      ((BaseProduct)inProduct).collectJobs(jobs, name, job);
                      return !jobs.isEmpty();
                    }
                    // CHECKSTYLE:OFF
                  }.withEditable());
        // CHECKSTYLE:ON
  }

  //........................................................................
  //----- editors ----------------------------------------------------------

  /** All the editors of the product. */
  @Key("editor")
  protected @Nonnull ValueList<Multiple> m_editors =
    new ValueList<Multiple>(new Multiple(new Multiple.Element []
      { new Multiple.Element(new Text()
                             .withFormatter(s_personFormatter)
                             .withEditType
                             ("autostring(persons/editor)[name]"),
                             false),
        new Multiple.Element(new Name()
                             .withFormatter(s_jobFormatter)
                             .withEditType("autoname(jobs/category|name)"
                                           + "[job]"),
                             true) }).withFormatter(s_nameFormatter))
    .withFormatter(s_listFormatter);

  //........................................................................
  //----- cover ------------------------------------------------------------

  /** All the cover artists. */
  @Key("cover")
  protected @Nonnull ValueList<Multiple> m_cover =
    new ValueList<Multiple>(new Multiple(new Multiple.Element []
      { new Multiple.Element(new Text()
                             .withFormatter(s_personFormatter)
                             .withEditType("autostring(persons/cover)[name]"),
                             false),
        new Multiple.Element(new Name().
                             withFormatter(s_jobFormatter)
                             .withEditType("autoname(jobs/cover|name)[job]"),
                             true) }).withFormatter(s_nameFormatter))
    .withFormatter(s_listFormatter);

  //........................................................................
  //----- cartographers ----------------------------------------------------

  /** The cartographers for the product. */
  @Key("cartography")
  protected @Nonnull ValueList<Multiple> m_cartographers =
    new ValueList<Multiple>(new Multiple(new Multiple.Element []
      { new Multiple.Element(new Text()
                             .withFormatter(s_personFormatter)
                             .withEditType("autostring(persons/cartographer)"
                                           + "[name]"),
                             false),
        new Multiple.Element(new Name()
                             .withFormatter(s_jobFormatter)
                             .withEditType("autoname(jobs/cartographer|name)"
                                           + "[job]"),
                             true) }).withFormatter(s_nameFormatter))
    .withFormatter(s_listFormatter);

  //........................................................................
  //----- illustrators -----------------------------------------------------

  /** The illustration artists for the product. */
  @Key("illustrations")
  protected @Nonnull ValueList<Multiple> m_illustrators =
    new ValueList<Multiple>(new Multiple(new Multiple.Element []
      { new Multiple.Element(new Text()
                             .withFormatter(s_personFormatter)
                             .withEditType("autostring(persons/illustrator)"
                                           + "[name]"),
                             false),
        new Multiple.Element(new Name()
                             .withFormatter(s_jobFormatter)
                             .withEditType("autoname(jobs/illustrator|name)"
                                           + "[job]"),
                             true) }).withFormatter(s_nameFormatter))
    .withFormatter(s_listFormatter);

  //........................................................................
  //----- typographers -----------------------------------------------------

  /** The typographers for this product. */
  @Key("typography")
  protected @Nonnull ValueList<Multiple> m_typographers =
    new ValueList<Multiple>(new Multiple(new Multiple.Element []
      { new Multiple.Element(new Text()
                             .withFormatter(s_personFormatter)
                             .withEditType("autostring(persons/typographer)"
                                           + "[name]"),
                             false),
        new Multiple.Element(new Name()
                             .withFormatter(s_jobFormatter)
                             .withEditType("autoname(jobs/typographer|name)"
                                           + "[job]"),
                             true) }).withFormatter(s_nameFormatter))
    .withFormatter(s_listFormatter);

  //........................................................................
  //----- managers ---------------------------------------------------------

  /** All the mangers and other people involved in the product creation. */
  @Key("management")
  protected @Nonnull ValueList<Multiple> m_managers =
    new ValueList<Multiple>(new Multiple(new Multiple.Element []
      { new Multiple.Element(new Text()
                             .withFormatter(s_personFormatter)
                             .withEditType("autostring(persons/manager)"
                                           + "[name]"), false),
        new Multiple.Element(new Name()
                             .withFormatter(s_jobFormatter)
                             .withEditType("autoname(jobs/manager|name)"
                                           + "[job]"),
                             true) }).withFormatter(s_nameFormatter))
    .withFormatter(s_listFormatter);

  //........................................................................
  //----- date -------------------------------------------------------------

  /** The formatter for the date. */
  protected static final Formatter<Date> s_dateFormatter =
    new LinkFormatter<Date>("/products/dates/");

  /** The date (month and year) the product was released. */
  @Key("date")
  protected @Nonnull Date m_date = new Date().withFormatter(s_dateFormatter);

  static
  {
    s_indexes.put("dates", new GroupedIndex("Dates", TYPE, 2)
      {
        private static final long serialVersionUID = 1L;

        public Set<String> names(@Nonnull Set<String> ioCollected,
                                 @Nonnull DMAData inData,
                                 @Nonnull String []inGroups)
        {
          if(inGroups.length == 0)
            for(BaseProduct product : inData.getEntriesList(TYPE))
              if(product.m_date.isDefined())
                ioCollected.add("" + product.m_date.getYear());
              else
                ioCollected.add(Value.UNDEFINED);
          else
            if(inGroups[0].equals(Value.UNDEFINED))
              ioCollected.add(Value.UNDEFINED);
            else
              for(BaseProduct product : inData.getEntriesList(TYPE))
                if(inGroups[0].equals("" + product.m_date.getYear())
                   && product.m_date.getMonth() >= 0)
                  if(product.m_date.getMonth() == 0)
                    ioCollected.add(Value.UNDEFINED);
                  else
                    ioCollected.add(product.m_date.getMonthAsString());

          return ioCollected;
        }

        public boolean matches(@Nonnull String []inGroups,
                               @Nonnull AbstractEntry inProduct)
        {
          if(!(inProduct instanceof BaseProduct))
            return false;

          BaseProduct product = (BaseProduct)inProduct;

          String year = inGroups[0];
          String month = inGroups[1];

          if(year.equals("" + product.m_date.getYear())
             || (Value.UNDEFINED.equals(year) && !product.m_date.isDefined()))
            return product.m_date.getMonth() <= 0
              || month.equals(product.m_date.getMonthAsString());

          return false;
        }
      });
  }

  //........................................................................
  //----- isbn -------------------------------------------------------------

  /** The product's ISBN number, if it has one. */
  @Key("ISBN")
  protected @Nonnull ISBN m_isbn = new ISBN();

  //........................................................................
  //----- isbn 13 ----------------------------------------------------------

  /** The product's ISBN 13 number, if it has one. */
  @Key("ISBN13")
  protected @Nonnull ISBN13 m_isbn13 = new ISBN13();

  //........................................................................
  //----- pages ------------------------------------------------------------

  /** The formatter for the pages. */
  protected static final Formatter<Number> s_pageFormatter =
    new LinkFormatter<Number>("/products/pages/");

  /** The grouping for the pages. */
  protected static final Group<Number, Long, String> s_pageGroup =
    new Group<Number, Long, String>(new Group.Extractor<Number, Long>()
      {
        public Long extract(@Nonnull Number inValue)
        {
          return inValue.get();
        }
      }, new Long [] { 5L, 10L, 20L, 25L, 50L, 100L, 200L, 250L, 300L, 400L,
                       500L, },
                                    new String []
      { "5", "10", "20", "25", "50", "100", "200", "250", "300", "400", "500",
        "500+", }, "$undefined");

  /** The total number of pages of the product. */
  @SuppressWarnings("unchecked")
  @Key("pages")
  protected @Nonnull Number m_pages =
    new Number(0, Integer.MAX_VALUE).withFormatter(s_pageFormatter)
    .withGrouping(s_pageGroup);

  static
  {
    s_indexes.put("pages", new GroupedIndex("Pages", TYPE, 1, s_pageGroup)
      {
        private static final long serialVersionUID = 1L;

        public Set<String> names(@Nonnull Set<String> ioCollected,
                                 @Nonnull DMAData inData,
                                 @Nonnull String []inGroups)
        {
          for(BaseProduct product : inData.getEntriesList(TYPE))
            ioCollected.add(product.m_pages.group());

          return ioCollected;
        }

        public boolean matches(@Nonnull String []inGroups,
                               @Nonnull AbstractEntry inProduct)
        {
          if(!(inProduct instanceof BaseProduct))
            return false;

          BaseProduct product = (BaseProduct)inProduct;
          return inGroups[0].equalsIgnoreCase(product.m_pages.group());
        }
      });
  }

  //........................................................................
  //----- system -----------------------------------------------------------

  /** The formatter for the system. */
  protected static final Formatter<EnumSelection<System>> s_systemFormatter =
    new LinkFormatter<EnumSelection<System>>("/product/systems/");

  /** The game system of the product. */
  @Key("system")
  protected @Nonnull EnumSelection<System> m_system =
    new EnumSelection<System>(System.class).withFormatter(s_systemFormatter);

  static
  {
    s_indexes.put("systems", new GroupedIndex("Systems", TYPE, 1)
      {
        private static final long serialVersionUID = 1L;

        public Set<String> names(@Nonnull Set<String> ioCollected,
                                 @Nonnull DMAData inData,
                                 @Nonnull String []inGroups)
        {
          for(BaseProduct product : inData.getEntriesList(TYPE))
            ioCollected.add(product.m_system.toString(false));

          return ioCollected;
        }

        public boolean matches(@Nonnull String []inGroups,
                               @Nonnull AbstractEntry inProduct)
        {
          if(!(inProduct instanceof BaseProduct))
            return false;

          BaseProduct product = (BaseProduct)inProduct;

          String system = inGroups[0];

          return system.equals(product.m_system.toString(false));
        }
        // CHECKSTYLE:OFF
      }.withImages());
    // CHECKSTYLE:ON
  }

  //........................................................................
  //----- audience ---------------------------------------------------------

  /** The formatter for the audience. */
  protected static final Formatter<EnumSelection<Audience>>
    s_audienceFormatter =
    new LinkFormatter<EnumSelection<Audience>>("/product/audiences/");

  /** The intended audience of the product. */
  @Key("audience")
  protected @Nonnull EnumSelection<Audience> m_audience =
    new EnumSelection<Audience>(Audience.class)
    .withFormatter(s_audienceFormatter);

  static
  {
    s_indexes.put("audiences", new GroupedIndex("Audiences", TYPE, 1)
      {
        private static final long serialVersionUID = 1L;

        public Set<String> names(@Nonnull Set<String> ioCollected,
                                 @Nonnull DMAData inData,
                                 @Nonnull String []inGroups)
        {
          for(BaseProduct product : inData.getEntriesList(TYPE))
            ioCollected.add(product.m_audience.toString(false));

          return ioCollected;
        }

        public boolean matches(@Nonnull String []inGroups,
                               @Nonnull AbstractEntry inProduct)
        {
          if(!(inProduct instanceof BaseProduct))
            return false;

          BaseProduct product = (BaseProduct)inProduct;

          String audience = inGroups[0];

          return audience.equals(product.m_audience.toString(false));
        }
        // CHECKSTYLE:OFF
      }.withImages());
    // CHECKSTYLE:ON
  }

  //........................................................................
  //----- type -------------------------------------------------------------

  /** The formatter for the type. */
  protected static final Formatter<EnumSelection<ProductType>>
    s_typeFormatter =
    new LinkFormatter<EnumSelection<ProductType>>("/products/types/");

  /** The type of product. */
  @Key("product type")
  protected @Nonnull EnumSelection<ProductType> m_productType =
    new EnumSelection<ProductType>(ProductType.class)
    .withFormatter(s_typeFormatter);

  static
  {
    s_indexes.put("types", new GroupedIndex("Types", TYPE, 1)
      {
        private static final long serialVersionUID = 1L;

        public Set<String> names(@Nonnull Set<String> ioCollected,
                                 @Nonnull DMAData inData,
                                 @Nonnull String []inGroups)
        {
          for(BaseProduct product : inData.getEntriesList(TYPE))
            ioCollected.add(product.m_productType.toString(false));

          return ioCollected;
        }

        public boolean matches(@Nonnull String []inGroups,
                               @Nonnull AbstractEntry inProduct)
        {
          if(!(inProduct instanceof BaseProduct))
            return false;

          BaseProduct product = (BaseProduct)inProduct;

          String type = inGroups[0];

          return type.equals(product.m_productType.toString(false));
        }
        // CHECKSTYLE:OFF
      }.withImages());
    // CHECKSTYLE:ON
  }

  //........................................................................
  //----- style ------------------------------------------------------------

  /** The formatter for the style. */
  protected static final Formatter<EnumSelection<Style>> s_styleFormatter =
    new LinkFormatter<EnumSelection<Style>>("/products/styles/");

  /** The style of the product, its general outlook. */
  @Key("style")
  protected @Nonnull EnumSelection<Style> m_style =
    new EnumSelection<Style>(Style.class).withFormatter(s_styleFormatter);

  static
  {
    s_indexes.put("styles", new GroupedIndex("Styles", TYPE, 1)
      {
        private static final long serialVersionUID = 1L;

        public Set<String> names(@Nonnull Set<String> ioCollected,
                                 @Nonnull DMAData inData,
                                 @Nonnull String []inGroups)
        {
          for(BaseProduct product : inData.getEntriesList(TYPE))
            ioCollected.add(product.m_style.toString(false));

          return ioCollected;
        }

        public boolean matches(@Nonnull String []inGroups,
                               @Nonnull AbstractEntry inProduct)
        {
          if(!(inProduct instanceof BaseProduct))
            return false;

          BaseProduct product = (BaseProduct)inProduct;

          String style = inGroups[0];

          return style.equals(product.m_style.toString(false));
        }
        // CHECKSTYLE:OFF
      }.withImages());
    // CHECKSTYLE:ON
  }

  //........................................................................
  //----- producer ---------------------------------------------------------

  /** The formatter for the producer. */
  protected static final Formatter<Selection> s_producerFormatter =
    new LinkFormatter<Selection>("/product/producers/");

  /** The name of the company that produced the product. */
  @Key("producer")
  protected @Nonnull Selection m_producer =
    new Selection(PRODUCERS).withFormatter(s_producerFormatter);

  static
  {
    s_indexes.put("producers", new GroupedIndex("Producers", TYPE, 1)
      {
        private static final long serialVersionUID = 1L;

        public Set<String> names(@Nonnull Set<String> ioCollected,
                                 @Nonnull DMAData inData,
                                 @Nonnull String []inGroups)
        {
          for(BaseProduct product : inData.getEntriesList(TYPE))
            ioCollected.add(product.m_producer.toString(false));

          return ioCollected;
        }

        public boolean matches(@Nonnull String []inGroups,
                               @Nonnull AbstractEntry inProduct)
        {
          if(!(inProduct instanceof BaseProduct))
            return false;

          BaseProduct product = (BaseProduct)inProduct;

          String producer = inGroups[0];

          return producer.equals(product.m_producer.toString(false));
        }
        // CHECKSTYLE:OFF
      }.withImages());
    // CHECKSTYLE:ON
  }

  //........................................................................
  //----- volume -----------------------------------------------------------

  /** The volume of the product for multi volume products. */
  @Key("volume")
  protected @Nonnull Name m_volume = new Name().withEditType("name[volume]");

  //........................................................................
  //----- number -----------------------------------------------------------

  /** The number of the series. */
  @Key("number")
  protected @Nonnull Name m_number = new Name().withEditType("name[number]");

  //........................................................................
  //----- series -----------------------------------------------------------

  /** The formatter for the series. */
  protected static final Formatter<Name> s_seriesFormatter =
    new LinkFormatter<Name>("/product/series/");

  /** The name of the series, even multiple if necessary, this product belongs
   *  to. */
  @Key("series")
  protected @Nonnull ValueList<Name> m_series =
    new ValueList<Name>(new Name().withEditType("name[series]")
                   .withFormatter(s_seriesFormatter));

  static
  {
    s_indexes.put("series", new GroupedIndex("Series", TYPE, 1)
      {
        private static final long serialVersionUID = 1L;

        public Set<String> names(@Nonnull Set<String> ioCollected,
                                 @Nonnull DMAData inData,
                                 @Nonnull String []inGroups)
        {
          for(BaseProduct product : inData.getEntriesList(TYPE))
            ioCollected.add(product.m_series.toString(false));

          return ioCollected;
        }

        public boolean matches(@Nonnull String []inGroups,
                               @Nonnull AbstractEntry inProduct)
        {
          if(!(inProduct instanceof BaseProduct))
            return false;

          BaseProduct product = (BaseProduct)inProduct;

          String series = inGroups[0];

          return series.equals(product.m_series.toString(false));
        }
      });
  }

  //........................................................................
  //----- price ------------------------------------------------------------

  /** The formatter for the price. */
  protected static final Formatter<Price> s_priceFormatter =
    new LinkFormatter<Price>("/product/prices/");

  /** The grouping for the pages. */
  protected static final Group<Price, Long, String> s_priceGrouping =
    new Group<Price, Long, String>(new Group.Extractor<Price, Long>()
      {
        public Long extract(@Nonnull Price inValue)
        {
          return inValue.get();
        }
      }, new Long [] { 100L, 500L, 1000L, 2500L, 5000L, 10000L, },
                                   new String []
        { "1", "5", "10", "25", "50", "100", "a fortune", }, "$undefined$");

  /** This is the price of the series. */
  @Key("price")
    protected @Nonnull Price m_price =
      new Price(0, 1000 * 100).withGrouping(s_priceGrouping)
        .withFormatter(s_priceFormatter);

  static
  {
    s_indexes.put("prices", new GroupedIndex("Prices", TYPE, 1, s_priceGrouping)
      {
        private static final long serialVersionUID = 1L;

        public Set<String> names(@Nonnull Set<String> ioCollected,
                                 @Nonnull DMAData inData,
                                 @Nonnull String []inGroups)
        {
          for(BaseProduct product : inData.getEntriesList(TYPE))
            ioCollected.add(product.m_price.group());

          return ioCollected;
        }

        public boolean matches(@Nonnull String []inGroups,
                               @Nonnull AbstractEntry inProduct)
        {
          if(!(inProduct instanceof BaseProduct))
            return false;

          BaseProduct product = (BaseProduct)inProduct;
          return inGroups[0].equalsIgnoreCase(product.m_price.group());
        }
      });
  }

  //........................................................................
  //----- contents ---------------------------------------------------------

  /** The formatter for the price. */
  protected static final Formatter<EnumSelection<Part>> s_partFormatter =
    new LinkFormatter<EnumSelection<Part>>("/product/parts/");

  /** The contents of the product, what kind of individual components it has,
   *  if any. */
  @Key("contents")
  protected @Nonnull ValueList<Multiple> m_contents =
    new ValueList<Multiple>(new Multiple(new Multiple.Element []
      { new Multiple.Element(new EnumSelection<Part>(Part.class)
                             .withFormatter(s_partFormatter)
                             .withEditType("selection[part]"), false),
        new Multiple.Element(new Text()
                             .withEditType("string[description]"), true),
        new Multiple.Element(new Number(1, Integer.MAX_VALUE)
                             .withEditType("number[number]"), true), }));

  static
  {
    s_indexes.put("parts", new GroupedIndex("Parts", TYPE, 1)
      {
        private static final long serialVersionUID = 1L;

        public Set<String> names(@Nonnull Set<String> ioCollected,
                                 @Nonnull DMAData inData,
                                 @Nonnull String []inGroups)
        {
          for(BaseProduct product : inData.getEntriesList(TYPE))
            for(Multiple content : product.m_contents)
              ioCollected.add(content.get(0).toString(false));

          return ioCollected;
        }

        public boolean matches(@Nonnull String []inGroups,
                               @Nonnull AbstractEntry inProduct)
        {
          if(!(inProduct instanceof BaseProduct))
            return false;

          BaseProduct product = (BaseProduct)inProduct;

          String part = inGroups[0];

          for(Multiple content : product.m_contents)
            if(part.equalsIgnoreCase(content.get(0).toString(false)))
              return true;

          return false;
        }
      });
  }

  //........................................................................
  //----- requirements -----------------------------------------------------

  /** The formatter for requirements. */
  protected static final Formatter<Multiple> s_requirementsFormatter =
    new MultipleFormatter<Multiple>(null, null, " optional ", null);

  /** The requirements of this product, which products are required to use this
   * one. */
  @Key("requirements")
  protected @Nonnull Multiple m_requirements =
    new Multiple(new Multiple.Element []
      { new Multiple.Element(new ValueList<Reference>
                             (new Reference(m_data)
                              .withEditType("autokey(products|system)"
                                            + "[required]")), true),
        new Multiple.Element(new ValueList<Reference>
                             (new Reference(m_data)
                              .withEditType("autokey(products|system)"
                                            + "[optional]")), true, " : ",
                             null),
      }).withFormatter(s_requirementsFormatter);

  //........................................................................
  //----- layout -----------------------------------------------------------

  /** The formatter for the layout. */
  protected static final Formatter<Selection> s_layoutFormatter =
    new LinkFormatter<Selection>("/product/layouts/");

  /** The layout of the product. */
  @Key("layout")
  protected @Nonnull EnumSelection<Layout> m_layout =
    new EnumSelection<Layout>(Layout.class);

  static
  {
    s_indexes.put("layouts", new GroupedIndex("Layouts", TYPE, 1)
      {
        private static final long serialVersionUID = 1L;

        public Set<String> names(@Nonnull Set<String> ioCollected,
                                 @Nonnull DMAData inData,
                                 @Nonnull String []inGroups)
        {
          for(BaseProduct product : inData.getEntriesList(TYPE))
            ioCollected.add(product.m_layout.toString(false));

          return ioCollected;
        }

        public boolean matches(@Nonnull String []inGroups,
                               @Nonnull AbstractEntry inProduct)
        {
          if(!(inProduct instanceof BaseProduct))
            return false;

          BaseProduct product = (BaseProduct)inProduct;

          String layout = inGroups[0];

          return layout.equals(product.m_layout.toString(false));
        }
      });
  }

  //........................................................................

  static
  {
    extractVariables(BaseProduct.class);
  }

  //----- worlds index -----------------------------------------------------

  static
  {
    s_indexes.put("worlds", new GroupedIndex("Worlds", TYPE, 1)
      {
        private static final long serialVersionUID = 1L;

        public Set<String> names(@Nonnull Set<String> ioCollected,
                                 @Nonnull DMAData inData,
                                 @Nonnull String []inGroups)
        {
          for(BaseProduct campaign : inData.getEntriesList(TYPE))
            for(Selection world : campaign.m_worlds)
              ioCollected.add(world.toString(false));

          return ioCollected;
        }

        public boolean matches(@Nonnull String []inGroups,
                               @Nonnull AbstractEntry inProduct)
        {
          if(!(inProduct instanceof BaseProduct))
            return false;

          BaseProduct campaign = (BaseProduct)inProduct;

          String requestedWorld = inGroups[0];

          for(Selection world : campaign.m_worlds)
            if(requestedWorld.equals(world.toString(false)))
              return true;

          return false;
        }
        // CHECKSTYLE:OFF
      }.withImages());
    // CHECKSTYLE:ON
  }

  //........................................................................

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
  //----------------------------- getAudience ------------------------------

  /**
   * Get the audience of the product.
   *
   * @return      the audience
   *
   */
  public @Nonnull Audience getAudience()
  {
    return m_audience.getSelected();
  }

  //........................................................................
  //------------------------------- getDate --------------------------------

  /**
   * Get the date of the product.
   *
   * @return      the date
   *
   */
  public @Nonnull String getDate()
  {
    return m_date.toString();
  }

  //........................................................................
  //------------------------------- getLeader ------------------------------

  /**
   * Accessor for the leader of the base product.
   *
   * @return      the requested leader
   *
   */
  public @Nonnull String getLeader()
  {
    return m_leader.get();
  }

  //........................................................................
  //------------------------------ getNumber -------------------------------

  /**
   * Get the series number of the product.
   *
   * @return      the series number
   *
   */
  public @Nonnull String getNumber()
  {
    return m_number.get();
  }

  //........................................................................
  //--------------------------- getNumberValue -----------------------------

  /**
   * Get the series number of the product as an integer.
   *
   * @return      the series number
   *
   */
  public int getNumberValue()
  {
    if(!m_number.isDefined())
      return 0;

    return Strings.extractNumber(m_number.get());
  }

  //........................................................................
  //------------------------------ getPages --------------------------------

  /**
   * Get the pages of the product.
   *
   * @return      the pages
   *
   */
  public long getPages()
  {
    return m_pages.get();
  }

  //........................................................................
  //------------------------------ getSeries -------------------------------

  /**
   * Get the series of the product.
   *
   * @return      the series
   *
   */
  public @Nonnull String getSeries()
  {
    return m_series.toString();
  }

  //........................................................................
  //------------------------------ hasSeries -------------------------------

  /**
    * Check if the product has a series value.
    *
    * @return      true if series is set, false if not
    *
    */
  public boolean hasSeries()
  {
    return m_series.isDefined();
  }

  //........................................................................
  //------------------------------ getStyle --------------------------------

  /**
   * Get the style of the product.
   *
   * @return      the style
   *
   */
  public @Nonnull Style getStyle()
  {
    return m_style.getSelected();
  }

  //........................................................................
  //----------------------------- getProducer ------------------------------

  /**
   * Get the producer of the product.
   *
   * @return      the producer
   *
   */
  public @Nonnull String getProducer()
  {
    return m_producer.toString();
  }

  //........................................................................
  //------------------------------ getSystem -------------------------------

  /**
   * Get the system of the product.
   *
   * @return      the system
   *
   */
  public @Nonnull System getSystem()
  {
    return m_system.getSelected();
  }

  //........................................................................
  //------------------------------- getTitle -------------------------------

  /**
   * Accessor for the title of the base product.
   *
   * @return      the requested title
   *
   */
  public @Nonnull String getTitle()
  {
    return m_title.get();
  }

  //........................................................................
  //----------------------------- getFullTitle -----------------------------

  /**
   * Accessor for the full title of the base product.
   *
   * @return      the requested title
   *
   */
  public @Nonnull String getFullTitle()
  {
    if(m_title == null || !m_title.isDefined())
      return "";

    if(m_leader == null || !m_title.isDefined()
       || m_leader.get().isEmpty())
      return m_title.get();

    return m_leader.get() + " " + m_title.get();
  }

  //........................................................................
  //---------------------------- getProductType ----------------------------

  /**
   * Get the type of the product.
   *
   * @return      the type
   *
   */
  public @Nonnull ProductType getProductType()
  {
    return m_productType.getSelected();
  }

  //........................................................................
  //------------------------------ getVolume -------------------------------

  /**
   * Get the volume of the product.
   *
   * @return      the volume
   *
   */
  public @Nonnull String getVolume()
  {
    return m_volume.get();
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
    if(!m_title.isDefined())
      return super.getRefName();

    if(m_leader.isDefined() && !m_leader.get().isEmpty())
      return getTitle() + ", " + getLeader();

    return getTitle();
  }

  //........................................................................

  //---------------------------- collectPersons ----------------------------

  /**
   * Collect all available persons from this product and add them to the given
   * set.
   *
   * @param    ioNames     the set to add to
   * @param    inJob       the job to limit to
   * @param    inPrefix    the prefix for the persons to collect (or null for
   *                       none)
   *
   * @return   the set of persons given
   *
   */
  public @Nonnull Set<? super String>
    collectPersons(@Nonnull Set<? super String> ioNames,
                   @Nullable String inJob,
                   @Nullable String inPrefix)
  {
    for(Map.Entry<String, ValueList<Multiple>> list
          : categoryLists().entrySet())
    {
      String listName = list.getKey();
      for(Multiple person : list.getValue())
      {
        if(!person.isDefined() || !person.get(0).isDefined())
          continue;

        String name = ((Text)person.get(0)).get();

        if(name.isEmpty())
          continue;

        if(inJob == null
           || listName.equalsIgnoreCase(inJob)
           || (person.get(1).isDefined()
               && ((Name)person.get(1)).get().equalsIgnoreCase(inJob)))
        {
          if(name.indexOf('\\') >= 0)
            name = ASCIIDocument.simpleConvert(name);

          if(inPrefix == null || inPrefix.isEmpty()
             || name.regionMatches(true, 0, inPrefix, 0, inPrefix.length()))
            ioNames.add(name);
        }
      }
    }

    return ioNames;
  }

  //........................................................................
  //----------------------------- collectJobs ------------------------------

  /**
   * Collect all available jobs from this product and add them to the given
   * set.
   *
   * @param    ioJobs      the set to add to
   * @param    inName      the name of the person for which to search jobs (or
   *                       null for all)
   * @param    inPrefix    the prefix for the jobs to collect (or null for
   *                       none)
   *
   * @return   the set of jobs given
   *
   */
  public @Nonnull Set<? super String>
    collectJobs(@Nonnull Set<? super String> ioJobs,
                @Nullable String inName,
                @Nullable String inPrefix)
  {
    for(Map.Entry<String, ValueList<Multiple>> list
          : categoryLists().entrySet())
    {
      String listName = list.getKey();
      for(Multiple person : list.getValue())
        {
          if(inName != null
             && !inName.equalsIgnoreCase(((Text)person.get(0)).get()))
            continue;

          if(inPrefix == null || inPrefix.isEmpty()
             || listName.regionMatches(true, 0, inPrefix, 0, inPrefix.length()))
            ioJobs.add(listName);

          if(!person.get(1).isDefined())
            continue;

          String job = ((Name)person.get(1)).get();

          if(job.indexOf('\\') >= 0)
            job = ASCIIDocument.simpleConvert(job);

          if(inPrefix == null || inPrefix.isEmpty()
             || job.regionMatches(true, 0, inPrefix, 0, inPrefix.length()))
            ioJobs.add(job);
        }
    }

    return ioJobs;
  }

  //........................................................................
  //----------------------------- categoryLists ----------------------------

  /**
   * Get the lists of name values for the given job.
   * TODO: move all persons/jobs into a single value.
   *
   * @return    the names of the requested category or null if not found
   *
   */
  private @Nullable Map<String, ValueList<Multiple>> categoryLists()
  {
    return new ImmutableMap.Builder<String, ValueList<Multiple>>()
      .put("author", m_authors)
      .put("editor", m_editors)
      .put("cover", m_cover)
      .put("cartographer", m_cartographers)
      .put("illustrator", m_illustrators)
      .put("typographer", m_typographers)
      .put("management", m_managers)
      .build();
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
   */
//   public boolean matches(@Nonnull String inPattern)
//   {
//     if(super.matches(inPattern))
//       return true;

//     String title = getTitle();

//     if(title == null)
//       return false;

//     return title.matches("(?i).*" + inPattern + ".*");
//   }

  //........................................................................
  //--------------------------------- isDM ---------------------------------

  /**
   * Check whether the given user is the DM for this entry. Every user is a DM
   * for a base product.
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
  //------------------------------- matches --------------------------------

  /**
   * Check whether the entry matches the given key and value.
   *
   * @param       inKey   the key of the value to match
   * @param       inValue the value to match with
   *
   * @return      true if it matches, false if not
   *
   */
  public boolean matches(@Nonnull String inKey, @Nonnull String inValue)
  {
    if("jobs".equalsIgnoreCase(inKey))
    {
      Set<String> jobs = new HashSet<String>();
      collectJobs(jobs, null, null);
      for(String job : jobs)
        if(inValue.equalsIgnoreCase(job))
          return true;

      return false;
    }
    else if ("persons".equalsIgnoreCase(inKey))
    {
      Set<String> persons = new HashSet<String>();
      collectPersons(persons, null, null);
      for(String person : persons)
        if(inValue.equalsIgnoreCase(person))
          return true;

      return false;
    }
    else
      return super.matches(inKey, inValue);
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
    if("name".equals(inKey))
      return new FormattedValue
        (new Command(computeValue("_leader", inDM).format(this, inDM, true),
                     " ",
                     new BaseCommand(m_title.get())),
         null, "name", false, true, false, true, "names", "");

    if("subtitle".equals(inKey))
      return new FormattedValue
        (new Subtitle(new BaseCommand(m_subtitle.get())),
         null, "subtitle", false, false, false, false, "subtitles", "");

    if("owners".equals(inKey))
    {
      List<Object> commands = new ArrayList<Object>();
      for(BaseCharacter owner : m_data.getEntriesList(BaseCharacter.TYPE))
        for(Product product : owner.getProducts())
        {
          if(product.isBasedOn(this))
          {
            if(!commands.isEmpty())
              commands.add(", ");

            commands.add(new Link(owner.getName(), product.getPath()));
          }
        }

      commands.add(" | ");
      commands.add(new Link("add", "/user/me/product/" + getID() + "?create"));
      return new FormattedValue(new Command(commands), null, "owners", false,
                                false, false, false, null, null);
    }

    return super.computeValue(inKey, inDM);
  }

  //........................................................................

  //...........................................................................

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
    String []parts = Strings.getPatterns(inKey, "(.*?)/(.*)");

    if(parts.length == 2)
      if("jobs".equalsIgnoreCase(parts[0]))
      {
        renameJob(parts[1], inText);
        return null;
      }
      else if("persons".equalsIgnoreCase(parts[0]))
      {
        renamePerson(parts[1], inText);
        return null;
      }

    return super.set(inKey, inText);
  }

  //........................................................................
  //------------------------------ renameJob -------------------------------

  /**
   * Change all occurrences of the given old job name to a new job name.
   *
   * @param       inOld  the old name of the job
   * @param       inNew  the new name of the job
   *
   */
  public void renameJob(@Nonnull String inOld, @Nonnull String inNew)
  {
    m_authors = renameJob(m_authors, inOld, inNew);
    m_editors = renameJob(m_editors, inOld, inNew);
    m_cover = renameJob(m_cover, inOld, inNew);
    m_cartographers = renameJob(m_cartographers, inOld, inNew);
    m_illustrators = renameJob(m_illustrators, inOld, inNew);
    m_typographers = renameJob(m_typographers, inOld, inNew);
    m_managers = renameJob(m_managers, inOld, inNew);
  }

  //........................................................................
  //------------------------------ renameJob -------------------------------

  /**
   * Rename the job in the given list.
   *
   * @param       inList the list of person to adjust
   * @param       inOld  the old name of the job
   * @param       inNew  the new name of the job
   *
   * @return      the changed list
   *
   */
  private @Nonnull ValueList<Multiple>
    renameJob(@Nonnull ValueList<Multiple> inList, @Nonnull String inOld,
              @Nonnull String inNew)
  {
    List<Multiple> list = new ArrayList<Multiple>();
    for(Multiple person : inList)
    {
      if(person.isDefined() && person.get(1).isDefined()
         && ((Name)person.get(1)).get().equalsIgnoreCase(inOld))
      {
        list.add(person.as(person.get(0), ((Name)person.get(1)).as(inNew)));
        changed();
      }
      else
        list.add(person);
      }

    return inList.as(list);
  }

  //........................................................................
  //---------------------------- renamePerson ------------------------------

  /**
   * Change all occurrences of the given old person name to a new job name.
   *
   * @param       inOld  the old name of the person
   * @param       inNew  the new name of the person
   *
   */
  public void renamePerson(@Nonnull String inOld, @Nonnull String inNew)
  {
    m_authors = renamePerson(m_authors, inOld, inNew);
    m_editors = renamePerson(m_editors, inOld, inNew);
    m_cover = renamePerson(m_cover, inOld, inNew);
    m_cartographers = renamePerson(m_cartographers, inOld, inNew);
    m_illustrators = renamePerson(m_illustrators, inOld, inNew);
    m_typographers = renamePerson(m_typographers, inOld, inNew);
    m_managers = renamePerson(m_managers, inOld, inNew);
  }

  //........................................................................
  //---------------------------- renamePerson ------------------------------

  /**
   * Rename the person in the given list.
   *
   * @param       inList the list of person to adjust
   * @param       inOld  the old name of the person
   * @param       inNew  the new name of the person
   *
   * @return      the changed list
   *
   */
  private @Nonnull ValueList<Multiple>
    renamePerson(@Nonnull ValueList<Multiple> inList, @Nonnull String inOld,
                 @Nonnull String inNew)
  {
    List<Multiple> list = new ArrayList<Multiple>();
    for(Multiple person : inList)
    {
      if(person.isDefined() && person.get(0).isDefined()
         && ((Text)person.get(0)).get().equalsIgnoreCase(inOld))
      {
        list.add(person.as(((Text)person.get(0)).as(inNew), person.get(1)));
        changed();
      }
      else
        list.add(person);
      }

    return inList.as(list);
  }

  //........................................................................

  //----------------------------- setAudience ------------------------------

  /**
   * Set the audience of the product.
   *
   * @param       inAudience the audience
   *
   * @return      true if set, false if not
   *
   */
  public boolean setAudience(@Nonnull Audience inAudience)
  {
    m_audience = m_audience.as(inAudience);
    return true;
  }

  //........................................................................
  //------------------------------ setCover --------------------------------

  /**
   * Set the cover of the product.
   *
   * @param       inCover the cover
   *
   * @return      true if set, false if not
   *
   */
  public boolean setCover(@Nonnull ValueList<Multiple> inCover)
  {
    m_cover = inCover;
    return true;
  }

  //........................................................................
  //------------------------------- setDate --------------------------------

  /**
   * Set the date of the product.
   *
   * @param       inDate the date
   *
   * @return      true if set, false if not
   *
   */
//   public boolean setDate(@Nonnull String inDate)
//   {
//     return m_date.setFromString(inDate) == null;
//   }

  //........................................................................
  //------------------------------- setLeader ------------------------------

  /**
   * Set the leader of the base product.
   *
   * @param       inLeader the new leader
   *
   * @return      true if set, false if not
   *
   */
//   public boolean setLeader(@Nonnull String inLeader)
//   {
//     if(inLeader == null)
//       return false;

//     m_leader.set(inLeader);

//     return true;
//   }

  //........................................................................
  //------------------------------ setNumber -------------------------------

  /**
   * Set the series number of the product.
   *
   * @param       inNumber the series number
   *
   * @return      true if set, false if not
   *
   */
//   public boolean setNumber(@Nonnull String inNumber)
//   {
//     if(inNumber == null)
//       return false;

//     m_number.set(inNumber);

//     return true;
//   }

  //........................................................................
  //------------------------------ setPages --------------------------------

  /**
   * Set the pages of the product.
   *
   * @param       inPages the pages
   *
   * @return      true if set, false if not
   *
   */
//   public boolean setPages(long inPages)
//   {
//     if(inPages < 0)
//       return false;

//     m_pages.set(inPages);

//     return true;
//   }

  //........................................................................
  //------------------------------ setISBN --------------------------------

  /**
   * Set the isbn of the product.
   *
   * @param       inGroup     the group code
   * @param       inPublisher the publisher code
   * @param       inTitle     the title code
   * @param       inCheck     the checksum (10 for X), set to -1 if to compute
   *
   * @return      true if set, false if not
   *
   */
// public boolean setISBN(@Nonnull String inGroup, @Nonnull String inPublisher,
//                          @Nonnull String inTitle, int inCheck)
//   {
//     return m_isbn.set(inGroup, inPublisher, inTitle, inCheck);
//   }

  //........................................................................
  //------------------------------ setISBN13 --------------------------------

  /**
   * Set the isbn of the product.
   *
   * @param       inG13       the new group for isbn 13
   * @param       inGroup     the group code
   * @param       inPublisher the publisher code
   * @param       inTitle     the title code
   * @param       inCheck     the checksum (10 for X), set to -1 if to compute
   *
   * @return      true if set, false if not
   *
   */
//   public boolean setISBN13(@Nonnull String inG13, @Nonnull String inGroup,
//                         @Nonnull String inPublisher, @Nonnull String inTitle,
//                            int inCheck)
//   {
//     return m_isbn13.set(inG13, inGroup, inPublisher, inTitle, inCheck);
//   }

  //........................................................................
  //------------------------------ setPrice --------------------------------

  /**
   * Set the price of the product.
   *
   * @param       inCurrency the currency of the price
   * @param       inNumber   the price * 100
   *
   * @return      true if set, false if not
   *
   */
//   public boolean setPrice(@Nonnull String inCurrency, long inNumber)
//   {
//     return m_price.set(inCurrency, inNumber);
//   }

  //........................................................................
  //------------------------------ addSeries -------------------------------

  /**
   * Add a series to the product.
   *
   * @param       inSeries the series
   *
   * @return      true if added, false if not
   *
   */
//   public boolean addSeries(@Nonnull String inSeries)
//   {
//     Text series = m_series.newElement();

//     series.set(inSeries);

//     return m_series.add(series);
//   }

  //........................................................................
  //----------------------------- addContents ------------------------------

  /**
   * Add a contents entry to the product.
   *
   * @param       inPart        the kind of contents added
   * @param       inDescription the content description
   * @param       inNumber      the number of parts the contents consists of
   *
   * @return      true if added, false if not
   *
   */
//   @SuppressWarnings("unchecked") // need to cast the enum selection
//   public boolean addContents(@Nonnull Part inPart,
//                              @Nonnull String inDescription, int inNumber)
//   {
//     if(inNumber <= 0)
//       return false;

//     Multiple contents = m_contents.newElement();

//     ((EnumSelection<Part>)contents.get(0).getMutable()).set(inPart);
//     ((Text)contents.get(1).getMutable()).set(inDescription);
//     ((Number)contents.get(2).getMutable()).set(inNumber);

//     return m_contents.add(contents);
//   }

  //........................................................................
  //--------------------------- addRequirement -----------------------------

  /**
   * Add a requirement entry to the product.
   *
   * @param       inRequirement the id of the product required by this one
   *
   * @return      true if added, false if not
   *
   */
//   @SuppressWarnings("unchecked") // Multiple value cast
//   public boolean addRequirement(@Nonnull String inRequirement)
//   {
//   ValueList<Text> list = (ValueList<Text>)m_requirements.get(0).getMutable();

//     Text requirement = list.newElement();
//     requirement.set(inRequirement);

//     return list.add(requirement);
//   }

  //........................................................................
  //----------------------- addOptionalRequirement -------------------------

  /**
   * Add an optional requirement entry to the product.
   *
   * @param       inRequirement the id of the product optionally required by
   *                            this one
   *
   * @return      true if added, false if not
   *
   */
//   @SuppressWarnings("unchecked") // multiple value cast
//   public boolean addOptionalRequirement(@Nonnull String inRequirement)
//   {
//   ValueList<Text> list = (ValueList<Text>)m_requirements.get(1).getMutable();

//     Text requirement = list.newElement();
//     requirement.set(inRequirement);

//     return list.add(requirement);
//   }

  //........................................................................
  //------------------------------- setStyle -------------------------------

  /**
   * Set the style of the product.
   *
   * @param       inStyle the style
   *
   * @return      true if set, false if not
   *
   */
//   public boolean setStyle(@Nonnull Style inStyle)
//   {
//     if(inStyle == null)
//       return false;

//     return m_style.set(inStyle);
//   }

  //........................................................................
  //----------------------------- setProducer ------------------------------

  /**
   * Set the producer of the product.
   *
   * @param       inProducer the producer
   *
   * @return      true if set, false if not
   *
   */
//   public boolean setProducer(@Nonnull String inProducer)
//   {
//     if(inProducer == null)
//       return false;

//     return m_producer.setSelected(inProducer);
//   }

  //........................................................................
  //------------------------------ setSystem -------------------------------

  /**
   * Set the system of the product.
   *
   * @param       inSystem the system
   *
   * @return      true if set, false if not
   *
   */
//   public boolean setSystem(@Nonnull System inSystem)
//   {
//     return m_system.set(inSystem);
//   }

  //........................................................................
  //------------------------------ setLayout -------------------------------

  /**
   * Set the layout of the product.
   *
   * @param       inLayout the layout
   *
   * @return      true if set, false if not
   *
   */
//   public boolean setLayout(@Nonnull Layout inLayout)
//   {
//     return m_layout.set(inLayout);
//   }

  //........................................................................
  //------------------------------- setTitle -------------------------------

  /**
   * Set the title of the base product.
   *
   * @param       inTitle the new title
   *
   * @return      true if set, false if not (because of error)
   *
   */
//   public boolean setTitle(@Nonnull String inTitle)
//   {
//     m_title.set(inTitle);

//     return true;
//   }

  //........................................................................
  //------------------------------- setNotes -------------------------------

  /**
   * Set the notes of the base product.
   *
   * @param       inNotes the new notes
   *
   * @return      true if set, false if not (because of error)
   *
   */
//   public boolean setNotes(@Nonnull String inNotes)
//   {
//     m_notes.set(inNotes);

//     return true;
//   }

  //........................................................................
  //------------------------------- setTitle -------------------------------

  /**
   * Set the sub title of the base product.
   *
   * @param       inSubtitle the new subtitle
   *
   * @return      true if set, false if not (because of error)
   *
   */
//   public boolean setSubtitle(@Nonnull String inSubtitle)
//   {
//     m_subtitle.set(inSubtitle);

//     return true;
//   }

  //........................................................................
  //---------------------------- setProductType ----------------------------

  /**
   * Set the type of the product.
   *
   * @param       inType the type to set to
   *
   * @return      true if set, false if not
   *
   */
//   public boolean setProductType(@Nonnull ProductType inType)
//   {
//     return m_productType.set(inType);
//   }

  //........................................................................
  //------------------------------ setVolume -------------------------------

  /**
   * Set the volume of the product.
   *
   * @param       inVolume the volume
   *
   * @return      true if set, false if not
   *
   */
//   public boolean setVolume(@Nonnull String inVolume)
//   {
//     m_volume.set(inVolume);

//     return true;
//   }

  //........................................................................
  //------------------------------ addAuthor -------------------------------

  /**
   * Add an author name to the current list of authors.
   *
   * @param       inAuthor the name of the author, as 'Lastnasme, Firstname'
   * @param       inJob    the job the person did, or null
   *
   * @return      true if added, false if not
   *
   */
//   public boolean addAuthor(@Nonnull String inAuthor, @Nonnull String inJob)
//   {
//     Multiple author = m_authors.newElement();

//     ((Text)author.get(0).getMutable()).set(inAuthor);
//     ((Text)author.get(1).getMutable()).set(inJob);

//     m_authors.add(author);

//     return true;
//   }

  //........................................................................
  //------------------------------ addEditor -------------------------------

  /**
   * Add an editor name to the current list of editors.
   *
   * @param       inEditor the name of the editor, as 'Lastnasme, Firstname'
   * @param       inJob    the job the person did, or null
   *
   * @return      true if added, false if not
   *
   */
//   public boolean addEditor(@Nonnull String inEditor, @Nonnull String inJob)
//   {
//     Multiple editor = m_editors.newElement();

//     ((Text)editor.get(0).getMutable()).set(inEditor);
//     ((Text)editor.get(1).getMutable()).set(inJob);

//     m_editors.add(editor);

//     return true;
//   }

  //........................................................................
  //------------------------------- addCover -------------------------------

  /**
   * Add an cover name to the current list of covers.
   *
   * @param       inCover the name of the cover, as 'Lastnasme, Firstname'
   * @param       inJob    the job the person did, or null
   *
   * @return      true if added, false if not
   *
   */
//   public boolean addCover(@Nonnull String inCover, @Nonnull String inJob)
//   {
//     Multiple cover = m_cover.newElement();

//     ((Text)cover.get(0).getMutable()).set(inCover);
//     ((Text)cover.get(1).getMutable()).set(inJob);

//     m_cover.add(cover);

//     return true;
//   }

  //........................................................................
  //---------------------------- addCartographer ---------------------------

  /**
   * Add an cartographer name to the current list of cartographers.
   *
   * @param       inCartographer the name of the cartographer, as
   *                             'Lastname, Firstname'
   *
   * @param       inJob          the job the person did, or null
   *
   * @return      true if added, false if not
   *
   */
//   public boolean addCartographer(@Nonnull String inCartographer,
//                                  @Nonnull String inJob)
//   {
//     Multiple cartographer = m_cartographers.newElement();

//     ((Text)cartographer.get(0).getMutable()).set(inCartographer);
//     ((Text)cartographer.get(1).getMutable()).set(inJob);

//     return m_cartographers.add(cartographer);
//   }

  //........................................................................
  //---------------------------- addIllustrator ---------------------------

  /**
   * Add an illustrator name to the current list of illustrators.
   *
   * @param       inIllustrator the name of the illustrator, as
   *                             'Lastname, Firstname'
   *
   * @param       inJob          the job the person did, or null
   *
   * @return      true if added, false if not
   *
   */
//   public boolean addIllustrator(@Nonnull String inIllustrator,
//                                 @Nonnull String inJob)
//   {
//     if(inIllustrator == null)
//       return false;

//     Multiple illustrator = m_illustrators.newElement();

//     ((Text)illustrator.get(0).getMutable()).set(inIllustrator);
//     ((Text)illustrator.get(1).getMutable()).set(inJob);

//     m_illustrators.add(illustrator);

//     return true;
//   }

  //........................................................................
  //----------------------------- addTypography ----------------------------

  /**
   * Add a typographer name to the current list of typographerss.
   *
   * @param       inTypographer the name of the typographer, as
   *                             'Lastname, Firstname'
   *
   * @param       inJob          the job the person did, or null
   *
   * @return      true if added, false if not
   *
   */
//   public boolean addTypographer(@Nonnull String inTypographer,
//                                 @Nonnull String inJob)
//   {
//     Multiple typographer = m_typographers.newElement();

//     ((Text)typographer.get(0).getMutable()).set(inTypographer);
//     ((Text)typographer.get(1).getMutable()).set(inJob);

//     m_typographers.add(typographer);

//     return true;
//   }

  //........................................................................
  //------------------------------- addManager -----------------------------

  /**
   * Add a manager name to the current list of managerss.
   *
   * @param       inManager the name of the manager, as 'Lastname, Firstname'
   *
   * @param       inJob          the job the person did, or null
   *
   * @return      true if added, false if not
   *
   */
//   public boolean addManager(@Nonnull String inManager, @Nonnull String inJob)
//   {
//     Multiple manager = m_managers.newElement();

//     ((Text)manager.get(0).getMutable()).set(inManager);
//     ((Text)manager.get(1).getMutable()).set(inJob);

//     m_managers.add(manager);

//     return true;
//   }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- addJobs --------------------------------

  /**
   * Add the jobs of the given list with the given key to the hashtable.
   *
   * @param       ioStore  where to store the information
   * @param       inKey    the default key to use
   * @param       inPerson the person to limit to (or null for all)
   * @param       inList   the list with the values
   *
   */
  protected static void addJobs(@Nonnull Set<String> ioStore,
                                @Nonnull String inKey,
                                @Nonnull String inPerson,
                                @Nonnull ValueList<Multiple> inList)
  {
    for(Multiple person : inList)
    {
      String job = inKey.toLowerCase(Locale.US);
      if(person.get(1).isDefined())
        job = ((Name)person.get(1)).get().toLowerCase(Locale.US);

      if(inPerson != null)
      {
        String name = ((Text)person.get(0)).get();

        if(name.indexOf('\\') >= 0)
          name = ASCIIDocument.simpleConvert(name);

        if(!name.equalsIgnoreCase(inPerson))
          continue;
      }

      ioStore.add(job);
    }
  }

  //........................................................................
  //------------------------------ addPersons ------------------------------

  /**
   * Add the persons to the given lists matching the given job.
   *
   * @param       ioStore where to store the information
   * @param       inKey   the default job if none is given
   * @param       inJob   the job to compare against
   * @param       inList  the list with the values
   *
   */
  protected static void addPersons(@Nonnull Set<String> ioStore,
                                   @Nonnull String inKey,
                                   @Nonnull String inJob,
                                   @Nonnull ValueList<Multiple> inList)
  {
    for(Multiple person : inList)
    {
      // if no job, add it in any case
      if(inJob != null)
      {
        String job = inKey;
        if(person.get(1).isDefined())
          job = ((Name)person.get(1)).get();

        if(!job.equalsIgnoreCase(inJob))
          continue;
      }

      String name = ((Text)person.get(0)).get();

      if(name.indexOf('\\') >= 0)
        name = ASCIIDocument.simpleConvert(name);

      ioStore.add(name);
    }
  }

  //........................................................................

  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled.
   *
   */
//   public void complete()
//   {
//     super.complete();

//     // if the normal isbn is not set and this one starts with the default
//     // numbers, deduce the normal isbn number
//     if(!m_isbn.isDefined()
//        && ("978".equals(m_isbn13.get13()) || "979".equals(m_isbn13.get13())))
//       m_isbn.set(m_isbn13.getGroup(), m_isbn13.getPublisher(),
//                  m_isbn13.getTitle(),
//                  net.ixitxachitls.dma.values.ISBN.compute
//                  (m_isbn13.getGroup(), m_isbn13.getPublisher(),
//                   m_isbn13.getTitle()));
//   }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** This is the test. */
  public static class Test extends ValueGroup.Test
  {
    //----- text -----------------------------------------------------------

    /** The text to read from. */
    private static String s_text =
      "#------ WTC 88567 -------------------\n"
      + "\n"
      + "base product WTC 88567 = \n"
      + "\n"
      + "  title         \"Silver M\\hat{a}rches\";\n"
      + "  leader        \"\";\n"
      + "  author        \"Ed Greenwood\", \"Jason Carl\", "
      + "                \"Richard Baker\" developer;\n"
      + "  editor        \"Kim Mohan\";\n"
      + "  cover         \"Vance Kovacs\";\n"
      + "  cartography   \"Dennis Kauth\", \"Rob Lazzaretti\";\n"
      + "  illustrations \"Matt Cavotta\", \"Michael Dubisch\", "
      + "                \"Jeff Easley\", \"Wayne England\", "
      + "                \"Raven Mimura\", \"Matt Mitchel\", \n"
      + "                \"Christopher Moeller\", \"Puddnhead\", \n"
      + "                \"Adam Rex\", \"Richard Sardinha\", \n"
      + "                \"Arnie Swekel\";\n"
      + "  typography    \"Sonya Percival\";\n"
      + "  management    \"Richard Baker\" creative direction, \n"
      + "                \"Bill Slavicsek\" vice-president RPG R&D,\n"
      + "                 \"Mary Kirchoff\" vice-president publishing,\n"
      + "                \"Anthony Valterra\" business management,\n"
      + "                \"Martin Durham\" project management,\n"
      + "                \"Chas DeLong\" production management,\n"
      + "                \"Robert Raper\" art direction,\n"
      + "                \"Robert Campbell\" graphic design,\n"
      + "                \"Cynthia Fliege\" graphic design,\n"
      + "                \"Dee & Barnett\" graphic design;\n"
      + "  date          July 2002;\n"
      + "  ISBN          0-7869-2835-2;\n"
      + "  pages         160;\n"
      + "  system        D&D 3rd;\n"
      + "  producer      TSR;\n"
      + "  worlds        Forgotten Realms;\n"
      + "  audience      DM;\n"
      + "  product type  accessory;\n"
      + "  style         soft cover;\n"
      + "  volume        XV;\n"
      + "  series        test series;\n"
      + "  number        42;\n"
      + "  price         $38.95;\n"
      + "  contents      book 3, poster \"color map\" 2;\n"
      + "  notes         \"test\";\n"
      + "  references    guru guru : 10, test, test : 304-330/400;\n"
      + "  subtitle      \"A Vast Frontier Fraught with Endless Peril\";\n"
      + "  requirements  DMA 007, DMA 42, DMA 3;\n"
      + "  description   \n"
      + "\n"
      + "  \"Haunted by malicious dragons, hordes of orcs, and other\n"
      + "  ferocious creatures, the relentless cold and unforgiving\n"
      + "  terrain of the \\Place{Silver Marches} promise undiscovered\n"
      + "  riches and unspeakable danger to those bold enough to\n"
      + "  venture there. Complete information on the towns and\n"
      + "  settlements of the burgeoning \\Place{Silver Marches} alliance\n"
      + "  and the many hazards that threaten it highlight this detailed\n"
      + "  survey of one of the most exciting regions in the\n"
      + "  \\Product[WTC 11836]{Forgotten Realms} game setting.\n"
      + "\n"
      + "  \\list{6 new prestige classes}\n"
      + "       {Indigenous monster}\n"
      + "       {Poster map of the region}\n"
      + "\n"
      + "  To use this accessory, you also need the\n"
      + "  \\Product[WTC 11836]{Forgotten Realms Campaign Setting}, the\n"
      + "  \\Product[WTC 11550]{Player's Handbook}, the\n"
      + "  \\Product[WTC 11551]{Dungeon Master's\n"
      + "  Guide}, and the \\Product[WTC 11552]{Monster Manual}.\".\n"
      + "\n"
      + "#.......................................................\n"
      + "\n";

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      ParseReader reader =
        new ParseReader(new java.io.StringReader(s_text), "test");

      String result =
        "#----- WTC 88567\n"
        + "\n"
        + "base product WTC 88567 =\n"
        + "\n"
        + "  title             \"Silver M\\hat{a}rches\";\n"
        + "  leader            \"\";\n"
        + "  subtitle          \"A Vast Frontier Fraught with Endless Peril\""
        + ";\n"
        + "  notes             \"test\";\n"
        + "  author            \"Ed Greenwood\",\n"
        + "                    \"Jason Carl\",\n"
        + "                    \"Richard Baker\" developer;\n"
        + "  editor            \"Kim Mohan\";\n"
        + "  cover             \"Vance Kovacs\";\n"
        + "  cartography       \"Dennis Kauth\",\n"
        + "                    \"Rob Lazzaretti\";\n"
        + "  illustrations     \"Matt Cavotta\",\n"
        + "                    \"Michael Dubisch\",\n"
        + "                    \"Jeff Easley\",\n"
        + "                    \"Wayne England\",\n"
        + "                    \"Raven Mimura\",\n"
        + "                    \"Matt Mitchel\",\n"
        + "                    \"Christopher Moeller\",\n"
        + "                    \"Puddnhead\",\n"
        + "                    \"Adam Rex\",\n"
        + "                    \"Richard Sardinha\",\n"
        + "                    \"Arnie Swekel\";\n"
        + "  typography        \"Sonya Percival\";\n"
        + "  management        \"Richard Baker\" creative direction,\n"
        + "                    \"Bill Slavicsek\" vice-president RPG R&D,\n"
        + "                    \"Mary Kirchoff\" vice-president publishing,\n"
        + "                    \"Anthony Valterra\" business management,\n"
        + "                    \"Martin Durham\" project management,\n"
        + "                    \"Chas DeLong\" production management,\n"
        + "                    \"Robert Raper\" art direction,\n"
        + "                    \"Robert Campbell\" graphic design,\n"
        + "                    \"Cynthia Fliege\" graphic design,\n"
        + "                    \"Dee & Barnett\" graphic design;\n"
        + "  date              July 2002;\n"
        + "  ISBN              0-7869-2835-2;\n"
        + "  pages             160;\n"
        + "  system            D&D 3rd;\n"
        + "  audience          DM;\n"
        + "  product type      Accessory;\n"
        + "  style             Soft Cover;\n"
        + "  producer          TSR;\n"
        + "  volume            XV;\n"
        + "  number            42;\n"
        + "  series            test series;\n"
        + "  price             $38.95;\n"
        + "  contents          Book 3,\n"
        + "                    Poster \"color map\" 2;\n"
        + "  requirements      DMA 007,\n"
        + "                    DMA 42,\n"
        + "                    DMA 3;\n"
        + "  worlds            Forgotten Realms;\n"
        + "  references        guru guru: 10,\n"
        + "                    test,\n"
        + "                    test: 304-330/400;\n"
        + "  description       \"Haunted by malicious dragons, hordes of orcs, "
        + "and other\n"
        + "                    ferocious creatures, the relentless cold and "
        + "unforgiving\n"
        + "                    terrain of the \\Place{Silver Marches} promise "
        + "undiscovered\n"
        + "                    riches and unspeakable danger to those bold "
        + "enough to\n"
        + "                    venture there. Complete information on the "
        + "towns and\n"
        + "                    settlements of the burgeoning \\Place{Silver "
        + "Marches} alliance\n"
        + "                    and the many hazards that threaten it highlight "
        + "this detailed\n"
        + "                    survey of one of the most exciting regions in "
        + "the\n"
        + "                    \\Product[WTC 11836]{Forgotten Realms} game "
        + "setting.\n"
        + "                    \\list{6 new prestige classes}\n"
        + "                    {Indigenous monster}\n"
        + "                    {Poster map of the region}\n"
        + "                    To use this accessory, you also need the\n"
        + "                    \\Product[WTC 11836]{Forgotten Realms Campaign "
        + "Setting}, the\n"
        + "                    \\Product[WTC 11550]{Player's Handbook}, the\n"
        + "                    \\Product[WTC 11551]{Dungeon Master's\n"
        + "                    Guide}, and the \\Product[WTC 11552]{Monster "
        + "Manual}.\".\n"
        + "\n"
        + "#.....\n";

      AbstractEntry entry = BaseProduct.read(reader, new DMAData("path"));

      //System.out.println("read entry:\n'" + entry + "'");

      assertNotNull("base product should have been read", entry);
      assertEquals("base product name does not match", "WTC 88567",
                   entry.getName());
      assertEquals("base product does not match", result, entry.toString());
    }

    //......................................................................
    //----- persons --------------------------------------------------------

    /** Testing get. */
    @org.junit.Test
    public void persons()
    {
      ParseReader reader =
        new ParseReader(new java.io.StringReader(s_text), "test");
      BaseProduct entry = (BaseProduct)
        BaseProduct.read(reader, new DMAData("path"));

      Set<String> persons = new java.util.TreeSet<String>();
      entry.collectPersons(persons, null, null);
      assertContent("persons", persons,
                    "Adam Rex",
                    "Anthony Valterra",
                    "Arnie Swekel",
                    "Bill Slavicsek",
                    "Chas DeLong",
                    "Christopher Moeller",
                    "Cynthia Fliege",
                    "Dee & Barnett",
                    "Dennis Kauth",
                    "Ed Greenwood",
                    "Jason Carl",
                    "Jeff Easley",
                    "Kim Mohan",
                    "Martin Durham",
                    "Mary Kirchoff",
                    "Matt Cavotta",
                    "Matt Mitchel",
                    "Michael Dubisch",
                    "Puddnhead",
                    "Raven Mimura",
                    "Richard Baker",
                    "Richard Sardinha",
                    "Rob Lazzaretti",
                    "Robert Campbell",
                    "Robert Raper",
                    "Sonya Percival",
                    "Vance Kovacs",
                    "Wayne England");

      persons.clear();
      entry.collectPersons(persons, null, "Ja");
      assertContent("persons", persons, "Jason Carl");
    }

    //......................................................................
    //----- jobs -----------------------------------------------------------

    /** Testing get. */
    @org.junit.Test
    public void jobs()
    {
      ParseReader reader =
        new ParseReader(new java.io.StringReader(s_text), "test");
      BaseProduct entry = (BaseProduct)
        BaseProduct.read(reader, new DMAData("path"));

      Set<String> jobs = new java.util.TreeSet<String>();
      entry.collectJobs(jobs, null, null);
      assertContent("jobs", jobs,
                    "art direction",
                    "author",
                    "business management",
                    "cartographer",
                    "cover",
                    "creative direction",
                    "developer",
                    "editor",
                    "graphic design",
                    "illustrator",
                    "management",
                    "production management",
                    "project management",
                    "typographer",
                    "vice-president RPG R&D",
                    "vice-president publishing");

      jobs.clear();
      entry.collectJobs(jobs, null, "cr");
      assertContent("jobs", jobs, "creative direction");

      jobs.clear();
      entry.collectJobs(jobs, "Robert Raper", null);
      assertContent("jobs", jobs, "art direction", "management");

      jobs.clear();
      entry.collectJobs(jobs, "Robert Raper", "a");
      assertContent("jobs", jobs, "art direction");

      jobs.clear();
      entry.collectJobs(jobs, "Robert Raper", "arti");
      assertContent("jobs", jobs);
    }

    //......................................................................
    //----- indexes --------------------------------------------------------

    /** Testing of the base product specific indexes. */
//     @SuppressWarnings("unchecked") // Filter.NONE
//     @org.junit.Test
//     public void indexes()
//     {
//       BaseCampaign.GLOBAL.m_bases.clear();

//       BaseProduct product1 = new BaseProduct("product1");
//       BaseProduct product2 = new BaseProduct("product2");

//       product1.addAuthor("author", "job1");
//       product2.addEditor("editor", "job1");
//       product1.addCover("cover", "job2");
//       product1.addCover("another cover", "job3");
//       product1.setDate("2008");
//       product2.setDate("May 2007");
//       product1.setPages(15);
//       product2.setPages(143);
//       product1.setSystem(System.DnD_3RD);
//       product2.setSystem(System.NONE);
//       product1.addContents(Part.CARD, "contents1", 42);
//       product2.addContents(Part.CD, "contents2", 23);
//       product1.addContents(Part.BOOK, "contents3", 666);

//       BaseCampaign.GLOBAL.add(product1);
//       BaseCampaign.GLOBAL.add(product2);

//       m_logger.verify();

//       for(net.ixitxachitls.dma.entries.indexes.Index index : s_indexes)
//       {
//         if("Product".equals(index.getGroup())
//            && "Persons".equals(index.getTitle()))
//         {
//           assertEquals("persons", 4,
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries()).size());

//           Iterator i =
//             index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
//             .iterator();
//           assertEquals("persons", "another cover", i.next().toString());
//           assertEquals("persons", "author", i.next().toString());
//           assertEquals("persons", "cover", i.next().toString());
//           assertEquals("persons", "editor", i.next().toString());

//           assertFalse("persons", index.matchesName("guru", product1));
//           assertTrue("persons", index.matchesName("author", product1));
//           assertTrue("persons", index.matchesName("cover", product1));
//           assertFalse("persons", index.matchesName("editor", product1));
//           assertTrue("persons", index.matchesName("editor", product2));

//           // checking built command
//           Collection<Object> commands =
//             index.buildCommand("author", BaseCampaign.GLOBAL.createView
//                                (Filter.NONE, index.getIdentificator()));

//           // now check the commands
//           assertEquals("size", 3, commands.size());

//           Iterator<Object> j = commands.iterator();

//           Object command = j.next();
//           assertEquals("command", "label", extract(command, 0));
//           assertEquals("command", "Job", extract(command, 1));

//           command = j.next();
//           assertEquals("command", "link", extract(command, 0));
//           assertEquals("command", "/index/jobs/job1", extract(command, -1));
//           assertEquals("command", "job1", extract(command, 1));

//           command = j.next();
//           assertEquals("command", "link", extract(command, 1, 0));
//           assertEquals("command", "/entry/baseproduct/product1",
//                        extract(command, 1, -1));
//           assertEquals("command", "", extract(command, 1, 1));

//           // checking getting all persons
//           SortedSet<String> persons = getAllPersons("author");

//           assertEquals("all persons", 1, persons.size());
//           assertTrue("all persons", persons.contains("author"));

//           persons = getAllPersons(null);

//           assertEquals("all persons", 4, persons.size());

//           i = persons.iterator();

//           assertEquals("all persons", "another cover", i.next());
//           assertEquals("all persons", "author", i.next());
//           assertEquals("all persons", "cover", i.next());
//           assertEquals("all persons", "editor", i.next());

//           continue;
//         }

//         if("Product".equals(index.getGroup())
//            && "Jobs".equals(index.getTitle()))
//         {
//           assertEquals("jobs", 3,
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries()).size());

//           Iterator i =
//             index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
//             .iterator();
//           assertEquals("jobs", "job1", i.next().toString());
//           assertEquals("jobs", "job2", i.next().toString());
//           assertEquals("jobs", "job3", i.next().toString());

//           assertFalse("jobs", index.matchesName("guru", product1));
//           assertTrue("jobs", index.matchesName("job1", product1));
//           assertTrue("jobs", index.matchesName("job2", product1));
//           assertFalse("jobs", index.matchesName("job2", product2));
//           assertTrue("jobs", index.matchesName("job1", product2));

//           // checking built command
//           Collection<Object> commands =
//             index.buildCommand("job1", BaseCampaign.GLOBAL.createView
//                                (Filter.NONE, index.getIdentificator()));

//           // now check the commands
//           assertEquals("size", 6, commands.size());

//           Iterator<Object> j = commands.iterator();

//           Object command = j.next();
//           assertEquals("command", "label", extract(command, 0));
//           assertEquals("command", "Person", extract(command, 1));

//           command = j.next();
//           assertEquals("command", "link", extract(command, 0));
//           assertEquals("command", "/index/persons/author",
//                        extract(command, -1));
//           assertEquals("command", "author", extract(command, 1));

//           command = j.next();
//           assertEquals("command", "link", extract(command, 1, 0));
//           assertEquals("command", "/entry/baseproduct/product1",
//                        extract(command, 1, -1));
//           assertEquals("command", "", extract(command, 1, 1));

//           command = j.next();
//           assertEquals("command", "Person", extract(command, 1));

//           command = j.next();
//           assertEquals("command", "editor", extract(command, 1));

//           command = j.next();
//           assertEquals("command", "/entry/baseproduct/product2",
//                        extract(command, 1, -1));

//           // checking getting all jobs
//           SortedSet<String> jobs = getAllJobs("cover");

//           assertEquals("all jobs", 2, jobs.size());
//           assertTrue("all jobs", jobs.contains("job2"));
//           assertTrue("all jobs", jobs.contains("job3"));

//           jobs = getAllJobs(null);

//           assertEquals("all jobs", 3, jobs.size());

//           i = jobs.iterator();

//           assertEquals("all jobs", "job1", i.next());
//           assertEquals("all jobs", "job2", i.next());
//           assertEquals("all jobs", "job3", i.next());

//           continue;
//         }

//         if("Product".equals(index.getGroup())
//            && "Date".equals(index.getTitle()))
//         {
//           assertEquals("dates", 2,
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries()).size());

//           Iterator i =
//             index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
//             .iterator();
//           assertEquals("dates", "May 2007", i.next().toString());
//           assertEquals("dates", "2008", i.next().toString());

//           assertFalse("dates", index.matchesName("guru", product1));
//           assertTrue("dates", index.matchesName("2008", product1));
//           assertFalse("dates", index.matchesName("2008", product2));
//           assertTrue("dates", index.matchesName("May 2007", product2));

//           continue;
//         }

//         if("Product".equals(index.getGroup())
//            && "Pages".equals(index.getTitle()))
//         {
//           assertEquals("pages", 2,
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries()).size());

//           Iterator i =
//             index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
//             .iterator();
//           assertEquals("pages", "15", i.next().toString());
//           assertEquals("pages", "143", i.next().toString());

//           assertFalse("pages", index.matchesName("guru", product1));
//           assertTrue("pages", index.matchesName("20", product1));
//           assertFalse("pages", index.matchesName("20", product2));
//           assertTrue("pages", index.matchesName("200", product2));

//           continue;
//         }

//         if("Product".equals(index.getGroup())
//            && "System".equals(index.getTitle()))
//         {
//           assertEquals("System", 2,
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries()).size());

//           Iterator i =
//             index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
//             .iterator();
//           assertEquals("System", "None", i.next().toString());
//           assertEquals("System", "D&D 3rd", i.next().toString());

//           assertFalse("System", index.matchesName("guru", product1));
//           assertTrue("System", index.matchesName("D&D 3rd", product1));
//           assertFalse("System", index.matchesName("D&D 3rd", product2));
//           assertTrue("System", index.matchesName("None", product2));

//           continue;
//         }

//         if("Product".equals(index.getGroup())
//            && "Part".equals(index.getTitle()))
//         {
//           assertEquals("Part", 3,
//                        index.buildNames(BaseCampaign.GLOBAL
//                                         .getAbstractEntries()).size());

//           Iterator i =
//             index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
//             .iterator();
//           assertEquals("Part", "Book", i.next().toString());
//           assertEquals("Part", "Card", i.next().toString());
//           assertEquals("Part", "CD", i.next().toString());

//           assertFalse("Part", index.matchesName("guru", product1));
//           assertTrue("Part", index.matchesName("Card", product1));
//           assertFalse("Part", index.matchesName("CD", product1));
//           assertTrue("Part", index.matchesName("CD", product2));

//           continue;
//         }
//       }

//       BaseCampaign.GLOBAL.m_bases.clear();
//     }

    //......................................................................
    //----- format ---------------------------------------------------------

    /** Check format for overview. */
//     @org.junit.Test
//     public void format()
//     {
//       BaseProduct product = new BaseProduct("format", new DMAData("path"));

//       product.setSystem(System.DnD_3RD);
//       product.setProducer("TSR");
//       product.setProductType(ProductType.ADVENTURE);

//       List<Object> list = FORMATTER.format("key", product);

//       assertEquals("system", "D&D 3rd", extract(list.get(4), 1));
//       assertEquals("producer", "TSR", extract(list.get(5), 1));
//       assertEquals("type", "Adventure", extract(list.get(6), 1));
//     }

    //......................................................................
    //----- requirements ---------------------------------------------------

    /** Test handling and extracting requirements. */
//     @org.junit.Test
//     public void requirements()
//     {
//       BaseCampaign.GLOBAL.m_bases.clear();

//       BaseProduct product1 = new BaseProduct("product 1");
//       BaseProduct product2 = new BaseProduct("product 2");
//       BaseProduct product3 = new BaseProduct("product 3");
//       BaseProduct product4 = new BaseProduct("product 4");
//       BaseProduct product5 = new BaseProduct("product 5");

//       product1.setWorld("Forgotten Realms");
//       product2.setWorld("Generic");
//       product3.setWorld("Forgotten Realms");
//       product4.setWorld("Forgotten Realms");
//       product5.setWorld("Forgotten Realms");

//       product1.setSystem(System.DnD_3_5);
//       product2.setSystem(System.DnD_3_5);
//       product3.setSystem(System.DnD_3_5);
//       product4.setSystem(System.DnD_3RD);
//       product5.setSystem(System.DnD_3_5);

//       product1.setProductType(ProductType.RULEBOOK);
//       product2.setProductType(ProductType.RULEBOOK);
//       product3.setProductType(ProductType.RULEBOOK);
//       product4.setProductType(ProductType.RULEBOOK);
//       product5.setProductType(ProductType.ADVENTURE);

//       BaseCampaign.GLOBAL.add(product1);
//       BaseCampaign.GLOBAL.add(product2);
//       BaseCampaign.GLOBAL.add(product3);
//       BaseCampaign.GLOBAL.add(product4);
//       BaseCampaign.GLOBAL.add(product5);

//       SortedSet<String> requirements =
//         BaseProduct.getRequirements("Forgotten Realms",
//                                     System.DnD_3_5.toString());

//       Iterator<String> i = requirements.iterator();

//     assertEquals("requirements", "product 1 (D&D 3.5)::product 1", i.next());
//     assertEquals("requirements", "product 2 (D&D 3.5)::product 2", i.next());
//     assertEquals("requirements", "product 3 (D&D 3.5)::product 3", i.next());
//     assertEquals("requirements", "product 4 (D&D 3rd)::product 4", i.next());
//       assertFalse("requirements", i.hasNext());

//       // test for all the products
//       SortedSet<String> products =
//         BaseProduct.getProducts("Forgotten Realms");

//       i = products.iterator();

//     assertEquals("requirements", "product 1 (D&D 3.5)::product 1", i.next());
//     assertEquals("requirements", "product 2 (D&D 3.5)::product 2", i.next());
//     assertEquals("requirements", "product 3 (D&D 3.5)::product 3", i.next());
//     assertEquals("requirements", "product 4 (D&D 3rd)::product 4", i.next());
//     assertEquals("requirements", "product 5 (D&D 3.5)::product 5", i.next());
//       assertFalse("requirements", i.hasNext());

//       BaseCampaign.GLOBAL.m_bases.clear();
//     }

    //......................................................................
    //----- references ---------------------------------------------------

    /** Test handling and extracting references. */
//     @org.junit.Test
//     public void references()
//     {
//       BaseCampaign.GLOBAL.m_bases.clear();

//       BaseProduct product1 = new BaseProduct("product 1");
//       BaseProduct product2 = new BaseProduct("product 2");
//       BaseProduct product3 = new BaseProduct("product 3");
//       BaseProduct product4 = new BaseProduct("product 4");
//       BaseProduct product5 = new BaseProduct("product 5");

//       product1.setProductType(ProductType.CATALOG);
//       product2.setProductType(ProductType.RULEBOOK);
//       product3.setProductType(ProductType.CATALOG);
//       product4.setProductType(ProductType.CATALOG);
//       product5.setProductType(ProductType.ADVENTURE);

//       BaseCampaign.GLOBAL.add(product1);
//       BaseCampaign.GLOBAL.add(product2);
//       BaseCampaign.GLOBAL.add(product3);
//       BaseCampaign.GLOBAL.add(product4);

//       SortedSet<String> requirements =
//         BaseProduct.getAllReferences();

//       Iterator<String> i = requirements.iterator();

//       assertEquals("requirements", "product 1::product 1", i.next());
//       assertEquals("requirements", "product 3::product 3", i.next());
//       assertEquals("requirements", "product 4::product 4", i.next());
//       assertEquals("requirements", "www.paizo.com", i.next());
//       assertEquals("requirements", "www.wizards.com", i.next());
//       assertFalse("requirements", i.hasNext());


//       BaseCampaign.GLOBAL.m_bases.clear();
//     }

    //......................................................................
    //----- update ---------------------------------------------------------

//     /** Test updating persons. */
//     @org.junit.Test
//     public void update()
//     {
//       BaseProduct product = new BaseProduct("product 1");

//       product.addAuthor("a", null);
//       product.addAuthor("b", null);
//       product.addAuthor("c", null);
//       product.addEditor("b", null);
//       product.addCover("a", null);
//       product.addCover("b", null);

//       assertTrue("update", product.updatePersons("b", "guru"));

//       // authors
//       Iterator<Multiple> i = product.m_authors.iterator();

//       assertEquals("update", "a", i.next().get(0).get().toString());
//       assertEquals("update", "guru", i.next().get(0).get().toString());
//       assertEquals("update", "c", i.next().get(0).get().toString());
//       assertFalse("update", i.hasNext());

//       // editor
//       i = product.m_editors.iterator();

//       assertEquals("update", "guru", i.next().get(0).get().toString());
//       assertFalse("update", i.hasNext());

//       // cover
//       i = product.m_cover.iterator();

//       assertEquals("update", "a", i.next().get(0).get().toString());
//       assertEquals("update", "guru", i.next().get(0).get().toString());
//       assertFalse("update", i.hasNext());
//     }

    //......................................................................
 }

  //........................................................................
}
