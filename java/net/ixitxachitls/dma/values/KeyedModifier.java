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

import net.ixitxachitls.dma.proto.Entries.BaseQualityProto;

/**
 * A modifier to an ability.
 *
 * @file   AbilityModifier.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */
public class KeyedModifier extends Value<BaseQualityProto.KeyedModifier>
{
  /** Create the modifier.
   *
   * @param inKey the key
   * @param inModifier the modifier
   */
  public KeyedModifier(String inKey, Modifier inModifier)
  {
    m_key = inKey;
    m_modifier = inModifier;
  }

  /** The modifier key. */
  private final String m_key;

  /** The modifier value. */
  private final Modifier m_modifier;

  /** The parser for keyed modifier. */
  public static final Parser<KeyedModifier> PARSER =
    new Parser<KeyedModifier>(2)
    {
      @Override
      public Optional<KeyedModifier> doParse(String inKey,
                                             String inModifier)
      {
        Optional<Modifier> modifier = Modifier.PARSER.parse(inModifier);
        if(!modifier.isPresent())
          return Optional.absent();

        return Optional.of(new KeyedModifier(inKey, modifier.get()));
      }
    };

  /**
   * Get the key.
   *
   * @return the key
   */
  public String getKey()
  {
    return m_key;
  }

  /**
   * Get the modifier.
   *
   * @return the modifier
   */
  public Modifier getModifier()
  {
    return m_modifier;
  }

  @Override
  public String toString()
  {
    return m_key + " " + m_modifier;
  }

  @Override
  public BaseQualityProto.KeyedModifier toProto()
  {
    return BaseQualityProto.KeyedModifier.newBuilder()
      .setKey(m_key)
      .setModifier(m_modifier.toProto())
      .build();
  }

  /**
   * Create the modifier from the given proto.
   *
   * @param inProto the proto to create from
   * @return the created value
   */
  public static KeyedModifier fromProto(BaseQualityProto.KeyedModifier inProto)
  {
    return new KeyedModifier(inProto.getKey(),
                               Modifier.fromProto(inProto.getModifier()));
  }
}
