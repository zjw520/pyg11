package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Specification;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.service.SpecificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;


import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    @PostMapping("/update")
    public boolean update(@RequestBody Specification specification) {
        try {
            specificationService.update(specification);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @GetMapping("/delete")
    public boolean delete(Long[] ids) {
        try {
            specificationService.deleteAll(ids);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @PostMapping("/save")
    public boolean save(@RequestBody Specification specification) {
        try {
            specificationService.save(specification);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @GetMapping("/findByPage")
    public PageResult findByPage(Specification specification, Integer page, Integer rows) {
        if (specification != null && StringUtils.isNoneBlank(specification.getSpecName())) {
            try {
                specification.setSpecName(new String(specification.getSpecName().getBytes("ISO8859-1"), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return specificationService.findByPage(specification, page, rows);
    }

    @GetMapping("/findSpecOption")
    public List<SpecificationOption> findSpecOption(Long id) {
        return specificationService.findSpecOption(id);
    }
}
