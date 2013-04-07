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
 * This class stores a distance. This is a convenience class for Units.
 *
 * @file          Distance.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Distance extends Units<Distance>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ Distance --------------------------------

  /**
   * Construct the distance object with an undefined value.
   *
   */
  public Distance()
  {
    super(s_sets, 3);

    m_template = "distance";
  }

  //........................................................................
  //------------------------------ Distance --------------------------------

  /**
   * Construct the distance object.
   *
   * @param       inLarge  the large number (yards or km)
   * @param       inMiddle the middle number (feet or m)
   * @param       inSmall  the small number (inches or cm)
   * @param       inMetric a flag for metric values (feet if false)
   *
   */
  public Distance(@Nullable Rational inLarge, @Nullable Rational inMiddle,
                  @Nullable Rational inSmall, boolean inMetric)
  {
    super(new Rational [] { inLarge, inMiddle, inSmall }, s_sets,
          s_sets[inMetric ? 1 : 0], 3);

    m_template = "distance";
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
public Distance create()
  {
    return super.create(new Distance());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The definition of distances. */
  private static String s_definition =
    Config.get("/game.distance",
               "1/1    : Feet =   5280/1   : mi : mile|miles,"
               + "                     1/1   : ft : foot|feet,"
               + "                     1/12  : in : inch|inches."
               + "2/5    : Metric = 1000/1   : km : kilometer|kilometers,"
               + "                     1/1   : m  : meter|meters,"
               + "                     1/100 : cm : centimeter|centimeters.");

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
  public Distance asMetric()
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
  public Distance asFeet()
  {
    if(m_set == m_sets[0])
      return this;

    return toSet(0, true);
  }

  //........................................................................

  //----------------------------- collectData ------------------------------

  /**
   * Collect the data available for printing the value.
   *
   * @param       inEntry    the entry this value is in
   * @param       inRenderer the renderer to render sub values
   *
   * @return      the data as a map
   *
   */
  // @Override
  // public Map<String, Object> collectData(AbstractEntry inEntry,
  //                                        SoyRenderer inRenderer)
  // {
  //   Map<String, Object> data = super.collectData(inEntry, inRenderer);

  //   data.put("metric", isMetric());
  //   data.put("feet", isFeet());
  //   data.put("asfeet",
  //            new SoyValue("as feet", asFeet(), inEntry, inRenderer));
  //   data.put("asmetric",
  //            new SoyValue("as metric", asMetric(), inEntry, inRenderer));

  //   return data;
  // }

  //........................................................................
  //------------------------------- doGroup --------------------------------

  /**
   * Return the group this value belongs to.
   *
   * @return      a string denoting the group this value is in
   *
   */
  @Override
  public String doGroup()
  {
    Rational distance = getAsFeet();

    if(distance.compare(new Rational(1, 120)) <= 0)
      return "1/10 in";

    if(distance.compare(new Rational(1, 48)) <= 0)
      return "1/4 in";

    if(distance.compare(new Rational(1, 24)) <= 0)
      return "1/2 in";

    if(distance.compare(new Rational(1, 12)) <= 0)
      return "1 in";

    if(distance.compare(new Rational(1, 6)) <= 0)
      return "2 in";

    if(distance.compare(new Rational(1, 4)) <= 0)
      return "3 in";

    if(distance.compare(new Rational(1, 3)) <= 0)
      return "4 in";

    if(distance.compare(new Rational(5, 12)) <= 0)
      return "5 in";

    if(distance.compare(new Rational(5, 6)) <= 0)
      return "10 in";

    if(distance.compare(1) <= 0)
      return "1 ft";

    if(distance.compare(5) <= 0)
      return "5 ft";

    if(distance.compare(10) <= 0)
      return "10 ft";

    if(distance.compare(25) <= 0)
      return "25 ft";

    if(distance.compare(50) <= 0)
      return "50 ft";

    if(distance.compare(100) <= 0)
      return "100 ft";

    if(distance.compare(250) <= 0)
      return "250 ft";

    if(distance.compare(500) <= 0)
      return "500 ft";

    if(distance.compare(1000) <= 0)
      return "1000 ft";

    if(distance.compare(5280) <= 0)
      return "1 ml";

    if(distance.compare(2 * 5280) <= 0)
      return "2 ml";

    if(distance.compare(5 * 5280) <= 0)
      return "5 ml";

    if(distance.compare(10 * 5280) <= 0)
      return "10 ml";

    if(distance.compare(25 * 5280) <= 0)
      return "25 ml";

    if(distance.compare(50 * 5280) <= 0)
      return "50 ml";

    if(distance.compare(100 * 5280) <= 0)
      return "100 ml";

    return "Infinite";
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- asMetric -------------------------------

  /**
   * Set the weight as metric value.
   *
   * @param       inKilometers  the number of kilometers
   * @param       inMeters      the number of meters
   * @param       inCentimeters the number of centimeters
   *
   * @return      a new object with the given value
   *
   */
  public Distance setMetric(@Nullable Rational inKilometers,
                            @Nullable Rational inMeters,
                            @Nullable Rational inCentimeters)
  {
    return as(new Rational [] { inKilometers, inMeters, inCentimeters }, 1);
  }

  //........................................................................
  //-------------------------------- asFeet --------------------------------

  /**
   * Set the weight as feet value.
   *
   * @param       inMiles  the number of kilometers
   * @param       inFeet   the number of meters
   * @param       inInches the number of centimeters
   *
   * @return      a new object with the new values
   *
   */
  public Distance asFeet(@Nullable Rational inMiles,
                         @Nullable Rational inFeet,
                         @Nullable Rational inInches)
  {
    return as(new Rational [] { inMiles, inFeet, inInches }, 0);
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
      Distance value = new Distance();

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

      // now with some value (cm)
      value = new Distance(null, null, new Rational(1, 1, 2), true);

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("string ", "1 1/2 cm", value.toString());
      assertEquals("feet",   false,  value.isFeet());
      assertEquals("metric", true,   value.isMetric());
      assertEquals("cm",   "3/200", value.getAsMeters().toString());
      assertEquals("in",   "3/80", value.getAsFeet().toString());

      value = new Distance(new Rational(1), new Rational(1, 2, 3),
                           new Rational(2), false);

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("string", "1 mi 1 2/3 ft 2 in", value.toString());
      assertEquals("feet",   true,  value.isFeet());
      assertEquals("metric", false, value.isMetric());
      assertEquals("cm", "2112 11/15", value.getAsMeters().toString());
      assertEquals("in", "5281 5/6", value.getAsFeet().toString());

      // now with some value (metric)
       value = new Distance(null, new Rational(1, 1, 2), null, true);

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("string", "1 1/2 m", value.toString());
      assertEquals("feet",   false,  value.isFeet());
      assertEquals("metric", true,  value.isMetric());
      assertEquals("cm",  "1 1/2", value.getAsMeters().toString());
      assertEquals("in",   "3 3/4", value.getAsFeet().toString());

      value = new Distance(new Rational(1), new Rational(3, 1, 4),
                           new Rational(20), true);

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("string", "1 km 3 1/4 m 20 cm",
                   value.toString());
      assertEquals("feet",   false, value.isFeet());
      assertEquals("metric", true,  value.isMetric());
      assertEquals("in", "2508 5/8", value.getAsFeet().toString());
      assertEquals("cm", "1003 9/20", value.getAsMeters().toString());

      // setting
      value = value.setMetric(new Rational(1), null, new Rational(1, 2));

      assertEquals("set metric", "1 km 1/2 cm", value.toString());

      value = value.asFeet(null, new Rational(3, 2), new Rational(1, 1, 2));

      assertEquals("set feet", "3/2 ft 1 1/2 in", value.toString());

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
          "simple", "1 km", "1 km", null,
          "no space", "5m 300cm", "5 m 300 cm", null,
          "whites", "1 \n1/2    ft 5 \n in", "1 1/2 ft 5 in", null,
          "order", "30 cm 5 m 10 cm 1 km", "1 km 5 m 40 cm", null,
          "other text", "1km 5 m 0 a", "1 km 5 m", " 0 a",
          "nothing", "50", null, "50",
          "nothing", "22.3 cm", null, "22.3 cm",
          "nothing", "1/4 gu", null, "1/4 gu",
          "mixed", "5 ft 200 cm", "5 ft", " 200 cm",
        };

      Value.Test.readTest(tests, new Distance());
    }

    //......................................................................

    //----- metric ---------------------------------------------------------

    /** Testing metric values. */
    @org.junit.Test
    public void metric()
    {
      String []texts =
        {
          "1 1/2 ft",  "60 cm",
          "5 mi",      "10 km 560 m",
          "3 ft",      "1 m 20 cm",
          "3 in 5 ft", "2 m 10 cm",
        };

      Distance value = new Distance();

      for(int i = 0; i < texts.length; i += 2)
      {
        ParseReader reader =
          new ParseReader(new java.io.StringReader(texts[i]), "test");

        value = value.read(reader);
        assertEquals("test " + i / 2, texts[i + 1],
                     value.asMetric().toString());
      }
    }

    //......................................................................
    //----- feet -----------------------------------------------------------

    /** Testing feet values. */
    @org.junit.Test
    public void feet()
    {
      String []texts =
        {
          "20 km",            "9 mi 2480 ft",
          "1 1/2 cm",         "9/20 in",
          "1 1/2m",           "3 ft 9 in",
          "1 km 300 m 30 cm", "3250 ft 9 in",
          "2 m",              "5 ft",
        };

      Distance value = new Distance();

      for(int i = 0; i < texts.length; i += 2)
      {
        ParseReader reader =
          new ParseReader(new java.io.StringReader(texts[i]), "test");

        value = value.read(reader);
        assertEquals("test " + i / 2, texts[i + 1], value.asFeet().toString());
      }
    }

    //......................................................................
  }

  //........................................................................
}
