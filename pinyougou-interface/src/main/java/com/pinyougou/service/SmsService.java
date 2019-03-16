package com.pinyougou.service;

public interface SmsService {

    /**
     * 发送短信方法
     * @param phone 手机号码
     * @param signName 签名
     * @param templateCode 短信模板
     * @param templateParam 模板参数(json格式)
     * @return
     */
    boolean sendSms(String phone, String signName, String templateCode, String templateParam);


}
