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

package net.ixitxachitls.dma.output;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.FormattedValue;
import net.ixitxachitls.dma.entries.ValueHandle;
import net.ixitxachitls.output.commands.Color;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Divider;
import net.ixitxachitls.output.commands.Value;
import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base for printing values.
 *
 *
 * @file          AbstractPrint.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class AbstractPrint
{
  //--------------------------------------------------------- constructor(s)

  //---------------------------- AbstractPrint -----------------------------

  /**
   * Construct the print object.
   *
   */
  public AbstractPrint()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Tokenizer string to separate values to print. */
  private static final @Nonnull String s_delimiter =
    "(\\$|#|%|\\?|&)(?:\\{(.*?)\\}|(\\w+))";

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- tokenize -------------------------------

  /**
   * Tokenize the given template.
   *
   * @param       inTemplate the tampel to tokenize
   *
   * @return      the tokens found
   *
   */
  public @Nonnull List<String> tokenize(@Nonnull String inTemplate)
  {
    return Encodings.tokenize(inTemplate, s_delimiter);
  }

  //........................................................................
  //------------------------------- convert --------------------------------

  /**
   * Convert the given list of tokens into a list of commands for printing.
   *
   * @param       inTokens    the tokens to parse
   * @param       inEntry     the entry with the values
   * @param       inNullValue the value to use for null tokens
   * @param       inUser      the user for whome to convert, if any
   *
   * @return      the object for printing the parse values
   *
   */
  public @Nonnull Object convert(@Nullable List<String> inTokens,
                                 @Nonnull AbstractEntry inEntry,
                                 @Nonnull String inNullValue,
                                 @Nullable BaseCharacter inUser)
  {
    List<Object> result = new ArrayList<Object>();

    boolean dm = inEntry.isDM(inUser);
    if(inTokens == null)
      result.add(inNullValue);
    else
      for(String token : inTokens)
      {
        if(token.isEmpty())
          continue;

        char prefix = token.charAt(0);

        if("$%".indexOf(prefix) < 0)
          result.add(token);
        else
        {
          String name = token.substring(1);
          ValueHandle handle = compute(inEntry, name, inEntry.isDM(inUser));

          switch(prefix)
          {
            case '$':
              // A simple, directly printed value
              if(handle != null)
              {
                Object formatted =
                  handle.format(inEntry, dm, inUser != null);
                if(formatted != null)
                  result.add(formatted);
              }
              else
                result.add(new Color("error", " * " + name + " * "));

              break;

            case '%':
              // A value as tabelarized data
              Command label =
                new Divider("value-label-container back-"
                            + inEntry.getType().getName().replaceAll("\\s+",
                                                                     "-"),
                            new Divider("value-label",
                                        Encodings.toWordUpperCase(name)));

              Object value;
              if(handle != null)
                value = handle.format(inEntry, dm, inUser != null);
              else
                value = new Color("error", " * unknown * ");

              if(value != null)
                result.add(new Value(inEntry.getType().getClassName(),  label,
                                     new Divider("value-content", value)));

              break;

            default:
              Log.warning("invalid token '" + token.charAt(0) + " encountered");
          }
        }
      }

    if(result.isEmpty())
      return "";

    if(result.size() == 1)
      return result.get(0);

    return new Command(result);
  }

  //........................................................................
  //------------------------------- compute --------------------------------

  /**
   * Computes the value for the given name.
   *
   * @param       inEntry     the entry with the values
   * @param       inName the name of the value to compute
   * @param       inDM   true if computing for dm, false if not
   *
   * @return      the computed value or null if not found
   *
   */
  public @Nullable ValueHandle compute(@Nonnull AbstractEntry inEntry,
                                       @Nonnull String inName, boolean inDM)
  {
    switch(inName.charAt(0))
    {
      case '+':
        return new FormattedValue
          (inEntry.combineBaseValues(inName.substring(1)), null, inName, inDM,
           false, false, false, null, null);

      default:
        return inEntry.computeValue(inName, inDM);
    }
  }

  //........................................................................


  //........................................................................

  //------------------------------------------------------------------- test

  // see Print

  //........................................................................
}
