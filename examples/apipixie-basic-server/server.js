var express = require('express'),
	http = require('http');

var app = express();

app.configure(function(){
	app.set('port', process.env.PORT || 1337);
	app.use(express.favicon());
	app.use(express.bodyParser());
	app.use(express.methodOverride());
	app.use(app.router);
});

var messages = [
	{
		id: 1,
		text: "My awesome message 1"
	},
	{
		id: 2,
		text: "My awesome message 2"
	}
];

app.get('/messages', function(req, res) {
	console.log("Received a request to send all the messages.");
	res.send(200, messages);
});

app.get('/messages/:id', function(req, res) {
	var id = req.params.id;
	console.log("Received a request to get the message#"+id+".");
	res.send(200, messages[id-1]);
});

http.createServer(app).listen(app.get('port'), function(){
	console.log("Express server listening on port " + app.get('port'));
});

