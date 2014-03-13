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


package net.ixitxachitls.dma.values;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.protobuf.Message;

/**
 * Base class for values.
 *
 * @file   NewValue.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */
@ParametersAreNonnullByDefault
public abstract class NewValue<T extends Message>
{
  /** Simple interface for parsing values. */
  public interface Parser<P>
  {
    /**
     * Parse the value from the given string.
     *
     * @param inValues the string values to parse from
     * @return the parse value
     */
    public @Nullable P parse(String ... inValues);
  }

  /**
   * Convert the value to a proto message.
   *
   * @return the converted proto message
   */
  public abstract T toProto();
}
