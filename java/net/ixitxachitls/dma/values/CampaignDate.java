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

package net.ixitxachitls.dma.values;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import net.ixitxachitls.dma.entries.NestedEntry;
import net.ixitxachitls.dma.proto.Values.CampaignDateProto;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.logging.Log;
import net.ixitxachitls.util.test.TestCase;
import org.junit.Test;

/**
 * A date in a campaign.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class CampaignDate extends NestedEntry
{
  public CampaignDate(Calendar inCalendar)
  {
    m_calendar = inCalendar;
  }

  public CampaignDate(Calendar inCalendar, int inYear, int inMonth, int inDay,
                      int inHour, int inMinute) {
    m_calendar = inCalendar;
    m_year = inYear;
    m_month = inMonth;
    m_day = inDay;
    m_hour = inHour;
    m_minute = inMinute;
  }

  private static final Pattern DATE_PATTERN  = Pattern.compile(
      "^\\s*(\\w+)\\s+(?:(\\d{1,3})\\s+)?(-?\\d{1,6})\\s+"
      + "(\\d{2}):(\\d{2})\\s*$");
  public static Parser<CampaignDate> parser(final Calendar inCalendar)
  {
    return new Parser<CampaignDate>(1)
    {
      @Override
      public Optional<CampaignDate> doParse(String inValue)
      {
        Matcher matcher = DATE_PATTERN.matcher(inValue);
        if(!matcher.find())
          return Optional.absent();

        try
        {
          int year = Integer.parseInt(matcher.group(3));
          int month = inCalendar.parseMonth(matcher.group(1));
          int day = Integer.parseInt(matcher.group(2));
          int hour = Integer.parseInt(matcher.group(4));
          int minute = Integer.parseInt(matcher.group(5));

          CampaignDate date = new CampaignDate(inCalendar, year, month, day,
                                               hour, minute);
          date.normalize();
          return Optional.of(date);
        }
        catch(NumberFormatException e)
        {
          Log.warning("Cannot properly parse integers: " + e);
          return Optional.absent();
        }
      }
    };
  }

  private final Calendar m_calendar;

  private int m_year;
  private int m_month;
  private int m_day;
  private int m_hour;
  private int m_minute;

  public int getYear()
  {
    return m_year;
  }

  public int getMonth()
  {
    return m_month;
  }

  public int getDay()
  {
    return m_day;
  }

  public int getHour()
  {
    return m_hour;
  }

  public int getMinute()
  {
    return m_minute;
  }

  public String getMonthFormatted()
  {
    if(m_month == 0)
      return "";

    if(m_calendar.getMonths().size() >= m_month)
      return m_calendar.getMonths().get(m_month - 1).getName();

    return String.valueOf(m_month);
  }

  private int daysPerMonth(int inMonth)
  {
    if(inMonth == 0 || inMonth >= m_calendar.getMonths().size())
      return 0;

    return m_calendar.getMonths().get(inMonth - 1).getDays();
  }

  public String getDayFormatted()
  {
    if(m_day == 0 || daysPerMonth(m_month) == 1)
      return "";

    return String.valueOf(m_day);
  }

  public String getTimeFormatted()
  {
    return Strings.pad(m_hour, 2, true) + ":" + Strings.pad(m_minute, 2, true);
  }

  public String getHoursFormatted()
  {
    return Strings.pad(m_hour, 2, true);
  }

  public String getMinutesFormatted()
  {
    return Strings.pad(m_minute, 2, true);
  }

  @Override
  public String toString()
  {
    return (m_month > 0 ? getMonthFormatted() + " " : "")
        + (m_day > 0 && daysPerMonth(m_month) != 1
            ? getDayFormatted() + " " : "")
        + m_year + " " + getTimeFormatted();
  }

  public CampaignDateProto toProto()
  {
    CampaignDateProto.Builder proto = CampaignDateProto.newBuilder();

    proto.setYear(m_year);
    proto.setMonth(m_month);
    proto.setDay(m_day);
    proto.setHour(m_hour);
    proto.setMinute(m_minute);

    return proto.build();
  }

  public static CampaignDate fromProto(Calendar inCalendar,
                                       CampaignDateProto inProto) {
    return new CampaignDate(inCalendar, inProto.getYear(), inProto.getMonth(),
                            inProto.getDay(), inProto.getHour(),
                            inProto.getMinute());
  }

  @Override
  public void set(Values inValues)
  {
    m_year = inValues.use("campaign_date.year", m_year, Value.INTEGER_PARSER);
    m_month =
        inValues.use("campaign_date.month", m_month, Value.INTEGER_PARSER);
    m_day = inValues.use("campaign_date.day", m_day, Value.INTEGER_PARSER);
    m_hour = inValues.use("campaign_date.hour", m_hour, Value.INTEGER_PARSER);
    m_minute =
        inValues.use("campaign_date.minute", m_minute, Value.INTEGER_PARSER);
  }

  public void manipulate(int inMinutes, int inHours, int inDays, int inMonths,
                         int inYears)
  {
    m_minute += inMinutes;
    m_hour += inHours;
    m_day += inDays;
    m_month += inMonths;
    m_year += inYears;

    normalize();
  }

  private void normalize()
  {
    normalizeMinutes();
    normalizeHours();
    normalizeDays();
  }

  private void normalizeMinutes()
  {
    if(m_minute >= m_calendar.getMinutesPerHour())
    {
      m_hour += m_minute / m_calendar.getMinutesPerHour();
      m_minute = m_minute % m_calendar.getMinutesPerHour();
    }
    else if(m_minute < 0)
    {
      m_hour += m_minute / m_calendar.getMinutesPerHour() - 1;
      m_minute = m_calendar.getMinutesPerHour()
          + m_minute % m_calendar.getMinutesPerHour();
    }
  }

  private void normalizeHours()
  {
    if(m_hour >= m_calendar.getHoursPerDay())
    {
      m_day += m_hour / m_calendar.getHoursPerDay();
      m_hour = m_hour % m_calendar.getHoursPerDay();
    }
    else if(m_hour < 0)
    {
      m_day += m_hour / m_calendar.getHoursPerDay() - 1;
      m_hour = m_calendar.getHoursPerDay()
          + m_hour % m_calendar.getHoursPerDay();
    }
  }

  private void normalizeDays()
  {
    normalizeMonths();
    while(m_day > m_calendar.getMonth(m_month).getDays()) {
      m_day -= m_calendar.getMonth(m_month).getDays();
      m_month++;
      normalizeMonths();
    }

    while(m_day <= 0)
    {
      m_month--;
      normalizeMonths();
      m_day += m_calendar.getMonth(m_month).getDays();
    }
  }

  private void normalizeMonths()
  {
    if(m_month > m_calendar.getMonths().size())
    {
      m_year += m_month / m_calendar.getMonths().size();
      m_month = m_month % m_calendar.getMonths().size() + 1;

      normalizeMonths();
    }
    else if(m_month <= 0)
    {
      m_year += m_month / m_calendar.getMonths().size() - 1;
      m_month = m_calendar.getMonths().size()
          + m_month % m_calendar.getMonths().size();

      normalizeMonths();
    } else {
      // Avoid leap year months.
      if(m_calendar.getMonth(m_month).getLeapYears() != 0
         && m_year % m_calendar.getMonth(m_month).getLeapYears() != 0)
      {
        m_month++;

        normalizeMonths();
      }
    }
  }

  public static class Test extends TestCase
  {
    @org.junit.Test
    public void parse()
    {
      Calendar calendar =
          new Calendar(Collections.<Calendar.Year>emptyList(),
                       ImmutableList.of(new Calendar.Month("First", 5),
                                        new Calendar.Month("Second", 1),
                                        new Calendar.Month("Leap", 2, 4),
                                        new Calendar.Month("Third", 10),
                                        new Calendar.Month("Leap 2nd", 1, 10)),
                       10, 24, 60, 60);

      Parser<CampaignDate> parser = CampaignDate.parser(calendar);
      Optional<CampaignDate> date = parser.parse("  ");
      assertFalse("empty", date.isPresent());

      date = parser.parse("Third 5 999 23:42");
      assertEquals("simple", "Third 5 999 23:42", date.get().toString());

      date = parser.parse("Third   5  999    23:42  ");
      assertEquals("spaces", "Third 5 999 23:42", date.get().toString());

      date = parser.parse("Third 5  -999 23:42");
      assertEquals("negative", "Third 5 -999 23:42", date.get().toString());

      date = parser.parse("Second 2  -999 23:42");
      assertEquals("invalid day", "Third 1 -999 23:42", date.get().toString());
      date = parser.parse("First 7  -999 23:42");
      assertEquals("invalid day", "Third 1 -999 23:42", date.get().toString());
    }

    @org.junit.Test
    public void normalize()
    {
      Calendar calendar =
          new Calendar(Collections.<Calendar.Year>emptyList(),
                       ImmutableList.of(new Calendar.Month("First", 5),
                                        new Calendar.Month("Second", 1),
                                        new Calendar.Month("Leap", 2, 4),
                                        new Calendar.Month("Third", 10),
                                        new Calendar.Month("Leap 2nd", 1, 10)),
                       10, 15, 30, 20);

      CampaignDate date  = new CampaignDate(calendar, 100, 3, 5, 16, 35);
      assertEquals("none", "Leap 5 100 16:35", date.toString());

      date.normalize();
      assertEquals("none", "Third 4 100 02:05", date.toString());

      date.manipulate(5, 0, 0, 0, 0);
      assertEquals("none", "Third 4 100 02:10", date.toString());

      date.manipulate(20, 0, 0, 0, 0);
      assertEquals("none", "Third 4 100 03:00", date.toString());

      date.manipulate(70, 12, 0, 0, 0);
      assertEquals("none", "Third 5 100 02:10", date.toString());

      date.manipulate(0, 0, 6, 0, 0);
      assertEquals("none", "Leap 2nd 1 100 02:10", date.toString());

      date.manipulate(0, 0, 10, 0, 0);
      assertEquals("none", "Third 9 101 02:10", date.toString());

      date.manipulate(0, 0, 0, 5, 0);
      assertEquals("none", "Third 8 103 02:10", date.toString());

      date.manipulate(0, 0, 0, 3, 0);
      assertEquals("none", "Third 6 104 02:10", date.toString());

      date.manipulate(-15, 0, 0, 0, 0);
      assertEquals("none", "Third 6 104 01:25", date.toString());

      date.manipulate(-30, -1, 0, 0, 0);
      assertEquals("none", "Third 5 104 14:25", date.toString());

      date.manipulate(0, 0, -1, 0, 0);
      assertEquals("none", "Third 4 104 14:25", date.toString());

      date.manipulate(0, 0, -4, 0, 0);
      assertEquals("none", "Leap 2 104 14:25", date.toString());

      date.manipulate(0, 0, -2, -2, 0);
      assertEquals("none", "Second 104 14:25", date.toString());
    }
  }
}
