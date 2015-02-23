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

package net.ixitxachitls.dma.rules;

import net.ixitxachitls.dma.values.enums.Size;

/**
 * Rules about carrying capacity.
 *
 * @file   CarryingCapacity.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public final class CarryingCapacity
{
  private CarryingCapacity()
  {
    // prevent instantiation
  }

  /** The light carrying capacities per strength score. */
  protected static final int []LIGHT_LOAD =
      {
          0,   3,   6,  10,  13,  16,  20,  23,  26,  30,
         33,  38,  43,  50,  58,  66,  76,  86, 100, 116,
        133, 153, 173, 200, 233, 266, 306, 346, 400, 466,
      };

  /** The medium carrying capacities per strength score. */
  protected static final int []MEDIUM_LOAD =
      {
            0,   6,  13,  20,  26,  33,  40,  46,  53,  60,
           66,  76,  86, 100, 116, 133, 153, 173, 200, 233,
          266, 306, 346, 400, 466, 533, 613, 693, 800, 933,
       };

  /** The heavy carrying capacities per strength score. */
  protected static final int []HEAVY_LOAD =
      {
            0,  10,  20,  30,  40,  50,  60,   70,   80,   90,
          100, 115, 130, 150, 175, 200, 230,  260,  300,  350,
          400, 460, 520, 600, 700, 800, 900, 1040, 1200, 1400,
      };

  public static int lightLoad(int inStrength, Size inSize, boolean inBipedal)
  {
    return (int) (lightLoad(inStrength) * sizeFactor(inSize, inBipedal));
  }

  public static int mediumLoad(int inStrength, Size inSize, boolean inBipedal)
  {
    return (int) (mediumLoad(inStrength) * sizeFactor(inSize, inBipedal));
  }

  public static int heavyLoad(int inStrength, Size inSize, boolean inBipedal)
  {
    return (int) (heavyLoad(inStrength) * sizeFactor(inSize, inBipedal));
  }

  public static int liftLoad(int inStrength, Size inSize, boolean inBipedal)
  {
    return heavyLoad(inStrength, inSize, inBipedal);
  }

  public static int dragLoad(int inStrength, Size inSize, boolean inBipedal)
  {
    return heavyLoad(inStrength, inSize, inBipedal) * 5;
  }

  private static int lightLoad(int inStrength)
  {
    if(inStrength < LIGHT_LOAD.length)
      return LIGHT_LOAD[inStrength];

    return lightLoad(inStrength - 10) * 4;
  }

  private static int mediumLoad(int inStrength)
  {
    if(inStrength < MEDIUM_LOAD.length)
      return MEDIUM_LOAD[inStrength];

    return mediumLoad(inStrength - 10) * 4;
  }

  private static int heavyLoad(int inStrength)
  {
    if(inStrength < HEAVY_LOAD.length)
      return HEAVY_LOAD[inStrength];

    return heavyLoad(inStrength - 10) * 4;
  }

  private static double sizeFactor(Size inSize, boolean inBipedal)
  {
    return
        inBipedal ? bipedalSizeFactor(inSize) : quadrupedalSizeFactor(inSize);
  }

  private static double bipedalSizeFactor(Size inSize)
  {
    switch(inSize)
    {
      default:
      case UNKNOWN:
      case MEDIUM:
        return 1.0;

      case FINE:
        return 1.0/8;

      case DIMINUTIVE:
        return 1.0/4;

      case TINY:
        return 1.0/2;

      case SMALL:
        return 3.0/4;

      case LARGE:
        return 2.0;

      case HUGE:
        return 4.0;

      case GARGANTUAN:
        return 8.0;

      case COLOSSAL:
        return 16.0;
    }
  }

  private static double quadrupedalSizeFactor(Size inSize)
  {
    switch(inSize)
    {
      default:
      case UNKNOWN:
      case MEDIUM:
        return 3.0/2;

      case FINE:
        return 1.0/4;

      case DIMINUTIVE:
        return 1.0/2;

      case TINY:
        return 3.0/4;

      case SMALL:
        return 1.0;

      case LARGE:
        return 3.0;

      case HUGE:
        return 6.0;

      case GARGANTUAN:
        return 12.0;

      case COLOSSAL:
        return 24.0;
    }
  }

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** The test. */
    @org.junit.Test
    public void test()
    {
      // Tordek
      assertEquals("light",   66, lightLoad(15, Size.MEDIUM, true));
      assertEquals("medium", 133, mediumLoad(15, Size.MEDIUM, true));
      assertEquals("heavy",  200, heavyLoad(15, Size.MEDIUM, true));

      // Mialee
      assertEquals("light",   33, lightLoad(10, Size.MEDIUM, true));
      assertEquals("medium",  66, mediumLoad(10, Size.MEDIUM, true));
      assertEquals("heavy",  100, heavyLoad(10, Size.MEDIUM, true));

      // Lidda
      assertEquals("light",   24, lightLoad(10, Size.SMALL, true));
      assertEquals("medium",  49, mediumLoad(10, Size.SMALL, true));
      assertEquals("heavy",   75, heavyLoad(10, Size.SMALL, true));

      // Donkey
      assertEquals("light",   49, lightLoad(10, Size.MEDIUM, false));
      assertEquals("medium",  99, mediumLoad(10, Size.MEDIUM, false));
      assertEquals("heavy",  150, heavyLoad(10, Size.MEDIUM, false));

      // Cloud Giant
      assertEquals("light",   4256, lightLoad(35, Size.HUGE, true));
      assertEquals("medium",  8528, mediumLoad(35, Size.HUGE, true));
      assertEquals("heavy",  12800, heavyLoad(35, Size.HUGE, true));
    }
  }
}
