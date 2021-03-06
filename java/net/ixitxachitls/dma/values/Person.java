/******************************************************************************
 * Copyright (c) 2002-2013 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.values;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.proto.Entries.BaseProductProto;

/**
 * Small class to encapsulate person information with job.
 *
 * @file Person.java
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Person extends Value<BaseProductProto.Person>
{
  /** The parser for persons. */
  public static class PersonParser extends Parser<Person>
  {
    /** Create the parser. */
    public PersonParser()
    {
      super(2);
    }

    @Override
    public Optional<Person> doParse(String inName, String inJob)
    {
      return Optional.of(new Person(inName, inJob));
    }
  }

  /**
   * Create a person.
   * @param inName the person's name
   * @param inJob the person's job
   */
  public Person(String inName, @Nullable String inJob)
  {
    m_name = inName;
    m_job = inJob == null ? "" : inJob;
  }

  /** The person parser. */
  public static final Parser<Person> PARSER = new PersonParser();

  /** The person's name. */
  private final String m_name;

  /** The person's job. */
  private final String m_job;

  /**
   * Get the name of the person.
   *
   * @return the name
   */
  public String getName()
  {
    return m_name;
  }

  /**
   * Get the job of the person.
   *
   * @return the job
   */
  public String getJob()
  {
    return m_job;
  }

  /**
   * Check whether a job is defined for the person.
   *
   * @return true if a job is set, false if not
   */
  public boolean hasJob()
  {
    return !m_job.isEmpty();
  }

  @Override
  public String toString()
  {
    if(hasJob())
      return m_name + " (" + m_job + ")";

    return m_name;
  }

  @Override
  public BaseProductProto.Person toProto()
  {
    BaseProductProto.Person.Builder proto =
      BaseProductProto.Person.newBuilder();

    proto.setName(m_name);
    if(!m_job.isEmpty())
      proto.setJob(m_job);

    return proto.build();
  }

  /**
   * Create a person from the given proto.
   *
   * @param inProto the proto to create from
   * @return the newly created person
   */
  public static Person fromProto(BaseProductProto.Person inProto)
  {
    return new Person(inProto.getName(), inProto.getJob());
  }
}
