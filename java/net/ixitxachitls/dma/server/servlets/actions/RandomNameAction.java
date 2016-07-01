/*******************************************************************************
 * Copyright (c) 2002-2016 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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

package net.ixitxachitls.dma.server.servlets.actions;

import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.BaseCampaign;
import net.ixitxachitls.dma.entries.Campaign;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.values.enums.Gender;

/**
 * Action to generate random names.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class RandomNameAction extends Action
{
  public RandomNameAction()
  {
    super("campaign", "kind", "race", "region", "gender");
  }

  @Override
  public String execute(Optional<String> inCampaign, Optional<String> inKind,
                        Optional<String> inRace, Optional<String> inRegion,
                        Optional<String> inGender)
  {
    if(!inCampaign.isPresent())
      return "no campaign given";

    Optional<EntryKey> campaignKey = EntryKey.fromString(inCampaign.get());
    if(!campaignKey.isPresent())
      return "invalid campaign key found";

    Optional<Campaign> campaign =
        DMADataFactory.get().getEntry(campaignKey.get());

    if(!campaign.isPresent())
      return "campaign not found";

    if(!inKind.isPresent())
      return "must specify kind for random";

    if(!inRace.isPresent() || inRace.get().isEmpty())
      return "must select a race to generate a random name";

    if(!inGender.isPresent())
      return "must select a gender to generate a random name";

    Optional<Gender> gender = Gender.fromString(inGender.get());
    if(!gender.isPresent())
      return "must select a proper gender to generate a random name";

    return campaign.get().generateRandomName(inRace.get(), inRegion,
                                             gender.get());
  }
}
