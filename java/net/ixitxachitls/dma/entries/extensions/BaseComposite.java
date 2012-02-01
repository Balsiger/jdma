/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import javax.annotation.Nonnull;

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.formatters.Formatter;
import net.ixitxachitls.dma.values.formatters.LinkFormatter;
import net.ixitxachitls.dma.values.formatters.ListFormatter;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the wearable extension for all the entries.
 *
 * @file          BaseComposite.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class BaseComposite extends BaseExtension<BaseItem>
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- BaseComposite ----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inName  the name of the extension
   *
   */
  public BaseComposite(@Nonnull BaseItem inEntry, @Nonnull String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //----------------------------- BaseComposite ----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   * @undefined   never
   *
   */
  // public BaseComposite(BaseItem inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The printer for printing the whole base item. */
  public static final Print s_pagePrint =
    new Print("%contains");

  //----- contains ---------------------------------------------------------

  /** The formatter for contained items. */
  protected static final Formatter<Name> s_containsFormatter =
    new LinkFormatter<Name>("/item/");

  /** The formatter for the outer list of contained items. */
  protected static final Formatter<ValueList<ValueList<Name>>> s_andFormatter =
    new ListFormatter<ValueList<ValueList<Name>>>(" and ");

  /** The formatter for the outer list of contained items. */
  protected static final Formatter<ValueList<Name>> s_orFormatter =
    new ListFormatter<ValueList<Name>>(" or ");

  /** The items names that are together with this one. */
  @Key("contains")
  protected ValueList<ValueList<Name>> m_contains =
    new ValueList<ValueList<Name>>
    (new ValueList<Name>(new Name().withFormatter(s_containsFormatter), "|")
     .withFormatter(s_orFormatter))
    .withFormatter(s_andFormatter);

  //........................................................................

  static
  {
    setAutoExtensions(BaseComposite.class, "composite");
    extractVariables(BaseItem.class, BaseComposite.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getPagePrint -----------------------------

  /**
   * Get the print for a full page.
   *
   * @return the print for page printing
   *
   */
  protected @Nonnull Print getPagePrint()
  {
    return s_pagePrint;
  }

  //........................................................................
  //----------------------------- getListPrint -----------------------------

  /**
   * Get the print for a list entry.
   *
   * @return the print for list entry
   *
   */
  protected @Nonnull ListPrint getListPrint()
  {
    return s_listPrint;
  }

  //........................................................................

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
