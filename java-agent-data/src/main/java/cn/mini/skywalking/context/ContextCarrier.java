package cn.mini.skywalking.context;

import org.apache.skywalking.apm.agent.core.base64.Base64;
import org.apache.skywalking.apm.agent.core.conf.Constants;
import org.apache.skywalking.apm.agent.core.context.*;
import org.apache.skywalking.apm.util.StringUtil;

import javax.annotation.processing.Generated;
import java.io.Serializable;

public class ContextCarrier extends org.apache.skywalking.apm.agent.core.context.ContextCarrier implements Serializable {
    private String traceId;
    private String traceSegmentId;
    private int spanId = -1;
    private String parentService;
    private String parentServiceInstance;
    private String parentEndpoint;
    private String addressUsedAtClient;
    private ExtensionContext extensionContext;
    private CorrelationContext correlationContext;

    public ContextCarrier() {
        this.parentService = Constants.EMPTY_STRING;
        this.parentServiceInstance = Constants.EMPTY_STRING;
        this.extensionContext = new ExtensionContext();
        this.correlationContext = new CorrelationContext();
    }

    public CarrierItem items() {
        SW8ExtensionCarrierItem sw8ExtensionCarrierItem = new SW8ExtensionCarrierItem(this.extensionContext, (CarrierItem)null);
        SW8CorrelationCarrierItem sw8CorrelationCarrierItem = new SW8CorrelationCarrierItem(this.correlationContext, sw8ExtensionCarrierItem);
        SW8CarrierItem sw8CarrierItem = new SW8CarrierItem(this, sw8CorrelationCarrierItem);
        return new CarrierItemHead(sw8CarrierItem);
    }

    public ExtensionInjector extensionInjector() {
        return new ExtensionInjector(this.extensionContext);
    }

    void extractExtensionTo(TracingContext tracingContext) {
        tracingContext.getExtensionContext().extract(this);
        this.extensionContext.handle(tracingContext.activeSpan());
    }

    void extractCorrelationTo(TracingContext tracingContext) {
        tracingContext.getCorrelationContext().extract(this);
        this.correlationContext.handle(tracingContext.activeSpan());
    }

    String serialize(org.apache.skywalking.apm.agent.core.context.ContextCarrier.HeaderVersion version) {
        return this.isValid(version) ? StringUtil.join('-', new String[]{"1", Base64.encode(this.getTraceId()), Base64.encode(this.getTraceSegmentId()), this.getSpanId() + "", Base64.encode(this.getParentService()), Base64.encode(this.getParentServiceInstance()), Base64.encode(this.getParentEndpoint()), Base64.encode(this.getAddressUsedAtClient())}) : "";
    }

    org.apache.skywalking.apm.agent.core.context.ContextCarrier deserialize(String text, org.apache.skywalking.apm.agent.core.context.ContextCarrier.HeaderVersion version) {
        if (text == null) {
            return this;
        } else {
            if (org.apache.skywalking.apm.agent.core.context.ContextCarrier.HeaderVersion.v3.equals(version)) {
                String[] parts = text.split("-", 8);
                if (parts.length == 8) {
                    try {
                        this.traceId = Base64.decode2UTFString(parts[1]);
                        this.traceSegmentId = Base64.decode2UTFString(parts[2]);
                        this.spanId = Integer.parseInt(parts[3]);
                        this.parentService = Base64.decode2UTFString(parts[4]);
                        this.parentServiceInstance = Base64.decode2UTFString(parts[5]);
                        this.parentEndpoint = Base64.decode2UTFString(parts[6]);
                        this.addressUsedAtClient = Base64.decode2UTFString(parts[7]);
                    } catch (IllegalArgumentException var5) {
                    }
                }
            }

            return this;
        }
    }

    public boolean isValid() {
        return this.isValid(org.apache.skywalking.apm.agent.core.context.ContextCarrier.HeaderVersion.v3);
    }

    boolean isValid(org.apache.skywalking.apm.agent.core.context.ContextCarrier.HeaderVersion version) {
        if (org.apache.skywalking.apm.agent.core.context.ContextCarrier.HeaderVersion.v3 != version) {
            return false;
        } else {
            return StringUtil.isNotEmpty(this.traceId) && StringUtil.isNotEmpty(this.traceSegmentId) && this.getSpanId() > -1 && StringUtil.isNotEmpty(this.parentService) && StringUtil.isNotEmpty(this.parentServiceInstance) && StringUtil.isNotEmpty(this.parentEndpoint) && StringUtil.isNotEmpty(this.addressUsedAtClient);
        }
    }

    @Generated
    void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Generated
    void setTraceSegmentId(String traceSegmentId) {
        this.traceSegmentId = traceSegmentId;
    }

    @Generated
    void setSpanId(int spanId) {
        this.spanId = spanId;
    }

    @Generated
    void setParentService(String parentService) {
        this.parentService = parentService;
    }

    @Generated
    void setParentServiceInstance(String parentServiceInstance) {
        this.parentServiceInstance = parentServiceInstance;
    }

    @Generated
    void setParentEndpoint(String parentEndpoint) {
        this.parentEndpoint = parentEndpoint;
    }

    @Generated
    void setAddressUsedAtClient(String addressUsedAtClient) {
        this.addressUsedAtClient = addressUsedAtClient;
    }

    @Generated
    void setExtensionContext(ExtensionContext extensionContext) {
        this.extensionContext = extensionContext;
    }

    @Generated
    void setCorrelationContext(CorrelationContext correlationContext) {
        this.correlationContext = correlationContext;
    }

    @Generated
    public String getTraceId() {
        return this.traceId;
    }

    @Generated
    public String getTraceSegmentId() {
        return this.traceSegmentId;
    }

    @Generated
    public int getSpanId() {
        return this.spanId;
    }

    @Generated
    public String getParentService() {
        return this.parentService;
    }

    @Generated
    public String getParentServiceInstance() {
        return this.parentServiceInstance;
    }

    @Generated
    public String getParentEndpoint() {
        return this.parentEndpoint;
    }

    @Generated
    public String getAddressUsedAtClient() {
        return this.addressUsedAtClient;
    }

    @Generated
    ExtensionContext getExtensionContext() {
        return this.extensionContext;
    }

    @Generated
    CorrelationContext getCorrelationContext() {
        return this.correlationContext;
    }

    public static enum HeaderVersion {
        v3;

        private HeaderVersion() {
        }
    }
}
