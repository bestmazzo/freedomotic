'use strict';

describe('Service: Blocklyservice', function () {

  // load the service's module
  beforeEach(module('fdappApp'));

  // instantiate service
  var Blocklyservice;
  beforeEach(inject(function (_Blocklyservice_) {
    Blocklyservice = _Blocklyservice_;
  }));

  it('should do something', function () {
    expect(!!Blocklyservice).toBe(true);
  });

});
