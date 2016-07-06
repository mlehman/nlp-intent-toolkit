var express = require('express');
var bodyParser = require('body-parser');
var fs = require('fs');
var app = express();
var path = '/Users/bastienbotella/web_docs/NLP/nlp-intent-toolkit/fds/';
var logPath = '/Users/bastienbotella/web_docs/NLP/nlp-intent-toolkit/node-server/log/';

var today = function() {
    var today = new Date();
    var dd = today.getDate();
    var mm = today.getMonth() + 1; //January is 0!
    var yyyy = today.getFullYear();
    return mm + '-' + dd + '-' + yyyy;
};

var writeFile = function(reqHash, content) {
    fs.writeFile(path + reqHash + '.source', content, function(err) {
        if (err) {
            return console.log(err);
        }
        console.log('The file was saved!');
    });
};

var accessSpeFile = function(reqHash) {
    try {
        fs.accessSync(path + reqHash + '.result');
        console.log('yes');
        return true;
    } catch (e) {
        console.log('no');
        return false;
    }
};

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended: true
}));

app.post('/', function(req, res) {
    if (!req.body.enquiry || req.body.enquiry === '') {
        return res.send('{status: false, error: Missing text field.}');
    } else {
        console.log(req.body.enquiry);
    }
    var reqHash = Math.random().toString(36).substring(12);
    writeFile(reqHash, req.body.enquiry);
    var interval = setInterval(function() {
        if (accessSpeFile(reqHash) === true) {
            try {
                var data = fs.readFileSync(path + reqHash + '.result');
                try {
                    var fileLog = fs.readFileSync(logPath + today());
                } catch (e) {
                    var fileLog = '';
                }
                try {
                    fs.unlinkSync(path + reqHash + '.result');
                } catch (e) {
                    console.log(e);
                }
                console.log(data.toString());
                fs.writeFile(logPath + today(), fileLog.toString() + '{in: \'' + req.body.enquiry + '\', out: ' + data.toString() + '},',
                function(err) {
                    if (err) {
                        return console.log(err);
                    }
                    console.log('Log saved!');
                });
                res.send('{status: true, data: ' + data + '}');
                clearInterval(interval);
            } catch (e) {
                console.log(e);
            }
        }
    }, 500);
    // res.send('Req done');
});

app.listen(3000, function () {
    console.log('Example app listening on port 3000!');
});
