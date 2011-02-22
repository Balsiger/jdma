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

package net.ixitxachitls.output;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import net.ixitxachitls.output.actions.Action;
import net.ixitxachitls.output.commands.BaseCommand;
import net.ixitxachitls.output.commands.Command;
import net.ixitxachitls.output.commands.Hrule;
import net.ixitxachitls.output.commands.Par;
import net.ixitxachitls.output.commands.Table;
import net.ixitxachitls.util.errors.BaseError;
import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base document for all output documents used.
 *
 * @file          Document.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 *
 */

//..........................................................................

//__________________________________________________________________________

@NotThreadSafe
public class Document
{
  //----------------------------------------------------------------- nested

  //----- Footnote ---------------------------------------------------------

  /**
   * This is a small class to store the information about a footnote entry.
   *
   */
  @NotThreadSafe
  protected static class Footnote
  {
    //------------------------------ Footnote ------------------------------

    /**
     * Create the footnote with all its information.
     *
     * @param       inMarker the marker to use for this entry
     * @param       inText   the text for this entry
     *
     */
    protected Footnote(@Nonnull String inMarker, @Nonnull Object inText)
    {
      m_marker = inMarker;
      m_text   = inText;
    }

    //......................................................................

    /** The marker to use for this footnote entry. */
    private @Nonnull String m_marker;

    /** The text of the footnote entry. */
    private @Nonnull Object m_text;

    //----------------------------- getMarker ------------------------------

    /**
     * Get the footnote marker.
     *
     * @return      the marker
     *
     */
    public @Nonnull String getMarker()
    {
      return m_marker;
    }

    //......................................................................
    //------------------------------ getText -------------------------------

    /**
     * Get the footnote text.
     *
     * @return      the text
     *
     */
    public @Nonnull Object getText()
    {
      return m_text;
    }

    //......................................................................
  }

  //........................................................................
  //----- SubDocument ------------------------------------------------------

  /**
   * This is a sub document to a general document allowing for an additional
   * buffer to allow the resulting document to be processed before inserting
   * it into the final (or super) document.
   *
   */
  public class SubDocument extends Document
  {
    //---------------------------- SubDocument -----------------------------

    /**
     * Create the sub document.
     *
     */
    protected SubDocument()
    {
      m_buffer = Document.this.getBuffer().newBuffer();
    }

    //......................................................................
    //---------------------------- SubDocument -----------------------------

    /**
     * Create the sub document.
     *
     * @param       inWidth the new width of the sub document
     *
     */
    protected SubDocument(int inWidth)
    {
      m_buffer = Document.this.getBuffer().newBuffer(inWidth);
    }

    //......................................................................

    /** The buffer for storage. */
    private @Nonnull Buffer m_buffer;

    //-------------------------- getKnownActions ---------------------------

    /**
     * Get all the actions known to this document type.
     *
     * @return      a hash map with all the known converters
     *
     */
    protected Map<String, Action> getKnownActions()
    {
      return Document.this.getKnownActions();
    }

  //........................................................................
    //----------------------------- getBuffer ------------------------------

    /**
     * Get the buffer associated with this document.
     *
     * @return      the buffer of this document
     *
     */
    protected Buffer getBuffer()
    {
      return m_buffer;
    }

    //......................................................................
    //----------------------------- getCounter -----------------------------

    /**
     * Get the current value of the counter.
     *
     * @return      the counter value
     *
     */
    public int getCounter()
    {
      return Document.this.getCounter();
    }

    //......................................................................

    //-------------------------------- isDM --------------------------------

    /**
     * Check if this is a DM document or not.
     *
     * @return      true if its a DM document, false if not
     *
     */
    public boolean isDM()
    {
      return Document.this.isDM();
    }

  //........................................................................

    //---------------------------- setAlignment ----------------------------

