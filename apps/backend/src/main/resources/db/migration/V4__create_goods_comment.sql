CREATE TABLE IF NOT EXISTS goods_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    goods_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content VARCHAR(500) NOT NULL,
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '1 deleted, 0 visible',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_goods_comment_goods_created (goods_id, created_at),
    KEY idx_goods_comment_user (user_id),
    CONSTRAINT fk_goods_comment_goods FOREIGN KEY (goods_id) REFERENCES goods(id),
    CONSTRAINT fk_goods_comment_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评论表';
