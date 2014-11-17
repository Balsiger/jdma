/******************************************************************************
" * Copyright (c) 2002-2013 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Values.WeightProto;
import net.ixitxachitls.util.Strings;

/**
 * A representation of a weight value.
 *
 * @file   NewWeight.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Weight extends NewValue.Arithmetic<WeightProto>
{
  public static class WeightParser extends Parser<Weight>
  {
    public WeightParser()
    {
      super(1);
    }

    @Override
    public Optional<Weight> doParse(String inValue)
    {
      Optional<NewRational> pounds = Optional.absent();
      Optional<NewRational> ounces = Optional.absent();

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

        Optional<NewRational> number = NewRational.PARSER.parse(part[0]);
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
        }
      }

      return Optional.of(new Weight(pounds, ounces));
    }
  }

  public Weight(Optional<NewRational> inPounds,
                Optional<NewRational> inOunces)
  {
    m_pounds = inPounds;
    m_ounces = inOunces;
  }

  public static Parser<Weight> PARSER = new WeightParser();

  private final Optional<NewRational> m_pounds;
  private final Optional<NewRational> m_ounces;

  public double asPounds()
  {
    return (m_pounds.isPresent() ? m_pounds.get().asDouble() : 0)
      + (m_ounces.isPresent() ? m_ounces.get().asDouble() /  16 : 0);
  }

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

  public static Weight fromProto(WeightProto inProto)
  {
    if(!inProto.hasImperial())
      throw new IllegalArgumentException("expected an imperial weight");

    Optional<NewRational> pounds = Optional.absent();
    Optional<NewRational> ounces = Optional.absent();

    if(inProto.getImperial().hasPounds())
      pounds =
        Optional.of(NewRational.fromProto(inProto.getImperial().getPounds()));
    if(inProto.getImperial().hasOunces())
      ounces =
        Optional.of(NewRational.fromProto(inProto.getImperial().getOunces()));

    return new Weight(pounds, ounces);
  }

  @Override
  public NewValue.Arithmetic<WeightProto>
    add(@Nullable NewValue.Arithmetic<WeightProto> inValue)
  {
    if(inValue == null)
      return this;

    if(!(inValue instanceof Weight))
      throw new IllegalArgumentException("can only add another weight value");

    Weight value = (Weight)inValue;
    return new Weight(add(m_pounds, value.m_pounds),
                         add(m_ounces, value.m_ounces));
  }

  @Override
  public NewValue.Arithmetic<WeightProto> multiply(int inFactor)
  {
    return new Weight(multiply(m_pounds, inFactor),
                         multiply(m_ounces, inFactor));
  }

  @Override
  public boolean canAdd(NewValue.Arithmetic<WeightProto> inValue)
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
      assertEquals("parse", "1 lb", PARSER.parse("1 lb").toString());
      assertEquals("parse", "1 lb", PARSER.parse("1   lbs").toString());
      assertEquals("parse", "1/2 oz", PARSER.parse("1/2 oz").toString());
      assertEquals("parse", "3 lb 3 oz",
                   PARSER.parse("1 lb 2 lb 3 oz").toString());
      assertEquals("parse", "1 lb", PARSER.parse("1 pounds").toString());
      assertNull("parse", PARSER.parse("1"));
      assertEquals("parse", "1 lb", PARSER.parse("1 lb 2").toString());
      assertEquals("parse", "1 lb", PARSER.parse("1 lbt").toString());
      assertEquals("parse", "1 lb 1 oz",
                   PARSER.parse("1 oz 1 lb 1 guru").toString());
      assertNull("parse", PARSER.parse(""));
    }
  }
}
