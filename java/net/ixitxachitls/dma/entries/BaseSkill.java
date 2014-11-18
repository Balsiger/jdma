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
import com.google.common.collect.Multimap;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseSkillProto;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.enums.Ability;
import net.ixitxachitls.dma.values.enums.SkillModifier;
import net.ixitxachitls.dma.values.enums.SkillRestriction;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the basic jDMA base spell.
 *
 * @file          BaseSkill.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public class BaseSkill extends BaseEntry
{
  public static class DC
  {
    public DC(int inDC, String inDescription)
    {
      m_dc = inDC;
      m_description = inDescription;
    }

    private final int m_dc;
    private final String m_description;

    public static final Value.Parser<DC> PARSER =
      new Value.Parser<DC>(2)
      {
        @Override
        public Optional<DC> doParse(String inValue, String inText)
        {
          try
          {
            int value = Integer.parseInt(inValue);
            return Optional.of(new DC(value, inText));
          }
          catch(NumberFormatException e)
          {
            return Optional.absent();
          }
        }
      };

    public int getDC()
    {
      return m_dc;
    }

    public String getDescription()
    {
      return m_description;
    }
  }

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * This is the internal, default constructor for an undefined value.
   */
  protected BaseSkill()
  {
    super(TYPE);
  }

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base item
   */
  public BaseSkill(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final BaseType<BaseSkill> TYPE =
    new BaseType<BaseSkill>(BaseSkill.class);

  /** The base ability for this skill. */
  public Ability m_ability = Ability.UNKNOWN;

  /** The check to make. */
  public Optional<String> m_check = Optional.absent();

  /** The action that can be done. */
  public Optional<String> m_action = Optional.absent();

  /** Can it be tried again. */
  public Optional<String> m_retry = Optional.absent();

  /** The special remarks. */
  public Optional<String> m_special = Optional.absent();

  /** The synergies to other skills. */
  public List<String> m_synergies = new ArrayList<String>();

  /** The restrictions. */
  public Optional<String> m_restriction = Optional.absent();

  /** What can be done untrained. */
  public Optional<String> m_untrained = Optional.absent();

  /** Restrictions when using the skill. */
  public List<SkillRestriction> m_restrictions = new ArrayList<>();

  /** A list of special modifiers to recognize. */
  public List<SkillModifier> m_modifiers = new ArrayList<>();

  /** Various DCs for this skill. */
  public List<DC> m_dcs = new ArrayList<>();

  /**
   * Get the index of the skills base ability.
   *
   * @return      the base ability of the skill.
   */
  public Ability getAbility()
  {
    return m_ability;
  }

  public Optional<String> getCheck()
  {
    return m_check;
  }

  public Optional<String> getAction()
  {
    return m_action;
  }

  public Optional<String> getRetry()
  {
    return m_retry;
  }

  public Optional<String> getSpecial()
  {
    return m_special;
  }

  public List<String> getSynergies()
  {
    return m_synergies;
  }

  public Optional<String> getRestriction()
  {
    return m_restriction;
  }

  public Optional<String> getUntrained()
  {
    return m_untrained;
  }

  public List<SkillRestriction> getRestrictions()
  {
    return m_restrictions;
  }

  public List<SkillModifier> getModifiers()
  {
    return m_modifiers;
  }

  public List<DC> getDCs()
  {
    return m_dcs;
  }

  public List<String> getModifierNames()
  {
    return SkillModifier.names();
  }

  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(BaseCharacter.Group.DM);
  }

  /**
   * Check if the skill can be used untrained.
   *
   * @return      true if it can be used trained only, false else
   *
   */
  public boolean isUntrained()
  {
    return !m_restrictions.contains(SkillRestriction.TRAINED_ONLY);
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

    values.put(Index.Path.ABILITIES, m_ability.toString());

    for(SkillModifier modifier : m_modifiers)
      values.put(Index.Path.MODIFIERS, modifier.toString());

    for(SkillRestriction restriction : m_restrictions)
      values.put(Index.Path.MODIFIERS, restriction.toString());

    return values;
  }

  @Override
  public void set(Values inValues)
  {
    super.set(inValues);

    m_ability = inValues.use("ability", m_ability, Ability.PARSER);
    m_check = inValues.use("check", m_check);
    m_action = inValues.use("action", m_action);
    m_retry= inValues.use("retry", m_retry);
    m_special = inValues.use("special", m_special);
    m_synergies = inValues.use("synergies", m_synergies);
    m_restriction = inValues.use("restriction", m_restriction);
    m_untrained = inValues.use("untrained", m_untrained);
    m_restrictions= inValues.use("restrictions", m_restrictions,
                                 SkillRestriction.PARSER);
    m_modifiers = inValues.use("modifier", m_modifiers, SkillModifier.PARSER);
    m_dcs = inValues.use("dcs", m_dcs, DC.PARSER, "dc", "text");
  }

  @Override
  public Message toProto()
  {
    BaseSkillProto.Builder builder = BaseSkillProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_ability != Ability.UNKNOWN)
      builder.setAbility(m_ability.toProto());

    if(m_check.isPresent())
      builder.setCheck(m_check.get());

    if(m_action.isPresent())
      builder.setAction(m_action.get());

    if(m_retry.isPresent())
      builder.setRetry(m_retry.get());

    if(m_special.isPresent())
      builder.setSpecial(m_special.get());

    for(String synergy : m_synergies)
      builder.addSynergy(synergy);

    if(m_restriction.isPresent())
      builder.setRestrictionText(m_restriction.get());

    if(m_untrained.isPresent())
      builder.setUntrained(m_untrained.get());

    for(SkillRestriction restriction : m_restrictions)
      builder.addRestriction(restriction.toProto());

    for(SkillModifier modifier : m_modifiers)
      builder.addModifier(modifier.toProto());

    for(DC dc : m_dcs)
      builder.addDc(BaseSkillProto.DC.newBuilder()
                    .setNumber(dc.getDC())
                    .setText(dc.getDescription())
                    .build());

    BaseSkillProto proto = builder.build();
    return proto;
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseSkillProto))
    {
      Log.warning("cannot parse proto " + inProto.getClass());
      return;
    }

    BaseSkillProto proto = (BaseSkillProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.hasAbility())
      m_ability = Ability.fromProto(proto.getAbility());

    if(proto.hasCheck())
      m_check = Optional.of(proto.getCheck());

    if(proto.hasAction())
      m_action = Optional.of(proto.getAction());

    if(proto.hasRetry())
      m_retry = Optional.of(proto.getRetry());

    if(proto.hasSpecial())
      m_special = Optional.of(proto.getSpecial());


    for(String synergy : proto.getSynergyList())
      m_synergies.add(synergy);

    if(proto.hasRestrictionText())
      m_restriction = Optional.of(proto.getRestrictionText());

    if(proto.hasUntrained())
      m_untrained = Optional.of(proto.getUntrained());

    for(BaseSkillProto.Restriction restriction : proto.getRestrictionList())
      m_restrictions.add(SkillRestriction.fromProto(restriction));

    for(BaseSkillProto.Modifier modifier : proto.getModifierList())
      m_modifiers.add(SkillModifier.fromProto(modifier));

    for(BaseSkillProto.DC dc : proto.getDcList())
      m_dcs.add(new DC(dc.getNumber(), dc.getText()));
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(BaseSkillProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

  //---------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- createBaseSkill() ----------------------------------------------

    /** Create a typical base item for testing purposes.
     *
     * @return the newly created base item
     *
     */
    public static AbstractEntry createBaseSkill()
    {
      try (ParseReader reader =
        new ParseReader(new java.io.StringReader(s_text), "test"))
      {
        return null; //BaseSkill.read(reader);
      }
    }

    //......................................................................

    //----- text -----------------------------------------------------------

    /** Test text. */
    private static String s_text =
      "#----- APPRAISE (INT) -----------------------------------------\n"
      + "\n"
      + "base skill Appraise =\n"
      + "\n"
      + "  ability            INT;\n"
      + "  check              \"How to check\";\n"
      + "  action             \"What action\";\n"
      + "  try again          \"Retry?\";\n"
      + "  special            \"Some special info.\";\n"
      + "  synergy            \"Synergies to other skill\";\n"
      + "  untrained          \"Untrained use\";\n"
      + "  worlds             generic;\n"
      + "  references         WTC 17524: 67;\n"
      + "  short description  \"Estimate the value of objects.\";\n"
      + "  description\n"
      + "\n"
      + "  \"The long description.\".\n"
      + "\n"
      + "#..............................................................\n";

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Test reading. */
    @org.junit.Test
    public void testRead()
    {
      //net.ixitxachitls.util.logging.Log.add("out",
      //                                      new net.ixitxachitls.util.logging
      //                                      .ANSILogger());

      String result =
        "#----- APPRAISE (INT)\n"
        + "\n"
        + "base skill Appraise =\n"
        + "\n"
        + "  ability           Intelligence;\n"
        + "  check             \"How to check\";\n"
        + "  action            \"What action\";\n"
        + "  try again         \"Retry?\";\n"
        + "  special           \"Some special info.\";\n"
        + "  synergy           \"Synergies to other skill\";\n"
        + "  untrained         \"Untrained use\";\n"
        + "  worlds            Generic;\n"
        + "  references        WTC 17524: 67;\n"
        + "  description       \"The long description.\";\n"
        + "  short description \"Estimate the value of objects.\";\n"
        + "  name              Appraise.\n"
        + "\n"
        + "#.....\n";

      AbstractEntry entry = createBaseSkill();

      //System.out.println("read entry:\n'" + entry + "'");

      assertNotNull("base item should have been read", entry);
      assertEquals("base item name does not match", "Appraise",
                   entry.getName());
      assertEquals("base item does not match", result, entry.toString());
    }

    //......................................................................
  }
}
