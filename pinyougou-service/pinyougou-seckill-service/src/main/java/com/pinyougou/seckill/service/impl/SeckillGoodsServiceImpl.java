package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Service(interfaceName = "com.pinyougou.service.SeckillGoodsService")
@Transactional
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void save(SeckillGoods seckillGoods) {

    }

    @Override
    public void update(SeckillGoods seckillGoods) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public SeckillGoods findOne(Serializable id) {
        return null;
    }

    @Override
    public List<SeckillGoods> findAll() {
        return null;
    }

    @Override
    public List<SeckillGoods> findByPage(SeckillGoods seckillGoods, int page, int rows) {
        return null;
    }

    @Override
    public List<SeckillGoods> findSeckillGoods() {

        List<SeckillGoods> seckillGoodsList = null;

        try {
            seckillGoodsList = redisTemplate.boundHashOps("seckillGoodsList").values();
            if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
                return seckillGoodsList;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status", "1");
            criteria.andGreaterThan("stockCount", 0);
            criteria.andLessThanOrEqualTo("startTime", new Date());
            criteria.andGreaterThanOrEqualTo("endTime", new Date());
            seckillGoodsList = seckillGoodsMapper.selectByExample(example);

            try {
                for (SeckillGoods seckillGoods : seckillGoodsList) {
                    redisTemplate.boundHashOps("seckillGoodsList").put(seckillGoods.getId(), seckillGoods);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return seckillGoodsList;
    }

    @Override
    public SeckillGoods findOneFromRedis(Long id) {
        try {
            return (SeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
