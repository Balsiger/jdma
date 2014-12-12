/*******************************************************************************
 * Copyright (c) 2002-2014 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.annotation.Nullable;

import com.google.appengine.labs.repackaged.com.google.common.collect.HashMultimap;
import com.google.appengine.labs.repackaged.com.google.common.collect.LinkedListMultimap;
import com.google.common.base.Optional;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.proto.Entries;
import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.util.logging.Log;
import net.ixitxachitls.util.resources.Resource;

/**
 * A fake dma datastore for testing.
 *
 * @author balsiger@ititxachitls.net (Peter Balsiger)
 */
public class FakeDMADatastore extends DMADatastore
{
  private final Map<EntryKey, AbstractEntry> m_entries = new HashMap<>();
  private final LinkedListMultimap<AbstractType<?>, AbstractEntry>
      m_entriesByType = LinkedListMultimap.create();

  public FakeDMADatastore()
  {
    try
    {
      Entries.EntriesProto.Builder builder = Entries.EntriesProto.newBuilder();
      TextFormat.merge(
          new InputStreamReader(Resource.get("fake_data.ascii").getInput()),
          builder);

      for(Entries.BaseEntryProto proto : builder.getBaseEntryList())
        add(proto.getAbstract().getName(), BaseEntry.TYPE, proto);
      for(Entries.BaseCharacterProto proto : builder.getBaseCharacterList())
        add(proto.getBase().getAbstract().getName(), BaseCharacter.TYPE, proto);
    }
    catch(IOException e)
    {
      Log.error("Cannot read fake data store proto file: " + e);
    }
  }

  private void add(String inID, AbstractType<?> inType, Message inProto)
  {
    Optional<? extends AbstractEntry> entry = inType.create();
    if(!entry.isPresent())
      return;

    entry.get().fromProto(inProto);

    m_entries.put(new EntryKey(inID, inType), entry.get());
    m_entriesByType.put(inType, entry.get());
  }

  @Override
  public <T extends AbstractEntry> Optional<T> getEntry(EntryKey inKey)
  {
    return Optional.fromNullable((T) m_entries.get(inKey));
  }

  @Override
  public <T extends AbstractEntry> List<T>
  getEntries(AbstractType<T> inType, @Nullable EntryKey inParent,
             int inStart, int inSize)
  {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public <T extends AbstractEntry>
  Optional<T> getEntry(AbstractType<T> inType, String inKey, String inValue)
  {
    for(AbstractEntry entry : m_entriesByType.get(inType)) {
      if(entry instanceof BaseCharacter)
        switch(inKey)
        {
          case "email":
            if(((BaseCharacter)entry).getEmail().equals(inValue))
              return Optional.of((T)entry);
        }
    }

    return Optional.absent();
  }

  @Override
  public @Nullable <T extends AbstractEntry>
  List<T> getEntries(AbstractType<T> inType, @Nullable EntryKey inParent,
                     String inKey, String inValue)
  {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public List<String> getIDs(AbstractType<?> inType,
                             @Nullable EntryKey inParent)
  {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public <T extends AbstractEntry>
  List<T> getRecentEntries(AbstractType<T> inType, @Nullable EntryKey inParent)
  {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public ListMultimap<String, String> getOwners(String inID)
  {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public List<AbstractEntry> getIndexEntries(String inIndex,
                                             AbstractType<?> inType,
                                             @Nullable EntryKey inParent,
                                             String inGroup,
                                             int inStart, int inSize)
  {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public SortedSet<String> getIndexNames
      (String inIndex,
       AbstractType<? extends AbstractEntry> inType, boolean inCached,
       String ... inFilters)
  {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public List<List<String>> getMultiValues
      (AbstractType<? extends AbstractEntry> inType, String ... inFields)
  {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public SortedSet<String> getValues
      (AbstractType<? extends AbstractEntry> inType, String inField)
  {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public boolean isChanged()
  {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public boolean update(AbstractEntry inEntry)
  {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public boolean save(AbstractEntry inEntry)
  {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public int rebuild(AbstractType<? extends AbstractEntry> inType)
  {
    throw new UnsupportedOperationException("not yet implemented");
  }

  @Override
  public int refresh(AbstractType<? extends AbstractEntry> inType,
                     DMARequest inRequest)
  {
    throw new UnsupportedOperationException("not yet implemented");
  }
}