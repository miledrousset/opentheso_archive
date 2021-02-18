# Building the Docker Image

The configuration for two Docker Images are provided; one image for Opentheso itself, and the other for a Postgres database which holds the Opentheso data. 

```
docker image build -t miledrousset/opentheso-postgres -f Dockerfile-postgres .

docker image build -t miledrousset/opentheso .
```


# Running the Docker Containers

There are two options for running the Docker Containers:
1. Using Docker Compose to manage the containers for you
2. Using Docker directly to start each container

Pre-requisites:
1. Docker
2. Place a copy of `opentheso-4.5.9.war` in the `opentheso/docker` directory.


## Running via Docker Compose

Simply execute:

```
cd opentheso/docker

docker compose up
```

## Running directly with Docker

A Docker Container for the Opentheso Posgres database must be started before starting a container for Opentheso itself.

```
cd opentheso/docker

docker run --name opentheso-db --volume opentheso-pgdata:/pgdata --env POSTGRES_USER=opentheso --env POSTGRES_PASSWORD=opentheso --env PGDATA=/pgdata miledrousset/opentheso-postgres

docker run --name opentheso --link opentheso-db --publish 8080:8080 -it miledrousset/opentheso
```

## Accessing Opentheso

Once the Docker Containers are running, you can access Opentheso in a web-browser by visiting: http://localhost:8080/opentheso


# Docker Volumes

The docker volume `opentheso-pgdata` stores the database, if you want to start with a clean slate you can remove the volume and it will be re-created. To remove the volume just run:

```
docker volume rm opentheso-pgdata
```