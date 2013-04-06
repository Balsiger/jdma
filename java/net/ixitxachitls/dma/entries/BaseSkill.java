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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

public class BaseSkill extends BaseEntry
{
  //----------------------------------------------------------------- nested

  //----- subtype ----------------------------------------------------------

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
    private @Nonnull String m_name;

    /** Create the name.
     *
     * @param inName       the name of the value
     *
     */
    private Subtype(@Nonnull String inName)
    {
      m_name = constant("skill.subtype", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the save as string.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
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
    private @Nonnull String m_name;

    /** Create the name.
     *
     * @param inName       the name of the value
     *
     */
    private Restrictions(@Nonnull String inName)
    {
      m_name = constant("skill.restrictions", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the save as string.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
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
  public BaseSkill(@Nonnull String inName)
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

  //----- commands ---------------------------------------------------------

  //----- print ------------------------------------------------------------

  /** The formatters to use for basic printing. */
  static
  {
    // TODO: reimplement
//     addCommand
//       (BaseSkill.class, PrintType.print,
//        new Command(new Object []
//          {
//            new Center(new Command(new Object []
//              {
//                new Icon(new Command(new Object []
//                  { "worlds/",
//                    new ValueCommand(PropertyKey.getKey("world")),
//                    ".png",
//                  }),
//                         new Command(new Object []
//                           { "world: ",
//                             new ValueCommand(PropertyKey.getKey("world")),
//                           }),
//                         new Command(new Object []
//                           { "../index/worlds/",
//                             new WordUpperCase
//                             (new ValueCommand(PropertyKey.getKey("world"))),
//                             ".html",
//                           }), true),
//                new Icon(new Command(new Object []
//                  { "abilities/",
//                    new ValueCommand(ABILITY),
//                    ".png",
//                  }),
//                         new Command(new Object []
//                           { "ability: ",
//                             new AccessorCommand
//                             (new AccessorCommand.Accessor()
//                               {
//                                 public String get(ValueGroup inEntry)
//                                 {
//                                   return
//                                    ((BaseSkill)inEntry).m_ability.toString();
//                                 }
//                               }),
//                           }),
//                         new Command(new Object []
//                           { "../index/abilities/",
//                             new AccessorCommand
//                             (new AccessorCommand.Accessor()
//                               {
//                                 public String get(ValueGroup inEntry)
//                                 {
//                                   return
//                                   ((BaseSkill)inEntry).m_ability.toString();
//                                 }
//                               }),
//                             ".html",
//                           }), true),
//                // attachments
//                new AttachmentCommand(false),
//              })),
//            "\n",
//            new Divider("main", new Command(new Object []
//              {
//                new Title(new Command(new Object []
//                  {
//                    new AccessorCommand(new AccessorCommand.Accessor()
//                      {
//                        public String get(ValueGroup inEntry)
//                        {
//                          return ((BaseSkill)inEntry).getName();
//                        }
//                      }),
//                    new Linebreak(),
//                    new Tiny(new Link("(base skill)", "BaseSkills/index")),
//                  })),
//                "\n",
//                 new VariableCommand(PropertyKey.getKey("description")),
//                "\n",
//                new net.ixitxachitls.output.commands.
//                Files(new AccessorCommand(new AccessorCommand.Accessor()
//                  {
//                    public String get(ValueGroup inEntry)
//                    {
//                      return "BaseSkills/" + ((BaseSkill)inEntry).getName();
//                    }
//                  })),
//                new Table("description", "f" + "Short Description: ".length()
//                          + ":L(desc-label);100:L(desc-text)", new Object []
//                  {
//                    BaseEntry.s_synonymLabel,
//                    BaseEntry.s_synonymCmd,
//                    new Window(new Bold("Check:"),
//                               Config.get
//                               ("resource:help/label.check",
//                                "What a character ('you' in the skill "
//                                 + "description) can do with a successful "
//                                 + "skill check and the check's DC.")),
//                    new VariableCommand(CHECK),
//                    new Window(new Bold("Action:"),
//                               Config.get
//                               ("resource:help/label.action",
//                              "The type of action using the skill requires, "
//                                 + "or the amount of time required for a "
//                                 + "check.")),
//                    new VariableCommand(ACTION),
//                    new IfDefinedCommand
//                    (RETRY,
//                     new Window(new Bold("Try Again:"),
//                                Config.get
//                                ("resource:help/label.retry",
//                                 "Any conditions that apply to successive "
//                                  + "attempts to use the skill successfully. "
//                                + "If the skill doesn't allow you to attempt "
//                                  + "the same task more than once, or if "
//                                + "failure carries an inherent penalty (such "
//                                 + "as with the Climb skill), you can't take "
//                                  + "20. If this paragraph is omitted, the "
//                                  + "skill can be retried without any "
//                                  + "inherent penalty, other than the "
//                                  + "additional time required.")), null),
//                    new IfDefinedCommand
//                    (RETRY, new VariableCommand(RETRY), null),
//                    new IfDefinedCommand
//                    (SPECIAL,
//                     new Window(new Bold("Special:"),
//                                Config.get
//                                ("resource:help/label.skill.special",
//                                 "Any extra facts that apply to the skill, "
//                                + "such as special effects deriving from its "
//                                  + "use or bonuses that certain characters "
//                                  + "receive because of class, feat choices, "
//                                  + "or race.")), null),
//                    new IfDefinedCommand
//                    (SPECIAL, new VariableCommand(SPECIAL), null),
//                    new IfDefinedCommand
//                    (SYNERGY,
//                     new Window(new Bold("Synergy:"),
//                                Config.get
//                                ("resource:help/label.synergy",
//                               "Some skills grant a bonus to the use of one "
//                                  + "or more other skills because of a "
//                                  + "synergistic effect. This entry, when "
//                                  + "present, indicates what bonuses this "
//                                  + "skill may grant or receive because of "
//                              + "such synergies. See Table 4â5 for a "
//                                  + "complete list of bonuses granted by "
//                                  + "synergy between skills (or between a "
//                                  + "skill and a class feature).")), null),
//                    new IfDefinedCommand
//                    (SYNERGY, new VariableCommand(SYNERGY), null),
//                    new IfDefinedCommand
//                    (RESTRICTION,
//                     new Window(new Bold("Restriction:"),
//                                Config.get
//                                ("resource:help/label.restriction",
//                                 "The full utility of certain skills is "
//                                  + "restricted to characters of certain "
//                                  + "classes or characters who possess "
//                                  + "certain feats.\n"
//                                  + "This entry indicates whether any such "
//                                  + "restrictions exist for the skill.")),
//                      null),
//                    new IfDefinedCommand
//                    (RESTRICTION, new VariableCommand(RESTRICTION), null),
//                    new IfDefinedCommand
//                    (UNTRAINED,
//                     new Window(new Bold("Untrained:"),
//                                Config.get
//                                ("resource:help/label.untrained",
//                                 "This entry indicates what a character "
//                                + "without at least 1 rank in the skill can "
//                                + "do with it. If this entry doesn't appear, "
//                                  + "it means that the skill functions "
//                                  + "normally for untrained characters (if "
//                                  + "it can be used untrained) or that an "
//                                 + "untrained character can't attempt checks "
//                                  + "with this skill (for skills that are "
//                                  + "designated as 'Trained Only').")), null),
//                    new IfDefinedCommand
//                    (UNTRAINED, new VariableCommand(UNTRAINED), null),
//                    new Window(new Bold("Restrictions:"),
//                               Config.get
//                               ("resource:help/label.restrictions",
//                                "These are possible restrictions when trying "
//                                + "to use the skill.")),
//                    new VariableCommand(RESTRICTIONS),
//                    new Window(new Bold("Modifiers:"),
//                             Config.get("resource:help/label.skill.modifiers",
//                                          "The modifiers, i.e. other values "
//                                          + "that might influence this "
//                                          + "skill.")),
//                    new VariableCommand(MODIFIERS),
//                    new Window(new Bold("DCs:"),
//                               Config.get("resource:help/label.skill.dc",
//                                          "These are the various difficulty "
//                                          + "classes to achieve to get a "
//                                        + "positive result from the skill.")),
//                    new VariableCommand(DC),
//                    new Window(new Bold("Short Description:"),
//                               Config.get("resource:help/label.short.desc",
//                                          "This is the short description of "
//                                          + "the entry.")),
//                    new VariableCommand(PropertyKey.getKey
//                                        ("short description")),
//                    new Skip(new AttachmentCommand(true)),
//                    new Window(new Bold("References:"),
//                               Config.get
//                               ("resource:help/label.references",
//                                "These are the products that mention "
//                                + "this entry. These references were used "
//                                + "to create the statistics for this "
//                                + "entry.")),
//                    new VariableCommand(PropertyKey.getKey("references")),
//                    BaseEntry.s_errorLabel,
//                    BaseEntry.s_errorCmd,
//                  }),
//                new Divider("clear", ""),
//              })),
//          new Nopictures(new Table(new int [] { -"Illustrations: ".length(),
//                                                  100 },
//                                     new Document.Alignment []
//              { Document.Alignment.left, Document.Alignment.left }, null,
//                                     new Object []
//              {
//                new Bold("World:"),
//                new VariableCommand(PropertyKey.getKey("world")),
//                new Bold("Ability:"),
//                new VariableCommand(ABILITY),
//              })),
//            "\n",
//          }));
  }

  //........................................................................
  //----- short ------------------------------------------------------------

  /** The commands for short printing. */
  static
  {
//    addCommand
//       (BaseSkill.class, PrintType.brief,
//        new Command(new Object []
//          {
//            new VariableCommand(PropertyKey.getKey("short description")),
//          }));
  }

  //........................................................................
  //----- reference --------------------------------------------------------

  /** Command for printing a reference. */
  static
  {
//     addCommand
//       (BaseSkill.class, PrintType.reference,
//        new Command(new Object []
//          {
//            new Label("Base Spell"),
//            new Link(new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                public String get(ValueGroup inEntry)
//                {
//                  return ((BaseSkill)inEntry).getName();
//                }
//              }), new Command(new Object []
//                { "BaseSkills/",
//                  new AccessorCommand(new AccessorCommand.Accessor()
//                    {
//                      public String get(ValueGroup inEntry)
//                      {
//                        return ((BaseSkill)inEntry).getName();
//                      }
//                    }),
//                })),
//            new VariableCommand(PropertyKey.getKey("short description")),
//            new VariableCommand(ABILITY, true, true),
//            new VariableCommand(PropertyKey.getKey("world")),
//          }));

//     addCommand(BaseSkill.class, PrintType.referenceFormat,
//                new Command("1:L(icon);"
//                            + "5:L(name)[Name];"
//                            + "50:L(short)[Short Description];"
//                            + "5:L(ability)[Ability];"
//                            + "5:L(world)[World];"));
  }

  //........................................................................

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
      ParseReader reader =
        new ParseReader(new java.io.StringReader(s_text), "test");

      return BaseSkill.read(reader);
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
