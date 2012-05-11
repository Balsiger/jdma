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

package net.ixitxachitls.dma.output.soy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.restricted.StringData;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.output.html.HTMLDocument;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.Value;

//..........................................................................

//------------------------------------------------------------------- header

/**
 *
 *
 *
 * @file          SoyCombination.java
 *
 * @author        Peter Balsiger
 *
 */

//..........................................................................

//__________________________________________________________________________

public class SoyCombination extends SoyValue
{
  //--------------------------------------------------------- constructor(s)

  public SoyCombination(@Nonnull String inName,
                        @Nonnull Combination inCombination,
                        @Nonnull Value inValue,
                        @Nonnull AbstractEntry inEntry,
                        @Nonnull SoyRenderer inRenderer)
  {
    super(inName, inValue, inEntry, inRenderer);

    m_combination = inCombination;
  }

  //........................................................................

  //-------------------------------------------------------------- variables

  private @Nonnull Combination m_combination;

  //........................................................................

  //-------------------------------------------------------------- accessors

  @Override
  public @Nullable SoyData getSingle(@Nonnull String inName)
  {
    if("print".equals(inName))
      return StringData.forValue(m_combination.print(m_renderer));

    return super.getSingle(inName);
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
