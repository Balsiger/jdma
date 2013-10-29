/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.proto.Entries.CampaignEntryProto;
import net.ixitxachitls.dma.proto.Entries.CharacterProto;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Money;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Rational;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.Weight;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The storage space for a character in the game.
 *
 * In the long run, this will probably be derived from Monster or even NPC,
 * but we don't currently have these available. We still want to manage some
 * values now, like items, thus this class is currently incomplete.
 *
 * @file          Character.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class Character extends CampaignEntry<BaseCharacter>
                       //implements Storage<Item>
{
  //----------------------------------------------------------------- nested

  //----- state -------------------------------------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The character state. */
  public enum State implements EnumSelection.Named,
    EnumSelection.Proto<CharacterProto.State>
  {
    /** A normal character going on adventures. */
    ADVENTURING("adventuring", CharacterProto.State.ADVENTURING),
    /** The character is currently incapable of adventuring. */
    INCAPACITATED("incapacitated", CharacterProto.State.INCAPACITATED),
    /** The character has been retired by the player or the DM. */
    RETIRED("retired", CharacterProto.State.RETIRED),
    /** The character died. */
    DEAD("dead", CharacterProto.State.DEAD);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private CharacterProto.State m_proto;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the proto enum value
     */
    private State(String inName, CharacterProto.State inProto)
    {
      m_name = constant("character.state", inName);
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
    public CharacterProto.State toProto()
    {
      return m_proto;
    }

    public static State fromProto(CharacterProto.State inProto)
    {
      for(State state : values())
        if(state.m_proto == inProto)
          return state;

      throw new IllegalArgumentException("cannot convert state: " + inProto);
    }
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------ Character -------------------------------

  /**
   * Create the character.
   *
   */
  protected Character()
  {
    super(TYPE, TYPE.getBaseType());
  }

  //........................................................................
  //------------------------------ Character -------------------------------

  /**
   * Create the character with an name.
   *
   * @param    inName the name of the character to create
   *
   */
  public Character(String inName)
  {
    super(inName, TYPE, TYPE.getBaseType());
  }

  //.......................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final Type<Character> TYPE =
    new Type<Character>(Character.class, BaseCharacter.TYPE);

  //----- state ------------------------------------------------------------

  /** The state value. */
  @Key("state")
  protected EnumSelection<State> m_state =
    new EnumSelection<State>(State.class);

  //........................................................................
  //----- items ------------------------------------------------------------

  /** The possessions value. */
  @Key("items")
  protected ValueList<Name> m_items = new ValueList<Name>(new Name());

  //........................................................................
  //----- level ------------------------------------------------------------

  /** The character level. This is a big simplification and has to be replaced
   * by a list of classes and their levels. */
  @Key("level")
  protected Number m_level = new Number(1, 1, 100);

  //........................................................................

  //----- wealth -----------------------------------------------------------

  /** The standard wealth per level in gold pieces. */
  private static final int []s_wealth =
    Config.get("/game/wealth.per.level", new int []
      {
        0,
        900,
        2700,
        5400,
        9000,
        13000,
        19000,
        27000,
        36000,
        49000,
        66000,
        88000,
        110000,
        150000,
        200000,
        260000,
        340000,
        440000,
        580000,
        760000,
      });

  //........................................................................

  static
  {
    extractVariables(Character.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //---------------------------- containedItems ----------------------------

  /**
   * Get all the items contained in this contents.
   *
   * @param       inDeep true for returning all item, including nested ones,
   *                     false for only the top level items
   * @return      a list with all the items
   *
   */
  public Map<String, Item> containedItems(boolean inDeep)
  {
    Map<String, Item> items = new HashMap<String, Item>();
    for(Name name : m_items)
    {
      Item item = getCampaign().getItem(name.get());
      if(item == null)
        continue;

      items.put(name.get(), item);
      items.putAll(item.containedItems(inDeep));
    }

    return items;
  }

  //........................................................................
  //------------------------------ possesses -------------------------------

  /**
   * Checks if the character has the item in possession.
   *
   * NOTE: this is an expensive operation, as it has to read all the items
   * recursively of a character.
   *
   * @param       inItem the name of the item to check
   *
   * @return      true if the character possesses the item, false if not
   *
   */
  public boolean possesses(String inItem)
  {
    return containedItems(true).containsKey(inItem);
  }

  //........................................................................

  //-------------------------- getCharacterLevel ---------------------------

  /**
   * Get the character level.
   *
   * @return      the level
   *
   */
  public int getCharacterLevel()
  {
    return (int)m_level.get();
  }

  //........................................................................
  //---------------------------- getStorageName ----------------------------

  /**
   * Get the name of the file.
   *
   * @return      the name of the file (without 'unnecessary path' info)
   *
   */
  // public String getStorageName()
  // {
  //   return m_storage.getStorageName() + ":" + getName();
  // }

  //........................................................................
  //----------------------------- getStorageID -----------------------------

  /**
   * Get the id of the storage or null if none (usually if not an value group).
   *
   * @return      the id of the storage or null
   *
   */
  // public String getStorageID()
  // {
  //   return getID();
  // }

  //........................................................................

  //---------------------------- wealthPerLevel ----------------------------

  /**
   * Get the wealth a character should approximately have for the given level.
   *
   * @param       inLevel the level for which to compute the wealth
   *
   * @return      the wealth for the given level in gp
   *
   */
  public static int wealthPerLevel(int inLevel)
  {
    if(inLevel <= 0)
      return s_wealth[0];

    if(inLevel > 20)
      return s_wealth[19] + (inLevel - 20) * (s_wealth[19] - s_wealth[18]);

    return s_wealth[inLevel - 1];
  }

  //........................................................................
  //----------------------------- totalWealth ------------------------------

  /**
   * The total wealth in gp of the character.
   *
   * @return      the gp value of all items
   *
   */
  public Money totalWealth()
  {
    Money total = new Money(0, 0, 0, 0);

    for(Name name : m_items)
    {
      Item item = getCampaign().getItem(name.get());
      if(item == null)
        continue;

      Money value = item.getValue();
      if(value != null)
        total = total.add(value);
    }

    return total;
  }

  //........................................................................
  //----------------------------- totalWeight ------------------------------

  /**
   * The total weight in pounts of the character.
   *
   * @return      the lb value of all items
   *
   */
  public Weight totalWeight()
  {
    Weight total = new Weight(new Rational(0), null);

    for(Name name : m_items)
    {
      Item item = getCampaign().getItem(name.get());
      if(item == null)
        continue;

      Weight weight = item.getTotalWeight();
      if(weight != null)
        total = total.add(weight);
    }

    return total;
  }

  //........................................................................

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
    if("icon".equals(inKey))
    {
      DMAData.File main = getMainFile();
      if(main == null)
        return new Name("character/person.png");
      else
        return new Name(main.getIcon() + "=s100");
    }

    if("wealth".equals(inKey))
      return new ImmutableMap.Builder<String, Object>()
        .put("total", (int)totalWealth().getAsGold().getValue())
        .put("lower", wealthPerLevel((int)m_level.get() - 1))
        .put("equal", wealthPerLevel((int)m_level.get()))
        .put("higher", wealthPerLevel((int)m_level.get() + 1))
        .build();

    if("weight".equals(inKey))
      return totalWeight();

    return super.compute(inKey);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- add ----------------------------------

  /**
   * Add the given entry to the character entry.
   *
   * @param       inEntry the entry to add
   *
   * @return      true if added, false if not
   *
   */
  @Override
  public boolean add(CampaignEntry<?> inEntry)
  {
    String name = inEntry.getName();
    List<Name> names = new ArrayList<Name>();
    for(Name item : m_items)
      if(name.equals(item.get()))
        return true;
      else
        names.add(item);

    names.add(m_items.newElement().as(name));
    m_items = m_items.as(names);

    inEntry.setParent(getKey());

    changed();
    save();
    return true;
  }

  //........................................................................
  //------------------------------ updateKey -------------------------------

  /**
   * Update the any values that are related to the key with new data.
   *
   * @param       inKey the new key of the entry
   *
   */
  @Override
  public void updateKey(EntryKey<? extends AbstractEntry> inKey)
  {
    EntryKey<?> parent = inKey.getParent();
    if(parent == null)
      return;

    EntryKey<?> parentParent = parent.getParent();
    if(parentParent == null)
      return;

    m_campaign = m_campaign.as(new Name(parentParent.getID()),
                               new Name(parent.getID()));
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  @Override
  public Message toProto()
  {
    CharacterProto.Builder builder = CharacterProto.newBuilder();

    builder.setBase((CampaignEntryProto)super.toProto());

    if(m_state.isDefined())
      builder.setState(m_state.getSelected().toProto());

    if(m_items.isDefined())
      for(Name item : m_items)
        builder.addItem(item.get());

    if(m_level.isDefined())
      builder.setLevel((int)m_level.get());

    CharacterProto proto = builder.build();
    System.out.println(proto);
    System.out.println(this);
    return proto;
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof CharacterProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    CharacterProto proto = (CharacterProto)inProto;

    if(proto.hasState())
      m_state = m_state.as(State.fromProto(proto.getState()));

    if(proto.getItemCount() > 0)
    {
      List<Name> items = new ArrayList<>();
      for(String item : proto.getItemList())
        items.add(m_items.createElement().as(item));

      m_items = m_items.as(items);
    }

    if(proto.hasLevel())
      m_level = m_level.as(proto.getLevel());

    super.fromProto(proto.getBase());

    System.out.println(proto);
    System.out.println(this);
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(CharacterProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

  //........................................................................
}
