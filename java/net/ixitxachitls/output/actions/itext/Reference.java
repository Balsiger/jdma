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

package net.ixitxachitls.output.actions.itext;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.AbstractType;
//import net.ixitxachitls.dma.entries.BaseEntry;
//import net.ixitxachitls.dma.entries.BaseCampaign;
import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.output.commands.Color;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a reference action. With this action, references can be
 * inserted.
 *
 * @file          Reference.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Reference extends Action
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Reference ------------------------------

  /**
   * Construct the action, mainly by giving the references to use. Any of
   * the references given can be null, in which case they are ignored.
   *
   * @param       inStyle the style to use for formatting
   * @param       inType  the type of the entry refernced
   *
   */
  public Reference(@Nonnull String inStyle,
                   @Nonnull AbstractType<? extends AbstractEntry> inType)
  {
    m_style = inStyle;
    m_type = inType;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The name of the style to use. */
  private @Nonnull String m_style;

  /** The type of entry being referenced. */
  private @Nonnull AbstractType<? extends AbstractEntry> m_type;

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- execute --------------------------------

  /**
   * Execute the action onto the given document.
   *
   * @param       inDocument  the document to output to
   * @param       inOptionals the optional argument
   * @param       inArguments the arguments
   *
   */
  @Override
public void execute(@Nonnull Document inDocument,
                      @Nullable List<? extends Object> inOptionals,
                      @Nullable List<? extends Object> inArguments)
  {
    if(inArguments == null || inArguments.size() != 1)
      throw new IllegalArgumentException("must have one argument here");

    String name = inDocument.convert(inArguments.get(0));

    inDocument.add("<font style=\"italic\" color=\"" + m_style + "\">" + name
                   + "</font> ");

    String []optionals =
      new String[inOptionals != null ? inOptionals.size() : 0];

    for(int i = 0; inOptionals != null && i < inOptionals.size(); i++)
      optionals[i] = inDocument.convert(inOptionals.get(i));

    //     BaseEntry entry = null;
    // TODO: BaseCampaign.GLOBAL.getBaseEntry(name, m_type);

    inDocument.add(" (");

//     if(name != null && entry != null)
//       inDocument.add(entry.getSummary(optionals));
//     else
      inDocument.add(new Color("error", "not found"));

    inDocument.add(")");
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  // TODO: write tests
//   public static class Test extends net.ixitxachitls.util.test.TestCase
//   {
//   }

  //........................................................................
}
