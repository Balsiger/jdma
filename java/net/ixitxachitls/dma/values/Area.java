/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Values.AreaProto;
import net.ixitxachitls.util.Strings;

/**
 * This class stores a area. This is a convenience class for Units.
 *
 * @file          Area.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@Immutable
public class Area extends Value.Arithmetic<AreaProto>
{
  /** The parser for areas. */
  public static final Parser<Area> PARSER = new Parser<Area>(1)
  {
    @Override
    protected Optional<Area> doParse(String inValue)
    {
      if(inValue.trim().isEmpty())
        return Optional.absent();

      List<String []>parts =
        Strings.getAllPatterns(inValue,
                               "^(?:\\s*(.*?)"
                                 + "\\s*(sq yd|square yard|square yards"
                                 + "|sq ft|square foot|square feet"
                                 + "|sq in|square inch|square inches"
                                 + "|sq m|square meter|square meters"
                                 + "|sq dm|square decimeter|square decimeters"
                                 + "|sq cm|square centimeter"
                                 + "|square centimeters))\\s*$");
      if(parts.isEmpty())
        return Optional.absent();

      Optional<Rational> sqYards = Optional.absent();
      Optional<Rational> sqFeet = Optional.absent();
      Optional<Rational> sqInches = Optional.absent();
      Optional<Rational> sqMeters = Optional.absent();
      Optional<Rational> sqDeciMeters = Optional.absent();
      Optional<Rational> sqCentiMeters = Optional.absent();

      for(String []part : parts)
      {
        if(part.length != 2)
          return Optional.absent();

        Optional<Rational> number = Rational.PARSER.parse(part[0]);
        if(!number.isPresent())
          return Optional.absent();

        switch(part[1].toLowerCase())
        {
          case "sq yd":
          case "square yard":
          case "square yards":
            sqYards = add(sqYards, number);
            break;

          case "sq ft":
          case "square foot":
          case "square feet":
            sqFeet = add(sqFeet, number);
            break;

          case "sq in":
          case "square inch":
          case "square inches":
            sqInches = add(sqInches, number);
            break;

          case "sq m":
          case "square meter":
          case "square meters":
            sqMeters = add(sqMeters, number);
            break;

          case "sq dm":
          case "square decimeter":
          case "square decimeters":
            sqDeciMeters = add(sqDeciMeters, number);
            break;

          case "sq cm":
          case "square centimeter":
          case "square centimeters":
            sqCentiMeters = add(sqCentiMeters, number);
            break;

          default:
            // Just ignore it.
            break;
        }
      }

      return Optional.of(new Area(sqYards, sqFeet, sqInches,
                                  sqMeters, sqDeciMeters, sqCentiMeters));
    }
  };

  /**
   * Construct the area object with an undefined value.
   *
   * @param inSqYards the number of square yards.
   * @param inSqFeet the number of square feet.
   * @param inSqInches the number of square inches.
   * @param inSqMeters the number of square meters.
   * @param inSqDeciMeters the number of square deci meters.
   * @param inSqCentiMeters the number of square centi meters.
   */
  public Area(Optional<Rational> inSqYards,
              Optional<Rational> inSqFeet,
              Optional<Rational> inSqInches,
              Optional<Rational> inSqMeters,
              Optional<Rational> inSqDeciMeters,
              Optional<Rational> inSqCentiMeters)
  {
    m_sqYards = inSqYards;
    m_sqFeet = inSqFeet;
    m_sqInches = inSqInches;
    m_sqMeters = inSqMeters;
    m_sqDeciMeters = inSqDeciMeters;
    m_sqCentiMeters = inSqCentiMeters;
  }

  /** The number of square yards in the area. */
  private final Optional<Rational> m_sqYards;

  /** The number of square feet in the area. */
  private final Optional<Rational> m_sqFeet;

  /** The number of square inches in the area. */
  private final Optional<Rational> m_sqInches;

  /** The number of square meters in the area. */
  private final Optional<Rational> m_sqMeters;

  /** The number of square deci meters in the area. */
  private final Optional<Rational> m_sqDeciMeters;

  /** The number of square centi meters in the area. */
  private final Optional<Rational> m_sqCentiMeters;

  /**
   * Determine if metric values are stored.
   *
   * @return      true if metric values are stored, false else
   */
  public boolean isMetric()
  {
    return m_sqMeters.isPresent() || m_sqDeciMeters.isPresent()
      || m_sqCentiMeters.isPresent();
  }

  /**
   * Determine if feet values are stored.
   *
   * @return      true if feet values are stored, false else
   */
  public boolean isFeet()
  {
    return m_sqYards.isPresent() || m_sqFeet.isPresent()
      || m_sqInches.isPresent();
  }

  @Override
  public String toString()
  {
    List<String> parts = new ArrayList<>();

    if(m_sqYards.isPresent())
      parts.add(m_sqYards.get() + " sq yd");

    if(m_sqFeet.isPresent())
      parts.add(m_sqFeet.get() + " sq ft");

    if(m_sqInches.isPresent())
      parts.add(m_sqInches.get() + " sq in");

    if(m_sqMeters.isPresent())
      parts.add(m_sqMeters.get() + " sq m");

    if(m_sqDeciMeters.isPresent())
      parts.add(m_sqDeciMeters.get() + " sq dm");

    if(m_sqCentiMeters.isPresent())
      parts.add(m_sqCentiMeters.get() + " sq cm");

    if(parts.isEmpty())
      return "0 sq ft";

    return Strings.SPACE_JOINER.join(parts);
  }

  /**
   * Create a proto for the value.
   *
   * @return the proto representation
   */
  @Override
  public AreaProto toProto()
  {
    AreaProto.Builder builder = AreaProto.newBuilder();

    if(isFeet())
    {
      AreaProto.Imperial.Builder imperial = AreaProto.Imperial.newBuilder();

      if(m_sqYards.isPresent())
        imperial.setSquareYards(m_sqYards.get().toProto());
      if(m_sqFeet.isPresent())
        imperial.setSquareFeet(m_sqFeet.get().toProto());
      if(m_sqInches.isPresent())
        imperial.setSquareInches(m_sqInches.get().toProto());

      builder.setImperial(imperial.build());
    }

    if(isMetric())
    {
      AreaProto.Metric.Builder metric = AreaProto.Metric.newBuilder();

      if(m_sqMeters.isPresent())
        metric.setSquareMeters(m_sqMeters.get().toProto());
      if(m_sqDeciMeters.isPresent())
        metric.setSquareDecimeters(m_sqDeciMeters.get().toProto());
      if(m_sqCentiMeters.isPresent())
        metric.setSquareCentimeters(m_sqCentiMeters.get().toProto());

      builder.setMetric(metric.build());
    }

    return builder.build();
  }

  /**
   * Create a new area similar to the current but with data from the
   * given proto.
   *
   * @param inProto  the proto with the data
   * @return the newly created area
   */
  public static Area fromProto(AreaProto inProto)
  {
    Optional<Rational> sqYards = Optional.absent();
    Optional<Rational> sqFeet = Optional.absent();
    Optional<Rational> sqInches = Optional.absent();
    Optional<Rational> sqMeters = Optional.absent();
    Optional<Rational> sqDeciMeters = Optional.absent();
    Optional<Rational> sqCentiMeters = Optional.absent();

    if(inProto.hasMetric())
    {
      if(inProto.getMetric().hasSquareMeters())
        sqMeters = Optional.of(Rational.fromProto
            (inProto.getMetric().getSquareMeters()));
      if(inProto.getMetric().hasSquareDecimeters())
        sqDeciMeters = Optional.of(Rational.fromProto
            (inProto.getMetric().getSquareDecimeters()));
      if(inProto.getMetric().hasSquareCentimeters())
        sqCentiMeters =
          Optional.of(Rational.fromProto
              (inProto.getMetric().getSquareCentimeters()));
    }

    if(inProto.hasImperial())
    {
      if(inProto.getImperial().hasSquareYards())
        sqYards = Optional.of(Rational.fromProto
            (inProto.getImperial().getSquareYards()));
      if(inProto.getImperial().hasSquareFeet())
        sqFeet = Optional.of(Rational.fromProto
            (inProto.getImperial().getSquareFeet()));
      if(inProto.getImperial().hasSquareInches())
        sqInches = Optional.of(Rational.fromProto
            (inProto.getImperial().getSquareInches()));
    }

    return new Area(sqYards, sqFeet, sqInches,
                    sqMeters, sqDeciMeters, sqCentiMeters);
  }

  @Override
  public Arithmetic<AreaProto> add(Arithmetic<AreaProto> inValue)
  {
    if(!(inValue instanceof Area))
      return this;

    Area value = (Area)inValue;

    return new Area(add(m_sqYards, value.m_sqYards),
                    add(m_sqFeet, value.m_sqFeet),
                    add(m_sqInches, value.m_sqInches),
                    add(m_sqMeters, value.m_sqMeters),
                    add(m_sqDeciMeters, value.m_sqDeciMeters),
                    add(m_sqCentiMeters, value.m_sqCentiMeters));
  }

  @Override
  public boolean canAdd(Value.Arithmetic<AreaProto> inValue)
  {
    return inValue instanceof Area;
  }

  @Override
  public Value.Arithmetic<AreaProto> multiply(int inFactor)
  {
    return new Area(multiply(m_sqYards, inFactor),
                    multiply(m_sqFeet, inFactor),
                    multiply(m_sqInches, inFactor),
                    multiply(m_sqMeters, inFactor),
                    multiply(m_sqDeciMeters, inFactor),
                    multiply(m_sqCentiMeters, inFactor));
  }

  @Override
  public Value.Arithmetic<AreaProto> multiply(Rational inFactor)
  {
    return new Area(multiply(m_sqYards, inFactor),
                    multiply(m_sqFeet, inFactor),
                    multiply(m_sqInches, inFactor),
                    multiply(m_sqMeters, inFactor),
                    multiply(m_sqDeciMeters, inFactor),
                    multiply(m_sqCentiMeters, inFactor));
  }

  //---------------------------------------------------------------------------

  /** The Test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      Area value = new Area(Optional.<Rational>absent(),
                            Optional.<Rational>absent(),
                            Optional.<Rational>absent(),
                            Optional.<Rational>absent(),
                            Optional.<Rational>absent(),
                            Optional.<Rational>absent());

      // undefined value
      assertEquals("undefined value not correct", "0 sq ft", value.toString());
      assertEquals("feet",   false, value.isFeet());
      assertEquals("metric", false, value.isMetric());

      // now with some value (sq cm)
      value = new Area(Optional.<Rational>absent(),
                       Optional.<Rational>absent(),
                       Optional.<Rational>absent(),
                       Optional.<Rational>absent(),
                       Optional.<Rational>absent(),
                       Optional.of(new Rational(1, 1, 2)));

      assertEquals("string ", "1 1/2 sq cm", value.toString());
      assertEquals("feet",   false,  value.isFeet());
      assertEquals("metric", true,   value.isMetric());

       value = new Area(Optional.<Rational>absent(),
                        Optional.of(new Rational(1, 2, 3)),
                        Optional.of(new Rational(2, 0, 0)),
                        Optional.<Rational>absent(),
                        Optional.<Rational>absent(),
                        Optional.<Rational>absent());

       assertEquals("string", "1 2/3 sq ft 2 sq in", value.toString());
       assertEquals("feet",   true,  value.isFeet());
       assertEquals("metric", false, value.isMetric());
   }
  }
}
