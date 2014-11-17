/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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

package net.ixitxachitls.dma.output.soy;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.StringData;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.util.Classes;

/**
 * A soy wrapper around an abstract Entry.
 *
 *
 * @file          SoyEntry.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@Immutable
@ParametersAreNonnullByDefault
public class SoyEntry extends SoyAbstract
{
  /**
   * Create the soy entry data.
   *
   * @param       inEntry    the entry with the values
   */
  public SoyEntry(AbstractEntry inEntry)
  {
    super(inEntry.getName(), inEntry);

    m_entry = inEntry;
  }

  /** The cache for values obtained so far. */
  Map<String, SoyData> m_cache = new HashMap<>();
  AbstractEntry m_entry;

  @Override
  public @Nullable SoyData getSingle(String inName)
  {
    if(m_cache.containsKey(inName))
      return m_cache.get(inName);

    SoyData data = value(inName);
    m_cache.put(inName, data);
    return data;
  }

  private @Nullable SoyData value(String inName)
  {
    if("key".equals(inName))
      return StringData.forValue(m_entry.getKey().toString());

    if("path".equals(inName))
      return StringData.forValue(m_entry.getPath());

    if("isBase".equals(inName))
      return BooleanData.forValue(m_entry.isBase());

    if("editType".equals(inName))
      return StringData.forValue(m_entry.getEditType());

    if("type".equals(inName))
      return new SoyMapData("name", m_entry.getType().getName(),
                            "link", m_entry.getType().getLink(),
                            "multi", m_entry.getType().getMultiple(),
                            "multilink", m_entry.getType().getMultipleLink(),
                            "multidir", m_entry.getType().getMultipleDir(),
                            "css",
                            m_entry.getType().getName().replace(" ", "-"));

    String name = inName.replace("__", "").replace("_", " ");

    Object value;
    // check if there is a function with the given name
    if(!inName.startsWith("__"))
    {
      value = Classes.callMethod(name, m_entry);
      if(value != null)
        return convert(name, value);
    }

    return new Undefined(m_name + "." + name);
  }

  @Override
  public boolean equals(Object inOther)
  {
    if(inOther == this)
      return true;

    if(!(inOther instanceof SoyEntry))
      return false;

    return m_entry.equals(((SoyEntry)inOther).m_entry)
      && super.equals(inOther);
  }

  @Override
  public int hashCode()
  {
    return super.hashCode() + m_entry.hashCode();
  }

  @Override
  public String toString()
  {
    return m_entry.getName() + " (soy)";
  }

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- single ---------------------------------------------------------

    /** The single Test. */
    @org.junit.Test
    public void single()
    {
      SoyEntry soyEntry = new SoyEntry(new net.ixitxachitls.dma.entries
                                       .BaseEntry("test entry"));

      assertEquals("extension", "false",
                   soyEntry.getSingle("extension_x").toString());
      assertEquals("key", "/base entry/test entry",
                   soyEntry.getSingle("key").toString());
      assertEquals("path", "/entry/test entry",
                   soyEntry.getSingle("path").toString());
      assertStringContent("type",
                          ((SoyMapData)soyEntry.getSingle("type")).asMap(),
                          "multidir", "BaseEntries",
                          "link", "entry",
                          "name", "base entry",
                          "multi", "Base Entries",
                          "multilink", "entrys",
                          "css", "base-entry");

      Map<String, SoyData> files =
          ((SoyMapData)soyEntry.getSingle("files")).asMap();
      assertEquals("files, other", files.get("other").toString(), "[]");
      assertStringContent("files, main",
                          ((SoyMapData)files.get("main")).asMap(),
                          "icon", "/icons/BaseEntries-dummy.png",
                          "name", "main",
                          "path", "/icons/BaseEntries-dummy.png",
                          "type", "image/png");
      assertEquals("name", "test entry",
                   soyEntry.getSingle("name").toString());
      assertEquals("dma", "#----- test entry\n\n"
                   + "base entry test entry =\n\n"
                   + "  name              test entry.\n\n#.....\n",
                   soyEntry.getSingle("dma").toString());
      assertEquals("errors", "[]",
                   soyEntry.getSingle("errors").toString());
      assertEquals("worlds", "",
                   soyEntry.getSingle("worlds").toString());
    }

    //......................................................................
  }
}
