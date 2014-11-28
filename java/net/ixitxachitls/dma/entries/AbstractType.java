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

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import net.ixitxachitls.util.Classes;
import net.ixitxachitls.util.logging.Log;

/**
 * The type abstract base of the entry types.
 *
 * @file          AbstractType.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 * @param         <T> the type represented by this type spec
 */
@Immutable
public class AbstractType<T extends AbstractEntry>
  implements Comparable<AbstractType<? extends AbstractEntry>>,
  java.io.Serializable
{
  /**
   * A builder for an abstract type.
   *
   * @param <T> the entries for the type
   * @param <B> the builder for the type (for extension)
   */
  public static class Builder<T extends AbstractEntry, B extends Builder<T, B>>
  {
    /** The class for the type of entries. */
    protected final Class<T> m_class;

    /** The string to use for multiple values of this type. */
    protected Optional<String> m_multiple = Optional.absent();

    /** The string to use to link to this type. */
    protected Optional<String> m_link = Optional.absent();

    /** The string to use to link to multiple of this type. */
    protected Optional<String> m_multipleLink = Optional.absent();

    /** The name of the field to use for sortig this type. */
    protected Optional<String> m_sort = Optional.absent();

    /**
     * Create the builder.
     *
     * @param inClass the class for entries of this type
     */
    public Builder(Class<T> inClass)
    {
      m_class = inClass;
    }

    /**
     * Build the type.
     *
     * @return the type built.
     */
    public AbstractType<T> build()
    {
      return new AbstractType(m_class, m_multiple, m_link, m_multipleLink,
                              m_sort);
    }

    /** Set the string to use for the multiple name of the type.
     *
     * @param inMultiple the name to use for multiple entries
     * @return the builder, for chaining
     */
    public B multiple(String inMultiple)
    {
      m_multiple = Optional.of(inMultiple);
      return (B)this;
    }

    /**
     * Set how to link to this type.
     *
     * @param inLink          the name to use to link
     * @param inMultipleLink  the name to use to link to multiple
     * @return the builder, for chaining
     */
    public B link(String inLink, String inMultipleLink)
    {
      m_link = Optional.of(inLink);
      m_multipleLink = Optional.of(inMultipleLink);
      return (B)this;
    }

    /**
     * Set the sort field of the type.
     * @param inSort the field to sort by
     * @return the builder, for chaining
     */
    public B sort(String inSort)
    {
      m_sort = Optional.of(inSort);
      return (B)this;
    }
  }

  /**
   * Create the type.
   * @param inClass the class of entries represented by the type
   * @param inMultiple the name to use for multiple entries of the type
   * @param inLink the name to use to link to an entry
   * @param inMultipleLink the name to use to link to multiple entries
   * @param inSort the name of the field to use for sorting entries
   */
  protected AbstractType(Class<T> inClass, Optional<String> inMultiple,
                         Optional<String> inLink,
                         Optional<String> inMultipleLink,
                         Optional<String> inSort)
  {
    m_name = Classes.fromClassName(inClass).toLowerCase(Locale.US);
    m_class = inClass;

    if(inMultiple.isPresent())
      m_multiple = inMultiple.get();
    else
      m_multiple = Classes.fromClassName(m_class) + "s";

    m_className = inClass.getName().replaceAll(".*\\.", "");

    if(inLink.isPresent())
      m_link = inLink.get();
    else
    {
      String[] parts = m_name.split("\\s+");
      m_link = parts[parts.length - 1].toLowerCase(Locale.US);
    }

    if(inMultipleLink.isPresent())
      m_multipleLink = inMultipleLink.get();
    else
      m_multipleLink = m_link + "s";

    m_sort = inSort;

    s_types.put(getName(), this);
    s_types.put(getLink(), this);
    s_types.put(getMultipleLink(), this);
    s_types.put(getMultiple(), this);
    s_types.put(getName().replace(" ", ""), this);
    s_types.put(getMultiple().replace(" ", ""), this);

    s_typedTypes.put(getName(), this);

    s_linkedTypes.put(getLink(), this);
    s_linkedTypes.put(getMultipleLink(), this);

    s_all.add(this);
  }

  /**
   * Set the link to use for this type.
   *
   * @param       inLink         the name of the link to use
   * @param       inMultipleLink the name to link to multiple entries
   *
   * @return      the type for chaining
   */
  public AbstractType<T> withLink(String inLink, String inMultipleLink)
  {
    m_link = inLink;
    m_multipleLink = inMultipleLink;

    s_types.put(getLink(), this);
    s_types.put(getMultipleLink(), this);
    return this;
  }

  /**
   * Set the sort field to use for this type.
   *
   * @param       inSort  the field used to sort
   *
   * @return      the type for chaining
   */
  public AbstractType<T> withSort(String inSort)
  {
    m_sort = Optional.of(inSort);

    return this;
  }

  /** the name of the type. */
  private final String m_name;

  /** the name for multiple instances of the type. */
  private final String m_multiple;

  /** the class for the type. */
  private final Class<T> m_class;

  /** The class name without package. */
  private final String m_className;

  /** The link to use to reference an entry of this type. */
  private String m_link;

  /** The link to use to reference multiple entries of this type. */
  private String m_multipleLink;

  /** The field to be used for sorting. */
  private Optional<String> m_sort = Optional.absent();

  /** All the available types. */
  private static final Map<String, AbstractType<? extends AbstractEntry>>
    s_types = new ConcurrentHashMap<String, AbstractType<?>>();

  /** All the availalbe types per full type spec. */
  private static final Map<String, AbstractType<? extends AbstractEntry>>
    s_typedTypes = new ConcurrentHashMap<String, AbstractType<?>>();

  /** All the availalbe types per link. */
  private static final Map<String, AbstractType<? extends AbstractEntry>>
    s_linkedTypes = new ConcurrentHashMap<String, AbstractType<?>>();

  /** All the available types. */
  public static final Set<AbstractType<? extends AbstractEntry>> s_all
    = Collections.synchronizedSet(new HashSet<AbstractType<?>>());

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Get the name to link to such a type.
   *
   * @return      the link
   */
  public String getLink()
  {
    return m_link;
  }

  /**
   * Get the name for this type.
   *
   * @return      the class name
   */
  public String getName()
  {
    return m_name;
  }

  /**
   * Get the class name (without package) for this type.
   *
   * @return      the class name
   */
  public String getClassName()
  {
    return m_className;
  }

  /**
   * Get the name for multiples of this type.
   *
   * @return      the multiple name
   */
  public String getMultiple()
  {
    return m_multiple;
  }

  /**
   * Get the name for multiples of this type.
   *
   * @return      the multiple name
   */
  public String getMultipleLink()
  {
    return m_multipleLink;
  }

  /**
   * Get the name for multiples of this type.
   *
   * @return      the multiple name as directory reference
   */
  public String getMultipleDir()
  {
    return m_multiple.replaceAll(" ", "");
  }

  /**
   * Get the type for the given name.
   *
   * @param     inName the name of the type to get
   *
   * @return    the type found, if any
   */
  public static Optional<? extends AbstractType<? extends AbstractEntry>>
    getTyped(String inName)
  {
    return Optional.fromNullable(s_typedTypes.get(inName));
  }

  /**
   * Get the base type to this one.
   *
   * @return      the requested base type or this if already a base type
   */
  public AbstractType<? extends AbstractEntry> getBaseType()
  {
    return this;
  }

  /**
   * Get the type for the given name.
   *
   * @return    the type found, if any
   */
  public static Set<AbstractType<? extends AbstractEntry>> getAll()
  {
    return ImmutableSet.copyOf(s_all);
  }

  /**
   * Compare this type to another one for sorting.
   *
   * @param       inOther the other type to compare to
   *
   * @return      < 0 if this is lower, > if this is bigger, 0 if equal
   */
  @Override
  public int compareTo(AbstractType<? extends AbstractEntry> inOther)
  {
    return m_name.compareTo(inOther.m_name);
  }

  /**
   * Check for equality of the given errors.
   *
   * @param       inOther the object to compare to
   *
   * @return      true if equal, false else
   */
  @Override
  public boolean equals(Object inOther)
  {
    if(this == inOther)
      return true;

    if(inOther == null)
      return false;

    if(inOther instanceof AbstractType)
      return m_name.equals(((AbstractType)inOther).m_name);
    else
      return false;
  }

  /**
   * Compute the hash code for this class.
   *
   * @return      the hash code
   */
  @Override
  public int hashCode()
  {
    return m_name.hashCode();
  }

  /**
   * Convert to human readable representation.
   *
   * @return      the converted String
   */
  @Override
  public String toString()
  {
    return m_name;
  }

  /**
   * Get the name of the field to be used for sorting.
   *
   * @return      the field to use for sorting, if any
   */
  public Optional<String> getSortField()
  {
    return m_sort;
  }

  /**
   * Create a entry of the type.
   *
   * @return      an empty, undefined entry of the type.
   */
  public Optional<T> create()
  {
    try
    {
      return Optional.of(m_class.newInstance());
    }
    catch(java.lang.InstantiationException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e + " / " + e.getCause());
      e.printStackTrace(System.err);
    }
    catch(java.lang.IllegalAccessException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e + " / " + e.getCause());
      e.printStackTrace(System.err);
    }

    return Optional.absent();
  }

  /**
   * Create a entry of the type.
   *
   * @param       inID   the id of the entry to create
   *
   * @return      an empty, undefined entry of the type.
   */
  public Optional<T> create(String inID)
  {
    try
    {
      // create the object
      return Optional.of(m_class.getConstructor(String.class)
                                .newInstance(inID));
    }
    catch(java.lang.NoSuchMethodException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e + " / " + e.getCause());
      e.printStackTrace(System.err);
    }
    catch(java.lang.reflect.InvocationTargetException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e + " / " + e.getCause());
      e.printStackTrace(System.err);
    }
    catch(java.lang.InstantiationException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e + " / " + e.getCause());
      e.printStackTrace(System.err);
    }
    catch(java.lang.IllegalAccessException e)
    {
      Log.error("cannot instantiate entry of type " + m_name + " ["
                + m_class + "]: " + e + " / " + e.getCause());
      e.printStackTrace(System.err);
    }

    return Optional.absent();
  }

  //----------------------------------------------------------------------------

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** The create Test. */
    @org.junit.Test
    public void create()
    {
      AbstractType<BaseEntry> type =
          new AbstractType.Builder<>(BaseEntry.class).build();

      assertEquals("link", "entry", type.getLink());
      assertEquals("class name", "BaseEntry", type.getClassName());
      assertEquals("name", "base entry", type.getName());
      assertEquals("multiple", "Base Entrys", type.getMultiple());
      assertEquals("multiple link", "entrys", type.getMultipleLink());
      assertEquals("multiple dir", "BaseEntrys", type.getMultipleDir());
      assertEquals("string", "base entry", type.toString());

      BaseEntry entry = type.create().get();
      assertEquals("create", "base entry ",
                   entry.toString());

      entry = type.create("guru").get();
      assertEquals("create", "base entry guru", entry.toString());

      AbstractType<AbstractEntry> type2 =
        new AbstractType.Builder<>(AbstractEntry.class)
            .multiple("Many More")
            .link("baseentry-link", "baseentry-links")
            .sort("sort")
          .build();

      assertEquals("link", "baseentry-link", type2.getLink());
      assertEquals("class name", "AbstractEntry", type2.getClassName());
      assertEquals("name", "abstract entry", type2.getName());
      assertEquals("multiple", "Many More", type2.getMultiple());
      assertEquals("multiple link", "baseentry-links", type2.getMultipleLink());
      assertEquals("multiple dir", "ManyMore", type2.getMultipleDir());
      assertEquals("sort", "sort", type2.getSortField().get());
      assertEquals("string", "abstract entry", type2.toString());
    }

    /** The compare Test. */
    @org.junit.Test
    public void compare()
    {
      AbstractType<AbstractEntry> type1 =
          new AbstractType.Builder<>(AbstractEntry.class)
              .build();
      AbstractType<BaseEntry> type2 =
          new AbstractType.Builder<>(BaseEntry.class).build();

      assertEquals("compare", 0, type1.compareTo(type1));
      assertTrue("compare", type1.compareTo(type2) < 0);
      assertTrue("compare", type2.compareTo(type1) > 0);

      assertEquals("compare", 0, type2.compareTo(type2));
    }
  }
}
