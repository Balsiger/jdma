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

import java.io.Serializable;

import javax.annotation.ParametersAreNonnullByDefault;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A interface defining a general grouping.
 *
 * @param         <T> The type of object to be grouped.
 * @param         <G> The type grouped into.
 *
 * @file          Grouping.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public interface Grouping<T, G> extends Serializable
{
  //-------------------------------------------------------------- accessors

  //-------------------------------- group ---------------------------------

  /**
    * An interface to group similar entries together into the same ID.
    *
    * @param       inEntry the value to group
    *
    * @return      the identification of the group
    *
    */
  public G group(T inEntry);

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
