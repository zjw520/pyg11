package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer rows, Integer page) {

        goods.setAuditStatus("0");
        if (StringUtils.isNoneBlank(goods.getGoodsName())) {
            try {
                goods.setGoodsName(new String(goods
                        .getGoodsName().getBytes("ISO8859-1"), "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return goodsService.findByPage(goods, page, rows);
    }

    @PostMapping("updateStatus")
    public boolean updateStatus(Long[] ids,String status){
        try{
            goodsService.updateAll(ids,status);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try{
            goodsService.deleteAll(ids);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
