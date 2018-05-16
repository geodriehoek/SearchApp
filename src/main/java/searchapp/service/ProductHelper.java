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
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductHelper.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    public List<Product> searchResponseToList(SearchResponse response) {
        SearchHit[] searchHits = response.getHits().getHits();
        List<Product> resultList = new ArrayList<>();

        for (SearchHit hit : searchHits) {
//            ObjectMapper objectMapper = new ObjectMapper();
            try {
                resultList.add(
                        new Product(
                                objectMapper.readValue(
                                        hit.getSourceAsString(),
                                        Product.class),
                                hit.getScore()
                        )
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    public String searchResponseTo_Id(SearchResponse response){                                                         //TODO: NPE bij afwezige entry
        SearchHit[] searchHits = response.getHits().getHits();
        return searchHits[0].getId();
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

    public Product getResponseToProduct(GetResponse response){
        Product product = null;

        if(response.isExists()){
            try {
                product = new Product(objectMapper.readValue(response.getSourceAsString(), Product.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            LOGGER.error("------------");
            LOGGER.error("NO SUCH PRODUCT");
            LOGGER.error("------------");
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
//            LOGGER.error("wrong pagination direction");                                                                       //TODO: altijd hier
//        }
//
//        return new PaginationObject(from, size, pagination.getDirection());
//    }                                       //TODO: mag weg, overgezet naar klasse-methode
}
