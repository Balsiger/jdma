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

import com.google.common.base.Joiner;

import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.FormattedValue;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.entries.ValueHandle;
import net.ixitxachitls.dma.entries.extensions.AbstractExtension;
import net.ixitxachitls.dma.values.Combination;
import net.ixitxachitls.output.commands.BaseCommand;
import net.ixitxachitls.output.commands.Color;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Divider;
import net.ixitxachitls.output.commands.Section;
import net.ixitxachitls.output.commands.Value;
import net.ixitxachitls.output.commands.Window;
import net.ixitxachitls.util.Encodings;

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
public abstract class AbstractPrint
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
    "((?:\\$|#|%|\\?|&)(?:\\+|&|>|<)?)(?:\\{(.*?)\\}|(\\w+))";

  /** The joiner to concatenate commands. */
  private static final Joiner s_joiner = Joiner.on("").skipNulls();

 //........................................................................

  //-------------------------------------------------------------- accessors

  //---------------------------- printExtension ----------------------------

  /**
   * Print the extension information.
   *
   * @param     inExtension the extension to print
   * @param     inUser      the user printing for
   *
   * @return    an object representing the desired print
   *
   */
  protected abstract @Nonnull Object
    printExtension(@Nonnull AbstractExtension inExtension,
                   @Nonnull BaseCharacter inUser);

  //........................................................................

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
                                 @Nonnull ValueGroup inEntry,
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

        if("$%#".indexOf(prefix) < 0)
          result.add(token);
        else
        {
          String name = token.substring(1);

          switch(prefix)
          {
            case '$':
              // A simple, directly printed value
              ValueHandle handle = compute(inEntry, name, inEntry.isDM(inUser));
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
              handle = compute(inEntry, name, inEntry.isDM(inUser));

              // treat special names for uppercase
              name = name.replaceAll("\\bdm\\b", "DM")
                .replaceAll("^>", "")
                .replaceAll("^<", "");

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

            case '#':

              AbstractExtension extension = null;
              if(inEntry instanceof AbstractEntry)
                extension = ((AbstractEntry)inEntry).getExtension(name);

              if(extension != null)
                result.add(new Section(name + " extension",
                                       printExtension(extension, inUser)));

              break;

            default:
              throw new IllegalStateException("should not happen");
          }
        }
      }

    if(result.isEmpty())
      return "";

    if(result.size() == 1)
      return result.get(0);

    //return new Command(result);
    return new BaseCommand(s_joiner.join(result));
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
  public @Nullable ValueHandle compute(@Nonnull ValueGroup inEntry,
                                       @Nonnull String inName, boolean inDM)
  {
    String name = inName.substring(1);
    switch(inName.charAt(0))
    {
      case '&':
      case '+':
        throw new UnsupportedOperationException("not yet implemented");

      case '>':
        Combination combination = new Combination(inEntry, name);
        if(combination.max() == null)
          return null;

        return new FormattedValue(new Window(combination.max().format(),
                                             combination.summary(), "", "base"),
                                  "", name);

      case '<':
        combination = new Combination(inEntry, name);
        if(combination.min() == null)
          return null;

        return new FormattedValue(new Window(combination.min().format(),
                                             combination.summary(), "", "base"),
                                  "", name);

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
