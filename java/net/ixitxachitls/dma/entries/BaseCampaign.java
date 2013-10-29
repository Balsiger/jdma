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

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.BaseCampaignProto;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base information about a campaign. It is also the place
 * where all the base entries are finally stored..
 *
 * @file          Basecampaign.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BaseCampaign extends BaseEntry
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- BaseCampaign ------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * This is the internal, default constructor for an undefined value.
   *
   */
  public BaseCampaign()
  {
    super(TYPE);
  }

  //........................................................................
  //---------------------------- BaseCampaign ------------------------------

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base item
   *
   */
  public BaseCampaign(String inName)
  {
    super(inName, TYPE);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final BaseType<BaseCampaign> TYPE =
    new BaseType<BaseCampaign>(BaseCampaign.class);

  static
  {
    extractVariables(BaseCampaign.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- compute --------------------------------

  /**
   * Compute a value for a given key, taking base entries into account if
   * available.
   *
   * @param    inKey the key of the value to compute
   *
   * @return   the compute value
   *
   */
  @Override
  public @Nullable Object compute(String inKey)
  {
    if("campaigns".equals(inKey))
    {
      List<Name> names = new ArrayList<Name>();
      for(String name : DMADataFactory.get().getIDs(Campaign.TYPE, getKey()))
        names.add(new Name(name));

      return new ValueList<Name>(names);
    }

    return super.compute(inKey);
  }

  //........................................................................

  //--------------------------------- isDM ---------------------------------

  /**
   * Check whether the given user is the DM for this entry. Every user is a DM
   * for a base campaign.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   *
   */
  @Override
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    if(inUser == null)
      return false;

    return inUser.hasAccess(BaseCharacter.Group.ADMIN);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  @Override
  public Message toProto()
  {
    BaseCampaignProto.Builder builder = BaseCampaignProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    BaseCampaignProto proto = builder.build();
    return proto;
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseCampaignProto))
    {
      Log.warning("cannot parse proto " + inProto.getClass());
      return;
    }

    BaseCampaignProto proto = (BaseCampaignProto)inProto;

    super.fromProto(proto.getBase());
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(BaseCampaignProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. @hidden */
  public static class Test extends ValueGroup.Test
  {
    // TODO: fix tests
    //----- text -----------------------------------------------------------

    /** Test text. */
    private static String s_text =
      "base campaign Test = \n"
      + "\n"
      + "  synonyms          \"test\", \"tst\";"
      + "  worlds            Generic, Forgotten Realms;"
      + "  short description \"Just a test\";"
      + "  description       \"A test campaign\".";

    //......................................................................
    //----- createBaseCampaign() -------------------------------------------

    /** Create a typical base item for testing purposes.
     *
     * @return the newly created base item
     *
     */
    public static BaseCampaign createBaseCampaign()
    {
      net.ixitxachitls.input.ParseReader reader =
        new net.ixitxachitls.input.ParseReader
        (new java.io.StringReader(s_text), "test"); // $codepro.audit.disable

      return (BaseCampaign)AbstractEntry.read(reader);
    }

    //......................................................................
  }

  //........................................................................
}
