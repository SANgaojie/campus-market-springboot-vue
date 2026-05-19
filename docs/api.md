# API 接口文档

项目集成 `springdoc-openapi`，本地启动后可访问 Swagger UI：

```text
http://localhost:8080/swagger-ui.html
```

## 通用响应格式

```json
{
  "code": 200,
  "message": "OK",
  "data": {}
}
```

## 认证模块

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/api/auth/register` | 用户注册 |
| `POST` | `/api/auth/login` | 用户/管理员登录，返回 JWT |
| `GET` | `/api/auth/me` | 获取当前登录用户 |
| `PATCH` | `/api/auth/me` | 修改昵称 |
| `PATCH` | `/api/auth/password` | 修改密码 |

## 商品模块

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/categories` | 查询启用分类 |
| `GET` | `/api/goods` | 查询在售商品，支持搜索筛选 |
| `GET` | `/api/goods/{goodsId}` | 商品详情 |
| `POST` | `/api/goods` | 发布商品 |
| `PUT` | `/api/goods/{goodsId}` | 编辑商品 |
| `GET` | `/api/goods/mine` | 我的发布 |
| `PATCH` | `/api/goods/{goodsId}/off-shelf` | 下架商品 |
| `PATCH` | `/api/goods/{goodsId}/relist` | 重新上架 |
| `POST` | `/api/images/goods` | 上传商品图片 |

### 商品搜索参数

`GET /api/goods` 支持以下查询参数：

| 参数 | 类型 | 说明 |
| --- | --- | --- |
| `categoryId` | number | 分类 ID |
| `keyword` | string | 标题/描述关键词 |
| `minPrice` | number | 最低价格 |
| `maxPrice` | number | 最高价格 |
| `minCondition` | number | 最低成色，1-5 |

示例：

```bash
curl 'http://localhost:8080/api/goods?keyword=MacBook&minPrice=1000&minCondition=4'
```

## 收藏与评论

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/goods/favorites` | 我的收藏 |
| `POST` | `/api/goods/{goodsId}/favorite` | 收藏商品 |
| `DELETE` | `/api/goods/{goodsId}/favorite` | 取消收藏 |
| `GET` | `/api/goods/{goodsId}/comments` | 商品评论列表 |
| `POST` | `/api/goods/{goodsId}/comments` | 发表评论 |
| `DELETE` | `/api/goods/comments/{commentId}` | 删除自己的评论/卖家删除本商品评论 |

## 订单模块

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/api/orders` | 创建订单 |
| `GET` | `/api/orders/bought` | 我买到的 |
| `GET` | `/api/orders/sold` | 我卖出的 |
| `GET` | `/api/orders/{orderId}` | 订单详情 |
| `PATCH` | `/api/orders/{orderId}/cancel` | 取消待支付订单 |
| `PATCH` | `/api/orders/{orderId}/pay` | 买家确认支付 |
| `PATCH` | `/api/orders/{orderId}/complete` | 卖家确认完成 |
| `PATCH` | `/api/orders/{orderId}/refund` | 买家申请退款 |

订单主流程：

```text
PENDING_PAYMENT -> PAID -> COMPLETED
        │              │
        └-> CANCELED   └-> REFUNDING -> REFUNDED
```

## 管理后台

管理员账号默认：`admin / admin123456`。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/admin/dashboard` | 后台统计概览 |
| `GET` | `/api/admin/users` | 用户管理 |
| `PATCH` | `/api/admin/users/{userId}/enable` | 启用用户 |
| `PATCH` | `/api/admin/users/{userId}/disable` | 禁用用户 |
| `GET` | `/api/admin/goods` | 商品管理 |
| `PATCH` | `/api/admin/goods/{goodsId}/off-shelf` | 管理员下架商品 |
| `GET` | `/api/admin/orders` | 订单管理，支持 `status` 筛选 |
| `PATCH` | `/api/admin/orders/{orderId}/cancel` | 管理员强制取消异常订单 |
| `PATCH` | `/api/admin/orders/{orderId}/refund` | 管理员确认退款 |
| `GET` | `/api/admin/categories` | 分类管理 |
| `POST` | `/api/admin/categories` | 新增分类 |
| `PATCH` | `/api/admin/categories/{categoryId}` | 修改分类 |
| `PATCH` | `/api/admin/categories/{categoryId}/enable` | 启用分类 |
| `PATCH` | `/api/admin/categories/{categoryId}/disable` | 禁用分类 |
| `GET` | `/api/admin/comments` | 评论管理 |
| `DELETE` | `/api/admin/comments/{commentId}` | 管理员删除评论 |
