package cn.mini.skywalking.trace.spanImpl;

import org.apache.skywalking.apm.agent.core.context.TracingContext;
import org.apache.skywalking.apm.agent.core.context.trace.*;
import org.apache.skywalking.apm.agent.core.context.util.TagValuePair;

import java.util.List;

/**
 * AbstractSpan的实现类
 */
public abstract class AbstractTracingSpan implements AbstractSpan {
    /**
     * Span id starts from 0.
     */
    protected int spanId;
    /**
     * Parent span id starts from 0. -1 means no parent span.
     */
    protected int parentSpanId;
    protected List<TagValuePair> tags;
    protected String operationName;
    protected SpanLayer layer;
    /**
     * The span has been tagged in async mode, required async stop to finish.
     */
    protected volatile boolean isInAsyncMode = false;
    /**
     * The flag represents whether the span has been async stopped
     */
    private volatile boolean isAsyncStopped = false;

    /**
     * The context to which the span belongs
     */
    protected final TracingContext owner;

    /**
     * The start time of this Span.
     */
    protected long startTime;
    /**
     * The end time of this Span.
     */
    protected long endTime;
    /**
     * Error has occurred in the scope of span.
     */
    protected boolean errorOccurred = false;

    protected int componentId = 0;

    /**
     * Log is a concept from OpenTracing spec. https://github.com/opentracing/specification/blob/master/specification.md#log-structured-data
     */
    protected List<LogDataEntity> logs;

    /**
     * The refs of parent trace segments, except the primary one. For most RPC call, {@link #refs} contains only one
     * element, but if this segment is a start span of batch process, the segment faces multi parents, at this moment,
     * we use this {@link #refs} to link them.
     */
    protected List<TraceSegmentRef> refs;

    /**
     * Tracing Mode. If true means represents all spans generated in this context should skip analysis.
     */
    protected boolean skipAnalysis;

    protected AbstractTracingSpan(int spanId, int parentSpanId, String operationName, TracingContext owner) {
        this.operationName = operationName;
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
        this.owner = owner;
    }

    /**
     * 结束当前span，并将其添加至给定拥有该span的TraceSegment中
     */
    public boolean finish(TraceSegment owner) {
        this.endTime = System.currentTimeMillis();
        // 添加至TraceSegment
        // 调用owner.archive()
        return true;
    }
}
