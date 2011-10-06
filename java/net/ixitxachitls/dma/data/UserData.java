/******************************************************************************
 * Copyright (c) 2002-2011 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.Product;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * Data for user specific information.
 *
 *
 * @file          UserData.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class UserData extends DMAData
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- UserData -------------------------------

  /**
   * Create the user data repository with the given files.
   *
   * @param       inUser     the user the data is for
   * @param       inPath     the base path to all files
   * @param       inBaseData the base data
   * @param       inFiles    the default files to read data from
   *
   */
  public UserData(@Nonnull BaseCharacter inUser, @Nonnull String inPath,
                  @Nonnull DMAData inBaseData, @Nullable String ... inFiles)
  {
    super(inPath, inFiles);

    m_user = inUser;
    m_base = inBaseData;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The user for this data. */
  private @Nonnull BaseCharacter m_user;

  /** All the available base data. */
  private @Nonnull DMAData m_base;

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- read ---------------------------------

  /**
   * Read all the files associated, if not yet read.
   *
   * @return true if all files could be read without error, false if there were
   *         errors
   *
   */
  public boolean read()
  {
    boolean result = super.read();

    // Associate all products with the user
    for(Product product : getEntries(Product.TYPE).values())
      product.setOwner(m_user);

    return result;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  // TODO: should write some tests at some time

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}