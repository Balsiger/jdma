/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.entries.extensions;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.proto.Entries.BaseMultiuseProto;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the commodity extension for all the entries.
 *
 * @file          BaseMultiuse.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BaseMultiuse extends BaseCounted
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- BaseMultiuse ----------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inName  the name of the extension
   *
   */
  public BaseMultiuse(BaseItem inEntry, String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //----------------------------- BaseMultiuse ----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   * @undefined   never
   *
   */
  // public BaseMultiuse(BaseItem inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  @Override
  public Message toProto()
  {
    BaseMultiuseProto.Builder builder = BaseMultiuseProto.newBuilder();

    builder.setCount((int)m_count.get());

    return builder.build();
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseMultiuseProto))
    {
      Log.warning("cannot parse base multiuse proto " + inProto.getClass());
      return;
    }

    BaseMultiuseProto proto = (BaseMultiuseProto)inProto;

    m_count = m_count.as(proto.getCount());
  }

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
