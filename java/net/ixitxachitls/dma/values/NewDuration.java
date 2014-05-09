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
import java.util.List;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Values.DurationProto;
import net.ixitxachitls.util.Strings;

/**
 * A duration.
 *
 * @file   NewCritical.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */
public class NewDuration extends NewValue.Addable<DurationProto>
  implements Comparable<NewDuration>
{
  public static final Parser<NewDuration> PARSER = new Parser<NewDuration>(1)
  {
    @Override
    protected Optional<NewDuration> doParse(String inValue)
    {
      if(inValue.trim().isEmpty())
        return Optional.absent();

      List<String []>parts =
        Strings.getAllPatterns(inValue,
                               "^(?:\\s*(.*?)"
                                 + "\\s*(day|days|d|ds|hour|hours|h|hr|hrs|"
                                 + "minute|minutes|m|min|mins|"
                                 + "second|seconds|s|sec|secs|"
                                 + "round|rounds|r|rd|rds|"
                                 + "standard action|standard actions|"
                                 + "move action|move actions|"
                                 + "swift action|swift actions|"
                                 + "free action|free actions))\\s*$");
      if(parts.isEmpty())
        return Optional.absent();

      Optional<NewRational> days = Optional.absent();
      Optional<NewRational> hours = Optional.absent();
      Optional<NewRational> minutes = Optional.absent();
      Optional<NewRational> seconds = Optional.absent();
      Optional<NewRational> rounds = Optional.absent();
      Optional<NewRational> standardActions = Optional.absent();
      Optional<NewRational> moveActions = Optional.absent();
      Optional<NewRational> swiftActions= Optional.absent();
      Optional<NewRational> freeActions= Optional.absent();

      for(String []part : parts)
      {
        if(part.length != 2)
          return Optional.absent();

        Optional<NewRational> number = NewRational.PARSER.parse(part[0]);
        if(!number.isPresent())
          return Optional.absent();

        switch(part[1].toLowerCase())
        {
          case "day":
          case "days":
          case "d":
          case "ds":
            days = add(days, number);
            break;

          case "hour":
          case "hours":
          case "h":
          case "hr":
          case "hrs":
            hours = add(hours, number);
            break;

          case "minutes":
          case "minute":
          case "m":
          case "min":
          case "mins":
            minutes = add(minutes, number);
            break;

          case "second":
          case "seconds":
          case "s":
          case "sec":
          case "secs":
            seconds = add(seconds, number);
            break;

          case "round":
          case "rounds":
          case "r":
          case "rd":
          case "rds":
            rounds = add(rounds, number);
            break;

          case "standard action":
          case "standard actions":
            standardActions = add(standardActions, number);
            break;

          case "move action":
          case "move actions":
            moveActions = add(moveActions, number);
            break;

          case "swift action":
          case "swift actions":
            swiftActions = add(swiftActions, number);
            break;

          case "free action":
          case "free actions":
            freeActions = add(freeActions, number);
            break;
        }
      }

      return Optional.of(new NewDuration(days, hours, minutes, seconds, rounds,
                                         standardActions, moveActions,
                                         swiftActions, freeActions));
    }
  };

  public NewDuration(Optional<NewRational> inDays,
                     Optional<NewRational> inHours,
                     Optional<NewRational> inMinutes,
                     Optional<NewRational> inSeconds,
                     Optional<NewRational> inRounds,
                     Optional<NewRational> inStandardActions,
                     Optional<NewRational> inMoveActions,
                     Optional<NewRational> inSwiftActions,
                     Optional<NewRational> inFreeActions)
  {
    m_days = inDays;
    m_hours = inHours;
    m_minutes = inMinutes;
    m_seconds = inSeconds;
    m_rounds = inRounds;
    m_standardActions = inStandardActions;
    m_moveActions = inMoveActions;
    m_swiftActions = inSwiftActions;
    m_freeActions = inFreeActions;
  }

  private final Optional<NewRational> m_days;
  private final Optional<NewRational> m_hours;
  private final Optional<NewRational> m_minutes;
  private final Optional<NewRational> m_seconds;
  private final Optional<NewRational> m_rounds;
  private final Optional<NewRational> m_standardActions;
  private final Optional<NewRational> m_moveActions;
  private final Optional<NewRational> m_swiftActions;
  private final Optional<NewRational> m_freeActions;

  @Override
  public String toString()
  {
    List<String> parts = new ArrayList<>();

    if(m_days.isPresent())
      parts.add(m_days.get() + " days");

    if(m_hours.isPresent())
      parts.add(m_hours.get() + " hours");

    if(m_minutes.isPresent())
      parts.add(m_minutes.get() + " minutes");

    if(m_seconds.isPresent())
      parts.add(m_seconds.get() + " seconds");

    if(m_rounds.isPresent())
      parts.add(m_rounds.get() + " rounds");

    if(m_standardActions.isPresent())
      parts.add(m_standardActions.get() + " standard actions");

    if(m_moveActions.isPresent())
      parts.add(m_moveActions.get() + " move actions");

    if(m_swiftActions.isPresent())
      parts.add(m_swiftActions.get() + " swift actions");

    if(m_freeActions.isPresent())
      parts.add(m_freeActions.get() + " free actions");

    if(parts.isEmpty())
      return "0 s";

    return Strings.SPACE_JOINER.join(parts);
  }

  public int asSeconds() {
    int seconds = 0;

    if(m_days.isPresent())
      seconds += m_days.get().asDouble() * 24 * 60 * 60;

    if(m_hours.isPresent())
      seconds += m_hours.get().asDouble() * 60 * 60;

    if(m_minutes.isPresent())
      seconds += m_minutes.get().asDouble() * 60;

    if(m_seconds.isPresent())
      seconds += m_seconds.get().asDouble();

    if(m_rounds.isPresent())
      seconds += m_rounds.get().asDouble() * 6;

    if(m_standardActions.isPresent())
      seconds += m_standardActions.get().asDouble() * 4;

    if(m_moveActions.isPresent())
      seconds += m_moveActions.get().asDouble() * 2;

    if(m_swiftActions.isPresent())
      seconds += m_swiftActions.get().asDouble();

    return seconds;
  }

  @Override
  public DurationProto toProto()
  {
    DurationProto.Builder builder = DurationProto.newBuilder();
    if(m_days.isPresent() || m_hours.isPresent() || m_minutes.isPresent()
      || m_seconds.isPresent())
    {
      DurationProto.Metric.Builder metric = DurationProto.Metric.newBuilder();

      if(m_days.isPresent())
        metric.setDays(m_days.get().toProto());
      if(m_hours.isPresent())
        metric.setHours(m_hours.get().toProto());
      if(m_minutes.isPresent())
        metric.setMinutes(m_minutes.get().toProto());
      if(m_seconds.isPresent())
        metric.setSeconds(m_seconds.get().toProto());

      builder.setMetric(metric.build());
    }

    if(m_rounds.isPresent())
      builder.setRounds(m_rounds.get().toProto());

    if(m_standardActions.isPresent() || m_moveActions.isPresent()
      || m_swiftActions.isPresent() || m_freeActions.isPresent())
    {
      DurationProto.Actions.Builder actions =
        DurationProto.Actions.newBuilder();

      if(m_standardActions.isPresent())
        actions.setStandardActions(m_standardActions.get().toProto());
      if(m_moveActions.isPresent())
        actions.setMoveActions(m_moveActions.get().toProto());
      if(m_swiftActions.isPresent())
        actions.setSwiftActions(m_swiftActions.get().toProto());
      if(m_freeActions.isPresent())
        actions.setFreeActions(m_freeActions.get().toProto());
    }

    return builder.build();
  }


  @Override
  public NewValue.Addable<DurationProto>
    add(NewValue.Addable<DurationProto> inValue)
  {
    if(!(inValue instanceof NewDuration))
      return this;

    NewDuration value = (NewDuration)inValue;

    return new NewDuration(add(m_days, value.m_days),
                           add(m_hours, value.m_hours),
                           add(m_minutes, value.m_minutes),
                           add(m_seconds, value.m_seconds),
                           add(m_rounds, value.m_rounds),
                           add(m_standardActions, value.m_standardActions),
                           add(m_moveActions, value.m_moveActions),
                           add(m_swiftActions, value.m_swiftActions),
                           add(m_freeActions, value.m_freeActions));
  }

  /**
   * Create a new duration value with the values from the given proto.
   *
   * @param inProto the proto to read the values from
   * @return the newly created duration
   */
  public static NewDuration fromProto(DurationProto inProto)
  {
    Optional<NewRational> days = Optional.absent();
    Optional<NewRational> hours = Optional.absent();
    Optional<NewRational> minutes = Optional.absent();
    Optional<NewRational> seconds = Optional.absent();
    Optional<NewRational> rounds = Optional.absent();
    Optional<NewRational> standardActions = Optional.absent();
    Optional<NewRational> moveActions = Optional.absent();
    Optional<NewRational> swiftActions = Optional.absent();
    Optional<NewRational> freeActions = Optional.absent();

    if(inProto.hasMetric())
    {
      if(inProto.getMetric().hasDays())
        days = Optional.of(NewRational.fromProto(inProto.getMetric().getDays()));
      if(inProto.getMetric().hasHours())
        hours =
          Optional.of(NewRational.fromProto(inProto.getMetric().getHours()));
      if(inProto.getMetric().hasMinutes())
        minutes =
          Optional.of(NewRational.fromProto(inProto.getMetric().getMinutes()));
      if(inProto.getMetric().hasSeconds())
        seconds =
          Optional.of(NewRational.fromProto(inProto.getMetric().getSeconds()));
    }

    if(inProto.hasRounds())
      rounds = Optional.of(NewRational.fromProto(inProto.getRounds()));

    if(inProto.hasActions())
    {
      if(inProto.getActions().hasStandardActions())
        standardActions =
          Optional.of(NewRational.fromProto
                      (inProto.getActions().getStandardActions()));
      if(inProto.getActions().hasMoveActions())
        moveActions =
          Optional.of(NewRational.fromProto
                      (inProto.getActions().getMoveActions()));
      if(inProto.getActions().hasSwiftActions())
        swiftActions =
          Optional.of(NewRational.fromProto
                      (inProto.getActions().getSwiftActions()));
      if(inProto.getActions().hasFreeActions())
        freeActions =
          Optional.of(NewRational.fromProto
                      (inProto.getActions().getFreeActions()));
    }

    return new NewDuration(days, hours, minutes, seconds,
                           rounds,
                           standardActions, moveActions, swiftActions,
                           freeActions);
  }

  @Override
  public int compareTo(NewDuration inOther)
  {
    if(this == inOther)
      return 0;

    return Integer.compare(asSeconds(), inOther.asSeconds());
  }

  @Override
  public boolean canAdd(NewValue.Addable<DurationProto> inValue)
  {
    return inValue instanceof NewDuration;
  }

  //----------------------------------------------------------------------------

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** Testing init. */
    @org.junit.Test
    public void parse()
    {
      assertEquals("parse", "None", PARSER.parse(" none  ").get().toString());
      assertEquals("parse", "x3", PARSER.parse(" x 3  ").get().toString());
      assertEquals("parse", "x3", PARSER.parse(" 20/x3  ").get().toString());
      assertEquals("parse", "19-20/x2",
                   PARSER.parse(" 19 - 20 / x 2 ").get().toString());
      assertEquals("parse", "12-19/x5",
                   PARSER.parse("12-19/x5").get().toString());
      assertFalse("parse", PARSER.parse("/").isPresent());
      assertFalse("parse", PARSER.parse("").isPresent());
      assertFalse("parse", PARSER.parse("12").isPresent());
      assertFalse("parse", PARSER.parse("x").isPresent());
      assertFalse("parse", PARSER.parse("19-20 x3").isPresent());
      assertFalse("parse", PARSER.parse("19 - x 4").isPresent());
    }
  }

}
