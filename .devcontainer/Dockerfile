# syntax=docker/dockerfile:1
# /////////////////////////////////////////////////////////////
# @file: Dockerfile
# @brief: Set up the dev. env. for mmd2html build toolchain.
# @author: [t-tang-rfc]
# @date: [created: 2026-09-06, updated: 2026-09-06]
# /////////////////////////////////////////////////////////////

# === Use an official Ubuntu 24.04 (noble numbat) as a base image 
# @see: https://hub.docker.com/_/ubuntu
# @note: Ubuntu image has a default non-root user named `ubuntu`, with user id 1000, group id 1000 and this dockerfile is designed to use that one.
FROM ubuntu:24.04

SHELL ["/bin/bash", "-euc"]

# === Build env. setup
ENV TZ='Asia/Tokyo'
ENV LANG='C.UTF-8'
ENV LC_ALL='C.UTF-8'

# Install the necessary packages and set up the development environment.
# @details:
# Primary packages:
# - Temurin JDK 21
# @see: https://adoptium.net/installation/linux
# @note: The here doc (<<-EOT) strips leading tabs from the content, allowing for cleaner indentation in the Dockerfile.

RUN <<-EOT
	export DEBIAN_FRONTEND=noninteractive
	apt-get update
	apt-get install -y --no-install-recommends \
		tzdata \
		wget gnupg ca-certificates \
		openssh-client git nano tmux \
		sudo \
		apt-transport-https software-properties-common
	wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | gpg --dearmor -o /usr/share/keyrings/adoptium.gpg \
		&& echo "deb [signed-by=/usr/share/keyrings/adoptium.gpg] https://packages.adoptium.net/artifactory/deb noble main" > /etc/apt/sources.list.d/adoptium.list
	apt-get update
	apt-get install -y --no-install-recommends \
		temurin-21-jdk
	apt-get clean
	rm -rf /var/lib/apt/lists/*
EOT

# --- Set up Java Environment
RUN <<-EOT
	mkdir -p /opt/java
	ln -s "$(dirname "$(dirname "$(readlink -f "$(command -v java)")")")" /opt/java/openjdk
EOT

# === Work env. setup

# --- Create a non-root user
# Create a non-root user with the specified UID and GID and grant sudo privileges
# @note: Ubuntu image has a default non-root user named `ubuntu`, with user id 1000, group id 1000 and this dockerfile is designed to use that one.
ARG PD_USER_NAME='ubuntu'
ARG PD_USER_UID='1000'
ARG PD_USER_GID=$PD_USER_UID
ARG PD_USER_HOME="/home/${PD_USER_NAME}"
ARG PD_WORKSPACE="${PD_USER_HOME}/wksp"
RUN <<-EOT
	echo "${PD_USER_NAME} ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/$PD_USER_NAME
	chmod 0440 /etc/sudoers.d/$PD_USER_NAME
EOT

# --- User Experience Enhancements
COPY --chmod=0644 copy/config/tmux-conf /tmp/tmux-conf
COPY --chmod=0600 copy/config/ssh-conf /tmp/ssh-conf

# Setup tmux
COPY --chmod=0644 copy/config/tmux-conf /tmp/tmux-conf
RUN <<-EOT
	cp /tmp/tmux-conf $PD_USER_HOME/.tmux.conf
	chown $PD_USER_NAME:$PD_USER_NAME $PD_USER_HOME/.tmux.conf
EOT

# Setup SSH
COPY --chmod=0600 copy/config/ssh-conf /tmp/ssh-conf
RUN <<-EOT
	[ -d "$PD_USER_HOME/.ssh" ] || mkdir $PD_USER_HOME/.ssh
	chown $PD_USER_NAME:$PD_USER_NAME $PD_USER_HOME/.ssh
	cp /tmp/ssh-conf $PD_USER_HOME/.ssh/config
	chown $PD_USER_NAME:$PD_USER_NAME $PD_USER_HOME/.ssh/config
EOT

# Setup GPG
# Create the necessary directory structure for gpg forwarding
# @note: This directory is automatically created on Ubuntu Desktop, but it does not exist by default for a Ubuntu docker image.
RUN <<-EOT
	mkdir -p -m 0700 /run/user/$PD_USER_UID
	chown $PD_USER_NAME:$PD_USER_NAME /run/user/$PD_USER_UID
EOT

# --- Clean up
RUN rm -rf /tmp/*

# === Launchpad

USER $PD_USER_NAME

ENV USER=$PD_USER_NAME
ENV HOME=$PD_USER_HOME
ENV SHELL='/bin/bash'
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

WORKDIR $PD_WORKSPACE

# Set the default command
CMD echo "PLEASE LOGIN TO THE CONTAINER OR USE IT AS DEVCONTAINER" && \
	echo "@host: docker run -it <image-name> bash" && \
	echo "@docker: <your-command-here>"
