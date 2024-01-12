#!/bin/bash

# Recursive version of `helm dep build`.
# Will hopefully become obsolete after: https://github.com/helm/helm/issues/2247
# Usage: helm-dep-build <chart-directory>

[[ -d $1 ]] || { echo -e "Provided parameter is not a directory, exiting!\nUsage: helm-dep-build <chart-directory>"; exit; }

set -eo pipefail

# Remove potentially outdated dependencies
# shellcheck disable=SC2046
rm -f $(find . -name '*.tgz') && rm -f $(find . -name 'Chart.lock')

# Resolve dependencies
refreshed=0
charts=$(find "$1" -name Chart.yaml -print | sed s/Chart.yaml//g | awk -F"/" '{print NF $0}' | sort -nr | sed 's/^.//')
for chart in $charts; do
  echo "Rebuilding directory ${chart}charts/ based on file Chart.lock"
  if helm dep list "$chart" | grep -v '\(^\|WARNING: no dependencies.*\|STATUS\|ok\|unpacked\)\s*$' >/dev/null; then
    if [ "$refreshed" = 0 ]; then
      helm dependency build "$chart"
      refreshed=1
    else
      helm dependency build --skip-refresh "$chart"
    fi
  fi
done
