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

/** The currently displayed notification messages. */
gui.pendingNotifications = 0;

//------------------------------- addAction --------------------------------

/**
  * Add an action to the page.
  *
  * @param       inName   the name of the action
  * @param       inTitle  the text to show when hovering
  * @param       inAction the function to execute on click
  *
  */
gui.addAction = function(inName, inTitle, inAction)
{
  var actions = $('#actions');

  // check if it's already there
  if(actions.data('actions') && actions.data('actions').contains(inName))
    return;

  // make sure it's actually shown
  if(!actions.data('actions') || actions.data('actions').length == 0) {
      $('#actions').show('fast');
      actions.data('actions', []);
  }

  var action = $('<div class="action-button" title="' + inTitle + '">' +
                 '<div class="sprite large" id="action-' + inName +
                 '"></div></div>');
  action.click(inAction);
  actions.append(action);
  actions.data('actions').push(inName);
};

//..........................................................................
//------------------------------ removeAction ------------------------------

/**
  * Remove an action from the page
  *
  * @param       inName   the name of the action
  *
  */
gui.removeAction = function(inName)
{
  var actions = $('#actions');
  if(!actions.data('actions'))
    return;

  $('#action-' + inName).remove();

  actions.data('actions').remove(inName);
  if(actions.data('actions').length == 0)
    actions.hide('fast');
};

//..........................................................................
//---------------------------------- info ----------------------------------

/** Show an alert message.
 *
 * @param inMessage the message to appear
 *
 */
gui.info = function(inMessage)
{
  gui.notification(inMessage, "/icons/info.png", "info");
}

//..........................................................................
//---------------------------------- alert ---------------------------------

/** Show an alert message.
 *
 * @param inMessage the message to appear
 *
 */
gui.alert = function(inMessage)
{
  gui.notification(inMessage, "/icons/alert.png", "alert");
}

//..........................................................................
//--------------------------------- debug ----------------------------------

/** Show an debug message.
 *
 * @param inMessage the message to appear
 *
 */
gui.debug = function(inMessage)
{
  gui.notification(inMessage, "/icons/bug.png", "debug");
}

//..........................................................................
//------------------------------- notificaton ------------------------------

/**
 * Show an notificaiton message.
 *
 * @param inMessage     the message to appear
 * @param inImage       the name of the image to display
 * @param inClass       the css class name for the alert
 *
 */
gui.notification = function(inMessage, inImage, inClass)
{
  // create the element
  var win = $('<div class="-gui-notification -gui-notification-' + inClass
              + '" '
              + 'style="z-index:' + (1000 - gui.pendingNotifications) + '">'
              + '<img class="-gui-notification-image" src="' + inImage + '"/>'
              + '<div class="-gui-notification-text">'
              + inMessage
              + '</div>'
              + '</div>');

  // increase pending counter
  gui.pendingNotifications++;

  // add a click handler to remove early
  win.click(function() { $(this).remove(); });

  // append to the page
  $('body').append(win);
  win.animate({ bottom: 0 }, 1000, 'swing').
  delay(5000 * gui.pendingNotifications).
  animate({ bottom: -150 }, 1000, 'swing',
          function() { $(this).remove(); gui.pendingNotifications--; });
}

//..........................................................................
//-------------------------------- delayed ---------------------------------

/**
 * Delay an action for some time.
 *
 * @param inAction the action to do at each step (the action returns false if
 *                 end is reached)
 * @param inDelay  the delay in miliseconds to wait before doing the action
 *
 * @return the reference to use to stop the action
 *
 */
gui.delayed = function(inAction, inDelay)
{
  var handle = new Object();

  handle.timeout = window.setTimeout(inAction, inDelay);

  return handle;
}

//..........................................................................
//--------------------------------- stop -----------------------------------

/**
 * Stop a delayed or repeated action.
 *
 * @param inReference the reference obtained by delay
 *
 */
gui.stop = function(inReference)
{
  if(!inReference)
    return;

  if(inReference.timeout)
  {
    window.clearTimeout(inReference.timeout);

    inReference.timeout = null;
  }
  else
    window.clearTimeout(inReference);
}

//..........................................................................
//----------------------------- setupHighlight -----------------------------

/**
 * Setup highlight for all images with highlight.
 *
 */
gui.setupHighlight = function()
{
  $('img.highlight').mouseover(gui.toggleHighlight);
  $('img.highlight').mouseout(gui.toggleHighlight);
}

//..........................................................................
//---------------------------- toggleHighlight -----------------------------

/**
 *
 *
 * @param
 *
 * @return
 *
 */
gui.toggleHighlight = function()
{
  var src = $(this).attr('src').match(/(.*?)(-highlight)?(\.png)/)

  if(src[2])
    $(this).attr('src', src[1] + src[3]);
  else
    $(this).attr('src', src[1] + '-highlight' + src[3]);
}

//..........................................................................

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
};

gui.Busy.prototype.add = function(inStep)
{
  this.steps.push(inStep);

  this._update();
};

gui.Busy.prototype.done = function(inStep)
{
  this.steps.remove(inStep);

  if(this.steps.length == 0)
    document.body.removeChild(this.display);
  else
    this._update();
};

gui.Busy.prototype._update = function()
{
  this.content.innerHTML = this.text + this.steps.join(", ") + "...";
};

//..........................................................................

// setup highlighting for images
$(document).ready(function () {
    gui.setupHighlight();
});
