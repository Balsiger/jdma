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

package net.ixitxachitls.dma.server.servlets;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.utils.SystemProperty;
import com.google.common.base.Optional;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.data.DMADatastore;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.values.enums.Group;
import net.ixitxachitls.server.servlets.BaseServlet;
import net.ixitxachitls.util.Tracer;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

/**
 * The base servlet for DMA specific servlets.
 *
 * @file          DMAServlet.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@Immutable
public abstract class DMAServlet extends BaseServlet
{
  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Create the dma servlet.
   */
  protected DMAServlet()
  {
    // nothing to do here
  }

  /**
   * Set the access level for this servlet.
   *
   * @param       inGroup the group required for accessing the servlet
   *
   * @return      this servlet for chaining
   */
  public DMAServlet withAccess(Group inGroup)
  {
    m_group = Optional.of(inGroup);

    return this;
  }

  /** The group required for accessing the content of this servlet. */
  private Optional<Group> m_group = Optional.absent();

  /**
   * Check for access to the page.
   *
   * @param       inRequest the request to the page
   *
   * @return      true for access, false for not
   */
  protected boolean allows(DMARequest inRequest)
  {
    // no access restriction defined
    if(!m_group.isPresent())
      return true;

    // normal user access
    return inRequest.hasUser()
        && inRequest.getUser().get().hasAccess(m_group.get());
  }

  /**
   * Checks whether we are running on dev or not.
   *
   * @return      true if running on a dev system
   */
  public static boolean isDev()
  {
    return SystemProperty.environment.value()
      == SystemProperty.Environment.Value.Development;
  }

  public static boolean isTesting()
  {
    return Config.get("web.data.testing", false);
  }

  /**
   * Extract the key to an entry from the given path. A path is assumed to have
   * the form (/<type>/<id>/)+, e.g. a list of types and ids, where the
   * rightmost is the id and type of the final entry and the earlier pairs are
   * its parents.
   *
   * @param    inPath a path denoting an entry
   *
   * @return   the entry key for the path, if any
\   */
  public static
  Optional<EntryKey> extractKey(String inPath)
  {
    return EntryKey.fromString(inPath);
  }

  /**
   * Get the abstract entry associated with the given request.
   *
   * @param       inRequest the request to get the entry for
   *
   * @return      the entry or null if it could not be found
   *
   */
  public Optional<AbstractEntry> getEntry(DMARequest inRequest)
  {
    Tracer tracer = new Tracer("getting entry for request");
    Optional<AbstractEntry> entry =
        getEntry(inRequest, inRequest.getRequestURI());

    tracer.done();
    return entry;
  }

  /**
   * Get the abstract entry associated with the given path.
   *
   * @param       inRequest the request to get the entry for
   * @param       inPath    the path to the entry
   *
   * @return      the entry or null if it could not be found
   */
  public Optional<AbstractEntry> getEntry(DMARequest inRequest, String inPath)
  {
    String path = inPath.replaceAll("\\.[^\\./\\\\]*$", "");
    Optional<EntryKey> key = extractKey(path);
    if(!key.isPresent())
      return Optional.absent();

    return inRequest.getEntry(key.get());
  }

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
  protected Optional<? extends SpecialResult>
  handle(HttpServletRequest inRequest, HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    if(inRequest instanceof DMARequest)
    {
      DMARequest request = (DMARequest)inRequest;

      DMADatastore.clearCache();
      if(allows(request))
        return handle(request, inResponse);

      Log.error("No access to page");
      return Optional.of(new HTMLError(HttpServletResponse.SC_FORBIDDEN,
                                       "Access Denied",
                                       "You don't have access to the "
                                       + "requested page."));
    }
    else
    {
      Log.error("Invalid request, expected a DMA request!");
      return Optional.of(new TextError(HttpServletResponse.SC_BAD_REQUEST,
                                       "Invalid request, expected a DMA "
                                       + "request"));
    }

  }

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
  protected abstract Optional<? extends SpecialResult>
  handle(DMARequest inRequest, HttpServletResponse inResponse)
    throws ServletException, IOException;

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /**
     * The get Test.
     * @throws Exception too lazy to catch
     */
    @org.junit.Test
    public void get() throws Exception  // $codepro.audit.disable
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
          /** Serial version id. */
          private static final long serialVersionUID = 1L;
          @Override
          protected Optional<? extends SpecialResult>
          handle(DMARequest inRequest, HttpServletResponse inResponse)
          {
            handled.set(true);
            return Optional.absent();
          }
        };

      servlet.doGet(request, response);
      assertTrue("handled", handled.get());

      EasyMock.verify(request, response);
    }

    /** The extractKey Test. */
    @org.junit.Test
    public void extractKey()
    {
      String [] tests =
      {
        "empty", "", null,
        "empty", "/", null,
        "invalid", "/guru", null,
        "invalid triling", "/guru/", null,
        "simple", "/base product/guru", "/base product/guru",
        "simple invalid", "guru/id", null,
        "parent", "/base product/Merlin/product/XYZ",
        "/base product/merlin/product/xyz",
        "double parent", "/base product/FR/product/cotsq/product/Longsword",
        "/base product/fr/product/cotsq/product/longsword",
        "leading", "/_entry/base product/guru", "/base product/guru",
        "multi leading", "/a/b/c/d/base product/guru", "/base product/guru",
      };

      for(int i = 0; i < tests.length; i += 3)
      {
        Optional<EntryKey> key = DMAServlet.extractKey(tests[i + 1]);
        assertEquals(tests[i], tests[i + 2],
                     key.isPresent() ? key.get().toString() : null);
      }
    }
 }
}
