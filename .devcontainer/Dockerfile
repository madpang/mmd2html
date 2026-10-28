# syntax=docker/dockerfile:1
# /////////////////////////////////////////////////////////////
# @file Dockerfile
# @brief Set up the dev. env. for mmd2html build toolchain.
# @author [t-tang-rfc]
# @date [created: 2026-09-06, updated: 2026-10-28]
# /////////////////////////////////////////////////////////////

# === Use an official Ubuntu 24.04 (noble numbat) as a base image 
# @see https://hub.docker.com/_/ubuntu
# @note Ubuntu image has a default non-root user named `ubuntu`, with user id 1000, group id 1000 and this dockerfile is designed to use that one.
FROM ubuntu:24.04

SHELL ["/bin/bash", "-euc"]

# === Build env. setup
ENV TZ='Asia/Tokyo'
ENV LANG='C.UTF-8'
ENV LC_ALL='C.UTF-8'
ENV COWORK_DIR="/mnt/datastore/cowork"

# --- Install the necessary packages and set up the development environment.
# @note The here doc (<<-EOT) strips leading tabs from the content, allowing for cleaner indentation in the Dockerfile.
RUN <<-EOT
	export DEBIAN_FRONTEND=noninteractive
	apt-get update
	apt-get install -y --no-install-recommends \
		tzdata \
		curl gnupg ca-certificates \
		openssh-client git emacs-nox tmux \
		sudo \
		apt-transport-https software-properties-common \
		tar gzip file binutils \
		build-essential zlib1g-dev
	apt-get clean
	rm -rf /var/lib/apt/lists/*
EOT

# --- Set up Java Environment (GraalVM JDK 25)
# @see https://www.graalvm.org/downloads/
ARG PD_JAVA_INSTALL_DIR="/opt/graalvm"
RUN <<-EOT
	mkdir -p "$PD_JAVA_INSTALL_DIR"
	curl -fL -o "/tmp/graalvm.tar.gz" "https://download.oracle.com/graalvm/25/latest/graalvm-jdk-25_linux-x64_bin.tar.gz"
	tar -xzf "/tmp/graalvm.tar.gz" -C "$PD_JAVA_INSTALL_DIR" --strip-components=1
EOT

# --- Set up workspace for devcontainer
RUN <<-EOT
	mkdir -p "$COWORK_DIR"
	chmod 777 "$COWORK_DIR"
EOT

# === Work env. setup

# --- Create a non-root user
# Create a non-root user with the specified UID and GID and grant sudo privileges
# @note: Ubuntu image has a default non-root user named `ubuntu`, with user id 1000, group id 1000 and this dockerfile is designed to use that one.
ARG PD_USER_NAME="ubuntu"
ARG PD_USER_UID="1000"
ARG PD_USER_GID="$PD_USER_UID"
ARG PD_USER_HOME="/home/$PD_USER_NAME"
RUN <<-EOT
	echo "$PD_USER_NAME ALL=(ALL) NOPASSWD:ALL" > "/etc/sudoers.d/$PD_USER_NAME"
	chmod 0440 "/etc/sudoers.d/$PD_USER_NAME"
EOT

# --- User Experience Enhancements

# Setup SSH
COPY --chmod=0600 "copy/config/default.ssh.config" "/tmp/default.ssh.config"
RUN <<-EOT
	[ -d "$PD_USER_HOME/.ssh" ] || mkdir "$PD_USER_HOME/.ssh"
	chown "$PD_USER_NAME:$PD_USER_NAME" "$PD_USER_HOME/.ssh"
	cp /tmp/default.ssh.config "$PD_USER_HOME/.ssh/config"
	chown "$PD_USER_NAME:$PD_USER_NAME" "$PD_USER_HOME/.ssh/config"
EOT

# Setup tmux
COPY --chmod=0644 "copy/config/default.tmux.config" "/tmp/default.tmux.config"
RUN <<-EOT
	cp "/tmp/default.tmux.config" "$PD_USER_HOME/.tmux.conf"
	chown "$PD_USER_NAME:$PD_USER_NAME" "$PD_USER_HOME/.tmux.conf"
EOT

# Setup GPG
# @note The following directory is automatically created on Ubuntu Desktop, but it does not exist by default for a Ubuntu docker image.
RUN <<-EOT
	mkdir -p -m 0700 "/run/user/$PD_USER_UID"
	chown "$PD_USER_NAME:$PD_USER_NAME" "/run/user/$PD_USER_UID"
EOT

# Create a dir for the repo.
RUN <<-EOT
	mkdir -p "$COWORK_DIR/20_repo/mmd2html"
EOT

# --- Clean up
RUN rm -rf /tmp/*

# === Launchpad

USER "$PD_USER_NAME"

ENV USER="$PD_USER_NAME"
ENV HOME="$PD_USER_HOME"
ENV JAVA_HOME="$PD_JAVA_INSTALL_DIR"
ENV PATH="$JAVA_HOME/bin:$PATH"
ENV SHELL="/bin/bash"

WORKDIR "$COWORK_DIR"

# Set the default command
CMD echo "PLEASE LOGIN TO THE CONTAINER OR USE IT AS DEVCONTAINER" && \
	echo "@host: docker run -it <image-name> bash" && \
	echo "@docker: <your-command-here>"
