var express = require('express');
var bodyParser = require('body-parser');
var colors = require('colors');
var request = require('request');
var fs = require('fs');
var app = express();
var path = "/Users/agent05/nlp-intent-toolkit/fds/";
var logPath = "/Users/agent05/nlp-intent-toolkit/node-server/log/";

var today = function() {
    var today = new Date();
    var dd = today.getDate();
    var mm = today.getMonth()+1; //January is 0!
    var yyyy = today.getFullYear();
    return mm + '-' + dd + '-' + yyyy;
}


app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended: true
}));
app.post('/log', function(req, res) {
	console.log(req.body.data);
    res.send("{\"data\":\"Voici le numero de telephone d'orange\"}");
});

app.listen(1234, function () {
    console.log('Response server listening on port 1234!'.green);
});

