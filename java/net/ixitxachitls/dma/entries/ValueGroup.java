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

package net.ixitxachitls.dma.entries;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
// TODO: clean up commented out code
// import java.util.Iterator;
// import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
// import java.util.regex.Pattern;
// import java.util.regex.Matcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//import net.ixitxachitls.dma.entries.indexes.Index;
//import net.ixitxachitls.dma.values.Modifiable;
//import net.ixitxachitls.dma.values.SimpleText;
//import net.ixitxachitls.dma.values.Text;
import net.ixitxachitls.dma.values.Value;
//import net.ixitxachitls.dma.values.ValueList;
//import net.ixitxachitls.dma.values.modifiers.BaseModifier;
//import net.ixitxachitls.dma.values.modifiers.ValueModifier;
import net.ixitxachitls.input.ParseReader;
import net.ixitxachitls.output.commands.BaseCommand;
// import net.ixitxachitls.output.commands.Bold;
//import net.ixitxachitls.output.commands.Color;
// import net.ixitxachitls.output.commands.Divider;
// import net.ixitxachitls.output.commands.Editable;
// import net.ixitxachitls.output.commands.ID;
// import net.ixitxachitls.output.commands.Icon;
// import net.ixitxachitls.output.commands.OverviewFiles;
// import net.ixitxachitls.output.commands.Table;
// import net.ixitxachitls.output.commands.TempGroup;
// import net.ixitxachitls.output.commands.Window;
//import net.ixitxachitls.util.Encodings;
import net.ixitxachitls.util.Strings;
import net.ixitxachitls.util.configuration.Config;
// import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This class groups a bunch of Values, its be base for all entries.
 *
 * @file          ValueGroup.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public abstract class ValueGroup
{
  //----------------------------------------------------------------- nested

  //----- Annotations ------------------------------------------------------

  /**
   * The annotations for variables.
   *
   * @param The key to use for this variable.
   *
   */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface Key {
    /** The name of the value. */
    String value();

    /** Flag if value is to be stored or not. */
    boolean stored() default true;

    /** Flag if the value is for dms only. */
    boolean dm() default false;

    /** Flag if the value is for players only. */
    boolean player() default false;

    /** Flag if the value can be edited by players. */
    boolean playerEditable() default true;

    /** The plural version of the name of hte value. */
    String plural() default "";
  }

  //........................................................................

  //----- ListCommand ------------------------------------------------------

  /** An class to store all the command to print various lists. */
//   public static class ListCommand
//   {
//     //----- Type -----------------------------------------------------------

//     /** The possible values for printing lists.
//      *
//      * The order of the entries also defines the order in which they will be
//      * printed.
//      */
//     public enum Type {
//       /** The general list. */
//       GENERAL,
//       /** Weapon information. */
//       WEAPON,
//       /** Information about counted items. */
//       COUNTED,
//       /** Informatiion about light sources. */
//       LIGHT,
//       /** Information about timed items. */
//       TIMED,
//       /** Information for the name. */
//       NAME,
//       /** Information about contents. */
//       CONTENTS,
//     };

//     //....................................................................

//     /** Create the list command object. */
//     public ListCommand()
//     {
//       // nothing to do
//     }

//     /** All the commands for all types. */
//     protected Map<Type, java.util.List<Object>> m_lists =
//       new HashMap<Type, java.util.List<Object>>();

//     /**
//      * Get the list of base commands for the given type.
//      *
//      * @param  inType the type to get the list for
//      *
//      * @return the list for the type (a new list is created and stored if not
//      *         available)
//      *
//      */
//     public java.util.List<Object> getList(Type inType)
//     {
//       java.util.List<Object> list = m_lists.get(inType);

//       if(list == null)
//       {
//         list = new ArrayList<Object>();

//         m_lists.put(inType, list);
//       }

//       return list;
//     }

//     /**
//      * Check if a command with the given type is present.
//      *
//      * @param  inType the type to check for
//      *
//      * @return true if the command is available, false if not
//      *
//      */
//     public boolean hasCommand(@MayBeNull Type inType)
//     {
//       return m_lists.containsKey(inType);
//     }

//     /**
//      * Get the command for the given type.
//      *
//      * @param  inType the type of the command to get
//      *
//      * @return the command found or null
//      *
//      */
//     @MayReturnNull
//     public Command get(@MayBeNull Type inType)
//     {
//       if(!m_lists.containsKey(inType))
//         return null;

//       return new Command(getList(inType).toArray());
//     }

//     /**
//      * Add a type command to the list.
//      *
//      * @param inType    the type of the command
//      * @param inCommand the command to add
//      *
//      */
//     public void add(Type inType, Object inCommand)
//     {
//       getList(inType).add(inCommand);
//     }
//   }

  //........................................................................
  //----- PrintCommand -----------------------------------------------------

  /** A simple structure to store the results of creating a print command. */
//   public class PrintCommand
//   {
//    //--------------------------------------------------------------- nested

//     private class PrintValue
//     {
//       private PrintValue(Object inValue, boolean inEditable, boolean inDM,
//                          boolean inPlayer, String inPluralKey)
//       {
//         if(inDM && inPlayer)
//          throw new IllegalArgumentException("value can't be for DM only and "
//                                              + "for player only, something "
//                                              + "must be wrong!");

//         m_value    = inValue;
//         m_editable = inEditable;
//         m_dm       = inDM;
//         m_player   = inPlayer;
//         m_plural   = inPluralKey;
//       }

//       private Object m_value;
//       private boolean m_editable;
//       private boolean m_dm;
//       private boolean m_player;
//       private String m_plural;
//       private PrintValue m_next = null;

//       public void add(PrintValue inValue)
//       {
//         if(m_next == null)
//           m_next = inValue;
//         else
//           m_next.add(inValue);
//       }

//       public String toString()
//       {
//         List<String> options = new ArrayList<String>();

//         if(m_editable)
//           options.add("editable");

//         if(m_dm)
//           options.add("dm");

//         StringBuilder builder = new StringBuilder();

//         for(PrintValue value = this; value != null; value = value.m_next)
//         {
//           builder.append(value.m_plural);
//           builder.append(": ");
//           builder.append(value.m_value.toString());

//           if(value.m_next != null)
//             builder.append(" / ");
//         }

//         if(options.size() > 0)
//         {
//           builder.append(" ");
//           builder.append(options);
//         }

//         return builder.toString();
//       }
//     }

//     //......................................................................

//     /** The type of entry printed. */
//     public String type = "type";

//     /** A map with all the individual values. */
//     private Map<String, PrintValue> m_values =
//       new HashMap<String, PrintValue>();

//     public java.util.List<Object> temp;

//     /** All the commands for the header. */
//     public java.util.List<Object> header = new ArrayList<Object>();

//     /** All the commands for the short header. */
//     public java.util.List<Object> shortHeader = new ArrayList<Object>();

//     /** All the commands before the value table. */
//     public java.util.List<Object> pre    = new ArrayList<Object>();

//     /** All the commands after the value table. */
//     public java.util.List<Object> post   = new ArrayList<Object>();

//     /** All the commands in the value table. */
//     public java.util.List<Object> values = new ArrayList<Object>();

//     /** All the commands in the icon list. */
//     public java.util.List<Object> icons  = new ArrayList<Object>();

//     /** All the values that are set in the icons. */
//     public java.util.List<Object> iconValues = new ArrayList<Object>();

//     /** All the images. */
//     public java.util.List<Object> images = new ArrayList<Object>();

//     /** The number of highlights used so far. */
//     private int m_count = 0;

//     /** The possible prefixes for base value. */
//     private static final String s_basePrefixes = "+<>^$#";

     //----------------------------- asCommands -----------------------------

//     /**
//      * Return a command representing this print command.
//      *
//      * @param       inDM      true if to get values for the DM, false else
//      * @param       inValue   the values to use for printing
//      *
//      * @return      the commands requested
//      *
//      */
//     public List<Object> asCommands(boolean inDM, String inValues)
//     {
//       List<Object> result = new ArrayList<Object>();

//       if(inValues == null)
//         return result;

//       for(String name : inValues.split(",\\s*"))
//         if(name.length() > 0)
//           addCommand(result, name, inDM);

//       return result;
//     }

//     //......................................................................
     //--------------------------- asIconCommands ---------------------------

//     /**
//      * Return a command representing this print command.
//      *
//      * @param       inDM      true if to get values for the DM, false else
//      * @param       inValue   the values to use for printing
//      *
//      * @return      the commands requested
//      *
//      */
//     public List<Object> asIconCommands(boolean inDM, String inValues)
//     {
//       List<Object> result = new ArrayList<Object>();

//       if(inValues == null)
//         return result;

//       for(String name : inValues.split(",\\s*"))
//         if(name.length() > 0)
//           addIconCommand(result, name, inDM);

//       return result;
//     }

//     //......................................................................
     //-------------------------- asValueCommands ---------------------------

//     /**
//      * Return a command representing this print command.
//      *
//      * @param       inDM      true if to get values for the DM, false else
//      * @param       inValue   the values to use for printing
//      *
//      * @return      the commands requested
//      *
//      */
//     public List<Object> asValueCommands(boolean inDM, String inValues)
//     {
//       List<Object> result = new ArrayList<Object>();

//       if(inValues == null)
//         return result;

//       for(String name : inValues.split(",\\s*"))
//         if(name.length() > 0)
//           addValueCommand(result, name, inDM);

//       return result;
//     }

//     //......................................................................
     //--------------------------- asPageCommands ---------------------------

//     /**
//      * Return a command representing this print command.
//      *
//      * @param       inDM      true if to get values for the DM, false else
//   * @param       inHeaders comma seperated list of names to print for headers
//   * @param       inIcons   comma seperated list of names to print for icons
//   * @param       inValues  comma seperated list of names to print for values
//      *
//      * @return      the commands requested
//      *
//      */
//     public List<Object> asPageCommands(boolean inDM, String inHeaders,
//                                        String inIcons, String inValues)
//     {
//       List<Object> result = new ArrayList<Object>();

       // icons ----------------------------------------------------------------
//       result.add(new Divider("center", new Command
//                              (asIconCommands(inDM, inIcons).toArray())));

      // header ---------------------------------------------------------------
//       result.addAll(asCommands(inDM, inHeaders));

      // images ---------------------------------------------------------------
//       for(Object image : asCommands("image", inDM))
//         result.add(new OverviewFiles(image));

      // values ---------------------------------------------------------------
//       result.add(new Table("description", "f" + "Illustrations: ".length()
//                            + ":L(desc-label);100:L(desc-text)",
//                            asValueCommands(inDM, inValues).toArray()));
//       result.add(new Divider("clear", ""));

//       return result;
//     }

//     //......................................................................
     //----------------------------- asCommand ------------------------------

//     /**
//      * Add a value command for the referenced value.
//      *
//      * @param       inValue  the value to return the command for
//      * @param       inKey    the key for the value
//      * @param       inDM     setting for the dm
//      *
//      * @return      the command/object for printing
//      *
//      */
//     private Object asCommand(String inKey, PrintValue inValue, boolean inDM)
//     {
//       if(inValue == null)
//         return null;

//       if(inValue.m_value == null)
//         return new Color("error", "*undefined*");

//       if(!inDM && inValue.m_dm || inDM && inValue.m_player)
//         return null;

//       Object val = inValue.m_value;
//       if(!inDM && inValue.m_value instanceof
//          net.ixitxachitls.dma.values.Modifiable)
//         val = ((net.ixitxachitls.dma.values.Modifiable)
//                inValue.m_value).getLow();

//       if(val instanceof Value)
//         return createValueCommand((Value)val, inKey,inValue.m_editable);

//       return val;
//     }

//     //......................................................................
     //----------------------------- asCommands -----------------------------

//     /**
//      * Get the value as commands.
//      *
//      * @param       inKey      the key for the value
//      * @param       inDM       setting for the dm
//      *
//      * @return      the command/object for printing
//      *
//      */
//     public List<Object> asCommands(String inKey, boolean inDM)
//     {
//       List<Object> result = new ArrayList<Object>();

//       for(PrintValue value = getValue(inKey, inDM); value != null;
//           value = value.m_next)
//         result.add(asCommand(inKey, value, inDM));

//       return result;
//     }

//     //......................................................................
     //---------------------------- addCommand ------------------------------

//     /**
//      * Add a value command for the referenced value.
//      *
//      * @param       ioCommands the list of commands to add to
//      * @param       inKey      the key for the value
//      * @param       inDM       setting for the dm
//      *
//      */
//     private void addCommand(List<Object> ioCommands, String inKey,
//                             boolean inDM)
//     {
//       List<Object> commands = asCommands(inKey, inDM);

//       if(commands == null)
//         return;

//       ioCommands.addAll(commands);
//     }

//     //......................................................................
     //--------------------------- addIconCommand ---------------------------

//     /**
//      * Add an icon command to the list for the referenced value.
//      *
//      * @param       ioCommands the list of commands to add to
//      * @param       inKey      the key for the value
//      * @param       inDM       setting for the dm
//      *
//      */
//     private void addIconCommand(List<Object> ioCommands, String inKey,
//                                 boolean inDM)
//     {
//       addIconCommand(ioCommands, getValue(inKey, inDM), inKey, inDM);
//     }

//     //......................................................................
     //--------------------------- addIconCommand ---------------------------

//     /**
//      * Add an icon command to the list for the referenced value.
//      *
//      * @param       ioCommands the list of commands to add to
//      * @param       inValue    the print value to print
//      * @param       inKey      the key for the value
//      * @param       inDM       setting for the dm
//      *
//      */
//     public void addIconCommand(List<Object> ioCommands, PrintValue inValue,
//                                String inKey, boolean inDM)
//     {
//       if(inValue == null || inValue.m_value == null)
//         return;

//       if(s_basePrefixes.indexOf(inKey.charAt(0)) >= 0)
//         inKey = inKey.substring(1);

//       if(!(inValue.m_value instanceof Value))
//       {
//         String asString = inValue.m_value.toString();
//         ioCommands.add(new Icon(inValue.m_plural + "/" + asString + ".png",
//                                 inKey + ": " + asString,
//                               "/index/" + inValue.m_plural + "/" + asString,
//                                 true));
//       }
//       else
//       {
//         List<Value> values = new ArrayList<Value>();

//         if(inValue.m_value instanceof ValueList)
//           for(Value val : (ValueList<?>)inValue.m_value)
//             values.add(val);
//         else
//           values.add((Value)inValue.m_value);

//         for(Value val : values)
//         {
//           Icon icon = new Icon(inValue.m_plural + "/"
//                                + val.toString().toLowerCase() + ".png",
//                                val.formatRemark(inKey + ": " + val),
//                              "/index/" + inValue.m_plural + "/" + val, true);

//           if(inValue.m_editable)
//             ioCommands.add(new Editable(getID(), icon, inKey, val.toStore(),
//                                         val.getEditType(), null,
//                                         val.getEditValues()));
//           else
//             ioCommands.add(icon);
//         }
//       }

//       if(inValue.m_next != null)
//         addIconCommand(ioCommands, inValue.m_next, inKey, inDM);
//     }

//     //......................................................................

     //-------------------------- addValueCommand ---------------------------

//     /**
//      * Add a value command for the referenced value.
//      *
//      * @param       ioCommands the list of commands to add to
//      * @param       inKey      the key for the value
//      * @param       inDM       setting for the dm
//      *
//      */
//     private void addValueCommand(List<Object> ioCommands, String inKey,
//                                  boolean inDM)
//     {
//       List<Object> commands = asCommands(inKey, inDM);

//       // Only add it if we actually got any commands for defined values.
//       if(commands.size() == 0)
//         return;

//       Command command = new Command(commands.toArray());

//       if(s_basePrefixes.indexOf(inKey.charAt(0)) >= 0)
//         inKey = inKey.substring(1);

//       ioCommands.add(createValueLabel(inKey));
//       ioCommands.add(command);
//     }

//     //......................................................................

     //------------------------------ getValue ------------------------------

//     /**
//      * Get a value from the group.
//      *
//      * @param       inKey the key of the value to get
//      * @param       inDM  flag if to get for dm or not
//      *
//      * @return      the requested print value
//      *
//      */
//     private PrintValue getValue(String inKey, boolean inDM)
//     {
//       PrintValue value = m_values.get(inKey);

//       if(value != null && value.m_value != null)
//         return value;

//       // Do we have to get a base value?
//       if(s_basePrefixes.indexOf(inKey.charAt(0)) >= 0)
//       {
//         String key = inKey.substring(1);

//         AbstractEntry.Combine combine;
//         switch(inKey.charAt(0))
//         {
//           // add up all base values
//           case '+':
//             combine = AbstractEntry.Combine.ADD;
//             break;

//           case '#':
//             combine = AbstractEntry.Combine.MODIFY;
//             break;

//           case '<':
//             combine = AbstractEntry.Combine.MINIMUM;
//             break;

//           case '>':
//             combine = AbstractEntry.Combine.MAXIMUM;
//             break;

//           case '^':
//           default:
//             combine = AbstractEntry.Combine.FIRST;
//             break;
//         }

//         Value val =
//           ((AbstractEntry)ValueGroup.this).getBaseValue
//           (key, combine, inDM, ValueGroup.this.getValue(key));

//         if(val == null)
//         {
//           Log.warning("Could not get value for '" + key + "'");

//           return null;
//         }

//         if(((AbstractEntry)ValueGroup.this).m_baseEntries != null)
//         for(BaseEntry base : ((AbstractEntry)ValueGroup.this).m_baseEntries)
//           {
//             if(base == null)
//               continue;

//             Pair<ValueGroup, Variable> var = base.getVariable(inKey);

//             if(var == null)
//               break;

//             return new PrintValue(val, false, var.second().isDMOnly(),
//                                   var.second().isPlayerOnly(),
//                                   var.second().getPluralKey());
//           }

//         return new PrintValue(val, false, false, false, key + "s");
//       }
//       else
//       {
//         Pair<ValueGroup, Variable> var = getVariable(inKey);

//         if(var == null)
//         {
//           Log.warning("Could not get print value for '" + inKey + "'");
//           return null;
//         }

//         ValueGroup group = var.first();
//         Variable variable = var.second();

//         return new PrintValue(variable.getValue(group),
//                               inDM || variable.isPlayerEditable(),
//                               variable.isDMOnly(), variable.isPlayerOnly(),
//                               variable.getPluralKey());
//       }
//     }

    //......................................................................
     //-------------------------------- has ---------------------------------

//     /**
//      * Check if the print command has the given value.
//      *
//      * @param       inKey the key of the value to check for
//      * @parm        inDM  true if checkinf for dm, false it nof
//      *
//      * @return      true if the values is present, false if not
//      *
//      */
//     public boolean has(String inKey, boolean inDM)
//     {
//       return getValue(inKey, inDM) != null;
//     }

//     //......................................................................

     //------------------------------ addValue ------------------------------

//     /**
//      * Add a value to the print output.
//      *
//      * @param       inKey      the key of the value added
//      * @param       inValue    the value to add
//      * @param       inEditable true if editable, false if not
//      * @param       inDM       true if the value is for DMs only, false else
//      * @param       inPlayer   true if the data is for players only
//      * @param       inPlural   the plural for the key of the value
//      *
//      */
//     public void addValue(String inKey, Object inValue, boolean inEditable,
//                          boolean inDM, boolean inPlayer, String inPlural)
//     {
//       PrintValue newValue =
//         new PrintValue(inValue, inEditable, inDM, inPlayer, inPlural);

//       PrintValue oldValue = m_values.get(inKey);

//       if(oldValue == null)
//         m_values.put(inKey, newValue);
//       else
//         oldValue.add(newValue);
//     }

//     //......................................................................
     //---------------------------- appendValue -----------------------------

//     /**
//      * Append a value to an already existing value in the print output.
//      *
//      * @param       inKey      the key of the value added
//      * @param       inValue    the value to add
//      *
//      */
//     public void appendValue(String inKey, Object inValue)
//     {
//       PrintValue value = m_values.get(inKey);

//       if(value == null)
//       {
//         value = getValue(inKey, true);

//         if(value == null)
//         {
//           Log.warning("could not add value for " + inKey
//                       + " since no existing value found");

//           return;
//         }

//         m_values.put(inKey, value);
//       }

//       value.add(new PrintValue(inValue, value.m_editable, value.m_dm,
//                                value.m_player, value.m_plural));
//     }

//     //......................................................................
     //---------------------------- replaceValue ----------------------------

//     /**
//      * replace a value to the print output.
//      *
//      * @param       inKey      the key of the value added
//      * @param       inValue    the value to add
//      * @param       inEditable true if editable, false if not
//      * @param       inDM       true if the value is for DMs only, false else
//   * @param       inPlayer   true if the value is for players only, false else
//      * @param       inPlural   the plural version of the key
//      *
//      */
//   public void replaceValue(String inKey, Object inValue, boolean inEditable,
//                              boolean inDM, boolean inPlayer, String plural)
//     {
//       m_values.put(inKey, new PrintValue(inValue, inEditable, inDM, inPlayer,
//                                          plural));
//     }

//     //......................................................................
     //------------------------------- reFlag -------------------------------

//     /**
//      * Change the flags for a value.
//      *
//      * @param       inKey      the key for the value
//      * @param       inEditable true if editable, false if not
//      * @param       inDM       true if the value is for DMs only, false else
//      * @param       inPlayer   true if the data is for players only
//      *
//      */
//     public void reFlag(String inKey, @MayBeNull Boolean inEditable,
//                        @MayBeNull Boolean inDM, @MayBeNull Boolean inPlayer)
//     {
//       PrintValue value = m_values.get(inKey);

//       if(value == null)
//         return;

//       if(inEditable != null)
//         value.m_editable = inEditable;

//       if(inDM != null)
//         value.m_dm = inDM;

//       if(inPlayer != null)
//         value.m_player = inPlayer;
//     }

//     //......................................................................

     //-------------------------- addEditableIcon ---------------------------

//     /**
//      * Add an editable icon command to the print command.
//      *
//      * @param       inValue    the value to add an icon for
//      * @param       inKey      the key of the value to add
//    * @param       inKeys     the key of the value to add (as multiple string)
//      * @param       inEditable true if the icon is editable, false if not
//      *
//      * @undefined   IllegalArgumentException if any argument is null
//      *
//      */
//     public void addIcon(Value inValue, String inKey, String inKeys,
//                         boolean inEditable)
//     {
//       if(inValue == null)
//         inValue = new SimpleText();

//       if(inKey == null)
//         throw new IllegalArgumentException("must have a key here");

//       if(inKeys == null)
//         throw new IllegalArgumentException("must have keys here");

//       Icon icon = new Icon(inKeys + "/" + inValue.toString().toLowerCase()
//                            + ".png",
//                            inValue.formatRemark(inKey + ": " + inValue),
//                            "/index/" + inKeys + "/" + inValue, true);

//       if(inEditable)
//         icons.add(new Editable(getID(), icon, inKey, inValue.toStore(),
//                                inValue.getEditType(), null,
//                                inValue.getEditValues()));
//       else
//         icons.add(icon);
//     }

//     //......................................................................
     //------------------------------ addValue ------------------------------

//     /**
//      * Add a value to the print output.
//      *
//      * @param       inValue    the value to add
//      * @param       inKey      the key of the value added
//      * @param       inEditable true if editable, false if not
//      *
//      * @undefined   never
//      *
//      */
//     @Deprecated
//     public void addValue(Value inValue, String inKey, boolean inEditable)
//     {
//       addValue(inValue, inKey, inEditable, null);
//     }

//     //......................................................................
     //---------------------------- addIconValue ----------------------------

//     /**
//      * Add an icon value to the print output.
//      *
//      * @param       inValue    the value to add
//      * @param       inKey      the key of the value added
//      * @param       inEditable true if editable, false if not
//      *
//      * @undefined   never
//      *
//      */
//     public void addIconValue(Value inValue, String inKey, boolean inEditable)
//     {
//       addIconValue(inValue, inKey, inEditable, null);
//     }

//     //......................................................................
     //------------------------------ addValue ------------------------------

//     /**
//      * Add a value to the print output.
//      *
//      * @param       inValue    the value to add
//      * @param       inKey      the key of the value added
//      * @param       inEditable true if editable, false if not
//      * @param       inScript   any script code to associate with editable
//      *
//      * @undefined   never
//      *
//      */
//     public void addValue(Value inValue, String inKey, boolean inEditable,
//                          String inScript)
//     {
//       if(inEditable)
//         addValue(createValueLabel(inKey),
//                  createValueCommand(inValue, inKey, null, inScript,
//                                     inEditable));
//       else
//         addValue(createValueLabel(inKey),
//                  createValueCommand(inValue, inKey, inEditable));
//     }

//     //......................................................................
     //---------------------------- addIconValue ----------------------------

//     /**
//      * Add an icon value to the print output.
//      *
//      * @param       inValue    the value to add
//      * @param       inKey      the key of the value added
//      * @param       inEditable true if editable, false if not
//      * @param       inScript   any script code to associate with editable
//      *
//      * @undefined   never
//      *
//      */
//     public void addIconValue(Value inValue, String inKey, boolean inEditable,
//                          String inScript)
//     {
//       if(inEditable)
//         addIconValue(createValueLabel(inKey),
//                      createValueCommand(inValue, inKey, null, inScript,
//                                         inEditable));
//       else
//         addIconValue(createValueLabel(inKey),
//                      createValueCommand(inValue, inKey, inEditable));
//     }

//     //......................................................................
     //------------------------------ addValue ------------------------------

//     /**
//      * Add a value to the print output.
//      *
//      * @param       inLabel    the label to use for printing
//      * @param       inValue    the value command for printing
//      *
//      * @undefined   never
//      *
//      */
//     public void addValue(Object inLabel, Object inValue)
//     {
//       values.add(inLabel);
//       values.add(inValue);
//     }

//     //......................................................................
     //---------------------------- addIconValue ----------------------------

//     /**
//      * Add an icon value to the print output.
//      *
//      * @param       inLabel    the label to use for printing
//      * @param       inValue    the value command for printing
//      *
//      */
//     public void addIconValue(Object inLabel, Object inValue)
//     {
//       iconValues.add(inLabel);
//       iconValues.add(inValue);
//     }

//     //......................................................................
     //------------------------- addAttachmentValue -------------------------

//     /**
//      * Add a value to the print output.
//      *
//      * @param       inValue    the value to add
//      * @param       inKey      the key of the value added
//      * @param       inID       the id for highlighting
//      * @param       inEditable true if editable, false if not
//      *
//      * @undefined   never
//      *
//      */
//     public void addAttachmentValue(Value inValue, String inKey, String inID,
//                                    boolean inEditable)
//     {
//       addAttachmentValue(inValue, inKey, inID + ++m_count, inEditable, null);
//     }

//     //......................................................................
     //------------------------- addAttachmentValue -------------------------

//     /**
//      * Add a value to the print output.
//      *
//      * @param       inValue    the value to add
//      * @param       inKey      the key of the value added
//      * @param       inID       the id for highlights
//      * @param       inEditable true if editable, false if not
//      * @param       inScript   any script code to associate with editable
//      *
//      * @undefined   never
//      *
//      */
//     public void addAttachmentValue(Value inValue, String inKey, String inID,
//                                    boolean inEditable, String inScript)
//     {
//       if(inEditable)
//         addValue(createHighlightedValueLabel(inKey, inID),
//                  createValueCommand(inValue, inKey, null, inScript,
//                                     inEditable));
//       else
//         addValue(createHighlightedValueLabel(inKey, inID),
//                  createValueCommand(inValue, inKey, inEditable));
//     }

//     //......................................................................
//     //------------------------------ toString ------------------------------

//     /**
//      * Convert to a string for debugging.
//      *
//      * @return      a human readable string representation of this object.
//      *
//      * @undefined   never
//      *
//      */
//     public String toString()
//     {
//       return type + ":\n"
//         + "  header: " + header + "\n"
//         + "  pre:    " + pre + "\n"
//         + "  post:   " + post + "\n"
//         + "  values: " + values + "\n"
//         + "  icons:  " + icons + "\n";
//     }

//     //......................................................................
//   }

//   //........................................................................
  //----- ValueTransformer -------------------------------------------------

//   /**
//    * The transformer to replace values in commands. This will replace
//    * string arguments as follows:
//    *
//    *   - $value is replaced as with asCommands() as a normal value
//    *   - #value is replaced as with asIconCommands() as an icon value
//    *   - %value is replaced as with asValueCommands() as a value
//    */
//   protected static class ValueTransformer implements Command.Transformer
//   {
//     public ValueTransformer(PrintCommand inValues, boolean inDM)
//     {
//       m_values = inValues;
//       m_dm     = inDM;
//     }

//     private PrintCommand m_values;
//     private boolean m_dm;

//     public Object transform(Object inCommand)
//     {
//       if(inCommand instanceof Command)
//         return ((Command)inCommand).transform(this);

//       if(inCommand instanceof String)
//       {
//         List<String> tokens =
//           Encodings.tokenize((String)inCommand,
//                              "(\\$|#|%|\\?|&)(?:\\{(.*?)\\}|(\\w+))\\s?");

//         List<Object> commands = new ArrayList<Object>();
//         for(String token : tokens)
//         {
//           if(token.startsWith("$"))
//             // normal value
//             commands.addAll(m_values.asCommands(m_dm, token.substring(1)));
//           else
//             if(token.startsWith("#"))
//               // value as icon
//               commands.addAll(m_values.asIconCommands(m_dm,
//                                                       token.substring(1)));
//             else
//               if(token.startsWith("%"))
//                 // value as table value
//                 commands.addAll(m_values.asValueCommands(m_dm,
//                                                        token.substring(1)));
//               else
//                 if(token.startsWith("?"))
//                 {
//                   String []parts = token.substring(1).split("\\|");

//                   String pre = "";
//                   String post = "";
//                   String name = parts[0];

//                   if(parts.length == 3)
//                   {
//                     pre  = parts[0];
//                     name = parts[1];
//                     post = parts[2];
//                   }

//                   List<Object> sub = m_values.asCommands(m_dm, name);

//                   if(sub != null && sub.size() > 0)
//                   {
//                     commands.add(pre);
//                     commands.addAll(sub);
//                     commands.add(post);
//                   }
//                 }
//                 else
//                   if(token.startsWith("&"))
//                   {
//                     List<Object> sub = m_values.asCommands(m_dm,
//                                                          token.substring(1));
//                     if(sub.size() > 0)
//                       commands.addAll(sub);
//                     else
//                       commands.add(new Command(""));
//                   }
//                 else
//                   commands.add(token);
//         }

//         if(commands.size() == 0)
//           return null;

//         if(commands.size() == 1)
//           return commands.get(0);

//         return new TempGroup(commands.toArray());
//       }

//       return inCommand;
//     }
//   }

  //........................................................................

  //........................................................................

  //--------------------------------------------------------- constructor(s)

  //------------------------------ ValueGroup ------------------------------

  /**
   * Default constructor.
   *
   */
  protected ValueGroup()
  {
    // nothing to do
  }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The delimiter between entries. */
  protected static final char s_delimiter =
    Config.get("resource:entries/delimiter", '.');

  /** The delimiter between individual values. */
  protected static final char s_keyDelimiter =
    Config.get("resource:entries/key.delimiter", ';');

  /** The delimiter for lists. */
  protected static final char s_listDelimiter =
    Config.get("resource:entries/list.delimiter", ',');

  /** The keyword indent to use. */
  protected static final int s_keyIndent =
  Config.get("resource:entries/key.indent", 2);

  /** The random generator. */
  protected static final @Nonnull Random s_random = new Random();

  /** All the variables for each individual derived class. */
  protected static final @Nonnull Map<Class, Variables> s_variables =
    new HashMap<Class, Variables>();

  /** An empty set of values for all unknown classes. */
  private static final @Nonnull Variables s_emptyVariables = new Variables();

//   /** All the indexes. */
//   protected static final @Nonnull List<Index<? extends Index>> s_indexes =
//     new ArrayList<Index<? extends Index>>();

  // TODO: make this not static and move to campaign.
  /** The name of the current game. */
  public static final String CURRENT =
    Config.get("user:configuration", "default");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- getVariables ------------------------------

  /**
   * Get the variables possible for this group.
   *
   * @return      all the variables
   *
   */
  public @Nonnull Variables getVariables()
  {
    Variables result = getVariables(this.getClass());

    if(result == null)
      return s_emptyVariables;

    return result;
  }

  //........................................................................
  //----------------------------- getVariables ------------------------------

  /**
   * Get the variables possible for the given class group.
   *
   * @param       inClass the class go get the variables for
   *
   * @return      all the variables
   *
   */
  public static @Nullable Variables getVariables(@Nonnull Class inClass)
  {
    return s_variables.get(inClass);
  }

  //........................................................................
  //------------------------------- getValue -------------------------------

//   /**
//    * Get a value given as a field.
//    *
//    * @param       inField the field for which to get the value
//    *
//    * @return      the value the field has in this object, if any
//    *
//    * @undefined   returns null if the field is not found
//    *
//    */
//   public Value getValue(Field inField)
//   {
//     if(inField == null)
//       return null;

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
  //----------------------------- getVariable -----------------------------

  /**
   * Get the variable for the given key.
   *
   * @param       inKey the name of the key to get the value for
   *
   * @return      the value for the key
   *
   */
  protected @Nullable Variable getVariable(@Nonnull String inKey)
  {
    return getVariables().getVariable(inKey);
  }

  //........................................................................
  //------------------------------ getValue --------------------------------

  /**
   * Get the value for the given key.
   *
   * @param       inKey the name of the key to get the value for
   *
   * @return      the value for the key
   *
   */
  public @Nullable Value getValue(@Nonnull String inKey)
  {
    Variable var = getVariable(inKey);

    if(var == null)
      return null;

    return var.get(this);
  }

  //........................................................................

  //-------------------------------- isBase --------------------------------

  /**
   * Check if the current entry represents a base entry or not.
   *
   * @return      true if this is a base entry, false else
   *
   */
  public boolean isBase()
  {
    return false;
  }

  //........................................................................

  //-------------------------------- getKey --------------------------------

  /**
   * Get the key for a given field.
   *
   * @param       inField the field to get the key for
   *
   * @return      the key of the field or null if none
   *
   */
//   protected static String getKey(Field inField)
//   {
//     Key key = inField.getAnnotation(Key.class);

//     if(key != null)
//       return key.value();

//     return null;
//   }

  //........................................................................
  //------------------------------- getName --------------------------------

  /**
    * Get the name of the group.
    *
    * @return      the name
    */
  public abstract @Nonnull String getName();

  //........................................................................
  //--------------------------------- getID --------------------------------

  /**
   * Get the ID of the entry. This can mainly be used for reference purposes.
   * In this case, the name is equal to the id, which is not true for entries.
   *
   * @return      the requested id
   *
   */
  public abstract @Nonnull String getID();

  //........................................................................
  //------------------------------ getIndexes ------------------------------

//   /**
//    * Get all the indexes.
//    *
//    * @return      a iterator over all indexes
//    *
//    * @undefined   never
//    *
//    */
//   public static Iterator<Index<? extends Index>> getIndexes()
//   {
//     return s_indexes.iterator();
//   }

  //........................................................................

  //---------------------------- getPrintCommand ---------------------------

//   /**
//    * Print the item to the document, in the general section.
//    *
//    * @param       inDM true if setting for dm, false if not
//    *
//    * @return      the command representing this item in a list
//    *
//    */
//   public Command getPrintCommand(boolean inDM)
//   {
//     return null;
//   }

  //........................................................................
  //------------------------------- getCommand -----------------------------

//   /**
//    * Print the item to the document, in the general section.
//    *
//    * @param       inValues  all the values used for printing
//    * @param       inCommand the command template to use for printing
//    * @param       inDM      true if setting for dm, false if not
//    *
//    * @return      the command representing this item in a list
//    *
//    */
//   public Command getCommand(PrintCommand inValues, Command inCommand,
//                             boolean inDM)
//   {
//     return inCommand.transform(new ValueTransformer(inValues, inDM));
//   }

  //........................................................................

  //--------------------------- createValueLabel ---------------------------

  /**
   * Create the command for a label of a value.
   *
   * @param       inKey the key of the value to create the label for
   *
   * @return      a window command for the label
   *
   */
//   protected @Nonnull Window createValueLabel(@Nonnull String inKey)
//   {
//     return new Window(new Bold(Encodings.toWordUpperCase(inKey) + ":"),
//                       Config.get("resource:help/labels/" + inKey,
//                                  "please add " + inKey
//                                  + " to help/labels.config"));
//   }

  //........................................................................
  //--------------------- createHighlightedValueLabel ----------------------

  /**
   * Create the command for a label of a value.
   *
   * @param       inKey the key of the value to create the label for
   * @param       inID  the id for highlighting
   *
   * @return      a window command for the label
   *
   */
//  protected @Nonnull Window createHighlightedValueLabel(@Nonnull String inKey,
//                                                         @Nonnull String inID)
//   {
//    return new Window(new Bold(new ID("highlight-" + inID,
//                                     Encodings.toWordUpperCase(inKey) + ":")),
//                       Config.get("resource:help/labels/" + inKey,
//                                  "please add " + inKey
//                                  + " to help/labels.config"));
//   }

  //........................................................................
  //-------------------------- createValueCommand ---------------------------

  /**
   * Create the command for a value.
   *
   * @param       inValue    the value to add
   * @param       inKey      the key of the value added
   * @param       inEditable true if editable, false if not
   *
   * @return      the command to print the value
   *
   */
//   protected @Nonnull Command createValueCommand(@Nonnull Value inValue,
//                                                 @Nonnull String inKey,
//                                                 boolean inEditable)
//   {
//     return createValueCommand(inValue, inKey, null, null, inEditable);
//   }

  //........................................................................
  //-------------------------- createValueCommand --------------------------

  /**
   * Create the command for a value.
   *
   * @param       inValue    the value to add
   * @param       inKey      the key of the value added
   * @param       inType     the edit type of the value
   * @param       inScript   any script code to associate with editable
   * @param       inEditable true if the value can be edited, false if not
   *
   * @return      the command to print the value
   *
   */
//   public @Nonnull Command createValueCommand(@Nonnull Value inValue,
//                                              @Nonnull String inKey,
//                                     @Nullable String inType,
//                                              @Nullable String inScript,
//                                     boolean inEditable)
//   {
//     if(inEditable)
//       return new Editable(getID(), inValue.format(true), inKey,
//                           inValue.toEdit(),
//                           inType != null ? inType : inValue.getEditType(),
//                           inScript, inValue.getEditValues());
//     else
//       return inValue.format(true);
//   }

  //........................................................................

  //----------------------------- formatValues -----------------------------

  /**
   * Format all the values contained in the entry for storing.
   *
   * @param       inAppend   the buffer to append the values to
   * @param       inFirst    flag if the printed values will be the first or
   *                         not
   * @param       inKeyWidth the width of the keys to use
   *
   * @return      true if at least one value was printed, false else
   *
   */
  protected boolean formatValues(@Nonnull StringBuilder inAppend,
                                 boolean inFirst,
                                 int inKeyWidth)
  {
    Variables variables = getVariables();

    for(Variable var : variables)
    {
      // if the variable is not stored, skip it
      if(!var.isStored())
        continue;

      Value value = var.getValue(this);

      // We don't store this if we don't have a value.
      if(value == null || !value.isDefined())
        continue;

      // add the delimiter
      if(inFirst)
        inFirst = false;
      else
      {
        inAppend.append(s_keyDelimiter);
        inAppend.append('\n');
      }

      inAppend.append(Strings.spaces(s_keyIndent));
      inAppend.append(variables.getPrefix());
      inAppend.append(var.getKey());
      inAppend.append(Strings.spaces
                      (Math.max(1, inKeyWidth - var.getKey().length()
                                - variables.getPrefix().length())));

      // now append the value of the variable
      // TODO: readd this handling when Text is in
//       if(value instanceof Text && ((Text)value).keepsFormatting())
//       {
//         inAppend.append("\n  ");
//         inAppend.append(value.toString());
//       }
//       else
        inAppend.append(value.toString().replaceAll
                        ("\\s*\n\\s*",
                         "\n" + Strings.spaces(inKeyWidth + s_keyIndent)));
    }

    return !inFirst;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //--------------------------------- set ----------------------------------

  /**
   * Set the value for the given key.
   *
   * @param       inKey  the name of the key to set the value for
   * @param       inText the text to set the value to
   *
   * @return      the part of the string that could not be parsed
   *
   */
  public @Nonnull String set(@Nonnull String inKey, @Nonnull String inText)
  {
    Variable var = getVariable(inKey);

    if(var == null)
      return inText;

    return var.setFromString(this, inText);
  }

  //........................................................................
  //------------------------------- readValue ------------------------------

  /**
   * Read a value, and only one value, from the reader into the object.
   *
   * @param       inReader   the reader to read from
   * @param       inVariable the variable to read into
   *
   * @return      true if read, faluse if not
   *
   */
  protected boolean readValue(@Nonnull ParseReader inReader,
                              @Nonnull Variable inVariable)
  {
    // TODO: change read to return the newly read value and thus make the
    // values really immutable.
    return inVariable.getValue(this).read(inReader);
  }

  //........................................................................

  //---------------------------- extractVariables ---------------------------

  /**
   * Extract all the keyed variables from the given class.
   *
   * @param       inClass the class to extract from
   *
   */
  protected static void extractVariables(@Nonnull Class inClass)
  {
    assert !s_variables.containsKey(inClass) : "already extracted";

    List<Variable> variables = new ArrayList<Variable>();

    // add all the annotated variables
    Field []fields = inClass.getDeclaredFields();

    for(Field field : fields)
    {
      Key key = field.getAnnotation(Key.class);

      if(key != null)
        variables.add(new Variable(key.value(), field, key.stored(), key.dm(),
                                   key.player(), key.playerEditable(),
                                   key.plural()));
    }

    // add all the variables of the parent class, if any
    Class superClass = inClass.getSuperclass();

    if(superClass != null && s_variables.get(superClass) != null)
      for(Variable v : s_variables.get(superClass))
        variables.add(v);

    s_variables.put(inClass, new Variables(variables));
  }

  //........................................................................
  //------------------------------- changed --------------------------------

  /**
    * Set the state of the file to changed.
    *
    */
  public void changed()
  {
    changed(true);
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
  public abstract void changed(boolean inChanged);

  //........................................................................
  //-------------------------- completeVariables ---------------------------

  /**
   * Complete all the variables of this group with the given group.
   *
   * @param       inBases the base groups to copy values from
   *
   */
//   protected void completeVariables
//     (@MayBeNull List<? extends ValueGroup> inBases)
//   {
//     if(inBases == null)
//       return;

//     for(Variable variable : getValues())
//     {
//       Value value = variable.getValue(this);
//       boolean defined = value.hasValue();
//       for(ValueGroup base : inBases)
//       {
//         if(base == null)
//           continue;

//         Value baseValue = base.getValue(variable.getKey());

//         if(value instanceof Modifiable)
//         {
//           Modifiable mValue = (Modifiable)value;

//           if(baseValue instanceof Modifiable)
//           {
//             for(BaseModifier<?> modifier :
//                   ((Modifiable<?>)baseValue).modifiers())
//               mValue.addModifier(modifier);

//             baseValue = ((Modifiable)baseValue).getBaseValue();
//           }

//           if(baseValue != null && baseValue.isDefined())
//             mValue.addModifier(new ValueModifier<Value>
//                                (ValueModifier.Operation.ADD, baseValue,
//                                 ValueModifier.Type.GENERAL, base.getName()));
//         }
//         else
//         {
//           if(baseValue != null && baseValue.isDefined())
//           {
//             if(!defined)
//             {
//               value.complete(baseValue, !defined);
//               value.setStored(false);
//               defined = value.isDefined();
//             }
//           }
//         }
//       }
//     }
//   }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------- constant -------------------------------

  /**
   * Get a string constant for an enumeration type from the configuration.
   *
   * @param       inGroup the group the constant is in
   * @param       inName  the name of the configuration value
   *
   * @return      the value as it is in the configuration
   *
   */
  public static @Nonnull String constant(@Nonnull String inGroup,
                                         @Nonnull String inName)
  {
    return constant(inGroup, inName, inName);
  }

  //........................................................................
  //------------------------------- constant -------------------------------

  /**
   * Get an string constant for an enumeration type from the configuration.
   *
   * @param       inGroup   the group the constant is in
   * @param       inName    the name of the configuration value
   * @param       inDefault the default value to return if none found
   *
   * @return      the value as it is in the configuration
   *
   */
  public static @Nonnull String constant(@Nonnull String inGroup,
                                         @Nonnull String inName,
                                         @Nonnull String inDefault)
  {
    return Config.get("resource:" + CURRENT + "/" + inGroup + "."
                      + inName, inDefault);
  }

  //........................................................................
  //------------------------------- constant -------------------------------

  /**
   * Get an integer constant for an enumeration type from the configuration.
   *
   * @param       inGroup  the group the constant is in
   * @param       inName   the name of the configuration value
   * @param       inDefault the default value to use if none is given
   *
   * @return      the value as it is in the configuration
   *
   */
  public static int constant(@Nonnull String inGroup, @Nonnull String inName,
                             int inDefault)
  {
    return Config.get("resource:" + CURRENT + "/" + inGroup + "."
                      + inName, inDefault);
  }

  //........................................................................
  //------------------------------- constant -------------------------------

  /**
   * Get a boolean constant for an enumeration type from the configuration.
   *
   * @param       inGroup   the group the constant is in
   * @param       inName    the name of the configuration value
   * @param       inDefault the default value to use if none is given
   *
   * @return      the value as it is in the configuration
   *
   */
  public static boolean constant(@Nonnull String inGroup,
                                 @Nonnull String inName,
                                 boolean inDefault)
  {
    return Config.get("resource:" + CURRENT + "/" + inGroup + "."
                      + inName, inDefault);
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The testing class. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //------------------------------ extract -------------------------------

    /**
     * Extract a specific (sub)command from the given command.
     *
     * @param       inCommand the command to extract from
     * @param       inIndexes the list of indexes to return; positive values
     *              denote normal arguments, numbered from 1, negative argument
     *              denote optional arguments, numbered from -1; 0 will
     *              return the name of the command itself
     *
     * @return      a string representation of the desired command or argument
     *
     */
    protected @Nonnull String extract(@Nonnull Object inCommand,
                                      int ... inIndexes)
    {
      return extract(inCommand, inIndexes, 0);
    }

    //......................................................................
    //------------------------------ extract -------------------------------

    /**
     * Extract a specific (sub)command from the given command. This is the
     * internal implementation for the recursion.
     *
     * @param       inCommand the command to extract from
     * @param       inIndexes the list of indexes to return; positive values
     *              denote normal arguments, numbered from 1, negative argument
     *              denote optional arguments, numbered from -1; 0 will
     *              return the name of the command itself
     * @param       inStart the index to the inIndexes to start with.
     *
     * @return      a string representation of the desired command or argument
     *
     */
    protected @Nonnull String extract(@Nonnull Object inCommand,
                                      int []inIndexes, int inStart)
    {
      if(inStart >= inIndexes.length)
        return inCommand.toString();

      int index = inIndexes[inStart];

      if(index == 0)
        if(inCommand instanceof BaseCommand)
          return ((BaseCommand)inCommand).getName();
        else
          return "expected command, found '" + inCommand + "'";

      if(index > 0)
        return extract(((BaseCommand)inCommand).getArguments().get(index - 1),
                       inIndexes, inStart + 1);

      return extract(((BaseCommand)inCommand).getOptionals().get(-index - 1),
                     inIndexes, inStart + 1);
    }

    //......................................................................

    /** A simple implementation of a value group for testing. */
    public static class TestGroup extends ValueGroup
    {
      /** The change state. */
      @Key(value = "changed", stored = false)
      protected boolean m_changed = false;

      /** A simple value. */
      @Key("simple value")
      protected Value m_value = new Value.Test().m_value.clone();

      /** A value for dms only. */
      @Key(value = "dm value", dm = true, plural = "dms value")
      protected Value m_dmValue = new Value.Test().m_value.clone();

      /** A value for players only. */
      @Key(value = "player value", player = true, playerEditable = false)
      protected Value m_playerValue = new Value.Test().m_value.clone();

      /** A player editable value. */
      @Key(value = "player editable", playerEditable = true)
      protected Value m_playerEditableValue = new Value.Test().m_value.clone();

      /** Set the change state.
       *
       * @param inState the new state
       */
      public void changed(boolean inState)
      {
        m_changed = inState;
      }

      /** Get the id of the group.
       *
       * @return the id
       */
      public String getID()
      {
        return "Test-ID";
      }

      /** Get the name of the group.
       *
       * @return the name
       */
      public String getName()
      {
        return "Test-Name";
      }
    }

    /** Simple test setup. */
    @org.junit.BeforeClass
    public static void setUp()
    {
      extractVariables(TestGroup.class);
    }

    //----- variables ------------------------------------------------------

    /** The variables Test. */
    @org.junit.Test
    public void variables()
    {
      Variables variables = s_variables.get(TestGroup.class);
      assertTrue("variables", variables != null);
      assertTrue("changed", variables.getVariable("changed") != null);
      assertNull("invalid", variables.getVariable("invalid"));
      assertFalse("stored", variables.getVariable("changed").isStored());
      assertTrue("stored", variables.getVariable("simple value").isStored());
      assertEquals("plural", "dms value",
                   variables.getVariable("dm value").getPluralKey());
      assertEquals("plural", "player values",
                   variables.getVariable("player value").getPluralKey());
      assertTrue("dm", variables.getVariable("dm value").isDMOnly());
      assertFalse("dm", variables.getVariable("player value").isDMOnly());
      assertFalse("player", variables.getVariable("dm value").isPlayerOnly());
      assertTrue("player",
                 variables.getVariable("player value").isPlayerOnly());
      assertTrue("editable",
                 variables.getVariable("player editable").isPlayerEditable());
      assertFalse("editable",
                  variables.getVariable("player value").isPlayerEditable());
    }

    //......................................................................
    //----- formatting -----------------------------------------------------

    /** The formatting Test. */
    @org.junit.Test
    public void formatting()
    {
      ValueGroup group = new TestGroup();

      StringBuilder builder = new StringBuilder();
      assertFalse("format", group.formatValues(builder, true, 5));
      assertEquals("format", "", builder.toString());

      group.set("simple value", "guru");
      group.set("dm value", "guru");
      group.set("player value", "guru");

      assertTrue("format", group.formatValues(builder, true, 13));
      assertEquals("format",
                   "  simple value guru;\n"
                   + "  dm value     guru;\n"
                   + "  player value guru",
                   builder.toString());
    }

    //......................................................................
    //----- values ---------------------------------------------------------

    /** The values Test. */
    @org.junit.Test
    public void values()
    {
      TestGroup group = new TestGroup();
      group.set("simple value", "guru");
      group.set("dm value", "guru");
      group.set("player value", "guru");

      assertEquals("id", "Test-ID", group.getID());
      assertEquals("name", "Test-Name", group.getName());
      assertFalse("base", group.isBase());
      assertEquals("value", "guru", group.getValue("simple value").toString());
      assertNull("value", group.getValue("invalid"));
      assertFalse("changed", group.m_changed);
      group.changed();
      assertTrue("changed", group.m_changed);
    }

    //......................................................................
    //----- read -----------------------------------------------------------

    /** The read Test. */
    @org.junit.Test
    public void read()
    {
      ValueGroup group = new TestGroup();
      ParseReader reader =
        new ParseReader(new java.io.StringReader("guru gugus"), "test");

      assertTrue("read",
                 group.readValue(reader, group.getVariable("simple value")));
      assertEquals("value", "guru", group.getValue("simple value").toString());
    }

    //......................................................................
  }

  //........................................................................
}
