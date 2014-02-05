'use strict';
angular.module('fdappApp').directive('commandEditor', ['blocklyService', 'blocklyCommandBlock', 'blocklyReactionBlock', 'blocklyTriggerBlock',
    function(blocklyService, blocklyCommandBlock, blocklyTriggerBlock, blocklyReactionBlock) {
        return {
            restrict: 'E',
            templateUrl: 'views/commandEditor.html',
            scope: {
                command: '=',
            },
            replace: true,
            link: function(scope, element, attrs) {
                blocklyService.blockly().then(function(blockly) {
                    blockly.inject(document.getElementById('blocklyDiv'), {
                        path: './',
                        toolbox: document.getElementById('toolbox'),
                    });
                    scope.toXML = function() {
                        var xml = blockly.Xml.workspaceToDom(blockly.mainWorkspace);
                        scope.xml = blockly.Xml.domToPrettyText(xml);
                        scope.json = scope.toPojo();
                    };
                    scope.$watch('command', function(command, OldValue, scope) {
                        if(command) {
                            scope.toBlock(command);
                        }
                    });
                    scope.toBlock = function(command) {
                        var workspace = blockly.getMainWorkspace();
                        workspace.clear();
                        var blk = blockly.Block.obtain(workspace, 'command');
                        blk.setFieldValue(command.receiver, 'RECEIVER');
                        blk.setFieldValue(command.name, 'NAME');
                        blk.setFieldValue(command.description, 'DESCRIPTION');
                        blk.initSvg();
                        
                        var stat = blk.getInput('PROPERTIES');
                        var props = command.properties.properties;
                        var latestChild;
                        for (var i=0; i<props.length; i++){
                            var prop = props[i];
                            var pb = blockly.Block.obtain(workspace, 'property');
                            pb.setFieldValue(prop['@name'],'NAME');
                            pb.setFieldValue(prop['@value'],'VALUE');
                            //pb.parent_ = blk;
                            pb.initSvg();
                            
                            if (i===0) {
                            stat.connection.connect(pb.previousConnection);
                            } else {
                                latestChild.nextConnection.connect(pb.previousConnection);
                            }
                            latestChild = pb;
                           
                        }
                        workspace.render();
                    };
                    scope.toPojo = function() {
                        var command = {};
                        var workspace = blockly.getMainWorkspace();
                        //alert(workspace.getTopBlocks().length);
                        var blk = workspace.getTopBlocks()[0];
                        //return blk.toString();
                        command.name = blk.getFieldValue('NAME');
                        command.description = blk.getFieldValue('DESCRIPTION');
                        command.receiver = blk.getFieldValue('RECEIVER');
                        command.properties = {};
                        command.properties.properties = [];
                        //alert('Chldren: ' + blk.getChildren().length);
                        var desc = blk.getDescendants();
                        for(var i = 0; i < desc.length; i++) {
                            var child = desc[i];
                           // alert(child.type);
                            if(child.type === 'property') {
                                var property = {};
                                property.name = child.getFieldValue('NAME');
                                property.value = child.getFieldValue('VALUE');
                                command.properties.properties.push(property);
                            }
                        }
                        return command;
                    };
                });
            }
        };
    }
]);
