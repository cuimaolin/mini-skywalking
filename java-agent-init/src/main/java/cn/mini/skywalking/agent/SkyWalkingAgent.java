package cn.mini.skywalking.agent;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.boot.AgentPackageNotFoundException;
import org.apache.skywalking.apm.agent.core.boot.ServiceManager;
import org.apache.skywalking.apm.agent.core.conf.Config;
import org.apache.skywalking.apm.agent.core.conf.SnifferConfigInitializer;
import org.apache.skywalking.apm.agent.core.plugin.AbstractClassEnhancePluginDefine;
import org.apache.skywalking.apm.agent.core.plugin.EnhanceContext;
import org.apache.skywalking.apm.agent.core.plugin.PluginBootstrap;
import org.apache.skywalking.apm.agent.core.plugin.PluginFinder;
import org.apache.skywalking.apm.agent.core.plugin.bootstrap.BootstrapInstrumentBoost;
import org.apache.skywalking.apm.agent.core.plugin.jdk9module.JDK9ModuleExporter;

import java.lang.instrument.Instrumentation;
import java.util.List;


public class SkyWalkingAgent {


    public static void premain(String agentArgs, Instrumentation instrumentation) throws AgentPackageNotFoundException {

        // 初始化一些参数
        SnifferConfigInitializer.initializeCoreConfig(agentArgs);

        // 加载所有插件，并将插件放入PluginFinder容器
        List<AbstractClassEnhancePluginDefine> abstractClassEnhancePluginDefines = new PluginBootstrap().loadPlugins();

        // PluginFinder会进行分类
        final PluginFinder pluginFinder = new PluginFinder(abstractClassEnhancePluginDefines);


        //创建一个 ByteBuddy对象用于修改字节码
        final ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.of(Config.Agent.IS_OPEN_DEBUGGING_CLASS));
        // 忽略一些不需要的包，相当于设置了aop切面
        AgentBuilder agentBuilder = new AgentBuilder.Default(byteBuddy);
//                .ignore();

        // 加载bootstrap相关插件
        JDK9ModuleExporter.EdgeClasses edgeClasses = new JDK9ModuleExporter.EdgeClasses();
        agentBuilder = BootstrapInstrumentBoost.inject(pluginFinder, instrumentation, agentBuilder, edgeClasses);

        agentBuilder = JDK9ModuleExporter.openReadEdge(instrumentation, agentBuilder, edgeClasses);

        // buildMatch会找到需要修改字节码的插件
        ElementMatcher<? super TypeDescription> elementMatcher = pluginFinder.buildMatch();
        // 字节码的修改规则
        Transformer transformer = new Transformer(pluginFinder);

        //使用bytebuddy 去修改字节码
        agentBuilder.type(elementMatcher)
                .transform(transformer)
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .installOn(instrumentation);

        // 初始化Agent服务管理
        ServiceManager.INSTANCE.boot();

        Runtime.getRuntime()
                .addShutdownHook(new Thread(ServiceManager.INSTANCE::shutdown, "skywalking service shutdown thread"));
    }

    private static class Transformer implements AgentBuilder.Transformer {
        private PluginFinder pluginFinder;

        Transformer(PluginFinder pluginFinder) {
            this.pluginFinder = pluginFinder;
        }

        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
            // 这里的typeDescription是要被修改的class
            // 找到哪几个插件需要去修改对应的class
            List<AbstractClassEnhancePluginDefine> pluginDefines = pluginFinder.find(typeDescription);
            if (pluginDefines.size() > 0) {
                DynamicType.Builder<?> newBuilder = builder;
                //这个EnhanceContext 只是为了保证流程的一个记录器,比如执行了某个步骤后就会记录一下,防止重复操作
                EnhanceContext context = new EnhanceContext();
                // 遍历需要修改的插件
                for (AbstractClassEnhancePluginDefine define : pluginDefines) {
                    // 真正去修改字节码的逻辑
                    DynamicType.Builder<?> possibleNewBuilder = define.define(
                            typeDescription, newBuilder, classLoader, context);
                    if (possibleNewBuilder != null) {
                        newBuilder = possibleNewBuilder;
                    }
                }
                // 返回修改的字节码，做替换
                return newBuilder;
            }
            return builder;
        }
    }
}
