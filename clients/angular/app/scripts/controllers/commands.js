'use strict';

angular.module('fdappApp')
        .controller('CommandsCtrl', function($scope, UserCommand, HWCommand, $location, $rootScope) {
            $scope.userCommands = UserCommand.list();
            $rootScope.loadingTracker.addPromise($scope.userCommands);
            $scope.hwCommands = HWCommand.list();
            $rootScope.loadingTracker.addPromise($scope.hwCommands);
            $scope.edit = function(cmd) {
                $scope.curCom = cmd;
                
            };
            
        });
