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

//------------------------------------------------------------------ imports

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.extensions.AbstractExtension;
import net.ixitxachitls.dma.entries.extensions.BaseArmor;
import net.ixitxachitls.dma.entries.extensions.BaseCommodity;
import net.ixitxachitls.dma.entries.extensions.BaseComposite;
import net.ixitxachitls.dma.entries.extensions.BaseContainer;
import net.ixitxachitls.dma.entries.extensions.BaseCounted;
import net.ixitxachitls.dma.entries.extensions.BaseIncomplete;
import net.ixitxachitls.dma.entries.extensions.BaseLight;
import net.ixitxachitls.dma.entries.extensions.BaseMagic;
import net.ixitxachitls.dma.entries.extensions.BaseMultiple;
import net.ixitxachitls.dma.entries.extensions.BaseMultiuse;
import net.ixitxachitls.dma.entries.extensions.BaseTimed;
import net.ixitxachitls.dma.entries.extensions.BaseWeapon;
import net.ixitxachitls.dma.entries.extensions.BaseWearable;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseArmorProto;
import net.ixitxachitls.dma.proto.Entries.BaseCommodityProto;
import net.ixitxachitls.dma.proto.Entries.BaseCompositeProto;
import net.ixitxachitls.dma.proto.Entries.BaseContainerProto;
import net.ixitxachitls.dma.proto.Entries.BaseCountedProto;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseIncompleteProto;
import net.ixitxachitls.dma.proto.Entries.BaseItemProto;
import net.ixitxachitls.dma.proto.Entries.BaseLightProto;
import net.ixitxachitls.dma.proto.Entries.BaseMagicProto;
import net.ixitxachitls.dma.proto.Entries.BaseMultipleProto;
import net.ixitxachitls.dma.proto.Entries.BaseMultiuseProto;
import net.ixitxachitls.dma.proto.Entries.BaseTimedProto;
import net.ixitxachitls.dma.proto.Entries.BaseWeaponProto;
import net.ixitxachitls.dma.proto.Entries.BaseWearableProto;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Group;
import net.ixitxachitls.dma.values.Money;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Rational;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.Weight;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the basic jDMA base item.
 *
 * @file          BaseItem.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

@ParametersAreNonnullByDefault
public class BaseItem extends BaseEntry
{
  //----------------------------------------------------------------- nested

  //----- size -------------------------------------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The possible sizes in the game. */
  public enum Size implements EnumSelection.Named, EnumSelection.Short
  {
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

    /** Create the name.
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

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
    {
      return m_name;
    }

    /** Get the short name of the value.
     *
     * @return the short name of the value
     *
     */
    @Override
    public String getShort()
    {
      return m_short;
    }

    /** Compute another size based on the difference given. Subtraction can be
     * done by giving a negative value.
     *
     * @param inDifference the difference to compute with
     *
     * @return the newly calculated size
     *
     */
    public Size add(int inDifference)
    {
      return values()[ordinal() + inDifference];
    }

    /** Compute the difference between the two sizes (as categories).
     *
     * @param  inOther the other size to compare to
     *
     * @return the size difference
     *
     */
    public int difference(Size inOther)
    {
      if(inOther == null)
        throw new IllegalArgumentException("must have another value here");

      return ordinal() - inOther.ordinal();
    }

    /** Check if the given size is bigger than the current one.
     *
     * @param inOther the other size to compare to
     *
     * @return true if this is bigger than the other, false else
     *
     */
    public boolean isBigger(Size inOther)
    {
      if(inOther == null)
        throw new IllegalArgumentException("must have another value here");

      return ordinal() > inOther.ordinal();
    }

    /** Check if the given size is smaller than the current one.
     *
     * @param inOther the other size to compare to
     *
     * @return true if this is smaller than the other, false else
     *
     */
    public boolean isSmaller(Size inOther)
    {
      if(inOther == null)
        throw new IllegalArgumentException("must have another value here");

      return ordinal() < inOther.ordinal();
    }

    /** Get the reach for a creature of this size.
     *
     * @param inModifier the modifier if the creature is more tall or long
     *
     * @return the reach in feet
     *
     */
    public int reach(SizeModifier inModifier)
    {
      if(inModifier == SizeModifier.TALL)
        return m_reachTall;

      return m_reachLong;
    }

    /** Get the space that is required for this size in feet.
     *
     * @return the space in feet
     *
     */
    public Rational space()
    {
      return m_space;
    }

    /** Get the bonus hit points for size for constructs.
     *
     * @return the number of bonus hit points
     *
     */
    public int construct()
    {
      return m_construct;
    }

    /** Get the general modifier for armor class and attack for this size.
     *
     * @return the armor class or attack modifier for size
     *
     */
    public int modifier()
    {
      return m_modifier;
    }

