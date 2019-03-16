package com.pinyougou.search.listner;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.ItemSearchService;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.Arrays;

public class DeleteMessageListener implements SessionAwareMessageListener<ObjectMessage> {

    @Reference(timeout=30000)
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        Long[] ids = (Long[])objectMessage.getObject();
        itemSearchService.delete(Arrays.asList(ids));
    }
}
