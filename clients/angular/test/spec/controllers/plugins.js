'use strict';

describe('Controller: PluginsCtrl', function () {

  // load the controller's module
  beforeEach(module('fdappApp'));

  var PluginsCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    PluginsCtrl = $controller('PluginsCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
