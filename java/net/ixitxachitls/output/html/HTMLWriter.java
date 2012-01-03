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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.Joiner;

import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A simple class to write html to a print writer.
 *
 * @file          HTMLWriter.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class HTMLWriter
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ HTMLWriter ------------------------------

  /**
   * Create the html writer.
   *
   * @param     inWriter the writer to output to
   *
   */
  public HTMLWriter(@Nonnull PrintWriter inWriter)
  {
    m_writer = inWriter;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The writer to output to. */
  protected @Nonnull PrintWriter m_writer;

  /** The writer containing the body. */
  protected @Nonnull StringWriter m_body = new StringWriter();

  /** The writer for the body. */
  protected @Nonnull PrintWriter m_bodyWriter = new PrintWriter(m_body);

  /** The current stack of tags. */
  protected Deque<String> m_tags = new ArrayDeque<String>();

  /** Whether currently in a tag. */
  protected boolean m_unclosed = false;

  /** Whether in html context. */
  protected boolean m_inHTML = false;

  /** Whether in head context. */
  protected boolean m_inHead = false;

  /** The joiner to space concatenate strings. */
  protected static final Joiner s_spaceJoiner = Joiner.on(' ');

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- begin ---------------------------------

  /**
   * Start a tag.
   *
   * @param       inTag the name of the tag to open
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter begin(@Nonnull String inTag)
  {
    indent();
    m_bodyWriter.print("<");
    m_bodyWriter.print(inTag.toUpperCase(Locale.US));
    m_tags.push(inTag);
    m_unclosed = true;

    return this;
  }

  //........................................................................
  //------------------------------ attribute -------------------------------

  /**
   * Add an attribute to a tag.
   *
   * @param       inName  the name of the attribute
   * @param       inValue the value for the attribute
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter attribute(@Nonnull String inName, @Nullable String inValue)
  {
    if(!m_unclosed)
    {
      Log.warning("not currently in a tag, ignoring attributes");
      return this;
    }

    m_bodyWriter.print(" ");
    m_bodyWriter.print(inName);
    if(inValue != null)
    {
      m_bodyWriter.print("=\"");
      m_bodyWriter.print(inValue);
      m_bodyWriter.print("\"");
    }

    return this;
  }

  //........................................................................
  //--------------------------------- id -----------------------------------

  /**
   * Add an id attribute to a tag.
   *
   * @param       inName  the name of the attribute
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter id(@Nonnull String inName)
  {
    return attribute("id", inName);
  }

  //........................................................................
  //------------------------------- tooltip --------------------------------

  /**
   * Add a title (tooltip) attribute to a tag.
   *
   * @param       inText   the text for the tooltip
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter tooltip(@Nonnull String inText)
  {
    return attribute("title", inText);
  }

  //........................................................................
  //--------------------------------- href ---------------------------------

  /**
   * Add a href attribute to a tag.
   *
   * @param       inText   the text for the href
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter href(@Nonnull String inText)
  {
    return attribute("href", inText);
  }

  //........................................................................
  //--------------------------------- src ----------------------------------

  /**
   * Add a src attribute to a tag.
   *
   * @param       inText   the text for the src
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter src(@Nonnull String inText)
  {
    return attribute("src", inText);
  }

  //........................................................................
  //--------------------------------- alt ----------------------------------

  /**
   * Add a alt attribute to a tag.
   *
   * @param       inText   the text for the alt
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter alt(@Nonnull String inText)
  {
    return attribute("alt", inText);
  }

  //........................................................................
  //--------------------------------- name ---------------------------------

  /**
   * Add a name attribute to a tag.
   *
   * @param       inText   the text for the name
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter name(@Nonnull String inText)
  {
    return attribute("name", inText);
  }

  //........................................................................
  //-------------------------------- value ---------------------------------

  /**
   * Add a value attribute to a tag.
   *
   * @param       inText   the text for the value
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter value(@Nonnull String inText)
  {
    return attribute("value", inText);
  }

  //........................................................................
  //-------------------------------- style ---------------------------------

  /**
   * Add a style attribute to a tag.
   *
   * @param       inText   the text for the style
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter style(@Nonnull String inText)
  {
    return attribute("style", inText);
  }

  //........................................................................
  //-------------------------------- onClick -------------------------------

  /**
   * Add an on click attribute to the current tag.
   *
   * @param       inText   the text for the href
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter onClick(@Nonnull String inText)
  {
    return attribute("onclick", inText);
  }

  //........................................................................
  //-------------------------------- onSubmit ------------------------------

  /**
   * Add an on submit attribute to the current tag.
   *
   * @param       inText   the text for the event handler
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter onSubmit(@Nonnull String inText)
  {
    return attribute("onsubmit", inText);
  }

  //........................................................................
  //------------------------------ onMouseOver -----------------------------

  /**
   * Add an on mouseover attribute to the current tag.
   *
   * @param       inText   the text for the mouse over
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter onMouseOver(@Nonnull String inText)
  {
    return attribute("onmouseover", inText);
  }

  //........................................................................
  //------------------------------ onMouseOver -----------------------------

  /**
   * Add an on mouseout attribute to the current tag.
   *
   * @param       inText   the text for the mouse out
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter onMouseOut(@Nonnull String inText)
  {
    return attribute("onmouseout", inText);
  }

  //........................................................................
  //------------------------------- onChange -------------------------------

  /**
   * Add an on change attribute to the current tag.
   *
   * @param       inText   the text for the on change
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter onChange(@Nonnull String inText)
  {
    return attribute("onchange", inText);
  }

  //........................................................................
  //------------------------------- classes -------------------------------

  /**
   * Add one or several class names to a tag.
   *
   * @param       inNames  the class names for the tag
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter classes(@Nonnull String ... inNames)
  {
    return attribute("class", s_spaceJoiner.join(inNames));
  }

  //........................................................................
  //--------------------------------- end ----------------------------------

  /**
   * End a tag.
   *
   * @param       inTag the name of the tag to end
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter end(@Nonnull String inTag)
  {
    if(m_tags.size() > 0 && !m_tags.peek().equalsIgnoreCase(inTag))
      Log.warning("closing tag " + inTag + ", but expected " + m_tags.peek());

    if(!m_tags.removeFirstOccurrence(inTag))
      Log.warning("closing tag " + inTag + ", but was never opened");

    if(m_unclosed && !inTag.equalsIgnoreCase("div")
       && !inTag.equalsIgnoreCase("a"))
    {
      m_bodyWriter.println("/>");
      m_unclosed = false;
    }
    else
    {
      if(!inTag.equalsIgnoreCase("a"))
        indent();
      else
        maybeCloseTag();

      m_bodyWriter.print("</");
      m_bodyWriter.print(inTag.toUpperCase(Locale.US));
      m_bodyWriter.println(">");
    }

    return this;
  }

  //........................................................................
  //------------------------------- comment --------------------------------

  /**
   * Write a comment.
   *
   * @param       inComment the comment to write
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter comment(@Nonnull String inComment)
  {
    indent();
    m_bodyWriter.print("<!-- ");
    m_bodyWriter.print(inComment);
    m_bodyWriter.println(" -->");

    return this;
  }

  //........................................................................
  //-------------------------------- title ---------------------------------

  /**
   * Set the title of the document.
   *
   * @param       inTitle the text for the title
   *
   * @return      this writer for chaining
   *
   */
  public HTMLWriter title(String inTitle)
  {
    ensureHead();
    m_writer.println("    <TITLE>" + inTitle + "</TITLE>");

    return this;
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Write a string to the writer.
   *
   * @param       inText the text to write
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter add(@Nonnull String inText)
  {
    indent();
    if("a".equalsIgnoreCase(m_tags.peek()))
      m_bodyWriter.print(inText);
    else
      m_bodyWriter.println(inText);

    return this;
  }

  //........................................................................
  //------------------------------ addCSSFile ------------------------------

  /**
   * Add a CSS file to the head of the file.
   *
   * @param       inName the name of the CSS resource to add
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter addCSSFile(@Nonnull String inName)
  {
    ensureHead();
    m_writer.println("    <LINK rel=\"STYLESHEET\" type=\"text/css\" "
                     + "href=\"/css/" + inName + ".css\" />");

    return this;
  }

  //........................................................................
  //------------------------------ addJSFile -------------------------------

  /**
   * Add a javascript file to the head of the file.
   *
   * @param       inName the name of the CSS resource to add
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter addJSFile(@Nonnull String inName)
  {
    ensureHead();
    m_writer.println("    <SCRIPT type=\"text/javascript\" "
                     + "src=\"/js/" + inName + ".js\"></SCRIPT>");

    return this;
  }

  //........................................................................
  //--------------------------------- meta ---------------------------------

  /**
   * Add a meta tag to the page header.
   *
   * @param       inName       the name of the meta info
   * @param       inContent    the content of the meta
   * @param       inAttributes the attributes for the tag, as key value pairs
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter meta(@Nonnull String inName, @Nonnull String inContent,
                         String ... inAttributes)
  {
    assert inAttributes.length % 2 == 0
      : "must have an even number of attribute value pairs";

    ensureHead();
    m_writer.print("    <META name=\"" + inName + "\" content=\""
                     + inContent + "\"");

    for(int i = 0; i < inAttributes.length; i += 2)
      m_writer.print(" " + inAttributes[i] + "=\"" + inAttributes[i + 1]
                     + "\"");

    m_writer.println("/>");

    return this;
  }

  //........................................................................
  //--------------------------------- meta ---------------------------------

  /**
   * Add a link tag to the page header.
   *
   * @param       inType       the type of the link (value of 'ref')
   * @param       inHRef       the value of the reference
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter link(@Nonnull String inType, @Nonnull String inHRef)
  {
    ensureHead();
    m_writer.println("    <LINK ref=\"" + inType + "\" href=\""
                     + inHRef + "\" />");

    return this;
  }

  //........................................................................
  //-------------------------------- script --------------------------------

  /**
   * Add a script tag with contents to the page.
   *
   * @param       inLines the lines of the script contents
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter script(@Nonnull String ... inLines)
  {
    ensureHTML();
    m_writer.println("    <SCRIPT type=\"text/javascript\">");

    for(String line : inLines)
      m_writer.println("      " + line);

    m_writer.println("    </SCRIPT>");

    return this;
  }

  //........................................................................
  //-------------------------------- script --------------------------------

  /**
   * Add a script tag with contents to the head of the page.
   *
   * @param       inLines the lines of the script contents
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter headScript(@Nonnull String ... inLines)
  {
    ensureHead();
    m_writer.println("    <SCRIPT type=\"text/javascript\">");

    for(String line : inLines)
      m_writer.println("      " + line);

    m_writer.println("    </SCRIPT>");

    return this;
  }

  //........................................................................
  //-------------------------------- script --------------------------------

  /**
   * Add a script tag with contents to the page.
   *
   * @param       inLines the lines of the script contents
   *
   * @return      the writer for chaining
   *
   */
  public HTMLWriter bodyScript(@Nonnull String ... inLines)
  {
    ensureHTML();
    indent();
    m_bodyWriter.println("<SCRIPT type=\"text/javascript\">");

    for(String line : inLines)
      m_bodyWriter.println("      " + line);

    indent();
    m_bodyWriter.println("</SCRIPT>");

    return this;
  }

  //........................................................................
  //-------------------------------- close ---------------------------------

  /**
   * Close the writer.
   *
   */
  public void close()
  {
    ensureHTML();
    maybeCloseTag();
    maybeCloseHead();

    if(m_tags.size() > 0)
      Log.warning("writer closed, but tags " + m_tags + " not closed");

    m_writer.println("  <BODY>");
    m_writer.print(m_body.toString());
    m_writer.println("  </BODY>");
    m_writer.println("</HTML>");
    m_writer.close();
    m_bodyWriter.close();
    m_inHTML = false;
  }

  //........................................................................
  //------------------------------ ensureHTML ------------------------------

  /**
   * Things to be done before writing a new tag.
   *
   */
  protected void ensureHTML()
  {
    if(!m_inHTML)
    {
      m_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      m_writer.println
        ("<!DOCTYPE html "
         + "PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
         + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
      m_writer.println("<HTML xmlns=\"http://www.w3.org/1999/xhtml\">");
      m_inHTML = true;
    }
  }

  //........................................................................
  //------------------------------ ensureHead ------------------------------

  /**
   * Ensure that we are in the head of the document.
   *
   */
  protected void ensureHead()
  {
    if(m_inHead)
      return;

    ensureHTML();

    m_writer.println("  <HEAD>");
    m_inHead = true;
  }

  //........................................................................
  //---------------------------- maybeCloseTag -----------------------------

  /**
   * Close a currently opened tag, if necessary.
   *
   */
  protected void maybeCloseTag()
  {
    if(!m_unclosed)
      return;

    if("a".equalsIgnoreCase(m_tags.peek()))
      m_bodyWriter.print(">");
    else
      m_bodyWriter.println(">");

    m_unclosed = false;
  }

  //........................................................................
  //---------------------------- maybeCloseHead ----------------------------

  /**
   * Close the head context if necessary.
   *
   */
  protected void maybeCloseHead()
  {
    if(!m_inHead)
      return;

    m_writer.println("  </HEAD>");
    m_inHead = false;
  }

  //........................................................................
  //-------------------------------- indent --------------------------------

  /**
   * Print the corret indent for a new line.
   *
   */
  protected void indent()
  {
    maybeCloseTag();
    if(!"a".equalsIgnoreCase(m_tags.peek()))
      m_bodyWriter.print(Strings.spaces(m_tags.size() * 2 + 4));
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

    /** The simple Test. */
    @org.junit.Test
    public void simple()
    {
      java.io.StringWriter contents = new java.io.StringWriter();
      HTMLWriter writer = new HTMLWriter(new PrintWriter(contents));

      writer
        .comment("This is a test.")
        .begin("p")
        .addCSSFile("jdma")
        .attribute("font", "Helvetica")
        .attribute("selected", null)
        .add("This is the body")
        .begin("br")
        .title("title")
        .end("br")
        .end("p")
        .close();

      assertEquals("contents",
                   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                   + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN"
                   + "\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
                   + "\">\n"
                   + "<HTML xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                   + "  <HEAD>\n"
                   + "    <LINK rel=\"STYLESHEET\" type=\"text/css\" "
                   + "href=\"/css/jdma.css\" />\n"
                   + "    <TITLE>title</TITLE>\n"
                   + "  </HEAD>\n"
                   + "  <BODY>\n"
                   + "    <!-- This is a test. -->\n"
                   + "    <P font=\"Helvetica\" selected>\n"
                   + "      This is the body\n"
                   + "      <BR/>\n"
                   + "    </P>\n"
                   + "  </BODY>\n"
                   + "</HTML>\n", contents.toString());
    }

    //......................................................................
  }

  //........................................................................
}
