package searchapp.domain.customExceptions;

public class SearchAppException extends RuntimeException {
    public SearchAppException(String message){
        super(message);
    }

    public SearchAppException(String message, Throwable cause){
        super(message, cause);
    }
}
