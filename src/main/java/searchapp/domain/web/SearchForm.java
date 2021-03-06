package searchapp.domain.web;

import javax.validation.constraints.NotBlank;

public class SearchForm {
    @NotBlank
    private String input;
    private CustomerRatingOptions rating;
    private long minQuantitySold;
    private SearchSortOption sortOption;


    public SearchForm() {
        this.rating = CustomerRatingOptions.ONE;
        this.minQuantitySold = 0;
        this.sortOption = SearchSortOption.RELEVANCE;
    }

    public SearchForm(String input) {
        this.input = input;
    }

    public SearchForm(SearchForm other){
        this.input = other.input;
        this.rating = other.rating;
        this.minQuantitySold = other.minQuantitySold;
        this.sortOption = other.sortOption;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public CustomerRatingOptions getRating() {
        return rating;
    }

    public void setRating(CustomerRatingOptions rating) {
        this.rating = rating;
    }

    public long getMinQuantitySold() {
        return minQuantitySold;
    }

    public void setMinQuantitySold(long minQuantitySold) {
        this.minQuantitySold = minQuantitySold;
    }

    public SearchSortOption getSortOption() {
        return sortOption;
    }

    public void setSortOption(SearchSortOption sortOption) {
        this.sortOption = sortOption;
    }

    @Override
    public String toString() {                                                                                          //TODO: enkel voor logging, dus mag weg
        return "SearchForm{" +
                "input='" + input + '\'' +
                ", rating=" + rating +
                ", minQuantitySold=" + minQuantitySold +
                ", sortOption=" + sortOption +
                '}';
    }
}
