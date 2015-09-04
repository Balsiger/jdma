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

import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.proto.Values;
import net.ixitxachitls.util.CommandLineParser;

/**
 * A condition the limits the application of another value or entry.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Condition extends Value.Arithmetic<Values.ConditionProto>
{
  public Condition(String inGeneric, WeaponStyle inStyle)
  {
    m_generic = inGeneric;
    m_style = inStyle;
  }

  private String m_generic;
  private WeaponStyle m_style;

  /** The parser for conditions. */
  public static final Parser<Condition> PARSER =
      new Parser<Condition>(2)
      {
        @Override
        public Optional<Condition> doParse(String inGeneric,
                                           String inWeaponStyle)
        {
          Optional<WeaponStyle> style = WeaponStyle.fromString(inWeaponStyle);
          if(!style.isPresent())
            return Optional.absent();

          return Optional.of(new Condition(inGeneric, style.get()));
        }
      };

  public String getGeneric()
  {
    return m_generic;
  }

  public WeaponStyle getWeaponStyle()
  {
    return m_style;
  }

  public Optional<Boolean> check()
  {
    if(m_style != WeaponStyle.UNKNOWN)
      return Optional.absent();

    if(m_generic.isEmpty())
      return Optional.of(true);

    return Optional.absent();
  }

  public Optional<Boolean> check(Item inItem)
  {
    if(!m_generic.isEmpty())
      return Optional.absent();

    if(m_style == WeaponStyle.UNKNOWN)
      return Optional.of(true);

    if(!inItem.isWeapon())
      return Optional.absent();

    Optional<WeaponStyle> style = inItem.getCombinedWeaponStyle().get();
    if(style.isPresent() && style.get() == m_style)
      return Optional.of(true);

    return Optional.of(false);
  }

  @Override
  public Arithmetic<Values.ConditionProto> add(
      Arithmetic<Values.ConditionProto> inValue)
  {
    Condition value = (Condition)inValue;
    return new Condition(add(m_generic, value.m_generic),
                         add(m_style, value.m_style));
  }

  @Override
  public boolean canAdd(Arithmetic<Values.ConditionProto> inValue)
  {
    return inValue instanceof Condition
        && (((Condition)inValue).m_style == WeaponStyle.UNKNOWN
            || m_style == ((Condition)inValue).m_style);
  }

  @Override
  public Arithmetic<Values.ConditionProto> multiply(int inFactor)
  {
    return this;
  }

  public static Condition fromProto(Values.ConditionProto inProto)
  {
    return new Condition(inProto.getGeneric(),
                         WeaponStyle.fromProto(inProto.getWeaponStyle()));
  }

  @Override
  public Values.ConditionProto toProto()
  {
    Values.ConditionProto.Builder proto = Values.ConditionProto.newBuilder();

    if(!m_generic.isEmpty())
      proto.setGeneric(m_generic);

    if(m_style != WeaponStyle.UNKNOWN)
      proto.setWeaponStyle(m_style.toProto());

    return proto.build();
  }

  private String add(String inFirst, String inSecond)
  {
    if(inFirst.isEmpty())
      return inSecond;

    if(inSecond.isEmpty())
      return inFirst;

    return inFirst + " " + inSecond;
  }

  private WeaponStyle add(WeaponStyle inFirst, WeaponStyle inSecond)
  {
    if(inFirst == inSecond)
      return inFirst;

    if(inFirst == WeaponStyle.UNKNOWN)
      return inSecond;

    if(inSecond == WeaponStyle.UNKNOWN)
      return inFirst;

    throw new IllegalArgumentException(
        "Cannot add conditions with different styles");
  }

  @Override
  public String toString()
  {
    if(m_style != WeaponStyle.UNKNOWN)
      if(m_generic.isEmpty())
        return m_style.toString();
      else
        return m_generic + ", " + m_style;
    else
      return m_generic;
  }
}
