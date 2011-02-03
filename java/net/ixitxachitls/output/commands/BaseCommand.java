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

package net.ixitxachitls.output.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.util.configuration.Config;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the command object that stores all the commands for the output.
 *
 * @file          Command.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class BaseCommand extends Command
{
  //----------------------------------------------------------------- nested

  //----- Builder ----------------------------------------------------------

  /** A simple builder to build commands with various arguments. */
  public static class Builder
  {
    /**
     * Create the builder.
     *
     * @param inCommand the command to build from.
     *
     */
    public Builder(@Nonnull BaseCommand inCommand)
    {
      m_command = (BaseCommand)inCommand.clone();
    }

    /** The command being built. */
    private @Nonnull BaseCommand  m_command;

    /**
     * Build the command and return it.
     *
     * @return the command built
     *
     */
    public BaseCommand build()
    {
      return m_command;
    }

    /**
     * Set the arguments the command is to be built with.
     *
     * @param inArguments the arguments for the command
     *
     * @return the builder, for chaining
     *
     */
    public Builder withArguments(@Nonnull Object ... inArguments)
    {
      m_command.withArguments(inArguments);

      return this;
    }

    /**
     * Set the arguments the command is to be built with.
     *
     * @param inArguments the arguments for the command
     *
     * @return the builder, for chaining
     *
     */
    public Builder withArguments(@Nonnull List<Object> inArguments)
    {
      m_command.withArguments(inArguments);

      return this;
    }

    /**
     * Set the optionals arguments the command is to be built with.
     *
     * @param inOptionals the optional arguments for the command
     *
     * @return the builder, for chaining
     *
     */
    public Builder withOptionals(@Nonnull Object ... inOptionals)
    {
      m_command.withOptionals(inOptionals);

      return this;
    }

    /**
     * Set the optionals arguments the command is to be built with.
     *
     * @param inOptionals the optional arguments for the command
     *
     * @return the builder, for chaining
     *
     */
    public Builder withOptionals(@Nonnull List<Object> inOptionals)
    {
      m_command.withOptionals(inOptionals);

      return this;
    }
  }

  //........................................................................


//   public interface Transformer
//   {
//     public Object transform(Object inCommand);
//   }

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //----------------------------- BaseCommand ------------------------------

  /**
   * This is the constructor for derivations, the arguments must be filled
   * separately.
   *
   * @param       inName         the name of the command (can be null for
   *                             text only)
   * @param       inNbrOptionals the number of optional arguments (-1 for any)
   * @param       inNbrArguments the number of arguments (-1 for any)
   * @param       inArguments    the arguments for the command (if any)
   *
   */
  protected BaseCommand(@Nonnull String inName, int inNbrOptionals,
                        int inNbrArguments, @Nonnull Object ... inArguments)
  {
    m_name = inName;
    m_optNumber = inNbrOptionals;
    m_argNumber = inNbrArguments;

    if(inArguments.length > 0)
      withArguments(inArguments);
  }

  //........................................................................
  //----------------------------- BaseCommand ------------------------------

  /**
   * This is the internal constructor for a command.
   *
   */
  protected BaseCommand()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The name of the command (null for no command but only text). */
  protected @Nonnull String m_name = s_defaultName;

  /** The number of optional arguments (-1 for any number). */
  protected int m_optNumber = -1;

  /** The number of arguments (-1 for any number). */
  protected int m_argNumber = -1;

  /** The optional arguments. */
  protected @Nullable java.util.List<Object> m_optionals =
    new ArrayList<Object>();

  /** Command starter character. */
  protected static final char s_command =
    Config.get("resource:writer/command", '\\');

  /** Argument starter character. */
  protected static final char s_argStart =
    Config.get("resource:writer/argument.start", '{');

  /** Argument ending character. */
  protected static final char s_argEnd =
    Config.get("resource:writer/argument.end", '}');

  /** Character starting an optional argument. */
  protected static final char s_optArgStart =
    Config.get("resource:writer/optional.argument.start", '[');

  /** Character ending an optional argument. */
  protected static final char s_optArgEnd =
    Config.get("resource:writer/optional.argument.end", ']');

  /** Escape character. */
  protected static final char s_escape =
    Config.get("resource:writer/escape", '\\');

  /** Marking character for argument starts. */
  protected static final char s_markArgStart = '\001';

  /** Marking character for argument ends. */
  protected static final char s_markArgEnd = '\002';

  /** Marking character for optional argument starts. */
  protected static final char s_markOptArgStart = '\003';

  /** Marking character for optional argument ends. */
  protected static final char s_markOptArgEnd = '\004';

  /** Marking character for escaped start. */
  protected static final char s_markStart = '\005';

  /** Marking character for escaped end. */
  protected static final char s_markEnd = '\006';

  /** The name of the package for this class. */
  protected static final @Nonnull String s_package =
    Command.class.getName().substring(0, Command.class.getName()
                                      .lastIndexOf('.') + 1);
                                              // Class.getPackage() is null
                                              // when testing

  /** The default name of an action with no other name. */
  private static final @Nonnull String s_defaultName = "baseCommand";

  /** The characters that are allowed in 'special character only commands'. */
  private static final @Nonnull String s_special = "<>=!~*#$%@?+|";

  /** The commands associated with each special command string. */
  private static final @Nonnull Map<String, String> s_specialNames =
    new HashMap<String, String>();

  static
  {
    // fill in the default special names
    s_specialNames.put("<",  "Less");
    s_specialNames.put("<=", "LessEqual");
    s_specialNames.put(">",  "Greater");
    s_specialNames.put(">=", "GreaterEqual");
    s_specialNames.put("\"", "Umlaut");
    s_specialNames.put("^",  "Hat");
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getName --------------------------------

  /**
   * Get the name of the command.
   *
   * @return      the command name
   *
   */
  public @Nonnull String getName()
  {
    return m_name;
  }

  //........................................................................
  //----------------------------- getOptionals -----------------------------

  /**
   * Get all the optional arguments.
   *
   * @return      all optional arguments (either as Commands or as arbitrary
   *              Object (use toString())
   *
   */
  public @Nonnull List<Object> getOptionals()
  {
    return Collections.unmodifiableList(m_optionals);
  }

  //........................................................................
  //----------------------------- getArguments -----------------------------

  /**
   * Get all the arguments.
   *
   * @return      all arguments (as Commands or Objects)
   *
   */
  public @Nonnull List<Object> getArguments()
  {
    return Collections.unmodifiableList(m_arguments);
  }

  //........................................................................

  //------------------------------- isEmpty --------------------------------

  /**
   * Check if the command represents any data at all.
   *
   * @return      true if the command is empty, false if not
   *
   */
  public boolean isEmpty()
  {
    if(!super.isEmpty())
      return false;

    for(Object element : m_optionals)
    {
      if(element instanceof String && !((String)element).isEmpty())
        return false;

      if(element instanceof Command && !((Command)element).isEmpty())
        return false;
    }

    return true;
  }

  //........................................................................
  //-------------------------------- equals --------------------------------

  /**
   * Check for equality of the given errors.
   *
   * @param       inOther the object to compare to
   *
   * @return      true if equal, false else
   *
   */
  public boolean equals(Object inOther)
  {
    if(this == inOther)
      return true;

    if(inOther == null)
      return false;

    if(inOther instanceof BaseCommand)
    {
      BaseCommand other = (BaseCommand)inOther;
      return m_name.equals(other.m_name)
        && m_optionals.equals(other.m_optionals)
        && super.equals(inOther);
    }
    else
      return super.equals(inOther);
  }

  //........................................................................
  //------------------------------- hashCode -------------------------------

  /**
   * Compute the hash code for this class.
   *
   * @return      the hash code
   *
   */
  public int hashCode()
  {
    return super.hashCode() + m_optionals.hashCode() + m_name.hashCode();
  }

  //........................................................................

  //------------------------------- toString -------------------------------

  /**
   * Convert the command to a human readable String, mainly for debugging.
   *
   * @return      a human readable conversion of the object
   *
   */
  public @Nonnull String toString()
  {
    StringBuilder result = new StringBuilder();

    // we have a real command, thus lets print it
    result.append(s_command);
    result.append(m_name);

    if(m_optionals.size() > 0
       || m_arguments != null && m_arguments.size() > 0)
    {
      for(Object object : m_optionals)
      {
        result.append(s_optArgStart);
        result.append(object);
        result.append(s_optArgEnd);
      }

      for(Object arg : m_arguments)
      {
        result.append(s_argStart);
        result.append(arg);
        result.append(s_argEnd);
      }
    }
    else
      // we print a space after a command with no arguments to make sure
      // it can really be parsed again
      result.append(' ');

    return result.toString();
  }

  //........................................................................

  //-------------------------------- parse ---------------------------------

  /**
   * Parse the given string and return a list of the commands found.
   *
   * @param       inText the text to parse
   *
   * @return      the list of all the commands found in the string
   *
   */
  protected static @Nonnull List<Object> parse(String inText)
  {
    List<Object> result = new ArrayList<Object>();

    // we don't have any text, thus we don't have to do anything
    if(inText == null || inText.isEmpty())
      return result;

    // mark the brackets in the text
    inText = markBrackets(inText, s_escape, s_argStart, s_argEnd,
                          s_markArgStart, s_markArgEnd);
    inText = markBrackets(inText, s_escape, s_optArgStart, s_optArgEnd,
                          s_markOptArgStart, s_markOptArgEnd);

    for(int start = 0; start < inText.length(); )
    {
      // search the first command (don't accept escaped commands)
      int pos = -1;
      for(pos = inText.indexOf(s_command, start); pos >= 0;
          pos = inText.indexOf(s_command, pos + 1))
        if((pos == 0 || inText.charAt(pos - 1) != s_escape)
           && (s_escape != s_command || inText.charAt(pos + 1) != s_command)
           && (Character.isLetterOrDigit(inText.charAt(pos + 1))
               || s_special.indexOf(inText.charAt(pos + 1)) >= 0))
          break;

      if(pos < 0)
      {
        // no commands any more
        result.add(clean(inText.substring(start)));

        break;
      }

      // intermediate text
      if(pos > 0)
        result.add(clean(inText.substring(start, pos)));

      // ok, we really have a command now
      int end = pos + 1;
      if(Character.isLetterOrDigit(inText.charAt(end)))
      {
        for( ; end < inText.length(); end++)
          if(!Character.isLetterOrDigit(inText.charAt(end)))
            break;
      }
      else
        for( ; end < inText.length(); end++)
          if(s_special.indexOf(inText.charAt(end)) < 0)
            break;

      String name = inText.substring(pos + 1, end);

      // extract the optional arguments
      List<Object> optionals = new ArrayList<Object>();

      Pattern pattern = Pattern.compile("^\\s*" + s_markOptArgStart
                                        + "<(\\d+)>((?:[^" + s_markOptArgEnd
                                        + "]*?" + s_markOptArgEnd
                                        + "<\\1>\\s*" + s_markOptArgStart
                                        + "<\\1>)*[^" + s_markOptArgEnd
                                        + "]*?)" + s_markOptArgEnd + "<\\1>");

      Matcher matcher = pattern.matcher(inText.substring(end));
      if(matcher.find())
      {
        String []args = matcher.group(2).split(s_markOptArgEnd + "<"
                                               + matcher.group(1) + ">\\s*"
                                               + s_markOptArgStart + "<"
                                               + matcher.group(1) + ">");

        end += matcher.end();

        // now we have all the arguments, we need to parse them as well
        for(int i = 0; i < args.length; i++)
        {
          List<Object> parsed = BaseCommand.parse(args[i]);

          if(parsed.size() == 1)
            optionals.add(parsed.get(0));
          else
            optionals.add(new Command(parsed));
        }
      }

      // extract the arguments (we have to copy the above because we need to
      // update the position in the String as well as extracting the arguments;
      // to solve that, the method would not be able to be static)
      List<Object> arguments = new ArrayList<Object>();

      pattern = Pattern.compile("^\\s*" + s_markArgStart + "<(\\d+)>((?:[^"
                                + s_markArgEnd + "]*?" + s_markArgEnd
                                + "<\\1>\\s*" + s_markArgStart
                                + "<\\1>)*[^" + s_markArgEnd + "]*?)"
                                + s_markArgEnd + "<\\1>");

      matcher = pattern.matcher(inText.substring(end));
      if(matcher.find())
      {
        String []args = matcher.group(2).split(s_markArgEnd + "<"
                                               + matcher.group(1) + ">\\s*"
                                               + s_markArgStart + "<"
                                               + matcher.group(1) + ">");

        end += matcher.end();

        // now we have all the arguments, we need to parse them as well
        for(int i = 0; i < args.length; i++)
        {
          List<Object> parsed = BaseCommand.parse(args[i]);

          if(parsed.size() == 1)
            arguments.add(parsed.get(0));
          else
            arguments.add(new Command(parsed));
        }
      }

      // now we instantiate the command with the name found

      // get the class associated with the name of the command
      String className;

      if(Character.isLetterOrDigit(name.charAt(0)))
        className = Character.toUpperCase(name.charAt(0))
          + name.substring(1);
      else
        className = s_specialNames.get(name);

      BaseCommand command = null;
      try
      {
        java.lang.Class commandClass =
          java.lang.Class.forName(s_package + className);

        command = (BaseCommand)commandClass.newInstance();
        command.m_name = name;
        // could load class, now construct it
      }
      catch(java.lang.InstantiationException e)
      {
        Log.error("cannot instantiate command " + name + " [" + className
                  + "]: " + e);

        command = new BaseCommand(name, -1, -1);
      }
      catch(java.lang.IllegalAccessException e)
      {
        Log.error("cannot instantiate command " + name + " [" + className
                  + "]: " + e);

        command = new BaseCommand(name, -1, -1);
      }
      catch(ClassNotFoundException e)
      {
        Log.warning("could not load command '" + name + "' [" + className
                    + "]: " + e);

        command = new BaseCommand(name, -1, -1);
      }

      // check and set the arguments
      if(command.m_optNumber == -1 || command.m_optNumber == optionals.size())
        command.m_optionals.addAll(optionals);
      else
      {
        if(command.m_optNumber < optionals.size())
          Log.warning("too many optional arguments given for '" + name
                      + "', surplus will be ignored");

        if(optionals.size() > 0)
        {
          for(int i = 0; i < command.m_optNumber; i++)
            if(i < optionals.size())
              command.m_optionals.add(optionals.get(i));
            else
              command.m_optionals.add("");
        }
      }

      if(command.m_argNumber == -1 || command.m_argNumber == arguments.size())
        command.m_arguments.addAll(arguments);
      else
      {
        if(command.m_argNumber < arguments.size())
          Log.warning("too many arguments given for '" + name
                      + "', surplus will be ignored (at "
                      + inText.substring(Math.max(pos - 15, 0),
                                         Math.min(inText.length(), pos + 35))
                      + "...)");
        else
          Log.warning("not enough arguments given for '" + name
                      + "', missing arguments will be empty (at "
                      + inText.substring(Math.max(pos - 15, 0),
                                         Math.min(inText.length(), pos + 35))
                      + "...)");

        if(command.m_argNumber > 0)
        {
          for(int i = 0; i < command.m_argNumber; i++)
            if(i < arguments.size())
              command.m_arguments.add(arguments.get(i));
            else
              command.m_arguments.add("");
        }
      }

      result.add(command);

      // if no arguments were found, then skip the character directly following
      // the command (must be a white space)
      if(arguments.size() == 0 && optionals.size() == 0
         && inText.length() > end
         && Character.isWhitespace(inText.charAt(end)))
        end++;

      start = end;
    }

    return result;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //----------------------------- withArguments ----------------------------

  /**
   * Add the given arguments to the list of arguments.
   *
   * @param       inArguments the arguments to add
   * @return      this command, for chaining
   *
   */
  protected Command withArguments(@Nonnull Object ... inArguments)
  {
    return withArguments(Arrays.asList(inArguments));
  }

  //........................................................................
  //----------------------------- withArguments ----------------------------

  /**
   * Add the given arguments to the list of arguments.
   *
   * @param       inArguments the arguments to add
   * @return      this command, for chaining
   *
   */
  protected Command withArguments(@Nonnull List<Object> inArguments)
  {
    if(m_argNumber >= 0 && m_argNumber != inArguments.size())
      Log.warning("invalid number of argments given for command " + m_name
                  + ", expected " + m_argNumber + " but got "
                  + inArguments.size());

    m_arguments.addAll(inArguments);

    return this;
  }

  //........................................................................
  //----------------------------- withOptionals ----------------------------

  /**
   * Add the given optional arguments to the list of arguments.
   *
   * @param       inArguments the optional arguments to add
   * @return      this command, for chaining
   *
   */
  public Command withOptionals(@Nonnull Object ... inArguments)
  {
    return withOptionals(Arrays.asList(inArguments));
  }

  //........................................................................
  //----------------------------- withOptionals ----------------------------

  /**
   * Add the given optional arguments to the list of arguments.
   *
   * @param       inArguments the optional arguments to add
   * @return      this command, for chaining
   *
   */
  public Command withOptionals(@Nonnull List<Object> inArguments)
  {
    if(m_optNumber >= 0 && m_optNumber < inArguments.size())
      Log.warning("invalid number of optional argments given for command "
                  + m_name + ", expected " + m_optNumber + " but got "
                  + inArguments.size());

    m_optionals.addAll(inArguments);

    return this;
  }

  //........................................................................

  //------------------------------ transform -------------------------------

  /**
   * Transform the arguments of this command.
   *
   * @param       inTransformer the transformer to use
   *
   */
  // TODO:
//   public Command transform(Transformer inTransformer)
//   {
//     if(inTransformer == null)
//       return this;

//     Command copy = this.clone();
//     copy.m_arguments = transform(inTransformer, m_arguments);
//     copy.m_optionals = transform(inTransformer, m_optionals);

//     return copy;
//   }

  //........................................................................
  //------------------------------ transform -------------------------------

  /**
   * Transform the given list of arguments.
   *
   * @param       inTransformer the transformer to use
   * @param       inArguments   the arguments to transform
   *
   * @return      the list of transformed arguments
   *
   */
  // TODO:
//   @MayReturnNull
//   private java.util.List<Object> transform
//    (Transformer inTransformer, @MayBeNull java.util.List<Object> inArguments)
//   {
//     if(inArguments == null)
//       return null;

//     java.util.List<Object> result = new ArrayList<Object>();

//     for(Object argument : inArguments)
//     {
//       argument = inTransformer.transform(argument);

//       if(argument == null)
//         continue;

//       if(argument instanceof TempGroup)
//       {
//         if(((Command)argument).m_arguments != null)
//           result.addAll(((Command)argument).m_arguments);
//       }
//       else
//         result.add(argument);
//     }

//     return result;
//   }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //----------------------------- markBrackets -----------------------------

  /**
   * Mark (and remove) the brackets given as arguments in the given string.
   * Brackets are marked with a number denoting their nesting level, i.e.
   * with marker<0> for nesting level 0.
   *
   * @param       inText        the text to replace in
   * @param       inEscape      the escape character to denote brackets to
   *                            ignore
   * @param       inStart       the start bracket character
   * @param       inEnd         the end bracket character
   * @param       inMarkerStart the character to use as marker start (will be
   *                            followed by the nesting leven in pointy
   *                            brackets)
   * @param       inMarkerEnd   the character to use as marker end (will be
   *                            followed by the nesting leven in pointy
   *                            brackets)
   *
   * @return      the text with all brackets replaced
   *
   */
  protected static @Nonnull String markBrackets(@Nonnull String inText,
                                                char inEscape,
                                                char inStart, char inEnd,
                                                char inMarkerStart,
                                                char inMarkerEnd)
  {
    assert inStart != '<' : "cannot replace '<' brackets";
    assert inEnd != '>' : "cannot replace '>' brackets";

    // remove all escaped markers
    inText = inText.replaceAll("\\" + inEscape + "\\" + inStart,
                               "" + s_markStart);
    inText = inText.replaceAll("\\" + inEscape + "\\" + inEnd, "" + s_markEnd);

    // we mark all bracket markers for easier replacement
    Pattern pattern = Pattern.compile("\\" + inStart + "([^\\" + inStart
                                      + "\\" + inEnd + "]*?)\\" + inEnd,
                                      Pattern.DOTALL);

    int i = 0;
    for(Matcher matcher = pattern.matcher(inText); matcher.find(0);
        matcher = pattern.matcher(inText))
      // replace the nested brackets
      inText = matcher.replaceAll(inMarkerStart + "<#" + i + "#>$1"
                                  + inMarkerEnd + "<#" + i++ + "#>");

    // 'invert' the number to make sure that really the nesting level starts
    // at 0 on the outside not on the inside
    // with the above, {{}} is {<1>{<0>}<0>}<1> instead of {<0>{<1>}<1>}<0>
    pattern = Pattern.compile(inMarkerStart + "<#(\\d+)#>(.*?)"
                              + inMarkerEnd + "<#\\1#>", Pattern.DOTALL);

    i = 0;
    for(Matcher matcher = pattern.matcher(inText);
        matcher.find(0);
        matcher = pattern.matcher(inText))
      // replace the nested brackets
      inText = matcher.replaceAll(inMarkerStart + "<" + i + ">$2"
                                  + inMarkerEnd + "<" + i++ + ">");

    // replace all non 0 markers (we can't leave nested markers or parsing
    // of multiple arguments may fail
    inText = inText.replaceAll(inMarkerStart + "<[1-9]\\d*>", "" + inStart);
    inText = inText.replaceAll(inMarkerEnd   + "<[1-9]\\d*>", "" + inEnd);

    // replace removed escaped markers
    inText = inText.replaceAll("" + s_markStart, "\\" + inEscape + inStart);
    inText = inText.replaceAll("" + s_markEnd, "\\" + inEscape + inEnd);

    return inText;
  }

  //........................................................................
  //-------------------------------- clean ---------------------------------

  /**
   * Remove all the markers from the text and replace the brackets back.
   *
   * @param       inText the text to replace the markers
   *
   * @return      the replaced text
   *
   */
  protected static @Nonnull String clean(@Nonnull String inText)
  {
    inText = inText.replaceAll(s_markArgStart + "<\\d+>", "" + s_argStart);
    inText = inText.replaceAll(s_markArgEnd + "<\\d+>", "" + s_argEnd);
    inText = inText.replaceAll(s_markOptArgStart + "<\\d+>",
                               "" + s_optArgStart);
    inText = inText.replaceAll(s_markOptArgEnd + "<\\d+>", "" + s_optArgEnd);

    return inText;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- text -----------------------------------------------------------

    /** Test text handling. */
    @org.junit.Test
    public void text()
    {
      String input = "just some test";
      Command command = new Command(BaseCommand.parse(input));
      Command expected = new Command(input);

      assertEquals("objects", expected, command);
      assertEquals("string", input, command.toString());

      input = "some \\\\pseudo command";
      command = new Command(BaseCommand.parse(input));
      expected = new Command(input);

      assertEquals("objects", expected, command);
      assertEquals("string", input, command.toString());

      input = "\\\\some \\\\pseudo command";
      command = new Command(BaseCommand.parse(input));
      expected = new Command(input);

      assertEquals("objects", expected, command);
      assertEquals("string", input, command.toString());

      command = new BaseCommand("name", 1, 2, "first", "second")
        .withOptionals("optional");

      assertEquals("constructor", "\\name[optional]{first}{second}",
                   command.toString());
      assertFalse("empty", command.isEmpty());

      command = new BaseCommand("name", 0, 0);
      assertTrue("empty", command.isEmpty());

      command = new BaseCommand("name", 0, 0, new Object [0])
        .withOptionals(new Object [0]);
      assertTrue("empty", command.isEmpty());

      command = new BaseCommand("name", 0, 1, "test");
      assertFalse("empty", command.isEmpty());

      command =
        new BaseCommand("name", 0, 2, "",
                        new BaseCommand("name", 1, 1, "").withOptionals(""));
       assertTrue("empty", command.isEmpty());
    }

    //......................................................................
    //----- commands -------------------------------------------------------

    /** Testing commands. */
    //@org.junit.Test
    public void commands()
    {
      String input = "some \\command to parse";
      Command command = new Command(BaseCommand.parse(input));
      Command expected =
        new Command("some", new BaseCommand("baseCommand", 0, 0), " to parse");

      assertEquals("objects", expected, command);
      assertEquals("string", input, command.toString());

      input = "\\starting command";
      command = new Command(BaseCommand.parse(input));
      expected = new Command(new BaseCommand("starting", 0, 0), " command");

      assertEquals("objects", expected, command);
      assertEquals("string", input, command.toString());

      m_logger.addExpected("WARNING: could not load command 'starting' "
                           + "[Starting]: java.lang.ClassNotFoundException: "
                           + "net.ixitxachitls.output.commands.Starting");

      // single command      input = "\\command";
      command = new Command(BaseCommand.parse(input));
      expected = new BaseCommand("baseCommand", 0, 0);

      assertEquals("objects", expected, command);
      assertEquals("string", input, command.toString());
    }

    //......................................................................
    //----- arguments ------------------------------------------------------

    /** Testing arguments. */
    @org.junit.Test
    public void arguments()
    {
      String input = "some \\baseCommand{with}{arguments} to parse";
      Command command = new Command(BaseCommand.parse(input));
      Command expected =
        new Command("some ",
                    new BaseCommand("baseCommand", 0, 2)
                    .withArguments("with", "arguments"),
                    " to parse");

      assertEquals("obects", expected, command);
      assertEquals("string", input, command.toString());

      // more complicated example
      input = "some \\baseCommand{with}{multiple \\{complicated\\}"
        + " arguments}and some {arguments} to parse";
      command = new Command(BaseCommand.parse(input));
      expected =
        new Command("some ",
                    new BaseCommand("baseCommand", 0, 2)
                    .withArguments("with",
                                   "multiple \\{complicated\\} arguments"),
                    "and some {arguments} to parse");

      assertEquals("obects", expected, command);
      assertEquals("string", input, command.toString());

      // nested command
      input = "some \\baseCommand{with}{nested \\baseCommand{with}{arguments}}"
        + "and some {arguments} to parse";
      command = new Command(BaseCommand.parse(input));
      expected =
        new Command("some ",
                    new BaseCommand("baseCommand", 0, 2, "with",
                                    new BaseCommand()
                                    .withArguments("nested ",
                                                   new BaseCommand
                                                   ("baseCommand", 0, 2,
                                                    "with", "arguments"))),
         "and some {arguments} to parse");

      assertEquals("obects", expected, command);
      assertEquals("string", input, command.toString());
    }

    //......................................................................
    //----- optionals ------------------------------------------------------

    /** Testing optional arguments. */
    @org.junit.Test
    public void optionals()
    {
      String input = "some \\baseCommand[with][some]{arguments}!!";
      Command command = new Command(BaseCommand.parse(input));
      Command expected =
        new Command("some ",
                    new BaseCommand("baseCommand", 2, 1, "arguments")
                    .withOptionals("with", "some"),
                    "!!");

      assertEquals("objects", expected, command);
      assertEquals("string", input, command.toString());

      // more complicated example
      input = "some \\baseCommand[with][multiple \\{complicated\\} "
        + "arguments]and some {arguments} to parse";
      command = new Command(BaseCommand.parse(input));
      expected =
        new Command("some ",
                     new BaseCommand("baseCommand", 2, 0)
                     .withOptionals("with",
                                    "multiple \\{complicated\\} arguments"),
                    "and some {arguments} to parse");

      assertEquals("objects", expected, command);
      assertEquals("string", input, command.toString());

      // nested command
      input = "some \\baseCommand[with]  [nested "
        + "\\baseCommand[with]{arguments}]and some {arguments} to parse";
      command = new Command(BaseCommand.parse(input));
      expected =
        new Command("some ",
                    new BaseCommand("baseCommand", 2, 0).withOptionals
                    ("with",
                     new BaseCommand()
                     .withArguments("nested ",
                                    new BaseCommand("baseCommand", 1, 1,
                                                    "arguments")
                                    .withOptionals("with"))),
                    "and some {arguments} to parse");

      assertEquals("objects", expected, command);
      assertEquals("string",
                   "some \\baseCommand[with][nested "
                   + "\\baseCommand[with]{arguments}]and some {arguments} to "
                   + "parse", command.toString());
    }

    //......................................................................
    //----- mark -----------------------------------------------------------

    /** Testing marking of passages. */
    @org.junit.Test
    public void mark()
    {
      assertEquals("simple", "\001<0>abc\002<0>",
                   markBrackets("{abc}", '\\', '{', '}', '\001', '\002'));
      assertEquals("multiple", "\001<0>abc\002<0>  \001<0>\002<0>",
                   markBrackets("{abc}  {}", '\\', '{', '}', '\001', '\002'));
      assertEquals("nested",
                   "\001<0>a{b{}}{{}}c\002<0>\001<0>\002<0>",
                   markBrackets("{a{b{}}{{}}c}{}", '\\', '{', '}', '\001',
                                '\002'));
      assertEquals("escaped", "\001<0>a\\{b\\}c\002<0>",
                   markBrackets("{a\\{b\\}c}", '\\', '{', '}', '\001',
                                '\002'));
      assertEquals("incomplete", "{a\001<0>b\002<0>{",
                   markBrackets("{a{b}{", '\\', '{', '}', '\001', '\002'));
    }

    //......................................................................
    //----- multiple -------------------------------------------------------

    /** Testing of multiple, nested commands. */
    @org.junit.Test
    public void multiple()
    {
      String input =
        "\\baseCommand{a\\baseCommand{ff}}{b\\baseCommand{g}} some text "
        + "\\baseCommand{c}{d}{e}";

      Command command = new Command(BaseCommand.parse(input));
      Command expected =
        new Command(new BaseCommand("baseCommand", 0, 2,
                                    new BaseCommand().withArguments
                                    ("a", new BaseCommand("baseCommand", 0, 1)
                                     .withArguments("ff")),
                                    new BaseCommand().withArguments
                                    ("b", new BaseCommand("baseCommand", 0, 1)
                                     .withArguments("g"))),
                    " some text ",
                    new BaseCommand("baseCommand", 0, 3)
                    .withArguments("c", "d", "e"));

      assertEquals("objects", expected, command);
      assertEquals("string", input, command.toString());
    }

    //......................................................................
  }

  //........................................................................
}
