package com.pinyougou.cart.controller;

import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WeixinPayService weixinPayService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping("/save")
    public boolean save(@RequestBody Order order, HttpServletRequest request) {
        try {
            String userId = request.getRemoteUser();
            order.setUserId(userId);
            order.setSourceType("2");
            orderService.save(order);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @GetMapping("/genPayCode")
    public Map<String, String> genPayCode() {
        String userId = request.getRemoteUser();
        PayLog payLog = orderService.findPayLogFromRedis(userId);
        return weixinPayService.genPayCode(payLog.getOutTradeNo(), String.valueOf(payLog.getTotalFee()));
    }

    @GetMapping("/queryPayStatus")
    public Map<String, Integer> queryPayStatus(String outTradeNo) {
        Map<String, Integer> data = new HashMap<>();
        data.put("status", 3);
        try {
            Map<String, String> resMap = weixinPayService.queryPayStatus(outTradeNo);
            if (resMap != null && resMap.size() > 0) {
                if ("SUCCESS".equals(resMap.get("trade_state"))) {
                    orderService.updateOrderStatus(outTradeNo,resMap.get("transaction_id"));
                    data.put("status", 1);
                }
                if ("NOTPAY".equals(resMap.get("trade_state"))) {
                    data.put("status", 2);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }
}
