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

    /**
     * 将context注入到ContextCarrier，用于跨进程，传播上下文
     */
    void inject(ContextCarrier var1);

    /**
     * 将ContextCarrier解压至context，用于跨进程，接收上下文
     * @param var1
     */
    void extract(ContextCarrier var1);

    /**
     * 将context快照到ContextSnapshot，用于跨线程，传播上下文
     */
    ContextSnapshot capture();

    /**
     * 将ContextSnapshot解呀到context，用于跨线程，接受上下文
     */
    void continued(ContextSnapshot var1);

    /**
     * 得到关联的主要链路编号
     */
    String getReadablePrimaryTraceId();

    /**
     * 得到segment的编号
     */
    String getSegmentId();

    int getSpanId();

    /**
     * 创建EntrySpan、LocalSpan以及ExitSpan对象
     */
    AbstractSpan createEntrySpan(String var1);

    AbstractSpan createLocalSpan(String var1);

    AbstractSpan createExitSpan(String var1, String var2);

    /**
     * 获取当前活跃的span对象
     */
    AbstractSpan activeSpan();

    /**
     * 停止指定span
     */
    boolean stopSpan(AbstractSpan var1);

    AbstractTracerContext awaitFinishAsync();

    void asyncStop(AsyncSpan var1);

    CorrelationContext getCorrelationContext();
}
