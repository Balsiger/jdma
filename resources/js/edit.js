/******************************************************************************
 * Copyright (c) 2002-2007 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
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
 * Script code for editing values.
 *
 * @file          edit.js
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

/** The object to store everything in. */
var edit = new Object();

/** All the values currently edited. */
edit.all = [];

//----------------------------- makeEditable -------------------------------

/**
 * Make the 'this' element editable.
 */
edit.makeEditable = function()
{
  var target = edit.findTarget(this);

  if(!target.__edit)
    target.__edit = [];

  target.__edit.push(this);

  $(target).dblclick(edit.edit);
  $(target).bind('contextmenu', edit.edit);

  if(location.search == '?create')
  {
    edit.editAll();
    $(':input')[0].focus();
    window.scroll(0, 0);
  }
}

//..........................................................................
//---------------------------------- edit ----------------------------------

/**
 * Edit this field.
 *
 * @param inEvent the event that led to the edit
 *
 */
edit.edit = function(inEvent, inElement)
{
  var element = inElement || this;

  // clear a possible selection
  util.clearSelection();

  // remove the handlers
  $(element).unbind('dblclick', edit.edit);
  $(element).unbind('contextmenu', edit.edit);

  for(var i = 0; i < element.__edit.length; i++)
    edit.editValue(element.__edit[i], element);

  gui.addAction('save', 'Save', edit.save);
  gui.addAction('cancel', 'Cancel', util.link);

  return false;
};

//..........................................................................
//------------------------------- editValue --------------------------------

/**
 * Edit the dma value.
 *
 * @param       inEditable the field to edit
 * @param       inTarget   the target container
 *
 */
edit.editValue = function(inEditable, inTarget)
{
  var editable = edit.Base.create(inEditable);

  $(inEditable).after(editable.getElement());
  $(inEditable).hide();
  inTarget.__edit = editable;
};

//..........................................................................
//------------------------------- findTarget -------------------------------

/**
 * Find the target for editing.
 *
 * @param   inElement the dma editable field
 *
 * @return  the element to edit
 *
 */
edit.findTarget = function(inElement)
{
  var parent = $(inElement).parent('.value-content');
  if(parent.length)
    return parent[0];

  return inElement;
};

//..........................................................................
//-------------------------------- editAll ---------------------------------

/**
 * Edit all values on the page.
 */
edit.editAll = function()
{
  var targets = [];
  $('dmaeditable').each(function()
  {
    var target = edit.findTarget(this);
    if(!targets.contains(target))
    {
      edit.edit(null, target);
      targets.push(target);
    }
  });
};

//..........................................................................
//---------------------------------- save ----------------------------------

/**
 * Save all the current edits.
 *
 */
edit.save = function()
{
  var values = { };

  for(var i = 0, editable; editable = edit.all[i]; i++)
    editable.save(values);

  window.console.log('saving', values);
  // send the data to the server
  util.ajax('/actions/save', values,
            function(inResult) { window.console.log("saved: " + inResult);
              eval(inResult); });

  // remove the move away code
  window.onbeforeunload = undefined;
};

//..........................................................................
//-------------------------------- unparsed --------------------------------

/** Show that some part of an input field could not be parsed.
 *
 * @param  inEntry the entry type of the error
 * @param  inID    the id of the entry with the error
 * @param  inKey   the name of the key that could not be parsed
 * @param  inText  the text that could not be parsed
 *
 */
edit.unparsed = function(inEntry, inID, inKey, inText)
{
  var editable =
    $('dmaeditable[key=' + inKey+ '][entry=' + inEntry + '][id=' + inID + ']');

  editable.addClass('unparsed');

//     if(element.getAttribute("key") == inKey)
//     {
//       element.__unparsed = inText;
//       element.innerHTML += "<span class='error'>" + inText + "</span>";
//       element.style.display = "";
//       element.parentNode.removeChild(element.__edit._element);

//       break;
//     }
}

//..........................................................................
//--------------------------------- reload ---------------------------------

/**
 * Redo all edit setup for a page (after a page refresh).
 *
 */
