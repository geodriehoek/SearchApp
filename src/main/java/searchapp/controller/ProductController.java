package searchapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.expression.Mvc;
import searchapp.domain.customExceptions.RepositoryException;
import searchapp.domain.customExceptions.ObjectMapperException;
import searchapp.domain.Product;
import searchapp.domain.customExceptions.SearchAppException;
import searchapp.domain.web.CustomerRatingOptions;
import searchapp.domain.web.ErrorMessage;
import searchapp.domain.web.SearchForm;
import searchapp.domain.web.SearchSortOption;
import searchapp.service.PaginationDirection;
import searchapp.service.PaginationObject;
import searchapp.service.ProductHelper;
import searchapp.service.ProductService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class ProductController {
    private static final String PRODUCTS_ROOT_URL = "/products/";
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductService service;
    @Autowired
    private Mvc mvc;
    private SearchForm searchForm = new SearchForm();                                                                   //TODO: indien 2 vensters, laatste form overwrite eerste form // Rebuild na bvb details faalt (obviously)
    private PaginationObject paginationObject = new PaginationObject(0, 10);                                 //TODO: unhardcode => SearchForm.PaginationObject || momenteel blijft 'from' behouden na nieuwe search
    @Autowired
    private ProductHelper helper;

    @ModelAttribute("searchForm")
    public SearchForm initializeSearchForm(){
        return new SearchForm(searchForm);
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "search")
    public String getSearchForm(@ModelAttribute("searchForm") SearchForm searchForm, Map<String, Object> model){
//        model.put("searchForm", new SearchForm());                                                                    //dit i.t.t. hieronder garandeert een nieuwe search
        model.put("searchForm", searchForm);
        model.put("sortOptions", SearchSortOption.values());
        model.put("ratingOptions", CustomerRatingOptions.values());
        return "search-product";
    }

    @PostMapping(path = PRODUCTS_ROOT_URL + "search")
    public String postSearchForm(@ModelAttribute("searchForm") @Valid SearchForm searchForm, BindingResult br, Map<String, Object> model){
        String returnUrl = null;
        model.put("searchForm", searchForm);
        this.searchForm = searchForm;
        if(br.hasErrors()){
            LOGGER.warn("search cannot be null, redirecting");
            model.put("sortOptions", SearchSortOption.values());
            model.put("ratingOptions", CustomerRatingOptions.values());
            return "search-product";
        }

        LOGGER.debug(searchForm.toString());
        List<Product> resultList = null;

        try {
            resultList = service.searchWithPaginationThrows(
                                                searchForm.getInput(),
                                                searchForm.getRating(),
                                                searchForm.getMinQuantitySold(),
                                                searchForm.getSortOption(),
                                                paginationObject
                                        );
            model.put("resultList", resultList);
//            model.put("searchForm", searchForm);
            model.put("paginationObject", paginationObject);
            LOGGER.info("pagination: " + paginationObject);
            model.put("numberOfResults", resultList.size());

            returnUrl = "search-result";
        } catch (RepositoryException re) {
            LOGGER.error("failed to search: ", re);

            ErrorMessage errorMessage = new ErrorMessage(re.getMessage());
            LOGGER.debug("errorMessge to display: " + errorMessage.getDescription());
            model.put("errorMessage", errorMessage);

            returnUrl = "error";
        } catch (ObjectMapperException ome) {
            LOGGER.error("failed to map: ", ome);

            ErrorMessage errorMessage = new ErrorMessage(ome.getMessage());
            LOGGER.debug("errorMessge to display: " + errorMessage.getDescription());
            model.put("errorMessage", errorMessage);

            returnUrl = "error";
//        } catch (SearchAppException e) {
//            e.printStackTrace();
        }

        return returnUrl;
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "searchResult")
    public String getResultList(@ModelAttribute("searchForm") SearchForm searchForm, Map<String, Object> model){
        LOGGER.info("pagination: " + paginationObject);

        List<Product> resultList = service.searchWithPagination(
                                            searchForm.getInput(),
                                            searchForm.getRating(),
                                            searchForm.getMinQuantitySold(),
                                            searchForm.getSortOption(),
                                            paginationObject
                                    );
        model.put("resultList", resultList);
        model.put("searchForm", searchForm);
        model.put("numberOfResults", resultList.size());
        return "search-result";
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "nextPage")
    public String getNextPage(){
        paginationObject.setDirection(PaginationDirection.FORWARD);
         paginationObject.interpretDirection();
        return "redirect:" + mvc.url("PC#getResultList").build();
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "previousPage")
    public String getPrevPage(){
        paginationObject.setDirection(PaginationDirection.BACK);
        paginationObject.interpretDirection();
        return "redirect:" + mvc.url("PC#getResultList").build();
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "details/{grpId}")
    public String details(@PathVariable("grpId") String grpId, Map<String, Object> model){
        model.put("updateProductForm", new Product(service.getOneByGrpId(grpId)));
        model.put("ratingOptions", CustomerRatingOptions.values());

        return "product-details";
    }

    @PostMapping(path = PRODUCTS_ROOT_URL + "details/{grpId}")                                                                   //TODO: idem als deleteById, return naar search-result
    public String updateProduct(@ModelAttribute("updateProductForm") Product updateProduct, @PathVariable("grpId") String grpId){
        service.updateByGrpId(grpId, updateProduct);
        Thread thread = new Thread();
        try {                                                                                                           // TODO: asynchronisatie
            thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "redirect:" + mvc.url("PC#getResultList").build();
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "/delete")
    public String delete(@RequestParam String grpId){                                           //TODO: searchForm meekrijgen na post zodat lijst opnieuw kan getoond worden na deleteById
        service.deleteByGrpId(grpId);
        Thread thread = new Thread();
        try {                                                                                                           // TODO: asynchronisatie
            thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "redirect:" + mvc.url("PC#getResultList").build();
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "/new")
    public String getAddForm(Map<String, Object> model){
        model.put("newProductForm", new Product());
        model.put("ratingOptions", CustomerRatingOptions.values());
        return "new-product";
    }

    @PostMapping(path = PRODUCTS_ROOT_URL + "/new")
    public String postAddForm(@ModelAttribute("newProductForm") Product newProduct){                                 //TODO: validation => Empty form indexed new product, slechte redirect
        service.add(newProduct);

        Thread thread = new Thread();
        try {                                                                                                           // TODO: asynchronisatie
            thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        String url = "redirect:" + mvc.url("PC#details").build() + newProduct.getUpc12();
//        LOGGER.debug(url);
        return "redirect:" + mvc.url("PC#details").build() + newProduct.getGrp_id();                        //TODO: "back to results" van details na newProduct crasht
    }
}
