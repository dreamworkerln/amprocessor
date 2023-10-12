#!/bin/bash

set -a
source ./config

docker-compose -p database up -d
