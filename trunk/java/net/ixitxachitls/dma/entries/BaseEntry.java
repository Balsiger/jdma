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

package net.ixitxachitls.dma.entries;

//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;

import javax.annotation.Nonnull;

// import net.ixitxachitls.dma.Global;
// import net.ixitxachitls.dma.entries.attachments.AbstractAttachment;
// import net.ixitxachitls.dma.entries.indexes.ExtractorIndex;
// import net.ixitxachitls.dma.entries.indexes.Index;
// import net.ixitxachitls.dma.entries.indexes.KeyIndex;
// import net.ixitxachitls.dma.values.Modifiable;
import net.ixitxachitls.dma.values.FormattedText;
import net.ixitxachitls.dma.values.Multiple;
import net.ixitxachitls.dma.values.Name;
import net.ixitxachitls.dma.values.Range;
//import net.ixitxachitls.dma.values.Selection;
import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.ValueList;
import net.ixitxachitls.dma.values.formatters.DelimFormatter;
import net.ixitxachitls.dma.values.formatters.Formatter;
import net.ixitxachitls.dma.values.formatters.LinkFormatter;
import net.ixitxachitls.dma.values.formatters.ListFormatter;
import net.ixitxachitls.input.ParseReader;
//import net.ixitxachitls.output.commands.Color;
//import net.ixitxachitls.output.commands.Command;
// import net.ixitxachitls.output.commands.Divider;
// import net.ixitxachitls.output.commands.Editable;
// import net.ixitxachitls.output.commands.Hrule;
// import net.ixitxachitls.output.commands.Icon;
// import net.ixitxachitls.output.commands.Linebreak;
// import net.ixitxachitls.output.commands.Link;
// import net.ixitxachitls.output.commands.Script;
// import net.ixitxachitls.output.commands.Super;
// import net.ixitxachitls.output.commands.Textblock;
//import net.ixitxachitls.util.MultiIterator;
//import net.ixitxachitls.util.Strings;
//import net.ixitxachitls.util.Encodings;
//import net.ixitxachitls.util.configuration.Config;
//import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the base class for all base entries of DMA.
 *
 * All the values stored in this entry (and in all derivations) need to
 * be non-null!
 *
 * @file          BaseEntry.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public class BaseEntry extends AbstractEntry
{
  //----------------------------------------------------------------- nested

  //----- worlds -----------------------------------------------------------

  /** The game worlds (this is configurable and thus not an enum). */
//   protected static final String []WORLDS =
//     Config.get("resource:" + CURRENT + "/game/worlds",
//                new String []
//     {
//       // these are unsorted because they are the most important
//       "Generic",
//       "Eberron",
//       "Forgotten Realms",

//       // the rest is sorted
//       "Al-Qadim",
//       "Birthright",
//       "Buck Rogers",
//       "Call of Cthulhu",
//       "Confrontation",
//       "Dark Sun",
//       "Dark.Matter",
//       "Dawn of the Emperors",
//       "Diablo II",
//       "Dominaria",
//       "Dragonlance",
//       "Episode I",
//       "Gamma World",
//       "Greyhawk",
//       "Hollow World",
//       "Lankhmar",
//       "Marvel Super Heroes",
//       "Middle Earth",
//       "Mystara",
//       "None",
//       "Odyssey",
//       "Planescape",
//       "Ptolus",
//       "Ravenloft",
//       "Real",
//       "Red Steel",
//       "Rokugan",
//       "S.P.I.",
//       "Savage Coast",
//       "Spelljammer",
//       "Star Wars",
//       "Star*Drive",
//       "Urza's Destiny",
//       "Wheel of Time",
//     });

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------ BaseEntry -------------------------------

  /**
   * The default constructor, with undefined values.
   *
   */
  protected BaseEntry()
  {
    super(TYPE);
  }

  //........................................................................
  //------------------------------ BaseEntry -------------------------------

  /**
   * The default constructor, with undefined values.
   *
   * @param   inType   the type of the entry
   *
   */
  protected BaseEntry(@Nonnull AbstractType<? extends BaseEntry> inType)
  {
    super(inType);
  }

  //........................................................................
  //------------------------------ BaseEntry -------------------------------

  /**
   * The complete constructor.
   *
   * @param       inName     the name of the entry
   *
   */
  public BaseEntry(@Nonnull String inName)
  {
    this(inName, TYPE);
  }

  //........................................................................
  //------------------------------ BaseEntry -------------------------------

  /**
   * The constructor for derivations, with a type.
   *
   * @param       inName     the name of the entry
   * @param       inType     the type of the entry
   *
   */
  protected BaseEntry(@Nonnull String inName,
                      @Nonnull AbstractType<? extends BaseEntry> inType)
  {
    super(inName, inType);
  }

  //........................................................................
  //------------------------------ BaseEntry -------------------------------

  /**
   * The constructor for derivations, with a type.
   *
   * @param       inName     the name of the entry
   * @param       inType     the type of the entry
   *
   * @undefined   never
   *
   */
//   protected BaseEntry(@Nonnull String inName, @Nonnull Type inType,
//                       @Nonnull BaseEntry ... inBases)
//   {
//     super(inName, inType, inBases);
//   }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The type of this entry. */
  public static final BaseType<BaseEntry> TYPE =
    new BaseType<BaseEntry>(BaseEntry.class, "Base Entries");

  /** The basic formatter for base entries. */
//   public static final Index.Formatter<AbstractEntry> FORMATTER =
//     new Index.Formatter<AbstractEntry>()
//     {
//       public List<Object> format(String inKey, AbstractEntry inEntry)
//       {
//         List<Object> list = Index.FORMATTER.format(inKey, inEntry);

//         if(!(inEntry instanceof BaseEntry))
//           return list;

//         BaseEntry entry = (BaseEntry)inEntry;

//         list.add(entry.m_world.format(false));

//         return list;
//       }
//     };

//   /** The basic format for base products. */
//   public static final String FORMAT =
//     Index.FORMAT + ";1:L(world)[World]";

  //----- world ------------------------------------------------------------

//   /** The formatter for worlds. */
//   private static final ValueFormatter<Selection> s_worldFormatter =
//     new LinkFormatter<Selection>("/index/worlds/");

  /** The world. */
  // TODO: move this to a BaseGameEntry
//   @Key("worlds")
//   protected ValueList<Selection> m_worlds =
//     new ValueList<Selection>(new Selection(WORLDS)
//                              .withFormatter(s_worldFormatter));

//   static
//   {
//     s_indexes.add(new KeyIndex<KeyIndex>("General", "Worlds", "worlds",
//                                          "world", true, false));
//   }

  //........................................................................
  //----- references -------------------------------------------------------

  /** The page formatter. */
  protected static final Formatter<ValueList<Range>> s_pagesFormatter =
    new DelimFormatter<ValueList<Range>>("p. ", "");

  // TODO: was modifiable; check if it needs to be
  /** The references for this entry. */
  @Key(value = "references", dm = true)
  protected ValueList<Multiple> m_references =
    new ValueList<Multiple>
    (new Multiple
     (new Multiple.Element(new Text()
                            // we use an instance specific formatter because
                            // we need the campaign
//                             .withFormatter(new ValueFormatter<Text>()
//       {
//         public @Nonnull Command format(@Nonnull Text inReference)
//         {
//           String name  = inReference.get();
//           String title = resolveReference(name);

//           Command result = new Link(title, TYPE.getLink() + name);

//           if(name == title)
//             return new Color("error", result);

//           return result;
//         }
//       })
                           .withEditType("dynselection(references)"), false),
      new Multiple.Element
      (new ValueList<Range>(new Range(0, Integer.MAX_VALUE)
                            .withEditType("number")
                            .withEditName("page"), "/")
       .withFormatter(s_pagesFormatter), true)));

  static
  {
//     s_indexes.add(new ExtractorIndex<ExtractorIndex>
//                   ("General", "References", "references",
//                    new ExtractorIndex.Extractor()
//                    {
//                      public Object []get(AbstractEntry inEntry)
//                      {
//                        if(!(inEntry instanceof BaseEntry))
//                          return null;

//                        return ((BaseEntry)inEntry).getReferences();
//                      }
//                    }, true, false));
  }

  //........................................................................
  //----- description ------------------------------------------------------

  /** The descriptive text for this entry. */
  @Key("description")
  protected FormattedText m_description = new FormattedText();

  //........................................................................
  //----- short description ------------------------------------------------

  /** The short description text for this entry. */
  @Key("short description")
  protected Text m_short = new Text().withEditName("short description");

  //........................................................................
  //----- synonyms ---------------------------------------------------------

  /** The formatter for a synonym list. */
  protected static final Formatter<ValueList<Text>> s_synonymListFormatter =
    new ListFormatter<ValueList<Text>>("; ");

  /** The synonyms for this entry. */
  @Key(value = "synonyms", dm = true)
  protected ValueList<Text> m_synonyms =
    new ValueList<Text>(new Text()).withFormatter(s_synonymListFormatter);

  //........................................................................
  //----- categories -------------------------------------------------------

  /** The formatter for a category (to prevent many objects being created). */
  protected static final Formatter<Name> s_categoryFormatter =
    new LinkFormatter<Name>("/index/categories/");

  /** The categories. */
  @Key(value = "categories", dm = true)
  protected ValueList<Name> m_categories =
    new ValueList<Name>(new Name().withFormatter(s_categoryFormatter));

//   static
//   {
//     s_indexes.add(new ExtractorIndex<ExtractorIndex>
//                   ("General", "Categories", "categories",
//                    new ExtractorIndex.Extractor()
//                    {
//                      public Object []get(AbstractEntry inEntry)
//                      {
//                        if(inEntry instanceof BaseProduct)
//                          return new Object []
//                          { ((BaseProduct)inEntry).getProductType(), };

//                        if(inEntry instanceof BaseEntry)
//                          return ((BaseEntry)inEntry).getCategories();

//                        if(inEntry instanceof Entry)
//                          return ((Entry)inEntry).getCategories().toArray();

//                        return new Object[0];
//                      }
//                    }, true, false));
//   }

  //........................................................................

  static
  {
    extractVariables(BaseEntry.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- isBase --------------------------------

  /**
   * Check if the current entry represents a base entry or not.
   *
   * @return      true if this is a base entry, false else
   *
   */
  public boolean isBase()
  {
    return true;
  }

  //........................................................................
  //------------------------------- matches --------------------------------

  /**
   * Determine if the current entry matches to the given base entry.
   *
   * @param       inBase the base entry to match to
   *
   * @return      true if they match, i.e. if the given base entry could
   *              be the base to the current object
   *
   */
//   public boolean matches(@Nonnull BaseEntry inBase)
//   {
//     // only matches if the types match
//     if(getType() != inBase.getType())
//       return false;

//     // check the name
//     String name = getName();

//     if(name != null)
//     {
//       // match the name
//       name = name.replaceAll("\\*\\*", ".*");

//       if(!inBase.getName().matches(name))
//         return false;
//     }

//     // now we do the real check, i.e. really compare values
//     Values values = getValues();
//     if(values != null)
//       for(Variable variable : values)
//       {
//         // if no matcher is present, we ignore this value
//         if(!variable.hasMatcher(this))
//           continue;

//         // we don't have a value, see if the base entry has one
//         Variable base = inBase.getValues().getValue(variable.getKey());

//         // no base, we ignore the value
//         if(base == null)
//         {
//           Log.warning("Cannot find matcher value for '"
//                       + variable.getKey() + "' in '" + inBase.getType()
//                       + "'");

//           continue;
//         }

//         if(!variable.check(this, base.get(inBase)))
//           return false;
//       }

//     return true;
//   }

  //........................................................................
  //----------------------------- hasCategory ------------------------------

  /**
   * Check if the base entry has a named category.
   *
   * @param       inCategory the category to look for
   *
   * @return      true if the category is present, false if not
   *
   */
  public boolean hasCategory(@Nonnull String inCategory)
  {
    for(Name text : m_categories)
      if(text.get().equalsIgnoreCase(inCategory))
        return true;

    return false;
  }

  //........................................................................

  //------------------------------- getWorld -------------------------------

  /**
   * Get the world the product is for.
   *
   * @return      the selection containing the selected world
   *
   */
//   public String getWorld()
//   {
//     return m_world.toString();
//   }

  //........................................................................
  //------------------------- getShortDescription --------------------------

  /**
   * Get the short description of the base entry.
   *
   * @return      the selection containing the selected world
   *
   */
  public @Nonnull String getShortDescription()
  {
    return m_short.get();
  }

  //........................................................................
  //------------------------- getRefSummaryCommand -------------------------

  /**
   * Get the command for printing a reference summary.
   *
   * @param       inParameters optional parametrs to further specifiy the entry
   *
   * @return      the command for printing a reference
   *
   */
//   public Command getRefSummaryCommand(String ... inParameters)
//   {
//     return new Command(new Object []
//       {
//         new Super(getReference(false)),
//         " (",
//         getSummary(inParameters),
//         ")",
//       });
//   }

  //........................................................................

  //------------------------------ getSummary ------------------------------

  /**
   * Get a summary for the entry.
   *
   * @param       inParameters optional parametrs to further specifiy the entry
   *
   * @return      the string with the summary
   *
   */
//   public String getSummary(String ... inParameters)
//   {
//     return getShortDescription();
//   }

  //........................................................................
  //----------------------------- getReference -----------------------------

  /**
   * Get the reference(s) for this entry.
   *
   * @param       inFull if true, get all references, if false only the first
   *                     one
   *
   * @return      a string with the reference
   *
   */
//   @SuppressWarnings("unchecked")
//   public String getReference(boolean inFull)
//   {
//     List <String> references = new ArrayList<String>();

//     for(Multiple reference : m_references.getHigh())
//     {
//       String text = resolveReference(((Text)reference.get(0).get()).get());
//       ValueList<Range> pages = (ValueList<Range>)reference.get(1).get();

//       if(pages.isDefined())
//         text += " p. " + pages;

//       references.add(text);

//       if(!inFull)
//         break;
//     }

//     return Strings.toString(references, ", ", "");
//   }

  //........................................................................
  //--------------------------- resolveReference ---------------------------

  /**
   * Resolve the given reference into something readable.
   *
   * @param       inName the name of the base product reference
   *
   * @return      the title of the base product, if found
   *
   */
//   protected String resolveReference(@Nonnull String inName)
//   {
//     BaseEntry base =
//       BaseCampaign.GLOBAL.getBaseEntry(inName, BaseProduct.TYPE);

//     if(base == null)
//       return inName;

//     return base.getRefName();
//   }

  //........................................................................

  //---------------------------- getDescription ----------------------------

  /**
   * Get the description of the product.
   *
   * @return      the description
   *
   */
//   public String getDescription()
//   {
//     return m_description.get();
//   }

  //........................................................................
  //------------------------------ getSynonyms -----------------------------

  /**
   * Get the synonyms of the entry.
   *
   * @return      the synonyms
   *
   */
//   public String []getSynonyms()
//   {
//     ArrayList<String> result = new ArrayList<String>();

//     for(Iterator<Text> i = m_synonyms.getHigh().iterator(); i.hasNext(); )
//       result.add(i.next().get());

//     return result.toArray(new String[0]);
//   }

  //........................................................................
  //----------------------------- getReferences ----------------------------

  /**
   * Get the references of the entry.
   *
   * @return      the references
   *
   */
//   public String []getReferences()
//   {
//     ArrayList<String> result = new ArrayList<String>();

//     for(Multiple value : m_references.getHigh())
//       result.add(((Text)value.get(0).get()).get());

//     return result.toArray(new String[0]);
//   }

  //........................................................................
  //----------------------------- getCategories ----------------------------

  /**
   * Get the categories of the entry.
   *
   * @return      the categories
   *
   */
//   public String []getCategories()
//   {
//     ArrayList<String> result = new ArrayList<String>();

//     for(Text text : m_categories)
//       result.add(Encodings.toWordUpperCase(text.get()));

//     // add the attachments, if any
//     for(Iterator<AbstractAttachment> i = getAttachments(); i.hasNext(); )
//       result.add(Encodings.toWordUpperCase(i.next().getName()));

//     // product specification
//     if(this instanceof BaseProduct)
//       result.add(((BaseProduct)this).getProductType().toString());

//     return result.toArray(new String[0]);
//   }

  //........................................................................
  //---------------------------- getProbability ----------------------------

  /**
   * Get the probability of the base entry. This method is primarily used in
   * derivations to support probability values in that case.
   *
   * @return      always 1 (will be changed in derivation)
   *
   */
//   public int getProbability()
//   {
//     return 1;
//   }

  //........................................................................
  //------------------------ getReqEntryAttachments ------------------------

  /**
   * Get a list of all attachments required by this entry (and all its own
   * attachments).
   *
   * @return      a list with all attachments or null if no attachments
   *              required
   *
   * @undefined   can return null
   *
   */
//   protected Iterator<String> getReqEntryAttachments()
//   {
//     ArrayList<Iterator<String>> attachments =
//       new ArrayList<Iterator<String>>();

//     for(Iterator<AbstractAttachment> i = m_attachments.values().iterator();
//         i.hasNext(); )
//     {
//       Iterator<String> auto =
//         AbstractAttachment.getAutoAttachments(i.next().getClass());

//       if(auto != null)
//         attachments.add(auto);
//     }

//     return
//       new MultiIterator<String>(attachments.toArray(new Iterator [0]));
//   }

  //........................................................................
  //------------------------------- getBased -------------------------------

  /**
   * Get the base entry this one is based upon, if any.
   *
   * @return      the base entry or null
   *
   */
//   @MayReturnNull
//   public BaseEntry getBased()
//   {
//     return m_base;
//   }

  //........................................................................

  //----------------------------- printCommand -----------------------------

  /**
   * Create the print command for printing this entry. This is an internal
   * method for more efficient creation. From the outside, use
   * <CODE>getPrintCommand()</CODE> instead.
   *
   * @param       inDM       true if set for DM, false for player
   * @param       inEditable true if values are editable, false if not
   *
   * @return      an object with the individual parts to set on the page
   *
   * @undefined   never
   *
   */
//   public PrintCommand printCommand(boolean inDM, boolean inEditable)
//   {
//     PrintCommand commands = super.printCommand(inDM, inEditable);

//     // values
//     commands.addValue(m_categories, "categories", inEditable);
//     commands.addValue(m_synonyms, "synonyms", inEditable);
//     commands.addValue(m_references, "references", inEditable);

//     // now the attachments
//     for(Iterator<AbstractAttachment> i = getAttachments(); i.hasNext(); )
//       i.next().addPrintCommands(commands, inDM, inEditable);

//     // TODO: check that this is actually printed elsewhere
// //     if(m_errors != null)
// //     {
// //  commands.values.add(new Bold(new Color("error", new Link("/index/errors",
// //                                                       "Errors:"))));

// //       ArrayList<Object>errors = new ArrayList<Object>();

// //       for(Iterator<BaseError> i = m_errors.iterator(); i.hasNext(); )
// //       {
// //         errors.add(i.next());

// //         if(i.hasNext())
// //           errors.add(", ");
// //       }

// //       commands.values.add(new Command(errors.toArray()));
// //     }

//     commands.pre.add(new Textblock(new Command(new Object []
//       {
//         createValueCommand(m_description, "description", inEditable),
//         new Linebreak(),
//         new Hrule(),
//         createValueCommand(m_short, "short description", inEditable),
//       }), "desc"));

//     commands.pre.add(new net.ixitxachitls.output.commands.Files
//                      (getType().getMultipleDir() + "/" + getName()));

//     commands.post.add(new Script
//                       ("gui.addAction('Save', "
//                        + "function() { save('base', '" + m_type.toString()
//                        + "'); }, "
//                     + "'Save all the changes made.', 'save', 'hidden');\n"));

//     return commands;
//   }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- setWorld ------------------------------

  /**
   * Set the world of the base entry.
   *
   * @param       inWorld the world to set to
   *
   * @return      true if set, false if not
   *
   */
//   public boolean setWorld(String inWorld)
//   {
//     if(inWorld == null)
//       return false;

//     return m_world.setSelected(inWorld);
//   }

  //........................................................................
  //------------------------------ setDescription --------------------------

  /**
   * Set the description of the base entry.
   *
   * @param       inDescription the description
   *
   * @return      true if set, false if not
   *
   */
//   public boolean setDescription(String inDescription)
//   {
//     if(inDescription == null)
//       return false;

//     m_description.set(inDescription);

//     return true;
//   }

  //........................................................................
  //---------------------------- setShortDescription -----------------------

  /**
   * Set the the short description of the base entry.
   *
   * @param       inShort the description
   *
   * @return      true if set, false if not
   *
   * @undefined   never
   *
   */
//   public boolean setShortDescription(String inShort)
//   {
//     if(inShort == null)
//       return false;

//     m_short.set(inShort);

//     return true;
//   }

  //........................................................................
  //-------------------------------- setSynonyms ---------------------------

  /**
   * Set the synonyms of the base entry.
   *
   * @param       inSynonyms the synonyms
   *
   * @return      true if added, false if not
   *
   * @undefined   assertion if synonyms is null
   *
   */
//   public boolean setSynonyms(String []inSynonyms)
//   {
//     if(inSynonyms == null)
//       return false;

//     m_synonyms.reset();

//     for(int i = 0; i < inSynonyms.length; i++)
//       m_synonyms.getBaseValue().add(new Text(inSynonyms[i], true, false));

//     return true;
//   }

  //........................................................................
  //-------------------------------- addSynonym ----------------------------

  /**
   * Add a synonym to the base entry.
   *
   * @param       inSynonym the synonym
   *
   * @return      true if added, false if not
   *
   */
//   public boolean addSynonym(String inSynonym)
//   {
//     if(inSynonym == null)
//       return false;

//     m_synonyms.getBaseValue().add(new Text(inSynonym, true, false));

//     return true;
//   }

  //........................................................................
  //------------------------------- setCategories --------------------------

  /**
   * Set the categories of the base entry.
   *
   * @param       inCategories the new categories
   *
   * @return      true if added, false if not
   *
   * @undefined   never
   *
   */
//   public boolean setCategories(String []inCategories)
//   {
//     if(inCategories == null)
//       return false;

//     m_categories.reset();

//     for(int i = 0; i < inCategories.length; i++)
//       m_categories.add(new Text(inCategories[i], true, false));

//     return true;
//   }

  //........................................................................
  //------------------------------- addCategory ----------------------------

  /**
   * Add a category to the base entry.
   *
   * @param       inCategory the category
   *
   * @return      true if added, false if not
   *
   */
//   public boolean addCategory(String inCategory)
//   {
//     if(inCategory == null)
//       return false;

//     m_categories.add(new Text(inCategory, true, false));

//     return true;
//   }

  //........................................................................
  //------------------------------- setReferences --------------------------

  /**
   * Set the references of the base entry.
   *
   * @param       inReferences the new references
   * @param       inStarts     the starting pages (0 for none)
   * @param       inEnds       the ending pages (0 for none)
   *
   * @return      true if added, false if not
   *
   * @undefined   never
   *
   */
//   public boolean setReferences(String []inReferences, int [][]inStarts,
//                                int [][]inEnds)
//   {
//     if(inReferences == null)
//       return false;

//     m_references.reset();

//     for(int i = 0; i < inReferences.length; i++)
//       addReference(inReferences[i], inStarts[i], inEnds[i]);

//     return true;
//   }

  //........................................................................

  //------------------------------ addReference ----------------------------

  /**
   * Add a reference to the base entry.
   *
   * @param       inReference the reference
   * @param       inStart     the start page (0 for none)
   * @param       inEnd       the end page (0 for none)
   *
   * @return      true if added, false if not
   *
   */
//   @SuppressWarnings("unchecked") // casting from multiple element
//   public boolean addReference(String inReference, int []inStart, int []inEnd)
//   {
//     if(inReference == null)
//       return false;

//     Multiple reference = m_references.getBaseValue().newElement();

//     ((Text)reference.get(0).getMutable()).set(inReference);

//     if(inStart != null || inEnd != null)
//     {
//       if(inStart.length != inEnd.length)
//         throw new IllegalArgumentException("number of starting pages does "
//                                            + "not match number of ending "
//                                            + "pages");

//     ValueList<Range> pages = (ValueList<Range>)reference.get(1).getMutable();

//       for(int i = 0; i < inStart.length; i++)
//       {
//         Range range = pages.newElement();

//         range.set(inStart[i], inEnd[i]);

//         pages.add(range);
//       }
//     }

//     m_references.getBaseValue().add(reference);

//     return true;
//   }

  //........................................................................
  //------------------------------- addBase --------------------------------

  /**
   * Add a base to this entry. The entry is ignored if name and entry are null.
   *
   * @param       inBase the base entry to add
   * @param       inName the name to add with (or null to use the name of the
   *                     given base entry, if any)
   *
   */
// protected void addBase(@MayBeNull BaseEntry inBase, @MayBeNull String inName)
//   {
//     super.addBase(inBase, inName);

//     if(inBase != null)
//       for(Iterator<AbstractAttachment> i = inBase.getAttachments();
//           i.hasNext(); )
//         addAttachment(i.next().getName());
//   }

  //........................................................................

  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled.
   *
   * @undefined   never
   *
   */
//   @SuppressWarnings("unchecked") // don't know real type of value
//   public void complete()
//   {
//     if(m_complete)
//       return;

//  // Add the attachments of all the bases (we have to do that before calling
//     // the super complete, as that will complete the attachments)
//     if(m_baseEntries != null)
//       for(BaseEntry entry : m_baseEntries)
//       {
//         if(entry == null)
//           continue;

//         for(Iterator<AbstractAttachment> i = entry.getAttachments();
//             i.hasNext(); )
//           addAttachment(i.next().getName());
//       }

//     super.complete();
//   }

  //........................................................................
  //------------------------------ readEntry -------------------------------

  /**
   * Read an entry, and only the entry without type and comments, from the
   * reader.
   *
   * @param       inReader the reader to read from
   *
   * @return      true if read successfully, false else
   *
   */
//   protected boolean readEntry(@Nonnull ParseReader inReader)
//   {
//     // TODO: remove this!
//     if(inReader.expect("based on"))
//     {
//       // we have to handle a value based on another one
//       Text base = new Text(false, false);
//       base.read(inReader);

//       // add the attachments from the base
//       m_base = BaseCampaign.GLOBAL.getBaseEntry(base.get(),
//                                                 getType().getBaseType());

//       if(m_base == null)
//         Log.warning("could not find base entry for '" + base
//                     + "', no attachments or initializers copied over");
//       else
//         for(Iterator<AbstractAttachment> i = m_base.getAttachments();
//             i.hasNext(); )
//           addAttachment(i.next().getName());

//       if(!inReader.expect(':'))
//         return false;
//     }

//     return super.readEntry(inReader);
//   }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions
  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends ValueGroup.Test
  {
    //----- text -----------------------------------------------------------

    /** Test text. */
    private static String s_text =
      "base entry test = \n"
      + "\n"
      + "  synonyms      \"blanket, winter\", \"guru\";\n"
      + "  categories    dagger, weapon, guru, test;\n"
      + "  references    \"a\" 10, \"b\" 5-10/20;\n"
      + "  short description \"A cozy, warm blanket.\";\n"
      + "  description   \n"
      + "\n"
      + "  \"A thick, quilted, wool blanket.\".\n"
      + "\n";

//     /** Test text with remarks. */
//     private static String s_remarks =
//       "base entry test remarks = \n"
//       + "\n"
//       + "  synonyms      {*}\"blanket, winter\", \"guru\";\n"
//       + "  categories    {~, some estimation}dagger, weapon, guru, test;\n"
//       + "  references    {p, a player remark} \"a\" 10, \"b\" 5-10/20;\n"
//       + "  description   \n"
//       + "\n"
//       + "  \"A thick, quilted, wool blanket.\".\n"
//       + "\n";

    //......................................................................
    //----- read -----------------------------------------------------------

     /** Testing reading. */
    @org.junit.Test
    public void read()
    {
      ParseReader reader =
        new ParseReader(new java.io.StringReader(s_text), "test");

      BaseEntry entry = (BaseEntry)BaseEntry.read(reader);

      //System.out.println(entry);
      m_logger.verify();

      assertNotNull("entry should have been read", entry);
      assertEquals("entry name does not match", "test",
                   entry.getName());
      assertEquals("entry does not match",
                   "#----- test\n"
                   + "\n"
                   + "base entry test =\n"
                   + "\n"
                   + "  references        \"a\" 10,\n"
                   + "                    \"b\" 5-10/20;\n"
                   + "  description       "
                   + "\"A thick, quilted, wool blanket.\";\n"
                   + "  short description \"A cozy, warm blanket.\";\n"
                   + "  synonyms          \"blanket, winter\",\n"
                   + "                    \"guru\";\n"
                   + "  categories        dagger,\n"
                   + "                    weapon,\n"
                   + "                    guru,\n"
                   + "                    test.\n"
                   + "\n"
                   + "#.....\n",
                   entry.toString());

      assertTrue("category dagger", entry.hasCategory("dagger"));
      assertTrue("category weapon", entry.hasCategory("weapon"));
      assertFalse("category gugus", entry.hasCategory("gugus"));
      assertEquals("short description", "A cozy, warm blanket.",
                   entry.getShortDescription());
      assertTrue("is base", entry.isBase());
//       assertEquals("synonyms 0", "blanket, winter", entry.getSynonyms()[0]);
//       assertEquals("synonyms 1", "guru", entry.getSynonyms()[1]);
    }

    //......................................................................
    //----- set/get --------------------------------------------------------

    /** Testing set and get. */
//     @org.junit.Test
//     public void setGet()
//     {
//       BaseEntry entry = new BaseEntry();

//       assertEquals("length", 0, entry.getSynonyms().length);

//       assertFalse("set", entry.setSynonyms(null));
//       assertTrue("set",  entry.setSynonyms(new String [] { "1", "2", "3"}));

//       assertEquals("length 2", 3, entry.getSynonyms().length);
//       assertEquals("synonyms 0", "1", entry.getSynonyms()[0]);
//       assertEquals("synonyms 1", "2", entry.getSynonyms()[1]);
//       assertEquals("synonyms 2", "3", entry.getSynonyms()[2]);

//       assertTrue("set",  entry.addSynonym("4"));

//       assertEquals("length 3", 4, entry.getSynonyms().length);
//       assertEquals("synonyms 0", "1", entry.getSynonyms()[0]);
//       assertEquals("synonyms 1", "2", entry.getSynonyms()[1]);
//       assertEquals("synonyms 2", "3", entry.getSynonyms()[2]);
//       assertEquals("synonyms 3", "4", entry.getSynonyms()[3]);

//       assertTrue("world", entry.setWorld("Forgotten Realms"));
//       assertFalse("world", entry.setWorld("guru"));
//       assertEquals("world", "Forgotten Realms", entry.getWorld());

//       assertTrue("category", entry.addCategory("cat"));
//       assertTrue("category", entry.hasCategory("cat"));
//     }

    //......................................................................
    //----- print ----------------------------------------------------------

    /** Testing printing. */
//     public void testPrint()
//     {
      // TODO: does not work right now because printing changed.
//       BaseEntry entry = new BaseEntry("test");

//       assertTrue("synonym", entry.addSynonym("synonym1"));
//       assertTrue("synonym", entry.addSynonym("synonym2"));
//       assertFalse("world", entry.setWorld("world"));
//       assertTrue("world", entry.setWorld("forgotten Realms"));
//       assertTrue("category", entry.addCategory("cat1"));
//       assertTrue("category", entry.addCategory("cat2"));
//       assertTrue("category", entry.addCategory("cat3"));
//       assertTrue("reference", entry.addReference("ref1", null, null));
//     assertTrue("reference", entry.addReference("ref2", new int [] { 23, 55 },
//                                     new int [] { 42, 66 }));
//       assertTrue("reference", entry.addReference("ref3", new int [] { 23 },
//                                     new int [] { 23 }));
//       assertTrue("description", entry.setDescription("description"));
//       assertTrue("short description", entry.setShortDescription("short"));

//       PrintCommand command = entry.printCommand(false, false);

//       assertNotNull("command", command);

//       // icons
//       assertEquals("world", "world: Forgotten Realms",
//                    extract((Command)command.icons.get(0), 2));
//       assertEquals("category", "divider",
//                    extract((Command)command.icons.get(1), 0));
//       assertEquals("category", "multiedit",
//                    extract((Command)command.icons.get(1), 1));
//       assertEquals("category", "categories: cat1",
//                    extract((Command)command.icons.get(1), 2, 1, 2));
//       assertEquals("category", "categories: cat2",
//                    extract((Command)command.icons.get(1), 2, 2, 2));
//       assertEquals("category", "categories: cat3",
//                    extract((Command)command.icons.get(1), 2, 3, 2));

//       // pre
//       assertEquals("description", "textblock",
//                    extract((Command)command.pre.get(0), 0));
//       assertEquals("description", "desc",
//                    extract((Command)command.pre.get(0), -1));
//       assertEquals("description", "description",
//                    extract((Command)command.pre.get(0), 1, 1));
//       assertEquals("short", "hrule",
//                    extract((Command)command.pre.get(0), 1, 3, 0));
//       assertEquals("short", "short",
//                    extract((Command)command.pre.get(0), 1, 4));
//       assertEquals("files", "files",
//                    extract((Command)command.pre.get(1), 0));
//       assertEquals("files", "BaseEntries/test",
//                    extract((Command)command.pre.get(1), 1));

//       // values
//       assertEquals("base", "Base:",
//                    extract((Command)command.values.get(0), 1, 1));
//       assertEquals("base", "", extract((Command)command.values.get(1)));
//       assertEquals("file", "File:",
//                    extract((Command)command.values.get(2), 1, 1));
//       assertEquals("file", "test",
//                    extract((Command)command.values.get(3), 1));
//       assertEquals("categories", "Categories:",
//                    extract((Command)command.values.get(4), 1, 1));
//       assertEquals("categories", "cat1,\ncat2,\ncat3",
//                    extract((Command)command.values.get(5)));
//       assertEquals("synoyms", "Synonyms:",
//                    extract((Command)command.values.get(6), 1, 1));
//       assertEquals("synonyms", "synonym1; synonym2",
//                    extract((Command)command.values.get(7), 1));
//       assertEquals("synoyms", "References:",
//                    extract((Command)command.values.get(8), 1, 1));
//       assertEquals("references", "color",
//                    extract((Command)command.values.get(9), 1, 1, 1, 0));
//       assertEquals("references", "error",
//                    extract((Command)command.values.get(9), 1, 1, 1, 1));
//       assertEquals("references", "link",
//                    extract((Command)command.values.get(9), 1, 1, 1, 2, 0));
//       assertEquals("references", "/entry/baseproduct/ref1",
//                    extract((Command)command.values.get(9), 1, 1, 1, 2, -1));
//       assertEquals("references", "ref1",
//                    extract((Command)command.values.get(9), 1, 1, 1, 2, 1));
//       assertEquals("references", ",\n",
//                    extract((Command)command.values.get(9), 1, 2));
//       assertEquals("references", "color",
//                    extract((Command)command.values.get(9), 1, 3, 1, 0));
//       assertEquals("references", "error",
//                    extract((Command)command.values.get(9), 1, 3, 1, 1));
//       assertEquals("references", "link",
//                    extract((Command)command.values.get(9), 1, 3, 1, 2, 0));
//       assertEquals("references", "/entry/baseproduct/ref2",
//                    extract((Command)command.values.get(9), 1, 3, 1, 2, -1));
//       assertEquals("references", "ref2",
//                    extract((Command)command.values.get(9), 1, 3, 1, 2, 1));
//       assertEquals("references", " ",
//                    extract((Command)command.values.get(9), 1, 3, 2));
//       assertEquals("references", "p. 23-42/55-66",
//                    extract((Command)command.values.get(9), 1, 3, 3));
//       assertEquals("references", ",\n",
//                    extract((Command)command.values.get(9), 1, 4));
//       assertEquals("references", "color",
//                    extract((Command)command.values.get(9), 1, 5, 1, 0));
//       assertEquals("references", "error",
//                    extract((Command)command.values.get(9), 1, 5, 1, 1));
//       assertEquals("references", "link",
//                    extract((Command)command.values.get(9), 1, 5, 1, 2, 0));
//       assertEquals("references", "/entry/baseproduct/ref3",
//                    extract((Command)command.values.get(9), 1, 5, 1, 2, -1));
//       assertEquals("references", "ref3",
//                    extract((Command)command.values.get(9), 1, 5, 1, 2, 1));
//       assertEquals("references", " ",
//                    extract((Command)command.values.get(9), 1, 5, 2));
//       assertEquals("references", "p. 23",
//                    extract((Command)command.values.get(9), 1, 5, 3));
//     }

    //......................................................................
    //----- testBased ------------------------------------------------------

    /** Testing with base values. */
//     public void testBased()
//     {
//       String base1 = "base entry with incomplete base1 = "
//         + "synonyms \"s1\", \"s2\"; "
//         + "world generic; "
//         + "references \"a\" 13-99; "
//         + "incomplete \"test\".";

//       String base3 = "base entry base3 = references \"b\" 23-42.";

//       String derived = "base entry derived [base1, base2, base3] = "
//         + "description \"d\".";

//       ParseReader reader =
//         new ParseReader(new java.io.StringReader(base1 + base3 + derived),
//                         "test");

//       // Add the base entry to the campaign for the second read to find it.
//       BaseCampaign.GLOBAL.add(BaseEntry.read(reader));
//       BaseCampaign.GLOBAL.add(BaseEntry.read(reader));

//       // Read the dervied entry
//       BaseEntry entry = (BaseEntry)BaseEntry.read(reader);
//       entry.complete();

//       m_logger.addExpectedPattern("WARNING: base.not-found:.*(base name "
//                                   + "'base2').*");

//       m_logger.verify();

//       assertNotNull("entry should have been read", entry);
//       assertEquals("entry name does not match", "derived",
//                    entry.getName());
//       assertEquals("entry does not match",
//                    "#----- derived\n"
//                    + "\n"
//                    + "base entry with incomplete derived "
//                    + "[base1, base2, base3] =\n"
//                    + "\n"
//                    + "  references        ;\n"
//                    + "  description       \n"
//                    + "  \"d\";\n"
//                    + "  synonyms          .\n"
//                    + "\n"
//                    + "#.....\n",
//                    entry.toString());

//       // check the values
//       assertEquals("world", "Generic", entry.getWorld());
//       assertEquals("references", "a 13-99",
//                    entry.m_references.getHigh().get(0).toString());
//       assertEquals("references", "b 23-42",
//                    entry.m_references.getHigh().get(1).toString());
//     }

    //......................................................................
    //----- testInitializer ------------------------------------------------

    /** Testing with initializers. */
//     public void testInitializer()
//     {
//       String s_initializer =
//         "#----- test2\n"
//         + "\n"
//         + "base entry test2 [test] = \n"
//         + "\n"
//         + "  synonyms      += \"another\";\n"
//         + "  categories    -= guru;\n"
//         + "  references    += \"c\";\n"
//         + "  description   += \n"
//         + "\n"
//         + "  \" With some more text.\".\n"
//         + "\n";

//       ParseReader reader =
//         new ParseReader(new java.io.StringReader(s_text + s_initializer),
//                         "test");

//       BaseCampaign.GLOBAL.add(BaseEntry.read(reader));
//       BaseEntry entry = (BaseEntry)BaseEntry.read(reader);
//       entry.complete();

//       //System.out.println(entry);
//       m_logger.verify();

//       assertNotNull("entry should have been read", entry);
//       assertEquals("entry name does not match", "test2",
//                    entry.getName());
//       assertEquals("synonyms", "blanket, winter", entry.getSynonyms()[0]);
//       assertEquals("synonyms", "guru", entry.getSynonyms()[1]);
//       assertEquals("synonyms", "another", entry.getSynonyms()[2]);
//       assertEquals("entry does not match", s_initializer, entry.toString());
      // TODO: this is currently changed!
//     }

    //......................................................................
    //----- testRemarks ----------------------------------------------------

    /** Testing with remarks. */
//     @org.junit.Test
//     public void remarks()
//     {
//       ParseReader reader =
//         new ParseReader(new java.io.StringReader(s_remarks),
//                         "test");

//       BaseEntry entry = (BaseEntry)BaseEntry.read(reader);

//       m_logger.verify();

//       assertNotNull("entry should have been read", entry);
//       assertEquals("entry name does not match", "test remarks",
//                    entry.getName());
//       assertEquals("entry does not match",
//                    "base entry test remarks =\n"
//                    + "\n"
//                    + "  world             {@} Generic;\n"
//                    + "  references        {p, a player remark} \"a\" 10,\n"
//                    + "                    \"b\" 5-10/20;\n"
//                    + "  description       \n"
//                    + "  \"A thick, quilted, wool blanket.\";\n"
//                    + "  synonyms          {*} \"blanket, winter\",\n"
//                    + "                    \"guru\";\n"
//                    + "  categories        {~, some estimation} dagger,\n"
//                    + "                    weapon,\n"
//                    + "                    guru,\n"
//                    + "                    test.\n",
//                    entry.toString());

//       // check if printing of remarks is ok

//       PrintCommand print = entry.printCommand(true, false);

//       // world
//     Command world = entry.getCommand(print, new Command("#{world}"), false);

//       assertEquals("world", "span", extract(world, 1, 2, 2, 0));
//       assertEquals("world", "AUTO", extract(world, 1, 2, 2, 1));
//       assertEquals("world", "window", extract(world, 1, 2, 2, 2, 0));
//       assertEquals("world", "world: Generic", extract(world, 1, 2, 2, 2, 1));
//       assertEquals("world", "Auto", extract(world, 1, 2, 2, 2, 2));

//       // references
//       Command references =
//         entry.getCommand(print, new Command("${references}"), true);
//       assertEquals("references", "span", extract(references, 1, 2, 0));
//       assertEquals("references", "PLAYER", extract(references, 1, 2, 1));
//       assertEquals("references", "window", extract(references, 1, 2, 2, 0));
//       assertEquals("references", "Player: a player remark",
//                    extract(references, 1, 2, 2, 2));

//       // synonyms
//       Command synonyms =
//         entry.getCommand(print, new Command("${synonyms}"), true);

//       assertEquals("synonyms", "span", extract(synonyms, 1, 2, 0));
//       assertEquals("synonyms", "HOUSE_RULE", extract(synonyms, 1, 2, 1));
//       assertEquals("synonyms", "window", extract(synonyms, 1, 2, 2, 0));
//       assertEquals("synonyms", "House Rule", extract(synonyms, 1, 2, 2, 2));

//       // categories
//       Command categories =
//         entry.getCommand(print, new Command("${categories}"), true);

//       assertEquals("categories", "span", extract(categories, 1, 2, 0));
//       assertEquals("categories", "ESTIMATION", extract(categories, 1, 2, 1));
//       assertEquals("categories", "window", extract(categories, 1, 2, 2, 0));
//       assertEquals("categories", "Estimation: some estimation",
//                    extract(categories, 1, 2, 2, 2));
//     }

    //......................................................................
    //----- testIndex ------------------------------------------------------

    /** Test indexes. */
//     public void testIndexes()
//     {
//       BaseCampaign.GLOBAL.m_bases.clear();

//       BaseEntry entry1 = new BaseEntry("name1");
//       entry1.setWorld("Generic");
//       entry1.addReference("ref1", null, null);
//    entry1.addReference("ref2", new int [] { 23, 55 }, new int [] { 42, 66 });
//       entry1.addReference("ref3", new int [] { 23 }, new int [] { 23 });
//       entry1.addCategory("cat1");
//       entry1.addCategory("cat2");
//       entry1.addCategory("cat3");

//       BaseEntry entry2 = new BaseEntry("name2");
//       entry2.setWorld("Forgotten Realms");
//       entry2.addReference("ref2", null, null);
//    entry2.addReference("ref4", new int [] { 23, 55 }, new int [] { 42, 66 });
//       entry2.addCategory("cat2");
//       entry2.addCategory("cat4");

//       BaseCampaign.GLOBAL.add(entry1);
//       BaseCampaign.GLOBAL.add(entry2);

//       m_logger.verify();

//       for(net.ixitxachitls.dma.entries.indexes.Index<?> index : s_indexes)
//       {
//         if("General".equals(index.getGroup())
//            && "Worlds".equals(index.getTitle()))
//         {
//           assertEquals("worlds", 2,
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries()).size());
//           Iterator i = index.buildNames
//             (BaseCampaign.GLOBAL.getAbstractEntries()).iterator();
//           assertEquals("worlds", "Forgotten Realms", i.next().toString());
//           assertEquals("worlds", "Generic", i.next().toString());

//           assertFalse("worlds", index.matchesName("guru", entry1));
//         assertTrue("worlds", index.matchesName("Forgotten realms", entry2));
//           assertTrue("worlds", index.matchesName("Generic", entry1));

//           continue;
//         }

//         if("General".equals(index.getGroup())
//            && "References".equals(index.getTitle()))
//         {
//           assertEquals("references", 4,
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries()).size());
//           assertTrue("references",
//                    index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
//                      .contains("ref1"));
//           assertTrue("references",
//                    index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
//                      .contains("ref2"));
//           assertTrue("references",
//                    index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
//                      .contains("ref3"));
//           assertTrue("references",
//                    index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
//                      .contains("ref4"));

