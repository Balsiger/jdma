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

package net.ixitxachitls.dma.values;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.Pair;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base expression for all expressions.
 *
 * @file          Expression.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public abstract class Expression implements Comparable<Expression>
{
  //----------------------------------------------------------------- nested

  //----- Shared -----------------------------------------------------------

  /** A class to share data when computing expressions. */
  public static class Shared
  {
    /** Simple default constructor. */
    protected Shared()
    {
      // nothing to do
    }

    /** The number of magical armor plusses computed so far. */
    private int m_magicArmor = 0;

    /** The number of magical weapon plusses computed so far. */
    private int m_magicWeapon = 0;

    /** The number of magical ammunition plusses computed so far. */
    private int m_magicAmmunition = 0;

    /**
     * Convert to a string.
     *
     * @return the converted string
     */
    @Override
    public String toString()
    {
      return "magic armor " + m_magicArmor + ", magic weapon " + m_magicWeapon
        + ", magic ammunition " + m_magicAmmunition;
    }
  }

  //........................................................................
  //----- MagicArmor -------------------------------------------------------

  /** An expression representing the number of plusses for magical armor. */
  public static class MagicArmor extends Expression
  {
    /**
     * Create the magic armor expression.
     *
     * @param inPlus the number of magical plusses for the armor
     */
    public MagicArmor(int inPlus)
    {
      super(1);

      m_plus = inPlus;
    }

    /** The number of plusses for magical armor. */
    private int m_plus;

    /** Pattern for magical armor. */
    private static final Pattern s_pattern =
      Pattern.compile("^\\s*magic armor\\((\\d+)\\)");

    @Override
    public @Nonnull String toString()
    {
      return "[magic armor(" + m_plus + ")]";
    }

    /**
     * Parse a magic armor from the given text.
     *
     * @param  inText the text to parse from
     *
     * @return the parsed magic armor or null if not properly parsed
     */
    protected static @Nullable MagicArmor parse(@Nonnull String inText)
    {
      Matcher matcher = s_pattern.matcher(inText);
      if(matcher.find())
        return new MagicArmor(Integer.parseInt(matcher.group(1)));

      return null;
    }

    //------------------------------ compute -------------------------------

    /**
     * Compute the value for the expression.  We compute the value here in
     * relation to previously computed magic armor values and only adding the
     * difference.
     *
     * @param       inEntry   the entry to compute for
     * @param       inValue   the value to compute from
     * @param       ioShared  shared data for all expressions
     *
     * @return      the computed, adjusted value, if any
     *
     */
    public @Nullable Value compute(@Nonnull ValueGroup inEntry,
                                   @Nullable Value inValue,
                                   @Nonnull Shared ioShared)
    {
      // (m + p)^2 - m^2 = 2mp + p^2
      int factor = 2 * ioShared.m_magicArmor * m_plus + m_plus * m_plus;
      ioShared.m_magicArmor += m_plus;

      Money value = new Money(0, 0, factor * 1000, 0);
      if(inValue == null)
        return value;

      if(!(inValue instanceof Money))
        return inValue;

      return ((Money)inValue).add(value);
    }

    //......................................................................
  }

  //........................................................................
  //----- Factor -----------------------------------------------------------

  /** An expression representing factor to multiply the values with. */
  public static class Factor extends Expression
  {
    /**
     * Create the factor expression.
     *
     * @param inMultiply the multiplication factor
     * @param inDivide   the dividing factor
     */
    public Factor(long inMultiply, long inDivide)
    {
      super(2);

      m_multiply = inMultiply;
      m_divide = inDivide;
    }

    /** The multiplication factor. */
    private long m_multiply;

    /** The dividing factor. */
    private long m_divide = 1;

    /** Pattern for a factor. */
    private static final Pattern s_pattern =
      Pattern.compile("^\\s*\\*\\s*(\\d+)(?:\\s*/\\s*(\\d+))?");

    @Override
    public @Nonnull String toString()
    {
      if(m_divide != 1)
        return "[* " + m_multiply + "/" + m_divide + "]";

      return "[* " + m_multiply + "]";
    }

    /**
     * Parse a magic weapon from the given text.
     *
     * @param  inText the text to parse from
     *
     * @return the parsed magic weapon or null if not properly parsed
     */
    protected static @Nullable Factor parse(@Nonnull String inText)
    {
      Matcher matcher = s_pattern.matcher(inText);
      if(matcher.find())
        if(matcher.group(2) == null)
          return new Factor(Integer.parseInt(matcher.group(1)), 1);
        else
          return new Factor(Integer.parseInt(matcher.group(1)),
                            Integer.parseInt(matcher.group(2)));

      return null;
    }

    //------------------------------ compute -------------------------------

    /**
     * Compute the value for the expression.
     *
     * @param       inEntry   the entry to compute for
     * @param       inValue   the value to compute from
     * @param       ioShared  shared data for all expressions
     *
     * @return      the compute, adjusted value
     *
     */
    public @Nullable Value compute(@Nonnull ValueGroup inEntry,
                                   @Nullable Value inValue,
                                   @Nonnull Shared ioShared)
    {
      if(inValue == null)
        return null;

      return inValue.multiply(m_multiply).divide(m_divide);
    }

    //......................................................................
  }

  //........................................................................
  //----- Addition ---------------------------------------------------------

  /** An expression representing factor to multiply the values with. */
  public static class Addition extends Expression
  {
    /**
     * Create the factor expression.
     *
     * @param inAddition the value to add or subtract
     * @param inSubtract true to subtract the value, false for addition
     */
    public Addition(String inAddition, boolean inSubtract)
    {
      super(3);

      m_addition = inAddition;
      m_subtract = inSubtract;
    }

    /** The additional value. */
    private String m_addition;

    /** The flag if adding or subtracting. */
    private boolean m_subtract;

    /** Pattern for an addition. */
    private static final Pattern s_pattern =
      Pattern.compile("^\\s*(\\+|\\-)(.*)");

    @Override
    public @Nonnull String toString()
    {
      return "[" + (m_subtract ? "-" : "+") + m_addition + "]";
    }

    /**
     * Parse an addition from the given text.
     *
     * @param  inText the text to parse from
     *
     * @return the parsed magic weapon or null if not properly parsed
     */
    protected static @Nullable Addition parse(@Nonnull String inText)
    {
      Matcher matcher = s_pattern.matcher(inText);
      if(matcher.find())
        return new Addition(matcher.group(2), "-".equals(matcher.group(1)));

      return null;
    }

    //------------------------------ compute -------------------------------

    /**
     * Compute the value for the expression.
     *
     * @param       inEntry   the entry to compute for
     * @param       inValue   the value to compute from
     * @param       ioShared  shared data for all expressions
     *
     * @return      the compute, adjusted value
     *
     */
    @SuppressWarnings("unchecked")
    public @Nullable Value compute(@Nonnull ValueGroup inEntry,
                                   @Nullable Value inValue,
                                   @Nonnull Shared ioShared)
    {
      if(inValue == null)
        return null;

      Value addition = inValue.read(m_addition);
      if(addition == null)
      {
        Log.warning("cannot parse value " + m_addition + " for addition");
        return inValue;
      }
      else
        if(m_subtract)
          return inValue.subtract(addition);
        else
          return inValue.add(addition);
    }

    //......................................................................
  }

  //........................................................................
  //----- Equal ------------------------------------------------------------

  /** An expression representing equal to a value. */
  public static class Equal extends Expression
  {
    /**
     * Create the equal expression.
     *
     * @param inValue the value to set to
     *
     */
    public Equal(@Nonnull String inValue)
    {
      super(10);

      m_value = inValue;
    }

    /** The value to set to. */
    private @Nonnull String m_value;

    /** Pattern for a equal. */
    private static final Pattern s_pattern = Pattern.compile("^\\s*=\\s*(.*)");

    @Override
    public @Nonnull String toString()
    {
      return "[= " + m_value + "]";
    }

    /**
     * Parse a magic weapon from the given text.
     *
     * @param  inText the text to parse from
     *
     * @return the parsed magic weapon or null if not properly parsed
     */
    protected static @Nullable Equal parse(@Nonnull String inText)
    {
      Matcher matcher = s_pattern.matcher(inText);
      if(matcher.find())
        return new Equal(matcher.group(1));

      return null;
    }

    /**
     * Compute the value for the expression.
     *
     * @param       inEntry   the entry to compute for
     * @param       inValue   the value to compute from
     * @param       ioShared  shared data for all expressions
     *
     * @return      the compute, adjusted value
     *
     */
    public @Nullable Value compute(@Nonnull ValueGroup inEntry,
                                   @Nullable Value inValue,
                                   @Nonnull Shared ioShared)
    {
      if(inValue == null)
        return null;

      return inValue.read(m_value);
    }
  }

  //........................................................................
  //----- Switch -----------------------------------------------------------

  /** An expression representing switch value. */
  public static class Switch extends Expression
  {
    /**
     * Create the switch expression.
     *
     * @param inExpressions the expressions for all the statements
     *
     */
    public Switch(@Nonnull String inExpressions)
    {
      super(3);

      for(String statement : inExpressions.split("\\s*,\\s*"))
      {
        String []parts = statement.split("\\s*:\\s*");
        if(parts.length != 2)
          continue;

        m_statements.add(new Pair<String, String>(parts[0], parts[1]));
      }
    }

    /** The expressions for the switch statements. */
    private @Nonnull List<Pair<String, String>> m_statements =
      new ArrayList<Pair<String, String>>();

    /** Pattern for a switch. */
    private static final Pattern s_pattern =
      Pattern.compile("^\\s*switch\\s*\\((.*)\\)");

    /** Pattern for 'in' conditions. */
    private static final Pattern s_inCondition =
      Pattern.compile("^(.*)\\s+in\\s+(.*)$");

    /** Pattern for 'is' conditions. */
    private static final Pattern s_isCondition =
      Pattern.compile("^(.*)\\s+is\\s+(.*)$");

    @Override
    public @Nonnull String toString()
    {
      StringBuilder builder = new StringBuilder("[switch(");
      boolean first = true;
      for(Pair<String, String> statement : m_statements)
      {
        if(first)
          first = false;
        else
          builder.append(", ");

        builder.append(statement.first() + ": " + statement.second());
      }

      builder.append(")]");

      return builder.toString();
    }

    /**
     * Parse a magic weapon from the given text.
     *
     * @param  inText the text to parse from
     *
     * @return the parsed magic weapon or null if not properly parsed
     */
    protected static @Nullable Switch parse(@Nonnull String inText)
    {
      Matcher matcher = s_pattern.matcher(inText);
      if(matcher.find())
        return new Switch(matcher.group(1));

      return null;
    }

    /**
     * Compute the value for the expression.
     *
     * @param       inEntry   the entry to compute for
     * @param       inValue   the value to compute from
     * @param       ioShared  shared data for all expressions
     *
     * @return      the compute, adjusted value
     *
     */
    @SuppressWarnings("unchecked") // adding
    public @Nullable Value compute(@Nonnull ValueGroup inEntry,
                                   @Nullable Value inValue,
                                   @Nonnull Shared ioShared)
    {
      if(inValue == null)
        return null;

      for(Pair<String, String> statement : m_statements)
      {
        String condition = statement.first();
        String value = statement.second();

        if(isTrue(inEntry, condition))
        {
          ParseReader reader =
            new ParseReader(new StringReader(value), "switch");
          return inValue.add(inValue.read(reader));
        }
      }

      return inValue;
    }

    /**
     * Check if the given condition is true.
     *
     * @param  inEntry     the entry to check for
     * @param  inCondition the string representation of a condition to check
     *
     * @return true if the condition holds, false if not
     *
     */
    private boolean isTrue(@Nonnull ValueGroup inEntry,
                           @Nonnull String inCondition)
    {
      if("default".equalsIgnoreCase(inCondition))
        return true;

      Matcher matcher = s_inCondition.matcher(inCondition);
      if(matcher.find())
      {
        String value = matcher.group(1);
        String key = matcher.group(2);

        return inEntry.isValueIn(value, key);
      }

      matcher = s_isCondition.matcher(inCondition);
      if(matcher.find())
      {
        String key = matcher.group(1);
        String value = matcher.group(2);

        Boolean result = inEntry.isValue(value, key);
        return result != null && result;
      }

      return false;
    }
  }

  //........................................................................
  //----- MagicItem --------------------------------------------------------

  /** An expression representing a magic item with spell levels. */
  public static class MagicItem extends Expression
  {
    /** Create the magic item expression. */
    public MagicItem()
    {
      super(1);
    }

    /** The name of the magic item category. */
    private @Nonnull String m_name;

    /** The base price for the item. */
    private @Nonnull int m_basePrice;

    /** The spell level of the wand. */
    private int m_spellLevel;

    /** The dividing factor. */
    private int m_casterLevel;

    /** The material costs. */
    private int m_material = 0;

    /** The XP cost. */
    private int m_xp = 0;

    /** Pattern for a wand (spell level only). */
    private static final Pattern s_pattern = Pattern.compile
      ("(\\w+)\\((\\d+)(?:,\\s*(\\d+)(?:,\\s*(\\d+)(?:,\\s*(\\d+))?)?)?\\)");

    /** The magic item names and base prices. */
    private static Map<String, Integer> s_prices =
      new ImmutableMap.Builder<String, Integer>()
      .put("wand", 15)
      .put("potion", 50)
      .put("scroll", 25)
      .build();

    @Override
    public @Nonnull String toString()
    {
      if(m_xp == 0)
        if(m_material == 0)
          if(m_casterLevel == m_spellLevel * 2 - 1)
            return "[" + m_name + "(" + m_spellLevel + ")]";
          else
            return
              "[" + m_name + "(" + m_spellLevel + ", " + m_casterLevel + ")]";
        else
          return "[" + m_name + "(" + m_spellLevel + ", " + m_casterLevel + ", "
            + m_material + ")]";
      else
        return "[" + m_name + "(" + m_spellLevel + ", " + m_casterLevel + ", "
          + m_material + ", " + m_xp + ")]";
    }

    /**
     * Parse a magic item from the given text.
     *
     * @param  inText the text to parse from
     *
     * @return the parsed magic weapon or null if not properly parsed
     */
    protected static @Nullable MagicItem parse(@Nonnull String inText)
    {
      Matcher matcher = s_pattern.matcher(inText);
      if(!matcher.find())
        return null;

      MagicItem item = new MagicItem();
      item.m_name = matcher.group(1);
      if(!s_prices.containsKey(item.m_name))
        Log.warning("invalid magic item " + item.m_name);
      else
        item.m_basePrice = s_prices.get(item.m_name);

      item.m_spellLevel = Integer.parseInt(matcher.group(2));

      if(matcher.group(5) != null)
        item.m_xp = Integer.parseInt(matcher.group(5));

      if(matcher.group(4) != null)
        item.m_material = Integer.parseInt(matcher.group(4));

      if(matcher.group(3) != null)
      {
        item.m_casterLevel = Integer.parseInt(matcher.group(3));

        if(item.m_casterLevel < item.m_spellLevel * 2 - 1)
        {
          Log.warning("invalid caster level for magic item: " + item);
        }
      }
      else
        item.m_casterLevel =
          item.m_spellLevel > 1 ? item.m_spellLevel * 2 - 1 : 1;

      return item;
    }

    /**
     * Compute the value for the expression.
     *
     * @param       inEntry   the entry to compute for
     * @param       inValue   the value to compute from
     * @param       ioShared  shared data for all expressions
     *
     * @return      the compute, adjusted value
     *
     */
    @SuppressWarnings("unchecked")
    public @Nullable Value compute(@Nonnull ValueGroup inEntry,
                                   @Nullable Value inValue,
                                   @Nonnull Shared ioShared)
    {
      int gold = m_material + 5 * m_xp;
      int silver = 0;

      if(m_spellLevel == 0)
        if((m_basePrice * m_casterLevel) % 2 == 0)
          gold = (m_basePrice * m_casterLevel) / 2;
        else
          silver = (m_basePrice * m_casterLevel * 10) / 2;
      else
        gold = m_basePrice * m_spellLevel * m_casterLevel;

      Money value = new Money(0, silver, gold, 0);
      if(inValue == null)
        return value;

      return inValue.add(value);
    }
  }

  //........................................................................
  //----- MagicWeapon ------------------------------------------------------

  /** An expression representing the number of plusses for magical weapon. */
  public static class MagicWeapon extends Expression
  {
    /**
     * Create the magic weapon expression.
     *
     * @param inPlus the number of magical plusses for the weapon
     */
    public MagicWeapon(int inPlus)
    {
      super(1);

      m_plus = inPlus;
    }

    /** The number of plusses for magical armor. */
    private int m_plus;

    /** Pattern for magical armor. */
    private static final Pattern s_pattern =
      Pattern.compile("magic weapon\\((\\d+)\\)");

    @Override
    public @Nonnull String toString()
    {
      return "[magic weapon(" + m_plus + ")]";
    }

    /**
     * Parse a magic weapon from the given text.
     *
     * @param  inText the text to parse from
     *
     * @return the parsed magic weapon or null if not properly parsed
     */
    protected static @Nullable MagicWeapon parse(@Nonnull String inText)
    {
      Matcher matcher = s_pattern.matcher(inText);
      if(matcher.find())
        return new MagicWeapon(Integer.parseInt(matcher.group(1)));

      return null;
    }

    /**
     * Compute the value for the expression.  We compute the value here in
     * relation to previously computed magic armor values and only adding the
     * difference.
     *
     * @param       inEntry   the entry to compute for
     * @param       inValue   the value to compute from
     * @param       ioShared  shared data for all expressions
     *
     * @return      the compute, adjusted value
     *
     */
    public @Nullable Value compute(@Nonnull ValueGroup inEntry,
                                   @Nullable Value inValue,
                                   @Nonnull Shared ioShared)
    {
      // 2 * (m + p)^2 - m^2 = 4mp + 2 * p^2
      int factor = 4 * ioShared.m_magicArmor * m_plus + 2 * m_plus * m_plus;
      ioShared.m_magicWeapon += m_plus;

      Money value = new Money(0, 0, factor * 1000, 0);

      if(inValue == null)
        return value;

      if(!(inValue instanceof Money))
        return inValue;

      return ((Money)inValue).add(value);
    }
  }

  //........................................................................
  //----- MagicAmmunition --------------------------------------------------

  /**
   * An expression representing the number of plusses for magical ammunition.
   */
  public static class MagicAmmunition extends Expression
  {
    /**
     * Create the magic ammunition expression.
     *
     * @param inPlus the number of magical plusses for the ammunition
     */
    public MagicAmmunition(int inPlus)
    {
      super(1);

      m_plus = inPlus;
    }

    /** The number of plusses for magical armor. */
    private int m_plus;

    /** Pattern for magical armor. */
    private static final Pattern s_pattern =
      Pattern.compile("magic ammunition\\((\\d+)\\)");

    @Override
    public @Nonnull String toString()
    {
      return "[magic ammunition(" + m_plus + ")]";
    }

    /**
     * Parse a magic ammunition from the given text.
     *
     * @param  inText the text to parse from
     *
     * @return the parsed magic ammunition or null if not properly parsed
     */
    protected static @Nullable MagicAmmunition parse(@Nonnull String inText)
    {
      Matcher matcher = s_pattern.matcher(inText);
      if(matcher.find())
        return new MagicAmmunition(Integer.parseInt(matcher.group(1)));

      return null;
    }

    /**
     * Compute the value for the expression.  We compute the value here in
     * relation to previously computed magic armor values and only adding the
     * difference.
     *
     * @param       inEntry   the entry to compute for
     * @param       inValue   the value to compute from
     * @param       ioShared  shared data for all expressions
     *
     * @return      the compute, adjusted value
     *
     */
    public @Nullable Value compute(@Nonnull ValueGroup inEntry,
                                   @Nullable Value inValue,
                                   @Nonnull Shared ioShared)
    {
      // 2 * (m + p)^2 - m^2 = 4mp + 2 * p^2
      int factor = 4 * ioShared.m_magicArmor * m_plus + 2 * m_plus * m_plus;
      ioShared.m_magicAmmunition += m_plus;

      Money value = new Money(0, 0, factor * 20, 0);

      if(inValue == null)
        return value;

      if(!(inValue instanceof Money))
        return inValue;

      return ((Money)inValue).add(value);
    }
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------ Expression ------------------------------

  /**
   * Create the expression.
   *
   * @param inPriority the priority of the expression for sorting
   *
   */
  public Expression(int inPriority)
  {
    m_priority = inPriority;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The remark starter. */
  private static final char s_start = '[';

  /** The remark end. */
  private static final char s_end = ']';

  /** The priority of the expression. */
  private int m_priority;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- parse ---------------------------------

  /**
   * Parse the string into the proper expression.
   *
   * @param    inText the text to parse
   *
   * @return   the expression parsed, if any
   *
   */
  protected static @Nullable Expression parse(@Nonnull String inText)
  {
    String text = inText.replace("\n", "");
    Expression result = MagicArmor.parse(text);
    if(result != null)
      return result;

    result = Factor.parse(text);
    if(result != null)
      return result;

    result = Addition.parse(text);
    if(result != null)
      return result;

    result = MagicWeapon.parse(text);
    if(result != null)
      return result;

    result = MagicAmmunition.parse(text);
    if(result != null)
      return result;

    result = MagicItem.parse(text);
    if(result != null)
      return result;

    result = Equal.parse(text);
    if(result != null)
      return result;

    result = Switch.parse(text);
    if(result != null)
      return result;

    Log.warning("could not parse expression for: '" + inText + "'");
    return null;
  }

  //........................................................................
  //------------------------------ compareTo -------------------------------

  /**
   * Compare this expression to the other one.
   *
   * @param       inOther the expression to compare to
   *
   * @return      <0 if this is smaller than the other, >0 if bigger, 0 if equal
   *
   */
  public int compareTo(@Nullable Expression inOther)
  {
    if(inOther == null)
      return +1;

    int diff = m_priority - inOther.m_priority;
    if(diff != 0)
      return diff;

    return toString().compareTo(inOther.toString());
  }

  //........................................................................
  //-------------------------------- equals --------------------------------

  /**
   * Determine if this key is equal to the given object.
   *
   * @param inOther the object to compare for
   *
   * @return true if they are equal, false if not
   */
  @Override
  public boolean equals(Object inOther)
  {
    if(this == inOther)
      return true;

    if(inOther == null)
      return false;

    if(!(inOther instanceof Expression))
      return false;

    return toString().equals(inOther.toString());
  }

  //........................................................................
  //------------------------------- hashCode -------------------------------

  /**
   * Compute the hash code for the key.
   *
   * @return the hash value
   */
  @Override
  public int hashCode()
  {
    return toString().hashCode();
  }

  //........................................................................
  //-------------------------------- shared --------------------------------

  /**
   * Create a shared storage for expression computing.
   *
   * @return      the shared data
   *
   */
  public @Nonnull Shared shared()
  {
    return new Shared();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- read ---------------------------------

  /**
   * Read an expression from the given reader.
   *
   * @param       inReader   the reader to read from
   *
   * @return      the Remark read or null if none was found
   *
   */
  public static @Nullable Expression read(@Nonnull ParseReader inReader)
  {
    ParseReader.Position pos = inReader.getPosition();

    if(!inReader.expect(s_start))
    {
      inReader.seek(pos);
      return null;
    }

    String expression = inReader.read(s_end).trim();

    if(!inReader.expect(s_end))
    {
      inReader.seek(pos);

      return null;
    }

    return Expression.parse(expression);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- compute --------------------------------

  /**
   * Compute the value for the expression.
   *
   * @param       inEntry   the entry to compute for
   * @param       inValue   the value to compute from
   * @param       ioShared  shared data for all expressions
   *
   * @return      the compute, adjusted value
   *
   */
  public abstract @Nullable Value compute(@Nonnull ValueGroup inEntry,
                                          @Nullable Value inValue,
                                          @Nonnull Shared ioShared);

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- read -----------------------------------------------------------

    /** The read Test. */
    @org.junit.Test
    public void read()
    {
      ParseReader reader = reader("[magic armor(42)]");

      Expression expression = Expression.read(reader);
      assertEquals("magic armor", "[magic armor(42)]", expression.toString());
    }

    //......................................................................

    /**
     * Create the reader with the given input.
     *
     * @param  inInput the input the reader will read from
     *
     * @return the parse reader
     *
     */
    private ParseReader reader(@Nonnull String inInput)
    {
      return new ParseReader(new java.io.StringReader(inInput), "test");
    }
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
