/******************************************************************************
 * Copyright (c) 2002-2011 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.util.logging;

import java.util.logging.Level;

/**
 * A logger to log to the standard java logs.
 *
 * @file JavaLogger.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class JavaLogger implements Logger
{
  /** Create the logger. */
  public JavaLogger()
  {
    // nothing to do here
  }

  /** The java logger to output to. */
  private static final java.util.logging.Logger s_logger =
      java.util.logging.Logger.getAnonymousLogger();

  @Override
  public void print(String inText, Log.Type inType)
  {
    s_logger.log(toLevel(inType), inText);
  }

  @Override
  public void print(Object inObject, Log.Type inType)
  {
    print(inObject.toString(), inType);
  }

  @Override
  public void close()
  {
    // nothing to do here
  }

  /**
   * Convert the given log type to a java log level.
   *
   * @param inType the log type to convert
   * @return the corresponding java log level
   */
  private Level toLevel(Log.Type inType)
  {
    switch(inType)
    {
      case FATAL:
      case ERROR:
        return Level.SEVERE;

      case EVENT:
      case NECESSARY:
      case IMPORTANT:
      case USEFUL:
      case INFO:
      case STATUS:
      default:
        return Level.INFO;

      case WARNING:
        return Level.WARNING;

      case COMPLETE:
        return Level.FINE;

      case DEBUG:
        return Level.FINER;

      case TRACE:
        return Level.FINEST;
    }
  }
}

