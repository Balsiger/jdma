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

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Group;
import net.ixitxachitls.dma.values.Volume;
import net.ixitxachitls.util.Grouping;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the container extension for all the entries.
 *
 * @file          BaseContainer.java
 *
 * @author        balsiger@ixitxachils.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BaseContainer extends BaseExtension<BaseItem>
{
  //----------------------------------------------------------------- nested

  //----- states -----------------------------------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The possible sizes in the game. */
  public enum State implements EnumSelection.Named
  {
    /** Made of paper. */
    SOLID("solid"),

    /** Made of cloth. */
    GRANULAR("granular"),

    /** Made of rope. */
    LIQUID("liquid"),

    /** Made of glass. */
    GASEOUS("gaseous");

    /** The value's name. */
    private String m_name;

    /** Create the name.
     *
     * @param inName     the name of the value
     *
     */
    private State(String inName)
    {
      m_name = constant("substance.state", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
    {
      return m_name;
    }
  };

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------ BaseContainer ---------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inName  the name of the extension
   *
   */
  public BaseContainer(BaseItem inEntry, String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //------------------------------ BaseContainer ---------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   */
  // public BaseContainer(BaseItem inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  //----- capacity ---------------------------------------------------------

  /** The grouping for liquid capacities in 'feet'. */
  protected static final Group<Volume, Long, String> s_liquidLiterGrouping =
    new Group<Volume, Long, String>(new Group.Extractor<Volume, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Volume inValue)
        {
          return (long)inValue.getAsLiters().getValue() * 100;
        }
      }, new Long [] { 1L, 10L, 1 * 100L, 2 * 100L, 5 * 100L, 10 * 100L,
                       20 * 100L, 50 * 100L, 100 * 100L, },
                                    new String []
        { "1 cl", "1 dl", "1 l", "2 l", "5 l", "10 l", "20 l", "50 l", "100 l",
          "a lot" }, "$undefined$");

  /** The grouping for liquid capacities in 'meter'. */
  protected static final Group<Volume, Long, String> s_liquidFeetGrouping =
    new Group<Volume, Long, String>(new Group.Extractor<Volume, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Volume inValue)
        {
          return (long)inValue.getAsGallons().getValue() * 16;
        }
      }, new Long [] { 1L, 2L, 4L, 1 * 16L, 2 * 16L, 5 * 16L, 10 * 16L,
                       20 * 16L, 50 * 16L, 100 * 16L, },
                               new String []
      { "1 cup", "1 ping", "1 quart", "1 gallon", "2 gallons", "5 gallons",
        "10 gallons", "20 gallons", "50 gallons", "100 gallons",
        "a lot liquid" }, "$undefined$");

  /** The grouping for liquid capacities in 'meter'. */
  protected static final Group<Volume, Long, String> s_solidFeetGrouping =
    new Group<Volume, Long, String>(new Group.Extractor<Volume, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Volume inValue)
        {
          return (long)inValue.getAsFeet().getValue() * 1728;
        }
      }, new Long [] { 1L, 1 * 1728L, 2 * 1728L, 5 * 1728L, 10 * 1728L,
                       20 * 1728L, 50 * 1728L, 100 * 1728L, },
                               new String []
      { "1 cu in", "1 cu ft", "2 cu ft", "5 cu ft", "10 cu ft", "20 cu ft",
        "50 cu ft", "100 cu ft", "a lot" }, "$undefined$");

  /** The grouping for liquid capacities in 'feet'. */
  protected static final Group<Volume, Long, String> s_solidMeterGrouping =
    new Group<Volume, Long, String>(new Group.Extractor<Volume, Long>()
      {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public Long extract(Volume inValue)
        {
          return (long)inValue.getAsLiters().getValue() * 1000;
        }
      }, new Long [] { 1L, 1 * 1000L, 2 * 1000L, 5 * 1000L, 10 * 1000L,
                       20 * 1000L, 50 * 1000L, 100 * 1000L, },
                               new String []
      { "1 cu dm", "1 cu m", "2 cu m", "5 cu m", "10 cu m", "20 cu m",
        "50 cu m", "100 cu m", "a lot" }, "$undefined$");

  /** The grouping for the capacity. */
  protected static final Grouping<Volume, String> s_capacityGrouping =
    new Grouping<Volume, String>()
    {
      /**
       *
       */
      private static final long serialVersionUID = 1L;

      @Override
      public String group(Volume inValue)
      {
        Volume volume = inValue;

        if(volume.isLiquid())
          if(volume.isFeet())
            return s_liquidFeetGrouping.group(inValue);
          else
            return s_liquidLiterGrouping.group(inValue);
        else
          if(volume.isFeet())
            return s_solidFeetGrouping.group(inValue);
          else
            return s_solidMeterGrouping.group(inValue);
      }
    };

  /** The container's capacity. */
  @Key("capacity")
  protected Volume m_capacity = new Volume()
    .withGrouping(s_capacityGrouping);

  static
  {
    addIndex(new Index(Index.Path.CAPACITIES, "Container Capacities",
                       BaseItem.TYPE));
  }

  //........................................................................
  //----- state ------------------------------------------------------------

  /** The state of substances that can be put into the container. */
  @Key("state")
  protected EnumSelection<State> m_state =
    new EnumSelection<State>(State.class);

  static
  {
    addIndex(new Index(Index.Path.STATES, "Container States", BaseItem.TYPE));
  }

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

    ioValues.put(Index.Path.CAPACITIES, m_capacity.group());
    ioValues.put(Index.Path.STATES, m_state.group());
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
