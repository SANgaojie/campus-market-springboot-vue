#!/usr/bin/env bash
# reset-demo-data 模块
#
# @author 阿德
# @date 2026/05/06
set -euo pipefail

DB_NAME="${DB_NAME:-campus_market}"
DB_USERNAME="${DB_USERNAME:-campus_app}"
DB_PASSWORD="${DB_PASSWORD:-campus_dev_password}"
BACKEND_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../apps/backend" && pwd)"
UPLOAD_DIR="${APP_UPLOAD_DIR:-$BACKEND_DIR/uploads}"
DEMO_IMAGE_DIR="$UPLOAD_DIR/goods"

mkdir -p "$DEMO_IMAGE_DIR"

write_svg() {
  local file="$1"
  local bg="$2"
  local fg="$3"
  local title="$4"
  cat > "$DEMO_IMAGE_DIR/$file" <<SVG
<svg xmlns="http://www.w3.org/2000/svg" width="960" height="720" viewBox="0 0 960 720">
  <defs>
    <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0" stop-color="$bg"/>
      <stop offset="1" stop-color="$fg"/>
    </linearGradient>
  </defs>
  <rect width="960" height="720" rx="48" fill="url(#g)"/>
  <rect x="96" y="116" width="768" height="416" rx="36" fill="rgba(255,255,255,0.28)"/>
  <circle cx="240" cy="252" r="58" fill="rgba(255,255,255,0.45)"/>
  <path d="M146 500l178-156 126 112 86-76 176 120H146z" fill="rgba(255,255,255,0.58)"/>
  <text x="480" y="618" text-anchor="middle" font-family="Arial, 'Microsoft YaHei', sans-serif" font-size="44" font-weight="800" fill="white">$title</text>
</svg>
SVG
}

write_svg macbook.svg '#5b7cfa' '#7c3aed' 'MacBook Air'
write_svg camera.svg '#0ea5e9' '#14b8a6' '微单相机'
write_svg book.svg '#f97316' '#ef4444' '考研教材'
write_svg bicycle.svg '#22c55e' '#0f766e' '校园自行车'
write_svg lamp.svg '#facc15' '#fb923c' '宿舍台灯'
write_svg keyboard.svg '#64748b' '#111827' '机械键盘'
write_svg hoodie.svg '#ec4899' '#8b5cf6' '连帽卫衣'
write_svg racket.svg '#38bdf8' '#2563eb' '羽毛球拍'
write_svg headphones.svg '#a855f7' '#0f172a' '降噪耳机'
write_svg calculator.svg '#10b981' '#0369a1' '图形计算器'

MYSQL_PWD="$DB_PASSWORD" mysql -u"$DB_USERNAME" "$DB_NAME" <<'SQL'
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE goods_favorite;
TRUNCATE TABLE goods_comment;
TRUNCATE TABLE trade_order;
TRUNCATE TABLE goods_image;
TRUNCATE TABLE goods;
TRUNCATE TABLE user_role;
TRUNCATE TABLE sys_user;
TRUNCATE TABLE goods_category;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO sys_user (id, username, password_hash, nickname, phone, email, status, created_at) VALUES
(1, 'admin', '$2a$10$XJUOrC3hxOEPD0a5vSd/4OcCQs.dcIPQG2Ot1Gm18TqxQCEJ8.Swu', '系统管理员', NULL, 'admin@campus.test', 1, NOW() - INTERVAL 20 DAY),
(2, 'seller_lin', '$2a$10$XJUOrC3hxOEPD0a5vSd/4OcCQs.dcIPQG2Ot1Gm18TqxQCEJ8.Swu', '林同学', '13800000002', 'lin@campus.test', 1, NOW() - INTERVAL 12 DAY),
(3, 'seller_chen', '$2a$10$XJUOrC3hxOEPD0a5vSd/4OcCQs.dcIPQG2Ot1Gm18TqxQCEJ8.Swu', '陈同学', '13800000003', 'chen@campus.test', 1, NOW() - INTERVAL 10 DAY),
(4, 'buyer_wang', '$2a$10$XJUOrC3hxOEPD0a5vSd/4OcCQs.dcIPQG2Ot1Gm18TqxQCEJ8.Swu', '王同学', '13800000004', 'wang@campus.test', 1, NOW() - INTERVAL 8 DAY),
(5, 'buyer_zhao', '$2a$10$XJUOrC3hxOEPD0a5vSd/4OcCQs.dcIPQG2Ot1Gm18TqxQCEJ8.Swu', '赵同学', '13800000005', 'zhao@campus.test', 1, NOW() - INTERVAL 6 DAY);

INSERT INTO user_role (user_id, role_code) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER'),
(3, 'ROLE_USER'),
(4, 'ROLE_USER'),
(5, 'ROLE_USER');

INSERT INTO goods_category (id, name, sort_order, enabled) VALUES
(1, '数码电子', 10, 1),
(2, '图书教材', 20, 1),
(3, '生活用品', 30, 1),
(4, '运动户外', 40, 1),
(5, '美妆服饰', 50, 1);

