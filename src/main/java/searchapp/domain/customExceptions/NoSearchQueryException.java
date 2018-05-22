package searchapp.domain.customExceptions;

public class NoSearchQueryException extends SearchAppException {
    public NoSearchQueryException(String message){
        super(message);
    }
    public NoSearchQueryException(String message, Throwable cause){
        super(message, cause);
    }
}
