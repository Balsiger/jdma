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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.base.Optional;

import net.ixitxachitls.dma.entries.NestedEntry;
import net.ixitxachitls.dma.proto.Values.CalendarProto;
import net.ixitxachitls.dma.server.servlets.MoveActionServlet;
import net.ixitxachitls.util.logging.Log;
import net.ixitxachitls.util.logging.Logger;
import org.hsqldb.lib.Collection;

/**
 * Specification of a calendar for a specific world.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Calendar extends NestedEntry
{
  public static class Year
  {
    public Year(int inStart, int inEnd, String inName)
    {
      m_start = inStart;
      m_end = inEnd;
      m_name = inName;
    }

    public Year(int year, String name)
    {
      this(year, year, name);
    }

    private static final Parser<Year> PARSER = new Parser<Year>(3) {
      @Override
      public Optional<Year> doParse(String inStart, String inEnd, String inName)
      {
        try
        {
          int start = 0;
          if (!Strings.isNullOrEmpty(inStart))
            start = Integer.parseInt(inStart);
          int end = 0;
          if (!Strings.isNullOrEmpty(inEnd))
            end = Integer.parseInt(inEnd);

          if (start == 0)
            start = end;
          if (end == 0)
            end = start;

          return Optional.of(new Year(start, end, inName));
        }
        catch(NumberFormatException e)
        {
          Log.warning("Cannot properly parse year for calendar: " + e);
          return Optional.absent();
        }
      }
    };

    private final int m_start;
    private final int m_end;
    private final String m_name;

    public int getStart()
    {
      return m_start;
    }

    public int getEnd()
    {
      return m_end;
    }

    public String getName()
    {
      return m_name;
    }

    @Override
    public String toString()
    {
      if(m_start == m_end)
        return m_start + " " + m_name;

      return m_start + "-" + m_end + " " + m_name;
    }
  }

  public static class Month
  {
    public Month(String inName, int inDays)
    {
      this(inName, inDays, 0);
    }

    public Month(String inName, int inDays, int inLeapYears)
    {
      m_name = inName;
      m_days = inDays;
      m_leapYears = inLeapYears;
    }

    private static final Parser<Month> PARSER = new Parser<Month>(3) {
      @Override
      public Optional<Month> doParse(String inName, String inDays,
                                     String inLeapYears)
      {
        try
        {
          int days = Integer.parseInt(inDays);

          if(inLeapYears.isEmpty())
            return Optional.of(new Month(inName, days));
          else
          return Optional.of(new Month(inName, days,
                                       Integer.parseInt(inLeapYears)));
        }
        catch(NumberFormatException e)
        {
          Log.warning("Cannot properly parse month for calendar: " + e);
          return Optional.absent();
        }
      }
    };

    private final String m_name;
    private final int m_days;
    private final int m_leapYears;

    public String getName()
    {
      return m_name;
    }

    public int getDays()
    {
      return m_days;
    }

    public int getLeapYears()
    {
      return m_leapYears;
    }

    @Override
    public String toString()
    {
      return m_name
          + " ("
          + m_days + " "
          + (m_days == 1 ? "day" : "days")
          + (m_leapYears == 0 ? "" : " every " + m_leapYears + " years")
          + ")";
    }
  }

  public Calendar()
  {
    this(Collections.<Year>emptyList(), Collections.<Month>emptyList(),
         7, 24, 60, 60);
  }

  public Calendar(List<Year> inYears, List<Month> inMonths, int inDaysPerWeek)
  {
    this(inYears, inMonths, inDaysPerWeek, 24, 60, 60);
  }

  public Calendar(List<Year> inYears, List<Month> inMonths)
  {
    this(inYears, inMonths, 7, 24, 60, 60);
  }

  public Calendar(List<Year> inYears, List<Month> inMonths, int inDaysPerWeek,
                  int inHoursPerDay, int inMinutesPerHour,
                  int inSecondsPerMinute)
  {
    m_years = inYears;
    m_months = inMonths;
    m_daysPerWeek = inDaysPerWeek;
    m_hoursPerDay = inHoursPerDay;
    m_minutesPerHour = inMinutesPerHour;
    m_secondsPerMinute = inSecondsPerMinute;
  }

  /** The names of the years, in ascending order. */
  private List<Year> m_years;

  /** The names and lengths of months, in ascending order. */
  private List<Month> m_months;

  /** The number of days per week. */
  private int m_daysPerWeek;

  /** The number of hours per day. */
  private int m_hoursPerDay;

  /** The number of minutes per hour. */
  private int m_minutesPerHour;

  /** Second per minutes (a round will always be 6 seconds). */
  private int m_secondsPerMinute;

  public List<Year> getYears()
  {
    return Collections.unmodifiableList(m_years);
  }

  public List<Month> getMonths()
  {
    return Collections.unmodifiableList(m_months);
  }

  public Month getMonth(int inMonth)
  {
    if (inMonth <= 0 || inMonth > m_months.size())
      throw new IllegalArgumentException("invalid month given: " + inMonth
                                         + ", must be 1 based.");

    return m_months.get(inMonth - 1);
  }

  public int parseMonth(String inName)
  {
    int i = 1;
    for(Month month : m_months)
      if(month.getName().equalsIgnoreCase(inName))
        return i;
      else
        i++;

    return 0;
  }

  public int getDaysPerWeek() {
    return m_daysPerWeek;
  }

  public int getHoursPerDay() {
    return m_hoursPerDay;
  }

  public int getMinutesPerHour() {
    return m_minutesPerHour;
  }

  public int getSecondsPerMinute() {
    return m_secondsPerMinute;
  }

  @Override
  public String toString()
  {
    return m_years + "; "
        + m_months + "; "
        + m_daysPerWeek + " days per week; "
        + m_hoursPerDay + " hours per day; "
        + m_minutesPerHour + " minutes per hour; "
        + m_secondsPerMinute + " seconds per minute";
  }

  public CalendarProto toProto()
  {
    CalendarProto.Builder proto = CalendarProto.newBuilder();

    Collections.sort(m_years, new Comparator<Year>()
    {
      @Override
      public int compare(Year inFirst, Year inSecond)
      {
        int compare = Integer.compare(inFirst.m_start, inSecond.m_start);
        if(compare != 0)
          return compare;

        compare = Integer.compare(inFirst.m_end, inSecond.m_end);
        if(compare != 0)
          return compare;

        return inFirst.m_name.compareTo(inSecond.m_name);
      }
    });

    for(Year year : m_years)
      proto.addYear(CalendarProto.Year.newBuilder()
                        .setStart(year.m_start)
                        .setEnd(year.m_end)
                        .setName(year.m_name)
                        .build());
    for(Month month : m_months)
      proto.addMonth(CalendarProto.Month.newBuilder()
                         .setName(month.m_name)
                         .setDays(month.m_days)
                         .setLeapYears(month.m_leapYears)
                         .build());

    proto.setDaysPerWeek(m_daysPerWeek);
    proto.setHoursPerDay(m_hoursPerDay);
    proto.setMinutesPerHour(m_minutesPerHour);
    proto.setSecondsPerMinute(m_secondsPerMinute);

    return proto.build();
  }

  public static Calendar fromProto(CalendarProto inProto)
  {
    List<Year> years = new ArrayList<>();
    for(CalendarProto.Year year : inProto.getYearList())
      years.add(new Year(year.getStart(), year.getEnd(), year.getName()));

    List<Month> months = new ArrayList<>();
    for(CalendarProto.Month month : inProto.getMonthList())
      months.add(new Month(month.getName(), month.getDays(),
                           month.getLeapYears()));

    return new Calendar(years, months, inProto.getDaysPerWeek(),
                        inProto.getHoursPerDay(), inProto.getMinutesPerHour(),
                        inProto.getSecondsPerMinute());
  }

  @Override
  public void set(Values inValues)
  {
    m_years = inValues.use("calendar.year", m_years, Year.PARSER,
                           "start", "end", "name");
    m_months = inValues.use("calendar.month", m_months, Month.PARSER,
                            "name", "days", "leap_years");
    m_daysPerWeek = inValues.use("calendar.days_per_week", m_daysPerWeek,
                                 Value.INTEGER_PARSER);
    m_hoursPerDay = inValues.use("calendar.hours_per_day", m_hoursPerDay,
                                 Value.INTEGER_PARSER);
    m_minutesPerHour = inValues.use("calendar.minutes_per_hour",
                                    m_minutesPerHour, Value.INTEGER_PARSER);
    m_secondsPerMinute = inValues.use("calendar.seconds_per_minute",
                                      m_secondsPerMinute, Value.INTEGER_PARSER);
  }
}
