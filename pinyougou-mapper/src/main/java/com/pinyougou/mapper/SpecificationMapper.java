package com.pinyougou.mapper;

import com.pinyougou.pojo.SpecificationOption;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Specification;

import java.util.List;

/**
 * SpecificationMapper 数据访问接口
 * @date 2019-02-27 16:21:09
 * @version 1.0
 */
public interface SpecificationMapper extends Mapper<Specification>{


    List<Specification> findAll(Specification specification);

}