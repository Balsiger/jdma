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
import java.util.Collections;
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Values.DamageProto;
import net.ixitxachitls.dma.values.enums.Named;
import net.ixitxachitls.dma.values.enums.Proto;
import net.ixitxachitls.util.Strings;

/**
 * A damage value.
 *
 * @file   NewDamage.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */
public class Damage extends Value.Arithmetic<DamageProto>
{
  /** The parser for damage values. */
  public static class DamageParser extends Parser<Damage>
  {
    /** Create the parser. */
    public DamageParser()
    {
      super(0);
    }

    @Override
    public Optional<Damage> doParse(String... inValues)
    {
      return parse(Strings.COMMA_SPLITTER.splitToList
                   (Strings.COMMA_JOINER.join(inValues)));
    }


    /**
     *  Parse a list of values.
     *
     * @param inValues the values to parse
     * @return the parsed damage value, if any
     */
    private Optional<Damage> parse(List<String> inValues)
    {
      List<String> values = new ArrayList<>(inValues);
      Collections.reverse(values);
      Optional<Damage> result = Optional.absent();
      for(String value : values)
      {
        String []parts =
          Strings.getPatterns(value, "^([0-9\\-+\\sd]+)\\s*("
                              + Strings.PIPE_JOINER.join(Type.names()) + ")?"
                              + "(?:\\s+plus\\s+(.*))?$");
        if(parts == null || parts.length != 3)
          return Optional.absent();

        if(parts[0] == null)
          return Optional.absent();

        Optional<Dice> dice = Dice.PARSER.parse(parts[0]);
        if(!dice.isPresent())
          return Optional.absent();

        Optional<Type> type;
        if(parts[1] != null)
          type = Type.fromString(parts[1]);
        else
          type = Optional.absent();

        Optional<String> effect = Optional.fromNullable(parts[2]);

        result = Optional.of(new Damage(dice.get(), type, result, effect));
      }

      return result;
    }
  }

  /**
   * Create a damage value.
   *
   * @param inDice the dice for the damage
   * @param inType the type of damage done
   * @param inOther an optional further damage linked with this one
   * @param inEffect an optional effect the damage has
   */
  public Damage(Dice inDice, Optional<Type> inType,
                Optional<Damage> inOther, Optional<String> inEffect)
  {
    m_dice = inDice;
    m_type = inType;
    m_other = inOther;
    m_effect = inEffect;
  }

  /**
   * Create a damage value with dice and type only.
   *
   * @param inDice the damage dice
   * @param inType the damage type
   */
  public Damage(Dice inDice, Type inType)
  {
    this(inDice, Optional.of(inType), Optional.<Damage>absent(),
         Optional.<String>absent());
  }

  /**
   * Create a damage with damage dice only.
   *
   * @param inDice the damage dice
   */
  public Damage(Dice inDice)
  {
    this(inDice, Optional.<Type>absent(), Optional.<Damage>absent(),
         Optional.<String>absent());
  }

