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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseFeatProto;
import net.ixitxachitls.dma.proto.Values.ModifierProto;
import net.ixitxachitls.dma.proto.Values.NameAndModifierProto;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.NameAndModifier;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.Condition;
import net.ixitxachitls.dma.values.enums.Affects;
import net.ixitxachitls.dma.values.enums.Effect;
import net.ixitxachitls.dma.values.enums.FeatType;
import net.ixitxachitls.dma.values.enums.Group;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the basic jDMA base feat.
 *
 * @file          BaseFeat.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

public class BaseFeat extends BaseEntry
{
  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
    * This is the internal, default constructor for an undefined value.
    */
  protected BaseFeat()
  {
    super(TYPE);
  }

  /**
    * This is the normal constructor.
    *
    * @param       inName the name of the base item
    */
  public BaseFeat(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final BaseType<BaseFeat> TYPE =
    new BaseType.Builder<>(BaseFeat.class).build();

  /** The type of the feat. */
  protected FeatType m_featType = FeatType.UNKNOWN;

  /** The benefits. */
  protected Optional<String> m_benefit = Optional.absent();

  /** The special remarks. */
  protected Optional<String> m_special = Optional.absent();

  /** The special remarks. */
  protected Optional<String> m_normal = Optional.absent();

  /** The prerequisites. */
  protected Optional<String> m_prerequisites = Optional.absent();

  /** The attack modifier provided by the feat, if any. */
  protected Optional<Modifier> m_attackModifier = Optional.absent();

  /** The damage modifier provided by the feat, if any. */
  protected Optional<Modifier> m_damageModifier = Optional.absent();

  /** The additional attacks. */
  protected Optional<Integer> m_additionalAttacks = Optional.absent();

  /** The initiative modifier. */
  protected Optional<Modifier> m_initiativeModifier = Optional.absent();

  /** The condition when this feat applies. */
  protected Optional<Condition> m_condition = Optional.absent();

  /** The skill modifiers given by the feat. */
  protected List<NameAndModifier> m_skillModifiers = new ArrayList<>();

  /** The strength modifier. */
  protected Optional<Modifier> m_strengthModifier = Optional.absent();

  /** The dexterity modifier. */
  protected Optional<Modifier> m_dexterityModifier = Optional.absent();

  /** The constitution modifier. */
  protected Optional<Modifier> m_constitutionModifier = Optional.absent();

  /** The intelligence modifier. */
  protected Optional<Modifier> m_intelligenceModifier = Optional.absent();

  /** The wisdom modifier. */
  protected Optional<Modifier> m_wisdomModifier = Optional.absent();

  /** The charisma modifier. */
  protected Optional<Modifier> m_charismaModifier = Optional.absent();

  /** The fortitude save modifier. */
  protected Optional<Modifier> m_fortitudeModifier = Optional.absent();

  /** The will save modifier. */
  protected Optional<Modifier> m_willModifier = Optional.absent();

  /** The reflex save modifier. */
  protected Optional<Modifier> m_reflexModifier = Optional.absent();


  /** The effects of the feat. */
  @Deprecated
  protected List<Effect> m_effects = new ArrayList<>();

  /**
   * Get the type of the feat.
   *
   * @return the type
   */
  public FeatType getFeatType()
  {
    return m_featType;
  }


  /**
   * Get the feats benefits.
   *
   * @return the benefits
   */
  public Optional<String> getBenefit()
  {
    return m_benefit;
  }

  /**
   * Get the special rules for the feat.
   *
   * @return the special
   */
  public Optional<String> getSpecial()
  {
    return m_special;
  }

  /**
   * Get the normal handling of situations without the feat.
   *
   * @return the normal situation
   */
  public Optional<String> getNormal()
  {
    return m_normal;
  }

  /**
   * Get the prerequisites for the feat.
   *
   * @return the prerequisites
   */
  public Optional<String> getPrerequisites()
  {
    return m_prerequisites;
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

  public Optional<Modifier> getInitiativeModifier()
  {
    return m_initiativeModifier;
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

  public Optional<Integer> getAdditionalAttacks()
  {
    return m_additionalAttacks;
  }

  public Optional<Condition> getCondition()
  {
    return m_condition;
  }

  public Optional<Modifier> getStrengthModifier()
  {
    return m_strengthModifier;
  }

  public Optional<Modifier> getDexterityModifier()
  {
    return m_dexterityModifier;
  }

  public Optional<Modifier> getConstitutionModifier()
  {
    return m_constitutionModifier;
  }

  public Optional<Modifier> getIntelligenceModifier()
  {
    return m_intelligenceModifier;
  }

  public Optional<Modifier> getWisdomModifier()
  {
    return m_wisdomModifier;
  }

  public Optional<Modifier> getCharismaModifier()
  {
    return m_charismaModifier;
  }

  public Optional<Modifier> getFortitudeModifier()
  {
    return m_fortitudeModifier;
  }

  public Optional<Modifier> getWillModifier()
  {
    return m_willModifier;
  }

  public Optional<Modifier> getReflexModifier()
  {
    return m_reflexModifier;
  }

  /**
   * Get the effects the feat has on values.
   *
   * @return a list with all the effects
   */
  public List<Effect> getEffects()
  {
    return m_effects;
  }

  /**
   * Check whether the given user is the DM for this entry.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   */
  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(Group.DM);
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

    values.put(Index.Path.TYPES, m_featType.toString());

    return values;
  }

  @Override
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    m_featType = inValues.use("feat_type", m_featType, FeatType.PARSER);
    m_benefit = inValues.use("benefit", m_benefit);
    m_special = inValues.use("special", m_special);
    m_normal = inValues.use("normal", m_normal);
    m_prerequisites = inValues.use("prerequisites", m_prerequisites);
    m_attackModifier = inValues.use("attack_modifier", m_attackModifier,
                                    Modifier.PARSER);
    m_damageModifier = inValues.use("damage_modifier", m_damageModifier,
                                    Modifier.PARSER);
    m_additionalAttacks = inValues.use("additional_attacks",
                                       m_additionalAttacks,
                                       Value.INTEGER_PARSER);
    m_initiativeModifier = inValues.use("initiative_modifier",
                                        m_initiativeModifier,
                                        Modifier.PARSER);
    m_condition = inValues.use("condition", m_condition, Condition.PARSER,
                               "generic", "weapon_style");
    m_skillModifiers = inValues.use("skill", m_skillModifiers,
                                    NameAndModifier.PARSER,
                                    "name", "modifier");
    m_strengthModifier = inValues.use("strength_modifier", m_strengthModifier,
                                      Modifier.PARSER);
    m_dexterityModifier = inValues.use("dexterity_modifier",
                                       m_dexterityModifier, Modifier.PARSER);
    m_constitutionModifier = inValues.use("constitution_modifier",
                                          m_constitutionModifier,
                                          Modifier.PARSER);
    m_intelligenceModifier = inValues.use("intelligence_modifier",
                                          m_intelligenceModifier,
                                          Modifier.PARSER);
    m_wisdomModifier = inValues.use("wisdom_modifier", m_wisdomModifier,
                                      Modifier.PARSER);
    m_charismaModifier = inValues.use("charisma_modifier", m_charismaModifier,
                                      Modifier.PARSER);
    m_fortitudeModifier = inValues.use("fortitude_modifier",
                                       m_fortitudeModifier, Modifier.PARSER);
    m_willModifier = inValues.use("will_modifier", m_willModifier,
                                  Modifier.PARSER);
    m_reflexModifier = inValues.use("reflex_modifier", m_reflexModifier,
                                    Modifier.PARSER);

    m_effects = inValues.use("effect", m_effects, Effect.PARSER,
                             "affects", "name", "modifier", "text");
  }

  @Override
  public Message toProto()
  {
    BaseFeatProto.Builder builder = BaseFeatProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_featType != FeatType.UNKNOWN)
      builder.setType(m_featType.toProto());

    if(m_benefit.isPresent())
      builder.setBenefit(m_benefit.get());

    if(m_special.isPresent())
      builder.setSpecial(m_special.get());

    if(m_normal.isPresent())
      builder.setNormal(m_normal.get());

    if(m_prerequisites.isPresent())
      builder.setPrerequisites(m_prerequisites.get());

    if(m_attackModifier.isPresent())
      builder.setAttackModifier(m_attackModifier.get().toProto());

    if(m_damageModifier.isPresent())
      builder.setDamageModifier(m_damageModifier.get().toProto());

    if(m_additionalAttacks.isPresent())
      builder.setAdditionalAttacks(m_additionalAttacks.get());

    if(m_initiativeModifier.isPresent())
      builder.setInitiativeModifier(m_initiativeModifier.get().toProto());

    if(m_condition.isPresent())
      builder.setCondition(m_condition.get().toProto());

    for(NameAndModifier skill : m_skillModifiers)
      builder.addSkillModifier(skill.toProto());

    if(m_strengthModifier.isPresent())
      builder.setStrengthModifier(m_strengthModifier.get().toProto());

    if(m_dexterityModifier.isPresent())
      builder.setDexterityModifier(m_dexterityModifier.get().toProto());

    if(m_constitutionModifier.isPresent())
      builder.setConstitutionModifier(m_constitutionModifier.get().toProto());

    if(m_intelligenceModifier.isPresent())
      builder.setIntelligenceModifier(m_intelligenceModifier.get().toProto());

    if(m_wisdomModifier.isPresent())
      builder.setWisdomModifier(m_wisdomModifier.get().toProto());

    if(m_charismaModifier.isPresent())
      builder.setCharismaModifier(m_charismaModifier.get().toProto());

    if(m_fortitudeModifier.isPresent())
      builder.setFortitudeModifier(m_fortitudeModifier.get().toProto());

    if(m_willModifier.isPresent())
      builder.setWillModifier(m_willModifier.get().toProto());

    if(m_reflexModifier.isPresent())
      builder.setReflexModifier(m_reflexModifier.get().toProto());

    for(Effect effect : m_effects)
    {
      BaseFeatProto.Effect.Builder effectBuilder =
        BaseFeatProto.Effect.newBuilder();

      effectBuilder.setAffects(effect.getAffects().toProto());
      if(effect.getName().isPresent())
        effectBuilder.setReference(effect.getName().get());
      if(effect.getModifier().isPresent())
        effectBuilder.setModifier(effect.getModifier().get().toProto());

      builder.addEffect(effectBuilder.build());
    }

    BaseFeatProto proto = builder.build();
    return proto;
  }

