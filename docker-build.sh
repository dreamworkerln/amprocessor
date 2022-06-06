#!/usr/bin/env bash

trap "exit" INT

source docker_config.sh

# проверяем, установлен ли maven, если да, то собираем проект

echo -e "${YELLOW}Java compiling...${NC}"
if command -v mvn &> /dev/null
then
  mvn -DskipTests clean package
fi


#echo -e "${YELLOW}Building docker images with tag: $TAG${RED}"

for name in "${modules[@]}"
do

  DOCKERFILE="$(realpath zinfrastructure/docker/"${name}"/Dockerfile)"

  # skip library modules (with no Dockerfile)
  if [ ! -f "$DOCKERFILE" ]; then
    continue
  fi

  # extract jar content to /target/extracted folder
  mkdir "$name"/target/extracted
  java -Djarmode=layertools -jar "$name"/target/*.jar extract --destination "$name"/target/extracted

  echo -e "\n${YELLOW}Building image: $name:$TAG${NC}"

  # chenge dir will shrink docker context, run in subshell
  (
    cd "$name"
    docker build -t dreamworkerln/amprocessor-"$name":"$TAG" -f "$DOCKERFILE" .
  )
done



# build additional docker images (no java sources)

# database
name="database"
echo -e "\n${YELLOW}Building image: $name:$TAG${NC}"
DOCKERFILE="$(realpath zinfrastructure/docker/"${name}"/Dockerfile)"
(
  cd zinfrastructure/docker/"${name}"
  docker build -t dreamworkerln/amprocessor-"$name":"$TAG" -f "$DOCKERFILE" .
)




#docker build -t dreamworkerln/amprocessor-configserver:"$TAG" -f zinfrastructure/docker/configserver/Dockerfile .
#docker build -t dreamworkerln/amprocessor-sgrabber:"$TAG" -f zinfrastructure/docker/sgrabber/Dockerfile .
#docker build -t dreamworkerln/amprocessor-flexporter:"$TAG" -f zinfrastructure/docker/flexporter/Dockerfile .
#docker build -t dreamworkerln/amprocessor-alerthandler:"$TAG" -f zinfrastructure/docker/alerthandler/Dockerfile .
#docker build -t dreamworkerln/amprocessor-telebot:"$TAG" -f zinfrastructure/docker/telebot/Dockerfile .
#docker build -t dreamworkerln/amprocessor-cameradetails:"$TAG" -f zinfrastructure/docker/cameradetails/Dockerfile .


#FREE_BYTES=$(df "$PWD" | awk '/[0-9]%/{print $(NF-2)}')
#if [ "$FREE_BYTES" -lt 1000000 ]; then
#    echo !!!!!!!!!!!!!!!!!!!!!!  LOW DISK SPACE  !!!!!!!!!!!!!!!
#fi

