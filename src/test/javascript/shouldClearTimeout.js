main(function() {
  output.numbers = [];

  setTimeout(function() {
    output.numbers.push(1);
  }, 10);

  var token = setTimeout(function() {
    output.numbers.push(3);
  }, 30);

  setTimeout(function() {
    output.numbers.push(2);
    clearTimeout(token);
  }, 20);


});