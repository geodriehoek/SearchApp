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
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import searchapp.domain.customExceptions.RepositoryException;

import java.io.IOException;

@Repository
public class ProductRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductRepository.class);
    @Autowired
    private RestHighLevelClient client;

    public SearchResponse search(SearchRequest searchRequest) throws RepositoryException {
        try {
            return client.search(searchRequest);
//            throw new IOException("test forced exception");                                                             //TODO: forced exception for testing
        }catch(IOException ioe){
            throw new RepositoryException("unable to access database: apiSearch", ioe);
        }
    }

    public GetResponse getById(String id) throws RepositoryException{
        GetResponse getResponse;
        GetRequest getRequest = new GetRequest(
                "products",
                "product",
                id
        );

        try {
            getResponse = client.get(getRequest);
//            throw new IOException("force test");                                                                      //TODO: forced exception for testing
        } catch (IOException ioe) {
            throw new RepositoryException("unable to access database: getOne", ioe);
        }

        return getResponse;
    }

    //TODO: update/deleteById; verschillende slaaggevallen opvangen. wat met return info?
    public void update(String id, String jsonStringNewData) throws RepositoryException {
        UpdateResponse response;

        UpdateRequest request = new UpdateRequest(
                                        "products",
                                        "product",
                                        id
                                );

//        request.upsert(jsonStringNewData, XContentType.JSON).docAsUpsert(true);
        request.doc(jsonStringNewData, XContentType.JSON)
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        try {
            response = client.update(request);
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
        } catch(IOException ioe){
            throw new RepositoryException("unable to access database: update", ioe);
        }
    }

    public void delete(String id) throws RepositoryException {
        DeleteResponse response;

        DeleteRequest request = new DeleteRequest(
                                        "products",
                                        "product",
                                        id
                                )
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

        try {
            response = client.delete(request);
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
//            throw new IOException("test force exception");                                                              //TODO: force exception for testing
        } catch(IOException ioe){
            throw new RepositoryException("unable to access database: delete", ioe);
        }
    }

    public void index(String jsonProduct) throws RepositoryException {
        IndexResponse response;

        IndexRequest request = new IndexRequest(
                                            "products",
                                            "product"
                                    );
        request.source(jsonProduct, XContentType.JSON)
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

        try {
            response = client.index(request);
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
//            throw new IOException("force exception test");                                                              //TODO: forced exception for testing
        } catch(IOException ioe){
            throw new RepositoryException("unable to access database: index", ioe);
        }
    }

//    public void deleteAsync(String id) throws RepositoryException {
//        DeleteRequest request = new DeleteRequest(
//                                        "products",
//                                        "product",
//                                        id
//                                );
//
//        ActionListener<DeleteResponse> deleteListener = new ActionListener<DeleteResponse>() {
//            @Override
//            public void onResponse(DeleteResponse deleteResponse) {
//                LOGGER.debug("deleting async by id: " + id);
//                LOGGER.debug(deleteResponse.getResult().toString());
//                LOGGER.debug(deleteResponse.status().toString());
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                LOGGER.error("failed to delete by id: " + id);                                                          //TODO: werkt, zie comment | MAAR hoe throwen??
//            }
//        };
////        try {
////            client.close();
////        } catch (IOException e) {
////            throw new RepositoryException("unable to access database: delete");
////        }
//        client.deleteAsync(request, deleteListener);
//    }

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
