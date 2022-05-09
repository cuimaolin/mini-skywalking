package cn.mini.skywalking.trace;

import org.apache.skywalking.apm.agent.core.context.AsyncSpan;
import org.apache.skywalking.apm.agent.core.context.tag.AbstractTag;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.agent.core.context.trace.TraceSegmentRef;
import org.apache.skywalking.apm.network.trace.component.Component;

public interface AbstractSpan extends AsyncSpan {

    /**
     * 设置component名称，例如MongoDB、SpringMVC、Tomcat等
     */
    AbstractSpan setComponent(Component component);

    /**
     * 设置spanLayer，例如DB、RPC_FRAMEWORK、HTTP、Cache、MQ等
     */
    AbstractSpan setLayer(SpanLayer layer);

    /**
     * 设置键值对的标签。可以调用多次，构成span的标签集合
     */
    AbstractSpan tag(AbstractTag<?> tag, String value);

    /**
     * 标记发生的异常
     */
    AbstractSpan errorOccurred();

    /**
     * 是否是入口span
     */
    boolean isEntry();

    /**
     * 是否是出口span
     */
    boolean isExit();

    /**
     * 设置操作名称
     */
    AbstractSpan setOperationName(String operationName);

    /**
     * 开始span。一般会设置开始时间
     */
    AbstractSpan start();

    /**
     * 获得span的id，在TraceSegment内唯一
     * 其会在创建span对象时自动生成
     */
    int getSpanId();

    /**
     * 获得操作名称
     */
    String getOperationName();

    /**
     * 设置父TraceSegment
     */
    void ref(TraceSegmentRef ref);

    /**
     * 设置开始时间
     */
    AbstractSpan start(long startTime);

    AbstractSpan setPeer(String remotePeer);
}
