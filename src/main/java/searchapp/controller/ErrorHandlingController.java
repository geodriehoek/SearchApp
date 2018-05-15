//package searchapp.controller;
//
////import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;                                   //basis-klasse voor errorhandling
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.NoHandlerFoundException;
//import searchapp.domain.web.ErrorMessage;
//
//import java.io.IOException;
//import java.util.Map;
//
//@ControllerAdvice
//public class ErrorHandlingController {
//    Logger log = LoggerFactory.getLogger(ErrorHandlingController.class);
//
////    @ExceptionHandler(IOException.class)
////    public ResponseEntity<Object> catchIOE(IOException ioe){
////
////
////
////        return null;
////    }
//
//    @ExceptionHandler(NoHandlerFoundException.class)
//    @ResponseStatus(value = HttpStatus.NOT_FOUND)
//    public String handleNoHandlerFound(){
//        log.info("4404 caught");
//        return "search-product";
//    }
//}
