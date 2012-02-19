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

package net.ixitxachitls.dma.output.pdf;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

//import net.ixitxachitls.dma.entries.BaseSpell;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.output.actions.Pattern;
//import net.ixitxachitls.output.actions.itext.Reference;
import net.ixitxachitls.output.commands.Class;
import net.ixitxachitls.output.commands.Command;
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

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The document for itext base printing of dma values.
 *
 * @file          ITextDocument.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class ITextDocument extends net.ixitxachitls.output.pdf.ITextDocument
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- ITextDocument ----------------------------

  /**
   * This is a convenience creator using the standard dimension of the page
   * from the configuration.
   *
   * @param       inTitle           the HTML title of the document
   *
   */
  public ITextDocument(@Nonnull String inTitle)
  {
    super(inTitle);
  }

  //........................................................................
  //----------------------------- ITextDocument ----------------------------

  /**
   * This is a convenience creator using the standard dimension of the page
   * from the configuration.
   *
   * @param       inTitle the HTML title of the document
   * @param       inDM    a flag denoting if this is a DM document or not
   *
   */
  public ITextDocument(@Nonnull String inTitle, boolean inDM)
  {
    super(inTitle, inDM);
  }

  //........................................................................
  //----------------------------- ITextDocument ----------------------------

  /**
   * The full creator.
   *
   * @param       inTitle the HTML title of the document
   * @param       inDM    a flag denoting if this is a DM document or not
   * @param       inLandscape true for landscape printing, false else
   *
   */
  public ITextDocument(@Nonnull String inTitle, boolean inDM,
                       boolean inLandscape)
  {
    super(inTitle, inDM, inLandscape);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The known actions. */
  protected static final HashMap<String, Action> s_actions =
    new HashMap<String, Action>(net.ixitxachitls.output.pdf.ITextDocument
                                .s_actions);

  static
  {
    // dma specific commands
    s_actions.put(Place.NAME,
                  new Pattern("<font style=\"italic\" color=\"Place\">"
                              + "$1</font>"));
    s_actions.put(Product.NAME,
                  new Pattern("<font style=\"italic\" color=\"Product\">"
                              + "$1</font>"));
    s_actions.put(NPC.NAME,
                  new Pattern("<font style=\"italic\" color=\"NPC\">"
                              + "$1</font>"));
    s_actions.put(Monster.NAME,
                  new Pattern("<font style=\"italic\" color=\"Monster\">"
                              + "$1</font>"));
    s_actions.put(Group.NAME,
                  new Pattern("<font style=\"italic\" color=\"Group\">"
                              + "$1</font>"));
    s_actions.put(Item.NAME,
                  new Pattern("<font style=\"italic\" color=\"Item\">"
                              + "$1</font>"));
    s_actions.put(God.NAME,
                  new Pattern("<font style=\"italic\" color=\"God\">"
                              + "$1</font>"));
    s_actions.put(Event.NAME,
                  new Pattern("<font style=\"italic\" color=\"Event\">"
                              + "$1</font>"));
    s_actions.put(Class.NAME,
                  new Pattern("<font style=\"italic\" color=\"Class\">"
                              + "$1</font>"));
    s_actions.put(Spell.NAME,
                  new Pattern("<font style=\"italic\" color=\"Spell\">"
                              + "$1</font>"));
    //s_actions.put(SpellRef.NAME, new Reference("Spell", BaseSpell.TYPE));
    s_actions.put(Feat.NAME,
                  new Pattern("<font style=\"italic\" color=\"Feat\">"
                              + "$1</font>"));
    s_actions.put(Skill.NAME,
                  new Pattern("<font style=\"italic\" color=\"Skill\">"
                              + "$1</font>"));
    s_actions.put(Quality.NAME,
                  new Pattern("<font style=\"italic\" color=\"Quality\">"
                              + "$1</font>"));
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
  @Override
protected @Nonnull Map<String, Action> getKnownActions()
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
      ITextDocument doc = new ITextDocument("title");

      doc.add(new Command
              (new Command("just "),
               new net.ixitxachitls.output.commands.Product("some "),
               new Command("test")));

      assertEquals("simple",
                   "just <font style=\"italic\" color=\"Product\">some "
                   + "</font>test",
                   doc.getBody());
    }

    //......................................................................
  }

  //........................................................................
}
