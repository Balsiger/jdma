/*****************************************************************************
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
import net.ixitxachitls.dma.values.Damage;
import net.ixitxachitls.dma.values.Dice;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Parameters;
import net.ixitxachitls.dma.values.Reference;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.conditions.Condition;

/**
 * An entry representing a base character level.
 *
 * @file   BaseLevel.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
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

  /** Special attacks. */
  @Key("special attacks")
  @WithBases
  protected ValueList<Multiple> m_specialAttacks =
  new ValueList<Multiple>
  (", ",
    new Multiple(new Multiple.Element []
      {
      new Multiple.Element(new Number(1, 100).withEditType("number[level]"),
                           false),
      new Multiple.Element
        (new Reference<BaseQuality>(BaseQuality.TYPE)
         .withParameter("Range", new Distance(), Parameters.Type.MAX)
         .withParameter("Increment", new Distance(), Parameters.Type.MAX)
         .withParameter("Name", new Name(), Parameters.Type.UNIQUE)
         .withParameter("Summary", new Name(), Parameters.Type.ADD)
         .withParameter("Level", new Number(0, 100), Parameters.Type.ADD)
         .withParameter("SpellLevel", new Number(0, 100), Parameters.Type.ADD)
         .withParameter("Value", new Number(1, 100), Parameters.Type.ADD)
         .withParameter("Modifier", new Modifier(), Parameters.Type.ADD)
         .withParameter("Dice", new Dice(), Parameters.Type.ADD)
         .withParameter("Times", new Number(1, 100), Parameters.Type.ADD)
         .withParameter("Class", new EnumSelection<BaseSpell.SpellClass>
                        (BaseSpell.SpellClass.class), Parameters.Type.ADD)
         .withParameter("Ability", new Number(0, 100), Parameters.Type.MAX)
         .withParameter("Type", new Name(), Parameters.Type.UNIQUE)
         .withParameter("Duration", new Name(), Parameters.Type.ADD)
         .withParameter("Initial", new Name(), Parameters.Type.UNIQUE)
         .withParameter("Secondary", new Name(), Parameters.Type.UNIQUE)
         .withParameter("Damage", new Damage(), Parameters.Type.ADD)
         .withParameter("Incubation", new Name(), Parameters.Type.MIN)
         .withParameter("DC", new Number(1, 100), Parameters.Type.MAX)
         .withParameter("HP", new Number(1, 1000), Parameters.Type.MAX)
         .withParameter("Burst", new Number(1, 100), Parameters.Type.MAX)
         .withParameter("Str", new Number(-100, 100), Parameters.Type.ADD)
         .withParameter("Dex", new Number(-100, 100), Parameters.Type.ADD)
         .withParameter("Con", new Number(-100, 100), Parameters.Type.ADD)
         .withParameter("Wis", new Number(-100, 100), Parameters.Type.ADD)
         .withParameter("Int", new Number(-100, 100), Parameters.Type.ADD)
         .withParameter("Cha", new Number(-100, 100), Parameters.Type.ADD)
         .withTemplate("reference", "/quality/"), false),
      new Multiple.Element(new Number(1, 100)
        .withEditType("name[per day]"), true, "/", null)
      }));

  /** Special qualities. */
  @Key("special qualities")
  protected ValueList<Multiple> m_specialQualities =
  new ValueList<Multiple>
  (", ",
    new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new Number(1, 100).withEditType("number[level]"),
                           false, null, ": "),
      new Multiple.Element
        (new Reference<BaseQuality>(BaseQuality.TYPE)
         .withParameter("Range", new Distance(), Parameters.Type.MAX)
         .withParameter("Name", new Name(), Parameters.Type.UNIQUE)
         .withParameter("Summary", new Name(), Parameters.Type.ADD)
         .withParameter("Level", new Number(0, 100), Parameters.Type.ADD)
         .withParameter("SpellLevel", new Number(0, 100), Parameters.Type.ADD)
         .withParameter("Racial",
                        new Number(-50, 50, true), Parameters.Type.ADD)
         .withParameter("Value", new Number(0, 100), Parameters.Type.ADD)
         .withParameter("Modifier", new Modifier(), Parameters.Type.ADD)
         .withTemplate("reference", "/quality/"), false),
      new Multiple.Element(new Condition().withEditType("string[condition]"),
                           true, " :if ", null),
      new Multiple.Element(new Number(1, 100).withEditType("name[per day]"),
                           true, "/", null),
    }));

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

