#!/bin/bash

# Copy al required application(*).properties and bootstrap(*).properties for specified profile.
# Transform them to default profile for docker-compose.

source docker_config.sh

# target dir
TARGET=target

# properties repository location
CONFIG_REPO="$(realpath ../"$CONFIG_REPO_NAME")"

# destination configserver properties folder
TARGET_CONFIG_REPO=$TARGET/configserver/"${CONFIG_REPO_NAME}"


# cd into compose dir
cd zinfrastructure/compose/

# clean TARGET
rm -f -R $TARGET

## create TARGET dirs
#for name in "${modules[@]}"
#do
#  mkdir -p $TARGET/"$name"
#done

# append "-" at beginning of non-default Spring profile
if [ -n "$PROFILE" ]
then
  PREFIX="-"
fi

# create target/ dir
mkdir -p $TARGET

# substitute TAG (to current version, assigned before) in docker-compose.yml and cp it to target
envsubst < docker-compose.yml > $TARGET/docker-compose.yml
#cp docker-compose.yml $TARGET/docker-compose.yml


# cp Start.sh, Stop.sh
cp start.sh $TARGET/start.sh
cp start-interactive.sh $TARGET/start-interactive.sh
cp stop.sh $TARGET/stop.sh
cp config $TARGET/config

# CONFIGSERVER - copy config (application)
mkdir -p $TARGET/configserver

OUT_CONFIG_FILE=$TARGET/configserver/application.properties
cp "$CONFIG_REPO"/configserver/"${PROFILE}"/application"${PREFIX}""${PROFILE}".properties $OUT_CONFIG_FILE

# append spring boot "native" (non-git source for spring config server) profile
printf "\n" >> "$OUT_CONFIG_FILE"
echo "# spring boot 'native', profiles" >> "$OUT_CONFIG_FILE"
echo "spring.profiles.include=native" >> "$OUT_CONFIG_FILE"


# append spring boot "log2json" (enable elasticsearch json logs) profile
if [ "${ENABLE_LOG2JSON}" = true ]; then
  echo "# spring boot 'native','log2json' profiles" >> "$OUT_CONFIG_FILE"
  echo "spring.profiles.include=native,log2json" >> "$OUT_CONFIG_FILE"
fi




# All Modules - copy configs (application,bootstrap) to target/
for name in "${modules[@]}"
do

   # config server doesn't have bootstrap properties
   if [ "$name" != configserver ]
   then

     # bootstrap property
     PROP="$CONFIG_REPO"/"$name"/"${PROFILE}"/bootstrap"${PREFIX}""${PROFILE}".properties
     if [ ! -f "$PROP" ]; then
       continue
     fi


     # copy bootstrap configs from CONFIG_REPO to TARGET/service/
     # transform bootstrap profile to default for docker-compose

     mkdir -p $TARGET/"$name"
     cp "$PROP" $TARGET/"$name"/bootstrap.properties

     # set profile to default in spring.cloud.config.profile=default in bootstrap.properties
     perl -pi -e 's/(?<=spring.cloud.config.profile=)[a-zA-Z0-9_\-]+/default/g' $TARGET/"$name"/bootstrap.properties
   fi

##   # copy configs from local config-repo to TARGET/configserver/amprocessor-config (configserver config-repo)
##   # to allow configserver share properties to modules
##   TMP="$TARGET_CONFIG_REPO"/"$name"/"${PROFILE}"
##   mkdir -p "$TMP"
##   cp "$CONFIG_REPO"/"$name"/"${PROFILE}"/application"${PREFIX}""${PROFILE}".properties "$TMP"/application"${PREFIX}""${PROFILE}".properties

   # application property
   PROP="$CONFIG_REPO"/"$name"/"${PROFILE}"/application"${PREFIX}""${PROFILE}".properties
   if [ ! -f "$PROP" ]; then
     continue
   fi


   # copy configs from CONFIG_REPO to TARGET/configserver/TARGET_CONFIG_REPO (configserver config-repo)
   # to allow configserver to work
   # transform current profile to default for docker-compose
   DEFAULT="$TARGET_CONFIG_REPO"/"$name"/default

   mkdir -p $TARGET/"$name"
   mkdir -p "$DEFAULT"

   OUT_CONFIG_FILE="$DEFAULT"/application.properties
   cp "$PROP" "$OUT_CONFIG_FILE"

   # append spring boot profile "log2json" to log to Elasticsearch
   if [ "${ENABLE_LOG2JSON}" = true ]; then
     printf "\n" >> "$OUT_CONFIG_FILE"
     echo "# append spring boot profile 'log2json'" >> "$OUT_CONFIG_FILE"
     echo "spring.profiles.include=log2json" >> "$OUT_CONFIG_FILE"
   fi

done


# copy filebeat config
mkdir -p $TARGET/filebeat
cp "$CONFIG_REPO"/filebeat/filebeat.yml $TARGET/filebeat/


#
## sqrabber
#cp CONFIG_REPO/configserver/"${PROFILE}"/application"${PROFILE}".properties $TARGET/configserver/application.properties
#
## sqrabber
#cp ../../sgrabber/src/main/resources/application"${PROFILE}".properties $TARGET/sgrabber/application.properties
#
## flexporter
#cp ../../flexporter/src/main/resources/application"${PROFILE}".properties $TARGET/sgrabber/application.properties

#cp ../../bot/src/main/resources/application"${PROFILE}".properties $TARGET/bot/application.properties
#cp ../../bot/src/main/resources/bot"${PROFILE}".properties $TARGET/bot/bot.properties
#
#cp ../../sgrabber/src/main/resources/application"${PROFILE}".properties $TARGET/sgrabber/application.properties
#cp ../../sgrabber/src/main/resources/sgrabber"${PROFILE}".properties $TARGET/sgrabber/sgrabber.properties
#
#cp ../../flexporter/src/main/resources/application"${PROFILE}".properties $TARGET/flexporter/application.properties
#cp ../../flexporter/src/main/resources/flexporter"${PROFILE}".properties $TARGET/flexporter/flexporter.properties


# restore original project root dir
