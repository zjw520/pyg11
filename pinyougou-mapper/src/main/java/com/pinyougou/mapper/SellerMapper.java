package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Seller;

import java.util.List;

/**
 * SellerMapper 数据访问接口
 *
 * @version 1.0
 * @date 2019-02-27 16:21:09
 */
public interface SellerMapper extends Mapper<Seller> {


    List<Seller> findAll(Seller seller);

    @Update("update tb_seller set status = #{status} where seller_id = #{sellerId}")
    void updateStatus(@Param("sellerId") String sellerId, @Param("status") String status);
}