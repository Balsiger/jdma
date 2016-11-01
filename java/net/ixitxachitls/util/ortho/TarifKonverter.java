/*******************************************************************************
 * Copyright (c) 2002-2016 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
 ******************************************************************************/

package net.ixitxachitls.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.api.client.util.Charsets;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/**
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class TarifKonverter
{
  public static void main(String[] args) throws IOException
  {
    String name = args[0];
    System.out.println("Reading file " + name);
    CSVParser parser =
        CSVParser.parse(new File(name), Charsets.UTF_8, CSVFormat.EXCEL);

    List<Group> parts = new ArrayList<>();
    List<Group> groups = new ArrayList<>();
    List<Group> subgroups = new ArrayList<>();
    List<Entry> entries = new ArrayList<>();

    Group lastPart = null;
    Group lastGroup = null;
    Group lastSubgroup = null;
    Group lastLevel3Subgroup = null;

    for(CSVRecord record : parser)
    {
      // Ignore first line with headers.
      if(record.getRecordNumber() == 1)
        continue;

      String type = record.get(0);
      int level = Integer.parseInt(record.get(1));

      // Don't need level 0;
      if(level == 0)
        continue;

      String chapter = record.get(2);
      String id = record.get(3);
      String title = record.get(4);
      String text = record.get(6);

      // Parts
      if(level == 1)
      {
        if(!"Kap".equals(type))
          throw new IllegalStateException("Did not expect non 'Kap' in level 1");
        lastPart = new Group(chapter, title, text);
        parts.add(lastPart);
        continue;
      }

      // Groups
      if(level == 2)
      {
        if(!"Kap".equals(type))
          throw new IllegalStateException("Did not expect non 'Kap' in level 1");

        lastGroup = new Group(chapter, title, text, lastPart.m_id);
        groups.add(lastGroup);
        continue;
      }

      // Subgroups
      if(level == 3)
      {
        if("Kap".equals(type))
        {
          lastSubgroup = new Group(chapter, title, text, lastGroup.m_id);
          lastLevel3Subgroup = lastSubgroup;
          subgroups.add(lastLevel3Subgroup);
          continue;
        }
        else
        {
          // Reuse the last group if it has not been used already.
          if(!lastGroup.equals(lastSubgroup))
          {
            lastSubgroup = new Group(lastGroup.m_id, lastGroup.m_title,
                                     lastGroup.m_text, lastGroup.m_id);
            subgroups.add(lastSubgroup);
          }
        }
      }

      // Subgroups of level 4
      if(level == 4)
      {
        if("Kap".equals(type))
        {
          // We use the last subgroup, but add the data from here.
          lastSubgroup = new Group(
              chapter, join(lastLevel3Subgroup.m_title, title),
              join(lastLevel3Subgroup.m_text, text), lastGroup.m_id);
          subgroups.add(lastSubgroup);
          continue;
        }
      }

      if(!"Lei".equals(type))
        throw new IllegalStateException("Unexpected type "
                                            + type + "/" + level);

      // Individual entries
      String points = record.get(17);
      String pointText = record.get(9);
      String relative =
          record.get(8).equals("1") ? "" : record.get(8).substring(1);
      entries.add(new Entry(id, title, text, lastSubgroup.m_id, points,
                            pointText, relative));
    }

    // Printing
    print("Teile.csv", parts);
    print("Gruppen.csv", groups);
    print("Subgruppen.csv", subgroups);
    printEntries("Leistungen.csv", entries);
  }

  private static String join(String first, String second)
  {
    if(first == null || first.isEmpty())
      return second;

    if(second == null || second.isEmpty())
      return first;

    return first + ", " + second;
  }

  private static void print(String name, List<Group> groups) throws IOException
  {
    CSVPrinter out =
        new CSVPrinter(new FileWriter(name), CSVFormat.EXCEL);
    for(Group group : groups)
      if(group.m_parent == null)
        out.printRecord(group.m_id, group.m_title, group.m_text);
      else
        out.printRecord(group.m_id, group.m_title, group.m_text,
                        group.m_parent);

    out.close();
  }

  private static String convert(String text) {
    return text.replaceAll("\n+", " ");
  }

  private static void printEntries(String name, List<Entry> groups)
      throws IOException
  {
    CSVPrinter out =
        new CSVPrinter(new FileWriter(name), CSVFormat.EXCEL);
    for(Entry entry : groups)
      out.printRecord(entry.m_id, entry.m_title, entry.m_text, entry.m_subgroup,
                      entry.m_points, entry.m_pointText, entry.m_relative);

    out.close();
  }

  public static class Group
  {
    private final String m_id;
    private final String m_title;
    private final String m_text;
    @Nullable private String m_parent;

    public Group(String id, String title, String text)
    {
      this(id, title, text, null);
    }

    public Group(String id, String title, String text, String parent)
    {
      m_id = id;
      m_title = convert(title);
      m_text = convert(text);
      m_parent = parent;
    }

    @Override
    public boolean equals(Object inOther)
    {
      if(this == inOther) return true;
      if(inOther == null || getClass() != inOther.getClass()) return false;

      final Group group = (Group)inOther;

      return m_id.equals(group.m_id);
    }

    @Override
    public int hashCode()
    {
      return m_id.hashCode();
    }
  }

  public static class Entry
  {
    private final String m_id;
    private final String m_title;
    private final String m_text;
    private final String m_subgroup;
    private final String m_points;
    private final String m_pointText;
    private final String m_relative;

    public Entry(String id, String title, String text, String subgroup,
                 String points, String pointText, String relative)
    {
      m_id = id;
      m_title = convert(title);
      m_text = convert(text);
      m_subgroup = subgroup;
      m_points = points;
      m_pointText = pointText;
      m_relative = relative;
    }
  }
}
