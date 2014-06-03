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

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseProductProto;
import net.ixitxachitls.dma.values.Content;
import net.ixitxachitls.dma.values.Date;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Group;
import net.ixitxachitls.dma.values.ISBN;
import net.ixitxachitls.dma.values.ISBN13;
import net.ixitxachitls.dma.values.NewValue;
import net.ixitxachitls.dma.values.Person;
import net.ixitxachitls.dma.values.Price;
import net.ixitxachitls.dma.values.ProductReference;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the entry for base products, the basic description of a
 * product available.
 *
 * @file          BaseProduct.java
 * @author        balsiger@ixitxachils.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public class BaseProduct extends BaseEntry
{
  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The producers of products. */
  private static final List<String> PRODUCERS = ImmutableList.of
    ("WTC",
     "TSR",
     "Paizo",
     "Armorcast",
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
     "Harper Collins",
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
     "Q-Workshop",
     "Rackham",
     "Ral Partha",
     "Reaper",
     "RPG International",
     "RPGA",
     "Sterling' Publishing",
     "tosa",
     "Toy Vault",
     "White Wolf",
     "Wiley");

  /** The product parts. */
  public enum Part implements EnumSelection.Named
  {
    /** A game board. */
    BOARD("Board", BaseProductProto.Content.Part.BOARD),
    /** A normal book. */
    BOOK("Book", BaseProductProto.Content.Part.BOOK),
    /** A booklet. */
    BOOKLET("Booklet", BaseProductProto.Content.Part.BOOKLET),
    /** Some kind of box. */
    BOX("Box", BaseProductProto.Content.Part.BOX),
    /** A playing card or an item card. */
    CARD("Card", BaseProductProto.Content.Part.CARD),
    /** A cd with music or programs. */
    CD("CD", BaseProductProto.Content.Part.CD),
    /** Some kind of counter. */
    COUNTER("Counter", BaseProductProto.Content.Part.COUNTER),
    /** A cover for a book or booklet (separate). */
    COVER("Cover", BaseProductProto.Content.Part.COVER),
    /** Some dice, normal or special. */
    DICE("Dice", BaseProductProto.Content.Part.DICE),
    /** A flyer, basically a piece of paper with something on it. */
    FLYER("Flyer", BaseProductProto.Content.Part.FLYER),
    /** A fold to organize things in it. */
    FOLDER("Folder", BaseProductProto.Content.Part.FOLDER),
    /** Like a cover with a booklet it in, but the cover can be folded out. */
    GATEFOLD("Gatefold", BaseProductProto.Content.Part.GATEFOLD),
    /** A magnet with some special design. */
    MAGNET("Magnet", BaseProductProto.Content.Part.MAGNET),
    /** A map used for playing (battle map) or for showing the lay of the
     *  environment. */
    MAP("Map", BaseProductProto.Content.Part.MAP),
    /** Some miniature figure that can be used for play or for decoration. */
    MINIATURE("Miniature", BaseProductProto.Content.Part.MINIATURE),
    /** Something not covered in any of the other categories. */
    MISC("Misc", BaseProductProto.Content.Part.MISC),
    /** An transparent or partially transparent sheet that is used as an
     *  overlay over something else (e.g. a map). */
    OVERLAY("Overlay", BaseProductProto.Content.Part.OVERLAY),
    /** A pack o other things, usually cards. */
    PACK("Pack", BaseProductProto.Content.Part.PACK),
    /** A page of paper. */
    PAGE("Page", BaseProductProto.Content.Part.PAGE),
    /** A playing piece other than a miniature. */
    PLAYING_PIECE("Playing Piece", BaseProductProto.Content.Part.PLAYING_PIECE),
    /** A poster other than a map. */
    POSTER("Poster", BaseProductProto.Content.Part.POSTER),
    /** A screen the DM can be used to not be observed too closely. */
    SCREEN("Screen", BaseProductProto.Content.Part.SCREEN),
    /** A sheet of paper, e.g. a handout. */
    SHEET("Sheet", BaseProductProto.Content.Part.SHEET),
    /** A sticker. */
    STICKER("Sticker", BaseProductProto.Content.Part.STICKER);

    /** The value's name. */
    private String m_name;

    /** The proto value. */
    private BaseProductProto.Content.Part m_proto;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     * @param inPart     the proto value
     *
     */
    private Part(String inName, BaseProductProto.Content.Part inPart)
    {
      m_name = constant("product.part", inName);
      m_proto = inPart;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
        return m_name;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseProductProto.Content.Part toProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inPart     the proto value to look for
     * @return the matched group (will throw exception if not found)
     */
    public static Part fromProto(BaseProductProto.Content.Part inPart)
    {
      for(Part part : values())
        if(part.m_proto == inPart)
          return part;

      throw new IllegalStateException("invalid proto part: " + inPart);
    }

    /**
     * Get the part matching the given text.
     */
    public static Optional<Part> fromString(String inText)
    {
      for(Part part : values())
        if(part.m_name.equalsIgnoreCase(inText))
          return Optional.of(part);

      return Optional.absent();
    }

    /**
     * All the possible names for the layout.
     *
     * @return the possible names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();

      for(Part part : values())
        names.add(part.getName());

      return names;
    }
  }

  /** The product layouts. */
  public enum Layout implements EnumSelection.Named
  {
    /** An undefined layout. */
    UNKNOWN("Unknown", BaseProductProto.Layout.UNKNOWN_LAYOUT),
    /** A product with full color on most pages. */
    FULL_COLOR("Full Color", BaseProductProto.Layout.FULL_COLOR),
    /** A product that uses 4 colors on most pages. */
    FOUR_COLOR("4 Color", BaseProductProto.Layout.FOUR_COLOR),
    /** A product that uses a two color print. */
    TWO_COLOR("2 Color", BaseProductProto.Layout.TWO_COLOR),
    /** A product that is basically black & white but has a color cover. */
    COLOR_COVER("Color Cover", BaseProductProto.Layout.COLOR_COVER),
    /** The product is completely in black & white. */
    BLACK_AND_WHITE("Black & White", BaseProductProto.Layout.BLACK_AND_WHITE),
    /** The product is mixed between different layout, with none really
     *  dominant (otherwise use the dominant layout). */
    MIXED("Mixed", BaseProductProto.Layout.MIXED);

    /** The value's name. */
    private String m_name;

    /** The layout proto value. */
    private BaseProductProto.Layout m_proto;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the proto enum value
     */
    private Layout(String inName, BaseProductProto.Layout inProto)
    {
      m_name = constant("product.layout", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
        return m_name;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseProductProto.Layout toProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inLayout    the proto value to look for
     * @return the matched group (will throw exception if not found)
     */
    public static Layout fromProto(BaseProductProto.Layout inLayout)
    {
      for(Layout layout : values())
        if(layout.m_proto == inLayout)
          return layout;

      throw new IllegalStateException("invalid proto layout: " + inLayout);
    }

    /**
     * Get the layout matching the given text.
     */
    public static Optional<Layout> fromString(String inText)
    {
      for(Layout layout : values())
        if(layout.m_name.equalsIgnoreCase(inText))
          return Optional.of(layout);

      return Optional.absent();
    }

    /**
     * All the possible names for the layout.
     *
     * @return the possible names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();

      for(Layout layout : values())
        names.add(layout.getName());

      return names;
    }
  }

  /** The game system. */
  public enum System implements EnumSelection.Named
  {
    /** No game system, e.g. for novels. */
    NONE("None", BaseProductProto.System.NONE, null),
    /** Chainmail. */
    CHAINMAIL("Chainmail", BaseProductProto.System.CHAINMAIL, null),
    /** Dungeons & Dragons, original edition. */
    DnD_1ST("D&D 1st", BaseProductProto.System.DND_1ST, "D&D 1st"),
    /** Advanced Dungeon & Dragons, first edition. */
    ADnD_1ST("AD&D 1st", BaseProductProto.System.ADND_1ST, "AD&D 1st"),
    /** Advanced Dungeon & Dragons, second edition, together with the Saga
     * rules. */
    ADnD_2ND_SAGA("AD&D 2nd & Saga", BaseProductProto.System.ADND_2ND_SAGA,
                  "AD&D 2nd"),
    /** Advanced Dungeon & Dragons, second edition. */
    ADnD_2ND("AD&D 2nd", BaseProductProto.System.ADND_2ND, "AD&D 2nd"),
    /** Advanced Dungeon & Dragons, second but revised edition (new logo). */
    ADnD_REVISED("AD&D revised", BaseProductProto.System.ADND_REVISED,
                 "AD&D 2nd"),
    /** Dungeons & Dragons, third edition. */
    DnD_3RD("D&D 3rd", BaseProductProto.System.DND_3RD, "D&D 3rd"),
    /** Dungeons & Dragons, version 3.5. */
    DnD_3_5("D&D 3.5", BaseProductProto.System.DND_3_5, "D&D 3rd"),
    /** Dungeon & Dragons, fourth edition. */
    DnD_4("D&D 4th", BaseProductProto.System.DND_4, "D&D 4th"),
    /** Games with d20 modern rules. */
    DnD_NEXT("D&D Next", BaseProductProto.System.DND_NEXT, "D&D Next"),
    /** Games with d20 modern rules. */
    D20_MODERN("d20 Modern", BaseProductProto.System.D20_MODERN, null),
    /** Games with d20 future rules. */
    D20_FUTURE("d20 Future", BaseProductProto.System.D20_FUTUTRE, null),
    /** Games with the d20 fantasy rules. */
    D20("d20", BaseProductProto.System.D20, null),
    /** Games with the science-fition rules of Alternaty. */
    ALTERNITY("Alternity", BaseProductProto.System.ALTERNITY, null),
    /** Amazing Engine games. */
    AMAZING_ENGINE("Amazing Engine", BaseProductProto.System.AMAZING_ENGINE,
                   null),
    /** The Blood Wars card game rules. */
    BLOOD_WARS("Blood Wars", BaseProductProto.System.BLOOD_WARS, null),
    /** Games using the Chaosium rules. */
    CHAOSIUM("Chaosium", BaseProductProto.System.CHAOSIUM, null),
    /** Miniatures from the Dark Heaven line. */
    DARK_HEAVEN("Dark Heaven", BaseProductProto.System.DARK_HEAVEN, null),
    /** Dragon Dice game system. */
    DRAGON_DICE("Dragon Dice", BaseProductProto.System.DRAGON_DICE, null),
    /** Dragon Strike game system. */
    DRAGON_STRIKE("Dragon Strike", BaseProductProto.System.DRAGON_STRIKE, null),
    /** Duel Master games. */
    DUEL_MASTER("Duel Master", BaseProductProto.System.DUEL_MASTER, null),
    /** Books with the Endless Quest system. */
    ENDLESS_QUEST("Endless Quest", BaseProductProto.System.ENDLESS_QUEST, null),
    /** First Quest introductory games. */
    FIRST_QUEST("First Quest", BaseProductProto.System.FIRST_QUEST, null),
    /** Games with the Gamma World rule. */
    GAMMA_WORLD("Gamma World", BaseProductProto.System.GAMMA_WORLD, null),
    /** Games with the Ganbusters rules. */
    GANGBUSTERS("Gangbusters", BaseProductProto.System.GANGBUSTERS, null),
    /** The legend of the Five Rings oriental fantasy rules. */
    LEGEND_OF_THE_FIVE_RINGS("Legend of the Five Rings",
                             BaseProductProto.System.LEGEND_OF_THE_FIVE_RINGS,
                             null),
    /** Card games with the Magic: the Gathering rules. */
    MAGIC_THE_GAHTERING("Magic: The Gathering",
                        BaseProductProto.System.MAGIC_THE_GATHERING, null),
    /** The Marvel Super Dice rules. */
    MARVEL_SUPER_DICE("Marvel Super Dice",
                      BaseProductProto.System.MARVEL_SUPER_DICE, null),
    /** Games using the Marvel Super Heroes rules. */
    MARVEL_SUPER_HEROES("Marvel Super Heroes",
                        BaseProductProto.System.MARVEL_SUPER_HEROES, null),
    /** Card games according to the MLB Showdown 2002 rules. */
    MLB_SHOWDOWN_2002("MLB Showdown 2002",
                      BaseProductProto.System.MLB_SHOWDOWN_2002, null),
    /** Card games according to the MLB Showdown 2003 rules. */
    MLB_SHOWDOWN_2003("MLB Showdown 2003",
                      BaseProductProto.System.MLB_SHOWDOWN_2003, null),
    /** Card games according to the MLB Showdown rules. */
    MLB_SHOWDOWN("MLB Showdown", BaseProductProto.System.MLB_SHOWDOWN, null),
    /** Books with the Neopets rules. */
    NEOPETS("Neopets", BaseProductProto.System.NEOPETS, null),
    /** Games with 1 on 1 rules. */
    ONE_ON_ONE("1 on 1", BaseProductProto.System.ONE_ON_ONE, null),
    /** Games inside the Pokemon universe. */
    POKEMON("Pokemon", BaseProductProto.System.POKEMON, null),
    /** Games using the Saga narrative rules. */
    SAGA("Saga", BaseProductProto.System.SAGA, null),
    /** Games using special rules (e.g. not covered by other systems). */
    SPECIAL("Special", BaseProductProto.System.SPECIAL, null),
    /** The spellfire card game. */
    SPELLFIRE("Spellfire", BaseProductProto.System.SPELLFIRE, null),
    /** The star wars trading card game rules. */
    STAR_WARS_TCG("Star Wars TCG", BaseProductProto.System.STAR_WARS_TCG, null),
    /** Games for the star wars role playing game. */
    STAR_WARS("Star Wars", BaseProductProto.System.STAR_WARS, null),
    /** Books with the super endless quest system. */
    SUPER_ENDLESS_QUEST("Super Endless Quest",
                        BaseProductProto.System.SUPER_ENDLESS_QUEST, null),
    /** Games for the Sword & Sorcery rules. */
    SWORD_SORCERY("Sword & Sorcery", BaseProductProto.System.SWORD_AND_SORCERY,
                  null),
    /** Games using the Terror Tracks system. */
    TERROR_TRACKS("Terror Tracks", BaseProductProto.System.TERROR_TRACKS, null),
    /** Gemes using the Terror T.R.A.X. system. */
    TERROR_TRAX("Terror T.R.A.X.", BaseProductProto.System.TERROR_TRAX, null),
    /** Games for Wild Space. */
    WILD_SPACE("Wild Space", BaseProductProto.System.WILD_SPACE, null),
    /** Games for World War II. */
    WORLD_WAR_II("World War II", BaseProductProto.System.WORLD_WAR_II, null),
    /** Games for the XXVC sci-fi game. */
    XXVC("XXVC", BaseProductProto.System.XXVC, null),
    /** Unknown system. */
    UNKNOWN("Unknown", BaseProductProto.System.UNKNOWN_SYSTEM, null);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseProductProto.System m_proto;

    /** The group of styles, if any. */
    private @Nullable String m_group;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the proto enumv value
     * @param inGroup    the group (if any) to use when sorting by system.
     */
    private System(String inName, BaseProductProto.System inProto,
                   @Nullable String inGroup)
    {
      m_name  = constant("system.name",  inName);
      m_proto = inProto;

      if(inGroup != null)
        m_group = constant("system.group", inName, inGroup);
    }

    @Override
    public String getName()
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

    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Get the enum with the given name (case insensitive).
     *
     * @param  inName the name to look for
     *
     * @return the enum value found or null if not found
     */
    public static @Nullable System valueOfIgnoreCase(String inName)
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

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseProductProto.System toProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inSystem the proto value to look for
     * @return the matched group (will throw exception if not found)
     */
    public static System fromProto(BaseProductProto.System inSystem)
    {
      for(System system : values())
        if(system.m_proto == inSystem)
          return system;

      throw new IllegalStateException("invalid proto system: " + inSystem);
    }

    /**
     * Get the system matching the given text.
     */
    public static Optional<System> fromString(String inText)
    {
      for(System system : values())
        if(system.m_name.equalsIgnoreCase(inText))
          return Optional.of(system);

      return Optional.absent();
    }

    /**
     * All the possible names for the system.
     *
     * @return the possible names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();

      for(System system : values())
        names.add(system.getName());

      return names;
    }
  }

  /** The product types. */
  public enum ProductType implements EnumSelection.Named
  {
    /** A game accessory, e.g. an optional product enhancing the game. */
    ACCESSORY("Accessory", BaseProductProto.Type.ACCESSORY, "Accessories"),
    /** A game adventure. */
    ADVENTURE("Adventure", BaseProductProto.Type.ADVENTURE, "Adventures"),
    /** A board game, usually outside of rpg. */
    BOARD_GAME("Board Game", BaseProductProto.Type.BOARD_GAME, null),
    /** A booster pack for a trading card or miniature game. */
    BOOSTER_PACK("Booster Pack", BaseProductProto.Type.BOOSTER_PACK, null),
    /** A calendar (no game). */
    CALENDAR("Calendar", BaseProductProto.Type.CALENDAR, null),
    /** An expansion to a campaign. */
    CAMPAIGN_EXPANSION("Campaign Expansion",
                       BaseProductProto.Type.CAMPAIGN_EXPANSION, "Accessories"),
    /** The base rules and description of a campaign. */
    CAMPAIGN_SETTING("Campaign Setting",
                     BaseProductProto.Type.CAMPAIGN_SETTING, "Accessories"),
    /** A card game. */
    CARD_GAME("Card Game", BaseProductProto.Type.CARD_GAME, null),
    /** A bunch of cards that are part of something else. */
    CARDS("Cards", BaseProductProto.Type.CARDS_TYPE, null),
    /** A catalog listing products. */
    CATALOG("Catalog", BaseProductProto.Type.CATALOG, "Others"),
    /** A collection of other things. */
    COLLECTION("Collection", BaseProductProto.Type.COLLECTION, null),
    /** A comics book. */
    COMICS("Comics", BaseProductProto.Type.COMICS, "Comics"),
    /** A book with cooking recipies. */
    COOKBOOK("Cookbook", BaseProductProto.Type.COOKBOOK, null),
    /** One or more dice. */
    DICE("Dice", BaseProductProto.Type.DICE, null),
    /** Additional information in electronic form (not programs). */
    ELECTRONIC_ACCESSORY("Electronic Accessory",
                         BaseProductProto.Type.ELECTRONIC_ACCESSORY, null),
    /** A guide for a game or something else. */
    GUIDE("Guide", BaseProductProto.Type.GUIDE, null),
    /** A gaming magazine. */
    MAGAZINE("Magazine", BaseProductProto.Type.MAGAZINE, "Magazines"),
    /** One ore more miniatures. */
    MINIATURE("Miniature", BaseProductProto.Type.MINIATURE, null),
    /** A compendium with monsters. */
    MONSTER_COMPENDIUM("Monster Compendium",
                       BaseProductProto.Type.MONSTER_COMPENDIUM, "Accessories"),
    /** A novel. */
    NOVEL("Novel", BaseProductProto.Type.NOVEL, "Novels"),
    /** A promotional product. */
    PROMOTION("Promotion", BaseProductProto.Type.PROMOTION, "Others"),
    /** A book with basic rules (usually the core rules books). */
    RULEBOOK("Rulebook", BaseProductProto.Type.RULEBOOK, "Rulebooks"),
    /** A supplement with addtional rules. */
    RULES_SUPPLEMENT("Rules Supplement",
                     BaseProductProto.Type.RULES_SUPPLEMENT, "Accessories"),
    /** A software tool to support the game. */
    SOFTWARE("Software", BaseProductProto.Type.SOFTWARE, null),
    /** A source book about rules and descriptions of real world things. */
    SOURCEBOOK("Sourcebook", BaseProductProto.Type.SOURCEBOOK, "Accessories"),
    /** Something not covered by all the other types. */
    SPECIAL_BOOK("Special Book", BaseProductProto.Type.SPECIAL_BOOK, "Others"),
    /** Unknown type. */
    UNKNOWN("Unknown", BaseProductProto.Type.UNKNOWN_TYPE, "Others");

    /** The value's name. */
    private String m_name;

    /** The proto enum. */
    private BaseProductProto.Type m_proto;

    /** The group of styles, if any. */
    private @Nullable String m_group = null;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the prot enum value
     * @param inGroup    the group used for sorting
     *
     */
    private ProductType(String inName, BaseProductProto.Type inProto,
                        @Nullable String inGroup)
    {
      m_name  = constant("product.type.name",  inName);
      m_proto = inProto;
      if(inGroup != null)
        m_group = constant("product.type.group", inName, inGroup);
    }

    @Override
    public String getName()
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

    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseProductProto.Type toProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto the proto value to look for
     * @return the matched group (will throw exception if not found)
     */
    public static ProductType fromProto(BaseProductProto.Type inProto)
    {
      for(ProductType type : values())
        if(type.m_proto == inProto)
          return type;

      throw new IllegalStateException("invalid proto type: " + inProto);
    }

    /**
     * Get the product type matching the given text.
     */
    public static Optional<ProductType> fromString(String inText)
    {
      for(ProductType type : values())
        if(type.m_name.equalsIgnoreCase(inText))
          return Optional.of(type);

      return Optional.absent();
    }

    /**
     * All the possible names for the product type.
     *
     * @return the possible names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();

      for(ProductType type : values())
        names.add(type.getName());

      return names;
    }
  }

  /** The product styles. */
  public enum Style implements EnumSelection.Named
  {
    /** A booklet, i.e. a small, stapled book. */
    BOOKLET("Booklet", BaseProductProto.Style.BOOKLET, null),
    /** A large box (A4 or bigger). */
    BOX("Box", BaseProductProto.Style.BOX, "Boxes"),
    /** A set of cards. */
    CARDS("Cards", BaseProductProto.Style.CARDS_STYLE, null),
    /** A sheet of paper. */
    FLYER("Flyer", BaseProductProto.Style.FLYER, null),
    /** A folder to store other things. */
    FOLDER("Folder", BaseProductProto.Style.FOLDER, null),
    /** A hardcover book. */
    HARDCOVER("Hardcover", BaseProductProto.Style.HARDCOVER, null),
    /** A map. */
    MAP("Map", BaseProductProto.Style.MAP, null),
    /** A medium box, roughly A5 or similar. */
    MEDIUM_BOX("Medium Box", BaseProductProto.Style.MEDIUM_BOX, "Medium Boxes"),
    /** A pack of cards or miniatures. */
    PACK("Pack", BaseProductProto.Style.PACK, null),
    /** A normal paperback book. */
    PAPERBAKC("Paperback", BaseProductProto.Style.PAPERBACK, null),
    /** A poster. */
    POSTER("Poster", BaseProductProto.Style.POSTER, null),
    /** A screen for the DM to guard his secrets. */
    SCREEN("Screen", BaseProductProto.Style.SCREEN, null),
    /** A bunch of sheets. */
    SHEETS("Sheets", BaseProductProto.Style.SHEETS, null),
    /** A small box, usually for miniatures or similar. */
    SMALL_BOX("Small Box", BaseProductProto.Style.SMALL_BOX, "Small Boxes"),
    /** A bound book with a soft cover. */
    SOFT_COVER("Soft Cover", BaseProductProto.Style.SOFT_COVER, null),
    /** A sticker. */
    STICKER("Sticker", BaseProductProto.Style.STICKER, null),
    /** Unknown. */
    UNKNOWN("Unknown", BaseProductProto.Style.UNKNOWN_STYLE, null);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseProductProto.Style m_proto;

    /** The group of styles, if any. */
    private @Nullable String m_group;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the proto value
     * @param inGroup    the group to use for sorting
     *
     */
    private Style(String inName, BaseProductProto.Style inProto,
                  @Nullable String inGroup)
    {
      m_name  = constant("product.style.name",  inName);
      m_proto = inProto;
      if(inGroup != null)
        m_group = constant("product.style.group", inName, inGroup);
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
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseProductProto.Style toProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto the proto value to look for
     * @return the matched group (will throw exception if not found)
     */
    public static Style fromProto(BaseProductProto.Style inProto)
    {
      for(Style style: values())
        if(style.m_proto == inProto)
          return style;

      throw new IllegalStateException("invalid proto style: " + inProto);
    }

    /**
     * Get the style matching the given text.
     */
    public static Optional<Style> fromString(String inText)
    {
      for(Style style : values())
        if(style.m_name.equalsIgnoreCase(inText))
          return Optional.of(style);

      return Optional.absent();
    }

    @Override
    public String getName()
    {
      return m_name;
    }

   /**
     * All the possible names for the style.
     *
     * @return the possible names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();

      for(Style style : values())
        names.add(style.getName());

      return names;
    }
  }

  /** The audiences for products. */
  public enum Audience implements EnumSelection.Named
  {
    /** Material indented for the DM only. */
    DM("DM", BaseProductProto.Audience.DM),
    /** Material targeted mostly to players. */
    PLAYER("Player", BaseProductProto.Audience.PLAYER),
    /** Material that is open to all. */
    ALL("All", BaseProductProto.Audience.ALL),
    /** Unknown. */
    UNKNOWN("Unknown", BaseProductProto.Audience.UNKNOWN);

    /** The value's name. */
    private String m_name;

    /** The proto enum. */
    private BaseProductProto.Audience m_proto;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the proto value
     */
    private Audience(String inName, BaseProductProto.Audience inProto)
    {
      m_name = constant("audiences", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseProductProto.Audience toProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static Audience fromProto(BaseProductProto.Audience inProto)
    {
      for(Audience audience: values())
        if(audience.m_proto == inProto)
          return audience;

      throw new IllegalStateException("invalid proto audience: " + inProto);
    }

    /**
     * Get the audience matching the given text.
     */
    public static Optional<Audience> fromString(String inText)
    {
      for(Audience audience : values())
        if(audience.m_name.equalsIgnoreCase(inText))
          return Optional.of(audience);

      return Optional.absent();
    }

    /**
     * All the possible names for the audience.
     *
     * @return the possible names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();

      for(Audience audience: values())
        names.add(audience.getName());

      return names;
    }
  }

  /**
   * This is the internal, default constructor.
   */
  protected BaseProduct()
  {
    super(TYPE);
  }

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base product
   */
  public BaseProduct(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final BaseType<BaseProduct> TYPE =
    new BaseType<BaseProduct>(BaseProduct.class).withSort("title");

  /** The title of the product. */
  protected String m_title = UNDEFINED_STRING;

  /** The leader of the product, any 'a', 'the' and the like. */
  protected String m_leader = UNDEFINED_STRING;

  /** The sub title of the product. */
  protected String m_subtitle = UNDEFINED_STRING;

  /** Notes about the product. */
  protected String m_notes = UNDEFINED_STRING;

  /** All the authors of the product. */
  protected List<Person> m_authors = new ArrayList<>();

  /** All the editors of the product. */
  protected List<Person> m_editors = new ArrayList<>();

  /** All the cover artists. */
  protected List<Person> m_cover = new ArrayList<>();

  /** The cartographers for the product. */
  protected List<Person> m_cartographers = new ArrayList<>();

  /** The illustration artists for the product. */
  protected List<Person> m_illustrators = new ArrayList<>();

  /** The typographers for this product. */
  protected List<Person> m_typographers = new ArrayList<>();

  /** All the mangers and other people involved in the product creation. */
  protected List<Person> m_managers = new ArrayList<>();

  /** The date (month and year) the product was released. */
  protected @Nullable Date m_date = null;

  /** The product's ISBN number, if it has one. */
  protected @Nullable ISBN m_isbn = null;

  /** The product's ISBN 13 number, if it has one. */
  protected @Nullable ISBN13 m_isbn13 = null;

  /** The grouping for the pages. */
  protected static final Group<Integer, Integer, String> s_pageGroup =
    new Group<Integer, Integer, String>(new Group.Extractor<Integer, Integer>()
      {
        @Override
        public Integer extract(Integer inValue)
        {
          return inValue;
        }
      }, new Integer [] { 5, 10, 20, 25, 50, 100, 200, 250, 300, 400, 500, },
      new String []
      { "5", "10", "20", "25", "50", "100", "200", "250", "300", "400", "500",
        "500+", }, "$undefined");

  /** The total number of pages of the product. */
  protected @Nullable Integer m_pages = null;

  /** The game system of the product. */
  protected System m_system = System.UNKNOWN;

  /** The intended audience of the product. */
  protected Audience m_audience = Audience.UNKNOWN;

  /** The type of product. */
  protected ProductType m_productType = ProductType.UNKNOWN;

  /** The style of the product, its general outlook. */
  protected Style m_style = Style.UNKNOWN;

  /** The name of the company that produced the product. */
  protected String m_producer = UNDEFINED_STRING;

  /** The volume of the product for multi volume products. */
  protected String m_volume = UNDEFINED_STRING;

  /** The number of the series. */
  protected String m_number = UNDEFINED_STRING;

  /**
   * The name of the series, even multiple if necessary, this product belongs
   *  to.
   */
  protected List<String> m_series = new ArrayList<>();

  /** The grouping for the prices. */
  protected static final Group<Price, Double, String> s_priceGrouping =
    new Group<Price, Double, String>(new Group.Extractor<Price, Double>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Double extract(Price inValue)
        {
          return inValue.getPrice();
        }
      }, new Double [] { 1.00, 5.00, 10.00, 25.00, 50.00, 100.00, },
                                   new String []
        { "1", "5", "10", "25", "50", "100", "a fortune", }, "$undefined$");

  /** This is the price of the series. */
  protected @Nullable Price m_price = null;

  /** The contents of the product, what kind of individual components it has,
   *  if any. */
  @Key("contents")
  protected List<Content> m_contents = new ArrayList<>();

  /** The mandatory requirements for this product. */
  protected List<ProductReference> m_mandatoryRequirements = new ArrayList<>();

  /** THe optional requirements for this product. */
  protected List<ProductReference> m_optionalRequirements = new ArrayList<>();

  /** The layout of the product. */
  protected Layout m_layout = Layout.UNKNOWN;

  /**
   * Get the audience of the product.
   *
   * @return      the audience
   */
  public Audience getAudience()
  {
    return m_audience;
  }

  /**
   * Get the layout of the product.
   *
   * @return the product's layout
   */
  public @Nullable Layout getLayout()
  {
    return m_layout;
  }

  /**
   * Get the notes for the product.
   *
   * @return the notes
   */
  public String getNotes()
  {
    return m_notes;
  }

  /**
   * Get the authors (with jobs) of the product.
   *
   * @return a list of persons with jobs
   */
  public List<Person> getAuthors()
  {
    return Collections.unmodifiableList(m_authors);
  }

  /**
   * Get the editors (with jobs) of the product.
   *
   * @return a list of persons with jobs
   */
  public List<Person> getEditors()
  {
    return Collections.unmodifiableList(m_editors);
  }

  /**
   * Get the cover artists (with jobs) of the product.
   *
   * @return a list of persons with jobs
   */
  public List<Person> getCoverArtists()
  {
    return Collections.unmodifiableList(m_cover);
  }

  /**
   * Get the cartographers (with jobs) of the product.
   *
   * @return a list of persons with jobs
   */
  public List<Person> getCartographers()
  {
    return Collections.unmodifiableList(m_cartographers);
  }

  /**
   * Get the illustrators (with jobs) of the product.
   *
   * @return a list of persons with jobs
   */
  public List<Person> getIllustrators()
  {
    return Collections.unmodifiableList(m_illustrators);
  }

  /**
   * Get the typographers (with jobs) of the product.
   *
   * @return a list of persons with jobs
   */
  public List<Person> getTypographers()
  {
    return Collections.unmodifiableList(m_typographers);
  }

  /**
   * Get the managers (with jobs) of the product.
   *
   * @return a list of persons with jobs
   */
  public List<Person> getManagers()
  {
    return Collections.unmodifiableList(m_managers);
  }

  /**
   * Get the date of the product.
   *
   * @return      the date
   */
  public String getDate()
  {
    if(m_date == null)
      return "";

    return m_date.toString();
  }

  /**
   * Get the mandatory requirements.
   *
   * @return the mandatory products
   */
  public List<ProductReference> getMandatoryRequirements()
  {
    return m_mandatoryRequirements;
  }

  /**
   * Get the names of the mandatory requirements.
   *
   * @return the names only of the requirements
   */
  public List<String> getMandatoryRequirementNames()
  {
    List<String> names = new ArrayList<>();
    for(ProductReference refernece : m_mandatoryRequirements)
      names.add(refernece.getName());

    return names;
  }

  /**
   * Get the optional requirements.
   *
   * @return the optional requirements
   */
  public List<ProductReference> getOptionalRequirements()
  {
    return m_optionalRequirements;
  }

  /**
   * Get the names of the optional requirements.
   *
   * @return the names only of the requirements
   */
  public List<String> getOptionalRequirementNames()
  {
    List<String> names = new ArrayList<>();
    for(ProductReference refernece : m_optionalRequirements)
      names.add(refernece.getName());

    return names;
  }

  /**
   * Accessor for the leader of the base product.
   *
   * @return      the requested leader
   */
  public String getLeader()
  {
    return m_leader;
  }

  /**
   * Get the series number of the product.
   *
   * @return      the series number
   */
  public String getNumber()
  {
    return m_number;
  }

  /**
   * Get the series number of the product as an integer.
   *
   * @return      the series number
   *
   */
  public int getNumberValue()
  {
    if(m_number.isEmpty())
      return 0;

    return Strings.extractNumber(m_number);
  }


  /**
   * Get the pages of the product.
   *
   * @return      the pages or -1 if undefined
   */
  public int getPages()
  {
    if(m_pages == null)
      return -1;

    return m_pages;
  }


  /**
   * Get the series of the product.
   *
   * @return      the series
   */
  public List<String> getSeries()
  {
    return m_series;
  }

  /**
    * Check if the product has a series value.
    *
    * @return      true if series is set, false if not
    */
  public boolean hasSeries()
  {
    return !m_series.isEmpty();
  }


  /**
   * Get the style of the product.
   *
   * @return      the style
   */
  public Style getStyle()
  {
    return m_style;
  }


  /**
   * Get the producer of the product.
   *
   * @return      the producer
   */
  public String getProducer()
  {
    return m_producer;
  }

  /**
   * Get all possible producers.
   *
   * @return the possible producers
   */
  public List<String> getProducers()
  {
    return PRODUCERS;
  }

  /**
   * Get the system of the product.
   *
   * @return      the system
   */
  public System getSystem()
  {
    return m_system;
  }

  /**
   * Accessor for the title of the base product.
   *
   * @return      the requested title
   */
  public String getTitle()
  {
    return m_title;
  }

  /**
   * Accessor for the subtitle of the product.
   *
   * @return the subtitle
   */
  public String getSubtitle()
  {
    return m_subtitle;
  }

  /**
   * Accessor for the full title of the base product.
   *
   * @return      the requested title
   */
  public String getFullTitle()
  {
    if(m_leader.isEmpty())
      return m_title;

    return m_leader + " " + m_title;
  }


  /**
   * Get the type of the product.
   *
   * @return      the type
   */
  public ProductType getProductType()
  {
    return m_productType;
  }

  /**
   * Get the volume of the product.
   *
   * @return      the volume
   */
  public String getVolume()
  {
    return m_volume;
  }


  /**
   * Get the name of the entry as a reference for humans (not necessarily how
   * it can be found in a campaign).
   *
   * @return      the requested name
   */
  @Override
  public String getRefName()
  {
    if(m_title.isEmpty())
      return super.getRefName();

    if(!m_leader.isEmpty())
      return getTitle() + ", " + getLeader();

    return getTitle();
  }

  /**
   * Get the categories of the entry.
   *
   * @return      the categories
   */
  @Override
  public List<String> getCategories()
  {
    List<String> categories = super.getCategories();
    ProductType type = getProductType();

    if(type == ProductType.UNKNOWN || categories.contains(type.toString()))
      return categories;

    List<String> result = new ArrayList<>(categories);
    result.add(getProductType().toString());

    return result;
  }

  /**
   * Get the price for the product.
   *
   * @return the original selling price
   */
  public String getPrice()
  {
    if(m_price == null)
      return "";

    return m_price.toString();
  }

  /**
   * Get the contents of the product.
   *
   * @param a list of contents
   */
  public List<Content> getContents()
  {
    return m_contents;
  }

  public List<String> getPartNames()
  {
    return Part.names();
  }


  /**
   * Get the isbn value.
   *
   * @return  the isbn value
   */
  public String getISBN()
  {
    if(m_isbn == null)
      return "";

    return m_isbn.toString();
  }

  /**
   * Get the isbn type 13 value.
   *
   * @return the isbn 13 value
   */
  public String getISBN13()
  {
    if(m_isbn13 == null)
      return "";

    return m_isbn13.toString();
  }

 /**
   * Collect all available persons from this product and add them to the given
   * set.
   *
   * @param    ioNames     the set to add to
   *
   * @return   the set of persons given
   */
  public Set<? super String> collectPersons(Set<? super String> ioNames)
  {
    for(List<Person> list : categoryLists().values())
      for(Person person : list)
        ioNames.add(person.getName());

    return ioNames;
  }

  /**
   * Collect all available jobs from this product and add them to the given
   * set.
   *
   * @param    ioJobs      the set to add to
   *
   * @return   the set of jobs given
   */
  public Set<? super String> collectJobs(Set<? super String> ioJobs)
  {
    for(Map.Entry<String, List<Person>> list : categoryLists().entrySet())
    {
      String listName = list.getKey();
      for(Person person : list.getValue())
        {
          ioJobs.add(listName);

          if(!person.hasJob())
            continue;

          ioJobs.add(person.getJob());
        }
    }

    return ioJobs;
  }

  /**
   * Get the lists of name values for the given job.
   *
   * @return    the names of the requested category or null if not found
   */
  private @Nullable Map<String, List<Person>> categoryLists()
  {
    return new ImmutableMap.Builder<String, List<Person>>()
      .put("author", m_authors)
      .put("editor", m_editors)
      .put("cover", m_cover)
      .put("cartographer", m_cartographers)
      .put("illustrator", m_illustrators)
      .put("typographer", m_typographers)
      .put("management", m_managers)
      .build();
  }

  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(BaseCharacter.Group.USER);
  }

  /**
   * Compute the owners of the product.
   *
   * @return   the compute value
   */
  public Multimap<String, String> owners()
  {
    return DMADataFactory.get().getOwners(this.getName());
  }

  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    // persons
    Set<String> persons = new HashSet<String>();
    collectPersons(persons);

    for(String person : persons)
      values.put(Index.Path.PERSONS, person);

    // jobs
    Set<String> jobs = new HashSet<String>();
    collectJobs(jobs);

    for(String job : jobs)
      values.put(Index.Path.JOBS, job);

    // date
    if(m_date != null)
    {
      String month = m_date.getMonthAsString();
      if(month.isEmpty())
        values.put(Index.Path.DATES,
                   Index.groupsToString("" + m_date.getYear()));
      else
        values.put(Index.Path.DATES,
                   Index.groupsToString("" + m_date.getYear(), month));
    }
    else
      values.put(Index.Path.DATES, Value.UNDEFINED);

    // audience
    values.put(Index.Path.AUDIENCES, m_audience.toString());

    // system
    values.put(Index.Path.SYSTEMS, m_system.toString());

    // type
    values.put(Index.Path.TYPES, m_productType.toString());

    // style
    values.put(Index.Path.STYLES, m_style.toString());

    // style
    values.put(Index.Path.PRODUCERS, m_producer.toString());

    // layout
    values.put(Index.Path.LAYOUTS, m_layout.toString());

    // series
    for(String series : m_series)
      values.put(Index.Path.SERIES, series);

    // page
    values.put(Index.Path.PAGES, s_pageGroup.group(m_pages));

    // price
    values.put(Index.Path.PRICES, s_priceGrouping.group(m_price));

    // parts
    for(Content content : m_contents)
      values.put(Index.Path.PARTS, content.getPart().toString());

    // worlds
    for(String world : m_worlds)
      values.put(Index.Path.WORLDS, world);

    // titles for references
    values.put(Index.Path.TITLES, getFullTitle() + " (" + getName() + ")");

    return values;
  }

  @Override
  public void set(Values inValues)
  {
    super.set(inValues);

    m_leader = inValues.use("leader", m_leader);
    m_title = inValues.use("title", m_title);
    m_subtitle = inValues.use("subtitle", m_subtitle);
    m_notes = inValues.use("notes", m_notes);
    m_authors = inValues.use("authors", m_authors, Person.PARSER, "name", "job");
    m_editors = inValues.use("editors", m_editors, Person.PARSER, "name", "job");
    m_cover = inValues.use("cover", m_cover, Person.PARSER, "name", "job");
    m_illustrators = inValues.use("illustrators", m_illustrators, Person.PARSER,
                                  "name", "job");
    m_cartographers = inValues.use("cartographers", m_cartographers,
                                   Person.PARSER, "name", "job");
    m_typographers = inValues.use("typographers", m_typographers, Person.PARSER,
                                  "name", "job");
    m_managers = inValues.use("managers", m_managers, Person.PARSER,
                              "name", "job");
    m_date = inValues.use("date", m_date, Date.PARSER);
    m_isbn = inValues.use("isbn.10", m_isbn, ISBN.PARSER);
    m_isbn13 = inValues.use("isbn.13", m_isbn13, ISBN13.PARSER);
    m_pages = inValues.use("pages", m_pages);
    m_producer = inValues.use("producer", m_producer);
    m_system= inValues.use("system", m_system, new NewValue.Parser<System>(1) {
      @Override
      public Optional<System> doParse(String inValue)
      {
        return System.fromString(inValue);
      }
    });
    m_audience = inValues.use("audience", m_audience,
                              new NewValue.Parser<Audience>(1) {
      @Override
      public Optional<Audience> doParse(String inValue)
      {
        return Audience.fromString(inValue);
      }
    });
    m_productType = inValues.use("product type", m_productType,
                                 new NewValue.Parser<ProductType>(1) {
      @Override
      public Optional<ProductType> doParse(String inValue)
      {
        return ProductType.fromString(inValue);
      }
    });
    m_style = inValues.use("style", m_style, new NewValue.Parser<Style>(1) {
      @Override
      public Optional<Style> doParse(String inValue)
      {
        return Style.fromString(inValue);
      }
    });
    m_layout = inValues.use("layout", m_layout, new NewValue.Parser<Layout>(1) {
      @Override
      public Optional<Layout> doParse(String inValue)
      {
        return Layout.fromString(inValue);
      }
    });
    m_series = inValues.use("series", m_series);
    m_number = inValues.use("number", m_number);
    m_volume = inValues.use("volume", m_volume);
    m_price = inValues.use("price", m_price, Price.PARSER);
    m_contents = inValues.use("contents", m_contents, Content.PARSER,
                              "part", "description", "amount");
    m_mandatoryRequirements = inValues.use("mandatoryRequirements",
                                           m_mandatoryRequirements,
                                           ProductReference.PARSER,
                                           "name", "pages");
    m_optionalRequirements = inValues.use("optionalRequirements",
                                          m_optionalRequirements,
                                          ProductReference.PARSER,
                                          "name", "pages");
  }

  @Override
  public Map<String, Object> collectSearchables()
  {
    Map<String, Object> searchables = super.collectSearchables();

    searchables.put("title", m_title);

    return searchables;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Message toProto()
  {
    BaseProductProto.Builder builder = BaseProductProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(!m_title.isEmpty())
      builder.setTitle(m_title);
    if(!m_leader.isEmpty())
      builder.setLeader(m_leader);
    if(!m_subtitle.isEmpty())
      builder.setSubtitle(m_subtitle);
    if(!m_notes.isEmpty())
      builder.setNotes(m_notes);
    for(Person person : m_authors)
      builder.addAuthor(person.toProto());
    for(Person person : m_editors)
      builder.addEditor(person.toProto());
    for(Person person : m_cartographers)
      builder.addCartographer(person.toProto());
    for(Person person : m_cover)
      builder.addCover(person.toProto());
    for(Person person : m_illustrators)
      builder.addIllustrator(person.toProto());
    for(Person person : m_typographers)
      builder.addTypographer(person.toProto());
    for(Person person : m_managers)
      builder.addManager(person.toProto());
    if(m_date != null)
      builder.setDate(m_date.toProto());
    if(m_isbn != null)
      builder.setIsbn(m_isbn.toProto());
    if(m_isbn13 != null)
      builder.setIsbn13(m_isbn13.toProto());
    if(m_pages != null)
      builder.setPages(m_pages);
    builder.setSystem(m_system.toProto());
    builder.setAudience(m_audience.toProto());
    builder.setType(m_productType.toProto());
    builder.setStyle(m_style.toProto());
    if(!m_producer.isEmpty())
      builder.setProducer(m_producer);
    if(!m_volume.isEmpty())
      builder.setVolume(m_volume);
    if(!m_number.isEmpty())
      builder.setNumber(m_number);
    for(String series : m_series)
        builder.addSeries(series);
    if(m_price != null)
      builder.setPrice(m_price.toProto());
    for(Content content : m_contents)
        builder.addContent(content.toProto());
    for(ProductReference requirement : m_mandatoryRequirements)
      builder.addRequiredRequirements(requirement.getName());
    for(ProductReference requirement : m_optionalRequirements)
      builder.addOptionalRequirements(requirement.getName());
    builder.setLayout(m_layout.toProto());

    BaseProductProto proto = builder.build();
    return proto;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseProductProto))
    {
      Log.warning("cannot parse product proto " + inProto.getClass());
      return;
    }

    BaseProductProto proto = (BaseProductProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.hasTitle())
      m_title = proto.getTitle();
    if(proto.hasLeader())
      m_leader = proto.getLeader();
    if(proto.hasSubtitle())
      m_subtitle = proto.getSubtitle();
    if(proto.hasNotes())
      m_notes = proto.getNotes();

    for(BaseProductProto.Person author : proto.getAuthorList())
      m_authors.add(Person.fromProto(author));

    for(BaseProductProto.Person editor : proto.getEditorList())
      m_editors.add(Person.fromProto(editor));

    for(BaseProductProto.Person cover : proto.getCoverList())
      m_cover.add(Person.fromProto(cover));

    for(BaseProductProto.Person cartographer : proto.getCartographerList())
      m_cartographers.add(Person.fromProto(cartographer));

    for(BaseProductProto.Person illustrator : proto.getIllustratorList())
      m_illustrators.add(Person.fromProto(illustrator));

    for(BaseProductProto.Person typographer : proto.getTypographerList())
      m_typographers.add(Person.fromProto(typographer));

    for(BaseProductProto.Person manager : proto.getManagerList())
      m_managers.add(Person.fromProto(manager));

    if(proto.hasDate())
      m_date = Date.fromProto(proto.getDate());

    if(proto.hasIsbn())
      m_isbn = ISBN.fromProto(proto.getIsbn());

    if(proto.hasIsbn13())
      m_isbn13 = ISBN13.fromProto(proto.getIsbn13());

    if(proto.hasPages())
      m_pages = proto.getPages();

    if(proto.hasSystem())
      m_system = System.fromProto(proto.getSystem());

    if(proto.hasAudience())
      m_audience = Audience.fromProto(proto.getAudience());

    if(proto.hasType())
      m_productType = ProductType.fromProto(proto.getType());

    if(proto.hasStyle())
      m_style = Style.fromProto(proto.getStyle());

    if(proto.hasProducer())
      m_producer = proto.getProducer();

    if(proto.hasVolume())
      m_volume = proto.getVolume();

    if(proto.hasNumber())
      m_number = proto.getNumber();

    if(proto.getSeriesCount() > 0)
      m_series = proto.getSeriesList();

    if(proto.hasPrice())
      m_price = Price.fromProto(proto.getPrice());

    for(BaseProductProto.Content contentProto : proto.getContentList())
      m_contents.add(Content.fromProto(contentProto));

    for(String requirement : proto.getRequiredRequirementsList())
      m_mandatoryRequirements.add(new ProductReference(requirement));

    for(String requirement : proto.getOptionalRequirementsList())
      m_optionalRequirements.add(new ProductReference(requirement));

    if(proto.hasLayout())
      m_layout = Layout.fromProto(proto.getLayout());
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(BaseProductProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

  //........................................................................

  //------------------------------------------------------------------- test

  /** This is the test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  //ValueGroup.Test
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
    //----- persons --------------------------------------------------------

    /** Testing get. */
    @org.junit.Test
    public void persons()
    {
      try (ParseReader reader =
        new ParseReader(new java.io.StringReader(s_text), "test"))
      {
        BaseProduct entry = null; //(BaseProduct)BaseProduct.read(reader);

        Set<String> persons = new java.util.TreeSet<String>();
        entry.collectPersons(persons);
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
      }
    }

    //......................................................................
    //----- jobs -----------------------------------------------------------

    /** Testing get. */
    @org.junit.Test
    public void jobs()
    {
      try (ParseReader reader =
        new ParseReader(new java.io.StringReader(s_text), "test"))
      {
        BaseProduct entry = null; //(BaseProduct)BaseProduct.read(reader);

        Set<String> jobs = new java.util.TreeSet<String>();
        entry.collectJobs(jobs);
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
      }
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
