/******************************************************************************
 * Copyright (c) 2002-2013 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import com.google.common.base.Optional;
import com.google.common.collect.Multiset;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.LevelProto;
import net.ixitxachitls.dma.proto.Entries.MonsterProto;
import net.ixitxachitls.dma.proto.Entries.NPCProto;
import net.ixitxachitls.dma.values.Annotated;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.enums.Gender;
import net.ixitxachitls.util.logging.Log;

/**
 * A non-player character.
 *
 * @file   NPC.javas
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */
public class NPC extends Monster
{
  /**
   * Create the NPC with no name.
   */
  public NPC()
  {
    super(TYPE);
  }

  /**
   * Create an NPC with the given name.
   *
   * @param inName the name of the monster
   */
  public NPC(String inName)
  {
    super(inName, TYPE);
  }

  protected NPC(Type<? extends NPC> inType)
  {
    super(inType);
  }

  protected NPC(String inName, Type<? extends NPC> inType)
  {
    super(inName, inType);
  }

  /** The type of this entry. */
  public static final Type<NPC> TYPE =
    new Type<NPC>(NPC.class, BaseMonster.TYPE);

  /** The type of the base entry to this entry. */
  public static final BaseType<BaseMonster> BASE_TYPE = BaseMonster.TYPE;

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The gender of the npc. */
  protected Gender m_gender = Gender.UNKNOWN;

  /** A special name for the npc, if any. */
  protected Optional<String> m_givenName = Optional.absent();

  /** The individual levels gained, in order. */
  protected List<Level> m_levels = new ArrayList<>();

  /** The religion or patron deity of the npc. */
  protected Optional<String> m_religion = Optional.absent();

  protected Optional<String> m_height = Optional.absent();
  protected Optional<String> m_weight = Optional.absent();
  protected Optional<String> m_looks = Optional.absent();

  /*
  @Override
  public String dmName()
  {
    if(m_givenName.isPresent())
      return m_givenName.get() + " - " + super.dmName();

    return super.dmName();
  }
  */

  public List<Level> getLevels()
  {
    return Collections.unmodifiableList(m_levels);
  }

  private SortedMultiset<String> cumulatedLevels()
  {
    SortedMultiset<String> levels = TreeMultiset.create();
    for(Level level : m_levels)
      if(level.getBase().isPresent())
        levels.add(level.getBase().get().getName());
      else
        levels.add(level.getAbbreviation());

    return levels;
  }

  public List<String> getCumulatedLevels()
  {
    SortedMultiset<String> levels = cumulatedLevels();

    List<String> result = new ArrayList<>();
    for(Multiset.Entry<String> entry : levels.entrySet())
      result.add(entry.getElement() + " " + entry.getCount());

    return result;
  }

  public int getEffectiveCharacterLevel()
  {
    int levels = m_levels.size();
    levels += getCombinedLevelAdjustment().getValue();
    return levels;
  }

  public Gender getGender()
  {
    return m_gender;
  }

  public Optional<String> getReligion()
  {
    return m_religion;
  }

  public Optional<String> getWeight()
  {
    return m_weight;
  }

  public Optional<String> getHeight()
  {
    return m_height;
  }

  public Optional<String> getLooks()
  {
    return m_looks;
  }

  @Override
  public Combination<Integer> getCombinedBaseAttack()
  {
    List<Combination<Integer>> combinations = new ArrayList<>();
    combinations.add(super.getCombinedBaseAttack());

    for(Multiset.Entry<String> entry : cumulatedLevels().entrySet())
    {
      BaseLevel level = (BaseLevel)DMADataFactory.get().getEntry
        (new EntryKey(entry.getElement(), BaseLevel.TYPE));

      int bonus = 0;
      if (level != null)
      {
        List<Integer> attacks = level.getBaseAttacks();
        for (int i = 0; i < entry.getCount(); i++)
          bonus += attacks.get(i);

        combinations.add(new Combination.Integer(level, bonus));
      }
    }

    return new Combination.Max<Integer>(this, combinations);
  }

  @Override
  public Annotated.Bonus getCombinedBaseFortitudeSave()
  {
    Annotated.Bonus save = super.getCombinedBaseFortitudeSave();

    for(Multiset.Entry<String> entry : cumulatedLevels().entrySet())
    {
      BaseLevel level = (BaseLevel)DMADataFactory.get().getEntry
        (new EntryKey(entry.getElement(), BaseLevel.TYPE));

      int bonus = 0;
      if (level != null)
      {
        List<Integer> saves = level.getFortitudeSaves();
        for (int i = 0; i < entry.getCount(); i++)
          bonus += saves.get(i);

        save.add(bonus, level.getName() + " " + entry.getCount());
      }
    }

    return save;
  }

