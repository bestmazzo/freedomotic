'use strict';
angular.module('fdappApp').controller('EnvironmentdetailsCtrl', function($scope, $routeParams, $rootScope, Env) {
    var e =
    Env.get({
        envID: $routeParams.envID
    }, function(env) {
        $scope.env = env;
    });
    $scope.envID = $routeParams.envID;
    $rootScope.loadingTracker.addPromise(e);
});