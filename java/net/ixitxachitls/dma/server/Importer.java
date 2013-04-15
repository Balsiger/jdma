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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

import net.ixitxachitls.dma.data.DMADatafiles;
import net.ixitxachitls.dma.data.DMADatastore;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.entries.Item;
import net.ixitxachitls.dma.entries.extensions.Composite;
import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.dma.server.servlets.DMAServlet;
import net.ixitxachitls.util.CommandLineParser;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.logging.ANSILogger;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A utility to import dma entries into the app engine data store.
 *
 * Useage:
 *
 * java net.ixitxachitls.dma.server.Importer dma/BaseCharacters/Ixitxachitls.dma
 * -h jdmaixit.appspot.com -p 443 -w 80 -u balsiger@ixitxachitls.net
 *
 * Adds base characters from the Ixitxachitls.dma file to the cloud store
 * (leave out host and port for local storage).
 *
 * @file          Importer.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public final class Importer
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Importer -------------------------------

  /**
   * Prevent instantiation.
   *
   * @param   inHost       the host to connect to
   * @param   inPort       the port to use for the remove api
   * @param   inWebPort    the port to use for web access
   * @param   inUserName   the username to connect to the remote api
   * @param   inPassword   the password to connect to the remote api
   * @param   inMain       if true, treat all images imported as main images
   * @param   inIndividual if true, store each entry after reading instead of
   *                       in batch (slower and more expensive, but can
   *                       properly find bases)
   *
   * @throws IOException unable to install remove api
   *
   */
  public Importer(String inHost, int inPort, int inWebPort,
                  String inUserName, String inPassword, boolean inMain,
                  boolean inIndividual)
    throws IOException
  {
    m_host = inHost;
    m_webPort = inWebPort;
    m_mainImages = inMain;
    m_individual = inIndividual;

    RemoteApiOptions options = new RemoteApiOptions()
      .server(inHost, inPort)
      .credentials(inUserName, inPassword);

    m_installer = new RemoteApiInstaller();
    m_installer.install(options);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The remove api installer. */
  private RemoteApiInstaller m_installer;

  /** A list of all files to import. */
  private List<String> m_files = new ArrayList<String>();

  /** The dma data parsed. */
  private DMADatafiles m_data = new DMADatafiles("./");

  /** The hostname to connect to. */
  private String m_host;

  /** The port of the web application. */
  private int m_webPort;

  /** If true, all images read as treated as main images. */
  private boolean m_mainImages;

  /** If true, read and store each entry individually (no batch). */
  private boolean m_individual;

  /** Joiner for paths. */
  public static final Joiner PATH_JOINER = Joiner.on('/').skipNulls();

  //........................................................................

  //-------------------------------------------------------------- accessors

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------ uninstall -------------------------------

  /**
   * Uninstall the remove api.
   *
   */
  public void uninstall()
  {
    m_installer.uninstall();
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the given file or directory for import.
   *
   * @param       inFile the file or directory to import
   *
   */
  public void add(String inFile)
  {
    File file = new File(inFile);
    if(file.isDirectory())
    {
      if("CVS".equals(file.getName()) || file.getName().startsWith("."))
        return;

      for(File entry : file.listFiles())
      {
        if(entry.getName().startsWith("."))
          continue;

        if(entry.getName().contains("_thumbnail."))
          continue;

        add(Files.concatenate(inFile, entry.getName()));
      }
    }
    else
    {
      if(file.getName().endsWith("~"))
        return;

      addFile(inFile);
    }
  }

  //........................................................................
  //------------------------------- addFile --------------------------------

  /**
   * Add the given file for importing.
   *
   * @param       inFile the file to import
   *
   */
  public void addFile(String inFile)
  {
    Log.important("adding file " + inFile);

    if(inFile.endsWith(".dma"))
      m_data.addFile(inFile);
    else
      m_files.add(inFile);
  }

  //........................................................................
  //--------------------------------- read ---------------------------------

  /**
   * Do the import of all the files.
   *
   * @throws IOException reading or writing failed
   *
   */
  public void read() throws IOException
  {
    if(!m_data.read())
    {
      Log.error("cannot properly read data file");
      return;
    }

    DatastoreService store = DatastoreServiceFactory.getDatastoreService();
    DMADatastore dmaStore = new DMADatastore();
    DMARequest.ensureTypes();

    List<Entity> entities = new ArrayList<Entity>();
    List<AbstractEntry> errors = new ArrayList<AbstractEntry>();

    for(AbstractType<? extends AbstractEntry> type : m_data.getTypes())
      for(AbstractEntry entry : m_data.getEntries(type, null, 0, 0))
      {
        if(!entry.ensureBaseEntries())
        {
          errors.add(entry);
          continue;
        }

        if(entry instanceof Entry)
          complete((Entry)entry);

        if(m_individual)
          store.put(dmaStore.convert(entry));
        else
          entities.add(dmaStore.convert(entry));
        Log.important("importing " + type + " " + entry.getName());
      }

    Log.important("storing entities in datastore");
    if(!entities.isEmpty())
      store.put(entities);

    int last = 0;
    while(last != errors.size())
    {
      last = errors.size();
      for(Iterator<AbstractEntry> i = errors.iterator(); i.hasNext(); )
      {
        AbstractEntry entry = i.next();
        if(!entry.ensureBaseEntries())
        {
          System.out.println("setting back " + entry.getName());
          continue;
        }

        if(entry instanceof Entry)
          complete((Entry)entry);

        entities.add(dmaStore.convert(entry));
        Log.important("importing after error " + entry.getName());

        i.remove();
      }

      Log.important("storing entities in datastore");
      store.put(entities);
    }

    if(!errors.isEmpty())
    {
      List<String> names = new ArrayList<String>();

      for(AbstractEntry entry : errors)
        names.add(entry.getName());

      Log.error("Could not properly read all entries: " + names);
    }

    m_data.save();

    Log.important("importing images");

    FileNameMap types = URLConnection.getFileNameMap();

    for(String image : m_files)
    {
      // Windows requires special handling...
      String []parts = image.split(File.separatorChar == '\\' ? "\\\\"
                                   : File.separator);
      if(parts.length < 3)
      {
        Log.warning("ignoring invalid file " + image + " "
                    + Arrays.toString(parts));
        continue;
      }

      String type = types.getContentTypeFor(image);
      String name = Files.file(parts[parts.length - 1]);
      parts[parts.length - 1] = null;
      AbstractEntry.EntryKey<?> key =
        DMAServlet.extractKey(PATH_JOINER.join(parts));

      if(key == null)
      {
        Log.warning("invalid key for " + image + ", ignored");
        continue;
      }

      // check if this is the main image
      if(m_mainImages || key.getID().equalsIgnoreCase(name)
         || name.contains(key.getID())
         || "cover".equalsIgnoreCase(name)
         || "official".equalsIgnoreCase(name)
         || "unofficial".equalsIgnoreCase(name)
         || "main".equalsIgnoreCase(name))
        name = "main";

      Log.important("importing image " + name + " with type " + type + " for "
                    + key);

      URL url = new URL("http", m_host, m_webPort,
                        "/__import"
                        + "?type=" + Encodings.urlEncode(type)
                        + "&name=" + Encodings.urlEncode(name)
                        + "&key=" + Encodings.urlEncode(key.toString()));
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");
      connection.connect();
      OutputStream output = connection.getOutputStream();

      FileInputStream input =
        new FileInputStream(image.replace("\\ ", " "));

      try
      {
        byte []buffer = new byte[1024 * 100];
        for(int read = input.read(buffer); read > 0; read = input.read(buffer))
          output.write(buffer, 0, read);

        output.flush();

        // Get the response
        BufferedReader rd = new BufferedReader(new InputStreamReader
                                               (connection.getInputStream(),
                                               Charsets.UTF_8));
        try
        {
          String line = rd.readLine();
          if(line != null && !"OK".equals(line) && !line.isEmpty())
          {
            Log.error("Server returned an error:");
            for(; line != null; line = rd.readLine())
              Log.error(line);
          }

          input.close();
          output.close();
        }
        finally
        {
          rd.close();
        }
      }
      finally
      {
        input.close();
      }
    }
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and do all necessary housekeeping.
   *
   * @param   inEntry the entry to complete
   */
  private void complete(Entry<?> inEntry)
  {
    inEntry.complete();

    // if the entry has a composite, we could have added some new items, which
    // we must properly store
    if(inEntry instanceof Item)
    {
      Composite composite = (Composite)inEntry.getExtension("composite");
      if(composite != null)
      {
        String file = m_data.getFilename(inEntry);

        if(file == null)
          Log.error("cannot properly store composites because "
                    + inEntry.getName() + " is not found in any file!");
        else
          for(Item item : composite.getIncludes())
            if(!m_data.hasEntry(item.getName(), Item.TYPE))
              m_data.add(item, file, false);
      }
    }
  }

  //........................................................................


  //........................................................................

  //--------------------------------------------------------- main/debugging

  //--------------------------------- main ---------------------------------

  /**
   * Main routine for the importer utility.
   *
   * @param    inArguments the command line arguments
   *
   * @throws   Exception too lazy to handle
   *
   */
  public static void main(String []inArguments) throws Exception
  {
    Log.setLevel(Log.Type.INFO);
    Log.add("import", new ANSILogger());

    CommandLineParser clp =
      new CommandLineParser
      (new CommandLineParser.StringOption
       ("h", "host", "The host to connect to.", "localhost"),
       new CommandLineParser.IntegerOption
       ("p", "port", "The port to connect to.", 8888),
       new CommandLineParser.IntegerOption
       ("w", "webport", "The web port to connect to.", 8888),
       new CommandLineParser.Flag
       ("m", "main", "Treat all images as main images."),
       new CommandLineParser.StringOption
       ("u", "username", "The username to connect with.",
        "balsiger@ixitxachitls.net"),
       new CommandLineParser.Flag
       ("i", "individual", "Individually store entries."),
       new CommandLineParser.Flag
       ("n", "nopassword", "Connect without a password."));

    String files = clp.parse(inArguments);
    String password = "";
    if(!clp.hasValue("nopassword"))
      password = new String(System.console().readPassword
                            ("password for " + clp.getString("username")
                             + ": "));

    Importer importer =
      new Importer(clp.getString("host"), clp.getInteger("port"),
                   clp.getInteger("webport"), clp.getString("username"),
                   password, clp.hasValue("main"), clp.hasValue("individual"));

    try
    {
      for(String file : files.split("(?<!\\\\)\\s+"))
        importer.add(file.replace("\\ ", " "));

      importer.read();
    }
    catch(Exception e)
    {
      Log.error("Random error: " + e.toString());
      e.printStackTrace();
    }
    finally
    {
      importer.uninstall();
    }
  }

  //........................................................................

  //........................................................................
}
