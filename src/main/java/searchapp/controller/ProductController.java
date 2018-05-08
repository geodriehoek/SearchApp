package searchapp.controller;

import org.elasticsearch.action.search.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.expression.Mvc;
import searchapp.domain.Product;
import searchapp.domain.web.CustomerRatingOptions;
import searchapp.domain.web.ScrollObject;
import searchapp.domain.web.SearchForm;
import searchapp.domain.web.SearchSortOption;
import searchapp.service.ProductHelper;
import searchapp.service.ProductService;

import java.util.List;
import java.util.Map;

@Controller
public class ProductController {
    private final static String PRODUCTS_ROOT_URL = "/products/";
    private Logger log = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductService service;
    @Autowired
    private Mvc mvc;
    private SearchForm searchForm = new SearchForm();                                                                   //TODO: indien 2 vensters, laatste form overwrite eerste form // Rebuild na bvb details faalt (obviously)
    private ScrollObject scrollObject = new ScrollObject();
    @Autowired
    private ProductHelper helper;                                                                                       //TODO: in beste geval niet hier

    @ModelAttribute("searchForm")
    public SearchForm initializeSearchForm(){
        return new SearchForm(searchForm);
    }                       //TODO: rename
    @ModelAttribute("scrollObject")
    public ScrollObject getScrollObject(){
        return new ScrollObject(scrollObject);
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "search")
    public String getSearchForm(Map<String, Object> model){
        model.put("searchForm", new SearchForm());
        model.put("sortOptions", SearchSortOption.values());
        model.put("ratingOptions", CustomerRatingOptions.values());
        return "search-product";
    }

    @PostMapping(path = PRODUCTS_ROOT_URL + "search")
    public String processSearchForm(@ModelAttribute("searchForm") SearchForm searchForm, Map<String, Object> model){
        log.debug(searchForm.toString());
        List<Product> resultList = service.search(
                                            searchForm.getInput(),
                                            searchForm.getRating(),
                                            searchForm.getMinQuantitySold(),
                                            searchForm.getSortOption()
                                    );
        model.put("resultList", resultList);
        model.put("searchForm", searchForm);
        this.searchForm = searchForm;
        model.put("numberOfResults", resultList.size());
        return "search-result";
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "searchResult")
    public String getResultList(@ModelAttribute("searchForm") SearchForm searchForm, Map<String, Object> model){
        log.debug(searchForm.toString());
        List<Product> resultList = service.search(
                                            searchForm.getInput(),
                                            searchForm.getRating(),
                                            searchForm.getMinQuantitySold(),
                                            searchForm.getSortOption()
                                    );
        model.put("resultList", resultList);
        model.put("searchForm", searchForm);
        model.put("numberOfResults", resultList.size());
        return "search-result";
    }           //TODO: nietmeer nodig bij scrollsearch?

    @GetMapping(path = PRODUCTS_ROOT_URL + "details/{upc12}")
    public String details(@PathVariable("upc12") String upc12, Map<String, Object> model){
        model.put("updateProductForm", new Product(service.searchByUpc12(upc12)));
        model.put("ratingOptions", CustomerRatingOptions.values());

        return "product-details";
    }

    @PostMapping(path = PRODUCTS_ROOT_URL + "details/{upc12}")                                                                   //TODO: idem als delete, return naar search-result
    public String updateProduct(@ModelAttribute("updateProductForm") Product updateProduct, @PathVariable("upc12") String upc12){
        service.updateByUpc12(upc12, updateProduct);
        Thread thread = new Thread();
        try {                                                                                                           // TODO: asynchronisatie
            thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "redirect:" + mvc.url("PC#getResultList").build();
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "/delete")
    public String delete(@RequestParam String id){                                           //TODO: searchForm meekrijgen na post zodat lijst opnieuw kan getoond worden na delete
        service.delete(id);
        Thread thread = new Thread();
        try {                                                                                                           // TODO: asynchronisatie
            thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "redirect:" + mvc.url("PC#getResultList").build();
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "/new")
    public String addFormPresent(Map<String, Object> model){
        model.put("newProductForm", new Product());
        model.put("ratingOptions", CustomerRatingOptions.values());
        return "new-product";
    }

    @PostMapping(path = PRODUCTS_ROOT_URL + "/new")
    public String addFormProcess(@ModelAttribute("newProductForm") Product newProduct){                                 //TODO: validation => Empty form indexed new product, slechte redirect
        service.add(newProduct);

        String url = "redirect:" + mvc.url("PC#details").build() + newProduct.getUpc12();
        log.debug(url);
        return url;
    }



    // scroll is hier niet voor gemaakt
    // ------------------ SCROLL ----------------------- //

    @GetMapping(path = PRODUCTS_ROOT_URL + "scroll/search")
    public String getSearchScrollForm(Map<String, Object> model){
        model.put("searchForm", new SearchForm());
        model.put("sortOptions", SearchSortOption.values());
        model.put("ratingOptions", CustomerRatingOptions.values());
        return "search-product";
    }

    @PostMapping(path = PRODUCTS_ROOT_URL + "scroll/search")
    public String processSearchScrollForm(@ModelAttribute("searchForm") SearchForm searchForm, @ModelAttribute("scrollObject") ScrollObject scrollObject, Map<String, Object> model){
        log.debug(searchForm.toString());
        SearchResponse response = service.searchScrollStart(
                searchForm.getInput(),
                searchForm.getRating(),
                searchForm.getMinQuantitySold(),
                searchForm.getSortOption()
        );
        List<Product> resultList = helper.searchResponseToList(response);
        model.put("resultList", resultList);
        model.put("searchForm", searchForm);
        model.put("numberOfResults", resultList.size());

        String scrollId = helper.getScrollIdFromResponse(response);
        this.scrollObject = new ScrollObject(scrollId);
        log.info("start scrollId: " + scrollId );
        model.put("scrollObject", this.scrollObject);                                                                        //TODO: als sessionAttribute en dan updaten??
        return "search-result-scroll";
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "scroll/searchResult")                                                       //TODO: wat met omgekeerde richting?
    public String getScrollResultList(@ModelAttribute("searchForm") SearchForm searchForm/*, @ModelAttribute("scrollObject") ScrollObject scrollObject*/, Map<String, Object> model){
        SearchResponse response = service.searchScrollContinue(this.scrollObject.getId());

        List<Product> resultList = helper.searchResponseToList(response);
        model.put("resultList", resultList);
        model.put("searchForm", searchForm);
        model.put("numberOfResults", resultList.size());

//        String scrollIdContinue = response.getScrollId();
//        this.scrollObject = new ScrollObject(scrollIdContinue);
        log.info("continue scrollId: " + this.scrollObject.getId());                                                    //TODO: niet nodig in productie
        model.put("scrollObject", this.scrollObject);                                                                   //TODO: beiden dus

        return "search-result-scroll";
    }

}
