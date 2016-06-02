/*******************************************************************************
 * Copyright (c) 2002-2016 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
 ******************************************************************************/

package net.ixitxachitls.dma.values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.entries.BaseMiniature;
import net.ixitxachitls.dma.proto.Entries;
import net.ixitxachitls.dma.values.enums.MiniatureLocationRuleType;
import net.ixitxachitls.util.Strings;

/**
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class MiniatureLocation
    extends Value<Entries.BaseCharacterProto.MiniatureLocation>
{
  public static class Rule
  {
    public Rule(MiniatureLocationRuleType inType, String inValue)
    {
      m_type = inType;
      m_value = inValue;
    }

    private final MiniatureLocationRuleType m_type;
    private final String m_value;

    public MiniatureLocationRuleType getType()
    {
      return m_type;
    }

    public String getValue()
    {
      return m_value;
    }

    public String toString()
    {
      return m_type + ":" + m_value;
    }

    public boolean matches(BaseMiniature inMiniature)
    {
      switch(m_type)
      {
        case TYPE:
          return inMiniature.getMiniatureType().isPresent()
              && inMiniature.getMiniatureType().get().equalsIgnoreCase(m_value);

        case ORIGIN:
          return inMiniature.getOrigin().isPresent()
              && inMiniature.getOrigin().get().equalsIgnoreCase(m_value);

        case SUBTYPE:
          for(String subtype : inMiniature.getSubtypes())
            if(subtype.equalsIgnoreCase(m_value))
              return true;

          return false;

        case CLASS:
          return !inMiniature.getClasses().isEmpty()
              && inMiniature.getClasses().get(0).toLowerCase().startsWith(
              m_value.toLowerCase());

        case SIZE:
          return inMiniature.getSize().toString().equalsIgnoreCase(m_value);
      }

      return false;
    }
  }

  public static class LocationParser extends Parser<MiniatureLocation>
  {
    public LocationParser()
    {
      super(4);
    }

    @Override    public Optional<MiniatureLocation> doParse(String inLocation,
                                               String inRules,
                                               String inOverrides,
                                               String inColor)
    {
      Optional<Boolean> overrides = BOOLEAN_PARSER.doParse(inOverrides);
      return Optional.of(new MiniatureLocation(inLocation, parseRules(inRules),
                                               inColor,
                                               overrides.isPresent()
                                               && overrides.get()));
    }

    private List<Rule> parseRules(String inRules)
    {
      List<Rule> rules = new ArrayList<>();

      for(String rule : inRules.split("\\s*,\\s*"))
      {
        String[] parts = rule.split("\\s*:\\s*");
        if(parts.length == 2)
        {
          Optional<MiniatureLocationRuleType> type =
              MiniatureLocationRuleType.fromString(parts[0]);
          if(type.isPresent())
            rules.add(new Rule(type.get(), parts[1]));
        }
      }

      return rules;
    }
  }

  public MiniatureLocation(String inLocation, List<Rule> inRules,
                           String inColor, boolean inOverrides)
  {
    m_location = inLocation;
    m_rules = Collections.unmodifiableList(inRules);
    m_color = inColor;
    m_overrides = inOverrides;
  }

  private String m_location;
  private List<Rule> m_rules;
  private String m_color;
  private final boolean m_overrides;

  public static final LocationParser PARSER = new LocationParser();

  public String getLocation()
  {
    return m_location;
  }

  public String getRules()
  {
    return Strings.COMMA_JOINER.join(m_rules);
  }

  public String getColor()
  {
    return m_color;
  }

  public boolean overrides()
  {
    return m_overrides;
  }

  @Override
  public String toString()
  {
    return m_location + ": " + getRules() + (m_overrides ? " (override)" : "");
  }

  @Override
  public Entries.BaseCharacterProto.MiniatureLocation toProto()
  {
    Entries.BaseCharacterProto.MiniatureLocation.Builder proto =
        Entries.BaseCharacterProto.MiniatureLocation.newBuilder()
            .setLocation(m_location)
            .setColor(m_color)
        .setOverrides(m_overrides);

    for(Rule rule : m_rules)
      proto.addRules(
          Entries.BaseCharacterProto.MiniatureLocation.Rule.newBuilder()
              .setType(rule.getType().toProto())
              .setValue(rule.getValue())
              .build())


        .build();

    return proto.build();
  }

  public static MiniatureLocation fromProto(
      Entries.BaseCharacterProto.MiniatureLocation inProto)
  {
    List<Rule> rules = new ArrayList<>();
    for (Entries.BaseCharacterProto.MiniatureLocation.Rule rule :
        inProto.getRulesList())
      rules.add(new Rule(MiniatureLocationRuleType.fromProto(rule.getType()),
                rule.getValue()));

    return new MiniatureLocation(inProto.getLocation(), rules,
                                 inProto.getColor(), inProto.getOverrides());
  }

  public int matches(BaseMiniature inMiniature) {
    int matches = 0;
    for (Rule rule : m_rules)
      if(rule.matches(inMiniature))
        matches++;
      else
        return 0;

    return matches;
  }
}
