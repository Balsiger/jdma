/*******************************************************************************
 * Copyright (c) 2002-2016 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.server.servlets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.appengine.labs.repackaged.com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import com.google.template.soy.data.SoyData;

import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.dma.values.Dice;

/**
 * Servlet for simple treasure generation.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class TreasureServlet extends PageServlet
{
  public TreasureServlet() {}

  private static final long serialVersionUID = 1L;
  private static final Random RANDOM = new Random();

  @Override
  public boolean isPublic(DMARequest inRequest)
  {
    return true;
  }

  @Override
  protected String getTemplateName(DMARequest inRequest,
                                   Map<String, SoyData> inData)
  {
    return "dma.page.treasure";
  }

  @Override
  protected Map<String, Object> collectData(DMARequest inRequest,
                                            SoyRenderer inRenderer)
  {
    Map<String, Object> data = super.collectData(inRequest, inRenderer);

    int factor = inRequest.getParam("x", 1);
    int level = inRequest.getParam("l", 1);
    int number = inRequest.getParam("n", 1);

    data.put("factor", factor);
    data.put("level", level);
    data.put("number", number);

    List<Map<String, String>> treasures = new ArrayList<>();
    for(int i = 0; i < number; i++)
      treasures.add(
          ImmutableMap.<String, String>builder()
              .put("coins", roll(random(getValue(COINS, level)), factor))
              .put("goods", roll(random(getValue(GOODS, level)), factor))
              .put("items", roll(random(getValue(ITEMS, level)), factor))
              .build());

    data.put("treasures", treasures);

    return data;
  }

  private List<Treasure> getValue(List<ImmutableList<Treasure>> inValues, int inIndex)
  {
    if (inIndex >= inValues.size())
      return inValues.get(inValues.size() -1);

    return inValues.get(inIndex);
  }

  private String roll(Treasure inTreasure, int factor)
  {
    if(inTreasure.m_type == Treasure.Type.none)
      return "-";

    String details;
    switch(inTreasure.m_type)
    {
      case none:
      case cp:
      case sp:
      case gp:
      case pp:
        details = "";
        break;

      case gem:
        Gem gem = random(GEMS);
        details = " (" + random(GEMS). + ")";
        break;

      case art:
        details = "to be implemented";
        break;
      case mundane:
        details = "to be implemented";
        break;
      case minor:
        details = "to be implemented";
        break;
      case medium:
        details = "to be implemented";
        break;
      case major:
        details = "to be implemented";
        break;
    }

    return inTreasure.m_random.roll() * factor + " " + inTreasure.m_type
        + details;
  }

  private <T extends Value> T random(List<T> inValues)
  {
    int random = RANDOM.nextInt(100) + 1;
    for(T value : inValues)
      if(value.getLimit() >= random)
        return value;

    // should never happen
    return inValues.get(0);
  }

  private static class Value
  {
    private Value(int inLimit)
    {
      m_limit = inLimit;
    }

    private int m_limit;

    public int getLimit()
    {
      return m_limit;
    }
  }

  private static class Treasure extends Value
  {
    public enum Type
    {
      none, cp, sp, gp, pp, gem, art, mundane, minor, medium, major,
    };

    private Treasure(int inLimit, Dice inRandom, int factor, Type inType)
    {
      super(inLimit);
      m_random = inRandom;
      m_factor = factor;
      m_type = inType;
    }

    private final Dice m_random;
    private int m_factor;
    private final Type m_type;
  }

  private static final
  ImmutableList<ImmutableList<Treasure>> COINS = ImmutableList.of(
      // 1
      ImmutableList.of(new Treasure(14, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(29, new Dice(1, 6), 1000, Treasure.Type.cp),
                       new Treasure(52, new Dice(1, 8), 100, Treasure.Type.sp),
                       new Treasure(95, new Dice(2, 8), 10, Treasure.Type.gp),
                       new Treasure(100, new Dice(1, 4), 10, Treasure.Type.pp)),
      // 2
      ImmutableList.of(new Treasure(13, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(23, new Dice(1, 10), 1000, Treasure.Type.cp),
                       new Treasure(43, new Dice(2, 10), 100, Treasure.Type.sp),
                       new Treasure(95, new Dice(4, 10), 10, Treasure.Type.gp),
                       new Treasure(100, new Dice(2, 8), 10, Treasure.Type.pp)),
      // 3
      ImmutableList.of(new Treasure(11, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(21, new Dice(2, 10), 1000, Treasure.Type.cp),
                       new Treasure(41, new Dice(4, 8), 100, Treasure.Type.sp),
                       new Treasure(95, new Dice(1, 4), 100, Treasure.Type.gp),
                       new Treasure(100, new Dice(1, 10), 10, Treasure.Type.pp)),
      // 4
      ImmutableList.of(new Treasure(11, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(21, new Dice(3, 10), 1000, Treasure.Type.cp),
                       new Treasure(41, new Dice(4, 12), 100, Treasure.Type.sp),
                       new Treasure(95, new Dice(1, 6), 100, Treasure.Type.gp),
                       new Treasure(100, new Dice(1, 8), 10, Treasure.Type.pp)),
      // 5
      ImmutableList.of(new Treasure(10, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(19, new Dice(1, 4), 10000, Treasure.Type.cp),
                       new Treasure(38, new Dice(1, 6), 1000, Treasure.Type.sp),
                       new Treasure(95, new Dice(1, 8), 100, Treasure.Type.gp),
                       new Treasure(100, new Dice(1, 10), 10, Treasure.Type.pp)),
      // 6
      ImmutableList.of(new Treasure(10, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(18, new Dice(1, 6), 10000, Treasure.Type.cp),
                       new Treasure(37, new Dice(1, 8), 1000, Treasure.Type.sp),
                       new Treasure(95, new Dice(1, 10), 100, Treasure.Type.gp),
                       new Treasure(100, new Dice(1, 12), 10, Treasure.Type.pp)),
      // 7
      ImmutableList.of(new Treasure(10, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(18, new Dice(1, 10), 10000, Treasure.Type.cp),
                       new Treasure(35, new Dice(1, 12), 1000, Treasure.Type.sp),
                       new Treasure(93, new Dice(2, 6), 100, Treasure.Type.gp),
                       new Treasure(100, new Dice(3, 4), 10, Treasure.Type.pp)),
      // 8
      ImmutableList.of(new Treasure(10, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(15, new Dice(1, 12), 10000, Treasure.Type.cp),
                       new Treasure(29, new Dice(2, 6), 1000, Treasure.Type.sp),
                       new Treasure(87, new Dice(2, 8), 100, Treasure.Type.gp),
                       new Treasure(100, new Dice(3, 6), 10, Treasure.Type.pp)),
      // 9
      ImmutableList.of(new Treasure(10, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(15, new Dice(2, 6), 10000, Treasure.Type.cp),
                       new Treasure(29, new Dice(2, 8), 1000, Treasure.Type.sp),
                       new Treasure(85, new Dice(5, 3), 100, Treasure.Type.gp),
                       new Treasure(100, new Dice(2, 10), 10, Treasure.Type.pp)),
      // 10
      ImmutableList.of(new Treasure(10, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(24, new Dice(2, 10), 1000, Treasure.Type.sp),
                       new Treasure(79, new Dice(6, 4), 100, Treasure.Type.gp),
                       new Treasure(100, new Dice(5, 6), 10, Treasure.Type.pp)),
      // 11
      ImmutableList.of(new Treasure(8, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(14, new Dice(3, 10), 1000, Treasure.Type.sp),
                       new Treasure(75, new Dice(4, 8), 100, Treasure.Type.gp),
                       new Treasure(100, new Dice(4, 10), 10, Treasure.Type.pp)),
      // 12
      ImmutableList.of(new Treasure(8, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(14, new Dice(3, 12), 1000, Treasure.Type.sp),
                       new Treasure(75, new Dice(1, 4), 1000, Treasure.Type.gp),
                       new Treasure(100, new Dice(1, 4), 100, Treasure.Type.pp)),
      // 13
      ImmutableList.of(new Treasure(8, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(75, new Dice(1, 4), 1000, Treasure.Type.gp),
                       new Treasure(100, new Dice(1, 10), 100, Treasure.Type.pp)),
      // 14
      ImmutableList.of(new Treasure(8, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(75, new Dice(1, 6), 1000, Treasure.Type.gp),
                       new Treasure(100, new Dice(1, 12), 100, Treasure.Type.pp)),
      // 15
      ImmutableList.of(new Treasure(3, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(74, new Dice(1, 8), 1000, Treasure.Type.gp),
                       new Treasure(100, new Dice(3, 4), 100, Treasure.Type.pp)),
      // 16
      ImmutableList.of(new Treasure(3, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(74, new Dice(1, 12), 1000, Treasure.Type.gp),
                       new Treasure(100, new Dice(3, 4), 100, Treasure.Type.pp)),
      // 17
      ImmutableList.of(new Treasure(3, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(68, new Dice(3, 4), 1000, Treasure.Type.gp),
                       new Treasure(100, new Dice(2, 10), 100, Treasure.Type.pp)),
      // 18
      ImmutableList.of(new Treasure(2, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(65, new Dice(3, 6), 1000, Treasure.Type.gp),
                       new Treasure(100, new Dice(5, 4), 100, Treasure.Type.pp)),
      // 19
      ImmutableList.of(new Treasure(2, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(65, new Dice(3, 8), 1000, Treasure.Type.gp),
                       new Treasure(100, new Dice(3, 10), 100, Treasure.Type.pp)),
      // 20
      ImmutableList.of(new Treasure(2, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(65, new Dice(4, 8), 1000, Treasure.Type.gp),
                       new Treasure(100, new Dice(4, 10), 100, Treasure.Type.pp))
  );

  private static final
  ImmutableList<ImmutableList<Treasure>> GOODS = ImmutableList.of(
      // 1
      ImmutableList.of(new Treasure(90, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(95, new Dice(1), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.art)),
      // 2
      ImmutableList.of(new Treasure(81, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(95, new Dice(1, 3), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(1, 3), 1, Treasure.Type.art)),
      // 3
      ImmutableList.of(new Treasure(77, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(95, new Dice(1, 3), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(1, 3), 1, Treasure.Type.art)),
      // 4
      ImmutableList.of(new Treasure(70, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(95, new Dice(1, 4), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(1, 3), 1, Treasure.Type.art)),
      // 5
      ImmutableList.of(new Treasure(60, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(95, new Dice(1, 4), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(1, 4), 1, Treasure.Type.art)),
      // 6
      ImmutableList.of(new Treasure(56, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(92, new Dice(1, 4), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(1, 4), 1, Treasure.Type.art)),
      // 7
      ImmutableList.of(new Treasure(48, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(88, new Dice(1, 4), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(1, 4), 1, Treasure.Type.art)),
      // 8
      ImmutableList.of(new Treasure(45, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(85, new Dice(1, 6), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(1, 4), 1, Treasure.Type.art)),
      // 9
      ImmutableList.of(new Treasure(40, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(80, new Dice(1, 8), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(1, 4), 1, Treasure.Type.art)),
      // 10
      ImmutableList.of(new Treasure(35, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(79, new Dice(1, 8), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(1, 6), 1, Treasure.Type.art)),
      // 11
      ImmutableList.of(new Treasure(24, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(74, new Dice(1, 10), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(1, 6), 1, Treasure.Type.art)),
      // 12
      ImmutableList.of(new Treasure(17, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(70, new Dice(1, 10), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(1, 8), 1, Treasure.Type.art)),
      // 13
      ImmutableList.of(new Treasure(11, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(66, new Dice(1, 12), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(1, 10), 1, Treasure.Type.art)),
      // 14
      ImmutableList.of(new Treasure(11, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(66, new Dice(2, 8), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(2, 6), 1, Treasure.Type.art)),
      // 15
      ImmutableList.of(new Treasure(9, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(65, new Dice(2, 10), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(2, 8), 1, Treasure.Type.art)),
      // 16
      ImmutableList.of(new Treasure(7, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(64, new Dice(4, 6), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(2, 10), 1, Treasure.Type.art)),
      // 17
      ImmutableList.of(new Treasure(4, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(63, new Dice(4, 8), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(3, 8), 1, Treasure.Type.art)),
      // 18
      ImmutableList.of(new Treasure(4, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(54, new Dice(3, 12), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(3, 10), 1, Treasure.Type.art)),
      // 19
      ImmutableList.of(new Treasure(3, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(50, new Dice(6, 6), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(6, 6), 1, Treasure.Type.art)),
      // 29
      ImmutableList.of(new Treasure(2, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(38, new Dice(4, 10), 1, Treasure.Type.gem),
                       new Treasure(100, new Dice(7, 6), 1, Treasure.Type.art))
  );

  private static final
  ImmutableList<ImmutableList<Treasure>> ITEMS = ImmutableList.of(
      // 1
      ImmutableList.of(new Treasure(71, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(95, new Dice(1), 1, Treasure.Type.mundane),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.minor)),
      // 2
      ImmutableList.of(new Treasure(49, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(85, new Dice(1), 1, Treasure.Type.mundane),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.minor)),
      // 3
      ImmutableList.of(new Treasure(49, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(79, new Dice(1, 3), 1, Treasure.Type.mundane),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.minor)),
      // 4
      ImmutableList.of(new Treasure(42, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(62, new Dice(1, 4), 1, Treasure.Type.mundane),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.minor)),
      // 5
      ImmutableList.of(new Treasure(57, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(67, new Dice(1, 4), 1, Treasure.Type.mundane),
                       new Treasure(100, new Dice(1, 3), 1, Treasure.Type.minor)),
      // 6
      ImmutableList.of(new Treasure(54, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(59, new Dice(1, 4), 1, Treasure.Type.mundane),
                       new Treasure(99, new Dice(1, 3), 1, Treasure.Type.minor),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.medium)),
      // 7
      ImmutableList.of(new Treasure(51, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(97, new Dice(1, 3), 1, Treasure.Type.minor),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.medium)),
      // 8
      ImmutableList.of(new Treasure(48, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(96, new Dice(1, 4), 1, Treasure.Type.minor),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.medium)),
      // 9
      ImmutableList.of(new Treasure(43, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(91, new Dice(1, 4), 1, Treasure.Type.minor),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.medium)),
      // 10
      ImmutableList.of(new Treasure(40, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(88, new Dice(1, 4), 1, Treasure.Type.minor),
                       new Treasure(99, new Dice(1), 1, Treasure.Type.medium),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.major)),
      // 11
      ImmutableList.of(new Treasure(31, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(84, new Dice(1, 4), 1, Treasure.Type.minor),
                       new Treasure(98, new Dice(1), 1, Treasure.Type.medium),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.major)),
      // 12
      ImmutableList.of(new Treasure(27, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(82, new Dice(1, 6), 1, Treasure.Type.minor),
                       new Treasure(97, new Dice(1), 1, Treasure.Type.medium),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.major)),
      // 13
      ImmutableList.of(new Treasure(19, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(73, new Dice(1, 6), 1, Treasure.Type.minor),
                       new Treasure(95, new Dice(1), 1, Treasure.Type.medium),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.major)),
      // 14
      ImmutableList.of(new Treasure(19, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(58, new Dice(1, 6), 1, Treasure.Type.minor),
                       new Treasure(92, new Dice(1), 1, Treasure.Type.medium),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.major)),
      // 15
      ImmutableList.of(new Treasure(11, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(46, new Dice(1, 10), 1, Treasure.Type.minor),
                       new Treasure(90, new Dice(1), 1, Treasure.Type.medium),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.major)),
      // 16
      ImmutableList.of(new Treasure(40, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(46, new Dice(1, 10), 1, Treasure.Type.minor),
                       new Treasure(90, new Dice(1, 3), 1, Treasure.Type.medium),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.major)),
      // 17
      ImmutableList.of(new Treasure(33, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(83, new Dice(1, 3), 1, Treasure.Type.medium),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.major)),
      // 18
      ImmutableList.of(new Treasure(24, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(80, new Dice(1, 4), 1, Treasure.Type.medium),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.major)),
      // 19
      ImmutableList.of(new Treasure(4, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(70, new Dice(1, 4), 1, Treasure.Type.medium),
                       new Treasure(100, new Dice(1), 1, Treasure.Type.major)),
      // 20
      ImmutableList.of(new Treasure(25, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(65, new Dice(1, 4), 1, Treasure.Type.medium),
                       new Treasure(100, new Dice(1, 3), 1, Treasure.Type.major)),
      // 21
      ImmutableList.of(new Treasure(25, new Dice(0), 0, Treasure.Type.none),
                       new Treasure(65, new Dice(1, 4), 1, Treasure.Type.medium),
                       new Treasure(100, new Dice(1, 3), 1, Treasure.Type.major))
  );

  private static class Gem
  {
    public Gem(int inLimit, Dice inGPValue, int inFactor, String ... inNames)
    {
      m_limit = inLimit;
      m_gpValue = inGPValue;
      m_factor = inFactor;
      m_names = inNames;
    }

    private final int m_limit;
    private final Dice m_gpValue;
    private final int m_factor;
    private final String[] m_names;
  }

  private static final ImmutableList<Gem> GEMS = ImmutableList.of(
      new Gem(25, new Dice(4, 4), 1,
              "Banded Agate", "Eye Agate", "Moss Agate", "Azurite",
              "Blue Quart", "Hematite", "Lapis Lazuli", "Malachite", "Obsidian",
              "Rhodochrosite", "Tiger Eye", "Turquoise", "Freshwater Pearl"),
      new Gem(50, new Dice(2, 4), 10,
              "Bloodstone", "Carnelian", "Chalcedony", "Chrysoprase", "Citrine",
              "Iolite", "Jasper", "Moonstone", "Onyx", "Peridot",
              "Rock crystal", "Sard", "Sardonyx", "Rose Quarty", "Smoky Quart",
              "Star Rose Quartz", "Zircon")
  );
}
