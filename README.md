# 🌦️ Weather Sensor Monitoring System (Дополняется)

REST API для мониторинга метеорологических датчиков с аутентификацией и авторизацией. Система позволяет регистрировать датчики погоды, собирать измерения температуры и осадков, а также анализировать статистику.

## 📋 Содержание

- [Функциональность](#-функциональность)
- [Технологии](#-технологии)
- [Архитектура](#-архитектура)
- [Быстрый старт](#-быстрый-старт)
- [API Документация](#-api-документация)
- [Аутентификация](#-аутентификация)
- [Примеры использования](#-примеры-использования)
- [Структура проекта](#-структура-проекта)
- [Что я изучил](#-что-я-изучил)
- [Планы развития](#-планы-развития)

---

## ✨ Функциональность

### Основные возможности
- 🔐 **Аутентификация и авторизация** через Spring Security (HTTP Basic Auth)
- 📊 **CRUD операции** для датчиков и измерений
- 🔍 **Пагинация и сортировка** больших объемов данных
- 📈 **Статистика** по дождливым дням
- ✅ **Валидация входных данных** через Jakarta Validation
- 🛡️ **Глобальная обработка исключений** с понятными сообщениями об ошибках
- 📚 **Swagger UI документация** для интерактивного тестирования API
- 🚀 **Оптимизированные запросы** к базе данных (решена проблема N+1)

### Роли пользователей
- **ADMIN** - полный доступ (регистрация датчиков + добавление измерений)
- **USER** - добавление измерений для существующих датчиков
- **Публичный доступ** - чтение данных о датчиках и измерениях

---

## 🛠 Технологии

### Backend
- **Java 17** - язык программирования
- **Spring Boot 3.2** - фреймворк для разработки
- **Spring Security** - аутентификация и авторизация
- **Spring Data JPA** - работа с базой данных
- **Hibernate** - ORM для маппинга объектов

### База данных
- **PostgreSQL 14+** - реляционная СУБД

### Документация
- **Springdoc OpenAPI 3** - автоматическая генерация API документации
- **Swagger UI** - интерактивная документация

### Инструменты
- **Lombok** - уменьшение boilerplate кода
- **Maven** - управление зависимостями
- **JUnit 5 & Mockito** - тестирование
- **SLF4J & Logback** - логирование

---

## 🏗 Архитектура

Проект следует **многоуровневой архитектуре** с четким разделением ответственности:
```
┌─────────────────────────────────────────────┐
│          Presentation Layer                 │
│  Controllers (REST API Endpoints)           │
│  + Swagger Annotations                      │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│          Business Logic Layer               │
│  Services (Business Rules)                  │
│  + Transaction Management                   │
│  + Validation                               │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│          Data Access Layer                  │
│  Repositories (Spring Data JPA)             │
│  + Query Optimization                       │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│          Database Layer                     │
│  PostgreSQL (Entities & Relations)          │
└─────────────────────────────────────────────┘

          Cross-Cutting Concerns:
      ┌───────────────────────────┐
      │ Security (Authentication) │
      │ Exception Handling        │
      │ DTO Mapping              │
      │ Logging                  │
      └───────────────────────────┘
```

### Основные компоненты

#### 1. **Models (Entity)**
- `Sensor` - датчик погоды
- `Measurement` - измерение (температура, осадки)
- `MyUser` - пользователь системы

#### 2. **DTOs (Data Transfer Objects)**
- `SensorDTO` - данные датчика для передачи клиенту
- `MeasurementDTO` - данные измерения для передачи клиенту

#### 3. **Controllers**
- `SensorController` - управление датчиками
- `MeasurementController` - управление измерениями
- `AuthController` - информация о текущем пользователе

#### 4. **Services**
- `SensorService` - бизнес-логика датчиков
- `MeasurementService` - бизнес-логика измерений
- `MyUserDetailsService` - загрузка пользователей для аутентификации

#### 5. **Repositories**
- `SensorRepository` - доступ к данным датчиков
- `MeasurementRepository` - доступ к данным измерений
- `UserRepository` - доступ к данным пользователей

---

## 🚀 Быстрый старт

### Требования
- **JDK 17** или выше
- **Maven 3.8+**
- **PostgreSQL 14+**

### 1. Клонирование репозитория
```bash
git clone https://github.com/yourusername/weather-sensor-api.git
cd weather-sensor-api
```

### 2. Настройка базы данных

Создайте базу данных в PostgreSQL:
```sql
CREATE DATABASE PetProject3;
```

### 3. Конфигурация приложения

Отредактируйте `src/main/resources/application.properties`:
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/PetProject3
spring.datasource.username=postgres
spring.datasource.password=your_password

# Server
server.port=8081
```

### 4. Запуск приложения
```bash
# Сборка проекта
mvn clean install

# Запуск
mvn spring-boot:run
```

Приложение будет доступно по адресу: `http://localhost:8081`

### 5. Проверка работы

- **Swagger UI:** http://localhost:8081/swagger-ui/index.html
- **API Docs:** http://localhost:8081/v3/api-docs

---

## 📚 API Документация

### Интерактивная документация

Откройте Swagger UI для интерактивного тестирования:
```
http://localhost:8081/swagger-ui/index.html
```

![Swagger UI Screenshot](docs/swagger-screenshot.png)

### Основные endpoints

#### Датчики (Sensors)

| Метод | Endpoint | Описание | Доступ |
|-------|----------|----------|--------|
| GET | `/api/sensors` | Список всех датчиков (с пагинацией) | Публичный |
| GET | `/api/sensors/{name}` | Получить датчик по имени | Публичный |
| POST | `/api/sensors/register` | Зарегистрировать новый датчик | **ADMIN** |

#### Измерения (Measurements)

| Метод | Endpoint | Описание | Доступ |
|-------|----------|----------|--------|
| GET | `/api/measurements` | Список всех измерений (с пагинацией) | Публичный |
| GET | `/api/measurements/{id}` | Получить измерение по ID | Публичный |
| GET | `/api/measurements/raining` | Получить дождливые измерения | Публичный |
| GET | `/api/measurements/raining/count` | Количество дождливых дней | Публичный |
| POST | `/api/measurements/add` | Добавить новое измерение | **USER/ADMIN** |

#### Аутентификация (Auth)

| Метод | Endpoint | Описание | Доступ |
|-------|----------|----------|--------|
| GET | `/api/auth/me` | Информация о текущем пользователе | Авторизованный |

---

## 🔐 Аутентификация

API использует **HTTP Basic Authentication** с ролевой моделью доступа.

### Тестовые пользователи

При первом запуске приложение автоматически создает пользователей:

| Username | Password | Роль | Права доступа |
|----------|----------|------|---------------|
| `admin` | `admin123` | ADMIN | Полный доступ (регистрация датчиков + добавление измерений) |
| `user` | `user123` | USER | Может добавлять измерения |
| `superadmin` | `super123` | USER, ADMIN | Все права |

### Использование аутентификации

#### cURL
```bash
# Пример с admin
curl -u admin:admin123 -X POST http://localhost:8081/api/sensors/register \
  -H "Content-Type: application/json" \
  -d '{"name": "Sensor_Home"}'
```

#### Postman
1. Перейдите в раздел **Authorization**
2. Выберите тип **Basic Auth**
3. Введите username и password
4. Отправьте запрос

#### Swagger UI
1. Нажмите кнопку **"Authorize"** 🔒
2. Введите username и password
3. Нажмите **"Authorize"**
4. Теперь можете тестировать защищенные endpoints

---

## 💡 Примеры использования

### 1. Регистрация нового датчика (ADMIN)
```bash
curl -u admin:admin123 -X POST http://localhost:8081/api/sensors/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sensor_Living_Room"
  }'
```

**Ответ:**
```
Sensor registered successfully
```

---

### 2. Добавление измерения (USER/ADMIN)
```bash
curl -u user:user123 -X POST \
  "http://localhost:8081/api/measurements/add?sensorName=Sensor_Living_Room" \
  -H "Content-Type: application/json" \
  -d '{
    "value": 23.5,
    "isRaining": false
  }'
```

**Ответ:**
```
Measurement has been added successfully
```

---

### 3. Получение всех датчиков (публичный доступ)
```bash
curl http://localhost:8081/api/sensors?page=0&size=10
```

**Ответ:**
```json
{
  "content": [
    {
      "name": "Sensor_Living_Room",
      "measurements": []
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

---

### 4. Получение измерений датчика
```bash
curl http://localhost:8081/api/sensors/Sensor_Living_Room
```

**Ответ:**
```json
{
  "name": "Sensor_Living_Room",
  "measurements": [
    {
      "value": 23.5,
      "isRaining": false,
      "measurementDateTime": "2024-10-17T14:30:00"
    }
  ]
}
```

---

### 5. Статистика по дождливым дням
```bash
curl http://localhost:8081/api/measurements/raining/count
```

**Ответ:**
```json
{
  "count": 15
}
```

---

### 6. Получение только дождливых измерений
```bash
curl http://localhost:8081/api/measurements/raining?page=0&size=20
```

---

### 7. Информация о текущем пользователе
```bash
curl -u admin:admin123 http://localhost:8081/api/auth/me
```

**Ответ:**
```json
{
  "username": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

---

## 📂 Структура проекта
```
weather-sensor-api/
│
├── src/
│   ├── main/
│   │   ├── java/ru/xing/springcourse/petproject3rest/
│   │   │   ├── config/                    # Конфигурация
│   │   │   │   ├── DataInitializer.java   # Инициализация данных
│   │   │   │   ├── SecurityConfig.java    # Security настройки
│   │   │   │   ├── SwaggerConfig.java     # Swagger настройки
│   │   │   │   └── MyUserDetails.java     # UserDetails реализация
│   │   │   │
│   │   │   ├── controllers/               # REST контроллеры
│   │   │   │   ├── SensorController.java
│   │   │   │   ├── MeasurementController.java
│   │   │   │   └── AuthController.java
│   │   │   │
│   │   │   ├── dto/                       # Data Transfer Objects
│   │   │   │   ├── SensorDTO.java
│   │   │   │   └── MeasurementDTO.java
│   │   │   │
│   │   │   ├── models/                    # JPA Entities
│   │   │   │   ├── Sensor.java
│   │   │   │   ├── Measurement.java
│   │   │   │   └── MyUser.java
│   │   │   │
│   │   │   ├── repositories/              # Spring Data JPA
│   │   │   │   ├── SensorRepository.java
│   │   │   │   ├── MeasurementRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   │
│   │   │   ├── services/                  # Бизнес-логика
│   │   │   │   ├── SensorService.java
│   │   │   │   ├── MeasurementService.java
│   │   │   │   └── MyUserDetailsService.java
│   │   │   │
│   │   │   └── util/                      # Утилиты
│   │   │       ├── BusinessException.java
│   │   │       ├── MeasurementException.java
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── ErrorUtil.java
│   │   │       ├── MeasurementMapper.java
│   │   │       └── SensorMapper.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties     # Конфигурация приложения
│   │       └── schema.sql                 # SQL скрипты (опционально)
│   │
│   └── test/
│       └── java/
│           └── controllers/
│               └── MeasurementControllerTest.java
│
├── pom.xml                                # Maven конфигурация
├── README.md                              # Документация
└── .gitignore
```

---

## 📖 Что я изучил

### Backend разработка
✅ **Spring Boot 3** - создание REST API приложений  
✅ **Spring Security** - аутентификация и авторизация  
✅ **Spring Data JPA** - работа с базами данных  
✅ **Hibernate** - ORM маппинг и оптимизация запросов  

### Архитектурные паттерны
✅ **Layered Architecture** - разделение на слои (Controller-Service-Repository)  
✅ **DTO Pattern** - безопасная передача данных между слоями  
✅ **Mapper Pattern** - конвертация Entity ↔ DTO  
✅ **Repository Pattern** - абстракция доступа к данным  

### Безопасность
✅ **HTTP Basic Authentication** - базовая аутентификация  
✅ **BCrypt password encoding** - безопасное хранение паролей  
✅ **Role-based access control (RBAC)** - управление доступом на основе ролей  
✅ **CSRF protection** - защита от межсайтовой подделки запросов  

### Оптимизация и производительность
✅ **Pagination** - обработка больших объемов данных  
✅ **Query optimization** - решение проблемы N+1 запросов  
✅ **Lazy loading** - ленивая загрузка связанных сущностей  
✅ **Transaction management** - управление транзакциями  

### Валидация и обработка ошибок
✅ **Jakarta Validation** - валидация входных данных  
✅ **Global Exception Handling** - централизованная обработка исключений  
✅ **Custom exceptions** - создание кастомных исключений  

### Документация
✅ **Swagger/OpenAPI 3** - автоматическая генерация документации  
✅ **Swagger UI** - интерактивное тестирование API  

### Тестирование
✅ **Unit testing** - тестирование отдельных компонентов  
✅ **Mockito** - мокирование зависимостей  
✅ **MockMvc** - тестирование REST контроллеров  

### DevOps практики
✅ **Maven** - управление зависимостями и сборка проекта  
✅ **Logging** - логирование с SLF4J/Logback  
✅ **Configuration management** - вынос настроек в properties  

---

## 🔮 Планы развития

### В разработке
- [ ] **Docker containerization** - упаковка в Docker контейнеры
- [ ] **docker-compose** - оркестрация контейнеров
- [ ] **Integration tests** - интеграционное тестирование
- [ ] **GitHub Actions CI/CD** - автоматизация сборки и тестирования

### Планируется
- [ ] **JWT authentication** - token-based аутентификация
- [ ] **Redis caching** - кэширование часто запрашиваемых данных
- [ ] **WebSocket support** - real-time обновления данных
- [ ] **Actuator metrics** - мониторинг и метрики приложения
- [ ] **Rate limiting** - ограничение частоты запросов
- [ ] **Kubernetes deployment** - развертывание в Kubernetes
- [ ] **Grafana dashboard** - визуализация метрик

---

## 📊 Статистика проекта

- **Строк кода:** ~1060 (без учета комментариев и пустых строк)
- **Классов:** 25+
- **REST endpoints:** 10
- **Unit tests:** 1 (в процессе расширения)
- **Покрытие тестами:** ~10% (планируется увеличить до 70%)

---

