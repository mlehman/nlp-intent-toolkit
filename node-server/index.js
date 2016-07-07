var express = require('express');
var bodyParser = require('body-parser');
var request = require('request');
var colors = require('colors');
var fs = require('fs');
var app = express();
var path = '/Users/agent05/nlp-intent-toolkit/fds/';
var logPath = '/Users/agent05/nlp-intent-toolkit/node-server/log/';

var today = function() {
    var today = new Date();
    var dd = today.getDate();
    var mm = today.getMonth() + 1; //January is 0!
    var yyyy = today.getFullYear();
    return mm + '-' + dd + '-' + yyyy;
};

var datetime = function() {
    var datetime = new Date().toISOString().
        replace(/T/, ' ').      // replace T with a space
        replace(/\..+/, '')
    return datetime;
};

var writeFile = function(reqHash, content) {
    fs.writeFile(path + reqHash + '.source', content, function(err) {
        if(err) {
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
        var source = req.body.enquiry.replace('\'', ' ');
        console.log(source.red);
    }
    var reqHash = Math.random().toString(36).substring(12);
    writeFile(reqHash, source);
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
                if (JSON.parse(data).found === false) {
                    res.send('{status: false}');
                } else {
                    var returnSentence;
                    console.log(data.toString());
                    request.post('http://localhost:1234/log', {
                      form: {
                        data: data
                      }
                    }, function(err, resultBO) {
                      // console.log(err, res);
                      console.log(JSON.parse(resultBO.body).data);
                      returnSentence = JSON.parse(resultBO.body).data;
                      res.send('{status: true, data: \"' + returnSentence + '\"}');
                    });
                }
                logger(source, fileLog.toString(), JSON.parse(data));
                clearInterval(interval);
            } catch (e) {
                console.log(e);
            }
        }
    }, 500);
    // res.send('Req done');
});

app.post('/result', function(req, res) {
    if (!req.body.result || req.body.result == '') {
        return res.send('{status: false, error: Missing result field.}');
    } else {
        console.log(req.body.result);
    }
    // res.send('Req done');
});

app.listen(3000, function () {
  console.log('NLP Server listening on port 3000!'.green);
});

function logger(input, fileLog, data) {
    var callback = function(err) {
            if (err) {
                return console.log(err);
            }
            console.log('Log saved!');
        };
    if (typeof data.intent != 'undefined') {
        fs.writeFile(logPath + today(), fileLog + '[SUCCESS] [' + datetime() + '] ' + input + ' <-> '  + JSON.stringify(data) + '\n', callback());
    } else {
        fs.writeFile(logPath + today(), fileLog + '[FAILED ] [' + datetime() + '] ' + input + '\n', callback());
    }
}