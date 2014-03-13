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

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.EntryProto;
import net.ixitxachitls.dma.proto.Entries.ProductProto;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.util.logging.Log;

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

@ParametersAreNonnullByDefault
public class Product extends Entry<BaseProduct>
{
  //----------------------------------------------------------------- nested

  //----- status -----------------------------------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The product status. */
  public enum Status implements EnumSelection.Named,
    EnumSelection.Proto<ProductProto.Status>
  {
    /** The product is available in the library. */
    AVAILABLE("available", ProductProto.Status.AVAILABLE),
    /** A highly desired product. */
    DESIRED1("desired 1", ProductProto.Status.DESIRED_1),
    /** A desired product. */
    DESIRED2("desired 2", ProductProto.Status.DESIRED_2),
    /** A marginally desired product. */
    DESIRED3("desired 3", ProductProto.Status.DESIRED_3);

    /** The value's name. */
    private String m_name;

    /** The proto enum value. */
    private ProductProto.Status m_proto;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the proto enum value
     */
    private Status(String inName, ProductProto.Status inProto)
    {
      m_name = constant("product.status", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public ProductProto.Status toProto()
    {
      return m_proto;
    }

    /**
     * Convert the proto value into the corresponding enum value.
     *
     * @param inProto the proto to convert
     * @return the corresponding enum value
     */
    public static Status fromProto(ProductProto.Status inProto)
    {
      for(Status status : values())
        if(status.m_proto == inProto)
          return status;

      throw new IllegalArgumentException("cannot convert status proto: "
                                         + inProto);
    }
  }

  //........................................................................
  //----- conditions -------------------------------------------------------

  /** The product condition. */
  public enum Condition implements EnumSelection.Named,
    EnumSelection.Proto<ProductProto.Condition>
  {
    /** The product is as good as new and has not been or only carefully
     * read. */
    MINT("mint", ProductProto.Condition.MINT),
    /** The product is in good shape but was read. */
    GOOD("good", ProductProto.Condition.GOOD),
    /** The product is used, but in good shape. Might have some pencil marks
     * or the like. */
    USED("used", ProductProto.Condition.USED),
    /** The product is usable in play but might not look too nice. */
    USABLE("usable", ProductProto.Condition.USABLE),
    /** Some part of the product is missing. */
    PARTIAL("partial", ProductProto.Condition.PARTIAL),
    /** The product is not really usable. */
    CRAP("crap", ProductProto.Condition.CRAP),
    /** Nothing defined. */
    none("none", ProductProto.Condition.NONE);

    /** The value's name. */
    private String m_name;

    /** The enum proto value. */
    private ProductProto.Condition m_proto;

    /**
     * Create the name.
     *
     * @param inName     the name of the value
     * @param inProto    the proto value
     */
    private Condition(String inName, ProductProto.Condition inProto)
    {
      m_name = constant("product.condition", inName);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public ProductProto.Condition toProto()
    {
      return m_proto;
    }

    public static Condition fromProto(ProductProto.Condition inProto)
    {
      for(Condition condition : values())
        if(condition.m_proto == inProto)
          return condition;

      throw new IllegalArgumentException("cannot convert condition: "
                                         + inProto);
    }
  }

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
  public Product(String inName)
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

  //----- edition ----------------------------------------------------------

  /** The edition of the copy. */
  @Key("edition")
  protected Name m_edition = new Name();

  //........................................................................
  //----- printing ---------------------------------------------------------

  /** The printing of the copy. */
  @Key("printing")
  protected Name m_printing = new Name();

  //........................................................................
  //----- owner ------------------------------------------------------------

  /** The owner of the copy. */
  @Key("owner")
  protected Name m_owner = new Name();

  //........................................................................
  //----- status -----------------------------------------------------------

  /** The status of the copy, if its available or not. */
  @Key("status")
  protected EnumSelection<Status> m_status =
    new EnumSelection<Status>(Status.class);

  //........................................................................
  //----- condition --------------------------------------------------------

  /** The condition of the copy. */
  @Key("condition")
  protected Multiple m_condition =
    new Multiple(new Multiple.Element []
    { new Multiple.Element(new EnumSelection<Condition>(Condition.class)
                           .withEditType("selection[condition]"),
                           false),
      new Multiple.Element(new Text()
                           .withEditType("string[comment]"), true) });

  //........................................................................

  static
  {
    extractVariables(Product.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getPath --------------------------------

  /**
   * Get the path to this entry.
   *
   * @return      the path to read this entry
   *
   */
  @Override
  public String getPath()
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
  @Override
  public String [] getNavigation()
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
  @Override
  public String [] getListNavigation()
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
  @Override
  public String getEditType()
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
  @Override
  public boolean isDM(@Nullable BaseCharacter inUser)
  {
    if(inUser == null)
      return false;

    return inUser.getName().equalsIgnoreCase(m_owner.get());
  }

  //........................................................................


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
  //------------------------------ getFullTitle ----------------------------

  /**
   * Get the full title of the product.
   *
   * @return      the requested title or null if undefined
   *
   */
  public String getFullTitle()
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

  //........................................................................

  //-------------------------------- getKey --------------------------------

  /**
   * Get the key uniqueliy identifying this entry.
   *
   * @return   the key
   *
   */
  @Override
  @SuppressWarnings("unchecked")
  public EntryKey<Product> getKey()
  {
    return new EntryKey<Product>
      (getName(), Product.TYPE,
       new EntryKey<BaseCharacter>(getOwner(), BaseCharacter.TYPE));
  }

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
    if("navigation".equals(inKey))
    {
      List<Object> list = new ArrayList<Object>();

      list.add(new ImmutableMap.Builder<String, Object>()
               .put("name", m_owner.get())
               .put("path", "/user/" + m_owner.get())
               .build());
      list.add(this);

      return list;
    }

    if("fullTitle".equals(inKey))
      return getFullTitle();

    return super.compute(inKey);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------ updateKey -------------------------------

  /**
   * Update the any values that are related to the key with new data.
   *
   * @param       inKey the new key of the entry
   *
   */
  @Override
  public void updateKey(EntryKey<? extends AbstractEntry> inKey)
  {
    EntryKey<?> parent = inKey.getParent();
    if(parent == null)
      return;

    m_owner = m_owner.as(parent.getID());
  }

  //........................................................................
  //--------------------------------- save ---------------------------------

  /**
   * Save the entry if it has been changed.
   *
   * @return      true if saved, false if not
   *
   */
  @Override
  public boolean save()
  {
    if(m_name.startsWith(Entry.TEMPORARY))
      do
      {
        randomID();
      } while(DMADataFactory.get().getEntry(getKey()) != null);

    return super.save();
  }

  //........................................................................

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
  @Override
  public void setOwner(AbstractEntry inOwner)
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
  public boolean setOwner(BaseCharacter inOwner)
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

  @SuppressWarnings("unchecked")
  @Override
  public Message toProto()
  {
    ProductProto.Builder builder = ProductProto.newBuilder();

    builder.setBase((EntryProto)super.toProto());

    if(m_edition.isDefined())
      builder.setEdition(m_edition.get());

    if(m_printing.isDefined())
      builder.setPrinting(m_printing.get());

    if(m_owner.isDefined())
      builder.setOwner(m_owner.get());

    if(m_status.isDefined())
      builder.setStatus(m_status.getSelected().toProto());

    if(m_condition.get(0).isDefined())
      builder.setCondition(((EnumSelection<Condition>)m_condition.get(0))
                           .getSelected().toProto());

    if(m_condition.get(1).isDefined())
      builder.setConditionComment(((Text)m_condition.get(1)).get());

    return builder.build();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof ProductProto))
    {
      Log.warning("cannot parse proto " + inProto);
      return;
    }

    ProductProto proto = (ProductProto)inProto;

    if(proto.hasEdition())
      m_edition = m_edition.as(proto.getEdition());

    if(proto.hasPrinting())
      m_printing = m_printing.as(proto.getPrinting());

    if(proto.hasOwner())
      m_owner = m_owner.as(proto.getOwner());

    if(proto.hasStatus())
      m_status = m_status.as(Status.fromProto(proto.getStatus()));

    if(proto.hasCondition() || proto.hasConditionComment())
      m_condition =
        m_condition.as(proto.hasCondition()
                       ? ((EnumSelection<Condition>)m_condition.get(0))
                         .as(Condition.fromProto(proto.getCondition()))
                       : m_condition.get(0),
                       proto.hasConditionComment()
                       ? ((Text)m_condition.get(1))
                         .as(proto.getConditionComment())
                       : m_condition.get(1));

    super.fromProto(proto.getBase());
  }

  @Override
  public void parseFrom(byte []inBytes)
  {
    try
    {
      fromProto(ProductProto.parseFrom(inBytes));
    }
    catch(InvalidProtocolBufferException e)
    {
      Log.warning("could not properly parse proto: " + e);
    }
  }

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
