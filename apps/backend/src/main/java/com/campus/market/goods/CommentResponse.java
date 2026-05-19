package com.campus.market.goods;

import java.time.LocalDateTime;

/**
 * CommentResponse 业务组件。
 *
 * @author 阿德
 * @date 2026/05/17
 */
public record CommentResponse(
        Long id,
        Long goodsId,
        Long userId,
        String content,
        Integer deleted,
        LocalDateTime createdAt
) {
}
