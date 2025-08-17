#!/bin/bash

#set -e 

cd task/

./gradlew clean build

./gradlew bootRun

exit 0
