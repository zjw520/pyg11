package com.pinyougou.order.service.impl;

import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.Cart;
import com.pinyougou.pojo.Order;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderItemService;
import com.pinyougou.service.OrderService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;

/**
 * OrderServiceImpl 服务接口实现类
 *
 * @version 1.0
 * @date 2019-02-27 16:23:07
 */
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private PayLogMapper payLogMapper;

    /**
     * 添加方法
     */
    public void save(Order order) {
        try {
            List<Cart> carts = (List<Cart>) redisTemplate.boundValueOps("cart_" + order.getUserId()).get();
            List<String> orderIdList = new ArrayList<>();
            double totalMoney = 0;

            for (Cart cart : carts) {
                long orderId = idWorker.nextId();
                Order newOrder = new Order();
                newOrder.setPaymentType(order.getPaymentType());
                newOrder.setStatus("1");
                newOrder.setUserId(order.getUserId());
                newOrder.setReceiverAreaName(order.getReceiverAreaName());
                newOrder.setReceiverMobile(order.getReceiverMobile());
                newOrder.setReceiver(order.getReceiver());
                newOrder.setSourceType(order.getSourceType());
                newOrder.setSellerId(cart.getSellerId());
                double money = 0;
                for (OrderItem orderItem : cart.getOrderItems()) {
                    orderItem.setId(idWorker.nextId());
                    orderItem.setOrderId(orderId);
                    money += orderItem.getTotalFee().doubleValue();
                    orderItemMapper.insertSelective(orderItem);
                }
                newOrder.setPayment(new BigDecimal(money));
                orderMapper.insertSelective(newOrder);

                orderIdList.add(String.valueOf(orderId));
                totalMoney += money;

            }

            if ("1".equals(order.getPaymentType())) {
                PayLog paylog = new PayLog();
                paylog.setCreateTime(new Date());
                String outTradeNo = String.valueOf(idWorker.nextId());
                paylog.setOutTradeNo(outTradeNo);
                paylog.setTotalFee((long) (totalMoney * 100));
                paylog.setUserId(order.getUserId());
                paylog.setTradeState("0");
                String ids = orderIdList.toString().replace("[", "")
                        .replace("]", "").replace(" ", "");
                paylog.setOrderList(ids);
                paylog.setPayType("1");

                payLogMapper.insertSelective(paylog);
                redisTemplate.boundValueOps("payLog_" + order.getUserId()).set(paylog);
            }

            redisTemplate.delete("cart_" + order.getUserId());

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 修改方法
     */
    public void update(Order order) {
        try {
            orderMapper.updateByPrimaryKeySelective(order);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id删除
     */
    public void delete(Serializable id) {
        try {
            orderMapper.deleteByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 批量删除
     */
    public void deleteAll(Serializable[] ids) {
        try {
            // 创建示范对象
            Example example = new Example(Order.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // 创建In条件
            criteria.andIn("id", Arrays.asList(ids));
            // 根据示范对象删除
            orderMapper.deleteByExample(example);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id查询
     */
    public Order findOne(Serializable id) {
        try {
            return orderMapper.selectByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 查询全部
     */
    public List<Order> findAll() {
        try {
            return orderMapper.selectAll();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 多条件分页查询
     */
    public List<Order> findByPage(Order order, int page, int rows) {
        try {
            PageInfo<Order> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            orderMapper.selectAll();
                        }
                    });
            return pageInfo.getList();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public PayLog findPayLogFromRedis(String userId) {
        try {
            return (PayLog) redisTemplate.boundValueOps("payLog_" + userId).get();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updateOrderStatus(String outTradeNo, String transaction_id) {
        try {
            PayLog payLog = new PayLog();
            payLog = payLogMapper.selectByPrimaryKey(outTradeNo);
            payLog.setPayTime(new Date());
            payLog.setTradeState("1");
            payLog.setTransactionId(transaction_id);
            payLogMapper.updateByPrimaryKeySelective(payLog);

            String[] orderIds = payLog.getOrderList().split(",");
            for (String orderId : orderIds) {
                Order order = new Order();
                order.setOrderId(Long.valueOf(orderId));
                order.setPaymentTime(new Date());
                order.setStatus("2");
                orderMapper.updateByPrimaryKeySelective(order);
            }
            redisTemplate.delete("payLog_" + payLog.getUserId());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}