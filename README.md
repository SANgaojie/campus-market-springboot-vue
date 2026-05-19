# Campus Market SpringBoot Vue

面向应届 Java 开发岗位的校园二手交易平台重构项目，基于 Spring Boot 3 + Vue 3，包含用户端、管理后台、JWT 鉴权、商品发布、收藏评论、订单流转、图片上传和演示数据初始化。

## 目标

基于原始校园二手交易项目的业务思路，重建一套更适合简历和面试讲解的现代技术栈项目。

## 技术栈

### 后端

- Java 17
- Spring Boot 3.5.x
- Spring Security 6 + JWT
- MyBatis Plus 3.5.x
- Flyway
- MySQL 8.4 LTS
- Redis 7.4 LTS
- springdoc-openapi
- Maven

### 前端

- Vue 3
- Vite
- TypeScript
- Vue Router
- Pinia（用户端）
- Axios

## 目录结构

```text
campus-market-springboot-vue/
├── apps/
│   ├── backend/      # Spring Boot 3 后端
│   ├── web/          # Vue 3 用户端
│   └── admin/        # Vue 3 管理后台
├── database/         # 原始 SQL 备份，运行时迁移以 Flyway 为准
├── docs/             # 项目文档和升级记录
├── infra/            # Docker/部署配置
├── scripts/          # 本地开发脚本
└── README.md
```

详细说明见：[`docs/project-structure.md`](docs/project-structure.md)

## 当前状态

已完成：

- 移除原始 `.git` 信息，后续可关联新仓库。
- 清理旧项目中源码不完整、与主线弱相关的内容。
- 新建 Spring Boot 3.5 + Java 17 后端。
- 接入 Spring Security 6 + JWT 登录鉴权。
- 接入 Flyway 自动建表和初始化分类。
- 完成用户端 Vue 3 基础业务闭环：商品列表、详情、登录注册、发布、我的商品、订单。
- 完成管理后台 Vue 3 登录和管理员接口接入。
- 后端测试、用户端构建、管理后台构建通过。

## 本地编译

当前环境使用 workspace 内本地 JDK/Maven 工具链：

```bash
cd apps/backend
export JAVA_HOME=/home/ubuntu/.openclaw/workspace/.tools/jdk-17
export PATH=/home/ubuntu/.openclaw/workspace/.tools/jdk-17/bin:/home/ubuntu/.openclaw/workspace/.tools/apache-maven/bin:$PATH
mvn -q -DskipTests compile
```

## 后续核心模块

- 用户认证与授权
- 商品发布与上下架
- 商品分类
- 图片上传
- 订单创建与状态流转
- 商品收藏
- 商品评论
- 管理后台接口


## 本地数据库

开发库使用 MySQL：

- database: `campus_market`
- app user: `campus_app`@`localhost`
- Flyway migrations: `apps/backend/src/main/resources/db/migration`

后端配置位于：`apps/backend/src/main/resources/application.yml`。

## 当前接口

- `GET /api/health`
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `PATCH /api/auth/me`
- `PATCH /api/auth/password`
- `POST /api/images/goods`
- `GET /api/categories`
- `GET /api/goods`
- `GET /api/goods/{goodsId}`
- `DELETE /api/goods/comments/{commentId}`
- `POST /api/goods/{goodsId}/comments`
- `GET /api/goods/{goodsId}/comments`
- `POST /api/goods`
- `GET /api/goods/mine`
- `GET /api/goods/mine/{goodsId}`
- `DELETE /api/goods/{goodsId}/favorite`
- `POST /api/goods/{goodsId}/favorite`
- `GET /api/goods/favorites`
- `PUT /api/goods/{goodsId}`
- `PATCH /api/goods/{goodsId}/off-shelf`
- `PATCH /api/goods/{goodsId}/relist`

