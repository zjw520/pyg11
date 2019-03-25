package com.pinyougou.seckill.service.impl;

import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Override
    public void save(SeckillOrder seckillOrder) {

    }

    @Override
    public void update(SeckillOrder seckillOrder) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public SeckillOrder findOne(Serializable id) {
        return null;
    }

    @Override
    public List<SeckillOrder> findAll() {
        return null;
    }

    @Override
    public List<SeckillOrder> findByPage(SeckillOrder seckillOrder, int page, int rows) {
        return null;
    }

    @Override
    public synchronized void saveOrderFromRedis(Long id, String username) {
        try {
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(id);
            if (seckillGoods != null && seckillGoods.getStockCount() > 0) {
                seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
                if (seckillGoods.getStockCount() <= 0) {
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
                    redisTemplate.boundHashOps("seckillGoodsList").delete(id);
                } else {
                    redisTemplate.boundHashOps("seckillGoodsList").put(id, seckillGoods);
                }

                SeckillOrder seckillOrder = new SeckillOrder();
                // 设置订单id
                seckillOrder.setId(idWorker.nextId());
                // 设置秒杀商品id
                seckillOrder.setSeckillId(id);
                // 设置秒杀价格
                seckillOrder.setMoney(seckillGoods.getCostPrice());
                // 设置用户id
                seckillOrder.setUserId(username);
                // 设置商家id
                seckillOrder.setSellerId(seckillGoods.getSellerId());
                // 设置创建时间
                seckillOrder.setCreateTime(new Date());
                // 设置状态码(未付款)
                seckillOrder.setStatus("0");
                // 保存订单到Redis
                redisTemplate.boundHashOps("seckillOrderList")
                        .put(username, seckillOrder);


            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public SeckillOrder findSeckillOrderFromRedis(String userId) {
        try {
            return (SeckillOrder) redisTemplate.boundHashOps("seckillOrderList").get(userId);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void saveOrder(String userId, String transactionId) {
        try{
            /** 根据用户ID从redis中查询秒杀订单 */
            SeckillOrder seckillOrder = (SeckillOrder)redisTemplate
                    .boundHashOps("seckillOrderList").get(userId);
            /** 判断秒杀订单 */
            if(seckillOrder != null){
                /** 微信交易流水号 */
                seckillOrder.setTransactionId(transactionId);
                /** 支付时间 */
                seckillOrder.setPayTime(new Date());
                /** 状态码(已付款) */
                seckillOrder.setStatus("1");
                /** 保存到数据库 */
                seckillOrderMapper.insertSelective(seckillOrder);
                /** 删除Redis中的订单 */
                redisTemplate.boundHashOps("seckillOrderList").delete(userId);
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }

    }
}
