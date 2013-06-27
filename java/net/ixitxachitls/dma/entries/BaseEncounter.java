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

//--------------------------------------------------------------------- Imports

package net.ixitxachitls.dma.entries;

import javax.annotation.Nullable;

import net.ixitxachitls.dma.entries.extensions.BaseIncomplete;
import net.ixitxachitls.dma.values.LongFormattedText;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.ValueList;

//.............................................................................

/**
 * A base encounter.
 *
 * @file   BaseEncounter.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class BaseEncounter extends BaseEntry
{
  /**
   * Create the encounter.
   */
  public BaseEncounter()
  {
    super(TYPE);
  }

  /**
   * Create the encounter with the given name.
   *
   * @param inName  the name of the base encounter
   */
  public BaseEncounter(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final BaseType<BaseEncounter> TYPE =
    new BaseType<>(BaseEncounter.class);

  /** The default serial version id. */
  private static final long serialVersionUID = 1L;

  /** The adventure this encounter takes places. */
  @Key("adventure")
  protected Text m_adventure = new Text();

  /** The location where this encounter takes place. */
  // TODO: long term, this should be a link to a location object
  @Key("location")
  protected ValueList<Name> m_location = new ValueList<Name>(new Name());

  /** A description of the doors. */
  @Key("doors")
  protected LongFormattedText m_doors = new LongFormattedText();

  /** A description of the floor. */
  @Key("floor")
  protected LongFormattedText m_floor = new LongFormattedText();

  /** A description of the ceiling. */
  @Key("ceiling")
  protected LongFormattedText m_ceiling = new LongFormattedText();

  /** A description of the walls. */
  @Key("walls")
  protected LongFormattedText m_walls = new LongFormattedText();

  /** A description of the temperature. */
  @Key("feel")
  protected LongFormattedText m_feel = new LongFormattedText();

  /** A description of the sounds. */
  @Key("sound")
  protected LongFormattedText m_sound = new LongFormattedText();

  /** A description of the odors. */
  @Key("smell")
  protected LongFormattedText m_smell = new LongFormattedText();

  /** A description of the odors. */
  @Key("taste")
  protected LongFormattedText m_taste = new LongFormattedText();

  /** A description of the light. */
  @Key("light")
  protected LongFormattedText m_light = new LongFormattedText();

  /** The base skills relevant for this encounter. */
  @Key("skills")
  protected ValueList<Name> m_skills = new ValueList<Name>(new Name());

  static
  {
    extractVariables(BaseEncounter.class);
    extractVariables(BaseEncounter.class, BaseIncomplete.class);
  }

  @Override
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    if(inUser == null)
      return false;

    return inUser.hasAccess(BaseCharacter.Group.DM);
  }
}
