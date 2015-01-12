/******************************************************************************
 * Copyright (c) 2002-2013 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.appengine.repackaged.com.google.common.collect.ImmutableMap;
import com.google.common.base.Optional;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.proto.Values.ExpressionProto;
import net.ixitxachitls.dma.proto.Values.ExpressionProto.Literal;
import net.ixitxachitls.dma.values.enums.Operator;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.input.ReadException;
import net.ixitxachitls.util.Strings;


/**
 * A value representing an expression.
 *
 * @file   ExpressionValue.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param <T> the type of value expressed
 */
public class ExpressionValue<T> extends Value
{
  /**
   * Create an expression value.
   *
   * @param inValue the value to create with
   */
  public ExpressionValue(T inValue)
  {
    m_value = Optional.of(inValue);
    m_variable = Optional.absent();
    m_integer = 0;
    m_operator = Operator.NONE;
  }

  /**
   * Create an expression value with a variable name.
   *
   * @param inVariable the name of the variable holding the value
   */
  public ExpressionValue(String inVariable)
  {
    m_value = Optional.absent();
    m_variable = Optional.of(inVariable);
    m_integer = 0;
    m_operator = Operator.NONE;
  }

  /**
   * Create a terminal expression value with a number.
   *
   * @param inValue the number
   */
  public ExpressionValue(int inValue)
  {
    m_value = Optional.absent();
    m_variable = Optional.absent();
    m_integer = inValue;
    m_operator = Operator.NONE;
  }

  /**
   * Create an expression value with an operator and arguments.
   *
   * @param inOperator the operator
   * @param inValues the arguments
   */
  @SafeVarargs
  public ExpressionValue(Operator inOperator, ExpressionValue<T> ... inValues)
  {
    m_value = Optional.absent();
    m_variable = Optional.absent();
    m_integer = 0;
    m_operator = inOperator;
    m_operands.addAll(Arrays.asList(inValues));
  }

  /**
   * Create an expression value with an operator and arguments.
   *
   * @param inOperator the operator
   * @param inValues the list of arguments
   */
  public ExpressionValue(Operator inOperator, List<ExpressionValue<T>> inValues)
  {
    m_value = Optional.absent();
    m_variable = Optional.absent();
    m_integer = 0;
    m_operator = inOperator;
    m_operands.addAll(inValues);
  }

  /** Create an expression with another one. This is mainly used for brackets.
   *
   * @param inValue the other value
   */
  public ExpressionValue(ExpressionValue<T> inValue)
  {
    m_value = Optional.absent();
    m_variable = Optional.absent();
    m_integer = 0;
    m_operator = Operator.NONE;
    m_operands.add(inValue);
  }

  /** The terminal value, if any. */
  private final Optional<T> m_value;

  /** The variable holding a value, if any. */
  private final Optional<String> m_variable;

  /** A terminal number. */
  private final int m_integer;

  /** The operator for an operating expression. NONE for no operation. */
  private final Operator m_operator;

  /** The arguments to the operator. */
  private final List<ExpressionValue<T>> m_operands = new ArrayList<>();

  /**
   * The parser for expressions.
   *
   * @param inValueParser the parser for the basic values
   * @param <V> the type of basic values
   * @return a parser for expressions of the denoted values
   */
  public static <V> Parser<ExpressionValue<V>>
    parser(final Parser<V> inValueParser)
  {
    return new Parser<ExpressionValue<V>>(1) {
      @Override
      public Optional<ExpressionValue<V>> doParse(String inValue)
      {
        Optional<V> value = inValueParser.parse(inValue);
        if(value.isPresent())
          return Optional.of(new ExpressionValue<V>(value.get()));

        ParseReader reader =
          new ParseReader(new StringReader(inValue), "expression");
        Optional<ExpressionValue<V>> parsed =
          ExpressionValue.parse(reader);
        try
        {
          reader.readChar();
          return Optional.absent();
        }
        catch(ReadException e)
        {
          // just ignored
        }

        return parsed;
      }
    };
  }