  /**
   * Merge the values of the given proto into this object.
   *
   * @param inProto the proto to merge from
   */
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseFeatProto))
    {
      Log.warning("cannot parse proto " + inProto.getClass());
      return;
    }

    BaseFeatProto proto = (BaseFeatProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.hasType())
      m_featType = FeatType.fromProto(proto.getType());

    if(proto.hasBenefit())
      m_benefit = Optional.of(proto.getBenefit());

    if(proto.hasSpecial())
      m_special = Optional.of(proto.getSpecial());

    if(proto.hasNormal())
      m_normal = Optional.of(proto.getNormal());

    if(proto.hasPrerequisites())
      m_prerequisites = Optional.of(proto.getPrerequisites());

    if(proto.hasAttackModifier())
      m_attackModifier =
          Optional.of(Modifier.fromProto(proto.getAttackModifier()));

    if(proto.hasDamageModifier())
      m_damageModifier =
          Optional.of(Modifier.fromProto(proto.getDamageModifier()));

    if(proto.hasAdditionalAttacks())
      m_additionalAttacks = Optional.of(proto.getAdditionalAttacks());

    if(proto.hasInitiativeModifier())
      m_initiativeModifier =
          Optional.of(Modifier.fromProto(proto.getInitiativeModifier()));

    if(proto.hasCondition())
      m_condition = Optional.of(Condition.fromProto(proto.getCondition()));

    for(NameAndModifierProto skill : proto.getSkillModifierList())
      m_skillModifiers.add(NameAndModifier.fromProto(skill));

    if(proto.hasStrengthModifier())
      m_strengthModifier =
          Optional.of(Modifier.fromProto(proto.getStrengthModifier()));

    if(proto.hasDexterityModifier())
      m_dexterityModifier =
          Optional.of(Modifier.fromProto(proto.getDexterityModifier()));

    if(proto.hasConstitutionModifier())
      m_constitutionModifier =
          Optional.of(Modifier.fromProto(proto.getConstitutionModifier()));

    if(proto.hasIntelligenceModifier())
      m_intelligenceModifier =
          Optional.of(Modifier.fromProto(proto.getIntelligenceModifier()));

    if(proto.hasWisdomModifier())
      m_wisdomModifier =
          Optional.of(Modifier.fromProto(proto.getWisdomModifier()));

    if(proto.hasCharismaModifier())
      m_charismaModifier =
          Optional.of(Modifier.fromProto(proto.getCharismaModifier()));

    if(proto.hasFortitudeModifier())
      m_fortitudeModifier =
          Optional.of(Modifier.fromProto(proto.getFortitudeModifier()));

    if(proto.hasWillModifier())
      m_willModifier = Optional.of(Modifier.fromProto(proto.getWillModifier()));

    if(proto.hasReflexModifier())
      m_reflexModifier =
          Optional.of(Modifier.fromProto(proto.getReflexModifier()));

    for(BaseFeatProto.Effect effect : proto.getEffectList())
      m_effects.add(new Effect(Affects.fromProto(effect.getAffects()),
                               effect.hasReference()
                                 ? Optional.of(effect.getReference())
                                 : Optional.<String>absent(),
                               effect.hasModifier()
                                 ? Optional.of(Modifier.fromProto
                                   (effect.getModifier()))
                                 : Optional.<Modifier>absent(),
                               Optional.<String>absent()));
  }

  public List<NameAndModifier> getSkillModifiers()
  {
    return m_skillModifiers;
  }

  public boolean hasSkillModifier(String inName)
  {
    for(NameAndModifier skill : m_skillModifiers)
      if(skill.hasName(inName))
        return true;

    return false;
  }

  public Modifier getSkillModifier(String inSkill)
  {
    for(NameAndModifier skill : m_skillModifiers)
      if(skill.hasName(inSkill))
        return skill.getModifier();

    return Modifier.EMPTY;
  }

  @Override
  protected Message defaultProto()
  {
    return BaseFeatProto.getDefaultInstance();
  }

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** The init Test. */
    @org.junit.Test
    public void init()
    {
      BaseFeat feat = new BaseFeat("Feat");

      assertEquals("name", "Feat", feat.getName());
      assertEquals("type", FeatType.UNKNOWN, feat.getFeatType());
      assertFalse("benefit", feat.getBenefit().isPresent());
      assertFalse("special", feat.getSpecial().isPresent());
      assertFalse("normal", feat.getNormal().isPresent());
      assertFalse("prerequisites", feat.getPrerequisites().isPresent());
      assertTrue("effects", feat.getEffects().isEmpty());
    }

    /** Test setting. */
    @org.junit.Test
    public void set()
    {
      BaseFeat feat = new BaseFeat("");

      Values values = new Values(
          new ImmutableSetMultimap.Builder<String, String>()
              .put("name", "Invincible")
              .put("feat_type", "FIGHTER")
              .put("benefit", "Cannot loose")
              .put("special", "wins")
              .put("normal", "looses")
              .put("prerequisites", "Are you a god?")
              .put("effect.affects", "Armor Class")
              .put("effect.name", "name-1")
              .put("effect.modifier", "+2")
              .put("effect.text", "text-1")
              .put("effect.affects", "Reflex save")
              .put("effect.name", "name-2")
              .put("effect.modifier", "+2 dodge")
              .put("effect.text", "text-2")
              .build());
      feat.set(values);
      assertEquals("messaegs", "[]", values.obtainMessages().toString());
      assertEquals("name", "Invincible", feat.getName());
      assertEquals("type", FeatType.FIGHTER, feat.getFeatType());
      assertEquals("benefit", "Cannot loose", feat.getBenefit().get());
      assertEquals("special", "wins", feat.getSpecial().get());
      assertEquals("normal", "looses", feat.getNormal().get());
      assertEquals("prerequisites", "Are you a god?",
                   feat.getPrerequisites().get());
      assertEquals("effects", "[Armor Class name-1 +2 text-1, "
                       + "Reflex Save name-2 +2 dodge text-2]",
                   feat.getEffects().toString());
    }

    /** Test user access. */
    @org.junit.Test
    public void user()
    {
      BaseCharacter character = new BaseCharacter("Me");
      BaseFeat feat = new BaseFeat("");

      assertTrue("shown to",
                 feat.isShownTo(Optional.<BaseCharacter>absent()));
      assertTrue("show to", feat.isShownTo(Optional.of(character)));

      assertFalse("is dm",
                  character.isDM(Optional.<BaseCharacter>absent()));
      assertFalse("is dm", feat.isDM(Optional.of(character)));
      character.setGroup(Group.ADMIN);
      assertTrue("is dm", feat.isDM(Optional.of(character)));
    }

    /** Test searchable. */
    @org.junit.Test
    public void searchables()
    {
      BaseFeat feat = new BaseFeat("Feast");
      assertEquals("size", 1, feat.collectSearchables().size());
      assertEquals("bases", "[]",
                   feat.collectSearchables().get("bases").toString());
    }

    /** Test indexes. */
    @org.junit.Test
    public void indexes()
    {
      BaseFeat feat = new BaseFeat("feat");
      assertEquals("indexes", "{TYPES=[Unknown]}",
                   feat.computeIndexValues().toString());
    }

    /** Test proto. */
    @org.junit.Test
    public void proto()
    {
      BaseFeatProto proto = BaseFeatProto
          .newBuilder()
          .setBase(BaseEntryProto.newBuilder()
                       .setAbstract(Entries.AbstractEntryProto
                                        .newBuilder()
                                        .setName("name")
                                        .setType("base feat")
                                        .build()))
          .setType(BaseFeatProto.Type.FIGHTER)
          .setBenefit("benefit")
          .setSpecial("special")
          .setNormal("normal")
          .setPrerequisites("prerequisites")
          .addEffect(
              BaseFeatProto.Effect.newBuilder()
                  .setAffects(Entries.BaseQualityProto.Effect.Affects.AC)
                  .setReference("refernece")
                  .setModifier(
                      ModifierProto.newBuilder()
                          .addModifier(
                              ModifierProto.Modifier
                                  .newBuilder()
                                  .setBaseValue(42)
                                  .setType(ModifierProto.Type.RAGE)
                                  .setCondition("condition")
                                  .build())
                          .build())
                  .build())
          .build();
      BaseFeat feat = new BaseFeat();
      feat.fromProto(proto);
      assertEquals("proto", proto, feat.toProto());
    }
  }
}
