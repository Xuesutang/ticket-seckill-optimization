# ticket-seckill

Spring Boot Maven multi-module monolith for a high-concurrency ticket flash-sale scenario. It uses Redis Lua for atomic stock reservation and RocketMQ for asynchronous order creation. No Spring Cloud or microservice components are used.

## Modules

- `common`: API result and shared exceptions
- `domain`: request, message, and result models
- `infrastructure`: Redis keys and MyBatis mappers
- `application`: flash-sale service and RocketMQ consumer
- `web`: application entry point, REST API, and JWT security

## Local Development

The Spring Boot application runs locally. MySQL, Redis, and RocketMQ can run in Docker on a development VM.

Configure these environment variables in the IDE run configuration before starting `SeckillApplication`:

```text
MYSQL_URL
MYSQL_USER
MYSQL_PASSWORD
REDIS_HOST
REDIS_PORT
REDIS_PASSWORD
ROCKETMQ_NAMESRV
JWT_SECRET
```

Do not put real values in this repository.

## Docker

Copy the template and replace all placeholders with local secrets:

```bash
cp .env.example .env
docker compose up -d --build
```

`.env`, build output, IDE metadata, and container data directories are excluded from Git.
