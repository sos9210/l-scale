package reehi.board.articleread.cache

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class OptimizedCacheAspect (private val optimizedCacheManager: OptimizedCacheManager) {

    @Around("@annotation(OptimizedCacheable)")
    @Throws(Throwable::class)
    fun around(joinPoint: ProceedingJoinPoint): Any? {
        val cacheable = findAnnotation(joinPoint)
        return optimizedCacheManager.process(
            cacheable.type,
            cacheable.ttlSeconds,
            joinPoint.args,
            findReturnType(joinPoint),
        ) { joinPoint.proceed() }
    }

    private fun findAnnotation(joinPoint: ProceedingJoinPoint): OptimizedCacheable {
        val signature = joinPoint.signature as MethodSignature
        return signature.method.getAnnotation(OptimizedCacheable::class.java)
    }

    private fun findReturnType(joinPoint: ProceedingJoinPoint): Class<*> {
        val signature = joinPoint.signature as MethodSignature
        return signature.returnType
    }
}