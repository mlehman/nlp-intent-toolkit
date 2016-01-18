nlp-intent-toolkit
==================

Recognizing intents with slots using OpenNLP.

This is an example of using OpenNLP to train a system to accept natural language input, particularly via a speech-to-text source, and return a recognized action with arguments. The system uses [document categorization](https://opennlp.apache.org/documentation/1.5.3/manual/opennlp.html#tools.doccat) to determine the action for inputs and [entity recognition](https://opennlp.apache.org/documentation/1.5.3/manual/opennlp.html#tools.namefind) to determing the arguments. The training system requires a directory containing separate files for each possible action, in this case the actions in a fictitious weather application:

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

## Running the Example

The training systems is run passing in the training file directory and any parameter name used in the training files.

```
$ mvn clean compile exec:java  -Dexec.args="example/weather/train city"
...
Training complete. Ready.

>show me the weather for chicago
action=current-weather args={ city=chicago }

>will it rain tonight
action=hourly-forecast args={ }

>how does it look in seattle
action=hourly-forecast args={ }

>what are the conditions in new york
action=current-weather args={ city=new york }

>how does this weekend look in boston
action=five-day-forecast args={ city=boston }

>give me the five day forecast
action=five-day-forecast args={ }
```
