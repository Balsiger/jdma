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

package net.ixitxachitls.util;

import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableList;

import com.sun.tools.classfile.Type;
import net.ixitxachitls.util.logging.Log;

/**
 * General utility functions for dealing with classes.
 *
 * @file          Classes.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@ParametersAreNonnullByDefault
public final class Classes
{
  /**
   * Prevent instantiation of static class.
   */
  private Classes()
  {
    // nothing to do here, never called
  }

  /** The known prefixes for methods. */
  private static final List<String> METHOD_PREFIXES =
    ImmutableList.of("get", "is", "has", "to");

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
  public static String toClassName(String inName, @Nullable String inPackage)
  {
    // trim white spaces
    inName = inName.trim().replaceAll("\\s+", " ");

    // make the first character upper case
    StringBuilder result =
      new StringBuilder("" + Character.toUpperCase(inName.charAt(0)));

    // replace spaces (and following character to upper case
    int pos;
    int start;
    for(pos = inName.indexOf(' ', 1), start = 1;
        pos >= 0;
        start = pos + 2, pos = inName.indexOf(' ', start))
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

  /**
    * Convert the name of the given class into a String.
    *
    * The name is converted without package and before all uppercase characters
    * as space is inserted.
    *
    * @param       inClass the class to convert from
    *
    * @return      the converted name
    */
  public static String fromClassName(Class<?> inClass)
  {
    String name = inClass.getName();

    name = name.substring(name.lastIndexOf('.') + 1);

    return name.replaceAll("(\\w)(\\p{Upper}\\p{Lower})", "$1 $2");
  }

  public static String toSnakeCaseName(Class<?> inClass)
  {
    String name = inClass.getName();

    name = name.substring(name.lastIndexOf('.') + 1);

    return name
        .replaceAll("(\\w)(\\p{Upper}\\p{Lower})", "$1_$2")
        .replaceAll("\\$", "_")
        .toLowerCase();
  }

  /**
    * Get the package name from the given class.
    *
    * @param       inClass the class to get the package name from
    *
    * @return      the package name of the class
    */
  public static String getPackage(Class<?> inClass)
  {
    String name = inClass.getName();

    return name.substring(0, name.lastIndexOf('.'));
  }

  /**
   * Obtains the method with the given name from the given class, traversing to
   * superclasses if necessary.
   *
   * @param     inClass     the class to get the method from
   * @param     inName      the name of the method
   * @param     inArguments the argument types for the method
   *
   * @return    the method found
   */
  public static @Nullable Method getMethod(Class<?> inClass, String inName,
                                           Class<?> ... inArguments)
  {
    Class<?> current = inClass;
    Method method = null;
    while (current != Object.class)
    {
      try
      {
        method = current.getDeclaredMethod(inName, inArguments);
        break;
      }
      catch (NoSuchMethodException e)
      {
        current = current.getSuperclass();
      }
    }

    return method;
  }

  /**
   * Call the name method with the given arguments.
   *
   * @param       inName      the name of the method to call
   * @param       inObject    the object in which to call the method
   * @param       inArguments the arguments to call the method with
   *
   * @return      the result of the method, or null if something failed
   */
  public static @Nullable Object callMethod(String inName, Object inObject,
                                            Object ... inArguments)
  {
    Class<?> []arguments = new Class<?>[inArguments.length];
    for(int i = 0; i < inArguments.length; i++)
      if(inArguments[i] != null)
        arguments[i] = inArguments[i].getClass();
      else
        arguments[i] = Object.class;

    String name = Encodings.toCamelCase(inName);

    Class objectClass;
    if(inObject instanceof Class)
      objectClass = (Class)inObject;
    else
      objectClass = inObject.getClass();

    Method method = getMethod(objectClass, name, arguments);
    if(method == null)
    {
      String stem = Character.toUpperCase(name.charAt(0)) + name.substring(1);;
      for(String prefix : METHOD_PREFIXES)
      {
        name = prefix + stem;
        method = getMethod(objectClass, name, arguments);
        if(method != null)
          break;
      }

      if(method == null)
        return null;
    }

    try
    {
      // Make this accessible in case it's used in a non-public inner
      // class.
      method.setAccessible(true);
      return method.invoke(inObject, inArguments);
    }
    catch(IllegalAccessException e)
    {
      Log.warning("cannot access method: " + e);
      e.printStackTrace(System.err);
    }
    catch(java.lang.reflect.InvocationTargetException e)
    {
      Log.warning("cannot invoke method: " + e);
      e.printStackTrace(System.err);
    }

    return null;
  }

  //----------------------------------------------------------------------------

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

    private static class BaseClass {
      public String foo() {
        return "base foo";
      }

      public static String bar() {
        return "base bar";
      }

      public static String baseBar() {
        return "base base bar";
      }
    }

    private static class DerivedClass extends BaseClass {
      public String foo() {
        return "derived foo";
      }

      public String foobar() {
        return "foobar";
      }

      public static String bar() {
        return "derived bar";
      }
    }

    /** Test for invocation. */
    @org.junit.Test
    public void callMethod()
    {
      assertEquals("base", "base foo",
                   Classes.callMethod("foo", new BaseClass()));
      assertEquals("derived", "derived foo",
                   Classes.callMethod("foo", new DerivedClass()));
      assertEquals("static base", "base bar",
                   Classes.callMethod("bar", BaseClass.class));
      assertEquals("static derived", "derived bar",
                   Classes.callMethod("bar", DerivedClass.class));
      assertEquals("static base", "base base bar",
                   Classes.callMethod("baseBar", DerivedClass.class));
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
