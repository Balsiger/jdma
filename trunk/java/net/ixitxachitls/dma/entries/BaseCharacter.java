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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * An object of this class represents a real person associated with D&D.
 *
 * @file          BaseCharacter.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BaseCharacter extends BaseEntry
{
  //----------------------------------------------------------------- nested

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

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
    private Group(String inName)
    {
      m_name = constant("group", inName);
    }

    /** The name of the group. */
    private String m_name;

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    @Override
    public String toString()
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
    public boolean allows(Group inGroup)
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
   *
   */
  public BaseCharacter(String inName, String inEmail)
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

  /** The number of recent products to show. */
  public static final int MAX_PRODUCTS =
    Config.get("entries/basecharacter.products", 5);

  //----- real name --------------------------------------------------------

  /** The files in the base campaign. */
  @Key("real name")
  @DM
  protected Text m_realName = new Text();

  //........................................................................
  //----- email ------------------------------------------------------------

  /** The files in the base campaign. */
  @Key("email")
  @DM
  protected Text m_email = new Text();

  //........................................................................
  //----- products ---------------------------------------------------------

  /** All the products for this user. */
  protected @Nullable DMAData m_productData = null;

  //........................................................................
  //----- last action ------------------------------------------------------

  /** The files in the base campaign. */
  @Key("last action")
  @NoEdit
  protected Text m_lastAction = new Text();

  //........................................................................
  //----- group ------------------------------------------------------------

  /** The access group of the user. */
  @Key("group")
  protected EnumSelection<Group> m_group =
    new EnumSelection<Group>(Group.class);

  //........................................................................

  static
  {
    extractVariables(BaseCharacter.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getGroup -------------------------------

  /**
    *
    * Get the group this user is in.
    *
    * @return      the group of the user
    *
    */
  public Group getGroup()
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
  public String getEMail()
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
  public boolean hasAccess(Group inGroup)
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
  //----------------------------- isShownTo -------------------------------

  /**
   * Check if the given user is allowed to see the entry.
   *
   * @param       inUser the user trying to edit
   *
   * @return      true if the entry can be seen, false if not
   *
   */
  @Override
  public boolean isShownTo(@Nullable BaseCharacter inUser)
  {
    return inUser != null;
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
  // @Override
  // public @Nullable ValueHandle computeValue(String inKey,
  //                                           boolean inDM)
  // {
  //   if("products".equals(inKey))
  //   {
  //     List<Product> products = DMADataFactory.get()
  //       .getRecentEntries(Product.TYPE, getKey());

  //     List<Object> commands = new ArrayList<Object>();
  //     boolean more = products.size() > MAX_PRODUCTS;
  //     for(int i = 0; i < MAX_PRODUCTS && i < products.size(); i++)
  //     {
  //       if(i > 0)
  //         commands.add(", ");

  //       Product product = products.get(i);
  //       commands.add(new Link(product.getFullTitle(), product.getPath()));
  //     }

  //     if(more)
  //       commands.add(" ... ");

  //     commands.add("| ");
  //     commands.add(new Link("view all", getPath() + "/products"));

  //     return new FormattedValue(new Command(commands), null, "products");
  //   }

  //   return super.computeValue(inKey, inDM);
  // }

  //........................................................................
  //------------------------------- compute --------------------------------

  /**
   * Compute a value for a given key, taking base entries into account if
   * available.
   *
   * @param    inKey the key of the value to compute
   *
   * @return   the compute value
   *
   */
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
  protected boolean readEntry(ParseReader inReader)
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
  public void setGroup(Group inSelected)
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
  public void setRealName(String inRealName)
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
                   + "  group             User;\n"
                   + "  name              Me.\n"
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
