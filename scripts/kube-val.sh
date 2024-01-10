#!/bin/bash

# Validates all templates under a given chart directory against $KUBERNETES_VERSION.
# Usage: kube-val <chart-directory>

[[ -d $1 ]] || { echo -e "Provided parameter is not a directory, exiting!\nUsage: kube-val <chart-directory>"; exit; }

binDir="./.bin"
export PATH="$binDir:$PATH"

#set -euxo pipefail

if ! compgen -G "./$binDir/kubeval*" > /dev/null; then
  extension=''
  platform=''
  case "$OSTYPE" in
    darwin*)
      platform='darwin'
      extension='tar.gz'
      ;;
    linux*)
      platform='linux'
      extension='tar.gz'
      ;;
    msys*)
      platform='windows'
      extension='zip'
      ;;
    cygwin*)
      platform='windows'
      extension='zip'
      ;;
    *)
      platform="unknown: $OSTYPE" ;;
  esac

  # from: datasource=github-releases depName=kubeval packageName=instrumenta/kubeval
  versionKubeval=v0.16.1

  # from: datasource=github-releases depName=semver2 packageName=Ariel-Rodriguez/sh-semversion-2
  versionSemversion=v1.0.5

  if ! [ -d $binDir ]; then
    mkdir $binDir
  fi

  # install kubeval
  curl --silent --show-error --fail --location --output "/tmp/kubeval.$extension" "https://github.com/instrumenta/kubeval/releases/download/${versionKubeval}/kubeval-${platform}-amd64.$extension"
  if [[ $platform == 'windows' ]]; then
    unzip -d $binDir "/tmp/kubeval.$extension" -x LICENSE "*.md"
  else
    tar -C $binDir --exclude=LICENSE --exclude=*.md -xf "/tmp/kubeval.$extension" kubeval
  fi

  # install semver compare
  curl -sSfLo $binDir/semver2 https://raw.githubusercontent.com/Ariel-Rodriguez/sh-semversion-2/${versionSemversion}/semver2.sh
  chmod +x $binDir/semver2
fi

# compute required kubernetes api versions
apis=()

# default Kubernetes version
: "${KUBERNETES_VERSION:=v1.20.15}"

if [[ "$(semver2 "${KUBERNETES_VERSION#v}" 1.21.0)" -ge 0 ]]; then
  apis=("${apis[@]}" --api-versions batch/v1/CronJob)
else
  apis=("${apis[@]}" --api-versions batch/v1beta1/CronJob)
fi

# validate charts
schemaLocation="https://raw.githubusercontent.com/yannh/kubernetes-json-schema/master"
echo "Validating templates under directory $1 against Kubernetes version ${KUBERNETES_VERSION#v}"
helm template \
  "${apis[@]}" \
  --values "$1/values.yaml" \
  "$1" | kubeval \
    --strict \
    --kubernetes-version "${KUBERNETES_VERSION#v}" \
    --schema-location "$schemaLocation"
