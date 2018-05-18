package searchapp.domain.customExceptions;

public class RepositoryException extends SearchAppException {
    public RepositoryException(String message){
        super(message);
    }
    public RepositoryException(String message, Throwable cause){
        super(message, cause);
    }
}
