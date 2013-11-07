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

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.AbstractEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Values.RangeProto;
import net.ixitxachitls.dma.values.LongFormattedText;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Range;
import net.ixitxachitls.dma.values.Reference;
import net.ixitxachitls.dma.values.Selection;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base class for all base entries of DMA.
 *
 * All the values stored in this entry (and in all derivations) need to
 * be non-null!
 *
 * @file          BaseEntry.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BaseEntry extends AbstractEntry
{
  //----------------------------------------------------------------- nested

  //----- worlds -----------------------------------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The game worlds (this is configurable and thus not an enum). */
  private static final String []WORLDS =
    Config.get("/game/worlds", new String []
    {
      // these are unsorted because they are the most important
      "Generic",
      "Eberron",
      "Forgotten Realms",

      // the rest is sorted
      "Al-Qadim",
      "Birthright",
      "Buck Rogers",
      "Call of Cthulhu",
      "Confrontation",
      "Dark Sun",
      "Dark.Matter",
      "Dawn of the Emperors",
      "Diablo II",
      "Dominaria",
      "Dragonlance",
      "Episode I",
      "Gamma World",
      "Greyhawk",
      "Hollow World",
      "Lankhmar",
      "Marvel Super Heroes",
      "Middle Earth",
      "Mystara",
      "None",
      "Odyssey",
      "Planescape",
      "Ptolus",
      "Ravenloft",
      "Real",
      "Red Steel",
      "Rokugan",
      "S.P.I.",
      "Savage Coast",
      "Spelljammer",
      "Star Wars",
      "Star*Drive",
      "Urza's Destiny",
      "Wheel of Time",
    });

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------ BaseEntry -------------------------------

  /**
   * The default constructor, with undefined values.
   *
   */
  protected BaseEntry()
  {
    super(TYPE);
  }

  //........................................................................
  //------------------------------ BaseEntry -------------------------------

  /**
   * The default constructor, with undefined values.
   *
   * @param   inType the type of the entry
   *
   */
  protected BaseEntry(AbstractType<? extends BaseEntry> inType)
  {
    super(inType);
  }

  //........................................................................
  //------------------------------ BaseEntry -------------------------------

  /**
   * The complete constructor.
   *
   * @param       inName     the name of the entry
   *
   */
  public BaseEntry(String inName)
  {
    this(inName, TYPE);
  }

  //........................................................................
  //------------------------------ BaseEntry -------------------------------

  /**
   * The constructor for derivations, with a type.
   *
   * @param       inName     the name of the entry
   * @param       inType     the type of the entry
   *
   */
  protected BaseEntry(String inName, AbstractType<? extends BaseEntry> inType)
  {
    super(inName, inType);
  }

  //........................................................................
  //------------------------------ BaseEntry -------------------------------

  /**
   * The constructor for derivations, with a type.
   *
   * @param       inName     the name of the entry
   * @param       inType     the type of the entry
   *
   * @undefined   never
   *
   */
//   protected BaseEntry(String inName, Type inType,
//                       BaseEntry ... inBases)
//   {
//     super(inName, inType, inBases);
//   }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final BaseType<BaseEntry> TYPE =
    new BaseType<BaseEntry>(BaseEntry.class, "Base Entries");

  //----- world ------------------------------------------------------------

  /** The world. */
  @Key("worlds")
  protected ValueList<Selection> m_worlds = new ValueList<Selection>
    (new Selection(WORLDS).withTemplate("link", "worlds"));

  //........................................................................
  //----- references -------------------------------------------------------

  // TODO: was modifiable; check if it needs to be
  /** The references for this entry. */
  @Key("references")
  @DM
  protected ValueList<Multiple> m_references =
    new ValueList<Multiple>
    (new Multiple
     (new Multiple.Element
      (new Reference<BaseProduct>(BaseProduct.TYPE).withEditType
       ("autokey(base product/titles)[product]"), false),
      new Multiple.Element
      (new ValueList<Range>(new Range(0, Integer.MAX_VALUE), "/")
       /*.withEditType("pages[pages]")*/, true, ": ", null)));

  //........................................................................
  //----- description ------------------------------------------------------

  /** The descriptive text for this entry. */
  @Key("description")
  @WithBases
  protected LongFormattedText m_description = new LongFormattedText();

  //........................................................................
  //----- short description ------------------------------------------------

  /** The short description text for this entry. */
  @Key("short description")
  @WithBases
  @PrintUndefined
  protected Text m_short =
    new Text().withEditType("string[short description]");

  //........................................................................
  //----- synonyms ---------------------------------------------------------

  /** The synonyms for this entry. */
  @Key("synonyms")
  @DM
  protected ValueList<Text> m_synonyms =
    new ValueList<Text>(new Text());

  //........................................................................
  //----- categories -------------------------------------------------------

  /** The categories. */
  @Key("categories")
  @DM
  protected ValueList<Name> m_categories =
    new ValueList<Name>(new Name());

  //........................................................................

  static
  {
    extractVariables(BaseEntry.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- isBase --------------------------------

  /**
   * Check if the current entry represents a base entry or not.
   *
   * @return      true if this is a base entry, false else
   *
   */
  @Override
  public boolean isBase()
  {
    return true;
  }

  //........................................................................
  //------------------------------- matches --------------------------------

  /**
   * Determine if the current entry matches to the given base entry.
   *
   * @param       inBase the base entry to match to
   *
   * @return      true if they match, i.e. if the given base entry could
   *              be the base to the current object
   *
   */
//   public boolean matches(BaseEntry inBase)
//   {
//     // only matches if the types match
//     if(getType() != inBase.getType())
//       return false;

//     // check the name
//     String name = getName();

//     if(name != null)
//     {
//       // match the name
//       name = name.replaceAll("\\*\\*", ".*");

//       if(!inBase.getName().matches(name))
//         return false;
//     }

//     // now we do the real check, i.e. really compare values
//     Values values = getValues();
//     if(values != null)
//       for(Variable variable : values)
//       {
//         // if no matcher is present, we ignore this value
//         if(!variable.hasMatcher(this))
//           continue;

//         // we don't have a value, see if the base entry has one
//         Variable base = inBase.getValues().getValue(variable.getKey());

//         // no base, we ignore the value
//         if(base == null)
//         {
//           Log.warning("Cannot find matcher value for '"
//                       + variable.getKey() + "' in '" + inBase.getType()
//                       + "'");

//           continue;
//         }

//         if(!variable.check(this, base.get(inBase)))
//           return false;
//       }

//     return true;
//   }

  //........................................................................
  //----------------------------- hasCategory ------------------------------

  /**
   * Check if the base entry has a named category.
   *
   * @param       inCategory the category to look for
   *
   * @return      true if the category is present, false if not
   *
   */
  public boolean hasCategory(String inCategory)
  {
    for(Name text : m_categories)
      if(text.get().equalsIgnoreCase(inCategory))
        return true;

    return false;
  }

  //........................................................................
  //------------------------------ hasSynonym ------------------------------

  /**
   * Check if the given base entry has a synonym with the given name.
   *
   * @param       inName the name to look for
   *
   * @return      true if the entry has a synonym, false if not
   *
   */
  public boolean hasSynonym(String inName)
  {
    for(Text text : m_synonyms)
      if(text.get().equalsIgnoreCase(inName))
        return true;

    return false;
  }

  //........................................................................

  //------------------------------- getWorlds ------------------------------

  /**
   * Get the worlds the product is for.
   *
   * @return      the selection containing the selected world
   *
   */
  public String getWorlds()
  {
    return m_worlds.toString();
  }

  //........................................................................
  //------------------------- getShortDescription --------------------------

  /**
   * Get the short description of the base entry.
   *
   * @return      the selection containing the selected world
   *
   */
  public String getShortDescription()
  {
    String desc = m_short.get();

    for(BaseEntry base : getBaseEntries())
      if(base != null)
        if (desc == null || desc.isEmpty())
          desc = base.getShortDescription();
        else
          desc += " " + base.getShortDescription();


    return desc;
  }

  //........................................................................

  //--------------------------- getReferenceIDs ----------------------------

  /**
   * Get the ids of the reference for this entry.
   *
   * @return      a list of the reference ids
   *
   */
  public List<String> getReferenceIDs()
  {
    List<String> ids = new ArrayList<String>();
    for(Multiple reference : m_references)
      if(reference.isDefined())
        ids.add(reference.get(0).toString());

    return ids;
  }

  //........................................................................

  //--------------------------- resolveReference ---------------------------

  /**
   * Resolve the given reference into something readable.
   *
   * @param       inName the name of the base product reference
   *
   * @return      the base product referenced, if found
   *
   */
  protected @Nullable BaseProduct resolveReference(String inName)
  {
    return DMADataFactory.get().getEntry(createKey(inName, BaseProduct.TYPE));
  }

  //........................................................................
  //------------------------------ getSynonyms -----------------------------

  /**
   * Get the synonyms of the entry.
   *
   * @return      the synonyms
   *
   */
  public List<String> getSynonyms()
  {
    List<String> result = new ArrayList<String>();

    // TODO: add some handling for base class synonyms here
    for(Text synonym : m_synonyms)
      result.add(synonym.get());

    return result;
  }

  //........................................................................
  //----------------------------- getReferences ----------------------------

  /**
   * Get the references of the entry.
   *
   * @return      the references
   *
   */
  public List<String> getReferences()
  {
    List<String> result = new ArrayList<String>();

    // TODO: must include base entries here!
    for(Multiple reference : m_references)
      if(reference.isDefined() && reference.get(0).isDefined())
      {
        String name = reference.get(0).toString();
        BaseProduct product = resolveReference(name);
        if(product == null)
          result.add(name);
        else
        {
          String title = product.getTitle();
          if(name.equals(title))
            result.add(title);
          else
            result.add(product.getTitle() + " (" + name + ")");
        }
      }

    return result;
  }

  //........................................................................
  //----------------------------- getCategories ----------------------------

  /**
   * Get the categories of the entry.
   *
   * @return      the categories
   *
   */
  public List<String> getCategories()
  {
    List<String> result = new ArrayList<String>();

    for(Name text : m_categories)
      result.add(Encodings.toWordUpperCase(text.get()));

    // add the extensions, if any
    for(String extension : m_extensions.keySet())
      result.add(Encodings.toWordUpperCase(extension));

    return result;
  }

  //........................................................................
  //-------------------------- computeIndexValues --------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @return      a multi map of values per index name
   *
   */
  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    for(Selection world : m_worlds)
      values.put(Index.Path.WORLDS, world.toString());

    for(String category : getCategories())
      values.put(Index.Path.CATEGORIES, category);

    for(String reference : getReferences())
      values.put(Index.Path.REFERENCES, reference);

    return values;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------ setDescription --------------------------

  /**
   * Set the description of the base entry.
   *
   * @param       inDescription the description
   *
   */
  public void setDescription(@Nullable String inDescription)
  {
    if(inDescription == null)
      m_description = new LongFormattedText();
    else
      m_description = new LongFormattedText(inDescription);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  @SuppressWarnings("unchecked")
  @Override
  public Message toProto()
  {
    BaseEntryProto.Builder builder = BaseEntryProto.newBuilder();

    builder.setAbstract((AbstractEntryProto)super.toProto());

    for(Name category : m_categories)
      builder.addCategory(category.get());

    if(m_description.isDefined())
      builder.setDescription(m_description.get());
    if(m_short.isDefined())
      builder.setShortDescription(m_short.get());
    for(Multiple reference : m_references)
    {
      BaseEntryProto.Reference.Builder ref =
        BaseEntryProto.Reference.newBuilder();
        ref.setName(((Reference<BaseProduct>)reference.get(0)).getName());

      for(Range pages : ((ValueList<Range>)reference.get(1)))
        ref.addPages(pages.toProto());

      builder.addReference(ref);
    }
    for(Text synonym : m_synonyms)
      builder.addSynonym(synonym.get());
    for(Selection world : m_worlds)
      builder.addWorld(world.toString());

    BaseEntryProto proto = builder.build();
    return proto;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseEntryProto))
    {
      Log.warning("cannot parse base entry proto " + inProto.getClass());
      return;
    }

    BaseEntryProto proto = (BaseEntryProto)inProto;

    super.fromProto(proto.getAbstract());

    if(proto.getCategoryCount() > 0)
    {
      List<Name> categories = new ArrayList<>();
      for(String category : proto.getCategoryList())
        categories.add(m_categories.createElement().as(category));

      m_categories = m_categories.as(categories);
    }

    if(proto.hasDescription())
      m_description = m_description.as(proto.getDescription());
    if(proto.hasShortDescription())
      m_short = m_short.as(proto.getShortDescription());

    List<Multiple> references = new ArrayList<>();
    for(BaseEntryProto.Reference reference : proto.getReferenceList())
    {
      Multiple ref = m_references.newElement();

      List<Range> pages = new ArrayList<>();
      for (RangeProto page : reference.getPagesList())
        pages.add(((ValueList<Range>)ref.get(1)).createElement()
                  .as(page.getLow(), page.getHigh()));

      ref = ref.as(((Reference<BaseProduct>)ref.get(0)).as(reference.getName()),
                   ((ValueList<Range>)ref.get(1)).as(pages));
      references.add(ref);
    }
    m_references = m_references.as(references);

    List<Text> synonyms = new ArrayList<>();
    for(String synonym : proto.getSynonymList())
      synonyms.add(m_synonyms.createElement().as(synonym));
    m_synonyms = m_synonyms.as(synonyms);

    List<Selection> worlds = new ArrayList<>();
    for(String world : proto.getWorldList())
      worlds.add(m_worlds.createElement().as(world));
    m_worlds = m_worlds.as(worlds);
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(BaseEntryProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends ValueGroup.Test
  {
    //----- text -----------------------------------------------------------

    /** Test text. */
    private static final String TEXT =
      "base entry test = \n"
      + "\n"
      + "  synonyms      \"blanket, winter\", \"guru\";\n"
      + "  categories    dagger, weapon, guru, test;\n"
      + "  references    a: 10, b: 5-10/20;\n"
      + "  short description \"A cozy, warm blanket.\";\n"
      + "  description   \n"
      + "\n"
      + "  \"A thick, quilted, wool blanket.\".\n"
      + "\n";

//     /** Test text with remarks. */
//     private static String s_remarks =
//       "base entry test remarks = \n"
//       + "\n"
//       + "  synonyms      {*}\"blanket, winter\", \"guru\";\n"
//       + "  categories    {~, some estimation}dagger, weapon, guru, test;\n"
//       + "  references    {p, a player remark} \"a\" 10, \"b\" 5-10/20;\n"
//       + "  description   \n"
//       + "\n"
//       + "  \"A thick, quilted, wool blanket.\".\n"
//       + "\n";

    //......................................................................
    //----- read -----------------------------------------------------------

     /** Testing reading. */
    @Override
    @org.junit.Test
    public void read()
    {
      try (java.io.StringReader sReader = new java.io.StringReader(TEXT))
      {
        ParseReader reader = new ParseReader(sReader, "test");
        BaseEntry entry = (BaseEntry)BaseEntry.read(reader);

        m_logger.verify();

        assertNotNull("entry should have been read", entry);
        assertEquals("entry name does not match", "test",
                     entry.getName());
        assertEquals("entry does not match",
                     "#----- test\n"
                     + "\n"
                     + "base entry test =\n"
                     + "\n"
                     + "  references        a: 10,\n"
                     + "                    b: 5-10/20;\n"
                     + "  description       "
                     + "\"A thick, quilted, wool blanket.\";\n"
                     + "  short description \"A cozy, warm blanket.\";\n"
                     + "  synonyms          \"blanket, winter\",\n"
                     + "                    \"guru\";\n"
                     + "  categories        dagger,\n"
                     + "                    weapon,\n"
                     + "                    guru,\n"
                     + "                    test;\n"
                     + "  name              test.\n"
                     + "\n"
                     + "#.....\n",
                     entry.toString());

        assertTrue("category dagger", entry.hasCategory("dagger"));
        assertTrue("category weapon", entry.hasCategory("weapon"));
        assertFalse("category gugus", entry.hasCategory("gugus"));
        assertEquals("short description", "A cozy, warm blanket.",
                     entry.getShortDescription());
        assertTrue("is base", entry.isBase());
        // assertEquals("synonyms 0", "blanket, winter",
        //              entry.getSynonyms()[0]);
        // assertEquals("synonyms 1", "guru", entry.getSynonyms()[1]);
      }
    }

    //......................................................................
  }

  //........................................................................
}

