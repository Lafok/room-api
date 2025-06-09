### Docker build
```bash
docker-compose up -d
```

### Gradle Build
```bash
./gradlew clean build
```
### [Swagger](http://localhost:8080/swagger-ui/index.html)

### [pgAdmin](http://localhost:8888/browser/)
#### Login: `admin@admin.com`  
#### Password: `admin`

### Git Ignore For Tokens
```bash
    git update-index --assume-unchanged src/main/resources/application.properties
```