### Docker build
```bash
docker-compose up -d
```

### Gradle Build
```bash
./gradlew clean build
```
### Gradle Build for Telegram

```bash
./gradlew bootJar
docker-compose up --build
```
### [Swagger](http://localhost:8080/swagger-ui/index.html)

### [pgAdmin](http://localhost:8888/browser/)
#### Login: `admin@admin.com`  
#### Password: `admin`

### Paste Tokens ONLY in `.env`
