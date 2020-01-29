#!/bin/bash
set -x #echo on

./mvnw package && LIGHTSTEP_ACCESS_TOKEN="<TOKEN>" LIGHTSTEP_PROTOCOL="https" LIGHTSTEP_HOST="ingest.lightstep.com" LIGHTSTEP_PORT="443" java -jar target/gs-spring-boot-docker-0.1.0.jar