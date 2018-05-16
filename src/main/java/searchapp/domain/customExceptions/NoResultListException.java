package searchapp.domain.customExceptions;

public class NoResultListException extends ObjectMapperException {
    public NoResultListException(String message){
        super(message);
    }
    public NoResultListException(String message, Throwable cause){
        super(message, cause);
    }
}
