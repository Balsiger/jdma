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

package net.ixitxachitls.dma.entries;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.util.Classes;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The type abstract base of the entriy types.
 * TODO: change to abstract entry when that is available.
 *
 *
 * @file          AbstractType.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <T> the type represented by this type spec
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public abstract class AbstractType<T /*extends ValueGroup*/>
  implements Comparable<AbstractType>, java.io.Serializable
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- AbstractType -----------------------------

  /**
   * Create the type.
   *
   * @param       inClass the class represented by this type
   *
   */
  public AbstractType(@Nonnull Class<T> inClass)
  {
    this(inClass, Classes.fromClassName(inClass) + "s");
  }

  //........................................................................
  //----------------------------- AbstractType -----------------------------

  /**
   * Create the type.
   *
   * @param       inClass    the class represented by this type
   * @param       inMultiple the name to use for multiple entries of the type
   *
   */
  public AbstractType(@Nonnull Class<T> inClass, @Nonnull String inMultiple)
  {
    m_name      = Classes.fromClassName(inClass).toLowerCase(Locale.US);
    m_class     = inClass;
    m_multiple  = inMultiple;
    m_className = inClass.getName().replaceAll(".*\\.", "");
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** the name of the type. */
  private @Nonnull String m_name;

  /** the name for multiple instances of the type. */
  private @Nonnull String m_multiple;

  /** the class for the type. */
  private @Nonnull Class m_class;

  /** The class name without package. */
  private @Nonnull String m_className;

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- getLink -------------------------------

  /**
   * Get the name to link to such a type.
   *
   * @return      the link
   *
   */
  public @Nonnull String getLink()
  {
    return "/entry/" + m_name.replaceAll(" ", "").toLowerCase(Locale.US) + "/";
  }

  //.......................................................................
  //-------------------------------- getName -------------------------------

  /**
   * Get the name for this type.
   *
   * @return      the class name
   *
   */
  public @Nonnull String getName()
  {
    return m_name;
  }

  //........................................................................
  //----------------------------- getClassName -----------------------------

  /**
   * Get the class name (without package) for this type.
   *
   * @return      the class name
   *
   */
  public @Nonnull String getClassName()
  {
    return m_className;
  }

  //........................................................................
  //------------------------------ getMultiple -----------------------------

  /**
   * Get the name for multiples of this type.
   *
   * @return      the multiple name
   *
   */
  public @Nonnull String getMultiple()
  {
    return m_multiple;
  }

  //........................................................................
  //---------------------------- getMultipleLink ---------------------------

  /**
   * Get the name for multiples of this type.
   *
   * @return      the multiple name
   *
   */
  public @Nonnull String getMultipleLink()
  {
    return m_multiple.replaceAll(" ", "").toLowerCase(Locale.US);
  }

  //........................................................................
  //---------------------------- getMultipleDir ----------------------------

  /**
   * Get the name for multiples of this type.
   *
   * @return      the multiple name as directory reference
   *
   */
  public @Nonnull String getMultipleDir()
  {
    return m_multiple.replaceAll(" ", "");
  }

  //........................................................................

  //------------------------------ compareTo -------------------------------

  /**
   * Compare this type to another one for sorting.
   *
   * @param       inOther the other type to compare to
   *
   * @return      < 0 if this is lower, > if this is bigger, 0 if equal
   *
   */
  public int compareTo(@Nullable AbstractType inOther)
  {
    if(inOther == null)
      return -1;

    return m_name.compareTo(inOther.m_name);
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
    if(inOther == null)
      return false;

    if(inOther instanceof AbstractType)
      return m_name.equals(((AbstractType)inOther).m_name);
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
    return m_name.hashCode();
  }

  //........................................................................
  //------------------------------- toString -------------------------------

  /**
   * Convert to human readable representation.
   *
   * @return      the converted String
   *
   */
  public @Nonnull String toString()
  {
    return m_name;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //........................................................................

  //------------------------------------------------- other member functions

  //-------------------------------- create --------------------------------

  /**
   * Create a entry of the type.
   *
   * @return      an empty, undefined entry of the type.
   *
   */
  @SuppressWarnings("unchecked") // need to cast
  public @Nullable T create()
  {
    try
    {
      // create the object
      return (T)m_class.newInstance();
    }
    catch(java.lang.InstantiationException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e);
    }
    catch(java.lang.IllegalAccessException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e);
    }

    return null;
  }

  //......................................................................
  //-------------------------------- create --------------------------------

  /**
   * Create a entry of the type.
   *
   * @param       inID the id of the entry to create
   *
   * @return      an empty, undefined entry of the type.
   *
   */
  @SuppressWarnings("unchecked") // need to cast
  public @Nullable T create(@Nonnull String inID)
  {
    try
    {
      // create the object
      return (T)m_class.getConstructor(String.class).newInstance(inID);
    }
    catch(java.lang.NoSuchMethodException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e);
    }
    catch(java.lang.reflect.InvocationTargetException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e);
    }
    catch(java.lang.InstantiationException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e);
    }
    catch(java.lang.IllegalAccessException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e);
    }

    return null;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** A type for testing. */
    public static class TestType<T> extends AbstractType<T>
    {
      /**
       * Create the type.
       *
       * @param inClass the class of the type
       */
      public TestType(@Nonnull Class<T> inClass)
      {
        super(inClass);
      }

      /**
       * Create the type.
       *
       * @param inClass the class of the type
       * @param inMultiple the text for multiple types
       */
      public TestType(@Nonnull Class<T> inClass, String inMultiple)
      {
        super(inClass, inMultiple);
      }

      /** The id for serialization. */
      private static final long serialVersionUID = 1L;
    }

    //----- create ---------------------------------------------------------

    /** The create Test. */
    @org.junit.Test
    public void create()
    {
      TestType<String> type = new TestType<String>(String.class);

      assertEquals("link", "/entry/string/", type.getLink());
      assertEquals("class name", "String", type.getClassName());
      assertEquals("name", "string", type.getName());
      assertEquals("multiple", "Strings", type.getMultiple());
      assertEquals("multiple link", "strings", type.getMultipleLink());
      assertEquals("multiple dir", "Strings", type.getMultipleDir());
      assertEquals("string", "string", type.toString());

      String string = type.create();
      assertEquals("create", "", string);

      string = type.create("guru");
      assertEquals("create", "guru", string);

      TestType<ValueGroup> type2 =
        new TestType<ValueGroup>(ValueGroup.class, "Many More");

      assertEquals("link", "/entry/valuegroup/", type2.getLink());
      assertEquals("class name", "ValueGroup", type2.getClassName());
      assertEquals("name", "value group", type2.getName());
      assertEquals("multiple", "Many More", type2.getMultiple());
      assertEquals("multiple link", "manymore", type2.getMultipleLink());
      assertEquals("multiple dir", "ManyMore", type2.getMultipleDir());
      assertEquals("string", "value group", type2.toString());
    }

    //......................................................................
    //----- compare --------------------------------------------------------

    /** The compare Test. */
    @org.junit.Test
    public void compare()
    {
      TestType<String> type1 = new TestType<String>(String.class);
      TestType<Number> type2 = new TestType<Number>(Number.class);

      assertEquals("compare", 0, type1.compareTo(type1));
      assertTrue("compare", type1.compareTo(null) < 0);
      assertTrue("compare", type1.compareTo(type2) > 0);
      assertTrue("compare", type2.compareTo(type1) < 0);

      assertEquals("compare", 0, type2.compareTo(type2));
      assertTrue("compare", type2.compareTo(null) < 0);
    }

    //......................................................................
  }

  //........................................................................
}
