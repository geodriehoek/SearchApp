package searchapp.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchapp.domain.Product;
import searchapp.domain.web.CustomerRatingOptions;
import searchapp.domain.web.SearchSortOption;
import searchapp.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class ProductRestController {
    @Autowired
    private ProductService service;

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
}
