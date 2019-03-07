package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Content;
import com.pinyougou.service.ContentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference(timeout = 10000)
    private ContentService contentService;

    @GetMapping("/findByPage")
    public PageResult findByPage(Content content, Integer page, Integer rows) {
        return contentService.findByPage(content, page, rows);
    }

    @PostMapping("/save")
    public boolean save(@RequestBody Content content) {
        try {
            contentService.save(content);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @PostMapping("update")
    public boolean update(@RequestBody Content content) {
        try {
            contentService.update(content);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @GetMapping("delete")
    public boolean delete(Long[] ids) {
        try {
            contentService.deleteAll(ids);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
