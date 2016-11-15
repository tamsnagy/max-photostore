# To start redis container locally execute:

docker run -d --name redis-max -p 6379:6379 redis --protected-mode no

# To start posgres container locally execute:
docker run -d --name postgres-max -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres


# Once the containers exist you can start them by:
docker start redis-max
docker start postges-max
