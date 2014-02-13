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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.output.soy.SoyEntry;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.server.ServerUtils;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.Tracer;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * An entry servlet that has a single type and gets the id from the path of the
 * request.
 *
 * @file          EntryServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class EntryServlet extends PageServlet
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------- EntryServlet ---------------------------

  /**
   * Create the servlet.
   *
   */
  public EntryServlet()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------- getLastModified ----------------------------

  /**
    * Get the time of the last modification. Since entries can change anytime,
    * we don't want to have any caching.
    *
    * @return      the time of the last modification in miliseconds or -1
    *              if unknown
    *
    */
  public long getLastModified()
  {
    return -1;
  }

  //........................................................................
  //------------------------------- isPublic -------------------------------

  /**
   * Checks whether the current page is public or requires some kind of
   * login.
   *
   * @param       inRequest the request for the page
   *
   * @return      true if public, false if login is required
   */
  @Override
  public boolean isPublic(DMARequest inRequest)
  {
    AbstractEntry entry = getEntry(inRequest);
    return entry != null && entry.isBase();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //----------------------------- collectData ------------------------------

  /**
   * Collect the data that is to be printed.
   *
   * @param    inRequest  the request for the page
   * @param    inRenderer the renderer to render sub values
   *
   * @return   a map with key/value pairs for data (values can be primitives
   *           or maps or lists)
   *
   */
  @Override
  protected Map<String, Object> collectData(DMARequest inRequest,
                                            SoyRenderer inRenderer)
  {
    Map<String, Object> data = super.collectData(inRequest, inRenderer);

    String path = inRequest.getRequestURI();
    if(path == null)
    {
      data.put("content", inRenderer.render("dma.error.noEntry"));
      return data;
    }

    String action = Strings.getPattern(path, "\\.(.*)$");
    if(action != null && !action.isEmpty())
      path = path.substring(0, path.length() - action.length() - 1);
    else
      action = "show";

    AbstractEntry entry = getEntry(inRequest, path);
    if(entry != null && !entry.isShownTo(inRequest.getUser()))
    {
      data.put("content", inRenderer.render
               ("dma.errors.invalidPage",
                map("name", inRequest.getAttribute(DMARequest.ORIGINAL_PATH))));

      return data;
    }

    if(entry == null)
    {
      AbstractEntry.EntryKey<? extends AbstractEntry> key = extractKey(path);
      if(key == null)
      {
        data.put("content", inRenderer.render("dma.errors.extract",
                                              map("name", path)));
        return data;
      }

      AbstractType<? extends AbstractEntry> type = key.getType();
      String id = key.getID();

      if(inRequest.hasParam("create") && inRequest.hasUser())
      {
        // create a new entry for filling out
        Log.info("creating " + type + " '" + id + "'");

        if(type.getBaseType() == type)
          entry = type.create(id);
        else
        {
          String postfix = "";
          if(inRequest.hasParam("store"))
            postfix = "-" + inRequest.getParam("store");

          entry = type.create(Entry.TEMPORARY + postfix);
          entry.updateKey(key);

          if(inRequest.hasParam("bases"))
            for(String base : inRequest.getParam("bases").split("\\s*,\\s*"))
              entry.addBase(base);

          if(inRequest.hasParam("identified") && entry instanceof Item)
            ((Item)entry).identify();

          if(inRequest.hasParam("extensions"))
            for(String extension
                  : inRequest.getParam("extensions").split("\\s*,\\s*"))
              if(extension != null && !extension.isEmpty())
                entry.addExtension(extension);

          if(entry instanceof Entry)
            ((Entry)entry).complete();
        }
        entry.setOwner(inRequest.getUser());
      }

      if(entry == null)
      {
        data.put("content", inRenderer.render("dma.entry.create",
                                              map("id", id,
                                                  "type", type.getName())));
        return data;
      }
    }

    AbstractType<? extends AbstractEntry> type = entry.getType();
    List<String> ids = DMADataFactory.get().getIDs(type, null);

    int current = ids.indexOf(entry.getName().toLowerCase(Locale.US));
    int last = ids.size() - 1;

    String template;
    String extension;
    switch(action)
    {
      case "dma":
        extension = ".dma";
        if(inRequest.hasParam("deep"))
          template = "dma.entry.dmadeepcontainer";
        else
          template = "dma.entry.dmacontainer";
        break;

      case "print":
        extension = ".print";
        template = "dma.entry.printcontainer";
        break;

      case "summary":
        extension = ".summary";
        template = "dma.entry.summarycontainer";
        break;

      case "edit":
        extension = ".edit";
        template = "dma.entries.basecharacters.edit";
        break;

      case "show":
      default:
        extension = "";
        template = "dma.entries."
          + entry.getType().getMultipleDir().toLowerCase();
    }

    data.put
      ("content",
       inRenderer.render
       (template,
        map("entry", new SoyEntry(entry),
            "first", current <= 0 ? "" : ids.get(0) + extension,
            "previous", current <= 0 ? "" : ids.get(current - 1) + extension,
            "list", "/" + entry.getType().getMultipleLink(),
            "next", current >= last ? "" : ids.get(current + 1) + extension,
            "last", current >= last ? "" : ids.get(last) + extension,
            "variant", type.getName().replace(" ", ""),
            "id", inRequest.getParam("id")),
        ImmutableSet.of(type.getName().replace(" ", ""))));

    return data;
  }

  //........................................................................
  //------------------------- collectInjectedData --------------------------

  /**
   * Collect the injected data that is to be printed.
   *
   * @param    inRequest  the request for the page
   * @param    inRenderer the renderer to render sub values
   *
   * @return   a map with key/value pairs for data (values can be primitives
   *           or maps or lists)
   *
   */
  @Override
  protected Map<String, Object> collectInjectedData(DMARequest inRequest,
                                                    SoyRenderer inRenderer)
  {
    Tracer tracer = new Tracer("collecting entry injected data");
    BaseCharacter user = inRequest.getUser();
    AbstractEntry entry = getEntry(inRequest);

    Map<String, Object> data = super.collectInjectedData(inRequest, inRenderer);

    // If we don't have an entry, it's probably being created and thus we
    // should have access to it.
    data.put("isDM", user != null && (entry == null || entry.isDM(user)));

    data.put("isOwner", user != null && (entry == null || entry.isOwner(user)));

    Tracer tracer2 = new Tracer("collecting request parameters");
    Map<String, Object> params = Maps.newHashMap();
    for(String param : inRequest.getParams().keySet())
      if(inRequest.getParam(param).isEmpty())
        params.put(param, true);
      else
        params.put(param, inRequest.getParam(param));

    data.put("params", params);
    tracer2.done();

    tracer.done();
    return data;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.server.ServerUtils.Test
  {
    /** The request for the test. */
    private DMARequest m_request = null;

    /** The response used in the test. */
    private HttpServletResponse m_response = null;

    /** The output of the test. */
    private net.ixitxachitls.server.ServerUtils.Test.MockServletOutputStream
      m_output = null;

    //----- setUp ----------------------------------------------------------

    /** Setup the mocks for testing. */
    @org.junit.Before
      public void setUp()
    {
      m_request = EasyMock.createMock(DMARequest.class);
      m_response = EasyMock.createMock(HttpServletResponse.class);
      m_output =
        new ServerUtils.Test // $codepro.audit.disable closeWhereCreated
        .MockServletOutputStream();
    }

    //......................................................................
    //----- cleanup --------------------------------------------------------

    /**
     * Cleanup after a test.
     *
     * @throws Exception should not happen
     */
    @org.junit.After
    public void cleanup() throws Exception
    {
      m_output.close(); // $codepro.audit.disable closeInFinally
    }

    //......................................................................
    //----- createServlet --------------------------------------------------

    /**
     * Create the servlet for testing.
     *
     * @param inPath   the path requested
     * @param inEntry  the entry to return
     * @param inType   the type of entry dealing with
     * @param inID     the id of the entry looking for
     * @param inCreate true for creating a new entry, false for not
     *
     * @return the created servlet
     * @throws Exception should not happen
     */
    public EntryServlet createServlet
      (String inPath, final @Nullable AbstractEntry inEntry,
       @Nullable final AbstractType<? extends AbstractEntry> inType,
       @Nullable final String inID, boolean inCreate) throws Exception
    {
      m_response.setHeader("Content-Type", "text/html");
      m_response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(m_request.isBodyOnly()).andReturn(true).anyTimes();
      EasyMock.expect(m_request.getQueryString()).andStubReturn("");
      EasyMock.expect(m_request.getRequestURI()).andStubReturn(inPath);
      EasyMock.expect(m_request.getOriginalPath()).andStubReturn(inPath);
      EasyMock.expect(m_request.hasUserOverride()).andStubReturn(false);
      EasyMock.expect(m_response.getOutputStream()).andReturn(m_output);
      EasyMock.expect(m_request.hasUser()).andStubReturn(true);
      EasyMock.expect(m_request.getUser()).andReturn(null).anyTimes();
      EasyMock.expect(m_request.getParams())
        .andReturn(ImmutableListMultimap.<String, String>of())
        .anyTimes();
      if(inEntry == null && inType != null && inID != null)
        EasyMock.expect(m_request.hasParam("create")).andReturn(inCreate);
      EasyMock.replay(m_request, m_response);

      return new EntryServlet()
        {
          /** Serial version id. */
          private static final long serialVersionUID = 1L;

          @Override
          public @Nullable AbstractEntry getEntry(DMARequest inRequest,
                                                  String inPath)
          {
            return inEntry;
          }
        };
    }

    //......................................................................

    //----- simple ---------------------------------------------------------

    /**
     * The simple Test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void simple() throws Exception
    {
      EntryServlet servlet =
        createServlet("/baseentry/guru",
                      new net.ixitxachitls.dma.entries.BaseItem("guru"), null,
                      null, false);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertPattern("content", ".*'DMA - guru'.*>Weight<.*>Probability<.*",
                    m_output.toString());

      EasyMock.verify(m_request, m_response);
    }

    //......................................................................
    //----- no path ---------------------------------------------------------

    /**
     * No path test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void noPath() throws Exception
    {
      EntryServlet servlet = createServlet("", null, null, null, false);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content",
                   "<script>"
                   + "document.title = 'DMA - Could Not Determine Entry Key';"
                   + "</script>"
                   + "<h1 style=\"\">Could Not Determine Entry Key</h1>"
                   + "<div>The key to the entry could not be extracted from "
                   + "&#39;&#39;.</div>\n",
                   m_output.toString());

      EasyMock.verify(m_request, m_response);
    }

    //......................................................................
    //----- no entry --------------------------------------------------------

    /**
     * No entry test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void noEntry() throws Exception
    {
      EntryServlet servlet =
        createServlet("/baseentry/guru", null, null, null, false);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content",
                   "<script>"
                   + "document.title = 'DMA - Could Not Determine Entry Key';"
                   + "</script>"
                   + "<h1 style=\"\">Could Not Determine Entry Key</h1>"
                   + "<div>The key to the entry could not be extracted from "
                   + "&#39;/baseentry/guru&#39;.</div>\n",
                   m_output.toString());

      EasyMock.verify(m_request, m_response);
    }

    //......................................................................
    //----- no id -----------------------------------------------------------

    /**
     * No id test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void noID() throws Exception
    {
      EntryServlet servlet =
        createServlet("/baseentry/guru", null, BaseEntry.TYPE, null, false);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content",
                   "<script>"
                   + "document.title = 'DMA - Could Not Determine Entry Key';"
                   + "</script>"
                   + "<h1 style=\"\">Could Not Determine Entry Key</h1>"
                   + "<div>The key to the entry could not be extracted from "
                   + "&#39;/baseentry/guru&#39;.</div>\n",
                   m_output.toString());

      EasyMock.verify(m_request, m_response);
    }

    //......................................................................
    //----- create ---------------------------------------------------------

    /**
     * create test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void create() throws Exception
    {
      EntryServlet servlet =
        createServlet("/base item/guru", null,
                      net.ixitxachitls.dma.entries.BaseItem.TYPE, "guru", true);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertPattern("content", ".*'DMA - guru'.*>Weight<.*>Probability<.*",
                    m_output.toString());

      EasyMock.verify(m_request, m_response);
    }

    //......................................................................
    //----- no create ------------------------------------------------------

    /**
     * no create test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void noCreate() throws Exception
    {
      EntryServlet servlet =
        createServlet("/base entry/guru", null, BaseEntry.TYPE, "guru", false);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content",
                   "<script>document.title = 'DMA - Entry Not Found';</script>"
                   + "<h1 style=\"\">Entry Not Found</h1>"
                   + "<div>The entry &#39;guru&#39; typed &#39;base entry&#39; "
                   + "could not be found.</div>\n",
                   m_output.toString());

      EasyMock.verify(m_request, m_response);
    }

    //......................................................................
    //----- path -----------------------------------------------------------

    /** The path Test. */
    @SuppressWarnings("unchecked")
    @org.junit.Test
    public void path()
    {
      BaseEntry entry = new net.ixitxachitls.dma.entries.BaseEntry("test");
      addEntry(entry);
      EntryServlet servlet = new EntryServlet();

      EasyMock.expect(m_request.getEntry
                      ((AbstractEntry.EntryKey<BaseEntry>)
                          DMAServlet.extractKey("/base entry/test")))
        .andStubReturn(entry);

      EasyMock.replay(m_request, m_response);

      assertEquals("simple", "id",
                   extractKey("/just/some/base entry/id").getID());

      assertNull("simple", extractKey("guru/id"));
      assertEquals("simple", "id.txt-some",
                   extractKey("/just/some/base entry/id.txt-some")
                   .getID());
      assertNull("simple", extractKey("id"));

      assertEquals("entry", "test",
                   servlet.getEntry(m_request, "/just/some/base entry/test")
                   .getName());
      assertEquals("entry", "test",
                   servlet.getEntry(m_request, "/base entry/test").getName());
      assertNull("entry", servlet.getEntry(m_request, "test"));
      assertNull("entry", servlet.getEntry(m_request, ""));
      assertNull("entry", servlet.getEntry(m_request, "test/"));
      assertNull("entry", servlet.getEntry(m_request, "test/guru"));

      assertEquals("type", net.ixitxachitls.dma.entries.BaseEntry.TYPE,
                   extractKey("/base entry/test").getType());
      assertEquals("type", net.ixitxachitls.dma.entries.BaseEntry.TYPE,
                   extractKey("/just/some/base entry/test").getType());
      assertEquals("type", net.ixitxachitls.dma.entries.BaseEntry.TYPE,
                   extractKey("/base entry/test").getType());
      assertNull("type", extractKey(""));

      EasyMock.verify(m_request, m_response);
    }

    //......................................................................
  }

  //........................................................................
}
