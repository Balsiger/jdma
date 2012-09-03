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

//import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.FormattedText;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
//import net.ixitxachitls.dma.values.Selection;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.input.ParseReader;
//import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the basic jDMA base spell.
 *
 * @file          BaseFeat.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

public class BaseFeat extends BaseEntry
{
  //----------------------------------------------------------------- nested

  //----- type -------------------------------------------------------------

  /** The possible areas to affect (cf. PHB 175). */
  public enum Type implements EnumSelection.Named
  {
    /** A general feat. */
    GENERAL("General"),

    /** An item creation feat. */
    ITEM_CREATION("Item Creation"),

    /** A metamagic feat. */
    METAMAGIC("Metamagic"),

    /** A regional feat. */
    REGIONA("Region"),

    /** A special feat. */
    SPECIAL("Special"),

    /** A fighter feat. */
    FIGHTER("Fighter");

    /** The value's name. */
    private @Nonnull String m_name;

    /** Create the name.
     *
     * @param inName     the name of the value
     *
     */
    private Type(@Nonnull String inName)
    {
      m_name = constant("feat.type", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public @Nonnull String toString()
    {
      return m_name;
    }
  };

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------- BaseFeat -------------------------------

  /**
    * This is the internal, default constructor for an undefined value.
    *
    */
  protected BaseFeat()
  {
    super(TYPE);
  }

  //........................................................................
  //------------------------------- BaseFeat -------------------------------

  /**
    * This is the normal constructor.
    *
    * @param       inName the name of the base item
    *
    */
  public BaseFeat(@Nonnull String inName)
  {
    super(inName, TYPE);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final BaseType<BaseFeat> TYPE =
    new BaseType<BaseFeat>(BaseFeat.class);

  //----- entry values -----------------------------------------------------

  //----- type -------------------------------------------------------------

  /** The type of the feat. */
  @Key("type")
  protected EnumSelection<Type> m_featType =
    new EnumSelection<Type>(Type.class);

  //........................................................................
  //----- benefit ----------------------------------------------------------

  /** The benefits. */
  @Key("benefit")
  protected FormattedText m_benefit = new FormattedText();

  //........................................................................
  //----- special ----------------------------------------------------------

  /** The special remarks. */
  @Key("special")
  protected FormattedText m_special = new FormattedText();

  //........................................................................
  //----- normal -----------------------------------------------------------

  /** The special remarks. */
  @Key("normal")
  protected FormattedText m_normal = new FormattedText();

  //........................................................................
  //----- prerequisites ----------------------------------------------------

  /** The prerequisites. */
  @Key("prerequisites")
  protected FormattedText m_prerequisites = new FormattedText();

  //........................................................................
  //----- effects ----------------------------------------------------------

  /** The effects of the feat. */
  @Key("effects")
  protected ValueList<Multiple> m_effects =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
      { new Multiple.Element(new EnumSelection<BaseQuality.Affects>
                             (BaseQuality.Affects.class), false),
        new Multiple.Element(new Name(), true),
        new Multiple.Element(new net.ixitxachitls.dma.values.Modifier(), true),
      }));

  //........................................................................

  //........................................................................

  static
  {
    extractVariables(BaseFeat.class);
  }

  //----- commands ---------------------------------------------------------

  //----- print ------------------------------------------------------------

