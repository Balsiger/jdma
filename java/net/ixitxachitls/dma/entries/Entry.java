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

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.extensions.AbstractExtension;
import net.ixitxachitls.dma.entries.extensions.Extension;
import net.ixitxachitls.dma.values.Combined;
import net.ixitxachitls.dma.values.ID;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Reference;
import net.ixitxachitls.dma.values.ValueList;

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

  //---------------------------- fullReferences ----------------------------

  /**
   * Get the references of this entry with full information for printing.
   *
   * @return      a list with the references and all values
   *
   */
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> fullReferences()
  {
    List<Map<String, Object>> references = Lists.newArrayList();

    Combined<ValueList<Multiple>> combinedRefs = collect("references");
    for(Multiple ref : combinedRefs.total())
    {
      Map<String, Object> values = Maps.newHashMap();
      Reference<BaseProduct> reference = (Reference<BaseProduct>)ref.get(0);
      BaseProduct product = reference.getEntry();
      Object pages = ref.get(1);

      if(product != null)
        values.put("title", product.getFullTitle());

      values.put("id", reference);
      values.put("pages", pages);
      references.add(values);
    }

    return references;
  }

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
