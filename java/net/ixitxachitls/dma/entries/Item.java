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
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.proto.Entries.CampaignEntryProto;
import net.ixitxachitls.dma.proto.Entries.ItemProto;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.NewMoney;
import net.ixitxachitls.dma.values.NewWeight;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

/**
 * This is a real item.
 *
 * @file          Item.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public class Item extends CampaignEntry
{
  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * This is the internal, default constructor.
   */
  public Item()
  {
    super(TYPE);
  }

  /** The type of this entry. */
  public static final Type<Item> TYPE =
    new Type<Item>(Item.class, BaseItem.TYPE);

  /** The type of the base entry to this entry. */
  public static final BaseType<BaseItem> BASE_TYPE = BaseItem.TYPE;

  /** The actual number of hit points the item currently has. */
  protected Integer m_hp = Integer.MIN_VALUE;

  /** The total value of the item. */
  protected Optional<NewMoney> m_value = Optional.absent();

  /** The appearance text for this entry. */
  protected String m_appearance = null;

  /** The player notes of the item. */
  protected Optional<String> m_playerNotes = Optional.absent();

  /** The name from the player for the item. */
  protected Optional<String> m_playerName = Optional.absent();

  /** The DM notes of the item. */
  protected Optional<String> m_dmNotes = Optional.absent();

  /**
   * Get the hit points of the base item.
   *
   * @return      the hit points
   */
  public int getHP()
  {
    return m_hp;
  }

  /**
   * Get the combined value of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<Integer> getCombinedMaxHP()
  {
    List<Combination<Integer>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedHP());

    return new Combination.Integer(this, combinations);
  }

  /**
   * Get the combined value of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewWeight> getCombinedWeight()
  {
    List<Combination<NewWeight>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedWeight());

    return new Combination.Addable<NewWeight>(this, combinations);
  }

  /**
   * Get the value of the item in gold piece and their fraction (e.g. silver is
   * 0.1).
   *
   * @return      the value
   */
  public double getGoldValue()
  {
    return getCombinedValue().getValue().asGold();
  }

  /**
   * Get the value of the item.
   *
   * @return      the value
   */
  public Optional<NewMoney> getValue()
  {
    return m_value;
  }

  /**
   * Get the combined value of the item, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<NewMoney> getCombinedValue()
  {
    if(m_value.isPresent())
      return new Combination.Addable<NewMoney>(this, m_value.get());

    List<Combination<NewMoney>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedValue());

    return new Combination.Addable<NewMoney>(this, combinations);
  }

  /**
   * Get the name of the entry. This time, also check for special quality
   * modifiers.
   *
   * @return      the requested name
   */
  // public String getName()
  // {
  //   String qualities = null;
  //   if(m_qualities.isDefined())
  //     for(Iterator<EntryValue<Quality>> i = m_qualities.iterator();
  //         i.hasNext(); )
  //     {
  //       Quality quality = i.next().get();
  //       String qualifier = quality.getQualifier();

  //       if(qualifier != null)
  //         if(qualities == null)
  //           qualities = qualifier;
  //         else
  //           qualities += " " + qualifier;
  //     }

  //   if(qualities == null)
  //     return super.getName();
  //   else
  //     return qualities + " " + super.getName();
  // }

  /**
   * Get the size of the item.
   *
   * @return      the size or null if not defined
   */
  // public BaseItem.Size getSize()
  // {
  //   return getBaseValue(new Extractor<BaseItem, BaseItem.Size>()
  //                       {
  //                         public BaseItem.Size get(BaseItem inBase)
  //                         {
  //                           return inBase.getSize();
  //                         }
  //                       }, new Combiner<BaseItem.Size, BaseItem.Size>()
  //                       {
  //                         public BaseItem.Size combine(BaseItem.Size inOld,
  //                                                      BaseItem.Size inNew)
  //                         {
  //                           if(inNew.ordinal() > inOld.ordinal())
  //                             return inNew;

  //                           return inOld;
  //                         }
  //                       });
  // }

  /**
   * Get the break DC of the item.
   *
   * @return      the break dc
   *
   */
  // public long getBreakDC()
  // {
  //   return getBaseValue(new Extractor<BaseItem, Long>()
  //                       {
  //                         public Long get(BaseItem inBase)
  //                         {
  //                           return inBase.m_break.get();
  //                         }
  //                       }, new Combiner<Long, Long>()
  //                       {
  //                         public Long combine(Long inOld, Long inNew)
  //                         {
  //                           if(inNew > inOld)
  //                             return inNew;

  //                           return inOld;
  //                         }
  //                       });
  // }

  /**
   * Get the appearance of the item.
   *
   * @return      the requested appearance
   *
   */
  public String getAppearance()
  {
    return m_appearance;
  }

  /**
   * Get the player notes of the item.
   *
   * @return      the requested notes
   */
  public Optional<String> getPlayerNotes()
  {
    return m_playerNotes;
  }

  /**
   * Get the dm notes of the item.
   *
   * @return      the requested notes
   */
  public Optional<String> getDMNotes()
  {
    return m_dmNotes;
  }

  /**
   * Get the player name defined for this item.
   *
   * @return the name
   */
  public Optional<String> getItemPlayerName()
  {
    return m_playerName;
  }

  /**
   * Get the combined player name for the item.
   *
   * @return the combined name
   */
  public Combination<String> getCombinedPlayerName()
  {
    if(m_playerName.isPresent())
      return new Combination.String(this, m_playerName.get());

    List<Combination<String>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      combinations.add(((BaseItem)entry).getCombinedPlayerName());

    return new Combination.String(this, combinations);
  }

  @Override
  public String getPlayerName()
  {
    return getCombinedPlayerName().getValue();
  }

  @Override
  public String getDMName()
  {
    List<String> parts = new ArrayList<String>();
    for(BaseEntry base : getBaseEntries())
      parts.add(base.getName());

    if(parts.isEmpty())
      parts.add(getName());

    return Strings.SPACE_JOINER.join(parts);
  }

  /**
   * Get all the items contained in this one.
   *
   * @param       inDeep true for returning all item, including nested ones,
   *                     false for only the top level items
   * @return      a list of all contained items
   */
  /*
  public Map<String, Item> containedItems(boolean inDeep)
  {
    Map<String, Item> items = new HashMap<String, Item>();

    Contents contents = (Contents)getExtension("contents");
    if(contents != null)
    {
      Map<String, Item> contained = contents.containedItems(inDeep);
      for(String key : contained.keySet())
        if(items.containsKey(key))
          Log.warning("item loop detected for " + key);

      items.putAll(contents.containedItems(inDeep));
    }

    Composite composite = (Composite)getExtension("composite");
    if(composite != null)
    {
      Map<String, Item> contained = composite.containedItems(inDeep);
      for(String key : contained.keySet())
        if(items.containsKey(key))
          Log.warning("item loop detected for " + key);

      items.putAll(composite.containedItems(inDeep));
    }

    return items;
  }
  */

  /**
   * Get a command to format the name of the item.
   *
   * @return   the command to format the name
   */
  public String fullName()
  {
    String name = getDMName();
    String playerName = getPlayerName();
    if(name.equalsIgnoreCase(playerName))
      return name;

    return playerName + " (" + name +")";
  }

  @Override
  public void set(Values inValues)
  {
    super.set(inValues);

    m_hp = inValues.use("hp", m_hp);
    m_value = inValues.use("value", m_value, NewMoney.PARSER);
    m_appearance = inValues.use("appearance", m_appearance);
    m_playerNotes = inValues.use("player_notes", m_playerNotes);
    m_playerName = inValues.use("player_name", m_playerName);
    m_dmNotes = inValues.use("dm_notes", m_dmNotes);
  }

  @Override
  public void complete()
  {
    if(m_hp == Integer.MIN_VALUE)
    {
      m_hp = getCombinedMaxHP().getValue();
      changed();
    }

    if(m_appearance == null)
    {
      // correct the random value with the computation from the value in
      // relation to the base value
      double itemValue = getGoldValue();
      double baseValue = getCombinedValue().getValue().asGold();

      // We have to try to get the value from our bases.
      List<String> appearances = new ArrayList<String>();
      for(BaseEntry base : getBaseEntries())
      {
        String appearance =
          ((BaseItem)base).getRandomAppearance(itemValue / baseValue);

        if(appearance != null)
          appearances.add(appearance);
      }

      m_appearance = Strings.toString(appearances, " ", "");
      changed();
    }

//     //----- qualities ------------------------------------------------------

//     if(!m_qualities.isDefined())
//       for(BaseEntry base : m_baseEntries)
//         if(base != null)
//           for(SimpleText value : ((BaseItem)base).m_qualities)
//             addQuality(value.get());

//     // finally, complete all the qualities
//     for(EntryValue<Quality> value : m_qualities)
//     {
//       Quality quality = value.get();

//       // complete the skill with this monster
//       quality.complete();
//     }

//     //......................................................................

//   we have to adjust some values that might have been changed by attachments

//     //----- hp -------------------------------------------------------------

//     if(!m_hp.isDefined() || getHP() > getMaxHP())
//       m_hp.setBaseValue(m_maxHP.getBaseValue());

//     //......................................................................

//     // TODO: check if we still need this and how to adapt it
//     // now we might have to replace some value dependent patterns (we do that
//     // after the super.complete() to make sure that all attachment and base
//     // values are completed
//     // String text = m_description.get();

//     // Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
//     // Matcher matcher = pattern.matcher(text);

//     // StringBuffer replaced = new StringBuffer();

//     // while(matcher.find())
//     // {
//     // Pair<ValueGroup, Variable> var = getVariable(matcher.group(1));

//     // if(var == null)
//     // matcher.appendReplacement(replaced, "\\\\color{error}{*no value*}");
//     // else
//     // {
//     // Variable   variable = var.second();
//     // ValueGroup entry    = var.first();

//     // TODO: this has to change
//     // if(variable.hasValue(entry))
//     // matcher.appendReplacement
//     // (replaced,
//     // Matcher.quoteReplacement
//     // (variable.asModifiedCommand
//     // (null, /*m_file.getCampaign(), */this, entry,
//     // Value.Convert.PRINT).statify(null, /*m_storage.getCampaign(), */
//     // this, entry, null,
//     // Value.Convert.PRINT).toString()));
//     // else
//     // matcher.appendReplacement(replaced,
//     // "\\\\color{error}{\\$undefined\\$}");
//     // }
//     // }

//     // matcher.appendTail(replaced);

//     // m_description.set(replaced.toString());

    super.complete();
  }

  /**
   * Identify the item by filling out the player name (and maybe notes?).
   */
  public void identify()
  {
    m_playerName = Optional.of(fullName());
    changed();
    save();
  }

  @Override
  public Message toProto()
  {
    ItemProto.Builder builder = ItemProto.newBuilder();

    builder.setBase((CampaignEntryProto)super.toProto());

    if(m_hp != Integer.MIN_VALUE)
      builder.setHitPoints(m_hp);

    if(m_value.isPresent())
      builder.setValue(m_value.get().toProto());

    if(!m_appearance.isEmpty())
      builder.setAppearance(m_appearance);

    if(m_playerNotes.isPresent())
      builder.setPlayerNotes(m_playerNotes.get());

    if(m_playerName.isPresent())
      builder.setPlayerName(m_playerName.get());

    if(m_dmNotes.isPresent())
      builder.setDmNotes(m_dmNotes.get());

    ItemProto proto = builder.build();
    return proto;
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof ItemProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    ItemProto proto = (ItemProto)inProto;

    if(proto.hasHitPoints())
      m_hp = proto.getHitPoints();

    if(proto.hasValue())
      m_value = Optional.of(NewMoney.fromProto(proto.getValue()));

    if(proto.hasAppearance())
      m_appearance = proto.getAppearance();

    if(proto.hasPlayerNotes())
      m_playerNotes = Optional.of(proto.getPlayerNotes());

    if(proto.hasPlayerName())
      m_playerName = Optional.of(proto.getPlayerName());

    if(proto.hasDmNotes())
      m_dmNotes = Optional.of(proto.getDmNotes());

    super.fromProto(proto.getBase());
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(ItemProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

  //---------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  //ValueGroup.Test
  {
    /** Storage to save the old, real random object. */
    //private java.util.Random m_random;

    /** Called before each test. */
    // public void setUp()
    // {
    //   super.setUp();

    //   m_random = Item.s_random;

    //   s_random = EasyMock.createMock(java.util.Random.class);

    //   BaseCampaign.GLOBAL.m_bases.clear();
    // }

    /** Called after each test.
     *
     * @throws Exception as in base class
     *
     */
    // public void tearDown()
    // {
    //   s_random = m_random;

    //   super.tearDown();

    //   BaseCampaign.GLOBAL.m_bases.clear();
    // }

    /** Give information about what random values are used.
     *
     * @param inValues always pairs of values requested and values to be
     *        returned
     *
     */
    // public void setupRandom(int ... inValues)
    // {
    //   if(inValues.length % 2 != 0)
    //     throw new IllegalArgumentException("must have pairs of values");

    //   for(int i = 0; i < inValues.length; i += 2)
    //     EasyMock.expect(s_random.nextInt(inValues[i]))
    //       .andReturn(inValues[i + 1]);

    //   EasyMock.replay(s_random);
    // }

    //----- createItem() -----------------------------------------------

    /** Create a typical item for testing purposes.
     *
     * @return the newly created item
     *
     */
    // public static AbstractEntry createItem()
    // {
    //   ParseReader reader =
    //     new ParseReader(new java.io.StringReader(s_text), "test");

    //   //return Item.read(reader, new BaseCampaign("Test"));
    //   return null;
    // }

    //......................................................................
    //----- createBasedItem() ------------------------------------------

    /** Create a typical item for testing purposes.
     *
     * @return the newly created item
     *
     */
//     public static AbstractEntry createBasedItem()
//     {
//       // read the base entry
// //       ParseReader reader =
// //         new ParseReader(new java.io.StringReader(s_base), "test");

// //       BaseCampaign campaign = new BaseCampaign("Test");

// //       BaseEntry base = (BaseEntry)BaseItem.read(reader, campaign);

// //       campaign.add(base);

// //       // and empty value
// //       reader =
// //         new ParseReader(new java.io.StringReader("item test 1."),
// //                         "test");

// //       return Item.read(reader, campaign);
//       return null;
//     }

    //......................................................................

    //----- text -----------------------------------------------------------

    /** Test text for item. */
    // private static String s_text =
    //   "#------ Winter Blanket ----------------------------------------\n"
    //   + "\n"
    //   + "item with wearable Winter Blanket = \n"
    //   + "\n"
    //   + "  user size     small;\n"
    //   + "  value      42 cp;\n"
    //   + "  hp         1.\n"
    //   + "\n"
    //   + "#..............................................................";

    /** Test text for first base item. */
    // private static String s_base =
    //   "base item test 1 = \n"
    //   + "\n"
    //   + "  synonyms      \"blanket, winter\";\n"
    //   + "  categories    1, 2, 3;\n"
    //   + "  value         100 gp;\n"
    //   + "  weight        7 lbs;\n"
    //   + "  size          small;\n"
    //   + "  probability   rare;\n"
    //   + "  hardness      0;\n"
    //   + "  hp            10;\n"
    //   + "  world         generic;\n"
    //   + "  appearances   unique    \"first\","
    //   + "                common    \"second\","
    //   + "                common    \"third\","
    //   + "                rare      \"fourth\","
    //   + "                very rare \"fifth\","
    //   + "                unique    \"and sixth, the last one\";"
    //   + "  references    \"TSR 11550\" 107;\n"
    //   + "  description   \n"
    //   + "\n"
    //   + "  \"A thick, quilted, wool blanket.\".\n"
    //   + "\n";

    /** Test text for first base item. */
    // private static String s_baseComplete =
    //   "base item Winter Blanket = \n"
    //   + "\n"
    //   + "  synonyms      \"blanket, winter\";\n"
    //   + "  categories    1, 2, 3;\n"
    //   + "  value         100 gp;\n"
    //   + "  weight        7 lbs;\n"
    //   + "  size          small;\n"
    //   + "  probability   rare;\n"
    //   + "  hardness      0;\n"
    //   + "  hp            10;\n"
    //   + "  world         generic;\n"
    //   + "  appearances   common \"A ${user size} weapon.\";\n"
    //   + "  references    \"TSR 11550\" 107;\n"
    //   + "  description   \n"
    //   + "\n"
    //   + "  \"A thick, quilted, wool blanket.\".\n"
    //   + "\n";

    /** Test text for first second item. */
    // private static String s_base2 =
    //   "base item test 2 = \n"
    //   + "\n"
    //   + "  synonyms      \"blanket, winter\";\n"
    //   + "  categories    2, 3;\n"
    //   + "  value         5 gp;\n"
    //   + "  weight        7 lbs;\n"
    //   + "  size          small;\n"
    //   + "  probability   very rare;\n"
    //   + "  hardness      0;\n"
    //   + "  hp            10;\n"
    //   + "  world         generic;\n"
    //   + "  appearances   unique    \"first\","
    //   + "                common    \"second\","
    //   + "                common    \"third\","
    //   + "                rare      \"fourth\","
    //   + "                very rare \"fifth\","
    //   + "                unique    \"and sixth, the last one\";"
    //   + "  references    \"TSR 11550\" 107;\n"
    //   + "  description   \n"
    //   + "\n"
    //   + "  \"A thick, quilted, wool blanket.\".\n"
    //   + "\n";

    /** Test text for first third item. */
    // private static String s_base3 =
    //   "base item test 3 = \n"
    //   + "\n"
    //   + "  synonyms      \"blanket, winter\";\n"
    //   + "  categories    1, 3;\n"
    //   + "  value         50 gp;\n"
    //   + "  weight        70 lbs;\n"
    //   + "  size          large;\n"
    //   + "  probability   common;\n"
    //   + "  hardness      5;\n"
    //   + "  hp            100;\n"
    //   + "  world         generic;\n"
    //   + "  appearances   unique    \"first\","
    //   + "                common    \"second\","
    //   + "                common    \"third\","
    //   + "                rare      \"fourth\","
    //   + "                very rare \"fifth\","
    //   + "                unique    \"and sixth, the last one\";"
    //   + "  references    \"TSR 11550\" 107;\n"
    //   + "  description   \n"
    //   + "\n"
    //   + "  \"A thick, quilted, wool blanket.\".\n"
    //   + "\n";

    /** Test text for first fourth item. */
    // private static String s_base4 =
    //   "base item test 4 = \n"
    //   + "\n"
    //   + "  synonyms      \"blanket, winter\";\n"
    //   + "  categories    2;\n"
    //   + "  value         50 gp;\n"
    //   + "  weight        7 lbs;\n"
    //   + "  size          small;\n"
    //   + "  probability   uncommon;\n"
    //   + "  hardness      6;\n"
    //   + "  hp            1;\n"
    //   + "  world         generic;\n"
    //   + "  appearances   unique    \"first\","
    //   + "                common    \"second\","
    //   + "                common    \"third\","
    //   + "                rare      \"fourth\","
    //   + "                very rare \"fifth\","
    //   + "                unique    \"and sixth, the last one\";"
    //   + "  references    \"TSR 11550\" 107;\n"
    //   + "  description   \n"
    //   + "\n"
    //   + "  \"A thick, quilted, wool blanket.\".\n"
    //   + "\n";

    /** Test text for first fifth item. */
    // private static String s_base5 =
    //   "base item test 5 = \n"
    //   + "\n"
    //   + "  synonyms      \"blanket, winter\";\n"
    //   + "  categories    1;\n"
    //   + "  value         50 gp;\n"
    //   + "  weight        7 lbs;\n"
    //   + "  size          small;\n"
    //   + "  probability   unique;\n"
    //   + "  hardness      6;\n"
    //   + "  hp            1;\n"
    //   + "  world         generic;\n"
    //   + "  appearances   unique    \"first\","
    //   + "                common    \"second\","
    //   + "                common    \"third\","
    //   + "                rare      \"fourth\","
    //   + "                very rare \"fifth\","
    //   + "                unique    \"and sixth, the last one\";"
    //   + "  references    \"TSR 11550\" 107;\n"
    //   + "  description   \n"
    //   + "\n"
    //   + "  \"A thick, quilted, wool blanket.\".\n"
    //   + "\n";

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    // public void testRead()
    // {
    //   ParseReader reader =
    //     new ParseReader(new java.io.StringReader(s_baseComplete), "test");

    //   BaseEntry base = (BaseEntry)BaseItem.read(reader);

    //   BaseCampaign.GLOBAL.add(base);

    //   reader = new ParseReader(new java.io.StringReader(s_text), "test");

    //   String result =
    //     "#----- Winter Blanket\n"
    //     + "\n"
    //     + "item with wearable Winter Blanket [Winter Blanket] =\n"
    //     + "\n"
    //     + "  user size         Small;\n"
    //     + "  hp                1;\n"
    //     + "  max hp            ;\n"
    //     + "  value             42 cp;\n"
    //     + "  hardness          ;\n"
    //     + "  appearance        \n"
    //     + "  \"A ${user size} weapon.\".\n"
    //     + "\n"
    //     + "#.....\n";

    //   Entry entry = (Entry)Item.read(reader);

    //   entry.complete();

    //   assertNotNull("item should have been read", entry);
    //   assertEquals("item name does not match", "Winter Blanket",
    //                 entry.getName());
    //   assertEquals("item does not match", result, entry.toString());
    // }

    //......................................................................
    //----- get/set --------------------------------------------------------

    /** Test setting and getting of values. */
    // public void testGetSet()
    // {
    //   // easy mock the random object
    //   setupRandom(0, 0,
    //               100 + BaseItem.Size.values().length, 50,
    //               100, 50);

    //   BaseItem base = new BaseItem("some item");

    //   assertTrue("base hp", base.setHP(42));
    //   assertTrue("value", base.setValue(0, 12, 3, 0));
    //   assertTrue("weight", base.setWeight(new Rational(2, 1, 2)));

    //   BaseItem base2 = new BaseItem("some other item");

    //   Item item = new Item(base, base2);

    //   item.complete();

    //   assertEquals("value", "12 gp 3 sp [+12 gp 3 sp some item]",
    //                item.m_value.toString());
    //   assertTrue("value", item.setValue(0, 0, 7, 1));

    //   // set back to medium to see that the values are base again
    //   assertEquals("max hp", 42, item.getMaxHP());
    //   assertEquals("hp", 42, item.getHP());
    //   assertEquals("value", "7 sp 1 cp", item.m_value.toString());
    //   assertEquals("weight", "2 1/2 lbs [+2 1/2 lbs some item]",
    //                item.m_weight.toString());

    //   assertTrue("hp", item.setHP(23));
    //   assertEquals("hp", 23, item.getHP());
    //   assertFalse("hp", item.setHP(-1));
    //   assertFalse("hp", item.setHP(50));
    //   assertTrue("hp", item.setHP(0));

    //   assertTrue("description", item.setDescription("a test"));
    //   assertEquals("description", "a test", item.getDescription());
    //   assertTrue("player notes", item.setPlayerNotes("some notes"));
    //   assertEquals("player notes", "some notes", item.getPlayerNotes());
    //   assertTrue("dm notes", item.setDMNotes("secret notes"));
    //   assertEquals("dm notes", "secret notes", item.getDMNotes());
    //   assertTrue("player name", item.setPlayerName("my name"));
    //   assertEquals("player name", "my name", item.getPlayerName());

    //   /*
    //   assertTrue("qualities");
    //   */
    // }

    //......................................................................
    //----- random ---------------------------------------------------------

    /** Test an item with random value. */
    // public void testRandom()
    // {
    //   setupRandom(625 + 625 + 25, 1251);

    //   BaseItem base = new BaseItem("some item for random");

    //   assertTrue("base hp", base.setHP(42));
    //   assertTrue("value", base.setValue(0, 12, 3, 0));
    // assertTrue("appearances", base.addAppearance(BaseItem.Probability.COMMON,
    //                                                "common appearance"));
    // assertTrue("appearances", base.addAppearance(BaseItem.Probability.COMMON,
    //                                                "common appearance 2"));
    //   assertTrue("appearances", base.addAppearance(BaseItem.Probability.RARE,
    //                                                "rare appearance"));

    //   Item item = new Item(base);

    //   item.complete();

    //   assertEquals("value", "12 gp 3 sp", item.getValue().toString());
    //   assertEquals("value", "12 gp 3 sp [+12 gp 3 sp some item for random]",
    //                item.m_value.toString());
    //   assertEquals("appearance", "rare appearance", item.getAppearance());

    //   EasyMock.verify(s_random);

    //   //s_random = old;
    // }

    //......................................................................
    //----- print ----------------------------------------------------------

    /** Testing printing. */
    // public void testPrint()
    // {
    //   setupRandom(0, 0,
    //               100 + BaseItem.Size.values().length, 50,
    //               100, 50);

    //   BaseItem base = new BaseItem("some item");

    //   assertTrue("weight", base.setWeight(new Rational(2)));

    //   Item item = new Item(base);

    //   item.complete();

    //   assertTrue("player name", item.setPlayerName("player name"));
    //   assertTrue("player notes", item.setPlayerNotes("player notes"));
    //   item.m_description.set("desc");
    //   item.m_short.set("short");

    //   PrintCommand commands = item.printCommand(false, false);

    //   assertNotNull("command", commands);

    //   // title with player name
    //   Command title =
    //   item.getCommand(commands, new Command("${title, player title}"), true);

    //   assertEquals("title", "title", extract(title, 1, 0));
    //   assertEquals("title", "some item", extract(title, 1, 1));

    //   title =
    //  item.getCommand(commands, new Command("${title, player title}"), false);

    //   assertEquals("title", "title", extract(title, 2, 0));
    //   assertEquals("title", "player name", extract(title, 2, 1));

    //   // values
    //   Command values =
    //     item.getCommand(commands,
    //                     new Command("%{base, player notes, weight}"), true);

    //   assertEquals("base", "Base:", extract(values, 1, 1, 1));
    //   assertEquals("base", "some item", extract(values, 2, 1, 1, 1));
    //   assertEquals("notes", "Player Notes:", extract(values, 3, 1, 1));
    //   assertEquals("notes", "player notes", extract(values, 4, 1, 2));
    //   assertEquals("weight", "Weight:", extract(values, 5, 1, 1));
    //   assertEquals("weight", "2 lbs", extract(values, 6, 1, 2, 1, 1, 2, 3));
    // }

    //......................................................................
    //----- print DM -------------------------------------------------------

    /** Test printing an item for the dm. */
    // public void testPrintDM()
    // {
    //   setupRandom(0, 0,
    //               100 + BaseItem.Size.values().length, 50,
    //               100, 50);

    //   BaseItem base = new BaseItem("some item");

    //   assertTrue("weight", base.setWeight(new Rational(2)));
    //   assertTrue("description", base.setDescription("some base desc"));
    //   assertTrue("player name", base.setPlayerName("base player name"));
    //   assertTrue("value", base.setValue(0, 3, 2, 0));
    //   assertTrue("hp", base.setHP(42));

    //   Item item = new Item(base);

    //   item.complete();

    //   assertTrue("player name", item.setPlayerName("player name"));
    //   assertTrue("player notes", item.setPlayerNotes("player notes"));
    //   assertTrue("player notes", item.setDMNotes("dm notes"));
    //   assertTrue("description", item.setDescription("desc"));
    //   assertTrue("hp", item.setHP(23));

    //   item.m_short.set("short");

    //   PrintCommand commands = item.printCommand(true, false);

    //   assertNotNull("command", commands);

    //   Command intro =
    //   item.getCommand(commands, new Command("${title, description}"), true);

    //   // title with player name
    //   assertEquals("title", "title", extract(intro, 1, 0));
    //   assertEquals("title", "link", extract(intro, 1, 1, 0));
    //   assertEquals("title", "/entry/item/some item",
    //                extract(intro, 1, 1, -1));
    //   assertEquals("title", "some item", extract(intro, 1, 1, 1));

    //   // description
    //   assertEquals("description", "description", extract(intro, 2, 3));

    //   Command values =
    //     item.getCommand(commands,
    //                     new Command("%{base, value, player notes, dm notes, "
    //                                 + "weight}"), true);

    //   // values
    //   assertEquals("base", "Base:", extract(values, 1, 1, 1));
    //   assertEquals("base", "some item", extract(values, 2, 1, 1, 1));
    //   assertEquals("value", "Value:", extract(values, 3, 1, 1));
    //   assertEquals("value", "3 gp", extract(values, 4, 1, 2, 1, 1, 1, 1, 2));
    //   assertEquals("value", "2 sp", extract(values, 4, 1, 2, 1, 1, 1, 3, 2));
    //   assertEquals("notes", "Player Notes:", extract(values, 5, 1, 1));
    //   assertEquals("notes", "player notes", extract(values, 6, 1, 3));
    //   assertEquals("notes", "Dm Notes:", extract(values, 7, 1, 1));
    //   assertEquals("notes", "dm notes", extract(values, 8, 1, 3));
    //   assertEquals("weight", "Weight:", extract(values, 9, 1, 1));
    // ssertEquals("weight", "2 lbs", extract(values, 10, 1, 2, 1, 1, 1, 1, 2));
    // }

    //......................................................................
    //----- lookup ---------------------------------------------------------

    /** Testing lookup. */
    // public void testLookup()
    // {
    //   setupRandom(0, 0,
    //               0, 0,
    //               0, 0,
    //               0, 0,
    //               100 + BaseItem.Size.values().length, 50, 100, 50,
    //               3125, 1200, // name lookup
    //               100 + BaseItem.Size.values().length, 50, 100, 50,
    //               625, 50, // lookup 2
    //               625, 50, // lookup 3
    //               4 * 625, 2 * 625 + 10, // lookup 4
    //               2 * 625, 626, // lookup 5
    //               2 * 625, 624, // lookup 6
    //               3 * 625, 2 * 625, // lookup 7
    //               2 * 625, 60, // lookup 8
    //               2 * 625, 627, // lookup 9
    //               625, 62, // lookup 10
    //               2 * 625, 50, // lookup 11
    //               2 * 625, 50, // lookup 12
    //               2 * 625, 680, // lookup 13
    //               625, 600 // lookup 14
    //               );

    //   BaseItem base1 = new BaseItem("base item 1");
    //   BaseItem base2 = new BaseItem("base item 2");
    //   BaseItem base3 = new BaseItem("base item 3");
    //   BaseItem base4 = new BaseItem("base item 4");
    //   BaseItem base5 = new BaseItem("base item 5");

    //   base1.setHP(42);
    //   base2.setHP(23);
    //   base3.setHP(1);
    //   base4.setHP(2);
    //   base5.setHP(15);

    //   base1.addCategory("1");
    //   base1.addCategory("2");
    //   base1.addCategory("3");
    //   base2.addCategory("2");
    //   base3.addCategory("3");
    //   base3.addCategory("4");
    //   base4.addCategory("1");
    //   base4.addCategory("3");
    //   base4.addCategory("2");
    //   base5.addCategory("1");
    //   base5.addCategory("5");

    //   BaseCampaign.GLOBAL.add(base1);
    //   BaseCampaign.GLOBAL.add(base2);
    //   BaseCampaign.GLOBAL.add(base3);
    //   BaseCampaign.GLOBAL.add(base4);
    //   BaseCampaign.GLOBAL.add(base5);

    //   Item item = new Item("base item 1");

    //   Campaign campaign = new Campaign("test", "tst", 0);

    //   campaign.add(item);

    //   assertEquals("simple lookup", base1, item.m_baseEntries.get(0));

    //   // how about an unnamed item
    //   item = new Item();

    //   campaign.add(item);

    //   assertEquals("empty lookup", base2, item.m_baseEntries.get(0));

    //   // lookup with predefines
    //   ParseReader reader =
    //     new ParseReader(new java.io.StringReader("item [[ = hp == 1. ]]."),
    //                     "test");

    //   BaseItem base =
    //     (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("hp", base3, base);

    //   reader =
    //     new ParseReader(new java.io.StringReader("item [[ = hp == 15. ]] "
    //                                              + "= hp 5."), "test");

    //   item = (Item)Item.read(reader);
    // base = (BaseItem)BaseCampaign.GLOBAL.lookup(item.getLookup(), BASE_TYPE);

    //   assertEquals("hp ==", base5, base);
    //   assertEquals("hp ==", 5,     item.getHP());

    //   // not equal
    //   reader =
    //     new ParseReader(new java.io.StringReader("item [[ = hp != 1. ]]."),
    //                     "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("hp !=", base4, base);
    //   assertTrue("hp !=", 1 != base.getHP());

    //   // smaller
    //   reader =
    //     new ParseReader(new java.io.StringReader("item [[ = hp < 10. ]]."),
    //                     "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("hp <", base4, base);
    //   assertTrue("hp <", base.getHP() < 10);

    //   // more or equal
    //   reader =
    //     new ParseReader(new java.io.StringReader("item [[ = hp >= 20. ]]."),
    //                     "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("hp >=", base1, base);
    //   assertTrue("hp >=", base.getHP() >= 20);

    //   // one of
    //   reader =
    //  new ParseReader(new java.io.StringReader("item [[ = categories ~= 2. ]]"
    //                                              + "."),
    //                     "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("categories ~=", base2, base);

    //   // two of
    //   reader =
    //     new ParseReader(new java.io.StringReader("item "
    //                                           + "[[ = categories ~= 1,2. ]]"
    //                                              + "."), "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("categories ~=", base1, base);

    //   // not one of
    //   reader =
    //  new ParseReader(new java.io.StringReader("item [[ = categories ~! 2. ]]"
    //                                              + "."), "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("categories ~!", base5, base);

    //   // not two of
    //   reader =
    //     new ParseReader(new java.io.StringReader("item "
    //                                           + "[[ = categories ~! 3, 2. ]]"
    //                                              + "."), "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("categories ~!", base5, base);

    //   // now with multiple matchers!
    //   reader =
    //     new ParseReader(new java.io.StringReader("item "
    //                                           + "[[ = hp > 10 && < 30. ]]."),
    //                     "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("hp < & >", base2, base);

    //   // now with multiple matchers!
    //   reader =
    //     new ParseReader(new java.io.StringReader("item "
    //                                            + "[[ = hp == 1 || == 23. ]]"
    //                                              + "."), "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("hp == | ==", base2, base);

    //   // now with multiple matchers!
    //   reader =
    //     new ParseReader(new java.io.StringReader("item "
    //                                             + "[[ = hp <= 1 || >= 40. ]]"
    //                                              + "."), "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("hp < | >", base3, base);

    //   // lookup with name
    //   reader =
    //     new ParseReader(new java.io.StringReader("item [[ **\\s+3. ]]."),
    //                     "test");

    //   base = (BaseItem)BaseCampaign.GLOBAL.lookup
    //     (((Item)Item.read(reader)).getLookup(), BASE_TYPE);

    //   assertEquals("name", base3, base);
    // }

    //......................................................................
    //----- auto attachment ------------------------------------------------

    /** Testing auto attachments. */
    // public void testAutoAttachment()
    // {
    //   String text =
    //     "base item with container Container = \n"
    //     + "  hp 3.\n"
    //     + "item Container = \n"
    //     + "   hp 2;\n"
    //     + "   contents item guru. item guru2 = hp 3..\n";

    //   ParseReader reader =
    //     new ParseReader(new java.io.StringReader(text), "container test");

    //   BaseItem base = (BaseItem)BaseItem.read(reader);

    //   BaseCampaign.GLOBAL.add(base);

    //   Item item = (Item)Item.read(reader);

    //   assertTrue("attachment",
    //              item.hasAttachment(net.ixitxachitls.dma.entries.attachments
    //                                 .Contents.class));

    //   m_logger.addExpectedPattern("WARNING: base.not-found:.*"
    //                               + "(base name 'guru').*");
    //   m_logger.addExpectedPattern("WARNING: base.not-found:.*"
    //                               + "(base name 'guru2').*");
    // }

    //......................................................................
    //----- qualities ------------------------------------------------------

    /** Testing qualities. */
    // public void testQualities()
    // {
    //   BaseQuality q1 = new BaseQuality("Q1");
    //   BaseQuality q2 = new BaseQuality("Q2");

    //   q1.m_qualifier.set("q1");
    //   q2.m_qualifier.set("q2");

    //   BaseCampaign.GLOBAL.add(q1);
    //   BaseCampaign.GLOBAL.add(q2);

    //   Item item = new Item("item");

    //   assertTrue("add", item.addQuality("Q1"));
    //   assertTrue("add", item.addQuality("Q2"));
    //   assertTrue("add", item.addQuality("Q3"));

    //   // check the name of the item
    //   // TODO: fix this test
    //   //assertEquals("name", "q1 q2 item", item.getName());

    //   m_logger.addExpected("WARNING: could not find base(s) for 'Q3'");
    // }

    //......................................................................
    //----- list commands --------------------------------------------------

    /** Test printing list commands. */
    // public void testListCommands()
    // {
    //   setupRandom();

    //   Item item = new Item("test");

    //   item.setWeight(new Rational(2));
    //   item.setPlayerNotes("player notes");
    //   item.setDescription("description");
    //   item.setPlayerName("name");
    //   item.setAppearance("shiny");

    //   item.complete();

    //   Command values =
    //   item.getCommand(item.printCommand(false, false), LIST_COMMAND, false);

    //   assertEquals("list", "table", extract(values, 1, 0));
    //   assertEquals("list", "name", extract(values, 1, 2, 1, 3, 1, 2));
    //   assertEquals("list", "2 lbs", extract(values, 1, 3, 1, 1, 1, 2));
    //   assertEquals("list", "shiny", extract(values, 1, 4, 1, 1, 1, 2));
    //   assertEquals("list", "player notes",
    //                extract(values, 1, 4, 1, 1, 2, 2));

    //   m_logger.addExpected("WARNING: could not find base(s) for 'test'");
    // }

    //......................................................................
    //----- based ----------------------------------------------------------

    /** Test basing items on multiple base items. */
    // public void testBased()
    // {
    //   BaseItem base1 = new BaseItem("base1");
    //   base1.addAttachment("armor");
    //   base1.setValue(0, 10, 0, 0);
    //   base1.setDescription("desc1");
    //   base1.set("max dexterity", "+4");
    //   base1.set("synonyms", "\"a\", \"b\"");

    //   BaseItem base2 = new BaseItem("base2");
    //   base2.addAttachment("armor");
    //   base2.setDescription("desc2");
    //   assertNull(base2.set("AC bonus", "+5 armor"));
    //   assertNull(base2.set("weight", "[/2 \"test\"]"));
    //   base2.set("synonyms", "\"c\", \"d\"");

    //   BaseItem base3 = new BaseItem("base3");
    //   base3.addAttachment("armor");
    //   base3.setValue(0, 10, 0, 0);
    //   assertNull(base3.set("AC bonus", "+2 shield"));
    //   assertNull(base3.set("max dexterity", "+2"));
    //   assertNull(base3.set("weight", "20 lbs"));

    //   Item item = new Item("test", base1, base2, base3);
    //   item.complete();

    // assertEquals("synonyms", "a,\nb,\nc,\nd [+\"c\",\n\"d\" base2]",
    //             item.getBaseValue("synonyms", Combine.ADD, true).toString());
    //   assertEquals("value", "20 gp [+10 gp base1, +10 gp base3]",
    //                item.m_value.toString());
    //assertEquals("description", "desc1 desc2", item.m_description.toString());
    //   assertEquals("ac bonus", "+7 [+5 armor, +2 shield]",
    //                item.getBaseValue("AC bonus", Combine.ADD, true,
    //                                  new Modifiable<Number>
    //                                  (new Number(0, 10, true))).toString());
    //   assertEquals("max dexterity", "+2",
    //                item.getBaseValue("max dexterity", Combine.MINIMUM, true)
    //                .toString());
    //   assertEquals("weight", "10 lbs [+20 lbs base3, /2 \"test\"]",
    //                item.m_weight.toString());
    // }

    //......................................................................
  }

  //........................................................................
}
