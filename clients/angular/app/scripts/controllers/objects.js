'use strict';
angular.module('fdappApp').controller('ObjectsCtrl', function($scope, $rootScope, Obj) {
    $scope.objs = Obj.list();
    $rootScope.loadingTracker.addPromise($scope.objs);
});