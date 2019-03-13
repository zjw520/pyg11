package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.Map;


@Service(interfaceName = "com.pinyougou.service.ItemSearchService")
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map<String, Object> params) {
        Map<String, Object> data = new HashMap<>();
        String keywords = (String) params.get("keywords");

        Integer page = (Integer) params.get("page");
        if (page == null) {
            page = 1;
        }

        Integer rows = (Integer) params.get("rows");
        if (rows == null) {
            rows = 20;
        }


        if (StringUtils.isNoneBlank(keywords)) {
            HighlightQuery highlightQuery = new SimpleHighlightQuery();
            HighlightOptions highlightOptions = new HighlightOptions();
            highlightOptions.addField("title");
            highlightOptions.setSimplePrefix("<font color='red'>");
            highlightOptions.setSimplePostfix("</font>");
            highlightQuery.setHighlightOptions(highlightOptions);


            Criteria criteria = new Criteria("keywords").is(keywords);
            highlightQuery.addCriteria(criteria);


            if (!"".equals(params.get("category"))) {
                Criteria criteria1 = new Criteria("category");
                criteria.is(params.get("category"));
                highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
            }

            if (!"".equals(params.get("brand"))) {
                Criteria criteria1 = new Criteria("brand");
                criteria.is(params.get("brand"));
                highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
            }

            if (params.get("spec") != null) {
                Map<String, String> specMap = (Map) params.get("spec");
                for (String key : specMap.keySet()) {
                    Criteria criteria1 = new Criteria("spec_" + key).is(specMap.get(key));
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }
                Criteria criteria1 = new Criteria("brand");
                criteria.is(params.get("brand"));
                highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
            }

            if (!"".equals(params.get("price"))) {
                String[] price = params.get("price").toString().split("-");
                if (!price[0].equals("0")) {
                    Criteria criteria1 = new Criteria("price").greaterThanEqual(price[0]);
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }
                if (!price[1].equals("*")) {
                    Criteria criteria1 = new Criteria("price").lessThanEqual(price[1]);
                    highlightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
                }
            }

            String sortField = (String) params.get("sortField");
            String sortValue = (String) params.get("sortValue");
            if (StringUtils.isNoneBlank(sortValue) && StringUtils.isNoneBlank(sortField)) {
                Sort sort = new Sort("ASC".equalsIgnoreCase(sortValue)? Sort.Direction.ASC:Sort.Direction.DESC,sortField);
                highlightQuery.addSort(sort);
            }

            highlightQuery.setOffset((page - 1) * rows);
            highlightQuery.setRows(rows);


            HighlightPage<SolrItem> highlightPage = solrTemplate.queryForHighlightPage(highlightQuery, SolrItem.class);


            for (HighlightEntry<SolrItem> highlightEntry : highlightPage.getHighlighted()) {
                SolrItem solrItem = highlightEntry.getEntity();
                if (highlightEntry.getHighlights().size() > 0 && highlightEntry.getHighlights().get(0).getSnipplets().size() > 0) {
                    solrItem.setTitle(highlightEntry.getHighlights().get(0).getSnipplets().get(0));
                }
            }
            data.put("rows", highlightPage.getContent());
            data.put("totalPages", highlightPage.getTotalPages());
            data.put("total", highlightPage.getTotalElements());
        } else {
            SimpleQuery simpleQuery = new SimpleQuery("*:*");
            simpleQuery.setOffset((page - 1) * rows);
            simpleQuery.setRows(rows);
            ScoredPage<SolrItem> scoredPage = solrTemplate.queryForPage(simpleQuery, SolrItem.class);
            data.put("rows", scoredPage.getContent());
            data.put("totalPages", scoredPage.getTotalPages());
            data.put("total", scoredPage.getTotalElements());
        }
        return data;
    }
}
