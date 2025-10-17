

##  Опис
Social Feed API — це backend-застосунок, який моделює роботу соціальної мережі з високим навантаженням.  
Він створений для експериментів із продуктивністю, масштабованістю та поведінкою системи при великій кількості запитів.  
Проєкт використовується у навчальних лабораторних роботах з дисципліни «Високонавантажені системи».

---

## ️ Основні можливості


-  **Стрічка новин** (`/api/feed`) — отримання останніх 20 постів із коментарями та лайками.
-  **Популярні пости** (`/api/trending`) — топ-10 постів за останні 24 години.
-  **Робота з постами** — створення, коментування, лайки.
- ️ **Моніторинг** — health-check, метрики, логування запитів.
-  **Chaos Mode** — випадкові помилки для тестування стійкості.

---

##  Архітектура проєкту

```
# /api/seed – створення тестових даних
# /api/feed – стрічка постів
# /api/trending – топ 10 постів
# CRUD для постів, лайків, коментарів
# інформація про користувачів
# /health – статус сервера
# /api/metrics – метрики
# /api/chaos – хаос режим


# Моделі бази даних (JPA)
User.java
Post.java
Comment.java
Like.java
LikeId.java


# Spring Data JPA репозиторії
UserRepository.java
PostRepository.java
CommentRepository.java
LikeRepository.java


DataSeederService.java  # Генерація тестових даних

RequestResponseLoggingFilter.java # Логування HTTP запитів


LatencySimulator.java   # Імітація затримок бази даних

SocialFeedApiApplication.java # Точка входу
```

---

##  Технологічний стек

| Компонент | Технологія |
|------------|-------------|
| Мова | **Java 17** |
| Фреймворк | **Spring Boot 3** |
| ORM | **Hibernate / JPA** |
| База даних | **PostgreSQL** |
| Контейнеризація | **Docker** |
| Логування | **SLF4J + Custom Request Filter** |
| Моніторинг | **Health Check, Metrics API** |
| Chaos Testing | **Випадкові помилки (10%)** |

---

## Запуск через Docker


### Запуск через Docker Compose
```bash
docker-compose up --build
```

### Після запуску:
- API → http://localhost:8080
- PostgreSQL → localhost:5432  
  **user:** `postgres`  
  **password:** `postgres`  
  **database:** `socialfeed`

---

##  Схема бази даних

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    followers_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    content TEXT NOT NULL,
    likes_count INTEGER DEFAULT 0,
    comments_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comments (
    id SERIAL PRIMARY KEY,
    post_id INTEGER REFERENCES posts(id),
    user_id INTEGER REFERENCES users(id),
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE likes (
    user_id INTEGER REFERENCES users(id),
    post_id INTEGER REFERENCES posts(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, post_id)
);
```

---

##  Основні ендпоінти

| Метод | Endpoint | Опис |
|--------|-----------|------|
| `POST` | `/api/seed` | Генерація тестових даних |
| `GET` | `/api/feed?userId=1` | Стрічка останніх 20 постів |
| `GET` | `/api/trending?userId=1` | Топ-10 найпопулярніших постів |
| `GET` | `/api/users/{id}/posts` | Пости користувача |
| `POST` | `/api/posts` | Створити новий пост |
| `POST` | `/api/posts/{id}/like` | Лайк / анлайк |
| `POST` | `/api/posts/{id}/comment` | Додати коментар |
| `GET` | `/api/metrics` | Метрики продуктивності |
| `GET` | `/health` | Перевірка стану сервера |
| `GET` | `/api/chaos/enable` | Включити хаос (10% збоїв) |

---

##  Приклади запитів

###  Генерація тестових даних
```bash
POST http://localhost:8080/api/seed
```
**Response:**
```
Генерація тестових даних запущена у фоновому режимі!
```

---

###  Отримання стрічки новин
```bash
GET http://localhost:8080/api/feed?userId=1
```

**Response (скорочено):**
```json
[
  {
    "id": 101,
    "user": { "id": 2, "username": "user2" },
    "content": "Пост #101",
    "likesCount": 45,
    "commentsCount": 12,
    "createdAt": "2025-10-16T10:22:00",
    "likedByUser": true,
    "comments": [
      { "id": 22, "content": "Коментар #22", "user": { "id": 5, "username": "user5" } }
    ]
  }
]
```

---

### Трендові пости
```bash
GET http://localhost:8080/api/trending?userId=1
```

**Response:**
```json
[
  {
    "id": 5001,
    "content": "Пост #5001",
    "likesCount": 1020,
    "commentsCount": 33,
    "likedByUser": false
  }
]
```

---

## Метрики

Приклад відповіді `/api/metrics`:
```json
{
  "total_requests": 10234,
  "requests_per_second": 42.5,
  "average_response_time": 78.2,
  "error_rate": 0.03,
  "active_connections": 12
}
```

---

## 🧩 Chaos Testing

**Вмикання хаосу:**
```bash
GET http://localhost:8080/api/chaos/enable
```

Після цього 10% усіх запитів випадково завершуватимуться помилкою `500 Internal Server Error`.

---

## Автор

**Петров Артем**  
Студент [ПЗ-204]  


