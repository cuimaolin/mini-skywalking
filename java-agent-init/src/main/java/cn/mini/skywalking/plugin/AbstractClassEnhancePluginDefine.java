package cn.mini.skywalking.plugin;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.skywalking.apm.agent.core.plugin.EnhanceContext;
import org.apache.skywalking.apm.agent.core.plugin.PluginException;
import org.apache.skywalking.apm.agent.core.plugin.WitnessMethod;
import org.apache.skywalking.apm.agent.core.plugin.bootstrap.BootstrapInstrumentBoost;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.EnhanceException;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.StaticMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ConstructorInter;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.OverrideCallable;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.v2.InstMethodsInterV2;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.v2.InstMethodsInterV2WithOverrideArgs;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.v2.DeclaredInstanceMethodsInterceptV2Point;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.v2.InstanceMethodsInterceptV2Point;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.v2.StaticMethodsInterceptV2Point;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.util.StringUtil;

import java.lang.reflect.Type;
import java.util.List;

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

    protected abstract ClassMatch enhanceClass();

    protected String[] witnessClasses() {
        return new String[0];
    }

    protected List<WitnessMethod> witnessMethods() {
        return null;
    }

    public boolean isBootstrapInstrumentation() {
        return false;
    }

    public abstract ConstructorInterceptPoint[] getConstructorsInterceptPoints();

    public abstract InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints();

    public abstract InstanceMethodsInterceptV2Point[] getInstanceMethodsInterceptV2Points();

    public abstract StaticMethodsInterceptPoint[] getStaticMethodsInterceptPoints();

    public abstract StaticMethodsInterceptV2Point[] getStaticMethodsInterceptV2Points();
}
