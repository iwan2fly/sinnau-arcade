package kr.sinnau.platform.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

@Slf4j
public class EventLogAspect {

    @Around("@annotation(eventLog)")
    public Object logEvent(ProceedingJoinPoint joinPoint, kr.sinnau.platform.common.aspect.annotation.EventLog eventLog) throws Throwable {
        String task = eventLog.value();

        log.info("[Event] '{}' 시작", task);
        try {
            return joinPoint.proceed();
        } finally {
            log.info("[Event] '{}' 종료", task);
        }
    }
}
