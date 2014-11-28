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

import net.ixitxachitls.dma.proto.Entries.BaseLightProto;
import net.ixitxachitls.dma.values.enums.Named;
import net.ixitxachitls.dma.values.enums.Proto;

/** The possible areas to affect (cf. PHB 175). */
public enum AreaShape implements Named,
    Proto<BaseLightProto.Light.Shape>
{
  /** An unknown shape. */
  UNKNOWN("Unknown", BaseLightProto.Light.Shape.UNKNOWN),
  /** A cone shaped area. */
  CONE("Cone", BaseLightProto.Light.Shape.CONE),
  /** A cylinder shaped area. */
  CYLINDER("Cylinder", BaseLightProto.Light.Shape.CYLINDER),
  /** An area in the form of a line. */
  LINE("Line", BaseLightProto.Light.Shape.LINE),
  /** A sphere shaped area. */
  SPHERE("Sphere", BaseLightProto.Light.Shape.SPHERE);

  /** The value's name. */
  private String m_name;

  /** The enum proto value. */
  private BaseLightProto.Light.Shape m_proto;

  /** The parser for an area shape. */
  public static final Parser<AreaShape> PARSER =
    new Parser<AreaShape>(1)
    {
      @Override
      public Optional<AreaShape> doParse(String inValue)
      {
        return AreaShape.fromString(inValue);
      }
    };

  /** Create the name.
   *
   * @param inName     the name of the value
   * @param inProto    the proto enum value
   */
  private AreaShape(String inName, BaseLightProto.Light.Shape inProto)
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
  public BaseLightProto.Light.Shape toProto()
  {
    return m_proto;
  }

  /**
   * Convert the proto value to the corresponding enum value.
   *
   *
   * @param inProto the proto value to convert
   * @return the corresponding enum value
   */
  public static AreaShape fromProto(BaseLightProto.Light.Shape inProto)
  {
    for(AreaShape area : values())
      if(area.m_proto == inProto)
        return area;

    throw new IllegalArgumentException("cannot convert area shape: "
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

    for(AreaShape shape : values())
      names.add(shape.getName());

    return names;
  }

  /**
   * Get the layout matching the given text.
   */
  public static Optional<AreaShape> fromString(String inText)
  {
    for(AreaShape shape : values())
      if(shape.m_name.equalsIgnoreCase(inText))
        return Optional.of(shape);

    return Optional.absent();
  }
}