# Modernization Audit

## 当前结论

本仓库已拉到本地：`/home/ubuntu/.openclaw/workspace/stuSecond-modern`

工作分支：`modernize-java17-springboot3-vue3`

## 重要风险

当前 GitHub 仓库源码不完整，不能直接作为可运行项目升级：

- 后端 Java 代码引用了 `com.mrxu.stucomplarear2.entity`，但仓库中没有 `entity` 包。
- 后端 Java 代码引用了 `com.mrxu.stucomplarear2.service`，但仓库中没有 `service` 包。
- 后端 Java 代码引用了 `com.mrxu.stucomplarear2.utils`，但仓库中没有 `utils` 包。
- 用户前端 `stucompla-front-feature-v0.0.1` 只有配置和 `public`，没有 `src`。
- 管理后台 `admin/stucompla-front-admin-feature-v0.0.1` 只有模板配置、mock、test、public，没有业务 `src`。
- 仓库中未发现 `.sql` 数据库初始化脚本。

因此：不能直接进行“大删大改”。第一阶段只做版本升级准备、目录清理建议和现代化迁移方案。

## 保留的核心业务模块

目标项目：校园二手交易平台。

建议保留：

- 用户模块：`UserController`
- 管理员模块：`AdminController`
- 商品模块：`GoodsController`
- 商品分类：`GoodsCategoryController`
- 图片上传：`ImageController`
- 订单模块：`MarketOrderController`
- 收藏模块：`CollectController`
- 评论模块：`CommentController`

## 建议删除/弱化的模块

这些模块和“Java 后端求职项目：校园二手交易平台”主线关系弱，后续可删除：

- 校园论坛：`PostController`、`Post*Dto`、`PostMapper*`
- 表白墙：`WallController`、`Wall*Dto`、`WallMapper*`
- 私信/通知：`LetterController`、`Letter*Dto`、`LetterMapper*`
- 普通论坛分类：`CategoryController`、`CategoryMapper*`，如果商品分类已由 `GoodsCategory` 承担，可删除。

删除顺序建议：

1. 先删除前端菜单和路由。
2. 再删除 Controller。
3. 再删除 DTO/Mapper/XML。
4. 最后删除数据库表和 SQL。

当前仓库源码不完整，所以暂不执行删除。

## 推荐目标技术版本

### 后端

- Java 17
- Spring Boot 3.5.14
- MyBatis Plus Spring Boot 3 Starter 3.5.16
- MySQL Connector/J：由 Spring Boot 管理
- Redis：Spring Boot Data Redis
- Spring Security 6：替代 Shiro
- springdoc-openapi 2.8.17：替代 Springfox Swagger
- java-jwt 4.5.2
- Lombok：由 Spring Boot 管理
- Jackson：替代 FastJSON 1.x

### 前端

- Vue 3.5.x
- Vite 8.x
- TypeScript 6.x
- Vue Router 5.x
- Pinia 3.x
- Element Plus 2.14.x
- Axios 1.16.x
- ECharts 6.x

## 现代 Java 优化方向

后续在业务代码完整后再逐步使用：

- Lambda 表达式
- Stream API
- Optional
- Java 17 `record` 用于请求/响应 DTO
- switch expression
- 局部变量 `var`，谨慎使用
- `List.of` / `Map.of`
- 构造器注入替代字段注入
- Spring Security 6 Lambda DSL
- `@RestControllerAdvice` 统一异常处理
- Bean Validation 参数校验

## 下一步

1. 先升级后端 `pom.xml` 版本与依赖。
2. 先不删除业务模块。
3. 安装/准备 Java 17 + Maven 后做编译验证。
4. 如果编译仍因源码缺失失败，需要换一个完整源码仓库，或基于当前业务接口重建 Spring Boot 3 后端。

## 本轮实际改动

### 已改版本文件

- `stucompla-rear2-feature-v0.0.1/pom.xml`
  - Spring Boot `2.5.7` -> `3.5.14`
  - Java `1.8` -> `17`
  - MyBatis Plus starter -> `mybatis-plus-spring-boot3-starter:3.5.16`
  - Shiro 依赖移除，改为 Spring Security starter
  - Springfox 依赖移除，改为 `springdoc-openapi-starter-webmvc-ui:2.8.17`
  - JWT `3.4.1` -> `4.5.2`
  - MySQL driver 坐标改为 `com.mysql:mysql-connector-j`
  - FastJSON 1.x 和 commons-lang 2.x 暂时移除

- `stucompla-front-feature-v0.0.1/package.json`
  - Vue 2 / Vue CLI / Element UI / Vuex -> Vue 3 / Vite / Element Plus / Pinia / TypeScript

