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

package net.ixitxachitls.util.logging;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the interface that has to be implemented by all loggers.
 *
 * @file          Logger.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public interface Logger
{
  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //-------------------------------- print ---------------------------------

  /**
    *
    * Print the given message.
    *
    * @param       inText the text to print
    * @param       inType the logging level to print
    *
    */
  public void print(String inText, Log.Type inType);

  //........................................................................
  //-------------------------------- print ---------------------------------

  /**
    *
    * Print the given object.
    *
    * @param       inObject the object to print
    * @param       inType   the type (level) printing
    *
    */
  public void print(Object inObject, Log.Type inType);

  //........................................................................

  //........................................................................
}
