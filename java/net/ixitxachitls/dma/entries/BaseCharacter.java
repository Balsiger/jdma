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
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.dma.data.DMAData;
//import net.ixitxachitls.dma.entries.indexes.ExtractorIndex;
//import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Text;
//import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.util.Strings;
//import net.ixitxachitls.util.TypeIterator;
import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

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
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
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
   * @param   inData all the avaialble data
   *
   */
  protected BaseCharacter(@Nonnull DMAData inData)
  {
    super(TYPE, inData);
  }

  //........................................................................
  //---------------------------- BaseCharacter -----------------------------

  /**
   * This is the standard constructor to create a base character with its
   * name.
   *
   * @param       inName the name of the base charcter to create
   * @param       inData all the avaialble data
   *
   */
  public BaseCharacter(@Nonnull String inName, @Nonnull DMAData inData)
  {
    super(inName, TYPE, inData);
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
              + "%{real name} %email %password %{last login} "
              + "%{last action} %token %group %characters %products "
              + "%errors");

  /** The printer for printing in a list. */
  public static final ListPrint s_listPrint =
    new ListPrint("1:L(label);20:L(name)[Name];20(name)[Real Name];"
                  + "1:L(group)[Group];1:L(last)[Last Login];"
                  + "1:L(action)[Last Action]",
                  "$label $listlink", null, "${real name}", "$group",
                  "${last login}", "${last action}");

  /** A random generator to create tokens. */
  private static final @Nonnull Random s_random =
    new Random(System.currentTimeMillis());

  /** The length of a token. */
  private static final int TOKEN_LENGTH = 20;

  /** The number of recent products to show. */
  private static final int MAX_PRODUCTS =
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
  //----- password ---------------------------------------------------------

  /** The files in the base campaign. */
  @Key("password")
  @DM
  protected @Nonnull Text m_password = new Text();

  //........................................................................
  //----- products ---------------------------------------------------------

  /** All the products for this user. */
  protected @Nullable DMAData m_productData = null;

  //........................................................................
  //----- last login -------------------------------------------------------

  /** The files in the base campaign. */
  @Key("last login")
  @NoEdit
  protected @Nonnull Text m_lastLogin = new Text();

  //........................................................................
  //----- last action ------------------------------------------------------

  /** The files in the base campaign. */
  @Key("last action")
  @NoEdit
  protected @Nonnull Text m_lastAction = new Text();

  //........................................................................
  //----- token ------------------------------------------------------------

  /** The files in the base campaign. */
  @Key("token")
  @DM
  protected @Nonnull Text m_token = new Text();

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
  protected @Nonnull ListPrint getListPrint()
  {
    return s_listPrint;
  }

  //........................................................................

  //------------------------------ checkToken ------------------------------

  /**
    *
    * Check that the given token is valid.
    *
    * @param       inToken the token to check for
    *
    * @return      true if the token is valid, false if not
    *
    */
  public boolean checkToken(@Nullable String inToken)
  {
    if(inToken == null)
      return false;

    if(!m_token.isDefined())
      return false;

    return inToken.equals(m_token.get());
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
  //----------------------------- getProducts ------------------------------

  /**
    *
    * Get all the products of this character.
    *
    * @return      all the products
    *
    */
  public List<Product> getProducts()
  {
    // TODO: this is very inefficient!
    return getProductData().getEntries(Product.TYPE, 0, 0);
  }

  //........................................................................
  //--------------------------- getProductData -----------------------------

  /**
    *
    * Get all the products of this character.
    *
    * @return      all the product information of the user
    *
    * @undefined   never
    *
    */
  public DMAData getProductData()
  {
    if(m_productData == null)
      m_productData = m_data.getUserData(this);

    return m_productData;
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
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    if(inUser == null)
      return false;

    return inUser.hasAccess(Group.ADMIN) || inUser == this;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- login ---------------------------------

  /**
    *
    * Login the user into the system.
    *
    * @param       inUsername the name of the user to login
    * @param       inPassword the password of the user
    *
    * @return      a string with the token to use or null if the password or
    *              username is wrong
    *
    */
  public @Nullable String login(@Nullable String inUsername,
                                @Nullable String inPassword)
  {
    if(inUsername == null || inPassword == null)
      return null;

    if(!inUsername.equals(getName()))
    {
      Log.warning("login with unknown user '" + inUsername + "'");

      return null;
    }

    if(!inPassword.equals(m_password.get()))
    {
      Log.warning("login for '" + inUsername + "' with wrong password");

      return null;
    }

    String token = createToken();

    Log.status("login by '" + inUsername + "' with token '" + token + "'");

    m_token = m_token.as(token);
    m_lastLogin = m_lastLogin.as(Strings.today());
    action();

    // make sure the current file is stored now
    changed();
    save();

    return token;
  }

  //........................................................................
  //------------------------------ clearToken ------------------------------

  /**
    *
    * Clear the token file of the character.
    *
    */
  public void clearToken()
  {
    m_token = m_token.create();

    // make sure the current file is stored now
    changed();

    if(m_file != null)
      m_file.write();
  }

  //........................................................................
  //-------------------------------- action --------------------------------

  /**
   * The character did or does an action, record this.
   *
   */
  public void action()
  {
    m_lastAction = m_lastAction.as(Strings.today());
  }

  //........................................................................

  //----------------------------- setPassword ------------------------------

  /**
   * Set the users password.
   *
   * @param       inPassword the new password
   *
   */
  public void setPassword(@Nonnull String inPassword)
  {
    m_password = m_password.as(inPassword);
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
      List<Product> products = m_data.getRecentEntries(Product.TYPE);

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

      return new FormattedValue(new Command(commands), null, "products", false,
                                false, false, false, null, null);
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
  protected boolean readEntry(@Nonnull ParseReader inReader)
  {
    return super.readEntry(inReader);
  }

  //........................................................................
  //---------------------------- removeProduct -----------------------------

  /**
   * Remove the given product from the list of a user's products.
   *
   * @param       inID the id of the product to remove
   *
   * @return      true if removed, false if not
   *
   */
  public boolean removeProduct(@Nonnull String inID)
  {
    return false;
    //return getProductData().removeEntry(inID, Product.TYPE);
  }

  //........................................................................
  //------------------------------ addProduct ------------------------------

  /**
   * Add the given product to this owner.
   *
   * @param       inProduct the product to add
   *
   */
  public void addProduct(@Nonnull Product inProduct)
  {
    inProduct.setOwner(this);
    getProductData().update(inProduct);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //----------------------------- createToken ------------------------------

  /**
    *
    * Create a random token.
    *
    * @return      a string with a unique token for a user.
    *
    * @undefined   never
    *
    */
  protected String createToken()
  {
    char []token = new char[TOKEN_LENGTH];

    for(int i = 0; i < TOKEN_LENGTH; i++)
      token[i] = (char)(s_random.nextInt(26) + 'A');

    return new String(token);
  }

  //........................................................................

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
      BaseCharacter character =
        new BaseCharacter("Me", new DMAData.Test.Data());

      assertEquals("id", "Me", character.getName());
      assertFalse("real name", character.m_realName.isDefined());
      assertFalse("email", character.m_email.isDefined());
      assertFalse("password", character.m_password.isDefined());
      assertFalse("last login", character.m_lastLogin.isDefined());
      assertFalse("last action", character.m_lastAction.isDefined());
      assertFalse("token", character.m_token.isDefined());
      assertFalse("group", character.m_group.isDefined());
    }

    //......................................................................
    //----- token ----------------------------------------------------------

    /** The token Test. */
    @org.junit.Test
    public void token()
    {
      BaseCharacter character =
        new BaseCharacter("Me", new DMAData.Test.Data());

      String token = character.createToken();
      String token2 = character.createToken();
      assertFalse("new", token.equals(token2));
      assertFalse("check", character.checkToken(token));
      assertFalse("check", character.checkToken(token2));

      character.m_token = character.m_token.as(token);
      assertTrue("check", character.checkToken(token));
      assertFalse("check", character.checkToken(token2));

      character.clearToken();
      assertFalse("check", character.checkToken(token));
      assertFalse("check", character.checkToken(token2));
    }

    //......................................................................
    //----- login ----------------------------------------------------------

    /** The login Test. */
    @org.junit.Test
    public void login()
    {
      BaseCharacter character =
        new BaseCharacter("Me", new DMAData.Test.Data());
      character.m_name = character.m_name.as("user");
      character.m_password = character.m_password.as("password");

      assertEquals("name", "user", character.m_name.get());
      assertEquals("password", "password", character.m_password.get());
      assertFalse("last login", character.m_lastLogin.isDefined());
      assertFalse("last action", character.m_lastAction.isDefined());
      assertFalse("token", character.m_token.isDefined());

      assertNull("empty login", character.login(null, null));
      assertNull("wrong user", character.login("name", "password"));
      assertNull("wrong password", character.login("user", "passwordd"));
      assertTrue("good login", character.login("user", "password") != null);

      assertEquals("name", "user", character.m_name.get());
      assertEquals("password", "password", character.m_password.get());
      assertTrue("last login", character.m_lastLogin.isDefined());
      assertTrue("last action", character.m_lastAction.isDefined());
      assertTrue("token", character.m_token.isDefined());

      m_logger.addExpected("WARNING: login with unknown user 'name'");
      m_logger.addExpected("WARNING: login for 'user' with wrong password");
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
        + "  password      \"carrot\";\n"
        + "  last login    \"yesterday\";\n"
        + "  last action   \"today\";\n"
        + "  token         \"12345678901234567890\";\n"
        + "  group         user.\n"
        + "\n";

      net.ixitxachitls.input.ParseReader reader =
        new net.ixitxachitls.input.ParseReader(new java.io.StringReader(text),
                                               "test");

      BaseCharacter character = (BaseCharacter)
        BaseCharacter.read(reader, new DMAData.Test.Data());

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
                   + "  password          \"carrot\";\n"
                   + "  last login        \"yesterday\";\n"
                   + "  last action       \"today\";\n"
                   + "  token             \"12345678901234567890\";\n"
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
      BaseCharacter character =
        new BaseCharacter("Me", new DMAData.Test.Data());

      assertTrue("guest", character.hasAccess(Group.GUEST));
      assertFalse("user", character.hasAccess(Group.USER));
      assertFalse("player", character.hasAccess(Group.PLAYER));
      assertFalse("dm", character.hasAccess(Group.DM));
      assertFalse("admin", character.hasAccess(Group.ADMIN));

      character.m_group = character.m_group.as(Group.USER);

      assertTrue("guest", character.hasAccess(Group.GUEST));
      assertTrue("user", character.hasAccess(Group.USER));
      assertFalse("player", character.hasAccess(Group.PLAYER));
      assertFalse("dm", character.hasAccess(Group.DM));
      assertFalse("admin", character.hasAccess(Group.ADMIN));

      character.m_group = character.m_group.as(Group.ADMIN);

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
