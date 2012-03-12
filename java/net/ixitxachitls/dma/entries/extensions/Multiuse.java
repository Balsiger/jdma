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

import javax.annotation.Nonnull;

import net.ixitxachitls.dma.entries.Item;

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

public class Multiuse extends Counted
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Multiuse ------------------------------

  /**
    * Default constructor.
    *
    * @param       inEntry the entry attached to
    * @param       inName  the name of the extension
    *
    */
  public Multiuse(@Nonnull Item inEntry, @Nonnull String inName)
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

  //------------------------------- complete -------------------------------

  /**
   * Complete the extension and make sure that all values are filled.
   *
   */
  // public void complete()
  // {
  //   // Adjust the value accordig to the count.
  //   if(m_count.isDefined())
  //     m_entry.addValueModifier
  //       (new NumberModifier(NumberModifier.Operation.MULTIPLY,
  //                           (int)m_count.get(),
  //                           NumberModifier.Type.GENERAL, "multiple count"));

  //   super.complete();
  // }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //----------------------------- modifyValue ------------------------------

  /**
    *
    * Modify the given value with information from the current extension.
    *
    * @param       inType    the type of value to modify
    * @param       inEntry   the entry to modify in
    * @param       inValue   the value to modify, return in this object
    * @param       inDynamic a flag denoting if dynamic modifiers should be
    *                        returned
    *
    * @return      the newly computed value (or null if no value to use)
    *
    * @undefined   never
    *
    * @algorithm   nothing done here
    *
    * @derivation  necessary if real modifications are desired
    *
    * @example     see Item
    *
    * @bugs
    * @to_do
    *
    * @keywords    modify . value
    *
    */
//   public Modifier modifyValue(PropertyKey inType, AbstractEntry inEntry,
//                               Value inValue, boolean inDynamic)
//   {
//     if(inValue == null || !inValue.isDefined())
//       return null;

//     if(m_count.isDefined() && inDynamic
//        && inType == PropertyKey.getKey("value"))
//       return new Modifier(Modifier.Type.MULTIPLY, (int)m_count.get());

//     return super.modifyValue(inType, inEntry, inValue, inDynamic);
//   }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
