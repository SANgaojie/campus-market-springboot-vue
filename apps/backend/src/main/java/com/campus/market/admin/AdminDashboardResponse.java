package com.campus.market.admin;

public record AdminDashboardResponse(
        Long userCount,
        Long enabledUserCount,
        Long disabledUserCount,
        Long categoryCount,
        Long enabledCategoryCount,
        Long goodsCount,
        Long onSaleGoodsCount,
        Long lockedGoodsCount,
        Long soldGoodsCount,
        Long orderCount,
        Long pendingPaymentOrderCount,
        Long paidOrderCount,
        Long completedOrderCount,
        Long refundingOrderCount,
        Long refundedOrderCount,
        Long commentCount,
        Long visibleCommentCount,
        Long deletedCommentCount
) {
}
