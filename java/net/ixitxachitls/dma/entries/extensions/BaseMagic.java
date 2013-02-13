/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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

//------------------------------------------------------------------ imports

package net.ixitxachitls.dma.entries.extensions;

import java.util.List;
import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.BaseMonster;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.Contribution;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Group;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Percent;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.formatters.Formatter;
import net.ixitxachitls.dma.values.formatters.LinkFormatter;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the magic extension for all the entries.
 *
 * @file          BaseMagic.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BaseMagic extends BaseExtension<BaseItem>
{
  //----------------------------------------------------------------- nested
  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------- BaseMagic -----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry associated with this extension
   * @param       inName  the name of the extension
   *
   */
  public BaseMagic(BaseItem inEntry, String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //------------------------------- BaseMagic -----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry associated with this extension
   * @param       inTag  the tag name for this instance
   * @param       inName the name of the extension
   *
   */
  // public BaseMagic(BaseItem inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  //----- ability -----------------------------------------------------------

  /** The bonus to an ability. */
  @Key("ability")
  @DM
  protected Multiple m_ability = new Multiple
    (new Multiple.Element(new EnumSelection<BaseMonster.Ability>
                          (BaseMonster.Ability.class), false),
     new Multiple.Element(new Modifier(), false));

  //........................................................................
  //----- modifier ---------------------------------------------------------

  /** A general modifier to a value. */
  @Key("modifier")
  @DM
  protected ValueList<Multiple> m_modifier = new ValueList<Multiple>
    (", ", new Multiple
     (new Multiple.Element(new Name(), false),
      new Multiple.Element(new Modifier(), false, ": ", null)));

  //........................................................................


  static
  {
    extractVariables(BaseItem.class, BaseMagic.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------- computeIndexValues ---------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @param       ioValues a multi map of values per index name
   *
   */
  // @Override
  // public void computeIndexValues(Multimap<Index.Path, String> ioValues)
  // {
  //   super.computeIndexValues(ioValues);

  //   ioValues.put(Index.Path.ARMOR_BONUSES, m_bonus.group());
  //   ioValues.put(Index.Path.ARMOR_TYPES, m_type.group());
  //   ioValues.put(Index.Path.MAX_DEXTERITIES, m_maxDex.group());
  //   ioValues.put(Index.Path.CHECK_PENALTIES, m_checkPenalty.group());
  //   ioValues.put(Index.Path.ARCANE_FAILURES, m_arcane.group());

  //   ioValues.put(Index.Path.SPEEDS, m_speed.get(0).group());
  //   ioValues.put(Index.Path.SPEEDS, m_speed.get(1).group());
  // }

  //........................................................................
  //------------------------------ collect ---------------------------------

  /**
   * Add current contributions to the given list.
   *
   * @param       inName     the name of the value to contribute to
   * @param       ioCombined the list of contributions collected
   *
   */
  @SuppressWarnings("unchecked")
  @Override
  public void collect(String inName, Combined ioCombined)
  {
    super.collect(inName, ioCombined);

    if("dexterity".equals(inName))
    {
      if(((EnumSelection<BaseMonster.Ability>)m_ability.get(0)).getSelected()
         == BaseMonster.Ability.DEXTERITY)
        ioCombined.add(new Contribution<Modifier>((Modifier)m_ability.get(1),
                                                  m_entry, "magic"));
    }

    for(Multiple modifier : m_modifier)
      if(inName.equals(((Name)modifier.get(0)).get()))
        ioCombined.add(new Contribution<Modifier>((Modifier)modifier.get(1),
                                                  m_entry, "magic"));
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
