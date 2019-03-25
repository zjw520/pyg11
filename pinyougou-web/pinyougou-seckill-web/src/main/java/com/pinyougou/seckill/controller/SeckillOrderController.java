package com.pinyougou.seckill.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class SeckillOrderController {

    @Reference(timeout = 10000)
    private SeckillOrderService seckillOrderService;

    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;


    @GetMapping("/submitOrder")
    public boolean submitOrder(HttpServletRequest request, Long id) {
        try {
            String username = request.getRemoteUser();
            seckillOrderService.saveOrderFromRedis(id, username);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @GetMapping("/genPayCode")
    public Map<String, String> genPayCode(HttpServletRequest request) {
        String userId = request.getRemoteUser();


        SeckillOrder seckillOrder = seckillOrderService.findSeckillOrderFromRedis(userId);

        long money = (long) (seckillOrder.getMoney().doubleValue() * 100);

        return weixinPayService.genPayCode(
                seckillOrder.getId().toString(),
                String.valueOf(money));

    }

    @GetMapping("/queryPayStatus")
    public Map<String, Integer> queryPayStatus(String outTradeNo,HttpServletRequest request) {
        Map<String, Integer> data = new HashMap<>();
        data.put("status", 3);
        try {
            Map<String, String> map = weixinPayService.queryPayStatus(outTradeNo);
            if (map != null && map.size() > 0) {
                if ("SUCCESS".equals(map.get("trade_state"))) {

                    String userId = request.getRemoteUser();
                    seckillOrderService.saveOrder(userId, map.get("transaction_id"));
                    data.put("status", 1);
                }
                if ("NOTPAY".equals(map.get("trade_state"))) {
                    data.put("status", 2);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }
}
