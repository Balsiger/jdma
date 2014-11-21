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

import java.io.Serializable;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Iterators;

import net.ixitxachitls.input.ParseReader;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * A remark to a value.
 *
 * @file          Remark.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class Remark implements Serializable
{
  //----------------------------------------------------------------- nested

  //----- Type -------------------------------------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The remark types. */
  @ParametersAreNonnullByDefault
  public enum Type
  {
    /** A house rule. */
    HOUSE_RULE("*", "House Rule"),

    /** An estimation only. */
    ESTIMATION("~", "Estimation"),

    /** The value was automatically computed. */
    AUTO("@", "Auto"),

    /** A randomly determine or adjusted value. */
    RANDOM("%", "Random"),

    /** A player denoted value. */
    PLAYER("p", "Player"),

    /** A DM denoted value. */
    DM("d", "DM");

    //------------------------------- Type -------------------------------

    /**
     * Construct the type.
     *
     * @param       inKey         the key for the type
     * @param       inDescription the description of the type
     *
     */
    private Type(String inKey, String inDescription)
    {
      m_key         = inKey;
      m_description = inDescription;
    }

    //......................................................................

    //---------------------------------------------------------- variables

    /** The key to be read. */
    private String m_key;

    /** The description. */
    private String m_description;

    //......................................................................

    //--------------------------- getDescription ---------------------------

    /**
     * Get the description of the type.
     *
     * @return      the description
     *
     */
    public String getDescription()
    {
      return m_description;
    }

    //......................................................................
    //------------------------------- toString -----------------------------

    /**
     * Convert the object to a human readable string.
     *
     * @return      the String representation
     *
     */
    @Override
    public String toString()
    {
      return m_key;
    }

    //......................................................................
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //-------------------------------- Remark --------------------------------

  /**
   * Create the remark.
   *
   * @param       inType    the type of the remark
   * @param       inComment the comment associated with the remark
   *
   */
  public Remark(Type inType, @Nullable String inComment)
  {
    m_type    = inType;
    m_comment = inComment;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of the remark. */
  private Type m_type;

  /** The comment. */
  private @Nullable String m_comment;

  /** The remark starter. */
  private static final char s_start = '{';

  /** The remark end. */
  private static final char s_end = '}';

  /** The comment starter. */
  private static final char s_comment = ',';

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------------- getType --------------------------------

  /**
   * Get the type of the remark.
   *
   * @return      the type of the remark
   *
   */
  public Type getType()
  {
    return m_type;
  }

  //........................................................................
  //------------------------------ getComment ------------------------------

  /**
   * Get the comment associated with the remark, if any.
   *
   * @return      a String with the comment or null if no comment
   *
   */
  public @Nullable String getComment()
  {
    return m_comment;
  }

  //........................................................................

  //-------------------------------- toString ------------------------------

  /**
   * Convert the object to a human readable string.
   *
   * @return      the String representation
   *
   */
  @Override
  public String toString()
  {
    return s_start + m_type.toString()
      + (m_comment == null ? "" : s_comment + m_comment) + s_end;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- read ---------------------------------

  /**
   * Read a remark from the given reader.
   *
   * @param       inReader   the reader to read from
   *
   * @return      the Remark read or null if none was found
   *
   */
  public static @Nullable Remark read(ParseReader inReader)
  {
    ParseReader.Position pos = inReader.getPosition();

    if(!inReader.expect(s_start))
    {
      inReader.seek(pos);

      return null;
    }

    Type type = inReader.expect(Iterators.forArray(Type.values()));
    if(type == null)
    {
      inReader.seek(pos);

      return null;
    }

    Remark remark = null;
    if(inReader.expect(s_comment))
      // we have some comment to read
      remark = new Remark(type, inReader.read(s_end).trim());
    else
      remark = new Remark(type, null);

    if(!inReader.expect(s_end))
    {
      inReader.seek(pos);

      return null;
    }

    return remark;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- read -----------------------------------------------------------

    /** Test reading of remark. */
    @org.junit.Test
    public void read()
    {
      try (ParseReader reader =
        new ParseReader(new java.io.StringReader("{~,just a test}    "
                                                 + "{*} guru"),
                        "test"))
      {
        Remark remark = Remark.read(reader);

        assertEquals("first", Type.ESTIMATION, remark.getType());
        assertEquals("first", "just a test", remark.getComment());
        assertEquals("first", "{~,just a test}", remark.toString());

        remark = Remark.read(reader);

        assertEquals("second", Type.HOUSE_RULE, remark.getType());
        assertNull("second", remark.getComment());
        assertEquals("second", "{*}", remark.toString());

        assertNull("last", Remark.read(reader));
      }
    }

    //......................................................................
  }

  //........................................................................
}
