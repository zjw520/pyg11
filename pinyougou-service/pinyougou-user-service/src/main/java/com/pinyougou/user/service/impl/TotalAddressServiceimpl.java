package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TotalAddressByIdMapper;
import com.pinyougou.service.TotalAddressService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service(interfaceName = "com.pinyougou.service.TotalAddressService")
public class TotalAddressServiceimpl implements TotalAddressService {
    @Autowired
    private TotalAddressByIdMapper totalAddressByIdMapper;

    @Override
    public List findTotalAddressById(String tableName, String kindsAreaId, String areaName, String id, String lastLevelCity) {

        List list = totalAddressByIdMapper.findTotalAddressById(tableName, kindsAreaId, areaName, id, lastLevelCity);

        return list;
    }
}