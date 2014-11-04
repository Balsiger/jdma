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

package net.ixitxachitls.util.logging;

/**
 * This is the interface that has to be implemented by all loggers.
 *
 * @file          Logger.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

public interface Logger extends AutoCloseable
{
  /**
    * Print the given message.
    *
    * @param       inText the text to print
    * @param       inType the logging level to print
    */
  void print(String inText, Log.Type inType);

  /**
    * Print the given object.
    *
    * @param       inObject the object to print
    * @param       inType   the type (level) printing
    */
  void print(Object inObject, Log.Type inType);

  @Override
  void close(); // Just here to ensure this does not throw any exception.
}
