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

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.FeatProto;
import net.ixitxachitls.dma.values.Condition;
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

    return new Modifier();
  }

  public Modifier initiativeModifier()
  {
    if(getBase().isPresent()
        && getBase().get().getInitiativeModifier().isPresent())
      return getBase().get().getInitiativeModifier().get();

    return new Modifier();
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

    return new Modifier();
  }

  public Modifier skillModifier(String inSkill)
  {
    if(getBase().isPresent())
      return getBase().get().getSkillModifier(inSkill);

    return new Modifier();
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
}
