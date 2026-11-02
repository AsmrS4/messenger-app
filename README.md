# Messenger App

## Описание проекта
Небольшое микросервисное приложение для демонстрации работы библиотеки log4j-api. 
## Презентация
https://docs.google.com/presentation/d/1YtXAFJ4M54Xgj58ngvSz744BvrVdr5YqmGBkyVQ0f38/edit?usp=sharing

# Автор
Сунчугашев Аймир Евгеньевич, 972303

## Рассматриваемая библиотека 
Log4j-api (v. 2.24.1)
```gradle
dependencies {
	implementation 'org.apache.logging.log4j:log4j-api:2.24.1'
}
```
Ссылка на репозиторий 
```
 https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api/2.24.1
```

## Сборка и запуск проекта

Для запуска проекта необходимо:
1. Склонировать репозиторий:
```bash
https://github.com/AsmrS4/messenger-app.git
cd messenger-app/backend
```
2. Ввести команду для сборки и запуска контейнеров:
```bash
docker compose up -d
```
После сборки и запуска контейнеров сервисы будут запущены на портах 8001 и 8002.

Для тестирования API после локального запуска:
```
 http://localhost:8001/swagger-ui/index.html#/
 http://localhost:8002/swagger-ui/index.html#/
```
## <strong>Важно!</strong> 
- порты 5432 и 5433 должны быть свободными для поднятия БД.<br>
- примеры использования логгера находятся в ветках ```demo/simple-logger``` и ```demo/any-logger```
