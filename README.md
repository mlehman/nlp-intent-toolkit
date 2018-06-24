nlp-intent-toolkit
==================

Recognizing intents with slots using OpenNLP for applications (such as bots using chat, IM, speech-to-text) to convert natural language into structured commands with arguments.

```
> What's the current stock price of General Motors?
{ action: 'stock-price', args: { company: 'General Motors' } }
```

The example system uses [document categorization](https://opennlp.apache.org/documentation/1.5.3/manual/opennlp.html#tools.doccat) to determine the intent (command) and [entity recognition](https://opennlp.apache.org/documentation/1.5.3/manual/opennlp.html#tools.namefind) to determine the slots (arguments) of the natural language text input. 

The training system uses a directory containing separate files for each possible action, in this case the actions in a fictitious weather application:

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
{ action: 'current-weather', args: { city: 'chicago' } }

>will it rain tonight?
{ action: 'hourly-forecast', args: { } }

>how does it look in seattle
{ action: 'hourly-forecast', args: { city: 'seattle' } }

>what are the conditions in new york?
{ action: 'current-weather', args: { city: 'new york' } }

>how does this weekend look in boston
{ action: 'five-day-forecast', args: { city: 'boston' } }

>give me the five day forecast
{ action: 'five-day-forecast', args: { } }
```

## Copyright and License

The source code provided by this repository is free and unencumbered to be copied or modified for any purpose, commercial or non-commercial.
The full license text is provided in the LICENSE file accompanying this repository.
