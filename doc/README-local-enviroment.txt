# To start redis container locally execute:

docker run -d --name redis-max -p 6379:6379 redis --protected-mode no

# Once the container exists it will be enough to execut:

docker start redis-max
