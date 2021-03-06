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

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries;
import net.ixitxachitls.dma.proto.Entries.BaseMiniatureProto;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Alignment;
import net.ixitxachitls.dma.values.enums.Size;
import net.ixitxachitls.util.logging.Log;

/**
 * A miniature for the D&D game.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class BaseMiniature extends BaseEntry
{
  protected BaseMiniature()
  {
    super(TYPE);
  }

  public BaseMiniature(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final BaseType<BaseMiniature> TYPE =
      new BaseType.Builder<>(BaseMiniature.class)
          .index(new Index.Builder(Index.Path.SETS, "Sets", BaseMiniature.TYPE)
                     .images().paginated().sort("number").build())
          .build();

  protected Optional<String> m_set = Optional.absent();
  protected Optional<Integer> m_number = Optional.absent();
  protected Optional<String> m_origin = Optional.absent();
  protected Optional<String> m_miniatureType = Optional.absent();
  protected List<String> m_subtypes = new ArrayList<>();
  protected Alignment m_alignment = Alignment.UNKNOWN;
  protected List<String> m_classes = new ArrayList<>();
  protected Optional<Integer> m_level = Optional.absent();
  protected Optional<String> m_role = Optional.absent();
  protected Size m_size = Size.UNKNOWN;

  public Optional<String> getSet()
  {
    return m_set;
  }

  public Optional<Integer> getNumber()
  {
    return m_number;
  }

  public Optional<String> getOrigin()
  {
    return m_origin;
  }

  public Optional<String> getMiniatureType()
  {
    return m_miniatureType;
  }

  public List<String> getSubtypes()
  {
    return m_subtypes;
  }

  public Alignment getAlignment()
  {
    return m_alignment;
  }

  public List<String> getClasses()
  {
    return m_classes;
  }

  public Optional<Integer> getLevel()
  {
    return m_level;
  }

  public Optional<String> getRole()
  {
    return m_role;
  }

  public Size getSize()
  {
    return m_size;
  }

  @Override
  public Message toProto()
  {
    BaseMiniatureProto.Builder builder = BaseMiniatureProto.newBuilder();

    builder.setBase((Entries.BaseEntryProto)super.toProto());

    if(m_set.isPresent())
      builder.setSet(m_set.get());
    if(m_number.isPresent())
      builder.setNumber(m_number.get());
    if(m_origin.isPresent())
      builder.setOrigin(m_origin.get());
    if(m_miniatureType.isPresent())
      builder.setType(m_miniatureType.get());
    builder.addAllSubtype(m_subtypes);
    builder.setAlignment(m_alignment.toProto());
    builder.addAllCharacterClass(m_classes);
    if(m_level.isPresent())
      builder.setLevel(m_level.get());
    if(m_role.isPresent())
      builder.setRole(m_role.get());
    builder.setSize(m_size.toProto());


    return builder.build();
  }

  @Override
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    m_set = inValues.use("set", m_set);
    m_number = inValues.use("number", m_number, Value.INTEGER_PARSER);
    m_origin = inValues.use("origin", m_origin);
    m_miniatureType = inValues.use("miniature_type", getMiniatureType());
    m_subtypes = inValues.use("subtype", m_subtypes);
    m_alignment = inValues.use("alignment", m_alignment, Alignment.PARSER);
    m_classes = inValues.use("class", m_classes);
    m_level = inValues.use("level", m_level, Value.INTEGER_PARSER);
    m_role = inValues.use("role", m_role);
    m_size = inValues.use("size", m_size, Size.PARSER);
  }

  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseMiniatureProto))
    {
      Log.warning("cannot parse proto " + inProto.getClass());
      return;
    }

    BaseMiniatureProto proto = (BaseMiniatureProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.hasSet())
      m_set = Optional.of(proto.getSet());
    if(proto.hasNumber())
      m_number = Optional.of(proto.getNumber());
    if(proto.hasOrigin())
      m_origin = Optional.of(proto.getOrigin());
    if(proto.hasType())
      m_miniatureType = Optional.of(proto.getType());
    m_subtypes = proto.getSubtypeList();
    m_alignment = Alignment.fromProto(proto.getAlignment());
    m_classes = proto.getCharacterClassList();
    if(proto.hasLevel())
      m_level = Optional.of(proto.getLevel());
    if(proto.hasRole())
      m_role = Optional.of(proto.getRole());
    m_size = Size.fromProto(proto.getSize());
  }

  @Override
  protected Message defaultProto()
  {
    return BaseMiniatureProto.getDefaultInstance();
  }

  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    if(m_set.isPresent())
      values.put(Index.Path.SETS, m_set.get());
    if(m_origin.isPresent())
      values.put(Index.Path.ORIGINS, m_origin.get());
    if(m_miniatureType.isPresent())
      values.put(Index.Path.MINIATURE_TYPES, m_miniatureType.get());
    values.putAll(Index.Path.SUBTYPES, m_subtypes);
    values.putAll(Index.Path.CLASSES, m_classes);
    if(m_role.isPresent())
      values.put(Index.Path.ROLES, m_role.get());

    return values;
  }

  public Map<String, Object> collectSearchables() {
    Map<String, Object> searchables = super.collectSearchables();

    searchables.put("number", m_number.isPresent() ? m_number.get() : 0);
    return searchables;
  }

  @Override
  public String getImageSearchQuery()
  {
    if(m_set.isPresent())
      return super.getImageSearchQuery() + " " + m_set.get();

    return super.getImageSearchQuery();
  }

  public Map<String, List<String>> owners()
  {
    return Multimaps.asMap(DMADataFactory.get().getOwners(Miniature.TYPE,
                                                          this.getName()));
  }
}
