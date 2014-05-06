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

package net.ixitxachitls.dma.entries.extensions;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseContainerProto;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.NewValue;
import net.ixitxachitls.dma.values.Volume;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the container extension for all the entries.
 *
 * @file          BaseContainer.java
 * @author        balsiger@ixitxachils.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public class BaseContainer extends ValueGroup
{
  /** The possible sizes in the game. */
  public enum State implements EnumSelection.Named,
    EnumSelection.Proto<BaseContainerProto.State>
  {
    /** Unknown state. */
    UNKNOWN("unknown", BaseContainerProto.State.UNKNOWN),

    /** Made of paper. */
    SOLID("solid", BaseContainerProto.State.SOLID),

    /** Made of cloth. */
    GRANULAR("granular", BaseContainerProto.State.GRANULAR),

    /** Made of rope. */
    LIQUID("liquid", BaseContainerProto.State.LIQUID),

    /** Made of glass. */
    GASEOUS("gaseous", BaseContainerProto.State.GASEOUS);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseContainerProto.State m_proto;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the proto enum value
     */
    private State(String inName, BaseContainerProto.State inProto)
    {
      m_name = constant("substance.state", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public BaseContainerProto.State toProto()
    {
      return m_proto;
    }

    /**
     * Convert the given proto value into a state enum.
     *
     * @param inProto  the proto to convert
     * @return the converted state
     */
    public static State fromProto(BaseContainerProto.State inProto)
    {
      for(State state : values())
        if(state.m_proto == inProto)
          return state;

      throw new IllegalStateException("invalid state proto: " + inProto);
    }

    /**
     * Get the state from the given string.
     *
     * @param inValue the string representation
     * @return the matching state, if any
     */
    public static Optional<State> fromString(String inValue)
    {
      for(State state : values())
        if(state.getName().equalsIgnoreCase(inValue))
          return Optional.of(state);

      return Optional.absent();
    }

    /**
     * Get the possible names of types.
     *
     * @return a list of the names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();
      for(State state : values())
        names.add(state.getName());

      return names;
    }
  }

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   */
  public BaseContainer(BaseItem inItem)
  {
    m_item = inItem;
  }

  /** The item for this container. */
  protected final BaseItem m_item;

  /** The container's capacity. */
  @Key("capacity")
  protected Optional<Volume> m_capacity = Optional.absent();
  /** The state of substances that can be put into the container. */
  protected State m_state = State.UNKNOWN;

  /**
   * Get the capacity value.
   *
   * @return      the capacity
   */
  public Optional<Volume> getCapacity()
  {
    return m_capacity;
  }

  /**
   * Get the combined capacity of this commodity, including values of bases.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<Volume> getCombinedCapacity()
  {
    if(m_capacity.isPresent())
      return new Combination.Addable<Volume>(m_item, m_capacity.get());

    List<Combination<Volume>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseContainer armor = ((BaseItem)entry).getContainer();
        combinations.add(armor.getCombinedCapacity());
      }

    return new Combination.Addable<Volume>(m_item, combinations);
  }

  /**
   * Get the state value.
   *
   * @return      the state
   */
  public State getState()
  {
    return m_state;
  }

  /**
   * Get the combined state of this commodity, including values of bases.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<State> getCombinedState()
  {
    if(m_state != State.UNKNOWN)
      return new Combination.Max<State>(m_item, m_state);

    List<Combination<State>> combinations = new ArrayList<>();
    for(BaseEntry entry : m_item.getBaseEntries())
      if(entry instanceof BaseItem)
      {
        BaseContainer armor = ((BaseItem)entry).getContainer();
        combinations.add(armor.getCombinedState());
      }

    return new Combination.Max<State>(m_item, combinations);
  }

  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    values.put(Index.Path.CAPACITIES, m_capacity.toString());
    values.put(Index.Path.STATES, m_state.toString());

    return values;
  }

  /**
   * Check whether any armor values are defined.
   *
   * @return true if armor values are defined, false if not
   */
  public boolean hasValues()
  {
    return m_state != State.UNKNOWN && m_capacity.isPresent();
  }

  @Override
  public void set(Values inValues)
  {
    m_capacity = inValues.use("container.capacity", m_capacity, Volume.PARSER);
    m_state = inValues.use("container.state", m_state,
                          new NewValue.Parser<State>(1)
   {
      @Override
      public Optional<State> doParse(String inValue)
      {
        return State.fromString(inValue);
      }
    });
  }

  @Override
  public Message toProto()
  {
    BaseContainerProto.Builder builder = BaseContainerProto.newBuilder();

    if(m_capacity.isPresent())
      builder.setCapacity(m_capacity.get().toProto());
    if(m_state != State.UNKNOWN)
      builder.setState(m_state.toProto());

    return builder.build();
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseContainerProto))
    {
      Log.warning("cannot parse base container proto " + inProto.getClass());
      return;
    }

    BaseContainerProto proto = (BaseContainerProto)inProto;

    if(proto.hasCapacity())
      m_capacity = Optional.of(Volume.fromProto(proto.getCapacity()));
    if(proto.hasState())
      m_state = State.fromProto(proto.getState());
  }

  @Override
  public <T extends AbstractEntry> AbstractType<T> getType()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getEditType()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AbstractEntry getEntry()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getName()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getID()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void changed(boolean inChanged)
  {
    // TODO Auto-generated method stub

  }

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
