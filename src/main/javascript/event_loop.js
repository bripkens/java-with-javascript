(function(context) {
  'use strict';

  var Timer = Java.type('java.util.Timer');
  var CountDownLatch = Java.type('java.util.concurrent.CountDownLatch');
  var TimeUnit = Java.type('java.util.concurrent.TimeUnit');

  var timer = new Timer('jsEventLoop', false);
  var countDownLatch = new CountDownLatch(1);

  var taskCount = 0;

  var onTaskFinished = function() {
    taskCount--;

    if (taskCount === 0) {
      timer.cancel();
      countDownLatch.countDown();
    }
  };

  context.setTimeout = function(fn, millis /* [, args] */) {
    var args = [].slice.call(arguments, 2, arguments.length);

    taskCount++;
    var canceled = false;
    timer.schedule(function() {
      if (!canceled) {
        try {
          fn.apply(context, args);
        } catch (e) {
          print(e);
        } finally {
          onTaskFinished();
        }
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

  context.setInterval = function(fn, delay /* [, args] */) {
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
    setTimeout(fn, 0);
    countDownLatch.await(waitTimeMillis, TimeUnit.MILLISECONDS);
  };

})(this);
