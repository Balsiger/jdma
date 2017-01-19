/******************************************************************************
 * Copyright (c) 2002-2013 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.FeatProto;
import net.ixitxachitls.dma.values.Condition;
import net.ixitxachitls.dma.values.Evaluator;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.NameAndModifier;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.FeatType;
import net.ixitxachitls.util.CommandLineParser;

/**
 * An actual feat.
 *
 * @file   feat.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Feat extends NestedEntry
{
  /**
   * Create a default, unnamed feat.
   */
  public Feat()
  {
  }

  public Feat(String inName)
  {
    m_name = Optional.of(inName);
  }

  public Feat(String inName, String inQualifier) {
    this(inName);

    m_qualifier = Optional.of(inQualifier);
  }

  /** The qualifier for the feat. */
  private Optional<String> m_qualifier = Optional.absent();

  /** The base feat to this feat. */
  private Optional<Optional<BaseFeat>> m_base = Optional.absent();

  public static final Creator<Feat> CREATOR = new NestedEntry.Creator<Feat>()
  {
    @Override
    public Feat create()
    {
      return new Feat();
    }
  };

  /**
   * Get the base feat.
   *
   * @return the base feat, if found
   */
  public Optional<BaseFeat> getBase()
  {
    if(!m_base.isPresent())
    {
      if(m_name.isPresent())
        m_base = Optional.of(DMADataFactory.get().<BaseFeat>getEntry
            (new EntryKey(m_name.get(), BaseFeat.TYPE)));
      else
        return Optional.absent();
    }

    return m_base.get();
  }

  @Override
  public String getName() {
    if(getBase().isPresent())
      return getBase().get().getName();

    return super.getName();
  }

  /**
   * Get the feat qualifier.
   *
   * @return the qualitier
   */
  public Optional<String> getQualifier()
  {
    return m_qualifier;
  }

  public String baseName()
  {
    if(getBase().isPresent())
      return getBase().get().getName();

    return getName();
  }

  public String getShortDescription()
  {
    if(getBase().isPresent())
      return getBase().get().getShortDescription();

    return "";
  }

  public FeatType getType()
  {
    if(getBase().isPresent())
      return getBase().get().getFeatType();

    return FeatType.UNKNOWN;
  }

  public Optional<String> getBenefit()
  {
    if(getBase().isPresent())
      return getBase().get().getBenefit();

    return Optional.absent();
  }

  /**
   * Get the attack modifier provided by this feat.
   *
   * @return the attack modifier
   */
  public Modifier attackModifier()
  {
    if(getBase().isPresent() && getBase().get().getAttackModifier().isPresent())
      return getBase().get().getAttackModifier().get();

    return Modifier.EMPTY;
  }

  public Modifier initiativeModifier()
  {
    if(getBase().isPresent()
        && getBase().get().getInitiativeModifier().isPresent())
      return getBase().get().getInitiativeModifier().get();

    return Modifier.EMPTY;
  }

  /**
   * Get the damage modifier provided by this feat.
   *
   * @return the damage modifier
   */
  public Modifier damageModifier()
  {
    if(getBase().isPresent() && getBase().get().getDamageModifier().isPresent())
      return getBase().get().getDamageModifier().get();

    return Modifier.EMPTY;
  }

  public boolean hasSkillModifier(String inName)
  {
    return getBase().isPresent() && getBase().get().hasSkillModifier(inName);
  }

  public Modifier skillModifier(String inSkill)
  {
    if(getBase().isPresent())
      return getBase().get().getSkillModifier(inSkill);

    return Modifier.EMPTY;
  }

  public int additionalAttacks()
  {
    if(getBase().isPresent()
        && getBase().get().getAdditionalAttacks().isPresent())
      return getBase().get().getAdditionalAttacks().get();

    return 0;
  }

  public Optional<Condition> getCondition()
  {
    if(getBase().isPresent())
      return getBase().get().getCondition();

    return Optional.absent();
  }

  public Modifier getStrengthModifier()
  {
    if(!getBase().isPresent()
        || !getBase().get().getStrengthModifier().isPresent())
      return Modifier.EMPTY;

    return getBase().get().getStrengthModifier().get();
  }

  public Modifier getDexterityModifier()
  {
    if(!getBase().isPresent()
        || !getBase().get().getDexterityModifier().isPresent())
      return Modifier.EMPTY;

    return getBase().get().getDexterityModifier().get();
  }

  public Modifier getConstitutionModifier()
  {
    if(!getBase().isPresent()
        || !getBase().get().getConstitutionModifier().isPresent())
      return Modifier.EMPTY;

    return getBase().get().getConstitutionModifier().get();
  }

  public Modifier getIntelligenceModifier()
  {
    if(!getBase().isPresent()
        || !getBase().get().getIntelligenceModifier().isPresent())
      return Modifier.EMPTY;

    return getBase().get().getIntelligenceModifier().get();
  }

  public Modifier getWisdomModifier()
  {
    if(!getBase().isPresent()
        || !getBase().get().getWisdomModifier().isPresent())
      return Modifier.EMPTY;

    return getBase().get().getWisdomModifier().get();
  }

  public Modifier getCharismaModifier()
  {
    if(!getBase().isPresent()
        || !getBase().get().getCharismaModifier().isPresent())
      return Modifier.EMPTY;

    return getBase().get().getCharismaModifier().get();
  }

  public Modifier fortitudeModifier()
  {
    if(!getBase().isPresent()
        || !getBase().get().getFortitudeModifier().isPresent())
      return Modifier.EMPTY;

    return getBase().get().getFortitudeModifier().get();
  }

  public Modifier willModifier()
  {
    if(!getBase().isPresent()
        || !getBase().get().getWillModifier().isPresent())
      return Modifier.EMPTY;

    return getBase().get().getFortitudeModifier().get();
  }

  public Modifier reflexModifier()
  {
    if(!getBase().isPresent()
        || !getBase().get().getReflexModifier().isPresent())
      return Modifier.EMPTY;

    return getBase().get().getReflexModifier().get();
  }

  @Override
  public String toString()
  {
    return (m_name.isPresent() ? m_name.get() : "*undefined*")
      + (m_qualifier.isPresent() ? " (" + m_qualifier + ")" : "")
      + (m_base.isPresent() && m_base.get().isPresent()
         ? " [" + m_base.get().get().getName() : "");
  }

  @Override
  public void set(Values inValues)
  {
    m_name = inValues.use("name", m_name);
    m_qualifier = inValues.use("qualifier", m_qualifier);
  }

  /**
   * Convert the feat into a proto.
   *
   * @return the proto representing the feat
   */
  public FeatProto toProto()
  {
    FeatProto.Builder builder = FeatProto.newBuilder();

    if(m_name.isPresent())
      builder.setName(m_name.get());
    else
      builder.setName("unknown");

    if(m_qualifier.isPresent())
      builder.setQualifier(m_qualifier.get());

    FeatProto proto = builder.build();
    return proto;
  }

  /**
   * Convert the proto to a feat.
   *
   * @param inProto the proto to convert
   * @return the newly created feat
   */
  public static Feat fromProto(FeatProto inProto)
  {
    Feat feat = new Feat();
    feat.m_name = Optional.of(inProto.getName());
    if(inProto.hasQualifier())
      feat.m_qualifier = Optional.of(inProto.getQualifier());

    return feat;
  }

  // PHB p. 87
  public static int availableFeats(int inLevel) {
    return inLevel / 3 + 1;
  }

  @Override
  public boolean equals(Object o)
  {
    if(this == o)
      return true;

    if(o == null || getClass() != o.getClass())
      return false;

    final Feat feat = (Feat)o;
    return m_qualifier.equals(feat.m_qualifier)
        && m_name.equals(feat.m_name);
  }

  @Override
  public int hashCode()
  {
    return m_qualifier.hashCode()
        + 31 * m_name.hashCode();
  }
}
