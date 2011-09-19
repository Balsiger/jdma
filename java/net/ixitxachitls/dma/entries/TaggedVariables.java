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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is an auxiliary class used to stored all parsable values of an
 * individual class. This class adds the possibility to add tags to the
 * keywords.
 *
 * @file          TaggedVariables.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class TaggedVariables extends Variables
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- TaggedValues -----------------------------

  /**
   * Create the object.
   *
   * @param       inTag       the tag to use
   * @param       inVariables the variables stored here
   *
   */
  public TaggedVariables(@Nonnull String inTag,
                         @Nonnull Variable ... inVariables)
  {
    m_tag = inTag;

    // Cannot call super, as we have to store the tag first.
    for(Variable variable : inVariables)
      add(variable);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The tag to use. */
  private @Nonnull String m_tag;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getPrefix -------------------------------

  /**
   * Get the prefix for all the keywords.
   *
   * @return      the tag
   *
   */
  public @Nonnull String getPrefix()
  {
    return m_tag + ":";
  }

  //........................................................................
  //------------------------------ getValue --------------------------------

  /**
   * Get all a specific variable stored.
   *
   * @param       inKey the name of the variable to get
   *
   * @return      the specific variable
   *
   */
  public @Nullable Variable getVariable(@Nonnull String inKey)
  {
    Variable var = super.getVariable(inKey);
    if(var != null)
      return var;

    return super.getVariable(m_tag + ":" + inKey);
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert the object to a human readable String.
   *
   * @return      the String representation
   *
   */
  public @Nonnull String toString()
  {
    return super.toString() + " [tag " + m_tag + "]";
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- add ----------------------------------

  /**
   * Add a variable to the values.
   *
   * @param       inVariable the variable to add
   *
   */
  protected void add(@Nonnull Variable inVariable)
  {
    super.add(m_tag + ":" + inVariable.getKey(), inVariable);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //--------------------------------- tag ----------------------------------

  /**
   * Create a TaggedValues object from a tag and a Values object.
   *
   * @param       inTag  the tag
   * @param       inVars the values
   *
   * @return      the TaggedValues object
   *
   * @undefined   null is returned if no values is given
   *
   */
  public static Variables tag(@Nonnull String inTag, @Nonnull Variables inVars)
  {
    TaggedVariables result = new TaggedVariables(inTag);

    result.add(inVars);

    return result;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
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
        new TaggedVariables("tag",
                            new Variable("1234", field, false, true, false,
                                         false, false, null, null, false),
                            new Variable("123456", field, false, true, false,
                                         false, false, null, null, false),
                            new Variable("123", field, false, true, false,
                                         false, false, null, null, false),
                            new Variable("1", field, false, true, false, false,
                                         false, null, null, false));

      assertEquals("width", 11, variables.getKeyWidth());
      assertEquals("string",
                   "tag:1234=var 1234 (not editable, DM), "
                   + "tag:123456=var 123456 (not editable, DM), "
                   + "tag:123=var 123 (not editable, DM), "
                   + "tag:1=var 1 (not editable, DM) [tag tag]",
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
        new Variables(new Variable("1234", field, false, true, false, false,
                                   false, null, null, false),
                      new Variable("123456", field, false, true, false, false,
                                   false, null, null, false),
                      new Variable("123", field, false, true, false, false,
                                   false, null, null, false),
                      new Variable("1", field, false, true, false, false, false,
                                   null, null, false));

      variables = TaggedVariables.tag("tag", variables);

      assertEquals("variable", "1234",
                   variables.getVariable("1234").getKey());
      assertEquals("variable", "1234",
                   variables.getVariable("tag:1234").getKey());
      assertNull("not found", variables.getVariable("guru"));
      assertContent("keys", variables.getKeywords(),
                    "tag:1234", "tag:123456", "tag:123", "tag:1");
      assertEquals("prefix", "tag:", variables.getPrefix());
      Iterator<Variable> i = variables.iterator();
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
