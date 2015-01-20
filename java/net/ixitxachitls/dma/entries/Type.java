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

package net.ixitxachitls.dma.entries;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Optional;

/**
 * The type specification for an entry.
 *
 * @file          BaseType.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 * @param         <T> the type represented by this type spec
 */

@Immutable
public class Type<T extends Entry> extends AbstractType<T>
{
  /** A builder for types. */
  public static class Builder<T extends Entry>
      extends AbstractType.Builder<T, Builder<T>>
  {
    /** The base type associated with this type. */
    private final BaseType<? extends BaseEntry> m_base;

    /**
     * Create the builder.
     *
     * @param inClass the class for the type
     * @param inBase the base type
     */
    public Builder(Class<T> inClass, BaseType<? extends BaseEntry> inBase)
    {
      super(inClass);

      m_base = inBase;
    }

    /**
     * Build the type.
     *
     * @return the built type
     */
    public Type<T> build()
    {
      return new Type(m_class, m_base, m_multiple, m_link, m_multipleLink,
                      m_sort);
    }
  }

  /**
   * Create a type.
   *
   * @param inClass the class for the type
   * @param inBase the base type
   * @param inMultiple the text to use to describe multiple types
   * @param inLink the text to link to the type
   * @param inMultipleLink the text to link to multiple of the types
   * @param inSort the text to use to sort the type
   */
  protected Type(Class<T> inClass, BaseType<? extends BaseEntry> inBase,
                 Optional<String> inMultiple,
                 Optional<String> inLink,
                 Optional<String> inMultipleLink,
                 Optional<String> inSort)
  {
    super(inClass, inMultiple, inLink, inMultipleLink, inSort);

    m_base = inBase;
  }

  /** The type of the corresponding base entry. */
  private final BaseType<? extends BaseEntry> m_base;

  /** All the non-base types available. */
  private static final Map<String, Type<? extends Entry>> s_types =
    new HashMap<String, Type<? extends Entry>>();

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Compare this type to another one for sorting.
   *
   * @param       inOther the other type to compare to
   *
   * @return      < 0 if this is lower, > if this is bigger, 0 if equal
   */
  @Override
  public int compareTo(AbstractType<? extends AbstractEntry> inOther)
  {
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

  /**
   * Get the base type to this one.
   *
   * @return      the requested base type or null if already a base type
   */
  @Override
  public AbstractType<? extends AbstractEntry> getBaseType()
  {
    return m_base;
  }

  /**
   * Get the base entry type for the given name.
   *
   * @param       inName the name of the type to get
   * @param       <E>    the type of entry the type is for
   *
   * @return      the base entry type with the given name or null if not
   *              found.
   */
  public static <E extends Entry> Optional<Type<E>> getType(String inName)
  {
    return Optional.fromNullable((Type<E>) s_types.get(inName));
  }

  /**
   * Get the non-base types available.
   *
   * @return      all the non-base types
   */
  public static Collection<Type<?>> getTypes()
  {
    return Collections.unmodifiableCollection(s_types.values());
  }
}
