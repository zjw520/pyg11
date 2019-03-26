package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.service.AreasService;
import com.pinyougou.service.CitiesService;
import com.pinyougou.service.ProvincesService;
import com.pinyougou.service.TotalAddressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TotalAdressController {
    @Reference(timeout = 10000)
    private TotalAddressService totalAddressService;

    @GetMapping("/total/address")
    public List<Map<String,Object>> findTotalAddressById(String id){

        List<Map<String,Object>> list = null;
        try {
            int[] arr = new int[id.length()];
            for (int i = 0; i < id.length(); i++) {
                arr[i] = Integer.parseInt(id.substring(i, i + 1));
            }
            if (arr[2]==0&&arr[3]==0&&arr[4]==0&&arr[5]==0){

                list = totalAddressService.findTotalAddressById("tb_cities","cityid","city",id,"provinceid");

            }else if (arr[0]!=0&&arr[1]!=0&&arr[4]==0&&arr[5]==0){

                list = totalAddressService.findTotalAddressById("tb_areas","areaid","area",id,"cityid");

            }
            return list;
        } catch (NumberFormatException e) {
            return null;
        }

    }
}
