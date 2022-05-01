# java-agent-init

skywalking java agent的初始化。agent是基于字节码侵入做的实现，premain方法是入口


主要逻辑如下，参考[SkyWalkingAgent#premain](src/main/java/cn/mini/skywalking/agent/SkyWalkingAgent.java)：

1. 初始化参数
2. 加载所有插件
3. 初始化bytebuddy对象
4. 定义忽略规则，忽略不需要侵入的包
5. 找到需要修改字节码的插件
6. 根据插件的规则对类的静态方法和实例方法做增强

对实例方法做字节码增强的逻辑，参考[AbstractClassEnhancePluginDefine#define](src/main/java/cn/mini/skywalking/plugin/AbstractClassEnhancePluginDefine.java)：

1. 找到目标类执行构造器和指定方法之前需要执行的方法
2. 向目标类写入属性：_$EnhancedClassField_ws，并让目标类实现EnhancedInstance接口，以让目标类能够该字段进行getter和setter
3. 增强构造器和指定方法，将定义好的增强方法代理到构造器上