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

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.FeatProto;

/**
 * An actual feat.
 *
 * @file   feat.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Feat extends NestedEntry
{
  /**
   * Create a default, unnamed feat.
   */
  public Feat()
  {
  }

  private Optional<String> m_qualifier = Optional.absent();

  private Optional<Optional<BaseFeat>> m_base = Optional.absent();

  public Optional<BaseFeat> getBase()
  {
    if(!m_base.isPresent())
    {
      if(m_name.isPresent())
        m_base = Optional.of(Optional.fromNullable
                             ((BaseFeat)DMADataFactory.get().getEntry
                              (new EntryKey(m_name.get(), BaseFeat.TYPE))));
      else
        return Optional.absent();
    }

    return m_base.get();
  }

  public Optional<String> getQualifier()
  {
    return m_qualifier;
  }

  @Override
  public void set(ValueGroup.Values inValues)
  {
    m_name = inValues.use("name", m_name);
    m_qualifier = inValues.use("qualifier", m_qualifier);
  }

  public FeatProto toProto()
  {
    FeatProto.Builder builder = FeatProto.newBuilder();

    if(m_name.isPresent())
      builder.setName(m_name.get());
    else
      builder.setName("unknown");

    if(m_qualifier.isPresent())
      builder.setQualifier(m_qualifier.get());

    FeatProto proto = builder.build();
    return proto;
  }

  public static Feat fromProto(FeatProto inProto)
  {
    Feat feat = new Feat();
    feat.m_name = Optional.of(inProto.getName());
    if(inProto.hasQualifier())
      feat.m_qualifier = Optional.of(inProto.getQualifier());

    return feat;
  }
}