//           assertTrue("references", index.matchesName("ref1", entry1));
//           assertTrue("references", index.matchesName("ref2", entry1));
//           assertTrue("references", index.matchesName("ref3", entry1));
//           assertFalse("references", index.matchesName("ref4", entry1));
//           assertFalse("references", index.matchesName("ref1", entry2));
//           assertTrue("references", index.matchesName("ref2", entry2));
//           assertFalse("references", index.matchesName("ref3", entry2));
//           assertTrue("references", index.matchesName("ref4", entry2));

//           continue;
//         }

//         if("General".equals(index.getGroup())
//            && "Categories".equals(index.getTitle()))
//         {
//           assertEquals("categories", 4,
//                        index.buildNames
//                        (BaseCampaign.GLOBAL.getAbstractEntries()).size());
//           assertTrue("categories",
//                    index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
//                      .contains("Cat1"));
//           assertTrue("categories",
//                    index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
//                      .contains("Cat2"));
//           assertTrue("categories",
//                    index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
//                      .contains("Cat3"));
//           assertTrue("categories",
//                    index.buildNames(BaseCampaign.GLOBAL.getAbstractEntries())
//                      .contains("Cat4"));

//           assertTrue("categories", index.matchesName("cat1", entry1));
//           assertTrue("categories", index.matchesName("cat2", entry1));
//           assertTrue("categories", index.matchesName("cat3", entry1));
//           assertFalse("categories", index.matchesName("cat4", entry1));
//           assertFalse("categories", index.matchesName("cat1", entry2));
//           assertTrue("categories", index.matchesName("cat2", entry2));
//           assertFalse("categories", index.matchesName("cat3", entry2));
//           assertTrue("categories", index.matchesName("cat4", entry2));

