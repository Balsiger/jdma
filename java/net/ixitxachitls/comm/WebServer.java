/******************************************************************************
 * Copyright (c) 2002-2007 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.comm;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.NotThreadSafe;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import net.ixitxachitls.comm.servlets.FixedTextServlet;
import net.ixitxachitls.util.CommandLineParser;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.ANSILogger;
import net.ixitxachitls.util.logging.EventLogger;
import net.ixitxachitls.util.logging.FileLogger;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base web server.
 *
 * @file          WebServer.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class WebServer
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ WebServer -------------------------------

  /**
   * Create and start the web server.
   *
   * @param     inHost the host name of the server
   * @param     inPort the port the server is going to use
   *
   */
  public WebServer(@Nonnull String inHost, int inPort)
  {
    if(inPort <= 0)
      throw new IllegalArgumentException("the port number must be positive");

    m_name = inHost;
    m_port = inPort;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The server. */
  protected Server m_server;

  /** The name of the server. */
  protected @Nonnull String m_name;

  /** The port of the server. */
  protected int m_port;

  /** The context during startup. */
  private ServletContextHandler m_startupContext;

  /** The startup handler used when the server is busy. */
  private static final HttpServlet s_startupServlet = new FixedTextServlet
    ("Server is Starting Up",
     "The server is currently starting up and cannot yet serve any "
     + "requests.<br />"
     + "Please wait a while til it is ready."
     + "<p>Sorry for the inconvenience ;-)</p>",
     HttpServletResponse.SC_ACCEPTED);

  /** The version string for this application. */
  private static final String s_version =
    Config.getPattern("resource:webserver.version",
                      "{resource:project.name} web server, "
                      + "{resource:project.version} version!\nSee "
                      + "{resource:project.url} for further information.\n");

  /** The log file to use. */
  private static final String s_logFile =
    Config.get("resource:web/log.file", "logs/server-%Y-%M-%D.log");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- start ---------------------------------

  /**
    * Start the web server. This will start a new thread!
    *
    */
  public void start()
  {
    // create the server
    Log.info("setting up web server '" + m_name + "' on port " + m_port);
    Log.info("classpath currently is: "
             + System.getProperty("java.class.path"));
    Log.event("", "server", "server startup on " + m_name + ":" + m_port);
    Log.track("Startup", "/server/startup/" + m_name + "/" + m_port);
    Log.track("Startup", "/startup");

    m_server = new Server();

    Log.info("starting web server");

    Connector connector = new SelectChannelConnector();
    connector.setPort(m_port);
    connector.setHost(m_name);
    m_server.addConnector(connector);
    m_server.setGracefulShutdown(5 * 1000);
    m_server.setStopAtShutdown(true);

    // install the default, startup handler
    m_startupContext = new ServletContextHandler(m_server, "/", false, false);
    m_startupContext.addServlet(new ServletHolder(s_startupServlet), "/*");

    try
    {
      m_server.start();
    }
    catch(Exception e)
    {
      Log.error("Could not start web server: " + e);
    }

    init();

    try
    {
      m_server.stop();
    }
    catch(Exception e)
    {
      Log.error("Could not stop web server: " + e);
    }

    // setup the resources for the handling or real requests
    setupServlets();

    try
    {
      m_server.start();
    }
    catch(Exception e)
    {
      Log.error("Could not start real web server: " + e);
    }
  }

  //........................................................................
  //-------------------------------- stop ----------------------------------

  /**
    * Stop the web server. This can take up to around 10 seconds!
    *
    */
  public void stop()
  {
    Log.info("stopping web server");

    try
    {
      m_server.stop();
    }
    catch(Exception e)
    {
      Log.error("Could not stop web server: " + e);
    }
  }

  //........................................................................
  //--------------------------------- init ---------------------------------

  /**
   * General initialization to be done while the server is starting up. The
   * server will server requests but will return an error message about it
   * being started up. This means this can take some time.
   *
   */
  @OverridingMethodsMustInvokeSuper
  public void init()
  {
    try
    {
      Thread.currentThread().sleep(5000);
    }
    catch(Exception e)
    {
    }

    // nothing to do here.
  }

  //........................................................................

  //---------------------------- setupServlets -----------------------------

  /**
   * Setup the servlets that this server should handle.
   *
   */
  @OverridingMethodsMustInvokeSuper
  protected void setupServlets()
  {
    // currently nothing here, but could add something if we want.
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- startup --------------------------------------------------------

    /** startup Test. */
    @org.junit.Test
    public void startup()
    {
      WebServer server = new WebServer("host", 1234);


    }

    //......................................................................

  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  /**
   * The main method used to start the application.
   *
   * @param  inArguments the command line arguments given when starting
   *
   * @throws Exception   any exception that is not caught
   *
   */
  public static void main(String []inArguments) throws Exception
  {
    //----- parse the command line -----------------------------------------

    CommandLineParser clp =
      new CommandLineParser
      (new CommandLineParser.IntegerOption
       ("p", "port", "The port the web server should use", 5555),
       new CommandLineParser.StringOption
       ("n", "name", "The name the web server should run under.",
        "localhost"),
       new CommandLineParser.Flag
       ("na", "no-ansi", "Don't log to standard output."),
       new CommandLineParser.StringOption
       ("f", "log-file", "Log to the given file.", null),
       new CommandLineParser.StringOption
       ("e", "event-log", "The file to log events to.", null),
       new CommandLineParser.EnumOption
       ("l", "log-level", "The level of details to log (FATAL, ERROR, "
        + "WARNING, NECESSARY, IMPORTANT, USEFUL, INFO, COMPLETE, DEBUG).",
        Log.Type.DEBUG),
       new CommandLineParser.Flag
       ("nal", "no-analytics",
        "Disable the use of Google analytics on the page."));

    clp.parse(inArguments);

    if(clp.hasValue("version"))
    {
      System.out.println(s_version);
      System.out.println("Use -h or --help for command line arguments");

      return;
    }

    if(clp.hasValue("help"))
    {
      System.out.println(s_version);
      System.out.println(clp.help());

      System.out.println("All other arguments are names of files to load into "
                         + "the campaign at startup.");

      return;
    }

    // add ANSI logger
    if(!clp.hasValue("no-ansi"))
    {
      Log.add("web server (ansi)", new ANSILogger());
      Log.setLevel((Log.Type)clp.getEnum("log-level"));
    }

    if(clp.hasValue("log-file"))
    {
      Log.add("web server (file)", new FileLogger(clp.getString("log-file")));
      Log.setLevel((Log.Type)clp.getEnum("log-level"));
    }
    else
      if(s_logFile.length() > 0)
        Log.add("web server (file)", new FileLogger(s_logFile));

    if(clp.hasValue("event-log"))
      Log.add("web server (events)",
              new EventLogger(clp.getString("event-log")));

    // check if we need analytics or not
    if(clp.hasValue("no-analytics"))
      Config.set("web/analytics", false);
    else
      Config.set("web/analytics", true);

    //......................................................................

    int    port = clp.get("port", Config.get("resource:web/port", 5555));
    String name = clp.get("name",
                          Config.get("resource:web/name", "localhost"));

    Log.info(s_version);

    WebServer server = new WebServer(name, port);
    server.start();
  }

  //........................................................................
}
