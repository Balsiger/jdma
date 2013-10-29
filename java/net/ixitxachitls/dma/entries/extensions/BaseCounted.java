/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.entries.extensions;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Multimap;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.entries.BaseItem;
import net.ixitxachitls.dma.entries.indexes.Index;
import net.ixitxachitls.dma.proto.Entries.BaseCountedProto;
import net.ixitxachitls.dma.values.EnumSelection;
import net.ixitxachitls.dma.values.Number;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the counted extension for all the entries.
 *
 * @file          BaseCounted.java
 *
 * @author        balsiger@ixitxachitls.net Peter 'Merlin' Balsiger
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class BaseCounted extends BaseExtension<BaseItem>
{
  //----------------------------------------------------------------- nested

  //----- units -------------------------------------------------------------

  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  /** The possible counting unites in the game. */
  public enum Unit implements EnumSelection.Named,
    EnumSelection.Proto<BaseCountedProto.Unit>
  {
    /** Number of days. */
    DAY("day", "days", BaseCountedProto.Unit.DAY),
    /** Numer of pieces. */
    PIECE("piece", "pieces", BaseCountedProto.Unit.PIECE),
    /** Number of sheets. */
    SHEET("sheet", "sheets", BaseCountedProto.Unit.SHEET),
    /** Number of individual uses. */
    USE("use", "uses", BaseCountedProto.Unit.USE),
    /** Number of pages. */
    PAGE("page", "pages", BaseCountedProto.Unit.PAGE),
    /** Charges. */
    CHARGE("charge", "charges", BaseCountedProto.Unit.CHARGE),
    /** Can be applied. */
    APPLICATION("application", "applications",
                BaseCountedProto.Unit.APPLICATION),
    /** Can absorb or take some damage. */
    DAMAGE("damage", "damage", BaseCountedProto.Unit.DAMAGE);

    /** The value's name. */
    private String m_name;

    /** The value's name for multiple unites. */
    private String m_multiple;

    /** The proto enum value. */
    private BaseCountedProto.Unit m_proto;

    /** Create the name.
     *
     * @param inName     the name of the value
     * @param inMultiple the text for multiple units
     * @param inProto    the proto enum value
     */
    private Unit(String inName, String inMultiple,
                 BaseCountedProto.Unit inProto)
    {
      m_name = constant("count.unit.name", inName);
      m_multiple = constant("count.unit.multiple", inMultiple);
      m_proto = inProto;
    }

    @Override
    public String getName()
    {
      return m_name;
    }

    /**
     * Get the multiple name.
     *
     * @return the multiple name
     */
    public String getMultiple()
    {
      return m_multiple;
    }

    @Override
    public String toString()
    {
      return m_name;
    }

    @Override
    public BaseCountedProto.Unit toProto()
    {
      return m_proto;
    }

    /**
     * Get the unit corresponding to the given proto value.
     *
     * @param inProto the proto value to convert
     * @return the corresponding enum value
     */
    public static Unit fromProto(BaseCountedProto.Unit inProto)
    {
      for(Unit unit : values())
        if(unit.m_proto == inProto)
          return unit;

      throw new IllegalStateException("cannot convert unit: " + inProto);
    }
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //----------------------------- BaseCounted ----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inName  the name of the extension
   *
   */
  public BaseCounted(BaseItem inEntry, String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //----------------------------- BaseCounted ----------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the base item attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   */
  // public BaseCounted(BaseItem inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  //----- count ------------------------------------------------------------

  /** The base  count value. */
  @Key("count")
  protected Number m_count = new Number(1, 10000);

  static
  {
    addIndex(new Index(Index.Path.COUNTS, "Counts", BaseItem.TYPE));
  }

  //........................................................................
  //----- unit -------------------------------------------------------------

  /** The unit used for the things counted. */
  @Key("unit")
  protected EnumSelection<Unit> m_unit = new EnumSelection<Unit>(Unit.class);

  static
  {
    addIndex(new Index(Index.Path.UNITS, "Units", BaseItem.TYPE));
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //------------------------- computeIndexValues ---------------------------

  /**
   * Get all the values for all the indexes.
   *
   * @param       ioValues a multi map of values per index name
   *
   */
  @Override
  public void computeIndexValues(Multimap<Index.Path, String> ioValues)
  {
    super.computeIndexValues(ioValues);

    ioValues.put(Index.Path.COUNTS, m_count.group());
    ioValues.put(Index.Path.UNITS, m_unit.group());
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  @Override
  public Message toProto()
  {
    BaseCountedProto.Builder builder = BaseCountedProto.newBuilder();

    builder.setCount((int)m_count.get());
    if(m_unit.isDefined())
      builder.setUnit(m_unit.getSelected().toProto());

    return builder.build();
  }

  @Override
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseCountedProto))
    {
      Log.warning("cannot parse base counted proto " + inProto.getClass());
      return;
    }

    BaseCountedProto proto = (BaseCountedProto)inProto;

    m_count = m_count.as(proto.getCount());
    m_unit = m_unit.as(Unit.fromProto(proto.getUnit()));
  }

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
