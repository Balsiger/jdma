/*******************************************************************************
 * Copyright (c) 2002-2015 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
 ******************************************************************************/

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.*;
import net.ixitxachitls.dma.proto.Entries;
import net.ixitxachitls.dma.values.Duration;
import net.ixitxachitls.dma.values.Evaluator;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.School;
import net.ixitxachitls.dma.values.enums.SpellClass;
import net.ixitxachitls.dma.values.enums.SpellComponent;
import net.ixitxachitls.dma.values.enums.SpellRange;
import net.ixitxachitls.dma.values.enums.Subschool;

/**
 * A representation of a specific spell being cast.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Spell extends NestedEntry
{
  public Spell(String inName, Level inLevel, int inBaseDC)
  {
    m_name = inName;
    m_level = inLevel;
    m_baseDC = inBaseDC;
  }

  private final String m_name;
  private final Level m_level;
  private final int m_baseDC;

  /** The base spell to this spell. */
  private Optional<Optional<BaseSpell>> m_base = Optional.absent();

  @Override
  public void set(Values inValues)
  {
    throw new UnsupportedOperationException("Not yet implemented!!");
  }

  public Optional<BaseSpell> getBase()
  {
    if(!m_base.isPresent())
      m_base = Optional.of(DMADataFactory.get().<BaseSpell>getEntry(
          new EntryKey(m_name, BaseSpell.TYPE)));

    return m_base.get();
  }

  public String getName()
  {
    if(getBase().isPresent())
      return getBase().get().getName();

    return m_name;
  }

  public BaseSpell.Level level()
  {
    if (getBase().isPresent())
      for(BaseSpell.Level level : getBase().get().getLevels())
        if(level.getSpellClass().getName().equalsIgnoreCase(m_level.getName()))
          return level;

    return new BaseSpell.Level(SpellClass.UNKNOWN, -1);
  }

  public School school()
  {
    if(getBase().isPresent())
      return getBase().get().getSchool();

    return School.UNKNOWN;
  }

  public List<Subschool> subschools()
  {
    if(getBase().isPresent())
      return getBase().get().getSubschools();

    return new ArrayList<>();
  }

  public List<SpellComponent> components()
  {
    if(getBase().isPresent())
      return getBase().get().getComponents();

    return new ArrayList<>();
  }

  public Optional<Duration> castingTime()
  {
    if(getBase().isPresent())
      return getBase().get().getCastingTime();

    return Optional.absent();
  }

  public SpellRange range()
  {
    if(getBase().isPresent())
      return getBase().get().getRange();

    return SpellRange.UNKNOWN;
  }

  public Optional<String> target()
  {
    if(getBase().isPresent())
      return getBase().get().getTarget();

    return Optional.absent();
  }

  public Optional<BaseSpell.Duration> duration()
  {
    if(getBase().isPresent())
      return getBase().get().getDuration();

    return Optional.absent();
  }

  public int levelCount()
  {
    if(getBase().isPresent())
      for(BaseSpell.Level level : getBase().get().getLevels())
        if(level.getSpellClass().toString().equals(m_level.getName()))
          return level.getLevel();

    return 0;
  }

  public Optional<String> spellResistance()
  {
    if(getBase().isPresent())
      return getBase().get().getResistance();

    return Optional.absent();
  }

  public int dc()
  {
    return m_baseDC + levelCount();
  }

  public String shortDescription()
  {
    if(getBase().isPresent())
      return new Evaluator(ImmutableMap.<String, Object>builder()
                               .put("level", "" + levelCount())
                               .put("dc", "" + dc())
                               .build())
          .evaluate(getBase().get().getShortDescription());

    return "";
  }
}
