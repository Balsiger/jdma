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

/** Small class to encapsulate person information with job. */
public class Content extends NewValue<BaseProductProto.Content> {

  public static class ContentParser extends Parser<Content>
  {
    public ContentParser()
    {
      super(3);
    }

    @Override
    public Optional<Content> doParse(String ... inValues)
    {
      if(inValues[0].isEmpty())
        return null;

      Optional<BaseProduct.Part> part =
        BaseProduct.Part.fromString(inValues[0]);
      if(!part.isPresent())
        return Optional.absent();

      try
      {
        return Optional
          .of(new Content(part.get(), inValues[1],
                          Integer.parseInt(inValues[2])));
      }
      catch(NumberFormatException e)
      {
        return null;
      }
    }
  }

  public Content(BaseProduct.Part inPart, String inDescription, int inAmount)
  {
    m_part = inPart;
    m_description = inDescription;
    m_amount = inAmount;
  }

  public static final Parser<Content> PARSER = new ContentParser();

  private final BaseProduct.Part m_part;
  private final String m_description;
  private final int m_amount;

  public BaseProduct.Part getPart()
  {
    return m_part;
  }

  public String getDescription()
  {
    return m_description;
  }

  public int getAmount()
  {
    return m_amount;
  }

  @Override
  public String toString() {
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

  public static Content fromProto(BaseProductProto.Content inProto)
  {
    return new Content(BaseProduct.Part.fromProto(inProto.getPart()),
                       inProto.getDescription(), inProto.getNumber());
  }
}