package cn.mini.skywalking.plugin.interceptor;

import cn.mini.skywalking.plugin.AbstractClassEnhancePluginDefine;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import org.apache.skywalking.apm.agent.core.plugin.EnhanceContext;
import org.apache.skywalking.apm.agent.core.plugin.PluginException;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.v2.InstanceMethodsInterceptV2Point;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.v2.StaticMethodsInterceptV2Point;

import java.lang.reflect.Type;


public abstract class ClassEnhancePluginDefineV2 extends AbstractClassEnhancePluginDefine {

    @Override
    protected DynamicType.Builder<?> enhanceClass(TypeDescription typeDescription, DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader) throws PluginException {

        // 找到目标类执行静态方法之前需要执行的方法，并放入数组
        StaticMethodsInterceptV2Point[] staticMethodsInterceptV2Points = this.getStaticMethodsInterceptV2Points();
        // 不为null并且长度不为0，则进行增强
        if (staticMethodsInterceptV2Points != null && staticMethodsInterceptV2Points.length != 0) {
            StaticMethodsInterceptV2Point[] var6 = staticMethodsInterceptV2Points;
            int var7 = staticMethodsInterceptV2Points.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                StaticMethodsInterceptV2Point staticMethodsInterceptV2Point = var6[var8];
                // 用 staticMethodsInterceptV2Point去操作 newClassBuilder 对象修改字节码
                // 这里忽略

            }

            return (DynamicType.Builder)newClassBuilder;
        } else {
            return (DynamicType.Builder)newClassBuilder;
        }
    }


    @Override
    protected DynamicType.Builder<?> enhanceInstance(TypeDescription typeDescription, DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader, EnhanceContext context) throws PluginException {

        // 找到目标类执行构造器之前需要执行的方法，并放入数组
        // 相当于将构造器aop化
        ConstructorInterceptPoint[] constructorInterceptPoints = this.getConstructorsInterceptPoints();
        // 找到目标类执行方法的时候需要执行的方法，并放入数组
        InstanceMethodsInterceptV2Point[] instanceMethodsInterceptV2Points = this.getInstanceMethodsInterceptV2Points();

        // 用于判断是否需要增强构造器
        boolean existedConstructorInterceptPoint = false;
        if (constructorInterceptPoints != null && constructorInterceptPoints.length > 0) {
            existedConstructorInterceptPoint = true;
        }

        // 用于判断是否需要增强方法
        boolean existedMethodsInterceptV2Points = false;
        if (instanceMethodsInterceptV2Points != null && instanceMethodsInterceptV2Points.length > 0) {
            existedMethodsInterceptV2Points = true;
        }

        // 不需要增强则返回
        if (!existedConstructorInterceptPoint && !existedMethodsInterceptV2Points) {
            return (DynamicType.Builder)newClassBuilder;
        }

        // 向目标类写入一个属性： _$EnhancedClassField_ws
        // 让目标类实现EnhancedInstance接口，以获取_$EnhancedClassField_ws属性
        if (!typeDescription.isAssignableTo(EnhancedInstance.class) && !context.isObjectExtended()) {
            newClassBuilder = ((DynamicType.Builder)newClassBuilder).defineField("_$EnhancedClassField_ws", Object.class, 66).implement(new Type[]{EnhancedInstance.class}).intercept(FieldAccessor.ofField("_$EnhancedClassField_ws"));
            // 用于流程控制，防止流程多次执行，因此一个类只会写入一次_$EnhancedClassField_ws
            // 就算有多个插件作用于同一个目标类，也只会执行一次
            context.extendObjectCompleted();
        }

        // 增强构造器，将定义好的增强方法代理到构造器上
        if (existedConstructorInterceptPoint) {
            ConstructorInterceptPoint[] var10 = constructorInterceptPoints;
            int var11 = constructorInterceptPoints.length;

            for(int var12 = 0; var12 < var11; ++var12) {
                ConstructorInterceptPoint constructorInterceptPoint = var10[var12];
                // 用 constructorInterceptPoint去操作 newClassBuilder 对象修改字节码
                // 这里忽略
            }
        }

        // 增强指定方法，将定义好的增强方法代理到指定方法上
        if (existedMethodsInterceptV2Points) {
            InstanceMethodsInterceptV2Point[] var16 = instanceMethodsInterceptV2Points;
            int var11 = instanceMethodsInterceptV2Points.length;

            for(int var12 = 0; var12 < var11; ++var12) {
                InstanceMethodsInterceptV2Point instanceMethodsInterceptV2Point = var16[var12];
                // 用 instanceMethodsInterceptPoint去操作 newClassBuilder 对象修改字节码
                // 这里忽略
            }
        }

        return newClassBuilder;
    }

}
