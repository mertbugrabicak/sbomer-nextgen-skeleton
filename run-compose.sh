#!/usr/bin/env bash

pushd errata-tool-adapter || exit
mvn clean package
popd

podman-compose down
podman-compose up --build