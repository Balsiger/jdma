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

//------------------------------------------------------------------ imports

package net.ixitxachitls.dma.entries.extensions;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.proto.Entries.BaseIncompleteProto;
import net.ixitxachitls.dma.proto.Entries.BaseWearableProto;
import net.ixitxachitls.dma.values.LongFormattedText;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the incomplete extension for all the entries.
 *
 * @file          BaseIncomplete.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BaseIncomplete extends BaseExtension<BaseEntry>
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- BaseIncomplete ---------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor.
   *
   * @param       inEntry the base entry attached to
   * @param       inName  the name of the extension
   *
   */
  public BaseIncomplete(BaseEntry inEntry, String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //----------------------------- BaseIncomplete ---------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base entry attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   * @undefined   never
   *
   */
  // public BaseIncomplete(BaseEntry inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  // /** The printer for printing the whole base item. */
  // public static final Print s_pagePrint =
  //   new Print("%incomplete");

  //----- incomplete -------------------------------------------------------

  /** The incomplete radius. */
  @Key("incomplete")
  protected LongFormattedText m_incomplete = new LongFormattedText();

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  @Override
  public Message toProto()
  {
    BaseIncompleteProto.Builder builder = BaseIncompleteProto.newBuilder();

    if(m_incomplete.isDefined())
      builder.setText(m_incomplete.get());

    return builder.build();
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseWearableProto))
    {
      Log.warning("cannot parse base wearable proto " + inProto.getClass());
      return;
    }

    BaseIncompleteProto proto = (BaseIncompleteProto)inProto;

    if(proto.hasText())
      m_incomplete = m_incomplete.as(proto.getText());
  }

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
