'use strict';

describe('Controller: RoomdetailsCtrl', function () {

  // load the controller's module
  beforeEach(module('fdappApp'));

  var RoomdetailsCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    RoomdetailsCtrl = $controller('RoomdetailsCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
