package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Goods;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * GoodsMapper 数据访问接口
 * @date 2019-02-27 16:21:09
 * @version 1.0
 */
public interface GoodsMapper extends Mapper<Goods>{


    List<Map<String,Object>> findAll(Goods goods);

    void updateAll(@Param("ids")Long[] ids, @Param("status") String status);

    void updateMarketable(@Param("ids")Long[] ids, @Param("status")String status);

    void updateDeleteStatus(Serializable[] ids, String s);
}