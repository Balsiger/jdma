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

import net.ixitxachitls.dma.proto.Entries.BaseItemProto;
import net.ixitxachitls.dma.values.enums.*;

/** The special size modifiers for monsters. */
public enum SizeModifier implements Named, net.ixitxachitls.dma.values.enums.Short,
    Proto<BaseItemProto.SizeModifier>
{
  /** An unknown size modifier. */
  UNKNOWN("Unknown", "U", BaseItemProto.SizeModifier.UNKNOWN_SIZE_MODIFIER),

  /** A taller than longer monster. */
  TALL("tall", "T", BaseItemProto.SizeModifier.TALL),

  /** A longer than taller monster. */
  LONG("long", "L", BaseItemProto.SizeModifier.LONG);

  /** The value's name. */
  private String m_name;

  /** The value's short name. */
  private String m_short;

  /** The proto enum value. */
  private BaseItemProto.SizeModifier m_proto;

  /** The parser for size modifiers. */
  public static final NewValue.Parser<SizeModifier> PARSER =
    new NewValue.Parser<SizeModifier>(1)
    {
      @Override
      public Optional<SizeModifier> doParse(String inValue)
      {
        return SizeModifier.fromString(inValue);
      }
    };

  /** Create the name.
   *
   * @param inName  the name of the value
   * @param inShort the short name of the value
   * @param inProto the proto enum value
   */
  private SizeModifier(String inName, String inShort,
                       BaseItemProto.SizeModifier inProto)
  {
    m_name  = inName;
    m_short = inShort;
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
  public String getShort()
  {
    return m_short;
  }

  @Override
  public BaseItemProto.SizeModifier toProto()
  {
    return m_proto;
  }

  /**
   * Convert the given proto enum to the corresponding enum value.
   *
   * @param inProto the proto enum value
   * @return the corresponding enum vbalue
   */
  public static SizeModifier fromProto(BaseItemProto.SizeModifier inProto)
  {
    for(SizeModifier modifier : values())
      if(modifier.m_proto == inProto)
        return modifier;

    throw new IllegalArgumentException("cannot convert size modifier: "
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

    for(SizeModifier modifier : values())
      names.add(modifier.getName());

    return names;
  }

  /**
   * Get the layout matching the given text.
   */
  public static Optional<SizeModifier> fromString(String inText)
  {
    for(SizeModifier modifier : values())
      if(modifier.m_name.equalsIgnoreCase(inText))
        return Optional.of(modifier);

    return Optional.absent();
  }
}