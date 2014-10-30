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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import com.google.common.base.Charsets;
import com.google.common.io.ByteSink;
import com.google.common.io.CharSink;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADatastore;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.util.CommandLineParser;
import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.logging.ANSILogger;
import net.ixitxachitls.util.logging.Log;

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
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@ParametersAreNonnullByDefault
public final class Exporter
{
  /**
   * Prevent instantiation.
   */
  private Exporter()
  {
  }

  /** The datastore service. */
  DatastoreService m_store = DatastoreServiceFactory.getDatastoreService();

  /** The image service. */
  ImagesService m_image = ImagesServiceFactory.getImagesService();

  /** The DMA data store. */
  DMADatastore m_dmaStore = new DMADatastore();


  static
  {
    net.ixitxachitls.dma.server.servlets.DMARequest.ensureTypes();
  }

  /**
   * Export all entries of the given type.
   *
   * @param inType        the type of entry to export
   * @param inID          the id of the entry to get or empty for all matching
   *                      entries
   * @param inDir         the base directory to export into
   * @param inBlobs       whether to write blobs related to an etnry
   *
   * @throws IOException  thrown when writing fails
   */
  public void export(String inType, String inID, String inDir, boolean inBlobs)
    throws IOException
  {
    Log.important("reading entities from datastore");

    Query query;
    if(inType.isEmpty())
      query = new Query();
    else
      query = new Query(inType);

    if(!inID.isEmpty())
      query.setFilter(new Query.FilterPredicate
                      ("__key__", Query.FilterOperator.EQUAL,
                       KeyFactory.createKey(inType, inID.toLowerCase())));

    for(Entity entity : m_store.prepare(query).asIterable
          (FetchOptions.Builder.withChunkSize(1000)))
    {
      // ignore internal entities
      if(entity.getKind().startsWith("__"))
        continue;

      // ignore blobs (written from entities)
      if("file".equals(entity.getKind()))
        continue;

      Log.important("converting entity " + entity.getKind() + ": "
                    + entity.getKey());
      AbstractEntry entry = m_dmaStore.convert(entity);

      if(entry == null)
      {
        Log.warning("could not convert " + entity);
        continue;
      }

      export(entry, inDir, inBlobs);
    }
  }

  /**
   * Export the given entry.
   *
   * @param inEntry the entry to export
   * @param inRoot  the base direactory to export to
   * @param inBlobs whether to export blobs related to an entry
   *
   * @throws IOException if writing fails
   */
  private void export(AbstractEntry inEntry, String inRoot, boolean inBlobs)
    throws IOException
  {
    String name = Files.encodeName(inEntry.getName());
    String dir = Files.concatenate(inRoot,
                                   inEntry.getPath().replaceAll("/[^/]+$", ""));
    Files.ensureDir(dir);
    Message proto = inEntry.toProto();

    Log.important("Writing " + inEntry.getType() + " " + name);
    ByteSink bytes = com.google.common.io.Files.asByteSink
      (new File(Files.concatenate(dir, name + ".pb")));
    try
    {
      bytes.write(proto.toByteArray());
    }
    catch(IOException e)
    {
      Log.warning("Cannot write binary proto " + name + ": " + e);
    }

    CharSink chars = com.google.common.io.Files.asCharSink
      (new File(Files.concatenate(dir, name + ".ascii")), Charsets.UTF_8);
    try
    {
      chars.write(proto.toString());
    }
    catch(IOException e)
    {
      Log.warning("Cannot write ascii proto " + name + ": " + e);
    }

    // Export any files associated with the entry.
    if(inBlobs)
      for(net.ixitxachitls.dma.values.File file : inEntry.getFiles())
        export(file, name, dir);
  }

  /**
   * Export a blob.
   *
   * @param inFile the file structure describing the blob
   * @param inName the name of the entry this blob is for
   * @param inDir  the directory to export to
   *
   * @thwos IOException if writing fails
   */
  private void export(net.ixitxachitls.dma.values.File inFile, String inName,
                      String inDir)
    throws IOException
  {
    String extension = Files.mimeExtension(inFile.getType());
    String path = Files.concatenate(inDir, inName + " - " + inFile.getName()
                                    + "." + extension);

    for(int i = 1; i <= 5; i++)
    {
      FileOutputStream output = null;
      InputStream input = null;

      try
      {
        try
        {
          String url =
            m_image.getServingUrl(ServingUrlOptions.Builder.withBlobKey
                                (new BlobKey(inFile.getPath()
                                             .replaceAll("^.*/", ""))));

          URLConnection connection = new URL(url).openConnection();

          byte[] buffer = new byte[100 * 1024];

          output = new FileOutputStream(path);
          input = connection.getInputStream();

          for(int read = input.read(buffer); read > 0;
              read = input.read(buffer))
            output.write(buffer, 0, read);

          break;
        }
        catch(java.io.IOException e)
        {
          Log.error("Deadline exceeded when trying to download file "
                    + inFile + " (retrying " + i + "): " + e);
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

    Log.important("Wrote blob " + path);
  }

  /**
   * Main routine for the exporter utility.
   *
   * @param    inArguments the command line arguments
   *
   * @throws   Exception too lazy to handle
   */
  public static void main(String []inArguments) throws Exception
  {
    Log.setLevel(Log.Type.INFO);
    Log.add("import", new ANSILogger());

    CommandLineParser clp =
      new CommandLineParser
      (new CommandLineParser.StringOption
       ("h", "host", "The host to connect to.", "localhost"),
       new CommandLineParser.StringOption
       ("t", "type", "The type of entries to export (or file for blobs)", ""),
       new CommandLineParser.IntegerOption
       ("p", "port", "The port to connect to.", 8888),
       new CommandLineParser.StringOption
       ("u", "username", "The username to connect with.",
        "balsiger@ixitxachitls.net"),
       new CommandLineParser.StringOption
       ("i", "id", "The id of the entry to get.", ""),
       new CommandLineParser.Flag
       ("n", "nopassword", "Connect without a password."),
       new CommandLineParser.Flag
       ("b", "blobs", "Store the blobs associated with entries."));

    List<String> dirs = clp.parse(inArguments);

    if(dirs.size() != 1)
    {
      System.err.println("Must have a single output directory");
      return;
    }

    String password = "";
    if(!clp.hasValue("nopassword"))
      password =
        new String(System.console().readPassword
                   ("password for " + clp.getString("username") + ": "));

    RemoteApiOptions options = new RemoteApiOptions()
      .server(clp.getString("host"), clp.getInteger("port"))
      .credentials(clp.getString("username"), password);

    RemoteApiInstaller installer = new RemoteApiInstaller();
    installer.install(options);

    try
    {
      SystemProperty.environment.set
          (SystemProperty.Environment.Value.Development);
      Exporter exporter = new Exporter();
      exporter.export(clp.getString("type"), clp.getString("id"), dirs.get(0),
                      clp.hasValue("blobs"));
    }
    finally
    {
      installer.uninstall();
    }
  }
}
