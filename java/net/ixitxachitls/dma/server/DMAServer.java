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

package net.ixitxachitls.dma.server;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.server.filters.DMAFilter;
import net.ixitxachitls.dma.server.filters.PrefixRedirectFilter;
import net.ixitxachitls.dma.server.servlets.LoginServlet;
import net.ixitxachitls.dma.server.servlets.LogoutServlet;
import net.ixitxachitls.dma.server.servlets.SaveActionServlet;
import net.ixitxachitls.dma.server.servlets.StaticPageServlet;
import net.ixitxachitls.dma.server.servlets.TypedEntryPDFServlet;
import net.ixitxachitls.dma.server.servlets.TypedEntryServlet;
import net.ixitxachitls.server.WebServer;
import net.ixitxachitls.server.servlets.FileServlet;
import net.ixitxachitls.server.servlets.TemplateServlet;
import net.ixitxachitls.util.CommandLineParser;
import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.ANSILogger;
import net.ixitxachitls.util.logging.EventLogger;
import net.ixitxachitls.util.logging.FileLogger;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the webserver providing access to the complete application.
 *
 * The following urls are used:
 *
 *   /                          the main page, either index.html or the overview
 *   /css/*                     static css files
 *   /js/*                      static js files
 *   /icons/*                   static icons
 *   /files/*                   static files
 *   /files-internal/*          static internal files
 *   /robots.txt                static robots.txt file
 *   /favicon.ico               static favicon
 *   /entry/<type>/<id>         base entries
 *   /user/<id>                 user information
 *   /user/<id>/product/<id>    a user's products
 *   /campaign/<id>             campaign information
 *   /cmapaign/<id>/<type>/<id> campaign specific entry
 *
 * @file          DMAServer.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class DMAServer extends WebServer
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ WebServer -------------------------------

  /**
    * Create the web server.
    *
    * @param       inHost      the host name of the server
    * @param       inPort      the port number to use
    * @param       inBase      the name of the dma file containing the base
    *                          campaigns
    * @param       inCampaigns the name of the file containing campaign info
    * @param       inUsers     the name of the file containing user info
    *
    */
  public DMAServer(@Nonnull String inHost, int inPort, @Nonnull String inBase,
                   @Nonnull String inCampaigns, @Nonnull String inUsers)
  {
    super(inHost, inPort);

    m_users =
      new DMAData(DATA_DIR, Files.concatenate("BaseCharacters", inUsers));

//     java.util.Date start = new java.util.Date();

    // load the necessary files into the base campaign
    // TODO: we should do that better, not having to read into a temp camapign
//     BaseCampaign all = new BaseCampaign("All");
//     all.readFile(inBase, DATA_DIR);
//     m_baseCampaign = (BaseCampaign)all.iterator().next();

//     // load the campaign specific files
//     m_campaigns.readFile(inCampaigns, DATA_DIR);

//     java.util.Date end = new java.util.Date();

//     long milis = end.getTime() - start.getTime();

//     Log.event(null, "startup",
//               "startup and reading took " + (milis / 1000) + "."
//               + Strings.pad((int)(milis % 1000), 3, true)
//               + " seconds.");
//     Log.trackEvent("Server", "Startup", "server", "" + (int)(milis / 1000));

//     // setup the resources for the handling or real requests
//     setupServlets();
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The root context for all root information. */
  private @Nonnull ServletContextHandler m_rootContext;

  /** The user information. */
  private @Nonnull DMAData m_users;

  /** The base campaign for the user information. */
//   private BaseCampaign m_users = new BaseCampaign("Users");

  /** The base campaign with all the base information. */
//   private BaseCampaign m_baseCampaign = new BaseCampaign("Base");

  /** The campaign for this server. */
//   private Campaign m_campaigns =
//     new Campaign("Campaigns", "cps",
//                  Config.get("resource:web/campaign.max.id", 0));

//   // we have to ensure Global is read before the following, to setup the
//   // default variable defintions
//   static
//   {
//     String ignore = Global.PROJECT;
//   }

  /** The directory containing all data files. */
  public static final @Nonnull String DATA_DIR = Config.get
    ("resource:web/dir.dma", "dma");

  /** The extractor to get the product data. */
//   private static final Extractor<DMARequest, Iterator<AbstractEntry>>
//     s_productExtractor =
//     new Extractor<DMARequest, Iterator<AbstractEntry>>()
//   {
//     public Iterator<AbstractEntry> get(DMARequest inRequest)
//     {
//       if(inRequest == null)
//         throw new IllegalArgumentException("need a request here");

//       BaseCharacter user = inRequest.getUser();

//       if(user == null)
//         return null;

//       return user.getProductData().getAbstractEntries();
//     }
//   };

  /** The extractor for all information available to a dm. */
//   private final Extractor<DMARequest, Iterator<AbstractEntry>>
//     m_dmExtractor =
//     new Extractor<DMARequest, Iterator<AbstractEntry>>()
//   {
//     @SuppressWarnings("unchecked") // casting of generic array
//     public Iterator<AbstractEntry> get(DMARequest inRequest)
//     {
//       if(inRequest == null)
//         throw new IllegalArgumentException("need a request here");

//       BaseCharacter user = inRequest.getUser();

//       if(user == null)
//         return BaseCampaign.GLOBAL.getAbstractEntries();

//       ArrayList<Iterator<AbstractEntry>> iterators =
//         new ArrayList<Iterator<AbstractEntry>>();

//       iterators.add(BaseCampaign.GLOBAL.getAbstractEntries());
//       iterators.add(user.getProductData().getAbstractEntries());

//       // all the campaign information this user is dm of
//       for(Iterator<Entry> i = m_campaigns.getUnique(); i.hasNext(); )
//       {
//         Entry entry = i.next();

//         if(!(entry instanceof Campaign))
//           continue;

//         if(user.getName().equalsIgnoreCase(((Campaign)entry).getDMName()))
//           iterators.add(((Campaign)entry).getAbstractEntries());
//       }

//       return new MultiIterator<AbstractEntry>
//       ((Iterator<AbstractEntry> [])iterators.toArray(new Iterator [0]));
//     }
//   };

  /** The extractor for all information available to a dm. */
//   private static final Extractor<DMARequest, Iterator<AbstractEntry>>
//     s_globalExtractor =
//     new Extractor<DMARequest, Iterator<AbstractEntry>>()
//   {
//     public Iterator<AbstractEntry> get(DMARequest inRequest)
//     {
//       return BaseCampaign.GLOBAL.getAbstractEntries();
//     }
//   };

  /** The extractor for all information available to a dm. */
//   private final Extractor<DMARequest, Iterator<AbstractEntry>>
//     m_campaignExtractor =
//     new Extractor<DMARequest, Iterator<AbstractEntry>>()
//   {
//     public Iterator<AbstractEntry> get(DMARequest inRequest)
//     {
//       return m_campaigns.getAbstractEntries();
//     }
//   };

  /** The extractor for all information available to a dm. */
//   private final Extractor<DMARequest, Iterator<AbstractEntry>>
//     m_usersExtractor =
//     new Extractor<DMARequest, Iterator<AbstractEntry>>()
//   {
//     public Iterator<AbstractEntry> get(DMARequest inRequest)
//     {
//       return m_users.getAbstractEntries();
//     }
//   };

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //---------------------------- setupServlets -----------------------------

  /**
   * Setup all the standard servlets to use in this server.
   *
   */
  @OverridingMethodsMustInvokeSuper
  protected void setupServlets()
  {
    super.setupServlets();

    Log.info("Setting up real contexts");
    m_rootContext = new ServletContextHandler(m_server, "/", false, false);

    m_rootContext.addFilter
      (new FilterHolder
       (new DMAFilter(m_users.getEntries(BaseCharacter.TYPE))), "/*", 0);
    m_rootContext.addFilter(new FilterHolder(new PrefixRedirectFilter("/pdf")),
                            "*.pdf", 0);

//     m_rootContext.setAttribute("users", m_users);
//     m_rootContext.setAttribute("campaigns", m_campaigns);

//     m_rootContext.addFilter(new FilterHolder(new MainPageFilter()),
//                             "/", 0);


    // static files
    m_rootContext.addServlet
      (new ServletHolder(new TemplateServlet("/css", "text/css",
                                             "web/css/template")),
       "/css/*");

    m_rootContext.addServlet
      (new ServletHolder(new FileServlet("/js", "text/javascript")), "/js/*");

    m_rootContext.addServlet
      (new ServletHolder(new FileServlet("/icons", "image/png")), "/icons/*");

    m_rootContext.addServlet
      (new ServletHolder(new FileServlet("/text/robots.txt", "text/plain")),
       "/robots.txt");

    m_rootContext.addServlet
      (new ServletHolder(new FileServlet("/icons/favicon.png", "image/png")),
       "/favicon.ico");

    m_rootContext.addServlet
      (new ServletHolder(new StaticPageServlet("/html/")), "/*");

    m_rootContext.addServlet
      (new ServletHolder(new FileServlet("/files", "")), "/files/*");

//     m_rootContext.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.USER)),
//        "/files-internal/*", 0);
//     m_rootContext.addServlet
//       (new ServletHolder(new StaticFileServlet("/files-internal", "")),
//        "/files-internal/*");



//     // base entry servlet
//     m_rootContext.addServlet
//       (new ServletHolder(new BaseEntryServlet(m_baseCampaign)),
//        "/entry/*");

    // users
    m_rootContext.addServlet
      (new ServletHolder(new TypedEntryServlet<BaseCharacter>
                         (BaseCharacter.TYPE, "/user/", m_users)
                         .withAccess(BaseCharacter.Group.USER)), "/user/*");
    m_rootContext.addServlet
      (new ServletHolder(new TypedEntryPDFServlet<BaseCharacter>
                         (BaseCharacter.TYPE, "/user/", m_users)
                         .withAccess(BaseCharacter.Group.USER)), "/pdf/user/*");

//     m_rootContext.addFilter(new FilterHolder(new MeUserFilter()),
//                             "/user/me/*", 0);

//     // products
//     for(BaseEntry entry : m_users)
//       if(entry instanceof BaseCharacter)
//         m_rootContext.addServlet
//           (new ServletHolder
//            (new EntryServlet(((BaseCharacter)entry).getProductData())),
//            "/user/" + entry.getName() + "/*");

//     // campaigns
//     m_rootContext.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.PLAYER)),
//        "/campaign/*", 0);

//   m_rootContext.addServlet(new ServletHolder(new EntryServlet(m_campaigns)),
//                              "/campaign/*");

//     // entries
//     for(Entry entry : m_campaigns)
//       if(entry instanceof Campaign)
//         m_rootContext.addServlet
//           (new ServletHolder(new EntryServlet((Campaign)entry)),
//            "/campaign/" + entry.getID() + "/*");

//     // entry specific stuff
//     m_rootContext.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.USER)),
//        "/index/products/*", 0);
//     m_rootContext.addServlet
//       (new ServletHolder(new ProductServlet(m_users)),
//        "/index/products/*");

//     m_rootContext.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.USER)),
//        "/index/products/*", 0);
//     m_rootContext.addServlet
//       (new ServletHolder(new ProductListServlet(m_users)),
//        "/pdf/products/list/*");

//     m_rootContext.addServlet
//       (new ServletHolder(new ProductShelfListServlet(m_users)),
//        "/pdf/products/shelf/*");

//     m_rootContext.addServlet
//       (new ServletHolder(new ProductMissingListServlet(m_users)),
//        "/pdf/products/missing/*");

//     m_rootContext.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.PLAYER)),
//        "/pdf/items/*", 0);
//     m_rootContext.addServlet
//       (new ServletHolder(new ItemListServlet(m_campaigns, false)),
//        "/pdf/items/*");

//     m_rootContext.addServlet
//       (new ServletHolder(new ItemListServlet(m_campaigns, true)),
//        "/pdf/dm/items/*");

//     m_rootContext.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.DM)),
//        "/pdf/dm/encounters/*", 0);
//     m_rootContext.addServlet
//       (new ServletHolder(new EncounterListServlet(m_campaigns)),
//        "/pdf/dm/encounters/*");

//     m_rootContext.addServlet
//       (new ServletHolder(new ImageServlet(m_campaigns)), "/images/*");

//     // user pages
//     m_rootContext.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.USER)),
//        "/overview", 0);
//     m_rootContext.addServlet
//       (new ServletHolder(new UserOverviewServlet(m_campaigns)), "/overview");

//     // indexes
//     for(Iterator<Index<? extends Index>> i = ValueGroup.getIndexes();
//         i.hasNext(); )
//     {
//       final Index<? extends Index> index = i.next();

//       String path = index.getPath();

//       if(!path.startsWith("/"))
//         path = "/index/" + path;

//       path += "/*";

//       Log.info("Installing index servlet for '" + path + "'");
//       IndexServlet servlet = null;

//       switch(index.getDataSource())
//       {
//         case dm:
//           servlet = new IndexServlet(index, m_dmExtractor);

//           break;

//         case global:
//           servlet = new IndexServlet(index, s_globalExtractor);

//           break;

//         case campaign:
//           servlet = new IndexServlet(index, m_campaignExtractor);

//           break;

//         case products:
//           servlet = new IndexServlet(index, s_productExtractor);

//           break;

//         case user:
//           servlet = new IndexServlet(index, m_usersExtractor);

//           break;

//         case typed:

//           servlet = new IndexServlet
//             (index,
//              new Extractor<DMARequest, Iterator<AbstractEntry>>()
//              {
//                public Iterator<AbstractEntry> get(DMARequest inRequest)
//                {
//                  return (Iterator<AbstractEntry>)
//                    BaseCampaign.GLOBAL.iterator(index.getType());
//                }
//              });
//           break;

//         default:
//           assert false : "should never come here";
//       }

//       m_rootContext.addServlet(new ServletHolder(servlet), path);
//     }

//     // search
//     m_rootContext.addServlet
//       (new ServletHolder
//        (new IndexServlet
//         (new Index<Index>("", "Search", "/search/", false, true)
//           {
//             public boolean matchesName(String inName, AbstractEntry inEntry)
//             {
//               if(inName == null || inEntry == null)
//                 return false;

//               return inEntry.matches(inName);
//             }

//             public Set<Object> buildNames(Iterator<AbstractEntry> inData)
//             {
//               return new HashSet<Object>();
//             }

//           }, m_dmExtractor)),
//          /*
//          new Extractor<DMARequest, Iterator<AbstractEntry>>()
//           {
//             @SuppressWarnings("unchecked") // casting of generic array
//             public Iterator<AbstractEntry> get(DMARequest inRequest)
//             {
//               ArrayList<Iterator<AbstractEntry>> iterators =
//                 new ArrayList<Iterator<AbstractEntry>>();

//               BaseCharacter user = inRequest.getUser();

//               if(user != null)
//                 iterators.add(user.getProductData().getAbstractEntries());

//               iterators.add(m_campaigns.getAbstractEntries());
//               iterators.add(BaseCampaign.GLOBAL.getAbstractEntries());

//               return new MultiIterator<AbstractEntry>
//                 ((Iterator<AbstractEntry> [])iterators.toArray
//                  (new Iterator [0]));
//             }
//             }
//           )), */
//        "/search/*");

//     // administration
//     m_rootContext.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.ADMIN)),
//        "/admin/*", 0);
//     m_rootContext.addServlet
//       (new ServletHolder(new AdminServlet()), "/admin/*");

    // actions
    m_rootContext.addServlet
      (new ServletHolder(new SaveActionServlet(m_users)), "/actions/save");

    m_rootContext.addServlet
      (new ServletHolder(new LoginServlet
                         (m_users.getEntries(BaseCharacter.TYPE))),
       "/actions/login");

    m_rootContext.addServlet
      (new ServletHolder(new LogoutServlet
                         (m_users.getEntries(BaseCharacter.TYPE))),
       "/actions/logout");

//     m_rootContext.addServlet
//       (new ServletHolder(new SelectCharacterServlet(m_campaigns)),
//        "/actions/select");

//     m_rootContext.addServlet
//       (new ServletHolder(new MoveServlet(m_campaigns)),
//        "/actions/move");

//     m_rootContext.addServlet
//       (new ServletHolder(new MailServlet(m_campaigns)),
//        "/actions/mail");

//     m_rootContext.addServlet
//       (new ServletHolder(new ReloadServlet(m_campaigns)),
//        "/actions/reload");

//     // ajax data requests
//     m_rootContext.addServlet
//       (new ServletHolder
//        (new AccessorServlet
//         (new AccessorServlet.Accessor()
//           {
//             public String getData(DMARequest inRequest)
//             {
//              if(inRequest == null)
//               throw new IllegalArgumentException("must have a reqiest here");

//               StringBuffer result = new StringBuffer();

//               result.append("[");
//               for(Iterator<String> i =
//                     BaseProduct.getAllPersons
//                     (inRequest.getURLParam("category")).iterator();
//                   i.hasNext(); )
//                 result.append("'" + Encodings.encodeHTMLAttribute
//                               (HTMLDocument.simpleConvert(i.next()))
//                               + (i.hasNext() ? "'," : "'"));

//               result.append("]");

//               return result.toString();
//             }
//           })), "/ajax/persons");

//     m_rootContext.addServlet
//       (new ServletHolder
//        (new AccessorServlet
//         (new AccessorServlet.Accessor()
//           {
//             public String getData(DMARequest inRequest)
//             {
//               if(inRequest == null)
//               throw new IllegalArgumentException("must have a request here");

//               StringBuffer result = new StringBuffer();

//               result.append("[");
//               for(Iterator<String> i =
//                     BaseProduct.getAllJobs
//                     (inRequest.getURLParam("category")).iterator();
//                   i.hasNext(); )
//                 result.append("'" + Encodings.encodeHTMLAttribute
//                               (HTMLDocument.simpleConvert(i.next()))
//                               + (i.hasNext() ? "'," : "'"));

//               result.append("]");

//               return result.toString();
//             }
//           })), "/ajax/jobs");

//     m_rootContext.addServlet
//       (new ServletHolder
//        (new AccessorServlet
//         (new AccessorServlet.Accessor()
//           {
//             public String getData(DMARequest inRequest)
//             {
//               if(inRequest == null)
//               throw new IllegalArgumentException("must have a request here");

//               StringBuffer result = new StringBuffer();

//               result.append("[");
//               for(Iterator<String> i =
//                     BaseProduct.getRequirements
//                     (inRequest.getURLParam("world"),
//                      inRequest.getURLParam("system")).iterator();
//                   i.hasNext(); )
//                 result.append("'" + Encodings.encodeHTMLAttribute
//                               (HTMLDocument.simpleConvert(i.next()))
//                               + (i.hasNext() ? "'," : "'"));

//               result.append("]");

//               return result.toString();
//             }
//           })), "/ajax/requirements");

//     m_rootContext.addServlet
//       (new ServletHolder
//        (new AccessorServlet
//         (new AccessorServlet.Accessor()
//           {
//             public String getData(DMARequest inRequest)
//             {
//               StringBuffer result = new StringBuffer();

//               result.append("[");
//               for(Iterator<String> i =
//                     BaseProduct.getAllReferences().iterator(); i.hasNext(); )
//                 result.append("'" + Encodings.encodeHTMLAttribute
//                               (HTMLDocument.simpleConvert(i.next()))
//                               + (i.hasNext() ? "'," : "'"));

//               result.append("]");

//               return result.toString();
//             }
//           })), "/ajax/references");

//     m_rootContext.addServlet
//       (new ServletHolder
//        (new AccessorServlet
//         (new AccessorServlet.Accessor()
//           {
//             public String getData(DMARequest inRequest)
//             {
//               if(inRequest == null)
//               throw new IllegalArgumentException("must have a request here");

//               StringBuffer result = new StringBuffer();

//               result.append("[");
//               for(Iterator<String> i =
//                     BaseProduct.getProducts
//                   (inRequest.getURLParam("world")).iterator(); i.hasNext(); )
//                 result.append("'" + Encodings.encodeHTMLAttribute
//                               (HTMLDocument.simpleConvert(i.next()))
//                               + (i.hasNext() ? "'," : "'"));

//               result.append("]");

//               return result.toString();
//             }
//           })), "/ajax/products");

//     m_rootContext.addServlet
//       (new ServletHolder
//        (new AccessorServlet
//         (new AccessorServlet.Accessor()
//           {
//             public String getData(DMARequest inRequest)
//             {
//               StringBuffer result = new StringBuffer();

//               result.append("[");
//               for(Iterator<BaseEntry> i = BaseCampaign.GLOBAL.iterator();
//                   i.hasNext(); )
//               {
//                 BaseEntry entry = i.next();

//                 if(!(entry instanceof BaseQuality))
//                   continue;

//                 result.append("'" + Encodings.encodeHTMLAttribute
//                               (HTMLDocument.simpleConvert(entry.getName()))
//                               + (i.hasNext() ? "'," : "'"));
//               }

//               result.append("]");

//               return result.toString();
//             }
//           })), "/ajax/qualities");
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
    Log.info("Loading user information");
    if(!m_users.read())
      Log.error("Could not properly read user files!");
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
//   public static class Test extends net.ixitxachitls.util.test.TestCase
//   {
//   }

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
       new CommandLineParser.StringOption
       ("d", "dma-files",
        "The directory from which to include all dma files.", ""),
       new CommandLineParser.StringOption
       ("b", "base-campaigns",
        "The file containing the base campaigns.", "BaseCampaigns.dma"),
       new CommandLineParser.StringOption
       ("c", "campaigns",
        "The file containing the campaigns.", "Campaigns.dma"),
       new CommandLineParser.StringOption
       ("u", "users",
        "The file containing the base characters (users).",
        "Ixitxachitls.dma"),
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
      Config.set("web.analytics", false);
    else
      Config.set("web.analytics", true);

    //......................................................................

    int    port = clp.get("port", Config.get("resource:web/port", 5555));
    String name = clp.get("name", Config.get("resource:web/name", "localhost"));
    String base = clp.get("base-campaigns",
                          Config.get("resource:web/base.campaigns",
                                     "BaseCampaigns.dma"));
    String campaigns = clp.get("campaigns", Config.get("resource:web/campaigns",
                                                       "Campaigns.dma"));
    String users = clp.get("users", Config.get("resource:web/users",
                                               "Ixitxachitls.dma"));

    Log.info(s_version);

    DMAServer server = new DMAServer(name, port, base, campaigns, users);
    server.start();
  }

  //........................................................................
}
