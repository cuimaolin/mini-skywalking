# java-agent-collect

skywalking java agent进行trance数据收集

本部分以spring mvc为例，可参考[AbstractMethodInterceptor](src/main/java/cn/mini/skywalking/collect/AbstractMethodInterceptor.java)，其主要有实现了beforeMethod和afterMethod两种方法

其分别在指定方法执行前和执行后执行。

对于beforeMethod，其主要逻辑如下：

1. 拿到当前的request
2. 从context中拿到当前调用栈深度
3. 如果调用栈不为空，创建local span，并置入栈中
4. 如果调用栈为空，说明是调用链的开端。创建entry span，并将将当前request信息塞进去

对于afterMethod，其主要逻辑如下

1. 拿到当前的request
2. 从context中拿到当前调用栈深度
3. 进行出栈，并清除context中的信息
4. 如果都出栈了，则将整个调用链塞入消费队列