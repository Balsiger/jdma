/******************************************************************************
 * Copyright (c) 2002-2013 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import com.google.common.base.Optional;
import net.ixitxachitls.dma.values.Values;

/**
 * An entry that is nested as part of another entry. This entry cannot be saved
 * and does not have a name.
 *
 * @file   NestedEntry.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public abstract class NestedEntry
{
  public interface Creator<E extends NestedEntry>
  {
    public E create();
  }

  /** The name of the nested entry. */
  protected Optional<String> m_name = Optional.absent();

  public String getName()
  {
    if(m_name.isPresent())
      return m_name.get();

    return "";
  }


  public abstract void set(Values inValues);
}
