'use strict';
angular.module('fdappApp').controller('RoomdetailsCtrl', function($scope, $routeParams, $rootScope, Room) {
    var r = Room.get({
        envID: $routeParams.envID,
        zoneID: $routeParams.zoneID
    }, function(room) {
        $scope.room = room['it.freedomotic.model.environment.Zone'];
        if(!$scope.room) {
            $scope.room = room['com.freedomotic.model.environment.Zone'];
        }
    });
    $rootScope.loadingTracker.addPromise(r);
    $scope.Switch = function(name) {
        /* add code for object power switching */
    };
});