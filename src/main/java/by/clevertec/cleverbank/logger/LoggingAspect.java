package by.clevertec.cleverbank.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

  @Around("execution(* by.clevertec.cleverbank.service.UserService.*(..)) || "
      + "execution(* by.clevertec.cleverbank.service.TransactionService.*(..)) || "
      + "execution(* by.clevertec.cleverbank.service.BankService.*(..)) || "
      + "execution(* by.clevertec.cleverbank.service.AccountService.*(..))")
  public Object logMethodCall(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    String methodName = proceedingJoinPoint.getSignature().getName();
    Object[] args = proceedingJoinPoint.getArgs();

    logger.info("Method: {}", methodName);
    logger.info("Arguments: {}", args);

    Object result = proceedingJoinPoint.proceed();

    logger.info("Method: {}", methodName);
    logger.info("Response: {}", result);

    return result;
  }
}