    /** Get the modifier for grappling.
     *
     * @return the grappling modifier for size
     *
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
    public BaseItemProto.Size getProto()
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
  }

  //........................................................................
  //----- sizes modifier ---------------------------------------------------

  /** The special size modifiers for monsters. */
  public enum SizeModifier implements EnumSelection.Named, EnumSelection.Short,
    EnumSelection.Proto<BaseItemProto.SizeModifier>
  {
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
  }

  //........................................................................
  //----- probabilities ----------------------------------------------------

  /** The possible probabilities for items. */
  public enum Probability implements EnumSelection.Named
  {
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

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the proto enum value
     */
    private Probability(String inName, BaseItemProto.Probability inProto)
    {
      m_name = constant("item.probabilities", inName);
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

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
    {
      return m_name;
    }

    /** Get the probability for this selection.
     *
     * @return the probability number for this. It can be used to randomly roll
     *         up items with the appropriate probability distribution.
     *
     */
    public int getProbability()
    {
      return (int)Math.pow(FACTOR, ordinal());
    }

    /** The probabilistic factor, each category will be more probable according
     * to this factor. */
    public static final int FACTOR =
      Config.get("/game/item.probabilitistFactor", 5);

    //                               /
    //                              /
    //                             /
    //     _______________________/
    //    /
    //   /
    //  /
    //     | VALUE_MOD_RANGE_LOW   | VALUE_MODE_RANGE_HIGH

    /** The percentage of the random range to use for adjustments (high). */
    public static final int RANGE_HIGH =
      Config.get("/game/value.modification.range.high", 90);

    /** The percentage of the random range to use for adjustments (high). */
    public static final int RANGE_LOW =
      Config.get("/game/value.modification.range.low", 10);

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
  }

  //........................................................................
  //----- substance --------------------------------------------------------

  /** The possible sizes in the game. */
  public enum Substance implements EnumSelection.Named
  {
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

    /** The multiplier for treasures. */
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
  }

  //........................................................................
  //----- area shapes ------------------------------------------------------

  /** The possible areas to affect (cf. PHB 175). */
  public enum AreaShapes implements EnumSelection.Named,
    EnumSelection.Proto<BaseLightProto.Light.Shape>
  {
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
  }

  //........................................................................

  //...........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------ BaseItem --------------------------------

  /**
   * This is the internal, default constructor for an undefined value.
   *
   */
  protected BaseItem()
  {
    super(TYPE);
  }

  //........................................................................
  //------------------------------ BaseItem --------------------------------

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base item
   *
   */
  public BaseItem(String inName)
  {
    super(inName, TYPE);
  }

  //........................................................................
  //------------------------------ BaseItem --------------------------------

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base item
   * @param       inBases the base items this one is based upon
   *
   */
  // public BaseItem(String inName, BaseItem ... inBases)
  // {
  //   super(inName, TYPE, inBases);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final BaseType<BaseItem> TYPE =
    new BaseType<BaseItem>(BaseItem.class);

  /** Flag if extensions are initialized. */
  private static boolean s_extensionsInitialized = false;

  //----- value ------------------------------------------------------------

  /** The total standard value of the base item. */
  @Key("value")
  @DM
  protected Money m_value = new Money();

  static
  {
    addIndex(new Index(Index.Path.VALUES, "Values", TYPE));
  }

  //........................................................................
  //----- weight -----------------------------------------------------------

  /** The standard weight of the item. */
  @Key("weight")
  protected Weight m_weight = new Weight();

  static
  {
    addIndex(new Index(Index.Path.WEIGHTS, "Weights", TYPE));
  }

  //........................................................................
  //----- probability ------------------------------------------------------

  /** The probability that for random determination, an item of this kind will
   * be selected. The probability is measured to the total of all probabilities
   * of all possible items. */
  @Key("probability")
  protected EnumSelection<Probability> m_probability =
    new EnumSelection<Probability>(Probability.COMMON)
    .withTemplate("link", Index.Path.PROBABILITIES.getPath());

  static
  {
    addIndex(new Index(Index.Path.PROBABILITIES, "Probabilities", TYPE));
  }

  //........................................................................
  //----- size -------------------------------------------------------------

  /** The size of items of this kind. */
  @Key("size")
  protected EnumSelection<Size> m_size =
    new EnumSelection<Size>(Size.class)
    .withTemplate("link", Index.Path.SIZES.getPath());

  static
  {
    addIndex(new Index(Index.Path.SIZES, "Sizes", TYPE));
  }

  //........................................................................
  //----- hardness ---------------------------------------------------------

  /** The grouping for the hardness. */
  protected static final Group<Number, Long, String> s_hardnessGroup =
    new Group<Number, Long, String>(new Group.Extractor<Number, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Number inValue)
        {
          if(inValue == null)
            throw new IllegalArgumentException("must have a number here");

          return inValue.get();
        }
      }, new Long [] { 0L, 1L, 2L, 3L, 4L, 5L, 10L, 15L, 20L, 25L, },
                                new String []
        { "0", "1", "2", "3", "4", "5", "10", "15", "20", "25", "Infinite", },
                                    "$undefined$");

