package searchapp.service;

import org.elasticsearch.action.ActionRequestValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchapp.domain.Product;
import searchapp.domain.customExceptions.ProductNotFoundException;
import searchapp.domain.customExceptions.RepositoryException;
import searchapp.domain.customExceptions.SearchAppException;
import searchapp.domain.web.CustomerRatingOptions;
import searchapp.domain.web.PaginationObject;
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

    public List<Product> apiSearch(String stringToSearch,
                                   CustomerRatingOptions ratingFilter,
                                   long minQuantitySold,
                                   SearchSortOption sortOption) throws SearchAppException {
        LOGGER.debug("searching: " + stringToSearch);
        return helper.searchResponseToList(
                    repo.search(
                            productQueryBuilder.buildMultiFieldQueryForAPI(stringToSearch, ratingFilter, minQuantitySold, sortOption)
                    )
                );
    }           //TODO: size-param

    public List<Product> apiSearch(String stringToSearch) throws SearchAppException {
        return apiSearch(stringToSearch, CustomerRatingOptions.ONE, 0L, SearchSortOption.RELEVANCE);
    }

    public List<Product> search(String stringToSearch,
                                CustomerRatingOptions ratingFilter,
                                long minQuantitySold,
                                SearchSortOption sortOption,
                                PaginationObject paginationObject) throws SearchAppException {
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

    public List<Product> search(SearchForm searchForm, PaginationObject paginationObject) throws SearchAppException {                                                   //TODO: goe design-principe om specifieke input te routen naar algemenere methods
        return search(
                searchForm.getInput(),
                searchForm.getRating(),
                searchForm.getMinQuantitySold(),
                searchForm.getSortOption(),
                paginationObject
        );
    }

    public void add(Product newProduct) throws SearchAppException {
        repo.index(
                helper.productToJson(newProduct)
        );
    }

    public Product getOneById(String id) throws SearchAppException{
        LOGGER.debug("getting by id: " + id);
        try {
            return helper.getResponseToProduct(
                    repo.getById(
                            id
                    )
            );
        }catch (ActionRequestValidationException arve){                                                                 //TODO: wel goe??
            throw new ProductNotFoundException("No given id present", arve);
        }
    }

    public Product getOneByGrpId(String grpId) throws SearchAppException{
        return getOneById(
                getIdByGrpId(
                        grpId
                )
        );
    }

    public void updateById(String id, Product newProductData) throws SearchAppException {
        repo.update(
                id,
                helper.productToJson(newProductData)
        );
    }

    public void updateByGrpId(String grpId, Product newProductData) throws SearchAppException {
        updateById(
                getIdByGrpId(grpId),
                newProductData
        );
    }

    public void deleteById(String id) throws RepositoryException {
        repo.delete(id);
    }                                                  //TODO: voor consistentie ook SearchAppException throwen?

    public void deleteByGrpId(String grpId) throws SearchAppException {
        deleteById(
                getIdByGrpId(grpId)
        );
    }

    public String getIdByGrpId(String grpId) throws SearchAppException {
        return helper.searchResponseToId(
                repo.search(
                        productQueryBuilder.buildSearchByGrpId(
                                grpId
                        )
                )
        );
    }

    public List<Product> matchAll() throws SearchAppException{
        return helper.searchResponseToList(
                    repo.search(
                            productQueryBuilder.buildMatchAllQuery()
                    )
        );
    }
}
