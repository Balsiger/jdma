/******************************************************************************
 * Copyright (c) 2002-2014 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Values.WeightProto;
import net.ixitxachitls.util.Strings;

/**
 * A representation of a weight value.
 *
 * @file   NewWeight.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Weight extends Value.Arithmetic<WeightProto>
{
  /** The weight parser. */
  public static class WeightParser extends Parser<Weight>
  {
    /** Create the parser. */
    public WeightParser()
    {
      super(1);
    }

    @Override
    public Optional<Weight> doParse(String inValue)
    {
      Optional<Rational> pounds = Optional.absent();
      Optional<Rational> ounces = Optional.absent();

      List<String []> parts =
        Strings.getAllPatterns(inValue,
                               "(?:\\s*(.*?)"
                               + "\\s*(lb|lbs|pound|pounds|oz|ounce|ounces))");

      if(parts.isEmpty())
        return Optional.absent();

      for(String []part : parts)
      {
        if(part.length != 2)
          return Optional.absent();

        Optional<Rational> number = Rational.PARSER.parse(part[0]);
        if(!number.isPresent())
          return Optional.absent();

        switch(part[1].toLowerCase())
        {
          case "lb":
          case "lbs":
          case "pound":
          case "pounds":
            pounds = add(pounds, number);
            break;

          case "oz":
          case "ounce":
          case "ounces":
            ounces = add(ounces, number);
            break;

          default:
            // we just ignore it
            break;
        }
      }

      return Optional.of(new Weight(pounds, ounces));
    }
  }

  public Weight()
  {
    this(Optional.<Rational>absent(), Optional.<Rational>absent());
  }

  /**
   * Create a weight with imperial metrics.
   *
   * @param inPounds the number of pounds
   * @param inOunces the number of ounces
   */
  public Weight(Optional<Rational> inPounds,
                Optional<Rational> inOunces)
  {
    m_pounds = inPounds;
    m_ounces = inOunces;
  }

  /** The parser for weights. */
  public static final Parser<Weight> PARSER = new WeightParser();

  /** The number of pounds for this weight. */
  private final Optional<Rational> m_pounds;

  /** The number of ounces for this weight. */
  private final Optional<Rational> m_ounces;

  /**
   * Get the weight as pounds.
   * @return the weight converted into pounds
   */
  public double asPounds()
  {
    return (m_pounds.isPresent() ? m_pounds.get().asDouble() : 0)
      + (m_ounces.isPresent() ? m_ounces.get().asDouble() /  16 : 0);
  }

  /**
   * Get the weight as ounces.
   * @return the weight converted into ounces
   */
  public double asOunces()
  {
    return (m_pounds.isPresent() ? m_pounds.get().asDouble() * 16 : 0)
      + (m_ounces.isPresent() ? m_ounces.get().asDouble() : 0);
  }

  @Override
  public String toString()
  {
    if(!m_pounds.isPresent() && !m_ounces.isPresent())
      return "0 lb";

    if(!m_pounds.isPresent())
      return m_ounces.get() + " oz";

    if(!m_ounces.isPresent())
      return m_pounds.get() + " lb";

    return m_pounds.get() + " lb " + m_ounces.get() + " oz";
  }

  @Override
  public String group()
  {
    double pounds = asPounds();
    if(pounds < 0.1)
      return "1/10 lb";

    if(pounds < 1)
      return "1 lb";

    if(pounds < 2)
      return "2 lb";

    if(pounds < 3)
      return "3 lb";

    if(pounds < 4)
      return "4 lb";

    if(pounds < 5)
      return "5 lb";

    if(pounds < 11)
      return "10 lb";

    if(pounds < 26)
      return "25 lb";

    if(pounds <= 51)
      return "50 lb";

    if(pounds < 101)
      return "100 lb";

    return "a lot";
  }

  @Override
  public WeightProto toProto()
  {
    WeightProto.Imperial.Builder builder = WeightProto.Imperial.newBuilder();

    if(m_pounds.isPresent())
      builder.setPounds(m_pounds.get().toProto());
    if(m_ounces.isPresent())
      builder.setOunces(m_ounces.get().toProto());

    return WeightProto.newBuilder().setImperial(builder.build()).build();
  }

  /**
   * Convert the weight from the given proto.
   *
   * @param  inProto the proto to convert from
   * @return the converted weight
   */
  public static Weight fromProto(WeightProto inProto)
  {
    if(!inProto.hasImperial())
      throw new IllegalArgumentException("expected an imperial weight");

    Optional<Rational> pounds = Optional.absent();
    Optional<Rational> ounces = Optional.absent();

    if(inProto.getImperial().hasPounds())
      pounds =
        Optional.of(Rational.fromProto(inProto.getImperial().getPounds()));
    if(inProto.getImperial().hasOunces())
      ounces =
        Optional.of(Rational.fromProto(inProto.getImperial().getOunces()));

    return new Weight(pounds, ounces);
  }

  @Override
  public Value.Arithmetic<WeightProto>
    add(Value.Arithmetic<WeightProto> inValue)
  {
    if(!(inValue instanceof Weight))
      throw new IllegalArgumentException("can only add another weight value");

    Weight value = (Weight)inValue;
    return new Weight(add(m_pounds, value.m_pounds),
                      add(m_ounces, value.m_ounces));
  }

  @Override
  public Value.Arithmetic<WeightProto> multiply(int inFactor)
  {
    return new Weight(multiply(m_pounds, inFactor),
                      multiply(m_ounces, inFactor));
  }
  @Override
  public Value.Arithmetic<WeightProto> multiply(Rational inFactor)
  {
    return new Weight(multiply(m_pounds, inFactor),
                      multiply(m_ounces, inFactor));
  }

  @Override
  public boolean canAdd(Value.Arithmetic<WeightProto> inValue)
  {
    return inValue instanceof Weight;
  }

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Spaces tests. */
    @org.junit.Test
    public void parse()
    {
      assertEquals("parse", "1 lb", PARSER.parse("1 lb").get().toString());
      assertEquals("parse", "1 lb", PARSER.parse("1   lbs").get().toString());
      assertEquals("parse", "1/2 oz", PARSER.parse("1/2 oz").get().toString());
      assertEquals("parse", "3 lb 3 oz",
                   PARSER.parse("1 lb 2 lb 3 oz").get().toString());
      assertEquals("parse", "1 lb", PARSER.parse("1 pounds").get().toString());
      assertFalse("parse", PARSER.parse("1").isPresent());
      assertEquals("parse", "1 lb", PARSER.parse("1 lb 2").get().toString());
      assertEquals("parse", "1 lb", PARSER.parse("1 lbt").get().toString());
      assertEquals("parse", "1 lb 1 oz",
                   PARSER.parse("1 oz 1 lb 1 guru").get().toString());
      assertFalse("parse", PARSER.parse("").isPresent());
    }
  }
}
