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
 * GNU General Public License for more details.x1
 *
 * You should have received a copy of the GNU General Public License
 * along with Dungeon Master Assistant; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *****************************************************************************/

//------------------------------------------------------------------ imports

package net.ixitxachitls.dma.entries;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.extensions.AbstractExtension;
import net.ixitxachitls.dma.entries.extensions.Extension;
import net.ixitxachitls.dma.proto.Entries.AbstractEntryProto;
import net.ixitxachitls.dma.proto.Entries.EntryProto;
import net.ixitxachitls.dma.values.ID;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the base class for all jDMA entries (not base entries!).
 *
 * @file          Entry.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 * @param         <B> the type of base entry associated with this entry
 */

@ParametersAreNonnullByDefault
public abstract class Entry<B extends BaseEntry> extends AbstractEntry<B>
{
  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * The complete and 'default' constructor.
   *
   * @param       inName     the name of the entry
   * @param       inType     the type of the entry
   */
  protected Entry(String inName, Type<? extends Entry<?>> inType)
  {
    super(inName, inType);
  }

  /**
   * The default constructor.
   *
   * @param       inType     the type of the entry
   */
  protected Entry(Type<? extends Entry<?>> inType)
  {
    super(inType);
  }

  /** The type of this entry. */
  public static final Type<Entry<?>> TYPE =
    new Type<Entry<?>>(Entry.class, BaseEntry.TYPE);

  static
  {
    TYPE.withLink("entry", "entries");
  }

  /** The name of a temporary entry. */
  public static final String TEMPORARY = "TEMPORARY";

  /**
   * Set the id to a random value.
   */
  public void randomID()
  {
    m_name = ID.random().get();
    changed(true);
  }

  /**
   * Check if the current entry represents a base entry or not.
   *
   * @return      true if this is a base entry, false else
   */
  @Override
  public boolean isBase()
  {
    return false;
  }

  /**
   * Complete the entry and make sure that all values are filled.
   *
   */
  @Deprecated
  @OverridingMethodsMustInvokeSuper
  public void complete()
  {
    if(m_name.isEmpty())
    {
      changed();

      do
      {
        randomID();
      } while(DMADataFactory.get().getEntry(getKey()) != null);
    }

    setupExtensions();
    for(AbstractExtension<?, ?> extension : m_extensions.values())
      if(extension instanceof Extension)
        ((Extension)extension).complete();
  }

  @Override
  public Message toProto()
  {
    EntryProto.Builder builder = EntryProto.newBuilder();

    builder.setAbstract((AbstractEntryProto)super.toProto());

    return builder.build();
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof EntryProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    EntryProto proto = (EntryProto)inProto;

    super.fromProto(proto.getAbstract());
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(EntryProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }
}
