nlp-intent-toolkit - Webserver version
==================

[![Build Status](https://travis-ci.org/Net-and-Work/nlp-intent-toolkit.svg?branch=master)](https://travis-ci.org/Net-and-Work/nlp-intent-toolkit)

Recognizing intents with slots using OpenNLP.

This is an example of using OpenNLP to train a system to accept natural language input, particularly via a speech-to-text source, and return a recognized action with arguments. The system uses [document categorization](https://opennlp.apache.org/documentation/1.5.3/manual/opennlp.html#tools.doccat) to determine the action for inputs and [entity recognition](https://opennlp.apache.org/documentation/1.5.3/manual/opennlp.html#tools.namefind) to determine the arguments. The training system requires a directory containing separate files for each possible action, in this case the actions in a fictitious weather application:

```
- example/weather/train
  - current-weather.txt - get the current weather
  - hourly-forecast.txt - get the hourly forcast
  - five-day-forecast.txt - get a five day forecast
```

Each training file contains one example per line with any possible arguments surrounded by mark up to indicate the name of the parameter:

```
file: five-day-forecast.txt
...
how dos the weather look for this Thursday in <START:city> Boston <END>
is it going to snow this week in <START:city> Chicago <END>
show me the forecast for <START:city> Denver <END>
...

```


## Start the node server

The webserver is node based and listens to port 3000.

```
$ mkdir node-server
$ npm install
$ node index.js
```

## Request exemple

Query
```
$ curl --data "enquiry=how does this weekend look in boston" -X POST http://localhost:3000
```

Response
```
{status: true, data: {action:five-day-forecast },args:{city:Boston }}
```
