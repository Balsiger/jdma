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

import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto;
import net.ixitxachitls.dma.proto.Entries.BaseMonsterProto.Attack.Mode;
import net.ixitxachitls.dma.values.Parser;

/**
 * The possible attack styles in the game.
 *
 * @file AttackMode.java
 * @author balsiger@ixitxahitls.net (Peter Balsiger)
 */
public enum AttackMode implements Named,
    Proto<Mode>
{
  /** Unknown. */
  UNKNOWN("Unknown", false, BaseMonsterProto.Attack.Mode.UNKNOWN_MODE),

  /** A tentacle attack. */
  TENTACLE("Tentacle", false, BaseMonsterProto.Attack.Mode.TENTACLE),

  /** A claw attack. */
  CLAW("Claw", false, BaseMonsterProto.Attack.Mode.CLAW),

  /** A bite attack. */
  BITE("bite", false, BaseMonsterProto.Attack.Mode.BITE),

  /** A fist attack. */
  FIST("Fist", false, BaseMonsterProto.Attack.Mode.FIST),

  /** A hoof attack. */
  HOOF("Hoof", true, BaseMonsterProto.Attack.Mode.HOOF),

  /** An incorporeal touch attack. */
  INCORPOREAL_TOUCH("Incorporeal Touch", true,
                    BaseMonsterProto.Attack.Mode.INCORPOREAL_TOUCH),

  /** A quill attack. */
  QUILL("Quill", true, BaseMonsterProto.Attack.Mode.QUILL),

  /** A ray attack. */
  RAY("Ray", true, BaseMonsterProto.Attack.Mode.RAY),

  /** A slam attack. */
  SLAM("Slam", false, BaseMonsterProto.Attack.Mode.SLAM),

  /** A snakes attack. */
  SNAKES("Snakes", true, BaseMonsterProto.Attack.Mode.SNAKES),

  /** A sting attack. */
  STING("Sting", false, BaseMonsterProto.Attack.Mode.STING),

  /** A touch attack. */
  /** A swarm attack. */
  SWARM("Swarm", false, BaseMonsterProto.Attack.Mode.SWARM),

  TOUCH("Touch", true, BaseMonsterProto.Attack.Mode.TOUCH),

  /** A weapon attack. */
  WEAPON("Weapon", false, BaseMonsterProto.Attack.Mode.WEAPON),

  /** A web attack. */
  WEB("Web", true, BaseMonsterProto.Attack.Mode.WEB);

  /** The value's name. */
  private String m_name;

  /** Flag if to use dexterity when attacking. */
  private boolean m_dexterity;

  /** The proto enum value. */
  private BaseMonsterProto.Attack.Mode m_proto;

  /** The parser for armor types. */
  public static final Parser<AttackMode> PARSER =
    new Parser<AttackMode>(1)
    {
      @Override
      public Optional<AttackMode> doParse(String inValue)
      {
        return AttackMode.fromString(inValue);
      }
    };

  /**
   * Create the name.
   *
   * @param inName       the name of the value
   * @param inDexterity  whether dexterity is used for the attack
   * @param inProto      the proto enum value
   */
  private AttackMode(String inName, boolean inDexterity,
                     BaseMonsterProto.Attack.Mode inProto)
  {
    m_name = inName;
    m_dexterity = inDexterity;
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

  /**
   * Check whether to use dexterity for attacking.
   *
   * @return true if using dexterity for attacking
   */
  public boolean useDexterity()
  {
    return m_dexterity;
  }

  @Override
  public Mode toProto()
  {
    return m_proto;
  }

  /**
   * Convert the proto value to an enum value.
   *
   * @param inProto  the proto value to convert
   * @return the corresponding enum value
   */
  public static AttackMode fromProto(BaseMonsterProto.Attack.Mode inProto)
  {
    for(AttackMode mode : values())
      if(mode.m_proto == inProto)
        return mode;

    throw new IllegalArgumentException("cannot convert attack mode: "
                                       + inProto);
  }

  /**
   * Get the armor type from the given string.
   *
   * @param inValue the string representation
   * @return the matching type, if any
   */
  public static Optional<AttackMode> fromString(String inValue)
  {
    for(AttackMode mode : values())
      if(mode.getName().equalsIgnoreCase(inValue))
        return Optional.of(mode);

    return Optional.absent();
  }

  /**
   * Get the possible names of types.
   *
   * @return a list of the names
   */
  public static List<String> names()
  {
    List<String> names = new ArrayList<>();
    for(AttackMode mode : values())
      names.add(mode.getName());

    return names;
  }
}
