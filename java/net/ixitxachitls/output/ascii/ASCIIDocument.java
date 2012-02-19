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

package net.ixitxachitls.output.ascii;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import net.ixitxachitls.output.Buffer;
import net.ixitxachitls.output.Document;
import net.ixitxachitls.output.WrapBuffer;
import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.output.actions.Delimiter;
import net.ixitxachitls.output.actions.Identity;
import net.ixitxachitls.output.actions.Multi;
import net.ixitxachitls.output.actions.Pattern;
import net.ixitxachitls.output.actions.Replace;
import net.ixitxachitls.output.actions.ascii.Align;
import net.ixitxachitls.output.actions.ascii.Hrule;
import net.ixitxachitls.output.actions.ascii.Table;
import net.ixitxachitls.output.actions.ascii.UpperCase;
import net.ixitxachitls.output.commands.Acute;
import net.ixitxachitls.output.commands.BaseCommand;
import net.ixitxachitls.output.commands.Block;
import net.ixitxachitls.output.commands.Bold;
import net.ixitxachitls.output.commands.Center;
import net.ixitxachitls.output.commands.Color;
import net.ixitxachitls.output.commands.Columns;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Count;
import net.ixitxachitls.output.commands.Divider;
import net.ixitxachitls.output.commands.Editable;
import net.ixitxachitls.output.commands.Emph;
import net.ixitxachitls.output.commands.Footer;
import net.ixitxachitls.output.commands.Footnotesize;
import net.ixitxachitls.output.commands.Frac;
import net.ixitxachitls.output.commands.Grave;
import net.ixitxachitls.output.commands.Greater;
import net.ixitxachitls.output.commands.Greaterequal;
import net.ixitxachitls.output.commands.Grouped;
import net.ixitxachitls.output.commands.Hat;
import net.ixitxachitls.output.commands.Highlight;
import net.ixitxachitls.output.commands.Huge;
import net.ixitxachitls.output.commands.Huger;
import net.ixitxachitls.output.commands.ID;
import net.ixitxachitls.output.commands.Icon;
import net.ixitxachitls.output.commands.Image;
import net.ixitxachitls.output.commands.ImageLink;
import net.ixitxachitls.output.commands.Large;
import net.ixitxachitls.output.commands.Larger;
import net.ixitxachitls.output.commands.Largest;
import net.ixitxachitls.output.commands.Left;
import net.ixitxachitls.output.commands.Less;
import net.ixitxachitls.output.commands.Lessequal;
import net.ixitxachitls.output.commands.Linebreak;
import net.ixitxachitls.output.commands.Link;
import net.ixitxachitls.output.commands.Navigation;
import net.ixitxachitls.output.commands.Nopictures;
import net.ixitxachitls.output.commands.NormalSize;
import net.ixitxachitls.output.commands.Par;
import net.ixitxachitls.output.commands.Picture;
import net.ixitxachitls.output.commands.Right;
import net.ixitxachitls.output.commands.Scriptsize;
import net.ixitxachitls.output.commands.Small;
import net.ixitxachitls.output.commands.Span;
import net.ixitxachitls.output.commands.Sub;
import net.ixitxachitls.output.commands.Subtitle;
import net.ixitxachitls.output.commands.Super;
import net.ixitxachitls.output.commands.Textblock;
import net.ixitxachitls.output.commands.Tiny;
import net.ixitxachitls.output.commands.Title;
import net.ixitxachitls.output.commands.Umlaut;
import net.ixitxachitls.output.commands.Value;
import net.ixitxachitls.output.commands.Window;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base document for all output documents used.
 *
 * @file          ASCIIDocument.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class ASCIIDocument extends Document
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- ASCIIDocument ----------------------------

  /**
   * Basic constructor.
   *
   */
  public ASCIIDocument()
  {
    this(Config.get("resource:ascii/width", 80));
  }

  //........................................................................
  //----------------------------- ASCIIDocument ----------------------------

  /**
   * Basic constructor.
   *
   * @param       inWidth the total width of a single line
   *
   */
  public ASCIIDocument(int inWidth)
  {
    if(inWidth <= 0)
      throw new IllegalArgumentException("line width must be above zero");

    m_width  = inWidth;
    m_buffer = new WrapBuffer(m_width);
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The line width. */
  protected int m_width;

  /** A stored document for simple conversions. */
  protected static final ASCIIDocument s_simple = new ASCIIDocument(5000);

  /** The known actions. */
  protected static final Map<String, Action> s_actions =
    new HashMap<String, Action>();

  static
  {
    // add the commands
    s_actions.put(Bold.NAME, new UpperCase());
    s_actions.put(Emph.NAME, new UpperCase());
    s_actions.put(Left.NAME, new Align(Buffer.Alignment.left));
    s_actions.put(Right.NAME, new Align(Buffer.Alignment.right));
    s_actions.put(Center.NAME, new Align(Buffer.Alignment.center));
    s_actions.put(Block.NAME, new Align(Buffer.Alignment.block));
    s_actions.put(Tiny.NAME, new Identity(1));
    s_actions.put(Footnotesize.NAME, new Identity(1));
    s_actions.put(Scriptsize.NAME, new Identity(1));
    s_actions.put(Small.NAME, new Identity(1));
    s_actions.put(NormalSize.NAME, new Identity(1));
    s_actions.put(Large.NAME, new Identity(1));
    s_actions.put(Larger.NAME, new Identity(1));
    s_actions.put(Largest.NAME, new Identity(1));
    s_actions.put(Huge.NAME, new Identity(1));
    s_actions.put(Huger.NAME, new Identity(1));
    s_actions.put(Icon.NAME, null);
    s_actions.put(Image.NAME, null);
    s_actions.put(Picture.NAME, null);
    s_actions.put(ImageLink.NAME, null);
    s_actions.put(net.ixitxachitls.output.commands.Table.NAME, new Table());
    s_actions.put(Link.NAME, new Identity(1));
    s_actions.put(Editable.NAME, new Identity(3));
    s_actions.put(Title.NAME, new Multi(new Action []
      {
        new Align(Buffer.Alignment.center),
        new UpperCase(),
      }));
    s_actions.put(Subtitle.NAME, new Align(Buffer.Alignment.center));
    s_actions.put(Textblock.NAME, new Align(Buffer.Alignment.block));
    s_actions.put(Hat.NAME,
                  new Replace(new Replace.Replacement("u", "Ã»"),
                              new Replace.Replacement("U", "Ã"),
                              new Replace.Replacement("o", "Ã´"),
                              new Replace.Replacement("O", "Ã"),
                              new Replace.Replacement("a", "Ã¢"),
                              new Replace.Replacement("A", "Ã"),
                              new Replace.Replacement("i", "Ã®"),
                              new Replace.Replacement("I", "Ã")));
    s_actions.put(Umlaut.NAME,
                  new Replace(new Replace.Replacement("a", "Ã¤"),
                              new Replace.Replacement("A", "Ã"),
                              new Replace.Replacement("o", "Ã¶"),
                              new Replace.Replacement("O", "Ã"),
                              new Replace.Replacement("u", "Ã¼"),
                              new Replace.Replacement("U", "Ã"),
                              new Replace.Replacement("i", "Ã¯"),
                              new Replace.Replacement("I", "Ã")));
    s_actions.put(Acute.NAME,
                  new Replace(new Replace.Replacement("e", "Ã©"),
                              new Replace.Replacement("E", "Ã"),
                              new Replace.Replacement("u", "\u00fa"),
                              new Replace.Replacement("U", "\u00da"),
                              new Replace.Replacement("a", "\u00e0"),
                              new Replace.Replacement("A", "\u00c0"),
                              new Replace.Replacement("o", "\u00f2"),
                              new Replace.Replacement("O", "\u00d2"),
                              new Replace.Replacement("i", "\u00ed"),
                              new Replace.Replacement("I", "\u00cd")));
    s_actions.put(Grave.NAME,
                  new Replace(new Replace.Replacement("e", "Ã¨"),
                              new Replace.Replacement("E", "Ã"),
                              new Replace.Replacement("u", "Ã¹"),
                              new Replace.Replacement("U", "Ã"),
                              new Replace.Replacement("a", "Ã "),
                              new Replace.Replacement("A", "Ã"),
                              new Replace.Replacement("o", "Ã²"),
                              new Replace.Replacement("O", "Ã"),
                              new Replace.Replacement("i", "Ã¬"),
                              new Replace.Replacement("I", "Ã")));
    s_actions.put(Par.NAME,
                  new Delimiter("\n\n", null, null, null, null, null));
    s_actions.put(net.ixitxachitls.output.commands.List.NAME,
                  new net.ixitxachitls.output.actions.ascii.List(" * "));
    s_actions.put(Nopictures.NAME, new Identity(1));
    s_actions.put(Highlight.NAME, null);
    s_actions.put(ID.NAME, new Identity(2));
    s_actions.put(Window.NAME, new Identity(1));
    s_actions.put(Frac.NAME, new Pattern("[[$0 ]]$1/$2"));
    s_actions.put(net.ixitxachitls.output.commands.Footnote.NAME,
                  new net.ixitxachitls.output.actions.Footnote());
    s_actions.put(Super.NAME, new Pattern("($1)"));
    s_actions.put(Sub.NAME, new Pattern("($1)"));
    s_actions.put(net.ixitxachitls.output.commands.Hrule.NAME, new Hrule());
    s_actions.put(Navigation.NAME, null);
    s_actions.put(Divider.NAME, new Identity(2));
    s_actions.put(Span.NAME, new Identity(2));
    s_actions.put(Color.NAME, null);
    s_actions.put(Columns.NAME, null);
    s_actions.put(Footer.NAME, null);
    s_actions.put(Linebreak.NAME, new Delimiter("\n", null, null, null,
                                                null, null));
    s_actions.put(Count.NAME, new Pattern("$1 (max $2) $3"));
    s_actions.put(Less.NAME, new Pattern("<"));
    s_actions.put(Greater.NAME, new Pattern(">"));
    s_actions.put(Lessequal.NAME, new Pattern("<="));
    s_actions.put(Greaterequal.NAME, new Pattern(">="));
    s_actions.put(Grouped.NAME, new Identity(1));
    s_actions.put(Value.NAME,
                  new Pattern("\\table{f15:l;1:l}{$2}{$3}", true));

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
  //------------------------------- getWidth -------------------------------

  /**
   * Get the width of the document.
   *
   * @return      the width in characters
   *
   */
  @Override
public int getWidth()
  {
    return m_width;
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

  //----------------------------- setAlignment -----------------------------

  /**
   * Set the alignment of the document buffer, if this is supported.
   *
   * @param       inAlignment the new alignment
   *
   */
  @Override
public void setAlignment(@Nonnull Buffer.Alignment inAlignment)
  {
    ((WrapBuffer)m_buffer).setAlignment(inAlignment);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- simple ---------------------------------------------------------

    /** Some simple tests. */
    @org.junit.Test
    public void simple()
    {
      Document doc = new ASCIIDocument();

      doc.add(new Command(new Command("just "),
                          new net.ixitxachitls.output.commands.Bold("some "),
                          new Command("test")));

      assertEquals("simple", "just SOME test", doc.toString());

      assertEquals("convert", "some BOLD text",
                   ASCIIDocument.simpleConvert("some \\bold{bold} text"));
    }

    //......................................................................
    //----- wrapping -------------------------------------------------------

    /** Test word wrapping. */
    @org.junit.Test
    public void wrapping()
    {
      Document doc = new ASCIIDocument(40);

      doc.add
        (new Command
         (new Command("we do something similar to the stuff we did before, "),
          new net.ixitxachitls.output.commands.Bold("but"),
          new Command(" this time the whole thing is to be wrapped over "
                      + "several lines, and we want to check that this "
                      + "works.")));

      assertEquals("command",
                   "we do something similar to the stuff we \n"
                   + "did before, BUT this time the whole     \n"
                   + "thing is to be wrapped over several     \n"
                   + "lines, and we want to check that this   \n"
                   + "works.", doc.toString());

      SubDocument sub = doc.createSubDocument();

      assertEquals("length", "just some text".length(),
                   sub.getLength("just some text"));
    }

    //......................................................................
    //----- justification --------------------------------------------------

    /** Test general justification. */
    @org.junit.Test
    public void justification()
    {
      Document doc = new ASCIIDocument(40);

      doc.add(new net.ixitxachitls.output.commands.Center("center"));
      doc.add(new net.ixitxachitls.output.commands.Left("left"));
      doc.add(new net.ixitxachitls.output.commands.Right("right"));
      doc.add(new net.ixitxachitls.output.commands
              .Block("and now, finally the whole text set blocked, i.e. "
                     + "aligned to the left and to the right at the same "
                     + "time"));
      // centering only happens on paragraphs, though
      doc.setAlignment(Buffer.Alignment.center);
      doc.add("center");

      assertEquals("command",
                   "                 center                 \n"
                   + "left                                    \n"
                   + "                                   right\n"
                    + "and now,  finally  the  whole  text  set\n"
                   + "blocked, i.e. aligned to the left and to\n"
                   + "the right at the same time              \n"
                   + "center",
                   doc.toString());
    }

    //......................................................................
    //----- footnote -------------------------------------------------------

    /** Test footnotes. */
    @org.junit.Test
    public void footnote()
    {
      Document doc = new ASCIIDocument(40);

      doc.add(new BaseCommand
              ("just some text for the document"
               + "\\footnote{test only}, including some commands "
               + "for setting footnotes.\\par Of course"
               + "\\footnote{as always} it is not easy to set all "
               + "possible footnotes\\footnote[a]{test with marker "
               + "and a somewhat larger text to see wrapping of "
               + "it}"));

      assertEquals("footnote",
                   "just some text for the document(1),     \n"
                   + "including some commands for setting     \n"
                   + "footnotes.                              \n"
                   + "\n"
                   + "Of course(2) it is not easy to set all  \n"
                   + "possible footnotes(a)\n"
                   + "\n"
                   + "\n"
                   + "------------                            \n"
                   + "1)  test only                           \n"
                   + "2)  as always                           \n"
                   + "a)  test with marker and a somewhat     \n"
                   + "    larger text to see wrapping of it   \n",
                   doc.toString());
    }

    //......................................................................
  }

  //........................................................................
}
