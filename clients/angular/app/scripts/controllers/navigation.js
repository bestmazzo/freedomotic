'use strict';

angular.module('fdappApp')
  .controller('NavigationCtrl', function($scope, $location, Breadcrumbs) {
                $scope.items = [
                    {path: '/environments', title: 'Environments', icon:'home'},
                    {path: '/plugins', title: 'Plugins', icon: 'cogs'},
                    {path: '/objects', title: 'Objects', icon:'lightbulb-o'},
                    {path: '/commands', title: 'Commands', icon:'tasks'},
                    {path: '/triggers', title: 'Triggers', icon:'flash'},
                    {path: '/dashboard', title: 'Dashboard', icon:'th'},
                    
                ];
                $scope.breadcrumbs = Breadcrumbs;
                $scope.isActive = function(item) {
                    if (item.path === $location.path()) {
                        return true;
                    }
                    return false;
                };
});
