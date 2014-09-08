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
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseQualityProto;
import net.ixitxachitls.dma.values.AbilityModifier;
import net.ixitxachitls.dma.values.ExpressionValue;
import net.ixitxachitls.dma.values.NewModifier;
import net.ixitxachitls.dma.values.Speed;
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
    new BaseType<BaseQuality>(BaseQuality.class, "Base Qualities")
    .withLink("quality", "qualities");

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
  private Optional<NewModifier> m_reflexModifier = Optional.absent();

  /** Will modifier.*/
  private Optional<NewModifier> m_willModifier = Optional.absent();

  /** Fortitude modifier.*/
  private Optional<NewModifier> m_fortitudeModifier = Optional.absent();

  public EffectType getQualityType()
  {
    return m_qualityType;
  }

  public List<Effect> getEffects()
  {
    return Collections.unmodifiableList(m_effects);
  }

  public Optional<String> getQualifier()
  {
    return m_qualifier;
  }

  public List<String> getAffectNames()
  {
    return Affects.names();
  }

  public Optional<ExpressionValue<Speed>> getSpeed()
  {
    return m_speed;
  }

  public List<AbilityModifier> getAbilityModifiers()
  {
    return Collections.unmodifiableList(m_abilityModifiers);
  }

  public List<Immunity> getImmunities()
  {
    return Collections.unmodifiableList(m_immunities);
  }

  public Optional<NewModifier> getReflexModifier()
  {
    return m_reflexModifier;
  }

  public Optional<NewModifier> getWillModifier()
  {
    return m_willModifier;
  }

  public Optional<NewModifier> getFortitudeModifier()
  {
    return m_fortitudeModifier;
  }

  @Override
  public void set(Values inValues)
  {
    super.set(inValues);

    m_qualityType = inValues.use("quality_type", m_qualityType,
                                 EffectType.PARSER);
    m_effects = inValues.use("effect", m_effects, Effect.PARSER,
                             "affects", "name", "modifier", "text");
    m_qualifier = inValues.use("qualifier", m_qualifier);
    m_speed = inValues.use("speed", m_speed,
                           ExpressionValue.parser(Speed.PARSER));
    m_abilityModifiers = inValues.use("ability_modifier", m_abilityModifiers,
                                      AbilityModifier.PARSER,
                                      "ability", "modifier");
    m_immunities = inValues.use("immunity", m_immunities, Immunity.PARSER);
    m_reflexModifier = inValues.use("reflex_modifier", m_reflexModifier,
                                    NewModifier.PARSER);
    m_willModifier = inValues.use("will_modifier", m_willModifier,
                                  NewModifier.PARSER);
    m_fortitudeModifier = inValues.use("fortitude_modifier",
                                       m_fortitudeModifier,
                                       NewModifier.PARSER);
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
  /*
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
  */

  /**
   * Compute the modifier base on an expression.
   *
   * @param   inModifier    the modifier to compute from
   * @param   inParameters  parameters to adjust computation
   *
   * @return  the modifier with evaluated expressions
   */
  /*
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
  */

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
  /*
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
  */

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

    return inUser.get().hasAccess(BaseCharacter.Group.DM);
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

    BaseQualityProto proto = builder.build();
    return proto;
  }

  @Override
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
                       ? Optional.of(NewModifier.fromProto
                                     (effect.getModifier()))
                       : Optional.<NewModifier>absent(),
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
        Optional.of(NewModifier.fromProto(proto.getReflexModifier()));

    if(proto.hasWillModifier())
      m_willModifier =
        Optional.of(NewModifier.fromProto(proto.getWillModifier()));

    if(proto.hasFortitudeModifier())
      m_fortitudeModifier =
        Optional.of(NewModifier.fromProto(proto.getFortitudeModifier()));
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

  //---------------------------------------------------------------------------

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
