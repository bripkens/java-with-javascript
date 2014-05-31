(function(context) {
  'use strict';

  var Timer = Java.type('java.util.Timer');
  var Phaser = Java.type('java.util.concurrent.Phaser');
  var CountDownLatch = Java.type('java.util.concurrent.CountDownLatch');
  var TimeUnit = Java.type('java.util.concurrent.TimeUnit');

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

})(this);
