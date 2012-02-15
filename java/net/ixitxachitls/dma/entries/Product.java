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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.output.ListPrint;
import net.ixitxachitls.dma.output.Print;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.formatters.Formatter;
import net.ixitxachitls.dma.values.formatters.LinkFormatter;
import net.ixitxachitls.dma.values.formatters.MultipleFormatter;
import net.ixitxachitls.output.commands.BaseCommand;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.util.Strings;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a real product.
 *
 * @file          Product.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class Product extends Entry<BaseProduct>
{
  //----------------------------------------------------------------- nested

  //----- status -----------------------------------------------------------

  /** The product status. */
  public enum Status implements EnumSelection.Named
  {
    /** The product is available in the library. */
    AVAILABLE("available"),
    /** A highly desired product. */
    DESIRED1("desired 1"),
    /** A desired product. */
    DESIRED2("desired 2"),
    /** A marginally desired product. */
    DESIRED3("desired 3");

    /** The value's name. */
    private @Nonnull String m_name;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     *
     */
    private Status(@Nonnull String inName)
    {
      m_name = constant("product.status", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Convert to human readable string.
     *
     * @return a human readable string representation
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  };

  //........................................................................
  //----- conditions -------------------------------------------------------

  /** The product condition. */
  public enum Condition implements EnumSelection.Named
  {
    /** The product is as good as new and has not been or only carefully
     * read. */
    MINT("mint"),
    /** The product is in good shape but was read. */
    GOOD("good"),
    /** The product is used, but in good shape. Might have some pencil marks
     * or the like. */
    USED("used"),
    /** The product is usable in play but might not look too nice. */
    USABLE("usable"),
    /** Some part of the product is missing. */
    PARTIAL("partial"),
    /** The product is not really usable. */
    CRAP("crap"),
    /** Nothing defined. */
    none("none");

    /** The value's name. */
    private @Nonnull String m_name;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     *
     */
    private Condition(@Nonnull String inName)
    {
      m_name = constant("product.condition", inName);
    }

    /** Get the name of the value.
     *
     * @return the name of the value
     *
     */
    public @Nonnull String getName()
    {
      return m_name;
    }

    /** Convert to human readable string.
     *
     * @return a human readable string representation
     *
     */
    public @Nonnull String toString()
    {
      return m_name;
    }
  };

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------- Product --------------------------------

  /**
   * This is the internal, default constructor.
   *
   */
  protected Product()
  {
    super(TYPE, BASE_TYPE);
  }

  //........................................................................
  //------------------------------- Product --------------------------------

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base product
   *
   */
  public Product(@Nonnull String inName)
  {
    super(inName, TYPE, BASE_TYPE);
  }

  //........................................................................
  //------------------------------- Product --------------------------------

//   /**
//    * This constructs the product with random values from the given
//    * base product.
//    *
//    * @param       inBase the base product to take values from
//    *
//    * @undefined   never
//    *
//    */
//   public Product(BaseProduct inBase)
//   {
//     super(inBase.getName(), TYPE, BASE_TYPE, inBase);
//   }

//   //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final Type<Product> TYPE =
    new Type<Product>(Product.class, BaseProduct.TYPE);

  /** The type of the base entry. */
  public static final BaseType<BaseProduct> BASE_TYPE = BaseProduct.TYPE;

  /** The print for printing a whole page entry. */
  public static final Print s_pagePrint =
    new Print("$image "
              + "${as pdf} ${as text} ${as dma} "
              + "$title "
              + "$clear "
              + "$files "
              + "\n"
              + "$par "
              + "%name "
              + "%base "
              + "%owner %edition %printing %status %condition"
              // admin
              + "%errors"
              );

  /** The printer for printing in a list. */
  public static final ListPrint s_listPrint =
    new ListPrint("1:L(label);20:L(id)[ID];20(producttitle)[Title];"
                  + "1:L(system)[System];1:L(worlds)[Worlds];"
                  + "1:L(status)[Status]",
                  "$label $listlink", null, "$name", "$+system", "$+worlds",
                  "$status");

  //----- edition ----------------------------------------------------------

  /** The edition of the copy. */
  @Key("edition")
  protected @Nonnull Name m_edition = new Name();

  //........................................................................
  //----- printing ---------------------------------------------------------

  /** The printing of the copy. */
  @Key("printing")
  protected @Nonnull Name m_printing = new Name();

  //........................................................................
  //----- owner ------------------------------------------------------------

  /** The formatter for the owner. */
  protected static final Formatter<Name> s_ownerFormatter =
    new LinkFormatter<Name>("/user/");

  /** The owner of the copy. */
  @Key("owner")
  protected @Nonnull Name m_owner = new Name().withFormatter(s_ownerFormatter);

  //........................................................................
  //----- status -----------------------------------------------------------

  /** The formatter for the status. */
  protected static final Formatter<EnumSelection<Status>> s_statusFormatter =
    new LinkFormatter<EnumSelection<Status>>("/index/status/");

  /** The status of the copy, if its available or not. */
  @Key("status")
  protected EnumSelection<Status> m_status =
    new EnumSelection<Status>(Status.class).withFormatter(s_statusFormatter);

  static
  {
    // s_indexes.add(new KeyIndex<KeyIndex>("Product", "Status", "status",
    //                                      "status", false, FORMATTER, FORMAT,
    //                                      false, null)
    //               .withDataSource(Index.DataSource.products));
  }

  //........................................................................
  //----- condition --------------------------------------------------------

  /** The formatter for a complete person. */
  protected static final Formatter<Multiple> s_condFormatter =
    new MultipleFormatter<Multiple>(null, null, " - ", null);

  /** The condition of the copy. */
  @Key("condition")
  protected Multiple m_condition = new Multiple(new Multiple.Element []
    { new Multiple.Element(new EnumSelection<Condition>(Condition.class)
                           .withEditType("selection[condition]"),
                           false),
      new Multiple.Element(new Text()
                           .withEditType("string[comment]"), true) })
    .withFormatter(s_condFormatter);

  //........................................................................

  static
  {
    extractVariables(Product.class);
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
  //------------------------------- getPath --------------------------------

  /**
   * Get the path to this entry.
   *
   * @return      the path to read this entry
   *
   */
  public @Nonnull String getPath()
  {
    return "/" + BaseCharacter.TYPE.getLink() + "/" + m_owner.get() + "/"
      + getType().getLink() + "/" + getName();
  }

  //........................................................................
  //---------------------------- getNavigation -----------------------------

  /**
   * Get the navigation information to this entry.
   *
   * @return      an array with pairs for caption and link per navigation entry
   *
   */
  public @Nonnull String [] getNavigation()
  {
    return new String [] {
      BaseCharacter.TYPE.getLink(),
      "/" + BaseCharacter.TYPE.getMultipleLink(),
      m_owner.toString(),
      "/" + BaseCharacter.TYPE.getLink() + "/" + m_owner,
      getType().getLink(),
      "/" + BaseCharacter.TYPE.getLink() + "/" + m_owner
      + "/" + getType().getMultipleLink(),
      getName(),
      "/" + BaseCharacter.TYPE.getLink() + "/" + m_owner
      + "/" + getType().getLink() + "/" + getName(),
    };
  }

  //........................................................................
  //-------------------------- getListNavigation ---------------------------

  /**
   * Get the list navigation information to this entry.
   *
   * @return      an array with pairs for caption and link per navigation entry
   *
   */
  public @Nonnull String [] getListNavigation()
  {
    return new String [] {
      BaseCharacter.TYPE.getLink(),
      "/" + BaseCharacter.TYPE.getMultipleLink(),
      m_owner.toString(),
      "/" + BaseCharacter.TYPE.getLink() + "/" + m_owner,
      getType().getMultipleLink(),
      "/" + BaseCharacter.TYPE.getLink() + "/" + m_owner
      + "/" + getType().getMultipleLink(),
    };
  }

  //........................................................................
  //----------------------------- getEditType ------------------------------

  /**
   * Get the type of the entry.
   *
   * @return      the requested name
   *
   */
  public @Nonnull String getEditType()
  {
    return "/user/" + m_owner.get() + "/" + super.getEditType();
  }

  //........................................................................

  //--------------------------------- isDM ---------------------------------

  /**
   * Check whether the given user is the DM for this entry. Everybody is a DM
   * for a base product.
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

    return inUser.getName().equals(m_owner.get());
  }

  //........................................................................


//   //----------------------------- getEdition -------------------------------

//   /**
//     *
//     * Get the edition of the product.
//     *
//     * @return      the edition value
//     *
//     * @undefined   never (a value is always returned)
//     *
//     * @algorithm   simple accessor
//     *
//     * @derivation  possible for storage changes
//     *
//     * @example     Selection edition = product.getEdition();
//     *
//     * @bugs
//     * @to_do
//     *
//     * @keywords    get . edition;
//     *
//     * @see         #setEdition
//     *
//     */
//   public String getEdition()
//   {
//     return m_edition.get();
//   }

//   //........................................................................
//   //----------------------------- getPrinting ------------------------------

//   /**
//     *
//     * Get the printing of the product.
//     *
//     * @return      the printing value
//     *
//     * @undefined   never (a value is always returned)
//     *
//     * @algorithm   simple accessor
//     *
//     * @derivation  possible for storage changes
//     *
//     * @example     Selection printing = product.getPrinting();
//     *
//     * @bugs
//     * @to_do
//     *
//     * @keywords    get . printing;
//     *
//     * @see         #setPrinting
//     *
//     */
//   public String getPrinting()
//   {
//     return m_printing.get();
//   }

//   //........................................................................
  //------------------------------ getOwner --------------------------------

  /**
   * Get the name of the owner of the product.
   *
   * @return      the owner of the product, if any
   *
   */
  public @Nullable String getOwner()
  {
    if(m_owner.isDefined())
      return m_owner.get();

    return null;
  }

  //........................................................................
//   //----------------------------- getStatus --------------------------------

//   /**
//     *
//     * Get the status of the product.
//     *
//     * @return      the status value
//     *
//     * @undefined   never (a value is always returned)
//     *
//     * @algorithm   simple accessor
//     *
//     * @derivation  possible for storage changes
//     *
//     * @example     Selection status = product.getStatus();
//     *
//     * @bugs
//     * @to_do
//     *
//     * @keywords    get . status;
//     *
//     * @see         #setStatus
//     *
//     */
//   public Product.Status getStatus()
//   {
//     return m_status.getSelected();
//   }

//   //........................................................................
//   //---------------------------- getCondition ------------------------------

//   /**
//    * Get the condition of the product.
//    *
//    * @return      the condition value
//    *
//    * @undefined   never (a value is always returned)
//    *
//    */
//   @SuppressWarnings("unchecked") // cast for enum selection
//   public Condition getCondition()
//   {
//  return ((EnumSelection<Condition>)m_condition.get(0).get()).getSelected();
//   }

//   //........................................................................
//   //------------------------ getConditionRemarks ---------------------------

//   /**
//    * Get the condition remarks of the product.
//    *
//    * @return      the condition remarks value
//    *
//    * @undefined   never (a value is always returned)
//    *
//    */
//   public String getConditionRemarks()
//   {
//     return ((Text)m_condition.get(1).get()).get();
//   }

//   //........................................................................
//   //------------------------------- getStyle -------------------------------

//   /**
//    * Get the style of the product.
//    *
//    * @return      the requested style or null for undefined
//    *
//    */
//   public @MayReturnNull BaseProduct.Style getStyle()
//   {
//     return getBaseValue(new Extractor<BaseProduct, BaseProduct.Style>()
//                         {
//                           public BaseProduct.Style get(BaseProduct inBase)
//                           {
//                             return inBase.getStyle();
//                           }
//                      }, new Combiner<BaseProduct.Style, BaseProduct.Style>()
//                         {
//                           public BaseProduct.Style combine
//                          (BaseProduct.Style inOld, BaseProduct.Style inNew)
//                           {
//                             if(inNew.ordinal() > inOld.ordinal())
//                               return inNew;

//                             return inOld;
//                           }
//                         });
//   }

//   //........................................................................
//   //---------------------------- getProductType ----------------------------

//   /**
//    * Get the type of the product.
//    *
//    * @return      the requested type or null for undefined
//    *
//    */
//   public @MayReturnNull BaseProduct.ProductType getProductType()
//   {
//   return getBaseValue(new Extractor<BaseProduct, BaseProduct.ProductType>()
//                         {
//                       public BaseProduct.ProductType get(BaseProduct inBase)
//                           {
//                             return inBase.getProductType();
//                           }
//                         }, new Combiner<BaseProduct.ProductType,
//                                         BaseProduct.ProductType>()
//                         {
//                           public BaseProduct.ProductType combine
//                             (BaseProduct.ProductType inOld,
//                              BaseProduct.ProductType inNew)
//                           {
//                             if(inNew.ordinal() > inOld.ordinal())
//                               return inNew;

//                             return inOld;
//                           }
//                         });
//   }

//   //........................................................................
//   //------------------------------- getSystem ------------------------------

//   /**
//    * Get the system of the product.
//    *
//    * @return      the requested system or null for undefined
//    *
//    */
//   public @MayReturnNull BaseProduct.System getSystem()
//   {
//     return getBaseValue(new Extractor<BaseProduct, BaseProduct.System>()
//                         {
//                           public BaseProduct.System get(BaseProduct inBase)
//                           {
//                             return inBase.getSystem();
//                           }
//                         }, new Combiner<BaseProduct.System,
//                                         BaseProduct.System>()
//                         {
//                           public BaseProduct.System combine
//                         (BaseProduct.System inOld, BaseProduct.System inNew)
//                           {
//                             if(inNew.ordinal() > inOld.ordinal())
//                               return inNew;

//                             return inOld;
//                           }
//                         });
//   }

//   //........................................................................
//   //------------------------------- getSystem ------------------------------

//   /**
//    * Get the product volume.
//    *
//    * @return      the requested volume
//    *
//    */
//   public String getVolume()
//   {
//     return getBaseValue(new Extractor<BaseProduct, String>()
//                         {
//                           public String get(BaseProduct inBase)
//                           {
//                             return inBase.getVolume();
//                           }
//                         }, new Combiner<String, String>()
//                         {
//                           public String combine(String inOld, String inNew)
//                           {
//                             return inOld + " " + inNew;
//                           }
//                         });
//   }

//   //........................................................................
//   //------------------------------ getSeries -------------------------------

//   /**
//    * Get all the series.
//    *
//    * @return      a list with all the series
//    *
//    */
//   public List<String> getSeries()
//   {
//     return getBaseValues(new Extractor<BaseProduct, String []>()
//                          {
//                            public String []get(BaseProduct inBase)
//                            {
//                              return inBase.getSeries().split("\\s*,\\s*");
//                            }
//                          }, new Combiner<List<String>, String []>()
//                          {
//                            public List<String> combine
//                              (List<String> ioOld, String []inNew)
//                            {
//                              for(String value : inNew)
//                                ioOld.add(value);

//                              return ioOld;
//                            }
//                          }, new ArrayList<String>());
//   }

//   //........................................................................
//   //---------------------------- getNumberValue ----------------------------

//   /**
//    * Get the series number of the product as an integer.
//    *
//    * @return      the requested series number
//    *
//    */
//   public int getNumberValue()
//   {
//     Integer result =
//       getBaseValue(new Extractor<BaseProduct, Integer>()
//                    {
//                      public Integer get(BaseProduct inBase)
//                      {
//                        return inBase.getNumberValue();
//                           }
//                    }, new Combiner<Integer, Integer>()
//                    {
//                      public Integer combine(Integer inOld, Integer inNew)
//                      {
//                        if(inNew > inOld)
//                          return inNew;

//                        return inOld;
//                      }
//                    });

//     if(result == null)
//       return 0;

//     return result;
//   }

//   //........................................................................
//   //-------------------------------- getTitle ------------------------------

//   /**
//    * Get the title of the product.
//    *
//    * @return      the requested title or null if undefined
//    *
//    */
//   public @MayReturnNull String getTitle()
//   {
//     return getBaseValue(new Extractor<BaseProduct, String>()
//                         {
//                           public String get(BaseProduct inBase)
//                           {
//                             return inBase.getTitle();
//                           }
//                         }, COMBINER_KEEP_FIRST);
//   }

//   //.......................................................................
  //------------------------------ getFullTitle ----------------------------

  /**
   * Get the full title of the product.
   *
   * @return      the requested title or null if undefined
   *
   */
  public @Nonnull String getFullTitle()
  {
    for(BaseEntry base : getBaseEntries())
    {
      if(!(base instanceof BaseProduct))
        continue;

      String title = ((BaseProduct)base).getFullTitle();
      if(!title.isEmpty())
        return title;
    }

    return getName();
  }

  //........................................................................

//   //----------------------------- hasSeries ------------------------------

//   /**
//    * Check if the entry's base has a named series.
//    *
//    * @return      true if the series is present, false if not
//    *
//    */
//   public boolean hasSeries()
//   {
//     if(m_baseEntries == null)
//       return false;

//     for(BaseEntry base : m_baseEntries)
//       if(base != null && ((BaseProduct)base).hasSeries())
//         return true;

//     return false;
//   }

//   //........................................................................

//   //----------------------------- matches --------------------------------

//   /**
//    * Check if this entry matches the given search string or pattern.
//    *
//    * @param       inPattern the pattern to search for
//    *
//    * @return      true if it matches, false if not
//    *
//    * @undefined   never
//    *
//    */
//   public boolean matches(String inPattern)
//   {
//     if(super.matches(inPattern))
//       return true;

//     if(m_baseEntries != null)
//       for(BaseEntry base : m_baseEntries)
//         if(base != null)
//           if(!base.matches(inPattern))
//             return false;

//     return true;
//   }

//   //........................................................................

  //........................................................................

  //-------------------------------- getKey --------------------------------

  /**
   * Get the key uniqueliy identifying this entry.
   *
   * @return   the key
   *
   */
  @Override
  public @Nonnull EntryKey<Product> getKey()
  {
    return new EntryKey<Product>(getName(), Product.TYPE,
                        new EntryKey<BaseCharacter>(getOwner(),
                                                    BaseCharacter.TYPE));
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
  public @Nullable ValueHandle computeValue(@Nonnull String inKey, boolean inDM)
  {
    if("name".equals(inKey) && m_baseEntries != null
       && m_baseEntries.size() > 0)
    {
      BaseProduct base = (BaseProduct)m_baseEntries.get(0);

      if(base != null)
        return new FormattedValue
          (new Command(new Link(new BaseCommand(base.getFullTitle()),
                                "/product/" + base.getName()),
                       " (",
                       getName(),
                       ")"), getName(), "name")
          .withPlayerEditable(true)
          .withEditable(true)
          .withEditType("name");
    }

    // we compute the owner here, as we don't want to get all ids if we don't
    // have to
    if("owner".equals(inKey) && m_owner != null)
    {
      String users = Strings.toString(DMADataFactory.get()
                                      .getIDs(BaseCharacter.TYPE), "||",
                                      m_owner.get());
      return new FormattedValue(m_owner, m_owner.get(), "owner")
        .withDM(true)
        .withEditable(true)
        .withEditType("selection").withEditChoices(users);
    }

    return super.computeValue(inKey, inDM);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

//   //----------------------------- setEdition ------------------------------

//   /**
//    * Set the edition of the product.
//    *
//    * @param       inEdition the edition
//    *
//    * @return      true if set, false if not
//    *
//    * @undefined   never
//    *
//    */
//   public boolean setEdition(String inEdition)
//   {
//     m_edition.set(inEdition);

//     return true;
//   }

//   //........................................................................
//   //---------------------------- setPrinting ------------------------------

//   /**
//    * Set the printing of the product.
//    *
//    * @param       inPrinting the printing
//    *
//    * @return      true if set, false if not
//    *
//    * @undefined   never
//    *
//    */
//   public boolean setPrinting(String inPrinting)
//   {
//     m_printing.set(inPrinting);

//     return true;
//   }

//   //........................................................................
    //------------------------------- setOwner -------------------------------

  /**
   * Set the owner of the entry.
   *
   * @param       inOwner the owning entry
   *
   */
  public void setOwner(@Nonnull AbstractEntry inOwner)
  {
    if(inOwner instanceof BaseCharacter)
      setOwner((BaseCharacter)inOwner);
  }

  //........................................................................
  //------------------------------ setOwner -------------------------------

  /**
   * Set the owner of the product.
   *
   * @param       inOwner the owner
   *
   * @return      true if set, false if not
   *
   */
  public boolean setOwner(@Nonnull BaseCharacter inOwner)
  {
    m_owner = m_owner.as(inOwner.getName());
    return true;
  }

  //........................................................................
//   //----------------------------- setStatus -------------------------------

//   /**
//    * Set the status of the product.
//    *
//    * @param       inStatus the status
//    *
//    * @return      true if set, false if not
//    *
//    * @undefined   never
//    *
//    */
//   public boolean setStatus(Status inStatus)
//   {
//     return m_status.set(inStatus);
//   }

//   //........................................................................
//   //---------------------------- setCondition -----------------------------

//   /**
//    * Set the condition of the product.
//    *
//    * @param       inCondition the condition to set to
//    * @param       inRemarks   the remarks about the condition
//    *
//    * @return      true if set, false if not
//    *
//    * @undefined   assertion if null given
//    *
//    */
//   @SuppressWarnings("unchecked") // casting to enum selection
//   public boolean setCondition(Condition inCondition, String inRemarks)
//   {
//     if(inCondition == null)
//       return false;

//     if(!((EnumSelection<Condition>)m_condition.get(0).getMutable())
//        .set(inCondition))
//       return false;

//     if(inRemarks != null)
//       ((Text)m_condition.get(1).getMutable()).set(inRemarks);
//     else
//       m_condition.get(1).getMutable().reset();

//     return true;
//   }

//   //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

//   /** The test.
//    *
//    * @hidden
//    *
//    */
//   public static class Test extends ValueGroup.Test
//   {
//     //----- text -----------------------------------------------------------

//     /** Text for testing. */
//     private static String s_text =
//       "#------ WTC 88686 ---------------------------------------------\n"
//       + "\n"
//       + "product WTTC 88686 = \n"
//       + "\n"
//       + "  edition       1st;\n"
//       + "  printing      1st;\n"
//       + "  owner         Merlin;\n"
//       + "  status        available;\n"
//       + "  condition     good \"some test things\".\n"
//       + "\n"
//       + "#..............................................................";

//     //......................................................................
//     //----- read -----------------------------------------------------------

//     /** Testing reading. */
//     public void testRead()
//     {
//       ParseReader reader =
//         new ParseReader(new java.io.StringReader(s_text), "test");

//       String result =
//         "#------ WTC 88686 ---------------------------------------------\n"
//         + "\n"
//         + "product WTTC 88686 [WTTC 88686] =\n"
//         + "\n"
//         + "  edition   1st;\n"
//         + "  printing  1st;\n"
//         + "  owner     Merlin;\n"
//         + "  status    available;\n"
//         + "  condition good \"some test things\".\n"
//         + "\n"
//       + "#..............................................................\n";

//       Product entry = (Product)Product.read(reader);

//       assertNotNull("product should have been read", entry);
//       assertEquals("product name does not match", "WTTC 88686",
//                    entry.getName());
//       assertEquals("product does not match", result, entry.toString());

//       m_logger.addExpectedPattern("WARNING: base.not-found:.*"
//                                   + "(base name 'WTTC 88686').*");
//     }

//     //......................................................................
//     //----- print ----------------------------------------------------------

//     /** Testing printing. */
//     public void testPrint()
//     {
//       BaseProduct base = new BaseProduct("some product");

//       base.setShortDescription("short");
//       base.setWorld("Forgotten Realms");
//       base.setSystem(BaseProduct.System.DnD_3RD);
//       base.setProductType(BaseProduct.ProductType.ADVENTURE);
//       base.setStyle(BaseProduct.Style.HARDCOVER);

//       Product product = new Product(base);

//       assertTrue("owner", product.setOwner("Merlin"));
//       assertTrue("status", product.setStatus(Status.DESIRED2));
//       assertTrue("edition", product.setEdition("2nd"));
//       assertTrue("printing", product.setPrinting("3rd"));

//       PrintCommand command = product.printCommand(true, false);

//       assertNotNull("command", command);

//       // values
//       Command values =
//         product.getCommand(command,
//                          new Command("%{id, notes, owner, status, edition, "
//                                        + "printing}"), true);

//       assertEquals("id", "Id:", extract(values, 1, 1, 1));
//       assertEquals("id", "some product", extract(values, 2, 1, 1));
//       assertEquals("notes", "Notes:", extract(values, 3, 1, 1));
//       assertEquals("notes", "", extract(values, 4));
//       assertEquals("owner", "Owner:", extract(values, 5, 1, 1));
//       assertEquals("owner", "Merlin", extract(values, 6, 1, 2));
//       assertEquals("status", "Status:", extract(values, 7, 1, 1));
//       assertEquals("status", "link", extract(values, 8, 1, 2, 0));
//       assertEquals("status", "/index/status/desired 2",
//                    extract(values, 8, 1, 2, -1));
//       assertEquals("status", "desired 2", extract(values, 8, 1, 2, 1));
//       assertEquals("edition", "Edition:", extract(values, 9, 1, 1));
//       assertEquals("edition", "2nd", extract(values, 10, 1, 2));
//       assertEquals("printing", "Printing:", extract(values, 11, 1, 1));
//       assertEquals("printing", "3rd", extract(values, 12, 1, 2));
//     }

//     //......................................................................
//     //----- format ---------------------------------------------------------

//     /** Check format for overview. */
//     public void testFormat()
//     {
//       BaseProduct base = new BaseProduct("some product");

//       base.setShortDescription("short");
//       base.setWorld("Forgotten Realms");
//       base.setSystem(BaseProduct.System.DnD_3RD);
//       base.setProductType(BaseProduct.ProductType.ADVENTURE);
//       base.setStyle(BaseProduct.Style.HARDCOVER);

//       Product product = new Product(base);

//       assertTrue("id", product.setID("test"));
//       assertTrue("owner", product.setOwner("Merlin"));
//       assertTrue("status", product.setStatus(Status.DESIRED2));
//       assertTrue("edition", product.setEdition("2nd"));
//       assertTrue("printing", product.setPrinting("3rd"));

//       java.util.List<Object> list = FORMATTER.format("key", product);

//       assertEquals("label", "label", extract(list.get(0), 0));
//       assertEquals("label", "Product", extract(list.get(0), 1));
//       assertEquals("title", "link", extract(list.get(1), 0));
//     assertEquals("title", "/entry/product/test", extract(list.get(1), -1));
//       assertEquals("title", "key", extract(list.get(1), 1));
//       assertEquals("world", "short", extract(list.get(2)));
//       assertEquals("world", "link", extract(list.get(3), 0));
//       assertEquals("world", "/entry/baseproduct/some product",
//                    extract(list.get(3), -1));
//       assertEquals("world", "Forgotten Realms", extract(list.get(4)));
//       assertEquals("system", "D&D 3rd", extract(list.get(5)));
//       assertEquals("type", "Adventure", extract(list.get(6)));
//       assertEquals("style", "Hardcover", extract(list.get(7)));
//     }

//     //......................................................................
//     //----- get/set --------------------------------------------------------

//     /** Test getting and setting. */
//     public void testGetSet()
//     {
//       Product product = new Product("test");

//       assertTrue("edition", product.setEdition("1st"));
//       assertEquals("edition", "1st", product.getEdition());
//       assertTrue("printing", product.setPrinting("2nd"));
//       assertEquals("printing", "2nd", product.getPrinting());
//       assertTrue("owner", product.setOwner("Merlin"));
//       assertEquals("owner", "Merlin", product.getOwner());
//       assertTrue("status", product.setStatus(Status.AVAILABLE));
//       assertEquals("status", Status.AVAILABLE, product.getStatus());
//       assertTrue("condition",
//               product.setCondition(Condition.MINT, "brand new, of course"));
//       assertEquals("condition", Condition.MINT, product.getCondition());
//       assertEquals("condition", "brand new, of course",
//                    product.getConditionRemarks());
//     }

//     //......................................................................
//   }

  //........................................................................
}
