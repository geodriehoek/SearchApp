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
import searchapp.domain.web.CustomerRatingOptions;
import searchapp.domain.web.SearchSortOption;
import searchapp.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/products/api/")
public class ProductRestController {
//    private final static String PRODUCTS_ROOT_URL = "/products/";
    @Autowired
    private ProductService service;
    @Autowired
    private Mvc mvc;
    private Logger log = LoggerFactory.getLogger(ProductRestController.class);

    @GetMapping(path = "simpleSearch/{stringToSearch}",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Product> simpleSearchProducts(@PathVariable(name = "stringToSearch") String stringToSearch){
        return service.simpleSearch(stringToSearch);
    }

    @GetMapping(path = "search/{stringToSearch}/{rating}/{minQuantitySold}/{sortOption}",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Product> search(
                    @PathVariable(name = "stringToSearch") String stringtoSearch,
                    @PathVariable(name = "rating") CustomerRatingOptions rating,                                        //TODO: hoe enum aanpakken?
                    @PathVariable(name = "minQuantitySold") long minQuantitySold,
                    @PathVariable(name = "sortOption") SearchSortOption sortOption){                                    //TODO: hoe enum aanpakken?
        return service.search(
                        stringtoSearch,
                        rating,
                        minQuantitySold,
                        sortOption
                );
    }

    @GetMapping(path = "{grpId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Product getProductByUpc12(@PathVariable("grpId") String grpId){
        return service.getOneByGrpId(grpId);
    }

    @PutMapping(path = "{grpId}",                                                                                          //TODO: put voor create want idempotent?
//    @PostMapping(path = "{upc12}",
                produces = MediaType.APPLICATION_JSON_VALUE,
                consumes = MediaType.APPLICATION_JSON_VALUE)
    public Product updateProduct(@RequestBody Product product, @PathVariable("grpId") String grpId){
        Product updatedProduct = new Product(product);                                                                  //TODO: lomp
        service.updateByGrpId(grpId, updatedProduct);
        return updatedProduct;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> addProduct(@RequestBody Product newProduct){
        service.add(newProduct);

        Thread thread = new Thread();
        try {                                                                                                           // TODO: asynchronisatie
            thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Product addedProduct = new Product(service.getOneByGrpId(newProduct.getGrp_id()));
        HttpHeaders headers = new HttpHeaders();
        headers.add(
                "Location",
                mvc.url("PRC#getProductByUpc12").arg(0, addedProduct.getUpc12()).build()
        );
        return new ResponseEntity<Product>(addedProduct, headers, HttpStatus.CREATED);
    }

//    @PatchMapping(path = "{grpId}",                                                                                        //TODO: voor enkel fields up te daten
//                    consumes = MediaType.APPLICATION_JSON_VALUE,
//                    produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Product> partialUpdateProduct(@RequestBody Product product, @PathVariable("grpId") String grpId){     //TODO: wat met @rRequestBody: input is 1 of meerdere json-field i.p.v. volledig Product
//        return null;
//    }

    @DeleteMapping(path = "{grpId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteProduct(@PathVariable("grpId") String grpId){
        service.deleteByGrpId(grpId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
