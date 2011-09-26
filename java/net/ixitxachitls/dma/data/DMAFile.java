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

package net.ixitxachitls.dma.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.values.Comment;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.util.Files;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.errors.BaseError;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This file encapsulates all the entries that were read from a single file.
 *
 * @file          DMAFile.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

// TODO: check commeted out code
public class DMAFile //implements Storage<AbstractEntry>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- DMAFile --------------------------------

  /**
   * Create the file, this will also read all the entries from the given file.
   *
   * @param       inName      the filename of the file to read
   * @param       inPath      the path to the file
   * @param       inBaseData  all the avaialble base data
   *
   */
  public DMAFile(@Nonnull String inName, @Nonnull String inPath,
                 @Nonnull DMAData inBaseData)
  {
    m_name = inName;
    m_path = inPath;
    m_data = inBaseData;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The filename of the file read. */
  private @Nonnull String m_name;

  /** The base path to the file read. */
  private @Nonnull String m_path;

  /** All the available data. */
  private @Nonnull DMAData m_data;

  /** The file level comment (if any). */
  private @Nonnull Comment m_comment = new Comment(1, -1);

  /** The list of entries read from the file. */
  private List<AbstractEntry> m_entries = new ArrayList<AbstractEntry>();

  /** The errors encountered when parsing. */
  private List<BaseError> m_errors = new ArrayList<BaseError>();

  /** The number of lines read. */
  private long m_lines = 0;

  /** Flag if the file was changed or not. */
  private boolean m_changed = false;

  /** Flag if a file was successfully read or not. */
  private boolean m_read = false;

  /** Flag to singal if this file can be stored or not. */
  private boolean m_readOnly = false;

  /** The path for the backup files. */
  private static final @Nonnull String s_backupPath =
    Config.get("resource:web/dir.backup", "dma/Backups");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //---------------------------- getStorageName ----------------------------

  /**
   * Get the name of the file.
   *
   * @return      the name of the file (without 'unnecessary path' info)
   *
   */
  public @Nonnull String getStorageName()
  {
    return m_name;
  }

  //........................................................................
  //------------------------------ getLines --------------------------------

  /**
   * Get the number of lines read in the file.
   *
   * @return      the number of lines read
   *
   */
  public synchronized long getLines()
  {
    return m_lines;
  }

  //........................................................................
  //------------------------------ getEntries ------------------------------

  /**
   * Get the entries currently in the file. The list is immutable but is backed
   * by the real contents of the file (e.g. is updated when an entry is added).
   *
   * @return      an immutable list with all the entries
   *
   */
  public @Nonnull List<AbstractEntry> getEntries()
  {
    return Collections.unmodifiableList(m_entries);
  }

  //........................................................................
  //------------------------------- getData --------------------------------

  /**
   * Get the data repository this file belongs to.
   *
   * @return    the data repository
   *
   */
  public @Nonnull DMAData getData()
  {
    return m_data;
  }

  //........................................................................

  //----------------------------- getCampaign ------------------------------

  /**
   * Get the campaign storing the data for the file.
   *
   * @return      the campaign data
   *
   * @undefined   never
   *
   */
//   public CampaignData getCampaign()
//   {
//     return m_campaign;
//   }

  //........................................................................
  //----------------------------- getStorageID -----------------------------

  /**
   * Get the id of the storage or null if none (usually if not an value group).
   *
   * @return      the id of the storage or null
   *
   */
//   public String getStorageID()
//   {
//     return null;
//   }

  //........................................................................

  //------------------------------ isChanged -------------------------------

  /**
   * Check if the file has been changed (and thus might need saving).
   *
   * @return      true if changed, false if not
   *
   */
  public boolean isChanged()
  {
    return m_changed;

//     if(m_changed)
//       return true;

//     for(AbstractEntry entry : m_entries)
//       if(entry.isChanged())
//         return true;

//     return false;
  }

  //........................................................................
  //------------------------------- wasRead --------------------------------

  /**
   * Check if the file was actually successfully read or not.
   *
   * @return      true if read, false if not
   *
   */
  public boolean wasRead()
  {
    return m_read;
  }

  //........................................................................

  //-------------------------------- format --------------------------------

  /**
   * Format the value for printing.
   *
   * @return      the command that can be printed
   *
   */
  public Command format()
  {
    return new Link(getStorageName(), "/index/files/" + getStorageName());
  }

  //........................................................................
  //------------------------------- toString -------------------------------

  /**
   * Create a human readable string representation of the object.
   *
   * @return      the object as a human readable string
   *
   */
  public String toString()
  {
    return m_path + "/" + m_name + ": " + m_entries
      + " (" + m_lines + " lines, "
      + (m_changed ? "changed" : "not changed")
      + (m_read ? ", read" : ", not read")
      + ")";
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- read ---------------------------------

  /**
   * Read a dma file into the campaign.
   *
   * @param       inBaseData all the available base data
   *
   * @return      true if read without error, false else
   *
   */
  public synchronized boolean read(@Nonnull DMAData inBaseData)
  {
    try
    {
      String name = Files.concatenate(m_path, m_name);

      return read(inBaseData, new BufferedReader(new FileReader(name)));
    }
    catch(java.io.FileNotFoundException e)
    {
      Log.warning("cannot find file '" + Files.concatenate(m_path, m_name)
                  + "'");

      return false;
    }
  }

  //........................................................................
  //--------------------------------- read ---------------------------------

  /**
   * Read a dma file into the campaign.
   *
   * @param       inBaseData all the available base data
   * @param       inReader the reader to read from
   *
   * @return      true if read without error, false else
   *
   */
  protected synchronized boolean read(@Nonnull DMAData inBaseData,
                                      @Nonnull Reader inReader)
  {
    ParseReader reader = new ParseReader(inReader, m_name);

    Log.info("reading file '" + m_name + "'");

    // read the top level comment
    Comment comment = m_comment.read(reader);

    if(comment == null || !comment.isDefined())
      m_comment = m_comment.as("# file: " + m_name + "\n\n");
    else
      m_comment = comment;

    // read all the entries
    while(!reader.isAtEnd())
    {
      //ParseReader.Position start = reader.getPosition();
      AbstractEntry entry = AbstractEntry.read(reader, inBaseData);
      //ParseReader.Position end = reader.getPosition();

      if(entry == null)
        break;

      add(entry, false);

      // if an entry is changed while reading, the file is changed as well
      if(entry.isChanged())
        changed();

      // add a stream error, if there was an error or warning
      if(reader.hadWarning() || reader.hadError())
      {
        m_errors.add(new BaseError("parse.error",
                                   "encountered a parse error"));

        // mark the file as read-only if we have errors
        m_readOnly = true;
      }

      Log.status("read " + entry.getName());
    }

    m_lines = reader.getLineNumber();
    Log.info("file '" + m_name + "' read");

    // close the file
    reader.close();

    // check if we have to save any files because values were completed
    if(isChanged())
      write();

    return true;
  }

  //........................................................................
  //-------------------------------- write ---------------------------------

  /**
   * Write the file and all its contents.
   *
   * @return      true if successfully written, false if not
   *
   */
  public synchronized boolean write()
  {
    if(!m_changed)
      return true;

    if(m_readOnly)
    {
      Log.event("*system*", "Save", "Readonly " + m_name);
      return false;
    }

    // backup the old file
    String renamed = Files.backup(Files.concatenate(m_path, m_name),
                                  s_backupPath);

    if(renamed != null)
      Log.info("backed up file '" + m_name + "' to '" + renamed + "'");
    else
      Log.warning("could not backup file '" + Files.concatenate(m_path, m_name)
                  + "' to " + s_backupPath);

    // now the path is clear to write the file
    // open the output file
    FileWriter writer = null;
    boolean result;
    try
    {
      writer = new FileWriter(Files.concatenate(m_path, m_name));
      result = write(writer);
      Log.event("*system*", "Save", "Written file " + m_name);
    }
    catch(java.io.IOException e)
    {
      Log.warning("could not write to file '" + m_name + "': " + e);

      return false;
    }
    finally
    {
      try
      {
        writer.close();
      }
      catch(java.io.IOException e)
      {
        Log.warning("cannot close writer for '" + m_name + "': " + e);
      }
    }

    return result;
  }

  //........................................................................
  //-------------------------------- write ---------------------------------

  /**
   * Write the file and all its contents.
   *
   * @param       inWriter the writer to write to
   *
   * @return      true if successfully written, false if not
   *
   * @throws      java.io.IOException if writing failed
   *
   */
  protected synchronized boolean write(@Nonnull Writer inWriter)
    throws java.io.IOException
  {
    if(m_comment.isDefined())
      inWriter.write(m_comment.toString());

    for(AbstractEntry entry : m_entries)
    {
      // only write entries that have us as storage (e.g. only top
      // level entries)
//       if(entry.getStorage() != this)
//         continue;

      Log.status("write " + entry.getName());

      inWriter.write(entry.toString());
      entry.changed(false);
    }

    Log.info("wrote file '" + m_name + "'");

    m_changed = false;

    return true;
  }

  //........................................................................

  //--------------------------------- add ----------------------------------

  /**
   * Add the given entry to the file. New entries will mark the file as
   * changed.
   *
   * @param       inEntry the entry to add
   * @param       inNew   true if this is a new entry, false if not
   *
   * @return      true if added, false if not
   *
   */
  protected boolean add(@Nonnull AbstractEntry inEntry, boolean inNew)
  {
    inEntry.addTo(this);

    // add to this file
    m_entries.add(inEntry);

    if(inNew || inEntry.isChanged())
      m_changed = true;

    return true;
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the given entry to the file as a new entry. This will _NOT_ mark the
   * file as changed.
   *
   * @param       inEntry the entry to add
   *
   * @return      true if added, false if not
   *
   */
  public boolean add(@Nonnull AbstractEntry inEntry)
  {
    return add(inEntry, true);
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add an entry to the storage.
   *
   * @param       inEntry the entry to add
   * @param       inAfter the entry to add after or null to add at the beginning
   *
   * @return      true if added, false if not
   *
   */
//   public boolean add(AbstractEntry inEntry, AbstractEntry inAfter)
//   {
//     throw new UnsupportedOperationException("not available");
//   }

  //........................................................................
  //-------------------------------- remove --------------------------------

  /**
   *
   * Remove the entry from the file.
   *
   * @param       inEntry the entry to remove
   *
   * @return      true if removed, false if not in file
   *
   */
  public boolean remove(@Nonnull AbstractEntry inEntry)
  {
    boolean removed = m_entries.remove(inEntry);

    if(removed)
    {
//       inEntry.store(null, -1, -1, -1, -1);

      m_changed = true;
    }

    return removed;
  }

  //........................................................................
  //------------------------------- changed --------------------------------

  /**
   * Set the state of the file to changed.
   *
   */
  public void changed()
  {
    m_changed = true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Test init of a file. */
    @org.junit.Test
    public void init()
    {
      DMAFile file = new DMAFile("test.dma", "build/test", new DMAData("path"));

      assertEquals("name", "test.dma", file.getStorageName());
      assertEquals("lines", 0, file.getLines());
      assertFalse("changed", file.isChanged());
      assertFalse("read", file.wasRead());
      assertEquals("format", "\\link[/index/files/test.dma]{test.dma}",
                   file.format().toString());
      assertEquals("string",
                   "build/test/test.dma: [] (0 lines, not changed, not read)",
                   file.toString());
    }

    //......................................................................
    //----- read/write -----------------------------------------------------

    /**
     * Test reading.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void readWrite() throws Exception
    {
      // mock the file to read from
      java.io.StringReader reader =
        new java.io.StringReader("# A test file\n\n"
                                 + "base entry test. base entry test2.");

      // mock the file to read to
      java.io.StringWriter writer = new java.io.StringWriter();

      // start the test
      DMAFile file = new DMAFile("read.dma", "path", new DMAData("path"));

      assertTrue("read", file.read(new DMAData("path"), reader));
      synchronized(file)
      {
        assertEquals("comment", "# A test file\n\n", file.m_comment.toString());
      }
      assertEquals("entry 1",
                   "#----- test\n"
                   + "\n"
                   + "base entry test =\n"
                   + "\n"
                   + ".\n"
                   + "\n"
                   + "#.....\n",
                   file.m_entries.get(0).toString());
      assertEquals("entry 2",
                   "#----- test2\n"
                   + "\n"
                   + "base entry test2 =\n"
                   + "\n"
                   + ".\n"
                   + "\n"
                   + "#.....\n",
                   file.m_entries.get(1).toString());
      assertFalse("changed", file.isChanged());
      assertTrue("write", file.write(writer));
      assertEquals("wrote",
                   "# A test file\n"
                   + "\n"
                   + "#----- test\n"
                   + "\n"
                   + "base entry test =\n"
                   + "\n"
                   + ".\n"
                   + "\n"
                   + "#.....\n"
                   + "#----- test2\n"
                   + "\n"
                   + "base entry test2 =\n"
                   + "\n"
                   + ".\n"
                   + "\n"
                   + "#.....\n",
                   writer.toString());


      // add an entry
      writer = new java.io.StringWriter();
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry("guru", new DMAData("path"));
      file.add(entry);

      assertTrue("changed", file.isChanged());
      assertTrue("write", file.write(writer));
      assertEquals("wrote",
                   "# A test file\n"
                   + "\n"
                   + "#----- test\n"
                   + "\n"
                   + "base entry test =\n"
                   + "\n"
                   + ".\n"
                   + "\n"
                   + "#.....\n"
                   + "#----- test2\n"
                   + "\n"
                   + "base entry test2 =\n"
                   + "\n"
                   + ".\n"
                   + "\n"
                   + "#.....\n"
                   + "#----- guru\n"
                   + "\n"
                   + "base entry guru =\n"
                   + "\n"
                   + ".\n"
                   + "\n"
                   + "#.....\n",
                   writer.toString());

      assertFalse("changed", file.isChanged());

      writer = new java.io.StringWriter();
      assertTrue("remove", file.remove(entry));
      assertTrue("changed", file.isChanged());
      assertTrue("write", file.write(writer));
      assertEquals("wrote",
                   "# A test file\n"
                   + "\n"
                   + "#----- test\n"
                   + "\n"
                   + "base entry test =\n"
                   + "\n"
                   + ".\n"
                   + "\n"
                   + "#.....\n"
                   + "#----- test2\n"
                   + "\n"
                   + "base entry test2 =\n"
                   + "\n"
                   + ".\n"
                   + "\n"
                   + "#.....\n",
                   writer.toString());

      m_logger.addExpected("WARNING: base base entry 'guru' not found");
    }

    //......................................................................
  }

  //........................................................................
}
