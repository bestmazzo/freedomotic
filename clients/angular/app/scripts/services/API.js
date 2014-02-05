'use strict';
angular.module('fdappApp').service('Env', function($resource, $rootScope) {
    return $resource($rootScope.RESTaddr + 'v3/environments/:envID', {}, {
        get: {
            method: 'GET',
            params: {
                envID: ''
            }
        },
        list: {
            method: 'GET',
            params: {}
            , isArray:true  
        },
    });
}).service('Room', function($resource, $rootScope) {
    return $resource($rootScope.RESTaddr + 'v3/environments/:envID/rooms/:zoneID', {}, {
        get: {
            method: 'GET',
            params: {
                envID: '',
                zoneID: ''
            }
        }
    });
}).service('Res', function($resource, $rootScope) {
    return $resource($rootScope.RESTaddr + 'v3/resources/:fileName', {}, {
        get: {
            method: 'GET',
            params: {
                fileName: ''
            }
        }
    });
}).service('Plug', function($resource, $rootScope) {
    return $resource($rootScope.RESTaddr + 'v3/plugins/', {}, {
        list: {
            method: 'GET',
            params: {},
            isArray:true  
        },
        start: {
            method: 'POST',
            url: $rootScope.RESTaddr + 'v3/plugins/:plugid/start/',
            params: {
                plugid: '@name'
            }
        },
        stop: {
            method: 'POST',
            url: $rootScope.RESTaddr + 'v3/plugins/:plugid/stop/',
            params: {
                plugid: '@name'
            }
        }
        
    });
}).service('Obj', function($resource, $rootScope) {
    return $resource($rootScope.RESTaddr + 'v3/objects/', {}, {
        list: {
            method: 'GET',
            params: {}, 
            isArray:true  
        }
    });
}).service('UserCommand', function($resource, $rootScope) {
    return $resource($rootScope.RESTaddr + 'v3/commands/user/', {}, {
        list: {
            method: 'GET',
            params: {}, 
           isArray:true  
        }
    });
}).service('HWCommand', function($resource, $rootScope) {
    return $resource($rootScope.RESTaddr + 'v3/commands/hardware/', {}, {
        list: {
            method: 'GET',
            params: {}, 
           isArray:true  
        }
    });
}).service('Trigger', function($resource, $rootScope) {
    return $resource($rootScope.RESTaddr + 'v3/triggers/', {}, {
        list: {
            method: 'GET',
            params: {}, 
           isArray:true  
        }
    });
}).service('api', function($http, $cookies) {
    return {
        init: function(token) {
        //    $http.defaults.headers.common['X-Access-Token'] = token || $cookies.token;
        }
    };
})
// register the interceptor as a service
.service('myHttpInterceptor', function($q, $rootScope) {
    return {
        // optional method
        'request': function(config) {
            // do something on success
            $rootScope.ApiStatus = 'pending';
            return config || $q.when(config);
        },
        // optional method
        'requestError': function(rejection) {
            // do something on error
            // alert("Resource ERROR on request, please check your API access");
            $rootScope.ApiStatus = 'error';
            return $q.reject(rejection);
        },
        // optional method
        'response': function(response) {
            // do something on success
            $rootScope.ApiStatus = 'good';
            return response || $q.when(response);
        },
        // optional method
        'responseError': function(rejection) {
            // do something on error
            // alert('Resource ERROR on response, please chek your API access');
            $rootScope.ApiStatus = 'error';
            return $q.reject(rejection);
        }
    };
}).service('Base64', function() {
    var keyStr = 'ABCDEFGHIJKLMNOP' + 'QRSTUVWXYZabcdef' + 'ghijklmnopqrstuv' + 'wxyz0123456789+/' + '=';
    return {
        encode: function(input) {
            var output = '';
            var chr1, chr2, chr3 = '';
            var enc1, enc2, enc3, enc4 = '';
            var i = 0;
            do {
                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);
                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;
                if(isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if(isNaN(chr3)) {
                    enc4 = 64;
                }
                output = output + keyStr.charAt(enc1) + keyStr.charAt(enc2) + keyStr.charAt(enc3) + keyStr.charAt(enc4);
                chr1 = chr2 = chr3 = '';
                enc1 = enc2 = enc3 = enc4 = '';
            } while (i < input.length);
            return output;
        },
        decode: function(input) {
            var output = '';
            var chr1, chr2, chr3 = '';
            var enc1, enc2, enc3, enc4 = '';
            var i = 0;
            // remove all characters that are not A-Z, a-z, 0-9, +, /, or =
            var base64test = /[^A-Za-z0-9\+\/\=]/g;
            if(base64test.exec(input)) {
                alert('There were invalid base64 characters in the input text.\n' + 'Valid base64 characters are A-Z, a-z, 0-9, \'+\', \'/\',and \'=\' \n' + 'Expect errors in decoding.');
            }
            input = input.replace(/[^A-Za-z0-9\+\/\=]/g, '');
            do {
                enc1 = keyStr.indexOf(input.charAt(i++));
                enc2 = keyStr.indexOf(input.charAt(i++));
                enc3 = keyStr.indexOf(input.charAt(i++));
                enc4 = keyStr.indexOf(input.charAt(i++));
                chr1 = (enc1 << 2) | (enc2 >> 4);
                chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                chr3 = ((enc3 & 3) << 6) | enc4;
                output = output + String.fromCharCode(chr1);
                if(enc3 != 64) {
                    output = output + String.fromCharCode(chr2);
                }
                if(enc4 != 64) {
                    output = output + String.fromCharCode(chr3);
                }
                chr1 = chr2 = chr3 = '';
                enc1 = enc2 = enc3 = enc4 = '';
            } while (i < input.length);
            return output;
        }
    };
}).service('authorization', function($q, $http, Base64) {
    // CHANGE THIS!
    //var url = config.analytics.url;
    return {
        login: function(credentials) {
            // CHANGE THIS!!
            /// return $http.post(url + '/auth', credentials);
            // or use an ad-hoc rest url to submit credentials 
            // and get back auth token (being it a base64 user:password string  or other)
            // 
            var deferred = $q.defer();
            setTimeout(function() {
                var encoded = Base64.encode(credentials.user + ':' + credentials.password);
                var auth = 'Basic ' + encoded;
                $http.defaults.headers.common.Authorization = auth;
                deferred.resolve(encoded);
            }, 10);
            return deferred.promise;
        }
    };
}).service('authInterceptor', function authInterceptor($q, $window, $location) {
    return function(promise) {
        var success = function(response) {
            return response;
        };
        var error = function(response) {
            if(response.status === 401) {
                // try to retrieve auth data, otherwise show login panel
                $location.url('/login');
            }
            return $q.reject(response);
        };
        return promise.then(success, error);
    };
})
 .service('ObjectChangeEvents', function($rootScope) {
      var sse = new EventSource($rootScope.RESTaddr + 'v3/events/object');
      return {
        addEventListener: function(eventName, callback) {
          sse.addEventListener(eventName, function() {
            var args = arguments;
            $rootScope.$apply(function () {
              callback.apply(sse, args);
            });
          });
        }
      };
    })
 .service('ZoneChangeEvents', function($rootScope) {
      var sse = new EventSource($rootScope.RESTaddr + 'v3/events/zone');
      return {
        addEventListener: function(eventName, callback) {
          sse.addEventListener(eventName, function() {
            var args = arguments;
            $rootScope.$apply(function () {
              callback.apply(sse, args);
            });
          });
        }
      };
    })
 .service('PluginChangeEvents', function($rootScope) {
      var sse = new EventSource($rootScope.RESTaddr + 'v3/events/plugin', { withCredentials: true });
     // After SSE handshake constructed
	sse.onopen = function (e) {
	 alert("Waiting message..");
	};
	 
	// Error handler
	sse.onerror = function (e) {
	 alert("Error");
	 console.log(e);
	};
     // Message handler
	sse.onmessage=function (e) {
	   alertg(e);
    };	 
      return {
        addEventListener: function(eventName, callback) {
          sse.addEventListener(eventName, function() {
            var args = arguments;
            $rootScope.$apply(function () {
              callback.apply(sse, args);
            });
          });
        }
      };
    })
.config(function($httpProvider) {
    $httpProvider.interceptors.push('myHttpInterceptor');
    $httpProvider.responseInterceptors.push('authInterceptor');
});