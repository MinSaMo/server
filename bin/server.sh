#!/bin/bash
git pull
cd ..
./gradlew build
java -jar build/libs/demo-mock.jar