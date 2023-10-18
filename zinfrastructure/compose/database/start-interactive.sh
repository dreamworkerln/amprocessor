#!/bin/bash

source ~/.bashrc

set -a
source ./config

docker compose -p database up
