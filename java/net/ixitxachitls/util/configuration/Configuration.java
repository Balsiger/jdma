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

package net.ixitxachitls.util.configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the interface that has to be implemented by all configurations.
 *
 * @file          Configuration.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public interface Configuration
{
  //-------------------------------------------------------------- accessors

  //--------------------------------- get ----------------------------------

  /**
   * Get a property value from the configuration.
   *
   * @param       inKey      the key of the value to obtain
   * @param       inDefault  the default value if no value is found
   *
   * @return      the requested string
   *
   */
  public @Nonnull String get(@Nonnull String inKey, @Nullable String inDefault);

  //........................................................................
  //-------------------------------- hasKey --------------------------------

  /**
   * Check if a key exists in the configuration.
   *
   * @param       inKey the key to check for
   *
   * @return      true if there is already a configuration value with the
   *              given key
   *
   * @undefined   never
   *
   */
  public boolean hasKey(@Nonnull String inKey);

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- set ----------------------------------

  /**
   * Set a property value from the configuration.
   *
   * @param       inKey    the key of the value to obtain
   * @param       inValue  the new value to set to
   *
   * @return      true if save, false if not possible
   *
   */
  public boolean set(@Nonnull String inKey, @Nonnull String inValue);

  //........................................................................

  //-------------------------------- remove --------------------------------

  /**
   * Remove a property value from the configuration.
   *
   * @param       inKey    the key of the value to obtain
   *
   * @return      true if removed, false if not possible
   *
   */
  public boolean remove(@Nonnull String inKey);

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