- `admin/stucompla-front-admin-feature-v0.0.1/package.json`
  - Vue 2 admin template -> Vue 3 / Vite / Element Plus / Pinia / TypeScript

### 已准备本地验证工具链

系统没有全局 Java/Maven，且没有 sudo 安装权限。因此在 workspace 下准备了本地工具链：

- JDK: `/home/ubuntu/.openclaw/workspace/.tools/jdk-17`
- Maven: `/home/ubuntu/.openclaw/workspace/.tools/apache-maven`

当前工具链验证：

- Java `17.0.19`
- Maven `3.9.11`

### 验证结果

已通过：

- `pom.xml` XML 语法校验
- 两个 `package.json` JSON 语法校验
- Java 17 / Maven 3.9.11 可用性检查

未通过：

- `mvn -DskipTests compile`

失败原因不是单一依赖版本问题，而是当前仓库源码缺失 + Spring Boot 3 迁移未完成：

1. 缺失原始业务源码：`entity`、`service`、`utils` 包不存在。
2. 老代码仍使用 `javax.*`，Spring Boot 3 需要迁移到 `jakarta.*`。
3. 老代码仍使用 Shiro 注解，例如 `@RequiresPermissions`，目标技术栈已切到 Spring Security，需要逐步替换。
4. 老代码仍使用 Swagger 2 注解 `io.swagger.annotations.ApiOperation`，目标技术栈应换成 springdoc 的 `io.swagger.v3.oas.annotations.Operation`。
5. 老代码仍引用 FastJSON `JSONObject`，目标技术栈建议使用 Jackson 或必要时切到 FastJSON2。

## 下一轮建议

不要继续在这个残缺仓库上“补洞式升级”。更稳的路线是：

1. 以当前 Controller/DTO/Mapper XML 为业务参考。
2. 新建一个干净的 Spring Boot 3.5 + Java 17 后端模块。
3. 先实现最小核心闭环：用户、商品、订单、收藏、图片。
4. 不迁移论坛、表白墙、私信模块。
5. 每完成一个模块就编译/测试一次。

## 2026-05-18 cleanup before Plan A

按用户要求，已先清理原始仓库信息和无用内容，再开始方案 A。

### 已移出项目目录

可恢复位置见 `.cleanup-trash-path`。

- 原始 `.git`：已移到 trash，当前目录不再关联原 GitHub 仓库。
- 旧截图目录 `picture/`。
- 残缺用户前端 `stucompla-front-feature-v0.0.1/`。
- 残缺后台前端 `admin/`。
- 旧后端 `stucompla-rear2-feature-v0.0.1/`：整体移到 trash 作为可恢复参考，避免残缺代码干扰搜索。
- 原 `.gitattributes`：其内容会把 js/css/html/vue 识别成 Java，不适合新项目。

### 已开始方案 A

新建干净后端目录：`backend/`

当前包含：

- Spring Boot 3.5.14 + Java 17 Maven 项目骨架
- Spring Security 6 Lambda DSL 基础配置
- Java 17 `record` 风格统一响应 `ApiResponse`
- `/api/health` 健康检查接口
- `application.yml` 基础配置

### 验证

已使用本地工具链执行：

```bash
cd backend
mvn -q -DskipTests compile
```

结果：通过。

## 2026-05-18 structure cleanup

按用户要求，在继续业务开发前先优化目录结构。

### 新结构

- `apps/backend`：Spring Boot 3 后端服务。
- `apps/web`：Vue 3 用户端预留目录。
- `apps/admin`：Vue 3 管理后台预留目录。
- `database/migrations`：数据库建表/变更 SQL。
- `database/seed`：初始化/演示数据 SQL。
- `docs`：项目文档、升级审计和设计说明。
- `infra/docker`：Docker/部署配置预留目录。
- `scripts/dev`：本地开发脚本预留目录。

### 已移动

- `backend/` -> `apps/backend/`
- `MODERNIZATION_AUDIT.md` -> `docs/modernization-audit.md`

### 已新增

- `docs/project-structure.md`
- 前端、管理端、数据库、部署、脚本预留目录和 `.gitkeep`

### 验证

已在新路径执行：

```bash
cd apps/backend
mvn -q -DskipTests compile
```

结果：通过。

## 2026-05-18 user authentication slice

完成第一个后端纵切：数据库核心表 + 用户注册/登录/JWT 鉴权。

### 新增数据库脚本

- `database/migrations/V001__init_core_schema.sql`
  - `sys_user`
  - `user_role`
  - `goods_category`
  - `goods`
  - `goods_image`
  - `trade_order`
- `database/seed/V001__seed_core_data.sql`
  - 初始化商品分类

### 新增后端能力

