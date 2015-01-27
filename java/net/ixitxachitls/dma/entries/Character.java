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

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Optional;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.CharacterProto;
import net.ixitxachitls.dma.proto.Entries.NPCProto;
import net.ixitxachitls.dma.values.File;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.CharacterState;
import net.ixitxachitls.util.logging.Log;

/**
 * The storage space for a character in the game.
 *
 * In the long run, this will probably be derived from Monster or even NPC,
 * but we don't currently have these available. We still want to manage some
 * values now, like items, thus this class is currently incomplete.
 *
 * @file          Character.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Character extends NPC
                       //extends CampaignEntry
                       //implements Storage<Item>
{
  /**
   * Create the character.
   */
  protected Character()
  {
    super(TYPE);
  }

  /**
   * Create the character with an name.
   *
   * @param    inName the name of the character to create
   */
  public Character(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final Type<Character> TYPE =
    new Type.Builder<>(Character.class, BaseMonster.TYPE).build();

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The state value. */
  protected CharacterState m_state = CharacterState.UNKNOWN;

  /** The current experience poins. */
  protected int m_xp = 0;

  /** TODO: not sure if and for what this is needed
  protected Optional<String> m_monsterName = Optional.absent();
  protected Optional<Optional<Monster>> m_monster = Optional.absent();
   */

  /** The name of the player for this character. */
  protected Optional<String> m_playerName = Optional.absent();

  /**
   * The base character for this one. If this is absent, the resolving it was
   * not tried. If the values present is optional, it means the base character
   * could not be found.
   */
  protected Optional<Optional<BaseCharacter>> m_player = Optional.absent();

  /** The possessions value. */
  protected List<String> m_items = new ArrayList<>();

  /**
   * The standard wealth per level in gold pieces.
   * There is no closed formula for this.
   */
  private static final int []s_wealth = new int []
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
      };

  /**
   * Get all the items contained in this contents.
   *
   * @param       inDeep true for returning all item, including nested ones,
   *                     false for only the top level items
   * @return      a list with all the items
   */
  /*
  public Map<String, Item> containedItems(boolean inDeep)
  {
    Map<String, Item> items = new HashMap<String, Item>();

    if(getCampaign().isPresent())
      for(Name name : m_items)
      {
        Item item = getCampaign().get().getItem(name.get());
        if(item == null)
          continue;

        items.put(name.get(), item);
        //items.putAll(item.containedItems(inDeep));
      }

    return items;
  }
  */

  /**
   * Checks if the character has the item in possession.
   *
   * NOTE: this is an expensive operation, as it has to read all the items
   * recursively of a character.
   *
   * @param       inItem the name of the item to check
   *
   * @return      true if the character possesses the item, false if not
   */
  /*
  public boolean possesses(String inItem)
  {
    return containedItems(true).containsKey(inItem);
  }
  */

  /**
   * Simple getter for state.
   *
   * @return the state
   */
  public CharacterState getState()
  {
    return m_state;
  }

  /**
   * Simple getter for items.
   *
   * @return the items
   */
  public List<String> getItems()
  {
    return m_items;
  }

  /**
   * Get the name of the base character.
   *
   * @return the base character name
   */
  public Optional<String> getBaseCharacterName()
  {
    return m_playerName;
  }

  /**
   * Get the player responsible for the character.
   *
   * @return the player
   */
  public Optional<BaseCharacter> getPlayer()
  {
    if(!m_player.isPresent())
    {
      if(m_playerName.isPresent())
        m_player = Optional.of(DMADataFactory.get().<BaseCharacter>getEntry
            (new EntryKey(m_playerName.get(), BaseCharacter.TYPE)));
      else
        return Optional.absent();
    }

    return m_player.get();
  }

  /**
   * Get the wealth a character should approximately have for the given level.
   *
   * @param       inLevel the level for which to compute the wealth
   *
   * @return      the wealth for the given level in gp
   */
  public static int wealthPerLevel(int inLevel)
  {
    if(inLevel <= 0)
      return s_wealth[0];

    if(inLevel > 20)
      return s_wealth[19] + (inLevel - 20) * (s_wealth[19] - s_wealth[18]);

    return s_wealth[inLevel - 1];
  }

  /**
   * Get the current number of experience points.
   *
   * @return the current xp level
   */
  public int getXP()
  {
    return m_xp;
  }

  /**
   * Get the experience points necessary to achieve the next level.
   *
   * @return the xp needed for the next level
   */
  public int nextLevelXP()
  {
    return minXP(getEffectiveCharacterLevel() + 1);
  }

  /**
   * Compute the minimal number of xp for the given character level.
   *
   * @param level the level to compute xp for
   * @return the minimal number of xp
   */
  public static int minXP(int level)
  {
    // xp per level = sum 1..n-1 * 1000 = n * (n-1)/2 * 1000
    return level * (level - 1) * 500;
  }

  /**
   * The total wealth in gp of the character.
   *
   * @return      the gp value of all items
   */
  /*
  public NewMoney totalWealth()
  {
    NewMoney total = new NewMoney(0, 0, 0, 0, 0, 0);

    if(getCampaign().isPresent())
      for(Name name : m_items)
      {
        Item item = getCampaign().get().getItem(name.get());
        if(item == null)
          continue;

        NewMoney value = item.getCombinedValue().getValue();
        if(value != null)
          total = (NewMoney)total.add(value);
      }

    return total;
  }
  */

  /**
   * The total weight in pounts of the character.
   *
   * @return      the lb value of all items
   */
  /*
  public NewWeight totalWeight()
  {
    NewWeight total = new NewWeight(Optional.of(NewRational.ZERO),
                                    Optional.of(NewRational.ZERO));

    if(getCampaign().isPresent())
      for(Name name : m_items)
      {
        Item item = getCampaign().get().getItem(name.get());
        if(item == null)
          continue;

        NewWeight weight = item.getCombinedWeight().getValue();
        if(weight != null)
          total = (NewWeight)total.add(weight);
      }

    return total;
  }
  */

  /**
   * Compute a value for a given key, taking base entries into account if
   * available.
   *
   * @ param    inKey the key of the value to compute
   *
   * @return   the compute value
   */
  /*
  @Override
  public @Nullable Object compute(String inKey)
  {
    if("wealth".equals(inKey))
      return new ImmutableMap.Builder<String, Object>()
        .put("total", (int)totalWealth().asGold())
        .put("lower", wealthPerLevel((int)m_level.get() - 1))
        .put("equal", wealthPerLevel((int)m_level.get()))
        .put("higher", wealthPerLevel((int)m_level.get() + 1))
        .build();

    if("weight".equals(inKey))
      return totalWeight();

    return super.compute(inKey);
  }
  */

  /**
   * Get the icon to use as an overlay to the character image.
   *
   * @return the icon
   */
  public String getIcon()
  {
    File main = getMainFile();
    if(main == null)
      return "character/person.png";
    else
      return main.getIcon();
  }

  /*
  public Optional<Monster> getMonster()
  {
    if(!m_monster.isPresent())
    {
      if(!m_monsterName.isPresent())
        return Optional.absent();

      m_monster = Optional.of(DMADataFactory.get().<Monster>getEntry
          (new EntryKey(m_monsterName.get(), Monster.TYPE)));
    }

    return m_monster.get();
  }
  */

  @Override
  public List<Item> getPossessions()
  {
    if(m_possessions == null)
      m_possessions = DMADataFactory.get().getEntries(
          Item.TYPE, Optional.of(getCampaign().get().getKey()),
          "index-parent", "character/" + getName().toLowerCase());

    return Collections.unmodifiableList(m_possessions);
  }

  public List<Item> getAllPossessions()
  {
    List<Item> possessions = getPossessions();
    List<Item> items = new ArrayList<>();

    for(Item item : possessions)
    {
      items.add(item);
      items.addAll(item.getAllContents());
    }

    return items;
  }

  /**
   * Add the given entry to the character entry.
   *
   * @ param       inEntry the entry to add
   *
   * @return      true if added, false if not
   *
   */
  /*
  @Override
  public boolean add(CampaignEntry inEntry)
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

    inEntry.setParent(Optional.of(getKey()));

    changed();
    save();
    return true;
  }
  */

  @Override
  public void set(Values inValues)
  {
    super.set(inValues);

    m_state = inValues.use("state", m_state, CharacterState.PARSER);
    m_playerName = inValues.use("player", m_playerName);
    m_xp = inValues.use("xp", m_xp, Value.INTEGER_PARSER);
  }

  @Override
  public Message toProto()
  {
    CharacterProto.Builder builder = CharacterProto.newBuilder();

    builder.setBase((NPCProto)super.toProto());

    if(m_state != CharacterState.UNKNOWN)
      builder.setState(m_state.toProto());

    for(String item : m_items)
        builder.addItem(item);

    if(m_playerName.isPresent())
      builder.setPlayerName(m_playerName.get());

    builder.setXp(m_xp);

    CharacterProto proto = builder.build();
    return proto;
  }

  /**
   * Set the values of the character from the given proto.
   *
   * @param inProto the proto to get values from
   */
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof CharacterProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    CharacterProto proto = (CharacterProto)inProto;

    if(proto.hasState())
      m_state = CharacterState.fromProto(proto.getState());

    for(String item : proto.getItemList())
      m_items.add(item);

    if(proto.hasPlayerName())
      m_playerName = Optional.of(proto.getPlayerName());

    m_xp = proto.getXp();

    super.fromProto(proto.getBase());
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
}
