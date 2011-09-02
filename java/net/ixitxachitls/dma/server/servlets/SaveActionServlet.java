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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;

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
// import net.ixitxachitls.dma.entries.Entry;
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
    BaseCharacter user = inRequest.getUser();

    Set<AbstractEntry> entries = new HashSet<AbstractEntry>();
    List<String> errors = new ArrayList<String>();

    Set<DMAData> datas = new HashSet<DMAData>();
    for(Map.Entry<String, String> param : inRequest.getParams().entries())
    {
      String []parts = param.getKey().split("::");

      // not a real key value pair
      if(parts.length != 3)
        continue;

      String fullType = parts[0];
      DMAData data = getData(inRequest, fullType, m_data);
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
        errors.add("gui.alert(" + Encodings.toJSString(error) + ");");
        continue;
      }

      // Figure out the affected entries.
      Set<AbstractEntry> affectedEntries = new HashSet<AbstractEntry>();
      parts = Strings.getPatterns(key, "(.*?)/(.*)");
      if(parts.length == 2)
      {
        for(AbstractEntry entry : data.getEntriesList(type))
          if(entry.matches(fullType, id))
            affectedEntries.add(entry);
      }
      else
      {
        AbstractEntry entry = data.getEntry(id, type);
        if(entry == null)
        {
          String error = "could not find " + type + " " + id + " for saving";
          Log.warning(error);
          errors.add("gui.alert(" + Encodings.toJSString(error) + ");");
          continue;
        }

        if(!entry.canEdit(key, user))
        {
          String error = "not allowed to edit " + key + " in " + type + " "
            + entry.getID();
          Log.warning(error);
          errors.add("gui.alert(" + Encodings.toJSString(error) + ");");
          continue;
        }

        affectedEntries.add(entry);
      }

      String value = param.getValue();

      for(AbstractEntry entry : affectedEntries)
      {
        String rest = entry.set(key, value);
        if(rest != null)
        {
          Log.warning("Could not fully parse " + key + " value for "
                      + type + " " + id + ": '" + rest + "'");
          errors.add("edit.unparsed("
                     + Encodings.toJSString(fullType) + ", "
                     + Encodings.toJSString(id) + ", "
                     + Encodings.toJSString(key) + ", "
                     + Encodings.toJSString(rest) + ");");
        }

        entries.add(entry);
      }

      datas.add(data);
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

      EasyMock.expect(request.getUser()).andStubReturn(user);
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(BaseCharacter.Group.ADMIN))
        .andStubReturn(true);

      EasyMock.replay(request, response, user);

      assertFalse("saved", data.wasSaved());
      assertEquals("name", "test", entry.getName());

      assertEquals("result",
                   "gui.info('The following entries were changed:"
                   + "<p>base entry guru'); util.link(null, '/entrys/guru');",
                   servlet.doAction(request, response));

      assertTrue("saved", data.wasSaved());
      assertEquals("name", "guru", entry.getName());

      EasyMock.verify(request, response, user);
    }

    //......................................................................
    //----- no access -------------------------------------------------------

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
        ("/base entry::test::guru", "guru");

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
                   + "util.link(null, '/entrys/test');"
                   + "edit.unparsed('/base entry', 'test', 'guru', 'guru');",
                   servlet.doAction(request, response));

      assertTrue("saved", data.wasSaved());
      assertEquals("name", "test", entry.getName());

      m_logger.addExpected("WARNING: Could not fully parse guru value for "
                           + "base entry test: 'guru'");
      EasyMock.verify(request, response, user);
    }

    //......................................................................
  }

  //........................................................................
}