- 用户实体与角色实体：`User`, `UserRole`
- MyBatis Plus Mapper：`UserMapper`, `UserRoleMapper`
- 请求/响应 DTO 使用 Java 17 `record`
- `UserService`：注册、查用户、查角色
- `AuthService`：注册后签发 Token、登录校验、当前用户查询
- `AuthController`
  - `POST /api/auth/register`
  - `POST /api/auth/login`
  - `GET /api/auth/me`
- `JwtTokenService`：基于 java-jwt 生成和验证 JWT
- `JwtAuthenticationFilter`：解析 Bearer Token 并写入 Spring Security 上下文
- `SecurityConfig`：Spring Security 6 Lambda DSL，放行健康检查、注册、登录、OpenAPI
- `GlobalExceptionHandler`：统一处理业务异常和参数校验异常
- `PasswordConfig`：BCrypt 密码加密

### 使用到的新技术写法

- Java 17 `record`：统一响应和 DTO
- Lambda 表达式 / Stream API：角色查询和 Security DSL
- `Optional`：参数校验错误消息处理
- Spring Security 6 Lambda DSL
- Jakarta Validation：`jakarta.validation.*`
- 构造器注入替代字段注入

### 验证

执行：

```bash
cd apps/backend
mvn -q test
```

结果：通过。

说明：第一次测试失败是因为注册测试使用合法请求体，触发真实 MySQL 连接；当前本地未启动 MySQL。已将该 smoke test 调整为非法参数请求，只验证注册接口公开可达，不依赖数据库。

## 2026-05-18 MySQL + goods catalog slice

本轮完成本地 MySQL 环境落地，并继续实现商品模块最小闭环。

### MySQL 环境

- 安装 MySQL Server 8.0
- 启动并设置开机自启 `mysql.service`
- 创建数据库：`campus_market`
- 创建本地开发用户：`campus_app`@`localhost`
- 执行脚本：
  - `database/migrations/V001__init_core_schema.sql`
  - `database/seed/V001__seed_core_data.sql`
- 验证结果：当前存在 6 张核心表，`goods_category` 初始化 5 条分类数据。

### 商品模块

新增包：`com.campus.market.goods`

已实现：

- `GET /api/categories`：公开获取启用商品分类
- `GET /api/goods`：公开获取在售商品列表，可选 `categoryId`
- `GET /api/goods/{goodsId}`：公开获取在售商品详情
- `POST /api/goods`：登录用户发布商品，需要 Bearer Token

### 验证

执行：

```bash
cd apps/backend
mvn -q test
```

结果：通过。

新增数据库集成测试：

- `AuthDatabaseTests`：注册、登录、查询当前用户
- `GoodsDatabaseTests`：分类查询、未登录禁止发布、登录后发布商品、商品详情查询

## 2026-05-18 goods management slice

本轮继续完善商品模块，补齐卖家对自己商品的管理能力。

### 新增后端能力

- 商品图片实体与 Mapper：`GoodsImage`, `GoodsImageMapper`
- 发布商品支持 `imageUrls`，当前保存 URL 列表，暂不做文件上传
- 商品响应 `GoodsResponse` 增加 `imageUrls`
- 商品编辑请求：`UpdateGoodsRequest`

### 新增/完善接口

- `GET /api/goods/mine`：查看我发布的商品，需要登录
- `GET /api/goods/mine/{goodsId}`：查看我的商品详情，需要登录
- `PUT /api/goods/{goodsId}`：编辑自己的商品，需要登录
- `PATCH /api/goods/{goodsId}/off-shelf`：下架自己的商品，需要登录
- `PATCH /api/goods/{goodsId}/relist`：重新上架自己的商品，需要登录

### 重要修复

- `BusinessException(404, ...)` 原先会被 `GlobalExceptionHandler` 固定返回 HTTP 400。
- 已改为按业务 code 映射 HTTP 状态，避免前端无法区分参数错误和资源不存在。

### 验证

执行：

```bash
cd apps/backend
mvn -q test
```

结果：通过。

## 2026-05-18 order transaction slice

本轮完成订单模块第一版，让“发布商品 → 买家下单 → 商品锁定/取消/完成”交易链路跑通。

### 新增后端模块

新增包：`com.campus.market.order`

- `TradeOrder`：订单实体，映射 `trade_order`
- `TradeOrderMapper`：MyBatis Plus Mapper
- `OrderStatus`：订单状态枚举
- `CreateOrderRequest`
- `OrderResponse`
- `OrderService`
- `OrderController`

### 新增接口

- `POST /api/orders`：买家创建订单，需要登录
- `GET /api/orders/bought`：买家查看我买到的订单，需要登录
- `GET /api/orders/sold`：卖家查看我卖出的订单，需要登录
- `GET /api/orders/{orderId}`：订单详情，仅买家/卖家可见
- `PATCH /api/orders/{orderId}/cancel`：取消待处理订单，并恢复商品为 `ON_SALE`
- `PATCH /api/orders/{orderId}/complete`：完成订单，并标记商品为 `SOLD`

