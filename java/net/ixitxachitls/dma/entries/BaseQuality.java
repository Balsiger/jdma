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

import com.google.common.collect.Multimap;

// import net.ixitxachitls.output.commands.Command;
// import net.ixitxachitls.output.commands.Divider;
// import net.ixitxachitls.output.commands.Hrule;
// import net.ixitxachitls.output.commands.Linebreak;
// import net.ixitxachitls.output.commands.Script;
// import net.ixitxachitls.output.commands.Table;
// import net.ixitxachitls.output.commands.Textblock;
import net.ixitxachitls.dma.entries.extensions.BaseIncomplete;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Multiple;
//import net.ixitxachitls.dma.values.Parameters;
import net.ixitxachitls.dma.values.Name;
// import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
// import net.ixitxachitls.dma.values.formatters.LinkFormatter;
// import net.ixitxachitls.dma.values.formatters.ValueFormatter;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the basic jDMA base quality.
 *
 * @file          BaseQuality.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class BaseQuality extends BaseEntry
{
  //----------------------------------------------------------------- nested

  //----- effect types -----------------------------------------------------

  /** The possible spell components (cf. PHB 174). */
  public enum EffectType implements EnumSelection.Named, EnumSelection.Short
  {
    /** Extraordinary effects. */
    EXTRAORDINARY("Extraordinary", "Ex"),

    /** Spell like effects. */
    SPELL_LIKE("Spell-like", "Sp"),

    /** Supernatural effects. */
    SUPERNATURAL("Supernatural", "Su");

    /** The value's name. */
    private @Nonnull String m_name;

    /** The value's short name. */
    private @Nonnull String m_short;

    /** Create the effect type.
     *
     * @param inName      the name of the value
     * @param inShort     the short name of the value
     *
     */
    private EffectType(@Nonnull String inName, @Nonnull String inShort)
    {
      m_name = constant("type", inName);
      m_short = constant("type.short", inShort);
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

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }

    /** Get the short name of the value.
     *
     * @return the short name of the value
     *
     */
    public @Nonnull String getShort()
    {
      return m_short;
    }
  };

  //........................................................................
  //----- affects ----------------------------------------------------------

  /** The possible affects in the game. */
  public enum Affects implements EnumSelection.Named, EnumSelection.Short
  {
    /** The fortitude save. */
    FORTITUDE_SAVE("Fortitude Save", "Fort"),

    /** The reflex save. */
    REFLEX_SAVE("Reflex Save", "Ref"),

    /** The will save. */
    WILL_SAVE("Will Save", "Will"),

    /** The skill. */
    SKILL("Skill", "Skill"),

    /** A grapple modifier. */
    GRAPPLE("Grapple", "Grp"),

    /** An initiative modifier. */
    INIT("Initiative", "Init"),

    /** A modifier to the armor class. */
    AC("Armor Class", "AC"),

    /** A modifier to the attack roll. */
    ATTACK("Attack", "Atk"),

    /** A modifier to damage. */
    DAMAGE("Damage", "Dmg"),

    /** A modifier to the hit points. */
    HP("Hit Points", "HP");

    /** The value's name. */
    private @Nonnull String m_name;

    /** The value's short name. */
    private @Nonnull String m_short;

    /** Create the name.
     *
     * @param inName      the name of the value
     * @param inShort     the short name of the value
     *
     */
    private Affects(@Nonnull String inName, @Nonnull String inShort)
    {
      m_name = constant("affects", inName);
      m_short = constant("affects.short", inShort);
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

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }

    /** Get the short name of the value.
     *
     * @return the short name of the value
     *
     */
    public @Nonnull String getShort()
    {
      return m_short;
    }
  };

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //----------------------------- BaseQuality ------------------------------

  /**
   * This is the internal, default constructor for an undefined value.
   *
   */
  protected BaseQuality()
  {
    super(TYPE);
  }

  //........................................................................
  //----------------------------- BaseQuality ------------------------------

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base item
   *
   */
  public BaseQuality(@Nonnull String inName)
  {
    super(inName, TYPE);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final BaseType<BaseQuality> TYPE =
    new BaseType<BaseQuality>(BaseQuality.class, "Base Qualities")
    .withLink("quality", "qualities");

  //----- type -------------------------------------------------------------

  /** The type of the effect. */
  @Key("type")
  protected EnumSelection<EffectType> m_qualityType =
    new EnumSelection<EffectType>(EffectType.class);

  static
  {
    addIndex(new Index(Index.Path.EFFECT_TYPES, "Type", TYPE));
  }

  //........................................................................
  //----- effects ----------------------------------------------------------

  /** The effects of the feat. */
  @Key("effects")
  protected ValueList<Multiple> m_effects =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
      { new Multiple.Element(new EnumSelection<Affects>(Affects.class), false),
        new Multiple.Element(new Name(), true),
        new Multiple.Element(new Modifier(), true, ": ", null),
      }));

  static
  {
    addIndex(new Index(Index.Path.AFFECTS, "Affects", TYPE));
    addIndex(new Index(Index.Path.MODIFIERS, "Modifiers", TYPE));
  }

  //........................................................................
  //----- qualifier --------------------------------------------------------

  /** The name qualifier, if any. */
  @Key("qualifier")
  protected Name m_qualifier = new Name();

  //........................................................................

  static
  {
    extractVariables(BaseQuality.class);
    extractVariables(BaseQuality.class, BaseIncomplete.class);
  }

  //----- special indexes --------------------------------------------------

  static
  {
    addIndex(new Index(Index.Path.WORLDS, "Worlds", TYPE));
    addIndex(new Index(Index.Path.REFERENCES, "References", TYPE));
    addIndex(new Index(Index.Path.EXTENSIONS, "Extensions", TYPE));
  }

  //........................................................................

  //----- printing commands ------------------------------------------------

  /** The command for printing on a page. */
  // public static Command PAGE_COMMAND = new Command(new Object []
  //   {
  //     new Divider("center", "#^world #attachment #+categories #type"),
  //     "${title}",
  //     new Textblock(new Command(new Object []
  //       {
  //         "${description}",
  //         new Linebreak(),
  //         new Hrule(),
  //         "${short description}",
  //       }), "desc"),
  //     new Table("description", "f" + "Illustrations: ".length()
  //               + ":L(desc-label);100:L(desc-text)",
  //               new Command("%effects %qualifier"
  //                           // incomplete
  //                           + "%incomplete "
  //                           // admin
  //                           + "%{+references} %file")),
  //     new Divider("clear", "$scripts"),
  //   });

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getSummary ------------------------------

  /**
   * Get the summary of the quality.
   *
   * @param       inParameters the parameters to parametrize this quality
   *
   * @return      a string with the summary
   *
   */
  // public String getSummary(Parameters inParameters)
  // {
  //   String summary = getShortDescription();

  //   if(!inParameters.isDefined())
  //     return summary;

  //   Map<String, Value> values = inParameters.getLCKeyValues();
  //   summary = computeExpressions(summary, values);

  //   Value notes = values.get("Notes");
  //   if(notes != null)
  //     summary += " (" + notes + ")";

  //   return summary;
  // }

  //........................................................................
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

    values.put(Index.Path.EFFECT_TYPES, m_qualityType.group());
    for(Multiple effect : m_effects)
    {
      values.put(Index.Path.AFFECTS, effect.get(0).toString()
                 + (effect.get(1).isDefined() ? " " + effect.get(1) : ""));
      for(String modifier : ((Modifier)effect.get(2)).getBase())
        values.put(Index.Path.MODIFIERS, modifier);
    }

    return values;
  }

  //........................................................................

  //------------------------------ printCommand ----------------------------

  /**
   * Print the item to the document, in the general section.
   *
   * @param       inDM   true if set for DM, false for player
   * @param       inEditable true if values are editable, false if not
   *
   * @return      the command representing this item in a list
   *
   */
  // public PrintCommand printCommand(boolean inDM, boolean inEditable)
  // {
  //   PrintCommand commands = super.printCommand(inDM, inEditable);

  //   commands.type = "quality";

  //   commands.temp = new ArrayList<Object>();
  //   commands.temp.add(PAGE_COMMAND.transform(new ValueTransformer(commands,
  //                                                                 inDM)));

  //   return commands;
  // }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    // TODO: fix tests
    //----- createBaseQuality() --------------------------------------------

    /** Create a typical base item for testing purposes.
     *
     * @return the newly created base item
     *
     */
    public static AbstractEntry createBaseQuality()
    {
      net.ixitxachitls.input.ParseReader reader =
        new net.ixitxachitls.input.ParseReader
        (new java.io.StringReader(s_text), "test");

      return BaseQuality.read(reader);
    }

    //......................................................................

    //----- text -----------------------------------------------------------

    /** Test text. */
    private static String s_text =
      "#----- Slime ---------------------------------------------------\n"
      + "\n"
      + "base quality Slime = \n"
      + "\n"
      + "  type              Ex;\n"
      + "  worlds            generic;\n"
      + "  references        WTC 17755: 8;\n"
      + "  short description \"Transforms a creature's skin into a clear, "
      + "slimy\n"
      + "                    membrane.\";\n"
      + "  description  \n"
      + "\n"
      + "  \"A blow from an \\Monster{aboleth}'s tentacle can cause a "
      + "terrible\n"
      + "  affliction. A creature hit by a tentacle must succeed on a DC 19 "
      + "Fortitude\n"
      + "  save or begin to transform over the next 1d4+1 minutes, the skin "
      + "gradually\n"
      + "  becoming a clear, slimy membrane. An afflicted creature must "
      + "remain moisted\n"
      + "  with cool, fresh water or take 1d12 points of damage every 10 "
      + "minutes. The\n"
      + "  slime reduces the creature's natural armor bonus by 1 (but never "
      + "to less than\n"
      + "  0). The save DC is Constitution-based.\n"
      + "\n"
      + "  A \\Spell{remove disease} spell cast before the transformation is "
      + "complete\n"
      + "  will restore an afflicted creature to normal. Afterward, however "
      + "only a\n"
      + "  \\Spell{heal} or \\Spell{mass heal} spell can reverse the "
      + "affliction\".\n"
      + "\n"
      + "#...........................................................\n";

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
        "#----- Slime\n"
        + "\n"
        + "base quality Slime =\n"
        + "\n"
        + "  type              Extraordinary;\n"
        + "  worlds            Generic;\n"
        + "  references        WTC 17755: 8;\n"
        + "  description       \"A blow from an \\Monster{aboleth}'s tentacle "
        + "can cause a terrible\n"
        + "                    affliction. A creature hit by a tentacle must "
        + "succeed on a DC 19 Fortitude\n"
        + "                    save or begin to transform over the next 1d4+1 "
        + "minutes, the skin gradually\n"
        + "                    becoming a clear, slimy membrane. An afflicted "
        + "creature must remain moisted\n"
        + "                    with cool, fresh water or take 1d12 points of "
        + "damage every 10 minutes. The\n"
        + "                    slime reduces the creature's natural armor "
        + "bonus by 1 (but never to less than\n"
        + "                    0). The save DC is Constitution-based.\n"
        + "                    A \\Spell{remove disease} spell cast before the "
        + "transformation is complete\n"
        + "                    will restore an afflicted creature to normal. "
        + "Afterward, however only a\n"
        + "                    \\Spell{heal} or \\Spell{mass heal} spell can "
        + "reverse the affliction\";\n"
        + "  short description \"Transforms a creature's skin into a clear, "
        + "slimy membrane.\".\n"
        + "\n"
        + "#.....\n";

      AbstractEntry entry = createBaseQuality();

      //System.out.println("read entry:\n'" + entry + "'");

      assertNotNull("base item should have been read", entry);
      assertEquals("base item name does not match", "Slime",
                   entry.getName());
      assertEquals("base item does not match", result, entry.toString());
    }

    //......................................................................
