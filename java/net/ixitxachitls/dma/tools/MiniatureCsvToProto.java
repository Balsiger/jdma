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

package net.ixitxachitls.dma.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.io.Files;

import net.ixitxachitls.dma.proto.Entries;
import net.ixitxachitls.dma.values.enums.Alignment;
import net.ixitxachitls.dma.values.enums.Size;

/**
 * Convert a csv file with miniature information to proto format.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class MiniatureCsvToProto
{

  public static void main(String []inArgs) throws Exception
  {
    if(inArgs.length != 1) {
      System.err.println("Expected a single argument with the name of the csv "
                         + "file to read.");
      return;
    }

    File input = new File(inArgs[0]);
    File output = new File(inArgs[0].replace(".csv", ".ascii"));

    System.out.println("Converting file " + input + " to " + output);

    Set<String> names = new HashSet<>();
    try (PrintWriter outputWriter = new PrintWriter(
        new BufferedOutputStream(new FileOutputStream(output))))
    {
      boolean first = true;
      for(String line : Files.readLines(input, Charset.defaultCharset()))
      {
        // Skip first line with label names
        if(first)
        {
          first = false;
          continue;
        }



        line = line.replaceAll("(\".*?),(.*?\")", "$1\0x01$2");
        line = line.replace("\"", "");

        String[] columns = line.split(",");
        for(int i = 0; i < columns.length; i++)
          columns[i] = columns[i].replace("\0x01", ",");

        if(columns[1].trim().isEmpty())
          continue;

        Entries.BaseMiniatureProto.Builder proto =
            Entries.BaseMiniatureProto.newBuilder();

        String name = columns[1];
        if(names.contains(name))
        {
          System.err.println("duplicate name for " + name);
          name = name + " (" + columns[2] + ")";
        }
        else
          names.add(name);

        proto.setBase(Entries.BaseEntryProto.newBuilder()
                          .setAbstract(Entries.AbstractEntryProto.newBuilder()
                                           .setName(name)
                                           .setType("base miniature")
                                           .build())
                          .build());

        proto.setSet(columns[15].replaceAll("\\s+\\d+$", ""))
            .setOrigin(columns[4])
            .setType(columns[5])
            .addAllSubtype(Arrays.asList(columns[6].split("/")))
            .addAllCharacterClass(Arrays.asList(columns[8].split("/")))
            .setRole(columns[10]);

        try
        {
          proto.setNumber(Integer.parseInt(columns[3]));
        } catch(NumberFormatException e)
        {
        }

        try
        {
          proto.setLevel(Integer.parseInt(columns[9]));
        } catch(NumberFormatException e)
        {
        }

        Optional<Alignment> alignment = Alignment.PARSER.parse(columns[7]);
        if(alignment.isPresent())
          proto.setAlignment(alignment.get().toProto());

        Optional<Size> size = Size.PARSER.parse(columns[12]);
        if(size.isPresent())
          proto.setSize(size.get().toProto());

        outputWriter.println("base_miniature {");
        outputWriter.print(proto.build().toString());
        outputWriter.println("}\n");
      }
    }
  }
}