  /** The items standard hardness. */
  @Key("hardness")
  protected Number m_hardness = new Number(0, 100)
    .withTemplate("link", Index.Path.HARDNESSES.getPath())
    .withGrouping(s_hardnessGroup);

  static
  {
    addIndex(new Index(Index.Path.HARDNESSES, "Hardnesses", TYPE));
  }

  //........................................................................
  //----- hit points -------------------------------------------------------

  /** The groups for hit points. */
  protected static final Group<Number, Long, String> s_hpGroup =
    new Group<Number, Long, String>(new Group.Extractor<Number, Long>()
    {
      /**
       *
       */
      private static final long serialVersionUID = 1L;

      @Override
      public Long extract(Number inValue)
      {
        if(inValue == null)
          throw new IllegalArgumentException("must have a number here");

        return inValue.get();
      }
    }, new Long [] { 0L, 5L, 10L, 15L, 20L, 30L, 40L, 50L, 100L, 250L, 500L,
                     1000L, },
                                    new String []
      { "0", "5", "10", "15", "20", "30", "40", "50", "100", "250", "500",
        "1000", "Infinite" }, "$undefined$");

  /** The standard hit points. */
  @Key("hp")
  @DM
  protected Number m_hp = new Number(0, 100000)
    .withTemplate("link", Index.Path.HPS.getPath())
    .withGrouping(s_hpGroup);

  static
  {
    addIndex(new Index(Index.Path.HPS, "Hitpoints", TYPE));
  }

  //........................................................................
  //----- appearances ------------------------------------------------------

  /** The possible standard appearances of items of this kind. */
  @Key("appearances")
  protected ValueList<Multiple> m_appearances =
    new ValueList<Multiple>(new Multiple(new Multiple.Element []
      { new Multiple.Element(new EnumSelection<Probability>
                             (Probability.class)
                             .withEditType("selection[probability]"), false),
        new Multiple.Element(new Text()
                             .withEditType("string[description]"), false) }))
    .withTemplate("appearances");

  //........................................................................
  //----- substance --------------------------------------------------------

