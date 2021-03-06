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

import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.SkillProto;
import net.ixitxachitls.dma.values.Annotated;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Ability;

/**
 * An actual skill a monster or character has.
 *
 * @file   Skill.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Skill extends NestedEntry
{
  /** Create the skill. */
  public Skill()
  {
    // nothing to do
  }

  public Skill(String inName)
  {
    m_name = Optional.of(inName);
  }

  public static final Creator<Skill> CREATOR =
      new NestedEntry.Creator<Skill>()
      {
        @Override
        public Skill create()
        {
          return new Skill();
        }
      };

  /** The number of ranks in the skill .*/
  protected int m_ranks;

  /** The base level to this level. */
  private Optional<Optional<BaseSkill>> m_base = Optional.absent();

  public boolean isUntrained()
  {
    Optional<BaseSkill> base = getBase();
    if(!base.isPresent())
      return false;

    return base.get().isUntrained();
  }

  public static List<String> getAvailableSkills()
  {
    return DMADataFactory.get().getIDs(BaseSkill.TYPE,
                                       Optional.<EntryKey>absent());
  }

  /**
   * Get the base level to this level.
   *
   * @return the base skill
   */
  public Optional<BaseSkill> getBase()
  {
    if(!m_base.isPresent() && m_name.isPresent())
      m_base = Optional.of(DMADataFactory.get().<BaseSkill>getEntry
            (new EntryKey(m_name.get(), BaseSkill.TYPE)));

    return m_base.get();
  }

  public int getRanks()
  {
    return m_ranks;
  }

  public Ability getAbility()
  {
    Optional<BaseSkill> base = getBase();
    if(!base.isPresent())
      return Ability.UNKNOWN;

    return base.get().getAbility();
  }

  public String getName()
  {
    if(m_name.isPresent())
      return m_name.get();

    return "<unknown>";
  }

  @Override
  public void set(Values inValues)
  {
    m_name = inValues.use("name", m_name);
    Optional<Integer> ranks = inValues.use("ranks", m_ranks);
    if(ranks.isPresent())
      m_ranks = ranks.get();
  }

  /**
   * Create a skill from the given proto.
   *
   * @param inProto the proto to create from
   * @return the created, new skill
   */
  public static Skill fromProto(SkillProto inProto)
  {
    String name = inProto.getName();
    int ranks = 0;
    if(inProto.hasRanks())
      ranks = inProto.getRanks();

    Skill skill = new Skill();
    skill.m_name = Optional.of(name);
    skill.m_ranks = ranks;

    return skill;
  }

  /**
   * Convert the skill into a proto representation.
   *
   * @return the proto representation
   */
  public SkillProto toProto()
  {
    SkillProto.Builder builder = SkillProto.newBuilder();

    builder.setName(m_name.get());
    builder.setRanks(m_ranks);

    return builder.build();
  }

  public Annotated.Modifier annotatedModifier(BaseMonster inMonster) {
    Annotated.Modifier modifier =
        new Annotated.Modifier(Modifier.newBuilder().add(getRanks()).build(),
                               "ranks", true);

    modifier.add(Modifier.newBuilder()
                     .add(Modifier.Type.ABILITY,
                          inMonster.abilityModifier(getAbility()))
                     .build(), getAbility().getShort());
    modifier.add(inMonster.annotatedSkillModifier(getName()));

    return modifier;
  }

  public Annotated.Modifier annotatedModifier(Monster inMonster) {
    Annotated.Modifier modifier =
        new Annotated.Modifier(Modifier.newBuilder().add(getRanks()).build(),
                               "ranks", true);

    modifier.add(Modifier.newBuilder()
                     .add(Modifier.Type.ABILITY,
                          inMonster.abilityModifier(getAbility()))
                     .build(), getAbility().getShort());
    modifier.add(inMonster.annotatedSkillModifier(getName()));

    return modifier;
  }

  public Annotated.Modifier annotatedModifier(NPC inNPC) {
    return annotatedModifier((Monster) inNPC);
  }

  public int modifier(BaseMonster inMonster)
  {
    return getRanks() + inMonster.abilityModifier(getAbility())
        + inMonster.skillModifier(getName()).totalModifier();
  }

  public int modifier(Monster inMonster) {
    int ranks1 = getRanks();
    int ranks2 = inMonster.skillModifier(getName(), getAbility());
    return getRanks() + inMonster.abilityModifier(getAbility())
        + inMonster.skillModifier(getName()).totalModifier();
  }

  // Needed to call() it in soy (as Classes.getMethod() cannot handle derived
  // types).
  public int modifier(NPC inMonster) {
    return modifier((Monster)inMonster);
  }

  // Needed to be available in soy.
  public int modifier(Character inCharacter) {
    return modifier((Monster)inCharacter);
  }
}
