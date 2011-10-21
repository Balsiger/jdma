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

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.data.DMADatafiles;
import net.ixitxachitls.dma.data.DMADatastore;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.util.CommandLineParser;
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
 * -t "base character" -h jdmaixit.appspot.com -p 443
 * -u balsiger@ixitxachitls.net
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
   */
  private Importer()
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
       new CommandLineParser.StringOption
       ("u", "username", "The username to connect with.",
        "balsiger@ixitxachitls.net"),
       new CommandLineParser.StringOption
       ("t", "type", "The type of entries to import.", ""));

    String files = clp.parse(inArguments);

    String password = new String(System.console().readPassword
                                 ("password for " + clp.getString("username")
                                  + ": "));

    RemoteApiOptions options = new RemoteApiOptions()
      .server(clp.getString("host"), clp.getInteger("port"))
      .credentials(clp.getString("username"), password);

    RemoteApiInstaller installer = new RemoteApiInstaller();
    installer.install(options);

    // read the dma files
    DMADatafiles data = new DMADatafiles("");
    for(String file : files.split(",\\s*"))
      data.addFile(file);

    if(!data.read())
    {
      Log.error("cannot properly read data file");
      return;
    }

    AbstractType<? extends AbstractEntry> type =
      AbstractType.get(clp.getString("type"));

    if(type == null)
    {
      Log.error("cannot find type for " + clp.getString("type"));
      return;
    }

    try
    {
      DatastoreService store = DatastoreServiceFactory.getDatastoreService();
      DMADatastore dmaStore = new DMADatastore();

      List<Entity> entities = new ArrayList<Entity>();
      for(AbstractEntry entry : data.getEntriesList(type))
      {
        entities.add(dmaStore.convert(entry));
        Log.important("importing " + type + " " + entry.getName());
      }

      Log.important("storing entities in datastore");
      store.put(entities);
    }
    finally
    {
      installer.uninstall();
    }
  }

  //........................................................................

  //........................................................................
}
