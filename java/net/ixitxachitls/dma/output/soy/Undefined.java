package net.ixitxachitls.dma.output.soy;

import javax.annotation.concurrent.Immutable;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.restricted.BooleanData;

/**
 * An undefined value that can still be dereferenced, resulting in another
 * undefined value.
 */
@Immutable
public class Undefined extends SoyAbstract
{
  /**
   * Create the undefined value with the given name.
   *
   * @param inName the name of the value
   */
  public Undefined(String inName)
  {
    super(inName, null);
  }

  /**
   * Get the named value.
   *
   * @param  inName the name of the value to get
   *
   * @return the value with the given name
   */
  @Override
  public SoyData getSingle(String inName)
  {
    switch(inName)
    {
      case "present":
        return BooleanData.FALSE;
    }

    return new Undefined(m_name + "." + inName);
  }

  /**
   * Convert the undefined value to a human readable string.
   *
   * @return the string conversion
   */
  @Override
  public String toString()
  {
    return "(undefined " + m_name + ")";
  }
}
