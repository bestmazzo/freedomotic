'use strict';
angular.module('fdappApp').controller('EnvironmentlistCtrl', function($scope, $rootScope, Env) {
    $scope.environments = Env.list();
    $rootScope.loadingTracker.addPromise($scope.environments);
});