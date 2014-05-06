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
  $(':input', inElement).keyup(form.validate).each(form.validate);
};

//..........................................................................
//-------------------------------- validate --------------------------------

/**
 * Validate the input in a given (this) field.
 */
form.validate = function(inField)
{
  var field;
  if(!inField)
    field = this;
  else
    field = inField;

  var value = field.value;
  var type = field.getAttribute('validate');
  var button = field.getAttribute('validateButton');

  if(!type)
    return;

  if(form.checkValue(type, value))
  {
    if($(field).hasClass('invalid'))
    {
      $(field).removeClass('invalid');
      $(button).each(form.enableButton);
    }
  }
  else
  {
    if(!$(field).hasClass('invalid'))
    {
      $(field).addClass('invalid');
      $(button).each(form.disableButton);
    }
  }
};

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
    case 'any':
    return true;

    case 'non-empty':
      if(inValue)
        return true;

      return false;

    case 'name':
      return inValue && !inValue.match(/[\":,.;=\[\]\{\}\|\(\)]/);

    case 'isbn':
      var parts = inValue.match(/^(\d+-\d+-\d+)-([0-9xX])$/);
      if(!parts || inValue.length != 13)
        return false;

      var numbers = parts[1].replace(/-/g, "");
      var check = parts[2];

      var sum = 0;
      for(var i = 10, j = 0; i > 1; i--, j++)
        sum += numbers.charAt(j) * i;

      return check == "" + (11 - (sum % 11)) % 11;

    case 'isbn13':
      parts = inValue.match(/^(\d+-\d+-\d+-\d+)-(\d)$/)

      if(!parts && inValue.length != 17)
        return false;

      numbers = parts[1].replace(/-/g, "");
      check = parts[2];

      sum = 0;
      for(var i = 0; i < 12; i++)
        sum += numbers.charAt(i) * (i % 2 == 0 ? 1 : 3);

      return check == "" + (10 - (sum % 10)) % 10;

    case 'number':
      return inValue.match(/^\+?\d+$/);
      
    case 'money':
      return inValue.match(/^(\s*\d+\s*(pp|gp|sp|cp))*$/);

    case 'weight':
      return inValue.match
        (/^(\s*\d+\s*(lb|lbs|oz|pound|pounds|ounce|ounces))*$/);

    case 'modifier':
    return inValue.match(/^[+-]?\d+(\s+[a-zA-Z]+)*$/);

    case 'price':
      return inValue.match(/^[^\d\s]+\s?\d+(\.\d\d)?$/);

    case 'dice':
      return inValue && inValue.match(/^(\d+d\d+)?\s*?([+-]?\d+)?$/);

    case 'pages':
      if(!inValue.match(/^\d+(\s*-\s*\d+)?(\/\d+(\s*-\s*\d+)?)*$/))
        return false;

      parts = inValue.split('/');
      for(var i = 0; i < parts.length; i++)
      {
        var pages = parts[i].split(/\s*-\s*/);
        if(pages.length > 1 && Number(pages[0]) > Number(pages[1]))
          return false;
      }

      return true;
  }

  return true;
};

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
};

//..........................................................................
//------------------------------ disableButton -----------------------------

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
};

//..........................................................................
