package searchapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.expression.Mvc;
import searchapp.domain.Product;
import searchapp.domain.web.CustomerRatingOptions;
import searchapp.domain.web.SearchForm;
import searchapp.domain.web.SearchSortOption;
import searchapp.service.ProductService;

import java.util.List;
import java.util.Map;

@Controller
public class ProductController {
    private final static String ROOT = "/products/";
    private Logger log = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductService service;
    @Autowired
    private Mvc mvc;
    private SearchForm searchForm = new SearchForm();

    @ModelAttribute("searchForm")
    public SearchForm initializeSearchForm(){
        return new SearchForm(searchForm);
    }

    @GetMapping(path = ROOT + "search")
    public String getSearchForm(Map<String, Object> model){
        model.put("searchForm", new SearchForm());
        model.put("sortOptions", SearchSortOption.values());
        model.put("ratingOptions", CustomerRatingOptions.values());
        return "search-product";
    }

    @PostMapping(path = ROOT + "search")
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

    @GetMapping(path = ROOT + "searchResult")
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
    }

    @GetMapping(path = ROOT + "details/{upc12}")
    public String details(@PathVariable("upc12") String upc12,
                          Map<String, Object> model){
        model.put("updateProductForm", new Product(service.searchByUpc12(upc12)));
        model.put("ratingOptions", CustomerRatingOptions.values());

        return "product-details";
    }

    @PostMapping(path = ROOT + "details/{upc12}")                                                                   //TODO: idem als delete, return naar search-result
    public String updateProduct(@ModelAttribute("updateProductForm") Product updateProduct,
                                @PathVariable("upc12") String upc12){
        service.updateByUpc12(upc12, updateProduct);
        Thread thread = new Thread();
        try {                                                                                                           // TODO: asynchronisatie
            thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "redirect:" + mvc.url("PC#getResultList").build();
    }

    @GetMapping(path = ROOT + "/delete")
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

    @GetMapping(path = ROOT + "/new")
    public String addFormPresent(Map<String, Object> model){
        model.put("newProductForm", new Product());
        model.put("ratingOptions", CustomerRatingOptions.values());
        return "new-product";
    }

    @PostMapping(path = ROOT + "/new")
    public String addFormProcess(@ModelAttribute("newProductForm") Product newProduct){                                 //TODO: validation => Empty form indexed new product, slechte redirect
        service.add(newProduct);

        String url = "redirect:" + mvc.url("PC#details").build() + newProduct.getUpc12();
        log.debug(url);
        return url;
    }
}
