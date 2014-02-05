'use strict';

describe('Directive: roomMap', function () {

  // load the directive's module
  beforeEach(module('fdappApp'));

  var element,
    scope;

  beforeEach(inject(function ($rootScope) {
    scope = $rootScope.$new();
  }));

  it('should make hidden element visible', inject(function ($compile) {
    element = angular.element('<room-map></room-map>');
    element = $compile(element)(scope);
    expect(element.text()).toBe('this is the roomMap directive');
  }));
});
