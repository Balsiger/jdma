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

//------------------------------------------------------------------ imports

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
import net.ixitxachitls.dma.proto.Values.SharedProto;
import net.ixitxachitls.dma.proto.Values.SharedProto.SkillSubtype;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.FormattedText;
import net.ixitxachitls.dma.values.LongFormattedText;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.Union;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the basic jDMA base spell.
 *
 * @file          BaseSkill.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BaseSkill extends BaseEntry
{
  //----------------------------------------------------------------- nested

  //----- subtype ----------------------------------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The possible sizes in the game. */
  public enum Subtype implements EnumSelection.Named,
    EnumSelection.Proto<SharedProto.SkillSubtype>
  {
    /** Drow religion. */
    DROW_RELIGION("Drow Religion", SharedProto.SkillSubtype.DROW_RELIGION),

    /** Religion. */
    RELIGION("Religion", SharedProto.SkillSubtype.RELIGION),

    /** Arcana. */
    ARCANA("Arcana", SharedProto.SkillSubtype.ARCANA),

    /** Alchemy. */
    ALCHEMY("Alchemy", SharedProto.SkillSubtype.ALCHEMY),

    /** Any sub type. */
    ANY_ONE("Any One", SharedProto.SkillSubtype.ANY_ONE);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private SharedProto.SkillSubtype m_proto;

    /** Create the name.
     *
     * @param inName       the name of the value
     * @param inProto      the proto enum value
     */
    private Subtype(String inName, SharedProto.SkillSubtype inProto)
    {
      m_name = constant("skill.subtype", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public SkillSubtype toProto()
    {
      return m_proto;
    }

    /**
     * Convert the given proto to an enum value.
     *
     * @param inProto the proto value to convert
     * @return the corresponding enum value
     */
    public static Subtype fromProto(SharedProto.SkillSubtype inProto)
    {
      for(Subtype subtype : values())
        if(subtype.m_proto == inProto)
          return subtype;

      throw new IllegalArgumentException("cannot convert skill subtype: "
                                         + inProto);
    }
  }

  //........................................................................
  //----- restrictions -----------------------------------------------------

  /** The possible sizes in the game. */
  public enum Restrictions implements EnumSelection.Named
  {
    /** Trained only. */
    TRAINED_ONLY("Trained Only", BaseSkillProto.Restriction.TRAINED_ONLY),

    /** Armor check penalty. */
    ARMOR_CHECK_PENALTY("Armor Check Penalty",
                        BaseSkillProto.Restriction.ARMOR_CHECK_PENALTY),

    /** Armor check penalty. */
    SUBTYPE_ONLY("Subtype Only", BaseSkillProto.Restriction.SUBTYPE_ONLY);

    /** The value's name. */
    private String m_name;

    /** The prot enum value. */
    private BaseSkillProto.Restriction m_proto;

    /** Create the name.
     *
     * @param inName       the name of the value
     * @param inProto      the proto enum value
     */
    private Restrictions(String inName, BaseSkillProto.Restriction inProto)
    {
      m_name = constant("skill.restrictions", inName);
      m_proto = inProto;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /** Get the save as string.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseSkillProto.Restriction getProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static Restrictions fromProto(BaseSkillProto.Restriction inProto)
    {
      for(Restrictions restriction : values())
        if(restriction.m_proto == inProto)
          return restriction;

      throw new IllegalStateException("invalid proto restriction: " + inProto);
    }
  };

  //........................................................................
  //----- modifiers --------------------------------------------------

  /** The possible sizes in the game. */
  public enum SkillModifier implements EnumSelection.Named
  {
    /** The skill is modified by a creatures speed. */
    SPEED("Speed", BaseSkillProto.Modifier.SPEED),

    /** The skill is modified by a creatures size. */
    SIZE("Size", BaseSkillProto.Modifier.SIZE);

    /** The value's name. */
    private String m_name;

    /** The prot enum value. */
    private BaseSkillProto.Modifier m_proto;

    /** Create the name.
     *
     * @param inName      the name of the value
     * @param inProto     the prot enum value
     */
    private SkillModifier(String inName, BaseSkillProto.Modifier inProto)
    {
      m_name = constant("skill.modifier", inName);
      m_proto = inProto;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseSkillProto.Modifier getProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static SkillModifier fromProto(BaseSkillProto.Modifier inProto)
    {
      for(SkillModifier modifier: values())
        if(modifier.m_proto == inProto)
          return modifier;

      throw new IllegalStateException("invalid proto modifier: " + inProto);
    }
  };

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------ BaseSkill -------------------------------

  /**
   * This is the internal, default constructor for an undefined value.
   */
  protected BaseSkill()
  {
    super(TYPE);
  }

  //........................................................................
  //------------------------------ BaseSkill -------------------------------

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base item
   *
   */
  public BaseSkill(String inName)
  {
    super(inName, TYPE);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final BaseType<BaseSkill> TYPE =
    new BaseType<BaseSkill>(BaseSkill.class);

  //----- ability ----------------------------------------------------------

  /** The base ability for this skill. */
  @Key("ability")
  protected EnumSelection<BaseMonster.Ability> m_ability =
    new EnumSelection<BaseMonster.Ability>(BaseMonster.Ability.class);

  static
  {
    addIndex(new Index(Index.Path.ABILITIES, "Abilities", TYPE));
  }

  //........................................................................
  //----- check ------------------------------------------------------------

  /** The check to make. */
  @Key("check")
  protected LongFormattedText m_check = new LongFormattedText();

  //........................................................................
  //----- action -----------------------------------------------------------

  /** The action that can be done. */
  @Key("action")
  protected LongFormattedText m_action = new LongFormattedText();

  //........................................................................
  //----- try again --------------------------------------------------------

  /** Can it be tried again. */
  @Key("try again")
  protected LongFormattedText m_retry = new LongFormattedText();

  //........................................................................
  //----- special ----------------------------------------------------------

  /** The special remarks. */
  @Key("special")
  protected LongFormattedText m_special = new LongFormattedText();

  //........................................................................
  //----- synergy ----------------------------------------------------------

  /** The synergies to other skills. */
  @Key("synergy")
  protected Union m_synergy =
    new Union(new LongFormattedText(), new ValueList<Name>(", ", new Name()));

  //........................................................................
  //----- restriction ------------------------------------------------------

  /** The restrictions. */
  @Key("restriction")
  protected LongFormattedText m_restriction = new LongFormattedText();

  //........................................................................
  //----- untrained --------------------------------------------------------

  /** What can be done untrained. */
  @Key("untrained")
  protected FormattedText m_untrained = new FormattedText();

  //........................................................................
  //----- restrictions -----------------------------------------------------

  /** Restrictions when using the skill. */
  @Key("restrictions")
  protected ValueList<EnumSelection<Restrictions>> m_restrictions =
    new ValueList<EnumSelection<Restrictions>>
    (new EnumSelection<Restrictions>(Restrictions.class), ", ");

  static
  {
    addIndex(new Index(Index.Path.RESTRICTIONS, "Restrictions", TYPE));
  }

  //........................................................................
  //----- modifiers --------------------------------------------------------

  /** A list of special modifiers to recognize. */
  @Key("modifiers")
  protected ValueList<EnumSelection<SkillModifier>> m_modifiers =
    new ValueList<EnumSelection<SkillModifier>>
    (new EnumSelection<SkillModifier>(SkillModifier.class), ", ");

  static
  {
    addIndex(new Index(Index.Path.MODIFIERS, "Modifiers", TYPE));
  }

  //........................................................................
  //----- dc ---------------------------------------------------------------

  /** Various DCs for this skill. */
  @Key("dc")
  protected ValueList<Multiple> m_dcs =
    new ValueList<Multiple>
    (", ", new Multiple(new Multiple.Element(new Number(1, 100), false),
                        new Multiple.Element(new Text(), false)));

  //........................................................................

  static
  {
    extractVariables(BaseSkill.class);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getAbility ------------------------------

  /**
   * Get the index of the skills base ability.
   *
   * @return      the base ability of the skill.
   *
   */
  public BaseMonster.Ability getAbility()
  {
    return m_ability.getSelected();
  }

  //........................................................................
  //--------------------------------- isDM ---------------------------------

  /**
   * Check whether the given user is the DM for this entry.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   *
   */
  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(BaseCharacter.Group.DM);
  }

  //........................................................................

  //----------------------------- isUntrained ------------------------------

  /**
   * Check if the skill can be used untrained.
   *
   * @return      true if it can be used trained only, false else
   *
   */
  public boolean isUntrained()
  {
    for(EnumSelection<Restrictions> value : m_restrictions)
      if(value.getSelected() == Restrictions.TRAINED_ONLY)
        return false;

    return true;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

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

    values.put(Index.Path.ABILITIES, m_ability.group());

    for(EnumSelection<SkillModifier> modifier : m_modifiers)
      values.put(Index.Path.MODIFIERS, modifier.group());

    for(EnumSelection<Restrictions> restriction : m_restrictions)
      values.put(Index.Path.MODIFIERS, restriction.group());

    return values;
  }

  //........................................................................

  @SuppressWarnings("unchecked")
  @Override
  public Message toProto()
  {
    BaseSkillProto.Builder builder = BaseSkillProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_ability.isDefined())
      builder.setAbility(m_ability.getSelected().toProto());

    if(m_check.isDefined())
      builder.setCheck(m_check.get());

    if(m_action.isDefined())
      builder.setAction(m_action.get());

    if(m_retry.isDefined())
      builder.setRetry(m_retry.get());

    if(m_special.isDefined())
      builder.setSpecial(m_special.get());

    if(m_synergy.isDefined())
      if(m_synergy.getIndex() == 0)
        builder.setSynergyText(((LongFormattedText)m_synergy.get()).get());
      else
        for(Name synergy : (ValueList<Name>)m_synergy.get())
          builder.addSynergy(synergy.get());

    if(m_restriction.isDefined())
      builder.setRestrictionText(m_restriction.get());

    if(m_untrained.isDefined())
      builder.setUntrained(m_untrained.get());

    if(m_restrictions.isDefined())
      for(EnumSelection<Restrictions> restriction : m_restrictions)
        builder.addRestriction(restriction.getSelected().getProto());

    if(m_modifiers.isDefined())
      for(EnumSelection<SkillModifier> modifier : m_modifiers)
        builder.addModifier(modifier.getSelected().getProto());

    if(m_dcs.isDefined())
      for(Multiple dc : m_dcs)
        builder.addDc(BaseSkillProto.DC.newBuilder()
                      .setNumber((int)((Number)dc.get(0)).get())
                      .setText(((Text)dc.get(1)).get())
                      .build());

    BaseSkillProto proto = builder.build();
    return proto;
  }

  @Override
  @SuppressWarnings("unchecked")
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
      m_ability =
        m_ability.as(BaseMonster.Ability.fromProto(proto.getAbility()));

    if(proto.hasCheck())
      m_check = m_check.as(proto.getCheck());

    if(proto.hasAction())
      m_action = m_action.as(proto.getAction());

    if(proto.hasRetry())
      m_retry = m_retry.as(proto.getRetry());

    if(proto.hasSpecial())
      m_special = m_special.as(proto.getSpecial());

    if(proto.getSynergyCount() > 0)
    {
      List<Name> synergies = new ArrayList<>();

      m_synergy =
        m_synergy.as(1, ((ValueList<Name>)m_synergy.get(1)).as(synergies));
    }
    else
      if(proto.hasSynergyText())
        m_synergy = m_synergy.as(0, ((LongFormattedText)m_synergy.get(0))
                                 .as(proto.getSynergyText()));

    if(proto.hasRestrictionText())
      m_restriction = m_restriction.as(proto.getRestrictionText());

    if(proto.hasUntrained())
      m_untrained = m_untrained.as(proto.getUntrained());

    if(proto.getRestrictionCount() > 0)
    {
      List<EnumSelection<Restrictions>> restrictions = new ArrayList<>();

      for(BaseSkillProto.Restriction restriction : proto.getRestrictionList())
        restrictions.add(m_restrictions.createElement()
                         .as(Restrictions.fromProto(restriction)));

      m_restrictions = m_restrictions.as(restrictions);
    }

    if(proto.getModifierCount() > 0)
    {
      List<EnumSelection<SkillModifier>> modifiers = new ArrayList<>();

      for(BaseSkillProto.Modifier modifier : proto.getModifierList())
        modifiers.add(m_modifiers.createElement()
                      .as(SkillModifier.fromProto(modifier)));

      m_modifiers = m_modifiers.as(modifiers);
    }

    if(proto.getDcCount() > 0)
    {
      List<Multiple> dcs = new ArrayList<>();

      for(BaseSkillProto.DC dc : proto.getDcList())
      {
        Multiple multiple = m_dcs.createElement();
        multiple = multiple.as(((Number)multiple.get(0)).as(dc.getNumber()),
                               ((Text)multiple.get(1)).as(dc.getText()));
        dcs.add(multiple);
      }

      m_dcs = m_dcs.as(dcs);
    }
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

  //........................................................................

  //------------------------------------------------------------------- test

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

  //........................................................................
}
