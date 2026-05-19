package com.campus.market.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.market.common.BusinessException;
import com.campus.market.goods.Goods;
import com.campus.market.goods.GoodsMapper;
import com.campus.market.goods.GoodsStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private static final DateTimeFormatter ORDER_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final TradeOrderMapper orderMapper;
    private final GoodsMapper goodsMapper;

    public OrderService(TradeOrderMapper orderMapper, GoodsMapper goodsMapper) {
        this.orderMapper = orderMapper;
        this.goodsMapper = goodsMapper;
    }

    @Transactional
    public OrderResponse create(Long buyerId, CreateOrderRequest request) {
        var goods = goodsMapper.selectById(request.goodsId());
        if (goods == null || !GoodsStatus.ON_SALE.name().equals(goods.getStatus())) {
            throw new BusinessException(404, "商品不存在或不可购买");
        }
        if (buyerId.equals(goods.getSellerId())) {
            throw new BusinessException(400, "不能购买自己发布的商品");
        }

        goods.setStatus(GoodsStatus.LOCKED.name());
        goodsMapper.updateById(goods);

        var order = new TradeOrder();
        order.setOrderNo(generateOrderNo());
        order.setGoodsId(goods.getId());
        order.setBuyerId(buyerId);
        order.setSellerId(goods.getSellerId());
        order.setAmount(goods.getPrice());
        order.setStatus(OrderStatus.PENDING_PAYMENT.name());
        order.setVersion(0);
        orderMapper.insert(order);

        return toResponse(order);
    }

    public List<OrderResponse> listAllForAdmin(OrderStatus status) {
        var query = new LambdaQueryWrapper<TradeOrder>()
                .orderByDesc(TradeOrder::getCreatedAt);
        if (status != null) {
            query.eq(TradeOrder::getStatus, status.name());
        }
        return orderMapper.selectList(query)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OrderResponse> listAllForAdmin() {
        return listAllForAdmin(null);
    }

    public List<OrderResponse> listBought(Long buyerId) {
        return orderMapper.selectList(new LambdaQueryWrapper<TradeOrder>()
                        .eq(TradeOrder::getBuyerId, buyerId)
                        .orderByDesc(TradeOrder::getCreatedAt))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OrderResponse> listSold(Long sellerId) {
        return orderMapper.selectList(new LambdaQueryWrapper<TradeOrder>()
                        .eq(TradeOrder::getSellerId, sellerId)
                        .orderByDesc(TradeOrder::getCreatedAt))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse detail(Long userId, Long orderId) {
        return toResponse(getVisibleOrder(userId, orderId));
    }

    @Transactional
    public OrderResponse cancel(Long userId, Long orderId) {
        var order = getVisibleOrder(userId, orderId);
        if (!OrderStatus.PENDING_PAYMENT.name().equals(order.getStatus())) {
            throw new BusinessException(400, "只有待支付订单可以取消");
        }
        if (!userId.equals(order.getBuyerId()) && !userId.equals(order.getSellerId())) {
            throw new BusinessException(404, "订单不存在");
        }
        order.setStatus(OrderStatus.CANCELED.name());
        orderMapper.updateById(order);

        var goods = goodsMapper.selectById(order.getGoodsId());
        if (goods != null && GoodsStatus.LOCKED.name().equals(goods.getStatus())) {
            goods.setStatus(GoodsStatus.ON_SALE.name());
            goodsMapper.updateById(goods);
        }
        return toResponse(orderMapper.selectById(orderId));
    }

    @Transactional
    public OrderResponse adminCancel(Long orderId) {
        var order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!OrderStatus.PENDING_PAYMENT.name().equals(order.getStatus()) && !OrderStatus.PAID.name().equals(order.getStatus())) {
            throw new BusinessException(400, "只有待支付或已支付订单可以强制取消");
        }
        order.setStatus(OrderStatus.CANCELED.name());
        orderMapper.updateById(order);

        var goods = goodsMapper.selectById(order.getGoodsId());
        if (goods != null && GoodsStatus.LOCKED.name().equals(goods.getStatus())) {
            goods.setStatus(GoodsStatus.ON_SALE.name());
            goodsMapper.updateById(goods);
        }
        return toResponse(orderMapper.selectById(orderId));
    }

    @Transactional
    public OrderResponse pay(Long userId, Long orderId) {
        var order = getVisibleOrder(userId, orderId);
        if (!userId.equals(order.getBuyerId())) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!OrderStatus.PENDING_PAYMENT.name().equals(order.getStatus())) {
            throw new BusinessException(400, "只有待支付订单可以确认支付");
        }
        order.setStatus(OrderStatus.PAID.name());
        orderMapper.updateById(order);
        return toResponse(orderMapper.selectById(orderId));
    }

    @Transactional
    public OrderResponse requestRefund(Long userId, Long orderId) {
        var order = getVisibleOrder(userId, orderId);
        if (!userId.equals(order.getBuyerId())) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!OrderStatus.PAID.name().equals(order.getStatus())) {
            throw new BusinessException(400, "只有已支付订单可以申请退款");
        }
        order.setStatus(OrderStatus.REFUNDING.name());
        orderMapper.updateById(order);
        return toResponse(orderMapper.selectById(orderId));
    }

    @Transactional
    public OrderResponse adminRefund(Long orderId) {
        var order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!OrderStatus.REFUNDING.name().equals(order.getStatus())) {
            throw new BusinessException(400, "只有退款中订单可以确认退款");
        }
        order.setStatus(OrderStatus.REFUNDED.name());
        orderMapper.updateById(order);

        var goods = goodsMapper.selectById(order.getGoodsId());
        if (goods != null && GoodsStatus.LOCKED.name().equals(goods.getStatus())) {
            goods.setStatus(GoodsStatus.ON_SALE.name());
            goodsMapper.updateById(goods);
        }
        return toResponse(orderMapper.selectById(orderId));
    }

    @Transactional
    public OrderResponse complete(Long userId, Long orderId) {
        var order = getVisibleOrder(userId, orderId);
        if (!userId.equals(order.getSellerId())) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!OrderStatus.PAID.name().equals(order.getStatus())) {
            throw new BusinessException(400, "只有已支付订单可以完成");
        }
        order.setStatus(OrderStatus.COMPLETED.name());
        orderMapper.updateById(order);

        var goods = goodsMapper.selectById(order.getGoodsId());
        if (goods != null) {
            goods.setStatus(GoodsStatus.SOLD.name());
            goodsMapper.updateById(goods);
        }
        return toResponse(orderMapper.selectById(orderId));
    }

    private TradeOrder getVisibleOrder(Long userId, Long orderId) {
        var order = orderMapper.selectById(orderId);
        if (order == null || (!userId.equals(order.getBuyerId()) && !userId.equals(order.getSellerId()))) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private String generateOrderNo() {
        var timePart = LocalDateTime.now().format(ORDER_TIME_FORMAT);
        var randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        return "CM" + timePart + randomPart;
    }

    private OrderResponse toResponse(TradeOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNo(),
                order.getGoodsId(),
                order.getBuyerId(),
                order.getSellerId(),
                order.getAmount(),
                OrderStatus.valueOf(order.getStatus()),
                order.getCreatedAt()
        );
    }
}
