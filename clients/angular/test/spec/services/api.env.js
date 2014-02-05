'use strict';

describe('Service: ApiEnv', function () {

  // load the service's module
  beforeEach(module('fdappApp'));

  // instantiate service
  var ApiEnv;
  beforeEach(inject(function (_ApiEnv_) {
    ApiEnv = _ApiEnv_;
  }));

  it('should do something', function () {
    expect(!!ApiEnv).toBe(true);
  });

});
