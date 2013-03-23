/******************************************************************************
 * Copyright (c) 2002-2011 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;


//..........................................................................

//------------------------------------------------------------------- header

/**
 * The type specification for a base entry.
 *
 * @file          BaseType.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <T> the type represented by this type spec
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class BaseType<T extends BaseEntry> extends AbstractType<T>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- BaseType -------------------------------

  /**
   * Create the type.
   *
   * @param       inClass the class represented by this type
   *
   */
  public BaseType(Class<T> inClass)
  {
    super(inClass);
  }

  //........................................................................
  //------------------------------- BaseType -------------------------------

  /**
   * Create the type.
   *
   * @param       inClass    the class represented by this type
   * @param       inMultiple the name to use for multiple entries of the type
   *
   */
  public BaseType(Class<T> inClass, String inMultiple)
  {
    super(inClass, inMultiple);
  }

  //........................................................................
  //------------------------------- withLink -------------------------------

  /**
   * Set the link to use for this type.
   *
   * @param       inLink         the name of the link to use
   * @param       inMultipleLink the name to link to multiple entries
   *
   * @return      the type for chaining
   *
   */
  @Override
  public BaseType<T> withLink(String inLink, String inMultipleLink)
  {
    super.withLink(inLink, inMultipleLink);

    return this;
  }

  //........................................................................
  //------------------------------- withSort -------------------------------

  /**
   * Set the sort field to use for this type.
   *
   * @param       inSort  the field used to sort
   *
   * @return      the type for chaining
   *
   */
  @Override
  public BaseType<T> withSort(String inSort)
  {
    super.withSort(inSort);

    return this;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** All the non-base types available. */
  private static final Map<String, BaseType<?/* extends BaseEntry */>> s_types =
    new HashMap<String, BaseType<?/* extends BaseEntry*/>>();

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getType --------------------------------

  /**
   * Get the base entry type for the given name.
   *
   * @param       inName the name of the type to get
   *
   * @return      the base entry type with the given name or null if not
   *              found.
   *
   */
  public static @Nullable BaseType<?> getType(String inName)
  {
    return s_types.get(inName);
  }

  //........................................................................
  //------------------------------- getTypes -------------------------------

  /**
   * Get the non-base types available.
   *
   * @return      all the non-base types
   *
   */
  public static Collection<BaseType<?>> getTypes()
  {
    return Collections.unmodifiableCollection(s_types.values());
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
