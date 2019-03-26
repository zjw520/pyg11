package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Address;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.service.AddressService;
import com.pinyougou.service.SmsService;
import com.pinyougou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/address")
public class AdressController {
    @Reference(timeout = 10000)
    private UserService userService;
    @Reference(timeout = 10000)
    private SmsService smsService;
    @Reference(timeout = 10000)
    private AddressService addressService;
    @Autowired
    private HttpServletRequest request;
    @GetMapping("/findAll")
    public Map<String,Object> findAll(){
        Map<String, Object> map = new HashMap<>();
        try {
            String userId = request.getRemoteUser();
            List<Provinces> provinceList = addressService.findProvinceList();
            List<Map<String,String>> addressList = addressService.findbyUserId(userId);
            map.put("provinceList",provinceList);
            map.put("addressList",addressList);
            return map;
        } catch (Exception e) {
            return null;
        }


    }
    @PostMapping("/addressDefaultStatus")
    public boolean addressDefaultStatus(@RequestBody Address address){

        try {
            addressService.updatestatus(address);
            return true;
        } catch (Exception e) {
            return false;
        }

    }
    @PostMapping("/saveAddress")
    public boolean saveOrUpdareAddress(@RequestBody Address address){
        if (address!=null){
            try {
                String remoteUser = request.getRemoteUser();
                address.setUserId(remoteUser);
                address.setIsDefault("0");
                return   addressService.saveOrUpdareAddress(address);
            } catch (Exception e) {
                return false;
            }
        }else{
            return false;
        }

    }
    @PostMapping("/updateAddress")
    public boolean update(@RequestBody Address address){
        try {
            addressService.update(address);
            return true;
        } catch (Exception e) {
            return false;
        }


    }
    @GetMapping("/delete")
    public boolean delete(Long id){
        try {
            addressService.delete(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
