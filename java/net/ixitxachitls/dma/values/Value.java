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

import com.google.common.base.Optional;
import com.google.protobuf.Message;

/**
 * Base class for values.
 *
 * @file   NewValue.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param <T> the proto message this value can be converted into/from
 */
public abstract class Value<T extends Message>
{
  /**
   * A value that supports arithmetic computations.
   *
   * @param <V> the type of proto message this value converts into/from
   */
  public static abstract class Arithmetic<V extends Message> extends Value<V>
  {
    /**
     * Add another arithmetic value to this one.
     *
     * @param inValue the value to add
     * @return a new value representing the addtion
     */
    public abstract Arithmetic<V> add(Arithmetic<V> inValue);

    /**
     * Check whether the given value can be added to this one.
     *
     * @param inValue the value to check for
     * @return true if the value can be added, false if not
     */
    public abstract boolean canAdd(Arithmetic<V> inValue);

    /**
     * Multiply the current value by the given factor.
     *
     * @param inFactor the factor to multiply with
     * @return a new value representing the multiplied value
     */
    public abstract Arithmetic<V> multiply(int inFactor);
  }

  /** A parser for parsing integer values. */
  public static final Parser<Integer> INTEGER_PARSER = new Parser<Integer>(1)
  {
    @Override
    public Optional<Integer> doParse(String inValue)
    {
      try
      {
        return Optional.of(Integer.parseInt(inValue));
      }
      catch(NumberFormatException e)
      {
        return Optional.absent();
      }
    }
  };

  /** A parser for parsing boolean values. */
  public static final Parser<Boolean> BOOLEAN_PARSER = new Parser<Boolean>(1)
  {
    @Override
    public Optional<Boolean> doParse(String inValue)
    {
      return Optional.of(Boolean.parseBoolean(inValue));
    }
  };

  /**
   * Convert the value to a short string.
   *
   * @return the short textual representation of this value
   */
  public String toShortString()
  {
    return toString();
  }

  /**
   * Convert the value to a proto message.
   *
   * @return the converted proto message
   */
  public abstract T toProto();

  /**
   * Group the value into a bucket.
   *
   * @return the name of the bucket grouped into.
   */
  public String group()
  {
    return toString();
  }

  /**
   * A utility method for adding two rational values.
   *
   * @param inFirst the first value to add
   * @param inSecond the second value to add
   * @return the addition of the two values
   */
  protected static Optional<Rational> add(Optional<Rational> inFirst,
                                          Optional<Rational> inSecond)
  {
    if(!inFirst.isPresent())
      return inSecond;
    if(!inSecond.isPresent())
      return inFirst;

    return Optional.of((Rational)inFirst.get().add(inSecond.get()));
  }

  /** A utility method to multiply a rational with a factor.
   *
   * @param inValue the value to multiply
   * @param inFactor the multiplication factor
   * @return the multiplied value
   */
  protected static Optional<Rational> multiply(Optional<Rational> inValue,
                                               int inFactor)
  {
    if(!inValue.isPresent())
      return inValue;

    return Optional.of((Rational)inValue.get().multiply(inFactor));
  }
}
