/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.extensions.BaseArmor;
import net.ixitxachitls.dma.entries.extensions.BaseCommodity;
import net.ixitxachitls.dma.entries.extensions.BaseContainer;
import net.ixitxachitls.dma.entries.extensions.BaseWeapon;
import net.ixitxachitls.dma.entries.extensions.BaseWearable;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseArmorProto;
import net.ixitxachitls.dma.proto.Entries.BaseCommodityProto;
import net.ixitxachitls.dma.proto.Entries.BaseContainerProto;
import net.ixitxachitls.dma.proto.Entries.BaseCountedProto;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseItemProto;
import net.ixitxachitls.dma.proto.Entries.BaseLightProto;
import net.ixitxachitls.dma.proto.Entries.BaseMagicProto;
import net.ixitxachitls.dma.proto.Entries.BaseMagicProto.AbilityModifier;
import net.ixitxachitls.dma.proto.Entries.BaseMagicProto.Modifier;
import net.ixitxachitls.dma.proto.Entries.BaseTimedProto;
import net.ixitxachitls.dma.proto.Entries.BaseWeaponProto;
import net.ixitxachitls.dma.proto.Entries.BaseWearableProto;
import net.ixitxachitls.dma.proto.Values.RandomDurationProto;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.NewDistance;
import net.ixitxachitls.dma.values.NewDuration;
import net.ixitxachitls.dma.values.NewModifier;
import net.ixitxachitls.dma.values.NewMoney;
import net.ixitxachitls.dma.values.NewValue;
import net.ixitxachitls.dma.values.NewWeight;
import net.ixitxachitls.dma.values.Rational;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the basic jDMA base item.
 *
 * @file          BaseItem.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public class BaseItem extends BaseEntry
{
  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The possible sizes in the game. */
  public enum Size implements EnumSelection.Named, EnumSelection.Short
  {
    /** This is an unknown size. */
    UNKNOWN("Unknown", "U", 0, 0, 0, 0, 0, 0, BaseItemProto.Size.UNKNOWN_SIZE),

    /** The smallest size. */
    FINE("Fine", "F", 0, 0, 1, 0, 8, -16, BaseItemProto.Size.FINE),

    /** A very small size. */
    DIMINUTIVE("Diminutive", "D", 0, 0, 2, 0, 4, -12,
               BaseItemProto.Size.DIMINUTIVE),

    /** Smaller than small. */
    TINY("Tiny", "T", 0, 0, 5, 0, 2, -8, BaseItemProto.Size.TINY),

    /** Just small. */
    SMALL("Small", "S", 0, 5, 10, 10, 1, -4, BaseItemProto.Size.SMALL),

    /** This is the medium size. */
    MEDIUM("Medium-size", "M", 5, 5, 10, 20, 0, 0, BaseItemProto.Size.MEDIUM),

    /** Simply large. */
    LARGE("Large", "L", 5, 10, 20, 30, -1, 4, BaseItemProto.Size.LARGE),

    /** Larger than large. */
    HUGE("Huge", "H", 10, 15, 30, 40, -2, 8, BaseItemProto.Size.HUGE),

    /** Really large. */
    GARGANTUAN("Gargantuan", "G", 15, 20, 40, 60, -4, 12,
               BaseItemProto.Size.GARGANTUAN),

    /** This is the biggest size. */
    COLOSSAL("Colossal", "C", 20, 30, 60, 80, -8, 16,
             BaseItemProto.Size.COLOSSAL);

    /** The value's name. */
    private String m_name;

    /** The value's short name. */
    private String m_short;

    /** The reach for a long creature. */
    private int m_reachLong;

    /** The reach for a tall creature. */
    private int m_reachTall;

    /** The space required for this size. */
    private Rational m_space;

    /** The bonus hit points for a construct of this size. */
    private int m_construct;

    /** The size modifier for armor class and attacks. */
    private int m_modifier;

    /** The size modifier for grappling. */
    private int m_grapple;

    /** The proto enum value. */
    private BaseItemProto.Size m_proto;

    /**
     * Create the name.
     *
     * @param inName      the name of the value
     * @param inShort     the short name of the value
     * @param inReachLong the reach for a long creature
     * @param inReachTall the reach for a tall creature
     * @param inSpace     the space in 1/2 feet!
     * @param inConstruct the bonus hit points for a construct
     * @param inModifier  the armor class and attack bonus for size
     * @param inGrapple   the grapple modifier for size
     * @param inProto     the proto enum value
     */
    private Size(String inName, String inShort, int inReachLong,
                 int inReachTall, int inSpace, int inConstruct, int inModifier,
                 int inGrapple, BaseItemProto.Size inProto)
    {
      m_name      = constant("size",             inName);
      m_short     = constant("size.short",       inShort);
      m_reachLong = constant("size.reach.long",  inName, inReachLong);
      m_reachTall = constant("size.reach.tall",  inName, inReachTall);
      m_space     = new Rational(constant("size.space.2ft",  inName, inSpace),
                                 2);
      m_construct = constant("size.hp.construct", inName, inConstruct);
      m_modifier  = constant("size.modifier",     inName, inModifier);
      m_grapple   = constant("size.grapple",      inName, inGrapple);
      m_proto     = inProto;

      m_space.reduce();
    }

    /**
     * Get the name of the value.
     *
     * @return the name of the value
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /**
     * Get the name of the value.
     *
     * @return the name of the value
     */
    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Get the short name of the value.
     *
     * @return the short name of the value
     */
    @Override
    public String getShort()
    {
      return m_short;
    }

    /**
     * Compute another size based on the difference given. Subtraction can be
     * done by giving a negative value.
     *
     * @param inDifference the difference to compute with
     *
     * @return the newly calculated size
     */
    public Size add(int inDifference)
    {
      return values()[ordinal() + inDifference];
    }

    /**
     * Compute the difference between the two sizes (as categories).
     *
     * @param  inOther the other size to compare to
     *
     * @return the size difference
     */
    public int difference(Size inOther)
    {
      if(inOther == null)
        throw new IllegalArgumentException("must have another value here");

      return ordinal() - inOther.ordinal();
    }

    /**
     * Check if the given size is bigger than the current one.
     *
     * @param inOther the other size to compare to
     *
     * @return true if this is bigger than the other, false else
     */
    public boolean isBigger(Size inOther)
    {
      if(inOther == null)
        throw new IllegalArgumentException("must have another value here");

      return ordinal() > inOther.ordinal();
    }

    /**
     * Check if the given size is smaller than the current one.
     *
     * @param inOther the other size to compare to
     *
     * @return true if this is smaller than the other, false else
     */
    public boolean isSmaller(Size inOther)
    {
      if(inOther == null)
        throw new IllegalArgumentException("must have another value here");

      return ordinal() < inOther.ordinal();
    }

    /**
     * Get the reach for a creature of this size.
     *
     * @param inModifier the modifier if the creature is more tall or long
     *
     * @return the reach in feet
     */
    public int reach(SizeModifier inModifier)
    {
      if(inModifier == SizeModifier.TALL)
        return m_reachTall;

      return m_reachLong;
    }

    /**
     * Get the space that is required for this size in feet.
     *
     * @return the space in feet
     */
    public Rational space()
    {
      return m_space;
    }

    /**
     * Get the bonus hit points for size for constructs.
     *
     * @return the number of bonus hit points
     */
    public int construct()
    {
      return m_construct;
    }

    /**
     * Get the general modifier for armor class and attack for this size.
     *
     * @return the armor class or attack modifier for size
     */
    public int modifier()
    {
      return m_modifier;
    }

    /**
     * Get the modifier for grappling.
     *
     * @return the grappling modifier for size
     */
    public int grapple()
    {
      return m_grapple;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseItemProto.Size toProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static Size fromProto(BaseItemProto.Size inProto)
    {
      for(Size size : values())
        if(size.m_proto == inProto)
          return size;

      throw new IllegalStateException("invalid proto size: " + inProto);
    }

   /**
     * All the possible names for the layout.
     *
     * @return the possible names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();

      for(Size size : values())
        names.add(size.getName());

      return names;
    }

    /**
     * Get the layout matching the given text.
     */
    public static Optional<Size> fromString(String inText)
    {
      for(Size size : values())
        if(size.m_name.equalsIgnoreCase(inText))
          return Optional.of(size);

      return Optional.absent();
    }
  }

  /** The special size modifiers for monsters. */
  public enum SizeModifier implements EnumSelection.Named, EnumSelection.Short,
    EnumSelection.Proto<BaseItemProto.SizeModifier>
  {
    /** An unknown size modifier. */
    UNKNOWN("Unknown", "U", BaseItemProto.SizeModifier.UNKNOWN_SIZE_MODIFIER),

    /** A taller than longer monster. */
    TALL("tall", "T", BaseItemProto.SizeModifier.TALL),

    /** A longer than taller monster. */
    LONG("long", "L", BaseItemProto.SizeModifier.LONG);

    /** The value's name. */
    private String m_name;

    /** The value's short name. */
    private String m_short;

    /** The proto enum value. */
    private BaseItemProto.SizeModifier m_proto;

    /** Create the name.
     *
     * @param inName  the name of the value
     * @param inShort the short name of the value
     * @param inProto the proto enum value
     */
    private SizeModifier(String inName, String inShort,
                         BaseItemProto.SizeModifier inProto)
    {
      m_name  = constant("size.modifier",       inName);
      m_short = constant("size.modifier.short", inShort);
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

    @Override
    public String getShort()
    {
      return m_short;
    }

    @Override
    public BaseItemProto.SizeModifier toProto()
    {
      return m_proto;
    }

    /**
     * Convert the given proto enum to the corresponding enum value.
     *
     * @param inProto the proto enum value
     * @return the corresponding enum vbalue
     */
    public static SizeModifier fromProto(BaseItemProto.SizeModifier inProto)
    {
      for(SizeModifier modifier : values())
        if(modifier.m_proto == inProto)
          return modifier;

      throw new IllegalArgumentException("cannot convert size modifier: "
                                         + inProto);
    }

     /**
     * All the possible names for the layout.
     *
     * @return the possible names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();

      for(SizeModifier modifier : values())
        names.add(modifier.getName());

      return names;
    }

    /**
     * Get the layout matching the given text.
     */
    public static Optional<SizeModifier> fromString(String inText)
    {
      for(SizeModifier modifier : values())
        if(modifier.m_name.equalsIgnoreCase(inText))
          return Optional.of(modifier);

      return Optional.absent();
    }
  }

  /** The possible probabilities for items. */
  public enum Probability implements EnumSelection.Named
  {
    /** Only a single such item exists in the world. */
    UNKNOWN("Unknown", BaseItemProto.Probability.UNKNOWN),
    /** Only a single such item exists in the world. */
    UNIQUE("Unique", BaseItemProto.Probability.UNIQUE),
    /** A very rare thing, multiple might exist in the whole world. */
    VERY_RARE("Very Rare", BaseItemProto.Probability.VERY_RARE),
    /** A rare thing, most people rarely see it. */
    RARE("Rare", BaseItemProto.Probability.RARE),
    /** An uncommon thing, but still often seen. */
    UNCOMMON("Uncommon", BaseItemProto.Probability.UNCOMMON),
    /** A common, everyday thing. */
    COMMON("Common", BaseItemProto.Probability.COMMON);

    /** The value's name. */
    private String m_name;

    /** The prot enum value. */
    private BaseItemProto.Probability m_proto;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the proto enum value
     */
    private Probability(String inName, BaseItemProto.Probability inProto)
    {
      m_name = constant("item.probabilities", inName);
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
     * Get the probability for this selection.
     *
     * @return the probability number for this. It can be used to randomly roll
     *         up items with the appropriate probability distribution.
     */
    public int getProbability()
    {
      return (int)Math.pow(FACTOR, ordinal());
    }

    /**
     * The probabilistic factor, each category will be more probable according
     * to this factor.
     */
    public static final int FACTOR = 5;

    //                               /
    //                              /
    //                             /
    //     _______________________/
    //    /
    //   /
    //  /
    //     | VALUE_MOD_RANGE_LOW   | VALUE_MODE_RANGE_HIGH

    /** The percentage of the random range to use for adjustments (high). */
    public static final int RANGE_HIGH = 90;

    /** The percentage of the random range to use for adjustments (high). */
    public static final int RANGE_LOW = 10;

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseItemProto.Probability getProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static Probability fromProto(BaseItemProto.Probability inProto)
    {
      for(Probability probability : values())
        if(probability.m_proto == inProto)
          return probability;

      throw new IllegalStateException("invalid proto probability: " + inProto);
    }

   /**
     * All the possible names for the probability.
     *
     * @return the possible names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();

      for(Probability probability : values())
        names.add(probability.getName());

      return names;
    }

    /**
     * Get the layout matching the given text.
     */
    public static Optional<Probability> fromString(String inText)
    {
      for(Probability probability : values())
        if(probability.m_name.equalsIgnoreCase(inText))
          return Optional.of(probability);

      return Optional.absent();
    }
  }

  /** The possible sizes in the game. */
  public enum Substance implements EnumSelection.Named
  {
    /** Unknown substance,. */
    UNKNOWN("unknown", 0, 0, BaseItemProto.Substance.Material.UNKNOWN),

    /** Made of paper. */
    PAPER("paper", 0, 2, BaseItemProto.Substance.Material.PAPER),

    /** Made of cloth. */
    CLOTH("cloth", 0, 2, BaseItemProto.Substance.Material.CLOTH),

    /** Made of rope. */
    ROPE("rope", 0, 2, BaseItemProto.Substance.Material.ROPE),

    /** Made of glass. */
    GLASS("glass", 1, 1, BaseItemProto.Substance.Material.GLASS),

    /** Made of ice. */
    ICE("ice", 0, 3, BaseItemProto.Substance.Material.ICE),

    /** Made of leather. */
    LEATHER("leather", 2, 5, BaseItemProto.Substance.Material.LEATHER),

    /** Made of hide. */
    HIDE("hide", 2, 5, BaseItemProto.Substance.Material.HIDE),

    /** Made of wood. */
    WOOD("wood", 5, 10, BaseItemProto.Substance.Material.WOOD),

    /** Made of stone. */
    STONE("stone", 8, 15, BaseItemProto.Substance.Material.STONE),

    /** Made of iron. */
    IRON("iron", 10, 30, BaseItemProto.Substance.Material.IRON),

    /** Made of steel. */
    STEEL("steel", 10, 30, BaseItemProto.Substance.Material.STEEL),

    /** Made of crystal. */
    CRYSTAL("crystal", 10, 30, BaseItemProto.Substance.Material.CRYSTAL),

    /** Made of mithral. */
    MITHRAL("mithral", 15, 30, BaseItemProto.Substance.Material.MITHRAL),

    /** Made of adamantine. */
    ADAMANTINE("adamantine", 20, 40,
               BaseItemProto.Substance.Material.ADAMANTINE),

    /** Made of bone. */
    BONE("bone", 5, 10, BaseItemProto.Substance.Material.BONE);

    /** The value's name. */
    private String m_name;

    /** The hardness of the substance. */
    private int m_hardness;

    /** The hit points per inch. */
    private int m_hp;

    /** The proto enum value. */
    private BaseItemProto.Substance.Material  m_proto;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inHardness the hardness of the material
     * @param inHP       the hit points of the material
     * @param inProto    the proto enum value
     */
    private Substance(String inName, int inHardness, int inHP,
                      BaseItemProto.Substance.Material inProto)
    {
      m_name     = constant("substance.name",     inName);
      m_hardness = constant("substance.hardness", inName, inHardness);
      m_hp       = constant("substance.hp",       inName, inHP);
      m_proto = inProto;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /** Get the hardness for this substance type.
     *
     * @return the hardness
     *
     */
    public int hardness()
    {
      return m_hardness;
    }

    /** Get the hit points per inch of the substance.
     *
     * @return the (maxiaml) hit points
     *
     */
    public int hp()
    {
      return m_hp;
    }

    /** Convert to a human readable string.
     *
     * @return the converted string
     *
     */
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
    public BaseItemProto.Substance.Material toProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static Substance fromProto(BaseItemProto.Substance.Material inProto)
    {
      for(Substance substance: values())
        if(substance.m_proto == inProto)
          return substance;

      throw new IllegalStateException("invalid proto substance: " + inProto);
    }

     /**
     * All the possible names for the layout.
     *
     * @return the possible names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();

      for(Substance substance : values())
        names.add(substance.getName());

      return names;
    }

    /**
     * Get the substance matching the given text.
     */
    public static Optional<Substance> fromString(String inText)
    {
      for(Substance substance : values())
        if(substance.m_name.equalsIgnoreCase(inText))
          return Optional.of(substance);

      return Optional.absent();
    }
  }

  /** The possible areas to affect (cf. PHB 175). */
  public enum AreaShapes implements EnumSelection.Named,
    EnumSelection.Proto<BaseLightProto.Light.Shape>
  {
    /** An unknown shape. */
    UNKNOWN("Unknown", BaseLightProto.Light.Shape.UNKNOWN),
    /** A cone shaped area. */
    CONE("Cone", BaseLightProto.Light.Shape.CONE),
    /** A cylinder shaped area. */
    CYLINDER("Cylinder", BaseLightProto.Light.Shape.CYLINDER),
    /** An area in the form of a line. */
    LINE("Line", BaseLightProto.Light.Shape.LINE),
    /** A sphere shaped area. */
    SPHERE("Sphere", BaseLightProto.Light.Shape.SPHERE);

    /** The value's name. */
    private String m_name;

    /** The enum proto value. */
    private BaseLightProto.Light.Shape m_proto;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the proto enum value
     */
    private AreaShapes(String inName, BaseLightProto.Light.Shape inProto)
    {
      m_name = constant("area.shapes", inName);
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

    @Override
    public BaseLightProto.Light.Shape toProto()
    {
      return m_proto;
    }

    /**
     * Convert the proto value to the corresponding enum value.
     *
     *
     * @param inProto the proto value to convert
     * @return the corresponding enum value
     */
    public static AreaShapes fromProto(BaseLightProto.Light.Shape inProto)
    {
      for(AreaShapes area : values())
        if(area.m_proto == inProto)
          return area;

      throw new IllegalArgumentException("cannot convert area shape: "
        + inProto);
    }

   /**
     * All the possible names for the layout.
     *
     * @return the possible names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();

      for(AreaShapes shape : values())
        names.add(shape.getName());

      return names;
    }

    /**
     * Get the layout matching the given text.
     */
    public static Optional<AreaShapes> fromString(String inText)
    {
      for(AreaShapes shape : values())
        if(shape.m_name.equalsIgnoreCase(inText))
          return Optional.of(shape);

      return Optional.absent();
    }
  }

  public class Appearance implements Comparable<Appearance>
  {
    public Appearance(Probability inProbability, String inText)
    {
      m_probability = inProbability;
      m_text = inText;
    }

    private final Probability m_probability;
    private final String m_text;

    public Probability getProbability()
    {
      return m_probability;
    }

    public String getText()
    {
      return m_text;
    }

    @Override
    public String toString()
    {
      return m_text + " (" + m_probability + ")";
    }

    @Override
    public int compareTo(Appearance inOther)
    {
      int probability = m_probability.compareTo(inOther.m_probability);
      if(probability != 0)
        return probability;

      return m_text.compareTo(inOther.m_text);
    }
  }

  /** The possible counting unites in the game. */
  public enum CountUnit implements EnumSelection.Named,
    EnumSelection.Proto<BaseCountedProto.Unit>
  {
    /** Unknown count. */
    UNKNOWN("unknown", "unknowns", BaseCountedProto.Unit.UNKNOWN),
    /** Number of days. */
    DAY("day", "days", BaseCountedProto.Unit.DAY),
    /** Numer of pieces. */
    PIECE("piece", "pieces", BaseCountedProto.Unit.PIECE),
    /** Number of sheets. */
    SHEET("sheet", "sheets", BaseCountedProto.Unit.SHEET),
    /** Number of individual uses. */
    USE("use", "uses", BaseCountedProto.Unit.USE),
    /** Number of pages. */
    PAGE("page", "pages", BaseCountedProto.Unit.PAGE),
    /** Charges. */
    CHARGE("charge", "charges", BaseCountedProto.Unit.CHARGE),
    /** Can be applied. */
    APPLICATION("application", "applications",
                BaseCountedProto.Unit.APPLICATION),
    /** Can absorb or take some damage. */
    DAMAGE("damage", "damage", BaseCountedProto.Unit.DAMAGE);

    /** The value's name. */
    private String m_name;

    /** The value's name for multiple unites. */
    private String m_multiple;

    /** The proto enum value. */
    private BaseCountedProto.Unit m_proto;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inMultiple the text for multiple units
     * @param inProto    the proto enum value
     */
    private CountUnit(String inName, String inMultiple,
                      BaseCountedProto.Unit inProto)
    {
      m_name = constant("count.unit.name", inName);
      m_multiple = constant("count.unit.multiple", inMultiple);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    /**
     * Get the multiple name.
     *
     * @return the multiple name
     */
    public String getMultiple()
    {
      return m_multiple;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public BaseCountedProto.Unit toProto()
    {
      return m_proto;
    }

    /**
     * Get the unit corresponding to the given proto value.
     *
     * @param inProto the proto value to convert
     * @return the corresponding enum value
     */
    public static CountUnit fromProto(BaseCountedProto.Unit inProto)
    {
      for(CountUnit unit : values())
        if(unit.m_proto == inProto)
          return unit;

      throw new IllegalStateException("cannot convert unit: " + inProto);
    }

    /**
     * Get the unit from the given string.
     *
     * @param inValue the string representation
     * @return the matching unit, if any
     */
    public static Optional<CountUnit> fromString(String inValue)
    {
      for(CountUnit unit : values())
        if(unit.getName().equalsIgnoreCase(inValue))
          return Optional.of(unit);

      return Optional.absent();
    }

    /**
     * Get the possible names of units.
     *
     * @return a list of the names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();
      for(CountUnit unit : values())
        names.add(unit.getName());

      return names;
    }
  }

  /** The possible weapon types. */
  public enum WeaponType implements EnumSelection.Named, EnumSelection.Short,
    EnumSelection.Proto<BaseWeaponProto.Type>
  {
    /** An unknown type. */
    UNKNOWN("Unknown", "U", BaseWeaponProto.Type.UNKNOWN),

    /** A piercing OR slashing weapon. */
    PIERCING_OR_SLASHING("Piercing or Slashing", "P or S",
                         BaseWeaponProto.Type.PIERCING_OR_SLASHING),

    /** A bludeoning OR piercing weapon. */
    BLUDGEONING_OR_PIERCING("Bludgeoning or Piercing", "B or P",
                            BaseWeaponProto.Type.BLUDGEONING_OR_PIERCING),

    /** A bludeoning AND piercing weapon. */
    BLUDGEONING_AND_PIERCING("Bludgeoning and Piercing", "B and P",
                             BaseWeaponProto.Type.BLUDGEONING_AND_PIERCING),

    /** A slashing OR piercing weapon. */
    SLASHING_OR_PIERCING("Slashing or Piercing", "S or P",
                         BaseWeaponProto.Type.SLASHING_OR_PIERCING),

    /** A slashing weapon. */
    SLASHING("Slashing", "S", BaseWeaponProto.Type.SLASHING),

    /** A bludgeoning weapon. */
    BLUDGEONING("Bludgeoning", "B", BaseWeaponProto.Type.BLUDGEONING),

    /** A piercing weapon. */
    PIERCING("Piercing", "P", BaseWeaponProto.Type.PIERCING),

    /** A grenade. */
    GRENADE("Grenade", "G", BaseWeaponProto.Type.GRENADE),

    /** No type. */
    NONE("None", "N", BaseWeaponProto.Type.NONE);

    /** The value's name. */
    private String m_name;

    /** The value's short name. */
    private String m_short;

    /** The proto enum value. */
    private BaseWeaponProto.Type m_proto;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inShort    the short name of the value
     * @param inProto    the proto enum value
     */
    private WeaponType(String inName, String inShort, BaseWeaponProto.Type inProto)
    {
      m_name = constant("weapon.types", inName);
      m_short = constant("wepon.types.short", inShort);
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

    @Override
    public String getShort()
    {
      return m_short;
    }

    @Override
    public BaseWeaponProto.Type toProto()
    {
      return m_proto;
    }

    /**
     * Convert the proto enum to its enum value.
     *
     * @param inProto  the proto enum value
     * @return the converted enum value
     */
    public static WeaponType fromProto(BaseWeaponProto.Type inProto)
    {
      for(WeaponType type : values())
        if(type.m_proto == inProto)
          return type;

      throw new IllegalStateException("cannot convert weapon type proto: "
        + inProto);
    }

    /**
     * Get the type from the given string.
     *
     * @param inValue the string representation
     * @return the matching type, if any
     */
    public static Optional<WeaponType> fromString(String inValue)
    {
      for(WeaponType type : values())
        if(type.getName().equalsIgnoreCase(inValue))
          return Optional.of(type);

      return Optional.absent();
    }

    /**
     * Get the possible names of types.
     *
     * @return a list of the namees
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();
      for(WeaponType type : values())
        names.add(type.getName());

      return names;
    }
  }

    /** The possible weapon styles. */
  public enum WeaponStyle implements EnumSelection.Named, EnumSelection.Short,
    EnumSelection.Proto<BaseWeaponProto.Style>
  {
    /** An unknown style value. */
    UNKNOWN("Unknown", "U", false, 0, BaseWeaponProto.Style.UNKNOWN_STYLE),

    /** A two-handed melee weapon. */
    TWOHANDED_MELEE("Two-Handed Melee", "Two", true, 0,
                    BaseWeaponProto.Style.TWOHANDED_MELEE),

    /** A one-handed melee weapon. */
    ONEANDED_MELEE("One-Handed Melee", "One", true, -1,
                   BaseWeaponProto.Style.ONEHANDED_MELEE),

    /** A light melee weapon. */
    LIGHT_MELEE("Light Melee", "Light", true, -2,
                BaseWeaponProto.Style.LIGHT_MELEE),

    /** An unarmed 'weapon'. */
    UNARMED("Unarmed", "Unarmed", true, 0, BaseWeaponProto.Style.UNARMED),

    /** A ranged touch weapon. */
    RANGED_TOUCH("Ranged Touch", "Touch R", false, 0,
                 BaseWeaponProto.Style.RANGED_TOUCH),

    /** A ranged weapon. */
    RANGED("Ranged", "Ranged", false, 0, BaseWeaponProto.Style.RANGED),

    /** A thrown touch weapon. */
    THROWN_TOUCH("Thrown Touch", "Touch T", false, 0,
                 BaseWeaponProto.Style.THROWN_TOUCH),

    /** A thrown weapon. */
    THROWN("Thrown", "Thrown", false, 0, BaseWeaponProto.Style.THROWN),

    /** A touch weapon. */
    TOUCH("Touch", "Touch", true, 0, BaseWeaponProto.Style.TOUCH);

    /** The value's name. */
    private String m_name;

    /** The value's short name. */
    private String m_short;

    /** Flag if this is a range or melee weapon. */
    private boolean m_melee;

    /** The size difference between a normal item an a weapon. */
    private int m_sizeDifference;

    /** The corresponding proto value. */
    private BaseWeaponProto.Style m_proto;

    /**
     * Create the name.
     *
     * @param inName           the name of the value
     * @param inShort          the short name of the value
     * @param inMelee          true if this is a melee weapon, false for ranged
     * @param inSizeDifference the number of steps between this and medium
     * @param inProto          the corresponding proto value
     */
    private WeaponStyle(String inName, String inShort, boolean inMelee,
                  int inSizeDifference, BaseWeaponProto.Style inProto)
    {
      m_name = constant("weapon.types", inName);
      m_short = constant("weapon.types.short", inShort);
      m_melee = inMelee;
      m_sizeDifference = inSizeDifference;
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

    @Override
    public String getShort()
    {
      return m_short;
    }

    /**
     * Check if the weapon style is ranged for melee.
     *
     * @return true if the weapon is a melee weapon, false for ranged.
     */
    public boolean isMelee()
    {
      return m_melee;
    }

    /**
     * Get the size difference.
     *
     * @return the number of steps between this and medium.
     */
    public int getSizeDifference()
    {
      return m_sizeDifference;
    }

    @Override
    public BaseWeaponProto.Style toProto()
    {
      return m_proto;
    }

    /**
     * Convert the proto value to the corresponding enum value.
     *
     * @param   inProto the proto value
     * @return  the converted enum value
     */
    public static WeaponStyle fromProto(BaseWeaponProto.Style inProto)
    {
      for(WeaponStyle style : values())
        if(style.m_proto == inProto)
          return style;

      throw new IllegalStateException("unknown weapon style: " + inProto);
    }

    /**
     * Convert the given string into a style.
     *
     * @param inValue the string representation
     * @return the matching style, if any
     */
    public static Optional<WeaponStyle> fromString(String inValue)
    {
      for(WeaponStyle style : values())
        if(style.getName().equalsIgnoreCase(inValue))
          return Optional.of(style);

      return Optional.absent();
    }

    /**
     * Get the possible names of types.
     *
     * @return a list of the namees
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();
      for(WeaponStyle style : values())
        names.add(style.getName());

      return names;
    }
  }

  /** The possible weapon proficiencies. */
  public enum Proficiency implements EnumSelection.Named,
    EnumSelection.Proto<BaseWeaponProto.Proficiency>
  {
    /** An unknown proficiency. */
    UNKNOWN("Unknown", BaseWeaponProto.Proficiency.UNKNOWN_PROFICIENCY),
    /** Proficiency for simple weapons. */
    SIMPLE("Simple", BaseWeaponProto.Proficiency.SIMPLE),

    /** Proficiency for simple weapons. */
    MARTIAL("Martial", BaseWeaponProto.Proficiency.MARTIAL),

    /** Proficiency for simple weapons. */
    EXOTIC("Exotic", BaseWeaponProto.Proficiency.EXOCTIC),

    /** Proficiency for simple weapons. */
    IMPROVISED("Improvised", BaseWeaponProto.Proficiency.IMPROVISED),

    /** Proficiency for simple weapons. */
    NONE("None", BaseWeaponProto.Proficiency.NONE_PROFICIENCY);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseWeaponProto.Proficiency m_proto;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the corresponding proto enum value
     */
    private Proficiency(String inName, BaseWeaponProto.Proficiency inProto)
    {
      m_name = constant("weapon.proficiencies", inName);
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

    @Override
    public BaseWeaponProto.Proficiency toProto()
    {
      return m_proto;
    }

    /**
     * Convert the proto enum value to the corresponding enum value.
     *
     * @param inProto the proto value to convert
     * @return the corresponding enum value
     */
    public static Proficiency fromProto(BaseWeaponProto.Proficiency inProto)
    {
      for(Proficiency proficiency : values())
        if(proficiency.m_proto == inProto)
          return proficiency;

      throw new IllegalStateException("unknown weapon proficiency: " + inProto);
    }

    /**
     * Match the proficiency to the given string.
     *
     * @param inValue the string representation
     * @return the matching proficiency, if any
     */
    public static Optional<Proficiency> fromString(String inValue)
    {
      for(Proficiency proficiency : values())
        if(proficiency.getName().equalsIgnoreCase(inValue))
          return Optional.of(proficiency);

      return Optional.absent();
    }

    /**
     * Get the possible names of proficiencies.
     *
     * @return a list of the names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();
      for(Proficiency proficiency : values())
        names.add(proficiency.getName());

      return names;
    }
  }

  public static class NamedModifier extends NewValue.Addable
  {
    private final Optional<BaseMonster.Ability> m_ability;
    private final Optional<String> m_name;
    private final NewModifier m_modifier;

    private final static NewValue.Parser<NamedModifier> PARSER =
      new NewValue.Parser<NamedModifier>(3)
      {
        @Override
        protected Optional<NamedModifier> doParse(String inAbility,
                                                 String inName,
                                                 String inModifier)
        {
          Optional<BaseMonster.Ability> ability =
            BaseMonster.Ability.fromString(inAbility);
          String name = inName;
          Optional<NewModifier> modifier = NewModifier.PARSER.parse(inModifier);

          if(ability.isPresent() && modifier.isPresent())
            return Optional.of(new NamedModifier(ability.get(),
                                                 modifier.get()));
          if(name != null && !name.isEmpty() && modifier.isPresent())
            return Optional.of(new NamedModifier(name, modifier.get()));

          return Optional.absent();
        }
      };

    public NamedModifier(BaseMonster.Ability inAbility, NewModifier inModifier)
    {
      m_ability = Optional.of(inAbility);
      m_name = Optional.absent();
      m_modifier = inModifier;
    }

    public NamedModifier(String inName, NewModifier inModifier)
    {
      m_ability = Optional.absent();
      m_name = Optional.of(inName);
      m_modifier = inModifier;
    }

    @Override
    public String toString()
    {
      if(m_ability.isPresent())
        return m_ability.get() + " " + m_modifier;

      return m_name.get() + " " + m_modifier;
    }

    public Optional<BaseMonster.Ability> getAbility()
    {
      return m_ability;
    }

    public Optional<String> getName()
    {
      return m_name;
    }

    public NewModifier getModifier()
    {
      return m_modifier;
    }

    /**
     * Create a named modifier from the given ability modifier proto.
     *
     * @param inProto the proto to generate from
     * @return the created named modifier
     */
    public static NamedModifier fromProto(AbilityModifier inProto)
    {
      return new NamedModifier(BaseMonster.Ability.fromProto
                               (inProto.getAbility()),
                               NewModifier.fromProto(inProto.getModifier()));
    }

    /**
     * Create a named modifier from the given proto.
     *
     * @param inProto the proto to generate from
     * @return the newly created named modifier
     */
    public static NamedModifier fromProto(Modifier inProto)
    {
      return new NamedModifier(inProto.getName(),
                               NewModifier.fromProto(inProto.getModifier()));
    }

    @Override
    public Addable add(Addable inValue)
    {
      if(!canAdd(inValue))
        return this;

      NamedModifier value = (NamedModifier)inValue;
      if(m_ability.isPresent())
        return new NamedModifier(m_ability.get(),
                                 (NewModifier)
                                 m_modifier.add(value.getModifier()));

      return new NamedModifier(m_name.get(),
                               (NewModifier)m_modifier.add(value.getModifier()));
    }

    @Override
    public boolean canAdd(Addable inValue)
    {
      if(!(inValue instanceof NamedModifier))
        return false;

      NamedModifier value = (NamedModifier)inValue;
      if(!m_ability.equals(value.getAbility()))
        return false;

      if(!m_name.equals(value.getName()))
        return false;

      return true;
    }

    @Override
    public Message toProto()
    {
      // TODO: To implement this we need a single modifier type proto, combining
      // ability modifier and named modifier. This should probably be an enum
      // for the type of modifier and the modifier only.
      throw new UnsupportedOperationException("not implemented");
    }
  }

  /**
   * This is the internal, default constructor for an undefined value.
   */
  protected BaseItem()
  {
    super(TYPE);
  }

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base item
   */
  public BaseItem(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final BaseType<BaseItem> TYPE =
    new BaseType<BaseItem>(BaseItem.class);

  /** The name used by the player for the item. */
  protected String m_playerName = UNDEFINED_STRING;

  /** The total standard value of the base item. */
  protected Optional<NewMoney> m_value = Optional.absent();

  /** The standard weight of the item. */
  protected Optional<NewWeight> m_weight = Optional.absent();

  /** The standard hit points. */
  protected Optional<Integer> m_hp = Optional.absent();

  /**
   * The probability that for random determination, an item of this kind will
   * be selected. The probability is measured to the total of all probabilities
   * of all possible items.
   */
  protected Probability m_probability = Probability.UNKNOWN;

  /** The size of items of this kind. */
  protected Size m_size = Size.UNKNOWN;

  /** The size modifier. */
  protected SizeModifier m_sizeModifier = SizeModifier.UNKNOWN;

  /** The items standard hardness. */
  protected Optional<Integer> m_hardness = Optional.absent();

  /** The possible standard appearances of items of this kind. */
  protected List<Appearance> m_appearances = new ArrayList<>();

  /** The substance this item is mainly made of. */
  protected Substance m_substance = Substance.UNKNOWN;

  /** The thickness of the item of the substance above. */
  protected Optional<NewDistance> m_thickness = Optional.absent();

  /** The break DC for breaking this item (or bursting out of it). */
  protected Optional<Integer> m_break = Optional.absent();

  /** The number of copies or uses. */
  protected Optional<Integer> m_count = Optional.absent();

  /** The unit count for multiples or multiuses. */
  protected CountUnit m_countUnit = CountUnit.UNKNOWN;

  /** The shape of the light spread. */
  protected AreaShapes m_lightShape = AreaShapes.UNKNOWN;

  /** The radius this item sheds bright light. */
  protected Optional<NewDistance> m_brightLight = Optional.absent();

  /** The radius this item sheds shadowy light. */
  protected Optional<NewDistance> m_shadowyLight = Optional.absent();

  /** The time this item is functioning. */
  protected Optional<NewDuration> m_timed = Optional.absent();

  /** The weapon properties. */
  protected BaseWeapon m_weapon = new BaseWeapon(this);

  /** The armor properties. */
  protected BaseArmor m_armor = new BaseArmor(this);

  /** The commodity properties. */
  protected BaseCommodity m_commodity = new BaseCommodity(this);

  /** The container properties. */
  protected BaseContainer m_container= new BaseContainer(this);

  /** The wearable properties. */
  protected BaseWearable m_wearable = new BaseWearable(this);

  /** The magical modifier. */
  protected List<NamedModifier> m_magicalModifiers = new ArrayList<>();

  /**
   * Get the weapon data for the item.
   *
   * @return an weapon data
   */
  public BaseWeapon getWeapon()
  {
    return m_weapon;
  }

  /**
   * Check whether this item has weapon properties.
   *
   * @return true if the item has weapon properties
   */
  public boolean isWeapon()
  {
    if(m_weapon.hasValues())
      return true;

    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isWeapon())
        return true;

    return false;
  }

  /**
   * Check whether this item is counted.
   *
   * @return true if the item is counted.
   */
  public boolean isCounted()
  {
    if(m_count.isPresent())
      return true;

    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isCounted())
        return true;

    return false;
  }

  /**
   * Check whether this item sheds light.
   *
   * @return true if the item sheds light.
   */
  public boolean isLight()
  {
    if(m_brightLight.isPresent() || m_shadowyLight.isPresent())
      return true;

    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isLight())
        return true;

    return false;
  }

  /**
   * Check whether this item has limited duration.
   *
   * @return true if the item has limited duration.
   */
  public boolean isTimed()
  {
    if(m_timed.isPresent())
      return true;

    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isTimed())
        return true;

    return false;
  }

  /**
   * Check whether this item has magical properties.
   *
   * @return true if the item has magical properties.
   */
  public boolean isMagical()
  {
    if(!m_magicalModifiers.isEmpty())
      return true;

    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isMagical())
        return true;

    return false;
  }

  /**
   * Get the armor data for the item.
   *
   * @return an armor data
   */
  public BaseArmor getArmor()
  {
    return m_armor;
  }

  /**
   * Check whether this item has armor properties.
   *
   * @return true if the item has armor properties
   */
  public boolean isArmor()
  {
    if(m_armor.hasValues())
      return true;

    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isArmor())
        return true;

    return false;
  }

  /**
   * Get the commodity data for the item.
   *
   * @return the commodity data
   */
  public BaseCommodity getCommodity()
  {
    return m_commodity;
  }

  /**
   * Check whether this item has commodity properties.
   *
   * @return true if the item has commodity properties
   */
  public boolean isCommodity()
  {
    if(m_commodity.hasValues())
      return true;

    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isCommodity())
        return true;

    return false;
  }

  /**
   * Get the container data for the item.
   *
   * @return the container data
   */
  public BaseContainer getContainer()
  {
    return m_container;
  }

  /**
   * Check whether this item has container properties.
   *
   * @return true if the item has container properties
   */
  public boolean isContainer()
  {
    if(m_container.hasValues())
      return true;

    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isContainer())
        return true;

    return false;
  }

  /**
   * Get the wearable data for the item.
   *
   * @return an wearable data
   */
  public BaseWearable getWearable()
  {
    return m_wearable;
  }

  /**
   * Check whether this item has wearable properties.
   *
   * @return true if the item has wearable properties
   */
  public boolean isWearable()
  {
    return m_wearable.hasValues();
  }

  /**
   * Get the hit points of the base item.
   *
   * @return      the hit points
   */
  public Optional<Integer> getHP()
  {
    if(m_hp.isPresent())
      return m_hp;

    if(m_substance != Substance.UNKNOWN && m_thickness.isPresent())
      return Optional.of(Math.max(1, (int)(m_substance.hp()
                                           * m_thickness.get().asInches())));

    return Optional.absent();
  }

  /**
   * Get the combined value of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<Integer> getCombinedHP()
  {
    Optional<Integer>hp = getHP();
    if(hp.isPresent())
      return new Combination.Integer(this, hp.get());

    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedHP());

    return new Combination.Integer(this, combinations);
  }

  /**
   * Get the hardness of the item.
   *
   * @return  the hardness of the item
   */
  public int getHardness()
  {
    if(m_hardness.isPresent())
      return m_hardness.get();

    if(m_substance != Substance.UNKNOWN)
      return m_substance.hardness();

    return 0;
  }

  /**
   * Get the combined hardness of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<Integer> getCombinedHardness()
  {
    if(m_hardness.isPresent())
      return new Combination.Max<Integer>(this, m_hardness.get());

    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedHardness());

    return new Combination.Max<Integer>(this, combinations);
  }

  /**
   * Get the hardness of the item.
   *
   * @return  the hardness of the item
   */
  public Optional<Integer> getBreakDC()
  {
    return m_break;
  }

  /**
   * Get the combined hardness of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<Integer> getCombinedBreakDC()
  {
    if(m_break.isPresent())
      return new Combination.Max<Integer>(this, m_break.get());

    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedBreakDC());

    return new Combination.Max<Integer>(this, combinations);
  }

  /**
   * Get the weight of the item.
   *
   * @return      the weight
   */
  public Optional<NewWeight> getWeight()
  {
    return m_weight;
  }

  /**
   * Get the combined value of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewWeight> getCombinedWeight()
  {
    if(m_weight.isPresent())
      return new Combination.Addable<NewWeight>(this, m_weight.get());

    List<Combination<NewWeight>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedWeight());

    return new Combination.Addable<NewWeight>(this, combinations);
  }

  /**
   * Get the value of the item.
   *
   * @return      the value
   */
  public Optional<NewMoney> getValue()
  {
    return m_value;
  }

  /**
   * Get the combined value of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewMoney> getCombinedValue()
  {
    if(m_value.isPresent())
      return new Combination.Addable<NewMoney>(this, m_value.get());

    List<Combination<NewMoney>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedValue());

    return new Combination.Addable<NewMoney>(this, combinations);
  }

  /**
   * Get the size of the item.
   *
   * @return      the size, as enum value
   */
  public Size getSize()
  {
    return m_size;
  }

  /**
   * Get the combined size of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<Size> getCombinedSize()
  {
    if(m_size != Size.UNKNOWN)
      return new Combination.Max<Size>(this, m_size);

    List<Combination<Size>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedSize());

    return new Combination.Max<Size>(this, combinations);
  }

 /**
   * Get the size modifier of the item.
   *
   * @return      the size mopdifier, as enum value
   */
  public SizeModifier getSizeModifier()
  {
    return m_sizeModifier;
  }

  /**
   * Get the combined size modifier of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<SizeModifier> getCombinedSizeModifier()
  {
    if(m_size != Size.UNKNOWN)
      return new Combination.Max<SizeModifier>(this, m_sizeModifier);

    List<Combination<SizeModifier>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedSizeModifier());

    return new Combination.Max<SizeModifier>(this, combinations);
  }

  /**
   * Get the substance of the item.
   *
   * @return      the substance, as enum value
   */
  public Substance getSubstance()
  {
    return m_substance;
  }

  /**
   * Get the combined substance of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<Substance> getCombinedSubstance()
  {
    if(m_substance != Substance.UNKNOWN)
      return new Combination.First<Substance>(this, m_substance);

    List<Combination<Substance>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedSubstance());

    return new Combination.First<Substance>(this, combinations);
  }

  /**
   * Get the thickness of the item.
   *
   * @return      the thickness
   */
  public Optional<NewDistance> getThickness()
  {
    return m_thickness;
  }

  /**
   * Get the combined thickness of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDistance> getCombinedThickness()
  {
    if(m_thickness.isPresent())
      return new Combination.Max<NewDistance>(this, m_thickness.get());

    List<Combination<NewDistance>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedThickness());

    return new Combination.Max<NewDistance>(this, combinations);
  }

  /**
   * Get the probability of the base item.
   *
   * @return      the weighted value of the selection
   */
  public Probability getProbability()
  {
    return m_probability;
  }

  /**
   * Get the combined size of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<Probability> getCombinedProbability()
  {
    if(m_probability != Probability.UNKNOWN)
      return new Combination.Max<Probability>(this, m_probability);

    List<Combination<Probability>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedProbability());

    return new Combination.Max<Probability>(this, combinations);
  }

  /**
   * Get the name of the entry as given to the player.
   *
   * @return      the requested name
   */
  public String getPlayerName()
  {
    return m_playerName;
  }

  /**
   * Get the combined player name of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<String> getCombinedPlayerName()
  {
    if(!m_playerName.isEmpty())
      return new Combination.String(this, m_playerName);

    List<Combination<String>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedPlayerName());

    return new Combination.String(this, combinations);
  }

  public List<Appearance> getAppearances()
  {
    return Collections.unmodifiableList(m_appearances);
  }

  /**
   * Get the combined appearances of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<List<Appearance>> getCombinedAppearances()
  {
    if(!m_appearances.isEmpty())
      return new Combination.Set<Appearance>(this, m_appearances);

    List<Combination<List<Appearance>>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedAppearances());

    return new Combination.Set<Appearance>(combinations, this);
  }

  /**
   * Get the possible names for probabilities.
   *
   * @return the possible probability choices.
   */
  public List<String> getProbabilityNames()
  {
    return Probability.names();
  }

  /**
   * Get the possible names for probabilities.
   *
   * @return the possible probability choices.
   */
  public List<String> getAbilityNames()
  {
    return BaseMonster.Ability.names();
  }

  /**
   * Get the count unit of the item.
   *
   * @return      the count unit, as enum value
   */
  public CountUnit getCountUnit()
  {
    return m_countUnit;
  }

  /**
   * Get the combined count unit of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<CountUnit> getCombinedCountUnit()
  {
    if(m_countUnit != CountUnit.UNKNOWN)
      return new Combination.Max<CountUnit>(this, m_countUnit);

    List<Combination<CountUnit>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedCountUnit());

    return new Combination.Max<CountUnit>(this, combinations);
  }

  /**
   * Get the count of the item.
   *
   * @return      the count, as enum value
   */
  public Optional<Integer> getCount()
  {
    return m_count;
  }

  /**
   * Get the combined count of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination.Integer getCombinedCount()
  {
    if(m_count.isPresent())
      return new Combination.Integer(this, m_count.get());

    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedCount());

    return new Combination.Integer(this, combinations);
  }

  /**
   * Get the shape of the light shed by the item.
   *
   * @return      the shape, as enum value
   */
  public AreaShapes getLightShape()
  {
    return m_lightShape;
  }

  /**
   * Get the combined light shape of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<AreaShapes> getCombinedLightShape()
  {
    if(m_lightShape != AreaShapes.UNKNOWN)
      return new Combination.Max<AreaShapes>(this, m_lightShape);

    List<Combination<AreaShapes>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedLightShape());

    return new Combination.Max<AreaShapes>(this, combinations);
  }

  /**
   * Get the radius this item sheds bright light.
   *
   * @return      the bright light radius
   */
  public Optional<NewDistance> getBrightLight()
  {
    return m_brightLight;
  }

  /**
   * Get the combined bright light radius of the item, including values of
   * base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDistance> getCombinedBrightLight()
  {
    if(m_brightLight.isPresent())
      return new Combination.Max<NewDistance>(this, m_brightLight.get());

    List<Combination<NewDistance>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedBrightLight());

    return new Combination.Max<NewDistance>(this, combinations);
  }

  /**
   * Get the radius this item sheds shadowy light.
   *
   * @return      the shadowy light radius
   */
  public Optional<NewDistance> getShadowyLight()
  {
    return m_shadowyLight;
  }

  /**
   * Get the combined shadowylight radius of the item, including values of
   * base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDistance> getCombinedShadowyLight()
  {
    if(m_shadowyLight.isPresent())
      return new Combination.Max<NewDistance>(this, m_shadowyLight.get());

    List<Combination<NewDistance>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedShadowyLight());

    return new Combination.Max<NewDistance>(this, combinations);
  }

  /**
   * Get the magical modifiers.
   *
   * @return      the magical modifiers.
   */
  public List<NamedModifier> getMagicalModifiers()
  {
    return Collections.unmodifiableList(m_magicalModifiers);
  }

  /**
   * Get the combined magical modifiers.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<List<NamedModifier>> getCombinedMagicalModifiers()
  {
    List<Combination<List<NamedModifier>>> combinations = new ArrayList<>();
    if(!m_magicalModifiers.isEmpty())
      combinations.add
      (new Combination.List<NamedModifier>(this, m_magicalModifiers));

    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedMagicalModifiers());

    return new Combination.List<NamedModifier>(combinations, this);
  }

  /**
   * Get the duration this item operates.
   *
   * @return      the time
   */
  public Optional<NewDuration> getTimed()
  {
    return m_timed;
  }

  /**
   * Get the combined time of the item, including values of
   * base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDuration> getCombinedTimed()
  {
    if(m_timed.isPresent())
      return new Combination.Min<NewDuration>(this, m_timed.get());

    List<Combination<NewDuration>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedTimed());

    return new Combination.Min<NewDuration>(this, combinations);
  }

 /**
  * Get a random appearance from this base item.
  *
  * @param       inFactor the factor to apply to generated random values.
  *
  * @return      the text for the random appearance.
  */
  public @Nullable String getRandomAppearance(double inFactor)
  {
    if(!m_appearances.isEmpty())
    {
      int total = 0;
      for(Appearance appearance : m_appearances)
        total += (int)Math.pow(BaseItem.Probability.FACTOR,
                               appearance.getProbability().ordinal());

      int random = 0;
      if(total > 0)
        random = (int)(RANDOM.nextInt(total) * inFactor);

      for(Appearance appearance : m_appearances)
      {
        random -= (int)Math.pow(BaseItem.Probability.FACTOR,
                                appearance.getProbability().ordinal());

        if(random <= 0)
          // we found the value
          return appearance.getText();
      }
    }

    // We have to try to get the value from our bases.
    List<String> appearances = new ArrayList<String>();

    for(BaseEntry base : m_baseEntries)
      if(base != null)
      {
        String appearance = ((BaseItem)base).getRandomAppearance(inFactor);

        if(appearance != null)
          appearances.add(appearance);
      }

    return Strings.SPACE_JOINER.join(appearances);
  }

  @Override
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    if(inUser == null)
      return false;

    return inUser.hasAccess(BaseCharacter.Group.DM);
  }

  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    if(m_value.isPresent())
      values.put(Index.Path.VALUES, m_value.get().group());
    if(m_weight.isPresent())
      values.put(Index.Path.WEIGHTS, m_weight.get().group());
    values.put(Index.Path.PROBABILITIES, m_probability.toString());
    values.put(Index.Path.SIZES, m_size.toString());
    if(m_hardness.isPresent())
      values.put(Index.Path.HARDNESSES, m_hardness.get().toString());
    values.put(Index.Path.HPS, "" + m_hp);
    values.put(Index.Path.SUBSTANCES, m_substance.toString());
    if(m_thickness.isPresent())
      values.put(Index.Path.DISTANCES, m_thickness.get().group());
    if(m_break.isPresent())
      values.put(Index.Path.BREAKS, m_break.get().toString());
    if(m_countUnit != CountUnit.UNKNOWN)
      values.put(Index.Path.UNITS, m_countUnit.toString());
    if(m_count.isPresent())
      values.put(Index.Path.COUNTS, m_count.get().toString());
    if(m_brightLight.isPresent())
      values.put(Index.Path.LIGHTS, m_brightLight.toString());
    if(m_shadowyLight.isPresent())
      values.put(Index.Path.LIGHTS, m_shadowyLight.toString());
    if(m_timed.isPresent())
      values.put(Index.Path.DURATIONS, m_timed.toString());

    values.putAll(m_weapon.computeIndexValues());
    values.putAll(m_armor.computeIndexValues());
    values.putAll(m_commodity.computeIndexValues());
    values.putAll(m_container.computeIndexValues());
    values.putAll(m_wearable.computeIndexValues());

    return values;
  }

  @Override
  public Message toProto()
  {
    BaseItemProto.Builder builder = BaseItemProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_value.isPresent())
      builder.setValue(m_value.get().toProto());

    if(m_weight.isPresent())
      builder.setWeight(m_weight.get().toProto());

    if(m_probability != Probability.UNKNOWN)
      builder.setProbability(m_probability.getProto());

    if(m_size != Size.UNKNOWN)
      builder.setSize(m_size.toProto());

    if(m_sizeModifier != SizeModifier.UNKNOWN)
      builder.setSizeModifier(m_sizeModifier.toProto());

    if(m_hardness.isPresent())
      builder.setHardness(m_hardness.get());

    if(m_hp.isPresent())
      builder.setHitPoints(m_hp.get());

    for(Appearance appearance : m_appearances)
        builder.addAppearance(BaseItemProto.Appearance.newBuilder()
                              .setProbability(appearance.getProbability()
                                              .getProto())
                              .setAppearance(appearance.getText())
                              .build());

    if(m_substance != Substance.UNKNOWN)
    {
      BaseItemProto.Substance.Builder substance =
        BaseItemProto.Substance.newBuilder()
      .setMaterial(m_substance.toProto());
      if(m_thickness.isPresent())
        substance.setThickness(m_thickness.get().toProto());

      builder.setSubstance(substance.build());
    }

    if(m_break.isPresent())
      builder.setBreakDc(m_break.get());

    if(!m_playerName.isEmpty())
      builder.setPlayerName(m_playerName);

    builder.setWeapon((BaseWeaponProto)m_weapon.toProto());
    builder.setArmor((BaseArmorProto)m_armor.toProto());
    builder.setCommodity((BaseCommodityProto)m_commodity.toProto());
    builder.setContainer((BaseContainerProto)m_container.toProto());
    builder.setWearable((BaseWearableProto)m_wearable.toProto());

    if(m_countUnit != CountUnit.UNKNOWN
      || m_count.isPresent())
    {
      BaseCountedProto.Builder counted = BaseCountedProto.newBuilder();
      if(m_countUnit != CountUnit.UNKNOWN)
        counted.setUnit(m_countUnit.toProto());
      if(m_count.isPresent())
        counted.setCount(m_count.get());
      else
        counted.setCount(1);

      builder.setCounted(counted.build());
    }

    if(m_brightLight.isPresent() || m_shadowyLight.isPresent())
    {
      BaseLightProto.Builder light = BaseLightProto.newBuilder();

      if(m_brightLight.isPresent())
        light.setBright(BaseLightProto.Light.newBuilder()
                        .setDistance(m_brightLight.get().toProto())
                        .setShape(m_lightShape.toProto())
                        .build());
      if(m_shadowyLight.isPresent())
        light.setShadowy(BaseLightProto.Light.newBuilder()
                         .setDistance(m_shadowyLight.get().toProto())
                         .setShape(m_lightShape.toProto())
                         .build());

      builder.setLight(light.build());
    }

    if(m_timed.isPresent())
      builder.setTimed(BaseTimedProto.newBuilder()
                       .setDuration(RandomDurationProto.newBuilder()
                                    .setDuration(m_timed.get().toProto())
                                    .build())
                       .build());

    if(!m_magicalModifiers.isEmpty())
    {
      BaseMagicProto.Builder magic = BaseMagicProto.newBuilder();
      for(NamedModifier modifier : m_magicalModifiers)
        if(modifier.getAbility().isPresent())
          magic.setAbilityModifier
            (BaseMagicProto.AbilityModifier.newBuilder()
             .setAbility(modifier.getAbility().get().toProto())
             .setModifier(modifier.getModifier().toProto())
             .build());
        else
          magic.addModifier(BaseMagicProto.Modifier.newBuilder()
                            .setName(modifier.getName().get())
                            .setModifier(modifier.getModifier().toProto())
                            .build());

      builder.setMagic(magic.build());
    }

    BaseItemProto proto = builder.build();
    return proto;
  }

  @Override
  public void set(Values inValues)
  {
    super.set(inValues);

    m_playerName = inValues.use("player_name", m_playerName);
    m_value = inValues.use("value", m_value, NewMoney.PARSER);
    m_weight = inValues.use("weight", m_weight, NewWeight.PARSER);
    m_size = inValues.use("size", m_size, new NewValue.Parser<Size>(1)
    {
      @Override
      public Optional<Size> doParse(String inValue)
      {
        return Size.fromString(inValue);
      }
    });
    m_sizeModifier = inValues.use("size_modifier", m_sizeModifier,
                                  new NewValue.Parser<SizeModifier>(1)
    {
      @Override
      public Optional<SizeModifier> doParse(String inValue)
      {
        return SizeModifier.fromString(inValue);
      }
    });
    m_substance = inValues.use("substance", m_substance,
                               new NewValue.Parser<Substance>(1) {
      @Override
      public Optional<Substance> doParse(String inValue)
      {
        return Substance.fromString(inValue);
      }
    });
    m_thickness = inValues.use("thickness", m_thickness, NewDistance.PARSER);
    m_hardness = inValues.use("hardness", m_hardness, NewValue.INTEGER_PARSER);
    m_break = inValues.use("break", m_break, NewValue.INTEGER_PARSER);
    m_probability = inValues.use("probability", m_probability,
                                 new NewValue.Parser<Probability>(1)
    {
      @Override
      public Optional<Probability> doParse(String inValue)
      {
        return Probability.fromString(inValue);
      }
    });
    m_appearances = inValues.use("appearances", m_appearances,
      new NewValue.Parser<Appearance>(2)
      {
        @Override
        public Optional<Appearance> doParse
        (String inProbability, String inText)
        {
          Optional<Probability> probability =
            Probability.fromString(inProbability);
          String text = inText;
          if(!probability.isPresent()
            || text == null || text.isEmpty())
            return Optional.absent();

          return Optional.of(new Appearance(probability.get(), text));
        }
      },
      "probability", "text");
    m_count = inValues.use("count", m_count, NewValue.INTEGER_PARSER);
    m_countUnit = inValues.use("count_unit", m_countUnit,
                               new NewValue.Parser<CountUnit>(1) {
      @Override
      public Optional<CountUnit> doParse(String inValue)
      {
        return CountUnit.fromString(inValue);
      }
    });
    m_lightShape = inValues.use("light.shape", m_lightShape,
                                new NewValue.Parser<AreaShapes>(1) {
      @Override
      public Optional<AreaShapes> doParse(String inValue)
      {
        return AreaShapes.fromString(inValue);
      }
    });
    m_brightLight = inValues.use("light.bright", m_brightLight,
                                 NewDistance.PARSER);
    m_shadowyLight = inValues.use("light.shadowy", m_shadowyLight,
                                  NewDistance.PARSER);
    m_timed = inValues.use("timed", m_timed, NewDuration.PARSER);
    m_magicalModifiers = inValues.use("magical", m_magicalModifiers,
                                      NamedModifier.PARSER,
                                      "ability", "name", "modifier");

    m_weapon.set(inValues);
    m_armor.set(inValues);
    m_commodity.set(inValues);
    m_container.set(inValues);
    m_wearable.set(inValues);
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseItemProto))
    {
      Log.warning("cannot parse proto " + inProto.getClass());
      return;
    }

    BaseItemProto proto = (BaseItemProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.hasValue())
      m_value = Optional.of(NewMoney.fromProto(proto.getValue()));

    if(proto.hasWeight())
      m_weight = Optional.of(NewWeight.fromProto(proto.getWeight()));

    if(proto.hasProbability())
      m_probability = Probability.fromProto(proto.getProbability());

    if(proto.hasSize())
      m_size = Size.fromProto(proto.getSize());

    if(proto.hasSizeModifier())
      m_sizeModifier = SizeModifier.fromProto(proto.getSizeModifier());

    if(proto.hasHardness())
      m_hardness = Optional.of(proto.getHardness());

    if(proto.hasHitPoints())
      m_hp = Optional.of(proto.getHitPoints());

    for(BaseItemProto.Appearance appearance : proto.getAppearanceList())
      m_appearances.add(new Appearance(Probability.fromProto
                                       (appearance.getProbability()),
                                       appearance.getAppearance()));

    if(proto.hasSubstance())
    {
      m_substance = Substance.fromProto(proto.getSubstance().getMaterial());
      if(proto.getSubstance().hasThickness())
        m_thickness = Optional.of(NewDistance.fromProto
                                      (proto.getSubstance().getThickness()));
    }

    if(proto.hasBreakDc())
      m_break = Optional.of(proto.getBreakDc());

    if(proto.hasPlayerName())
      m_playerName = proto.getPlayerName();

    if(proto.hasCounted())
    {
      if(proto.getCounted().hasUnit())
        m_countUnit = CountUnit.fromProto(proto.getCounted().getUnit());
      if(proto.getCounted().hasCount())
        m_count = Optional.of(proto.getCounted().getCount());
    }

    if(proto.hasWeapon())
      m_weapon.fromProto(proto.getWeapon());

    if(proto.hasWearable())
      m_wearable.fromProto(proto.getWearable());

    if(proto.hasMagic())
    {
      if(proto.getMagic().hasAbilityModifier())
        m_magicalModifiers.add(NamedModifier.fromProto
                               (proto.getMagic().getAbilityModifier()));

      for(BaseMagicProto.Modifier modifier : proto.getMagic().getModifierList())
        m_magicalModifiers.add(NamedModifier.fromProto(modifier));
    }

    if(proto.hasMultiple())
    {
      if(proto.getMultiple().hasUnit())
        m_countUnit = CountUnit.fromProto(proto.getMultiple().getUnit());
      if(proto.getMultiple().hasCount())
        m_count = Optional.of(proto.getMultiple().getCount());
    }

    if(proto.hasTimed())
      m_timed =
        Optional.of(NewDuration.fromProto
                    (proto.getTimed().getDuration().getDuration()));

    if(proto.hasArmor())
      m_armor.fromProto(proto.getArmor());

    if(proto.hasCommodity())
      m_commodity.fromProto(proto.getCommodity());

    if(proto.hasContainer())
      m_container.fromProto(proto.getContainer());

    if(proto.hasLight())
    {
      if(proto.getLight().hasBright())
      {
        m_brightLight = Optional.of
          (NewDistance.fromProto(proto.getLight().getBright().getDistance()));
        m_lightShape =
          AreaShapes.fromProto(proto.getLight().getBright().getShape());
      }
      if(proto.getLight().hasShadowy())
      {
        m_shadowyLight = Optional.of
          (NewDistance.fromProto(proto.getLight().getShadowy().getDistance()));
        m_lightShape =
          AreaShapes.fromProto(proto.getLight().getShadowy().getShape());
      }
    }

    if(proto.hasMultiuse())
    {
      m_countUnit = CountUnit.USE;
      if(proto.getMultiuse().hasCount())
        m_count = Optional.of(proto.getMultiuse().getCount());
    }
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(BaseItemProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

  //---------------------------------------------------------------------------

  /** The test. */
  public static class Test extends ValueGroup.Test
  {
    /**
     * Create a typical base item for testing purposes.
     *
     * @return the newly created base item
     */
    public static AbstractEntry createBaseItem()
    {
      try (java.io.StringReader sReader = new java.io.StringReader(s_text))
      {
        ParseReader reader = new ParseReader(sReader, "test");

        return null; //BaseItem.read(reader);
      }
    }

    /** Test text. */
    private static String s_text =
      "#------ winter blanket -------------------\n"
      + "\n"
      + "base item with light, timed winter blanket = \n"
      + "\n"
      + "  synonyms      \"blanket, winter\", \"guru\";\n"
      + "  player name   \"guru\";\n"
      + "  categories    warmth;\n"
      + "  value         5 sp;\n"
      + "  weight        [+3 lbs \"guru\"];\n"
      + "  size          small;\n"
      + "  probability   rare;\n"
      + "  hardness      0;\n"
      + "  hp            1;\n"
      + "  bright light  2 m sphere;\n"
      + "  shadowy light 4 m cone;\n"
      + "  duration      1 hour;\n"
      + "  world         generic;\n"
      + "  appearances   unique    \"first\","
      + "                common    \"second\","
      + "                common    \"third\","
      + "                rare      \"fourth\","
      + "                very rare \"fifth\","
      + "                unique    \"and sixth, the last one\";"
      + "  references    \"TSR 11550\" 107;\n"
      + "  description   \n"
      + "\n"
      + "  \"A thick, quilted, wool blanket.\".\n"
      + "\n"
      + "#.......................................................\n"
      + "\n";

    /** Test reading. */
    // @org.junit.Test
    // public void read()
    // {
    //   String result =
    //     "#------ winter blanket -------------------\n"
    //     + "\n"
    //     + "base item with light, timed winter blanket =\n"
    //     + "\n"
    //     + "  bright light      2 m Sphere;\n"
    //     + "  shadowy light     4 m Cone;\n"
    //     + "  duration          1 hour;\n"
    //     + "  value             5 sp;\n"
    //     + "  weight            [+3 lbs \"guru\"];\n"
    //     + "  probability       Rare;\n"
    //     + "  size              Small;\n"
    //     + "  hardness          0;\n"
    //     + "  hp                1;\n"
    //     + "  appearances       Unique \"first\",\n"
    //     + "                    Common \"second\",\n"
    //     + "                    Common \"third\",\n"
    //     + "                    Rare \"fourth\",\n"
    //     + "                    Very Rare \"fifth\",\n"
    //     + "                    Unique \"and sixth, the last one\";\n"
    //     + "  player name       \"guru\";\n"
    //     + "  world             Generic;\n"
    //     + "  references        \"TSR 11550\" 107;\n"
    //     + "  description       \n"
    //     + "  \"A thick, quilted, wool blanket.\";\n"
    //     + "  synonyms          \"blanket, winter\",\n"
    //     + "                    \"guru\";\n"
    //     + "  categories        warmth.\n"
    //     + "\n"
    //     + "#.......................................................\n";
    //   AbstractEntry entry = createBaseItem();

    //   assertNotNull("base item should have been read", entry);
    //   assertEquals("base item name does not match", "winter blanket",
    //                 entry.getName());
    //   assertEquals("base item does not match", result, entry.toString());
    // }

    /** Check size values. */
    @org.junit.Test
    public void size()
    {
      assertEquals("add", Size.MEDIUM, Size.TINY.add(2));
      assertEquals("add", Size.LARGE, Size.SMALL.add(2));
      assertEquals("add", Size.GARGANTUAN, Size.SMALL.add(4));
      assertEquals("add", Size.SMALL, Size.GARGANTUAN.add(-4));

      assertEquals("difference", 1, Size.TINY.difference(Size.DIMINUTIVE));
      assertEquals("difference", -1, Size.TINY.difference(Size.SMALL));
      assertEquals("difference", -6, Size.TINY.difference(Size.COLOSSAL));

      assertTrue("bigger", Size.SMALL.isBigger(Size.TINY));
      assertTrue("bigger", Size.LARGE.isBigger(Size.SMALL));
      assertTrue("bigger", Size.GARGANTUAN.isBigger(Size.HUGE));
      assertFalse("bigger", Size.HUGE.isBigger(Size.GARGANTUAN));
      assertFalse("bigger", Size.SMALL.isBigger(Size.MEDIUM));
      assertFalse("bigger", Size.TINY.isBigger(Size.GARGANTUAN));

      assertFalse("smaller", Size.SMALL.isSmaller(Size.TINY));
      assertFalse("smaller", Size.LARGE.isSmaller(Size.SMALL));
      assertFalse("smaller", Size.GARGANTUAN.isSmaller(Size.HUGE));
      assertTrue("smaller", Size.HUGE.isSmaller(Size.GARGANTUAN));
      assertTrue("smaller", Size.SMALL.isSmaller(Size.MEDIUM));
      assertTrue("smaller", Size.TINY.isSmaller(Size.GARGANTUAN));

      assertEquals("reach", 5, Size.SMALL.reach(SizeModifier.TALL));
      assertEquals("reach", 0, Size.SMALL.reach(SizeModifier.LONG));
      assertEquals("reach", 5, Size.MEDIUM.reach(SizeModifier.TALL));
      assertEquals("reach", 5, Size.MEDIUM.reach(SizeModifier.LONG));
      assertEquals("reach", 10, Size.LARGE.reach(SizeModifier.TALL));
      assertEquals("reach", 5, Size.LARGE.reach(SizeModifier.LONG));
      assertEquals("reach", 20, Size.GARGANTUAN.reach(SizeModifier.TALL));
      assertEquals("reach", 15, Size.GARGANTUAN.reach(SizeModifier.LONG));
    }

    /** Test probabilistic value. */
    @org.junit.Test
    public void probability()
    {
      assertEquals("probability", 1, Probability.UNIQUE.getProbability());
      assertEquals("probability", 5, Probability.VERY_RARE.getProbability());
      assertEquals("probability", 25, Probability.RARE.getProbability());
      assertEquals("probability", 125, Probability.UNCOMMON.getProbability());
      assertEquals("probability", 625, Probability.COMMON.getProbability());
    }

    /** Check format for overview. */
    // @org.junit.Test
    // public void format()
    // {
    //   BaseItem item = new BaseItem("format");

    //   item.setValue(0, 5, 0, 0);
    //   item.setWeight(33);

    //   List<Object> list = FORMATTER.format("key", item);

    //   assertEquals("weight", "5 gp", extract(list.get(4), 1, 1, 2));
    //   assertEquals("value", "33 lbs", extract(list.get(5), 1, 1, 1, 2));
    // }

    /** Testing of the base product specific indexes. */
    // @org.junit.Test
    // public void indexes()
    // {
    //   BaseCampaign.GLOBAL.m_bases.clear();

    //   BaseItem item1 = new BaseItem("item1");
    //   BaseItem item2 = new BaseItem("item2");

    //   item1.setSubstance(Substance.GLASS, 3);
    //   item2.setSubstance(Substance.ADAMANTINE, new Rational(4, 1, 2));

    //   BaseCampaign.GLOBAL.add(item1);
    //   BaseCampaign.GLOBAL.add(item2);

    //   m_logger.verify();

    //   for(net.ixitxachitls.dma.entries.indexes.Index<?> index : s_indexes)
    //   {
    //     if("Item::Physical".equals(index.getGroup())
    //        && "Substances".equals(index.getTitle()))
    //     {
    //       assertEquals("substances", 2,
    //                    index.buildNames
    //                    (BaseCampaign.GLOBAL.getAbstractEntries()).size());

    //       Iterator i =
    //         index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
    //         .iterator();
    //       assertEquals("substances", "glass", i.next().toString());
    //       assertEquals("substances", "adamantine", i.next().toString());

    //       assertFalse("substances", index.matchesName("guru", item1));
    //       assertTrue("substances", index.matchesName("Glass", item1));
    //       assertTrue("substances", index.matchesName("Adamantine", item2));
    //       assertFalse("substances", index.matchesName("Adamantine", item1));

    //       continue;
    //     }

    //     if("Item::Physical".equals(index.getGroup())
    //        && "Thicknesses".equals(index.getTitle()))
    //     {
    //       assertEquals("thicknesses", 2,
    //                    index.buildNames
    //                    (BaseCampaign.GLOBAL.getAbstractEntries()).size());

    //       Iterator i =
    //         index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
    //         .iterator();
    //       assertEquals("thicknesses", "3 in", i.next().toString());
    //       assertEquals("thicknesses", "5 in", i.next().toString());

    //       assertFalse("thicknesses", index.matchesName("guru", item1));
    //       assertTrue("thicknesses", index.matchesName("3 in", item1));
    //       assertTrue("thicknesses", index.matchesName("5 in", item2));
    //       assertFalse("thicknesses", index.matchesName("4 1/2 in", item1));

    //       continue;
    //     }
    //   }

    //   BaseCampaign.GLOBAL.m_bases.clear();
    // }

    /** Testing get. */
    // @org.junit.Test
    // public void get()
    // {
    //   ParseReader reader =
    //     new ParseReader(new java.io.StringReader(s_text), "test");

    //   BaseItem entry = (BaseItem)BaseProduct.read(reader);

    //   assertEquals("hp", 1, entry.getHP());

    //   assertEquals("weight", "3 lbs", entry.getWeight().toString());
    //   assertEquals("value", "5 sp", entry.getValue().toString());
    //   assertEquals("size", Size.SMALL, entry.getSize());
    //   assertEquals("probability", 25, entry.getProbability());
    //   assertEquals("player name", "guru", entry.getPlayerName());
    // }

    /** Test basing items on multiple base items. */
    // @org.junit.Test
    // public void based()
    // {
    //   BaseItem base1 = new BaseItem("base1");
    //   base1.setValue(0, 10, 0, 0);
    //   base1.addAttachment("armor");
    //   base1.set("AC bonus", "+42");

    //   BaseItem base2 = new BaseItem("base2");
    //   base2.addAttachment("armor");
    //   base2.set("check penalty", "-2");

    //   BaseItem base3 = new BaseItem("base3");
    //   base3.setValue(0, 10, 0, 0);

    //   BaseItem item = new BaseItem("test", base1, base2, base3);
    //   item.setValue(1, 2, 3, 4);
    //   item.m_value.setInitializer
    //     (new net.ixitxachitls.dma.values.aux.Initializer
    //      (net.ixitxachitls.dma.values.aux.Initializer.ADD));

    //   item.complete();
    //   item.set("check penalty", "0");

    //   assertEquals("value", "1 pp 22 gp 3 sp 4 cp", item.m_value.toString());
    //   assertEquals("attachment",
    //                "net.ixitxachitls.dma.entries.attachments.BaseArmor",
    //                item.getAttachments().next().getClass().getName());
    //   assertEquals("check penalty", "0",
    //                item.getValue("check penalty").toString());
    //   assertEquals("ac bonus", "+42",
    //                item.getValue("AC bonus").toString());
    // }
  }
}
