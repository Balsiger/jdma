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

import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.values.Text;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the timed extension for all the entries.
 *
 * @file          Incomplete.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class Incomplete extends Extension<Entry<?>>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Incomplete -----------------------------

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor.
   *
   * @param       inEntry the entry attached to
   * @param       inName the name of the extension
   *
   */
  public Incomplete(Entry<?> inEntry, String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //------------------------------- Incomplete -----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   */
  // public Incomplete(Entry inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  //----- incomplete -------------------------------------------------------

  /** The time that is left for the item. */
  @Key("incomplete")
  @DM
  @WithBases
  protected Text m_incomplete = new Text();

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- computeValue -----------------------------

  /**
   * Get a value for printing.
   *
   * @param     inKey  the name of the value to get
   * @param     inDM   true if formattign for dm, false if not
   *
   * @return    a value handle ready for printing
   *
   */
  // @Override
  // public @Nullable ValueHandle computeValue(String inKey, boolean inDM)
  // {
  //   if(inDM && "summary".equals(inKey))
  //   {
  //     List<Object> commands = new ArrayList<Object>();
  //     commands.add(new Symbol("\u2639"));
  //     maybeAddValue(commands, "incomplete", inDM, null, null);

  //     return new FormattedValue(new Color("dm-notes", new Command(commands)),
  //                               null, "summary");
  //   }

  //   return super.computeValue(inKey, inDM);
  // }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
