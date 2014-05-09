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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Values.DistanceProto;
import net.ixitxachitls.util.Strings;

/**
 * A representation of a distance value.
 *
 * @file   NewDistance.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class NewDistance extends NewValue.Addable<DistanceProto>
  implements Comparable<NewDistance>
{
  public static class DistanceParser extends Parser<NewDistance>
  {
    public DistanceParser()
    {
      super(1);
    }

    @Override
    public Optional<NewDistance> doParse(String inValue)
    {
      NewRational miles = null;
      NewRational feet = null;
      NewRational inches = null;

      List<String []> parts =
        Strings.getAllPatterns(inValue,
                               "(?:\\s*(.*?)"
                               + "\\s*(in|inch|inches"
                                 + "|ml|mile|miles|ft|feet|foot))");

      if(parts.isEmpty())
        return null;

      for(String []part : parts)
      {
        if(part.length != 2)
          return Optional.absent();

        Optional<NewRational> number = NewRational.PARSER.parse(part[0]);
        if(!number.isPresent())
          return Optional.absent();

        switch(part[1].toLowerCase())
        {
          case "ml":
          case "mile":
          case "miles":
            if(miles == null)
              miles = number.get();
            else
              miles = (NewRational)miles.add(number.get());
            break;

          case "ft":
          case "feet":
          case "foot":
            if(feet == null)
              feet = number.get();
            else
              feet = (NewRational)feet.add(number.get());
            break;

          case "in":
          case "inch":
          case "inches":
            if(inches == null)
              inches = number.get();
            else
              inches = (NewRational)inches.add(number.get());
            break;
        }
      }

      return Optional.of(new NewDistance(miles, feet, inches));
    }
  }

  public NewDistance(@Nullable NewRational inMiles,
                     @Nullable NewRational inFeet,
                     @Nullable NewRational inInches)
  {
    m_miles = inMiles;
    m_feet = inFeet;
    m_inches = inInches;
  }

  public static Parser<NewDistance> PARSER = new DistanceParser();

  private final @Nullable NewRational m_miles;
  private final @Nullable NewRational m_feet;
  private final @Nullable NewRational m_inches;

  public double asMiles()
  {
    return (m_miles == null ? 0 : m_miles.asDouble())
      + (m_feet == null ? 0 : m_feet.asDouble() / 5280)
      + (m_inches == null ? 0 : m_inches.asDouble() / 63360);
  }

  public double asFeet()
  {
    return (m_miles == null ? 0 : m_miles.asDouble() * 5280)
      + (m_feet == null ? 0 : m_feet.asDouble())
      + (m_inches == null ? 0 : m_inches.asDouble() / 12);
  }

  public double asInches()
  {
    return (m_miles == null ? 0 : m_miles.asDouble() * 63360)
      + (m_feet == null ? 0 : m_feet.asDouble() * 12)
      + (m_inches == null ? 0 : m_inches.asDouble());
  }

  @Override
  public String toString()
  {
    if(m_miles == null && m_feet == null && m_inches == null)
      return "0 ft";

    List<String> parts = new ArrayList<>();
    if(m_miles != null)
      parts.add(m_miles + " ml");

    if(m_feet != null)
      parts.add(m_feet + " ft");

    if(m_inches != null)
      parts.add(m_inches + " in");

    return Strings.SPACE_JOINER.join(parts);
  }

  @Override
  public String group()
  {
    double inches = asInches();
    if(inches <= 1)
      return "1 in";

    if(inches <= 2)
      return "2 in";

    if(inches <= 6)
      return "6 in";

    double feet = asFeet();
    if(feet <= 1)
      return "1 ft";

    if(feet <= 5)
      return "5 ft";

    if(feet <= 10)
      return "10 ft";

    if(feet <= 25)
      return "25 ft";

    if(feet <= 50)
      return "50 ft";

    if(feet <= 100)
      return "100 ft";

    if(feet <= 500)
      return "500 ft";

    if(feet <= 1000)
      return "1000 ft";

    double miles = asMiles();
    if(miles <= 1)
      return "1 ml";

    if(miles <= 5)
      return "5 ml";

    if(miles <= 10)
      return "10 ml";

    if(miles <= 25)
      return "25 ml";

    if(miles <= 50)
      return "50 ml";

    if(miles <= 100)
      return "100 ml";

    return "a lot";
  }

  @Override
  public DistanceProto toProto()
  {
    DistanceProto.Imperial.Builder builder = DistanceProto.Imperial.newBuilder();

    if(m_miles!= null)
      builder.setMiles(m_miles.toProto());
    if(m_feet != null)
      builder.setFeet(m_feet.toProto());
    if(m_inches != null)
      builder.setInches(m_inches.toProto());

    return DistanceProto.newBuilder().setImperial(builder.build()).build();
  }

  public static NewDistance fromProto(DistanceProto inProto)
  {
    if(!inProto.hasImperial())
      throw new IllegalArgumentException("expected an imperial weight");

    NewRational miles = null;
    NewRational feet = null;
    NewRational inches = null;

    if(inProto.getImperial().hasMiles())
      miles = NewRational.fromProto(inProto.getImperial().getMiles());
    if(inProto.getImperial().hasFeet())
      feet = NewRational.fromProto(inProto.getImperial().getFeet());
    if(inProto.getImperial().hasInches())
      inches = NewRational.fromProto(inProto.getImperial().getInches());

    return new NewDistance(miles, feet, inches);
  }

  @Override
  public NewValue.Addable<DistanceProto>
    add(@Nullable NewValue.Addable<DistanceProto> inValue)
  {
    if(inValue == null)
      return this;

    if(!(inValue instanceof NewDistance))
      throw new IllegalArgumentException("can only add another distance value");

    NewDistance value = (NewDistance)inValue;
    return new NewDistance(m_miles == null
                         ? value.m_miles
                         : (NewRational)m_miles.add(value.m_miles),
                         m_feet == null
                         ? value.m_feet
                         : (NewRational)m_feet.add(value.m_feet),
                         m_inches == null
                         ? value.m_inches
                         : (NewRational)m_inches.add(value.m_inches));
  }

  @Override
  public boolean canAdd(NewValue.Addable<DistanceProto> inValue)
  {
    return inValue instanceof NewDistance;
  }

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Spaces tests. */
    @org.junit.Test
    public void parse()
    {
      assertEquals("parse", "1 ml", PARSER.parse("1 ml").toString());
      assertEquals("parse", "1 ml", PARSER.parse("1   mls").toString());
      assertEquals("parse", "1/2 in", PARSER.parse("1/2 inch").toString());
      assertEquals("parse", "3 ml 3 in",
                   PARSER.parse("1 ml 2 ml 3 in").toString());
      assertEquals("parse", "1 ml", PARSER.parse("1 mile").toString());
      assertNull("parse", PARSER.parse("1"));
      assertEquals("parse", "1 ft", PARSER.parse("1 ft 2").toString());
      assertEquals("parse", "1 ft", PARSER.parse("1 ftt").toString());
      assertEquals("parse", "1 ft 1 in",
                   PARSER.parse("1 ft 1 in 1 guru").toString());
      assertNull("parse", PARSER.parse(""));
    }
  }

  @Override
  public int compareTo(NewDistance inOther)
  {
    if(this == inOther)
      return 0;

    return Double.compare(asFeet(), inOther.asFeet());
  }
}
