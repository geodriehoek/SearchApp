package searchapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import searchapp.domain.Product;
import searchapp.domain.customExceptions.ObjectMapperException;
import searchapp.domain.customExceptions.ProductNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductHelper.class);
    private ObjectMapper objectMapper = new ObjectMapper();

//    public List<Product> searchResponseToList(SearchResponse response){
//        SearchHit[] searchHits = response.getHits().getHits();
//        List<Product> resultList = new ArrayList<>();
//
//        for (SearchHit hit : searchHits) {
//            try {
//                resultList.add(
//                        new Product(
//                                objectMapper.readValue(
//                                        hit.getSourceAsString(),
//                                        Product.class),
//                                hit.getScore()
//                        )
//                );
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return resultList;
//    }

    public List<Product> searchResponseToList(SearchResponse response) throws ObjectMapperException {
        SearchHit[] searchHits = response.getHits().getHits();
        List<Product> resultList = new ArrayList<>();

        for (SearchHit hit : searchHits) {
            try {
                resultList.add(
                        new Product(
                                objectMapper.readValue(
                                        hit.getSourceAsString(),
                                        Product.class),
                                hit.getScore()
                        )
                );
//                throw new IOException("test: force exception");                                                         //TODO: forced exception for testing
            } catch (IOException e) {
                throw new ObjectMapperException("failed mapping to Object", e);
            }
        }
        return resultList;
    }

    public String productToJson(Product product) throws ObjectMapperException {
        String jsonString = null;

        try {
            jsonString = objectMapper.writeValueAsString(product);
//            jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(product);
        } catch (JsonProcessingException e) {
            throw new ObjectMapperException("failed mapping to Json", e);
        }
        System.out.println(jsonString);

        return jsonString;
    }

    public String searchResponseToId(SearchResponse response) throws ProductNotFoundException {                                                          //TODO: potentiële NPE/betere oplossing?
        SearchHit[] searchHits = response.getHits().getHits();
        String id = null;
        if (searchHits.length != 0){
            id = searchHits[0].getId();
        } else {
            throw new ProductNotFoundException("No Product found with given id");
        }
        return id;
    }

    public Product getResponseToProduct(GetResponse response) throws ProductNotFoundException {
        Product product = null;

        if(response.isExists()){
            try {
                product = new Product(objectMapper.readValue(response.getSourceAsString(), Product.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            throw new ProductNotFoundException("No Product found by given id");                                         //TODO: hier sowieso al te laat?
        }
        return product;
    }
}
