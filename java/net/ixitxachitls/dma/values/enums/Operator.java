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

package net.ixitxachitls.dma.values.enums;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Values.ExpressionProto;

/**
 * An operator in an expression.
 *
 * @file Operator.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public enum Operator implements Proto<ExpressionProto.Operator>
{
  /** No operator. */
  NONE("", ExpressionProto.Operator.NONE, false),

  /** Addition. */
  ADD("+", ExpressionProto.Operator.ADD, false),

  /** Subtraction. */
  SUBTRACT("-", ExpressionProto.Operator.SUBTRACT, false),

  /** Multiplication. */
  MULTIPLY("*", ExpressionProto.Operator.MULTIPLY, false),

  /** Division. */
  DIVIDE("/", ExpressionProto.Operator.DIVIDE, false),

  /** Modulo compuation. */
  MODULO("%", ExpressionProto.Operator.MODULO, false),

  /** Minimal value. */
  MIN("min", ExpressionProto.Operator.MIN, true),

  /** Maximal value. */
  MAX("max", ExpressionProto.Operator.MAX, true);

  /**
   * Create the operator.
   *
   * @param inMarkup the text to print the operator
   * @param inProto the proto representation
   * @param inPrefix whether this is a prefix operator
   */
  private Operator(String inMarkup, ExpressionProto.Operator inProto,
                   boolean inPrefix)
  {
    m_markup = inMarkup;
    m_proto = inProto;
    m_prefix = inPrefix;
  }

  /** The text to print the operator. */
  private final String m_markup;

  /** The proto value to store the operator. */
  private final ExpressionProto.Operator m_proto;

  /** Whether this is a prefix operator. */
  private final boolean m_prefix;

  /** All the prefixed operators. */
  private static final List<String> s_prefixed = new ArrayList<>();

  /** All the infix operators. */
  private static final List<String> s_infixed = new ArrayList<>();

  /**
   * Get the text used to print the operator.
   *
   * @return the text markup
   */
  public String getMarkup()
  {
    return m_markup;
  }

  /**
   * Return whether this is a prefixed operator.
   *
   * @return true for prefix, false for infix
   */
  public boolean isPrefixed()
  {
    return m_prefix;
  }

  /** Initialize the operator values. */
  private static void init()
  {
    if(!s_prefixed.isEmpty() || !s_infixed.isEmpty())
      return;

    for(Operator operator : values())
    if(operator.isPrefixed())
      s_prefixed.add(operator.getMarkup());
    else
      s_infixed.add(operator.getMarkup());
  }

  /**
   * Get all the prefixed operators.
   *
   * @return the list of prefix operators
   */
  public static List<String> prefixed()
  {
    init();
    return s_prefixed;
  }

  /**
   * Get all the infixed operators.
   *
   * @return the list of infix operators.
   */
  public static List<String> infixed()
  {
    init();
    return s_infixed;
  }

  @Override
  public ExpressionProto.Operator toProto()
  {
    return m_proto;
  }

  /**
   * Get the operator corresponding to the given proto value.
   *
   * @param inProto the proto representation
   * @return the matching operator
   */
  public static Operator fromProto(ExpressionProto.Operator inProto)
  {
    for(Operator operator : values())
      if(operator.m_proto == inProto)
        return operator;

    throw new IllegalStateException("cannot convert operator proto: "
      + inProto);
  }

  /**
   * Convert the given string to the operator with the same markup.
   *
   * @param inValue the string representation
   * @return the corresponding operator, if one matches
   */
  public static Optional<Operator> fromString(String inValue)
  {
    String value = inValue.trim();
    for(Operator operator : values())
      if(operator.getMarkup().equals(value))
        return Optional.of(operator);

    return Optional.absent();
  }
}
