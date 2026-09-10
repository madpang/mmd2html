#!/bin/bash
# ////////////////////////////////////////////////////////////////
# @file: build.sh
# @brief: Build the devcontainer image
# @author: [t-tang-rfc]
# @date: [created: 2026-09-06, updated: 2026-09-06]
# @usage: ./build.sh
# ////////////////////////////////////////////////////////////////

set -euo pipefail

# Make the script executable independently of the current working directory
script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

docker build --network=host \
	-f "${script_dir}/Dockerfile" \
	-t mmd2html-dev:latest \
	"${script_dir}"
