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
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseArmorProto;
import net.ixitxachitls.dma.proto.Entries.BaseArmorProto.Type;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.NewDistance;
import net.ixitxachitls.dma.values.NewModifier;
import net.ixitxachitls.dma.values.NewValue;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the weapon extension for all the entries.
 *
 * @file          BaseArmor.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public class BaseArmor extends ValueGroup
{
  /** The possible armor types. */
  public enum ArmorTypes implements EnumSelection.Named,
    EnumSelection.Proto<BaseArmorProto.Type>
  {
    /** The unknown armor type. */
    UNKNOWN("Unknown", BaseArmorProto.Type.UNKNOWN),
    /** Light armor. */
    LIGHT("Light Armor", BaseArmorProto.Type.LIGHT),
    /** Medium armor. */
    MEDIUM("Medium Armor", BaseArmorProto.Type.MEDIUM),
    /** Heavy armor. */
    HEAVY("Heavy Armor", BaseArmorProto.Type.HEAVY),
    /** A shield. */
    SHIELD("Shield", BaseArmorProto.Type.SHIELD),
    /** A shield. */
    TOWER_SHIELD("Tower Shield", BaseArmorProto.Type.TOWER_SHIELD),
    /** A shield. */
    None("None", BaseArmorProto.Type.NONE);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseArmorProto.Type m_proto;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the prot enum value
     */
    private ArmorTypes(String inName, BaseArmorProto.Type inProto)
    {
      m_name = constant("armor.types", inName);
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
    public Type toProto()
    {
      return m_proto;
    }

    /**
     * Convert the given proto into the corresponding enum value.
     *
     * @param inProto  the proto value to convert
     * @return  the converted enum value
     */
    public static ArmorTypes fromProto(BaseArmorProto.Type inProto)
    {
      for(ArmorTypes type : values())
        if(type.m_proto == inProto)
          return type;

      throw new IllegalArgumentException("cannot convert armor type: "
        + inProto);
    }

    /**
     * Get the armor type from the given string.
     *
     * @param inValue the string representation
     * @return the matching type, if any
     */
    public static Optional<ArmorTypes> fromString(String inValue)
    {
      for(ArmorTypes type : values())
        if(type.getName().equalsIgnoreCase(inValue))
          return Optional.of(type);

      return Optional.absent();
    }

    /**
     * Get the possible names of types.
     *
     * @return a list of the names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();
      for(ArmorTypes type : values())
        names.add(type.getName());

      return names;
    }
  }

  /**
   * Default constructor.
   *
   * @param       inEntry the entry associated with this extension
   * @param       inName  the name of the extension
   *
   */
  public BaseArmor(BaseItem inItem)
  {
    m_item = inItem;
  }

  /** The entry this weapon belongs to. */
  protected BaseItem m_item;

  /** The bonus of the armor. */
  protected Optional<NewModifier> m_bonus = Optional.absent();

  /** The type of the armor. */
  @Key("armor type")
  protected ArmorTypes m_type = ArmorTypes.UNKNOWN;

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

  /**
   * Get the ac bonus value.
   *
   * @return      the bonus value
   */
  public Optional<NewModifier> getBonus()
  {
    return m_bonus;
  }

  /**
   * Get the combined ac bonus of the armor, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewModifier> getCombinedBonus()
  {
    if(m_bonus.isPresent())
      return new Combination.Addable<NewModifier>(m_item, m_bonus.get());

    List<Combination<NewModifier>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseArmor armor = ((BaseItem)entry).getArmor();
        combinations.add(armor.getCombinedBonus());
      }

    return new Combination.Addable<NewModifier>(m_item, combinations);
  }

  /**
   * Get the armor type.
   *
   * @return      the armor type
   */
  public ArmorTypes getArmorType()
  {
    return m_type;
  }

  /**
   * Get the combined armor type, including values of base armor.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<ArmorTypes> getCombinedArmorType()
  {
    if(m_type != ArmorTypes.UNKNOWN)
      return new Combination.Max<ArmorTypes>(m_item, m_type);

    List<Combination<ArmorTypes>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseArmor armor = ((BaseItem)entry).getArmor();
        combinations.add(armor.getCombinedArmorType());
      }

    return new Combination.Max<ArmorTypes>(m_item, combinations);
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
      return new Combination.Min<Integer>(m_item, m_maxDex.get());

    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseArmor armor = ((BaseItem)entry).getArmor();
        combinations.add(armor.getCombinedMaxDex());
      }

    return new Combination.Min<Integer>(m_item, combinations);
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
      return new Combination.Integer(m_item, m_checkPenalty.get());

    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseArmor armor = ((BaseItem)entry).getArmor();
        combinations.add(armor.getCombinedCheckPenalty());
      }

    return new Combination.Integer(m_item, combinations);
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
      return new Combination.Integer(m_item, m_arcane.get());

    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseArmor armor = ((BaseItem)entry).getArmor();
        combinations.add(armor.getCombinedArcaneFailure());
      }

    return new Combination.Integer(m_item, combinations);
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
      return new Combination.Addable<NewDistance>(m_item, m_speedSlow.get());

    List<Combination<NewDistance>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseArmor armor = ((BaseItem)entry).getArmor();
        combinations.add(armor.getCombinedSlowSpeed());
      }

    return new Combination.Addable<NewDistance>(m_item, combinations);
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
      return new Combination.Addable<NewDistance>(m_item, m_speedFast.get());

    List<Combination<NewDistance>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseArmor armor = ((BaseItem)entry).getArmor();
        combinations.add(armor.getCombinedFastSpeed());
      }

    return new Combination.Addable<NewDistance>(m_item, combinations);
  }

  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    if(m_bonus.isPresent())
      values.put(Index.Path.ARMOR_BONUSES, "" + m_bonus.get());
    values.put(Index.Path.ARMOR_TYPES, m_type.toString());
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

    return values;
  }

  /**
   * Add current contributions to the given list.
   *
   * @param       inName     the name of the value to collect
   * @param       ioCombined the combined value to collect into
   * @param   <T>        the type of value being collected
   */
  /*
  @Override
  public <T extends Value<T>> void collect(String inName,
                                           Combined<T> ioCombined)
  {
    super.collect(inName, ioCombined);

    if("armor class".equals(inName) && m_bonus.isDefined())
      ioCombined.addModifier(m_bonus, m_entry, "armor");
  }
  */

  @Override
  public void set(Values inValues)
  {
    m_bonus = inValues.use("armor.bonus", m_bonus, NewModifier.PARSER);
    m_type = inValues.use("armor.type", m_type,
                          new NewValue.Parser<ArmorTypes>(1)
   {
      @Override
      public Optional<ArmorTypes> doParse(String inValue)
      {
        return ArmorTypes.fromString(inValue);
      }
    });
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
  }

  @Override
  public Message toProto()
  {
    BaseArmorProto.Builder builder = BaseArmorProto.newBuilder();

    if(m_bonus.isPresent())
      builder.setAcBonus(m_bonus.get().toProto());
    builder.setType(m_type.toProto());
    if(m_maxDex.isPresent())
      builder.setMaxDexterity(m_maxDex.get());
    if(m_checkPenalty.isPresent())
      builder.setCheckPenalty(m_checkPenalty.get());
    if(m_arcane.isPresent())
      builder.setArcaneFailure(m_arcane.get());
    if(m_speedFast.isPresent())
      builder.setSpeedFast(m_speedFast.get().toProto());
    if(m_speedSlow.isPresent())
      builder.setSpeedSlow(m_speedSlow.get().toProto());

    return builder.build();
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseArmorProto))
    {
      Log.warning("cannot parse base armor proto " + inProto.getClass());
      return;
    }

    BaseArmorProto proto = (BaseArmorProto)inProto;

    if(proto.hasAcBonus())
      m_bonus = Optional.of(NewModifier.fromProto(proto.getAcBonus()));
    if(proto.hasType())
      m_type = ArmorTypes.fromProto(proto.getType());
    if(proto.hasMaxDexterity())
      m_maxDex = Optional.of(proto.getMaxDexterity());
    if(proto.hasCheckPenalty())
      m_checkPenalty = Optional.of(proto.getCheckPenalty());
    if(proto.hasArcaneFailure())
      m_arcane = Optional.of(proto.getArcaneFailure());
    if(proto.hasSpeedFast())
      m_speedFast = Optional.of(NewDistance.fromProto(proto.getSpeedFast()));
    if(proto.hasSpeedSlow())
      m_speedFast = Optional.of(NewDistance.fromProto(proto.getSpeedSlow()));
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
   * Check whether any armor values are defined.
   *
   * @return true if armor values are defined, false if not
   */
  public boolean hasValues()
  {
    return m_type != ArmorTypes.UNKNOWN;
  }
}