  /**
   * Parse an expression.
   *
   * @param inReader the reader to read values from
   * @param <V> the typeo of values parsed
   * @return the expression value parsed, if any
   */
  private static <V> Optional<ExpressionValue<V>> parse(ParseReader inReader)
  {
    try
    {
      Optional<ExpressionValue<V>> first = Optional.absent();
      if(inReader.expect("$"))
        first = Optional.of(new ExpressionValue<V>(inReader.readWord()));
      else if(inReader.expect('('))
      {
        first = parse(inReader);
        if(!inReader.expect(')'))
          return Optional.absent();
        if(first.isPresent())
          first = Optional.of(new ExpressionValue<V>(first.get()));
      }
      else
      {
        String operator = inReader.expect(Operator.prefixed().iterator());
        if(operator != null)
        {
          if(!inReader.expect('('))
            return Optional.absent();

          List<ExpressionValue<V>> operands = new ArrayList<>();
          do
          {
            Optional<ExpressionValue<V>> operand = parse(inReader);
            if(operand.isPresent())
              operands.add(operand.get());
          } while(inReader.expect(','));

          if(!inReader.expect(')'))
            return Optional.absent();

          first = Optional.of(new ExpressionValue<V>
            (Operator.fromString(operator).get(), operands));
        }
        else
          first = Optional.of(new ExpressionValue<V>(inReader.readInt()));
      }

      if(inReader.isAtEnd())
        return first;

      String operator = inReader.expect(Operator.infixed().iterator());
      if (operator == null)
        return first;

      Optional<ExpressionValue<V>> second = parse(inReader);

      if(first.isPresent() && second.isPresent())
        return Optional.of(new ExpressionValue<V>
                           (Operator.fromString(operator).get(),
                            first.get(), second.get()));

      return Optional.absent();
    }
    catch(ReadException exception)
    {
      return Optional.absent();
    }
  }

  /**
   * Check whether theere is a value defined.
   *
   * @return true for a value, false for none
   */
  public boolean hasValue()
  {
    return m_value.isPresent();
  }

  /**
   * Get the value of the expression.
   *
   * @return the value
   */
  public Optional<T> getValue()
  {
    return m_value;
  }

  /**
   * Get the value given the map of parameters and the parser.
   *
   * @param inParameters the parameters defining variable values
   * @param inParser the parser for values
   * @return the value parsed, if any
   */
  @SuppressWarnings("unchecked")
  public Optional<T> getValue(Map<String, String> inParameters,
                              Parser<T> inParser)
  {
    Optional<? extends Object> value = evaluate(inParameters, inParser);
    if(!value.isPresent())
      return Optional.absent();

    return (Optional<T>)value;
  }

  /**
   * Check whether the given value represents an integer.
   *
   * @param inValue the value to check
   * @return true for integer, false if not
   */
  private boolean isInteger(Optional<? extends Object> inValue)
  {
    return inValue.isPresent() && inValue.get() instanceof Integer;
  }

  /**
   * Check whether the given value is Arithmetic.
   *
   * @param inValue the value to check
   * @return true if the value is arithmetic, false if not
   */
  private boolean isArithmetic(Optional<? extends Object> inValue)
  {
    return inValue.isPresent()
      && inValue.get() instanceof Value.Arithmetic<?>;
  }

