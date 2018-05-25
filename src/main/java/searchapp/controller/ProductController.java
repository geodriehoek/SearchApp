package searchapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.expression.Mvc;
import searchapp.domain.customExceptions.*;
import searchapp.domain.Product;
import searchapp.domain.web.ErrorMessage;
import searchapp.domain.web.SearchForm;
import searchapp.domain.web.PaginationDirection;
import searchapp.domain.web.PaginationObject;
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
    private PaginationObject paginationObject = new PaginationObject(0, 10);                                 //TODO: unhardcode => SearchForm.PaginationObject || momenteel blijft 'from' behouden na nieuwe search => opgelost door PaginationObject.reset() -> goe genoeg?

    @ModelAttribute("searchForm")
    public SearchForm initializeSearchForm(){
        return new SearchForm(searchForm);
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "search")
    public String getSearchForm(@ModelAttribute("searchForm") SearchForm searchForm, Map<String, Object> model){
        paginationObject.reset();
        model.put("searchForm", searchForm);
        return "search-product";
    }

    @PostMapping(path = PRODUCTS_ROOT_URL + "search")
    public String postSearchForm(@ModelAttribute("searchForm") @Valid SearchForm searchForm, BindingResult br, Map<String, Object> model){
        String returnUrl;
        List<Product> resultList;
        LOGGER.debug(searchForm.toString());
        model.put("searchForm", searchForm);
        this.searchForm = searchForm;

        if(br.hasErrors()){
            LOGGER.warn("search cannot be null, redirecting");
            return "search-product";
        }

        try {
            resultList = service.search(searchForm, paginationObject);

            model.put("resultList", resultList);
            model.put("paginationObject", paginationObject);
            LOGGER.debug("pagination: " + paginationObject);
            model.put("numberOfResults", resultList.size());

            returnUrl = "search-result";
        } catch (SearchAppException sae){                                                                               //TODO: philippe: gezien context is dit beter (dan comment), waarschijnlijk algemeen gesproken niet?
            LOGGER.error("error occurred: ", sae);

            ErrorMessage errorMessage = new ErrorMessage(sae.getMessage());
            LOGGER.debug("errorMessage to display: " + errorMessage.getDescription());
            model.put("errorMessage", errorMessage);

            returnUrl = "error";
//        } catch (RepositoryException re) {
//            LOGGER.error("failed to search: ", re);
//
//            ErrorMessage errorMessage = new ErrorMessage(re.getMessage());
//            LOGGER.debug("errorMessge to display: " + errorMessage.getDescription());
//            model.put("errorMessage", errorMessage);
//
//            returnUrl = "error";
//        } catch (ObjectMapperException ome) {
//            LOGGER.error("failed to map: ", ome);
//
//            ErrorMessage errorMessage = new ErrorMessage(ome.getMessage());
//            LOGGER.debug("errorMessge to display: " + errorMessage.getDescription());
//            model.put("errorMessage", errorMessage);
//
//            returnUrl = "error";
//        } catch (SearchAppException e) {
//            LOGGER.error("PLACEHOLDER: ", e);
//            return "error";
        }

        return returnUrl;
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "searchResult")
    public String getResultList(@ModelAttribute("searchForm") SearchForm searchForm, Map<String, Object> model/*, @RequestParam(value="dir", required = false) PaginationDirection dir*/){
        LOGGER.debug("pagination: " + paginationObject);
        LOGGER.debug(searchForm.toString());
//        LOGGER.debug(dir.toString());
        String returnUrl;
        List<Product> resultList;

        if (searchForm.getInput()==null){                                                                               //TODO: om crash na delete na rebuild (searchForm==null) te voorkomen
            LOGGER.error("null search. redirecting to searchForm");
            return "redirect:" + mvc.url("PC#getSearchForm").build();
        }

        try {
            resultList = service.search(searchForm, paginationObject);
            model.put("resultList", resultList);
            model.put("searchForm", searchForm);
            model.put("numberOfResults", resultList.size());

            returnUrl = "search-result";
        } catch (SearchAppException sae){
            LOGGER.error("error occurred: ", sae);

            ErrorMessage errorMessage = new ErrorMessage(sae.getMessage());
            LOGGER.debug("errorMessage to display: " + errorMessage.getDescription());
            model.put("errorMessage", errorMessage);

            returnUrl = "error";
        }
        return returnUrl;
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "nextPage")                                                                  //TODO: raar, doch werkend
    public String getNextPage(){
        paginationObject.setDirection(PaginationDirection.FORWARD);
        paginationObject.interpretDirection();
        return "redirect:" + mvc.url("PC#getResultList").build();
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "previousPage")                                                              //TODO: raar, doch werkend
    public String getPrevPage(){
        paginationObject.setDirection(PaginationDirection.BACK);
        paginationObject.interpretDirection();
        return "redirect:" + mvc.url("PC#getResultList").build();
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "details/{grpId}")
    public String details(@PathVariable("grpId") String grpId, Map<String, Object> model){
        String returnUrl;

        try {
            model.put("updateProductForm", new Product(service.getOneByGrpId(grpId)));

            returnUrl = "product-details";
        } catch (ProductNotFoundException pnfe){
            LOGGER.error("failed to getOne by id: " + grpId, pnfe);

            ErrorMessage errorMessage = new ErrorMessage("404", pnfe.getMessage());
            LOGGER.debug("errorMessage to display: " + errorMessage.getDescription());                                  //TODO: hoe @ResponseStatus-message tonen? via whitelabel page werkt het
            model.put("errorMessage", errorMessage);

            returnUrl = "error";
        } catch (SearchAppException sae){
            LOGGER.error("error occurred: ", sae);

            ErrorMessage errorMessage = new ErrorMessage(sae.getMessage());
            LOGGER.debug("errorMessage to display: " + errorMessage.getDescription());
            model.put("errorMessage", errorMessage);

            returnUrl = "error";
        }

        return returnUrl;
    }                                                               //TODO: manier vinden om id al mee door te geven? skippen van 2e search via producthelper

    @PostMapping(path = PRODUCTS_ROOT_URL + "details/{grpId}")
    public String updateProduct(@ModelAttribute("updateProductForm") Product updateProduct, @PathVariable("grpId") String grpId, Map<String, Object> model){
        String returnUrl;

        try {
            service.updateByGrpId(grpId, updateProduct);
            returnUrl = "redirect:" + mvc.url("PC#getResultList").build();
        } catch (ObjectMapperException ome){
            LOGGER.error("mapping error occurred: ", ome);

            ErrorMessage errorMessage = new ErrorMessage(ome.getMessage());
            LOGGER.debug("errorMessage to display: " + errorMessage.getDescription());
            model.put("errorMessage", errorMessage);

            returnUrl = "error";
        } catch (SearchAppException sae){
            LOGGER.error("error occurred: ", sae);

            ErrorMessage errorMessage = new ErrorMessage(sae.getMessage());
            LOGGER.debug("errorMessage to display: " + errorMessage.getDescription());
            model.put("errorMessage", errorMessage);

            returnUrl = "error";
        }

        return returnUrl;
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "/delete")
    public String delete(@RequestParam String grpId, Map<String, Object> model){
        String returnUrl;

        try {
            service.deleteByGrpId(grpId);
            returnUrl = "redirect:" + mvc.url("PC#getResultList").build();
        } catch (SearchAppException sae){
            LOGGER.error("error occurred: ", sae);

            ErrorMessage errorMessage = new ErrorMessage(sae.getMessage());
            LOGGER.debug("errorMessage to display: " + errorMessage.getDescription());
            model.put("errorMessage", errorMessage);

            returnUrl = "error";
        }

        return returnUrl;
    }

    @GetMapping(path = PRODUCTS_ROOT_URL + "/new")
    public String getAddForm(Map<String, Object> model){
        model.put("newProductForm", new Product());
        return "new-product";
    }

    @PostMapping(path = PRODUCTS_ROOT_URL + "/new")
    public String postAddForm(@ModelAttribute("newProductForm") Product newProduct, Map<String, Object> model){                                 //TODO: validation of inputfields
        String returnUrl;

        try {
            service.add(newProduct);
            returnUrl = "redirect:" + mvc.url("PC#details").build() + newProduct.getGrp_id();
        } catch (SearchAppException sae){
            LOGGER.error("error occurred: ", sae);

            ErrorMessage errorMessage = new ErrorMessage(sae.getMessage());
            LOGGER.debug("errorMessage to display: " + errorMessage.getDescription());
            model.put("errorMessage", errorMessage);

            returnUrl = "error";
        }

        return returnUrl;
    }
}

//    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "probleem")
//    @ExceptionHandler(RepositoryException.class)
//    public void conflict(){}
//    //lijkt te werken, doch eerst uitzoeken hoe info door te geven naar view
//    //  bvb whitelabel vraagt timestamp
//    //  mss goe om uniforme handeling te bekomen
//    //  https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc#controller-based-exception-handling
