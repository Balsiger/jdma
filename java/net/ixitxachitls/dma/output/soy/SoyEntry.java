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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.data.restricted.UndefinedData;

import net.ixitxachitls.dma.data.DMAData;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.values.Value;
import net.ixitxachitls.util.errors.BaseError;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A soy wrapper around an abstract Entry.
 *
 *
 * @file          SoyEntry.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class SoyEntry extends SoyMapData
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- SoyEntry -------------------------------

  /**
   * Create the soy entry data.
   *
   * @param       inEntry    the entry with the values
   * @param       inRenderer the renderer used for printing
   *
   */
  public SoyEntry(@Nonnull AbstractEntry inEntry,
                  @Nonnull SoyRenderer inRenderer)
  {
    m_entry = inEntry;
    m_renderer = inRenderer;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The entry with the data. */
  private final @Nonnull AbstractEntry m_entry;

  /** The renderer used for priting sub values. */
  private final @Nonnull SoyRenderer m_renderer;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------ getSingle -------------------------------

  /**
   * Get a single value out of the entry.
   *
   * @param  inName the name of the value to get
   *
   * @return the value found or null if not found
   */
  @Override
  public @Nullable SoyData getSingle(@Nonnull String inName)
  {
    if(inName.startsWith("extension_"))
      return BooleanData.forValue(m_entry.hasExtension(inName.substring(10)));

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

    if("files".equals(inName))
    {
      DMAData.File main = m_entry.getMainFile();
      List<DMAData.File> files = m_entry.getFiles();

      SoyListData data = new SoyListData();
      for(DMAData.File file : files)
        if(!file.getName().equals(main.getName()))
          data.add(new SoyMapData("path", file.getPath(),
                                  "icon", file.getIcon(),
                                  "name", file.getName(),
                                  "type", file.getType()));

      return new SoyMapData("main",
                            main == null ? UndefinedData.INSTANCE
                            : new SoyMapData("path", main.getPath(),
                                             "icon", main.getIcon(),
                                             "name", main.getName(),
                                             "type", main.getType()),
                            "other", data);
    }

    if("name".equals(inName))
      return StringData.forValue(m_entry.getName());

    if("dma".equals(inName))
      return StringData.forValue(m_entry.toString());

    if("errors".equals(inName))
    {
      List<String> errors = new ArrayList<String>();
      for(Iterator<BaseError> i = m_entry.getErrors(); i.hasNext(); )
        errors.add(i.next().format().toString());

      return new SoyListData(errors);
    }

    String name = inName.replace("_", " ");
    Value value = m_entry.compute(name);
    if(value != null)
      return new SoyValue(name, value, m_entry, m_renderer);

    return null;
  }

  //........................................................................
  //-------------------------------- equals --------------------------------

  /**
   * Checks if the given object is equal to this one.
   *
   * @param    inOther the object to compare against
   *
   * @return   true if the other is equal, false if not
   *
   */
  @Override
  public boolean equals(Object inOther)
  {
    if(!(inOther instanceof SoyEntry))
      return false;

    return m_entry.equals(((SoyEntry)inOther).m_entry)
      && m_renderer.equals(((SoyEntry)inOther).m_renderer)
      && super.equals(inOther);
  }

  //........................................................................
  //------------------------------- hashCode -------------------------------

  /**
   * Compute the hash code of the object.
   *
   * @return      the object's hash code
   *
   */
  @Override
  public int hashCode()
  {
    return super.hashCode() + m_renderer.hashCode() + m_entry.hashCode();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //........................................................................

  //------------------------------------------------- other member functions

  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- single ---------------------------------------------------------

    /** The single Test. */
    @org.junit.Test
    public void single()
    {
      SoyEntry soyEntry = new SoyEntry(new net.ixitxachitls.dma.entries
                                       .BaseEntry("test entry"),
                                       new SoyRenderer(new SoyTemplate()));

      assertEquals("extension", "false",
                   soyEntry.getSingle("extension_x").toString());
      assertEquals("key", "/base entry/test entry",
                   soyEntry.getSingle("key").toString());
      assertEquals("path", "/entry/test entry",
                   soyEntry.getSingle("path").toString());
      assertEquals("type", "{multidir: BaseEntries, link: entry, "
                   + "name: base entry, multi: Base Entries, "
                   + "multilink: entrys, css: base-entry}",
                   soyEntry.getSingle("type").toString());
      assertEquals("files", "{other: [], "
                   + "main: {icon: /icons/BaseEntries-dummy.png, name: main, "
                   + "path: /icons/BaseEntries-dummy.png, type: image/png}}",
                   soyEntry.getSingle("files").toString());
      assertEquals("name", "test entry",
                   soyEntry.getSingle("name").toString());
      assertEquals("dma", "#----- test entry\n\n"
                   + "base entry test entry =\n\n.\n\n#.....\n",
                   soyEntry.getSingle("dma").toString());
      assertEquals("errors", "[]",
                   soyEntry.getSingle("errors").toString());
      assertEquals("worlds", "{}",
                   soyEntry.getSingle("worlds").toString());
    }

    //......................................................................
  }

  //........................................................................
}
