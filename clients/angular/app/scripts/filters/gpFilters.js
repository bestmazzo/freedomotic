'use strict';

angular.module('fdappApp')
  .filter('plugRunning', function() {
            return function(input) {
                return input ? 'list-group-item-success' : 'list-group-item-warning';
            };
        });
