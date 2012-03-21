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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.output.html.HTMLDocument;
import net.ixitxachitls.output.ascii.ASCIIDocument;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Divider;
import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.output.commands.Verbatim;
import net.ixitxachitls.output.html.HTMLWriter;
import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * An entry servlet that has a single type and gets the id from the path of the
 * request.
 *
 *
 * @file          EntryServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 *
 */

//..........................................................................

//__________________________________________________________________________

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

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- writeBody ------------------------------

  /**
   * Handles the body content of the request.
   *
   * @param     inWriter  the writer to take up the content (will be closed
   *                      by the PageServlet)
   * @param     inPath    the path of the request
   * @param     inRequest the request for the page
   *
   */
  @Override
  @OverridingMethodsMustInvokeSuper
  protected void writeBody(@Nonnull HTMLWriter inWriter,
                           @Nullable String inPath,
                           @Nonnull DMARequest inRequest)
  {
    super.writeBody(inWriter, inPath, inRequest);

    if(inPath == null)
    {
      inWriter.title("Not Found")
        .begin("h1").add("Invalid Reference").end("h1")
        .add("The page referenced does not exist!");
      Log.warning("no path given for request");

      return;
    }

    boolean dma = false;
    boolean txt = false;
    String path = Files.decodeName(inPath);
    if(path.endsWith(".dma"))
    {
      dma = true;
      path = path.substring(0, path.length() - 4);
    }
    else if(path.endsWith(".txt"))
    {
      txt = true;
      path = path.substring(0, path.length() - 4);
    }

    AbstractEntry entry = getEntry(path);

    if(entry == null)
    {
      AbstractEntry.EntryKey<? extends AbstractEntry> key = extractKey(path);
      if(key == null)
      {
        Log.warning("could not extract entry from '" + path + "'");
        inWriter.title("Not Found")
          .begin("h1").add("Could Not Extract Entry").end("h1")
          .add("Could not find the type and/or id of the entry for this page.");

        return;
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
          {
            for(String base : inRequest.getParam("bases").split("\\s*,\\s*"))
              entry.addBase(base);
          }

          if(inRequest.hasParam("extensions"))
          {
            for(String extension
                  : inRequest.getParam("extensions").split("\\s*,\\s*"))
              if(extension != null && !extension.isEmpty())
                entry.addExtension(extension);
          }
          else
            entry.addBase(id);

          if(entry instanceof Entry)
            ((Entry)entry).complete();
        }
        entry.setOwner(inRequest.getUser());
      }

      if(entry == null)
      {
        // entry not found, but ask if we want to create a new entry
        Log.warning("could not find entry '" + id + "'.");

        inWriter.title("Not Found")
          .begin("h1").add("Entry Not Found").end("h1")
          .add("Could not find " + type + " '" + id + "'.");

        if(inRequest.hasUser())
            inWriter
              .script("if(confirm('The desired entry does not exist!\\n\\n"
                      + "Do you want to create a new entry with id \\'" + id
                      + "\\'?'))",
                      "  location.href = location.href.replace(/\\?.*$/, '') "
                      + "+ '?create';");

        return;
      }
    }

    String title = entry.getType() + ": " + entry.getName();
    inWriter.title(title);

    HTMLDocument document = new HTMLDocument(title);
    AbstractType<? extends AbstractEntry> type = entry.getType();

    List<String> ids = DMADataFactory.get().getIDs(type, null);

    int current = ids.indexOf(entry.getName());
    int last = ids.size() - 1;

    Command navigation =
      new Divider("entry-nav",
                  new Command
                  (new Link(new Divider("first sprite"
                                        + (current <= 0
                                           ? " disabled" : ""), ""),
                            current <= 0 ? "" : ids.get(0)),
                   new Link(new Divider("previous sprite"
                                        + (current <= 0
                                           ? " disabled" : ""), ""),
                            current <= 0 ? "" : ids.get(current - 1)),
                   new Link(new Divider("index sprite", ""),
                            "/" + entry.getType().getMultipleLink()),
                   new Link(new Divider("next sprite"
                                        + (current >= last
                                           ? " disabled" : ""), ""),
                            current >= last ? "" : ids.get(current + 1)),
                   new Link(new Divider("last sprite"
                                        + (current >= last
                                           ? " disabled" : ""), ""),
                            current >= last ? "" : ids.get(last)),
                   new Link(new Divider("add sprite", ""),
                            "javascript:createEntry()"),
                   new Link(new Divider("remove sprite", ""),
                            "javascript:removeEntry('"
                            + entry.getName() + "')")));

    document.add(navigation);

    boolean dm = false;
    if(inRequest.getUser() != null)
      dm = entry.isDM(inRequest.getUser());

    if(dma && dm)
      document.add(new Divider("dma-formatted",
                               new Verbatim(entry.toString())));
    else
      if(txt)
      {
        ASCIIDocument doc = new ASCIIDocument(80);
        doc.add(entry.printPage(inRequest.getUser()));
        document.add(new Divider("text-formatted",
                                 new Verbatim(doc.toString())));
      }
      else
        document.add(entry.printPage(inRequest.getUser()));

    document.add(navigation);

    inWriter.add(document.toString());

    // add some javascript for the entry
    if(!dma && !txt)
      inWriter.script("$(document).ready(function ()",
                      "{",
                      "  $('DIV.files IMG.image')"
                      + ".mouseover(util.replaceMainImage)"
                      + ".mouseout(util.restoreMainImage)",
                      "});");

    addNavigation(inWriter, entry.getNavigation());

  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** The request for the test. */
    private DMARequest m_request = null;

    /** The response used in the test. */
    private HttpServletResponse m_response = null;

    /** The output of the test. */
    private net.ixitxachitls.server.ServerUtils.Test.MockServletOutputStream
      m_output = null;

    /** Expected text for the dummy navigation. */
    private static final String s_navigation = "<div class=\"entry-nav\">"
      + "<a href=\"\" class=\"link\" onclick=\"return util.link(event, '');\">"
      + "<div class=\"first sprite disabled\"></div>"
      + "</a>"
      + "<a href=\"\" class=\"link\" onclick=\"return util.link(event, '');\">"
      + "<div class=\"previous sprite disabled\"></div>"
      + "</a>"
      + "<a href=\"/entrys\" class=\"link\" "
      + "onclick=\"return util.link(event, '/entrys');\">"
      + "<div class=\"index sprite\"></div>"
      + "</a>"
      + "<a href=\"\" class=\"link\" onclick=\"return util.link(event, '');\">"
      + "<div class=\"next sprite disabled\"></div>"
      + "</a>"
      + "<a href=\"\" class=\"link\" onclick=\"return util.link(event, '');\">"
      + "<div class=\"last sprite disabled\"></div>"
      + "</a>"
      + "<a href=\"javascript:createEntry()\" class=\"link\" "
      + "onclick=\"return util.link(event, 'javascript:createEntry()');\">"
      + "<div class=\"add sprite\"></div>"
      + "</a>"
      + "<a href=\"javascript:removeEntry('guru')\" class=\"link\" "
      + "onclick=\"return util.link(event, "
      + "'javascript:removeEntry('guru')');\">"
      + "<div class=\"remove sprite\"></div></a>"
      + "</div>";

    /** Exepcted test for the image script. */
    private static final String s_imageScript =
      "    <SCRIPT type=\"text/javascript\">\n"
      + "      $(document).ready(function ()\n"
      + "      {\n"
      + "        $('DIV.files IMG.image')"
      + ".mouseover(util.replaceMainImage)"
      + ".mouseout(util.restoreMainImage)\n"
      + "      });\n"
      + "    </SCRIPT>\n";

    //----- setUp ----------------------------------------------------------

    /** Setup the mocks for testing. */
    @org.junit.Before
      public void setUp()
    {
      m_request = EasyMock.createMock(DMARequest.class);
      m_response = EasyMock.createMock(HttpServletResponse.class);;
      m_output =
        new net.ixitxachitls.server.ServerUtils.Test.MockServletOutputStream();
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
      m_output.close();
      EasyMock.verify(m_request, m_response);
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
      (String inPath, final AbstractEntry inEntry,
       final AbstractType<? extends AbstractEntry> inType,
       final String inID, boolean inCreate) throws Exception
    {
      m_response.setHeader("Content-Type", "text/html");
      m_response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(m_request.isBodyOnly()).andReturn(true).anyTimes();
      EasyMock.expect(m_request.getQueryString()).andReturn("").anyTimes();
      EasyMock.expect(m_request.getRequestURI()).andReturn(inPath);
      EasyMock.expect(m_response.getOutputStream()).andReturn(m_output);
      EasyMock.expect(m_request.hasUser()).andStubReturn(true);
      EasyMock.expect(m_request.getUser()).andReturn(null).anyTimes();
      if(inEntry == null && inType != null && inID != null)
        EasyMock.expect(m_request.hasParam("create")).andReturn(inCreate);
      EasyMock.replay(m_request, m_response);

      return new EntryServlet()
        {
          private static final long serialVersionUID = 1L;

          @Override
          public @Nullable AbstractEntry getEntry(@Nonnull String inPath)
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
                      new BaseEntry("guru"), null, null, false);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content",
                   "    <SCRIPT type=\"text/javascript\">\n"
                   + "      document.title = 'base entry: guru';\n"
                   + "    </SCRIPT>\n"
                   + s_imageScript
                   + "    " + s_navigation
                   + "\n<h1 class=\"entrytitle\">"
                   + "guru</h1>\n"
                   + s_navigation
                   + "\n"
                   + "    <SCRIPT type=\"text/javascript\">\n"
                   + "      $('#subnavigation').html(' &raquo; "
                   + "<a href=\"/entrys\" class=\"navigation-link\" "
                   + "onclick=\"return util.link(event, \\'/entrys\\');\" >"
                   + "entry</a> &raquo; <a href=\"/entry/guru\" "
                   + "class=\"navigation-link\" "
                   + "onclick=\"return util.link(event, \\'/entry/guru\\');\" >"
                   + "guru</a>');\n"
                   + "    </SCRIPT>\n",
                   m_output.toString());
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
      EntryServlet servlet = createServlet(null, null, null, null, false);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content",
                   "    <SCRIPT type=\"text/javascript\">\n"
                   + "      document.title = 'Not Found';\n"
                   + "    </SCRIPT>\n"
                   + "    <H1>\n"
                   + "      Invalid Reference\n"
                   + "    </H1>\n"
                   + "    The page referenced does not exist!\n",
                   m_output.toString());

      m_logger.addExpected("WARNING: no path given for request");
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
                   "    <SCRIPT type=\"text/javascript\">\n"
                   + "      document.title = 'Not Found';\n"
                   + "    </SCRIPT>\n"
                   + "    <H1>\n"
                   + "      Could Not Extract Entry\n"
                   + "    </H1>\n"
                   + "    Could not find the type and/or id of the entry "
                   + "for this page.\n",
                   m_output.toString());

      m_logger.addExpected("WARNING: could not extract entry from "
                           + "'/baseentry/guru'");
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
                   "    <SCRIPT type=\"text/javascript\">\n"
                   + "      document.title = 'Not Found';\n"
                   + "    </SCRIPT>\n"
                   + "    <H1>\n"
                   + "      Could Not Extract Entry\n"
                   + "    </H1>\n"
                   + "    Could not find the type and/or id of the entry "
                   + "for this page.\n",
                   m_output.toString());

      m_logger.addExpected("WARNING: could not extract entry from "
                           + "'/baseentry/guru'");
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
        createServlet("/base entry/guru", null, BaseEntry.TYPE, "guru", true);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content",
                   "    <SCRIPT type=\"text/javascript\">\n"
                   + "      document.title = 'base entry: guru';\n"
                   + "    </SCRIPT>\n"
                   + s_imageScript
                   + "    " + s_navigation
                   + "\n<h1 class=\"entrytitle\">"
                   + "guru</h1>\n"
                   + s_navigation
                   + "\n"
                   + "    <SCRIPT type=\"text/javascript\">\n"
                   + "      $('#subnavigation').html(' &raquo; "
                   + "<a href=\"/entrys\" class=\"navigation-link\" "
                   + "onclick=\"return util.link(event, \\'/entrys\\');\" >"
                   + "entry</a> &raquo; <a href=\"/entry/guru\" "
                   + "class=\"navigation-link\" "
                   + "onclick=\"return util.link(event, \\'/entry/guru\\');\" >"
                   + "guru</a>');\n"
                   + "    </SCRIPT>\n",
                   m_output.toString());
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
                   "    <SCRIPT type=\"text/javascript\">\n"
                   + "      document.title = 'Not Found';\n"
                   + "    </SCRIPT>\n"
                   + "    <SCRIPT type=\"text/javascript\">\n"
                   + "      if(confirm('The desired entry does not exist!\\n\\n"
                   + "Do you want to create a new entry with id "
                   + "\\'guru\\'?'))\n"
                   + "        location.href = "
                   + "location.href.replace(/\\?.*$/, '') + '?create';\n"
                   + "    </SCRIPT>\n"
                   + "    <H1>\n"
                   + "      Entry Not Found\n"
                   + "    </H1>\n"
                   + "    Could not find base entry 'guru'.\n",
                   m_output.toString());

      m_logger.addExpected("WARNING: could not find entry 'guru'.");
    }

    //......................................................................
    //----- path -----------------------------------------------------------

    /** The path Test. */
    @org.junit.Test
    public void path()
    {
      addEntry(new net.ixitxachitls.dma.entries.BaseEntry("test"));
      EntryServlet servlet = new EntryServlet();

      EasyMock.replay(m_request, m_response);

      assertEquals("simple", "id",
                   servlet.extractKey("/just/some/base entry/id").getID());

      assertNull("simple", servlet.extractKey("guru/id"));
      assertEquals("simple", "id.txt-some",
                   servlet.extractKey("/just/some/base entry/id.txt-some")
                   .getID());
      assertNull("simple", servlet.extractKey("id"));

      assertEquals("entry", "test",
                   servlet.getEntry("/just/some/base entry/test").getName());
      assertEquals("entry", "test",
                   servlet.getEntry("/base entry/test").getName());
      assertNull("entry", servlet.getEntry("test"));
      assertNull("entry", servlet.getEntry(""));
      assertNull("entry", servlet.getEntry("test/"));
      assertNull("entry", servlet.getEntry("test/guru"));

      assertEquals("type", net.ixitxachitls.dma.entries.BaseEntry.TYPE,
                   servlet.extractKey("/base entry/test").getType());
      assertEquals("type", net.ixitxachitls.dma.entries.BaseEntry.TYPE,
                   servlet.extractKey("/just/some/base entry/test").getType());
      assertEquals("type", net.ixitxachitls.dma.entries.BaseEntry.TYPE,
                   servlet.extractKey("/base entry/test").getType());
      assertNull("type", servlet.extractKey(""));
    }

    //......................................................................
  }

  //........................................................................
}
