package pl.zielona_baza.admin.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.zielona_baza.common.entity.product.Product;
import pl.zielona_baza.common.exception.ProductNotFoundException;

@RestController
@RequestMapping("/products")
public class ProductRestController {
    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/check_unique")
    public ResponseEntity<String> checkUnique(@RequestParam("id") Integer id, @RequestParam("name") String name) {
        String result = productService.checkUnique(id, name);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/get/{id}")
    public ProductDTO getProductInfo(@PathVariable("id") Integer id) throws ProductNotFoundException {
        Product product = productService.get(id);

        return new ProductDTO(product.getName(), product.getMainImagePath(), product.getDiscountPrice().floatValue(), product.getCost().floatValue());
    }
}
