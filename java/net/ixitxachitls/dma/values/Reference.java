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

package net.ixitxachitls.dma.values;

import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.BaseType;
import net.ixitxachitls.input.ParseReader;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A text value representing a reference to a product.
 *
 * @file          Reference.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 * @param         <T> the type of entry referenced
 *
 */

//..........................................................................

//__________________________________________________________________________

@Immutable
@ParametersAreNonnullByDefault
public class Reference<T extends BaseEntry> extends Value<Reference<T>>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ Reference -------------------------------

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Construct the reference object.
   *
   * @param   inType the type of entry referenced
   *
   */
  public Reference(BaseType<T>inType)
  {
    m_type = inType;
    m_name = new Name();

    withTemplate("reference", "/" + inType.getLink() + "/");
    m_editType = "non-empty";
  }

  //........................................................................
  //------------------------------ Reference -------------------------------

  /**
   * Construct the text object.
   *
   * @param       inType           the type of entry referenced
   * @param       inText           the text to store
   *
   */
  public Reference(BaseType<T>inType, String inText)
  {
    m_type = inType;
    m_name = new Name(inText);

    withTemplate("reference", "/" + inType.getLink() + "/");
    m_editType = "non-empty";
  }

  //........................................................................
  //----------------------------- withParameter ----------------------------

  /**
   * Add parameters to the object. Should only be called when constructing.
   *
   * @param       inName     the parameter name
   * @param       inValue    the parameter value
   * @param       inType     the parameter type
   *
   * @return      the object for chaining
   *
   */
  public Reference<T> withParameter(String inName, Value<?> inValue,
                                    Parameters.Type inType)
  {
    if(m_parameters == null)
      m_parameters = new Parameters();

    m_parameters.with(inName, inValue, inType);

    return this;
  }

  //........................................................................
  //---------------------------- withParameters ----------------------------

  /**
   * Add parameters to the object. Should only be called when constructing.
   *
   * @param       inParameters     the parameters to the reference
   *
   * @return      the object for chaining
   *
   */
  public Reference<T> withParameters(Parameters inParameters)
  {
    m_parameters = inParameters;

    return this;
  }

  //........................................................................

  //-------------------------------- create --------------------------------

  /**
   * Create a new text with the same type information as this one, but one
   * that is still undefined.
   *
   * @return      a similar text, but without any contents
   *
   */
  @Override
  public Reference<T> create()
  {
    if(m_parameters == null)
      return super.create(new Reference<T>(m_type));

    return super.create(new Reference<T>(m_type)
                        .withParameters(m_parameters.create()));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** Flag if references was resolved or not. */
  private boolean m_resolved = false;

  /** The base entry referenced here. */
  private @Nullable T m_entry;

  /** The type of entry referenced. */
  private BaseType<T> m_type;

  /** The name of the refernce. */
  private Name m_name;

  /** The parameters for the reference, if any. */
  private @Nullable Parameters m_parameters;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getName --------------------------------

  /**
   * Get the name of the reference.
   *
   * @return  the name of the reference (without parameters)
   *
   */
  public String getName()
  {
    return m_name.get();
  }

  //........................................................................
  //----------------------------- getFullName ------------------------------

  /**
   * Get the full name for the reference, including available unique parameters.
   *
   * @return the requested full name
   *
   */
  public String getFullName()
  {
    if(m_parameters == null)
      return getName();

    String paramNames = m_parameters.getUniques();
    if(paramNames.isEmpty())
      return getName();

    return getName() + " " + paramNames;
  }

  //........................................................................

  //---------------------------- getParameters -----------------------------

  /**
   * Get the parameters defined for the feat.
   *
   * @return      the parameters, if any
   *
   */
  public @Nullable Parameters getParameters()
  {
    return m_parameters;
  }

  //........................................................................
  //------------------------------- summary --------------------------------

  /**
   * Get the summary for the reference.
   *
   * @param       inParameters  a map with all the parameters to use for the
   *                            reference
   *
   * @return      the string with the summary
   *
   */
  public String summary(@Nullable Map<String, String> inParameters)
  {
    resolve();
    if(m_entry == null)
      return "(unknown)";

    return m_entry.getSummary(m_parameters.asValues(inParameters));
  }

  //........................................................................
  //------------------------------- summary --------------------------------

  /**
   * Get the summary for the reference.
   *
   * @return      the string with the summary
   *
   */
  public String summary()
  {
    resolve();
    if(m_entry == null)
      return "(unknown)";

    return m_entry.getSummary(m_parameters);
  }

  //........................................................................
  //------------------------------- getEntry -------------------------------

  /**
   * Get the entry that is referenced.
   *
   * @return  the referenced entry or null if it could not be found
   *
   */
  public @Nullable T getEntry()
  {
    resolve();
    return m_entry;
  }

  //........................................................................

  //------------------------------ doToString ------------------------------

  /**
   * Return a string representation of the value. The value can be assumed to
   * be defined when this is called. This method should not be called directly,
   * instead call toString().
   *
   * @return      a string representation.
   *
   */
  @Override
  protected String doToString()
  {
    if(m_parameters == null || !m_parameters.isDefined())
      return m_name.toString();

    return m_name.toString() + " [" + m_parameters.toString() + "]";
  }

  //........................................................................
  //------------------------------- resolve --------------------------------

  /**
   * Resolve the referenced base product.
   *
   */
  public void resolve()
  {
    if(m_resolved)
      return;

    if(m_entry == null)
      m_entry = DMADataFactory.get()
        .getEntry(AbstractEntry.createKey(m_name.get(), m_type));

    m_resolved = true;
  }

  //........................................................................
  //------------------------------- doGroup --------------------------------

  /**
   * Really do grouping for this object. This method can be derived to have
   * special grouping in derivations.
   *
   * @return      a string denoting the group this value is in
   *
   */
  @Override
  protected String doGroup()
  {
    return m_name.toString();
  }

  //........................................................................

  //------------------------------ isDefined -------------------------------

  /**
   * Check if the value is defined or not.
   *
   * @return      true if the value is defined, false if not
   *
   */
  @Override
  public boolean isDefined()
  {
    return m_name.isDefined();
  }

  //........................................................................
  //----------------------------- isArithmetic -----------------------------

  /**
   * Checks whether the value is arithmetic and thus can be computed with.
   *
   * @return      true if the value is arithemtic
   *
   */
  @Override
  public boolean isArithmetic()
  {
    return false;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- add ----------------------------------

  /**
   * Add the given reference to this one, if possible.
   *
   * @param    inOther  the reference to add
   *
   * @return   the additional of the current and given reference.
   *
   */
  @Override
  public Reference<T> add(Reference<T> inOther)
  {
    if(!getName().equals(inOther.getName()))
      throw new IllegalStateException("cannot add references with different "
                                      + "names");

    Reference<T> ref = new Reference<T>(m_type, getName());
    if(m_parameters == null)
      ref.m_parameters = inOther.m_parameters;
    else if(inOther.m_parameters == null)
      ref.m_parameters = m_parameters;
    else
      ref.m_parameters = m_parameters.add(inOther.m_parameters);

    return ref;
  }

  //........................................................................

  //------------------------------- doRead ---------------------------------

  /**
   * Read the value from the reader and replace the current one.
   *
   * @param       inReader   the reader to read from
   *
   * @return      true if read, false if not
   *
   */
  @Override
  public boolean doRead(ParseReader inReader)
  {
    Name name = m_name.read(inReader);
    if(name == null)
      return false;

    m_name = name;

    ParseReader.Position pos = inReader.getPosition();
    if(m_parameters != null && inReader.expect('['))
    {
      Parameters parameters = m_parameters.read(inReader);
      if (parameters == null)
      {
        inReader.seek(pos);
        return true;
      }

      m_parameters = parameters;

      if(!inReader.expect(']'))
      {
        inReader.error(inReader.getPosition(), "read.reference.params", null);
        return false;
      }
    }

    return true;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- init -----------------------------------------------------------

    /** Testing init. */
    @org.junit.Test
    public void testInit()
    {
      Name text = new Name();

      // undefined value
      assertEquals("not undefined at start", false, text.isDefined());
      assertEquals("undefined value not correct", "$undefined$",
                   text.toString());
      assertEquals("undefined value not correct", null, text.get());

      // now with some text
      text = text.as("just some = test");

      assertEquals("not defined after setting", true, text.isDefined());
      assertEquals("value not correctly gotten", "just some \\= test",
                   text.toString());
      assertEquals("value not correctly converted", "just some = test",
                   text.get());

      // now with some text
      text = text.as("just some \" test");

      assertEquals("not defined after setting", true, text.isDefined());
      assertEquals("value not correctly gotten", "just some \\\" test",
                   text.toString());
      assertEquals("value not correctly converted", "just some \" test",
                   text.get());

      // add something to the text
      Name added = text.add(new Name("more text"));
      assertEquals("added", "just some \\\" test more text", added.toString());

      added = text.add(new Name(" and more"));
      assertEquals("added", "just some \\\" test and more", added.toString());

      Value.Test.createTest(text);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** Testing reading. */
    @org.junit.Test
    public void testRead()
    {
      // name test
      String []texts =
        {
          "simple", "just some test", "just some test", null,
          "empty", "", null, null,
          "other", "some text = other", "some text", "= other",
          "whites", "   \nsome   \n text  \n \n read", "some text read", null,

          "escapes",
          "some \\= escaped \\\" text",
          "some \\= escaped \\\" text", null,

          "space delimiters",
          "some-text-to-read -here",
          "some-text-to-read", "-here",

          "hint 1", "{*} some text", "{*}some text", null,
          "hint 2", "{~}some text", "{~}some text", null,
          "hint 3", "{*, comment # !.} some text",
          "{*,comment # !.}some text", null,
          "hint 4", "{* some text", null, "{* some text",
        };

      Value.Test.readTest(texts, new Name());
    }

    //......................................................................
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
