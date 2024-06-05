#!/bin/bash

IMAGE_NAME="username/my_docker_image:latest"
CONTAINER_NAME="my_container"

docker pull $IMAGE_NAME

docker stop $CONTAINER_NAME || true
docker rm $CONTAINER_NAME || true

docker run -d -p 80:80 --name $CONTAINER_NAME $IMAGE_NAME