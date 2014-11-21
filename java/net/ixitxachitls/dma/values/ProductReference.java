/******************************************************************************
 * Copyright (c) 2002-2013 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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


package net.ixitxachitls.dma.values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;

import net.ixitxachitls.dma.entries.BaseProduct;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.proto.Values.RangeProto;

@ParametersAreNonnullByDefault
public class ProductReference extends Reference<BaseProduct>
{
  public static class ProductReferenceParser
    extends ReferenceParser<BaseProduct, ProductReference>
  {
    public ProductReferenceParser()
    {
      super(BaseProduct.TYPE, 2);
    }

    @Override
    public Optional<ProductReference> doParse(String inName, String inPages)
    {
      return ProductReference.parse(inName, inPages);
    }
  }

  public ProductReference(String inName)
  {
    super(BaseProduct.TYPE, inName);
  }

  public ProductReference(String inName, Range... inPages)
  {
    this(inName);

    for(Range page : inPages)
      m_pages.add(page);
  }

  public ProductReference(String inName, List<Range> inPages)
  {
    this(inName);

    m_pages.addAll(inPages);
  }

  private static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults();
  private static final Joiner COMMA_JOINER = Joiner.on(", ");
  public static final Parser<ProductReference> PARSER =
    new ProductReferenceParser();

  private final List<Range> m_pages = new ArrayList<>();

  /**
   * Get the product title.
   *
   * @return the product tile
   */
  public String getTitle()
  {
    resolve();

    if(!m_entry.isPresent())
      return m_name;

    return m_entry.get().getFullTitle();
  }

  @Override
  public String toString()
  {
    if(m_pages.isEmpty())
      return super.toString();

    return super.toString() + " " + COMMA_JOINER.join(m_pages);
  }

  /**
   * Get the pages to refernece to, if any.
   *
   * @return the pages
   */
  public List<Range> getPages()
  {
    return Collections.unmodifiableList(m_pages);
  }

  /**
   * Get a string with all the pages information for editing.
   *
   * @return a pages string
   */
  public String getPagesString()
  {
    return COMMA_JOINER.join(m_pages);
  }

  @Override
  public BaseEntryProto.Reference toProto()
  {
    BaseEntryProto.Reference.Builder reference =
      BaseEntryProto.Reference.newBuilder();

    reference.setName(m_name);
    for(Range page : m_pages)
      reference.addPages(page.toProto());

    return reference.build();
  }

  /**
   * Create a new reference from the given proto message.
   *
   * @param inProto the proto message
   * @return the newly create reference
   */
  public static ProductReference fromProto(BaseEntryProto.Reference inProto)
  {
    ProductReference reference = new ProductReference(inProto.getName());
    for(RangeProto page : inProto.getPagesList())
      reference.m_pages.add(Range.fromProto(page));

    return reference;
  }

  /**
   * Parse the product reference from the given strings.
   *
   * @param inName the name of the reference
   * @param inPages the pages of the reference
   * @return
   */
  public static Optional<ProductReference> parse(String inName, String inPages)
  {
    List<Range> pages = new ArrayList<>();
    for(String page : COMMA_SPLITTER.split(inPages))
    {
      Optional<Range> range = Range.PARSER.parse(page);
      if(!range.isPresent())
        return Optional.absent();

      pages.add(range.get());
    }

    return Optional.of(new ProductReference(inName, pages));
  }

  @Override
  public boolean equals(@Nullable Object inOther)
  {
    if(this == inOther)
      return true;

    if(inOther == null)
      return false;

    if(!(inOther instanceof ProductReference))
      return false;

    ProductReference other = (ProductReference)inOther;
    return m_name.equals(other.m_name)
      && m_pages.equals(other.m_pages);
  }

  @Override
  public int hashCode()
  {
    return m_name.hashCode();
  }

  @Override
  public int compareTo(Reference inOther)
  {
    if(inOther == this)
      return 0;

    return toString().compareTo(inOther.toString());
  }
}