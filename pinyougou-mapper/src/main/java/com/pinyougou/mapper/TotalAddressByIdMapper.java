package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.omg.CORBA.OBJECT_NOT_EXIST;

import java.util.List;
import java.util.Map;

public interface TotalAddressByIdMapper  extends Mapper{

    @Select("select ${kindsAreaId},${areaName} from ${tableName} where ${lastLevelCity} = #{id}")
    List<Map<String,Object>> findTotalAddressById(@Param("tableName") String tableName,@Param("kindsAreaId") String kindsAreaId,@Param("areaName") String areaName, @Param("id")String id,@Param("lastLevelCity") String lastLevelCity);
}
