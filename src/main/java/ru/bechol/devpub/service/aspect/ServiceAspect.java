package ru.bechol.devpub.service.aspect;

import io.opentracing.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ServiceAspect {

	@Autowired
	private Tracer tracer;

	@Pointcut(value = "@within(ru.bechol.devpub.service.aspect.Trace) || @annotation(ru.bechol.devpub.service.aspect.Trace)")
	public void callAtMyServicePublic() {
	}

	@Around("callAtMyServicePublic()")
	public Object aroundCallAtMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		Span span = tracer.buildSpan("_" + joinPoint.getSignature().getName()).start();
		Object proceed = joinPoint.proceed();
		span.finish();
		return proceed;
	}

}
