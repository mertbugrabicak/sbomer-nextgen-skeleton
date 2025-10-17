#!/bin/env/bash

podman-compose down

echo "Building the application with Maven..."
mvn clean package

echo "Building the application image and starting all services..."
podman-compose up --build