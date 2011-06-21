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

package net.ixitxachitls.dma.output.ascii;

import java.util.HashMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import net.ixitxachitls.output.commands.Class;
import net.ixitxachitls.output.commands.Event;
import net.ixitxachitls.output.commands.Feat;
import net.ixitxachitls.output.commands.God;
import net.ixitxachitls.output.commands.Group;
import net.ixitxachitls.output.commands.Item;
import net.ixitxachitls.output.commands.Monster;
import net.ixitxachitls.output.commands.NPC;
import net.ixitxachitls.output.commands.Place;
import net.ixitxachitls.output.commands.Product;
import net.ixitxachitls.output.commands.Quality;
import net.ixitxachitls.output.commands.Skill;
import net.ixitxachitls.output.commands.Spell;
import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.output.actions.Delimiter;
import net.ixitxachitls.output.commands.Command;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the document for all dma specific ascii documents used.
 *
 * @file          ASCIIDocument.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class ASCIIDocument extends net.ixitxachitls.output.ascii.ASCIIDocument
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- ASCIIDocument ----------------------------

  /**
   * Basic constructor.
   *
   */
  public ASCIIDocument()
  {
    // nothing to do
  }

  //........................................................................
  //----------------------------- ASCIIDocument ----------------------------

  /**
   * Basic constructor.
   *
   * @param       inWidth the maximal width of a text line
   *
   */
  public ASCIIDocument(int inWidth)
  {
    super(inWidth);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The known actions. */
  protected static final @Nonnull HashMap<String, Action> s_actions =
    new HashMap<String, Action>(net.ixitxachitls.output.ascii.ASCIIDocument
                                .s_actions);

  static
  {
    // DMA specific commands
    s_actions.put(Place.NAME,      new Delimiter("--place-->", null,
                                                 new String [] { "" }, null,
                                                 new String [] { "[" },
                                                 new String [] { "]" }));
    s_actions.put(Product.NAME,    new Delimiter("--product-->", null,
                                                 new String [] { "" }, null,
                                                 new String [] { "[" },
                                                 new String [] { "]" }));
    s_actions.put(NPC.NAME,        new Delimiter("--npc-->", null,
                                                 new String [] { "" }, null,
                                                 new String [] { "[" },
                                                 new String [] { "]" }));
    s_actions.put(Monster.NAME,    new Delimiter("--monster-->", null,
                                                 new String [] { "" }, null,
                                                 new String [] { "[" },
                                                 new String [] { "]" }));
    s_actions.put(Group.NAME,      new Delimiter("--group-->", null,
                                                 new String [] { "" }, null,
                                                 new String [] { "[" },
                                                 new String [] { "]" }));
    s_actions.put(Item.NAME,       new Delimiter("--item-->", null,
                                                 new String [] { "" }, null,
                                                 new String [] { "[" },
                                                 new String [] { "]" }));
    s_actions.put(God.NAME,        new Delimiter("--god-->", null,
                                                 new String [] { "" }, null,
                                                 new String [] { "[" },
                                                 new String [] { "]" }));
    s_actions.put(Event.NAME,      new Delimiter("--event-->", null,
                                                 new String [] { "" }, null,
                                                 new String [] { "[" },
                                                 new String [] { "]" }));
    s_actions.put(Class.NAME,      new Delimiter("--class-->", null,
                                                 new String [] { "" }, null,
                                                 new String [] { "[" },
                                                 new String [] { "]" }));
    s_actions.put(Spell.NAME,      new Delimiter("--spell-->", null,
                                                 new String [] { "" }, null,
                                                 new String [] { "[" },
                                                 new String [] { "]" }));
    s_actions.put(Feat.NAME,       new Delimiter("--feat-->", null,
                                                 new String [] { "" }, null,
                                                 new String [] { "[" },
                                                 new String [] { "]" }));
    s_actions.put(Skill.NAME,      new Delimiter("--skill-->", null,
                                                 new String [] { "" }, null,
                                                 new String [] { "[" },
                                                 new String [] { "]" }));
    s_actions.put(Quality.NAME,    new Delimiter("--quality-->", null,
                                                 new String [] { "" }, null,
                                                 new String [] { "[" },
                                                 new String [] { "]" }));
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------- getKnownActions ----------------------------

  /**
   * Get all the actions known to this document type.
   *
   * @return      a hash map with all the known converters
   *
   */
  protected @Nonnull HashMap<String, Action> getKnownActions()
  {
    return s_actions;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The Test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- simple ---------------------------------------------------------

    /** Simple tests. */
    @org.junit.Test
    public void simple()
    {
      Document doc = new ASCIIDocument();

      doc.add(new Command(new Command("just "), new Product("some "),
                          new Command("test")));

      assertEquals("simple", "just --product-->some test", doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
