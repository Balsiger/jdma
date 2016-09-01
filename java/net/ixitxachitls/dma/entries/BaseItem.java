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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries;
import net.ixitxachitls.dma.proto.Entries.AbstractEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseArmorProto;
import net.ixitxachitls.dma.proto.Entries.BaseCommodityProto;
import net.ixitxachitls.dma.proto.Entries.BaseContainerProto;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseItemProto;
import net.ixitxachitls.dma.proto.Entries.BaseLightProto;
import net.ixitxachitls.dma.proto.Entries.BaseMagicProto;
import net.ixitxachitls.dma.proto.Entries.BaseMultipleProto;
import net.ixitxachitls.dma.proto.Entries.BaseMultiuseProto;
import net.ixitxachitls.dma.proto.Entries.BaseTimedProto;
import net.ixitxachitls.dma.proto.Entries.BaseWeaponProto;
import net.ixitxachitls.dma.proto.Entries.BaseWearableProto;
import net.ixitxachitls.dma.proto.Values.RandomDurationProto;
import net.ixitxachitls.dma.values.AggregationState;
import net.ixitxachitls.dma.values.Annotated;
import net.ixitxachitls.dma.values.Appearance;
import net.ixitxachitls.dma.values.Area;
import net.ixitxachitls.dma.values.AreaShape;
import net.ixitxachitls.dma.values.ArmorType;
import net.ixitxachitls.dma.values.CountUnit;
import net.ixitxachitls.dma.values.Critical;
import net.ixitxachitls.dma.values.Damage;
import net.ixitxachitls.dma.values.Dice;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.Duration;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.ModifierType;
import net.ixitxachitls.dma.values.Money;
import net.ixitxachitls.dma.values.NamedModifier;
import net.ixitxachitls.dma.values.Parser;
import net.ixitxachitls.dma.values.Proficiency;
import net.ixitxachitls.dma.values.SizeModifier;
import net.ixitxachitls.dma.values.Slot;
import net.ixitxachitls.dma.values.Substance;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.Volume;
import net.ixitxachitls.dma.values.WeaponStyle;
import net.ixitxachitls.dma.values.WeaponType;
import net.ixitxachitls.dma.values.Weight;
import net.ixitxachitls.dma.values.enums.Ability;
import net.ixitxachitls.dma.values.enums.Group;
import net.ixitxachitls.dma.values.enums.Named;
import net.ixitxachitls.dma.values.enums.Probability;
import net.ixitxachitls.dma.values.enums.Proto;
import net.ixitxachitls.dma.values.enums.Size;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the basic jDMA base item.
 *
 * @file          BaseItem.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */
public class BaseItem extends BaseEntry
{
  public static class Random {
    public enum Type implements Named, Proto<BaseItemProto.Random.Type>
    {
      UNKNOWN("Unknown", BaseItemProto.Random.Type.UNKNOWN),
      MUNDANE("Mundane", BaseItemProto.Random.Type.MUNDANE);

      private Type(String inName, BaseItemProto.Random.Type inProto)
      {
        m_name = inName;
        m_proto = inProto;
      }

      private final String m_name;
      private final BaseItemProto.Random.Type m_proto;

      @Override
      public String getName()
      {
        return m_name;
      }

      @Override
      public BaseItemProto.Random.Type toProto()
      {
        return m_proto;
      }

      public static Type fromProto(BaseItemProto.Random.Type inProto)
      {
        for(Type type : values())
          if(type.m_proto == inProto)
            return type;

        throw new IllegalArgumentException("cannot convert random type proto: "
          + inProto);
      }

      public static Optional<Type> fromString(String inValue)
      {
        for(Type type : values())
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
        for(Type type : values())
          names.add(type.getName());

        return names;
      }
    }

    public Random(Type inType, Dice inMultiple, List<String> inItems)
    {
      m_type = inType;
      m_multiple = inMultiple;
      m_items = inItems;
    }

    public static final Parser<Random> PARSER = new Parser<Random>(3) {
      @Override
      public Optional<Random> doParse(String inType, String inMultiple,
                                      String inItems)
      {
        Optional<Type> type = Type.fromString(inType);
        Optional<Dice> multiple = Dice.PARSER.parse(inMultiple);
        List<String> items = Arrays.asList(inItems.split("\\s*\\|\\s*"));

        if(type.isPresent() && multiple.isPresent())
          return Optional.of(new Random(type.get(), multiple.get(), items));

        return Optional.absent();
      }
    };

    private final Type m_type;
    private final Dice m_multiple;
    private List<String> m_items;

    public Type getType()
    {
      return m_type;
    }

    public Dice getMultiple()
    {
      return m_multiple;
    }

    public List<String> getItems()
    {
      return Collections.unmodifiableList(m_items);
    }

    public BaseItemProto.Random toProto()
    {
      return BaseItemProto.Random.newBuilder()
          .setType(m_type.toProto())
          .setMultiple(m_multiple.toProto())
          .addAllItem(m_items)
          .build();
    }

    public static Random fromProto(BaseItemProto.Random inProto)
    {
      return new Random(Type.fromProto(inProto.getType()),
                        Dice.fromProto(inProto.getMultiple()),
                        inProto.getItemList());
    }

    public String getItemsString()
    {
      return Strings.PIPE_JOINER.join(m_items);
    }

    @Override
    public String toString()
    {
      String items = getItemsString();
      if(!items.isEmpty())
        items = " with " + items;
      if(m_multiple.isZero() || m_multiple.isOne())
        return m_type.getName() + items;

      return m_type.getName() + " " + m_multiple + items;
    }
  }

  private static final int VALUE_CHANGE_PERCENTS = 5;

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

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
      new BaseType.Builder<>(BaseItem.class).build();

  /** The name used by the player for the item. */
  protected Optional<String> m_playerName = Optional.absent();

  /** The total standard value of the base item. */
  protected Optional<Money> m_value = Optional.absent();

  /** The standard weight of the item. */
  protected Optional<Weight> m_weight = Optional.absent();

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
  protected Optional<Distance> m_thickness = Optional.absent();

  /** The break DC for breaking this item (or bursting out of it). */
  protected Optional<Integer> m_break = Optional.absent();

  /** The number of copies. */
  protected Optional<Integer> m_multiple = Optional.absent();

  /** The number of uses. */
  protected Optional<Integer> m_multiuse = Optional.absent();

  /** The unit count for multiples or multiuses. */
  protected CountUnit m_countUnit = CountUnit.UNKNOWN;

  /** The shape of the light spread. */
  protected AreaShape m_lightShape = AreaShape.UNKNOWN;

  /** The radius this item sheds bright light. */
  protected Optional<Distance> m_brightLight = Optional.absent();

  /** The radius this item sheds shadowy light. */
  protected Optional<Distance> m_shadowyLight = Optional.absent();

  /** The time this item is functioning. */
  protected Optional<Duration> m_timed = Optional.absent();

  /** The magical modifier. */
  protected List<NamedModifier> m_magicalModifiers = new ArrayList<>();

  /** The damage the weapon inflicts. */
  protected Optional<Damage> m_damage = Optional.absent();

  /** The secondary damage the weapon inflicts. */
  protected Optional<Damage> m_secondaryDamage = Optional.absent();

  /** The splash damage the weapon inflicts (if any). */
  protected Optional<Damage> m_splash = Optional.absent();

  /** The type of the weapon damage. */
  protected WeaponType m_weaponType = WeaponType.UNKNOWN;
  protected Size m_wielderSize = Size.UNKNOWN;

  /** The critical range. */
  protected Optional<Critical> m_critical = Optional.absent();

  /** The style of the weapon (for a medium character). */
  protected WeaponStyle m_style = WeaponStyle.UNKNOWN;

  /** The proficiency required for the weapon. */
  protected Proficiency m_proficiency = Proficiency.UNKNOWN;

  /** The range increment, if any, for this weapon. */
  protected Optional<Distance> m_range = Optional.absent();

  /** The reach of the weapon. */
  protected Optional<Distance> m_reach = Optional.absent();

