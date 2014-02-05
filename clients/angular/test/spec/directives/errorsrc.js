'use strict';

describe('Directive: errorsrc', function () {

  // load the directive's module
  beforeEach(module('fdappApp'));

  var element,
    scope;

  beforeEach(inject(function ($rootScope) {
    scope = $rootScope.$new();
  }));

  it('should make hidden element visible', inject(function ($compile) {
    element = angular.element('<errorsrc></errorsrc>');
    element = $compile(element)(scope);
    expect(element.text()).toBe('this is the errorsrc directive');
  }));
});
