/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.indexes.Index;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base class for all base extensions.
 *
 * @file          BaseExtension.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <T> the type of the entry associated with this extension
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BaseExtension<T extends BaseEntry> extends AbstractExtension<T>
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- BaseExtension ----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry  the entry attached to
   * @param       inName   the name of the extension
   *
   */
  public BaseExtension(T inEntry, String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //---------------------------- BaseExtension ----------------------------

  /**
   * Constructor with all the values.
   *
   * @param       inEntry  the entry attached to
   * @param       inTag    the tag for this extension
   * @param       inName   the name for this extension
   *
   */
  // public BaseExtension(T inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables
  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------- computeIndexValues ---------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @param       ioValues a multi map of values per index name
   *
   */
  @Override
  public void computeIndexValues(Multimap<Index.Path, String> ioValues)
  {
    // nothing to do here
  }

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // no tests here

  //........................................................................
}
