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
 * GNU General Public License for more details.x1
 *
 * You should have received a copy of the GNU General Public License
 * along with Dungeon Master Assistant; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *****************************************************************************/

//------------------------------------------------------------------ imports

package net.ixitxachitls.dma.entries;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.ParametersAreNonnullByDefault;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.extensions.AbstractExtension;
import net.ixitxachitls.dma.entries.extensions.Extension;
import net.ixitxachitls.dma.values.ID;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base class for all jDMA entries (not base entries!).
 *
 * @file          Entry.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @param         <B> the type of base entry associated with this entry
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public abstract class Entry<B extends BaseEntry> extends AbstractEntry
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Entry ---------------------------------

  /**
   * The complete and 'default' constructor.
   *
   * @param       inName     the name of the entry
   * @param       inType     the type of the entry
   * @param       inBaseType the type of the base entry to this one
   *
   */
  protected Entry(String inName, Type<? extends Entry<?>> inType,
                  BaseType<? extends BaseEntry> inBaseType)
  {
    super(inName, inType);

    m_baseType = inBaseType;
  }

  //........................................................................
  //-------------------------------- Entry ---------------------------------

  /**
   * The default constructor.
   *
   * @param       inType     the type of the entry
   * @param       inBaseType the type of the base entry to this one
   *
   */
  protected Entry(Type<? extends Entry<?>> inType,
                  BaseType<? extends BaseEntry> inBaseType)
  {
    super(inType);

    m_baseType = inBaseType;
  }

  //........................................................................
  //-------------------------------- Entry ---------------------------------

  /**
   * The complete constructor.
   *
   * @param       inName     the name of the entry
   * @param       inType     the type of the entry
   * @param       inBaseType the type of the base entry to this one
   * @param       inBases    the base entries to use
   *
   */
  protected Entry(String inName, Type<? extends Entry<?>> inType,
                  BaseType<? extends BaseEntry> inBaseType,
                  String ... inBases)
  {
    super(inName, inType, inBases);

    m_baseType = inBaseType;
  }

  //........................................................................
  //-------------------------------- Entry ---------------------------------

  /**
   * The complete constructor.
   *
   * @param       inType     the type of the entry
   * @param       inBaseType the type of the base entry to this one
   * @param       inBases    the names of base entries to use
   *
   */
  protected Entry(Type<? extends Entry<?>> inType,
                  BaseType<? extends BaseEntry> inBaseType,
                  String ... inBases)
  {
    super(inType, inBases);

    m_baseType = inBaseType;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of the base entry. */
  protected BaseType<? extends BaseEntry> m_baseType;

  /** The type of the base to this entry. */
  public static final BaseType<BaseEntry> BASE_TYPE =
    BaseEntry.TYPE;

  /** The type of this entry. */
  @SuppressWarnings("rawtypes")
  public static final Type<Entry<?>> TYPE =
    new Type<Entry<?>>(Entry.class, BaseEntry.TYPE);

  /** The name of a temporary entry. */
  public static final String TEMPORARY = "TEMPORARY";

  static
  {
    TYPE.withLink("entry", "entries");
    extractVariables(Entry.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- computeValue -----------------------------

  /**
   * Get a value for printing.
   *
   * @param     inKey  the name of the value to get
   * @param     inDM   true if formattign for dm, false if not
   *
   * @return    a value handle ready for printing
   *
   */
  // @Override
  // public @Nullable ValueHandle computeValue(String inKey, boolean inDM)
  // {
  //   if("categories".equals(inKey))
  //   {
  //     Set<String> categories = new TreeSet<String>();
  //     for(BaseEntry base : getBaseEntries())
  //       if(base != null)
  //         categories.addAll(base.getCategories());

  //     List<Object> commands = new ArrayList<Object>();
  //     for(String category : categories)
  //     {
  //       if(!commands.isEmpty())
  //         commands.add(", ");

  //       commands.add(new Link(category,
  //                             link(getType(),
  //                                  Index.Path.CATEGORIES)
  //                             + category.toLowerCase(Locale.US)));
  //     }

  //     return new FormattedValue
  //       (new Command(commands), Strings.toString(categories, ", ", ""),
  //        "categories");
  //   }

  //   return super.computeValue(inKey, inDM);
  // }

  //........................................................................
  //------------------------------- randomID -------------------------------

  /**
   * Set the id to a random value.
   *
   */
  public void randomID()
  {
    setName(ID.random().get());
    changed(true);
  }

  //........................................................................

  //-------------------------------- isBase --------------------------------

  /**
   * Check if the current entry represents a base entry or not.
   *
   * @return      true if this is a base entry, false else
   *
   */
  @Override
  public boolean isBase()
  {
    return false;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled.
   *
   */
  @OverridingMethodsMustInvokeSuper
  public void complete()
  {
    if(!m_name.isDefined() || m_name.get().isEmpty())
    {
      changed();

      do
      {
        randomID();
      } while(DMADataFactory.get().getEntry(getKey()) != null);
    }

    setupExtensions();
    for(AbstractExtension<?> extension : m_extensions.values())
      if(extension instanceof Extension)
        ((Extension)extension).complete();
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
