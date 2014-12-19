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

package net.ixitxachitls.dma.server.servlets;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Group;
import net.ixitxachitls.util.Encodings;

/**
 * The servlet to save given entries.
 *
 * @file          SaveActionServlet.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

public class SaveActionServlet extends ActionServlet
{
  /**
   * Create the entry action servlet.
   */
  public SaveActionServlet()
  {
    // nothing to do
  }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /** The joiner to join errors. */
  private static final Joiner newlineJoiner = Joiner.on("<br />");

  @Override
  public String toString()
  {
    return "SaveActionServlet";
  }

  /**
   * Execute the action associated with this servlet.
   *
   * @param       inRequest  the request for the page
   * @param       inResponse the response to write to
   *
   * @return      the javascript code to send back to the client
   */
  @Override
  protected String doAction(DMARequest inRequest,
                            HttpServletResponse inResponse)
  {
    Optional<BaseCharacter> user = inRequest.getUser();
    if(!user.isPresent() || !user.get().hasAccess(Group.PLAYER))
    {
      return "gui.alert('You don't have the rights to change the entry!');";
    }

    Optional<String> keyParam = inRequest.getParam("_key_");
    if(!keyParam.isPresent() || keyParam.get().isEmpty())
      return "gui.alert('Cannot save values, as no key is given');";

    Optional<EntryKey> key = EntryKey.fromString(keyParam.get());

    if(!key.isPresent())
      return "gui.alert('Cannot create entry key for " + keyParam.get() + "');";

    if(!key.get().editableBy(user.get()))
    {
      return "gui.alert('You don't own that entry, " +
          "thus you can't change it!');";
    }

    Optional<AbstractEntry> entry = DMADataFactory.get().getEntry(key.get());
    if(!entry.isPresent())
      if(inRequest.hasParam("_create_"))
      {
        entry = (Optional<AbstractEntry>)
            key.get().getType().create(key.get().getID());
        if(entry.isPresent())
          entry.get().updateKey(key.get());
      }
      else
        return "gui.alert('Cannot find entry for " + key.get() + "');";

    if(!entry.isPresent())
      return "gui.alert('could not create entry');";

    Values values = new Values(inRequest.getParams());
    entry.get().set(values);
    List<String> errors = values.obtainMessages();

    if(!errors.isEmpty())
      return "gui.alert('" + Encodings.escapeJS(newlineJoiner.join(errors))
        + "');";

    if(values.isChanged())
    {
      entry.get().changed();
      entry.get().save();
      return "gui.info('Entry " + entry.get().getName()
          + " has been saved.'); true";
    }

    return "gui.info('No changes needed saving'); true";
  }

  //----------------------------------------------------------------------------

    /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** The save Test. */
    @org.junit.Test
    public void save()
    {
      SaveActionServlet servlet = new SaveActionServlet();
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);
      com.google.common.collect.Multimap<String, String> params =
        com.google.common.collect.ImmutableMultimap.of("name", "guru");

      EasyMock.expect(user.getName()).andStubReturn("user");
      EasyMock.expect(request.getUser()).andStubReturn(Optional.of(user));
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(Group.PLAYER)).andStubReturn(true);
      EasyMock.expect(request.getParam("_key_"))
          .andStubReturn(Optional.of("/base entry/test"));
      EasyMock.expect(user.hasAccess(Group.ADMIN))
        .andStubReturn(true);

      EasyMock.replay(request, response, user);

      BaseEntry entry = (BaseEntry) DMADataFactory.get().getEntry(
          new EntryKey("test", BaseEntry.TYPE)).get();
      assertEquals("name", "test", entry.getName());

      assertEquals("result", "gui.info('Entry guru has been saved.'); true",
                   servlet.doAction(request, response));

      assertEquals("name", "guru", entry.getName());

      EasyMock.verify(request, response, user);
    }

    /** The save Test. */
    @org.junit.Test
    public void noAccess()
    {
      SaveActionServlet servlet = new SaveActionServlet();
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);
      com.google.common.collect.Multimap<String, String> params =
        com.google.common.collect.ImmutableMultimap.of("name", "guru");

      EasyMock.expect(request.getUser()).andStubReturn(Optional.of(user));
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(Group.ADMIN))
          .andStubReturn(false);
      EasyMock.expect(user.hasAccess(Group.PLAYER))
          .andStubReturn(false);
      EasyMock.expect(request.getParam("_key_"))
          .andStubReturn(Optional.of("/base entry/test"));
      EasyMock.replay(request, response, user);

      BaseEntry entry = (BaseEntry) DMADataFactory.get().getEntry(
          new EntryKey("test", BaseEntry.TYPE)).get();
      assertEquals("name", "test", entry.getName());

      assertEquals("result",
                   "gui.alert('You don't have the rights to change the "
                       + "entry!');",
                   servlet.doAction(request, response));

      assertEquals("name", "test", entry.getName());
      EasyMock.verify(request, response, user);
    }

    /** The save Test. */
    @org.junit.Test
    public void invalidID()
    {
      SaveActionServlet servlet = new SaveActionServlet();
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);
      com.google.common.collect.Multimap<String, String> params =
        com.google.common.collect.ImmutableMultimap.of("name", "guru");

      EasyMock.expect(request.getUser()).andStubReturn(Optional.of(user));
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(Group.ADMIN))
        .andStubReturn(true);
      EasyMock.expect(user.hasAccess(Group.PLAYER))
          .andStubReturn(true);
      EasyMock.expect(request.getParam("_key_"))
          .andStubReturn(Optional.of("/base entry/invalid"));
      EasyMock.expect(request.hasParam("_create_")).andStubReturn(false);
      EasyMock.expect(user.getName()).andStubReturn("Merlin");

      EasyMock.replay(request, response, user);

      assertEquals("result",
                   "gui.alert('Cannot find entry for /base entry/invalid');",
                   servlet.doAction(request, response));

      EasyMock.verify(request, response, user);
    }

    /** The save Test. */
    @org.junit.Test
    public void invalidType()
    {
      SaveActionServlet servlet = new SaveActionServlet();
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);
      com.google.common.collect.Multimap<String, String> params =
        com.google.common.collect.ImmutableMultimap.of("name", "guru");

      EasyMock.expect(request.getUser()).andStubReturn(Optional.of(user));
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(Group.ADMIN))
        .andStubReturn(false);
      EasyMock.expect(user.hasAccess(Group.PLAYER))
          .andStubReturn(true);
      EasyMock.expect(request.getParam("_key_"))
          .andStubReturn(Optional.of("/invalid/test"));

      EasyMock.replay(request, response, user);

      assertEquals("result",
                   "gui.alert('Cannot create entry key for /invalid/test');",
                   servlet.doAction(request, response));

      EasyMock.verify(request, response, user);
    }

    /** The save Test. */
    @org.junit.Test
    public void invalidKey()
    {
      SaveActionServlet servlet = new SaveActionServlet();
      DMARequest request = EasyMock.createMock(DMARequest.class);
      HttpServletResponse response =
        EasyMock.createMock(HttpServletResponse.class);
      BaseCharacter user = EasyMock.createMock(BaseCharacter.class);
      com.google.common.collect.Multimap<String, String> params =
        com.google.common.collect.ImmutableMultimap.of("guru", "guru",
                                                       "description", "test");

      EasyMock.expect(request.getUser()).andStubReturn(Optional.of(user));
      EasyMock.expect(request.getParams()).andStubReturn(params);
      EasyMock.expect(user.hasAccess(Group.ADMIN))
        .andStubReturn(true);
      EasyMock.expect(user.hasAccess(Group.PLAYER))
          .andStubReturn(true);
      EasyMock.expect(request.getParam("_key_"))
          .andStubReturn(Optional.of("/base entry/test"));

      EasyMock.replay(request, response, user);

      assertEquals("result",
                   "gui.alert('Parse error for values');"
                   + "gui.info('The following entries were updated:"
                   + "<p>base entry test'); "
                   + "util.link(null, '/entry/test');"
                   + "edit.unparsed('base entry', 'test', 'guru', 'guru');",
                   servlet.doAction(request, response));

      m_logger.addExpected("WARNING: trying to set undefined variable guru "
          + "with guru in test [class net.ixitxachitls.dma.entries.BaseEntry]");
      m_logger.addExpected("WARNING: Could not fully parse guru value for "
                           + "/base entry/test: 'guru'");
      EasyMock.verify(request, response, user);
    }
  }
}
