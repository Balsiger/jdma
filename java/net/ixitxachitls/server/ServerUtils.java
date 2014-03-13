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

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ArrayListMultimap;
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
@ParametersAreNonnullByDefault
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
  public static Multimap<String, String> extractParams
    (HttpServletRequest inRequest)
  {
    Multimap<String, String> values = ArrayListMultimap.create();

    // don't parse post requests to special urls
    if(!inRequest.getServletPath().startsWith("/__")
       && !inRequest.getServletPath().startsWith("/_ah/"))
    {
      // read parameters from the parameter map
      Map<String, String []> params = inRequest.getParameterMap();
      for(Map.Entry<String, String []> entry : params.entrySet())
        values.putAll(entry.getKey(), Arrays.asList(entry.getValue()));

      if(values.isEmpty())
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
                                  + "name=\"(.*?)\"(?:;\\s+"
                                  + "filename=\"(.*?)\")?");

            if(form.length > 0)
            {
              if(form.length > 1 && form[1] != null)
              {
                values.put(form[0], form[1]);
                continue;
              }

              for(line = reader.readLine(); line != null && line.isEmpty();
                  line = reader.readLine())
              {
                // nothing to do
              }

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
    }

    try
    {
      if(inRequest.getQueryString() != null)
        for(String param : inRequest.getQueryString().split("&"))
        {
          String []parts = param.split("=");

          String key   = param;
          String value = "";

          if(parts.length == 2)
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

    /** The AppEngine TestHelper for the UserService. */
    protected LocalServiceTestHelper m_localServiceTestHelper =
      new LocalServiceTestHelper(new LocalUserServiceTestConfig());

    @Override
    public void setUpTest()
    {
      super.setUpTest();
      m_localServiceTestHelper.setEnvIsLoggedIn(true);
      m_localServiceTestHelper.setEnvEmail("test@test.net");
      m_localServiceTestHelper.setEnvAuthDomain("test");
      m_localServiceTestHelper.setUp();
    }

    @Override
    public void tearDown()
    {
      super.tearDown();

      m_localServiceTestHelper.tearDown();
      m_localServiceTestHelper = null;
    }

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
      public MockServletInputStream(String inContents)
      {
        // $codepro.audit.disable closeWhereCreated
        m_contents = new java.io.ByteArrayInputStream(inContents.getBytes());
        // $codepro.audit.enable
      }

      /** The contents of the stream. */
      private java.io.ByteArrayInputStream m_contents;

      /**
       * Read a character from the stream.
       *
       * @return The character read.
       *
       */
      @Override
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
      // $codepro.audit.disable closeWhereCreated
      private java.io.ByteArrayOutputStream m_contents =
        new java.io.ByteArrayOutputStream();
      // $codepro.audit.enable

      /**
       * Wrie a character to the string.
       *
       * @param inCharacter the character to write
       *
       */
      @Override
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
      @Override
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
      try (java.io.BufferedReader reader = new java.io.BufferedReader
        (new java.io.StringReader("post_1=val_3\npost_2=\n"
                                  + "post_3=val_4\npost_3=val_4a\n"
                                  + "both=val_5a")))
      {
        EasyMock.expect(request.getParameterMap()).andStubReturn
          (new java.util.HashMap<String, String []>());
        EasyMock.expect(request.getReader()).andStubReturn(reader);
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
        assertContentAnyOrder("post_3", params.get("post_3"), "val_4",
                              "val_4a");
        assertContentAnyOrder("both", params.get("both"), "val_5a", "val_5b",
                              "val_5c");

        EasyMock.verify(request);
      }
    }

    //......................................................................
  }

  //........................................................................
}
