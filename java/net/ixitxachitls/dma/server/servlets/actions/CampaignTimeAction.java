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
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.Campaign;
import net.ixitxachitls.dma.entries.EntryKey;
import net.ixitxachitls.dma.server.servlets.DMARequest;
import net.ixitxachitls.dma.values.CampaignDate;
import net.ixitxachitls.util.logging.Log;

/**
 * Action to modify the campaign time.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class CampaignTimeAction extends Action
{
  public CampaignTimeAction()
  {
    super("campaign, minutes, hours, days, months, years");
  }

  @Override
  public String execute(DMARequest inRequest)
  {
    int minutes = inRequest.getParam("minutes", 0);
    int hours = inRequest.getParam("hours", 0);
    int days = inRequest.getParam("days", 0);
    int months = inRequest.getParam("months", 0);
    int years = inRequest.getParam("years", 0);
    Optional<String> keyName = inRequest.getParam("campaign");
    Optional<BaseCharacter> user = inRequest.getUser();

    if((minutes == 0 && hours == 0 && days == 0 && months == 0 && years == 0)
        || !keyName.isPresent() || !user.isPresent())
    {
      Log.warning("not possible to advance time for "
                      + minutes + "/" + hours + "/" + days + ", "
                      + keyName + ", " + user);
      return "??";
    }

    Optional<EntryKey> key =
        EntryKey.fromString(keyName.get().replace("_", "+").replace("-", " "));

    if(!key.isPresent())
    {
      Log.warning("cannot parse key " + keyName.get());
      return "??";
    }

    Optional<Campaign> campaign = DMADataFactory.get().getEntry(key.get());
    if(!campaign.isPresent())
    {
      Log.warning("cannot find campaign " + keyName.get());
      return "??";
    }

    Optional<CampaignDate> date =
        campaign.get().manipulateTime(minutes, hours, days, months, years);

    if(!date.isPresent())
    {
      Log.warning("campaign " + campaign.get().getName()
                      + " does not have a date");
      return "??";
    }

    Log.info("Changing campaign time to " + date.get());
    return date.get().getMonthFormatted()
        + "::" + date.get().getDayFormatted()
        + "::" + date.get().getYear()
        + "::" + date.get().getHoursFormatted()
        + "::" + date.get().getMinutesFormatted();

  }
}
