package com.sgc.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.sgc.common.entity.ResultBean;
import com.sgc.common.exception.CheckException;



/**
 * 处理和包装异常
 * 
 * @author
 */
@Aspect
@Configuration
public class ControllerAOP {
	private static final Logger logger = LoggerFactory.getLogger(ControllerAOP.class);

	// 配置切点
    @Pointcut("execution(public com.sgc.common.entity.ResultBean *(..))")
    public void execudeService() {
    }
	
    @Around("execudeService()")
	public Object handlerControllerMethod(ProceedingJoinPoint pjp) {
		long startTime = System.currentTimeMillis();

		ResultBean<?> result;

		try {
			result = (ResultBean<?>) pjp.proceed();
			
			// 如果需要打印入参，可以从这里取出打印
			// Object[] args = pjp.getArgs();

			// 本次操作用时（毫秒）
			long elapsedTime = System.currentTimeMillis() - startTime;
			logger.info("[{}]use time: {}", pjp.getSignature(), elapsedTime);
		} catch (Throwable e) {
			result = handlerException(pjp, e);
		}

		return result;
	}

	private ResultBean<?> handlerException(ProceedingJoinPoint pjp, Throwable e) {
		ResultBean<?> result = new ResultBean();

		// 已知异常【注意：已知异常不要打印堆栈，否则会干扰日志】
		// 校验出错，参数非法
		if (e instanceof CheckException || e instanceof IllegalArgumentException) {
			result.setMsg(e.getMessage());
			result.setCode(ResultBean.CHECK_FAIL);
		} else {
			logger.error(pjp.getSignature() + " error ", e);

			// TODO 未知的异常，应该格外注意，可以发送邮件通知等
			result.setMsg(e.toString());
			result.setCode(ResultBean.UNKNOWN_EXCEPTION);
		}

		return result;
	}
}
