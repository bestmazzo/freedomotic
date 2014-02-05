'use strict';
angular.module('fdappApp').controller('PluginsCtrl', function($scope, $rootScope, Plug, PluginChangeEvents) {
    $scope.plugs = Plug.list();
    $rootScope.loadingTracker.addPromise($scope.plugs);
    $scope.toggle = function(item){
        if (item.isRunning == false){
            //alert('Starting: ' + item.name);
            Plug.start({plugid: item.className},''); 
        } else {
            //alert('Stopping:' + item.name)
            Plug.stop({plugid: item.className},''); 
        }
    };   
    PluginChangeEvents.addEventListener('message', function (e) {
        console.log(e);
      });
});