  /** The possible damage types. */
  public enum Type
    implements Named, Proto<DamageProto.Damage.Type>
  {
    /** Fire damage. */
    FIRE("fire", DamageProto.Damage.Type.FIRE),

    /** Electrical damage. */
    ELECTRICAL("electrical", DamageProto.Damage.Type.ELECTRICAL),

    /** Sonic damage. */
    SONIC("sonic", DamageProto.Damage.Type.SONIC),

    /** Water damage. */
    WATER("water", DamageProto.Damage.Type.WATER),

    /** Acid damage. */
    ACID("acid", DamageProto.Damage.Type.ACID),

    /** Holy damage. */
    HOLY("holy", DamageProto.Damage.Type.HOLY),

    /** Negative Energy damage. */
    NEGATIVE_ENERGY("negative energy", DamageProto.Damage.Type.NEGATIVE_ENERGY),

    /** Nonlethal damage. */
    NONLETHAL("nonlethal", DamageProto.Damage.Type.NONLETHAL),

    /** Cold damage. */
    COLD("cold", DamageProto.Damage.Type.COLD),

    /** Strength damage. */
    STR("Str", DamageProto.Damage.Type.STR),

    /** Dexterity damage. */
    DEX("Dex", DamageProto.Damage.Type.DEX),

    /** Constitution damage. */
    CON("Con", DamageProto.Damage.Type.CON),

    /** Intelligence damage. */
    INT("Int", DamageProto.Damage.Type.INT),

    /** Wisdom damage. */
    WIS("Wis", DamageProto.Damage.Type.WIS),

    /** Charisma damage. */
    CHA("Cha", DamageProto.Damage.Type.CHA);

    /** The value's name. */
    private String m_name;

    /** The enum proto value. */
    private DamageProto.Damage.Type m_proto;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the proto enum value
     */
    private Type(String inName, DamageProto.Damage.Type inProto)
    {
      m_name = inName;
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

    @Override
    public DamageProto.Damage.Type toProto()
    {
      return m_proto;
    }

    /**
     * Convert the proto enum to an enum value.
     *
     * @param inProto  the proto value
     * @return the converted enum value
     */
    public static Type fromProto(DamageProto.Damage.Type inProto)
    {
      for(Type type : values())
        if(type.m_proto == inProto)
          return type;

      throw new IllegalStateException("cannot convert damage type enum: "
        + inProto);
    }

    /** All the possible names for damage types.
     *
     * @return the possible damage type names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();

      for(Type type : values())
        names.add(type.getName());

      return names;
    }

    /** Create a damage type from a string.
     *
     * @param inValue the value to convert from
     * @return the damage type, if it could be parsed
     */
    public static Optional<Type> fromString(String inValue)
    {
      for(Type type : values())
        if(type.getName().equalsIgnoreCase(inValue))
          return Optional.of(type);

      return Optional.absent();
    }
  }

  /** The base damage. */
  protected final Dice m_dice;

  /** The kind of base damage, if any. */
  protected final Optional<Type> m_type;

  /** Additional damages, if any. */
  protected final Optional<Damage> m_other;

  /** Additional effects together with the damage, if any. */
  protected final Optional<String> m_effect;

  /** The parser for parsing damages. */
  public static final Parser<Damage> PARSER = new DamageParser();

  /**
   * Get the number of base dices of damage.
   *
   * @return      an integer with the number of dice
   */
  public int getBaseNumber()
  {
    return m_dice.getNumber();
  }

  /**
   * Get the type of base dices of damage.
   *
   * @return      an integer with the type of dice
   */
  public int getBaseDice()
  {
    return m_dice.getDice();
  }

  /**
   * Get the type of base dices of damage.
   *
   * @return      an integer with the type of dice
   */
  public int getBaseModifier()
  {
    return m_dice.getModifier();
  }

  /**
   * Get the type of damage.
   *
   * @return      the damage type (or null if none)
   */
  public Optional<Type> getType()
  {
    return m_type;
  }

  /**
   * Get the effect that applies with the damage, if any.
   *
   * @return      the applied effect
   */
  public Optional<String> getEffect()
  {
    return m_effect;
  }

  /**
   * Get the next damage in the chain, if any.
   *
   * @return      the next damage or null if none any more
   */
  public Optional<Damage> next()
  {
    return m_other;
  }

  @Override
  public String toString()
  {
    return m_dice
      + (m_type.isPresent() ? " " + m_type.get() : "")
      + (m_effect.isPresent() ? " plus " + m_effect.get() : "")
      + (m_other.isPresent() ? ", " + m_other.get() : "");
  }

  /**
   * Create a proto representation from the damage.
   *
   * @return  the proto created
   */
  @Override
  public DamageProto toProto()
  {
    DamageProto.Builder builder = DamageProto.newBuilder();

    addToProto(builder);

    return builder.build();
  }

  /**
   * Add the damage and all its other damages to the given builder.
   *
   * @param inBuilder the proto builder to add to
   */
  private void addToProto(DamageProto.Builder inBuilder)
  {
    DamageProto.Damage.Builder damage = DamageProto.Damage.newBuilder();
    damage.setBase(m_dice.toProto());
    if(m_type.isPresent())
      damage.setType(m_type.get().toProto());
    if(m_effect.isPresent())
      damage.setEffect(m_effect.get());

    inBuilder.addDamage(damage.build());

    if(m_other.isPresent())
      m_other.get().addToProto(inBuilder);
  }

    /**
   * Create a new damage value from the given proto.
   *
   * @param inProto  the proto to take values from
   * @return a newly create damage
   */
  public static Damage fromProto(DamageProto inProto)
  {
    List<DamageProto.Damage> protos = new ArrayList<>(inProto.getDamageList());
    Collections.reverse(protos);

    Damage result = null;
    for(DamageProto.Damage proto : protos)
      result = fromProto(proto, Optional.fromNullable(result));

    return result;
  }

  /** Convert a damage proto into a damage value.
   *
   * @param inProto the proto to convert
   * @param inNext  an optional next damage value
   * @return the converted damage value
   */
  private static Damage fromProto(DamageProto.Damage inProto,
                                  Optional<Damage> inNext)
  {
    Dice dice = Dice.fromProto(inProto.getBase());

    Optional<Type> type;
    if(inProto.hasType())
      type = Optional.of(Type.fromProto(inProto.getType()));
    else
      type = Optional.absent();

    Optional<String> effect;
    if(inProto.hasEffect())
      effect = Optional.of(inProto.getEffect());
    else
      effect = Optional.absent();

    return new Damage(dice, type, inNext, effect);
  }

  @Override
  public Value.Arithmetic<DamageProto>
    add(Value.Arithmetic<DamageProto> inValue)
  {
    if(inValue == null)
      return this;

    if(!(inValue instanceof Damage))
      throw new IllegalArgumentException("can only add another damage value");

    Damage value = (Damage)inValue;
    if(value.m_type.equals(m_type)
       && (value.m_dice.getDice() == m_dice.getDice()
           || value.m_dice.getNumber() == 0 || m_dice.getNumber() == 0
           || value.m_dice.getDice() <= 1 || m_dice.getDice() <= 1))
      if(!m_other.isPresent())
        return new Damage(m_dice.add(value.m_dice), m_type, value.m_other,
                             Strings.concatenate(m_effect, value.m_effect,
                                                 " "));
      else if(!value.m_other.isPresent())
        return new Damage(m_dice.add(value.m_dice), m_type, m_other,
                             Strings.concatenate(m_effect, value.m_effect,
                                                 " "));
      else
        return new Damage(m_dice.add(value.m_dice), m_type,
                             Optional.of((Damage)m_other.get()
                                         .add(value.m_other.get())),
                             Strings.concatenate(m_effect, value.m_effect,
                                                 " "));
    if(!m_other.isPresent())
      return new Damage(m_dice, m_type, Optional.of(value), m_effect);

    return new Damage(m_dice, m_type,
                         Optional.of((Damage)m_other.get().add(value)),
                         m_effect);
  }

  @Override
  public boolean canAdd(Value.Arithmetic<DamageProto> inValue)
  {
    return inValue instanceof Damage;
  }

  @Override
  public Value.Arithmetic<DamageProto> multiply(int inFactor)
  {
    return new Damage(m_dice.multiply(inFactor),
                         m_type,
                         m_other.isPresent()
                           ? Optional.of((Damage)
                                         m_other.get().multiply(inFactor))
                           : m_other,
                         m_effect);
  }

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Parsing tests. */
    @org.junit.Test
    public void parse()
    {
      assertEquals("parsing", "1d4", PARSER.parse("1d4").get().toString());
      assertEquals("parsing", "1d4 +2",
                   PARSER.parse(" 1d4   \n+ 2").get().toString());
      assertEquals("parsing", "1d4 -3",
                   PARSER.parse("  1d4  -  3").get().toString());
      assertEquals("parsing", "5d12", PARSER.parse("5d12").get().toString());
      assertEquals("parsing", "+3", PARSER.parse("  +3  ").get().toString());
      assertEquals("parsing", "+3 fire plus poison",
                   PARSER.parse("  +3  fire plus poison").get().toString());
      assertEquals("parsing", "1d4, 1d6, 1d8",
                   PARSER.parse("1d4,  1d6,  1d8").get().toString());
      assertEquals("parsing", "1d4 fire, 1d6 electrical, 1d8 plus poison",
                   PARSER.parse("1d4 fire,  1d6 electrical,  1d8 plus poison")
                   .get().toString());
      assertFalse("parsing", PARSER.parse("1d").isPresent());
      assertFalse("parsing", PARSER.parse("d5").isPresent());
      assertFalse("parsing", PARSER.parse("1 d 5 + 2").isPresent());
      assertFalse("parsing", PARSER.parse("1d4 ++3").isPresent());
      assertFalse("parsing", PARSER.parse("1d2 +").isPresent());
      assertFalse("parsing", PARSER.parse("2 - 3").isPresent());
      assertFalse("parsing", PARSER.parse("1d4,").isPresent());
      assertFalse("parsing", PARSER.parse("fire,").isPresent());
      assertFalse("parsing", PARSER.parse("1 plus poison,").isPresent());
    }
  }
}
