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


package net.ixitxachitls.dma.entries;

import javax.annotation.Nullable;

import net.ixitxachitls.dma.entries.extensions.BaseArmor;
import net.ixitxachitls.dma.entries.extensions.BaseIncomplete;
import net.ixitxachitls.dma.entries.extensions.BaseWeapon;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Reference;
import net.ixitxachitls.dma.values.ValueList;

/**
 * An entry representing a base character level.
 *
 * @file   BaseLevel.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */
public class BaseLevel extends BaseEntry
{
  /**
   * Create the base level.
   */
  public BaseLevel()
  {
    super(TYPE);
  }

  /**
   * Create the base level with the given name.
   *
   * @param inName the name of the level
   */
  public BaseLevel(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final BaseType<BaseLevel> TYPE =
    new BaseType<>(BaseLevel.class);

    /** Serialize version id. */
  private static final long serialVersionUID = 1L;

  /** The xp per level. */
  @Key("xp")
  protected ValueList<Number> m_xp =
    new ValueList<>(new Number(0, 1_000_000));

  /** The level at which to gain a feat. */
  @Key("feat levels")
  protected ValueList<Number> m_featLevels =
    new ValueList<>(new Number(0, 100));

  /** The level at which to gain an ability increase. */
  @Key("ability levels")
  protected ValueList<Number> m_abilityLevels =
    new ValueList<>(new Number(0, 100));

  /** Skill points per level (x4 at first level, +Int modifier). */
  @Key("skill points")
  protected Number m_skillPoints = new Number(1, 10);

  /** The class skills. */
  @Key("class skills")
  protected ValueList<Reference<BaseSkill>> m_classSkill =
    new ValueList<>(new Reference<BaseSkill>(BaseSkill.TYPE));

  /** The weapon proficiencies. */
  @Key("weapon proficiencies")
  protected ValueList<EnumSelection<BaseWeapon.Proficiency>>
    m_weaponProficiencies =
      new ValueList<>(new EnumSelection<BaseWeapon.Proficiency>
        (BaseWeapon.Proficiency.class));

  /** The weapon proficiencies. */
  @Key("armor proficiencies")
  protected ValueList<EnumSelection<BaseArmor.Proficiency>>
    m_armorProficiencies =
      new ValueList<>(new EnumSelection<BaseArmor.Proficiency>
        (BaseArmor.Proficiency.class));

  static
  {
    extractVariables(BaseLevel.class);
    extractVariables(BaseLevel.class, BaseIncomplete.class);
      }

  @Override
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    if(inUser == null)
      return false;

    return inUser.hasAccess(BaseCharacter.Group.DM);
  }
}
