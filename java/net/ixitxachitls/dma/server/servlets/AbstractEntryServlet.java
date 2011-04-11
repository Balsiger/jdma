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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.output.html.HTMLDocument;
import net.ixitxachitls.output.ascii.ASCIIDocument;
// import net.ixitxachitls.output.commands.Button;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Divider;
// import net.ixitxachitls.output.commands.Hidden;
//import net.ixitxachitls.output.commands.Linebreak;
import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.output.commands.Verbatim;
import net.ixitxachitls.output.html.HTMLWriter;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base servlet for entry printing.
 *
 * @file          AbstractEntryServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public abstract class AbstractEntryServlet extends PageServlet
{
  //--------------------------------------------------------- constructor(s)

  //------------------------- AbstractEntryServlet -------------------------

  /**
   * The basic constructor for the servlet.
   *
   * @param   inData all the available data
   *
   */
  public AbstractEntryServlet(@Nonnull DMAData inData)
  {
    m_data = inData;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** All the available data. */
  protected @Nonnull DMAData m_data;

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
  //------------------------------- getEntry -------------------------------

  /**
   * Get the abstract entry associated with the given request.
   *
   * @param       inPath the path to the page
   *
   * @return      the entry or null if it could not be found
   *
   */
  public abstract @Nullable AbstractEntry getEntry(@Nonnull String inPath);

  //........................................................................
  //------------------------------- getType --------------------------------

  /**
   * Get the type associated with the given request.
   *
   * @param       inPath the path to the page
   *
   * @return      the type for the request
   *
   */
  public abstract @Nullable AbstractType<? extends AbstractEntry>
    getType(String inPath);

  //........................................................................
  //------------------------------- getPath --------------------------------

  /**
   * Get the path to the given entry.
   *
   * @param       inEntry the entry to get the path for
   *
   * @return      the path to the entry
   *
   */
  public abstract @Nullable String getPath(@Nonnull AbstractEntry inEntry);

  //........................................................................
  //-------------------------------- getID ---------------------------------

  /**
   * Get the id associated with the given request.
   *
   * @param       inPath the path to the page
   *
   * @return      the id of the entry
   *
   */
  public abstract @Nullable String getID(String inPath);

  //........................................................................
  //------------------------------- getFirst -------------------------------

  /**
   * Get the first available entry.
   *
   * @param     inType the type entry to get
   *
   * @return    the first entry available
   *
   */
  public abstract @Nonnull AbstractEntry getFirst
    (@Nonnull AbstractType<? extends AbstractEntry> inType);

  //........................................................................
  //------------------------------ getPrevious -----------------------------

  /**
   * Get the first available entry.
   *
   * @param     inID   the id of the current entry
   * @param     inType the type entry to get
   *
   * @return    the first entry available
   *
   */
  public abstract @Nullable AbstractEntry getPrevious
    (@Nonnull String inID,
     @Nonnull AbstractType<? extends AbstractEntry> inType);

  //........................................................................
  //------------------------------- getNext --------------------------------

  /**
   * Get the first available entry.
   *
   * @param     inID   the id of the current entry
   * @param     inType the type entry to get
   *
   * @return    the first entry available
   *
   */
  public abstract @Nullable AbstractEntry getNext
    (@Nonnull String inID,
     @Nonnull AbstractType<? extends AbstractEntry> inType);

  //........................................................................
  //------------------------------- getLast -------------------------------

  /**
   * Get the last available entry.
   *
   * @param     inType the type entry to get
   *
   * @return    the first entry available
   *
   */
  public abstract @Nonnull AbstractEntry getLast
    (@Nonnull AbstractType<? extends AbstractEntry> inType);

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

      return;
    }

    boolean dma = false;
    boolean pdf = false;
    boolean txt = false;
    if(inPath.endsWith(".dma"))
    {
      dma = true;
      inPath = inPath.substring(0, inPath.length() - 4);
    }
    else if(inPath.endsWith(".pdf"))
    {
      pdf = true;
      inPath = inPath.substring(0, inPath.length() - 4);
    }
    else if(inPath.endsWith(".txt"))
    {
      txt = true;
      inPath = inPath.substring(0, inPath.length() - 4);
    }

    AbstractEntry entry = getEntry(inPath);

    if(entry == null)
    {
      AbstractType<? extends AbstractEntry> type = getType(inPath);

      if(type == null)
      {
        Log.warning("could not extract entry from '" + inPath + "'");
        inWriter.title("Not Found")
          .begin("h1").add("Type not found").end("h1")
          .add("Could not find the type of the entry of this page.");

        return;
      }

      String id = getID(inPath);

      if(id == null || id.isEmpty())
      {
        Log.warning("could not extract id from '" + inPath + "'");
        inWriter.title("Not Found")
          .begin("h1").add("ID Not Found").end("h1")
          .add("Could not find the id of the entry of this page.");

        return;
      }

      if(inRequest.hasParam("create"))
      {
        // create a new entry for filling out
        Log.info("creating " + type + " '" + id + "'");

        entry = type.create(id, m_data);
      }

      if(entry == null)
      {
        // entry not found, but ask if we want to create a new entry
        Log.warning("could not find entry '" + id + "'.");
        inWriter.title("Not Found")
          .begin("h1").add("Entry Not Found").end("h1")
          .add("Could not find " + type + " '" + id + "'.")
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

    AbstractEntry first = getFirst(entry.getType());
    if(first == entry)
      first = null;

    AbstractEntry previous = getPrevious(entry.getID(), entry.getType());
    AbstractEntry next = getNext(entry.getID(), entry.getType());

    AbstractEntry last = getLast(entry.getType());
    if(last == entry)
      last = null;

    Command navigation =
      new Divider("entry-nav",
                  new Command(new Link(new Divider("first icon"
                                                   + (first == null
                                                      ? " disabled" : ""), ""),
                                       first == null ? "" : getPath(first)),
                              new Link(new Divider("previous icon"
                                                   + (previous == null
                                                      ? " disabled" : ""), ""),
                                       previous == null ? ""
                                       : getPath(previous)),
                              new Link(new Divider("index icon", ""), "/user/"),
                              new Link(new Divider("next icon"
                                                   + (next == null
                                                      ? " disabled" : ""), ""),
                                       next == null ? "" : getPath(next)),
                              new Link(new Divider("last icon"
                                                   + (last == null
                                                      ? " disabled" : ""), ""),
                                       last == null ? "" : getPath(last))));

    if(!pdf)
      document.add(navigation);

    boolean dm = entry.isDM(inRequest.getUser());

    if(pdf)
      ;
    else if(dma && dm)
      document.add(new Divider("dma-formatted",
                               new Verbatim(entry.toString())));
    else if(txt)
    {
      ASCIIDocument doc = new ASCIIDocument(80);
      doc.add(entry.printPage(dm));
      document.add(new Divider("text-formatted",
                               new Verbatim(doc.toString())));
    }
    else
      document.add(entry.printPage(dm));

    if(!pdf)
      document.add(navigation);

    inWriter.add(document.toString());

    // add some javascript for the entry
    if(!pdf && !dma && !txt)
      inWriter.script("$(document).ready(function ()",
                      "{",
                      "  $('DIV.files IMG.image')"
                      + ".mouseover(util.replaceMainImage)"
                      + ".mouseout(util.restoreMainImage)",
                      "});");
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
    private DMARequest m_request;

    /** The response used in the test. */
    private HttpServletResponse m_response;

    /** The output of the test. */
    private net.ixitxachitls.server.ServerUtils.Test.MockServletOutputStream
      m_output;

    /** Paths encountered while testing. */
    private final List<String> m_paths = new ArrayList<String>();

    /** Expected text for the dummy navigation. */
    private static final String s_navigation = "<div class=\"entry-nav\">"
      + "<a href=\"\" class=\"link\" onclick=\"return util.link(event, '');\">"
      + "<div class=\"first icon disabled\"></div>"
      + "</a>"
      + "<a href=\"\" class=\"link\" onclick=\"return util.link(event, '');\">"
      + "<div class=\"previous icon disabled\"></div>"
      + "</a>"
      + "<a href=\"/user/\" class=\"link\" "
      + "onclick=\"return util.link(event, '/user/');\">"
      + "<div class=\"index icon\"></div>"
      + "</a>"
      + "<a href=\"\" class=\"link\" onclick=\"return util.link(event, '');\">"
      + "<div class=\"next icon disabled\"></div>"
      + "</a>"
      + "<a href=\"\" class=\"link\" onclick=\"return util.link(event, '');\">"
      + "<div class=\"last icon disabled\"></div>"
      + "</a>"
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
      m_paths.clear();
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
    public AbstractEntryServlet createServlet
      (String inPath, final AbstractEntry inEntry,
       final AbstractType<? extends AbstractEntry> inType,
       final String inID, boolean inCreate) throws Exception
    {
      m_response.setHeader("Content-Type", "text/html");
      m_response.setHeader("Cache-Control", "max-age=0");
      EasyMock.expect(m_request.isBodyOnly()).andReturn(true).anyTimes();
      EasyMock.expect(m_request.getQueryString()).andReturn("").anyTimes();
      EasyMock.expect(m_request.getPathInfo()).andReturn(inPath);
      EasyMock.expect(m_response.getOutputStream()).andReturn(m_output);
      EasyMock.expect(m_request.getUser()).andReturn(null).anyTimes();
      if(inEntry == null && inType != null && inID != null)
        EasyMock.expect(m_request.hasParam("create")).andReturn(inCreate);
      EasyMock.replay(m_request, m_response);

      return new AbstractEntryServlet(new DMAData("path"))
        {
          private static final long serialVersionUID = 1L;

          @Override
          public String getID(String inPath)
          {
            m_paths.add(inPath);
            return inID;
          }

          @Override
          public AbstractType<? extends AbstractEntry> getType(String inPath)
          {
            m_paths.add(inPath);
            return inType;
          }

          @Override
          public @Nonnull String getPath(@Nonnull AbstractEntry inEntry)
          {
            return "/somewhere/" + inEntry.getID();
          }

          @Override
          public AbstractEntry getEntry(String inPath)
          {
            m_paths.add(inPath);
            return inEntry;
          }

          @Override
          public AbstractEntry getFirst
            (AbstractType<? extends AbstractEntry> inType)
          {
            return inEntry;
          }

          @Override
          public @Nullable AbstractEntry getPrevious
            (String inID,
             AbstractType<? extends AbstractEntry> inType)
          {
            return null;
          }

          @Override
          public AbstractEntry getNext
            (String inID, AbstractType<? extends AbstractEntry> inType)
          {
            return null;
          }

          @Override
          public AbstractEntry getLast
            (AbstractType<? extends AbstractEntry> inType)
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
      AbstractEntryServlet servlet =
        createServlet("/baseentry/guru",
                      new BaseEntry("guru", new net.ixitxachitls.dma.data
                                    .DMAData("path")), null, null,
                      false);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content",
                   "    <SCRIPT type=\"text/javascript\">\n"
                   + "      document.title = 'base entry: guru';\n"
                   + "    </SCRIPT>\n"
                   + s_imageScript
                   + "    " + s_navigation
                   + "<span class=\"error\"> * id * </span> "
                   + "guru"
                   + s_navigation
                   + "\n",
                   m_output.toString());
      assertContent("paths", m_paths, "/baseentry/guru");
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
      AbstractEntryServlet servlet = createServlet(null, null, null, null,
                                                   false);

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
      assertContent("paths", m_paths);
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
      AbstractEntryServlet servlet =
        createServlet("/baseentry/guru", null, null, null, false);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content",
                   "    <SCRIPT type=\"text/javascript\">\n"
                   + "      document.title = 'Not Found';\n"
                   + "    </SCRIPT>\n"
                   + "    <H1>\n"
                   + "      Type not found\n"
                   + "    </H1>\n"
                   + "    Could not find the type of the entry of this page.\n",
                   m_output.toString());
      assertContent("paths", m_paths, "/baseentry/guru", "/baseentry/guru");

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
      AbstractEntryServlet servlet =
        createServlet("/baseentry/guru", null, BaseEntry.TYPE, null, false);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content",
                   "    <SCRIPT type=\"text/javascript\">\n"
                   + "      document.title = 'Not Found';\n"
                   + "    </SCRIPT>\n"
                   + "    <H1>\n"
                   + "      ID Not Found\n"
                   + "    </H1>\n"
                   + "    Could not find the id of the entry of this page.\n",
                   m_output.toString());
      assertContent("paths", m_paths,
                    "/baseentry/guru", "/baseentry/guru", "/baseentry/guru");

      m_logger.addExpected("WARNING: could not extract id from "
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
      AbstractEntryServlet servlet =
        createServlet("/baseentry/guru", null, BaseEntry.TYPE, "guru", true);

      assertNull("handle", servlet.handle(m_request, m_response));
      assertEquals("content",
                   "    <SCRIPT type=\"text/javascript\">\n"
                   + "      document.title = 'base entry: guru';\n"
                   + "    </SCRIPT>\n"
                   + s_imageScript
                   + "    " + s_navigation
                   + "<span class=\"error\"> * id * </span> "
                   + "guru"
                   + s_navigation
                   + "\n",
                   m_output.toString());
      assertContent("paths", m_paths,
                    "/baseentry/guru", "/baseentry/guru", "/baseentry/guru");
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
      AbstractEntryServlet servlet =
        createServlet("/baseentry/guru", null, BaseEntry.TYPE, "guru", false);

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
      assertContent("paths", m_paths,
                    "/baseentry/guru", "/baseentry/guru", "/baseentry/guru");

      m_logger.addExpected("WARNING: could not find entry 'guru'.");
    }

    //......................................................................
  }

  //........................................................................
}
