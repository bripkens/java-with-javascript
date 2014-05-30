main(function() {
  output.numbers = [];

  function add(n) {
    setTimeout(function() {
        output.numbers.push(n);
    }, n);
  }

  add(50);
  add(20);
  add(10);
  add(30);
  add(40);
});
