docker pull mongo:3.6.14
docker rm -f mongo-fander
docker run -d --name mongo-fander -p 27017:27017 mongo:3.6.14
pause
