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

package net.ixitxachitls.dma.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;

import net.ixitxachitls.dma.data.DMADatastore;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
import net.ixitxachitls.dma.entries.Campaign;
import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.entries.Product;
import net.ixitxachitls.dma.proto.Entries;
import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.util.CommandLineParser;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.ANSILogger;
import net.ixitxachitls.util.logging.Log;
import net.ixitxachitls.util.resources.Resource;

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
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */
public final class Importer
{
  /**
   * Create the importer.
   *
   * @param   inHost       the host to connect to
   * @param   inWebHost    the host to connect to via web
   * @param   inPort       the port to use for the remove api
   * @param   inWebPort    the port to use for web access
   * @param   inDevCredentials if true, use development credentials
   * @param   inMain       if true, treat all images imported as main images
   * @param   inIndividual if true, store each entry after reading instead of
   *                       in batch (slower and more expensive, but can
   *                       properly find bases)
   * @param   inBlobs      if true, import blobs alongside entries
   * @param   inASCII      if true, import ascii protos
   *
   * @throws IOException unable to install remove api
   *
   */
  public Importer(String inHost, String inWebHost, int inPort, int inWebPort,
                  boolean inDevCredentials, boolean inMain,
                  boolean inIndividual, boolean inBlobs, boolean inASCII,
                  String inType)
    throws IOException
  {
    m_host = inHost;
    m_webHost = inWebHost;
    m_webPort = inWebPort;
    m_mainImages = inMain;
    m_individual = inIndividual;
    m_blobs = inBlobs;
    m_ascii = inASCII;

    RemoteApiOptions options = new RemoteApiOptions()
      .server(inHost, inPort);
    if(inDevCredentials)
      options.useDevelopmentServerCredential();
    else
      options.useApplicationDefaultCredential();

    m_installer = new RemoteApiInstaller();
    m_installer.install(options);

    DMARequest.ensureTypes();
    m_type = AbstractType.getTyped(inType);
}

  /** The data store. */
  private DatastoreService m_store =
      DatastoreServiceFactory.getDatastoreService();

  /** THe dma data store. */
  private DMADatastore m_dmaStore = new DMADatastore();

  /** The remove api installer. */
  private RemoteApiInstaller m_installer;

  /** A list of all external files to import. */
  private Map<String, AbstractEntry> m_files = new HashMap<>();

  /** A list of proto buffer files to import. */
  private List<String> m_protoFiles = new ArrayList<>();

  /** The hostname to connect to. */
  private String m_host;

  /** THe host name for web connections. */
  private String m_webHost;

  /** The port of the web application. */
  private int m_webPort;

  /** If true, all images read as treated as main images. */
  private boolean m_mainImages;

  /** If true, read and store each entry individually (no batch). */
  private boolean m_individual;

  /** If true, import blobs of entries. */
  private boolean m_blobs;

  /** IF true, import ascii proto files. */
  private boolean m_ascii;

  /** The list of entities to store in batch. */
  private List<Entity> m_entities = new ArrayList<>();

  /** The list of entries with errors to store later. */
  private List<AbstractEntry> m_errors = new ArrayList<>();
  private final Optional<? extends AbstractType<? extends AbstractEntry>> m_type;

  /** Joiner for paths. */
  public static final Joiner PATH_JOINER = Joiner.on('/').skipNulls();

  /**
   * Uninstall the remove api.
   */
  public void uninstall()
  {
    m_installer.uninstall();
  }

  /**
   * Add the given file or directory for import.
   *
   * @param       inFile the file or directory to import
   */
  public void add(String inFile)
  {
    File file = new File(inFile);
    if(file.isDirectory())
      for(File entry : file.listFiles())
        add(entry.getPath());
    else
      if(file.getName().endsWith(m_ascii ? ".ascii" : ".pb"))
        addFile(inFile);
  }

  /**
   * Add the given file for importing.
   *
   * @param       inFile the file to import
   */
  public void addFile(String inFile)
  {
    Log.important("adding file " + inFile);
    m_protoFiles.add(inFile);
  }

