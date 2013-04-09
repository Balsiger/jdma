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
 * The type specification for an entry.
 *
 * @file          BaseType.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <T> the type represented by this type spec
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Type<T extends Entry<?>> extends AbstractType<T>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Type ---------------------------------

  /**
   * Create the type.
   *
   * @param       inClass the class represented by this type
   * @param       inBase  the base class for the type
   */
  @SuppressWarnings("unchecked") // We don't use Class<T> here, as this would
                                 // not allow us to create Entry.TYPE properly.
  public Type(Class<?> inClass, BaseType<? extends BaseEntry> inBase)
  {
    super((Class<T>)inClass);

    m_base = inBase;
  }

  //........................................................................
  //--------------------------------- Type ---------------------------------

  /**
   * Create the type.
   *
   * @param       inClass    the class represented by this type
   * @param       inBase     the base class for the type
   * @param       inMultiple the name to use for multiple entries of the type
   *
   */
  public Type(Class<T> inClass, BaseType<BaseEntry> inBase,
              String inMultiple)
  {
    super(inClass, inMultiple);

    m_base = inBase;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** the type of the corresponding base entry. */
  private @Nullable BaseType<? extends BaseEntry> m_base;

  /** All the non-base types available. */
  private static final Map<String, Type<? extends Entry<?>>> s_types =
    new HashMap<String, Type<? extends Entry<?>>>();

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ compareTo -------------------------------

  /**
   * Compare this type to another one for sorting.
   *
   * @param       inOther the other type to compare to
   *
   * @return      < 0 if this is lower, > if this is bigger, 0 if equal
   *
   */
  @Override
  public int compareTo(@Nullable AbstractType<? extends AbstractEntry> inOther)
  {
    if(inOther == null)
      return -1;

    if(inOther instanceof Type)
    {
      Type<?> other = (Type<?>)inOther;

      if(m_base != null && other.m_base == null)
        return -1;

      if(m_base == null && other.m_base != null)
        return +1;
    }

    return super.compareTo(inOther);
  }

  //........................................................................
  //----------------------------- getBaseType ------------------------------

  /**
   * Get the base type to this one.
   *
   * @return      the requested base type or null if already a base type
   *
   */
  @Override
  public @Nullable BaseType<? extends BaseEntry> getBaseType()
  {
    return m_base;
  }

  //........................................................................
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
  public static @Nullable Type<? /*extends Entry*/> getType(String inName)
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
  public static Collection<Type<?>> getTypes()
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
