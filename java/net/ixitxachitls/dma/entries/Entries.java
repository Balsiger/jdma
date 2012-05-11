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

package net.ixitxachitls.dma.entries;

import java.util.List;
import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * Utility functions for entries.
 *
 * @file          Entries.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Entries
{
  //--------------------------------------------------------- constructor(s)

  private Entries()
  {
    // don't construct
  }

  //........................................................................

  //-------------------------------------------------------------- variables

  private static final Joiner s_commaJoiner = Joiner.on(", ");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

  //........................................................................

  //------------------------------------------------- other member functions

  public static List<String> names(Iterable<ValueGroup> inEntries)
  {
    List<String> names = new ArrayList<String>();
    for(ValueGroup entry : inEntries)
      if(entry != null && entry instanceof AbstractEntry)
        names.add(((AbstractEntry)entry).getName());

    return names;
  }

  public static String namesString(Iterable<ValueGroup> inEntries)
  {
    return s_commaJoiner.join(names(inEntries));
  }

  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
