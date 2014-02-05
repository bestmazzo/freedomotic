'use strict';
angular.module('fdappApp').controller('LoginCtrl', function($scope, $rootScope, $cookieStore, $location, authorization, api) {
    $rootScope.$on('$routeChangeStart', function(next, current) {
        if(!$rootScope.config) {
            $rootScope.config = $cookieStore.get('config');
        }
        if(!$rootScope.config) {
            $location.path('login');
        } else {
            $scope.login($rootScope.config,false);
        }
    });
    $scope.login = function(config, redir) {
        var success = function (data, redir) {
            
            // TODO: clear unused data
            $rootScope.config = angular.copy(config);
            $rootScope.signedIn = true;
            var ssl = $rootScope.config.ssl ? 's' : '';
            $rootScope.RESTaddr = 'http' + ssl + '://' + config.addr + ':' + config.port + '/';
            $cookieStore.remove('config');
            if(config.rememberMe) {
                $cookieStore.put('config', config);
            }
            
            api.init(data);
            if (redir){
                $location.path('environments');
            }
            
        };
        var error = function () {
          // TODO: apply user notification here..
      };
        authorization.login(config).then(success,error);
    };
    $scope.logout = function() {
        $rootScope.config = undefined;
        $cookieStore.remove('config');
        $rootScope.signedIn = false;
        $location.path('login');
    };
});