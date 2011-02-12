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
      width: 500,
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
    ajax("/actions/login", { username: inUsername, password: inPassword });

  if(result != "")
    $("#login-error").html(result);
  else
    // reload the page to show login
    reload();
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
  var result = ajax("/actions/logout", {});

  reload();
}

//..........................................................................

//---------------------------------- ajax ----------------------------------

/** Send a request to a server.
 *
 * @param  inURL      the url to send to
 * @param  inValues   the key value pairs with the data to send
 * @param  inFunction the function to call on an asynchronous request
 *
 * @return the result from the server (or an empty string for asynchronous
 *         requests)
 *
 */
function ajax(inURL, inValues, inFunction)
{
  var request;

  if(window.XMLHttpRequest)
    request = new XMLHttpRequest();
  else
    if(window.ActiveXObject)
    {
      try
      {
        request = new ActiveXObject("MSXML2.XMLHTTP");
      }
      catch(e)
      {
        try
        {
          request = new ActiveXObject("Microsoft.XMLHTTP");
        }
        catch(e2)
        {
          alert("Your browser is currently not supported!");
        }
      }
    }

  // build up the data
  var data = "";
  for(var key in inValues)
    data += key + "=" + encodeURIComponent(inValues[key]) + "\n";

  request.open("POST", inURL, inFunction !=  null);
  request.setRequestHeader("Content-Type",
                           "application/x-www-form-urlencoded");

  request.send(data);

  if(!inFunction)
    return request.responseText;

  // build up the arguments array
  var args = [ "<nothing yet>" ];

  for(var i = 3, argument; argument = arguments[i]; i++)
    args.push(argument);

  // we have an asynchronous request
  request.onreadystatechange = function()
    {
      if(request.readyState == 4)
      {
        args[0] = request.responseText;

        inFunction.apply(null, args);
      }
    }

  return request;
}

//..........................................................................
//--------------------------------- reload ---------------------------------

/** Reload the current page.
  *
  * @param inPage an optional page to use instead of the default one; this
  *               is only the page, without the path
  *
  */
reload = function(inPage)
{
  var destination = document.location.pathname;

  if(document.location.hash)
    destination = document.location.hash.substring(1);

  if(inPage)
    destination = destination.replace(/^(.*\/).*?$/, "$1" + inPage);

  document.location.href = destination;
}

//..........................................................................
//---------------------------- setupValidation -----------------------------

/**
 * Installs validation handlers in all input fields inside the given element.
 *
 * @param       inElement the element to look for input fields
 *
 */
function setupValidation(inElement)
{
  var inputs = $(':input', inElement).keyup(validate).each(validate);
}

//..........................................................................
//-------------------------------- validate --------------------------------

/**
 * Validate the input in a given field.
 *
 */
function validate()
{
  var value = this.value;
  var type = this.getAttribute('validate');
  var button = this.getAttribute('validateButton');

  if(!type)
    return;

  if(checkValue(type, value))
  {
    if($(this).hasClass('invalid'))
    {
      $(this).removeClass('invalid');
      $(button).each(enableButton);
    }
  }
  else
  {
    if(!$(this).hasClass('invalid'))
    {
      $(this).addClass('invalid');
      $(button).each(disableButton);
    }
  }
}

//..........................................................................
//------------------------------- checkValue -------------------------------

/**
 * Check if the given value is valid.
 *
 * @param       inType  the type of validation to do
 * @param       inValue the value to check
 *
 * @return      true if the value is valid, false if not
 *
 */
function checkValue(inType, inValue)
{
  switch(inType)
  {
    case 'non-empty':
      if(inValue)
        return true;

      return false;
  }

  return true;
}

//..........................................................................
//------------------------------ enableButton ------------------------------

/**
 * Enable the button, if all references enable it.
 *
 */
function enableButton()
{
  if(this.invalidCount)
    this.invalidCount--;

  if(!this.invalidCount)
    $(this).button('enable');
}

//..........................................................................
//------------------------------ enableButton ------------------------------

/**
 * Disable the button.
 *
 */
function disableButton()
{
  if(this.invalidCount)
    this.invalidCount++;
  else
    this.invalidCount = 1;

  $(this).button('disable');
}

//..........................................................................
