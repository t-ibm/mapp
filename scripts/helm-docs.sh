#!/bin/bash

# Generates Helm docs for a given chart directory.
# Usage: helm-docs <chart-directory>

[[ -d $1 ]] || { echo -e "Provided parameter is not a directory, exiting!\nUsage: kube-val <chart-directory>"; exit; }

binDir="./.bin"
export PATH="$binDir:$PATH"

#set -euxo pipefail

if ! compgen -G "./$binDir/helm-docs*" > /dev/null; then
  platform=''
  case "$OSTYPE" in
    darwin*)
      platform='darwin'
      ;;
    linux*)
      platform='linux'
      ;;
    msys*)
      platform='windows'
      ;;
    cygwin*)
      platform='windows'
      ;;
    *)
      platform="unknown: $OSTYPE" ;;
  esac

  versionHelmDocs=1.12.0

  if ! [ -d $binDir ]; then
    mkdir $binDir
  fi

  # install helm-docs
  curl --silent --show-error --fail --location --output /tmp/helm-docs.tar.gz "https://github.com/norwoodj/helm-docs/releases/download/v${versionHelmDocs}/helm-docs_${versionHelmDocs}_${platform}_x86_64.tar.gz"
  tar -C $binDir --exclude=LICENSE --exclude=*.md -xf /tmp/helm-docs.tar.gz
fi

# validate docs
helm-docs -c "$1"
git diff --exit-code
