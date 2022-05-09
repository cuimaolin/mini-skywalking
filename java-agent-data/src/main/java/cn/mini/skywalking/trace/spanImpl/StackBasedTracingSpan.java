package cn.mini.skywalking.trace.spanImpl;

import org.apache.skywalking.apm.agent.core.context.TracingContext;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractTracingSpan;
import org.apache.skywalking.apm.agent.core.context.trace.TraceSegment;
import org.apache.skywalking.apm.network.language.agent.v3.SpanObject;
import org.apache.skywalking.apm.util.StringUtil;

/**
 * 实现AbstractTracingSpan抽象类
 * 基于栈的链路追踪Span抽象类
 */
public abstract class StackBasedTracingSpan extends AbstractTracingSpan {
    // 栈深度
    protected int stackDepth;
    protected String peer;

    protected StackBasedTracingSpan(int spanId, int parentSpanId, String operationName, TracingContext owner) {
        super(spanId, parentSpanId, operationName, owner);
        this.stackDepth = 0;
        this.peer = null;
    }

    protected StackBasedTracingSpan(int spanId, int parentSpanId, String operationName, String peer, TracingContext owner) {
        super(spanId, parentSpanId, operationName, owner);
        this.peer = peer;
    }

    public SpanObject.Builder transform() {
        SpanObject.Builder spanBuilder = super.transform();
        if (StringUtil.isNotEmpty(this.peer)) {
            spanBuilder.setPeer(this.peer);
        }

        return spanBuilder;
    }

    public boolean finish(TraceSegment owner) {
        // 如果栈深度为0，调用AbstractTracingSpan#finish方法
        // 将当前span添加至TraceSegment中
        return --this.stackDepth == 0 ? super.finish(owner) : false;
    }

    public AbstractSpan setPeer(String remotePeer) {
        this.peer = remotePeer;
        return this;
    }
}