  /** The formatters to use for basic printing. */
  static
  {
    // TODO: reimplement
//     addCommand
//       (BaseFeat.class, PrintType.print,
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
//                  { "feattypes/",
//                    new ValueCommand(FEAT_TYPE),
//                    ".png",
//                  }),
//                         new Command(new Object []
//                           { "type: ",
//                             new AccessorCommand
//                             (new AccessorCommand.Accessor()
//                               {
//                                 public String get(ValueGroup inEntry)
//                                 {
//                                   return
//                                     ((BaseFeat)inEntry).m_type.toString();
//                                 }
//                               }),
//                           }),
//                         new Command(new Object []
//                           { "../index/feattypes/",
//                             new AccessorCommand
//                             (new AccessorCommand.Accessor()
//                               {
//                                 public String get(ValueGroup inEntry)
//                                 {
//                                   return
//                                     ((BaseFeat)inEntry).m_type.toString();
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
//                          return ((BaseFeat)inEntry).getName();
//                        }
//                      }),
//                    new Linebreak(),
//                    new Tiny(new Link("(base feat)", "BaseFeats/index")),
//                  })),
//                "\n",
//                 new VariableCommand(PropertyKey.getKey("description")),
//                "\n",
//                new net.ixitxachitls.output.commands.
//                Files(new AccessorCommand(new AccessorCommand.Accessor()
//                  {
//                    public String get(ValueGroup inEntry)
//                    {
//                      return "BaseFeats/" + ((BaseFeat)inEntry).getName();
//                    }
//                  })),
//                new Table("description", "f" + "Short Description: ".length()
//                          + ":L(desc-label);100:L(desc-text)", new Object []
//                  {
//                    BaseEntry.s_synonymLabel,
//                    BaseEntry.s_synonymCmd,
//                    new Window(new Bold("Benefit:"),
//                               Config.get
//                               ("resource:help/label.benefit",
//                              "What the feat enables the character ('you' in "
//                              + "the feat description) to do. If a character "
//                                + "has the same feat more than once, its "
//                                + "benefits do not stack unless indicated "
//                                + "otherwise in the description. In general, "
//                                + "having a feat twice is the same as having "
//                                + "it once.")),
//                    new VariableCommand(BENEFIT),
//                    new IfDefinedCommand
//                    (SPECIAL,
//                     new Window(new Bold("Special:"),
//                                Config.get
//                                ("resource:help/label.special",
//                               "Additional facts about the feat that may be "
//                               + "helpful when you decide whether to acquire "
//                                 + "the feat.")), null),
//                    new IfDefinedCommand
//                    (SPECIAL, new VariableCommand(SPECIAL), null),
//                    new IfDefinedCommand
//                    (PREREQUISITES,
//                     new Window(new Bold("Prerequisites:"),
//                                Config.get
//                                ("resource:help/label.prerequisites",
//                                 "A minimum ability score, another feat or "
//                                 + "feats, a minimum base attack bonus, a "
//                                 + "minimum number of ranks in one or more "
//                                + "skills, or a class level that a character "
//                                 + "must have in order to acquire this feat. "
//                                 + "This entry is absent if a feat has no "
//                                 + "prerequisite. A feat may have more than "
//                                 + "one prerequisite.")), null),
//                    new IfDefinedCommand
//                    (PREREQUISITES, new VariableCommand(PREREQUISITES), null),
//                    new Window(new Bold("Short Description:"),
//                               Config.get("resource:help/label.short.desc",
//                                          "This is the short description of "
//                                          + "the entry.")),
//                    new VariableCommand(PropertyKey.getKey
//                                        ("short description")),
//                    new IfDefinedCommand
//                    (NORMAL,
//                     new Window(new Bold("Normal:"),
//                                Config.get
//                                ("resource:help/label.normal",
//                               "What a character who does not have this feat "
//                               + "is limited to or restricted from doing. If "
//                                 + "not having the feat causes no particular "
//                                 + "drawback, this entry is absent.")), null),
//                    new IfDefinedCommand
//                    (NORMAL, new VariableCommand(NORMAL), null),
//                    new Window(new Bold("Effects:"),
//                                Config.get
//                                ("resource:help/label.effects",
//                                 "These are the game effects the feat has.")),
//                    new VariableCommand(EFFECTS),
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
//           new Nopictures(new Table(new int [] { -"Illustrations: ".length(),
//                                                  100 },
//                                     new Document.Alignment []
//              { Document.Alignment.left, Document.Alignment.left }, null,
//                                     new Object []
//              {
//                new Bold("World:"),
//                new VariableCommand(PropertyKey.getKey("world")),
//                new Bold("Type:"),
//                new VariableCommand(FEAT_TYPE),
//              })),
//            "\n",
//          }));
  }

  //........................................................................
  //----- brief ------------------------------------------------------------

