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
import net.ixitxachitls.dma.values.enums.Ability;

/**
 * A modifier to an ability.
 *
 * @file   AbilityModifier.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */
public class AbilityModifier extends Value<BaseQualityProto.AbilityModifier>
{
  /**
   * Create the ability modifier.
   *
   * @param inAbility the ability being modified
   * @param inModifier the modifier to the ability
   */
  public AbilityModifier(Ability inAbility, Modifier inModifier)
  {
    m_ability = inAbility;
    m_modifier = inModifier;
  }

  /** The ability being modified. */
  private final Ability m_ability;

  /** The modifier to the ability. */
  private final Modifier m_modifier;

  /** The parser for ability modifiers. */
  public static final Parser<AbilityModifier> PARSER =
    new Parser<AbilityModifier>(2)
    {
      @Override
      public Optional<AbilityModifier> doParse(String inAbility,
                                               String inModifier)
      {
        Optional<Ability> ability = Ability.fromString(inAbility);
        Optional<Modifier> modifier = Modifier.PARSER.parse(inModifier);
        if(!ability.isPresent() || !modifier.isPresent())
          return Optional.absent();

        return Optional.of(new AbilityModifier(ability.get(), modifier.get()));
      }
    };

  /**
   * Get the ability being modified.
   *
   * @return the ability
   */
  public Ability getAbility()
  {
    return m_ability;
  }

  /**
   * Get the modifier to the ability.
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
    return m_ability + " " + m_modifier;
  }

  @Override
  public BaseQualityProto.AbilityModifier toProto()
  {
    return BaseQualityProto.AbilityModifier.newBuilder()
      .setAbility(m_ability.toProto())
      .setModifier(m_modifier.toProto())
      .build();
  }

  /**
   * Convert the given proto into its respective ability modifier.
   *
   * @param inProto the proto to convert
   * @return the converted ability modifier
   */
  public static AbilityModifier
  fromProto(BaseQualityProto.AbilityModifier inProto)
  {
    return new AbilityModifier(Ability.fromProto(inProto.getAbility()),
                               Modifier.fromProto(inProto.getModifier()));
  }
}
