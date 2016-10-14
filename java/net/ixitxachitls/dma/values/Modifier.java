/******************************************************************************
 * Copyright (c) 2002-2013 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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


package net.ixitxachitls.dma.values;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import com.google.common.base.Optional;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;

import net.ixitxachitls.dma.entries.Monster;
import net.ixitxachitls.dma.output.soy.SoyTemplate;
import net.ixitxachitls.dma.output.soy.SoyValue;
import net.ixitxachitls.dma.proto.Values.ModifierProto;
import net.ixitxachitls.dma.values.enums.Ability;
import net.ixitxachitls.dma.values.enums.Named;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.input.ReadException;
import net.ixitxachitls.util.Strings;

/**
 * A modifier value.
 *
 * @file   NewModifier.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Modifier extends Value.Arithmetic<ModifierProto>
{
  /** The modifiers type. */
  public enum Type implements Named
  {
    /** Dodging stuff. */
    DODGE("dodge", true, ModifierProto.Type.DODGE),

    /** Better armor. */
    ARMOR("armor", false, ModifierProto.Type.ARMOR),

    /** Some equipment benefit. */
    EQUIPMENT("equipment", false, ModifierProto.Type.EQUIPMENT),

    /** A shield helping out. */
    SHIELD("shield", false, ModifierProto.Type.SHIELD),

    /** The general or standard type. */
    GENERAL("general", true, ModifierProto.Type.GENERAL),

    /** Modifier for natural armor. */
    NATURAL_ARMOR("natural armor", false, ModifierProto.Type.NATURAL_ARMOR),

    /** A modifier from an ability. */
    ABILITY("ability", true, ModifierProto.Type.ABILITY),

    /** A modifier according to size. */
    SIZE("size", false, ModifierProto.Type.SIZE),

    /** A racial modifier. */
    RACIAL("racial", false, ModifierProto.Type.RACIAL),

    /** Circumstances giving a modifier. */
    CIRCUMSTANCE("circumstance", true, ModifierProto.Type.CIRCUMSTANCE),

    /** A magical enhancement modifier. */
    ENHANCEMENT("enhancement", false, ModifierProto.Type.ENHANCEMENT),

    /** A deflection modifier against attacks. */
    DEFLECTION("deflection", false, ModifierProto.Type.DEFLECTION),

    /** A rage modifier. */
    RAGE("rage", false, ModifierProto.Type.RAGE),

    /** A competence modifier against attacks. */
    COMPETENCE("competence", false, ModifierProto.Type.COMPETENCE),

    /** A synergy bonus. */
    SYNERGY("synergy", false, ModifierProto.Type.SYNERGY);

    /** The value's name. */
    private final String m_name;

    /** Flag if the value stacks with others of its kind. */
    private final boolean m_stacks;

    /** The proto enum value. */
    private final ModifierProto.Type m_proto;

    /** Create the name.
     *
     * @param inName   the name of the value
     * @param inStacks true if this modifier stacks with similar ones, false
     *                 if not
     * @param inProto  the proto enum value
     */
    private Type(String inName, boolean inStacks, ModifierProto.Type inProto)
    {
      m_name = inName;
      m_stacks = inStacks;
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

    /** Convert to a human readable string.
     *
     * @return the converted string
     *
     */
    @Override
    public String toString()
    {
        return m_name;
    }

    /** Check if this type stacks with similar ones of its kind.
     *
     * @return true if it stacks, false if not
     *
     */
    public boolean stacks()
    {
      return m_stacks;
    }

    public boolean isGeneral()
    {
      return this == GENERAL;
    }

    /**
     * Get the proto value for this value.
     *
     * @return the proto enum value
     */
    public ModifierProto.Type getProto()
    {
      return m_proto;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inProto     the proto value to look for
     * @return the matched enum (will throw exception if not found)
     */
    public static Type fromProto(ModifierProto.Type inProto)
    {
      for(Type type : values())
        if(type.m_proto == inProto)
          return type;

      throw new IllegalStateException("invalid proto type: " + inProto);
    }

    /**
     * Get the armor type from the given string.
     *
     * @param inValue the string representation
     * @return the matching type, if any
     */
    public static Optional<Type> fromString(String inValue)
    {
      for(Type type : values())
        if(type.getName().equalsIgnoreCase(inValue))
          return Optional.of(type);

      return Optional.absent();
    }

    /**
     * Get the possible names of types.
     *
     * @return a list of the namees
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();
      for(Type type : values())
        names.add(type.getName());

      return names;
    }
  }

  /** The parser for modifiers. */
  public static final Parser<Modifier> PARSER = new Parser<Modifier>(0)
  {
    @Override
    protected Optional<Modifier> doParse(String ... inValues)
    {
      return parse(Strings.COMMA_SPLITTER.splitToList
                   (Strings.COMMA_JOINER.join(inValues)));
    }

    /**
     * Parse a list of values to a modifier.
     *
     * @param inValues the values to parse
     * @return the parsed modifier, if any
     */
    private Optional<Modifier> parse(List<String> inValues)
    {
      List<String> values = new ArrayList<>(inValues);
      Collections.reverse(values);
      Optional<Modifier> result = Optional.absent();
      for(String value : values)
      {
        String []parts =
          Strings.getPatterns(Matcher.quoteReplacement(value),
                              "^\\s*([+-]\\d+)\\s*(" + TYPES + ")?\\s*"
                              + "(?: if\\s+(.*))?$");
        if(parts == null || parts.length == 0)
          return Optional.absent();

        try
        {
          int modifier = Integer.parseInt(parts[0]);
          Type type;
          if(parts[1] == null)
            type = Type.GENERAL;
          else
            type = Type.fromString(parts[1]).get();

          Optional<String> condition = Optional.fromNullable(parts[2]);

          result =
            Optional.of(new Modifier(modifier, type, condition, result));
        }
        catch(NumberFormatException e)
        {
          return Optional.absent();
        }
      }

      return result;
    }
  };

  /** Create a default modifier. */
  public Modifier()
  {
    this(0, Type.GENERAL, Optional.<String>absent(),
         Optional.<Modifier>absent());
  }

  public Modifier(int inModifier)
  {
    this(inModifier, Type.GENERAL, Optional.<String>absent(),
         Optional.<Modifier>absent());
  }

  public Modifier(int inModifier, Type inType)
  {
    this(inModifier, inType, Optional.<String>absent(),
         Optional.<Modifier>absent());
  }

  /**
   * Create a modifier with a value.
   *
   * @param inModifier the base modifier
   * @param inType the type of modifier
   * @param inCondition the condition, if any
   * @param inNext the next modifier in chain, if any
   */
  public Modifier(int inModifier, Type inType, Optional<String> inCondition,
                  Optional<Modifier> inNext)
  {
    this(inModifier,
         inCondition.isPresent()
             ? Condition.PARSER.parse(inCondition.get())
             : Optional.<Condition>absent(), inNext, inType);
  }

  private Modifier(int inModifier, Optional<Condition> inCondition,
                   Optional<Modifier> inNext, Type inType)
  {
    m_modifier = inModifier;
    m_type = inType;
    m_condition = inCondition;
    m_next = inNext;
  }

  /** The types of modifiers available. */
  private static final String TYPES = Strings.PIPE_JOINER.join(Type.names());

  /** The modifier value itself. */
  private final int m_modifier;

  /** The type of the modifier. */
  private final Type m_type;

  /** The default type, if any. */
  private final Type m_defaultType = Type.GENERAL;

  /** The condition for the modifier, if any. */
  private final Optional<Condition> m_condition;

  /** A next modifier, if any. */
  private final Optional<Modifier> m_next;

  /**
   * Get the value of the modifier, ignoring additional modifiers.
   *
   * @return      the requested valu
   */
  public int getModifier()
  {
    return m_modifier;
  }

  public int totalModifier()
  {
    return m_modifier
        + (m_next.isPresent() ? m_next.get().totalModifier() : 0);
  }

  public int unconditionalModifier()
  {
    int modifier = 0;
    if (!m_condition.isPresent())
      modifier += m_modifier;

    if (m_next.isPresent())
      modifier += m_next.get().unconditionalModifier();

    return modifier;
  }

  public int total(Monster inMonster)
  {
    int modifier = 0;
    if(m_next.isPresent())
      modifier += m_next.get().total(inMonster);

    if(m_condition.isPresent()) {
      Optional<Boolean> check = m_condition.get().check(inMonster);
      if(check.isPresent() && check.get()) {
        modifier += m_modifier;
      }
    }

    return modifier;
  }

  public boolean hasValue()
  {
    if(m_modifier != 0)
      return true;

    if(m_next.isPresent())
      return m_next.get().hasValue();

    return false;
  }

  /**
   * Get the type of the modifier.
   *
   * @return the modifier type
   */
  public Type getType()
  {
    return m_type;
  }

  /**
   * Get the condition of the modifier, if any.
   *
   * @return the condition
   */
  public Optional<String> getCondition()
  {
    return m_condition.isPresent()
        ? Optional.of(m_condition.get().toString())
        : Optional.<String>absent();
  }

  public boolean hasCondition()
  {
    return m_condition.isPresent();
  }

  public boolean hasAnyCondition()
  {
    if(hasCondition())
      return true;

    if(m_next.isPresent())
      return m_next.get().hasAnyCondition();

    return false;
  }

  /**
   * Get the next modifier if there are chained modifiers.
   *
   * @return the next modifier
   */
  public Optional<Modifier> getNext()
  {
    return m_next;
  }

  public SanitizedContent formatted()
  {
    return UnsafeSanitizedContentOrdainer.ordainAsSafe(
        SoyTemplate.VALUE_RENDERER
            .renderSoy("dma.value.modifier",
                       Optional.of(SoyTemplate.map
                           ("modifier", new SoyValue("(render Modifier)",
                                                     this)))),
        SanitizedContent.ContentKind.HTML);
  }

  @Override
  public String toString()
  {
    StringBuilder result = new StringBuilder();
    if(m_modifier >= 0)
      result.append("+");

    result.append(m_modifier);

    if(m_type != Type.GENERAL)
      result.append(" " + m_type);

    if(m_condition.isPresent())
      result.append(" if " + m_condition.get());

    if(m_next.isPresent())
      result.append(", " + m_next.get());

    return result.toString();
  }

  @Override
  public ModifierProto toProto()
  {
    ModifierProto.Builder builder = ModifierProto.newBuilder();
    addToProto(builder);

    return builder.build();
  }

  /**
   * Add the values of this modifier to the given proto.
   *
   * @param inBuilder the builder to fill
   */
  private void addToProto(ModifierProto.Builder inBuilder)
  {
    ModifierProto.Modifier.Builder modifier =
      ModifierProto.Modifier.newBuilder();

    modifier.setBaseValue(m_modifier);
    modifier.setType(m_type.getProto());
    if (m_condition.isPresent())
      modifier.setCondition(m_condition.get().toString());

    inBuilder.addModifier(modifier.build());

    if(m_next.isPresent())
      m_next.get().addToProto(inBuilder);
   }

  @Override
  public Value.Arithmetic<ModifierProto>
    add(Value.Arithmetic<ModifierProto> inValue)
  {
    if(!(inValue instanceof Modifier))
      return this;

    if(m_modifier == 0)
      if(m_next.isPresent())
        return m_next.get().add(inValue);
      else
        return inValue;

    if(((Modifier) inValue).m_modifier == 0)
      if(((Modifier) inValue).m_next.isPresent())
        return ((Modifier) inValue).m_next.get().add(this);
      else
        return this;

    Modifier value = (Modifier)inValue;
    if(m_type == value.m_type && m_condition.equals(value.m_condition))
    {
      Optional<Modifier> next;
      if(!m_next.isPresent())
        next = value.m_next;
      else if(!value.m_next.isPresent())
        next = m_next;
      else
        next = Optional.of((Modifier)m_next.get().add(value.m_next.get()));

      if(m_type.stacks())
        return new Modifier(m_modifier + value.m_modifier, m_condition,
                            next, m_type);
      else
        return new Modifier(Math.max(m_modifier, value.m_modifier),
                            m_condition, next, m_type);
    }

    if(!m_next.isPresent())
      return new Modifier(m_modifier, m_condition, Optional.of(value), m_type);

    return new Modifier(m_modifier, m_condition,
                        Optional.of((Modifier)m_next.get().add(value)), m_type);
  }

  @Override
  public Value.Arithmetic<ModifierProto> multiply(int inFactor)
  {
    return new Modifier(m_modifier * inFactor, m_condition,
                        m_next.isPresent()
                            ? Optional.of((Modifier)
                                              m_next.get().multiply(inFactor))
                            : m_next, m_type);
  }

  @Override
  public Value.Arithmetic<ModifierProto> multiply(Rational inFactor)
  {
    return new Modifier(m_modifier * inFactor.getLeader(), m_condition,
                        m_next.isPresent()
                            ? Optional.of((Modifier)
                                              m_next.get().multiply(inFactor))
                            : m_next, m_type);
  }

  /**
   * Create a new modifier with the values from the given proto.
   *
   * @param inProto the proto to read the values from
   * @return the newly created critical
   */
  public static Modifier fromProto(ModifierProto inProto)
  {
    Modifier result = null;
    List<ModifierProto.Modifier> modifiers =
        new ArrayList<>(inProto.getModifierList());
    Collections.reverse(modifiers);
    for(ModifierProto.Modifier modifier : modifiers)
    {
      result = new Modifier(modifier.getBaseValue(),
                            Type.fromProto(modifier.getType()),
                            modifier.hasCondition()
                                ? Optional.of(modifier.getCondition())
                                : Optional.<String>absent(),
                            Optional.fromNullable(result));
    }

    return result;
  }

  @Override
  public boolean canAdd(Value.Arithmetic<ModifierProto> inValue)
  {
    return inValue instanceof Modifier;
  }

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Testing init. */
    @org.junit.Test
    public void parse()
    {
      assertFalse("parse", PARSER.parse(null).isPresent());
      assertFalse("parse", PARSER.parse("").isPresent());
      assertFalse("parse", PARSER.parse(" none  ").isPresent());
      assertEquals("parse", "+2",
                   PARSER.parse(" +2 general  ").get().toString());
      assertEquals("parse", "+2 dodge, +3 shield",
                   PARSER.parse("+2 dodge,   +3 shield ").get().toString());
      assertEquals("parse", "+2 dodge, +3 dodge",
                   PARSER.parse("+2 dodge, +3 dodge").get().toString());
      assertEquals("parse", "+2 dodge if guru, +3 shield",
                   PARSER.parse("+2 dodge if guru, +3 shield")
                         .get().toString());
      assertFalse("parse", PARSER.parse("+2 dodge guru").isPresent());
      assertFalse("parse", PARSER.parse("+2 dodge +3 guru").isPresent());
      assertFalse("parse", PARSER.parse("+ 2 dodge").isPresent());
    }

    /** Testing proto conversion. */
    @org.junit.Test
    public void proto()
    {
      ModifierProto proto = ModifierProto
          .newBuilder()
          .addModifier(ModifierProto.Modifier
                           .newBuilder()
                           .setBaseValue(42)
                           .setType(ModifierProto.Type.RAGE)
                           .setCondition("condition"))
          .build();
      Modifier modifier = Modifier.fromProto(proto);

      assertEquals("from proto", "+42 rage if condition", modifier.toString());
      assertEquals("to proto", proto, modifier.toProto());
      assertEquals("modifier", 42, modifier.getModifier());
      assertEquals("condition", "condition", modifier.getCondition().get());
      assertEquals("type", Type.RAGE, modifier.getType());
    }

    /** Test adding. */
    @org.junit.Test
    public void add()
    {
      Modifier modifier = new Modifier(23, Type.ARMOR,
                                       Optional.<String>absent(),
                                       Optional.<Modifier>absent());
      modifier = (Modifier)
          modifier.add(new Modifier(42, Type.GENERAL, Optional.<String>absent(),
                                    Optional.<Modifier>absent()));
      modifier = (Modifier)
          modifier.add(new Modifier(1, Type.ARMOR, Optional.<String>absent(),
                                    Optional.of
                                        (new Modifier
                                             (1, Type.GENERAL,
                                              Optional.<String>absent(),
                                              Optional.<Modifier>absent()))));
      modifier = (Modifier)
          modifier.add(new Modifier(1, Type.ARMOR, Optional.of("maybe"),
                                    Optional.of(
                                        new Modifier
                                            (1, Type.GENERAL,
                                             Optional.of("maybe"),
                                             Optional.<Modifier>absent()))));

      assertEquals("modifier", "+23 armor, +43, +1 armor if maybe, +1 if maybe",
                   modifier.toString());
    }
  }
}
