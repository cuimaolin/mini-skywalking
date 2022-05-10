package cn.mini.skywalking.context;

import org.apache.skywalking.apm.agent.core.boot.BootService;
import org.apache.skywalking.apm.agent.core.boot.ServiceManager;
import org.apache.skywalking.apm.agent.core.conf.Config;
import org.apache.skywalking.apm.agent.core.context.*;
import org.apache.skywalking.apm.agent.core.context.AbstractTracerContext;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.sampling.SamplingService;
import org.apache.skywalking.apm.util.StringUtil;

import java.util.Objects;

/**
 * 链路追踪的上下文管理器
 * 其封装了所有AbstractTracerContext提供的方法，外部插件只调用ContextManager的方法，而不调用AbstractTracerContext的方法
 */
public class ContextManager implements BootService {

    /**
     * 用于存储AbstractTracerContext对象
     * ContextManager负责获取、创建、销毁AbstractTracerContext对象
     */
    private static ThreadLocal<AbstractTracerContext> CONTEXT = new ThreadLocal();
    private static ThreadLocal<RuntimeContext> RUNTIME_CONTEXT = new ThreadLocal();
    private static ContextManagerExtendService EXTEND_SERVICE;

    public ContextManager() {
    }

    /**
     * 获取AbstractTracerContext对象。若不存在，进行创建
     */
    private static AbstractTracerContext getOrCreate(String operationName, boolean forceSampling) {
        AbstractTracerContext context = CONTEXT.get();
        // 不存在则创造
        if (context == null) {
            // 操作名为空，创建IgnoredTracerContext对象
            if (StringUtil.isEmpty(operationName)) {
                context = new IgnoredTracerContext();
            } else {
                if (EXTEND_SERVICE == null) {
                    EXTEND_SERVICE = ServiceManager.INSTANCE.findService(ContextManagerExtendService.class);
                }

                context = EXTEND_SERVICE.createTraceContext(operationName, forceSampling);
            }

            CONTEXT.set(context);
        }

        return context;
    }

    private static AbstractTracerContext get() {
        return CONTEXT.get();
    }

    public static String getGlobalTraceId() {
        AbstractTracerContext context = CONTEXT.get();
        return Objects.nonNull(context) ? context.getReadablePrimaryTraceId() : "N/A";
    }

    public static String getSegmentId() {
        AbstractTracerContext context = CONTEXT.get();
        return Objects.nonNull(context) ? context.getSegmentId() : "N/A";
    }

    public static int getSpanId() {
        AbstractTracerContext context = CONTEXT.get();
        return Objects.nonNull(context) ? context.getSpanId() : -1;
    }

    public static AbstractSpan createEntrySpan(String operationName, ContextCarrier carrier) {
        // 获得操作名
        operationName = StringUtil.cut(operationName, Config.Agent.OPERATION_NAME_THRESHOLD);
        AbstractSpan span;
        AbstractTracerContext context;
        // 如果carrier不为null且有效
        if (carrier != null && carrier.isValid()) {
            // 强制收集链路数据
            SamplingService samplingService = (SamplingService)ServiceManager.INSTANCE.findService(SamplingService.class);
            samplingService.forceSampled();
            // 获取AbstractTracerContext
            context = getOrCreate(operationName, true);
            // 提取carrier的数据到context，跨进程，接受上下文
            context.extract(carrier);
        } else {
            context = getOrCreate(operationName, false);
        }

        // 创建EntrySpan
        return context.createEntrySpan(operationName);
    }

    public static AbstractSpan createLocalSpan(String operationName) {
        operationName = StringUtil.cut(operationName, Config.Agent.OPERATION_NAME_THRESHOLD);
        AbstractTracerContext context = getOrCreate(operationName, false);
        return context.createLocalSpan(operationName);
    }

    public static AbstractSpan createExitSpan(String operationName, ContextCarrier carrier, String remotePeer) {
        if (carrier == null) {
            throw new IllegalArgumentException("ContextCarrier can't be null.");
        } else {
            operationName = StringUtil.cut(operationName, Config.Agent.OPERATION_NAME_THRESHOLD);
            AbstractTracerContext context = getOrCreate(operationName, false);
            AbstractSpan span = context.createExitSpan(operationName, remotePeer);
            context.inject(carrier);
            return span;
        }
    }

    public static AbstractSpan createExitSpan(String operationName, String remotePeer) {
        operationName = StringUtil.cut(operationName, Config.Agent.OPERATION_NAME_THRESHOLD);
        AbstractTracerContext context = getOrCreate(operationName, false);
        return context.createExitSpan(operationName, remotePeer);
    }

    public static void inject(ContextCarrier carrier) {
        get().inject(carrier);
    }

    public static void extract(ContextCarrier carrier) {
        if (carrier == null) {
            throw new IllegalArgumentException("ContextCarrier can't be null.");
        } else {
            if (carrier.isValid()) {
                get().extract(carrier);
            }

        }
    }

    public static ContextSnapshot capture() {
        return get().capture();
    }

    public static void continued(ContextSnapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("ContextSnapshot can't be null.");
        } else {
            if (!snapshot.isFromCurrent()) {
                get().continued(snapshot);
            }

        }
    }

    public static AbstractTracerContext awaitFinishAsync(AbstractSpan span) {
        AbstractTracerContext context = get();
        AbstractSpan activeSpan = context.activeSpan();
        if (span != activeSpan) {
            throw new RuntimeException("Span is not the active in current context.");
        } else {
            return context.awaitFinishAsync();
        }
    }

    public static AbstractSpan activeSpan() {
        return get().activeSpan();
    }

    public static void stopSpan() {
        AbstractTracerContext context = get();
        stopSpan(context.activeSpan(), context);
    }

    public static void stopSpan(AbstractSpan span) {
        stopSpan(span, get());
    }

    private static void stopSpan(AbstractSpan span, AbstractTracerContext context) {
        if (context.stopSpan(span)) {
            CONTEXT.remove();
            RUNTIME_CONTEXT.remove();
        }

    }

    public void prepare() {
    }

    public void boot() {
    }

    public void onComplete() {
    }

    public void shutdown() {
    }

    public static boolean isActive() {
        return get() != null;
    }

    public static RuntimeContext getRuntimeContext() {
        RuntimeContext runtimeContext = (RuntimeContext)RUNTIME_CONTEXT.get();
        if (runtimeContext == null) {
            runtimeContext = new RuntimeContext(RUNTIME_CONTEXT);
            RUNTIME_CONTEXT.set(runtimeContext);
        }

        return runtimeContext;
    }

    public static CorrelationContext getCorrelationContext() {
        AbstractTracerContext tracerContext = get();
        return tracerContext == null ? null : tracerContext.getCorrelationContext();
    }
}
