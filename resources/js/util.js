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
 * @param  inEval     if the result should be evalued (parsed)
 *
 * @return the result from the server (or an empty string for asynchronous
 *         requests)
 *
 */
util.ajax = function(inURL, inValues, inFunction, inEval)
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
  request.setRequestHeader('Content-Type', 'application/octet-stream');

  request.send(data);
  if(!inFunction)
    if(inEval)
      return eval(request.responseText);
    else
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
        if(inEval)
          args[0] = eval(request.responseText);
        else
          args[0] = request.responseText;

        inFunction.apply(null, args);
      }
    }

  return request;
};

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
};

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
  if(inTarget && inTarget.match('^javascript:'))
    return true;

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

  // inform google analytics about this page change
  if(location.hostname != 'localhost')
    pageTracker._trackPageview(inTarget);

  var target = inTarget;
  if(!target.match(/\?/))
  {
    var search = location.search;
    search = util.removeQueryParam(search, 'create');
    search = util.removeQueryParam(search, 'start');
    search = util.removeQueryParam(search, 'end');

    if(search)
      target += search;
  }

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
    try
    {
      inText.replace(/<script.*?>((\n|.)*?)<\/script>/g,
                     function(match, group) { eval(group); });
    }
    catch(e)
    {
      gui.alert('Error when replaying javascript: ' + e);
    }

    if(inFunction)
      inFunction();

    if(!inEvent || !inEvent.state)
      window.history.pushState(target, document.title, target);

    edit.refresh();
    gui.setupHighlight();
  });

  return false;
};

//..........................................................................
//--------------------------------- linkRow --------------------------------

/**
  * Goto the specified target.
  *
  * @param       inElement   the cell with the row to link from
  * @param       inTarget    the URL to go to
  *
  */
util.linkRow = function(inElement, inTarget)
{
  if(!inElement)
    return;

  // lookup the parent row
  for(var row = inElement.parentNode; row; row = row.parentNode)
    if(row.tagName == 'TR')
      break;

  if(!row)
  {
    gui.alert('Cannot find parent table row for click linking for ' + inTarget);
    return;
  }

  row.onclick = function(event) {
    return util.link(event, inTarget);
  };
};

//..........................................................................
//----------------------------- clearSelection -----------------------------

/**
 * Clears the current selection, if any.
 *
 */
util.clearSelection = function()
{
  if(document.selection && document.selection.empty)
    document.selection.empty();
  else if(window.getSelection)
    window.getSelection().removeAllRanges();
};

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
};

//..........................................................................
//---------------------------- restoreMainImage ----------------------------

/**
 * Restore the main image with it's original value.
 *
 */
util.restoreMainImage = function()
{
  $('DIV.mainimage IMG').attr('src', util.mainImage);
};

//..........................................................................
//---------------------------- removeQueryParam ----------------------------

/**
 * Remove the query param (with value) from the given url/search string, if it
 * is there at all.
 *
 * @param       inText the url or search text to replace in
 * @param       inName the query param to remove
 *
 * @return      the replace string that can be used as a query param
 *
 */
util.removeQueryParam = function(inText, inName)
{
  var text = inText.replace(new RegExp('[?&]' + inName + '(=[^&]*)?', 'g'), '');
  text = text.replace(/^&/, '?');
  return text;
}

//..........................................................................

//--------------------------------- extend ---------------------------------

/**
  * Copy all properties from the given source object to the destination object,
  * thus gaining a crude, static kind of inheritance.
  *
  * @param  ioDestination the destination to copy to
  * @param  inSource      the source object to copy from
  *
  * @return the destination object again
  *
  */
function extend(inSubClass, inBaseClass)
{
  function tempConstructor() {};
  tempConstructor.prototype = inBaseClass.prototype;
  inSubClass._super = inBaseClass.prototype;
  inSubClass.prototype = new tempConstructor();
  inSubClass.prototype.constructor = inSubClass;
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
//----------------------------- Array.contains -----------------------------

/**
  * Check if an array contains the given element.
  *
  * @param  inElement the element to check for
  *
  * @return true if the element is there, false if not
  *
  */
Array.prototype.contains = function(inElement)
{
  if(!inElement)
    return false;

  for(var i = 0, length = this.length; i < length; i++)
    if(inElement == this[i])
      return true;

  return false;
};

//..........................................................................
//------------------------- String.removeNewlines --------------------------

/**
  * Compact the white space in the string and return it.
  *
  * @return the same string, but with all newlines removed.
  *
  */
String.prototype.removeNewlines = function()
{
  return this.replace(/\s*\n\s*/g, " ");
};

//..........................................................................
//------------------------------ String.trim -------------------------------

/**
  * Remove leading and trailing white space.
  *
  * @return the same string, but trimmed
  *
  */
String.prototype.trim = function()
{
  return this.replace(/^\s+/, "").replace(/\s+$/, "");
};

//..........................................................................
//------------------------------- Array.from -------------------------------

/**
  * Create an array from the given iterable.
  *
  * @param  inIterable the iterable to create the array from
  *
  * @return an array with the values of the iterable
  *
  */
Array.from = function(inIterable)
{
  if(!inIterable)
    return [];

  if(inIterable.toArray)
    return inIterable.toArray();

  var results = [];

  for(var i = 0, length = inIterable.length; i < length; i++)
    results.push(inIterable[i]);

  return results;
};

//..........................................................................
//----------------------------- Function.bind ------------------------------

/**
 * Bind the function to the given object and arguments.
 *
 * @param  the object to bind to
 * @param  ... any other arguments
 *
 * @return a function that can be called with the appropriate binding
 *
 */
Function.prototype.bind = function()
{
  var method   = this;
  var args     = Array.from(arguments);
  var object   = args.shift();

  return function()
  {
    return method.apply(object, args.concat(Array.from(arguments)));
  }
};

//..........................................................................

//..............................................................................

// install a handler to support back/forward actions
$(window).bind('popstate', function(event) {
    window.console.log("pop", event.originalEvent.state, event.originalEvent,
                       location.pathname, location.search);
    if(event.originalEvent.state)
      util.link(event.originalEvent, location.pathname + location.search)
  });

// add a history state for the first page to be able to come back here
$(document).ready(function () {
    window.history.pushState(location.pathName + location.search,
                             document.title, null);
});
