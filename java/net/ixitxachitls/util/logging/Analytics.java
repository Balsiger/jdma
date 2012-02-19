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

package net.ixitxachitls.util.logging;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.easymock.EasyMock;

import net.ixitxachitls.util.AsyncExecutor;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * Allows logging of information to Google analytics.
 *
 * @file          Analytics.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ThreadSafe
public class Analytics
{
  //----------------------------------------------------------------- nested

  /** A tracker thread for tracking messages to be processed for analytics. */
  @ThreadSafe
  private static class Tracker extends AsyncExecutor<String>
  {
    /** Create the tracker thread. */
    public Tracker()
    {
      super(30 * 1000, 0, Config.get("web/analytics.trackers", 100));

      setPriority(MIN_PRIORITY);
    }

    /**
     * Execute tracking of an url.
     *
     * @param inURL the url of the (virtual) page to track.
     *
     */
    @Override
	public void execute(String inURL)
    {
      try
      {
        URLConnection conn = new URL(inURL).openConnection();

        if(!(conn instanceof HttpURLConnection))
        {
          Log.warning("Can only track http urls, '" + inURL + "' ignored");
          return;
        }

        HttpURLConnection connection = (HttpURLConnection)conn;

        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod("GET");
        connection.connect();
        int code = connection.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK)
        {
          Log.warning("Could not track for url: " + inURL + ", response "
                      + code);
        }
        else
        {
          Log.debug("Tracked for url: " + inURL);
        }
      }
      catch(java.io.IOException e)
      {
        Log.warning("Could not track for url: " + inURL + ", " + e);
      }
    }
  }

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------ Analytics -------------------------------

  /**
   * Create the analytics adaptor.
   *
   */
  public Analytics()
  {
    this(new Tracker());
  }

  //........................................................................
  //------------------------------ Analytics -------------------------------

  /**
   * Private constructor for tests.
   *
   * @param     inTracker the tracker to use.
   *
   */
  private Analytics(Tracker inTracker)
  {
    m_tracker = inTracker;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The id to use when contacting Google analytics. */
  private static final @Nonnull String s_id =
    Config.get("web/analytics.id", "UA-1524401-1");

  /** The thread to use for tracking information. */
  private final @Nonnull Tracker m_tracker;

  /** Flag if tracker has been initialized. */
  private boolean m_init = false;

  /** A random number generator. */
  private static final @Nonnull Random s_random = new Random();

  /** The host name tracked. */
  private static final @Nonnull String s_hostName =
    Config.get("web/host.name", "www.ixitxachitls.net");

  /** The base url for tracking. */
  private static final @Nonnull String s_baseURL =
    "http://www.google-analytics.com/__utm.gif"
    + "?utmwv=1" // Google analytics version (4.3?)
    + "&utmhn=" + Encodings.urlEncode(s_hostName) // host name
    + "&utmcs=UTF-8" // character encoding
    + "&utmcn=1" // new campaign session
    + "&utmsr=1440x900" // screen resulution
    + "&utmsc=32-bit" // color depth
    + "&utmje=1" // java enabled
    + "&utmr=none" // no referral page
    + "&utmul=en" // user language
    + "&utmcr=1" // carriage return
    + "&utmdt=<title>" // document title
    + "&utmp=<page>" // page
    + "&utmac=" + s_id; // analytics id

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- track ---------------------------------

  /**
   * Track a requst to the given name. You can use a directory structure to
   * get hierarchies.
   *
   * @param       inTitle the title of the information to track
   * @param       inPage  the name of the page tracked
   *
   */
  public void track(@Nonnull String inTitle, @Nonnull String inPage)
  {
    init();

    // create the URL
    String url = s_baseURL
      .replaceAll("<title>", Encodings.urlEncode(inTitle))
      .replaceAll("<page>", Encodings.urlEncode(inPage))
      + createCookie();

    // store it in the queue for processing
    m_tracker.offer(url, 500);
  }

  //........................................................................
  //----------------------------- createCookie -----------------------------

  /**
   * Create the cookie information for the url.
   *
   * @return       a string with the cookie informaiton
   *
   */
  private @Nonnull String createCookie()
  {
    int cookie = s_random.nextInt(2147483647) + 1;
    int random = s_random.nextInt(2147483647) + 1;
    long now = new Date().getTime();

    return "&utmn=" + random // random number
      + "&utmcc=__utma='" + cookie + "." + random + "." + now + "."
      + now + "." + now + ".3;+__utmb=" + cookie + ";+__utmc="
      + cookie + ";+__utmz=" + cookie + "." + now
      + ".3.2.utmccn=" + Encodings.urlEncode(s_hostName)
      + "|utmcsr=google|utmctr=term|utmcmd=Dorganic;+";
  }

  //.........................................................................

  //-------------------------------- event ----------------------------------

  /**
   * Track a requst to the given name. You can use a directory structure to
   * get hierarchies.
   *
   * @param       inObject name of the object for the event
   * @param       inAction name of action that lead to the event
   * @param       inLabel  label of the event
   * @param       inValue  the value for the event
   *
   */
  public void event(@Nonnull String inObject, @Nonnull String inAction,
                    @Nonnull String inLabel, @Nonnull String inValue)
  {
    init();

    // create the URL
    String url = s_baseURL
      + "&utmdt=" + Encodings.urlEncode("title") // document title
      + "&utmp=" + Encodings.urlEncode("page")
      + createCookie()
      + "&utme=5(" + Encodings.urlEncode(inObject) + "*"
      + Encodings.urlEncode(inAction) + "*"
      + Encodings.urlEncode(inLabel) + ")("
      + Encodings.urlEncode(inValue) + ")";

    // store it in the queue for processing
    m_tracker.offer(url, 500);
  }

  //........................................................................
  //--------------------------------- init ---------------------------------

  /**
   * Initialize the tracker.
   *
   */
  protected synchronized void init()
  {
    if(m_init)
      return;

    m_tracker.start();

    m_init = true;
  }

  //........................................................................
  //--------------------------------- stop ---------------------------------

  /**
   * Stop the analytics tracker.
   *
   */
  public void stop()
  {
    m_tracker.done();

    try
    {
      m_tracker.join(5 * 1000);
    }
    catch(InterruptedException e)
    {
      // ignoring, wanted to stop anyway
    }
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test.
   *
   */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- tracker --------------------------------------------------------

    /**
     * Tracker test. This test will fail without an internet connection.
     *
     * @throws Exception just a test
     *
     */
    @org.junit.Test
    public void tracker() throws Exception
    {
      Tracker tracker = new Tracker();

      tracker.start();

      // not http
      m_logger.addExpected("WARNING: Can only track http urls, "
                           + "'file://resources/config/test/test.config' "
                           + "ignored");
      tracker.offer("file://resources/config/test/test.config", 100);

      // invalid host
      m_logger.addExpected("WARNING: Could not track for url: "
                           + "http://localhost:0, "
                           + "java.net.NoRouteToHostException: Can't assign "
                           + "requested address");
      tracker.offer("http://localhost:0", 100);

      // invalid url
      m_logger.addExpected("WARNING: Could not track for url: "
                           + "http://www.google.com/guruguru, response 404");
      tracker.offer("http://www.google.com/guruguru", 100);

      // valid
      tracker.offer("http://www.google.com", 100);

      tracker.done();
      tracker.join(3 * 1000);
    }

    //......................................................................
    //----- cookie ---------------------------------------------------------

    /** Cookie test. */
    @org.junit.Test
    public void cookie()
    {
      Analytics analytics = new Analytics();

      assertPattern("cookie",
                    "&utmn=(\\d+)&utmcc=__utma='"
                    + "(\\d+)\\.\\1\\.(\\d+)\\.\\3\\.\\3\\.3;"
                    + "\\+__utmb=\\2;\\+__utmc=\\2;\\+__utmz="
                    + "\\2\\.\\3\\.3\\.2\\."
                    + "utmccn=www.ixitxachitls\\.net\\|utmcsr=google\\|"
                    + "utmctr=term\\|utmcmd=Dorganic;\\+",
                    analytics.createCookie());
    }

    //......................................................................
    //----- track ----------------------------------------------------------

    /** Tracking tests. */
    @org.junit.Test
    public void track()
    {
      Tracker tracker = EasyMock.createStrictMock(Tracker.class);
      Analytics analytics = new Analytics(tracker);

      tracker.start();
      EasyMock.expect
        (tracker.offer(EasyMock.and
                       (EasyMock.startsWith("http://www.google-analytics.com/"),
                        EasyMock.and(EasyMock.contains("&utmdt=title"),
                                     EasyMock.contains("&utmp=page"))),
                       EasyMock.eq(500L))).andReturn(true);
      EasyMock.expect
        (tracker.offer(EasyMock.and
                       (EasyMock.startsWith("http://www.google-analytics.com/"),
                        EasyMock.contains
                        ("&utme=5(object*action*label)(value)")),
                       EasyMock.eq(500L))).andReturn(true);
      tracker.done();

      EasyMock.replay(tracker);
      analytics.track("title", "page");
      analytics.event("object", "action", "label", "value");
      analytics.stop();
      EasyMock.verify(tracker);
    }

    //......................................................................
  }

  //........................................................................
}
