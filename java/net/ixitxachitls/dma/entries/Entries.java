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

package net.ixitxachitls.dma.entries;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Optional;

/**
 * Utility functions for entries.
 *
 * @file          Entries.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@Immutable
public final class Entries
{
  /**
   * Private constructor to prevent instantiation.
   *
   */
  private Entries()
  {
    // don't construct
  }

  public static <T> Optional<T> first(List<T> inList)
  {
    if(inList.isEmpty())
      return Optional.absent();

    return Optional.of(inList.get(0));
  }

  public static <T> Optional<T> second(List<T> inList)
  {
    if(inList.size() < 2)
      return Optional.absent();

    return Optional.of(inList.get(1));
  }

  //-------------------------------------------------------------- variables

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }
}
