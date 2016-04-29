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

package net.ixitxachitls.dma.values;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.*;
import net.ixitxachitls.dma.proto.Values;

/**
 * A class representing when and where something occured.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Occurrence extends Value<Values.OccurrenceProto>
{
  /** The parser. */
  public static Parser<Occurrence> parser(final Calendar inCalendar) {
    return new Parser<Occurrence>(2)
    {
      @Override
      public Optional<Occurrence> doParse(String inLocation, String inDate)
      {
        Optional<CampaignDate> date =
            CampaignDate.parser(inCalendar).parse(inDate);
        if(!date.isPresent())
          return Optional.absent();

        return Optional.of(new Occurrence(inLocation, date.get()));
      }
    };
  }

  public Occurrence(String inLocation, CampaignDate inDate)
  {
    m_location = inLocation;
    m_date = inDate;
  }

  private final String m_location;
  private final CampaignDate m_date;

  public String getLocation()
  {
    return m_location;
  }

  public CampaignDate getDate()
  {
    return m_date;
  }

  @Override
  public String toString()
  {
    return m_location + " (" + m_date + ")";
  }

  @Override
  public Values.OccurrenceProto toProto()
  {
    return Values.OccurrenceProto.newBuilder()
        .setLocation(Values.LocationProto.newBuilder().setName(m_location))
        .setDate(m_date.toProto())
        .build();
  }

  public static Occurrence fromProto(Calendar inCalendar,
                                     Values.OccurrenceProto inProto)
  {
    return new Occurrence(
        inProto.getLocation().getName(),
        CampaignDate.fromProto(inCalendar, inProto.getDate()));
  }
}