    /**
     * Set the alignment of the document buffer, if this is supported.
     *
     * @param       inAlignment the new alignment
     *
     */
    public void setAlignment(@Nonnull Buffer.Alignment inAlignment)
    {
      assert m_buffer instanceof WrapBuffer
        : "setAlignment() can only be called using a wrapping buffer";

      ((WrapBuffer)m_buffer).setAlignment(inAlignment);
    }

    //......................................................................
    //------------------------------ endLine -------------------------------

    /**
     * End the current line of necessary (ie not yet ended).
     *
     */
    public void endLine()
    {
      assert m_buffer instanceof WrapBuffer
        : "endLine() can only be called using a wrapping buffer";

      ((WrapBuffer)m_buffer).endLine();
    }

    //......................................................................
    //------------------------------ getLine -------------------------------

    /**
     * Get a single line from the internal buffer.
     *
     * @return      the line from the internal buffer
     *
     */
    public String getLine()
    {
      assert m_buffer instanceof WrapBuffer
        : "getLine() can only be called using a wrapping buffer";

      return ((WrapBuffer)m_buffer).getLine();
    }

    //......................................................................
    //----------------------------- getLength ------------------------------

    /**
     * Get the length of a line.
     *
     * @param       inText the text line for which to determine the length
     *
     * @return      the length of the given line
     *
     */
    public int getLength(@Nonnull String inText)
    {
      assert m_buffer instanceof WrapBuffer
        : "getLength() can only be called using a wrapping buffer";

      return ((WrapBuffer)m_buffer).getLength(inText);
    }

    //......................................................................

    //------------------------------ addError ------------------------------

    /**
     * Add the given error to the document.
     *
     * @param       inError the error to add
     *
     */
    public void addError(@Nonnull BaseError inError)
    {
      Document.this.addError(inError);
    }

    //......................................................................
    //----------------------------- addDocument ----------------------------

    /**
     * Add a complete document to this one. It will be saved along with the
     * parent document.
     *
     * @param       inDocument the document to add
     *
     */
    public void addDocument(@Nonnull Document inDocument)
    {
      Document.this.addDocument(inDocument);
    }

    //......................................................................
  }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------- Document -------------------------------

  /**
   * Basic constructor.
   *
   */
  public Document()
  {
    this(false);
  }

  //........................................................................
  //------------------------------- Document -------------------------------

