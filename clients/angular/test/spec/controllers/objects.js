'use strict';

describe('Controller: ObjectsCtrl', function () {

  // load the controller's module
  beforeEach(module('fdappApp'));

  var ObjectsCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    ObjectsCtrl = $controller('ObjectsCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
