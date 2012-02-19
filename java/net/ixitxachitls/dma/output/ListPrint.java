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

package net.ixitxachitls.dma.output;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.entries.extensions.AbstractExtension;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A print object for printing into a list.
 *
 *
 * @file          ListPrint.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class ListPrint extends AbstractPrint
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ ListPrint -------------------------------

  /**
   * Create the print values.
   *
   * @param       inFormat  the table format for printing
   * @param       inTemplates  the individual cell values to print
   *
   */
  public ListPrint(@Nonnull String inFormat, @Nonnull String ... inTemplates)
  {
    m_format = inFormat;
    m_templates = inTemplates;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The table format for printing. */
  private @Nonnull String m_format;

  /** The templates for each table cell. */
  private @Nonnull String []m_templates;

  /** The tokens for each template. */
  private volatile @Nullable List<List<String>>m_tokens;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- print ---------------------------------

  /**
   * Print the given entry into a command.
   *
   * @param       inKey    the key really printed (e.g. for synonyms)
   * @param       inEntry  the entry to print
   * @param       inUser   the user for whcih to print, if any
   *
   * @return      the object that can be added to a document for printing
   *
   */
  @SuppressWarnings("unchecked") // need to case for generic array creation
  public @Nonnull List<Object> print
    (@Nonnull String inKey, @Nonnull ValueGroup inEntry,
     @Nullable BaseCharacter inUser)
  {
    // CHECKSTYLE:OFF (this works in Java 1.6)
    if(m_tokens == null)
      synchronized(this)
      {
        if(m_tokens == null)
        {
          m_tokens = new ArrayList<List<String>>();
          for(int i = 0; i < m_templates.length; i++)
            if(m_templates[i] == null)
              m_tokens.add(null);
            else
              m_tokens.add(tokenize(m_templates[i]));
        }
      }
    // CHECKSTYLE:ON

    List<Object> result = new ArrayList<Object>();
    for(List<String> tokens : m_tokens)
      result.add(convert(tokens, inEntry, inKey, inUser));

    return result;
  }

  //........................................................................
  //---------------------------- printExtension ----------------------------

  /**
   * Print the extension information.
   *
   * @param     inExtension the extension to print
   * @param     inUser      the user printing for
   *
   * @return    an object representing the desired print
   *
   */
  @Override
protected @Nonnull Object
    printExtension(@Nonnull AbstractExtension inExtension,
                   @Nonnull BaseCharacter inUser)
  {
    return inExtension.printList("??guru??", inUser);
  }

  //........................................................................
  //------------------------------ getFormat -------------------------------

  /**
   * Get the table format for printing the complete list.
   *
   * @return      the table format
   *
   */
  public @Nonnull String getFormat()
  {
    return m_format;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- print ----------------------------------------------------------

    /** The print Test. */
    @SuppressWarnings("unchecked") // unchecked cast
    @org.junit.Test
    public void print()
    {
      net.ixitxachitls.dma.entries.BaseEntry entry =
        new net.ixitxachitls.dma.entries.BaseEntry("test");

      entry.setDescription("desc");

      ListPrint print =
        new ListPrint("format", "start", "$first", "${title}");

      assertEquals("format", "format", print.getFormat());
      assertEquals("printing",
                   "[start, \\color{error}{ * first * }, "
                   + "\\title[entrytitle]"
                   + "{test}]",
                   print.print("key", entry, null).toString());
    }

    //......................................................................
  }

  //........................................................................
}
