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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.dma.output.soy.SoyValue;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.server.ServerUtils;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.Tracer;
import net.ixitxachitls.util.logging.Log;

/**
 * An entry servlet that has a single type and gets the id from the path of the
 * request.
 *
 * @file          EntryServlet.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class EntryServlet extends PageServlet
{
  /**
   * Create the servlet.
   */
  public EntryServlet()
  {
    // nothing to do
  }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /**
    * Get the time of the last modification. Since entries can change anytime,
    * we don't want to have any caching.
    *
    * @return      the time of the last modification in miliseconds or -1
    *              if unknown
    */
  public long getLastModified()
  {
    return -1;
  }

  @Override
  public boolean isPublic(DMARequest inRequest)
  {
    Optional<AbstractEntry> entry = getEntry(inRequest);
    return entry.isPresent() && entry.get().isBase();
  }

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

    Optional<EntryKey> key = extractKey(path);
    if(!key.isPresent())
    {
      data.put("content", inRenderer.render("dma.errors.extract",
                                            Optional.of(map("name", path))));
      return data;
    }

    Optional<? extends AbstractEntry> entry = Optional.absent();
    if(inRequest.hasUser()
        && (inRequest.hasParam("create")
            || "CREATE".equalsIgnoreCase(key.get().getID())))
    {
      AbstractType<? extends AbstractEntry> type = key.get().getType();
      String id = key.get().getID();

      action = "create";

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
        if(entry.isPresent())
        {
          entry.get().updateKey(key.get());

          if(inRequest.hasParam("values"))
          {
            Multimap<String, String> values = ArrayListMultimap.create();
            for(String value
                : inRequest.getParam("values").get().split("\\s*,\\s*"))
            {
              String[] parts = value.split(":");
              if(parts.length != 2)
                continue;

              values.put(parts[0], parts[1]);
            }

            entry.get().set(new Values(values));
          }

          // bases are overwritten by values if done before!
          if(inRequest.hasParam("bases"))
            for(String base
                : inRequest.getParam("bases").get().split("\\s*,\\s*"))
              if(!base.isEmpty())
                entry.get().addBase(base);

          if(inRequest.hasParam("identified") && entry.get() instanceof Item)
            ((Item) entry.get()).identify();

          if(entry.get() instanceof Entry)
            ((Entry) entry.get()).complete();
        }
      }

      if(entry.isPresent())
        entry.get().setOwner(inRequest.getUser().get());
      else
      {
        data.put("content",
                 inRenderer.render("dma.entry.create",
                                   Optional.of(map("id", id,
                                                   "type", type.getName()))));
        return data;
      }
    }
    else
    {
      entry = getEntry(inRequest, path);
      if(entry.isPresent() && !entry.get().isShownTo(inRequest.getUser()))
      {
        data.put("content", inRenderer.render
            ("dma.errors.invalidPage",
             Optional.of(map(
                 "name", inRequest.getAttribute(DMARequest.ORIGINAL_PATH)))));

        return data;
      }
    }

    if(entry.isPresent())
    {
      AbstractType<? extends AbstractEntry> type = entry.get().getType();
      List<String> ids = DMADataFactory.get().getIDs(type, null);

      int current = ids.indexOf(entry.get().getName().toLowerCase(Locale.US));
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

        case "card":
          extension = ".card";
          template = "dma.entries."
              + entry.get().getType().getMultipleDir().toLowerCase() + ".large";
          break;

        case "create":
        case "edit":
          extension = ".edit";
          template = "dma.entries."
              + entry.get().getType().getMultipleDir().toLowerCase() + ".edit";
          break;

        case "show":
        default:
          extension = "";
          template = "dma.entries."
              + entry.get().getType().getMultipleDir().toLowerCase() + ".show";
      }

      data.put(
          "content",
          inRenderer.render(
              template,
              Optional.of(map(
                  "entry",
                  new SoyValue(entry.get().getKey().toString(), entry.get()),
                  "first", current <= 0 ? "" : ids.get(0) + extension,
                  "previous", current <= 0
                      ? "" : ids.get(current - 1) + extension,
                  "list", "/" + entry.get().getType().getMultipleLink(),
                  "next", current >= last
                      ? "" : ids.get(current + 1) + extension,
                  "last", current >= last ? "" : ids.get(last) + extension,
                  "variant", type.getName().replace(" ", ""),
                  "id", inRequest.getParam("id"),
                  "create", "create".equals(action)))));
      data.put("title", entry.get().getName());
    }

    return data;
  }

  @Override
  protected Map<String, Object> collectInjectedData(DMARequest inRequest,
                                                    SoyRenderer inRenderer)
  {
    Tracer tracer = new Tracer("collecting entry injected data");
    Optional<BaseCharacter> user = inRequest.getUser();
    Optional<AbstractEntry> entry = getEntry(inRequest);

    Map<String, Object> data = super.collectInjectedData(inRequest, inRenderer);

    // If we don't have an entry, it's probably being created and thus we
    // should have access to it.
    data.put("isDM", user != null
        && (!entry.isPresent() || entry.get().isDM(user)));
    data.put("isDev", DMAServlet.isDev() || inRequest.hasParam("dev"));
    data.put("isOwner", user.isPresent()
        && (!entry.isPresent() || entry.get().isOwner(user.get())));

    Tracer tracer2 = new Tracer("collecting request parameters");
    Map<String, Object> params = Maps.newHashMap();
    for(String param : inRequest.getParams().keySet())
      if(inRequest.getParam(param).get().isEmpty())
        params.put(param, true);
      else
        params.put(param, inRequest.getParam(param));

    data.put("params", params);
    tracer2.done();

    tracer.done();
    return data;
  }

  //----------------------------------------------------------------------------

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
      (String inPath, final Optional<? extends AbstractEntry> inEntry,
       final Optional<AbstractType<? extends AbstractEntry>> inType,
       final Optional<String> inID, boolean inCreate) throws Exception
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
      EasyMock.expect(m_request.getUser()).andReturn(
          Optional.<BaseCharacter>absent()).anyTimes();
      EasyMock.expect(m_request.getParams())
        .andReturn(ImmutableListMultimap.<String, String>of())
        .anyTimes();
      if(!inEntry.isPresent() && inType.isPresent() && inID.isPresent())
        EasyMock.expect(m_request.hasParam("create")).andReturn(inCreate);
      EasyMock.replay(m_request, m_response);

      return new EntryServlet()
        {
          /** Serial version id. */
          private static final long serialVersionUID = 1L;

          @Override
          public Optional<AbstractEntry> getEntry(DMARequest inRequest,
                                                  String inPath)
          {
            return (Optional<AbstractEntry>) inEntry;
          }
        };
    }

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
                      Optional.of(new BaseItem("guru")),
                      Optional.<AbstractType<? extends AbstractEntry>>absent(),
                      Optional.<String>absent(), false);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertPattern("content", ".*'DMA - guru'.*>Weight<.*>Probability<.*",
                    m_output.toString());

      EasyMock.verify(m_request, m_response);
    }

    /**
     * No path test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void noPath() throws Exception
    {
      EntryServlet servlet =
          createServlet("", Optional.<AbstractEntry>absent(),
                        Optional.<AbstractType<? extends AbstractEntry>>absent(),
                        Optional.<String>absent(), false);

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

    /**
     * No entry test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void noEntry() throws Exception
    {
      EntryServlet servlet =
        createServlet("/baseentry/guru", Optional.<AbstractEntry>absent(),
                      Optional.<AbstractType<? extends AbstractEntry>>absent(),
                      Optional.<String>absent(), false);

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

    /**
     * No id test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void noID() throws Exception
    {
      EntryServlet servlet =
        createServlet("/baseentry/guru",
                      Optional.<AbstractEntry>absent(),
                      Optional.<AbstractType<? extends AbstractEntry>>of(
                          BaseEntry.TYPE),
                      Optional.<String>absent(), false);

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

    /**
     * Create test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void create() throws Exception
    {
      EntryServlet servlet =
        createServlet("/base item/guru", Optional.<AbstractEntry>absent(),
                      Optional.<AbstractType<? extends AbstractEntry>>of(
                          BaseItem.TYPE), Optional.of("guru"), true);

      assertFalse("handle", servlet.handle(m_request, m_response).isPresent());
      assertPattern("content", ".*'DMA - guru'.*>Weight<.*>Probability<.*",
                    m_output.toString());

      EasyMock.verify(m_request, m_response);
    }

    /**
     * no create test.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void noCreate() throws Exception
    {
      EntryServlet servlet =
        createServlet("/base entry/guru", Optional.<AbstractEntry>absent(),
                      Optional.<AbstractType<? extends AbstractEntry>>of(
                          BaseEntry.TYPE), Optional.of("guru"), false);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content",
                   "<script>document.title = 'DMA - Entry Not Found';</script>"
                   + "<h1 style=\"\">Entry Not Found</h1>"
                   + "<div>The entry &#39;guru&#39; typed &#39;base entry&#39; "
                   + "could not be found.</div>\n",
                   m_output.toString());

      EasyMock.verify(m_request, m_response);
    }

    /** The path Test. */
    @org.junit.Test
    public void path()
    {
      EntryServlet servlet = new EntryServlet();

      EasyMock.expect(m_request.getEntry
                      (DMAServlet.extractKey("/base entry/test").get()))
        .andStubReturn(DMADataFactory.get().getEntry(
            DMAServlet.extractKey("/base entry/test").get()));

      EasyMock.replay(m_request, m_response);

      assertEquals("simple", "id",
                   extractKey("/just/some/base entry/id").get().getID());

      assertFalse("simple", extractKey("guru/id").isPresent());
      assertEquals("simple", "id.txt-some",
                   extractKey("/just/some/base entry/id.txt-some")
                   .get().getID());
      assertFalse("simple", extractKey("id").isPresent());

      assertEquals("entry", "test",
                   servlet.getEntry(m_request, "/just/some/base entry/test")
                          .get().getName());
      assertEquals("entry", "test",
                   servlet.getEntry(m_request, "/base entry/test").get()
                          .getName());
      assertFalse("entry", servlet.getEntry(m_request, "test").isPresent());
      assertFalse("entry", servlet.getEntry(m_request, "").isPresent());
      assertFalse("entry", servlet.getEntry(m_request, "test/").isPresent());
      assertFalse("entry", servlet.getEntry(m_request,
                                            "test/guru").isPresent());

      assertEquals("type", net.ixitxachitls.dma.entries.BaseEntry.TYPE,
                   extractKey("/base entry/test").get().getType());
      assertEquals("type", net.ixitxachitls.dma.entries.BaseEntry.TYPE,
                   extractKey("/just/some/base entry/test").get().getType());
      assertEquals("type", net.ixitxachitls.dma.entries.BaseEntry.TYPE,
                   extractKey("/base entry/test").get().getType());
      assertFalse("type", extractKey("").isPresent());

      EasyMock.verify(m_request, m_response);
    }
  }
}
