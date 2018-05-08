package searchapp.domain.web;

public class ScrollObject {                                                                     //TODO: 2 fields: scrollId en resultList<Product>  => logica in service-laag behouden
    private String id;

    public ScrollObject() {
    }

    public ScrollObject(ScrollObject other){
        this.id = other.getId();
    }

    public ScrollObject(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
