#!/bin/bash
set -x #echo on

rm -rf target/dependency

./mvnw package
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
TAG=$(git log -1 --pretty=%h)
docker build -t spring-app-$TAG .