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

import javax.annotation.Nonnull;

import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.Expression;
import net.ixitxachitls.dma.values.Value;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the multiple extension for all the entries.
 *
 * @file          Multiple.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Multiple extends Counted
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Multiple ------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry attached to
   * @param       inName the name of the extension
   *
   */
  public Multiple(@Nonnull Item inEntry, @Nonnull String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //------------------------------- Multiple ------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   */
  // public Multiple(Item inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  static
  {
    extractVariables(Item.class, Multiple.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------- adjustCombination ---------------------------

  /**
   * Adjust the value for the given name for any special properites.
   *
   * @param       inName        the name of the value to adjust
   * @param       ioCombination the combinstaion to adjust
   * @param       <V>           the real type of value being adjusted
   *
   */
  @Override
  public <V extends Value> void
            adjustCombination(@Nonnull String inName,
                              Combination<V> ioCombination)
  {
    if("value".equals(inName) || "weight".equals(inName))
      ioCombination.add(new Expression.Factor(m_count.get(), 1), this);

    super.adjustCombination(inName, ioCombination);
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
