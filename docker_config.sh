#!/usr/bin/env bash

# Docker TAG
#export TAG=latest
export TAG=0.41

# modules
declare -a modules=("configserver" "sgrabber" "flexporter" "alerthandler" "telebot" "mailer" "cameradetails")
export modules


export CONFIG_REPO_NAME=amprocessor-config


# Docker no tag -> latest tag
if [ -z "$TAG" ]
then
  TAG=latest
fi






# formatting stuff -----------------

export RED='\033[0;31m'
export YELLOW='\033[1;33m'
export NC='\033[0m' # No Color
