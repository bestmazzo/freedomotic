'use strict';

describe('Controller: EnvironmentlistCtrl', function () {

  // load the controller's module
  beforeEach(module('fdappApp'));

  var EnvironmentlistCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    EnvironmentlistCtrl = $controller('EnvironmentlistCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
