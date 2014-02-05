'use strict';
angular.module('ui.dashboard.widgets', []);
angular.module('fdappApp', ['ngCookies', 'ngResource', 'ngSanitize', 'ngRoute', 'ui.bootstrap', 'cgBusy','ajoslin.promise-tracker'])
    .config(function($routeProvider) {
    $routeProvider.when('/login', {
        templateUrl: 'views/login.html',
        controller: 'LoginCtrl'
    }).when('/environments', {
        templateUrl: 'views/environmentList.html',
        controller: 'EnvironmentlistCtrl'
    }).when('/environments/:envID', {
        templateUrl: 'views/environmentDetails.html',
        controller: 'EnvironmentdetailsCtrl'
    }).when('/environments/:envID/room/:zoneID', {
        templateUrl: 'views/roomDetails.html',
        controller: 'RoomdetailsCtrl'
    }).when('/objects', {
        templateUrl: 'views/objects.html',
        controller: 'ObjectsCtrl'
    }).when('/plugins', {
        templateUrl: 'views/plugins.html',
        controller: 'PluginsCtrl'
    }).when('/commands', {
        templateUrl: 'views/commands.html',
        controller: 'CommandsCtrl'
    }).when('/commands/:cmdID', {
        templateUrl: 'views/commandDetails.html',
        controller: 'CommandsCtrl'
    }).when('/triggers', {
        templateUrl: 'views/triggers.html',
        controller: 'TriggersCtrl'
    }).when('/dashboard', {
        templateUrl: 'views/demo.html',
    }).otherwise({
        redirectTo: '/dashboard'
    });
})

.config(function($httpProvider) {
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
}).run(function($rootScope, promiseTracker, api) {
  $rootScope.loadingTracker = promiseTracker('ng');
  api.init();  
});
