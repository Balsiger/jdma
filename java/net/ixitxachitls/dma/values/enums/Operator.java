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

public enum Operator implements Proto<ExpressionProto.Operator>
{
  NONE("", ExpressionProto.Operator.NONE, false),
  ADD("+", ExpressionProto.Operator.ADD, false),
  SUBTRACT("-", ExpressionProto.Operator.SUBTRACT, false),
  MULTIPLY("*", ExpressionProto.Operator.MULTIPLY, false),
  DIVIDE("/", ExpressionProto.Operator.DIVIDE, false),
  MODULO("%", ExpressionProto.Operator.MODULO, false),
  MIN("min", ExpressionProto.Operator.MIN, true),
  MAX("max", ExpressionProto.Operator.MAX, true);

  private Operator(String inMarkup, ExpressionProto.Operator inProto,
                   boolean inPrefix)
  {
    m_markup = inMarkup;
    m_proto = inProto;
    m_prefix = inPrefix;
  }

  private final String m_markup;
  private final ExpressionProto.Operator m_proto;
  private final boolean m_prefix;
  private static final List<String> s_prefixed = new ArrayList<>();
  private static final List<String> s_infixed = new ArrayList<>();

  public String getMarkup()
  {
    return m_markup;
  }

  public boolean isPrefixed()
  {
    return m_prefix;
  }

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

  public static List<String> prefixed()
  {
    init();
    return s_prefixed;
  }

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

  public static Operator fromProto(ExpressionProto.Operator inProto)
  {
    for(Operator operator : values())
      if(operator.m_proto == inProto)
        return operator;

    throw new IllegalStateException("cannot convert operator proto: "
      + inProto);
  }

  public static Optional<Operator> fromString(String inValue)
  {
    String value = inValue.trim();
    for(Operator operator : values())
      if(operator.getMarkup().equals(value))
        return Optional.of(operator);

    return Optional.absent();
  }
}