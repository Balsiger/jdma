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

package net.ixitxachitls.dma.server.servlets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Multimap;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.CampaignEntry;
import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The servlet to save given entries.
 *
 * @file          SaveActionServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class SaveActionServlet extends ActionServlet
{
  //----------------------------------------------------------------- nested

  /**
   * Storage for the change information for one or multiple entries.
   */
  private static class Changes
  {
    /**
     * Create a change for one or multiple entries.
     *
     * @param inKey         the key to the entry
     * @param inOwner       the owner of the entry/entries
     *
     */
    public Changes(@Nonnull AbstractEntry.EntryKey inKey,
                   @Nonnull AbstractEntry inOwner)
    {
      m_key = inKey;
      m_owner = inOwner;
    }

    /** The key of the entry/entries changed. */
    protected @Nonnull AbstractEntry.EntryKey m_key;

    /** The owner of the entry/entries changed. */
    protected @Nullable AbstractEntry m_owner;

    /** The file for the entry/entries. */
    protected @Nullable String m_file;

    /** Flag if creating a new value or not. */
    protected boolean m_create = false;

    /** The extensions for the entry, if any. */
    protected @Nullable String []m_extensions;

    /** A flag if multiple entries are affected by the change. */
    protected boolean m_multiple = false;

    /** The path to store the created entry after creation, if any. */
    protected @Nullable String m_store;

    /** A map with all the changed values. */
    protected @Nonnull Map<String, String>m_values =
      new HashMap<String, String>();

    /**
     * Convert to a human readable string for debugging.
     *
     * @return the converted string
     *
     */
    @Override
    public @Nonnull String toString()
    {
      return m_key + " (" + (m_owner == null ? "no owner" : m_owner.getName())
        + "/" + m_file + (m_multiple ? ", multiple" : ", single") + "):"
        + m_values;
    }

    /**
     * Set a value in the change for a given key.
     *
     * @param inKey   the key of the value to change
     * @param inValue the value to change to
     *
     */
    public void set(@Nonnull String inKey, @Nonnull String inValue)
    {
      if("file".equals(inKey))
        m_file = inValue;
      else if("create".equals(inKey))
        m_create = true;
      else if("extensions".equals(inKey))
      {
        m_extensions = inValue.split("\\s*,\\s*");
        // also set it as a normal value for existing entries
        m_values.put(inKey, inValue);
      }
      else
        m_values.put(inKey, inValue);

      if(inKey.indexOf("=") >= 0)
        m_multiple = true;
    }

    /**
     * Set the path to store this entry after creation, if any.
     *
     * @param  inStore the name of the entry to store in
     */
    public void store(@Nullable String inStore)
    {
      if(inStore != null)
        m_store = inStore;
    }

    /**
     * Figure out the affected entries.
     *
     * @param ioErrors the errors encountered, will be adjusted
     *
     * @return a set with all the entries affected by this change
     *
     */
    @SuppressWarnings("unchecked")
    public @Nonnull Set<AbstractEntry> entries(@Nonnull List<String> ioErrors)
    {
      Set<AbstractEntry> entries = new HashSet<AbstractEntry>();
      if(m_multiple)
      {
        assert m_values.size() == 1 : "Only expected a single value to change";
        String []parts = m_values.keySet().iterator().next().split("/");
        String index = parts[0];
        String value = parts[1];
        entries.addAll(DMADataFactory.get().getIndexEntries(index,
                                                            m_key.getType(),
                                                            m_key.getParent(),
                                                            value, 0, 0));
      }
      else
      {
        AbstractEntry entry = DMADataFactory.get().getEntry(m_key);

        if(entry == null && m_create)
        {
          // create a new entry for filling out
          Log.event(m_owner.getName(), "create",
                    "creating " + m_key.getType() + " entry '" + m_key.getID()
                    + "'");

          entry = m_key.getType().create(m_key.getID());
          if(entry != null)
            entry.updateKey(m_key);

          // setting up extensions first
          if(m_extensions != null)
            for(String extension : m_extensions)
              entry.addExtension(extension);
        }

        if(entry == null)
        {
          String error = "could not find " + m_key + " for saving";
          Log.warning(error);
          ioErrors.add("gui.alert(" + Encodings.toJSString(error) + ");");
        }
        else
          entries.add(entry);
      }

      return entries;
    }
  }

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //--------------------------- SaveActionServlet --------------------------

  /**
   * Create the entry action servlet.
   */
  public SaveActionServlet()
  {
    // nothing to do
  }

  //......................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- doAction -------------------------------

  /**
   *
   * Execute the action associated with this servlet.
   *
   * @param       inRequest  the request for the page
   * @param       inResponse the response to write to
   *
   * @return      the javascript code to send back to the client
   *
   */
  @Override
  @SuppressWarnings("unchecked")
  protected @Nonnull String doAction(@Nonnull DMARequest inRequest,
                                     @Nonnull HttpServletResponse inResponse)
  {
    List<String> errors = new ArrayList<String>();
    Set<AbstractEntry> entries = new HashSet<AbstractEntry>();
    Map<String, CampaignEntry> stores = new HashMap<String, CampaignEntry>();
    for(Changes change : preprocess(inRequest, inRequest.getParams(), errors))
      for(AbstractEntry entry : change.entries(errors))
      {
        for(Map.Entry<String, String> keyValue : change.m_values.entrySet())
        {
          if(!entry.canEdit(keyValue.getKey(), inRequest.getUser()))
          {
            String error = "not allowed to edit " + keyValue.getKey() + " in "
              + change.m_key;
            Log.warning(error);
            errors.add("gui.alert(" + Encodings.toJSString(error) + ");");
            continue;
          }

          String rest = entry.set(keyValue.getKey(), keyValue.getValue());
          if(rest != null)
          {
            Log.warning("Could not fully parse " + keyValue.getKey()
                        + " value for " + change.m_key + ": '" + rest + "'");
            errors.add
              ("edit.unparsed("
               + Encodings.toJSString(change.m_key.getType().toString()) + ", "
               + Encodings.toJSString(change.m_key.getID()) + ", "
               + Encodings.toJSString(keyValue.getKey()) + ", "
               + Encodings.toJSString(rest) + ");");
          }
          else
          {
            entries.add(entry);

            if(change.m_store != null && entry instanceof CampaignEntry)
            {
              AbstractEntry.EntryKey<? extends AbstractEntry> key =
                extractKey(change.m_store);

              System.out.println(change.m_store + ": " + key);

              if(key == null)
                Log.warning("Cannot find entry for storage: " + change.m_store);
              else
              {
                CampaignEntry store =
                  (CampaignEntry)DMADataFactory.get().getEntry(key);
                if(store != null)
                  stores.put(entry.getName(), store);
              }
            }
          }
        }
      }

    List<String> saved = new ArrayList<String>();
    String path = "";

    // do we really have something to do?
    if(entries.size() <= 0)
      errors.add("gui.alert('No values to save');");
    else
    {
      // update all entries and mark them as saved
      for(AbstractEntry entry : entries)
      {
        // We have to get the store for the entry before saving, as saving can
        // change the name.
        CampaignEntry store = stores.get(entry.getName());
        if(entry.save())
        {
          saved.add(Encodings.escapeJS(entry.getType().toString()) + " "
                    + Encodings.escapeJS(entry.getName()));

          if(store != null && store.add((CampaignEntry)entry))
            path = Encodings.toJSString(store.getPath());
        }
        else
          errors.add("Coult not store " + entry.getType() + " '"
                     + entry.getName() + "'");
      }

      if(entries.size() == 1 && path.isEmpty())
        path = Encodings.toJSString(entries.iterator().next().getPath());
    }

    return
      (errors.isEmpty() ? "" : "gui.alert('Parse error for values');")
      + (saved.isEmpty() ? ""
         : "gui.info('The following entries were updated:<p>"
         + Strings.BR_JOINER.join(saved) + "'); "
         + "util.link(null" + (path.isEmpty() ? "" : ", "  + path + "")
         + ");")
      + Strings.NEWLINE_JOINER.join(errors);
  }

  //........................................................................
  //------------------------------ preprocess ------------------------------

  /**
   * Preprocess the request by collecting all changes that need to be made.
   *
   * @param       inRequest the original request for the page
   * @param       inParams  the params for the request
   * @param       ioErrors  the errors encountered
   *
   * @return      a collection of all changes requested
   *
   */
  private Collection<Changes> preprocess
    (@Nonnull DMARequest inRequest,
     @Nonnull Multimap<String, String> inParams,
     @Nonnull List<String> ioErrors)
  {
    Map<AbstractEntry.EntryKey<? extends AbstractEntry>, Changes> changes =
      new HashMap<AbstractEntry.EntryKey<? extends AbstractEntry>, Changes>();
    for(Map.Entry<String, String> param : inParams.entries())
    {
      String []parts = param.getKey().split("::");

      // not a real key value pair
      if(parts.length != 2)
        continue;

      String keyName = parts[0];
      String valueName = parts[1];

      // extract the storage info if there is one
      String storePath = null;
      String []store =
        Strings.getPatterns(keyName, "(.*/" + Entry.TEMPORARY + ")-(.+)");
      if(store.length == 2)
      {
        keyName = store[0];
        storePath = store[1];
      }

      AbstractEntry.EntryKey<? extends AbstractEntry> key = extractKey(keyName);
      if(key == null)
      {
        String error = "invalid entry '" + keyName + "' ignored";
        Log.warning(error);
        ioErrors.add("gui.alert(" + Encodings.toJSString(error) + ");");
        continue;
      }

      Changes change = changes.get(key);
      if(change == null)
      {
        change = new Changes(key, inRequest.getUser());
        changes.put(key, change);
      }

      if("name".equals(valueName)
         && param.getValue().startsWith(Entry.TEMPORARY))
        change.set("name", Entry.TEMPORARY);
      else
        change.set(valueName, param.getValue());
      change.store(storePath);
    }

    return changes.values();
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

    /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- save -----------------------------------------------------------

    /** The save Test. */
    @org.junit.Test
    public void save()
    {
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry("test");
      addEntry(entry);

      SaveActionServlet servlet = new SaveActionServlet();
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);
      com.google.common.collect.Multimap<String, String> params =
        com.google.common.collect.ImmutableMultimap.of
        ("/base entry/test::name", "guru");

      EasyMock.expect(user.getName()).andStubReturn("user");
      EasyMock.expect(request.getUser()).andStubReturn(user);
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(BaseCharacter.Group.ADMIN))
        .andStubReturn(true);
      EasyMock.expect(DMADataFactory.get().update(entry)).andReturn(true);

      EasyMock.replay(request, response, user);

      assertEquals("name", "test", entry.getName());

      assertEquals("result",
                   "gui.info('The following entries were updated:"
                   + "<p>base entry guru'); util.link(null, '/entry/guru');",
                   servlet.doAction(request, response));

      assertEquals("name", "guru", entry.getName());

      EasyMock.verify(request, response, user);
    }

    //......................................................................
    //----- no access ------------------------------------------------------

    /** The save Test. */
    @org.junit.Test
    public void noAccess()
    {
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry("test");
      addEntry(entry);

      SaveActionServlet servlet = new SaveActionServlet();
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);
      com.google.common.collect.Multimap<String, String> params =
        com.google.common.collect.ImmutableMultimap.of
        ("/base entry/test::name", "guru");

      EasyMock.expect(request.getUser()).andStubReturn(user);
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(BaseCharacter.Group.ADMIN))
        .andStubReturn(false);
      EasyMock.replay(request, response, user);

      assertEquals("name", "test", entry.getName());

      assertEquals("result",
                   "gui.alert('Parse error for values');"
                   + "gui.alert('not allowed to edit name in "
                   + "/base entry/test');\n"
                   + "gui.alert('No values to save');",
                   servlet.doAction(request, response));

      assertEquals("name", "test", entry.getName());

      m_logger.addExpected("WARNING: not allowed to edit name in "
                           + "/base entry/test");
      EasyMock.verify(request, response, user);
    }

    //......................................................................
    //----- invalid id -----------------------------------------------------

    /** The save Test. */
    @org.junit.Test
    public void invalidID()
    {
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry("test");
      addEntry(entry);

      SaveActionServlet servlet = new SaveActionServlet();
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);
      com.google.common.collect.Multimap<String, String> params =
        com.google.common.collect.ImmutableMultimap.of
        ("/base entry/guru::name", "guru");

      EasyMock.expect(request.getUser()).andStubReturn(user);
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(BaseCharacter.Group.ADMIN))
        .andStubReturn(true);
      EasyMock.expect(user.getName()).andStubReturn("Merlin");

      EasyMock.replay(request, response, user);

      assertEquals("name", "test", entry.getName());

      assertEquals("result",
                   "gui.alert('Parse error for values');"
                   + "gui.alert('could not find /base entry/guru for "
                   + "saving');\n"
                   + "gui.alert('No values to save');",
                   servlet.doAction(request, response));

      assertEquals("name", "test", entry.getName());

      m_logger.addExpected("WARNING: could not find /base entry/guru for "
                           + "saving");
      EasyMock.verify(request, response, user);
    }

    //......................................................................
    //----- invalid type ---------------------------------------------------

    /** The save Test. */
    @org.junit.Test
    public void invalidType()
    {
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry("test");
      addEntry(entry);

      SaveActionServlet servlet = new SaveActionServlet();
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);
      com.google.common.collect.Multimap<String, String> params =
        com.google.common.collect.ImmutableMultimap.of
        ("guru/test::name", "guru");

      EasyMock.expect(request.getUser()).andStubReturn(user);
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(BaseCharacter.Group.ADMIN))
        .andStubReturn(false);

      EasyMock.replay(request, response, user);

      assertEquals("name", "test", entry.getName());

      assertEquals("result",
                   "gui.alert('Parse error for values');"
                   + "gui.alert('invalid entry \\'guru/test\\' ignored');\n"
                   + "gui.alert('No values to save');",
                   servlet.doAction(request, response));

      assertEquals("name", "test", entry.getName());

      m_logger.addExpected("WARNING: invalid entry 'guru/test' ignored");
      EasyMock.verify(request, response, user);
    }

    //......................................................................
    //----- invalid key ----------------------------------------------------

    /** The save Test. */
    @org.junit.Test
    public void invalidKey()
    {
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry("test");

      SaveActionServlet servlet = new SaveActionServlet();
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);
      com.google.common.collect.Multimap<String, String> params =
        com.google.common.collect.ImmutableMultimap.of
        ("/base entry/test::guru", "guru",
         "/base entry/test::description", "\"test\"");

      EasyMock.expect(request.getUser()).andStubReturn(user);
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(BaseCharacter.Group.ADMIN))
        .andStubReturn(true);
      EasyMock.expect(DMADataFactory.get().update(entry)).andReturn(true);

      EasyMock.replay(request, response, user);

      assertEquals("name", "test", entry.getName());

      assertEquals("result",
                   "gui.alert('Parse error for values');"
                   + "gui.info('The following entries were updated:"
                   + "<p>base entry test'); "
                   + "util.link(null, '/entry/test');"
                   + "edit.unparsed('base entry', 'test', 'guru', 'guru');",
                   servlet.doAction(request, response));

      assertEquals("name", "test", entry.getName());

      m_logger.addExpected("WARNING: Could not fully parse guru value for "
                           + "/base entry/test: 'guru'");
      EasyMock.verify(request, response, user);
    }

    //......................................................................
    //----- collet changes -------------------------------------------------

    /** The changes Test. */
    @org.junit.Test
    public void collectChanges()
    {
      SaveActionServlet servlet = new SaveActionServlet();
      DMARequest request = EasyMock.createMock(DMARequest.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);

      EasyMock.expect(request.getUser()).andStubReturn(user);

      EasyMock.replay(request, user);

      Multimap<String, String> params =
        com.google.common.collect.ImmutableSetMultimap.<String, String>builder()
        .put("/base entry/id::name", "guru")
        .put("/base entry/id::description", "\"text\"")
        .put("/base entry/id2::name", "guru2")
        .put("/base entry/id2::file", "file")
        .put("/base entry/id2::worlds", "wolrd")
        .put("something/base entry/my-id::name", "name guru")
        .put("/character/me/base entry/id::key", "value")
        .put("/character/me/base entry/id::file", "file")
        .put("/character/me/base entry/id::key2", "value2")
        .put("/base entry/*=Person::key", "value")
        .put("/base entry/*=Something::key", "value")
        .build();
      List<String> errors = new ArrayList<String>();
      Collection<Changes> changes = servlet.preprocess(request, params, errors);

      java.util.Iterator<Changes> i = changes.iterator();
      checkChanges(i.next(), "/base entry/*=Something", null, "key", "value");
      checkChanges(i.next(), "/base entry/id", null,
                   "name", "guru", "description", "\"text\"");
      checkChanges(i.next(), "/base entry/id2", "file",
                   "name", "guru2", "worlds", "wolrd");
      checkChanges(i.next(), "/character/me/base entry/id", "file",
                   "key", "value", "key2", "value2");
      checkChanges(i.next(), "/base entry/my-id", null, "name", "name guru");
      checkChanges(i.next(), "/base entry/*=Person", null, "key", "value");
      assertFalse(i.hasNext());

      EasyMock.verify(request, user);
    }

    /** Check assertions for changes.
     *
     * @param inChanges   the changes to check
     * @param inKey       the expected key of the entry changed
     * @param inFile      the expected file for the entry changed
     * @param inKeyValues the expected key value pairs for the entry changed
     *
     */
    private void checkChanges(Changes inChanges, String inKey, String inFile,
                              String ... inKeyValues)
    {
      assertEquals("file", inFile, inChanges.m_file);
      assertEquals("key", inKey, inChanges.m_key.toString());

      assertEquals("number of values", inChanges.m_values.keySet().size(),
                   inKeyValues.length / 2);
      for(int i = 0; i < inKeyValues.length; i += 2)
        assertEquals(inKeyValues[i], inKeyValues[i + 1],
                     inChanges.m_values.get(inKeyValues[i]));
    }

    //......................................................................
  }

  //........................................................................
}
