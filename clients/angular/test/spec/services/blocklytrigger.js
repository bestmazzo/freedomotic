'use strict';

describe('Service: Blocklytrigger', function () {

  // load the service's module
  beforeEach(module('fdappApp'));

  // instantiate service
  var Blocklytrigger;
  beforeEach(inject(function (_Blocklytrigger_) {
    Blocklytrigger = _Blocklytrigger_;
  }));

  it('should do something', function () {
    expect(!!Blocklytrigger).toBe(true);
  });

});
