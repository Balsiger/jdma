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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

import net.ixitxachitls.dma.data.DMADatafiles;
import net.ixitxachitls.dma.data.DMADatastore;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.util.CommandLineParser;
import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.logging.ANSILogger;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A utility to export dma entries into the app engine data store.
 *
 * Useage:
 *
 * java net.ixitxachitls.dma.server.Exporter
 * -h jdmaixit.appspot.com -p 443 -u balsiger@ixitxachitls.net <dir>
 *
 * Exports base  characters from the datastore to file file.dma
 * (leave out host and port for local storage).
 *
 * @file          Exporter.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public final class Exporter
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Exporter -------------------------------

  /**
   * Prevent instantiation.
   *
   */
  private Exporter()
  {
    // nothing to do
  }

  //........................................................................


  //........................................................................

  //-------------------------------------------------------------- variables
  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //--------------------------------------------------------- main/debugging

  //--------------------------------- main ---------------------------------

  /**
   * Main routine for the exporter utility.
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
       new CommandLineParser.StringOption
       ("u", "username", "The username to connect with.",
        "balsiger@ixitxachitls.net"));

    String dir = clp.parse(inArguments);

    String password =
      new String(System.console().readPassword("password for "
                                               + clp.getString("username")
                                               + ": "));

    RemoteApiOptions options = new RemoteApiOptions()
      .server(clp.getString("host"), clp.getInteger("port"))
      .credentials(clp.getString("username"), password);

    RemoteApiInstaller installer = new RemoteApiInstaller();
    installer.install(options);

    // read the dma files
    DMADatafiles data = new DMADatafiles(dir + "/dma");

    // in order to actually have the proper types defined, we need to
    // reference them...
    // TODO: can we somehow get rid of that?
    AbstractType<? extends AbstractEntry> dummy =
      net.ixitxachitls.dma.entries.BaseCharacter.TYPE;

    try
    {
      DatastoreService store = DatastoreServiceFactory.getDatastoreService();
      ImagesService image = ImagesServiceFactory.getImagesService();
      DMADatastore dmaStore = new DMADatastore();
      Log.important("reading entities from datastore");

      Query query = new Query();
      for(Entity entity : store.prepare(query).asIterable
            (FetchOptions.Builder.withChunkSize(100)))
      {
        // ignore internal entities
        if(entity.getKind().startsWith("__"))
          continue;

        // write out blobs specially
        if("file".equals(entity.getKind()))
        {
          String id = entity.getParent().getName();
          String type = entity.getParent().getKind();
          String name = (String)entity.getProperty("name");
          String path = (String)entity.getProperty("path");
          String extension =
            Files.mimeExtension((String)entity.getProperty("type"));
          File blobDir = new File(Files.concatenate(dir, "blobs", type, id));
          if(!blobDir.exists())
            if(!blobDir.mkdirs())
              Log.warning("could not create directory " + blobDir);

          String file = Files.concatenate(dir, "blobs", type, id,
                                          name + "." + extension);

          for(int i = 1; i <= 5; i++)
          {
            FileOutputStream output = null;
            InputStream input = null;

            try
            {
              try
              {
                String url = image.getServingUrl(new BlobKey(path));

                URLConnection connection = new URL(url).openConnection();

                byte[] buffer = new byte[100 * 1024];

                output = new FileOutputStream(file);
                input = connection.getInputStream();

                for(int read = input.read(buffer); read > 0;
                    read = input.read(buffer))
                  output.write(buffer, 0, read);

                break;
              }
              catch(java.io.IOException e)
              {
                Log.error("Deadline exceeded when trying to download file "
                          + file + " (retrying " + i + "): " + e);
              }
              finally
              {
                if(input != null)
                  input.close();
              }
            }
            finally
            {
              if(output != null)
                output.close();
            }
          }

          Log.important("Wrote blob " + file);

          continue;
        }

        Log.important("converting entity " + entity.getKind() + ": "
                      + entity.getKey());
        AbstractEntry entry = dmaStore.convert(entity);

        if(entry == null)
        {
          Log.warning("could not convert " + entity);
          continue;
        }

        data.add(entry, false);
      }

      if(!data.save())
        Log.error("could not write data");
    }
    finally
    {
      installer.uninstall();
    }
  }

  //........................................................................

  //........................................................................
}
