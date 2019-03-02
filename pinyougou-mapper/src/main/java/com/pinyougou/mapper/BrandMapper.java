package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Brand;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * BrandMapper 数据访问接口
 *
 * @version 1.0
 * @date 2019-02-27 16:21:09
 */
public interface BrandMapper extends Mapper<Brand> {

    @Select("select id,name as text from tb_brand order by id asc")
    List<Map<String, Object>> findAllByIdAndName();

    List<Brand> findAll(Brand brand);

}