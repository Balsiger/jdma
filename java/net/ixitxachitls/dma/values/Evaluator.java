/*******************************************************************************
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
 ******************************************************************************/

package net.ixitxachitls.dma.values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ixitxachitls.util.logging.Log;

/**
 * Evaluates values and expressions in entity values.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Evaluator
{
  public Evaluator(Map<String, ?> inValues)
  {
    m_values = inValues;
  }

  /** The pattern to replace values in expressions. */
  private static final Pattern PATTERN_VAR =
      Pattern.compile("\\$(\\w+)");

  /** The pattern for expressions. */
  protected static final Pattern PATTERN_EXPR =
      Pattern.compile("\\[\\[(.*?)\\]\\]");

  private final Map<String, ?> m_values;

  public String evaluate(String inText)
  {
    return evaluateExpressions(replaceVariables(inText));
  }

  private String replaceVariables(String inText)
  {
    StringBuffer result = new StringBuffer();

    Matcher matcher = PATTERN_VAR.matcher(inText);
    while(matcher.find())
    {
      String value = m_values.get(matcher.group(1)).toString();
      if(value != null)
        matcher.appendReplacement(result, value.replace('$', '_'));
      else
      {
        Log.warning("Cannot find value " + matcher.group(1) + " to replace in"
                        + inText);
        matcher.appendReplacement(result,
                                  "\\\\color{error}{&#x24;" + matcher.group(1)
                                      + "}");
      }
    }

    matcher.appendTail(result);

    return result.toString();
  }

  private String evaluateExpressions(String inText)
  {
    StringBuffer result = new StringBuffer();

    Matcher matcher = PATTERN_EXPR.matcher(inText);
    while(matcher.find())
      matcher.appendReplacement(result,
                                evaluateExpression(matcher.group(1))
                                    .replace("\\", "\\\\"));

    matcher.appendTail(result);

    return result.toString();
  }

  private String evaluateExpression(String inExpression)
  {
    String expression = inExpression.replaceAll("[ \t\n\f\r]", "");

    StringTokenizer tokens =
        new StringTokenizer(expression, "()+-*/,^", true);

    return evaluateExpression(expression, tokens).replaceAll("_", " ");
  }

  private String evaluateExpression(String inExpression,
                                    StringTokenizer inTokens)
  {
    if(!inTokens.hasMoreTokens())
    {
      Log.warning("invalid expression, expected more: " + inExpression);
      return "* invalid expression, expected (: " + inExpression + " *";
    }

    String token = inTokens.nextToken();
    switch(token)
    {
      case "min":
        return evaluateMinExpression(inExpression, inTokens);

      case "max":
        return evaluateMaxExpression(inExpression, inTokens);

      case "range":
        return evaluateRangeExpression(inExpression, inTokens);

      case "switch":
        return evaluateSwitchExpression(inExpression, inTokens);

      default:
        return evaluateOperatorExpression(token, inExpression, inTokens);
    }
  }

  private String evaluateMinExpression(String inExpression,
                                       StringTokenizer inTokens)
  {
    // Expects an expression like min(<int>, <int>), where <int> is an
    // expression that can be parsed to an integer.
    if(!"(".equals(inTokens.nextToken()))
    {
      Log.warning("invalid expression, expected '(': " + inExpression);
      return "* invalid expression, expected (: " + inExpression + " *";
    }

    String first = evaluateExpression(inExpression, inTokens);
    String second = evaluateExpression(inExpression, inTokens);

    try
    {
      return "" + Math.min(Integer.parseInt(first), Integer.parseInt(second));
    }
    catch(NumberFormatException e)
    {
      Log.warning("invalid expression for min, expected numbers: "
                      + first + " / " + second);
      return "* invalid expression for min, expected numbers: "
          + first + " / " + second;
    }
  }

  private String evaluateMaxExpression(String inExpression,
                                       StringTokenizer inTokens)
  {
    // Expects an expression like max(<int>, <int>), where <int> is an
    // expression that can be parsed to an integer.
    if(!"(".equals(inTokens.nextToken()))
    {
      Log.warning("invalid expression, expected '(': " + inExpression);
      return "* invalid expression, expect (: " + inExpression + " *";
    }

    String first = evaluateExpression(inExpression, inTokens);
    String second = evaluateExpression(inExpression, inTokens);

    try
    {
      return "" + Math.max(Integer.parseInt(first), Integer.parseInt(second));
    }
    catch(NumberFormatException e)
    {
      Log.warning("invalid expression for max, expected numbers: "
                      + first + " / " + second);
      return "* invalid expression for max, expected numbers: "
          + first + " / " + second;
    }
  }

  private String evaluateRangeExpression(String inExpression,
                                         StringTokenizer inTokens)
  {
    // Expects an expression of the form
    // range(<int>, <int>: <expr>, <int>: <expr> ...),
    // where int evaluates to an integer and expr evaluates to any string;
    // The expression is returned that corresponds to the lowest listed
    // integer that is equal or above the first number; number given have to
    // be in ascending order.
    if(!"(".equals(inTokens.nextToken()))
    {
      Log.warning("invalid expression, expected '(': " + inExpression);
      return "* invalid expression, expect (: " + inExpression + " *";
    }

    int selected;
    try
    {
      selected = Integer.parseInt(evaluateExpression(inExpression, inTokens));
    }
    catch(NumberFormatException e)
    {
      return "* invalid expression, expected number: " + inExpression + " *";
    }

    List<String> ranges = new ArrayList<>();

    String current = "";
    for(String argument = inTokens.nextToken();
        !"(".equals(argument) && inTokens.hasMoreTokens();
        argument = inTokens.nextToken())
    {
      if(",".equals(argument))
      {
        ranges.add(current);
        current = "";
      }
      else
        current += argument;
    }

    ranges.add(current);
    Collections.reverse(ranges);

    String match = null;
    int matchValue = 0;
    for(String range : ranges)
    {
      String []parts = range.split(":\\s*");
      if(parts.length != 2)
        continue;

      try
      {
        int value = Integer.parseInt(parts[0]);
        if(selected >= value && value > matchValue)
        {
          match = parts[1];
          matchValue = value;
        }
      }
      catch(NumberFormatException e)
      {
        // just ignore it
      }
    }

    if (match == null)
      return "* invalid range *";

    return match;
  }

  private String evaluateSwitchExpression(String inExpression,
                                          StringTokenizer inTokens)
  {
    // Expects an expression of the form
    // switch(<expr>, <expr>: <expr>, <expr>: <expr>, ...),
    // where expr is any expression and the value returned is the expression
    // that either matches the first expression given or 'default'.
    if(!"(".equals(inTokens.nextToken()))
    {
      Log.warning("invalid expression, expected '(': " + inExpression);
      return "* invalid expression, expect (: " + inExpression + " *";
    }

    String value = evaluateExpression(inExpression, inTokens);
    List<String> options = new ArrayList<>();

    String current = "";
    for(String argument = inTokens.nextToken();
        !"(".equals(argument) && inTokens.hasMoreTokens();
        argument = inTokens.nextToken())
    {
      if(",".equals(argument))
      {
        options.add(current);
        current = "";
      }
      else
      {
        current += argument;
      }
    }
    options.add(current);

    for(String option : options)
    {
      String []parts = option.split(":\\s*");
      if(parts.length != 2)
        continue;

      String []cases = parts[0].split("\\|");
      for(String single : cases)
        if(single.trim().equalsIgnoreCase(value))
          return parts[1];
        else if("default".equalsIgnoreCase(single))
          return parts[1];
    }

    return "* invalid switch *";
  }

  private String evaluateOperatorExpression(String inToken, String inExpression,
                                            StringTokenizer inTokens)
  {
    try
    {
      String value;
      switch(inToken)
      {
        case "(":
          value = evaluateExpression(inExpression, inTokens);
          break;

        case "-":
          value = "-" + evaluateExpression(inExpression, inTokens);
          break;

        case "+":
          value = "+" + evaluateExpression(inExpression, inTokens);
          break;

        default:
          value = inToken;
      }

      if(!inTokens.hasMoreTokens())
        return value;

      String operator = inTokens.nextToken();

      if(",".equals(operator) || ")".equals(operator))
        return value;

      String operand = evaluateExpression(inExpression, inTokens);
      switch(operand)
      {
        case "+":
          return "" + (Integer.parseInt(value) + Integer.parseInt(operand));

        case "-":
          return "" + (Integer.parseInt(value) - Integer.parseInt(operand));

        case "*":
          return "" + (Integer.parseInt(value) * Integer.parseInt(operand));

        case "/":
          if(Integer.parseInt(value) == 0)
            return "0";
          else
            return "" + (Integer.parseInt(value) / Integer.parseInt(operand));

        case "^":
          return "" + (int)Math.pow(Integer.parseInt(value),
                                    Integer.parseInt(operand));
        default:
          Log.warning("invalid operator " + operator + ": " + inExpression);
          return value;
      }
    }
    catch(NumberFormatException e)
    {
      Log.warning(e + ", for " + inExpression);
      return "* invalid number *";
    }
  }
}
