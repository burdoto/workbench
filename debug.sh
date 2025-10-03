#!/bin/bash

./pull.sh

./gradlew bootWar
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar build/libs/workbench-0.war
