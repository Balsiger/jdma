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

//------------------------------------------------------------------ imports

package net.ixitxachitls.util.logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the logging class to log all messages.
 *
 * @file          Log.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

public final class Log
{
  //----------------------------------------------------------------- nested

  /** A simple class for representing a message to be logged. */
  public static class Message
  {
    /**
     * Create the logged message.
     *
     * @param inText the log message
     * @param inType the log type
     *
     */
    public Message(String inText, Log.Type inType)
    {
      m_type = inType;
      m_text = inText;
    }

    /** The text of the message. */
    private String m_text;

    /** The log type. */
    private Log.Type m_type;

    /** The date this message was logged. */
    private Date m_date = new Date();

    /**
     * Get the log message.
     *
     * @return the log message
     *
     */
    public String getText()
    {
      return m_text;
    }

    /**
     * Get the log type.
     *
     * @return the log type
     *
     */
    public Log.Type getType()
    {
      return m_type;
    }

    /**
     * Get the date of the log message in seconds from now.
     *
     * @return the date of the log message as seconds from now
     *
     */
    public long getDate()
    {
      return (new Date().getTime() - m_date.getTime()) / 1000;
    }

    /**
     * Convert to stringh.
     *
     * @return string representation of the message.
     */
    public String toString()
    {
      return getType() + ": " + getText() + " (" + getDate() + ")";
    }
  }

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Log ----------------------------------

