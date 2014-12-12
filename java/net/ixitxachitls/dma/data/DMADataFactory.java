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

package net.ixitxachitls.dma.data;

import javax.annotation.ParametersAreNonnullByDefault;

import net.ixitxachitls.util.configuration.Config;

/**
 * Factory for creating dma data stores.
 *
 *
 * @file          DMADataFactory.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@ParametersAreNonnullByDefault
public final class DMADataFactory
{
  /**
   * Prevent instantiations.
   */
  private DMADataFactory()
  {
    // nothing to do
  }

  /** The static singleton with all the base data. */
  private static volatile DMADatastore s_base;

  /**
   * Get the base data for all entries.
   *
   * @return      the repository with all the base data
   */
  public static synchronized DMADatastore get()
  {
    if(s_base == null)
      if(Config.get("web.data.datastore", true))
        s_base = new DMADatastore();
      else
        s_base = new FakeDMADatastore();

    return s_base;
  }

  /**
   * Clear the currently used base to get a new one next time. Mostly used for
   * testing.
   *
   */
  public static synchronized void clear()
  {
    s_base = null;
  }
}
