/*******************************************************************************
 * Copyright (c) 2002-2016 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
 ******************************************************************************/

package net.ixitxachitls.dma.entries;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries;
import net.ixitxachitls.dma.proto.Entries.MiniatureProto;
import net.ixitxachitls.dma.values.MiniatureLocation;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.util.logging.Log;

/**
 * An available miniature.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Miniature extends Entry
{
  protected Miniature()
  {
    super(TYPE);
  }

  public Miniature(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final Type<Miniature> TYPE =
      new Type.Builder<>(Miniature.class, BaseMiniature.TYPE)
          .sort("bases").build();

  protected Optional<Integer> m_number = Optional.absent();
  protected Optional<String> m_notes = Optional.absent();
  protected Optional<String> m_owner = Optional.absent();

  public Optional<Integer> getNumber()
  {
    return m_number;
  }

  public Optional<String> getNotes()
  {
    return m_notes;
  }

  public Optional<String> getOwner()
  {
    return m_owner;
  }

  public String getBaseName()
  {
    for(BaseEntry base : getBaseEntries())
      return ((BaseMiniature)base).getName();

    return getName();
  }

  @Override
  public EntryKey getKey()
  {
    if(m_owner.isPresent())
      return new EntryKey(getName(), TYPE,
                          Optional.of(new EntryKey(m_owner.get(),
                                                   BaseCharacter.TYPE)));

    return new EntryKey(getName(), TYPE);
  }

  @Override
  public String getPath()
  {
    return "/" + BaseCharacter.TYPE.getLink() + "/"
        + (m_owner.isPresent() ? m_owner.get() : "") + "/"
        + getType().getLink() + "/" + getName();
  }

  @Override
  public List<Link> getNavigation()
  {
    return ImmutableList.of
        (new Link(BaseCharacter.TYPE.getLink(),
                  "/" + BaseCharacter.TYPE.getMultipleLink()),
         new Link(m_owner.toString(),
                  "/" + BaseCharacter.TYPE.getLink() + "/" + m_owner),
         new Link(getType().getLink(),
                  "/" + BaseCharacter.TYPE.getLink() + "/" + m_owner
                      + "/" + getType().getMultipleLink()),
         new Link(getName(),
                  "/" + BaseCharacter.TYPE.getLink() + "/" + m_owner
                      + "/" + getType().getLink() + "/" + getName()));
  }

  @Override
  public String [] getListNavigation()
  {
    return new String [] {
        BaseCharacter.TYPE.getLink(),
        "/" + BaseCharacter.TYPE.getMultipleLink(),
        m_owner.toString(),
        "/" + BaseCharacter.TYPE.getLink() + "/" + m_owner,
        getType().getMultipleLink(),
        "/" + BaseCharacter.TYPE.getLink() + "/" + m_owner
            + "/" + getType().getMultipleLink(),
    };
  }

  public Optional<MiniatureLocation> getLocation()
  {
    Optional<BaseCharacter> user = getUser();
    if(user.isPresent())
      return getLocation(user.get().getMiniatureLocations());

    return Optional.absent();
  }

  private Optional<MiniatureLocation> getLocation(
      List<MiniatureLocation> inLocations)
  {
    int matches = 0;
    MiniatureLocation result = null;
    BaseMiniature base = (BaseMiniature)getBaseEntry();
    for(MiniatureLocation location : inLocations)
    {
      int newMatches = location.matches(base);
      if(newMatches > 0 && location.overrides())
        return Optional.of(location);

      if(newMatches > matches)
      {
        matches = newMatches;
        result = location;
      }
    }

    return Optional.fromNullable(result);
  }

  public Optional<BaseCharacter> getUser() {
    if(m_owner.isPresent())
      return DMADataFactory.get().getEntry(new EntryKey(m_owner.get(),
                                                        BaseCharacter.TYPE));

    return Optional.absent();
  }

  @Override
  public void updateKey(EntryKey inKey)
  {
    Optional<EntryKey> parent = inKey.getParent();
    if(!parent.isPresent())
      return;

    m_owner = Optional.of(parent.get().getID());
  }

  @Override
  public boolean save()
  {
    if(m_name.startsWith(Entry.TEMPORARY))
      do
      {
        randomID();
      } while(DMADataFactory.get().getEntry(getKey()).isPresent());

    return super.save();
  }

  @Override
  public void setOwner(AbstractEntry inOwner)
  {
    if(inOwner instanceof BaseCharacter)
      setOwner((BaseCharacter)inOwner);
  }

  public boolean setOwner(BaseCharacter inOwner)
  {
    m_owner = Optional.of(inOwner.getName());
    return true;
  }

  @Override
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    m_owner = inValues.use("owner",  m_owner);
    m_number = inValues.use("number", m_number, Value.INTEGER_PARSER);
    m_notes = inValues.use("notes", m_notes);
  }

  @Override
  public Message toProto()
  {
    MiniatureProto.Builder builder = MiniatureProto.newBuilder();

    builder.setBase((Entries.EntryProto)super.toProto());

    if(m_owner.isPresent())
      builder.setOwner(m_owner.get());

    if(m_number.isPresent())
      builder.setNumber(m_number.get());

    if(m_notes.isPresent())
      builder.setNotes(m_notes.get());

    return builder.build();
  }

  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof MiniatureProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    MiniatureProto proto = (MiniatureProto)inProto;

    if(proto.hasOwner())
      m_owner = Optional.of(proto.getOwner());

    if(proto.hasNumber())
      m_number = Optional.of(proto.getNumber());

    if(proto.hasNotes())
      m_notes = Optional.of(proto.getNotes());

    super.fromProto(proto.getBase());
  }

  @Override
  protected Message defaultProto()
  {
    return MiniatureProto.getDefaultInstance();
  }
}