### 业务规则

- 只能购买 `ON_SALE` 商品。
- 买家不能购买自己发布的商品。
- 创建订单后商品状态从 `ON_SALE` 变为 `LOCKED`，防止重复下单。
- 取消待处理订单后，商品恢复 `ON_SALE`。
- 完成订单后，商品变为 `SOLD`，公开详情不可见，也不能再次下单。
- 订单详情只有买家和卖家可以查看。

### 验证

执行：

```bash
cd apps/backend
mvn -q test
```

结果：通过。

新增测试：`OrderDatabaseTests`

覆盖：

- 买家创建订单
- 锁定商品后不可重复下单
- 取消订单后可重新下单
- 卖家不能购买自己的商品
- 买家/卖家订单列表
- 陌生人不可查看订单
- 完成订单后商品不可公开访问且不可再次下单

## 2026-05-18 web app skeleton

本轮开始用户端前端，完成 Vue 3 + Vite + TypeScript 最小可用骨架，并接入当前后端接口。

### 新增前端目录

- `apps/web/package.json`
- `apps/web/vite.config.ts`
- `apps/web/tsconfig.json`
- `apps/web/src/main.ts`
- `apps/web/src/App.vue`
- `apps/web/src/router/index.ts`
- `apps/web/src/stores/auth.ts`
- `apps/web/src/api/*`
- `apps/web/src/views/*`

### 技术栈

- Vue 3
- Vite
- TypeScript
- Vue Router
- Pinia
- Axios

### 已接入页面

- `/`：商品列表 + 分类筛选
- `/goods/:id`：商品详情 + 创建订单
- `/login`：登录
- `/register`：注册
- `/publish`：发布商品
- `/my-goods`：我的商品，下架/重新上架
- `/orders`：我的买入/卖出订单，取消/完成订单

### 验证

执行：

```bash
cd apps/web
npm install
npm run build
```

结果：通过。

构建产物：`apps/web/dist/`

### 备注

本阶段图片先使用 URL 列表，不做本地文件上传；后续可接入对象存储或后端上传接口。

## 2026-05-18 local dev workflow slice

本轮完善本地联调体验：补 CORS 兜底配置和开发启动脚本。

### 后端 CORS

新增：

- `CorsProperties`
- `SecurityConfig#corsConfigurationSource`

配置项：

```yaml
app:
  cors:
    allowed-origins:
      - http://localhost:5173
      - http://127.0.0.1:5173
```

说明：前端开发阶段主要通过 Vite proxy 访问 `/api`，CORS 配置用于直接跨端口访问时兜底。

### 开发脚本

新增：

- `scripts/dev/start-backend.sh`
- `scripts/dev/start-web.sh`
- `scripts/dev/start-all.sh`

用途：

- 后端启动到 `http://localhost:8080`
- 前端启动到 `http://localhost:5173`
- 前端代理 `/api` 到后端

### 验证

后端：

```bash
cd apps/backend
mvn -q test
```

前端：

```bash
cd apps/web
npm run build
```

结果：均通过。

## 2026-05-18 admin app skeleton

本轮创建管理后台前端骨架，先完成后台信息架构和可构建页面，后续再补管理员权限与管理接口。

### 新增目录

- `apps/admin/package.json`
- `apps/admin/vite.config.ts`
- `apps/admin/tsconfig.json`
- `apps/admin/src/main.ts`
- `apps/admin/src/App.vue`
- `apps/admin/src/router/index.ts`
- `apps/admin/src/api/*`
- `apps/admin/src/views/*`

### 技术栈

- Vue 3
- Vite
- TypeScript
- Vue Router
- Axios

### 页面

- `/`：后台概览，读取公开分类和公开商品形成简易统计
- `/goods`：商品管理占位版，当前展示公开商品
- `/orders`：订单管理占位，列出后续管理员接口设计
- `/users`：用户管理占位，列出后续管理员接口设计
- `/categories`：分类管理占位版，当前展示启用分类

### 验证

执行：

```bash
cd apps/admin
npm install
npm run build
```

结果：通过。

### 开发脚本补充

新增：

- `scripts/dev/start-admin.sh`

并更新 `scripts/dev/start-all.sh`：现在会同时启动后端、用户端、管理后台。

端口：

- 后端：8080
- 用户端：5173
- 管理后台：5174

## 2026-05-18 admin auth and API slice

本轮把管理后台从“页面骨架”推进到“可登录并调用真实管理员接口”。

### 后端新增

