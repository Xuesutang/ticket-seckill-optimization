# ticket-seckill

城市演出票限量秒杀平台后端，采用 Spring Boot Maven 多模块单体架构，不使用 Spring Cloud 或微服务组件。

## 项目能力

- Spring Security + JWT 登录鉴权
- Redis Lua 脚本原子校验一次性令牌、库存和一人一单
- RocketMQ 异步创建订单，削峰并降低 MySQL 瞬时写入压力
- MyBatis-Plus 持久化订单、库存和消息消费记录
- 消费幂等与订单唯一约束，避免重复消息创建重复订单
- Docker 部署 MySQL、Redis、RocketMQ 等中间件

## 模块说明

| 模块 | 职责 |
| --- | --- |
| `common` | 统一响应对象、业务异常与公共代码 |
| `domain` | 秒杀请求、RocketMQ 消息、响应模型 |
| `infrastructure` | Redis Key、MyBatis Mapper、基础设施访问 |
| `application` | 秒杀编排、Redis Lua 调用、RocketMQ 消费者 |
| `web` | Spring Boot 启动模块、REST API、JWT 安全配置 |

## 秒杀链路

1. 管理员预热活动库存，将 MySQL 中票档库存写入 Redis。
2. 已登录用户获取五分钟有效的一次性秒杀令牌。
3. Lua 脚本原子完成令牌校验、库存校验、一人一单校验和 Redis 库存预扣。
4. 成功请求投递到 `seckill-order-create` Topic，接口立刻返回 `QUEUED`。
5. RocketMQ 消费者在 MySQL 事务中扣减最终库存、创建订单并记录已消费消息。
6. 订单创建完成后，查询结果由 `QUEUED` 更新为 `UNPAID`。

## 本地运行方式

Spring Boot 在 Windows 本地通过 IDEA 启动；MySQL、Redis、RocketMQ 在虚拟机 Docker 中运行。

在 IDEA 的 `SeckillApplication` 启动配置中设置环境变量：

```text
MYSQL_URL=jdbc:mysql://虚拟机IP:MySQL端口/seckill?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
MYSQL_USER=你的数据库用户名
MYSQL_PASSWORD=你的数据库密码
REDIS_HOST=虚拟机IP
REDIS_PORT=Redis端口
REDIS_PASSWORD=你的Redis密码
ROCKETMQ_NAMESRV=虚拟机IP:NameServer端口
JWT_SECRET=至少32位的随机字符串
SERVER_PORT=8081
```

然后运行：

```text
web/src/main/java/com/seckill/web/SeckillApplication.java
```

## Docker 完整部署

完整 Docker 部署时，先从模板创建仅本地使用的环境变量文件：

```bash
cp .env.example .env
```

编辑 `.env`，将所有占位符替换为本地密码和 JWT 密钥后启动：

```bash
docker compose up -d --build
docker compose ps
```

## 安全约定

- `.env` 存放真实密码和密钥，已被 Git 忽略，禁止提交。
- `.env.example` 只包含变量名和占位符，应提交到仓库。
- `.idea`、Maven `target` 目录、MySQL/Redis 数据目录均不会提交。
- 生产环境应使用强密码、独立数据库账户、HTTPS 与定期轮换的 JWT 密钥。
