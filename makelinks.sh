#!/usr/bin/env bash


# Run this script before working with amprocessor in Intellij Idea
#
# This script create required symlinks
#
# CONFIG_REPO - directory, spring cloud repository with applications properties
# it's name configured in file docker_config.sh
# (spring cloud server configured to use directory (added native profile), not git repository)
#
# This script do:
# 1. make symbolic links from CONFIG_REPO to modules resources dirs
# (without this you can't run/debug apps from intellij idea)
#
# 2. make symlink from ../CONFIG_REPO_NAME to ./CONFIG_REPO_NAME
# (without this configserver won't work)
#
# CONFIG_REPO should be located same level to amprocessor
#
# amprocessor
# amprocessor-config

source docker_config.sh

RES_DIR=src/main/resources
RES_DIR_TEST=src/test/resources

APP=application
BOOT=bootstrap

CURRENT_DIR=$(pwd)
CONFIG_REPO="$(realpath ../"$CONFIG_REPO_NAME")"
#HBM2DDL=create-drop


# make link from ../$CONFIG_REPO_NAME to ./$CONFIG_REPO_NAME
# configserver will search for conf repositiory in pwd that in intellij idea debug is amprocessor dir
if [ ! -d "$CONFIG_REPO_NAME" ]; then
    ln -s ../"$CONFIG_REPO_NAME" .
fi

profiles=("dev" "test")

# add core-db module (contain properties for tests)
modules+=('core/core-db')

for name in "${modules[@]}"
do

  for profile in "${profiles[@]}"
  do



    # append "-" at beginning of non-default Spring profile
    if [ "$profile" = "" ] || [ "$profile" = "default" ]; then
      PREFIX=""
    else
      PREFIX="-"
    fi


    if [ "$profile" = "test" ]; then
      RES="$CURRENT_DIR"/"$name"/"$RES_DIR_TEST"
    else
      RES="$CURRENT_DIR"/"$name"/"$RES_DIR"
    fi

    if [ ! -d "$RES" ]; then
      continue
    fi

    cd "$RES"

    # delete old application[-]*.properties bootstrap[-]*properties symlinks
    rm "$APP""$PREFIX""$profile".properties "$BOOT""$PREFIX""$profile".properties 2> /dev/null

    # source property file
    PROP="$CONFIG_REPO"/"$name"/"$profile"



    # Maven resource plugin is mad about relative symbolic links

#    echo $(pwd)
#    echo "$PROP"/"$APP""$PREFIX""$profile".properties
#    echo "$PROP"/"$BOOT""$PREFIX""$profile".properties

    if [ -f "$PROP"/"$APP""$PREFIX""$profile".properties ]; then
        ln -s "$PROP"/"$APP""$PREFIX""$profile".properties .
    fi

    if [ -f "$PROP"/"$BOOT""$PREFIX""$profile".properties ]; then
        ln -s "$PROP"/"$BOOT""$PREFIX""$profile".properties .
    fi
  done
done






# configserver read .properties from dir, not git repo, so need to add native profile to it thru application.properties
#cd "$CURRENT_DIR"/configserver/"$RES_DIR_TEST"
#if [ ! -f "application.properties" ]; then
#  echo "# bootstrap for mvn testing, add active profiles" >> application.properties
#  echo "spring.profiles.active=native,dev" >> application.properties
#fi

#
## manually copy properties from 'cameradetails' to db tests =================================================================
#
## module core-db/db/tests
#cd "$CURRENT_DIR"/core/core-db/"$RES_DIR_TEST"
#DIRNAME=../../../../../../$CONFIG_REPO_NAME/cameradetails/dev
#DIRNAME_FULL="$(realpath "$DIRNAME")"
#if [ -f "$APP" ]; then
#  echo exists $(pwd)/"$APP"
#else
#    cp "$DIRNAME_FULL"/"$APP" .
#    perl -pi -e 's/(?<=spring.jpa.properties.hibernate.hbm2ddl.auto=)[a-zA-Z0-9_\-]+/'$HBM2DDL'/g' "$APP"
#fi
#
#if [ -f "$BOOT" ]; then
#  echo exists $(pwd)/"$BOOT"
#else
#    cp "$DIRNAME_FULL"/"$BOOT" .
#    perl -pi -e 's/(?<=spring.jpa.properties.hibernate.hbm2ddl.auto=)[a-zA-Z0-9_\-]+/'$HBM2DDL'/g' "$BOOT"
#fi
#
#
## module cameradetails/tests
#cd "$CURRENT_DIR"/cameradetails/"$RES_DIR_TEST"
#DIRNAME=../../../../../$CONFIG_REPO_NAME/cameradetails/dev
#DIRNAME_FULL="$(realpath "$DIRNAME")"
#if [ -f "$APP" ]; then
#    echo exists $(pwd)/"$APP"
#else
#    cp "$DIRNAME_FULL"/"$APP" .
#    perl -pi -e 's/(?<=spring.jpa.properties.hibernate.hbm2ddl.auto=)[a-zA-Z0-9_\-]+/'$HBM2DDL'/g' "$APP"
#fi
#
#if [ -f "$BOOT" ]; then
#  echo exists $(pwd)/"$BOOT"
#else
#  cp "$DIRNAME_FULL"/"$BOOT" .
#  perl -pi -e 's/(?<=spring.jpa.properties.hibernate.hbm2ddl.auto=)[a-zA-Z0-9_\-]+/'$HBM2DDL'/g' "$BOOT"
#fi