- `AdminBootstrapProperties`：读取开发环境管理员配置
- `AdminBootstrapRunner`：应用启动时确保管理员账号存在
- `AdminController`：管理员接口入口

### 默认开发管理员

配置位于 `apps/backend/src/main/resources/application.yml`：

```yaml
app:
  admin:
    username: admin
    password: admin123456
    nickname: 系统管理员
```

说明：该账号仅用于本地开发，部署前必须改为环境变量或安全初始化流程。

### 权限

- 新增 `ROLE_ADMIN`
- `/api/admin/**` 需要 `ROLE_ADMIN`
- 普通用户访问管理员接口返回 403

### 新增管理员接口

- `GET /api/admin/users`
- `PATCH /api/admin/users/{userId}/enable`
- `PATCH /api/admin/users/{userId}/disable`
- `GET /api/admin/goods`
- `PATCH /api/admin/goods/{goodsId}/off-shelf`
- `GET /api/admin/orders`
- `GET /api/admin/categories`

### 管理后台接入

- 新增 `/login` 管理员登录页
- 后台本地保存管理员 Token
- 路由守卫要求 `ROLE_ADMIN`
- 概览、用户、商品、订单、分类页面改为调用 `/api/admin/*`

### 验证

后端：

```bash
cd apps/backend
mvn -q test
```

后台：

```bash
cd apps/admin
npm run build
```

用户端回归：

```bash
cd apps/web
npm run build
```

结果：均通过。


## 2026-05-18 Flyway and environment configuration slice

本轮把数据库初始化从“手动执行 SQL”升级为“后端启动自动迁移”。

### 新增

- 后端依赖：`flyway-core`、`flyway-mysql`
- 迁移目录：`apps/backend/src/main/resources/db/migration`
- `V1__init_core_schema.sql`：核心表结构
- `V2__seed_core_data.sql`：基础分类初始化
- `scripts/dev/init-db.sh`：只负责创建本地数据库和应用用户，表结构交给 Flyway
- `application-dev.yml`：开发环境配置
- `application-prod.yml`：生产环境覆盖配置

### 配置调整

`application.yml` 改为环境变量优先：

- `SPRING_PROFILES_ACTIVE`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `REDIS_HOST`
- `REDIS_PORT`
- `APP_CORS_ALLOWED_ORIGINS`
- `APP_ADMIN_USERNAME`
- `APP_ADMIN_PASSWORD`
- `APP_ADMIN_NICKNAME`
- `APP_JWT_SECRET`
- `APP_JWT_EXPIRATION_MINUTES`

生产 profile 禁用 Swagger/OpenAPI，并要求通过环境变量提供管理员密码和 JWT secret。

### 兼容已有开发库

当前本地开发库之前已经手动建过表，因此初始迁移采用过渡期兼容策略：

- `CREATE TABLE IF NOT EXISTS`
- `INSERT IGNORE`
- `spring.flyway.baseline-on-migrate=true`
- `baseline-version=0`

这样已有库可以平滑纳入 Flyway，新空库也能自动完整建表。

### 验证

已执行：

```bash
cd apps/backend && mvn -q test
cd apps/admin && npm run build
cd apps/web && npm run build
```

结果：通过。

额外验证空库自动迁移：

- 临时创建 `campus_market_flyway_check`
- 使用 `DB_URL=jdbc:mysql://localhost:3306/campus_market_flyway_check?...` 启动 `CampusMarketApplicationTests`
- Flyway 自动应用 V1/V2
- 验证结果：7 张表，5 条基础分类
- 验证后删除临时库


## 2026-05-18 goods image upload slice

本轮把商品图片从“手动填写 URL”升级为“登录用户上传本地图片”。

### 后端新增

- `ImageController`：`POST /api/images/goods`
- `FileStorageService`：保存商品图片并返回公开 URL
- `FileStorageProperties`：读取 `app.storage.*` 配置
- `UploadResponse`：上传响应 DTO
- `WebMvcConfig`：把上传目录映射为静态资源 `/uploads/**`

### 配置

`application.yml` 新增：

- `spring.servlet.multipart.max-file-size`
- `spring.servlet.multipart.max-request-size`
- `app.storage.upload-dir`
- `app.storage.public-path`

默认：

- 上传目录：`./uploads`
- 公开路径：`/uploads`
- 单张图片限制：5MB
- 单次请求限制：20MB

### 安全策略

- 上传接口 `POST /api/images/goods` 需要登录
- 图片静态资源 `/uploads/**` 公开可读
- 支持类型：jpg、png、webp、gif

### 前端用户端

`PublishGoodsView.vue` 已从“图片 URL 文本框”改为文件选择上传：

- 支持多图上传
- 上传后展示缩略图预览
- 可移除已上传图片
- 发布商品时提交上传接口返回的 URL 列表

