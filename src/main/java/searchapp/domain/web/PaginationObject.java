package searchapp.domain.web;

import org.slf4j.LoggerFactory;

public class PaginationObject {
    private int from;
    private int size;
    private PaginationDirection direction;


    public PaginationObject() {
    }

    public PaginationObject(int from, int size) {
        this.from = from;
        this.size = size;
    }

    public PaginationObject(int from, int size, PaginationDirection direction) {
        this.from = from;
        this.size = size;
        this.direction = direction;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public PaginationDirection getDirection() {
        return direction;
    }

    public void setDirection(PaginationDirection direction) {
        this.direction = direction;
    }

    public void interpretDirection(){
        switch(direction){
            case FORWARD:   from = from + size;
                break;
            case BACK:
                if(from - size >= 0){
                    from = from - size;
                }else{
                    LoggerFactory.getLogger(PaginationObject.class).warn("apiSearch-from can't be negative");             //TODO: hier wel loggen?
                }
                break;
            default:                                                                                                  //TODO: nood aan default?
                LoggerFactory.getLogger(PaginationObject.class).warn("unable to interpret apiSearch-from");             //TODO: hier wel loggen?
        }
    }

    public void reset(){
        this.from = 0;
    }

    @Override
    public String toString() {
        return "PaginationObject{" +
                "from=" + from +
                ", size=" + size +
                ", direction=" + direction +
                '}';
    }                                                                                      //TODO: weg
}
