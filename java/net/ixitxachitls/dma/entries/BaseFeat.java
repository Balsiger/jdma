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

import net.ixitxachitls.dma.entries.BaseQuality.Affects;
import net.ixitxachitls.dma.entries.extensions.BaseIncomplete;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseFeatProto;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Expression;
import net.ixitxachitls.dma.values.FormattedText;
import net.ixitxachitls.dma.values.LongFormattedText;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Parameters;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.logging.Log;

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

@ParametersAreNonnullByDefault
public class BaseFeat extends BaseEntry
{
  //----------------------------------------------------------------- nested

  //----- type -------------------------------------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The possible areas to affect (cf. PHB 175). */
  public enum Type implements EnumSelection.Named
  {
    /** A general feat. */
    GENERAL("General", BaseFeatProto.Type.GENERAL),

    /** An item creation feat. */
    ITEM_CREATION("Item Creation", BaseFeatProto.Type.ITEM_CREATION),

    /** A metamagic feat. */
    METAMAGIC("Metamagic", BaseFeatProto.Type.METAMAGIC),

    /** A regional feat. */
    REGIONAL("Regional", BaseFeatProto.Type.REGIONAL),

    /** A special feat. */
    SPECIAL("Special", BaseFeatProto.Type.SPECIAL),

    /** A fighter feat. */
    FIGHTER("Fighter", BaseFeatProto.Type.FIGHTER);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private BaseFeatProto.Type m_proto;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the proto enum value
     *
     */
    private Type(String inName, BaseFeatProto.Type inProto)
    {
      m_name = constant("feat.type", inName);
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

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public BaseFeatProto.Type getProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static Type fromProto(BaseFeatProto.Type inProto)
    {
      for(Type type: values())
        if(type.m_proto == inProto)
          return type;

      throw new IllegalStateException("invalid proto type: " + inProto);
    }
  }

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
  public BaseFeat(String inName)
  {
    super(inName, TYPE);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final BaseType<BaseFeat> TYPE =
    new BaseType<BaseFeat>(BaseFeat.class);

  //----- type -------------------------------------------------------------

  /** The type of the feat. */
  @Key("type")
  protected EnumSelection<Type> m_featType =
    new EnumSelection<Type>(Type.class);

  static
  {
    addIndex(new Index(Index.Path.TYPES, "Types", TYPE));
  }

  //........................................................................
  //----- benefit ----------------------------------------------------------

  /** The benefits. */
  @Key("benefit")
  protected LongFormattedText m_benefit = new LongFormattedText();

  //........................................................................
  //----- special ----------------------------------------------------------

  /** The special remarks. */
  @Key("special")
  protected LongFormattedText m_special = new LongFormattedText();

  //........................................................................
  //----- normal -----------------------------------------------------------

  /** The special remarks. */
  @Key("normal")
  protected FormattedText m_normal = new FormattedText();

  //........................................................................
  //----- prerequisites ----------------------------------------------------

  /** The prerequisites. */
  @Key("prerequisites")
  protected LongFormattedText m_prerequisites =
    new LongFormattedText();

  //........................................................................
  //----- effects ----------------------------------------------------------

  /** The effects of the feat. */
  @Key("effects")
  protected ValueList<Multiple> m_effects =
    new ValueList<Multiple>(", ", new Multiple(new Multiple.Element []
      { new Multiple.Element(new EnumSelection<BaseQuality.Affects>
                             (BaseQuality.Affects.class), false),
        new Multiple.Element(new Name(), true),
        new Multiple.Element(new Modifier(), true, ": ", null),
      }));

  //........................................................................

  static
  {
    extractVariables(BaseFeat.class);
    extractVariables(BaseFeat.class, BaseIncomplete.class);
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

    values.put(Index.Path.TYPES, m_featType.group());

    return values;
  }

  //........................................................................
  //-------------------------------- collect -------------------------------

  /**
   * Add contributions for this entry to the given list.
   *
   * @param       inName          the name of the value to collect
   * @param       ioCombined      the combined value to collect into
   * @param       inParameters    parameters to adjust values
   */
  public void collect(String inName, Combined<?> ioCombined,
                      Parameters inParameters)
  {
    super.collect(inName, ioCombined);

    for(Multiple multiple : m_effects)
    {
      if(inName.equalsIgnoreCase(multiple.get(1).toString())
         || inName.equalsIgnoreCase(multiple.get(0).toString()))
      {
        Modifier modifier = (Modifier)multiple.get(2);
        if(modifier.getExpression() instanceof Expression.Expr)
        {
          String expression =
            computeExpressions(((Expression.Expr)modifier.getExpression())
                               .getText(), inParameters);

          Modifier computed = modifier.read(expression);
          if (computed != null)
            ioCombined.addModifier(computed, this, null);
          else
            ioCombined.addModifier
              (modifier.as(Integer.valueOf(expression.replace('+', '0')),
                           modifier.getType(), modifier.getCondition(), null),
               this, null);
        }
        else
          ioCombined.addModifier(modifier, this, null);
      }
    }
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  @SuppressWarnings("unchecked")
  @Override
  public Message toProto()
  {
    BaseFeatProto.Builder builder = BaseFeatProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());

    if(m_featType.isDefined())
      builder.setType(m_featType.getSelected().getProto());

    if(m_benefit.isDefined())
      builder.setBenefit(m_benefit.get());

    if(m_special.isDefined())
      builder.setSpecial(m_special.get());

    if(m_normal.isDefined())
      builder.setNormal(m_normal.get());

    if(m_prerequisites.isDefined())
      builder.setPrerequisites(m_prerequisites.get());

    if(m_effects.isDefined())
      for(Multiple effect : m_effects)
      {
        BaseFeatProto.Effect.Builder effectBuilder =
          BaseFeatProto.Effect.newBuilder();

        effectBuilder.setAffects
          (((EnumSelection<Affects>)effect.get(0)).getSelected().getProto());
        if(effect.get(1).isDefined())
          effectBuilder.setReference(((Name)effect.get(1)).get());
        if(effect.get(2).isDefined())
          effectBuilder.setModifier(((Modifier)effect.get(2)).toProto());

        builder.addEffect(effectBuilder.build());
      }

    BaseFeatProto proto = builder.build();
    return proto;
  }

  @Override
  @SuppressWarnings("unchecked")
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
      m_featType = m_featType.as(Type.fromProto(proto.getType()));

    if(proto.hasBenefit())
      m_benefit = m_benefit.as(proto.getBenefit());

    if(proto.hasSpecial())
      m_special = m_special.as(proto.getSpecial());

    if(proto.hasNormal())
      m_normal = m_normal.as(proto.getNormal());

    if(proto.hasPrerequisites())
      m_prerequisites = m_prerequisites.as(proto.getPrerequisites());

    if(proto.getEffectCount() > 0)
    {
      List<Multiple> effects = new ArrayList<>();
      for(BaseFeatProto.Effect effect : proto.getEffectList())
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
                               : multiple.get(2));

        effects.add(multiple);
      }

      m_effects = m_effects.as(effects);
    }
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(BaseFeatProto.parseFrom(inBytes));
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
    //----- createBaseFeat() ----------------------------------------------

    /** Create a typical base item for testing purposes.
     *
     * @return the newly created base item
     *
     */
    public static AbstractEntry createBaseFeat()
    {
      try (ParseReader reader =
        new ParseReader(new java.io.StringReader(s_text), "test"))
      {
        return BaseFeat.read(reader);
      }
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
      + "  worlds            generic;\n"
      + "  references        WTC 17524: 89;\n"
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
      String result =
      "#----- Acrobatic [General]\n"
        + "\n"
        + "base feat Acrobatic =\n"
        + "\n"
        + "  type              General;\n"
        + "  benefit           \"You get a +2 bonus on all Jump checks and "
        + "Tumble checks.\";\n"
        + "  worlds            Generic;\n"
        + "  references        WTC 17524: 89;\n"
        + "  description       \"You have excellent body awareness and "
        + "coordination.\";\n"
        + "  short description \"+2 bonus on Jump and Tumble checks\";\n"
        + "  name              Acrobatic.\n"
        + "\n"
        + "#.....\n";

      AbstractEntry entry = createBaseFeat();

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
