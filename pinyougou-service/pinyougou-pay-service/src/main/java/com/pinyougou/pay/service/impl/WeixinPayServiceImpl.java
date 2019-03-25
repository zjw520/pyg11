package com.pinyougou.pay.service.impl;

import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Value("${unifiedorder}")
    private String unifiedorder;

    @Value("{orderquery}")
    private String orderquery;


    @Override
    public Map<String, String> genPayCode(String outTradeNo, String totalFee) {

        /** 创建Map集合封装请求参数 */
        Map<String, String> param = new HashMap<>();
        /** 公众号 */
        param.put("appid", appid);
        /** 商户号 */
        param.put("mch_id", partner);
        /** 随机字符串 */
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        /** 商品描述 */
        param.put("body", "品优购");
        /** 商户订单交易号 */
        param.put("out_trade_no", outTradeNo);
        /** 总金额（分） */
        param.put("total_fee", totalFee);
        /** IP地址 */
        param.put("spbill_create_ip", "127.0.0.1");
        /** 回调地址(随意写) */
        param.put("notify_url", "http://test.itcast.cn");
        /** 交易类型 */
        param.put("trade_type", "NATIVE");
        try {
            /** 根据商户密钥签名生成XML格式请求参数 */
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            /** 创建HttpClientUtils对象发送请求 */
            HttpClientUtils client = new HttpClientUtils(true);
            /** 发送请求，得到响应数据 */
            String result = client.sendPost(unifiedorder, xmlParam);
            /** 将响应数据XML格式转化成Map集合 */
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            /** 创建Map集合封装返回数据 */
            Map<String, String> data = new HashMap<>();
            /** 支付地址(二维码中的URL) */
            data.put("codeUrl", resultMap.get("code_url"));
            /** 总金额 */
            data.put("totalFee", totalFee);
            /** 订单交易号 */
            data.put("outTradeNo", outTradeNo);
            return data;
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    @Override
    public Map<String, String> queryPayStatus(String outTradeNo) {
        Map<String, String> param = new HashMap<>();
        param.put("appid", appid);
        param.put("mch_id", partner);
        param.put("out_trade_no", outTradeNo);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClientUtils httpClientUtils = new HttpClientUtils(true);
            String result = httpClientUtils.sendPost(orderquery, xmlParam);
            return WXPayUtil.xmlToMap(result);
        } catch (Exception ex) {
            throw new RuntimeException();
        }

    }
}