edit.refresh = function()
{
  edit.all = [];
  $('dmaeditable').each(edit.makeEditable);
  gui.removeAction('save');
  gui.removeAction('cancel');
};

//..........................................................................

//--------------------------------------------------------------------- Base

/**
 * The base object for ediable values
 *
 * @param inEditable the editable element create from
 * @param inID       the id of the entry edited
 * @param inEntry    the type of the entry edited
 * @param inKey      the key uniquely defining the value
 * @param inType     the type of the editable
 * @param inValue    the initial value
 * @param inLeader   the text to put in front of the value
 * @param inLabel    the label for the field
 * @param inNote     any note for editing
 *
 */
edit.Base = function(inEditable, inID, inEntry, inKey, inType, inValue,
                     inLabel, inNote)
{
  this.editable = inEditable;
  this.id       = inID,
  this.entry    = inEntry;
  this.key      = inKey;
  this.type     = inType;
  this.label    = inLabel;
  this.note     = inNote;
  this._value   = (inValue == '$undefined$' ? '' : inValue);
  this._defined = inValue != '$undefined$';

  this._element   = this._createElement();
  this._unlabeled = this._element;

  if(this.key)
    edit.all.push(this);
};

/**
 * Create an editable object for the given editable tag.
 *
 * @param  inElement the editable element to create from
 *
 * @return the edit object of the right type
 */
edit.Base.create = function(inElement)
{
  var properties = edit.Base._parse(inElement);

  window.console.log("properties", properties);
  switch(properties.type)
  {
    case 'name':
      return new edit.Name(inElement, properties.id, properties.entry,
                           properties.key, properties.type, properties.value,
                           properties.label, properties.note);

    case 'string':
      return new edit.String(inElement, properties.id, properties.entry,
                             properties.key, properties.type, properties.value,
                             properties.label, properties.note);

    case 'selection':
      return new edit.Selection(inElement, properties.id, properties.entry,
                                properties.key, properties.type,
                                properties.value, properties.values,
                                properties.label, properties.note);
  }

  window.alert('Could not find edit object for ' + properties.type);
};

/**
 * Get the element that renders the editable.
 *
 * @return the element rendered
 */
edit.Base.prototype.getElement = function()
{
  return this._element;
};

edit.Base.prototype.isDefined = function()
{
  return this._defined;
};

edit.Base.prototype.getValue = function()
{
  return this._getValue();
};

/**
 * Parse the properties of the dma editable.
 *
 * @param     inEditable the field with the properties
 *
 * @return    an object with all the properties extracted
 *
 */
