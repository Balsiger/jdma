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

package net.ixitxachitls.dma.entries.extensions;

import javax.annotation.ParametersAreNonnullByDefault;

import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.Expression;
import net.ixitxachitls.dma.values.Value;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the multiuse extension for all the entries.
 *
 * @file          Multiuse.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class Multiuse extends Counted
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Multiuse ------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
    * Default constructor.
    *
    * @param       inEntry the entry attached to
    * @param       inName  the name of the extension
    *
    */
  public Multiuse(Item inEntry, String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //------------------------------- Multiuse ------------------------------

  /**
    * Default constructor.
    *
    * @param       inEntry the entry attached to
    * @param       inTag   the tag name for this instance
    * @param       inName  the name of the extension
    *
    */
  // public Multiuse(Item inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  static
  {
    extractVariables(Item.class, Multiuse.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- collect --------------------------------

  /**
   * Adjust the value for the given name for any special properites.
   *
   * @param       inName      the name of the value to adjust
   * @param       ioCombined  the combinstaion to adjust
   * @param       <V>         the real type of value being collected
   *
   */
  @Override
  public <V extends Value<V>> void collect(String inName,
                                           Combined<V> ioCombined)
  {
    if("value".equals(inName))
      ioCombined.addExpression(new Expression.Factor(m_count.get(), 1), this,
                               "multiuse");

    super.collect(inName, ioCombined);
  }

  //........................................................................

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends ValueGroup.Test
  {
    //----- value ----------------------------------------------------------

    /** The value Test. */
    @org.junit.Test
    public void value()
    {
      String text = "campaign campaign = base FR.\n"
        + "item with multiuse item = value 100 gp; campaign FR / campaign; "
        + "count 20.\n";

      try (net.ixitxachitls.input.ParseReader reader =
        new net.ixitxachitls.input.ParseReader(new java.io.StringReader(text),
                                               "container test"))
      {
        addEntry(net.ixitxachitls.dma.entries.Campaign.read(reader));
        Item item = (Item)Item.read(reader);

        assertEquals("value", 2000.0, item.getGoldValue(), 0.5);
      }
    }

    //......................................................................
  }

  //........................................................................
}
