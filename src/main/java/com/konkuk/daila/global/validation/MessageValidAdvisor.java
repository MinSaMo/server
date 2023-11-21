package com.konkuk.daila.global.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MessageValidAdvisor {

    private final RequestMessageValidator validator;

    @Before("@annotation(com.konkuk.daila.global.validation.MessageValid)")
    public void validate(JoinPoint joinPoint) {
        log.info("join point = {}", joinPoint.getTarget().getClass());
        for (Object arg : joinPoint.getArgs()) {
            if (validator.supports(arg.getClass())) {
                validator.validate(arg);
            }
        }
    }
}
