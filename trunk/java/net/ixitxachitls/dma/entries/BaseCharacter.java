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
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.BaseCharacterProto;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

/**
 * An object of this class represents a real person associated with D&D.
 *
 * @file          BaseCharacter.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@ParametersAreNonnullByDefault
public class BaseCharacter extends BaseEntry
{
  /** The possible groups for a character. */
  public enum Group implements EnumSelection.Named,
                               EnumSelection.Proto<BaseCharacterProto.Group>
  {
    /** An administrator. */
    ADMIN("Admin", BaseCharacterProto.Group.ADMIN),

    /** A DM (in any campaign). */
    DM("DM", BaseCharacterProto.Group.DM),

    /** A guest user without any special permissions. */
    GUEST("Guest", BaseCharacterProto.Group.GUEST),

    /** The player in possession of the entry. */
    PLAYER("Player", BaseCharacterProto.Group.PLAYER),

    /** A normal user. */
    USER("User", BaseCharacterProto.Group.USER);

    /** Create the group.
     *
     * @param inName  the name of the value
     * @param inProto the proto enum value
     */
    private Group(String inName, BaseCharacterProto.Group inProto)
    {
      m_name = constant("group", inName);
      m_proto = inProto;
    }

    /** The name of the group. */
    private String m_name;

    /** The proto enum value. */
    private BaseCharacterProto.Group m_proto;

    /**
     * Check if a group allows a given group.
     *
     * @param  inGroup the group to check against
     *
     * @return true if the other group is less or equally restricted than the
     *         current one
     */
    public boolean allows(Group inGroup)
    {
      return this.ordinal() <= inGroup.ordinal();
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public BaseCharacterProto.Group toProto()
    {
      return m_proto;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    /**
     * Get the group matching the given proto value.
     *
     * @param  inGroup the proto value to look for
     * @return the matched group (will throw exception if not found)
     */
    public static Group fromProto(BaseCharacterProto.Group inGroup)
    {
      for(Group group : values())
        if(group.m_proto == inGroup)
          return group;

      throw new IllegalStateException("invalid proto group: " + inGroup);
    }

    /**
     * All the possible names for the group.
     *
     * @return the possible names
     */
    public static List<String> names()
    {
      List<String> names = new ArrayList<>();

      for(Group group : values())
        names.add(group.getName());

      return names;
    }

    /**
     * Get the group matching the given text.
     */
    public static @Nullable Group fromString(String inText)
    {
      for(Group group : values())
        if(group.m_name.equalsIgnoreCase(inText))
          return group;

      return null;
    }
  }


  /**
   * This is the standard constructor to create a base character with its
   * name.
   *
   * @param       inName the name of the base charcter to create
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

    m_email = inEmail;
  }

  /**
   * The default internal constructor to create an undefined entry to be
   * filled by reading it from a file.
   *
   */
  protected BaseCharacter()
  {
    super(TYPE);
  }

  /** The files in the base campaign. */
  protected String m_email = UNDEFINED_STRING;

  /** The access group of the user. */
  protected Group m_group = Group.GUEST;

  /** The files in the base campaign. */
  protected String m_lastAction = UNDEFINED_STRING;

  /** All the products for this user. */
  protected transient @Nullable DMAData m_productData = null;

  /** The files in the base campaign. */
  protected String m_realName = UNDEFINED_STRING;

  /** The number of recent products to show. */
  public static final int MAX_PRODUCTS =
    Config.get("entries/basecharacter.products", 5);

  /** The type of this entry. */
  public static final BaseType<BaseCharacter> TYPE =
    new BaseType<BaseCharacter>(BaseCharacter.class).withLink("user", "users");

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  static
  {
    extractVariables(BaseCharacter.class);
  }

  /**
   * Get the users email address.
   *
   * @return      the users email address.
   */
  public String getEmail()
  {
    return m_email;
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
    return m_realName;
  }

  /**
   * Get the users last action time.
   *
   * @return the time and date of the last action
   */
  public String getLastAction()
  {
    return m_lastAction;
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
    return inGroup.allows(getGroup());
  }

  @Override
  public boolean isShownTo(@Nullable BaseCharacter inUser)
  {
    return inUser != null;
  }

  @Override
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    if(inUser == null)
      return false;

    return inUser.hasAccess(Group.ADMIN) || inUser == this;
  }

  @Override
  public Map<String, Object> collectSearchables()
  {
    Map<String, Object> searchables = super.collectSearchables();

    searchables.put("email", m_email);

    return searchables;
  }

  @Override
  public @Nullable Object compute(String inKey)
  {
    if("products".equals(inKey))
    {
      List<Product> products = DMADataFactory.get()
        .getRecentEntries(Product.TYPE, getKey());

      List<Multiple> values = new ArrayList<Multiple>();
      for(Product product : products)
      {
        values.add(new Multiple(new Name(product.getFullTitle()),
                                new Name(product.getPath())));

        if(values.size() > MAX_PRODUCTS)
          break;
      }

      if(values.isEmpty())
        return new ValueList<Multiple>(new Multiple(new Name(), new Name()));

      return new ValueList<Multiple>(values);
    }

    return super.compute(inKey);
  }

  @Override
  public Message toProto()
  {
    BaseCharacterProto.Builder builder = BaseCharacterProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());
    builder.setGroup(m_group.toProto());
    if(!m_lastAction.isEmpty())
      builder.setLastAction(m_lastAction);
    if(!m_realName.isEmpty())
      builder.setRealName(m_realName);
    if(!m_email.isEmpty())
      builder.setEmail(m_email);

    return builder.build();
  }

  @Override
  public @Nullable String set(String inKey, String inText)
  {
    switch(inKey)
    {
      case "real name":
        m_realName = inText;
        return null;

      case "email":
        m_email = inText;
        return null;

      case "group":
         m_group = Group.fromString(inText);
         if (m_group == null)
         {
           m_group = Group.GUEST;
           return inText;
         }
         return null;
    }

    return super.set(inKey,  inText);
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
    m_realName = inRealName;
    changed();
  }

  /**
   * The character did or does an action, record this.
   */
  public void action()
  {
    m_lastAction = Strings.today();
    save();
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(BaseCharacterProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

  @Override
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
      m_lastAction = proto.getLastAction();
    if(proto.hasRealName())
      m_realName = proto.getRealName();
    if(proto.hasEmail())
      m_email = proto.getEmail();
  }

  /**
   * Read an entry, and only the entry without type and comments, from the
   * reader.
   *
   * @param       inReader the reader to read from
   *
   * @return      true if read successfully, false else
   *
   */
  @Override
  protected boolean readEntry(ParseReader inReader)
  {
    return super.readEntry(inReader);
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

      assertEquals("id", "Me", character.getName());
      assertTrue("real name", character.m_realName.isEmpty());
      assertTrue("email", character.m_email.isEmpty());
      assertTrue("last action", character.m_lastAction.isEmpty());
      assertEquals("group", Group.GUEST, character.m_group);
    }

    /** The read Test. */
    @org.junit.Test
    public void read()
    {
      String text =
        "base character Me = \n"
        + "\n"
        + "  real name     \"Roger Rabbit\";\n"
        + "  email         \"roger@acme.com <'Roger Rabbit'>\";\n"
        + "  last action   \"today\";\n"
        + "  group         user.\n"
        + "\n";

      try (net.ixitxachitls.input.ParseReader reader =
        new net.ixitxachitls.input.ParseReader(new java.io.StringReader(text),
                                               "test"))
      {
        BaseCharacter character = (BaseCharacter)BaseCharacter.read(reader);

        assertNotNull("base character should have been read", character);
        assertEquals("base character name does not match", "Me",
                     character.getName());
        assertEquals("base character does not match",
                     "#----- Me\n"
                     + "\n"
                     + "base character Me =\n"
                     + "\n"
                     + "  real name         \"Roger Rabbit\";\n"
                     + "  email             "
                     + "\"roger@acme.com <'Roger Rabbit'>\";\n"
                     + "  last action       \"today\";\n"
                     + "  group             User;\n"
                     + "  name              Me.\n"
                     + "\n"
                     + "#.....\n",
                     character.toString());
      }
    }
  }
}
