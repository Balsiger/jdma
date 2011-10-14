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

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableSet;

import net.ixitxachitls.dma.data.DMAData;
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
public abstract class AbstractType<T extends AbstractEntry>
  implements Comparable<AbstractType<? extends AbstractEntry>>,
  java.io.Serializable
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

    String []parts = m_name.split("\\s+");
    m_link = parts[parts.length - 1].toLowerCase(Locale.US);
    m_multipleLink = m_link + "s";

    s_types.put(getName(), this);
    s_types.put(getLink(), this);
    s_all.add(this);
  }

  //........................................................................

  //------------------------------- withLink -------------------------------

  /**
   * Set the link to use for this type.
   *
   * @param       inLink         the name of the link to use
   * @param       inMultipleLink the name to link to multiple entries
   *
   * @return      the type for chaining
   *
   */
  public @Nonnull AbstractType<T> withLink(@Nonnull String inLink,
                                           @Nonnull String inMultipleLink)
  {
    m_link = inLink;
    m_multipleLink = inMultipleLink;

    return this;
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

  /** The link to use to reference an entry of this type. */
  private @Nonnull String m_link;

  /** The link to use to reference multiple entries of this type. */
  private @Nonnull String m_multipleLink;

  /** All the available types. */
  private static final Map<String, AbstractType<? extends AbstractEntry>>
    s_types = new ConcurrentHashMap<String, AbstractType<?>>();

  /** All the available types. */
  private static final Set<AbstractType<? extends AbstractEntry>> s_all
    = Collections.synchronizedSet(new HashSet<AbstractType<?>>());

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
    return m_link;
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
    return m_multipleLink;
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
  //--------------------------------- get ----------------------------------

  /**
   * Get the type for the given name.
   *
   * @param     inName the name of the type to get
   *
   * @return    the type found, if any
   *
   */
  public static @Nullable AbstractType<? extends AbstractEntry>
    get(@Nonnull String inName)
  {
    return s_types.get(inName);
  }

  //........................................................................
  //-------------------------------- getAll --------------------------------

  /**
   * Get the type for the given name.
   *
   * @return    the type found, if any
   *
   */
  public static @Nullable Set<AbstractType<? extends AbstractEntry>>
    getAll()
  {
    return ImmutableSet.copyOf(s_all);
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
  public int compareTo(@Nullable AbstractType<? extends AbstractEntry> inOther)
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
   * @param       inData all the available data
   *
   * @return      an empty, undefined entry of the type.
   *
   */
  @SuppressWarnings("unchecked") // need to cast
  public @Nullable T create(@Nonnull DMAData inData)
  {
    try
    {
      return (T)m_class.getDeclaredConstructor(DMAData.class)
        .newInstance(inData);
    }
    catch(java.lang.InstantiationException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e + " / " + e.getCause());
    }
    catch(java.lang.IllegalAccessException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e + " / " + e.getCause());
    }
    catch(java.lang.NoSuchMethodException e)
    {
      Log.error("cannot find data constructor for entry of type " + m_name
                + " [" + m_class + "]: " + e + " / " + e.getCause());

      return null;
    }
    catch(java.lang.reflect.InvocationTargetException e)
    {
      Log.error("cannot invoke data constructor for entry of type " + m_name
                + " [" + m_class + "]: " + e  + " / " + e.getCause());

      return null;
    }

    return null;
  }

  //......................................................................
  //-------------------------------- create --------------------------------

  /**
   * Create a entry of the type.
   *
   * @param       inID   the id of the entry to create
   * @param       inData all the available data
   *
   * @return      an empty, undefined entry of the type.
   *
   */
  @SuppressWarnings("unchecked") // need to cast
  public @Nullable T create(@Nonnull String inID, @Nonnull DMAData inData)
  {
    try
    {
      // create the object
      return (T)m_class.getConstructor(String.class, DMAData.class)
        .newInstance(inID, inData);
    }
    catch(java.lang.NoSuchMethodException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e + " / " + e.getCause());
    }
    catch(java.lang.reflect.InvocationTargetException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e + " / " + e.getCause());
    }
    catch(java.lang.InstantiationException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e + " / " + e.getCause());
    }
    catch(java.lang.IllegalAccessException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e + " / " + e.getCause());
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
    public static class TestType<T extends AbstractEntry>
      extends AbstractType<T>
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
      AbstractType<BaseEntry> type = new TestType<BaseEntry>(BaseEntry.class);

      assertEquals("link", "entry", type.getLink());
      assertEquals("class name", "BaseEntry", type.getClassName());
      assertEquals("name", "base entry", type.getName());
      assertEquals("multiple", "Base Entrys", type.getMultiple());
      assertEquals("multiple link", "entrys", type.getMultipleLink());
      assertEquals("multiple dir", "BaseEntrys", type.getMultipleDir());
      assertEquals("string", "base entry", type.toString());

      BaseEntry entry = type.create(new DMAData("path"));
      assertEquals("create", "base entry $undefined$ =\n\n.\n",
                   entry.toString());

      entry = type.create("guru", new DMAData("path"));
      assertEquals("create",
                   "#----- guru\n"
                   + "\n"
                   + "base entry guru =\n"
                   + "\n"
                   + ".\n"
                   + "\n"
                   + "#.....\n",
                   entry.toString());

      AbstractType<AbstractEntry> type2 =
        new TestType<AbstractEntry>(AbstractEntry.class, "Many More")
        .withLink("baseentry-link", "baseentry-links");

      assertEquals("link", "baseentry-link", type2.getLink());
      assertEquals("class name", "AbstractEntry", type2.getClassName());
      assertEquals("name", "abstract entry", type2.getName());
      assertEquals("multiple", "Many More", type2.getMultiple());
      assertEquals("multiple link", "baseentry-links", type2.getMultipleLink());
      assertEquals("multiple dir", "ManyMore", type2.getMultipleDir());
      assertEquals("string", "abstract entry", type2.toString());
    }

    //......................................................................
    //----- compare --------------------------------------------------------

    /** The compare Test. */
    @org.junit.Test
    public void compare()
    {
      TestType<AbstractEntry> type1 =
        new TestType<AbstractEntry>(AbstractEntry.class);
      TestType<BaseEntry> type2 = new TestType<BaseEntry>(BaseEntry.class);

      assertEquals("compare", 0, type1.compareTo(type1));
      assertTrue("compare", type1.compareTo(null) < 0);
      assertTrue("compare", type1.compareTo(type2) < 0);
      assertTrue("compare", type2.compareTo(type1) > 0);

      assertEquals("compare", 0, type2.compareTo(type2));
      assertTrue("compare", type2.compareTo(null) < 0);
    }

    //......................................................................
  }

  //........................................................................
}
