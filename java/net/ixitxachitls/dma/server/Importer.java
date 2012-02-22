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
import java.util.List;

import javax.annotation.Nonnull;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

import net.ixitxachitls.dma.data.DMADatafiles;
import net.ixitxachitls.dma.data.DMADatastore;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.Entry;
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

public final class Importer
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Importer -------------------------------

  /**
   * Prevent instantiation.
   *
   * @param   inHost     the host to connect to
   * @param   inPort     the port to use for the remove api
   * @param   inWebPort  the port to use for web access
   * @param   inUserName the username to connect to the remote api
   * @param   inPassword the password to connect to the remote api
   * @param   inMain     if true, treal all images imported as main images
   *
   * @throws IOException unable to install remove api
   *
   */
  public Importer(@Nonnull String inHost, int inPort, int inWebPort,
                  @Nonnull String inUserName, @Nonnull String inPassword,
                  boolean inMain)
    throws IOException
  {
    m_host = inHost;
    m_webPort = inWebPort;
    m_mainImages = inMain;

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
  private @Nonnull RemoteApiInstaller m_installer;

  /** A list of all files to import. */
  private List<String> m_files = new ArrayList<String>();

  /** The dma data parsed. */
  private DMADatafiles m_data = new DMADatafiles("./");

  /** The hostname to connect to. */
  private @Nonnull String m_host;

  /** The port of the web application. */
  private int m_webPort;

  /** If true, all images read as treated as main images. */
  private boolean m_mainImages;

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
  public void add(@Nonnull String inFile)
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
      addFile(inFile);
  }

  //........................................................................
  //------------------------------- addFile --------------------------------

  /**
   * Add the given file for importing.
   *
   * @param       inFile the file to import
   *
   */
  public void addFile(@Nonnull String inFile)
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

    List<Entity> entities = new ArrayList<Entity>();

    for(AbstractType<? extends AbstractEntry> type : m_data.getTypes())
      for(AbstractEntry entry : m_data.getEntries(type, null, 0, 0))
      {
        Entity entity;
        if(entry instanceof Entry
           && (entry.getName() == null || entry.getName().isEmpty()))
        {
          do
          {
            ((Entry)entry).randomID();
            Log.debug("Creating a new random id " + entry.getName());
            entity = dmaStore.convert(entry);
          } while(dmaStore.getEntity(entity.getKey()) != null);
        }
        else
          entity = dmaStore.convert(entry);

        entities.add(entity);
        Log.important("importing " + type + " " + entry.getName());
      }

    Log.important("storing entities in datastore");
    m_data.save();
    store.put(entities);

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
      AbstractType<? extends AbstractEntry> entry =
        AbstractType.getTyped(parts[parts.length - 3]);
      String id = parts[parts.length - 2].replace("\\ ", " ");
      String name = Files.file(parts[parts.length - 1]);

      // check if this is the main image
      if(m_mainImages || id.equalsIgnoreCase(name) || name.contains(id)
         || "cover".equalsIgnoreCase(name)
         || "official".equalsIgnoreCase(name)
         || "unofficial".equalsIgnoreCase(name)
         || "main".equalsIgnoreCase(name))
        name = "main";

      Log.important("importing image " + name + " with type " + type
                    + " and entry " + entry + " with id " + id);

      URL url = new URL("http", m_host, m_webPort,
                        "/__import"
                        + "?type=" + Encodings.urlEncode(type)
                        + "&name=" + Encodings.urlEncode(name)
                        + "&entry=" + Encodings.urlEncode(entry.toString())
                        + "&id=" + Encodings.urlEncode(id));
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
                                               (connection.getInputStream()));
        try
        {
          String line = rd.readLine();
          if(!"OK".equals(line))
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
        "balsiger@ixitxachitls.net"));

    String files = clp.parse(inArguments);
    String password = new String(System.console().readPassword
                                 ("password for " + clp.getString("username")
                                  + ": "));

    Importer importer =
      new Importer(clp.getString("host"), clp.getInteger("port"),
                   clp.getInteger("webport"), clp.getString("username"),
                   password, clp.hasValue("main"));

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
