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

package net.ixitxachitls.output.pdf;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.collect.ImmutableMap;

import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.output.actions.Delimiter;
import net.ixitxachitls.output.actions.Identity;
import net.ixitxachitls.output.actions.Ignore;
import net.ixitxachitls.output.actions.Pattern;
import net.ixitxachitls.output.actions.Replace;
import net.ixitxachitls.output.actions.Selection;
import net.ixitxachitls.output.actions.Verbatim;
import net.ixitxachitls.output.actions.itext.Count;
import net.ixitxachitls.output.actions.itext.Frac;
import net.ixitxachitls.output.actions.itext.List;
import net.ixitxachitls.output.actions.itext.Picture;
import net.ixitxachitls.output.actions.itext.Span;
import net.ixitxachitls.output.actions.itext.Table;
//import net.ixitxachitls.output.commands.Checked;
import net.ixitxachitls.output.commands.Acute;
import net.ixitxachitls.output.commands.Block;
import net.ixitxachitls.output.commands.Bold;
import net.ixitxachitls.output.commands.Center;
import net.ixitxachitls.output.commands.Color;
import net.ixitxachitls.output.commands.Columns;
import net.ixitxachitls.output.commands.Divider;
import net.ixitxachitls.output.commands.Editable;
import net.ixitxachitls.output.commands.Emph;
import net.ixitxachitls.output.commands.Enumeration;
import net.ixitxachitls.output.commands.Footnotesize;
import net.ixitxachitls.output.commands.Greater;
import net.ixitxachitls.output.commands.Greaterequal;
import net.ixitxachitls.output.commands.Grouped;
import net.ixitxachitls.output.commands.Hat;
import net.ixitxachitls.output.commands.Highlight;
import net.ixitxachitls.output.commands.Hrule;
import net.ixitxachitls.output.commands.Huge;
import net.ixitxachitls.output.commands.Huger;
import net.ixitxachitls.output.commands.ID;
import net.ixitxachitls.output.commands.Icon;
import net.ixitxachitls.output.commands.ImageLink;
import net.ixitxachitls.output.commands.Indent;
import net.ixitxachitls.output.commands.Italic;
import net.ixitxachitls.output.commands.Label;
import net.ixitxachitls.output.commands.Large;
import net.ixitxachitls.output.commands.Larger;
import net.ixitxachitls.output.commands.Largest;
import net.ixitxachitls.output.commands.Left;
import net.ixitxachitls.output.commands.Less;
import net.ixitxachitls.output.commands.Lessequal;
import net.ixitxachitls.output.commands.Linebreak;
import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.output.commands.Navigation;
import net.ixitxachitls.output.commands.Newpage;
import net.ixitxachitls.output.commands.Nopictures;
import net.ixitxachitls.output.commands.NormalSize;
import net.ixitxachitls.output.commands.Page;
import net.ixitxachitls.output.commands.Par;
import net.ixitxachitls.output.commands.Right;
import net.ixitxachitls.output.commands.SansSerif;
import net.ixitxachitls.output.commands.Script;
import net.ixitxachitls.output.commands.Scriptsize;
import net.ixitxachitls.output.commands.Small;
import net.ixitxachitls.output.commands.Sub;
import net.ixitxachitls.output.commands.Subtitle;
import net.ixitxachitls.output.commands.Super;
import net.ixitxachitls.output.commands.Textblock;
import net.ixitxachitls.output.commands.Tiny;
import net.ixitxachitls.output.commands.Title;
import net.ixitxachitls.output.commands.TocEntry;
import net.ixitxachitls.output.commands.Umlaut;
import net.ixitxachitls.output.commands.Underline;
import net.ixitxachitls.output.commands.Value;
import net.ixitxachitls.output.commands.Window;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base document for all output documents used.
 *
 * @file          ITextDocument.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class ITextDocument extends Document
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- ITextDocument ----------------------------

  /**
   * This is a convenience create using the standard dimension of the page
   * from the configuration.
   *
   * @param       inTitle           the HTML title of the document
   *
   */
  public ITextDocument(@Nonnull String inTitle)
  {
    this(inTitle, false);
  }

  //........................................................................
  //----------------------------- ITextDocument ----------------------------

  /**
   * This is a convenience create using the standard dimension of the page
   * from the configuration.
   *
   * @param       inTitle the HTML title of the document
   * @param       inDM    a flag denoting if this is a DM document or not
   *
   */
  public ITextDocument(@Nonnull String inTitle, boolean inDM)
  {
    this(inTitle, inDM, false);
  }

  //........................................................................
  //----------------------------- ITextDocument ----------------------------

  /**
   * This is a convenience method to create using the standard dimension of the
   * page from the configuration.
   *
   * @param       inTitle     the HTML title of the document
   * @param       inDM        a flag denoting if this is a DM document or not
   * @param       inLandscape true for landscape printing, false else
   *
   */
  public ITextDocument(@Nonnull String inTitle, boolean inDM,
                       boolean inLandscape)
  {
    super(inDM);

    m_title     = inTitle;
    m_landscape = inLandscape;

    // start counting with 1 for footnotes
    m_counter = 1;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The document title. */
  protected @Nonnull String m_title;

  /** The date of printing. */
  protected @Nonnull String m_date =
    new SimpleDateFormat("dd.MM.yyyy").format(new Date());

  /** A flag denoting if landscape printing is desired. */
  protected boolean m_landscape = false;

  /** The directory for the files. */
  protected static final @Nonnull String s_dirFiles =
    Config.get("itext/dir.files", "files");

  /** The directory for the files. */
  protected static final @Nonnull String s_dirFilesInternal =
    Config.get("itext/dir.files.internal", "files-internal");

  /** The base resources directory for html. */
  private static final @Nonnull String s_resources =
    Config.get("itext/dir.resources", "html");

  /** The extension for style sheets. */
  private static final @Nonnull String s_pngExtension =
    Config.get("itext/extension.image", ".png");

  /** The known actions. */
  protected static final @Nonnull HashMap<String, Action> s_actions =
    new HashMap<String, Action>();

  static
  {
    s_actions.put(Bold.NAME,
                  new Delimiter(null, null,
                                new String [] { "<b>" },
                                new String [] { "</b>" }, null, null));
    s_actions.put(Underline.NAME,
                  new Delimiter(null, null,
                                new String [] { "<u>" },
                                new String [] { "</u>" }, null, null));
    s_actions.put(Italic.NAME,
                  new Delimiter(null, null,
                                new String [] { "<i>" },
                                new String [] { "</i>" }, null, null));
    // maybe a better solution for the following would be good, because this
    // will not specially mark an emphasized text inside an already emphasized
    // text
    s_actions.put(Emph.NAME,
                  new Delimiter(null, null,
                                new String [] { "<i>" },
                                new String [] { "</i>" }, null, null));
    // actually everything is in sans serif
    s_actions.put(SansSerif.NAME, null);
    s_actions.put(Tiny.NAME,
                  new Delimiter(null, null,
                                new String []
                    { "<font name=\"tiny\">" },
                                new String [] { "</font>" }, null, null));
    s_actions.put(Footnotesize.NAME,
                  new Delimiter(null, null,
                                new String []
                    { "<font name=\"footnote\">" },
                                new String [] { "</font>" }, null, null));
    s_actions.put(Scriptsize.NAME,
                  new Delimiter(null, null,
                                new String []
                    { "<font name=\"script\">" },
                                new String [] { "</font>" }, null, null));
    s_actions.put(Small.NAME,
                  new Delimiter(null, null,
                                new String []
                    { "<font name=\"small\">" },
                                new String [] { "</font>" }, null, null));
    s_actions.put(NormalSize.NAME,
                  new Delimiter(null, null,
                                new String []
                    { "<font name=\"normal\">" },
                                new String [] { "</font>" }, null, null));
    s_actions.put(Large.NAME,
                  new Delimiter(null, null,
                                new String []
                    { "<font name=\"large\">" },
                                new String [] { "</font>" }, null, null));
    s_actions.put(Larger.NAME,
                  new Delimiter(null, null,
                                new String []
                    { "<font name=\"larger\">" },
                                new String [] { "</font>" }, null, null));
    s_actions.put(Largest.NAME,
                  new Delimiter(null, null,
                                new String []
                    { "<font name=\"largest\">" },
                                new String [] { "</font>" }, null, null));
    s_actions.put(Huge.NAME,
                  new Delimiter(null, null,
                                new String []
                    { "<font name=\"huge\">" },
                                new String [] { "</font>" }, null, null));
    s_actions.put(Huger.NAME,
                  new Delimiter(null, null,
                                new String []
                    { "<font name=\"huger\">" },
                                new String [] { "</font>" }, null, null));
    s_actions.put(net.ixitxachitls.output.commands.List.NAME, new List());
    s_actions.put(Indent.NAME,
                  new Pattern("<table spacing-before=\"0\" columns=\"2\" "
                              + "width=\"100\" widths=\"1,99\" "
                              + "split-rows=\"true\" keep-together=\"false\">"
                              + "<cell border-style=\"right\" "
                              + "border-color=\"indent\" "
                              + "border-width=\"2\"/>"
                              + "<cell padding-left=\"2\">$1</cell>"
                              + "</table>"));
    s_actions.put(Left.NAME,
                  new Delimiter(null, null,
                                new String []
                    { "<paragraph halign=\"left\" line-spacing=\"1.0\">" },
                                new String [] { "</paragraph>"} , null,
                                null));
    s_actions.put(Right.NAME,
                  new Delimiter(null, null,
                                new String []
                    { "<paragraph halign=\"right\">"
                                },
                                new String [] { "</paragraph>"} , null,
                                null));
    s_actions.put(Center.NAME,
                  new Delimiter(null, null,
                                new String []
                    { "<paragraph halign=\"center\">" },
                                new String [] { "</paragraph>"} , null,
                                null));
    s_actions.put(Block.NAME,
                  new Delimiter(null, null,
                                new String []
                    { "<paragraph halign=\"justified\">" },
                                new String [] { "</paragraph>"} , null,
                                null));
    s_actions.put(net.ixitxachitls.output.commands.Table.NAME, new Table());
    s_actions.put(Par.NAME,
                  new Delimiter("<br />",
                                null, null, null, null, null));
    s_actions.put(Linebreak.NAME,
                  new Delimiter("<br />", null, null, null, null,
                                null));
    s_actions.put(Enumeration.NAME,
                  new Delimiter("<list numbered=\"true\" "
                                + "symbol-indent=\"10\">",
                                "</list>",
                                new String [] { "  <list-item>" },
                                new String [] { "  </list-item>"}, null,
                                null));
    s_actions.put(Hrule.NAME, new Ignore());
//                   new Pattern("<table columns=\"1\"[[ width=\"%1\"]]>"
//                               + "<cell border-width=\"0.1\" "
//                               + "padding-top=\"5\" "
//                               + "border-color=\"$1\" "
//                               + "border-style=\"bottom\">"
//                               + "</cell></table>"));
    s_actions.put(Title.NAME,
                  new Pattern("<paragraph font=\"title\">"
                              + "$1</paragraph>"
                              + "<table columns=\"1\" "
                              + "spacing-after=\"5\">"
                              + "<cell border-width=\"1\" "
                              + "border-color=\"title\" "
                              + "border-style=\"bottom\">"
                              + "</cell></table>"));
    s_actions.put(Subtitle.NAME,
                  new Pattern("<paragraph font=\"subtitle\" halign=\"left\">"
                              + "$1"
                              + "</paragraph>"));
    s_actions.put(net.ixitxachitls.output.commands.Picture.NAME,
                  new Picture(net.ixitxachitls.output.commands.Picture.NAME,
                              true, true));
    s_actions.put(net.ixitxachitls.output.commands.Image.NAME,
                  new Picture(net.ixitxachitls.output.commands.Image.NAME,
                              false, false));
    s_actions.put(Icon.NAME, new Picture(Icon.NAME, true, true, 50, 0));
    s_actions.put(ImageLink.NAME, new Ignore());
    s_actions.put(Label.NAME, new Ignore());
//     s_actions.put(LABEL,
//                   new Picture(LABEL, false, false,
//                               Config.get("resource:itext/dir.labels",
//                                          "itext/labels")));
    s_actions.put(Nopictures.NAME, new Identity(1));
    s_actions.put(Textblock.NAME,
                  new Pattern
                  ("<paragraph line-spacing=\"1.1\" spacing-before=\"10\">"
                   + "$1</paragraph><br />"));
    s_actions.put(Link.NAME, new Identity(1));
    s_actions.put(Editable.NAME, new Identity(2));
    s_actions.put(Hat.NAME,
                  new Replace(new Replace.Replacement []
                    {
                      new Replace.Replacement("u", "&#x00FB;"),
                      new Replace.Replacement("U", "&#x00DB;"),
                      new Replace.Replacement("o", "&#x00F4;"),
                      new Replace.Replacement("O", "&#x00D4;"),
                      new Replace.Replacement("a", "&#x00E2;"),
                      new Replace.Replacement("A", "&#x00C2;"),
                    }));
    s_actions.put(Umlaut.NAME,
                  new Replace(new Replace.Replacement []
                    {
                      new Replace.Replacement("u", "&#x00FC;"),
                      new Replace.Replacement("U", "&#x00DC;"),
                      new Replace.Replacement("o", "&#x00F6;"),
                      new Replace.Replacement("O", "&#x00D6;"),
                      new Replace.Replacement("a", "&#x00E4;"),
                      new Replace.Replacement("A", "&#x00F4;"),
                    }));
    s_actions.put(Acute.NAME,
                  new Replace(new Replace.Replacement []
                    {
                      new Replace.Replacement("e", "&#x00E9;"),
                      new Replace.Replacement("E", "&#x00C9;"),
                      new Replace.Replacement("u", "&#x00FA;"),
                      new Replace.Replacement("U", "&#x00DA;"),
                    }));
    s_actions.put(Window.NAME, new Identity(1));
    s_actions.put(net.ixitxachitls.output.commands.Frac.NAME, new Frac());
    s_actions.put(Highlight.NAME, null);
    s_actions.put(ID.NAME, new Identity(2));
    s_actions.put(Super.NAME, new Pattern("<super>$1</super>"));
    s_actions.put(Sub.NAME, new Pattern("<sub>$1</sub>"));
    // cannot easily be implemented...
    s_actions.put(net.ixitxachitls.output.commands.Footnote.NAME, null);
    s_actions.put(Navigation.NAME, null);
    s_actions.put(Color.NAME,
                  new Pattern("<font color=\"$1\">$2</font>"));
    s_actions.put(Newpage.NAME,
                  new Pattern("<new-page />"));
    s_actions.put(net.ixitxachitls.output.commands.Span.NAME, new Span());
    s_actions.put(net.ixitxachitls.output.commands.Count.NAME, new Count());
    s_actions.put(Less.NAME, new Pattern("&lt;"));
    s_actions.put(Greater.NAME, new Pattern("&gt;"));
    s_actions.put(Lessequal.NAME, new Pattern("&lt;="));
    s_actions.put(Greaterequal.NAME, new Pattern("&gt;="));
    s_actions.put(net.ixitxachitls.output.commands.Verbatim.NAME,
                  new Verbatim());
    s_actions.put
      (Divider.NAME,
       new Selection(0, 1, new ImmutableMap.Builder<String, Action>()
                     .put("files",
                          new Pattern("<paragraph spacing-after=\"5\">$2"
                                      + "</paragraph>"))
                     .build()));
    s_actions.put(Page.NAME,
                  new Pattern("<table cell-valign=\"top\" width=\"100\" "
                              + "columns=\"2\" class=\"page\" "
                              + "split-rows=\"true\" keep-together=\"false\" "
                              + "widths=\"90,10\">"
                              + "<cell valign=\"top\" halign=\"left\">"
                              + "<table cell-valign=\"top\" width=\"100\" "
                              + "columns=\"2\" class=\"page\" "
                              + "split-rows=\"true\" keep-together=\"false\" "
                              + "widths=\"60,40\">"
                              + "<cell valign=\"top\" halign=\"left\">"
                              + "$1"
                              + "<font name=\"small\">"
                              + "$5"
                              + "</font>"
                              + "</cell>"
                              + "<cell valign=\"top\" halign=\"left\">"
                              + "$3"
                              + "</cell>"
                              + "</table>"
                              + "</cell>"
                              + "<cell valign=\"top\" halign=\"left\">"
                              + "$2"
                              + "</cell>"
                              + "</table>"
                              + "<table cell-valign=\"top\" width=\"100\" "
                              + "columns=\"2\" class=\"page\" "
                              + "split-rows=\"true\" keep-together=\"false\" "
                              + "widths=\"70,30\">"
                              + "<cell valign=\"top\" halign=\"left\">"
                              + "<font name=\"small\">"
                              + "$6"
                              + "</font>"
                              + "$4"
                              + "</cell>"
                              + "<cell valign=\"top\" halign=\"left\" "
                              + "padding-left=\"15\">"
                              + "<font name=\"script\">"
                              + "$7"
                              + "</font>"
                              + "</cell></table>"));
    s_actions.put(TocEntry.NAME,
                  new Pattern("<outline name=\"$1\">$1</outline>"));
    s_actions.put(Grouped.NAME, new Identity(new int [] { 1 }));
    s_actions.put(Value.NAME,
                  new Pattern("<table columns=\"2\" width=\"100\" "
                              + "widths=\"1,6\" split-rows=\"true\" "
                              + "keep-together=\"false\">"
                              + "<cell bgcolor=\"$1\" class=\"label\" "
                              + "valign=\"top\">$2</cell>"
                              + "<cell class=\"value\">$3</cell>"
                              + "</table>"));
    s_actions.put(Columns.NAME,
                  new Pattern("<column-text columns=\"$1\" padding=\"10\">"
                              + "<font>$2</font></column-text>"));
//     s_actions.put(Checked.NAME,
//                   new Pattern("<font name=\"symbol\">c</font> $1"));
    s_actions.put(Script.NAME, new Ignore());

    s_actions.put("command", new Action());
    s_actions.put("baseCommand", new Action());
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

  //--------------------------------- add ----------------------------------

  /**
   * Add the given String to the current position of the document. Make sure
   * no \ stay in the String.
   *
   * @param       inText the text to add
   *
   */
  @Override
  public void add(@Nonnull String inText)
  {
    super.add(inText.replaceAll("\\\\", "*backslash*"));
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- toString -------------------------------

  /**
   * Return the contents of the document as a single String.
   *
   * @return      The complete contents of the document.
   *
   */
  @Override
public @Nonnull String toString()
  {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<!DOCTYPE ITEXT SYSTEM \"http://itext.sourceforge.net/itext.dtd\">\n"
      + "<document size=\"A4\" margin-left=\"25\" margin-right=\"25\" "
      + "margin-top=\"25\" margin-bottom=\"25\""
      + (m_landscape ? " rotate=\"true\"" : "") + ">\n"

      // meta information
      + "<meta title=\"" + m_title + "\" subject=\"" + m_title + "\" "
      + "author=\"jDMA\" creator=\"jDMA\" creation-date=\"" + m_date
      + "\" />"

      // color definitions
      + "<color-def name=\"title\" color-space=\"RGB\" "
      + "value=\"0x00,0x00,0x80\" />"
      + "<color-def name=\"subtitle\" color-space=\"RGB\" "
      + "value=\"0x00,0x00,0x40\" />"
      + "<color-def name=\"dm\" color-space=\"RGB\" "
      + "value=\"0xee,0x88,0x00\" />"
      + "<color-def name=\"player-notes\" color-space=\"RGB\" "
      + "value=\"0x00,0x80,0x00\" />"
      + "<color-def name=\"dm-notes\" color-space=\"RGB\" "
      + "value=\"0xee,0x88,0x00\" />"
      + "<color-def name=\"count\" color-space=\"RGB\" "
      + "value=\"0x00,0x40,0x00\" />"
      + "<color-def name=\"count-max\" color-space=\"RGB\" "
      + "value=\"0xdd,0xdd,0xdd\" />"
      + "<color-def name=\"header\" color-space=\"RGB\" "
      + "value=\"0xaa,0xaa,0xaa\" />"
      + "<color-def name=\"error\" color-space=\"RGB\" "
      + "value=\"0xff,0x00,0x00\" />"
      + "<color-def name=\"incomplete\" color-space=\"RGB\" "
      + "value=\"0xff,0xaa,0xaa\" />"
      + "<color-def name=\"table-odd\" color-space=\"RGB\" "
      + "value=\"0xff,0xff,0xff\" />"
      + "<color-def name=\"table-even\" color-space=\"RGB\" "
      + "value=\"0xff,0xff,0xff\" />"
      + "<color-def name=\"description-odd\" color-space=\"RGB\" "
      + "value=\"0xff,0xff,0xff\" />"
      + "<color-def name=\"description-even\" color-space=\"RGB\" "
      + "value=\"0xff,0xff,0xff\" />"
      + "<color-def name=\"keep-odd\" color-space=\"RGB\" "
      + "value=\"0xff,0xff,0xff\" />"
      + "<color-def name=\"keep-even\" color-space=\"RGB\" "
      + "value=\"0xff,0xff,0xff\" />"
      + "<color-def name=\"colored-odd\" color-space=\"RGB\" "
      + "value=\"0xff,0xff,0xff\" />"
      + "<color-def name=\"colored-even\" color-space=\"RGB\" "
      + "value=\"0xff,0xff,0xff\" />"
      + "<color-def name=\"base-odd\" color-space=\"RGB\" "
      + "value=\"0xff,0xff,0xff\" />"
      + "<color-def name=\"base-even\" color-space=\"RGB\" "
      + "value=\"0xff,0xff,0xff\" />"

      + "<color-def name=\"Place\" color-space=\"RGB\" "
      + "value=\"0x80,0x40,0x00\" />"
      + "<color-def name=\"Product\" color-space=\"RGB\" "
      + "value=\"0x40,0x40,0x40\" />"
      + "<color-def name=\"NPC\" color-space=\"RGB\" "
      + "value=\"0x80,0x00,0x00\" />"
      + "<color-def name=\"Monster\" color-space=\"RGB\" "
      + "value=\"0x00,0x80,0x00\" />"
      + "<color-def name=\"Group\" color-space=\"RGB\" "
      + "value=\"0xff,0x80,0x80\" />"
      + "<color-def name=\"Item\" color-space=\"RGB\" "
      + "value=\"0x00,0x00,0x80\" />"
      + "<color-def name=\"God\" color-space=\"RGB\" "
      + "value=\"0x80,0x80,0x00\" />"
      + "<color-def name=\"Event\" color-space=\"RGB\" "
      + "value=\"0x80,0x00,0x80\" />"
      + "<color-def name=\"Class\" color-space=\"RGB\" "
      + "value=\"0xff,0x80,0xff\" />"
      + "<color-def name=\"Spell\" color-space=\"RGB\" "
      + "value=\"0x80,0x80,0xff\" />"
      + "<color-def name=\"Domain\" color-space=\"RGB\" "
      + "value=\"0x80,0x80,0xff\" />"
      + "<color-def name=\"Feat\" color-space=\"RGB\" "
      + "value=\"0x00,0x80,0x80\" />"
      + "<color-def name=\"Skill\" color-space=\"RGB\" "
      + "value=\"0x80,0xff,0xff\" />"
      + "<color-def name=\"Quality\" color-space=\"RGB\" "
      + "value=\"0x80,0xff,0xff\" />"
      + "<color-def name=\"#AAAAAA\" color-space=\"RGB\" "
      + "value=\"0xaa,0xaa,0xaa\" />"
      + "<color-def name=\"BaseCharacter\" color-space=\"RGB\" "
      + "value=\"0x33,0x99,0xcc\" />"
      + "<color-def name=\"Character\" color-space=\"RGB\" "
      + "value=\"0x33,0x99,0xcc\" />"
      + "<color-def name=\"BaseItem\" color-space=\"RGB\" "
      + "value=\"0x00,0x00,0x80\" />"
      + "<color-def name=\"BaseProduct\" color-space=\"RGB\" "
      + "value=\"0x80,0x80,0x80\" />"
      + "<color-def name=\"colored-even\" color-space=\"RGB\" "
      + "value=\"0xee,0xee,0xee\" />"
      + "<color-def name=\"indent\" color-space=\"RGB\" "
      + "value=\"0xcc,0xcc,0xcc\" />"

      // font definitions
      + "<font-def name=\"tiny\" family=\"Helvetica\" style=\"normal\" "
      + "size=\"3\"/>"
      + "<font-def name=\"footnote\" family=\"Helvetica\" style=\"normal\" "
      + "size=\"4\"/>"
      + "<font-def name=\"script\" family=\"Helvetica\" style=\"normal\" "
      + "size=\"6\"/>"
      + "<font-def name=\"small\" family=\"Helvetica\" style=\"normal\" "
      + "size=\"8\"/>"
      + "<font-def name=\"normal\" family=\"Helvetica\" style=\"normal\" "
      + "size=\"10\"/>"
      + "<font-def name=\"large\" family=\"Helvetica\" style=\"normal\" "
      + "size=\"12\"/>"
      + "<font-def name=\"larger\" family=\"Helvetica\" style=\"normal\" "
      + "size=\"14\"/>"
      + "<font-def name=\"largest\" family=\"Helvetica\" style=\"normal\" "
      + "size=\"16\"/>"
      + "<font-def name=\"huge\" family=\"Helvetica\" style=\"normal\" "
      + "size=\"18\"/>"
      + "<font-def name=\"huger\" family=\"Helvetica\" style=\"normal\" "
      + "size=\"20\"/>"
      + "<font-def name=\"title\" family=\"Helvetica\" style=\"bold\" "
      + "size=\"16\" color=\"title\"/>"
      + "<font-def name=\"subtitle\" family=\"Helvetica\" style=\"bold\" "
      + "size=\"12\" color=\"subtitle\"/>"
      + "<font-def name=\"count-max\" family=\"Helvetica\" "
      + "style=\"line-through\" color=\"count-max\"/>"
      + "<font-def name=\"count\" family=\"Helvetica\" "
      + "color=\"count\"/>"
      + "<font-def name=\"header\" family=\"Helvetica\" "
      + "size=\"8\" color=\"header\"/>"
      + "<font-def name=\"tiny-header\" family=\"Helvetica\" "
      + "size=\"5\" color=\"header\"/>"
      + "<font-def name=\"dm\" color=\"dm\"/>"
      + "<register-font family=\"Webdings\" "
      + "source=\"fonts/webdings.ttf\" />"
      + "<font-def size=\"6\" name=\"symbol\" family=\"Webdings\"/>"

      // style definitions
      + "<style-def name=\".count\" border-style=\"box\" "
      + "border-color=\"count\" font=\"count\"/>"
      + "<style-def name=\".count-max\" border-style=\"box\" "
      + "border-color=\"count-max\" bgcolor=\"dm\" font=\"count-max\" />"
      + "<style-def name=\".table.colored\" padding-bottom=\"5\" "
      + "padding-left=\"5\" padding-right=\"5\" />"
      + "<style-def name=\".description\" padding-bottom=\"5\" />"
      + "<style-def name=\".label\" padding-left=\"5\" padding-top=\"0\" "
      + "padding-bottom=\"5\" border-width=\"0.1\" "
      + "border-color=\"white\" border-style=\"top\"/>"
      + "<style-def name=\".value\" padding-left=\"5\" padding-top=\"0\" "
      + "padding-bottom=\"5\" />"

      // header
      + "<header rule-width=\"0.1\" rule-color=\"header\">"
      + "<header-part font=\"header\" halign=\"left\">" + m_title
      + "</header-part>"
      + "</header>"

      // the footer
      + "<footer rule-width=\"0.1\" rule-color=\"header\">"
      + "<footer-part padding-top=\"10\" font=\"header\" width=\"400\" "
      + "halign=\"center\">"
      //+ "<table columns=\"1\" cell-halign=\"center\">"
      //+ "<cell halign=\"center\">"
      //+ "<image width=\"50\" source=\"itext/DMA_logo.png\" /></cell>"
      //+ "<cell halign=\"center\">"
      + "<font name=\"tiny-header\">" + getFooter() + "</font>"
      //+ </cell>"
      //+ "</table>"
      + "</footer-part>"
      + "<footer-part padding-top=\"10\" font=\"header\" halign=\"left\">"
      + m_date
      + "</footer-part>"
      + "<footer-part padding-top=\"10\" font=\"header\" halign=\"right\">"
      + "${pageNumber} / ${pageCount}"
      + "</footer-part>"
      + "</footer>"

      // the real body text
      + getBody()

      // the end of the document
      + "</document>\n";
  }

  //........................................................................
  //------------------------------- getBody --------------------------------

  /**
   * Create a string containing the body only of the document.
   *
   * @return      the body of the document
   *
   */
  protected String getBody()
  {
    // Don't know why it's necessary to escape twice...
    return super.toString().replaceAll("&(?![#\\w]+\\;)", "&amp;amp;");
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- simple ---------------------------------------------------------

    /** Simple tests. */
    @org.junit.Test
    public void simple()
    {
      ITextDocument doc = new ITextDocument("title");

      doc.add(new net.ixitxachitls.output.commands.Command
              (new Object []
                { new net.ixitxachitls.output.commands.Command("just "),
                  new net.ixitxachitls.output.commands.Bold("some "),
                  new net.ixitxachitls.output.commands.Command("test"), }));

      assertEquals("simple", "just <b>some </b>test",
                   doc.getBody());
    }

    //......................................................................
    //----- wrapping -------------------------------------------------------

    /** Testing of word wrapping. */
    @org.junit.Test
    public void wrapping()
    {
      ITextDocument doc = new ITextDocument("title", true, true);

      doc.add(new net.ixitxachitls.output.commands.Command
              (new net.ixitxachitls.output.commands.Command
               ("we do something similar to the stuff we did before, "),
               new net.ixitxachitls.output.commands.Bold("but"),
               new net.ixitxachitls.output.commands.Command
               (" this time the whole thing is to be wrapped over "
                + "several lines, and we want to check that this "
                + "works.")));

      assertEquals("command",
                   "we do something similar to the stuff we "
                   + "did before, <b>but</b> this time the whole "
                   + "thing is to be wrapped over several "
                   + "lines, and we want to check that this "
                   + "works.", doc.getBody());
    }

    //......................................................................
    //----- justification --------------------------------------------------

    /** Testing justification. */
    @org.junit.Test
    public void justification()
    {
      ITextDocument doc = new ITextDocument("title", false, true);

      doc.add(new net.ixitxachitls.output.commands.Center("center"));
      doc.add(new net.ixitxachitls.output.commands.Left("left"));
      doc.add(new net.ixitxachitls.output.commands.Right("right"));
      doc.add(new net.ixitxachitls.output.commands
              .Block("and now, finally the whole text set blocked, i.e. "
                     + "aligned to the left and to the right at the same "
                     + "time"));

      assertEquals("command",
                   "<paragraph halign=\"center\">"
                   + "center"
                   + "</paragraph>"
                   + "<paragraph halign=\"left\" line-spacing=\"1.0\">"
                   + "left"
                   + "</paragraph>"
                   + "<paragraph halign=\"right\">"
                   + "right"
                   + "</paragraph>"
                   + "<paragraph halign=\"justified\">"
                   + "and now, finally the whole text set "
                   + "blocked, i.e. aligned to the left and to "
                   + "the right at the same time"
                   + "</paragraph>",
                   doc.getBody());
    }

    //......................................................................
    //----- frac -----------------------------------------------------------

    /** Test printing of fractures. */
    @org.junit.Test
    public void frac()
    {
      ITextDocument doc = new ITextDocument("title");

      doc.add(new net.ixitxachitls.output.commands.BaseCommand
              ("1/10 = \\frac{1}{10}"));

      assertEquals("frac", "1/10 = <super>1</super>/<sub>10</sub>",
                   doc.getBody());
    }

    //......................................................................
  }

  //........................................................................
}
