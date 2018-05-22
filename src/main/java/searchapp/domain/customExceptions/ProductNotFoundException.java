package searchapp.domain.customExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such Product")                                               //TODO; vangt automatisch op naar error.html => checken hoe gebruiken
public class ProductNotFoundException extends SearchAppException {                                                      //TODO: runtime???
    public ProductNotFoundException(String message, Throwable cause){
        super(message, cause);
    }

    public ProductNotFoundException(String message){
        super(message);
    }
}
