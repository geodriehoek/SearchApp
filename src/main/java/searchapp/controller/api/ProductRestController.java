package searchapp.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.expression.Mvc;
import searchapp.domain.Product;
import searchapp.domain.customExceptions.SearchAppException;
import searchapp.domain.web.CustomerRatingOptions;
import searchapp.domain.web.SearchSortOption;
import searchapp.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/products/api/")
public class ProductRestController {
//    private final static String PRODUCTS_ROOT_URL = "/products/";
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductRestController.class);
    @Autowired
    private ProductService service;
    @Autowired
    private Mvc mvc;
//    private Logger log = LoggerFactory.getLogger(ProductRestController.class);

    @GetMapping(path = "simpleSearch/{stringToSearch}",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Product> simpleSearchProducts(@PathVariable(name = "stringToSearch") String stringToSearch){
        try {
            return service.simpleSearch(stringToSearch);
        } catch (SearchAppException e) {
            LOGGER.error("PLACEHOLDER: ", e);
            return null;
        }
    }

    @GetMapping(path = "search/{stringToSearch}/{rating}/{minQuantitySold}/{sortOption}",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Product> search(
                    @PathVariable(name = "stringToSearch") String stringtoSearch,
                    @PathVariable(name = "rating") CustomerRatingOptions rating,                                        //TODO: hoe enum aanpakken?
                    @PathVariable(name = "minQuantitySold") long minQuantitySold,
                    @PathVariable(name = "sortOption") SearchSortOption sortOption){                                    //TODO: hoe enum aanpakken?
        try {
            return service.search(
                            stringtoSearch,
                            rating,
                            minQuantitySold,
                            sortOption
                    );
        } catch (SearchAppException e) {
            LOGGER.error("PLACEHOLDER: ", e);
            return null;
        }
    }

    @GetMapping(path = "{grpId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Product getProductByUpc12(@PathVariable("grpId") String grpId){                                              //TODO: wordt potentieel nog misgeinterpreteerd?
        try {
            return service.getOneByGrpId(grpId);
        } catch (SearchAppException e) {
            LOGGER.error("PLACEHOLDER: ", e);
            return null;
        }
    }

    @PutMapping(path = "{grpId}",                                                                                          //TODO: put voor create want idempotent?
//    @PostMapping(path = "{upc12}",
                produces = MediaType.APPLICATION_JSON_VALUE,
                consumes = MediaType.APPLICATION_JSON_VALUE)
    public Product updateProduct(@RequestBody Product product, @PathVariable("grpId") String grpId){
        Product updatedProduct = new Product(product);                                                                  //TODO: lomp
        try {
            service.updateByGrpId(grpId, updatedProduct);
        } catch (SearchAppException e) {
            LOGGER.error("PLACEHOLDER: ", e);
            return null;
        }
        return updatedProduct;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> addProduct(@RequestBody Product newProduct){
        try {
            service.add(newProduct);
        } catch (SearchAppException e) {
            LOGGER.error("PLACEHOLDER: ", e);
            return null;
        }

        Thread thread = new Thread();
        try {                                                                                                           // TODO: asynchronisatie
            thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Product addedProduct = null;
        try {
            addedProduct = new Product(service.getOneByGrpId(newProduct.getGrp_id()));                                  //TODO: moet beter
        } catch (SearchAppException e) {
            LOGGER.error("PLACEHOLDER: ", e);
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(
                "Location",
                mvc.url("PRC#getProductByUpc12").arg(0, addedProduct.getUpc12()).build()
        );
        return new ResponseEntity<Product>(addedProduct, headers, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "{grpId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteProduct(@PathVariable("grpId") String grpId){
        try {
            service.deleteByGrpId(grpId);
        } catch (SearchAppException e) {
            LOGGER.error("PLACEHOLDER: ", e);
            return null;
        }
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

//    @PatchMapping(path = "{grpId}",                                                                                        //TODO: voor enkel fields up te daten
//                    consumes = MediaType.APPLICATION_JSON_VALUE,
//                    produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Product> partialUpdateProduct(@RequestBody Product product, @PathVariable("grpId") String grpId){     //TODO: wat met @rRequestBody: input is 1 of meerdere json-field i.p.v. volledig Product
//        return null;
//    }

}
