package searchapp.domain.customExceptions;

public class NoResultListException extends SearchAppException {
    public NoResultListException(String message){
        super(message);
    }
    public NoResultListException(String message, Throwable cause){
        super(message, cause);
    }
}
