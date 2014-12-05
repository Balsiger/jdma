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

import java.util.*;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.CampaignEntryProto;
import net.ixitxachitls.dma.proto.Entries.ItemProto;
import net.ixitxachitls.dma.values.*;
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

  /**
   * The type of this entry.
   */
  public static final Type<Item> TYPE =
      new Type.Builder<>(Item.class, BaseItem.TYPE).build();

  /**
   * The type of the base entry to this entry.
   */
  public static final BaseType<BaseItem> BASE_TYPE = BaseItem.TYPE;

  /**
   * The actual number of hit points the item currently has.
   */
  protected int m_hp = Integer.MIN_VALUE;

  /**
   * The total value of the item.
   */
  protected Optional<Money> m_value = Optional.absent();

  /**
   * The appearance text for this entry.
   */
  protected Optional<String> m_appearance = Optional.absent();

  /**
   * The player notes of the item.
   */
  protected Optional<String> m_playerNotes = Optional.absent();

  /**
   * The name from the player for the item.
   */
  protected Optional<String> m_playerName = Optional.absent();

  /**
   * The DM notes of the item.
   */
  protected Optional<String> m_dmNotes = Optional.absent();

  /**
   * The count for multiple, similar items.
   */
  protected Optional<Integer> m_multiple = Optional.absent();

  /**
   * The count for a multiuse item.
   */
  protected Optional<Integer> m_multiuse = Optional.absent();

  /**
   * The time remaining for a timed item.
   */
  protected Optional<Duration> m_timeLeft = Optional.absent();

  /**
   * The cached contents.
   */
  private Optional<List<Item>> m_contents = Optional.absent();

  /**
   * The possessor of the item, if any.
   */
  private Optional<Monster> m_possessor = null;

  /**
   * Get the hit points of the base item.
   *
   * @return the hit points
   */
  public int getHP()
  {
    return m_hp;
  }

  public boolean hasFinesse()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem) base).hasFinesse())
        return true;

    return false;
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

  public List<Item> getContents()
  {
    if(!m_contents.isPresent())
      m_contents = Optional.fromNullable
          (DMADataFactory.get().getEntries(Item.TYPE,
                                           getCampaign().get().getKey(),
                                           "index-parent",
                                           "item/" + getName().toLowerCase()));

    return m_contents.get();
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
   * Get the name of the entry. This time, also check for special quality
   * modifiers.
   *
   * @return the requested name
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

  public Optional<Integer> getMultiple()
  {
    return m_multiple;
  }

  public Optional<Integer> getMultiuse()
  {
    return m_multiuse;
  }

  public boolean isContainer()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem) base).isContainer())
        return true;

    return false;
  }

  public boolean isLight()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem) base).isLight())
        return true;

    return false;
  }

  public boolean isTimed()
  {
    return m_timeLeft.isPresent();
  }

  public boolean isWearable()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem) base).isWearable())
        return true;

    return false;
  }

  public boolean isAmmunition()
  {
    for(BaseEntry entry : getBaseEntries())
      if(((BaseItem) entry).isAmmunition())
        return true;

    return false;
  }

  public boolean isArmor()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem) base).isArmor())
        return true;

    return false;
  }

  public boolean isWeapon()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem) base).isWeapon())
        return true;

    return false;
  }

  public boolean isCounted()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem) base).isCounted())
        return true;

    return false;
  }

  public boolean isCommodity()
  {
    for(BaseEntry base : getBaseEntries())
      if(((BaseItem)base).isCommodity())
        return true;

    return false;
  }

  /**
   * Get all the items contained in this one.
   *
   * @ param       inDeep true for returning all item, including nested ones,
   *                     false for only the top level items
   * @return      a list of all contained items
   */
  /*
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
  */

  public Optional<Monster> getPossessor()
  {
    if(m_possessor == null)
    {
      if(m_parentName.isPresent() && getCampaign().isPresent())
      {
        String []parts = m_parentName.get().split("/");
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
          m_possessor = DMADataFactory.get().getEntry
            (new EntryKey(id, type.get(), Optional.of(getCampaign().get().getKey())));
        }
        else
          m_possessor = Optional.absent();
      }
      else
        m_possessor = Optional.absent();

    }

    return m_possessor;
  }

  public int getAttackBonus()
  {
    int bonus = 0;
    if(getPossessor().isPresent())
    {
      Optional<Integer> attack = 
        getPossessor().get().getCombinedBaseAttack().get(); 
      bonus = attack.isPresent() ? attack.get() : 0; 

      if(isWeapon())
      {
        boolean finesse = getPossessor().get().hasFeat("weapon finesse")
          && hasFinesse();

        Optional<WeaponStyle> style = getCombinedWeaponStyle().get();
        if(!finesse && style.isPresent() && style.get().isMelee())
          // Add the strength bonus of the wielder.
          bonus += getPossessor().get().getStrengthModifier();
        else
          // Add the dexterity bonus of the wielder.
          bonus += getPossessor().get().getDexterityModifier();

        Optional<Feat> specialization =
          getPossessor().get().getFeat("weapon specialization");
        if(specialization.isPresent()
           && specialization.get().getQualifier().isPresent()
           && hasBaseName(specialization.get().getQualifier().get()))
            bonus += 1;
      }
    }

    return bonus;
  }

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
          damage.add(baseDamage.get());
    }

    if(damage == null)
      damage = new Damage(new Dice(0, 0, 0));

    // add strength modifier
    int strengthModifier = getPossessor().get().getStrengthModifier();
    Optional<WeaponStyle> style = getCombinedWeaponStyle().get();
    if(style.isPresent() && style.get().isMelee()
       && getPossessor().isPresent())
      damage = (Damage)
        damage.add(new Damage(new Dice(0, 0, strengthModifier)));

    // + additional 1/2 strength bonus for two handed melee weapons
    if(style.isPresent() && style.get() == WeaponStyle.TWOHANDED_MELEE) {
      damage = (Damage)
        damage.add(new Damage(new Dice(0, 0, strengthModifier / 2)));
    }

    // TODO: have to subtract strength penalty for non-composite bows
    // TODO: deal with offhand and two-hand wielded weapons

    return damage;
  }

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

    return playerName + " (" + name +")";
  }

  @Override
  public void set(Values inValues)
  {
    super.set(inValues);

    m_hp = inValues.use("hp", m_hp);
    m_value = inValues.use("value", m_value, Money.PARSER);
    m_appearance = inValues.use("appearance", m_appearance);
    m_playerNotes = inValues.use("player_notes", m_playerNotes);
    m_playerName = inValues.use("player_name", m_playerName);
    m_dmNotes = inValues.use("dm_notes", m_dmNotes);
    m_multiple = inValues.use("multiple", m_multiple, Value.INTEGER_PARSER);
    m_multiuse = inValues.use("multiuse", m_multiuse, Value.INTEGER_PARSER);
    m_timeLeft = inValues.use("time_left", m_timeLeft, Duration.PARSER);

    if(m_parentName.isPresent() && !m_parentName.get().contains("/"))
      m_parentName = Optional.of("item/" + m_parentName.get());
  }

  @Override
  public void complete()
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

    super.complete();
  }

  /**
   * Identify the item by filling out the player name (and maybe notes?).
   */
  public void identify()
  {
    m_playerName = Optional.of(fullName());
    changed();
    save();
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

    ItemProto proto = builder.build();
    return proto;
  }

  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof ItemProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    ItemProto proto = (ItemProto)inProto;

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

    super.fromProto(proto.getBase());
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(ItemProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

  //---------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  //ValueGroup.Test
  {
    /** Storage to save the old, real random object. */
    //private java.util.Random m_random;

    /** Called before each test. */
    // public void setUp()
    // {
    //   super.setUp();

    //   m_random = Item.RANDOM;

    //   RANDOM = EasyMock.createMock(java.util.Random.class);

    //   BaseCampaign.GLOBAL.m_bases.clear();
    // }

    /** Called after each test.
     *
     * @throws Exception as in base class
     *
     */
    // public void tearDown()
    // {
    //   RANDOM = m_random;

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
    //     EasyMock.expect(RANDOM.nextInt(inValues[i]))
    //       .andReturn(inValues[i + 1]);

    //   EasyMock.replay(RANDOM);
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

    //   EasyMock.verify(RANDOM);

    //   //RANDOM = old;
    // }

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
  }
}
