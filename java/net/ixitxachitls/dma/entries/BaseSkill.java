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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.indexes.Index;
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
  public enum Subtype implements EnumSelection.Named
  {
    /** Drow religion. */
    DROW_RELIGION("Drow Religion"),

    /** Religion. */
    RELIGION("Religion"),

    /** Arcana. */
    ARCANA("Arcana"),

    /** Alchemy. */
    ALCHEMY("Alchemy"),

    /** Any sub type. */
    ANY_ONE("Any One");

    /** The value's name. */
    private String m_name;

    /** Create the name.
     *
     * @param inName       the name of the value
     *
     */
    private Subtype(String inName)
    {
      m_name = constant("skill.subtype", inName);
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
  };

  //........................................................................
  //----- restrictions -----------------------------------------------------

  /** The possible sizes in the game. */
  public enum Restrictions implements EnumSelection.Named
  {
    /** Trained only. */
    TRAINED_ONLY("Trained Only"),

    /** Armor check penalty. */
    ARMOR_CHECK_PENALTY("Armor Check Penalty"),

    /** Armor check penalty. */
    SUBTYPE_ONLY("Subtype Only");

    /** The value's name. */
    private String m_name;

    /** Create the name.
     *
     * @param inName       the name of the value
     *
     */
    private Restrictions(String inName)
    {
      m_name = constant("skill.restrictions", inName);
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
  };

  //........................................................................
  //----- modifiers --------------------------------------------------

  /** The possible sizes in the game. */
  public enum SkillModifier implements EnumSelection.Named
  {
    /** The skill is modified by a creatures speed. */
    SPEED("Speed"),

    /** The skill is modified by a creatures size. */
    SIZE("Size");

    /** The value's name. */
    private String m_name;

    /** Create the name.
     *
     * @param inName      the name of the value
      *
     */
    private SkillModifier(String inName)
    {
      m_name      = constant("skill.modifier", inName);
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
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    if (inUser == null)
      return false;

    return inUser.hasAccess(BaseCharacter.Group.DM);
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
        return BaseSkill.read(reader);
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