- `POST /api/orders`
- `GET /api/orders/bought`
- `GET /api/orders/sold`
- `GET /api/orders/{orderId}`
- `PATCH /api/orders/{orderId}/cancel`
- `PATCH /api/orders/{orderId}/pay`
- `PATCH /api/orders/{orderId}/complete`
- `PATCH /api/orders/{orderId}/refund`


## 前端用户端

用户端位于：`apps/web`

技术栈：Vue 3 + Vite + TypeScript + Vue Router + Pinia + Axios。

常用命令：

```bash
cd apps/web
npm install
npm run dev
npm run build
```

开发服务器默认代理 `/api` 和 `/uploads` 到 `http://localhost:8080`。

发布商品页面支持本地图片上传，文件会通过 `POST /api/images/goods` 上传，后端默认保存到 `apps/backend/uploads` 并通过 `/uploads/**` 公开访问。编辑商品页面复用图片上传能力，并支持旧图回显和移除。前端与后端均限制最多 9 张商品图片。

当前页面：

- `/` 商品列表
- `/goods/:id` 商品详情，包含评论区
- `/login` 登录
- `/register` 注册
- `/publish` 发布商品
- `/goods/:id/edit` 编辑我的商品
- `/my-goods` 我的商品
- `/favorites` 我的收藏
- `/orders` 我的订单


## 本地联调

先确保 MySQL 已启动。数据库表和初始化分类由 Flyway 在后端启动时自动迁移。

首次创建本地数据库和应用用户：

```bash
./scripts/dev/init-db.sh
```

重置为适合演示/录屏的干净数据：

```bash
./scripts/dev/reset-demo-data.sh
```

演示账号密码均为 `admin123456`，常用账号：`admin`、`seller_lin`、`seller_chen`、`buyer_wang`、`buyer_zhao`。

一键启动前后端：

```bash
./scripts/dev/start-all.sh
```

分别启动：

```bash
./scripts/dev/start-backend.sh
./scripts/dev/start-web.sh
./scripts/dev/start-admin.sh
```

访问地址：

- 后端 API：`http://localhost:8080`
- Swagger UI：`http://localhost:8080/swagger-ui.html`
- 用户端前端：`http://localhost:5173`
- 管理后台：`http://localhost:5174`

前端开发服务器会代理 `/api` 和 `/uploads` 到 `http://localhost:8080`。

验证命令：

```bash
cd apps/backend && mvn -q test
cd apps/web && npm run build
cd apps/admin && npm run build
```


## 管理后台

管理后台位于：`apps/admin`

技术栈：Vue 3 + Vite + TypeScript + Vue Router + Axios。

常用命令：

```bash
cd apps/admin
npm install
npm run dev
npm run build
```

开发服务器默认端口：`http://localhost:5174`，并代理 `/api` 和 `/uploads` 到 `http://localhost:8080`。

当前页面：

- `/login` 管理员登录
- `/` 后台概览
- `/goods` 商品管理
- `/orders` 订单管理
- `/users` 用户管理
- `/categories` 分类管理
- `/comments` 评论管理

说明：管理后台已接入 `/api/admin/*`，需要使用 `ROLE_ADMIN` 账号登录。


## 默认开发管理员

后端启动时会确保本地开发管理员存在：

- username: `admin`
- password: `admin123456`
- role: `ROLE_ADMIN`

该账号仅用于本地开发。部署前必须改为环境变量、安全初始化脚本或首次启动向导。

管理员接口：

- `GET /api/admin/users`
- `PATCH /api/admin/users/{userId}/enable`
- `PATCH /api/admin/users/{userId}/disable`
- `GET /api/admin/goods`
- `PATCH /api/admin/goods/{goodsId}/off-shelf`
- `GET /api/admin/orders`
- `GET /api/admin/orders?status=PENDING_PAYMENT`
- `PATCH /api/admin/orders/{orderId}/cancel`
- `PATCH /api/admin/orders/{orderId}/refund`
- `GET /api/admin/categories`
- `POST /api/admin/categories`
- `PATCH /api/admin/categories/{categoryId}`
- `PATCH /api/admin/categories/{categoryId}/enable`
- `PATCH /api/admin/categories/{categoryId}/disable`
- `DELETE /api/admin/comments/{commentId}`
- `GET /api/admin/comments`

