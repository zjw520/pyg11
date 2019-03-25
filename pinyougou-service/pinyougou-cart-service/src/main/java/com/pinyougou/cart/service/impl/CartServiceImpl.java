package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Cart;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.CartService")
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num) {
        try {
            Item item = itemMapper.selectByPrimaryKey(itemId);
            String sellerId = item.getSellerId();
            Cart cart = searchCartBySellerId(carts, sellerId);
            if (cart != null) {
                OrderItem orderItem = searchOrderItemByItemId(cart.getOrderItems(), itemId);
                if (orderItem != null) {
                    orderItem.setNum(orderItem.getNum() + num);
                    orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                    if (orderItem.getNum() <= 0) {
                        cart.getOrderItems().remove(orderItem);
                    }
                    if (cart.getOrderItems().size() == 0) {
                        carts.remove(cart);
                    }
                }
            } else {
                cart = new Cart();
                cart.setSellerId(sellerId);
                cart.setSellerName(item.getSeller());
                List<OrderItem> orderItems = new ArrayList<>();
                OrderItem orderItem = createOrderItem(item, num);
                orderItems.add(orderItem);
                cart.setOrderItems(orderItems);
                carts.add(cart);
            }
            return carts;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Cart> findCartsRedis(String username) {
        List<Cart> carts = (List<Cart>) redisTemplate.boundValueOps("cart_" + username).get();
        if (carts == null) {
            carts = new ArrayList<>();
        }
        return carts;
    }

    @Override
    public void saveCartRedis(String username, List<Cart> carts) {
        redisTemplate.boundValueOps("cart_" + username).set(carts);
    }

    @Override
    public List<Cart> mergeCart(List<Cart> cookieCarts, List<Cart> carts) {
        for (Cart cart : cookieCarts) {
            for (OrderItem orderItem : cart.getOrderItems()) {
                carts = addItemToCart(carts, orderItem.getId(), orderItem.getNum());
            }
        }
        return carts;
    }

    private OrderItem createOrderItem(Item item, Integer num) {
        OrderItem orderItem = new OrderItem();
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setTitle(item.getTitle());
        orderItem.setSellerId(item.getSellerId());
        return orderItem;
    }


    private Cart searchCartBySellerId(List<Cart> carts, String sellerId) {
        for (Cart cart : carts) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItems, Long itemId) {
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getItemId() == itemId) {
                return orderItem;
            }
        }
        return null;
    }
}
