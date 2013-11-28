var express = require('express');
var app = express();
app.use(express.bodyParser());

var ids = 0;
var messages = {};

app.get('/messages', function(req, res) {
	console.log("Received a request to get all the messages.");

	// TODO I don't remember how to get the values of an object.
	// I will look it up when my plane lands and delete this shitty code!
	var result = [];
	for (var key in messages) result.push(messages[key]);

	res.json(result);
});

app.post('/messages', function(req, res) {
	console.log("Received a request to post a message.");
	var input = req.body;
	
	input.id = ids++;
	messages[input.id] = input;
	res.json(201, input);
});

app.get('/messages/:id', function(req, res) {
	var id = req.params.id;
	console.log("Received a request to get the message#"+id+".");
	var message = messages[id];
	if (message == null)
		res.send(404);
	else
		res.json(message);
});

app.put('/messages/:id', function(req, res) {
	var id = req.params.id;
	var input = req.body;

	console.log("Received a request to update the message#"+id);

	var message = messages[id];
	if (message == null)
		res.send(404);
	else {
		// In case of a modification of the id by the user...
		input.id = parseInt(id);
		messages[id] = input;
		res.json(input);
	}
});

app.del('/messages/:id', function(req, res) {
	var id = req.params.id;
	console.log("Received a request to delete the message#"+id+".");
	var message = messages[id];
	if (message == null)
		res.send(404);
	else {
		delete messages[id];
		res.send(204);
	}
});

app.listen(1337);
console.log("Express server listening on port 1337");

