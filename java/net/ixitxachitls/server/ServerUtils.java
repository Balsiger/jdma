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

package net.ixitxachitls.server;

import java.io.BufferedReader;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.easymock.EasyMock;

import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A static class with some server utility functions.
 *
 *
 * @file          ServerUtils.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public final class ServerUtils
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- ServerUtils ------------------------------

  /**
   * A private constructor to prevent instantiation.
   *
   */
  private ServerUtils()
  {
    // nothing to do
  }

  //........................................................................


  //........................................................................

  //-------------------------------------------------------------- variables

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------- extractParams ------------------------------

  /**
   * Extract all the parameters from the request and return them.
   * This extraction is a bit limited, as it does not allow specifying multiple
   * values for a single key.
   *
   * @param       inRequest the request from the client
   *
   * @return      A multi map with keys of the parameter names and values
   *              containing the parameter values.
   *
   */
  public static @Nonnull Multimap<String, String>
    extractParams(@Nonnull HttpServletRequest inRequest)
  {
    Multimap<String, String> values = HashMultimap.create();

    // read parameters from the parameter map
    for(Map.Entry<String, String []> entry
          : inRequest.getParameterMap().entrySet())
      values.putAll(entry.getKey(), Arrays.asList(entry.getValue()));

    // don't parse post requests to special urls
    if(values.isEmpty() && !inRequest.getServletPath().startsWith("/__")
       && !inRequest.getServletPath().startsWith("/_ah/"))
    {
      BufferedReader reader = null;
      try
      {
        reader = inRequest.getReader();

        // parse all the key values pairs
        for(String line = reader.readLine(); line != null;
            line = reader.readLine())
        {
          // ignore multipart boundary or empty lines
          if(line.startsWith("--") || line.isEmpty())
            continue;

          // specially parse form data
          String []form =
            Strings.getPatterns(line,
                                "(?i)"
                                + "Content-Disposition: form-data; "
                                + "name=\"(.*?)\"(?:;\\s+filename=\"(.*?)\")?");

          if(form != null && form.length > 0)
          {
            if(form.length > 1 && form[1] != null)
            {
              values.put(form[0], form[1]);
              continue;
            }

            for(line = reader.readLine(); line != null && line.isEmpty();
                line = reader.readLine())
              ;

            if(line != null)
              values.put(form[0], line);

            continue;
          }

          String []matches = line.split("=", 2);

          if(matches.length != 2)
            Log.warning("invalid line of post request ignored: " + line);
          else
            values.put(matches[0], URLDecoder.decode(matches[1], "utf-8"));
        }
      }
      catch(java.io.IOException e)
      {
        Log.warning("Could not extract post parameters!");
      }
      finally
      {
        try
        {
          if(reader != null)
            reader.close();
        }
        catch(java.io.IOException e)
        {
          Log.warning("Could not close stream of post parameters");
        }
      }
    }

    try
    {
      if(inRequest.getQueryString() != null)
        for(String param : inRequest.getQueryString().split("&"))
        {
          String []parts = param.split("=");

          String key   = param;
          String value = "";

          if(parts != null && parts.length == 2)
          {
            key   = parts[0];
            value = parts[1];
          }

          values.put(key, URLDecoder.decode(value, "utf-8"));
        }
    }
    catch(java.io.UnsupportedEncodingException e)
    {
      Log.error("Unsupported encoding for parsing get paramters!");
    }

    return values;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //--------------------------------------------------------------- nested

    /** A simple mock servlet stream implementation. */
    public static class MockServletInputStream extends ServletInputStream
    {
      /**
       * Create a mock input stream.
       *
       * @param inContents The contents the stream will return.
       *
       */
      public MockServletInputStream(@Nonnull String inContents)
      {
        m_contents = new java.io.ByteArrayInputStream(inContents.getBytes());
      }

      /** The contents of the stream. */
      private @Nonnull java.io.ByteArrayInputStream m_contents;

      /**
       * Read a character from the stream.
       *
       * @return The character read.
       *
       */
      public int read()
      {
        return m_contents.read();
      }
    }

    /** A simple mock servlet output stream implementation. */
    public static class MockServletOutputStream extends ServletOutputStream
    {
      /**
       * Create a mock output stream.
       */
      public MockServletOutputStream()
      {
      }

      /** The text printed. */
      private @Nonnull java.io.ByteArrayOutputStream m_contents =
        new java.io.ByteArrayOutputStream();

      /**
       * Wrie a character to the string.
       *
       * @param inCharacter the character to write
       *
       */
      public void write(int inCharacter)
      {
        m_contents.write(inCharacter);
      }

      /**
       * Get the contents of the stream.
       *
       * @return the contents printed so far.
       *
       */
      public String toString()
      {
        return m_contents.toString();
      }
    }

    //......................................................................

    //----- params ---------------------------------------------------------

    /**
     * The params Test.
     * @throws Exception to lazy to catch
     */
    @org.junit.Test
    public void params() throws Exception
    {
      HttpServletRequest request =
        EasyMock.createMock(HttpServletRequest.class);
      javax.servlet.ServletInputStream inputStream =
        new MockServletInputStream("post_1=val_3\npost_2=\n"
                                   + "post_3=val_4\npost_3=val_4a\n"
                                   + "both=val_5a");

      EasyMock.expect(request.getInputStream()).andReturn(inputStream);
      EasyMock.expect(request.getQueryString())
        .andReturn("url_1=val_1&url_2&url_3=val_2&url_3=val_2a"
                   + "&both=val_5b&both=val_5c").times(2);
      EasyMock.expect(request.getServletPath()).andStubReturn("/");

      EasyMock.replay(request);

      Multimap<String, String> params = extractParams(request);
      assertContent("url_1", params.get("url_1"), "val_1");
      assertContent("url_2", params.get("url_2"), "");
      assertContentAnyOrder("url_3", params.get("url_3"), "val_2", "val_2a");
      assertContent("post_1", params.get("post_1"), "val_3");
      assertContent("post_2", params.get("post_2"), "");
      assertContentAnyOrder("post_3", params.get("post_3"), "val_4", "val_4a");
      assertContentAnyOrder("both", params.get("both"), "val_5a", "val_5b",
                            "val_5c");

      EasyMock.verify(request);
    }

    //......................................................................
  }

  //........................................................................
}
