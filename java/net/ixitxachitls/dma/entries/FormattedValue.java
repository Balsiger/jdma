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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A handle with a formatted value.
 *
 * @file          FormattedValue.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class FormattedValue extends ValueHandle
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------- FormattedValue -----------------------------

  /**
   * Creates the formatted value.
   *
   * @param       inValue          the comnputed (and formatted) value itself
   * @param       inKey            the key of the value
   * @param       inDM             true if the value is for dms only
   * @param       inPlayer         true if the value is for players only
   * @param       inPlayerEditable true if the value can be edited by players
   * @param       inPlural         the plural value of the key
   *
   * @param
   *
   * @return
   *
   */
  public FormattedValue(@Nonnull Object inValue, @Nonnull String inKey,
                        boolean inDM, boolean inPlayer,
                        boolean inPlayerEditable, @Nullable String inPlural)
  {
    super(inKey, inDM, inPlayer, inPlayerEditable, inPlural);

    m_value = inValue;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The formatted value. */
  protected @Nonnull Object m_value;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- value ---------------------------------

  /**
   * Get the handled value from the given entry.
   *
   * @param    inEntry the entry containing the value
   * @param    inDM    true if getting the value for a DM
   *
   * @return   the value found or null if not found or not accessible
   *
   */
  public @Nullable Object value(@Nonnull ValueGroup inEntry, boolean inDM)
  {
    return m_value;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- value ----------------------------------------------------------

    /** The value Test. */
    @org.junit.Test
    public void value()
    {
      ValueHandle formatted =
        new FormattedValue("value", "key", false, false, false, null);
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry
        ("guru", new net.ixitxachitls.dma.data.DMAData("path"));

      assertEquals("value", "value", formatted.value(entry, true));
    }

    //......................................................................

  }

  //........................................................................
}
