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

import net.ixitxachitls.dma.entries.Product;
import net.ixitxachitls.dma.proto.Entries.ProductProto;
import net.ixitxachitls.dma.values.Value;

/** The product condition. */
public enum ProductCondition implements Named,
    Proto<ProductProto.Condition>
{
  /** The unknown condition. */
  UNKNOWN("unknown", ProductProto.Condition.UNKNOWN_CONDITION),
  /** The product is as good as new and has not been or only carefully
   * read. */
  MINT("mint", ProductProto.Condition.MINT),
  /** The product is in good shape but was read. */
  GOOD("good", ProductProto.Condition.GOOD),
  /** The product is used, but in good shape. Might have some pencil marks
   * or the like. */
  USED("used", ProductProto.Condition.USED),
  /** The product is usable in play but might not look too nice. */
  USABLE("usable", ProductProto.Condition.USABLE),
  /** Some part of the product is missing. */
  PARTIAL("partial", ProductProto.Condition.PARTIAL),
  /** The product is not really usable. */
  CRAP("crap", ProductProto.Condition.CRAP),
  /** Nothing defined. */
  none("none", ProductProto.Condition.NONE);

  /** The value's name. */
  private String m_name;

  /** The enum proto value. */
  private ProductProto.Condition m_proto;

  /** The parser for condition values. */
  public static final Value.Parser<ProductCondition> PARSER =
    new Value.Parser<ProductCondition>(1)
    {
      @Override
      public Optional<ProductCondition> doParse(String inValue)
      {
        return ProductCondition.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName     the name of the value
   * @param inProto    the proto value
   */
  private ProductCondition(String inName, ProductProto.Condition inProto)
  {
    m_name = Product.constant("product.condition", inName);
    m_proto = inProto;
  }

  @Override
  public String getName()
  {
    return m_name;
  }

  @Override
  public String toString()
  {
    return m_name;
  }

  @Override
  public ProductProto.Condition toProto()
  {
    return m_proto;
  }

  public static ProductCondition fromProto(ProductProto.Condition inProto)
  {
    for(ProductCondition condition : values())
      if(condition.m_proto == inProto)
        return condition;

    throw new IllegalArgumentException("cannot convert condition: "
                                       + inProto);
  }

  /**
   * All the possible names for the layout.
   *
   * @return the possible names
   */
  public static List<String> names()
  {
    List<String> names = new ArrayList<>();

    for(ProductCondition condition : values())
      names.add(condition.getName());

    return names;
  }

  /**
   * Get the substance matching the given text.
   */
  public static Optional<ProductCondition> fromString(String inText)
  {
    for(ProductCondition condition : values())
      if(condition.m_name.equalsIgnoreCase(inText))
        return Optional.of(condition);

    return Optional.absent();
  }
}