this.doIt = function() {
  var numbers = [];

  for (var i = 0; i < 5000; i++) {
    numbers.push(i);
  }

  return numbers.join(', ');
};