//           continue;
//         }
//       }

//       BaseCampaign.GLOBAL.m_bases.clear();
//     }

    //......................................................................
    //----- testMatching ---------------------------------------------------

    /** Test matching. */
//     public void testMatching()
//     {
//       BaseEntry entry1 = new BaseEntry("entry 1");
//       BaseEntry entry2 = new BaseEntry("entry 2");

//       // not matching if not same type
//       assertFalse("match type", entry1.matches(new BaseItem("item")));

//       // not matching because of name
//       assertFalse("match name", entry2.matches(entry1));

//       // match name
//       entry2 = new BaseEntry(null);
//       assertTrue("match name", entry2.matches(entry1));

//       entry2 = new BaseEntry("entry **");
//       assertTrue("match name", entry2.matches(entry1));

//       // match some values now
//       assertEquals("set value", null,
//                    entry2.m_world.setFromString("== Forgotten Realms"));
//       assertFalse("match value", entry2.matches(entry1));

//       entry1.setWorld("Forgotten Realms");
//       assertTrue("match value", entry2.matches(entry1));

//       // matching in a list
//       assertEquals("set value", null,
//                    entry2.m_categories.setFromString("~= cat"));
//       assertFalse("match value", entry2.matches(entry1));

//       entry1.addCategory("cat1");
//       entry1.addCategory("cat2");
//       entry1.addCategory("cat");

//       assertTrue("match value", entry2.matches(entry1));
//     }

    //......................................................................
  }

  //........................................................................
}
