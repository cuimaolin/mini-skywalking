/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.mini.skywalking.trace;

import org.apache.skywalking.apm.agent.core.conf.Config;
import org.apache.skywalking.apm.agent.core.context.ids.DistributedTraceId;
import org.apache.skywalking.apm.agent.core.context.ids.GlobalIdGenerator;
import org.apache.skywalking.apm.agent.core.context.ids.NewDistributedTraceId;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractTracingSpan;
import org.apache.skywalking.apm.agent.core.context.trace.TraceSegmentRef;
import org.apache.skywalking.apm.network.language.agent.v3.SegmentObject;

import java.util.LinkedList;
import java.util.List;

/**
 * TraceSegment是分布式链路的一段，用于记录当前线程的链路
 * 一条分布式链路可能包含多条TraceSegment，因此是跨进程（例如RPC、MQ等），跨线城（例如并发执行、异步回调等）
 */
public class TraceSegment {

    /**
     * traceSegment的编号，每个segment具有其全局唯一的id
     */
    private String traceSegmentId;

    /**
     * 当前TraceSegment的父segment
     * 对于大多数的RPC调用，ref仅有一个父节点
     * 但是如果这个segment是批处理进程的一个开始span，则有多个父节点。这时仅记录第一个父节点
     */
    private TraceSegmentRef ref;

    /**
     * 属于当前segment的span
     */
    private List<AbstractTracingSpan> spans;

    /**
     * TraceSegmentRef的trace id
     */
    private DistributedTraceId relatedGlobalTraceId;

    /**
     * 是否忽略该条TraceSegment
     */
    private boolean ignore = false;

    /**
     * span的数量是否超过上限（用户可配置）。若超过上线，则不在记录Span
     */
    private boolean isSizeLimited = false;

    private final long createTime;

    /**
     * TraceSegment的构造函数
     */
    public TraceSegment() {
        // 生成id对象，赋值给traceSegmentId
        this.traceSegmentId = GlobalIdGenerator.generate();
        // AbstractSpan#finish(TraceSegment) 调用，添加到spans数组中
        this.spans = new LinkedList<>();
        // 创建NewDistributedTraceId对象，赋值给relatedGlobalTraceI
        this.relatedGlobalTraceId = new NewDistributedTraceId();
        this.createTime = System.currentTimeMillis();
    }

    /**
     * Establish the link between this segment and its parents.
     *
     * @param refSegment {@link TraceSegmentRef}
     */
    public void ref(TraceSegmentRef refSegment) {
        if (null == ref) {
            this.ref = refSegment;
        }
    }

    /**
     * 添加DistributedTraceId 对象
     * 被 TracingContext#continued 或 TracingContext#extract(ContextCarrier) 方法调用
     */
    public void relatedGlobalTrace(DistributedTraceId distributedTraceId) {
        if (relatedGlobalTraceId instanceof NewDistributedTraceId) {
            this.relatedGlobalTraceId = distributedTraceId;
        }
    }

    /**
     * After {@link AbstractSpan} is finished, as be controller by "skywalking-api" module, notify the {@link
     * TraceSegment} to archive it.
     */
    public void archive(AbstractTracingSpan finishedSpan) {
        spans.add(finishedSpan);
    }

    /**
     * Finish this {@link TraceSegment}. <p> return this, for chaining
     */
    public TraceSegment finish(boolean isSizeLimited) {
        this.isSizeLimited = isSizeLimited;
        return this;
    }

    public String getTraceSegmentId() {
        return traceSegmentId;
    }

    /**
     * Get the first parent segment reference.
     */
    public TraceSegmentRef getRef() {
        return ref;
    }

    public DistributedTraceId getRelatedGlobalTrace() {
        return relatedGlobalTraceId;
    }

    public boolean isSingleSpanSegment() {
        return this.spans != null && this.spans.size() == 1;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    /**
     * This is a high CPU cost method, only called when sending to collector or test cases.
     *
     * @return the segment as GRPC service parameter
     */
    public SegmentObject transform() {
        SegmentObject.Builder traceSegmentBuilder = SegmentObject.newBuilder();
        traceSegmentBuilder.setTraceId(getRelatedGlobalTrace().getId());
        /*
         * Trace Segment
         */
        traceSegmentBuilder.setTraceSegmentId(this.traceSegmentId);
        // Don't serialize TraceSegmentReference

        // SpanObject
        for (AbstractTracingSpan span : this.spans) {
            traceSegmentBuilder.addSpans(span.transform());
        }
        traceSegmentBuilder.setService(Config.Agent.SERVICE_NAME);
        traceSegmentBuilder.setServiceInstance(Config.Agent.INSTANCE_NAME);
        traceSegmentBuilder.setIsSizeLimited(this.isSizeLimited);

        return traceSegmentBuilder.build();
    }

    @Override
    public String toString() {
        return "TraceSegment{" + "traceSegmentId='" + traceSegmentId + '\'' + ", ref=" + ref + ", spans=" + spans + "}";
    }
}