### 验证

新增测试：`ImageUploadTests`

覆盖：

- 未登录上传返回 403
- 非图片文件返回 400
- 登录用户上传图片成功
- 上传后的 `/uploads/**` URL 可公开读取

已执行：

```bash
cd apps/backend && mvn -q test
cd apps/web && npm run build
cd apps/admin && npm run build
```

结果：通过。


## 2026-05-18 user goods edit page slice

本轮补齐用户端“编辑我的商品”闭环。

### 前端新增/调整

- 新增 `EditGoodsView.vue`
- 新增路由：`/goods/:id/edit`
- `goods.ts` 新增 `fetchMyGoodsDetail(goodsId)`，调用 `GET /api/goods/mine/{goodsId}`
- `MyGoodsView.vue` 增加“编辑”入口，仅对 `ON_SALE` / `OFF_SHELF` 商品展示
- `main.css` 增加页面头部行样式

### 功能

编辑页会：

- 加载商品当前分类、标题、描述、价格、成色、图片
- 回显已有图片
- 支持追加上传图片
- 支持移除图片
- 调用 `PUT /api/goods/{goodsId}` 保存修改
- 保存后跳转商品详情页

### 复用约束

后端已有业务规则保持不变：

- 只能编辑自己的商品
- `SOLD` / `LOCKED` 商品不可编辑
- 分类必须存在且启用

### 验证

已执行：

```bash
cd apps/web && npm run build
cd apps/backend && mvn -q test
cd apps/admin && npm run build
```

结果：通过。


## 2026-05-18 goods favorite slice

本轮补齐用户收藏商品功能。

### 数据库

新增 Flyway 迁移：

- `V3__create_goods_favorite.sql`

新增表：`goods_favorite`

- `user_id`
- `goods_id`
- `created_at`
- 唯一约束：`(user_id, goods_id)`
- 外键关联：`sys_user`、`goods`

### 后端

新增：

- `GoodsFavorite`
- `GoodsFavoriteMapper`
- `GoodsFavoriteTests`

新增接口：

- `GET /api/goods/favorites`
- `POST /api/goods/{goodsId}/favorite`
- `DELETE /api/goods/{goodsId}/favorite`

规则：

- 收藏、取消收藏、我的收藏都需要登录
- 不能收藏自己的商品
- 只能收藏 `ON_SALE` 商品
- 重复收藏幂等返回成功
- 我的收藏只返回仍在售的商品

### 前端用户端

新增/调整：

- `FavoriteGoodsView.vue`
- 路由：`/favorites`
- 顶部导航新增“我的收藏”
- 商品详情页新增“收藏商品 / 取消收藏”按钮
- `goods.ts` 新增收藏相关 API 方法

### 验证

已执行：

```bash
cd apps/web && npm run build
cd apps/backend && mvn -q test
cd apps/admin && npm run build
```

结果：通过。


## 2026-05-18 goods comment slice

本轮补齐商品评论功能。

### 数据库

新增 Flyway 迁移：

- `V4__create_goods_comment.sql`

新增表：`goods_comment`

- `goods_id`
- `user_id`
- `content`
- `deleted`
- `created_at`
- `updated_at`

采用软删除，便于保留审计和后续管理后台扩展。

### 后端

新增：

- `GoodsComment`
- `GoodsCommentMapper`
- `CreateCommentRequest`
- `CommentResponse`
- `GoodsCommentTests`

新增接口：

- `GET /api/goods/{goodsId}/comments`
- `POST /api/goods/{goodsId}/comments`
- `DELETE /api/goods/comments/{commentId}`

规则：

- 评论列表公开读取，但只针对在售商品
- 发表评论需要登录
- 评论内容会 trim，最大 500 字
- 空评论拒绝
- 评论作者或商品卖家可删除评论
- 删除为软删除

### 前端用户端

新增/调整：

- `api/comments.ts`
- `GoodsComment` 类型
- 商品详情页增加评论区

评论区支持：

- 查看评论列表
- 登录后发表评论
- 评论作者/商品卖家删除评论
- 未登录评论时跳转登录并保留 redirect

### 验证

已执行：

```bash
cd apps/web && npm run build
cd apps/backend && mvn -q test
cd apps/admin && npm run build
```

结果：通过。


## 2026-05-18 admin comment management slice

本轮把商品评论接入管理后台，形成评论审核/删除能力。

### 后端

`AdminController` 新增：

- `GET /api/admin/comments`
- `DELETE /api/admin/comments/{commentId}`

`GoodsService` 新增：

- `listAllCommentsForAdmin()`：管理员查看全部评论，包含已删除评论
- `adminDeleteComment(commentId)`：管理员软删除评论，重复删除幂等

