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
import net.ixitxachitls.dma.proto.Entries.LevelProto;

/**
 * An actual character level.
 *
 * @file   Level.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Level extends NestedEntry
{
  /**
   * Create a default, unnamed level.
   */
  public Level()
  {
  }

  /** The hit points rolled for this level. */
  private int m_hp = 0;

  private Optional<Optional<BaseLevel>> m_base = Optional.absent();

  public int getHP()
  {
    return m_hp;
  }

  public Optional<BaseLevel> getBase()
  {
    if(!m_base.isPresent())
    {
      if(m_name.isPresent())
        m_base = Optional.of(Optional.fromNullable
                             ((BaseLevel)DMADataFactory.get().getEntry
                              (new EntryKey(m_name.get(), BaseLevel.TYPE))));
      else
        return Optional.absent();
    }

    return m_base.get();
  }

  public String getAbbreviation()
  {
    if(getBase().isPresent())
    {
      if(getBase().get().getAbbreviation().isPresent())
        return getBase().get().getAbbreviation().get();
      else
        return getBase().get().getName();
    }

    if(m_name.isPresent())
      return "(" + m_name.get() + ")";

    return "(unknown)";
  }

  public static List<String> getAvailableLevels()
  {
    return DMADataFactory.get().getIDs(BaseLevel.TYPE, null);
  }


  @Override
  public void set(ValueGroup.Values inValues)
  {
    m_name = inValues.use("name", m_name);
    m_hp = inValues.use("hp", m_hp);
  }

  public LevelProto toProto()
  {
    LevelProto.Builder builder = LevelProto.newBuilder();

    if(m_name.isPresent())
      builder.setName(m_name.get());
    else
      builder.setName("unknown");
    builder.setHp(m_hp);

    LevelProto proto = builder.build();
    return proto;
  }

  public static Level fromProto(LevelProto inProto)
  {
    Level level = new Level();
    level.m_name = Optional.of(inProto.getName());
    level.m_hp = inProto.getHp();

    return level;
  }
}
