/******************************************************************************
 * Copyright (c) 2002-2007 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * General utility functions for dealing with classes.
 *
 * @file          Classes.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public final class Classes
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Classes --------------------------------

  /**
   * Prevent instantiation of static class.
   *
   */
  private Classes()
  {
    // nothing to do here, never called
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- toClassName ------------------------------

  /**
    * Convert the given name into a possible class name.
    *
    * This means to convert the leading character into an upper case character
    * and by removing spaces while at the same time making the following
    * character upper case.
    *
    * @param       inName    the name of the convert
    * @param       inPackage the name of the package to use
    *
    * @return      the converted name
    *
    */
  public static @Nonnull String toClassName
    (@Nonnull String inName, @Nullable String inPackage)
  {
    // trim white spaces
    inName = inName.trim().replaceAll("\\s+", " ");

    // make the first character upper case
    StringBuilder result =
      new StringBuilder("" + Character.toUpperCase(inName.charAt(0)));

    // replace spaces (and following character to upper case
    int pos, start;
    for(pos = inName.indexOf(" ", 1), start = 1;
        pos >= 0;
        start = pos + 2, pos = inName.indexOf(" ", start))
    {
      result.append(inName.substring(start, pos));
      result.append(Character.toUpperCase(inName.charAt(pos + 1)));
    }

    // last space cannot be at the end
    result.append(inName.substring(start));

    if(inPackage != null && inPackage.length() > 0)
      return inPackage + "." + result.toString();
    else
      return result.toString();
  }

  //........................................................................
  //---------------------------- fromClassName -----------------------------

  /**
    *
    * Convert the name of the given class into a String.
    *
    * The name is converted without package and before all uppercase characters
    * as space is inserted.
    *
    * @param       inClass the class to convert from
    *
    * @return      the converted name
    *
    * @undefined   return null if a null name is given (but not with a null
    *              package)
    *
    */
  public static @Nonnull String fromClassName(@Nonnull Class inClass)
  {
    String name = inClass.getName();

    name = name.substring(name.lastIndexOf('.') + 1);

    return name.replaceAll("(\\w)(\\p{Upper}\\p{Lower})", "$1 $2");
  }

  //........................................................................
  //------------------------------ getPackage ------------------------------

  /**
    *
    * Get the package name from the given class.
    *
    * @param       inClass the class to get the package name from
    *
    * @return      the package name of the class
    *
    * @undefined   if given class is null
    *
    */
  public static @Nonnull String getPackage(@Nonnull Class inClass)
  {
    String name = inClass.getName();

    return name.substring(0, name.lastIndexOf("."));
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
    /** Test name to class conversion. */
    @org.junit.Test
    public void toClassName()
    {
      assertEquals("wrong conversion", "my.package.JustATest",
                   Classes.toClassName("just a test", "my.package"));
      assertEquals("wrong conversion", "my.package.AnotherOfTheTests",
                   Classes.toClassName("   Another \n  of \t  the  tests   \n",
                                       "my.package"));

      // null values
      assertEquals("wrong conversion", "JustATest",
                   Classes.toClassName("just a test", null));
    }

    /** Test class to name conversion. */
    @org.junit.Test
    public void fromClassName()
    {
      // from a class
      assertEquals("wrong name from class", "Classes",
                   Classes.fromClassName(Classes.class));
    }

    /** Test package extraction. */
    @org.junit.Test
    public void getPackage()
    {
      // get package name
      assertEquals("package", "net.ixitxachitls.util",
                   Classes.getPackage(Classes.class));
    }

    /** Tests to make coverage happy. */
    @org.junit.Test
    public void coverage()
    {
      new Classes();
    }
  }

  //........................................................................
}
