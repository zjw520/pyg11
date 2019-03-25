package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.pojo.Cart;
import com.pinyougou.service.CartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout = 10000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;


    @RequestMapping("/addCart")
    public boolean addCart(Long itemId, Integer num) {
        try {
            String username = request.getRemoteUser();

            List<Cart> carts = findCart();

            carts = cartService.addItemToCart(carts, itemId, num);

            if (StringUtils.isNoneBlank(username)) {
                cartService.saveCartRedis(username, carts);
            } else {
                CookieUtils.setCookie(request, response, CookieUtils.CookieName.PINYOUGOU_CART, JSON.toJSONString(carts), 3600 * 24, true);

            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @GetMapping("/findCart")
    public List<Cart> findCart() {

        String username = request.getRemoteUser();

        List<Cart> carts = null;

        if (StringUtils.isNoneBlank(username)) {
            carts = cartService.findCartsRedis(username);
            String cartStr = CookieUtils.getCookieValue(request, CookieUtils.CookieName.PINYOUGOU_CART, true);
            if (StringUtils.isNoneBlank(cartStr)) {
                List<Cart> cookieCarts = JSON.parseArray(cartStr, Cart.class);
                if (cookieCarts != null && cookieCarts.size() > 0) {
                    carts = cartService.mergeCart(cookieCarts, carts);
                    cartService.saveCartRedis(username, carts);
                    CookieUtils.deleteCookie(request, response, CookieUtils.CookieName.PINYOUGOU_CART);
                }
            }
        } else {
            String cartStr = CookieUtils.getCookieValue(request, CookieUtils.CookieName.PINYOUGOU_CART, true);
            if (StringUtils.isBlank(cartStr)) {
                cartStr = "[]";
            }
            carts = JSON.parseArray(cartStr, Cart.class);
        }


        return carts;
    }
}