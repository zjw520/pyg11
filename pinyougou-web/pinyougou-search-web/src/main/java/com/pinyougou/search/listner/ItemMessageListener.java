package com.pinyougou.search.listner;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.Item;
import com.pinyougou.service.GoodsService;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemMessageListener implements SessionAwareMessageListener<ObjectMessage> {

    @Reference(timeout = 30000)
    private GoodsService goodsService;

    @Reference(timeout = 30000)
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        Long[] ids = (Long[]) objectMessage.getObject();
        List<Item> itemList = goodsService.findItemByGoodsId(ids);
        if (itemList.size() > 0) {
            List<SolrItem> solrItems = new ArrayList<>();
            for (Item item : itemList) {
                SolrItem solrItem = new SolrItem();
                solrItem.setTitle(item.getTitle());
                solrItem.setUpdateTime(item.getUpdateTime());
                solrItem.setSeller(item.getSeller());
                solrItem.setPrice(item.getPrice());
                solrItem.setImage(item.getImage());
                solrItem.setId(item.getId());
                solrItem.setGoodsId(item.getGoodsId());
                solrItem.setCategory(item.getCategory());
                solrItem.setBrand(item.getBrand());
                solrItem.setSpecMap(JSON.parseObject(item.getSpec(), Map.class));
                solrItems.add(solrItem);
            }
            itemSearchService.saveOrUpdate(solrItems);
        }
    }
}
