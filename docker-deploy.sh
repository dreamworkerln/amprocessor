#!/usr/bin/env bash

trap "exit" INT

source docker_config.sh

# Only prepare config for local docker run, then exit script
# (don't docker push to registry, don't deploy to server)
MAKE_CONFIG_ONLY=false

# Only push docker images to registry, then exit script
# (don't deploy to server)
PUSH_TO_REGISTRY_ONLY=false

# Push docker images to registry
PUSH_TO_REGISTRY=true

# spring profile may be empty -> will be used default profile
#export PROFILE=dev
#(dev/prod)
export PROFILE=prod

# enable json logging for elasticsearch
export ENABLE_LOG2JSON=true

# Using ansible host group
export HOST=amprocessor_host


if [ -z "$HOST" ]
then
  echo "Target host not specified."
  exit
fi


echo "Using spring profile:  $PROFILE"
echo "Using docker tag:      $TAG"
echo "Deploying to host:     $HOST"
echo "---"
echo "Use LOG2JSON:          $ENABLE_LOG2JSON"
echo "Make config only:      $MAKE_CONFIG_ONLY"
echo "Push to registry:      $PUSH_TO_REGISTRY"
echo "Push to registry only: $PUSH_TO_REGISTRY_ONLY"


# export local docker images to tar archive
#rm ztmp/*
#mkdir -p ztmp
#docker save dreamworkerln/amprocessor-:latest > ztmp/alertmanager-tbot.tar
#docker save dreamworkerln/amprocessor-:latest > ztmp/alertmanager-monitor.tar
#docker save dreamworkerln/amprocessor-:latest > ztmp/alertmanager-relay.tar


echo -e "\n${YELLOW}Prepare applications properties${NC}"
(
  zinfrastructure/compose/copy_properties.sh
)

# Exit if only make configs
if [ "${MAKE_CONFIG_ONLY}" = true ]; then
  exit 0;
fi


# pushing all modules
if [ "${PUSH_TO_REGISTRY}" = true ]; then

  for name in "${modules[@]}"
  do
    echo -e "\n${YELLOW}Pushing image: $name:$TAG${NC}"
    docker push dreamworkerln/amprocessor-"${name}":"$TAG"
  done

  # pushing additional images (non java)
  name="database"
  echo -e "\n${YELLOW}Pushing image: $name:$TAG${NC}"
  docker push dreamworkerln/amprocessor-"${name}":"$TAG"
fi


# Exit if only push to registry
if [ "${PUSH_TO_REGISTRY_ONLY}" = true ]; then
  exit 0;
fi

echo "=============== EXECUTE ANSIBLE ==============="

# subshell
(
  cd zinfrastructure/ansible || exit
  # install docker, docker-compose on remote host
  #ansible-playbook -i inventory amprocessor-deploy.yml --extra-vars "variable_host=$HOST" --tags install_docker
  ansible-playbook -i inventory amprocessor-deploy.yml --extra-vars "variable_host=$HOST tag=$TAG"
)


#echo "PULLING IN REMOTE ==============================================================="
#
#ssh p1-root bash -c "'
#docker pull dreamworkerln/amprocessor-telebot;
#docker pull dreamworkerln/amprocessor-sgrabber;
#docker pull dreamworkerln/amprocessor-flexporter;
#'"

#ssh u3-root bash -c "'
#docker pull dreamworkerln/amprocessor-relay
#'"

