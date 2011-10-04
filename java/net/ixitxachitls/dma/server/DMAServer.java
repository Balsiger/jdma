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

import java.util.EnumSet;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.servlet.DispatcherType;

import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.RewriteRegexRule;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.BaseProduct;
import net.ixitxachitls.dma.entries.BaseType;
import net.ixitxachitls.dma.server.filters.DMAFilter;
import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.dma.server.servlets.EntryListServlet;
import net.ixitxachitls.dma.server.servlets.EntryPDFServlet;
import net.ixitxachitls.dma.server.servlets.EntryServlet;
import net.ixitxachitls.dma.server.servlets.IndexServlet;
import net.ixitxachitls.dma.server.servlets.JobAutocomplete;
import net.ixitxachitls.dma.server.servlets.LibraryServlet;
import net.ixitxachitls.dma.server.servlets.LoginServlet;
import net.ixitxachitls.dma.server.servlets.LogoutServlet;
import net.ixitxachitls.dma.server.servlets.PersonAutocomplete;
import net.ixitxachitls.dma.server.servlets.ProductsAutocomplete;
import net.ixitxachitls.dma.server.servlets.SaveActionServlet;
import net.ixitxachitls.dma.server.servlets.StaticPageServlet;
import net.ixitxachitls.server.WebServer;
import net.ixitxachitls.server.servlets.FileServlet;
import net.ixitxachitls.server.servlets.TemplateServlet;
import net.ixitxachitls.util.CommandLineParser;
//import net.ixitxachitls.util.Files;
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
 *   /user/<id>                 specific base characters
 *   /user/<id>/products        all a users products
 *   /user/<id>/product/<id>    a user's products
 *   /users                     all base characters
 *   /<short-type>/<id>         specific base entries
 *                              internally redirected  to /entry/<type>/<id>
 *   /<multi-type>              index entries of given type
 *                              internally redirected to /entries/<type>
 *   /<multi-type>/<index>      additional index for the given type
 *                              internally redirected to /index/<index>
 *   /campaign/<id>             campaign information
 *   /campaign/<id>/<type>/<id> campaign specific entry
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
    * @param       inBaseDirs  the name of the base directories to read from
    * @param       inCampaigns the name of the file containing campaign info
    *
    */
  public DMAServer(@Nonnull String inHost, int inPort,
                   @Nonnull String inBaseDirs,
                   @Nonnull String inCampaigns)
  {
    super(inHost, inPort);

    // determine which base files to read
    m_baseData = new DMAData(DATA_DIR);
    System.out.println(inBaseDirs);
    for(String baseDir : inBaseDirs.split(",\\s*"))
      m_baseData.addAllFiles(baseDir);

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

  /** The user information. */
  private @Nonnull DMAData m_baseData;

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

    RewriteHandler handler = new RewriteHandler();
    handler.setRewriteRequestURI(true);
    handler.setRewritePathInfo(true);
    handler.setOriginalPathAttribute(DMARequest.ORIGINAL_PATH);

    WebAppContext context = new WebAppContext(handler, "build/war", "/");
    context.setParentLoaderPriority(true);
    m_server.setHandler(handler);

    addRewrite(handler, "^(.+)\\.pdf", "/pdf$1");
    for(AbstractType<? extends AbstractEntry> type : AbstractType.getAll())
    {
      if(type instanceof BaseType)
      {
        addRewrite(handler, "^(|pdf)/" + type.getLink() + "/([^/]+)$",
                   "$1/_entry/" + type.getName() + "/$2");
        addRewrite(handler, "^/" + type.getMultipleLink() + "/?",
                   "/_entries/" + type.getName());
        addRewrite(handler, "^/" + type.getMultipleLink() + "/(.+)",
                   "/_index/" + type.getName() + "/$1");
      }
      else
      {
        addRewrite(handler, "/user/([^/]+)/" + type.getName() + "/(.+)",
                   "/_entry/user/$1/" + type.getName() + "/$2");
        addRewrite(handler, "/user/([^/]+)/" + type.getMultipleLink(),
                   "/_entries/user/$1/" + type.getName());
      }
    }

    // campaign information
    addRewrite(handler, "/campaign/([^/]+)/(.*)",
               "/_entry/base campaign/$1/campaign/$2");

    // TODO: this is temporary, remove once the main page filter is in
    addRewrite(handler, "/", "/index.html");

    context.addFilter
      (new FilterHolder
       (new DMAFilter(m_baseData.getEntries(BaseCharacter.TYPE))), "/*",
       EnumSet.of(DispatcherType.REQUEST));

//     context.setAttribute("users", m_users);
//     context.setAttribute("campaigns", m_campaigns);

//     context.addFilter(new FilterHolder(new MainPageFilter()),
//                             "/", 0);


    // static files
    context.addServlet
      (new ServletHolder(new TemplateServlet("/css", "text/css",
                                             "web/css/template")),
       "/css/*");

    context.addServlet
      (new ServletHolder(new FileServlet("/js", "text/javascript")), "/js/*");

    context.addServlet
      (new ServletHolder(new FileServlet("/icons", "image/png")), "/icons/*");

    context.addServlet
      (new ServletHolder(new FileServlet("/text/robots.txt", "text/plain")),
       "/robots.txt");

    context.addServlet
      (new ServletHolder(new FileServlet("/icons/favicon.png", "image/png")),
       "/favicon.ico");

    context.addServlet
      (new ServletHolder(new StaticPageServlet("/html")), "/*");

    context.addServlet
      (new ServletHolder(new FileServlet("/files", "")), "/files/*");

//     context.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.USER)),
//        "/files-internal/*", 0);
//     context.addServlet
//       (new ServletHolder(new StaticFileServlet("/files-internal", "")),
//        "/files-internal/*");

    // base entries
    context.addServlet
      (new ServletHolder(new EntryServlet(m_baseData)
                         .withAccess(BaseCharacter.Group.USER)),
        "/_entry/*");
    context.addServlet
      (new ServletHolder(new EntryPDFServlet(m_baseData)
                         .withAccess(BaseCharacter.Group.USER)),
       "/pdf/_entry/*");
    context.addServlet
      (new ServletHolder(new EntryListServlet(m_baseData)), "/_entries/*");

    context.addServlet(new ServletHolder(new LibraryServlet(m_baseData)),
                       "/library");

//     // products
//     for(BaseEntry entry : m_users)
//       if(entry instanceof BaseCharacter)
//         context.addServlet
//           (new ServletHolder
//            (new EntryServlet(((BaseCharacter)entry).getProductData())),
//            "/user/" + entry.getName() + "/*");

//     // campaigns
//     context.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.PLAYER)),
//        "/campaign/*", 0);

//   context.addServlet(new ServletHolder(new EntryServlet(m_campaigns)),
//                              "/campaign/*");

//     // entries
//     for(Entry entry : m_campaigns)
//       if(entry instanceof Campaign)
//         context.addServlet
//           (new ServletHolder(new EntryServlet((Campaign)entry)),
//            "/campaign/" + entry.getID() + "/*");

//     // entry specific stuff
//     context.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.USER)),
//        "/index/products/*", 0);
//     context.addServlet
//       (new ServletHolder(new ProductServlet(m_users)),
//        "/index/products/*");

//     context.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.USER)),
//        "/index/products/*", 0);
//     context.addServlet
//       (new ServletHolder(new ProductListServlet(m_users)),
//        "/pdf/products/list/*");

//     context.addServlet
//       (new ServletHolder(new ProductShelfListServlet(m_users)),
//        "/pdf/products/shelf/*");

//     context.addServlet
//       (new ServletHolder(new ProductMissingListServlet(m_users)),
//        "/pdf/products/missing/*");

//     context.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.PLAYER)),
//        "/pdf/items/*", 0);
//     context.addServlet
//       (new ServletHolder(new ItemListServlet(m_campaigns, false)),
//        "/pdf/items/*");

//     context.addServlet
//       (new ServletHolder(new ItemListServlet(m_campaigns, true)),
//        "/pdf/dm/items/*");

//     context.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.DM)),
//        "/pdf/dm/encounters/*", 0);
//     context.addServlet
//       (new ServletHolder(new EncounterListServlet(m_campaigns)),
//        "/pdf/dm/encounters/*");

//     context.addServlet
//       (new ServletHolder(new ImageServlet(m_campaigns)), "/images/*");

//     // user pages
//     context.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.USER)),
//        "/overview", 0);
//     context.addServlet
//       (new ServletHolder(new UserOverviewServlet(m_campaigns)), "/overview");

    // indexes
    context.addServlet(new ServletHolder(new IndexServlet(m_baseData)),
                       "/_index/*");
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

//       context.addServlet(new ServletHolder(servlet), path);
//     }

//     // search
//     context.addServlet
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
//     context.addFilter
//       (new FilterHolder(new AccessFilter(BaseCharacter.Group.ADMIN)),
//        "/admin/*", 0);
//     context.addServlet
//       (new ServletHolder(new AdminServlet()), "/admin/*");

    // actions
    context.addServlet
      (new ServletHolder(new SaveActionServlet(m_baseData)), "/actions/save/*");

    context.addServlet
      (new ServletHolder(new LoginServlet
                         (m_baseData.getEntries(BaseCharacter.TYPE))),
       "/actions/login");

    context.addServlet
      (new ServletHolder(new LogoutServlet
                         (m_baseData.getEntries(BaseCharacter.TYPE))),
       "/actions/logout");

//     context.addServlet
//       (new ServletHolder(new SelectCharacterServlet(m_campaigns)),
//        "/actions/select");

//     context.addServlet
//       (new ServletHolder(new MoveServlet(m_campaigns)),
//        "/actions/move");

//     context.addServlet
//       (new ServletHolder(new MailServlet(m_campaigns)),
//        "/actions/mail");

//     context.addServlet
//       (new ServletHolder(new ReloadServlet(m_campaigns)),
//        "/actions/reload");

    // autocomplete requests
    context.addServlet
      (new ServletHolder
       (new PersonAutocomplete(m_baseData.getEntries(BaseProduct.TYPE))),
       "/autocomplete/persons/*");
    context.addServlet
      (new ServletHolder
       (new JobAutocomplete(m_baseData.getEntries(BaseProduct.TYPE))),
       "/autocomplete/jobs/*");
    context.addServlet
      (new ServletHolder
       (new ProductsAutocomplete(m_baseData.getEntries(BaseProduct.TYPE))),
       "/autocomplete/products/*");

//     // ajax data requests
//     context.addServlet
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

//     context.addServlet
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

//     context.addServlet
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

//     context.addServlet
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

//     context.addServlet
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

//     context.addServlet
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
   * server will serve requests but will return an error message about starting
   * up. This means this can take some time.
   *
   */
  @OverridingMethodsMustInvokeSuper
  public void init()
  {
    Log.info("Loading user information");
    if(!m_baseData.read())
      Log.error("Could not properly base data files!");
  }

  //........................................................................
  //------------------------------ addRewrite ------------------------------

  /**
   * Add a rewrite rule to the given handler.
   *
   * @param       inHandler     the handler to add the rule to
   * @param       inPattern     the path pattern to rewrite
   * @param       inReplacement the replacement to rewrite to
   *
   */
  public void addRewrite(@Nonnull RewriteHandler inHandler,
                         @Nonnull String inPattern,
                         @Nonnull String inReplacement)
  {
    Log.debug("Adding rewrite rule " + inPattern + " -> " + inReplacement);
    RewriteRegexRule rewrite = new RewriteRegexRule();
    rewrite.setRegex(inPattern);
    rewrite.setReplacement(inReplacement);
    inHandler.addRule(rewrite);
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
       ("d", "dma-directory",
        "The directory from which to include all dma files.", ""),
       new CommandLineParser.StringOption
       ("b", "base-directories",
        "Comma separated list of the directories with base files.",
        Config.get("entries/base.dirs",
                   "BaseProducts, BaseProducts/DnD, BaseProducts/Novels, "
                   + "BaseProducts/Magazines, BaseCharacters, BaseCampaigns")),
       // base characters need base products
       new CommandLineParser.StringOption
       ("c", "campaigns",
        "The file containing the campaigns.", "Campaigns.dma"),
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

    int    port = clp.get("port", Config.get("web/port", 5555));
    String name = clp.get("name", Config.get("web/name", "localhost"));
    String base = clp.get("base-directories", "");
    String campaigns = clp.get("campaigns", Config.get("web/campaigns",
                                                       "Campaigns.dma"));

    Log.info(s_version);

    DMAServer server = new DMAServer(name, port, base, campaigns);
    server.start();
  }

  //........................................................................
}
