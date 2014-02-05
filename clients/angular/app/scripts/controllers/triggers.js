'use strict';

angular.module('fdappApp')
  .controller('TriggersCtrl', function($scope, Trigger) {
                $scope.triggers = Trigger.list();
            });
