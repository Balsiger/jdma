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

package net.ixitxachitls.dma.entries.extensions;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.BaseItem.WeaponStyle;
import net.ixitxachitls.dma.entries.BaseItem.WeaponType;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseWeaponProto;
import net.ixitxachitls.dma.proto.Entries.BaseWeaponProto.Proficiency;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.NewCritical;
import net.ixitxachitls.dma.values.NewDamage;
import net.ixitxachitls.dma.values.NewDistance;
import net.ixitxachitls.dma.values.NewValue;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the weapon extension for all the entries.
 *
 * @file          BaseWeapon.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public class BaseWeapon extends ValueGroup
{
  /**
   * Default constructor.
   */
  public BaseWeapon(BaseItem inItem)
  {
    m_item = inItem;
  }

  /** The entry this weapon belongs to. */
  protected BaseItem m_item;

  /** The damage the weapon inflicts. */
  protected Optional<NewDamage> m_damage = Optional.absent();

  /** The secondary damage the weapon inflicts. */
  protected Optional<NewDamage> m_secondaryDamage = Optional.absent();

  /** The splash damage the weapon inflicts (if any). */
  protected Optional<NewDamage> m_splash = Optional.absent();

  /** The type of the weapon damage. */
  protected WeaponType m_type = WeaponType.UNKNOWN;

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
      return new Combination.Addable<NewDamage>(m_item, m_damage.get());

    List<Combination<NewDamage>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseWeapon weapon = ((BaseItem)entry).getWeapon();
        combinations.add(weapon.getCombinedDamage());
      }

    return new Combination.Addable<NewDamage>(m_item, combinations);
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
      return new Combination.Addable<NewDamage>(m_item, m_secondaryDamage.get());

    List<Combination<NewDamage>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseWeapon weapon = ((BaseItem)entry).getWeapon();
        combinations.add(weapon.getCombinedSecondaryDamage());
      }

    return new Combination.Addable<NewDamage>(m_item, combinations);
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
      return new Combination.Addable<NewDamage>(m_item, m_splash.get());

    List<Combination<NewDamage>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseWeapon weapon = ((BaseItem)entry).getWeapon();
        combinations.add(weapon.getCombinedSplash());
      }

    return new Combination.Addable<NewDamage>(m_item, combinations);
  }

  /**
   * Get the weapon type.
   *
   * @return      the weapon type
   */
  public WeaponType getWeaponType()
  {
    return m_type;
  }

  /**
   * Get the combined weapon type.
   *
   * @return a combination value with the maximal weapon type
   */
  public Combination<WeaponType> getCombinedWeaponType()
  {
    if(m_type != WeaponType.UNKNOWN)
      return new Combination.Max<WeaponType>(m_item, m_type);

    List<Combination<WeaponType>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseWeapon weapon = ((BaseItem)entry).getWeapon();
        combinations.add(weapon.getCombinedWeaponType());
      }

    return new Combination.Max<WeaponType>(m_item, combinations);
  }

  /**
   * Get the weapon style.
   *
   * @return      the weapon type
   */
  public WeaponStyle getStyle()
  {
    return m_style;
  }

  /**
   * Get the combined weapon style.
   *
   * @return a combination value with the maximal weapon style
   */
  public Combination<WeaponStyle> getCombinedStyle()
  {
    if(m_style!= WeaponStyle.UNKNOWN)
      return new Combination.Max<WeaponStyle>(m_item, m_style);

    List<Combination<WeaponStyle>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseWeapon weapon = ((BaseItem)entry).getWeapon();
        combinations.add(weapon.getCombinedStyle());
      }

    return new Combination.Max<WeaponStyle>(m_item, combinations);
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
      return new Combination.Max<Proficiency>(m_item, m_proficiency);

    List<Combination<Proficiency>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseWeapon weapon = ((BaseItem)entry).getWeapon();
        combinations.add(weapon.getCombinedProficiency());
      }

    return new Combination.Max<Proficiency>(m_item, combinations);
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
      return new Combination.Min<NewDistance>(m_item, m_range.get());

    List<Combination<NewDistance>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseWeapon weapon = ((BaseItem)entry).getWeapon();
        combinations.add(weapon.getCombinedRange());
      }

    return new Combination.Min<NewDistance>(m_item, combinations);
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
      return new Combination.Min<NewDistance>(m_item, m_reach.get());

    List<Combination<NewDistance>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseWeapon weapon = ((BaseItem)entry).getWeapon();
        combinations.add(weapon.getCombinedReach());
      }

    return new Combination.Min<NewDistance>(m_item, combinations);
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
      return new Combination.Min<Integer>(m_item, m_maxAttacks.get());

    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseWeapon weapon = ((BaseItem)entry).getWeapon();
        combinations.add(weapon.getCombinedMaxAttacks());
      }

    return new Combination.Min<Integer>(m_item, combinations);
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
      return new Combination.Addable<NewCritical>(m_item, m_critical.get());

    List<Combination<NewCritical>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseWeapon weapon = ((BaseItem)entry).getWeapon();
        combinations.add(weapon.getCombinedCritical());
      }

    return new Combination.Addable<NewCritical>(m_item, combinations);
  }

  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

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
    values.put(Index.Path.WEAPON_TYPES, m_type.toString());
    values.put(Index.Path.WEAPON_STYLES, m_style.toString());
    values.put(Index.Path.PROFICIENCIES, m_proficiency.toString());
    if(m_range.isPresent())
      values.put(Index.Path.RANGES, m_range.get().toString());
    if(m_reach.isPresent())
      values.put(Index.Path.REACHES, m_reach.get().toString());

    return values;
  }

  @Override
  public Message toProto()
  {
    BaseWeaponProto.Builder builder = BaseWeaponProto.newBuilder();

    if(m_damage.isPresent())
      builder.setDamage(m_damage.get().toProto());
    if(m_secondaryDamage.isPresent())
      builder.setSecondaryDamage(m_secondaryDamage.get().toProto());
    if(m_splash.isPresent())
      builder.setSplash(m_splash.get().toProto());
    if(m_type != WeaponType.UNKNOWN)
      builder.setType(m_type.toProto());
    if(m_critical.isPresent())
      builder.setCritical(m_critical.get().toProto());
    if(m_style != WeaponStyle.UNKNOWN)
      builder.setStyle(m_style.toProto());
    if(m_proficiency != Proficiency.UNKNOWN)
      builder.setProficiency(m_proficiency.toProto());
    if(m_range.isPresent())
      builder.setRange(m_range.get().toProto());
    if(m_reach.isPresent())
      builder.setReach(m_reach.get().toProto());
    if(m_maxAttacks.isPresent())
      builder.setMaxAttacks(m_maxAttacks.get());

    return builder.build();
  }

  @Override
  public void set(Values inValues)
  {
    m_damage = inValues.use("weapon.damage.first", m_damage, NewDamage.PARSER);
    m_secondaryDamage = inValues.use("weapon.damage.second",
                                     m_secondaryDamage, NewDamage.PARSER);
    m_splash = inValues.use("weapon.damage.splash", m_splash, NewDamage.PARSER);
    m_critical = inValues.use("weapon.damage.critical", m_critical,
                              NewCritical.PARSER);
    m_type = inValues.use("weapon.type", m_type,
                          new NewValue.Parser<WeaponType>(1)
    {
      @Override
      public Optional<WeaponType> doParse(String inValue)
      {
        return WeaponType.fromString(inValue);
      }
    });
    m_style = inValues.use("weapon.style", m_style,
                           new NewValue.Parser<WeaponStyle>(1)
    {
      @Override
      public Optional<WeaponStyle> doParse(String inValue)
      {
        return WeaponStyle.fromString(inValue);
      }
    });
    m_proficiency = inValues.use("weapon.proficiency", m_proficiency,
                           new NewValue.Parser<Proficiency>(1)
    {
      @Override
      public Optional<Proficiency> doParse(String inValue)
      {
        return Proficiency.fromString(inValue);
      }
    });
    m_range = inValues.use("weapon.range", m_range, NewDistance.PARSER);
    m_reach = inValues.use("weapon.reach", m_reach, NewDistance.PARSER);
    m_maxAttacks = inValues.use("weapon.max_attacks", m_maxAttacks,
                                NewValue.INTEGER_PARSER);
  }

  public static BaseWeapon fromProto(BaseItem inItem, BaseWeaponProto inProto)
  {
    BaseWeapon result = new BaseWeapon(inItem);
    result.fromProto(inProto);

    return result;
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseWeaponProto))
    {
      Log.warning("cannot parse base weapon proto " + inProto.getClass());
      return;
    }

    BaseWeaponProto proto = (BaseWeaponProto)inProto;

    if(proto.hasDamage())
      m_damage = Optional.of(NewDamage.fromProto(proto.getDamage()));
    if(proto.hasSecondaryDamage())
      m_secondaryDamage =
        Optional.of(NewDamage.fromProto(proto.getSecondaryDamage()));
    if(proto.hasSplash())
      m_splash = Optional.of(NewDamage.fromProto(proto.getSplash()));
    if(proto.hasType())
      m_type = WeaponType.fromProto(proto.getType());
    if(proto.hasCritical())
      m_critical = Optional.of(NewCritical.fromProto(proto.getCritical()));
    if(proto.hasStyle())
      m_style = WeaponStyle.fromProto(proto.getStyle());
    if(proto.hasProficiency())
      m_proficiency = Proficiency.fromProto(proto.getProficiency());
    if(proto.hasRange())
      m_range = Optional.of(NewDistance.fromProto(proto.getRange()));
    if(proto.hasReach())
      m_reach = Optional.of(NewDistance.fromProto(proto.getReach()));
    if(proto.hasMaxAttacks())
      m_maxAttacks = Optional.of(proto.getMaxAttacks());
  }

  @Override
  public <T extends AbstractEntry> AbstractType<T> getType()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getEditType()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AbstractEntry getEntry()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getName()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getID()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void changed(boolean inChanged)
  {
    // TODO Auto-generated method stub

  }

  /**
   * Check whether this group has any values.
   *
   * @return true if there are any values defined, false if not.
   */
  public boolean hasValues()
  {
    return m_damage.isPresent();
  }
}
