package com.pinyougou.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/showLoginName")
    public Map<String, String> showLoginName() {
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, String> data = new HashMap<>();
        data.put("loginName", loginName);
        return data;
    }

    @RequestMapping("/login")
    public String login(String username, String password, String code,
                        HttpServletRequest request, HttpServletResponse response){
        System.out.println("username:" + username);
        System.out.println("password:" + password);
        System.out.println("code:" + code);
        // 判断验证码
        String oldCode = (String)request.getSession().getAttribute(VerifyController.VERIFY_CODE);
        if (code.equalsIgnoreCase(oldCode)){
            // 创建用户名与密码对象
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(username, password);
            // 登录认证(Spring Security)
            Authentication authenticate = authenticationManager.authenticate(token);
            // 判断认证是否成功
            if (authenticate.isAuthenticated()){
                // 认证成功
                SecurityContextHolder.getContext()
                        .setAuthentication(authenticate);
                return "redirect:/admin/index.html";
            }
        }
        return  "redirect:/shoplogin.html";
    }

}
