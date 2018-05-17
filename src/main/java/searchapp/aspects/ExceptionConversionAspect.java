//package searchapp.aspects;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import searchapp.domain.customExceptions.RepositoryException;
//
//import java.io.IOException;
//
//@Aspect
//public class ExceptionConversionAspect {
//
//    @Around("execution(* searchapp.repository.ProductRepository.*(..))")
//    public Object IOExceptionToRepositoryException(ProceedingJoinPoint pjp) throws Throwable {
//        try{
//            return pjp.proceed();
//        }catch (IOException ioe){
//            throw new RepositoryException("unable to access database: ", ioe);
//        }
//    }
//}