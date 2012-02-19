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

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * An object of this class represents a real person associated with D&D.
 *
 * @file          BaseCharacter.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class BaseCharacter extends BaseEntry
{
  //----------------------------------------------------------------- nested

  /** The possible groups for a character. */
  public enum Group implements EnumSelection.Named
  {
    /** A guest user without any special permissions. */
    GUEST("Guest"),

    /** A normal user. */
    USER("User"),

    /** The player in possession of the entry. */
    PLAYER("Player"),

    /** A DM (in any campaign). */
    DM("DM"),

    /** An administrator. */
    ADMIN("Admin");

    /** Create the group.
     *
     * @param inName the name of the value
     *
     */
    private Group(@Nonnull String inName)
    {
      m_name = constant("group", inName);
    }

    /** The name of the group. */
    private @Nonnull String m_name;

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
	public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
	public @Nonnull String toString()
    {
      return m_name;
    }

    /** Check if a group allows a given group.
     *
     * @param  inGroup the group to check against
     *
     * @return true if the other group is less or equally restricted than the
     *         current one
     *
     */
    public boolean allows(@Nonnull Group inGroup)
    {
      return this.ordinal() <= inGroup.ordinal();
    }
  }

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //---------------------------- BaseCharacter -----------------------------

  /**
   * The default internal constructor to create an undefined entry to be
   * filled by reading it from a file.
   *
   */
  protected BaseCharacter()
  {
    super(TYPE);
  }

  //........................................................................
  //---------------------------- BaseCharacter -----------------------------

  /**
   * This is the standard constructor to create a base character with its
   * name.
   *
   * @param       inName the name of the base charcter to create
   *
   */
  public BaseCharacter(@Nonnull String inName)
  {
    super(inName, TYPE);
  }

  /**
   * This is the constructor to create a base character with its
   * name and email.
   *
   * @param       inName the name of the base character to create
   * @param       inEmail the email address of the base character to create
   *
   */
  public BaseCharacter(@Nonnull String inName,
                       @Nonnull String inEmail)
  {
    this(inName);
    m_email = new Text(inEmail);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final BaseType<BaseCharacter> TYPE =
    new BaseType<BaseCharacter>(BaseCharacter.class).withLink("user", "users");

  /** The printer for printing the whole base character. */
  public static final Print s_pagePrint =
    new Print("$image "
              + "${as pdf} ${as text} ${as dma}"
              + "$title "
              + "$clear $files"
              + "\n " // need to start a new line for ascii
              + "$par "
              + "%name "
              + "%{real name} %email "
              + "%{last action} %group %characters %products "
              + "%errors");

  /** The printer for printing in a list. */
  public static final ListPrint s_listPrint =
    new ListPrint("1:L(label);20:L(name)[Name];20(name)[Real Name];"
                  + "1:L(group)[Group];"
                  + "1:L(action)[Last Action]",
                  "$label $listlink", null, "${real name}", "$group",
                  "${last action}");

  /** The number of recent products to show. */
  public static final int MAX_PRODUCTS =
    Config.get("entries/basecharacter.products", 5);

  //----- real name --------------------------------------------------------

  /** The files in the base campaign. */
  @Key("real name")
  @DM
  protected @Nonnull Text m_realName = new Text();

  //........................................................................
  //----- email ------------------------------------------------------------

  /** The files in the base campaign. */
  @Key("email")
  @DM
  protected @Nonnull Text m_email = new Text();

  //........................................................................
  //----- products ---------------------------------------------------------

  /** All the products for this user. */
  protected @Nullable DMAData m_productData = null;

  //........................................................................
  //----- last action ------------------------------------------------------

  /** The files in the base campaign. */
  @Key("last action")
  @NoEdit
  protected @Nonnull Text m_lastAction = new Text();

  //........................................................................
  //----- group ------------------------------------------------------------

  /** The access group of the user. */
  @Key("group")
  protected @Nonnull EnumSelection<Group> m_group =
    new EnumSelection<Group>(Group.class);

  //........................................................................

  static
  {
    extractVariables(BaseCharacter.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getPagePrint -----------------------------

  /**
   * Get the print for a full page.
   *
   * @return the print for page printing
   *
   */
  @Override
protected @Nonnull Print getPagePrint()
  {
    return s_pagePrint;
  }

  //........................................................................
  //----------------------------- getListPrint -----------------------------

  /**
   * Get the print for a list entry.
   *
   * @return the print for list entry
   *
   */
  @Override
protected @Nonnull ListPrint getListPrint()
  {
    return s_listPrint;
  }

  //........................................................................

  //------------------------------- getGroup -------------------------------

  /**
    *
    * Get the group this user is in.
    *
    * @return      the group of the user
    *
    */
  public @Nonnull Group getGroup()
  {
    if(m_group.isDefined())
      return m_group.getSelected();

    return Group.GUEST;
  }

  //........................................................................
  //------------------------------- getEMail -------------------------------

  /**
   * Get the users email address.
   *
   * @return      the users email address.
   *
   */
  public @Nonnull String getEMail()
  {
    return m_email.get();
  }

  //........................................................................
  //------------------------------ hasAccess -------------------------------

  /**
   * Checks if the user has at least the given access.
   *
   * @param       inGroup the group to check for
   *
   * @return      true if enough access, false if not
   *
   */
  public boolean hasAccess(@Nonnull Group inGroup)
  {
    return inGroup.allows(getGroup());
  }

  //........................................................................
  //--------------------------------- isDM ---------------------------------

  /**
   * Check whether the given user is the DM for this entry.
   *
   * @param       inUser the user accessing
   *
   * @return      true for DM, false for not
   *
   */
  @Override
public boolean isDM(@Nullable BaseCharacter inUser)
  {
    if(inUser == null)
      return false;

    return inUser.hasAccess(Group.ADMIN) || inUser == this;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- action --------------------------------

  /**
   * The character did or does an action, record this.
   *
   */
  public void action()
  {
    m_lastAction = m_lastAction.as(Strings.today());
    save();
  }

  //........................................................................

  //----------------------------- computeValue -----------------------------

  /**
   * Get a value for printing.
   *
   * @param     inKey  the name of the value to get
   * @param     inDM   true if formattign for dm, false if not
   *
   * @return    a value handle ready for printing
   *
   */
  @Override
  public @Nullable ValueHandle computeValue(@Nonnull String inKey,
                                            boolean inDM)
  {
    if("products".equals(inKey))
    {
      List<Product> products = DMADataFactory.get()
        .getRecentEntries(Product.TYPE, this.getName(),
                          BaseCharacter.TYPE);

      List<Object> commands = new ArrayList<Object>();
      boolean more = products.size() > MAX_PRODUCTS;
      for(int i = 0; i < MAX_PRODUCTS && i < products.size(); i++)
      {
        if(i > 0)
          commands.add(", ");

        Product product = products.get(i);
        commands.add(new Link(product.getFullTitle(), product.getPath()));
      }

      if(more)
        commands.add(" ... ");

      commands.add("| ");
      commands.add(new Link("view all", getPath() + "/products"));

      return new FormattedValue(new Command(commands), null, "products");
    }

    return super.computeValue(inKey, inDM);
  }

  //........................................................................
  //------------------------------ readEntry -------------------------------

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
protected boolean readEntry(@Nonnull ParseReader inReader)
  {
    return super.readEntry(inReader);
  }

  //........................................................................
  //------------------------------- setGroup -------------------------------

  /**
   * Set the group of the user.
   *
   * @param inSelected the selected group
   *
   */
  public void setGroup(@Nonnull Group inSelected)
  {
    m_group = m_group.as(inSelected);
    changed();
  }

  //........................................................................
  //------------------------------- setRealName ----------------------------

  /**
   * Set the real name of the user.
   *
   * @param inRealName the real name of the user
   *
   */
  public void setRealName(@Nonnull String inRealName)
  {
    m_realName = m_realName.as(inRealName);
    changed();
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

    /** The init Test. */
    @org.junit.Test
    public void init()
    {
      BaseCharacter character = new BaseCharacter("Me");

      assertEquals("id", "Me", character.getName());
      assertFalse("real name", character.m_realName.isDefined());
      assertFalse("email", character.m_email.isDefined());
      assertFalse("last action", character.m_lastAction.isDefined());
      assertFalse("group", character.m_group.isDefined());
    }

    //......................................................................
    //----- read -----------------------------------------------------------

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

      net.ixitxachitls.input.ParseReader reader =
        new net.ixitxachitls.input.ParseReader(new java.io.StringReader(text),
                                               "test");

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
                   + "  group             User.\n"
                   + "\n"
                   + "#.....\n",
                   character.toString());
    }

    //......................................................................
    //----- group ----------------------------------------------------------

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

    //......................................................................
  }

  //........................................................................
}
