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
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseQualityProto;
import net.ixitxachitls.dma.values.AbilityModifier;
import net.ixitxachitls.dma.values.ExpressionValue;
import net.ixitxachitls.dma.values.KeyedModifier;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Speed;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Affects;
import net.ixitxachitls.dma.values.enums.Effect;
import net.ixitxachitls.dma.values.enums.EffectType;
import net.ixitxachitls.dma.values.enums.Group;
import net.ixitxachitls.dma.values.enums.Immunity;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the basic jDMA base quality.
 *
 * @file          BaseQuality.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public class BaseQuality extends BaseEntry
{
  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * This is the internal, default constructor for an undefined value.
   */
  protected BaseQuality()
  {
    super(TYPE);
  }

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base item
   */
  public BaseQuality(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final BaseType<BaseQuality> TYPE =
      new BaseType.Builder<BaseQuality>(BaseQuality.class)
        .multiple("Base Qualities")
        .link("quality", "qualities")
        .build();

  /** The type of the effect. */
  private EffectType m_qualityType = EffectType.UNKNOWN;

  /** The effects of the feat. */
  @Deprecated // ?
  private List<Effect> m_effects = new ArrayList<>();

  /** The name qualifier, if any. */
  @Deprecated // ?
  private Optional<String> m_qualifier = Optional.absent();

  /** The speed change the quality is responsible for. */
  private Optional<ExpressionValue<Speed>> m_speed = Optional.absent();

  /** The ability modifiers. */
  private List<AbilityModifier> m_abilityModifiers = new ArrayList<>();

  /** The immunities. */
  private List<Immunity> m_immunities = new ArrayList<>();

  /** Relfex modifier. */
  private Optional<Modifier> m_reflexModifier = Optional.absent();

  /** Will modifier.*/
  private Optional<Modifier> m_willModifier = Optional.absent();

  /** Fortitude modifier.*/
  private Optional<Modifier> m_fortitudeModifier = Optional.absent();

  /** The skill modifiers. */
  private List<KeyedModifier> m_skillModifiers = new ArrayList<>();

  /** The modifier for attacks. */
  private Optional<Modifier> m_attackModifier = Optional.absent();

  /** The modifier for damage. */
  private Optional<Modifier> m_damageModifier = Optional.absent();
  private Optional<Modifier> m_acModifier = Optional.absent();

  /** The bonus feats (or-ed). */
  private List<String> m_bonusFeats = new ArrayList<>();

  private Optional<String> m_nameFormat = Optional.absent();

  /**
   * Get the type of the quality.
   *
   * @return the type
   */
  public EffectType getQualityType()
  {
    return m_qualityType;
  }

  /**
   * Get the speed modification of the quality, if any.
   *
   * @return the speed
   */
  public Optional<ExpressionValue<Speed>> getSpeed()
  {
    return m_speed;
  }

  /**
   * Get all the ability modifiers provided by the quality.
   *
   * @return the list of modifiers
   */
  public List<AbilityModifier> getAbilityModifiers()
  {
    return Collections.unmodifiableList(m_abilityModifiers);
  }

  /**
   * Get the list of immunities provided by the quality.
   *
   * @return the list of immunities.
   */
  public List<Immunity> getImmunities()
  {
    return Collections.unmodifiableList(m_immunities);
  }

  /**
   * Get the reflex modifier provided by the quality, if any.
   *
   * @return the reflex modifier
   */
  public Optional<Modifier> getReflexModifier()
  {
    return m_reflexModifier;
  }

  /**
   * Get the will modifier provided by the quality, if any.
   *
   * @return thw will modifier
   */
  public Optional<Modifier> getWillModifier()
  {
    return m_willModifier;
  }

  /**
   * Get the fortitude modifier provided by the quality, if any.
   *
   * @return the fortitude modifier
   */
  public Optional<Modifier> getFortitudeModifier()
  {
    return m_fortitudeModifier;
  }

  /**
   * Get the modifiers to skill provided by this qualit.
   *
   * @return the list of skill modifiers
   */
  public List<KeyedModifier> getSkillModifiers()
  {
    return Collections.unmodifiableList(m_skillModifiers);
  }

  /**
   * Get the attack modifier provided by this quality.
   *
   * @return the attack modifier
   */
  public Optional<Modifier> getAttackModifier()
  {
    return m_attackModifier;
  }

  public Optional<Modifier> getAcModifier()
  {
    return m_acModifier;
  }

  /**
   * Get the damage modifier provided by this quality.
   *
   * @return the damage modifier
   */
  public Optional<Modifier> getDamageModifier()
  {
    return m_damageModifier;
  }

  /**
   * Simple getter for bonus feats.
   *
   * @return the bonusFeats
   */
  public List<String> getBonusFeats()
  {
    return Collections.unmodifiableList(m_bonusFeats);
  }

  public Optional<String> getNameFormat()
  {
    return m_nameFormat;
  }

  @Deprecated
  public List<Effect> getEffects()
  {
    return m_effects;
  }

  @Override
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    m_qualityType = inValues.use("quality_type", m_qualityType,
                                 EffectType.PARSER);
    m_speed = inValues.use("speed", m_speed,
                           ExpressionValue.parser(Speed.PARSER));
    m_abilityModifiers = inValues.use("ability_modifier", m_abilityModifiers,
                                      AbilityModifier.PARSER,
                                      "ability", "modifier");
    m_immunities = inValues.use("immunity", m_immunities, Immunity.PARSER);
    m_reflexModifier = inValues.use("reflex_modifier", m_reflexModifier,
                                    Modifier.PARSER);
    m_willModifier = inValues.use("will_modifier", m_willModifier,
                                  Modifier.PARSER);
    m_fortitudeModifier = inValues.use("fortitude_modifier",
                                       m_fortitudeModifier,
                                       Modifier.PARSER);
    m_skillModifiers = inValues.use("skill_modifier", m_skillModifiers,
                                      KeyedModifier.PARSER,
                                      "skill", "modifier");
    m_attackModifier = inValues.use("attack_modifier", m_attackModifier,
                                    Modifier.PARSER);
    m_damageModifier = inValues.use("damage_modifier", m_damageModifier,
                                    Modifier.PARSER);
    m_acModifier = inValues.use("ac_modifier", m_acModifier, Modifier.PARSER);
    m_bonusFeats = inValues.use("bonus_feat", m_bonusFeats);
    m_nameFormat = inValues.use("name_format", m_nameFormat);

    m_effects = inValues.use("effect", m_effects, Effect.PARSER,
                             "affects", "name", "modifier", "text");
    m_qualifier = inValues.use("qualifier", m_qualifier);
  }

 /**
   * Get all the values for all the indexes.
   *
   * @return      a multi map of values per index name
   */
  @Override
  public Multimap<Index.Path, String> computeIndexValues()
  {
    Multimap<Index.Path, String> values = super.computeIndexValues();

    values.put(Index.Path.EFFECT_TYPES, m_qualityType.toString());
    for(Effect effect : m_effects)
      values.put(Index.Path.AFFECTS, effect.getAffects().toString());

    return values;
  }

  /**
   * Check whether the given user is the DM for this entry.
   *
   * @param       inUser the user accessing
   * @return      true for DM, false for not
   */
  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(Group.DM);
  }

  @Override
  public Message toProto()
  {
    BaseQualityProto.Builder builder = BaseQualityProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_qualityType != EffectType.UNKNOWN)
      builder.setType(m_qualityType.toProto());

    for(Effect effect : m_effects)
    {
      BaseQualityProto.Effect.Builder effectBuilder =
        BaseQualityProto.Effect.newBuilder();

      effectBuilder.setAffects(effect.getAffects().toProto());
      if(effect.getName().isPresent())
        effectBuilder.setReference(effect.getName().get());
      if(effect.getModifier().isPresent())
        effectBuilder.setModifier(effect.getModifier().get().toProto());
      if(effect.getText().isPresent())
        effectBuilder.setText(effect.getText().get());

      builder.addEffect(effectBuilder.build());
    }

    if(m_qualifier.isPresent())
      builder.setQualifier(m_qualifier.get());

    if(m_speed.isPresent())
      if(m_speed.get().hasValue())
        builder.setSpeed(m_speed.get().getValue().get().toProto());
      else
        builder.setSpeedExpression(m_speed.get().toProto());

    for(AbilityModifier modifier : m_abilityModifiers)
      builder.addAbilityModifier(modifier.toProto());

    for(Immunity immunity : m_immunities)
      builder.addImmunity(immunity.toProto());

    if(m_reflexModifier.isPresent())
      builder.setReflexModifier(m_reflexModifier.get().toProto());

    if(m_willModifier.isPresent())
      builder.setWillModifier(m_willModifier.get().toProto());

    if(m_fortitudeModifier.isPresent())
      builder.setFortitudeModifier(m_fortitudeModifier.get().toProto());

    for(KeyedModifier skillModifier : m_skillModifiers)
      builder.addSkillModifier(skillModifier.toProto());

    if(m_attackModifier.isPresent())
      builder.setAttackModifier(m_attackModifier.get().toProto());

    if(m_damageModifier.isPresent())
      builder.setDamageModifier(m_damageModifier.get().toProto());

    if(m_acModifier.isPresent())
      builder.setAcModifier(m_acModifier.get().toProto());

    for(String feat : m_bonusFeats)
      builder.addBonusFeat(feat);

    if(m_nameFormat.isPresent())
      builder.setNameFormat(m_nameFormat.get());

    BaseQualityProto proto = builder.build();
    return proto;
  }

  /**
   * Set values of the quality from the given proto.
   *
   * @param inProto the proto with the values
   */
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseQualityProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    BaseQualityProto proto = (BaseQualityProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.hasType())
      m_qualityType = EffectType.fromProto(proto.getType());

    for(BaseQualityProto.Effect effect : proto.getEffectList())
      m_effects.add(new Effect
                    (Affects.fromProto(effect.getAffects()),
                     effect.hasReference()
                       ? Optional.of(effect.getReference())
                       : Optional.<String>absent(),
                     effect.hasModifier()
                       ? Optional.of(Modifier.fromProto
                         (effect.getModifier()))
                       : Optional.<Modifier>absent(),
                     effect.hasText()
                       ? Optional.of(effect.getText())
                       : Optional.<String>absent()));

    if(proto.hasSpeed())
      m_speed = Optional.of
        (new ExpressionValue<Speed>(Speed.fromProto(proto.getSpeed())));
    else if(proto.hasSpeedExpression())
      m_speed = Optional.of
        (ExpressionValue.<Speed>fromProto(proto.getSpeedExpression()));

    if(proto.hasQualifier())
      m_qualifier = Optional.of(proto.getQualifier());

    for(BaseQualityProto.AbilityModifier modifier
        : proto.getAbilityModifierList())
      m_abilityModifiers.add(AbilityModifier.fromProto(modifier));

    for(BaseQualityProto.Immunity immunity : proto.getImmunityList())
      m_immunities.add(Immunity.fromProto(immunity));

    if(proto.hasReflexModifier())
      m_reflexModifier =
        Optional.of(Modifier.fromProto(proto.getReflexModifier()));

    if(proto.hasWillModifier())
      m_willModifier =
        Optional.of(Modifier.fromProto(proto.getWillModifier()));

    if(proto.hasFortitudeModifier())
      m_fortitudeModifier =
        Optional.of(Modifier.fromProto(proto.getFortitudeModifier()));

    if(proto.hasAttackModifier())
      m_attackModifier =
          Optional.of(Modifier.fromProto(proto.getAttackModifier()));

    if(proto.hasDamageModifier())
      m_damageModifier =
          Optional.of(Modifier.fromProto(proto.getDamageModifier()));

    if(proto.hasAcModifier())
      m_acModifier = Optional.of(Modifier.fromProto(proto.getAcModifier()));

    for(BaseQualityProto.KeyedModifier skillModifeir
          : proto.getSkillModifierList())
      m_skillModifiers.add(KeyedModifier.fromProto(skillModifeir));

    for(String feat : proto.getBonusFeatList())
      m_bonusFeats.add(feat);

    if(proto.hasNameFormat())
      m_nameFormat = Optional.of(proto.getNameFormat());
  }

  @Override
  protected Message defaultProto()
  {
    return BaseQualityProto.getDefaultInstance();
  }

  //---------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }
}
