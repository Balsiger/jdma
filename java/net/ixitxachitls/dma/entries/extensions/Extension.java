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

package net.ixitxachitls.dma.entries.extensions;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.entries.indexes.Index;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the basic extension for all the entries.
 *
 * @file          Extension.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @param         <T> the type of entry this is attached to
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public abstract class Extension<T extends Entry<?>> extends AbstractExtension<T>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ Attachment ------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor.
   *
   * @param       inEntry  the entry attached to
   * @param       inName   the name of the attachment
   *
   */
  protected Extension(T inEntry, String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //------------------------------ Attachment ------------------------------

  /**
   * Constructor with all the values.
   *
   * @param       inEntry  the entry attached to
   * @param       inTag    the tag for this attachment
   * @param       inName   the name for this attachment
   *
   */
  // public Attachment(T inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables
  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------- computeIndexValues ---------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @param       ioValues a multi map of values per index name
   *
   */
  @Override
  public void computeIndexValues(Multimap<Index.Path, String> ioValues)
  {
    // nothing to do here
  }

  //........................................................................
  //------------------------------- getBases -------------------------------

  /**
   * Get the base attachment to the current attachment, if any.
   *
   * @param       <S> the type of base attachment to get
   * @param       inClass the class for S, only because we can't use S directly
   *
   * @return      the base attachment to this one, or null if none found
   *
   */
  // @Deprecated
  // @SuppressWarnings("unchecked")
  // protected <S extends BaseAttachment> List<S> getBases(Class<S> inClass)
  // {
  //   List<S> result = new ArrayList<S>();

  //   // can only do something if we have an entry set
  //   if(m_entry == null)
  //     return result;

  //   for(AbstractAttachment attachment : m_entry.getBaseAttachments())
  //     if(inClass.isAssignableFrom(attachment.getClass()))
  //       result.add((S)attachment);

  //   return result;
  // }

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
    // nothing to do here
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // no tests because no values are defined here

  //........................................................................
}
