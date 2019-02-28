package com.pinyougou.mapper;

import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Brand;

import java.io.Serializable;
import java.util.List;

/**
 * BrandMapper 数据访问接口
 *
 * @version 1.0
 * @date 2019-02-27 16:21:09
 */
public interface BrandMapper extends Mapper<Brand> {

    List<Brand> findAll(Brand brand);

}