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

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.server.servlets.BaseServlet;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base servlet for DMA specific servlets.
 *
 *
 * @file          DMAServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public abstract class DMAServlet extends BaseServlet
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ DMAServlet ------------------------------

  /**
   * Cerate the dma servlet.
   *
   */
  public DMAServlet()
  {
    // nothing to do here
  }

  //........................................................................

  //------------------------------ withAccess ------------------------------

  /**
   * Set the access level for this servlet.
   *
   * @param       inGroup the group required for accessing the servlet
   *
   * @return      this servlet for chaining
   *
   */
  public @Nonnull DMAServlet withAccess(@Nonnull BaseCharacter.Group inGroup)
  {
    m_group = inGroup;

    return this;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The group required for accessing the content of this servlet. */
  private @Nullable BaseCharacter.Group m_group;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- allows --------------------------------

  /**
   * Check for access to the page.
   *
   * @param       inRequest the request to the page
   *
   * @return      true for access, false for not
   *
   */
  protected boolean allows(@Nonnull DMARequest inRequest)
  {
    // no access restriction defined
    if(m_group == null)
      return true;

    // normal user access
    return inRequest.hasUser() && inRequest.getUser().hasAccess(m_group);
  }

  //........................................................................

  //------------------------------ extractKey ------------------------------

  /**
   * Extract the key to an entry from the given path. A path is assumed to have
   * the form (/<type>/<id>/)+, e.g. a list of types and ids, where the
   * rightmost is the id and type of the final entry and the earlier pairs are
   * its parents.
   *
   * @param    inPath a path denoting an entry
   *
   * @return   the entry key for the path, if any
   *
   */
  public static @Nullable AbstractEntry.EntryKey<? extends AbstractEntry>
    extractKey(@Nonnull String inPath)
  {
    String []paths = inPath.split("/");
    if(paths == null || paths.length == 0)
      return null;

    return extractKey(paths, paths.length - 1);
  }

  //........................................................................
  //------------------------------ extractKey ------------------------------

  /**
   * Extract the key from the given paths array nd index.
   *
   * @param    inPaths the paths pieces
   * @param    inIndex the index to start from with computation (descaending)
   *
   * @return   the key for the path part or null if not found
   *
   */
  @SuppressWarnings("unchecked") // creating wildard type
  private static @Nullable AbstractEntry.EntryKey<? extends AbstractEntry>
    extractKey(@Nonnull String []inPaths, int inIndex)
  {
    if(inPaths.length <= inIndex || inIndex < 1)
      return null;

    String id = inPaths[inIndex--].replace("%20", " ");
    AbstractType<? extends AbstractEntry> type =
      AbstractType.getTyped(inPaths[inIndex].replace("%20", " "));

    if(type == null)
      return null;

    AbstractEntry.EntryKey<? extends AbstractEntry> parent =
      extractKey(inPaths, inIndex - 1);

    if(parent == null)
      return new AbstractEntry.EntryKey(id, type);

    return new AbstractEntry.EntryKey(id, type, parent);
  }

  //........................................................................
  //------------------------------- getEntry -------------------------------

  /**
   * Get the abstract entry associated with the given request.
   *
   * @param       inPath    the path to the page
   *
   * @return      the entry or null if it could not be found
   *
   */
  public @Nullable AbstractEntry getEntry(@Nonnull String inPath)
  {
    AbstractEntry.EntryKey<? extends AbstractEntry> key = extractKey(inPath);
    if(key == null)
      return null;

    return DMADataFactory.get().getEntry(key);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- handle --------------------------------

  /**
   * Handle the request if it is allowed.
   *
   * @param       inRequest  the original request
   * @param       inResponse the original response
   *
   * @return      an error if something went wrong
   *
   * @throws      ServletException general error when processing the page
   * @throws      IOException      writing to the page failed
   *
   */
  @Override
  protected @Nullable SpecialResult handle
    (@Nonnull HttpServletRequest inRequest,
     @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    if(inRequest instanceof DMARequest)
    {
      DMARequest request = (DMARequest)inRequest;

      if(allows(request))
        return handle(request, inResponse);

      Log.error("No access to page");
      return new HTMLError(HttpServletResponse.SC_FORBIDDEN,
                           "Access Denied",
                           "You don't have access to the requested page.");
    }
    else
    {
      Log.error("Invalid request, expected a DMA request!");
      return new TextError(HttpServletResponse.SC_BAD_REQUEST,
                           "Invalid request, expected a DMA request");
    }

  }

  //........................................................................
  //-------------------------------- handle --------------------------------

  /**
   * Handle the request if it is allowed.
   *
   * @param       inRequest  the original request
   * @param       inResponse the original response
   *
   * @return      an error if something went wrong
   *
   * @throws      ServletException general error when processing the page
   * @throws      IOException      writing to the page failed
   *
   * @undefined   never
   *
   */
  protected abstract @Nullable SpecialResult handle
    (@Nonnull DMARequest inRequest,
     @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException;

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- get ------------------------------------------------------------

    /**
     * The get Test.
     * @throws Exception too lazy to catch
     */
    @org.junit.Test
    public void get() throws Exception
    {
      HttpServletRequest request =
        EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);

      EasyMock.expect(request.getMethod()).andReturn("method");
      EasyMock.expect(request.getRequestURI()).andReturn("uri");
      EasyMock.replay(request, response);

      final java.util.concurrent.atomic.AtomicBoolean handled =
        new java.util.concurrent.atomic.AtomicBoolean(false);
      DMAServlet servlet = new DMAServlet() {
          private static final long serialVersionUID = 1L;
          @Override
          protected SpecialResult handle
            (@Nonnull DMARequest inRequest,
             @Nonnull HttpServletResponse inResponse)
          {
            handled.set(true);
            return null;
          }
        };

      servlet.doGet(request, response);
      assertTrue("handled", handled.get());

      EasyMock.verify(request, response);
    }

    //......................................................................
    //----- extractKey -----------------------------------------------------

    /** The extractKey Test. */
    @org.junit.Test
    public void extractKey()
    {
      DMAServlet servlet = new DMAServlet() {
          private static final long serialVersionUID = 1L;

          @Override
          protected @Nullable SpecialResult handle
            (@Nonnull DMARequest inRequest,
             @Nonnull HttpServletResponse inResponse)
          {
            return null;
          }
        };

      String [] tests =
      {
        "empty", "", null,
        "empty", "/", null,
        "invalid", "/guru", null,
        "invalid triling", "/guru/", null,
        "simple", "/base product/guru", "/base product/guru",
        "simple invalid", "guru/id", null,
        "parent", "/base product/Merlin/product/XYZ",
        "/base product/Merlin/product/XYZ",
        "double parent", "/base product/FR/product/cotsq/product/Longsword",
        "/base product/FR/product/cotsq/product/Longsword",
        "leading", "/_entry/base product/guru", "/base product/guru",
        "multi leading", "/a/b/c/d/base product/guru", "/base product/guru",
      };

      for(int i = 0; i < tests.length; i += 3)
      {
        AbstractEntry.EntryKey key = servlet.extractKey(tests[i + 1]);
        assertEquals(tests[i], tests[i + 2],
                     key == null ? null : key.toString());
      }
    }

    //......................................................................

 }

  //........................................................................
}
