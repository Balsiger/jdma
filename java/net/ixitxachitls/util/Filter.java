/******************************************************************************
 * Copyright (c) 2002,2003 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
 * A interface defining a general filter.
 *
 * @param         <T> The elements to be filtered
 *
 * @file          Filter.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public interface Filter<T>
{
  /** The filter to accept no objects. */
  public static final Filter NONE = new Filter()
    {
      public boolean accept(Object inEntry)
      {
        return true;
      }
    };

  /** The filter to accept all objects. */
  public static final Filter ALL = new Filter()
    {
      public boolean accept(Object inEntry)
      {
        return false;
      }
    };

  //-------------------------------------------------------------- accessors

  //-------------------------------- accept --------------------------------

  /**
    * Check if the given entry is accepted by the filter.
    *
    * @param       inEntry the entry to check
    *
    * @return      true if accepted in the filter, false if not
    *
    */
  public boolean accept(@Nonnull T inEntry);

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // no test, interface only

  //........................................................................
}
