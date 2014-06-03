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

import net.ixitxachitls.dma.entries.indexes.Index;
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
import net.ixitxachitls.dma.values.Appearance;
import net.ixitxachitls.dma.values.Area;
import net.ixitxachitls.dma.values.AreaShape;
import net.ixitxachitls.dma.values.ArmorType;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.CountUnit;
import net.ixitxachitls.dma.values.ModifierType;
import net.ixitxachitls.dma.values.NamedModifier;
import net.ixitxachitls.dma.values.NewCritical;
import net.ixitxachitls.dma.values.NewDamage;
import net.ixitxachitls.dma.values.NewDistance;
import net.ixitxachitls.dma.values.NewDuration;
import net.ixitxachitls.dma.values.NewModifier;
import net.ixitxachitls.dma.values.NewMoney;
import net.ixitxachitls.dma.values.NewValue;
import net.ixitxachitls.dma.values.NewWeight;
import net.ixitxachitls.dma.values.Probability;
import net.ixitxachitls.dma.values.Proficiency;
import net.ixitxachitls.dma.values.Size;
import net.ixitxachitls.dma.values.SizeModifier;
import net.ixitxachitls.dma.values.Slot;
import net.ixitxachitls.dma.values.Substance;
import net.ixitxachitls.dma.values.Volume;
import net.ixitxachitls.dma.values.WeaponStyle;
import net.ixitxachitls.dma.values.WeaponType;
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
  protected Optional<String> m_playerName = Optional.absent();

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

  /** The number of copies. */
  protected Optional<Integer> m_multiple = Optional.absent();

  /** The number of uses. */
  protected Optional<Integer> m_multiuse = Optional.absent();

  /** The unit count for multiples or multiuses. */
  protected CountUnit m_countUnit = CountUnit.UNKNOWN;

  /** The shape of the light spread. */
  protected AreaShape m_lightShape = AreaShape.UNKNOWN;

  /** The radius this item sheds bright light. */
  protected Optional<NewDistance> m_brightLight = Optional.absent();

  /** The radius this item sheds shadowy light. */
  protected Optional<NewDistance> m_shadowyLight = Optional.absent();

  /** The time this item is functioning. */
  protected Optional<NewDuration> m_timed = Optional.absent();

  /** The magical modifier. */
  protected List<NamedModifier> m_magicalModifiers = new ArrayList<>();

  /** The damage the weapon inflicts. */
  protected Optional<NewDamage> m_damage = Optional.absent();

  /** The secondary damage the weapon inflicts. */
  protected Optional<NewDamage> m_secondaryDamage = Optional.absent();

  /** The splash damage the weapon inflicts (if any). */
  protected Optional<NewDamage> m_splash = Optional.absent();

  /** The type of the weapon damage. */
  protected WeaponType m_weaponType = WeaponType.UNKNOWN;

  /** The critical range. */
  protected Optional<NewCritical> m_critical = Optional.absent();

  /** The style of the weapon (for a medium character). */
  protected WeaponStyle m_style = WeaponStyle.UNKNOWN;

  /** The proficiency required for the weapon. */
  protected Proficiency m_proficiency = Proficiency.UNKNOWN;

  /** The range increment, if any, for this weapon. */
  protected Optional<NewDistance> m_range = Optional.absent();

  /** The reach of the weapon. */
  protected Optional<NewDistance> m_reach = Optional.absent();

  /** The maximal number of attacks per round. */
  protected Optional<Integer> m_maxAttacks = Optional.absent();

  /** The bonus of the armor. */
  protected Optional<NewModifier> m_armorBonus = Optional.absent();

  /** The type of the armor. */
  protected ArmorType m_armorType = ArmorType.UNKNOWN;

  /** The maximal dexterity allowed when wearing the armor. */
  protected Optional<Integer> m_maxDex = Optional.absent();

  /** The armor check penalty. */
  protected Optional<Integer> m_checkPenalty = Optional.absent();

  /** The arcane spell failure. */
  protected Optional<Integer> m_arcane = Optional.absent();

  /** The speed in the armor for 30ft base. */
  protected Optional<NewDistance> m_speedFast = Optional.absent();

  /** The speed in the armor for 20ft base. */
  protected Optional<NewDistance> m_speedSlow = Optional.absent();

  /** The area for this commodity. */
  protected Optional<Area> m_area = Optional.absent();

  /** The length of this commodity. */
  protected Optional<NewDistance> m_length = Optional.absent();

  /** The container's capacity. */
  protected Optional<Volume> m_capacity = Optional.absent();

  /** The state of substances that can be put into the container. */
  protected AggregationState m_state = AggregationState.UNKNOWN;

  /** The slot where the item can be worn. */
  protected Slot m_slot = Slot.UNKNOWN;

  /** How much time it takes to don the item. */
  protected Optional<NewDuration> m_don = Optional.absent();

  /** How much time it takes to don the item hastily. */
  protected Optional<NewDuration> m_donHastily = Optional.absent();

  /** How much time it takes to remove the item. */
  protected Optional<NewDuration> m_remove = Optional.absent();

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
      return new Combination.Arithmetic<NewWeight>(this, m_weight.get());

    List<Combination<NewWeight>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedWeight());

    return new Combination.Arithmetic<NewWeight>(this, combinations);
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
      return new Combination.Arithmetic<NewMoney>(this, m_value.get());

    List<Combination<NewMoney>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedValue());

    return new Combination.Arithmetic<NewMoney>(this, combinations);
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
      combinations.add(((BaseItem)entry).getCombinedProbability());

    return new Combination.Max<Probability>(this, combinations);
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
  public Combination<String> getCombinedPlayerName()
  {
    if(m_playerName.isPresent())
      return new Combination.String(this, m_playerName.get());

    List<Combination<String>> combinations = new ArrayList<>();
    combinations.add(new Combination.String(this, getName()));
    for(BaseEntry entry : getBaseEntries())
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
  public Combination<CountUnit> getCombinedCountUnit()
  {
    if(m_countUnit != CountUnit.UNKNOWN)
      return new Combination.Max<CountUnit>(this, m_countUnit);

    List<Combination<CountUnit>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedCountUnit());

    return new Combination.Max<CountUnit>(this, combinations);
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
  public Combination.Integer getCombinedMultiple()
  {
    if(m_multiple.isPresent())
      return new Combination.Integer(this, m_multiple.get());

    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedMultiple());

    return new Combination.Integer(this, combinations);
  }

  /**
   * Get the combined count of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination.Integer getCombinedMultiuse()
  {
    if(m_multiuse.isPresent())
      return new Combination.Integer(this, m_multiuse.get());

    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedMultiuse());

    return new Combination.Integer(this, combinations);
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
  public Combination<AreaShape> getCombinedLightShape()
  {
    if(m_lightShape != AreaShape.UNKNOWN)
      return new Combination.Max<AreaShape>(this, m_lightShape);

    List<Combination<AreaShape>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedLightShape());

    return new Combination.Max<AreaShape>(this, combinations);
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
      combinations.add(((BaseItem)entry).getCombinedTimed());

    return new Combination.Min<NewDuration>(this, combinations);
  }

  /**
   * Get the damage value.
   *
   * @return      the damage value
   */
  public Optional<NewDamage> getDamage()
  {
    return m_damage;
  }

  /**
   * Get the combined damage of the weapon, including values of base weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDamage> getCombinedDamage()
  {
    if(m_damage.isPresent())
      return new Combination.Arithmetic<NewDamage>(this, m_damage.get());

    List<Combination<NewDamage>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedDamage());

    return new Combination.Arithmetic<NewDamage>(this, combinations);
  }

  /**
   * Get the secondary damage value.
   *
   * @return      the secondary damage value
   */
  public Optional<NewDamage> getSecondaryDamage()
  {
    return m_secondaryDamage;
  }

  /**
   * Get the combined secondary damage of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDamage> getCombinedSecondaryDamage()
  {
    if(m_secondaryDamage.isPresent())
      return new Combination.Arithmetic<NewDamage>(this, m_secondaryDamage.get());

    List<Combination<NewDamage>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedSecondaryDamage());

    return new Combination.Arithmetic<NewDamage>(this, combinations);
  }

  /**
   * Get the splash damage value.
   *
   * @return      the splash damage value
   */
  public Optional<NewDamage> getSplash()
  {
    return m_splash;
  }

  /**
   * Get the combined splash damage of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDamage> getCombinedSplash()
  {
    if(m_splash.isPresent())
      return new Combination.Arithmetic<NewDamage>(this, m_splash.get());

    List<Combination<NewDamage>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedSplash());

    return new Combination.Arithmetic<NewDamage>(this, combinations);
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
  public Combination<WeaponType> getCombinedWeaponType()
  {
    if(m_weaponType != WeaponType.UNKNOWN)
      return new Combination.Max<WeaponType>(this, m_weaponType);

    List<Combination<WeaponType>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedWeaponType());

    return new Combination.Max<WeaponType>(this, combinations);
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
  public Combination<WeaponStyle> getCombinedWeaponStyle()
  {
    if(m_style!= WeaponStyle.UNKNOWN)
      return new Combination.Max<WeaponStyle>(this, m_style);

    List<Combination<WeaponStyle>> combinations = new ArrayList<>();
    for(BaseEntry entry : this.getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedWeaponStyle());

    return new Combination.Max<WeaponStyle>(this, combinations);
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
  public Combination<Proficiency> getCombinedProficiency()
  {
    if(m_proficiency != Proficiency.UNKNOWN)
      return new Combination.Max<Proficiency>(this, m_proficiency);

    List<Combination<Proficiency>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedProficiency());

    return new Combination.Max<Proficiency>(this, combinations);
  }

  /**
   * Get the range value.
   *
   * @return      the range value
   */
  public Optional<NewDistance> getRange()
  {
    return m_range;
  }

  /**
   * Get the combined range of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDistance> getCombinedRange()
  {
    if(m_range.isPresent())
      return new Combination.Min<NewDistance>(this, m_range.get());

    List<Combination<NewDistance>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedRange());

    return new Combination.Min<NewDistance>(this, combinations);
  }

  /**
   * Get the reach value.
   *
   * @return      the reach value
   */
  public Optional<NewDistance> getReach()
  {
    return m_reach;
  }

  /**
   * Get the combined reach of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDistance> getCombinedReach()
  {
    if(m_reach.isPresent())
      return new Combination.Min<NewDistance>(this, m_reach.get());

    List<Combination<NewDistance>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
        combinations.add(((BaseItem)entry).getCombinedReach());

    return new Combination.Min<NewDistance>(this, combinations);
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
  public Combination<Integer> getCombinedMaxAttacks()
  {
    if(m_maxAttacks.isPresent())
      return new Combination.Min<Integer>(this, m_maxAttacks.get());

    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedMaxAttacks());

    return new Combination.Min<Integer>(this, combinations);
  }

  /**
   * Get the critical value.
   *
   * @return      the critical value
   */
  public Optional<NewCritical> getCritical()
  {
    return m_critical;
  }

  /**
   * Get the combined critical of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewCritical> getCombinedCritical()
  {
    if(m_critical.isPresent())
      return new Combination.Arithmetic<NewCritical>(this, m_critical.get());

    List<Combination<NewCritical>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedCritical());

    return new Combination.Arithmetic<NewCritical>(this, combinations);
  }

  /**
   * Get the ac bonus value.
   *
   * @return      the bonus value
   */
  public Optional<NewModifier> getArmorBonus()
  {
    return m_armorBonus;
  }

  /**
   * Get the combined ac bonus of the armor, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewModifier> getCombinedArmorBonus()
  {
    if(m_armorBonus.isPresent())
      return new Combination.Arithmetic<NewModifier>(this, m_armorBonus.get());

    List<Combination<NewModifier>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedArmorBonus());

    return new Combination.Arithmetic<NewModifier>(this, combinations);
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
  public Combination<ArmorType> getCombinedArmorType()
  {
    if(m_armorType != ArmorType.UNKNOWN)
      return new Combination.Max<ArmorType>(this, m_armorType);

    List<Combination<ArmorType>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedArmorType());

    return new Combination.Max<ArmorType>(this, combinations);
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
  public Combination<Integer> getCombinedMaxDex()
  {
    if(m_maxDex.isPresent())
      return new Combination.Min<Integer>(this, m_maxDex.get());

    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedMaxDex());

    return new Combination.Min<Integer>(this, combinations);
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
  public Combination<Integer> getCombinedCheckPenalty()
  {
    if(m_checkPenalty.isPresent())
      return new Combination.Integer(this, m_checkPenalty.get());

    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedCheckPenalty());

    return new Combination.Integer(this, combinations);
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
  public Combination<Integer> getCombinedArcaneFailure()
  {
    if(m_arcane.isPresent())
      return new Combination.Integer(this, m_arcane.get());

    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedArcaneFailure());

    return new Combination.Integer(this, combinations);
  }

  /**
   * Get the slow speed in armor.
   *
   * @return      the slow speed value
   */
  public Optional<NewDistance> getSlowSpeed()
  {
    return m_speedSlow;
  }

  /**
   * Get the combined slow speed, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDistance> getCombinedSlowSpeed()
  {
    if(m_speedSlow.isPresent())
      return new Combination.Arithmetic<NewDistance>(this, m_speedSlow.get());

    List<Combination<NewDistance>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedSlowSpeed());

    return new Combination.Arithmetic<NewDistance>(this, combinations);
  }

  /**
   * Get the fast speed in armor.
   *
   * @return      the fast speed value
   */
  public Optional<NewDistance> getFastSpeed()
  {
    return m_speedFast;
  }

  /**
   * Get the combined fast speed, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDistance> getCombinedFastSpeed()
  {
    if(m_speedFast.isPresent())
      return new Combination.Arithmetic<NewDistance>(this, m_speedFast.get());

    List<Combination<NewDistance>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedFastSpeed());

    return new Combination.Arithmetic<NewDistance>(this, combinations);
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
  public Combination<Area> getCombinedArea()
  {
    if(m_area.isPresent())
      return new Combination.Arithmetic<Area>(this, m_area.get());

    List<Combination<Area>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedArea());

    return new Combination.Arithmetic<Area>(this, combinations);
  }

  /**
   * Get the length value.
   *
   * @return      the length
   */
  public Optional<NewDistance> getLength()
  {
    return m_length;
  }

  /**
   * Get the combined length of this commodity, including values of bases.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDistance> getCombinedLength()
  {
    if(m_length.isPresent())
      return new Combination.Arithmetic<NewDistance>(this, m_length.get());

    List<Combination<NewDistance>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedLength());

    return new Combination.Arithmetic<NewDistance>(this, combinations);
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
  public Combination<Volume> getCombinedCapacity()
  {
    if(m_capacity.isPresent())
      return new Combination.Arithmetic<Volume>(this, m_capacity.get());

    List<Combination<Volume>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedCapacity());

    return new Combination.Arithmetic<Volume>(this, combinations);
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
  public Combination<AggregationState> getCombinedState()
  {
    if(m_state != AggregationState.UNKNOWN)
      return new Combination.Max<AggregationState>(this, m_state);

    List<Combination<AggregationState>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedState());

    return new Combination.Max<AggregationState>(this, combinations);
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
  public Combination<Slot> getCombinedSlot()
  {
    if(m_slot != Slot.UNKNOWN)
      return new Combination.Max<Slot>(this, m_slot);

    List<Combination<Slot>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedSlot());

    return new Combination.Max<Slot>(this, combinations);
  }

  /**
   * Get the duration for donning the item.
   *
   * @return      the don duration
   */
  public Optional<NewDuration> getDon()
  {
    return m_don;
  }

  /**
   * Get the combined duration for donning of the item, including values of
   * base wearable.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDuration> getCombinedDon()
  {
    if(m_don.isPresent())
      return new Combination.Arithmetic<NewDuration>(this, m_don.get());

    List<Combination<NewDuration>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedDon());

    return new Combination.Arithmetic<NewDuration>(this, combinations);
  }

  /**
   * Get the duration for donning the item hastily.
   *
   * @return      the don hastily duration
   */
  public Optional<NewDuration> getDonHastily()
  {
    return m_donHastily;
  }

  /**
   * Get the combined duration for donning of the item hastily, including
   * values of base wearable.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDuration> getCombinedDonHastily()
  {
    if(m_donHastily.isPresent())
      return new Combination.Arithmetic<NewDuration>(this, m_donHastily.get());

    List<Combination<NewDuration>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedDonHastily());

    return new Combination.Arithmetic<NewDuration>(this, combinations);
  }

  /**
   * Get the duration for removing the item.
   *
   * @return      the remove duration
   */
  public Optional<NewDuration> getRemove()
  {
    return m_remove;
  }

  /**
   * Get the combined duration for rewmoving of the item, including values of
   * base wearable.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDuration> getCombinedRemove()
  {
    if(m_remove.isPresent())
      return new Combination.Arithmetic<NewDuration>(this, m_remove.get());

    List<Combination<NewDuration>> combinations = new ArrayList<>();
    for(BaseEntry entry : this.getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedRemove());

    return new Combination.Arithmetic<NewDuration>(this, combinations);
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

        if(appearance != null)
          appearances.add(appearance);
      }

    return Strings.SPACE_JOINER.join(appearances);
  }

  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(BaseCharacter.Group.DM);
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
    for(Optional<NewDamage> damage = m_damage; damage.isPresent();
        damage = damage.get().next())
    {
      values.put(Index.Path.DAMAGES, damage.get().getBaseNumber() + "d"
        + damage.get().getBaseDice());

      Optional<NewDamage.Type> type = damage.get().getType();
      if(type.isPresent())
        values.put(Index.Path.DAMAGE_TYPES, type.toString());
    }

    for(Optional<NewDamage> damage = m_secondaryDamage; damage.isPresent();
          damage = damage.get().next())
      {
        values.put(Index.Path.DAMAGES, damage.get().getBaseNumber() + "d"
                   + damage.get().getBaseDice());

        Optional<NewDamage.Type> type = damage.get().getType();
        if(type.isPresent())
          values.put(Index.Path.DAMAGE_TYPES, type.toString());
      }

    for(Optional<NewDamage> damage = m_splash; damage.isPresent();
          damage = damage.get().next())
    {
      values.put(Index.Path.DAMAGES, damage.get().getBaseNumber() + "d"
                 + damage.get().getBaseDice());

      Optional<NewDamage.Type> type = damage.get().getType();
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
      BaseCommodityProto.Builder commodityBuilder = BaseCommodityProto.newBuilder();

      if(m_area.isPresent())
        commodityBuilder.setArea(m_area.get().toProto());
      if(m_length.isPresent())
        commodityBuilder.setLength(m_length.get().toProto());

      builder.setCommodity(commodityBuilder.build());
    }

    if(isContainer())
    {
      BaseContainerProto.Builder containerBuilder = BaseContainerProto.newBuilder();

      if(m_capacity.isPresent())
        containerBuilder.setCapacity(m_capacity.get().toProto());
      if(m_state != AggregationState.UNKNOWN)
        containerBuilder.setState(m_state.toProto());

      builder.setContainer(containerBuilder.build());
    }

    if(isWearable())
    {
      BaseWearableProto.Builder wearableBuilder = BaseWearableProto.newBuilder();

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

    BaseItemProto proto = builder.build();
    System.out.println(proto);
    return proto;
  }

  @Override
  public void set(Values inValues)
  {
    super.set(inValues);

    m_playerName = inValues.use("player_name", m_playerName);
    m_value = inValues.use("value", m_value, NewMoney.PARSER);
    m_weight = inValues.use("weight", m_weight, NewWeight.PARSER);
    m_size = inValues.use("size", m_size, Size.PARSER);
    m_sizeModifier = inValues.use("size_modifier", m_sizeModifier,
                                  SizeModifier.PARSER);
    m_thickness = inValues.use("thickness", m_thickness, NewDistance.PARSER);
    m_hardness = inValues.use("hardness", m_hardness, NewValue.INTEGER_PARSER);
    m_break = inValues.use("break", m_break, NewValue.INTEGER_PARSER);
    m_probability = inValues.use("probability", m_probability,
                                 Probability.PARSER);
    m_substance = inValues.use("substance", m_substance, Substance.PARSER);
    m_appearances = inValues.use("appearances", m_appearances,
                                 Appearance.PARSER, "probability", "text");
    m_multiple = inValues.use("multiple", m_multiple, NewValue.INTEGER_PARSER);
    m_multiuse = inValues.use("multiuse", m_multiuse, NewValue.INTEGER_PARSER);
    m_countUnit = inValues.use("count_unit", m_countUnit, CountUnit.PARSER);
    m_lightShape = inValues.use("light.shape", m_lightShape, AreaShape.PARSER);
    m_brightLight = inValues.use("light.bright", m_brightLight,
                                 NewDistance.PARSER);
    m_shadowyLight = inValues.use("light.shadowy", m_shadowyLight,
                                  NewDistance.PARSER);
    m_timed = inValues.use("timed", m_timed, NewDuration.PARSER);
    m_magicalModifiers = inValues.use("magical", m_magicalModifiers,
                                      NamedModifier.PARSER,
                                      "type", "modifier");

    m_damage = inValues.use("weapon.damage.first", m_damage, NewDamage.PARSER);
    m_secondaryDamage = inValues.use("weapon.damage.second",
                                     m_secondaryDamage, NewDamage.PARSER);
    m_splash = inValues.use("weapon.damage.splash", m_splash, NewDamage.PARSER);
    m_critical = inValues.use("weapon.damage.critical", m_critical,
                              NewCritical.PARSER);
    m_weaponType = inValues.use("weapon.type", m_weaponType, WeaponType.PARSER);
    m_style = inValues.use("weapon.style", m_style, WeaponStyle.PARSER);
    m_proficiency = inValues.use("weapon.proficiency", m_proficiency,
                                 Proficiency.PARSER);
    m_range = inValues.use("weapon.range", m_range, NewDistance.PARSER);
    m_reach = inValues.use("weapon.reach", m_reach, NewDistance.PARSER);
    m_maxAttacks = inValues.use("weapon.max_attacks", m_maxAttacks,
                                NewValue.INTEGER_PARSER);

    m_armorBonus = inValues.use("armor.bonus", m_armorBonus,
                                NewModifier.PARSER);
    m_armorType = inValues.use("armor.type", m_armorType, ArmorType.PARSER);
    m_maxDex = inValues.use("armor.max_dex", m_maxDex,
                            NewModifier.INTEGER_PARSER);
    m_checkPenalty = inValues.use("armor.check_penalty", m_checkPenalty,
                                  NewModifier.INTEGER_PARSER);
    m_arcane = inValues.use("armor.arcane_failure", m_arcane,
                            NewModifier.INTEGER_PARSER);
    m_speedSlow = inValues.use("armor.speed_slow", m_speedSlow,
                               NewDistance.PARSER);
    m_speedFast = inValues.use("armor.speed_fast", m_speedFast,
                               NewDistance.PARSER);

    m_area = inValues.use("commodity.area", m_area, Area.PARSER);
    m_length = inValues.use("commodity.length", m_length, NewDistance.PARSER);

    m_capacity = inValues.use("container.capacity", m_capacity, Volume.PARSER);
    m_state = inValues.use("container.state", m_state, AggregationState.PARSER);

    m_slot = inValues.use("wearable.slot", m_slot, Slot.PARSER);
    m_don = inValues.use("wearable.don", m_don, NewDuration.PARSER);
    m_donHastily = inValues.use("wearable.don_hastily", m_donHastily,
                                NewDuration.PARSER);
    m_remove = inValues.use("wearable.remove", m_remove, NewDuration.PARSER);
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
        m_damage = Optional.of(NewDamage.fromProto(weaponProto.getDamage()));
      if(weaponProto.hasSecondaryDamage())
        m_secondaryDamage =
          Optional.of(NewDamage.fromProto(weaponProto.getSecondaryDamage()));
      if(weaponProto.hasSplash())
        m_splash = Optional.of(NewDamage.fromProto(weaponProto.getSplash()));
      if(weaponProto.hasType())
        m_weaponType = WeaponType.fromProto(weaponProto.getType());
      if(weaponProto.hasCritical())
        m_critical =
        Optional.of(NewCritical.fromProto(weaponProto.getCritical()));
      if(weaponProto.hasStyle())
        m_style = WeaponStyle.fromProto(weaponProto.getStyle());
      if(weaponProto.hasProficiency())
        m_proficiency = Proficiency.fromProto(weaponProto.getProficiency());
      if(weaponProto.hasRange())
        m_range = Optional.of(NewDistance.fromProto(weaponProto.getRange()));
      if(weaponProto.hasReach())
        m_reach = Optional.of(NewDistance.fromProto(weaponProto.getReach()));
      if(weaponProto.hasMaxAttacks())
        m_maxAttacks = Optional.of(weaponProto.getMaxAttacks());
    }

    if(proto.hasWearable())
    {
      BaseWearableProto wearableProto = proto.getWearable();

      if(wearableProto. hasSlot())
        m_slot = Slot.fromProto(wearableProto. getSlot());

      if(wearableProto. hasWear())
        m_don = Optional.of(NewDuration.fromProto(wearableProto. getWear()));

      if(wearableProto. hasWearHastily())
        m_donHastily = Optional.of(NewDuration.fromProto(wearableProto. getWearHastily()));

      if(wearableProto. hasRemove())
        m_remove = Optional.of(NewDuration.fromProto(wearableProto. getRemove()));
    }

    if(proto.hasMagic())
      for(BaseMagicProto.Modifier modifier : proto.getMagic().getModifierList())
        m_magicalModifiers.add(NamedModifier.fromProto(modifier));

    if(proto.hasTimed())
      m_timed =
        Optional.of(NewDuration.fromProto
                    (proto.getTimed().getDuration().getDuration()));

    if(proto.hasArmor())
    {
      BaseArmorProto armorProto = proto.getArmor();

      if(armorProto. hasAcBonus())
        m_armorBonus = Optional.of(NewModifier.fromProto(armorProto. getAcBonus()));
      if(armorProto. hasType())
        m_armorType = ArmorType.fromProto(armorProto. getType());
      if(armorProto. hasMaxDexterity())
        m_maxDex = Optional.of(armorProto. getMaxDexterity());
      if(armorProto. hasCheckPenalty())
        m_checkPenalty = Optional.of(armorProto. getCheckPenalty());
      if(armorProto. hasArcaneFailure())
        m_arcane = Optional.of(armorProto. getArcaneFailure());
      if(armorProto. hasSpeedFast())
        m_speedFast = Optional.of(NewDistance.fromProto(armorProto. getSpeedFast()));
      if(armorProto. hasSpeedSlow())
        m_speedFast = Optional.of(NewDistance.fromProto(armorProto. getSpeedSlow()));
    }

    if(proto.hasCommodity())
    {
      if(proto.getCommodity().hasArea())
        m_area = Optional.of(Area.fromProto(proto.getCommodity().getArea()));
      if(proto.getCommodity().hasLength())
        m_length =
          Optional.of(NewDistance.fromProto(proto.getCommodity().getLength()));
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
          (NewDistance.fromProto(proto.getLight().getBright().getDistance()));
        m_lightShape =
          AreaShape.fromProto(proto.getLight().getBright().getShape());
      }
      if(proto.getLight().hasShadowy())
      {
        m_shadowyLight = Optional.of
          (NewDistance.fromProto(proto.getLight().getShadowy().getDistance()));
        m_lightShape =
          AreaShape.fromProto(proto.getLight().getShadowy().getShape());
      }
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
  public static class Test extends net.ixitxachitls.util.test.TestCase
  //extends ValueGroup.Test
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
