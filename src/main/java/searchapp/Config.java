package searchapp;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.expression.Mvc;

@Configuration
public class Config {
    Logger log = LoggerFactory.getLogger(Config.class);

    @Bean
    public RestHighLevelClient client(){
        log.info("GETTING CLIENT");
        HttpHost http = new HttpHost("localhost", 9200, "http");
        log.info("URL: " + http.toString());
        return new RestHighLevelClient(RestClient.builder(http));
    }

    @Bean
    public Mvc mvc(){
        return new Mvc();
    }
}
