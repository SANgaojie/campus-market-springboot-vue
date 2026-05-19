CREATE TABLE IF NOT EXISTS goods_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    goods_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_goods_favorite_user_goods (user_id, goods_id),
    KEY idx_goods_favorite_user (user_id),
    KEY idx_goods_favorite_goods (goods_id),
    CONSTRAINT fk_goods_favorite_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_goods_favorite_goods FOREIGN KEY (goods_id) REFERENCES goods(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品收藏表';