//     //----- print ----------------------------------------------------------

//     /** Test raw printing. */
//     public void testPrint()
//     {
//       ParseReader reader =
//         new ParseReader(new java.io.StringReader(s_text), "test");

//       AbstractEntry entry = BaseQuality.read(reader, null);

//       m_logger.verify();

//       // title and icons
//       String result = "\\center{"
//         + "\\icon{worlds/Generic.png}{world: Generic}"
//         + "{../index/worlds/\\worduppercase{Generic}.html}{highlight}"
//         + "\\icon{effecttypes/0Extraordinary.png}"
//         + "{type: Ex}"
//         + "{../index/effecttypes/0Extraordinary.html}{highlight}}\n"
//         + "\\divider{main}{\\title{Slime\\linebreak "
//         + "\\tiny{\\link[BaseQualitys/index]{(base effect)}}}\n";

//       // description text
//       result += "\\textblock[desc]{A blow from an \\Monster{aboleth}'s "
//         + "tentacle can cause a terrible affliction. A creature hit by a "
//         + "tentacle must succeed on a DC 19 Fortitude save or begin to "
//       + "transform over the next 1d4+1 minutes, the skin gradually becoming "
//         + "a clear, slimy membrane. An afflicted creature must remain "
//         + "moisted with cool, fresh water or take 1d12 points of damage "
//         + "every 10 minutes. The slime reduces the creature's natural armor "
//         + "bonus by 1 (but never to less than 0). The save DC is "
//         + "Constitution-based.\\par A \\Spell{remove disease} spell cast "
//         + "before the transformation is complete will restore an afflicted "
//         + "creature to normal. Afterward, however only a \\Spell{heal} or "
//         + "\\Spell{mass heal} spell can reverse the affliction}\n";