管理员登录接口复用：`POST /api/auth/login`，登录后依赖 `ROLE_ADMIN` 访问后台。

管理后台登录页：`http://localhost:5174/login`


## 环境变量

后端配置支持环境变量覆盖，常用项：

- `SPRING_PROFILES_ACTIVE`：默认 `dev`，生产可设为 `prod`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `APP_JWT_SECRET`
- `APP_ADMIN_USERNAME`
- `APP_ADMIN_PASSWORD`
- `APP_ADMIN_NICKNAME`
- `APP_CORS_ALLOWED_ORIGINS`
- `APP_UPLOAD_DIR`：默认 `./uploads`
- `APP_UPLOAD_PUBLIC_PATH`：默认 `/uploads`
- `APP_UPLOAD_MAX_FILE_SIZE`：默认 `5MB`
- `APP_UPLOAD_MAX_REQUEST_SIZE`：默认 `20MB`

生产环境不要使用默认 JWT secret 和默认管理员密码。


## 订单流转

当前交易闭环：

1. 买家下单：`PENDING_PAYMENT`，商品锁定为 `LOCKED`
2. 买家确认支付：`PAID`
3. 卖家确认完成：`COMPLETED`，商品变为 `SOLD`
4. 待支付订单可取消：`CANCELED`，商品重新上架为 `ON_SALE`

规则：

- 买家不能购买自己发布的商品
- 买家只能确认自己的订单支付
- 卖家只能完成自己的卖出订单
- 已支付订单不能取消


## 后台订单管理

管理员后台订单页支持：

- 查看全量订单
- 按订单状态筛选
- 强制取消 `PENDING_PAYMENT` / `PAID` 异常订单

强制取消规则：

- 仅管理员可操作
- 待支付或已支付订单可强制取消
- 强制取消后订单变为 `CANCELED`
- 如商品仍为 `LOCKED`，会恢复为 `ON_SALE`
- 已完成、已取消、退款中、已退款订单不可强制取消


## 后台分类管理

管理员后台分类页支持：

- 查看全部分类，包括禁用分类
- 新增分类
- 编辑分类名称和排序值
- 启用/禁用分类

规则：

- 公开分类接口 `/api/categories` 只返回启用分类
- 后台分类接口 `/api/admin/categories` 返回全部分类
- 分类名称唯一
- 禁用分类后，用户不能在该分类下发布新商品


## 个人中心

用户端新增 `/profile` 个人中心：

- 查看用户名、账号状态、角色
- 修改昵称
- 修改密码

规则：

- 修改资料和密码都需要登录
- 昵称最长 50 字
- 修改密码需要校验原密码
- 新密码长度 6-72 位
- 修改昵称后会同步刷新本地登录用户信息


## 后台仪表盘

管理后台首页通过 `GET /api/admin/dashboard` 展示真实统计：

- 用户总数、正常用户数、禁用用户数
- 分类总数、启用分类数
- 商品总数、在售商品数、锁定商品数、已售商品数
- 订单总数、待支付订单数、已支付订单数、已完成订单数
- 评论总数、可见评论数、已删除评论数

首页还会展示待关注事项：待支付订单、已支付待完成订单、已删除评论。


## 退款/售后流转

订单支持退款状态流转：

1. 买家确认支付后：`PAID`
2. 买家申请退款：`REFUNDING`
3. 管理员确认退款：`REFUNDED`
4. 如果商品仍处于 `LOCKED`，退款完成后恢复为 `ON_SALE`

规则：

- 只有买家可以申请退款
- 只有 `PAID` 订单可以申请退款
- 退款中订单不能由卖家确认完成
- 只有管理员可以确认退款
- 只有 `REFUNDING` 订单可以确认退款
- 后台订单管理页会对退款中订单显示“确认退款”操作
