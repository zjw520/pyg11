package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Address;

import java.util.List;
import java.util.Map;

/**
 * AddressMapper 数据访问接口
 * @date 2019-02-27 16:21:09
 * @version 1.0
 */
public interface AddressMapper extends Mapper<Address>{


    @Select("select * from `tb_address` where user_id=#{userId} order by is_default desc ")
    List<Map<String,String>> findbyUserId(String userId);
}