//       // files
//       result += "\\files{BaseQualitys/Slime}";

//       // description table
//       result += "\\table[description]{f19:L(desc-label);100:L(desc-text)}"
//         + "{null}{null}"
//         + "{\\window{\\bold{Effects:}}{"
//         + Config.get("resource:help/label.effects", (String)null)
//         + "}}{\\color{error}{$undefined$}}"
//         + "{\\window{\\bold{References:}}{"
//         + Config.get("resource:help/label.references", (String)null)
//         + "}}{\\span{unit}{\\link[BaseProducts/WTC 17755]{WTC 17755} p. 8}}"
//         + "{null}{null}"
//         + "\\divider{clear}{}}";

//       // no picture descriptions
//       result += "\\nopictures{\\table{f15:L;100:L}"
//         + "{\\bold{World:}}{\\link[index/worlds/Generic]{Generic}}"
//         + "{\\bold{Type:}}{\\link[index/effecttypes/Extraordinary]"
//         + "{Extraordinary}}}\n";

//       assertEquals("print commands",
//                    result,
//                    entry.getCommand((CampaignData)null,
//                                     PrintType.print).toString());
//     }

//     //......................................................................
//     //----- shortPrint -----------------------------------------------------

//     /** Test short printing. */
//     public void testShortPrint()
//     {
//       ParseReader reader =
//         new ParseReader(new java.io.StringReader(s_text), "test");

//       AbstractEntry entry = BaseQuality.read(reader, null);

//       String result =
//         "Transforms a creature's skin into a clear, slimy membrane.";

//       //System.out.println(entry.getShortPrintCommand().toString());
//       assertEquals("print commands",
//                    result, entry.getCommand((CampaignData)null,
//                                             PrintType.brief).toString());
//     }

//     //......................................................................
  }

  //........................................................................
}
