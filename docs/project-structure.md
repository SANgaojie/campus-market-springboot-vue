# Project Structure

本项目采用前后端分离 + 文档/脚本/数据库分区的结构，避免业务代码、文档和构建产物混在一起。

```text
campus-market-springboot-vue/
├── apps/
│   ├── backend/              # Java 17 + Spring Boot 3 后端服务
│   ├── web/                  # Vue 3 用户端
│   └── admin/                # Vue 3 管理后台
├── database/
│   ├── migrations/           # 数据库结构 SQL 备份
│   └── seed/                 # 初始化/演示数据 SQL 备份
├── docs/
│   ├── api.md                # 接口文档
│   ├── screenshots/          # 项目截图
│   ├── project-structure.md  # 当前目录说明
│   └── modernization-audit.md# 清理和升级记录
├── infra/
│   └── docker/               # Docker/部署配置预留目录
├── scripts/
│   └── dev/                  # 本地开发辅助脚本
├── docker-compose.yml        # MySQL + Redis + 后端 + 前端一键启动
├── README.md
└── .gitignore
```

## 命名约定

- `apps/backend`：唯一后端应用，避免继续沿用旧项目名 `stucompla-*`。
- `apps/web`：面向普通用户的校园二手交易前台。
- `apps/admin`：面向管理员的用户、商品、订单、分类、评论管理后台。
- `database/migrations`：保留结构 SQL 备份；运行时迁移以 `apps/backend/src/main/resources/db/migration` 为准。
- `database/seed`：保留测试账号、商品分类、演示商品等初始化数据备份。
- `docs`：保留项目说明、接口文档、截图、架构设计和审计记录。
- `infra`：保留部署相关内容，不和业务代码混放。
- `scripts`：保留本地启动、构建、数据初始化、截图生成等脚本。
