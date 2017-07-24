var http = require('http');

var server = http.createServer(function(request, response) {
  response.writeHead(200, {
    'Access-Control-Allow-Origin': '*',
    'Content-Type': 'application/json'
  });
  response.end('hi');
}).listen(8088);