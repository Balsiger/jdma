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


/**
 * This is the main DMA javascript file. It contains most javascript methods
 * used in all the HTML pages.
 *
 * @file          DMA.js
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//--------------------------------- login ----------------------------------

/**
 * Create the login dialog and ask the user to lo in.
 *
 */
function login()
{
  var loginDialog = $('<div id="login"/>')
    .html('Please provide username and password to log in:'
          + '<p>'
          + '<label>Username'
          + '<input type="text" name="username" validate="non-empty" '
          + 'validateButton="#login-button" size="30" maxlength="30">'
          + '</label>'
          + '<label>Password'
          + '<input type="password" name="password" validate="non-empty" '
          + 'validateButton="#login-button" size="30" maxlength="30">'
          + '</label>'
          + '<p>'
          + '<div id="login-error"/>')
    .dialog({
      title: 'DMA Login',
      modal: true,
      resizable: false,
      width: 300,
      dialogClass: 'login-dialog',
      buttons: [
          {
            id:  'login-button',
            text: 'Login',
            click: function() {
              doLogin($('input[name="username"]', this)[0].value,
                      $('input[name="password"]', this)[0].value);
            }
          },
          {
            text: 'Cancel',
            click: function() {
              $(this).dialog('close');
            }
          }
        ]
    });

  setupValidation(loginDialog);
}

//..........................................................................
//--------------------------------- logout ---------------------------------

/**
 * Logout the user.
 *
 */
function logout()
{
  doLogout();
}

//..........................................................................

//-------------------------------- doLogin ---------------------------------

/**
  *
  * Try to login into the server. If the login is successful, the page is
  * reloaded, otherwise an error message will be shown.
  *
  * @param       inUsername the username
  * @param       inPassword the password
  *
  */
function doLogin(inUsername, inPassword)
{
  var result =
    util.ajax("/actions/login", { username: inUsername, password: inPassword });

  if(result != "")
    $("#login-error").html(result);
  else
    // reload the page to show login
    util.reload();
}

//..........................................................................
//------------------------------- doLogout ---------------------------------

/**
  *
  * Logout the user from the server.
  *
  * @param       inUsername the username
  * @param       inPassword the password
  *
  */
function doLogout()
{
  var result = util.ajax("/actions/logout", {});

  util.reload();
}

//..........................................................................

