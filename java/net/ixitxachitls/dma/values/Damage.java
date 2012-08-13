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

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.dma.output.soy.SoyValue;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Link;

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
public class Damage extends Value<Damage>
{
  //----------------------------------------------------------------- nested

  //----- type -------------------------------------------------------------

  /** The possible damage types. */
  public enum Type implements EnumSelection.Named
  {
    /** Fire damage. */
    FIRE("fire"),

    /** Electrical damage. */
    ELECTRICAL("electrical"),

    /** Sonic damage. */
    SONIC("sonic"),

    /** Water damage. */
    WATER("water"),

    /** Acid damage. */
    ACID("acid"),

    /** Holy damage. */
    HOLY("holy"),

    /** Nonlethal damage. */
    NONLETHAL("nonlethal"),

    /** Cold damage. */
    COLD("cold");

    /** The value's name. */
    private String m_name;

    /** Create the name.
     *
     * @param inName     the name of the value
     *
     */
    private Type(String inName)
    {
      m_name = ValueGroup.constant("damage.types", inName);
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

    /** Convert to a string.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
    {
      return m_name;
    }
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Damage --------------------------------

  /**
   * Construct the damage object as undefined.
   *
   */
  public Damage()
  {
    withEditType("name");
    withTemplate("damage");
  }

  //........................................................................

  //-------------------------------- create --------------------------------

  /**
   * Create a new list with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   *
   */
  @Override
  public @Nonnull Damage create()
  {
    return super.create(new Damage());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The base damage. */
  protected @Nonnull Dice m_base = new Dice();

  /** The kind of base damage, if any. */
  protected @Nonnull EnumSelection<Type> m_type =
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
  // public void addDamages(@Nonnull Set<String> ioTypes)
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
  // public void addDamageTypes(@Nonnull Set<String> ioTypes)
  // {
  //   if(m_other != null)
  //     m_other.addDamageTypes(ioTypes);

  //   if(m_type.isDefined())
  //     ioTypes.add(m_type.toString());
  // }

  //........................................................................

  //------------------------------- doFormat -------------------------------

  /**
   * Really to the formatting.
   *
   * @return      the command for setting the value
   *
   */
  @Override
  protected @Nonnull Command doFormat()
  {
    java.util.List<Object> commands = new ArrayList<Object>();

    Object command = m_base.format(false);
    if(m_indexBase != null)
      command = new Link(command, m_indexBase + "damages/" + m_base.getNumber()
                         + "d" + m_base.getDice());

    commands.add(command);

    if(m_type.isDefined())
    {
      commands.add(" ");
      command = m_type.format(false);
      if(m_indexBase != null)
        command = new Link(command, m_indexBase + "damagetypes/" + m_type);
      commands.add(command);
    }

    if(m_effect != null)
    {
      commands.add(" plus ");
      commands.add(m_effect);
    }

    if(m_other != null)
    {
      commands.add(", ");
      commands.add(m_other.format(false));
    }

    return new Command(commands.toArray());
  }

  //........................................................................
  //----------------------------- convertValue -----------------------------

  /**
   * Convert the value to a string, depending on the given kind.
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
  //----------------------------- collectData ------------------------------

  /**
   * Collect the data available for printing the value.
   *
   * @param       inEntry    the entry this value is in
   * @param       inRenderer the renderer to render sub values
   *
   * @return      the data as a map
   *
   */
  // @Override
  // public Map<String, Object> collectData(@Nonnull AbstractEntry inEntry,
  //                                        @Nonnull SoyRenderer inRenderer)
  // {
  //   Map<String, Object> data = super.collectData(inEntry, inRenderer);

  //   data.put("base", new SoyValue("base damage", m_base, inEntry, inRenderer));
  //   data.put("type", new SoyValue("damage type", m_type, inEntry, inRenderer));
  //   data.put("effect", m_effect);

  //   if(m_other != null)
  //     data.put("other", new SoyValue("other damage", m_other, inEntry,
  //                                    inRenderer));

  //   return data;
  // }

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

  //-------------------------------- addTo ---------------------------------

  /**
   * Add the given value to the beginning of the current one.
   *
   * @param       inValue the value to add to this one
   *
   * @undefined   IllegalArgumentException if given value is null
   * @undefined   UnsupportedOperationException if operation not allowed
   *
   */
  // public void addTo(Damage inValue)
  // {
  //   m_stored = true;
  //   if(isDefined())
  //   {
  //     if((inValue.m_type == null && m_type == null
  //         || (inValue.m_type != null && m_type != null
  //             && inValue.m_type.getSelected() == m_type.getSelected()))
  //        && (m_effect == null && inValue.m_effect == null
  //            || (m_effect != null && m_effect.equals(inValue.m_effect)))
  //        && (m_base.getDice() == inValue.m_base.getDice()
  //            || m_base.getDice() == 1 || inValue.m_base.getDice() == 1))
  //       m_base.set(m_base.getNumber() + inValue.m_base.getNumber(),
  //                  Math.max(m_base.getDice(), m_base.getDice()),
  //                  m_base.getModifier() + inValue.m_base.getModifier());
  //     else
  //       if(m_other == null)
  //         m_other = inValue.clone();
  //       else
  //         m_other.addTo(inValue);
  //   }
  //   else
  //   {
  //     m_base = (Dice)inValue.m_base.clone();
  //     m_type = (EnumSelection<Type>)inValue.m_type.clone();

  //     if(m_other != null)
  //       m_other = (Damage)inValue.m_other.clone();

  //     m_effect = inValue.m_effect;
  //   }
  // }

  //........................................................................

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
  protected boolean doRead(@Nonnull ParseReader inReader)
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

      if(m_effect == null || m_effect.length() == 0)
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
  public @Nonnull Damage add(@Nonnull Damage inValue)
  {
    if(inValue.m_type == m_type && inValue.m_base.getDice() == m_base.getDice())
      if(m_other == null)
        return as(m_base.add(inValue.m_base), m_type, inValue.m_other);
      else if(inValue.m_other == null)
        return as(m_base.add(inValue.m_base), m_type, m_other);
      else
        return as(m_base.add(inValue.m_base), m_type,
                  m_other.add(inValue.m_other));

    if(m_other == null)
      return as(m_base, m_type, inValue);

    return as(m_base, m_type, m_other.add(inValue));
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
  public Damage as(@Nonnull Dice inDice, EnumSelection<Type> inType)
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
  public Damage as(@Nonnull Dice inDice, EnumSelection<Type> inType,
                   @Nullable Damage inOther)
  {
    Damage damage = create();

    damage.m_base = inDice;
    damage.m_type = inType;
    damage.m_other = inOther;

    return damage;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
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
      assertEquals("undefined value not correct", "\\color{error}{$undefined$}",
                   damage.format(false).toString());

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
          "positive only", "+13", "+13", null,
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
          "other", "42a", "+42", "a",
        };

      Value.Test.readTest(tests, new Damage());
    }

    //......................................................................
  }

  //........................................................................
}
