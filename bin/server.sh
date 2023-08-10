#!/bin/bash
git fetch --all
git reset --hard origin/main
cd ..
./gradlew build
java -jar build/libs/demo-mock.jar