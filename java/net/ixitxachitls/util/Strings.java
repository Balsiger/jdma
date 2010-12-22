/******************************************************************************
 * Copyright (c) 2002,2003 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 *
 * This class encapsulates some basic String methods.
 *
 * @file          Strings.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ThreadSafe
public final class Strings
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Strings -------------------------------

  /**
    * Private constructor to prevent instantiation of the class.
    *
    */
  private Strings()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** A simple variable to store a bunch of spaces to easily access variable
   *  number of space only substrings. */
  private static final String s_spaces =
    "                                                                      "
    + "                                                                     "
    + "                                                                      "
    + "                                                                      "
    + "                                                                      "
    + "                                                                      "
    + "                                                                      "
    + "                                                                      "
    + "                                                                      "
    + "                                                                      "
    + "                                                                      "
    + "                                                                      "
    + "                                                                      "
    + "                                                                      ";

  /** A bunch of zeroes to generate 0 only substrings. */
  private static final String s_zeroes =
  "000000000000000000000000000000000000000000000000000000000000000000000000"
    + "0000000000000000000000000000000000000000000000000000000000000000000000"
    + "0000000000000000000000000000000000000000000000000000000000000000000000"
    + "0000000000000000000000000000000000000000000000000000000000000000000000";

  /** The date format to use. */
  private static final String DATE =
    Config.get("resource:general/format.date", "EEEE, MMMM d yyyy (HH:mm)");

  /** The words to recognize as numbers. */
  private static final String []s_words =
    Config.get("number.words", new String []
      {
        "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
        "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen",
        "sixteen", "seventeen", "eighteen", "nineteen", "twenty",
      });

  /** The roman numerals to read as numbers. */
  private static final String []s_romans =
    Config.get("number.romans", new String []
      {
        "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI",
        "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX",
      });

  /** The formatter for numbers. */
  private static final NumberFormat s_number =
    NumberFormat.getInstance(new Locale(Config.get("number.language", "de"),
                                        Config.get("number.country", "ch")));

  /** The pattern for teplates. */
  private static final Pattern s_template = Pattern.compile("\\$(\\S+)");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- spaces --------------------------------

  /**
    * Return the given number of spaces as a single String.
    *
    * @param       inLength the number of spaces to return
    *
    * @return      the request String consisting of spaces only
    *
    */
  public static @Nonnull String spaces(int inLength)
  {
    if(inLength >= s_spaces.length())
      throw new IllegalArgumentException("given length to high, check code");

    if(inLength <= 0)
      return "";

    return s_spaces.substring(0, inLength);
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
    * Convert the given object into a String, using the default value if the
    * object is null.
    *
    * @param       inValue   the value to convert
    * @param       inDefault the default value
    *
    * @return      the object converted to a String or the default value
    *
    */
  public static @Nonnull String toString(@Nullable Object inValue,
                                         @Nonnull String inDefault)
  {
    if(inValue == null)
      return inDefault;

    return inValue.toString();
  }

  //........................................................................
  //------------------------------- toString -------------------------------

  /**
    * Convert the given array into a String, using the default value if the
    * object is null.
    *
    * @param       inValues    the value to convert
    * @param       inDelimiter the delimiter to use
    * @param       inDefault   the default value
    *
    * @return      the object converted to a String or the default value
    *
    * @undefined   never, a null default value can be returned
    *
    */
  public static String toString(Object []inValues, String inDelimiter,
                                String inDefault)
  {
    if(inValues == null)
      return inDefault;

    StringBuilder result = new StringBuilder();

    for(int i = 0; i < inValues.length; i++)
    {
      result.append(inValues[i]);

      if(i + 1 < inValues.length)
        result.append(inDelimiter);
    }

    return result.toString();
  }

  //........................................................................
  //------------------------------- toString -------------------------------

  /**
    * Convert the given iterator into a String, using the given delimiter.
    *
    * @param       inIterator  the value to convert
    * @param       inDelimiter the delimiter between entries
    * @param       inDefault   the default value
    *
    * @return      the object converted to a String or the default value
    *
    */
  public static @Nonnull String toString(@Nullable Iterator inIterator,
                                         @Nonnull String inDelimiter,
                                         @Nonnull String inDefault)
  {
    if(inIterator == null)
      return inDefault;

    StringBuilder result = new StringBuilder();

    for(; inIterator.hasNext(); )
    {
      result.append(inIterator.next().toString());

      if(inIterator.hasNext())
        result.append(inDelimiter);
    }

    return result.toString();
  }

  //........................................................................
  //------------------------------- toString -------------------------------

  /**
    * Convert the given iterable into a String, using the given delimiter.
    *
    * @param       inIterable  the value to convert
    * @param       inDelimiter the delimiter between entries
    * @param       inDefault   the default value
    *
    * @return      the object converted to a String or the default value
    *
    */
  public static @Nonnull String toString(@Nullable Iterable inIterable,
                                         @Nonnull String inDelimiter,
                                         @Nonnull String inDefault)
  {
    if(inIterable == null)
      return inDefault;

    return toString(inIterable.iterator(), inDelimiter, inDefault);
  }

  //........................................................................

  //------------------------------ getPattern ------------------------------

  /**
    * Get the text that matches the given pattern.
    *
    * @param       inText the text to match in
    * @param       inPattern the pattern to match (with or without the (/))
    *
    * @return      the text that matched or null if nothing matched
    *
    * @example     String matched = Files.getPattern("my text", "t.*t");
    *
    */
  public static @Nullable String getPattern(@Nonnull String inText,
                                            @Nullable String inPattern)
  {
    if(inPattern == null)
      return inText;

    String pattern = inPattern;
    if(pattern.indexOf("(") < 0)
      pattern = "(" + pattern + ")";

    // check if we have a name
    Matcher matcher = Pattern.compile(pattern).matcher(inText);

    if(matcher.find())
      return matcher.group(1);
    else
      return null;
  }

  //........................................................................
  //----------------------------- getPatterns ------------------------------

  /**
   * Get the texts that matches the given pattern.
   *
   * @param       inText the text to match in
   * @param       inPattern the pattern to match (with or without the (/))
   *
   * @return      the texts that matched or null if nothing matched
   *
   * @undefined   never (may return null)
   *
   * @example     String []matched = Files.getPatterns("my text", "t.*t");
   *
   */
  public static @Nonnull String []getPatterns(@Nonnull String inText,
                                              @Nullable String inPattern)
  {
    if(inPattern == null)
      return new String [] { inText };

    if(inPattern.indexOf("(") < 0)
    {
      String result = getPattern(inText, inPattern);

      if(result == null)
        return new String [0];

      return new String [] { result };
    }

    // check if we have a name
    Matcher matcher = Pattern.compile(inPattern).matcher(inText);

    if(matcher.find())
    {
      String []found = new String[matcher.groupCount()];

      for(int i = 0; i < found.length; i++)
        found[i] = matcher.group(i + 1);

      return found;
    }
    else
      return new String[0];
  }

  //........................................................................
  //-------------------------- replaceTemplates ----------------------------

  /**
   * Replace templates in the given string.
   *
   * @param       inText   the text to replace in
   * @param       inPrefix the prefix to use to access variables in the config
   *
   * @return      the text with al templates replaced
   *
   * @example     String replaced = Files.replaceTemplate("my text");
   *
   */
  public static @Nonnull String replaceTemplates(@Nonnull String inText,
                                                 @Nonnull String inPrefix)
  {
    // check if we have a name
    Matcher matcher = s_template.matcher(inText);

    StringBuffer result = new StringBuffer();
    while(matcher.find())
    {
      String template = matcher.group(1);
      matcher.appendReplacement(result, Config.get(inPrefix + "." + template,
                                                   "*unknown*"));
    }

    matcher.appendTail(result);
    return result.toString();
  }

  //........................................................................


  //--------------------------------- pad ----------------------------------

  /**
    * Pad the given string into a field of the given length (padding spaces
    * if too short). If the boolean flag is true, pad to the left, otherwise
    * to the right.
    *
    * @param       inText   the text to pad
    * @param       inLength the length of the field to pad in
    * @param       inLeft   if true, pad to the left, otherwise to the right
    *
    * @return      the padded String
    *
    */
  public static @Nonnull String pad(@Nonnull String inText, int inLength,
                                    boolean inLeft)
  {
    if(inText.length() >= inLength)
      return inText;

    if(inLeft)
      return s_spaces.substring(0, inLength - inText.length()) + inText;
    else
      return inText + s_spaces.substring(0, inLength - inText.length());
  }

  //........................................................................
  //--------------------------------- pad ----------------------------------

  /**
    * Pad the given number into a field of the given length (padding spaces or
    * 0s if too short). If the boolean flag is true, pad to the left, otherwise
    * to the right.
    *
    * @param       inNumber the number to pad
    * @param       inLength the length of the field to pad in
    * @param       inLeft   if true, pad to the left, otherwise to the right
    *
    * @return      the padded String
    *
    */
  public static @Nonnull String pad(long inNumber, int inLength, boolean inLeft)
  {
    String text = Long.toString(inNumber);

    if(text.length() >= inLength)
      return text;

    if(inLeft)
      return s_zeroes.substring(0, inLength - text.length()) + text;
    else
      return text + s_spaces.substring(0, inLength - text.length());
  }

  //........................................................................
  //-------------------------------- limit ---------------------------------

  /**
   * Limit the given string to at most the given number of characters or up to
   * the given string.
   *
   * @param       inText the string to limit
   * @param       inLength the maximal length allowed
   * @param       inDelimiter the delimiter to stop at
   *
   * @return      the limited string
   *
   */
  public static @Nonnull String limit(@Nonnull String inText, int inLength,
                                      @Nonnull String inDelimiter)
  {
    int length = inText.indexOf(inDelimiter);

    if(length < 0 || length > inLength)
      length = inLength;

    if(length < 0)
      length = 0;
    else
      if(length > inText.length())
        return inText;

    return inText.substring(0, length);
  }

  //........................................................................
  //-------------------------------- format --------------------------------

  /**
   * Format the given number to be easier readable.
   *
   * @param       inNumber the number to format
   *
   * @return      A string representing the number
   *
   * @undefined   never
   *
   */
  public static @Nonnull String format(long inNumber)
  {
    return s_number.format(inNumber);
  }

  //........................................................................
  //-------------------------------- format --------------------------------

  /**
   * Format the given number to be easier readable.
   *
   * @param       inNumber the number to format
   *
   * @return      A string representing the number
   *
   * @undefined   never
   *
   */
  public static @Nonnull String format(double inNumber)
  {
    return s_number.format(inNumber);
  }

  //........................................................................

  //-------------------------------- nameLF --------------------------------

  /**
    * Switch the given Name to a name starting with the surename.
    *
    * @param       inName the name to switch
    *
    * @return      the switched name, in the form 'last, first'
    *
    */
  public static @Nonnull String nameLF(@Nonnull String inName)
  {
    String []parts = inName.split("\\s+");

    if(parts.length <= 1)
      return inName;

    StringBuilder switched = new StringBuilder(parts[parts.length - 1] + ",");

    for(int i = 0; i < parts.length - 1; i++)
      switched.append(" ").append(parts[i]);

    return switched.toString();
  }

  //........................................................................
  //-------------------------------- nameFL --------------------------------

  /**
    * Switch the given Name to a name starting with the firstname. I.e. from
    * 'L, F' to 'F L'.
    *
    * @param       inName the name to switch
    *
    * @return      the switched name, in the form 'first last'
    *
    */
  public static @Nonnull String nameFL(@Nonnull String inName)
  {
    String []parts = inName.split(",");

    if(parts.length <= 1)
      return inName;

    StringBuilder switched = new StringBuilder(parts[parts.length - 1].trim());

    for(int i = 0; i < parts.length - 1; i++)
      switched.append(" ").append(parts[i].trim());

    return switched.toString();
  }

  //........................................................................

  //-------------------------------- today ---------------------------------

  /**
    * Return a String giving the current date (and time).
    *
    * @return      A string with the current date
    *
    */
  public static @Nonnull String today()
  {
    return new SimpleDateFormat(DATE).format(new Date());
  }

  //........................................................................
  //---------------------------- extractNumber -----------------------------

  /**
    * Extract a number from the given string.
    *
    * @param       inText the text to extract from
    *
    * @return      the number found in the text or 0 if none was found
    *
    */
  public static int extractNumber(@Nullable String inText)
  {
    if(inText == null || inText.length() == 0)
      return 0;

    String pattern = getPattern(inText, "\\d+");

    if(pattern != null)
      return Integer.parseInt(pattern);

    // try with words
    for(int i = 0; i < s_words.length; i++)
      if(inText.matches("(?i).*\\b" + s_words[i] + "\\b.*"))
        return i + 1;

    // try with romans
    for(int i = 0; i < s_romans.length; i++)
      if(inText.matches(".*\\b" + s_romans[i] + "\\b.*"))
        return i + 1;

    return 0;
  }

  //........................................................................
  //----------------------------- formatNumber -----------------------------

  /**
    * Format the given number into a easy readable String.
    *
    * @param       inNumber the number to format
    *
    * @return      the formatted string
    *
    */
  public static @Nonnull String formatNumber(long inNumber)
  {
    StringBuffer result = new StringBuffer("" + inNumber);

    for(int i = result.length() - 4; i >= 0; i -= 3)
      if(Character.isDigit(result.charAt(i)))
        result.insert(i + 1, "'");

    return result.toString();
  }

  //........................................................................

  //------------------------------ handleNull ------------------------------

  /**
   * Return the same object, unless it is null, then return an empty string.
   *
   * @param       inObject the object to handle null for
   *
   * @return      the given object or the empty string if null is given
   *
   */
  public static @Nonnull Object handleNull(@Nullable Object inObject)
  {
    if(inObject == null)
      return "";

    return inObject;
  }

  //........................................................................
  //--------------------------------- sort ---------------------------------

  /**
   * Sort the given List (in place) according to the given values first and
   * lexicographically second.
   *
   * @param       inList  the list to sort (will be changed)
   * @param       inOrder the strings for a special sort order
   *
   * @return      the sorted list
   *
   */
  public static @Nonnull List<String> sort(@Nonnull List<String> inList,
                                           @Nonnull final String ... inOrder)
  {
    Collections.sort(inList, new Comparator<String>()
                     {
                       public int compare(String inFirst, String inSecond)
                       {
                         if(inFirst == null)
                           throw new IllegalArgumentException
                             ("must have a value here");

                         if(inSecond == null)
                           throw new IllegalArgumentException
                             ("must have a value here");

                         for(int i = 0; i < inOrder.length; i++)
                         {
                           if(inFirst.indexOf(inOrder[i]) != -1)
                             return -1;

                           if(inSecond.indexOf(inOrder[i]) != -1)
                             return 1;
                         }

                         // now the normal text ordering
                         return inFirst.compareTo(inSecond);
                       }
                     });

    return inList;
  }

  //........................................................................
  //------------------------------- sortable -------------------------------

  /**
   * Convert the string into one that can be sorted lexicographically.
   * This is mainly used to make sure that number are correctly sorted
   * by prefixing them with a bunch of 0.
   *
   * @param       inText the text to make sortable
   *
   * @return      the sortable text
   *
   */
  public static @Nonnull String sortable(@Nonnull String inText)
  {
    // check if we have a number
    Matcher matcher = Pattern.compile("(\\d+)").matcher(inText);

    StringBuffer result = new StringBuffer();

    while (matcher.find())
      matcher.appendReplacement(result,
                                pad(Long.parseLong(matcher.group(1)), 10,
                                    true));

    matcher.appendTail(result);

    return result.toString();
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //---------------------------------------------------------------- testing

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- spaces ---------------------------------------------------------

    /** Spaces tests. */
    @org.junit.Test(expected = IllegalArgumentException.class)
    public void spaces()
    {
      assertEquals("spaces", "     ", Strings.spaces(5));
      assertEquals("spaces", "             ", Strings.spaces(13));

      // wrong values
      assertEquals("wrong spaces", "", Strings.spaces(0));
      assertEquals("wrong spaces", "", Strings.spaces(-1));

      Strings.spaces(Integer.MAX_VALUE);
    }

    //......................................................................
    //----- conversion -----------------------------------------------------

    /** Conversion test. */
    @org.junit.Test
    public void conversion()
    {
      // Strings
      assertEquals("object", "test", Strings.toString("test", "guru"));
      assertEquals("empty object", "guru", Strings.toString((Object)null,
                                                            "guru"));
      // iterators
      assertEquals("list",   "1, 2, 3",
                   Strings.toString(java.util.Arrays.asList(new String []
                     {"1", "2", "3"}).iterator(), ", ", "empty"));
      assertEquals("empty list",   "empty",
                   Strings.toString((Iterator)null, ", ", "empty"));

      // iterables
      assertEquals("iterable", "1, 2, 3",
                   Strings.toString(java.util.Arrays.asList(new String []
                     {"1", "2", "3"}), ", ", "default"));
      assertEquals("iterable", "1",
                   Strings.toString(java.util.Arrays.asList(new String []
                     {"1", }), ", ", "default"));
      assertEquals("empty", "",
                   Strings.toString(java.util.Arrays.asList(new String [0]),
                                    ", ", "default"));
      assertEquals("null", "default",
                   Strings.toString((Iterable)null, ", ", "default"));

      // arrays
      assertEquals("array", "1, 2, null, 4",
                   Strings.toString(new String [] { "1", "2", null, "4" },
                                    ", ",
                                    "empty"));
      assertEquals("empty array", "",
                   Strings.toString(new String [0], ", ", "default"));
      assertEquals("empty array", "default",
                   Strings.toString((String [])null, ", ", "default"));
    }

    //......................................................................
    //----- pad String -----------------------------------------------------

    /** String padding test. */
    @org.junit.Test
    public void padString()
    {
      final int pad1   = 7;
      final int pad2   = 2;
      final int pad3   = -2;
      final int pad4   = -22;

      // normal
      assertEquals("right padding", "pad    ",
                   Strings.pad("pad", pad1, false));
      assertEquals("left padding",  "    pad",
                   Strings.pad("pad", pad1, true));

      // no real padding
      assertEquals("no padding", "padding",
                   Strings.pad("padding", pad2, false));
      assertEquals("no padding", "padding",
                   Strings.pad("padding", pad2, true));

      // wrong number
      assertEquals("no padding", "padding",
                   Strings.pad("padding", pad3, true));
      assertEquals("no padding", "padding",
                   Strings.pad("padding", pad4, false));
      assertEquals("no padding", "padding",
                   Strings.pad("padding", pad3, true));
      assertEquals("no padding", "padding",
                   Strings.pad("padding", pad4, false));
    }

    //......................................................................
    //----- pad number -----------------------------------------------------

    /** Padding tests. */
    @org.junit.Test
    public void padNumber()
    {
      final int number = 123;
      final int pad1   = 7;
      final int pad2   = 2;
      final int pad3   = -2;
      final int pad4   = -22;

      // normal
      assertEquals("right padding", "123    ",
                   Strings.pad(number, pad1, false));
      assertEquals("left padding",  "0000123",
                   Strings.pad(number, pad1, true));

      // no real padding
      assertEquals("no padding", "123", Strings.pad(number, pad2, false));
      assertEquals("no padding", "123", Strings.pad(number, pad2, true));

      // wrong number
      assertEquals("no padding", "123", Strings.pad(number, pad3, true));
      assertEquals("no padding", "123", Strings.pad(number, pad4, false));
    }

    //......................................................................
    //----- pattern --------------------------------------------------------

    /** Testing the pattern method. */
    @org.junit.Test
    public void pattern()
    {
      assertEquals("simple", "some",
                   Strings.getPattern("just some test", "some"));
      assertEquals("simple", "t some test",
                   Strings.getPattern("just some test", "t.*t"));
      assertEquals("group", "just some file",
                   Strings.getPattern("file:just some file!", ":(.*)!"));
      assertNull("no match", Strings.getPattern("just some test", "guru"));
      assertEquals("empty pattern", "",
                   Strings.getPattern("just some test", ""));
      assertNull("empty text", Strings.getPattern("", "a"));
      assertEquals("null pattern", "test", Strings.getPattern("test", null));
    }

    //......................................................................
    //----- patterns -------------------------------------------------------

    /** Testing the patterns method. */
    @org.junit.Test
    public void patterns()
    {
      assertEquals("simple", "[some]",
                   java.util.Arrays.toString
                   (Strings.getPatterns("just some test", "some")));
      assertEquals("simple", "[t some test]",
                   java.util.Arrays.toString
                   (Strings.getPatterns("just some test", "t.*t")));
      assertEquals("group", "[just some file]",
                   java.util.Arrays.toString
                   (Strings.getPatterns("file:just some file!", ":(.*)!")));
      assertEquals("no match", 0,
                   Strings.getPatterns("just some test", "guru").length);

      // really multiple
      assertEquals("multiple", "[a, b, 42]",
                   java.util.Arrays.toString
                   (Strings.getPatterns("a b 42", "(\\w) (\\w) (\\d+)")));
      assertEquals("no match", 0,
                   Strings.getPatterns("a b 42", "(\\d) (\\w)(\\w)").length);
      assertEquals("empty text", "[]",
                   java.util.Arrays.toString(Strings.getPatterns("", "")));
      assertEquals("null pattern", "[test]",
                   java.util.Arrays.toString(Strings.getPatterns("test",
                                                                 null)));
    }

    //......................................................................
    //----- name -----------------------------------------------------------

    /** Testing the names. */
    @org.junit.Test
    public void name()
    {
      // empty strings
      assertEquals("empty", "", nameFL(""));
      assertEquals("empty", "", nameLF(""));

      // normal strings
      assertEquals("normal", "last, first", nameLF("first last"));
      assertEquals("normal", "first last", nameFL("last, first"));

      // multiple delimiters
      assertEquals("multi", "last, first, second",
                   nameLF("first, second last"));
      assertEquals("multi", "first last second", nameFL("last second, first"));

      // no delimiters
      assertEquals("none", "last first", nameFL("last first"));
      assertEquals("none", "last,first", nameLF("last,first"));

      // ending delimiters
      assertEquals("ending", "last ", nameFL("last "));
      assertEquals("ending", "last,", nameLF("last,"));
    }

    //......................................................................
    //----- extract number -------------------------------------------------

    /** Test extracting of number. */
    @org.junit.Test
    public void extractNumber()
    {
      assertEquals("simple", 42, Strings.extractNumber("42"));
      assertEquals("with text", 42, Strings.extractNumber("hi 42 you"));

      // negative values
      assertEquals("negative", 42, Strings.extractNumber("again -42 value"));

      // words
      assertEquals("words", 1, Strings.extractNumber("some one is there"));
      assertEquals("words", 12, Strings.extractNumber("its soon TWELVE"));
      assertEquals("words", 5, Strings.extractNumber("five bottles"));
      assertEquals("words", 10, Strings.extractNumber("Ten"));

      // roman literals
      assertEquals("roman", 5, Strings.extractNumber("ave V ceasar"));
      assertEquals("roman", 9, Strings.extractNumber("IX"));
      assertEquals("roman", 0, Strings.extractNumber("how about iv"));
      // two numbers
      assertEquals("two", 15, Strings.extractNumber("now 15 IV 3 seven"));

      // limits
      assertEquals("null", 0, Strings.extractNumber(null));
      assertEquals("empty", 0, Strings.extractNumber(""));
    }

    //......................................................................
    //----- formatNumber ---------------------------------------------------

    /** Testing the formatting of numbers. */
    @org.junit.Test
    public void formatNumber()
    {
      // border cases
      assertEquals("0", "0", Strings.formatNumber(0));

      // big numbers
      assertEquals("big", "123'456", Strings.formatNumber(123456));
      assertEquals("big", "1'234", Strings.formatNumber(1234));
      assertEquals("small", "123", Strings.formatNumber(123));
      assertEquals("very big", "1'234'567", Strings.formatNumber(1234567));

      // negative numbers
      assertEquals("negative", "-1", Strings.formatNumber(-1));
      assertEquals("negative", "-1'234", Strings.formatNumber(-1234));
      assertEquals("negative", "-123'456", Strings.formatNumber(-123456));
    }

    //......................................................................
    //----- sortable -------------------------------------------------------

    /** Test sortable strings. */
    @org.junit.Test
    public void sortable()
    {
      assertEquals("empty", "", Strings.sortable(""));

      assertEquals("text", "just a test", Strings.sortable("just a test"));

      assertEquals("one", "just 0000000001 test",
                   Strings.sortable("just 1 test"));
      assertEquals("42",  "just 0000000042 test",
                   Strings.sortable("just 42 test"));
      assertEquals("120", "just 0000000120 test",
                   Strings.sortable("just 120 test"));
      assertEquals("007", "just 0000000007 test",
                   Strings.sortable("just 007 test"));
      assertEquals("0",   "just 0000000000 test",
                   Strings.sortable("just 0 test"));
      assertEquals("-23", "just -0000000023 test",
                   Strings.sortable("just -23 test"));
    }

    //......................................................................
    //----- limit ----------------------------------------------------------

    /** Limit Test. */
    @org.junit.Test
    public void limit()
    {
      assertEquals("empty", "", Strings.limit("", 42, ","));
      assertEquals("0 limit", "", Strings.limit("", 0, ","));
      assertEquals("empty delim", "", Strings.limit("a test", 5, ""));
      assertEquals("- limit", "", Strings.limit("", -4, ","));
      assertEquals("0 limit with text", "", Strings.limit("text", 0, ","));
      assertEquals("full", "text", Strings.limit("text", 42, " "));

      assertEquals("text", "just a", Strings.limit("just a test", 6, ":"));
      assertEquals("text char", "just", Strings.limit("just a test", 6, " "));
      assertEquals("multi char", "just a t",
                   Strings.limit("just a test", 10, "est"));
    }

    //......................................................................
    //----- format ---------------------------------------------------------

    /** format Test. */
    @org.junit.Test
    public void format()
    {
      assertEquals("0", "0", Strings.format(0));
      assertEquals("<0", "-42", Strings.format(-42));
      assertEquals(">0", "42", Strings.format(42));
      assertEquals("<<0", "-123'456'789", Strings.format(-123456789));
      assertEquals(">>0", "123'456'789", Strings.format(123456789));

      assertEquals("0", "0", Strings.format(0.0));
      assertEquals("<0", "-4.2", Strings.format(-4.2));
      assertEquals(">0", "4.2", Strings.format(4.2));
      assertEquals("<<0", "-12'345.679", Strings.format(-12345.6789));
      assertEquals(">>0", "12'345.679", Strings.format(12345.6789));
    }

    //......................................................................
    //----- today ----------------------------------------------------------

    /** today Test. */
    @org.junit.Test
    public void today()
    {
      assertNotNull("not null", Strings.today());
    }

    //......................................................................
    //----- handle ---------------------------------------------------------

    /** handle Test. */
    @org.junit.Test
    public void handle()
    {
      assertEquals("null", "", Strings.handleNull(null));
      assertEquals("non null", "test", Strings.handleNull("test"));
    }

    //......................................................................
    //----- sort -----------------------------------------------------------

    /** sort Test. */
    @org.junit.Test
    public void sort()
    {
      assertEquals
        ("simple",
         com.google.common.collect.Lists.newArrayList("two", "four", "five",
                                                      "one", "three"),
         Strings.sort
         (com.google.common.collect.Lists.newArrayList("one", "two", "three",
                                                       "four", "five"),
          "two", "six", "four"));
    }

    //......................................................................
    //----- replaceTemplates -----------------------------------------------

    /** The replaceTemplates Test. */
    @org.junit.Test
    public void replaceTemplates()
    {
      Config.setRewriting(false);
      assertEquals("no templates", "a test without a template",
                   Strings.replaceTemplates("a test without a template",
                                            "test/test/template"));
      assertEquals("unknown", "a *unknown* test",
                   Strings.replaceTemplates("a $test test",
                                            "test/test/template"));
      assertEquals("valid", "some single word test",
                   Strings.replaceTemplates("some $simple test",
                                            "test/test/template"));
      Config.setRewriting(true);
    }

    //......................................................................

    //----- coverage -------------------------------------------------------

    /** Coverage test. */
    @org.junit.Test
    public void coverage()
    {
      new Strings();
    }

    //......................................................................
  }

  //........................................................................
}