  /**
   * Evaluate the expression given the variable values.
   *
   * @param inParameters the values of variables
   * @param inParser the parser for values
   * @return the value evaluated or absent if none could be evaluate
   */
  @SuppressWarnings("unchecked")
  private Optional<? extends Object> evaluate(Map<String, String> inParameters,
                                              Parser<T> inParser)
  {
    if(m_variable.isPresent())
    {
      if(!inParameters.containsKey(m_variable.get()))
        return Optional.absent();

      String unparsed = inParameters.get(m_variable.get());
      Optional<T> value = inParser.parse(unparsed);
      if(value.isPresent())
        return value;

      try
      {
        return Optional.of(Integer.parseInt(unparsed));
      }
      catch(NumberFormatException e)
      {
        return Optional.absent();
      }
    }

    List<Optional<? extends Object>> operands = new ArrayList<>();
    for(ExpressionValue<T> operand : m_operands)
    {
      Optional<? extends Object> value =
        operand.evaluate(inParameters, inParser);
      if(!value.isPresent())
        return Optional.absent();
      operands.add(value);
    }

    switch(m_operator)
    {
      case NONE:
        if(operands.isEmpty())
          return Optional.of(m_integer);

        return operands.get(0);

      case ADD:
        if(operands.size() != 2)
          return Optional.absent();

        if(isInteger(operands.get(0)) && isInteger(operands.get(1)))
          return Optional.of((Integer) operands.get(0).get()
                             + (Integer) operands.get(1).get());
        if(isArithmetic(operands.get(0)) && isArithmetic(operands.get(1)))
          return Optional.of
            (((Value.Arithmetic<Message>) operands.get(0).get())
             .add((Value.Arithmetic<Message>) operands.get(1).get()));

        return Optional.absent();

      case SUBTRACT:
        if(operands.size() != 2)
          return Optional.absent();

        if(isInteger(operands.get(0)) && isInteger(operands.get(1)))
          return Optional.of((Integer) operands.get(0).get()
                             - (Integer) operands.get(1).get());

        return Optional.absent();

      case MULTIPLY:
        if(operands.size() != 2)
          return Optional.absent();

        if(isInteger(operands.get(0)) && isInteger(operands.get(1)))
          return Optional.of((Integer) operands.get(0).get()
                             * (Integer) operands.get(1).get());
        if(isArithmetic(operands.get(0)) && isInteger(operands.get(1)))
          return Optional.of
            (((Value.Arithmetic<Message>) operands.get(0).get())
             .multiply((Integer) operands.get(1).get()));
        if(isArithmetic(operands.get(1)) && isInteger(operands.get(0)))
          return Optional.of
            (((Value.Arithmetic<Message>) operands.get(1).get())
             .multiply((Integer) operands.get(0).get()));

        return Optional.absent();

      case DIVIDE:
        if(operands.size() != 2)
          return Optional.absent();

        if(isInteger(operands.get(0)) && isInteger(operands.get(1)))
          return Optional.of((Integer) operands.get(0).get()
                             / (Integer) operands.get(1).get());

        return Optional.absent();

      case MAX:
        Integer max = null;
        for(Optional<? extends Object> operand : operands)
          if(isInteger(operand) && max == null || max < (Integer) operand.get())
            max = (Integer) operand.get();

        return Optional.fromNullable(max);

      case MIN:
        Integer min = null;
        for(Optional<? extends Object> operand : operands)
          if(isInteger(operand) && min == null || min > (Integer) operand.get())
            min = (Integer) operand.get();

        return Optional.fromNullable(min);

      case MODULO:
        if(operands.size() != 2)
          return Optional.absent();

        if(isInteger(operands.get(0)) && isInteger(operands.get(1)))
          return Optional.of((Integer) operands.get(0).get()
                             % (Integer) operands.get(1).get());

        return Optional.absent();

      default:
        return Optional.absent();
    }
  }

  /**
   * Convert the expression to a proto.
   *
   * @return the proto representation
   */
  @Override
  public ExpressionProto toProto()
  {
    ExpressionProto.Builder builder = ExpressionProto.newBuilder();

    if(m_value.isPresent())
      return builder.build();

    if(m_variable.isPresent())
      builder.setLiteral(Literal.newBuilder().setVariable(m_variable.get())
                         .build());
    else if(m_operator == Operator.NONE && m_operands.isEmpty())
      builder.setLiteral(Literal.newBuilder().setInteger(m_integer));

    for(ExpressionValue<T> operand : m_operands)
      builder.addOperand(operand.toProto());

    return builder.build();
  }

  /**
   * Convert the proto to an expression value.
   *
   * @param inProto the proto to convert
   * @param <V> the value represented by the expression
   * @return the expression
   */
  @SuppressWarnings("unchecked")
  public static <V extends Value> ExpressionValue<V>
    fromProto(ExpressionProto inProto)
  {
    if(inProto.hasLiteral())
      if(inProto.getLiteral().hasVariable())
        return new ExpressionValue<V>(inProto.getLiteral().getVariable());
      else
        return new ExpressionValue<V>(inProto.getLiteral().getInteger());

    List<ExpressionValue<V>> operands = new ArrayList<>();
    for(ExpressionProto operand : inProto.getOperandList())
      operands.add((ExpressionValue<V>) fromProto(operand));

    return new ExpressionValue<V>(Operator.fromProto(inProto.getOperator()),
                                  operands);
  }

