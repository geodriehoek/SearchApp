package searchapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Service;
import searchapp.domain.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductHelper {
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
            System.out.println("------------");
            System.out.println("NO SUCH PRODUCT");
            System.out.println("------------");
        }
        return product;
    }

    public List<Product> getHitsFromScroll(SearchResponse response){                                                  //TODO: sowieso identiek als hierboven?
        return null;
    }

    public String getScrollIdFromResponse(SearchResponse response){
        return response.getScrollId();
    }
}
