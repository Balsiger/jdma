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

package net.ixitxachitls.util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * Static encoding utility functions.
 *
 * @file          Encodings.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public final class Encodings
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ Encodings -------------------------------

  /**
   * Private constructor to prevent instantiations.
   *
   */
  private Encodings()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The special escape characters that cannot be saved in a preferences
   *  file. */
  private static final @Nonnull String s_escapes = "\t\f\r\n\u001B";

  /** Marker used for replacements (should not otherwise appear in the text. */
  private static final @Nonnull String s_marker  = "@#@";

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- encodeEscapes ----------------------------

  /**
    * Encode the given string in such a way, that no more escape characters
    * remain and the text can safely be stored in a preferences file.
    *
    * @param       inString the String to encode
    *
    * @return      the encoded String
    *
    */
  public static @Nonnull String encodeEscapes(@Nullable String inString)
  {
    if(inString == null)
      return "";

    // replace all special characters with some equivalent
    StringBuilder result = new StringBuilder();
    for(int i = 0; i < inString.length(); i++)
    {
      char c   = inString.charAt(i);
      int  pos = s_escapes.indexOf(c);

      if(pos >= 0)
        result.append(s_marker + pos + s_marker);
      else
        result.append(c);
    }

    return result.toString();
  }

  //........................................................................
  //----------------------------- decodeEscapes ----------------------------

  /**
    * Decode the given encoded string in such a way, that it is the same
    * as before encoding.
    *
    * @param       inString the String to decode
    *
    * @return      the decoded String
    *
    */
  public static @Nonnull String decodeEscapes(@Nonnull String inString)
  {
    String []parts = inString.split(s_marker);

    StringBuilder result = new StringBuilder(parts[0]);

    for(int i = 1; i < parts.length; i += 2)
    {
      int escape = Integer.parseInt(parts[i]);

      result.append(s_escapes.charAt(escape));

      if(i + 1 < parts.length)
        result.append(parts[i + 1]);
    }

    return result.toString();
  }

  //........................................................................

  //------------------------------ markSpaces ------------------------------

  /**
   * Mark all whitespace in the text to make it obvious.
   *
   * @param       inText the text to mark
   *
   * @return      the marked text
   *
   */
  public static @Nonnull String markSpaces(@Nonnull String inText)
  {
    return inText.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r")
      .replaceAll("\t", "\\\\t").replaceAll("\f", "\\\\f").replaceAll(" ", "_");
  }

  //........................................................................
  //------------------------------- tokenize -------------------------------

  /**
   * Extract all tokens from the given string. Ordinary string parts are
   * simply returned, variable denotions of the form ($|#|%){x} are
   * returned as ($|#|%)x.
   *
   * @param       inTemplate the string with the template data
   * @param       inPattern  the pattern for the tokens to recognize
   *
   * @return      a list with all parsed tokens
   *
   */
  public static @Nonnull List<String> tokenize(@Nonnull String inTemplate,
                                               @Nonnull String inPattern)
  {
    List<String> result = new ArrayList<String>();

    if(inTemplate.length() == 0)
      return result;

    Pattern pattern = Pattern.compile(inPattern);
    Matcher matcher = pattern.matcher(inTemplate);

    int pos;
    for(pos = 0; matcher.find(); pos = matcher.end())
    {
      // any normal text?
      if(matcher.start() > pos)
        result.add(inTemplate.substring(pos, matcher.start()));

      // the real token
      String command = matcher.group(2);
      if(command == null)
        command = matcher.group(3);

      result.add(matcher.group(1) + command);
    }

    if(pos < inTemplate.length())
      result.add(inTemplate.substring(pos));

    return result;
  }

  //........................................................................

  //------------------------------ urlEncode -------------------------------

  /**
   * Encode the string for an url.
   *
   * @param       inText the text to encode
   *
   * @return      the url encoded text
   *
   */
  public static @Nonnull String urlEncode(@Nonnull String inText)
  {
    try
    {
      return URLEncoder.encode(inText, "UTF-8");
    }
    catch(java.io.UnsupportedEncodingException e)
    {
      Log.error("invalid encoding for url encoding: " + e);

      return "error";
    }
  }

  //........................................................................
  //------------------------- encodeHTMLAttribute --------------------------

  /**
    *
    * Encode the given string in a way that it can safely be used as an
    * attribute value in html.
    *
    * @param       inText the text to encode
    *
    * @return      the encoded text
    *
    * @example     String encoded = Encodings.encodeHTMLAttribute("\"");
    *
    */
  public static @Nonnull String encodeHTMLAttribute(@Nonnull String inText)
  {
    return inText.replaceAll("\"", "&#34;").replaceAll("\'", "&#39;");
  }

  //........................................................................
  //------------------------------ toJSString ------------------------------

  /**
   * Convert the given String into a javascript string.
   *
   * @param       inText the text to convert
   *
   * @return      the converted text
   *
   */
  public static @Nonnull String toJSString(@Nonnull String inText)
  {
    //String text = HTMLDocument.simpleConvert(inText);
    // TODO: check if we need HTML conversion here and if this could be moved
    // to util or something
    return "'" + escapeJS(inText) + "'";
  }

  //........................................................................
  //------------------------------ toCSSString ------------------------------

  /**
   * Convert the given String into a CSS string.
   *
   * @param       inText the text to convert
   *
   * @return      the converted text
   *
   */
  public static @Nonnull String toCSSString(@Nonnull String inText)
  {
    return inText.replaceAll("\\W", "");
  }

  //........................................................................
  //--------------------------- toCamelCaseCase -----------------------------

  /**
    * Remove all '_' and ' ' and replace subsequene characters with upper case
    * letters.
    *
    * @param       inText the text to convert
    *
    * @return      the converted text
    *
    */
  public static @Nonnull String toCamelCase(@Nonnull String inText)
  {
    if(inText.length() == 0)
      return inText;

    String []split = inText.split("[_ ]");

    if(split.length <= 1)
      return inText;

    StringBuilder result = new StringBuilder(split[0]);

    for(int i = 1; i < split.length; i++)
      if(split[i].length() == 1)
        result.append(Character.toUpperCase(split[i].charAt(0)));
      else if(split[i].length() > 1)
        result.append(Character.toUpperCase(split[i].charAt(0))
                      + split[i].substring(1));

    return result.toString();
  }

  //........................................................................
  //--------------------------- toWordUpperCase ----------------------------

  /**
    * Convert the String by making sure that each word beginning starts
    * with an uppercase letter.
    *
    * @param       inText the text to convert
    *
    * @return      the converted text
    *
    */
  public static @Nonnull String toWordUpperCase(@Nonnull String inText)
  {
    if(inText.length() == 0)
      return inText;

    String []split = inText.split("(?<![&'])\\b(?=\\p{Lower})");

    if(split.length == 1)
      if(Character.isLowerCase(inText.charAt(0)))
        return Character.toUpperCase(inText.charAt(0)) + inText.substring(1);
      else
        return inText;

    StringBuilder result = new StringBuilder(split[0]);

    for(int i = 1; i < split.length; i++)
      result.append(Character.toUpperCase(split[i].charAt(0))
                    + split[i].substring(1));

    return result.toString();
  }

  //........................................................................
  //------------------------------- escapeJS -------------------------------

  /**
   * Escape characters in the given text to make it save to include in a
   * javascript string.
   *
   * @param    inText the text to escape
   *
   * @return   the escaped text
   *
   */
  public static @Nonnull String escapeJS(@Nonnull String inText)
  {
    return inText.replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"")
      .replaceAll("\n", "\\\\n");
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
    //----- encode decode --------------------------------------------------

    /** Testing encoding. */
    @org.junit.Test
    public void encodeDecode()
    {
      String test = "just some \t\ftes\rt\u001B for decoding\t and...\n";

      assertEquals("encode/decode", test,
                   Encodings.decodeEscapes(Encodings.encodeEscapes(test)));
      assertEquals("encode/decode", "",
                   Encodings.decodeEscapes(Encodings.encodeEscapes(null)));
    }

    //......................................................................
    //----- tokenize -------------------------------------------------------

    /** Test tokenizing of templates. */
    @org.junit.Test
    public void tokenize()
    {
      List<String> tokens = Encodings.tokenize("", "(\\$|#|%)\\{(.*?)\\}");

      assertEquals("empty", 0, tokens.size());

      tokens = Encodings.tokenize("just a simple string",
                                "(\\$|#|%)(?:\\{(.*?)\\}|(\\w+))");

      assertEquals("string only", "just a simple string", tokens.get(0));
      assertEquals("string only", 1, tokens.size());

      tokens = Encodings.tokenize
        ("a $first template with some %{real values} to parse",
         "(\\$|#|%)(?:\\{(.*?)\\}|(\\w+))");

      assertEquals("real", "a ", tokens.get(0));
      assertEquals("real", "$first", tokens.get(1));
      assertEquals("real", " template with some ", tokens.get(2));
      assertEquals("real", "%real values", tokens.get(3));
      assertEquals("real", " to parse", tokens.get(4));
      assertEquals("real", 5, tokens.size());

      tokens = Encodings.tokenize("${test}", "(\\$|#|%)\\{(.*?)\\}");

      assertEquals("value only", "$test", tokens.get(0));
      assertEquals("value only", 1, tokens.size());
    }

    //......................................................................
    //----- word upper case ------------------------------------------------

    /** Testing casing. */
    @org.junit.Test
    public void casing()
    {
      assertEquals("simple", "Just A Test",
                   Encodings.toWordUpperCase("just a test"));
      assertEquals("spaces", "   Just  A   TeST  ",
                   Encodings.toWordUpperCase("   just  A   teST  "));
      assertEquals("start", "Start With Lower. Case",
                   Encodings.toWordUpperCase("start with lower. case"));
      assertEquals("word", "Test", Encodings.toWordUpperCase("test"));
      assertEquals("word", "Test", Encodings.toWordUpperCase("Test"));
      // Does not work on the server
      //assertEquals("word", "Faerûn", Encodings.toWordUpperCase("faerûn"));
      assertEquals("brackets", "Test (Test)",
                   Encodings.toWordUpperCase("test (test)"));
      assertEquals("empty", "", Encodings.toWordUpperCase(""));
    }

    //......................................................................
    //----- encode html ----------------------------------------------------

    /** Test encoding as html attribute. */
    @org.junit.Test
    public void encodeHTML()
    {
      assertEquals("apos", "I can&#39;t do &#34;this&#34;",
                   Encodings.encodeHTMLAttribute("I can't do \"this\""));
      assertEquals("no replace", "just a test",
                   Encodings.encodeHTMLAttribute("just a test"));
      assertEquals("empty", "", Encodings.encodeHTMLAttribute(""));
    }

    //......................................................................
    //----- js string ------------------------------------------------------

    /** Testing js string conversion. */
    @org.junit.Test
    public void jsString()
    {
      assertEquals("empty", "''", Encodings.toJSString(""));
      assertEquals("simple", "'test'", Encodings.toJSString("test"));
      assertEquals("escape", "'escape \\\'\\\"strings\\\"\\''",
                   Encodings.toJSString("escape '\"strings\"'"));
    }

    //......................................................................
    //----- mark spaces ----------------------------------------------------

    /** Testing of marking spaces. */
    @org.junit.Test
    public void markingSpaces()
    {
      assertEquals("empty", "", markSpaces(""));
      assertEquals("simple", "just_a_test", markSpaces("just a test"));
      assertEquals("all", "just\\ta\\ntest\\f\\r_with___all_\\n\\nthings",
                   markSpaces("just\ta\ntest\f\r with   all \n\nthings"));
    }

    //......................................................................
    //----- css ------------------------------------------------------------

    /** css Test. */
    @org.junit.Test
    public void css()
    {
      assertEquals("empty", "", Encodings.toCSSString(""));
      assertEquals("simple", "simple", Encodings.toCSSString("simple"));
      assertEquals("spaces", "justatest", Encodings.toCSSString("just a test"));
      assertEquals("special", "a", Encodings.toCSSString("&*%$#a%#!@()"));
    }

    //......................................................................
    //----- coverage -------------------------------------------------------

    /** Coverage test. */
    @org.junit.Test
    public void coverage()
    {
      new Encodings();
    }

    //......................................................................
  }

  //........................................................................
}
