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

import java.io.StringReader;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.entries.Monster;
import net.ixitxachitls.dma.proto.Values;
import net.ixitxachitls.dma.values.enums.Ability;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.input.ReadException;

/**
 * A condition the limits the application of another value or entry.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Condition extends Value.Arithmetic<Values.ConditionProto>
{
  private static class Limit
  {
    private Limit(Operator inOperator, int inLimit)
    {
      m_operator = inOperator;
      m_limit = inLimit;
    }

    private enum Operator
    {
      ABOVE(Values.ConditionProto.Limit.Operator.ABOVE, "above", ">", "over"),
      ABOVE_OR_EQUAL(Values.ConditionProto.Limit.Operator.ABOVE_OR_EQUAL,
                     "above or equal", ">=", "over or equal"),
      EQUAL(Values.ConditionProto.Limit.Operator.EQUAL, "equal", "="),
      BELOW_OR_EQUAL(Values.ConditionProto.Limit.Operator.BELOW_OR_EQUAL,
                     "below or equal", "<=", "under or equal"),
      BELOW(Values.ConditionProto.Limit.Operator.BELOW, "below", "<", "under");

      private Operator(Values.ConditionProto.Limit.Operator inProto,
                       String ... inOperators)
      {
        m_proto = inProto;
        m_operators = inOperators;
      }

      private final String []m_operators;
      private final Values.ConditionProto.Limit.Operator m_proto;

      public static Operator fromProto(
          Values.ConditionProto.Limit.Operator inProto)
      {
        for(Operator operator : values())
          if(operator.m_proto == inProto)
            return operator;

        throw new IllegalArgumentException("invalid proto operator: "
                                           + inProto);
      }

      public Values.ConditionProto.Limit.Operator toProto()
      {
        return m_proto;
      }

      private static Operator valueOf(ParseReader inReader)
      {
        for(Operator operator : values())
          if (inReader.expectCase(operator.m_operators, false) >= 0)
            return operator;

        throw new IllegalArgumentException(
            "unknown operator detected: " + inReader.readLine());
      }

      public String toString()
      {
        return m_operators[0];
      }
    }

    private final Operator m_operator;
    private final int m_limit;

    public boolean check(int inValue)
    {
      switch(m_operator)
      {
        case ABOVE:
          return inValue > m_limit;

        case ABOVE_OR_EQUAL:
          return inValue >= m_limit;

        case EQUAL:
          return inValue == m_limit;

        case BELOW_OR_EQUAL:
          return inValue <= m_limit;

        case BELOW:
          return inValue < m_limit;

        default:
          return false;
      }
    }

    public boolean canAdd(Limit inLimit)
    {
      return canAdd(m_operator, inLimit.m_operator)
          && (m_operator != Operator.EQUAL
              || m_limit == inLimit.m_limit);
    }

    public Limit add(Limit inOther)
    {
      if(m_operator == inOther.m_operator)
      {
        switch(m_operator)
        {
          case ABOVE:
          case ABOVE_OR_EQUAL:
            return new Limit(m_operator, Math.min(m_limit, inOther.m_limit));

          case EQUAL:
            if (m_limit == inOther.m_limit)
              return this;

            break;

          case BELOW_OR_EQUAL:
          case BELOW:
            return new Limit(m_operator, Math.max(m_limit, inOther.m_limit));
        }

        throw new IllegalArgumentException("cannot add " + this + " to "
                                               + inOther);
      }

      switch(m_operator)
      {
        case ABOVE:
          if(inOther.m_operator == Operator.ABOVE_OR_EQUAL)
            if(m_limit > inOther.m_limit)
              return new Limit(Operator.ABOVE, m_limit);
            else
              return new Limit(Operator.ABOVE_OR_EQUAL, inOther.m_limit);

          break;

        case ABOVE_OR_EQUAL:
          if(inOther.m_operator == Operator.ABOVE)
            if(m_limit >= inOther.m_limit)
              return new Limit(Operator.ABOVE_OR_EQUAL, m_limit);
            else
              return new Limit(Operator.ABOVE, inOther.m_limit);

          break;

        case BELOW_OR_EQUAL:
          if(inOther.m_operator == Operator.BELOW)
            if(m_limit <= inOther.m_limit)
              return new Limit(Operator.BELOW_OR_EQUAL, m_limit);
            else
              return new Limit(Operator.BELOW, inOther.m_limit);

          break;

        case BELOW:
          if(inOther.m_operator == Operator.BELOW_OR_EQUAL)
            if(m_limit < inOther.m_limit)
              return new Limit(Operator.BELOW, m_limit);
            else
              return new Limit(Operator.BELOW_OR_EQUAL, inOther.m_limit);

          break;
      }

      throw new IllegalArgumentException("cannot add " + this + " to "
                                         + inOther);
    }

    public static Limit fromProto(Values.ConditionProto.Limit inLimit)
    {
      return new Limit(Operator.fromProto(inLimit.getOperator()),
                       inLimit.getValue());
    }

    public Values.ConditionProto.Limit toProto()
    {
      return Values.ConditionProto.Limit.newBuilder()
          .setOperator(m_operator.toProto())
          .setValue(m_limit)
          .build();
    }

    private boolean canAdd(Operator inFirst, Operator inSecond)
    {
      if(inFirst == inSecond)
        return true;

      switch(inFirst)
      {
        case ABOVE:
          return inSecond == Operator.ABOVE_OR_EQUAL;

        case ABOVE_OR_EQUAL:
          return inSecond == Operator.ABOVE;

        case BELOW_OR_EQUAL:
          return inSecond == Operator.BELOW;

        case BELOW:
          return inSecond == Operator.BELOW_OR_EQUAL;
      }

      return false;
    }

    private static Optional<Limit> parse(ParseReader reader)
    {
      try
      {
        return Optional.of(
            new Limit(Operator.valueOf(reader), reader.readInt()));
      }
      catch(IllegalArgumentException|ReadException e) {
        return Optional.absent();
      }
    }

    public String toString()
    {
      return m_operator.toString() + " " + m_limit;
    }
  }

  public Condition(String inGeneric)
  {
    this(Optional.of(inGeneric), WeaponStyle.UNKNOWN, Ability.UNKNOWN,
         Optional.<Limit>absent());
  }

  public Condition(WeaponStyle inStyle)
  {
    this(Optional.<String>absent(), inStyle, Ability.UNKNOWN,
         Optional.<Limit>absent());
  }

  public Condition(Ability inAbility, Limit inLimit)
  {
    this(Optional.<String>absent(), WeaponStyle.UNKNOWN,
         inAbility, Optional.of(inLimit));
  }

  private Condition(Optional<String> inGeneric, WeaponStyle inStyle,
                    Ability inAbility,
                    Optional<Limit> inLimit)
  {
    m_generic = inGeneric;
    m_style = inStyle;
    m_ability = inAbility;
    m_limit = inLimit;
  }

  private Optional<String> m_generic;
  private WeaponStyle m_style;
  private Ability m_ability;
  private Optional<Limit> m_limit;

  /** The parser for conditions. */
  public static final Parser<Condition> PARSER =
      new Parser<Condition>(1)
      {
        @Override
        public Optional<Condition> doParse(String inText)
        {
          ParseReader reader =
              new ParseReader(new StringReader(inText), "condition");
          Optional<String> ability =
              reader.expectCase(Ability.allNames(), true);
          if(ability.isPresent())
            return Optional.of(parseAbility(ability.get(), reader));

          Optional<String> weaponStyle =
              reader.expectCase(WeaponStyle.names(), false);
          if(weaponStyle.isPresent())
          {
            Optional<WeaponStyle> style =
                WeaponStyle.fromString(weaponStyle.get());
            if(style.isPresent())
              return Optional.of(new Condition(style.get()));
          }

          return Optional.of(new Condition(inText));
        }

        private Condition parseAbility(String inAbility,
                                       ParseReader inReader)
        {
          Optional<Ability> ability = Ability.fromString(inAbility);
          Optional<Limit> limit = Limit.parse(inReader);
          if(ability.isPresent() && limit.isPresent())
            return new Condition(ability.get(), limit.get());

          return new Condition(inReader.readLine());
        }
      };

  public Optional<String> getGeneric()
  {
    return m_generic;
  }

  public WeaponStyle getWeaponStyle()
  {
    return m_style;
  }

  public Ability getAbilitty()
  {
    return m_ability;
  }

  public Optional<Limit> getLimit()
  {
    return m_limit;
  }

  public Optional<Boolean> check()
  {
    if(isEmpty())
      return Optional.of(true);

    return Optional.absent();
  }

  public Optional<Boolean> check(Item inItem)
  {
    if(isEmpty())
      return Optional.of(true);

    Optional<WeaponStyle> style = inItem.getCombinedWeaponStyle().get();
    if(style.isPresent() && style.get() == m_style)
      return Optional.of(true);

    return Optional.absent();
  }

  public Optional<Boolean> check(Monster inMonster)
  {
    if(isEmpty())
      return Optional.of(true);

    if(m_limit.isPresent() && m_ability != Ability.UNKNOWN)
      return Optional.of(m_limit.get().check(inMonster.ability(m_ability)));

    return Optional.absent();
  }

  private boolean isEmpty()
  {
    return m_ability == Ability.UNKNOWN
        && m_style == WeaponStyle.UNKNOWN
        && (!m_generic.isPresent() || m_generic.get().isEmpty());
  }

  @Override
  public Arithmetic<Values.ConditionProto> add(
      Arithmetic<Values.ConditionProto> inValue)
  {
    Condition value = (Condition)inValue;
    return new Condition(addStrings(m_generic, value.m_generic),
                         addStyles(m_style, value.m_style),
                         addAbilities(m_ability, value.m_ability),
                         addLimits(m_limit, value.m_limit));
  }

  @Override
  public boolean canAdd(Arithmetic<Values.ConditionProto> inValue)
  {
    if(!(inValue instanceof Condition))
      return false;

    Condition value = (Condition)inValue;
    return (value.m_style == WeaponStyle.UNKNOWN
        || m_style == WeaponStyle.UNKNOWN
        || m_style == value.m_style)
        && (value.m_ability == Ability.UNKNOWN
            || m_ability == Ability.UNKNOWN
            || (m_ability == value.m_ability
                && m_limit.get().canAdd(value.m_limit.get())));
  }

  @Override
  public Arithmetic<Values.ConditionProto> multiply(int inFactor)
  {
    return this;
  }

  @Override
  public Arithmetic<Values.ConditionProto> multiply(Rational inFactor)
  {
    return this;
  }

  public static Condition fromProto(Values.ConditionProto inProto)
  {
    if(inProto.hasWeaponStyle())
      return new Condition(WeaponStyle.fromProto(inProto.getWeaponStyle()));

    if(inProto.hasAbility() && inProto.hasLimit())
      return new Condition(Ability.fromProto(inProto.getAbility()),
                           Limit.fromProto(inProto.getLimit()));

    return new Condition(inProto.getGeneric());
  }

  @Override
  public Values.ConditionProto toProto()
  {
    Values.ConditionProto.Builder proto = Values.ConditionProto.newBuilder();

    if(m_generic.isPresent())
      proto.setGeneric(m_generic.get());

    if(m_style != WeaponStyle.UNKNOWN)
      proto.setWeaponStyle(m_style.toProto());

    if(m_ability != Ability.UNKNOWN && m_limit.isPresent())
    {
      proto.setAbility(m_ability.toProto());
      proto.setLimit(m_limit.get().toProto());
    }

    return proto.build();
  }

  @Override
  public boolean equals(Object inOther)
  {
    if(this == inOther)
      return true;

    if(inOther == null || getClass() != inOther.getClass())
      return false;

    final Condition condition = (Condition)inOther;

    if(!m_generic.equals(condition.m_generic))
      return false;

    if(m_style != condition.m_style)
      return false;

    if(m_ability != condition.m_ability)
      return false;

    return m_limit.equals(condition.m_limit);
  }

  @Override
  public int hashCode()
  {
    int result = m_generic.hashCode();
    result = 31 * result + m_style.hashCode();
    result = 31 * result + m_ability.hashCode();
    result = 31 * result + m_limit.hashCode();

    return result;
  }

  private Optional<String> addStrings(Optional<String> inFirst,
                                      Optional<String> inSecond)
  {
    if(!inFirst.isPresent())
      return inSecond;

    if(!inSecond.isPresent())
      return inFirst;

    return Optional.of(inFirst.get() + " " + inSecond.get());
  }

  private WeaponStyle addStyles(WeaponStyle inFirst, WeaponStyle inSecond)
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

  private Ability addAbilities(Ability inFirst, Ability inSecond)
  {
    if(inFirst == inSecond)
      return inFirst;

    if(inFirst == Ability.UNKNOWN)
      return inSecond;

    if(inSecond == Ability.UNKNOWN)
      return inFirst;

    throw new IllegalArgumentException(
        "Cannot add conditions with different styles");
  }

  private Optional<Limit> addLimits(Optional<Limit> inFirst,
                                    Optional<Limit> inSecond)
  {
    if(!inFirst.isPresent())
      return inSecond;

    if(!inSecond.isPresent())
      return inFirst;

    return Optional.of(inFirst.get().add(inSecond.get()));
  }

  @Override
  public String toString()
  {
    if(m_generic.isPresent())
      return m_generic.get();

    if(m_style != WeaponStyle.UNKNOWN)
      return m_style.toString();

    if(m_ability != Ability.UNKNOWN && m_limit.isPresent())
      return m_ability + " " + m_limit.get();

    return "";
  }

  public static Optional<Condition> parse(Optional<String> inText)
  {
    if(inText.isPresent())
      return parse(inText.get());

    return Optional.absent();
  }

  public static Optional<Condition> parse(String inText)
  {
    return PARSER.parse(inText);
  }
}
