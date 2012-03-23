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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a weight value.
 *
 * @file          Weight.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
public class Weight extends Units<Weight>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------- Weight --------------------------------

  /**
   * Construct the weight object with an undefined value.
   *
   */
  public Weight()
  {
    super(s_sets, 5);
  }

  //........................................................................
  //------------------------------- Weight --------------------------------

  /**
   * Construct the weight object.
   *
   * @param       inPounds number of pounds
   * @param       inOunces number of ounces
   *
   */
  public Weight(@Nullable Rational inPounds, @Nullable Rational inOunces)
  {
    super(new Rational [] { inPounds, inOunces}, s_sets, s_sets[0], 5);
  }

  //........................................................................
  //------------------------------- Weight --------------------------------

  /**
   * Construct the weight object.
   *
   * @param       inTons  number of metric tons
   * @param       inKilos number of metric kilograms
   * @param       inGrams number of metric grams
   *
   */
  public Weight(Rational inTons, Rational inKilos, Rational inGrams)
  {
    super(new Rational [] { inTons, inKilos, inGrams }, s_sets, s_sets[1], 5);
  }

  //........................................................................
  //------------------------------- Weight --------------------------------

  /**
   * Construct the weight object.
   *
   * @param       inCarats number of carats
   *
   */
  public Weight(Rational inCarats)
  {
    super(new Rational [] { inCarats }, s_sets, s_sets[2], 5);
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
  public Weight create()
  {
    return super.create(new Weight());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The definition of the units for weights. */
  private static @Nonnull String s_definition =
    Config.get("/game.weight",
               "1/1    : Pounds =    1/1    : lb|lbs : pounds|pound,"
               + "                     1/16   : oz     : ounces|ounce."
               + "1/2    : Metric = 1000/1    : t  : tons|ton,"
               + "                     1/1    : kg : "
               + "kilo|kilos|kilograms|kilogram,"
               + "                     1/1000 : g  : grams|gram."
               + "2500/1 : Carat  =    1/1    : ct : carats|carat.");

  /** The sets of available units. */
  private static @Nonnull Set []s_sets = parseDefinition(s_definition);

  /** The grouping. */
  protected static final @Nonnull Group<Weight, Long, String> s_grouping =
    new Group<Weight, Long, String>(new Group.Extractor<Weight, Long>()
      {
        @Override
        public Long extract(Weight inValue)
        {
          if(inValue == null)
            throw new IllegalArgumentException("must have a  value here");

          return (long)(inValue.getAsPounds().getValue() * 10);
        }
      }, new Long [] { 1L, 1 * 10L, 5 * 10L, 10 * 10L, 25 * 10L, 50 * 10L,
                       100 * 10L, 1000 * 10L, },
                                new String []
      { "1/10 lb ", "1 lb", "5 lbs", "10 lbs", "25 lbs", "50 lbs",
        "100 lbs", "1000 lbs", "Infinite", }, "$undefined$");

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
  //------------------------------- isPound --------------------------------

  /**
   * Determine if pound values are stored.
   *
   * @return      true if pound values are stored, false else
   *
   */
  public boolean isPound()
  {
    return m_set == m_sets[0];
  }

  //........................................................................
  //------------------------------- isCarat -------------------------------

  /**
   * Determine if carat values are stored.
   *
   * @return      true if carat values are stored, false else
   *
   */
  public boolean isCarat()
  {
    return m_set == m_sets[2];
  }

  //........................................................................

  //----------------------------- getAsPounds -----------------------------

  /**
   * Get the value as an approximate equivalent of pounds.
   *
   * @return      the ounces
   *
   * @algorithm   just compute the value, using a simplified conversion:
   *
   *                1  t = 33000 ounces
   *                1 kg =    32 ounces
   *                1 lb =    16 ounces
   *               30  g =     1 ounce
   *              150 ct =     1 ounce
   *
   */
  public @Nonnull Rational getAsPounds()
  {
    if(!isDefined())
      return new Rational(0);

    if(m_set == m_sets[0])
      return getAsBase();

    return toSet(0, false).getAsBase();
  }

  //........................................................................
  //--------------------------- getAsKiloGrams ----------------------------

  /**
   * Get the value as an approximate equivalent of kilograms.
   *
   * @return      the grams
   *
   * @algorithm   just compute the value, using a simplified conversion:
   *
   *                1  t = 1000000 grams
   *                1 kg =    1000 grams
   *                1 lb =     500 grams
   *                1 oz =      30 grams
   *                5 ct =       1 gram
   *
   */
  public @Nonnull Rational getAsKilograms()
  {
    if(!isDefined())
      return new Rational(0);

    if(m_set == m_sets[1])
      return getAsBase();

    return toSet(1, false).getAsBase();
  }

  //........................................................................
  //----------------------------- getAsCarats ------------------------------

  /**
   * Get the value as an approximate equivalent of carats.
   *
   * @return      the carats
   *
   * @algorithm   just compute the value, using a simplified conversion:
   *
   *                1  t = 5000000 carats
   *                1 kg =    5000 carats
   *                1 lb =    2500 carats
   *                1 oz =     150 carats
   *                1  g =       5 carats
   *
   */
  public @Nonnull Rational getAsCarats()
  {
    if(!isDefined())
      return new Rational(0);

    if(m_set == m_sets[2])
      return getAsBase();

    return toSet(2, false).getAsBase();
  }

  //........................................................................

  //------------------------------- asMetric -------------------------------

  /**
   * Convert this value into a metric value.
   *
   * @return      the corresponding metric value.
   *
   */
  public @Nonnull Weight asMetric()
  {
    if(m_set == m_sets[1])
      return this;

    return toSet(1, true);
  }

  //........................................................................
  //------------------------------- asPound --------------------------------

  /**
   * Convert this value into a pound value.
   *
   * @return      the corresponding pound value.
   *
   */
  public Weight asPound()
  {
    if(m_set == m_sets[0])
      return this;

    return toSet(0, true);
  }

  //........................................................................
  //------------------------------- asCarat -------------------------------

  /**
   * Convert this value into a carat value.
   *
   * @return      the corresponding carat value.
   *
   */
  public Weight asCarat()
  {
    if(m_set == m_sets[2])
      return this;

    return toSet(2, true);
  }

  //........................................................................

  //------------------------------- doGroup --------------------------------

  /**
   * Return the group this value belongs to.
   *
   * @return      a string denoting the group this value is in
   *
   */
  @Override
  protected @Nonnull String doGroup()
  {
    return s_grouping.group(this);
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- asMetric -------------------------------

  /**
   * Set the weight as metric value.
   *
   * @param       inTons  number of metric tons
   * @param       inKilos number of metric kilograms
   * @param       inGrams number of metric grams
   *
   * @return      the new metric value
   *
   */
  public @Nonnull Weight asMetric(@Nullable Rational inTons,
                                  @Nullable Rational inKilos,
                                  @Nullable Rational inGrams)
  {
    return as(new Rational [] { inTons, inKilos, inGrams }, 1);
  }

  //........................................................................
  //------------------------------- asPounds -------------------------------

  /**
   * Set the weight as pound value.
   *
   * @param       inPounds number of pounds
   * @param       inOunces number of ounces
   *
   * @return      the new pound value
   *
   */
  public @Nonnull Weight asPounds(@Nullable Rational inPounds,
                                  @Nullable Rational inOunces)
  {
    return as(new Rational [] { inPounds, inOunces }, 0);
  }

  //........................................................................
  //------------------------------- asCarats -------------------------------

  /**
   * Set the weight as carat value.
   *
   * @param       inCarats number of carats
   *
   * @return      the new carat value
   *
   */
  public @Nonnull Weight asCarats(@Nonnull Rational inCarats)
  {
    return as(new Rational [] { inCarats }, 2);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Test inits. */
    @org.junit.Test
    public void init()
    {
      Weight value = new Weight();

      // undefined value
      assertEquals("not undefined at start", false, value.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   value.toString());
      assertEquals("undefined value not correct", "\\color{error}{$undefined$}",
                   value.format(false).toString());
      assertEquals("pound",  false, value.isPound());
      assertEquals("metric", false, value.isMetric());
      assertEquals("carat",  false, value.isCarat());
      assertEquals("undefined value not correct", "0",
                   value.getAsPounds().toString());
      assertEquals("undefined value not correct", "0",
                   value.getAsCarats().toString());
      assertEquals("undefined value not correct", "0",
                   value.getAsKilograms().toString());

      // now with some value (pounds)
      value = new Weight(new Rational(1, 1, 2), null);

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("undefined value not correct", "1 1/2 lbs",
                   value.toString());
      assertEquals("undefined value not correct",
                   "\\window{\\span{unit}{\\frac[1]{1}{2} lbs}}"
                   + "{\\table{#inline#1:L,,;100:L}"
                   + "{Total:}{\\frac[1]{1}{2} lbs}{}{}"
                   + "{Metric:}{\\span{unit}{\\frac{3}{4} kg}}"
                   + "{Carat:}{\\span{unit}{3750 ct}}}",
                   value.format(false).toString());
      assertEquals("pound",  true,  value.isPound());
      assertEquals("metric", false, value.isMetric());
      assertEquals("carat",  false, value.isCarat());
      assertEquals("undefined value not correct",   "1 1/2",
                   value.getAsPounds().toString());
      assertEquals("undefined value not correct", "3750",
                   value.getAsCarats().toString());
      assertEquals("undefined value not correct",  "3/4",
                   value.getAsKilograms().toString());

      value = new Weight(new Rational(1), new Rational(3));

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("undefined value not correct", "1 lb 3 oz",
                   value.toString());
      assertEquals("undefined value not correct",
                   "\\window{\\span{unit}{1 lb} \\span{unit}{3 oz}}"
                   + "{\\table{#inline#1:L,,;100:L}"
                   + "{Total:}{\\frac[1]{3}{16} lbs}{}{}"
                   + "{Metric:}{\\span{unit}{\\frac[593]{3}{4} g}}"
                   + "{Carat:}{\\span{unit}{\\frac[2968]{3}{4} ct}}}",
                   value.format(false).toString());
      assertEquals("pound",  true,  value.isPound());
      assertEquals("metric", false, value.isMetric());
      assertEquals("carat",  false, value.isCarat());
      assertEquals("undefined value not correct",   "1 3/16",
                   value.getAsPounds().toString());
      assertEquals("undefined value not correct", "2968 3/4",
                   value.getAsCarats().toString());
      assertEquals("undefined value not correct",  "19/32",
                   value.getAsKilograms().toString());

      // now with some value (metric)
       value = new Weight(null, new Rational(1, 1, 2), null);

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("undefined value not correct", "1 1/2 kg",
                   value.toString());
      assertEquals("undefined value not correct",
                   "\\window{\\span{unit}{\\frac[1]{1}{2} kg}}"
                   + "{\\table{#inline#1:L,,;100:L}"
                   + "{Total:}{\\frac[1]{1}{2} kg}{}{}"
                   + "{Pounds:}{\\span{unit}{3 lbs}}{Carat:}"
                   + "{\\span{unit}{7500 ct}}}",
                   value.format(false).toString());
      assertEquals("pound",  false,  value.isPound());
      assertEquals("metric", true,  value.isMetric());
      assertEquals("carat",  false, value.isCarat());
      assertEquals("undefined value not correct",   "3",
                   value.getAsPounds().toString());
      assertEquals("undefined value not correct", "7500",
                   value.getAsCarats().toString());
      assertEquals("undefined value not correct", "1 1/2",
                   value.getAsKilograms().toString());

      value = new Weight(new Rational(1), new Rational(3, 1, 4),
                         new Rational(20));

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("undefined value not correct", "1 t 3 1/4 kg 20 g",
                   value.toString());
      assertEquals("undefined value not correct",
                   "\\window{\\span{unit}{1 t} "
                   + "\\span{unit}{\\frac[3]{1}{4} kg} \\span{unit}{20 g}}"
                   + "{\\table{#inline#1:L,,;100:L}"
                   + "{Total:}{\\frac[1003]{27}{100} kg}"
                   + "{}{}{Pounds:}{\\span{unit}{2006 lbs} "
                   + "\\span{unit}{\\frac[8]{16}{25} oz}}"
                   + "{Carat:}{\\span{unit}{5016350 ct}}}",
                   value.format(false).toString());
      assertEquals("pound",  false, value.isPound());
      assertEquals("metric", true,  value.isMetric());
      assertEquals("carat",  false, value.isCarat());
      assertEquals("undefined value not correct",   "2006 27/50",
                   value.getAsPounds().toString());
      assertEquals("undefined value not correct", "5016350",
                   value.getAsCarats().toString());
      assertEquals("undefined value not correct", "1003 27/100",
                   value.getAsKilograms().toString());

      // now with some value (carats)
      value = new Weight(new Rational(25, 1, 3));

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("undefined value not correct", "25 1/3 ct",
                   value.toString());
      assertEquals("undefined value not correct",
                   "\\window{\\span{unit}{\\frac[25]{1}{3} ct}}"
                   + "{\\table{#inline#1:L,,;100:L}"
                   + "{Total:}{\\frac[25]{1}{3} ct}{}{}"
                   + "{Pounds:}{\\span{unit}{\\frac{304}{1875} oz}}{Metric:}"
                   + "{\\span{unit}{\\frac[5]{1}{15} g}}}",
                   value.format(false).toString());
      assertEquals("pound",  false, value.isPound());
      assertEquals("metric", false, value.isMetric());
      assertEquals("carat",  true,  value.isCarat());
      assertEquals("undefined value not correct",   "19/1875",
                   value.getAsPounds().toString());
      assertEquals("undefined value not correct",  "25 1/3",
                   value.getAsCarats().toString());
      assertEquals("undefined value not correct",   "19/3750",
                   value.getAsKilograms().toString());

      Value.Test.createTest(value);
   }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Test reading. */
    @org.junit.Test
    public void read()
    {
      String []tests =
        {
          "simple lb", "1 lb", "1 lb", null,
          "simple kg", "5 kg 200 g", "5 kg 200 g", null,
          "simple ct", "22 1/2 ct", "22 1/2 ct",  null,
          "plural", "1 lbs", "1 lb", null,
          "plural 2 ", "5 lb", "5 lbs", null,
          "whites", "1 \n1/2    lb 5 \n    oz", "1 1/2 lbs 5 oz", null,
          "empty", "", null, null,
          "other", "1 kg 2 guru", "1 kg", " 2 guru",
          "other 2", "1 kg there", "1 kg", " there",
          "mixed", "5 1/2 lbs 1 kg", "5 1/2 lbs", " 1 kg",
          "summed", "1 kg 2 g 3 kg", "4 kg 2 g", null,
          "invalid", "33 guru", null, "33 guru",
          "invalid 2", "guru", null, "guru",
        };

      Value.Test.readTest(tests, new Weight());
    }

    //......................................................................
    //----- metric ---------------------------------------------------------

    /** Test metric values. */
    @org.junit.Test
    public void metric()
    {
      String []texts =
        {
          "2 lb",      "1 kg",
          "3 lbs",     "1 1/2 kg",
          "3 1/2 lbs", "1 3/4 kg",
          "10 ct",     "2 g",
          "1 ct",      "1/5 g",
          "5 oz",      "156 1/4 g",
          "50 oz",     "1 kg 562 1/2 g",
          "60 oz",     "1 kg 875 g",
        };

      Weight value = new Weight();

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
    //----- pound ----------------------------------------------------------

    /** Test imperial values. */
    @org.junit.Test
    public void pound()
    {
      String []texts =
        {
          "1 kg",      "2 lbs",
          "1/4 kg",    "1/2 lb",
          "100 g",     "3 1/5 oz",
          "1/2 t",     "1000 lbs",
          "1 kg 100g", "2 lbs 3 1/5 oz",
          "1 ct",      "4/625 oz",
          "300 ct",    "1 23/25 oz",
          "3000 ct",   "1 lb 3 1/5 oz",
          "3050 ct",   "1 lb 3 13/25 oz",
        };

      Weight value = new Weight();

      for(int i = 0; i < texts.length; i += 2)
      {
        ParseReader reader =
          new ParseReader(new java.io.StringReader(texts[i]), "test");

        value = value.read(reader);
        assertEquals("test " + i / 2, texts[i + 1],
                     value.asPound().toString());
      }
    }

    //......................................................................
    //----- carat ----------------------------------------------------------

    /** Test carats. */
    @org.junit.Test
    public void carat()
    {
      String []texts =
        {
          "1 g",      "5 ct",
          "1/2 g",    "2 1/2 ct",
          "4 oz",     "625 ct",
          "1/3 oz",   "52 1/12 ct",
          "1/3 t",    "1666666 2/3 ct",
        };

      Weight value = new Weight();

      for(int i = 0; i < texts.length; i += 2)
      {
        ParseReader reader =
          new ParseReader(new java.io.StringReader(texts[i]), "test");

        value = value.read(reader);
        assertEquals("test " + i / 2, texts[i + 1],
                     value.asCarat().toString());
      }
    }

    //......................................................................
    //----- as -------------------------------------------------------------

    /** Test setting. */
    @org.junit.Test
    public void as()
    {
      Weight weight = new Weight();

      weight = weight.asCarats(new Rational(1, 3, 5));
      assertEquals("set carat", "1 3/5 ct", weight.toString());

      weight = weight.asPounds(new Rational(1, 3, 5), new Rational(4));
      assertEquals("set carat", "1 3/5 lbs 4 oz", weight.toString());

      weight = weight.asMetric(null, new Rational(5), new Rational(1, 3, 5));
      assertEquals("set carat", "5 kg 1 3/5 g", weight.toString());
    }

    //......................................................................
  }

  //........................................................................
}
