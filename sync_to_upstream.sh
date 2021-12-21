#! /usr/bin/env bash

# USAGE: RELEASE_VERSION=< Git tag of the version to publish > ./sync_to_upstream.sh

if [[ ! -z "${RELEASE_VERSION}" ]]; then
    echo "Sync version ${RELEASE_VERSION} to Solarisbank/identhub-android"
else
	echo "Set the RELEASE_VERSION to sync 🙏🏻"
  exit
fi

if [[ $(git diff --stat) != '' ]]; then
  echo 'Commit or stash your local changes before continuing with the sync'
  exit
fi

git checkout -b $RELEASE_VERSION
git push "https://github.com/Solarisbank/identhub-android.git" main
git checkout main
git branch -D $RELEASE_VERSION
