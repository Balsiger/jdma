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

package net.ixitxachitls.dma.output.soy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyListData;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.Entries;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.dma.values.Expression;
import net.ixitxachitls.dma.values.FormattedText;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.util.Strings;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A soy data object for combination values taking data from an entry and it's
 * base entries.
 *
 *
 * @file          SoyCombination.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class SoyCombination extends SoyValue
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- SoyCombination ----------------------------

  /**
   * Create the soy combination value.
   *
   * @param    inName        the name of the value
   * @param    inCombination the combination representing the value
   * @param    inValue       the base value
   * @param    inEntry       the entry witht the value
   *
   */
  public SoyCombination(@Nonnull String inName,
                        @Nonnull Combination<? extends Value> inCombination,
                        @Nonnull Value inValue,
                        @Nonnull AbstractEntry inEntry)
  {
    super(inName, inValue, inEntry);

    m_combination = inCombination;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The combination stored here. */
  private final @Nonnull Combination<? extends Value> m_combination;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getSingle -------------------------------

  /**
   * Get a single value out of the combination.
   *
   * @param  inName the name of the value to get
   *
   * @return the value found or null if not found
   */
  @Override
  public @Nullable SoyData getSingle(@Nonnull String inName)
  {
    // try
    // {
    if("total".equals(inName))
    {
      Value total = m_combination.total();
      if (total == null)
        return null;

      return new SoyValue(m_combination.getName(), total, m_entry);
    }

    if("min".equals(inName))
      return new SoyValue(m_name, m_combination.min(), m_entry);

    if("max".equals(inName))
      return new SoyValue(m_name, m_combination.max(), m_entry);

    if("bases".equals(inName))
    {
      List<Map<String, Object>> bases = new ArrayList<Map<String, Object>>();

      for(Map.Entry<? extends Value, Collection<String>> entry
            : m_combination.valuesPerGroup().asMap().entrySet())
        if(entry.getValue().size() != 1
           || !entry.getValue().iterator().next().equals(m_entry.getName()))
          bases.add
            (SoyTemplate.map
             ("value", new SoyValue(m_combination.getName(), entry.getKey(),
                                    m_entry),
              "isLongText", entry.getKey() instanceof FormattedText,
              "isText", entry.getKey() instanceof Text,
              "names", Strings.COMMA_JOINER.join(entry.getValue())));

      for(Map.Entry<Expression, Collection<ValueGroup>> entry
            : m_combination.expressionsPerGroup().asMap().entrySet())
        bases.add
          (SoyTemplate.map
           ("expression", entry.getKey().toString(),
            "names", Entries.namesString(entry.getValue())));

      return new SoyListData(bases);
    }
    // }
    // catch(Exception e)
    // {
    //   System.out.println("eception when getting combination value:");
    //   e.printStackTrace(System.out);
    // }

    return super.getSingle(inName);
  }

  //........................................................................
  //-------------------------------- equals --------------------------------

  /**
   * Checks if the given object is equal to this one.
   *
   * @param    inOther the object to compare against
   *
   * @return   true if the other is equal, false if not
   *
   */
  @Override
  public boolean equals(Object inOther)
  {
    if(!(inOther instanceof SoyCombination))
      return false;

    return m_combination.equals(((SoyCombination)inOther).m_combination)
      && super.equals(inOther);
  }

  //........................................................................
  //------------------------------- hashCode -------------------------------

  /**
   * Compute the hash code of the object.
   *
   * @return      the object's hash code
   *
   */
  @Override
  public int hashCode()
  {
    return super.hashCode() + m_combination.hashCode();
  }

  //........................................................................
  //------------------------------- toString -------------------------------

  /**
   * Convert to a string for debugging.
   *
   * @return      the string represenation
   *
   */
  public @Nonnull String toString()
  {
    return "total " + (getSingle("total") != null ? getSingle("total") : "")
      + ", min " + (getSingle("min") != null ? getSingle("min") : "")
      + ", max " + (getSingle("max") != null ? getSingle("max") : "")
      + ", bases " + (getSingle("bases") != null ? getSingle("bases") : "");
  }

  //........................................................................


  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
