/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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
 * Script code for editing items.
 *
 * @file          item.js
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

/** The object to store everything in. */
var item = new Object();

//--------------------------------- create ---------------------------------

/**
 * Create an item for the given entry.
 *
 * @param   inCampaign the campaign to create the item in
 * @param   inStore    the key of the entry to store the item in (if any)
 *
 */
item.create = function(inCampaign, inStore)
{
  var entryDialog = $('<div id="create-item"/>')
    .html('<label>Base Items (comma separated)<br>'
          + '<input type="text" name="bases" validate="non-empty" '
          + 'validateButton="#create-button" size="50" maxlength="300">'
          + '</label><br>'
          + '<label>'
          + '<input type="checkbox" name="identified" value="yes">'
          + ' identified<br />'
          + '<label>Extensions (comma separated)<br>'
          + '<input type="text" name="extensions" validate="non-empty" '
          + 'validateButton="#create-button" size="50" maxlength="300">'
          + '</label>'
          + '<p>')
    .dialog({
      title: 'Item Creation',
      modal: true,
      resizable: false,
      width: 500,
      closeOnEscape: true,
      dialogClass: 'create-dialog dialog',
      buttons: [
          {
            id:  'create-button',
            text: 'Create',
            click: function() {
              $(this).dialog('close');
              util.link(event,
                        '/' + inCampaign + '/item/TEMPORARY?create' +
                        (inStore ? '&store=' + inStore : '') +
                        '&bases=' +
                        encodeURIComponent($('input[name="bases"]', this)
                                           [0].value) +
                        '&extensions=' +
                        encodeURIComponent($('input[name="extensions"]', this)
                                           [0].value) +
                        ($('input[name="identified"]', this)[0].checked ?
                         '&identified' : ''));
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
};

//..........................................................................
