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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseQualityProto;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Expression;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Parameters;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.conditions.Condition;
import net.ixitxachitls.util.logging.Log;

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

@ParametersAreNonnullByDefault
public class BaseQuality extends BaseEntry
{
  //----------------------------------------------------------------- nested

  //----- effect types -----------------------------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The possible spell components (cf. PHB 174). */
  public enum EffectType implements EnumSelection.Named, EnumSelection.Short
  {
    /** Extraordinary effects. */
    EXTRAORDINARY("Extraordinary", "Ex", BaseQualityProto.Type.EXTRAORDINARY),

    /** Spell like effects. */
    SPELL_LIKE("Spell-like", "Sp", BaseQualityProto.Type.SPELL_LIKE),

    /** Supernatural effects. */
    SUPERNATURAL("Supernatural", "Su", BaseQualityProto.Type.SUPERNATURAL);

    /** The value's name. */
    private String m_name;

    /** The value's short name. */
    private String m_short;

    /** The enum proto value. */
    private BaseQualityProto.Type m_proto;

    /** Create the effect type.
     *
     * @param inName      the name of the value
     * @param inShort     the short name of the value
     * @param inProto     the proto enum value
     */
    private EffectType(String inName, String inShort,
                       BaseQualityProto.Type inProto)
    {
      m_name = constant("type", inName);
      m_short = constant("type.short", inShort);
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

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
    {
      return m_name;
    }

    /** Get the short name of the value.
     *
     * @return the short name of the value
     *
     */
    @Override
    public String getShort()
    {
      return m_short;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseQualityProto.Type getProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static EffectType fromProto(BaseQualityProto.Type inProto)
    {
      for(EffectType type : values())
        if(type.m_proto == inProto)
          return type;

      throw new IllegalStateException("invalid proto type: " + inProto);
    }
  }

  //........................................................................
  //----- affects ----------------------------------------------------------

  /** The possible affects in the game. */
  public enum Affects implements EnumSelection.Named, EnumSelection.Short
  {
    /** The fortitude save. */
    FORTITUDE_SAVE("Fortitude Save", "Fort",
                   BaseQualityProto.Effect.Affects.FORTITUDE_SAVE),

    /** The reflex save. */
    REFLEX_SAVE("Reflex Save", "Ref",
                BaseQualityProto.Effect.Affects.REFLEX_SAVE),

    /** The will save. */
    WILL_SAVE("Will Save", "Will", BaseQualityProto.Effect.Affects.WILL_SAVE),

    /** The skill. */
    SKILL("Skill", "Skill", BaseQualityProto.Effect.Affects.SKILL),

    /** A grapple modifier. */
    GRAPPLE("Grapple", "Grp", BaseQualityProto.Effect.Affects.GRAPPLE),

    /** An initiative modifier. */
    INIT("Initiative", "Init", BaseQualityProto.Effect.Affects.INIT),

    /** A modifier to the armor class. */
    AC("Armor Class", "AC", BaseQualityProto.Effect.Affects.AC),

    /** A modifier to the attack roll. */
    ATTACK("Attack", "Atk", BaseQualityProto.Effect.Affects.ATTACK),

    /** A modifier to damage. */
    DAMAGE("Damage", "Dmg", BaseQualityProto.Effect.Affects.DAMAGE),

    /** A modifier to Speed. */
    SPEED("Speed", "Spd", BaseQualityProto.Effect.Affects.SPEED),

    /** A modifier to the hit points. */
    HP("Hit Points", "HP", BaseQualityProto.Effect.Affects.HP),

    /** A modifier to strength. */
    STRENGTH("Strength", "Str", BaseQualityProto.Effect.Affects.STRENGTH),

    /** A modifier to dexterity. */
    DEXTERITY("Dexterity", "Dex", BaseQualityProto.Effect.Affects.DEXTERITY),

    /** A modifier to constitution. */
    CONSTITUTION("Constitution", "Con",
                 BaseQualityProto.Effect.Affects.CONSTITUTION),

    /** A modifier to intelligence. */
    INTELLIGENCE("Intelligence", "Int",
                 BaseQualityProto.Effect.Affects.INTELLIGENCE),

    /** A modifier to wisdom. */
    WISDOM("Wisdom", "Wis", BaseQualityProto.Effect.Affects.WISDOM),

    /** A modifier to strength. */
    CHARISMA("Charisma", "Cha", BaseQualityProto.Effect.Affects.CHARISMA);

    /** The value's name. */
    private final String m_name;

    /** The value's short name. */
    private final String m_short;

    /** The proto enum value. */
    private final BaseQualityProto.Effect.Affects m_proto;

    /** Create the name.
     *
     * @param inName      the name of the value
     * @param inShort     the short name of the value
     * @param inProto     the prot enum value
     */
    private Affects(String inName, String inShort,
                    BaseQualityProto.Effect.Affects inProto)
    {
      m_name = constant("affects", inName);
      m_short = constant("affects.short", inShort);
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

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
    {
      return m_name;
    }

    /** Get the short name of the value.
     *
     * @return the short name of the value
     *
     */
    @Override
    public String getShort()
    {
      return m_short;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseQualityProto.Effect.Affects getProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static Affects fromProto(BaseQualityProto.Effect.Affects inProto)
    {
      for(Affects affects : values())
        if(affects.m_proto == inProto)
          return affects;

      throw new IllegalStateException("invalid proto affects: " + inProto);
    }
  }

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
  public BaseQuality(String inName)
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
        new Multiple.Element(new Text(), true, " = ", null),
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
  }

  //----- special indexes --------------------------------------------------

  static
  {
    addIndex(new Index(Index.Path.WORLDS, "Worlds", TYPE));
    addIndex(new Index(Index.Path.REFERENCES, "References", TYPE));
    addIndex(new Index(Index.Path.EXTENSIONS, "Extensions", TYPE));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

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
  //------------------------- computeSkillModifier -------------------------

  /**
   * Get a modifier for a skill.
   *
   * @param       inName the name of the skill to modify
   * @param       inParameters the parameters for the skill
   *
   * @return      the modifier, if any
   *
   */
  @SuppressWarnings("unchecked")
  public @Nullable Modifier computeSkillModifier
    (String inName, Parameters inParameters)
  {
    Modifier result = null;

    for(Multiple effect : m_effects)
      if(((EnumSelection<Affects>)effect.get(0)).getSelected() == Affects.SKILL
         && inName.equalsIgnoreCase(effect.get(1).toString()))
        result =
          computeModifierExpression((Modifier)effect.get(2), inParameters)
          .as(result);

    return result;
  }

  /**
   * Compute the modifier base on an expression.
   *
   * @param   inModifier    the modifier to compute from
   * @param   inParameters  parameters to adjust computation
   *
   * @return  the modifier with evaluated expressions
   */
  private @Nullable Modifier computeModifierExpression
    (Modifier inModifier, Parameters inParameters)
  {
    if(inModifier == null)
      return null;

    Modifier next =
      computeModifierExpression(inModifier.getNext(), inParameters);

    if(inModifier.getExpression() instanceof Expression.Expr)
    {
      String computed =
        computeExpressions(((Expression.Expr)inModifier.getExpression())
                           .getText(), inParameters);

      Modifier modifier = inModifier.read(computed);
      if (modifier != null)
        return modifier.as(next);

      return inModifier.as(Integer.valueOf
                           (computed.replace('+', '0')),
                           inModifier.getType(), inModifier.getCondition(),
                           next);
    }

    return inModifier.as(next);
  }

  //........................................................................
  //-------------------------------- collect -------------------------------

  /**
   * Collect a name value.
   *
   * @param       inName          the name of the value to collect
   * @param       ioCombined      the value collected so far (to add to)
   * @param       inDescription   the description why collecting the value
   * @param       inParameters    the parameters for collecting values
   * @param       inCondition     the condition for collecting
   * @param       <T>             the type of value collected
   */
  @SuppressWarnings("unchecked")
  protected <T extends Value<T>> void
               collect(String inName, Combined<T> ioCombined,
                       String inDescription,
                       Parameters inParameters,
                       @Nullable Condition<?> inCondition)
  {
    super.collect(inName, ioCombined);

    for(Multiple multiple : m_effects)
    {
      Affects affects = ((EnumSelection<Affects>)multiple.get(0)).getSelected();
      if(("fortitude save".equals(inName) && affects == Affects.FORTITUDE_SAVE)
         || ("reflex save".equals(inName) && affects == Affects.REFLEX_SAVE)
         || ("will save".equals(inName) && affects == Affects.WILL_SAVE)
         || ("strength".equals(inName) && affects == Affects.STRENGTH)
         || ("dexterity".equals(inName) && affects == Affects.DEXTERITY)
         || ("constitution".equals(inName) && affects == Affects.CONSTITUTION)
         || ("widsom".equals(inName) && affects == Affects.WISDOM)
         || ("intelligence".equals(inName) && affects == Affects.INTELLIGENCE)
         || ("charisma".equals(inName) && affects == Affects.CHARISMA)
         || (affects == Affects.SKILL
             && inName.equals(computeExpressions(multiple.get(1).toString(),
                                                 inParameters)))
         || (affects == Affects.DAMAGE && "damage".equals(inName))
         || (affects == Affects.AC && "armor class".equals(inName))
         || (affects == Affects.SPEED && "speed".equals(inName))
         || (affects == Affects.ATTACK && "attack".equals(inName)))
      {
        Modifier modifier = (Modifier)multiple.get(2);
        if(modifier.isDefined())
        {
          modifier.withCondition(inCondition);
          if(modifier.getExpression() instanceof Expression.Expr)
          {
            String expression =
              computeExpressions(((Expression.Expr)modifier.getExpression())
                                 .getText(), inParameters);

            Modifier computed = modifier.read(expression);
            if(computed != null)
            {
              computed.withCondition(inCondition);
              computed.withCondition(modifier.getCondition());
              ioCombined.addModifier(computed, this, inDescription);
            }
            else
              ioCombined.addModifier
                (modifier.as(Integer.valueOf(expression.replace('+', '0')),
                             modifier.getType(), modifier.getCondition(), null),
                  this, null);
          }
          else
            ioCombined.addModifier(modifier, this, null);
        }

        Text valueText = (Text)multiple.get(3);
        if(valueText.isDefined())
        {
          List<T> values = ioCombined.valuesOnly();
          if(!values.isEmpty())
          {
            String text = computeExpressions(valueText.get(), inParameters);
            ioCombined.addValue(values.get(0).read(text), this, inDescription);
          }
        }
      }
    }
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
    if(inUser == null)
      return false;

    return inUser.hasAccess(BaseCharacter.Group.DM);
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

  @SuppressWarnings("unchecked")
  @Override
  public Message toProto()
  {
    BaseQualityProto.Builder builder = BaseQualityProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_qualityType.isDefined())
      builder.setType(m_qualityType.getSelected().getProto());

    if(m_effects.isDefined())
      for(Multiple effect : m_effects)
      {
        BaseQualityProto.Effect.Builder effectBuilder =
          BaseQualityProto.Effect.newBuilder();

        effectBuilder.setAffects
          (((EnumSelection<Affects>)effect.get(0)).getSelected().getProto());
        if(effect.get(1).isDefined())
          effectBuilder.setReference(((Name)effect.get(1)).get());
        if(effect.get(2).isDefined())
          effectBuilder.setModifier(((Modifier)effect.get(2)).toProto());
        if(effect.get(3).isDefined())
          effectBuilder.setText(((Text)effect.get(3)).get());

        builder.addEffect(effectBuilder.build());
      }

    if(m_qualifier.isDefined())
      builder.setQualifier(m_qualifier.get());

    BaseQualityProto proto = builder.build();
    return proto;
  }

  @Override
  @SuppressWarnings("unchecked")
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
      m_qualityType = m_qualityType.as(EffectType.fromProto(proto.getType()));

    if(proto.getEffectCount() > 0)
    {
      List<Multiple> effects = new ArrayList<>();
      for(BaseQualityProto.Effect effect : proto.getEffectList())
      {
        Multiple multiple = m_effects.createElement();
        multiple = multiple.as(((EnumSelection<Affects>)multiple.get(0))
                               .as(Affects.fromProto(effect.getAffects())),
                               effect.hasReference()
                               ? ((Name)multiple.get(1))
                                 .as(effect.getReference())
                               : multiple.get(1),
                               effect.hasModifier()
                               ? ((Modifier)multiple.get(2))
                                 .fromProto(effect.getModifier())
                               : multiple.get(2),
                               effect.hasText()
                               ? ((Text)multiple.get(3))
                                 .as(effect.getText())
                               : multiple.get(3));

        effects.add(multiple);
      }

      if(proto.hasQualifier())
        m_qualifier = m_qualifier.as(proto.getQualifier());

      m_effects = m_effects.as(effects);
    }
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(BaseQualityProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

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
      try (net.ixitxachitls.input.ParseReader reader =
        new net.ixitxachitls.input.ParseReader
        (new java.io.StringReader(s_text), "test"))
      {
        return null; //BaseQuality.read(reader);
      }
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
        + "slimy membrane.\";\n"
        + "  name              Slime.\n"
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
