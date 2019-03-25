package com.pinyougou.cart.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {
    @GetMapping("/user/showName")
    public Map<String, String> showName(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        String username = request.getRemoteUser();
        map.put("loginName", username);
        return map;
    }
}
