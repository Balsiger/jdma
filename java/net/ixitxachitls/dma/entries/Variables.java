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

package net.ixitxachitls.dma.entries;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.util.Strings;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is an auxiliary class used to store all parsable variables of an
 * individual class.
 *
 * @file          Variables.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Variables implements Iterable<Variable>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Variables ------------------------------

  /**
   * Default constructor.
   *
   * @param  inVariables the variables to be stored here
   *
   */
  public Variables(Variable ... inVariables)
  {
    for(Variable variable : inVariables)
      add(variable);
  }

  //........................................................................
  //------------------------------- Variables ------------------------------

  /**
   * Default constructor.
   *
   * @param  inVariables the variables to be stored here
   *
   */
  public Variables(Iterable<Variable> inVariables)
  {
    add(inVariables);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The maximal widths of the keys. */
  private int m_keyWidth  = 0;

  /** The keywords available, contains the variables again. */
  private @Nonnull Map<String, Variable> m_variables =
    new LinkedHashMap<String, Variable>();

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getPrefix -------------------------------

  /**
   * Get the prefix for all the keywords.
   *
   * @return      empty String
   *
   */
  public @Nonnull String getPrefix()
  {
    return "";
  }

  //........................................................................
  //----------------------------- getVariable ------------------------------

  /**
   * Get a specific variable stored.
   *
   * @param       inKey the name of the variable to get the value for
   *
   * @return      the specific variable
   *
   */
  public @Nullable Variable getVariable(@Nonnull String inKey)
  {
    return m_variables.get(inKey);
  }

  //........................................................................
  //------------------------------- iterator -------------------------------

  /**
   * Get all the variables stored.
   *
   * @return      iterator with the values
   *
   */
  @Override
public Iterator<Variable> iterator()
  {
    return m_variables.values().iterator();
  }

  //........................................................................
  //----------------------------- getKeywords ------------------------------

  /**
   * Get all the keywords stored.
   *
   * @return      iterator with the keywords
   *
   */
  public @Nonnull Iterator<String> getKeywords()
  {
    return m_variables.keySet().iterator();
  }

  //......................................................................
  //----------------------------- getKeyWidth ------------------------------

  /**
   * Get all the maximal width of the keywords.
   *
   * @return      the maximal width
   *
   */
  public int getKeyWidth()
  {
    return m_keyWidth;
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert the object to a human readable String.
   *
   * @return      the String representation
   *
   */
  @Override
public @Nonnull String toString()
  {
    return Strings.toString(m_variables.entrySet().iterator(), ", ", "<empty>");
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- add ----------------------------------

  /**
   * Add a variable to the values.
   *
   * @param       inKey      the key to add the variable for
   * @param       inVariable the variable to add
   *
   */
  protected void add(@Nonnull String inKey, @Nonnull Variable inVariable)
  {
    if(inKey.length() >= m_keyWidth)
      m_keyWidth = inKey.length() + 1;

    m_variables.put(inKey, inVariable);
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add a variable to the values.
   *
   * @param       inVariable the variable to add
   *
   */
  protected void add(@Nonnull Variable inVariable)
  {
    add(inVariable.getKey(), inVariable);
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add all values to the current values.
   *
   * @param       inValues the variables to add
   *
   */
  protected void add(@Nonnull Iterable<Variable> inValues)
  {
    for(Variable var : inValues)
      if(var != null)
        add(var);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** A simple field used for testing. */
    private @Nullable Object m_field = null;

    //----- width ----------------------------------------------------------

    /**
     * The width Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void width() throws Exception
    {
      java.lang.reflect.Field field =
        Variables.Test.class.getDeclaredField("m_field");
      Variables variables =
        new Variables(new Variable("1234", field, false, false)
                      .withDM(true),
                      new Variable("123456", field, false, false)
                      .withDM(true),
                      new Variable("123", field, false, false)
                      .withDM(true),
                      new Variable("1", field, false, false)
                      .withDM(true));

      assertEquals("width", 7, variables.getKeyWidth());
      assertEquals("string",
                   "1234=var 1234 (not editable, DM), "
                   + "123456=var 123456 (not editable, DM), "
                   + "123=var 123 (not editable, DM), "
                   + "1=var 1 (not editable, DM)",
                   variables.toString());
    }

    //......................................................................
    //----- variables ------------------------------------------------------

    /**
     * The variables Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void variables() throws Exception
    {
      java.lang.reflect.Field field =
        Variables.Test.class.getDeclaredField("m_field");
      Variables variables =
        new Variables(new Variable("1234", field, false, false)
                      .withDM(true),
                      new Variable("123456", field, false, false)
                      .withDM(true),
                      new Variable("123", field, false, false)
                      .withDM(true),
                      new Variable("1", field, false, false)
                      .withDM(true));

      assertEquals("variable", "1234", variables.getVariable("1234").getKey());
      assertNull("not found", variables.getVariable("guru"));
      assertContent("keys", variables.getKeywords(),
                    "1234", "123456", "123", "1");
      Iterator<Variable> i = variables.iterator();
      assertEquals("values", "1234", i.next().getKey());
      assertEquals("values", "123456", i.next().getKey());
      assertEquals("values", "123", i.next().getKey());
      assertEquals("values", "1", i.next().getKey());
      assertFalse("end", i.hasNext());

      variables = new Variables(variables);

      assertEquals("variable", "1234", variables.getVariable("1234").getKey());
      assertNull("not found", variables.getVariable("guru"));
      assertContent("keys", variables.getKeywords(),
                    "1234", "123456", "123", "1");
      i = variables.iterator();
      assertEquals("values", "1234", i.next().getKey());
      assertEquals("values", "123456", i.next().getKey());
      assertEquals("values", "123", i.next().getKey());
      assertEquals("values", "1", i.next().getKey());
      assertFalse("end", i.hasNext());
    }

    //......................................................................
  }

  //........................................................................
}
