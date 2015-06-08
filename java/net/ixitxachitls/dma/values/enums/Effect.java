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


package net.ixitxachitls.dma.values.enums;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Parser;

/**
 * An effect.
 *
 * @file Effect.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Effect
{
  /**
   * Create the effect.
   *
   * @param inAffects what is affected
   * @param inName the name of the effect
   * @param inModifier the modifier of what is affected
   * @param inText a description of the effect
   */
  public Effect(Affects inAffects, Optional<String> inName,
                Optional<Modifier> inModifier, Optional<String> inText)
  {
    m_affects = inAffects;
    m_name = inName;
    m_modifier = inModifier;
    m_text = inText;
  }

  /** What is affected by the effect. */
  private final Affects m_affects;

  /** The name of the effect. */
  private final Optional<String> m_name;

  /** The modifier of the effect. */
  private final Optional<Modifier> m_modifier;

  /** The effect description. */
  private final Optional<String> m_text;

  /** The parser for effects. */
  public static final Parser<Effect> PARSER =
      new Parser<Effect>(4)
      {
        @Override
        public Optional<Effect> doParse
            (String inAffects, String inName, String inModifier, String inText)
        {
          Optional<Affects> affects = Affects.fromString(inAffects);
          if(!affects.isPresent())
            return Optional.absent();

          Optional<String> name =
              inName.isEmpty() ? Optional.<String>absent() : Optional.of(inName);
          Optional<Modifier> modifier =
              Modifier.PARSER.parse(inModifier);
          Optional<String> text = inText.isEmpty()
              ? Optional.<String>absent() : Optional.of(inText);

          return Optional.of(new Effect(affects.get(), name, modifier, text));
        }
      };

  /**
   * Get what is affected by the effect.
   *
   * @return what's affected
   */
  public Affects getAffects()
  {
    return m_affects;
  }

  /**
   * Get the effect name.
   *
   * @return the name
   */
  public Optional<String> getName()
  {
    return m_name;
  }

  /**
   * Get the modifier.
   *
   * @return the modifier
   */
  public Optional<Modifier> getModifier()
  {
    return m_modifier;
  }

  /**
   * Get the effect description.
   *
   * @return the description
   */
  public Optional<String> getText()
  {
    return m_text;
  }

  @Override
  public String toString()
  {
    return m_affects
      + (m_name.isPresent() ? " " + m_name.get() : "")
      + (m_modifier.isPresent() ? " " + m_modifier.get() : "")
      + (m_text.isPresent() ? " " + m_text.get() : "");
  }
}
