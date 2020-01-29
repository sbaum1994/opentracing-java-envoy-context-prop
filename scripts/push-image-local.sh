#!/bin/bash
set -x #echo on

TAG=$(git log -1 --pretty=%h)

SOURCE=spring-app-$TAG:latest
TARGET=localhost:5000/$SOURCE

docker tag $SOURCE $TARGET
docker push $TARGET