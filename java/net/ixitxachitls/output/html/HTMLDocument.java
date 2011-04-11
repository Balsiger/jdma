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

package net.ixitxachitls.output.html;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import net.ixitxachitls.output.Document;
// import net.ixitxachitls.dma.Global;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.output.actions.Delimiter;
import net.ixitxachitls.output.actions.Pattern;
import net.ixitxachitls.output.actions.Replace;
import net.ixitxachitls.output.actions.Verbatim;
import net.ixitxachitls.output.actions.WordUpperCase;
import net.ixitxachitls.output.actions.html.Files;
import net.ixitxachitls.output.actions.html.Footer;
import net.ixitxachitls.output.actions.html.Frac;
import net.ixitxachitls.output.actions.html.Link;
import net.ixitxachitls.output.actions.html.Menu;
import net.ixitxachitls.output.actions.html.Navigation;
import net.ixitxachitls.output.actions.html.Picture;
import net.ixitxachitls.output.actions.html.Table;
import net.ixitxachitls.output.commands.Acute;
import net.ixitxachitls.output.commands.BaseCommand;
import net.ixitxachitls.output.commands.Block;
import net.ixitxachitls.output.commands.Bold;
import net.ixitxachitls.output.commands.Button;
import net.ixitxachitls.output.commands.Card;
import net.ixitxachitls.output.commands.Center;
import net.ixitxachitls.output.commands.Color;
import net.ixitxachitls.output.commands.Columns;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Count;
import net.ixitxachitls.output.commands.Divider;
import net.ixitxachitls.output.commands.Editable;
import net.ixitxachitls.output.commands.Emph;
import net.ixitxachitls.output.commands.Enumeration;
import net.ixitxachitls.output.commands.Footnotesize;
import net.ixitxachitls.output.commands.Grave;
import net.ixitxachitls.output.commands.Greater;
import net.ixitxachitls.output.commands.Greaterequal;
import net.ixitxachitls.output.commands.Grouped;
import net.ixitxachitls.output.commands.Hat;
import net.ixitxachitls.output.commands.Hidden;
import net.ixitxachitls.output.commands.Hide;
import net.ixitxachitls.output.commands.Highlight;
import net.ixitxachitls.output.commands.Hrule;
import net.ixitxachitls.output.commands.Huge;
import net.ixitxachitls.output.commands.Huger;
import net.ixitxachitls.output.commands.ID;
import net.ixitxachitls.output.commands.Icon;
import net.ixitxachitls.output.commands.Image;
import net.ixitxachitls.output.commands.ImageLink;
import net.ixitxachitls.output.commands.Italic;
import net.ixitxachitls.output.commands.Label;
import net.ixitxachitls.output.commands.Large;
import net.ixitxachitls.output.commands.Larger;
import net.ixitxachitls.output.commands.Largest;
import net.ixitxachitls.output.commands.Left;
import net.ixitxachitls.output.commands.Less;
import net.ixitxachitls.output.commands.Lessequal;
import net.ixitxachitls.output.commands.Linebreak;
import net.ixitxachitls.output.commands.List;
import net.ixitxachitls.output.commands.Newpage;
import net.ixitxachitls.output.commands.Nopictures;
import net.ixitxachitls.output.commands.NormalSize;
import net.ixitxachitls.output.commands.OverlayIcon;
import net.ixitxachitls.output.commands.OverviewFiles;
import net.ixitxachitls.output.commands.Page;
import net.ixitxachitls.output.commands.Par;
import net.ixitxachitls.output.commands.Right;
import net.ixitxachitls.output.commands.SansSerif;
import net.ixitxachitls.output.commands.Script;
import net.ixitxachitls.output.commands.Scriptsize;
import net.ixitxachitls.output.commands.Serif;
import net.ixitxachitls.output.commands.Small;
import net.ixitxachitls.output.commands.Span;
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
 * This is the document for html documents.
 *
 * @file          HTMLDocument.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class HTMLDocument extends Document
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- HTMLDocument ----------------------------

  /**
   * This is a convenience create using the standard dimension of the page
   * from the configuration.
   *
   * @param       inTitle           the HTML title of the document
   *
   */
  public HTMLDocument(@Nonnull String inTitle)
  {
    m_title = inTitle;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The document title. */
  protected @Nonnull String m_title;

  /** The directory for the files. */
  protected static final @Nonnull String s_dirFiles =
    Config.get("resource:html/dir.files", "/files");

  /** The directory for the files. */
  protected static final @Nonnull String s_dirFilesInternal =
    Config.get("resource:html/dir.files.internal", "/files-internal");

  /** The base resources directory for html. */
  private static final @Nonnull String s_resources =
    Config.get("resource:html/dir.resources", "html");

  /** The directory with the icons. */
  protected static final @Nonnull String s_dirIcons =
    Config.get("resource:html/dir.icons", "/icons");

  /** The known actions. */
  protected static final @Nonnull Map<String, Action> s_actions =
    new HashMap<String, Action>();

  /** A simple document for easy conversions. This should be last or the
   * static values above will not be correctly set!*/
  protected static final @Nonnull HTMLDocument s_simple =
    new HTMLDocument("simple");

  static
  {
    s_actions.put(Bold.BOLD,
                  new Delimiter(null, null,
                                new String [] { "<strong>" },
                                new String [] { "</strong>" }, null, null));
    s_actions.put(Underline.UNDERLINE,
                  new Delimiter(null, null,
                                new String [] { "<u>" },
                                new String [] { "</u>" }, null, null));
    s_actions.put(Italic.ITALIC,
                  new Delimiter(null, null,
                                new String [] { "<i>" },
                                new String [] { "</i>" }, null, null));
    s_actions.put(Emph.EMPH,
                  new Delimiter(null, null,
                                new String [] { "<em>" },
                                new String [] { "</em>" }, null, null));
    s_actions.put(SansSerif.SANS_SERIF,
                  new Delimiter(null, null,
                                new String []
                    { "<span class=\"font-sansserif\">" },
                                new String [] { "</span>" }, null, null));
    s_actions.put(Serif.SERIF,
                  new Delimiter(null, null,
                                new String []
                    { "<span class=\"font-serif\">" },
                                new String [] { "</span>" }, null, null));
    s_actions.put(Tiny.TINY,
                  new Delimiter(null, null,
                              new String [] { "<span class=\"size-tiny\">" },
                                new String [] { "</span>" }, null, null));
    s_actions.put(Footnotesize.FOOTNOTE_SIZE,
                  new Delimiter(null, null,
                                new String [] { "<span "
                                                + "class=\"size-footnote\">" },
                                new String [] { "</span>" }, null, null));
    s_actions.put(Scriptsize.SCRIPT_SIZE,
                  new Delimiter(null, null,
                                new String [] { "<span "
                                                + "class=\"size-script\">" },
                                new String [] { "</span>" }, null, null));
    s_actions.put(Small.SMALL,
                  new Delimiter(null, null,
                                new String [] { "<span "
                                                + "class=\"size-small\">" },
                                new String [] { "</span>" }, null, null));
    s_actions.put(NormalSize.NORMAL_SIZE,
                  new Delimiter(null, null,
                                new String [] { "<span "
                                                + "class=\"size-normal\">" },
                                new String [] { "</span>" }, null, null));
    s_actions.put(Large.LARGE,
                  new Delimiter(null, null,
                                new String [] { "<span "
                                                + "class=\"size-large\">" },
                                new String [] { "</span>" }, null, null));
    s_actions.put(Larger.LARGER,
                  new Delimiter(null, null,
                                new String [] { "<span "
                                                + "class=\"size-larger\">" },
                                new String [] { "</span>" }, null, null));
    s_actions.put(Largest.LARGEST,
                  new Delimiter(null, null,
                                new String [] { "<span "
                                                + "class=\"size-largest\">" },
                                new String [] { "</span>" }, null, null));
    s_actions.put(Huge.HUGE,
                  new Delimiter(null, null,
                                new String [] { "<span "
                                                + "class=\"size-huge\">" },
                                new String [] { "</span>" }, null, null));
    s_actions.put(Huger.HUGER,
                  new Delimiter(null, null,
                                new String [] { "<span "
                                                + "class=\"size-huger\">" },
                                new String [] { "</span>" }, null, null));
    s_actions.put(List.LIST,
                  new Delimiter("\n<ul>\n", "\n</ul>\n",
                                new String [] { "  <li>" },
                                new String [] { "</li>\n" }, null, null));
    s_actions.put(Left.LEFT,
                  new Delimiter(null, null,
                                new String [] { "\n<div "
                                                + "class=\"align-left\">\n" },
                                new String [] { "\n</div>\n"} , null, null));
    s_actions.put(Right.RIGHT,
                  new Delimiter(null, null,
                                new String [] { "\n<div "
                                                + "class=\"align-right\">\n" },
                                new String [] { "\n</div>\n"} , null, null));
    s_actions.put(Center.CENTER,
                  new Delimiter(null, null,
                                new String [] { "\n<div "
                                                + "class=\"align-center\">\n"
                                },
                                new String [] { "\n</div>\n" }, null, null));
    s_actions.put(Block.BLOCK,
                  new Delimiter(null, null,
                                new String [] { "\n<div "
                                                + "class=\"align-justify\">\n"
                                },
                                new String [] { "\n</div>\n" }, null, null));
    s_actions.put(net.ixitxachitls.output.commands.Table.TABLE, new Table());
    s_actions.put(Par.PAR,
                  new Delimiter("\n<p />\n", null, null, null, null, null));
    s_actions.put(Linebreak.LINE_BREAK,
                  new Delimiter("<br />\n", null, null, null, null, null));
    s_actions.put(Enumeration.ENUMERATION,
                  new Delimiter("\n<ol>\n", "\n</ol>\n",
                                new String [] { "  <li>" },
                                new String [] { "</li>\n" }, null, null));
    s_actions.put(Hrule.HRULE,
                  new Pattern("\n<hr[[ width=\"%1%\"]]>\n"));
    s_actions.put(Title.TITLE,
                  new Pattern("\n<h1[[ class=\"%1\"]]>$1</h1>"
                              + "[[<div class=\"subtitletext\">"
                              + "(%2)</div>]]\n"));
    s_actions.put(Subtitle.SUBTITLE,
                  new Pattern("\n<h2[[ CLASS=%1]]>$1</h2>\n"));
    s_actions.put(net.ixitxachitls.output.commands.Picture.PICTURE,
                  new Picture(net.ixitxachitls.output.commands.Picture.PICTURE,
                              true, true, null));
    s_actions.put(Icon.ICON,
                  new Picture(Icon.ICON, true, true, s_dirIcons));
    s_actions.put(OverlayIcon.OVERLAY_ICON,
                  new Picture(OverlayIcon.OVERLAY_ICON, true, true,
                              s_dirIcons));
    s_actions.put(Image.IMAGE, new Picture(Image.IMAGE, false, false, null));
    s_actions.put(Label.LABEL,
                  new Picture(Label.LABEL, false, false,
                              Config.get("resource:html/dir.labels",
                                         "/icons/labels")));
    s_actions.put(Button.BUTTON,
                  new Pattern("<input type='button' value='$1' "
                              + "onclick='$2' />"));
    s_actions.put(Nopictures.NO_PICTURES, null);
    s_actions.put(Textblock.TEXT_BLOCK,
                  new Pattern("<div class=\"textblock %1\">$1</div>"));
    s_actions.put(net.ixitxachitls.output.commands.Link.LINK,
                  new Link(net.ixitxachitls.output.commands.Link.LINK, null,
                           Config.get("resource:html/extension.html", "")));
    s_actions.put(ImageLink.IMAGE_LINK,
                  new Link(ImageLink.IMAGE_LINK, null,
                           Config.get("resource:html/extension.html", ""),
                           true));
    s_actions.put(Hat.HAT,
                  new Replace(new Replace.Replacement("u", "&ucirc;"),
                              new Replace.Replacement("U", "&Ucirc;"),
                              new Replace.Replacement("o", "&ocirc;"),
                              new Replace.Replacement("O", "&Ocirc;"),
                              new Replace.Replacement("a", "&acirc;"),
                              new Replace.Replacement("A", "&Acirc;"),
                              new Replace.Replacement("i", "&icirc;"),
                              new Replace.Replacement("I", "&Icirc;")));
    s_actions.put(Umlaut.UMLAUT,
                  new Replace(new Replace.Replacement("u", "&uuml;"),
                              new Replace.Replacement("U", "&Uuml;"),
                              new Replace.Replacement("o", "&ouml;;"),
                              new Replace.Replacement("O", "&Ouml;"),
                              new Replace.Replacement("a", "&auml;"),
                              new Replace.Replacement("A", "&Auml;"),
                              new Replace.Replacement("i", "&iuml;"),
                              new Replace.Replacement("I", "&Iuml;")));
     s_actions.put(Acute.ACUTE,
                   new Replace(new Replace.Replacement("e", "&eacute;"),
                               new Replace.Replacement("E", "&Eacute;"),
                               new Replace.Replacement("u", "&uacute;"),
                               new Replace.Replacement("U", "&Uacute;"),
                               new Replace.Replacement("a", "&aacute;"),
                               new Replace.Replacement("A", "&Aacute;"),
                               new Replace.Replacement("o", "&oacute;"),
                               new Replace.Replacement("O", "&Oacute;"),
                               new Replace.Replacement("i", "&iacute;"),
                               new Replace.Replacement("I", "&Iacute;")));
     s_actions.put(Grave.GRAVE,
                   new Replace(new Replace.Replacement("e", "&egrave;"),
                               new Replace.Replacement("E", "&Egrave;"),
                               new Replace.Replacement("u", "&ugrave;"),
                               new Replace.Replacement("U", "&Ugrave;"),
                               new Replace.Replacement("o", "&ograve;"),
                               new Replace.Replacement("O", "&Ograve;"),
                               new Replace.Replacement("a", "&agrave;"),
                               new Replace.Replacement("A", "&Agrave;"),
                               new Replace.Replacement("i", "&igrave;"),
                               new Replace.Replacement("I", "&Igrave;")));
     s_actions.put(Window.WINDOW,
                   new Pattern("<span onmouseover=\"document.getElementById"
                               + "('window$count').style.visibility="
                               + "'visible'\" "
                               + "onmouseout=\"document.getElementById"
                               + "('window$count').style.visibility='hidden'\" "
                               + "class=\"windowed\">$1</span>"
                               + "<span id=\"window$count\" "
                               + "class=\"window\">"
                               + "$2</span>"));
     s_actions.put(net.ixitxachitls.output.commands.Frac.FRAC, new Frac());
     s_actions.put(net.ixitxachitls.output.commands.Files.FILES,
                   new Files(s_dirFiles, s_dirFilesInternal, s_dirIcons,
                             new String [] { "cover", "back", "inside",
                                             "contents", "electronic" }));
     s_actions.put(OverviewFiles.OVERVIEW_FILES,
                   new Files(s_dirFiles, s_dirFilesInternal, s_dirIcons,
                             new String [] { "cover", "back", "inside",
                                             "contents", "electronic" }));
     s_actions.put(Highlight.HIGHLIGHT,
                   new Pattern("<span onmouseover=\"gui.addAllStyle"
                               + "('highlight-$1', 'highlight-attachment')\" "
                               + "onmouseout=\"gui.removeAllStyle"
                               + "('highlight-$1', 'highlight-attachment')\">"
                               + "$2</span>"));
     s_actions.put(ID.ID, new Pattern("<span id=\"$1\">$2</span>"));
     s_actions.put(Super.SUPER, new Pattern("<sup>$1</sup>"));
     s_actions.put(Sub.SUB, new Pattern("<sub>$1</sub>"));
     s_actions.put(net.ixitxachitls.output.commands.Footnote.FOOTNOTE,
                   new net.ixitxachitls.output.actions.Footnote());
     s_actions.put(net.ixitxachitls.output.commands.Navigation.NAVIGATION,
                   new Navigation(Config.get("resource:html/dir.icons",
                                             "/icons")));
     s_actions.put(net.ixitxachitls.output.commands.Menu.MENU,
                   new Menu(Config.get("resource:html/dir.icons", "/icons")));
     s_actions.put(Card.CARD,
                   new Pattern("<div class=\"card card-$1\" id=\"$2\">"
                               + "$3</div>\n\n"));
     s_actions.put(Color.COLOR,
                   new Pattern("<span class=\"$1\">$2</span>"));
     s_actions.put(Divider.DIVIDER,
                   new Pattern("<div [[id=\"%1\" ]]class=\"$1\">$2</div>"));
     s_actions.put(Span.SPAN,
                   new Pattern("<span class=\"$1\">$2</span>"));
     s_actions.put
       (net.ixitxachitls.output.commands.WordUpperCase.WORD_UPPERCASE,
        new WordUpperCase());
     s_actions.put(Columns.COLUMNS,
                   new Delimiter("<table class=\"columns\"><tr>\n",
                                 "</tr></table>\n",
                                 new String [] { "<td>" },
                                 new String [] { "</td>\n" }, null, null));
     s_actions.put(Hidden.HIDDEN,
                   new Pattern("<span class=\"hidden-label\" "
                               + "onmouseup=\"toggle(document.getElementById"
                               + "('hidden-$count'))\" "
                               + "id=\"hidden-label-$count\">"
                               + "$1</span><br />"
                               + "<div class=\"hidden-text\" "
                               + "id=\"hidden-$count\">$2</div>"));
     s_actions.put(Hide.HIDE,
                   new Pattern("<span style='display:none;' id='$1'>$2</span"));
     s_actions.put(net.ixitxachitls.output.commands.Footer.FOOTER,
                   new Footer());
     s_actions.put(Count.COUNT, new Pattern("$1 (max $2) $3"));
     s_actions.put(Less.LESS, new Pattern("&lt;"));
     s_actions.put(Greater.GREATER, new Pattern("&gt;"));
     s_actions.put(Lessequal.LESS_OR_EQUAL, new Pattern("&lt;="));
     s_actions.put(Greaterequal.GREATER_OR_EQUAL, new Pattern("&gt;="));
     s_actions.put(net.ixitxachitls.output.commands.Verbatim.VERBATIM,
                   new Verbatim());
     s_actions.put(TocEntry.TOC_ENTRY, null);
     s_actions.put(Page.PAGE,
                   new Pattern("<div class=\"align-center\">$2<div>\n"
                               + "<div class=\"main\" id=\"main\">"
                               + "$1\n\n" // title
                               + "$3$4" // pics
                               // table 1
                               + "\\table[description]{1:L;100:L}$5"
                               + "$6" // text
                               + "$7" // remarks
                               + "</div>", true));
     s_actions.put(Newpage.NEWPAGE, null);
     s_actions.put(Grouped.GROUPED, new Pattern("$1##$2##$3##"));
     s_actions.put(Editable.EDITABLE,
                   new Pattern("<dmaeditable key=\"$4\" "
                               + "value=\"$html((@5))\" "
                               + "id=\"$1\" class=\"editable\" "
                               + "entry=\"$2\" "
                               + "type=\"$6\"[[ note=\"%1\"]]"
                               + "[[ values=\"$html((%2))\"]]>"
                               + "<span>$3</span>"
                               + "</dmaeditable>", false));
     s_actions.put(Script.SCRIPT,
                   new Pattern("\n<script type='text/javascript'>$1"
                               + "</script>\n"));
     s_actions.put(Value.VALUE,
                   new Pattern("<div class=\"value\">$1$2</div>"));
     s_actions.put("command", new Action());
     s_actions.put("baseCommand", new Action());
  }
  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getTitle -------------------------------

  /**
   * Get the document title.
   *
   * @return      the document title
   *
   */
  public @Nonnull String getTitle()
  {
    return m_title;
  }

  //........................................................................
  //--------------------------- getKnownActions ----------------------------

  /**
   * Get all the actions known to this document type.
   *
   * @return      a hash map with all the known converters
   *
   */
  protected @Nonnull Map<String, Action> getKnownActions()
  {
    return s_actions;
  }

  //........................................................................

  //----------------------------- simpleConvert ----------------------------

  /**
   * Convert the given Object to a String (using command conversion).
   *
   * @param       inCommand the command to convert
   *
   * @return      the converted result
   *
   */
  public static @Nonnull String simpleConvert(@Nonnull Object inCommand)
  {
    Command command;

    if(inCommand instanceof Command)
      command = (Command)inCommand;
    else
      command = new BaseCommand(inCommand.toString());

    Document sub = s_simple.createSubDocument();
    sub.add(command);

    return sub.toString();
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
    //----- simple ---------------------------------------------------------

    /** Simple tests. */
    @org.junit.Test
    public void simple()
    {
      HTMLDocument doc = new HTMLDocument("title");

      doc.add(new Command(new Command("just "), new Bold("some "),
                          new Command("test")));

      assertEquals("simple", "just <strong>some </strong>test",
                   doc.toString());

      assertEquals("convert", "just <strong>some</strong> test",
                   HTMLDocument.simpleConvert("just \\bold{some} test"));
    }

    //......................................................................
    //----- wrapping -------------------------------------------------------

    /** Testing of word wrapping. */
    @org.junit.Test
    public void wrapping()
    {
      HTMLDocument doc = new HTMLDocument("title");

      doc.add
        (new Command
         (new Command("we do something similar to the stuff we did before, "),
          new net.ixitxachitls.output.commands.Bold("but"),
          new Command(" this time the whole thing is to be wrapped over "
                      + "several lines, and we want to check that this "
                      + "works.")));

      assertEquals("command",
                   "we do something similar to the stuff we "
                   + "did before, <strong>but</strong> this time the whole "
                   + "thing is to be wrapped over several "
                   + "lines, and we want to check that this "
                   + "works.", doc.toString());
    }

    //......................................................................
    //----- justification --------------------------------------------------

    /** Testing justification. */
    @org.junit.Test
    public void justification()
    {
      HTMLDocument doc = new HTMLDocument("title");

      doc.add(new net.ixitxachitls.output.commands.Center("center"));
      doc.add(new net.ixitxachitls.output.commands.Left("left"));
      doc.add(new net.ixitxachitls.output.commands.Right("right"));
      doc.add(new net.ixitxachitls.output.commands
              .Block("and now, finally the whole text set blocked, i.e. "
                     + "aligned to the left and to the right at the same "
                     + "time"));

      assertEquals("command",
                   "\n"
                   + "<div class=\"align-center\">\n"
                   + "center\n"
                   + "</div>\n"
                   + "\n"
                   + "<div class=\"align-left\">\n"
                   + "left\n"
                   + "</div>\n"
                   + "\n"
                   + "<div class=\"align-right\">\n"
                   + "right\n"
                   + "</div>\n"
                   + "\n"
                   + "<div class=\"align-justify\">\n"
                   + "and now, finally the whole text set "
                   + "blocked, i.e. aligned to the left and to "
                   + "the right at the same time\n"
                   + "</div>\n",
                   doc.toString());
    }

    //......................................................................
    //----- footnote -------------------------------------------------------

    /** Testing footnotes. */
    @org.junit.Test
    public void footnote()
    {
      HTMLDocument doc = new HTMLDocument("title");

      doc.add(new BaseCommand
              ("just some text for the document\\footnote{test only}, "
               + "including some commands for setting footnotes.\\par "
               + "Of course\\footnote{as always} it is not easy to set "
               + "all possible footnotes\\footnote[a]{test with marker "
               + "and a somewhat larger text to see wrapping of it}"));

      assertEquals("footnote",
                   "just some text for the document<sup>1</sup>, "
                   + "including some commands for setting "
                   + "footnotes.\n"
                   + "<p />\n"
                   + "Of course<sup>2</sup> it is not easy to set all "
                   + "possible footnotes<sup>a</sup>\n"
                   + "<p />\n"
                   + "\n"
                   + "<hr width=\"30%\">\n"
                   + "\n"
                   + "<table class=\"footnote\">"
                   + "<tr class=\"odd \">"
                   + "<td>1)</td>"
                   + "<td>test only</td>"
                   + "</tr>"
                   + "<tr class=\"even \">"
                   + "<td>2)</td>"
                   + "<td>as always</td>"
                   + "</tr>"
                   + "<tr class=\"odd \">"
                   + "<td>a)</td>"
                   + "<td>test with "
                   + "marker and a somewhat larger text to see wrapping "
                   + "of it</td></tr>"
                   + "</table>",
                   doc.toString());
    }

    //......................................................................
    //----- frac -----------------------------------------------------------

    /** Test printing of fractures. */
    @org.junit.Test
    public void frac()
    {
      HTMLDocument doc = new HTMLDocument("title");

      doc.add(new BaseCommand("1/10 = \\frac{1}{10}"));

      assertEquals("frac", "1/10 = 1/10", doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
