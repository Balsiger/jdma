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

package net.ixitxachitls.util.errors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import net.ixitxachitls.util.Classes;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base class for all errors.
 *
 * @file          BaseError.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ThreadSafe
public class BaseError
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ BaseError -------------------------------

  /**
   * Create a base error.
   *
   * @param       inID the id of the error
   *
   */
  public BaseError(@Nonnull String inID)
  {
    this(inID, null, null);
  }

  //........................................................................
  //------------------------------ BaseError -------------------------------

  /**
   * Create a base error.
   *
   * @param       inID      the id of the error
   * @param       inSpecial the special text for this error
   *
   */
  public BaseError(@Nonnull String inID, @Nullable String inSpecial)
  {
    this(inID, inSpecial, null);
  }

  //........................................................................
  //------------------------------ BaseError -------------------------------

  /**
   * Create a base error.
   *
   * @param       inID        the id of the error
   * @param       inSpecial   some special text for this specific error
   * @param       inException an exception that happened
   *
   */
  public BaseError(@Nonnull String inID, @Nullable String inSpecial,
                   @Nullable Exception inException)
  {
    m_id        = inID;
    m_special   = inSpecial;
    m_exception = inException;

    // compute the message now to be able to use it for equals() and hashCode()
    m_description = getMessage(m_id);
    m_message = Classes.fromClassName(getClass()) + ": " + m_description;

    if(m_special != null)
      m_message += " (" + m_special + ")";

    if(m_exception != null)
      m_message += " [" + m_exception + "]";
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The ID of the error. */
  private @Nonnull String m_id;

  /** The special description text. */
  protected @Nullable String m_special = null;

  /** The exception that happened with this error. */
  protected @Nullable Exception m_exception = null;

  /** The error description. */
  private @Nonnull String m_description;

  /** The whole message text. */
  protected @Nonnull String m_message;

  /** The default error description. */
  protected static final @Nonnull String s_default =
    Config.get("resource:errors/default",
               "no definition found for this error");

  //........................................................................

  //-------------------------------------------------------------- accessors

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
    if(inOther == null)
      return false;

    if(inOther instanceof BaseError)
      return m_message.equals(((BaseError)inOther).m_message);
    else
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
    return m_message.hashCode();
  }

  //........................................................................

  //------------------------------ isInvalid -------------------------------

  /**
   * Determine if the error is still valid or if it has been corrected.
   *
   * @return      true if invalid (i.e. has been corrected), false else
   *
   */
  public boolean isInvalid()
  {
    return false;
  }

  //........................................................................

  //------------------------------ getMessage ------------------------------

  /**
   * Get the error message for the given id.
   *
   * @param       inID the id of the message to obtain
   *
   * @return      the text of the error message
   *
   */
  public static @Nonnull String getMessage(@Nonnull String inID)
  {
    return Config.get("resource:errors/" + inID,
                      "[" + inID + "] " + s_default);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- toString -------------------------------

  /**
   * Convert the error to a String.
   *
   * @return      a String representation
   *
   */
  public @Nonnull String toString()
  {
    return m_message;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- string ---------------------------------------------------------

    /** Test string handling. */
    @org.junit.Test
    public void string()
    {
      assertEquals("convert to string",
                   "Base Error: [test] " + s_default,
                   new BaseError("test").toString());

      assertEquals("convert to string",
                   "Base Error: [test] " + s_default + " (special)",
                   new BaseError("test", "special").toString());

      assertEquals("convert to string",
                   "Base Error: [test] " + s_default
                   + " (special) [java.lang.Exception: guru]",
                   new BaseError("test", "special",
                                 new Exception("guru")).toString());
    }

    //......................................................................
    //----- misc -----------------------------------------------------------

    /** Test equals, hashCode and validity. */
    @org.junit.Test
    public void misc()
    {
      BaseError error1 = new BaseError("test");
      BaseError error2 = new BaseError("test");
      BaseError error3 = new BaseError("test", "special");
      BaseError error4 = new BaseError("test", "special");

      assertTrue("1-2", error1.equals(error2));
      assertTrue("2-1", error2.equals(error1));

      assertFalse("1-3", error1.equals(error3));
      assertFalse("3-1", error3.equals(error1));

      assertTrue("3-4", error3.equals(error4));
      assertTrue("4-3", error4.equals(error3));

      assertTrue("reflexivity", error1.equals(error1));
      assertTrue("consistency", error1.equals(error2));
      assertTrue("consistency", error1.equals(error2));
      assertTrue("consistency", error1.equals(error2));

      // handling of null
      assertFalse("null", error1.equals(null));

      // comparing with other objects
      assertFalse("other", error1.equals("guru"));

      // hashCode
      assertEquals("hash code", error1.hashCode(), error2.hashCode());
      assertEquals("hash code", error3.hashCode(), error4.hashCode());

      // validity
      assertFalse("invalid", error1.isInvalid());
      assertFalse("invalid", error2.isInvalid());
      assertFalse("invalid", error3.isInvalid());
      assertFalse("invalid", error4.isInvalid());
    }

    //......................................................................
  }

  //........................................................................
}
