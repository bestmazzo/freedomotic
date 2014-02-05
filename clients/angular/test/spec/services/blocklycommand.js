'use strict';

describe('Service: Blocklycommand', function () {

  // load the service's module
  beforeEach(module('fdappApp'));

  // instantiate service
  var Blocklycommand;
  beforeEach(inject(function (_Blocklycommand_) {
    Blocklycommand = _Blocklycommand_;
  }));

  it('should do something', function () {
    expect(!!Blocklycommand).toBe(true);
  });

});
