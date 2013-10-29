/******************************************************************************
 * Copyright (c) 2003-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

import net.ixitxachitls.dma.proto.Values.VolumeProto;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class stores a volume. This is a convenience class for Units.
 *
 * @file          Volume.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Volume extends Units<Volume>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ Volume --------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /**
   * Construct the volume object with an undefined value.
   *
   */
  public Volume()
  {
    super(s_sets, 5);

    withTemplate("volume");
  }

  //........................................................................
  //------------------------------ Volume --------------------------------

  /**
   * Construct the volume object.
   *
   * @param       inLarge  the large number (cubic meter or feet)
   * @param       inSmall  the small number (cubic cm or in)
   * @param       inMetric a flag for metric values (feet if false)
   *
   */
  public Volume(@Nullable Rational inLarge, @Nullable Rational inSmall,
                boolean inMetric)
  {
    super(new Rational [] { inLarge, inSmall }, s_sets,
          s_sets[inMetric ? 1 : 0], 5);

    withTemplate("volume");
  }

  //........................................................................
  //------------------------------ Volume --------------------------------

  /**
   * Construct the volume object.
   *
   * @param       inGallons the number of gallons
   * @param       inQuarts  the number of quarts
   * @param       inPints   the number of pints
   * @param       inCups    the number of cups
   * @param       inOunces  the number of ounces
   */
  public Volume(@Nullable Rational inGallons, @Nullable Rational inQuarts,
                @Nullable Rational inPints, @Nullable Rational inCups,
                @Nullable Rational inOunces)
  {
    super(new Rational [] { inGallons, inQuarts, inPints, inCups, inOunces },
          s_sets, s_sets[2], 5);

    withTemplate("volume");
  }

  //........................................................................
  //------------------------------ Volume --------------------------------

  /**
   * Construct the volume object.
   *
   * @param       inCentiLiter the number of dl
   * @param       inDeciLiter  the number of cl
   * @param       inLiter      the number of l
   */
  public Volume(@Nullable Rational inLiter, @Nullable Rational inDeciLiter,
                @Nullable Rational inCentiLiter)
  {
    super(new Rational [] { inLiter, inDeciLiter, inCentiLiter, }, s_sets,
          s_sets[3], 5);

    withTemplate("volume");
  }

  //........................................................................

  //------------------------------- create ---------------------------------

  /**
   * Create a new list with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar list, but without any contents
   *
   */
  @Override
  public Volume create()
  {
    return super.create(new Volume());
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The definition of volumes. */
  private static String s_definition =
    Config.get("/game.volume",
               "1/1  : Feet =   1/1    : cu ft : cubic foot|cubic feet,"
               + "                1/1728 : cu in : cubic inch|cubic inches."
               + "1/25 : Metric = 1/1    : cu m  : cubic meter|cubic meters,"
               + "          1/1000 : cu dm : cubic decimeter|cubic decimeters."
               + "15/2 : Gallons =  1/1   : gallon|gallons,"
               + "                  1/4   : quart|quarts,"
               + "                  1/8   : pint|pints,"
               + "                  1/16  : cup|cups,"
               + "                  1/128 : ounce|ounces|oz."
               + "30/1 : Liters = 1/1   : l  : liter|liters,"
               + "                1/10  : dl : deciliter|deciliters,"
               + "                1/100 : cl : centiliter|centiliters.");

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
    return m_set == m_sets[1] || m_set == s_sets[3];
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
    return m_set == s_sets[0] || m_set == s_sets[2];
  }

  //........................................................................
  //------------------------------ isLiquid --------------------------------

  /**
   * Determine if liquid values are stored.
   *
   * @return      true if liquid values are stored, false else
   *
   */
  public boolean isLiquid()
  {
    return m_set == s_sets[2] || m_set == s_sets[3];
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
  //----------------------------- getAsGallons -----------------------------

  /**
   * Get the value as an approximate equivalent of US liquids.
   *
   * @return      the liquid value
   *
   */
  public Rational getAsGallons()
  {
    if(!isDefined())
      return new Rational(0);

    if(m_set == m_sets[2])
      return getAsBase();

    return toSet(2, false).getAsBase();
  }

  //........................................................................
  //----------------------------- getAsLiters ------------------------------

  /**
   * Get the value as an approximate equivalent of liters.
   *
   * @return      the liters value
   *
   */
  public Rational getAsLiters()
  {
    if(!isDefined())
      return new Rational(0);

    if(m_set == m_sets[3])
      return getAsBase();

    return toSet(3, false).getAsBase();
  }

  //........................................................................

  //------------------------------- asMetric -------------------------------

  /**
   * Convert this value into a metric value.
   *
   * @return      the corresponding metric value.
   *
   */
  public Volume asMetric()
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
  public Volume asFeet()
  {
    if(m_set == m_sets[0])
      return this;

    return toSet(0, true);
  }

  //........................................................................
  //------------------------------ asGallons -------------------------------

  /**
   * Convert this value into a gallon value.
   *
   * @return      the corresponding feet value.
   *
   */
  public Volume asGallons()
  {
    if(m_set == m_sets[2])
      return this;

    return toSet(2, true);
  }

  //........................................................................
  //------------------------------- asLiters -------------------------------

  /**
   * Convert this value into a liter value.
   *
   * @return      the corresponding feet value.
   *
   */
  public Volume asLiters()
  {
    if(m_set == m_sets[3])
      return this;

    return toSet(3, true);
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
  public String doGroup()
  {
    if(isLiquid())
    {
      Rational volume = getAsGallons();

      if(volume.compare(new Rational(1, 16)) <= 0)
        return "cup";

      if(volume.compare(new Rational(1, 8)) <= 0)
        return "pint";

      if(volume.compare(new Rational(1, 4)) <= 0)
        return "quart";

      if(volume.compare(1) <= 0)
        return "1 gallon";

      if(volume.compare(2) <= 0)
        return "2 gallons";

      if(volume.compare(5) <= 0)
        return "5 gallons";

      if(volume.compare(10) <= 0)
        return "10 gallons";

      if(volume.compare(25) <= 0)
        return "25 gallons";

      if(volume.compare(50) <= 0)
        return "50 gallons";

      if(volume.compare(100) <= 0)
        return "100 gallons";

      return "a lot";
    }

    Rational volume = getAsFeet();

    if(volume.compare(new Rational(1, 1728)) <= 0)
      return "1 cu in";

    if(volume.compare(new Rational(5, 1728)) <= 0)
      return "5 cu in";

    if(volume.compare(new Rational(10, 1728)) <= 0)
      return "10 cu in";

    if(volume.compare(new Rational(50, 1728)) <= 0)
      return "50 cu in";

    if(volume.compare(new Rational(100, 1728)) <= 0)
      return "100 cu in";

    if(volume.compare(1) <= 0)
      return "1 cu ft";

    if(volume.compare(5) <= 0)
      return "5 cu ft";

    if(volume.compare(10) <= 0)
      return "10 cu ft";

    if(volume.compare(25) <= 0)
      return "25 cu ft";

    if(volume.compare(50) <= 0)
      return "50 cu ft";

    if(volume.compare(100) <= 0)
      return "100 cu ft";

    return "a lot";
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------ asMetric --------------------------------

  /**
   * Set the weight as metric value.
   *
   * @param       inCubicMeters      the number of cubic meters
   * @param       inCubicCentimeters the number of cubic cm
   *
   * @return      the volume as metric
   *
   */
  public Volume asMetric(@Nullable Rational inCubicMeters,
                         @Nullable Rational inCubicCentimeters)
  {
    return as(new Rational [] { inCubicMeters, inCubicCentimeters }, 1);
  }

  //........................................................................
  //------------------------------- setFeet --------------------------------

  /**
   * Set the weight as feet value.
   *
   * @param       inCubicFeet   the number of cubic feet
   * @param       inCubicInches the number of cubic inches
   *
   * @return      the volume as feet
   *
   */
  public Volume asFeet(@Nullable Rational inCubicFeet,
                       @Nullable Rational inCubicInches)
  {
    return as(new Rational [] { inCubicFeet, inCubicInches }, 0);
  }

  //........................................................................
  //------------------------------ setGallons ------------------------------

  /**
   * Set the weight as feet value.
   *
   * @param       inGallons the number of gallons (may be null)
   * @param       inQuarts  the number of quarts (may be null)
   * @param       inPints   the number of Pints (may be null)
   * @param       inCups    the number of cups (may be null)
   * @param       inOunces  the number of ounces (may be null)
   *
   * @return      the volume as gallons
   *
   */
  public Volume asGallons(@Nullable Rational inGallons,
                          @Nullable Rational inQuarts,
                          @Nullable Rational inPints,
                          @Nullable Rational inCups,
                          @Nullable Rational inOunces)
  {
    return as(new Rational [] { inGallons, inQuarts, inPints, inCups,
                                inOunces }, 2);
  }

  //........................................................................
  //------------------------------ setLiters -------------------------------

  /**
   * Set the weight as feet value.
   *
   * @param       inLiters      the number of liters
   * @param       inDeziLiters  the number of dl
   * @param       inCentiLiters the number of cl
   *
   * @return      the volume as liters
   *
   */
  public Volume asLiters(@Nullable Rational inLiters,
                         @Nullable Rational inDeziLiters,
                         @Nullable Rational inCentiLiters)
  {
    return as(new Rational [] { inLiters, inDeziLiters, inCentiLiters }, 3);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  /**
   * Create a proto for the value.
   *
   * @return the proto representation
   */
  public VolumeProto toProto()
  {
    VolumeProto.Builder builder = VolumeProto.newBuilder();

    if(m_set == m_sets[0])
      if(m_values != null && m_values.length == 2)
      {
        VolumeProto.Imperial.Builder imperial =
          VolumeProto.Imperial.newBuilder();

        if(m_values[0] != null)
          imperial.setCubicFeet(m_values[0].toProto());
        if(m_values[1] != null)
          imperial.setCubicInches(m_values[1].toProto());

        builder.setImperial(imperial.build());
      }

    if(m_set == m_sets[1])
      if(m_values != null && m_values.length == 2)
      {
        VolumeProto.Metric.Builder metric = VolumeProto.Metric.newBuilder();

        if(m_values[0] != null)
          metric.setCubicMeters(m_values[0].toProto());
        if(m_values[1] != null)
          metric.setCubicDecimeters(m_values[1].toProto());

        builder.setMetric(metric.build());
      }

    if(m_set == m_sets[2])
      if(m_values != null && m_values.length == 4)
      {
        VolumeProto.Gallons.Builder metric = VolumeProto.Gallons.newBuilder();

        if(m_values[0] != null)
          metric.setGallons(m_values[0].toProto());
        if(m_values[1] != null)
          metric.setQuarts(m_values[1].toProto());
        if(m_values[2] != null)
          metric.setPints(m_values[2].toProto());
        if(m_values[3] != null)
          metric.setCups(m_values[3].toProto());

        builder.setGallons(metric.build());
      }

    if(m_set == m_sets[3])
      if(m_values != null && m_values.length == 3)
      {
        VolumeProto.Liters.Builder metric = VolumeProto.Liters.newBuilder();

        if(m_values[0] != null)
          metric.setLiters(m_values[0].toProto());
        if(m_values[1] != null)
          metric.setDeciliters(m_values[1].toProto());
        if(m_values[2] != null)
          metric.setCentiliters(m_values[2].toProto());

        builder.setLiters(metric.build());
      }

    return builder.build();
  }

  /**
   * Create a new volume similar to the current but with data from the
   * given proto.
   *
   * @param inProto  the proto with the data
   * @return the newly created volume
   */
  public Volume fromProto(VolumeProto inProto)
  {
    Volume result = create();

    if(inProto.hasMetric())
    {
      result.m_values = new Rational[2];
      if(inProto.getMetric().hasCubicMeters())
        result.m_values[0] =
          Rational.fromProto(inProto.getMetric().getCubicMeters());
      if(inProto.getMetric().hasCubicDecimeters())
        result.m_values[1] =
          Rational.fromProto(inProto.getMetric().getCubicDecimeters());
      result.m_set = result.m_sets[1];
    }
    else if(inProto.hasImperial())
    {
      result.m_values = new Rational[2];
      if(inProto.getImperial().hasCubicFeet())
        result.m_values[0] =
          Rational.fromProto(inProto.getImperial().getCubicFeet());
      if(inProto.getImperial().hasCubicInches())
        result.m_values[1] =
          Rational.fromProto(inProto.getImperial().getCubicInches());
      result.m_set = result.m_sets[0];
    }
    else if(inProto.hasGallons())
    {
      result.m_values = new Rational[4];
      if(inProto.getGallons().hasGallons())
        result.m_values[0] =
          Rational.fromProto(inProto.getGallons().getGallons());
      if(inProto.getGallons().hasQuarts())
        result.m_values[1] =
          Rational.fromProto(inProto.getGallons().getQuarts());
      if(inProto.getGallons().hasPints())
        result.m_values[2] =
          Rational.fromProto(inProto.getGallons().getPints());
      if(inProto.getGallons().hasCups())
        result.m_values[3] =
          Rational.fromProto(inProto.getGallons().getCups());
      result.m_set = result.m_sets[2];
    }
    else if(inProto.hasLiters())
    {
      result.m_values = new Rational[3];
      if(inProto.getLiters().hasLiters())
        result.m_values[0] =
          Rational.fromProto(inProto.getLiters().getLiters());
      if(inProto.getLiters().hasDeciliters())
        result.m_values[1] =
          Rational.fromProto(inProto.getLiters().getDeciliters());
      if(inProto.getLiters().hasCentiliters())
        result.m_values[2] =
          Rational.fromProto(inProto.getLiters().getCentiliters());
      result.m_set = result.m_sets[3];
    }

    return result;
  }

  //........................................................................

  //------------------------------------------------------------------- test

  /** Test test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Test init. */
    @org.junit.Test
      public void init()
    {
      Volume value = new Volume();

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
      assertEquals("undefined value not correct", "0",
                   value.getAsGallons().toString());
      assertEquals("undefined value not correct", "0",
                   value.getAsLiters().toString());

      // now with some value (cu cm)
      value = new Volume(null, new Rational(1, 1, 2), true);

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("string ", "1 1/2 cu dm", value.toString());
      assertEquals("feet",   false,  value.isFeet());
      assertEquals("metric", true,   value.isMetric());
      assertEquals("metric", false,   value.isLiquid());
      assertEquals("metric", false,   value.isLiquid());
      assertEquals("m",       "3/2000", value.getAsMeters().toString());
      assertEquals("ft",      "3/80", value.getAsFeet().toString());
      assertEquals("gallons", "9/32", value.getAsGallons().toString());
      assertEquals("liters",  "1 1/8", value.getAsLiters().toString());

      value = new Volume(new Rational(1, 2, 3), new Rational(2), false);

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("string", "1 2/3 cu ft 2 cu in", value.toString());
      assertEquals("feet",   true,  value.isFeet());
      assertEquals("metric", false, value.isMetric());
      assertEquals("metric", false,   value.isLiquid());
      assertEquals("m", "1441/21600", value.getAsMeters().toString());
      assertEquals("ft", "1 577/864", value.getAsFeet().toString());
      assertEquals("gallons", "50 5/144", value.getAsLiters().toString());
      assertEquals("liters", "12 293/576", value.getAsGallons().toString());

      // now with some value (metric)
      value = new Volume(new Rational(1), new Rational(1, 1, 2),
                         new Rational(2, 3), new Rational(4), null);

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("string", "1 gallon 1 1/2 quarts 2/3 pint 4 cups",
                   value.toString());
      assertEquals("feet",   true,  value.isFeet());
      assertEquals("metric", false,  value.isMetric());
      assertEquals("metric", true,   value.isLiquid());
      assertEquals("m",  "41/4500", value.getAsMeters().toString());
      assertEquals("ft",   "41/180", value.getAsFeet().toString());
      assertEquals("gallons", "6 5/6", value.getAsLiters().toString());
      assertEquals("liters", "1 17/24", value.getAsGallons().toString());

      value = new Volume(new Rational(1), new Rational(3, 1, 4), null);

      assertEquals("not defined at start", true, value.isDefined());
      assertEquals("string", "1 l 3 1/4 dl", value.toString());
      assertEquals("feet",   false, value.isFeet());
      assertEquals("metric", true,  value.isMetric());
      assertEquals("metric", true,   value.isLiquid());
      assertEquals("in", "53/1200", value.getAsFeet().toString());
      assertEquals("cm", "53/30000", value.getAsMeters().toString());
      assertEquals("gallons", "1 13/40", value.getAsLiters().toString());
      assertEquals("liters", "53/160", value.getAsGallons().toString());

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
          "simple", "1 cu m", "1 cu m", null,
          "no space", "5cu m 300cu     dm", "5 cu m 300 cu dm", null,
          "whites", "1 \n1/2    cu ft 5 \n cu \nin", "1 1/2 cu ft 5 cu in",
          null,

          "order", "30 cup 5 pint 10 gallons 1 pint",
          "10 gallons 6 pints 30 cups", null,

          "other text", "1cu m 5 cu dm 0 a", "1 cu m 5 cu dm", " 0 a",
          "nothing", "50", null, "50",
          "nothing", "22.3 cu cm", null, "22.3 cu cm",
          "nothing", "1/4 gu", null, "1/4 gu",
          "mixed", "5 cu ft 200 cu cm", "5 cu ft", " 200 cu cm",
        };

      Value.Test.readTest(tests, new Volume());
    }

    //......................................................................
    //----- set ------------------------------------------------------------

    /** Testing setting. */
    @org.junit.Test
    public void as()
    {
      Volume volume = new Volume();

      volume = volume.asMetric(new Rational(1, 3, 5), new Rational(1));
      assertEquals("set metric", "1 3/5 cu m 1 cu dm",
                   volume.toString());

      volume = volume.asFeet(new Rational(1, 3, 5), new Rational(1));
      assertEquals("set feet", "1 3/5 cu ft 1 cu in",
                   volume.toString());

      volume = volume.asGallons(new Rational(1, 3, 5), new Rational(1), null,
                                new Rational(1, 2), new Rational(3));
      assertEquals("set gallons", "1 3/5 gallons 1 quart 1/2 cup 3 ounces",
                   volume.toString());

      volume = volume.asLiters(new Rational(1, 3, 5), new Rational(1),
                               new Rational(1, 2));
      assertEquals("set liters", "1 3/5 l 1 dl 1/2 cl", volume.toString());
    }

    //......................................................................

    //----- metric ---------------------------------------------------------

    /** Test metric values. */
    @org.junit.Test
    public void metric()
    {
      String []texts =
        {
          "1 cu ft",         "40 cu dm",
          "1 cu in",         "5/216 cu dm",
          "5 cu in",         "25/216 cu dm",
          "3 cu ft",         "120 cu dm",
          "3 cu in 5 cu ft", "200 5/72 cu dm",
        };

      Volume value = new Volume();

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

    /** test feet values. */
    @org.junit.Test
    public void feet()
    {
      //       net.ixitxachitls.util.logging.Log.add
      //         ("test", new net.ixitxachitls.util.logging.ANSILogger());

      String []texts =
        {
          "1 cu m",           "25 cu ft",
          "1 1/2 cu dm",      "64 4/5 cu in",
          "1 cu m 300 cu dm", "32 1/2 cu ft",
          "2 cu m",           "50 cu ft",
        };

      Volume value = new Volume();

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
    //----- gallons --------------------------------------------------------

    /** Test gallong values. */
    @org.junit.Test
    public void gallons()
    {
      //       net.ixitxachitls.util.logging.Log.add
      //         ("test", new net.ixitxachitls.util.logging.ANSILogger());

      String []texts =
        {
          "1 liter",      "1 quart",
          "1 dl",         "3 1/5 ounces",
          "1 1/2 l 5 dl", "1/2 gallon",
          "2/3 dl",       "2 2/15 ounces",
        };

      Volume value = new Volume();

      for(int i = 0; i < texts.length; i += 2)
      {
        try (ParseReader reader =
          new ParseReader(new java.io.StringReader(texts[i]), "test"))
        {
          value = value.read(reader);
          assertEquals("test " + i / 2,
                       texts[i + 1], value.asGallons().toString());
        }
      }
    }

    //......................................................................
    //----- liters ---------------------------------------------------------

    /** Test liter values. */
    @org.junit.Test
    public void liters()
    {
      //       net.ixitxachitls.util.logging.Log.add
      //         ("test", new net.ixitxachitls.util.logging.ANSILogger());

      String []texts =
        {
          "1 gallon",                  "4 l",
          "1 quart",                   "1 l",
          "1 cup",                     "1/4 l",
          "1/2 gallon 1 quart 3 cups", "3 3/4 l",
        };

      Volume value = new Volume();

      for(int i = 0; i < texts.length; i += 2)
      {
        try (ParseReader reader =
          new ParseReader(new java.io.StringReader(texts[i]), "test"))
        {
          value = value.read(reader);
          assertEquals("test " + i / 2,
                       texts[i + 1], value.asLiters().toString());
        }
      }
    }

    //......................................................................
  }

  //........................................................................
}