edit.Base._parse = function(inEditable)
{
  var properties = {
    id: inEditable.getAttribute('id'),
    entry: inEditable.getAttribute('entry'),
    type: inEditable.getAttribute('type'),
    value: inEditable.getAttribute('value'),
    key: inEditable.getAttribute('key'),
    script: inEditable.getAttribute('script'),
    values: inEditable.getAttribute('vaues'),
    note: inEditable.getAttribute('note'),
  };

  // extract subtype, if any 'type#subtype'
  var types = properties.type.match(/^((?:\n|.)+?)\#((?:\n|.)+)$/);

  if(types)
  {
    properties.type    = types[1];
    properties.subtype = types[2];
  }

  // extract type options, if any 'type(option)type'
  types = properties.type.match(/(.*?)\(((?:\n|.)*)\)(.*)/);

  if(types)
  {
    properties.type    = types[1] + types[3];
    properties.options = types[2];
  }

  // extract label if any 'type[label]'
  types = properties.type.match(/(.*)\[(.*)\]/);

  if(types)
  {
    properties.type  = types[1];
    properties.label = types[2];
  }

  // extract values if any
  var values = inEditable.getAttribute('values');
  if(values)
    properties.values = values.split('||');

  return properties;
};

/**
 * Save the editable and it's value to the given object
 *
 * @param inValues where to store the value(s)
 *
 */
edit.Base.prototype.save = function(inValues)
{
  var value;
  if(this.isDefined())
    value = this.getValue();
  else
    value = '$undefined$';

  inValues[this.entry + '::' + this.id + '::' + this.key] = value;
};

/**
 * Make the value undefined.
 */
edit.Base.prototype._undefine = function()
{
  this._defined = false;
};

/**
 * Make the value defined.
 */
edit.Base.prototype._define = function()
{
  this._defined = true;
};

/**
 * Cancel editing.
 */
edit.Base.prototype._cancel = function()
{
  $(this.editable).show();
  $(this._element).remove();

  // remove from the list of pending changes
  edit.all.remove(this);
  if(edit.all.length == 0)
    gui.removeAction('save');

  edit.findTarget(this.editable).__edit = null;
  $(this.editable).each(edit.makeEditable);
};


//..........................................................................
//-------------------------------------------------------------------- Field

/**
 * The base object for editable values with a single field.
 *
 * @param inEditable the editable element create from
 * @param inID       the id of the entry edited
 * @param inEntry    the type of the entry edited
 * @param inKey      the key uniquely defining the value
 * @param inType     the type of the editable
 * @param inValue    the initial value
 * @param inLabel    the label to set
 * @param inNote     any note for editing
 *
 */
edit.Field = function(inEditable, inID, inEntry, inKey, inType, inValue,
                      inLabel, inNote)
{
  edit.Base.call(this, inEditable, inID, inEntry, inKey, inType, inValue,
                 inLabel, inNote);

  this.hasFocus = false;
  this.hasMouse = false;

  if(!this._field)
    this._field = this._unlabeled;

  this._field.addClass('edit ' + this.type).
    attr('value', this._value).
    attr('name', this.key).
    attr('id', 'field-' + this.key).
    data('editable', this);

  // add a container around the field for all the additional values
  this._element =
    $(this._element).wrap('<div class="edit-container"></div>').parent();

  // add a note, if necessary
  if(this.note)
    this._element.prepend('<div class="edit-note edit-dynamic">' + this.note
                          + '</div>');

  // add the button to undefine a value
  $('<div class="icon edit-undefine edit-dynamic" title="Undefine"></div>').
  prependTo(this._element).
  click(this._undefine.bind(this));

  $('<div class="icon edit-cancel edit-dynamic" title="Cancel"></div>').
  prependTo(this._element).
  click(this._cancel.bind(this));

  this._element.mouseover(this._updateDecoration.bind(this, undefined, true));
  this._element.mouseout(this._updateDecoration.bind(this, undefined, false));
  this._field.focus(this._updateDecoration.bind(this, true, undefined));
  this._field.blur(this._updateDecoration.bind(this, false, undefined));
  this._field.keypress(this._define.bind(this));

  form.setupValidation(this._element);
};
extend(edit.Field, edit.Base);

/**
 * Update the sate of decoration elements for editing.
 *
 * @param inFocus true if the field has focus, false if not
 * @param inMouse true if the filed has mouse over, false if not
 */
edit.Field.prototype._updateDecoration = function(inFocus, inMouse) {
  if(inFocus != undefined)
    this._hasFocus = inFocus;

  if(inMouse != undefined)
    this._hasMouse = inMouse;

  if(this._hasMouse || this._hasFocus)
    this._element.find('.edit-dynamic').show();
  else
    this._element.find('.edit-dynamic').hide();
};

/**
  * Get the value entered for this editable.
  *
  * @return A string with the value for this editable.
  *
  */
edit.Field.prototype._getValue = function()
{
  return this._field.attr('value');
};

/**
 * Make the value undefined.
 */
edit.Field.prototype._undefine = function()
{
  edit.Field._super._undefine.call(this);

  $(this._field).attr('value', '');
  $(this._field).addClass('edit-undefined');
};

/**
 * Make the value defined.
 */
edit.Field.prototype._define = function()
{
  edit.Field._super._define.call(this);

  $(this._field).removeClass('edit-undefined');
};

/**
  * Set the width of the field.
  *
  * @param inWidth the new width to set
  *
  */
edit.Field.prototype.setWidth = function(inWidth)
{
  this._field.style.width = inWidth + '%';
};

/**
 * Set the focus to this field.
 *
 */
edit.Field.prototype.focus = function()
{
  this._field.focus();
};

//..........................................................................
//--------------------------------------------------------------------- Name

/**
 * An object representing an editable name field.
 *
 * @param inEditable the editable element create from
 * @param inID       the id of the entry for the value
 * @param inEntry    the type of the entry edited
 * @param inKey      the key uniquely defining the value
 * @param inType     the type of the editable
 * @param inValue    the initial
 * @param inLabel    the field's label
 * @param inNote     any note for editing
 *
 */
edit.Name = function(inEditable, inID, inEntry, inKey, inType, inValue,
                     inLabel, inNote)
{
  edit.Field.call(this, inEditable, inID, inEntry, inKey, inType,
                  inValue.removeNewlines(), inLabel, inNote);
};
extend(edit.Name, edit.Field);

/**
  * Create the element associated with this editable.
  *
  * @return the html element created
  */
edit.Name.prototype._createElement = function()
{
  return $('<input class="edit-field" validate="name"/>');
};

//..........................................................................
//------------------------------------------------------------------- String

/**
 * An object representing an editable string field.
 *
 * @param inEditable the editable element create from
 * @param inID       the id of the entry for the value
 * @param inEntry    the type of the entry edited
 * @param inKey      the key uniquely defining the value
 * @param inType     the type of the editable
 * @param inValue    the initial
 * @param inLabel    the field's label
 * @param inNote     any note for editing
 *
 */
edit.String = function(inEditable, inID, inEntry, inKey, inType, inValue,
                       inLabel, inNote)
{
  edit.Field.call(this, inEditable, inID, inEntry, inKey, inType,
                  inValue.replace(/^\s*\"([\s\S\n]*)\"\s*$/, "$1"), inLabel,
                  inNote);
};
extend(edit.String, edit.Field);

/**
  * Create the element associated with this editable.
  *
  * @return the html element created
  */
edit.String.prototype._createElement = function()
{
  return $('<input class="edit-field" validate="string"/>');
};

/**
 * Get the value of the field.
 *
 * @return the fields value, ready for storing.
 */
edit.String.prototype._getValue = function()
{
  var value = edit.String._super._getValue.call(this);
  value = value.replace(/(["'])/g, '\\$1'); // '"
  return "\"" + value + "\"";
};

//..........................................................................
//---------------------------------------------------------------- Selection

/**
  * An object representing an editable selection field.
  *
 * @param inEditable  the editable element create from
 * @param inID        the id of the entry for the value
 * @param inEntry     the type of the entry edited
 * @param inKey       the key uniquely defining the value
 * @param inType      the type of the editable
 * @param inValue     the initial
 * @param inSelectons an array with all the selectable values (each value can
 *                    be given as x::y, where x will be displayed, but y will
 *                    be stored)
 * @param inLabel     the field's label
 * @param inNote      any note for editing
 *
 */
edit.Selection = function(inEditable, inID, inEntry, inKey, inType, inValue,
                          inSelections, inLabel, inNote)
{
  // this is used in the constructor
  this._selections = inSelections;

  edit.Field.call(this, inEditable, inID, inEntry, inKey, inType, inValue,
                  inLabel, inNote);
};
extend(edit.Selection, edit.Field);

/**
  * Create the element associated with this editable.
  *
  * @return the html element created
  */
edit.Selection.prototype._createElement = function()
{
  var element = $('<select class="edit-field" />');

  if(this._selections)
    for(var i = 0; i < this._selections.length; i++)
    {
      var parts = this._selections[i].split("::");

      var text  = parts[0];
      var value = parts[0];

      if(parts.length > 1)
        value = parts[1];

      var option = $('<option value="' + value + '">' + text + '</option>');

      element.append(option);

      if(value == this._value)
        element.attr('selectedIndex', i);
    }

  return element;
};

//..........................................................................


// make the form fields editable
$(document).ready(edit.refresh());
