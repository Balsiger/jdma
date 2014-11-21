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

import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.proto.Entries.SkillProto;

/**
 * An actual skill a monster or character has.
 *
 * @file   Skill.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Skill extends NestedEntry
{
  public Skill()
  {
  }

  protected String m_name;
  protected int m_ranks;

  @Override
  public void set(Values inValues)
  {
    m_name = inValues.use("skill.name", m_name);
    m_ranks = inValues.use("skill.ranks", m_ranks);
  }

  public static Skill fromProto(SkillProto inProto)
  {
    String name = inProto.getName();
    int ranks = 0;
    if(inProto.hasRanks())
      ranks = inProto.getRanks();

    Skill skill = new Skill();
    skill.m_name = name;
    skill.m_ranks = ranks;

    return skill;
  }

  public SkillProto toProto()
  {
    SkillProto.Builder builder = SkillProto.newBuilder();

    builder.setName(m_name);
    builder.setRanks(m_ranks);

    return builder.build();
  }
}
