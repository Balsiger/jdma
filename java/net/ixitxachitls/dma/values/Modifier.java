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
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.values.conditions.And;
import net.ixitxachitls.dma.values.conditions.Condition;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Emph;
import net.ixitxachitls.output.commands.Window;
import net.ixitxachitls.util.ArrayIterator;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a single modifier value.
 *
 * @file          Modifier.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Modifier extends Value<Modifier>
{
  //----------------------------------------------------------------- nested

  //----- type -------------------------------------------------------------

  /** The modifiers type. */
  public enum Type implements EnumSelection.Named
  {
    /** Doding stuff. */
    DODGE("dodge", true),

    /** Better armor. */
    ARMOR("armor", false),

    /** Some equipment benefit. */
    EQUIPMENT("equipment", false),

    /** A shield helping out. */
    SHIELD("shield", false),

    /** The general or standard type. */
    GENERAL("general", true),

    /** Modifier for natural armor. */
    NATURAL_ARMOR("natural armor", false),

    /** A modifier from an ability. */
    ABILITY("ability", true),

    /** A modifier according to size. */
    SIZE("size", false),

    /** A racial modifier. */
    RACIAL("racial", false),

    /** Circumstances giving a modifier. */
    CIRCUMSTANCE("circumstance", true),

    /** A magical enhancement modifier. */
    ENHANCEMENT("enhancement", false),

    /** A deflection modifier against attacks. */
    DEFLECTION("deflection", false),

    /** A competence modifier against attacks. */
    COMPETENCE("competence", false);

    /** The value's name. */
    private @Nonnull String m_name;

    /** Flag if the value stacks with others of its kind. */
    private boolean m_stacks;

    /** Create the name.
     *
     * @param inName   the name of the value
     * @param inStacks true if this modifier stacks with similar ones, false
     *                 if not
     *
     */
    private Type(@Nonnull String inName, boolean inStacks)
    {
      m_name = ValueGroup.constant("product.part", inName);
      m_stacks = inStacks;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Convert to a human readable string.
     *
     * @return the converted string
     *
     */
    @Override
    public @Nonnull String toString()
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
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------- Modifier -------------------------------

  /**
   * Construct the modifier object with an undefined value.
   *
   */
  public Modifier()
  {
    // nothing to do
  }

  //........................................................................
  //------------------------------- Modifier -------------------------------

  /**
   * Construct the modifier object with a general modifier.
   *
   * @param       inValue       the modifier value
   *
   */
  public Modifier(int inValue)
  {
    this(inValue, Type.GENERAL);
  }

  //........................................................................
  //------------------------------- Modifier -------------------------------

  /**
   * Construct the modifier object.
   *
   * @param       inValue       the modifier value
   * @param       inType        the type of the modifier
   *
   */
  public Modifier(int inValue, @Nonnull Type inType)
  {
    m_value = inValue;
    m_type  = inType;
    m_defined = true;
  }

  //........................................................................
  //---------------------------- withCondition -----------------------------

  /**
   *
   *
   * @param
   *
   * @return
   *
   */
  public Modifier withCondition(@Nullable Condition inCondition)
  {
    if(inCondition == null)
      return this;

    if(m_condition == null)
      m_condition = inCondition;
    else
      m_condition = new And(m_condition, inCondition);

    return this;
  }

  //........................................................................

  {
    withTemplate("modifier");
    withEditType("non-empty");
  }

  //-------------------------------- create --------------------------------

  /**
   * Create a new list with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   *
   */
  @Override
  public Modifier create()
  {
    return super.create(new Modifier());
  }

  //........................................................................
  //------------------------------- withNext -------------------------------

  /**
   * Set the next modifier in the chain.
   *
   * @param       inNext the next modifier
   *
   * @return      this modifier for chaining
   *
   */
  public @Nonnull Modifier withNext(@Nullable Modifier inNext)
  {
    m_next = inNext;

    return this;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The modifier value itself. */
  private int m_value = 0;

  /** The type of the modifier. */
  private @Nonnull Type m_type = Type.GENERAL;

  /** The flag if defined or not. */
  private boolean m_defined = false;

  /** The condition for the modifier, if any. */
  private @Nullable Condition m_condition;

  /** A next modifier, if any. */
  private @Nullable Modifier m_next;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getValue -------------------------------

  /**
   * Get the value of the modifier stored.
   *
   * @return      the requested value
   *
   */
  public int getValue()
  {
    if(m_next == null)
      return m_value;

    return m_value + m_next.getValue();
  }

  //........................................................................
  //------------------------------ getMinValue -----------------------------

  /**
   * Get the value of the modifier stored.
   *
   * @return      the minimally possible value
   *
   */
  public int getMinValue()
  {
    if(m_next == null)
      return minValue();

    return minValue() + m_next.getMinValue();
  }

  //........................................................................
  //------------------------------- minValue -------------------------------

  /**
   * Compute the minimal value of this modifier, ignoring the next.
   *
   * @return   the minimal possible value
   *
   */
  private int minValue()
  {
    if(m_condition == null || m_condition.check(false) == Condition.Result.TRUE)
      return m_value;

    if(m_condition.check(false) == Condition.Result.FALSE)
      return 0;

    if(m_value < 0)
      return m_value;

    return 0;
  }

  //........................................................................
  //------------------------------ getMaxValue -----------------------------

  /**
   * Get the value of the modifier stored.
   *
   * @return      the maximally possible value
   *
   */
  public int getMaxValue()
  {
    if(m_next == null)
      return maxValue();

    return maxValue() + m_next.getMaxValue();
  }

  //........................................................................
  //------------------------------- maxValue -------------------------------

  /**
   * Compute the minimal value of this modifier, ignoring the next.
   *
   * @return  the maxiamally possible value
   *
   */
  private int maxValue()
  {
    if(m_condition == null || m_condition.check(false) == Condition.Result.TRUE)
      return m_value;

    if(m_condition.check(false) == Condition.Result.FALSE)
      return 0;

    if(m_value > 0)
      return m_value;

    return 0;
  }

  //........................................................................
  //-------------------------------- getType -------------------------------

  /**
   * Get the modifier type stored.
   *
   * @return      the requested type
   *
   */
  public @Nonnull Type getType()
  {
    return m_type;
  }

  //........................................................................
  //----------------------------- getCondition -----------------------------

  /**
   * Get the condition type stored.
   *
   * @return      the requested condition
   *
   */
  public @Nullable Condition getCondition()
  {
    return m_condition;
  }

  //........................................................................
  //-------------------------------- getNext -------------------------------

  /**
   * Get the next modifier stored.
   *
   * @return      the next modifier, if any
   *
   */
  public @Nullable Modifier getNext()
  {
    return m_next;
  }

  //........................................................................
  //------------------------------- getBase --------------------------------

  /**
   * Get the base modifiers, without conditions.
   *
   * @return      a list of the modifiers found
   *
   */
  public @Nonnull List<String> getBase()
  {
    List<String> result = new ArrayList<String>();
    return addBase(result);
  }

  //........................................................................
  //------------------------------- addBase --------------------------------

  /**
   * Add the base modifier value (without condition) to the given list.
   *
   * @param       ioBases the list to add to
   *
   * @return      the complete list
   *
   */
  private @Nonnull List<String> addBase(@Nonnull List<String> ioBases)
  {
    if(!isDefined())
      return ioBases;

    ioBases.add((m_value >= 0 ? "+" : "") + m_value + " " + m_type);
    if(m_next != null)
      m_next.addBase(ioBases);

    return ioBases;
  }

  //........................................................................

  //----------------------------- stackOrMore ------------------------------

  /**
   * Check if this modifier is stackable with the given one or if it has
   * a larger bonus, id est it has to be used. If the modifier types are
   * different, they stack in any case
   *
   * @param       inOther the other modifier to check against
   *
   * @return      true if the modifier is still useful, false else
   *
   */
  public boolean stackOrMore(@Nonnull Modifier inOther)
  {
    if(stacks() || inOther.stacks())
      return true;

    if(m_condition != null
       && m_condition.check(false) == Condition.Result.FALSE)
      return false;

    if(inOther.m_condition != null
       && inOther.m_condition.check(false) == Condition.Result.FALSE)
      return true;

    // the values generally don't stack, i.e. only the larger one is used
    return m_value > inOther.m_value;
  }

  //........................................................................
  //----------------------------- stackOrMore ------------------------------

  /**
   * Check if this modifier is stackable with the given ones or if it has
   * a larger bonus, id est it has to be used. If the modifier types are
   * different, they stack in any case
   *
   * @param       inOthers the other modifiers to check against
   *
   * @return      true if the modifier is still useful, false else
   *
   */
  public boolean stackOrMore(@Nonnull List<Modifier> inOthers)
  {
    // penalties always stack!
    if(m_value < 0)
      return true;

    for(Iterator<Modifier> i = inOthers.iterator(); i.hasNext(); )
      if(!stackOrMore(i.next()))
        return false;

    return true;
  }

  //........................................................................
  //----------------------------- stackOrMore ------------------------------

  /**
   * Check if this modifier is stackable with the given one or if it has
   * a larger bonus, id est it has to be used. If the modifier types are
   * different, they stack in any case
   *
   * @param       inOthers the other modifiers to check against
   *
   * @return      true if the modifier is still useful, false else
   *
   */
  public boolean stackOrMore(@Nonnull Modifier ... inOthers)
  {
    // penalties always stack!
    if(m_value < 0)
      return true;

    for(int i = 0; i < inOthers.length; i++)
      if(!stackOrMore(inOthers[i]))
        return false;

    return true;
  }

  //........................................................................
  //-------------------------------- stacks --------------------------------

  /**
   * Check if this modifier stacks with others of the same type.
   *
   * @return      true if it stacks, false else
   *
   */
  private boolean stacks()
  {
    // TODO: check if this is still true!
    if(m_value < 0)
      return true;

    // undefined conditions always stack to let the user decide
    if(m_condition != null
       && m_condition.check(false) == Condition.Result.UNDEFINED)
      return true;

    return m_type.stacks();
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
    return m_defined;
  }

  //........................................................................
  //-------------------------------- isZero --------------------------------

  /**
   *
   *
   * @param
   *
   * @return
   *
   */
  public boolean isZero()
  {
    return getValue() == 0;
  }

  //........................................................................


  //------------------------------- doFormat -------------------------------

  /**
   * Really to the formatting.
   *
   * @return      the command for setting the value
   *
   */
  @Override
  @Deprecated
  protected @Nonnull Command doFormat()
  {
    int value = getValue();
    return new Command(value >= 0 ? "+" : "", value, " (", formatList(), ")");
  }

  //........................................................................
  //------------------------------ formatList ------------------------------

  /**
   * Format all the modifiers into a string list.
   *
   * @return  the command for printing the list
   *
   */
  private @Nonnull Command formatList()
  {
    List<Object> commands = new ArrayList<Object>();

    if(m_value >= 0)
      commands.add("+");

    commands.add(m_value);
    commands.add(" ");
    commands.add(m_type);

    List<Object> desc = new ArrayList<Object>();

    if(stacks())
      desc.add("this modifier stacks with similar ones");
    else
    {
      desc.add("this modifier does ");
      desc.add(new Emph("not"));
      desc.add(" stack with similar ones");
    }

    Command command = new Window(new Command(commands), new Command(desc));

    if(m_next == null || !m_next.isDefined())
      return command;

    return new Command(command, " &nbsp;", m_next.formatList());
  }

  //........................................................................
  //------------------------------ doToString ------------------------------

  /**
   * Convert the value to a string.
   *
   * @return      a String representation
   *
   */
  @Override
  protected @Nonnull String doToString()
  {
    StringBuilder result = new StringBuilder();

    if(m_value >= 0)
      result.append("+");

    result.append(m_value);
    result.append(" " + m_type);

    if(m_condition != null)
    {
      result.append(" if ");
      result.append(m_condition.toString());
    }

    if(m_next != null)
    {
      result.append(" ");
      result.append(m_next.toString());
    }

    return result.toString();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //---------------------------------- as ----------------------------------

  /**
   * Create a new the value with similar setup but new value.
   *
   * @param       inValue the new value of the modifier
   * @param       inType  the type of the modifier
   *
   * @return      the new value
   *
   */
  public @Nonnull Modifier as(int inValue, @Nonnull Type inType)
  {
    return as(inValue, inType, null, null);
  }

  //........................................................................
  //---------------------------------- as ----------------------------------

  /**
   * Create a new the value with similar setup but new value.
   *
   * @param       inValue     the new value of the modifier
   * @param       inType      the type of the modifier
   * @param       inCondition the condition, if any
   * @param       inNext      the next modifier for this chain
   *
   * @return      the new value
   *
   */
  public @Nonnull Modifier as(int inValue, @Nonnull Type inType,
                              @Nullable Condition inCondition,
                              @Nullable Modifier inNext)
  {
    Modifier modifier = create();

    modifier.m_value = inValue;
    modifier.m_type = inType;
    modifier.m_defined = true;
    modifier.m_condition = inCondition;
    modifier.m_next = inNext;

    return modifier;
  }

  //........................................................................
  //---------------------------------- as ----------------------------------

  /**
   * Create a new the value with similar setup but new value.
   *
   * @param       inNext  the next modifier for this chain
   *
   * @return      the new value
   *
   */
  public @Nonnull Modifier as(@Nullable Modifier inNext)
  {
    if(m_next == null)
      return as(m_value, m_type, m_condition, inNext);

    return as(m_value, m_type, m_condition, m_next.as(inNext));
  }

  //........................................................................
  //-------------------------------- doRead --------------------------------

  /**
   * Read the value from the reader and replace the current one.
   *
   * @param       inReader the reader to read from
   *
   * @return      true if read, false if not
   *
   */
  @Override
  public boolean doRead(@Nonnull ParseReader inReader)
  {
    try
    {
      m_value = inReader.readInt();

      m_defined = true;

      m_type = inReader.expect(new ArrayIterator<Type>(m_type.values()));

      // no type read, thus it is general
      if(m_type == null)
        m_type = Type.GENERAL;

      ParseReader.Position pos = inReader.getPosition();
      if(inReader.expect("if"))
      {
        m_condition = (Condition)new Condition().read(inReader);

        if(m_condition == null)
        {
          inReader.logError(pos, "expected.condition", null);
          inReader.seek(pos);
        }
       }

      m_next = read(inReader);
    }
    catch(net.ixitxachitls.input.ReadException e)
    {
      return false;
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
  public @Nonnull Modifier add(@Nonnull Modifier inValue)
  {
    int value = m_value;
    if(m_type == inValue.m_type &&
       ((m_condition != null && m_condition.equals(inValue.m_condition))
        || m_condition == null && inValue.m_condition == null))
    {
      Modifier next;
      if(m_next == null)
        next = inValue.m_next;
      else if(inValue.m_next == null)
        next = m_next;
      else
        next = m_next.add(inValue.m_next);

      if(m_type.stacks())
        return as(m_value + inValue.m_value, m_type, m_condition, next);
      else
        return
          as(Math.max(value, inValue.m_value), m_type, m_condition, next);
    }

    if(m_next == null)
      return as(m_value, m_type, m_condition, inValue);

    return as(m_value, m_type, m_condition, m_next.add(inValue));
  }

  //........................................................................
  //-------------------------------- ignore --------------------------------

  /**
   * Ignore all modifiers with the given type.
   *
   * @param      inTypes the types to ignore
   *
   * @return     the modifier that is left after ignoring the given types, if
   *             any
   *
   */
  public @Nullable Modifier ignore(@Nonnull Type ... inTypes)
  {
    for(Type type : inTypes)
      if(m_type == type && m_value >= 0)
        if(m_next != null)
          return m_next.ignore(inTypes);
        else
          return null;

    if(m_next == null)
      return this;

    Modifier next = m_next.ignore(inTypes);
    if(next == m_next)
      return this;

    return as(m_value, m_type, m_condition, next);
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

    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      Modifier value = new Modifier();

      // undefined value
      assertEquals("not undefined at start", false, value.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   value.toString());

      // now with some value
      value = new Modifier(2, Type.DODGE);

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("output", "+2 dodge", value.toString());
      assertEquals("print",
                   "+2 (\\window{+2 dodge}"
                   + "{this modifier stacks with similar ones})",
                   value.format(true).toString());

      value = new Modifier(0);

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("output", "+0 general", value.toString());
      assertEquals("output",
                   "+0 (\\window{+0 general}"
                   + "{this modifier stacks with similar ones})",
                   value.format(true).toString());

      // now with some value
      value = new Modifier(-5, Type.ARMOR);

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("output", "-5 armor", value.toString());
      assertEquals("output",
                   "-5 (\\window{-5 armor}"
                   + "{this modifier stacks with similar ones})",
                   value.format(true).toString());

      Value.Test.createTest(value);

      m_logger.verify();
   }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
     String []texts =
        {
          "empty", "", null, null,
          "invalid", "hello", null, "hello",
          "number only", "+2 ", "+2 general", " ",
          "invalid type", "-5 hi", "-5 general", " hi",
          "plus", "5 dodge", "+5 dodge", null,
          "complete", "0 general", "+0 general",   null,
          "complete 2", "0 armor \"test\"", "+0 armor", " \"test\"",
          "complete negative", "-3 dodge", "-3 dodge",   null,
          "condition", "+3 armor if \"just a test\"",
          "+3 armor if \"just a test\"", null,

          "multiple", "+3 armor +2 shield", "+3 armor +2 shield", null,
          "multiple no type", "+3 +2", "+3 general +2 general", null,
          "multiple no type", "+3 +2 dodge", "+3 general +2 dodge", null,
          "multiple condition", "+3 shield if \"test\" +2 armor",
          "+3 shield if \"test\" +2 armor", null,
        };

     Value.Test.readTest(texts, new Modifier());
    }

    //......................................................................
    //----- stack ----------------------------------------------------------

    /** Testing stacking. */
    @org.junit.Test
    public void stack()
    {
      Modifier first  = new Modifier(4, Type.ARMOR);
      Modifier second = new Modifier(3, Type.ARMOR);

      assertEquals("stack",          true,  first.stackOrMore(second));
      assertEquals("not stack",      false, second.stackOrMore(first));
      assertEquals("not stack self", false, first.stackOrMore(first));
      assertEquals("not stack self", false, second.stackOrMore(second));

      first  = new Modifier(2, Type.DODGE);
      second = new Modifier(3, Type.DODGE);

      assertEquals("stack",      true, first.stackOrMore(second));
      assertEquals("stack",      true, second.stackOrMore(first));
      assertEquals("stack self", true, first.stackOrMore(first));
      assertEquals("stack self", true, second.stackOrMore(second));

      ArrayList<Modifier> list = new ArrayList<Modifier>();

      list.add(new Modifier(2, Type.DODGE));
      list.add(new Modifier(4, Type.DODGE));
      list.add(new Modifier(5, Type.ARMOR));
      list.add(new Modifier(6, Type.SHIELD));

      assertTrue("stack", first.stackOrMore(list));
      assertFalse("stack",
                  new Modifier(3, Type.ARMOR)
                  .stackOrMore(new Modifier(2, Type.DODGE),
                               new Modifier(4, Type.ARMOR)));
    }

    //......................................................................
    //----- as -------------------------------------------------------------

    /** Testing sets. */
    @org.junit.Test
    public void as()
    {
      Modifier value = new Modifier();

      // undefined value
      assertFalse("not undefined at start", value.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   value.toString());

      value = value.as(42, Type.ARMOR);
      assertTrue("value", value.isDefined());
      assertEquals("value", "+42 armor", value.toString());
    }

    //......................................................................
  }

  //........................................................................
}
