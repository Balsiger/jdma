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


package net.ixitxachitls.dma.entries;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.proto.Entries.CampaignEntryProto;
import net.ixitxachitls.dma.proto.Entries.LevelProto;
import net.ixitxachitls.util.logging.Log;

/**
 * An actual character level.
 *
 * @file   Level.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */
public class Level extends Entry<BaseLevel>
{
  /**
   * Create a default, unnamed level.
   */
  public Level()
  {
    super(TYPE);
  }

  /**
   * Create a level with a given name.
   *
   * @param inName the name of the level
   */
  public Level(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final Type<Level> TYPE =
    new Type<Level>(Level.class, BaseLevel.TYPE);

  /** The type of the base entry to this entry. */
  public static final BaseType<BaseLevel> BASE_TYPE = BaseLevel.TYPE;

  /** The serial version uid. */
  private static final long serialVersionUID = 1L;

  static
  {
    extractVariables(Level.class);
  }

  @Override
  public Message toProto()
  {
    LevelProto.Builder builder = LevelProto.newBuilder();

    builder.setBase((CampaignEntryProto)super.toProto());

    LevelProto proto = builder.build();
    return proto;
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof LevelProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    LevelProto proto = (LevelProto)inProto;

    super.fromProto(proto.getBase());

  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(LevelProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }
}
