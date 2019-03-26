package com.pinyougou.service;

import java.util.List;
import java.util.Map;

public interface TotalAddressService {


    List<Map<String,Object>> findTotalAddressById(String tb_areas, String areaid, String area, String id, String lastLevelId);
}
