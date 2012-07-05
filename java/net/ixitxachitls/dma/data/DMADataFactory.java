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

package net.ixitxachitls.dma.data;

import javax.annotation.Nonnull;

import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * Factory for creating dma data stores.
 *
 *
 * @file          DMADataFactory.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public final class DMADataFactory
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- DMADataFactory ----------------------------

  /**
   * Prevent instantiations.
   *
   */
  private DMADataFactory()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The static singleton with all the base data. */
  private static volatile DMAData s_base;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------------- gets ---------------------------------

  /**
   * Get the base data for all entries.
   *
   * @return      the repository with all the base data
   *
   */
  public static synchronized @Nonnull DMAData get()
  {
    if(s_base == null)
    {
      if(Config.get("web.data.datastore", true))
        s_base = new DMADatastore();
      else
        if(Config.get("web.data.datafiles", true))
        {
          // TODO: this is risky, since we might return the data before it's
          // actually read, but if we wait here, we end up requesting it (and
          // rereading it while reading the files below. Since this will go
          // away, this should do for now.
          DMADatafiles data =
            new DMADatafiles(Config.get("web.dma.data", "dma"));
          String dirs =
            Config.get("web.dma.files",
                       "BaseProducts, BaseProducts/DnD, BaseProducts/Novels, "
                       + "BaseProducts/Magazines, BaseCharacters, "
                       + "BaseCampaigns");
          Log.info("reading base data for " + dirs);
          for(String baseDir : dirs.split(",\\s*"))
            data.addAllFiles(baseDir);

          if(!data.read())
            Log.error("Could not properly read base data files!");
          s_base = data;
        }
        else
          s_base = new DMAData.Test.Data();
    }

    return s_base;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- clear ---------------------------------

  /**
   * Clear the currently used base to get a new one next time. Mostly used for
   * testing.
   *
   */
  public static synchronized void clear()
  {
    s_base = null;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
