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

package net.ixitxachitls.dma.values;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

//..........................................................................

//------------------------------------------------------------------- header

/**
 *
 *
 *
 * @file          ValueInfo.java
 *
 * @author        Peter Balsiger
 *
 */

//..........................................................................

//__________________________________________________________________________

public class ValueInfo
{
  //--------------------------------------------------------- constructor(s)

  public ValueInfo(@Nonnull String inName, @Nonnull String inType,
                   @Nonnull Object inValue)
  {
    m_name = inName;
    m_type = inType;
    m_value = inValue;
  }

  public ValueInfo withTemplate(@Nonnull String inTemplate)
  {
    m_template = inTemplate;

    return this;
  }

  //........................................................................

  //-------------------------------------------------------------- variables

  private @Nonnull String m_name;
  private @Nonnull String m_type;
  private @Nonnull Object m_value;
  private @Nullable String m_template;

  //........................................................................

  //-------------------------------------------------------------- accessors

  public @Nonnull String getName()
  {
    return m_name;
  }

  public @Nonnull String getType()
  {
    return m_type;
  }

  public @Nonnull Object getValue()
  {
    return m_value;
  }

  public @Nonnull String getTemplate()
  {
    if(m_template != null)
      return m_template;

    return m_type;
  }

  //........................................................................

  //----------------------------------------------------------- manipulators

  //........................................................................

  //------------------------------------------------- other member functions

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
