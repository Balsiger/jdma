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

import com.google.common.collect.Multimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.BaseItem.AreaShapes;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseLightProto;
import net.ixitxachitls.dma.values.Distance;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Group;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the light extension for all the entries.
 *
 * @file          BaseLight.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BaseLight extends BaseExtension<BaseItem>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- BaseLight ------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inName  the name of the extension
   *
   */
  public BaseLight(BaseItem inEntry, String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //------------------------------- BaseLight ------------------------------

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
  // public BaseLight(BaseItem inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  //----- bright light -----------------------------------------------------

  /** The grouping for bright light. */
  protected static final Group<Multiple, Long, String> s_brightGrouping =
    new Group<Multiple, Long, String>(new Group.Extractor<Multiple, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Multiple inValue)
        {
          return (long)((Distance)inValue.get(0)).getAsFeet().getValue();
        }
      }, new Long [] { 0L, 5L, 10L, 25L, 50L, 100L, 250L, },
                                new String []
      { "0 ft bright", "5 ft bright", "10 ft bright", "25 ft bright",
        "50 ft bright", "100 ft bright", "250 ft bright", "infinite bright",
      }, "$undefined$");

  /** The light radius. */
  @Key("bright light")
  protected Multiple m_brightLight = new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new Distance(), false),
      new Multiple.Element(new EnumSelection<BaseItem.AreaShapes>
                           (BaseItem.AreaShapes.class), false),
    }).withGrouping(s_brightGrouping);

  static
  {
    addIndex(new Index(Index.Path.LIGHTS, "Lights", BaseItem.TYPE));
  }

  //........................................................................
  //----- shadowy light ----------------------------------------------------

  /** The grouping for shadowy light. */
  protected static final Group<Multiple, Long, String> s_shadowyGrouping =
    new Group<Multiple, Long, String>(new Group.Extractor<Multiple, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Multiple inValue)
        {
          return (long)((Distance)inValue.get(0)).getAsFeet().getValue();
        }
      }, new Long [] { 0L, 5L, 10L, 25L, 50L, 100L, 250L, },
                                new String []
      { "0 ft shadowy", "5 ft shadowy", "10 ft shadowy", "25 ft shadowy",
        "50 ft shadowy", "100 ft shadowy", "250 ft shadowy",
        "infinite shadowy", }, "$undefined$");

  /** The light radius. */
  @Key("shadowy light")
  protected Multiple m_shadowyLight = new Multiple(new Multiple.Element []
    {
      new Multiple.Element(new Distance(), false),
      new Multiple.Element(new EnumSelection<BaseItem.AreaShapes>
                           (BaseItem.AreaShapes.class), false),
    }).withGrouping(s_shadowyGrouping);

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------- computeIndexValues ---------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @param       ioValues a multi map of values per index name
   *
   */
  @Override
  public void computeIndexValues(Multimap<Index.Path, String> ioValues)
  {
    super.computeIndexValues(ioValues);

    // light
    ioValues.put(Index.Path.LIGHTS, m_brightLight.group());
    ioValues.put(Index.Path.LIGHTS, m_shadowyLight.group());
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  @SuppressWarnings("unchecked")
  @Override
  public Message toProto()
  {
    BaseLightProto.Builder builder = BaseLightProto.newBuilder();

    if(m_brightLight.isDefined())
      builder.setBright(BaseLightProto.Light.newBuilder()
                        .setDistance(((Distance)m_brightLight.get(0)).toProto())
                        .setShape(((EnumSelection<AreaShapes>)m_brightLight
                                  .get(1)).getSelected().toProto())
                        .build());
    if(m_shadowyLight.isDefined())
      builder.setShadowy(BaseLightProto.Light.newBuilder()
                         .setDistance(((Distance)m_shadowyLight.get(0))
                                      .toProto())
                         .setShape(((EnumSelection<AreaShapes>)m_shadowyLight
                                  .get(1)).getSelected().toProto())
                         .build());

    return builder.build();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseLightProto))
    {
      Log.warning("cannot parse base light proto " + inProto.getClass());
      return;
    }

    BaseLightProto proto = (BaseLightProto)inProto;

    if(proto.hasBright())
      m_brightLight =
        m_brightLight.as(((Distance)m_brightLight.get(0))
                         .fromProto(proto.getBright().getDistance()),
                         ((EnumSelection<AreaShapes>)m_brightLight.get(1))
                         .as(AreaShapes.fromProto(proto.getBright()
                                                  .getShape())));
    if(proto.hasShadowy())
      m_shadowyLight =
        m_shadowyLight.as(((Distance)m_shadowyLight.get(0))
                          .fromProto(proto.getShadowy().getDistance()),
                          ((EnumSelection<AreaShapes>)m_shadowyLight.get(1))
                          .as(AreaShapes.fromProto(proto.getShadowy()
                                                   .getShape())));
  }

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
