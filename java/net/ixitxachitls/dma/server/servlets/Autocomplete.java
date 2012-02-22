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

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableList;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.output.html.JsonWriter;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base servlet for autocomplete requests.
 *
 *
 * @file          Autocomplete.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Autocomplete extends JSONServlet
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- Autocomplete -----------------------------

  /**
   * Create the autocomplete servlet.
   *
   */
  public Autocomplete()
  {
    // nothing to do here
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /** The maximal number of results to return. */
  private static final int s_max = 20;

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
  @Override
  @SuppressWarnings("unchecked") // need to cast from cache
  protected void writeJson(@Nonnull DMARequest inRequest,
                           @Nonnull String inPath,
                           @Nonnull JsonWriter inWriter)
  {
    // compute the index involved
    String []parts = inPath.replace("%20", " ").split("/");

    if(parts.length > 3)
    {
      String term = inRequest.getParam("term");
      AbstractType<? extends AbstractEntry> type =
        AbstractType.getTyped(parts[2]);

      if(type != null && parts[3] != null)
      {
        SortedSet<String> names = new TreeSet<String>();
        String []filters = new String[0];
        if(parts.length > 4)
          filters = Arrays.copyOfRange(parts, 4, parts.length);
        for(String name : DMADataFactory.get()
              .getIndexNames(parts[3], type, true, filters))
        {
          if(match(name, term))
            names.add(name);

          if(names.size() > s_max)
            break;
        }

        inWriter.strings(names);
        return;
      }
    }

    inWriter.strings(ImmutableList.of("* Error computing autocomplete *"));
  }

  //........................................................................
  //-------------------------------- match ---------------------------------

  /**
   * Check if the given name matches the given autocomplete string.
   *
   * @param    inName the name to match against
   * @param    inAuto the autocomplete value to match with
   *
   * @return   true if the values match, false if not
   *
   */
  private boolean match(@Nonnull String inName, @Nullable String inAuto)
  {
    if(inAuto == null)
      return true;

    if(inName.regionMatches(true, 0, inAuto, 0, inAuto.length()))
      return true;

    String []nameParts = inName.split(" ");
    String []autoParts = inAuto.split(" ");
    for(int i = 0; i < autoParts.length; i++)
      if(nameParts.length <= i || !nameParts[i].regionMatches
         (true, 0, autoParts[i], 0, autoParts[i].length()))
        return false;

    return true;
  }

  //........................................................................



  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

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
      MockServletOutputStream output = new MockServletOutputStream();

      EasyMock.expect(request.getMethod()).andReturn("POST");
      EasyMock.expect(request.getRequestURI()).andStubReturn("uri");
      response.setHeader("Content-Type", "application/json");
      response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(response.getOutputStream()).andReturn(output);
      EasyMock.replay(request, response);

      Autocomplete servlet = new Autocomplete() {
          private static final long serialVersionUID = 1L;
          @Override
          protected void writeJson(@Nonnull DMARequest inRequest,
                                   @Nonnull String inPath,
                                   @Nonnull JsonWriter inWriter)
          {
            inWriter.add(inPath);
          }
        };

      servlet.doPost(request, response);
      assertEquals("post", "uri", output.toString());

      EasyMock.verify(request, response);
    }

    //......................................................................
  }

  //........................................................................
}
