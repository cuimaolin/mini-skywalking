package cn.mini.skywalking.context;

import org.apache.skywalking.apm.agent.core.context.AsyncSpan;
import org.apache.skywalking.apm.agent.core.context.ContextCarrier;
import org.apache.skywalking.apm.agent.core.context.ContextSnapshot;
import org.apache.skywalking.apm.agent.core.context.CorrelationContext;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;

public class TracingContext implements AbstractTracerContext{

    @Override
    public void inject(ContextCarrier carrier) {

    }

    @Override
    public void extract(ContextCarrier carrier) {

    }

    @Override
    public ContextSnapshot capture() {
        return null;
    }

    @Override
    public void continued(ContextSnapshot snapshot) {

    }

    @Override
    public String getReadablePrimaryTraceId() {
        return null;
    }

    @Override
    public String getSegmentId() {
        return null;
    }

    @Override
    public int getSpanId() {
        return 0;
    }

    @Override
    public AbstractSpan createEntrySpan(String var1) {
        return null;
    }

    @Override
    public AbstractSpan createLocalSpan(String var1) {
        return null;
    }

    @Override
    public AbstractSpan createExitSpan(String var1, String var2) {
        return null;
    }

    @Override
    public AbstractSpan activeSpan() {
        return null;
    }

    @Override
    public boolean stopSpan(AbstractSpan var1) {
        return false;
    }

    @Override
    public AbstractTracerContext awaitFinishAsync() {
        return null;
    }

    @Override
    public void asyncStop(AsyncSpan var1) {

    }

    @Override
    public CorrelationContext getCorrelationContext() {
        return null;
    }
}
