package searchapp.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import searchapp.domain.web.CustomerRatingOptions;
import searchapp.domain.web.SearchSortOption;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProductQueryBuilder {
    private String[] allFieldsList = {"brandName", "productName", "customerRating", "price", "grp_id", "quantitySold", "upc"};
    private String[] textFieldsList = {"brandName", "productName", "grp_id", "upc"};
    private Map<String, Float> textFieldsListWithWeights = new HashMap<>();
    private String[] numberFieldsList = {"customerRating", "price", "quantitySold"};
    private String[] termsFieldsList = {"brandName", "grp_id", "upc"};

    {
        textFieldsListWithWeights.put("brandName", 1.5f);
        textFieldsListWithWeights.put("productName", 1.5f);      //productName minste weight aangezien enigste analyzed-field?
        textFieldsListWithWeights.put("grp_id", 2.0f);
        textFieldsListWithWeights.put("upc", 2.0f);
    }

    public SearchRequest buildMultiFieldQueryWithPagination(String stringToSearch,
                                                            CustomerRatingOptions ratingFilter,
                                                            long minQuantitySold,
                                                            SearchSortOption sortOption,
                                                            int from,
                                                            int size){
        SearchRequest request = new SearchRequest("products");
        request.types("product");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(
                QueryBuilders
                        .boolQuery()
                        //QUERY'S
                        .should(
                                QueryBuilders.matchPhraseQuery("productName", stringToSearch)
                                        .slop(2)
                        )
                        .should(
                                QueryBuilders.multiMatchQuery(stringToSearch)
                                        .fields(textFieldsListWithWeights)
                                        .fuzziness(1)                                           //fuzzi zou niet mogen bij cijfers/minder waarde
                        )
                        //voor apiSearch in upc12
                        .should(
                                QueryBuilders.prefixQuery(
                                        "upc12",
                                        stringToSearch
                                )
                                        .boost(20)                                              //zoniet is fuzzi-match in grp_id sterker
                        )
                        .minimumShouldMatch(1)
                        //FILTERS
                        .must(
                                QueryBuilders.rangeQuery("customerRating")
                                        .gte(ratingFilter.getValue())
                        )
                        .must(
                                QueryBuilders.rangeQuery("quantitySold")
                                        .gte(minQuantitySold)
                        )
        )
                .from(from)
                .size(size)
                .sort(sortOption.getValue(), SortOrder.DESC)
        ;

        return request.source(searchSourceBuilder);
    }

    public SearchRequest buildMultiFieldQueryForAPI(String stringToSearch,
                                                    CustomerRatingOptions ratingFilter,
                                                    long minQuantitySold,
                                                    SearchSortOption sortOption){

        SearchRequest searchRequest = new SearchRequest("products");
        searchRequest.types("product");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(
                QueryBuilders
                    .boolQuery()
                        //QUERY'S
                        .should(
                                QueryBuilders.matchPhraseQuery("productName", stringToSearch)
                                        .slop(2)
                        )
                        .should(
                                QueryBuilders.multiMatchQuery(stringToSearch)
                                .fields(textFieldsListWithWeights)
                                .fuzziness(1)                                           //fuzzi zou niet mogen bij cijfers/minder waarde
                        )
                        .should(
                                QueryBuilders.termQuery(
                                        "brandName",
                                        stringToSearch
                                )
                                        .boost(2)
                        )
                        //voor apiSearch in upc12
                        .should(
                                QueryBuilders.prefixQuery(
                                        "upc12",
                                        stringToSearch
                                )
                                .boost(20)                                              //zoniet is fuzzi-match in grp_id sterker
                        )
                        .minimumShouldMatch(1)
                        //FILTERS
                        .must(
                               QueryBuilders.rangeQuery("customerRating")
                               .gte(ratingFilter.getValue())
                        )
                        .must(
                               QueryBuilders.rangeQuery("quantitySold")
                               .gte(minQuantitySold)
                        )
        )
        .size(1000)
        .sort(sortOption.getValue(), SortOrder.DESC);

        return searchRequest.source(searchSourceBuilder);
    }                                         //TODO: dedicated RestAPI method => hoe best aanpakken?

    public SearchRequest buildSearchByGrpId(String grpId){

        SearchRequest searchRequest = new SearchRequest("products");
        searchRequest.types("product");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(
                QueryBuilders.matchQuery(
                        "grp_id",
                        grpId
                )
        );

        return searchRequest.source(searchSourceBuilder);
    }

    public SearchRequest buildMatchAllQuery(){
        SearchRequest searchRequest = new SearchRequest("products");
        searchRequest.types("product");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(
                QueryBuilders
                        .matchAllQuery()
                );
        return searchRequest.source(searchSourceBuilder);
    }

    public SearchRequest buildSearchByUpc12(String upc12){

        SearchRequest searchRequest = new SearchRequest("products");
        searchRequest.types("product");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(
                QueryBuilders.matchQuery(
                        "upc12",
                        upc12
                )
        );

        return searchRequest.source(searchSourceBuilder);
    }
}
