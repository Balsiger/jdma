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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import com.google.template.soy.data.SoyData;

import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.dma.values.Dice;
import net.ixitxachitls.util.Strings;

/**
 * Servlet for simple treasure generation.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class TreasureServlet extends PageServlet
{
  public TreasureServlet()
  {
  }

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
    if(inIndex >= inValues.size())
      return inValues.get(inValues.size() - 1);

    return inValues.get(inIndex);
  }

  private String roll(Treasure inTreasure, int factor)
  {
    if(inTreasure.m_type == Treasure.Type.none)
      return "-";

    int count = inTreasure.m_random.roll() * factor;

    List<String> details = new ArrayList<>();
    switch(inTreasure.m_type)
    {
      default:
      case cp:
      case sp:
      case gp:
      case pp:
        break;

      case gem:
        for(int i = 0; i < count; i++)
          details.add(random(GEMS).roll().toString());
        break;

      case art:
        for(int i = 0; i < count; i++)
          details.add(random(ART).roll().toString());
        break;

      case mundane:
        for(int i = 0; i < count; i++)
          details.add(random(MUNDANE).roll().toString());
        break;

      case minor:
        for(int i = 0; i < count; i++)
          details.add(random(MINOR).roll().toString());
        break;

      case medium:
        for(int i = 0; i < count; i++)
          details.add(random(MEDIUM).roll().toString());
        break;

      case major:
        for(int i = 0; i < count; i++)
          details.add(random(MAJOR).roll().toString());
        break;
    }

    return count + " " + inTreasure.m_type
        + (details.isEmpty()
        ? "" : " (" + Strings.COMMA_JOINER.join(details) + ")");
  }

  private static <T extends Value> T random(List<T> inValues)
  {
    int random = RANDOM.nextInt(100) + 1;
    for(T value : inValues)
      if(value.getLimit() >= random)
        return value;

    // should never happen
    if(!inValues.isEmpty())
      return inValues.get(0);

    throw new IllegalStateException("Empty list of values");
  }

  private abstract static class Value
  {
    public static class Result
    {
      public Result() {
        this.m_baseGp = 0;
        this.m_armorBonus = 0;
        this.m_weaponBonus = 0;
        this.m_status = Status.done;
      }

      public Result(String inName, int inGp)
      {
        this.m_baseGp = inGp;
        this.m_armorBonus = 0;
        this.m_weaponBonus = 0;
        this.m_names.add(inName);
        this.m_status = Status.done;
      }

      public Result(Result inResult, String inName, int inGp, int inArmorBonus,
                    int inWeaponBonus, Status inStatus)
      {
        this.m_armorBonus = inResult.m_armorBonus + inArmorBonus;
        this.m_weaponBonus = inResult.m_weaponBonus + inWeaponBonus;
        this.m_baseGp = inResult.m_baseGp + inGp;
        this.m_names.add(inName);
        this.m_names.addAll(inResult.m_names);
        this.m_status = inStatus;
      }

      public Result(String inName, int inGp, int inArmorBonus,
                    int inWeaponBonus)
      {
        this.m_armorBonus = inArmorBonus;
        this.m_weaponBonus = inWeaponBonus;
        this.m_baseGp = inGp;
        this.m_names.add(inName);
        this.m_status = Status.done;
      }

      public Result(int inGp, int inArmorBonus, int inWeaponBonus,
                    List<String> inNames) {
        this.m_baseGp = inGp;
        this.m_armorBonus = inArmorBonus;
        this.m_weaponBonus = inWeaponBonus;
        this.m_names.addAll(inNames);
        this.m_status = Status.done;
      }

      public Result(int inGp, int inArmorBonus, int inWeaponBonus,
                    List<String> inNames, Status inStatus) {
        this.m_baseGp = inGp;
        this.m_armorBonus = inArmorBonus;
        this.m_weaponBonus = inWeaponBonus;
        this.m_names.addAll(inNames);
        this.m_status = inStatus;
      }

      enum Status { done, reroll, rerollTwo };

      private final int m_baseGp;
      private final int m_armorBonus;
      private final int m_weaponBonus;
      private final List<String> m_names = new ArrayList<>();
      private final Status m_status;

      public Result merge(Result inOther)
      {
        List<String> names = new ArrayList<>(m_names);
        names.addAll(inOther.m_names);
        return new Result(m_baseGp + inOther.m_baseGp,
                          m_armorBonus + inOther.m_armorBonus,
                          m_weaponBonus + inOther.m_weaponBonus,
                          names, inOther.m_status);
      }

      @Override
      public String toString()
      {
        return Strings.SPACE_JOINER.join(m_names) + " " + computeGp() + " gp";
      }

      private int computeGp()
      {
        return m_baseGp
            + (m_armorBonus * m_armorBonus * 1000)
            + (m_weaponBonus * m_weaponBonus * 2000);
      }
    }

    enum Type
    {
      none, cp, sp, gp, pp, gem, art, mundane, minor, medium, major,
    }

    private Value(int inLimit)
    {
      m_limit = inLimit;
    }

    private int m_limit;

    public int getLimit()
    {
      return m_limit;
    }

    public abstract Result roll();
  }

  private static class Treasure extends Value
  {
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

    @Override
    public Result roll()
    {
      return new Result();
    }
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

  private static class Gem extends Value
  {
    public Gem(int inLimit, Dice inGPValue, int inFactor, String... inNames)
    {
      super(inLimit);

      m_gpValue = inGPValue;
      m_factor = inFactor;
      m_names = inNames;
    }

    private final Dice m_gpValue;
    private final int m_factor;
    private final String[] m_names;

    public Result roll()
    {
      return new Result(randomName(), m_gpValue.roll() * m_factor);
    }

    private String randomName()
    {
      return m_names[RANDOM.nextInt(m_names.length)];
    }
  }

  private static final ImmutableList<Gem> GEMS = ImmutableList.of(
      new Gem(25, new Dice(4, 4), 1,
              "banded agate", "eye agate", "moss agate", "azurite",
              "blue QuartZ", "hematite", "lapis lazuli", "malachite", "obsidian",
              "rhodochrosite", "tiger eye", "turquoise", "freshwater pearl"),
      new Gem(50, new Dice(2, 4), 10,
              "bloodstone", "carnelian", "chalcedony", "chrysoprase", "citrine",
              "iolite", "jasper", "moonstone", "onyx", "peridot",
              "rock crystal", "sard", "sardonyx", "rose quartz", "smoky quartz",
              "star rose quartz", "zircon"),
      new Gem(70, new Dice(4, 4), 10,
              "amber", "amethyst", "chrysoberyl", "coral", "red garnet",
              "brown-green garnet", "jade", "jet", "white pearl",
              "golden pearl", "pink pearl", "silver pearl", "red spinel",
              "red-brown spinel", "deep green spinel", "tourmaline"),
      new Gem(90, new Dice(2, 4), 100,
              "alexandrite", "aquamarine", "violet garnet", "black pearl",
              "deep blue spinel", "golden yellow topaz"),
      new Gem(99, new Dice(4, 4), 100,
              "emerald", "white opal", "black opal", "fire opal",
              "blue sapphire", "fiery yellow corundum", "rich purple corundum",
              "blue star sapphire", "black star sapphire", "star ruby"),
      new Gem(100, new Dice(2, 4), 1000,
              "clearest bright green emerald", "blue-white diamond",
              "canary diamond", "pink diamond", "brown diamond", "blue diamond",
              "jacinth")
  );

  private static class Art extends Gem
  {
    public Art(int inLimit, Dice inGPValue, int inFactor, String... inNames)
    {
      super(inLimit, inGPValue, inFactor, inNames);
    }
  }

  ;

  private static final ImmutableList<Art> ART = ImmutableList.of(
      new Art(10, new Dice(1, 10), 10,
              "silver ewer",
              "carved bone statuette",
              "carved ivory statuette",
              "finely wrought small gold bracelet"),
      new Art(25, new Dice(3, 6), 10,
              "cloth of gold vestments",
              "black velvet mask with numerous citrines",
              "silver chalice with lapis lazuli gems"),
      new Art(40, new Dice(1, 6), 100,
              "large well-done wool tapestry",
              "brass mug with jade inlays"),
      new Art(50, new Dice(1, 10), 100,
              "silver comb with moonstones",
              "silver-plated steel longsword with jet jewel in hilt"),
      new Art(60, new Dice(2, 6), 100,
              "carved harp of exotic wood with ivory inlay and zircon gems",
              "solid gold idol (10 lb.)"),
      new Art(70, new Dice(3, 6), 100,
              "gold dragon comb with red garnet eye",
              "gold and topaz bottle stopper cork",
              "ceremonial electrum dagger with a star ruby in the pommel"),
      new Art(80, new Dice(4, 6), 100,
              "eyepatch with mock eye of sapphire and moonstone",
              "fire opal pendant on a fine gold chain",
              "old masterpiece painting"),
      new Art(85, new Dice(5, 6), 100,
              "embroidered silk and velvet mantle with numerous moonstones",
              "sapphire pendant on gold chain"),
      new Art(90, new Dice(1, 4), 1000,
              "embroidered and bejeweled glove",
              "Jjeweled anklet",
              "gold music box"),
      new Art(95, new Dice(1, 6), 1000,
              "golden circlet with four aquamarines",
              "a string of small pink pearls (necklace)"),
      new Art(99, new Dice(2, 4), 1000,
              "jeweled gold crown",
              "jeweled electrum ring"),
      new Art(100, new Dice(2, 6), 1000,
              "gold and ruby ring",
              "gold cup set with emeralds")
  );

  private static class Item extends Value
  {
    private Item(int inLimit, String inName, List<Item> inMundanes)
    {
      super(inLimit);
      m_name = inName;
      m_mundanes = inMundanes;
      m_count = Dice.ZERO;
      m_gp = 0;
    }

    private Item(int inLimit, String inName, int inGp)
    {
      this(inLimit, inName, Dice.ONE, inGp);
    }

    private Item(int inLimit, String inName, Dice inCount, int inGp)
    {
      super(inLimit);
      m_mundanes = Collections.emptyList();
      m_name = inName;
      m_count = inCount;
      m_gp = inGp;
    }

    private final String m_name;
    private final Dice m_count;
    private final int m_gp;
    private final List<Item> m_mundanes;

    @Override
    public Result roll()
    {
      if(m_mundanes.isEmpty())
      {
        int count = m_count.roll();
        int gp = m_gp * count;

        if(count == 1)
          return new Result(m_name, gp);

        return new Result(count + "x " + m_name, gp);
      }

      return random(m_mundanes).roll();
    }
  }

  private static final List<Item> ARMOR = ImmutableList.of(
      new Item(12, "chain shirt", 100),
      new Item(18, "Masterwork studded leather", 175),
      new Item(26, "breastplate", 200),
      new Item(34, "banded mail", 250),
      new Item(54, "half-plate", 600),
      new Item(80, "full plate", 1500),
      new Item(90, "darkwood", ImmutableList.of(
          new Item(50, "buckler", 205),
          new Item(50, "shield", 257))),
      new Item(100, "masterwork shield", ImmutableList.of(
          new Item(40, "light wooden shield", 153),
          new Item(60, "light steel shield", 159),
          new Item(83, "heavy wooden shield", 157),
          new Item(100, "light steel shield", 170)
      )));

  private static final List<Item> WEAPON_COMMON_MELEE = ImmutableList.of(
      new Item(4, "dagger", 302),
      new Item(14, "greataxe", 320),
      new Item(24, "greatsword", 350),
      new Item(28, "kama", 302),
      new Item(41, "longsword", 315),
      new Item(45, "light Mace", 305),
      new Item(50, "heavy Mace", 312),
      new Item(54, "nunchaku", 302),
      new Item(57, "quarterstaff", 600),
      new Item(61, "rapier", 320),
      new Item(66, "scimitar", 315),
      new Item(70, "shortspear", 302),
      new Item(74, "siangham", 303),
      new Item(84, "bastard sword", 335),
      new Item(89, "short sword", 310),
      new Item(100, "dwarven waraxe", 330));

  private static final List<Item> WEAPON_UNCOMMON_MELEE = ImmutableList.of(
      new Item(3, "orc double axe", 660),
      new Item(7, "battleaxe", 310),
      new Item(10, "spiked chain", 325),
      new Item(12, "club", 300),
      new Item(16, "hand crossbow", 400),
      new Item(19, "repeating crossbow", 550),
      new Item(21, "punching dagger", 302),
      new Item(23, "falchion", 375),
      new Item(26, "dire flail", 690),
      new Item(31, "heavy flail", 315),
      new Item(35, "light flail", 305),
      new Item(37, "gauntlet", 302),
      new Item(39, "spiked gauntlet", 305),
      new Item(41, "glaive", 308),
      new Item(43, "greatclub", 305),
      new Item(45, "guisarme", 309),
      new Item(48, "halberd", 310),
      new Item(51, "halfspear", 301),
      new Item(54, "gnome hooked hammer", 620),
      new Item(56, "light hammer", 301),
      new Item(58, "handaxe", 306),
      new Item(61, "kukri", 308),
      new Item(64, "lance", 310),
      new Item(67, "longspear", 305),
      new Item(70, "morningstar", 308),
      new Item(72, "net", 320),
      new Item(74, "heavy pick", 308),
      new Item(76, "light pick", 304),
      new Item(78, "ranseur", 310),
      new Item(80, "sap", 301),
      new Item(82, "scythe", 318),
      new Item(84, "shuriken", 301),
      new Item(86, "sickle", 306),
      new Item(89, "two-bladed sword", 700),
      new Item(91, "trident", 315),
      new Item(94, "dwarven Urgrosh", 650),
      new Item(97, "warhammer", 312),
      new Item(100, "whip", 301));

  private static final List<Item> WEAPON_RANGED = ImmutableList.of(
      new Item(10, "ammunition", ImmutableList.of(
          new Item(50, "arrows (50)", 350),
          new Item(80, "crossbow bolts (50)", 350),
          new Item(100, "sling bullets (50)", 350))),
      new Item(15, "throwing Axe", 308),
      new Item(25, "heavy Crossbow", 350),
      new Item(35, "light Crossbow", 335),
      new Item(39, "dart", 301),
      new Item(41, "javelin", 301),
      new Item(46, "shortbow", 330),
      new Item(51, "composite shortbow (+0 Str bonus)", 375),
      new Item(56, "composite shortbow (+1 Str bonus)", 450),
      new Item(61, "composite shortbow (+2 Str bonus)", 525),
      new Item(65, "sling", 300),
      new Item(75, "longbow", 375),
      new Item(80, "composite longbow (+0 Str bonus)", 400),
      new Item(85, "composite longbow (+1 Str bonus)", 500),
      new Item(90, "composite longbow (+2 Str bonus)", 600),
      new Item(95, "composite longbow (+3 Str bonus)", 700),
      new Item(100, "composite longbow (+4 Str bonus)", 800));

  private static final List<Item> MUNDANE = ImmutableList.of(
      new Item(17, "alchemical item", ImmutableList.of(
          new Item(12, "alchemist's fire", new Dice(1, 4), 20),
          new Item(24, "acid", new Dice(2, 4), 10),
          new Item(36, "smokesticks", new Dice(1, 4), 20),
          new Item(48, "holy water", new Dice(1, 4), 25),
          new Item(62, "antitoxin", new Dice(1, 4), 50),
          new Item(74, "everburning torch", 110),
          new Item(88, "tanglefoot bag", new Dice(1, 4), 50),
          new Item(100, "thunderstone", new Dice(1, 4), 30))),
      new Item(50, "armor", ImmutableList.of(
          new Item(10, "small", ARMOR),
          new Item(100, "medium", ARMOR))),
      new Item(83, "weapon", ImmutableList.of(
          new Item(50, "masterwork common melee weapon", WEAPON_COMMON_MELEE),
          new Item(70, "masterwork uncommon weapon", WEAPON_UNCOMMON_MELEE),
          new Item(100, "masterwork common ranged weapon", WEAPON_RANGED))),
      new Item(100, "tools and gear", ImmutableList.of(
          new Item(3, "backpack, empty", 2),
          new Item(6, "crowbar", 2),
          new Item(11, "bullseye Lantern", 12),
          new Item(16, "simple Lock", 20),
          new Item(21, "average Lock", 40),
          new Item(28, "good Lock", 80),
          new Item(35, "superior Lock", 150),
          new Item(40, "masterwork manacles", 50),
          new Item(43, "small steel mirror", 10),
          new Item(46, "silk Rope (50 ft.)", 10),
          new Item(53, "spyglass", 1000),
          new Item(58, "masterwork Artisan's tool", 55),
          new Item(63, "climber's Kit", 80),
          new Item(68, "disguise Kit", 50),
          new Item(73, "healer's Kit", 50),
          new Item(77, "silver Holy Symbol", 25),
          new Item(81, "hourglass", 25),
          new Item(88, "magnifying glass", 100),
          new Item(95, "masterwork musical instrument", 100),
          new Item(100, "masterwork thieve's tools", 50)
      )));

  private static class Magic extends Value
  {
    private Magic(int inLimit, String inName, List<Magic> inMagic)
    {
      super(inLimit);

      m_name = inName;
      m_dice = Dice.ONE;
      m_magic = inMagic;
      m_gp = 0;
      m_items = Collections.emptyList();
      m_status = Result.Status.done;
      m_armorBonus = 0;
      m_weaponBonus = 0;
    }

    private Magic(int inLimit, String inName, Dice inDice, List<Magic> inMagic)
    {
      super(inLimit);

      m_name = inName;
      m_dice = inDice;
      m_magic = inMagic;
      m_gp = 0;
      m_items = Collections.emptyList();
      m_status = Result.Status.done;
      m_armorBonus = 0;
      m_weaponBonus = 0;
    }

    private Magic(int inLimit, String inName, int inArmorBonus, int WeaponBonus,
                  List<Item> inItems)
    {
      super(inLimit);

      m_dice = Dice.ONE;
      m_armorBonus = inArmorBonus;
      m_weaponBonus = WeaponBonus;
      m_name = inName;
      m_items = inItems;
      m_magic = Collections.emptyList();
      m_gp = 0;
      m_status = Result.Status.done;
    }

    private Magic(int inLimit, int inArmorBonus, int inWeaponBonus,
                  Result.Status inStatus, List<Magic> inMagic)
    {
      super(inLimit);

      m_dice = Dice.ONE;
      m_armorBonus = inArmorBonus;
      m_weaponBonus = inWeaponBonus;
      m_name = "";
      m_items = Collections.emptyList();
      m_magic = inMagic;
      m_gp = 0;
      m_status = inStatus;
    }

    private Magic(int inLimit, String inName, Result.Status inStatus)
    {
      super(inLimit);

      m_dice = Dice.ONE;
      m_armorBonus = 0;
      m_weaponBonus = 0;
      m_name = inName;
      m_items = Collections.emptyList();
      m_magic = Collections.emptyList();
      m_gp = 0;
      m_status = inStatus;
    }

    private Magic(int inLimit, String inName, int inGp)
    {
      super(inLimit);

      m_dice = Dice.ONE;
      m_name = inName;
      m_items = Collections.emptyList();
      m_magic = Collections.emptyList();
      m_status = Result.Status.done;
      m_gp = inGp;
      m_armorBonus = 0;
      m_weaponBonus = 0;
    }

    private Magic(int inLimit, String inName, int inArmorBonus,
                  int inWeaponBonus)
    {
      super(inLimit);

      m_dice = Dice.ONE;
      m_name = inName;
      m_items = Collections.emptyList();
      m_magic = Collections.emptyList();
      m_status = Result.Status.done;
      m_gp = 0;
      m_armorBonus = inArmorBonus;
      m_weaponBonus = inWeaponBonus;
    }

    private final String m_name;
    private final Dice m_dice;
    private final List<Item> m_items;
    private final List<Magic> m_magic;
    private final int m_gp;
    private final Result.Status m_status;
    private final int m_armorBonus;
    private final int m_weaponBonus;

    @Override
    public Result roll()
    {
      if(!m_items.isEmpty())
      {
        Item item = random(m_items);
        return new Result(item.roll(), m_name, m_gp, m_armorBonus,
                          m_weaponBonus, m_status);
      }

      if(!m_magic.isEmpty())
      {
        Result result = random(m_magic).roll();
        int rolls = rolls(result.m_status) + m_dice.roll() - 1;
        while(rolls > 0) {
          result = result.merge(random(m_magic).roll());
          rolls = rolls - 1 + rolls(result.m_status);
        }

        return new Result(result, m_name, m_gp, m_armorBonus,
                          m_weaponBonus, m_status);
      }

      return new Result(m_name, m_gp, m_armorBonus, m_weaponBonus);
    }

    private int rolls(Result.Status inStatus) {
      switch(inStatus) {
        default:
          return 0;

        case reroll:
          return 1;

        case rerollTwo:
          return 2;
      }
    }
  }

  private static final List<Item> RANDOM_SHIELDS = ImmutableList.of(
      new Item(10, "buckler", 165),
      new Item(15, "light wooden shield", 153),
      new Item(20, "light steel shield", 159),
      new Item(30, "heavy wooden shield", 157),
      new Item(95, "heavy steel shield", 170),
      new Item(100, "tower shield", 180)
  );

  private static final List<Item> RANDOM_ARMOR = ImmutableList.of(
      new Item(1, "padded armor", 155),
      new Item(2, "leather armor", 160),
      new Item(17, "studded leather", 175),
      new Item(32, "chain shirt", 250),
      new Item(42, "hide armor", 165),
      new Item(43, "scale mail", 200),
      new Item(44, "chainmail", 300),
      new Item(57, "breastplate", 350),
      new Item(58, "split mail", 350),
      new Item(59, "banded mail", 400),
      new Item(60, "half-plate", 750),
      new Item(100, "full plate", 1650)
  );

  private static final List<Item> WEAPON_SPECIFIC_MINOR = ImmutableList.of(
            new Item(15, "sleep arrow", 132),
            new Item(25, "screaming bolt", 267),
            new Item(45, "masterwork silver dagger", 322),
            new Item(65, "masterwork cold iron longsword", 330),
            new Item(75, "javelin of lightning", 1500),
            new Item(80, "slaying arrow", 2282),
            new Item(90, "adamantine dagger", 3002),
            new Item(100, "adamantine battleaxe", 3010));

  private static final List<Item> WEAPON_SPECIFIC_MEDIUM = ImmutableList.of(
            new Item(9, "javelin of lightning", 1500),
            new Item(15, "slaying arrow", 2282),
            new Item(24, "adamantine dagger", 3002),
            new Item(33, "adamantine battleaxe", 3010),
            new Item(37, "greater slaying arrow", 4057),
            new Item(40, "shatterspike", 4315),
            new Item(46, "dagger of venom", 8302),
            new Item(51, "trident of warning", 10115),
            new Item(57, "assassin's dagger", 10302),
            new Item(62, "shifter's sorrow", 12780),
            new Item(66, "trident of fish command", 18650),
            new Item(74, "flame tongue", 20715),
            new Item(79, "luck blade (0 wishes)", 22060),
            new Item(86, "sword of sublety", 22310),
            new Item(91, "sword of the planes", 22315),
            new Item(95, "nine lives stealer", 23057),
            new Item(98, "sword of life stealing", 25715),
            new Item(100, "oathbow", 25600));

  private static final List<Item> WEAPON_SPECIFIC_MAJOR = ImmutableList.of(
            new Item(4, "assassin's dagger", 10302),
            new Item(7, "shifter's sorrow", 12780),
            new Item(9, "trident of fish command", 18650),
            new Item(13, "flame tongue", 20715),
            new Item(17, "luck blade (0 wishes)", 22060),
            new Item(24, "sword of sublety", 22310),
            new Item(31, "sword of the planes", 22315),
            new Item(37, "nine lives stealer", 23057),
            new Item(42, "sword of life stealing", 25715),
            new Item(46, "oathbow", 25600),
            new Item(51, "mace of terror", 38552),
            new Item(57, "life-drinker", 40320),
            new Item(62, "sylvan scimitar", 47315),
            new Item(67, "rapier of puncturing", 50320),
            new Item(73, "sun blade", 50335),
            new Item(79, "frost brand", 54475),
            new Item(84, "dwarven thrower", 60312),
            new Item(91, "luck blade (1 wish)", 62360),
            new Item(95, "mace of smiting", 75312),
            new Item(97, "luck blade (2 wishes)", 102660),
            new Item(99, "holy avenger", 120630),
            new Item(100, "luck blade (3 wishes)", 142960));

  private static final List<Magic> WEAPON_MELEE_SPECIAL_MINOR =
      ImmutableList.of(
          new Magic(10, "bane", 0, 1),
          new Magic(17, "defending", 0, 1),
          new Magic(27, "flaming", 0, 1),
          new Magic(37, "frost", 0, 1),
          new Magic(47, "shock", 0, 1),
          new Magic(56, "ghost touch", 0, 1),
          new Magic(67, "keen", 0, 1),
          new Magic(71, "ki focus", 0, 1),
          new Magic(75, "merciful", 0, 1),
          new Magic(82, "mighty cleaving", 0, 1),
          new Magic(87, "spell storing", 0, 1),
          new Magic(91, "throwing", 0, 1),
          new Magic(95, "thundering", 0, 1),
          new Magic(99, "vicious", 0, 1),
          new Magic(100, "", Value.Result.Status.rerollTwo));

  private static final List<Magic> WEAPON_MELEE_SPECIAL_MEDIUM =
      ImmutableList.of(
          new Magic(6, "bane", 0, 1),
          new Magic(12, "defending", 0, 1),
          new Magic(19, "flaming", 0, 1),
          new Magic(26, "frost", 0, 1),
          new Magic(33, "shock", 0, 1),
          new Magic(38, "ghost touch", 0, 1),
          new Magic(44, "keen", 0, 1),
          new Magic(48, "ki focus", 0, 1),
          new Magic(50, "merciful", 0, 1),
          new Magic(54, "mighty cleaving", 0, 1),
          new Magic(59, "spell storing", 0, 1),
          new Magic(63, "throwing", 0, 1),
          new Magic(65, "thundering", 0, 1),
          new Magic(69, "vicious", 0, 1),
          new Magic(72, "anarchic", 0, 2),
          new Magic(75, "axiomatic", 0, 2),
          new Magic(78, "disruption", 0, 2),
          new Magic(81, "flaming burst", 0, 2),
          new Magic(84, "icy burst", 0, 2),
          new Magic(87, "holy", 0, 2),
          new Magic(90, "shocking burst", 0, 2),
          new Magic(93, "unholy", 0, 2),
          new Magic(95, "wounding", 0, 2),
          new Magic(100, "", Value.Result.Status.rerollTwo));

  private static final List<Magic> WEAPON_MELEE_SPECIAL_MAJOR =
      ImmutableList.of(
          new Magic(3, "bane", 0, 1),
          new Magic(6, "flaming", 0, 1),
          new Magic(9, "frost", 0, 1),
          new Magic(12, "shock", 0, 1),
          new Magic(15, "ghost touch", 0, 1),
          new Magic(19, "ki focus", 0, 1),
          new Magic(21, "mighty cleaving", 0, 1),
          new Magic(24, "spell storing", 0, 1),
          new Magic(28, "throwing", 0, 1),
          new Magic(32, "thundering", 0, 1),
          new Magic(36, "vicious", 0, 1),
          new Magic(41, "anarchic", 0, 2),
          new Magic(46, "axiomatic", 0, 2),
          new Magic(49, "disruption", 0, 2),
          new Magic(54, "flaming burst", 0, 2),
          new Magic(59, "icy burst", 0, 2),
          new Magic(64, "holy", 0, 2),
          new Magic(69, "shocking burst", 0, 2),
          new Magic(74, "unholy", 0, 2),
          new Magic(78, "wounding", 0, 2),
          new Magic(83, "speed", 0, 3),
          new Magic(86, "brilliant energy", 0, 4),
          new Magic(88, "dancing", 0, 4),
          new Magic(90, "vorpal", 0, 5),
          new Magic(100, "", Value.Result.Status.rerollTwo));

  private static final List<Magic> WEAPON_RANGED_SPECIAL_MINOR =
      ImmutableList.of(
          new Magic(12, "bane", 0, 1),
          new Magic(25, "distance", 0, 1),
          new Magic(40, "flaming", 0, 1),
          new Magic(55, "frost", 0, 1),
          new Magic(60, "merciful", 0, 1),
          new Magic(68, "returning", 0, 1),
          new Magic(83, "shock", 0, 1),
          new Magic(93, "seeking", 0, 1),
          new Magic(99, "thundering", 0, 1),
          new Magic(100, "", Value.Result.Status.rerollTwo));

  private static final List<Magic> WEAPON_RANGED_SPECIAL_MEDIUM =
      ImmutableList.of(
          new Magic(8, "bane", 0, 1),
          new Magic(16, "distance", 0, 1),
          new Magic(28, "flaming", 0, 1),
          new Magic(40, "frost", 0, 1),
          new Magic(42, "merciful", 0, 1),
          new Magic(47, "returning", 0, 1),
          new Magic(59, "shock", 0, 1),
          new Magic(64, "seeking", 0, 1),
          new Magic(68, "thundering", 0, 1),
          new Magic(71, "anarchic", 0, 2),
          new Magic(74, "axiomatic", 0, 2),
          new Magic(79, "flaming burst", 0, 2),
          new Magic(82, "holy", 0, 2),
          new Magic(87, "icy burst", 0, 2),
          new Magic(92, "shocking burst", 0, 2),
          new Magic(95, "unholy", 0, 2),
          new Magic(100, "", Value.Result.Status.rerollTwo));

  private static final List<Magic> WEAPON_RANGED_SPECIAL_MAJOR =
      ImmutableList.of(
          new Magic(4, "bane", 0, 1),
          new Magic(8, "distance", 0, 1),
          new Magic(12, "flaming", 0, 1),
          new Magic(16, "frost", 0, 1),
          new Magic(21, "returning", 0, 1),
          new Magic(25, "shock", 0, 1),
          new Magic(27, "seeking", 0, 1),
          new Magic(29, "thundering", 0, 1),
          new Magic(34, "anarchic", 0, 2),
          new Magic(39, "axiomatic", 0, 2),
          new Magic(49, "flaming burst", 0, 2),
          new Magic(54, "holy", 0, 2),
          new Magic(64, "icy burst", 0, 2),
          new Magic(74, "shocking burst", 0, 2),
          new Magic(79, "unholy", 0, 2),
          new Magic(84, "speed", 0, 3),
          new Magic(90, "brilliant energy", 0, 4),
          new Magic(100, "", Value.Result.Status.rerollTwo));

  private static final List<Item> SCROLLS_ARCANE_0 = ImmutableList.of(
      new Item(4, "acid splash", 13),
      new Item(8, "arcane mark", 13),
      new Item(13, "dancing lights", 13),
      new Item(17, "daze", 13),
      new Item(24, "detec magic", 13),
      new Item(28, "detect poison", 13),
      new Item(32, "disrupt undead", 13),
      new Item(37, "flare", 13),
      new Item(42, "ghost sound", 13),
      new Item(44, "know direction", 13),
      new Item(50, "light", 13),
      new Item(52, "lullaby", 13),
      new Item(57, "mage hand", 13),
      new Item(62, "mending", 13),
      new Item(67, "message", 13),
      new Item(72, "open/close", 13),
      new Item(77, "prestidigitation", 13),
      new Item(81, "ray of frost", 13),
      new Item(87, "read magic", 13),
      new Item(94, "resistance", 13),
      new Item(96, "summon instrument", 13),
      new Item(100, "touch of fatigue", 13));

  private static final List<Item> SCROLLS_ARCANE_1 = ImmutableList.of(
      new Item(3, "alarm", 25),
      new Item(5, "animate rope", 25),
      new Item(7, "burning hands", 25),
      new Item(9, "cause fear", 25),
      new Item(12, "charm person", 25),
      new Item(14, "chill touch", 25),
      new Item(16, "color spray", 25),
      new Item(19, "comprehend languages", 25),
      new Item(20, "lesser confusion", 25),
      new Item(21, "cure light wounds", 25),
      new Item(24, "detect secret doors", 25),
      new Item(26, "detect undead", 25),
      new Item(29, "disguise self", 25),
      new Item(32, "endure elements", 25),
      new Item(35, "enlarge person", 25),
      new Item(37, "erase", 25),
      new Item(40, "expeditious retreat", 25),
      new Item(41, "feather fall", 25),
      new Item(43, "grease", 25),
      new Item(45, "hold portal", 25),
      new Item(47, "hypnotism", 25),
      new Item(49, "identify", 25),
      new Item(51, "jump", 25),
      new Item(54, "mage armor", 25),
      new Item(56, "magic missile", 25),
      new Item(59, "magic weapon", 25),
      new Item(62, "mount", 25),
      new Item(64, "Nystul's magic aura", 25),
      new Item(66, "obscuring mist", 25),
      new Item(74, "protection from chaos/evil/good/law", 25),
      new Item(76, "ray of enfeeblement", 25),
      new Item(78, "reduce person", 25),
      new Item(80, "remove fear", 25),
      new Item(82, "shield", 25),
      new Item(84, "shocking grasp", 25),
      new Item(86, "silent image", 25),
      new Item(88, "sleep", 25),
      new Item(90, "summon monster I", 25),
      new Item(93, "Tenser's floating disk", 25),
      new Item(95, "true strike", 25),
      new Item(96, "undetectable alignment", 25),
      new Item(98, "unseen servant", 25),
      new Item(100, "ventriloquism", 25));

  private static final List<Item> SCROLLS_ARCANE_2 = ImmutableList.of(
      new Item(1, "animal messenger", 200),
      new Item(2, "animal trance", 200),
      new Item(3, "arcane lock", 175),
      new Item(6, "bear's endurance", 150),
      new Item(8, "blindness/deafness", 150),
      new Item(10, "blur", 150),
      new Item(13, "bull's strength", 150),
      new Item(14, "calm emotions", 200),
      new Item(17, "cat's grace", 150),
      new Item(19, "command undead", 150),
      new Item(20, "continual flame", 200),
      new Item(21, "cure moderate wounds", 200),
      new Item(22, "darkness", 150),
      new Item(25, "darkvision", 150),
      new Item(26, "daze monster", 150),
      new Item(27, "delay poison", 200),
      new Item(29, "detect thoughts", 150),
      new Item(31, "disguise self", 150),
      new Item(34, "eagle's splendor", 150),
      new Item(35, "enthrall", 200),
      new Item(37, "false life", 150),
      new Item(39, "flaming sphere", 150),
      new Item(40, "fog cloud", 150),
      new Item(43, "fox's cunning", 150),
      new Item(44, "ghoul touch", 150),
      new Item(46, "glitterdust", 150),
      new Item(47, "gust of wind", 150),
      new Item(49, "hypnotic pattern", 150),
      new Item(52, "invisibility", 150),
      new Item(55, "knock", 150),
      new Item(56, "Leomund's trap", 200),
      new Item(58, "levitate", 150),
      new Item(59, "locate object", 150),
      new Item(60, "magic mouth", 160),
      new Item(62, "Melf's acid arrow", 150),
      new Item(63, "minor image", 150),
      new Item(65, "mirror image", 150),
      new Item(66, "misdirection", 150),
      new Item(67, "obscure object", 150),
      new Item(70, "owl's wisdom", 150),
      new Item(73, "protection from arrows", 150),
      new Item(75, "pyrothechnics", 150),
      new Item(78, "resist energy", 150),
      new Item(79, "rope trick", 150),
      new Item(80, "scare", 150),
      new Item(82, "scorching ray", 150),
      new Item(85, "see invisibility", 150),
      new Item(86, "shatter", 150),
      new Item(87, "silence", 200),
      new Item(88, "sound burst", 200),
      new Item(89, "spectral hand", 150),
      new Item(91, "spider climb", 150),
      new Item(93, "summon monster II", 150),
      new Item(95, "summon swarm", 150),
      new Item(96, "Tasha's hideous laughter", 150),
      new Item(97, "touch of idiocy", 150),
      new Item(99, "web", 150),
      new Item(100, "whispering wind", 150));

  private static final List<Item> SCROLLS_ARCANE_3 = ImmutableList.of(
      new Item(2, "arcane sight", 375),
      new Item(4, "blink", 375),
      new Item(6, "clairaudience/clairvoyance", 375),
      new Item(7, "cure serious wounds", 525),
      new Item(10, "daylight", 525),
      new Item(12, "deep slumber", 375),
      new Item(15, "dispel magic", 375),
      new Item(17, "displacement", 375),
      new Item(18, "explosive runes", 375),
      new Item(20, "fireball", 375),
      new Item(22, "flame arrow", 375),
      new Item(25, "fly", 375),
      new Item(27, "gaseous form", 375),
      new Item(29, "gentle repose", 375),
      new Item(30, "glibness", 525),
      new Item(31, "good hope", 525),
      new Item(33, "halt undead", 375),
      new Item(36, "haste", 375),
      new Item(38, "heroism", 375),
      new Item(40, "hold person", 375),
      new Item(41, "illusory script", 425),
      new Item(44, "invisibility sphere", 375),
      new Item(47, "keen edge", 375),
      new Item(49, "Leomund's tiny hut", 375),
      new Item(51, "lightning bolt", 375),
      new Item(59, "magic cericle against chos/evil/good/law", 375),
      new Item(62, "greater magic weapon", 375),
      new Item(64, "major image", 375),
      new Item(66, "nondetection", 425),
      new Item(68, "phantom steed", 375),
      new Item(71, "protection from energy", 375),
      new Item(73, "rage", 375),
      new Item(75, "ray of exhaustion", 375),
      new Item(76, "sculpt sound", 525),
      new Item(77, "secret page", 375),
      new Item(78, "sepia snake sigil", 875),
      new Item(79, "shrink item", 375),
      new Item(81, "sleet storm", 375),
      new Item(83, "slow", 375),
      new Item(84, "speak with animals", 375),
      new Item(86, "stinking cloud", 375),
      new Item(88, "suggestion", 375),
      new Item(90, "summon monster III", 375),
      new Item(93, "tongues", 375),
      new Item(95, "vampirit touch", 375),
      new Item(98, "water breathing", 375),
      new Item(100, "wind wall", 375));

  private static final List<Item> SCROLLS_ARCANE_4 = ImmutableList.of(
      new Item(2, "animate date", 1050),
      new Item(5, "arcane eye", 700),
      new Item(7, "bestow curse", 700),
      new Item(10, "charm monster", 700),
      new Item(13, "confusion", 700),
      new Item(15, "contagion", 700),
      new Item(17, "crushing despair", 700),
      new Item(18, "cure critical wounds", 1000),
      new Item(19, "detect scrying", 700),
      new Item(23, "dimension door", 700),
      new Item(26, "dimensional anchor", 700),
      new Item(28, "enervation", 700),
      new Item(30, "mass enlarge person", 700),
      new Item(32, "Evard's black tentacles", 700),
      new Item(34, "fear", 700),
      new Item(37, "fire shield", 700),
      new Item(39, "fire trap", 725),
      new Item(42, "freedom of movement", 1000),
      new Item(43, "lesser geas", 700),
      new Item(46, "lesser globe of invulnerability", 700),
      new Item(48, "hallucinatory terrain", 700),
      new Item(50, "ice storm", 700),
      new Item(52, "illusory wall", 700),
      new Item(55, "greater invisibility", 700),
      new Item(57, "Leomund's secure shelter", 700),
      new Item(58, "locate creature", 700),
      new Item(60, "minor creation", 700),
      new Item(61, "modify memory", 1000),
      new Item(62, "neutralize poison", 1000),
      new Item(64, "Otiluke's resilient sphere", 700),
      new Item(66, "phantasmal killer", 700),
      new Item(68, "polymorph", 700),
      new Item(70, "rainbow pattern", 700),
      new Item(71, "Rary's mnemonic enhancer", 700),
      new Item(73, "mass reduce person", 700),
      new Item(76, "remove curse", 700),
      new Item(77, "repel vermin", 700),
      new Item(79, "scrying", 700),
      new Item(81, "shadow conjuration", 700),
      new Item(83, "shout", 700),
      new Item(85, "solid fog", 700),
      new Item(86, "speak with plants", 1000),
      new Item(88, "stone shape", 700),
      new Item(91, "stoneskin", 950),
      new Item(93, "summon monster IV", 700),
      new Item(96, "wall of fire", 700),
      new Item(99, "wall of ice", 700),
      new Item(100, "zone of silence", 1000));

  private static final List<Item> SCROLLS_ARCANE_5 = ImmutableList.of(
      new Item(2, "animal growth", 1125),
      new Item(5, "baleful polymorph", 1125),
      new Item(7, "Bigby's interposing hand", 1125),
      new Item(9, "blight", 1125),
      new Item(12, "break enchantment", 1125),
      new Item(14, "cloudkill", 1125),
      new Item(17, "cone of cold", 1125),
      new Item(19, "contact other plane", 1125),
      new Item(20, "mass cure light wounds", 1625),
      new Item(23, "dismissal", 1125),
      new Item(26, "greater dispel magic", 1625),
      new Item(28, "dominate person", 1125),
      new Item(29, "dream", 1125),
      new Item(31, "fabricate", 1125),
      new Item(33, "false vision", 1325),
      new Item(35, "feeblemind", 1125),
      new Item(39, "hold monster", 1125),
      new Item(40, "Leomund's secret chest", 1125),
      new Item(41, "magic jar", 1125),
      new Item(43, "major creation", 1125),
      new Item(45, "mind fog", 1125),
      new Item(47, "mirage arcana", 1125),
      new Item(49, "Mordenkainen's faithful hound", 1125),
      new Item(51, "Mordenkainen's private sanctum", 1125),
      new Item(53, "nightmare", 1125),
      new Item(57, "overland flight", 1125),
      new Item(60, "passwall", 1125),
      new Item(61, "permanency", 10125),
      new Item(63, "persistance image", 1125),
      new Item(65, "lesser planar binding", 1125),
      new Item(67, "prying eyes", 1125),
      new Item(69, "Rary's telephathic bond", 1125),
      new Item(71, "seeming", 1125),
      new Item(74, "sending", 1125),
      new Item(76, "shadow evocation", 1125),
      new Item(77, "song of discord", 1625),
      new Item(79, "summon monster V", 1125),
      new Item(80, "symbol of pain", 2125),
      new Item(81, "symbol of sleep", 1125),
      new Item(83, "telekinesis", 1125),
      new Item(88, "teleport", 1125),
      new Item(90, "transmute mud to rock", 1125),
      new Item(92, "transmute rock to mud", 1125),
      new Item(95, "wall of force", 1125),
      new Item(98, "wall of stone", 1125),
      new Item(100, "waves of fatigue", 1125));

  private static final List<Item> SCROLLS_ARCANE_6 = ImmutableList.of(
      new Item(2, "acid fog", 1650),
      new Item(5, "analyze dweomer", 1650),
      new Item(6, "animate objects", 2400),
      new Item(9, "antimagic field", 1650),
      new Item(12, "mass bear's endurance", 1650),
      new Item(14, "Bigby's forceful hand", 1650),
      new Item(17, "mass bull's strength", 1650),
      new Item(20, "mass cat's grace", 1650),
      new Item(23, "chain lightning", 1650),
      new Item(25, "circle of death", 2150),
      new Item(26, "contingency", 1650),
      new Item(28, "control water", 1650),
      new Item(29, "create undead", 2350),
      new Item(30, "mass cure moderate wounds", 2400),
      new Item(33, "disintegrate", 1650),
      new Item(37, "greater dispel magic", 1650),
      new Item(40, "mass eagle's splendor", 1650),
      new Item(42, "eyebite", 1650),
      new Item(43, "find the path", 2400),
      new Item(45, "flesh to stone", 1650),
      new Item(48, "mass fox's cunning", 1650),
      new Item(49, "geas/quest", 1650),
      new Item(52, "globe of invulnerability", 1650),
      new Item(53, "guards and wards", 1650),
      new Item(54, "heroes' feast", 2400),
      new Item(56, "greater herosim", 1650),
      new Item(57, "legend lore", 1900),
      new Item(59, "mislead", 1650),
      new Item(60, "Mordenkainen's lucubration", 1650),
      new Item(62, "move earth", 1650),
      new Item(64, "Otiluke's freezing sphere", 1650),
      new Item(67, "mass owl's wisdom", 1650),
      new Item(69, "permanent image", 1650),
      new Item(71, "planar binding", 1650),
      new Item(73, "programmed image", 1650),
      new Item(75, "repulsion", 1650),
      new Item(78, "shadow walk", 1650),
      new Item(81, "stone to flesh", 1650),
      new Item(83, "mass suggestion", 1650),
      new Item(85, "summon monster VI", 1650),
      new Item(86, "symbol of fear", 2650),
      new Item(87, "symbol of persuasion", 6650),
      new Item(88, "sympathtic vibration", 2400),
      new Item(90, "Tenser's transformation", 1950),
      new Item(93, "true seeing", 1900),
      new Item(95, "undead to death", 2150),
      new Item(97, "veil", 1650),
      new Item(100, "wall of iron", 1700));

  private static final List<Item> SCROLLS_ARCANE_7 = ImmutableList.of(
      new Item(3, "greater arcane sight", 2275),
      new Item(7, "banishment", 2275),
      new Item(10, "Bigby's grasping hand", 2275),
      new Item(13, "control undead", 2275),
      new Item(16, "control weather", 2275),
      new Item(19, "delayed blast fireball", 2275),
      new Item(21, "Drawmij's instant summons", 3275),
      new Item(25, "ethereal jaunt", 275),
      new Item(28, "finger of death", 2275),
      new Item(31, "forcecage", 2775),
      new Item(35, "mass hold person", 2275),
      new Item(38, "insanity", 2275),
      new Item(42, "mass invisiblity", 2275),
      new Item(43, "limited wish", 3775),
      new Item(45, "Mordenkainen's magnificient mansion", 2275),
      new Item(48, "Mordenkainen's sword", 2275),
      new Item(51, "phase door", 2275),
      new Item(54, "plane shift", 2275),
      new Item(57, "power word blind", 2275),
      new Item(61, "prismatic spray", 2275),
      new Item(64, "project image", 2280),
      new Item(67, "reverse gravity", 2275),
      new Item(70, "greater scrying", 2275),
      new Item(73, "sequester", 2275),
      new Item(76, "greater shadow conjuration", 2275),
      new Item(77, "simulacrum", 2275),
      new Item(80, "spell turning", 2275),
      new Item(82, "statue", 2275),
      new Item(85, "summon monster VII", 2275),
      new Item(86, "symbol of stunning", 7275),
      new Item(87, "symbol of weakness", 7275),
      new Item(90, "teleport object", 2275),
      new Item(95, "greater teleport", 2275),
      new Item(97, "vision", 2275),
      new Item(100, "waves of exhaustion", 2275));

  private static final List<Item> SCROLLS_ARCANE_8 = ImmutableList.of(
      new Item(2, "antipathy", 3000),
      new Item(5, "Bigby's clenched fist", 3000),
      new Item(8, "binding", 3000),
      new Item(12, "mass charm monster", 3000),
      new Item(13, "clone", 4000),
      new Item(16, "create greater undead", 3000),
      new Item(19, "demand", 3600),
      new Item(22, "dimensional lock", 3000),
      new Item(26, "discern location", 3000),
      new Item(29, "horrid wilting", 3000),
      new Item(32, "incendiary cloud", 3000),
      new Item(35, "iron body", 3000),
      new Item(38, "maze", 3000),
      new Item(41, "mind blank", 3000),
      new Item(44, "moment of prescience", 3000),
      new Item(48, "Otiluke's telekinetic sphere", 3000),
      new Item(51, "Otto's irresistible dance", 3000),
      new Item(54, "greater planar binding", 3000),
      new Item(57, "polar ray", 3000),
      new Item(60, "polymorph any object", 3000),
      new Item(63, "power word stun", 3000),
      new Item(66, "prismatic wall", 3000),
      new Item(70, "protection from spells", 3500),
      new Item(73, "greater prying eyes", 3000),
      new Item(76, "scintillating pattern", 3000),
      new Item(78, "screen", 3000),
      new Item(81, "greater shadow evocation", 3000),
      new Item(84, "greater shout", 3000),
      new Item(87, "summon monster VIII", 3000),
      new Item(90, "sunburst", 3000),
      new Item(91, "symbol of death", 8000),
      new Item(92, "symbol of insanity", 8000),
      new Item(94, "sympathy", 4500),
      new Item(98, "temporal stasis", 3500),
      new Item(100, "trap the soul", 13000));

  private static final List<Item> SCROLLS_ARCANE_9 = ImmutableList.of(
      new Item(3, "astral projection", 4870),
      new Item(7, "Bigby's crushing hand", 3825),
      new Item(12, "dominate monster", 3825),
      new Item(16, "energy drain", 3825),
      new Item(21, "etherealness", 3825),
      new Item(25, "foresight", 3825),
      new Item(31, "freedom", 3825),
      new Item(36, "gate", 8825),
      new Item(40, "mass hold monster", 3825),
      new Item(44, "imprisonment", 3825),
      new Item(49, "meteor swarm", 3825),
      new Item(53, "Mordenkainen's disjunction", 3825),
      new Item(58, "power word kill", 3825),
      new Item(62, "prismatic sphere", 3825),
      new Item(66, "refuge", 3825),
      new Item(70, "shades", 3825),
      new Item(76, "shapechange", 3825),
      new Item(79, "soul bind", 3825),
      new Item(83, "summon monster IX", 3825),
      new Item(86, "teleportation circle", 4825),
      new Item(91, "time stop", 3825),
      new Item(95, "wail of the banshee", 3825),
      new Item(99, "weird", 3825),
      new Item(100, "wish", 28825));

  private static final List<Item> SCROLLS_DIVINE_0 = ImmutableList.of(
      new Item(7, "create water", 13),
      new Item(14, "cure minor wounds", 13),
      new Item(22, "detect magic", 13),
      new Item(29, "detect poison", 13),
      new Item(36, "flare", 13),
      new Item(43, "guidance", 13),
      new Item(50, "inflict minor wounds", 13),
      new Item(57, "know direction", 13),
      new Item(65, "light", 13),
      new Item(72, "mending", 13),
      new Item(79, "purify food and drink", 13),
      new Item(86, "read magic", 13),
      new Item(93, "resistance", 13),
      new Item(100, "virtue", 13));

  private static final List<Item> SCROLLS_DIVINE_1 = ImmutableList.of(
      new Item(3, "bane", 25),
      new Item(6, "bless", 25),
      new Item(9, "bless water", 50),
      new Item(10, "bless weapon", 100),
      new Item(12, "calm animals", 25),
      new Item(14, "cause fear", 25),
      new Item(16, "charm animal", 25),
      new Item(19, "command", 25),
      new Item(21, "comprehend languages", 25),
      new Item(26, "cure light wounds", 25),
      new Item(28, "curse water", 50),
      new Item(30, "deathwatch", 25),
      new Item(32, "detect animals or plants", 25),
      new Item(35, "detect choas/evil/good/law", 25),
      new Item(37, "detect snares and pits", 25),
      new Item(39, "detect undead", 25),
      new Item(41, "divine favor", 25),
      new Item(43, "doom", 25),
      new Item(48, "endure elements", 25),
      new Item(50, "entangle", 25),
      new Item(52, "entropic shield", 25),
      new Item(54, "faerie fire", 25),
      new Item(56, "goodberry", 25),
      new Item(58, "hide from animals", 25),
      new Item(60, "hide from undead", 25),
      new Item(62, "inflict light wounds", 25),
      new Item(64, "jump", 25),
      new Item(66, "longstrider", 25),
      new Item(68, "magic fang", 25),
      new Item(72, "magic stone", 25),
      new Item(74, "magic weapon", 25),
      new Item(78, "obscuring mist", 25),
      new Item(80, "pass without trace", 25),
      new Item(82, "produce flame", 25),
      new Item(86, "protection from chaos/evil/good/law", 25),
      new Item(88, "remove fear", 25),
      new Item(90, "sanctuary", 25),
      new Item(92, "shield of faith", 25),
      new Item(94, "shillelagh", 25),
      new Item(96, "speak with animals", 25),
      new Item(98, "summon monster I", 25),
      new Item(100, "summon nature's ally I", 25));

  private static final List<Item> SCROLLS_DIVINE_2 = ImmutableList.of(
      new Item(1, "animal messenger", 150),
      new Item(2, "animal trance", 150),
      new Item(4, "augury", 175),
      new Item(6, "barkskin", 150),
      new Item(9, "bear's endurance", 150),
      new Item(12, "bull's strength", 150),
      new Item(14, "calm emotions", 150),
      new Item(17, "cat's grace", 150),
      new Item(18, "chill metal", 150),
      new Item(20, "consecrate", 200),
      new Item(24, "cure moderate wounds", 150),
      new Item(26, "darkness", 150),
      new Item(27, "death knell", 150),
      new Item(30, "delay poison", 150),
      new Item(32, "desecrate", 200),
      new Item(35, "eagle's splendor", 150),
      new Item(37, "enthrall", 150),
      new Item(39, "find traps", 150),
      new Item(40, "fire trap", 157),
      new Item(42, "flame blade", 150),
      new Item(44, "flaming sphere", 150),
      new Item(46, "fog cloud", 150),
      new Item(47, "gentle repose", 150),
      new Item(48, "gust of wind", 150),
      new Item(49, "heat metal", 150),
      new Item(51, "hold animal", 150),
      new Item(54, "hold person", 150),
      new Item(56, "inclifct moderate wounds", 150),
      new Item(58, "make whole", 150),
      new Item(61, "owl's wisdom", 150),
      new Item(62, "reduce animal", 150),
      new Item(64, "remove paralysis", 150),
      new Item(67, "resist energy", 150),
      new Item(70, "lesser restoration", 150),
      new Item(72, "shatter", 150),
      new Item(74, "shield other", 150),
      new Item(76, "silence", 150),
      new Item(77, "snare", 150),
      new Item(78, "soften earth and stone", 150),
      new Item(80, "sound burst", 150),
      new Item(81, "speak with plants", 150),
      new Item(83, "spider climb", 150),
      new Item(85, "spiritual weapon", 150),
      new Item(86, "status", 150),
      new Item(88, "summon monster II", 150),
      new Item(90, "summon nature's ally II", 150),
      new Item(92, "summon swarm", 150),
      new Item(93, "tree shape", 150),
      new Item(95, "undetectable alignment", 150),
      new Item(97, "warp wood", 150),
      new Item(98, "wood shape", 150),
      new Item(100, "zone of truth", 150));

  private static final List<Item> SCROLLS_DIVINE_3 = ImmutableList.of(
      new Item(2, "animate dead", 625),
      new Item(4, "bestow curse", 375),
      new Item(6, "blindness/deafness", 375),
      new Item(8, "call lightning", 375),
      new Item(10, "contagion", 375),
      new Item(12, "continual flame", 425),
      new Item(14, "create food and water", 375),
      new Item(18, "cure serous wounds", 375),
      new Item(19, "darkvision", 375),
      new Item(21, "daylight", 375),
      new Item(23, "deeper darkness", 375),
      new Item(25, "dminish plants", 375),
      new Item(27, "dispel magic", 375),
      new Item(29, "dominate animal", 375),
      new Item(31, "glyph of warding", 375),
      new Item(32, "heal mount", 375),
      new Item(34, "helping hand", 375),
      new Item(36, "inflict serious woundes", 375),
      new Item(38, "invisibility purge", 375),
      new Item(40, "locate object", 375),
      new Item(46, "magic circle against chaos/evil/good/law", 375),
      new Item(48, "greater magic fang", 375),
      new Item(50, "magic vestment", 375),
      new Item(52, "meld into stone", 375),
      new Item(55, "neutralize poison", 375),
      new Item(57, "obscure object", 375),
      new Item(59, "plant growth", 375),
      new Item(62, "prayer", 375),
      new Item(64, "protection from energy", 375),
      new Item(66, "quench", 375),
      new Item(69, "remove blindness/deafness", 375),
      new Item(71, "remove curse", 375),
      new Item(73, "remove disease", 375),
      new Item(76, "searing light", 375),
      new Item(78, "sleet storm", 375),
      new Item(80, "snare", 375),
      new Item(83, "speak with dead", 375),
      new Item(85, "speak with plants", 375),
      new Item(87, "spike growth", 375),
      new Item(89, "stone shape", 375),
      new Item(91, "summon monster III", 375),
      new Item(93, "summon nature's ally III", 375),
      new Item(96, "water breathing", 375),
      new Item(98, "water walk", 375),
      new Item(100, "wind wall", 375));

  private static final List<Item> SCROLLS_DIVINE_4 = ImmutableList.of(
      new Item(5, "air walk", 700),
      new Item(7, "antiplant shell", 700),
      new Item(9, "blight", 700),
      new Item(11, "break enchantment", 700),
      new Item(13, "command plants", 700),
      new Item(15, "control water", 700),
      new Item(21, "cure critical wounds", 700),
      new Item(26, "death ward", 700),
      new Item(31, "dimensional anchor", 700),
      new Item(34, "discern lies", 700),
      new Item(37, "dismissal", 700),
      new Item(39, "diviniation", 725),
      new Item(42, "divine power", 700),
      new Item(47, "freedom of movement", 700),
      new Item(49, "giant vermin", 700),
      new Item(51, "holy sword", 700),
      new Item(54, "imbue with spell ability", 700),
      new Item(57, "inflict cirtical wounds", 700),
      new Item(60, "greater magic weapon", 700),
      new Item(62, "nondetection", 750),
      new Item(64, "lesser planar ally", 1200),
      new Item(67, "poison", 700),
      new Item(69, "reincarnate", 700),
      new Item(71, "repel vermin", 700),
      new Item(76, "restoration", 800),
      new Item(78, "rusting grasp", 700),
      new Item(81, "sending", 700),
      new Item(85, "spell immunity", 700),
      new Item(87, "spike stones", 700),
      new Item(90, "summon monster IV", 700),
      new Item(93, "summon nature's ally IV", 700),
      new Item(98, "tongues", 700),
      new Item(100, "tree stride", 700));

  private static final List<Item> SCROLLS_DIVINE_5 = ImmutableList.of(
      new Item(3, "animal growth", 1125),
      new Item(5, "atonement", 3625),
      new Item(6, "awake", 2375),
      new Item(9, "baleful polymorph", 1125),
      new Item(13, "break enchantment", 1125),
      new Item(16, "call lightning storm", 1125),
      new Item(20, "greater command", 1125),
      new Item(21, "commune", 1625),
      new Item(22, "commune with nature", 1125),
      new Item(24, "control winds", 1125),
      new Item(30, "mass cure light wounds", 1125),
      new Item(34, "dispel chaos/evil/good/law", 1125),
      new Item(38, "disrupting weapon", 1125),
      new Item(41, "flame strike", 1125),
      new Item(43, "hallow", 6125),
      new Item(46, "ice storm", 1125),
      new Item(49, "mass inflict light wounds", 1125),
      new Item(52, "insect plague", 1125),
      new Item(53, "mark of justice", 1125),
      new Item(56, "plane shift", 1125),
      new Item(58, "raise dead", 6125),
      new Item(61, "righteous might", 1125),
      new Item(63, "scrying", 1125),
      new Item(66, "slay living", 1125),
      new Item(69, "spell resistance", 1125),
      new Item(71, "stoneskin", 1375),
      new Item(74, "summon monster V", 1125),
      new Item(77, "summon nature's ally V", 1125),
      new Item(78, "symbol of pain", 2125),
      new Item(79, "symbol of sleep", 2125),
      new Item(82, "transmute mud to rock", 1125),
      new Item(85, "transmute rock to mud", 1125),
      new Item(89, "true seeing", 1375),
      new Item(91, "unhallow", 6125),
      new Item(94, "wall of fire", 1125),
      new Item(97, "wall of stone", 1125),
      new Item(100, "wall of thorns", 1125));

  private static final List<Item> SCROLLS_DIVINE_6 = ImmutableList.of(
      new Item(3, "animate objects", 1650),
      new Item(6, "antilife shell", 1650),
      new Item(9, "banishment", 1650),
      new Item(13, "mass bear's endurance", 1650),
      new Item(16, "blade barrier", 1650),
      new Item(20, "mass bull's strength", 1650),
      new Item(24, "mass cat's grace", 1650),
      new Item(25, "create undead", 1650),
      new Item(29, "mass cure moderate wounds", 1650),
      new Item(33, "greater dispel magic", 1650),
      new Item(37, "mass eagle's splendor", 1650),
      new Item(40, "find the path", 1650),
      new Item(43, "fire seeds", 1650),
      new Item(44, "forbiddance", 4650),
      new Item(45, "geas/quest", 1650),
      new Item(46, "greater glyph of warding", 1650),
      new Item(49, "harm", 1650),
      new Item(52, "heal", 1650),
      new Item(55, "heroes' feast", 1650),
      new Item(58, "mass inflict moderate wounds", 1650),
      new Item(61, "ironwood", 1650),
      new Item(62, "liveoak", 1650),
      new Item(65, "move earth", 1650),
      new Item(69, "mass owl's wisdom", 1650),
      new Item(71, "planar ally", 2400),
      new Item(74, "repel wood", 1650),
      new Item(77, "spellstaff", 1650),
      new Item(80, "stone tell", 1650),
      new Item(83, "summon monster VI", 1650),
      new Item(86, "summon nature's ally VI", 1650),
      new Item(87, "symbol of fear", 2650),
      new Item(88, "symbol of persuasion", 6650),
      new Item(91, "transport via plants", 1650),
      new Item(94, "undeath to death", 2150),
      new Item(97, "wind walk", 1650),
      new Item(100, "word of recall", 1650));

  private static final List<Item> SCROLLS_DIVINE_7 = ImmutableList.of(
      new Item(5, "animate plants", 2275),
      new Item(9, "blasphemy", 2275),
      new Item(14, "changestaff", 2275),
      new Item(16, "control weather", 2275),
      new Item(21, "creeping doom", 2275),
      new Item(27, "mass cure serious wounds", 2275),
      new Item(32, "destruction", 2275),
      new Item(36, "dictum", 2275),
      new Item(41, "ethereal jaunt", 2275),
      new Item(45, "holy word", 2275),
      new Item(50, "mass inflict serious wounds", 2275),
      new Item(55, "refuge", 3775),
      new Item(60, "regenerate", 2275),
      new Item(65, "repulsion", 2275),
      new Item(69, "greater restoration", 4775),
      new Item(71, "resurrection", 12275),
      new Item(76, "greater scrying", 2275),
      new Item(81, "summon monster VII", 2275),
      new Item(85, "summon nature's ally VII", 2275),
      new Item(90, "sunbeam", 2275),
      new Item(91, "symbol of stunning", 7275),
      new Item(92, "symbol of weakness", 7275),
      new Item(97, "transmute metal to wood", 2275),
      new Item(100, "word of chaos", 2275));

  private static final List<Item> SCROLLS_DIVINE_8 = ImmutableList.of(
      new Item(4, "animal shapes", 3000),
      new Item(10, "antimagic field", 3000),
      new Item(13, "cloak of chaos", 3000),
      new Item(17, "control plants", 3000),
      new Item(20, "create greater undead", 3600),
      new Item(27, "mass cure critical wounds", 3000),
      new Item(32, "dimensional lock", 3000),
      new Item(36, "discern location", 3000),
      new Item(41, "earthquake", 3000),
      new Item(45, "finger of death", 3000),
      new Item(49, "fire storm", 3000),
      new Item(52, "holy aura", 3000),
      new Item(56, "mass inflict critical wounds", 3000),
      new Item(60, "greater planar ally", 5500),
      new Item(65, "repel metal or stone", 3000),
      new Item(69, "reverse gravity", 3000),
      new Item(72, "shield of law", 3000),
      new Item(76, "greater spell immunity", 3000),
      new Item(80, "summon monster VIII", 3000),
      new Item(84, "summon nature's ally VIII", 3000),
      new Item(89, "sunburst", 3000),
      new Item(91, "symbol of death", 8000),
      new Item(93, "symbol of insanity", 8000),
      new Item(96, "unholy aura", 3000),
      new Item(100, "whirlwind", 3000));

  private static final List<Item> SCROLLS_DIVINE_9 = ImmutableList.of(
      new Item(4, "antipathy", 3825),
      new Item(7, "astral projection", 4870),
      new Item(13, "elemental swarm", 3825),
      new Item(19, "energy drain", 3825),
      new Item(25, "etherealness", 3825),
      new Item(31, "foresight", 3825),
      new Item(37, "gate", 8825),
      new Item(46, "mass heal", 3825),
      new Item(53, "implosion", 3825),
      new Item(55, "miracle", 28825),
      new Item(61, "regenerate", 3825),
      new Item(66, "shambler", 3825),
      new Item(72, "shapechange", 3825),
      new Item(77, "soul bind", 3825),
      new Item(83, "storm of vengeance", 3825),
      new Item(89, "summon monster IX", 3825),
      new Item(95, "summon nature's ally IV", 3825),
      new Item(99, "sympathy", 5325),
      new Item(100, "true resurrection", 28825));

  private static final List<Magic> MINOR = ImmutableList.of(
      // Armor and shield
      new Magic(4, "", ImmutableList.of(
          new Magic(60, "+1", 1, 0, RANDOM_SHIELDS),
          new Magic(80, "+1", 1, 0, RANDOM_ARMOR),
          new Magic(85, "+2", 2, 0, RANDOM_SHIELDS),
          new Magic(87, "+2", 2, 0, RANDOM_ARMOR),
          // Specific armor
          new Magic(89, "", 0, 0, ImmutableList.of(
              new Item(50, "mithral shirt", 1100),
              new Item(80, "dragonhite plate", 3300),
              new Item(100, "elven chain", 4150)
          )),
          // Specific shield
          new Magic(91, "", 0, 0, ImmutableList.of(
              new Item(30, "darkwood buckler", 205),
              new Item(80, "darkwood shield", 257),
              new Item(95, "mithral heavy shield", 1020),
              new Item(100, "caster's shield", 3153)
          )),
          // Special ability
          new Magic(92, 0, 0, Value.Result.Status.reroll, ImmutableList.of(
              new Magic(25, "glamered", 2700),
              new Magic(32, "light fortification", 1, 0),
              new Magic(52, "slick", 3, 0),
              new Magic(72, "shadow", 3, 0),
              new Magic(92, "silent moves", 3, 0),
              new Magic(96, "spell resistance (13)", 2, 0),
              new Magic(97, "improved slick", 15000),
              new Magic(98, "improved shadow", 15000),
              new Magic(99, "improved silent moves", 15000),
              new Magic(100, "", Value.Result.Status.rerollTwo)))
      )),
      // Weapons
      new Magic(9, "", ImmutableList.of(
          // Common melee weapons
          new Magic(70, "", ImmutableList.of(
              new Magic(70, "+1", 0, 1, WEAPON_COMMON_MELEE),
              new Magic(85, "+2", 0, 2, WEAPON_COMMON_MELEE),
              // Specific weapon
              new Magic(90, "", 0, 0, WEAPON_SPECIFIC_MINOR),
              // Special ability
              new Magic(100, "", WEAPON_MELEE_SPECIAL_MINOR))),
          // Uncommon melee weapons
          new Magic(80, "", ImmutableList.of(
              new Magic(70, "+1", 0, 1, WEAPON_UNCOMMON_MELEE),
              new Magic(85, "+2", 0, 2, WEAPON_UNCOMMON_MELEE),
              // Specific weapon
              new Magic(90, "", 0, 0, WEAPON_SPECIFIC_MINOR),
              // Special ability
              new Magic(100, "", WEAPON_MELEE_SPECIAL_MINOR))),
          // Common ranged weapons
          new Magic(100, "", ImmutableList.of(
              new Magic(70, "+1", 0, 1, WEAPON_RANGED),
              new Magic(85, "+2", 0, 2, WEAPON_RANGED),
              // Specific weapon
              new Magic(90, "", 0, 0, WEAPON_SPECIFIC_MINOR),
              // Special ability
              new Magic(100, "", WEAPON_RANGED_SPECIAL_MINOR))))),
      new Magic(44, "", 0, 0, ImmutableList.of(
        new Item(10, "potion of cure light wounds", 50),
        new Item(13, "potion of endure elements", 50),
        new Item(15, "potion of hide from animals", 50),
        new Item(17, "potion of hide from undead", 50),
        new Item(19, "potion of jump", 50),
        new Item(22, "potion of mage armor", 50),
        new Item(25, "potion of magic fang", 50),
        new Item(26, "oil of magic stone", 50),
        new Item(29, "oil of magic weapon", 50),
        new Item(30, "potion of pass without trace", 50),
        new Item(32, "potion of protection from alignment", 50),
        new Item(34, "potion of remove fear", 50),
        new Item(35, "potion of sanctuary", 50),
        new Item(38, "potion of shield of faith +2", 50),
        new Item(39, "oil of shillelagh", 50),
        new Item(41, "oil of bless weapon", 100),
        new Item(44, "potion of enlarge person", 250),
        new Item(45, "potion of reduce person", 250),
        new Item(47, "potion of aid", 300),
        new Item(50, "potion of barskin +2", 300),
        new Item(53, "potion of bear's endurance", 300),
        new Item(56, "potion of blur", 300),
        new Item(59, "potion of bull's strength", 300),
        new Item(62, "potion of cat's grace", 300),
        new Item(67, "potion of cure moderate wounds", 300),
        new Item(68, "oil of darkness", 300),
        new Item(71, "potion of darkvision", 300),
        new Item(74, "potion of delay poison", 300),
        new Item(76, "potion of eagle's splendor", 300),
        new Item(78, "potion of fox's cunning", 300),
        new Item(81, "potion of invisibility", 300),
        new Item(84, "potion of lesser restoration", 300),
        new Item(86, "potion of levitate", 300),
        new Item(87, "potion of misdirection", 300),
        new Item(89, "potion of owl's wisdom", 300),
        new Item(91, "potion of protection from arrows 10/magic", 300),
        new Item(93, "potion of remove paralysis", 300),
        new Item(96, "potion of resist energy 10", 300),
        new Item(97, "potion of shield of faith +3", 300),
        new Item(99, "potion of spider climb", 300),
        new Item(100, "potion of undetectable alignment", 300))),
      new Magic(46, "ring of", 0, 0, ImmutableList.of(
          new Item(18, "protection +1", 2000),
          new Item(28, "feather falling", 2200),
          new Item(36, "sustenance", 2500),
          new Item(44, "climbing", 2500),
          new Item(52, "jumping", 2500),
          new Item(60, "swimming", 2500),
          new Item(70, "counterspells", 4000),
          new Item(75, "mind shielding", 8000),
          new Item(80, "protection +2", 8000),
          new Item(85, "force shield", 8500),
          new Item(90, "ram", 8600),
          new Item(93, "animal friendship", 10800),
          new Item(96, "minor energy resistance", 12000),
          new Item(98, "chameleon power", 12700),
          new Item(100, "water walking", 15000))),
      new Magic(81, "", ImmutableList.of(
          new Magic(70, "arcane scroll of", new Dice(1, 3), ImmutableList.of(
              new Magic(5, "", 0, 0, SCROLLS_ARCANE_0),
              new Magic(50, "", 0, 0, SCROLLS_ARCANE_1),
              new Magic(95, "", 0, 0, SCROLLS_ARCANE_2),
              new Magic(100, "", 0, 0, SCROLLS_ARCANE_3))),
          new Magic(100, "divine scroll of", new Dice(1, 3), ImmutableList.of(
              new Magic(5, "", 0, 0, SCROLLS_DIVINE_0),
              new Magic(50, "", 0, 0, SCROLLS_DIVINE_1),
              new Magic(95, "", 0, 0, SCROLLS_DIVINE_2),
              new Magic(100, "", 0, 0, SCROLLS_DIVINE_3))))),
      new Magic(91, "wand of", 0, 0, ImmutableList.of(
          new Item(2, "detect magic", 375),
          new Item(4, "light", 375),
          new Item(7, "burning hands", 750),
          new Item(10, "charm animal", 750),
          new Item(13, "charm person", 750),
          new Item(15, "color spray", 750),
          new Item(19, "cure light wounds", 750),
          new Item(22, "detect secret doors", 750),
          new Item(25, "enlarge person", 750),
          new Item(28, "magic missile (1st)", 750),
          new Item(31, "shocking grasp", 750),
          new Item(34, "summon monster I", 750),
          new Item(36, "magic missile (3rd)", 2250),
          new Item(37, "magic missile (5th)", 3750),
          new Item(40, "bear's endurance", 4500),
          new Item(43, "bull's strength", 4500),
          new Item(46, "cat's grace", 4500),
          new Item(49, "cure moderate wounds", 4500),
          new Item(51, "darkness", 4500),
          new Item(54, "daylight", 4500),
          new Item(57, "delay poison", 4500),
          new Item(60, "eagle's splendor", 4500),
          new Item(63, "false life", 4500),
          new Item(66, "fox's cunning", 4500),
          new Item(68, "ghoul touch", 4500),
          new Item(71, "hold person", 4500),
          new Item(74, "invisibility", 4500),
          new Item(77, "knock", 4500),
          new Item(80, "levitate", 4500),
          new Item(83, "Melf's acid arrow", 4500),
          new Item(86, "mirror image", 4500),
          new Item(89, "owl's wisdom", 4500),
          new Item(91, "shatter", 4500),
          new Item(94, "silence", 4500),
          new Item(97, "summon monster II", 4500),
          new Item(100, "web", 4500))),
      new Magic(100, "", 0, 0, ImmutableList.of(
          new Item(1, "Quaal's feather token (anchor)", 50),
          new Item(2, "universal solvent", 50),
          new Item(3, "elixir of love", 150),
          new Item(4, "unguent of timelessness", 150),
          new Item(5, "Quaal's feather token (fan)", 200),
          new Item(6, "dust of tracelessness", 250),
          new Item(7, "elixir of hiding", 250),
          new Item(8, "elixir of sneaking", 250),
          new Item(9, "elixir of swimming", 250),
          new Item(10, "elixir of vision", 250),
          new Item(11, "silversheen", 250),
          new Item(12, "Quaal's feather token (bird)", 300),
          new Item(13, "Quaal's feather token (tree)", 400),
          new Item(14, "Quaal's feather token (swan boat)", 450),
          new Item(15, "elixir of truth", 500),
          new Item(16, "Quaal's feather token (whip)", 500),
          new Item(17, "dust of dryness", 850),
          new Item(18, "gray bag of tricks", 900),
          new Item(19, "hand of the mage", 900),
          new Item(20, "bracers of armor +1", 1000),
          new Item(21, "cloak of resistance +1", 1000),
          new Item(22, "pearl of power (1st)", 1000),
          new Item(23, "phylactery of faithfulness", 1000),
          new Item(24, "salve of slipperiness", 1000),
          new Item(25, "elixir of fire breath", 1100),
          new Item(26, "pipes of the sewers", 1150),
          new Item(27, "dust of illusion", 1200),
          new Item(28, "goggles of minute seeing", 1250),
          new Item(29, "brooch of shielding", 1500),
          new Item(30, "necklace of fireballs (type I)", 1650),
          new Item(31, "dust of appearance", 1800),
          new Item(32, "hat of disguise", 1800),
          new Item(33, "pipes of sounding", 1800),
          new Item(34, "quiver of Ehlonna", 1800),
          new Item(35, "Amulet of natural armor +1", 2000),
          new Item(36, "Heward's handy haversack", 2000),
          new Item(37, "horn of fog", 2000),
          new Item(38, "elemental gem", 2250),
          new Item(39, "robe of bones", 2400),
          new Item(40, "sovereign glue", 2400),
          new Item(41, "bag of holding (type I)", 2500),
          new Item(42, "boots of elvenkind", 2500),
          new Item(43, "boots of the winterlands", 2500),
          new Item(44, "candle of truth", 2500),
          new Item(45, "cloak of elvenkind", 2500),
          new Item(46, "eyes of the eagle", 2500),
          new Item(47, "golembane scarab", 2500),
          new Item(48, "necklace of fireballs (type II)", 2700),
          new Item(49, "stone of alarm", 2700),
          new Item(50, "rust bag of tricks", 3000),
          new Item(51, "bead of force", 3000),
          new Item(52, "chime of opening", 3000),
          new Item(53, "horseshoes of speed", 3000),
          new Item(54, "rope of climbing", 3000),
          new Item(55, "dust of disappearance", 3500),
          new Item(56, "lens of detection", 3500),
          new Item(57, "druid's vestment", 3750),
          new Item(58, "silver raven figurine of wondrous power", 3800),
          new Item(59, "amulet of health +2", 4000),
          new Item(60, "bracers of armor +2", 4000),
          new Item(61, "cloak of charisma +2", 4000),
          new Item(62, "cloak of resistance +2", 4000),
          new Item(63, "gauntlets of ogre power", 4000),
          new Item(64, "gloves of arrow snaring", 4000),
          new Item(65, "gloves of dexterity +2", 4000),
          new Item(66, "headband of intellect +2", 4000),
          new Item(67, "clear spindle ioun stone", 4000),
          new Item(68, "Keaghtom's ointment", 4000),
          new Item(69, "Nolzur's marvelous pigments", 4000),
          new Item(70, "pearl of power (2nd)", 4000),
          new Item(71, "periapt of wisdom +2", 4000),
          new Item(72, "stone salve", 4000),
          new Item(73, "necklace of fireballs (type III)", 4350),
          new Item(74, "circlet of persuasion", 4500),
          new Item(75, "slippers of spider climbing", 4800),
          new Item(76, "incense of meditation", 4900),
          new Item(77, "bag of holding (type II)", 5000),
          new Item(78, "lesser bracers of archery", 5000),
          new Item(79, "dusty rose prism ioun stone", 5000),
          new Item(80, "helm of comprehend languages and read magic", 5200),
          new Item(81, "vest of escape", 5200),
          new Item(82, "eversmoking bottle", 5400),
          new Item(83, "Muylynd's spoon", 5400),
          new Item(84, "necklace of fireballs (type IV)", 5400),
          new Item(85, "boots of striding and springing", 5500),
          new Item(86, "wind fan", 5500),
          new Item(87, "amulet of mighty fists +1", 6000),
          new Item(88, "horeshoes of a zephyr", 6000),
          new Item(89, "pipes of haunting", 6000),
          new Item(90, "necklace of fireballs (type V)", 6150),
          new Item(91, "gloves of swimming and climbing", 6250),
          new Item(92, "tan bag of tricks", 6300),
          new Item(93, "minor circlet of blasting", 6480),
          new Item(94, "horn of gooness/evil", 7000),
          new Item(95, "robe of useful items", 7000),
          new Item(96, "folding boat", 7200),
          new Item(97, "cloak of the manta ray", 7200),
          new Item(98, "bottle of air", 7250),
          new Item(99, "bag of holding (type III)", 7400),
          new Item(100, "periapt of health", 7400))));

  private static final List<Magic> MEDIUM = ImmutableList.of(
      // Armor and shield
      new Magic(10, "", ImmutableList.of(
          new Magic(5, "+1", 1, 0, RANDOM_SHIELDS),
          new Magic(10, "+1", 1, 0, RANDOM_ARMOR),
          new Magic(20, "+2", 2, 0, RANDOM_SHIELDS),
          new Magic(30, "+2", 2, 0, RANDOM_ARMOR),
          new Magic(40, "+3", 3, 0, RANDOM_SHIELDS),
          new Magic(50, "+3", 3, 0, RANDOM_ARMOR),
          new Magic(55, "+4", 4, 0, RANDOM_SHIELDS),
          new Magic(57, "+4", 4, 0, RANDOM_ARMOR),
          // Specific armor
          new Magic(60, "", 0, 0, ImmutableList.of(
              new Item(25, "mithral shirt", 1100),
              new Item(45, "dragonhite plate", 3300),
              new Item(57, "elven chain", 4150),
              new Item(67, "rhino hide", 5165),
              new Item(82, "adamantine breastplate", 10200),
              new Item(97, "dwarven plate", 16500),
              new Item(100, "banded mail of luck", 18900)
          )),
          // Specific shield
          new Magic(63, "", 0, 0, ImmutableList.of(
              new Item(20, "darkwood buckler", 205),
              new Item(45, "darkwood shield", 257),
              new Item(70, "mithral heavy shield", 1020),
              new Item(85, "caster's shield", 3153),
              new Item(90, "spined shield", 5580),
              new Item(95, "lion's shield", 9170),
              new Item(100, "winged shield", 17257)
          )),
          new Magic(64, 0, 0, Value.Result.Status.reroll, ImmutableList.of(
              new Magic(5, "glamered", 2700),
              new Magic(8, "light fortification", 1, 0),
              new Magic(11, "slick", 3, 0),
              new Magic(14, "shadow", 3, 0),
              new Magic(17, "silent moves", 3, 0),
              new Magic(19, "spell resistance (13)", 2, 0),
              new Magic(29, "improved slick", 15000),
              new Magic(39, "improved shadow", 15000),
              new Magic(49, "improved silent moves", 15000),
              new Magic(54, "acid resistance", 18000),
              new Magic(59, "cold resistance", 18000),
              new Magic(64 ,"electricity resistance", 18000),
              new Magic(69, "fire resistance", 18000),
              new Magic(74, "sonic resistance", 18000),
              new Magic(79, "ghost touch", 3, 0),
              new Magic(84, "invulnerability", 3, 0),
              new Magic(89, "moderate fortification", 3, 0),
              new Magic(94, "spell resistance (15)", 3, 0),
              new Magic(99, "wild", 3, 0),
              new Magic(100, "", Value.Result.Status.rerollTwo)))
      )),
      // Weapon
      new Magic(20, "", ImmutableList.of(
          // Common melee weapons
          new Magic(70, "", ImmutableList.of(
              new Magic(10, "+1", 0, 1, WEAPON_COMMON_MELEE),
              new Magic(29, "+2", 0, 2, WEAPON_COMMON_MELEE),
              new Magic(58, "+3", 0, 3, WEAPON_COMMON_MELEE),
              new Magic(62, "+4", 0, 4, WEAPON_COMMON_MELEE),
              // Specific weapon
              new Magic(68, "", 0, 0, WEAPON_SPECIFIC_MEDIUM),
              // Special ability
              new Magic(100, "", WEAPON_MELEE_SPECIAL_MEDIUM))),
          // Uncommon melee weapons
          new Magic(80, "", ImmutableList.of(
              new Magic(70, "+1", 0, 1, WEAPON_UNCOMMON_MELEE),
              new Magic(85, "+2", 0, 2, WEAPON_UNCOMMON_MELEE),
              // Specific weapon
              new Magic(90, "", 0, 0, WEAPON_SPECIFIC_MEDIUM),
              // Special ability
              new Magic(100, "", WEAPON_MELEE_SPECIAL_MEDIUM))),
          // Common ranged weapons
          new Magic(100, "", ImmutableList.of(
              new Magic(70, "+1", 0, 1, WEAPON_RANGED),
              new Magic(85, "+2", 0, 2, WEAPON_RANGED),
              // Specific weapon
              new Magic(90, "", 0, 0, WEAPON_SPECIFIC_MEDIUM),
              // Special ability
              new Magic(100, "", WEAPON_RANGED_SPECIAL_MEDIUM))))),
      new Magic(30, "Potion", 0, 0, ImmutableList.of(
        new Item(2, "oil of bless weapon", 100),
        new Item(4, "potion of enlarge person", 250),
        new Item(5, "potion of reduce person", 250),
        new Item(6, "potion of aid", 300),
        new Item(7, "potion of barskin +2", 300),
        new Item(10, "potion of bear's endurance", 300),
        new Item(13, "potion of blur", 300),
        new Item(16, "potion of bull's strength", 300),
        new Item(19, "potion of cat's grace", 300),
        new Item(27, "potion of cure moderate wounds", 300),
        new Item(28, "oil of darkness", 300),
        new Item(30, "potion of darkvision", 300),
        new Item(31, "potion of delay poison", 300),
        new Item(33, "potion of eagle's splendor", 300),
        new Item(35, "potion of fox's cunning", 300),
        new Item(37, "potion of invisibility", 300),
        new Item(38, "potion of lesser restoration", 300),
        new Item(39, "potion of levitate", 300),
        new Item(40, "potion of misdirection", 300),
        new Item(42, "potion of owl's wisdom", 300),
        new Item(43, "potion of protection from arrows 10/magic", 300),
        new Item(44, "potion of remove paralysis", 300),
        new Item(46, "potion of resist energy 10", 300),
        new Item(48, "potion of shield of faith +3", 300),
        new Item(49, "potion of spider climb", 300),
        new Item(50, "potion of undetectable alignment", 300),
        new Item(51, "potion of barkskin +3", 600),
        new Item(52, "potion of shield of faith +4", 600),
        new Item(55, "potion of resist energy 20", 700),
        new Item(60, "potion of cure serious wounds", 750),
        new Item(61, "oil of daylight", 750),
        new Item(64, "potion of displacement", 750),
        new Item(65, "oil of flame arrow", 750),
        new Item(68, "potion of fly", 750),
        new Item(69, "potion of gaseous form", 750),
        new Item(71, "potion of greater magic fang +1", 750),
        new Item(73, "oil of magic weapon +1", 750),
        new Item(75, "potion of haste", 750),
        new Item(78, "potion of heroism", 750),
        new Item(80, "oil of keen edge", 750),
        new Item(81, "potion of magic circle against alignment", 750),
        new Item(83, "oil of magic vestment", 750),
        new Item(86, "potion of neutralize poison", 750),
        new Item(88, "potion of nondetection", 750),
        new Item(91, "potion of protection from energy", 760),
        new Item(93, "potion of rage", 750),
        new Item(94, "potion of remove blindness/deafness", 750),
        new Item(95, "potion of remove curse", 750),
        new Item(96, "potion of remove disease", 750),
        new Item(97, "potion of tongues", 750),
        new Item(98, "potion of water breathing", 750),
        new Item(100, "potion of water walk", 750))),
      new Magic(40, "ring of", 0, 0, ImmutableList.of(
          new Item(5, "counterspells", 4000),
          new Item(8, "mind shielding", 8000),
          new Item(18, "protection +2", 8000),
          new Item(23, "force shield", 8500),
          new Item(28, "ram", 8600),
          new Item(34, "improved climbing", 10000),
          new Item(40, "improved jumping", 10000),
          new Item(46, "improved swimming", 10000),
          new Item(51, "animal friendship", 10800),
          new Item(56, "minor energy resistance", 12000),
          new Item(61, "chameleon power", 12700),
          new Item(66, "water walking", 15000),
          new Item(71, "protection +3", 18000),
          new Item(76, "minor spell storing", 18000),
          new Item(81, "invisibility", 20000),
          new Item(85, "wizardry I", 20000),
          new Item(90, "evasion", 25000),
          new Item(93, "x-ray vision", 25000),
          new Item(97, "blinking", 27000),
          new Item(100, "major energy resistance", 28000))),
      new Magic(50, "rod of", 0, 0, ImmutableList.of(
          new Item(7, "lesser enlarge metamagic", 3000),
          new Item(14, "lesser extend metamagic", 3000),
          new Item(21, "lesser silent metamagic", 3000),
          new Item(28, "immovable", 5000),
          new Item(35, "lesser empower metamagic", 9000),
          new Item(42, "metal and mineral detection", 10500),
          new Item(53, "cancellation", 11000),
          new Item(57, "enlarge metamagic", 11000),
          new Item(61, "extend metamagic", 11000),
          new Item(65, "silent megamagic", 11000),
          new Item(71, "wonder", 12000),
          new Item(79, "python", 13000),
          new Item(83, "lesser maximize metamagic", 14000),
          new Item(89, "flame extinguishing", 15000),
          new Item(97, "viper", 19000),
          new Item(99, "empower metamagic", 32500),
          new Item(100, "lesser quicken metamagic", 35000))),
      new Magic(65, "", ImmutableList.of(
          new Magic(70, "arcane scroll of", new Dice(1, 4), ImmutableList.of(
              new Magic(5, "", 0, 0, SCROLLS_ARCANE_2),
              new Magic(65, "", 0, 0, SCROLLS_ARCANE_3),
              new Magic(95, "", 0, 0, SCROLLS_ARCANE_4),
              new Magic(100, "", 0, 0, SCROLLS_ARCANE_5))),
          new Magic(100, "divine scroll of", new Dice(1, 4), ImmutableList.of(
              new Magic(5, "", 0, 0, SCROLLS_DIVINE_2),
              new Magic(65, "", 0, 0, SCROLLS_DIVINE_3),
              new Magic(95, "", 0, 0, SCROLLS_DIVINE_4),
              new Magic(100, "", 0, 0, SCROLLS_DIVINE_5))))),
      new Magic(68, "staff of", 0, 0, ImmutableList.of(
          new Item(15, "charming", 16500),
          new Item(30, "fire", 17750),
          new Item(40, "swarming insects", 24750),
          new Item(60, "healing", 27750),
          new Item(75, "size alteration", 29000),
          new Item(90, "illumination", 48250),
          new Item(95, "frost", 56250),
          new Item(100, "defense", 58250))),
      new Magic(83, "wand of", 0, 0, ImmutableList.of(
          new Item(3, "magic missile (5th)", 3750),
          new Item(7, "bear's endurance", 4500),
          new Item(11, "bull's strength", 4500),
          new Item(15, "cat's grace", 4500),
          new Item(20, "cure moderate wounds", 4500),
          new Item(22, "darkness", 4500),
          new Item(24, "daylight", 4500),
          new Item(27, "delay poison", 4500),
          new Item(31, "eagle's splendor", 4500),
          new Item(33, "false life", 4500),
          new Item(37, "fox's cunning", 4500),
          new Item(38, "ghoul touch", 4500),
          new Item(39, "hold person", 4500),
          new Item(42, "invisibility", 4500),
          new Item(44, "knock", 4500),
          new Item(45, "levitate", 4500),
          new Item(47, "Melf's acid arrow", 4500),
          new Item(49, "mirror image", 4500),
          new Item(53, "owl's wisdom", 4500),
          new Item(54, "shatter", 4500),
          new Item(56, "silence", 4500),
          new Item(57, "summon monster II", 4500),
          new Item(59, "web", 4500),
          new Item(62, "magic missile (7th)", 5250),
          new Item(64, "magic missile (9th)", 6750),
          new Item(67, "call lightning (5th)", 11250),
          new Item(68, "heightened charm person (3rd)", 11250),
          new Item(70, "contagion", 11250),
          new Item(74, "cure serious wounds", 11250),
          new Item(77, "dispel magic", 11250),
          new Item(81, "fireball (5th)", 11250),
          new Item(83, "keen edge", 11250),
          new Item(87, "lightning bolt (5th)", 11250),
          new Item(89, "major image", 11250),
          new Item(91, "slow", 11250),
          new Item(94, "suggestion", 11250),
          new Item(97, "summon monster III", 11250),
          new Item(98, "fireball (6th)", 13500),
          new Item(99, "lightning bolt (6th)", 13500),
          new Item(100, "searing light (6th)", 13500))),
      new Magic(100, "Wondrous item", 0, 0, ImmutableList.of(
          new Item(1, "boots of levitation", 7500),
          new Item(2, "harp of charming", 7500),
          new Item(3, "amulet of natural armor +2", 8000),
          new Item(4, "flesh golem manual", 8000),
          new Item(5, "hand of glory", 8000),
          new Item(6, "deep red sphere ioun stone", 8000),
          new Item(7, "incandescent blue sphere ioun stone", 8000),
          new Item(8, "pale blue rhomboid ioun stone", 8000),
          new Item(9, "pink and green sphere ioun stone", 8000),
          new Item(10, "pink rhomboid ioun stone", 8000),
          new Item(11, "scarlet and blue sphere ioun stone", 8000),
          new Item(12, "deck of illusions", 8100),
          new Item(13, "necklace of fireballs (type VI)", 8100),
          new Item(14, "candle of invocation", 8400),
          new Item(15, "bracers of armor +3", 9000),
          new Item(16, "cloak of resistance +3", 9000),
          new Item(17, "decanter of endless water", 9000),
          new Item(18, "necklace of adaptation", 9000),
          new Item(19, "pearl of power (3rd)", 9000),
          new Item(20, "talisman of the sphere", 9000),
          new Item(21, "serpentine owl figurine of wondrous power", 9100),
          new Item(22, "necklace of fireballs (type VII)", 9150),
          new Item(23, "lesser strand of prayer beads", 9600),
          new Item(24, "bag of holding (type IV)", 10000),
          new Item(25, "bronze griffon figurine of wondrous power", 10000),
          new Item(26, "ebony fly figurine of woundrous power", 10000),
          new Item(27, "glove of storing", 10000),
          new Item(28, "dark blue rhomboid ioun stone", 10000),
          new Item(29, "courser stone horse", 10000),
          new Item(30, "cape of the mountebank", 10080),
          new Item(31, "phylactery of undead turning", 11000),
          new Item(32, "gauntlet of rust", 11500),
          new Item(33, "boots of speed", 12000),
          new Item(34, "goggles of night", 12000),
          new Item(35, "clay golem manual", 12000),
          new Item(36, "medallion of thoughts", 12000),
          new Item(37, "pipes of pain", 12000),
          new Item(38, "Boccob's blessed book", 12500),
          new Item(39, "monk's belt", 13000),
          new Item(40, "gem of brightness", 13000),
          new Item(41, "lyre of building", 13000),
          new Item(42, "cloak of arachnida", 14000),
          new Item(43, "destrier stone horse", 14800),
          new Item(44, "belt of dwarvenkind", 14900),
          new Item(45, "periapt of wound closure", 15000),
          new Item(46, "horn of the tritons", 15100),
          new Item(47, "pearl of the sirines", 15300),
          new Item(48, "onyx dog figurine of wondrous power", 15500),
          new Item(49, "amulet of health +4", 16000),
          new Item(50, "belt of giant strength +4", 16000),
          new Item(51, "winged boots", 16000),
          new Item(52, "bracers of armor +4", 16000),
          new Item(53, "cloak of charisma +4", 16000),
          new Item(54, "cloak of resistance +4", 16000),
          new Item(55, "gloves of dexterity +4", 16000),
          new Item(56, "headband of intellect +4", 16000),
          new Item(57, "pearl of power (4th)", 16000),
          new Item(58, "periapt of wisdom +4", 16000),
          new Item(59, "scabbard of keen edges", 16000),
          new Item(60, "golden lion figurine of wondrous power", 16500),
          new Item(61, "chime of interruption", 16800),
          new Item(62, "broom of flying", 17000),
          new Item(63, "marble elephant figurine of wondrous power", 17000),
          new Item(64, "amulet of natural armor +3", 18000),
          new Item(65, "iridescent spindle ioun stone", 18000),
          new Item(66, "bracelet of friends", 19000),
          new Item(67, "carpet of flying (5 ft by 5 ft)", 20000),
          new Item(68, "horn of blasting", 20000),
          new Item(69, "pale lavender ellipsoid ioun stone", 20000),
          new Item(70, "pearly white spindle ioun stone", 20000),
          new Item(71, "portable hole", 20000),
          new Item(72, "stone of good luck (luckstone)", 20000),
          new Item(73, "ivoary goat figurine of wondrous power", 21000),
          new Item(74, "rope of entanglement", 21000),
          new Item(75, "stone golem manual", 22000),
          new Item(76, "mask of the skull", 22000),
          new Item(77, "mattock of the titans", 23348),
          new Item(78, "major circlet of blasting", 23760),
          new Item(79, "amulet of mighty fists +2", 24000),
          new Item(80, "minor cloak of displacement", 24000),
          new Item(81, "helm of underwater action", 24000),
          new Item(82, "greater bracers of archery", 25000),
          new Item(83, "bracers of armor +5", 25000),
          new Item(84, "cloak of resistance +5", 25000),
          new Item(85, "eyes of doom", 25000),
          new Item(86, "pearl of power (5th)", 25000),
          new Item(87, "maul of the titans", 25305),
          new Item(88, "strand of prayer beads", 25800),
          new Item(89, "cloak of the bat", 26000),
          new Item(90, "iron bands of Bilarro", 26000),
          new Item(91, "cube of frost resistance", 27000),
          new Item(92, "helm of telepathy", 27000),
          new Item(93, "periapt of proof against poison", 27000),
          new Item(94, "robe of scintillating colors", 27000),
          new Item(95, "manual of bodily health +1", 27500),
          new Item(96, "manual of gainful excercise +1", 27500),
          new Item(97, "manual of quickness in action +1", 27500),
          new Item(98, "tome of clear thought +1", 27500),
          new Item(99, "tome of leadership and influence +1", 27500),
          new Item(100, "tome of understanding +1", 27500))));

  private static final List<Magic> MAJOR = ImmutableList.of(
      // Armor and shield
      new Magic(10, "", ImmutableList.of(
          new Magic(8, "+3", 3, 0, RANDOM_SHIELDS),
          new Magic(16, "+3", 3, 0, RANDOM_ARMOR),
          new Magic(27, "+4", 4, 0, RANDOM_SHIELDS),
          new Magic(38, "+4", 4, 0, RANDOM_ARMOR),
          new Magic(49, "+5", 5, 0, RANDOM_SHIELDS),
          new Magic(57, "+5", 5, 0, RANDOM_ARMOR),
          // Specific armor
          new Magic(60, "", 0, 0, ImmutableList.of(
              new Item(10, "adamantine breastplate", 10200),
              new Item(20, "dwarven plate", 16500),
              new Item(32, "banded mail of luck", 18900),
              new Item(50, "celestial armor", 22400),
              new Item(60, "plate armor of the deep", 24650),
              new Item(75, "breastplate of command", 25400),
              new Item(90, "mithral full plate of speed", 26500),
              new Item(100, "demon armor", 52260)
          )),
          new Magic(63, "", 0, 0, ImmutableList.of(
              new Item(20, "caster's shield", 3153),
              new Item(40, "spined shield", 5580),
              new Item(60, "lion's shield", 9170),
              new Item(90, "winged shield", 17257),
              new Item(100, "absorbing shield", 50170)
          )),
          new Magic(64, 0, 0, Value.Result.Status.reroll, ImmutableList.of(
              new Magic(3, "glamered", 2700),
              new Magic(4, "light fortification", 1, 0),
              new Magic(7, "improved slick", 15000),
              new Magic(10, "improved shadow", 15000),
              new Magic(13, "improved silent moves", 15000),
              new Magic(16, "acid resistance", 18000),
              new Magic(19, "cold resistance", 18000),
              new Magic(22 ,"electricity resistance", 18000),
              new Magic(25, "fire resistance", 18000),
              new Magic(28, "sonic resistance", 18000),
              new Magic(33, "ghost touch", 3, 0),
              new Magic(35, "invulnerability", 3, 0),
              new Magic(40, "moderate fortification", 3, 0),
              new Magic(42, "spell resistance (15)", 3, 0),
              new Magic(42, "wild", 3, 0),
              new Magic(48, "greater slick", 33750),
              new Magic(53, "greater shadow", 33750),
              new Magic(58, "greater silent moves", 33750),
              new Magic(63, "improved acid resistance", 42000),
              new Magic(68, "improved cold resistance", 42000),
              new Magic(73, "improved electricity resistance", 42000),
              new Magic(78, "improved fire resistance", 42000),
              new Magic(83, "improved sonic resistance", 42000),
              new Magic(88, "spell resistance (17)", 4, 0),
              new Magic(89, "etherealness", 49000),
              new Magic(90, "undead controlling", 49000),
              new Magic(92, "heavy fortification", 5, 0),
              new Magic(94, "spell resistance (19)", 5, 0),
              new Magic(95, "greater acid resistance", 66000),
              new Magic(96, "greater cold resistance", 66000),
              new Magic(97, "greater electricity resistance", 66000),
              new Magic(98, "greater fire resistance", 66000),
              new Magic(99, "greater sonic resistance", 66000),
              new Magic(100, "", Value.Result.Status.rerollTwo)))
      )),
      // Weapon
      new Magic(20, "", ImmutableList.of(
          // Common melee weapons
          new Magic(70, "", ImmutableList.of(
              new Magic(20, "+3", 0, 3, WEAPON_COMMON_MELEE),
              new Magic(38, "+4", 0, 4, WEAPON_COMMON_MELEE),
              new Magic(49, "+5", 0, 5, WEAPON_COMMON_MELEE),
              // Specific weapon
              new Magic(63, "", 0, 0, WEAPON_SPECIFIC_MAJOR),
              // Special ability
              new Magic(100, "", WEAPON_MELEE_SPECIAL_MAJOR))),
          // Uncommon melee weapons
          new Magic(80, "", ImmutableList.of(
              new Magic(70, "+1", 0, 1, WEAPON_UNCOMMON_MELEE),
              new Magic(85, "+2", 0, 2, WEAPON_UNCOMMON_MELEE),
              // Specific weapon
              new Magic(90, "", 0, 0, WEAPON_SPECIFIC_MAJOR),
              // Special ability
              new Magic(100, "", WEAPON_MELEE_SPECIAL_MAJOR))),
          // Common ranged weapons
          new Magic(100, "", ImmutableList.of(
              new Magic(70, "+1", 0, 1, WEAPON_RANGED),
              new Magic(85, "+2", 0, 2, WEAPON_RANGED),
              // Specific weapon
              new Magic(90, "", 0, 0, WEAPON_SPECIFIC_MAJOR),
              // Special ability
              new Magic(100, "", WEAPON_RANGED_SPECIAL_MAJOR))))),
      new Magic(25, "Potion", 0, 0, ImmutableList.of(
        new Item(2, "potion of blur", 300),
        new Item(7, "potion of cure moderate wounds", 300),
        new Item(9, "potion of darkvision", 300),
        new Item(11, "potion of invisibility", 300),
        new Item(13, "potion of remove paralysis", 300),
        new Item(14, "potion of shield of faith +3", 300),
        new Item(15, "potion of undetectable alignment", 300),
        new Item(16, "potion of barkskin +3", 600),
        new Item(18, "potion of shield of faith +4", 600),
        new Item(20, "potion of resist energy 20", 700),
        new Item(28, "potion of cure serious wounds", 750),
        new Item(29, "oil of daylight", 750),
        new Item(32, "potion of displacement", 750),
        new Item(33, "oil of flame arrow", 750),
        new Item(38, "potion of fly", 750),
        new Item(39, "potion of gaseous form", 750),
        new Item(41, "potion of haste", 750),
        new Item(44, "potion of heroism", 750),
        new Item(46, "oil of keen edge", 750),
        new Item(47, "potion of magic circle against alignment", 750),
        new Item(50, "potion of neutralize poison", 750),
        new Item(52, "potion of nondetection", 750),
        new Item(54, "potion of protection from energy", 760),
        new Item(55, "potion of rage", 750),
        new Item(56, "potion of remove blindness/deafness", 750),
        new Item(57, "potion of remove curse", 750),
        new Item(58, "potion of remove disease", 750),
        new Item(59, "potion of tongues", 750),
        new Item(60, "potion of water breathing", 750),
        new Item(61, "potion of water walk", 750),
        new Item(63, "potion of barkskin +4", 900),
        new Item(64, "potion of shield of faith +5", 900),
        new Item(65, "potion good hope", 1050),
        new Item(68, "potion of resist energy 30", 1100),
        new Item(69, "potion of barkskin +5", 1200),
        new Item(73, "potion of greater magic fang +2", 1200),
        new Item(77, "oil of greater magic weapon +3", 1800),
        new Item(81, "oil of magic vestment +2", 1200),
        new Item(82, "potion of protection from arrows 15/magic", 1500),
        new Item(85, "potion of greater magic fang +3", 1800),
        new Item(88, "oil of greater magic weapon +3", 1800),
        new Item(91, "oil of magic vestment +3", 1800),
        new Item(93, "potion of greater magic fang +4", 2400),
        new Item(95, "oil of greater magic weapon +4", 2400),
        new Item(97, "oil of magic vestment +4", 2400),
        new Item(98, "potion of greater magic fang +5", 3000),
        new Item(99, "oil of greater magic weapon +5", 3000),
        new Item(100, "oil of magic vestment +5", 3000))),
      new Magic(35, "ring of", 0, 0, ImmutableList.of(
          new Item(2, "minor energy resistance", 12000),
          new Item(7, "protection +3", 18000),
          new Item(10, "minor spell storing", 18000),
          new Item(15, "invisibility", 20000),
          new Item(19, "wizardry I", 20000),
          new Item(25, "evasion", 25000),
          new Item(28, "x-ray vision", 25000),
          new Item(32, "blinking", 27000),
          new Item(39, "major energy resistance", 28000),
          new Item(49, "protection +4", 32000),
          new Item(55, "wizardry II", 40000),
          new Item(60, "freedom of movement", 40000),
          new Item(63, "greater energy restistance", 44000),
          new Item(65, "friend shield (pair)", 50000),
          new Item(70, "protection +5", 50000),
          new Item(74, "shooting stars", 50000),
          new Item(79, "spell storing", 50000),
          new Item(83, "wizardry III", 70000),
          new Item(86, "telekinesis", 75000),
          new Item(88, "regeneration", 90000),
          new Item(89, "three wishes", 97950),
          new Item(92, "spell turning", 98280),
          new Item(94, "wizardry IV", 100000),
          new Item(95, "djinni calling", 125000),
          new Item(96, "elemental command (air)", 200000),
          new Item(97, "elemental command (earth)", 200000),
          new Item(98, "elemental command (fire)", 200000),
          new Item(99, "elemental command (water)", 200000),
          new Item(100, "major spell storing", 200000))),
      new Magic(45, "Rod", 0, 0, ImmutableList.of(
          new Item(4, "cancellation", 11000),
          new Item(6, "enlarge metamagic", 11000),
          new Item(8, "extend metamagic", 11000),
          new Item(10, "silent megamagic", 11000),
          new Item(14, "wonder", 12000),
          new Item(18, "python", 13000),
          new Item(21, "flame extinguishing", 15000),
          new Item(25, "viper", 19000),
          new Item(30, "enemy detection", 23500),
          new Item(36, "greater enlarge metamagic", 24500),
          new Item(42, "greater extend metamagic", 24500),
          new Item(48, "greater silent metamagic", 24500),
          new Item(53, "splendor", 25000),
          new Item(58, "withering", 25000),
          new Item(64, "empower metamagic", 32500),
          new Item(69, "thunder and lightning", 33000),
          new Item(73, "lesser quicken metamagic", 35000),
          new Item(77, "negation", 37000),
          new Item(80, "absorption", 50000),
          new Item(84, "flailing", 50000),
          new Item(86, "maximize metamagic", 54000),
          new Item(88, "rulership", 60000),
          new Item(90, "security", 61000),
          new Item(92, "lordly might", 70000),
          new Item(94, "greater empower metamagic", 73000),
          new Item(96, "quicken metamagic", 75500),
          new Item(98, "alertness", 85000),
          new Item(99, "greater maximize metamagic", 121500),
          new Item(100, "greater quicken metamagic", 170000))),
      new Magic(55, "", ImmutableList.of(
          new Magic(70, "arcane scroll of", new Dice(1, 6), ImmutableList.of(
              new Magic(5, "", 0, 0, SCROLLS_ARCANE_4),
              new Magic(50, "", 0, 0, SCROLLS_ARCANE_5),
              new Magic(70, "", 0, 0, SCROLLS_ARCANE_6),
              new Magic(85, "", 0, 0, SCROLLS_ARCANE_7),
              new Magic(95, "", 0, 0, SCROLLS_ARCANE_8),
              new Magic(100, "", 0, 0, SCROLLS_ARCANE_9))),
          new Magic(100, "divine scroll of", new Dice(1, 6), ImmutableList.of(
              new Magic(5, "", 0, 0, SCROLLS_DIVINE_4),
              new Magic(50, "", 0, 0, SCROLLS_DIVINE_5),
              new Magic(70, "", 0, 0, SCROLLS_DIVINE_6),
              new Magic(85, "", 0, 0, SCROLLS_DIVINE_7),
              new Magic(95, "", 0, 0, SCROLLS_DIVINE_8),
              new Magic(100, "", 0, 0, SCROLLS_DIVINE_9))))),
      new Magic(75, "staff of", 0, 0, ImmutableList.of(
          new Item(3, "charming", 16500),
          new Item(9, "fire", 17750),
          new Item(11, "swarming insects", 24750),
          new Item(17, "healing", 27750),
          new Item(19, "size alteration", 29000),
          new Item(24, "illumination", 48250),
          new Item(31, "frost", 56250),
          new Item(38, "defense", 58250),
          new Item(43, "abjuration", 65000),
          new Item(48, "conjuration", 65000),
          new Item(53, "enchantment", 65000),
          new Item(58, "evocation", 65000),
          new Item(63, "illusion", 65000),
          new Item(68, "necromancy", 65000),
          new Item(73, "transmutation", 65000),
          new Item(77, "divination", 65000),
          new Item(82, "earth and stone", 80500),
          new Item(87, "woodlands", 101250),
          new Item(92, "life", 155750),
          new Item(97, "passage", 170500),
          new Item(100, "power", 211000))),
      new Magic(80, "Wand", 0, 0, ImmutableList.of(
          new Item(2, "magic missile (7th)", 5250),
          new Item(5, "magic missile (9th)", 6750),
          new Item(7, "call lightning (5th)", 11250),
          new Item(8, "heightened charm person (3rd)", 11250),
          new Item(10, "contagion", 11250),
          new Item(13, "cure serious wounds", 11250),
          new Item(15, "dispel magic", 11250),
          new Item(17, "fireball (5th)", 11250),
          new Item(19, "keen edge", 11250),
          new Item(21, "lightning bolt (5th)", 11250),
          new Item(23, "major image", 11250),
          new Item(25, "slow", 11250),
          new Item(27, "suggestion", 11250),
          new Item(29, "summon monster III", 11250),
          new Item(31, "fireball (6th)", 13500),
          new Item(33, "lightning bolt (6th)", 13500),
          new Item(35, "searing light (6th)", 13500),
          new Item(37, "call lightning (8th)", 18000),
          new Item(39, "fireball (8th)", 18000),
          new Item(41, "lightning bolt (8th)", 18000),
          new Item(45, "charm monster", 21000),
          new Item(50, "cure critical wounds", 21000),
          new Item(52, "dimensional anchor", 21000),
          new Item(55, "fear", 21000),
          new Item(59, "greater invisibility", 21000),
          new Item(60, "heightened hold person (4th)", 21000),
          new Item(65, "ice storm", 21000),
          new Item(68, "inflict critical wounds", 21000),
          new Item(72, "neutralize poison", 21000),
          new Item(74, "poison", 21000),
          new Item(77, "polymorph", 21000),
          new Item(78, "heightened ray of enfeeblement (4th)", 21000),
          new Item(79, "heightened suggestion (4th)", 21000),
          new Item(82, "summon monseeter IV", 21000),
          new Item(86, "wall of fire", 21000),
          new Item(90, "wall of ice", 21000),
          new Item(91, "dispel magic (10th)", 22500),
          new Item(92, "fireball (10th)", 22500),
          new Item(93, "lightning bolt (10th)", 22500),
          new Item(94, "chaos hammer (8th)", 24000),
          new Item(95, "holy smite (8th)", 24000),
          new Item(96, "order's wrath (8th)", 24000),
          new Item(97, "unholy blight (8th)", 24000),
          new Item(99, "restoration", 26000),
          new Item(100, "stoneskin", 33500))),
      new Magic(100, "", 0, 0, ImmutableList.of(
          new Item(1, "dimensional shackles", 28000),
          new Item(2, "obsidian steed figurine of wondrous power", 28500),
          new Item(3, "drums of panic", 30000),
          new Item(4, "orange ioun stone", 30000),
          new Item(5, "pale green prism ioun stone", 30000),
          new Item(6, "lantern of revealing", 30000),
          new Item(7, "robe of blending", 30000),
          new Item(8, "amulet of natural armor +4", 32000),
          new Item(9, "amulet of proof against detection and location", 35000),
          new Item(10, "carpet of flying (5ft by 10 ft)", 35000),
          new Item(11, "iron golem manual", 35000),
          new Item(12, "amulet of health +6", 36000),
          new Item(13, "belt of giant strength +6", 36000),
          new Item(14, "bracers of armor +6", 36000),
          new Item(15, "cloak of charisma +6", 36000),
          new Item(16, "gloves of dexterity +6", 36000),
          new Item(17, "headband of intellect +6", 36000),
          new Item(18, "vibrant purple prism ioun stone", 36000),
          new Item(19, "pearl of power (6th)", 36000),
          new Item(20, "periapt of wisdom +6", 36000),
          new Item(21, "scarab of protection", 38000),
          new Item(22, "lavender and green ellipsoid ioun stone", 40000),
          new Item(23, "raing gates", 40000),
          new Item(24, "crystal ball", 42000),
          new Item(25, "greater stone golem manual", 44000),
          new Item(26, "orb of storms", 48000),
          new Item(27, "boots of teleportation", 49000),
          new Item(28, "bracers of armor +7", 49000),
          new Item(29, "pearl of power (7th)", 49000),
          new Item(30, "amulet of natural armor +5", 50000),
          new Item(31, "major cloak of displacement", 50000),
          new Item(32, "crystal ball with see invisibility", 50000),
          new Item(33, "horn of valhalla", 50000),
          new Item(34, "crystal ball with detect thoughts", 51000),
          new Item(35, "carpet of flying (6ft by 9ft)", 53000),
          new Item(36, "amulet of mighty fists +3", 54000),
          new Item(37, "wings of flying", 54000),
          new Item(38, "cloak of etherealness", 55000),
          new Item(39, "Daern's instant fortress", 55000),
          new Item(40, "manual of bodily health +2", 55000),
          new Item(41, "manual of gainful exercise +2", 55000),
          new Item(42, "manual of quickness in action +2", 55000),
          new Item(43, "tome of clear thought +2", 55000),
          new Item(44, "tome of leadership and influence +2", 55000),
          new Item(45, "tome of understanding +2", 55000),
          new Item(46, "eyes of charming", 56000),
          new Item(47, "robe of stars", 58000),
          new Item(48, "carpet of flying (10ft by 10ft)", 60000),
          new Item(49, "darkskull", 60000),
          new Item(50, "cube of force", 62000),
          new Item(51, "bracers of armor +8", 64000),
          new Item(52, "pearl of power (8th)", 64000),
          new Item(53, "crystal ball with telepathy", 70000),
          new Item(54, "greater horn of blasting", 70000),
          new Item(55, "pearl of power (two spells)", 70000),
          new Item(56, "helm of teleporation", 73500),
          new Item(57, "gem of seeing", 75000),
          new Item(58, "robe of the archmagi", 75000),
          new Item(59, "mantle of faith", 76000),
          new Item(60, "crystal ball with true seeing", 80000),
          new Item(61, "pearl of power (9th)", 81000),
          new Item(62, "well of many worlds", 82000),
          new Item(63, "manual of bodily health +3", 82500),
          new Item(64, "manual of gainful exercise +3", 82500),
          new Item(65, "manual of quickness in action +3", 82500),
          new Item(66, "tome of clear thought +3", 82500),
          new Item(67, "tome of leadership and influence +3", 82500),
          new Item(68, "tome of understanding +3", 82500),
          new Item(69, "aparatus of Kwalish", 90000),
          new Item(70, "mantle of spell resistance", 90000),
          new Item(71, "mirror of opposition", 92000),
          new Item(72, "greater strand of prayer beads", 95000),
          new Item(73, "amulet of mighty fists +4", 96000),
          new Item(74, "eyes of petrification", 98000),
          new Item(75, "bowl of commanding water elementals", 100000),
          new Item(76, "brazier of commanding fire elementals", 100000),
          new Item(77, "censer of controlling air elementals", 100000),
          new Item(78, "stone of controlling earth elementals", 100000),
          new Item(79, "manual of bodily health +4", 110000),
          new Item(80, "manual of gainful exercise +4", 110000),
          new Item(81, "manual of quickness in action +4", 110000),
          new Item(82, "tome of clear thought +4", 110000),
          new Item(83, "tome of leadership and influence +4", 110000),
          new Item(84, "tome of understanding +4", 110000),
          new Item(85, "amulet of the planes", 120000),
          new Item(86, "robe of eyes", 120000),
          new Item(87, "helm of brilliance", 125000),
          new Item(88, "manual of bodily health +5", 137500),
          new Item(89, "manual of gainful exercise +5", 137500),
          new Item(90, "manual of quickness in action +5", 137500),
          new Item(91, "tome of clear thought +5", 137500),
          new Item(92, "tome of leadership and influence +5", 137500),
          new Item(93, "tome of understanding +5", 137500),
          new Item(94, "efreeti bottle", 145000),
          new Item(95, "amulet of might fists +5", 150000),
          new Item(96, "chaos diamond", 160000),
          new Item(97, "cubic gate", 164000),
          new Item(98, "iron flask", 170000),
          new Item(99, "mirror of mental prowess", 175000),
          new Item(100, "mirror of life trapping", 200000))));
}

