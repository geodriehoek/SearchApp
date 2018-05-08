package searchapp.service;

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
}
