#!/bin/bash

./pull.sh

./gradlew bootWar
java -jar build/libs/workbench-0.war
