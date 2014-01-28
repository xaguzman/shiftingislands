var connect = require('connect');

var port = 8888;
connect.createServer(connect.static(__dirname)).listen(port);

console.log('Started server at port ' + port);