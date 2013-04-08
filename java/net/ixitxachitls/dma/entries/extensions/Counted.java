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

import javax.annotation.ParametersAreNonnullByDefault;

import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.Number;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the counted extension for all the entries.
 *
 * @file          Counted.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class Counted extends Extension<Item>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Counted ------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry attached to
   * @param       inName the name of the extension
   *
   */
  public Counted(Item inEntry, String inName)
  {
    super(inEntry, inName);

    init();
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  //----- count ------------------------------------------------------------

  /** The amount of units of this counted. */
  @Key("count")
  protected Number m_count = new Number(0, 10000);

  //........................................................................

  static
  {
    extractVariables(Item.class, Counted.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled.
   *
   */
  @Override
  public void complete()
  {
    super.complete();

    init();
  }

  //........................................................................
  //--------------------------------- init ---------------------------------

  /**
   * Initialize the count if it is not yet set.
   *
   */
  private void init()
  {
    if(!m_count.isDefined())
    {
      Combined<Number> combinedCount = m_entry.collect("count");
      Number total = combinedCount.total();
      if(total != null)
      {
        m_count = m_count.as(total.get());
        changed();
      }
    }
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
