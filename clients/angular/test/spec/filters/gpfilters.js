'use strict';

describe('Filter: gpFilters', function () {

  // load the filter's module
  beforeEach(module('fdappApp'));

  // initialize a new instance of the filter before each test
  var gpFilters;
  beforeEach(inject(function ($filter) {
    gpFilters = $filter('gpFilters');
  }));

  it('should return the input prefixed with "gpFilters filter:"', function () {
    var text = 'angularjs';
    expect(gpFilters(text)).toBe('gpFilters filter: ' + text);
  });

});
