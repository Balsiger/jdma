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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.QualityProto;
import net.ixitxachitls.dma.values.AbilityModifier;
import net.ixitxachitls.dma.values.Condition;
import net.ixitxachitls.dma.values.Evaluator;
import net.ixitxachitls.dma.values.ExpressionValue;
import net.ixitxachitls.dma.values.KeyedModifier;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Speed;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Ability;
import net.ixitxachitls.dma.values.enums.Immunity;
import net.ixitxachitls.dma.values.enums.MovementMode;

/**
 * A monster specific quality.
 *
 * @file   Quality.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Quality extends NestedEntry
{
  /** Create a default quality. */
  public Quality()
  {
  }

  /** The parameters defined for this quality to parameterize the base value. */
  private final Map<String, String> m_parameters = new HashMap<>();

  /** The base quality, if found. */
  private Optional<Optional<BaseQuality>> m_base = Optional.absent();

  /** The condition for the quality, if any. */
  private Optional<Condition> m_condition = Optional.absent();

  public static final Creator<Quality> CREATOR =
      new NestedEntry.Creator<Quality>()
      {
        @Override
        public Quality create()
        {
          return new Quality();
        }
      };

  /**
   * Get the base quality, if it can be found.
   *
   * @return the base quality, if found
   */
  public Optional<BaseQuality> getBase()
  {
    if(!m_base.isPresent())
    {
      if(m_name.isPresent())
        m_base = Optional.of(DMADataFactory.get().<BaseQuality>getEntry
            (new EntryKey(m_name.get(), BaseQuality.TYPE)));
      else
        return Optional.absent();
    }

    return m_base.get();
  }

  public Optional<Condition> getCondition()
  {
    return m_condition;
  }

  /**
   * Get the parameters for the quality.
   *
   * @return a map of key to value parameters
   */
  public Map<String, String> getParameters()
  {
    return Collections.unmodifiableMap(m_parameters);
  }

  public Map<String, String> getNonFormattedParameters()
  {
    if(!getBase().isPresent())
      return getParameters();

    BaseQuality base = getBase().get();
    if(!base.getNameFormat().isPresent())
      return getParameters();

    String format = base.getNameFormat().get();
    Map<String, String> parameters = new HashMap<>();
    for(Map.Entry<String, String> entry : m_parameters.entrySet())
      if(!format.contains("$" + entry.getKey()))
        parameters.put(entry.getKey(), entry.getValue());

    return parameters;
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
      return parametrizeText(getBase().get().getShortDescription());

    return "";
  }

  public String getDescription()
  {
    if(getBase().isPresent())
      return parametrizeText(getBase().get().getDescription());

    return "";
  }

  /**
   * Get the speed modification for the given movement mode.
   *
   * @param inMode the movement mode for which to get the speed modification
   * @return the speed modification, if any
   */
  public Optional<Speed> getSpeed(MovementMode inMode)
  {
    if(!getBase().isPresent())
      return Optional.absent();

    Optional<ExpressionValue<Speed>> speed = getBase().get().getSpeed();
    if(!speed.isPresent())
      return Optional.absent();

    Optional<Speed> value = Optional.absent();
    if(speed.get().hasValue())
      value = speed.get().getValue();
    else
      value = speed.get().getValue(m_parameters, Speed.PARSER);

    if(value.isPresent() && value.get().getMode() == inMode)
      return value;

    return Optional.absent();
  }

  @Override
  public void set(Values inValues)
  {
    m_name = inValues.use("name", m_name);
    m_condition = inValues.use("condition", m_condition, Condition.PARSER,
                               "generic", "weapon_style");

    List<String> names = new ArrayList<>();
    List<String> values = new ArrayList<>();
    for(Map.Entry<String, String> entry : m_parameters.entrySet())
    {
      names.add(entry.getKey());
      values.add(entry.getValue());
    }

    names = inValues.use("parameter.name", names);
    values = inValues.use("parameter.value", values);

    m_parameters.clear();
    for(int i = 0; i < names.size() && i < values.size(); i++)
      m_parameters.put(names.get(i), values.get(i));
  }

  /**
   * Convert the quality to a proto.
   *
   * @return the qualities value in a proto
   */
  public QualityProto toProto()
  {
    QualityProto.Builder builder = QualityProto.newBuilder();

    if(m_name.isPresent())
      builder.setName(m_name.get());
    else
      builder.setName("unknown");

    if(m_condition.isPresent())
      builder.setCondition(m_condition.get().toProto());

    for(Map.Entry<String, String> parameter : m_parameters.entrySet())
      builder.addParameter(QualityProto.Parameter.newBuilder()
                               .setName(parameter.getKey())
                               .setValue(parameter.getValue())
                               .build());

    QualityProto proto = builder.build();
    return proto;
  }

  /**
   * Create a quality from the given proto.
   *
   * @param inProto the proto values
   * @return the newly created quality
   */
  public static Quality fromProto(QualityProto inProto)
  {
    Quality quality = new Quality();
    quality.m_name = Optional.of(inProto.getName());
    if(inProto.hasCondition())
      quality.m_condition =
          Optional.of(Condition.fromProto(inProto.getCondition()));

    for(QualityProto.Parameter parameter : inProto.getParameterList())
      quality.m_parameters.put(parameter.getName(), parameter.getValue());

    return quality;
  }

  public String toString()
  {
    if(m_name.isPresent())
      return m_name.get();

    return "(unknown)";
  }

  public List<AbilityModifier> abilityModifiers()
  {
    List<AbilityModifier> modifiers = new ArrayList<>();
    if(getBase().isPresent())
      for(AbilityModifier modifier : getBase().get().getAbilityModifiers())
        modifiers.add(parametrize(modifier));

    return modifiers;
  }

  public List<Immunity> immunities()
  {
    if(getBase().isPresent())
      return getBase().get().getImmunities();

    return new ArrayList<>();
  }

  public List<KeyedModifier> skillModifiers()
  {
    List<KeyedModifier> modifiers = new ArrayList<>();

    if(getBase().isPresent())
      for(KeyedModifier modifier : getBase().get().getSkillModifiers())
        modifiers.add(parametrize(modifier));

    return modifiers;
  }

  public List<String> bonusFeats()
  {
    if(getBase().isPresent())
      return getBase().get().getBonusFeats();

    return Collections.emptyList();
  }

  public Modifier abilityModifier(Ability inAbility)
  {
    Modifier modifier = new Modifier();

    if(!getBase().isPresent())
      return modifier;

    for(AbilityModifier abilityMod : getBase().get().getAbilityModifiers())
      if(abilityMod.getAbility() == inAbility)
        modifier = (Modifier)modifier.add(abilityMod.getModifier());

    return parametrize(modifier);
  }

  public Modifier reflexModifier()
  {
    if(getBase().isPresent())
      if(getBase().get().getReflexModifier().isPresent())
        return parametrize(getBase().get().getReflexModifier().get());

    return new Modifier();
  }

  public Modifier willModifier()
  {
    if(getBase().isPresent())
      if(getBase().get().getWillModifier().isPresent())
        return parametrize(getBase().get().getWillModifier().get());

    return new Modifier();
  }

  public Modifier fortitudeModifier()
  {
    if(getBase().isPresent())
      if(getBase().get().getFortitudeModifier().isPresent())
        return parametrize(getBase().get().getFortitudeModifier().get());

    return new Modifier();
  }

  public Modifier skillModifier(String inSkill)
  {
    Modifier result = new Modifier();

    if(getBase().isPresent())
      for(KeyedModifier modifier
          : getBase().get().getSkillModifiers())
        if(modifier.getKey().equalsIgnoreCase(inSkill))
          result = (Modifier)result.add(parametrize(modifier.getModifier()));

    return parametrize(result);
  }

  public Modifier attackModifier()
  {
    if(getBase().isPresent() && getBase().get().getAttackModifier().isPresent())
      return parametrize(getBase().get().getAttackModifier().get());

    return new Modifier();
  }

  public Modifier damageModifier()
  {
    if(getBase().isPresent() && getBase().get().getDamageModifier().isPresent())
      return parametrize(getBase().get().getDamageModifier().get());

    return new Modifier();
  }

  private Modifier parametrize(Modifier inModifier) {
    if(!inModifier.hasAnyCondition())
      return inModifier;

    return new Modifier(inModifier.getModifier(),
                        inModifier.getType(),
                        parametrizeText(inModifier.getCondition()),
                        parametrize(inModifier.getNext()));
  }

  private AbilityModifier parametrize(AbilityModifier inModifier) {
    return new AbilityModifier(inModifier.getAbility(),
                               parametrize(inModifier.getModifier()));
  }

  private KeyedModifier parametrize(KeyedModifier inModifier)
  {
    return new KeyedModifier(inModifier.getKey(),
                             parametrize(inModifier.getModifier()));
  }

  private Optional<Modifier> parametrize(Optional<Modifier> inModifier) {
    if(!inModifier.isPresent())
      return inModifier;

    if(!inModifier.get().hasAnyCondition())
      return inModifier;

    return Optional.of(parametrize(inModifier.get()));
  }

  private Optional<String> parametrizeText(Optional<String> inText)
  {
    if(inText.isPresent())
      return Optional.of(parametrizeText(inText.get()));

    return inText;
  }

  private String parametrizeText(String inText) {
    Evaluator evaluator = new Evaluator(m_parameters);
    return evaluator.evaluate(inText);
  }

  public String getFormattedName()
  {
    if(!getBase().isPresent())
      return getName();

    BaseQuality base = getBase().get();
    if(!base.getNameFormat().isPresent())
      return base.getName();

    return parametrizeText(
        base.getNameFormat().get().replaceAll("<name>", base.getName()));
  }
}
