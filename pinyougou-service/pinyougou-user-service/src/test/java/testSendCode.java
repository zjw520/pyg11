import com.alibaba.fastjson.JSON;
import com.pinyougou.common.util.HttpClientUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-service.xml")
public class testSendCode {

    @Autowired
    RedisTemplate redisTemplate;


    @Test
    public void sendCode() {
        try {
            String code = UUID.randomUUID().toString().replaceAll("-", "").replaceAll("[a-z|A-Z]", "").substring(0, 6);
            HttpClientUtils httpClientUtils = new HttpClientUtils(false);
            Map<String, String> param = new HashMap<>();
            param.put("phone", "18502001280");
            param.put("signName", "ysak");
            param.put("templateCode", "SMS_160577020");
            param.put("templateParam", "{\"code\":\"" + code + "\"}");
            String content = httpClientUtils.sendPost("http://sms.pinyougou.com/sms/sendSms", param);

            Map<String, Object> resMap = JSON.parseObject(content, Map.class);
            redisTemplate.boundValueOps("18502001280").set(code, 90, TimeUnit.SECONDS);
            Object success = resMap.get("success");
            System.out.println(success);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
