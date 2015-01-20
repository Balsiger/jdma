/*******************************************************************************
 * Copyright (c) 2002-2015 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
 ******************************************************************************/

package net.ixitxachitls.dma.values.enums;

import com.google.protobuf.ProtocolMessageEnum;

/**
 * The interface for all enumeration values that convert to protos.
 *
 * @file Proto.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param <P> the type of message for this proto
 */
public interface Proto<P extends ProtocolMessageEnum>
{
  /**
   * Convert the enum value to its corresponding proto value.
   *
   * @return the converted proto value
   */
  public P toProto();
}
