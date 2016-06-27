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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Optional;

import net.ixitxachitls.dma.data.DMADataFactory;
import net.ixitxachitls.dma.entries.AbstractEntry;
import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.values.enums.Group;
import net.ixitxachitls.util.logging.Log;

/**
 * Action for registering a new user.
 *
 * @author balsiger@ixitxachitls.net (Peter Balsiger)
 */
public class RegisterAction extends Action
{
  public RegisterAction()
  {
    super("username", "realname");
  }

  @Override
  public String execute(Optional<String> inUserName,
                        Optional<String> inRealName)
  {
    if(!inUserName.isPresent())
      return "No username given";

    if(!inRealName.isPresent())
      return "No real name given";

    UserService userService = UserServiceFactory.getUserService();
    if(!userService.isUserLoggedIn())
    {
      return "You must login to register";
    }

    Optional<BaseCharacter> user = DMADataFactory.get().getEntry
        (AbstractEntry.createKey(inUserName.get(), BaseCharacter.TYPE));
    if(user.isPresent())
      return "Username allready used, choose a new one.";

    //Save the new user with the default group GUEST.
    BaseCharacter baseCharacter = new BaseCharacter(inUserName.get(), userService
        .getCurrentUser().getEmail());
    baseCharacter.setRealName(inRealName.get());
    baseCharacter.setGroup(Group.GUEST);
    baseCharacter.save();

    Log.event(inUserName.get(), "register", "user registered");

    return "";
  }
}
