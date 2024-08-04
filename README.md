
# 개발 설정
docker run --cpus=1 -m 2048m -d -p 27017:27017 --name mongo7 -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=powpow mongo:7