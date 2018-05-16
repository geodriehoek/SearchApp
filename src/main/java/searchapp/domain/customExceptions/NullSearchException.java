package searchapp.domain.customExceptions;

public class NullSearchException extends ObjectMapperException {
    public NullSearchException(String message){
        super(message);
    }
    public NullSearchException(String message, Throwable cause){
        super(message, cause);
    }
}
