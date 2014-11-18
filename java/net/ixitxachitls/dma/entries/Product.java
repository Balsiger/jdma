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

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.proto.Entries.EntryProto;
import net.ixitxachitls.dma.proto.Entries.ProductProto;
import net.ixitxachitls.dma.values.ProductStatus;
import net.ixitxachitls.dma.values.enums.ProductCondition;
import net.ixitxachitls.util.logging.Log;

/**
 * This is a real product.
 *
 * @file          Product.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public class Product extends Entry
{
  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * This is the internal, default constructor.
   */
  protected Product()
  {
    super(TYPE);
  }

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base product
   */
  public Product(String inName)
  {
    super(inName, TYPE);
  }

  /** The type of this entry. */
  public static final Type<Product> TYPE =
    new Type<Product>(Product.class, BaseProduct.TYPE);

  /** The edition of the copy. */
  protected Optional<String> m_edition = Optional.absent();

  /** The printing of the copy. */
  protected Optional<String> m_printing = Optional.absent();

  /** The owner of the copy. */
  protected Optional<String> m_owner = Optional.absent();

  /** The status of the copy, if its available or not. */
  protected ProductStatus m_status = ProductStatus.UNKNOWN;

  /** The condition of the copy. */
  protected ProductCondition m_condition = ProductCondition.UNKNOWN;

  /** The comment for the condition. */
  protected Optional<String> m_conditionComment = Optional.absent();

  /**
   * Get the edition of the product.
   *
   * @return      the edition
   */
  public Optional<String> getEdition()
  {
    return m_edition;
  }

  public Optional<String> getPrinting()
  {
    return m_printing;
  }

  public Optional<String> getOwner()
  {
    return m_owner;
  }

  public ProductStatus getStatus()
  {
    return m_status;
  }

  public ProductCondition getCondition()
  {
    return m_condition;
  }

  public Optional<String> getConditionComment()
  {
    return m_conditionComment;
  }

  @Override
  public String getPath()
  {
    return "/" + BaseCharacter.TYPE.getLink() + "/" + m_owner.get() + "/"
      + getType().getLink() + "/" + getName();
  }

  @Override
  public List<Link> getNavigation()
  {
    return ImmutableList.of
      (new Link(BaseCharacter.TYPE.getLink(),
                "/" + BaseCharacter.TYPE.getMultipleLink()),
      new Link(m_owner.toString(),
               "/" + BaseCharacter.TYPE.getLink() + "/" + m_owner),
      new Link(getType().getLink(),
               "/" + BaseCharacter.TYPE.getLink() + "/" + m_owner
               + "/" + getType().getMultipleLink()),
      new Link(getName(),
               "/" + BaseCharacter.TYPE.getLink() + "/" + m_owner
               + "/" + getType().getLink() + "/" + getName()));
  }

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

  @Override
  public String getEditType()
  {
    return "/user/" + m_owner.get() + "/" + super.getEditType();
  }

  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().getName().equalsIgnoreCase(m_owner.get());
  }

  /**
   * Get the full title of the product.
   *
   * @return      the requested title or null if undefined
   */
  public String getFullTitle()
  {
    for(BaseEntry base : getBaseEntries())
    {
      String title = ((BaseProduct)base).getFullTitle();
      if(!title.isEmpty())
        return title;
    }

    return getName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public EntryKey getKey()
  {
    if(m_owner.isPresent())
      return new EntryKey(getName(), Product.TYPE,
                          Optional.of(new EntryKey(m_owner.get(),
                                                   BaseCharacter.TYPE)));

    return new EntryKey(getName(), Product.TYPE);
  }

  @Override
  public void updateKey(EntryKey inKey)
  {
    Optional<EntryKey> parent = inKey.getParent();
    if(!parent.isPresent())
      return;

    m_owner = Optional.of(parent.get().getID());
  }

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

  @Override
  public void setOwner(AbstractEntry inOwner)
  {
    if(inOwner instanceof BaseCharacter)
      setOwner((BaseCharacter)inOwner);
  }

  /**
   * Set the owner of the product.
   *
   * @param       inOwner the owner
   *
   * @return      true if set, false if not
   */
  public boolean setOwner(BaseCharacter inOwner)
  {
    m_owner = Optional.of(inOwner.getName());
    return true;
  }

  @Override
  public void set(Values inValues)
  {
    super.set(inValues);

    m_owner = inValues.use("owner",  m_owner);
    m_edition = inValues.use("edition", m_edition);
    m_printing = inValues.use("printing", m_printing);
    m_status = inValues.use("status", m_status, ProductStatus.PARSER);
    m_condition = inValues.use("condition", m_condition,
                               ProductCondition.PARSER);
    m_conditionComment = inValues.use("condition_comment", m_conditionComment);
  }

  @Override
  public Message toProto()
  {
    ProductProto.Builder builder = ProductProto.newBuilder();

    builder.setBase((EntryProto)super.toProto());

    if(m_edition.isPresent())
      builder.setEdition(m_edition.get());

    if(m_printing.isPresent())
      builder.setPrinting(m_printing.get());

    if(m_owner.isPresent())
      builder.setOwner(m_owner.get());

    if(m_status != ProductStatus.UNKNOWN)
      builder.setStatus(m_status.toProto());

    if(m_condition != ProductCondition.UNKNOWN)
      builder.setCondition(m_condition.toProto());

    if(m_conditionComment.isPresent())
      builder.setConditionComment(m_conditionComment.get());

    return builder.build();
  }

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
      m_edition = Optional.of(proto.getEdition());

    if(proto.hasPrinting())
      m_printing = Optional.of(proto.getPrinting());

    if(proto.hasOwner())
      m_owner = Optional.of(proto.getOwner());

    if(proto.hasStatus())
      m_status = ProductStatus.fromProto(proto.getStatus());

    if(proto.hasCondition())
      m_condition = ProductCondition.fromProto(proto.getCondition());

    if(proto.hasConditionComment())
      m_conditionComment = Optional.of(proto.getConditionComment());

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

  //----------------------------------------------------------------------------

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
//   }
}
