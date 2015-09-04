/*******************************************************************************
 * Copyright (c) 2002-2015 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.values;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.*;
import net.ixitxachitls.dma.proto.Values;

/**
 * A name with a modifier.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class NameAndModifier
    extends Value.Arithmetic<Values.NameAndModifierProto>
{
  public NameAndModifier(String inName, Modifier inModifier)
  {
    m_name = inName;
    m_modifier = inModifier;
  }

  private final String m_name;
  private final Modifier m_modifier;

  public static final Parser<NameAndModifier> PARSER =
      new Parser<NameAndModifier>(2)
      {
        @Override
        public Optional<NameAndModifier> doParse(String inName,
                                                 String inModifier)
        {
          Optional<Modifier> modifier = Modifier.PARSER.parse(inModifier);
          if(modifier.isPresent())
            return Optional.of(new NameAndModifier(inName, modifier.get()));

          return Optional.absent();
        }
      };

  @Override
  public Arithmetic<Values.NameAndModifierProto> add(
      Arithmetic<Values.NameAndModifierProto> inValue)
  {
    if(!canAdd(inValue))
      return this;

    return new NameAndModifier(
        m_name,
        (Modifier)m_modifier.add(((NameAndModifier)inValue).m_modifier));
  }

  public boolean hasName(String inName)
  {
    return m_name.equalsIgnoreCase(inName);
  }

  public String getName()
  {
    return m_name;
  }

  public Modifier getModifier()
  {
    return m_modifier;
  }


  @Override
  public boolean canAdd(Arithmetic<Values.NameAndModifierProto> inValue)
  {
    if(!(inValue instanceof NameAndModifier))
      return false;

    return m_name.equals(((NameAndModifier)inValue).m_name)
        && m_modifier.canAdd(((NameAndModifier)inValue).m_modifier);
  }

  @Override
  public Arithmetic<Values.NameAndModifierProto> multiply(int inFactor)
  {
    return new NameAndModifier(m_name,
                               (Modifier)m_modifier.multiply(inFactor));
  }

  @Override
  public Values.NameAndModifierProto toProto()
  {
    return Values.NameAndModifierProto.newBuilder()
        .setName(m_name)
        .setModifier(m_modifier.toProto())
        .build();
  }

  public static NameAndModifier fromProto(Values.NameAndModifierProto inProto)
  {
    return new NameAndModifier(inProto.getName(),
                               Modifier.fromProto(inProto.getModifier()));
  }
}
