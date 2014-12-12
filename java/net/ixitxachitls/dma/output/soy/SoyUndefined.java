/******************************************************************************
 * Copyright (c) 2002-2014 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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

package net.ixitxachitls.dma.output.soy;

import javax.annotation.concurrent.Immutable;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.restricted.BooleanData;

/**
 * An undefined value that can still be dereferenced, resulting in another
 * undefined value.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
@Immutable
public class SoyUndefined extends SoyValue
{
  /**
   * Create the undefined value with the given name.
   *
   * @param inName the name of the value
   */
  public SoyUndefined(String inName)
  {
    super(inName, null);
  }

  /**
   * Get the named value.
   *
   * @param  inName the name of the value to get
   *
   * @return the value with the given name
   */
  @Override
  public SoyData getSingle(String inName)
  {
    switch(inName)
    {
      case "present":
        return BooleanData.FALSE;

      default:
        return new SoyUndefined(m_name + "." + inName);
    }
  }

  /**
   * Convert the undefined value to a human readable string.
   *
   * @return the string conversion
   */
  @Override
  public String toString()
  {
    return "(undefined " + m_name + ")";
  }
}
