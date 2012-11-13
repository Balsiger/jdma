/******************************************************************************
 * Copyright (c) 2002-2011 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.values.formatters.Formatter;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Color;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.util.Grouping;
import net.ixitxachitls.util.PublicCloneable;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base class for all DMA values.
 *
 * @file          Value.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @param         <T> the real type of the value, used for typ casting
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public abstract class Value<T extends Value>
  implements Comparable<Object>, PublicCloneable
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Value ---------------------------------

  /**
   * This is an empty default constructor.
   *
   */
  protected Value()
  {
    // nothing to do
  }

  //........................................................................

  //------------------------------- create ---------------------------------

  /**
   * Create a new value with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar value, but without any contents
   *
   */
  public abstract @Nonnull T create();

  //........................................................................
  //------------------------------- create ---------------------------------

  /**
   * Initialize the given value with the same information as the current value.
   *
   * @param       inNew the newly created object that needs to be completed
   *
   * @return      the created value
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull T create(@Nonnull T inNew)
  {
    inNew.m_formatter = m_formatter;
    inNew.m_grouping = m_grouping;
    inNew.m_editType = m_editType;
    inNew.m_choices = m_choices;
    inNew.m_related = m_related;
    inNew.m_indexBase = m_indexBase;
    inNew.m_template = m_template;
    inNew.m_templateArguments = m_templateArguments;

    return inNew;
  }

  //........................................................................
  //-------------------------------- clone ---------------------------------

  /**
   * Make a copy of the value and return it. If necessary, this is a deep
   * copy, although type relevant values are never copied, because these
   * are immutable.
   *
   * @return      a copy of the current value
   *
   */
  @Override
  @SuppressWarnings("unchecked")
  @Deprecated // do we still need this??
  public @Nonnull T clone()
  {
    try
    {
      return (T)super.clone();
    }
    catch(CloneNotSupportedException e)
    {
      throw new UnsupportedOperationException("Strange, clone should be "
                                              + "supported: " + e);
    }
  }

  //........................................................................

  //---------------------------- withFormatter -----------------------------

  /**
   * Set a formatter for this value.
   *
   * @param       inFormatter to formatter to use when printing the value
   *
   * @return      the value itself (to allow new T().setFormatter())
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull T withFormatter(@Nonnull Formatter<T> inFormatter)
  {
    m_formatter = inFormatter;

    return (T)this;
  }

  //........................................................................
  //----------------------------- withGrouping -----------------------------

  /**
   * Set a grouping for this value.
   *
   * @param       inGrouping to grouping to use when printing the value
   *
   * @return      the value itself (to allow new T().setFormatter())
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull T withGrouping(@Nonnull Grouping<T, String> inGrouping)
  {
    m_grouping = inGrouping;
    return (T)this;
  }

  //........................................................................
  //----------------------------- withEditType -----------------------------

  /**
   * Set a edit type for this value.
   *
   * @param       inType the type to set to
   *
   * @return      the value itself (to allow new T().setFormatter())
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull T withEditType(@Nonnull String inType)
  {
    if(inType.isEmpty())
      throw new IllegalArgumentException("must have a type here");

    m_editType = inType;

    return (T)this;
  }

  //........................................................................
  //----------------------------- withChoices ------------------------------

  /**
   * Set the edit values for this value.
   *
   * @param       inValues the edit values to use
   *
   * @return      the value itself (to allow new T().setFormatter())
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull T withChoices(@Nonnull String inValues)
  {
    m_choices = inValues;

    return (T)this;
  }

  //........................................................................
  //----------------------------- withRelated ------------------------------

  /**
   * Set the related edits for this value.
   *
   * @param       inValues the related values to use
   *
   * @return      the value itself (to allow new T().setFormatter())
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull T withRelated(@Nonnull String inValues)
  {
    m_related = inValues;

    return (T)this;
  }

  //........................................................................
  //---------------------------- withIndexBase -----------------------------

  /**
   * Set the base url for a value index.
   *
   * @param       inBase the base url
   *
   * @return      the value itself for chaining
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull T withIndexBase(@Nonnull String inBase)
  {
    m_indexBase = inBase;
    return (T)this;
  }

  //........................................................................
  //---------------------------- withIndexBase -----------------------------

  /**
   * Set the base url for a value index.
   *
   * @param       inType the type for the url
   *
   * @return      the value itself for chaining
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull T withIndexBase
    (@Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    return withIndexBase("/" + inType.getMultipleLink() + "/");
  }

  //........................................................................
  //---------------------------- withExpression ----------------------------

  /**
   * Set the expression for the value.
   *
   * @param       inExpression the expression to set
   *
   * @return      the value itself for chaining
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull T withExpression(@Nonnull Expression inExpression)
  {
    m_expression = inExpression;

    return (T)this;
  }

  //........................................................................
  //----------------------------- withTemplate -----------------------------

  /**
   * Set the template for printing the value.
   *
   * @param       inTemplate the template name
   *
   * @return      the value itself for chaining
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull T withTemplate(@Nonnull String inTemplate)
  {
    m_template = inTemplate;

    return (T)this;
  }

  //........................................................................
  //----------------------------- withTemplate -----------------------------

  /**
   * Set the template for printing the value.
   *
   * @param       inTemplate  the template name
   * @param       inArguments the (static) template arguments
   *
   * @return      the value itself for chaining
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull T withTemplate(@Nonnull String inTemplate,
                                 @Nonnull String ... inArguments)
  {
    m_template = inTemplate;
    m_templateArguments = ImmutableList.copyOf(inArguments);

    return (T)this;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The text to use for undefined values. */
  public static final @Nonnull String UNDEFINED =
    Config.get("resource:values/undefined", "$undefined$");

  /** The hint for this value. */
  protected @Nullable Remark m_remark = null;

  /** The expression for value computation, if any. */
  protected @Nullable Expression m_expression = null;

  /** The formatter for the value. */
  protected @Nullable Formatter<T> m_formatter = null;

  /** A grouping of the values, if any. */
  protected @Nullable Grouping<T, String> m_grouping = null;

  /** The type to use for editing. */
  protected @Nonnull String m_editType = "";

  /** The values for editing the type. */
  protected @Nullable String m_choices = null;

  /** The related values for editing. */
  protected @Nullable String m_related = null;

  /** The index base, if any. */
  protected @Nullable String m_indexBase = null;

  /** The template, if any. */
  protected @Nullable String m_template = null;

  /** The arguments to the template. */
  protected @Nullable ImmutableList<String> m_templateArguments = null;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getTemplate ------------------------------

  /**
   * Get the template to use for rendering.
   *
   * @return      the name of the template, if any
   *
   */
  public @Nullable String getTemplate()
  {
    return m_template;
  }

  //........................................................................
  //------------------------- getTemplateArguments -------------------------

  /**
   * Get the list of arguments for the template rendering this value.
   *
   * @return  the list of arguments for the template
   *
   */
  public @Nullable List<String> getTemplateArguments()
  {
    return m_templateArguments;
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert the value into a String that can be shown to a user.
   *
   * @return      a String representation for human reading
   *
   */
  @Override
  public @Nonnull String toString()
  {
    return toString(true);
  }

  //........................................................................
  //------------------------------- toString -------------------------------

  /**
   * Convert the value into a String that can be shown to a user.
   *
   * @param       inAll true if printing all values, false just for humans
   *
   * @return      a String representation for human reading
   *
   */
  public @Nonnull String toString(boolean inAll)
  {
    if(!isDefined())
      if(hasExpression())
        return m_expression.toString();
      else
        return UNDEFINED;

    return (!inAll || m_remark == null ? "" : m_remark.toString())
      + (m_expression == null ? "" : m_expression + " ") + doToString();
  }

  //........................................................................
  //------------------------------ doToString ------------------------------

  /**
   * Return a string representation of the value. The value can be assumed to
   * be defined when this is called. This method should not be called directly,
   * instead call toString().
   *
   * @return      a string representation.
   *
   */
  protected abstract @Nonnull String doToString();

  //........................................................................

  //------------------------------ isDefined -------------------------------

  /**
   * Check if the value is defined or not.
   *
   * @return      true if the value is defined, false if not
   *
   */
  public abstract boolean isDefined();

  //........................................................................

  //------------------------------ compareTo -------------------------------

  /**
   * Compare this value to another one.
   *
   * @param       inOther the value to compare to
   *
   * @return      -1 for less than, 0 for equal and +1 for greater than the
   *              object given
   *
   */
  @Override
  public int compareTo(@Nonnull Object inOther)
  {
    int compared = toString().compareTo(inOther.toString());
    if(compared != 0 || !(inOther instanceof Value))
      return compared;

    if(m_remark == null && ((Value)inOther).m_remark != null)
      return +1;

    if(m_remark != null && ((Value)inOther).m_remark == null)
      return -1;

    return 0;
  }

  //........................................................................
  //-------------------------------- equals --------------------------------

  /**
   * Check for equality of the given errors.
   *
   * @param       inOther the object to compare to
   *
   * @return      true if equal, false else
   *
   */
  @Override
  public boolean equals(Object inOther)
  {
    if(this == inOther)
      return true;

    if(inOther == null)
      return false;

    if(inOther instanceof Value)
      return toString().equals(inOther.toString());

    return false;
  }

  //........................................................................
  //------------------------------- hashCode -------------------------------

  /**
   * Compute the hash code for this class.
   *
   * @return      the hash code
   *
   */
  @Override
  public int hashCode()
  {
    return toString().hashCode();
  }

  //........................................................................

  //-------------------------------- print ---------------------------------

  /**
   * Generate a string representation of the value for printing.
   *
   * @param       inEntry    the entry this value is in
   *
   * @return  the printed value as a string.
   *
   */
  public @Nonnull String print(@Nonnull AbstractEntry inEntry)
  {
    return doPrint(inEntry);
  }

  //........................................................................
  //------------------------------- doPrint --------------------------------

  /**
   * Do the standard printing after handling templates.
   *
   * @param       inEntry    the entry this value is in
   *
   * @return      the string to be printed
   *
   */
  protected @Nonnull String doPrint(@Nonnull AbstractEntry inEntry)
  {
    return toString(false);
  }

  //........................................................................

  //-------------------------------- format --------------------------------

  /**
   * Format the value for printing.
   *
   * @return      the command that can be printed
   *
   */
  public @Nonnull Command format()
  {
    return format(false, false);
  }

  //........................................................................
  //-------------------------------- format --------------------------------

  /**
   * Format the value for printing.
   *
   * @param       inIgnoreUndefined true if undefined values should be returned
   *                                as empty
   *
   * @return      the command that can be printed
   *
   */
  public @Nonnull Command format(boolean inIgnoreUndefined)
  {
    return format(inIgnoreUndefined, false);
  }

  //........................................................................
  //-------------------------------- format --------------------------------

  /**
   * Format the value for printing.
   *
   * @param       inIgnoreUndefined true if undefined values should be returned
   *                                as empty
   * @param       inIgnoreFormatter true if set formattter should be ignored
   *
   * @return      the command that can be printed
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull Command format(boolean inIgnoreUndefined,
                                 boolean inIgnoreFormatter)
  {
    Command command = null;

    if(!isDefined())
      if(hasExpression())
        return new Command(m_expression);
      else
        if(inIgnoreUndefined)
          command = new Command(new Object[0]);
        else
          command = new Color("error", UNDEFINED);
    else
    {
      if(!inIgnoreFormatter && m_formatter != null)
        command = m_formatter.format((T)this);
      else
        command = doFormat();

      if(hasExpression())
        command = new Command(m_expression, " ", command);
    }

    if(m_remark != null)
      command = m_remark.format(command);

    return command;
  }

  //........................................................................
  //------------------------------- doFormat -------------------------------

  /**
   * Really to the formatting.
   *
   * @return      the command for setting the value
   *
   */
  protected abstract @Nonnull Command doFormat();

  //........................................................................

  //-------------------------------- group ---------------------------------

  /**
   * Return the group this value belongs to. As default we assume that each
   * distinct value has its own group. If other partitions are desired, they
   * have to be implemented in derivations or by using a grouping.
   *
   * @return      a string denoting the group this value is in
   *
   */
  @SuppressWarnings("unchecked")
  public @Nonnull String group()
  {
    if(m_grouping != null)
      return m_grouping.group((T)this);

    return doGroup();
  }

  //........................................................................
  //------------------------------- doGroup --------------------------------

  /**
   * Really do grouping for this object. This method can be derived to have
   * special grouping in derivations.
   *
   * @return      a string denoting the group this value is in
   *
   */
  protected @Nonnull String doGroup()
  {
    return this.toString(false);
  }

  //........................................................................

  //----------------------------- getEditType ------------------------------

  /**
   * Get the type used for editing.
   *
   * @return      a string representing the type to use for editing
   *
   */
  public @Nonnull String getEditType()
  {
    return m_editType;
  }

  //........................................................................
  //------------------------------ getChoices ------------------------------

  /**
   * Get the all the possible values this value can be edited with. Returns
   * null if no preselection is available.
   *
   * TODO: this could probably be done more generic by returning the edit type
   * and values at the same time.
   *
   * @return      the possible value to select from or null for no selection
   *
   */
  public @Nullable String getChoices()
  {
    return m_choices;
  }

  //........................................................................
  //------------------------------ getRelated ------------------------------

  /**
   * Get the all the possibly related values this value can be edited
   * with. Returns null if no related values are available.
   *
   * @return      the possible related values or none for null
   *
   */
  public @Nullable String getRelated()
  {
    return m_related;
  }

  //........................................................................
  //----------------------------- getEditValue -----------------------------

  /**
   * Get the value to be used for editing.
   *
   * @return      the value for editing
   *
   */
  public String getEditValue()
  {
    return toString();
  }

  //........................................................................
  //----------------------------- isArithmetic -----------------------------

  /**
   * Checks whether the value is arithmetic and thus can be computed with.
   *
   * @return      true if the value is arithemtic
   *
   */
  public boolean isArithmetic()
  {
    return true;
  }

  //........................................................................
  //---------------------------- hasExpression -----------------------------

  /**
   * Check if the value has an expression.
   *
   * @return      true if there is an expression, false if not
   *
   */
  public boolean hasExpression()
  {
    return m_expression != null;
  }

  //........................................................................
  //---------------------------- getExpression -----------------------------

  /**
   * Get the expression for this value.
   *
   * @return      the expression
   *
   */
  public @Nullable Expression getExpression()
  {
    return m_expression;
  }

  //........................................................................
  //------------------------------ getRemark -------------------------------

  /**
   * Get the remark for this value, if any.
   *
   * @return   the remark set for the value
   *
   */
  public @Nullable Remark getRemark()
  {
    return m_remark;
  }

  //........................................................................
  //------------------------------- compute --------------------------------

  /**
   * Compute a value for a given key.
   *
   * @param    inKey the key of the value to compute
   *
   * @return   the computed value
   *
   */
  public @Nullable Object compute(@Nonnull String inKey)
  {
    if("template".equals(inKey))
      if(m_template != null)
        return m_template;
      else
        return "(none)";

    return null;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

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
  public @Nonnull T add(@Nonnull T inValue)
  {
    throw new UnsupportedOperationException
      ("cannot add " + getClass() + " for " + this);
  }

  //........................................................................
  //------------------------------ subtract --------------------------------

  /**
   * Subtract the current value from the given one, return the result.
   *
   * @param       inValue the value to subtract from
   *
   * @return      the subtracted values
   *
   */
  public @Nonnull T subtract(@Nonnull T inValue)
  {
    throw new UnsupportedOperationException("cannot subtract from "
                                            + getClass());
  }

  //........................................................................
  //------------------------------- multiply -------------------------------

  /**
   * Multiply this value and return the result.
   *
   * @param       inMultiplier the multiplication factor
   *
   * @return      the multiplied value
   *
   */
  public @Nonnull T multiply(@Nonnull T inMultiplier)
   {
     throw new UnsupportedOperationException("cannot multiply " + getClass());
   }

  //........................................................................
  //-------------------------------- divide --------------------------------

  /**
   * Divide this value and return the result.
   *
   * @param       inDivisor the divisor factor
   *
   * @return      the divided value
   *
   */
  public @Nonnull T divide(@Nonnull T inDivisor)
  {
    throw new UnsupportedOperationException("cannot divide " + getClass());
  }

  //........................................................................
  //------------------------------- multiply -------------------------------

  /**
   * Multiply the value with the given value.
   *
   * @param       inValue the multiplication factor
   *
   * @return      the multiple value.
   *
   */
  public @Nonnull T multiply(long inValue)
  {
    throw new UnsupportedOperationException("multiplication for " + getClass());
  }

  //........................................................................
  //-------------------------------- divide --------------------------------

  /**
   * Divide the value with the given value.
   *
   * @param       inValue the divising
   *
   * @return      the divided value.
   *
   */
  public @Nonnull T divide(long inValue)
  {
    throw new UnsupportedOperationException("division for " + getClass());
  }

  //........................................................................
  //--------------------------------- max ----------------------------------

  /**
   * Compute the maximum of the two values.
   *
   * @param       inValue the value to add to this one
   *
   * @return      the maximum value (usually one of the values)
   *
   */
  public @Nonnull T max(@Nonnull T inValue)
  {
    throw new UnsupportedOperationException("cannot compute max for "
                                            + getClass());
  }

  //........................................................................
  //--------------------------------- min ----------------------------------

  /**
   * Compute the minimal of the two values.
   *
   * @param       inValue the value to add to this one
   *
   * @return      the minimal value (usually one of the values)
   *
   */
  public @Nonnull T min(@Nonnull T inValue)
  {
    throw new UnsupportedOperationException("cannot compute min for "
                                            + getClass());
  }

  //........................................................................

  //--------------------------------- read ---------------------------------

  /**
   * Read the value from the given string.
   *
   * @param       inText the text to read from
   *
   * @return      the value read, if any
   *
   */
  public @Nullable T read(@Nonnull String inText)
  {
    StringReader string = new StringReader(inText);
    ParseReader reader  = new ParseReader(string, "set");

    return read(reader);
  }

  //........................................................................

  //--------------------------------- read ---------------------------------

  /**
   * Try to read the value from the given stream.
   *
   * @param       inReader       the reader to read from
   *
   * @return      the value read
   *
   */
  public @Nullable T read(@Nonnull ParseReader inReader)
  {
    T result = create();

    // check if undefined is encountered
    if(inReader.expect(UNDEFINED))
      return result;

    result.m_remark = Remark.read(inReader);
    result.m_expression = Expression.read(inReader);

    // store the current position
    ParseReader.Position pos = inReader.getPosition();

    // could we read the value?
    if(!result.doRead(inReader))
    {
      // restore position
      inReader.seek(pos);

      if(result.hasExpression())
        return result;

      return null;
    }

    return result;
  }

  //........................................................................
  //-------------------------------- doRead --------------------------------

  /**
   * This is the derivable method that does the real reading.
   *
   * This method does not have to care about undefined values or about
   * resetting the current value or even handling the position for next reads
   * in case of error.
   *
   * @param       inReader the reader to read from
   *
   * @return      true if a valid value was read, false else
   *
   */
  protected abstract boolean doRead(@Nonnull ParseReader inReader);

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The Test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- readTest -------------------------------------------------------

    /**
     * A simple test routine for testing reads.
     *
     * @param inTests the note, test and return value triplets
     * @param inValue the value to read into
     *
     */
    public static void readTest(@Nonnull String []inTests,
                                @Nonnull Value inValue)
    {
      if(inTests.length % 4 != 0)
        throw new IllegalArgumentException("quadruplets of input test strings "
                                           + "must be given");

      for(int i = 0; i < inTests.length; i += 4)
      {
        ParseReader reader =
          new ParseReader(new java.io.StringReader(inTests[i + 1]), "test");

        Value value = inValue.read(reader);

        if(inTests[i + 2] == null)
          assertNull(i / 4 + ": " + inTests[i]
                      + ", should not have been read",
                      value);
        else
        {
          assertTrue(i / 4 + ": " + inTests[i] + ", should have been read",
                     value != null);
          assertEquals(i / 4 + ": " + inTests[i] + ", does not match",
                       inTests[i + 2], value.toString());
        }

        String rest = reader.readLine();

        if(inTests[i + 3] != null)
          assertEquals(i / 4 + ": " + inTests[i] + ", rest does not match",
                       inTests[i + 3], rest);
        else
          assertTrue(i / 4 + ": " + inTests[i] + ", still having rest '"
                     + rest + "'", rest.length() == 0);
      }
    }

    //......................................................................
    //----- setTest --------------------------------------------------------

    /**
     * A simple test routine for testing sets.
     *
     * @param inTests the note, test and return value triplets
     * @param inValue the value to read into
     *
     */
//     public static void setTest(@Nonnull String []inTests,
//                                @Nonnull Value inValue)
//     {
//       if(inTests.length % 4 != 0)
//       throw new IllegalArgumentException("quadruplets of input test strings "
//                                            + "must be given");

//       for(int i = 0; i < inTests.length; i += 4)
//       {
//         String rest = inValue.setFromString(inTests[i + 1]);

//         if(inTests[i + 2] == null)
//           assertTrue(i / 4 + ": " + inTests[i]
//                      + ", should not have been set",
//                      rest == inTests[i + 1]);
//         else
//           assertEquals(i / 4 + ": " + inTests[i] + ", does not match",
//                        inTests[i + 2], inValue.toString());

//         if(inTests[i + 3] != null)
//           assertEquals(i / 4 + ": " + inTests[i] + ", rest does not match",
//                        inTests[i + 3], rest);
//         else
//           assertTrue(i / 4 + ": " + inTests[i] + ", still having rest '"
//                      + rest + "'", rest.length() == 0);
//       }
//     }

    //......................................................................
    //----- createTest -----------------------------------------------------

    /**
     * A simple check to test clone(), create() and reset().
     *
     * @param inValue the value to test
     *
     */
    @SuppressWarnings("unchecked")
    public static void createTest(@Nonnull Value inValue)
    {
      // create a new value
      Value newValue = inValue.create();

      assertEquals("new not undefined", false, newValue.isDefined());
      assertEquals("new not undefined", UNDEFINED, newValue.toString());
      assertEquals("new not same class",
                   inValue.getClass(), newValue.getClass());
      assertEquals("new value not same formatter",
                   inValue.m_formatter, newValue.m_formatter);
      assertEquals("new value not same group",
                   inValue.m_grouping, newValue.m_grouping);
      assertEquals("new value not same edit type",
                   inValue.m_editType, newValue.m_editType);
      assertEquals("new value not same choices",
                   inValue.m_choices, newValue.m_choices);

      // clone the value
      Value clone = inValue.clone();

      assertEquals("clone not defined", true, clone.isDefined());
      assertEquals("not correctly cloned", 0, inValue.compareTo(clone));
      assertEquals("not correctly cloned", inValue.toString(),
                   clone.toString());
    }

    //......................................................................

    //----- value ----------------------------------------------------------

    /** A specific value class. */
    // CHECKSTYLE:OFF
    public static class TestValue extends Value<TestValue>
    {
      public TestValue()
      {
      }

      public TestValue(boolean inDefined)
      {
        m_defined = inDefined;
      }

      private boolean m_defined = false;

      @Override
	protected boolean doRead(@Nonnull ParseReader inReader)
      {
        m_defined = inReader.expect("guru");
        return m_defined;
      }

      public void reset()
      {
        m_defined = false;
      }

      @Override
	public @Nonnull Command doFormat()
      {
        return new Command("guru");
      }

      @Override
	public boolean isDefined()
      {
        return m_defined;
      }

      @Override
	public @Nonnull String doToString()
      {
        return "guru";
      }

      @Override
	public TestValue create()
      {
        TestValue copy = this.clone();
        copy.reset();
        return copy;
      }
    };
    // CHECKSTYLE:ON

    //......................................................................

    //----- value tests ----------------------------------------------------

    /** The value tests Test. */
    @org.junit.Test
    public void read()
    {
      String []tests =
      {
        "simple", "guru", "guru", null,
        "invalid", "hello", null, "hello",
        "remark", "{*} guru", "{*}guru", null,
        "invalid remark", "{* guru", null, "{* guru",
        "invalid value", "{*} hello", null, " hello",
        "valid with rest", "  guru   hello", "guru", "   hello",
      };

      readTest(tests, new TestValue());

      createTest(new TestValue(true));
    }

    //......................................................................
    //----- formatting -----------------------------------------------------

    /** The formatting Test. */
    @org.junit.Test
    public void formatting()
    {
      TestValue value = new TestValue();

      assertEquals("undefined", new Color("error", UNDEFINED),
                   value.format(false));
      assertEquals("undefined", new Command(new Object [0]),
                   value.format(true));

      value = new TestValue(true);
      assertEquals("defined", new Command("guru"),
                   value.format(false));
      assertEquals("defined", new Command("guru"),
                   value.format(true));


      StringReader string = new StringReader("{*} guru");
      ParseReader reader = new ParseReader(string, "test");
      value = new TestValue().read(reader);
      assertEquals("defined, remark",
                   new net.ixitxachitls.output.commands.Span
                   ("HOUSE_RULE",
                    new net.ixitxachitls.output.commands.Window
                    (new Command("guru"), "House Rule")),
                   value.format(false));
      assertEquals("defined, remark",
                   new net.ixitxachitls.output.commands.Span
                   ("HOUSE_RULE",
                    new net.ixitxachitls.output.commands.Window
                    (new Command("guru"), "House Rule")),
                   value.format(true));
    }

    //......................................................................
    //----- equalsHash -----------------------------------------------------

    /** The equalsHash Test. */
    @org.junit.Test
    public void equalsHash()
    {
      TestValue value1 = new TestValue(true);
      TestValue value2 = new TestValue(true);

      assertTrue("equals", value1.equals(value2));
      assertTrue("equals", value2.equals(value1));
      assertEquals("hash", value1.hashCode(), value2.hashCode());

      value2 = new TestValue();
      assertFalse("equals", value1.equals(value2));
      assertFalse("equals", value2.equals(value1));
      assertFalse("hash", value1.hashCode() == value2.hashCode());
    }

    //......................................................................
  }

  //........................................................................
}
