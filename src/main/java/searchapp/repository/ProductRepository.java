package searchapp.repository;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import searchapp.domain.customExceptions.RepositoryException;
import searchapp.domain.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductRepository.class);
    @Autowired
    private RestHighLevelClient client;

    public SearchResponse search(SearchRequest searchRequest){
        SearchResponse response = null;
        List<Product> resultList = new ArrayList<>();

        try{
            response = client.search(searchRequest);
        } catch (IOException e){
            LOGGER.error("-------------------------------");
            LOGGER.error("------@ProductRepo.search------");
            LOGGER.error(e.getMessage());
            LOGGER.error("-------------------------------");

        }

        return response;
    }

    public SearchResponse searchThrows(SearchRequest searchRequest) throws RepositoryException {
        try {
//            return client.search(searchRequest);
            throw new IOException("test exception forced");
        }catch(IOException ioe){
            throw new RepositoryException("unable to access database", ioe);
        }
    }

    public GetResponse getById(String id){
        GetResponse getResponse = null;
        GetRequest getRequest = new GetRequest(
                "products",
                "product",
                id
        );

        try {
            getResponse = client.get(getRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getResponse;
    }

    //TODO: update/deleteById; verschillende slaaggevallen opvangen. wat met return info?
    public void update(String id, String jsonStringNewData){
        UpdateResponse response = new UpdateResponse();

        UpdateRequest request = new UpdateRequest(
                "products",
                "product",
                id
        );

//        request.upsert(jsonStringNewData, XContentType.JSON).docAsUpsert(true);
        request.doc(jsonStringNewData, XContentType.JSON);
        try {
            response = client.update(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.getResult() == DocWriteResponse.Result.UPDATED){
            LOGGER.info("------------------");
            LOGGER.info("-------@Repo------");
            LOGGER.info("-PRODUCT UPDATED--");
            LOGGER.info(response.getId());
            LOGGER.info("------------------");
        }else{
            LOGGER.warn("------------------");
            LOGGER.warn("-------@Repo------");
            LOGGER.warn("PRODUCT NOT UPDATED");
            LOGGER.warn(response.getId());
            LOGGER.warn("------------------");
        }

    }

    public void delete(String id){
        DeleteResponse response = new DeleteResponse();
        DeleteRequest request = new DeleteRequest(
                "products",
                "product",
                id
        );
        try {
            response = client.delete(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response.status().getStatus());

        if (response.getResult() == DocWriteResponse.Result.DELETED){
            LOGGER.info("------------------");
            LOGGER.info("-------@Repo------");
            LOGGER.info("-PRODUCT DELETED--");
            LOGGER.info(response.getId());
            LOGGER.info("------------------");
        }else{

            LOGGER.warn("------------------");
            LOGGER.warn("-------@Repo------");
            LOGGER.warn("PRODUCT NOT DELETED");
            LOGGER.warn(response.getId());
            LOGGER.warn("------------------");
        }
    }

    public void index(String jsonProduct){
        IndexResponse response = new IndexResponse();
        IndexRequest request = new IndexRequest(
                                            "products",
                                            "product"
                                    );
        request.source(jsonProduct, XContentType.JSON);

        try {
            response = client.index(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(response.status().getStatus());

        if (response.getResult() == DocWriteResponse.Result.CREATED){
            LOGGER.info("------------------");
            LOGGER.info("-------@Repo------");
            LOGGER.info("-PRODUCT CREATED--");
            LOGGER.info(response.getId());
            LOGGER.info("------------------");
        }else{

            LOGGER.warn("-------------------");
            LOGGER.warn("-------@Repo-------");
            LOGGER.warn("PRODUCT NOT CREATED");
            LOGGER.warn(response.getId());
            LOGGER.warn("-------------------");
        }
    }

//    @PreDestroy
//    public void cleanUp(){
//        try {
//            System.out.println("-------------------------------");
//            System.out.println("--------CLOSING CLIENT---------");
//            System.out.println("-------------------------------");
//            client.close();
//        } catch (IOException e) {
//            System.out.println("-------------------------------");
//            System.out.println("------@ProductRepo.cleanUp-----");
//            System.out.println("-------------------------------");
//            e.printStackTrace();
//        }
//    }
}
