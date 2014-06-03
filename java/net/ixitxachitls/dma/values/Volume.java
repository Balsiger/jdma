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

package net.ixitxachitls.dma.values;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Values.VolumeProto;
import net.ixitxachitls.util.Strings;

/**
 * This class stores a volume. This is a convenience class for Units.
 *
 * @file          Volume.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@Immutable
@ParametersAreNonnullByDefault
public class Volume extends NewValue.Arithmetic<VolumeProto>
{
  public static final Parser<Volume> PARSER = new Parser<Volume>(1)
  {
    @Override
    protected Optional<Volume> doParse(String inValue)
    {
      if(inValue.trim().isEmpty())
        return Optional.absent();

      List<String []>parts =
        Strings.getAllPatterns(inValue,
                               "^(?:\\s*(.*?)"
                                 + "\\s*(cu ft|cubit foot|cubit feet"
                                 + "|cu in|cubic inch|cubic inches"
                                 + "|cu m|cubic meter|cubic meters"
                                 + "|cu dm|cubic decimeter|cubic decimeters"
                                 + "|cu cm|cubic centimeter|cubic centimeter"
                                 + "|gallon|gallons"
                                 + "|quart|quart"
                                 + "|pint|pints"
                                 + "|cup|cups"
                                 + "|ounce|ounces|oz"
                                 + "|liter|liters"
                                 + "|deciliter|deciliters"
                                 + "|centiliter|centiliters))\\s*$");
      if(parts.isEmpty())
        return Optional.absent();

      Optional<NewRational> feet = Optional.absent();
      Optional<NewRational> inches = Optional.absent();
      Optional<NewRational> meters = Optional.absent();
      Optional<NewRational> decimeters = Optional.absent();
      Optional<NewRational> centimeters = Optional.absent();
      Optional<NewRational> gallons = Optional.absent();
      Optional<NewRational> quarts = Optional.absent();
      Optional<NewRational> pints = Optional.absent();
      Optional<NewRational> cups = Optional.absent();
      Optional<NewRational> liters = Optional.absent();
      Optional<NewRational> deciliters = Optional.absent();
      Optional<NewRational> centiliters = Optional.absent();

      for(String []part : parts)
      {
        if(part.length != 2)
          return Optional.absent();

        Optional<NewRational> number = NewRational.PARSER.parse(part[0]);
        if(!number.isPresent())
          return Optional.absent();

        switch(part[1].toLowerCase())
        {
          case "cu ft":
          case "cubic foot":
          case "cubic feet":
            feet = add(feet, number);
            break;

          case "cu in":
          case "cubic inch":
          case "cubic inches":
            inches = add(inches, number);
            break;

          case "cu m":
          case "cubic meter":
          case "cubic meters":
            meters = add(meters, number);
            break;

          case "cu dm":
          case "cubic decimeter":
          case "cubic decimeters":
            decimeters = add(decimeters, number);
            break;

          case "cu cm":
          case "cubic centimeter":
          case "cubic centimeters":
            centimeters = add(centimeters, number);
            break;

          case "gallon":
          case "gallons":
            gallons = add(gallons, number);
            break;

          case "quart":
          case "quarts":
            quarts = add(quarts, number);
            break;

          case "pint":
          case "pints":
            gallons = add(gallons, number);
            break;

          case "cup":
          case "cups":
            cups = add(cups, number);
            break;

          case "liter":
          case "liters":
            liters = add(liters, number);
            break;

          case "deciliter":
          case "deciliters":
            deciliters = add(deciliters, number);
            break;

          case "centiiliter":
          case "centiliters":
            centiliters = add(centiliters, number);
            break;
        }
      }

      return Optional.of(new Volume(feet, inches,
                                    meters, decimeters, centimeters,
                                    gallons, quarts, pints, cups,
                                    liters, deciliters, centiliters));
    }
  };

  /**
   * Construct the volume object with an undefined value.
   */
  public Volume(Optional<NewRational> inFeet,
                Optional<NewRational> inInches,
                Optional<NewRational> inMeters,
                Optional<NewRational> inDecimeters,
                Optional<NewRational> inCentimeters,
                Optional<NewRational> inGallons,
                Optional<NewRational> inQuarts,
                Optional<NewRational> inPints,
                Optional<NewRational> inCups,
                Optional<NewRational> inLiters,
                Optional<NewRational> inDeciliters,
                Optional<NewRational> inCentiliters)
  {
    m_feet = inFeet;
    m_inches = inInches;
    m_meters = inMeters;
    m_decimeters = inDecimeters;
    m_centimeters = inCentimeters;
    m_gallons = inGallons;
    m_quarts = inQuarts;
    m_pints = inPints;
    m_cups = inCups;
    m_liters = inLiters;
    m_deciliters = inDeciliters;
    m_centiliters = inCentiliters;
  }

  private final Optional<NewRational> m_feet;
  private final Optional<NewRational> m_inches;
  private final Optional<NewRational> m_meters;
  private final Optional<NewRational> m_decimeters;
  private final Optional<NewRational> m_centimeters;
  private final Optional<NewRational> m_gallons;
  private final Optional<NewRational> m_quarts;
  private final Optional<NewRational> m_pints;
  private final Optional<NewRational> m_cups;
  private final Optional<NewRational> m_liters;
  private final Optional<NewRational> m_deciliters;
  private final Optional<NewRational> m_centiliters;

  /**
   * Determine if metric values are stored.
   *
   * @return      true if metric values are stored, false else
   */
  public boolean isMetric()
  {
    return m_meters.isPresent() || m_decimeters.isPresent()
      || m_centimeters.isPresent()
      || m_liters.isPresent() || m_deciliters.isPresent()
      || m_centiliters.isPresent();
  }

  /**
   * Determine if imperial values are stored.
   *
   * @return      true if feet values are stored, false else
   */
  public boolean isImperial()
  {
    return m_feet.isPresent() || m_inches.isPresent()
      || m_gallons.isPresent() || m_quarts.isPresent() || m_cups.isPresent()
      || m_pints.isPresent();
  }

  /**
   * Determine if liquid values are stored.
   *
   * @return      true if liquid values are stored, false else
   */
  public boolean isLiquid()
  {
    return m_gallons.isPresent() || m_quarts.isPresent() || m_cups.isPresent()
      || m_pints.isPresent()
      || m_liters.isPresent() || m_deciliters.isPresent()
      || m_centiliters.isPresent();
  }

  @Override
  public String toString()
  {
    List<String> parts = new ArrayList<>();

    if(m_feet.isPresent())
      parts.add(m_feet.get() + " cu feet");

    if(m_inches.isPresent())
      parts.add(m_inches.get() + " cu inches");

    if(m_meters.isPresent())
      parts.add(m_meters.get() + " cu meters");

    if(m_decimeters.isPresent())
      parts.add(m_decimeters.get() + " cu decimeters");

    if(m_centimeters.isPresent())
      parts.add(m_centimeters.get() + " cu centimeters");

    if(m_gallons.isPresent())
      parts.add(m_gallons.get() + " gallons");

    if(m_quarts.isPresent())
      parts.add(m_quarts.get() + " quarts");

    if(m_pints.isPresent())
      parts.add(m_pints.get() + " pints");

    if(m_cups.isPresent())
      parts.add(m_cups.get() + " cups");

    if(m_liters.isPresent())
      parts.add(m_liters.get() + " liters");

    if(m_deciliters.isPresent())
      parts.add(m_deciliters.get() + " deciliters");

    if(m_centiliters.isPresent())
      parts.add(m_centiliters.get() + " centiliters");

    if(parts.isEmpty())
      return "0 cu feet";

    return Strings.SPACE_JOINER.join(parts);
  }

    @Override
  public String toShortString()
  {
    List<String> parts = new ArrayList<>();

    if(m_feet.isPresent())
      parts.add(m_feet.get() + "ft&sup3;");

    if(m_inches.isPresent())
      parts.add(m_inches.get() + "in&sup3;");

    if(m_meters.isPresent())
      parts.add(m_meters.get() + "m&sup3;");

    if(m_decimeters.isPresent())
      parts.add(m_decimeters.get() + "dm&sup3;");

    if(m_centimeters.isPresent())
      parts.add(m_centimeters.get() + "cm&sup3;");

    if(m_gallons.isPresent())
      parts.add(m_gallons.get() + "gal");

    if(m_quarts.isPresent())
      parts.add(m_quarts.get() + "qt");

    if(m_pints.isPresent())
      parts.add(m_pints.get() + "pt");

    if(m_cups.isPresent())
      parts.add(m_cups.get() + "c");

    if(m_liters.isPresent())
      parts.add(m_liters.get() + "l");

    if(m_deciliters.isPresent())
      parts.add(m_deciliters.get() + "dl");

    if(m_centiliters.isPresent())
      parts.add(m_centiliters.get() + "cl");

    if(parts.isEmpty())
      return "0ft&sup3;";

    return Strings.SPACE_JOINER.join(parts);
  }

  /**
   * Create a proto for the value.
   *
   * @return the proto representation
   */
  @Override
  public VolumeProto toProto()
  {
    VolumeProto.Builder builder = VolumeProto.newBuilder();

    if(isImperial() && !isLiquid())
    {
      VolumeProto.Imperial.Builder imperial = VolumeProto.Imperial.newBuilder();

      if(m_feet.isPresent())
        imperial.setCubicFeet(m_feet.get().toProto());
      if(m_inches.isPresent())
        imperial.setCubicInches(m_inches.get().toProto());

      builder.setImperial(imperial.build());
    }

    if(isMetric() && !isLiquid())
    {
      VolumeProto.Metric.Builder metric = VolumeProto.Metric.newBuilder();

      if(m_meters.isPresent())
        metric.setCubicMeters(m_meters.get().toProto());
      if(m_decimeters.isPresent())
        metric.setCubicDecimeters(m_decimeters.get().toProto());
      if(m_centimeters.isPresent())
        metric.setCubicCentimeters(m_centimeters.get().toProto());

        builder.setMetric(metric.build());
      }

    if(isImperial() && isLiquid())
    {
      VolumeProto.Gallons.Builder metric = VolumeProto.Gallons.newBuilder();

      if(m_gallons.isPresent())
        metric.setGallons(m_gallons.get().toProto());
      if(m_quarts.isPresent())
        metric.setQuarts(m_quarts.get().toProto());
      if(m_pints.isPresent())
        metric.setPints(m_pints.get().toProto());
      if(m_cups.isPresent())
        metric.setCups(m_cups.get().toProto());

      builder.setGallons(metric.build());
    }

    if(isMetric() && isLiquid())
    {
      VolumeProto.Liters.Builder metric = VolumeProto.Liters.newBuilder();

      if(m_liters.isPresent())
        metric.setLiters(m_liters.get().toProto());
      if(m_deciliters.isPresent())
        metric.setDeciliters(m_deciliters.get().toProto());
      if(m_centiliters.isPresent())
        metric.setCentiliters(m_centiliters.get().toProto());

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
  public static Volume fromProto(VolumeProto inProto)
  {
    Optional<NewRational> feet = Optional.absent();
    Optional<NewRational> inches = Optional.absent();
    Optional<NewRational> meters = Optional.absent();
    Optional<NewRational> decimeters = Optional.absent();
    Optional<NewRational> centimeters = Optional.absent();
    Optional<NewRational> gallons = Optional.absent();
    Optional<NewRational> quarts = Optional.absent();
    Optional<NewRational> pints = Optional.absent();
    Optional<NewRational> cups = Optional.absent();
    Optional<NewRational> liters = Optional.absent();
    Optional<NewRational> deciliters = Optional.absent();
    Optional<NewRational> centiliters = Optional.absent();

    if(inProto.hasMetric())
    {
      if(inProto.getMetric().hasCubicMeters())
        meters = Optional.of(NewRational.fromProto
                             (inProto.getMetric().getCubicMeters()));
      if(inProto.getMetric().hasCubicDecimeters())
        decimeters = Optional.of(NewRational.fromProto
                                 (inProto.getMetric().getCubicDecimeters()));
      if(inProto.getMetric().hasCubicCentimeters())
        centimeters = Optional.of(NewRational.fromProto
                                  (inProto.getMetric().getCubicCentimeters()));
    }

    if(inProto.hasImperial())
    {
      if(inProto.getImperial().hasCubicFeet())
        feet = Optional.of(NewRational.fromProto
                           (inProto.getImperial().getCubicFeet()));
      if(inProto.getImperial().hasCubicInches())
        inches = Optional.of(NewRational.fromProto
                             (inProto.getImperial().getCubicInches()));
    }

    if(inProto.hasGallons())
    {
      if(inProto.getGallons().hasGallons())
        gallons = Optional.of(NewRational.fromProto
                              (inProto.getGallons().getGallons()));
      if(inProto.getGallons().hasQuarts())
        quarts = Optional.of(NewRational.fromProto
                             (inProto.getGallons().getQuarts()));
      if(inProto.getGallons().hasPints())
        pints = Optional.of(NewRational.fromProto
                            (inProto.getGallons().getPints()));
      if(inProto.getGallons().hasCups())
        cups = Optional.of(NewRational.fromProto
                           (inProto.getGallons().getCups()));
    }

    if(inProto.hasLiters())
    {
      if(inProto.getLiters().hasLiters())
        liters = Optional.of(NewRational.fromProto
                             (inProto.getLiters().getLiters()));
      if(inProto.getLiters().hasDeciliters())
        deciliters = Optional.of(NewRational.fromProto
                                 (inProto.getLiters().getDeciliters()));
      if(inProto.getLiters().hasCentiliters())
        centiliters = Optional.of(NewRational.fromProto
                                  (inProto.getLiters().getCentiliters()));
    }

    return new Volume(feet, inches,
                      meters, decimeters, centimeters,
                      gallons, quarts, pints, cups,
                      liters, deciliters, centiliters);
  }

  @Override
  public Arithmetic<VolumeProto> add(Arithmetic<VolumeProto> inValue)
  {
    if(inValue == null)
      return this;

    if(!(inValue instanceof Volume))
      throw new IllegalArgumentException("can only add another volume value");

    Volume value = (Volume)inValue;
    return new Volume(add(m_feet, value.m_feet),
                      add(m_inches, value.m_inches),
                      add(m_meters, value.m_meters),
                      add(m_decimeters, value.m_decimeters),
                      add(m_centimeters, value.m_centimeters),
                      add(m_gallons, value.m_gallons),
                      add(m_quarts, value.m_quarts),
                      add(m_pints, value.m_pints),
                      add(m_cups, value.m_cups),
                      add(m_liters, value.m_liters),
                      add(m_deciliters, value.m_deciliters),
                      add(m_centiliters, value.m_centiliters));
  }

  @Override
  public Arithmetic<VolumeProto> multiply(int inFactor)
  {
    return new Volume(multiply(m_feet, inFactor),
                      multiply(m_inches, inFactor),
                      multiply(m_meters, inFactor),
                      multiply(m_decimeters, inFactor),
                      multiply(m_centimeters, inFactor),
                      multiply(m_gallons, inFactor),
                      multiply(m_quarts, inFactor),
                      multiply(m_pints, inFactor),
                      multiply(m_cups, inFactor),
                      multiply(m_liters, inFactor),
                      multiply(m_deciliters, inFactor),
                      multiply(m_centiliters, inFactor));
  }

  @Override
  public boolean canAdd(NewValue.Arithmetic<VolumeProto> inValue)
  {
    return inValue instanceof Volume;
  }

  //---------------------------------------------------------------------------

  /** Test test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Test init. */
    @org.junit.Test
    public void init()
    {
      Volume value = new Volume(Optional.<NewRational>absent(),
                                Optional.<NewRational>absent(),
                                Optional.<NewRational>absent(),
                                Optional.<NewRational>absent(),
                                Optional.<NewRational>absent(),
                                Optional.<NewRational>absent(),
                                Optional.<NewRational>absent(),
                                Optional.<NewRational>absent(),
                                Optional.<NewRational>absent(),
                                Optional.<NewRational>absent(),
                                Optional.<NewRational>absent(),
                                Optional.<NewRational>absent());

      // undefined value
      assertEquals("undefined value not correct", "0 cu ft",
                   value.toString());
      assertEquals("feet",   false, value.isImperial());
      assertEquals("metric", false, value.isMetric());

      // now with some value (cu cm)
      value = new Volume(Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.of(new NewRational(1, 1, 2)),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent());

      assertEquals("string ", "1 1/2 cu dm", value.toString());
      assertEquals("feet",   false,  value.isImperial());
      assertEquals("metric", true,   value.isMetric());
      assertEquals("liquid", false,   value.isLiquid());

      value = new Volume(Optional.of(new NewRational(1, 2, 3)),
                         Optional.of(new NewRational(2, 0, 0)),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent());

      assertEquals("string", "1 2/3 cu ft 2 cu in", value.toString());
      assertEquals("feet",   true,  value.isImperial());
      assertEquals("metric", false, value.isMetric());
      assertEquals("metric", false,   value.isLiquid());

      // now with some value (metric)
      value = new Volume(Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.of(new NewRational(1, 0, 0)),
                         Optional.of(new NewRational(1, 1, 2)),
                         Optional.of(new NewRational(0, 2, 3)),
                         Optional.of(new NewRational(4, 0, 0)),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent());

      assertEquals("string", "1 gallon 1 1/2 quarts 2/3 pint 4 cups",
                   value.toString());
      assertEquals("feet",   true,  value.isImperial());
      assertEquals("metric", false,  value.isMetric());
      assertEquals("metric", true,   value.isLiquid());

      value = new Volume(Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.<NewRational>absent(),
                         Optional.of(new NewRational(1, 0, 0)),
                         Optional.of(new NewRational(3, 1, 4)),
                         Optional.<NewRational>absent());

      assertEquals("string", "1 l 3 1/4 dl", value.toString());
      assertEquals("feet",   false, value.isImperial());
      assertEquals("metric", true,  value.isMetric());
      assertEquals("metric", true,   value.isLiquid());
    }

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

      //Value.Test.readTest(tests, new Volume());
    }
  }
}
