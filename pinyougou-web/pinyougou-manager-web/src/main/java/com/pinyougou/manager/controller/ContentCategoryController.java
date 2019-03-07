package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.ContentCategory;
import com.pinyougou.service.ContentCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {

    @Reference(timeout = 10000)
    private ContentCategoryService contentCategoryService;

    @GetMapping("/findByPage")
    public PageResult findByPage(ContentCategory contentCategory, Integer page, Integer rows) {
        return contentCategoryService.findByPage(contentCategory, page, rows);
    }

    @GetMapping("findAll")
    public List<ContentCategory> findAll() {
        return contentCategoryService.findAll();
    }

    @PostMapping("save")
    public boolean save(@RequestBody ContentCategory contentCategory) {
        try {
            contentCategoryService.save(contentCategory);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @PostMapping("update")
    public boolean update(@RequestBody ContentCategory contentCategory) {
        try {
            contentCategoryService.update(contentCategory);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @GetMapping("delete")
    public boolean delete(Long[] ids) {
        try {
            contentCategoryService.deleteAll(ids);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

}
