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
 * This is a set of utility javascript routines.
 *
 * @file          util.js
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

/** The object to store everything in. */
var util = new Object();

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
util.ajax = function(inURL, inValues, inFunction)
{
  var request;

  if(window.XMLHttpRequest)
    request = new XMLHttpRequest();
  else
    if(window.ActiveXObject)
    {
      try
      {
        request = new ActiveXObject('MSXML2.XMLHTTP');
      }
      catch(e)
      {
        try
        {
          request = new ActiveXObject('Microsoft.XMLHTTP');
        }
        catch(e2)
        {
          alert('Your browser is currently not supported!');
        }
      }
    }

  // build up the data
  var data = '';
  for(var key in inValues)
    data += key + '=' + encodeURIComponent(inValues[key]) + '\n';

  request.open('POST', inURL, inFunction !=  null);
  request.setRequestHeader('Content-Type',
                           'application/x-www-form-urlencoded');

  request.send(data);

  if(!inFunction)
    return request.responseText;

  // build up the arguments array
  var args = [ '<nothing yet>' ];

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
util.reload = function(inPage)
{
  var destination = document.location.pathname;

  if(document.location.hash)
    destination = document.location.hash.substring(1);

  if(inPage)
    destination = destination.replace(/^(.*\/).*?$/, '$1' + inPage);

  document.location.href = destination;
}

//..........................................................................
//---------------------------------- link ----------------------------------

/**
  * Goto the specified target.
  *
  * @param       inEvent     the event that lead to this invocation
  * @param       inTarget    the URL to go to
  * @param       inFunction  an optional function to be called when the new
  *                          page was loaded
  */
util.link = function(inEvent, inTarget, inFunction)
{
  if(inEvent)
  {
    inEvent.preventDefault();
    inEvent.cancelBubble = true;
  }

  if(inTarget == null)
    inTarget = location.pathname;

  // only link via ajax if its an html file
  var matched = inTarget.match(/\/.*\.(.*?)$/);

  if(matched && matched[1] != 'html' && matched[1] != '')
  {
    location.href = inTarget;

    return false;
  }

  // check if we have prevented moving away
  if(window.onbeforeunload)
    if(!confirm('Do you really want to leave the page?\n\n'
                + window.onbeforeunload()))
      return false;

  window.onbeforeunload = null;

  var busy = new gui.Busy('Please wait while ', ['loading page']);

  // remove all current actions
//   $p('actions').innerHTML = '';

  // inform google analytics about this page change
  if(location.hostname != 'localhost')
    pageTracker._trackPageview(inTarget);

  var target = inTarget;
  if(!target.match(/\?/) && location.search)
    target += location.search;

  var bodyTarget = target;
  if(bodyTarget.match(/\?/))
    bodyTarget += '&body';
  else
    bodyTarget += '?body';

  util.ajax(bodyTarget, null, function(inText)
  {
    $('#page').html(inText);

    busy.done('loading page');
    delete busy;

    // Adding something to the innerHTML will not execute any javascript in it.
    inText.replace(/<script.*?>((\n|.)*?)<\/script>/g,
                   function(match, group) { eval(group) });

    if(inFunction)
      inFunction();

    if(!inEvent.state)
      window.history.pushState(target, document.title, target);

    // make all editable, if necessary
    //gui.makeEditable();
  });

  return false;
}

//..........................................................................

//---------------------------- replaceMainImage ----------------------------

/**
 * Replace the main image with the current one of this.
 *
 */
util.replaceMainImage = function()
{
  var main = $('DIV.mainimage IMG');
  util.mainImage = main.attr('src')
  main.attr('src', this.src);
}

//..........................................................................
//---------------------------- restoreMainImage ----------------------------

/**
 * Restore the main image with it's original value.
 *
 */
util.restoreMainImage = function()
{
  $('DIV.mainimage IMG').attr('src', util.mainImage);
}

//..........................................................................


//---------------------------------------------------------- extend existing

//------------------------------ Array.remove ------------------------------

/**
  * Remove the given value from the array.
  *
  * @param  inValue  the value to remove from the array
  *
  * @return the value removed or null if not found
  *
  */
Array.prototype.remove = function(inValue)
{
  for(var i = 0; i < this.length; i++)
    if(this[i] == inValue)
    {
      var deleted = this[i];

      this.splice(i, 1);

      return deleted;
    }

  return null;
};

//..........................................................................

//..............................................................................

// install a handler to support back/forward actions
$(window).bind('popstate', function(event) {
    util.link(event.originalEvent, location.pathname + location.search)
  });

