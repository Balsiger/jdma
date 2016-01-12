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
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Entries.BaseSkillProto;
import net.ixitxachitls.dma.values.Annotated;
import net.ixitxachitls.dma.values.Parser;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Ability;
import net.ixitxachitls.dma.values.enums.Group;
import net.ixitxachitls.dma.values.enums.SkillModifier;
import net.ixitxachitls.dma.values.enums.SkillRestriction;
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
  /** A difficulty class.  */
  public static class DC
  {
    /**
     * Create the difficulty class.
     *
     * @param inDC the difficulty class
     * @param inDescription the description
     */
    public DC(int inDC, String inDescription)
    {
      m_dc = inDC;
      m_description = inDescription;
    }

    /** The difficulty to surpass. */
    private final int m_dc;

    /** The description of the class. */
    private final String m_description;

    /** The parser for the dc. */
    public static final Parser<DC> PARSER =
        new Parser<DC>(2)
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

    /**
     * Get the difficulty.
     *
     * @return the dc value
     */
    public int getDC()
    {
      return m_dc;
    }

    /**
     * Get the description.
     *
     * @return the description
     */
    public String getDescription()
    {
      return m_description;
    }
  }

  public static class Synergy
  {
    public Synergy(String inName) {
      m_name = inName;
      m_condition = Optional.absent();
    }

    public Synergy(String inName, String inCondition) {
      m_name = inName;
      m_condition = Optional.of(inCondition);
    }

    private String m_name;
    private Optional<String> m_condition;

    public static final Parser<Synergy> PARSER =
        new Parser<Synergy>(2)
        {
          @Override
          public Optional<Synergy> doParse(String inName, String inCondition)
          {
            if(inCondition != null && !inCondition.isEmpty())
              return Optional.of(new Synergy(inName, inCondition));

            return Optional.of(new Synergy(inName));
          }
        };

    public String getName()
    {
      return m_name;
    }

    public Optional<String> getCondition()
    {
      return m_condition;
    }

    public String toString()
    {
      if(m_condition.isPresent())
        return m_name + " if " + m_condition.get();

      return m_name;
    }

    public BaseSkillProto.Synergy toProto()
    {
      if(m_condition.isPresent())
        return BaseSkillProto.Synergy.newBuilder()
            .setName(m_name)
            .setCondition(m_condition.get())
            .build();

      return BaseSkillProto.Synergy.newBuilder().setName(m_name).build();
    }

    public static Synergy fromProto(BaseSkillProto.Synergy inSynergy)
    {
      if(inSynergy.hasCondition())
        return new Synergy(inSynergy.getName(), inSynergy.getCondition());

      return new Synergy(inSynergy.getName());
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
    new BaseType.Builder<>(BaseSkill.class).build();

  /** The base ability for this skill. */
  private Ability m_ability = Ability.UNKNOWN;

  /** The check to make. */
  private Optional<String> m_check = Optional.absent();

  /** The action that can be done. */
  private Optional<String> m_action = Optional.absent();

  /** Can it be tried again. */
  private Optional<String> m_retry = Optional.absent();

  /** The special remarks. */
  private Optional<String> m_special = Optional.absent();

  /** The synergies to other skills. */
  private List<String> m_synergies_deprecated = new ArrayList<>();

  /** The synergies from other skills. */
  private List<Synergy> m_synergies = new ArrayList<>();

  /** The restrictions. */
  private Optional<String> m_restriction = Optional.absent();

  /** What can be done untrained. */
  private Optional<String> m_untrained = Optional.absent();

  /** Restrictions when using the skill. */
  private List<SkillRestriction> m_restrictions = new ArrayList<>();

  /** A list of special modifiers to recognize. */
  private List<SkillModifier> m_modifiers = new ArrayList<>();

  /** Various DCs for this skill. */
  private List<DC> m_dcs = new ArrayList<>();

  /**
   * Get the index of the skills base ability.
   *
   * @return      the base ability of the skill.
   */
  public Ability getAbility()
  {
    return m_ability;
  }

  public Annotated<Optional<Ability>> getCombinedAbility()
  {
    if(m_ability != Ability.UNKNOWN)
      return new Annotated.Max<Ability>(m_ability, getName());

    Annotated<Optional<Ability>> combined = new Annotated.Max<Ability>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseSkill)entry).getCombinedAbility());

    return combined;
  }


  /**
   * Get the description about how to do the skill check.
   *
   * @return the check description
   */
  public Optional<String> getCheck()
  {
    return m_check;
  }

  public Annotated<Optional<String>> getCombinedCheck()
  {
    if(m_check.isPresent())
      return new Annotated.String(m_check.get(), getName());

    Annotated<Optional<String>> combined = new Annotated.String();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseSkill)entry).getCombinedCheck());

    return combined;
  }

  /**
   * Get the description about the action needed for the skill check.
   *
   * @return the action description
   */
  public Optional<String> getAction()
  {
    return m_action;
  }

  public Annotated<Optional<String>> getCombinedAction()
  {
    if(m_action.isPresent())
      return new Annotated.String(m_action.get(), getName());

    Annotated<Optional<String>> combined = new Annotated.String();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseSkill)entry).getCombinedAction());

    return combined;
  }

  /**
   * Get the description explaining if retrying is possible or not.
   *
   * @return the description about retrying
   */
  public Optional<String> getRetry()
  {
    return m_retry;
  }

  public Annotated<Optional<String>> getCombinedRetry()
  {
    if(m_retry.isPresent())
      return new Annotated.String(m_retry.get(), getName());

    Annotated<Optional<String>> combined = new Annotated.String();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseSkill)entry).getCombinedRetry());

    return combined;
  }

  /**
   * Get the special comments about the skill.
   *
   * @return the special comments
   */
  public Optional<String> getSpecial()
  {
    return m_special;
  }

  public Annotated<Optional<String>> getCombinedSpecial()
  {
    if(m_special.isPresent())
      return new Annotated.String(m_special.get(), getName());

    Annotated<Optional<String>> combined = new Annotated.String();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseSkill)entry).getCombinedSpecial());

    return combined;
  }

  /**
   * Get the skill synergy description.
   *
   * @return the skill synergies
   */
  public List<Synergy> getSynergies()
  {
    return m_synergies;
  }

  public Annotated<List<Synergy>> getCombinedSynergies()
  {
    if(!m_synergies.isEmpty())
      return new Annotated.List(m_synergies, getName());

    Annotated<List<Synergy>> combined = new Annotated.List<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseSkill)entry).getCombinedSynergies());

    return combined;
  }

  /**
   * Get the description about restrictions for the skill.
   *
   * @return the restrictions
   */
  public Optional<String> getRestriction()
  {
    return m_restriction;
  }

  public Annotated<Optional<String>> getCombinedRestriction()
  {
    if(m_restriction.isPresent())
      return new Annotated.String(m_restriction.get(), getName());

    Annotated<Optional<String>> combined = new Annotated.String();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseSkill)entry).getCombinedRestriction());

    return combined;
  }

  /**
   * Get the explanation how the skill can be used untrained.
   *
   * @return the untrained text
   */
  public Optional<String> getUntrained()
  {
    return m_untrained;
  }

  public Annotated<Optional<String>> getCombinedUntrained()
  {
    if(m_untrained.isPresent())
      return new Annotated.String(m_untrained.get(), getName());

    Annotated<Optional<String>> combined = new Annotated.String();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseSkill)entry).getCombinedUntrained());

    return combined;
  }

  /**
   * Get the restrictions for the skill.
   *
   * @return the restrictions
   */
  public List<SkillRestriction> getRestrictions()
  {
    return m_restrictions;
  }

  public Annotated<List<SkillRestriction>> getCombinedRestrictions()
  {
    if(!m_restrictions.isEmpty())
      return new Annotated.List(m_restrictions, getName());

    Annotated<List<SkillRestriction>> combined = new Annotated.List<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseSkill)entry).getCombinedRestrictions());

    return combined;
  }

  /**
   * Get the skill modifiers.
   *
   * @return the skill modifiers
   */
  public List<SkillModifier> getModifiers()
  {
    return m_modifiers;
  }

  public Annotated<List<SkillModifier>> getCombinedModifiers()
  {
    if(!m_modifiers.isEmpty())
      return new Annotated.List<>(m_modifiers, getName());

    Annotated<List<SkillModifier>> combined = new Annotated.List<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseSkill)entry).getCombinedModifiers());

    return combined;
  }

  /**
   * Get the relevant DCs for the skill.
   *
   * @return the dcs
   */
  public List<DC> getDCs()
  {
    return m_dcs;
  }

  public Annotated<List<DC>> getCombinedDCs()
  {
    if(!m_dcs.isEmpty())
      return new Annotated.List<>(m_dcs, getName());

    Annotated<List<DC>> combined = new Annotated.List<>();
    for(BaseEntry entry : getBaseEntries())
      combined.add(((BaseSkill)entry).getCombinedDCs());

    return combined;
  }

  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(Group.DM);
  }

  /**
   * Check if the skill can be used untrained.
   *
   * @return      true if it can be used trained only, false else
   *
   */
  public boolean isUntrained()
  {
    return !getCombinedRestrictions().get()
        .contains(SkillRestriction.TRAINED_ONLY);
  }

  public boolean isSubtypeOnly()
  {
    return m_restrictions.contains(SkillRestriction.SUBTYPE_ONLY);
  }

  public boolean hasArmorCheckPenalty()
  {
    return getCombinedRestrictions().get()
        .contains(SkillRestriction.ARMOR_CHECK_PENALTY)
        || getCombinedRestrictions().get()
        .contains(SkillRestriction.DOUBLE_ARMOR_CHECK_PENALTY);
  }

  public boolean hasDoubleArmorCheckPenalty()
  {
    return getCombinedRestrictions().get()
        .contains(SkillRestriction.DOUBLE_ARMOR_CHECK_PENALTY);
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
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    m_ability = inValues.use("ability", m_ability, Ability.PARSER);
    m_check = inValues.use("check", m_check);
    m_action = inValues.use("action", m_action);
    m_retry = inValues.use("retry", m_retry);
    m_special = inValues.use("special", m_special);
    m_synergies_deprecated = inValues.use("synergies", m_synergies_deprecated);
    m_synergies = inValues.use("synergy", m_synergies, Synergy.PARSER,
                               "name", "condition");
    m_restriction = inValues.use("restriction", m_restriction);
    m_untrained = inValues.use("untrained", m_untrained);
    m_restrictions = inValues.use("restrictions", m_restrictions,
                                 SkillRestriction.PARSER);
    m_modifiers = inValues.use("modifier", m_modifiers, SkillModifier.PARSER);
    m_dcs = inValues.use("dc", m_dcs, DC.PARSER, "dc", "text");
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

    for(Synergy synergy : m_synergies)
      builder.addSynergy(synergy.toProto());

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

  /**
   * Set all the values of the entry from the given proto.
   *
   * @param inProto the proto with the values
   */
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

    for(String synergy : proto.getSynergyDeprecatedList())
      m_synergies_deprecated.add(synergy);

    for(BaseSkillProto.Synergy synergy : proto.getSynergyList())
      m_synergies.add(Synergy.fromProto(synergy));

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
  protected Message defaultProto()
  {
    return BaseSkillProto.getDefaultInstance();
  }

  public static List<BaseSkill> allSkills()
  {
    return DMADataFactory.get().getEntries(BaseSkill.TYPE,
                                           Optional.<EntryKey>absent(),
                                           0, 1000);
  }

  public static Optional<BaseSkill> get(String inName)
  {
    for(BaseSkill skill : allSkills()) {
      if(skill.getName().equalsIgnoreCase(inName))
        return Optional.of(skill);
    }

    return Optional.absent();
  }

  //---------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }
}
