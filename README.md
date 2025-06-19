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
#### Reference of `.env`:
```
#TELEGRAM
TELEGRAM_BOT_USERNAME=ruummanager_bot
TELEGRAM_BOT_TOKEN="YOUR_TOKEN"

#GEMINI
GEMINI_ID="PROJECT_ID"
GEMINI_TOKEN="YOUR_TOKEN"
spring.ai.vertex.ai.gemini.location=europe-west1
GCP_PROJECT_ID="PROJECT_ID"
GCP_LOCATION=europe-west1
```