  /** The maximal number of attacks per round. */
  protected Optional<Integer> m_maxAttacks = Optional.absent();

  /** Whether the weapon can be used with finesse. */
  protected boolean m_finesse = false;

  /** Whether the weapon is ammunition. */
  protected boolean m_ammunition = false;

  /** Whether the item is monetary. */
  protected boolean m_monetary = false;

  /** The names of possible ammunition items. */
  protected List<String> m_ammunitionNeeded = new ArrayList<>();

  /** The bonus of the armor. */
  protected Optional<Modifier> m_armorBonus = Optional.absent();

  /** The type of the armor. */
  protected ArmorType m_armorType = ArmorType.UNKNOWN;

  /** The maximal dexterity allowed when wearing the armor. */
  protected Optional<Integer> m_maxDex = Optional.absent();

  /** The armor check penalty. */
  protected Optional<Integer> m_checkPenalty = Optional.absent();

  /** The arcane spell failure. */
  protected Optional<Integer> m_arcane = Optional.absent();

  /** The speed in the armor for 30ft base. */
  protected Optional<Distance> m_speedFast = Optional.absent();

  /** The speed in the armor for 20ft base. */
  protected Optional<Distance> m_speedSlow = Optional.absent();

  /** The area for this commodity. */
  protected Optional<Area> m_area = Optional.absent();

  /** The length of this commodity. */
  protected Optional<Distance> m_length = Optional.absent();

  /** The container's capacity. */
  protected Optional<Volume> m_capacity = Optional.absent();

  /** The state of substances that can be put into the container. */
  protected AggregationState m_state = AggregationState.UNKNOWN;

  /** The slot where the item can be worn. */
  protected Slot m_slot = Slot.UNKNOWN;

  /** How much time it takes to don the item. */
  protected Optional<Duration> m_don = Optional.absent();

  /** How much time it takes to don the item hastily. */
  protected Optional<Duration> m_donHastily = Optional.absent();

  /** How much time it takes to remove the item. */
  protected Optional<Duration> m_remove = Optional.absent();

  /** The item's qualities. */
  protected List<Quality> m_qualities = new ArrayList<>();

  protected List<Random> m_randoms = new ArrayList<>();

  /**
   * Check whether this item has weapon properties.
   *
   * @return true if the item has weapon properties
   */
  public boolean isWeapon()
  {
    if(m_damage.isPresent())
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
    if(m_multiple.isPresent() || m_multiuse.isPresent())
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
   * Check whether this item has armor properties.
   *
   * @return true if the item has armor properties
   */
  public boolean isArmor()
  {
    if(m_armorType != ArmorType.UNKNOWN)
      return true;

    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isArmor())
        return true;

    return false;
  }

  /**
   * Check whether this item has commodity properties.
   *
   * @return true if the item has commodity properties
   */
  public boolean isCommodity()
  {
    if(m_area.isPresent() || m_length.isPresent())
      return true;

    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isCommodity())
        return true;

