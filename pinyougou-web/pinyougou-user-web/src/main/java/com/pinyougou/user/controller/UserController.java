package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Address;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.pojo.User;
import com.pinyougou.service.AddressService;
import com.pinyougou.service.SmsService;
import com.pinyougou.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(timeout = 10000)
    private UserService userService;

    @Reference(timeout = 10000)
    private SmsService smsService;
    @Reference(timeout = 10000)
    private AddressService addressService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping("/save")
    private boolean save(@RequestBody User user, String smsCode) {
        try {
            boolean ok = userService.checkSmsCode(user.getPhone(), smsCode);
            if (ok) {
                userService.save(user);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @GetMapping("/sendCode")
    private boolean sendCode(String phone) {
        try {
            if (StringUtils.isNoneBlank(phone)) {
                userService.sendCode(phone);
                return true;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @GetMapping("/showName")
    public Map<String, String> showName() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, String> data = new HashMap<>();
        data.put("loginName", name);
        return data;
    }
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
}
