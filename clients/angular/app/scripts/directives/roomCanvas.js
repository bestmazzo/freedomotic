'use strict';
angular.module('fdappApp').directive('roomCanvas', ['$rootScope',
    function($rootScope) {
        return {
            restrict: 'E',
            scope: {
                zones: '=',
                env: '=',
                mode: '@'
            },
            link: function link(scope, element, attrs) {
                var id = attrs['id'];
                //create random unique id for element
                if(!id) {
                    id = Math.random().toString(36).substring(7);
                }
                // functions
                var createRoom = function(room, layer) {
                    // draw room contour
                    var points = [];
                    angular.forEach(room.shape.points, function(point) {
                        points.push(point.x);
                        points.push(point.y);
                    });
                    var config = {
                        points: points,
                        strokeWidth: 1,
                        stroke: 'black',
                        closed: true
                    };
                    var pol = new Kinetic.Line(config);
                    layer.add(pol);
                };
                var createObj = function(obj, oLayer, tLayer) {
                    var repr = obj.representation[obj.currentRepresentation];
                    // insert background
                    // add tooltip
                    var tooltip = new Kinetic.Label({
                        opacity: 0.75,
                        visible: false,
                        listening: false
                    });
                    tooltip.add(new Kinetic.Tag({
                        fill: 'black',
                        pointerDirection: 'down',
                        pointerWidth: 10,
                        pointerHeight: 10,
                        lineJoin: 'round',
                        shadowColor: 'black',
                        shadowBlur: 10,
                        shadowOffset: {
                            x: 10,
                            y: 10
                        },
                        shadowOpacity: 0.5
                    }));
                    tooltip.add(new Kinetic.Text({
                        text: obj.name,
                        fontFamily: 'Calibri',
                        fontSize: 18,
                        padding: 5,
                        fill: 'white'
                    }));
                    tLayer.add(tooltip);
                    var updateTooltip = function(layer, obj) {
                        var mousePos = scope.kStage.getPointerPosition();
                        var x = mousePos.x / scope.scale;
                        var y = (mousePos.y / scope.scale) - 5;
                        var repr = obj.representation[obj.currentRepresentation];
                        //console.log('X:'+x+' Y:'+y+' ox:'+repr.offset.x+' oy:'+repr.offset.y+' scale:'+scope.scale);
                        tooltip.getText().text(obj.name);
                        tooltip.position({
                            x: x,
                            y: y
                        });
                        tooltip.show();
                        layer.draw();
                    };
                    if(repr.icon) {
                        var imageObj = new Image();
                        imageObj.onload = function() {
                            var image = new Kinetic.Image({
                                x: repr.offset.x,
                                y: repr.offset.y,
                                image: imageObj,
                                width: 64,
                                height: 64
                            });
                            image.on('touchstart click', function(evt) {
                                console.log(obj.name);
                            });
                            image.on('mouseover', function() {
                                document.body.style.cursor = 'pointer';
                            });
                            image.on('mouseout', function() {
                                document.body.style.cursor = 'default';
                                tooltip.hide();
                                tLayer.draw();
                            });
                            image.on('mousemove', function(evt) {
                                updateTooltip(tLayer, obj);
                            });
                            oLayer.add(image);
                            oLayer.draw();
                        };
                        imageObj.src = $rootScope.RESTaddr + 'v3/resources/' + repr.icon;
                    } else { // draw object shape
                        var points = [];
                        angular.forEach(repr.shape.points, function(point) {
                            points.push(point.x);
                            points.push(point.y);
                        });
                        var config = {
                            points: points,
                            strokeWidth: 1,
                            stroke: 'black',
                            closed: true,
                            x: repr.offset.x,
                            y: repr.offset.y
                        };
                        var pol = new Kinetic.Line(config);
                        pol.on('touchstart click', function(evt) {
                            console.log(obj.name);
                        });
                        pol.on('mouseover', function() {
                            document.body.style.cursor = 'pointer';
                        });
                        pol.on('mouseout', function() {
                            document.body.style.cursor = 'default';
                            tooltip.hide();
                            tLayer.draw();
                        });
                        pol.on('mousemove', function(evt) {
                            updateTooltip(tLayer, obj);
                        });
                        oLayer.add(pol);
                    }
                };
                // add layers
                var cLayer = new Kinetic.Layer();
                var oLayer = new Kinetic.Layer();
                var tLayer = new Kinetic.Layer();
                scope.kStage = new Kinetic.Stage({
                    container: id,
                    width: element.parent().width(),
                    height: element.parent().width() / scope.ratio
                });
                scope.kStage.width(element.parent().width());
                scope.kStage.height(element.parent().width() / scope.ratio);
                scope.kStage.scale({
                    x: scope.scale,
                    y: scope.scale
                });
                scope.kStage.add(oLayer);
                oLayer.setZIndex(20);
                scope.kStage.add(tLayer);
                tLayer.setZIndex(30);
                scope.$watch('zones', function(zones, OldValue, scope) {
                    scope.ratio = 1;
                    scope.scale = 1;
                    if(scope.env) {
                        scope.ratio = scope.env.width / scope.env.height;
                        scope.scale = element.parent().width() / scope.env.width;
                        // add environment background
                        if(scope.env.renderer === 'photo') {
                            var imageObj = new Image();
                            imageObj.onload = function() {
                                var image = new Kinetic.Image({
                                    x: 0,
                                    y: 0,
                                    image: imageObj,
                                    width: scope.env.width,
                                    height: scope.env.height
                                });
                                cLayer.add(image);
                                cLayer.draw();
                            };
                            imageObj.src = $rootScope.RESTaddr + 'v3/resources/' + scope.env.backgroundImage;
                        }
                    } else {}
                    if(zones) {
                        var zone = zones[0];
                        if(zone) {}
                        angular.forEach(zones, function(zone) {
                            if(zone.room) {
                                createRoom(zone, cLayer);
                            }
                        });
                        angular.forEach(zone.objects, function(obj) {
                            createObj(obj, oLayer, tLayer);
                        });
                    }
                    // add the layer to the stage
                    scope.kStage.add(cLayer);
                    cLayer.setZIndex(0);
                    scope.kStage.draw();
                    // canvas resize on window resize
                    element.parent().bind('resize', function(evt) {
                        scope.scale = element.parent().width() / scope.env.width;
                        scope.kStage.width(element.parent().width());
                        scope.kStage.height(element.parent().width() / scope.ratio);
                        scope.kStage.scale({
                            x: scope.scale,
                            y: scope.scale
                        });
                        scope.kStage.draw();
                    });
                });
            }
        };
    }
]);