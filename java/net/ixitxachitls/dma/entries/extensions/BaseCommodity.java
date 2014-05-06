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
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseCommodityProto;
import net.ixitxachitls.dma.values.Area;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.NewDistance;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the commodity extension for all the entries.
 *
 * @file          BaseCommodity.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public class BaseCommodity extends ValueGroup
{
  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inName the name of the extension
   */
  public BaseCommodity(BaseItem inItem)
  {
    m_item = inItem;
  }

  /** The item this commodity belongs to. */
  protected BaseItem m_item;

  /** The area for this commodity. */
  protected Optional<Area> m_area = Optional.absent();

  /** The length of this commodity. */
  protected Optional<NewDistance> m_length = Optional.absent();

  /**
   * Get the area value.
   *
   * @return      the area
   */
  public Optional<Area> getArea()
  {
    return m_area;
  }

  /**
   * Get the combined area of this commodity, including values of bases.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<Area> getCombinedArea()
  {
    if(m_area.isPresent())
      return new Combination.Addable<Area>(m_item, m_area.get());

    List<Combination<Area>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseCommodity armor = ((BaseItem)entry).getCommodity();
        combinations.add(armor.getCombinedArea());
      }

    return new Combination.Addable<Area>(m_item, combinations);
  }

  /**
   * Get the length value.
   *
   * @return      the length
   */
  public Optional<NewDistance> getLength()
  {
    return m_length;
  }

  /**
   * Get the combined length of this commodity, including values of bases.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewDistance> getCombinedLength()
  {
    if(m_length.isPresent())
      return new Combination.Addable<NewDistance>(m_item, m_length.get());

    List<Combination<NewDistance>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseCommodity armor = ((BaseItem)entry).getCommodity();
        combinations.add(armor.getCombinedLength());
      }

    return new Combination.Addable<NewDistance>(m_item, combinations);
  }

  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    if(m_area.isPresent())
      values.put(Index.Path.AREAS, m_area.toString());
    if(m_length.isPresent())
      values.put(Index.Path.LENGTHS, m_length.toString());

    return values;
  }

  @Override
  public void set(Values inValues)
  {
    m_area = inValues.use("commodity.area", m_area, Area.PARSER);
    m_length = inValues.use("commodity.length", m_length, NewDistance.PARSER);
  }

  @Override
  public Message toProto()
  {
    BaseCommodityProto.Builder builder = BaseCommodityProto.newBuilder();

    if(m_area.isPresent())
      builder.setArea(m_area.get().toProto());
    if(m_length.isPresent())
      builder.setLength(m_length.get().toProto());

    return builder.build();
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseCommodityProto))
    {
      Log.warning("cannot parse base commodity proto " + inProto.getClass());
      return;
    }

    BaseCommodityProto proto = (BaseCommodityProto)inProto;

    if(proto.hasArea())
      m_area = Optional.of(Area.fromProto(proto.getArea()));
    if(proto.hasLength())
      m_length = Optional.of(NewDistance.fromProto(proto.getLength()));
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
   * Check whether any commodity values are defined.
   *
   * @return true if commodity values are defined, false if not
   */
  public boolean hasValues()
  {
    return m_area.isPresent() || m_length.isPresent();
  }
}
