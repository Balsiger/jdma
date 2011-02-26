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

package net.ixitxachitls.util;

import javax.annotation.Nonnull;

//..........................................................................

//------------------------------------------------------------------- header

/**
 *
 * A simple interface denoting a public clone() method.
 *
 * Creating this class as a generic class with a T clone(); method did
 * unfortunately not work. Because derivations cannot implement the same
 * generic interface but with a different type than their base classes, this
 * interface could only be implemented by the base class and is therefore not
 * really usable.
 *
 * @file          PublicCloneable.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public interface PublicCloneable extends Cloneable
{
  //-------------------------------------------------------------- accessors

  //-------------------------------- clone ---------------------------------

  /**
   * Clone the object into a completely equal one but without using the
   * same references.
   *
   * @return      the cloned object
   *
   */
  public @Nonnull Object clone();

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // interface only

  //........................................................................
}
