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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Optional;

/**
 * The type specification for a base entry.
 *
 * @file          BaseType.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 * @param         <T> the type represented by this type spec
 *
 */

@Immutable
public class BaseType<T extends BaseEntry> extends AbstractType<T>
{
  public static class Builder<T extends BaseEntry>
      extends AbstractType.Builder<T, Builder<T>>
  {
    public Builder(Class<T> inClass)
    {
      super(inClass);
    }

    public BaseType<T> build()
    {
      return new BaseType(m_class, m_multiple, m_link, m_multipleLink, m_sort);
    }
  }

  protected BaseType(Class<T> inClass, Optional<String> inMultiple,
                     Optional<String> inLink,
                     Optional<String> inMultipleLink,
                     Optional<String> inSort)
  {
    super(inClass, inMultiple, inLink, inMultipleLink, inSort);
  }

  /** All the non-base types available. */
  private static final Map<String, BaseType<? extends BaseEntry>> s_types =
    new HashMap<String, BaseType<? extends BaseEntry>>();

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Get the base entry type for the given name.
   *
   * @param       inName the name of the type to get
   *
   * @return      the base entry type with the given name or null if not
   *              found.
   */
  public static <T extends BaseEntry>
  Optional<BaseType<T>> getType(String inName)
  {
    return Optional.fromNullable((BaseType<T>)s_types.get(inName));
  }

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
}
