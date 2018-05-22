package searchapp.domain.customExceptions;

public class ObjectMapperException extends SearchAppException {
    public ObjectMapperException(String message){
        super(message);
    }

    public ObjectMapperException(String message, Throwable cause){
        super(message, cause);
    }
}
