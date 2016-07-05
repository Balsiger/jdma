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

package net.ixitxachitls.dma.entries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Optional;
import com.google.protobuf.Message;

import net.ixitxachitls.dma.proto.Entries.BaseCampaignProto;
import net.ixitxachitls.dma.proto.Entries.BaseEntryProto;
import net.ixitxachitls.dma.values.Calendar;
import net.ixitxachitls.dma.values.Values;
import net.ixitxachitls.dma.values.enums.Gender;
import net.ixitxachitls.dma.values.enums.Group;
import net.ixitxachitls.util.logging.Log;

/**
 * This is the base information about a campaign. It is also the place
 * where all the base entries are finally stored..
 *
 * @file          Basecampaign.java
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 */

@ParametersAreNonnullByDefault
public class BaseCampaign extends BaseEntry
{
  /** The serial version id. */
  private static final long serialVersionUID = 1L;

  private Calendar m_calendar;
  private List<Names> m_names = new ArrayList<>();

  private static class Names extends NestedEntry {
    private String m_race;
    private Optional<String> m_region;
    private List<String> m_maleNames;
    private List<String> m_femaleNames ;
    private List<String> m_surenames;

    protected Names()
    {
      m_race = "";
      m_region = Optional.absent();
      m_maleNames = new ArrayList<>();
      m_femaleNames = new ArrayList<>();
      m_surenames = new ArrayList<>();
    }

    public Names(String inRace, Optional<String> inRegion,
                 List<String> inMaleNames, List<String> inFemaleNames,
                 List<String> inSurenames)
    {
      m_race = inRace;
      m_region = inRegion;
      m_maleNames = new ArrayList<>(inMaleNames);
      m_femaleNames = new ArrayList<>(inFemaleNames);
      m_surenames = new ArrayList<>(inSurenames);
    }

    public String getRace()
    {
      return m_race;
    }

    public Optional<String> getRegion()
    {
      return m_region;
    }

    public List<String> getMaleNames()
    {
      return m_maleNames;
    }

    public List<String> getFemaleNames()
    {
      return m_femaleNames;
    }

    public List<String> getSurenames()
    {
      return m_surenames;
    }

    public BaseCampaignProto.Names toProto()
    {
      BaseCampaignProto.Names.Builder names =
          BaseCampaignProto.Names.newBuilder()
              .setRace(m_race);

      if (m_region.isPresent())
        names.setRegion(m_region.get());

      names.addAllMale(m_maleNames);
      names.addAllFemale(m_femaleNames);
      names.addAllSurename(m_surenames);

      return names.build();
    }

    public static Names fromProto(BaseCampaignProto.Names inProto)
    {
      return new Names(inProto.getRace(),
                       inProto.hasRegion()
                           ? Optional.of(inProto.getRegion())
                           : Optional.<String>absent(),
                       inProto.getMaleList(), inProto.getFemaleList(),
                       inProto.getSurenameList());
    }

    @Override
    public void set(Values inValues)
    {
      m_race = inValues.use("race", m_race);
      m_region = inValues.use("region", m_region);
      m_maleNames = inValues.use("male_name", m_maleNames);
      m_femaleNames = inValues.use("female_name", m_femaleNames);
      m_surenames = inValues.use("surename", m_surenames);
    }

    public boolean matches(String inRace, Optional<String> inRegion)
    {
      if(!m_race.equalsIgnoreCase(inRace))
        return false;

      if(!m_region.isPresent())
        return true;

      if(!inRegion.isPresent() || inRegion.get().isEmpty())
        return false;

      return m_region.get().equalsIgnoreCase(inRegion.get());
    }

    public String generate(Gender inGender)
    {
      String first;
      switch(inGender)
      {
        case MALE:
          first = random(m_maleNames);
          break;

        case FEMALE:
          first = random(m_femaleNames);
          break;

        case UNKNOWN:
        case OTHER:
        default:
          return "must select a gender to generate a name";
      }

      String last = random(m_surenames);
      if (first.trim().isEmpty())
        return last;

      if(last.isEmpty())
        return first;

      return first + " " + last;
    }

