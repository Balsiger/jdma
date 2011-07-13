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

package net.ixitxachitls.dma.server.servlets;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

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
      if((system == null
          || (product.getSystem() != null
              && product.getSystem().getName().equalsIgnoreCase(system)))
         && (term == null
             || product.getFullTitle().toLowerCase().startsWith(term)))
        products.add(product.getFullTitle() + " (" + product.getName() + ")");

    inWriter.strings(products);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------ tests

  /** The test. */
  public static class Test extends net.ixitxachitls.server.ServerUtils.Test
  {
    //----- emptyHandle ----------------------------------------------------

    /**
     * The handle Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void emptyHandle() throws Exception
    {
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      MockServletOutputStream output = new MockServletOutputStream();

      EasyMock.expect(request.getMethod()).andReturn("POST");
      EasyMock.expect(request.getRequestURI()).andStubReturn("uri");
      EasyMock.expect(request.getParam("term")).andStubReturn(null);
      response.setHeader("Content-Type", "application/json");
      response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(response.getOutputStream()).andReturn(output);
      EasyMock.replay(request, response);

      Map<String, BaseProduct> products =
        new java.util.HashMap<String, BaseProduct>();
      Autocomplete servlet = new ProductsAutocomplete(products);

      servlet.doPost(request, response);
      assertEquals("empty", "[\n]\n", output.toString());

      EasyMock.verify(request, response);
    }

    //......................................................................
    //----- handle ---------------------------------------------------------

    /**
     * The handle Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void handle() throws Exception
    {
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      MockServletOutputStream output = new MockServletOutputStream();

      EasyMock.expect(request.getMethod()).andReturn("POST");
      EasyMock.expect(request.getRequestURI()).andStubReturn("persons/author");
      EasyMock.expect(request.getParam("term")).andStubReturn(null);
      response.setHeader("Content-Type", "application/json");
      response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(response.getOutputStream()).andReturn(output);
      EasyMock.replay(request, response);

      Map<String, BaseProduct> products =
        new java.util.HashMap<String, BaseProduct>();

      BaseProduct product1 =
        new BaseProduct("First", new net.ixitxachitls.dma.data.DMAData("path"));
      BaseProduct product2 =
        new BaseProduct("Second",
                        new net.ixitxachitls.dma.data.DMAData("path"));
      BaseProduct product3 =
        new BaseProduct("Third",
                        new net.ixitxachitls.dma.data.DMAData("path"));

      net.ixitxachitls.dma.entries.Variable title =
        product1.getVariable("title");
      title.setFromString(product1, "\"The first one\"");
      title.setFromString(product2, "\"On to the second\"");
      title.setFromString(product3, "\"Last but not least\"");
      products.put("First", product1);
      products.put("Second", product2);
      products.put("Third", product3);

      Autocomplete servlet = new ProductsAutocomplete(products);

      servlet.doPost(request, response);
      assertEquals("empty",
                   "[\n  \"Last but not least (Third)\",\n  "
                   + "\"On to the second (Second)\",\n"
                   + "  \"The first one (First)\"\n]\n",
                   output.toString());

      EasyMock.verify(request, response);
    }

    //......................................................................
    //----- prefixHandle ---------------------------------------------------

    /**
     * The handle Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void prefixHandle() throws Exception
    {
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      MockServletOutputStream output = new MockServletOutputStream();

      EasyMock.expect(request.getMethod()).andReturn("POST");
      EasyMock.expect(request.getRequestURI()).andStubReturn("persons/author");
      EasyMock.expect(request.getParam("term")).andStubReturn("l");
      response.setHeader("Content-Type", "application/json");
      response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(response.getOutputStream()).andReturn(output);
      EasyMock.replay(request, response);

      Map<String, BaseProduct> products =
        new java.util.HashMap<String, BaseProduct>();

      BaseProduct product1 =
        new BaseProduct("First", new net.ixitxachitls.dma.data.DMAData("path"));
      BaseProduct product2 =
        new BaseProduct("Second",
                        new net.ixitxachitls.dma.data.DMAData("path"));
      BaseProduct product3 =
        new BaseProduct("Third",
                        new net.ixitxachitls.dma.data.DMAData("path"));

      net.ixitxachitls.dma.entries.Variable title =
        product1.getVariable("title");
      title.setFromString(product1, "\"The first one\"");
      title.setFromString(product2, "\"On to the second\"");
      title.setFromString(product3, "\"Last but not least\"");
      products.put("First", product1);
      products.put("Second", product2);
      products.put("Third", product3);

      Autocomplete servlet = new ProductsAutocomplete(products);

      servlet.doPost(request, response);
      assertEquals("empty",
                   "[\n  \"Last but not least (Third)\"\n]\n",
                   output.toString());

      EasyMock.verify(request, response);
    }

    //......................................................................
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
