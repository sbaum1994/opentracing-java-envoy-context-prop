#!/bin/bash
set -x #echo on

# Trigger a redeploy by doing a nonsense change
kubectl patch deployment spring-app -p "$(printf '{"spec":{"template":{"metadata":{"labels":{"date":"%s"}}}}}' `date +%s`)"