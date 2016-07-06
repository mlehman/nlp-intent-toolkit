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


var writeFile = function(reqHash, content) {
    fs.writeFile(path + reqHash + ".source", content, function(err) {
        if(err) {
            return console.log(err);
        }
        console.log("The file was saved!");
    });
}

var accessSpeFile = function(reqHash) {
    try {
        fs.accessSync(path + reqHash + ".result");
            console.log("yes");
            return true;
        } catch(e) {
            console.log("no");
            return false;
        }
}

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended: true
}));
app.post('/log', function(req, res) {
	console.log(req.body.data);
});

app.listen(1234, function () {
    console.log('Response server listening on port 1234!'.green);
});

