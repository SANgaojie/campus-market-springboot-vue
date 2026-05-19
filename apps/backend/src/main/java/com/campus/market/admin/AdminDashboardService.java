package com.campus.market.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.market.goods.Goods;
import com.campus.market.goods.GoodsCategory;
import com.campus.market.goods.GoodsCategoryMapper;
import com.campus.market.goods.GoodsComment;
import com.campus.market.goods.GoodsCommentMapper;
import com.campus.market.goods.GoodsMapper;
import com.campus.market.goods.GoodsStatus;
import com.campus.market.order.OrderStatus;
import com.campus.market.order.TradeOrder;
import com.campus.market.order.TradeOrderMapper;
import com.campus.market.user.User;
import com.campus.market.user.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardService {

    private final UserMapper userMapper;
    private final GoodsCategoryMapper goodsCategoryMapper;
    private final GoodsMapper goodsMapper;
    private final TradeOrderMapper orderMapper;
    private final GoodsCommentMapper goodsCommentMapper;

    public AdminDashboardService(UserMapper userMapper,
                                 GoodsCategoryMapper goodsCategoryMapper,
                                 GoodsMapper goodsMapper,
                                 TradeOrderMapper orderMapper,
                                 GoodsCommentMapper goodsCommentMapper) {
        this.userMapper = userMapper;
        this.goodsCategoryMapper = goodsCategoryMapper;
        this.goodsMapper = goodsMapper;
        this.orderMapper = orderMapper;
        this.goodsCommentMapper = goodsCommentMapper;
    }

    public AdminDashboardResponse summary() {
        return new AdminDashboardResponse(
                userMapper.selectCount(null),
                countUsersByStatus(1),
                countUsersByStatus(0),
                goodsCategoryMapper.selectCount(null),
                countCategoriesByEnabled(1),
                goodsMapper.selectCount(null),
                countGoodsByStatus(GoodsStatus.ON_SALE),
                countGoodsByStatus(GoodsStatus.LOCKED),
                countGoodsByStatus(GoodsStatus.SOLD),
                orderMapper.selectCount(null),
                countOrdersByStatus(OrderStatus.PENDING_PAYMENT),
                countOrdersByStatus(OrderStatus.PAID),
                countOrdersByStatus(OrderStatus.COMPLETED),
                countOrdersByStatus(OrderStatus.REFUNDING),
                countOrdersByStatus(OrderStatus.REFUNDED),
                goodsCommentMapper.selectCount(null),
                countCommentsByDeleted(0),
                countCommentsByDeleted(1)
        );
    }

    private Long countUsersByStatus(Integer status) {
        return userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getStatus, status));
    }

    private Long countCategoriesByEnabled(Integer enabled) {
        return goodsCategoryMapper.selectCount(new LambdaQueryWrapper<GoodsCategory>().eq(GoodsCategory::getEnabled, enabled));
    }

    private Long countGoodsByStatus(GoodsStatus status) {
        return goodsMapper.selectCount(new LambdaQueryWrapper<Goods>().eq(Goods::getStatus, status.name()));
    }

    private Long countOrdersByStatus(OrderStatus status) {
        return orderMapper.selectCount(new LambdaQueryWrapper<TradeOrder>().eq(TradeOrder::getStatus, status.name()));
    }

    private Long countCommentsByDeleted(Integer deleted) {
        return goodsCommentMapper.selectCount(new LambdaQueryWrapper<GoodsComment>().eq(GoodsComment::getDeleted, deleted));
    }
}
