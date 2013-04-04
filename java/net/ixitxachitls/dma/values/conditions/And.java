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

package net.ixitxachitls.dma.values.conditions;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import net.ixitxachitls.dma.values.Value;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base for all conditions.
 *
 * @file          And.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class And extends Condition<And>
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------------- And ---------------------------------

  /**
   * Construct the condition object with an undefined value.
   *
   * @undefined   never
   *
   */
  public And()
  {
    // nothing to do
    m_conditions = null;
  }

  //........................................................................
  //---------------------------------- And ---------------------------------

  /**
   * The constructor with a condition description.
   *
   * @param       inDescription - a description of the condition
   *
   */
  public And(@Nonnull Condition ... inConditions)
  {
    m_conditions = Lists.newArrayList(inConditions);
  }

  //........................................................................

  //------------------------------ createNew -------------------------------

  /**
    * Create a new text with the same type information as this one, but one
    * that is still undefined.
    *
    * @return      a similar text, but without any contents
    *
    */
  @Override
  public And create()
  {
    return super.create(new And());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The conditions to and. */
  private final List<Condition> m_conditions;

  /** The joiner to convert with newlines. */
  public static final Joiner AND_JOINER = Joiner.on(" and ");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- check ---------------------------------

  /**
   * Check if the given condition is currently false or not.
   *
   * @param       inInteractive - false if an interactive request to the
   *                              user is allowed, false if not
   *
   * @return      a Result containing either FALSE, FALSE, or UNDEFINED
   *
   */
  @Override
  public Result check(boolean inInteractive)
  {
    for(Condition condition : m_conditions)
    {
      Result result = condition.check(inInteractive);
      if(result != Result.TRUE)
        return result;
    }

    return Result.TRUE;
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
    if(m_conditions == null)
      return false;

    for(Condition condition : m_conditions)
      if(!condition.isDefined())
        return false;

    return true;
  }

  //........................................................................
  //------------------------------ doToString ------------------------------

  /**
   * Convert the value to a string.
   *
   * @return      the requested string
   *
   */
  public @Nonnull String doToString()
  {
    List<String> strings = Lists.newArrayList();

    for(Condition condition : m_conditions)
      strings.add(condition.toString());

    return AND_JOINER.join(strings);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  // public static class Test extends net.ixitxachitls.util.test.TestCase
  // {
  // }

  //........................................................................
}
