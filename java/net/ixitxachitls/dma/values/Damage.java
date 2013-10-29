/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.values;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.proto.Values.DamageProto;
import net.ixitxachitls.input.ParseReader;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a damage and is capable of reading such damages
 * from a reader (and write it to a writer of course).
 *
 * @file          Damage.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Damage extends Value<Damage>
{
  //----------------------------------------------------------------- nested

  //----- type -------------------------------------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The possible damage types. */
  public enum Type
    implements EnumSelection.Named, EnumSelection.Proto<DamageProto.Damage.Type>
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
      m_name = ValueGroup.constant("damage.types", inName);
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
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Damage --------------------------------

  /**
   * Construct the damage object as undefined.
   */
  public Damage()
  {
    withEditType("name");
    withTemplate("damage");
  }

  //........................................................................
  //-------------------------------- Damage --------------------------------

  /**
   * Create a damage from the given dice.
   *
   * @param       inDice the dice for the base damage
   */
  public Damage(Dice inDice)
  {
    m_base = inDice;
  }


  //........................................................................


  //-------------------------------- create --------------------------------

  /**
   * Create a new list with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   */
  @Override
  public Damage create()
  {
    return super.create(new Damage());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The base damage. */
  protected Dice m_base = new Dice();

  /** The kind of base damage, if any. */
  protected EnumSelection<Type> m_type =
    new EnumSelection<Type>(Type.class);

  /** Additional damages, if any. */
  protected @Nullable Damage m_other = null;

  /** Additional effects together with the damage, if any. */
  protected @Nullable String m_effect = null;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //---------------------------- getBaseNumber -----------------------------

  /**
   * Get the number of base dices of damage.
   *
   * @return      an integer with the number of dice
   *
   */
  public int getBaseNumber()
  {
    return m_base.getNumber();
  }

  //........................................................................
  //---------------------------- getBaseDice -------------------------------

  /**
   * Get the type of base dices of damage.
   *
   * @return      an integer with the type of dice
   *
   */
  public int getBaseDice()
  {
    return m_base.getDice();
  }

  //........................................................................
  //-------------------------- getBaseModifier -----------------------------

  /**
   * Get the type of base dices of damage.
   *
   * @return      an integer with the type of dice
   *
   */
  public int getBaseModifier()
  {
    return m_base.getModifier();
  }

  //........................................................................
  //------------------------------ getType ---------------------------------

  /**
   * Get the type of damage.
   *
   * @return      the damage type (or null if none)
   *
   */
  public @Nullable Type getType()
  {
    if(m_type.isDefined())
      return m_type.getSelected();

    return null;
  }

  //........................................................................
  //------------------------------ getEffect -------------------------------

  /**
   * Get the effect that applies with the damage, if any.
   *
   * @return      the applied effect
   *
   */
  public @Nullable String getEffect()
  {
    return m_effect;
  }

  //........................................................................

  //--------------------------------- next ---------------------------------

  /**
   * Get the next damage in the chain, if any.
   *
   * @return      the next damage or null if none any more
   *
   */
  public @Nullable Damage next()
  {
    return m_other;
  }

  //........................................................................
  //------------------------------ addDamages ------------------------------

  /**
   * Add all the damage stored in this damage to the list.
   *
   * @param       ioTypes the list to add the damages to
   *
   */
  // public void addDamages(Set<String> ioTypes)
  // {
  //   if(m_other != null)
  //     m_other.addDamages(ioTypes);

  //   ioTypes.add(m_base.toString());
  // }

  //........................................................................
  //---------------------------- addDamageTypes ----------------------------

  /**
   * Add all the damage types stored in this damage to the list.
   *
   * @param       ioTypes the list to add the types to
   *
   */
  // public void addDamageTypes(Set<String> ioTypes)
  // {
  //   if(m_other != null)
  //     m_other.addDamageTypes(ioTypes);

  //   if(m_type.isDefined())
  //     ioTypes.add(m_type.toString());
  // }

  //........................................................................

  //----------------------------- doToString ------------------------------

  /**
   * Convert the value to a string.
   *
   * @return      a String representation, depending on the kind given
   *
   */
  @Override
  public String doToString()
  {
    return
      m_base.toString()
      + (m_type.isDefined() ? " " + m_type : "")
      + (m_effect != null ? " plus " + m_effect : "")
      + (m_other != null ? ", " + m_other : "");
  }

  //........................................................................

  //------------------------------ isDefined -------------------------------

  /**
   * Check if the value is defined or not.
   *
   * @return      true if the value is defined, false if not
   *
   */
  @Override
  public boolean isDefined()
  {
    return m_base.isDefined();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- doRead ---------------------------------

  /**
   * Read the value from the reader and replace the current one.
   *
   * @param       inReader the reader to read from
   *
   * @return      true if read, false if not
   *
   */
  @Override
  protected boolean doRead(ParseReader inReader)
  {
    // read the base damage
    if(!m_base.doRead(inReader))
      return false;

    // try to read the type
    m_type.doRead(inReader);

    // try to read an effect, if any
    ParseReader.Position pos = inReader.getPosition();
    if(inReader.expect("plus"))
    {
      m_effect = inReader.read("),").trim().replaceAll("\\s+", " ");

      if(m_effect.length() == 0)
      {
        m_effect = null;

        inReader.seek(pos);

        return true;
      }
    }

    // now there might be some other damage types, thus we try to read them
    // as well
    pos = inReader.getPosition();

    if(inReader.expect(","))
    {
      m_other = read(inReader);
      if(m_other == null)
      {
        inReader.seek(pos);

        return true;
      }
    }

    return true;
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the current and given value and return it. The current value is not
   * changed.
   *
   * @param       inValue the value to add to this one
   *
   * @return      the added values
   *
   */
  @Override
  public Damage add(Damage inValue)
  {
    String effect = m_effect;
    if(inValue.m_effect != null)
      if(effect != null)
        effect += " " + inValue.m_effect;
      else
        effect = inValue.m_effect;

    if(inValue.m_type.getSelected() == m_type.getSelected()
       && (inValue.m_base.getDice() == m_base.getDice()
           || inValue.m_base.getNumber() == 0 || m_base.getNumber() == 0
           || inValue.m_base.getDice() <= 1 || m_base.getDice() <= 1))
      if(m_other == null)
        return as(m_base.add(inValue.m_base), m_type, inValue.m_other, effect);
      else if(inValue.m_other == null)
        return as(m_base.add(inValue.m_base), m_type, m_other, effect);
      else
        return as(m_base.add(inValue.m_base), m_type,
                  m_other.add(inValue.m_other), effect);

    if(m_other == null)
      return as(m_base, m_type, inValue, effect);

    return as(m_base, m_type, m_other.add(inValue), effect);
  }

  //........................................................................
  //---------------------------------- as ----------------------------------

  /**
   * Create a new damage value with the new data.
   *
   * @param     inDice the damage dice
   * @param     inType the type of the damage
   *
   * @return    the newly created value
   *
   */
  public Damage as(Dice inDice, EnumSelection<Type> inType)
  {
    return as(inDice, inType, null);
  }

  //........................................................................
  //---------------------------------- as ----------------------------------

  /**
   * Create a new damage value with the new data.
   *
   * @param     inDice   the damage dice
   * @param     inType   the type of the damage
   * @param     inOther  other additional damage, if any
   *
   * @return    the newly created value
   *
   */
  public Damage as(Dice inDice, EnumSelection<Type> inType,
                   @Nullable Damage inOther)
  {
    Damage damage = create();

    damage.m_base = inDice;
    damage.m_type = inType;
    damage.m_other = inOther;

    return damage;
  }

  //........................................................................
  //---------------------------------- as ----------------------------------

  /**
   * Create a new damage value with the new data.
   *
   * @param     inDice   the damage dice
   * @param     inType   the type of the damage
   * @param     inOther  other additional damage, if any
   * @param     inEffect the effects, if any
   *
   * @return    the newly created value
   *
   */
  public Damage as(Dice inDice, EnumSelection<Type> inType,
                   @Nullable Damage inOther, @Nullable String inEffect)
  {
    Damage damage = create();

    damage.m_base = inDice;
    damage.m_type = inType;
    damage.m_other = inOther;
    damage.m_effect = inEffect;

    return damage;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  /**
   * Create a new damage value from the given proto.
   *
   * @param inProto  the proto to take values from
   * @return a newly create damage
   */
  public Damage fromProto(DamageProto inProto)
  {
    Damage result = null;
    Damage next = null;
    for(DamageProto.Damage damage : inProto.getDamageList())
    {
      if(result == null)
      {
        result = create();
        next = result;
      }
      else
      {
        next.m_other = create();
        next = next.m_other;
      }

      next.m_base = m_base.fromProto(damage.getBase());

      if(damage.hasType())
        next.m_type = m_type.as(Type.fromProto(damage.getType()));

      if(damage.hasEffect())
        next.m_effect = damage.getEffect();
    }

    return result;
  }

  /**
   * Create a proto representation from the damage.
   *
   * @return  the proto created
   */
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
    damage.setBase(m_base.toProto());
    if(m_type.isDefined())
      damage.setType(m_type.getSelected().toProto());
    if(m_effect != null)
      damage.setEffect(m_effect);

    inBuilder.addDamage(damage.build());

    if(m_other != null)
      m_other.addToProto(inBuilder);
  }

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Test of init. */
    @org.junit.Test
    public void init()
    {
      Damage damage = new Damage();

      // undefined value
      assertFalse("not undefined at start", damage.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   damage.toString());

      // define the value, otherwise the following test will fail
      damage.m_base = new Dice(2, 10, +0);

      assertEquals("base", 2, damage.getBaseNumber());
      assertEquals("dice", 10, damage.getBaseDice());
      assertEquals("modifier", 0, damage.getBaseModifier());
      assertNull("type", damage.getType());
      assertNull("next", damage.next());

      // Set<String> damages = new java.util.TreeSet<String>();
      // damage.addDamages(damages);

      // java.util.Iterator<String> i = damages.iterator();
      // assertEquals("size", 1, damages.size());
      // assertEquals("element", "2d10", i.next());
      // assertFalse("last", i.hasNext());

      // damages.clear();
      // damage.addDamageTypes(damages);
      // assertEquals("size", 0, damages.size());

      // assertEquals("format", "\\link[/index/damages/2d10]{2d10}",
      //              damage.format(false).toString());

      Value.Test.createTest(damage);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "simple", "1d6", "1d6", null,
          "modifier", "1d6 +3", "1d6 +3", null,
          "whites", "\n   1   \nd  3    \n+2 ", "1d3 +2", " ",
          "modifier only", "-13", "-13", null,
          "positive only", "+13", "13", null,
          "type", "1d6 +2 fire", "1d6 +2 fire", null,
          "other", "1d6, 1d3", "1d6, 1d3", null,
          "other types", "1d6, 1d3 fire", "1d6, 1d3 fire", null,

          "others", "1d5 fire, 1d2 cold, 1d12, 1d20",
          "1d5 fire, 1d2 cold, 1d12, 1d20", null,

          "effect", "1d5 fire plus slime", "1d5 fire plus slime", null,
          "effect 2", "1d4 +2 plus slime", "1d4 +2 plus slime", null,
          "effect 3", "1d10 plus slime it", "1d10 plus slime it", null,

          "effect and others", "1d5 fire plus slime, 1d3 plus guru, 1d4",
          "1d5 fire plus slime, 1d3 plus guru, 1d4", null,

          "invalid", "a", null, "a",
          "empty", "", null, null,
          "other", "42a", "42", "a",
        };

      Value.Test.readTest(tests, new Damage());
    }

    //......................................................................
  }

  //........................................................................
}
