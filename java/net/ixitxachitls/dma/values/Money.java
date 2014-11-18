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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Values.MoneyProto;
import net.ixitxachitls.util.Strings;

/**
 * A representation of a monetary value.
 *
 * @file   NewMoney.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Money extends Value.Arithmetic<MoneyProto>
{
  public static class MoneyParser extends Parser<Money>
  {
    public MoneyParser()
    {
      super(1);
    }

    @Override
    public Optional<Money> doParse(String inValue)
    {
      int platinum = 0;
      int gold = 0;
      int silver = 0;
      int copper = 0;
      int armor = 0;
      int weapon = 0;

      List<String []> parts =
        Strings.getAllPatterns(inValue,
                               "(?:\\s*(\\d+)\\s*(pp|gp|sp|cp|armor|weapon))");
      for(String []part : parts)
      {
        if(part.length != 2)
          return Optional.absent();

        try
        {
          int number = Integer.parseInt(part[0]);

          switch(part[1].toLowerCase())
          {
            case PP:
              platinum += number;
              break;

            case GP:
              gold += number;
              break;

            case SP:
              silver += number;
              break;

            case CP:
              copper += number;
              break;

            case "armor":
              armor += number;
              break;

            case "weapon":
              weapon += number;
              break;
          }
        }
        catch(NumberFormatException e)
        {
          return Optional.absent();
        }
      }

      return Optional.of(new Money(platinum, gold, silver, copper,
                                      armor, weapon));
    }
  }

  public Money(int inPlatinum, int inGold, int inSilver, int inCopper,
               int inArmor, int inWeapon)
  {
    m_platinum = inPlatinum;
    m_gold = inGold;
    m_silver = inSilver;
    m_copper = inCopper;
    m_armor = inArmor;
    m_weapon = inWeapon;
  }

  public static final String PP = "pp";
  public static final String GP = "gp";
  public static final String SP = "sp";
  public static final String CP = "cp";

  public static Parser<Money> PARSER = new MoneyParser();

  private final int m_platinum;
  private final int m_gold;
  private final int m_silver;
  private final int m_copper;
  private final int m_armor;
  private final int m_weapon;

  public int getPlatinum()
  {
    return m_platinum;
  }

  public int getGold()
  {
    return m_gold;
  }

  public int getSilver()
  {
    return m_silver;
  }

  public int getCopper()
  {
    return m_copper;
  }

  public int getArmor()
  {
    return m_armor;
  }

  public int getWeapon()
  {
    return m_weapon;
  }

  public double asGold()
  {
    return m_platinum * 10 + m_gold + m_silver / 10.0 + m_copper / 100.0
      + m_armor * m_armor * 1000 + m_weapon + m_weapon * m_weapon * 2000;
  }

  @Override
  public String toString()
  {
    List<String> parts = new ArrayList<>();

    if(m_platinum > 0)
      parts.add(m_platinum + " " + CP);
    if(m_gold > 0)
      parts.add(m_gold + " " + GP);
    if(m_silver > 0)
      parts.add(m_silver + " " + SP);
    if(m_copper > 0)
      parts.add(m_copper + " " + CP);
    if(m_armor > 0)
      parts.add("+" + m_armor + " armor");
    if(m_weapon > 0)
      parts.add("+" + m_weapon + " weapon");

    if(parts.isEmpty())
      return "0 " + GP;

    return Strings.SPACE_JOINER.join(parts);
  }

  public String toPureString()
  {
    List<String> parts = new ArrayList<>();

    if(m_platinum > 0)
      parts.add(m_platinum + " " + CP);
    if(m_gold > 0 || m_armor > 0 || m_weapon > 0)
      parts.add((m_gold + m_weapon * m_weapon * 2000 + m_armor * m_armor * 1000)
                + " " + GP);
    if(m_silver > 0)
      parts.add(m_silver + " " + SP);
    if(m_copper > 0)
      parts.add(m_copper + " " + CP);

    if(parts.isEmpty())
      return "0 " + GP;

    return Strings.SPACE_JOINER.join(parts);
  }

  @Override
  public String group()
  {
    double gold = asGold();
    if(gold < 0.1)
      return "1 " + CP;

    if(gold < 1)
      return "1 " + SP;

    if(gold < 2)
      return "1 " + GP;

    if(gold < 3)
      return "2 " + GP;

    if(gold < 4)
      return "3 " + GP;

    if(gold < 5)
      return "4 " + GP;

    if(gold < 6)
      return "5 " + GP;

    if(gold < 11)
      return "10 " + GP;

    if(gold < 26)
      return "25 " + GP;

    if(gold <= 51)
      return "50 " + GP;

    if(gold < 101)
      return "100 " + GP;

    if(gold < 251)
      return "200 " + GP;

    if(gold < 251)
      return "250 " + GP;

    if(gold < 501)
      return "500 " + GP;

    if(gold < 1001)
      return "1'000 " + GP;

    if(gold < 5001)
      return "5'000 " + GP;

    if(gold < 10001)
      return "100'00 " + GP;

    return "a lot";
  }

  @Override
  public MoneyProto toProto()
  {
    MoneyProto.Builder builder = MoneyProto.newBuilder();
    if(m_platinum > 0)
      builder.setPlatinum(m_platinum);
    if(m_gold > 0)
      builder.setGold(m_gold);
    if(m_silver > 0)
      builder.setSilver(m_silver);
    if(m_copper > 0)
      builder.setCopper(m_copper);
    if(m_armor > 0)
      builder.setMagicArmor(m_armor);
    if(m_weapon > 0)
      builder.setMagicWeapon(m_weapon);

    return builder.build();
  }

  public static Money fromProto(MoneyProto inProto)
  {
    return new Money(inProto.getPlatinum(), inProto.getGold(),
                        inProto.getSilver(), inProto.getCopper(),
                        inProto.getMagicArmor(), inProto.getMagicWeapon());
  }

  @Override
  public Value.Arithmetic<MoneyProto> add(Value.Arithmetic<MoneyProto> inValue)
  {
    if(inValue == null)
      return this;

    if(!(inValue instanceof Money))
      throw new IllegalArgumentException("can only add another money value, "
                                         + "not a " + inValue.getClass());

    Money value = (Money)inValue;
    return new Money(m_platinum + value.m_platinum,
                        m_gold + value.m_gold,
                        m_silver + value.m_silver,
                        m_copper + value.m_copper,
                        m_armor + value.m_armor,
                        m_weapon + value.m_weapon);
  }

  @Override
  public boolean canAdd(Value.Arithmetic<MoneyProto> inValue)
  {
    return inValue instanceof Money;
  }

  @Override
  public Value.Arithmetic<MoneyProto> multiply(int inFactor)
  {
    return new Money(m_platinum * inFactor,
                        m_gold * inFactor,
                        m_silver * inFactor,
                        m_copper * inFactor,
                        m_armor,
                        m_weapon);
  }

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Parsing tests. */
    @org.junit.Test
    public void parse()
    {
      assertEquals("parsing", "[42, gp]",
                   Arrays.toString(Strings.getAllPatterns
                                   ("42 gp 23   sp     17   cp 5sp",
                                    "\\s*(\\d+)\\s*(pp|gp|sp|cp)").get(0)));
      assertEquals("parsting", "[23, sp]",
                   Arrays.toString(Strings.getAllPatterns
                                   ("42 gp 23 sp 17 cp 5sp",
                                    "\\s*(\\d+)\\s*(pp|gp|sp|cp)").get(1)));
      assertEquals("parsing", "[17, cp]",
                   Arrays.toString(Strings.getAllPatterns
                                   ("42 gp 23 sp 17 cp 5sp",
                                    "\\s*(\\d+)\\s*(pp|gp|sp|cp)").get(2)));
      assertEquals("parsing", "[5, sp]",
                   Arrays.toString(Strings.getAllPatterns
                                   ("42 gp 23 sp 17 cp 5sp",
                                    "\\s*(\\d+)\\s*(pp|gp|sp|cp)").get(3)));
    }
  }
}
