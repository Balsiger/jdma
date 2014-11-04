package net.ixitxachitls.util.logging;

import net.ixitxachitls.util.logging.Log;
import net.ixitxachitls.util.logging.Logger;

import java.util.logging.Level;

/**
 * Created by balsiger on 11/4/14.
 */
public class JavaLogger implements Logger
{
  public JavaLogger()
  {
    // nothing to do here
  }

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

