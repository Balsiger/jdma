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

import com.google.common.base.Optional;

import net.ixitxachitls.dma.entries.BaseProduct;
import net.ixitxachitls.dma.proto.Entries.BaseProductProto;

/**
 * Small class to encapsulate person information with job.
 *
 * @file Content.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Content extends Value<BaseProductProto.Content>
{
  /** The parser for content. */
  public static class ContentParser extends Parser<Content>
  {
    /** Create the parser. */
    public ContentParser()
    {
      super(3);
    }

    @Override
    public Optional<Content> doParse(String inPart, String inDescription,
                                     String inAmount)
    {
      Optional<BaseProduct.Part> part =
        BaseProduct.Part.fromString(inPart);
      if(!part.isPresent())
        return Optional.absent();

      try
      {
        return Optional
          .of(new Content(part.get(), inDescription,
                          Integer.parseInt(inAmount)));
      }
      catch(NumberFormatException e)
      {
        return null;
      }
    }
  }

  /**
   * Create a cotent value.
   *
   * @param inPart the kid of content
   * @param inDescription the description of the content
   * @param inAmount the number of pieces of the content
   */
  public Content(BaseProduct.Part inPart, String inDescription, int inAmount)
  {
    m_part = inPart;
    m_description = inDescription;
    m_amount = inAmount;
  }

  /** The default content parser. */
  public static final Parser<Content> PARSER = new ContentParser();

  /** The type of cotent. */
  private final BaseProduct.Part m_part;

  /** The description of the content. */
  private final String m_description;

  /** The number of pieces of the content. */
  private final int m_amount;

  /** Get the type of content.
   *
   * @return the type
   */
  public BaseProduct.Part getPart()
  {
    return m_part;
  }

  /**
   * Get the content description.
   *
   * @return the description
   */
  public String getDescription()
  {
    return m_description;
  }

  /**
   * Get the number of content pieces.
   *
   * @return the amount
   */
  public int getAmount()
  {
    return m_amount;
  }

  @Override
  public String toString()
  {
    if(m_amount != 1)
      return m_amount + "x " + m_part + " " + m_description;

    return m_part + " " + m_description;
  }

  @Override
  public BaseProductProto.Content toProto()
  {
    return BaseProductProto.Content.newBuilder()
      .setPart(m_part.toProto())
      .setDescription(m_description)
      .setNumber(m_amount)
      .build();
  }

  /**
   * Create a content from the given proto.
   *
   * @param inProto the proto to convert
   * @return the content value representing the proto
   */
  public static Content fromProto(BaseProductProto.Content inProto)
  {
    return new Content(BaseProduct.Part.fromProto(inProto.getPart()),
                       inProto.getDescription(), inProto.getNumber());
  }
}
