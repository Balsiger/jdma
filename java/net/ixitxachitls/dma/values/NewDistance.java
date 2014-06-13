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
public class NewDistance extends NewValue.Arithmetic<DistanceProto>
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
      Optional<NewRational> miles = Optional.absent();
      Optional<NewRational> feet = Optional.absent();
      Optional<NewRational> inches = Optional.absent();

      List<String []> parts =
        Strings.getAllPatterns(inValue,
                               "(?:\\s*(.*?)"
                               + "\\s*(in|inch|inches"
                                 + "|ml|mile|miles|ft|feet|foot))");

      if(parts.isEmpty())
        return Optional.absent();

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
            miles = add(miles, number);
            break;

          case "ft":
          case "feet":
          case "foot":
            feet = add(feet, number);
            break;

          case "in":
          case "inch":
          case "inches":
            inches = add(inches, number);
            break;
        }
      }

      return Optional.of(new NewDistance(miles, feet, inches));
    }
  }

  public NewDistance(Optional<NewRational> inMiles,
                     Optional<NewRational> inFeet,
                     Optional<NewRational> inInches)
  {
    m_miles = inMiles;
    m_feet = inFeet;
    m_inches = inInches;
  }

  public static Parser<NewDistance> PARSER = new DistanceParser();

  private final Optional<NewRational> m_miles;
  private final Optional<NewRational> m_feet;
  private final Optional<NewRational> m_inches;

  public double asMiles()
  {
    return (m_miles.isPresent() ? m_miles.get().asDouble() : 0)
      + (m_feet.isPresent() ? m_feet.get().asDouble() / 5280 : 0)
      + (m_inches.isPresent() ? m_inches.get().asDouble() / 63360 : 0);
  }

  public double asFeet()
  {
    return (m_miles.isPresent() ? m_miles.get().asDouble() * 5280 : 0)
      + (m_feet.isPresent() ? m_feet.get().asDouble() : 0)
      + (m_inches.isPresent() ? m_inches.get().asDouble() / 12 : 0);
  }

  public double asInches()
  {
    return (m_miles.isPresent() ? m_miles.get().asDouble() * 63360 : 0)
      + (m_feet.isPresent() ? m_feet.get().asDouble() * 12 : 0)
      + (m_inches.isPresent() ? m_inches.get().asDouble() : 0);
  }

  @Override
  public String toString()
  {
    if(!m_miles.isPresent() && !m_feet.isPresent() && !m_inches.isPresent())
      return "0 ft";

    List<String> parts = new ArrayList<>();
    if(m_miles.isPresent())
      parts.add(m_miles.get() + " ml");

    if(m_feet.isPresent())
      parts.add(m_feet.get() + " ft");

    if(m_inches.isPresent())
      parts.add(m_inches.get() + " in");

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

    if(m_miles.isPresent())
      builder.setMiles(m_miles.get().toProto());
    if(m_feet.isPresent())
      builder.setFeet(m_feet.get().toProto());
    if(m_inches.isPresent())
      builder.setInches(m_inches.get().toProto());

    return DistanceProto.newBuilder().setImperial(builder.build()).build();
  }

  public static NewDistance fromProto(DistanceProto inProto)
  {
    if(!inProto.hasImperial())
      throw new IllegalArgumentException("expected an imperial distance");

    Optional<NewRational> miles = Optional.absent();
    Optional<NewRational> feet = Optional.absent();
    Optional<NewRational> inches = Optional.absent();

    if(inProto.getImperial().hasMiles())
      miles =
        Optional.of(NewRational.fromProto(inProto.getImperial().getMiles()));
    if(inProto.getImperial().hasFeet())
      feet =
        Optional.of(NewRational.fromProto(inProto.getImperial().getFeet()));
    if(inProto.getImperial().hasInches())
      inches =
        Optional.of(NewRational.fromProto(inProto.getImperial().getInches()));

    return new NewDistance(miles, feet, inches);
  }

  @Override
  public NewValue.Arithmetic<DistanceProto>
    add(@Nullable NewValue.Arithmetic<DistanceProto> inValue)
  {
    if(inValue == null)
      return this;

    if(!(inValue instanceof NewDistance))
      throw new IllegalArgumentException("can only add another distance value");

    NewDistance value = (NewDistance)inValue;
    return new NewDistance(add(m_miles, value.m_miles),
                           add(m_feet, value.m_feet),
                           add(m_inches, value.m_inches));
  }

  @Override
  public boolean canAdd(NewValue.Arithmetic<DistanceProto> inValue)
  {
    return inValue instanceof NewDistance;
  }

  @Override
  public NewValue.Arithmetic<DistanceProto> multiply(int inFactor)
  {
    return new NewDistance(multiply(m_miles, inFactor),
                           multiply(m_feet, inFactor),
                           multiply(m_inches, inFactor));

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
