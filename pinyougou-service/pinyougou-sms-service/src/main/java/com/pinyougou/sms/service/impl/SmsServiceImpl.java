package com.pinyougou.sms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.pinyougou.service.SmsService;
import org.springframework.beans.factory.annotation.Value;

@Service(interfaceName = "com.pinyougou.service.SmsService")
public class SmsServiceImpl implements SmsService {

    /**
     * 产品名称:云通信短信API产品,开发者无需替换
     */
    private static final String PRODUCT = "Dysmsapi";
    /**
     * 产品域名,开发者无需替换
     */
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";
    // 签名KEY
    @Value("${sms.accessKeyId}")
    private String accessKeyId;
    // 签名密钥
    @Value("${sms.accessKeySecret}")
    private String accessKeySecret;


    @Override
    public boolean sendSms(String phone, String signName, String templateCode, String templateParam) {
        try {
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                    accessKeyId, accessKeySecret);
            /** cn-hangzhou: 中国.杭州 */
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou",
                    PRODUCT, DOMAIN);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            /** 组装请求对象*/
            SendSmsRequest request = new SendSmsRequest();


            // 必填: 待发送手机号
            request.setPhoneNumbers(phone);
            // 必填: 短信签名-可在短信控制台中找到
            request.setSignName(signName);
            // 必填: 短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode);
            /**
             * 可选: 模板中的变量替换JSON串,
             * 如模板内容为"亲爱的${name},您的验证码为${code}"
             */
            request.setTemplateParam(templateParam);



            // 此处可能会抛出异常，注意catch
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            /** 判断短信是否发送成功 */
            return sendSmsResponse.getCode() != null &&
                    sendSmsResponse.getCode().equals("OK");

        } catch (Exception ex) {
            throw new RuntimeException("短信发送异常",ex);
        }
    }
}
