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

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Entries.ProductProto;

/** The product status. */
public enum ProductStatus implements EnumSelection.Named,
  EnumSelection.Proto<ProductProto.Status>
{
  /** The unknown status. */
  UNKNOWN("unknown", ProductProto.Status.UNKNOWN),
  /** The product is available in the library. */
  AVAILABLE("available", ProductProto.Status.AVAILABLE),
  /** A highly desired product. */
  DESIRED1("desired 1", ProductProto.Status.DESIRED_1),
  /** A desired product. */
  DESIRED2("desired 2", ProductProto.Status.DESIRED_2),
  /** A marginally desired product. */
  DESIRED3("desired 3", ProductProto.Status.DESIRED_3);

  /** The value's name. */
  private String m_name;

  /** The proto enum value. */
  private ProductProto.Status m_proto;

  /** The parser for status values. */
  public static final NewValue.Parser<ProductStatus> PARSER =
    new NewValue.Parser<ProductStatus>(1)
    {
      @Override
      public Optional<ProductStatus> doParse(String inValue)
      {
        return ProductStatus.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName     the name of the value
   * @param inProto    the proto enum value
   */
  private ProductStatus(String inName, ProductProto.Status inProto)
  {
    m_name = inName;
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
  public ProductProto.Status toProto()
  {
    return m_proto;
  }

  /**
   * Convert the proto value into the corresponding enum value.
   *
   * @param inProto the proto to convert
   * @return the corresponding enum value
   */
  public static ProductStatus fromProto(ProductProto.Status inProto)
  {
    for(ProductStatus status : values())
      if(status.m_proto == inProto)
        return status;

    throw new IllegalArgumentException("cannot convert status proto: "
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

    for(ProductStatus status : values())
      names.add(status.getName());

    return names;
  }

  /**
   * Get the substance matching the given text.
   */
  public static Optional<ProductStatus> fromString(String inText)
  {
    for(ProductStatus status : values())
      if(status.m_name.equalsIgnoreCase(inText))
        return Optional.of(status);

    return Optional.absent();
  }
}