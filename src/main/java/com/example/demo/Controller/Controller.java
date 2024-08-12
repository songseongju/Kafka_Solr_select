package com.example.demo.Controller;

import com.example.demo.config.KafkaProducer;
import com.example.demo.domain.SearchRequest;
import com.example.demo.service.ResultConsumer;
import com.example.demo.service.SolrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/solr")
public class Controller {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private SolrService solrService;

    @Autowired
    private ResultConsumer resultConsumer;

    // Apache Kafka(Que) 적용 실행 = solr select
    @GetMapping("/search")
    public ResponseEntity<?> search (@RequestBody SearchRequest searchRequest){
        kafkaProducer.sendQuery(searchRequest.getQuery());
        return ResponseEntity.ok("컨슈머에게 던짐");
    }
}
