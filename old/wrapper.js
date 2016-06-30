var fs = require('fs');
var spawn = require('child_process').spawn;
var prc = spawn('mvn', ['clean', 'compile', 'exec:java', '-Dexec.args="example/weather/train\""']);

prc.stdout.setEncoding('utf8');
prc.stdout.on('data', function (data) {
    var str = data.toString()
    var lines = str.split(/(\r?\n)/g);
    console.log(lines.join(""));
});

prc.on('close', function (code) {
    console.log('process exit code ' + code);
});
