'use strict';
angular.module('fdappApp').service('blocklyService', function blocklyService($document, $q, $rootScope) {
    var d = $q.defer();

    function onScriptLoad() {
        // Load client in the browser
        $rootScope.$apply(function() {
            d.resolve(window.Blockly);
        });
    }
    var scriptTag = $document[0].createElement('script');
    scriptTag.type = 'text/javascript';
    scriptTag.async = true;
    scriptTag.src = 'http://blockly.googlecode.com/svn/trunk/blockly_compressed.js';
    scriptTag.onreadystatechange = function() {
        if(this.readyState === 'complete') {
            onScriptLoad();
        }
    };
    scriptTag.onload = onScriptLoad;
    var s = $document[0].getElementsByTagName('body')[0];
    s.appendChild(scriptTag);
    return {
        blockly: function() {
            return d.promise;
        }
    };
}).service('blocklyCommandBlock', ['blocklyService', 'blocklyProperty',
    function(blocklyService, blocklyProperty) {
        blocklyService.blockly().then(function(blockly) {
            blockly.Blocks.command = {
                init: function() {
                    this.setHelpUrl('http://www.w3schools.com/jsref/jsref_length_string.asp');
                    this.setColour(230);
                    this.appendDummyInput().appendField('Command');
                    this.appendDummyInput().appendField('Name').appendField(new blockly.FieldTextInput('Name'), 'NAME');
                    this.appendDummyInput().appendField('Description').appendField(new blockly.FieldTextInput('Description'), 'DESCRIPTION');
                    this.appendDummyInput().appendField('Receiver').appendField(new blockly.FieldTextInput('Channel'), 'RECEIVER');
                    this.appendStatementInput('PROPERTIES').appendField('Properties');
                    this.setOutput(false);
                    this.setTooltip('A channel');
                }
            };
            blockly.Blocks.getCommand = {
                init: function() {
                    this.setHelpUrl('http://www.w3schools.com/jsref/jsref_length_string.asp');
                    this.setColour(80);
                    this.setOutput(false);
                    this.setPreviousStatement(true);
                    this.setNextStatement(true);
                    this.setTooltip('Get a command by its name');
                    this.appendDummyInput().appendField(new blockly.FieldTextInput('Command name'), 'CNAME');
                }
            };
            blockly.Blocks.commandSequence = {
                init: function() {
                    this.setHelpUrl('http://www.w3schools.com/jsref/jsref_length_string.asp');
                    this.setColour(90);
                    this.setOutput(false);
                    this.setPreviousStatement(true);
                    this.setNextStatement(true);
                    this.setTooltip('Prepare a command sequence');
                    this.appendDummyInput().appendField('Sequence');
                    this.appendStatementInput('COMMANDS');
                }
            };
        });
    }
]).service('blocklyTriggerBlock', ['blocklyService', 'blocklyStatement',
    function(blocklyService, blocklyStatement) {
        blocklyService.blockly().then(function(blockly) {
            blockly.Blocks.trigger = {
                init: function() {
                    this.setHelpUrl('http://www.w3schools.com/jsref/jsref_length_string.asp');
                    this.setColour(0);
                    this.appendDummyInput().appendField('Trigger');
                    this.appendDummyInput().appendField('Name').appendField(new blockly.FieldTextInput('Name'), 'NAME');
                    this.appendDummyInput().appendField('Description').appendField(new blockly.FieldTextInput('Description'), 'DESCRIPTION');
                    this.appendDummyInput().appendField('Channel').appendField(new blockly.FieldTextInput('Channel'), 'CHANNEL');
                    this.appendStatementInput('PAYLOAD').appendField('Conditions');
                    this.setOutput(false);
                    this.setTooltip('A trigger');
                }
            };
            blockly.Blocks.getTrigger = {
                init: function() {
                    this.setHelpUrl('http://www.w3schools.com/jsref/jsref_length_string.asp');
                    this.setColour(80);
                    this.setOutput(true, 'String');
                    this.setTooltip('Get a trigger by its name');
                    this.appendDummyInput().appendField(new blockly.FieldTextInput('Trigger name'), 'TNAME');
                }
            };
        });
    }
]).service('blocklyReactionBlock', ['blocklyService',
    function(blocklyService) {
        blocklyService.blockly().then(function(blockly) {
            blockly.Blocks.reaction = {
                init: function() {
                    this.setHelpUrl('http://www.w3schools.com/jsref/jsref_length_string.asp');
                    this.setColour(400);
                    this.appendDummyInput().appendField('Reaction');
                    this.appendValueInput('TRIGGER').appendField('when').setCheck('String');
                    this.setOutput(false);
                    this.appendStatementInput('DO').appendField('do').setCheck(['Command', 'Sequence']);
                    this.setTooltip('Automation');
                }
            };
        });
    }
]).service('blocklyProperty', ['blocklyService',
    function(blocklyService) {
        blocklyService.blockly().then(function(blockly) {
            blockly.Blocks.property = {
                init: function() {
                    this.setHelpUrl('http://www.w3schools.com/jsref/jsref_length_string.asp');
                    this.setColour(160);
                    this.appendDummyInput().appendField('Property');
                    this.appendDummyInput().appendField(new blockly.FieldTextInput('Name'), 'NAME');
                    this.appendDummyInput().appendField(new blockly.FieldTextInput('Value'), 'VALUE');
                    this.setOutput(false);
                    this.setPreviousStatement(true);
                    this.setNextStatement(true);
                    this.setInputsInline(true);
                    this.setTooltip('Returns a property');
                }
            };
        });
    }
]).service('blocklyStatement', ['blocklyService',
    function(blocklyService) {
        blocklyService.blockly().then(function(blockly) {
            blockly.Blocks.fdStatement = {
                init: function() {
                    this.setHelpUrl('http://www.w3schools.com/jsref/jsref_length_string.asp');
                    this.setColour(180);
                    this.appendDummyInput().appendField('Statement');
                    this.appendDummyInput().appendField(new blockly.FieldTextInput('Logical'), 'LOGICAL');
                    this.appendDummyInput().appendField(new blockly.FieldTextInput('Operator'), 'OPERATOR');
                    this.appendDummyInput().appendField(new blockly.FieldTextInput('Name'), 'NAME');
                    this.appendDummyInput().appendField(new blockly.FieldTextInput('Value'), 'VALUE');
                    this.setPreviousStatement(true);
                    this.setNextStatement(true);
                    this.setInputsInline(true);
                    this.setOutput(false);
                    this.setTooltip('A statement');
                }
            };
        });
    }
]);