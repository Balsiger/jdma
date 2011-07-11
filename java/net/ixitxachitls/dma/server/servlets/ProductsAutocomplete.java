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

package net.ixitxachitls.dma.server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ixitxachitls.dma.entries.BaseProduct;
import net.ixitxachitls.output.html.JsonWriter;
import net.ixitxachitls.util.Strings;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The servlet for person autocomplete data.
 *
 *
 * @file          PersonAutocomplete.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class ProductsAutocomplete extends Autocomplete
{
  //--------------------------------------------------------- constructor(s)

  //------------------------ ProductsAutocomplete --------------------------

  /**
   * Create the autocomplete servlet.
   *
   * @param  inProducts all the base products
   *
   */
  public ProductsAutocomplete(@Nonnull Map<String, BaseProduct> inProducts)
  {
    super(inProducts);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------ writeJson -------------------------------

  /**
   * Write the json output to the given writer.
   *
   * @param       inRequest  the original request
   * @param       inPath   the path requested
   * @param       inWriter the writer to write to
   *
   */
  public void writeJson(@Nonnull DMARequest inRequest, @Nonnull String inPath,
                        @Nonnull JsonWriter inWriter)
  {
    String system = Strings.getPattern(inPath, "products/([^/]*)(/|$)");
    if(system != null)
      system = system.replaceAll("%20", " ");

    String term = inRequest.getParam("term");
    if(term != null)
      term = term.toLowerCase();

    SortedSet<String> products = new TreeSet<String>();
    for(BaseProduct product : m_products.values())
      if((system == null ||
          (product.getSystem() != null &&
           product.getSystem().getName().equalsIgnoreCase(system)))
         && (term == null
             || product.getFullTitle().toLowerCase().startsWith(term)))
        products.add(product.getFullTitle() + " (" + product.getName() + ")");

    inWriter.strings(products);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
