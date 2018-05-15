//package searchapp.aspects;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.AfterThrowing;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.elasticsearch.action.search.SearchResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//
//@Aspect
//@Component
//public class ExceptionCatchingAspect {
//    private Logger log = LoggerFactory.getLogger(ExceptionCatchingAspect.class);
//
////    @Around("execution(* searchapp.repository.ProductRepository.search(..))")
////    public SearchResponse catchAroundRepoSearch(ProceedingJoinPoint pjp){                                               //TODO: na catchen, nog steeds NPE gethrowed door ProductHelper
////        SearchResponse response = null;
////        log.info("before " + pjp.getSignature());
////        try {
////            response = (SearchResponse) pjp.proceed();
////        } catch (Throwable throwable) {
////            log.error("---------------------------");
////            log.error("failed @" + pjp.getSignature());
////            log.error("---------------------------");
////            throwable.printStackTrace();
////        }
////        log.info("after");
////        return response;
////    }
//
//    @AfterThrowing(value = "execution(* searchapp.repository.ProductRepository.search(..))", throwing = "IOException")
//    public void catchAfterThrowingRepoSearch(JoinPoint jp, IOException ioe){                                            // vooral voor loggen?? erder dan catchen
//        log.error("------------------------");
//        log.error("failed @ " + jp.getSignature());
//        log.error("------------------------");
//        log.error(ioe.getMessage());
//    }
//}
