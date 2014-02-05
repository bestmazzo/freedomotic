'use strict';
angular.module('fdappApp').directive('vbox', function() {
    return {
        link: function(scope, element, attrs) {
            attrs.$observe('vbox', function(value) {
                element.context.setAttribute('viewBox', value);
            });
        }
    };
});
angular.module('fdappApp').directive('resize', function($window) {
    return function(scope, element) {
        var w = angular.element($window);
        scope.getWindowDimensions = function() {
            return {
                'h': w.height(),
                'w': w.width()
            };
        };
        scope.$watch(scope.getWindowDimensions, function(newValue, oldValue) {
            scope.windowHeight = newValue.h;
            scope.windowWidth = newValue.w;
            scope.style = function() {
                return {
                    'height': (newValue.h * 0.9) + 'px',
                    'width': (newValue.w * 0.9) + 'px'
                };
            };
        }, true);
        w.bind('resize', function() {
            scope.$apply();
        });
    };
});
angular.module('fdappApp').directive('roomMap', [ '$rootScope', '$q',
    function($rootScope, $q) {
        return {
            restrict: 'E',
            templateUrl: 'views/roomMap.html',
            scope: {
                zones: '=',
                env: '=',
                mode: '@'
            },
            replace: true,
            link: function(scope, element, attrs) {
                     scope.line = d3.svg.line().x(function(d) {
                        return d.x;
                    }).y(function(d) {
                        return d.y;
                    }).interpolate('linear-closed');
                    scope.respath = function(data) {
                        if(data) {
                            return $rootScope.RESTaddr + 'v2/resources/' + data;
                        }
                        return '';
                    };
                    scope.mappath = function(data) {
                        if(data) {
                            if(scope.mode === 'photo') {
                                return $rootScope.RESTaddr + 'v2/resources/' + data;
                            }
                        }
                    };
                    scope.texpath = function(data) {
                        if(data) {
                            if(scope.mode === 'image') {
                                return $rootScope.RESTaddr + 'v2/resources/' + data;
                            }
                        }
                    };
                    scope.$watch('zones', function(zones, OldValue, scope) {
                        if(scope.env) {
                            scope.width = scope.env.width;
                            scope.height = scope.env.height;
                            scope.transform = 'translate(0,0)';
                        } else {
                            if(zones) {
                                var zone = zones[0];
                                if(zone) {
                                    var minx = d3.min(zone.shape.points, function(d) {
                                        return d.x;
                                    }) - 5;
                                    var miny = d3.min(zone.shape.points, function(d) {
                                        return d.y;
                                    }) - 5;
                                    scope.width = d3.max(zone.shape.points, function(d) {
                                        return d.x;
                                    }) - minx + 10;
                                    scope.height = d3.max(zone.shape.points, function(d) {
                                        return d.y;
                                    }) - miny + 10;
                                    scope.transform = 'translate(-' + minx + ',-' + miny + ')';
                                }
                            }
                        }
                    });
                    scope.objclick = function(f) {
                        // to be filled in later
                        alert(f);
                    };
                    var modify = false;
                    scope.togglemodify = function() {
                        modify = !modify;
                        if(modify) {
                            d3.selectAll('.handle').call(drag).attr('style', 'display:inline;');
                        } else {
                            d3.selectAll('.handle').attr('style', 'display:none;');
                        }
                    };
                    scope.rightclickroom = function(f) {
                        scope.togglemodify();
                    };
                    scope.rightclickobj = function(f) {
                        // to be filled in later
                        alert('right ' + f);
                    };
                    scope.roomclick = function(f) {
                        // to be filled in later
                        alert('this should to to ' + f + '\'s detailed map ');
                    };
                    scope.objhover = function(status, obj) {};
                    var drag = d3.behavior.drag()
                    //.origin(function(d){ return d; })
                    .on('drag', function() {
                        d3.select(this).attr('cx', d3.event.x).attr('cy', d3.event.y).on('dragend', function() {
                            // apply data to model
                        });
                    });
                    scope.updateHandle = function($event, point) {
                        point.x = d3.select($event.target).attr('cx');
                        point.y = d3.select($event.target).attr('cy');
                    };
            }
        };
    }
]);