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

import javax.annotation.ParametersAreNonnullByDefault;

import net.ixitxachitls.dma.proto.Entries.AbstractEntryProto;

/**
 * A simple representation for a file associated with an entry.
 */
@ParametersAreNonnullByDefault
public class File extends NewValue<AbstractEntryProto.File>
{
  /**
   * Create the file with all its data.
   *
   * @param inName   the file name
   * @param inType   the mime type of the file
   * @param inPath   the url path to access the file
   * @param inIcon   the icon or thumbnail to show for the file
   */
  public File(String inName, String inType, String inPath, String inIcon)
  {
    m_name = inName;
    m_type = inType;
    m_path = inPath;
    m_icon = inIcon;
  }

  /** The name of the file. */
  String m_name;

  /** The mime type of the file. */
  private final String m_type;

  /** The url to display the file. */
  private final String m_path;

  /** The url to display a thumbnail of the file. */
  private final String m_icon;

  /**
   * Get the name of the file.
   *
   * @return the name of the file (without path)
   */
  public String getName()
  {
    return m_name;
  }

  /**
   * Get the mime type of the file.
   *
   * @return the mime type
   */
  public String getType()
  {
    return m_type;
  }

  /**
   * Get the path of the file.
   *
   * @return the path to access the file
   */
  public String getPath()
  {
    return m_path;
  }

  /**
   * Get the icon for the file.
   *
   * @return the icon for the file
   */
  public String getIcon()
  {
    return m_icon;
  }

  @Override
  public String toString()
  {
    return m_name + "/" + m_type + "=" + m_path;
  }

  @Override
  public AbstractEntryProto.File toProto()
  {
    return AbstractEntryProto.File.newBuilder()
      .setName(m_name)
      .setPath(m_path)
      .setType(m_type)
      .setIcon(m_icon)
      .build();
  }

  /**
   * Create a file from the given proto.
   *
   * @param inProto the proto to create from
   * @return the created file
   */
  public static File fromProto(AbstractEntryProto.File inProto)
  {
    return new File(inProto.getName(), inProto.getType(), inProto.getPath(),
                    inProto.getIcon());
  }
}