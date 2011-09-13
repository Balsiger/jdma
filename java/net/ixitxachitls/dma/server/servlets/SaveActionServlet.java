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

import net.ixitxachitls.dma.data.DMAData;
// import net.ixitxachitls.dma.data.DMAFile;
// import net.ixitxachitls.dma.data.Storage;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
// import net.ixitxachitls.dma.entries.BaseCampaign;
import net.ixitxachitls.dma.entries.BaseCharacter;
// import net.ixitxachitls.dma.entries.BaseEntry;
// import net.ixitxachitls.dma.entries.Campaign;
// import net.ixitxachitls.dma.entries.Character;
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
  //--------------------------------------------------------- constructor(s)

  //--------------------------- SaveActionServlet --------------------------

  /**
   * Create the entry action servlet.
   *
   * @param       inData all the available data
   *
   */
  public SaveActionServlet(@Nonnull DMAData inData)
  {
    m_data = inData;
  }

  //......................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** All the available data. */
  protected @Nonnull DMAData m_data;

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
  @SuppressWarnings("unchecked")
  protected @Nonnull String doAction(@Nonnull DMARequest inRequest,
                                     @Nonnull HttpServletResponse inResponse)
  {
    Set<DMAData> datas = new HashSet<DMAData>();
    List<String> errors = new ArrayList<String>();
    Set<AbstractEntry> entries = new HashSet<AbstractEntry>();
    for(Changes change : preprocess(inRequest, inRequest.getParams(), errors))
      for(AbstractEntry entry : change.entries(errors))
      {
        for(Map.Entry<String, String> keyValue : change.m_values.entrySet())
        {
          if(!entry.canEdit(keyValue.getKey(), inRequest.getUser()))
          {
            String error = "not allowed to edit " + keyValue.getKey() + " in "
              + change.m_type + " " + entry.getID();
            Log.warning(error);
            errors.add("gui.alert(" + Encodings.toJSString(error) + ");");
            continue;
          }

          String rest = entry.set(keyValue.getKey(), keyValue.getValue());
          if(rest != null)
          {
            Log.warning("Could not fully parse " + keyValue.getKey()
                        + " value for " + change.m_type + " " + change.m_id
                        + ": '" + rest + "'");
            errors.add("edit.unparsed("
                       + Encodings.toJSString(change.m_fullType) + ", "
                       + Encodings.toJSString(change.m_id) + ", "
                       + Encodings.toJSString(keyValue.getKey()) + ", "
                       + Encodings.toJSString(rest) + ");");
          } else
          {
            entries.add(entry);
            datas.add(change.m_data);
          }
        }
      }

    List<String> saved = new ArrayList<String>();

    // do we really have something to do?
    String path = "";
    if(entries.size() <= 0)
      errors.add("gui.alert('No values to save');");
    else
    {
      boolean success = true;
      for(DMAData data : datas)
        success &= data.save();

      if(!success)
        errors.add("gui.alert('Could not save all changes');");
      else
      {
        // mark all entries as saved
        for(AbstractEntry entry : entries)
          saved.add(Encodings.escapeJS(entry.getType().toString()) + " "
                    + Encodings.escapeJS(entry.getName()));
      }

      if(entries.size() == 1)
        path = Encodings.toJSString(entries.iterator().next().getPath());
    }

    return
      (errors.isEmpty() ? "" : "gui.alert('Parse error for values');")
      + (saved.isEmpty() ? ""
         : "gui.info('The following entries were changed:<p>"
         + Strings.BR_JOINER.join(saved) + "'); "
         + "util.link(null, " + path + ");")
      + Strings.NEWLINE_JOINER.join(errors);
  }

  //........................................................................
  //------------------------------ preprocess ------------------------------

  /**
   *
   *
   * @param       inParams the parameters given
   *
   * @return
   *
   */
  private Collection<Changes> preprocess
    (@Nonnull DMARequest inRequest,
     @Nonnull Multimap<String, String> inParams,
     @Nonnull List<String> inErrors)
  {
    Map<String, Changes> changes = new HashMap<String, Changes>();
    for(Map.Entry<String, String> param : inParams.entries())
    {
      String []parts = param.getKey().split("::");

      // not a real key value pair
      if(parts.length != 3)
        continue;

      String fullType = parts[0];
      String typeName = Strings.getPattern(fullType, "/([^/]+)$");
      AbstractType<? extends AbstractEntry> type = null;
      if(typeName != null)
        type = AbstractType.get(typeName);
      else
        type = AbstractType.get(fullType);

      String id = parts[1];
      String key = parts[2];

      if(type == null)
      {
        String error = "invalid type '" + fullType + "' ignored";
        Log.warning(error);
        inErrors.add("gui.alert(" + Encodings.toJSString(error) + ");");
        continue;
      }

      Changes change = changes.get(id + ":" + fullType);
      if(change == null)
      {
        change = new Changes(id, type, fullType,
                             getData(inRequest, fullType, m_data),
                             inRequest.getUser());
        changes.put(id + ":" + fullType, change);
      }

      change.set(key, param.getValue());
    }

    return changes.values();
  }

  //........................................................................

  private static class Changes {
    public Changes(@Nonnull String inID,
                   @Nonnull AbstractType<? extends AbstractEntry> inType,
                   @Nonnull String inFullType,
                   @Nonnull DMAData inData,
                   @Nonnull AbstractEntry inOwner) {
      m_id = inID;
      m_name = m_id;
      m_type = inType;
      m_fullType = inFullType;
      m_data = inData;
      m_owner = inOwner;
    }

    public @Nonnull String m_id;
    public @Nonnull AbstractType<? extends AbstractEntry> m_type;
    public @Nonnull DMAData m_data;
    public @Nonnull AbstractEntry m_owner;
    public @Nullable String m_name;
    public @Nullable String m_file;
    public @Nonnull String m_fullType;
    public boolean m_multiple = false;
    public @Nonnull Map<String, String>m_values = new HashMap<String, String>();

    public @Nonnull String toString() {
      return m_name + " [" + m_id + "]/" + m_type + " (" + m_owner.getName()
        + "/" + m_file + (m_multiple ? ", single" : ", multiple") + "):"
        + m_values;
    }

    public void set(@Nonnull String inKey, @Nonnull String inValue)
    {
      if("file".equals(inKey))
        m_file = inValue;
      else
      {
        if("name".equals(inKey))
          m_name = inValue;

        m_values.put(inKey, inValue);
      }

      if(inKey.indexOf("/") > 0)
        m_multiple = true;
    }

    // Figure out the affected entries.
    public @Nonnull Set<AbstractEntry> entries(@Nonnull List<String> ioErrors)
    {
      Set<AbstractEntry> entries = new HashSet<AbstractEntry>();
      if(m_multiple)
      {
        for(AbstractEntry entry : m_data.getEntriesList(m_type))
          if(entry.matches(m_fullType, m_id))
            entries.add(entry);
      }
      else
      {
        AbstractEntry entry = m_data.getEntry(m_id, m_type);

        if(entry == null && m_file != null)
        {
          // create a new entry for filling out
          Log.event(m_owner.getID(), "create",
                    "creating " + m_type + " entry '" + m_name + "'");

          entry = m_type.create(m_name, m_data);
          if(entry instanceof Entry)
            entry.setOwner(m_owner);

          // store it in the campaign
          if(!m_data.addEntry(entry, m_file))
          {
            Log.warning("Could not store " + m_type + " '" + m_name + "' in '"
                        + m_file + "'");
            entry = null;
          }
        }

        if(entry == null)
        {
          String error = "could not find " + m_type + " " + m_name
            + " for saving";
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
        new net.ixitxachitls.dma.entries.BaseEntry
        ("test", new DMAData.Test.Data());
      DMAData.Test.Data data = new DMAData.Test.Data(entry);

      SaveActionServlet servlet = new SaveActionServlet(data);
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);
      com.google.common.collect.Multimap<String, String> params =
        com.google.common.collect.ImmutableMultimap.of
        ("/base entry::test::name", "guru");

      EasyMock.expect(user.getName()).andStubReturn("user");
      EasyMock.expect(request.getUser()).andStubReturn(user);
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(BaseCharacter.Group.ADMIN))
        .andStubReturn(true);

      EasyMock.replay(request, response, user);

      assertFalse("saved", data.wasSaved());
      assertEquals("name", "test", entry.getName());

      assertEquals("result",
                   "gui.info('The following entries were changed:"
                   + "<p>base entry guru'); util.link(null, '/entry/guru');",
                   servlet.doAction(request, response));

      assertTrue("saved", data.wasSaved());
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
        new net.ixitxachitls.dma.entries.BaseEntry
        ("test", new DMAData.Test.Data());
      DMAData.Test.Data data = new DMAData.Test.Data(entry);

      SaveActionServlet servlet = new SaveActionServlet(data);
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);
      com.google.common.collect.Multimap<String, String> params =
        com.google.common.collect.ImmutableMultimap.of
        ("/base entry::test::name", "guru");

      EasyMock.expect(request.getUser()).andStubReturn(user);
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(BaseCharacter.Group.ADMIN))
        .andStubReturn(false);

      EasyMock.replay(request, response, user);

      assertFalse("saved", data.wasSaved());
      assertEquals("name", "test", entry.getName());

      assertEquals("result",
                   "gui.alert('Parse error for values');"
                   + "gui.alert('not allowed to edit name in base entry "
                   + "test');\n"
                   + "gui.alert('No values to save');",
                   servlet.doAction(request, response));

      assertFalse("saved", data.wasSaved());
      assertEquals("name", "test", entry.getName());

      m_logger.addExpected("WARNING: not allowed to edit name in base entry "
                           + "test");
      EasyMock.verify(request, response, user);
    }

    //......................................................................
    //----- invalid id -----------------------------------------------------

    /** The save Test. */
    @org.junit.Test
    public void invalidID()
    {
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry
        ("test", new DMAData.Test.Data());
      DMAData.Test.Data data = new DMAData.Test.Data(entry);

      SaveActionServlet servlet = new SaveActionServlet(data);
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);
      com.google.common.collect.Multimap<String, String> params =
        com.google.common.collect.ImmutableMultimap.of
        ("/base entry::guru::name", "guru");

      EasyMock.expect(request.getUser()).andStubReturn(user);
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(BaseCharacter.Group.ADMIN))
        .andStubReturn(false);

      EasyMock.replay(request, response, user);

      assertFalse("saved", data.wasSaved());
      assertEquals("name", "test", entry.getName());

      assertEquals("result",
                   "gui.alert('Parse error for values');"
                   + "gui.alert('could not find base entry guru for saving');\n"
                   + "gui.alert('No values to save');",
                   servlet.doAction(request, response));

      assertFalse("saved", data.wasSaved());
      assertEquals("name", "test", entry.getName());

      m_logger.addExpected("WARNING: could not find base entry guru for "
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
        new net.ixitxachitls.dma.entries.BaseEntry
        ("test", new DMAData.Test.Data());
      DMAData.Test.Data data = new DMAData.Test.Data(entry);

      SaveActionServlet servlet = new SaveActionServlet(data);
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);
      com.google.common.collect.Multimap<String, String> params =
        com.google.common.collect.ImmutableMultimap.of
        ("guru::test::name", "guru");

      EasyMock.expect(request.getUser()).andStubReturn(user);
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(BaseCharacter.Group.ADMIN))
        .andStubReturn(false);

      EasyMock.replay(request, response, user);

      assertFalse("saved", data.wasSaved());
      assertEquals("name", "test", entry.getName());

      assertEquals("result",
                   "gui.alert('Parse error for values');"
                   + "gui.alert('invalid type \\'guru\\' ignored');\n"
                   + "gui.alert('No values to save');",
                   servlet.doAction(request, response));

      assertFalse("saved", data.wasSaved());
      assertEquals("name", "test", entry.getName());

      m_logger.addExpected("WARNING: invalid type 'guru' ignored");
      EasyMock.verify(request, response, user);
    }

    //......................................................................
    //----- invalid key ----------------------------------------------------

    /** The save Test. */
    @org.junit.Test
    public void invalidKey()
    {
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry
        ("test", new DMAData.Test.Data());
      DMAData.Test.Data data = new DMAData.Test.Data(entry);

      SaveActionServlet servlet = new SaveActionServlet(data);
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);
      com.google.common.collect.Multimap<String, String> params =
        com.google.common.collect.ImmutableMultimap.of
        ("/base entry::test::guru", "guru",
         "/base entry::test::description", "\"test\"");

      EasyMock.expect(request.getUser()).andStubReturn(user);
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(BaseCharacter.Group.ADMIN))
        .andStubReturn(true);

      EasyMock.replay(request, response, user);

      assertFalse("saved", data.wasSaved());
      assertEquals("name", "test", entry.getName());

      assertEquals("result",
                   "gui.alert('Parse error for values');"
                   + "gui.info('The following entries were changed:"
                   + "<p>base entry test'); "
                   + "util.link(null, '/entry/test');"
                   + "edit.unparsed('/base entry', 'test', 'guru', 'guru');",
                   servlet.doAction(request, response));

      assertTrue("saved", data.wasSaved());
      assertEquals("name", "test", entry.getName());

      m_logger.addExpected("WARNING: Could not fully parse guru value for "
                           + "base entry test: 'guru'");
      EasyMock.verify(request, response, user);
    }

    //......................................................................
    //----- changes --------------------------------------------------------

    /** The changes Test. */
    @org.junit.Test
    public void collectChanges()
    {
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry
        ("test", new DMAData.Test.Data());
      DMAData.Test.Data data = new DMAData.Test.Data(entry);

      SaveActionServlet servlet = new SaveActionServlet(data);
      DMARequest request = EasyMock.createMock(DMARequest.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);

      EasyMock.expect(request.getUser()).andStubReturn(user);

      EasyMock.replay(request);

      Multimap<String, String> params =
        com.google.common.collect.ImmutableSetMultimap.<String, String>builder()
        .put("/base entry::id::name", "guru")
        .put("/base entry::id::description", "\"text\"")
        .put("/base entry::id2::name", "guru2")
        .put("/base entry::id2::file", "file")
        .put("/base entry::id2::worlds", "wolrd")
        .put("something/base entry::my-id::name", "name guru")
        .put("/user/me/base entry::id::key", "value")
        .put("/user/me/base entry::id::file", "file")
        .put("/user/me/base entry::id::key2", "value2")
        .put("/base entry::*/Person::key", "value")
        .put("/base entry::*/Something::key", "value")
        .build();
      List<String> errors = new ArrayList<String>();
      Collection<Changes> changes = servlet.preprocess(request, params, errors);

      java.util.Iterator<Changes> i = changes.iterator();
      checkChanges(i.next(), "my-id", "name guru", null, "something/base entry",
                   data, user, "name", "name guru");
      checkChanges(i.next(), "id", "id", "file", "/user/me/base entry", null,
                   user, "key", "value", "key2", "value2");
      checkChanges(i.next(), "id2", "guru2", "file", "/base entry", data, user,
                   "name", "guru2", "worlds", "wolrd");
      checkChanges(i.next(), "id", "guru", null, "/base entry", data, user,
                   "name", "guru", "description", "\"text\"");
      checkChanges(i.next(), "*/Something", "*/Something", null, "/base entry",
                   data, user, "key", "value");
      checkChanges(i.next(), "*/Person", "*/Person", null, "/base entry",
                   data, user, "key", "value");
      assertFalse(i.hasNext());
    }

    /** Check assertions for changes. */
    private void checkChanges(Changes inChanges, String inID, String inName,
                              String inFile, String inFullType, DMAData inData,
                              AbstractEntry inOwner, String ... inKeyValues)
    {
      assertEquals("id", inID, inChanges.m_id);
      assertEquals("name", inName, inChanges.m_name);
      assertEquals("file", inFile, inChanges.m_file);
      assertEquals("full type", inFullType, inChanges.m_fullType);
      assertEquals("data", inData, inChanges.m_data);
      assertEquals("owner", inOwner, inChanges.m_owner);

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
