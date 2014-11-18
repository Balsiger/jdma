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
import net.ixitxachitls.dma.proto.Entries.BaseFeatProto;
import net.ixitxachitls.dma.values.Modifier;
import net.ixitxachitls.dma.values.enums.Affects;
import net.ixitxachitls.dma.values.enums.Effect;
import net.ixitxachitls.dma.values.enums.FeatType;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the basic jDMA base spell.
 *
 * @file          BaseFeat.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
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
    new BaseType<BaseFeat>(BaseFeat.class);

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

  /** The effects of the feat. */
  protected List<Effect> m_effects = new ArrayList<>();

  public FeatType getFeatType()
  {
    return m_featType;
  }

  public Optional<String> getBenefit()
  {
    return m_benefit;
  }

  public Optional<String> getSpecial()
  {
    return m_special;
  }

  public Optional<String> getNormal()
  {
    return m_normal;
  }

  public Optional<String> getPrerequisites()
  {
    return m_prerequisites;
  }

  public List<Effect> getEffects()
  {
    return m_effects;
  }

  public List<String> getAffectNames()
  {
    return Affects.names();
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

    return inUser.get().hasAccess(BaseCharacter.Group.DM);
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
  public void set(Values inValues)
  {
    super.set(inValues);

    m_featType = inValues.use("feat_type", m_featType, FeatType.PARSER);
    m_benefit = inValues.use("benefit", m_benefit);
    m_special = inValues.use("special", m_special);
    m_normal = inValues.use("normal", m_normal);
    m_prerequisites = inValues.use("prerequisites", m_prerequisites);
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

  @Override
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

  //----------------------------------------------------------------------------

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
        return null; //BaseFeat.read(reader);
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
