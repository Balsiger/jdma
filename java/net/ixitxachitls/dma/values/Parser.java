/******************************************************************************
 * Copyright (c) 2002-2015 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.values;

import com.google.common.base.Optional;

/**
 * Simple interface for parsing values.
 *
 * @file Parser.java
 * @author balsiger@ixitxahitls.net (Peter Balsiger)
 *
 * @param <P> the value resulting after parsing
 */
public abstract class Parser<P>
{
  /**
   * Create the parser.
   *
   * @param inArguments the number of arguments needed for parsing or -1 for
   *                    any number
   */
  public Parser(int inArguments)
  {
    m_arguments = inArguments;
  }

  /** The number of expected arguments, or -1 for any number. */
  private final int m_arguments;

  /**
   * Parse the value from the given string.
   *
   * @param inValues the string values to parse from
   * @return the parsed value
   */
  public Optional<P> parse(String ... inValues)
  {
    if(inValues == null || inValues.length == 0)
      return Optional.absent();

    for(int i = 0; i < inValues.length; i++)
      if(inValues[i] == null)
        inValues[i] = "";

    if(m_arguments > 0 && inValues.length != m_arguments)
      inValues = split(inValues);

    if(m_arguments > 0 && inValues.length != m_arguments)
      return Optional.absent();

    switch(m_arguments)
    {
      case 1:
        return doParse(inValues[0]);

      case 2:
        return doParse(inValues[0], inValues[1]);

      case 3:
        return doParse(inValues[0], inValues[1], inValues[2]);

      case 4:
        return doParse(inValues[0], inValues[1], inValues[2], inValues[3]);

      case 5:
        return doParse(inValues[0], inValues[1], inValues[2], inValues[3],
                       inValues[4]);

      default:
        return doParse(inValues);
    }
  }

  /**
   * Split the given values into part values.
   *
   * @param inValues the values to split
   * @return the split values
   */
  protected String []split(String []inValues)
  {
    return inValues;
  }

  /**
   * Do the parsing for a single value parser.
   *
   * @param inValue the value to parse
   * @return the parsed value or absent if parsing failed
   */
  protected Optional<P> doParse(String inValue)
  {
    return Optional.absent();
  }

  /**
   * Do the parsing for a two value parser.
   *
   * @param inFirst the first value to parse
   * @param inSecond the second value to parse
   * @return the parsed value or absent if parsing failed
   */
  protected Optional<P> doParse(String inFirst, String inSecond)
  {
    return Optional.absent();
  }

  /**
   * Do the parsing for a three value parser.
   *
   * @param inFirst the first value to parse
   * @param inSecond the second value to parse
   * @param inThird the third value to parse
   * @return the parsed value or absent if parsing failed
   */
  protected Optional<P> doParse(String inFirst, String inSecond,
                                String inThird)
  {
    return Optional.absent();
  }

  /**
   * Do the parsing for a four value parser.
   *
   * @param inFirst the first value to parse
   * @param inSecond the second value to parse
   * @param inThird the third value to parse
   * @param inFourth the fourth value to parse
   * @return the parsed value or absent if parsing failed
   */
  protected Optional<P> doParse(String inFirst, String inSecond,
                                String inThird, String inFourth)
  {
    return Optional.absent();
  }

  /**
   * Do the parsing for a five value parser.
   *
   * @param inFirst the first value to parse
   * @param inSecond the second value to parse
   * @param inThird the third value to parse
   * @param inFourth the fourth value to parse
   * @param inFifth the fifth value to parse
   * @return the parsed value or absent if parsing failed
   */
  protected Optional<P> doParse(String inFirst, String inSecond,
                                String inThird, String inFourth,
                                String inFifth)
  {
    return Optional.absent();
  }

  /**
   * Do the parsing for more than 5 arguments.
   *
   * @param inValues the arguments to parse with
   * @return the parsed value or absent if parsing failed
   */
  protected Optional<P> doParse(String ... inValues)
  {
    return Optional.absent();
  }
}
