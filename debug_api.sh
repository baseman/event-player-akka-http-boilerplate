#!/usr/bin/env bash
java -Xdebug -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n -jar ./my-api/build/libs/my-api-0.0.1-SNAPSHOT.jar