package cn.mini.skywalking.context;

import org.apache.skywalking.apm.agent.core.context.AsyncSpan;
import org.apache.skywalking.apm.agent.core.context.ContextCarrier;
import org.apache.skywalking.apm.agent.core.context.ContextSnapshot;
import org.apache.skywalking.apm.agent.core.context.CorrelationContext;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;

/**
 * 一个TraceSegment对象关联一个线程，负责收集盖线程的链路追踪数据
 * 一个AbstractTracerContext关联一个TraceSegment对象
 * ContextManager负责获取、创建、销毁AbstractTracerContext对象
 */
public interface AbstractTracerContext {
    void inject(ContextCarrier var1);

    void extract(ContextCarrier var1);

    ContextSnapshot capture();

    void continued(ContextSnapshot var1);

    String getReadablePrimaryTraceId();

    String getSegmentId();

    int getSpanId();

    AbstractSpan createEntrySpan(String var1);

    AbstractSpan createLocalSpan(String var1);

    AbstractSpan createExitSpan(String var1, String var2);

    AbstractSpan activeSpan();

    boolean stopSpan(AbstractSpan var1);

    org.apache.skywalking.apm.agent.core.context.AbstractTracerContext awaitFinishAsync();

    void asyncStop(AsyncSpan var1);

    CorrelationContext getCorrelationContext();
}
