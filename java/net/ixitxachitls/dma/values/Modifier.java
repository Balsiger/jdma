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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;

import net.ixitxachitls.dma.entries.Monster;
import net.ixitxachitls.dma.output.soy.SoyTemplate;
import net.ixitxachitls.dma.output.soy.SoyValue;
import net.ixitxachitls.dma.proto.Values.ModifierProto;
import net.ixitxachitls.dma.values.enums.Named;
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
    DODGE("dodge", true, ModifierProto.Type.DODGE),
    ARMOR("armor", false, ModifierProto.Type.ARMOR),
    EQUIPMENT("equipment", false, ModifierProto.Type.EQUIPMENT),
    SHIELD("shield", false, ModifierProto.Type.SHIELD),
    GENERAL("general", true, ModifierProto.Type.GENERAL),
    NATURAL_ARMOR("natural armor", false, ModifierProto.Type.NATURAL_ARMOR),
    ABILITY("ability", true, ModifierProto.Type.ABILITY),
    SIZE("size", false, ModifierProto.Type.SIZE),
    RACIAL("racial", false, ModifierProto.Type.RACIAL),
    CIRCUMSTANCE("circumstance", true, ModifierProto.Type.CIRCUMSTANCE),
    ENHANCEMENT("enhancement", false, ModifierProto.Type.ENHANCEMENT),
    DEFLECTION("deflection", false, ModifierProto.Type.DEFLECTION),
    RAGE("rage", false, ModifierProto.Type.RAGE),
    COMPETENCE("competence", false, ModifierProto.Type.COMPETENCE),
    SYNERGY("synergy", false, ModifierProto.Type.SYNERGY);

    private final String m_name;
    private final boolean m_stacks;
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

    @Override
    public String getName()
    {
      return m_name;
    }

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
    public ModifierProto.Type toProto()
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
      Modifier.Builder builder = Modifier.newBuilder();
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

          // Replace \$ to $.
          if (parts[2] != null)
            parts[2] = parts[2].replaceAll("\\\\\\$", "\\$");
          Optional<String> condition = Optional.fromNullable(parts[2]);

          if(condition.isPresent())
            builder.add(type, modifier,
                        Condition.PARSER.parse(condition.get()));
          else
            builder.add(type, modifier);
        }
        catch(NumberFormatException e)
        {
          return Optional.absent();
        }
      }

      if(builder.isEmpty())
        return Optional.absent();

      return Optional.of(builder.build());
    }
  };

  private static class Part
  {
    private Part(Type inType, int inModifier)
    {
      this(inType, inModifier, Optional.<Condition>absent());
    }

    private Part(Type inType, int inModifier, Condition inCondition)
    {
      this(inType, inModifier, Optional.of(inCondition));
    }

    private Part(Type inType, int inModifier, Optional<Condition> inCondition)
    {
      m_type = inType;
      m_modifier = inModifier;
      m_condition = inCondition;
    }

    private final Type m_type;
    private final int m_modifier;
    private final Optional<Condition> m_condition;

    public boolean canAdd(Part inOther)
    {
      if(m_type != inOther.m_type)
        return false;

      if(!m_condition.equals(inOther.m_condition))
        return false;

      return true;
    }

    public Part add(Part inOther)
    {
      if(!canAdd(inOther))
        throw new IllegalArgumentException(
            "cannot add value " + inOther + " to " + this);

      if(m_type.stacks())
        return new Part(m_type, m_modifier + inOther.m_modifier, m_condition);

      if(m_modifier >= inOther.m_modifier)
        return this;

      return inOther;
    }

    public ModifierProto.Modifier toProto()
    {
      ModifierProto.Modifier.Builder builder =
          ModifierProto.Modifier.newBuilder()
              .setBaseValue(m_modifier)
              .setType(m_type.toProto());

      if(m_condition.isPresent())
        builder.setCondition(m_condition.get().toString());

      return builder.build();
    }

    public static Part fromProto(ModifierProto.Modifier proto)
    {
      return new Part(Type.fromProto(proto.getType()),
                      proto.getBaseValue(),
                      proto.hasCondition()
                          ? Condition.PARSER.parse(proto.getCondition())
                          : Optional.<Condition>absent());
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

      return result.toString();
    }


    @Override
    public boolean equals(Object inOther)
    {
      if(this == inOther)
        return true;

      if(inOther == null || getClass() != inOther.getClass())
        return false;

      final Part part = (Part)inOther;

      if(m_modifier != part.m_modifier)
        return false;
      if(m_type != part.m_type)
        return false;

      return m_condition.equals(part.m_condition);
    }

    @Override
    public int hashCode()
    {
      int result = m_type.hashCode();
      result = 31 * result + m_modifier;
      result = 31 * result + m_condition.hashCode();

      return result;
    }
  }

  public static class Builder
  {
    private Builder() {}

    private Builder(Collection<Part> inParts)
    {
      m_parts.addAll(inParts);
    }

    private List<Part> m_parts = new ArrayList<>();

    public Builder add(int inModifier)
    {
      return add(Type.GENERAL, inModifier);
    }

    public Builder add(Type inType, int inModifier)
    {
      m_parts.add(new Part(inType, inModifier));

      return this;
    }

    public Builder add(Type inType, int inModifier, Condition inCondition)
    {
      m_parts.add(new Part(inType, inModifier, inCondition));

      return this;
    }

    public Builder add(Type inType, int inModifier,
                       Optional<Condition> inCondition)
    {
      m_parts.add(new Part(inType, inModifier, inCondition));

      return this;
    }

    public Builder parametrize(Map<String, String> inParameters)
    {
      Evaluator evaluator = new Evaluator(inParameters);
      List<Part> parts = new ArrayList<>();
      for(Part part : m_parts)
        if(part.m_condition.isPresent())
          parts.add(new Part(part.m_type, part.m_modifier,
                             Condition.PARSER.parse(evaluator.evaluate(
                                 part.m_condition.get().toString()))));
        else
          parts.add(part);

      m_parts = parts;

      return this;
    }

    public Builder add(Modifier inModifier)
    {
      m_parts.addAll(inModifier.m_parts);

      return this;
    }

    public Builder multiply(int inFactor)
    {
      List<Part> multiplied = new ArrayList<>(m_parts.size());
      for(Part part : m_parts)
        multiplied.add(new Part(part.m_type, part.m_modifier * inFactor,
                                part.m_condition));

      m_parts = multiplied;

      return this;
    }

    public boolean isEmpty()
    {
      return m_parts.isEmpty();
    }

    public Builder with(Monster inMonster)
    {
      List<Part> parts = new ArrayList<>(m_parts.size());
      for(Part part : m_parts)
      {
        if(part.m_condition.isPresent())
        {
          Optional<Boolean> check = part.m_condition.get().check(inMonster);
          if(!check.isPresent() || check.get())
            parts.add(new Part(part.m_type, part.m_modifier));
        }
        else
          parts.add(part);
      }

      m_parts = parts;

      return this;
    }

    public Builder without(Type inType)
    {
      for(Iterator<Part> i = m_parts.iterator(); i.hasNext(); )
      {
        Part part = i.next();
        if(part.m_type == inType)
          i.remove();
      }

      return this;
    }

    public Builder fromProto(ModifierProto inProto)
    {
      for(ModifierProto.Modifier modifier : inProto.getModifierList())
        m_parts.add(Part.fromProto(modifier));

      return this;
    }

    public Modifier build()
    {
      return new Modifier(simplify(m_parts));
    }

    private static List<Part> simplify(List<Part> inParts)
    {
      Multimap<Type, Part> partsByType = ArrayListMultimap.create();
      for(Part part : inParts)
        if(part.m_modifier != 0)
          partsByType.put(part.m_type, part);

      List<Part> parts = new ArrayList<>();
      for(Type type : partsByType.keySet())
        parts.addAll(simplifySingleType(partsByType.get(type)));

      return parts;
    }

    private static Collection<Part> simplifySingleType(Collection<Part> inParts)
    {
      if(inParts.size() <= 1)
        return inParts;

      List<Part> parts = new ArrayList<>();
      for(Part part : inParts)
        addSimplified(parts, part);

      return parts;
    }

    private static void addSimplified(List<Part> ioParts, Part inPart)
    {
      if(ioParts.isEmpty())
      {
        ioParts.add(inPart);
        return;
      }

      for(int i = 0; i < ioParts.size(); i++)
        if(ioParts.get(i).canAdd(inPart))
        {
          ioParts.set(i, ioParts.get(i).add(inPart));
          return;
        }

      ioParts.add(inPart);
    }
  }

  /** Create a default modifier. */
  private Modifier(Collection<Part> inParts)
  {
    m_parts.addAll(inParts);
  }

  /** The types of modifiers available. */
  private static final String TYPES = Strings.PIPE_JOINER.join(Type.names());
  public static final Modifier EMPTY = Modifier.newBuilder().build();
  public static final Modifier ONE =
      Modifier.newBuilder().add(Type.GENERAL, 1).build();

  private final List<Part> m_parts = new ArrayList<>();

  /**
   * Get the value of the modifier, ignoring additional modifiers.
   *
   * @return      the requested valu
   */
  /*public int getModifier()
  {
    return m_modifier;
  }*/

  public int totalModifier()
  {
    int total = 0;
    for(Part part : m_parts)
      total += part.m_modifier;

    return total;
  }

  public int unconditionalModifier()
  {
    int total = 0;
    for(Part part : m_parts)
      if(!part.m_condition.isPresent())
        total += part.m_modifier;

    return total;
  }

  public int low()
  {
    return unconditionalModifier();
  }

  public int high()
  {
    return totalModifier();
  }

  /*
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
  */

  /*
  public Modifier with(Monster inMonster)
  {
    Optional<Modifier> modifier = with(Optional.of(this), inMonster);
    if(modifier.isPresent())
      return modifier.get();

    return new Modifier();
  }
  */

  /*
  private static Optional<Modifier> with(Optional<Modifier> inModifier,
                                         Monster inMonster)
  {
    if(!inModifier.isPresent())
      return Optional.absent();

    Optional<Modifier> next = with(inModifier.get().m_next, inMonster);
    if(inModifier.get().m_condition.isPresent())
    {
      Optional<Boolean> checked =
          inModifier.get().m_condition.get().check(inMonster);
      if(checked.isPresent())
      {
        if(checked.get())
          return Optional.of(new Modifier(inModifier.get().m_modifier,
                                          Optional.<Condition>absent(), next,
                                          inModifier.get().m_type));
        else
          return next;
      }
    }

    // Current modifier does not change, but maybe mext does.
    if(inModifier.get().m_next.equals(next))
      return Optional.of(inModifier.get());
    else
      return Optional.of(new Modifier(inModifier.get().m_modifier,
                                      inModifier.get().m_condition, next,
                                      inModifier.get().m_type));
  }
  */

  public boolean hasValue()
  {
    for(Part part : m_parts)
      if(part.m_modifier != 0)
        return true;

    return false;
  }

  /*
  public Modifier without(Type inType)
  {
    if(m_type == inType) {
      if (m_next.isPresent())
        return m_next.get().without(inType);

      // Nothing left.
      return new Modifier();
    }

    Optional<Modifier> next = m_next.isPresent()
        ? Optional.of(m_next.get().without(inType))
        : Optional.<Modifier>absent();

    if(next.equals(m_next))
      return this;

    return new Modifier(m_modifier, m_condition, next, m_type);
  }
  */

  /**
   * Get the type of the modifier.
   *
   * @return the modifier type
   */
  /*
  public Type getType()
  {
    return m_type;
  }
  */

  /**
   * Get the condition of the modifier, if any.
   *
   * @return the condition
   */
  /*
  public Optional<String> getCondition()
  {
    return m_condition.isPresent()
        ? Optional.of(m_condition.get().toString())
        : Optional.<String>absent();
  }
  */

  /*
  public boolean hasCondition()
  {
    return m_condition.isPresent();
  }
  */

  public boolean hasAnyCondition()
  {
    for(Part part : m_parts)
      if(part.m_condition.isPresent())
        return true;

    return false;
  }

  /**
   * Get the next modifier if there are chained modifiers.
   *
   * @return the next modifier
   */
  /*
  public Optional<Modifier> getNext()
  {
    return m_next;
  }
  */

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
    return Strings.COMMA_JOINER.join(m_parts);
  }

  @Override
  public ModifierProto toProto()
  {
    ModifierProto.Builder builder = ModifierProto.newBuilder();
    for(Part part : m_parts)
      builder.addModifier(part.toProto());

    return builder.build();
  }

  /*
  public Modifier add(int inModifier, Type inType) {
    return (Modifier) add(new Modifier(inModifier, inType));
  }
  */

  @Override
  public Value.Arithmetic<ModifierProto>
    add(Value.Arithmetic<ModifierProto> inValue)
  {
    if(!(inValue instanceof Modifier))
      return this;

    return toBuilder().add((Modifier)inValue).build();
  }

  @Override
  public Value.Arithmetic<ModifierProto> multiply(int inFactor)
  {
    return toBuilder().multiply(inFactor).build();
  }

  @Override
  public Value.Arithmetic<ModifierProto> multiply(Rational inFactor)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  /**
   * Create a new modifier with the values from the given proto.
   *
   * @param inProto the proto to read the values from
   * @return the newly created critical
   */
  public static Modifier fromProto(ModifierProto inProto)
  {
    return Modifier.newBuilder().fromProto(inProto).build();
  }

  @Override
  public boolean canAdd(Value.Arithmetic<ModifierProto> inValue)
  {
    return inValue instanceof Modifier;
  }

  public static Builder newBuilder()
  {
    return new Builder();
  }

  public Builder toBuilder()
  {
    return new Builder(m_parts);
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
    }

    /** Test adding. */
    @org.junit.Test
    public void add()
    {
      Modifier.Builder builder = Modifier.newBuilder();
      builder.add(Type.ARMOR, 23);
      builder.add(Type.GENERAL, 42);
      builder.add(Type.ARMOR, 1);
      builder.add(Type.GENERAL, 1);
      builder.add(Type.ARMOR, 1, Condition.PARSER.parse("maybe"));
      builder.add(Type.GENERAL, 1, Condition.PARSER.parse("maybe"));

      assertEquals("modifier", "+23 armor, +43, +1 armor if maybe, +1 if maybe",
                   builder.build().toString());
    }

    @org.junit.Test
    public void simplify()
    {
      assertTrue("empty", Modifier.Builder.simplify(
          Collections.<Part>emptyList()).isEmpty());

      assertEquals("single", ImmutableList.of(new Part(Type.GENERAL, 10)),
                   Modifier.Builder.simplify(
                       ImmutableList.of(new Part(Type.GENERAL, 10))));
      assertEquals("multiple", "[+30, +3 armor, +5 dodge]",
                   Modifier.Builder.simplify(
                       ImmutableList.of(new Part(Type.GENERAL, 10),
                                        new Part(Type.ARMOR, 3),
                                        new Part(Type.GENERAL, 20),
                                        new Part(Type.ARMOR, 3),
                                        new Part(Type.DODGE, 5))).toString());
    }

    @org.junit.Test
    public void addSimplified()
    {
      List<Part> parts = new ArrayList<>();
      Modifier.Builder.addSimplified(parts,new Part(Type.GENERAL, 10));
      assertEquals("single", "[+10]", parts.toString());

      Modifier.Builder.addSimplified(parts,new Part(Type.GENERAL, 13));
      assertEquals("non stacking", "[+23]", parts.toString());

      Modifier.Builder.addSimplified(parts,new Part(Type.ARMOR, 1));
      assertEquals("other type", "[+23, +1 armor]", parts.toString());

      Modifier.Builder.addSimplified(parts,new Part(Type.ARMOR, 2));
      assertEquals("non stacking higher", "[+23, +2 armor]", parts.toString());

      Modifier.Builder.addSimplified(parts,new Part(Type.ARMOR, 1));
      assertEquals("non stacking lower", "[+23, +2 armor]", parts.toString());
    }

    @org.junit.Test
    public void partCanAdd()
    {
      assertTrue("simple", new Part(Type.GENERAL, 10).canAdd(
          new Part(Type.GENERAL, 42)));

      assertFalse("wrong type", new Part(Type.GENERAL, 10).canAdd(
          new Part(Type.ABILITY, 10)));
      assertFalse("mismatched conditionals",
                  new Part(Type.GENERAL, 10, new Condition("first")).canAdd(
                      new Part(Type.GENERAL, 10)));
      assertFalse("mismatched conditionals",
                  new Part(Type.GENERAL, 10).canAdd(
                      new Part(Type.GENERAL, 10, new Condition("first"))));
      assertTrue("mismatched conditionals",
                 new Part(Type.GENERAL, 10, new Condition("first")).canAdd(
                      new Part(Type.GENERAL, 10, new Condition("first"))));
    }

    @org.junit.Test
    public void partAdd()
    {
      try
      {
        new Part(Type.GENERAL, 10).add(new Part(Type.ABILITY, 10));
        fail("expeted exception to be thrown");
      }
      catch(IllegalArgumentException e)
      {
        // this is expected
      }

      assertEquals("simple", "+42",
                   new Part(Type.GENERAL, 10).add(new Part(Type.GENERAL, 32))
                       .toString());
      assertEquals("stacks", "+23 armor",
                   new Part(Type.ARMOR, 10).add(new Part(Type.ARMOR, 23))
                       .toString());
      assertEquals("stacks", "+23 shield",
                   new Part(Type.SHIELD, 23).add(new Part(Type.SHIELD, 10))
                       .toString());
      assertEquals("stacks", "+23 shield",
                   new Part(Type.SHIELD, 23).add(new Part(Type.SHIELD, 23))
                       .toString());
    }
  }
}
