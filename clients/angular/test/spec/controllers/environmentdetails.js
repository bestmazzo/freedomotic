'use strict';

describe('Controller: EnvironmentdetailsCtrl', function () {

  // load the controller's module
  beforeEach(module('fdappApp'));

  var EnvironmentdetailsCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    EnvironmentdetailsCtrl = $controller('EnvironmentdetailsCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
