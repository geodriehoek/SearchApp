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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductHelper {
    Logger log = LoggerFactory.getLogger(ProductHelper.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    public List<Product> searchResponseToList(SearchResponse searchResponse) {
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        List<Product> resultList = new ArrayList<>();

        for (SearchHit hit : searchHits) {
//            ObjectMapper objectMapper = new ObjectMapper();
            try {
                resultList.add(
                        new Product(
                                objectMapper.readValue(
                                        hit.getSourceAsString(),
                                        Product.class),
                                hit.getScore(),
                                hit.getId()
                        )
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    public String productToJson(Product product) {
        String jsonString = null;

        try {
            jsonString = objectMapper.writeValueAsString(product);
//            jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(product);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(jsonString);

        return jsonString;
    }

    public Product getResponseToProduct(GetResponse getResponse){
        Product product = null;

        if(getResponse.isExists()){
            try {
                product = new Product(objectMapper.readValue(getResponse.getSourceAsString(), Product.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            log.error("------------");
            log.error("NO SUCH PRODUCT");
            log.error("------------");
        }
        return product;
    }

//    public PaginationObject interpretPagination(PaginationObject pagination){
//        int from = pagination.getFrom();
//        int size = pagination.getSize();
//
//        if(pagination.getDirection() == PaginationDirection.FORWARD){                                                   //TODO: error-bounds
//            from = from + size;
//        }else if(pagination.getDirection() == PaginationDirection.BACK){
//            from = from - size;
//        }else{
//            log.error("wrong pagination direction");                                                                       //TODO: altijd hier
//        }
//
//        return new PaginationObject(from, size, pagination.getDirection());
//    }                                       //TODO: mag weg, overgezet naar klasse-methode
}
