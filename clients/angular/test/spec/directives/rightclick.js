'use strict';

describe('Directive: rightclick', function () {

  // load the directive's module
  beforeEach(module('fdappApp'));

  var element,
    scope;

  beforeEach(inject(function ($rootScope) {
    scope = $rootScope.$new();
  }));

  it('should make hidden element visible', inject(function ($compile) {
    element = angular.element('<rightclick></rightclick>');
    element = $compile(element)(scope);
    expect(element.text()).toBe('this is the rightclick directive');
  }));
});
