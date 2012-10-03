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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseEntry;
import net.ixitxachitls.dma.entries.BaseType;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.Command;

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
public class Reference<T extends BaseEntry> extends Value<Reference>
{
  //--------------------------------------------------------- constructor(s)

  //------------------------------ Reference -------------------------------

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
  public Reference(BaseType<T>inType, @Nonnull String inText)
  {
    m_type = inType;
    m_name = new Name(inText);

    withTemplate("reference", "/" + inType.getLink() + "/");
    m_editType = "non-empty";
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
  public Reference<T> withParameters(@Nonnull Map<String, Value> inParameters)
  {
    return withParameters(new Parameters(inParameters));
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
  public Reference<T> withParameters(@Nonnull Parameters inParameters)
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
  @SuppressWarnings("unchecked") // this only works if it is overriden in all
                                 // derivations
  public @Nonnull Reference create()
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
  private @Nonnull BaseType<T> m_type;

  /** The name of the refernce. */
  private @Nonnull Name m_name;

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
  public @Nonnull String getName()
  {
    return m_name.get();
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
   * @return      the string with the summary
   *
   */
  public @Nonnull String summary()
  {
    resolve();
    if(m_entry == null)
      return "(unknown)";

    return m_entry.getSummary(m_parameters);
  }

  //........................................................................

  //------------------------------- doFormat -------------------------------

  /**
   * Really to the formatting.
   *
   * @return      the command for setting the value
   *
   */
  @Override
  protected @Nonnull Command doFormat()
  {
    // resolve();
    // if(m_product == null)
    //   return new Command(getEditValue());

    // return new Link(getEditValue(), "/product/" + m_product.getName());
    return new Command("guru");
  }

  //........................................................................
  //------------------------------- doPrint --------------------------------

  /**
   * Do the standard printing after handling templates.
   *
   * @param       inEntry    the entry this value is in
   * @param       inRenderer the renderer to render sub values
   *
   * @return      the string to be printed
   *
   */
  // protected @Nonnull String doPrint(@Nonnull AbstractEntry inEntry,
  //                                   @Nonnull SoyRenderer inRenderer)
  // {
  //   resolve();
  //   if(m_product == null)
  //     return getEditValue();

  //   return inRenderer.render("dma.value.reference",
  //                            collectData(inEntry, inRenderer));
  // }

  //........................................................................
  //----------------------------- collectData ------------------------------

   /**
    * Collect the data available for printing the value.
    *
    * @param       inEntry    the entry this value is in
    * @param       inRenderer the renderer to render sub values
    *
    * @return      the data as a map
    *
    */
   // @Override
   // public Map<String, Object> collectData(@Nonnull AbstractEntry inEntry,
   //                                        @Nonnull SoyRenderer inRenderer)
   // {
   //   Map<String, Object> data = super.collectData(inEntry, inRenderer);
   //   data.put("id", get());

   //   if(m_product != null)
   //     data.put("name", inRenderer.renderCommands(m_product.getFullTitle()));

   //   return data;
   // }

   //........................................................................
  //----------------------------- getEditValue -----------------------------

   /**
    * Get the value to be used for editing.
    *
    * @return      the value for editing
    *
    */
   // @Override
   // public String getEditValue()
   // {
   //   return m_name.get();
   // }

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
  protected @Nonnull String doToString()
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
  protected @Nonnull String doGroup()
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
  public boolean doRead(@Nonnull ParseReader inReader)
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
      assertEquals("undefined value not correct",
                   "\\color{error}{$undefined$}",
                   text.format().toString());
      assertEquals("undefined value not correct", null, text.get());

      // now with some text
      text = text.as("just some = test");

      assertEquals("not defined after setting", true, text.isDefined());
      assertEquals("value not correctly gotten", "just some \\= test",
                   text.toString());
      assertEquals("value not correctly gotten", "just some = test",
                   text.format().toString());
      assertEquals("value not correctly converted", "just some = test",
                   text.get());

      // now with some text
      text = text.as("just some \" test");

      assertEquals("not defined after setting", true, text.isDefined());
      assertEquals("value not correctly gotten", "just some \" test",
                   text.format().toString());
      assertEquals("value not correctly gotten", "just some \\\" test",
                   text.toString());
      assertEquals("value not correctly converted", "just some \" test",
                   text.get());

      // add something to the text
      Name added = text.add(new Name("more text"));
      assertEquals("added", "just some \\\" test more text", added.toString());
      assertEquals("added", "just some \" test more text",
                   added.format().toString());

      added = text.add(new Name(" and more"));
      assertEquals("added", "just some \\\" test and more", added.toString());
      assertEquals("added", "just some \" test and more",
                   added.format().toString());

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
