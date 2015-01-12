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


package net.ixitxachitls.dma.values;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Entries.BaseMagicProto;

/**
 * A named modifier.
 *
 * @file NamedModifier.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class NamedModifier extends Value.Arithmetic<BaseMagicProto.Modifier>
{
  /**
   * Create the name modifier.
   *
   * @param inType the type of modifier
   * @param inModifier the modifier itself
   */
  public NamedModifier(ModifierType inType, Modifier inModifier)
  {
    m_type = inType;
    m_modifier = inModifier;
  }

  /** The type of the modifier. */
  private final ModifierType m_type;

  /** The modifier value. */
  private final Modifier m_modifier;

  /** The parser for a named modifier. */
  public final static Parser<NamedModifier> PARSER =
    new Parser<NamedModifier>(2)
    {
      @Override
      protected Optional<NamedModifier> doParse(String inType,
                                               String inModifier)
      {
        Optional<ModifierType> type = ModifierType.PARSER.parse(inType);
        Optional<Modifier> modifier = Modifier.PARSER.parse(inModifier);

        if(type.isPresent() && modifier.isPresent())
          return Optional.of(new NamedModifier(type.get(),
                                               modifier.get()));

        return Optional.absent();
      }
    };

  @Override
  public String toString()
  {
    return m_type + " " + m_modifier;
  }

  /**
   * Get the modifier type.
   *
   * @return the type
   */
  public ModifierType getType()
  {
    return m_type;
  }

  /**
   * Get the modifier value.
   *
   * @return the value
   */
  public Modifier getModifier()
  {
    return m_modifier;
  }

  /**
   * Create a named modifier from the given proto.
   *
   * @param inProto the proto to generate from
   * @return the newly created named modifier
   */
  public static NamedModifier fromProto(BaseMagicProto.Modifier inProto)
  {
    return new NamedModifier(ModifierType.fromProto(inProto.getType()),
                             Modifier.fromProto(inProto.getModifier()));
  }

  @Override
  public Arithmetic<BaseMagicProto.Modifier> add(Arithmetic inValue)
  {
    if(!canAdd(inValue))
      return this;

    NamedModifier value = (NamedModifier)inValue;
    return new NamedModifier(m_type,
                               (Modifier)
                               m_modifier.add(value.getModifier()));
  }

  @Override
  public boolean canAdd(Arithmetic inValue)
  {
    if(!(inValue instanceof NamedModifier))
      return false;

    NamedModifier value = (NamedModifier)inValue;
    return m_type == value.m_type;
  }

  @Override
  public Arithmetic<BaseMagicProto.Modifier> multiply(int inFactor)
  {
    return new NamedModifier(m_type,
                             (Modifier)m_modifier.multiply(inFactor));
  }

  @Override
  public BaseMagicProto.Modifier toProto()
  {
    return BaseMagicProto.Modifier.newBuilder()
      .setType(m_type.toProto())
      .setModifier(m_modifier.toProto())
      .build();
  }
}