  /**
   * Basic constructor.
   *
   * @param       inDM a flag denoting if this is a dm document or not
   *
   */
  public Document(boolean inDM)
  {
    m_dm = inDM;
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The buffer for storage. */
  protected @Nonnull Buffer m_buffer = new SimpleBuffer();

  /** The current counter value. */
  protected int m_counter = 0;

  /** The counter of the footnotes. */
  protected int m_footnoteCounter = 0;

  /** Flag if a DM document or not. */
  protected boolean m_dm = false;

  /** The known actions. */
  private static @Nonnull HashMap<String, Action> s_actions =
    new HashMap<String, Action>();

  /** The errors found. */
  protected @Nonnull Set<BaseError> m_errors = new LinkedHashSet<BaseError>();

  /** The footnote texts. */
  protected @Nonnull ArrayList<Footnote> m_footnotes =
    new ArrayList<Footnote>();

  /** The footer texts. */
  protected @Nonnull ArrayList<Object> m_footer = new ArrayList<Object>();

  /** The subdocument, if any. */
  private @Nullable List<Document> m_subDocuments = null;

  /** The documents attributes. */
  protected @Nonnull Map<String, String> m_attributes =
    new HashMap<String, String>();

  static
  {
    // init the known actions
    s_actions.put("command", new Action());
    s_actions.put("baseCommand", new Action());
  }

  //----- command definitions ----------------------------------------------

//    /** Command for emphasized printing. */
//   public static final String EMPH =
//     Config.get("resource:commands/emph", "emph");

//   /** Command for underlined. */
//   public static final String UNDERLINE =
//     Config.get("resource:commands/underline", "underline");

//   /** Command for italic. */
//   public static final String ITALIC =
//     Config.get("resource:commands/italic", "italic");

//   /** Command for sans serif. */
//   public static final String SANS_SERIF =
//     Config.get("resource:commands/sansserif", "sansserif");

//   /** Command for sans serif. */
//   public static final String SERIF =
//     Config.get("resource:commands/sansserif", "serif");

//   /** Command for footnote size. */
//   public static final String FOOTNOTE_SIZE =
//     Config.get("resource:commands/footnotesize", "footnotesize");

//   /** Command for script size. */
//   public static final String SCRIPT_SIZE =
//     Config.get("resource:commands/scriptsize", "scriptsize");

//   /** Command for small printing. */
//   public static final String SMALL =
//     Config.get("resource:commands/small", "small");

//   /** Command for normal sized printing. */
//   public static final String NORMAL_SIZE =
//     Config.get("resource:commands/normalsize", "normalsize");

//   /** Command for large printing. */
//   public static final String LARGE =
//     Config.get("resource:commands/large", "large");

//   /** Command for larger printing. */
//   public static final String LARGER =
//     Config.get("resource:commands/larger", "larger");

//   /** Command for largest printing. */
//   public static final String LARGEST =
//     Config.get("resource:commands/largest", "largest");

//   /** Command for huge printing. */
//   public static final String HUGE =
//     Config.get("resource:commands/huge", "huge");

//   /** Command for huger printing. */
//   public static final String HUGER =
//     Config.get("resource:commands/huger", "huger");

//   /** Command for formatting a list. */
//   public static final String LIST =
//     Config.get("resource:commands/list", "list");

//   /** Command for formatting an enumeration list. */
//   public static final String ENUMERATION =
//     Config.get("resource:commands/enumeration", "enumeration");

//   /** Command for formatting a title. */
//   public static final String TITLE =
//     Config.get("resource:commands/title", "title");

//   /** Command for formatting a title. */
//   public static final String SUBTITLE =
//     Config.get("resource:commands/subtitle", "subtitle");

//   /** Command for adding an icon. */
//   public static final String ICON =
//     Config.get("resource:commands/icon", "icon");

//   /** Command for adding an overlay icon. */
//   public static final String OVERLAY_ICON =
//     Config.get("resource:commands/icon.overlay", "overlayicon");

//   /** Command for adding a picture. */
//   public static final String PICTURE =
//     Config.get("resource:commands/picture", "picture");

//   /** Command for adding a label (a small icon or text in front of more
//    * text). */
//   public static final String LABEL =
//     Config.get("resource:commands/label", "label");

//   /** Command for formatting text for channels without picture support. */
//   public static final String NO_PICTURES =
//     Config.get("resource:commands/nopictures", "nopictures");

//   /** Command for adding a text block. */
//   public static final String TEXT_BLOCK =
//     Config.get("resource:commands/textblock", "textblock");

//   /** Command for adding hatted characters. */
//   public static final String HAT =
//     Config.get("resource:commands/hat", "hat");

//   /** Command for adding hatted characters. */
//   public static final String UMLAUT =
//     Config.get("resource:commands/umlaut", "umlaut");

//   /** Command for adding a character with an acute. */
//   public static final String GRAVE =
//     Config.get("resource:commands/grave", "grave");

//   /** Command for adding files found to the text. */
//   public static final String FILES =
//     Config.get("resource:commands/files", "files");

//   /** Command for adding files found to the text. */
//   public static final String OVERVIEW_FILES =
//     Config.get("resource:commands/overview.files", "overivewfiles");

//   /** Command for highlighting special texts. */
//   public static final String HIGHLIGHT =
//     Config.get("resource:commands/highlight", "highlight");

//   /** Command for setting an id at a special position in the text. */
//   public static final String SUB =
//     Config.get("resource:commands/sub", "sub");

//   /** Command for setting an id at a special position in the text. */
//   public static final String NAVIGATION =
//     Config.get("resource:commands/navigation", "navigation");

//   /** Command for setting an id at a special position in the text. */
//   public static final String MENU =
//     Config.get("resource:commands/menu", "menu");

//   // dma specifics

//   /** Command for setting a product name. */
//   public static final String PRODUCT =
//     Config.get("resource:commands/Product", "Product");

//   /** Command for setting a place name. */
//   public static final String PLACE =
//     Config.get("resource:commands/Place", "Place");

//   /** Command for setting an NPC name. */
//   public static final String NPC =
//     Config.get("resource:commands/NPC", "NPC");

//   /** Command for setting a monster name. */
//   public static final String MONSTER =
//     Config.get("resource:commands/Monster", "Monster");

//   /** Command for setting a group name. */
//   public static final String GROUP =
//     Config.get("resource:commands/Group", "Group");

//   /** Command for setting a group name. */
//   public static final String QUALITY =
//     Config.get("resource:commands/Quality", "Quality");

//   /** Command for setting a group name. */
//   public static final String ITEM =
//     Config.get("resource:commands/Item", "Item");

//   /** Command for setting a group name. */
//   public static final String GOD =
//     Config.get("resource:commands/God", "God");

//   /** Command for setting a group name. */
//   public static final String EVENT =
//     Config.get("resource:commands/Event", "Event");

//   /** Command for setting a group name. */
//   public static final String CLASS =
//     Config.get("resource:commands/Class", "Class");

//   /** Command for setting a group name. */
//   public static final String SPELL =
//     Config.get("resource:commands/Spell", "Spell");

//   /** Command for setting a group name. */
//   public static final String SPELLREF =
//     Config.get("resource:commands/SpellRef", "SpellRef");

//   /** Command for setting a group name. */
//   public static final String DOMAIN =
//     Config.get("resource:commands/Domain", "Domain");

//   /** Command for setting a group name. */
//   public static final String FEAT =
//     Config.get("resource:commands/Feat", "Feat");

//   /** Command for setting a group name. */
//   public static final String SKILL =
//     Config.get("resource:commands/Skill", "Skill");

//   /** Command for setting a group name. */
//   public static final String NEWPAGE =
//     Config.get("resource:commands/newpage", "newpage");

//   /** Command for indentation. */
//   public static final String INDENT =
//     Config.get("resource:commands/indent", "indent");

//   /** Command for indentation. */
//   public static final String COLUMNS =
//     Config.get("resource:commands/columns", "columns");

//   /** Command for hide contents. */
//   public static final String HIDE =
//     Config.get("resource:commands/hide", "hide");

//   /** Command for word uppercase text. */
//   public static final String WORD_UPPERCASE =
//     Config.get("resource:commands/worduppercase", "worduppercase");

//   /** Command for skipping text. */
//   public static final String SKIP =
//     Config.get("resource:commands/SKIP", "SKIP");

//   /** Command for footer text. */
//   public static final String FOOTER =
//     Config.get("resource:commands/footer", "footer");

//   /** Command for entry pictures text. */
//   public static final String ENTRY_PIC =
//     Config.get("resource:commands/entry.pic", "entrypic");

//   /** Command for printing a less sign. */
//   public static final String LESS =
//     Config.get("resource:commands/key.less", "less");

//   /** Command for printing a less sign. */
//   public static final String LESS_OR_EQUAL =
//     Config.get("resource:commands/key.less.equal", "lessequal");

//   /** Command for printing a less sign. */
//   public static final String GREATER =
//     Config.get("resource:commands/key.greater", "greater");

//   /** Command for printing a less sign. */
//   public static final String GREATER_OR_EQUAL =
//     Config.get("resource:commands/key.greater.equal", "greaterequal");

//   /** Command for counting. */
//   public static final String COUNT =
//     Config.get("resource:commands/count", "count");

//   /** Command for verbatim printing. */
//   public static final String VERBATIM =
//     Config.get("resource:commands/verbatim", "verbatim");

//   /** Command for a page. */
//   public static final String PAGE =
//     Config.get("resource:commands/page", "page");

//   /** Command for a table of contents entry. */
//   public static final String TOC_ENTRY =
//     Config.get("resource:commands/toc", "toc");

//   /** Command for a table of contents entry. */
//   public static final String GROUPED =
//     Config.get("resource:commands/grouped", "grouped");

//   /** Command for setting a value. */
//   public static final String VALUE =
//     Config.get("resource:commands/value", "Value");

//   /** Command for a checked value. */
//   public static final String CHECKED =
//     Config.get("resource:commands/checked", "checked");

//   /** Command for an editable value. */
//   public static final String EDITABLE =
//     Config.get("resource:commands/editable", "editable");

//   /** Command for a button. */
//   public static final String BUTTON =
//     Config.get("resource:commands/button", "button");

//   /** Command for bold printing. */
//   public static final String SCRIPT =
//     Config.get("resource:commands/script", "script");

//   /** Command for temporary groups. */
//   public static final String TEMPGROUP = "TempGroup";

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------- getKnownActions ----------------------------

  /**
   * Get all the actions known to this document type.
   *
   * @return      a hash map with all the known converters
   *
   */
  protected @Nonnull Map<String, Action> getKnownActions()
  {
    return s_actions;
  }

  //........................................................................
  //------------------------------ getBuffer -------------------------------

  /**
   * Get the buffer associated with this document.
   *
   * @return      the buffer of this document
   *
   */
   protected @Nonnull Buffer getBuffer()
   {
     return m_buffer;
   }

  //........................................................................
  //------------------------------- getWidth -------------------------------

  /**
   * Get the width of the document, or 0 if there is no limit.
   *
   * @return      the width in characters
   *
   */
  public int getWidth()
  {
    Buffer buffer = getBuffer();

    if(buffer instanceof WrapBuffer)
      return ((WrapBuffer)buffer).getWidth();
    else
      return 0;
  }

  //........................................................................
  //------------------------------ getCounter ------------------------------

  /**
   * Get the current value of the counter.
   *
   * @return      the counter value
   *
   */
  public int getCounter()
  {
    return m_counter++;
  }

  //........................................................................
  //-------------------------- getFootnoteCounter --------------------------

  /**
   * Get the current value of the footnote counter.
   *
   * @return      the counter value
   *
   */
  public int getFootnoteCounter()
  {
    return ++m_footnoteCounter;
  }

  //........................................................................
  //----------------------------- setAttribute -----------------------------

  /**
   * Get an attribute of the document.
   *
   * @param       inName  the name of the attribute
   *
   * @return      the value of the attribute, if any
   *
   */
  public @Nullable String getAttribute(@Nonnull String inName)
  {
    return m_attributes.get(inName);
  }

  //........................................................................

  //--------------------------------- isDM ---------------------------------

  /**
   * Check if this is a DM document or not.
   *
   * @return      true if its a DM document, false if not
   *
   */
  public boolean isDM()
  {
    return m_dm;
  }

  //........................................................................

  //-------------------------- createSubDocument ---------------------------

  /**
   * Create a sub document to the current one.
   *
   * @return      the desired document
   *
   */
  public @Nonnull SubDocument createSubDocument()
  {
    return new SubDocument();
  }

  //........................................................................
  //-------------------------- createSubDocument ---------------------------

  /**
   * Create a sub document to the current one.
   *
   * @param       inWidth the new line width of the created document
   *
   * @return      the desired document
   *
   */
  public @Nonnull SubDocument createSubDocument(int inWidth)
  {
    return new SubDocument(inWidth);
  }

  //........................................................................

  //------------------------------- convert --------------------------------

  /**
   * Convert the given command into its action.
   *
   * @param       inCommand the command to convert
   *
   * @return      the action corresponding to the command or null if the
   *              command should be ignored
   *
   */
  public @Nullable Action convert(@Nonnull Command inCommand)
  {
    if(inCommand instanceof BaseCommand)
    {
      String name = ((BaseCommand)inCommand).getName();

      // determine the action for the command
      Map<String, Action> actions = getKnownActions();

      Action action = actions.get(name);

      if(action != null)
        return action;

      // an empty (i.e. ignored) command ?
      if(actions.containsKey(name))
        return null;

      if(!"command".equals(name))
      {
        addError(new BaseError("action.no.command", name));
        Log.error("Could not get action for '" + name + "'");
      }
    }

    return new Action();
  }

  //........................................................................
  //------------------------------- convert --------------------------------

  /**
   * Convert the given Object to a String.
   *
   * @param       inCommand the command to convert
   *
   * @return      the converted result
   *
   */
  public String convert(@Nonnull Object inCommand)
  {
    if(!(inCommand instanceof Command))
      inCommand = new BaseCommand(inCommand.toString());

    Document sub = createSubDocument();

    sub.add(inCommand);

    return sub.toString();
  }

  //........................................................................

  //--------------------------------- save ---------------------------------

  /**
   * Save the contents of the document to a file with the given name.
   *
   * @param       inFileName the name of the file to store in
   *
   * @return      true if stored, false if not
   *
   */
  public boolean save(@Nonnull String inFileName)
  {
    FileWriter file = null;
    try
    {
      file = new FileWriter(inFileName);

      file.write(toString());
      file.close();

      Log.info("wrote file '" + inFileName + "'");

      if(m_subDocuments != null)
      {
        int count = 1;

        for(Document doc : m_subDocuments)
          doc.save(inFileName + "-" + count++);
      }

      return true;
    }
    catch(java.io.IOException e)
    {
      Log.warning("cannot write to '" + inFileName + "': " + e);

      return false;
    }
    finally
    {
      try
      {
        if(file != null)
          file.close();
      }
      catch(java.io.IOException e)
      {
        Log.warning("could not close file '" + inFileName + "': " + e);

        return false;
      }
    }
  }

  //........................................................................

  //------------------------------ getErrors -------------------------------

  /**
   * Get all the errors encountered in the document.
   *
   * @return      the list of all errors not found
   *
   */
  public Set<BaseError> getErrors()
  {
    return Collections.unmodifiableSet(m_errors);
  }

  //........................................................................
  //------------------------------- getFooter ------------------------------

  /**
   * Return the contents of the document as a single String.
   *
   * @return      The complete contents of the document.
   *
   */
  public @Nonnull String getFooter()
  {
    Document sub = createSubDocument();

    for(Iterator i = m_footer.iterator(); i.hasNext(); )
      sub.add(i.next());

    return sub.toString();
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- add ----------------------------------

  /**
   * Add the given object to the current position of the document.
   *
   * @param       inObject the object to add
   *
   */
  public void add(@Nonnull Object inObject)
  {
    if(inObject instanceof BaseCommand)
      add((BaseCommand)inObject);
    else if(inObject instanceof Command)
      add((Command)inObject);
    else
      add(inObject.toString());
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the given String to the current position of the document.
   *
   * @param       inText the text to add
   *
   */
  public void add(@Nonnull String inText)
  {
    getBuffer().append(inText);
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the given command to the current position of the document.
   *
   * @param       inCommand the command to add
   *
   */
  public void add(@Nonnull Command inCommand)
  {
    Action action = convert(inCommand);

    // no action, do nothing
    if(action == null)
      return;

    action.execute(this, null, inCommand.getArguments());
  }

  //........................................................................
  //--------------------------------- add ----------------------------------

  /**
   * Add the given command to the current position of the document.
   *
   * @param       inCommand the command to add
   *
   */
  public void add(@Nonnull BaseCommand inCommand)
  {
    Action action = convert(inCommand);

    // no action, do nothing
    if(action == null)
      return;

    action.execute(this, inCommand.getOptionals(), inCommand.getArguments());
  }

  //........................................................................
  //----------------------------- addFootnote ------------------------------

  /**
   * Add a footnote text to the document.
   *
   * @param       inMarker the marker of the footnote
   * @param       inObject the object to add
   *
   */
  public void addFootnote(@Nonnull String inMarker, @Nonnull Object inObject)
  {
    m_footnotes.add(new Footnote(inMarker, inObject));
  }

  //........................................................................
  //------------------------------ addFooter -------------------------------

  /**
   * Add a footer text to the document.
   *
   * @param       inObject the object to add as footer
   *
   */
  public void addFooter(@Nonnull Object inObject)
  {
    m_footer.add(inObject);
  }

  //........................................................................
  //----------------------------- addDocument ------------------------------

  /**
   * Add a complete document to this one. It will be saved along with the
   * parent document.
   *
   * @param       inDocument the document to add
   *
   */
  public void addDocument(@Nonnull Document inDocument)
  {
    if(m_subDocuments == null)
      m_subDocuments = new ArrayList<Document>();

    m_subDocuments.add(inDocument);
  }

  //........................................................................

  //----------------------------- setAlignment -----------------------------

  /**
   * Set the alignment of the document buffer, if this is supported.
   *
   * @param       inAlignment the new alignment
   *
   */
  public void setAlignment(@Nonnull Buffer.Alignment inAlignment)
  {
    // nothing done
  }

  //........................................................................
  //------------------------------- endLine --------------------------------

  /**
   * End the current line, if it is not yet ended.
   *
   */
  public void endLine()
  {
    getBuffer().endLine();
  }

  //........................................................................
  //----------------------------- setAttribute -----------------------------

  /**
   * Set an attribute of the document.
   *
   * @param       inName  the name of the attribute
   * @param       inValue the value of the attribute
   *
   */
  public void setAttribute(@Nonnull String inName, @Nonnull String inValue)
  {
    m_attributes.put(inName, inValue);
  }

  //........................................................................

  //------------------------------- addError -------------------------------

  /**
   * Add the given error to the document.
   *
   * @param       inError the error to add (null errors are ignored)
   *
   */
  public void addError(@Nonnull BaseError inError)
  {
    m_errors.add(inError);
  }

  //........................................................................
  //------------------------------- addErrors ------------------------------

  /**
   * Add the given errors to the document.
   *
   * @param       inErrors the errosr to add
   *
   */
  public void addErrors(@Nonnull List<BaseError> inErrors)
  {
    m_errors.addAll(inErrors);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- toString -------------------------------

  /**
   * Return the contents of the document as a single String.
   *
   * @return      The complete contents of the document.
   *
   */
  public @Nonnull String toString()
  {
    String page = getBuffer().getContents();

    // handle footnotes
    if(m_footnotes.size() != 0)
    {
      Document sub = createSubDocument();

      Object []notes = new Object[m_footnotes.size() * 2];

      int j = 0;
      for(Iterator i = m_footnotes.iterator(); i.hasNext(); )
      {
        Footnote note = (Footnote)i.next();

        notes[j++] = note.getMarker() + ")";
        notes[j++] = note.getText();
      }

      sub.add(new Par());
      sub.add(new Hrule("black", 30));
      sub.add(new Table("footnote", "f4:L;100:L", notes));

      page += sub;
    }

    return page;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    // TODO: add more tests

    /** A test command for testing with base command. */
    public static class TestCommand extends BaseCommand
    {
      /**
       * Create the test command.
       *
       * @param inArguments the arguments to use.
       *
       */
      TestCommand(Object ... inArguments)
      {
        super("command", 0, 3, inArguments);
      }
    }

    //----- command --------------------------------------------------------

    /** Test the basic command. */
    @org.junit.Test
    public void command()
    {
      Document doc = new Document();

      assertEquals("empty", "", doc.toString());

      doc.add(new Command(new Command("just "), new Command("some "),
                          new Command("test")));

      assertEquals("command", "just some test", doc.toString());
      assertEquals("width", 0, doc.getWidth());
      assertFalse("dm", doc.isDM());
    }

    //......................................................................
    //----- base command ---------------------------------------------------

    /** The base command Test. */
    @org.junit.Test
    public void baseCommand()
    {
      Document doc = new Document();

      doc.add(new TestCommand("just ", "some ", "test"));
      assertEquals("command", "just some test", doc.toString());
    }

    //......................................................................
    //----- save -----------------------------------------------------------

    /** Test saving of a document.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void save() throws Exception
    {
      Document doc = new Document();

      doc.setAlignment(Buffer.Alignment.left);
      doc.add("This is just some test file");
      doc.endLine();

      // save to a temp file
      java.io.File tmp = java.io.File.createTempFile("test", "file");

      assertTrue("save", doc.save(tmp.getPath()));

      java.io.BufferedReader reader = new java.io.BufferedReader
        (new java.io.InputStreamReader(new java.io.FileInputStream(tmp)));

      assertEquals("text", "This is just some test file", reader.readLine());
      assertNull("end", reader.readLine());
      assertTrue("delete", tmp.delete());
      reader.close();
    }

    //......................................................................
    //----- footnote -------------------------------------------------------

    /** The footnote Test. */
    @org.junit.Test
    public void footnote()
    {
      Document doc = new Document();
      doc.addFootnote("m1", "text1");
      doc.addFootnote("m2", "text2");

      assertEquals("footnote", "30blackfootnotef4:L;100:Lm1)text1m2)text2",
                   doc.toString());
      assertEquals("errors", 3, doc.getErrors().size());

      m_logger.addExpected("ERROR: Could not get action for 'par'");
      m_logger.addExpected("ERROR: Could not get action for 'hrule'");
      m_logger.addExpected("ERROR: Could not get action for 'table'");
    }

    //......................................................................
    //----- counters -------------------------------------------------------

    /** The counters Test. */
    @org.junit.Test
    public void counters()
    {
      Document doc = new Document();

      assertEquals("footnote 1", 1, doc.getFootnoteCounter());
      assertEquals("footnote 2", 2, doc.getFootnoteCounter());
      assertEquals("footnote 3", 3, doc.getFootnoteCounter());

      assertEquals("counter", 0, doc.getCounter());
      assertEquals("counter", 1, doc.getCounter());
      assertEquals("counter", 2, doc.getCounter());
      assertEquals("counter", 3, doc.getCounter());
    }

    //......................................................................
    //----- attributes -----------------------------------------------------

    /** The attributes Test. */
    @org.junit.Test
    public void attributes()
    {
      Document doc = new Document();

      doc.setAttribute("attr1", "value1");
      doc.setAttribute("attr2", "value2");

      assertEquals("attr 1", "value1", doc.getAttribute("attr1"));
      assertEquals("attr 2", "value2", doc.getAttribute("attr2"));
      assertEquals("attr 3", null, doc.getAttribute("attr3"));
    }

    //......................................................................
    //----- footer ---------------------------------------------------------

    /** The footer Test. */
    @org.junit.Test
    public void footer()
    {
      Document doc = new Document();

      doc.addFooter("footer1");
      doc.addFooter("footer2");
      doc.addFooter("footer3");

      assertEquals("footer", "footer1footer2footer3", doc.getFooter());
    }

    //......................................................................
    //----- document -------------------------------------------------------

    /** Test saving of a document.
     *
     * @throws Exception should not happen
     */
    @org.junit.Test
    public void document() throws Exception
    {
      Document doc = new Document();
      doc.addDocument(new Document());

      // save to a temp file
      java.io.File tmp = java.io.File.createTempFile("test", "file");

      assertTrue("save", doc.save(tmp.getPath()));

      assertTrue("delete", tmp.delete());
      assertTrue("delete", new java.io.File(tmp.getPath() + "-1").delete());
    }

    //......................................................................
  }

  //........................................................................
}
