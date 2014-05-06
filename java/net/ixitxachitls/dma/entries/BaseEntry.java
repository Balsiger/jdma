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

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.AbstractEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.ProductReference;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the base class for all base entries of DMA.
 *
 * All the values stored in this entry (and in all derivations) need to
 * be non-null!
 *
 * @file          BaseEntry.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public class BaseEntry extends AbstractEntry
{
  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The game worlds (this is configurable and thus not an enum). */
  private static final List<String> WORLDS = ImmutableList.of
    (// these are unsorted because they are the most important
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
     "Wheel of Time");

  /**
   * The default constructor, with undefined values.
   */
  protected BaseEntry()
  {
    super(TYPE);
  }

   /**
   * The default constructor, with undefined values.
   *
   * @param   inType the type of the entry
   */
  protected BaseEntry(AbstractType<? extends BaseEntry> inType)
  {
    super(inType);
  }

  /**
   * The complete constructor.
   *
   * @param       inName     the name of the entry
   */
  public BaseEntry(String inName)
  {
    this(inName, TYPE);
  }

  /**
   * The constructor for derivations, with a type.
   *
   * @param       inName     the name of the entry
   * @param       inType     the type of the entry
   */
  protected BaseEntry(String inName, AbstractType<? extends BaseEntry> inType)
  {
    super(inName, inType);
  }

  /** The type of this entry. */
  public static final BaseType<BaseEntry> TYPE =
    new BaseType<BaseEntry>(BaseEntry.class, "Base Entries");

  /** The world. */
  protected List<String> m_worlds = new ArrayList<>();

  /** The references for this entry. */
  protected List<ProductReference> m_references = new ArrayList<>();

  /** The descriptive text for this entry. */
  protected String m_description = UNDEFINED_STRING;

  /** The short description text for this entry. */
  protected String m_short = UNDEFINED_STRING;

  /** The synonyms for this entry. */
  protected List<String> m_synonyms = new ArrayList<>();

  /** The categories. */
  protected List<String> m_categories = new ArrayList<>();

  /** The information that is incomplete for the entry. */
  protected String m_incomplete = UNDEFINED_STRING;

  /**
   * Get the entry description.
   *
   * @return the description of the entry
   */
  public String getDescription()
  {
    return m_description;
  }

  /**
   * Get the combined description of the entry, including values of base items.
   *
   * @return a combined description with the sum and their sources.
   */
  public Combination<String> getCombinedDescription()
  {
    String description = getDescription();
    if(!description.isEmpty())
      return new Combination.String(this, description);

    List<Combination<String>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseEntry)
        combinations.add(entry.getCombinedDescription());

    return new Combination.String(this, combinations);
  }

  /**
  * Get the short description of the base entry.
   *
   * @return      the selection containing the selected world
   */
  public String getShortDescription()
  {
    return m_short;
  }

  /**
   * Get the combined short description of the entry, including values of base
   * items.
   *
   * @return a combined description with the sum and their sources.
   */
  public Combination<String> getCombinedShortDescription()
  {
    String description = getShortDescription();
    if(!description.isEmpty())
      return new Combination.String(this, description);

    List<Combination<String>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseEntry)
        combinations.add(entry.getCombinedShortDescription());

    return new Combination.String(this, combinations);
  }

  /**
   * Check if the current entry represents a base entry or not.
   *
   * @return      true if this is a base entry, false else
   */
  @Override
  public boolean isBase()
  {
    return true;
  }

  /**
   * Check if the base entry has a named category.
   *
   * @param       inCategory the category to look for
   *
   * @return      true if the category is present, false if not
   */
  public boolean hasCategory(String inCategory)
  {
    return m_categories.contains(inCategory);
  }

  /**
   * Check if the given base entry has a synonym with the given name.
   *
   * @param       inName the name to look for
   *
   * @return      true if the entry has a synonym, false if not
   */
  public boolean hasSynonym(String inName)
  {
    return m_synonyms.contains(inName);
  }

  /**
   * Get the worlds the product is for.
   *
   * @return a list of the worlds for this product
   */
  public List<String> getWorlds()
  {
    return Collections.unmodifiableList(m_worlds);
  }

  /**
   * Get the combined worlds of the entry, including values from base entries.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<List<String>> getCombinedWorlds()
  {
    if(!m_worlds.isEmpty())
      return new Combination.List<String>(this, m_worlds);

    List<Combination<List<String>>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedWorlds());

    return new Combination.List<String>(combinations, this);
  }

  /**
   * Get the worlds as a single string.
   *
   * @return the worlds as string representation
   */
  public String getWorldsString()
  {
    return Strings.COMMA_JOINER.join(m_worlds);
  }

  /**
   * Get the synonyms of the entry.
   *
   * @return      the synonyms
   */
  public List<String> getSynonyms()
  {
    return Collections.unmodifiableList(m_synonyms);
  }

  /**
   * Get the product references.
   *
   * @return the product references
   */
  public List<ProductReference> getReferences()
  {
    return Collections.unmodifiableList(m_references);
  }

  /**
   * Get the combined references of the entry, including values from base
   * entries.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<List<ProductReference>> getCombinedReferences()
  {
    List<Combination<List<ProductReference>>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedReferences());

    if(m_categories.isEmpty())
      return new Combination.Set<ProductReference>(combinations, this);

    return new Combination.Set<ProductReference>(this, m_references,
                                                 combinations);
  }

  /**
   * Get the categories of the entry.
   *
   * @return      the categories
   */
  public List<String> getCategories()
  {
    return Collections.unmodifiableList(m_categories);
  }

  /**
   * Get the combined categories of the entry, including values from base
   * entries.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<List<String>> getCombinedCategories()
  {
    List<Combination<List<String>>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedCategories());

    if(m_categories.isEmpty())
      return new Combination.Set<String>(combinations, this);

    return new Combination.Set<String>(this, m_categories, combinations);
  }

  /**
   * Get the incomplete information.
   *
   * @return the incomplete information
   */
  public String getIncomplete()
  {
    return m_incomplete;
  }

  /**
   * Get the combined incomplete data, including values of base items.
   *
   * @return a combination value with the sum and their sources.
   */
  public Combination<String> getCombinedIncomplete()
  {
    List<Combination<String>> combinations = new ArrayList<>();
    for(BaseEntry entry : getBaseEntries())
      if(entry instanceof BaseItem)
        combinations.add(((BaseItem)entry).getCombinedIncomplete());

    if(m_incomplete.isEmpty())
      return new Combination.String(this, combinations);

    return new Combination.String(this, m_incomplete, combinations);
  }

  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    for(String world : m_worlds)
      values.put(Index.Path.WORLDS, world);

    for(String category : getCategories())
      values.put(Index.Path.CATEGORIES, category);

    for(ProductReference reference : getReferences())
      values.put(Index.Path.REFERENCES, reference.getName());

    return values;
  }

  @Override
  public Message toProto()
  {
    BaseEntryProto.Builder builder = BaseEntryProto.newBuilder();

    builder.setAbstract((AbstractEntryProto)super.toProto());

    if(!m_description.isEmpty())
      builder.setDescription(m_description);
    if(!m_short.isEmpty())
      builder.setShortDescription(m_short);
    for(ProductReference reference : m_references)
      builder.addReference(reference.toProto());
    builder.addAllSynonym(m_synonyms);
    builder.addAllWorld(m_worlds);
    builder.addAllCategory(m_categories);
    if(!m_incomplete.isEmpty())
      builder.setIncomplete(m_incomplete);

    BaseEntryProto proto = builder.build();
    return proto;
  }

  @Override
  public void set(Values inValues)
  {
    super.set(inValues);

    m_description = inValues.use("description", m_description);
    m_short = inValues.use("short description", m_short);
    m_worlds = inValues.use("worlds", m_worlds, new Values.Checker()
    {
      @Override
      public boolean check(String inCheck)
      {
        return WORLDS.contains(inCheck);
      }
    });
    m_references = inValues.use("references",  m_references,
                                ProductReference.PARSER, "name", "pages");
    m_synonyms = inValues.use("synonyms", m_synonyms, Values.NOT_EMPTY);
    m_categories = inValues.use("categories", m_categories, Values.NOT_EMPTY);
    m_incomplete = inValues.use("incomplete", m_incomplete);
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseEntryProto))
    {
      Log.warning("cannot parse base entry proto " + inProto.getClass());
      return;
    }

    BaseEntryProto proto = (BaseEntryProto)inProto;

    super.fromProto(proto.getAbstract());

    if(proto.hasDescription())
      m_description = proto.getDescription();
    if(proto.hasShortDescription())
      m_short = proto.getShortDescription();

    for(BaseEntryProto.Reference reference : proto.getReferenceList())
      m_references.add(ProductReference.fromProto(reference));

    m_worlds = proto.getWorldList();
    m_categories = proto.getCategoryList();
    m_synonyms = proto.getSynonymList();
    m_incomplete = proto.getIncomplete();
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
}