    private String random(List<String> inList)
    {
      if(inList.isEmpty())
        return "";

      return inList.get(RANDOM.nextInt(inList.size()));
    }
  }

  /**
   * This is the internal, default constructor for an undefined value.
   */
  public BaseCampaign()
  {
    super(TYPE);
  }

  /**
   * This is the normal constructor.
   *
   * @param       inName the name of the base item
   *
   */
  public BaseCampaign(String inName)
  {
    super(inName, TYPE);

    m_calendar = new Calendar();
  }

  /** The type of this entry. */
  public static final BaseType<BaseCampaign> TYPE =
    new BaseType.Builder<BaseCampaign>(BaseCampaign.class).build();

  @Override
  public boolean isDM(Optional<BaseCharacter> inUser)
  {
    if(!inUser.isPresent())
      return false;

    return inUser.get().hasAccess(Group.ADMIN);
  }

  public Calendar getCalendar()
  {
    return m_calendar;
  }

  public List<Names> getNames()
  {
    return Collections.unmodifiableList(m_names);
  }

  @Override
  public Message toProto()
  {
    BaseCampaignProto.Builder builder = BaseCampaignProto.newBuilder();

    builder.setBase((BaseEntryProto)super.toProto());
    builder.setCalendar(m_calendar.toProto());
    for(Names names : m_names)
      builder.addNames(names.toProto());

    BaseCampaignProto proto = builder.build();
    return proto;
  }

  /**
   * Read all values from a proto into this entry.
   *
   * @param inProto the proto with the values
   */
  public void fromProto(Message inProto)
  {
    if(!(inProto instanceof BaseCampaignProto))
    {
      Log.warning("cannot parse proto " + inProto.getClass());
      return;
    }

    BaseCampaignProto proto = (BaseCampaignProto)inProto;

    if(proto.hasCalendar())
      m_calendar = Calendar.fromProto(proto.getCalendar());
    for(BaseCampaignProto.Names names : proto.getNamesList())
      m_names.add(Names.fromProto(names));

    super.fromProto(proto.getBase());
  }

  public String generateRandomName(String inRace,
                                   Optional<String> inRegion,
                                   Gender inGender)
  {
    for(Names names : m_names)
      if(names.matches(inRace, inRegion))
        return names.generate(inGender);

    return "(no match for " + inRace +
        (inRegion.isPresent() && !inRegion.get().isEmpty()
            ? "/" + inRegion.get() : "") + ")";
  }

  @Override
  protected Message defaultProto()
  {
    return BaseCampaignProto.getDefaultInstance();
  }

  @Override
  public void setValues(Values inValues)
  {
    super.setValues(inValues);

    m_calendar.set(inValues);
    m_names = inValues.useEntries("names", m_names,
                                  new NestedEntry.Creator<Names>()
                                  {
                                    @Override
                                    public Names create()
                                    {
                                      return new Names();
                                    }
                                  });
  }

  //---------------------------------------------------------------------------

  /** The test. @hidden */
  public static class Test //extends ValueGroup.Test
  {
    // TODO: fix tests
    /** Test text. */
    private static String s_text =
      "base campaign Test = \n"
      + "\n"
      + "  synonyms          \"test\", \"tst\";"
      + "  worlds            Generic, Forgotten Realms;"
      + "  short description \"Just a test\";"
      + "  description       \"A test campaign\".";

    /** Create a typical base item for testing purposes.
     *
     * @return the newly created base item
     */
    public static BaseCampaign createBaseCampaign()
    {
      net.ixitxachitls.input.ParseReader reader =
        new net.ixitxachitls.input.ParseReader
        (new java.io.StringReader(s_text), "test"); // $codepro.audit.disable

      //return (BaseCampaign)AbstractEntry.read(reader);
      return null;
    }
  }
}
