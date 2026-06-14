package ci.inphb.ensi.portail.configuration.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Journalise l'execution des methodes annotees {@link Logged}.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(ci.inphb.ensi.portail.configuration.logger.Logged)")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String signature = joinPoint.getSignature().toShortString();
        long debut = System.currentTimeMillis();
        try {
            Object resultat = joinPoint.proceed();
            LOGGER.info("{} execute en {} ms", signature, System.currentTimeMillis() - debut);
            return resultat;
        } catch (Throwable ex) {
            LOGGER.error("{} en echec apres {} ms : {}", signature, System.currentTimeMillis() - debut, ex.getMessage());
            throw ex;
        }
    }
}