  @Override
  public String toString()
  {
    if(m_value.isPresent())
      return m_value.get().toString();

    if(m_operator == Operator.NONE)
      if(m_operands.size() == 1)
        return "(" + m_operands.get(0) + ")";
      else if(m_variable.isPresent())
        return "$" + m_variable.get();
      else
        return "" + m_integer;

    if(m_operator.isPrefixed())
      return m_operator.getMarkup() + "("
        + Strings.COMMA_JOINER.join(m_operands) + ")";

    if(m_operands.size() != 2)
      return "<invalid number of operands for infix operator>";

    return m_operands.get(0) + " " + m_operator.getMarkup() + " "
      + m_operands.get(1);
  }

  //---------------------------------------------------------------------------

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Parsing test. */
    @org.junit.Test
    public void parse()
    {
      assertEquals("parse", Optional.absent(), parser(Speed.PARSER).parse(""));
      assertEquals("parse", "$guru",
                   parser(Speed.PARSER).parse("  $guru  ").get().toString());
      assertEquals("parse", "42",
                   parser(Speed.PARSER).parse("  42  ").get().toString());
      assertEquals("parse", "(42)",
                   parser(Speed.PARSER).parse(" ( 42 ) ").get().toString());
      assertEquals("parse", "((42))",
                   parser(Speed.PARSER).parse("( ( 42 )) ").get().toString());
      assertEquals("parse", "($guru)",
                   parser(Speed.PARSER).parse("(  $guru)  ").get().toString());
      assertEquals("parse", "(min(23, 42, $guru))",
                   parser(Speed.PARSER).parse("(  min( 23,  42,$guru) ) ").get()
                     .toString());
      assertEquals("parse", "42 + $guru",
                   parser(Speed.PARSER).parse(" 42 + $guru ").get().toString());
      assertEquals("parse", "42 + ($guru / 2)",
                   parser(Speed.PARSER).parse(" 42 + ($guru/2)").get()
                     .toString());
      assertEquals("parse", "10 ft",
                   parser(Speed.PARSER).parse(" 10 ft").get()
                     .toString());
      assertEquals("parse", "Fly 10 ft (Perfect)",
                   parser(Speed.PARSER).parse(" fly 10 ft (perfect)").get()
                     .toString());
    }

    /** Evaluatio test. */
    @org.junit.Test
    public void evaluate()
    {
      assertEquals("evaluate", "42",
                   parser(Value.INTEGER_PARSER).parse("$guru").get()
                     .getValue(map("guru", "42"), Value.INTEGER_PARSER)
                     .get().toString());
      assertEquals("evaluate", "10 ft",
                   parser(Speed.PARSER).parse("$guru").get()
                     .getValue(map("guru", "10 ft"), Speed.PARSER)
                     .get().toString());

      assertEquals("evaluate", "65",
                   parser(Value.INTEGER_PARSER).parse("42 + $guru").get()
                     .getValue(map("guru", "23"), Value.INTEGER_PARSER)
                     .get().toString());
      assertEquals("evaluate", "29",
                   parser(Value.INTEGER_PARSER).parse("(42 + $guru * 2)/3")
                     .get().getValue(map("guru", "23"), Value.INTEGER_PARSER)
                     .get().toString());
      assertEquals("evaluate", "46",
                   parser(Value.INTEGER_PARSER).parse("max(2, 3, $guru) * 2")
                     .get().getValue(map("guru", "23"), Value.INTEGER_PARSER)
                     .get().toString());

      assertEquals("evaluate", "15 ft",
                   parser(Speed.PARSER).parse("$guru + $gugus").get()
                     .getValue(map("guru", "10 ft", "gugus", "5 ft"),
                               Speed.PARSER)
                     .get().toString());
      assertEquals("evaluate", "30 ft",
                   parser(Speed.PARSER).parse("2 * ($guru + $gugus)").get()
                     .getValue(map("guru", "10 ft", "gugus", "5 ft"),
                               Speed.PARSER)
                     .get().toString());
    }

    /**
     * Create a map out of string pairs.
     *
     * @param inValues the values paris to create the map
     * @return the created map
     */
    private Map<String, String> map(String ... inValues)
    {
      ImmutableMap.Builder<String, String> map = ImmutableMap.builder();
      for(int i = 0; i < inValues.length; i += 2)
        map.put(inValues[i], inValues[i + 1]);

      return map.build();
    }
  }
}
