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

import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.output.actions.html.Link;
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
   *
   */
  public HTMLDocument(String inTitle)
  {
    super(inTitle);
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
    s_actions.put(Place.NAME,
                  new Link(Place.NAME,
                           "/entry/" + Config.get("resource:html/place.dir",
                                                 "place"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Product.NAME,
                  new Link(Product.NAME,
                           "/entry/" + Config.get("resource:html/product.dir",
                                                 "baseproduct"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(NPC.NAME,
                  new Link(NPC.NAME,
                           "/entry/" + Config.get("resource:html/npc.dir",
                                           "npc"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Monster.NAME,
                  new Link(Monster.NAME,
                           "/entry/" + Config.get("resource:html/monster.dir",
                                                 "basemonster"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Group.NAME,
                  new Link(Group.NAME,
                           "/entry/" + Config.get("resource:html/group.dir",
                                                 "group"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Item.NAME,
                  new Link(Item.NAME,
                           "/entry/" + Config.get("resource:html/item.dir",
                                                 "item"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(God.NAME,
                  new Link(God.NAME,
                           "/entry/" + Config.get("resource:html/god.dir",
                                                  "god"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Event.NAME,
                  new Link(Event.NAME,
                           "/entry/" + Config.get("resource:html/event.dir",
                                                  "event"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(net.ixitxachitls.output.commands.Class.NAME,
                  new Link(net.ixitxachitls.output.commands.Class.NAME,
                           "/entry/" + Config.get("resource:html/class.dir",
                                                  "class"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Spell.NAME,
                  new Link(Spell.NAME,
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
    s_actions.put(Feat.NAME,
                  new Link(Feat.NAME,
                           "/entry/" + Config.get("resource:html/feat.dir",
                                                  "basefeat"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Quality.NAME,
                  new Link(Quality.NAME,
                           "/entry/" + Config.get("resource:html/quality.dir",
                                                  "basequality"),
                           Config.get("resource:html/extension.html",
                                      ".html")));
    s_actions.put(Skill.NAME,
                  new Link(Skill.NAME,
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
      HTMLDocument doc = new HTMLDocument("title");

      doc.add(new Command
              ("just ",
               new net.ixitxachitls.output.commands.Product("some "),
               new Command("test")));

      assertEquals("simple",
                   "just <a href=\"/entry/baseproduct/some \" "
                   + "class=\"Product\" "
                   + "onclick=\"return util.link(event, "
                   + "'/entry/baseproduct/some ');\">some </a>test",
                   doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
