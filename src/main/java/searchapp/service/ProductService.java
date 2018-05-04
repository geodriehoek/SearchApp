package searchapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchapp.domain.Product;
import searchapp.domain.web.CustomerRatingOptions;
import searchapp.domain.web.SearchForm;
import searchapp.domain.web.SearchSortOption;
import searchapp.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {
    Logger log = LoggerFactory.getLogger(ProductService.class);
    @Autowired
    private ProductQueryBuilder productQueryBuilder;
    @Autowired
    private ProductRepository repo;
    @Autowired
    private ProductHelper helper;

//    public List<Product> search(String stringToSearch, CustomerRatingOptions ratingFilter, long minQuantitySold, SearchSortOption sortOption){
//        if (stringToSearch != null && !stringToSearch.equals("")) {                                                                                  //TODO: hier wel throwen??
//            return repo.search(productQueryBuilder.buildMultiFieldQuery(stringToSearch, ratingFilter, minQuantitySold, sortOption));                 //TODO: beter om builder te autowiren in repo??
//        } else {
//            throw new IllegalArgumentException("search cannot be empty");
//        }
//    }

    public List<Product> searchFromSearchForm(SearchForm searchForm){                                                   //TODO: goe design-principe om specifieke input te routen naar algemenere methods
        return search(
                searchForm.getInput(),
                searchForm.getRating(),
                searchForm.getMinQuantitySold(),
                searchForm.getSortOption()
        );
    }

    public List<Product> search(String stringToSearch, CustomerRatingOptions ratingFilter, long minQuantitySold, SearchSortOption sortOption){
        log.info("searching: " + stringToSearch);
        return helper.searchResponseToList(
                    repo.search(
                            productQueryBuilder.buildMultiFieldQuery(stringToSearch, ratingFilter, minQuantitySold, sortOption)
                    )
                );
    }

    public List<Product> simpleSearch(String stringToSearch){
        return helper.searchResponseToList(
                    repo.search(
                            productQueryBuilder.buildMultiFieldQuery(stringToSearch, CustomerRatingOptions.ONE, 0, SearchSortOption.RELEVANCE)
                    )
                );
    }

    public List<Product> searchWithScroll(String stringToSearch, CustomerRatingOptions ratingFilter, long minQuantitySold, SearchSortOption sortOption){
        return helper.searchResponseToList(
                    repo.search(
                            productQueryBuilder.buildMultiFieldQuery(stringToSearch, ratingFilter, minQuantitySold, sortOption)
                    )
                );
    }

    public void delete(String id){
        repo.delete(id);
    }

    public Product searchByUpc12(String upc12){
        return getOneById(
                    getIdByUpc12(upc12)
            );
    }

    public String getIdByUpc12(String upc12){
        return helper.searchResponseToList(
                    repo.search(
                        productQueryBuilder.buildSearchByUpc12(
                                upc12
                        )
                    )
                ).get(0)
                 .getId();
    }

    public void updateById(String id, Product newProductData){
        repo.update(
                id,
                helper.productToJson(newProductData)
        );
    }

    public void updateByUpc12(String upc12, Product newProductData){
        repo.update(
                getIdByUpc12(upc12),
                helper.productToJson(newProductData)
        );
    }

    public Product getOneById(String id){
        return helper.getResponseToProduct(
                    repo.getById(
                            id
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

    public void add(Product newProduct) {
        repo.index(
                helper.productToJson(newProduct)
        );
    }
}
