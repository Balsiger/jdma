/******************************************************************************
 * Copyright (c) 2002-2013 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.values;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;

/**
 * A reference to an entry.
 *
 * @file Reference.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 * @param <T> the type of entry being referenced
 */
public class Reference<T extends AbstractEntry>
  extends Value<BaseEntryProto.Reference>
  implements Comparable<Reference>
{
  /** The parser for references. */
  public static class ReferenceParser<T extends AbstractEntry,
                                      C extends Reference<T>>
    extends Parser<C>
  {
    /**
     * Create the parser.
     *
     * @param inType the type of entries referenced
     */
    public ReferenceParser(AbstractType<T> inType)
    {
      this(inType, 1);
    }

    /**
     * Create the parser.
     *
     * @param inType the type of entries referenced
     * @param inArguments the number of arguments to parse
     */
    protected ReferenceParser(AbstractType<T> inType, int inArguments)
    {
      super(inArguments);

      m_type = inType;
    }

    /** The type of entry referenced. */
    private final AbstractType<T> m_type;

    @SuppressWarnings("unchecked")
    @Override
    public Optional<C> doParse(String inName)
    {
      return (Optional<C>) Reference.parse(m_type, inName);
    }
  }

  /**
   * Create a reference.
   *
   * @param inType the type of entry referenced
   * @param inName the name (id) of the entry referenced
   */
  public Reference(AbstractType<T> inType, String inName)
  {
    m_name = inName;
    m_type = inType;
  }

  /** The id of the referenced entry. */
  protected final String m_name;

  /** The type of entry referenced. */
  protected final AbstractType<T> m_type;

  /** The actual entry referenced, if any. */
  protected Optional<T> m_entry = Optional.absent();

  /**
   * Whether the entry referenced was resolved. If this is true, then m_entry
   * gives the entry referenced or absent if the entry is not available.
   */
  private boolean m_resolved = false;

  /**
   * Get the product referenced.
   *
   * @return the product name
   */
  public String getName()
  {
    return m_name;
  }

  /**
   * Get the url to the product referenced.
   *
   * @return the url
   */
  public String getUrl()
  {
    return "/" + m_type.getLink() + "/" + m_name;
  }

  /**
   * Get the entry referenced, if available.
   *
   * @return the entry referenced or absent if not available
   */
  public Optional<T> get()
  {
    resolve();

    return m_entry;
  }

  @Override
  public String toString()
  {
    return m_name;
  }

  @Override
  public BaseEntryProto.Reference toProto()
  {
    BaseEntryProto.Reference.Builder reference =
      BaseEntryProto.Reference.newBuilder();

    reference.setName(m_name);

    return reference.build();
  }

  /** Resolve the entry referenced. */
  @SuppressWarnings("unchecked")
  protected void resolve()
  {
    if(m_resolved)
      return;

    m_resolved = true;
    m_entry =
        DMADataFactory.get().getEntry(AbstractEntry.createKey(m_name, m_type));
  }

  /**
   * Create a new reference from the given proto message.
   *
   * @param inType the type of entry referenced
   * @param inProto the proto to create the reference
   * @param <T> the type of entry referenced
   * @return the new reference created for the proto
   */
  public static <T extends AbstractEntry> Reference<T>
  fromProto(AbstractType<T> inType, BaseEntryProto.Reference inProto)
  {
    return new Reference<T>(inType, inProto.getName());
  }

  /**
   * Parse the product reference from the given strings.
   *
   * @param inType the type of entry to parse
   * @param inName the name of the reference
   * @param <T> the type of entry to parse
   * @return the parsed reference
   */
  public static <T extends AbstractEntry>
  Optional<? extends Reference<T>> parse(AbstractType<T> inType, String inName)
  {
    return Optional.of(new Reference<T>(inType, inName));
  }

  @Override
  public boolean equals(@Nullable Object inOther)
  {
    if(this == inOther)
      return true;

    if(inOther == null)
      return false;

    if(!(inOther instanceof Reference))
      return false;

    Reference other = (Reference)inOther;
    return m_name.equals(other.m_name);
  }

  @Override
  public int hashCode()
  {
    return m_name.hashCode();
  }

  @Override
  public int compareTo(Reference inOther)
  {
    if(inOther == this)
      return 0;

     return m_name.compareTo(inOther.m_name);
  }
}
