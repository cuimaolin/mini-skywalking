package cn.mini.skywalking.commons;

public class Constants {

    public static final String GET_BEAN_INTERCEPTOR = "org.apache.skywalking.apm.plugin.spring.mvc.commons.interceptor.GetBeanInterceptor";

    public static final String INVOKE_FOR_REQUEST_INTERCEPTOR = "org.apache.skywalking.apm.plugin.spring.mvc.commons.interceptor.InvokeForRequestInterceptor";

    public static final String REQUEST_MAPPING_METHOD_INTERCEPTOR = "org.apache.skywalking.apm.plugin.spring.mvc.commons.interceptor.RequestMappingMethodInterceptor";

    public static final String REST_MAPPING_METHOD_INTERCEPTOR = "org.apache.skywalking.apm.plugin.spring.mvc.commons.interceptor.RestMappingMethodInterceptor";

    public static final String REQUEST_KEY_IN_RUNTIME_CONTEXT = "SW_REQUEST";

    public static final String RESPONSE_KEY_IN_RUNTIME_CONTEXT = "SW_RESPONSE";

    public static final String REACTIVE_ASYNC_SPAN_IN_RUNTIME_CONTEXT = "SW_REACTIVE_RESPONSE_ASYNC_SPAN";

    public static final String FORWARD_REQUEST_FLAG = "SW_FORWARD_REQUEST_FLAG";

    public static final String WEBFLUX_REQUEST_KEY = "SW_WEBFLUX_REQUEST_KEY";

    public static final String CONTROLLER_METHOD_STACK_DEPTH = "SW_CONTROLLER_METHOD_STACK_DEPTH";
}