    return false;
  }

  /**
   * Check whether this item has container properties.
   *
   * @return true if the item has container properties
   */
  public boolean isContainer()
  {
    if(m_state != AggregationState.UNKNOWN && m_capacity.isPresent())
      return true;

    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isContainer())
        return true;

    return false;
  }

  /**
   * Check whether this item has wearable properties.
   *
   * @return true if the item has wearable properties
   */
  public boolean isWearable()
  {
    if(m_slot != Slot.UNKNOWN)
      return true;

    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isWearable())
        return true;

    return false;
  }

  /**
   * Check whether the item is a weapon with finesse.
   *
   * @return true if it has finesse, false else
   */
  public boolean hasFinesse()
  {
    if(m_finesse)
      return true;

    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).hasFinesse())
        return true;

    return false;
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
  public Annotated<Optional<Integer>> getCombinedHP()
  {
    Optional<Integer>hp = getHP();
    if(hp.isPresent())
      return new Annotated.Integer(hp.get(), getName());

    Annotated<Optional<Integer>> combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedHP());

    return combined;
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
  public Annotated<Optional<Integer>> getCombinedHardness()
  {
    if(m_hardness.isPresent())
      return new Annotated.Max<>(m_hardness.get(), getName());

    Annotated.Max<Integer> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedHardness());

    return combined;
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
  public Annotated<Optional<Integer>> getCombinedBreakDC()
  {
    if(m_break.isPresent())
      return new Annotated.Max<>(m_break.get(), getName());

    Annotated.Max<Integer> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedBreakDC());

    return combined;
  }

  /**
   * Get the weight of the item.
   *
   * @return      the weight
   */
  public Optional<Weight> getWeight()
  {
    return m_weight;
  }

  /**
   * Get the combined value of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Weight>> getCombinedWeight()
  {
    if(m_weight.isPresent())
      return new Annotated.Arithmetic<>(m_weight.get(), getName());

    Annotated.Arithmetic<Weight> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedWeight());

    return combined;
  }

  /**
   * Get the value of the item.
   *
   * @return      the value
   */
  public Optional<Money> getValue()
  {
    return m_value;
  }

  /**
   * Get the combined value of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Money>> getCombinedValue()
  {
    if(m_value.isPresent())
      return new Annotated.Arithmetic<>(m_value.get(), getName());

    Annotated<Optional<Money>> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedValue());

    return combined;
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
  public Annotated<Optional<Size>> getCombinedSize()
  {
    if(m_size != Size.UNKNOWN)
      return new Annotated.Max<>(m_size, getName());

    Annotated.Max<Size> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedSize());

    return combined;
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
  public Annotated<Optional<SizeModifier>> getCombinedSizeModifier()
  {
    if(m_sizeModifier != SizeModifier.UNKNOWN)
      return new Annotated.Max<SizeModifier>(m_sizeModifier, getName());

    Annotated.Max<SizeModifier> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedSizeModifier());

    return combined;
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
  public Annotated<Optional<Substance>> getCombinedSubstance()
  {
    if(m_substance != Substance.UNKNOWN)
      return new Annotated.Max<Substance>(m_substance, getName());

    Annotated.Max<Substance> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedSubstance());

    return combined;
  }

  /**
   * Get the thickness of the item.
   *
   * @return      the thickness
   */
  public Optional<Distance> getThickness()
  {
    return m_thickness;
  }

  /**
   * Get the combined thickness of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedThickness()
  {
    if(m_thickness.isPresent())
      return
          new Annotated.Arithmetic<Distance>(m_thickness.get(), getName());

    Annotated.Arithmetic<Distance> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedThickness());

    return combined;
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
  public Annotated<Optional<Probability>> getCombinedProbability()
  {
    if(m_probability != Probability.UNKNOWN)
      return new Annotated.Max<>(m_probability, getName());

    Annotated.Max<Probability> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedProbability());

    return combined;
  }

  /**
   * Get the name of the entry as given to the player.
   *
   * @return      the requested name
   */
  public Optional<String> getPlayerName()
  {
    return m_playerName;
  }

  /**
   * Get the combined player name of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<String>> getCombinedPlayerName()
  {
    if(m_playerName.isPresent())
      return new Annotated.String(m_playerName.get(), getName());

    Annotated.String combined = new Annotated.String();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedPlayerName());

    return combined;
  }

  /**
   * Get the items appearances.
   *
   * @return a list of the appearances with probability
   */
  public List<Appearance> getAppearances()
  {
    return Collections.unmodifiableList(m_appearances);
  }

  /**
   * Get the combined appearances of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<List<Appearance>> getCombinedAppearances()
  {
    if(!m_appearances.isEmpty())
      return new Annotated.List<Appearance>(m_appearances, getName());

    Annotated.List<Appearance> combined = new Annotated.List<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedAppearances());

    return combined;
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
    return Ability.names();
  }

  /**
   * Get the possible names for modifier types.
   *
   * @return the possible modifier type choices.
   */
  public List<String> getModifierTypeNames()
  {
    return ModifierType.names();
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
  public Annotated<Optional<CountUnit>> getCombinedCountUnit()
  {
    if(m_countUnit != CountUnit.UNKNOWN)
      return new Annotated.Max<>(m_countUnit, getName());

    Annotated.Max<CountUnit> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedCountUnit());

    return combined;
  }

  /**
   * Get the count of the item.
   *
   * @return      the count, as enum value
   */
  public Optional<Integer> getMultiple()
  {
    return m_multiple;
  }

  /**
   * Get the count of the item.
   *
   * @return      the count, as enum value
   */
  public Optional<Integer> getMultiuse()
  {
    return m_multiuse;
  }

  /**
   * Get the combined count of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Integer>> getCombinedMultiple()
  {
    if(m_multiple.isPresent())
      return new Annotated.Integer(m_multiple.get(), getName());

    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedMultiple());

    return combined;
  }

  /**
   * Get the combined count of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Integer>> getCombinedMultiuse()
  {
    if(m_multiuse.isPresent())
      return new Annotated.Integer(m_multiuse.get(), getName());

    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedMultiuse());

    return combined;
  }

  /**
   * Get the shape of the light shed by the item.
   *
   * @return      the shape, as enum value
   */
  public AreaShape getLightShape()
  {
    return m_lightShape;
  }

  /**
   * Get the combined light shape of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<AreaShape>> getCombinedLightShape()
  {
    if(m_lightShape != AreaShape.UNKNOWN)
      return new Annotated.Max<AreaShape>(m_lightShape, getName());

    Annotated.Max<AreaShape> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedLightShape());

    return combined;
  }

  /**
   * Get the radius this item sheds bright light.
   *
   * @return      the bright light radius
   */
  public Optional<Distance> getBrightLight()
  {
    return m_brightLight;
  }

  /**
   * Get the combined bright light radius of the item, including values of
   * base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedBrightLight()
  {
    if(m_brightLight.isPresent())
      return new Annotated.Max<Distance>(m_brightLight.get(), getName());

    Annotated.Max<Distance> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedBrightLight());

    return combined;
  }

  /**
   * Get the radius this item sheds shadowy light.
   *
   * @return      the shadowy light radius
   */
  public Optional<Distance> getShadowyLight()
  {
    return m_shadowyLight;
  }

  /**
   * Get the combined shadowylight radius of the item, including values of
   * base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedShadowyLight()
  {
    if(m_shadowyLight.isPresent())
      return new Annotated.Max<Distance>(m_shadowyLight.get(), getName());

    Annotated.Max<Distance> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedShadowyLight());

    return combined;
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
  public Annotated<List<NamedModifier>> getCombinedMagicalModifiers()
  {
    Annotated.List<NamedModifier> combined = new Annotated.List<>();
    if(!m_magicalModifiers.isEmpty())
      combined.add(m_magicalModifiers, getName());

    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedMagicalModifiers());

    return combined;
  }

  /**
   * Get the duration this item operates.
   *
   * @return      the time
   */
  public Optional<Duration> getTimed()
  {
    return m_timed;
  }

  /**
   * Get the combined time of the item, including values of
   * base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Duration>> getCombinedTimed()
  {
    if(m_timed.isPresent())
      return new Annotated.Min<Duration>(m_timed.get(), getName());

    Annotated.Min<Duration> combined = new Annotated.Min<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedTimed());

    return combined;
  }

  /**
   * Get the damage value.
   *
   * @return      the damage value
   */
  public Optional<Damage> getDamage()
  {
    return m_damage;
  }

  /**
   * Get the combined damage of the weapon, including values of base weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Damage>> getCombinedDamage()
  {
    if(m_damage.isPresent())
      return new Annotated.Arithmetic<Damage>(m_damage.get(), getName());

    Annotated.Arithmetic<Damage> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedDamage());

    return combined;
  }

  /**
   * Get the secondary damage value.
   *
   * @return      the secondary damage value
   */
  public Optional<Damage> getSecondaryDamage()
  {
    return m_secondaryDamage;
  }

  /**
   * Get the combined secondary damage of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Damage>> getCombinedSecondaryDamage()
  {
    if(m_secondaryDamage.isPresent())
      return new Annotated.Arithmetic<Damage>(m_secondaryDamage.get(),
                                                 getName());

    Annotated.Arithmetic<Damage> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedSecondaryDamage());

    return combined;
  }

  /**
   * Get the splash damage value.
   *
   * @return      the splash damage value
   */
  public Optional<Damage> getSplash()
  {
    return m_splash;
  }

  /**
   * Get the combined splash damage of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Damage>> getCombinedSplash()
  {
    if(m_splash.isPresent())
      return new Annotated.Arithmetic<Damage>(m_splash.get(), getName());

    Annotated.Arithmetic<Damage> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedSplash());

    return combined;
  }

  /**
   * Get the weapon type.
   *
   * @return      the weapon type
   */
  public WeaponType getWeaponType()
  {
    return m_weaponType;
  }

  /**
   * Get the combined weapon type.
   *
   * @return a combination value with the maximal weapon type
   */
  public Annotated<Optional<WeaponType>> getCombinedWeaponType()
  {
    if(m_weaponType != WeaponType.UNKNOWN)
      return new Annotated.Max<WeaponType>(m_weaponType, getName());

    Annotated.Max<WeaponType> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedWeaponType());

    return combined;
  }

  /**
   * Get the weapon style.
   *
   * @return      the weapon type
   */
  public WeaponStyle getWeaponStyle()
  {
    return m_style;
  }

  /**
   * Get the combined weapon style.
   *
   * @return a combination value with the maximal weapon style
   */
  public Annotated<Optional<WeaponStyle>> getCombinedWeaponStyle()
  {
    if(m_style != WeaponStyle.UNKNOWN)
      return new Annotated.Max<WeaponStyle>(m_style, getName());

    Annotated.Max<WeaponStyle> combined = new Annotated.Max<>();
    for(BaseEntry entry : this.getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedWeaponStyle());

    return combined;
  }

  public Size getWielderSize()
  {
    return m_wielderSize;
  }

  public Annotated<Optional<Size>> getCombinedWielderSize()
  {
    if(m_wielderSize != Size.UNKNOWN)
      return new Annotated.Max<Size>(m_wielderSize, getName());

    Annotated.Max<Size> combined = new Annotated.Max<>();
    for(BaseEntry entry : this.getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedWielderSize());

    return combined;
  }

  /**
   * Get the weapon proficiency.
   *
   * @return      the weapon proficiency
   */
  public Proficiency getProficiency()
  {
    return m_proficiency;
  }

  /**
   * Get the combined weapon style.
   *
   * @return a combination value with the maximal weapon style
   */
  public Annotated<Optional<Proficiency>> getCombinedProficiency()
  {
    if(m_proficiency != Proficiency.UNKNOWN)
      return new Annotated.Max<Proficiency>(m_proficiency, getName());

    Annotated.Max<Proficiency> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedProficiency());

    return combined;
  }

  /**
   * Get the range value.
   *
   * @return      the range value
   */
  public Optional<Distance> getRange()
  {
    return m_range;
  }

  /**
   * Get the combined range of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedRange()
  {
    if(m_range.isPresent())
      return new Annotated.Min<Distance>(m_range.get(), getName());

    Annotated.Min<Distance> combined = new Annotated.Min<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedRange());

    return combined;
  }

  /**
   * Get the reach value.
   *
   * @return      the reach value
   */
  public Optional<Distance> getReach()
  {
    return m_reach;
  }

  /**
   * Get the combined reach of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedReach()
  {
    if(m_reach.isPresent())
      return new Annotated.Min<Distance>(m_reach.get(), getName());

    Annotated.Min<Distance> combined = new Annotated.Min<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedReach());

    return combined;
  }

  /**
   * Get maximal number of attacks per round.
   *
   * @return      the max attacks value
   */
  public Optional<Integer> getMaxAttacks()
  {
    return m_maxAttacks;
  }

  /**
   * Get the combined maximal attqcks of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Integer>> getCombinedMaxAttacks()
  {
    if(m_maxAttacks.isPresent())
      return new Annotated.Min<Integer>(m_maxAttacks.get(), getName());

    Annotated.Min<Integer> combined = new Annotated.Min<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedMaxAttacks());

    return combined;
  }

  /**
   * Get the critical value.
   *
   * @return      the critical value
   */
  public Optional<Critical> getCritical()
  {
    return m_critical;
  }

  /**
   * Get the combined critical of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Critical>> getCombinedCritical()
  {
    if(m_critical.isPresent())
      return new Annotated.Arithmetic<Critical>(m_critical.get(), getName());

    Annotated.Arithmetic<Critical> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedCritical());

    return combined;
  }

  /**
   * Check whether the item is ammunition.
   *
   * @return true for ammunition, false if not
   */
  public boolean isAmmunition()
  {
    if(m_ammunition)
      return true;

    for(BaseEntry entry : getBaseEntries())
      if(((BaseItem)entry).isAmmunition())
        return true;

    return false;
  }

  public boolean isMonetary()
  {
    if(m_monetary)
      return true;

    for(BaseEntry entry : getBaseEntries())
      if(((BaseItem)entry).isMonetary())
        return true;

    return false;
  }

  public List<String> getAmmunitionNeeded()
  {
    return m_ammunitionNeeded;
  }

  public Annotated<List<String>> getCombinedAmmunitionNeeded()
  {
    if(!m_ammunitionNeeded.isEmpty())
      return new Annotated.List<String>(m_ammunitionNeeded, getName());

    Annotated.List<String> combined = new Annotated.List<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedAmmunitionNeeded());

    return combined;
  }

  /**
   * Get the ac bonus value.
   *
   * @return      the bonus value
   */
  public Optional<Modifier> getArmorBonus()
  {
    return m_armorBonus;
  }

  /**
   * Get the combined ac bonus of the armor, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Modifier>> getCombinedArmorBonus()
  {
    if(m_armorBonus.isPresent())
      return new Annotated.Arithmetic<Modifier>(m_armorBonus.get(),
                                                   getName());

    Annotated.Arithmetic<Modifier> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedArmorBonus());

    return combined;
  }

  /**
   * Get the armor type.
   *
   * @return      the armor type
   */
  public ArmorType getArmorType()
  {
    return m_armorType;
  }

  /**
   * Get the combined armor type, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<ArmorType>> getCombinedArmorType()
  {
    if(m_armorType != ArmorType.UNKNOWN)
      return new Annotated.Max<ArmorType>(m_armorType, getName());

    Annotated.Max<ArmorType> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedArmorType());

    return combined;
  }

  /**
   * Get the maximum dexterity.
   *
   * @return      the max dex
   */
  public Optional<Integer> getMaxDex()
  {
    return m_maxDex;
  }

  /**
   * Get the combined max dexterity, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Integer>> getCombinedMaxDex()
  {
    if(m_maxDex.isPresent())
      return new Annotated.Min<Integer>(m_maxDex.get(), getName());

    Annotated<Optional<Integer>> combined = new Annotated.MinBonus<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedMaxDex());

    return combined;
  }

  /**
   * Get the check penalty.
   *
   * @return      the check penalty
   */
  public Optional<Integer> getCheckPenalty()
  {
    return m_checkPenalty;
  }

  /**
   * Get the combined check penalty, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Integer>> getCombinedCheckPenalty()
  {
    if(m_checkPenalty.isPresent())
      return new Annotated.Integer(m_checkPenalty.get(), getName());

    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedCheckPenalty());

    return combined;
  }

  /**
   * Get the arcane check penalty.
   *
   * @return      the arcane check penalty
   */
  public Optional<Integer> getArcaneFailure()
  {
    return m_arcane;
  }

  /**
   * Get the combined arcane check penalty, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Integer>> getCombinedArcaneFailure()
  {
    if(m_arcane.isPresent())
      return new Annotated.Integer(m_arcane.get(), getName());

    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedArcaneFailure());

    return combined;
  }

  /**
   * Get the slow speed in armor.
   *
   * @return      the slow speed value
   */
  public Optional<Distance> getSlowSpeed()
  {
    return m_speedSlow;
  }

  /**
   * Get the combined slow speed, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedSlowSpeed()
  {
    if(m_speedSlow.isPresent())
      return new Annotated.Arithmetic<Distance>(m_speedSlow.get(),
                                                   getName());

    Annotated.Arithmetic<Distance> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedSlowSpeed());

    return combined;
  }

  /**
   * Get the fast speed in armor.
   *
   * @return      the fast speed value
   */
  public Optional<Distance> getFastSpeed()
  {
    return m_speedFast;
  }

  /**
   * Get the combined fast speed, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedFastSpeed()
  {
    if(m_speedFast.isPresent())
      return new Annotated.Arithmetic<Distance>(m_speedFast.get(),
                                                   getName());

    Annotated.Arithmetic<Distance> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedFastSpeed());

    return combined;
  }

  /**
   * Get the area value.
   *
   * @return      the area
   */
  public Optional<Area> getArea()
  {
    return m_area;
  }

  /**
   * Get the combined area of this commodity, including values of bases.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Area>> getCombinedArea()
  {
    if(m_area.isPresent())
      return new Annotated.Arithmetic<Area>(m_area.get(), getName());

    Annotated.Arithmetic<Area> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedArea());

    return combined;
  }

  /**
   * Get the length value.
   *
   * @return      the length
   */
  public Optional<Distance> getLength()
  {
    return m_length;
  }

  /**
   * Get the combined length of this commodity, including values of bases.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedLength()
  {
    if(m_length.isPresent())
      return new Annotated.Arithmetic<Distance>(m_length.get(), getName());

    Annotated.Arithmetic<Distance> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedLength());

    return combined;
  }

  /**
   * Get the capacity value.
   *
   * @return      the capacity
   */
  public Optional<Volume> getCapacity()
  {
    return m_capacity;
  }

  /**
   * Get the combined capacity of this commodity, including values of bases.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Volume>> getCombinedCapacity()
  {
    if(m_capacity.isPresent())
      return new Annotated.Arithmetic<Volume>(m_capacity.get(), getName());

    Annotated.Arithmetic<Volume> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedCapacity());

    return combined;
  }

  /**
   * Get the state value.
   *
   * @return      the state
   */
  public AggregationState getState()
  {
    return m_state;
  }

  /**
   * Get the combined state of this commodity, including values of bases.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<AggregationState>> getCombinedState()
  {
    if(m_state != AggregationState.UNKNOWN)
      return new Annotated.Max<AggregationState>(m_state, getName());

    Annotated.Max<AggregationState> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedState());

    return combined;
  }

  /**
   * Get the slot.
   *
   * @return      the slot
   */
  public Slot getSlot()
  {
    return m_slot;
  }

  /**
   * Get the combined slot, including values of base wearable.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Slot>> getCombinedSlot()
  {
    if(m_slot != Slot.UNKNOWN)
      return new Annotated.Max<Slot>(m_slot, getName());

    Annotated.Max<Slot> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedSlot());

    return combined;
  }

  /**
   * Get the duration for donning the item.
   *
   * @return      the don duration
   */
  public Optional<Duration> getDon()
  {
    return m_don;
  }

  /**
   * Get the combined duration for donning of the item, including values of
   * base wearable.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Duration>> getCombinedDon()
  {
    if(m_don.isPresent())
      return new Annotated.Arithmetic<Duration>(m_don.get(), getName());

    Annotated.Arithmetic<Duration> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedDon());

    return combined;
  }

  /**
   * Get the duration for donning the item hastily.
   *
   * @return      the don hastily duration
   */
  public Optional<Duration> getDonHastily()
  {
    return m_donHastily;
  }

  /**
   * Get the combined duration for donning of the item hastily, including
   * values of base wearable.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Duration>> getCombinedDonHastily()
  {
    if(m_donHastily.isPresent())
      return new Annotated.Arithmetic<Duration>(m_donHastily.get(),
                                                   getName());

    Annotated.Arithmetic<Duration> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedDonHastily());

    return combined;
  }

  /**
   * Get the duration for removing the item.
   *
   * @return      the remove duration
   */
  public Optional<Duration> getRemove()
  {
    return m_remove;
  }

  /**
   * Get the combined duration for rewmoving of the item, including values of
   * base wearable.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Duration>> getCombinedRemove()
  {
    if(m_remove.isPresent())
      return new Annotated.Arithmetic<Duration>(m_remove.get(), getName());

    Annotated.Arithmetic<Duration> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : this.getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedRemove());

    return combined;
  }

 /**
  * Get a random appearance from this base item.
  *
  * @param       inFactor the factor to apply to generated random values.
  *
  * @return      the text for the random appearance.
  */
  public String getRandomAppearance(double inFactor)
  {
    if(!m_appearances.isEmpty())
    {
      int total = 0;
      for(Appearance appearance : m_appearances)
        total += (int)Math.pow(Probability.FACTOR,
                               appearance.getProbability().ordinal());

      int random = 0;
      if(total > 0)
        random = (int)(RANDOM.nextInt(total) * inFactor);

      for(Appearance appearance : m_appearances)
      {
        random -= (int)Math.pow(Probability.FACTOR,
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

        if(!appearance.isEmpty())
          appearances.add(appearance);
      }

    return Strings.SPACE_JOINER.join(appearances);
  }

  public List<Random> getRandoms()
  {
    return m_randoms;
  }

  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(Group.DM);
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
    if(m_multiple.isPresent())
      values.put(Index.Path.COUNTS, m_multiple.get().toString());
    if(m_multiuse.isPresent())
      values.put(Index.Path.COUNTS, m_multiuse.get().toString());
    if(m_brightLight.isPresent())
      values.put(Index.Path.LIGHTS, m_brightLight.toString());
    if(m_shadowyLight.isPresent())
      values.put(Index.Path.LIGHTS, m_shadowyLight.toString());
    if(m_timed.isPresent())
      values.put(Index.Path.DURATIONS, m_timed.toString());

    // damages
    for(Optional<Damage> damage = m_damage; damage.isPresent();
        damage = damage.get().next())
    {
      values.put(Index.Path.DAMAGES, damage.get().getBaseNumber() + "d"
        + damage.get().getBaseDice());

      Optional<Damage.Type> type = damage.get().getType();
      if(type.isPresent())
        values.put(Index.Path.DAMAGE_TYPES, type.toString());
    }

    for(Optional<Damage> damage = m_secondaryDamage; damage.isPresent();
          damage = damage.get().next())
      {
        values.put(Index.Path.DAMAGES, damage.get().getBaseNumber() + "d"
                   + damage.get().getBaseDice());

        Optional<Damage.Type> type = damage.get().getType();
        if(type.isPresent())
          values.put(Index.Path.DAMAGE_TYPES, type.toString());
      }

    for(Optional<Damage> damage = m_splash; damage.isPresent();
          damage = damage.get().next())
    {
      values.put(Index.Path.DAMAGES, damage.get().getBaseNumber() + "d"
                 + damage.get().getBaseDice());

      Optional<Damage.Type> type = damage.get().getType();
      if(type.isPresent())
        values.put(Index.Path.DAMAGE_TYPES, type.toString());
    }

    if(m_critical.isPresent())
    {
      values.put(Index.Path.CRITICALS, "x" + m_critical.get().getMultiplier());
      values.put(Index.Path.THREATS, m_critical.get().getLowThreat() + "-20");
    }
    values.put(Index.Path.WEAPON_TYPES, m_weaponType.toString());
    values.put(Index.Path.WEAPON_STYLES, m_style.toString());
    values.put(Index.Path.PROFICIENCIES, m_proficiency.toString());
    if(m_range.isPresent())
      values.put(Index.Path.RANGES, m_range.get().toString());
    if(m_reach.isPresent())
      values.put(Index.Path.REACHES, m_reach.get().toString());

    if(m_armorBonus.isPresent())
      values.put(Index.Path.ARMOR_BONUSES, "" + m_armorBonus.get());
    values.put(Index.Path.ARMOR_TYPES, m_weaponType.toString());
    if(m_maxDex.isPresent())
      values.put(Index.Path.MAX_DEXTERITIES, "" + m_maxDex.get());
    if(m_checkPenalty.isPresent())
      values.put(Index.Path.CHECK_PENALTIES, "" + m_checkPenalty.get());
    if(m_arcane.isPresent())
      values.put(Index.Path.ARCANE_FAILURES, m_arcane.get() + "%");
    if(m_speedFast.isPresent())
      values.put(Index.Path.SPEEDS, m_speedFast.get().toString());
    if(m_speedSlow.isPresent())
      values.put(Index.Path.SPEEDS, m_speedSlow.get().toString());

    if(m_area.isPresent())
      values.put(Index.Path.AREAS, m_area.toString());
    if(m_length.isPresent())
      values.put(Index.Path.LENGTHS, m_length.toString());

    if(m_capacity.isPresent())
      values.put(Index.Path.CAPACITIES, m_capacity.toString());
    values.put(Index.Path.STATES, m_state.toString());

    if(m_don.isPresent())
      values.put(Index.Path.DONS, m_don.get().toString());
    if(m_donHastily.isPresent())
      values.put(Index.Path.DONS, m_donHastily.get().toString());

    values.put(Index.Path.SLOTS, m_slot.getName());
    if(m_remove.isPresent())
      values.put(Index.Path.REMOVES, m_remove.get().toString());

    for(Random random : m_randoms)
      values.put(Index.Path.RANDOM, random.getType().getName());

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

    if(m_playerName.isPresent())
      builder.setPlayerName(m_playerName.get());

    if(m_monetary)
      builder.setMonetary(m_monetary);

    if(isWeapon())
    {
      BaseWeaponProto.Builder weaponBuilder = BaseWeaponProto.newBuilder();

      if(m_damage.isPresent())
        weaponBuilder.setDamage(m_damage.get().toProto());
      if(m_secondaryDamage.isPresent())
        weaponBuilder.setSecondaryDamage(m_secondaryDamage.get().toProto());
      if(m_splash.isPresent())
        weaponBuilder.setSplash(m_splash.get().toProto());
      if(m_weaponType != WeaponType.UNKNOWN)
        weaponBuilder.setType(m_weaponType.toProto());
      if(m_wielderSize != Size.UNKNOWN)
        weaponBuilder.setWielderSize(m_wielderSize.toProto());
      if(m_critical.isPresent())
        weaponBuilder.setCritical(m_critical.get().toProto());
      if(m_style != WeaponStyle.UNKNOWN)
        weaponBuilder.setStyle(m_style.toProto());
      if(m_proficiency != Proficiency.UNKNOWN)
        weaponBuilder.setProficiency(m_proficiency.toProto());
      if(m_range.isPresent())
        weaponBuilder.setRange(m_range.get().toProto());
      if(m_reach.isPresent())
        weaponBuilder.setReach(m_reach.get().toProto());
      if(m_maxAttacks.isPresent())
        weaponBuilder.setMaxAttacks(m_maxAttacks.get());
      if(m_finesse)
        weaponBuilder.setFinesse(true);
      weaponBuilder.setAmmunition(m_ammunition);
      weaponBuilder.addAllAmmunitionNeeded(m_ammunitionNeeded);

      builder.setWeapon(weaponBuilder.build());
    }

    if(isArmor())
    {
      BaseArmorProto.Builder armorBuilder = BaseArmorProto.newBuilder();

      if(m_armorBonus.isPresent())
        armorBuilder.setAcBonus(m_armorBonus.get().toProto());
      armorBuilder.setType(m_armorType.toProto());
      if(m_maxDex.isPresent())
        armorBuilder.setMaxDexterity(m_maxDex.get());
      if(m_checkPenalty.isPresent())
        armorBuilder.setCheckPenalty(m_checkPenalty.get());
      if(m_arcane.isPresent())
        armorBuilder.setArcaneFailure(m_arcane.get());
      if(m_speedFast.isPresent())
        armorBuilder.setSpeedFast(m_speedFast.get().toProto());
      if(m_speedSlow.isPresent())
        armorBuilder.setSpeedSlow(m_speedSlow.get().toProto());

      builder.setArmor(armorBuilder.build());
    }

    if(isCommodity())
    {
      BaseCommodityProto.Builder commodityBuilder =
          BaseCommodityProto.newBuilder();

      if(m_area.isPresent())
        commodityBuilder.setArea(m_area.get().toProto());
      if(m_length.isPresent())
        commodityBuilder.setLength(m_length.get().toProto());

      builder.setCommodity(commodityBuilder.build());
    }

    if(isContainer())
    {
      BaseContainerProto.Builder containerBuilder =
          BaseContainerProto.newBuilder();

      if(m_capacity.isPresent())
        containerBuilder.setCapacity(m_capacity.get().toProto());
      if(m_state != AggregationState.UNKNOWN)
        containerBuilder.setState(m_state.toProto());

      builder.setContainer(containerBuilder.build());
    }

    if(isWearable())
    {
      BaseWearableProto.Builder wearableBuilder =
          BaseWearableProto.newBuilder();

      if(m_slot != Slot.UNKNOWN)
        wearableBuilder.setSlot(m_slot.toProto());
      if(m_don.isPresent())
        wearableBuilder.setWear(m_don.get().toProto());
      if(m_donHastily.isPresent())
        wearableBuilder.setWearHastily(m_donHastily.get().toProto());
      if(m_remove.isPresent())
        wearableBuilder.setRemove(m_remove.get().toProto());

      builder.setWearable(wearableBuilder.build());
    }

    if(m_multiple.isPresent())
    {
      BaseMultipleProto.Builder counted = BaseMultipleProto.newBuilder();
      if(m_countUnit != CountUnit.UNKNOWN)
        counted.setUnit(m_countUnit.toProto());
      counted.setCount(m_multiple.get());

      builder.setMultiple(counted.build());
    }

    if(m_multiuse.isPresent())
    {
      BaseMultiuseProto.Builder counted = BaseMultiuseProto.newBuilder();
      counted.setCount(m_multiuse.get());

      builder.setMultiuse(counted.build());
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
          magic.addModifier(modifier.toProto());

      builder.setMagic(magic.build());
    }

    for(Quality quality : m_qualities)
      builder.addQualities(quality.toProto());

    for(Random random : m_randoms)
      builder.addRandom(random.toProto());

    BaseItemProto proto = builder.build();
    return proto;
  }

  @Override
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    m_playerName = inValues.use("player_name", m_playerName);
    m_value = inValues.use("value", m_value, Money.PARSER);
    m_weight = inValues.use("weight", m_weight, Weight.PARSER);
    m_hp = inValues.use("hp", m_hp, Value.INTEGER_PARSER);
    m_size = inValues.use("size", m_size, Size.PARSER);
    m_sizeModifier = inValues.use("size_modifier", m_sizeModifier,
                                  SizeModifier.PARSER);
    m_thickness = inValues.use("thickness", m_thickness, Distance.PARSER);
    m_hardness = inValues.use("hardness", m_hardness, Value.INTEGER_PARSER);
    m_break = inValues.use("break", m_break, Value.INTEGER_PARSER);
    m_probability = inValues.use("probability", m_probability,
                                 Probability.PARSER);
    m_substance = inValues.use("substance", m_substance, Substance.PARSER);
    m_appearances = inValues.use("appearances", m_appearances,
                                 Appearance.PARSER, "probability", "text");
    m_monetary = inValues.use("monetary", m_monetary, Value.BOOLEAN_PARSER);
    m_multiple = inValues.use("multiple", m_multiple, Value.INTEGER_PARSER);
    m_multiuse = inValues.use("multiuse", m_multiuse, Value.INTEGER_PARSER);
    m_countUnit = inValues.use("count_unit", m_countUnit, CountUnit.PARSER);
    m_lightShape = inValues.use("light.shape", m_lightShape, AreaShape.PARSER);
    m_brightLight = inValues.use("light.bright", m_brightLight,
                                 Distance.PARSER);
    m_shadowyLight = inValues.use("light.shadowy", m_shadowyLight,
                                  Distance.PARSER);
    m_timed = inValues.use("timed", m_timed, Duration.PARSER);
    m_magicalModifiers = inValues.use("magical", m_magicalModifiers,
                                      NamedModifier.PARSER,
                                      "type", "modifier");

    m_damage = inValues.use("weapon.damage.first", m_damage, Damage.PARSER);
    m_secondaryDamage = inValues.use("weapon.damage.second",
                                     m_secondaryDamage, Damage.PARSER);
    m_splash = inValues.use("weapon.damage.splash", m_splash, Damage.PARSER);
    m_critical = inValues.use("weapon.damage.critical", m_critical,
                              Critical.PARSER);
    m_weaponType = inValues.use("weapon.type", m_weaponType, WeaponType.PARSER);
    m_wielderSize = inValues.use("weapon.wielder_size", m_wielderSize,
                                 Size.PARSER);
    m_style = inValues.use("weapon.style", m_style, WeaponStyle.PARSER);
    m_proficiency = inValues.use("weapon.proficiency", m_proficiency,
                                 Proficiency.PARSER);
    m_range = inValues.use("weapon.range", m_range, Distance.PARSER);
    m_reach = inValues.use("weapon.reach", m_reach, Distance.PARSER);
    m_maxAttacks = inValues.use("weapon.max_attacks", m_maxAttacks,
                                Value.INTEGER_PARSER);
    m_finesse = inValues.use("weapon.finesse", m_finesse,
                             Value.BOOLEAN_PARSER);
    m_ammunition = inValues.use("weapon.ammunition", m_ammunition,
                                Value.BOOLEAN_PARSER);
    m_ammunitionNeeded = inValues.use("weapon.ammunition_needed",
                                      m_ammunitionNeeded);

    m_armorBonus = inValues.use("armor.bonus", m_armorBonus,
                                Modifier.PARSER);
    m_armorType = inValues.use("armor.type", m_armorType, ArmorType.PARSER);
    m_maxDex = inValues.use("armor.max_dex", m_maxDex,
                            Modifier.INTEGER_PARSER);
    m_checkPenalty = inValues.use("armor.check_penalty", m_checkPenalty,
                                  Modifier.INTEGER_PARSER);
    m_arcane = inValues.use("armor.arcane_failure", m_arcane,
                            Modifier.INTEGER_PARSER);
    m_speedSlow = inValues.use("armor.speed_slow", m_speedSlow,
                               Distance.PARSER);
    m_speedFast = inValues.use("armor.speed_fast", m_speedFast,
                               Distance.PARSER);

    m_area = inValues.use("commodity.area", m_area, Area.PARSER);
    m_length = inValues.use("commodity.length", m_length, Distance.PARSER);

    m_capacity = inValues.use("container.capacity", m_capacity, Volume.PARSER);
    m_state = inValues.use("container.state", m_state, AggregationState.PARSER);

    m_slot = inValues.use("wearable.slot", m_slot, Slot.PARSER);
    m_don = inValues.use("wearable.don", m_don, Duration.PARSER);
    m_donHastily = inValues.use("wearable.don_hastily", m_donHastily,
                                Duration.PARSER);
    m_remove = inValues.use("wearable.remove", m_remove, Duration.PARSER);
    m_qualities = inValues.useEntries("quality", m_qualities, Quality.CREATOR);
    m_randoms = inValues.use("random", m_randoms, Random.PARSER,
                             "type", "multiple", "items");
  }

  /**
   * Merge the data from the given proto into this item.
   *
   * @param inProto the proto to merge from
   */
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
      m_value = Optional.of(Money.fromProto(proto.getValue()));

    if(proto.hasWeight())
      m_weight = Optional.of(Weight.fromProto(proto.getWeight()));

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

    if(proto.hasMonetary())
      m_monetary = proto.getMonetary();

    if(proto.hasSubstance())
    {
      m_substance = Substance.fromProto(proto.getSubstance().getMaterial());
      if(proto.getSubstance().hasThickness())
        m_thickness = Optional.of(Distance.fromProto
            (proto.getSubstance().getThickness()));
    }

    if(proto.hasBreakDc())
      m_break = Optional.of(proto.getBreakDc());

    if(proto.hasPlayerName())
      m_playerName = Optional.of(proto.getPlayerName());

    if(proto.hasMultiple())
    {
      if(proto.getMultiple().hasUnit())
        m_countUnit = CountUnit.fromProto(proto.getMultiple().getUnit());
      if(proto.getMultiple().hasCount())
        m_multiple = Optional.of(proto.getMultiple().getCount());
    }

    if(proto.hasMultiuse())
    {
      if(proto.getMultiuse().hasCount())
        m_multiuse = Optional.of(proto.getMultiuse().getCount());
    }

    if(proto.hasWeapon())
    {
      BaseWeaponProto weaponProto = proto.getWeapon();

      if(weaponProto.hasDamage())
        m_damage = Optional.of(Damage.fromProto(weaponProto.getDamage()));
      if(weaponProto.hasSecondaryDamage())
        m_secondaryDamage =
          Optional.of(Damage.fromProto(weaponProto.getSecondaryDamage()));
      if(weaponProto.hasSplash())
        m_splash = Optional.of(Damage.fromProto(weaponProto.getSplash()));
      if(weaponProto.hasType())
        m_weaponType = WeaponType.fromProto(weaponProto.getType());
      if(weaponProto.hasWielderSize())
        m_wielderSize = Size.fromProto(weaponProto.getWielderSize());
      if(weaponProto.hasCritical())
        m_critical =
        Optional.of(Critical.fromProto(weaponProto.getCritical()));
      if(weaponProto.hasStyle())
        m_style = WeaponStyle.fromProto(weaponProto.getStyle());
      if(weaponProto.hasProficiency())
        m_proficiency = Proficiency.fromProto(weaponProto.getProficiency());
      if(weaponProto.hasRange())
        m_range = Optional.of(Distance.fromProto(weaponProto.getRange()));
      if(weaponProto.hasReach())
        m_reach = Optional.of(Distance.fromProto(weaponProto.getReach()));
      if(weaponProto.hasMaxAttacks())
        m_maxAttacks = Optional.of(weaponProto.getMaxAttacks());
      if(weaponProto.hasFinesse())
        m_finesse = weaponProto.getFinesse();
      m_ammunition = weaponProto.getAmmunition();
      m_ammunitionNeeded = weaponProto.getAmmunitionNeededList();
    }

    if(proto.hasWearable())
    {
      BaseWearableProto wearableProto = proto.getWearable();

      if(wearableProto.hasSlot())
        m_slot = Slot.fromProto(wearableProto.getSlot());

      if(wearableProto.hasWear())
        m_don = Optional.of(Duration.fromProto(wearableProto.getWear()));

      if(wearableProto.hasWearHastily())
        m_donHastily =
            Optional.of(Duration.fromProto(wearableProto.getWearHastily()));

      if(wearableProto.hasRemove())
        m_remove = Optional.of(Duration.fromProto(wearableProto.getRemove()));
    }

    if(proto.hasMagic())
      for(BaseMagicProto.Modifier modifier : proto.getMagic().getModifierList())
        m_magicalModifiers.add(NamedModifier.fromProto(modifier));

    if(proto.hasTimed())
      m_timed =
        Optional.of(Duration.fromProto
            (proto.getTimed().getDuration().getDuration()));

    if(proto.hasArmor())
    {
      BaseArmorProto armorProto = proto.getArmor();

      if(armorProto.hasAcBonus())
        m_armorBonus = Optional.of(Modifier.fromProto(armorProto.getAcBonus()));
      if(armorProto.hasType())
        m_armorType = ArmorType.fromProto(armorProto.getType());
      if(armorProto.hasMaxDexterity())
        m_maxDex = Optional.of(armorProto.getMaxDexterity());
      if(armorProto.hasCheckPenalty())
        m_checkPenalty = Optional.of(armorProto.getCheckPenalty());
      if(armorProto.hasArcaneFailure())
        m_arcane = Optional.of(armorProto.getArcaneFailure());
      if(armorProto.hasSpeedFast())
        m_speedFast =
            Optional.of(Distance.fromProto(armorProto.getSpeedFast()));
      if(armorProto.hasSpeedSlow())
        m_speedFast =
            Optional.of(Distance.fromProto(armorProto.getSpeedSlow()));
    }

    if(proto.hasCommodity())
    {
      if(proto.getCommodity().hasArea())
        m_area = Optional.of(Area.fromProto(proto.getCommodity().getArea()));
      if(proto.getCommodity().hasLength())
        m_length =
          Optional.of(Distance.fromProto(proto.getCommodity().getLength()));
    }

    if(proto.hasContainer())
    {
      if(proto.getContainer().hasCapacity())
        m_capacity =
          Optional.of(Volume.fromProto(proto.getContainer().getCapacity()));
      if(proto.getContainer().hasState())
        m_state = AggregationState.fromProto(proto.getContainer().getState());
    }

    if(proto.hasLight())
    {
      if(proto.getLight().hasBright())
      {
        m_brightLight = Optional.of
          (Distance.fromProto(proto.getLight().getBright().getDistance()));
        m_lightShape =
          AreaShape.fromProto(proto.getLight().getBright().getShape());
      }
      if(proto.getLight().hasShadowy())
      {
        m_shadowyLight = Optional.of
          (Distance.fromProto(proto.getLight().getShadowy().getDistance()));
        m_lightShape =
          AreaShape.fromProto(proto.getLight().getShadowy().getShape());
      }
    }

    for(Entries.QualityProto quality : proto.getQualitiesList())
      m_qualities.add(Quality.fromProto(quality));

    for(BaseItemProto.Random random : proto.getRandomList())
      m_randoms.add(Random.fromProto(random));
  }

  public Optional<Money> randomValue()
  {
    if(!m_value.isPresent())
      return Optional.absent();

    if(RANDOM.nextInt(100) >= VALUE_CHANGE_PERCENTS)
      return m_value;

    // We want to randomly adjust the value of the item a bit (+/-50%);
    int random = RANDOM.nextInt(100) + 50;
    double value = m_value.get().asGold() * random / 100;
    return Optional.of(Money.fromGold(value));
  }

  @Override
  protected Message defaultProto()
  {
    return BaseItemProto.getDefaultInstance();
  }

  public List<Quality> getQualities()
  {
    return Collections.unmodifiableList(m_qualities);
  }

  public Annotated<List<Quality>> getCombinedQualities()
  {
    Annotated.List<Quality> combined = new Annotated.List<>();
    combined.add(m_qualities, getName());
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedQualities());

    return combined;
  }

  @Override
  public int randomChance()
  {
    return m_probability.getProbability();
  }

  public Optional<BaseItem.Random> randomChoice()
  {
    if(m_randoms.isEmpty())
      return Optional.absent();

    return Optional.of(m_randoms.get(RANDOM.nextInt(m_randoms.size())));
  }

  //---------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** The init Test. */
    @org.junit.Test
    public void init()
    {
      BaseItem item = new BaseItem("Item");

      assertEquals("name", "Item", item.getName());
      assertFalse("weapon", item.isWeapon());
      assertFalse("counted", item.isCounted());
      assertFalse("light", item.isLight());
      assertFalse("timed", item.isTimed());
      assertFalse("magical", item.isMagical());
      assertFalse("armor", item.isArmor());
      assertFalse("commodity", item.isCommodity());
      assertFalse("container", item.isContainer());
      assertFalse("wearable", item.isWearable());
      assertFalse("finesse", item.hasFinesse());
      assertFalse("hp", item.getHP().isPresent());
      assertEquals("hardness", 0, item.getHardness());
    }

    /** Test setting. */
    @org.junit.Test
    public void set()
    {
      BaseItem item = new BaseItem("");

      Values values = new Values(
          new ImmutableSetMultimap.Builder<String, String>()
              .put("name", "Item")
              .put("player_name", "Player")
              .put("value", "5gp")
              .put("weight", "3 lb")
              .put("hp", "42")
              .put("size", "TIny")
              .put("size_modifier", "tall")
              .put("thickness", "3 in")
              .put("hardness", "23")
              .put("break", "33")
              .put("probability", "RARE")
              .put("substance", "wood")
              .put("appearances.probability", "rare")
              .put("appearances.text", "rare")
              .put("appearances.probability", "common")
              .put("appearances.text", "common")
              .put("appearances.probability", "unique")
              .put("appearances.text", "unique")

              .put("multiple", "2")
              .put("multiuse", "4")
              .put("count_unit", "day")

              .put("light.shape", "sphere")
              .put("light.bright", "20ft")
              .put("light.shadowy", "12 ft")

              .put("timed", "3h")

              .put("magical.type", "Strength")
              .put("magical.modifier", "+2 dodge")

              .put("weapon.damage.first", "2d4")
              .put("weapon.damage.second", "1d6")
              .put("weapon.damage.splash", "1d3")
              .put("weapon.damage.critical", "19-20/x3")
              .put("weapon.type", "bludgeoning")
              .put("weapon.proficiency", "martial")
              .put("weapon.range", "20 ft")
              .put("weapon.reach", "15 ft")
              .put("weapon.max_attacks", "3")
              .put("weapon.finesse", "true")
              .put("weapon.ammunition", "FALSE")

              .put("armor.bonus", "+2 dodge")
              .put("armor.type", "light armor")
              .put("armor.max_dex", "4")
              .put("armor.check_penalty", "10")
              .put("armor.arcane_failure", "5")
              .put("armor.speed_slow", "20 ft")
              .put("armor.speed_fast", "40 ft")

              .put("commodity.area", "2 sq ft")
              .put("commodity.length", "3 ft")

              .put("container.capacity", "3 cu in")
              .put("container.state", "liquid")

              .put("wearable.slot", "TORSO")
              .put("wearable.don", "4 min")
              .put("wearable.don_hastily", "1 standard action")
              .put("wearable.remove", "1 min")
              .build());
      item.set(values);
      assertEquals("messages", "[]", values.obtainMessages().toString());
      assertEquals("name", "Item", item.getName());
      assertEquals("player name", "Player", item.getPlayerName().get());
      assertEquals("value", "5 gp", item.getValue().get().toString());
      assertEquals("weight", "3 lb", item.getWeight().get().toString());
      assertEquals("hp", 42, (long) item.getHP().get());
      assertEquals("size", "Tiny", item.getSize().toString());
      assertEquals("size modifier", "tall", item.getSizeModifier().toString());
      assertEquals("thickness", "3 in", item.getThickness().get().toString());
      assertEquals("hardness", 23, item.getHardness());
      assertEquals("break", 33, (long) item.getBreakDC().get());
      assertEquals("probability", "Rare", item.getProbability().toString());
      assertEquals("substance", "wood", item.getSubstance().toString());
      assertEquals("appearances",
                   "[rare (Rare), common (Common), unique (Unique)]",
                   item.getAppearances().toString());
      assertEquals("multiple", 2, (int) item.getMultiple().get());
      assertTrue("counted", item.isCounted());
      assertEquals("multiuse", 4, (int) item.getMultiuse().get());
      assertEquals("light shape", "Sphere", item.getLightShape().toString());
      assertEquals("bright light", "20 ft",
                   item.getBrightLight().get().toString());
      assertEquals("shadowy light", "12 ft",
                   item.getShadowyLight().get().toString());
      assertTrue("timed", item.isTimed());
      assertEquals("timed", "3 hours", item.getTimed().get().toString());
      assertEquals("magical", "[Strength +2 dodge]",
                   item.getMagicalModifiers().toString());
      assertEquals("weapon damage", "2d4", item.getDamage().get().toString());
      assertEquals("weapon damage", "1d6",
                   item.getSecondaryDamage().get().toString());
      assertEquals("splash", "1d3", item.getSplash().get().toString());
      assertEquals("critical", "19-20/x3", item.getCritical().get().toString());
      assertEquals("weapon type", "Bludgeoning",
                   item.getWeaponType().toString());
      assertEquals("weapon proficiency", "Martial",
                   item.getProficiency().toString());
      assertEquals("range", "20 ft", item.getRange().get().toString());
      assertEquals("reach", "15 ft", item.getReach().get().toString());
      assertEquals("max attack", 3, (int) item.getMaxAttacks().get());
      assertTrue("finesse", item.hasFinesse());
      assertFalse("ammunition", item.isAmmunition());
      assertTrue("weapon", item.isWeapon());
      assertEquals("armor bonus", "+2 dodge",
                   item.getArmorBonus().get().toString());
      assertEquals("armor type", "Light Armor", item.getArmorType().toString());
      assertEquals("max dex", 4, (int) item.getMaxDex().get());
      assertEquals("check penalty", 10, (int)item.getCheckPenalty().get());
      assertEquals("arcane failure", 5, (int) item.getArcaneFailure().get());
      assertEquals("slow speed", "20 ft", item.getSlowSpeed().get().toString());
      assertEquals("fast speed", "40 ft", item.getFastSpeed().get().toString());
      assertTrue("armor", item.isArmor());
      assertEquals("area", "2 sq ft", item.getArea().get().toString());
      assertEquals("length", "3 ft", item.getLength().get().toString());
      assertTrue("commodity", item.isCommodity());
      assertEquals("capacity", "3 cu inches",
                   item.getCapacity().get().toString());
      assertEquals("state", "liquid", item.getState().toString());
      assertTrue("container", item.isContainer());
      assertEquals("slot", "Torso", item.getSlot().toString());
      assertEquals("don", "4 minutes", item.getDon().get().toString());
      assertEquals("player don hastily", "1 standard actions",
                   item.getDonHastily().get().toString());
      assertEquals("remove", "1 minutes", item.getRemove().get().toString());
      assertTrue("wearable", item.isWearable());
    }

    /** Test user access. */
    @org.junit.Test
    public void user()
    {
      BaseCharacter character = new BaseCharacter("Me");
      BaseItem item = new BaseItem("");

      assertTrue("shown to",
                 item.isShownTo(Optional.<BaseCharacter>absent()));
      assertTrue("show to", item.isShownTo(Optional.of(character)));

      assertFalse("is dm",
                  character.isDM(Optional.<BaseCharacter>absent()));
      assertFalse("is dm", item.isDM(Optional.of(character)));
      character.setGroup(Group.ADMIN);
      assertTrue("is dm", item.isDM(Optional.of(character)));
    }

    /** Test searchable. */
    @org.junit.Test
    public void searchables()
    {
      BaseItem item = new BaseItem("Item");
      assertEquals("size", 1, item.collectSearchables().size());
      assertEquals("bases", "[]",
                   item.collectSearchables().get("bases").toString());
    }

    /** Test indexes. */
    @org.junit.Test
    public void indexes()
    {
      BaseItem item = new BaseItem("item");
      assertEquals("size", 10, item.computeIndexValues().size());
      assertContentAnyOrder("keys", item.computeIndexValues().keySet(),
                            "ARMOR_TYPES", "HPS", "WEAPON_STYLES", "STATES",
                            "PROBABILITIES", "WEAPON_TYPES", "PROFICIENCIES",
                            "SUBSTANCES", "SLOTS", "SIZES");
    }

    /** Test proto. */
    @org.junit.Test
    public void proto()
    {
      BaseItemProto proto = BaseItemProto
          .newBuilder()
          .setBase(BaseEntryProto.newBuilder()
                       .setAbstract(AbstractEntryProto
                                        .newBuilder()
                                        .setName("name")
                                        .setType("base item")
                                        .build()))
          .build();
      BaseItem item = new BaseItem();
      item.fromProto(proto);
      assertEquals("proto", proto, item.toProto());
    }
  }
}
