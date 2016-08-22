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

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.CampaignEntryProto;
import net.ixitxachitls.dma.proto.Entries.ItemProto;
import net.ixitxachitls.dma.proto.Values.OccurrenceProto;
import net.ixitxachitls.dma.rules.Combat;
import net.ixitxachitls.dma.values.AggregationState;
import net.ixitxachitls.dma.values.Annotated;
import net.ixitxachitls.dma.values.Area;
import net.ixitxachitls.dma.values.AreaShape;
import net.ixitxachitls.dma.values.ArmorType;
import net.ixitxachitls.dma.values.Condition;
import net.ixitxachitls.dma.values.CountUnit;
import net.ixitxachitls.dma.values.Critical;
import net.ixitxachitls.dma.values.Damage;
import net.ixitxachitls.dma.values.Dice;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.Duration;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Money;
import net.ixitxachitls.dma.values.Occurrence;
import net.ixitxachitls.dma.values.Proficiency;
import net.ixitxachitls.dma.values.Slot;
import net.ixitxachitls.dma.values.Substance;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.Volume;
import net.ixitxachitls.dma.values.WeaponStyle;
import net.ixitxachitls.dma.values.WeaponType;
import net.ixitxachitls.dma.values.Weight;
import net.ixitxachitls.dma.values.enums.Size;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

/**
 * This is a real item.
 *
 * @author balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 * @file Item.java
 */

@ParametersAreNonnullByDefault
public class Item extends CampaignEntry
{
  /**
   * The serial version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * This is the internal, default constructor.
   */
  public Item()
  {
    super(TYPE);
  }

