package searchapp.domain;

public class Product {

    private String brandName;
    private String productName;
    private Long customerRating;
    private Double price;
    private String grp_id;
    private Long quantitySold;
    private String upc12;
    private double score;                                               //TODO: na analyse weg// via update voegt het momenteel een "score"-field toe hierdoor

    public Product() {                                                  //TODO: constructors optimaliseren
    }

    public Product(Product other, double score) {
        this.brandName = other.brandName;
        this.productName = other.productName;
        this.customerRating = other.customerRating;
        this.price = other.price;
        this.grp_id = other.grp_id;
        this.quantitySold = other.quantitySold;
        this.upc12 = other.upc12;
        this.score = score;
    }

    public Product(Product other) {
        this.brandName = other.brandName;
        this.productName = other.productName;
        this.customerRating = other.customerRating;
        this.price = other.price;
        this.grp_id = other.grp_id;
        this.quantitySold = other.quantitySold;
        this.upc12 = other.upc12;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(Long customerRating) {
        this.customerRating = customerRating;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getGrp_id() {
        return grp_id;
    }

    public void setGrp_id(String grp_id) {
        this.grp_id = grp_id;
    }

    public Long getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Long quantitySold) {
        this.quantitySold = quantitySold;
    }

    public String getUpc12() {
        return upc12;
    }

    public void setUpc12(String upc12) {
        this.upc12 = upc12;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
