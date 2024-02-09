Create postgres image in docker   
docker run --name urlData -e POSTGRES_PASSWORD=admin -p 5432:5432 -d postgres  
Create database in image  
docker exec -it urlData psql -U postgres -c "CREATE DATABASE urlData"  
Or docker-compose.yml  
version: '3.1'

services:
db:
image: postgres
restart: always
environment:
POSTGRES_PASSWORD: admin
volumes:
- ./init.sql:/docker-entrypoint-initdb.d/init.sql
ports:
- "5432:5432"
  
init.sql: CREATE DATABASE urlData;