  /**
   * Do the import of all the files.
   *
   * @throws IOException reading or writing failed
   */
  public void read() throws IOException
  {
    Collections.sort(m_protoFiles, new Comparator<String>() {

      @Override
      public int compare(String inFirst, String inSecond)
      {
        // Do base products first.
        boolean product1 = inFirst.contains("/product/");
        boolean product2 = inSecond.contains("/product/");
        if(product1 && !product2)
          return -1;
        if(!product1 && product2)
          return +1;

        if(product1 && product2)
        {
          boolean user1 = inFirst.contains("/user/");
          boolean user2 = inSecond.contains("/user/");
          if (user1 && !user2)
            return +1;
          if (!user1 && user2)
            return -1;
        }

        // Do campaign files last.
        boolean campaign1 = inFirst.contains("/campaign/");
        boolean campaign2 = inSecond.contains("/campaign/");
        if(campaign1 && !campaign2)
          return +1;
        if(!campaign1 && campaign2)
          return -1;

        // Do base campaigns before campaign entries.
        boolean base1 = inFirst.matches(".*/campaign/[^/]+");
        boolean base2 = inSecond.matches("/campaign/[^/]+");
        if(base1 && !base2)
          return -1;
        if(!base1 && base2)
          return +1;


        // Do campaigns before other entries.
        boolean main1 = inFirst.matches(".*/campaign/[^/]+/[^/]+");
        boolean main2 = inSecond.matches(".*/campaign/[^/]+/[^/]+");
        if(main1 && !main2)
          return -1;
        if(!main1 && main2)
          return +1;

        return inFirst.compareTo(inSecond);
      }
    });

    AbstractType lastType = null;
    for(String file : m_protoFiles)
    {
      Log.important("Processing file " + file);

      if(m_type.isPresent())
      {
        Entries.EntriesProto.Builder builder = Entries.EntriesProto.newBuilder();
        TextFormat.merge(new InputStreamReader(new FileInputStream(file)),
                         builder);
        for(Entries.BaseMiniatureProto proto : builder.getBaseMiniatureList())
          add(m_type.get().create().get(), proto);
      }
      else
      {
        Optional<? extends AbstractType<? extends AbstractEntry>> type;
        String[] parts = file.split("/");

        if(parts.length >= 3 && "campaign".equals(parts[parts.length - 3]))
          type = Optional.of(Campaign.TYPE);
        else if(parts.length >= 5 && "campaign".equals(parts[parts.length - 5]))
          type = AbstractType.getTyped(parts[parts.length - 2]);
        else if(parts.length >= 4 && "product".equals(parts[parts.length - 2])
            && "user".equals(parts[parts.length - 4]))
          type = Optional.of(Product.TYPE);
        else
          type = AbstractType.getTyped("base " + parts[parts.length - 2]);

        if(!type.isPresent())
          Log.warning("ignoring invalid type for " + file);
        else
        {
          if(lastType != type.get() && !m_entities.isEmpty())
          {
            lastType = type.get();
            Log.important("storing " + m_entities.size()
                              + " entities in datastore");
            m_store.put(m_entities);
            m_entities.clear();
          }

          final Optional<? extends AbstractEntry> entry =
              type.get().create("proto import");
          if(entry.isPresent())
            add(entry.get(),
                fill(entry.get().toProto().newBuilderForType(), file));
        }
      }
    }

    Log.important("storing entities in datastore");
    if(!m_entities.isEmpty())
      m_store.put(m_entities);

    m_entities.clear();
    int last = 0;
    while(last != m_errors.size())
    {
      last = m_errors.size();
      for(Iterator<AbstractEntry> i = m_errors.iterator(); i.hasNext(); )
      {
        AbstractEntry entry = i.next();
        if(!entry.ensureBaseEntries())
        {
          System.err.println("setting back " + entry.getName());
          continue;
        }

        if(entry instanceof Entry)
          complete((Entry)entry);

        m_entities.add(m_dmaStore.convert(entry));
        Log.important("importing after error " + entry.getName());

        i.remove();
      }

      Log.important("storing entities in datastore");
      m_store.put(m_entities);
      m_entities.clear();
    }

    if(!m_errors.isEmpty())
    {
      List<String> names = new ArrayList<String>();

      for(AbstractEntry entry : m_errors)
        names.add(entry.getName() + " (" + entry.getType() + ")");

      Log.error("Could not properly read all entries: " + names);
    }

    Log.important("importing images");

    for(Map.Entry<String, AbstractEntry> image : m_files.entrySet())
      importFile(image.getKey(), image.getValue());
  }

  /**
   * Fill the proto with the values of the named file.
   *
   * @param inProto   the proto to fill
   * @param inFile    the name of the file with the proto values
   * @return The built message read
   * @throws IOException            when reading fails
   */
  private Message fill(Message.Builder inProto, String inFile)
    throws IOException
  {
    if(m_ascii)
    {
      TextFormat.merge(new InputStreamReader(new FileInputStream(inFile)),
                       inProto);
      return inProto.build();
    }
    else
      return inProto.mergeFrom(new FileInputStream(inFile)).build();
  }