  /** The group for thicknesses. */
  protected static final Group<Distance, Long, String> s_thicknessGrouping =
    new Group<Distance, Long, String>(new Group.Extractor<Distance, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Distance inValue)
        {
          return (long)(inValue.getAsFeet().getValue() * 240);
        }
      }, new Long [] { 2L, 5L, 10L, 20L, 40L, 60L, 80L, 100L, 200L, 500L,
                       1000L, 2000L, },
                               new String []
      { "1/10 in", "1/4 in", "1/2 in", "1 in", "2 in", "3 in", "4 in",
        "5 in", "10 in", "25 in", "50 ijn", "100 in", "Infinite", },
                                      "$undefined$");

  /** The substance this item is mainly made of. */
  @Key("substance")
  protected Multiple m_substance =
    new Multiple(new Multiple.Element []
      {
        new Multiple.Element
        (new EnumSelection<Substance>(Substance.class)
         .withTemplate("link", Index.Path.SUBSTANCES.getPath()), false),
        new Multiple.Element(new Distance()
                             .withGrouping(s_thicknessGrouping),
                             false, " ", null),
      });

  static
  {
    addIndex(new Index(Index.Path.SUBSTANCES, "Substances", TYPE));
    addIndex(new Index(Index.Path.DISTANCES, "Distances", TYPE));
  }


  //........................................................................
  //----- break DC ---------------------------------------------------------

  /** The group for break values. */
  protected static final Group<Number, Long, String> s_breakGrouping =
    new Group<Number, Long, String>(new Group.Extractor<Number, Long>()
    {
      /**
       *
       */
      private static final long serialVersionUID = 1L;

      @Override
      public Long extract(Number inValue)
      {
        if(inValue == null)
          throw new IllegalArgumentException("must have a number here");

        return inValue.get();
      }
    }, new Long [] { 0L, 5L, 10L, 15L, 20L, 25L, 30L, 25L, 40L, 45L, 50L,
                     100L, },
                                    new String []
      { "0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "100",
        "Infinite", }, "$undefined$");

  /** The break DC for breaking this item (or bursting out of it). */
  @Key("break DC")
  protected Number m_break = new Number(0, 1000)
    .withTemplate("link", Index.Path.BREAKS.getPath())
    .withGrouping(s_breakGrouping);

  static
  {
    addIndex(new Index(Index.Path.BREAKS, "Breadk DCs", TYPE));
  }

  //........................................................................
  //----- player name ------------------------------------------------------

  /** The name used by the player for the item. */
  @Key("player name")
  protected Text m_playerName = new Text();

  //........................................................................
  //----- qualities --------------------------------------------------------

  // /** The items' special qualities. */
  // @Key("qualities")
  // protected ValueList<Name> m_qualities =
  //   new ValueList<Name>(", ", new SimpleText());

  //........................................................................
  //----- effects ----------------------------------------------------------

  // /** The items' special effects. */
  // @Key("effects")
  // protected ValueList<Multiple> m_effects =
  //   new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
  //     { new Multiple.Element(new EnumSelection<BaseQuality.EffectType>
  //                            (BaseQuality.EffectType.class)
  //                            .withEditType("selection[type]"), false),
  //       new Multiple.Element(new SimpleText()
  //                            .withEditType("selection[affects]")
  //                            .withEditValues
  //                            (Strings.toString
  //                           (BaseQuality.Affects.values(), "||", "")), true),
  //       new Multiple.Element(new net.ixitxachitls.dma.values.Modifier()
  //                            .withEditType("name[modifier]"), true),
  //     }));

  //........................................................................

  static
  {
    // set the possible values
    extractVariables(BaseItem.class);
    extractVariables(BaseItem.class, BaseIncomplete.class);
  }

  //----- special indexes --------------------------------------------------

  static
  {
    addIndex(new Index(Index.Path.WORLDS, "Worlds", TYPE));
    addIndex(new Index(Index.Path.CATEGORIES, "Categories", TYPE));
    addIndex(new Index(Index.Path.REFERENCES, "References", TYPE));
    addIndex(new Index(Index.Path.EXTENSIONS, "Extensions", TYPE));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- getHP ---------------------------------

  /**
   * Get the hit points of the base item.
   *
   * @return      the hit points, or 0 if not defined
   *
   */
  @SuppressWarnings("unchecked") // casting multiple value
  public long getHP()
  {
    if(m_hp.isDefined())
      return m_hp.get();

    if(m_substance.isDefined())
      return Math.max(1,
                      (int)(((EnumSelection<Substance>)
                             m_substance.get(0)).getSelected().hp()
                            * (int)(((Distance)
                                     m_substance.get(1)).getAsFeet().getValue()
                                    * 12 * 10) / 10.0));

    return 0;
  }

  //........................................................................
  //----------------------------- getHardness ------------------------------

  /**
   * Get the hardness of the item.
   *
   * @return  the hardness of the item
   *
   */
  @SuppressWarnings("unchecked") // have to cast multiple value
  public int getHardness()
  {
    if(m_hardness.isDefined())
      return (int)m_hardness.get();

    if(m_substance.isDefined())
      return ((EnumSelection<Substance>)
              m_substance.get(0)).getSelected().hardness();

    return 0;
  }

  //........................................................................
  //------------------------------ getValue --------------------------------

  /**
   * Get the value for the given key.
   *
   * @param       inKey the name of the key to get the value for
   *
   * @return      the value for the key
   *
   */
  @Override
  @SuppressWarnings("unchecked")
  public @Nullable Value<?> getValue(String inKey)
  {
    if("hp".equals(inKey))
    {
      Value<?> hp = super.getValue(inKey);
      if(hp.isDefined() || !m_substance.isDefined())
        return hp;

      return m_hp.as((int)Math.round(((EnumSelection<Substance>)
                                      m_substance.get(0)).getSelected().hp()
                                     * ((Distance)m_substance.get(1))
                                     .getAsFeet().getValue()
                                     * 12));
    }

    return super.getValue(inKey);
  }

  //........................................................................

  //------------------------------- getWeight ------------------------------

  /**
   * Get the weight of the item.
   *
   * @return      the weight
   *
   */
  public Weight getWeight()
  {
    return m_weight;
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
    return m_value;
  }

  //........................................................................
  //------------------------------- getSize --------------------------------

  /**
   * Get the size of the item.
   *
   * @return      the size, as enum value
   *
   */
  public Size getSize()
  {
    return m_size.getSelected();
  }

  //........................................................................
  //---------------------------- getProbability ----------------------------

  /**
   * Get the probability of the base item.
   *
   * @return      the weighted value of the selection
   *
   */
  public int getProbability()
  {
    return m_probability.getSelected().getProbability();
  }

  //........................................................................
  //---------------------------- getPlayerName -----------------------------

  /**
   * Get the name of the entry as given to the plaer.
   *
   * @return      the requested name
   *
   */
  public String getPlayerName()
  {
    if(m_playerName.isDefined())
      return m_playerName.get();

    return m_name;
  }

  //........................................................................
  //------------------------- getRandomAppearance --------------------------

  /**
   * Get a random appearance from this base item.
   *
   * @param       inFactor the factor to apply to generated random values.
   *
   * @return      the text for the random appearance.
   *
   */
  public @Nullable String getRandomAppearance(double inFactor)
  {
    if(m_appearances.isDefined())
    {
      int total = 0;
      for(Value<?> value : m_appearances)
        total += (int)Math.pow(BaseItem.Probability.FACTOR,
                               ((EnumSelection)((Multiple)value).get(0))
                               .getSelected().ordinal());

      int random = 0;
      if(total > 0)
        random = (int)(RANDOM.nextInt(total) * inFactor);

      for(Iterator<Multiple> i = m_appearances.iterator(); i.hasNext(); )
      {
        Multiple multiple = i.next();
        EnumSelection<?> value = (EnumSelection<?>)multiple.get(0);

        random -= (int)Math.pow(BaseItem.Probability.FACTOR,
                                value.getSelected().ordinal());

        if(random <= 0 || !i.hasNext())
          // we found the value
          return ((Text)multiple.get(1)).get();
      }
    }
    else
      if(m_baseEntries != null)
      {
        // We have to try to get the value from our bases.
        List<String> appearances = new ArrayList<String>();

        for(BaseEntry base : m_baseEntries)
          if(base != null)
          {
            String appearance = ((BaseItem)base).getRandomAppearance(inFactor);

            if(appearance != null)
              appearances.add(appearance);
          }

        return Strings.toString(appearances, " ", "");
      }

    return null;
  }

  //........................................................................

  //--------------------------------- isDM ---------------------------------

  /**
   * Check whether the given user is the DM for this entry. Every DM is a DM
   * for a base product.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   *
   */
  @Override
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    if(inUser == null)
      return false;

    return inUser.hasAccess(BaseCharacter.Group.DM);
  }

  //........................................................................

 //-------------------------- computeIndexValues --------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @return      a multi map of values per index name
   *
   */
  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    values.put(Index.Path.VALUES, m_value.group());
    values.put(Index.Path.WEIGHTS, m_weight.group());
    values.put(Index.Path.PROBABILITIES, m_probability.group());
    values.put(Index.Path.SIZES, m_size.group());
    values.put(Index.Path.HARDNESSES, m_hardness.group());
    values.put(Index.Path.HPS, m_hp.group());
    values.put(Index.Path.SUBSTANCES, m_substance.get(0).group());
    values.put(Index.Path.DISTANCES,
               s_thicknessGrouping.group((Distance)m_substance.get(1)));
    values.put(Index.Path.BREAKS, m_break.group());

    return values;
  }

  //........................................................................

  @SuppressWarnings("unchecked")
  @Override
  public Message toProto()
  {
    BaseItemProto.Builder builder = BaseItemProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_value.isDefined())
      builder.setValue(m_value.toProto());

    if(m_weight.isDefined())
      builder.setWeight(m_weight.toProto());

    if(m_probability.isDefined())
      builder.setProbability(m_probability.getSelected().getProto());

    if(m_size.isDefined())
      builder.setSize(m_size.getSelected().getProto());

    if(m_hardness.isDefined())
      builder.setHardness((int)m_hardness.get());

    if(m_hp.isDefined())
      builder.setHitPoints((int)m_hp.get());

    if(m_appearances.isDefined())
      for(Multiple appearance : m_appearances)
        builder.addAppearance(BaseItemProto.Appearance.newBuilder()
                              .setProbability(((EnumSelection<Probability>)
                                              appearance.get(0))
                                              .getSelected().getProto())
                              .setAppearance(((Text)appearance.get(1)).get())
                              .build());

    if(m_substance.isDefined())
      builder.setSubstance(BaseItemProto.Substance.newBuilder()
                           .setMaterial(((EnumSelection<Substance>)
                                        m_substance.get(0)).getSelected()
                                        .toProto())
                           .setThickness(((Distance)m_substance.get(1))
                                         .toProto())
                           .build());

    if(m_break.isDefined())
      builder.setBreakDc((int)m_break.get());

    if(m_playerName.isDefined())
      builder.setPlayerName(m_playerName.get());

    // TODO: move extensions into simple members
    for(Map.Entry<String, AbstractExtension<? extends AbstractEntry>> entry
      : m_extensions.entrySet())
    {
      switch(entry.getKey())
      {
        case "armor":
          builder.setArmor((BaseArmorProto)entry.getValue().toProto());
          break;

        case "commodity":
          builder.setCommodity((BaseCommodityProto)entry.getValue().toProto());
          break;

        case "composite":
          builder.setComposite((BaseCompositeProto)entry.getValue().toProto());
          break;

        case "container":
          builder.setContainer((BaseContainerProto)entry.getValue().toProto());
          break;

        case "counted":
          builder.setCounted((BaseCountedProto)entry.getValue().toProto());
          break;

        case "incomplete":
          builder.setIncomplete((BaseIncompleteProto)
                                entry.getValue().toProto());
          break;

        case "light":
          builder.setLight((BaseLightProto)entry.getValue().toProto());
          break;

        case "magic":
          builder.setMagic((BaseMagicProto)entry.getValue().toProto());
          break;

        case "multiple":
          builder.setMultiple((BaseMultipleProto)entry.getValue().toProto());
          break;

        case "multiuse":
          builder.setMultiuse((BaseMultiuseProto)entry.getValue().toProto());
          break;

        case "timed":
          builder.setTimed((BaseTimedProto)entry.getValue().toProto());
          break;

        case "weapon":
          builder.setWeapon((BaseWeaponProto)entry.getValue().toProto());
          break;

        case "wearable":
          builder.setWearable((BaseWearableProto)entry.getValue().toProto());

        default:
          break;
      }
    }

    BaseItemProto proto = builder.build();
    return proto;
  }

  @Override
  @SuppressWarnings("unchecked")
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
      m_value = m_value.fromProto(proto.getValue());

    if(proto.hasWeight())
      m_weight = m_weight.fromProto(proto.getWeight());

    if(proto.hasProbability())
      m_probability =
        m_probability.as(Probability.fromProto(proto.getProbability()));

    if(proto.hasSize())
      m_size = m_size.as(Size.fromProto(proto.getSize()));

    if(proto.hasHardness())
      m_hardness = m_hardness.as(proto.getHardness());

    if(proto.hasHitPoints())
      m_hp = m_hp.as(proto.getHitPoints());

    if(proto.getAppearanceCount() > 0)
    {
      List<Multiple> appearances = new ArrayList<>();
      Multiple multiple = m_appearances.createElement();
      for(BaseItemProto.Appearance appearance : proto.getAppearanceList())
        appearances.add
          (multiple.as(((EnumSelection<Probability>)multiple.get(0))
                       .as(Probability.fromProto(appearance.getProbability())),
                       ((Text)multiple.get(1)).as(appearance.getAppearance())));

      m_appearances = m_appearances.as(appearances);
    }

    if(proto.hasSubstance())
      m_substance =
      m_substance.as(((EnumSelection<Substance>)m_substance.get(0))
                     .as(Substance.fromProto
                         (proto.getSubstance().getMaterial())),
                     ((Distance)m_substance.get(1))
                     .fromProto(proto.getSubstance().getThickness()));

    if(proto.hasBreakDc())
      m_break = m_break.as(proto.getBreakDc());

    if(proto.hasPlayerName())
      m_playerName = m_playerName.as(proto.getPlayerName());

    if(proto.hasWeapon())
    {
      BaseWeapon extension = new BaseWeapon(this, "weapon");
      extension.fromProto(proto.getWeapon());
      addExtension("weapon", extension);
    }

    if(proto.hasWearable())
    {
      BaseWearable extension = new BaseWearable(this, "wearable");
      extension.fromProto(proto.getWearable());
      addExtension("wearable", extension);
    }

    if(proto.hasIncomplete())
    {
      BaseIncomplete extension = new BaseIncomplete(this, "incomplete");
      extension.fromProto(proto.getIncomplete());
      addExtension("incomplete", extension);
    }

    if(proto.hasMagic())
    {
      BaseMagic extension = new BaseMagic(this, "magic");
      extension.fromProto(proto.getMagic());
      addExtension("magic", extension);
    }

    if(proto.hasCounted())
    {
      BaseCounted extension = new BaseCounted(this, "counted");
      extension.fromProto(proto.getCounted());
      addExtension("counted", extension);
    }

    if(proto.hasMultiple())
    {
      BaseMultiple extension = new BaseMultiple(this, "multiple");
      extension.fromProto(proto.getMultiple());
      addExtension("multiple", extension);
    }

    if(proto.hasTimed())
    {
      BaseTimed extension = new BaseTimed(this, "timed");
      extension.fromProto(proto.getTimed());
      addExtension("timed", extension);
    }

    if(proto.hasArmor())
    {
      BaseArmor extension = new BaseArmor(this, "armor");
      extension.fromProto(proto.getArmor());
      addExtension("armor", extension);
    }

    if(proto.hasCommodity())
    {
      BaseCommodity extension = new BaseCommodity(this, "commodity");
      extension.fromProto(proto.getCommodity());
      addExtension("comodity", extension);
    }

    if(proto.hasComposite())
    {
      BaseComposite extension = new BaseComposite(this, "composite");
      extension.fromProto(proto.getComposite());
      addExtension("composite", extension);
    }

    if(proto.hasContainer())
    {
      BaseContainer extension = new BaseContainer(this, "container");
      extension.fromProto(proto.getContainer());
      addExtension("container", extension);
    }

    if(proto.hasLight())
    {
      BaseLight extension = new BaseLight(this, "light");
      extension.fromProto(proto.getLight());
      addExtension("light", extension);
    }

    if(proto.hasMultiuse())
    {
      BaseMultiuse extension = new BaseMultiuse(this, "multiuse");
      extension.fromProto(proto.getMultiuse());
      addExtension("multiuse", extension);
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

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- setSize --------------------------------

  /**
   * Set the size of the item.
   *
   * @param       inSize the new size of the item
   *
   * @return      true if set, false if not
   *
   */
  // public boolean setSize(Size inSize)
  // {
  //   return m_size.set(inSize);
  // }

  //........................................................................
  //--------------------------- setProbability -----------------------------

  /**
   * Set the probability of the item.
   *
   * @param       inProbability the new probability of the item
   *
   * @return      true if set, false if not
   *
   * @undefined   never
   *
   */
  // public boolean setProbability(Probability inProbability)
  // {
  //   return m_probability.set(inProbability);
  // }

  //........................................................................
  //--------------------------------- setHP --------------------------------

  /**
   * Set the hp of the base item.
   *
   * @param       inHp the hp to set to
   *
   * @return      true if set, false it not (error)
   *
   * @undefined   never
   *
   */
  // public boolean setHP(long inHp)
  // {
  //   return m_hp.set(inHp);
  // }

  //........................................................................
  //--------------------------------- setHP --------------------------------

  /**
   * Set the name for the item that is shown to the player.
   *
   * @param       inName the new name to set to
   *
   * @return      true if set, false it not (error)
   *
   * @undefined   never
   *
   */
  // public boolean setPlayerName(String inName)
  // {
  //   return m_playerName.set(inName);
  // }

  //........................................................................
  //------------------------------ setBreakDC ------------------------------

  /**
   * Set the break DC of the base item.
   *
   * @param       inDC the dc to set to
   *
   * @return      true if set, false it not (error)
   *
   * @undefined   never
   *
   */
  // public boolean setBreakDC(int inDC)
  // {
  //   return m_break.set(inDC);
  // }

  //........................................................................
  //------------------------------- setValue -------------------------------

  /**
   * Set the value of the base item.
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

  //   return m_value.setStandard(platinum, gold, silver, copper);
  // }

  //........................................................................
  //------------------------------ setWeight -------------------------------

  /**
   * Set the weight of the base item.
   *
   * @param       inPounds the number of pounds to set to
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

  //   return m_weight.getBaseValue().setPounds(inPounds.clone(), null);
  // }

  //........................................................................
  //------------------------------ setWeight -------------------------------

  /**
   * Set the weight of the base item.
   *
   * @param       inPounds the number of pounds to set to
   *
   * @return      true for successful set, false else
   *
   * @undefined   assertion if null given
   *
   */
  // public boolean setWeight(int inPounds)
  // {
  //   return m_weight.getBaseValue().setPounds(new Rational(inPounds), null);
  // }

  //........................................................................
  //----------------------------- setHardness ------------------------------

  /**
   * Set the hardness of the base item.
   *
   * @param       inHardness the new hardness of the item
   *
   * @return      true for successful set, false else
   *
   * @undefined   assertion if null given
   *
   */
  // public boolean setHardness(int inHardness)
  // {
  //   return m_hardness.set(inHardness);
  // }

  //........................................................................
  //---------------------------- setSubstance ------------------------------

  /**
   * Set the substance of the base item.
   *
   * @param       inSubstance the new substance of the item
   * @param       inThickness the thickness of the substance in the items, in
   *                          inches
   *
   * @return      true for successful set, false else
   *
   * @undefined   never
   *
   */
  // @SuppressWarnings("unchecked") // casting to enum selection
  // public boolean setSubstance(Substance inSubstance, int inThickness)
  // {
  //   if(!((EnumSelection<Substance>)m_substance.get(0).getMutable())
  //      .set(inSubstance))
  //     return false;

  //   return ((Distance)m_substance.get(1).getMutable())
  //     .setFeet(null, null, new Rational(inThickness));
  // }

  //........................................................................
  //---------------------------- setSubstance ------------------------------

  /**
   * Set the substance of the base item.
   *
   * @param       inSubstance the new substance of the item
   * @param       inThickness the thickness of the substance in the items, in
   *                          inches
   *
   * @return      true for successful set, false else
   *
   * @undefined   never
   *
   */
  // @SuppressWarnings("unchecked") // casting to enum selection
  // public boolean setSubstance(Substance inSubstance, Rational inThickness)
  // {
  //   if(inThickness == null)
  //     return false;

  //   if(!((EnumSelection<Substance>)m_substance.get(0).getMutable())
  //      .set(inSubstance))
  //     return false;

  //   return ((Distance)m_substance.get(1).getMutable())
  //     .setFeet(null, null, inThickness.clone());
  // }

  //........................................................................
  //---------------------------- addAppearance -----------------------------

  /**
   * Add a appearance entry to the product.
   *
   * @param       inProbability the probability for this appearance
   * @param       inAppearance  the id of the product required by this one
   *
   * @return      true if added, false if not
   *
   * @undefined   never
   *
   */
  // @SuppressWarnings("unchecked") // casting to enum selection
  //public boolean addAppearance(Probability inProbability, String inAppearance)
  // {
  //   if(inAppearance == null)
  //     return false;

  //   Multiple appearance = m_appearances.newElement();

  //   ((EnumSelection<Probability>)(appearance.get(0).getMutable()))
  //     .set(inProbability);
  //   ((Text)appearance.get(1).getMutable()).set(inAppearance);

  //   return m_appearances.add(appearance);
  // }

  //........................................................................
  //----------------------------- addQuality -------------------------------

  /**
   * Add a quality entry to the product.
   *
   * @param       inQuality  the id of the product required by this one
   *
   * @return      true if added, false if not
   *
   * @undefined   never
   *
   */
  // public boolean addQuality(String inQuality)
  // {
  //   if(inQuality == null)
  //     return false;

  //   SimpleText quality = m_qualities.newElement();

  //   if(!quality.set(inQuality))
  //     return false;

  //   return m_qualities.add(quality);
  // }

  //........................................................................
  //------------------------------ addEffect -------------------------------

  /**
   * Add a effect entry to the product.
   *
   * @param       inType         the type of the effect to add
   * @param       inAffects      what is affected by the effect
   * @param       inModifier     the modifier granted by the effect
   * @param       inModifierType the type of modifier granted
   * @param       inModifierDesc the description of the modifier
   * @param       inModifierCond the condition of the modifier
   *
   * @return      true if added, false if not
   *
   * @undefined   never
   *
   */
  // @SuppressWarnings("unchecked")
  // public boolean addEffect(BaseQuality.EffectType inType,
  //                          String inAffects, int inModifier,
  //                          net.ixitxachitls.dma.values.
  //                          Modifier.Type inModifierType,
  //                          String inModifierDesc, Condition inModifierCond)
  // {
  //   Multiple effect = m_effects.newElement();

  //   if(!((EnumSelection<BaseQuality.EffectType>)effect.get(0).getMutable())
  //      .set(inType))
  //     return false;

  //   if(!((SimpleText)effect.get(1).getMutable()).set(inAffects))
  //     return false;

  //   net.ixitxachitls.dma.values.Modifier modifier =
  //     (net.ixitxachitls.dma.values.Modifier)effect.get(2).getMutable();

  //   if(!modifier.setValue(inModifier, inModifierType))
  //     return false;

  //   if(inModifierDesc != null)
  //     modifier.setDescription(inModifierDesc);

  //   if(inModifierCond != null)
  //     modifier.setCondition(inModifierCond);

  //   return m_effects.add(effect);
  // }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //--------------------------- ensureExtensions ---------------------------

  /**
   * Ensure that extensions are properly initialized.
   *
   */
  @Override
  protected void ensureExtensions()
  {
    // Since we have to prevent initialization loops, we load up extensions
    // here in a non-static context.
    if(!s_extensionsInitialized)
    {
      s_extensionsInitialized = true;

      AbstractExtension.setAutoExtensions(BaseWeapon.class, "weapon",
                                          "wearable", "base wearable");
      extractVariables(BaseItem.class, BaseWeapon.class);
      AbstractExtension.setAutoExtensions(BaseWearable.class, "wearable");
      extractVariables(BaseItem.class, BaseWearable.class);
      AbstractExtension.setAutoExtensions(BaseLight.class, "light");
      extractVariables(BaseItem.class, BaseLight.class);
      AbstractExtension.setAutoExtensions(BaseTimed.class, "timed");
      extractVariables(BaseItem.class, BaseTimed.class);
      AbstractExtension.setAutoExtensions(BaseArmor.class, "armor", "wearable");
      extractVariables(BaseItem.class, BaseArmor.class);
      AbstractExtension.setAutoExtensions(BaseCommodity.class, "commodity");
      extractVariables(BaseItem.class, BaseCommodity.class);
      AbstractExtension.setAutoExtensions(BaseComposite.class, "composite");
      extractVariables(BaseItem.class, BaseComposite.class);
      AbstractExtension.setAutoExtensions(BaseContainer.class, "contents");
      extractVariables(BaseItem.class, BaseContainer.class);
      AbstractExtension.setAutoExtensions(BaseCounted.class, "counted");
      extractVariables(BaseCounted.class);
      AbstractExtension.setAutoExtensions(BaseIncomplete.class, "incomplete");
      AbstractExtension.setAutoExtensions(BaseMultiple.class, "multiple");
      extractVariables(BaseItem.class, BaseMultiple.class);
      AbstractExtension.setAutoExtensions(BaseMultiuse.class, "multiuse");
      extractVariables(BaseItem.class, BaseMultiuse.class);
      extractVariables(BaseItem.class, BaseMagic.class);
    }

    super.ensureExtensions();
  }

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends ValueGroup.Test
  {
    //----- createBaseItem() -----------------------------------------------

    /** Create a typical base item for testing purposes.
     *
     * @return the newly created base item
     *
     */
    public static AbstractEntry createBaseItem()
    {
      try (java.io.StringReader sReader = new java.io.StringReader(s_text))
      {
        ParseReader reader = new ParseReader(sReader, "test");

        return null; //BaseItem.read(reader);
      }
    }

    //......................................................................

    //----- text -----------------------------------------------------------

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
    //......................................................................
    //----- read -----------------------------------------------------------

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

    //......................................................................
    //----- size -----------------------------------------------------------

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

    //......................................................................
    //----- probability ----------------------------------------------------

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

    //......................................................................
    //----- format ---------------------------------------------------------

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

    //......................................................................
    //----- indexes --------------------------------------------------------

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

    //......................................................................
    //----- get ------------------------------------------------------------

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

    //......................................................................
    //----- based ----------------------------------------------------------

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

    //......................................................................
  }

  //........................................................................
}
