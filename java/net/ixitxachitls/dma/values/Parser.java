package net.ixitxachitls.dma.values;

import com.google.common.base.Optional;

/** Simple interface for parsing values. */
public abstract class Parser<P>
{
  public Parser(int inArguments)
  {
    m_arguments = inArguments;
  }

  /** The number of expected arguments, or -1 for any number. */
  private final int m_arguments;

  /**
   * Parse the value from the given string.
   *
   * @param inValues the string values to parse from
   * @return the parsed value
   */
  public Optional<P> parse(String ... inValues)
  {
    if(inValues == null || inValues.length == 0)
      return Optional.absent();

    for(int i = 0; i < inValues.length; i++)
      if(inValues[i] == null)
        inValues[i] = "";

    if(m_arguments > 0 && inValues.length != m_arguments)
      inValues = split(inValues);

    if(m_arguments > 0 && inValues.length != m_arguments)
      return Optional.absent();

    switch(m_arguments)
    {
      case 1:
        return doParse(inValues[0]);

      case 2:
        return doParse(inValues[0], inValues[1]);

      case 3:
        return doParse(inValues[0], inValues[1], inValues[2]);

      case 4:
        return doParse(inValues[0], inValues[1], inValues[2], inValues[3]);

      case 5:
        return doParse(inValues[0], inValues[1], inValues[2], inValues[3],
                       inValues[4]);

      default:
        return doParse(inValues);
    }
  }

  protected String []split(String []inValues)
  {
    return inValues;
  }

  protected Optional<P> doParse(String inValue)
  {
    return Optional.absent();
  }

  protected Optional<P> doParse(String inFirst, String inSecond)
  {
    return Optional.absent();
  }

  protected Optional<P> doParse(String inFirst, String inSecond,
                                String inThird)
  {
    return Optional.absent();
  }

  protected Optional<P> doParse(String inFirst, String inSecond,
                                String inThird, String inFourth)
  {
    return Optional.absent();
  }

  protected Optional<P> doParse(String inFirst, String inSecond,
                                String inThird, String inFourth,
                                String inFifth)
  {
    return Optional.absent();
  }

  protected Optional<P> doParse(String ... inValues)
  {
    return Optional.absent();
  }
}
