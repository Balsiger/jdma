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

//------------------------------------------------------------------- header

/**
 * This is a set of javascript routines for form handling.
 *
 * @file          form.js
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

/** The object to store everything in. */
var form = new Object();

//---------------------------- setupValidation -----------------------------

/**
 * Installs validation handlers in all input fields inside the given element.
 *
 * @param       inElement the element to look for input fields
 *
 */
form.setupValidation = function(inElement)
{
  var inputs = $(':input', inElement).keyup(validate).each(validate);
}

//..........................................................................
//-------------------------------- validate --------------------------------

/**
 * Validate the input in a given field.
 *
 */
form.validate = function()
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
form.checkValue = function(inType, inValue)
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
form.enableButton = function()
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
form.disableButton = function()
{
  if(this.invalidCount)
    this.invalidCount++;
  else
    this.invalidCount = 1;

  $(this).button('disable');
}

//..........................................................................
