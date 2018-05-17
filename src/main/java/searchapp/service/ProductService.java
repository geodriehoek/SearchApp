package searchapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchapp.domain.customExceptions.NullSearchException;
import searchapp.domain.customExceptions.ObjectMapperException;
import searchapp.domain.Product;
import searchapp.domain.customExceptions.SearchAppException;
import searchapp.domain.web.CustomerRatingOptions;
import searchapp.domain.web.SearchForm;
import searchapp.domain.web.SearchSortOption;
import searchapp.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    @Autowired
    private ProductQueryBuilder productQueryBuilder;
    @Autowired
    private ProductRepository repo;
    @Autowired
    private ProductHelper helper;

    public List<Product> searchFromSearchForm(SearchForm searchForm){                                                   //TODO: goe design-principe om specifieke input te routen naar algemenere methods
        return search(
                searchForm.getInput(),
                searchForm.getRating(),
                searchForm.getMinQuantitySold(),
                searchForm.getSortOption()
        );
    }

    public List<Product> search(String stringToSearch, CustomerRatingOptions ratingFilter, long minQuantitySold, SearchSortOption sortOption){
        LOGGER.info("searching: " + stringToSearch);
        return helper.searchResponseToList(
                    repo.search(
                            productQueryBuilder.buildMultiFieldQuery(stringToSearch, ratingFilter, minQuantitySold, sortOption)
                    )
                );
    }           //TODO: behouden voor RESTapi, of alles weg ifv ...WithPagination

    public List<Product> simpleSearch(String stringToSearch){
        return helper.searchResponseToList(
                    repo.search(
                            productQueryBuilder.buildMultiFieldQuery(stringToSearch, CustomerRatingOptions.ONE, 0, SearchSortOption.RELEVANCE)          //TODO: goe design om te hardcoden?
                    )
                );
    }

    public List<Product> searchWithPagination(String stringToSearch, CustomerRatingOptions ratingFilter, long minQuantitySold, SearchSortOption sortOption, PaginationObject paginationObject){
        return helper.searchResponseToList(
                    repo.search(
                            productQueryBuilder.buildMultiFieldQueryWithPagination(
                                    stringToSearch,
                                    ratingFilter,
                                    minQuantitySold,
                                    sortOption,
                                    paginationObject.getFrom(),
                                    paginationObject.getSize()
                            )
                    )
        );
    }

    public List<Product> searchWithPaginationThrows(String stringToSearch,
                                                    CustomerRatingOptions ratingFilter,
                                                    long minQuantitySold,
                                                    SearchSortOption sortOption,
                                                    PaginationObject paginationObject) throws SearchAppException {
        return helper.searchResponseToListThrows(
                    repo.searchThrows(
                            productQueryBuilder.buildMultiFieldQueryWithPaginationThrows(
                                    stringToSearch,
                                    ratingFilter,
                                    minQuantitySold,
                                    sortOption,
                                    paginationObject.getFrom(),
                                    paginationObject.getSize()
                            )
                    )
        );
    }

    public void add(Product newProduct) {
        repo.index(
                helper.productToJson(newProduct)
        );
    }

    public Product getOneById(String id){
        return helper.getResponseToProduct(
                repo.getById(
                        id
                )
        );
    }

    public Product getOneByGrpId(String grpId){
        return getOneById(
                getIdByGrpId(
                        grpId
                )
        );
    }

    public void updateById(String id, Product newProductData){
        repo.update(
                id,
                helper.productToJson(newProductData)
        );
    }

    public void updateByGrpId(String grpId, Product newProductData){
        updateById(
                getIdByGrpId(grpId),
                newProductData
        );
    }

    public void deleteById(String id){
        repo.delete(id);
    }

    public void deleteByGrpId(String grpId){
        deleteById(
                getIdByGrpId(grpId)
        );
    }

    public String getIdByGrpId(String grpId){
        return helper.searchResponseToId(
                repo.search(
                        productQueryBuilder.buildSearchByGrpId(
                                grpId
                        )
                )
        );
    }

    public List<Product> matchAll(){
        return helper.searchResponseToList(
                    repo.search(
                            productQueryBuilder.buildMatchAllQuery()
                    )
        );
    }


//    public void deleteByUpc12(String upc12){
//        repo.delete(
//                getIdByUpc12(upc12)
//        );
//    }
//
//    public Product searchByUpc12(String upc12){
//        return getOneById(
//                    getIdByUpc12(upc12)
//            );
//    }
//
//    public String getIdByUpc12(String upc12){                                                                           //enkel hier correct aangezien upc12 uniek is
//        return helper.searchResponseToList(
//                    repo.search(
//                        productQueryBuilder.buildSearchByUpc12(
//                                upc12
//                        )
//                    )
//                ).get(0)
//                 .getId();
//    }                                                                       // TODO: NPE indien upc12 (nog) niet geregistreerd
//
//    public Product getOneByUpc12(String upc12){
//        return getOneById(
//                getIdByUpc12(
//                        upc12
//                )
//        );
//    }
//
//    public void updateByUpc12(String upc12, Product newProductData){
//        updateById(
//                getIdByUpc12(upc12),
//                newProductData
//        );
//    }
}
