package cn.mini.skywalking.plugin;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.apache.skywalking.apm.agent.core.plugin.EnhanceContext;
import org.apache.skywalking.apm.agent.core.plugin.PluginException;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.v2.InstanceMethodsInterceptV2Point;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.v2.StaticMethodsInterceptV2Point;

public abstract class AbstractClassEnhancePluginDefine {
    public AbstractClassEnhancePluginDefine() {
    }

    /**
     * 修改字节码的入口
     * @param typeDescription
     * @param builder
     * @param classLoader
     * @param context
     * @return
     * @throws PluginException
     */
    public DynamicType.Builder<?> define(TypeDescription typeDescription, DynamicType.Builder<?> builder, ClassLoader classLoader, EnhanceContext context) throws PluginException {
        // 在对应方法上添加拦截器，以修改原来的class
        DynamicType.Builder<?> newClassBuilder = this.enhance(typeDescription, builder, classLoader, context);
        context.initializationStageCompleted();
        return newClassBuilder;
    }

    protected DynamicType.Builder<?> enhance(TypeDescription typeDescription, DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader, EnhanceContext context) throws PluginException {
        // 增强静态方法
        newClassBuilder = this.enhanceClass(typeDescription, newClassBuilder, classLoader);
        // 增强实例方法
        newClassBuilder = this.enhanceInstance(typeDescription, newClassBuilder, classLoader, context);
        return newClassBuilder;
    }

    protected abstract DynamicType.Builder<?> enhanceInstance(TypeDescription var1, DynamicType.Builder<?> var2, ClassLoader var3, EnhanceContext var4) throws PluginException;

    protected abstract DynamicType.Builder<?> enhanceClass(TypeDescription var1, DynamicType.Builder<?> var2, ClassLoader var3) throws PluginException;


    public abstract ConstructorInterceptPoint[] getConstructorsInterceptPoints();


    public abstract InstanceMethodsInterceptV2Point[] getInstanceMethodsInterceptV2Points();


    public abstract StaticMethodsInterceptV2Point[] getStaticMethodsInterceptV2Points();
}
