/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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

import net.ixitxachitls.dma.entries.Item;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is the weapon extension for all the entries.
 *
 * @file          Weapon.java
 *
 * @author        balsiger@ixitxachitls.net (Peter 'Merlin' Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

@ParametersAreNonnullByDefault
public class Weapon extends Extension<Item>
{
  //--------------------------------------------------------- constructor(s)

  //--------------------------------- Weapon -------------------------------

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor.
   *
   * @param       inEntry the entry this extension is attached to
   * @param       inName  the name of the extension
   *
   */
  public Weapon(Item inEntry, String inName)
  {
    super(inEntry, inName);
  }

  //........................................................................
  //--------------------------------- Weapon -------------------------------

  /**
   * Default constructor.
   *
   * @param       inEntry the entry this extension is attached to
   * @param       inTag   the tag name for this instance
   * @param       inName  the name of the extension
   *
   */
  // public Weapon(Item inEntry, String inTag, String inName)
  // {
  //   super(inEntry, inTag, inName);
  // }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  static
  {
    extractVariables(Item.class, Weapon.class);
  }

  //........................................................................

  //-------------------------------------------------------------- accessors

  //----------------------------- computeValue -----------------------------

  /**
   * Get a value for printing.
   *
   * @param     inKey  the name of the value to get
   * @param     inDM   true if formattign for dm, false if not
   *
   * @return    a value handle ready for printing
   *
   */
  // @Override
  // public @Nullable ValueHandle computeValue(String inKey,
  // boolean inDM)
  // {
  //   if(inDM && "summary".equals(inKey))
  //   {
  //     List<Object> commands = new ArrayList<Object>();
  //     commands.add(new Linebreak());
  //     commands.add(new Symbol("\u2694"));
  //     maybeAddValue(commands, "weaypon type", inDM, null, null);
  //     maybeAddValue(commands, "proficiency", inDM, " ", null);
  //     maybeAddValue(commands, "weapon style", inDM, " ", null);
  //     maybeAddValue(commands, "damage", inDM, " ", null);
  //     maybeAddValue(commands, "critical", inDM, " (critical ", ")");
  //     maybeAddValue(commands, "range", inDM, ", range ", null);
  //     maybeAddValue(commands, "reach", inDM, ", reach ", null);

  //     return new FormattedValue(new Command(commands), null, "summary");
  //   }

  //   return super.computeValue(inKey, inDM);
  // }

  //........................................................................
  //---------------------------- addListCommands ---------------------------

  /**
   * Add the commands for printing this extension to a list.
   *
   * @param       ioCommands the commands to add to
   * @param       inDM       flag if setting for DM or not
    *
   * @undefined   IllegalArgumentException if given commands are null
   *
   */
 // public void addListCommands(@MayBeNull ListCommand ioCommands, boolean inDM)
  // {
  //   if(ioCommands == null)
  //     return;

  //   super.addListCommands(ioCommands, inDM);

  //   List<BaseWeapon> bases = getBases(BaseWeapon.class);

  //   if(bases.size() > 0)
  //   {
  //     BaseWeapon base = bases.get(0);
  //     // damage + critical, type, style
  //     ioCommands.add
  //       (ListCommand.Type.WEAPON,
  //        new Command(new Object []
  //          {
  //            new Bold(new Color("subtitle", m_entry.getPlayerName())),
  //            new Super(new Scriptsize("(" + m_entry.getID() + ")")),
  //          }));
  //     ioCommands.add(ListCommand.Type.WEAPON,
  //                    new Command(new Object []
  //                      {
  //                        base.m_damage,
  //                        " (",
  //                        base.m_critical,
  //                        ") ",
  //                      }));
  //     ioCommands.add(ListCommand.Type.WEAPON, base.m_type);
  //     ioCommands.add(ListCommand.Type.WEAPON, base.m_style);
  //     ioCommands.add(ListCommand.Type.WEAPON, base.m_range);
  //     ioCommands.add(ListCommand.Type.WEAPON, base.m_reach);
  //   }
  // }

  //........................................................................
  //-------------------------- addSummaryCommand ---------------------------

  /**
   * Add the extensions value to the summary command list.
   *
   * @param       ioCommands the commands so far, will add here
   * @param       inDM       true if setting for dm
   *
   */
  // public void addSummaryCommands(List<Object> ioCommands, boolean inDM)
  // {
  //   ioCommands.add(", ");

  //   BaseWeapon base = getBases(BaseWeapon.class).get(0);

  //   if(base == null)
  //     ioCommands.add(new Color("error", "base weapon not found"));
  //   else
  //   {
  //     ioCommands.add(base.m_proficiency.format(false));
  //     ioCommands.add(" ");
  //     ioCommands.add(base.m_type.format(false));
  //     ioCommands.add(" ");
  //     ioCommands.add(base.m_damage.format(false));
  //     ioCommands.add(" (");
  //     ioCommands.add(base.m_critical);
  //     ioCommands.add(")");

  //     if(base.m_splash.isDefined())
  //     {
  //       ioCommands.add(", splash ");
  //       ioCommands.add(base.m_splash.format(false));
  //     }

  //     if(base.m_range.isDefined())
  //     {
  //       ioCommands.add(", range ");
  //       ioCommands.add(base.m_range.format(false));
  //     }

  //     if(base.m_reach.isDefined())
  //     {
  //       ioCommands.add(", reach ");
  //       ioCommands.add(base.m_reach.format(false));
  //     }
  //   }
  // }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //------------------------------- complete -------------------------------

  /**
   * Complete the entry and make sure that all values are filled.
   *
   * @undefined   never
   *
   */
  // public void complete()
  // {
   // check the size compared to the real size (cf. Player's Handbook p. 113)
  //   // (but ignore grenades...)
  //   if(getBases(BaseWeapon.class).size() == 0)
  //     return;
  //   BaseWeapon base = getBases(BaseWeapon.class).get(0);

  //   if(base == null || m_entry == null)
  //     return;

  //   BaseWeapon.WeaponType type  = base.m_type.getSelected();
  //   BaseWeapon.Style      style = base.m_style.getSelected();

  //   if(type != BaseWeapon.WeaponType.GRENADE)
  //   {
  //     BaseItem.Size weaponSize =
  //       m_entry.getExtension(Wearable.class).getUserSize();
  //     BaseItem.Size itemSize   = m_entry.getSize();

  //     if(weaponSize == null)
  //       m_entry.getExtension(Wearable.class).complete();

  //     weaponSize = m_entry.getExtension(Wearable.class).getUserSize();

  //     if(itemSize != null && style != null && weaponSize != null
  //        && weaponSize.isBigger(BaseItem.Size.DIMINUTIVE)
  //        && style.isMelee()
  //        && itemSize != weaponSize.add(style.getSizeDifference()))
  //     {
  //       CheckError error =
  //         new CheckError("weapon.size",
  //                        m_entry.getName() + " (" + m_entry.getID() + ")"
  //                        + " is too "
  //                        + (itemSize.isBigger(weaponSize.add
  //                                             (style.getSizeDifference()))
  //                         ? "large" : "small") + " compared to object size; "
  //                        + "should be a "
  //                        + weaponSize.add(style.getSizeDifference())
  //                        + " object but is " + itemSize);

  //       Log.warning(error);

  //       m_entry.addError(error);
  //     }
  //   }

  //   super.complete();
  // }

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //----------------------------- modifyValue ------------------------------

  /**
    *
    * Modify the given value with information from the current extension.
    *
    * @param       inType    the type of value to modify
    * @param       inEntry   the entry to modify in
    * @param       inValue   the value to modify, return in this object
    * @param       inDynamic a flag denoting if dynamic modifiers should be
    *                        returned
    *
    * @return      the newly computed value (or null if no value to use)
    *
    * @undefined   never
    *
    * @algorithm   nothing done here
    *
    * @derivation  necessary if real modifications are desired
    *
    * @example     see Item
    *
    * @bugs
    * @to_do
    *
    * @keywords    modify . value
    *
    */
//   public Modifier modifyValue(PropertyKey inType, AbstractEntry inEntry,
//                               Value inValue, boolean inDynamic)
//   {
//     if(inValue == null || !inValue.isDefined())
//       return null;

//     if(inType == BaseWeapon.DAMAGE)
//     {
//       if(inEntry != null && inEntry instanceof Item)
//       {
//         Pair<ValueGroup, Variable> pair =
//           inEntry.getVariable(Item.USER_SIZE.toString());

//         if(pair.first() != null && pair.second() != null)
//         {
//           BaseItem.Size size =
//             ((EnumSelection<BaseItem.Size>)
//              pair.second().get(pair.first())).getSelected();

//           // nothing to do for medium sized weapons
//           if(size == BaseItem.Size.MEDIUM)
//             return null;

//           // larger than medium
//           if(size.isBigger(BaseItem.Size.MEDIUM))
//             return new Modifier(Modifier.Type.MULTIPLY,
//                                 size.difference(BaseItem.Size.MEDIUM) + 1);
//           else
//             // smaller than medium
//             return new Modifier(Modifier.Type.DIVIDE,
//                                 BaseItem.Size.MEDIUM.difference(size) + 1);
//         }
//       }
//     }

//     return super.modifyValue(inType, inEntry, inValue, inDynamic);
//   }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests, see BaseItem for tests

  //........................................................................
}
