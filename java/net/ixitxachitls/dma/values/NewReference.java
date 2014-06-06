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
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;

@ParametersAreNonnullByDefault
public class NewReference<T extends AbstractEntry>
  extends NewValue<BaseEntryProto.Reference>
  implements Comparable<NewReference>
{
  public static class ReferenceParser<T extends AbstractEntry,
                                      C extends NewReference<T>>
    extends Parser<C>
  {
    public ReferenceParser(AbstractType<T> inType)
    {
      this(inType, 1);
    }

    protected ReferenceParser(AbstractType<T> inType, int inArguments)
    {
      super(inArguments);

      m_type = inType;
    }

    private final AbstractType<T> m_type;

    @SuppressWarnings("unchecked")
    @Override
    public Optional<C> doParse(String inName)
    {
      return (Optional<C>)NewReference.parse(m_type, inName);
    }
  }

  public NewReference(AbstractType<T> inType, String inName)
  {
    m_name = inName;
    m_type = inType;
  }

  protected final String m_name;
  protected final AbstractType<T> m_type;
  protected Optional<T> m_entry = Optional.absent();
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

  @SuppressWarnings("unchecked")
  protected void resolve()
  {
    if(m_resolved)
      return;

    m_resolved = true;
    m_entry = Optional.fromNullable((T)DMADataFactory.get()
                                    .getEntry(AbstractEntry
                                              .createKey(m_name, m_type)));
  }

  /**
   * Create a new reference from the given proto message.
   *
   *
   * @param inProto the proto message
   * @return the newly create reference
   */
  public static <T extends AbstractEntry> NewReference<T>
  fromProto(AbstractType<T> inType, BaseEntryProto.Reference inProto)
  {
    return new NewReference<T>(inType, inProto.getName());
  }

  /**
   * Parse the product reference from the given strings.
   *
   * @param inName the name of the reference
   * @param inPages the pages of the reference
   * @return
   */
  public static <T extends AbstractEntry>
  Optional<NewReference<T>> parse(AbstractType<T> inType, String inName)
  {
    return Optional.of(new NewReference<T>(inType, inName));
  }

  @Override
  public boolean equals(@Nullable Object inOther)
  {
    if(this == inOther)
      return true;

    if(inOther == null)
      return false;

    if(!(inOther instanceof NewReference))
      return false;

    NewReference other = (NewReference)inOther;
    return m_name.equals(other.m_name);
  }

  @Override
  public int hashCode()
  {
    return m_name.hashCode();
  }

  @Override
  public int compareTo(NewReference inOther)
  {
    if(inOther == this)
      return 0;

     return m_name.compareTo(inOther.m_name);
  }
}