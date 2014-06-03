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

package net.ixitxachitls.dma.server.servlets;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The servlet to save given entries.
 *
 * @file          RemoveActionServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class RemoveActionServlet extends ActionServlet
{
  //----------------------------------------------------------------- nested

  /**
   * Storage for the change information for one or multiple entries.
   */
//  private static class Changes
//  {
//    /**
//     * Create a change for one or multiple entries.
//     *
//     * @param inKey         the key to the entry
//     * @param inOwner       the owner of the entry/entries
//     *
//     */
//    public Changes(AbstractEntry.EntryKey<?> inKey, AbstractEntry inOwner)
//    {
//      m_key = inKey;
//      m_owner = inOwner;
//    }
//
//    /** The key of the entry/entries changed. */
//    protected AbstractEntry.EntryKey<?> m_key;
//
//    /** The owner of the entry/entries changed. */
//    protected @Nullable AbstractEntry m_owner;
//
//    /** The file for the entry/entries. */
//    protected @Nullable String m_file;
//
//    /** Flag if creating a new value or not. */
//    protected boolean m_create = false;
//
//    /** The extensions for the entry, if any. */
//    protected @Nullable String []m_extensions;
//
//    /** A flag if multiple entries are affected by the change. */
//    protected boolean m_multiple = false;
//
//    /** The path to store the created entry after creation, if any. */
//    protected @Nullable String m_store;
//
//    /** A map with all the changed values. */
//    protected Map<String, String>m_values =
//      new HashMap<String, String>();
//
//    /**
//     * Convert to a human readable string for debugging.
//     *
//     * @return the converted string
//     *
//     */
//    @Override
//    public String toString()
//    {
//      return m_key + " (" + (m_owner == null ? "no owner" : m_owner.getName())
//        + "/" + m_file + (m_multiple ? ", multiple" : ", single") + "):"
//        + m_values;
//    }
//
//    /**
//     * Set a value in the change for a given key.
//     *
//     * @param inKey   the key of the value to change
//     * @param inValue the value to change to
//     *
//     */
//    public void set(String inKey, String inValue)
//    {
//      if("file".equals(inKey))
//        m_file = inValue;
//      else if("create".equals(inKey))
//        m_create = true;
//      else if("extensions".equals(inKey))
//      {
//        m_extensions = inValue.split("\\s*,\\s*");
//        // also set it as a normal value for existing entries
//        m_values.put(inKey, inValue);
//      }
//      else
//        m_values.put(inKey, inValue);
//
//      if(inKey.indexOf("=") >= 0)
//        m_multiple = true;
//    }
//
//    /**
//     * Set the path to store this entry after creation, if any.
//     *
//     * @param  inStore the name of the entry to store in
//     */
//    public void store(@Nullable String inStore)
//    {
//      if(inStore != null)
//        m_store = inStore;
//    }
//
//    /**
//     * Figure out the affected entries.
//     *
//     * @param ioErrors the errors encountered, will be adjusted
//     *
//     * @return a set with all the entries affected by this change
//     *
//     */
//    @SuppressWarnings("unchecked")
//    public Set<AbstractEntry> entries(List<String> ioErrors)
//    {
//      Set<AbstractEntry> entries = new HashSet<AbstractEntry>();
//      if(m_multiple)
//      {
//       assert m_values.size() == 1 : "Only expected a single value to change";
//        String []parts = m_values.keySet().iterator().next().split("/");
//        String index = parts[0];
//        String value = parts[1];
//        entries.addAll(DMADataFactory.get().getIndexEntries(index,
//                                                            m_key.getType(),
//                                                            m_key.getParent(),
//                                                            value, 0, 0));
//      }
//      else
//      {
//        AbstractEntry entry = DMADataFactory.get().getEntry(m_key);
//
//        if(entry == null && m_create)
//        {
//          // create a new entry for filling out
//          Log.event(m_owner.getName(), "create",
//                    "creating " + m_key.getType() + " entry '" + m_key.getID()
//                    + "'");
//
//          entry = m_key.getType().create(m_key.getID());
//          if(entry != null)
//            entry.updateKey(m_key);
//
//          // setting up extensions first
//          if(m_extensions != null)
//            for(String extension : m_extensions)
//              entry.addExtension(extension);
//        }
//
//        if(entry == null)
//        {
//          String error = "could not find " + m_key + " for saving";
//          Log.warning(error);
//          ioErrors.add("gui.alert(" + Encodings.toJSString(error) + ");");
//        }
//        else
//          entries.add(entry);
//      }
//
//      return entries;
//    }
//  }

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //--------------------------- RemoveActionServlet --------------------------

  /**
   * Create the entry action servlet.
   */
  public RemoveActionServlet()
  {
    // nothing to do
  }

  //......................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  //........................................................................

  //-------------------------------------------------------------- accessors
  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- doAction -------------------------------

  /**
   *
   * Execute the action associated with this servlet.
   *
   * @param       inRequest  the request for the page
   * @param       inResponse the response to write to
   *
   * @return      the javascript code to send back to the client
   *
   */
  @Override
  protected String doAction(DMARequest inRequest,
                            HttpServletResponse inResponse)
  {
    Optional<BaseCharacter> user = inRequest.getUser();

    if(!user.isPresent())
      return "gui.alert('Must be logged in to delete!');";

    String keyParam = inRequest.getParam("key");
    Optional<EntryKey> key = EntryKey.fromString(keyParam);

    if(!key.isPresent())
      return "gui.alert('Invalid key " + keyParam + "');";

    AbstractEntry entry = DMADataFactory.get().getEntry(key.get());
    if(entry == null)
      return "gui.alert('Could not find " + key + " to delete');";

    if(!entry.isDM(user))
      return "gui.alert('Not allow to delete " + key + "!');";

    if(DMADataFactory.get().remove(entry))
    {
      Log.important("Deleted entry " + keyParam);
      return "gui.info('Entry " + key + " deleted!');";
    }

    return "gui.alert('Could not delete " + key + "');";
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................
}
