/******************************************************************************
 * Copyright (c) 2002-2007 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.values.formatters;

import javax.annotation.Nonnull;

import net.ixitxachitls.output.commands.Command;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The interface for all formatters.
 *
 * @file          Formatter.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <K> the type of value to be formatted
 *
 */

//..........................................................................

//__________________________________________________________________________

public interface Formatter<K>
{
  //-------------------------------------------------------------- accessors

  //-------------------------------- format --------------------------------

  /**
   * Format the given value into a command.
   *
   * @param       inValue the value to format
   *
   * @return      the formatted command
   *
   */
  public @Nonnull Command format(@Nonnull K inValue);

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
