#!/usr/bin/env bash

pushd errata-tool-adapter
mvn clean package
popd

podman-compose down
podman-compose up --build