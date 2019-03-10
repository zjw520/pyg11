package com.pinyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.solr.SolrItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtils {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ItemMapper itemMapper;

    public void importItemData() {
        Item item = new Item();
        item.setStatus("1");
        List<Item> itemList = itemMapper.select(item);
        System.out.println("=====商品列表=====");
        List<SolrItem> solrItems = new ArrayList<>();
        for (Item item1 : itemList) {
            SolrItem solrItem = new SolrItem();
            solrItem.setId(item1.getId());
            solrItem.setBrand(item1.getBrand());
            solrItem.setCategory(item1.getCategory());
            solrItem.setGoodsId(item1.getGoodsId());
            solrItem.setImage(item1.getImage());
            solrItem.setPrice(item1.getPrice());
            solrItem.setSeller(item1.getSeller());
            solrItem.setTitle(item1.getTitle());
            solrItem.setUpdateTime(item1.getUpdateTime());

            Map specMap = JSON.parseObject(item1.getSpec(), Map.class);
            solrItem.setSpecMap(specMap);

            solrItems.add(solrItem);
        }

        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItems);
        if (updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        } else {
            solrTemplate.rollback();
        }
        System.out.println("====结束=====");
    }

    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        SolrUtils solrUtils = ac.getBean(SolrUtils.class);
        solrUtils.importItemData();
    }


}
