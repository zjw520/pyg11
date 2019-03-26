package com.pinyougou.service;

import com.pinyougou.pojo.Address;
import com.pinyougou.pojo.Provinces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * AddressService 服务接口
 * @date 2019-02-27 16:23:07
 * @version 1.0
 */
public interface AddressService {

	/** 添加方法 */
	void save(Address address);

	/** 修改方法 */
	void updatestatus(Address address);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Address findOne(Serializable id);

	/** 查询全部 */
	List<Address> findAll();

	/** 多条件分页查询 */
	List<Address> findByPage(Address address, int page, int rows);

    List<Address> findAddressByUser(String username);

	List<Map<String,String>> findbyUserId(String userId);

    List<Provinces> findProvinceList();

	boolean saveOrUpdareAddress(Address address);

	boolean update(Address address);

}