`CommentResponse` 增加 `deleted` 字段，便于后台展示状态。

### 测试

`AdminDatabaseTests` 增加管理员评论审核覆盖：

- 管理员可以访问评论列表
- 普通用户不能调用管理员删除接口
- 管理员删除评论后，前台评论列表不再展示该评论

### 管理后台

新增：

- `CommentManageView.vue`
- 路由：`/comments`
- 侧边栏入口：评论管理
- `admin.ts` 评论管理 API
- `GoodsComment` 类型

页面能力：

- 查看评论 ID、商品 ID、用户 ID、内容、状态、时间
- 删除可见评论
- 已删除评论显示状态但不再显示操作按钮

### 验证

已执行：

```bash
cd apps/admin && npm run build
cd apps/web && npm run build
cd apps/backend && mvn -q test
```

结果：通过。


## 2026-05-18 order payment flow slice

本轮补强订单状态流转，让交易闭环更接近真实二手交易。

### 后端

新增接口：

- `PATCH /api/orders/{orderId}/pay`

调整规则：

- 创建订单后状态为 `PENDING_PAYMENT`，商品状态变为 `LOCKED`
- 买家确认支付后订单状态变为 `PAID`
- 卖家只能完成 `PAID` 订单
- 完成后订单状态变为 `COMPLETED`，商品状态变为 `SOLD`
- `PENDING_PAYMENT` 订单可取消，取消后商品恢复 `ON_SALE`
- `PAID` 订单不可取消
- 买家不能直接完成订单
- 卖家不能代买家确认支付

调整文件：

- `OrderService`
- `OrderController`
- `OrderDatabaseTests`

### 前端用户端

调整：

- `api/orders.ts` 新增 `payOrder(orderId)`
- `MyOrdersView.vue` 接入买家确认支付、卖家确认完成

页面行为：

- 我买到的：待支付可“取消订单”或“确认支付”
- 我买到的：已支付显示等待卖家确认完成
- 我卖出的：待支付显示等待买家支付，可取消交易
- 我卖出的：已支付可确认完成

### 验证

已执行：

```bash
cd apps/web && npm run build
cd apps/admin && npm run build
cd apps/backend && mvn -q test
```

结果：通过。


## 2026-05-18 admin order management slice

本轮增强后台订单管理，补齐管理员对交易异常状态的处理能力。

### 后端

新增/调整接口：

- `GET /api/admin/orders?status={status}`
- `PATCH /api/admin/orders/{orderId}/cancel`

规则：

- 管理员可按状态筛选订单
- 管理员可强制取消 `PENDING_PAYMENT` / `PAID` 订单
- 强制取消后订单状态变为 `CANCELED`
- 商品仍处于 `LOCKED` 时恢复 `ON_SALE`
- 普通用户不能调用管理员取消接口
- 已完成、已取消、退款中、已退款订单不可强制取消

测试：

- `AdminDatabaseTests.adminCanFilterAndCancelOrders`

### 管理后台

调整：

- `OrderManageView.vue` 增加状态筛选、刷新、强制取消按钮
- `admin.ts` 增加 `fetchAdminOrders(status)` 与 `adminCancelOrder(orderId)`
- `types.ts` 抽出 `OrderStatus` 类型

### 验证

已执行：

```bash
cd apps/admin && npm run build
cd apps/web && npm run build
cd apps/backend && mvn -q test
```

结果：通过。


## 2026-05-19 admin category management slice

本轮补齐后台分类管理，让后台从只读分类列表升级为可维护分类基础数据。

### 后端

新增请求模型：

- `CategoryManageRequest`

调整响应：

- `CategoryResponse` 增加 `sortOrder`、`enabled`

新增/调整接口：

- `GET /api/admin/categories`：返回全部分类，包括禁用分类
- `POST /api/admin/categories`
- `PATCH /api/admin/categories/{categoryId}`
- `PATCH /api/admin/categories/{categoryId}/enable`
- `PATCH /api/admin/categories/{categoryId}/disable`

规则：

- 公开接口 `/api/categories` 仍只返回启用分类
- 分类名称唯一
- 新增分类默认启用
- 禁用分类后，发布/编辑商品时不能选用该分类
- 非管理员不能调用后台分类写接口

测试：

- `AdminDatabaseTests.adminCanManageCategories`

### 管理后台

调整：

- `CategoryManageView.vue` 增加新增、编辑、启用/禁用
- `admin.ts` 增加分类管理 API
- `types.ts` 扩展 `Category` 类型
- `main.css` 增加行内表单样式

### 验证

已执行：

```bash
cd apps/admin && npm run build
cd apps/web && npm run build
cd apps/backend && mvn -q test
```

结果：通过。


## 2026-05-19 goods image upload alignment

