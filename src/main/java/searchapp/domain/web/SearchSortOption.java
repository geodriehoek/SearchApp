package searchapp.domain.web;

public enum SearchSortOption {
    RELEVANCE("_score", "relevance"),
    RATING("customerRating", "customer rating"),
    QUANTITY_SOLD("quantitySold", "quantity sold");
    private final String value;
    private final String description;

    SearchSortOption(String value, String description){
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription(){
        return description;
    }
}