  /**
   * Import the named file.
   *
   * @param inName the name of the file to import
   * @param inEntry the entry to import to
   * @throws IOException if reading the file fails
   */
  private void importFile(String inName, AbstractEntry inEntry)
    throws IOException
  {
    String name = Strings.getPattern(inName, " - (.*)\\.*?$");

    // Windows requires special handling...
    String []parts = Files.decodeName(inName).split(File.separatorChar == '\\'
      ? "\\\\" : File.separator);

    if(parts.length < 3)
    {
      Log.warning("ignoring invalid file " + inName + " "
                  + Arrays.toString(parts));
      return;
    }

    FileNameMap types = URLConnection.getFileNameMap();
    String type = types.getContentTypeFor(parts[parts.length - 1]);
    EntryKey key = inEntry.getKey();

    if(key == null)
    {
      Log.warning("invalid key for " + inName + ", ignored");
      return;
    }

    // check if this is the main image
    if(m_mainImages || key.getID().equalsIgnoreCase(name)
       || name.contains(key.getID())
       || "cover".equalsIgnoreCase(name)
       || "official".equalsIgnoreCase(name)
       || "unofficial".equalsIgnoreCase(name)
       || "main".equalsIgnoreCase(name))
      name = "main";

    Log.important("importing image " + name + " with type " + inEntry.getType()
                  + " for " + key);

    URL url =
      new URL("http", m_webHost, m_webPort,
              "/__import"
                + "?type=" + Encodings.urlEncode(type)
                + "&name=" + Encodings.urlEncode(name)
                + "&key=" + Encodings.urlEncode(key.toString()));
    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
    connection.setDoOutput(true);
    connection.setRequestMethod("POST");
    connection.connect();

    try (OutputStream output = connection.getOutputStream();
      FileInputStream input =
        new FileInputStream(inName.replace("\\ ", " ")))
    {
      byte []buffer = new byte[1024 * 100];
      for(int read = input.read(buffer); read > 0; read = input.read(buffer))
        output.write(buffer, 0, read);

      output.flush();

      // Get the response
      try (BufferedReader rd =
        new BufferedReader(new InputStreamReader(connection.getInputStream(),
                                                 Charsets.UTF_8)))
      {
        String line = rd.readLine();
        if(line != null && !"OK".equals(line) && !line.isEmpty())
        {
          Log.error("Server returned an error:");
          for(; line != null; line = rd.readLine())
            Log.error(line);
        }
      }
    }
  }

  /**
   * Complete the entry and do all necessary housekeeping.
   *
   * @param   inEntry the entry to complete
   */
  private void complete(Entry inEntry)
  {
    inEntry.initialize(false);

    // if the entry has a composite, we could have added some new items, which
    // we must properly store
//    if(inEntry instanceof Item)
//    {
//      Composite composite = (Composite)inEntry.getExtension("composite");
//      if(composite != null)
//      {
//        String file = m_data.getFilename(inEntry);
//
//        if(file == null)
//          Log.error("cannot properly store composites because "
//                    + inEntry.getName() + " is not found in any file!");
//        else
//          for(Item item : composite.getIncludes())
//            if(!m_dmaStore.hasEntry(item.getName(), Item.TYPE))
//              m_dmaStore.add(item, file, false);
//      }
//    }
  }

  /**
   * Add the entry to import.
   *
   * @param inEntry the entry to import
   * @param inProto the proto representation of the entry
   */
  private void add(AbstractEntry inEntry, Message inProto)
  {
    inEntry.fromProto(inProto);

    if(!inEntry.ensureBaseEntries())
      m_errors.add(inEntry);
    else
    {
      if(inEntry instanceof Entry)
        complete((Entry)inEntry);

      if(m_individual)
        m_store.put(m_dmaStore.convert(inEntry));
      else
        m_entities.add(m_dmaStore.convert(inEntry));

      Log.important("importing " + inEntry.getType() + " " + inEntry.getName());
    }
  }

  /**
   * Main routine for the importer utility.
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
       ("v", "webhost", "The webhost to connect to.", "localhost"),
       new CommandLineParser.IntegerOption
       ("p", "port", "The port to connect to.", 8888),
       new CommandLineParser.IntegerOption
       ("w", "webport", "The web port to connect to.", 8888),
       new CommandLineParser.Flag
       ("m", "main", "Treat all images as main images."),
       new CommandLineParser.Flag
       ("i", "individual", "Individually store entries."),
       new CommandLineParser.Flag
       ("d", "dev", "User dev credentials."),
       new CommandLineParser.Flag
       ("a", "ascii", "Import ascii protos (default is binary)."),
       new CommandLineParser.Flag
       ("b", "blobs", "Import blobs associated with entries."),
       new CommandLineParser.StringOption(
           "t", "type", "The type of entries to read", ""));

    List<String> files = clp.parse(inArguments);

    SystemProperty.environment.set
        (SystemProperty.Environment.Value.Development);
    Importer importer =
      new Importer(clp.getString("host"), clp.getString("webhost"),
                   clp.getInteger("port"), clp.getInteger("webport"),
                   clp.hasValue("dev"), clp.hasValue("main"),
                   clp.hasValue("individual"),
                   clp.hasValue("blobs"), clp.hasValue("ascii"),
                   clp.getString("type"));

    try
    {
      for(String file : files)
        importer.add(file.replace("\\ ", " "));

      importer.read();
    }
    catch(Error e) // $codepro.audit.disable caughtExceptions
    {
      System.out.println(Arrays.toString(e.getSuppressed()));
      Log.error("Random error: " + e.toString());
      e.printStackTrace();
    }
    finally
    {
      importer.uninstall();
    }
  }
}
