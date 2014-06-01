(function(context) {
  'use strict';

  var Timer = Java.type('java.util.Timer');
  var Phaser = Java.type('java.util.concurrent.Phaser');
  var TimeUnit = Java.type('java.util.concurrent.TimeUnit');
  var AsyncHttpClient = Java.type('com.ning.http.client.AsyncHttpClient');

  var timer = new Timer('jsEventLoop', false);
  var phaser = new Phaser();

  var onTaskFinished = function() {
    phaser.arriveAndDeregister();
  };

  context.setTimeout = function(fn, millis /* [, args...] */) {
    var args = [].slice.call(arguments, 2, arguments.length);

    var phase = phaser.register();
    var canceled = false;
    timer.schedule(function() {
      if (canceled) {
        return;
      }

      try {
        fn.apply(context, args);
      } catch (e) {
        print(e);
      } finally {
        onTaskFinished();
      }
    }, millis);

    return function() {
      onTaskFinished();
      canceled = true;
    };
  };

  context.clearTimeout = function(cancel) {
    cancel();
  };

  context.setInterval = function(fn, delay /* [, args...] */) {
    var args = [].slice.call(arguments, 2, arguments.length);

    var cancel = null;

    var loop = function() {
      cancel = context.setTimeout(loop, delay);
      fn.apply(context, args);
    };

    cancel = context.setTimeout(loop, delay);
    return function() {
      cancel();
    };
  };

  context.clearInterval = function(cancel) {
    cancel();
  };

  context.main = function(fn, waitTimeMillis) {
    if (!waitTimeMillis) {
      waitTimeMillis = 60 * 1000;
    }

    if (phaser.isTerminated()) {
      phaser = new Phaser();
    }

    // we register the main(...) function with the phaser so that we
    // can be notified of all cases. If we wouldn't do this, we would have a
    // race condition as `fn` could be finished before we call `await(...)`
    // on the phaser.
    phaser.register();
    setTimeout(fn, 0);

    // timeout is handled via TimeoutException. This is good enough for us.
    phaser.awaitAdvanceInterruptibly(phaser.arrive(),
      waitTimeMillis,
      TimeUnit.MILLISECONDS);

    // a new phase will have started, so we need to arrive and deregister
    // to make sure that following executions of main(...) will work as well.
    phaser.arriveAndDeregister();
  };

  context.shutdown = function() {
    timer.cancel();
    phaser.forceTermination();
  };

  context.XMLHttpRequest = function() {
    var method, url, async, user, password, headers = {};

    this.onreadystatechange = function(){};
    this.readyState = 0;
    this.response = null;
    this.responseText = null;
    this.responseType = '';
    this.status = null;
    this.statusText = null;
    this.timeout = 0; // no timeout by default
    this.ontimeout = function(){};
    this.withCredentials = false;

    this.abort = function() {

    };

    this.getAllResponseHeaders = function() {

    };

    this.getResponseHeader = function(key) {

    };

    this.setRequestHeader = function(key, value) {
      headers[key] = value;
    };

    this.open = function(_method, _url, _async, _user, _password) {
      this.readyState = 1;

      method = _method;
      url = _url;

      async = _async === false ? false : true;

      user = _user || '';
      password = _password || '';

      setTimeout(this.onreadystatechange, 0);
    };

    this.send = function(data) {
      phaser.register();

      var that = this;
      var client = new AsyncHttpClient();

      var methodPascalCase = method.replace(/^([a-z])(.*)$/i, function(_, firstChar, rest) {
        return firstChar.toUpperCase() + rest.toLowerCase()
      });
      var requestBuilder = client['prepare' + methodPascalCase](url);

      Object.keys(headers)
        .forEach(function(header) {
          var value = headers[header];
          requestBuilder.addHeader(header, value);
        });

      // TODO configure timeouts on AsyncHttpClientConfig
      // TODO handle errors
      requestBuilder.execute(new com.ning.http.client.AsyncCompletionHandler({
        onCompleted: function(response) {
          that.readyState = 4;
          that.responseText = that.response = response.getResponseBody('UTF-8');
          that.status = response.getStatusCode();
          that.statusText = response.getStatusCode() + ' ' + response.getStatusText();

          if (that.responseType === 'json') {
            that.response = JSON.parse(that.response);
          }

          context.setTimeout(that.onreadystatechange, 0);
          phaser.arriveAndDeregister();
        }
      }));
    };
  };

})(this);
