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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.template.soy.data.SoyData;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.CampaignEntry;
import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.dma.output.soy.SoyValue;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Group;
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
  protected String getTemplateName(DMARequest inRequest,
                                   Map<String, SoyData> inData)
  {
    String path = inData.get("path").coerceToString();
    if(path == null)
      return "dma.errors.noEntry";

    EntryKey key = (EntryKey) ((SoyValue) inData.get("key")).getValue();
    if(key == null)
      return "dma.errors.extract";

    if(!inData.containsKey("entry"))
      return "dma.errors.invalidPage";

    AbstractEntry entry = (AbstractEntry)
        ((SoyValue) inData.get("entry")).getValue();
    if(entry != null && !entry.isShownTo(inRequest.getUser()))
      return "dma.errors.invalidPage";

    String actionName = inData.get("action").coerceToString();
    Action action = Action.edit;
    for(Action act : Action.values())
      if(act.name().equalsIgnoreCase(actionName))
      {
        action = act;
        break;
      }

    switch(action)
    {
      case print:
        return "dma.entry.printcontainer";

      case summary:
        return "dma.entry.summarycontainer";

      case card:
        return "dma.entries."
            + key.getType().getMultipleDir().toLowerCase() + ".large";

      case create:
      case edit:
        return "dma.entries."
            + key.getType().getMultipleDir().toLowerCase() + ".edit";

      case show:
      default:
        return "dma.entries."
            + key.getType().getMultipleDir().toLowerCase() + ".show";
    }
  }

  /**
   * Get the action for the given path and key.
   *
   * @param inPath the path of the request
   * @param inKey the key of the entry to handle
   * @param inRequest the original request for the page
   *
   * @return the action that should be used to show the page
   */
  private Action getAction(String inPath, EntryKey inKey, DMARequest inRequest)
  {
    if(isCreate(inRequest, inKey))
      return Action.edit;

    String actionName = Strings.getPattern(inPath, ACTION_REGEXP);
    for(Action action : Action.values())
      if(action.name().equalsIgnoreCase(actionName))
        return action;

    return Action.show;
  }

  /**
   * Check whether the request is for creating a new entry.
   *
   * @param inRequest the request for the page
   * @param inKey the key for the entry
   *
   * @return true if a new entry is to be created, false if not
   */
  private boolean isCreate(DMARequest inRequest, EntryKey inKey)
  {
    return inRequest.hasUser()
        && (inRequest.hasParam(Action.create.name())
            || Action.create.name().equalsIgnoreCase(inKey.getID()));
  }

  @Override
  protected Map<String, Object> collectData(DMARequest inRequest,
                                            SoyRenderer inRenderer)
  {
    Map<String, Object> data = super.collectData(inRequest, inRenderer);

    String path = (String)data.get(Key.path.name());
    if(path == null)
      return data;

    Optional<EntryKey> key = extractKey(path);
    data.put("key",
             new SoyValue(key.isPresent() ? key.get().toString() : "(null)",
                          key.orNull()));
    if(!key.isPresent())
      return data;

    Action action = getAction(path, key.get(), inRequest);
    data.put("action", action.name());

    Optional<? extends AbstractEntry> entry = Optional.absent();
    boolean isCreate = isCreate(inRequest, key.get());
    if(isCreate)
    {
      AbstractType<? extends AbstractEntry> type = key.get().getType();
      String id = key.get().getID();

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
                : inRequest.getParam("bases").get().split("\\s*,,\\s*"))
              if(!base.isEmpty())
                entry.get().addBase(base);

          if(entry.get() instanceof Entry)
            ((Entry) entry.get()).complete();
        }
      }

      data.put("entry", entry.orNull());
      if(entry.isPresent())
        entry.get().setOwner(inRequest.getUser().get());
      else
      {
        data.put("id", id);
        data.put("type", type.getName());
        return data;
      }
    }
    else
    {
      entry = getEntry(inRequest, path);
      if(entry.isPresent() && !entry.get().isShownTo(inRequest.getUser()))
      {
        data.put("name", inRequest.getAttribute(DMARequest.ORIGINAL_PATH));
        return data;
      }
    }

    if(entry.isPresent())
    {
      AbstractType<? extends AbstractEntry> type = entry.get().getType();
      List<String> ids =
          DMADataFactory.get().getIDs(type, Optional.<EntryKey>absent());

      int current = ids.indexOf(entry.get().getName().toLowerCase(Locale.US));
      int last = ids.size() - 1;

      data.put("entry",
               new SoyValue(entry.get().getKey().toString(), entry.get()));
      data.put("first", current <= 0 ? "" : ids.get(0) + "." + action);
      data.put("previous",
               current <= 0 ? "" : ids.get(current - 1) + "." + action);
      data.put("list", "/" + entry.get().getType().getMultipleLink());
      data.put("next",
               current >= last ? "" : ids.get(current + 1) + "." + action);
      data.put("last", current >= last ? "" : ids.get(last) + "." + action);
      data.put("variant", type.getName().replace(" ", ""));
      data.put("id",
               inRequest.hasParam("id")
                   ? inRequest.getParam("id").get() : entry.get().getName());
      data.put("create", isCreate);
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
    data.put("hasDMAccess", user.isPresent() && user.get().hasAccess(Group.DM));
    data.put("isOwner", user.isPresent()
        && (!entry.isPresent() || entry.get().isOwner(user.get())));

    Tracer tracer2 = new Tracer("collecting request parameters");
    Map<String, Object> params = Maps.newHashMap();
    for(String param : inRequest.getParams().keySet())
      if(inRequest.getParam(param).get().isEmpty())
        params.put(param, true);
      else
        params.put(param, new SoyValue("params", inRequest.getParam(param)));

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
    private StringWriter m_output = null;

    /** Setup the mocks for testing. */
    @org.junit.Before
      public void setUp()
    {
      m_request = EasyMock.createMock(DMARequest.class);
      m_response = EasyMock.createMock(HttpServletResponse.class);
      m_output = new StringWriter();
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
      m_response.setContentType("text/html");
      m_response.setCharacterEncoding("UTF-8");
      m_response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(m_request.isBodyOnly()).andReturn(true).anyTimes();
      EasyMock.expect(m_request.getQueryString()).andStubReturn("");
      EasyMock.expect(m_request.getRequestURI()).andStubReturn(inPath);
      EasyMock.expect(m_request.getOriginalPath()).andStubReturn(inPath);
      EasyMock.expect(m_request.hasUserOverride()).andStubReturn(false);
      EasyMock.expect(m_response.getWriter())
          .andReturn(new PrintWriter(m_output));
      EasyMock.expect(m_request.hasUser()).andStubReturn(true);
      EasyMock.expect(m_request.getUser()).andReturn(
          Optional.of(new BaseCharacter("test"))).anyTimes();
      EasyMock.expect(m_request.hasParam("dev")).andStubReturn(false);
      EasyMock.expect(m_request.getParam("id")).andStubReturn(
          inEntry.isPresent() ? Optional.of(inEntry.get().getName())
              : Optional.<String>absent());
      EasyMock.expect(m_request.hasParam("id"))
          .andStubReturn(inEntry.isPresent());
      EasyMock.expect(m_request.getParams())
        .andReturn(ImmutableListMultimap.<String, String>of())
        .anyTimes();
      if(!inEntry.isPresent() && inType.isPresent() && inID.isPresent())
        EasyMock.expect(m_request.hasParam("create")).andStubReturn(inCreate);
      else
        EasyMock.expect(m_request.hasParam("create")).andStubReturn(false);
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
        createServlet("/base item/guru",
                      Optional.of(new BaseItem("guru")),
                      Optional.<AbstractType<? extends AbstractEntry>>absent(),
                      Optional.<String>absent(), false);

      assertFalse("handle", servlet.handle(m_request, m_response).isPresent());
      assertPattern("content", ".*>Weight:.*>Probability:.*",
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
      EntryServlet servlet = createServlet(
          "", Optional.<AbstractEntry>absent(),
          Optional.<AbstractType<? extends AbstractEntry>>absent(),
          Optional.<String>absent(), false);

      assertFalse("handle", servlet.handle(m_request, m_response).isPresent());
      assertPattern("content",
                    ".*<script>"
                        + "document.title = "
                        + "'DMA - Could Not Determine Entry Key';"
                        + "</script>"
                        + "<h1 style=\"\">Could Not Determine Entry Key</h1>"
                        + "<div>The key to the entry could not be extracted "
                        + "from &#39;&#39;.</div>.*\n",
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

      assertFalse("handle", servlet.handle(m_request, m_response).isPresent());
      assertPattern("content",
                    ".*<script>"
                        + "document.title = "
                        + "'DMA - Could Not Determine Entry Key';"
                        + "</script>"
                        + "<h1 style=\"\">Could Not Determine Entry Key</h1>"
                        + "<div>The key to the entry could not be extracted "
                        + "from &#39;/baseentry/guru&#39;.</div>.*\n",
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

      assertFalse("handle", servlet.handle(m_request, m_response).isPresent());
      assertPattern("content",
                    ".*<script>"
                        + "document.title = "
                        + "'DMA - Could Not Determine Entry Key';"
                        + "</script>"
                        + "<h1 style=\"\">Could Not Determine Entry Key</h1>"
                        + "<div>The key to the entry could not be extracted "
                        + "from &#39;/baseentry/guru&#39;.</div>\n.*",
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
      assertPattern("content", ".*>Weight:.*>Probability:.*",
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

      assertFalse("handle", servlet.handle(m_request, m_response).isPresent());
      assertPattern("content",
                    ".*The requested page &#39;/base entry/guru&#39; does not "
                    + "exist..*",
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
