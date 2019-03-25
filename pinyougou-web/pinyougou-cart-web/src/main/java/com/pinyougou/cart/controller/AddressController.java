package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Address;
import com.pinyougou.service.AddressService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Reference(timeout = 10000)
    private AddressService addressService;

    @RequestMapping("/findAddressByUser")
    public List<Address> findAddressByUser(HttpServletRequest request){
        String username = request.getRemoteUser();
        return addressService.findAddressByUser(username);
    }
}
