var express = require('express');
var app = express();
app.use(express.bodyParser());

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
	console.log("Received a request to get all the messages.");
	res.json(messages);
});

app.get('/messages/:id', function(req, res) {
	var id = req.params.id;
	console.log("Received a request to get the message#"+id+".");
	res.json(messages[id-1]);
});

app.put('/messages/:id', function(req, res) {
	var id = req.params.id;
	var input = req.body;

	console.log("Received a request to update the message#"+id);

	// In case of a modification of the id by the user...
	input.id = parseInt(id);

	messages[id-1] = input;
	res.json(messages[id-1]);
});

app.listen(1337);
console.log("Express server listening on port 1337");

