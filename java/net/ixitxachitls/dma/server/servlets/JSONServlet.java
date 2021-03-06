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
import java.io.PrintWriter;

import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Optional;

import org.easymock.EasyMock;

import net.ixitxachitls.output.html.JsonWriter;

/**
 * The base servlet for json requests.
 *
 * @file          JSONServlet.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */
@Immutable
public abstract class JSONServlet extends DMAServlet
{
  /** Create the servlet. */
  protected JSONServlet()
  {
    // nothing to do
  }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Handle the request.
   *
   * @param       inRequest  the original request
   * @param       inResponse the original response
   *
   * @return      a special result if something went wrong
   *
   * @throws      ServletException general error when processing the page
   * @throws      IOException      writing to the page failed
   */
  @Override
  protected Optional<? extends SpecialResult>
  handle(DMARequest inRequest, HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    // Set the output header.
    inResponse.setHeader("Content-Type", "application/json");
    inResponse.setHeader("Cache-Control", "max-age=0");

    String path = inRequest.getRequestURI();
    try (JsonWriter writer =
      new JsonWriter(new PrintWriter(inResponse.getOutputStream())))
    {
      writeJson(inRequest, path, writer);
    }

    return Optional.absent();
  }

  /**
   * Write the json output to the given writer.
   *
   * @param       inRequest  the original request
   * @param       inPath   the path requested
   * @param       inWriter the writer to write to
   *
   */
  protected abstract void writeJson(DMARequest inRequest, String inPath,
                                    JsonWriter inWriter);

  //----------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.server.ServerUtils.Test
  {
    //----- handle ---------------------------------------------------------

    /**
     * The handle Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void handle() throws Exception
    {
      HttpServletRequest request =
        EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      try (MockServletOutputStream output = new MockServletOutputStream())
      {
        EasyMock.expect(request.getMethod()).andReturn("POST");
        EasyMock.expect(request.getRequestURI()).andStubReturn("uri");
        response.setHeader("Content-Type", "application/json");
        response.setHeader("Cache-Control", "max-age=0");
        EasyMock.expect(response.getOutputStream()).andReturn(output);
        EasyMock.replay(request, response);

        JSONServlet servlet = new JSONServlet() {
            /** Serial version id. */
            private static final long serialVersionUID = 1L;
            @Override
            protected void writeJson(DMARequest inRequest, String inPath,
                                     JsonWriter inWriter)
            {
              inWriter.add(inPath);
            }
          };

        servlet.doPost(request, response);
        assertEquals("post", "uri", output.toString());

        EasyMock.verify(request, response);
      }
    }

    //......................................................................
  }
}
