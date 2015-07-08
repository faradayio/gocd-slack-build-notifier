#!/bin/bash
#
# Build our GoCD plugin.  Designed to be called from GoCD, with builds
# potentially run on a separate machine (so we don't use volumes).

# Standard paranoia.
set -e
set -u
set -o xtrace

IMAGE="$GO_PIPELINE_NAME-$GO_PIPELINE_COUNTER"
CONTAINER="$IMAGE-container"

docker build -t "$IMAGE" .
docker run --name "$CONTAINER" "$IMAGE" bash -c "echo Doing nothing."
mkdir -p target
rm -f target/gocd-slack-notifier-*.jar
docker cp "$CONTAINER":/build/target/gocd-slack-notifier-1.1.jar target/

docker rm "$CONTAINER"
docker rmi "$IMAGE"
