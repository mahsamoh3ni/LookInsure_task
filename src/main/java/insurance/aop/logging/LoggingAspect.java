package insurance.aop.logging;

import insurance.web.rest.errors.InsuranceException;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Aspect
@Slf4j
@Order(1)
@Component
public class LoggingAspect {

    @Pointcut(" within(@org.springframework.web.bind.annotation.RestController *)")
    public void restResourcePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Around(value = "restResourcePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        try {
            var result = joinPoint.proceed();
            log.info("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), result);
            return result;
        } catch (InsuranceException e) {
            log.error(Markers.append("error_code", e.getInsuranceErrorType())
                            .and(Markers.append("error_message", e.getMessage())),
                    "Error (Insurance): in {}.{}() with errorCode: {}, message {}, data {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    e.getInsuranceErrorType(),
                    e.getMessage(),
                    e.getData(),
                    e);
            throw e;
        } catch (Exception e) {
            log.error("Error: in {}.{}()", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), e);
            throw e;
        }
    }
}
