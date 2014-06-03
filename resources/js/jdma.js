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

//------------------------------- register ---------------------------------

/**
 * Create the registration dialog and ask the user to register.
 *
 */
function register()
{
  var registrationDialog = $('<div id="register"/>')
    .html('Please provide username and real name to register:'
          + '<p>'
          + '<label>Username'
          + '<input type="text" name="username" validate="non-empty" '
          + 'validateButton="#register-button" size="30" maxlength="30">'
          + '</label>'
          + '<p>'
          + '<p>'
          + '<label>Real name'
          + '<input type="text" name="realname" validate="non-empty" '
          + 'validateButton="#register-button" size="30" maxlength="30">'
          + '</label>'
          + '<p>'
          + '<div id="register-error"/>')
    .dialog({
      title: 'DMA Registration',
      modal: true,
      resizable: false,
      width: 300,
      dialogClass: 'register-dialog dialog',
      buttons: [
          {
            id:  'register-button',
            text: 'Registration',
            click: function() {
              doRegister($('input[name="username"]', this).val(),
                         $('input[name="realname"]', this).val());
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

  // submit on return if valid
  registrationDialog.keyup(function(event) {
      if(event.keyCode == 13 && !$('#register-button').is(':disabled'))
        $('#register-button').click();
    });


  form.setupValidation(registrationDialog);
}

//..........................................................................
//------------------------------ createEntry -------------------------------

/**
 * Create a new entry.
 */
function createEntry()
{
  var entryDialog = $('<div id="create-entry"/>')
    .html('<label>ID<br>'
          + '<input type="text" name="id" validate="name" '
          + 'validateButton="#create-button" size="30" maxlength="30">'
          + '</label>'
          + '<p>')
    .dialog({
      title: 'Entry Creation',
      modal: true,
      resizable: false,
      width: 300,
      closeOnEscape: true,
      dialogClass: 'create-dialog dialog',
      buttons: [
          {
            id:  'create-button',
            text: 'Create',
            click: function() {
              $(this).dialog('close');
              util.link(event,
                        $('input[name="id"]', this)[0].value + '?create');
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

  // submit on return if valid
  entryDialog.keyup(function(event) {
      if(event.keyCode == 13 && !$('#create-button').is(':disabled'))
        $('#create-button').click();
    });

  form.setupValidation(entryDialog);
}

//..........................................................................
//------------------------------ removeEntry -------------------------------

/**
 * Remove the entry with the given id from the data store.
 *
 * @param       inID the id of the entry to remove
 *
 */
function removeEntry(inID)
{
  gui.alert('Not yet implemented');
}

//..........................................................................
//----------------------------- doRegistration -----------------------------

/**
  *
  * Try to register a new user. If the registration is successful, the page is
  * reloaded, otherwise an error message will be shown.
  *
  * @param       inUsername the username
  *
  */
function doRegister(inUsername, inRealName)
{
  var result =
    util.ajax("/actions/register",
              { username: inUsername, realname: inRealName });

  if(result != "")
    $("#register-error").html(result);
  else
    // reload the page to show registration
    util.reload();
}

//..........................................................................
//--------------------------------- ready ----------------------------------

/**
 * Called when the document is ready (or a new dynamic 'page' is loaded)
 *
 */
function ready()
{
  // setup sections for extensions
  $('div.section-title').click(function() {
      $(this).parent().toggleClass('rotated');
      if($(this).parent().hasClass('rotated'))
      {
        $(this).css('width', $(this).parent().height() + 'px');
        $(this).css('min-width', $(this).parent().height() + 'px');
      }
      else
        $(this).css('width', 'auto');
    });

  $('div.section-title').click();
}

//..........................................................................

/**
 * Load and show the details for the given entry.
 *
 * @param    inPath the key of the entry to show
 */
function details(inPath)
{
  var dialog = $('<div class="details-card"/>')
    .html("Loading...")
    .dialog({
      modal: true,
      width: 600,
      resizable: false,
      dialogClass: 'card-inline'
      });

  util.ajax(inPath + '?body&card', null, function(html) {
      dialog.html(html);
    });
}

//----- admin --------------------------------------------------------------

var admin = {};

admin.addEvent = function(inText, inSeconds)
{
  $('#admin-events').append('<div class="event"><div class="date">' +
                            util.niceDate(inSeconds)
                            + '</div><div class="text">' +
                            inText + '</div></div>');
};

admin.addLog = function(inType, inText, inSeconds)
{
  $('#admin-logs').append('<div class="log ' + inType + '">' +
                          '<div class="date">' + util.niceDate(inSeconds) +
                          '</div><div class="text">' + inText + '</div>' +
                          '</div></div>');
};

admin.show = function(inType)
{
  $('#admin-logs div.DEBUG').hide();
  $('#admin-logs div.' + inType).show();
};

admin.resetIndexes = function(inType)
{
  if(!inType)
    return;

  util.ajax('/admin', { 'reset': inType }, null, true);
};

admin.clearCache = function()
{
  util.ajax('/admin', { 'cache': 'clear' }, null, true);
};

admin.refresh = function(inType)
{
  util.ajax('/admin', { 'refresh': inType }, null, true);
};

//..........................................................................
//----- jQuery extensions --------------------------------------------------
//..........................................................................


// if there are sections on the page, install a handler for them and open them
$(document).ready(function () {
    ready();
});


