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

package net.ixitxachitls.util.errors;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the class for all resource errors.
 *
 * @file          ResourceError.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ThreadSafe
@ParametersAreNonnullByDefault
public class ResourceError extends BaseError
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- ResourceError -----------------------------

  /**
   * Create a resource error.
   *
   * @param       inID   the id of the error
   * @param       inFile the name of the resource with the problem
   *
   */
  public ResourceError(String inID, String inFile)
  {
    this(inID, inFile, null);
  }

  //........................................................................
  //---------------------------- ResourceError -----------------------------

  /**
   * Create a resource error.
   *
   * @param       inID        the id of the error
   * @param       inFile      the name of the resource with the problem
   * @param       inException the exception that occurred
   *
   */
  public ResourceError(String inID, String inFile,
                       @Nullable Exception inException)
  {
    super(inID, "resource file " + inFile, inException);

    // m_file = inFile;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Default serial version id. */
  private static final long serialVersionUID = 1L;

  /** The resource with the error. */
  // private String m_file;

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
  @Override
  public boolean equals(Object inOther) // $codepro.audit.disable
  {
    if (this == inOther)
      return true;

    if(!(inOther instanceof ResourceError))
      return false;

    return super.equals(inOther);
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
    return super.hashCode();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
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
                   "Resource Error: [test] " + s_default
                   + " (resource file path/file.ext)",
                   new ResourceError("test", "path/file.ext").toString());
    }

    //......................................................................
    //----- equals ---------------------------------------------------------

    /** equals Test. */
    @org.junit.Test
    public void equals()
    {
      ResourceError error1 = new ResourceError("test", "file");
      ResourceError error2 = new ResourceError("test2", "file");
      ResourceError error3 = new ResourceError("test", "file2");
      ResourceError error4 = new ResourceError("test", "file");

      assertFalse("not equals message", error1.equals(error2));
      assertFalse("not equals file", error1.equals(error3));
      assertTrue("equals", error1.equals(error4));
      assertTrue("equals, same object", error1.equals(error1));
      assertFalse("not equals null", error1.equals(null));
      assertFalse("not equals string", error1.equals("test"));

      assertFalse("not same hash", error1.hashCode() == error2.hashCode());
      assertFalse("not same hash", error1.hashCode() == error3.hashCode());
      assertTrue("same hash", error1.hashCode() == error4.hashCode());
      assertTrue("same hash, same object",
                 error1.hashCode() == error1.hashCode());
    }

    //......................................................................
  }

  //........................................................................
}
