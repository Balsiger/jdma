/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.values;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a area. This is a convenience class for Units.
 *
 * @file          Area.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Area extends Units<Area>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ Area --------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Construct the area object with an undefined value.
   *
   */
  public Area()
  {
    super(s_sets, 5);
  }

  //........................................................................
  //------------------------------ Area --------------------------------

  /**
   * Construct the area object.
   *
   * @param       inLarge  the large number (yards or km)
   * @param       inMiddle the middle number (feet or m)
   * @param       inSmall  the small number (inches or cm)
   * @param       inMetric a flag for metric values (feet if false)
   *
   */
  public Area(@Nullable Rational inLarge, @Nullable Rational inMiddle,
              @Nullable Rational inSmall,
              boolean inMetric)
  {
    super(new Rational [] { inLarge, inMiddle, inSmall }, s_sets,
          s_sets[inMetric ? 1 : 0], 5);
  }

  //........................................................................

  //-------------------------------- create --------------------------------

  /**
   * Create a new list with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   *
   */
  @Override
  public Area create()
  {
    return super.create(new Area());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The definition of areas. */
  private static final String s_definition =
    Config.get("/game.area",
               "1/1  : Feet =   9/1    : sq yd : square yard|square yards,"
               + "                1/1    : sq ft : square foot|square feet,"
               + "                1/144  : sq in : square inch|square inches."
               + "1/25 : Metric = 1/1    : sq m  : square meter|square meters,"
               + "                1/100  : sq dm : "
               + "square decimeter|square decimeters,"
               + "                1/10000 : sq cm : "
               + "square centimeter|square centimeters.");

  /** The sets with the possible units. */
  private static Set []s_sets = parseDefinition(s_definition);

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- isMetric -------------------------------

  /**
   * Determine if metric values are stored.
   *
   * @return      true if metric values are stored, false else
   *
   */
  public boolean isMetric()
  {
    return m_set == m_sets[1];
  }

  //........................................................................
  //------------------------------- isFeet ---------------------------------

  /**
   * Determine if feet values are stored.
   *
   * @return      true if feet values are stored, false else
   *
   */
  public boolean isFeet()
  {
    return m_set == s_sets[0];
  }

  //........................................................................

  //------------------------------ getAsFeet -------------------------------

  /**
   * Get the value as an approximate equivalent of inches.
   *
   * @return      the inches
   *
   */
  public Rational getAsFeet()
  {
    if(!isDefined())
      return new Rational(0);

    if(m_set == m_sets[0])
      return getAsBase();

    return toSet(0, false).getAsBase();
  }

  //........................................................................
  //----------------------------- getAsMeters ------------------------------

  /**
   * Get the value as an approximate equivalent of centimeters.
   *
   * @return      the inches
   *
   */
  public Rational getAsMeters()
  {
    if(!isDefined())
      return new Rational(0);

    if(m_set == m_sets[1])
      return getAsBase();

    return toSet(1, false).getAsBase();
  }

  //........................................................................

  //------------------------------- asMetric -------------------------------

  /**
   * Convert this value into a metric value.
   *
   * @return      the corresponding metric value.
   *
   */
  public Area asMetric()
  {
    if(m_set == m_sets[1])
      return this;

    return toSet(1, true);
  }

  //........................................................................
  //-------------------------------- asFeet --------------------------------

  /**
   * Convert this value into a feet value.
   *
   * @return      the corresponding feet value.
   *
   */
  public Area asFeet()
  {
    if(m_set == m_sets[0])
      return this;

    return toSet(0, true);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------ asMetric --------------------------------

  /**
   * Set the weight as metric value.
   *
   * @param       inSquareMeters      the number of m2
   * @param       inSquareDecimeters  the number of dm2
   * @param       inSquareCentimeters the number of cm2
   *
   * @return      the value as metric
   *
   */
  public Area asMetric(@Nullable Rational inSquareMeters,
                       @Nullable Rational inSquareDecimeters,
                       @Nullable Rational inSquareCentimeters)
  {
    return as(new Rational [] { inSquareMeters, inSquareDecimeters,
                                inSquareCentimeters }, 1);
  }

  //........................................................................
  //------------------------------- asFeet ---------------------------------

  /**
   * Set the weight as feet value.
   *
   * @param       inSquareYards  the number of square yards
   * @param       inSquareFeet   the number of square feet
   * @param       inSquareInches the number of square inches
   *
   * @return      the value as feet
   */
  public Area asFeet(@Nullable Rational inSquareYards,
                     @Nullable Rational inSquareFeet,
                     @Nullable Rational inSquareInches)
  {
    return as(new Rational [] { inSquareYards, inSquareFeet,
                                inSquareInches }, 0);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The Test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Testing init. */
    @org.junit.Test
    public void init()
    {
      Area value = new Area();

      // undefined value
      assertEquals("not undefined at start", false, value.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   value.toString());
      assertEquals("feet",   false, value.isFeet());
      assertEquals("metric", false, value.isMetric());
      assertEquals("undefined value not correct", "0",
                   value.getAsMeters().toString());
      assertEquals("undefined value not correct", "0",
                   value.getAsFeet().toString());

      // now with some value (sq cm)
      value = new Area(null, null, new Rational(1, 1, 2), true);

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("string ", "1 1/2 sq cm", value.toString());
      assertEquals("feet",   false,  value.isFeet());
      assertEquals("metric", true,   value.isMetric());
      assertEquals("m",       "3/20000", value.getAsMeters().toString());
      assertEquals("ft",      "3/800", value.getAsFeet().toString());

       value = new Area(null, new Rational(1, 2, 3), new Rational(2), false);

       assertEquals("not defined at start", true, value.isDefined());
       assertEquals("string", "1 2/3 sq ft 2 sq in", value.toString());
       assertEquals("feet",   true,  value.isFeet());
       assertEquals("metric", false, value.isMetric());
       assertEquals("m", "121/1800", value.getAsMeters().toString());
       assertEquals("ft", "1 49/72", value.getAsFeet().toString());

       // now with some value (metric)
       value = new Area(new Rational(1), new Rational(1, 1, 2),
                        new Rational(2, 3), false);

       assertEquals("not defined at start", true, value.isDefined());
       assertEquals("string", "1 sq yd 1 1/2 sq ft 2/3 sq in",
                    value.toString());
       assertEquals("feet",   true,  value.isFeet());
       assertEquals("metric", false,  value.isMetric());
       assertEquals("m",  "2269/5400", value.getAsMeters().toString());
       assertEquals("ft",   "10 109/216", value.getAsFeet().toString());

       value = new Area(new Rational(1), new Rational(3, 1, 4),
                        new Rational(2, 3), true);

       assertEquals("not defined at start", true, value.isDefined());
       assertEquals("string", "1 sq m 3 1/4 sq dm 2/3 sq cm",
                    value.toString());
       assertEquals("feet",   false, value.isFeet());
       assertEquals("metric", true,  value.isMetric());
       assertEquals("in", "25 977/1200", value.getAsFeet().toString());
       assertEquals("cm", "1 977/30000", value.getAsMeters().toString());

       // set
       value = value.asMetric(new Rational(1), null, new Rational(1, 2));

       assertEquals("metric", "1 sq m 1/2 sq cm", value.toString());

       value = value.asFeet(new Rational(1), new Rational(2, 3),
                            new Rational(1, 4));

       assertEquals("feet", "1 sq yd 2/3 sq ft 1/4 sq in", value.toString());

       Value.Test.createTest(value);
   }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "simple", "1 sq m", "1 sq m", null,
          "no space", "5sq m 300sq     dm", "5 sq m 300 sq dm", null,
          "whites", "1 \n1/2    sq ft 5 \n sq \nin", "1 1/2 sq ft 5 sq in",
          null,

          "order", "30 sq yd 5 sq in 10 sq in 1 sq yd",
          "31 sq yd 15 sq in", null,

          "other text", "1sq m 5 sq dm 0 a", "1 sq m 5 sq dm", " 0 a",
          "nothing", "50", null, "50",
          "nothing", "22.3 sq cm", null, "22.3 sq cm",
          "nothing", "1/4 gu", null, "1/4 gu",
          "mixed", "5 sq ft 200 sq cm", "5 sq ft", " 200 sq cm",
        };

      Value.Test.readTest(tests, new Area());
    }

    //......................................................................

    //----- metric ---------------------------------------------------------

    /** Testing metric. */
    public void testMetric()
    {
      String []texts =
        {
          "1 sq ft",         "4 sq dm",
          "1 sq in",         "2 7/9 sq cm",
          "5 sq in",         "13 8/9 sq cm",
          "3 sq ft",         "12 sq dm",
          "3 sq in 5 sq ft", "20 sq dm 8 1/3 sq cm",
        };

      Area value = new Area();

      for(int i = 0; i < texts.length; i += 2)
      {
        try (ParseReader reader =
          new ParseReader(new java.io.StringReader(texts[i]), "test"))
        {
          value = value.read(reader);
          assertEquals("test " + i / 2, texts[i + 1],
                       value.asMetric().toString());
        }
      }
    }

    //......................................................................
    //----- feet -----------------------------------------------------------

    /** Testing feet. */
    @org.junit.Test
    public void feet()
    {
//       net.ixitxachitls.util.logging.Log.add
//         ("test", new net.ixitxachitls.util.logging.ANSILogger());

      String []texts =
        {
          "1 sq m",           "2 sq yd 7 sq ft",
          "1 1/2 sq dm",      "54 sq in",
          "1 sq m 300 sq dm", "11 sq yd 1 sq ft",
          "2 sq m",           "5 sq yd 5 sq ft",
        };

      Area value = new Area();

      for(int i = 0; i < texts.length; i += 2)
      {
        try (ParseReader reader =
          new ParseReader(new java.io.StringReader(texts[i]), "test"))
        {
          value = value.read(reader);
          assertEquals("test " + i / 2, texts[i + 1],
                       value.asFeet().toString());
        }
      }
    }

    //......................................................................
  }

  //........................................................................
}
