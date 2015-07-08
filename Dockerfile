# This file is set up to build gocd-slack-build-notifier on a remote docker
# instance.

FROM ubuntu:latest

RUN apt-get update && apt-get -y install default-jdk maven

WORKDIR /build

# Cache dependencies so we don't have to re-download, from:
# https://keyholesoftware.com/2015/01/05/caching-for-maven-docker-builds/
ADD pom.xml /build/pom.xml
RUN mvn verify clean --fail-never

# Build source code into a jar.
ADD . /build
RUN mvn package

