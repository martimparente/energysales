# EnergySales

## Instalation

### Build React SPA

```
cd ./src/energysales-spa/
npm install
```
```
npm run build
```

### Build Ktor API

```
cd ./src/energysales-api/
./gradlew buildFatJar
```

## Execution - Docker Compose

### Production
```
docker compose up
```

### Development Mode - Docker for DB and local for API and SPA

#### API
```
docker compose -f docker-compose-dev.yml up
```at
```
cd ./src/energysales-api/
./gradlew run
```
#### SPA
```
cd ./src/energysales-spa/
npm run dev
```

## Run Tests API

```
cd ./src/energysales-api/
./gradlew test
```

Or just do the build it will also run the tests
```
./gradlew clean build
```