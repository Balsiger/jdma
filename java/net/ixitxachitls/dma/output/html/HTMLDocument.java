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

package net.ixitxachitls.dma.output.html;

import java.util.HashMap;
import java.util.Map;

import net.ixitxachitls.dma.output.commands.Event;
import net.ixitxachitls.dma.output.commands.Feat;
import net.ixitxachitls.dma.output.commands.God;
import net.ixitxachitls.dma.output.commands.Group;
import net.ixitxachitls.dma.output.commands.Item;
import net.ixitxachitls.dma.output.commands.Monster;
import net.ixitxachitls.dma.output.commands.NPC;
import net.ixitxachitls.dma.output.commands.Place;
import net.ixitxachitls.dma.output.commands.Product;
import net.ixitxachitls.dma.output.commands.Quality;
import net.ixitxachitls.dma.output.commands.Skill;
import net.ixitxachitls.dma.output.commands.Spell;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.output.actions.html.Link;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base document for all output documents used.
 *
 * @file          HTMLDocument.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class HTMLDocument extends net.ixitxachitls.output.html.HTMLDocument
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- HTMLDocument ----------------------------

  /**
   * This is a convenience create using the standard dimension of the page
   * from the configuration.
   *
   * @param       inTitle the HTML title of the document
   * @param       inType the type of the document
   *
   */
  public HTMLDocument(String inTitle, String inType)
  {
    super(inTitle, inType);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The known actions. */
  protected static final Map<String, Action> s_actions =
    new HashMap<String, Action>
    (net.ixitxachitls.output.html.HTMLDocument.s_actions);

  static
  {
    // dma specific commands
    s_actions.put(Place.PLACE,
                  new Link(Place.PLACE,
                           "/entry/" + Config.get("resource:html/place.dir",
                                                 "place"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Product.PRODUCT,
                  new Link(Product.PRODUCT,
                           "/entry/" + Config.get("resource:html/product.dir",
                                                 "baseproduct"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(NPC.NPC,
                  new Link(NPC.NPC,
                           "/entry/" + Config.get("resource:html/npc.dir",
                                           "npc"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Monster.MONSTER,
                  new Link(Monster.MONSTER,
                           "/entry/" + Config.get("resource:html/monster.dir",
                                                 "basemonster"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Group.GROUP,
                  new Link(Group.GROUP,
                           "/entry/" + Config.get("resource:html/group.dir",
                                                 "group"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Item.ITEM,
                  new Link(Item.ITEM,
                           "/entry/" + Config.get("resource:html/item.dir",
                                                 "item"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(God.GOD,
                  new Link(God.GOD,
                           "/entry/" + Config.get("resource:html/god.dir",
                                                  "god"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Event.EVENT,
                  new Link(Event.EVENT,
                           "/entry/" + Config.get("resource:html/event.dir",
                                                  "event"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(net.ixitxachitls.dma.output.commands.Class.CLASS,
                  new Link(net.ixitxachitls.dma.output.commands.Class.CLASS,
                           "/entry/" + Config.get("resource:html/class.dir",
                                                  "class"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Spell.SPELL,
                  new Link(Spell.SPELL,
                           "/entry/" + Config.get("resource:html/spell.dir",
                                                  "basespell"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
//     s_actions.put(SpellRef.SPELLREF,
//                   new Reference(Spell.SPELL, BaseSpell.TYPE,
//                                 "/entry/"
//                                 + Config.get("resource:html/spell.dir",
//                                              "basespell"),
//                                 Config.get("resource:html/extension.html",
//                                            ".html")));
    s_actions.put(Feat.FEAT,
                  new Link(Feat.FEAT,
                           "/entry/" + Config.get("resource:html/feat.dir",
                                                  "basefeat"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Quality.QUALITY,
                  new Link(Quality.QUALITY,
                           "/entry/" + Config.get("resource:html/quality.dir",
                                                  "basequality"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Skill.SKILL,
                  new Link(Skill.SKILL,
                           "/entry/" + Config.get("resource:html/skill.dir",
                                                  "baseskill"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------- getKnownActions ----------------------------

  /**
   * Get all the actions known to this document type.
   *
   * @return      a hash map with all the known converters
   *
   * @undefined   never
   *
   */
  protected Map<String, Action> getKnownActions()
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

  /** The Test. *
   * @hidden
   *
   */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- simple ---------------------------------------------------------

    /** Simple tests. */
    @org.junit.Test
    public void testSimple()
    {
      HTMLDocument doc = new HTMLDocument("title", "type");

      doc.add(new Command
              ("just ",
               new net.ixitxachitls.dma.output.commands.Product("some "),
               new Command("test")));

      assertEquals("simple",
                   "just <a href=\"/entry/baseproduct/some .html\" "
                   + "class=\"Product\" "
                   + "onclick=\"link(event, '/entry/baseproduct/some .html'"
                   + ");\">some </a>test",
                   doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