  /** The formatters to use for basic printing. */
  static
  {
//     addCommand
//       (BaseFeat.class, PrintType.brief,
//        new Command(new Object []
//          {
//            new VariableCommand(PropertyKey.getKey("short description")),
//            new IfDefinedCommand(SPECIAL,
//                                 new Command(new Object []
//                                   {
//                                     " [",
//                                     new VariableCommand(SPECIAL),
//                                     " ]",
//                                   }), new Command("")),
//            new Italic(new Command(new Object []
//              { " (cf. ",
//                new VariableCommand(PropertyKey.getKey("references")),
//                ")",
//              })),
//          }));
  }

  //........................................................................
  //----- reference --------------------------------------------------------

  /** Command for printing a reference. */
  static
  {
//     addCommand
//       (BaseFeat.class, PrintType.reference,
//        new Command(new Object []
//          {
//            new Label("Base Spell"),
//            new Link(new AccessorCommand(new AccessorCommand.Accessor()
//              {
//                public String get(ValueGroup inEntry)
//                {
//                  return ((BaseFeat)inEntry).getName();
//                }
//              }), new Command(new Object []
//                { "BaseFeats/",
//                  new AccessorCommand(new AccessorCommand.Accessor()
//                    {
//                      public String get(ValueGroup inEntry)
//                      {
//                        return ((BaseFeat)inEntry).getName();
//                      }
//                    }),
//                })),
//            new VariableCommand(PropertyKey.getKey("short description")),
//            new VariableCommand(FEAT_TYPE, true, true),
//            new VariableCommand(PropertyKey.getKey("world")),
//          }));

//     addCommand(BaseFeat.class, PrintType.referenceFormat,
//                new Command("1:L(icon);"
//                            + "5:L(name)[Name];"
//                            + "50:L(short)[Short Description];"
//                            + "5:L(type)[Type];"
//                            + "5:L(world)[World];"));
  }

  //........................................................................

  //........................................................................
  //----- indices ----------------------------------------------------------

  static
  {
    //----- types ----------------------------------------------------------

//     s_indices.add
//       (new IndexInfo
//        (Config.get("resource:html/title.feattypes",
//                    "Types"),
//         Config.get("resource:html/dir.feattypes",
//                    "feattypes"), "Feat", true,
//         new Filter<AbstractEntry>()
//        {
//          public boolean accept(AbstractEntry inEntry)
//            {
//              return inEntry instanceof BaseFeat;
//            }
//        },
//         new Identificator<ValueGroup>()
//        {
//          public String []id(ValueGroup inEntry)
//            {
//              return new String []
//              { GROUP_SELECT.group(((BaseFeat)inEntry).m_type).toString(), };
//            }
//        },
//         new Command(new Object []
//           {
// //             CMD_LABEL,
// //             CMD_NAME,
// //             new VariableCommand(PropertyKey.getKey("short description")),
// //             new VariableCommand(PropertyKey.getKey("world")),
//           }),
//         "1:L(icon);1:L(name)[Name];5:L(description)[Description];"
//         + "1:L(world)[World]", null));

    //......................................................................
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- createBaseFeat() ----------------------------------------------

    /** Create a typical base item for testing purposes.
     *
     * @return the newly created base item
     *
     */
    public static AbstractEntry createBaseFeat()
    {
      ParseReader reader =
        new ParseReader(new java.io.StringReader(s_text), "test");

      return BaseFeat.read(reader);
    }

    //......................................................................

    //----- text -----------------------------------------------------------

    /** Test text. */
    private static String s_text =
      "#----- Acrobatic [General] --------------------------------------\n"
      + "\n"
      + "base feat Acrobatic =\n"
      + "\n"
      + "  type              General;\n"
      + "  world             generic;\n"
      + "  references        \"WTC 17524\" 89;\n"
      + "  short description \"+2 bonus on Jump and Tumble checks\";\n"
      + "  benefit           \"You get a +2 bonus on all Jump checks and "
      + "Tumble checks.\";\n"
      + "  description\n"
      + "\n"
      + "  \"You have excellent body awareness and coordination.\".\n"
      + "\n"
      + "#..............................................................\n"
      + "\n";

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
      "#----- Acrobatic [General] --------------------------------------\n"
        + "\n"
        + "base feat Acrobatic =\n"
        + "\n"
        + "  type              General;\n"
        + "  benefit           \"You get a +2 bonus on all Jump checks and "
        + "Tumble checks.\";\n"
        + "  world             Generic;\n"
        + "  references        \"WTC 17524\" 89;\n"
        + "  short description \"+2 bonus on Jump and Tumble checks\";\n"
        + "  description       \"You have excellent body awareness and "
        + "coordination.\".\n"
        + "\n"
        + "#..............................................................\n";

      AbstractEntry entry = createBaseFeat();

      //System.out.println("read entry:\n'" + entry + "'");

      assertNotNull("base item should have been read", entry);
      assertEquals("base item name does not match", "Acrobatic",
                   entry.getName());
      assertEquals("base item does not match", result, entry.toString());
    }

