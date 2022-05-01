package cn.mini.skywalking.collect;

import cn.mini.skywalking.collect.commons.StackDepth;
import org.apache.skywalking.apm.agent.core.context.*;
import org.apache.skywalking.apm.agent.core.context.tag.Tags;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.network.trace.component.ComponentsDefine;

import javax.servlet.http.HttpServletRequest;

import static cn.mini.skywalking.collect.commons.Constants.*;

import java.lang.reflect.Method;

public abstract class AbstractMethodInterceptor {

    /**
     * spring mvc插件的进行链路数据采集的入口
     * 在spring mvc方法执行之前执行
     * @param objInst
     * @param method
     * @param allArguments
     * @param argumentsTypes
     * @param result
     * @throws Throwable
     */
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                             MethodInterceptResult result) throws Throwable {

        // 获取当前的请求
        final HttpServletRequest httpServletRequest = (HttpServletRequest) ContextManager.getRuntimeContext().get(REQUEST_KEY_IN_RUNTIME_CONTEXT);


        if (httpServletRequest != null) {
            // 从Context中获取调用栈深度
            StackDepth stackDepth = (StackDepth) ContextManager.getRuntimeContext().get(CONTROLLER_METHOD_STACK_DEPTH);

            // 等于null，则说明是调用链的开端
            if (stackDepth == null) {
                // 获取跨进程的context
                final ContextCarrier contextCarrier = new ContextCarrier();
                CarrierItem next = contextCarrier.items();

                while (next.hasNext()) {
                    next = next.next();
                    // 从header里获取了远程调用时候传送过来的数据，塞入contextCarrier
                    // setHeadValue的时候会自动反序列化
                    next.setHeadValue(httpServletRequest.getHeader(next.getHeadKey()));
                }

                // 获取操作名称，这里做了简化
                String operationName = this.buildOperationName();

                // 创建一个span，由于是当前服务的第一个span，故使用entry span
                AbstractSpan span = ContextManager.createEntrySpan(operationName, contextCarrier);
                Tags.URL.set(span, httpServletRequest.getRequestURL().toString());
                Tags.HTTP.METHOD.set(span, httpServletRequest.getMethod());
                span.setComponent(ComponentsDefine.SPRING_MVC_ANNOTATION);
                SpanLayer.asHttp(span);

                // 建立一个新的stackDepth，并写入Context
                stackDepth = new StackDepth();
                ContextManager.getRuntimeContext().put(CONTROLLER_METHOD_STACK_DEPTH, stackDepth);
            } else {
                // 如果spanDepth不为null，则说明已经创建过span了，则创建local span
                AbstractSpan span = ContextManager.createLocalSpan(buildOperationName());
                span.setComponent(ComponentsDefine.SPRING_MVC_ANNOTATION);
            }

            stackDepth.increment();
        }
    }

    /**
     * spring mvc在进行链路数据采集的出口
     * 在方法执行完后进行执行
     * @param objInst
     * @param method
     * @param allArguments
     * @param argumentsTypes
     * @param ret
     * @return
     * @throws Throwable
     */
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                              Object ret) throws Throwable {

        // 获取context
        final RuntimeContext runtimeContext = ContextManager.getRuntimeContext();

        // 获取当前的请求
        Object request = runtimeContext.get(REQUEST_KEY_IN_RUNTIME_CONTEXT);

        if (request != null) {
            try {
                // 获取stackDepth
                StackDepth stackDepth = (StackDepth) runtimeContext.get(CONTROLLER_METHOD_STACK_DEPTH);
                if (stackDepth == null) {
                    // 抛出异常，在这里省略
                } else {
                    // stackDepth减少
                    stackDepth.decrement();
                }

                // 如果所有span已经全部出栈
                if (stackDepth.depth() == 0) {
                    // 获取response
                    Object response = runtimeContext.get(RESPONSE_KEY_IN_RUNTIME_CONTEXT);
                    if (response == null) {
                        // 抛出异常，在这里省略
                    }

                    // 对当前context的内容进行清除
                    runtimeContext.remove(REACTIVE_ASYNC_SPAN_IN_RUNTIME_CONTEXT);
                    runtimeContext.remove(REQUEST_KEY_IN_RUNTIME_CONTEXT);
                    runtimeContext.remove(RESPONSE_KEY_IN_RUNTIME_CONTEXT);
                    runtimeContext.remove(CONTROLLER_METHOD_STACK_DEPTH);
                }
            } finally {
                // 进行出栈，会对当前的span进行释放
                ContextManager.stopSpan();
            }
        }

        return ret;
    }


    private String buildOperationName() {
        return null;
    }

}
