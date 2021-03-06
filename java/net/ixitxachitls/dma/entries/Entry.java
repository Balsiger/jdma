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

import java.util.List;

import com.google.common.base.Optional;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.AbstractEntryProto;
import net.ixitxachitls.dma.proto.Entries.EntryProto;
import net.ixitxachitls.dma.values.Annotated;
import net.ixitxachitls.dma.values.ProductReference;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the base class for all jDMA entries (not base entries!).
 *
 * @file          Entry.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */
public abstract class Entry extends AbstractEntry
{
  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * The complete and 'default' constructor.
   *
   * @param       inName     the name of the entry
   * @param       inType     the type of the entry
   */
  protected Entry(String inName, Type<?> inType)
  {
    super(inName, inType);
  }

  /**
   * The default constructor.
   *
   * @param       inType     the type of the entry
   */
  protected Entry(Type<?> inType)
  {
    super(inType);
  }

  /** The type of this entry. */
  public static final Type<Entry> TYPE =
    new Type.Builder<>(Entry.class, BaseEntry.TYPE).build();

  static
  {
    TYPE.withLink("entry", "entries");
  }

  /** The name of a temporary entry. */
  public static final String TEMPORARY = "TEMPORARY";

  /** The number of digits to be used in ids. */
  public static final int s_digits = 4;

  /**
   * Get the combined description of the entry, including values of base items.
   *
   * @return a combined description with the sum and their sources.
   */
  public Annotated<Optional<String>> getCombinedDescription()
  {
    Annotated<Optional<String>> combined = new Annotated.String();
    for(BaseEntry entry : getBaseEntries())
      combined.add(entry.getCombinedDescription());

    return combined;
  }

  /**
   * Get the combined short description of the entry, including values of base
   * items.
   *
   * @return a combined description with the sum and their sources.
   */
  public Annotated<Optional<String>> getCombinedShortDescription()
  {
    Annotated<Optional<String>> combined = new Annotated.String();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseEntry)
      combined.add(entry.getCombinedShortDescription());

    return combined;
  }

  /**
   * Get the combined incomplete data, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<Optional<String>> getCombinedIncomplete()
  {
    Annotated.String combined = new Annotated.String();
    for(BaseEntry entry : getBaseEntries())
      combined.add(entry.getCombinedIncomplete());

    return combined;
  }

  /**
   * Get the combined references of the entry, including values from base
   * entries.
   *
   * @return a combination value with the sum and their sources.
   */
  public Annotated<List<ProductReference>> getCombinedReferences()
  {
    Annotated.List<ProductReference> combined = new Annotated.List<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(entry.getCombinedReferences());

    return combined;
  }

  /**
   * Get the base entry for this entry.
   *
   * @return the base entry for this
   */
  public BaseEntry getBaseEntry()
  {
    List<BaseEntry> bases = getBaseEntries();
    assert bases.size() == 1
        : "Expected a single base entry, found " + bases.size();

    return bases.get(0);
  }

  /**
   * Set the id to a random value.
   */
  public void randomID()
  {
    char []generated = new char[s_digits];
    for(int i = 0; i < s_digits; i++)
      generated[i] = (char)(RANDOM.nextInt(26) + 'A');

    m_name = new String(generated);
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

  @Override
  public void initialize(boolean inForce)
  {
    if(m_name.isEmpty())
    {
      changed();

      do
      {
        randomID();
      } while(DMADataFactory.get().getEntry(getKey()) != null);
    }
  }

  @Override
  public Message toProto()
  {
    EntryProto.Builder builder = EntryProto.newBuilder();

    builder.setAbstract((AbstractEntryProto)super.toProto());

    return builder.build();
  }

  /**
   * Set all the value from the given proto.
   *
   * @param inProto the proto to set from
   */
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
  protected Message defaultProto()
  {
    return EntryProto.getDefaultInstance();
  }

  /**
   * Check whether the given user is the owner of the entry.
   *
   * @param       inUser the user accessing
   *
   * @return      true if the user is the owner, false if not
   */
  public boolean isOwner(BaseCharacter inUser)
  {
    // TODO: handle this properly.
    return false;
  }
}
