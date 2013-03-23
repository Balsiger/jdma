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

import java.lang.reflect.Field;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.Changeable;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.entries.Variable;
import net.ixitxachitls.dma.values.Value;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * An empty derivation to give the variable access to the protected variables
 * of extensions.
 *
 *
 * @file          ExtensionVariable.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class ExtensionVariable extends Variable
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------- ExtensionVariable ---------------------------

  /**
   * The constructor.
   *
   * @param       inExtension      the extension class for this variable
   * @param       inKey            the key this variable is read with
   * @param       inField          the field that contains the value for this
   *                               variable
   * @param       inStored         true if the value will be stored, false
   *                               if not
   * @param       inPrintUndefined if printing the value when undefined
   *
   */
  public ExtensionVariable(Class<? extends AbstractExtension<?>> inExtension,
                           String inKey, Field inField, boolean inStored,
                           boolean inPrintUndefined)
  {
    super(inKey, inField, inStored, inPrintUndefined);

    m_extension = inExtension;
  }

  //......................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The extension class for this variable. */
  protected Class<? extends AbstractExtension<?>> m_extension;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------------- get ----------------------------------

  /**
   * Get the value of the variable given a specific entry.
   *
   * @param       inEntry the entry to get the value from
   *
   * @return      the current value
   *
   */
  @Override
  public @Nullable Value<?> get(Object inEntry)
  {
    AbstractExtension<?> extension = null;

    if(inEntry instanceof AbstractExtension)
      extension = (AbstractExtension<?>)inEntry;
    else if(inEntry instanceof AbstractEntry)
      extension = ((AbstractEntry)inEntry).getExtension(m_extension);

    // if we don't find the extension in question, it also does not help to
    // call super
    if(extension == null)
      return null; //return super.get(inEntry);

    try
    {
      return (Value<?>)m_field.get(extension);
    }
    catch(java.lang.IllegalAccessException e)
    {
      throw new UnsupportedOperationException
        ("Cannot access field " + m_field.getName() + ": " + e);
    }
  }

  //........................................................................
  //----------------------------- getExtension -----------------------------

  /**
   * Get the extension class for this variable.
   *
   * @return   the class of the extension
   *
   */
  public Class<? extends AbstractExtension<?>> getExtension()
  {
    return m_extension;
  }

  //........................................................................
  //----------------------------- hasVariable ------------------------------

  /**
   * Checks if the given entry has this variable.
   *
   * @param       inEntry the entry to check
   *
   * @return      true if the variable is there, false if not
   *
   */
  @Override
  public boolean hasVariable(ValueGroup inEntry)
  {
    if(inEntry instanceof AbstractEntry)
      return ((AbstractEntry)inEntry).hasExtension(m_extension);

    return m_extension.isAssignableFrom(inEntry.getClass());
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert the object to a human readable String representation.
   *
   * @return      the String representation
   *
   */
  @Override
  public String toString()
  {
    return "extension " + super.toString();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- set ----------------------------------

  /**
   * Set the value of the variable in a specific entry. This method does
   * not change the variable itself.
   *
   * @param       inEntry the entry to set the value in
   * @param       inValue the value to set to
   *
   */
  @Override
  public void set(Changeable inEntry, Value<?> inValue)
  {
    if(!(inEntry instanceof AbstractEntry))
    {
      super.set(inEntry, inValue);
      return;
    }

    try
    {
      AbstractExtension<?> extension =
        ((AbstractEntry)inEntry).getExtension(m_extension);
      if(extension == null)
        super.set(inEntry, inValue);
      else
        m_field.set(extension, inValue);
    }
    catch(java.lang.IllegalAccessException e)
    {
      throw new UnsupportedOperationException
        ("Cannot access field " + m_field.getName() + " for " + m_key
         + ": " + e);
    }

    inEntry.changed();
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, same as base variables

  //........................................................................
}
