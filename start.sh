#!/bin/sh

mvn clean compile exec:java  -Dexec.args="example/contacter/train societe"

node node-server/FakeServer.js
node node-server/index.js
