#!/usr/bin/env bash

set -e

function print {
  echo ">>> $1"
}

function usage() {
  print "Usage: $0 [-c] [-h] [-b] [-v] [-r]"
  print "  -b, --build    Build holiday-info app with maven before running"
  print "  -c, --clean    Remove previous environment before running new one"
  print "  -r, --remote   Add remote debugger access for holiday-info-server"
  print "  -h, --help     Print this help message"
  print ""
  print "Example: $0"
  print "Example: $0 --build"
  exit 1
}

BUILD="false"
CLEAN="false"
REMOTE="false"

while [[ "$#" -gt 0 ]]; do
    case $1 in
        -b|--build) BUILD="true"; shift ;;
        -c|--clean) CLEAN="true"; shift ;;
        -r|--remote) REMOTE="true"; shift ;;
        -h|--help) usage;;
        *) print "Unknown parameter passed: $1"; exit 1 ;;
    esac
done

if [ "${CLEAN}" == "true" ]
then
  docker compose down -v --remove-orphans
fi

if [ "${BUILD}" == "true" ]
then
  mvn clean install
fi

DOCKER_COMPOSE_OPTIONS=""
if [ "${REMOTE}" == "true" ]; then
  DOCKER_COMPOSE_OPTIONS+="-f docker-compose.remote.yaml"
fi

print "Run environment"
docker compose -f docker-compose.yaml ${DOCKER_COMPOSE_OPTIONS} up -d
