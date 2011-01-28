/******************************************************************************
 * Copyright (c) 2002,2003 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.entries.attachments;

// import java.lang.reflect.Field;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.Iterator;
// import java.util.List;
// import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// import net.ixitxachitls.dma.data.Storage;
// import net.ixitxachitls.dma.entries.AbstractEntry;
// import net.ixitxachitls.dma.entries.BaseEntry;
// import net.ixitxachitls.dma.entries.Entry;
import net.ixitxachitls.dma.entries.TaggedVariables;
import net.ixitxachitls.dma.entries.ValueGroup;
import net.ixitxachitls.dma.entries.Variables;
// import net.ixitxachitls.dma.entries.actions.Action;
import net.ixitxachitls.dma.values.Value;
// import net.ixitxachitls.output.commands.Highlight;
// import net.ixitxachitls.output.commands.Icon;
// import net.ixitxachitls.util.ArrayIterator;
import net.ixitxachitls.util.configuration.Config;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the basic attachment for all the entries.
 * TODO: clean up comments
 *
 * @file          AbstractAttachment.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 * @param         <T> the type of entry this attachment is associated with
 *
 */

//..........................................................................

//__________________________________________________________________________

public abstract class AbstractAttachment<T /*extends AbstractEntry*/>
  extends ValueGroup
{
  //--------------------------------------------------------- constructor(s)

  //-------------------------- AbstractAttachment --------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry  the entry attached to
   * @param       inName   the name of the attachment
   *
   */
  public AbstractAttachment(@Nonnull T inEntry, @Nonnull String inName)
  {
    this(inEntry, null, inName);
  }

  //........................................................................
  //-------------------------- AbstractAttachment --------------------------

  /**
   * Constructor with all the values.
   *
   * @param       inEntry  the entry attached to
   * @param       inTag    the tag for this attachment
   * @param       inName   the name for this attachment
   *
   */
  public AbstractAttachment(@Nonnull T inEntry, @Nullable String inTag,
                            @Nonnull String inName)
  {
    m_tag   = inTag;
    m_name  = inName;
    m_entry = inEntry;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The name of the attachment. */
  protected @Nonnull String m_name;

  /** The tag of the attachment, if any. */
  protected @Nullable String m_tag;

  /** The entry this attachment is associated with. */
  protected @Nonnull T m_entry;

  /** The directory for attachment icons (inside the icon directory). */
  protected static final @Nonnull String s_attachmentDir =
    Config.get("resource:html/dir.attachments", "attachments");

//   /** All the possible auto attachments for each class (if any). */
// protected static final @Nonnull Map<Class, List<String>> s_autoAttachments =
//     new HashMap<Class, List<String>>();

  //........................................................................

  //-------------------------------------------------------------- accessors

  //---------------------------- getVariables ------------------------------

  /**
   * Get the variables possible for this attachment. This version also handles
   * tags, if they are present.
   *
   * @return      all the values
   *
   */
  public @Nonnull Variables getVariables()
  {
    if(m_tag == null)
      return super.getVariables();

    return TaggedVariables.tag(m_tag, super.getVariables());
  }

  //........................................................................
  //------------------------------- getValue -------------------------------

  /**
   * Get a value given as a field. We have to copy the method from ValueGroup
   * to give ValueGroups access to protected fields of this class.
   *
   * @param       inField the field for which to get the value
   *
   * @return      the value the field has in this object, if any
   *
   */
//   public @Nullable Variable getVariable(@Nonnull Field inField)
//   {

//     try
//     {
//       return (Value)inField.get(this);
//     }
//     catch(java.lang.IllegalAccessException e)
//     {
//       throw new UnsupportedOperationException
//         ("Cannot access field " + inField.getName() + ": " + e);
//     }
//   }

  //........................................................................
  //--------------------------- addPrintCommands ---------------------------

  /**
   * Add the commands for printing this attachment to the given print command.
   *
   * @param       ioCommands the commands to add to
   * @param       inDM       flag if setting for DM or not
   * @param       inEditable flag if values editable or not
   *
   */
//   public void addPrintCommands(@MayBeNull PrintCommand ioCommands,
//                                boolean inDM, boolean inEditable)
//   {
//     if(ioCommands == null)
//       return;

//     ioCommands.addValue("attachment", getName(), false, false, false,
//                         "attachments");
//   }

  //........................................................................
  //---------------------------- addListCommands ---------------------------

  /**
   * Add the commands for printing this attachment to a list.
   *
   * @param       ioCommands the commands to add to
   * @param       inDM       flag if setting for DM or not
   *
   */
//   public void addListCommands(ListCommand ioCommands, boolean inDM)
//   {
//     // nothing to do here yet
//   }

  //........................................................................
  //-------------------------- addSummaryCommand ---------------------------

  /**
   * Add the attachments value to the summary command list.
   *
   * @param       ioCommands the commands so far, will add here
   * @param       inDM       true if setting for the dm
   *
   */
//   public void addSummaryCommands(List<Object> ioCommands, boolean inDM)
//   {
//     // nothing to do here
//   }

  //........................................................................

  //---------------------------- getSubEntries -----------------------------

  /**
   * Get all the sub entries present in this attachment.
   *
   * @param       inDeep return subentries deeply or just for this attachment
   *
   * @return      the list with all the entries or null if none
   *
   * @undefined   never (may return null)
   *
   */
//   public List<Entry> getSubEntries(boolean inDeep)
//   {
//     return null;
//   }

  //........................................................................

  //------------------------------- getEntry -------------------------------

  /**
   * Get the entry this attachment is attached to, if any.
   *
   * @return      the entry or base entry this one is attached to
   *
   */
  public @Nonnull T getEntry()
  {
    return m_entry;
  }

  //........................................................................
  //------------------------------- getName --------------------------------

  /**
    * Get the name of the attachment.
    *
    * @return      the name
    *
    */
  public @Nonnull String getName()
  {
    return m_name;
  }

  //........................................................................
  //-------------------------------- getID ---------------------------------

  /**
   * Get the identification of the attachment, containing the name and the
   * tag, if any.
   *
   * @return      the id
   *
   * @undefined   never
   *
   */
  public @Nonnull String getID()
  {
    if(m_tag == null)
      return m_name;
    else
      return m_tag + ":" + m_name;
  }

  //........................................................................
  //-------------------------- getAutoAttachments --------------------------

//   /**
//    * Get all attachments automatically present in entry if this attachment
//    * is present in a base entry.
//    *
//    * @param       inClass the class to get the attachments for
//    *
//    * @return      an iterator with all the names of the attachments
//    *
//    */
//   public static List<String> getAutoAttachments(Class inClass)
//   {
//     return List<String>(s_autoAttachments.get(inClass));
//   }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- store ---------------------------------

  /**
   * Store this entry in the given storage container.
   *
   * @param       inStorage   the storage that stores this entry
   *
   * @return      true if stored, false if not
   *
   */
//   public boolean store(Storage<? extends AbstractEntry> inStorage)
//   {
//     // nothing to do here, maybe in derivations...
//     return true;
//   }

  //........................................................................
  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled.
   *
   */
//   public void complete()
//   {
//     List<AbstractEntry> bases = new ArrayList<AbstractEntry>();

//     List<BaseEntry> baseEntries = m_entry.getBaseEntries();

//     if(baseEntries != null)
//       for(BaseEntry entry : m_entry.getBaseEntries())
//       {
//         if(entry == null)
//           continue;

//         bases.add(entry);
//       }

//     // complete the values to make sure intializers and the like are handled
//     completeVariables(bases);

//     m_complete = true;
//   }

  //........................................................................
  //-------------------------------- check ---------------------------------

  /**
   * Check the attachment for possible problems.
   *
   * @return      true if no problem found, false if there was a problem
   *
   */
  public boolean check()
  {
    return true;
  }

  //........................................................................
  //------------------------------- changed --------------------------------

  /**
   * Set the state of the file to changed.
   *
   * @param       inChanged the value to set to, true for changed (dirty),
   *                        false for unchanged (clean)
   *
   */
  public void changed(boolean inChanged)
  {
    // TODO: enable this as soon as entry is there
//     m_entry.changed(inChanged);
  }

  //........................................................................
  //------------------------------- execute --------------------------------

  /**
   * Execute the given action.
   *
   * @param       inAction the action to execute
   *
   * @return      true if executed and no more execution necessary, false if
   *              execute but either unsuccessfully or other instances need to
   *              execute as well.
   *
   * @undefined   IllegalArgumentException if no action given
   *
   */
//   public boolean execute(Action inAction)
//   {
//     if(inAction == null)
//       throw new IllegalArgumentException("must have an action here");

//     // don't know how to handle actions
//     return false;
//   }

  //........................................................................

  //-------------------------- setAutoAttachments --------------------------

  /**
   * Set the automatic attachments to be used for entry attachments.
   *
   * @param       inClass       the class to set for
   * @param       inAbstractAttachments the automatic attachments to use
   *
   */
//   protected static void setAutoAttachments
//     (@Nonnull Class inClass, @Nonnull String ... inAbstractAttachments)
//   {
//     s_autoAttachments.put(inClass, Collections.unmodifiableList
//                           (Arrays.asList(inAbstractAttachments)));
//   }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends ValueGroup.Test
  {
    /** A simple attachment class for testing. */
    public static class TestAttachment<T> extends AbstractAttachment<T>
    {
      /** A test value. */
      @Key("value")
      protected Value m_value = new Value.Test.TestValue();

      /**
       * The constructor.
       *
       * @param inEntry the entry this is attached to
       * @param inTag   the tag
       * @param inName  the name
       */
      public TestAttachment(T inEntry, String inTag, String inName)
      {
        super(inEntry, inTag, inName);
      }
    }

    //----- create ---------------------------------------------------------

    /** The create Test. */
    @org.junit.Test
    public void create()
    {
      extractVariables(TestAttachment.class);

      TestAttachment<String> attachment =
        new TestAttachment<String>("guru", null, "name");

      Variables variables = attachment.getVariables();
      assertEquals("variables", "value=var value", variables.toString());
      assertEquals("name", "name", attachment.getName());
      assertEquals("id", "name", attachment.getID());
      assertEquals("entry", "guru", attachment.getEntry());

      attachment = new TestAttachment<String>("guru", "tag", "name");

      variables = attachment.getVariables();
      assertEquals("variables", "tag:value=var value [tag tag]",
                   variables.toString());
      assertEquals("name", "name", attachment.getName());
      assertEquals("id", "tag:name", attachment.getID());
      assertEquals("entry", "guru", attachment.getEntry());
    }

    //......................................................................
  }

  //........................................................................
}