商品图片上传闭环已存在，本轮做前后端约束对齐。

### 调整

- `PublishGoodsView.vue` 增加最多 9 张图片的前端限制
- `EditGoodsView.vue` 增加最多 9 张图片的前端限制
- 文案同步提示：支持 jpg/png/webp/gif，单张不超过 5MB，最多 9 张

### 背景

后端 `CreateGoodsRequest` / `UpdateGoodsRequest` 中 `imageUrls` 已有 `@Size(max = 9)` 约束。此前前端可继续上传超过 9 张，直到提交商品时才被后端拒绝；现在会在选择图片阶段拦截，体验更一致。

### 验证

已执行：

```bash
cd apps/web && npm run build
cd apps/admin && npm run build
cd apps/backend && mvn -q -Dtest=ImageUploadTests,GoodsDatabaseTests test
```

结果：通过。


## 2026-05-19 user profile management slice

本轮补齐用户端个人中心，让账号资料可维护。

### 后端

新增请求模型：

- `UpdateProfileRequest`
- `ChangePasswordRequest`

调整响应：

- `UserProfileResponse` 增加 `status`

新增接口：

- `PATCH /api/auth/me`：修改昵称
- `PATCH /api/auth/password`：修改密码

规则：

- 两个接口都需要登录
- 昵称不能为空，最长 50 字
- 修改密码需要校验原密码
- 新密码长度 6-72 位
- 原密码错误返回 400

测试：

- `AuthDatabaseTests.userCanUpdateProfileAndChangePassword`

### 前端用户端

新增：

- `ProfileView.vue`
- 路由：`/profile`
- 顶部导航入口：个人中心
- `auth.ts` 新增 `updateProfile`、`changePassword`
- `auth` store 新增 `setUser`

页面能力：

- 展示用户名、账号状态、角色
- 修改昵称并同步本地登录态
- 修改密码，前端校验两次新密码一致

### 验证

已执行：

```bash
cd apps/web && npm run build
cd apps/admin && npm run build
cd apps/backend && mvn -q test
```

结果：通过。


## 2026-05-19 admin dashboard summary slice

本轮把后台首页从前端聚合多个列表升级为后端真实统计接口。

### 后端

新增：

- `AdminDashboardResponse`
- `AdminDashboardService`
- `GET /api/admin/dashboard`

统计字段：

- 用户：总数、正常、禁用
- 分类：总数、启用
- 商品：总数、在售、锁定、已售
- 订单：总数、待支付、已支付、已完成
- 评论：总数、可见、已删除

测试：

- `AdminDatabaseTests.bootstrapAdminCanAccessAdminApis` 覆盖 dashboard 接口返回数字字段

### 管理后台

调整：

- `dashboard.ts` 改为调用 `GET /api/admin/dashboard`
- `DashboardView.vue` 展示真实统计卡片和待关注事项
- `types.ts` 新增 `AdminDashboardSummary`

### 验证

已执行：

```bash
cd apps/admin && npm run build
cd apps/web && npm run build
cd apps/backend && mvn -q test
```

结果：通过。


## 2026-05-19 refund flow slice

本轮接入退款/售后状态流转，补齐订单枚举中 `REFUNDING` / `REFUNDED` 的业务含义。

### 后端

新增接口：

- `PATCH /api/orders/{orderId}/refund`：买家申请退款
- `PATCH /api/admin/orders/{orderId}/refund`：管理员确认退款

规则：

- 只有买家可以申请退款
- 只有 `PAID` 订单可以申请退款
- 申请退款后订单变为 `REFUNDING`
- `REFUNDING` 订单不能由卖家确认完成
- 只有管理员可以确认退款
- 只有 `REFUNDING` 订单可以确认退款
- 确认退款后订单变为 `REFUNDED`
- 若商品仍是 `LOCKED`，确认退款后恢复为 `ON_SALE`

统计调整：

- `AdminDashboardResponse` 增加 `refundingOrderCount`、`refundedOrderCount`
- 管理后台首页展示退款中订单数量

测试：

- `OrderDatabaseTests.buyerCanRequestRefundAndAdminCanApprove`

### 前端用户端

调整：

- `orders.ts` 新增 `requestRefund(orderId)`
- `MyOrdersView.vue` 中买家已支付订单可申请退款
- 买卖双方订单列表展示退款中/已退款提示

### 管理后台

调整：

- `admin.ts` 新增 `adminRefundOrder(orderId)`
- `OrderManageView.vue` 对 `REFUNDING` 订单显示“确认退款”
- `DashboardView.vue` 待关注事项新增退款中订单

### 验证

已执行：

```bash
cd apps/admin && npm run build
cd apps/web && npm run build
cd apps/backend && mvn -q test
```

结果：通过。
