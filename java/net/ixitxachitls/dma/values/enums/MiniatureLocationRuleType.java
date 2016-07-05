/*******************************************************************************
 * Copyright (c) 2002-2016 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.values.enums;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Entries;

/**
 * The key for a rule for miniature locations.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public enum MiniatureLocationRuleType implements Named
{
  TYPE("type", Entries.BaseCharacterProto.MiniatureLocation.Rule.Type.TYPE),
  ORIGIN("origin",
         Entries.BaseCharacterProto.MiniatureLocation.Rule.Type.ORIGIN),
  SUBTYPE("subtype",
          Entries.BaseCharacterProto.MiniatureLocation.Rule.Type.SUBTYPE),
  CLASS("class",
        Entries.BaseCharacterProto.MiniatureLocation.Rule.Type.CLASS),
  SIZE("size",
        Entries.BaseCharacterProto.MiniatureLocation.Rule.Type.SIZE);

  private String m_name;
  private Entries.BaseCharacterProto.MiniatureLocation.Rule.Type m_proto;

  private MiniatureLocationRuleType(
      String inName,
      Entries.BaseCharacterProto.MiniatureLocation.Rule.Type inProto)
  {
    m_name = inName;
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

  public Entries.BaseCharacterProto.MiniatureLocation.Rule.Type toProto()
  {
    return m_proto;
  }

  public static MiniatureLocationRuleType fromProto(
      Entries.BaseCharacterProto.MiniatureLocation.Rule.Type inRule)
  {
    for(MiniatureLocationRuleType rule : values())
      if(rule.m_proto == inRule)
        return rule;

    throw new IllegalStateException("invalid proto rule: " + inRule);
  }

  public static Optional<MiniatureLocationRuleType> fromString(String inText)
  {
    for(MiniatureLocationRuleType key : values())
      if(key.m_name.equalsIgnoreCase(inText))
        return Optional.of(key);

    return Optional.absent();
  }

  public static List<String> names()
  {
    List<String> names = new ArrayList<>();

    for(MiniatureLocationRuleType key : values())
      names.add(key.getName());

    return names;
  }
}
