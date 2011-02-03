/******************************************************************************
 * Copyright (c) 2002,2003 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.output.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.util.PublicCloneable;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the command object that stores all the commands for the output.
 *
 * @file          Command.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Command implements PublicCloneable
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Command --------------------------------

  /**
   * Create a command from a static objects (strings or commands). Any strings
   * will not be parsed.
   *
   * @param       inTexts the static texts
   *
   */
  public Command(@Nonnull Object ... inTexts)
  {
    m_arguments.addAll(Arrays.asList(inTexts));
  }

  //........................................................................
  //------------------------------- Command --------------------------------

  /**
   * Create a command from a static objects (strings or commands). Any strings
   * will not be parsed.
   *
   * @param       inTexts the static texts
   *
   */
  public Command(@Nonnull List<Object> inTexts)
  {
    m_arguments.addAll(inTexts);
  }

  //........................................................................
  //------------------------------- Command --------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected Command()
  {
    // nothing to do
  }

  //........................................................................

  //-------------------------------- clone ---------------------------------

  /**
   * Make a copy of the command and return it. If necessary, this is a deep
   * copy.
   *
   * @return      a copy of the current value
   *
   */
  @SuppressWarnings("unchecked")
  public Command clone()
  {
    try
    {
      return (Command)super.clone();
    }
    catch(CloneNotSupportedException e)
    {
      throw new UnsupportedOperationException("Strange, clone should be "
                                              + "supported: " + e);
    }
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The arguments. */
  protected @Nullable java.util.List<Object> m_arguments =
    new ArrayList<Object>();

  /** The name of the package for this class. */
  protected static final @Nonnull String s_package =
    Command.class.getName().substring(0, Command.class.getName()
                                      .lastIndexOf('.') + 1);
                                              // Class.getPackage() is null
                                              // when testing

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getArguments -----------------------------

  /**
   * Get all the arguments.
   *
   * @return      all arguments (as Commands or Objects)
   *
   */
  public @Nonnull List<Object> getArguments()
  {
    return Collections.unmodifiableList(m_arguments);
  }

  //........................................................................

  //------------------------------- isEmpty --------------------------------

  /**
   * Check if the command represents any data at all.
   *
   * @return      true if the command is empty, false if not
   *
   */
  public boolean isEmpty()
  {
    for(Object element : m_arguments)
    {
      if(element instanceof String && !((String)element).isEmpty())
        return false;

      if(element instanceof Command && !((Command)element).isEmpty())
        return false;
    }

    return true;
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
  public boolean equals(Object inOther)
  {
    if(this == inOther)
      return true;

    if(inOther == null)
      return false;

    if(inOther instanceof Command)
      return m_arguments.equals(((Command)inOther).m_arguments);

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
  public int hashCode()
  {
    return m_arguments.hashCode();
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert the command to a human readable String, mainly for debugging.
   *
   * @return      a human readable conversion of the object
   *
   */
  public @Nonnull String toString()
  {
    StringBuilder result = new StringBuilder();

    for(Object next : m_arguments)
      if(next == null)
        result.append("*null*");
      else
        result.append(next.toString());

    return result.toString();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- simple ---------------------------------------------------------

    /** The simple Test. */
    @org.junit.Test
    public void simple()
    {
      Command command = new Command("just ", "a", " test");
      assertEquals("simple", "just a test", command.toString());
      assertEquals("clone", command, command.clone());
      assertFalse("empty", command.isEmpty());

      command = new Command();
      assertEquals("string", "", command.toString());
      assertEquals("clone", command, command.clone());
      assertTrue("empty", command.isEmpty());

      command = new Command("a ", null,
                            new Command("test ", "with ", "nested "),
                            "commands");
      assertEquals("string", "a *null*test with nested commands",
                   command.toString());
      assertEquals("clone", command, command.clone());
      assertFalse("empty", command.isEmpty());
    }

    //......................................................................
  }

  //........................................................................
}
