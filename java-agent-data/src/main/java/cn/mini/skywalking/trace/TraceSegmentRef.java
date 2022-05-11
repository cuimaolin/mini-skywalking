package cn.mini.skywalking.trace;

import org.apache.skywalking.apm.agent.core.conf.Config;
import org.apache.skywalking.apm.agent.core.context.ContextCarrier;
import org.apache.skywalking.apm.agent.core.context.ContextSnapshot;
import org.apache.skywalking.apm.agent.core.context.trace.TraceSegment;
import org.apache.skywalking.apm.network.language.agent.v3.RefType;
import org.apache.skywalking.apm.network.language.agent.v3.SegmentReference;

/**
 * TraceSegment指向的类型，通过traceSegmentId和spanId属性，其指向父级TraceSegment的指定span
 */
public class TraceSegmentRef {

    /**
     * 指向SegmentRefType类型。不同的指向类型，使用不同的构造方法
     * CROSS_PROCESS，跨进程，例如远程调用，对应构造方法 TraceSegmentRef(ContextCarrier)
     * CROSS_THREAD，跨线程，例如异步线程任务，对应构造方法 TraceSegmentRef(ContextSnapshot)
     */
    private SegmentRefType type;

    /**
     * 父SegmentRef的traceId
     */
    private String traceId;

    /**
     * 父TraceSegment id
     */
    private String traceSegmentId;

    /**
     * 父span 编号
     */
    private int spanId;

    /**
     * 父节点信息
     */
    private String parentService;
    private String parentServiceInstance;
    private String parentEndpoint;
    private String addressUsedAtClient;

    /**
     * 将ContextCarrier转换为TraceSegmentRef
     */
    public TraceSegmentRef(ContextCarrier carrier) {
        // 跨进程类型
        this.type = SegmentRefType.CROSS_PROCESS;
        this.traceId = carrier.getTraceId();
        this.traceSegmentId = carrier.getTraceSegmentId();
        this.spanId = carrier.getSpanId();
        this.parentService = carrier.getParentService();
        this.parentServiceInstance = carrier.getParentServiceInstance();
        this.parentEndpoint = carrier.getParentEndpoint();
        this.addressUsedAtClient = carrier.getAddressUsedAtClient();
    }

    /**
     * 将ContextSnapshot转换为TraceSegmentRef
     */
    public TraceSegmentRef(ContextSnapshot snapshot) {
        this.type = SegmentRefType.CROSS_THREAD;
        this.traceId = snapshot.getTraceId().getId();
        this.traceSegmentId = snapshot.getTraceSegmentId();
        this.spanId = snapshot.getSpanId();
        this.parentService = Config.Agent.SERVICE_NAME;
        this.parentServiceInstance = Config.Agent.INSTANCE_NAME;
        this.parentEndpoint = snapshot.getParentEndpoint();
    }

    public SegmentReference transform() {
        SegmentReference.Builder refBuilder = SegmentReference.newBuilder();
        if (SegmentRefType.CROSS_PROCESS.equals(type)) {
            refBuilder.setRefType(RefType.CrossProcess);
        } else {
            refBuilder.setRefType(RefType.CrossThread);
        }
        refBuilder.setTraceId(traceId);
        refBuilder.setParentTraceSegmentId(traceSegmentId);
        refBuilder.setParentSpanId(spanId);
        refBuilder.setParentService(parentService);
        refBuilder.setParentServiceInstance(parentServiceInstance);
        refBuilder.setParentEndpoint(parentEndpoint);
        if (addressUsedAtClient != null) {
            refBuilder.setNetworkAddressUsedAtPeer(addressUsedAtClient);
        }

        return refBuilder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TraceSegmentRef ref = (TraceSegmentRef) o;

        if (spanId != ref.spanId)
            return false;
        return traceSegmentId.equals(ref.traceSegmentId);
    }

    @Override
    public int hashCode() {
        int result = traceSegmentId.hashCode();
        result = 31 * result + spanId;
        return result;
    }

    public enum SegmentRefType {
        CROSS_PROCESS, CROSS_THREAD
    }
}
