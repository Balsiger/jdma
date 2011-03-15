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

//import java.util.ArrayList;
//import java.util.Iterator;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//import net.ixitxachitls.dma.Global;
//import net.ixitxachitls.dma.entries.indexes.ExtractorIndex;
//import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Text;
//import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.util.Strings;
//import net.ixitxachitls.util.TypeIterator;
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

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final BaseType<BaseCharacter> TYPE =
    new BaseType<BaseCharacter>(BaseCharacter.class);

  /** The printer for printing the whole base character. */
  public static final Print s_pagePrint =
    new Print("$mainimage $title $clear $files"
              "$id "
              + "\\par and some other text $name ${real name} "
              + "and some "
              + "more $group ${last login} ${last action}"
              + "");

  /** The basic formatter for base characters. */
//   public static final Index.Formatter<AbstractEntry> FORMATTER =
//     new Index.Formatter<AbstractEntry>()
//     {
//     public java.util.List<Object> format(String inKey, AbstractEntry inEntry)
//       {
//         // ignore base values here
//         java.util.List<Object> list = new ArrayList<Object>();

//         if(!(inEntry instanceof BaseCharacter))
//           return list;

//         BaseCharacter character = (BaseCharacter)inEntry;

//         // real name
//         list.add(character.m_realName.format(true));

//         // group
//         list.add(character.m_group.format(true));

//         // last login
//         list.add(character.m_lastLogin.format(true));

//         // last action
//         list.add(character.m_lastAction.format(true));

//         return list;
//       }
//     };

  /** The basic format for base campaigns. */
//   public static final String FORMAT =
//     "20(name)[Real Name];1:L(group)[Group];1:L(last)[Last Login];"
//     + "1:L(action)[Last Action]";

  // the general index with all base campaigns
//   static
//   {
//     s_indexes.add(new ExtractorIndex<ExtractorIndex>
//                   ("index", "index", "basecharacters",
//                    new ExtractorIndex.Extractor()
//                    {
//                      public Object []get(AbstractEntry inEntry)
//                      {
//                        if(!(inEntry instanceof BaseCharacter))
//                          return null;

//                        Object []result = { "base character" };

//                        return result;
//                      }
//                    }, true, FORMATTER, FORMAT, false).withAccess(Group.ADMIN)
//                   .withDataSource(Index.DataSource.user));
//   }

  /** A random generator to create tokens. */
  private static final @Nonnull Random s_random =
    new Random(System.currentTimeMillis());

  /** The length of a token. */
  private static final int TOKEN_LENGTH = 20;

  //----- real name --------------------------------------------------------

  /** The files in the base campaign. */
  @Key("real name")
  protected Text m_realName = new Text();

  //........................................................................
  //----- email ------------------------------------------------------------

  /** The files in the base campaign. */
  @Key("email")
  protected Text m_email = new Text();

  //........................................................................
  //----- password ---------------------------------------------------------

  /** The files in the base campaign. */
  @Key("password")
  protected Text m_password = new Text();

  //........................................................................
  //----- products ---------------------------------------------------------

  /** The files with the products. */
  @Key("products")
  protected Text m_products = new Text();

//   /** The campaign storing all the products. */
//   protected Campaign m_productEntries = new Campaign("Products", "prd", 0);

  //........................................................................
  //----- last login -------------------------------------------------------

  /** The files in the base campaign. */
  @Key("last login")
  protected Text m_lastLogin = new Text();

  //........................................................................
  //----- last action ------------------------------------------------------

  /** The files in the base campaign. */
  @Key("last action")
  protected Text m_lastAction = new Text();

  //........................................................................
  //----- token ------------------------------------------------------------

  /** The files in the base campaign. */
  @Key("token")
  protected Text m_token = new Text();

  //........................................................................
  //----- group ------------------------------------------------------------

  /** The files in the base campaign. */
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

  //------------------------------ printCommand ----------------------------

  /**
    * Print the item to the document, in the general section.
    *
    * @param       inDM   true if set for DM, false for player
    * @param       inEditable true if values are editable, false if not
    *
    * @return      the command representing this item in a list
    *
    */
//   public PrintCommand printCommand(boolean inDM, boolean inEditable)
//   {
//     PrintCommand commands = super.printCommand(inDM, inEditable);

//     commands.type = "character";

//     commands.addValue(m_realName, "real name", inEditable);
//     commands.addValue(m_email, "email", inEditable);
//     commands.addValue(m_group, "group", inEditable);
//     commands.addValue(m_products, "products", inEditable);
//     commands.addValue(m_lastLogin, "last login", false);
//     commands.addValue(m_lastAction, "last action", false);

//     return commands;


// //     // real name
// //     values.add(new Window(new Bold("Real Name:"),
// //                           Config.get("resource:help/label.real.name",
// //                                      "The real name of the user.")));
// //     values.add(m_realName.format(true));

// //     // email
// //     values.add(new Window(new Bold("EMail:"),
// //                           Config.get("resource:help/label.email",
// //                                      "The email address of the user.")));
// //     values.add(m_email.format(true));

// //     // group
// //     values.add(new Window(new Bold("Group:"),
// //                           Config.get("resource:help/label.group",
// //                                      "The access group of the user.")));
// //     values.add(m_group.format(true));

// //     // products
// //     values.add(new Window(new Bold("Products:"),
// //                           Config.get("resource:help/label.products",
// //                                      "The products of the user.")));
// //     values.add(m_products.format(true));

// //     // last login
// //     values.add(new Window(new Bold("Last Login:"),
// //                           Config.get("resource:help/label.last.login",
// //                                      "The data and time this user last "
// //                                      + "logged in.")));
// //     values.add(m_lastLogin.format(true));
//   }

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
  //----------------------------- hasProducts ------------------------------

  /**
    *
    * Check if the base characters has a product file associated with him.
    *
    * @return      true if a file is available, false if not
    *
    */
//   public boolean hasProducts()
//   {
//     return m_products.isDefined();
//   }

  //........................................................................
  //----------------------------- getProducts ------------------------------

  /**
    *
    * Get all the products of this character.
    *
    * @return      an iterator over all the products
    *
    */
//   public Iterator<Product> getProducts()
//   {
//     return new TypeIterator<AbstractEntry, Product>
//       (m_productEntries.getAbstractEntries(), Product.class);
//   }

  //........................................................................
  //--------------------------- getProductData -----------------------------

  /**
    *
    * Get all the products of this character.
    *
    * @return      an iterator over all the products
    *
    * @undefined   never
    *
    */
//   public Campaign getProductData()
//   {
//     return m_productEntries;
//   }

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

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled.
   *
   */
//   public void complete()
//   {
//     super.complete();

//     // read the products file and store the entries found there
//     if(m_products.isDefined())
//     {
//       m_productEntries.readFile(m_products.get(), Global.DATA_DIR);

//       // if the file was completed while reading, save it
//       m_productEntries.write();
//     }
//   }

  //........................................................................
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
  public @Nullable String login(@Nullable String inUsername, @
                                Nullable String inPassword)
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

    if(m_file != null)
      m_file.write();

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
      BaseCharacter character = new BaseCharacter("Me");

      assertEquals("id", "Me", character.getID());
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
      BaseCharacter character = new BaseCharacter("Me");

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
      BaseCharacter character = new BaseCharacter("Me");
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
      BaseCharacter character = new BaseCharacter("Me");

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
