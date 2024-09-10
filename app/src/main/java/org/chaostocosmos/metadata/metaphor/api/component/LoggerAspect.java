package org.chaostocosmos.metadata.metaphor.api.component;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * LoggingAspect
 * 
 * @author Kooin-Shin
 */
@Aspect
@Configuration
public class LoggerAspect {    
    /**
     * Log object
     */
    private static final Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

    /**
     * Get logger
     * @return
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Replace parameters to message
     * @param message
     * @parma params
     */
    public static String replaceParams(String message, Object... params) {
        for(Object param : params) {
            int idx = message.indexOf("{}");
            message = message.substring(0, idx) + param + message.substring(idx+2);
        }
        return message;
    }

    /**
     * Log information message with params
     * @param message
     * @param params
     */
    public static void info(String message, Object... params) {
        logger.info(replaceParams(message, params));
    }

    /**
     * Log debug message with params
     * @param message
     * @param params
     */
    public static void debug(String message, Object... params) {        
        logger.debug(replaceParams(message, params));
    }

    /**
     * Log warning message with params
     * @param message
     * @param params
     */
    public static void warn(String message, Object... params) {
        logger.warn(replaceParams(message, params));   
    }

    /**
     * Log error message with params
     * @param message
     * @param params
     */
    public static void err(String message, Object... params) {
        logger.error(replaceParams(message, params));
    }

    /**
     * Log fatal message with params
     * @param message
     * @param params
     */
    public static void trace(String message, Object... params) {
        logger.trace(replaceParams(message, params));
    }

    /**
     * Log method parameters
     * @param joinPoint
     */
    @Before("execution(* com.example.yourpackage..*(..))")
    public void logMethodParameters(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        logger.info("Method {} called with arguments: {}", methodName, args);
    }

    /**
     * Log method returns
     * @param joinPoint
     * @param result
     */
    @AfterReturning(pointcut = "execution(* com.example.yourpackage..*(..))", returning = "result")
    public void logMethodReturn(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Method {} returned with value: {}", methodName, result);
    }

    /**
     * Log method exceptions
     * @param joinPoint
     * @param error
     */
    @AfterThrowing(pointcut = "execution(* com.example.yourpackage..*(..))", throwing = "error")
    public void logMethodException(JoinPoint joinPoint, Throwable error) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.error("Method {} threw exception: {}", methodName, error.getMessage());
    }
}