  @Override
  public Annotated.Bonus getCombinedBaseReflexSave()
  {
    Annotated.Bonus save = super.getCombinedBaseReflexSave();

    for(Multiset.Entry<String> entry : cumulatedLevels().entrySet())
    {
      BaseLevel level = (BaseLevel)DMADataFactory.get().getEntry
        (new EntryKey(entry.getElement(), BaseLevel.TYPE));

      int bonus = 0;
      if (level != null)
      {
        List<Integer> saves = level.getReflexSaves();
        for (int i = 0; i < entry.getCount(); i++)
          bonus += saves.get(i);

        save.add(bonus, level.getName() + " " + entry.getCount());
      }
    }

    return save;
  }

  @Override
  public Annotated.Bonus getCombinedBaseWillSave()
  {
    Annotated.Bonus save = super.getCombinedBaseWillSave();

    for(Multiset.Entry<String> entry : cumulatedLevels().entrySet())
    {
      BaseLevel level = (BaseLevel)DMADataFactory.get().getEntry
        (new EntryKey(entry.getElement(), BaseLevel.TYPE));

      int bonus = 0;
      if (level != null)
      {
        List<Integer> saves = level.getWillSaves();
        for (int i = 0; i < entry.getCount(); i++)
          bonus += saves.get(i);

        save.add(bonus, level.getName() + " " + entry.getCount());
      }
    }

    return save;
  }

  @Override
  public int getMaxHP()
  {
    // Use the levels plus the max hp of the monster
    int hp = super.getMaxHP();

    for(Level level : m_levels)
    {
      hp += level.getHP();
      hp += getConstitutionModifier();
    }

    return hp;
  }

  /*
  @SuppressWarnings("unchecked")
  @Override
  protected <T extends Value<T>> void collect(String inName,
                                              Combined<T> ioCombined)
  {
    super.collect(inName, ioCombined);

    Multiset<String> levels = HashMultiset.create();
    for(Reference<BaseLevel> level : m_levels)
    {
      / *
      levels.add(level.getName());
      if(level.hasEntry())
         level.getEntry().collect(levels.count(level.getName()), inName,
                                  ioCombined,
                                  level.getName() + " "
                                  + levels.count(level.getName()));
                                  * /
    }

    switch(inName)
    {
      case "hit dice":
        / *
        for(Reference<BaseLevel> level : m_levels)
          if(level.hasEntry())
          {
            BaseLevel baseLevel = level.getEntry();
            ioCombined.addValue((T)baseLevel.getHitDie(), baseLevel,
                                baseLevel.getAbbreviation());
          }
          * /

        break;

      default:
        break;
    }
  }
  */

  /*
  @Override
  public int getLevel()
  {
    int totalAdjustment = 0;
    Combined<Union> adjustments = collect("level adjustment");
    for (Union adjustment : adjustments.valuesOnly())
      if(adjustment.get() instanceof Number)
        totalAdjustment += ((Number)adjustment.get()).get();

    return m_levels.size() + totalAdjustment;
  }
  */

  @Override
  public void set(Values inValues)
  {
    super.set(inValues);

    m_gender = inValues.use("gender", m_gender, Gender.PARSER);
    m_religion = inValues.use("religion", m_religion);
    m_weight = inValues.use("weight", m_weight);
    m_height = inValues.use("height", m_height);
    m_looks = inValues.use("looks", m_looks);
    m_levels = inValues.useEntries("level", m_levels,
                                   new NestedEntry.Creator<Level>()
    {
      @Override
      public Level create()
      {
        return new Level();
      }
    });
  }

  @Override
  public Message toProto()
  {
    NPCProto.Builder builder = NPCProto.newBuilder();

    builder.setBase((MonsterProto)super.toProto());

    if(m_gender != Gender.UNKNOWN)
      builder.setGender(m_gender.toProto());

    if(m_givenName.isPresent())
      builder.setGivenName(m_givenName.get());

    for(Level level : m_levels)
      builder.addLevel(level.toProto());

    if(m_religion.isPresent())
      builder.setReligion(m_religion.get());

    if(m_height.isPresent())
      builder.setHeight(m_height.get());

    if(m_weight.isPresent())
      builder.setWeight(m_weight.get());

    if(m_looks.isPresent())
      builder.setLooks(m_looks.get());

    NPCProto proto = builder.build();
    return proto;
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof NPCProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    NPCProto proto = (NPCProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.hasGender())
      m_gender = Gender.fromProto(proto.getGender());

    if(proto.hasGivenName())
      m_givenName = Optional.of(proto.getGivenName());

    for(LevelProto level : proto.getLevelList())
        m_levels.add(Level.fromProto(level));

    if(proto.hasReligion())
      m_religion = Optional.of(proto.getReligion());

    if(proto.hasHeight())
      m_height = Optional.of(proto.getHeight());

    if(proto.hasWeight())
      m_weight = Optional.of(proto.getWeight());

    if(proto.hasLooks())
      m_looks = Optional.of(proto.getLooks());
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(NPCProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }
}
