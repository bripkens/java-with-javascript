main(function() {
  var stopAfter = 5;
  output.iterationCount = 0;

  var token = setInterval(function() {
    output.iterationCount++;

    if (output.iterationCount >= stopAfter) {
      clearInterval(token);
    }
  }, 10);
});