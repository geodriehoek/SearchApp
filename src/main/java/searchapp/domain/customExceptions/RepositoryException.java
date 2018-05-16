package searchapp.domain.customExceptions;

public class RepositoryException extends ObjectMapperException {
    public RepositoryException(String message){
        super(message);
    }
    public RepositoryException(String message, Throwable cause){
        super(message, cause);
    }
}
