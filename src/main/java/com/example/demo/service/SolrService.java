package com.example.demo.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
public class SolrService {

    @Autowired
    private SolrClient solrClient;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.data.solr.core}")
    private String solrCore;

    private static final String RESULT_TOPIC = "search_result_topic";

    public SolrService(@Value("${spring.data.solr.host}") String solrHost) {
        this.solrClient = new HttpSolrClient.Builder(solrHost).build();
    }
    // Apache Kafka(Que) 적용 실행 = solr select
    @KafkaListener(topics = "search_query_topic", groupId = "test-group")
    public void consumeSearchQuery(String query) {
        try {
            String decodedQuery = URLDecoder.decode(query, StandardCharsets.UTF_8.toString());
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(decodedQuery);
            solrQuery.setRows(100);
            QueryResponse response = solrClient.query(solrCore, solrQuery);
            String result = response.getResults().toString();
            kafkaTemplate.send(RESULT_TOPIC, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}