  /**
   * This is the internal, default constructor.
   *
   * @param inName the name of the item
   */
  public Item(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final Type<Item> TYPE =
      new Type.Builder<>(Item.class, BaseItem.TYPE).build();

  /** The type of the base entry to this entry. */
  public static final BaseType<BaseItem> BASE_TYPE = BaseItem.TYPE;

  /** The actual number of hit points the item currently has. */
  protected int m_hp = Integer.MIN_VALUE;

  /** The total value of the item. */
  protected Optional<Money> m_value = Optional.absent();

  /** The appearance text for this entry. */
  protected Optional<String> m_appearance = Optional.absent();

  /** The player notes of the item. */
  protected Optional<String> m_playerNotes = Optional.absent();

  /** The name from the player for the item. */
  protected Optional<String> m_playerName = Optional.absent();

  /** The DM notes of the item. */
  protected Optional<String> m_dmNotes = Optional.absent();

  /** The count for multiple, similar items. */
  protected Optional<Integer> m_multiple = Optional.absent();

  /** The count for a multiuse item. */
  protected Optional<Integer> m_multiuse = Optional.absent();

  /** The time remaining for a timed item. */
  protected Optional<Duration> m_timeLeft = Optional.absent();

  /** The cached contents. */
  private Optional<List<Item>> m_contents = Optional.absent();

  /** The possessor of the item, if any. */
  private Optional<Monster> m_possessor = null;

  /**
   * The locations the item was obtained from, in chronoligical order, oldest
   * first.
   */
  private List<Occurrence> m_locations = new ArrayList<>();

  /** Whether the item has been identified or not. */
  private boolean m_identified = false;

  private static final int BAB_PER_ATTACK = 6;

  /**
   * Get the hit points of the base item.
   *
   * @return the hit points
   */
  public int getHP()
  {
    return m_hp;
  }

  /**
   * Check whether this item supports fighting with finesse.
   *
   * @return true if finesse supported, false if not
   */
  public boolean hasFinesse()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem) base).hasFinesse())
        return true;

    return false;
  }

  /**
   * Get the container this item is in, if any.
   *
   * @return the container or absent
   */
  public Optional<Item> getContainer() {
    if (!m_parentName.isPresent())
      return Optional.absent();

    String []parts = m_parentName.get().split("/");
    String id = parts[1];
    Optional<? extends AbstractType<? extends AbstractEntry>> type =
        AbstractType.getTyped(parts[0]);
    if(type.isPresent() && type.get() == Item.TYPE)
    {
      Optional<Item> container = DMADataFactory.get().getEntry
          (new EntryKey(id, Item.TYPE,
                        Optional.of(getCampaign().get().getKey())));
      return container;
    }

    // Probably a character or monster.
    return Optional.absent();
  }

  /**
   * Get the combined value of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Integer>> getCombinedMaxHP()
  {
    Annotated<Optional<Integer>> combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedHP());

    return combined;
  }

  /**
   * Get the combined value of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated.Arithmetic<Weight> getCombinedWeight()
  {
    Annotated.Arithmetic<Weight> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedWeight());

    if(isContainer())
      for(Item item : getContents())
        combined.add(item.getCombinedWeight());

    if(m_multiple.isPresent())
      combined.multiply(m_multiple.get(),
                        getCombinedCountUnit().get().toString());

    return combined;
  }

  /**
   * Get the combined size of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Size>> getCombinedSize()
  {
    Annotated.Max<Size> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedSize());

    return combined;
  }

  /**
   * Get the combined hardness of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Integer>> getCombinedHardness()
  {
    Annotated.Max<Integer> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedHardness());

    return combined;
  }

  /**
   * Get the value of the item in gold piece and their fraction (e.g. silver is
   * 0.1).
   *
   * @return the value
   */
  public double getGoldValue()
  {
    Optional<Money> value = getCombinedValue().get();
    if(!value.isPresent())
      return 0;

    return value.get().asGold();
  }

  /**
   * Get the value of the item.
   *
   * @return the value
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
      return new Annotated.Arithmetic<Money>(m_value.get(), getName());

    Annotated.Arithmetic<Money> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedValue());

    if(isContainer())
      for(Item item : getContents())
        combined.add(item.getCombinedValue());

    // We have to multiply at the end to be sure to include the previous values.
    if(m_multiple.isPresent())
      combined.multiply(m_multiple.get(),
                        getCombinedCountUnit().get().toString());

    if(m_multiuse.isPresent())
      combined.multiply(m_multiuse.get(), "uses");

    return combined;
  }

  /**
   * Get all the items that are contained within this one.
   *
   * @return the items
   */
  public List<Item> getContents()
  {
    if(!m_contents.isPresent())
      m_contents = Optional.fromNullable(DMADataFactory.get().getEntries(
          Item.TYPE, Optional.of(getCampaign().get().getKey()),
          "index-parent", "item/" + getName().toLowerCase()));

    return m_contents.get();
  }

  public List<Item> getAllContents()
  {
    List<Item> items = new ArrayList<>();

    for(Item item : getContents()) {
      items.add(item);
      items.addAll(item.getAllContents());
    }

    return items;
  }

  /**
   * Get the combined hardness of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Integer>> getCombinedBreakDC()
  {
    Annotated.Max<Integer> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedBreakDC());

    return combined;
  }

  /**
   * Get the combined substance of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Substance>> getCombinedSubstance()
  {
    Annotated.Max<Substance> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedSubstance());

    return combined;
  }

  /**
   * Get the combined thickness of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedThickness()
  {
    Annotated.Max<Distance> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedThickness());

    return combined;
  }

  /**
   * Get the combined count of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Integer>> getCombinedMaxMultiple()
  {
    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedMultiple());

    return combined;
  }

  /**
   * Get the combined count of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<CountUnit>> getCombinedCountUnit()
  {
    Annotated.Max<CountUnit> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedCountUnit());

    return combined;
  }

  /**
   * Get the combined count of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Volume>> getCombinedCapacity()
  {
    Annotated.Arithmetic<Volume> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedCapacity());

    return combined;
  }

  /**
   * Get the combined count of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<AggregationState>> getCombinedState()
  {
    Annotated.Max<AggregationState> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedState());

    return combined;
  }

  /**
   * Get the combined count of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Slot>> getCombinedSlot()
  {
    Annotated.Max<Slot> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedSlot());

    return combined;
  }

  /**
   * Get the combined count of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Integer>> getCombinedMaxMultiuse()
  {
    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedMultiuse());

    return combined;
  }

  /**
   * Get the combined duration for donning of the item, including values of
   * base wearable.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Duration>> getCombinedDon()
  {
    Annotated.Arithmetic<Duration> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedDon());

    return combined;
  }

  /**
   * Get the combined duration for donning of the item hastily, including
   * values of base wearable.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Duration>> getCombinedDonHastily()
  {
    Annotated.Arithmetic<Duration> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedDonHastily());

    return combined;
  }

  /**
   * Get the combined duration for rewmoving of the item, including values of
   * base wearable.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Duration>> getCombinedRemove()
  {
    Annotated.Arithmetic<Duration> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : this.getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedRemove());

    return combined;
  }

  /**
   * Get the combined light shape of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<AreaShape>> getCombinedLightShape()
  {
    Annotated.Max<AreaShape> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedLightShape());

    return combined;
  }

  /**
   * Get the combined bright light radius of the item, including values of
   * base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedBrightLight()
  {
    Annotated.Max<Distance> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedBrightLight());

    return combined;
  }

  /**
   * Get the combined shadowylight radius of the item, including values of
   * base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedShadowyLight()
  {
    Annotated.Max<Distance> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedShadowyLight());

    return combined;
  }

  /**
   * Get the duration this still item operates.
   *
   * @return the time
   */
  public Optional<Duration> getTimeLeft()
  {
    return m_timeLeft;
  }

  /**
   * Get the combined time of the item, including values of
   * base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Duration>> getCombinedTimed()
  {
    Annotated.Min<Duration> combined = new Annotated.Min<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedTimed());

    return combined;
  }

  /**
   * Get the combined length of this commodity, including values of bases.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedLength()
  {
    Annotated.Arithmetic<Distance> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedLength());

    return combined;
  }

  /**
   * Get the combined area of this commodity, including values of bases.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Area>> getCombinedArea()
  {
    Annotated.Arithmetic<Area> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedArea());

    return combined;
  }

  /**
   * Get the combined damage of the weapon, including values of base weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Damage>> getCombinedDamage()
  {
    Annotated.Arithmetic<Damage> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedDamage());

    return combined;
  }

  /**
   * Get the combined secondary damage of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Damage>> getCombinedSecondaryDamage()
  {
    Annotated.Arithmetic<Damage> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedSecondaryDamage());

    return combined;
  }

  /**
   * Get the combined splash damage of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Damage>> getCombinedSplash()
  {
    Annotated.Arithmetic<Damage> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedSplash());

    return combined;
  }

  /**
   * Get the combined weapon type.
   *
   * @return a combination value with the maximal weapon type
   */
  public Annotated<Optional<WeaponType>> getCombinedWeaponType()
  {
    Annotated.Max<WeaponType> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedWeaponType());

    return combined;
  }

  /**
   * Get the combined weapon style.
   *
   * @return a combination value with the maximal weapon style
   */
  public Annotated<Optional<WeaponStyle>> getCombinedWeaponStyle()
  {
    Annotated.Max<WeaponStyle> combined = new Annotated.Max<>();
    for(BaseEntry entry : this.getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedWeaponStyle());

    return combined;
  }

  /**
   * Get the combined weapon style.
   *
   * @return a combination value with the maximal weapon style
   */
  public Annotated<Optional<Proficiency>> getCombinedProficiency()
  {
    Annotated.Max<Proficiency> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedProficiency());

    return combined;
  }

  /**
   * Get the combined range of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedRange()
  {
    Annotated.Min<Distance> combined = new Annotated.Min<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedRange());

    return combined;
  }

  /**
   * Get the combined reach of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedReach()
  {
    Annotated.Min<Distance> combined = new Annotated.Min<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedReach());

    return combined;
  }

  /**
   * Get the combined maximal attqcks of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Integer>> getCombinedMaxAttacks()
  {
    Annotated.Min<Integer> combined = new Annotated.Min<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedMaxAttacks());

    return combined;
  }

  /**
   * Get the combined critical of the weapon, including values of base
   * weapons.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Critical>> getCombinedCritical()
  {
    Annotated.Arithmetic<Critical> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedCritical());

    return combined;
  }

  /**
   * Get the combined ac bonus of the armor, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Modifier>> getCombinedArmorBonus()
  {
    Annotated.Arithmetic<Modifier> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedArmorBonus());

    return combined;
  }

  /**
   * Get the combined armor type, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<ArmorType>> getCombinedArmorType()
  {
    Annotated.Max<ArmorType> combined = new Annotated.Max<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedArmorType());

    return combined;
  }

  /**
   * Get the combined max dexterity, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Integer>> getCombinedMaxDex()
  {
    Annotated.Min<Integer> combined = new Annotated.MinBonus<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedMaxDex());

    return combined;
  }

  /**
   * Get the combined check penalty, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Integer>> getCombinedCheckPenalty()
  {
    Annotated<Optional<Integer>> combined = new Annotated.MaxBonus<Integer>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedCheckPenalty());

    return combined;
  }

  /**
   * Get the combined arcane check penalty, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Integer>> getCombinedArcaneFailure()
  {
    Annotated.Integer combined = new Annotated.Integer();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedArcaneFailure());

    return combined;
  }

  /**
   * Get the combined slow speed, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedSlowSpeed()
  {
    Annotated.Arithmetic<Distance> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedSlowSpeed());

    return combined;
  }

  /**
   * Get the combined fast speed, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<Distance>> getCombinedFastSpeed()
  {
    Annotated.Arithmetic<Distance> combined = new Annotated.Arithmetic<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedFastSpeed());

    return combined;
  }

  /**
   * Get the appearance of the item.
   *
   * @return the requested appearance
   */
  public Optional<String> getAppearance()
  {
    return m_appearance;
  }

  /**
   * Get the player notes of the item.
   *
   * @return the requested notes
   */
  public Optional<String> getPlayerNotes()
  {
    return m_playerNotes;
  }

  public List<Item> availableAmmunition()
  {
    List<String> ammunition = getCombinedAmmunitionNeeded().get();
    if(ammunition.isEmpty())
      return new ArrayList<>();

    Optional<Monster> possessor = getPossessor();
    if(!possessor.isPresent())
      return new ArrayList<>();

    return possessor.get().getPossessions(ammunition);
  }

  public Annotated<List<String>> getCombinedAmmunitionNeeded()
  {
    Annotated.List<String> combined = new Annotated.List<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem)entry).getCombinedAmmunitionNeeded());

    return combined;
  }

  /**
   * Get the dm notes of the item.
   *
   * @return the requested notes
   */
  public Optional<String> getDMNotes()
  {
    return m_dmNotes;
  }

  /**
   * Get the player name defined for this item.
   *
   * @return the name
   */
  public Optional<String> getItemPlayerName()
  {
    return m_playerName;
  }

  /**
   * Get the combined player name for the item.
   *
   * @return the combined name
   */
  public Annotated<Optional<String>> getCombinedPlayerName()
  {
    if(m_playerName.isPresent() && !m_playerName.get().isEmpty())
      return new Annotated.String(m_playerName.get(), getName());

    Annotated.String combined = new Annotated.String();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseItem) entry).getCombinedPlayerName());

    return combined;
  }

  /**
   * Check whether the item has been identified.
   *
   * @return true for complete idenfitication, false if not.
   */
  public boolean isIdentified()
  {
    return m_identified;
  }

  @Override
  public String getPlayerName()
  {
    Annotated<Optional<String>> combined = getCombinedPlayerName();
    if (combined.get().isPresent())
      return combined.get().get();

    return getName();
  }

  @Override
  public String getDMName()
  {
    List<String> parts = new ArrayList<String>();
    for(BaseEntry base : getBaseEntries())
      parts.add(base.getName());

    if(parts.isEmpty())
      parts.add(getName());

    return Strings.SPACE_JOINER.join(parts);
  }

  /**
   * Get the count for multiple values.
   *
   * @return the multiple value, if any.
   */
  public Optional<Integer> getMultiple()
  {
    return m_multiple;
  }

  /**
   * Get the count for multi use items.
   *
   * @return the number of uses, if any
   */
  public Optional<Integer> getMultiuse()
  {
    return m_multiuse;
  }

  /**
   * Check whether the item is a container.
   *
   * @return true if this is a container, false if not
   */
  public boolean isContainer()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem) base).isContainer())
        return true;

    return false;
  }

  /**
   * Check whether the item sheds light.
   *
   * @return true if the item produces light, false if not
   */
  public boolean isLight()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem) base).isLight())
        return true;

    return false;
  }

  /**
   * Check whether the item has a time constraint.
   *
   * @return true if the item is time constraint, false if not
   */
  public boolean isTimed()
  {
    return m_timeLeft.isPresent();
  }

  public boolean shownAsWorn()
  {
    Optional<Monster> possessor = getPossessor();
    if(possessor.isPresent())
      return possessor.get().shownAsWorn(this);

    return false;
  }

  /**
   * Check whether the item can be worn.
   *
   * @return true if it is wearable, false if not
   */
  public boolean isWearable()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem) base).isWearable())
        return true;

    return false;
  }

  /**
   * Check whether the item is ammunition for a weapon.
   *
   * @return true for ammunition, false if not
   */
  public boolean isAmmunition()
  {
    for(BaseEntry entry : getBaseEntries())
      if(((BaseItem) entry).isAmmunition())
        return true;

    return false;
  }

  /**
   * Check whether the item is armor.
   *
   * @return true for armor, false if not
   */
  public boolean isArmor()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem) base).isArmor())
        return true;

    return false;
  }

  /**
   * Check whether the item represents a weapon.
   *
   * @return true if it is a weapon, false if not
   */
  public boolean isWeapon()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem) base).isWeapon())
        return true;

    return false;
  }

  /**
   * Check whether an item is counted.
   *
   * @return true if counted, false if not.
   */
  public boolean isCounted()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem) base).isCounted())
        return true;

    return false;
  }

  /**
   * Check whether an item represents a commodity.
   *
   * @return true for commodities, false if not
   */
  public boolean isCommodity()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isCommodity())
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
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isMagical())
        return true;

    return false;
  }

  public boolean isMonetary()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isMonetary())
        return true;

    return false;
  }

  public static void sortByValue(List<Item> inItems)
  {
    Collections.sort(inItems, new Comparator<Item>()
    {
      @Override
      public int compare(Item inFirst, Item inSecond)
      {
        Optional<Money> first = inSecond.getCombinedValue().get();
        Optional<Money> second = inSecond.getCombinedValue().get();

        if(!first.isPresent() && !second.isPresent())
          return 0;

        if(!first.isPresent())
          return -1;

        if(!second.isPresent())
          return +1;

        return Double.compare(first.get().asGold(), second.get().asGold());
      }
    });
  }

  /**
   * Get the monster (or NPC, PC) that possess this item, if any.
   *
   * @return the monster, if any
   */
  public Optional<Monster> getPossessor()
  {
    if(m_possessor == null)
    {
      if(m_parentName.isPresent() && getCampaign().isPresent())
      {
        String []parts = m_parentName.get().split("/");
        Log.important("getting parent for " + m_parentName.get());
        String id = parts[1];
        Optional<? extends AbstractType<? extends AbstractEntry>> type =
            AbstractType.getTyped(parts[0]);
        if(type.isPresent() && type.get() == Item.TYPE)
        {
          Optional<Item> container = DMADataFactory.get().getEntry
              (new EntryKey(id, Item.TYPE,
                            Optional.of(getCampaign().get().getKey())));
          if(container.isPresent())
            m_possessor = container.get().getPossessor();
          else
            m_possessor = Optional.absent();
        }
        else if(type.isPresent()
            && (type.get() == Monster.TYPE || type.get() == NPC.TYPE
            || type.get() == Character.TYPE))
        {
          m_possessor = DMADataFactory.get().getEntry(
              new EntryKey(id, type.get(),
                           Optional.of(getCampaign().get().getKey())));
        }
        else
          m_possessor = Optional.absent();
      }
      else
        m_possessor = Optional.absent();

    }

    return m_possessor;
  }

  public List<Occurrence> getLocations()
  {
    return Collections.unmodifiableList(m_locations);
  }


  /**
   * Get the attack bonus for this item.
   *
   * @return the attack bonus when attacking with this weapon
   */
  public Map<String, List<Integer>> getAttackBonus()
  {
    Map<String, List<Integer>> boni = new LinkedHashMap<>();

    if(getPossessor().isPresent())
    {
      int bonus = 0;
      if(isWeapon())
      {
        boolean finesse =
            getPossessor().get().hasFeat(Combat.FEAT_WEAPON_FINESSE)
            && hasFinesse();

        Optional<WeaponStyle> style = getCombinedWeaponStyle().get();
        if(!finesse && style.isPresent() && style.get().isMelee())
          // Add the strength bonus of the wielder.
          bonus += getPossessor().get().getStrengthModifier();
        else
          // Add the dexterity bonus of the wielder.
          bonus += getPossessor().get().getDexterityModifier();

        // Size modifier
        bonus += getPossessor().get().sizeModifier();

        Optional<Feat> specialization =
            getPossessor().get().getFeat("weapon specialization");
        if(specialization.isPresent()
            && specialization.get().getQualifier().isPresent()
            && hasBaseName(specialization.get().getQualifier().get()))
          bonus += 1;

        List<Feat> focus =
            getPossessor().get().getFeats("weapon focus");
        for(Feat feat : focus)
          if(feat.getQualifier().isPresent()
             && hasBaseName(feat.getQualifier().get()))
          bonus += 1;

        // Add penalties for wrongly sized weapons (PHB 113).
        Optional<Size> possessorSize =
            getPossessor().get().getCombinedSize().get();
        Size wielderSize = getWielderSize();
        if(possessorSize.isPresent()
            && wielderSize != Size.UNKNOWN
            && wielderSize != possessorSize.get())
          bonus += -2 * Math.abs(
              possessorSize.get().ordinal() - wielderSize.ordinal());
      }

      Optional<Integer> attack =
          getPossessor().get().getCombinedBaseAttack().get();
      int bab = attack.isPresent() ? attack.get() : 0;

      List<Integer> attacks = new ArrayList<>();
      for(; bab >= 0; bab -= BAB_PER_ATTACK)
        attacks.add(bab + bonus);

      // Add attack modifiers from qualities.
      for(Quality quality : getPossessor().get().allQualities())
        attacks = adjustEach(
            attacks, quality.attackModifier().total(getPossessor().get()));

      for(BaseEntry base : getBaseEntries())
        for(Quality quality : ((BaseItem)base).getCombinedQualities().get())
          attacks = adjustEach(
              attacks, quality.attackModifier().total(getPossessor().get()));

      boni.put("", attacks);

      // Add conditional attack modifiers.
      for(Feat feat : getPossessor().get().getCombinedFeats().get()) {
        if (feat.getName().equalsIgnoreCase("weapon focus")
            || feat.getName().equalsIgnoreCase("weapon specialization"))
          continue;

        Optional<Condition> condition = feat.getCondition();
        if(condition.isPresent() && condition.get().check(this).isPresent()
            && !condition.get().check(this).get())
          continue;

        attacks = new ArrayList<>(attacks);
        int modifier = feat.attackModifier().unconditionalModifier();
        if (modifier != 0)
        {
          for (int i = 0; i < feat.additionalAttacks(); i++)
            attacks.add(0, attacks.get(0));

          attacks = adjustEach(attacks, modifier);
          boni.put(feat.baseName(), attacks);
        }
      }
    }
    else
      boni.put("", ImmutableList.of(0));

    return boni;
  }

  private List<Integer> adjustEach(List<Integer> values, int adjustment)
  {
    if(adjustment == 0)
      return values;

    List<Integer> result = new ArrayList<>(values.size());
    for(int value : values)
      result.add(value + adjustment);

    return result;
  }

  /**
   * Get the damage this item inflicts (weapons only).
   *
   * @return the damage
   */
  public Damage getDamage()
  {
    Damage damage = null;
    for(BaseEntry base : getBaseEntries())
    {
      Optional<Damage> baseDamage =
          ((BaseItem)base).getCombinedDamage().get();
      if (baseDamage.isPresent())
        if(damage == null)
          damage = baseDamage.get();
        else
          damage = (Damage)damage.add(baseDamage.get());
    }

    if(damage == null)
      damage = new Damage(new Dice(0, 0, 0));

    // add strength modifier
    if(getPossessor().isPresent())
    {
      int strengthModifier = getPossessor().get().getStrengthModifier();
      Optional<WeaponStyle> style = getCombinedWeaponStyle().get();
      if(style.isPresent() && style.get().isMelee()
          && getPossessor().isPresent())
        damage = (Damage)
            damage.add(new Damage(new Dice(0, 0, strengthModifier)));

      // + additional 1/2 strength bonus for two handed melee weapons
      if(style.isPresent() && style.get() == WeaponStyle.TWOHANDED_MELEE)
      {
        damage = (Damage)
            damage.add(new Damage(new Dice(0, 0, strengthModifier / 2)));
      }
    }

    for(BaseEntry base : getBaseEntries())
      for(Quality quality : ((BaseItem)base).getCombinedQualities().get())
      {
        int modifier = quality.damageModifier().total(getPossessor().get());
        if(modifier != 0)
          damage = (Damage)damage.add(new Damage(new Dice(0, 0, modifier)));
      }

    // TODO: deal with offhand and two-hand wielded weapons

    return damage;
  }

  /**
   * Get the critical value for this weapon.
   *
   * @return the critical value
   */
  public Critical getCritical()
  {
    Critical result = null;
    for(BaseEntry base : getBaseEntries())
    {
      Optional<Critical> critical =
          ((BaseItem)base).getCombinedCritical().get();
      if (critical.isPresent())
        if(result == null)
          result = critical.get();
        else
          result = (Critical)result.add(critical.get());
    }

    return result;
  }

  /**
   * Get the distance this weapon can attack to.
   *
   * @return the range
   */
  public Distance getRange()
  {
    Distance result = null;
    for(BaseEntry base : getBaseEntries())
    {
      Optional<Distance> range =
          ((BaseItem)base).getCombinedRange().get();
      if(range.isPresent())
        if(result == null)
          result = range.get();
        else
          result = (Distance)result.add(range.get());
    }

    return result;
  }

  /**
   * Get the type of weapon this item represents.
   *
   * @return the type of weapon
   */
  public WeaponType getWeaponType()
  {
    WeaponType result = WeaponType.UNKNOWN;
    for(BaseEntry base : getBaseEntries())
    {
      WeaponType type = ((BaseItem)base).getWeaponType();
      if(type.ordinal() > result.ordinal())
        result = type;
    }

    return result;
  }

  public Size getWielderSize()
  {
    Size result = Size.UNKNOWN;
    for(BaseEntry base : getBaseEntries())
    {
      Size size = ((BaseItem)base).getWielderSize();
      if(size.ordinal() > result.ordinal())
        result = size;
    }

    return result;
  }

  public WeaponStyle getWeaponStyle()
  {
    WeaponStyle result = WeaponStyle.UNKNOWN;
    for(BaseEntry base : getBaseEntries())
    {
      Annotated<Optional<WeaponStyle>> style =
          ((BaseItem)base).getCombinedWeaponStyle();
      if(style.get().isPresent()
          && style.get().get().ordinal() > result.ordinal())
        result = style.get().get();
    }

    return result;
  }

  /**
   * Get the type of this armor.
   *
   * @return the armor type
   */
  public ArmorType getArmorType()
  {
    ArmorType result = ArmorType.UNKNOWN;
    for(BaseEntry base : getBaseEntries())
    {
      ArmorType type = ((BaseItem)base).getArmorType();
        if(type.ordinal() > result.ordinal())
          result = type;
    }

    return result;
  }

  /**
   * Get the armor class value of this armor.
   *
   * @return the AC value
   */
  public Optional<Modifier> getArmorClass()
  {
    Optional<Modifier> armor = Optional.absent();
    for(BaseEntry base : getBaseEntries())
    {
      Optional<Modifier> baseArmor = ((BaseItem)base).getArmorBonus();
      if(baseArmor.isPresent())
        if(armor.isPresent())
          armor = Optional.of((Modifier) armor.get().add(baseArmor.get()));
        else
          armor = baseArmor;
    }

    return armor;
  }

  /**
   * Get a command to format the name of the item.
   *
   * @return   the command to format the name
   */
  public String fullName()
  {
    String name = getDMName();
    String playerName = getPlayerName();
    if(name.equalsIgnoreCase(playerName))
      return name;

    return playerName + " (" + name + ")";
  }

  @Override
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    Optional<Integer> hp = inValues.use("hp", m_hp);
    if(hp.isPresent())
      m_hp = hp.get();

    m_value = inValues.use("value", m_value, Money.PARSER);
    m_appearance = inValues.use("appearance", m_appearance);
    m_playerNotes = inValues.use("player_notes", m_playerNotes);
    m_playerName = inValues.use("player_name", m_playerName);
    m_dmNotes = inValues.use("dm_notes", m_dmNotes);
    m_multiple = inValues.use("multiple", m_multiple, Value.INTEGER_PARSER);
    m_multiuse = inValues.use("multiuse", m_multiuse, Value.INTEGER_PARSER);
    m_timeLeft = inValues.use("time_left", m_timeLeft, Duration.PARSER);
    m_identified = inValues.use("identified", m_identified,
                                Value.BOOLEAN_PARSER);
    if(getCampaign().isPresent())
      m_locations =
          inValues.use("location", m_locations,
                       Occurrence.parser(getCampaign().get().getCalendar()),
                       "location", "date");

    if(m_parentName.isPresent() && !m_parentName.get().contains("/"))
      m_parentName = Optional.of("item/" + m_parentName.get());
  }

  @Override
  public void initialize()
  {
    if(m_hp == Integer.MIN_VALUE)
    {
      Optional<Integer> hp = getCombinedMaxHP().get();
      if(hp.isPresent())
        m_hp = hp.get();
      else
        m_hp = 1;

      changed();
    }

    if(!m_multiple.isPresent())
    {
      m_multiple = getCombinedMaxMultiple().get();
      if(m_multiple.isPresent())
        changed();
    }

    if(!m_multiuse.isPresent())
    {
      m_multiuse = getCombinedMaxMultiuse().get();
      if(m_multiuse.isPresent())
        changed();
    }

    if(!m_timeLeft.isPresent())
    {
      m_timeLeft = getCombinedTimed().get();
      if(m_timeLeft.isPresent())
        changed();
    }

    if(!m_appearance.isPresent())
    {
      // correct the random value with the computation from the value in
      // relation to the base value
      double itemValue = getGoldValue();
      Optional<Money> baseMoneyValue = getCombinedValue().get();
      double baseValue = 0;
      if (baseMoneyValue.isPresent())
        baseValue = baseMoneyValue.get().asGold();

      // We have to try to get the value from our bases.
      List<String> appearances = new ArrayList<String>();
      for(BaseEntry base : getBaseEntries())
      {
        String appearance =
          ((BaseItem)base).getRandomAppearance(itemValue / baseValue);

        if(appearance != null)
          appearances.add(appearance);
      }

      m_appearance = Optional.of(Strings.toString(appearances, " ", ""));
      changed();
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

    super.initialize();
  }

  @Override
  public Message toProto()
  {
    ItemProto.Builder builder = ItemProto.newBuilder();

    builder.setBase((CampaignEntryProto)super.toProto());

    if(m_hp != Integer.MIN_VALUE)
      builder.setHitPoints(m_hp);

    if(m_value.isPresent())
      builder.setValue(m_value.get().toProto());

    if(m_appearance.isPresent() && !m_appearance.get().isEmpty())
      builder.setAppearance(m_appearance.get());

    if(m_playerNotes.isPresent())
      builder.setPlayerNotes(m_playerNotes.get());

    if(m_playerName.isPresent())
      builder.setPlayerName(m_playerName.get());

    if(m_dmNotes.isPresent())
      builder.setDmNotes(m_dmNotes.get());

    if(m_multiple.isPresent())
      builder.setMultiple(m_multiple.get());

    if(m_multiuse.isPresent())
      builder.setMultiuse(m_multiuse.get());

    if(m_timeLeft.isPresent())
      builder.setTimeLeft(m_timeLeft.get().toProto());

    builder.setIdentified(m_identified);
    for(Occurrence occurrence : m_locations)
      builder.addLocation(occurrence.toProto());

    ItemProto proto = builder.build();
    return proto;
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof ItemProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    ItemProto proto = (ItemProto)inProto;
    super.fromProto(proto.getBase());

    if(proto.hasHitPoints())
      m_hp = proto.getHitPoints();

    if(proto.hasValue())
      m_value = Optional.of(Money.fromProto(proto.getValue()));

    if(proto.hasAppearance())
      m_appearance = Optional.of(proto.getAppearance());

    if(proto.hasPlayerNotes())
      m_playerNotes = Optional.of(proto.getPlayerNotes());

    if(proto.hasPlayerName())
      m_playerName = Optional.of(proto.getPlayerName());

    if(proto.hasDmNotes())
      m_dmNotes = Optional.of(proto.getDmNotes());

    if(proto.hasMultiple())
      m_multiple = Optional.of(proto.getMultiple());

    if(proto.hasMultiuse())
      m_multiuse = Optional.of(proto.getMultiuse());

    if(proto.hasTimeLeft())
      m_timeLeft = Optional.of(Duration.fromProto(proto.getTimeLeft()));

    m_identified = proto.getIdentified();
    if(getCampaign().isPresent())
      for(OccurrenceProto occurrence : proto.getLocationList())
        m_locations.add(Occurrence.fromProto(getCampaign().get().getCalendar(),
                                             occurrence));
  }

  @Override
  protected Message defaultProto()
  {
    return ItemProto.getDefaultInstance();
  }

  //---------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }
}
