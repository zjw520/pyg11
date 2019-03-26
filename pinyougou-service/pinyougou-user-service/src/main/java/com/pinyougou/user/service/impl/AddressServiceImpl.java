package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.service.*;

import java.util.List;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * AddressServiceImpl 服务接口实现类
 *
 * @version 1.0
 * @date 2019-02-27 16:23:07
 */
@Service(interfaceName = "com.pinyougou.service.AddressService")
@Transactional
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private TotalAddressByIdMapper totalAddressByIdMapper;
    @Autowired
    private ProvincesMapper provincesMapper;
     /**
     * 添加方法
     */
    public void save(Address address) {
        try {
            addressMapper.insertSelective(address);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean update(Address address) {

        try {
            addressMapper.updateByPrimaryKeySelective(address);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 修改方法
     */
    public void updatestatus(Address address) {
        try {
            String userId = address.getUserId();
            Address address1 = new Address();
            address1.setUserId(userId);
            List<Address> findAddressById = addressMapper.select(address1);
                for (Address address2 : findAddressById) {
                    if (address2.getIsDefault().equals("1")) {
                        address2.setIsDefault("0");
                        addressMapper.updateByPrimaryKeySelective(address2);
                    }
            }
            address.setIsDefault("1");
            addressMapper.updateByPrimaryKeySelective(address);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }



    /**
     * 根据主键id删除
     */
    public void delete(Serializable id) {
        try {
            addressMapper.deleteByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 批量删除
     */
    public void deleteAll(Serializable[] ids) {
        try {
            // 创建示范对象
            Example example = new Example(Address.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // 创建In条件
            criteria.andIn("id", Arrays.asList(ids));
            // 根据示范对象删除
            addressMapper.deleteByExample(example);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }



    /**
     * 根据主键id查询
     */
    public Address findOne(Serializable id) {
        try {
            return addressMapper.selectByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 查询全部
     */
    public List<Address> findAll() {
        try {
            return addressMapper.selectAll();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 多条件分页查询
     */
    public List<Address> findByPage(Address address, int page, int rows) {
        try {
            PageInfo<Address> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            addressMapper.selectAll();
                        }
                    });
            return pageInfo.getList();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Address> findAddressByUser(String username) {
        try {
            Address address = new Address();
            address.setUserId(username);
            return addressMapper.select(address);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    @Override
    public  List<Map<String,String>> findbyUserId(String userId) {
        try {
            List<Map<String,String>>  addressList = addressMapper.findbyUserId(userId);
            for (Map<String, String> map : addressList) {
                Provinces provinces = new Provinces();
                provinces.setProvinceId((map.get("province_id")));
                Provinces provinces1 = provincesMapper.selectOne(provinces);
                map.put("province",provinces1.getProvince());
            }


            return  addressList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Provinces> findProvinceList() {
        List<Provinces> provinces = provincesMapper.selectAll();
        return provinces;

    }

    @Override
    public boolean saveOrUpdareAddress(Address address) {
        try {
            addressMapper.insertSelective(address);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


}