  /**
    *
    * Prevent instantiation.
    *
    * @undefined   never
    *
    */
  private Log()
  {
    // nothing done here
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** All the currently registered loggers. */
  private static Map<String, Logger> s_loggers =
    new HashMap<String, Logger>();

  /** The possible loggging levels. */
  public enum Type
  {
    /** A fatal error, will usually terminate the program. */
    FATAL,

    /** An important event in the program. */
    EVENT,

    /** An important failure, but program execution can normally go on, but
        usually results are not the expected or correct ones. */
    ERROR,

    /** Something went wrong, but it's not critical; this often means that some
     * action or data was ignored. */
    WARNING,

    /** This is a really necessary logging message, which does not show any
     * malfunction but instead an necessary message that needs to be noted. */
    NECESSARY,

    /** An important logging message. */
    IMPORTANT,

    /** This is a useful message, but not really that important. */
    USEFUL,

    /** A general information message, there may be quite a bunch of those. */
    INFO,

    /** A status message. */
    STATUS,

    /** Thats the complete set of logging messages to be shown. */
    COMPLETE,

    /** This level prints all message, even those that are
        mainly used for debugging purposes. */
    DEBUG;
  }

  /** The default and current message level. */
  private static Type s_level = Type.DEBUG;

  /** The maximal number of message to store. */
  private static int s_maxMessages = 1000;

  /** The last log entries processed. */
  private static final LinkedList<Message> s_last =
    new LinkedList<Message>();

  /** The analytics tracker. */
  //private static Analytics s_analytics = new Analytics();

  static
  {
    // setup default logging
    String level = Config.get("logging.level", "DEBUG");
    String []loggers = Config.get("logging.loggers", "").split(",\\s*");

    Log.setLevel(Type.valueOf(level));
    for(String logger : loggers)
      Log.add("default (" + logger + ")", logger);

    Log.important("setup initial debug configuration to level " + level
                  + " and logger " + Arrays.toString(loggers));
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------------- get ----------------------------------

  /**
    * Get a currently installed logger.
    *
    * @param       inName the name of the logger to get
    *
    * @return      the logger (i.e. a reference to it!)
    *
    */
  public static @Nullable Logger get(@Nullable String inName)
  {
    if(inName == null)
      return null;

    return s_loggers.get(inName);
  }

  //........................................................................
  //------------------------------- getLast --------------------------------

  /**
   * Get the last log messages that were registered.
   *
   * @return      an iterator over all log messages
   *
   */
  public static @Nonnull Iterator<Message> getLast()
  {
    return s_last.iterator();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- add ----------------------------------

  /**
    * Add a logger to be logged to.
    *
    * @param       inName   the name of the logger to add
    * @param       inLogger the logger to add
    *
    * @return      true if newly added, false if another logger was replaced
    *
    */
  public static boolean add(@Nullable String inName, @Nullable Logger inLogger)
  {
    if(inName == null || inLogger == null)
      return false;

    return s_loggers.put(inName, inLogger) == null;
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
    * Add a logger to be logged to.
    *
    * @param       inName   the name of the logger to add
    * @param       inLogger the logger to add
    *
    * @return      true if newly added, false if another logger was replaced
    *
    */
  public static boolean add(@Nonnull String inName, @Nonnull String inLogger)
  {
    if(inLogger.isEmpty())
      return false;

    String name = inLogger;
    if(name.indexOf(".") < 0)
      name = "net.ixitxachitls.util.logging." + name;

    try
    {
      Logger logger =
        Class.forName(name).asSubclass(Logger.class).newInstance();
      return Log.add(inName, logger);
    }
    catch(ClassNotFoundException e)
    {
      System.err.println("Could not find class '" + inLogger + "' for logging: "
                         + e);
    }
    catch(InstantiationException e)
    {
      System.err.println("Could not instantiate class '" + inLogger
                         + "' for logging: " + e);
    }
    catch(IllegalAccessException e)
    {
      System.err.println("Could not access class '" + inLogger
                         + "' for logging: " + e);
    }

    return false;
  }

  //........................................................................
  //----------------------------- addMessage -------------------------------

  /**
   * Add the given message to the messages created so far. If more messages are
   * stored than required, the oldest message will be deleted.
   *
   * @param       inMessage the message to add
   *
   */
  public static synchronized void addMessage(@Nonnull Message inMessage)
  {
    s_last.addFirst(inMessage);

    if(s_last.size() > s_maxMessages)
      s_last.removeLast();
  }

  //........................................................................
  //-------------------------------- remove --------------------------------

  /**
    * Remove a registered logger.
    *
    * @param       inName the name of the logger to remove
    *
    * @return      true if removed, false if not
    *
    */
  public static boolean remove(@Nullable String inName)
  {
    return s_loggers.remove(inName) != null;
  }

  //........................................................................

  //------------------------------- setLevel -------------------------------

  /**
    * Set the logging level to print up to.
    *
    * @param       inLevel the level to print (more severe levels are
    *                      printed as well)
    *
    */
  public static void setLevel(@Nonnull Type inLevel)
  {
    s_level = inLevel;
  }

  //........................................................................
  //------------------------------- setLevel -------------------------------

  /**
    * Set the logging level to print up to.
    *
    * @param       inLevel the level to print (more severe levels are
    *                      printed as well)
    *
    */
  public static void setLevel(@Nonnull String inLevel)
  {
    s_level = Type.valueOf(inLevel);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //--------------------------------- fatal --------------------------------

  /**
    * Print a fatal message.
    *
    * @param       inMessage the message to print
    *
    * @see         #error
    * @see         #info
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void fatal(@Nullable String inMessage)
  {
    print(inMessage, Type.FATAL);
  }

  //........................................................................
  //--------------------------------- fatal --------------------------------

  /**
    * Print a fatal message.
    *
    * @param       inMessage the message to print
    *
    * @see         #error
    * @see         #info
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void fatal(@Nullable Object inMessage)
  {
    print(inMessage, Type.FATAL);
  }

  //........................................................................
  //--------------------------------- event --------------------------------

  /**
    * Print an event message.
    *
    * @param       inUser    the user responsible for the event, if any
    * @param       inType    the type of the event
    * @param       inMessage the message to print
    *
    */
  public static void event(@Nonnull String inUser, @Nonnull String inType,
                           @Nullable String inMessage)
  {
    print(inUser + " - " + inType + " - " + inMessage, Type.EVENT);
  }

  //........................................................................
  //--------------------------------- track --------------------------------

  /**
    * Track a value with analytics message.
    *
    * @param       inTitle   the title of the value to track
    * @param       inPage    the value to track as a hieararchical page name
    *
    */
  public static void track(@Nonnull String inTitle, @Nonnull String inPage)
  {
    //s_analytics.track(inTitle, inPage);
  }

  //........................................................................
  //------------------------------- trackEvent -----------------------------

  /**
    * Track a value with analytics message.
    *
    * @param       inObject name of the object for the event
    * @param       inAction name of action that lead to the event
    * @param       inLabel  label of the event
    * @param       inValue  the value for the event
    *
    */
  public static void trackEvent(@Nonnull String inObject,
                                @Nonnull String inAction,
                                @Nonnull String inLabel,
                                @Nonnull String inValue)
  {
    //s_analytics.event(inObject, inAction, inLabel, inValue);
  }

  //........................................................................
  //--------------------------------- error --------------------------------

  /**
    * Print an error message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #info
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void error(@Nullable String inMessage)
  {
    print(inMessage, Type.ERROR);
  }

  //........................................................................
  //--------------------------------- error --------------------------------

  /**
    * Print an error message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #info
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void error(@Nullable Object inMessage)
  {
    print(inMessage, Type.ERROR);
  }

  //........................................................................
  //-------------------------------- warning -------------------------------

  /**
    * Print a warning message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #info
    * @see         #error
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void warning(@Nullable String inMessage)
  {
    print(inMessage, Type.WARNING);
  }

  //........................................................................
  //-------------------------------- warning -------------------------------

  /**
    * Print a warning message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #info
    * @see         #error
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void warning(@Nullable Object inMessage)
  {
    print(inMessage, Type.WARNING);
  }

  //........................................................................
  //------------------------------- necessary ------------------------------

  /**
    * Print a necessary logging message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #info
    * @see         #warning
    * @see         #error
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void necessary(@Nullable String inMessage)
  {
    print(inMessage, Type.NECESSARY);
  }

  //........................................................................
  //------------------------------- necessary ------------------------------

  /**
    * Print a necessary logging message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #info
    * @see         #warning
    * @see         #error
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void necessary(@Nullable Object inMessage)
  {
    print(inMessage, Type.NECESSARY);
  }

  //........................................................................
  //------------------------------- important ------------------------------

  /**
    * Print an important logging message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #info
    * @see         #warning
    * @see         #necessary
    * @see         #error
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void important(@Nullable String inMessage)
  {
    print(inMessage, Type.IMPORTANT);
  }

  //........................................................................
  //------------------------------- important ------------------------------

  /**
    * Print an important logging message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #info
    * @see         #warning
    * @see         #necessary
    * @see         #error
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void important(@Nullable Object inMessage)
  {
    print(inMessage, Type.IMPORTANT);
  }

  //........................................................................
  //--------------------------------- useful -------------------------------

  /**
    * Print a useful logging message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #info
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #error
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void useful(@Nullable String inMessage)
  {
    print(inMessage, Type.USEFUL);
  }

  //........................................................................
  //--------------------------------- useful -------------------------------

  /**
    * Print a useful logging message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #info
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #error
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void useful(@Nullable Object inMessage)
  {
    print(inMessage, Type.USEFUL);
  }

  //........................................................................
  //--------------------------------- info ---------------------------------

  /**
    * Print an information message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #error
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void info(@Nullable String inMessage)
  {
    print(inMessage, Type.INFO);
  }

  //........................................................................
  //--------------------------------- info ---------------------------------

  /**
    * Print an information message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #error
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void info(@Nullable Object inMessage)
  {
    print(inMessage, Type.INFO);
  }

  //........................................................................
  //-------------------------------- status --------------------------------

  /**
    * Print an information message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #error
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void status(@Nullable String inMessage)
  {
    print(inMessage, Type.STATUS);
  }

  //........................................................................
  //-------------------------------- status --------------------------------

  /**
    * Print an information message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #error
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  public static void status(@Nullable Object inMessage)
  {
    print(inMessage, Type.STATUS);
  }

  //........................................................................
  //-------------------------------- complete ------------------------------

  /**
    * Print a logging message for complete logging message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #info
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #error
    * @see         #print
    *
    */
  public static void complete(@Nullable String inMessage)
  {
    print(inMessage, Type.COMPLETE);
  }

  //........................................................................
  //-------------------------------- complete ------------------------------

  /**
    * Print a logging message for complete logging message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #info
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #error
    * @see         #print
    *
    */
  public static void complete(@Nullable Object inMessage)
  {
    print(inMessage, Type.COMPLETE);
  }

  //........................................................................
  //--------------------------------- debug --------------------------------

  /**
    * Print a debug message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #info
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #error
    * @see         #complete
    * @see         #print
    *
    */
  public static void debug(@Nullable String inMessage)
  {
    print(inMessage, Type.DEBUG);
  }

  //........................................................................
  //--------------------------------- debug --------------------------------

  /**
    * Print a debug message.
    *
    * @param       inMessage the message to print
    *
    * @see         #fatal
    * @see         #info
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #error
    * @see         #complete
    * @see         #print
    *
    */
  public static void debug(@Nullable Object inMessage)
  {
    print(inMessage, Type.DEBUG);
  }

  //........................................................................

  //-------------------------------- print ---------------------------------

  /**
    * Print a message to all the registered loggers using the given type.
    *
    * @param       inMessage the message to print
    * @param       inType    the message type printed
    *
    * @see         #fatal
    * @see         #error
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
  private static void print(@Nullable Object inMessage, @Nonnull Type inType)
  {
    if(inType.compareTo(s_level) > 0)
      return;

    addMessage(new Message(inMessage.toString(), inType));

    for(Iterator<Logger> i = s_loggers.values().iterator(); i.hasNext(); )
      i.next().print(inMessage, inType);
  }

  //........................................................................
  //-------------------------------- print ---------------------------------

  /**
    * Print a message to all the registered loggers using the given type.
    *
    * @param       inMessage the message to print
    * @param       inType    the message type printed
    *
    * @see         #fatal
    * @see         #error
    * @see         #warning
    * @see         #necessary
    * @see         #important
    * @see         #useful
    * @see         #info
    * @see         #complete
    * @see         #print
    *
    */
//   private static void print(@Nullable Object inMessage, @Nonnull Type inType)
//   {
//     print(inMessage.toString(), inType);
//   }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The Test class for the logging stuff.
   *
   * @hidden
   *
   */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    /** The name of the test logger. */
    private static final String s_name = "test";

    //----- mock -----------------------------------------------------------

    /** This is a mock for the logger for testing purposes. */
    public static class MockLogger implements Logger
    {
      /** The messages expected to be logged. */
      private ArrayList<String> m_expected = new ArrayList<String>();

      /** The messages obtained while logging. */
      private ArrayList<String> m_obtained = new ArrayList<String>();

      /** The classes that are banned. */
      private Set<String> m_banned = new HashSet<String>();

      /** The classes to log. */
      private Set<String> m_logged = new HashSet<String>();

      /** The pattern to denote a pattern message to check.*/
      private static final String PATTERN = "*pattern*";

      /** The pattern to denote a file message to check.*/
      private static final String FILE    = "*file*";

      /**
       * The mock for banning a class.
       *
       * @param inClass the class to ban
       *
       */
      public void banClass(@Nonnull Class inClass)
      {
        banClass(inClass.getName());
      }

      /**
       * The mock for banning a class by string.
       *
       * @param inClass the class to ban
       *
       */
      public void banClass(@Nullable String inClass)
      {
        if(inClass == null)
          return;

        m_banned.add(inClass);
      }

      /**
       * The mock for adding a class to log.
       *
       * @param inClass the class to log
       *
       */
      public void logClass(@Nonnull Class inClass)
      {
        logClass(inClass.getName());
      }

      /**
       * The mock for adding a class by string to log.
       *
       * @param inClass the class to log
       *
       */
      public void logClass(@Nonnull String inClass)
      {
        m_logged.add(inClass);
      }

      /**
       * Mock printing of Strings.
       *
       * @param inText the String to print
       * @param inType the debugging level to print to
       *
       */
      public void print(@Nullable String inText, @Nonnull Type inType)
      {
        StackTraceElement []stack = new Throwable().getStackTrace();

        for(StackTraceElement element : stack)
          if(m_banned.contains(element.getClassName()))
            return;

        if(inType != Type.FATAL && inType != Type.ERROR
           && inType != Type.WARNING)
        {
          boolean found = false;
          for(StackTraceElement element : stack)
            if(m_logged.contains(element.getClassName()))
            {
              found = true;
              break;
            }

          if(!found)
            return;
        }

        m_obtained.add(inType + ": " + inText);
      }

      /**
       * Mock printing of objects.
       *
       * @param inObject the object to print
       * @param inType   the debugging level to print to
       *
       */
      public void print(@Nonnull Object inObject, @Nonnull Type inType)
      {
         print(inObject.toString(), inType);
      }

      /**
       * Add a logging message to be expected in the log.
       *
       * @param inText the logging message to add
       *
       */
      public void addExpected(@Nullable String inText)
      {
        m_expected.add(inText);
      }

      /**
       * Add a logging message containing a pattern to the log.
       *
       * @param inText the pattern message to add
       *
       */
      public void addExpectedPattern(@Nullable String inText)
      {
        m_expected.add(PATTERN + inText);
      }

      /**
       * Add a logging messages containing a file to the log.
       *
       * @param inText the text of the message
       *
       */
      public void addExpectedFile(@Nullable String inText)
      {
        m_expected.add(FILE + inText);
      }

      /** Verify the logging values without a name. */
      public void verify()
      {
        verify("");
      }

      /**
       * Verify the logging values.
       *
       * @param inName the name to verify for
       *
       */
      public void verify(@Nullable String inName)
      {
        final String backslash = "\\\\";
        final String slash     = "/";

        Iterator<String> i;
        Iterator<String> j;
        int count;
        for(i = m_expected.iterator(), j = m_obtained.iterator(), count = 1;
            i.hasNext() || j.hasNext(); count++)
        {
          String expected = "<null>";
          if(i.hasNext())
            expected = i.next();

          String obtained = "<null>";
          if(j.hasNext())
            obtained = j.next();

          if(expected.startsWith(FILE))
            junit.framework.Assert.assertEquals
              ("file message " + count + " not identical (" + inName + ")",
               expected.substring(FILE.length()).replaceAll(backslash, slash),
               obtained.replaceAll(backslash, slash));
          else
            if(!expected.startsWith(PATTERN)
               || !obtained.matches
               ("(?s)" + expected.substring(PATTERN.length())))
              junit.framework.Assert.assertEquals
                ("message " + count + " not identical (" + inName + ")",
                 expected, obtained);
        }

        junit.framework.Assert.assertEquals
          ("number of lines do not match (" + inName + ")",
           m_expected.size(), m_obtained.size());

        m_expected.clear();
        m_obtained.clear();
      }
    }

    //......................................................................

    //----- add ------------------------------------------------------------

    /** Testing for adding logging expressions. */
    @org.junit.Test
    public void add()
    {
      assertFalse("add", Log.add(s_name, m_logger));
      assertEquals("get (added)", m_logger, Log.get(s_name));
      assertTrue("remove", Log.remove(s_name));
      assertFalse("remove (removed)", Log.remove(s_name));
      assertNull("get (removed)", Log.get(s_name));
      assertNull("get empty", Log.get(""));
      assertNull("get empty", Log.get(null));
      assertFalse("add", Log.add(null, m_logger));
      assertFalse("add", Log.add("test", (Logger)null));

      // really adding and removing
      assertTrue("add", Log.add("guru", m_logger));
      assertEquals("get", m_logger, Log.get("guru"));
      assertTrue("remove", Log.remove("guru"));
    }

    //......................................................................
    //----- print ----------------------------------------------------------

    /** Testing of printing. */
    @org.junit.Test
    public void print()
    {
      m_logger.logClass(Test.class);

      m_logger.addExpected("INFO: this is just a test");
      m_logger.addExpected("ERROR: an error message");

      Log.setLevel(Type.COMPLETE);
      Log.info("this is just a test");
      Log.setLevel(Type.ERROR);
      Log.error("an error message");
      Log.info("another info");
      Log.info(Type.INFO);
    }

    //......................................................................
    //----- messages -------------------------------------------------------

    /** Test all message printing. */
    @org.junit.Test
    public void messages()
    {
      m_logger.logClass(Test.class);
      m_logger.logClass(Log.class);

      Log.setLevel(Type.DEBUG);

      // now all the message levels
      m_logger.addExpected("FATAL: fatal error");
      m_logger.addExpected("ERROR: error message");
      m_logger.addExpected("WARNING: warning message");
      m_logger.addExpected("NECESSARY: necessary info message");
      m_logger.addExpected("IMPORTANT: important info message");
      m_logger.addExpected("USEFUL: useful info message");
      m_logger.addExpected("INFO: information message");
      m_logger.addExpected("COMPLETE: just for completeness");
      m_logger.addExpected("DEBUG: debugging purposes");
      m_logger.addExpected("STATUS: some status");
      m_logger.addExpected("EVENT: user - type - message");

      Log.fatal("fatal error");
      Log.error("error message");
      Log.warning("warning message");
      Log.necessary("necessary info message");
      Log.important("important info message");
      Log.useful("useful info message");
      Log.info("information message");
      Log.complete("just for completeness");
      Log.debug("debugging purposes");
      Log.status("some status");
      Log.event("user", "type", "message");
      Log.track("title", "page");
      Log.trackEvent("object", "action", "label", "value");

      m_logger.verify();

      // now the same with an object
      int old = Log.s_maxMessages;
      Log.s_last.clear();
      Log.s_maxMessages = 3;
      m_logger.addExpected("FATAL: FATAL");
      m_logger.addExpected("ERROR: ERROR");
      m_logger.addExpected("WARNING: WARNING");
      m_logger.addExpected("NECESSARY: NECESSARY");
      m_logger.addExpected("IMPORTANT: IMPORTANT");
      m_logger.addExpected("USEFUL: USEFUL");
      m_logger.addExpected("INFO: INFO");
      m_logger.addExpected("COMPLETE: COMPLETE");
      m_logger.addExpected("DEBUG: DEBUG");
      m_logger.addExpected("STATUS: STATUS");

      Log.fatal(Type.FATAL);
      Log.error(Type.ERROR);
      Log.warning(Type.WARNING);
      Log.necessary(Type.NECESSARY);
      Log.important(Type.IMPORTANT);
      Log.useful(Type.USEFUL);
      Log.info(Type.INFO);
      Log.complete(Type.COMPLETE);
      Log.debug(Type.DEBUG);
      Log.status(Type.STATUS);

      // check last message
      Iterator<Message> i = Log.getLast();
      Message message = i.next();
      assertEquals("last", "STATUS: STATUS (0)", message.toString());
      message = i.next();
      assertEquals("last", "DEBUG: DEBUG (0)", message.toString());
      message = i.next();
      assertEquals("last", "COMPLETE: COMPLETE (0)", message.toString());
      assertFalse("end", i.hasNext());

      m_logger.verify();
      Log.s_maxMessages = old;
    }

    //......................................................................
    //----- mock -----------------------------------------------------------

    /** Testing the mock. */
    @org.junit.Test
    public void mock()
    {
      final MockLogger logger = new MockLogger();

      // simple message
      logger.print("test", Type.WARNING);
      logger.addExpected("WARNING: test");

      logger.verify();

      // ignored message
      logger.print("info", Type.INFO);

      logger.verify();

      // banned class
      logger.banClass(Log.Test.class);

      // it is usually called from a higher level, thus we have to simulate
      // that here
      new Object() {
        public void f()
        {
          new Object() {
            public void f()
            {
              logger.print("banned", Type.FATAL);
            }
          } .f();
        }
      } .f();

      logger.verify();
      logger.m_banned.clear();

      // banned class by string
      logger.banClass("net.ixitxachitls.util.logging.Log$Test");
      logger.banClass((String)null);

      new Object() {
        public void f()
        {
          new Object() {
            public void f()
            {
              logger.print("banned", Type.FATAL);
            }
          } .f();
        }
      } .f();

      logger.verify();
      logger.m_banned.clear();

      // logged class
      logger.logClass(Log.Test.class);

      new Object() {
        public void f()
        {
          new Object() {
            public void f()
            {
              logger.print("info test", Type.INFO);
            }
          } .f();
        }
      } .f();

      logger.addExpected("INFO: info test");

      logger.verify();
      logger.m_logged.clear();

      // logged class by string
      logger.logClass("net.ixitxachitls.util.logging.Log$Test");

      new Object() {
        public void f()
        {
          new Object() {
            public void f()
            {
              logger.print("info test", Type.INFO);
            }
          } .f();
        }
      } .f();

      logger.addExpected("INFO: info test");

      logger.verify();
      logger.m_logged.clear();

      // expected file
      logger.addExpectedFile("WARNING: some /path/file.ext");

      logger.print("some \\path\\file.ext", Type.WARNING);

      // expected pattern
      logger.addExpectedPattern("WARNING: some .*");

      logger.print("some with something", Type.WARNING);

      logger.verify();
    }

    //......................................................................
    //----- coverage -------------------------------------------------------

    /** Clean up coverage. */
    @org.junit.Test
    public void coverage()
    {
      new Log();
      assertEquals("value of", Type.WARNING, Log.Type.valueOf("WARNING"));
      assertTrue("values", Log.Type.values().length > 0);

      //s_analytics.stop();
    }

    //......................................................................
  }

  //........................................................................
}


