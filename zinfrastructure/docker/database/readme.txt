docker build -t dreamworkerln/amprocessor-database:${TAG} -f Dockerfile .
docker push dreamworkerln/amprocessor-database:${TAG}

run docker database
docker run --name database -e POSTGRES_PASSWORD=gjhUY876787ytuh87gdfzk5 -e POSTGRES_USER=cameras_user -e POSTGRES_DB=cameras -p 5432:5432 -v postgres-data:/var/lib/postgresql/data -d dreamworkerln/amprocessor-database:${TAG}


