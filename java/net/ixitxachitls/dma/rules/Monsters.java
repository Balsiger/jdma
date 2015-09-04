/*******************************************************************************
 * Copyright (c) 2002-2015 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
 ******************************************************************************/

package net.ixitxachitls.dma.rules;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.values.enums.Ability;

/**
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class Monsters
{
  public static final int SECONDARY_ATTACK_PENALTY = 5;
  public static final double SECONDARY_DAMAGE_FACTOR = 0.5;

  /**
   * Compute the ability modifier for the given score.
   *
   * @param       inScore the ability score to compute for
   *
   * @return      the compute modifier
   */
  public static int abilityModifier(long inScore)
  {
    if(inScore < 0)
      return 0;

    return (int) (inScore / 2) - 5;
  }

  public static int abilityModifier(Optional<? extends Integer> inScore)
  {
    if(inScore.isPresent())
      return abilityModifier(inScore.get());

    return 0;
  }
}
