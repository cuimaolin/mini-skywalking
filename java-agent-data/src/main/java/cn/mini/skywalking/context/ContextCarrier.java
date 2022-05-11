package cn.mini.skywalking.context;

import java.io.Serializable;
import org.apache.skywalking.apm.agent.core.base64.Base64;
import org.apache.skywalking.apm.agent.core.conf.Constants;
import org.apache.skywalking.apm.agent.core.context.*;
import org.apache.skywalking.apm.util.StringUtil;

public class ContextCarrier implements Serializable {
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

    void extractExtensionTo(TracingContext tracingContext) {
    }

    void extractCorrelationTo(TracingContext tracingContext) {

    }


    ContextCarrier deserialize(String text, org.apache.skywalking.apm.agent.core.context.ContextCarrier.HeaderVersion version) {
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

}
