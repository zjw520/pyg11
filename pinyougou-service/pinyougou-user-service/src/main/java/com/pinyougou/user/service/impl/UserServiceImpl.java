package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.User;

import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * UserServiceImpl 服务接口实现类
 *
 * @version 1.0
 * @date 2019-02-27 16:23:07
 */

@Service(interfaceName = "com.pinyougou.service.UserService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${sms.url}")
    private String smsUrl;

    @Value("${sms.signName}")
    private String signName;

    @Value("${sms.templateCode}")
    private String templateCode;

    /**
     * 添加方法
     */
    public void save(User user) {
        try {
            user.setCreated(new Date());
            user.setUpdated(user.getCreated());
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            userMapper.insertSelective(user);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 修改方法
     */
    public void update(User user) {
        try {
            userMapper.updateByPrimaryKeySelective(user);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id删除
     */
    public void delete(Serializable id) {
        try {
            userMapper.deleteByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 批量删除
     */
    public void deleteAll(Serializable[] ids) {
        try {
            // 创建示范对象
            Example example = new Example(User.class);
            // 创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // 创建In条件
            criteria.andIn("id", Arrays.asList(ids));
            // 根据示范对象删除
            userMapper.deleteByExample(example);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 根据主键id查询
     */
    public User findOne(Serializable id) {
        try {
            return userMapper.selectByPrimaryKey(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 查询全部
     */
    public List<User> findAll() {
        try {
            return userMapper.selectAll();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 多条件分页查询
     */
    public List<User> findByPage(User user, int page, int rows) {
        try {
            PageInfo<User> pageInfo = PageHelper.startPage(page, rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            userMapper.selectAll();
                        }
                    });
            return pageInfo.getList();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean sendCode(String phone) {
        try {
            String code = UUID.randomUUID().toString().replaceAll("-", "").replaceAll("[a-z|A-Z]", "").substring(0, 6);
            HttpClientUtils httpClientUtils = new HttpClientUtils(false);
            Map<String, String> param = new HashMap<>();
            param.put("phone", phone);
            param.put("signName", signName);
            param.put("templateCode", templateCode);
            param.put("templateParam", "{\"code\":\"" + code + "\"}");
            String content = httpClientUtils.sendPost(smsUrl, param);

            Map<String, Object> resMap = JSON.parseObject(content, Map.class);
            redisTemplate.boundValueOps(phone).set(code, 90, TimeUnit.SECONDS);
            return (boolean) resMap.get("success");

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean checkSmsCode(String phone, String code) {
        String sysCode = redisTemplate.boundValueOps(phone).get();
        return StringUtils.isNoneBlank(sysCode) && sysCode.equals(code);
    }

    @Override
    public User findUser(String username) {
        User user = new User();
        user.setUsername(username);
        User user2 = userMapper.selectOne(user);
        return user2;
    }


}