INSERT INTO goods (id, seller_id, category_id, title, description, price, condition_level, status, version, created_at) VALUES
(1, 2, 1, 'MacBook Air M1 8G 256G', '自用电脑，电池健康 91%，适合写代码和做课程设计，附原装充电器。', 3999.00, 4, 'ON_SALE', 0, NOW() - INTERVAL 2 HOUR),
(2, 3, 1, 'Sony A6000 微单套机', '入门摄影套机，含 16-50 镜头和备用电池，成色见图。', 2180.00, 4, 'ON_SALE', 0, NOW() - INTERVAL 5 HOUR),
(3, 2, 2, '考研数学复习全书 + 真题', '部分页有笔记，适合二轮复习，打包出。', 68.00, 3, 'ON_SALE', 0, NOW() - INTERVAL 1 DAY),
(4, 3, 4, '捷安特校园通勤自行车', '车况稳定，适合校内通勤，支持当面看车。', 520.00, 3, 'ON_SALE', 0, NOW() - INTERVAL 2 DAY),
(5, 2, 3, '米家 LED 宿舍台灯', '亮度可调，晚上看书很舒服，毕业闲置。', 59.00, 4, 'ON_SALE', 0, NOW() - INTERVAL 3 DAY),
(6, 3, 1, 'Keychron K2 机械键盘', '茶轴，蓝牙/有线双模，键帽轻微使用痕迹。', 239.00, 4, 'ON_SALE', 0, NOW() - INTERVAL 4 DAY),
(7, 2, 5, '学院风连帽卫衣 L 码', '只穿过两次，尺码偏宽松。', 89.00, 5, 'ON_SALE', 0, NOW() - INTERVAL 5 DAY),
(8, 3, 4, '尤尼克斯羽毛球拍', '适合新手进阶，送一卷手胶。', 168.00, 3, 'ON_SALE', 0, NOW() - INTERVAL 6 DAY),
(9, 2, 1, 'Bose QC35 降噪耳机', '功能正常，耳罩去年换过，适合自习室。', 699.00, 3, 'LOCKED', 0, NOW() - INTERVAL 7 DAY),
(10, 3, 2, '卡西欧图形计算器', '课程已结束，功能正常，附保护壳。', 320.00, 4, 'SOLD', 0, NOW() - INTERVAL 8 DAY),
(11, 2, 3, '宿舍收纳架三层', '可拆卸，搬宿舍方便。', 35.00, 3, 'OFF_SHELF', 0, NOW() - INTERVAL 9 DAY),
(12, 3, 1, 'iPad mini 6 64G', '屏幕完好，边框轻微磕碰，暂时下架保留。', 2580.00, 4, 'OFF_SHELF', 0, NOW() - INTERVAL 10 DAY);

INSERT INTO goods_image (goods_id, image_url, sort_order) VALUES
(1, '/uploads/goods/macbook.svg', 0),
(2, '/uploads/goods/camera.svg', 0),
(3, '/uploads/goods/book.svg', 0),
(4, '/uploads/goods/bicycle.svg', 0),
(5, '/uploads/goods/lamp.svg', 0),
(6, '/uploads/goods/keyboard.svg', 0),
(7, '/uploads/goods/hoodie.svg', 0),
(8, '/uploads/goods/racket.svg', 0),
(9, '/uploads/goods/headphones.svg', 0),
(10, '/uploads/goods/calculator.svg', 0);

INSERT INTO trade_order (id, order_no, goods_id, buyer_id, seller_id, amount, status, version, created_at) VALUES
(1, 'DEMO202605190001', 9, 4, 2, 699.00, 'PENDING_PAYMENT', 0, NOW() - INTERVAL 1 DAY),
(2, 'DEMO202605190002', 10, 5, 3, 320.00, 'COMPLETED', 0, NOW() - INTERVAL 3 DAY),
(3, 'DEMO202605190003', 6, 4, 3, 239.00, 'REFUNDED', 0, NOW() - INTERVAL 5 DAY);

INSERT INTO goods_comment (goods_id, user_id, content, deleted, created_at) VALUES
(1, 4, '电脑还在吗？可以今天晚上在图书馆门口看一下吗？', 0, NOW() - INTERVAL 90 MINUTE),
(1, 2, '还在的，晚上 7 点以后都可以。', 0, NOW() - INTERVAL 70 MINUTE),
(4, 5, '车可以小刀吗？', 0, NOW() - INTERVAL 1 DAY),
(5, 4, '台灯支持 Type-C 供电吗？', 0, NOW() - INTERVAL 2 DAY);

INSERT INTO goods_favorite (user_id, goods_id, created_at) VALUES
(4, 1, NOW() - INTERVAL 1 DAY),
(4, 6, NOW() - INTERVAL 2 DAY),
(5, 2, NOW() - INTERVAL 3 DAY);
SQL

echo "[demo-data] Reset complete for database '$DB_NAME'."
echo "[demo-data] Demo accounts all use password: admin123456"
echo "[demo-data] Images written to: $DEMO_IMAGE_DIR"
