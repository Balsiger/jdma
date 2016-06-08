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

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.proto.Entries;
import net.ixitxachitls.dma.proto.Entries.BaseCharacterProto;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.server.servlets.workers.EntryRefresh;
import net.ixitxachitls.dma.values.MiniatureLocation;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Group;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

/**
 * An object of this class represents a real person associated with D&D.
 *
 * @file          BaseCharacter.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class BaseCharacter extends BaseEntry
{

  /**
   * This is the standard constructor to create a base character with its
   * name.
   *
   * @param       inName the name of the base character to create
   */
  public BaseCharacter(String inName)
  {
    super(inName, TYPE);
  }

  /**
   * This is the constructor to create a base character with its
   * name and email.
   *
   * @param       inName the name of the base character to create
   * @param       inEmail the email address of the base character to create
   */
  public BaseCharacter(String inName, String inEmail)
  {
    this(inName);

    m_email = Optional.of(inEmail);
  }

  /**
   * The default internal constructor to create an undefined entry to be
   * filled by reading it from a file.
   */
  protected BaseCharacter()
  {
    super(TYPE);
  }

  /** The files in the base campaign. */
  protected Optional<String> m_email = Optional.absent();

  /** The access group of the user. */
  protected Group m_group = Group.GUEST;

  /** The files in the base campaign. */
  protected Optional<String> m_lastAction = Optional.absent();

  /** The files in the base campaign. */
  protected Optional<String> m_realName = Optional.absent();

  protected List<MiniatureLocation> m_miniatureLocations = new ArrayList<>();

  /** The number of recent products to show. */
  public static final int MAX_PRODUCTS = 5;

  /** The type of this entry. */
  public static final BaseType<BaseCharacter> TYPE =
    new BaseType.Builder<BaseCharacter>(BaseCharacter.class)
        .link("user", "users").build();

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Get the users email address.
   *
   * @return      the users email address.
   */
  public String getEmail()
  {
    if(m_email.isPresent())
      return m_email.get();

    return "";
  }

  /**
   * Get the group this user is in.
   *
   * @return      the group of the user
   */
  public Group getGroup()
  {
    return m_group;
  }

  /**
   * Get the users real name.
   *
   * @return the real name
   */
  public String getRealName()
  {
    if(m_realName.isPresent())
      return m_realName.get();

    return "";
  }

  /**
   * Get the users last action time.
   *
   * @return the time and date of the last action
   */
  public String getLastAction()
  {
    if(m_lastAction.isPresent())
      return m_lastAction.get();

    return "";
  }

  public List<MiniatureLocation> getMiniatureLocations()
  {
    return Collections.unmodifiableList(m_miniatureLocations);
  }

  /**
   * Checks if the user has at least the given access.
   *
   * @param       inGroup the group to check for
   *
   * @return      true if enough access, false if not
   */
  public boolean hasAccess(Group inGroup)
  {
    return getGroup().allows(inGroup);
  }

  @Override
  public boolean isShownTo(Optional<BaseCharacter> inUser)
  {
    return inUser.isPresent();
  }

  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(Group.ADMIN);
  }

  @Override
  public Map<String, Object> collectSearchables()
  {
    Map<String, Object> searchables = super.collectSearchables();

    searchables.put("email", getEmail());

    return searchables;
  }

  @Override
  public Message toProto()
  {
    BaseCharacterProto.Builder builder = BaseCharacterProto.newBuilder();

    builder.setBase((BaseEntryProto) super.toProto());
    builder.setGroup(m_group.toProto());
    if(m_lastAction.isPresent())
      builder.setLastAction(m_lastAction.get());
    if(m_realName.isPresent())
      builder.setRealName(m_realName.get());
    if(m_email.isPresent())
      builder.setEmail(m_email.get());

    // Sort the location before storing.
    Collections.sort(m_miniatureLocations, new Comparator<MiniatureLocation>()
    {
      @Override
      public int compare(MiniatureLocation o1, MiniatureLocation o2)
      {
        int compare = o1.getLocation().compareTo(o2.getLocation());
        if(compare != 0)
          return compare;

        return o1.getRules().compareTo(o2.getRules());
      }
    });

    for(MiniatureLocation location : m_miniatureLocations)
      builder.addMiniatureLocations(location.toProto());

    return builder.build();
  }

  @Override
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    m_realName = inValues.use("real_name", m_realName);
    m_email = inValues.use("email", m_email);
    m_group = inValues.use("group", m_group, Group.PARSER);
    m_miniatureLocations = inValues.use("miniature_location",
                                        m_miniatureLocations,
                                        MiniatureLocation.PARSER,
                                        "location", "rules", "overrides",
                                        "color");
  }

  /**
   * Set the group of the user.
   *
   * @param inSelected the selected group
   */
  public void setGroup(Group inSelected)
  {
    m_group = inSelected;
    changed();
  }

  /**
   * Set the real name of the user.
   *
   * @param inRealName the real name of the user
   */
  public void setRealName(String inRealName)
  {
    m_realName = Optional.of(inRealName);
    changed();
  }

  /**
   * The character did or does an action, record this.
   */
  public void action()
  {
    m_lastAction = Optional.of(Strings.today());
    save();
  }

  @Override
  public boolean save()
  {
    if(m_changed)
    {
      Queue queue = QueueFactory.getDefaultQueue();
      queue.add(TaskOptions.Builder.withUrl("/task/refresh")
                    .param(EntryRefresh.PARENT_PARAM, getKey().toString())
                    .param(EntryRefresh.TYPE_PARAM, Miniature.TYPE.toString()));
    }

    return super.save();
  }

  @Override
  protected Message defaultProto()
  {
    return BaseCharacterProto.getDefaultInstance();
  }

  /**
   * Update the entry with the values from the given proto.
   *
   * @param inProto the proto to merge from
   */
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseCharacterProto))
    {
      Log.warning("cannot parse character proto " + inProto.getClass());
      return;
    }

    BaseCharacterProto proto = (BaseCharacterProto)inProto;

    super.fromProto(proto.getBase());

    if(proto.hasGroup())
      m_group = Group.fromProto(proto.getGroup());
    if(proto.hasLastAction())
      m_lastAction = Optional.of(proto.getLastAction());
    if(proto.hasRealName())
      m_realName = Optional.of(proto.getRealName());
    if(proto.hasEmail())
      m_email = Optional.of(proto.getEmail());
    for(BaseCharacterProto.MiniatureLocation location
        : proto.getMiniatureLocationsList())
      m_miniatureLocations.add(MiniatureLocation.fromProto(location));
  }

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** The group Test. */
    @org.junit.Test
    public void group()
    {
      BaseCharacter character = new BaseCharacter("Me");

      assertTrue("guest", character.hasAccess(Group.GUEST));
      assertFalse("user", character.hasAccess(Group.USER));
      assertFalse("player", character.hasAccess(Group.PLAYER));
      assertFalse("dm", character.hasAccess(Group.DM));
      assertFalse("admin", character.hasAccess(Group.ADMIN));

      character.setGroup(Group.USER);

      assertTrue("guest", character.hasAccess(Group.GUEST));
      assertTrue("user", character.hasAccess(Group.USER));
      assertFalse("player", character.hasAccess(Group.PLAYER));
      assertFalse("dm", character.hasAccess(Group.DM));
      assertFalse("admin", character.hasAccess(Group.ADMIN));

      character.setGroup(Group.ADMIN);

      assertTrue("guest", character.hasAccess(Group.GUEST));
      assertTrue("user", character.hasAccess(Group.USER));
      assertTrue("player", character.hasAccess(Group.PLAYER));
      assertTrue("dm", character.hasAccess(Group.DM));
      assertTrue("admin", character.hasAccess(Group.ADMIN));
    }

    /** The init Test. */
    @org.junit.Test
    public void init()
    {
      BaseCharacter character = new BaseCharacter("Me");

      assertEquals("name", "Me", character.getName());
      assertFalse("real name", character.m_realName.isPresent());
      assertFalse("email", character.m_email.isPresent());
      assertFalse("last action", character.m_lastAction.isPresent());
      assertEquals("group", Group.GUEST, character.m_group);
    }

    /** Test setting. */
    @org.junit.Test
    public void set()
    {
      BaseCharacter character = new BaseCharacter("Me");

      character.set(new Values(
          new ImmutableSetMultimap.Builder<String, String>()
              .put("name", "Merlin")
              .put("real_name", "Myrrdin")
              .put("email", "merlin@camelot.me")
              .put("group", "DM")
              .build()));
      assertEquals("name", "Merlin", character.getName());
      assertEquals("real name", "Myrrdin", character.getRealName());
      assertEquals("email", "merlin@camelot.me", character.getEmail());
      assertFalse("last action", character.m_lastAction.isPresent());
      assertEquals("group", Group.DM, character.getGroup());
    }

    /** Test user access. */
    @org.junit.Test
    public void user()
    {
      BaseCharacter character = new BaseCharacter("Me");

      assertFalse("shown to",
                  character.isShownTo(Optional.<BaseCharacter>absent()));
      assertTrue("show to", character.isShownTo(Optional.of(character)));

      assertFalse("is dm",
                  character.isDM(Optional.<BaseCharacter>absent()));
      assertFalse("is dm", character.isDM(Optional.of(character)));
      character.setGroup(Group.ADMIN);
      assertTrue("is dm", character.isDM(Optional.of(character)));
    }

    /** Test searchable. */
    @org.junit.Test
    public void searchables()
    {
      BaseCharacter character = new BaseCharacter("Me");
      assertEquals("size", 2, character.collectSearchables().size());
      assertEquals("email", "", character.collectSearchables().get("email"));
      assertEquals("bases", "[]",
                   character.collectSearchables().get("bases").toString());
    }

    /** Test proto. */
    @org.junit.Test
    public void proto()
    {
      BaseCharacterProto proto = BaseCharacterProto
          .newBuilder()
          .setBase(BaseEntryProto.newBuilder()
                                 .setAbstract(Entries.AbstractEntryProto
                                                  .newBuilder()
                                                  .setName("name")
                                                  .setType("base character")
                                                  .build()))
          .setGroup(Group.ADMIN.toProto())
          .setEmail("email")
          .setRealName("real name")
          .build();
      BaseCharacter character = new BaseCharacter();
      character.fromProto(proto);
      assertEquals("proto", proto, character.toProto());
    }
  }
}
