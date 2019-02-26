package com.pinyougou.service;

import com.pinyougou.pojo.Brand;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BrandMapper {

    @Select("select * from tb_brand order by id asc")
    List<Brand> findAll();
}
