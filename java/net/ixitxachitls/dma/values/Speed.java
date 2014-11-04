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

import net.ixitxachitls.dma.entries.Maneuverability;
import net.ixitxachitls.dma.entries.MovementMode;
import net.ixitxachitls.dma.proto.Values.SpeedProto;
import net.ixitxachitls.util.Strings;

public class Speed extends NewValue.Arithmetic<SpeedProto>
{
  public Speed(MovementMode inMode, NewDistance inSpeed,
               Optional<Maneuverability> inManeuverability)
  {
    m_mode = inMode;
    m_speed = inSpeed;
    m_maneuverability = inManeuverability;

    if(m_mode == MovementMode.UNKNOWN)
      m_mode = MovementMode.RUN;
  }

  private MovementMode m_mode;
  private NewDistance m_speed;
  private Optional<Maneuverability> m_maneuverability;

  /** The parser for armor types. */
  public static final Parser<Speed> PARSER = new Parser<Speed>(3)
    {
      @Override
      public Optional<Speed> doParse(String inMode, String inSpeed,
                                     String inManeuverability)
      {
        Optional<MovementMode> mode = MovementMode.fromString(inMode);
        if(!mode.isPresent())
          mode = Optional.of(MovementMode.RUN);

        Optional<NewDistance> speed = NewDistance.PARSER.parse(inSpeed);
        if(!speed.isPresent())
          return Optional.absent();

        return Optional.of(new Speed
                           (mode.get(), speed.get(),
                            Maneuverability.PARSER.parse(inManeuverability)));
      }

      @Override
      public String []split(String []inValues)
      {
        if(inValues.length != 1)
          return super.split(inValues);

        return Strings.getPatterns(inValues[0],
                                   "^\\s*([a-zA-Z]\\w+)?\\s*(\\d.*?)\\s*"
                                   + "(?:\\((.*)\\))?\\s*$");
      }
    };

  public MovementMode getMode()
  {
    return m_mode;
  }

  public NewDistance getSpeed()
  {
    return m_speed;
  }

  public Optional<Maneuverability> getManeuverability()
  {
    return m_maneuverability;
  }

  @Override
  public String toString()
  {
    return (m_mode != MovementMode.RUN && m_mode != MovementMode.UNKNOWN
      ? m_mode + " " : "")
      + m_speed.toString()
      + (m_maneuverability.isPresent()
        && m_maneuverability.get() != Maneuverability.UNKNOWN
        && m_maneuverability.get() != Maneuverability.NONE
        ? " (" + m_maneuverability.get() + ")"
        : "");
  }

  @Override
  public SpeedProto toProto()
  {
    SpeedProto.Builder builder = SpeedProto.newBuilder();

    builder.setMode(getMode().toProto());
    builder.setDistance(getSpeed().toProto());
    if(getManeuverability().isPresent())
      builder.setManeuverability(getManeuverability().get().toProto());

    return builder.build();
  }

  public static Speed fromProto(SpeedProto inProto)
  {
    return new Speed(MovementMode.fromProto(inProto.getMode()),
                     NewDistance.fromProto(inProto.getDistance()),
                     inProto.hasManeuverability()
                      ? Optional.of(Maneuverability.fromProto
                                    (inProto.getManeuverability()))
                      : Optional.<Maneuverability>absent());
  }

  @Override
  public NewValue.Arithmetic<SpeedProto>
    add(NewValue.Arithmetic<SpeedProto> inValue)
  {
    if(!canAdd(inValue))
      return this;

    Speed speed = (Speed)inValue;
    return new Speed(m_mode,
                     (NewDistance) m_speed.add(speed.m_speed),
                     min(speed.m_maneuverability, m_maneuverability));
  }

  private Optional<Maneuverability> min(Optional<Maneuverability> first,
                                        Optional<Maneuverability> second) {
    if(!first.isPresent())
      return second;

    if(!second.isPresent())
      return first;

    if(first.get().ordinal() > second.get().ordinal())
      return first;

    return second;
  }

  @Override
  public boolean canAdd(NewValue.Arithmetic<SpeedProto> inValue)
  {
    if(!(inValue instanceof Speed))
      return false;

    Speed value = (Speed) inValue;
    return m_mode == value.m_mode;
  }

  @Override
  public NewValue.Arithmetic<SpeedProto> multiply(int inFactor)
  {
    return new Speed(m_mode,
                     (NewDistance)m_speed.multiply(inFactor),
                     m_maneuverability);
  }
}