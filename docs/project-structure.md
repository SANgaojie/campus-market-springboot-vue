# Project Structure

本项目采用前后端分离 + 文档/脚本/数据库分区的结构，避免业务代码、历史资料和构建产物混在一起。

```text
stuSecond-modern/
├── apps/
│   ├── backend/              # Java 17 + Spring Boot 3 后端服务
│   ├── web/                  # Vue 3 用户端，后续创建
│   └── admin/                # Vue 3 管理后台，后续创建
├── database/
│   ├── migrations/           # 数据库结构变更 SQL
│   └── seed/                 # 初始化/演示数据 SQL
├── docs/
│   ├── project-structure.md  # 当前目录说明
│   └── modernization-audit.md# 清理和升级记录
├── infra/
│   └── docker/               # Docker / docker-compose 配置，后续创建
├── scripts/
│   └── dev/                  # 本地开发辅助脚本
├── README.md
└── .gitignore
```

## 命名约定

- `apps/backend`：唯一后端应用，避免继续沿用旧项目名 `stucompla-*`。
- `apps/web`：面向普通用户的校园二手交易前台。
- `apps/admin`：面向管理员的审核、商品、订单管理后台。
- `database/migrations`：只放建表/变更表结构 SQL。
- `database/seed`：只放测试账号、商品分类、演示商品等初始化数据。
- `docs`：保留项目说明、架构设计、审计记录、面试讲解材料。
- `infra`：保留部署相关内容，不和业务代码混放。
- `scripts`：保留本地启动、构建、数据初始化等脚本。

## 后续开发顺序

1. `database/migrations`：先定义核心表结构。
2. `apps/backend`：实现用户认证、商品、订单核心模块。
3. `apps/web`：实现用户端页面。
4. `apps/admin`：实现管理后台页面。
5. `infra/docker`：补 Docker Compose，一键启动 MySQL、Redis、后端和前端。
