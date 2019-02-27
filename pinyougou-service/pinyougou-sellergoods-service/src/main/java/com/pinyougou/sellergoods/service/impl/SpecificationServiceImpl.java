package com.pinyougou.sellergoods.service.impl;

import com.pinyougou.pojo.Specification;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.service.SpecificationService;
import java.util.List;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import java.io.Serializable;
import java.util.Arrays;
/**
 * SpecificationServiceImpl 服务接口实现类
 * @date 2019-02-27 16:23:07
 * @version 1.0
 */
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private SpecificationMapper specificationMapper;

	/** 添加方法 */
	public void save(Specification specification){
		try {
			specificationMapper.insertSelective(specification);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 修改方法 */
	public void update(Specification specification){
		try {
			specificationMapper.updateByPrimaryKeySelective(specification);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id删除 */
	public void delete(Serializable id){
		try {
			specificationMapper.deleteByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 批量删除 */
	public void deleteAll(Serializable[] ids){
		try {
			// 创建示范对象
			Example example = new Example(Specification.class);
			// 创建条件对象
			Example.Criteria criteria = example.createCriteria();
			// 创建In条件
			criteria.andIn("id", Arrays.asList(ids));
			// 根据示范对象删除
			specificationMapper.deleteByExample(example);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 根据主键id查询 */
	public Specification findOne(Serializable id){
		try {
			return specificationMapper.selectByPrimaryKey(id);
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 查询全部 */
	public List<Specification> findAll(){
		try {
			return specificationMapper.selectAll();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

	/** 多条件分页查询 */
	public List<Specification> findByPage(Specification specification, int page, int rows){
		try {
			PageInfo<Specification> pageInfo = PageHelper.startPage(page, rows)
				.doSelectPageInfo(new ISelect() {
					@Override
					public void doSelect() {
						specificationMapper.selectAll();
					}
				});
			return pageInfo.getList();
		}catch (Exception ex){
			throw new RuntimeException(ex);
		}
	}

}