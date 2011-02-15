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
 * This is a set of javascript routines for gui related functions.
 *
 * @file          gui.js
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

/** The object to store everything in. */
var gui = new Object();

//----- Busy ---------------------------------------------------------------

/**
  * The class represents a busy status to be displayed.
  *
  * @param       inText  The text to show for busy (will be followed by steps).
  * @param       inSteps An array with all the steps to show.
  *
  */
gui.Busy = function(inText, inSteps)
{
  this.text  = inText;
  this.steps = inSteps;

  this.display = document.createElement('div');
  this.display.className = 'busy';

  var loading = document.createElement('img');
  loading.src = '/icons/loading.gif';
  loading.alt = 'Loading...';

  this.display.appendChild(loading);

  this.content = document.createElement('span');

  this.display.appendChild(this.content);

  document.body.appendChild(this.display);

  this._update();
}

gui.Busy.prototype.add = function(inStep)
{
  this.steps.push(inStep);

  this._update();
}

gui.Busy.prototype.done = function(inStep)
{
  this.steps.remove(inStep);

  if(this.steps.length == 0)
    document.body.removeChild(this.display);
  else
    this._update();
}

gui.Busy.prototype._update = function()
{
  this.content.innerHTML = this.text + this.steps.join(", ") + "...";
}

//..........................................................................
