package com.campus.market.goods;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long goodsId,
        Long userId,
        String content,
        Integer deleted,
        LocalDateTime createdAt
) {
}