    //......................................................................
    //----- print ----------------------------------------------------------

    /** Test raw printing. */
    // public void testPrint()
    // {
    //   ParseReader reader =
    //     new ParseReader(new java.io.StringReader(s_text), "test");

    //   AbstractEntry entry = BaseFeat.read(reader);

    //   m_logger.verify();

    //   // title and icons
    //   String result = "\\center{"
    //     + "\\icon{worlds/Generic.png}{world: Generic}"
    //     + "{../index/worlds/\\worduppercase{Generic}.html}{highlight}"
    //     + "\\icon{feattypes/General.png}"
    //     + "{type: General}"
    //     + "{../index/feattypes/General.html}{highlight}}\n"
    //     + "\\divider{main}{\\title{Acrobatic\\linebreak "
    //     + "\\tiny{\\link[BaseFeats/index]{(base feat)}}}\n";

    //   // description text
    //   result += "\\textblock[desc]{You have excellent body awareness and "
    //     + "coordination.}\n";

    //   // files
    //   result += "\\files{BaseFeats/Acrobatic}";

    //   // description table
    //   result += "\\table[description]{f19:L(desc-label);100:L(desc-text)}"
    //     + "{null}{null}"
    //     + "{\\window{\\bold{Benefit:}}{"
    //     + Config.get("resource:help/label.benefit", (String)null)
    //     + "}}{You get a +2 bonus on all Jump checks and Tumble checks.}"
    //     + "{null}{null}"
    //     + "{null}{null}"
    //     + "{\\window{\\bold{Short Description:}}"
    //     + "{This is the short description of the entry.}}"
    //     + "{+2 bonus on Jump and Tumble checks}"
    //     + "{null}{null}"
    //     + "{\\window{\\bold{Effects:}}{"
    //     + Config.get("resource:help/label.effects", (String)null)
    //     + "}}{\\color{error}{$undefined$}}"
    //     + "{\\window{\\bold{References:}}{"
    //     + Config.get("resource:help/label.references", (String)null)
    //     + "}}{\\span{unit}{\\link[BaseProducts/WTC 17524]{WTC 17524} p. 89}}"
    //     + "{null}{null}"
    //     + "\\divider{clear}{}}";

    //   // no picture descriptions
    //   result += "\\nopictures{\\table{f15:L;100:L}"
    //     + "{\\bold{World:}}{\\link[index/worlds/Generic]{Generic}}"
    //     + "{\\bold{Type:}}{General}}\n";

    //   assertEquals("print commands",
    //                result,
    //                entry.getPrintCommand(false));
    // }

    //......................................................................
    //----- shortPrint -----------------------------------------------------

    /** Test short printing. */
    // public void testShortPrint()
    // {
    //   ParseReader reader =
    //     new ParseReader(new java.io.StringReader(s_text), "test");

    //   AbstractEntry entry = BaseFeat.read(reader);

    //   String result =
    //     "+2 bonus on Jump and Tumble checks"
    //     + "\\italic{ (cf. \\span{unit}{\\link[BaseProducts/WTC 17524]"
    //     + "{WTC 17524} p. 89})}";

    //   //System.out.println(entry.getShortPrintCommand().toString());
    //   assertEquals("print commands",
    //                result, entry.getPrintCommand(false));
    // }

    //......................................................................
  }

  //........................................................................
}
