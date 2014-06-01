var request = new XMLHttpRequest();

request.onreadystatechange = function() {
  if (request.readyState === 4) {
    print(request.responseText);
    output.responseArrived = true;
  }
};

request.open('GET', 'http://movie-database.herokuapp.com/movies');
request.setRequestHeader('Accept', 'application/json');
request.send();
